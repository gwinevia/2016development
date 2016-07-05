package dash;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import editortools.*;

public class Newif extends JFrame implements ActionListener, NewifItface {

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
  private AclPanel aclPanel;
  //public AclPanel aclPanel;

  /** ACL�G�f�B�^�ւ̃��b�Z�[�W��\�����镔�i */
  private JTextArea receiveArea;

  /** ���b�Z�[�W�̓��e��\�����镔�i */
  private JTextArea msgArea;

  /** �^�u */
  private JTabbedPane logTabbedPane;

  /** �G�[�W�F���g��\������ */
  private DashTree treePane;
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
  private CutAction cutAction = new CutAction("Cut", null);
  private CopyAction copyAction = new CopyAction("Copy", null);
  private PasteAction pasteAction = new PasteAction("Paste", null);

  /** �������\�� */
  private JLabel memoryLabel;

  /** �X�e�b�v�{�^���Ȃ� */
  private JCheckBox nonStopCheck;
  private JButton stepButton;

  /** �C���f���g�p�X�y�[�X */
  private static String INDENT = "    ";

  /** �u��������I�u�W�F�N�g�Boff�̏ꍇ��null�B*/
  private static ConsoleReplace consoleReplace = null;

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

  public ProjectTree projectTree1 = null;
  public ProjectTree projectTree2 = null;
  public ProjectTree projectTree3 = null;
  public ProjectTree projectTree4 = null;
  private SearchPanel searchPanel = null;

