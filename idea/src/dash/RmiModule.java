package dash;

import java.util.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;
/*
 * DASH-1.1�����̒ʐM���W���[��
 * �X���b�h�͌ďo���ō��B
 *
 * RMI�Ń��b�Z�[�W�̑���M���s���B
 *
 * DVM�̖��O�́A
 *   name : hostname relayHosts
 * �݂����Ȋ����B
 *
 * ���p�T�[�o���g��Ȃ��ꍇ�ADVM���N�������z�X�g��RMI�T�[�o���N������B
 * �����[�g�T�[�o�́Armi://localhost/w1
 *
 * ���p�T�[�o���g���ꍇ�ADVM���N�������z�X�g�ł�RMI�T�[�o�͋N�����Ȃ��B
 * �����[�g�T�[�o�́Armi://relayhost/relayserver
 */
class RmiModule extends UnicastRemoteObject implements ComInterface, RmiInterface {

  /** DVM */
  private DVM dvm;

  /** DVM��(�z�X�g���܂܂Ȃ�) */
  private String name;

  /** DVM�̋N�����Ă���z�X�g���B*/
  private String hostname;

  /** ���p�T�[�o�̃��X�g�B
   * ":server1:server2:server3"�̂悤�ȁA":�T�[�o��"��0�ȏ�̕��сB
   * ���p�T�[�o���g��Ȃ��ꍇ��null�B
   */
  private String relayHosts;

  /** �ׂ̒��p�T�[�o�̖��O�B���p�T�[�o���g��Ȃ��ꍇ��null�B */
  private String neighborHostname;

  /** �ׂ̒��p�T�[�o�̃����[�g�I�u�W�F�N�g�B���p�T�[�o���g��Ȃ��ꍇ��null�B */
  private RelayInterface neighborHost;

  /** �������b�Z�[�W�L���[ */
  private Vector queue;

  /** �����[�g�I�u�W�F�N�g�̃L���b�V�� */
  private Hashtable moduleCache;

  /** ���b�Z�[�W��z�������� */
  private static int deliveryClass = 0;
  private static final int NO_RMI  = 1;  // �l�b�g���[�N���g��Ȃ��B
  private static final int RMI     = 2;  // RMI�T�[�o�����[�J���ɋN������B
  private static final int RELAY   = 3;  // ���p�T�[�o���g���B

  /**
   * RMI���g���ꍇ�Anull�B
   * RMI���g��Ȃ��ꍇ�A"r1._localhost"��"w1._localhost"��
   * ���|�W�g���ƃ��[�N�v���[�X��RmiModule������B
   */
  private static Hashtable localNaming;

  /** RMI�̎��A�l�[���T�[�o�̃����[�g�I�u�W�F�N�g�BNO_RMI,RELAY�̎��Anull�B*/
  private NSInterface nameserver;

  /** �l�[���T�[�o���g���ꍇ�A���̃z�X�g���B�g��Ȃ��ꍇ�Anull�B*/
  private String nameserverHost;

  /** �l�[���T�[�o�[�N�����̃G���[���b�Z�[�W */
  private String NameServerErrMsg = "";

