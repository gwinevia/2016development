package dash;

interface RmiInterface extends java.rmi.Remote {

  /** ����DVM����̃��b�Z�[�W���󂯎�� */
  void putMsg(DashMessage msg) throws java.rmi.RemoteException;

}
