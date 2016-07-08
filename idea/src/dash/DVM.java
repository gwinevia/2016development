package dash;

import java.io.*;
import java.util.*;
import ps.OAVdata;
import ps.TafList;
import ps.Value;
import javax.swing.*;

/**
 * DASH Virtual Machine�̎����B���[�N�v���[�X�Ƃ��ĂԁB
 */
public abstract class DVM implements Runnable {

  /** RMI���g�킸�ɍs���ꍇtrue */
  private static boolean localOnly = false;

  private String tempclassname = "";

  private String name = "";

  /** �ʐM��Ճ��W���[�� */
  public ComInterface comInt;

  /** ���b�Z�[�W�҂��̃X���b�h */
  private Thread msgWaitThread;

  /** RMI���W���[�����g���Ă���ꍇ��True */
  boolean usingRmiModule = false;

  /** ���j�^ */
  // COSMOS public�ɕύX
  private NewifItface newif;

  /** �r���[�A **/
  /** ADD COSMOS **/
  Viewer viewer = null;

  /** �X���b�h�O���[�v */
  ThreadGroup dashThreads;

  /** �G�[�W�F���g�̃e�[�u���Bdash.DashAgent�̃C���X�^���X������B*/
  Hashtable agTable;

  /** �G�[�W�F���g�𐶐����邽�߂̃X���b�h�B*/
  private Creater creater;
  private Thread createrThread;

  /**
   * ���ɐ������郁�b�Z�[�W��ID�Breply-with�ɗp����B�ŏ��̃��b�Z�[�WID��1�B
   */
  private long messageID;

  /** �x�[�X�v���Z�X/���\�b�h�̂���f�B���N�g�� */
  private String[] userClassPath;

  /** _putBytecode�p */
  private Hashtable replyBox;

  /**
   * �ړ��G�[�W�F���g�̃e�[�u���B
   * key��_createdInstance��replyWith, value�̓G�[�W�F���g���B
   * _activateMA���󂯎��ƁA�G���g�����폜�����B
   */
  private Hashtable noActivateMA;

  /** �A���[���̊Ǘ�����������N���X */
  private AlarmManager alarmManager;

  // ADD COSMOS
  // Viewer�̍쐬
  public void showViewer ( ) {
    if (viewer == null ) {
      viewer = new Viewer (comInt.getDVMname(), this );
      
      //viewer.show();
      //changed uchiya
      viewer.setVisible(true);
      viewer.initialize();
    }else {
      //viewer.show();
      viewer.setVisible(true);
    }
  }

  // ADD COSMOS
  public void closeViewer ( ) {
    viewer.close();
  }

  public NewifItface getNewIf () {
    return newif;
  }
  private boolean isDevEnv = false;
  /**
   * �R���X�g���N�^
   * @param name ���O�̌��B�ŏI�I�Ȗ��O��ComInt�����肷��B
   */
  //public DVM(String name, File msgfile, boolean useNewif, File dashdir) {
  // UPDATE COSMOS

  public void RmiModule_ReCreate() {
    comInt = RmiModule.createNew(this, name, localOnly);

    msgWaitThread = new Thread(dashThreads, this, "dash.DVM");
    msgWaitThread.start();

    creater = new Creater(dashThreads, this);
    creater.start();
  }
  public DVM(String name, File msgfile, boolean useNewif, boolean useViewer, File dashdir) {
    // RMI���W���[�����g���B
    comInt = RmiModule.createNew(this, name, localOnly);
    usingRmiModule = true;
    this.name = name;

    boolean noGUI =
      System.getProperty("dash.noGUI", "off").equalsIgnoreCase("on");

    isDevEnv = System.getProperty("dash.isDevEnv", "no").equalsIgnoreCase("yes");

    String DashMode = System.getProperty("DashMode");
    if (noGUI)
      newif = new NewifDummy();
    else {
      if (!DashMode.equals("on") ) {
        newif = new NewIf2(comInt.getDVMname(), this, msgfile, dashdir);
      }
      else {
        newif = new Newif(comInt.getDVMname(), this, msgfile, dashdir);
      }
    }

    dashThreads = new ThreadGroup("DASH");
    agTable = new Hashtable();
    messageID = 1;
    noActivateMA = new Hashtable();
    alarmManager = new AlarmManager(this);

    setupClassLoader();
    replyBox = new Hashtable();

    if (useNewif && !noGUI) {
      if (DashMode.equals("on") ) {
        newif.show();
      }
      newif.startMemoryWatch(); // ������Newif.replaceConsole()����
    } else
      newif.setNonstop();

    // ADD COSMOS
    if (useViewer && !noGUI) {
      viewer = new Viewer (comInt.getDVMname(), this );
      //viewer.show();
      viewer.setVisible(true);
      viewer.initialize();
    }

    comInt.printInfo();
  }

  /**
   * �N��
   */
  void startVM() {

    msgWaitThread = new Thread(dashThreads, this, "dash.DVM");
    msgWaitThread.start();

    creater = new Creater(dashThreads, this);
    creater.start();
  }

