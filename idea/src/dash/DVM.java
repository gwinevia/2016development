package dash;

import java.io.*;
import java.util.*;
import ps.OAVdata;
import ps.TafList;
import ps.Value;
import javax.swing.*;

/**
 * DASH Virtual Machineの実装。ワークプレースとも呼ぶ。
 */
public abstract class DVM implements Runnable {

  /** RMIを使わずに行う場合true */
  private static boolean localOnly = false;

  private String tempclassname = "";

  private String name = "";

  /** 通信基盤モジュール */
  public ComInterface comInt;

  /** メッセージ待ちのスレッド */
  private Thread msgWaitThread;

  /** RMIモジュールを使っている場合はTrue */
  boolean usingRmiModule = false;

  /** モニタ */
  // COSMOS publicに変更
  private NewifItface newif;

  /** ビューア **/
  /** ADD COSMOS **/
  Viewer viewer = null;

  /** スレッドグループ */
  ThreadGroup dashThreads;

  /** エージェントのテーブル。dash.DashAgentのインスタンスが入る。*/
  Hashtable agTable;

  /** エージェントを生成するためのスレッド。*/
  private Creater creater;
  private Thread createrThread;

  /**
   * 次に生成するメッセージのID。reply-withに用いる。最小のメッセージIDは1。
   */
  private long messageID;

  /** ベースプロセス/メソッドのあるディレクトリ */
  private String[] userClassPath;

  /** _putBytecode用 */
  private Hashtable replyBox;

  /**
   * 移動エージェントのテーブル。
   * keyは_createdInstanceのreplyWith, valueはエージェント名。
   * _activateMAを受け取ると、エントリが削除される。
   */
  private Hashtable noActivateMA;

  /** アラームの管理をする内部クラス */
  private AlarmManager alarmManager;

  // ADD COSMOS
  // Viewerの作成
  public void showViewer ( ) {
    if (viewer == null ) {
      viewer = new Viewer (comInt.getDVMname(), this );
      
      //viewer.show();
      //changed uchiya
      viewer.setVisible(true);
      viewer.initialize();
    }else {
      //viewer.show();
      viewer.setVisible(true);
    }
  }

  // ADD COSMOS
  public void closeViewer ( ) {
    viewer.close();
  }

  public NewifItface getNewIf () {
    return newif;
  }
  private boolean isDevEnv = false;
  /**
   * コンストラクタ
   * @param name 名前の候補。最終的な名前はComIntが決定する。
   */
  //public DVM(String name, File msgfile, boolean useNewif, File dashdir) {
  // UPDATE COSMOS

  public void RmiModule_ReCreate() {
    comInt = RmiModule.createNew(this, name, localOnly);

    msgWaitThread = new Thread(dashThreads, this, "dash.DVM");
    msgWaitThread.start();

    creater = new Creater(dashThreads, this);
    creater.start();
  }
  public DVM(String name, File msgfile, boolean useNewif, boolean useViewer, File dashdir) {
    // RMIモジュールを使う。
    comInt = RmiModule.createNew(this, name, localOnly);
    usingRmiModule = true;
    this.name = name;

    boolean noGUI =
      System.getProperty("dash.noGUI", "off").equalsIgnoreCase("on");

    isDevEnv = System.getProperty("dash.isDevEnv", "no").equalsIgnoreCase("yes");

    String DashMode = System.getProperty("DashMode");
    if (noGUI)
      newif = new NewifDummy();
    else {
      if (!DashMode.equals("on") ) {
        newif = new NewIf2(comInt.getDVMname(), this, msgfile, dashdir);
      }
      else {
        newif = new Newif(comInt.getDVMname(), this, msgfile, dashdir);
      }
    }

    dashThreads = new ThreadGroup("DASH");
    agTable = new Hashtable();
    messageID = 1;
    noActivateMA = new Hashtable();
    alarmManager = new AlarmManager(this);

    setupClassLoader();
    replyBox = new Hashtable();

    if (useNewif && !noGUI) {
      if (DashMode.equals("on") ) {
        newif.show();
      }
      newif.startMemoryWatch(); // ここでNewif.replaceConsole()する
    } else
      newif.setNonstop();

    // ADD COSMOS
    if (useViewer && !noGUI) {
      viewer = new Viewer (comInt.getDVMname(), this );
      //viewer.show();
      viewer.setVisible(true);
      viewer.initialize();
    }

    comInt.printInfo();
  }

  /**
   * 起動
   */
  void startVM() {

    msgWaitThread = new Thread(dashThreads, this, "dash.DVM");
    msgWaitThread.start();

    creater = new Creater(dashThreads, this);
    creater.start();
  }

