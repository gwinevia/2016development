package dash;

import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

/**
 * メッセージを中継するサーバ。プライベートアドレス間の通信などに使う。
 * 1つのホストに最大1つ動作する。
 * 次の2つの機能を持つ。
 * (1)クライアントDVMへのメッセージをバッファリングする機能
 * (2)クライアントDVMからのメッセージ取得要求を受け付ける。
 */
public class RelayServer extends UnicastRemoteObject implements RelayInterface {

  /**
   * keyは中継サーバを利用しているDVMの名前(w1:lynx:taurusなど)。
   * valueはメッセージを到着順に保持するVector。
   */
  Hashtable folders;

  int counter;

  /** この中継サーバのホスト名 */
  String myHostname;

  /**
   * コンストラクタ
   */
  public RelayServer(String name) throws RemoteException {
    folders = new Hashtable();
    startupRMIregistry();
    counter = 0;
    try {
      if (name != null)
        myHostname = name;
      else
        myHostname = InetAddress.getLocalHost().getHostName();
      Naming.rebind("relayserver", this);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (java.net.UnknownHostException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("My hostname = "+myHostname);
  }


  /**
   * RMIレジストリが起動していなければ起動する。
   */
  private void startupRMIregistry() {
    // 起動しているか調べる。
    try {
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
   * DVMの名前を決めて、ホスト名を含まない名前を返す。
   * (DVMの名前が"w1:lynx:aries"なら、"w1"を返す)
   * 成功した場合、そのホストを登録する。
   * 失敗した場合、"error "で始まる文字列を返す。
   * @param n ユーザが指定した名前
   * @param candidates dash.[rw].nameで指定した名前(,で区切られている)
   * @param hostname DVMを起動したホスト名
   * @param relayHosts ":中継サーバ:中継サーバ ..."なる文字列
   * @param relayList これ以降の中継サーバ(呼ぶたびに減って行く)
   */
  public String resolveDVMname(String n, String candidates, String hostname, String relayHosts, String relayList) throws java.rmi.RemoteException {
    String name = null;

    if (relayList.equals("")) {
      String fullname = null;
      RESOLVE:
      if (n != null) {
        name = n;
        fullname = n + ":" + hostname + relayHosts;
        if (folders.get(fullname) != null) {
          name = "error " + fullname + "という名のDVMは、既に存在します。";
          fullname = null;
        }
      } else {
        StringTokenizer st = new StringTokenizer(candidates, ",");
        while (st.hasMoreTokens()) {
          name = st.nextToken();
          fullname = name + ":" + hostname + relayHosts;
          if (folders.get(fullname) == null)
            break RESOLVE;
        }
        name = "error 名前が足りません。";
        fullname = null;
      }
      if (fullname != null)
        folders.put(fullname, new Vector());
      System.out.println("◯"+name);
      System.out.println("●"+fullname);

    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        name = neighbor.resolveDVMname(n, candidates, hostname, relayHosts, relayList);
      } catch (NotBoundException e) {
        e.printStackTrace();
        name = "error "+relayList+"以降のホストが見つかりません。";
      } catch (MalformedURLException e) {
        e.printStackTrace();
        name = "error "+next+"というホストが見つかりません。";
      }
    }

    return name;
  }
  /*
  public String resolveDVMname(String n, String candidates, String hostname, String relayHosts, Vector relayHostsV) throws java.rmi.RemoteException {
    String name = null;
    String fullname = null;
    String next = null;

    RESOLVE:
    if (relayHostsV.size() > 0) {
      System.out.println("kita");
      try {
        next = (String)relayHostsV.remove(0);
        String url = "rmi://" + next + "/relayserver";
        System.out.println(url);
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        name = neighbor.resolveDVMname(n, candidates, hostname, relayHosts, relayHostsV);
        fullname = null;
      } catch (NotBoundException e) {
        e.printStackTrace();
        StringBuffer buf = new StringBuffer();
        for (Enumeration h = relayHostsV.elements(); h.hasMoreElements(); )
          buf.append(":"+h.nextElement());
        name = "error "+buf+"以降のホストが見つかりません。";
      } catch (MalformedURLException e) {
        e.printStackTrace();
        name = "error "+next+"というホストが見つかりません。";
      }

    } else if (n != null) {
      name = n;
      fullname = n + ":" + hostname + relayHosts;
      if (folders.get(fullname) != null) {
        name = "error " + fullname + "という名のDVMは、既に存在します。";
        fullname = null;
      }

    } else {
      StringTokenizer st = new StringTokenizer(candidates, ",");
      while (st.hasMoreTokens()) {
        name = st.nextToken();
        fullname = name + ":" + hostname + relayHosts;
        if (folders.get(fullname) == null)
          break RESOLVE;
      }
      name = "error 名前が足りません。";
      fullname = null;
    }

    if (fullname != null)
      folders.put(fullname, new Vector());
    System.out.println("◯"+name);
    System.out.println("●"+fullname);
    return name;
  }
  */

  /**
   * dvmnameで指定されたDVMがこの中継サーバを使っている場合にtrueを返す。
   * この中継サーバを使っていないDVM(or中継サーバ)が呼び出す。
   * @see checkDVM()
   */
  public boolean knowsDVM(String dvmname) {
    return folders.get(dvmname) != null;
  }
  
  /**
   * 指定されたDVMが存在しているか調べる。
   * この中継サーバを使っているDVM(or中継サーバ)が呼び出す。
   * @see knowsDVM()
   */
  public boolean checkDVM(String dvmname, String relayList) {
    if (relayList.equals("")) {
      // RmiModule.checkDVM()と同じようなことをする
      boolean useRelay = (dvmname.indexOf(':') != dvmname.lastIndexOf(':'));
      if (!useRelay) {
        int p = dvmname.lastIndexOf(':');
        String ename = dvmname.substring(0, p);
        String ehost = dvmname.substring(p+1);
        String url = "rmi://"+ehost+"/"+ename;
        try {
          RmiInterface module = (RmiInterface)Naming.lookup(url);
        } catch (Exception e) { return false; }
        return true;
      } else {
        int p = dvmname.lastIndexOf(':');
        String ehost = dvmname.substring(p+1);
        String url = "rmi://"+ehost+"/relayserver";
        try {
          RelayInterface module = (RelayInterface)Naming.lookup(url);
          return module.knowsDVM(dvmname);
        } catch (Exception e) {
          return false;
        }
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        return neighbor.checkDVM(dvmname, newList);
      } catch (Exception e) {
        return false;
      }
    }
  }


  /**
   * この中継サーバを利用している(おそらくはプライベートの内側の)DVMに
   * メッセージを届ける。このDVMは、getMsg()でメッセージを待つ。
   * 4通りの処理を行う。
   * ・relayListが空の場合、
   *   - arrivalがこのホストではない場合:
   *     (1)そのホストにputMsg(M)する(中継サーバを使っている場合putMsg(M,""))
   *   - arrivalがこのホストの場合:
   *     - arrivalに1つだけ:を含む場合(中継サーバを利用していない場合)
   *       (2)そのホストにputMsg(M)する。
   *     - arrivalに複数の:が含まれる場合(中継サーバを利用している場合)
   *       (3)メッセージを受け取る。
   * ・relayListが空でない場合、
   *   - (4)さらに中継する(putMsg(M,l))。
   * @param msg メッセージ
   * @param relayList ""あるいは、さらに転送する中継サーバの名前
   */
  public void putMsg(DashMessage msg, String relayList) throws java.rmi.RemoteException {
    if (relayList.equals("")) {
      String fullname = msg.arrival; // w1:leo, w1:lynx:leoなど
      int p = fullname.lastIndexOf(':');
      String target = fullname.substring(p+1);
      boolean useRelay = (fullname.indexOf(':') != fullname.lastIndexOf(':'));

      if (!target.equals(myHostname) || !useRelay) {
        //(1)arrivalが別のホストの場合
        //(2)このホストで中継サーバを使っていない場合
        try {
          if (useRelay) {
            String ename = "relayserver";
            String ehost = fullname.substring(p+1);
            String url = "rmi://"+ehost+"/"+ename;
            RelayInterface relay = (RelayInterface)Naming.lookup(url);
            relay.putMsg(msg, "");
          } else {
            String ename = fullname.substring(0, p);
            String ehost = fullname.substring(p+1);
            String url = "rmi://"+ehost+"/"+ename;
            RmiInterface server = (RmiInterface)Naming.lookup(url);
            server.putMsg(msg);
          }
        } catch (Exception e) {
          System.err.println("RelayServer.putMsg(): "+msg+"を"+
                             fullname+"に送れません。");
          e.printStackTrace();
        }
      } else {
        //(3)arrivalがこのホストの場合: foldersに格納する。
        Vector vector = (Vector)folders.get(fullname);
        if (vector != null) {
          synchronized (vector) {
            vector.add(msg);
            vector.notify();
          }
        } else {
          System.err.println("error: 登録されていないDVMへのメッセージです: "+
                             msg);
        }
      }
    } else {
      //(4)さらに中継する。
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        neighbor.putMsg(msg, newList);
      } catch (Exception e) {
        System.err.println("RelayServer.putMsg(): "+msg+"を"+
                           relayList+"に中継できません");
        e.printStackTrace();
      }
    }

  }
  

  /**
   * この中継サーバに届いたメッセージのうち、
   * fullname宛のものを返す。
   * 10000msだけ待つ。
   * @param fullname DVM名
   * @param relayList ""あるいは、さらに転送する中継サーバの名前
   */
  public DashMessage waitMsg(String fullname, String relayList) throws java.rmi.RemoteException {
    DashMessage msg = null;
    if (relayList.equals("")) {
      Vector vector = (Vector)folders.get(fullname);
      if (vector != null) {
        synchronized (vector) {
          try {
            if (vector.isEmpty())
              vector.wait(10000);
          } catch (Exception e) { e.printStackTrace(); }
          if (!vector.isEmpty())
            msg = (DashMessage)vector.remove(0);
          System.out.println(counter+": "+msg);
          counter++;
        }
      } else {
        System.err.println("error: 登録されていません: "+fullname);
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        msg = neighbor.waitMsg(fullname, newList);
      } catch (Exception e) {
        System.err.println("RelayServer.waitMsg(): "+relayList+"以降にwaitMsg()できません。");
        e.printStackTrace();
      }
    }

    return msg;
  }

  /**
   * ネームサーバに環境名を登録する。
   * @param name 環境名
   * @param relayList 中継サーバのリスト
   * @param servername ネームサーバ
   * @return 指定したネームサーバが存在しないか、このホストでネームサーバが
   *  起動していない場合、-1。
   */
  public int registerEnv(String name, boolean isR, String relayList, String servername) throws RemoteException {
    int result = 0;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.registerEnv(name, isR);
      } catch (NotBoundException e) {
        result = -1;
      } catch (MalformedURLException e) {
        result = -1;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.registerEnv(name, isR, newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.registerEnv(): "+relayList+"以降にregisterEnv()できません。");
        e.printStackTrace();
        result = -1;
      }
    }
    
    return result;
  }
  
  /**
   * ネームサーバから環境名を削除する。
   * @param name 環境名
   * @param relayList 中継サーバのリスト
   * @param servername ネームサーバ
   */
  public void unregisterEnv(String name, String relayList, String servername) throws RemoteException {
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        server.unregisterEnv(name);
      } catch (NotBoundException e) {
      } catch (MalformedURLException e) {
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        neighbor.unregisterEnv(name, newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.unregisterEnv(): "+relayList+"以降にunregisterEnv()できません。");
        e.printStackTrace();
      }
    }
  }
  