  /**
   * ���b�Z�[�W�҂��̃X���b�h�B
   * DVM�̓��b�Z�[�W���͂��̂�҂B
   * �͂�����A�G�[�W�F���g�ɓn���B
   */
  public void run() {
    while(true) {
      try {
        DashMessage msg = comInt.waitMsg();
        if (wpIndex != -1 ) {
          if (wpTab != null ) {
            wpTab.setSelectedIndex(wpIndex);
          }
        }
        
        // (1)�C���^�t�F�[�X��
        if (msg.to.equals(DashMessage.IF)) {
          newif.replaceConsole();
          newif.showMsg(msg);
          newif.putMsg(msg);
          if (viewer != null ) {
             viewer.showMsg(msg);	// ADD COSMOS 2002.09.13
          }

          newif.ViewerShowMsg(msg);
          continue;
        }

        // (2)DVM��
        if (msg.to.equals(DashMessage.DVM)) {
          newif.replaceConsole();
          processDVMmsg(msg);
          continue;
        }

        // (2)�u���[�h�L���X�g
        if (msg.to.equals(DashMessage.BCAST)) {
          newif.replaceConsole();
          newif.showMsg(msg);
          if (viewer != null ) {
            viewer.showMsg(msg);	// ADD COSMOS 2002.09.13
          }
          newif.ViewerShowMsg(msg);
          for (Enumeration e = agTable.keys(); e.hasMoreElements(); ) {
            Object receiverName = e.nextElement();
            if (!receiverName.equals(msg.from)) {
              DashAgent receiver = (DashAgent)agTable.get(receiverName);
              receiver.putMsg(msg);
            }
          }
          continue;
        }

        DashAgent receiver = (DashAgent)agTable.get(msg.to);

        // (3)���݂���G�[�W�F���g
        if (receiver != null) {
          newif.replaceConsole();
          if (msg.performative.equals(DashMessage.KILLFORCE) &&
              msg.from.equals(DashMessage.IF) &&
              msg.content == null)
            ; //(@see DashAgent.run())
          else {
            newif.showMsg(msg);
            if (viewer != null ) {
              viewer.showMsg(msg);		// ADD COSMOS
            }
            newif.ViewerShowMsg(msg);
          }
          receiver.putMsg(msg);
          continue;
        }

        // (4)���݂��Ȃ��G�[�W�F���g
        if (!msg.isNoSuchAgentOrDvm()) {
          newif.replaceConsole();
          sendMessageFromUser(msg, "Error",
                              "NO_SUCH_AGENT", null, null,msg.toString(),null);
          continue;
        }
      } catch (Exception e) {
        System.err.println(e);
        e.printStackTrace();
      }
    }
  }

  /* �I������ */
  void finalizeDVM(){
    // RMI���W�X�g������G���g������菜���B
    // �l�[���T�[�o���g���Ă���ꍇ�́A�폜����B
    comInt.finalizeModule();

    // �I��
    //System.exit( 0 );
  }

  /**
   * DVM���̃��b�Z�[�W����������B
   */
  private void processDVMmsg(DashMessage msg) {
    if (msg.performative.equals(DashMessage.CREATEINSTANCE)||
        msg.performative.equals(DashMessage.MOVE)) {
      // (1)���[�N�v���[�X����(instantiate)(move)
      String cname   = null;
      String oldname = null;
      if (msg.performative.equals(DashMessage.MOVE)) { // (move)
        cname = msg.getOtherAttributes(":cname");
        oldname = msg.from;
      } else {
        cname = msg.from; // (instantiate :into)
        oldname = null;
      }
      String description = msg.getOtherAttributes(":description");
      ps.AgentProgram program =
        (ps.AgentProgram)msg.getOtherAttributesB(":program");
      ps.WorkMem workmem =
        (ps.WorkMem)msg.getOtherAttributesB(":workmem");
      String filename = msg.getOtherAttributes(":filename");
      String facts = msg.content; // 0�ȏ��OAV�f�[�^��v�f�Ɏ����X�g
      String origin = msg.getOtherAttributes(":origin");
      String newname =
        createAgent(cname, description, filename, facts, origin, oldname, program, workmem);
      DashMessage newmsg =
        sendMessageFromUser(msg, DashMessage.CREATEDINSTANCE,
                            "SUCCESS", null, null, "("+newname+")", null);

      if (msg.performative.equals(DashMessage.MOVE)) {
        Long key = new Long(newmsg.replyWith);
        noActivateMA.put(key, oldname);
      }

    } else if (msg.performative.equals(DashMessage.INSTANTIATE)) {
      // (2)���|�W�g������(instantiate)
      newif.showMsg(msg);
      String cname = msg.getOtherAttributes(":target");
      DashAgent agent = (DashAgent)agTable.get(cname);
      if (agent == null)
        sendMessageFromUser(msg, "Error", "NO_SUCH_AGENT", null, null, msg.toString(), null);
      else {
        Hashtable other = new Hashtable();
        other.put(":description", agent.getScript());
        other.put(":filename", agent.getFilename());
        other.put(":origin", agent.getOrigin());
        other.put(":target", cname);
        sendMessageFromUser(msg, DashMessage.CREATEINSTANCE,
                            "SUCCESS", null, null, null, other);
      }

    } else if (msg.performative.equals(DashMessage.GETBYTECODE)) {
      // (3)�o�C�g�R�[�h�v����M
      loadLocalClassData(msg);

    } else if (msg.performative.equals(DashMessage.PUTBYTECODE)) {
      // (4)�o�C�g�R�[�h��M
      putReply(msg);

    } else if (msg.performative.equals(DashMessage.ACTIVATEMOBILE)) {
      // (5)�ړ���̊�����(��������)
      Long key = new Long(msg.inReplyTo);
      String agname = (String)noActivateMA.remove(key);
      DashAgent agent = (DashAgent)agTable.get(agname);
      agent.startAgent(dashThreads);

    } else if (msg.performative.equals(DashMessage.GETRULESET)) {
      // (6)���[���Z�b�g�v����M
      getLocalRuleset(msg);

    } else if (msg.performative.equals(DashMessage.PUTRULESET)) {
      // (7)���[���Z�b�g��M
      putReply(msg);

    } else
      System.err.println("DVM.processDVMmsg(): cannot process:\n"+msg);
  }

