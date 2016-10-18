package dash;

import java.util.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;
/*
 * DASH-1.1相当の通信モジュール
 * スレッドは呼出側で作る。
 *
 * RMIでメッセージの送受信を行う。
 *
 * DVMの名前は、
 *   name : hostname relayHosts
 * みたいな感じ。
 *
 * 中継サーバを使わない場合、DVMを起動したホストでRMIサーバを起動する。
 * リモートサーバは、rmi://localhost/w1
 *
 * 中継サーバを使う場合、DVMを起動したホストではRMIサーバは起動しない。
 * リモートサーバは、rmi://relayhost/relayserver
 */
class RmiModule extends UnicastRemoteObject implements ComInterface, RmiInterface {

  /** DVM */
  private DVM dvm;

  /** DVM名(ホスト名含まない) */
  private String name;

  /** DVMの起動しているホスト名。*/
  private String hostname;

  /** 中継サーバのリスト。
   * ":server1:server2:server3"のような、":サーバ名"の0個以上の並び。
   * 中継サーバを使わない場合はnull。
   */
  private String relayHosts;

  /** 隣の中継サーバの名前。中継サーバを使わない場合はnull。 */
  private String neighborHostname;

  /** 隣の中継サーバのリモートオブジェクト。中継サーバを使わない場合はnull。 */
  private RelayInterface neighborHost;

  /** 到着メッセージキュー */
  private Vector queue;

  /** リモートオブジェクトのキャッシュ */
  private Hashtable moduleCache;

  /** メッセージを配送する種別 */
  private static int deliveryClass = 0;
  private static final int NO_RMI  = 1;  // ネットワークを使わない。
  private static final int RMI     = 2;  // RMIサーバをローカルに起動する。
  private static final int RELAY   = 3;  // 中継サーバを使う。

  /**
   * RMIを使う場合、null。
   * RMIを使わない場合、"r1._localhost"と"w1._localhost"に
   * リポジトリとワークプレースのRmiModuleが入る。
   */
  private static Hashtable localNaming;

  /** RMIの時、ネームサーバのリモートオブジェクト。NO_RMI,RELAYの時、null。*/
  private NSInterface nameserver;

  /** ネームサーバを使う場合、そのホスト名。使わない場合、null。*/
  private String nameserverHost;

  /** ネームサーバー起動時のエラーメッセージ */
  private String NameServerErrMsg = "";

