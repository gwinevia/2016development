package dash;

import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

/**
 * エージェントの名前を保持するサーバ。
 */
public class NameServer extends UnicastRemoteObject implements NSInterface {

  /**
   * エージェントの記録簿。
   * 要素はString[9]。
   * [0] name        完全なエージェント名(Sample01.200208031614312:w1:lynx:leo)
   * [1] rname       リポジトリエージェント名(Sample01)
   * [2] birthday    生成時刻 (200208031614312)
   * [3] birthplace  生成された環境名(w1:lynx:leo)
   * [4] environment 現在居る環境名(w1:taurus)
   * [5] function    機能名(WebServer)
   * [6] comment     コメント (This is a Web server agent.)
   * [7] origin      リポジトリ名(orワークプレース名)
   * [8] type        リポジトリエージェントなら"r", 違うなら"w"
   *
   * リポジトリエージェントの場合、
   * [0]=[1], [2]=[3]="", [4]=[7]=リポジトリ名, [5]=機能名, [6]=comment。
   *
   * これを変更する場合、NSInterfaceのSELECTORの順番も変える必要あり。
   * @see dash.NSInterface
   */
  Vector agentData;

  /**
   * 環境の記録簿。
   * keyは環境名。
   * valueはString[3]。
   * [0] ename    環境名 (w1:lynx:leo)
   * [1] etype    種別   (リポジトリならr, ワークプレースならw)
   * [2] ecomment コメント (This environment provides some functions for FVCS)
   */
  Hashtable envData;