  /**
   * _getBytecode����M�����Ƃ��̏����B
   * �o�C�g�R�[�h��ǂݍ��݁A_putBytecode�ɂ��ĕԂ��B
   * @see getLocalRuleset()
   */
  private void loadLocalClassData(DashMessage msg) {
    byte[] classBytes = null;
    String classname = msg.getOtherAttributes(":classname");
    try {
      classBytes = loadLocalClassData(classname);
    } catch (Exception e) {
      e.printStackTrace();
      classBytes = null;
    }

    if (classBytes == null) {
      sendMessageFromUser(msg, DashMessage.PUTBYTECODE,
                          "FAILED", null, null, null, null);
    } else {
      Hashtable other = new Hashtable();
      other.put(":bytecode", classBytes);
      sendMessageFromUser(msg, DashMessage.PUTBYTECODE,
                          "SUCCESS", null, null, null, other);
    }
  }

  /**
   * _getRuleset����M�����Ƃ��̏����B
   * ���[���Z�b�g�̃t�@�C����ǂݍ��݁A_putRuleset�ɂ��ĕԂ��B
   * @see loadLocalClassData()
   */
  private void getLocalRuleset(DashMessage msg) {
    String filename = msg.getOtherAttributes(":filename");
    String LoadDir = System.getProperty("dash.loadpath");
    File f  = null;
    Hashtable htFileList = null;
    if (LoadDir != null ) {
      htFileList = new Hashtable();
      StringTokenizer st = new StringTokenizer(LoadDir,";");
        while (st.hasMoreTokens()) {
          String data = st.nextToken();
          f  = new File (data);
          createFileList(f,htFileList, LoadDir);
          break;
        }

      }
      
    String text = getLocalRuleset(filename, htFileList);

    if (text == null) {
      sendMessageFromUser(msg, DashMessage.PUTRULESET,
                          "FAILED", null, null, null, null);
    } else {
      Hashtable other = new Hashtable();
      other.put(":ruleset", text);
      sendMessageFromUser(msg, DashMessage.PUTRULESET,
                          "SUCCESS", null, null, null, other);
    }
  }


  /**
   * Creater()�ɃG�[�W�F���g�𐶐�����悤�w�����o���B
   * loadAgent()�œǂݍ��ށB
   */
  void addLoadQueue(File file) {
    newif.replaceConsole();
    creater.create(file);
  }

