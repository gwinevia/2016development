package dash;

import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

/**
 * ���b�Z�[�W�𒆌p����T�[�o�B�v���C�x�[�g�A�h���X�Ԃ̒ʐM�ȂǂɎg���B
 * 1�̃z�X�g�ɍő�1���삷��B
 * ����2�̋@�\�����B
 * (1)�N���C�A���gDVM�ւ̃��b�Z�[�W���o�b�t�@�����O����@�\
 * (2)�N���C�A���gDVM����̃��b�Z�[�W�擾�v�����󂯕t����B
 */
public class RelayServer extends UnicastRemoteObject implements RelayInterface {

  /**
   * key�͒��p�T�[�o�𗘗p���Ă���DVM�̖��O(w1:lynx:taurus�Ȃ�)�B
   * value�̓��b�Z�[�W�𓞒����ɕێ�����Vector�B
   */
  Hashtable folders;

  int counter;

  /** ���̒��p�T�[�o�̃z�X�g�� */
  String myHostname;

  /**
   * �R���X�g���N�^
   */
  public RelayServer(String name) throws RemoteException {
    folders = new Hashtable();
    startupRMIregistry();
    counter = 0;
    try {
      if (name != null)
        myHostname = name;
      else
        myHostname = InetAddress.getLocalHost().getHostName();
      Naming.rebind("relayserver", this);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (java.net.UnknownHostException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("My hostname = "+myHostname);
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
   * DVM�̖��O�����߂āA�z�X�g�����܂܂Ȃ����O��Ԃ��B
   * (DVM�̖��O��"w1:lynx:aries"�Ȃ�A"w1"��Ԃ�)
   * ���������ꍇ�A���̃z�X�g��o�^����B
   * ���s�����ꍇ�A"error "�Ŏn�܂镶�����Ԃ��B
   * @param n ���[�U���w�肵�����O
   * @param candidates dash.[rw].name�Ŏw�肵�����O(,�ŋ�؂��Ă���)
   * @param hostname DVM���N�������z�X�g��
   * @param relayHosts ":���p�T�[�o:���p�T�[�o ..."�Ȃ镶����
   * @param relayList ����ȍ~�̒��p�T�[�o(�ĂԂ��тɌ����čs��)
   */
  public String resolveDVMname(String n, String candidates, String hostname, String relayHosts, String relayList) throws java.rmi.RemoteException {
    String name = null;

    if (relayList.equals("")) {
      String fullname = null;
      RESOLVE:
      if (n != null) {
        name = n;
        fullname = n + ":" + hostname + relayHosts;
        if (folders.get(fullname) != null) {
          name = "error " + fullname + "�Ƃ�������DVM�́A���ɑ��݂��܂��B";
          fullname = null;
        }
      } else {
        StringTokenizer st = new StringTokenizer(candidates, ",");
        while (st.hasMoreTokens()) {
          name = st.nextToken();
          fullname = name + ":" + hostname + relayHosts;
          if (folders.get(fullname) == null)
            break RESOLVE;
        }
        name = "error ���O������܂���B";
        fullname = null;
      }
      if (fullname != null)
        folders.put(fullname, new Vector());
      System.out.println("��"+name);
      System.out.println("��"+fullname);

    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        name = neighbor.resolveDVMname(n, candidates, hostname, relayHosts, relayList);
      } catch (NotBoundException e) {
        e.printStackTrace();
        name = "error "+relayList+"�ȍ~�̃z�X�g��������܂���B";
      } catch (MalformedURLException e) {
        e.printStackTrace();
        name = "error "+next+"�Ƃ����z�X�g��������܂���B";
      }
    }

    return name;
  }
  /*
  public String resolveDVMname(String n, String candidates, String hostname, String relayHosts, Vector relayHostsV) throws java.rmi.RemoteException {
    String name = null;
    String fullname = null;
    String next = null;

    RESOLVE:
    if (relayHostsV.size() > 0) {
      System.out.println("kita");
      try {
        next = (String)relayHostsV.remove(0);
        String url = "rmi://" + next + "/relayserver";
        System.out.println(url);
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        name = neighbor.resolveDVMname(n, candidates, hostname, relayHosts, relayHostsV);
        fullname = null;
      } catch (NotBoundException e) {
        e.printStackTrace();
        StringBuffer buf = new StringBuffer();
        for (Enumeration h = relayHostsV.elements(); h.hasMoreElements(); )
          buf.append(":"+h.nextElement());
        name = "error "+buf+"�ȍ~�̃z�X�g��������܂���B";
      } catch (MalformedURLException e) {
        e.printStackTrace();
        name = "error "+next+"�Ƃ����z�X�g��������܂���B";
      }

    } else if (n != null) {
      name = n;
      fullname = n + ":" + hostname + relayHosts;
      if (folders.get(fullname) != null) {
        name = "error " + fullname + "�Ƃ�������DVM�́A���ɑ��݂��܂��B";
        fullname = null;
      }

    } else {
      StringTokenizer st = new StringTokenizer(candidates, ",");
      while (st.hasMoreTokens()) {
        name = st.nextToken();
        fullname = name + ":" + hostname + relayHosts;
        if (folders.get(fullname) == null)
          break RESOLVE;
      }
      name = "error ���O������܂���B";
      fullname = null;
    }

    if (fullname != null)
      folders.put(fullname, new Vector());
    System.out.println("��"+name);
    System.out.println("��"+fullname);
    return name;
  }
  */

  /**
   * dvmname�Ŏw�肳�ꂽDVM�����̒��p�T�[�o���g���Ă���ꍇ��true��Ԃ��B
   * ���̒��p�T�[�o���g���Ă��Ȃ�DVM(or���p�T�[�o)���Ăяo���B
   * @see checkDVM()
   */
  public boolean knowsDVM(String dvmname) {
    return folders.get(dvmname) != null;
  }
  
  /**
   * �w�肳�ꂽDVM�����݂��Ă��邩���ׂ�B
   * ���̒��p�T�[�o���g���Ă���DVM(or���p�T�[�o)���Ăяo���B
   * @see knowsDVM()
   */
  public boolean checkDVM(String dvmname, String relayList) {
    if (relayList.equals("")) {
      // RmiModule.checkDVM()�Ɠ����悤�Ȃ��Ƃ�����
      boolean useRelay = (dvmname.indexOf(':') != dvmname.lastIndexOf(':'));
      if (!useRelay) {
        int p = dvmname.lastIndexOf(':');
        String ename = dvmname.substring(0, p);
        String ehost = dvmname.substring(p+1);
        String url = "rmi://"+ehost+"/"+ename;
        try {
          RmiInterface module = (RmiInterface)Naming.lookup(url);
        } catch (Exception e) { return false; }
        return true;
      } else {
        int p = dvmname.lastIndexOf(':');
        String ehost = dvmname.substring(p+1);
        String url = "rmi://"+ehost+"/relayserver";
        try {
          RelayInterface module = (RelayInterface)Naming.lookup(url);
          return module.knowsDVM(dvmname);
        } catch (Exception e) {
          return false;
        }
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        return neighbor.checkDVM(dvmname, newList);
      } catch (Exception e) {
        return false;
      }
    }
  }


  /**
   * ���̒��p�T�[�o�𗘗p���Ă���(�����炭�̓v���C�x�[�g�̓�����)DVM��
   * ���b�Z�[�W��͂���B����DVM�́AgetMsg()�Ń��b�Z�[�W��҂B
   * 4�ʂ�̏������s���B
   * �ErelayList����̏ꍇ�A
   *   - arrival�����̃z�X�g�ł͂Ȃ��ꍇ:
   *     (1)���̃z�X�g��putMsg(M)����(���p�T�[�o���g���Ă���ꍇputMsg(M,""))
   *   - arrival�����̃z�X�g�̏ꍇ:
   *     - arrival��1����:���܂ޏꍇ(���p�T�[�o�𗘗p���Ă��Ȃ��ꍇ)
   *       (2)���̃z�X�g��putMsg(M)����B
   *     - arrival�ɕ�����:���܂܂��ꍇ(���p�T�[�o�𗘗p���Ă���ꍇ)
   *       (3)���b�Z�[�W���󂯎��B
   * �ErelayList����łȂ��ꍇ�A
   *   - (4)����ɒ��p����(putMsg(M,l))�B
   * @param msg ���b�Z�[�W
   * @param relayList ""���邢�́A����ɓ]�����钆�p�T�[�o�̖��O
   */
  public void putMsg(DashMessage msg, String relayList) throws java.rmi.RemoteException {
    if (relayList.equals("")) {
      String fullname = msg.arrival; // w1:leo, w1:lynx:leo�Ȃ�
      int p = fullname.lastIndexOf(':');
      String target = fullname.substring(p+1);
      boolean useRelay = (fullname.indexOf(':') != fullname.lastIndexOf(':'));

      if (!target.equals(myHostname) || !useRelay) {
        //(1)arrival���ʂ̃z�X�g�̏ꍇ
        //(2)���̃z�X�g�Œ��p�T�[�o���g���Ă��Ȃ��ꍇ
        try {
          if (useRelay) {
            String ename = "relayserver";
            String ehost = fullname.substring(p+1);
            String url = "rmi://"+ehost+"/"+ename;
            RelayInterface relay = (RelayInterface)Naming.lookup(url);
            relay.putMsg(msg, "");
          } else {
            String ename = fullname.substring(0, p);
            String ehost = fullname.substring(p+1);
            String url = "rmi://"+ehost+"/"+ename;
            RmiInterface server = (RmiInterface)Naming.lookup(url);
            server.putMsg(msg);
          }
        } catch (Exception e) {
          System.err.println("RelayServer.putMsg(): "+msg+"��"+
                             fullname+"�ɑ���܂���B");
          e.printStackTrace();
        }
      } else {
        //(3)arrival�����̃z�X�g�̏ꍇ: folders�Ɋi�[����B
        Vector vector = (Vector)folders.get(fullname);
        if (vector != null) {
          synchronized (vector) {
            vector.add(msg);
            vector.notify();
          }
        } else {
          System.err.println("error: �o�^����Ă��Ȃ�DVM�ւ̃��b�Z�[�W�ł�: "+
                             msg);
        }
      }
    } else {
      //(4)����ɒ��p����B
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        neighbor.putMsg(msg, newList);
      } catch (Exception e) {
        System.err.println("RelayServer.putMsg(): "+msg+"��"+
                           relayList+"�ɒ��p�ł��܂���");
        e.printStackTrace();
      }
    }

  }
  

  /**
   * ���̒��p�T�[�o�ɓ͂������b�Z�[�W�̂����A
   * fullname���̂��̂�Ԃ��B
   * 10000ms�����҂B
   * @param fullname DVM��
   * @param relayList ""���邢�́A����ɓ]�����钆�p�T�[�o�̖��O
   */
  public DashMessage waitMsg(String fullname, String relayList) throws java.rmi.RemoteException {
    DashMessage msg = null;
    if (relayList.equals("")) {
      Vector vector = (Vector)folders.get(fullname);
      if (vector != null) {
        synchronized (vector) {
          try {
            if (vector.isEmpty())
              vector.wait(10000);
          } catch (Exception e) { e.printStackTrace(); }
          if (!vector.isEmpty())
            msg = (DashMessage)vector.remove(0);
          System.out.println(counter+": "+msg);
          counter++;
        }
      } else {
        System.err.println("error: �o�^����Ă��܂���: "+fullname);
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        msg = neighbor.waitMsg(fullname, newList);
      } catch (Exception e) {
        System.err.println("RelayServer.waitMsg(): "+relayList+"�ȍ~��waitMsg()�ł��܂���B");
        e.printStackTrace();
      }
    }

    return msg;
  }

  /**
   * �l�[���T�[�o�Ɋ�����o�^����B
   * @param name ����
   * @param relayList ���p�T�[�o�̃��X�g
   * @param servername �l�[���T�[�o
   * @return �w�肵���l�[���T�[�o�����݂��Ȃ����A���̃z�X�g�Ńl�[���T�[�o��
   *  �N�����Ă��Ȃ��ꍇ�A-1�B
   */
  public int registerEnv(String name, boolean isR, String relayList, String servername) throws RemoteException {
    int result = 0;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.registerEnv(name, isR);
      } catch (NotBoundException e) {
        result = -1;
      } catch (MalformedURLException e) {
        result = -1;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.registerEnv(name, isR, newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.registerEnv(): "+relayList+"�ȍ~��registerEnv()�ł��܂���B");
        e.printStackTrace();
        result = -1;
      }
    }
    
    return result;
  }
  
  /**
   * �l�[���T�[�o����������폜����B
   * @param name ����
   * @param relayList ���p�T�[�o�̃��X�g
   * @param servername �l�[���T�[�o
   */
  public void unregisterEnv(String name, String relayList, String servername) throws RemoteException {
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        server.unregisterEnv(name);
      } catch (NotBoundException e) {
      } catch (MalformedURLException e) {
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        neighbor.unregisterEnv(name, newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.unregisterEnv(): "+relayList+"�ȍ~��unregisterEnv()�ł��܂���B");
        e.printStackTrace();
      }
    }
  }
  
  /**
   * �l�[���T�[�o�ɃG�[�W�F���g����o�^����B
   * @param name �G�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   *             ���|�W�g���G�[�W�F���g�̏ꍇ��Sample01�̂݁B
   * @param envname ���݋������(w1:lynx:leo)
   * @param function �@�\��
   * @param comment �R�����g
   * @param relayList ���p�T�[�o�̃��X�g
   * @param servername �l�[���T�[�o
   * @return �w�肵���l�[���T�[�o�����݂��Ȃ����A���̃z�X�g�Ńl�[���T�[�o��
   *  �N�����Ă��Ȃ��ꍇ�A-1�B
   */
  public int register(String name, String envname, String origin, String function, String comment, String relayList, String servername) throws RemoteException {
    int result = 0;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.register(name, envname, origin, function, comment);
      } catch (NotBoundException e) {
        result = -1;
      } catch (MalformedURLException e) {
        result = -1;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.register(name, envname, origin, function, comment,
                                   newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.register(): "+relayList+"�ȍ~��register()�ł��܂���B");
        e.printStackTrace();
        result = -1;
      }
    }
    
    return result;
  }
  
