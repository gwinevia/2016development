package dash;

import java.util.*;

public interface NSInterface extends java.rmi.Remote {

  /** 環境を登録する */
  int registerEnv(String name, boolean isR) throws java.rmi.RemoteException;

  /** 登録した環境を削除する */
  void unregisterEnv(String name) throws java.rmi.RemoteException;

  /** エージェントの名前を登録する */
  int register(String name, String environment, String origin, String function, String comment) throws java.rmi.RemoteException;

  /** エージェントの名前を削除する */
  int unregister(String name, String environment) throws java.rmi.RemoteException;

  /** 移動エージェントの環境を変更する */
  int move(String name, String oldEnvironment, String newEnvironment) throws java.rmi.RemoteException;

  /**
   * selectorで指定されたエージェントの情報を返す。
   * @param selector 一つの要素は、String[2]。
   * [0]は、条件指定子。":name", ":environment"など。
   * [1]は、その属性値。
   * @return マッチするものがない場合、空のVector。
   * マッチした場合、String[9]を要素とするVector。
   * 配列の添字の意味については、NSInterface参照。
   */
  Vector lookup(String[][] selector) throws java.rmi.RemoteException;

  /**
   * 登録されている全てのエージェントデータを返す。
   * 要素はString[9]。内容は、NameServer.agentDataのコメント参照。
   */
  Vector getAgentData() throws java.rmi.RemoteException;

  /**
   * 登録されている全ての環境データを返す。
   * 要素はString[3]。内容は、
   * [0] 環境名
   * [1] 種別
   * [2] コメント
   */
  Vector getEnvData() throws java.rmi.RemoteException;

  /** 検索で指定できる条件 */
  public static final String[] SELECTOR = 
  { ":name",        ":Name",        ":namE",        ":NAME",
    ":rname",       ":Rname",       ":rnamE",       ":RNAME",
    ":birthday",    ":Birthday",    ":birthdaY",    ":BIRTHDAY",
    ":birthplace",  ":Birthplace",  ":birthplacE",  ":BIRTHPLACE",
    ":environment", ":Environment", ":environmenT", ":ENVIRONMENT",
    ":function",    ":Function",    ":functioN",    ":FUNCTION",
    ":comment",     ":Comment",     ":commenT",     ":COMMENT",
    ":origin",      ":Origin",      ":origiN",      ":ORIGIN",
    ":type",        ":Type",        ":typE",        ":TYPE"      };

  /**
   * lookupで返るVectorの要素String[9]の添字の意味
   */
  public static final int NAME        = 0;
  public static final int RNAME       = 1;
  public static final int BIRTHDAY    = 2;
  public static final int BIRTHPLACE  = 3;
  public static final int ENVIRONMENT = 4;
  public static final int FUNCTION    = 5;
  public static final int COMMENT     = 6;
  public static final int ORIGIN      = 7;
  public static final int TYPE        = 8;

  /**
   * 環境
   */
  public static final int ENAME    = 0;
  public static final int ETYPE    = 1;
  public static final int ECOMMENT = 2;


}
