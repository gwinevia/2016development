package dash;

import java.util.*;

/**
 * DVMの通信モジュールのインタフェース。
 * これを実装するクラスは、
 * (1)クラスRmiModule   (DASH-1.1相当の通信モジュール)
 * (2)クラスCysolModule (サイバーソリューションズの機能を持つ通信モジュール)
 */
interface ComInterface {

  /**
   * このDVMの情報を出力する。
   */
  void printInfo();

  /**
   * このDVMあるいは他のDVMにメッセージを送信する。
   * @return 成功したらtrue
   */
  boolean sendMsg(DashMessage dm);

  /** メッセージが来るのを待つ(ブロッキング) */
  DashMessage waitMsg();

  /** 動作を停止する */
  void finalizeModule();

  /**
   * DVMの名前を返す。
   * DVMの名前は通信モジュールが決める。
   */
  String getDVMname();

  /**
   * dvmnameで指定されたDVMが存在するかを調べる。
   * @return 存在する場合、true。
   */
  boolean checkDVM(String dvmname);

  /**
   * エージェント名をネームサーバに登録する。
   */
  int registerAgent(String name, String envname, String origin, String function, String comment);


  /**
   * エージェント名をネームサーバから削除する。
   */
  int unregisterAgent(String name, String envname);


  /**
   * エージェントが移動したことをネームサーバに通知する。
   */
  int moveAgent(String name, String oldEnvname, String newEnvname);


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
  public Vector lookup(String[][] selector);

  /** ADD COSMOS */
  public NSInterface getNameServer() ;
  public String getNameServerErrMsg() ;

}