  /** �C���X�^���X������ĕԂ��B*/
  static ComInterface createNew(DVM dvm, String n, boolean local) {
    if (System.getProperty("language") == null ) {
      System.setProperty("language","japanese");
    }
    //String relayHosts = "192.168.104.3";//System.getProperty("dash.relayHosts");
    String relayHosts = System.getProperty("dash.relayHosts");
    if (local)
      deliveryClass = NO_RMI;
    else if (relayHosts == null || relayHosts.equals(""))
      deliveryClass = RMI;
    else
      deliveryClass = RELAY;

    ComInterface comInt = null;
    try {
      comInt = new RmiModule(dvm, n);
    } catch (RemoteException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return comInt;
  }


  //���O�ۊǂ��邩�ǂ��� create 05/02/17
  private boolean USELOG = false;
  
  /**
   * �R���X�g���N�^
   * @param dvm   DVM
   * @param n     �z�X�g�����܂܂Ȃ�����
   * @param local RMI���g��Ȃ��ꍇtrue
   */
  RmiModule(DVM dvm, String n) throws RemoteException {
  	String prop=null;
  	//���O�ۊǂ̗L��
  	 if ((prop=System.getProperty("dash.log"))!=null &&
			prop.equalsIgnoreCase("on")){
 			USELOG = true;
  	//System.out.println("log���g���܂��B"+this+" "+dvm);
			}
		//else{
		//System.out.println("log���g���܂���B"+this+" "+dvm);
		//}
    this.dvm = dvm;
    queue = new Vector();
    moduleCache = new Hashtable();

    switch (deliveryClass) {
    case NO_RMI:
      name = dvm.isRtype() ? "r1" : "w1";
      hostname = DashMessage.LOCALHOST;
      if (localNaming == null)
        localNaming = new Hashtable();
      localNaming.put(getDVMname(), this);
      break;
    case RMI:
      try {
        initializeForRMI(n);
      } catch (RemoteException e) {
        //e.printStackTrace();
        nameserver = null;
        if (System.getProperty("language").equals("japanese") ) {
          NameServerErrMsg = "�l�[���T�[�o�̋N���ŃG���[���������Ă��܂��B";
        }
        else {
          NameServerErrMsg = "The error has occurred in starting of a name server.";
        }
      }

      break;
    case RELAY:
      initializeForRelay(n);
      break;
    }
  }

  /**
   * ���p�T�[�o���g��Ȃ��ꍇ�̏���
   */
  private void initializeForRMI(String n) throws RemoteException {
    // RMI�T�[�o�N��
    startupRMIregistry();

    try {
      // DVM���E�z�X�g������
      name = resolveDVMname(n, dvm.isRtype());

      // ���[�J���z�X�g���擾(.ac.jp�܂Ŏ���ꍇ�Ǝ��Ȃ��ꍇ������)
      hostname = new String(InetAddress.getLocalHost().getHostName());
      //hostname = "CSMSXPCLNT01";
      // �o�^
      Naming.rebind(name, this);

    } catch (java.net.UnknownHostException e) {
      if (System.getProperty("language").equals("japanese") ) {
        System.err.println("localhost���s���ł��B");
      }
      else {
        System.err.println("localhost is unknown.");
      }

      System.exit(1);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // �l�[���T�[�o�̃`�F�b�N
    String nshost = System.getProperty("dash.nameserver");
    if (nshost == null)
      nameserver = null;
    else {
      try {
        String url = "rmi://"+nshost+"/nameserver";
        //System.out.println(url);
        nameserver = (NSInterface)Naming.lookup(url);
        nameserver.registerEnv(getDVMname(), dvm.isRtype());
        nameserverHost = nshost;
      } catch (java.rmi.UnknownHostException e) {
        if (System.getProperty("language").equals("japanese") ) {
          System.err.println("�l�[���T�[�o"+nshost+"���s���ł��B");
          nameserver = null;
          NameServerErrMsg = "�l�[���T�[�o"+nshost+"���s���ł��B";
        }
        else {
          System.err.println("NameServer("+nshost+") is unknown.");
          nameserver = null;
          NameServerErrMsg ="NameServer("+nshost+") is unknown.";
        }
        //System.exit(1);
      } catch (java.rmi.NotBoundException e) {
        if (System.getProperty("language").equals("japanese") ) {
          System.err.println("�z�X�g"+nshost+"�����݂��Ȃ����A���邢�͂��̃z�X�g�Ńl�[���T�[�o���N�����Ă��܂���B");
          nameserver = null;
          NameServerErrMsg = "�z�X�g"+nshost+"�����݂��Ȃ����A���邢�͂��̃z�X�g�Ńl�[���T�[�o���N�����Ă��܂���B";
        }
        else {
          System.err.println("A host("+nshost+") does not exist or the name server has not started by this host");
          nameserver = null;
          NameServerErrMsg = "A host("+nshost+") does not exist or the name server has not started by this host";
        }
        //System.exit(1);
      } catch (MalformedURLException e) {
        e.printStackTrace();
        nameserver = null;
        NameServerErrMsg = e.toString();
        //System.exit(1);
      }
    }
  }

  /**
   * ���p�T�[�o���g���ꍇ�̏����B
   * �ϐ�neighborHost��neighborHostname���Z�b�g����B
   * RMI�T�[�o�͋N�����Ȃ��B
   */
  private void initializeForRelay(String n) throws RemoteException {
    // ���p�T�[�o�̃z�X�g���擾
    relayHosts = System.getProperty("dash.relayHosts");
    System.out.println("koko"+relayHosts.toString());
    //relayHosts = "192.168.104.3";//System.getProperty("dash.relayHosts");
    StringTokenizer st = new StringTokenizer(relayHosts, ":");
    Vector relayHostsV = new Vector();
    while (st.hasMoreTokens())
      relayHostsV.add(st.nextToken());
    neighborHostname = (String)relayHostsV.remove(0);

    String target = "���p�T�[�o";
    try {
      // ���[�J���z�X�g���擾(.ac.jp�܂Ŏ���ꍇ�Ǝ��Ȃ��ꍇ������)
      hostname = InetAddress.getLocalHost().getHostName();

      // DVM���E�z�X�g������
      String rw = (dvm.isRtype() ? "r" : "w");
      String names = System.getProperty("dash." + rw + ".name");
      String url = "rmi://"+neighborHostname+"/relayserver";
      neighborHost = (RelayInterface)Naming.lookup(url);
      int p = relayHosts.indexOf(":", 1);
      String relayList = (p==-1 ? "" : relayHosts.substring(p));
      name = neighborHost.resolveDVMname(n,names,hostname,relayHosts, relayList);
      if (name.startsWith("error ")) {
        System.err.println(name);
        System.exit(1);
      }

      // �l�[���T�[�o���N�����Ă��邩�`�F�b�N����
      String nshost = System.getProperty("dash.nameserver");
      if (nshost != null) {
        target = "�l�[���T�[�o";
        /*
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        */
        int r = neighborHost.registerEnv(getDVMname(), dvm.isRtype(), relayList, nshost);
        nameserverHost = nshost;
        if (r == -1) {
          System.err.println("�z�X�g"+nshost+"�����݂��Ȃ����A���邢�͂��̃z�X�g�Ńl�[���T�[�o���N�����Ă��܂���B");
          System.exit(1);
        }
      }

    } catch (java.net.UnknownHostException e) {
      System.err.println(target+neighborHostname+"���s���ł��B");
      System.exit(1);
    } catch (java.rmi.NotBoundException e) {
      System.err.println("�z�X�g"+neighborHostname+"�����݂��Ȃ����A���邢�͂��̃z�X�g��"+target+"���N�����Ă��܂���B");
      System.exit(1);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (RemoteException e) {
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
      //Naming.unbind("rmi://localhost/");
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
   * DVM�������肷��B
   * ���̃��\�b�h�͒��p�T�[�o���g��Ȃ��ꍇ�ɗp����B
   * �g���ꍇ�́ARelayServer.resolveDVMname()���Ăяo���B
   * @param n ���[�U���w�肵����
   * @param isR ���|�W�g���^DVM�̏ꍇ�Atrue�B
   */
  private String resolveDVMname(String n, boolean isR) {
    String names = System.getProperty("dash."+(isR ? "r" : "w") +".name");
    StringTokenizer st = new StringTokenizer(names, ",");

    String name = null;
    if (n != null) {
      name = n;
    } else {
      while (st.hasMoreTokens()) {
        name = st.nextToken();
        if (System.getProperty("DashMode").equals("on") ) {
          name = name + "_";
        }
        try {
          Naming.lookup("rmi://localhost/"+name);
          name = null;                   // ���łɎg���Ă���ꍇ
        } catch (NotBoundException e) {
          break;                         // �܂��g���Ă��Ȃ��ꍇ
        } catch (Exception e) {
          e.printStackTrace();
          name = "EXCEPTION";
        }
      }
    }

    if (name==null)
      name = "���O������܂���B�V�������O��defaults.txt��dash.dvmnames�ɒǉ����Ă��������B";
    //name = "r1";
    return name;
  }

  /**
   * �I��
   */
  public void finalizeModule() {
    try {
      switch (deliveryClass) {
      case RMI:
        Naming.unbind("rmi://localhost/"+name);
        if (nameserver != null)
          nameserver.unregisterEnv(getDVMname());
        break;
      case RELAY:
        if (nameserverHost != null) {
          int p = relayHosts.indexOf(":", 1);
          String relayList = (p==-1 ? "" : relayHosts.substring(p));
          neighborHost.unregisterEnv(getDVMname(), relayList, nameserverHost);
        }
        break;
      default:
        break;
      }
    } catch (Exception e) {e.printStackTrace(); }
  }

  public void printInfo() {
  	//System.out.println("dvmname = " + getDVMname()+" hostname = " + hostname);
    //System.out.println("hostname = " + hostname);
    //System.out.println("dvmname  = " + getDVMname());
  }

  /**
   * DVM�̖��O��Ԃ��B
   * RmiModule�̏ꍇ�ADASH-1.0�Ɠ����B
   */
  public String getDVMname() {
    if (relayHosts == null){
      String dvmname = name + ":" + hostname;
      return dvmname;
    }
    else
      return name + ":" + hostname + relayHosts;
  }

  /**
   * ����DVM����̃��b�Z�[�W���󂯎��B
   * sendMsg()���Ńe�X�g�̂��߂�null�𑗂�ꍇ������B
   */
  public void putMsg(DashMessage msg) throws java.rmi.RemoteException {
    if (msg != null)
      enqueueMsg(msg);
  }

  /**
   * ����DVM���邢�͑���DVM�Ƀ��b�Z�[�W�𑗐M����B
   * @return ����������true
   */
  public boolean sendMsg(DashMessage msg) {
  	
		//	�������烁�b�Z�[�W���O�ۑ�
		if(USELOG){
 		// ���b�Z�[�W���O�t�@�C���ւ̏�������
  	String msgLogText = createSendDatetime() + " ";

    // ���M���G�[�W�F���g��
  	msgLogText += msg.from + "@";
  	
    // ���b�Z�[�W�̑��M���̊����A�܂���null�B
    // null�̏ꍇ�́A���������ւ̃��b�Z�[�W�ł��邱�Ƃ�\���B
  	if (msg.departure == null ) {
	  	msgLogText += getDVMname() + " ";
  	}
  	else {
	  	msgLogText += msg.departure + " ";
  	}

		// ���M��G�[�W�F���g�� 
  	msgLogText += msg.to + "@";

    // ���b�Z�[�W�̓�����������A�܂���null�B
    //  null�̏ꍇ�́A���������ւ̃��b�Z�[�W�ł��邱�Ƃ�\���B
  	if (msg.arrival == null ) {
	  	msgLogText += getDVMname() + " ";
  	}
  	else {
	  	msgLogText += msg.arrival + " ";
  	}
  	
		// ���b�Z�[�W�{��
  	msgLogText += msg.toString();

    
  	writeMsgLog (msgLogText );
  	//�����܂Ń��b�Z�[�W���O�ۑ�
		}
		
    // (1)����DVM�ɑ��M����ꍇ
    if (msg.arrival == null || msg.arrival.equals(getDVMname())) {
      enqueueMsg(msg);
      return true;
    }

    // (2)����DVM�ɑ��M����ꍇ
    msg.departure = getDVMname();
    dvm.getNewIf().showMsg(msg);

    boolean result = false;
    //deliveryClass = 1;
    switch (deliveryClass) {
    case NO_RMI:
      result = sendMsgLocal(msg);
      break;
    case RMI:
      result = sendMsgRemote(msg);
      break;
    case RELAY:
      result = sendMsgRelay(msg);
      break;
    }
    return result;
  }

  /** NO_RMI�̎��̔z�� */
  private boolean sendMsgLocal(DashMessage msg) {
    RmiInterface module = (RmiInterface)localNaming.get(msg.arrival);
    if (module == null) {
      dvm.printlnE(errstr(msg, "RmiModule.sendMsg()",
                          "cannot delivery msg:"+msg.toString2()));
      return false;
    } else {
      try {
        module.putMsg(msg);
      } catch (RemoteException e) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsg()",
                            "cannot delivery msg:"+e));
      }
      return true;
    }
  }

  /**
   * RMI�̎��̔z���B
   * 2�ʂ�̏������s���B
   * - arrival��1����:���܂ޏꍇ(���p�T�[�o�𗘗p���Ă��Ȃ��ꍇ)
   *   (1)���̃z�X�g��putMsg(M)����B
   * - arrival�ɕ�����:���܂܂��ꍇ(���p�T�[�o�𗘗p���Ă���ꍇ)
   *   (2)���̃z�X�g��putMsg(M,"")����B

   */
  private boolean sendMsgRemote(DashMessage msg) {
    String arrival = msg.arrival;
    boolean useRelay = (arrival.indexOf(':') != arrival.lastIndexOf(':'));
    if (!useRelay)
      return sendMsgRemoteToRMI(msg);
    else
      return sendMsgRemoteToRelay(msg);
  }

  /** �͂��悪���p�T�[�o���g���Ă��Ȃ��ꍇ */
  private boolean sendMsgRemoteToRMI(DashMessage msg) {
    boolean exist = checkDVM(msg.arrival);

    //�L���b�V�����`�F�b�N
    RmiInterface module = (RmiInterface)moduleCache.get(msg.arrival);
    if (module != null) {
      try {
        module.putMsg(null); // ���݂��邩�e�X�g���Ă���
      } catch (Exception e) {
        module = null;
      }
    }
    //�V���Ɏ擾���L���b�V������
    if (module == null) {
      int p = msg.arrival.indexOf(':');
      if (p == -1) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRMI()",
                            "illegal arrival(no ':'):"+msg.arrival));
        return false;
      }
      String ename = msg.arrival.substring(0, p);
      String ehost = msg.arrival.substring(p+1);
      String url = "rmi://"+ehost+"/"+ename;
      try {
        module = (RmiInterface)Naming.lookup(url);
        moduleCache.put(msg.arrival, module);
      } catch (Exception e) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRMI()",
                            "illegal arrival(not exists):"+msg.arrival));
        return false;
      }
    }
    //���M����
    try {
      module.putMsg(msg);
    } catch (java.rmi.RemoteException e) {
      dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRelay()",
                          "cannot send msg:"+e));
      return false;
    }
    return true;
  }

  /** �͂��悪���p�T�[�o���g���Ă���ꍇ */
  private boolean sendMsgRemoteToRelay(DashMessage msg) {
    //�L���b�V�����`�F�b�N
    RelayInterface module = (RelayInterface)moduleCache.get(msg.arrival);
    if (module != null) {
      try {
        module.putMsg(null, ""); // ���݂��邩�e�X�g���Ă���
      } catch (Exception e) {
        module = null;
      }
    }
    //�V���Ɏ擾���L���b�V������
    if (module == null) {
      int p = msg.arrival.lastIndexOf(':');
      if (p == -1) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRelay()",
                            "illegal arrival(no ':'):"+msg.arrival));
        return false;
      }
      String ehost = msg.arrival.substring(p+1);
      String url = "rmi://"+ehost+"/relayserver";
      try {
        module = (RelayInterface)Naming.lookup(url);
        moduleCache.put(msg.arrival, module);
      } catch (Exception e) {
        dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRelay()",
                            "illegal arrival(not exists):"+msg.arrival));
        return false;
      }
    }
    //���M����
    try {
      module.putMsg(msg, "");
    } catch (java.rmi.RemoteException e) {
      dvm.printlnE(errstr(msg, "RmiModule.sendMsgRemoteToRelay()",
                          "cannot send msg:"+e));
      return false;
    }
    return true;
  }

  /**
   * RELAY�̎��̔z���BneighborHost�Ƀ��b�Z�[�W��n���B
   */
  private boolean sendMsgRelay(DashMessage msg) {
    try {
      int p = relayHosts.indexOf(":", 1);
      String relayList = (p==-1 ? "" : relayHosts.substring(p));
      neighborHost.putMsg(msg, relayList);
    } catch (RemoteException e) {
      dvm.printlnE(errstr(msg, "RmiModule.sendMsgRelay()",
                          "cannot relay msg:"+e));
      return false;
    }
    return true;
  }

  /** �������b�Z�[�W�L���[�Ƀ��b�Z�[�W��ǉ�����B*/
  private void enqueueMsg(DashMessage msg) {
    synchronized (queue) {
      queue.addElement(msg);
      queue.notify();
    }
  }

  /**
   * �������b�Z�[�W�L���[�̃��b�Z�[�W�����o���A�Ԃ��B
   * �L���[����̏ꍇ�A���b�Z�[�W������܂Ńu���b�N����B
   * �X���b�h�͌ďo���ō��B
   */
  public DashMessage waitMsg() {
    DashMessage result = null;
    switch (deliveryClass) {
    case NO_RMI:
    case RMI:
      result = waitMsgRMI();
      break;
    case RELAY:
      result = waitMsgRelay();
      break;
    }
    return result;
  }

  /** NO_RMI, RMI�p��waitMsg() */
  private DashMessage waitMsgRMI() {
    synchronized(queue) {
      try {
        if (queue.isEmpty())
          queue.wait();
      } catch(Exception e) { e.printStackTrace(); }
    }
    return (DashMessage)queue.remove(0);
  }

  /** RELAY�p��waitMsg()�BneighborHost�ɐu���ɍs���B */
  private DashMessage waitMsgRelay() {
    // �͂��܂ő҂B
    DashMessage msg = null;
    while (msg==null) {
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        //msg = module.waitMsg(getDVMname(), relayList);
        msg = neighborHost.waitMsg(getDVMname(), relayList);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.waitMsgRelay(): cannot wait:"+e);
      }
    }
    System.out.println(msg);
    return msg;
  }

  /**
   * �G�[�W�F���g�����l�[���T�[�o�ɓo�^����
   * �ʐM���Ȃ��ꍇ(NO_RMI�̏ꍇ)�͎g���Ȃ��B
   * @param name �G�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   *             ���|�W�g���G�[�W�F���g�̏ꍇ��Sample01�̂݁B
   * @param envname ���݋������(w1:lynx:leo)
   * @param origin ���|�W�g��(�t�@�C����ǂݍ���Ő��������ꍇ�A���[�N�v���[�X)
   * @param function �@�\��
   * @param comment �R�����g
   */
  public int registerAgent(String name, String envname, String origin, String function, String comment) {
    if (nameserverHost == null)
      return 0;

    int result = 0;
    switch (deliveryClass) {
    case NO_RMI:
      result = 0;
      break;
    case RMI:
      try {
        result = nameserver.register(name, envname, origin, function, comment);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.registerAgent(): cannot register: "+e);
      }
      break;
    case RELAY:
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        result =neighborHost.register(name, envname, origin, function, comment,
                                      relayList, nameserverHost);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.registerAgent(): cannot register: "+e);
      }
      break;
    }
    return result;
  }

  /**
   * �G�[�W�F���g�����l�[���T�[�o����폜����
   * �ʐM���Ȃ��ꍇ(NO_RMI�̏ꍇ)�͎g���Ȃ��B
   * @param name �G�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   *             ���|�W�g���G�[�W�F���g�̏ꍇ��Sample01�̂݁B
   * @param envname ���݋������(w1:lynx:leo)
   */
  public int unregisterAgent(String name, String envname) {
    if (nameserverHost == null)
      return 0;

    int result = 0;
    switch (deliveryClass) {
    case NO_RMI:
      result = 0;
      break;
    case RMI:
      try {
        result = nameserver.unregister(name, envname);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.unregisterAgent(): cannot unregister(1): "+e);
      }
      break;
    case RELAY:
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        result =neighborHost.unregister(name, envname,
                                        relayList, nameserverHost);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.unregisterAgent(): cannot register(2): "+e);
      }
      break;
    }
    return result;
  }

  /**
   * �G�[�W�F���g���ړ��������Ƃ��l�[���T�[�o�ɒʒm����B
   * �ʐM���Ȃ��ꍇ(NO_RMI�̏ꍇ)�͎g���Ȃ��B
   * @param name �G�[�W�F���g��(Sample01.200208031614312:w1:lynx:leo)
   *             ���|�W�g���G�[�W�F���g�̏ꍇ��Sample01�̂݁B
   * @param oldEnvname ���܂ŋ�������(w1:lynx:leo)
   * @param newEnvname �V��������(w1:taurus:leo)
   */
  public int moveAgent(String name, String oldEnvname, String newEnvname) {
    if (nameserverHost == null)
      return 0;

    int result = 0;
    switch (deliveryClass) {
    case NO_RMI:
      result = 0;
      break;
    case RMI:
      try {
        result = nameserver.move(name, oldEnvname, newEnvname);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.moveAgent(): cannot notify(1): "+e);
      }
      break;
    case RELAY:
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        result =neighborHost.move(name, oldEnvname, newEnvname,
                                  relayList, nameserverHost);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.moveAgent(): cannot notify(2): "+e);
      }
      break;
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
   * �l�[���T�[�o���g��Ȃ��ꍇ��A
   * �w�肵���l�[���T�[�o�����݂��Ȃ����A���̃z�X�g�Ńl�[���T�[�o��
   * �N�����Ă��Ȃ��ꍇ�Anull�B
   * �z��̓Y���̈Ӗ��ɂ��ẮANSInterface�Q�ƁB
   */
  public Vector lookup(String[][] selector) {
    if (nameserverHost == null)
      return null;

    Vector result = null;
    switch (deliveryClass) {
    case NO_RMI:
      break;
    case RMI:
      try {
        result = nameserver.lookup(selector);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.lookup(): cannot lookup: "+e);
      }
      break;
    case RELAY:
      try {
        int p = relayHosts.indexOf(":", 1);
        String relayList = (p==-1 ? "" : relayHosts.substring(p));
        result =neighborHost.lookup(selector,
                                    relayList, nameserverHost);
      } catch (RemoteException e) {
        dvm.printlnE("RmiModule.lookup(): cannot lookup: "+e);
      }
      break;
    }
    return result;
  }

  /**
   * dvmname�Ŏw�肳�ꂽDVM�����݂��邩�𒲂ׂ�B
   * ���݂���ꍇ�A���W���[���L���b�V���Ɋi�[����B
   * @return ���݂���ꍇ�Atrue�B
   */
  public boolean checkDVM(String dvmname) {
    switch (deliveryClass) {
    case NO_RMI:
      return false;
    case RMI:
      boolean useRelay = (dvmname.indexOf(':') != dvmname.lastIndexOf(':'));
      if (!useRelay)
        return checkDVM_RMI_RMI(dvmname);
      else
        return checkDVM_RMI_Relay(dvmname);
    case RELAY:
      return checkDVM_Relay(dvmname);
    default:
      return false;
    }
  }

  /** ������RMI�ŁA�����RMI�̏ꍇ */
  private boolean checkDVM_RMI_RMI(String dvmname) {
    // �L���b�V�����`�F�b�N
    RmiInterface module = (RmiInterface)moduleCache.get(dvmname);
    if (module != null) {
      try {
        module.putMsg(null); // ���݂��邩�e�X�g���Ă���
      } catch (Exception e) {
        module = null;
      }
    }
    // �V���Ɏ擾���L���b�V������
    if (module == null) {
      int p = dvmname.indexOf(':');
      if (p == -1)
        return false;

      String ename = dvmname.substring(0, p);
      String ehost = dvmname.substring(p+1);
      String url = "rmi://"+ehost+"/"+ename;
      try {
        module = (RmiInterface)Naming.lookup(url);
      } catch (Exception e) {
        return false;
      }
      moduleCache.put(dvmname, module);
    }
    return true;
  }

  /** ������RMI�ŁA�����Relay�̏ꍇ */
  private boolean checkDVM_RMI_Relay(String dvmname) {
    // �L���b�V�����`�F�b�N
    RelayInterface module = (RelayInterface)moduleCache.get(dvmname);
    if (module != null) {
      try {
        if (!module.knowsDVM(dvmname)) // ���݂��邩�e�X�g���Ă���
          module = null;
      } catch (Exception e) {
        module = null;
      }
    }
    // (�Ŋ��̒��p�T�[�o��)�V���Ɏ擾���L���b�V������
    if (module == null) {
      int p = dvmname.lastIndexOf(':');
      if (p == -1)
        return false;

      String ehost = dvmname.substring(p+1);
      String url = "rmi://"+ehost+"/relayserver";
      try {
        module = (RelayInterface)Naming.lookup(url);
      } catch (Exception e) {
        return false;
      }
      moduleCache.put(dvmname, module);
    }
    return true;
  }

  /** ������Relay�̏ꍇ(�����RMI��������Relay) */
  private boolean checkDVM_Relay(String dvmname) {
    boolean result = false;
    try {
      int p = relayHosts.indexOf(":", 1);
      String relayList = (p==-1 ? "" : relayHosts.substring(p));
      result = neighborHost.checkDVM(dvmname, relayList);
    } catch (RemoteException e) {
      result = false;
    }
    return result;
  }

  private String errstr(DashMessage msg, String method, String s) {
    return msg.from+": "+method+": "+s;
  }

  public NSInterface getNameServer() {
    return nameserver;
  }
  public String getNameServerErrMsg() {
    return NameServerErrMsg;
  }


	private synchronized void writeMsgLog (String msglogText ) {

		String logFileName = "";

		// �V�X�e���v���p�e�B����p�X�t�����O�t�@�C�������擾����
		logFileName = (String)System.getProperty("msgfilename");
		// ���b�Z�[�W�ԍ����擾����
		String msgnoStr = (String)System.getProperty("msgno");
		String lastmsgnoStr;
		if (msglogText.indexOf(":to _broadcast") != -1) {
				lastmsgnoStr=String.valueOf(Integer.parseInt(msgnoStr)+dvm.agTable.size()-1);
				 msglogText= msgnoStr+"-"+lastmsgnoStr+" "+msglogText ;
				 msgnoStr=lastmsgnoStr;
		}else{
		
		// ������������ރ��b�Z�[�W�̐擪�ɒǉ�
		msglogText = msgnoStr + " " + msglogText;
		}
		// ���b�Z�[�W�ԍ����P�������āA�V�X�e���v���p�e�B�ɍď�������
		System.setProperty("msgno", new Integer(new Integer(msgnoStr).intValue() + 1).toString());
				
    

		// �ǉ��������݂̂��߁A���݂̃��O�t�@�C���̓��e���x�N�^�[�Ɏ擾���A
		// �x�N�^�[�̍Ō�̗v�f�ɒǉ����郍�O���e��ǉ�����B
		try {
    	Vector vecOrgData = new Vector();
	    String sLine = "";
	    BufferedReader b_in = new BufferedReader(new InputStreamReader(
	              new FileInputStream(logFileName),
	              "JISAutoDetect"));
	    while ((sLine = b_in.readLine()) != null){
		    vecOrgData.addElement(sLine);
	    }
	    b_in.close();

			// ��L�ō쐬�����x�N�^�[�̓��e���ēx���O�t�@�C���ɏ�������
			vecOrgData.addElement(msglogText);
	    File fp  = new File ( logFileName );
	    FileOutputStream fos = new FileOutputStream (fp);
	    PrintWriter pw  = new PrintWriter (fos);
	    for (int i=0; i<vecOrgData.size(); i++ ) {
				pw.println((String)vecOrgData.elementAt(i));
	    }
	    pw.close ();
	    fos.close();
		}
		catch (IOException e ) {}
		

	}
  
  private String createSendDatetime () {
    Calendar rightNow = Calendar.getInstance();
    int year = rightNow.get(Calendar.YEAR) ;
    int month = rightNow.get(Calendar.MONTH)+1;
    int date = rightNow.get(Calendar.DATE);

    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
    int minute = rightNow.get(Calendar.MINUTE);
    int second = rightNow.get(Calendar.SECOND);
    //20050705 uchiya update
    int millisecond = rightNow.get(Calendar.MILLISECOND);
    
    String yearStr = new Integer(year).toString();
    String monthStr = new Integer(month).toString();
    if (monthStr.length() == 1 ) {
      monthStr = "0" + monthStr;
    }
    String dateStr = new Integer(date).toString();
    if (dateStr.length() == 1 ) {
      dateStr = "0" + dateStr;
    }

    String hourStr = new Integer(hour).toString();
    if (hourStr.length() == 1 ) {
      hourStr = "0" + hourStr;
    }
    String minuteStr = new Integer(minute).toString();
    if (minuteStr.length() == 1 ) {
      minuteStr = "0" + minuteStr;
    }
    String secondStr = new Integer(second).toString();
    if (secondStr.length() == 1 ) {
      secondStr = "0" + secondStr;
    }

	String millisecondStr = new Integer(millisecond).toString();
		//System.out.println("millisecond="+millisecond);
	   if (millisecondStr.length() == 1 ) {
		 millisecondStr = "00" + millisecondStr;
	   }else if(millisecondStr.length() == 2) {
	   millisecondStr = "0" + millisecondStr;
	   } 
	    String SendDatetimeStr = yearStr + "/" + monthStr + "/" + dateStr + " " +
                       hourStr + ":" + minuteStr + ":" + secondStr  + ":" +millisecondStr;
    return SendDatetimeStr;    
  }

}