  /**
   * メッセージ待ちのスレッド。
   * DVMはメッセージが届くのを待つ。
   * 届いたら、エージェントに渡す。
   */
  public void run() {
    while(true) {
      try {
        DashMessage msg = comInt.waitMsg();
        if (wpIndex != -1 ) {
          if (wpTab != null ) {
            wpTab.setSelectedIndex(wpIndex);
          }
        }
        
        // (1)インタフェース宛
        if (msg.to.equals(DashMessage.IF)) {
          newif.replaceConsole();
          newif.showMsg(msg);
          newif.putMsg(msg);
          if (viewer != null ) {
             viewer.showMsg(msg);	// ADD COSMOS 2002.09.13
          }

          newif.ViewerShowMsg(msg);
          continue;
        }

        // (2)DVM宛
        if (msg.to.equals(DashMessage.DVM)) {
          newif.replaceConsole();
          processDVMmsg(msg);
          continue;
        }

        // (2)ブロードキャスト
        if (msg.to.equals(DashMessage.BCAST)) {
          newif.replaceConsole();
          newif.showMsg(msg);
          if (viewer != null ) {
            viewer.showMsg(msg);	// ADD COSMOS 2002.09.13
          }
          newif.ViewerShowMsg(msg);
          for (Enumeration e = agTable.keys(); e.hasMoreElements(); ) {
            Object receiverName = e.nextElement();
            if (!receiverName.equals(msg.from)) {
              DashAgent receiver = (DashAgent)agTable.get(receiverName);
              receiver.putMsg(msg);
            }
          }
          continue;
        }

        DashAgent receiver = (DashAgent)agTable.get(msg.to);

        // (3)存在するエージェント
        if (receiver != null) {
          newif.replaceConsole();
          if (msg.performative.equals(DashMessage.KILLFORCE) &&
              msg.from.equals(DashMessage.IF) &&
              msg.content == null)
            ; //(@see DashAgent.run())
          else {
            newif.showMsg(msg);
            if (viewer != null ) {
              viewer.showMsg(msg);		// ADD COSMOS
            }
            newif.ViewerShowMsg(msg);
          }
          receiver.putMsg(msg);
          continue;
        }

        // (4)存在しないエージェント
        if (!msg.isNoSuchAgentOrDvm()) {
          newif.replaceConsole();
          sendMessageFromUser(msg, "Error",
                              "NO_SUCH_AGENT", null, null,msg.toString(),null);
          continue;
        }
      } catch (Exception e) {
        System.err.println(e);
        e.printStackTrace();
      }
    }
  }

  /* 終了処理 */
  void finalizeDVM(){
    // RMIレジストリからエントリを取り除く。
    // ネームサーバを使っている場合は、削除する。
    comInt.finalizeModule();

    // 終了
    //System.exit( 0 );
  }

  /**
   * DVM宛のメッセージを処理する。
   */
  private void processDVMmsg(DashMessage msg) {
    if (msg.performative.equals(DashMessage.CREATEINSTANCE)||
        msg.performative.equals(DashMessage.MOVE)) {
      // (1)ワークプレース宛の(instantiate)(move)
      String cname   = null;
      String oldname = null;
      if (msg.performative.equals(DashMessage.MOVE)) { // (move)
        cname = msg.getOtherAttributes(":cname");
        oldname = msg.from;
      } else {
        cname = msg.from; // (instantiate :into)
        oldname = null;
      }
      String description = msg.getOtherAttributes(":description");
      ps.AgentProgram program =
        (ps.AgentProgram)msg.getOtherAttributesB(":program");
      ps.WorkMem workmem =
        (ps.WorkMem)msg.getOtherAttributesB(":workmem");
      String filename = msg.getOtherAttributes(":filename");
      String facts = msg.content; // 0個以上のOAVデータを要素に持つリスト
      String origin = msg.getOtherAttributes(":origin");
      String newname =
        createAgent(cname, description, filename, facts, origin, oldname, program, workmem);
      DashMessage newmsg =
        sendMessageFromUser(msg, DashMessage.CREATEDINSTANCE,
                            "SUCCESS", null, null, "("+newname+")", null);

      if (msg.performative.equals(DashMessage.MOVE)) {
        Long key = new Long(newmsg.replyWith);
        noActivateMA.put(key, oldname);
      }

    } else if (msg.performative.equals(DashMessage.INSTANTIATE)) {
      // (2)リポジトリ宛の(instantiate)
      newif.showMsg(msg);
      String cname = msg.getOtherAttributes(":target");
      DashAgent agent = (DashAgent)agTable.get(cname);
      if (agent == null)
        sendMessageFromUser(msg, "Error", "NO_SUCH_AGENT", null, null, msg.toString(), null);
      else {
        Hashtable other = new Hashtable();
        other.put(":description", agent.getScript());
        other.put(":filename", agent.getFilename());
        other.put(":origin", agent.getOrigin());
        other.put(":target", cname);
        sendMessageFromUser(msg, DashMessage.CREATEINSTANCE,
                            "SUCCESS", null, null, null, other);
      }

    } else if (msg.performative.equals(DashMessage.GETBYTECODE)) {
      // (3)バイトコード要求受信
      loadLocalClassData(msg);

    } else if (msg.performative.equals(DashMessage.PUTBYTECODE)) {
      // (4)バイトコード受信
      putReply(msg);

    } else if (msg.performative.equals(DashMessage.ACTIVATEMOBILE)) {
      // (5)移動後の活性化(消去完了)
      Long key = new Long(msg.inReplyTo);
      String agname = (String)noActivateMA.remove(key);
      DashAgent agent = (DashAgent)agTable.get(agname);
      agent.startAgent(dashThreads);

    } else if (msg.performative.equals(DashMessage.GETRULESET)) {
      // (6)ルールセット要求受信
      getLocalRuleset(msg);

    } else if (msg.performative.equals(DashMessage.PUTRULESET)) {
      // (7)ルールセット受信
      putReply(msg);

    } else
      System.err.println("DVM.processDVMmsg(): cannot process:\n"+msg);
  }

