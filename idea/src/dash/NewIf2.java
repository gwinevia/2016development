package dash;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import editortools.*;

//�r���[�A����̒��S�ƂȂ�N���X
public class NewIf2 extends JPanel implements ActionListener, NewifItface {

  /** DVM */
  private DVM dvm;

  private String dvmname;

  /** �G���[��\�����镔�i */
  private JTextArea errorLogArea;
  private JScrollPane errorScrollPane;
  private int errorLogAreaLines;

  /** �S�Ẵ��O��\�����镔�i */
  private JTextArea logArea;
  private JScrollPane logScrollPane;
  private int logAreaLines;

  /** ACL�G�f�B�^ */
  //private AclPanel aclPanel;
  //uchiya
  public AclPanel aclPanel;

  /** ACL�G�f�B�^�ւ̃��b�Z�[�W��\�����镔�i */
  private JTextArea receiveArea;

  /** ���b�Z�[�W�̓��e��\�����镔�i */
  private JTextArea msgArea;

  /** �^�u */
  //uchiya
  //private JTabbedPane logTabbedPane;
  public JTabbedPane logTabbedPane;

  /** �G�[�W�F���g��\������ */
  private DashTree2 treePane;
  private DashTreeModel treePaneModel;

  /** ���|�W�g���^DVM�Ȃ�true */
  private boolean isRtype;

  /** �|�b�v�A�b�v */
  private JPopupMenu treePopupMenu;

  /** �t���[�� */
  private JFrame ifFrame;
  private JSplitPane split;

  /** �A�N�V���� */
  private SendAction sendAction = new SendAction("Send", null);
  private InspectAction inspectAction = new InspectAction("Inspect", null);
  private CopyAction copyAction = new CopyAction("Copy", null);

  /** �������\�� */
  private JLabel memoryLabel;

  /** �X�e�b�v�{�^���Ȃ� */
  private JCheckBox nonStopCheck;
  private JButton stepButton;

  /** �C���f���g�p�X�y�[�X */
  private static String INDENT = "    ";

  /** �u��������I�u�W�F�N�g�Boff�̏ꍇ��null�B*/
  private static ConsoleReplace consoleReplace = null;
  //uchiya
  //private ConsoleReplace consoleReplace = null;
  
  /** �u��������I�u�W�F�N�g�Boff�̏ꍇ��false�B
   * true�̏ꍇ�AProdSys�̃��C�����[�v��sleep���Ă���B
   */
  private static boolean usingConsoleReplace = false;

  /** open����f�t�H���g�̃f�B���N�g�� */
  private File defaultOpenDir;

  /** EditMenu��\�������Ƃ���FocusedObject��ێ����邽��(?)�̕ϐ� */
  private EditMenuListener editMenuListener;

  /** synchronized�p�I�u�W�F�N�g */
  ////private String objectForSync = "s";

  /** nextTurn�̂��߂�key��ێ�����ϐ� */
  Long keyForShowMsg;

  /** nonstop���[�h�̑҂����ԁB200ms���f�t�H���g�B*/
  private int waittimeForNonstop;

  /****************************************************************************/
  /* �R�X���X�ǉ���                                                             */
  /****************************************************************************/
  /** �R�[�h�G�f�B�^�p�f�X�N�g�b�v�y�C�� **/
  public JDesktopPane CodeEditorDesktop  = null;
  public JDesktopPane CodeEditorDesktop2 = null;

  /** WIndow�����p **/
  // �J�X�P�[�h�\���p�̃C���N�������g�l
  int xincrement = 20, yincrement = 30;
  int xnew = xincrement, ynew = yincrement;
  int newwidth = 200, newheight = 150;

  public ViewerCanvasW2 canvas;
  public ViewerCanvasW2 canvasW;
  public ViewerCanvasR2 canvasR;

  public ProjectTree projectTree1 = null;
  public ProjectTree projectTree2 = null;
  public ProjectTree projectTree3 = null;
  public ProjectTree projectTree4 = null;
  private SearchPanel searchPanel = null;

  private Simulator simulatorPanel = null;
  private SavePanel savePanel = null;
  private File dashdir = null;
  private String dashdirStr = null;
  
  //add uchiya
  private final Color eventColor = Color.RED;
  public File msgfile=null;
  private Vector vecMsgHist = new Vector();
  
  private static final int MAX_MSG_CNT = 30;
  private static final int MAX_LOG_CNT = 200;
  private static final int MAX_ERR_CNT = 200;
 
  //���O�ۊǂ��邩�ǂ��� create 05/02/17
  private boolean USELOG = false;
  
 
  /** �R���X�g���N�^ */
  NewIf2(String dvmname, DVM dvmparam, File msgfile, File dashdir) {

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
 
    this.msgfile=msgfile;
    this.setLayout(new BorderLayout());
    //super(dvmname);
    this.dvmname = dvmname;
    this.dvm = dvmparam;
    this.dashdir = dashdir;
    isRtype = dvm.isRtype();
    
    vecMsgHist.clear();
    
    //uchiya changed
    //ifFrame = this;

    String dirnames = System.getProperty("dash.loadpath");
    StringTokenizer st = new StringTokenizer(dirnames, File.pathSeparator);
    String dirname = st.nextToken();
    File file = new File(dirname);
    if (file.exists() && file.canRead() && file.isDirectory())
      defaultOpenDir = file;
    else
      defaultOpenDir = dashdir;

    if (!dashdir.toString().endsWith(File.separator)) {
        dashdirStr = dashdir.toString() + File.separator;
    }
    else {
      dashdirStr = dashdir.toString() + File.separator;
    }

		String wk = dashdir.toString();
    // �c�[���o�[
    JToolBar toolbar = new JToolBar();

    /**************************************************************************/
    /* revised by cosmos 2003/01/18                                           */
    /* �C�����e�F�t�@�C���I�[�v���E�V�K�쐬�̃{�^����ǉ�                     */
    /**************************************************************************/
    /*
    JButton newfileBtn = new JButton (new actNewFile());
    JButton openfileBtn = new JButton (new actOpen());
    toolbar.add(newfileBtn);
    toolbar.add(openfileBtn);
    */
    /**************************************************************************/
    /* revised by cosmos 2003/01/18�@�����܂�                                  */
    /**************************************************************************/

    nonStopCheck = new JCheckBox(getImageIcon("resources/pause.gif"));
    nonStopCheck.setSelectedIcon(getImageIcon("resources/nonstop.gif"));
    nonStopCheck.setSelected(true); // �X�e�b�v����
    nonStopCheck.setHorizontalAlignment(SwingConstants.CENTER);
    nonStopCheck.setFocusPainted(false);
    nonStopCheck.setBorderPainted(true);
    nonStopCheck.setMargin(new Insets(0,0,1,0));
    toolbar.add(nonStopCheck);

    stepButton = new JButton(getImageIcon("resources/step.gif"));
    stepButton.setEnabled(false);      // �����s�\
    nonStopCheck.setHorizontalAlignment(SwingConstants.CENTER);
    stepButton.setBorder(BorderFactory.createRaisedBevelBorder());
    stepButton.setFocusPainted(false);
    toolbar.add(stepButton);
    toolbar.setFloatable(false);

    CheckBoxListener listener = new CheckBoxListener();
    nonStopCheck.addItemListener(listener);
    stepButton.addActionListener(listener);


    // Tree�y�C��
    treePaneModel = new DashTreeModel(dvmname);
    treePane = new DashTree2(this, treePaneModel);
    treePane.setRootVisible(false);
    treePane.setCellRenderer(new DashRenderer());
    JScrollPane treeScrollPane = new JScrollPane(treePane);
    treeScrollPane.setBorder(new TitledBorder("Env. & Agent"));
    // Tree�y�C���̃A�N�V����
    treePane.addMouseListener(new TreeSelect());
    treePopupMenu = new JPopupMenu("Tree");
    treePopupMenu.add(inspectAction);
    treePopupMenu.add(sendAction);
    treePopupMenu.addSeparator();
    treePopupMenu.add(copyAction);
    // Tree���J��
    treePane.expand();

    JPanel treePanel = new JPanel (new BorderLayout() );

    treePanel.add(toolbar,BorderLayout.NORTH);
    treePanel.add(treeScrollPane,BorderLayout.CENTER);

    //memoryLabel = new JLabel("? / ? Kb");
    //treePanel.add(memoryLabel, BorderLayout.SOUTH);

    // log�^�u
    logArea = new JTextArea(""); //(dvmname="+dvmname+")\n");
    logArea.setEditable(false);
    logArea.setLineWrap(true);
    logScrollPane = new JScrollPane(logArea);
    logAreaLines = 0;

    // error�^�u
    errorLogArea = new JTextArea();
    errorLogArea.setEditable(false);
    errorLogArea.setLineWrap(true);
    errorScrollPane = new JScrollPane(errorLogArea);
    errorLogAreaLines = 0;

    // acl-editor�^�u
    String options[] = { "Send" };
    aclPanel = new AclPanel(options, null, this, dvm);
    aclPanel.setContArea("()");
    JScrollPane aclScrollPane = new JScrollPane(aclPanel);

    // receive�^�u
    receiveArea = new JTextArea();
    receiveArea.setLineWrap(true);
    JScrollPane receiveScrollPane = new JScrollPane(receiveArea);

    // msg�^�u
    msgArea = new JTextArea();
    msgArea.setEditable(false);
    msgArea.setLineWrap(true);
    JScrollPane msgScrollPane = new JScrollPane(msgArea);

    // �^�u����t��
    logTabbedPane = new JTabbedPane(JTabbedPane.TOP);

    logTabbedPane.addTab("Env. & Agent", treePanel);
    logTabbedPane.addTab("log", logScrollPane);
    logTabbedPane.addTab("error", errorScrollPane);
    logTabbedPane.addTab("acl-editor", aclScrollPane);
    logTabbedPane.addTab("receive", receiveScrollPane);
    logTabbedPane.addTab("msg", msgScrollPane);
	  //by ohmae
	  logTabbedPane.addChangeListener(new TabCheck());

    this.add(logTabbedPane,BorderLayout.CENTER);
    //memoryLabel = new JLabel("? / ? Kb");
    //uchiya
	memoryLabel = new JLabel(" ");
    this.add(memoryLabel, BorderLayout.SOUTH);
    //this.add(new TextField(), BorderLayout.SOUTH);
  }

