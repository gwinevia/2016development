package dash;

import java.util.*;

interface RelayInterface extends java.rmi.Remote {

  /** ���O�̌�������� */
  String resolveDVMname(String n, String candidates, String hostname, String relayHosts, String relayList) throws java.rmi.RemoteException;

  /**
   * �w�肳�ꂽDVM�����̒��p�T�[�o���g���Ă��邩�𒲂ׂ�B
   * ���̒��p�T�[�o���g���Ă��Ȃ�DVM���Ăяo���B
   */
  boolean knowsDVM(String dvmname) throws java.rmi.RemoteException;

  /**
   * �w�肳�ꂽDVM�����݂��Ă��邩���ׂ�B
   * ���̒��p�T�[�o���g���Ă���DVM(or���p�T�[�o)���Ăяo���B
   */
  boolean checkDVM(String dvmname, String relayList) throws java.rmi.RemoteException;

  /** ����DVM����̃��b�Z�[�W���󂯎�� */
  void putMsg(DashMessage msg, String relayList) throws java.rmi.RemoteException;

  /** ���b�Z�[�W���͂��̂�҂� */
  DashMessage waitMsg(String fullname, String relayList) throws java.rmi.RemoteException;

  /** �l�[���T�[�o��DVM��o�^���� */
  int registerEnv(String name, boolean isR, String relayList, String servername) throws java.rmi.RemoteException;

  /** �l�[���T�[�o����DVM���폜���� */
  void unregisterEnv(String name, String relayList, String servername) throws java.rmi.RemoteException;

  /** �l�[���T�[�o�ɃG�[�W�F���g��o�^���� */
  int register(String name, String envname, String origin, String function, String comment, String relayList, String servername) throws java.rmi.RemoteException;

  /** �l�[���T�[�o����G�[�W�F���g���폜���� */
  int unregister(String name, String envname, String relayList, String servername) throws java.rmi.RemoteException;

  /** �l�[���T�[�o�ɃG�[�W�F���g���ړ��������Ƃ�ʒm���� */
  int move(String name, String oldEnvname, String newEnvname, String relayList, String servername) throws java.rmi.RemoteException;

  /**
   * selector�Ŏw�肳�ꂽ�G�[�W�F���g�̏���Ԃ��B
   * @param selector ��̗v�f�́AString[2]�B
   * [0]�́A�����w��q�B":name", ":environment"�ȂǁB
   * [1]�́A���̑����l�B
   * @return �}�b�`������̂��Ȃ��ꍇ�A���Vector�B
   * �}�b�`�����ꍇ�AString[9]��v�f�Ƃ���Vector�B
   * �w�肵���l�[���T�[�o�����݂��Ȃ����A���̃z�X�g�Ńl�[���T�[�o��
   * �N�����Ă��Ȃ��ꍇ�Anull�B
   * �z��̓Y���̈Ӗ��ɂ��ẮANSInterface�Q�ƁB
   */
  Vector lookup(String[][] selector, String relayList, String servername) throws java.rmi.RemoteException;

}