  /**
   * �l�[���T�[�o����G�[�W�F���g�����폜����B
   * @param name �G�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   *             ���|�W�g���G�[�W�F���g�̏ꍇ��Sample01�̂݁B
   * @param envname ���݋������(w1:lynx:leo)
   * @param relayList ���p�T�[�o�̃��X�g
   * @param servername �l�[���T�[�o
   * @return �w�肵���l�[���T�[�o�����݂��Ȃ����A���̃z�X�g�Ńl�[���T�[�o��
   *  �N�����Ă��Ȃ��ꍇ�A-1�B
   */
  public int unregister(String name, String envname, String relayList, String servername) throws RemoteException {
    int result = 0;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.unregister(name, envname);
      } catch (NotBoundException e) {
        result = -1;
      } catch (MalformedURLException e) {
        result = -1;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.unregister(name, envname,
                                     newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.unregister(): "+relayList+"�ȍ~��register()�ł��܂���B");
        e.printStackTrace();
        result = -1;
      }
    }
    
    return result;
  }
  
  /**
   * �l�[���T�[�o�ɃG�[�W�F���g���ړ��������Ƃ�ʒm����B
   * @param name �G�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   *             ���|�W�g���G�[�W�F���g�̏ꍇ��Sample01�̂݁B
   * @param oldEnvname ���܂ŋ�������(w1:lynx:leo)
   * @param newEnvname �V��������(w1:lynx:leo)
   * @param relayList ���p�T�[�o�̃��X�g
   * @param servername �l�[���T�[�o
   * @return �w�肵���l�[���T�[�o�����݂��Ȃ����A���̃z�X�g�Ńl�[���T�[�o��
   *  �N�����Ă��Ȃ��ꍇ�A-1�B
   */
  public int move(String name, String oldEnvname, String newEnvname, String relayList, String servername) throws RemoteException {
    int result = 0;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.move(name, oldEnvname, newEnvname);
      } catch (NotBoundException e) {
        result = -1;
      } catch (MalformedURLException e) {
        result = -1;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.move(name, oldEnvname, newEnvname,
                               newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.move(): "+relayList+"�ȍ~��move()�ł��܂���B");
        e.printStackTrace();
        result = -1;
      }
    }
    
    return result;
  }
  
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
  public Vector lookup(String[][] selector, String relayList, String servername) throws RemoteException {
    Vector result = null;
    if (relayList.equals("")) {
      try {
        String url = "rmi://"+servername+"/nameserver";
        NSInterface server = (NSInterface)Naming.lookup(url);
        result = server.lookup(selector);
      } catch (NotBoundException e) {
        result = null;
      } catch (MalformedURLException e) {
        result = null;
      }
    } else {
      int p = relayList.indexOf(":", 1);
      String next = (p>1 ? relayList.substring(1, p) : relayList.substring(1));
      String newList = (p>1 ? relayList.substring(p) : "");
      String url = "rmi://"+next+"/relayserver";
      try {
        RelayInterface neighbor = (RelayInterface)Naming.lookup(url);
        result = neighbor.lookup(selector,
                                 newList, servername);
      } catch (Exception e) {
        System.err.println("RelayServer.lookup(): "+relayList+"�ȍ~��lookup()�ł��܂���B");
        e.printStackTrace();
        result = null;
      }
    }
    
    return result;
  }
  

  /**
   * �N���B���p�T�[�o�̃z�X�g�����w�肷�邱�Ƃ��ł���B
   * (���̃z�X�g�����̖��O�ŃA�N�Z�X�ł��邱�Ƃ��K�v)
   */
  public static void main(String args[]) {
    try {
      String name = null;
      if (args.length == 1)
        name = args[0];
      RelayServer server = new RelayServer(name);
    } catch (RemoteException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