  private JMenuItem menuItem(String label) {
    JMenuItem item = new JMenuItem(label);
    item.addActionListener(this);
    return item;
  }

  
  /** ImageIcon��Ԃ��B*/
  private ImageIcon getImageIcon(String path) {
    java.net.URL url = this.getClass().getResource(path);
    return new ImageIcon(url);
  }
 
  static boolean allowMakeMemoryThread = true;
  /** �������̊Ď����J�n���� */
  public void startMemoryWatch() {
    if(allowMakeMemoryThread==false) return;
	
    Runnable r = new Runnable() {
        Runtime runtime = Runtime.getRuntime();
        
        long total, free, use;

        public void run() {
          replaceConsole();
		 // System.out.println("start!! ["+this.toString()+"]");
          if (usingConsoleReplace)
            while (consoleReplace==null)
              try { Thread.sleep(500); } catch (InterruptedException e) { }

          while (true) {
            if (usingConsoleReplace){
            	//System.out.println("memorywatch.print");
            	consoleReplace.print();
	      	}
            	
            memoryLabel.setText("agent: "+dvm.numberOfAgent());
            try { Thread.sleep(1000); } catch (InterruptedException e) { }
          }
        }
    };
    Thread thread = new Thread(dvm.dashThreads, r, "MemoryWatcher");
    thread.start();
    allowMakeMemoryThread=false;
    while (usingConsoleReplace && consoleReplace==null)
      try { Thread.sleep(500); } catch (InterruptedException e) { }
  }