  /**
   * _getBytecodeを受信したときの処理。
   * バイトコードを読み込み、_putBytecodeにして返す。
   * @see getLocalRuleset()
   */
  private void loadLocalClassData(DashMessage msg) {
    byte[] classBytes = null;
    String classname = msg.getOtherAttributes(":classname");
    try {
      classBytes = loadLocalClassData(classname);
    } catch (Exception e) {
      e.printStackTrace();
      classBytes = null;
    }

    if (classBytes == null) {
      sendMessageFromUser(msg, DashMessage.PUTBYTECODE,
                          "FAILED", null, null, null, null);
    } else {
      Hashtable other = new Hashtable();
      other.put(":bytecode", classBytes);
      sendMessageFromUser(msg, DashMessage.PUTBYTECODE,
                          "SUCCESS", null, null, null, other);
    }
  }

  /**
   * _getRulesetを受信したときの処理。
   * ルールセットのファイルを読み込み、_putRulesetにして返す。
   * @see loadLocalClassData()
   */
  private void getLocalRuleset(DashMessage msg) {
    String filename = msg.getOtherAttributes(":filename");
    String LoadDir = System.getProperty("dash.loadpath");
    File f  = null;
    Hashtable htFileList = null;
    if (LoadDir != null ) {
      htFileList = new Hashtable();
      StringTokenizer st = new StringTokenizer(LoadDir,";");
        while (st.hasMoreTokens()) {
          String data = st.nextToken();
          f  = new File (data);
          createFileList(f,htFileList, LoadDir);
          break;
        }

      }
      
    String text = getLocalRuleset(filename, htFileList);

    if (text == null) {
      sendMessageFromUser(msg, DashMessage.PUTRULESET,
                          "FAILED", null, null, null, null);
    } else {
      Hashtable other = new Hashtable();
      other.put(":ruleset", text);
      sendMessageFromUser(msg, DashMessage.PUTRULESET,
                          "SUCCESS", null, null, null, other);
    }
  }


  /**
   * Creater()にエージェントを生成するよう指示を出す。
   * loadAgent()で読み込む。
   */
  void addLoadQueue(File file) {
    newif.replaceConsole();
    creater.create(file);
  }