  /**
   * ネームサーバにエージェント名を登録する。
   * @param name エージェント名(Sample01.200208031614312:w1:lynx:leo)
   *             リポジトリエージェントの場合はSample01のみ。
   * @param envname 現在居る環境名(w1:lynx:leo)
   * @param function 機能名
   * @param comment コメント
   * @param relayList 中継サーバのリスト
   * @param servername ネームサーバ
   * @return 指定したネームサーバが存在しないか、このホストでネームサーバが
   *  起動していない場合、-1。
   */
  public int register(String name, String envname, String origin, String function, String comment, String relayList, String servername) throws RemoteException {
    int result = 0;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.register(name, envname, origin, function, comment);
      } catch (NotBoundException e) {
        result = -1;
      } catch (MalformedURLException e) {
        result = -1;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.register(name, envname, origin, function, comment,
                                   newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.register(): "+relayList+"以降にregister()できません。");
        e.printStackTrace();
        result = -1;
      }
    }
    
    return result;
  }
  
  /**
   * ネームサーバからエージェント名を削除する。
   * @param name エージェント名(Sample01.200208031614312:w1:lynx:leo)
   *             リポジトリエージェントの場合はSample01のみ。
   * @param envname 現在居る環境名(w1:lynx:leo)
   * @param relayList 中継サーバのリスト
   * @param servername ネームサーバ
   * @return 指定したネームサーバが存在しないか、このホストでネームサーバが
   *  起動していない場合、-1。
   */
  public int unregister(String name, String envname, String relayList, String servername) throws RemoteException {
    int result = 0;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.unregister(name, envname);
      } catch (NotBoundException e) {
        result = -1;
      } catch (MalformedURLException e) {
        result = -1;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.unregister(name, envname,
                                     newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.unregister(): "+relayList+"以降にregister()できません。");
        e.printStackTrace();
        result = -1;
      }
    }
    
    return result;
  }
  
  /**
   * ネームサーバにエージェントが移動したことを通知する。
   * @param name エージェント名(Sample01.200208031614312:w1:lynx:leo)
   *             リポジトリエージェントの場合はSample01のみ。
   * @param oldEnvname 今まで居た環境名(w1:lynx:leo)
   * @param newEnvname 新しい環境名(w1:lynx:leo)
   * @param relayList 中継サーバのリスト
   * @param servername ネームサーバ
   * @return 指定したネームサーバが存在しないか、このホストでネームサーバが
   *  起動していない場合、-1。
   */
  public int move(String name, String oldEnvname, String newEnvname, String relayList, String servername) throws RemoteException {
    int result = 0;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.move(name, oldEnvname, newEnvname);
      } catch (NotBoundException e) {
        result = -1;
      } catch (MalformedURLException e) {
        result = -1;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.move(name, oldEnvname, newEnvname,
                               newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.move(): "+relayList+"以降にmove()できません。");
        e.printStackTrace();
        result = -1;
      }
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
   * 指定したネームサーバが存在しないか、このホストでネームサーバが
   * 起動していない場合、null。
   * 配列の添字の意味については、NSInterface参照。
   */
  public Vector lookup(String[][] selector, String relayList, String servername) throws RemoteException {
    Vector result = null;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.lookup(selector);
      } catch (NotBoundException e) {
        result = null;
      } catch (MalformedURLException e) {
        result = null;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.lookup(selector,
                                 newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.lookup(): "+relayList+"以降にlookup()できません。");
        e.printStackTrace();
        result = null;
      }
    }
    
    return result;
  }
  

  /**
   * 起動。中継サーバのホスト名を指定することができる。
   * (他のホストがその名前でアクセスできることが必要)
   */
  public static void main(String args[]) {
    try {
      String name = null;
      if (args.length == 1)
        name = args[0];
      RelayServer server = new RelayServer(name);
    } catch (RemoteException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