  /** インスタンスを作って返す。*/
  static ComInterface createNew(DVM dvm, String n, boolean local) {
    if (System.getProperty("language") == null ) {
      System.setProperty("language","japanese");
    }
    //String relayHosts = "192.168.104.3";//System.getProperty("dash.relayHosts");
    String relayHosts = System.getProperty("dash.relayHosts");
    if (local)
      deliveryClass = NO_RMI;
    else if (relayHosts == null || relayHosts.equals(""))
      deliveryClass = RMI;
    else
      deliveryClass = RELAY;

    ComInterface comInt = null;
    try {
      comInt = new RmiModule(dvm, n);
    } catch (RemoteException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return comInt;
  }


  //ログ保管するかどうか create 05/02/17
  private boolean USELOG = false;
  
  /**
   * コンストラクタ
   * @param dvm   DVM
   * @param n     ホスト名を含まない環境名
   * @param local RMIを使わない場合true
   */
  RmiModule(DVM dvm, String n) throws RemoteException {
  	String prop=null;
  	//ログ保管の有無
  	 if ((prop=System.getProperty("dash.log"))!=null &&
			prop.equalsIgnoreCase("on")){
 			USELOG = true;
  	//System.out.println("logを使います。"+this+" "+dvm);
			}
		//else{
		//System.out.println("logを使いません。"+this+" "+dvm);
		//}
    this.dvm = dvm;
    queue = new Vector();
    moduleCache = new Hashtable();

    switch (deliveryClass) {
    case NO_RMI:
      name = dvm.isRtype() ? "r1" : "w1";
      hostname = DashMessage.LOCALHOST;
      if (localNaming == null)
        localNaming = new Hashtable();
      localNaming.put(getDVMname(), this);
      break;
    case RMI:
      try {
        initializeForRMI(n);
      } catch (RemoteException e) {
        //e.printStackTrace();
        nameserver = null;
        if (System.getProperty("language").equals("japanese") ) {
          NameServerErrMsg = "ネームサーバの起動でエラーが発生しています。";
        }
        else {
          NameServerErrMsg = "The error has occurred in starting of a name server.";
        }
      }

      break;
    case RELAY:
      initializeForRelay(n);
      break;
    }
  }

  /**
   * 中継サーバを使わない場合の準備
   */
  private void initializeForRMI(String n) throws RemoteException {
    // RMIサーバ起動
    startupRMIregistry();

    try {
      // DVM名・ホスト名決定
      name = resolveDVMname(n, dvm.isRtype());

      // ローカルホスト名取得(.ac.jpまで取れる場合と取れない場合がある)
      hostname = new String(InetAddress.getLocalHost().getHostName());
      //hostname = "CSMSXPCLNT01";
      // 登録
      Naming.rebind(name, this);

    } catch (java.net.UnknownHostException e) {
      if (System.getProperty("language").equals("japanese") ) {
        System.err.println("localhostが不明です。");
      }
      else {
        System.err.println("localhost is unknown.");
      }

      System.exit(1);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // ネームサーバのチェック
    String nshost = System.getProperty("dash.nameserver");
    if (nshost == null)
      nameserver = null;
    else {
      try {
        String url = "rmi://"+nshost+"/nameserver";
        //System.out.println(url);
        nameserver = (NSInterface)Naming.lookup(url);
        nameserver.registerEnv(getDVMname(), dvm.isRtype());
        nameserverHost = nshost;
      } catch (java.rmi.UnknownHostException e) {
        if (System.getProperty("language").equals("japanese") ) {
          System.err.println("ネームサーバ"+nshost+"が不明です。");
          nameserver = null;
          NameServerErrMsg = "ネームサーバ"+nshost+"が不明です。";
        }
        else {
          System.err.println("NameServer("+nshost+") is unknown.");
          nameserver = null;
          NameServerErrMsg ="NameServer("+nshost+") is unknown.";
        }
        //System.exit(1);
      } catch (java.rmi.NotBoundException e) {
        if (System.getProperty("language").equals("japanese") ) {
          System.err.println("ホスト"+nshost+"が存在しないか、あるいはこのホストでネームサーバが起動していません。");
          nameserver = null;
          NameServerErrMsg = "ホスト"+nshost+"が存在しないか、あるいはこのホストでネームサーバが起動していません。";
        }
        else {
          System.err.println("A host("+nshost+") does not exist or the name server has not started by this host");
          nameserver = null;
          NameServerErrMsg = "A host("+nshost+") does not exist or the name server has not started by this host";
        }
        //System.exit(1);
      } catch (MalformedURLException e) {
        e.printStackTrace();
        nameserver = null;
        NameServerErrMsg = e.toString();
        //System.exit(1);
      }
    }
  }

  /**
   * 中継サーバを使う場合の準備。
   * 変数neighborHostとneighborHostnameをセットする。
   * RMIサーバは起動しない。
   */
  private void initializeForRelay(String n) throws RemoteException {
    // 中継サーバのホスト名取得
    relayHosts = System.getProperty("dash.relayHosts");
    System.out.println("koko"+relayHosts.toString());
    //relayHosts = "192.168.104.3";//System.getProperty("dash.relayHosts");
    StringTokenizer st = new StringTokenizer(relayHosts, ":");
    Vector relayHostsV = new Vector();
    while (st.hasMoreTokens())
      relayHostsV.add(st.nextToken());
    neighborHostname = (String)relayHostsV.remove(0);

    String target = "中継サーバ";
    try {
      // ローカルホスト名取得(.ac.jpまで取れる場合と取れない場合がある)
      hostname = InetAddress.getLocalHost().getHostName();

      // DVM名・ホスト名決定
      String rw = (dvm.isRtype() ? "r" : "w");
      String names = System.getProperty("dash." + rw + ".name");
      String url = "rmi://"+neighborHostname+"/relayserver";
      neighborHost = (RelayInterface)Naming.lookup(url);
      int p = relayHosts.indexOf(":", 1);
      String relayList = (p==-1 ? "" : relayHosts.substring(p));
      name = neighborHost.resolveDVMname(n,names,hostname,relayHosts, relayList);
      if (name.startsWith("error ")) {
        System.err.println(name);
        System.exit(1);
      }

      // ネームサーバが起動しているかチェックする
      String nshost = System.getProperty("dash.nameserver");
      if (nshost != null) {
        target = "ネームサーバ";
        /*
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        */
        int r = neighborHost.registerEnv(getDVMname(), dvm.isRtype(), relayList, nshost);
        nameserverHost = nshost;
        if (r == -1) {
          System.err.println("ホスト"+nshost+"が存在しないか、あるいはこのホストでネームサーバが起動していません。");
          System.exit(1);
        }
      }

    } catch (java.net.UnknownHostException e) {
      System.err.println(target+neighborHostname+"が不明です。");
      System.exit(1);
    } catch (java.rmi.NotBoundException e) {
      System.err.println("ホスト"+neighborHostname+"が存在しないか、あるいはこのホストで"+target+"が起動していません。");
      System.exit(1);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (RemoteException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * RMIレジストリが起動していなければ起動する。
   */
  private void startupRMIregistry() {
    // 起動しているか調べる。
    try {
      //Naming.unbind("rmi://localhost/");
      String[] s = Naming.list("rmi://localhost/");
      return;
    } catch (Exception e) { /* System.out.println("no rmi"); */ }

    try {
      LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  /**
   * DVM名を決定する。
   * このメソッドは中継サーバを使わない場合に用いる。
   * 使う場合は、RelayServer.resolveDVMname()を呼び出す。
   * @param n ユーザが指定した名
   * @param isR リポジトリ型DVMの場合、true。
   */
  private String resolveDVMname(String n, boolean isR) {
    String names = System.getProperty("dash."+(isR ? "r" : "w") +".name");
    StringTokenizer st = new StringTokenizer(names, ",");

    String name = null;
    if (n != null) {
      name = n;
    } else {
      while (st.hasMoreTokens()) {
        name = st.nextToken();
        if (System.getProperty("DashMode").equals("on") ) {
          name = name + "_";
        }
        try {
          Naming.lookup("rmi://localhost/"+name);
          name = null;                   // すでに使われている場合
        } catch (NotBoundException e) {
          break;                         // まだ使われていない場合
        } catch (Exception e) {
          e.printStackTrace();
          name = "EXCEPTION";
        }
      }
    }

    if (name==null)
      name = "名前が足りません。新しい名前をdefaults.txtのdash.dvmnamesに追加してください。";
    //name = "r1";
    return name;
  }

  /**
   * 終了
   */
  public void finalizeModule() {
    try {
      switch (deliveryClass) {
      case RMI:
        Naming.unbind("rmi://localhost/"+name);
        if (nameserver != null)
          nameserver.unregisterEnv(getDVMname());
        break;
      case RELAY:
        if (nameserverHost != null) {
          int p = relayHosts.indexOf(":", 1);
          String relayList = (p==-1 ? "" : relayHosts.substring(p));
          neighborHost.unregisterEnv(getDVMname(), relayList, nameserverHost);
        }
        break;
      default:
        break;
      }
    } catch (Exception e) {e.printStackTrace(); }
  }

  public void printInfo() {
  	//System.out.println("dvmname = " + getDVMname()+" hostname = " + hostname);
    //System.out.println("hostname = " + hostname);
    //System.out.println("dvmname  = " + getDVMname());
  }

  /**
   * DVMの名前を返す。
   * RmiModuleの場合、DASH-1.0と同じ。
   */
  public String getDVMname() {
    if (relayHosts == null){
      String dvmname = name + ":" + hostname;
      return dvmname;
    }
    else
      return name + ":" + hostname + relayHosts;
  }

  /**
   * 他のDVMからのメッセージを受け取る。
   * sendMsg()内でテストのためにnullを送る場合もある。
   */
  public void putMsg(DashMessage msg) throws java.rmi.RemoteException {
    if (msg != null)
      enqueueMsg(msg);
  }

  /**
   * このDVMあるいは他のDVMにメッセージを送信する。
   * @return 成功したらtrue
   */
  public boolean sendMsg(DashMessage msg) {
  	
		//	ここからメッセージログ保存
		if(USELOG){
 		// メッセージログファイルへの書き込み
  	String msgLogText = createSendDatetime() + " ";

    // 送信元エージェント名
  	msgLogText += msg.from + "@";
  	
    // メッセージの送信元の環境名、またはnull。
    // nullの場合は、同じ環境内へのメッセージであることを表す。
  	if (msg.departure == null ) {
	  	msgLogText += getDVMname() + " ";
  	}
  	else {
	  	msgLogText += msg.departure + " ";
  	}

		// 送信先エージェント名 
  	msgLogText += msg.to + "@";

    // メッセージの到着する環境名、またはnull。
    //  nullの場合は、同じ環境内へのメッセージであることを表す。
  	if (msg.arrival == null ) {
	  	msgLogText += getDVMname() + " ";
  	}
  	else {
	  	msgLogText += msg.arrival + " ";
  	}
  	
		// メッセージ本体
  	msgLogText += msg.toString();

    
  	writeMsgLog (msgLogText );
  	//ここまでメッセージログ保存
		}
		
    // (1)このDVMに送信する場合
    if (msg.arrival == null || msg.arrival.equals(getDVMname())) {
      enqueueMsg(msg);
      return true;
    }

    // (2)他のDVMに送信する場合
    msg.departure = getDVMname();
    dvm.getNewIf().showMsg(msg);

    boolean result = false;
    //deliveryClass = 1;
    switch (deliveryClass) {
    case NO_RMI:
      result = sendMsgLocal(msg);
      break;
    case RMI:
      result = sendMsgRemote(msg);
      break;
    case RELAY:
      result = sendMsgRelay(msg);
      break;
    }
    return result;
  }

  /** NO_RMIの時の配送 */
  private boolean sendMsgLocal(DashMessage msg) {
    RmiInterface module = (RmiInterface)localNaming.get(msg.arrival);
    if (module == null) {
      dvm.printlnE(errstr(msg, "RmiModule.sendMsg()",
                          "cannot delivery msg:"+msg.toString2()));
      return false;
    } else {
      try {
        module.putMsg(msg);
      } catch (RemoteException e) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsg()",
                            "cannot delivery msg:"+e));
      }
      return true;
    }
  }

  /**
   * RMIの時の配送。
   * 2通りの処理を行う。
   * - arrivalに1つだけ:を含む場合(中継サーバを利用していない場合)
   *   (1)そのホストにputMsg(M)する。
   * - arrivalに複数の:が含まれる場合(中継サーバを利用している場合)
   *   (2)そのホストにputMsg(M,"")する。

   */
  private boolean sendMsgRemote(DashMessage msg) {
    String arrival = msg.arrival;
    boolean useRelay = (arrival.indexOf(':') != arrival.lastIndexOf(':'));
    if (!useRelay)
      return sendMsgRemoteToRMI(msg);
    else
      return sendMsgRemoteToRelay(msg);
  }

  /** 届け先が中継サーバを使っていない場合 */
  private boolean sendMsgRemoteToRMI(DashMessage msg) {
    boolean exist = checkDVM(msg.arrival);

    //キャッシュをチェック
    RmiInterface module = (RmiInterface)moduleCache.get(msg.arrival);
    if (module != null) {
      try {
        module.putMsg(null); // 存在するかテストしておく
      } catch (Exception e) {
        module = null;
      }
    }
    //新たに取得しキャッシュする
    if (module == null) {
      int p = msg.arrival.indexOf(':');
      if (p == -1) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRMI()",
                            "illegal arrival(no ':'):"+msg.arrival));
        return false;
      }
      String ename = msg.arrival.substring(0, p);
      String ehost = msg.arrival.substring(p+1);
      String url = "rmi://"+ehost+"/"+ename;
      try {
        module = (RmiInterface)Naming.lookup(url);
        moduleCache.put(msg.arrival, module);
      } catch (Exception e) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRMI()",
                            "illegal arrival(not exists):"+msg.arrival));
        return false;
      }
    }
    //送信する
    try {
      module.putMsg(msg);
    } catch (java.rmi.RemoteException e) {
      dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRelay()",
                          "cannot send msg:"+e));
      return false;
    }
    return true;
  }

  /** 届け先が中継サーバを使っている場合 */
  private boolean sendMsgRemoteToRelay(DashMessage msg) {
    //キャッシュをチェック
    RelayInterface module = (RelayInterface)moduleCache.get(msg.arrival);
    if (module != null) {
      try {
        module.putMsg(null, ""); // 存在するかテストしておく
      } catch (Exception e) {
        module = null;
      }
    }
    //新たに取得しキャッシュする
    if (module == null) {
      int p = msg.arrival.lastIndexOf(':');
      if (p == -1) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRelay()",
                            "illegal arrival(no ':'):"+msg.arrival));
        return false;
      }
      String ehost = msg.arrival.substring(p+1);
      String url = "rmi://"+ehost+"/relayserver";
      try {
        module = (RelayInterface)Naming.lookup(url);
        moduleCache.put(msg.arrival, module);
      } catch (Exception e) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRelay()",
                            "illegal arrival(not exists):"+msg.arrival));
        return false;
      }
    }
    //送信する
    try {
      module.putMsg(msg, "");
    } catch (java.rmi.RemoteException e) {
      dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRelay()",
                          "cannot send msg:"+e));
      return false;
    }
    return true;
  }

  /**
   * RELAYの時の配送。neighborHostにメッセージを渡す。
   */
  private boolean sendMsgRelay(DashMessage msg) {
    try {
      int p = relayHosts.indexOf(":", 1);
      String relayList = (p==-1 ? "" : relayHosts.substring(p));
      neighborHost.putMsg(msg, relayList);
    } catch (RemoteException e) {
      dvm.printlnE(errstr(msg, "RmiModule.sendMsgRelay()",
                          "cannot relay msg:"+e));
      return false;
    }
    return true;
  }

  /** 到着メッセージキューにメッセージを追加する。*/
  private void enqueueMsg(DashMessage msg) {
    synchronized (queue) {
      queue.addElement(msg);
      queue.notify();
    }
  }

  /**
   * 到着メッセージキューのメッセージを取り出し、返す。
   * キューが空の場合、メッセージが来るまでブロックする。
   * スレッドは呼出側で作る。
   */
  public DashMessage waitMsg() {
    DashMessage result = null;
    switch (deliveryClass) {
    case NO_RMI:
    case RMI:
      result = waitMsgRMI();
      break;
    case RELAY:
      result = waitMsgRelay();
      break;
    }
    return result;
  }

  /** NO_RMI, RMI用のwaitMsg() */
  private DashMessage waitMsgRMI() {
    synchronized(queue) {
      try {
        if (queue.isEmpty())
          queue.wait();
      } catch(Exception e) { e.printStackTrace(); }
    }
    return (DashMessage)queue.remove(0);
  }

  /** RELAY用のwaitMsg()。neighborHostに訊きに行く。 */
  private DashMessage waitMsgRelay() {
    // 届くまで待つ。
    DashMessage msg = null;
    while (msg==null) {
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        //msg = module.waitMsg(getDVMname(), relayList);
        msg = neighborHost.waitMsg(getDVMname(), relayList);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.waitMsgRelay(): cannot wait:"+e);
      }
    }
    System.out.println(msg);
    return msg;
  }

  /**
   * エージェント名をネームサーバに登録する
   * 通信しない場合(NO_RMIの場合)は使えない。
   * @param name エージェント名(Sample01.200208031614312:w1:lynx:leo)
   *             リポジトリエージェントの場合はSample01のみ。
   * @param envname 現在居る環境名(w1:lynx:leo)
   * @param origin リポジトリ(ファイルを読み込んで生成した場合、ワークプレース)
   * @param function 機能名
   * @param comment コメント
   */
  public int registerAgent(String name, String envname, String origin, String function, String comment) {
    if (nameserverHost == null)
      return 0;

    int result = 0;
    switch (deliveryClass) {
    case NO_RMI:
      result = 0;
      break;
    case RMI:
      try {
        result = nameserver.register(name, envname, origin, function, comment);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.registerAgent(): cannot register: "+e);
      }
      break;
    case RELAY:
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        result =neighborHost.register(name, envname, origin, function, comment,
                                      relayList, nameserverHost);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.registerAgent(): cannot register: "+e);
      }
      break;
    }
    return result;
  }

  /**
   * エージェント名をネームサーバから削除する
   * 通信しない場合(NO_RMIの場合)は使えない。
   * @param name エージェント名(Sample01.200208031614312:w1:lynx:leo)
   *             リポジトリエージェントの場合はSample01のみ。
   * @param envname 現在居る環境名(w1:lynx:leo)
   */
  public int unregisterAgent(String name, String envname) {
    if (nameserverHost == null)
      return 0;

    int result = 0;
    switch (deliveryClass) {
    case NO_RMI:
      result = 0;
      break;
    case RMI:
      try {
        result = nameserver.unregister(name, envname);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.unregisterAgent(): cannot unregister(1): "+e);
      }
      break;
    case RELAY:
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        result =neighborHost.unregister(name, envname,
                                        relayList, nameserverHost);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.unregisterAgent(): cannot register(2): "+e);
      }
      break;
    }
    return result;
  }

  /**
   * エージェントが移動したことをネームサーバに通知する。
   * 通信しない場合(NO_RMIの場合)は使えない。
   * @param name エージェント名(Sample01.200208031614312:w1:lynx:leo)
   *             リポジトリエージェントの場合はSample01のみ。
   * @param oldEnvname 今まで居た環境名(w1:lynx:leo)
   * @param newEnvname 新しい環境名(w1:taurus:leo)
   */
  public int moveAgent(String name, String oldEnvname, String newEnvname) {
    if (nameserverHost == null)
      return 0;

    int result = 0;
    switch (deliveryClass) {
    case NO_RMI:
      result = 0;
      break;
    case RMI:
      try {
        result = nameserver.move(name, oldEnvname, newEnvname);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.moveAgent(): cannot notify(1): "+e);
      }
      break;
    case RELAY:
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        result =neighborHost.move(name, oldEnvname, newEnvname,
                                  relayList, nameserverHost);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.moveAgent(): cannot notify(2): "+e);
      }
      break;
    }
    return result;
  }

  /**
   * selectorで指定されたエージェントの情報を返す。
   * @param selector 一つの要素は、String[2]。
   * [0]は、条件指定子。":name", ":environment"など。
   * [1]は、その属性値。
   * @return マッチするものがない場合、空のVector。
   * マッチした場合、String[9]を要素とするVector。
   * ネームサーバを使わない場合や、
   * 指定したネームサーバが存在しないか、このホストでネームサーバが
   * 起動していない場合、null。
   * 配列の添字の意味については、NSInterface参照。
   */
  public Vector lookup(String[][] selector) {
    if (nameserverHost == null)
      return null;

    Vector result = null;
    switch (deliveryClass) {
    case NO_RMI:
      break;
    case RMI:
      try {
        result = nameserver.lookup(selector);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.lookup(): cannot lookup: "+e);
      }
      break;
    case RELAY:
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        result =neighborHost.lookup(selector,
                                    relayList, nameserverHost);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.lookup(): cannot lookup: "+e);
      }
      break;
    }
    return result;
  }

  /**
   * dvmnameで指定されたDVMが存在するかを調べる。
   * 存在する場合、モジュールキャッシュに格納する。
   * @return 存在する場合、true。
   */
  public boolean checkDVM(String dvmname) {
    switch (deliveryClass) {
    case NO_RMI:
      return false;
    case RMI:
      boolean useRelay = (dvmname.indexOf(':') != dvmname.lastIndexOf(':'));
      if (!useRelay)
        return checkDVM_RMI_RMI(dvmname);
      else
        return checkDVM_RMI_Relay(dvmname);
    case RELAY:
      return checkDVM_Relay(dvmname);
    default:
      return false;
    }
  }

  /** 自分がRMIで、相手もRMIの場合 */
  private boolean checkDVM_RMI_RMI(String dvmname) {
    // キャッシュをチェック
    RmiInterface module = (RmiInterface)moduleCache.get(dvmname);
    if (module != null) {
      try {
        module.putMsg(null); // 存在するかテストしておく
      } catch (Exception e) {
        module = null;
      }
    }
    // 新たに取得しキャッシュする
    if (module == null) {
      int p = dvmname.indexOf(':');
      if (p == -1)
        return false;

      String ename = dvmname.substring(0, p);
      String ehost = dvmname.substring(p+1);
      String url = "rmi://"+ehost+"/"+ename;
      try {
        module = (RmiInterface)Naming.lookup(url);
      } catch (Exception e) {
        return false;
      }
      moduleCache.put(dvmname, module);
    }
    return true;
  }

  /** 自分がRMIで、相手はRelayの場合 */
  private boolean checkDVM_RMI_Relay(String dvmname) {
    // キャッシュをチェック
    RelayInterface module = (RelayInterface)moduleCache.get(dvmname);
    if (module != null) {
      try {
        if (!module.knowsDVM(dvmname)) // 存在するかテストしておく
          module = null;
      } catch (Exception e) {
        module = null;
      }
    }
    // (最寄りの中継サーバを)新たに取得しキャッシュする
    if (module == null) {
      int p = dvmname.lastIndexOf(':');
      if (p == -1)
        return false;

      String ehost = dvmname.substring(p+1);
      String url = "rmi://"+ehost+"/relayserver";
      try {
        module = (RelayInterface)Naming.lookup(url);
      } catch (Exception e) {
        return false;
      }
      moduleCache.put(dvmname, module);
    }
    return true;
  }

  /** 自分がRelayの場合(相手はRMIもしくはRelay) */
  private boolean checkDVM_Relay(String dvmname) {
    boolean result = false;
    try {
      int p = relayHosts.indexOf(":", 1);
      String relayList = (p==-1 ? "" : relayHosts.substring(p));
      result = neighborHost.checkDVM(dvmname, relayList);
    } catch (RemoteException e) {
      result = false;
    }
    return result;
  }

  private String errstr(DashMessage msg, String method, String s) {
    return msg.from+": "+method+": "+s;
  }

  public NSInterface getNameServer() {
    return nameserver;
  }
  public String getNameServerErrMsg() {
    return NameServerErrMsg;
  }


	private synchronized void writeMsgLog (String msglogText ) {

		String logFileName = "";

		// システムプロパティからパス付きログファイル名を取得する
		logFileName = (String)System.getProperty("msgfilename");
		// メッセージ番号を取得する
		String msgnoStr = (String)System.getProperty("msgno");
		String lastmsgnoStr;
		if (msglogText.indexOf(":to _broadcast") != -1) {
				lastmsgnoStr=String.valueOf(Integer.parseInt(msgnoStr)+dvm.agTable.size()-1);
				 msglogText= msgnoStr+"-"+lastmsgnoStr+" "+msglogText ;
				 msgnoStr=lastmsgnoStr;
		}else{
		
		// これを書き込むメッセージの先頭に追加
		msglogText = msgnoStr + " " + msglogText;
		}
		// メッセージ番号を１増分して、システムプロパティに再書き込み
		System.setProperty("msgno", new Integer(new Integer(msgnoStr).intValue() + 1).toString());
				
    

		// 追加書き込みのため、現在のログファイルの内容をベクターに取得し、
		// ベクターの最後の要素に追加するログ内容を追加する。
		try {
    	Vector vecOrgData = new Vector();
	    String sLine = "";
	    BufferedReader b_in = new BufferedReader(new InputStreamReader(
	              new FileInputStream(logFileName),
	              "JISAutoDetect"));
	    while ((sLine = b_in.readLine()) != null){
		    vecOrgData.addElement(sLine);
	    }
	    b_in.close();

			// 上記で作成したベクターの内容を再度ログファイルに書き込む
			vecOrgData.addElement(msglogText);
	    File fp  = new File ( logFileName );
	    FileOutputStream fos = new FileOutputStream (fp);
	    PrintWriter pw  = new PrintWriter (fos);
	    for (int i=0; i<vecOrgData.size(); i++ ) {
				pw.println((String)vecOrgData.elementAt(i));
	    }
	    pw.close ();
	    fos.close();
		}
		catch (IOException e ) {}
		

	}
  
  private String createSendDatetime () {
    Calendar rightNow = Calendar.getInstance();
    int year = rightNow.get(Calendar.YEAR) ;
    int month = rightNow.get(Calendar.MONTH)+1;
    int date = rightNow.get(Calendar.DATE);

    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
    int minute = rightNow.get(Calendar.MINUTE);
    int second = rightNow.get(Calendar.SECOND);
    //20050705 uchiya update
    int millisecond = rightNow.get(Calendar.MILLISECOND);
    
    String yearStr = new Integer(year).toString();
    String monthStr = new Integer(month).toString();
    if (monthStr.length() == 1 ) {
      monthStr = "0" + monthStr;
    }
    String dateStr = new Integer(date).toString();
    if (dateStr.length() == 1 ) {
      dateStr = "0" + dateStr;
    }

    String hourStr = new Integer(hour).toString();
    if (hourStr.length() == 1 ) {
      hourStr = "0" + hourStr;
    }
    String minuteStr = new Integer(minute).toString();
    if (minuteStr.length() == 1 ) {
      minuteStr = "0" + minuteStr;
    }
    String secondStr = new Integer(second).toString();
    if (secondStr.length() == 1 ) {
      secondStr = "0" + secondStr;
    }

	String millisecondStr = new Integer(millisecond).toString();
		//System.out.println("millisecond="+millisecond);
	   if (millisecondStr.length() == 1 ) {
		 millisecondStr = "00" + millisecondStr;
	   }else if(millisecondStr.length() == 2) {
	   millisecondStr = "0" + millisecondStr;
	   } 
	    String SendDatetimeStr = yearStr + "/" + monthStr + "/" + dateStr + " " +
                       hourStr + ":" + minuteStr + ":" + secondStr  + ":" +millisecondStr;
    return SendDatetimeStr;    
  }

}