  /** nonStopCheck�����������(nonstop���)�ɂ��� */
  public void setNonstop() {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if (!nonStopCheck.isSelected()) {
            nonStopCheck.doClick();
          }}});
  }

  /**
   * �G�[�W�F���g�����ɒǉ�����B���̏ꍇ�ɌĂ΂��B
   * 1)���|�W�g���G�[�W�F���g���t�@�C�����琶�����ꂽ�Ƃ�
   * 2)�C���X�^���X�G�[�W�F���g���������ꂽ�Ƃ�
   *
   * @param name �G�[�W�F���g��
   */
  public void addAgent(final String name) {
  }
  public void addAgent(final String name, String origin){
    final Long key = treePaneModel.waitTurn();

    // �ǉ�����B
    Runnable r = new Runnable() {
        public void run() {
          DefaultMutableTreeNode envnode = treePaneModel.addAgentnode(name);
          DefaultTreeModel treeModel = (DefaultTreeModel)treePane.getModel();
          treeModel.nodeStructureChanged(treePaneModel.getRootnode());

          // ������B
          TreeNode nodes[] = treeModel.getPathToRoot(envnode);
          TreePath path = new TreePath(nodes);
          treePane.expandPath(path);
          treePaneModel.nextTurn(key);
        }
      };
    SwingUtilities.invokeLater(r);

    //canvas.showNewAgent ("origin",name );
    //canvas.showNewAgent ("w1:CSMSXPCLNT01",name );


    if (isRtype ) {
    	if(canvasR!=null){
    //System.out.println(canvasR);
      canvasR.showNewAgent (origin,name );
    	}
      //canvasR.showNewAgent ("origin",name );
    }
    else {
		if(canvasW!=null){
      canvasW.showNewAgent (origin,name );
		}
      //add uchiya
      //�}�l�[�W�������
      StringTokenizer st = new StringTokenizer(((DashAgent)dvm.agTable.get(name))
      .prodsys.workmem.toStringAllFacts());
      String manager=null;
      while(st.hasMoreTokens()){
      	String s1= st.nextToken();
      	if(s1.equals("(Members")){
      		if(st.nextToken().equals(":manager")){
      			String str2=st.nextToken();
      			//System.out.println(str2);
      			if(!str2.equals("_interface)")&&!str2.equals("_interface")){
      				manager=str2;
      			}
      		}
      	}
      }
      if(manager!=null){
      	DashMessage dm= new DashMessage("_specify",manager,name,null,0);
		if(canvasW!=null){
		canvasW.specifyManager(dm);
		}
      }
    }

    //treePaneModel.nextTurn();
  }

  /** �G�[�W�F���g������ */
  public void removeAgent(final String name) {
    final Long key = treePaneModel.waitTurn();

    Runnable r = new Runnable() {
        public void run() {
          // �����B
          DefaultMutableTreeNode parent = treePaneModel.removeAgentnode(name);
          treePane.removeEntry(name);

          DefaultTreeModel treeModel = (DefaultTreeModel)treePane.getModel();
          treeModel.nodeStructureChanged(treePaneModel.getRootnode());

          // ������B
          TreeNode nodes[] = treeModel.getPathToRoot(parent);
          TreePath path = new TreePath(nodes);
          treePane.expandPath(path);
          treePaneModel.nextTurn(key);

        }
      };
    SwingUtilities.invokeLater(r);
    if (isRtype ) {
	if(canvasR!=null){
      canvasR.removeAgent (name );
	}
      //canvasR.showNewAgent ("origin",name );
    }
    else {
		if(canvasW!=null){
      canvasW.removeAgent (name);
		}
      //canvasW.showNewAgent ("origin",name );
    }
    //treePaneModel.nextTurn();
  }

  /** �{���ɏ����ꂽ�̂�҂B*/
  public void confirmSync() {
    Long key = treePaneModel.waitTurn(); // �L���[�̍Ō�ő҂B
    treePaneModel.nextTurn(key); //
  }

  /**
   * ���b�Z�[�W�̏������s���B
   */
  public void showMsg(DashMessage m) {
    //System.out.println ("NewIf Call ShowMsg()");  COSMOS

    keyForShowMsg = treePaneModel.waitTurn();
    showMessage(m);

    /*
    if (!m.performative.equals(DashMessage.INSTANTIATE)) {
      if (isRtype ) {
        canvasR.showMsg(m, isRtype);
      }
      else {
        canvasW.showMsg(m, isRtype);
      }
    }
    */
    //canvas.showMsg(m, isRtype);
    //treePaneModel.nextTurn();  �������ł͌Ă΂Ȃ��B

    /*DTM
    synchronized (objectForSync) {
      showMessage(m);
    }
    */
  }
  public void ViewerShowMsg(DashMessage m){
    if (isRtype ) {
		if(canvasR!=null){
      canvasR.showMsg(m, isRtype);
		}
    }
    else {
		if(canvasW!=null){
      canvasW.showMsg(m, isRtype);
		}
    }
  }


  /**
   * ���b�Z�[�W�̏������s���B
   */
  private void showMessage(final DashMessage m) {
    //System.out.println ("NewIf Call showMessage()"); COSMOS

    //logTabbedPane.setSelectedIndex(0);
	  //logTabbedPane.setForegroundAt(0, eventColor);
    // specify_manager�Ȃ�A�؂����B
    if (m.isSpecifyManager()) {
      //System.out.println ("NewIf Call showMessage()-specify_manager"); COSMOS
      Runnable r = new Runnable() {
          public void run() {
            // �Ώ�
            DefaultMutableTreeNode manager =
              treePaneModel.getAgentnode(m.from);
            DefaultMutableTreeNode contractor =
              treePaneModel.getAgentnode(m.to);

            // �t���ւ���
            manager.add(contractor);

            DefaultTreeModel treeModel = (DefaultTreeModel)treePane.getModel();
            treeModel.nodeStructureChanged(treePaneModel.getRootnode());

            // ������B
            TreeNode nodes[] = treeModel.getPathToRoot(manager);
            TreePath path = new TreePath(nodes);
            treePane.expandPath(path);

            treePaneModel.nextTurn(keyForShowMsg);
            keyForShowMsg = null;
          }
        };
      SwingUtilities.invokeLater(r);
    } else {
      Runnable r = new Runnable() {
          public void run() {
            synchronized (msgArea) {
            	/*
              int maxcnt = 30;
              if (System.getProperty("dash.msgmaxcnt") != null ) {
              	maxcnt = new Integer( System.getProperty("dash.msgmaxcnt") ).intValue();
              }
					    
							vecMsgHist.addElement(m.toString2());
							if (vecMsgHist.size() > maxcnt ) {
								vecMsgHist.removeElementAt(0);
							}
							*/
							vecMsgHist.addElement(m.toString2());
							if (vecMsgHist.size() > MAX_MSG_CNT ) {
								vecMsgHist.removeElementAt(0);
							}
							

              StringBuffer buf =new StringBuffer();
              for (int i=0; i<vecMsgHist.size();i++) {
                  buf.append((String)vecMsgHist.elementAt(i));
                  try {
					buf.append('\n');
				} catch (Exception e) {
						e.printStackTrace();
				}
                  try {
					buf.append('\n');
				} catch (Exception e1) {
						e1.printStackTrace();
				}
              }
              String text = buf.toString();
							msgArea.setText(text);

              //msgArea.setText(m.toString2());
            }
          }};
      SwingUtilities.invokeLater(r);
      treePane.showMsg(m, dvm.getFullname(), keyForShowMsg);
      //treePaneModel.nextTurn(); ��DashTree.showMsg()�̒��ŌĂԁB
    }
  }



	/** ���O�̋L�^�@�@
	 * @logText ���O�̓��e
	 * @logKind ���O�̎��
	 * */
	private void writeLog (String logText, String logKind ) {
		//	�������烁�b�Z�[�W���O�ۑ�
		if(USELOG){
 	  String logFileName = "";
		// �V�X�e���v���p�e�B����p�X�t�����O�t�@�C�������擾����
		if (logKind.equals("msg") ) {
			logFileName = (String)System.getProperty("msgfilename");
			// MSG�̏ꍇ�A���b�Z�[�W�ԍ����擾����
			String msgnoStr = (String)System.getProperty("msgno");
			// ������������ރ��b�Z�[�W�̐擪�ɒǉ�
			logText = msgnoStr + " " + logText;
			
			// ���b�Z�[�W�ԍ����P�������āA�V�X�e���v���p�e�B�ɍď�������
			System.setProperty("", new Integer(new Integer(msgnoStr).intValue() + 1).toString());			
		}
		else if (logKind.equals("err") ) {
			logFileName = (String)System.getProperty("errfilename");
		}
		else if (logKind.equals("log") ) {
			logFileName = (String)System.getProperty("logfilename");
		}
    
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
	    
	    vecOrgData.addElement(logText);

		// ��L�ō쐬�����x�N�^�[�̓��e���ēx���O�t�@�C���ɏ�������
	    File fp  = new File ( logFileName );
	    FileOutputStream fos = new FileOutputStream (fp);
	    PrintWriter pw  = new PrintWriter (fos);
	    for (int i=0; i<vecOrgData.size(); i++ ) {
				pw.println((String)vecOrgData.elementAt(i));
	    }
	    pw.close ();
	    fos.close();
		}
		catch (IOException e) {
		}
		}
	}
	
	
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (e.getSource() instanceof JButton && command.equals("Send")) {
      // ADD COSMOS
      // �����t���[���̔z����擾����
      /*
      JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
      if (iframes.length == 0 )
        return;

      for (int i=0; i<iframes.length; i++ ) {
        if (((ps.InspectorItface)iframes[i]).isChanenged() ) {
        //if (iframes[i].getTitle().indexOf("*") != -1 ) {
          Object[] options = { "OK", "CANCEL" };

          int ret = JOptionPane.showOptionDialog(null,
              "�ҏW����Ă���t�@�C�������݂��܂��B\n�ҏW���e�������[�h���Ă�낵���ł����H", "Warning",
                                       JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                                       null, options, options[0]);

          int r = ret;

          if (ret == 1 ) {
            return;
          }
          else {
            break;
          }

        }
      }
      for (int i=0; i<iframes.length; i++ ) {
        if (((ps.InspectorItface)iframes[i]).isChanenged() ) {
        //if (iframes[i].getTitle().indexOf("*") != -1 ) {
          if (!((ps.InspectorItface)iframes[i]).reloadRule() ) {
            return;
          }

          //iframes[i].setTitle(iframes[i].getTitle().substring(0,iframes[i].getTitle().indexOf(" ")));

        }
      }
      */
      sendMessageFromACLeditor();
    }
    else if (command.equals("Open"))
        openAgentfile();
    else if (command.equals("Quit"))
      dvm.finalizeDVM();
    else if (command.equals("Kill"))
      killAgent();
    else if (command.equals("Memory"))
      printMemory();
    else if (command.equals("Threads"))
      printThreads(false);
    else if (command.equals("All threads"))
      //dumpThreads(Thread.currentThread().getThreadGroup());
      printThreads(true);
    else if (command.equals("RMI lookup"))
      printRMIlookup();
    else if (command.equals("Show"))  // ADD COSMOS
      //dvm.showViewer ( );             // ADD COSMOS
      canvas.setVisible(true);
    else if (command.equals("Close")) // ADD COSMOS
      //dvm.closeViewer ( );            // ADD COSMOS
      canvas.setVisible(false);
    else if (command.equals("Tile_Horizonal")) // ADD COSMOS
      setTile_H (0 ) ;                         // ADD COSMOS
    else if (command.equals("Tile_Vertical"))  // ADD COSMOS
      setTile_V (0 );                          // ADD COSMOS
    else if (command.equals("Cascade"))        // ADD COSMOS
      setCascade ( );                          // ADD COSMOS
    else if (command.equals("Iconize All"))        // ADD COSMOS
      setIconize ( );                          // ADD COSMOS
      //sendMessageFromACLeditor();

    /* cout
    else if (command.equals("info"))
      printAgentInfo();
    else if (command.equals("log"))
      printAgentLog();
    */
  }

  /** ACL�G�f�B�^�ɓ��͂��ꂽ���b�Z�[�W�𑗐M���� */
  public void sendMessageFromACLeditor() {
    String performative = aclPanel.getPerfField();
    String to = aclPanel.getToField();
    String content = aclPanel.getContArea();

    if (performative.equals("") || to.equals("") || content.equals("")) {
      printlnE("[�G���[]: acl-editor�ɓ��͂��Ă��Ȃ���������܂��B");
      return;
    }

    if (!content.startsWith("(") || !content.endsWith(")")) {
      printlnE("[�G���[]: acl-editor��:content�̓��͂��A\"(\"�Ŏn�܂��Ă��Ȃ����A\")\"�ŏI���Ă��܂���B");
      return;
    }

    if (to.equals(DashMessage.IF)) {
      printlnE("[�G���[]: �u_interface@�����v�Ȃ�OK�B");
      return;
    }

    createACLHist (performative,1);
    createACLHist (to,2);
    createACLHist (content,3);
    
    // add mabune
    createAllACLHist (performative, to, content);   /* ACL�G�f�B�^�̑S�Ă̗�����ۑ� */
    
    // ���[��( )����菜��(TAF�Ƃ̌݊����̂���)��2.0�ł͎�菜���Ȃ��B
    /*
    int length = content.length();
    content = content.substring(1,length-1);
    */

    // to��@���t���Ă���Ȃ�Aarrival��ݒ肷��B
    String arrival = null;
    int p = to.indexOf('@');
    int q = to.lastIndexOf('@');
    if (p>0)
      if (p==q) {
        arrival = to.substring(p+1);
        to = to.substring(0, p);
        if (arrival.equals("") || to.equals("")) {
          printlnE("[�G���[]: acl-editor�� :to�̒l���s���ł��B");
          return;
        }
      } else {
        printlnE("[�G���[]: acl-editor�� :to�̒l���s���ł�(������@)�B");
        return;
      }

    try {
      // Creater�̃L���[�ɓ����B
      dvm.sendMessageFromUser(null, performative, null, to, arrival, content, null);
    } catch (DashException e) {
      printlnE("[�G���[]: ACL�G�f�B�^�ɓ��͂���:content�̒l���s���ł��B");
    }

  }

  /** �G�[�W�F���g���I��������B
      release�̏����ɂ��ẮACM_J.finalizeInstAg()�Q�ƁI*/
  private void killAgent() {
    /*
    TreePath path = treePane.getSelectionPath();
    if (path == null) return; //�O�̂���

    String name = path.getLastPathComponent().toString();
    Object[] msg = { "Kill "+name+". OK?" };
    int ans = JOptionPane.showConfirmDialog(getContentPane(), msg, "Confirm Dialog", JOptionPane.OK_CANCEL_OPTION);

    if (ans==JOptionPane.OK_OPTION)
      dvm.stopAgent(name);
    */
  }

  /**
   * �G�[�W�F���g�L�q�t�@�C���܂��̓v���W�F�N�g�t�@�C�����J���B
   */
  private void openAgentfile() {
    JFileChooser fileChooser = new JFileChooser(defaultOpenDir);
    fileChooser.addChoosableFileFilter(new DashFileFilter());
    //int ret = fileChooser.showOpenDialog(getContentPane());
    int ret = fileChooser.showOpenDialog(this.getParent());
    File file = fileChooser.getSelectedFile();
    if (ret!=JFileChooser.APPROVE_OPTION || file==null)
      return;
    if (file.getName().toLowerCase().endsWith(".dash"))
      dvm.addLoadQueue(file);
    else if (file.getName().toLowerCase().endsWith(".prj"))
      dvm.loadProject(file);
    File cdir = fileChooser.getCurrentDirectory();
    if (cdir!=null &&
        cdir.exists() && cdir.canRead() && cdir.isDirectory())
      defaultOpenDir = cdir;
  }

  /** �t�@�C���t�B���^ */
  class DashFileFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept(File file) {
      if (file.isDirectory())
        return true;
      boolean accepted =false;
      String filename = file.getName().toLowerCase();
      if (filename.endsWith(".dash") || filename.endsWith(".prj"))
        accepted = true;
      return accepted;
    }

    public String getDescription() {
      return "Dash Files (*.dash), Project Files (*.prj)";
    }
  }


  /**
   * �v���W�F�N�g�t�@�C��(*.prj)��ǂݍ��݁A��������B
   */
  /* DVM�Ɏ����Ă�����
  public void loadProject(File file) {
    StringBuffer buffer = new StringBuffer();
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      while (br.ready()) {
        String line = br.readLine();
        if (line != null)
          buffer.append(line + "\n");
      }
    } catch (FileNotFoundException e) {
      System.err.println("�t�@�C��"+file+"������܂���");
    } catch (IOException e) {
      e.printStackTrace();
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
          dvm.addLoadQueue((File)obj);
      }
  }
  */

  /** �������Ɋւ������\������ */
  private void printMemory() {
    Runtime runtime = Runtime.getRuntime();
    long total = runtime.totalMemory();
    long free = runtime.freeMemory();
    println("[Memory]");
    println("total: "+total/1024+"Kb\n"+
            "use: "+(total - free)/1024+"Kb\n"+
            "free: "+free/1024+"Kb\n");

  }

  /** VM��̑S�ẴX���b�h��W���o�͂ɕ\������B
      http://java-house.etl.go.jp/ml/archive/j-h-b/001346.html#body���B*/
  private void dumpThreads(ThreadGroup tg) {
    ThreadGroup parent = tg.getParent();
    if (parent != null)
      dumpThreads(parent);
    else { // reached the top
      if (tg != null) {
        System.out.println("Dumping Thread");
        tg.list();
      }
    }
  }

  /** DASH�ŋN�������S�ẴX���b�h��\������ */
  private void printThreads(boolean allSystem) {
    // �܂�top���邢��main�܂ők��
    ThreadGroup root = dvm.dashThreads;
    if (allSystem)
      while (root.getParent()!=null) root = root.getParent();
    else
      while (!root.getName().equals("main")) root = root.getParent();

    //�~��čs���B
    Hashtable hash = getThreadHash(root);

    // �\��
    int i = logTabbedPane.indexOfTab("log");
	logTabbedPane.setForegroundAt(i, eventColor);
 //   logTabbedPane.setSelectedIndex(i);
    if (allSystem)
      println("[All threads]");
    else
      println("[Threads]");
    println(printThreadsHash(hash, 0));
  }

  private String printThreadsHash(Hashtable hash, int level) {
    String space = "";
    for (int i=0; i<level; i++)
      space += INDENT;

    StringBuffer buf = new StringBuffer();
    for (Enumeration e = hash.keys(); e.hasMoreElements(); ) {
      Object groupname = e.nextElement();
      buf.append(space);
      buf.append(groupname);
      buf.append(" {\n");

      Vector value = (Vector)hash.get(groupname);
      level+=1;
      for (Enumeration ee = value.elements(); ee.hasMoreElements(); ) {
        Object o = ee.nextElement();
        if (o instanceof Hashtable) {
          buf.append(printThreadsHash((Hashtable)o, level));
        } else
          buf.append(space);
          buf.append(INDENT);
          buf.append(o);
          try {
			buf.append('\n');
		} catch (Exception e1) {
			e1.printStackTrace();
		}
      }
      buf.append(space);
      buf.append("}\n");
    }
    return buf.toString();
  }

  private Hashtable getThreadHash(ThreadGroup root) {
    Vector vector = new Vector();

    // �܂��X���b�h
    Thread[] threads = new Thread[root.activeCount()];
    root.enumerate(threads, false);
    for (int i=0; i<threads.length; i++)
      if (threads[i] != null)
        vector.add(threads[i].getName()); // .getName()�������ƏڍׂɂȂ�B

    // ���ɃO���[�v
    ThreadGroup[] groups = new ThreadGroup[root.activeGroupCount()];
    root.enumerate(groups, false);
    for (int i=0; i<groups.length; i++)
      if (groups[i] != null)
        vector.add(getThreadHash(groups[i]));

    Hashtable hash = new Hashtable();
    hash.put(root.getName(), vector); // .getName()�������ƏڍׂɂȂ�B
    return hash;
  }

  /**
   * ���̃z�X�g�ŋN������Ă���RMI���W�X�g���ɓo�^����Ă��閼�O��\������B
   */
  private void printRMIlookup() {
    if (!dvm.usingRmiModule) {
      println("[RMI lookup]\nRMI is not used.");
      return;
    }

    try {
      String[] url = java.rmi.Naming.list("rmi://localhost/");
      println("[RMI lookup]");
      for (int i=0; i<url.length; i++)
        println(url[i]);
    } catch (Exception e) {
      printlnE("[RMI lookup]", e);
    }
  }

  /** ���C���X�y�N�^�̃R�}���h */
  /* cout
  private void printAgentInfo() {
    TreePath path = treePane.getSelectionPath();
    if (path != null) { // �O�̂���
      String receiver = path.getLastPathComponent().toString();
      adips97.dkset_J.CM_J cm =
        (adips97.dkset_J.CM_J)environment.agTable.get(receiver);
      if (cm == null)
        println("no such agent.\n");
      else
        println(cm.getInfo()+"\n");
    }
  }
  */

  /** ���C���X�y�N�^�̃R�}���h */
  /* cout
  private void printAgentLog() {
    TreePath path = treePane.getSelectionPath();
    if (path != null) { // �O�̂���
      String receiver = path.getLastPathComponent().toString();
      adips97.dkset_J.CM_J cm =
        (adips97.dkset_J.CM_J)environment.agTable.get(receiver);
      if (cm == null)
        println("no such agent.\n");
      else {
        String s = cm.getAllLog();
        println((s.length()>0) ? s : "no log.\n");
      }
    }
  }
  */

  /** ��M�������b�Z�[�W��receive�^�u�ɕ\������B*/
  public void putMsg(DashMessage msg) {
    final String string;
    string = msg.toString2();


    // ���M���G�[�W�F���g��
    String from =msg.from;
    // ���M��G�[�W�F���g�� 
    String to = msg.to;
    // ���b�Z�[�W�̑��M���̊����A�܂���null�B
    // null�̏ꍇ�́A���������ւ̃��b�Z�[�W�ł��邱�Ƃ�\���B
    String departure = msg.departure;
    // ���b�Z�[�W�̓�����������A�܂���null�B
    //  null�̏ꍇ�́A���������ւ̃��b�Z�[�W�ł��邱�Ƃ�\���B
    String arrival = msg.arrival;
    
    
    if (isShowing()) {
      Runnable r = new Runnable() {
          public void run() {
            receiveArea.append(string+"\n\n");
            receiveArea.setCaretPosition(receiveArea.getText().length());
          }};
      SwingUtilities.invokeLater(r);
    } else
      System.out.println(string);
  }

  /** cout
  public void notifySendMsg(Message msg) {
    String arrival = (msg.arrival == null ? "" : " at "+msg.arrival);
    println("send "+msg.performative()+
            " to "+msg.to+arrival);
  }
  */

  /////////////////////////////////////////////////////////////////
  //  ���O�֌W  ///////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////
  /**
   * ���O�����O�^�u�ɕ\������B
   * 200�s���z������A�Ō��100�s�ɂ���B
   * @param s ���O
   */
  int printnum=0;
  boolean printflag=false;
  StringBuffer sb = new StringBuffer();
  
  public synchronized void println(final String s) {
  	printnum++;
 	//JFileChooser j= new JFileChooser();
 	//j.setDialogTitle(s);
 	//j.showOpenDialog(this);
    if (!usingConsoleReplace) {
      System.out.println(s);
      return;
    }
    
    //if (isShowing()) {
      //logTabbedPane.setSelectedIndex(1);
    	logTabbedPane.setForegroundAt(1, eventColor);
      Runnable r = new Runnable() {
          public void run() {
            synchronized (logArea) {
            	writeLog (s, "log" );
			 logArea.append(s+"\n");
             //logArea.append("�u"+dvm.getDVMname()+" str="+sb.toString()+s+"�v\n");
             sb.delete(0,sb.length());
              logAreaLines++;
              String text = logArea.getText();
              if (logAreaLines > 200) {
                StringTokenizer st = new StringTokenizer(text,"\n");
                // �̂Ă�s
                /*
                int maxcnt = 100;
                if (System.getProperty("dash.logmaxcnt") != null ) {
                	maxcnt = new Integer( System.getProperty("dash.logmaxcnt") ).intValue();
                }
                int loops = st.countTokens() - maxcnt;
                */
                int loops = st.countTokens() - MAX_LOG_CNT;
                for (int i=0; i<loops; i++)
                  st.nextToken();
                // �c���s
                StringBuffer buf =new StringBuffer(text.length());
                for (logAreaLines=0; st.hasMoreTokens(); logAreaLines++) {
                  buf.append(st.nextToken());
                  try {
					buf.append('\n');
				} catch (Exception e) {
					e.printStackTrace();
				}
                }
                text = buf.toString();
                logArea.setText(text);
              }
              logArea.setCaretPosition(text.length());
            }
          }
        };
      SwingUtilities.invokeLater(r);
  }

  /**
   * �G���[���G���[�^�u�ɕ\������B
   * @param s �G���[�̐���
   */
  public void printlnE(final String s) {
    if (!usingConsoleReplace) {
      System.err.println(s);
      return;
    }

    if (isShowing()) {
      //logTabbedPane.setSelectedIndex(2);
	    logTabbedPane.setForegroundAt(2, eventColor);
      Runnable r = new Runnable() {
          public void run() {
            synchronized (errorLogArea) {
            	writeLog (s, "err" );
              errorLogArea.append(s+"\n");
              errorLogAreaLines++;
              String text = errorLogArea.getText();
              if (errorLogAreaLines > 200) {
                StringTokenizer st = new StringTokenizer(text,"\n");
                // �̂Ă�s
                /*
                int maxcnt = 100;
                if (System.getProperty("dash.errmaxcnt") != null ) {
                	maxcnt = new Integer( System.getProperty("dash.errmaxcnt") ).intValue();
                }
                int loops = st.countTokens() - maxcnt;
                */
                int loops = st.countTokens() - MAX_ERR_CNT;
                for (int i=0; i<loops; i++)
                  st.nextToken();
                // �c���s
                StringBuffer buf =new StringBuffer(text.length());
                for (errorLogAreaLines=0; st.hasMoreTokens(); errorLogAreaLines++) {
                  buf.append(st.nextToken());
                  try {
					buf.append('\n');
				} catch (Exception e) {
						e.printStackTrace();
				}
                }
                text = buf.toString();
                errorLogArea.setText(text);
              }
              errorLogArea.setCaretPosition(text.length());
            }
            // �^�u��\��������
            int i = logTabbedPane.indexOfTab("error");
      			logTabbedPane.setForegroundAt(i, eventColor);
            //logTabbedPane.setSelectedIndex(i);
          }
        };
      SwingUtilities.invokeLater(r);
    } else
      System.out.println(s);
  }

  /**
   * ��O�Ƃ��̐������G���[�^�u�ɕ\������B
   * @param s ����
   * @param e ��O
   */
  void printlnE(String s, Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    pw.flush();
    sw.flush();
    try { sw.close(); } catch (Exception ee) { ee.printStackTrace(); }
    printlnE(s+"\n"+sw.toString());
  }

  /** �؂�\���̂��߂̓����N���X */
  class DashRenderer extends JLabel implements TreeCellRenderer {
    Color bgcolor =new DefaultTreeCellRenderer().getBackgroundSelectionColor();
    Icon openedIcon, closedIcon, aclIcon;
    public DashRenderer() {
      super();
      this.setFont(null);
      this.setForeground(Color.black);
      aclIcon = getImageIcon("resources/dashAcl.gif");
      if (isRtype) {
        openedIcon = getImageIcon("resources/dashRep.gif");
        closedIcon = getImageIcon("resources/dashRepClosed.gif");
      } else {
        openedIcon = getImageIcon("resources/dashWp.gif");
        closedIcon = getImageIcon("resources/dashWpClosed.gif");
      }
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      if (hasFocus) {
        setOpaque(true);
        setBackground(bgcolor);
      } else {
        setOpaque(false);
      }
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

      if (node.getLevel()==1) {                        // ��
        if (expanded)
          this.setIcon(openedIcon);
        else
          this.setIcon(closedIcon);
      } else if (value == treePaneModel.getACLnode()) {// ACL�G�f�B�^
        this.setIcon(aclIcon);
      } else {                                         // �G�[�W�F���g
        this.setIcon(null);
      }

      this.setText(node.toString());
      return this;
    }
  }

  /** settext */
  public void settext(String agent, String s) {
    treePane.settext(agent, s);
  }

  /** Edit���j���[�Ɋւ�������N���X */
  class EditMenuListener implements MenuListener {
    Object focusedObj;

    public void menuCanceled(MenuEvent e) {}
    public void menuDeselected(MenuEvent e) {}
    public void menuSelected(MenuEvent e) {
      //Object focusedObj = ifFrame.getFocusOwner();
      focusedObj = ifFrame.getFocusOwner();
    }
  }

  /** Agent���j���[�Ɋւ�������N���X */
  class AgentMenuListener implements MenuListener {
    public void menuCanceled(MenuEvent e) {}
    public void menuDeselected(MenuEvent e) {}
    public void menuSelected(MenuEvent e) {
      Object focusedObj = ifFrame.getFocusOwner();

      boolean bool = false;
      TreePath path = treePane.getSelectionPath();
      if (focusedObj instanceof JTree && path!=null &&
          path.getPathCount()>2 &&
          !path.getLastPathComponent().equals(treePaneModel.getACLnode()))
        bool = true;
      inspectAction.setEnabled(bool);
      sendAction.setEnabled(bool);
      JMenu menu = (JMenu)e.getSource();
      Component[] comps = menu.getMenuComponents();
      for (int i=0; i<comps.length; i++)
        if (comps[i] instanceof JMenuItem)
          ((JMenuItem)comps[i]).setEnabled(bool);
    }
  }

  /** �؂̃|�b�v�A�b�v���j���[�Ɋւ�������N���X */
  class TreeSelect extends MouseAdapter {

    /** Linux�p */
    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger())
        showPopupMenu(e);
      else if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        deselect(e);
    }

    /** Windows�p */
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger())
        showPopupMenu(e);
      else if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        deselect(e);
    }

    private void deselect(final MouseEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            TreePath path = treePane.getPathForLocation(e.getX(), e.getY());
            if (path == null)
              treePane.clearSelection();
          }
        };
      SwingUtilities.invokeLater(r);
    }

    /** �C���X�y�N�^�̕\�� */
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        TreePath path = treePane.getPathForLocation(e.getX(), e.getY());

        if (path != null && path.getPathCount() > 2 &&
            !path.getLastPathComponent().equals(treePaneModel.getACLnode())) {
          String receiver = path.getLastPathComponent().toString();
          JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
          for (int i=0; i<iframes.length; i++ ) {
            if (iframes[i].getTitle().equals(receiver) ) {
              try {
                iframes[i].setIcon (false);
                iframes[i].toFront();
                iframes[i].setSelected (true );
                //iframes[i].show();
              }
              catch (Exception ex) {}
              return;
            }
           
          }


          dvm.openInspector(receiver);
        }
      }
    }

    /**
     * �|�b�v�A�b�v���j���[���J���B
     * �G�[�W�F���g���̏�ŊJ�����ꍇ�A���̃G�[�W�F���g��I�����Ă���J���B
     */
    private void showPopupMenu(MouseEvent e) {
      JTree tree = (JTree)e.getComponent();
      final TreePath path = tree.getPathForLocation(e.getX(), e.getY());
      if (path != null) {
        Runnable r = new Runnable() {
            public void run() {
              treePane.setSelectionPath(path);
            }};
        SwingUtilities.invokeLater(r);
      }
      if (path != null || treePane.getSelectionPath() != null) {
        TreePath p = (path!=null) ? path : treePane.getSelectionPath();
        boolean bool = p.getPathCount() > 2 && !p.getLastPathComponent().equals(treePaneModel.getACLnode());
        inspectAction.setEnabled(bool);
        sendAction.setEnabled(bool);
        copyAction.setEnabled(true);
      } else {
        inspectAction.setEnabled(false);
        sendAction.setEnabled(false);
        copyAction.setEnabled(false);
      }
      treePopupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

  }

  /** Send�A�N�V���� */
  class SendAction extends AbstractAction {
    public SendAction(String label, Icon icon) { super(label, icon); }
    /** ACL�G�f�B�^���J�� */
    public void actionPerformed(ActionEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            TreePath path = treePane.getSelectionPath();
            if (path != null) { // �O�̂���
              String receiver = path.getLastPathComponent().toString();
              int i = logTabbedPane.indexOfTab("acl-editor");
              //logTabbedPane.setSelectedIndex(i);
			  logTabbedPane.setForegroundAt(i, eventColor);
   
              aclPanel.setToField(receiver);
            }
          }};
      SwingUtilities.invokeLater(r);
    }
  }

  /** Inspect�A�N�V���� */
  class InspectAction extends AbstractAction {
    public InspectAction(String label, Icon icon) { super(label, icon); }
    //�C���X�y�N�^���J��
    public void actionPerformed(ActionEvent e) {
      TreePath path = treePane.getSelectionPath();
      if (path != null) { // �O�̂���
        String receiver = path.getLastPathComponent().toString();
        dvm.openInspector(receiver);
      }
    }
  }


  /** Copy�A�N�V���� */
  class CopyAction extends AbstractAction {
    public CopyAction(String label, Icon icon) { super(label, icon); }

    /**
     * �V�X�e���N���b�v�{�[�h�ɃR�s�[����B
     * JTree���ΏۂȂ�A�I�����ꂽ�G�[�W�F���g�����R�s�[����B
     * JTextComponent���ΏۂȂ�A�e�L�X�g���R�s�[����B
     */
    public void actionPerformed(ActionEvent e) {
    
      Runnable r = new Runnable() {
          public void run() {
        	  TreePath path = treePane.getSelectionPath();
              if (path != null) { 
                String agentname = path.getLastPathComponent().toString();
                StringSelection contents = new StringSelection(agentname);
                Clipboard clip =
                  Toolkit.getDefaultToolkit().getSystemClipboard();
                clip.setContents(contents, null);
              }
          }
        };
      SwingUtilities.invokeLater(r);
    }
  }

  /**
   * Message���j���[��I�������Ƃ��ɌĂяo�����ActionListener
   */
  class MessageAction implements ActionListener {

    /**
     * key   = ���j���[�A�C�e��.�T�u���j���[�A�C�e���B
     * value = ���b�Z�[�W
     */
    private Hashtable messageHash = new Hashtable();

    void putHash(String command, Hashtable hash) {
      messageHash.put(command, hash);
    }

    public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();
      Hashtable hash = (Hashtable)messageHash.get(command);

      int i = logTabbedPane.indexOfTab("acl-editor");
      //logTabbedPane.setSelectedIndex(i);
	  logTabbedPane.setForegroundAt(i, eventColor);
   
      String performative = (String)hash.get("performative");
      String to = (String)hash.get("to");
      String arrival = (String)hash.get("arrival");
      String content = (String)hash.get("content");

      if (arrival != null)
        to = to + "@" + arrival;
      aclPanel.setPerfField(performative);
      aclPanel.setToField(to);
      aclPanel.setContArea(content);
    }
  }



  /** �`�F�b�N�{�b�N�X/�{�^���p */
  class CheckBoxListener implements ItemListener, ActionListener {
    public void itemStateChanged(ItemEvent evt) {
      synchronized (stepButton) {
        if (evt.getStateChange() == ItemEvent.SELECTED)
          stepButton.notify();
      }
    }
    public void actionPerformed(ActionEvent evt) {
      synchronized (stepButton) {
        stepButton.notify();
      }
    }
  }

  /** �X�e�b�v�{�^�����������̂�҂� **/
  void waitStepButton() {
    if (!nonStopCheck.isSelected()) {
      synchronized (stepButton) {
        stepButton.setEnabled(true);
        try { stepButton.wait(); } catch (InterruptedException e) { }
        stepButton.setEnabled(false);
      }
    } else
      //try { Thread.sleep(200); } catch (InterruptedException e) { }
      try { Thread.sleep(waittimeForNonstop); } catch (InterruptedException e) { }
  }

  /////////////////////////////////////////////////////////////////
  //  NoConsole�֌W  //////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////

  /**
   * �v���p�e�Bdash.noconsole��on�̏ꍇ�A
   * �W���o�͂�all�^�u�ɁA�W���G���[�o�͂�error�^�u�ɐؑւ���B
   * Repository.main()����Ăяo���ƁA
   * main()�����s���Ă����X���b�h�����ł����Ƃ��Ƀp�C�v������B
   */
  public void replaceConsole() {
  	//uchiya
    //if (consoleReplace != null) {
    // 	return;} 

    
    if (System.getProperty("JavaCompile").equals("on") ) {
      return;
    }
    //if (System.getProperty("JavaCompile").equals("off") ) {
    //  return;
    //}
    
    //change uchiya
    if (consoleReplace != null) {consoleReplace.replace(this);
		return;} 


    String sw = System.getProperty("dash.noconsole");
    if (sw.equalsIgnoreCase("on")) {
      consoleReplace = new ConsoleReplace(this);
      usingConsoleReplace = true;
    }
  }

  private class ConsoleReplace {

    /** System.setOut(), System.setErr()����Stream */
    private PrintStream stdoutPrintStream, stderrPrintStream;

    
    /** System.{out|err}.println()�����o�C�g����i�[����Stream*/
    ByteArrayOutputStream stdoutStream, stderrStream;

    int num=0;
    /** �R���X�g���N�^ */
    ConsoleReplace(NewIf2 if2) {
      stdoutStream = new ByteArrayOutputStream();
      stdoutPrintStream = new PrintStream(stdoutStream);
      System.setOut(stdoutPrintStream);
      //System.setOut(System.out);
    
      stderrStream = new ByteArrayOutputStream();
      stderrPrintStream = new PrintStream(stderrStream);
      System.setErr(stderrPrintStream);
	  targetif= if2;
	 num++;
	 //System.err.println("replaceConsole�̃R���X�g���N�^�N����"+num);
    }
    
    NewIf2 targetif = null;
    
    void replace(NewIf2 if2) {
     //System.setOut(stdoutPrintStream);
      //System.setErr(stderrPrintStream);
      if(targetif==if2) return;
      
      	synchronized(stdoutPrintStream){
      	//System.out.println("��if= "+targetif.getDvmName()+" �Vif="+if2.getDvmName());
		//System.out.println("replace.print");
		//print();
		//System.out.println(stdoutStream.size());
   	    targetif= if2;
      	}
    }
   public  int a=0;
   public  int b=0;
   public  int c=0;
   public int d=0;
   public int e=0;
   public int f=0;
   String s1="",s2="",s3=""; 
    /** �\�� */
    synchronized void print() {
		//targetif.println("�O��̏��");
		//targetif.println("a="+a+" b="+b+" s1="+s1);		//targetif.println("c="+c+" d="+d+" s2="+s2);
		//targetif.println("e="+e+" f="+f+" s3="+s3);
      synchronized (stdoutPrintStream) {
		 if (stdoutStream.size()>0) {
		 	//s1="1"+stdoutStream.toString()+"1";
		     //a=stdoutStream.size();
			 //b=stdoutStream.toString().trim().length();
        	synchronized (targetif){
        	 targetif.println(stdoutStream.toString());
			 //s2="2"+stdoutStream.toString()+"2"; 
        	 //c=stdoutStream.size();
			 //d=stdoutStream.toString().trim().length();
        	
        	// if(a!=b){
        	// 	System.setOut(System.out);
        	// 	System.out.println("a="+a+" b="+b+" c="+c);
        	 	//System.exit(0);
        	 //}
             stdoutStream.reset();
			 //s3="3"+stdoutStream.toString()+"3"; 
		     //e=stdoutStream.size();
			 //f=stdoutStream.toString().trim().length();
        	
        	}
        }
      }
      synchronized (stderrPrintStream) {
      	//uchiya test
		 if (stderrStream.size()>0) {
        	synchronized (targetif){
				
          targetif.printlnE(stderrStream.toString());
		  
          stderrStream.reset();
        	}
        }
      }
    }

  }

  /****************************************************************************/
  /****************************************************************************/
  /** �ȉ��A�R�X���X�ǉ���                                                       */
  /****************************************************************************/
  /****************************************************************************/
  public void addChildWindow (JInternalFrame iframe ){
    //CodeEditorDesktop = simulatorPanel.inspectorDesktop;

    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    for (int i=0; i<iframes.length; i++ ) {
      if (iframes[i].getTitle().equals(iframe.getTitle())){
        iframes[i].setFocusable(true);
        iframes[i].toFront();
        try {
          iframes[i].setSelected(true);
        }
        catch (Exception e) {}
        return;
      }
    }
    iframe.setResizable(true);
    iframe.setMaximizable(true);
    iframe.setClosable(true);
    iframe.setIconifiable(true);
    Dimension desktopSize = CodeEditorDesktop.getSize();

    /*
    if (System.getProperty("dash.r_w.pos") == null) {
      System.setProperty("dash.r_w.pos","fixed");
    }

    if (System.getProperty("dash.r_w.pos").equals("fixed")) {
      iframe.setSize (CodeEditorDesktop.getWidth(), CodeEditorDesktop.getHeight());
      iframe.setLocation (0, 0);
    }
    else {
      iframe.setSize (CodeEditorDesktop.getWidth()/2, CodeEditorDesktop.getHeight()*3/4);
      iframe.setLocation ((CodeEditorDesktop.getWidth()/2)-20, 30);
    }
    */

    iframe.setSize (CodeEditorDesktop.getWidth(), CodeEditorDesktop.getHeight());
    iframe.setLocation (0, 0);

    try {
      //iframe.setMaximum (true );
    }
    catch (Exception e) {}
    CodeEditorDesktop.add (iframe );
    iframe.setVisible (true );
    CodeEditorDesktop.setSelectedFrame (iframe );
    iframe.revalidate();
    CodeEditorDesktop.revalidate();
    try {
      iframe.setSelected(true);
    }
    catch (Exception e) {}

    if (isRtype ) {
      iframe.setToolTipText("Repository");
    }
    else {
      iframe.setToolTipText("Workplace");
    }
    CodeEditorDesktop.repaint();
    iframe.show();
    iframe.toFront();
    //iframe.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    iframe.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
      public void internalFrameClosing(InternalFrameEvent e) {
        this_internalFrameClosing(e);
      }
      public void internalFrameDeiconified(InternalFrameEvent e) {
        this_internalFrameDeiconified(e);
      }
    });
    //updateWindowMenu ( );
  }
  void this_internalFrameClosing(InternalFrameEvent e) {
    //JOptionPane.showMessageDialog(null,"AA" );
    e.getInternalFrame().setVisible(false);
    updateWindowMenu ( );
  }
  void this_internalFrameDeiconified(InternalFrameEvent e) {
    updateWindowMenu ( );
  }

  //***************************************************************************
  // Window�������j���[
  //***************************************************************************
  // �����^�C�����\�b�h
  void setTile_H (int optionitems) {
    // �����t���[���̔z����擾����
    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;

    int itemcount = 0;
    // �A�C�R��������Ă��Ȃ������t���[�����𒲂ׂ�
    for (int i=0; i<iframes.length; i++ ) {
      if (!iframes[i].isIcon() ) {
        itemcount++;
      }
    }

    // �f�X�N�g�b�v�̓������E��`�����߂�
    Insets is = CodeEditorDesktop.getInsets();
    // �f�X�N�g�b�v�̓����������߂�
    int width = CodeEditorDesktop.getWidth() - is.left - is.right;
    // �f�X�N�g�b�v�̓����������߂�
    int height = CodeEditorDesktop.getHeight() - is.top - is.bottom;

    // �K�v�ȃ^�C���������߂�
    // 1.�]�肪�o�Ȃ��P�[�X�ł́A�u�������~�������v�Ƃ���
    // 2.�]�肪�o��P�[�X�ł́A�u�������~�i�������{�Q�j�v�Ƃ���
    int hitems, vitems, excess;

    // optoinitems�I�v�V�������g�����i���݂�����P�[�X
    if (optionitems != 0 && optionitems <= itemcount ) {
      itemcount = optionitems;
    }

    int tempi = (int)Math.sqrt(itemcount);
    // �c��̃A�C�e�������Z�o����
    excess = itemcount - (int)Math.pow(tempi,2);
    // �]��̏o�Ȃ��P�[�X
    if (excess == 0 ) {
      hitems = vitems = tempi;
    }
    else { // �]��̏o��P�[�X
      hitems = tempi;
      vitems = tempi + 2; // �v���X�Q�͗]��̏o�Ȃ��������̗ݏ�l�|�P���Ӗ�����
      // �]���ȍs���Ȃ���
      if (itemcount <= hitems * (vitems-1) ) {
        vitems--;
      }
    }

    // �^�C���T�C�Y�����߂�
    int itemwidth = width / hitems;
    int itemheight = height / vitems;
    // �^�C�����E�ցA���ւƃ��C�A�E�g����
    for (int counter = 0, rows = 0; rows < vitems && counter < itemcount; rows++ ) {
      // ������̃��[�v����ɉ��
      for (int cols = 0; cols < hitems && counter < itemcount; cols++ ) {
        iframes[counter++].reshape(is.left + itemwidth * cols,
          is.top + itemheight * rows, itemwidth, itemheight );
      }
    }
    updateWindowMenu ( );

  }

  // �����^�C���\�����\�b�h
  void setTile_V (int optionitems ) {
    // �����t���[���̔z����擾����
    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;

    int itemcount = 0;
    // �A�C�R��������Ă��Ȃ������t���[�����𒲂ׂ�
    for (int i=0; i<iframes.length; i++ ) {
      if (!iframes[i].isIcon() )
        itemcount++;
    }

    // �f�X�N�g�b�v�̓������E��`�����߂�
    Insets is = CodeEditorDesktop.getInsets();
    // �f�X�N�g�b�v�̓����������߂�
    int width = CodeEditorDesktop.getWidth() - is.left - is.right;
    // �f�X�N�g�b�v�̓����������߂�
    int height = CodeEditorDesktop.getHeight() - is.top - is.bottom;

    // �K�v�ȃ^�C���������߂�
    // 1.�]��̏o�Ȃ��P�[�X�ł́A�u�������~�������v�Ƃ���
    // 2.�]��̏o��P�[�X�ł́A�u�������~�i�������{�Q�j�v�Ƃ���
    int hitems, vitems, excess;

    // optionitem�I�v�V�������g�����i���݂�����P�[�X
    if (optionitems != 0 && optionitems < itemcount )
      itemcount = optionitems;

    int tempi = (int)Math.sqrt (itemcount );
    // �c��̃A�C�e�������Z�o����
    excess = itemcount - (int)Math.pow(tempi, 2 );
    // �]��̏o�Ȃ��P�[�X
    if (excess == 0 ) {
      hitems = vitems = tempi;
    }
    else { // �]��̏o��P�[�X
      vitems = tempi;
      hitems = tempi + 2; // �v���X�Q�͗]��̏o�Ȃ��������̗ݏ�l�|�P���Ӗ�����
      // �]���ȍs���Ȃ���
      if (itemcount <= vitems *(hitems-1) ) {
        hitems--;
      }
    }

    // �^�C���T�C�Y�����߂�
    int itemwidth = width / hitems;
    int itemheight = height / vitems;
    // �^�C�������ցA�E�ւƃ��C�A�E�g����
    for (int counter = 0, cols=0; cols<hitems && counter < itemcount; cols++ ) {
      // ������̃��[�v����ɉ��
      for (int rows=0; rows<vitems && counter < itemcount; rows++ ) {
        iframes[counter++].reshape(is.left + itemwidth * cols,
           is.top + itemheight * rows, itemwidth, itemheight );
      }
    }
    updateWindowMenu ( );
  }

  // �J�X�P�[�h�\�����\�b�h
  void setCascade ( ) {
    // �����t���[���̔z����擾����
    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;
    // ���݂�xnew��ynew�̒l��ۑ����Ă���
    int savexnew = xnew, saveynew = ynew;
    xnew = xincrement; ynew = yincrement;
    // �t���ɕ\������
    for (int i=iframes.length-1; i>=0; i--  ) {
      if (!iframes[i].isIcon() ) {
        iframes[i].setSize(newwidth, newheight );
        iframes[i].setLocation (xnew, ynew );
        updateLocation (iframes[i] );
      }
    }

    // xnew��ynew�̒l�𕜌�����
    xnew = savexnew; ynew = saveynew;
    updateWindowMenu ( );
  }

  void updateLocation (JInternalFrame frame ) {
    // �f�X�N�g�b�v�̓������E��`�����߂�
    Insets is = CodeEditorDesktop.getInsets();
    // �f�X�N�g�b�v�̓����������߂�
    int width = CodeEditorDesktop.getWidth() - is.left - is.right;
    // �f�X�N�g�b�v�̓����������߂�
    int height = CodeEditorDesktop.getHeight() - is.top - is.bottom;

    if (width == 0 ) {
      width = 470;
      height = 330;
    }

    // ����΁A�����ʒu�ɖ߂�
    if (frame.getX() + newwidth + xincrement > width ||
        frame.getY() + newheight + yincrement > height ) {
      xnew = xincrement; ynew = yincrement;
    }
    else {
      xnew += xincrement; ynew += yincrement;
    }
    updateWindowMenu ( );
  }

  // �A�C�R����
  private void setIconize ( ) {
    /*
    JMenuBar menubar = this.getJMenuBar();
    //JOptionPane.showMessageDialog(null, new Integer(menubar.getMenuCount()).toString());
    JMenu menu = menubar.getMenu(5);
    menu.getMenuComponent(0).setEnabled(false);
    menu.getMenuComponent(1).setEnabled(false);
    menu.getMenuComponent(2).setEnabled(false);
    */
    // �����t���[���̔z����擾����
    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;

    for (int i=0; i<iframes.length; i++ ) {
      // �����t���[�����A�C�R��������
      try {
        iframes[i].setIcon (true);
      }
      catch (Exception e ) {}
     }
     updateWindowMenu ( );
  }

  // Window���j���[�X�V
  private void updateWindowMenu (){
    /*
    JMenuBar menubar = this.getJMenuBar();
    JMenu menu = menubar.getMenu(5);
    menu.getMenuComponent(0).setEnabled(false);
    menu.getMenuComponent(1).setEnabled(false);
    menu.getMenuComponent(2).setEnabled(false);
    menu.getMenuComponent(3).setEnabled(false);

    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 ) {
      return;
    }

    for (int i=0; i<iframes.length; i++ ) {
      if (!iframes[i].isIcon() && iframes[i].isVisible() ) {
        menu.getMenuComponent(0).setEnabled(true);
        menu.getMenuComponent(1).setEnabled(true);
        menu.getMenuComponent(2).setEnabled(true);
        menu.getMenuComponent(3).setEnabled(true);
      }
    }


    */
  }


  // �t�@�C���V�K�쐬�A�N�V����
  class actNewFile extends AbstractAction {
    // �R���X�g���N�^
    actNewFile () { super ("",getImageIcon ("resources/new.gif")); }
    public void actionPerformed (ActionEvent e ) {
      JOptionPane.showMessageDialog(null, "���݁A���̋@�\�͎g�p�ł��܂���B\r�쐬���ł�...");
    }
  }

  // �t�@�C���I�[�v���A�N�V����
  class actOpen extends AbstractAction {
    // �R���X�g���N�^
    actOpen () { super ("",getImageIcon ("resources/open.gif")); }
    public void actionPerformed (ActionEvent e ) {
      openAgentfile();

      /*
      JFileChooser fileChooser = new JFileChooser(defaultOpenDir);
      fileChooser.addChoosableFileFilter(new DashFileFilter());
      int ret = fileChooser.showOpenDialog(getContentPane());
      File file = fileChooser.getSelectedFile();
      if (ret!=JFileChooser.APPROVE_OPTION || file==null)
        return;
      if (file.getName().toLowerCase().endsWith(".dash"))
        System.out.println (file.getName());
        //dvm.addLoadQueue(file);
      else if (file.getName().toLowerCase().endsWith(".prj")) {
        System.out.println (file.getName());
        //loadProject(file);
      }
      */

    }
  }

  public void initialize(){
    //canvas.initialize();
  }
  void jRadioButton_actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    canvas.changeItem(command);
  }


  class TreeSelect2 extends MouseAdapter {

    /** Linux�p */
    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger())
        JOptionPane.showMessageDialog(null, "mousePressed");
      else if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        deselect (e );
    }

    /** Windows�p */
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger())
        JOptionPane.showMessageDialog(null, "mouseReleased");
      else if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        deselect (e );
    }

    private void deselect(final MouseEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            TreePath path = projectTree2.getPathForLocation(e.getX(), e.getY());
            if (path == null)
              projectTree2.clearSelection();
          }
        };
      SwingUtilities.invokeLater(r);
    }


    /** �C���X�y�N�^�̕\�� */
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        TreePath path = projectTree2.getPathForLocation(e.getX(), e.getY());

        String receiver = path.getLastPathComponent().toString();

        //JOptionPane.showMessageDialog(null, receiver);
        //myThread thread = new myThread();
        //thread.setDesktopPane(CodeEditorDesktop2);
        //thread.start();
        JInternalFrame iframe = new JInternalFrame(receiver);
        Container con = iframe.getContentPane(); //�t���[���̕\���̈���Ƃ��Ă���
        Editor edit = new Editor(con);
        edit.FileRead("C:\\dash-1.9.7h\\scripts\\cnp\\" + receiver);
        iframe.setResizable(true);
        iframe.setMaximizable(true);
        iframe.setClosable(true);
        iframe.setIconifiable(true);
        Dimension desktopSize = CodeEditorDesktop2.getSize();
        iframe.setSize (CodeEditorDesktop2.getWidth(), CodeEditorDesktop2.getHeight());
        iframe.setLocation (0, 0);
        CodeEditorDesktop2.add (iframe );
        iframe.setVisible (true );
        CodeEditorDesktop2.setSelectedFrame (iframe );

        File file = new File("C:\\dash-1.9.7h\\scripts\\cnp\\" + receiver);
        dvm.addLoadQueue(file);
      }
    }
  }

  public AclPanel getAclPanel() {
    return aclPanel;
  }
  public JPanel getThis(){
    return this;
  }
  public void setInspectorDesktopPane (JDesktopPane desktopPane ) {
    CodeEditorDesktop = desktopPane;
  }
  public String getDvmName(){
    return dvmname;
  }
  public DVM getDVM(){
    return dvm;
  }

  public void setViewerCanvasW2 (ViewerCanvasW2 canvas ) {
    canvasW = canvas;
  }
  public void setViewerCanvasR2 (ViewerCanvasR2 canvas ) {
    canvasR = canvas;
  }

  public Vector getAllAgentName(){
    Vector vecAgentName = new Vector();
    for (Enumeration e = treePane.getAllnodes(); e.hasMoreElements(); ) {
      vecAgentName.add((String)e.nextElement());
    }
    return vecAgentName;
  }

  public void removeAgentAll ()  {
    Vector vecAgentName = new Vector();
    for (Enumeration e = treePane.getAllnodes(); e.hasMoreElements(); ) {
      vecAgentName.add((String)e.nextElement());
      //this.removeAgent((String)e.nextElement());
      //JOptionPane.showMessageDialog(null, (String)e.nextElement());
    }

    for (int i=0; i<vecAgentName.size(); i++ ) {
      String name = (String)vecAgentName.elementAt(i);
      //add uchiya  
	  //dvm.sendMessageFromUser(null, DashMessage.KILLFORCE,
		//						  null, name, null, null, null);
	// System.out.println(name+ "�G�[�W�F���g��kill���b�Z�[�W���M�I��");
     ((DashAgent)dvm.agTable.get(name)).killForce();
     //dvm.killAgent(name,null);

      // �����B
      DefaultMutableTreeNode parent = treePaneModel.removeAgentnode(name);
      treePane.removeEntry(name);

      DefaultTreeModel treeModel = (DefaultTreeModel)treePane.getModel();
      treeModel.nodeStructureChanged(treePaneModel.getRootnode());

      // ������B
      TreeNode nodes[] = treeModel.getPathToRoot(parent);
      TreePath path = new TreePath(nodes);
      treePane.expandPath(path);

      DashAgent ag  = (DashAgent)dvm.agTable.remove(name);
      
	//  if (ag.baseProcess != null ) {
    //ag.baseProcess.finalizeBP();
    //  ag.baseProcess=null;
    //}
	   
      dvm.comInt.unregisterAgent(name,dvm.getDVMname());
	  ag=null;
      //DashAgent ag = (DashAgent)dvm.agTable.remove(name);
      //ag.prodsys.inspector.dispose();
      //String s = ag.getName();
      //ag = null;
      //System.gc();
      //treePaneModel.nextTurn(key);
   
	}
    dvm.agTable.clear();

    dvm.agTable = new Hashtable();
    //treePane.removeAgentAll();
     }

  public void clearLog() {
    errorLogArea.setText("");
    logArea.setText("");
    receiveArea.setText("");
    msgArea.setText("");
    errorLogAreaLines = 0;
    logAreaLines = 0;
    aclPanel.setContArea("()");
    aclPanel.setPerfField("");
    aclPanel.setToField("");
  }
  private void createACLHist(String inputStr, int kind) {
    String dirpath = "";
    if (!dashdir.toString().endsWith(File.separator)) {
        dirpath = dashdir.toString() + File.separator;
    }
    String FilePath = dirpath;
    if (kind == 1 ) {
      FilePath += "properties" + File.separator +"perfomativeHist";
    }
    else if (kind == 2 ) {
      FilePath += "properties" + File.separator +"toHist";
    }
    else if (kind == 3 ) {
      FilePath += "properties" + File.separator +"contentHist";
    }
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    //�ǂݍ��ݏ���
    Vector vecList = new Vector();
    String content = "";
    try {
        f_in = new FileReader(FilePath);
        b_in = new BufferedReader(f_in);
        while((sLine = b_in.readLine()) != null) {

          if (kind != 3 ) {
            if (vecList.size()<30 ) {
              vecList.addElement(sLine);
            }
          }
          else {
            if (sLine.equals("----------")) {
              vecList.addElement(content);
              content = "";
            }
            else {
              if (content.equals("") ) {
                content += sLine;
              }
              else {
                content += '\n' + sLine;
              }
            }
          }
        }
        if (kind == 3 ) {
          if (!content.equals("")) {
            vecList.addElement(content);
          }

        }
        b_in.close();
        f_in.close();

    } catch(Exception ex) {
    }

    if (vecList.indexOf(inputStr) != -1 ) {
      vecList.remove(vecList.indexOf(inputStr));
    }

    try {
      File fp  = new File ( FilePath );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.println(inputStr);
      if (kind == 3 ) {
        pw.println("----------");
      }
      for (int i=0; i<vecList.size(); i++ ) {
        if (i > 30 ) {
          break;
        }
        pw.println((String)vecList.elementAt(i));
        if (kind == 3 ) {
          pw.println("----------");
        }
      }
      pw.close ();
    } catch ( Exception e ){}

  }
  
  // add mabune
  private void createAllACLHist(String performative, String to, String content){
    String dirpath = "";
    if (!dashdir.toString().endsWith(File.separator)) {
        dirpath = dashdir.toString() + File.separator;
    }
    String filePath = dirpath;
    
    filePath += "properties" + File.separator + "aclHist";
       
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    
    
    //�ǂݍ��ݏ���
    Vector vecList = new Vector();
    String strContent = "";
    try {
        f_in = new FileReader(filePath);
        b_in = new BufferedReader(f_in);
        
        while((sLine = b_in.readLine()) != null) {
        	while(!(sLine.equals("##########"))){
        		if(sLine.equals("----------")){
        			vecList.addElement(strContent);	
        			strContent = "";
        		}
        		else{
        			if(strContent.equals("")){
        				strContent = sLine;
        			}
        			else{
        				strContent += '\n' + sLine;
        			}
        		}        		
        		sLine = b_in.readLine();
        	}        	
        }
        b_in.close();
        f_in.close();

    } catch(Exception ex) {
    }
    
    //�������ݏ���
    try {
    	File fp = new File(filePath);
    	FileOutputStream fos = new FileOutputStream(fp);
    	PrintWriter pw = new PrintWriter(fos);
    	pw.println(performative);
    	pw.println("----------");
    	pw.println(to);
    	pw.println("----------");
    	pw.println(content);
    	pw.println("----------");
    	pw.println("##########");
    	for(int i=0; i<vecList.size(); i++){
    		if(i > 89) {
    			break;
    		}
    		pw.println((String)vecList.elementAt(i));
    		pw.println("----------");
    		if(i%3==2){
    			pw.println("##########");
    		}
    	}
    	pw.close();
    }
    catch (Exception e){}     
  } 
  private class TabCheck implements ChangeListener{
	   public void stateChanged(ChangeEvent e){
		   int i = logTabbedPane.getSelectedIndex();
		   logTabbedPane.setForegroundAt(i, Color.BLACK);
	   }
   }
}