  /**
   * コンストラクタ
   */
  public NameServer() throws RemoteException {
    agentData = new Vector();
    envData = new Hashtable();
    startupRMIregistry();
    try {
      Naming.rebind("nameserver", this);
    } catch (MalformedURLException e) {
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
   * 環境の起動時にその環境を登録する。
   * さらに、エージェント記録簿にその環境で起動しているエージェントがあれば
   * 削除する。
   * @param name 環境名(w1:lynx:leo)
   * @param isR リポジトリならtrue。
   * @return
   */
  public int registerEnv(String name, boolean isR) throws java.rmi.RemoteException {
    String[] data = new String[3];
    data[ENAME]    = name;
    data[ETYPE]    = (isR ? "r" : "w");
    data[ECOMMENT] = "";
    envData.put(name, data);

    removeAgentsOf(name);
    return envData.size();
  }

  /**
   * 環境の終了時などにその環境を削除する。
   */
  public void unregisterEnv(String name) throws java.rmi.RemoteException {
    envData.remove(name);
    removeAgentsOf(name);
  }

  /**
   * nameで指定された環境で動作しているエージェントのデータを削除する。
   */
  private void removeAgentsOf(String name) {
    for (Iterator i = agentData.iterator(); i.hasNext(); ) {
      String[] agent = (String[])i.next();
      if (agent[ENVIRONMENT].equals(name))
        i.remove();
    }
  }

  /**
   * エージェントの名前を登録する。
   * @param name 完全なエージェント名(Sample01.200208031614312:w1:lynx:leo)
   * @param environment 現在居る環境名(w1:taurus)
   * @param function 機能名(WebServer)
   * @param comment コメント
   * @return ネームサーバで一意(?)となる整数(無保証っぽい)
   */
  public int register(String name, String environment, String origin, String function, String comment) throws java.rmi.RemoteException {
    String[] data = new String[9];
    data[NAME] = name;
    data[ENVIRONMENT] = environment;
    data[FUNCTION] = function;
    data[COMMENT] = comment;
    data[ORIGIN] = origin;

    int p = name.indexOf('.');
    int q = name.indexOf(':');
    if (p==-1) {
      data[RNAME] = name;
      data[BIRTHDAY] = "";
      data[BIRTHPLACE] = "";
      data[TYPE] = "r";
    } else {
      data[RNAME] = name.substring(0, p);
      data[BIRTHDAY] = name.substring(p+1, q);
      data[BIRTHPLACE] = name.substring(q+1);
      data[TYPE] = "w";
    }

    agentData.addElement(data);
    return agentData.size();
  }

  /**
   * エージェントの名前を削除する。
   * @param name 完全なエージェント名(Sample01.200208031614312:w1:lynx:leo)
   * @param environment 現在居る環境名(w1:taurus)
   */
  public int unregister(String name, String environment) throws java.rmi.RemoteException {
    for (Iterator i = agentData.iterator(); i.hasNext(); ) {
      String[] agent = (String[])i.next();
      if (agent[NAME].equals(name) && agent[ENVIRONMENT].equals(environment)) {
        i.remove();
        break;
      }
    }
    return 1;
  }

  /**
   * 移動エージェントの環境を変更する
   * @param name 完全なエージェント名(Sample01.200208031614312:w1:lynx:leo)
   * @param oldEnvironment 今まで居た環境名(w1:taurus)
   * @param newEnvironment 新しい環境名(w1:taurus)
   */
  public int move(String name, String oldEnvironment, String newEnvironment) throws java.rmi.RemoteException {
    for (Iterator i = agentData.iterator(); i.hasNext(); ) {
      String[] agent = (String[])i.next();
      if (agent[NAME].equals(name) && agent[ENVIRONMENT].equals(oldEnvironment)) {
        agent[ENVIRONMENT] = newEnvironment;
        break;
      }
    }
    return 1;
  }

  /**
   * selectorで指定されたエージェントの情報を返す。
   * @param selector 一つの要素は、String[2]。
   * [0]は、条件指定子。":name", ":environment"など。
   * [1]は、その属性値。
   * @return マッチするものがない場合、空のVector。
   * マッチした場合、String[9]を要素とするVector。
   * 配列の添字の意味については、NSInterface参照。
   */
  public Vector lookup(String[][] selector) {
    // 準備(selector[*][0]をcode[*]に変換する)
    int size = selector.length;
    int[] code = new int[size];
    int all = NSInterface.SELECTOR.length;
    for (int i=0; i<size; i++) {
      String sel = selector[i][0];
      for (int j=0; j<all; j++)
        if (sel.equals(NSInterface.SELECTOR[j])) {
          code[i] = j;
          break;
        }
    }

    Vector matches = new Vector();
    Hashtable envhash = new Hashtable();

    // agentData[]内の全てのエージェントのデータを調べる
    EACH_AGENT:
    for (Enumeration e = agentData.elements(); e.hasMoreElements(); ) {
      String[] data = (String[])e.nextElement();

      // 一つのエージェントのデータに対し、selectorで指定された順に調べる
      for (int i=0; i<selector.length; i++) {
        int selectorCode = code[i];
        String sel = selector[i][1];

        // 比較するフィールドを取り出す
        String string = data[selectorCode/4]; // 4種類ずつあるから。

        // 比較
        boolean match = false;
        switch (selectorCode % 4) {
        case 0: match = (string.indexOf(sel) != -1); break; // ～を含む
        case 1: match = string.startsWith(sel);      break; // ～で始まる
        case 2: match = string.endsWith(sel);        break; // ～で終わる
        case 3: match = string.equals(sel);          break; // ～と一致する
        }

        if (sel.equals("*") ) {
          match = true;
        }
        if (!match)
          continue EACH_AGENT;
      }

      // 一致したよー
      //System.out.println(data[NAME]+" "+data[ENVIRONMENT]);

      // 環境が存在するか確かめる。存在するときだけマッチしたことにする。
      /*
      String env = data[ENVIRONMENT];
      boolean exist = false;
      if (envhash.get(env) != null)
        exist = true;
      else {
        int p = env.indexOf(':');
        String ename = env.substring(0, p);
        String ehost = env.substring(p);
        String url =
        Naming.lookup(
      }


      if (exist)*/
        matches.addElement(data);
    }

    return matches;
  }

  /** 全てのエージェントデータを返す */
  public Vector getAgentData() {
    return agentData;
  }

  /**
   * 登録されている全ての環境データを返す。
   * 要素はString[3]。内容は、
   * [0] 環境名
   * [1] 種別
   * [2] コメント
   */
  public Vector getEnvData() {
    Vector v = new Vector();
    for (Enumeration e = envData.elements(); e.hasMoreElements(); )
      v.addElement(e.nextElement());
    return v;
  }

  /* main */
  public static void main(String args[]) {
    try {
      NameServer ns = new NameServer();
    } catch (RemoteException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