  /**
   * �G�[�W�F���g�t�@�C����ǂݍ��ށB
   * Creater.run()���Ăяo���B
   */
  void loadAgent(File file) {
    newif.replaceConsole();
    // �t�@�C�����`�F�b�N
    String cname = file.getName();
    int idx = cname.lastIndexOf('.');
    if (idx == -1)
      return;
    String ext = cname.substring(idx).toLowerCase();
    if (!ext.equals(".dash"))
      return;
    cname = cname.substring(0, idx);

    // �t�@�C����ǂݍ���
    StringBuffer buf = new StringBuffer();
    BufferedReader br = null;
    try {
      //BufferedReader br = new BufferedReader(new FileReader(file));
      br = new BufferedReader(new InputStreamReader(
                                              new FileInputStream(file.getAbsolutePath()),
                                              "JISAutoDetect"));
      while (br.ready()) {
        String line = br.readLine();
        if (line != null) {
          // ����if�߂�StreamTokenizer�̃o�O�΍�B
          // /* */���ɋ�s������Ƃ���܂���lineno��Ԃ��̂ŁA�󔒂�ǉ�����B
          if (line.length() == 0)
            line = " ";
          buf.append(line);
          buf.append("\n");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
        try {
    	    if (br != null) {
    		 br.close();
    		 br=null;
       }
      } catch (IOException e) {
       e.printStackTrace();
      }
    }
    createAgent(cname, buf.toString(), file.getName(), null, getDVMname(), null, null, null);
  }

  /**
   * �G�[�W�F���g�𐶐�����BkillAgent()�̋t���s���B
   * @param cname ���|�W�g���G�[�W�F���g��(�N���X��)
   * @param description �m���L�q
   * @param filename �f�B���N�g�����܂܂Ȃ��t�@�C����(Agentname.dash)
   *                 ���[�N�v���[�XAg�ł������B
   * @param facts 0�ȏ��OAV�f�[�^��v�f�Ɏ����X�g�B
   *              �C���X�^���V�G�[�g���Ɏg���B
   * @param origin �ŏ��ɃG�[�W�F���g�𐶐�����DVM��
   * @param oldname move�A�N�V�����Ő��������ꍇ�A�ړ��O�̖��O(�ړ�����s��)
   *                �Ⴄ�ꍇnull�B
   * @param program move�A�N�V�����Ő��������ꍇ�A�v���O�����B�Ⴄ�ꍇnull�B
   * @param workmem move�A�N�V�����Ő��������ꍇ�AWorkMem�B   �Ⴄ�ꍇnull�B
   * @return �G�[�W�F���g��
   */
  String createAgent(String cname, String description, String filename, String facts, String origin, String oldname, ps.AgentProgram program, ps.WorkMem workmem) {
    newif.replaceConsole();
    String name = null;

    // �G�[�W�F���g�̐����E�o�^�E�N��
    // (���O����͖{����ComInt�����ׂ��H)
    try {
      if (isRtype()) {
        if (agTable.get(cname)!=null) {
          printlnE("Error: Agent \""+cname+"\" has already created.");
          return null;
        }
        name = cname;
      } else {
        if (oldname != null)
          name = oldname;
        else
          name = newName(cname);
      }


      // �J�����̃G�[�W�F���g�c���[�ŁA�G�[�W�F���g�������\�����邽�߂�
      // �Ƃ肠�����E�E�Eby COSMOS 2003.02.13
      //name = cname;
      if (agTable.get(name) != null ) {
        return null;
      }
      // �����܂ŁB2003.02.13

      //DashAgent agent = new DashAgent(cname,name,description, filename, this, facts, origin, (oldname!=null));
      DashAgent agent = new DashAgent(cname,name,description, filename, this, facts, origin, program, workmem);
      if (!agent.result ) {
        return null;
      }

      agTable.put(name, agent);

      newif.addAgent(name, origin);
      newif.confirmSync();
      
      // ADD COSMOS �r���[�A�ɃG�[�W�F���g��`��
      if (viewer != null ) {	
      	 viewer.showNewAgent (origin,name);
      }
      // �ړ��G�[�W�F���g�̏ꍇ��_activateMobile������܂ő҂B
      // (DVM.processDVMmsg()����startAgent()����)
      if (oldname == null) {
        String[] fc=agent.getFunctionAndComment(); // [0]�͋@�\��,[1]�̓R�����g
        //System.out.println(agent.getScript());
        comInt.registerAgent(name, getDVMname(), origin, fc[0], fc[1]);
        agent.result = true;
        agent.startAgent(dashThreads);
        if (!agent.result ) {
          return null;
        }
      }
    } catch (ps.SyntaxException se) {
      System.out.println("Read Error: "+se);
    }
    return name;
  }

  /**
   * �G�[�W�F���g�����ł�����BcreateAgent()�̋t���s���B
   * DashAgent.run()�̖����ŌĂяo���B
   * @param name �G�[�W�F���g��
   * @param yuigon �ړ������ꍇ�A_createdInstance
   * @see stopAgent()
   */
  void killAgent(String name, DashMessage yuigon) {
	//del�A�C�R�����������Ƃ��͂��̃��\�b�h�͌Ă΂�Ȃ�
  	System.out.println("dvm.killAgent");
  	
    newif.replaceConsole();
    newif.removeAgent(name);
    newif.confirmSync();

    // ADD COSMOS �r���[�A����G�[�W�F���g���폜 (2002.09.10)
    //if (viewer != null ) {
    //  viewer.removeAgent(name);
    //}

    DashAgent agent = (DashAgent)agTable.remove(name);
    if (yuigon != null) {
      sendMessageFromUser(yuigon, DashMessage.ACTIVATEMOBILE,
                          "SUCCESS", null, null, "()", null);
      comInt.moveAgent(name, getDVMname(), yuigon.departure);
    } else {
      comInt.unregisterAgent(name, getDVMname());
    }
  }


  /**
   * �G�[�W�F���g�����ł�����Bcontent��null��_killForce�𑗂�B
   * Newif.killAgent()�ŌĂяo���B
   * @param name �G�[�W�F���g��
   * @see killAgent()
   * @see DashAgent.run()
   */
  void stopAgent(String name) {
    newif.replaceConsole();
    sendMessageFromUser(null, DashMessage.KILLFORCE,
                        null, name, null, null, null);
  }

  /**
   * ���[�N�v���[�X�ɐ������ꂽ�G�[�W�F���g�̖��O�����߂�B
   * FileName.dash�Ȃ�fileName1�ɂ���B
   * �������A���݂�DVM�ɓ����̃G�[�W�F���g������ꍇ�A
   * ���O�̖����̐�����1���₷�B
   * @param cname FileName�̕���
   */
  /* cout
  private String newName(String cname) {
    String first = cname.substring(0, 1);
    String candidate = first.toLowerCase() + cname.substring(1);
    String suffix = "."+getDVMname();

    int i=1;
    while (agTable.get(candidate+i+suffix) != null)
      i++;

    return candidate+i+suffix;
  }
  */

  /**
   * ���[�N�v���[�X�ɐ������ꂽ�G�[�W�F���g�̖��O�����߂�B
   * ������2002�N01��23��04��56��01�b3�ɁAFileName.dash���琶�������Ȃ�,
   * FileName.200201230456013:lynx�ɂȂ�B
   * @param cname FileName�̕���
   */
  private String newName(String cname) {
    String newname = null;
    do {
      String time = currentTime();
      newname = cname + "." + time + ":" + getDVMname();
    } while (agTable.get(newname) != null);

    return newname;
  }

  /**
   * ���݂̎������AYYYYMMDDhhmmssx�`���ŕԂ��B
   */
  private String currentTime() {
    Calendar rightNow = Calendar.getInstance();
    StringBuffer buf = new StringBuffer();

    int i = rightNow.get(Calendar.YEAR);     // YYYY�N
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.MONTH)+1;      // MM��
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.DATE);         // DD��
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.HOUR_OF_DAY);  // hh��
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.MINUTE);       // mm��
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.SECOND);       // ss�b
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.MILLISECOND)/100;  // x �~���b(
    buf.append(Integer.toString(i));

    return buf.toString();
  }

  /**
   * �G�[�W�F���g���l�[���T�[�o�ɓo�^����B
   * �������A
   */
  /*
  private void registerAgent(String cname, String name, DashAgent agent) {

  }
  */

  /**
   * Newif�Ȃǂ̃C���^�t�F�[�X���邢��DVM����A
   * ���b�Z�[�W�������(Creater��)���M�L���[�ɓ����B
   * @param msg     null(�܂��́A�ԐM�Ώۃ��b�Z�[�W)
   * @param perf    �p�t�H�[�}�e�B�u
   * @param diag    DVM����̏ꍇ�A�G���[�̐f�f�BIF����̏ꍇ�Anull�B
   * @param to      ���M��G�[�W�F���g��
   * @param arrival ���M�����(�܂���null)
   * @param content :content (..)�́A(..)
   * @param other   ���̑�����
   */
  public DashMessage sendMessageFromUser(DashMessage msg, String perf, String diag, String to, String arrival, String content, Hashtable other) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    DashMessage newmsg;

    newif.replaceConsole();
    if (msg == null)
      newmsg = new DashMessage(perf, DashMessage.IF, to, arrival, messageID);
    else
      newmsg = new DashMessage(perf, msg, messageID);

    if (diag != null) {
      newmsg.from = DashMessage.DVM;
      newmsg.setOtherAttributes(":system", diag);
    }

    if (other != null)
      for (Enumeration e = other.keys(); e.hasMoreElements(); ) {
        String key = (String)e.nextElement();
        Object val = other.get(key);
        newmsg.setOtherAttributes(key, val);
      }

    newmsg.setContent(content);
    messageID++;

    creater.send(newmsg); // ���M�L���[�ɓ����B
    //System.out.println("newMsg= "+newmsg);
    return newmsg;
  }

  /**
   * Creater.run()����Ă΂��B
   */
  void sendMessageFromUser(DashMessage msg) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    boolean success = comInt.sendMsg(msg);

    if (!success) {
      if (!msg.isNoSuchAgentOrDvm())
        sendMessageFromUser(msg, "Error", "NO_SUCH_DVM", null, null, msg.toString(), null);
    }
  }

  /**
   * �G�[�W�F���g����A���b�Z�[�W������đ��M�L���[�ɓ����B
   * @param oav send�A�N�V�����B(send :performative ...)�܂���
   *            reply�A�N�V�����B
   * @param name �G�[�W�F���g��
   * @return �쐬�������b�Z�[�W�̃��b�Z�[�WID�B
   * ���s�����ꍇ���쐬�͂���̂Ń��b�Z�[�WID�B
   */
  String sendMessageFromAgent(OAVdata oav, String name) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    String perf    = oav.getValueString(":performative");
    String content = oav.getValueString(":content");

    DashMessage newmsg = null;
    if (oav.getObject().equals("send")) {
      String to      = oav.getValueString(":to");
      String arrival = oav.getValueString(":arrival");
      newmsg = new DashMessage(perf, name, to, arrival, messageID);

    } else if (oav.getObject().equals("broadcast")) {
      String to      = DashMessage.BCAST;
      String arrival = oav.getValueString(":arrival");
      newmsg = new DashMessage(perf, name, to, arrival, messageID);

    } else {
      OAVdata msg = (OAVdata)oav.getValue(":to");
      newmsg = new DashMessage(perf, name, msg, messageID);
    }

    newmsg.setContent(content);
    newmsg.setOtherAttributes(oav);
    messageID++;

    boolean success = comInt.sendMsg(newmsg);
    if (!success) {
      if (!newmsg.isNoSuchAgentOrDvm())
        sendMessageFromUser(newmsg, "Error", "NO_SUCH_DVM", null, null, newmsg.toString(), null);
    }
    return ""+newmsg.replyWith;
  }

  /**
   * (move)�ɂ��Ăяo�����B
   * �G�[�W�F���g���ړ�����B
   * _move�Ƃ������b�Z�[�W�𐶐����DVM�ɓ͂���B
   * DVM.processDVMmsg()�ŏ��������B
   * @param arrival �ړ���̊���
   * @param name �A�N�V���������s�����G�[�W�F���g��(�ړ���ł�����)
   * @param program �G�[�W�F���g�v���O����
   * @param workmem ���[�L���O������
   * @param filename �f�B���N�g�����̂Ȃ��t�@�C�����B
   * @return �������Ă����s���Ă��A_move�̃��b�Z�[�WID
   * @see instantiateAgent()
   */
  long moveAgent(String arrival, String cname, String name, ps.AgentProgram program, ps.WorkMem workmem, String filename, String origin) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    DashMessage newmsg = new DashMessage(DashMessage.MOVE,
                                         name,
                                         DashMessage.DVM,
                                         arrival,
                                         messageID);

    newmsg.setOtherAttributes(":filename", filename);
    newmsg.setOtherAttributes(":program", program);
    newmsg.setOtherAttributes(":workmem", workmem);
    newmsg.setOtherAttributes(":origin", origin);
    newmsg.setOtherAttributes(":cname", cname);
    messageID++;


    boolean success = comInt.sendMsg(newmsg);
    if (!success) {
      if (!newmsg.isNoSuchAgentOrDvm())
        sendMessageFromUser(newmsg, "Error", "NO_SUCH_DVM", null, null, newmsg.toString(), null);
    }

    return newmsg.replyWith;
  }

  /**
   * (instantiate :into)�ɂ��Ăяo�����B
   * �G�[�W�F���g���C���X�^���V�G�[�g����B
   * _createInstance�Ƃ������b�Z�[�W�𐶐����DVM�ɓ͂���B
   * DVM.processDVMmsg()�ŏ��������B
   * @param oav  instantiate�A�N�V�����̋L�q
   * @param name �A�N�V���������s�����G�[�W�F���g��
   * @param description �m���L�q
   * @param filename �f�B���N�g�����̂Ȃ��t�@�C�����B
   * @return �������Ă����s���Ă��A_createInstance�̃��b�Z�[�WID
   * @see moveAgent()
   */
  long instantiateAgent(OAVdata oav, String name, String description, String filename, String origin) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    String arrival = oav.getValueString(":into");
    DashMessage newmsg = new DashMessage(DashMessage.CREATEINSTANCE,
                                         name,
                                         DashMessage.DVM,
                                         arrival,
                                         messageID);

    // :content��:fact()�Ŏw�肵��OAV�f�[�^���l�߂�
    TafList list = new TafList(0);
    TafList facts = (TafList)oav.getValue(":facts");
    for (int i=0; i<facts.size(); i++) {
      Value value = facts.getValue(i);
      if (value instanceof OAVdata)
        list.addElement(value);
    }
    newmsg.setContent(list.toString());

    newmsg.setOtherAttributes(":filename", filename);
    newmsg.setOtherAttributes(":description", description);
    newmsg.setOtherAttributes(":origin", origin);
    messageID++;

    boolean success = comInt.sendMsg(newmsg);
    if (!success) {
      if (!newmsg.isNoSuchAgentOrDvm())
        sendMessageFromUser(newmsg, "Error", "NO_SUCH_DVM", null, null, newmsg.toString(), null);
    }

    return newmsg.replyWith;
  }

  /**
   * (instantiate :from :name)�ɂ��Ăяo�����B
   * �G�[�W�F���g���C���X�^���V�G�[�g����B
   * _instantiate�Ƃ������b�Z�[�W�����|�W�g���ɓ͂���B
   * @param oav  instantiate�A�N�V�����̋L�q
   * @param name �A�N�V���������s�����G�[�W�F���g��
   */
  long sendInstantiate(OAVdata oav, String name) {
    if (wpIndex != -1 ) {
      if (wpTab != null ) {
        wpTab.setSelectedIndex(wpIndex);
      }
    }

    newif.replaceConsole();
    String arrival = oav.getValueString(":from");
    DashMessage newmsg = new DashMessage(DashMessage.INSTANTIATE,
                                         name,
                                         DashMessage.DVM,
                                         arrival,
                                         messageID);

    newmsg.setOtherAttributes(":target", oav.getValueString(":name"));
    messageID++;

    boolean success = comInt.sendMsg(newmsg);
    if (!success) {
      if (!newmsg.isNoSuchAgentOrDvm())
        sendMessageFromUser(newmsg, "Error", "NO_SUCH_DVM", null, null, newmsg.toString(), null);
    }

    return newmsg.replyWith;
  }

  /**
   * dvmname�Ŏw�肳�ꂽDVM�����݂��邩�𒲂ׂ�B
   * @return ���݂���ꍇ�Atrue�B
   */
  boolean checkDVM(String dvmname) {
    newif.replaceConsole();
    return comInt.checkDVM(dvmname);
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
    //change uchiya
    newif.replaceConsole();
    return comInt.lookup(selector);
  }

  /**
   * �N���X���[�_�̏����B
   */
  private void setupClassLoader() {
    String dirs = System.getProperty("dash.userClassPath");
    StringTokenizer st = new StringTokenizer(dirs, File.pathSeparator);
    int pathCount = st.countTokens();
    //System.out.println("count="+pathCount);
    userClassPath = new String[pathCount];

    int i=0;
    while (st.hasMoreTokens()) {
      String dir = st.nextToken();
      if (!dir.endsWith(File.separator))
        dir += File.separator;
      userClassPath[i] = dir;
      i++;
    }
  }

  /**
   * �o�C�g�R�[�h��Ԃ��B
   * @param classname �N���X��
   * @param origin    �G�[�W�F���g�̏o��DVM��
   * @see getRuleset()
   */
  byte[] loadClassData(String classname, String origin) throws Exception {
  	//System.out.println("this="+getDVMname()+" origin="+origin);
    //tempclassname = "";
    if (getDVMname().equals(origin))
      return loadLocalClassData(classname);
    else
      return loadRemoteClassData(classname, origin);
  }

  /**
   * �N���X�t�@�C������N���X��ǂݍ��݁A�o�C�g�R�[�h��Ԃ��B
   * (classname���v���p�e�B�t�@�C���̃t�@�C�����̏ꍇ������)
   * @param classname �N���X��
   */
  private byte[] loadLocalClassData(String classname) throws Exception {
    File classfile = null;
    for (int i=0; i<userClassPath.length; i++) {
    	//System.out.println("�N���X"+i+"�Ԗ�"+userClassPath[i]);
      // (1)<classname>.class�̏ꍇ
      String filename =
        userClassPath[i]+
        classname.replace('.', File.separatorChar)+
        ".class";
      classfile = new File(filename);
      if (classfile.exists()){
        break;
    }
      // (2)<classname>.properties�̏ꍇ
      filename = userClassPath[i]+
        classname.replace('.', File.separatorChar)+
        ".properties";
      classfile = new File(filename);
      if (classfile.exists()) {
        break;
      }
      // (*)�����ꍇ
      classfile = null;
    }

    //�N���X�p�X���������Ă�������Ȃ������ꍇ
    if (classfile==null) {
			  setTempClassName("dash.DammyBP");
        String path= "classes"+ File.separator +"dash"+ File.separator +"DammyBP.class";
        classfile = new File(new DashDefaults().getDashdir(),path);
        System.err.println("DVM.loadClassData(): �N���X"+classname+"�̃N���X�t�@�C����������܂���");
       
    }
    byte[] classBytes = null;
    FileInputStream fis = new FileInputStream(classfile);
    classBytes = new byte[(int)classfile.length()];
    fis.read(classBytes);
	//System.out.println(classBytes.length);
    return classBytes;
  }

  /**
 * @param string
 */