  /**
   * エージェントファイルを読み込む。
   * Creater.run()が呼び出す。
   */
  void loadAgent(File file) {
    newif.replaceConsole();
    // ファイル名チェック
    String cname = file.getName();
    int idx = cname.lastIndexOf('.');
    if (idx == -1)
      return;
    String ext = cname.substring(idx).toLowerCase();
    if (!ext.equals(".dash"))
      return;
    cname = cname.substring(0, idx);

    // ファイルを読み込む
    StringBuffer buf = new StringBuffer();
    BufferedReader br = null;
    try {
      //BufferedReader br = new BufferedReader(new FileReader(file));
      br = new BufferedReader(new InputStreamReader(
                                              new FileInputStream(file.getAbsolutePath()),
                                              "JISAutoDetect"));
      while (br.ready()) {
        String line = br.readLine();
        if (line != null) {
          // このif節はStreamTokenizerのバグ対策。
          // /* */内に空行があるとあやまったlinenoを返すので、空白を追加する。
          if (line.length() == 0)
            line = " ";
          buf.append(line);
          buf.append("\n");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
        try {
    	    if (br != null) {
    		 br.close();
    		 br=null;
       }
      } catch (IOException e) {
       e.printStackTrace();
      }
    }
    createAgent(cname, buf.toString(), file.getName(), null, getDVMname(), null, null, null);
  }

  /**
   * エージェントを生成する。killAgent()の逆を行う。
   * @param cname リポジトリエージェント名(クラス名)
   * @param description 知識記述
   * @param filename ディレクトリを含まないファイル名(Agentname.dash)
   *                 ワークプレースAgでも同じ。
   * @param facts 0個以上のOAVデータを要素に持つリスト。
   *              インスタンシエート時に使う。
   * @param origin 最初にエージェントを生成したDVM名
   * @param oldname moveアクションで生成される場合、移動前の名前(移動後も不変)
   *                違う場合null。
   * @param program moveアクションで生成される場合、プログラム。違う場合null。
   * @param workmem moveアクションで生成される場合、WorkMem。   違う場合null。
   * @return エージェント名
   */
  String createAgent(String cname, String description, String filename, String facts, String origin, String oldname, ps.AgentProgram program, ps.WorkMem workmem) {
    newif.replaceConsole();
    String name = null;

    // エージェントの生成・登録・起動
    // (名前決定は本当はComIntがやるべき？)
    try {
      if (isRtype()) {
        if (agTable.get(cname)!=null) {
          printlnE("Error: Agent \""+cname+"\" has already created.");
          return null;
        }
        name = cname;
      } else {
        if (oldname != null)
          name = oldname;
        else
          name = newName(cname);
      }


      // 開発環境のエージェントツリーで、エージェント名だけ表示するために
      // とりあえず・・・by COSMOS 2003.02.13
      //name = cname;
      if (agTable.get(name) != null ) {
        return null;
      }
      // ここまで。2003.02.13

      //DashAgent agent = new DashAgent(cname,name,description, filename, this, facts, origin, (oldname!=null));
      DashAgent agent = new DashAgent(cname,name,description, filename, this, facts, origin, program, workmem);
      if (!agent.result ) {
        return null;
      }

      agTable.put(name, agent);

      newif.addAgent(name, origin);
      newif.confirmSync();
      
      // ADD COSMOS ビューアにエージェントを描画
      if (viewer != null ) {	
      	 viewer.showNewAgent (origin,name);
      }
      // 移動エージェントの場合は_activateMobileが来るまで待つ。
      // (DVM.processDVMmsg()内でstartAgent()する)
      if (oldname == null) {
        String[] fc=agent.getFunctionAndComment(); // [0]は機能名,[1]はコメント
        //System.out.println(agent.getScript());
        comInt.registerAgent(name, getDVMname(), origin, fc[0], fc[1]);
        agent.result = true;
        agent.startAgent(dashThreads);
        if (!agent.result ) {
          return null;
        }
      }
    } catch (ps.SyntaxException se) {
      System.out.println("Read Error: "+se);
    }
    return name;
  }

  /**
   * エージェントを消滅させる。createAgent()の逆を行う。
   * DashAgent.run()の末尾で呼び出す。
   * @param name エージェント名
   * @param yuigon 移動した場合、_createdInstance
   * @see stopAgent()
   */
  void killAgent(String name, DashMessage yuigon) {
	//delアイコンを押したときはこのメソッドは呼ばれない
  	System.out.println("dvm.killAgent");
  	
    newif.replaceConsole();
    newif.removeAgent(name);
    newif.confirmSync();

    // ADD COSMOS ビューアからエージェントを削除 (2002.09.10)
    //if (viewer != null ) {
    //  viewer.removeAgent(name);
    //}

    DashAgent agent = (DashAgent)agTable.remove(name);
    if (yuigon != null) {
      sendMessageFromUser(yuigon, DashMessage.ACTIVATEMOBILE,
                          "SUCCESS", null, null, "()", null);
      comInt.moveAgent(name, getDVMname(), yuigon.departure);
    } else {
      comInt.unregisterAgent(name, getDVMname());
    }
  }


  /**
   * エージェントを消滅させる。contentがnullの_killForceを送る。
   * Newif.killAgent()で呼び出す。
   * @param name エージェント名
   * @see killAgent()
   * @see DashAgent.run()
   */
  void stopAgent(String name) {
    newif.replaceConsole();
    sendMessageFromUser(null, DashMessage.KILLFORCE,
                        null, name, null, null, null);
  }

  /**
   * ワークプレースに生成されたエージェントの名前を決める。
   * FileName.dashならfileName1にする。
   * ただし、現在のDVMに同名のエージェントがいる場合、
   * 名前の末尾の数字を1つ増やす。
   * @param cname FileNameの部分
   */
  /* cout
  private String newName(String cname) {
    String first = cname.substring(0, 1);
    String candidate = first.toLowerCase() + cname.substring(1);
    String suffix = "."+getDVMname();

    int i=1;
    while (agTable.get(candidate+i+suffix) != null)
      i++;

    return candidate+i+suffix;
  }
  */

  /**
   * ワークプレースに生成されたエージェントの名前を決める。
   * 時刻が2002年01月23日04時56分01秒3に、FileName.dashから生成したなら,
   * FileName.200201230456013:lynxになる。
   * @param cname FileNameの部分
   */
  private String newName(String cname) {
    String newname = null;
    do {
      String time = currentTime();
      newname = cname + "." + time + ":" + getDVMname();
    } while (agTable.get(newname) != null);

    return newname;
  }

  /**
   * 現在の時刻を、YYYYMMDDhhmmssx形式で返す。
   */
  private String currentTime() {
    Calendar rightNow = Calendar.getInstance();
    StringBuffer buf = new StringBuffer();

    int i = rightNow.get(Calendar.YEAR);     // YYYY年
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.MONTH)+1;      // MM月
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.DATE);         // DD日
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.HOUR_OF_DAY);  // hh時
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.MINUTE);       // mm分
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.SECOND);       // ss秒
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.MILLISECOND)/100;  // x ミリ秒(
    buf.append(Integer.toString(i));

    return buf.toString();
  }

  /**
   * エージェントをネームサーバに登録する。
   * ただし、
   */
  /*
  private void registerAgent(String cname, String name, DashAgent agent) {

  }
  */

  /**
   * NewifなどのインタフェースあるいはDVMから、
   * メッセージを作って(Createrの)送信キューに入れる。
   * @param msg     null(または、返信対象メッセージ)
   * @param perf    パフォーマティブ
   * @param diag    DVMからの場合、エラーの診断。IFからの場合、null。
   * @param to      送信先エージェント名
   * @param arrival 送信先環境名(またはnull)
   * @param content :content (..)の、(..)
   * @param other   その他属性
   */
  public DashMessage sendMessageFromUser(DashMessage msg, String perf, String diag, String to, String arrival, String content, Hashtable other) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    DashMessage newmsg;

    newif.replaceConsole();
    if (msg == null)
      newmsg = new DashMessage(perf, DashMessage.IF, to, arrival, messageID);
    else
      newmsg = new DashMessage(perf, msg, messageID);

    if (diag != null) {
      newmsg.from = DashMessage.DVM;
      newmsg.setOtherAttributes(":system", diag);
    }

    if (other != null)
      for (Enumeration e = other.keys(); e.hasMoreElements(); ) {
        String key = (String)e.nextElement();
        Object val = other.get(key);
        newmsg.setOtherAttributes(key, val);
      }

    newmsg.setContent(content);
    messageID++;

    creater.send(newmsg); // 送信キューに入れる。
    //System.out.println("newMsg= "+newmsg);
    return newmsg;
  }

  /**
   * Creater.run()から呼ばれる。
   */
  void sendMessageFromUser(DashMessage msg) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    boolean success = comInt.sendMsg(msg);

    if (!success) {
      if (!msg.isNoSuchAgentOrDvm())
        sendMessageFromUser(msg, "Error", "NO_SUCH_DVM", null, null, msg.toString(), null);
    }
  }

  /**
   * エージェントから、メッセージを作って送信キューに入れる。
   * @param oav sendアクション。(send :performative ...)または
   *            replyアクション。
   * @param name エージェント名
   * @return 作成したメッセージのメッセージID。
   * 失敗した場合も作成はするのでメッセージID。
   */
  String sendMessageFromAgent(OAVdata oav, String name) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    String perf    = oav.getValueString(":performative");
    String content = oav.getValueString(":content");

    DashMessage newmsg = null;
    if (oav.getObject().equals("send")) {
      String to      = oav.getValueString(":to");
      String arrival = oav.getValueString(":arrival");
      newmsg = new DashMessage(perf, name, to, arrival, messageID);

    } else if (oav.getObject().equals("broadcast")) {
      String to      = DashMessage.BCAST;
      String arrival = oav.getValueString(":arrival");
      newmsg = new DashMessage(perf, name, to, arrival, messageID);

    } else {
      OAVdata msg = (OAVdata)oav.getValue(":to");
      newmsg = new DashMessage(perf, name, msg, messageID);
    }

    newmsg.setContent(content);
    newmsg.setOtherAttributes(oav);
    messageID++;

    boolean success = comInt.sendMsg(newmsg);
    if (!success) {
      if (!newmsg.isNoSuchAgentOrDvm())
        sendMessageFromUser(newmsg, "Error", "NO_SUCH_DVM", null, null, newmsg.toString(), null);
    }
    return ""+newmsg.replyWith;
  }

  /**
   * (move)により呼び出される。
   * エージェントを移動する。
   * _moveというメッセージを生成先のDVMに届ける。
   * DVM.processDVMmsg()で処理される。
   * @param arrival 移動先の環境名
   * @param name アクションを実行したエージェント名(移動先でも同じ)
   * @param program エージェントプログラム
   * @param workmem ワーキングメモリ
   * @param filename ディレクトリ名のないファイル名。
   * @return 成功しても失敗しても、_moveのメッセージID
   * @see instantiateAgent()
   */
  long moveAgent(String arrival, String cname, String name, ps.AgentProgram program, ps.WorkMem workmem, String filename, String origin) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    DashMessage newmsg = new DashMessage(DashMessage.MOVE,
                                         name,
                                         DashMessage.DVM,
                                         arrival,
                                         messageID);

    newmsg.setOtherAttributes(":filename", filename);
    newmsg.setOtherAttributes(":program", program);
    newmsg.setOtherAttributes(":workmem", workmem);
    newmsg.setOtherAttributes(":origin", origin);
    newmsg.setOtherAttributes(":cname", cname);
    messageID++;


    boolean success = comInt.sendMsg(newmsg);
    if (!success) {
      if (!newmsg.isNoSuchAgentOrDvm())
        sendMessageFromUser(newmsg, "Error", "NO_SUCH_DVM", null, null, newmsg.toString(), null);
    }

    return newmsg.replyWith;
  }

  /**
   * (instantiate :into)により呼び出される。
   * エージェントをインスタンシエートする。
   * _createInstanceというメッセージを生成先のDVMに届ける。
   * DVM.processDVMmsg()で処理される。
   * @param oav  instantiateアクションの記述
   * @param name アクションを実行したエージェント名
   * @param description 知識記述
   * @param filename ディレクトリ名のないファイル名。
   * @return 成功しても失敗しても、_createInstanceのメッセージID
   * @see moveAgent()
   */
  long instantiateAgent(OAVdata oav, String name, String description, String filename, String origin) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    String arrival = oav.getValueString(":into");
    DashMessage newmsg = new DashMessage(DashMessage.CREATEINSTANCE,
                                         name,
                                         DashMessage.DVM,
                                         arrival,
                                         messageID);

    // :contentに:fact()で指定したOAVデータを詰める
    TafList list = new TafList(0);
    TafList facts = (TafList)oav.getValue(":facts");
    for (int i=0; i<facts.size(); i++) {
      Value value = facts.getValue(i);
      if (value instanceof OAVdata)
        list.addElement(value);
    }
    newmsg.setContent(list.toString());

    newmsg.setOtherAttributes(":filename", filename);
    newmsg.setOtherAttributes(":description", description);
    newmsg.setOtherAttributes(":origin", origin);
    messageID++;

    boolean success = comInt.sendMsg(newmsg);
    if (!success) {
      if (!newmsg.isNoSuchAgentOrDvm())
        sendMessageFromUser(newmsg, "Error", "NO_SUCH_DVM", null, null, newmsg.toString(), null);
    }

    return newmsg.replyWith;
  }

  /**
   * (instantiate :from :name)により呼び出される。
   * エージェントをインスタンシエートする。
   * _instantiateというメッセージをリポジトリに届ける。
   * @param oav  instantiateアクションの記述
   * @param name アクションを実行したエージェント名
   */
  long sendInstantiate(OAVdata oav, String name) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    String arrival = oav.getValueString(":from");
    DashMessage newmsg = new DashMessage(DashMessage.INSTANTIATE,
                                         name,
                                         DashMessage.DVM,
                                         arrival,
                                         messageID);

    newmsg.setOtherAttributes(":target", oav.getValueString(":name"));
    messageID++;

    boolean success = comInt.sendMsg(newmsg);
    if (!success) {
      if (!newmsg.isNoSuchAgentOrDvm())
        sendMessageFromUser(newmsg, "Error", "NO_SUCH_DVM", null, null, newmsg.toString(), null);
    }

    return newmsg.replyWith;
  }

  /**
   * dvmnameで指定されたDVMが存在するかを調べる。
   * @return 存在する場合、true。
   */
  boolean checkDVM(String dvmname) {
    newif.replaceConsole();
    return comInt.checkDVM(dvmname);
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
    //change uchiya
    newif.replaceConsole();
    return comInt.lookup(selector);
  }

  /**
   * クラスローダの準備。
   */
  private void setupClassLoader() {
    String dirs = System.getProperty("dash.userClassPath");
    StringTokenizer st = new StringTokenizer(dirs, File.pathSeparator);
    int pathCount = st.countTokens();
    //System.out.println("count="+pathCount);
    userClassPath = new String[pathCount];

    int i=0;
    while (st.hasMoreTokens()) {
      String dir = st.nextToken();
      if (!dir.endsWith(File.separator))
        dir += File.separator;
      userClassPath[i] = dir;
      i++;
    }
  }

  /**
   * バイトコードを返す。
   * @param classname クラス名
   * @param origin    エージェントの出自DVM名
   * @see getRuleset()
   */
  byte[] loadClassData(String classname, String origin) throws Exception {
  	//System.out.println("this="+getDVMname()+" origin="+origin);
    //tempclassname = "";
    if (getDVMname().equals(origin))
      return loadLocalClassData(classname);
    else
      return loadRemoteClassData(classname, origin);
  }

  /**
   * クラスファイルからクラスを読み込み、バイトコードを返す。
   * (classnameがプロパティファイルのファイル名の場合もある)
   * @param classname クラス名
   */
  private byte[] loadLocalClassData(String classname) throws Exception {
    File classfile = null;
    for (int i=0; i<userClassPath.length; i++) {
    	//System.out.println("クラス"+i+"番目"+userClassPath[i]);
      // (1)<classname>.classの場合
      String filename =
        userClassPath[i]+
        classname.replace('.', File.separatorChar)+
        ".class";
      classfile = new File(filename);
      if (classfile.exists()){
        break;
    }
      // (2)<classname>.propertiesの場合
      filename = userClassPath[i]+
        classname.replace('.', File.separatorChar)+
        ".properties";
      classfile = new File(filename);
      if (classfile.exists()) {
        break;
      }
      // (*)無い場合
      classfile = null;
    }

    //クラスパスを検索しても見つからなかった場合
    if (classfile==null) {
			  setTempClassName("dash.DammyBP");
        String path= "classes"+ File.separator +"dash"+ File.separator +"DammyBP.class";
        classfile = new File(new DashDefaults().getDashdir(),path);
        System.err.println("DVM.loadClassData(): クラス"+classname+"のクラスファイルが見つかりません");
       
    }
    byte[] classBytes = null;
    FileInputStream fis = new FileInputStream(classfile);
    classBytes = new byte[(int)classfile.length()];
    fis.read(classBytes);
	//System.out.println(classBytes.length);
    return classBytes;
  }

  /**
 * @param string
 */
