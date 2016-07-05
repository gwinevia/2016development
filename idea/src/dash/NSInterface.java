package dash;

import java.util.*;

public interface NSInterface extends java.rmi.Remote {

  /** ����o�^���� */
  int registerEnv(String name, boolean isR) throws java.rmi.RemoteException;

  /** �o�^���������폜���� */
  void unregisterEnv(String name) throws java.rmi.RemoteException;

  /** �G�[�W�F���g�̖��O��o�^���� */
  int register(String name, String environment, String origin, String function, String comment) throws java.rmi.RemoteException;

  /** �G�[�W�F���g�̖��O���폜���� */
  int unregister(String name, String environment) throws java.rmi.RemoteException;

  /** �ړ��G�[�W�F���g�̊���ύX���� */
  int move(String name, String oldEnvironment, String newEnvironment) throws java.rmi.RemoteException;

  /**
   * selector�Ŏw�肳�ꂽ�G�[�W�F���g�̏���Ԃ��B
   * @param selector ��̗v�f�́AString[2]�B
   * [0]�́A�����w��q�B":name", ":environment"�ȂǁB
   * [1]�́A���̑����l�B
   * @return �}�b�`������̂��Ȃ��ꍇ�A���Vector�B
   * �}�b�`�����ꍇ�AString[9]��v�f�Ƃ���Vector�B
   * �z��̓Y���̈Ӗ��ɂ��ẮANSInterface�Q�ƁB
   */
  Vector lookup(String[][] selector) throws java.rmi.RemoteException;

  /**
   * �o�^����Ă���S�ẴG�[�W�F���g�f�[�^��Ԃ��B
   * �v�f��String[9]�B���e�́ANameServer.agentData�̃R�����g�Q�ƁB
   */
  Vector getAgentData() throws java.rmi.RemoteException;

  /**
   * �o�^����Ă���S�Ă̊��f�[�^��Ԃ��B
   * �v�f��String[3]�B���e�́A
   * [0] ����
   * [1] ���
   * [2] �R�����g
   */
  Vector getEnvData() throws java.rmi.RemoteException;

  /** �����Ŏw��ł������ */
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
   * lookup�ŕԂ�Vector�̗v�fString[9]�̓Y���̈Ӗ�
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
   * ��
   */
  public static final int ENAME    = 0;
  public static final int ETYPE    = 1;
  public static final int ECOMMENT = 2;


}
