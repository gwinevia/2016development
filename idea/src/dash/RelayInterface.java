package dash;

import java.util.*;

interface RelayInterface extends java.rmi.Remote {

  /** 名前の決定をする */
  String resolveDVMname(String n, String candidates, String hostname, String relayHosts, String relayList) throws java.rmi.RemoteException;

  /**
   * 指定されたDVMがこの中継サーバを使っているかを調べる。
   * この中継サーバを使っていないDVMが呼び出す。
   */
  boolean knowsDVM(String dvmname) throws java.rmi.RemoteException;

  /**
   * 指定されたDVMが存在しているか調べる。
   * この中継サーバを使っているDVM(or中継サーバ)が呼び出す。
   */
  boolean checkDVM(String dvmname, String relayList) throws java.rmi.RemoteException;

  /** 他のDVMからのメッセージを受け取る */
  void putMsg(DashMessage msg, String relayList) throws java.rmi.RemoteException;

  /** メッセージが届くのを待つ */
  DashMessage waitMsg(String fullname, String relayList) throws java.rmi.RemoteException;

  /** ネームサーバにDVMを登録する */
  int registerEnv(String name, boolean isR, String relayList, String servername) throws java.rmi.RemoteException;

  /** ネームサーバからDVMを削除する */
  void unregisterEnv(String name, String relayList, String servername) throws java.rmi.RemoteException;

  /** ネームサーバにエージェントを登録する */
  int register(String name, String envname, String origin, String function, String comment, String relayList, String servername) throws java.rmi.RemoteException;

  /** ネームサーバからエージェントを削除する */
  int unregister(String name, String envname, String relayList, String servername) throws java.rmi.RemoteException;

  /** ネームサーバにエージェントが移動したことを通知する */
  int move(String name, String oldEnvname, String newEnvname, String relayList, String servername) throws java.rmi.RemoteException;

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
  Vector lookup(String[][] selector, String relayList, String servername) throws java.rmi.RemoteException;

}