  private Simulator simulatorPanel = null;
  private SavePanel savePanel = null;
  /** �R���X�g���N�^ */
  Newif(String dvmname, DVM dvmparam, File msgfile, File dashdir) {
    super(dvmname);
    this.dvmname = dvmname;
    this.dvm = dvmparam;
    isRtype = dvm.isRtype();
    ifFrame = this;

    String dirnames = System.getProperty("dash.loadpath");
    StringTokenizer st = new StringTokenizer(dirnames, File.pathSeparator);
    String dirname = st.nextToken();
    File file = new File(dirname);
    if (file.exists() && file.canRead() && file.isDirectory())
      defaultOpenDir = file;
    else
      defaultOpenDir = dashdir;

    // Tree�y�C��
    treePaneModel = new DashTreeModel(dvmname);
    treePane = new DashTree(this, treePaneModel);
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

    // log�^�u
    logArea = new JTextArea(""); //(dvmname="+dvmname+")\n");
    logArea.setLineWrap(true);
    logScrollPane = new JScrollPane(logArea);
    logAreaLines = 0;

    // error�^�u
    errorLogArea = new JTextArea();
    errorLogArea.setLineWrap(true);
    errorScrollPane = new JScrollPane(errorLogArea);
    errorLogAreaLines = 0;

    // acl-editor�^�u
    String options[] = { "Send" };
    aclPanel = new AclPanel(options, null, this, null);
    aclPanel.setContArea("()");
    JScrollPane aclScrollPane = new JScrollPane(aclPanel);

    // receive�^�u
    receiveArea = new JTextArea();
    receiveArea.setLineWrap(true);
    JScrollPane receiveScrollPane = new JScrollPane(receiveArea);

    // msg�^�u
    msgArea = new JTextArea();
    msgArea.setLineWrap(true);
    JScrollPane msgScrollPane = new JScrollPane(msgArea);

    // �^�u����t��
    logTabbedPane = new JTabbedPane(JTabbedPane.TOP);
    logTabbedPane.addTab("log", logScrollPane);
    logTabbedPane.addTab("error", errorScrollPane);
    logTabbedPane.addTab("acl-editor", aclScrollPane);
    logTabbedPane.addTab("receive", receiveScrollPane);
    logTabbedPane.addTab("msg", msgScrollPane);

    // ���j���[�o�[
    JMenuBar menubar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(menuItem("Open"));
    fileMenu.add(menuItem("Quit"));
    menubar.add(fileMenu);
    JMenu editMenu = new JMenu("Edit");
    editMenu.add(cutAction);
    editMenu.add(copyAction);
    editMenu.add(pasteAction);
    editMenuListener = new EditMenuListener();
    editMenu.addMenuListener(editMenuListener);
    menubar.add(editMenu);
    JMenu agentMenu = new JMenu("Agent");
    agentMenu.add(inspectAction);
    agentMenu.add(sendAction);
    agentMenu.addSeparator();
    agentMenu.add(menuItem("Kill"));
    agentMenu.addSeparator();
    agentMenu.add(menuItem("info"));
    agentMenu.add(menuItem("log"));
    agentMenu.addMenuListener(new AgentMenuListener());
    menubar.add(agentMenu);
    JMenu sysMenu = new JMenu("System");
    sysMenu.add(menuItem("Memory"));
    sysMenu.addSeparator();
    sysMenu.add(menuItem("Threads"));
    sysMenu.add(menuItem("All threads"));
    sysMenu.add(menuItem("RMI lookup"));
    menubar.add(sysMenu);

    // ADD COSMOS
    JMenu viewerMenu = new JMenu("Viewer");
    viewerMenu.add(menuItem("Show"));
    viewerMenu.add(menuItem("Close"));
    menubar.add(viewerMenu);
    /**************************************************************************/
    /* revised by cosmos 2003/01/22                                           */
    /* �C�����e�FWindow���j���[�iWindow�̐��񓙂̏����j                            */
    /**************************************************************************/
    JMenu windowMenu = new JMenu("Window");
    windowMenu.add(menuItem("Tile_Horizonal"));
    windowMenu.add(menuItem("Tile_Vertical"));
    windowMenu.add(menuItem("Cascade"));
    windowMenu.add(menuItem("Iconize All"));
    menubar.add(windowMenu);
    /**************************************************************************/
    /* revised by cosmos 2003/01/22                                           */
    /* �C�����e�FWindow���j���[�iWindow�̐��񓙂̏����j�����܂�                     */
    /**************************************************************************/

    /* cout
    if (msgfile != null)
      menubar.add(makeMessageMenu(msgfile));
    */
    setJMenuBar(menubar);

    // �c�[���o�[
    JToolBar toolbar = new JToolBar();

    /**************************************************************************/
    /* revised by cosmos 2003/01/18                                           */
    /* �C�����e�F�t�@�C���I�[�v���E�V�K�쐬�̃{�^����ǉ�                            */
    /**************************************************************************/
    JButton newfileBtn = new JButton (new actNewFile());
    JButton openfileBtn = new JButton (new actOpen());
    toolbar.add(newfileBtn);
    toolbar.add(openfileBtn);
    /**************************************************************************/
    /* revised by cosmos 2003/01/18�@�����܂�                                  */
    /**************************************************************************/

    nonStopCheck = new JCheckBox(getImageIcon("resources/pause.gif"));
    nonStopCheck.setSelectedIcon(getImageIcon("resources/nonstop.gif"));
    nonStopCheck.setSelected(false); // �X�e�b�v����
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

    CheckBoxListener listener = new CheckBoxListener();
    nonStopCheck.addItemListener(listener);
    stepButton.addActionListener(listener);

    memoryLabel = new JLabel("? / ? Kb");

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(treeScrollPane, BorderLayout.CENTER);
    panel.add(memoryLabel, BorderLayout.SOUTH);

    split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panel, logTabbedPane);
    if (isRtype)
      split.setDividerLocation(200);
    else
      split.setDividerLocation(150);

