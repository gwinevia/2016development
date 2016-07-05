package dash;

import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

/**
 * �G�[�W�F���g�̖��O��ێ�����T�[�o�B
 */
public class NameServer extends UnicastRemoteObject implements NSInterface {

  /**
   * �G�[�W�F���g�̋L�^��B
   * �v�f��String[9]�B
   * [0] name        ���S�ȃG�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   * [1] rname       ���|�W�g���G�[�W�F���g��(Sample01)
   * [2] birthday    �������� (200208031614312)
   * [3] birthplace  �������ꂽ����(w1:lynx:leo)
   * [4] environment ���݋������(w1:taurus)
   * [5] function    �@�\��(WebServer)
   * [6] comment     �R�����g (This is a Web server agent.)
   * [7] origin      ���|�W�g����(or���[�N�v���[�X��)
   * [8] type        ���|�W�g���G�[�W�F���g�Ȃ�"r", �Ⴄ�Ȃ�"w"
   *
   * ���|�W�g���G�[�W�F���g�̏ꍇ�A
   * [0]=[1], [2]=[3]="", [4]=[7]=���|�W�g����, [5]=�@�\��, [6]=comment�B
   *
   * �����ύX����ꍇ�ANSInterface��SELECTOR�̏��Ԃ��ς���K�v����B
   * @see dash.NSInterface
   */
  Vector agentData;

  /**
   * ���̋L�^��B
   * key�͊����B
   * value��String[3]�B
   * [0] ename    ���� (w1:lynx:leo)
   * [1] etype    ���   (���|�W�g���Ȃ�r, ���[�N�v���[�X�Ȃ�w)
   * [2] ecomment �R�����g (This environment provides some functions for FVCS)
   */
  Hashtable envData;

  /**
   * �R���X�g���N�^
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
   * RMI���W�X�g�����N�����Ă��Ȃ���΋N������B
   */
  private void startupRMIregistry() {
    // �N�����Ă��邩���ׂ�B
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
   * ���̋N�����ɂ��̊���o�^����B
   * ����ɁA�G�[�W�F���g�L�^��ɂ��̊��ŋN�����Ă���G�[�W�F���g�������
   * �폜����B
   * @param name ����(w1:lynx:leo)
   * @param isR ���|�W�g���Ȃ�true�B
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
   * ���̏I�����Ȃǂɂ��̊����폜����B
   */
  public void unregisterEnv(String name) throws java.rmi.RemoteException {
    envData.remove(name);
    removeAgentsOf(name);
  }

  /**
   * name�Ŏw�肳�ꂽ���œ��삵�Ă���G�[�W�F���g�̃f�[�^���폜����B
   */
  private void removeAgentsOf(String name) {
    for (Iterator i = agentData.iterator(); i.hasNext(); ) {
      String[] agent = (String[])i.next();
      if (agent[ENVIRONMENT].equals(name))
        i.remove();
    }
  }

  /**
   * �G�[�W�F���g�̖��O��o�^����B
   * @param name ���S�ȃG�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   * @param environment ���݋������(w1:taurus)
   * @param function �@�\��(WebServer)
   * @param comment �R�����g
   * @return �l�[���T�[�o�ň��(?)�ƂȂ鐮��(���ۏ؂��ۂ�)
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
   * �G�[�W�F���g�̖��O���폜����B
   * @param name ���S�ȃG�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   * @param environment ���݋������(w1:taurus)
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
   * �ړ��G�[�W�F���g�̊���ύX����
   * @param name ���S�ȃG�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   * @param oldEnvironment ���܂ŋ�������(w1:taurus)
   * @param newEnvironment �V��������(w1:taurus)
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
   * selector�Ŏw�肳�ꂽ�G�[�W�F���g�̏���Ԃ��B
   * @param selector ��̗v�f�́AString[2]�B
   * [0]�́A�����w��q�B":name", ":environment"�ȂǁB
   * [1]�́A���̑����l�B
   * @return �}�b�`������̂��Ȃ��ꍇ�A���Vector�B
   * �}�b�`�����ꍇ�AString[9]��v�f�Ƃ���Vector�B
   * �z��̓Y���̈Ӗ��ɂ��ẮANSInterface�Q�ƁB
   */
  public Vector lookup(String[][] selector) {
    // ����(selector[*][0]��code[*]�ɕϊ�����)
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

    // agentData[]���̑S�ẴG�[�W�F���g�̃f�[�^�𒲂ׂ�
    EACH_AGENT:
    for (Enumeration e = agentData.elements(); e.hasMoreElements(); ) {
      String[] data = (String[])e.nextElement();

      // ��̃G�[�W�F���g�̃f�[�^�ɑ΂��Aselector�Ŏw�肳�ꂽ���ɒ��ׂ�
      for (int i=0; i<selector.length; i++) {
        int selectorCode = code[i];
        String sel = selector[i][1];

        // ��r����t�B�[���h�����o��
        String string = data[selectorCode/4]; // 4��ނ����邩��B

        // ��r
        boolean match = false;
        switch (selectorCode % 4) {
        case 0: match = (string.indexOf(sel) != -1); break; // �`���܂�
        case 1: match = string.startsWith(sel);      break; // �`�Ŏn�܂�
        case 2: match = string.endsWith(sel);        break; // �`�ŏI���
        case 3: match = string.equals(sel);          break; // �`�ƈ�v����
        }

        if (sel.equals("*") ) {
          match = true;
        }
        if (!match)
          continue EACH_AGENT;
      }

      // ��v������[
      //System.out.println(data[NAME]+" "+data[ENVIRONMENT]);

      // �������݂��邩�m���߂�B���݂���Ƃ������}�b�`�������Ƃɂ���B
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

  /** �S�ẴG�[�W�F���g�f�[�^��Ԃ� */
  public Vector getAgentData() {
    return agentData;
  }

  /**
   * �o�^����Ă���S�Ă̊��f�[�^��Ԃ��B
   * �v�f��String[3]�B���e�́A
   * [0] ����
   * [1] ���
   * [2] �R�����g
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