private void setTempClassName(String string) {
tempclassname = string;
}

/**
   * ����DVM��_getBytecode�𑗐M����_putBytecode��Ԃ��Ă��炢�A
   * ���̒��Ɋi�[���ꂽ�o�C�g�R�[�h��Ԃ��B
   * @param classname �N���X��
   * @param origin ���肩
   * @see getRemoteRuleset()
   */
  private byte[] loadRemoteClassData(String classname, String origin) throws Exception {
    Hashtable other = new Hashtable();
    other.put(":classname", classname);
    DashMessage msg =
      sendMessageFromUser(null, DashMessage.GETBYTECODE,
                          "REQUEST", DashMessage.DVM, origin, null, other);

    DashMessage reply = waitReply(msg);

    String result = reply.getOtherAttributes(":system");
    byte[] classBytes = null;
    if (result.equals("SUCCESS"))
      classBytes = (byte[])reply.getOtherAttributesB(":bytecode");

    return classBytes;
  }

  /**
   * _putBytecode, _putRuleset��҂B
   */
  private DashMessage waitReply(DashMessage msg) {
    String key = Long.toString(msg.replyWith);

    synchronized(replyBox) {
      if (replyBox.get(key) == null)
        replyBox.put(key, new Vector());
    }
    Vector box = (Vector)replyBox.get(key);

    synchronized(box) {
      try {
        if (box.isEmpty())
          box.wait();
      } catch (Exception e) { e.printStackTrace(); }
    }

    DashMessage reply = (DashMessage)box.remove(0);
    replyBox.remove(key);
    return reply;
  }


  /**
   * _putBytecode, _putRuleset���͂����Ƃ��ɌĂ΂��B
   */
  private void putReply(DashMessage msg) {
    String key = Long.toString(msg.inReplyTo);

    synchronized(replyBox) {
      if (replyBox.get(key) == null)
        replyBox.put(key, new Vector());
    }
    Vector box = (Vector)replyBox.get(key);

    synchronized(box) {
      box.addElement(msg);
      box.notify();
    }
  }


  /**
   * �G�[�W�F���g����v�����ꂽ���[���Z�b�g�̃t�@�C����ǂ݂��݁A�Ԃ��B
   * @param filename �t�@�C����
   * @param env ���[���Z�b�g���u���Ă����
   * @return ���[���Z�b�g
   * @see loadClassData()
   */
  public String getRuleset(String filename, String env) {
    newif.replaceConsole();
    String dvmname = getDVMname();
    
    String LoadDir = System.getProperty("dash.loadpath");
      File f  = null;
      Hashtable htFileList = null;
      if (LoadDir != null ) {
        htFileList = new Hashtable();
        String[] st = LoadDir.split(":");
        for (int i=0; i < st.length; i++) {
          String data = st[i];
          f  = new File (data);
          createFileList(f,htFileList, LoadDir);
        }
      }

    if (getDVMname().equals(env)) {
      return getLocalRuleset(filename, htFileList);
    }
    else {
      return getLocalRuleset(filename, htFileList);
    }
  }


  /**
   * <p>createFileList </p>
   * <p>�w�肵���f�B���N�g���ȉ��Ɋ܂܂��S�Ẵt�@�C���̈ꗗ���쐬���� </p>
   * <p>���쌠: Copyright (c) 2003</p>
   * <p>�R�X���X </p>
   * @author ������
   * @version 1.0
   */
  public void createFileList(File f, Hashtable htFileTable, String DefaultDir ) {
    File current_dir = new File(f,".");
    String file_list[] = current_dir.list();

    String wkDefaultDir = DefaultDir + "/:" ;

    for (int i=0; i<file_list.length; i++ ) {
      File current_file = new File(f,file_list[i]);
      if (current_file.isDirectory()){
        String dirstr = current_file.getAbsolutePath();
        if (!dirstr.endsWith(File.separator) ) {
          dirstr += File.separator;
        }

        if (wkDefaultDir.indexOf(dirstr) != -1 ) {
          createFileList(current_file, htFileTable, DefaultDir);
        }
      }
      else {
        String parentpath = current_file.getParent();
        if (!parentpath.endsWith(File.separator) ) {
          parentpath += File.separator;
        }
        if (wkDefaultDir.indexOf(parentpath) != -1 ) {
          htFileTable.put(current_file.getName(),current_file.getAbsolutePath());
        }
      }
    }
  }

  /**
   * dash.loadpath�Ŏw�肳��Ă���f�B���N�g������
   * ���[���Z�b�g�̃t�@�C����ǂ݂��݁A�Ԃ��B
   * @param filename (�f�B���N�g�����Ȃǂ��t���Ȃ�)�t�@�C����
   * @return ���݂����ꍇ���[���Z�b�g�̃e�L�X�g�B���݂��Ȃ��ꍇ�Anull�B
   */
  private String getLocalRuleset(String filename, Hashtable htFileList) {
  	if (htFileList.get(filename) == null ) {
  		return "";
  	}
  	
  	String filename_wk = (String)htFileList.get(filename);
  	if (filename_wk.equals("")) {
  		return "";
  	}
    File file = new File(filename_wk);
    
    if (file.exists() && file.canRead() && !file.isDirectory()) {
      StringBuffer buf = new StringBuffer();
      BufferedReader br = null;
      try {
        //BufferedReader br = new BufferedReader(new FileReader(file));
        br = new BufferedReader(new InputStreamReader(
                                                new FileInputStream(file.getAbsolutePath()),
                                                "JISAutoDetect"));

        while (br.ready()) {
          String line = br.readLine();
          if (line != null) {
            // ����if�߂�StreamTokenizer�̃o�O�΍�B/* */���ɋ�s�������
            // ����܂���lineno��Ԃ��̂ŁA�󔒂�ǉ�����B
            if (line.length() == 0)
              line = " ";
            buf.append(line);
            buf.append("\n");
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
          try {
      	    if (br != null) {
      		 br.close();
      		 br=null;
         }
        } catch (IOException e) {
         e.printStackTrace();
        }
      }
      
      return buf.toString();
    }
		return null;

  }

  /**
   * ����DVM��_getRuleset�𑗐M����_putRuleset��Ԃ��Ă��炢�A
   * ���̒��Ɋi�[���ꂽ���[���Z�b�g��Ԃ��B
   * @param filename �t�@�C����
   * @param env DVM��
   * @see loadRemoteClassData()
   */
  private String getRemoteRuleset(String filename, String env) {
    Hashtable other = new Hashtable();
    other.put(":filename", filename);
    DashMessage msg =
      sendMessageFromUser(null, DashMessage.GETRULESET,
                          "REQUEST", DashMessage.DVM, env, null, other);

    DashMessage reply = waitReply(msg);

    String result = reply.getOtherAttributes(":system");
    String text = null;
    if (result.equals("SUCCESS"))
      text = reply.getOtherAttributes(":ruleset");

    return text;
  }

  /**
   * �G�[�W�F���gagname�ɃA���[��oav��`����B
   * @return �G�[�W�F���g�ɓ͂��邱�Ƃ��ł�����true�B
   */
  boolean notifyAlarm(String agname, String id, String oav) {
    newif.replaceConsole();
    DashAgent agent = (DashAgent)agTable.get(agname);
    if (agent != null) {
      agent.notifyAlarm(id, oav);
      return true;
    } else
      return false;
  }

  /**
   * ���O��Ԃ��B
   */
  String getFullname() {
    return comInt.getDVMname();
  }

  /**
   * �C���X�y�N�^��\������
   */
  void openInspector(String agname) {
    DashAgent agent = (DashAgent)agTable.get(agname);
    agent.inspect();
  }

  /**
   * ���|�W�g���^DVM�Ȃ�true��Ԃ��B
   */
  public boolean isRtype() {
    return false;
  }

  /** �G�[�W�F���g�������true��Ԃ��B*/
  boolean hasAgent(String agname) {
    return (agTable.get(agname) != null);
  }

  /** �G�[�W�F���g�̌���Ԃ��B*/
  int numberOfAgent() {
    return agTable.size();
  }

  /**
   * Newif�ɕ\������B
   */
  void println(String s) {
    newif.println(s);
  }

  /**
   * Newif�ɕ\������B
   */
  void printlnE(String s) {
    newif.printlnE(s);
  }

  /** Newif�ɐݒ肷�� */
  void settextOnNewif(String agent, String s) {
    newif.settext(agent, s);
  }

  /**
   * �z�X�g�����܂ފ�����Ԃ��B
   */
  public String getDVMname() {
    return comInt.getDVMname();
  }

  // ADD COSMOS
  void showAgentScript(String agname) {
    Vector agnames = new Vector();
    for (Enumeration e = agTable.keys(); e.hasMoreElements(); )
      agnames.addElement(e.nextElement());
  }

  /**
   * �v���W�F�N�g�t�@�C��(*.prj)��ǂݍ��݁A��������B
   */
  public void loadProject(File file) {
    newif.replaceConsole();
    StringBuffer buffer = new StringBuffer();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(
                                              new FileInputStream(file.getAbsolutePath()),
                                              "JISAutoDetect"));
      while (br.ready()) {
        String line = br.readLine();
        if (line != null)
          buffer.append(line + "\n");
      }
    } catch (FileNotFoundException e) {
      System.err.println("�t�@�C��"+file+"������܂���");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
        try {
    	    if (br != null) {
    		 br.close();
    		 br=null;
       }
      } catch (IOException e) {
       e.printStackTrace();
      }
    }
    ps.Parser parser = new ps.Parser(file.getName(), buffer.toString(), false);
    Vector lists = null;
    try {
      lists = parser.parseOAVinProject(file.getParentFile());
    } catch (ps.SyntaxException e) {
      System.err.println("�x��: �t�@�C�� "+file+":"+e.lineno+": "+e.comment);
      lists.removeAllElements();
    }
    boolean error = false;
    for (Enumeration e = lists.elements(); e.hasMoreElements(); ) {
      Object element = e.nextElement();
      if (element instanceof String) {
        System.err.println(element); // �G���[
        error = true;
      }
    }
    if (!error)
      for (Enumeration e = lists.elements(); e.hasMoreElements(); ) {
        Object obj = e.nextElement();
        if (obj instanceof File)
          addLoadQueue((File)obj);
      }
  }

  /**
   * �A���[�����Z�b�g����B
   * @param agname �G�[�W�F���g�̖��O
   * @param repeat ����Ԃ��ꍇtrue
   * @param time   �~���b
   * @param oav    �ʒm����OAV�^�f�[�^
   * @return �A���[��ID
   */
  String setAlarm(String agname, boolean repeat, long time, String oav) {
    newif.replaceConsole();
    return alarmManager.setAlarm(dashThreads, agname, repeat, time, oav);
  }

  /**
   * �A���[�����L�����Z������B
   * @param agname �G�[�W�F���g�̖��O
   * @param alarmID "all"�܂���"alarm�A���[��ID"
   * @return �A���[��ID
   */
  String[] cancelAlarm(String agname, String alarmID) {
    newif.replaceConsole();
    return alarmManager.cancelAlarm(agname, alarmID);
  }

  IdeaMainFrame parentframe;
  public void setParentFrame (IdeaMainFrame parentframe){
    this.parentframe = parentframe;
  }
  public IdeaMainFrame getParentFrame (){
    return parentframe;
  }

  private int wpIndex = -1;
  public void setWpIndex (int wpIndex){
    this.wpIndex = wpIndex;
  }
  private JTabbedPane wpTab = null;
  public void setWpTab (JTabbedPane wpTab ) {
    this.wpTab = wpTab;
  }
  public static void main(String args[]) {
    localOnly = true;

    String s[] = { };
    Repository.main(s);
    Workplace.main(s);
  }
  // added by takagaki
  public String getProjectPath()
  {
	return parentframe.getProjectPath();
  }

/**
 * 
 */
public String getTempClassName() {
return tempclassname;
}
  
  public String[] getUserClasspath(){
  return userClassPath;
  }
  
}