    /**************************************************************************/
    /* revised by cosmos 2003/01/18                                           */
    /* �C�����e�FACL�G�f�B�^�ɃR�[�h�G�f�B�^��\������f�X�N�g�b�v�y�C���̗̈��ǉ�     */
    /**************************************************************************/
    Container container = this.getContentPane();
    container.add(split, BorderLayout.CENTER);
    container.add(toolbar, BorderLayout.NORTH);
/*
    // ACL�G�f�B�^���i�[����p�l�����쐬���܂�
    JPanel AclEditorPanel = new JPanel (new BorderLayout() );
    AclEditorPanel.add(split, BorderLayout.CENTER);

    // �R�[�h�G�f�B�^��\������f�X�N�g�b�v�y�C�����쐬���܂�
    CodeEditorDesktop = new JDesktopPane();
    CodeEditorDesktop2 = new JDesktopPane();
    CodeEditorDesktop.setBackground(Color.gray);
    CodeEditorDesktop2.setBackground(Color.gray);

    JPanel ViewerPanel = new JPanel();
    Viewer viewer = new Viewer (dvmname,dvm);
    canvas  = new ViewerCanvasW2(dvmname);

    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayout(0, 3));

    // Swing�̃��W�I�{�^���̃O���[�v��
    ButtonGroup group = new ButtonGroup();
    JRadioButton cb1 = new JRadioButton("non-stop", false);
    JRadioButton cb2 = new JRadioButton("step", true);
    group.add(cb1);
    group.add(cb2);
    panel1.add(cb1);
    panel1.add(cb2);
    cb1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jRadioButton_actionPerformed(e);
        }
    });
    cb1.setActionCommand ("non-stop");
    cb2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jRadioButton_actionPerformed(e);
        }
    });
    cb2.setActionCommand ("step");


    JLabel stepLabel = new JLabel("0", Label.RIGHT);
    panel1.add(stepLabel);
    canvas.stepLabel = stepLabel;   // ViewerCanvas�ɋ�����

    JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout());
    //panel2.add("Center", canvas);
    panel2.add("South", panel1);

    //JSplitPane split3 = new JSplitPane (JSplitPane.VERTICAL_SPLIT,true,CodeEditorDesktop,canvas);
    JSplitPane split3 = new JSplitPane (JSplitPane.VERTICAL_SPLIT,true,CodeEditorDesktop,panel2);
    split3.setDividerLocation(300);
    split3.setOneTouchExpandable(true);
    //ViewerPanel.setVisible(false);

    // ACL�G�f�B�^�p�l���ƃf�X�N�g�b�v�y�C�������E�ɕ������܂�
    //JSplitPane split2 = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,AclEditorPanel,CodeEditorDesktop);
    JSplitPane split2 = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,AclEditorPanel,split3);
    split2.setDividerLocation(300);
    split2.setOneTouchExpandable(true);

    JPanel p1 = new JPanel (new BorderLayout());
    JPanel p2 = new JPanel (new BorderLayout());
    JPanel p3 = new JPanel (new BorderLayout());

    JLabel jlabel_import_image = new JLabel();
    jlabel_import_image.setIcon(getImageIcon("resources/import_image.GIF"));
    p1.add(jlabel_import_image,BorderLayout.CENTER);
    //::container.add(split2, BorderLayout.CENTER);
    //::container.add(toolbar, BorderLayout.NORTH);
    p2.add(split2, BorderLayout.CENTER);
    p2.add(toolbar, BorderLayout.NORTH);
  */

//if (true ) return;
    /*
    //--------------------------------------------------------------------------
    // �����E��荞�݉�ʍ쐬
    //--------------------------------------------------------------------------
    // �v���W�F�N�g�c���[�쐬
    projectTree1 = new ProjectTree("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
    // �����p�l���쐬
    //searchPanel = new SearchPanel(this);
    searchPanel = new SearchPanel(null);
    // �c���[�ƌ����p�l�������E�ɕ�������
    JSplitPane spane = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree1.TreePanel,searchPanel );
    spane.setDividerLocation(150);
    spane.setOneTouchExpandable(true);

    //--------------------------------------------------------------------------
    // �J����ʍ쐬
    //--------------------------------------------------------------------------
    projectTree2 = new ProjectTree("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
    projectTree2.addMouseListener(new TreeSelect2());
    JSplitPane spaneDevScreen = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree2.TreePanel,CodeEditorDesktop2 );
    spaneDevScreen.setDividerLocation(150);
    spaneDevScreen.setOneTouchExpandable(true);

    // ����V�~�����[�^��ʍ쐬
    projectTree3 = new ProjectTree("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
    //simulatorPanel = new Simulator(treeScrollPane);
    simulatorPanel = new Simulator(null);
    JSplitPane spaneSimulator = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree3.TreePanel,simulatorPanel );
    spaneSimulator.setDividerLocation(150);
    spaneSimulator.setOneTouchExpandable(true);

    // �o�^��ʍ쐬
    projectTree4 = new ProjectTree("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
    savePanel = new SavePanel(null);
    JSplitPane spaneSave = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree4.TreePanel,savePanel );
    spaneSave.setDividerLocation(150);
    spaneSave.setOneTouchExpandable(true);

    JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
    jTabbedPane.addTab("����/��荞��", spane);
    jTabbedPane.addTab("�J��", spaneDevScreen);
    jTabbedPane.addTab("�J��/����Эڰ�", spaneSimulator);
    //jTabbedPane.addTab("�J��/����Эڰ�", p2);
    jTabbedPane.addTab("�o�^/�X�V", spaneSave);
    //jTabbedPane.setSelectedIndex(1);
    container.add(jTabbedPane, BorderLayout.CENTER);

    //File file2 = new File("C:\\dash-1.9.7h\\scripts\\cnp\\CnpManager.dash");
    //dvm.addLoadQueue(file2);

    //���������@Old Code�@��������
    //Container container = this.getContentPane();
    //container.add(split, BorderLayout.CENTER);
    //���������@Old Code�@��������

    //************************************************************************
    // revised by cosmos 2003/01/18 �����܂�
    //************************************************************************

    //Container container = this.getContentPane();
    //container.add(split, BorderLayout.CENTER);


    //:::container.add(toolbar, BorderLayout.NORTH);
    //p.add(toolbar, BorderLayout.NORTH);
*/
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent evt) {
          dvm.finalizeDVM();
        }});

    setSize(300,400);

    waittimeForNonstop =
      Integer.parseInt(System.getProperty("dash.waittimeForNonstop", "1000"));

    /**************************************************************************/
    /* revised by cosmos 2003/01/18                                           */
    /* �C�����e�F�����\���̃T�C�Y��傫������                                       */
    /**************************************************************************/
    /** ���݂̉�ʃT�C�Y���擾 **/
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //setSize(screenSize.width,screenSize.height);
    /*
    setSize(1000,800);
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    updateWindowMenu ( );
    */
    //���������@Old Code�@��������
    setSize(300,400);
    //���������@Old Code�@��������
    /**************************************************************************/
    /* revised by cosmos 2003/01/18 �����܂�                                   */
    /**************************************************************************/

  }

  private JMenuItem menuItem(String label) {
    JMenuItem item = new JMenuItem(label);
    item.addActionListener(this);
    return item;
  }




  /** "<?Repository>"�Ȃǂ�u��������B*/
  private void replaceMeta(StringBuffer buffer) {
    replaceMeta(buffer,
                "\"<?Repository>\"",
                System.getProperty("adips97.w.defaultR"));

    replaceMeta(buffer,
                "\"<?Here>\"",
                dvm.getDVMname());
  }

  private void replaceMeta(StringBuffer buffer, String oldstr, String newstr) {
    int length = oldstr.length();
    while (true) {
      int start = buffer.toString().indexOf(oldstr);
      if (start < 0)
        break;
      buffer.replace(start, start+length, newstr);
    }
  }

  /** ImageIcon��Ԃ��B*/
  private ImageIcon getImageIcon(String path) {
    java.net.URL url = this.getClass().getResource(path);
    return new ImageIcon(url);
  }

  /** �������̊Ď����J�n���� */
  public void startMemoryWatch() {
    Runnable r = new Runnable() {
        Runtime runtime = Runtime.getRuntime();
        long total, free, use;

        public void run() {
          replaceConsole();
          if (usingConsoleReplace)
            while (consoleReplace==null)
	      try { Thread.sleep(500); } catch (InterruptedException e) { }

          while (true) {
            if (usingConsoleReplace)
              consoleReplace.print();
            total = runtime.totalMemory() / 1024L;
            free = runtime.freeMemory() / 1024L;
            use = total - free;
            memoryLabel.setText("use: "+use+ " Kb    total: "+total+" Kb    agent: "+dvm.numberOfAgent());
            try { Thread.sleep(1000); } catch (InterruptedException e) { }
          }
        }
      };
    Thread thread = new Thread(dvm.dashThreads, r, "MemoryWatcher");
    thread.start();
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

    canvas.showMsg(m, isRtype);
    //treePaneModel.nextTurn();  �������ł͌Ă΂Ȃ��B

    /*DTM
    synchronized (objectForSync) {
      showMessage(m);
    }
    */
  }

  /**
   * ���b�Z�[�W�̏������s���B
   */
  private void showMessage(final DashMessage m) {
    //System.out.println ("NewIf Call showMessage()"); COSMOS

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
              msgArea.setText(m.toString2());
            }
          }};
      SwingUtilities.invokeLater(r);
      //treePane.showMsg(m, dvm.getFullname(), keyForShowMsg);

      //treePaneModel.nextTurn(); ��DashTree.showMsg()�̒��ŌĂԁB
    }
  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (e.getSource() instanceof JButton && command.equals("Send")) {
      // ADD COSMOS
      // �����t���[���̔z����擾����
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
  private void sendMessageFromACLeditor() {
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
    TreePath path = treePane.getSelectionPath();
    if (path == null) return; //�O�̂���

    String name = path.getLastPathComponent().toString();
    Object[] msg = { "Kill "+name+". OK?" };
    int ans = JOptionPane.showConfirmDialog(getContentPane(), msg, "Confirm Dialog", JOptionPane.OK_CANCEL_OPTION);

    if (ans==JOptionPane.OK_OPTION)
      dvm.stopAgent(name);
  }

  /**
   * �G�[�W�F���g�L�q�t�@�C���܂��̓v���W�F�N�g�t�@�C�����J���B
   */
  private void openAgentfile() {
    JFileChooser fileChooser = new JFileChooser(defaultOpenDir);
    fileChooser.addChoosableFileFilter(new DashFileFilter());
    int ret = fileChooser.showOpenDialog(getContentPane());
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
    logTabbedPane.setSelectedIndex(i);
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
  public void println(final String s) {
    if (!usingConsoleReplace) {
      System.out.println(s);
      return;
    }

    if (isShowing()) {
      Runnable r = new Runnable() {
          public void run() {
            synchronized (logArea) {
              logArea.append(s+"\n");
              logAreaLines++;
              String text = logArea.getText();
              if (logAreaLines > 200) {
                StringTokenizer st = new StringTokenizer(text,"\n");
                // �̂Ă�s
                int loops = st.countTokens() - 100;
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
    } else
      System.out.println(s);
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
      Runnable r = new Runnable() {
          public void run() {
            synchronized (errorLogArea) {
              errorLogArea.append(s+"\n");
              errorLogAreaLines++;
              String text = errorLogArea.getText();
              if (errorLogAreaLines > 200) {
                StringTokenizer st = new StringTokenizer(text,"\n");
                // �̂Ă�s
                int loops = st.countTokens() - 100;
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
            logTabbedPane.setSelectedIndex(i);
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

      if (focusedObj instanceof JTree) {
        cutAction.setEnabled(false);
        copyAction.setEnabled(treePane.getSelectionPath() != null);
        pasteAction.setEnabled(false);
      } else if (focusedObj instanceof JTextComponent) {
        boolean selected =((JTextComponent)focusedObj).getSelectedText()!=null;
        cutAction.setEnabled(selected);
        copyAction.setEnabled(selected);
        pasteAction.setEnabled(true);
      } else {
        cutAction.setEnabled(false);
        copyAction.setEnabled(false);
        pasteAction.setEnabled(false);
      }
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

        String receiver = path.getLastPathComponent().toString();
        JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
        if (iframes.length == 0 )
          return;
        for (int i=0; i<iframes.length; i++ ) {
          if (iframes[i].getTitle().equals(receiver) ) {
            try {
              iframes[i].setIcon (false);
              iframes[i].setSelected (true );
              iframes[i].show();
            }
            catch (Exception ex) {}
          }
          else {
            try {
              iframes[i].setSelected (false );
            }
            catch (Exception ex) {}
          }
        }


        /*
        if (path != null && path.getPathCount() > 2 &&
            !path.getLastPathComponent().equals(treePaneModel.getACLnode())) {
          String receiver = path.getLastPathComponent().toString();
          dvm.openInspector(receiver);
        }
        */
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
              logTabbedPane.setSelectedIndex(i);
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

  /** Cut�A�N�V���� */
  class CutAction extends AbstractAction {
    public CutAction(String label, Icon icon) { super(label, icon); }
    public void actionPerformed(ActionEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            //Object focusedObj = ifFrame.getFocusOwner();
            Object focusedObj = editMenuListener.focusedObj;
            if (focusedObj instanceof JTextComponent)
              ((JTextComponent)focusedObj).cut();
          }};
      SwingUtilities.invokeLater(r);
    }
  }

  /** Paste�A�N�V���� */
  class PasteAction extends AbstractAction {
    public PasteAction(String label, Icon icon) { super(label, icon); }
    public void actionPerformed(ActionEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            //Object focusedObj = ifFrame.getFocusOwner();
            Object focusedObj = editMenuListener.focusedObj;
            if (focusedObj instanceof JTextComponent)
              ((JTextComponent)focusedObj).paste();
          }};
      SwingUtilities.invokeLater(r);
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
            //Object focusedObj = ifFrame.getFocusOwner();
            Object focusedObj = editMenuListener.focusedObj;
            if (focusedObj instanceof JTree) {
              TreePath path = ((JTree)focusedObj).getSelectionPath();
              if (path != null) { //�O�̂���
                String receiver = path.getLastPathComponent().toString();
                StringSelection contents = new StringSelection(receiver);
                Clipboard clip =
                  Toolkit.getDefaultToolkit().getSystemClipboard();
                clip.setContents(contents, null);
              }
            } else if (focusedObj instanceof JTextComponent) {
              JTextComponent jtext = (JTextComponent)focusedObj;
              if (jtext.getSelectedText() != null)
                jtext.copy();
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
      logTabbedPane.setSelectedIndex(i);

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
   * �v���p�e�Badips97.noconsole��on�̏ꍇ�A
   * �W���o�͂�all�^�u�ɁA�W���G���[�o�͂�error�^�u�ɐؑւ���B
   * Repository.main()����Ăяo���ƁA
   * main()�����s���Ă����X���b�h�����ł����Ƃ��Ƀp�C�v������B
   */
  public void replaceConsole() {
    if (consoleReplace != null) return;

    String sw = System.getProperty("dash.noconsole");
    if (sw.equalsIgnoreCase("on")) {
      consoleReplace = new ConsoleReplace();
      usingConsoleReplace = true;
    }
  }

  private class ConsoleReplace {

    /** System.setOut(), System.setErr()����Stream */
    private PrintStream stdoutPrintStream, stderrPrintStream;

    /** System.{out|err}.println()�����o�C�g����i�[����Stream*/
    ByteArrayOutputStream stdoutStream, stderrStream;

    /** �R���X�g���N�^ */
    ConsoleReplace() {
      stdoutStream = new ByteArrayOutputStream();
      stdoutPrintStream = new PrintStream(stdoutStream);
      System.setOut(stdoutPrintStream);

      stderrStream = new ByteArrayOutputStream();
      stderrPrintStream = new PrintStream(stderrStream);
      System.setErr(stderrPrintStream);
    }

    /** �\�� */
    void print() {
      synchronized (stdoutPrintStream) {
        if (stdoutStream.size()>0) {
          println(stdoutStream.toString());
          stdoutStream.reset();
        }
      }
      synchronized (stderrPrintStream) {
        if (stderrStream.size()>0) {
          printlnE(stderrStream.toString());
          stderrStream.reset();
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
    CodeEditorDesktop = simulatorPanel.inspectorDesktop;
    iframe.setResizable(true);
    iframe.setMaximizable(true);
    iframe.setClosable(true);
    iframe.setIconifiable(true);
    Dimension desktopSize = CodeEditorDesktop.getSize();
    iframe.setSize (CodeEditorDesktop.getWidth(), CodeEditorDesktop.getHeight());
    //iframe.setLocation (xnew, ynew);
    //updateLocation (iframe );
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
    CodeEditorDesktop.repaint();
    iframe.show();
    iframe.toFront();
    iframe.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    iframe.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
      public void internalFrameClosing(InternalFrameEvent e) {
        this_internalFrameClosing(e);
      }
      public void internalFrameDeiconified(InternalFrameEvent e) {
        this_internalFrameDeiconified(e);
      }
    });
    updateWindowMenu ( );
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

  // WIndow���j���[�X�V
  private void updateWindowMenu (){
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
    return null;
  }
  public void setInspectorDesktopPane (JDesktopPane desktopPane ) {
  }
  public String getDvmName(){
    return dvmname;
  }
  public void setViewerCanvasW2 (ViewerCanvasW2 canvas ){
  }
  public void setViewerCanvasR2 (ViewerCanvasR2 canvas ){
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



  }
  public void ViewerShowMsg(DashMessage m){
  }
  public void removeAgentAll (){
  }
  public DVM getDVM(){
    return dvm;
  }
  public void clearLog(){}
  public Vector getAllAgentName(){
    return null;
  }

}