private void setTempClassName(String string) {
tempclassname = string;
}

/**
   * 他のDVMに_getBytecodeを送信して_putBytecodeを返してもらい、
   * その中に格納されたバイトコードを返す。
   * @param classname クラス名
   * @param origin ありか
   * @see getRemoteRuleset()
   */
  private byte[] loadRemoteClassData(String classname, String origin) throws Exception {
    Hashtable other = new Hashtable();
    other.put(":classname", classname);
    DashMessage msg =
      sendMessageFromUser(null, DashMessage.GETBYTECODE,
                          "REQUEST", DashMessage.DVM, origin, null, other);

    DashMessage reply = waitReply(msg);

    String result = reply.getOtherAttributes(":system");
    byte[] classBytes = null;
    if (result.equals("SUCCESS"))
      classBytes = (byte[])reply.getOtherAttributesB(":bytecode");

    return classBytes;
  }

  /**
   * _putBytecode, _putRulesetを待つ。
   */
  private DashMessage waitReply(DashMessage msg) {
    String key = Long.toString(msg.replyWith);

    synchronized(replyBox) {
      if (replyBox.get(key) == null)
        replyBox.put(key, new Vector());
    }
    Vector box = (Vector)replyBox.get(key);

    synchronized(box) {
      try {
        if (box.isEmpty())
          box.wait();
      } catch (Exception e) { e.printStackTrace(); }
    }

    DashMessage reply = (DashMessage)box.remove(0);
    replyBox.remove(key);
    return reply;
  }


  /**
   * _putBytecode, _putRulesetが届いたときに呼ばれる。
   */
  private void putReply(DashMessage msg) {
    String key = Long.toString(msg.inReplyTo);

    synchronized(replyBox) {
      if (replyBox.get(key) == null)
        replyBox.put(key, new Vector());
    }
    Vector box = (Vector)replyBox.get(key);

    synchronized(box) {
      box.addElement(msg);
      box.notify();
    }
  }


  /**
   * エージェントから要求されたルールセットのファイルを読みこみ、返す。
   * @param filename ファイル名
   * @param env ルールセットが置いてある環境
   * @return ルールセット
   * @see loadClassData()
   */
  public String getRuleset(String filename, String env) {
    newif.replaceConsole();
    String dvmname = getDVMname();
    
    String LoadDir = System.getProperty("dash.loadpath");
      File f  = null;
      Hashtable htFileList = null;
      if (LoadDir != null ) {
        htFileList = new Hashtable();
        String[] st = LoadDir.split(":");
        for (int i=0; i < st.length; i++) {
          String data = st[i];
          f  = new File (data);
          createFileList(f,htFileList, LoadDir);
        }
      }

    if (getDVMname().equals(env)) {
      return getLocalRuleset(filename, htFileList);
    }
    else {
      return getLocalRuleset(filename, htFileList);
    }
  }


  /**
   * <p>createFileList </p>
   * <p>指定したディレクトリ以下に含まれる全てのファイルの一覧を作成する </p>
   * <p>著作権: Copyright (c) 2003</p>
   * <p>コスモス </p>
   * @author 未入力
   * @version 1.0
   */
  public void createFileList(File f, Hashtable htFileTable, String DefaultDir ) {
    File current_dir = new File(f,".");
    String file_list[] = current_dir.list();

    String wkDefaultDir = DefaultDir + "/:" ;

    for (int i=0; i<file_list.length; i++ ) {
      File current_file = new File(f,file_list[i]);
      if (current_file.isDirectory()){
        String dirstr = current_file.getAbsolutePath();
        if (!dirstr.endsWith(File.separator) ) {
          dirstr += File.separator;
        }

        if (wkDefaultDir.indexOf(dirstr) != -1 ) {
          createFileList(current_file, htFileTable, DefaultDir);
        }
      }
      else {
        String parentpath = current_file.getParent();
        if (!parentpath.endsWith(File.separator) ) {
          parentpath += File.separator;
        }
        if (wkDefaultDir.indexOf(parentpath) != -1 ) {
          htFileTable.put(current_file.getName(),current_file.getAbsolutePath());
        }
      }
    }
  }

  /**
   * dash.loadpathで指定されているディレクトリから
   * ルールセットのファイルを読みこみ、返す。
   * @param filename (ディレクトリ名などが付かない)ファイル名
   * @return 存在した場合ルールセットのテキスト。存在しない場合、null。
   */
  private String getLocalRuleset(String filename, Hashtable htFileList) {
  	if (htFileList.get(filename) == null ) {
  		return "";
  	}
  	
  	String filename_wk = (String)htFileList.get(filename);
  	if (filename_wk.equals("")) {
  		return "";
  	}
    File file = new File(filename_wk);
    
    if (file.exists() && file.canRead() && !file.isDirectory()) {
      StringBuffer buf = new StringBuffer();
      BufferedReader br = null;
      try {
        //BufferedReader br = new BufferedReader(new FileReader(file));
        br = new BufferedReader(new InputStreamReader(
                                                new FileInputStream(file.getAbsolutePath()),
                                                "JISAutoDetect"));

        while (br.ready()) {
          String line = br.readLine();
          if (line != null) {
            // このif節はStreamTokenizerのバグ対策。/* */内に空行があると
            // あやまったlinenoを返すので、空白を追加する。
            if (line.length() == 0)
              line = " ";
            buf.append(line);
            buf.append("\n");
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
          try {
      	    if (br != null) {
      		 br.close();
      		 br=null;
         }
        } catch (IOException e) {
         e.printStackTrace();
        }
      }
      
      return buf.toString();
    }
		return null;

  }

  /**
   * 他のDVMに_getRulesetを送信して_putRulesetを返してもらい、
   * その中に格納されたルールセットを返す。
   * @param filename ファイル名
   * @param env DVM名
   * @see loadRemoteClassData()
   */
  private String getRemoteRuleset(String filename, String env) {
    Hashtable other = new Hashtable();
    other.put(":filename", filename);
    DashMessage msg =
      sendMessageFromUser(null, DashMessage.GETRULESET,
                          "REQUEST", DashMessage.DVM, env, null, other);

    DashMessage reply = waitReply(msg);

    String result = reply.getOtherAttributes(":system");
    String text = null;
    if (result.equals("SUCCESS"))
      text = reply.getOtherAttributes(":ruleset");

    return text;
  }

  /**
   * エージェントagnameにアラームoavを伝える。
   * @return エージェントに届けることができたらtrue。
   */
  boolean notifyAlarm(String agname, String id, String oav) {
    newif.replaceConsole();
    DashAgent agent = (DashAgent)agTable.get(agname);
    if (agent != null) {
      agent.notifyAlarm(id, oav);
      return true;
    } else
      return false;
  }

  /**
   * 名前を返す。
   */
  String getFullname() {
    return comInt.getDVMname();
  }

  /**
   * インスペクタを表示する
   */
  void openInspector(String agname) {
    DashAgent agent = (DashAgent)agTable.get(agname);
    agent.inspect();
  }

  /**
   * リポジトリ型DVMならtrueを返す。
   */
  public boolean isRtype() {
    return false;
  }

  /** エージェントが居ればtrueを返す。*/
  boolean hasAgent(String agname) {
    return (agTable.get(agname) != null);
  }

  /** エージェントの個数を返す。*/
  int numberOfAgent() {
    return agTable.size();
  }

  /**
   * Newifに表示する。
   */
  void println(String s) {
    newif.println(s);
  }

  /**
   * Newifに表示する。
   */
  void printlnE(String s) {
    newif.printlnE(s);
  }

  /** Newifに設定する */
  void settextOnNewif(String agent, String s) {
    newif.settext(agent, s);
  }

  /**
   * ホスト名を含む環境名を返す。
   */
  public String getDVMname() {
    return comInt.getDVMname();
  }

  // ADD COSMOS
  void showAgentScript(String agname) {
    Vector agnames = new Vector();
    for (Enumeration e = agTable.keys(); e.hasMoreElements(); )
      agnames.addElement(e.nextElement());
  }

  /**
   * プロジェクトファイル(*.prj)を読み込み、何かする。
   */
  public void loadProject(File file) {
    newif.replaceConsole();
    StringBuffer buffer = new StringBuffer();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(
                                              new FileInputStream(file.getAbsolutePath()),
                                              "JISAutoDetect"));
      while (br.ready()) {
        String line = br.readLine();
        if (line != null)
          buffer.append(line + "\n");
      }
    } catch (FileNotFoundException e) {
      System.err.println("ファイル"+file+"がありません");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
        try {
    	    if (br != null) {
    		 br.close();
    		 br=null;
       }
      } catch (IOException e) {
       e.printStackTrace();
      }
    }
    ps.Parser parser = new ps.Parser(file.getName(), buffer.toString(), false);
    Vector lists = null;
    try {
      lists = parser.parseOAVinProject(file.getParentFile());
    } catch (ps.SyntaxException e) {
      System.err.println("警告: ファイル "+file+":"+e.lineno+": "+e.comment);
      lists.removeAllElements();
    }
    boolean error = false;
    for (Enumeration e = lists.elements(); e.hasMoreElements(); ) {
      Object element = e.nextElement();
      if (element instanceof String) {
        System.err.println(element); // エラー
        error = true;
      }
    }
    if (!error)
      for (Enumeration e = lists.elements(); e.hasMoreElements(); ) {
        Object obj = e.nextElement();
        if (obj instanceof File)
          addLoadQueue((File)obj);
      }
  }

  /**
   * アラームをセットする。
   * @param agname エージェントの名前
   * @param repeat くり返す場合true
   * @param time   ミリ秒
   * @param oav    通知するOAV型データ
   * @return アラームID
   */
  String setAlarm(String agname, boolean repeat, long time, String oav) {
    newif.replaceConsole();
    return alarmManager.setAlarm(dashThreads, agname, repeat, time, oav);
  }

  /**
   * アラームをキャンセルする。
   * @param agname エージェントの名前
   * @param alarmID "all"または"alarmアラームID"
   * @return アラームID
   */
  String[] cancelAlarm(String agname, String alarmID) {
    newif.replaceConsole();
    return alarmManager.cancelAlarm(agname, alarmID);
  }

  IdeaMainFrame parentframe;
  public void setParentFrame (IdeaMainFrame parentframe){
    this.parentframe = parentframe;
  }
  public IdeaMainFrame getParentFrame (){
    return parentframe;
  }

  private int wpIndex = -1;
  public void setWpIndex (int wpIndex){
    this.wpIndex = wpIndex;
  }
  private JTabbedPane wpTab = null;
  public void setWpTab (JTabbedPane wpTab ) {
    this.wpTab = wpTab;
  }
  public static void main(String args[]) {
    localOnly = true;

    String s[] = { };
    Repository.main(s);
    Workplace.main(s);
  }
  // added by takagaki
  public String getProjectPath()
  {
	return parentframe.getProjectPath();
  }

/**
 * 
 */
public String getTempClassName() {
return tempclassname;
}
  
  public String[] getUserClasspath(){
  return userClassPath;
  }
  
}
