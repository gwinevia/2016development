package dash;

interface RmiInterface extends java.rmi.Remote {

  /** 他のDVMからのメッセージを受け取る */
  void putMsg(DashMessage msg) throws java.rmi.RemoteException;

}
