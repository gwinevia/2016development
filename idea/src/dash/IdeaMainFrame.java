package dash;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.event.PopupMenuEvent;
import java.util.*;
import editortools.*;
import javax.swing.border.*;

import ps.*;


import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.lang.reflect.Method;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

//import sun.tools.javac.Main;
//import com.sun.tools.javac.Main;

/*
import java.util.Enumeration;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
*/
/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

public class IdeaMainFrame extends JFrame implements ActionListener , TreeSelectionListener,
    DragGestureListener, DropTargetListener, DragSourceListener
{

  //private MyNodeInfo nodeinfo = null;

  private JPanel contentPane;
  private BorderLayout borderLayout1 = new BorderLayout();
  public static JDesktopPane desktop  = new JDesktopPane();
  //private static JScrollDesktopPane desktop = new JScrollDesktopPane();
  //private static JScrollDesktopPane CodeEditorDesktop  = null;
  private String UseDesktopPane = "jscroll";

  /* �v���W�F�N�g���̃N���X **/
  private static Project project = null;
  private static Project wkproject = null;

  /* �v���W�F�N�g�c���[�N���X **/
  public static ProjectTree projectTree1 = null;// �����E��荞�݉�ʗp
  public static ProjectTree projectTree2 = null;// �J����ʗp
  public static ProjectTree projectTree3 = null;// ����V�~�����[�^��ʗp
  public static ProjectTree projectTree4 = null;// �o�^��ʗp

  public static JScrollPane projectTreeSrlPane1 = null;// �����E��荞�݉�ʗp
  public static JScrollPane projectTreeSrlPane2 = null;// �����E��荞�݉�ʗp
  public static JScrollPane projectTreeSrlPane3 = null;// �����E��荞�݉�ʗp
  public static JScrollPane projectTreeSrlPane4 = null;// �����E��荞�݉�ʗp


  /* �����E��荞�݃p�l�� **/
  private static SearchPanel searchPanel = null;

  /* ����V�~�����[�^�p�l�� **/
  private static Simulator simulatorPanel = null;

  /** �o�^�p�l�� */
  private static SavePanel savePanel = null;


  /* �J����ʗp���j���[ **/
  private JMenuBar menubar_DevScreen = new JMenuBar();
  //�t�@�C�����j���[
  private JMenu fileMenu_DevScreen = null;
  // �ҏW���j���[
  private JMenu editMenu_DevScreen = null;
  // �������j���[
  private static JMenu searchMenu_DevSecreen = null;
  // �c�[�����j���[
  private JMenu toolMenu_DevScreen = null;
  // �E�B���h�E���j���[
  private JMenu windowMenu_DevScreen = null;
  // �v���W�F�N�g���j���[
  private JMenu projectMenu_DevScreen = null;

  // �t�@�C�����j���[�̃��j���[�A�C�e��
  private static JMenuItem exitMenu = null;
  private static JMenuItem savemenuitem = null;
  private static JMenuItem allsavemenuitem = null;
  private static JMenu openagainprjmenu = null;
  private static JMenu outsideTool = null;
  private static JMenu outsideTool2 = null;
  private static JMenuItem deletePrjMenuItem = null;
  private static JMenuItem newFileMenuItem = null;
  private static JMenuItem openProjectMenuItem = null;
  private static JMenuItem allSelMenuItem = null;
  private static JMenuItem newProjectMenuItem = null;
  private static JMenuItem deleteProjectHistMenuItem1 = null;
  private static JMenuItem deleteProjectHistMenuItem2 = null;

  // �ҏW���j���[�̃��j���[�A�C�e��
  private static JMenuItem undoMenuItem = null;
  private static JMenuItem redoMenuItem = null;
  private static JMenuItem cutMenuItem = null;
  private static JMenuItem copyMenuItem = null;
  private static JMenuItem pasetMenuItem = null;

  // �������j���[�̃��j���[�A�C�e��
  private static JMenuItem searchMenu1 = null;
  private static JMenuItem searchMenu2 = null;
  private static JMenuItem searchMenu3 = null;
  private static JMenuItem searchMenu4 = null;

  // �c�[�����j���[�̃��j���[�A�C�e��
  private static JMenuItem toolMenu1 = null;
  private static JMenuItem toolMenu2 = null;
  private static JMenuItem toolMenu3 = null;
  private static JMenuItem toolMenu4 = null;
  private static JMenuItem toolMenu5 = null;

  // �E�B���h�E���j���[�̃��j���[�A�C�e��
  private static JMenuItem windowMenu1 = null;
  private static JMenuItem windowMenu2 = null;
  private static JMenuItem windowMenu3 = null;
  private static JMenuItem windowMenu4 = null;

  // �v���W�F�N�g���j���[�̃��j���[�A�C�e��
  private static JMenuItem projectMenu1 = null;
  private static JMenuItem projectMenu2 = null;


  /** �J����ʗp�c�[���o�[ */
  private JToolBar toolbar_DevScreen = null;

  /** �J����ʗp�c�[���{�^�� */
  private static JButton saveBtn = null;
  private static JButton allsaveBtn = null;
  private static JButton newfileBtn = null;
  private static JButton newprjBtn = null;
  private static JButton openprjBtn = null;
  private static JButton undoBtn = null;
  private static JButton redoBtn = null;
  private static JButton copyBtn =  null;
  private static JButton codeCheck = null;
  private static JButton pasteBtn = null;
  private static JButton cutBtn = null;
  private static JButton searchBtn = null;
  private static JButton researchBtn = null;
  private static JButton substitutionBtn = null;

  /** �J���Ȃ����c�[���{�^�� */
  private JToggleButton openagainprjToggleButton = new JToggleButton();
  private JPopupMenu openagainprjPopupMenu = new JPopupMenu();

  /** �J����ʗpJEditTextArea�i�[�e�[�u�� */
  private static Hashtable htEditor = new Hashtable();
  private static Hashtable htEditorFrame = new Hashtable();
  private static Hashtable htFileTimeStampe = new Hashtable();


  /** WIndow�����p **/
  // �J�X�P�[�h�\���p�̃C���N�������g�l
  int xincrement = 20, yincrement = 30;
  int xnew = xincrement, ynew = yincrement;
  int newwidth = 400, newheight = 350;

  /** �c�[���o�[�p�A�C�R�� */
  private static ImageIcon saveicon = null;
  private static ImageIcon allsaveicon = null;
  private static ImageIcon saveicon_gray = null;
  private static ImageIcon allsaveicon_gray = null;

  /** �^�u*/
  private JTabbedPane jTabbedPane = null;

  /** �|�b�v�A�b�v���j���[ */
  private JPopupMenu treePopupMenu = null;
  private JPopupMenu CodeCheckPopupMenu = null;
  private JMenuItem popupMenuItem1 = null;
  private JMenuItem popupMenuItem2 = null;
  private JMenuItem popupMenuItem3 = null;
  private JMenuItem popupMenuItem4 = null;
  private JMenuItem popupMenuItem5 = null;
  private JMenuItem popupMenuItem6 = null;
  private JMenuItem popupMenuItem7 = null;
  private JMenuItem popupMenuItem8 = null;
  private JMenuItem popupMenuItem9 = null;
  private JMenuItem popupMenuItem10 = null;
  private JMenuItem popupMenuItem11 = null;
  private JMenuItem popupMenuItem12 = null;
  private JMenuItem toolMenu3_2 = null;
  private JMenuItem CodeCheckPopupMenuItem = null;




  /** dash�f�B���N�g�� */
  private static File dashdir = null;

  /** */
  private JFrame frame = null;

  /** DVM */
  private DVM dvm = null;

  /** ���݁A�J����Ă���v���W�F�N�g�t�@�C���� */
  private static String PrevProjectFileName = "";

  /** �G�f�B�^���ݒ�\���p�_�~�[�p�l�� */
  private JPanel dmypanel = new JPanel();
  private Editor dmyedit =null;


  private JList List = null;

  // �O���c�[�����
  public Vector vecOutsideToolInfo = new Vector();

  // �ȉ����g�p
  private UIDefaults defaults = UIManager.getLookAndFeelDefaults();
  public  Border rolloverBorder;
  private Border raisedBorder;
  private Border loweredBorder;


  private JLabel memoryLabel = new JLabel();

  /** �v���W�F�N�g���J����Ă��Ȃ����̌x����\������e�L�X�g�G���A */
  private JTextArea notOpenProjectMsg1 = new JTextArea();
  private JTextArea notOpenProjectMsg2 = new JTextArea();
  private JTextArea notOpenProjectMsg3 = new JTextArea();
  private JTextArea notOpenProjectMsg4 = new JTextArea();

  /** �t���[�������Ŏg�p���� */
  private JSplitPane spaneSearchScreen = null;
  private JSplitPane spaneDevScreen = null;
  private JSplitPane spaneSimulator = null;
  private JSplitPane spaneSave = null;

  private boolean projectOpened = false;

  private JSplitPane spaneDevScreen2 = null;
  private int ResultWindowDefaultHeight = 0;

  private DragSource dragSource = null;
  private DirectoryNode SelectedNode = null;
  protected TreePath SelectedTreePath = null;
  private String SelectedFolederPath = "";
  private TreePath dropTargetPath = null;

  private String SelectedBpFileName = "";
  private PrintStream stdoutPrintStream, stderrPrintStream;
  private ByteArrayOutputStream stdoutStream, stderrStream;

  /****************************************************************************
   * �R���X�g���N�^
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public IdeaMainFrame() {
    //enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /****************************************************************************
   * �t���[���̏�����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void jbInit() throws Exception  {

    /*
    UIDefaults uiDefaults = UIManager.getDefaults();
    Enumeration enum = uiDefaults.keys();
    while (enum.hasMoreElements())
    {
      Object key = enum.nextElement();
      Object value = uiDefaults.get(key);

      if (key.toString().startsWith("FileChooser.")) {
        System.out.println(key.toString());
      }
    }
    */

    //uidftest();

    /*
    for (Enumeration emu = UIManager.getDefaults().keys(); emu.hasMoreElements(); ) {
      String key = emu.nextElement().toString();
      if (key.startsWith("FileChooser.") ) {
        System.out.println(key);
      }
    }
    */
    System.setProperty("JavaCompile","off");
    stdoutStream = new ByteArrayOutputStream();
    stdoutPrintStream = new PrintStream(stdoutStream);

    stderrStream = new ByteArrayOutputStream();
    stderrPrintStream = new PrintStream(stderrStream);

    // �X�v���b�V����ʂ��쐬
    SplashScreen splashScreen = new SplashScreen();

    //--------------------------------------------------------------------------
    // ��ʂ̏����T�C�Y�ƈʒu��ݒ肷��
    //--------------------------------------------------------------------------
    frame = this;

    /** ���݂̉�ʃT�C�Y���擾 **/
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    setSize(1000,800);
    //setSize(screenSize);
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height-50;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    this.setSize(frameSize.width,frameSize.height);
    //setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    setLocation((screenSize.width - frameSize.width) / 2, 0);

    this.setTitle("IDEA");

    // �X�v���b�V����ʂ̃v���O���X�o�[���X�V
    splashScreen.advance();

    //--------------------------------------------------------------------------
    // �e��ݒ�t�@�C���̓ǂݍ��݂�UIManager�̕ύX
    //--------------------------------------------------------------------------
    // IDE�I�v�V�������̓Ǎ�
    readIdeOptionXml();
    // ���b�Z�[�W���̓Ǎ�
    readBilingaulXml();
		// JFileChooser�̂t�h�ύX
    changeFileChooserUI();
    // JColorChooser�̂t�h�ύX
    changeColorChooserUI();
    // �f�t�H���g�t�@�C���̓ǂݍ���
    DashDefaults dashDefaults = new DashDefaults();
    dashDefaults.loadDefaults();
    //File msgfile = dashDefaults.getMessageFile();
    dashdir = dashDefaults.getDashdir();
    // �O���c�[�����Ǎ�
    readOutsideToolInfo ();

    // �_�~�[�G�f�B�^�쐬
    // �c�[�����j���[����A�G�f�B�^�ݒ�@�\���Ăяo������
    dmyedit = new Editor(dmypanel,this );

    // UI�I�u�W�F�N�g��z�u����R���e�i���擾����
    contentPane = (JPanel)this.getContentPane();
    // �R���e�i�̃��C�A�E�g�ݒ�
    contentPane.setLayout(borderLayout1);


    // �X�v���b�V����ʂ̃v���O���X�o�[���X�V
    splashScreen.advance();


    //--------------------------------------------------------
    // �v���W�F�N�g���J����Ă��Ȃ����ɕ\������JTextArea��������
    //notOpenProjectMsg1�F����/�捞�t�F�[�Y�p
    //notOpenProjectMsg2�F�J���t�F�[�Y�p
    //notOpenProjectMsg3�F����V�~�����[�g�t�F�[�Y�p
    //notOpenProjectMsg4�F�o�^�t�F�[�Y�p
    //--------------------------------------------------------
    notOpenProjectMsg1 = createNotOpenProjectMsg();
    notOpenProjectMsg2 = createNotOpenProjectMsg();
    notOpenProjectMsg3 = createNotOpenProjectMsg();
    notOpenProjectMsg4 = createNotOpenProjectMsg();

    //--------------------------------------------------------------------------
    // �����E��荞�݉�ʍ쐬
    //--------------------------------------------------------------------------
    // �v���W�F�N�g�c���[�쐬
    projectTree1 = new ProjectTree("");
    projectTree1.addMouseListener(new TreeSelect2());
    projectTree1.addTreeSelectionListener(this);

    /* ********************** CHANGED ********************** */
    dragSource = DragSource.getDefaultDragSource() ;
    /* ****************** END OF CHANGE ******************** */

    DragGestureRecognizer dgr =
        dragSource.createDefaultDragGestureRecognizer(
        (Component)projectTree1,                             //DragSource
        DnDConstants.ACTION_COPY_OR_MOVE, //specifies valid actions
        this                              //DragGestureListener
        );


    /* Eliminates right mouse clicks as valid actions - useful especially
    * if you implement a JPopupMenu for the JTree
    */
    dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

    /* First argument:  Component to associate the target with
     * Second argument: DropTargetListener
    */
    DropTarget dropTarget = new DropTarget((Component)projectTree1, this);

    // �����p�l���쐬
    searchPanel = new SearchPanel(this);
    // �c���[�ƌ����p�l�������E�ɕ�������
    projectTreeSrlPane1 = new JScrollPane(projectTree1);
    //spaneSearchScreen = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree1.TreePanel,searchPanel );
    spaneSearchScreen = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTreeSrlPane1,searchPanel );
    spaneSearchScreen.remove(0);
    spaneSearchScreen.add(notOpenProjectMsg1,0);
    spaneSearchScreen.setDividerLocation(150);
    spaneSearchScreen.setOneTouchExpandable(true);
    this.setJMenuBar(searchPanel.menubar);

    JPanel panel_SearchScreen = new JPanel(new BorderLayout() );
    searchPanel.toolbar.setFloatable(false);
    panel_SearchScreen.add(searchPanel.toolbar,BorderLayout.NORTH);
    panel_SearchScreen.add(spaneSearchScreen,BorderLayout.CENTER);

    // scripts�t�H���_�̃p�X�������p�l���ɒm�点��
    String dirpathstr = "";
    if (!dashdir.toString().endsWith(File.separator)) {
        dirpathstr = dashdir.toString() + File.separator;
    }
    else {
      dirpathstr = dashdir.toString() + File.separator;
    }
    
    // ���̑O�ɁA���O�t�@�C����ۑ�����t�H���_���쐬����
    new File(dirpathstr + "log" ).mkdirs();
    
    // ��̃��O�t�@�C�����쐬����
    java.util.Date dt = new java.util.Date(System.currentTimeMillis());
    //long t = dt.getTime();
    //uchiya
    Calendar c= Calendar.getInstance();
	int year= c.get(Calendar.YEAR);
	int month= c.get(Calendar.MONTH)+1;
	int day= c.get(Calendar.DATE);
	int hour=c.get(Calendar.HOUR_OF_DAY);
	int minute=c.get(Calendar.MINUTE);
	int second=c.get(Calendar.SECOND);		
	String time = ""+year+month+day+hour+minute+second;
    //String logfilename = dirpathstr + "log" + File.separatorChar + "IDEA_LOG" + new Long(t).toString() + ".log";
    //String errfilename = dirpathstr + "log" + File.separatorChar + "IDEA_LOG" + new Long(t).toString() + ".error";
    //String msgfilename = dirpathstr + "log" + File.separatorChar + "IDEA_LOG" + new Long(t).toString() + ".msg";
    String logfilename = dirpathstr + "log" + File.separatorChar + "IDEA_LOG" + time + ".log";
	String errfilename = dirpathstr + "log" + File.separatorChar + "IDEA_LOG" + time + ".error";
	String msgfilename = dirpathstr + "log" + File.separatorChar + "IDEA_LOG" + time + ".msg";
  
    File fp  = new File (logfilename);
    FileOutputStream fos = new FileOutputStream (fp);
    PrintWriter pw  = new PrintWriter (fos);
    pw.close ();

    fp  = new File (errfilename);
    fos = new FileOutputStream (fp);
    pw  = new PrintWriter (fos);
    pw.close ();

    fp  = new File (msgfilename);
    fos = new FileOutputStream (fp);
    pw  = new PrintWriter (fos);
    pw.close ();


    System.setProperty("logfilename",logfilename);
    System.setProperty("errfilename",errfilename);
    System.setProperty("msgfilename",msgfilename);
    System.setProperty("msgno","1");
    
    dirpathstr += "scripts" + File.separator;
    searchPanel.setDefaultDir(dirpathstr);

    splashScreen.advance();

    //--------------------------------------------------------------------------
    // �J����ʍ쐬
    //--------------------------------------------------------------------------
    // �G�f�B�^�@�\������MDI�q�E�B���h�E��\������̈���쐬����
    //CodeEditorDesktop = new JDesktopPane();
    //desktop = new JScrollDesktopPane();
    //desktop = new JScrollDesktopPane(menubar_DevScreen);
    desktop.setBackground(Color.gray);

    // �J����ʗp�v���W�F�N�g�c���[���쐬����
    projectTree2 = new ProjectTree("");
    projectTree2.addMouseListener(new TreeSelect2());
    projectTree2.addTreeSelectionListener(this);
    DragGestureRecognizer dgr2 =
        dragSource.createDefaultDragGestureRecognizer(
        (Component)projectTree2,                             //DragSource
        DnDConstants.ACTION_COPY_OR_MOVE, //specifies valid actions
        this                              //DragGestureListener
        );


    /* Eliminates right mouse clicks as valid actions - useful especially
    * if you implement a JPopupMenu for the JTree
    */
    dgr2.setSourceActions(dgr2.getSourceActions() & ~InputEvent.BUTTON3_MASK);

    /* First argument:  Component to associate the target with
     * Second argument: DropTargetListener
    */
    DropTarget dropTarget2 = new DropTarget((Component)projectTree2, this);

    projectTreeSrlPane2 = new JScrollPane (projectTree2 );
    //spaneDevScreen = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree2.TreePanel,desktop );
    spaneDevScreen = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTreeSrlPane2,desktop );

    /*
    JPanel wkpanel = new JPanel(new BorderLayout());
    wkpanel.setBackground(Color.red);
    wkpanel.add(new JLabel("TEST"),BorderLayout.NORTH);
    wkpanel.add(CodeEditorDesktop,BorderLayout.CENTER);
    spaneDevScreen = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree2.TreePanel,wkpanel );
    */

    /*
    JScrollPane scrollPane = new JScrollPane();
    JViewport viewport = new JViewport();
    viewport.setView(CodeEditorDesktop);
    //viewport.setBounds(-100,-100,1000,16000);
    scrollPane.setViewport(viewport);
    CodeEditorDesktop.setPreferredSize(new Dimension(1600,1200)); //very important
    spaneDevScreen = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree2.TreePanel,scrollPane );
    */

    spaneDevScreen.remove(0);
    spaneDevScreen.add(notOpenProjectMsg2,0);

    spaneDevScreen.setDividerLocation(150);
    spaneDevScreen.setOneTouchExpandable(true);

    // ���j���[�쐬
    createMenu();

    // �c�[���o�[�쐬
    createToolBar();

    JPanel panel_DevScreen = new JPanel (new BorderLayout());
    panel_DevScreen.add(toolbar_DevScreen, BorderLayout.NORTH);

    // �R�[�h�`�F�b�N�̌��ʕ\���p�̈�쐬
    JPanel ResultPanel = new JPanel( new BorderLayout() );
    List = new JList();
    List.setCellRenderer(new MyCellRenderer());
    List.addMouseListener(new ResultListMouseHandler());
    JScrollPane scrlPane = new JScrollPane(List);
    ResultPanel.add(scrlPane, BorderLayout.CENTER);

    spaneDevScreen2 = new JSplitPane (JSplitPane.VERTICAL_SPLIT ,true,spaneDevScreen,ResultPanel );
    spaneDevScreen2.setDividerLocation(this.getHeight()/10*7);
    ResultWindowDefaultHeight = this.getHeight()-(this.getHeight()/10*7);
    spaneDevScreen2.setDividerSize(3);
    panel_DevScreen.add(spaneDevScreen2, BorderLayout.CENTER);

    splashScreen.advance();
    //--------------------------------------------------------------------------
    // ����V�~�����[�^��ʍ쐬
    //--------------------------------------------------------------------------
    projectTree3 = new ProjectTree("");
    projectTree3.addMouseListener(new TreeSelect2());

    projectTree3.addTreeSelectionListener(this);
    DragGestureRecognizer dgr3 =
        dragSource.createDefaultDragGestureRecognizer(
        (Component)projectTree3,                             //DragSource
        DnDConstants.ACTION_COPY_OR_MOVE, //specifies valid actions
        this                              //DragGestureListener
        );


    /* Eliminates right mouse clicks as valid actions - useful especially
    * if you implement a JPopupMenu for the JTree
    */
    dgr3.setSourceActions(dgr3.getSourceActions() & ~InputEvent.BUTTON3_MASK);

    /* First argument:  Component to associate the target with
     * Second argument: DropTargetListener
    */
    DropTarget dropTarget3 = new DropTarget((Component)projectTree3, this);

    simulatorPanel = new Simulator(this);
    projectTreeSrlPane3 = new JScrollPane (projectTree3);
    //spaneSimulator = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree3.TreePanel,simulatorPanel );
    spaneSimulator = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTreeSrlPane3,simulatorPanel );
    spaneSimulator.remove(0);
    spaneSimulator.add(notOpenProjectMsg3,0);

    spaneSimulator.setDividerLocation(150);
    spaneSimulator.setOneTouchExpandable(true);



    JPanel panel_SimulatorScreen = new JPanel(new BorderLayout() );
    panel_SimulatorScreen.add(simulatorPanel.toolbar,BorderLayout.NORTH);
    panel_SimulatorScreen.add(spaneSimulator,BorderLayout.CENTER);

    dvm = simulatorPanel.getDVM();
    startMemoryWatch();
    splashScreen.advance();
    simulatorPanel.setDefaultDir(dirpathstr);
    //--------------------------------------------------------------------------
    // �o�^���
    //--------------------------------------------------------------------------
    savePanel = new SavePanel (this);
    projectTree4 = new ProjectTree("");
    projectTree4.addMouseListener(new TreeSelect2());

    projectTree4.addTreeSelectionListener(this);
    DragGestureRecognizer dgr4 =
        dragSource.createDefaultDragGestureRecognizer(
        (Component)projectTree4,                             //DragSource
        DnDConstants.ACTION_COPY_OR_MOVE, //specifies valid actions
        this                              //DragGestureListener
        );


    /* Eliminates right mouse clicks as valid actions - useful especially
    * if you implement a JPopupMenu for the JTree
    */
    dgr3.setSourceActions(dgr4.getSourceActions() & ~InputEvent.BUTTON3_MASK);

    /* First argument:  Component to associate the target with
     * Second argument: DropTargetListener
    */
    DropTarget dropTarget4 = new DropTarget((Component)projectTree4, this);

    projectTreeSrlPane4 = new JScrollPane (projectTree4);
    //spaneSave = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree4.TreePanel,savePanel );
    spaneSave = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTreeSrlPane4,savePanel );
    spaneSave.remove(0);
    spaneSave.add(notOpenProjectMsg4,0);

    JPanel panel_SaveScreen = new JPanel(new BorderLayout() );
    savePanel.toolbar.setFloatable(false);
    panel_SaveScreen.add(savePanel.toolbar,BorderLayout.NORTH);
    panel_SaveScreen.add(spaneSave,BorderLayout.CENTER);

    spaneSave.setDividerLocation(150);
    spaneSave.setOneTouchExpandable(true);


    splashScreen.advance();

    //--------------------------------------------------------------------------
    // �^�u���쐬
    //--------------------------------------------------------------------------
    jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
    // "����/��荞��"
    jTabbedPane.addTab(getBilingualMsg("0037"), panel_SearchScreen);
    // "�J��"
    jTabbedPane.addTab(getBilingualMsg("0038"), panel_DevScreen);
    // "����V�~�����[�g"
    jTabbedPane.addTab(getBilingualMsg("0039"), panel_SimulatorScreen);
    // "�o�^"
    jTabbedPane.addTab(getBilingualMsg("0040"), panel_SaveScreen);
    jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jTabbedPane1_stateChanged(e);
      }
    });
	  this.getContentPane().add(jTabbedPane, BorderLayout.CENTER);
    this.getContentPane().add(memoryLabel, BorderLayout.SOUTH);
	  //�^�u�����A�������ʕ\�������̐F��ω�
	  //this.getContentPane().setBackground(Color.RED);
		
		splashScreen.advance();


    //--------------------------------------------------------------------------
    // �|�b�v�A�b�v���j���[�̍쐬
    //--------------------------------------------------------------------------
    treePopupMenu = new JPopupMenu();
    //"�J��"
    popupMenuItem1 = menuItem(getBilingualMsg("0033"),"OpenFile",getImageIcon("resources/permeation.gif"));
    treePopupMenu.add(popupMenuItem1);

    //"�V�K..."
    popupMenuItem2 = menuItem(getBilingualMsg("0002") + "...","NewFile",getImageIcon("resources/newfile.gif"));
    treePopupMenu.add(popupMenuItem2);

    //"���S�ɍ폜"
    popupMenuItem3 = menuItem(getBilingualMsg("0034"),"DeleteFile",getImageIcon("resources/delfile.gif"));
    treePopupMenu.add(popupMenuItem3);

    //"�v���W�F�N�g����̍폜"
    popupMenuItem4 = menuItem(getBilingualMsg("0035"),"DeleteFile2",getImageIcon("resources/delfile.gif"));
    treePopupMenu.add(popupMenuItem4);

    treePopupMenu.addSeparator();

    //"�v���W�F�N�g�̍폜"
    popupMenuItem5 = menuItem(getBilingualMsg("0116"),"DeleteProject",getImageIcon("resources/delfile.gif"));
    treePopupMenu.add(popupMenuItem5);

    treePopupMenu.addSeparator();

    //"�������쐬..."
    popupMenuItem6 = menuItem(getBilingualMsg("0031") + "...","CreateAliasFile",getImageIcon("resources/permeation.gif"));
    treePopupMenu.add(popupMenuItem6);

    // ���O�̕ύX
    popupMenuItem12 = menuItem(getBilingualMsg("0211") + "...","ChangeName",getImageIcon("resources/permeation.gif"));
    treePopupMenu.add(popupMenuItem12);

    // �ۑ�
    popupMenuItem8 = menuItem(getBilingualMsg("0006") + "(S)","Save2",getImageIcon("resources/save.gif"));
    // �S�ĕۑ�
    popupMenuItem9 = menuItem(getBilingualMsg("0007") + "(A)","AllSave",getImageIcon("resources/allsave.gif"));

    // �t�H���_�̒ǉ�
    popupMenuItem10 = menuItem(getBilingualMsg("0204"),"AddFolder",getImageIcon("resources/closefolder.gif"));

    //Dash�t�@�C���֘A�ݒ�
    popupMenuItem11 = menuItem(getBilingualMsg("0205"),"SetupDash-Bp-Relation",getImageIcon("resources/permeation.gif"));


    treePopupMenu.addSeparator();
    treePopupMenu.add(popupMenuItem8);
    treePopupMenu.add(popupMenuItem9);

    treePopupMenu.addSeparator();

    //"�R�[�h�`�F�b�N"
    popupMenuItem7 = menuItem(getBilingualMsg("0032"),"CodeCheck",getImageIcon("resources/permeation.gif"));
    treePopupMenu.add(popupMenuItem7);
    treePopupMenu.addSeparator();

    //"�\��(C)..."
    toolMenu3_2 = menuItem(getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3_2.setMnemonic('C');
    outsideTool2.add(toolMenu3_2);
    treePopupMenu.add(outsideTool2);
    if (vecOutsideToolInfo.size() > 0 ) {
      outsideTool2.addSeparator();
    }

    for (int i=0; i<vecOutsideToolInfo.size(); i++ ) {
      String wk = (String)vecOutsideToolInfo.elementAt(i) ;

      StringTokenizer st = new StringTokenizer(wk,",");
      int cnt = 0;
      while (st.hasMoreTokens()) {
        String data = st.nextToken();

        JMenuItem toolMenu4 = menuItem(new Integer(i+1).toString() + ":" + data, "OutsideTool_" + new Integer(i).toString(), null );
        outsideTool2.add(toolMenu4);
        break;
      }
    }

    treePopupMenu.addSeparator();
    treePopupMenu.add(popupMenuItem10);
    treePopupMenu.addSeparator();
    treePopupMenu.add(popupMenuItem11);


    // �R�[�h�`�F�b�N���ʕ\���̈�̃|�b�v�A�b�v���j���[���쐬
    CodeCheckPopupMenu = new JPopupMenu();
    //"�S�ăN���A"
    CodeCheckPopupMenuItem = menuItem(getBilingualMsg("0119"),"CodeCheckResultClear",null);
    CodeCheckPopupMenu.add(CodeCheckPopupMenuItem);


    // �O��A�I�����ɊJ����Ă����v���W�F�N�g���J��
    // �O��A�I�����Ƀv���W�F�N�g���J����Ă����ꍇ�AgetOpenHist���Ԃ��x�N�^�[�̃T�C�Y��
    // �P�ȏ�ɂȂ�
    Vector vecOpenHist = getOpenHist();
    if (vecOpenHist.size() > 0 ) {
         readProjectFile((String)vecOpenHist.elementAt(0));
    }


    // ���j���[�̃V���[�g�J�b�g�ݒ�
    changeMenuItemAccelerator();
    searchPanel.changeMenuItemAccelerator();
    savePanel.changeMenuItemAccelerator();
    simulatorPanel.changeMenuItemAccelerator();

    splashScreen.advance();
    splashScreen.dispose();


    // windowClosing�C�x���g�����s���ꂽ���Ƀt���[���������I�ɏ�����̂�h��
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    searchPanel.setDVM(simulatorPanel.getDVM());
    /*
    this.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        this_focusGained(e);
      }
    });
    */

    this.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        this_componentResized(e);
      }
    });

    this.addWindowListener(new WindowAdapter() {
      public void windowActivated(WindowEvent evt) {
        this_windowActivated(evt);
      }
      public void windowClosing(WindowEvent evt) {
        SystemExit();
      }
    });

  }

  void this_componentResized(ComponentEvent e) {
    int location = this.getHeight()/10*7;
    int h = this.getHeight();
    if (this.getHeight() - location < 150 ) {
      location -= 80;
    }
    //location = this.getHeight() -ResultWindowDefaultHeight;
    spaneDevScreen2.setDividerLocation(location);
    searchPanel.this_componentResized(e);
    savePanel.this_componentResized(e);
  }

  boolean checkFileTimeStampFlag = false;
  void this_windowActivated(WindowEvent e) {
    checkFileTimeStampFlag = true;
  }

  private void checkFileTimeStamp() {

    JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
    int iframe_index=0;
    for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
      String name = (String)emu.nextElement();
      JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
      iframes[iframe_index] = iframe;
    }
    //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    for (int i=0; i<iframes.length; i++ ) {
        String name = iframes[i].getTitle();
        Editor edit = (Editor)htEditor.get(name);

        File file = new File(edit.sFilePath);
        String lastModifiedStr = (String)htFileTimeStampe.get(name);

        if (!lastModifiedStr.equals(new Long(file.lastModified()).toString())){

          Object[] options = { "�͂�", "������" };
          String msg = getBilingualMsg("0189");
          options[0] = getBilingualMsg("0191");
          options[1] = getBilingualMsg("0192");

          //[path]�����ւ���
          int chkcnt = msg.indexOf("filename");
          msg = msg.substring(0,chkcnt) + edit.sFilePath + msg.substring(chkcnt+8);
          int ret = JOptionPane.showOptionDialog(this,msg,getBilingualMsg("0190"),
                           JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                            null, options, options[0]);
          /*
          int ret = JOptionPane.showOptionDialog(this,
              edit.sFilePath + "�̃^�C���X�^���v���ύX����܂����B�ǂݍ��݂Ȃ����܂����H", "���",
                                     JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                     null, options, options[0]);
          */

          int r = ret;

          if (ret != 1 ) {
            edit.FileRead(edit.sFilePath);
            edit.jetx.changecnt = 0;
            file = new File(edit.sFilePath);
            htFileTimeStampe.put(name, new Long(file.lastModified()).toString() );
            boolean ExistsChangeText = false;

            for (int j=0; j<iframes.length; j++ ) {
              name = iframes[j].getTitle();
              edit = (Editor)htEditor.get(name);
              if (edit.jetx.changecnt > 0 ) {
                ExistsChangeText = true;
                break;
              }
            }

            if (!ExistsChangeText ) {
              allsavemenuitem.setEnabled(false);
              allsaveBtn.setEnabled(false);
            }

          }

        }
    }

    for (int i=0; i<iframes.length; i++ ) {
      if (iframes[i].isSelected() ) {
        String name = iframes[i].getTitle();
        Editor edit = (Editor)htEditor.get(name);
        if (edit.jetx.changecnt > 0 ) {
          savemenuitem.setEnabled(true);
          saveBtn.setEnabled(true);
        }
        else {
          savemenuitem.setEnabled(false);
          saveBtn.setEnabled(false);
        }
      }
    }

  }

  //�E�B���h�E������ꂽ�Ƃ��ɏI������悤�ɃI�[�o�[���C�h
  /*
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      SystemExit();
      //this.dispose();
      //System.exit(0);
    }
    if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
    }
  }
  */


  /****************************************************************************
   * �J����ʗp���j���[�̍쐬
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void createMenu(){
    /* ���j���[�쐬 **/
    // �t�@�C�����j���[�쐬
    // �t�@�C��
    fileMenu_DevScreen = new JMenu(getBilingualMsg("0001") + "(F)");
    fileMenu_DevScreen.setMnemonic('F');
    // �ۑ�
    savemenuitem = menuItem(getBilingualMsg("0006") + "(S)","Save",getImageIcon("resources/save.gif"));
    // �S�ĕۑ�
    allsavemenuitem = menuItem(getBilingualMsg("0007") + "(A)","AllSave",getImageIcon("resources/allsave.gif"));
    // �I��
    exitMenu = menuItem(getBilingualMsg("0008")+"(X)","Exit",null);
    // �V�K
    newFileMenuItem = menuItem(getBilingualMsg("0002") + "(N)...","NewFile",getImageIcon("resources/newfile.gif"));
    // �V�K�v���W�F�N�g
    newProjectMenuItem = menuItem(getBilingualMsg("0003") + "(P)...","NewProject",getImageIcon("resources/newprj.gif"));
    //�v���W�F�N�g���J��
    openProjectMenuItem = menuItem(getBilingualMsg("0004") + "(O)...","OpenProject",getImageIcon("resources/openprj.gif"));
    //�v���W�F�N�g�̍폜
    deletePrjMenuItem = menuItem(getBilingualMsg("0116"),"DeleteProject",getImageIcon("resources/delfile.gif"));
    deletePrjMenuItem.setEnabled(false);

    // �j�[���j�b�N��ݒ�
    newFileMenuItem.setMnemonic('N');
    newProjectMenuItem.setMnemonic('P');
    openProjectMenuItem.setMnemonic('O');
    savemenuitem.setMnemonic('S');
    allsavemenuitem.setMnemonic('A');
    exitMenu.setMnemonic('X');

    // �A�N�Z�����[�^�L�[��ݒ�
    //setMenuItemAccelerator ("new-file", newFileMenuItem );
    //setMenuItemAccelerator ("open-project", openProjectMenuItem );
    //setMenuItemAccelerator ("file-save", savemenuitem );
    //setMenuItemAccelerator ("file-save-all", allsavemenuitem );

    // �ۑ����j���[�͖������ɂ��Ă���
    savemenuitem.setEnabled(false);
    allsavemenuitem.setEnabled(false);


    fileMenu_DevScreen.add(newFileMenuItem);
    fileMenu_DevScreen.add(newProjectMenuItem);
    fileMenu_DevScreen.add(openProjectMenuItem);

    // �J������
    openagainprjmenu = new JMenu(getBilingualMsg("0005") + "(R)");
    openagainprjmenu.setMnemonic('R');
    openagainprjmenu.setIcon(getImageIcon("resources/openagainprj.gif"));
    openagainprjmenu.setEnabled(false);
    fileMenu_DevScreen.add(openagainprjmenu);


    fileMenu_DevScreen.add(deletePrjMenuItem);
    fileMenu_DevScreen.add(savemenuitem);
    fileMenu_DevScreen.add(allsavemenuitem);
    fileMenu_DevScreen.addSeparator();
    fileMenu_DevScreen.add(exitMenu);

    menubar_DevScreen.add(fileMenu_DevScreen);

    //--------------------------------------------------------------------------
    // �ҏW���j���[
    //--------------------------------------------------------------------------
    // �ҏW
    editMenu_DevScreen = new JMenu(getBilingualMsg("0009") + "(E)");
    editMenu_DevScreen.setMnemonic('E');

    // ���j���[�A�C�e���쐬
    // ���ɖ߂�
    undoMenuItem  = menuItem(getBilingualMsg("0010") + "(U)", "Undo", getImageIcon("resources/undo.gif") );
    // ��蒼��
    redoMenuItem  = menuItem(getBilingualMsg("0011") + "(R)", "Redo", getImageIcon("resources/redo.gif") );
    // �؂���
    cutMenuItem   = menuItem(getBilingualMsg("0012") + "(T)", "Cut", getImageIcon("resources/cut.gif") );
    // �R�s�[
    copyMenuItem  = menuItem(getBilingualMsg("0013") + "(C)", "Copy", getImageIcon("resources/copy.gif") );
    // �\��t��
    pasetMenuItem = menuItem(getBilingualMsg("0014") +"(P)", "Paste", getImageIcon("resources/paste.gif") );
    // �S�đI��
    allSelMenuItem = menuItem(getBilingualMsg("0015") + "(A)", "AllSelect", getImageIcon("resources/permeation.gif") );

    // �j�[���j�b�N�쐬
    undoMenuItem.setMnemonic('U');
    redoMenuItem.setMnemonic('R');
    cutMenuItem.setMnemonic('T');
    copyMenuItem.setMnemonic('C');
    pasetMenuItem .setMnemonic('P');
    allSelMenuItem.setMnemonic('A');

    // �A�N�Z�����[�^�L�[�쐬
    //setMenuItemAccelerator ("undo", undoMenuItem );
    //setMenuItemAccelerator ("redo", redoMenuItem );
    //setMenuItemAccelerator ("cut-to-clipboard", cutMenuItem );
    //setMenuItemAccelerator ("copy-to-clipboard", copyMenuItem );
    //setMenuItemAccelerator ("paste-from-clipboard", pasetMenuItem );
    //setMenuItemAccelerator ("select-all", allSelMenuItem );


    editMenu_DevScreen.add(undoMenuItem);
    editMenu_DevScreen.add(redoMenuItem);
    editMenu_DevScreen.addSeparator();
    editMenu_DevScreen.add(cutMenuItem);
    editMenu_DevScreen.add(copyMenuItem);
    editMenu_DevScreen.add(pasetMenuItem);
    editMenu_DevScreen.addSeparator();
    editMenu_DevScreen.add(allSelMenuItem);
    menubar_DevScreen.add(editMenu_DevScreen);

    //--------------------------------------------------------------------------
    // �������j���[
    //--------------------------------------------------------------------------
    // ����
    searchMenu_DevSecreen = new JMenu(getBilingualMsg("0016") + "(S)");
    searchMenu_DevSecreen.setMnemonic('S');

    // ���j���[�A�C�e���쐬
    // ����
    searchMenu1 = menuItem(getBilingualMsg("0017") + "(F)...", "Search", getImageIcon("resources/search.gif") );
    // �u��
    searchMenu2 = menuItem(getBilingualMsg("0018") + "(R)...", "substitution", getImageIcon("resources/substitution.gif") );
    // �Č���
    searchMenu3 = menuItem(getBilingualMsg("0019") + "(A)", "ReSearch", getImageIcon("resources/research.gif") );
    // �w��s�Ɉړ�
    searchMenu4 = menuItem(getBilingualMsg("0020") + "(L)...", "Jump", getImageIcon("resources/permeation.gif") );

    // �j�[���j�b�N�ݒ�
    searchMenu1.setMnemonic('F');
    searchMenu2.setMnemonic('R');
    searchMenu3.setMnemonic('A');
    searchMenu4.setMnemonic('L');

    // �A�N�Z�����[�^�L�[�ݒ�
    //setMenuItemAccelerator ("search-dialog", searchMenu1 );
    //setMenuItemAccelerator ("replace-dialog", searchMenu2 );
    //setMenuItemAccelerator ("search-again", searchMenu3 );
    //setMenuItemAccelerator ("goto-line", searchMenu4 );


    searchMenu_DevSecreen.add(searchMenu1);
    searchMenu_DevSecreen.add(searchMenu2);
    searchMenu_DevSecreen.add(searchMenu3);
    searchMenu_DevSecreen.addSeparator();
    searchMenu_DevSecreen.add(searchMenu4);
    menubar_DevScreen.add(searchMenu_DevSecreen);

    //--------------------------------------------------------------------------
    // �v���W�F�N�g���j���[
    //--------------------------------------------------------------------------
    // ����
    projectMenu_DevScreen = new JMenu(getBilingualMsg("0136") + "(P)");
    projectMenu_DevScreen.setMnemonic('P');

    projectMenu1 = menuItem(getBilingualMsg("0032"), "CodeCheck", getImageIcon("resources/CodeCheck.gif") );
    projectMenu_DevScreen.add(projectMenu1);
    projectMenu2 = menuItem(getBilingualMsg("0209"), "Projectproperty", getImageIcon("resources/permeation.gif") );
    projectMenu_DevScreen.add(projectMenu2);

    menubar_DevScreen.add(projectMenu_DevScreen);


    //--------------------------------------------------------------------------
    // �c�[�����j���[
    //--------------------------------------------------------------------------
    toolMenu_DevScreen = new JMenu (getBilingualMsg("0021") + "(T)");
    toolMenu_DevScreen.setMnemonic('T');

    // ���j���[�A�C�e���쐬
    // �G�f�B�^�ݒ�
    toolMenu1 = menuItem(getBilingualMsg("0022") + "(E)...", "SetupEditor", null );
    // �L�[���[�h�ݒ�
    toolMenu2 = menuItem(getBilingualMsg("0023") +  "(K)...", "SetupKeyword", null );
    // IDE�I�v�V����
    toolMenu4 = menuItem(getBilingualMsg("0117") + "...", "IdeOption", null );
    // �V�~�����[�^�[���ݒ�
    toolMenu5 = menuItem(getBilingualMsg("0118") + "...", "SimulatorOption", null );


    // �O���c�[��
    outsideTool = new JMenu(getBilingualMsg("0024") + "(O)");
    outsideTool.setMnemonic('O');
    // �O���c�[��
    outsideTool2 = new JMenu(getBilingualMsg("0024") + "(O)");
    outsideTool2.setMnemonic('O');
    outsideTool2.setIcon(getImageIcon("resources/permeation.gif"));


    // �j�[���j�b�N�ݒ�
    toolMenu1.setMnemonic('E');
    toolMenu2.setMnemonic('K');

    toolMenu_DevScreen.add(toolMenu4);
    toolMenu_DevScreen.add(toolMenu1);
    toolMenu_DevScreen.add(toolMenu2);
    toolMenu_DevScreen.add(toolMenu5);
    toolMenu_DevScreen.addSeparator();
    toolMenu_DevScreen.add(outsideTool);

    // �\��
    toolMenu3 = menuItem(getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3.setMnemonic('C');
    outsideTool.add(toolMenu3);

    menubar_DevScreen.add(toolMenu_DevScreen);

    //readOutsideToolInfo ();

    if (vecOutsideToolInfo.size() > 0 ) {
      outsideTool.addSeparator();
    }

    for (int i=0; i<vecOutsideToolInfo.size(); i++ ) {
      String wk = (String)vecOutsideToolInfo.elementAt(i) ;

      StringTokenizer st = new StringTokenizer(wk,",");
      int cnt = 0;
      while (st.hasMoreTokens()) {
        String data = st.nextToken();

        JMenuItem toolMenu6 = menuItem(new Integer(i+1).toString() + ":" + data, "OutsideTool_" + new Integer(i).toString(), null );
        outsideTool.add(toolMenu6);
        break;
      }
    }

    //--------------------------------------------------------------------------
    // �E�B���h�E���j���[
    //--------------------------------------------------------------------------
    // �E�B���h�E
    windowMenu_DevScreen = new JMenu(getBilingualMsg("0026") + "(W)");
    windowMenu_DevScreen.setMnemonic('W');

    // �d�˂ĕ\��
    windowMenu1 = menuItem(getBilingualMsg("0027") + "(W)", "Cascade", null);
    // �㉺�ɕ��ׂĕ\��
    windowMenu2 = menuItem(getBilingualMsg("0028") + "(H)", "Tile_Horizonal", null);
    // ���E�ɕ��ׂĕ\��
    windowMenu3 = menuItem(getBilingualMsg("0029") + "(V)", "Tile_Vertical", null);
    // �S�čŏ���
    windowMenu4 = menuItem(getBilingualMsg("0030") + "(I)", "Iconize All", null);

    // �j�[���j�b�N�ݒ�
    windowMenu1.setMnemonic('W');
    windowMenu2.setMnemonic('H');
    windowMenu3.setMnemonic('V');
    windowMenu4.setMnemonic('I');

    windowMenu_DevScreen.add(windowMenu1);
    windowMenu_DevScreen.add(windowMenu2);
    windowMenu_DevScreen.add(windowMenu3);
    windowMenu_DevScreen.add(windowMenu4);
    menubar_DevScreen.add(windowMenu_DevScreen);

  }

  /****************************************************************************
   * �J����ʗp�c�[���o�[�̍쐬
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void createToolBar(){
    toolbar_DevScreen = new JToolBar();

    saveBtn = new JButton (null, getImageIcon("resources/save.gif"));
    allsaveBtn = new JButton (null,getImageIcon("resources/allsave.gif"));

    newfileBtn = new JButton(null,getImageIcon("resources/newfile.gif"));
    newprjBtn = new JButton(null,getImageIcon("resources/newprj.gif"));
    openprjBtn = new JButton(null,getImageIcon("resources/openprj.gif"));
    openagainprjToggleButton.setIcon(getImageIcon("resources/openagainprj2.gif"));
    undoBtn = new JButton(null,getImageIcon("resources/undo.gif"));
    redoBtn = new JButton(null,getImageIcon("resources/redo.gif"));
    copyBtn = new JButton(null,getImageIcon("resources/copy.gif"));
    codeCheck = new JButton(null,getImageIcon("resources/CodeCheck.gif"));
    pasteBtn = new JButton(null,getImageIcon("resources/paste.gif"));
    cutBtn = new JButton(null,getImageIcon("resources/cut.gif"));
    searchBtn = new JButton(null,getImageIcon("resources/search.gif"));
    researchBtn = new JButton(null,getImageIcon("resources/research.gif"));
    substitutionBtn = new JButton(null,getImageIcon("resources/substitution.gif"));

    newfileBtn.setActionCommand("NewFile");
    newprjBtn.setActionCommand("NewProject");
    openprjBtn.setActionCommand("OpenProject");
    undoBtn.setActionCommand("Undo");
    redoBtn.setActionCommand("Redo");
    copyBtn.setActionCommand("Copy");
    pasteBtn.setActionCommand("Paste");
    cutBtn.setActionCommand("Cut");
    searchBtn.setActionCommand("Search");
    researchBtn.setActionCommand("ReSearch");
    substitutionBtn.setActionCommand("substitution");
    codeCheck.setActionCommand("CodeCheck2");

    newfileBtn.addActionListener(this);
    newprjBtn.addActionListener(this);
    openprjBtn.addActionListener(this);
    undoBtn.addActionListener(this);
    redoBtn.addActionListener(this);
    copyBtn.addActionListener(this);
    pasteBtn.addActionListener(this);
    cutBtn.addActionListener(this);
    searchBtn.addActionListener(this);
    researchBtn.addActionListener(this);
    substitutionBtn.addActionListener(this);
    codeCheck.addActionListener(this);


    saveBtn.setActionCommand("Save");
    allsaveBtn.setActionCommand("AllSave");

    saveBtn.addActionListener(this);
    allsaveBtn.addActionListener(this);



    newfileBtn.setFocusable(false);
    newprjBtn.setFocusable(false);
    openprjBtn.setFocusable(false);
    undoBtn.setFocusable(false);
    redoBtn.setFocusable(false);
    copyBtn.setFocusable(false);
    pasteBtn.setFocusable(false);
    cutBtn.setFocusable(false);
    searchBtn.setFocusable(false);
    researchBtn.setFocusable(false);
    substitutionBtn.setFocusable(false);
    codeCheck.setFocusable(false);
    saveBtn.setFocusable(false);
    allsaveBtn.setFocusable(false);


    toolbar_DevScreen.add(newfileBtn);
    toolbar_DevScreen.add(newprjBtn);
    toolbar_DevScreen.add(openprjBtn);
    toolbar_DevScreen.add(openagainprjToggleButton);

    toolbar_DevScreen.add(saveBtn);
    toolbar_DevScreen.add(allsaveBtn);
    toolbar_DevScreen.add(codeCheck);

    toolbar_DevScreen.addSeparator();

    toolbar_DevScreen.add(undoBtn);
    toolbar_DevScreen.add(redoBtn);
    toolbar_DevScreen.add(copyBtn);
    toolbar_DevScreen.add(pasteBtn);
    toolbar_DevScreen.add(cutBtn);
    toolbar_DevScreen.add(searchBtn);
    toolbar_DevScreen.add(researchBtn);
    toolbar_DevScreen.add(substitutionBtn);


    newfileBtn.setToolTipText(getBilingualMsg("0002"));
    newprjBtn.setToolTipText(getBilingualMsg("0003"));
    openprjBtn.setToolTipText(getBilingualMsg("0004"));
    openagainprjToggleButton.setToolTipText(getBilingualMsg("0005"));
    undoBtn.setToolTipText(getBilingualMsg("0010"));
    redoBtn.setToolTipText(getBilingualMsg("0011"));
    copyBtn.setToolTipText(getBilingualMsg("0013"));
    pasteBtn.setToolTipText(getBilingualMsg("0014"));
    cutBtn.setToolTipText(getBilingualMsg("0012"));
    searchBtn.setToolTipText(getBilingualMsg("0017"));
    researchBtn.setToolTipText(getBilingualMsg("0019"));
    substitutionBtn.setToolTipText(getBilingualMsg("0018"));
    codeCheck.setToolTipText(getBilingualMsg("0032"));

    saveBtn.setToolTipText(getBilingualMsg("0006"));
    allsaveBtn.setToolTipText(getBilingualMsg("0007"));

    openagainprjToggleButton.setEnabled(false);
    openagainprjToggleButton.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        openagainprjToggleButton_stateChanged(e);
      }
    });

    openagainprjPopupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        openagainprjPopupMenu_popupMenuWillBecomeInvisible(e);
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });
    toolbar_DevScreen.setFloatable(false);

    saveBtn.setEnabled(false);
    allsaveBtn.setEnabled(false);

  }
  void openagainprjToggleButton_stateChanged(ChangeEvent e) {
    try {
      openagainprjPopupMenu.show(toolbar_DevScreen,openagainprjToggleButton.getX(),openagainprjToggleButton.getY()+openagainprjToggleButton.getHeight());
    }
    catch(Exception ee){}
  }

  void openagainprjPopupMenu_popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    openagainprjToggleButton.setSelected(false);
  }


  /****************************************************************************
   * �����t�F�[�Y�^�u�̐؂�ւ�����������C�x���g
   * @param ChangeEvent e
   * @return �Ȃ�
   ****************************************************************************/
  private int spaneSimu_DivLocation = 0;
  void jTabbedPane1_stateChanged(ChangeEvent e) {
    switch(jTabbedPane.getSelectedIndex() ) {
      case 0:// �����E��荞��
        if (spaneSimu_DivLocation == 0 ) {
          spaneSimu_DivLocation = spaneSimulator.getDividerLocation();
        }
        spaneSimulator.setDividerLocation(getWidth());
        for (int i=0;i<simulatorPanel.wp_max_cnt ; i++ ) {
          simulatorPanel.canvasW[i].setVisible(false);
        }
        simulatorPanel.canvasR.setVisible(false);
        //simulatorPanel.setVisible(false);
        this.setJMenuBar(searchPanel.menubar);
        break;
      case 1:// �J��
        if (spaneSimu_DivLocation == 0 ) {
          spaneSimu_DivLocation = spaneSimulator.getDividerLocation();
        }
        spaneSimulator.setDividerLocation(getWidth());
        for (int i=0;i<simulatorPanel.wp_max_cnt ; i++ ) {
          simulatorPanel.canvasW[i].setVisible(false);
        }
        simulatorPanel.canvasR.setVisible(false);
        //simulatorPanel.setVisible(false);

        toolbar_DevScreen.repaint();
        desktop.repaint();
        this.setJMenuBar(menubar_DevScreen);

        // �J����ʂŊJ����Ă���t�@�C���Ɠ������̂��A�捞�i�����E�捞�t�F�[�Y�Łj�����ꍇ��
        // ��荞�񂾍ŐV�̃t�@�C�����J����ʂŊJ���Ȃ������ۂ������[�U�[�Ɋm�F���鏈��
        JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
        int iframe_index=0;
        for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
          String name = (String)emu.nextElement();
          JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
          iframes[iframe_index] = iframe;
        }

        //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
        for (int i=0; i<iframes.length; i++ ) {
            String name = iframes[i].getTitle();
            Editor edit = (Editor)htEditor.get(name);

            File file = new File(edit.sFilePath);
            String lastModifiedStr = (String)htFileTimeStampe.get(name);

            if (!lastModifiedStr.equals(new Long(file.lastModified()).toString())){

              Object[] options = { "�͂�", "������" };
              String msg = getBilingualMsg("0189");
              options[0] = getBilingualMsg("0191");
              options[1] = getBilingualMsg("0192");

              //[path]�����ւ���
              int chkcnt = msg.indexOf("filename");
              msg = msg.substring(0,chkcnt) + edit.sFilePath + msg.substring(chkcnt+8);
              int ret = JOptionPane.showOptionDialog(this,msg,getBilingualMsg("0190"),
                               JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                null, options, options[0]);
              /*
              int ret = JOptionPane.showOptionDialog(this,
                  edit.sFilePath + "�̃^�C���X�^���v���ύX����܂����B�ǂݍ��݂Ȃ����܂����H", "���",
                                         JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                         null, options, options[0]);
              */

              int r = ret;

              if (ret != 1 ) {
                edit.FileRead(edit.sFilePath);
                edit.jetx.changecnt = 0;
                file = new File(edit.sFilePath);
                htFileTimeStampe.put(name, new Long(file.lastModified()).toString() );
                boolean ExistsChangeText = false;

                for (int j=0; j<iframes.length; j++ ) {
                  name = iframes[j].getTitle();
                  edit = (Editor)htEditor.get(name);
                  if (edit.jetx.changecnt > 0 ) {
                    ExistsChangeText = true;
                    break;
                  }
                }

                if (!ExistsChangeText ) {
                  allsavemenuitem.setEnabled(false);
                  allsaveBtn.setEnabled(false);
                }

              }

            }
        }

        for (int i=0; i<iframes.length; i++ ) {
          if (iframes[i].isSelected() ) {
            String name = iframes[i].getTitle();
            Editor edit = (Editor)htEditor.get(name);
            if (edit.jetx.changecnt > 0 ) {
              savemenuitem.setEnabled(true);
              saveBtn.setEnabled(true);
            }
            else {
              savemenuitem.setEnabled(false);
              saveBtn.setEnabled(false);
            }
          }
        }
        break;
      case 2:
        if (spaneSimu_DivLocation != 0 ) {
          spaneSimulator.setDividerLocation(spaneSimu_DivLocation);
          spaneSimu_DivLocation = 0;
        }
        for (int i=0;i<simulatorPanel.wp_max_cnt ; i++ ) {
          simulatorPanel.canvasW[i].setVisible(true);
        }
        simulatorPanel.canvasR.setVisible(true);
        //simulatorPanel.setVisible(true);
        // �V�~�����[�^�̐ݒ�̓Ǎ�����
        simulatorPanel.SettingCheck();
        this.setJMenuBar(simulatorPanel.menubar);
        break;
      case 3:
        if (spaneSimu_DivLocation == 0 ) {
          spaneSimu_DivLocation = spaneSimulator.getDividerLocation();
        }
        spaneSimulator.setDividerLocation(getWidth());
        for (int i=0;i<simulatorPanel.wp_max_cnt ; i++ ) {
          simulatorPanel.canvasW[i].setVisible(false);
        }
        //simulatorPanel.canvasR.setVisible(false);
        //simulatorPanel.setVisible(false);
        this.setJMenuBar(savePanel.menubar);
        break;
    }
  }

  /****************************************************************************
   * �C���[�W�A�C�R���̎擾<br>
   * ���j���[�A�c�[���o�[���Ŏg�p�����C���[�W�A�C�R�����쐬���ĕԂ��܂��B
   * @param�@�C���[�W�t�@�C���ւ̑��΃p�X
   * @return �Ȃ�
   ****************************************************************************/
  private ImageIcon getImageIcon(String path) {
    java.net.URL url = this.getClass().getResource(path);
    return new ImageIcon(url);

  }

  /****************************************************************************
   * ������
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  public void initialize() {
    simulatorPanel.initialize();
  }


  /****************************************************************************
   * �v���W�F�N�g�c���[�p�h���b�O���h���b�v
   * @param
   * @return �Ȃ�
   ****************************************************************************/


    /** Returns The selected node */
    /*
    public PersonNode getSelectedNode() {
      return projectTree2.SelectedNode;
    }
    */

    public void dragGestureRecognized(DragGestureEvent e) {
      int i =1;

      if (e.getTriggerEvent().getModifiers() == e.getTriggerEvent().BUTTON3_MASK ) {
        return;
      }
      
      //Get the Transferable Object
      DirectoryNode dragNode = SelectedNode;
      if (dragNode != null) {
      	DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)dragNode.getUserObject();
      	if (nodeinfo.getKind() == 4 ) {
      		// Java�t�@�C���̏ꍇ�A�h���b�O���������Ȃ�
      		return;
      	}
      	if (nodeinfo.isRelation() ) {
      		// ���̃t�@�C���ւ̊֘A�t���\������Ă���ꍇ�́A�h���b�O���������Ȃ�
      		return;
      	}
        Transferable transferable = (Transferable) dragNode.getUserObject();
        /* ********************** CHANGED ********************** */

        //Select the appropriate cursor;
        //Cursor cursor = DragSource.DefaultCopyNoDrop;
        Cursor cursor = DragSource.DefaultCopyDrop;
        int action = e.getDragAction();
        if (action == DnDConstants.ACTION_MOVE)
          //cursor = DragSource.DefaultMoveNoDrop;
          cursor = DragSource.DefaultMoveDrop;


        //In fact the cursor is set to NoDrop because once an action is rejected
        // by a dropTarget, the dragSourceListener are no more invoked.
        // Setting the cursor to no drop by default is so more logical, because
        // when the drop is accepted by a component, then the cursor is changed by the
        // dropActionChanged of the default DragSource.
        /* ****************** END OF CHANGE ******************** */

        /** ���m�F
        System.out.println(e.getTriggerEvent().getModifiers());
        System.out.println("BUTTON1_DOWN_MASK:" + e.getTriggerEvent().BUTTON1_DOWN_MASK);
        System.out.println("BUTTON1_MASK:" + e.getTriggerEvent().BUTTON1_MASK);

        System.out.println("BUTTON2_DOWN_MASK:" + e.getTriggerEvent().BUTTON2_DOWN_MASK);
        System.out.println("BUTTON2_MASK:" + e.getTriggerEvent().BUTTON2_MASK);

        System.out.println("BUTTON3_DOWN_MASK:" + e.getTriggerEvent().BUTTON3_DOWN_MASK);
        System.out.println("BUTTON3_MASK:" + e.getTriggerEvent().BUTTON3_MASK);
        **/

        //begin the drag
        dragSource.startDrag(e, cursor, transferable, this);
      }
    }
    /** DragSourceListener interface method */
    public void dragDropEnd(DragSourceDropEvent dsde) {
    }

    /** DragSourceListener interface method */
    public void dragEnter(DragSourceDragEvent dsde) {
      /* ********************** CHANGED ********************** */
      /* ****************** END OF CHANGE ******************** */
    }

    /** DragSourceListener interface method */
    public void dragOver(DragSourceDragEvent dsde) {
      /* ********************** CHANGED ********************** */
      /* ****************** END OF CHANGE ******************** */
    }

    /** DragSourceListener interface method */
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    /** DragSourceListener interface method */
    public void dragExit(DragSourceEvent dsde) {
    }

    /** DropTargetListener interface method - What we do when drag is released */
    public void drop(DropTargetDropEvent e) {
      try {

        ProjectTree projectTree = null;
        if (jTabbedPane.getSelectedIndex() == 0 ) {
          projectTree = projectTree1;
        }
        else if (jTabbedPane.getSelectedIndex() == 1 ) {
          projectTree = projectTree2;
        }
        else if (jTabbedPane.getSelectedIndex() == 2 ) {
          projectTree = projectTree3;
        }
        else if (jTabbedPane.getSelectedIndex() == 3 ) {
          projectTree = projectTree4;
        }

        Transferable tr = e.getTransferable();

        //flavor not supported, reject drop
        if (!tr.isDataFlavorSupported( DirectoryNodeInfo.INFO_FLAVOR)) e.rejectDrop();

        //cast into appropriate data type
        DirectoryNodeInfo childInfo =
          (DirectoryNodeInfo) tr.getTransferData( DirectoryNodeInfo.INFO_FLAVOR );
        String s = childInfo.getPath();
       // System.out.println(s);
        //JOptionPane.showMessageDialog(this,childInfo.getPath());
        //get new parent node
        Point loc = e.getLocation();
        TreePath destinationPath = projectTree.getPathForLocation(loc.x, loc.y);
        dropTargetPath = destinationPath;

        final String msg = testDropTarget(destinationPath, SelectedTreePath);
        if (msg != null) {

          e.rejectDrop();
          /*
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              JOptionPane.showMessageDialog(
                   this, msg, "Error Dialog", JOptionPane.ERROR_MESSAGE
              );
            }
          });
          */
          return;
        }

        File file = new File (childInfo.getPath());

        if (!file.isDirectory() ) {

          DirectoryNode newParent =
              (DirectoryNode) destinationPath.getLastPathComponent();

          DirectoryNodeInfo newParentInfo = (DirectoryNodeInfo)newParent.getUserObject();
          String NewPath = newParentInfo.getPath();
          if (!NewPath.endsWith(File.separator) ) {
            NewPath += File.separator;
          }
          //System.out.println(NewPath);

          String FileName = childInfo.getName();
          //System.out.println(FileName);

          String OrgPath = childInfo.getPath().substring(0,childInfo.getPath().lastIndexOf(FileName));
          if (!OrgPath.endsWith(File.separator) ) {
            OrgPath += File.separator;
          }
          //System.out.println(OrgPath);

          //System.out.println(childInfo.getPath().length());


          String NewFolder = newParentInfo.getPath().substring(project.getProjectPath().length());
          //System.out.println(NewFolder);

          String OrgFolder = OrgPath.substring(project.getProjectPath().length());
          //System.out.println(OrgFolder);

          String orgfile = childInfo.getPath();
          String makfile = NewPath + FileName;
          String[] copyfiles = {orgfile,makfile};
          CopyFile.filecopy(copyfiles);

          // ���̃t�@�C��������
          file = new File (makfile );
          if (file.exists() ) {
            file = new File (orgfile );
            file.delete();
          }

          if (!OrgFolder.equals("") ) {
            if (!OrgFolder.endsWith(File.separator) ) {
              OrgFolder += File.separator;
            }
          }

          if (!NewFolder.equals("") ) {
            if (!NewFolder.endsWith(File.separator) ) {
              NewFolder += File.separator;
            }
          }

          BufferedReader b_in;
          String sLine = "";
          Vector vecFileName = new Vector();
          b_in = new BufferedReader(new InputStreamReader(
              new FileInputStream(project.getProjectFileNameWithPath()),
              "JISAutoDetect"));
          while ((sLine = b_in.readLine()) != null)
          {
            if (sLine.equals(OrgFolder + FileName) ) {
              vecFileName.addElement(NewFolder + FileName );
            }
            else {
              vecFileName.addElement(sLine);
            }
          }
          b_in.close();

          File fp  = new File ( project.getProjectFileNameWithPath());
          FileOutputStream fos = new FileOutputStream (fp);
          PrintWriter pw  = new PrintWriter (fos);
          for (int i=0; i<vecFileName.size(); i++ ) {
            pw.println((String)vecFileName.elementAt(i));
          }
          pw.close ();

        }
        else {
          /*
          for (Enumeration emu = projectTree1.getExpandedDescendants(projectTree1.getPathForRow(0)); emu.hasMoreElements(); ) {
            TreePath path = (TreePath)emu.nextElement();

            DirectoryNode node =
                (DirectoryNode) path.getLastPathComponent();

            DirectoryNodeInfo nodeInfo = (DirectoryNodeInfo)node.getUserObject();
            System.out.println(nodeInfo.getPath());
          }
          */


          //if (true ) return;
          // Directory Move
          // �ړ�����f�B���N�g���̏����擾����
          Hashtable htFileInfo = new Hashtable();
          Vector vecOldPath = new Vector ();
          Vector vecOldRpath = new Vector ();
          Vector vecOldRpath2 = new Vector ();
          Vector vecNewpath = new Vector ();
          Vector vecNewRpath = new Vector ();

          Vector vecOldPrjFileInfo = new Vector ();
          Vector vecNewPrjFileInfo = new Vector ();


          //System.out.println("�ړ����̐e�F" + file.getParent());
          String orgParent = file.getParent();
          if (!orgParent.endsWith(File.separator) ) {
            orgParent += File.separator;
          }

          vecOldPath.addElement(file.getAbsolutePath());
          createDirectoryList (file, vecOldPath );
          //System.out.println("�p�X");
          for (int i=0; i<vecOldPath.size(); i++ ) {
            String path = (String)vecOldPath.elementAt(i);
            //System.out.println(path);
            String rpath = path.substring(project.getProjectPath().length());
            //System.out.println(rpath);
            vecOldRpath.addElement(rpath);

            rpath = path.substring(orgParent.length());
            //System.out.println(rpath);
            vecOldRpath2.addElement(rpath);
          }

          // �V�����쐬����p�X�����쐬
          DirectoryNode newParent =
              (DirectoryNode) destinationPath.getLastPathComponent();

          DirectoryNodeInfo newParentInfo = (DirectoryNodeInfo)newParent.getUserObject();
          String NewPath = newParentInfo.getPath();
          if (!NewPath.endsWith(File.separator) ) {
            NewPath += File.separator;
          }

          String newRPath = NewPath.substring(project.getProjectPath().length());
          if (!newRPath.equals("") ) {
            if (!newRPath.endsWith(File.separator) ) {
              newRPath += File.separator;
            }
          }
          for (int i=0; i<vecOldPath.size(); i++ ) {
            vecNewRpath.addElement(newRPath + (String)vecOldRpath2.elementAt(i));
            //System.out.println((String)vecOldRpath2.elementAt(i));
            //System.out.println(newRPath + (String)vecOldRpath2.elementAt(i));
          }


          for (int i=0; i<vecOldPath.size(); i++ ) {
            String newpath = NewPath + (String)vecOldRpath2.elementAt(i);
            vecNewpath.addElement(newpath);
            // �ړ���ɐV�����f�B���N�g�����쐬
            //System.out.println(newpath);

            File f = new File(newpath);
            if (f.isDirectory() ) {
              e.rejectDrop();
              Object[] options = { "OK" };
              String cstmStr = getBilingualMsg("0210");
              cstmStr = cstmStr.replaceAll("FOLDER_NAME",(String)vecOldRpath2.elementAt(i));

              JOptionPane.showOptionDialog(this,
                  cstmStr,getBilingualMsg("0129"),
                                         JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                         null, options, options[0]);
              return;
            }
            f.mkdirs();
          }

          //if (true ) return;

          createFileList (file, htFileInfo);

          // �t�@�C����V�����f�B���N�g���ɃR�s�[����
          for (int i=0; i<vecOldPath.size(); i++ ) {
            String OldPath = (String)vecOldPath.elementAt(i);

            for (Enumeration emu = htFileInfo.keys(); emu.hasMoreElements(); ) {
              String key = (String)emu.nextElement();
              String orgfile = (String)htFileInfo.get(key);
              File f = new File (orgfile ) ;
              String parentPath = f.getParent();
              if (parentPath.equals(OldPath) ) {

                //f = new File (orgfile );

                String newPath = (String)vecNewpath.elementAt(i);
                if (!newPath.endsWith(File.separator) ) {
                  newPath += File.separator;
                }
                String makfile = newPath + f.getName();

                String[] copyfiles = {orgfile,makfile};
                CopyFile.filecopy(copyfiles);

                //System.out.println(orgfile.substring(project.getProjectPath().length()));
                //System.out.println(makfile.substring(project.getProjectPath().length()));

                f.delete();
                vecOldPrjFileInfo.addElement(orgfile.substring(project.getProjectPath().length()));
                vecNewPrjFileInfo.addElement(makfile.substring(project.getProjectPath().length()));
              }
            }


          }

          for (int i=vecOldPath.size()-1; i>=0; i-- ) {
            String path = (String)vecOldPath.elementAt(i);
            File f = new File(path);
            f.delete();
          }

          // �v���W�F�N�g�t�@�C���̍X�V
          BufferedReader b_in;
          String sLine = "";
          Vector vecFileName = new Vector();
          b_in = new BufferedReader(new InputStreamReader(
              new FileInputStream(project.getProjectFileNameWithPath()),
              "JISAutoDetect"));
          while ((sLine = b_in.readLine()) != null)
          {
            int pos = vecOldPrjFileInfo.indexOf(sLine);
            if (pos != -1 ) {
              vecFileName.addElement((String)vecNewPrjFileInfo.elementAt(pos) );
              //System.out.println(sLine + " -> " + (String)vecNewPrjFileInfo.elementAt(pos) );
            }
            else {
              vecFileName.addElement(sLine);
            }
          }
          b_in.close();

          File fp  = new File ( project.getProjectFileNameWithPath());
          FileOutputStream fos = new FileOutputStream (fp);
          PrintWriter pw  = new PrintWriter (fos);
          for (int i=0; i<vecFileName.size(); i++ ) {
            pw.println((String)vecFileName.elementAt(i));
          }
          pw.close ();

          // �f�B���N�g�����t�@�C���̍X�V
          String ProjectPath = project.getProjectPath();
          String directoryInfoFile = "directoryinfo";
          String ProjectName = project.getProjectFileName();
          directoryInfoFile = ProjectName.substring(0,ProjectName.toLowerCase().lastIndexOf (".dpx")+1) + "directoryinfo";

          Vector vecFolderPath = new Vector();
          sLine = "";
          b_in = new BufferedReader(new InputStreamReader(
              new FileInputStream(ProjectPath + directoryInfoFile),
              "JISAutoDetect"));
          while ((sLine = b_in.readLine()) != null)
          {
            int pos = vecOldRpath.indexOf(sLine);
            if (pos != -1 ) {
              vecFolderPath.addElement((String)vecNewRpath.elementAt(pos));
              //System.out.println(sLine + " -> " + (String)vecNewRpath.elementAt(pos) );
            }
            else {
              vecFolderPath.addElement(sLine);
            }
          }
          b_in.close();

          fp  = new File ( ProjectPath + directoryInfoFile );
          fos = new FileOutputStream (fp);
          pw  = new PrintWriter (fos);
          for (int i=0; i<vecFolderPath.size(); i++ ) {
            pw.println((String)vecFolderPath.elementAt(i));
          }
          pw.close ();


        }
        readProjectFile(project.getProjectFileNameWithPath());
        /*
        //get old parent node
        PersonNode oldParent = (PersonNode) getSelectedNode().getParent();

        int action = e.getDropAction();
        boolean copyAction = (action == DnDConstants.ACTION_COPY);

        //make new child node
        PersonNode newChild = new PersonNode(childInfo);

        try {
          if (!copyAction) oldParent.remove(getSelectedNode());
          newParent.add(newChild);

          if (copyAction) e.acceptDrop (DnDConstants.ACTION_COPY);
          else e.acceptDrop (DnDConstants.ACTION_MOVE);
        }
        catch (java.lang.IllegalStateException ils) {
          e.rejectDrop();
        }

        e.getDropTargetContext().dropComplete(true);

        //expand nodes appropriately - this probably isnt the best way...
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        model.reload(oldParent);
        model.reload(newParent);
        TreePath parentPath = new TreePath(newParent.getPath());
        expandPath(parentPath);
        */
      }
      catch (IOException io) { e.rejectDrop(); }
      catch (UnsupportedFlavorException ufe) {e.rejectDrop();}

    }

    /**
    * �w�肵���f�B���N�g���ȉ��Ɋ܂܂��S�Ẵt�@�C���̈ꗗ���쐬����
    * @param File f
    * @param Hashtable htFileTable
    */
    public void createFileList(File f, Hashtable htFileTable ) {
      File current_dir = new File(f,".");
      String file_list[] = current_dir.list();

      for (int i=0; i<file_list.length; i++ ) {
        File current_file = new File(f,file_list[i]);
        if (current_file.isDirectory()){
          createFileList(current_file, htFileTable);
        }
        else {
          //System.out.println("�e�F" + current_file.getParent());
          //htFileTable.put(current_file.getParent(),current_file.getAbsolutePath());
          //htFileTable.put(current_file.getName(),current_file.getAbsolutePath());
          htFileTable.put(current_file.getAbsolutePath(),current_file.getAbsolutePath());
        }
      }
    }

    public void createDirectoryList(File f, Vector vecDirectory ) {
      File current_dir = new File(f,".");
      String file_list[] = current_dir.list();

      for (int i=0; i<file_list.length; i++ ) {
        File current_file = new File(f,file_list[i]);
        if (current_file.isDirectory()){
          vecDirectory.addElement(current_file.getAbsolutePath());
          createDirectoryList(current_file, vecDirectory);
        }
        else {
          //vecDirectory.add(current_file.getAbsolutePath());
        }
      }
    }

    /** DropTaregetListener interface method */
    public void dragEnter(DropTargetDragEvent e) {
    }

    /** DropTaregetListener interface method */
    public void dragExit(DropTargetEvent e) {
    }

    /** DropTaregetListener interface method */
    public void dragOver(DropTargetDragEvent e) {
      ProjectTree projectTree = null;
      if (jTabbedPane.getSelectedIndex() == 0 ) {
        projectTree = projectTree1;
      }
      else if (jTabbedPane.getSelectedIndex() == 1 ) {
        projectTree = projectTree2;
      }
      else if (jTabbedPane.getSelectedIndex() == 2 ) {
        projectTree = projectTree3;
      }
      else if (jTabbedPane.getSelectedIndex() == 3 ) {
        projectTree = projectTree4;
      }


      /* ********************** CHANGED ********************** */
      //set cursor location. Needed in setCursor method
      Point cursorLocationBis = e.getLocation();
      TreePath destinationPath =
          projectTree.getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);


      //e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE ) ;
      // if destination path is okay accept drop...
      if (testDropTarget(destinationPath, SelectedTreePath) == null){
        e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE ) ;
      }
      // ...otherwise reject drop
      else {
        e.rejectDrag() ;
      }
      /* ****************** END OF CHANGE ******************** */

    }

    private String testDropTarget(TreePath destination, TreePath dropper) {
      //Typical Tests for dropping

      //Test 1.
      boolean destinationPathIsNull = destination == null;
      if (destinationPathIsNull)
        return "Invalid drop location.";

      //Test 2.
      DirectoryNode node = (DirectoryNode) destination.getLastPathComponent();
      if ( !node.getAllowsChildren() )
        return "This node does not allow children";

      if (destination.equals(dropper))
        return "Destination cannot be same as source";

      //Test 3.
      if ( dropper.isDescendant(destination))
         return "Destination node cannot be a descendant.";

      //Test 4.
      if ( dropper.getParentPath().equals(destination))
         return "Destination node cannot be a parent.";

      //TEST 5
      DirectoryNodeInfo dni = (DirectoryNodeInfo)node.getUserObject();
      if (dni.getKind() > 1 ) {
        return "Not Folder";
      }

      return null;
    }

    /** DropTaregetListener interface method */
    public void dropActionChanged(DropTargetDragEvent e) {
    }



  public void valueChanged(TreeSelectionEvent e){

    SelectedTreePath = e.getNewLeadSelectionPath();
    if (SelectedTreePath == null) {
      SelectedNode = null;
      return;
    }
    SelectedNode =
      (DirectoryNode)SelectedTreePath.getLastPathComponent();

    DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)SelectedNode.getUserObject();
    SelectedFolederPath = nodeinfo.getPath();
    /*
    JTree tree = (JTree)e.getSource();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

    if (node != null){
      //label.setText((String)node.getUserObject());
    }
    */
  }

  /****************************************************************************
   * �v���W�F�N�g�c���[�p�}�E�X�C�x���g
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  class TreeSelect2 extends MouseAdapter {

    /** Linux�p */
    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger()){
        if (jTabbedPane.getSelectedIndex() == 0 ) {
          ShowTreePopupMenu(e, projectTree1 );
        }
        else if (jTabbedPane.getSelectedIndex() == 1 ) {
          ShowTreePopupMenu(e, projectTree2 );
        }
        else if (jTabbedPane.getSelectedIndex() == 2 ) {
          ShowTreePopupMenu(e, projectTree3 );
        }
        else if (jTabbedPane.getSelectedIndex() == 3 ) {
          ShowTreePopupMenu(e, projectTree4 );
        }
      }
      else if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        deselect (e );
    }

    /** Windows�p */
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger()){
        if (jTabbedPane.getSelectedIndex() == 0 ) {
          ShowTreePopupMenu(e, projectTree1 );
        }
        else if (jTabbedPane.getSelectedIndex() == 1 ) {
          ShowTreePopupMenu(e, projectTree2 );
        }
        else if (jTabbedPane.getSelectedIndex() == 2 ) {
          ShowTreePopupMenu(e, projectTree3 );
        }
        else if (jTabbedPane.getSelectedIndex() == 3 ) {
          ShowTreePopupMenu(e, projectTree4 );
        }
      }
      else if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        deselect (e );
    }

    private void deselect(final MouseEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            if (jTabbedPane.getSelectedIndex() == 0 ) {
              TreePath path = projectTree1.getPathForLocation(e.getX(), e.getY());
              if (path == null)
                projectTree1.clearSelection();
            }
            else if (jTabbedPane.getSelectedIndex() == 1 ) {
              TreePath path = projectTree2.getPathForLocation(e.getX(), e.getY());
              if (path == null)
                projectTree2.clearSelection();
            }
            else if (jTabbedPane.getSelectedIndex() == 2 ) {
              TreePath path = projectTree3.getPathForLocation(e.getX(), e.getY());
              if (path == null)
                projectTree3.clearSelection();
            }
            else if (jTabbedPane.getSelectedIndex() == 3 ) {
              TreePath path = projectTree4.getPathForLocation(e.getX(), e.getY());
              if (path == null)
                projectTree4.clearSelection();
            }

          }
        };
      SwingUtilities.invokeLater(r);
    }

    private void deselect2(final MouseEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            if (jTabbedPane.getSelectedIndex() == 0 ) {
              TreePath path = projectTree1.getPathForLocation(e.getX(), e.getY());
              if (path != null)
                projectTree1.setSelectionPath(path);
            }
            else if (jTabbedPane.getSelectedIndex() == 1 ) {
              TreePath path = projectTree2.getPathForLocation(e.getX(), e.getY());
              if (path != null)
                projectTree2.setSelectionPath(path);
            }
            else if (jTabbedPane.getSelectedIndex() == 2 ) {
              TreePath path = projectTree3.getPathForLocation(e.getX(), e.getY());
              if (path != null)
                projectTree3.setSelectionPath(path);
            }
            else if (jTabbedPane.getSelectedIndex() == 3 ) {
              TreePath path = projectTree4.getPathForLocation(e.getX(), e.getY());
              if (path != null)
                projectTree4.setSelectionPath(path);
            }

          }
        };
      SwingUtilities.invokeLater(r);
    }

    /** �C���X�y�N�^�̕\�� */
    public void mouseClicked(MouseEvent e) {
      //if (jTabbedPane.getSelectedIndex() != 1 ) {
      //  return;
      //}
      if (e.getClickCount() == 2) {

        if (project == null ) {
          return;
        }

        ProjectTree projectTree = null;
        if (jTabbedPane.getSelectedIndex() == 0 ) {
          // �捞�t�F�[�Y�I����
          projectTree = projectTree1;
        }
        else if (jTabbedPane.getSelectedIndex() == 1 ) {
          // �J���t�F�[�Y�I����
          projectTree = projectTree2;
        }
        else if (jTabbedPane.getSelectedIndex() == 2 ) {
          // ����V�~�����[�^�I����
          projectTree = projectTree3;
        }
        else if (jTabbedPane.getSelectedIndex() == 3 ) {
          // �o�^�t�F�[�Y�I����
          projectTree = projectTree4;
        }
        TreePath path = projectTree.getPathForLocation(e.getX(), e.getY());
        if (path == null ) {
          return;
        }
        String receiver = path.getLastPathComponent().toString();
        //OpenFile(receiver,0);

        DirectoryNode node = (DirectoryNode)path.getLastPathComponent();
        DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

        if (nodeinfo.getKind() <= 1 ) {
          return;
        }

        jTabbedPane.setSelectedIndex(1);
        OpenFile(receiver,nodeinfo.getPath(),0,nodeinfo.getKind());
      }
    }
  }


  /****************************************************************************
   * �v���W�F�N�g�c���[�p�}�E�X�C�x���g
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  class TreeSelect4 extends MouseAdapter {

    /** Linux�p */
    public void mousePressed(MouseEvent e) {
    }

    /** Windows�p */
    public void mouseReleased(MouseEvent e) {
    }

    private void deselect(final MouseEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            TreePath path = projectTree2.getPathForLocation(e.getX(), e.getY());
            if (path == null)
              projectTree4.clearSelection();
          }
        };
      SwingUtilities.invokeLater(r);
    }

    /** �C���X�y�N�^�̕\�� */
    public void mouseClicked(MouseEvent e) {

      if (e.getClickCount() == 1) {
        TreePath[] path = projectTree4.getSelectionPaths();

        for (int i=0; i<path.length;i++ ) {
          String filename = path[i].getLastPathComponent().toString();
          //System.out.println(filename);
        }

      }
    }
  }

  /****************************************************************************
   * �v���W�F�N�g�c���[�p�|�b�v�A�b�v���j���[�̕\��
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  private void ShowTreePopupMenu(MouseEvent e, ProjectTree projectTree){
    // �I������Ă���m�[�h���擾
    TreePath path = projectTree.getPathForLocation(e.getX(), e.getY());
    if (path != null) {
      // �|�b�v�A�b�v���j���[�̕\�����Ƀ}�E�X�J�[�\�����m�[�h��ɂ������ꍇ�B
      int row = projectTree.getRowForPath(path);
      int[] selectionRow = projectTree.getSelectionRows();
      boolean FindFlag = false;
      try {
        for (int i=0; i<selectionRow.length; i++ ) {
          if (selectionRow[i] == row ) {
            FindFlag = true;
            break;
          }
        }

        if (!FindFlag ) {
          projectTree.setSelectionPath(path);
        }
      }
      catch (Exception ee){
        projectTree.setSelectionPath(path);
        selectionRow = projectTree.getSelectionRows();
      }



      DirectoryNode dn = (DirectoryNode)path.getLastPathComponent();
      DirectoryNodeInfo dni = (DirectoryNodeInfo)dn.getUserObject();

      // �t�H���_�̒ǉ��𖳌��ɂ���
      popupMenuItem10.setEnabled(false);
      // Dash�t�@�C���֘A�ݒ�𖳌��ɂ���
      popupMenuItem11.setEnabled(false);

      // �������쐬��L���ɂ���
      popupMenuItem6.setEnabled(true);
      // ���O��ύX��L���ɂ���
      popupMenuItem12.setEnabled(true);

      // �R�[�h�`�F�b�N��L���ɂ���
      popupMenuItem7.setEnabled(true);

      if (selectionRow.length == 1 ) {
        if (dni.getKind() <= 1 ) {
          // �t�H���_�̒ǉ���L���ɂ���
          popupMenuItem10.setEnabled(true);
          // �������쐬��L���ɂ���
          popupMenuItem6.setEnabled(false);
          // ���O��ύX��L���ɂ���
          popupMenuItem12.setEnabled(false);
        }
        else if (dni.getKind() == 4 || dni.getKind() == 3 ) {
          // Dash�t�@�C���֘A�ݒ��L���ɂ���
          popupMenuItem11.setEnabled(true);
        }
        else if (dni.getKind() == 5 ) {
          // �R�[�h�`�F�b�N�𖳌��ɂ���
          popupMenuItem7.setEnabled(false);
        }
      }

      // �J����L���ɂ���
      popupMenuItem1.setEnabled(true);
      // �V�K��L���ɂ���
      popupMenuItem2.setEnabled(true);
      // ���S�ɍ폜��L���ɂ���
      popupMenuItem3.setEnabled(true);
      // �v���W�F�N�g����̍폜��L���ɂ���
      popupMenuItem4.setEnabled(true);
      // �ۑ���L���ɂ���
      popupMenuItem8.setEnabled(true);
      // �S�ĕۑ���L���ɂ���
      popupMenuItem9.setEnabled(true);


      /*
      treePopupMenu.getComponent(0).setEnabled(true);
      treePopupMenu.getComponent(1).setEnabled(true);
      treePopupMenu.getComponent(2).setEnabled(true);
      treePopupMenu.getComponent(3).setEnabled(true);
      treePopupMenu.getComponent(7).setEnabled(true);
      treePopupMenu.getComponent(9).setEnabled(true);
      treePopupMenu.getComponent(10).setEnabled(true);
      treePopupMenu.getComponent(12).setEnabled(true);
      */
      if (selectionRow.length == 1 ) {
        if (path.getLastPathComponent().toString().toLowerCase().endsWith(".dpx") ) {
          // �I���m�[�h��1�ŁA���̃m�[�h���v���W�F�N�g�̏ꍇ

          // �J���𖳌��ɂ���
          popupMenuItem1.setEnabled(false);
          // ���S�ɍ폜�𖳌��ɂ���
          popupMenuItem3.setEnabled(false);
          // �v���W�F�N�g����̍폜�𖳌��ɂ���
          popupMenuItem4.setEnabled(false);
          // �������쐬�𖳌��ɂ���
          popupMenuItem6.setEnabled(false);
          // ���O��ύX�𖳌��ɂ���
          popupMenuItem12.setEnabled(false);
          // �ۑ��𖳌��ɂ���
          popupMenuItem8.setEnabled(false);

          /*
          treePopupMenu.getComponent(0).setEnabled(false);
          treePopupMenu.getComponent(2).setEnabled(false);
          treePopupMenu.getComponent(3).setEnabled(false);
          treePopupMenu.getComponent(7).setEnabled(false);
          treePopupMenu.getComponent(9).setEnabled(false);
          */

          // �C������Ă���t�@�C���̗L���ɂ��A�u�S�ĕۑ��v�̗L��/������ݒ肷��
          if (getChangeFileCount() > 0 ) {
            // �S�ĕۑ���L���ɂ���
            popupMenuItem9.setEnabled(true);
            //treePopupMenu.getComponent(10).setEnabled(true);
          }
          else {
            // �S�ĕۑ��𖳌��ɂ���
            popupMenuItem9.setEnabled(false);
            //treePopupMenu.getComponent(10).setEnabled(false);
          }
        }
        else {
          // �C������Ă���t�@�C���̗L���ɂ��A�u�S�ĕۑ��v�̗L��/������ݒ肷��
          if (getChangeFileCount() > 0 ) {
            // �S�ĕۑ���L���ɂ���
            popupMenuItem9.setEnabled(true);
            //treePopupMenu.getComponent(10).setEnabled(true);
          }
          else {
            // �S�ĕۑ��𖳌��ɂ���
            popupMenuItem9.setEnabled(false);
            //treePopupMenu.getComponent(10).setEnabled(false);
          }

          // �ۑ����j���[���ꎞ�����ɂ���
          popupMenuItem8.setEnabled(false);
          //treePopupMenu.getComponent(9).setEnabled(false);
          Vector vecChangeFileName = getChangeFileName();
          Vector vecSelectFile = getSelectFiles();

          // �I���t�@�C���ɕύX�ς݂̃t�@�C��������ꍇ�́A�ۑ����j���[��L��������
          for (int i=0; i<vecSelectFile.size();i++){
            if (vecChangeFileName.indexOf((String)vecSelectFile.elementAt(i)) != -1 ) {
              popupMenuItem8.setEnabled(true);
              //treePopupMenu.getComponent(9).setEnabled(true);
              break;
            }
          }
        }
      }
      else {
        // �C������Ă���t�@�C���̗L���ɂ��A�u�S�ĕۑ��v�̗L��/������ݒ肷��
        if (getChangeFileCount() > 0 ) {
          // �S�ĕۑ���L���ɂ���
          popupMenuItem9.setEnabled(true);
          //treePopupMenu.getComponent(10).setEnabled(true);
        }
        else {
          // �S�ĕۑ��𖳌��ɂ���
          popupMenuItem9.setEnabled(false);
          //treePopupMenu.getComponent(10).setEnabled(false);
        }

        // �ۑ����j���[���ꎞ�����ɂ���
        popupMenuItem8.setEnabled(false);
        //treePopupMenu.getComponent(9).setEnabled(false);
        Vector vecChangeFileName = getChangeFileName();
        Vector vecSelectFile = getSelectFiles();

        // �I���t�@�C���ɕύX�ς݂̃t�@�C��������ꍇ�́A�ۑ����j���[��L��������
        for (int i=0; i<vecSelectFile.size();i++){
          if (vecChangeFileName.indexOf((String)vecSelectFile.elementAt(i)) != -1 ) {
            popupMenuItem8.setEnabled(true);
            //treePopupMenu.getComponent(9).setEnabled(true);
            break;
          }
        }

      }
    }
    else {
      // �����I������Ă��Ȃ���
      projectTree.clearSelection();
      // �J�����j���[�𖳌���
      popupMenuItem1.setEnabled(false);
      // �V�K���j���[��L����
      popupMenuItem2.setEnabled(true);
      // ���S�ɍ폜���j���[�𖳌���
      popupMenuItem3.setEnabled(false);
      // �������쐬���j���[�𖳌���
      popupMenuItem6.setEnabled(false);
      // ���O��ύX���j���[�𖳌���
      popupMenuItem12.setEnabled(false);
      // �R�[�h�`�F�b�N���j���[�𖳌���
      popupMenuItem7.setEnabled(false);
      // �ۑ����j���[�𖳌���
      popupMenuItem8.setEnabled(false);
      // �S�ĕۑ��𖳌���
      popupMenuItem9.setEnabled(false);

      // �v���W�F�N�g����̍폜�𖳌���
      popupMenuItem4.setEnabled(false);

      // Dash�t�@�C���֘A�ݒ�𖳌��ɂ���
      popupMenuItem11.setEnabled(false);
      /*

      treePopupMenu.getComponent(0).setEnabled(false);
      treePopupMenu.getComponent(1).setEnabled(true);
      treePopupMenu.getComponent(2).setEnabled(false);
      treePopupMenu.getComponent(3).setEnabled(false);
      treePopupMenu.getComponent(7).setEnabled(false);
      treePopupMenu.getComponent(12).setEnabled(false);
      treePopupMenu.getComponent(9).setEnabled(false);
      treePopupMenu.getComponent(10).setEnabled(true);
      */
      // �C������Ă���t�@�C���̗L���ɂ��A�u�S�ĕۑ��v�̗L��/������ݒ肷��
      if (getChangeFileCount() > 0 ) {
        // �S�ĕۑ���L���ɂ���
        popupMenuItem9.setEnabled(true);
        //treePopupMenu.getComponent(10).setEnabled(true);
      }
      else {
        // �S�ĕۑ��𖳌��ɂ���
        popupMenuItem9.setEnabled(false);
        //treePopupMenu.getComponent(10).setEnabled(false);
      }

    }

    // �v���W�F�N�g�̍폜��L����
    popupMenuItem5.setEnabled(true);
    //treePopupMenu.getComponent(5).setEnabled(true);
    if (project == null ) {
      // �v���W�F�N�g���J���Ă��Ȃ���

      popupMenuItem1.setEnabled(false);
      popupMenuItem2.setEnabled(false);
      popupMenuItem3.setEnabled(false);
      popupMenuItem4.setEnabled(false);
      popupMenuItem5.setEnabled(false);
      popupMenuItem6.setEnabled(false);
      popupMenuItem7.setEnabled(false);
      popupMenuItem8.setEnabled(false);
      popupMenuItem9.setEnabled(false);
      popupMenuItem10.setEnabled(false);
      popupMenuItem11.setEnabled(false);
      popupMenuItem12.setEnabled(false);

      /*
      treePopupMenu.getComponent(1).setEnabled(false);
      treePopupMenu.getComponent(2).setEnabled(false);
      treePopupMenu.getComponent(3).setEnabled(false);
      treePopupMenu.getComponent(4).setEnabled(false);
      treePopupMenu.getComponent(5).setEnabled(false);
      treePopupMenu.getComponent(6).setEnabled(false);
      treePopupMenu.getComponent(7).setEnabled(false);
      treePopupMenu.getComponent(8).setEnabled(false);
      treePopupMenu.getComponent(12).setEnabled(false);
      treePopupMenu.getComponent(13).setEnabled(false);
      */
    }
    showPopupMenu(e);
  }


  //****************************************************************************
  /** ���j���[�֘A
  //****************************************************************************
  /****************************************************************************
   * ���j���[�A�C�e���쐬
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  private JMenuItem menuItem(String label, String command, Icon icon) {
   JMenuItem item = new JMenuItem(label, icon);
   item.setActionCommand(command);
   item.addActionListener(this);
   return item;
 }

 /****************************************************************************
  * ���j���[�A�N�V����
  * @param
  * @return �Ȃ�
  ****************************************************************************/
 public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    //--------------------------------------------------------------------------
    // �t�@�C�������֘A
    //--------------------------------------------------------------------------
    // �ۑ������E�E�E���݁A�I������Ă���G�f�B�^�̕ۑ��������s��
    //--------------------------------------------------------------------------
    if (command.equals("Save")) {
      // �I������Ă���G�f�B�^�ŕ\�����Ă���t�@�C�������擾
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {return;}
      // �t�@�C���ۑ�
      fileSave (name );
    }
    //--------------------------------------------------------------------------
    // �ۑ������E�E�E�c���[�̃|�b�v�A�b�v���j���[������s���ꂽ�ꍇ
    //--------------------------------------------------------------------------
    if (command.equals("Save2")) {
      // �I������Ă���t�@�C���ƕύX���������Ă���t�@�C���̖��̂����߂�
      Vector vecChangeFileName = getChangeFileName();
      Vector vecSelectFiles = getSelectFiles();

      for (int i=0; i<vecSelectFiles.size();i++ ) {
        String name = "";
        name = (String)vecSelectFiles.elementAt(i);
        if (vecChangeFileName.indexOf(name) != -1 ) {
          // �t�@�C���ۑ�
          fileSave (name );
        }
      }
    }
    //--------------------------------------------------------------------------
    // �S�ĕۑ�
    //--------------------------------------------------------------------------
    else if (command.equals("AllSave")) {

      // �G�f�B�^�̃t���[����S�Ď擾���A�ύX������Ă�����̂�����ۑ��Ώۂɂ���
      for (Enumeration emu = htEditor.keys(); emu.hasMoreElements(); ) {
        String name = (String)emu.nextElement();
        Editor edit = (Editor)htEditor.get(name);
        if (edit.jetx.changecnt > 0 ) {
          // �t�@�C���ۑ�
          fileSave(name);
        }
      }
    }
    //--------------------------------------------------------------------------
    // �t�@�C�����J��
    //--------------------------------------------------------------------------
    else if (command.equals("OpenFile")) {
      if (project == null ) {
        return;
      }

      /*
      Vector vecSelectFiles = getSelectFiles();
      for (int j=0; j<vecSelectFiles.size(); j++ ) {
        String receiver = (String)vecSelectFiles.elementAt(j);
        OpenFile(receiver,0);
      }
      */

      TreePath[] path = getSelectPaths();
      for (int i=0; i<path.length;i++ ) {
        String filename = path[i].getLastPathComponent().toString();
        DirectoryNode node = (DirectoryNode)path[i].getLastPathComponent();
        DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

        if (!nodeinfo.getName().toLowerCase().endsWith(".dpx")) {
          OpenFile(nodeinfo.getName(),nodeinfo.getPath(),0,nodeinfo.getKind());
        }
      }


    }
    //--------------------------------------------------------------------------
    // �V�K��Dash�t�@�C���쐬
    //--------------------------------------------------------------------------
    else if (command.equals("NewFile")) {
      if (project == null ) {
        Object[] options = { "OK" };
        JOptionPane.showOptionDialog(this,
            getBilingualMsg("0068"),getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        /*
        JOptionPane.showOptionDialog(this,
            "�v���W�F�N�g���J����Ă��Ȃ����́A���̑���͖����ł��B","�G���[",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        */
        return;
      }
      NewChoice newChoice = new NewChoice(this,project);
      //newChoice.show();
  	  newChoice.setVisible(true);
      //NewFile newfile = new NewFile(this,project);
      //newfile.show();
    }
    //--------------------------------------------------------------------------
    // �����t�@�C���̍쐬
    //--------------------------------------------------------------------------
    else if(command.equals("CreateAliasFile") ) {
      if (project == null ) {
        Object[] options = { "OK" };
        JOptionPane.showOptionDialog(this,
            getBilingualMsg("0068"),getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        /*
        JOptionPane.showOptionDialog(this,
            "�v���W�F�N�g���J����Ă��Ȃ����́A���̑���͖����ł��B","�G���[",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
      */
        return;
      }

      /*
      Vector vecSelectFiles = getSelectFiles();
      if (vecSelectFiles.size() == 0 ) {
        return;
      }
      */

      TreePath[] path = getSelectPaths();
      if (path == null ) {
        return;
      }
      DirectoryNode node = (DirectoryNode)path[0].getLastPathComponent();
      DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

      if (nodeinfo.getKind() == 4 ) {
        //return;
      }
      //public CopyFile(IdeaMainFrame mainframe, int Mode, String Title, Project prj, String orgFileName, String orgFileNameWithPath) {

      CopyFile copyfile = new CopyFile(this,"CreateAliasFile",getBilingualMsg("0031"),project,nodeinfo.getName(), nodeinfo.getPath());
      //copyfile.show();
	    copyfile.setVisible(true);
 
      readProjectFile(project.getProjectFileNameWithPath());
    }
    //--------------------------------------------------------------------------
    // �����t�@�C���̍쐬
    //--------------------------------------------------------------------------
    else if(command.equals("ChangeName") ) {
      if (project == null ) {
        Object[] options = { "OK" };
        JOptionPane.showOptionDialog(this,
            getBilingualMsg("0068"),getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        /*
        JOptionPane.showOptionDialog(this,
            "�v���W�F�N�g���J����Ă��Ȃ����́A���̑���͖����ł��B","�G���[",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
      */
        return;
      }

      /*
      Vector vecSelectFiles = getSelectFiles();
      if (vecSelectFiles.size() == 0 ) {
        return;
      }
      */

      TreePath[] path = getSelectPaths();
      if (path == null ) {
        return;
      }
      DirectoryNode node = (DirectoryNode)path[0].getLastPathComponent();
      DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

      if (nodeinfo.getKind() == 4 ) {
        //return;
      }
      //public CopyFile(IdeaMainFrame mainframe, int Mode, String Title, Project prj, String orgFileName, String orgFileNameWithPath) {

      String orgName = nodeinfo.getName();
      CopyFile copyfile = new CopyFile(this,"ChangeName",getBilingualMsg("0211"),project,nodeinfo.getName(), nodeinfo.getPath());
      //copyfile.show();
		  copyfile.setVisible(true);


      String NewFileNameWithPath = copyfile.NewName;
      String NewFileName = new File(NewFileNameWithPath).getName();

      JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(orgName);
      if (iframe != null ) {
        iframe.setTitle(NewFileName);
      }

      Editor edit = (Editor)htEditor.get(orgName);
      if (edit != null ) {
        //edit.setFileName(NewFileName);
        edit.sFilePath = NewFileNameWithPath;
        edit.setTextName(NewFileNameWithPath);


        htEditor.put(NewFileName,edit);
        htEditorFrame.put(NewFileName,iframe);
        htFileTimeStampe.put(NewFileName, (String)htFileTimeStampe.get(orgName) );

        htEditor.remove(orgName);
        htEditorFrame.remove(orgName);
        htFileTimeStampe.remove(orgName);

      }
     if (projectTree1.vecChangeFileName.indexOf(orgName) != -1 ) {
        // �c���[�̕\�����X�V����K�v�����邽�߁A�ύX�t�@�C����m�点��
        projectTree1.vecChangeFileName.addElement(NewFileName);
        projectTree2.vecChangeFileName.addElement(NewFileName);
        projectTree3.vecChangeFileName.addElement(NewFileName);
        projectTree4.vecChangeFileName.addElement(NewFileName);

        projectTree1.vecChangeFileName.remove(orgName);
        projectTree2.vecChangeFileName.remove(orgName);
        projectTree3.vecChangeFileName.remove(orgName);
        projectTree4.vecChangeFileName.remove(orgName);

        projectTree1.repaint();
        projectTree2.repaint();
        projectTree3.repaint();
        projectTree4.repaint();

      }
     readProjectFile(project.getProjectFileNameWithPath());

    }
    //--------------------------------------------------------------------------
    // �t�@�C���̍폜
    //--------------------------------------------------------------------------
    else if (command.equals("DeleteFile")){
      if (project == null ) {
        Object[] options = { "OK" };
        JOptionPane.showOptionDialog(this,
            getBilingualMsg("0068"),getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        /*
        JOptionPane.showOptionDialog(this,
            "�v���W�F�N�g���J����Ă��Ȃ����́A���̑���͖����ł��B","�G���[",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        */
        return;
      }
      deletefiles(1);
    }
    //--------------------------------------------------------------------------
    // �t�@�C���̍폜
    //--------------------------------------------------------------------------
    else if (command.equals("DeleteFile2")){
      if (project == null ) {
        Object[] options = { "OK" };
        JOptionPane.showOptionDialog(this,
            getBilingualMsg("0068"),getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        /*
        JOptionPane.showOptionDialog(this,
            "�v���W�F�N�g���J����Ă��Ȃ����́A���̑���͖����ł��B","�G���[",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        */
        return;
      }
      deletefiles(2);
    }
    //--------------------------------------------------------------------------
    // �V�K�v���W�F�N�g
    //--------------------------------------------------------------------------
    else if (command.equals("NewProject")) {
      NewProject NewPrjWin = new NewProject (this,null);
      //NewPrjWin.show();
	    NewPrjWin.setVisible(true);
    }
    //--------------------------------------------------------------------------
    // �v���W�F�N�g���J��
    //--------------------------------------------------------------------------
    else if (command.equals("OpenProject")) {
      searchPanel.openprojectfile();
    }
    //--------------------------------------------------------------------------
    // �v���W�F�N�g���J���Ȃ���
    //--------------------------------------------------------------------------
    else if (command.equals("againPrjct")){
      //�v���W�F�N�g���J������
      if(e.getSource() instanceof JMenuItem){
        JMenuItem menuitem = (JMenuItem)e.getSource();
        this.readProjectFile(menuitem.getText());
      }

    }
    //--------------------------------------------------------------------------
    // �v���W�F�N�g�����̍폜
    //--------------------------------------------------------------------------
    else if (command.equals("deletehist") ) {
      this.deleteOpenHist();
    }
    //--------------------------------------------------------------------------
    // �v���W�F�N�g�̍폜
    //--------------------------------------------------------------------------
    else if (command.equals("DeleteProject") ) {
      this.deleteProject();
    }
    //--------------------------------------------------------------------------
    // �G�f�B�^�E�B���h�E�����֘A
    //--------------------------------------------------------------------------
    // �G�f�B�^�E�B���h�E�̐����^�C���\��
    //--------------------------------------------------------------------------
    else if (command.equals("Tile_Horizonal"))
      setTile_H (0 ) ;
    //--------------------------------------------------------------------------
    // �G�f�B�^�E�B���h�E�̐����^�C���\��
    //--------------------------------------------------------------------------
    else if (command.equals("Tile_Vertical"))
      setTile_V (0 );
    //--------------------------------------------------------------------------
    // �G�f�B�^�E�B���h�E�̃J�X�P�[�h
    //--------------------------------------------------------------------------
    else if (command.equals("Cascade"))
      setCascade ( );
    //--------------------------------------------------------------------------
    // �G�f�B�^�E�B���h�E��S�ăA�C�R����
    //--------------------------------------------------------------------------
    else if (command.equals("Iconize All"))
      setIconize ( );
    //--------------------------------------------------------------------------
    // �ȉ��A�G�f�B�^�֘A�̏����ł�
    //--------------------------------------------------------------------------
    // �G�f�B�^�ݒ�
    //--------------------------------------------------------------------------
    else if (command.equals("SetupEditor")) {
      SetupEditor();
    }
    //--------------------------------------------------------------------------
    // �L�[���[�h�ݒ�
    //--------------------------------------------------------------------------
    else if (command.equals("SetupKeyword")) {
      SetupKeyword();
    }
    //--------------------------------------------------------------------------
    // �L�[���[�h�}�b�v
    //--------------------------------------------------------------------------
    else if (command.equals("IdeOption")) {
      IdeOption();
    }
    //--------------------------------------------------------------------------
    // �L�[���[�h�}�b�v
    //--------------------------------------------------------------------------
    else if (command.equals("SimulatorOption")) {
      SimulatorOption();
    }
    //--------------------------------------------------------------------------
    // �R�s�[
    //--------------------------------------------------------------------------
    else if (command.equals("Copy")) {
      if (desktop.getSelectedFrame() == null ) {
        return;
      }
      if (desktop.getSelectedFrame().isIcon() ) {
        return;
      }
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {return;}
      Editor edit = (Editor)htEditor.get(name);
      edit.Copy(e);
    }
    //--------------------------------------------------------------------------
    // �\��t��
    //--------------------------------------------------------------------------
    else if (command.equals("Paste")) {
      if (desktop.getSelectedFrame() == null ) {
        return;
      }
      if (desktop.getSelectedFrame().isIcon() ) {
        return;
      }
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {
        return;
      }
      Editor edit = (Editor)htEditor.get(name);
      edit.Paste(e);
    }
    //--------------------------------------------------------------------------
    // �؂���
    //--------------------------------------------------------------------------
    else if (command.equals("Cut")) {
      if (desktop.getSelectedFrame() == null ) {
        return;
      }
      if (desktop.getSelectedFrame().isIcon() ) {
        return;
      }
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {
        return;
      }
      Editor edit = (Editor)htEditor.get(name);
      edit.Cut(e);
    }
    //--------------------------------------------------------------------------
    // UNDO
    //--------------------------------------------------------------------------
    else if (command.equals("Undo")) {
      if (desktop.getSelectedFrame() == null ) {
        return;
      }
      if (desktop.getSelectedFrame().isIcon() ) {
        return;
      }
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {
        return;
      }
      Editor edit = (Editor)htEditor.get(name);
      edit.jetx.ur.undoAction.actionPerformed(e);
    }
    //--------------------------------------------------------------------------
    // REDO
    //--------------------------------------------------------------------------
    else if (command.equals("Redo")) {
      if (desktop.getSelectedFrame() == null ) {
        return;
      }
      if (desktop.getSelectedFrame().isIcon() ) {
        return;
      }
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {
        return;
      }
      Editor edit = (Editor)htEditor.get(name);
      edit.jetx.ur.redoAction.actionPerformed(e);

    }
    //--------------------------------------------------------------------------
    // �����_�C�A���O�\��
    //--------------------------------------------------------------------------
    else if (command.equals("Search")) {
      if (desktop.getSelectedFrame() == null ) {
        return;
      }
      if (desktop.getSelectedFrame().isIcon() ) {
        return;
      }
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {
        return;
      }
      Editor edit = (Editor)htEditor.get(name);
      edit.jetx.search.SearchDialogOpen();
    }
    //--------------------------------------------------------------------------
    // �Č������s
    //--------------------------------------------------------------------------
    else if (command.equals("ReSearch")) {
      if (desktop.getSelectedFrame() == null ) {
        return;
      }
      if (desktop.getSelectedFrame().isIcon() ) {
        return;
      }
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {
        return;
      }
      Editor edit = (Editor)htEditor.get(name);
      edit.jetx.search.ReFind_Down();
    }
    //--------------------------------------------------------------------------
    // �u���_�C�A���O�\��
    //--------------------------------------------------------------------------
    else if (command.equals("substitution")) {
      if (desktop.getSelectedFrame() == null ) {
        return;
      }
      if (desktop.getSelectedFrame().isIcon() ) {
        return;
      }
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {
        return;
      }
      Editor edit = (Editor)htEditor.get(name);
      edit.jetx.substitution.SubstitutionDialogOpen();
    }
    //--------------------------------------------------------------------------
    // �w��s�ֈړ��_�C�A���O�̕\��
    //--------------------------------------------------------------------------
    else if (command.equals("Jump")) {
      if (desktop.getSelectedFrame() == null ) {
        return;
      }
      if (desktop.getSelectedFrame().isIcon() ) {
        return;
      }
      String name = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
      }
      catch (Exception ee ) {
        return;
      }
      Editor edit = (Editor)htEditor.get(name);
      LineMove lm = new LineMove (edit.jetx, this);
    }
    //--------------------------------------------------------------------------
    // ���̑�
    //--------------------------------------------------------------------------
    // �R�[�h�`�F�b�N(�c���[�̃|�b�v�A�b�v���j���[�p)
    //--------------------------------------------------------------------------
    else if (command.equals("CodeCheck") ){
      if (project == null ) {
        Object[] options = { "OK" };
        JOptionPane.showOptionDialog(this,
            getBilingualMsg("0193"),getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        /*
        JOptionPane.showOptionDialog(this,
            "�v���W�F�N�g���J����Ă��Ȃ����́A���̑���͖����ł��B","�G���[",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        */
        return;
      }

      CodeCheck(null, null);
    }
    //--------------------------------------------------------------------------
    // �R�[�h�`�F�b�N(�c�[���o�[�p)
    //--------------------------------------------------------------------------
    else if (command.equals("CodeCheck2") ){
      if (project == null ) {
        Object[] options = { "OK" };
        JOptionPane.showOptionDialog(this,
            getBilingualMsg("0193"),getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        /*
        JOptionPane.showOptionDialog(this,
            "�v���W�F�N�g���J����Ă��Ȃ����́A���̑���͖����ł��B","�G���[",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        */
        return;
      }

      String name = "";
      String FileKind = "";
      try {
        name = desktop.getSelectedFrame().getTitle();
        FileKind = desktop.getSelectedFrame().getName();
      }
      catch (Exception ee ) {
        return;
      }

      Editor edit = (Editor)htEditor.get(name);
      String filename = edit.sFilePath;
      CodeCheck(filename, FileKind);
      //CodeCheck(name);

    }
    //--------------------------------------------------------------------------
    // �R�[�h�`�F�b�N�̌��ʕ\�����X�g�̃N���A
    //--------------------------------------------------------------------------
    else if (command.equals("CodeCheckResultClear") ) {
      vecCodeCheckResultStr.clear();
      vecCodeCheckResult.clear();
      vecCodeCheckFile.clear();
      vecCodeCheckFile2.clear();
      vecCodeCheckErrRow.clear();
      List.removeAll();
      List.repaint();
    }
    //--------------------------------------------------------------------------
    // �O���c�[���\���_�C�A���O�̕\��
    //--------------------------------------------------------------------------
    else if (command.equals("SetOutsideTool")) {
      SetOutsideTool();
    }
    //--------------------------------------------------------------------------
    // �O���c�[���̎��s
    //--------------------------------------------------------------------------
    else if (command.startsWith("OutsideTool_")) {
      ExecOutsideTool (command );
    }
    //--------------------------------------------------------------------------
    // Idea�I��
    //--------------------------------------------------------------------------
    else if (command.equals("Exit")) {
      SystemExit();
    }
    //--------------------------------------------------------------------------
    // �t�H���_�ǉ�
    //--------------------------------------------------------------------------
    else if (command.equals("AddFolder") ) {
      AddFolderDialog dlg = new AddFolderDialog(this,getBilingualMsg("0204"),true, project, SelectedFolederPath);
      //dlg.show();
      dlg.setVisible(true);
      int a = 1;
    }
    //--------------------------------------------------------------------------
    // Dash�t�@�C���֘A�ݒ�
    //--------------------------------------------------------------------------
    else if (command.equals("SetupDash-Bp-Relation") ) {
      TreePath path[] = getSelectPaths();
      SelectedBpFileName = path[0].getLastPathComponent().toString();
      DirectoryNode node =
              (DirectoryNode) path[0].getLastPathComponent();

      DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();
			String fileNamewithPath = nodeinfo.getPath();
			//if (fileNamewithPath.toLowerCase().endsWith(".rset") ) {
			//	SelectedBpFileName = fileNamewithPath;
			//}
      SetBpOptionDialog dlg = new SetBpOptionDialog(this,getBilingualMsg("0205"), project, SelectedBpFileName, fileNamewithPath);
      //dlg.show();
	    dlg.setVisible(true);
      int a = 1;
    }
    else if (command.equals("Projectproperty") ) {
      ProjectPropertyDialog dlg = new ProjectPropertyDialog (this,getBilingualMsg("0209"), project );
      //dlg.show();
	    dlg.setVisible(true);
    }





  }

  /****************************************************************************
   * �o�b�N�A�b�v�t�@�C���̍쐬
   * @param �o�b�N�A�b�v���쐬����t�@�C���̖���
   * @return �Ȃ�
   ****************************************************************************/
  private void createBackupFile(String filenameWithPath ) {
    File tempfile = new File (project.getProjectPath() + "bak" );
    tempfile.mkdir();

    File f = new File (filenameWithPath );
    String filename = f.getName();

    // �o�b�N�A�b�v�t�H���_�����������āA�o�b�N�A�b�v�t�@�C���ɕt�^����Ă���A�Ԃ̍ő�l���擾����
    String file_list[] = tempfile.list();
    int maxno = 0;
    for (int i=0; i<file_list.length; i++ ) {
      String wkfilename = file_list[i];
      if (wkfilename.indexOf(filename) != -1 ) {
        String no = wkfilename.substring(wkfilename.indexOf("~")+1, wkfilename.lastIndexOf("~"));
        int wkno = new Integer(no).intValue();
        if (wkno > maxno ){
          maxno = wkno;
        }
      }
    }

    // �ύX���e��ۑ��O�Ɍ��݂̃t�@�C���𖼏̂�ς��āi�o�b�N�A�b�v�p�̃t�@�C�����ɕς��āj�A
    // �o�b�N�A�b�v�t�H���_�ɃR�s�[����
    maxno++;
    String orgfile = filenameWithPath;//project.getProjectPath() + filename ;
    String makfile = project.getProjectPath() + "bak" + File.separator +  filename + "~" + new Integer(maxno).toString() + "~";
    String[] copyfiles = {orgfile,makfile};
    CopyFile.filecopy(copyfiles);

  }

  /****************************************************************************
   * �t�@�C���ۑ�
   * @param �t�@�C����
   * @return �Ȃ�
   ****************************************************************************/
  private void fileSave(String filename ) {

    // �o�b�N�A�b�v�t�@�C���̍쐬
    //createBackupFile (filename);

    Editor edit = (Editor)htEditor.get(filename);

    // �o�b�N�A�b�v�t�@�C���̍쐬
    createBackupFile (edit.sFilePath);

    edit.FileSaveDialog();
    edit.jetx.changecnt = 0;
    edit.jetx.jLabel3.setText("");

    File file = new File(edit.sFilePath);
    htFileTimeStampe.put(filename, new Long(file.lastModified()).toString() );

    savemenuitem.setEnabled(false);
    saveBtn.setEnabled(false);

    projectTree1.vecChangeFileName.remove(filename);
    projectTree2.vecChangeFileName.remove(filename);
    projectTree3.vecChangeFileName.remove(filename);
    projectTree4.vecChangeFileName.remove(filename);

    projectTree1.repaint();
    projectTree2.repaint();
    projectTree3.repaint();
    projectTree4.repaint();

    if (projectTree1.vecChangeFileName.size() == 0 ) {
      allsavemenuitem.setEnabled(false);
      allsaveBtn.setEnabled(false);
    }

    JInternalFrame iframe = (JInternalFrame)htEditorFrame.get(filename);
    if (iframe.isSelected() ) {
      iframe.requestFocus();
      edit.jetx.requestFocus();
      edit.jetx.setCaretVisible(true);
    }

  }

  //***************************************************************************
  // Window�����֘A
  //***************************************************************************
  // �����^�C�����\�b�h
  void setTile_H (int optionitems) {
    // �����t���[���̔z����擾����
    JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
    int iframe_index=0;
    for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
      String name = (String)emu.nextElement();
      JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
      iframes[iframe_index] = iframe;
    }

    //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
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
    Insets is = desktop.getInsets();
    // �f�X�N�g�b�v�̓����������߂�
    int width = desktop.getWidth() - is.left - is.right;
    // �f�X�N�g�b�v�̓����������߂�
    int height = desktop.getHeight() - is.top - is.bottom;

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
    //updateWindowMenu ( );

  }

  // �����^�C���\�����\�b�h
  void setTile_V (int optionitems ) {
    // �����t���[���̔z����擾����
    JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
    int iframe_index=0;
    for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
      String name = (String)emu.nextElement();
      JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
      iframes[iframe_index] = iframe;
    }

    //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;

    int itemcount = 0;
    // �A�C�R��������Ă��Ȃ������t���[�����𒲂ׂ�
    for (int i=0; i<iframes.length; i++ ) {
      if (!iframes[i].isIcon() )
        itemcount++;
    }

    // �f�X�N�g�b�v�̓������E��`�����߂�
    Insets is = desktop.getInsets();
    // �f�X�N�g�b�v�̓����������߂�
    int width = desktop.getWidth() - is.left - is.right;
    // �f�X�N�g�b�v�̓����������߂�
    int height = desktop.getHeight() - is.top - is.bottom;

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
    JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
    int iframe_index=0;
    for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
      String name = (String)emu.nextElement();
      JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
      iframes[iframe_index] = iframe;
    }

    //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
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
    Insets is = desktop.getInsets();
    // �f�X�N�g�b�v�̓����������߂�
    int width = desktop.getWidth() - is.left - is.right;
    // �f�X�N�g�b�v�̓����������߂�
    int height = desktop.getHeight() - is.top - is.bottom;

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
    JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
    int iframe_index=0;
    for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
      String name = (String)emu.nextElement();
      JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
      iframes[iframe_index] = iframe;
    }

    //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;

    for (int i=0; i<iframes.length; i++ ) {
      // �����t���[�����A�C�R��������
      try {
        if (iframes[i].isVisible() ) {
          iframes[i].setIcon (true);
        }
      }
      catch (Exception e ) {}
     }
     updateWindowMenu ( );
  }

  // WIndow���j���[�X�V
  private void updateWindowMenu (){
    JMenuBar menubar = this.getJMenuBar();
    JMenu menu = menubar.getMenu(2);
    menu.getMenuComponent(0).setEnabled(false);
    menu.getMenuComponent(1).setEnabled(false);
    menu.getMenuComponent(2).setEnabled(false);
    menu.getMenuComponent(3).setEnabled(false);

    JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
    int iframe_index=0;
    for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
      String name = (String)emu.nextElement();
      JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
      iframes[iframe_index] = iframe;
    }

    //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
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

  /****************************************************************************
   * �G�f�B�^�ɂ���āA�ύX����������Ƃ��Ɏ��s�����
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  public static void TextChanged() {
    savemenuitem.setEnabled(true);
    allsavemenuitem.setEnabled(true);
    saveBtn.setEnabled(true);
    allsaveBtn.setEnabled(true);

    String name = "";
    try {
      name = desktop.getSelectedFrame().getTitle();
    }
    catch (Exception ee ) {
      return;
    }
    Editor edit = (Editor)htEditor.get(name);
    if (edit.jetx.changecnt > 0 ) {
      if (projectTree1.vecChangeFileName.indexOf(name) == -1 ) {
        // �c���[�̕\�����X�V����K�v�����邽�߁A�ύX�t�@�C����m�点��
        projectTree1.vecChangeFileName.addElement(name);
        projectTree2.vecChangeFileName.addElement(name);
        projectTree3.vecChangeFileName.addElement(name);
        projectTree4.vecChangeFileName.addElement(name);
      }
    }
    else {

      FileReader f_in;
      BufferedReader b_in;
      String sAll = "";
      String sLine = "";
      try
      {
        //f_in = new FileReader(project.getProjectPath() + name);
        //b_in = new BufferedReader(f_in);
        b_in = new BufferedReader(new InputStreamReader(
            new FileInputStream(edit.sFilePath),
            "JISAutoDetect"));

        Vector vecLines = new Vector();
        while ((sLine = b_in.readLine()) != null)
        {
          vecLines.addElement(sLine);
        }
        b_in.close();
        for (int i=0; i<vecLines.size(); i++ ) {
          if (i < vecLines.size()-1 ) {
            sAll += (String)vecLines.elementAt(i) + "\n";
          }
          else {
            sAll += (String)vecLines.elementAt(i);
          }
        }

      } catch (Exception ex)
      {
        System.out.println(ex);
      }
      String wk = edit.getTextProf();

      if (!sAll.equals(edit.getTextProf())) {
        edit.jetx.changecnt = 1;
        if (System.getProperty("language").equals("japanese") ) {
          edit.jetx.jLabel3.setText("�ύX����");
        }
        else {
          edit.jetx.jLabel3.setText("changed");
        }
        if (projectTree1.vecChangeFileName.indexOf(name) == -1 ) {
          projectTree1.vecChangeFileName.addElement(name);
          projectTree2.vecChangeFileName.addElement(name);
          projectTree3.vecChangeFileName.addElement(name);
          projectTree4.vecChangeFileName.addElement(name);
          projectTree1.repaint();
          projectTree2.repaint();
          projectTree3.repaint();
          projectTree4.repaint();
        }
        return;
      }


      projectTree1.vecChangeFileName.remove(name);
      projectTree2.vecChangeFileName.remove(name);
      projectTree3.vecChangeFileName.remove(name);
      projectTree4.vecChangeFileName.remove(name);

      savemenuitem.setEnabled(false);
      saveBtn.setEnabled(false);

      boolean ExistsChangeText = false;
      for (Enumeration emu = htEditor.elements(); emu.hasMoreElements(); ) {
        edit = (Editor)emu.nextElement();
        if (edit.jetx.changecnt > 0 ) {
          ExistsChangeText = true;
          break;
        }
      }

      if (!ExistsChangeText ) {
        allsavemenuitem.setEnabled(false);
        allsaveBtn.setEnabled(false);
      }

    }
    projectTree1.repaint();
    projectTree2.repaint();
    projectTree3.repaint();
    projectTree4.repaint();
  }

  /****************************************************************************
   * ????
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  private Vector vecReplaceFiles = null;
  public void setReplaceFiles (Vector vecReplaceFiles ) {
  	this.vecReplaceFiles = vecReplaceFiles;
  }

  /****************************************************************************
   * �v���W�F�N�g�̓ǂݍ���
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  public void readProjectFile(String projectfile ) {

    if (projectfile.equals(" ") ) {
      // �v���W�F�N�g�폜���\�b�h�ideleteProject�j����Ă΂ꂽ�ꍇ
      // �v���W�F�N�g���J�̏�Ԃɂ���
      project = null;

      // �v���W�F�N�g�c���[�������Ă���ύX�t�@�C���������N���A����
      projectTree1.vecChangeFileName.clear();
      projectTree2.vecChangeFileName.clear();
      projectTree3.vecChangeFileName.clear();
      projectTree4.vecChangeFileName.clear();

      // �ۑ��{�^���A�ۑ����j���[�𖳌�������
      savemenuitem.setEnabled(false);
      saveBtn.setEnabled(false);
      allsavemenuitem.setEnabled(false);
      allsaveBtn.setEnabled(false);

      //------------------------------------------------------------------------
      // �v���W�F�N�g�c���[����ʂ�������A�����
      // �v���W�F�N�g���J���Ă��Ȃ��|��`���郁�b�Z�[�W��\������
      //------------------------------------------------------------------------
      // �����E�捞���
      spaneSearchScreen.remove(projectTreeSrlPane1);
      spaneSearchScreen.add(notOpenProjectMsg1);
      spaneSearchScreen.setDividerLocation(150);

      // �J�����
      spaneDevScreen.remove(projectTreeSrlPane2);
      spaneDevScreen.add(notOpenProjectMsg2);
      spaneDevScreen.setDividerLocation(150);

      // ����V�~�����[�g���
      spaneSimulator.remove(projectTreeSrlPane3);
      spaneSimulator.add(notOpenProjectMsg3);
      spaneSimulator.setDividerLocation(150);

      // �o�^���
      spaneSave.remove(projectTreeSrlPane4);
      spaneSave.add(notOpenProjectMsg4);
      spaneSave.setDividerLocation(150);
      projectOpened = false;

      // ����V�~�����[�g��ʂ̃��|�W�g���ƃ��[�N�v���[�X�ɓǂݍ��܂�Ă���Ag���N���A����
      try {
        simulatorPanel.clear_Wp_Rep();
      }
      catch (Exception ex) {}

      // �e��ʂ�project�N���X��n��
      searchPanel.setProject(project);
      simulatorPanel.setProject(project);
      savePanel.setProject(project);

      // �v���W�F�N�g�폜���j���[�𖳌�������
      deletePrjMenuItem.setEnabled(false);

      // �G�f�B�^��S�č폜
      for (Enumeration emu = htEditor.keys(); emu.hasMoreElements(); ) {
        String name = (String)emu.nextElement();
        JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
        desktop.remove(iframe);
      }
      htEditor.clear();
      htFileTimeStampe.clear();
      htEditorFrame.clear();


      //desktop.removeAll();

      // �{���\�b�h�I��
      return;

    }

    // �v���W�F�N�g�폜���j���[��L��������
    deletePrjMenuItem.setEnabled(true);

    if (project == null ) {
      // ���߂ĊJ�����Ƃ��́A�V�K�Ƀv���W�F�N�g�N���X���쐬����
      project = new Project (projectfile );
    }
    else {
      project.createProjectInfo(projectfile);
    }

    //new File(project.getProjectFileNameWithPath() + "java_").mkdirs();

    // ���[���Z�b�g�t�@�C���̓ǂݍ��ݏꏊ���v���W�F�N�g�t�@�C���Ɠ����ꏊ�ɐݒ肷��
    System.getProperties().remove("dash.loadpath");
    System.getProperties().setProperty("dash.loadpath", project.getProjectPath());

    boolean reload = false;

    ProjectTree projectTree = null;
    if (jTabbedPane.getSelectedIndex() == 0 ) {
      projectTree = projectTree1;
    }
    else if (jTabbedPane.getSelectedIndex() == 1 ) {
      projectTree = projectTree2;
    }
    else if (jTabbedPane.getSelectedIndex() == 2 ) {
      projectTree = projectTree3;
    }
    else if (jTabbedPane.getSelectedIndex() == 3 ) {
      projectTree = projectTree4;
    }

    Enumeration orgExpandNodeEmu = projectTree.getExpandedDescendants(projectTree.getPathForRow(0));
    if (PrevProjectFileName.equals(project.getProjectFileNameWithPath())){
      reload = true;
      //------------------------------------------------------------------------
      // ���݁A�J����Ă���v���W�F�N�g�Ɠ����v���W�F�N�g���J���ꂽ�Ƃ�
      // �t�@�C���̍폜���������s���ꂽ�ꍇ�ɂ��̂悤�Ȃ��Ƃ���������
      // �P�D�t�@�C���폜���s���v���W�F�N�g�t�@�C���̓��e����폜�����t�@�C���̋L�q�������A
      //                    �ēx�A�{���\�b�h���Ă�
      // �Q�D�v���W�F�N�g�t�@�C���X�V���ēx�A�����v���W�F�N�g���J��
      // �R�D�t�@�C���̐V�K�쐬���捞
      //------------------------------------------------------------------------
      JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
      int iframe_index=0;
      for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
        String name = (String)emu.nextElement();
        JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
        iframes[iframe_index] = iframe;
      }

      //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();

      // �폜����Ă���t�@�C�������邩���ׂ�
      //
      Vector vecDelFiles = new Vector();
      Vector vecDelFileNames = new Vector();
      for (int i=0; i<iframes.length; i++ ) {
        boolean FindFlag = false;
        for (int j=0; j<project.getFileNames().size(); j++ ) {
          if (iframes[i].getTitle().equals(project.getFileNames().elementAt(j)) ){
            FindFlag = true;
            break;
          }
        }

        if (!FindFlag) {
          vecDelFiles.addElement(new Integer(i).toString());
          vecDelFileNames.addElement(iframes[i].getTitle());
        }
      }

      for (int i=0; i<vecDelFiles.size(); i++ ) {
        //CodeEditorDesktop.remove((JInternalFrame)vecDelFiles.elementAt(i));
        desktop.remove(new Integer((String)vecDelFiles.elementAt(i)).intValue());
        htEditor.remove((String)vecDelFileNames.elementAt(i));
        htFileTimeStampe.remove((String)vecDelFileNames.elementAt(i));

        projectTree1.vecChangeFileName.remove((String)vecDelFileNames.elementAt(i));
        projectTree2.vecChangeFileName.remove((String)vecDelFileNames.elementAt(i));
        projectTree3.vecChangeFileName.remove((String)vecDelFileNames.elementAt(i));
        projectTree4.vecChangeFileName.remove((String)vecDelFileNames.elementAt(i));
      }
      desktop.repaint();

      if (projectTree1.vecChangeFileName.size() == 0 ) {
        allsavemenuitem.setEnabled(false);
        allsaveBtn.setEnabled(false);
      }

      /*�ȉ��̏����́A���������������̂��s���E�E�E�E
      if (vecReplaceFiles != null ) {
      	iframes = CodeEditorDesktop.getAllFrames();
      	Vector vecTmp = new Vector();

      	for (int i=0; i<iframes.length; i++ ) {
        	boolean FindFlag = false;
	        for (int j=0; j<vecReplaceFiles.size(); j++ ) {
	          if (iframes[i].getTitle().equals(vecReplaceFiles.elementAt(j)) ){
	            FindFlag = true;
	            break;
	          }
	        }

	        if (FindFlag) {
	          vecTmp.addElement(new Integer(i).toString());
	        }
      	}
	      for (int i=0; i<vecTmp.size(); i++ ) {
	        //CodeEditorDesktop.remove((JInternalFrame)vecDelFiles.elementAt(i));
	        //CodeEditorDesktop.remove(new Integer((String)vecTmp.elementAt(i)).intValue());
	      }
	      CodeEditorDesktop.repaint();
      }
      */
    }
    else {



      // �ύX����Ă���t�@�C��������ꍇ�A�ۑ��������s��
      if (projectTree1.vecChangeFileName.size() > 0 ) {
        SaveComfirmDialog dlg = new SaveComfirmDialog(this,projectTree1.vecChangeFileName);
        //dlg.show();
	    	dlg.setVisible(true);

        if (dlg.ret == 0 ) {
          project.createProjectInfo(PrevProjectFileName);
          PrevProjectFileName = project.getProjectFileNameWithPath();
          System.getProperties().remove("dash.loadpath");
          System.getProperties().setProperty("dash.loadpath", project.getProjectPath());
          return;
        }

        Vector vecSelectFiles = dlg.getSelectFiles();
        if (vecSelectFiles.size() > 0 ) {
          for (int i=0; i<vecSelectFiles.size(); i++ ) {
            String name = (String)vecSelectFiles.elementAt(i);
            //createBackupFile(name);
            fileSave(name);
          }
        }
      }

      PrevProjectFileName = project.getProjectFileNameWithPath();

      for (Enumeration emu = htEditor.keys(); emu.hasMoreElements(); ) {
        String name = (String)emu.nextElement();
        JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
        desktop.remove(iframe);
      }
      //desktop.removeAll();
      desktop.repaint();

      htEditor.clear();
      htFileTimeStampe.clear();
      htEditorFrame.clear();

      projectTree1.vecChangeFileName.clear();
      projectTree2.vecChangeFileName.clear();
      projectTree3.vecChangeFileName.clear();
      projectTree4.vecChangeFileName.clear();

      savemenuitem.setEnabled(false);
      saveBtn.setEnabled(false);
      allsavemenuitem.setEnabled(false);
      allsaveBtn.setEnabled(false);
    }




    if (!projectTree1.openProjectFile(projectfile) ) {
      if (projectOpened ) {
        spaneSearchScreen.remove(projectTreeSrlPane1);
        spaneSearchScreen.add(notOpenProjectMsg1);
        spaneSearchScreen.setDividerLocation(150);
      }
    }
    else {
      if (!projectOpened ) {
        spaneSearchScreen.remove(notOpenProjectMsg1);
        spaneSearchScreen.add(projectTreeSrlPane1);
        spaneSearchScreen.setDividerLocation(150);
      }
    }

    if (!projectTree2.openProjectFile(projectfile) ) {
      if (projectOpened ) {
        spaneDevScreen.remove(projectTreeSrlPane2);
        spaneDevScreen.add(notOpenProjectMsg2);
        spaneDevScreen.setDividerLocation(150);
      }
    }
    else {
      if (!projectOpened ) {
        spaneDevScreen.remove(notOpenProjectMsg2);
        spaneDevScreen.add(projectTreeSrlPane2);
        spaneDevScreen.setDividerLocation(150);
      }
    }

    if (!projectTree3.openProjectFile(projectfile) ) {
      if (projectOpened ) {
        spaneSimulator.remove(projectTreeSrlPane3);
        spaneSimulator.add(notOpenProjectMsg3);
        spaneSimulator.setDividerLocation(150);
      }
    }
    else {
      if (!projectOpened ) {
        spaneSimulator.remove(notOpenProjectMsg3);
        spaneSimulator.add(projectTreeSrlPane3);
        spaneSimulator.setDividerLocation(150);
      }
    }

    if (!projectTree4.openProjectFile(projectfile) ) {
      if (projectOpened ) {
        spaneSave.remove(projectTreeSrlPane4);
        spaneSave.add(notOpenProjectMsg4);
        spaneSave.setDividerLocation(150);
        projectOpened = false;
      }
    }
    else {
      if (!projectOpened ) {
        spaneSave.remove(notOpenProjectMsg4);
        spaneSave.add(projectTreeSrlPane4);
        spaneSave.setDividerLocation(150);
        projectOpened = true;
      }
    }


    if (reload && orgExpandNodeEmu != null) {
      Vector vecTreePath = new Vector();
      if (dropTargetPath != null ) {
        vecTreePath.addElement (dropTargetPath);
        //System.out.println(dropTargetPath.toString());
      }
      for (; orgExpandNodeEmu.hasMoreElements(); ) {
        TreePath path = (TreePath)orgExpandNodeEmu.nextElement();
        vecTreePath.addElement (path);
      }
      if (dropTargetPath != null ) {
        vecTreePath.addElement (dropTargetPath);
        //System.out.println(dropTargetPath.toString());
      }
      for (int i=0; i<vecTreePath.size() ; i++ ) {
        TreePath path = (TreePath)vecTreePath.elementAt(i);

        DirectoryNode node =
            (DirectoryNode) path.getLastPathComponent();

        DirectoryNodeInfo nodeInfo = (DirectoryNodeInfo)node.getUserObject();
        //System.out.println(nodeInfo.getPath());

        for (int j=0; j<projectTree1.getRowCount(); j++ ) {
          TreePath path2 = (TreePath)projectTree1.getPathForRow(j);
          DirectoryNode node2 =
              (DirectoryNode) path2.getLastPathComponent();
          DirectoryNodeInfo nodeInfo2 = (DirectoryNodeInfo)node2.getUserObject();
          if (nodeInfo.getPath().equals(nodeInfo2.getPath()) ) {
            projectTree1.expandPath(path2);
          }
        }

        for (int j=0; j<projectTree2.getRowCount(); j++ ) {
          TreePath path2 = (TreePath)projectTree2.getPathForRow(j);
          DirectoryNode node2 =
              (DirectoryNode) path2.getLastPathComponent();
          DirectoryNodeInfo nodeInfo2 = (DirectoryNodeInfo)node2.getUserObject();
          if (nodeInfo.getPath().equals(nodeInfo2.getPath()) ) {
            projectTree2.expandPath(path2);
          }
        }

        for (int j=0; j<projectTree3.getRowCount(); j++ ) {
          TreePath path2 = (TreePath)projectTree3.getPathForRow(j);
          DirectoryNode node2 =
              (DirectoryNode) path2.getLastPathComponent();
          DirectoryNodeInfo nodeInfo2 = (DirectoryNodeInfo)node2.getUserObject();
          if (nodeInfo.getPath().equals(nodeInfo2.getPath()) ) {
            projectTree3.expandPath(path2);
          }
        }

        for (int j=0; j<projectTree4.getRowCount(); j++ ) {
          TreePath path2 = (TreePath)projectTree4.getPathForRow(j);
          DirectoryNode node2 =
              (DirectoryNode) path2.getLastPathComponent();
          DirectoryNodeInfo nodeInfo2 = (DirectoryNodeInfo)node2.getUserObject();
          if (nodeInfo.getPath().equals(nodeInfo2.getPath()) ) {
            projectTree4.expandPath(path2);
          }
        }
      }
    }

    //projectTree1.openProjectFile(projectfile);
    //projectTree2.openProjectFile(projectfile);
    //projectTree3.openProjectFile(projectfile);
    //projectTree4.openProjectFile(projectfile);
    try {
      simulatorPanel.clear_Wp_Rep();
    }
    catch (Exception ex) {}
    searchPanel.setProject(project);
    simulatorPanel.setProject(project);
    savePanel.setProject(project);


    //
    String dirpath = "";
    if (!dashdir.toString().endsWith(File.separator)) {
        dirpath = dashdir.toString() + File.separator;
    }
    String FilePath = dirpath + "properties" + File.separator + "session";

    // �v���W�F�N�g�ŊǗ�����t�@�C���̖��̂��擾
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    //�ǂݍ��ݏ���
    Vector vecFileNames = new Vector();
    try {
        f_in = new FileReader(FilePath);
        b_in = new BufferedReader(f_in);
        while((sLine = b_in.readLine()) != null) {

          if (sLine.equals("--DeleteFile--") ) {
            continue;
          }

          File file = new File(sLine);
          if (!file.canRead() || file.isDirectory())
            continue;

          int p = sLine.lastIndexOf('.');
          if (p == -1)
            continue;

          String cname = sLine.substring(0, p);
          //if (!Character.isUpperCase((char)cname.charAt(0)))
          //  continue;

          if (!sLine.toLowerCase().endsWith(".dpx")) {
            continue;
          }

          if (vecFileNames.size()<12 ) {
            vecFileNames.addElement(sLine);
          }
        }
        b_in.close();
        f_in.close();

    } catch(Exception ex) {
    }

    if (vecFileNames.indexOf(projectfile) != -1 ) {
      vecFileNames.remove(vecFileNames.indexOf(projectfile));
    }

    try {
      File fp  = new File ( FilePath );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.println(projectfile);
      for (int i=0; i<vecFileNames.size(); i++ ) {
        if (i > 10 ) {
          break;
        }
        pw.println((String)vecFileNames.elementAt(i));
      }
      pw.close ();
    } catch ( Exception e ){}

    this.setOpenHist(vecFileNames);
    searchPanel.setOpenHist(vecFileNames);
    simulatorPanel.setOpenHist(vecFileNames);
    savePanel.setOpenHist(vecFileNames);

    this.setTitle("IDEA - " + projectfile);

    if (vecReplaceFiles != null ) {
    	vecReplaceFiles.clear();
    	vecReplaceFiles = null;
    }

  }

  /****************************************************************************
   * �v���W�F�N�g�c���[��őI������Ă���t�@�C���̖��̂�Ԃ�
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  public Vector getSelectFiles() {
    Vector vecSelectFiles = new Vector();
    TreePath[] path = null;
    if (jTabbedPane.getSelectedIndex() == 0 ) {
      path = projectTree1.getSelectionPaths();
    }
    else if (jTabbedPane.getSelectedIndex() == 1 ) {
      path = projectTree2.getSelectionPaths();
    }
    else if (jTabbedPane.getSelectedIndex() == 2 ) {
      path = projectTree3.getSelectionPaths();
    }
    else if (jTabbedPane.getSelectedIndex() == 3 ) {
      path = projectTree4.getSelectionPaths();
    }

    if (path == null ) {
      return vecSelectFiles;
    }

    for (int i=0; i<path.length;i++ ) {
      String filename = path[i].getLastPathComponent().toString();
      if (filename.toLowerCase().endsWith(".dash") || filename.toLowerCase().endsWith(".rset")) {
        vecSelectFiles.addElement(filename);
      }
    }

    return vecSelectFiles;
  }
  public TreePath[] getSelectPaths() {
    Vector vecSelectPath = new Vector();
    TreePath[] path = null;
    TreePath[] retpath = null;
    if (jTabbedPane.getSelectedIndex() == 0 ) {
      path = projectTree1.getSelectionPaths();
    }
    else if (jTabbedPane.getSelectedIndex() == 1 ) {
      path = projectTree2.getSelectionPaths();
    }
    else if (jTabbedPane.getSelectedIndex() == 2 ) {
      path = projectTree3.getSelectionPaths();
    }
    else if (jTabbedPane.getSelectedIndex() == 3 ) {
      path = projectTree4.getSelectionPaths();
    }

    if (path == null ) {
      return null;
    }

    for (int i=0; i<path.length;i++ ) {
      DirectoryNode node = (DirectoryNode)path[i].getLastPathComponent();
      DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();
      if (nodeinfo.getKind() > 1 ) {
        vecSelectPath.addElement(path[i]);
      }

      /*
      String filename = path[i].getLastPathComponent().toString();
      if (filename.toLowerCase().endsWith(".dash") || filename.toLowerCase().endsWith(".rset")) {
        vecSelectPath.addElement(path[i]);
      }
      */
    }

    if (vecSelectPath.size() == 0 ) {
      return null;
    }
    retpath = new TreePath[vecSelectPath.size()];
    for (int i=0; i<vecSelectPath.size(); i++ ) {
      //retpath[i] = new TreePath((TreePath)vecSelectPath.elementAt(i));
      retpath[i] = (TreePath)vecSelectPath.elementAt(i);
    }

    return retpath;
  }

  public TreePath[] getSelectPaths2() {
    Vector vecSelectPath = new Vector();
    TreePath[] path = null;
    if (jTabbedPane.getSelectedIndex() == 0 ) {
      path = projectTree1.getSelectionPaths();
    }
    else if (jTabbedPane.getSelectedIndex() == 1 ) {
      path = projectTree2.getSelectionPaths();
    }
    else if (jTabbedPane.getSelectedIndex() == 2 ) {
      path = projectTree3.getSelectionPaths();
    }
    else if (jTabbedPane.getSelectedIndex() == 3 ) {
      path = projectTree4.getSelectionPaths();
    }

    return path;
  }
  /****************************************************************************
   * �|�b�v�A�b�v���j���[���J���B<BR>
   * �G�[�W�F���g���̏�ŊJ�����ꍇ�A���̃G�[�W�F���g��I�����Ă���J���B
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  private void showPopupMenu(MouseEvent e) {
    if (jTabbedPane.getSelectedIndex() == 0 || jTabbedPane.getSelectedIndex() == 2 || jTabbedPane.getSelectedIndex() == 3) {
      // �J���ȊO�̃t�F�[�Y���I�΂�Ă��鎞
      // �R�[�h�`�F�b�N���j���[���B��
      popupMenuItem7.setVisible(false);
      treePopupMenu.getComponent(12).setVisible(false);

      /*
      treePopupMenu.getComponent(0).setVisible(false);
      //treePopupMenu.getComponent(6).setVisible(false);
      //treePopupMenu.getComponent(7).setVisible(false);
      //treePopupMenu.getComponent(8).setVisible(false);
      treePopupMenu.getComponent(11).setVisible(false);
      treePopupMenu.getComponent(12).setVisible(false);
      */
    }
    else if (jTabbedPane.getSelectedIndex() == 1 ) {

      popupMenuItem7.setVisible(true);
      treePopupMenu.getComponent(12).setVisible(true);
      /*
      treePopupMenu.getComponent(0).setVisible(true);
      treePopupMenu.getComponent(6).setVisible(true);
      treePopupMenu.getComponent(7).setVisible(true);
      //treePopupMenu.getComponent(8).setVisible(true);
      treePopupMenu.getComponent(11).setVisible(true);
      treePopupMenu.getComponent(12).setVisible(true);
      */
      JTree tree = (JTree)e.getComponent();
      final TreePath path = projectTree2.getPathForLocation(e.getX(), e.getY());
      if (path != null) {
        Runnable r = new Runnable() {
          public void run() {
            TreePath[] tmppath = projectTree2.getSelectionPaths();
            if (tmppath == null ) {
              projectTree2.setSelectionPath(path);
            }
          }};
        SwingUtilities.invokeLater(r);
      }
    }
    treePopupMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  /****************************************************************************
   * �R�[�h�`�F�b�N���ʕ\�����X�g�p�̃|�b�v�A�b�v���j���[���J��
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  private void showCodeCheckPopupMenu(MouseEvent e) {
    CodeCheckPopupMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  /****************************************************************************
   * �v���W�F�N�g�������̎擾
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  private Vector getOpenHist() {
    //
    String dirpath = "";
    if (!dashdir.toString().endsWith(File.separator)) {
        dirpath = dashdir.toString() + File.separator;
    }
    String FilePath = dirpath + "properties" + File.separator + "session";

    // �v���W�F�N�g�ŊǗ�����t�@�C���̖��̂��擾
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    boolean line1_DeleteFlag = false;
    //�ǂݍ��ݏ���
    Vector vecFileNames = new Vector();
    try {
        f_in = new FileReader(FilePath);
        b_in = new BufferedReader(f_in);
        while((sLine = b_in.readLine()) != null) {

          if (sLine.equals("--DeleteFile--") ) {
            line1_DeleteFlag = true;
            continue;
          }
          File file = new File(sLine);
          if (!file.canRead() || file.isDirectory())
            continue;

          int p = sLine.lastIndexOf('.');
          if (p == -1)
            continue;

          String cname = sLine.substring(0, p);
//           if (!Character.isUpperCase((char)cname.charAt(0)))
//             continue;

          if (!sLine.toLowerCase().endsWith(".dpx")) {
            continue;
          }

          vecFileNames.addElement(sLine);
        }
        b_in.close();
        f_in.close();

    } catch(Exception ex) {}

    if (line1_DeleteFlag ) {
      this.setOpenHist(vecFileNames);
      searchPanel.setOpenHist(vecFileNames);
      simulatorPanel.setOpenHist(vecFileNames);
      savePanel.setOpenHist(vecFileNames);
      vecFileNames.clear();
    }

    return vecFileNames;
  }

  /****************************************************************************
   * �t�@�C���̍폜<BR>
   * mode��0�̏ꍇ�A�v���W�F�N�g����t�@�C�����폜���A�t�@�C���̎��͎̂c��<br>
   * mode��1�̏ꍇ�A�t�@�C���̎��̂�����<br>
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  private void deletefiles(int mode){
    Vector vecSelectFiles = getSelectFiles();
    TreePath[] path = getSelectPaths2();
    /*
    for (int i=0; i<path.length;i++ ) {
      String filename = path[i].getLastPathComponent().toString();
      DirectoryNode node = (DirectoryNode)path[i].getLastPathComponent();
      DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

      if (!nodeinfo.getName().toLowerCase().endsWith(".dpx")) {
        OpenFile(nodeinfo.getName(),nodeinfo.getPath(),0);
      }
    }
    */

    //if (vecSelectFiles.size() == 0 ) {
    if (path.length == 0 ) {
      //JOptionPane.showMessageDialog(this,"�폜����t�@�C�����w�肵�Ă��������B");
      Object[] options = { "OK" };
      JOptionPane.showOptionDialog(this,
          getBilingualMsg("0071"),getBilingualMsg("0129"),
                                 JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                 null, options, options[0]);
      /*
      JOptionPane.showOptionDialog(this,
          "�폜����t�@�C�����w�肵�Ă��������B","�G���[",
                                 JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                 null, options, options[0]);
      */
      return;
    }

    Object[] options = { "OK", "�L�����Z��" };
    options[1] = getBilingualMsg("0126");
    int ret = JOptionPane.showOptionDialog(this,
        getBilingualMsg("0072"), getBilingualMsg("0193"),
        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
        null, options, options[0]);
    /*
    int ret = JOptionPane.showOptionDialog(this,
        "�I������Ă���t�@�C�����폜���Ă�낵���ł����H", "�t�@�C���폜",
        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
        null, options, options[0]);
    */
    int r = ret;

    if (ret == 1 ) {
      return;
    }


    //for (int i=0; i<vecSelectFiles.size(); i++ ) {
    for (int i=0; i<path.length;i++ ) {
      String filename = path[i].getLastPathComponent().toString();
      DirectoryNode node = (DirectoryNode)path[i].getLastPathComponent();
      DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

      int cnt1 = project.getFileNames().size();
      String nam = filename;//(String)vecSelectFiles.elementAt(i);

      if (projectTree1.vecChangeFileName.indexOf(nam) != -1 ) {
        projectTree1.vecChangeFileName.remove(projectTree1.vecChangeFileName.indexOf(nam));
        projectTree2.vecChangeFileName.remove(projectTree2.vecChangeFileName.indexOf(nam));
        projectTree3.vecChangeFileName.remove(projectTree3.vecChangeFileName.indexOf(nam));
        projectTree4.vecChangeFileName.remove(projectTree4.vecChangeFileName.indexOf(nam));
      }


      //int a = project.getFileNames().indexOf((String)vecSelectFiles.elementAt(i));
      //project.getFileNames().remove(project.getFileNames().indexOf((String)vecSelectFiles.elementAt(i)));
      if (mode == 1 ) {
        if (nodeinfo.getKind() <= 1 ) {
          Hashtable htFileInfo = new Hashtable();
          //String p = nodeinfo.getPath();

          //p = nodeinfo.getPath().substring(project.getProjectPath().length());

          File file = new File (nodeinfo.getPath());
          createFileList (file, htFileInfo);
          for (Enumeration emu = htFileInfo.keys(); emu.hasMoreElements(); ) {
            String key = (String)emu.nextElement();// key = filename wjth path
            String s = key;
            file = new File (key);
            file.delete();

          }

          file = new File (nodeinfo.getPath());
          file.delete();

        }
        else if (nodeinfo.getKind() == 4 ) {//Java�t�@�C��
          BufferedReader b_in;
          String sLine = "";
          Vector vecFileName = new Vector();
          String projectpath = project.getProjectPath();
          if (!projectpath.endsWith(File.separator)) {
            projectpath += File.separator;
          }

          try {
            b_in = new BufferedReader(new InputStreamReader(
                new FileInputStream(projectpath + "java_" + File.separator + nodeinfo.getName() + "_inf"),
                "JISAutoDetect"));
            int inf_kind = 0;
            String javafilename = "";
            while ((sLine = b_in.readLine()) != null)
            {
              if (sLine.equals("[path]")) {
                inf_kind = 1;//Path���
              }
              else if (sLine.equals("[relation dash file]")) {
                inf_kind = 2;//�֘ADash�t�@�C�����
              }
              else {
                if (inf_kind == 1 ) {
                  if (sLine.equals("current")) {
                    String s = projectpath + "java_" + File.separator + nodeinfo.getName();
                    File file = new File (projectpath + "java_" + File.separator + nodeinfo.getName() );
                    file.delete();
                  }
                }

              }
            }
            b_in.close();
          } catch ( Exception e ){}

          //File file = new File (projectpath + "java_" + File.separator + nodeinfo.getName() + "_inf");
          //file.delete();

        }
        else {
          //File file = new File (project.getProjectPath() + (String)vecSelectFiles.elementAt(i));
          File file = new File (nodeinfo.getPath());
          file.delete();
        }
      }
    }


    for (int i=0; i<path.length;i++ ) {
      String filename = path[i].getLastPathComponent().toString();
      DirectoryNode node = (DirectoryNode)path[i].getLastPathComponent();
      DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

      int cnt1 = project.getFileNames().size();
      String nam = filename;//(String)vecSelectFiles.elementAt(i);
      if (nodeinfo.getKind() == 4 ) {//Java�t�@�C��
        String projectpath = project.getProjectPath();
        File file = new File (projectpath + "java_" + File.separator + nodeinfo.getName() + "_inf");
        file.delete();

      }

    }

    try {

      BufferedReader b_in;
      String sLine = "";
      Vector vecFileName = new Vector();
      b_in = new BufferedReader(new InputStreamReader(
          new FileInputStream(project.getProjectFileNameWithPath()),
          "JISAutoDetect"));
      while ((sLine = b_in.readLine()) != null)
      {

        boolean findflag = false;
        for (int i=0; i<path.length;i++ ) {
          String filename = path[i].getLastPathComponent().toString();
          DirectoryNode node = (DirectoryNode)path[i].getLastPathComponent();
          DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

          if (nodeinfo.getKind() > 1 ) {
            if ( (sLine.equals(nodeinfo.getName()) || sLine.endsWith(File.separator + nodeinfo.getName()))  ) {
              findflag = true;

            }
          }
        }

        if (!findflag ){
          vecFileName.addElement(sLine);
        }

      }
      b_in.close();


     File fp  = new File ( project.getProjectFileNameWithPath() );
     FileOutputStream fos = new FileOutputStream (fp);
     PrintWriter pw  = new PrintWriter (fos);
     for (int i=0; i<vecFileName.size(); i++ ) {
       pw.println((String)vecFileName.elementAt(i));
     }
     pw.close ();
     fos.close();
     /*
     for (int i=0; i<project.getFileNames().size(); i++ ) {
       pw.println((String)project.getFileNames().elementAt(i));
     }
     */

     // �f�B���N�g�����t�@�C���̍X�V
     boolean directoryDel = false;
     String ProjectPath = project.getProjectPath();
     String directoryInfoFile = "directoryinfo";
     String ProjectName = project.getProjectFileName();
     directoryInfoFile = ProjectName.substring(0,ProjectName.toLowerCase().lastIndexOf (".dpx")+1) + "directoryinfo";

     Vector vecFolderPath = new Vector();
     sLine = "";
     b_in = new BufferedReader(new InputStreamReader(
         new FileInputStream(ProjectPath + directoryInfoFile),
         "JISAutoDetect"));
     while ((sLine = b_in.readLine()) != null)
     {

       for (int i=0; i<path.length;i++ ) {
         String filename = path[i].getLastPathComponent().toString();
         DirectoryNode node = (DirectoryNode)path[i].getLastPathComponent();
         DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

         if (nodeinfo.getKind() <= 1 ) {
           directoryDel = true;
           String p = nodeinfo.getPath().substring(project.getProjectPath().length());
           if (!sLine.equals(p) ) {
             vecFolderPath.addElement(sLine);
           }
         }
       }
     }
     b_in.close();

     if (directoryDel ) {
       fp  = new File ( ProjectPath + directoryInfoFile );
       fos = new FileOutputStream (fp);
       pw  = new PrintWriter (fos);
       for (int i=0; i<vecFolderPath.size(); i++ ) {
         pw.println((String)vecFolderPath.elementAt(i));
       }
       pw.close ();
       fos.close();
     }

     // java_inf�̍X�V
     File current_dir = new File(project.getProjectPath() + "java_" );
     String file_list[] = current_dir.list();
     Hashtable htFileInfo = new Hashtable();
     createFileList (current_dir, htFileInfo);
     for (Enumeration emu = htFileInfo.keys(); emu.hasMoreElements(); ) {
       String key = (String)emu.nextElement();// key = filename wjth path
       String s = key;
       String parentpath = key.substring(0,key.lastIndexOf(File.separator)+1);
       if (!parentpath.endsWith(File.separator) ) {
         parentpath += File.separator;
       }
       if (key.endsWith("java_inf") && parentpath.equals(project.getProjectPath() + "java_" + File.separator)) {
         vecFileName.clear();
         b_in = new BufferedReader(new InputStreamReader(
             new FileInputStream(key),
             "JISAutoDetect"));
         while ((sLine = b_in.readLine()) != null)
         {
           for (int j=0; j<path.length;j++ ) {
             String filename = path[j].getLastPathComponent().toString();
             DirectoryNode node = (DirectoryNode)path[j].getLastPathComponent();
             DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();
             if (!sLine.equals(nodeinfo.getName())  ) {
               vecFileName.addElement(sLine);
             }

           }
         }
         b_in.close();


        fp  = new File ( key );
        fos = new FileOutputStream (fp);
        pw  = new PrintWriter (fos);
        for (int j=0; j<vecFileName.size(); j++ ) {
          pw.println((String)vecFileName.elementAt(j));
        }
        pw.close ();
        fos.close();
       }
     }



     //pw.close ();
     //fos.close();
   } catch ( Exception e ){}





   readProjectFile(project.getProjectFileNameWithPath());


  }

  /****************************************************************************
   * �J���Ȃ������j���[�̍X�V
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  private void setOpenHist( Vector histlist ){

    if(histlist.size() <= 0){
      openagainprjmenu.removeAll();
      openagainprjPopupMenu.removeAll();
      openagainprjmenu.setEnabled(false);
      openagainprjToggleButton.setEnabled(false);
    }
    else{
      openagainprjmenu.removeAll();
      openagainprjmenu.setEnabled(true);
      openagainprjToggleButton.setEnabled(true);

      openagainprjPopupMenu.removeAll();
      for(int i=0; i < histlist.size(); i++){
        String addname = (String)histlist.elementAt(i);

        //�T�u���j���[
        openagainprjmenu.add(menuItem(addname, "againPrjct", null));

        //�|�b�v�A�b�v���j���[
        openagainprjPopupMenu.add(menuItem(addname, "againPrjct", null));

      }
      //�T�u���j���[
      //�v���W�F�N�g�����̍폜
      deleteProjectHistMenuItem1 = menuItem(getBilingualMsg("0036") , "deletehist", null);
      deleteProjectHistMenuItem2 = menuItem(getBilingualMsg("0036") , "deletehist", null);
      openagainprjmenu.add(deleteProjectHistMenuItem1);
      //�|�b�v�A�b�v���j���[
      openagainprjPopupMenu.add(deleteProjectHistMenuItem2);
    }

  }


  /****************************************************************************
   * Idea�I��
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  public void SystemExit() {

    if (project != null ) {
      PrevProjectFileName = project.getProjectFileNameWithPath();

      JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
      int iframe_index=0;
      for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
        String name = (String)emu.nextElement();
        JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
        iframes[iframe_index] = iframe;
      }

      //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
      String name = "";
      Editor edit = null;
      boolean ExistsChnageFile = false;
      for (int i=0; i<iframes.length; i++ ) {
        if (iframes[i].isSelected() ) {
          name = iframes[i].getTitle();
          edit = (Editor)htEditor.get(name);
          if (edit != null ) {
            if (edit.jetx.changecnt > 0 ) {
              ExistsChnageFile = true;
              break;
            }
          }
        }
      }



      if (projectTree1.vecChangeFileName.size() > 0 ) {
        SaveComfirmDialog dlg = new SaveComfirmDialog(this,projectTree1.vecChangeFileName);
        //dlg.show();
    		dlg.setVisible(true);

        if (dlg.ret == 0 ) {
          return;
        }

        Vector vecSelectFiles = dlg.getSelectFiles();
        if (vecSelectFiles.size() > 0 ) {
          for (int i=0; i<vecSelectFiles.size(); i++ ) {
            name = (String)vecSelectFiles.elementAt(i);
            //createBackupFile(name);
            fileSave(name);
          }
        }
        /*
        Object[] options = { "YES", "NO", "�L�����Z��" };

        int ret = JOptionPane.showOptionDialog(this,
            "�ҏW����Ă���t�@�C�������݂��܂��B\n�ۑ����Ă�낵���ł����H", "�t�@�C���ۑ�",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                                   null, options, options[0]);

        int r = ret;
        if (ret == 2 ) {
          return;
        }
        if (ret != 1 ) {
          Vector vecwk = (Vector)projectTree1.vecChangeFileName.clone();
          for (int i=0; i<vecwk.size(); i++ ) {
            name = (String)vecwk.elementAt(i);
            createBackupFile(name);
            fileSave(name);
          }
        }
        */
      }
    }
    simulatorPanel.getRepDVM().finalizeDVM();
    simulatorPanel.getWpDVM(0).finalizeDVM();
    simulatorPanel.getWpDVM(1).finalizeDVM();
    simulatorPanel.getWpDVM(2).finalizeDVM();
    simulatorPanel.getWpDVM(3).finalizeDVM();
    simulatorPanel.getWpDVM(4).finalizeDVM();
    this.dispose();
    System.exit(0);

  }

  /****************************************************************************
   * �v���W�F�N�g�t�@�C�����J���_�C�A���O�̕\��
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  public void openprojectfile(){
    searchPanel.openprojectfile();
 }

 /****************************************************************************
  * �R�[�h�`�F�b�N
  * @param�@String CheckFileName �`�F�b�N����t�@�C�����@�܂���null
  * @return �Ȃ�
  * �c�[���o�[������s���ꂽ�ꍇ�́A�t�H�[�J�X������t�@�C����<br>
  * �v���W�F�N�g�c���[������s���ꂽ�ꍇ�́Anull
  ****************************************************************************/
 private void CodeCheck(String CheckFileName, String FileKind) {
   vecCodeCheckResultStr.clear();
   vecCodeCheckResult.clear();
   vecCodeCheckFile.clear();
   vecCodeCheckFile2.clear();
   vecCodeCheckErrRow.clear();

   Vector vecSelDashFileNames = new Vector();
   Vector vecSelDashFileNames_with_path = new Vector();
   Vector vecSelBpFileNames = new Vector();
   Vector vecSelBpFileNames_with_path = new Vector();
   TreePath[] path = null;
   if (CheckFileName == null ) {
     // �v���W�F�N�g�c���[������s���ꂽ��
     path = getSelectPaths();
     if (path.length == 0 ) {

       // �I������Ă���t�@�C�����[���̂Ƃ��A�S�t�@�C�����`�F�b�N�ΏۂɂȂ�
       TreePath[] pathAr = projectTree2.getSelectionPaths();
       String name = pathAr[0].getLastPathComponent().toString();
       if (name.toLowerCase().endsWith(".dpx")) {
         vecSelDashFileNames.clear();
         int rowcount = projectTree2.getRowCount();
         for (int i=0; i<rowcount; i++ ) {
           TreePath path_ = projectTree2.getPathForRow(i);

           DirectoryNode dn = (DirectoryNode)path_.getLastPathComponent();
           DirectoryNodeInfo dni = (DirectoryNodeInfo)dn.getUserObject();
           if (dni.getKind() == 2 || dni.getKind() == 3 ) {
             vecSelDashFileNames.addElement(dni.getName());
             vecSelDashFileNames_with_path.addElement(dni.getPath());
           }
           else if (dni.getKind() == 4 ) {
             vecSelDashFileNames.addElement(dni.getName());
           }

           /*
           String filename = path.getLastPathComponent().toString();
           if (filename.toLowerCase().endsWith(".dash") || filename.toLowerCase().endsWith(".rset")) {
             vecSelectFiles.addElement(filename);
           }
           */
         }
       }
     }
     else {
       for (int i=0; i<path.length;i++ ) {
         String filename = path[i].getLastPathComponent().toString();
         DirectoryNode dn = (DirectoryNode)path[i].getLastPathComponent();
         DirectoryNodeInfo dni = (DirectoryNodeInfo)dn.getUserObject();

         if (dni.getKind() == 2 || dni.getKind() == 3 ) {
           vecSelDashFileNames.addElement(dni.getName());
           vecSelDashFileNames_with_path.addElement(dni.getPath());
         }
         else if (dni.getKind() == 4 ) {
           vecSelBpFileNames.addElement(dni.getName());
           vecSelBpFileNames_with_path.addElement(dni.getPath());
         }
       }
     }
   }
   else {
     // �c�[���o�[������s���ꂽ�ꍇ�́A�p�����[�^�[�Ŏw�肳�ꂽ�t�@�C���̂݃`�F�b�N����
     //vecSelectDashFiles = new Vector();
     if (FileKind.equals("Dash") || FileKind.equals("RSet") ) {
	     vecSelDashFileNames.addElement(new File(CheckFileName).getName());
	     vecSelDashFileNames_with_path.addElement(CheckFileName);
     }
     else if (FileKind.equals("Java") ) {
	     vecSelBpFileNames.addElement(new File(CheckFileName).getName());
       vecSelBpFileNames_with_path.addElement(CheckFileName);
     }
     else if (FileKind.equals("Etc") ) {
     	return;
     }

   }


   JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
   int iframe_index=0;
   for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
     String name = (String)emu.nextElement();
     JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
     iframes[iframe_index] = iframe;
   }

   //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();

   for (int i=0; i<vecSelDashFileNames.size(); i++ ) {

     //-------------------------------------------------------------------------
     // �`�F�b�N����ɂ́A�t�@�C���̓��e���p�[�T�[�ɓn���K�v�����邽�߁A
     // ���Ƀt�@�C�����J����Ă���A�ҏW���������Ă���ꍇ�i���ҏW���܂ށj�́A���̓��e��
     // �p�[�T�[�ɓn���B
     // ���G�f�B�^���J����Ă���ꍇ�A�����ŕ\�����Ă�����e�́A�G�f�B�^�N���X���擾�ł���
     // �t�ɁA�J����Ă��Ȃ��ꍇ�́A�t�@�C�����璼�ړ��e���擾����
     //-------------------------------------------------------------------------

     // �`�F�b�N����t�@�C�����J����Ă��邩�ǂ������ׂ�
     String name = "";
     String Code = "";
     for (int j=0; j<iframes.length; j++ ) {
       if (iframes[j].getTitle().equals((String)vecSelDashFileNames.elementAt(i)) ) {
         name = iframes[j].getTitle();
         break;
       }
     }

     if (name.equals("") ) {
       // �t�@�C�����J����Ă��Ȃ���
       FileReader f_in;
       BufferedReader b_in;
       String sAll = "";
       String sLine = "";

       //�ǂݍ��ݏ���
       try {
           //f_in = new FileReader(project.getProjectPath() + (String)vecSelDashFileNames.elementAt(i));
           //b_in = new BufferedReader(f_in);
           b_in = new BufferedReader(new InputStreamReader(
               new FileInputStream((String)vecSelDashFileNames_with_path.elementAt(i)),
               "JISAutoDetect"));

           while((sLine = b_in.readLine()) != null) {
               sAll += sLine + "\n";
           }
           b_in.close();

       } catch(Exception ex) {
           System.out.println(ex);
           //printErrln(ex.getMessage());
           return ;
       }

       name = (String)vecSelDashFileNames.elementAt(i);
       Code = sAll;

     }
     else{
       // �t�@�C�����J����Ă��鎞
       Editor edit = (Editor)htEditor.get(name);
       Code = edit.getTextProf();
     }

     // �t�@�C�����Ɠ��e��n���āA�`�F�b�N����
     CodeCheck_Sub (name, Code, vecSelDashFileNames, vecSelDashFileNames_with_path,(String)vecSelDashFileNames_with_path.elementAt(i) );
   }


	/*
	System.getProperties().list(System.out);
			String java_home = System.getProperty("java.home",".");
	System.out.println(java_home);
	String line;
	try {
		String[] cmds = {"/bin/sh", "-c", "echo $PATH"};
		Process proc = Runtime.getRuntime().exec(cmds);
		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while ((line = br.readLine()) != null) {
			// ���ϐ�PATH�̒l��ǂݏo���ė��p����
			System.out.println(line);
		}
	}
	catch (java.io.IOException ex){}
	*/

   if (vecCodeCheckResult.size() == 0 ) {
     vecCodeCheckResultStr.addElement(getBilingualMsg("0195"));
     vecCodeCheckResult.addElement("NO ERROR");
     vecCodeCheckFile.addElement("");
     vecCodeCheckFile2.addElement("");
     vecCodeCheckErrRow.addElement("0");
   }

   for (int i=0; i<vecSelBpFileNames.size(); i++ ) {
     System.setProperty("JavaCompile", "on");

     stdoutStream.reset();
     stderrStream.reset();


     //stdoutStream = new ByteArrayOutputStream();
     //stdoutPrintStream = new PrintStream(stdoutStream);
     System.setOut(stdoutPrintStream);

     //stderrStream = new ByteArrayOutputStream();
     //stderrPrintStream = new PrintStream(stderrStream);
     System.setErr(stderrPrintStream);

    //System.out.println("SetOutsideTool");
    //String s = stdoutStream.toString();

    try {
      String ClassDir = (String)System.getProperty("BpOutputPath");
      if (ClassDir.equals("current") ) {
        ClassDir = project.getProjectPath() + "java_";
      }

      /*
      String[] args = new String[3];
      args[0] = "-classpath=" + dashdir + File.separator +  "Dash.jar";
      args[1] = "-d " + ClassDir;
      args[2] = (String)vecSelBpFileNames_with_path.elementAt(i) ;
      */
      // �����̃t�@�C�����J����Ă��邩�`�F�b�N����
      String name = "";
      for (int j=0; j<iframes.length; j++ ) {
        if (iframes[j].getTitle().equals((String)vecSelBpFileNames.elementAt(i)) ) {
          name = iframes[j].getTitle();
          break;
        }
      }

      String[] args = new String[6];


      args[0] = "-deprecation";
      args[1] = "-d";
      args[2] = ClassDir;
      args[3] = "-classpath";
      args[4] = dashdir + File.separator +  "Dash.jar";
 
      //uchiya
      //�N���X�p�X�̊g��
    	//System.err.println(dvm.getUserClasspath());
			String addclasspath="";
			String[] classpath = dvm.getUserClasspath();
			for(int a=0;a<classpath.length;a++){
				addclasspath= addclasspath+";"+classpath[a];
			}
			args[4]= args[4]+addclasspath;
			//System.err.println("classpath="+args[4]);
			
			
      if (name.equals("" ) ) {
        args[5] = (String)vecSelBpFileNames_with_path.elementAt(i) ;
      }
      else {
        Editor edit = (Editor)htEditor.get(name);
        String Code = edit.getTextProf();
        if(edit.jetx.changecnt == 0 ) {
          args[5] = (String)vecSelBpFileNames_with_path.elementAt(i) ;
        }
        else {
          new File(dashdir + File.separator + "temp").mkdirs();
          File fp  = new File (dashdir + File.separator + "temp" + File.separator + name );
          FileOutputStream fos = new FileOutputStream (fp);
          PrintWriter pw  = new PrintWriter (fos);

          pw.println(Code);
          pw.close ();
          args[5] = dashdir + File.separator + "temp" + File.separator + name;
        }
      }

      String filepath = args[5].substring(0,args[5].lastIndexOf(File.separator));
      if (!filepath.endsWith(File.separator) ) {
        filepath += File.separator;
      }

/*
      String[] args = {"classpath=" + dashdir + File.separator +  "Dash.jar",
        "-d=" + ClassDir,
        (String)vecSelBpFileNames_with_path.elementAt(i)};
    */
      //com.sun.tools.javac.Main.compile(args);
      //����� Java �̃\�[�X���R���p�C���ł���̂ł����Acom.sun.tools.javac.Main �N���X���g���ɂ�
      //CLASSPATH ��ݒ肷��K�v������܂��B
      //com.sun.tools.javac.Main �N���X�� J2SDK ���C���X�g�[�������f�B���N�g���̒����ɂ���
      //lib �f�B���N�g���� tools.jar �Ɋ܂܂�Ă��܂��B�ł�����A���� Jar �t�@/�C���� CLASSPATH �Ɋ܂߂�K�v������܂��B

		/*
        File baseDir = (new File( "C:\\j2sdk1.4.1_01\\lib\\tools.jar" )).getCanonicalFile();
        //System.out.println( "File: " + baseDir );
        URL baseURL = baseDir.toURL();
        //System.out.println( "URL : " + baseURL );
        URLClassLoader loader = new URLClassLoader( new URL[]{baseURL} );
        Class c = loader.loadClass( "com.sun.tools.javac.Main" );
    */
      //Commandline cmd = new Commandline();
        Class c = Class.forName ("com.sun.tools.javac.Main");
        Object compiler = c.newInstance ();
        Method compile = c.getMethod ("compile",
            new Class [] {(new String [] {}).getClass ()});
        System.setErr(stderrPrintStream);
        int result = ((Integer) compile.invoke
                      (compiler, new Object[] {args}))
            .intValue ();

       // return (result == MODERN_COMPILER_SUCCESS);
        String s2 = stdoutStream.toString();
        String s3 = stderrStream.toString();
        String s4= "";
        System.setProperty("JavaCompile", "off");

        if (!s3.equals("") ) {
          while (true ) {
            int pos = vecCodeCheckResult.indexOf("NO ERROR");
            if (pos == -1) {
              break;
            }
            vecCodeCheckResultStr.remove(pos);
            vecCodeCheckResult.remove(pos);
            vecCodeCheckFile.remove(pos);
            vecCodeCheckFile2.remove(pos);
            vecCodeCheckErrRow.remove(pos);
          }
          //vecCodeCheckResultStr.addElement("[" + getBilingualMsg("0129")+"]" + s3 );

          String ErrRow = "0";
          StringTokenizer st = new StringTokenizer(s3,"\r\n");
          int cnt = 0;
          while (st.hasMoreTokens()) {
            String data = st.nextToken();
            if (data.equals("") ){
              continue;
            }

            if (data.indexOf(filepath) != -1 ) {
              data = data.substring(filepath.length());
            }
            vecCodeCheckResultStr.addElement(data );
            if (result == 1 ) {
              vecCodeCheckResult.addElement("ERROR");
            }
            else {
              vecCodeCheckResult.addElement("WARNING");
            }
            vecCodeCheckFile.addElement(vecSelBpFileNames.elementAt(i));
            vecCodeCheckFile2.addElement(vecSelBpFileNames_with_path.elementAt(i));


            if (data.indexOf(":") != -1 ) {
              try {
                data = data.substring(data.indexOf(":")+1);
                ErrRow = data.substring(0,data.indexOf(":"));
              } catch (Exception ex) {

              }


            }
            vecCodeCheckErrRow.addElement(ErrRow);
          }

          //vecCodeCheckResultStr.addElement(s3 );
          //vecCodeCheckResult.addElement("ERROR");
          //vecCodeCheckFile.addElement(vecSelBpFileNames.elementAt(i));
          //vecCodeCheckErrRow.addElement("0");

          // ���ʕ\�����Ɍ��ʂ�\������
          List.setListData(vecCodeCheckResultStr);
          List.ensureIndexIsVisible(vecCodeCheckResultStr.size()-1);

        }
        else {
          // �R���p�C���G���[(�@�L�T`)�@��

        }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("�R���p�C���G���[");
      String s = ex.getMessage();
      String ss = s;
      //  if (ex instanceof BuildException) {
      //      throw (BuildException) ex;
      //  } else {
      //      throw new BuildException("Error starting modern compiler",
      //                               ex, location);
      //  }
    }

   }

   int pos = vecCodeCheckResult.indexOf("NO ERROR");
   if (pos != -1) {
     // ���ʕ\�����Ɍ��ʂ�\������
     List.setListData(vecCodeCheckResultStr);
     List.ensureIndexIsVisible(vecCodeCheckResultStr.size()-1);
   }


 }

 /****************************************************************************
  * �R�[�h�`�F�b�N�i�T�u�j
  * @param�@String AgentName
  * @param�@String Code
  * @param�@Vector vecSelectFiles
  * @return �Ȃ�
  ****************************************************************************/
 private Vector vecCodeCheckResult = new Vector();
 private Vector vecCodeCheckResultStr = new Vector();
 private Vector vecCodeCheckFile = new Vector();
 private Vector vecCodeCheckFile2 = new Vector();
 private Vector vecCodeCheckErrRow = new Vector();
 private boolean CodeCheck_Sub(String AgentName, String Code, Vector vecSelDashFileNames, Vector vecSelDashFileNames_with_path,String AgentName_with_path ){
   try {
     Parser parser = new Parser(AgentName, Code, false);
     if (AgentName.toLowerCase().endsWith(".dash")) {
       String dirpathstr = "";
       if (!dashdir.toString().endsWith(File.separator)) {
         dirpathstr = dashdir.toString() + File.separator;
       }
       else {
         dirpathstr = dashdir.toString() + File.separator;
       }
       dirpathstr  = project.getProjectPath();//  += "scripts" + File.separator;

       // ���݂̃v���W�F�N�g�p�X��ݒ肷��
       // ��͂���R�[�h�����[���Z�b�g���C���N���[�h���Ă���ꍇ�A
       // parse���\�b�h�́A���̃C���N���[�h�t�@�C����ǂݍ��ނ��A
       // ���̓ǂݍ��ݏ������v���W�F�N�g�̃t�H���_�Ɍ��肷�邽�߂Ɏw�肷��
       parser.setDefaultDir(dirpathstr);
       // ��͎��s
       // �����ŃG���[�����������ꍇ�A[catch1]�ɔ��
       parser.parse();
       // �C���N���[�h�t�@�C�������擾
       Vector vecIncudeFileName = parser.getIncludeFileNames();
       Vector vecIncludesErrorMsg = parser.getIncludesErrorMsg();
       Vector vecIncludesErrorLineNo = parser.getIncludesErrorLineNo();
       if (vecIncudeFileName.size() > 0 ) {
         // ���[���Z�b�g���C���N���[�h���Ă���ꍇ
         // ���[���Z�b�g�t�@�C���ɃG���[���Ȃ������ׂ�
         // �����ŁA��������G���[�́A�u���[���Z�b�g�t�@�C����������Ȃ��v�Ƃ������G���[
         // �ł���B�G���[�����������ꍇ�Aparser.parse();��getIncludesErrorMsg���\�b�h��
         // �G���[���b�Z�[�W���i�[�����x�N�^�[��Ԃ�
         // �`�F�b�N����R�[�h���Q���[���Z�b�g���C���N���[�h���Ă���ꍇ�A
         // parser.parse()����擾����R�̃x�N�^�[��
         // �S�ăT�C�Y���Q�ɂȂ�A
         // vecIncudeFileName:�C���N���[�h�t�@�C����
         // vecIncludesErrorMsg:�C���N���[�h�t�@�C���̃G���[���b�Z�[�W
         //�@�@�@�@�@�@�@�@�@�@�@�@�G���[���Ȃ��ꍇ�̓u�����N
         // vecIncludesErrorLineNo:�G���[���������ꍇ�A�`�F�b�N�����R�[�h��
         //                       �G���[�̂������A�C���N���[�h���߂̏����Ă���s�ԍ�
         // �ƂȂ�
         for (int i=0; i<vecIncludesErrorMsg.size(); i++ ) {
           if (!((String)vecIncludesErrorMsg.elementAt(i)).equals("") ) {
             // �G���[���������ꍇ
             try {
               // �����̏����́A�K�v�Ȃ�����
               while (true ) {
                 int pos = vecCodeCheckFile.indexOf(AgentName);
                 if (pos == -1 ) {
                   break;
                 }
                 vecCodeCheckResultStr.remove(pos);
                 vecCodeCheckResult.remove(pos);
                 vecCodeCheckFile.remove(pos);
                 vecCodeCheckFile2.remove(pos);
                 vecCodeCheckErrRow.remove(pos);

               }

               // �R�[�h�`�F�b�N�ŃG���[���Ȃ������t�@�C���̏�������
               // �����t�@�C���̃R�[�h�`�F�b�N���s���A�����ꂩ�̃t�@�C���ɃG���[�����������ꍇ
               // ����ȃt�@�C���͖������A�G���[�t�@�C���̏ڍׂ݂̂����ʕ\�����ɕ\�����邽��
               // NO ERROR�̃f�[�^�͏����Ă���
               while (true ) {
                 int pos = vecCodeCheckResult.indexOf("NO ERROR");
                 if (pos == -1) {
                   break;
                 }
                 vecCodeCheckResultStr.remove(pos);
                 vecCodeCheckResult.remove(pos);
                 vecCodeCheckFile.remove(pos);
                 vecCodeCheckFile2.remove(pos);
                 vecCodeCheckErrRow.remove(pos);
               }
               vecCodeCheckResultStr.addElement("[" + getBilingualMsg("0129")+"]" + AgentName + ":" + (String)vecIncludesErrorMsg.elementAt(i) + getBilingualMsg("0194") + "�F" + (String)vecIncludesErrorLineNo.elementAt(i) );
               vecCodeCheckResult.addElement("ERROR");
               vecCodeCheckFile.addElement(AgentName);
               vecCodeCheckFile2.addElement(AgentName_with_path);
               vecCodeCheckErrRow.addElement((String)vecIncludesErrorLineNo.elementAt(i));

               // ���ʕ\�����Ɍ��ʂ�\������
               List.setListData(vecCodeCheckResultStr);
               List.ensureIndexIsVisible(vecCodeCheckResultStr.size()-1);
               return false;
             }
             catch (Exception e) {}

           }
           else {
             // ���[���Z�b�g�̃G���[���Ȃ������ꍇ
             // CodeCheck���\�b�h����n���ꂽvecSelectFiles�Ƀ��[���Z�b�g�̃t�@�C������
             // �i�[����
             // ���ʁAvecSelectFiles�̗v�f�������邱�ƂɂȂ�̂ŁA
             // CodeCheck���\�b�h�́A���̕��]�v��CodeCheck_Sub���\�b�h�i�{���\�b�h�j��
             // �ĂԂ��ƂɂȂ�
             vecSelDashFileNames.addElement( new File((String)vecIncudeFileName.elementAt(i)).getName());
             vecSelDashFileNames_with_path.addElement((String)vecIncudeFileName.elementAt(i));
           }
         }

       }

     }
     else {
       // �`�F�b�N�Ώۂ��A���[���Z�b�g�t�@�C���̏ꍇ
       parser.parseRuleset();
     }

     //Vector vecIncudeFileName = parser.getIncludeFileNames();
     //for (int i=0; i<vecIncudeFileName.size(); i++ ) {
     //  System.out.println(vecIncudeFileName.elementAt(i));
     //}

     try {

       // �����t�@�C���̃`�F�b�N���s���A�S�Đ���̏ꍇ�AvecCodeCheckResult�̃T�C�Y��
       // 1�ɂȂ�B
       // �`�F�b�N�����t�@�C�����u�G���[�͌�����܂���ł����B�v�ƕ\�������ɁA
       // 1�s�����A�u�G���[�͌�����܂���ł����B�v�ƕ\������
       // �Ȃ̂ŁAvecCodeCheckResult�̃T�C�Y���O�̏ꍇ�̂݁A���펞�̏����i�[����
       if (vecCodeCheckResult.size() == 0 ) {
         //vecCodeCheckResultStr.addElement("�G���[�͌�����܂���ł����B");
         vecCodeCheckResultStr.addElement(getBilingualMsg("0195"));
         vecCodeCheckResult.addElement("NO ERROR");
         vecCodeCheckFile.addElement(AgentName);
         vecCodeCheckFile2.addElement(AgentName_with_path);
         vecCodeCheckErrRow.addElement("0");
         // ���ʕ\�����Ɍ��ʂ�\������
         List.setListData(vecCodeCheckResultStr);
         List.ensureIndexIsVisible(vecCodeCheckResultStr.size()-1);
       }

       /*
       while (true ) {
         int pos = vecCodeCheckFile.indexOf(AgentName);
         if (pos == -1 ) {
           break;
         }
         vecCodeCheckResultStr.remove(pos);
         vecCodeCheckResult.remove(pos);
         vecCodeCheckFile.remove(pos);
         vecCodeCheckErrRow.remove(pos);

       }

       while (true ) {
         int pos = vecCodeCheckResult.indexOf("NO ERROR");
         if (pos == -1) {
           break;
         }
         vecCodeCheckResultStr.remove(pos);
         vecCodeCheckResult.remove(pos);
         vecCodeCheckFile.remove(pos);
         vecCodeCheckErrRow.remove(pos);


       }

       if (vecCodeCheckResult.indexOf("ERROR") == -1 ) {
         //vecCodeCheckResultStr.addElement("�G���[�͌�����܂���ł����B");
         vecCodeCheckResultStr.addElement(getBilingualMsg("0195"));
         vecCodeCheckResult.addElement("NO ERROR");
         vecCodeCheckFile.addElement(AgentName);
         vecCodeCheckErrRow.addElement("0");
         List.setListData(vecCodeCheckResultStr);
         List.ensureIndexIsVisible(vecCodeCheckResultStr.size()-1);
       }
       */
     }
     catch (Exception e) {}
     return true;
   }
   catch (SyntaxException ee ) {
     // catch1
     // parse���\�b�h,parseRuleset���\�b�h�ŃR�[�h�̃G���[�����������ꍇ
     try {

       // �����̏����́A�K�v�Ȃ�����
       while (true ) {
         int pos = vecCodeCheckFile.indexOf(AgentName);
         if (pos == -1 ) {
           break;
         }
         vecCodeCheckResultStr.remove(pos);
         vecCodeCheckResult.remove(pos);
         vecCodeCheckFile.remove(pos);
         vecCodeCheckFile2.remove(pos);
         vecCodeCheckErrRow.remove(pos);

       }

       // �R�[�h�`�F�b�N�ŃG���[���Ȃ������t�@�C���̏�������
       // �����t�@�C���̃R�[�h�`�F�b�N���s���A�����ꂩ�̃t�@�C���ɃG���[�����������ꍇ
       // ����ȃt�@�C���͖������A�G���[�t�@�C���̏ڍׂ݂̂����ʕ\�����ɕ\�����邽��
       // NO ERROR�̃f�[�^�͏����Ă���
       while (true ) {
         int pos = vecCodeCheckResult.indexOf("NO ERROR");
         if (pos == -1) {
           break;
         }
         vecCodeCheckResultStr.remove(pos);
         vecCodeCheckResult.remove(pos);
         vecCodeCheckFile.remove(pos);
         vecCodeCheckErrRow.remove(pos);
         vecCodeCheckFile2.remove(pos);
       }
       vecCodeCheckResultStr.addElement("[" + getBilingualMsg("0129")+"]" + AgentName + ":" + ee.comment + getBilingualMsg("0194") + "�F" + ee.lineno );
       vecCodeCheckResult.addElement("ERROR");
       vecCodeCheckFile.addElement(AgentName);
       vecCodeCheckFile2.addElement(AgentName_with_path);
       vecCodeCheckErrRow.addElement(new Integer(ee.lineno).toString());
       // ���ʕ\�����Ɍ��ʂ�\������
       List.setListData(vecCodeCheckResultStr);
       List.ensureIndexIsVisible(vecCodeCheckResultStr.size()-1);

     }
     catch (Exception e) {}
     System.err.println(ee.comment + getBilingualMsg("0194") + "�F" + ee.lineno);
     return false;
   }

  }

  /****************************************************************************
   * �R�[�h�`�F�b�N���ʕ\�����X�g�pCellRenderer
   ****************************************************************************/
  class MyCellRenderer extends JLabel implements ListCellRenderer {
       ImageIcon alermIcon = getImageIcon("resources/alerm.gif");
       ImageIcon alerm2Icon = getImageIcon("resources/alerm2.gif");

       // This is the only method defined by ListCellRenderer.
       // We just reconfigure the JLabel each time we're called.

       public MyCellRenderer(){
         setOpaque(true);
       }
    public Component getListCellRendererComponent(
        JList list,
        Object value,            // value to display
         int index,               // cell index
         boolean isSelected,      // is the cell selected
         boolean cellHasFocus)    // the list and the cell have the focus
       {
          String s = value.toString();
          setText(s);

          if (((String)vecCodeCheckResult.elementAt(index)).equals("ERROR") ) {
            setIcon(alermIcon);
          }
          else {
            setIcon(alerm2Icon);
          }
          //setIcon((s.indexOf("[�G���[]") != -1 || s.indexOf("[Error]") != -1) ? alermIcon : alerm2Icon);
          //setVisible(true);
          if (isSelected) {
            setBackground(list.getSelectionBackground());
            //setForeground((s.indexOf("[�G���[]") != -1) ? Color.red : list.getSelectionForeground());
            if (((String)vecCodeCheckResult.elementAt(index)).equals("ERROR") ) {
              setForeground(Color.red);
            }
            else {
              setForeground(Color.blue);
            }

            //setForeground((s.indexOf("[�G���[]") != -1 || s.indexOf("[Error]") != -1) ? Color.red : Color.blue);
            //setForeground(list.getSelectionForeground());
          }
          else {
           setBackground(list.getBackground());
           //setForeground((s.indexOf("[�G���[]") != -1) ? Color.red : list.getSelectionForeground());
           if (((String)vecCodeCheckResult.elementAt(index)).equals("ERROR") ) {
             setForeground(Color.red);
           }
           else {
             setForeground(Color.blue);
           }

           //setForeground((s.indexOf("[�G���[]") != -1 || s.indexOf("[Error]") != -1 ) ? Color.red : Color.blue);
           //setForeground(list.getForeground());
          }
          setEnabled(list.isEnabled());
          setFont(list.getFont());
          return this;
       }
   }


   /****************************************************************************
    * �v���W�F�N�g�����̍폜
    * @param�@�Ȃ�
    * @return �Ȃ�
    ****************************************************************************/
   public void deleteOpenHist() {
     String dirpath = "";
     if (!dashdir.toString().endsWith(File.separator)) {
       dirpath = dashdir.toString() + File.separator;
     }
     String FilePath = dirpath + "properties" + File.separator + "session";

     // �v���W�F�N�g�ŊǗ�����t�@�C���̖��̂��擾
     FileReader f_in;
     BufferedReader b_in;
     String sLine = "";

     //�ǂݍ��ݏ���
     Vector vecFileNames = new Vector();
     try {
       f_in = new FileReader(FilePath);
       b_in = new BufferedReader(f_in);
       while((sLine = b_in.readLine()) != null) {

         File file = new File(sLine);
         if (!file.canRead() || file.isDirectory())
           continue;

         int p = sLine.lastIndexOf('.');
         if (p == -1)
           continue;

         String cname = sLine.substring(0, p);
         //if (!Character.isUpperCase((char)cname.charAt(0)))
         //  continue;

         if (!sLine.toLowerCase().endsWith(".dpx")) {
           continue;
         }

         vecFileNames.addElement(sLine);
         break;
       }
       b_in.close();
       f_in.close();

     } catch(Exception ex) {}


     try {
       File fp  = new File ( FilePath );
       FileOutputStream fos = new FileOutputStream (fp);
       PrintWriter pw  = new PrintWriter (fos);
       for (int i=0; i<vecFileNames.size(); i++ ) {
         if (i > 10 ) {
           break;
         }
         pw.println((String)vecFileNames.elementAt(i));
       }
       pw.close ();
    } catch ( Exception e ){}

    vecFileNames.clear();
    //vecFileNames.remove(0);
    this.setOpenHist(vecFileNames);
    searchPanel.setOpenHist(vecFileNames);
    simulatorPanel.setOpenHist(vecFileNames);
    savePanel.setOpenHist(vecFileNames);

  }


  /****************************************************************************
   * �O���c�[�����̓ǂݍ���
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void readOutsideToolInfo(){
    // �O���c�[�����̓ǂݍ���
    String dirpath = "";
    if (!dashdir.toString().endsWith(File.separator)) {
        dirpath = dashdir.toString() + File.separator;
    }

    //String FilePath = dirpath + "outsidetool";
    String FilePath = dirpath + "properties" + File.separator +  "outsidetool";
    File wkfile = new File (FilePath);
    if (!wkfile.exists() ){
      String wkFilePath = dirpath + "outsidetool";
      wkfile = new File (wkFilePath);
      if (wkfile.exists() ){
        String[] buf = new String[2];
        buf[0] = dirpath + "outsidetool";
        buf[1] = dirpath + "properties" + File.separator +  "outsidetool";
        searchPanel.filecopy(buf);
        wkfile.delete();
      }
    }


    String[] filenames = new String[4];
    filenames[0] = "contentHist";
    filenames[1] = "perfomativeHist";
    filenames[2] = "toHist";
    filenames[3] = "session";
    for (int i=0; i<filenames.length; i++ ) {
      FilePath = dirpath + "properties" + File.separator +  filenames[i];
      wkfile = new File (FilePath);
      if (!wkfile.exists() ){
        String wkFilePath = dirpath + filenames[i];
        wkfile = new File (wkFilePath);
        if (wkfile.exists() ){
          String[] buf = new String[2];
          buf[0] = dirpath + filenames[i];
          buf[1] = dirpath + "properties" + File.separator +  filenames[i];
          searchPanel.filecopy(buf);
          wkfile.delete();
        }
      }
    }

    FilePath = dirpath + "properties" + File.separator +  "outsidetool";
    // �v���W�F�N�g�ŊǗ�����t�@�C���̖��̂��擾
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    //�ǂݍ��ݏ���
    vecOutsideToolInfo.clear();
    try {
        f_in = new FileReader(FilePath);
        b_in = new BufferedReader(f_in);
        while((sLine = b_in.readLine()) != null) {

          File file = new File(sLine);

          vecOutsideToolInfo.addElement(sLine);
        }
        b_in.close();
        f_in.close();

    } catch(Exception ex) { }

  }

  /****************************************************************************
   * �t�@�C�����J��
   * @param�@String FileName�@�t�@�C����
   * @param�@int DefauleTextLineNo �J�������ɑI����Ԃɂ���s
   * @return �Ȃ�
   ****************************************************************************/
  private void OpenFile(String FileName, String FileNameWithPath, int DefauleTextLineNo, int FileKind) {
    if (FileName.toLowerCase().endsWith(".dpx") ) {
      return;
    }
    Editor edit = (Editor)htEditor.get(FileName);
    if (edit != null ) {
      JInternalFrame iframe = (JInternalFrame)htEditorFrame.get(FileName);
      boolean iframe_isVisible = true;
      if (!iframe.isVisible() ) {
       iframe_isVisible = false;
      }
      iframe.setVisible(true);
      iframe.toFront();


      try {
        iframe.setSelected(true);
      }
      catch (Exception ee) {}
      desktop.setSelectedFrame(iframe);
      //desktop.flagContentsChanged(iframe);
      iframe.requestFocus();
      edit.jetx.requestFocus();
      edit.jetx.setCaretVisible(true);
      if (!iframe_isVisible ) {
        Dimension desktopSize = desktop.getSize();
        iframe.setSize (desktop.getWidth(), desktop.getHeight());
        iframe.setLocation (0, 0);
      }
    }
    else {
      //File file = new File(project.getProjectPath() + FileName);
      boolean readOnly = false;
      File file = new File(FileNameWithPath);
      if (!file.isFile() ) {
        return;
      }
      if (file.canWrite() ){
        readOnly = false;
      }
      else {
        readOnly = true;
      }


      JInternalFrame iframe = null;
      if (!readOnly ) {
        iframe = new JInternalFrame(FileName);
      }
      else {
        if (System.getProperty("language").equals("japanese") ) {
          iframe = new JInternalFrame(FileName + " [�ǂݎ���p]");
        }
        else {
          iframe = new JInternalFrame(FileName + " [ReadOnly]");
        }

      }
      codeCheck.setEnabled(true);
      projectMenu1.setEnabled(true);

      if (FileKind == 2 ) {
	      iframe.setName("Dash");
      }
      else if (FileKind == 3 ) {
	      iframe.setName("RSet");
      }
      else if (FileKind == 4 ) {
	      iframe.setName("Java");
      }
      else if (FileKind == 5 ) {
	      iframe.setName("Etc");
        codeCheck.setEnabled(false);
        projectMenu1.setEnabled(false);
      }

      //JInternalFrame iframe = new JInternalFrame(file.getName());
      Container con = iframe.getContentPane(); //�t���[���̕\���̈���Ƃ��Ă���
      edit = new Editor(con, frame);
      edit.EnabledBreakPointSet(false);
      edit.setTextName(FileName);
      //edit.ReadOnlySet();
      // �L�[�o�C���h�ݒ�
      edit.jetx.getDefaults().inputHandler.changeKeyBindings(htCurrentKeyBind);
      //edit.FileRead(project.getProjectPath() + FileName);
      edit.FileRead(FileNameWithPath);

      edit.ReadOnlySet(readOnly);

      //edit.TokenMarkerUpdate();
      //edit.jetx.repaint();
      iframe.setResizable(true);
      iframe.setMaximizable(true);
      iframe.setClosable(true);
      iframe.setIconifiable(true);

      Dimension desktopSize = desktop.getSize();
      int w = desktop.getWidth();
      int h = desktop.getHeight();
      iframe.setSize (desktop.getWidth(), desktop.getHeight());
      iframe.setLocation (0, 0);
      //iframe.setVisible (true );
      desktop.add (iframe );
      //CodeEditorDesktop.add (new TextFrame());

      iframe.setVisible (true );

      //desktop.setSelectedFrame (iframe );
      iframe.setSize (desktop.getWidth(), desktop.getHeight());
      iframe.setLocation (0, 0);

      int CaretPosition = edit.jetx.getCaretPosition();
      edit.jetx.setCaretPosition(CaretPosition);
      edit.jetx.requestFocus();
      iframe.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      iframe.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
        public void internalFrameClosing(InternalFrameEvent e) {
          this_internalFrameClosing(e);
        }
        public void internalFrameDeiconified(InternalFrameEvent e) {
          this_internalFrameDeiconified(e);
        }
        public void internalFrameActivated(InternalFrameEvent e) {
          this_internalFrameActivated(e);
        }
      });

      iframe.addComponentListener(new java.awt.event.ComponentAdapter() {
        public void componentMoved(ComponentEvent e) {
          this_componentMoved(e);
        }
      });


      htEditor.put(FileName,edit);
      htEditorFrame.put(FileName,iframe);
      htFileTimeStampe.put(FileName, new Long(file.lastModified()).toString() );
      updateWindowMenu ( );
      savemenuitem.setEnabled(false);
      saveBtn.setEnabled(false);
    }

      if (DefauleTextLineNo != 0 )
        edit.jetx.LineSelect(DefauleTextLineNo);

  }

  /****************************************************************************
   * �q�E�B���h�E�i�G�f�B�^�E�B���h�E�j�̃N���[�Y����
   * @param�@InternalFrameEvent e
   * @return �Ȃ�
   ****************************************************************************/
  void this_internalFrameClosing(InternalFrameEvent e) {
    e.getInternalFrame().setVisible(false);
    updateWindowMenu ( );
  }
  void this_internalFrameDeiconified(InternalFrameEvent e) {
    updateWindowMenu ( );
  }
  void this_componentMoved(ComponentEvent e) {
    //System.out.println("this_componentMoved");
    //System.out.println(e.getComponent().getLocation().getX() );
    //System.out.println(e.getComponent().isShowing() );

  }


  /****************************************************************************
   * �q�E�B���h�E�i�G�f�B�^�E�B���h�E�j���A�N�e�B�u�ɂȂ������̏���
   * @param�@InternalFrameEvent e
   * @return �Ȃ�
   ****************************************************************************/
  void this_internalFrameActivated(InternalFrameEvent e) {

    String name = e.getInternalFrame().getTitle();
    Editor edit = (Editor)htEditor.get(name);

    if (edit == null ) {
      return;
    }
    if (edit.jetx.changecnt > 0 ) {
      savemenuitem.setEnabled(true);
      saveBtn.setEnabled(true);
    }
    else {
      savemenuitem.setEnabled(false);
      saveBtn.setEnabled(false);
    }

    if (e.getInternalFrame().getName().equals("Etc") ) {
      codeCheck.setEnabled(false);
      projectMenu1.setEnabled(false);
    }
    else {
      codeCheck.setEnabled(true);
      projectMenu1.setEnabled(true);
    }
  }

  /****************************************************************************
   * ���ʕ\�����X�g�̃}�E�X�C�x���g����
   * @param�@
   * @return �Ȃ�
   ****************************************************************************/
  public class ResultListMouseHandler extends MouseAdapter {

    /** Linux�p */
    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger())
        showCodeCheckPopupMenu(e);
    }

    /** Windows�p */
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
          showCodeCheckPopupMenu(e);
    }

    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        int index = List.locationToIndex(e.getPoint());

        String result = (String)vecCodeCheckResult.elementAt(index);
        String FileName = (String)vecCodeCheckFile2.elementAt(index);
        int ErrRow = new Integer((String)vecCodeCheckErrRow.elementAt(index)).intValue();
        if (result.equals("ERROR") || result.equals("WARNING")) {

          File f = new File (FileName);
          int filekind = 0;
          if (FileName.toUpperCase().endsWith(".DASH") ) {
            filekind = 2;
          }
          else if (FileName.toUpperCase().endsWith(".RSET") ) {
            filekind = 3;
          }
          else if (FileName.toUpperCase().endsWith(".JAVA") ) {
            filekind = 4;
          }
          else  {
            filekind = 5;
          }
          OpenFile(f.getName(),FileName,ErrRow, filekind);

          /*
          for (int i=0; i<projectTree2.getRowCount(); i++ ) {
            TreePath path = (TreePath)projectTree2.getPathForRow(i);

            String filename = path.getLastPathComponent().toString();
            DirectoryNode node = (DirectoryNode)path.getLastPathComponent();
            DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();

            if (nodeinfo.getName().toLowerCase().equals(FileName.toLowerCase())) {
              OpenFile(nodeinfo.getName(),nodeinfo.getPath(),ErrRow, nodeinfo.getKind());
            }
          }
          */
        }
      }
    }
  }

  /****************************************************************************
   * �v���W�F�N�g�̍폜����
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void deleteProject() {
    Object[] options = { "�͂�", "������","�L�����Z��" };
    options[0] = getBilingualMsg("0191");
    options[1] = getBilingualMsg("0192");
    options[2] = getBilingualMsg("0126");
    String msg = getBilingualMsg("0196");
    //msg = msg.replaceAll("*","\n");
    msg = msg.replace('*','\n');

    int ret = JOptionPane.showOptionDialog(this,msg,getBilingualMsg("0116"),
                               JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                               null, options, options[0]);
    /*
    int ret = JOptionPane.showOptionDialog(this,
        "���S�ɍ폜���܂����H\n�v���W�F�N�g�t�@�C���A����� �����ŊǗ�����Ă���G�[�W�F���g�t�@�C����\n�c���ꍇ�́u�������v��I�����ĉ������B", "�v���W�F�N�g�̍폜",
                               JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                               null, options, options[0]);
    */

    if (ret == 2 ) {
      return;
    }


    String dirpath = "";
    if (!dashdir.toString().endsWith(File.separator)) {
        dirpath = dashdir.toString() + File.separator;
    }
    String FilePath = dirpath + "properties" + File.separator + "session";

    // �v���W�F�N�g�ŊǗ�����t�@�C���̖��̂��擾
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    //�ǂݍ��ݏ���
    Vector vecFileNames = new Vector();
    try {
        f_in = new FileReader(FilePath);
        b_in = new BufferedReader(f_in);
        while((sLine = b_in.readLine()) != null) {

          File file = new File(sLine);
          if (!file.canRead() || file.isDirectory())
            continue;

          //String cname = sLine.substring(0, p);
          //if (!Character.isUpperCase((char)cname.charAt(0)))
          //  continue;

          if (sLine.toLowerCase().equals(project.getProjectFileNameWithPath().toLowerCase())) {
            vecFileNames.addElement("--DeleteFile--");
            continue;
          }

          if (vecFileNames.size()<12 ) {
            vecFileNames.addElement(sLine);
          }
        }
        b_in.close();
        f_in.close();

    } catch(Exception ex) {
    }


    try {
      File fp  = new File ( FilePath );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      for (int i=0; i<vecFileNames.size(); i++ ) {
        if (i > 10 ) {
          break;
        }
        pw.println((String)vecFileNames.elementAt(i));
      }
      pw.close ();
    } catch ( Exception e ){}


    if (ret == 0 ) {
      boolean delResult = true;
      for (int i=0; i<project.getFileCount(); i++ ) {
        File file = project.getFile(i);
        if (!file.delete() ) {
          delResult = false;
        }
      }

      File file = new File(project.getProjectPath() + "bak" );
      if (file.isDirectory() ) {
        String file_list[] = file.list();
        for (int i=0; i<file_list.length; i++ ) {
          boolean findflag = false;
          for (int j=0; j<project.getFileCount(); j++ ) {
            String FileName = project.getFileName(j);
            if (file_list[i].toLowerCase().indexOf(FileName.toLowerCase()) != -1 ) {
              findflag = true;
              break;
            }
          }

          if (findflag ) {
            String wk = project.getProjectPath() + "bak" + File.separator + file_list[i];
            file = new File (project.getProjectPath() + "bak" + File.separator + file_list[i] );
            file.delete();
          }
        }

        file = new File(project.getProjectPath() + "bak" );
        file.delete();
      }
      file = new File (project.getProjectFileNameWithPath() );
      file.delete();

      file = new File(project.getProjectPath());
      if (file.isDirectory()) {
        if (!file.delete() ) {
          delResult = false;
        }
      }

      if (!delResult ) {
        //options = {"OK"};
        Object[] options2 = { "OK" };

        msg = getBilingualMsg("0197");
        JOptionPane.showOptionDialog(this,
            msg, getBilingualMsg("0116"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                                   null, options2, options2[0]);
        /*
        JOptionPane.showOptionDialog(this,
            "�������̃t�@�C���A�܂��̓t�H���_���폜�o���܂���ł����B\n�s�v�ȏꍇ�́A�蓮�ō폜���ĉ������B", "�v���W�F�N�g�̍폜",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                                   null, options2, options2[0]);
        */

      }
    }

    /*
    vecFileNames.remove(0);
    this.setOpenHist(vecFileNames);
    searchPanel.setOpenHist(vecFileNames);
    simulatorPanel.setOpenHist(vecFileNames);
    savePanel.setOpenHist(vecFileNames);
    */

    this.setTitle("IDEA - ");

    this.readProjectFile(" ");

  }


  /****************************************************************************
   * �������`�F�b�J�[�̊J�n
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void startMemoryWatch() {
    Runnable r = new Runnable() {
        Runtime runtime = Runtime.getRuntime();
        long total, free, use;

        public void run() {

          while (true) {
            total = runtime.totalMemory() / 1024L;
            free = runtime.freeMemory() / 1024L;
            use = total - free;
            memoryLabel.setText("use: "+use+ " Kb    total: "+total+" Kb");

            if (checkFileTimeStampFlag ) {
              checkFileTimeStampFlag = false;
              checkFileTimeStamp();
            }
            try { Thread.sleep(1000); } catch (InterruptedException e) { }
          }
        }
    };
    Thread thread = new Thread(dvm.dashThreads, r, "MemoryWatcher");
    thread.start();
  }



  /*****************************************************************************
   * ID�I�v�V�����֘A
   ****************************************************************************/
  /** �L�[�o�C���h��� */
  //private Hashtable htKeyBind = new Hashtable();
  private Hashtable htCurrentKeyBind = new Hashtable();
  /** ���݂̌��� */
  private String SelectLanguage = "";

  /****************************************************************************
   * ���݂̌�����擾
   * @param�@�Ȃ�
   * @return String "japanese" or "english"
   ****************************************************************************/
  public String getLanguage() {
    return SelectLanguage;
  }

  /****************************************************************************
   * IDE�I�v�V�������Ǎ�
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void readIdeOptionXml() {
    try
    {

      htCurrentKeyBind.clear();
      // �h�L�������g�r���_�[�t�@�N�g���𐶐�
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      // �h�L�������g�r���_�[�𐶐�
      DocumentBuilder builder = dbfactory.newDocumentBuilder();
      // �p�[�X�����s����Document�I�u�W�F�N�g���擾
      Document doc = builder.parse(new File("properties/ide-option.xml"));

      /* �i�R�j�t�q�k���g���ă��\�[�X�t�@�C�����J�� */
      //java.net.URL url = this.getClass().getResource("../properties/keybind.xml");
      //Document doc = builder.parse(url.openStream());

      // ���[�g�v�f���擾�i�^�O���Fsite�j
      Element root = doc.getDocumentElement();
      //System.out.println("���[�g�v�f�̃^�O���F" + root.getTagName());
      //System.out.println("***** �y�[�W���X�g *****");

      // �u����v���̓Ǎ�
      NodeList language = root.getElementsByTagName("language");
      for (int i=0; i < language.getLength() ; i++) {
        Element element = (Element)language.item(i);
        String title = element.getFirstChild().getNodeValue();
        SelectLanguage = title;
        System.setProperty("language",SelectLanguage.toLowerCase());
      }

      NodeList kanji = root.getElementsByTagName("kanjicode");
      for (int i=0; i < kanji.getLength() ; i++) {
        Element element = (Element)kanji.item(i);
        String title = element.getFirstChild().getNodeValue();
        //SelectkanjiCode = title;

        if (title != null ) {
          if (title.equals("SHIFT-JIS") ) {
            System.setProperty("kanjicode","SHIFT_JIS");
          }
          else if (title.equals("EUC") ) {
            System.setProperty("kanjicode","EUC_JP");
          }
        }
      }

      if (System.getProperty("kanjicode") == null ) {
        System.setProperty("kanjicode","SHIFT_JIS");
      }



      // �u�L�[�o�C���h�v���̓Ǎ�
      NodeList keybindlist = root.getElementsByTagName("keybind");
      String KeyBind = "";
      for (int i=0; i < keybindlist.getLength() ; i++) {
        Element element = (Element)keybindlist.item(i);
        String title = element.getFirstChild().getNodeValue();
        KeyBind = title;
        //System.out.println("keybind�F" + title);
      }

      String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};
      // windows�v�f�̃��X�g���擾
      NodeList list = root.getElementsByTagName("User-define");
      // windows�v�f�̐��������[�v
      for (int i=0; i < list.getLength() ; i++) {
        // windows�v�f���擾
        Element element = (Element)list.item(i);
        for (int i2 = 0; i2<NodeList.length;i2++ ) {
          NodeList list2 = element.getElementsByTagName(NodeList[i2]);
          for (int j=0; j < list2.getLength() ; j++) {
            Element element2 = (Element)list2.item(j);
            //
            NodeList list3 = element2.getElementsByTagName("property");
            for (int k=0; k < list3.getLength() ; k++) {
              Element element3 = (Element)list3.item(k);
              String name = element3.getAttribute("name");
              String value = element3.getAttribute("value");
              String comment = element3.getAttribute("comment");
              htCurrentKeyBind.put(name,value);
            }
          }

          if (NodeList[i2].equals("other")){
            if (htCurrentKeyBind.get("codecheck") == null ) {
              htCurrentKeyBind.put("codecheck","CS+F9");
            }
          }
        }
      }

    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /****************************************************************************
   * ���j���[���ڂ̃L�[�o�C���h�ݒ���s��
   * @param�@String action
   * @param�@JMenuItem menuitem
   * @return �Ȃ�
   ****************************************************************************/
  public void setMenuItemAccelerator(String action, JMenuItem menuitem)
  {

    String keyBinding = (String)htCurrentKeyBind.get(action);
    KeyStroke keyStroke = parseKeyStroke(keyBinding);
    if (keyStroke == null ) {
      return;
    }
    if (menuitem == null ) {
      return ;
    }
    menuitem.setAccelerator(keyStroke);
  }

  public static KeyStroke parseKeyStroke(String keyStroke)
  {
    if(keyStroke == null)
      return null;

    if (keyStroke.equals("") ) {
      return null;
    }
    int modifiers = 0;
    int index = keyStroke.indexOf('+');
    if(index != -1)
    {
      for(int i = 0; i < index; i++)
      {
        switch(Character.toUpperCase(keyStroke
          .charAt(i)))
        {
        case 'A':
          modifiers |= InputEvent.ALT_MASK;
          break;
        case 'C':
          modifiers |= InputEvent.CTRL_MASK;
          break;
        case 'M':
          modifiers |= InputEvent.META_MASK;
          break;
        case 'S':
          modifiers |= InputEvent.SHIFT_MASK;
          break;
        }
      }
    }
    String key = keyStroke.substring(index + 1);
    if(key.length() == 1)
    {
      char ch = Character.toUpperCase(key.charAt(0));
      if(modifiers == 0)
        return KeyStroke.getKeyStroke(ch);
      else
        return KeyStroke.getKeyStroke(ch,modifiers);
    }
    else if(key.length() == 0)
    {
      System.err.println("Invalid key stroke: " + keyStroke);
      return null;
    }
    else
    {
      int ch;

      try
      {
        ch = KeyEvent.class.getField("VK_".concat(key))
          .getInt(null);
      }
      catch(Exception e)
      {
        System.err.println("Invalid key stroke: "
          + keyStroke);
        return null;
      }

      return KeyStroke.getKeyStroke(ch,modifiers);
    }
  }

  /****************************************************************************
   * ���j���[���ڂ̃L�[�o�C���h�ύX���s��
   * @param�@String action
   * @param�@JMenuItem menuitem
   * @return �Ȃ�
   ****************************************************************************/
  private void changeMenuItemAccelerator() {
    setMenuItemAccelerator ("new-file", newFileMenuItem );
    setMenuItemAccelerator ("open-project", openProjectMenuItem );
    setMenuItemAccelerator ("file-save", savemenuitem );
    setMenuItemAccelerator ("file-save-all", allsavemenuitem );

    setMenuItemAccelerator ("undo", undoMenuItem );
    setMenuItemAccelerator ("redo", redoMenuItem );
    setMenuItemAccelerator ("cut-to-clipboard", cutMenuItem );
    setMenuItemAccelerator ("copy-to-clipboard", copyMenuItem );
    setMenuItemAccelerator ("paste-from-clipboard", pasetMenuItem );
    setMenuItemAccelerator ("select-all", allSelMenuItem );

    setMenuItemAccelerator ("search-dialog", searchMenu1 );
    setMenuItemAccelerator ("replace-dialog", searchMenu2 );
    setMenuItemAccelerator ("search-again", searchMenu3 );
    setMenuItemAccelerator ("goto-line", searchMenu4 );

    setMenuItemAccelerator ("codecheck", projectMenu1 );
  }

  /****************************************************************************
   * �o�C�����K�����̓Ǎ�
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private Hashtable htBilingualMsg = new Hashtable();
  private void readBilingaulXml() {
    try
    {

      // �h�L�������g�r���_�[�t�@�N�g���𐶐�
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      // �h�L�������g�r���_�[�𐶐�
      DocumentBuilder builder = dbfactory.newDocumentBuilder();
      // �p�[�X�����s����Document�I�u�W�F�N�g���擾

      /* �i�R�j�t�q�k���g���ă��\�[�X�t�@�C�����J�� */
      //java.net.URL url = this.getClass().getResource("../properties/bilingual.xml");
      //Document doc = builder.parse(url.openStream());
      Document doc = builder.parse(new File("properties/bilingual.xml"));

      // ���[�g�v�f���擾�i�^�O���Fsite�j
      Element root = doc.getDocumentElement();
      //System.out.println("���[�g�v�f�̃^�O���F" + root.getTagName());
      //System.out.println("***** �y�[�W���X�g *****");

      // windows�v�f�̃��X�g���擾
      NodeList list = root.getElementsByTagName("msg");
      // windows�v�f�̐��������[�v
      for (int i=0; i < list.getLength() ; i++) {
        Element element = (Element)list.item(i);
        String code = element.getAttribute("code");
        String jpn = element.getAttribute("jpn");
        String eng = element.getAttribute("eng");

        htBilingualMsg.put("jpn" + code, jpn);
        htBilingualMsg.put("eng" + code, eng);

        //System.out.println("code=" + code + " jpn=" + jpn + " eng=" + eng);
      }

    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /****************************************************************************
   * �o�C�����K�����b�Z�[�W�̎擾
   * @param�@String code
   * @return String
   ****************************************************************************/
  public String getBilingualMsg (String code ) {
    String ret = "";
    String lang = "eng";
    if (SelectLanguage.toLowerCase().equals("japanese") ) {
      lang = "jpn";
    }
    else {
      lang = "eng";
    }


    ret = (String)htBilingualMsg.get(lang.toLowerCase()+code);
    if (ret == null ) {
      return "Undefined msg!";
    }
    return ret;
  }

  /****************************************************************************
   * �G�f�B�^�ݒ��ʂ��J��
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void SetupEditor() {
    // �_�~�[�G�f�B�^���g�p���Đݒ��ʂ��J��
    dmyedit.EnvironmentOpen();

    // ���݁A�J����Ă���G�f�B�^���ĕ`��
    for (Enumeration emu = htEditor.elements(); emu.hasMoreElements(); ) {
      Editor edit = (Editor)emu.nextElement();
      edit.TokenMarkerUpdate();
      edit.jetx.repaint();
    }
  }

  /****************************************************************************
   * �L�[���[�h�ݒ��ʂ��J��
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void SetupKeyword() {
    Emphasis emp = new Emphasis(this);
    // ���݁A�J����Ă���G�f�B�^���ĕ`��
    for (Enumeration emu = htEditor.elements(); emu.hasMoreElements(); ) {
      Editor edit = (Editor)emu.nextElement();
      edit.TokenMarkerUpdate();
      edit.jetx.repaint();
    }
  }

  /****************************************************************************
   * IDE�I�v�V������ʂ��J��
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void IdeOption() {
    IdeOption dlg = new IdeOption(this,"�L�[�}�b�v�ݒ�",true );
    //dlg.show();
  	dlg.setVisible(true);
    if (dlg.ret ) {
      readIdeOptionXml();
      changeMenuItemAccelerator();
      searchPanel.changeMenuItemAccelerator();
      savePanel.changeMenuItemAccelerator();
      simulatorPanel.changeMenuItemAccelerator();
      for (Enumeration emu = htEditor.elements(); emu.hasMoreElements(); ) {
        Editor edit = (Editor)emu.nextElement();
        edit.jetx.getDefaults().inputHandler.changeKeyBindings(htCurrentKeyBind);
        edit.jetx.repaint();
      }

      // �o�C�����K��
      changeMenuText();
      searchPanel.changeMenuText();
      simulatorPanel.changeMenuText();
      savePanel.changeMenuText();

      changeFormLabel();
      searchPanel.changeFormLabel();
      simulatorPanel.changeFormLabel();
      savePanel.changeFormLabel();

      changeFileChooserUI();
      changeColorChooserUI();

    }
  }

  /****************************************************************************
   * �V�~�����[�^�ݒ��ʂ��J��
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void SimulatorOption() {
    SimulatorOption dlg = new SimulatorOption(this );
    //dlg.show();
    	dlg.setVisible(true);
  }

  /****************************************************************************
   * �O���c�[���\���_�C�A���O�̕\��
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void SetOutsideTool() {

    /*
    PrintStream stdoutPrintStream, stderrPrintStream;

    // System.{out|err}.println()�����o�C�g����i�[����Stream
    ByteArrayOutputStream stdoutStream, stderrStream;

      stdoutStream = new ByteArrayOutputStream();
      stdoutPrintStream = new PrintStream(stdoutStream);
      System.setOut(stdoutPrintStream);
      //System.setOut(System.out);

      stderrStream = new ByteArrayOutputStream();
      stderrPrintStream = new PrintStream(stderrStream);
      System.setErr(stderrPrintStream);

    System.out.println("SetOutsideTool");
    String s = stdoutStream.toString();


    try {
      String[] args = {"C:\\dash-1.9.7h\\src\\baseProcess\\dashSample\\SimpleWindow.java"};
      //com.sun.tools.javac.Main.compile(args);
      //����� Java �̃\�[�X���R���p�C���ł���̂ł����Acom.sun.tools.javac.Main �N���X���g���ɂ�
      //CLASSPATH ��ݒ肷��K�v������܂��B
      //com.sun.tools.javac.Main �N���X�� J2SDK ���C���X�g�[�������f�B���N�g���̒����ɂ���
      //lib �f�B���N�g���� tools.jar �Ɋ܂܂�Ă��܂��B�ł�����A���� Jar �t�@�C���� CLASSPATH �Ɋ܂߂�K�v������܂��B

        Class c = Class.forName ("com.sun.tools.javac.Main");
        Object compiler = c.newInstance ();
        Method compile = c.getMethod ("compile",
            new Class [] {(new String [] {}).getClass ()});
        System.setErr(stderrPrintStream);
        int result = ((Integer) compile.invoke
                      (compiler, new Object[] {args}))
            .intValue ();
       // return (result == MODERN_COMPILER_SUCCESS);
        String s2 = stdoutStream.toString();
        String s3 = stderrStream.toString();
        String s4= "";
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      //  if (ex instanceof BuildException) {
      //      throw (BuildException) ex;
      //  } else {
      //      throw new BuildException("Error starting modern compiler",
      //                               ex, location);
      //  }
    }
    */

    // �O���c�[���\���_�C�A���O�̕\��
    OutsideTool ost = new OutsideTool (this );
    //ost.show();
	  ost.setVisible(true);
    ost.dispose();

    // �O���c�[���̍\������ǂݒ���
    readOutsideToolInfo ();

    // �O���c�[���p���j���[����蒼��
    outsideTool.removeAll();
    outsideTool2.removeAll();

    //JMenuItem toolMenu3 = menuItem("�\��(C)...", "SetOutsideTool", null );
    JMenuItem toolMenu3 = menuItem(getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3.setMnemonic('C');
    outsideTool.add(toolMenu3);

    //JMenuItem toolMenu3_2 = menuItem("�\��(C)...", "SetOutsideTool", null );
    JMenuItem toolMenu3_2 = menuItem(getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3_2.setMnemonic('C');
    outsideTool2.add(toolMenu3_2);


    if (vecOutsideToolInfo.size() > 0 ) {
      outsideTool.addSeparator();
      outsideTool2.addSeparator();
    }

    for (int i=0; i<vecOutsideToolInfo.size(); i++ ) {
      String wk = (String)vecOutsideToolInfo.elementAt(i) ;

      StringTokenizer st = new StringTokenizer(wk,",");
      while (st.hasMoreTokens()) {
        String data = st.nextToken();

        JMenuItem toolMenu4 = menuItem(new Integer(i+1).toString() + ":" + data, "OutsideTool_" + new Integer(i).toString(), null );
        outsideTool.add(toolMenu4);

        JMenuItem toolMenu4_2 = menuItem(new Integer(i+1).toString() + ":" + data, "OutsideTool_" + new Integer(i).toString(), null );
        outsideTool2.add(toolMenu4_2);
        break;
      }
    }

    simulatorPanel.ReMakeOutsideToolMenu();
    searchPanel.ReMakeOutsideToolMenu();
    savePanel.ReMakeOutsideToolMenu();
  }

  /****************************************************************************
   * �O���c�[���̎��s
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void ExecOutsideTool (String command ) {
    String ToolNoStr = command.substring(12);

    int ToolNo = new Integer(ToolNoStr).intValue();

    String wk = (String)vecOutsideToolInfo.elementAt(ToolNo) ;
    StringTokenizer st = new StringTokenizer(wk,",");

    String ToolPath = "";
    String Param = "";
    int cnt = 0;
    while (st.hasMoreTokens()) {
      cnt ++;
      String data = st.nextToken();

      if (cnt == 2 ) {
        ToolPath = data;
      }
      else if (cnt == 3 ) {
        Param = data;
        if (Param.equals("1")) {

          TreePath[] path = getSelectPaths();
          if (path == null ) {
            Param = "";
          }
          else {
            DirectoryNode node = (DirectoryNode)path[0].getLastPathComponent();
            DirectoryNodeInfo nodeinfo = (DirectoryNodeInfo)node.getUserObject();
            Param = " " + nodeinfo.getPath();
          }

          /*
          Vector vecSelectFiles = getSelectFiles();
          if (vecSelectFiles.size() > 0 ) {
            Param = " " + project.getProjectPath() + (String)vecSelectFiles.elementAt(0);
          }
          else {
            Param = "";
          }
          */
        }
		else {
	 	Param = "";
		}
      }
    }

    try {
      if (!new File(ToolPath).exists() ) {
        Object[] options = { "OK" };
        int ret = JOptionPane.showOptionDialog(this,
            getBilingualMsg("0198"),getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        /*
        int ret = JOptionPane.showOptionDialog(this,
            "�O���c�[���Ɏw�肳�ꂽ���s�t�@�C����������܂���B","�G���[",
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        */
        return;
      }
      java.lang.Runtime.getRuntime().exec(ToolPath + Param);
    }
    catch (Exception ee ) {}

  }

  /****************************************************************************
   * ��ʏ�̃��x���̕\�����e��ύX�iIDE�I�v�V�����Łu����v���ύX���ꂽ���ɌĂ΂��j
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void changeFormLabel() {
    jTabbedPane.setTitleAt(0,getBilingualMsg("0037"));
    jTabbedPane.setTitleAt(1,getBilingualMsg("0038"));
    jTabbedPane.setTitleAt(2,getBilingualMsg("0039"));
    jTabbedPane.setTitleAt(3,getBilingualMsg("0040"));

    newfileBtn.setToolTipText(getBilingualMsg("0002"));
    newprjBtn.setToolTipText(getBilingualMsg("0003"));
    openprjBtn.setToolTipText(getBilingualMsg("0004"));
    openagainprjToggleButton.setToolTipText(getBilingualMsg("0005"));
    undoBtn.setToolTipText(getBilingualMsg("0010"));
    redoBtn.setToolTipText(getBilingualMsg("0011"));
    copyBtn.setToolTipText(getBilingualMsg("0013"));
    pasteBtn.setToolTipText(getBilingualMsg("0014"));
    cutBtn.setToolTipText(getBilingualMsg("0012"));
    searchBtn.setToolTipText(getBilingualMsg("0017"));
    researchBtn.setToolTipText(getBilingualMsg("0019"));
    substitutionBtn.setToolTipText(getBilingualMsg("0018"));
    codeCheck.setToolTipText(getBilingualMsg("0032"));
    saveBtn.setToolTipText(getBilingualMsg("0006"));
    allsaveBtn.setToolTipText(getBilingualMsg("0007"));


    notOpenProjectMsg1.setText(getBilingualMsg("0199"));
    notOpenProjectMsg2.setText(getBilingualMsg("0199"));
    notOpenProjectMsg3.setText(getBilingualMsg("0199"));
    notOpenProjectMsg4.setText(getBilingualMsg("0199"));


  }

  /****************************************************************************
   * ���j���[�������ύX<br>
   * �{���\�b�h�́AIdeaMainFrame.IdeOption���\�b�h����Ă΂�܂��B
   * @param�@�Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void changeMenuText() {
    fileMenu_DevScreen.setText(getBilingualMsg("0001") + "(F)");
    savemenuitem.setText(getBilingualMsg("0006") + "(S)");
    allsavemenuitem.setText(getBilingualMsg("0007") + "(A)");
    exitMenu.setText(getBilingualMsg("0008")+"(X)");
    newFileMenuItem.setText(getBilingualMsg("0002") + "(N)...");
    newProjectMenuItem.setText(getBilingualMsg("0003") + "(P)...");
    openProjectMenuItem.setText(getBilingualMsg("0004") + "(O)...");
    deletePrjMenuItem.setText(getBilingualMsg("0116"));
    openagainprjmenu.setText(getBilingualMsg("0005") + "(R)");
    if (deleteProjectHistMenuItem1 != null ) {
      deleteProjectHistMenuItem1.setText(getBilingualMsg("0036"));
    }
    if (deleteProjectHistMenuItem2 != null) {
      deleteProjectHistMenuItem2.setText(getBilingualMsg("0036"));
    }

    editMenu_DevScreen.setText(getBilingualMsg("0009") + "(E)");
    undoMenuItem.setText(getBilingualMsg("0010") + "(U)");
    redoMenuItem.setText(getBilingualMsg("0011") + "(R)");
    cutMenuItem.setText(getBilingualMsg("0012") + "(T)");
    copyMenuItem.setText(getBilingualMsg("0013") + "(C)");
    pasetMenuItem.setText(getBilingualMsg("0014") +"(P)");
    allSelMenuItem.setText(getBilingualMsg("0015") + "(A)");

    searchMenu_DevSecreen.setText(getBilingualMsg("0016") + "(S)");
    searchMenu1.setText(getBilingualMsg("0017") + "(F)...");
    searchMenu2.setText(getBilingualMsg("0018") + "(R)...");
    searchMenu3.setText(getBilingualMsg("0019") + "(A)");
    searchMenu4 .setText(getBilingualMsg("0020") + "(L)...");

    toolMenu_DevScreen.setText(getBilingualMsg("0021") + "(T)");
    toolMenu1.setText(getBilingualMsg("0022") + "(E)...");
    toolMenu2.setText(getBilingualMsg("0023") +  "(K)...");
    toolMenu4.setText(getBilingualMsg("0117") + "...");
    toolMenu5.setText(getBilingualMsg("0118") + "...");
    outsideTool.setText(getBilingualMsg("0024") + "(O)");
    outsideTool2.setText(getBilingualMsg("0024") + "(O)");
    toolMenu3.setText(getBilingualMsg("0025") + "(C)...");

    windowMenu_DevScreen.setText(getBilingualMsg("0026") + "(W)");
    windowMenu1.setText(getBilingualMsg("0027") + "(W)");
    windowMenu2.setText(getBilingualMsg("0028") + "(H)");
    windowMenu3.setText(getBilingualMsg("0029") + "(V)");
    windowMenu4.setText(getBilingualMsg("0030") + "(I)");

    projectMenu_DevScreen.setText(getBilingualMsg("0136") + "(P)");
    projectMenu1.setText(getBilingualMsg("0032"));


    // �|�b�v�A�b�v���j���[
    //"�J��"
    popupMenuItem1.setText(getBilingualMsg("0033"));
    //"�V�K..."
    popupMenuItem2.setText(getBilingualMsg("0002") + "...");
    //"���S�ɍ폜"
    popupMenuItem3.setText(getBilingualMsg("0034"));
    //"�v���W�F�N�g����̍폜"
    popupMenuItem4.setText(getBilingualMsg("0035"));
    //"�v���W�F�N�g�̍폜"
    popupMenuItem5.setText(getBilingualMsg("0116"));
    //"�������쐬..."
    popupMenuItem6.setText(getBilingualMsg("0031") + "...");
    //"���O��ύX���쐬..."
    popupMenuItem6.setText(getBilingualMsg("0211") + "...");

    //"�R�[�h�`�F�b�N"
    popupMenuItem7.setText(getBilingualMsg("0032"));
    popupMenuItem8.setText(getBilingualMsg("0006") + "(S)");
    popupMenuItem9.setText(getBilingualMsg("0007") + "(A)");

    //"�\��(C)..."
    toolMenu3_2.setText(getBilingualMsg("0025") + "(C)...");
    CodeCheckPopupMenuItem.setText(getBilingualMsg("0119"));

    popupMenuItem10.setText(getBilingualMsg("0204"));

    //Dash�t�@�C���֘A�ݒ�
    popupMenuItem11.setText(getBilingualMsg("0205"));



 }

 /****************************************************************************
  * JFileChooser��UI�ύX�iIDE�I�v�V�����Łu����v���ύX���ꂽ���ɌĂ΂��j
  * @param�@�Ȃ�
  * @return �Ȃ�
  ****************************************************************************/
 private void changeFileChooserUI(){
   if (System.getProperty("language").equals("japanese") ) {
     UIManager.getDefaults().put("FileChooser.lookInLabelText", "�Q��");
     UIManager.getDefaults().put("FileChooser.openButtonText", "�J��");
     UIManager.getDefaults().put("FileChooser.openButtonToolTipText", "�I�������t�@�C�����J��");
     UIManager.getDefaults().put("FileChooser.cancelButtonText", "����");
     UIManager.getDefaults().put("FileChooser.cancelButtonToolTipText", "�t�@�C���`���[�U�_�C�A���O���I��");
     UIManager.getDefaults().put("FileChooser.fileNameLabelText", "�t�@�C����:");
     UIManager.getDefaults().put("FileChooser.filesOfTypeLabelText", "�t�@�C���^�C�v:");
     UIManager.getDefaults().put("FileChooser.upFolderToolTipText", "�P���x�����");
     UIManager.getDefaults().put("FileChooser.homeFolderToolTipText", "�f�X�N�g�b�v");
     UIManager.getDefaults().put("FileChooser.newFolderToolTipText", "�t�H���_�̐V�K�쐬");
     UIManager.getDefaults().put("FileChooser.listViewButtonToolTipText", "���X�g");
     UIManager.getDefaults().put("FileChooser.detailsViewButtonToolTipText", "�ڍ�");
     UIManager.getDefaults().put("FileChooser.acceptAllFileFilterText", "�S�Ẵt�@�C�� (*.*)");
     UIManager.getDefaults().put("ColorChooser.swatchesRecentText", "�ŐV:");
   }
   else {
     //BasicFileView fileView = new WindowsFileView();
     UIManager.getDefaults().put("FileChooser.lookInLabelText", "Look in:");
     UIManager.getDefaults().put("FileChooser.openButtonText", "Open");
     UIManager.getDefaults().put("FileChooser.openButtonToolTipText", "Open selected file");
     UIManager.getDefaults().put("FileChooser.cancelButtonText", "Cancel");
     UIManager.getDefaults().put("FileChooser.cancelButtonToolTipText", "Abort file chooser dialog");
     UIManager.getDefaults().put("FileChooser.fileNameLabelText", "File name:");
     UIManager.getDefaults().put("FileChooser.filesOfTypeLabelText", "Files of type:");
     UIManager.getDefaults().put("FileChooser.upFolderToolTipText", "Up One Level");
     UIManager.getDefaults().put("FileChooser.homeFolderToolTipText", "Home");
     UIManager.getDefaults().put("FileChooser.newFolderToolTipText", "Create New Folder");
     UIManager.getDefaults().put("FileChooser.listViewButtonToolTipText", "List");
     UIManager.getDefaults().put("FileChooser.detailsViewButtonToolTipText", "Details");
     UIManager.getDefaults().put("FileChooser.acceptAllFileFilterText", "All Files (*.*)");
     UIManager.getDefaults().put("ColorChooser.swatchesRecentText", "Recent:");

     //System.out.println(UIManager.getString("FileChooser.directoryOpenButtonToolTipText"));
   }

 }


 /****************************************************************************
  * JColorChooser��UI�ύX�iIDE�I�v�V�����Łu����v���ύX���ꂽ���ɌĂ΂��j
  * @param�@�Ȃ�
  * @return �Ȃ�
  ****************************************************************************/
 //------------------------------------------------------------------
 // ColorChooser
 // 2003/07/01 add suzuki
 //------------------------------------------------------------------
 private void changeColorChooserUI() {
   if (System.getProperty("language").equals("japanese") ) {
     UIManager.getDefaults().put("ColorChooser.cancelText", "�����");
     UIManager.getDefaults().put("ColorChooser.okText", "OK");
     UIManager.getDefaults().put("ColorChooser.resetText", "���Z�b�g");
     UIManager.getDefaults().put("ColorChooser.previewText", "�v���r���[");
     UIManager.getDefaults().put("ColorChooser.sampleText", "�T���v���e�L�X�g �T���v���e�L�X�g");
     UIManager.getDefaults().put("ColorChooser.swatchesRecentText", "�ŐV");
     UIManager.getDefaults().put("ColorChooser.swatchesNameText", "�T���v��");
     UIManager.getDefaults().put("ColorChooser.rgbRedText", "��");
     UIManager.getDefaults().put("ColorChooser.rgbBlueText", "��");
     UIManager.getDefaults().put("ColorChooser.rgbGreenText", "��");
   }
   else{
     UIManager.getDefaults().put("ColorChooser.cancelText", "Cancel");
     UIManager.getDefaults().put("ColorChooser.okText", "OK");
     UIManager.getDefaults().put("ColorChooser.resetText", "Reset");
     UIManager.getDefaults().put("ColorChooser.previewText", "Preview");
     UIManager.getDefaults().put("ColorChooser.sampleText", "Sample Text  Sample Text");
     UIManager.getDefaults().put("ColorChooser.swatchesRecentText", "Recent");
     //UIManager.getDefaults().put("ColorChooser.swatchesNameText", "Swatches");
     UIManager.getDefaults().put("ColorChooser.rgbRedText", "Red");
     UIManager.getDefaults().put("ColorChooser.rgbBlueText", "Blue");
     UIManager.getDefaults().put("ColorChooser.rgbGreenText", "Green");

   }

 }

 /****************************************************************************
  * �ύX����Ă���t�@�C���̐����擾
  * @param�@�Ȃ�
  * @return �Ȃ�
  ****************************************************************************/
 private int getChangeFileCount() {
   int chgcnt = 0;
   JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
   int iframe_index=0;
   for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
     String name = (String)emu.nextElement();
     JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
     iframes[iframe_index] = iframe;
   }

   //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
   for (int j=0; j<iframes.length; j++ ) {
     String name = iframes[j].getTitle();
     Editor edit = (Editor)htEditor.get(name);
     if (edit.jetx.changecnt > 0 ) {
       chgcnt++;
     }
   }

   return chgcnt;

 }

 /****************************************************************************
  * �ύX����Ă���t�@�C���̖��̂��擾
  * @param�@�Ȃ�
  * @return �Ȃ�
  ****************************************************************************/
 private Vector getChangeFileName() {
   int chgcnt = 0;
   Vector vecChangeFileName = new Vector();
   JInternalFrame[] iframes = new JInternalFrame[htEditor.size()];
   int iframe_index=0;
   for (Enumeration emu = htEditor.keys(); emu.hasMoreElements();iframe_index++ ) {
     String name = (String)emu.nextElement();
     JInternalFrame iframe =  (JInternalFrame)htEditorFrame.get(name);
     iframes[iframe_index] = iframe;
   }

   //JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
   for (int j=0; j<iframes.length; j++ ) {
     String name = iframes[j].getTitle();
     Editor edit = (Editor)htEditor.get(name);
     if (edit.jetx.changecnt > 0 ) {
       vecChangeFileName.addElement(name);
     }
   }

   return vecChangeFileName;

 }


 /****************************************************************************
  * �v���W�F�N�g���J����Ă��Ȃ����ɕ\������JTextArea���쐬
  * @param�@�Ȃ�
  * @return JTextArea
  ****************************************************************************/
 private JTextArea createNotOpenProjectMsg() {
   JTextArea txtArea =  new JTextArea();
   txtArea.setEditable(false);
   txtArea.setText(getBilingualMsg("0199"));
   txtArea.setLineWrap(true);
   txtArea.setBackground(Color.lightGray);
   txtArea.setForeground(Color.blue);
   txtArea.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

   return txtArea;
 }


/*
  public void uidftest()  {
    System.out.println("UIManager Default Properties");
    UIDefaults df1 = UIManager.getDefaults(); // returns a HashTable
    Enumeration dfkeys = df1.keys(); // returns an Enumeration
    Enumeration dfEmu = df1.elements(); // returns an Enumeration
    while (dfEmu.hasMoreElements()) {
      Object key = dfEmu.nextElement();
      System.out.println(" key: "+ key );
      //System.out.println(" key: "+ key +"\tval: "+ df1.get("ColorChooser.cancelText") );
    }

    System.out.println("\n KEY / VALUE list");
    while (dfkeys.hasMoreElements()) {
      Object key = dfkeys.nextElement();
      System.out.println(" key: "+ key +"\tval: "+ df1.get(key) );
      //System.out.println(" key: "+ key +"\tval: "+ df1.get("ColorChooser.cancelText") );
    }
    System.out.println("\n");

    Map defaults = new HashMap(UIManager.getLookAndFeelDefaults());

    Iterator it = defaults.keySet().iterator();
    while (it.hasNext()) {
    Object key = it.next();
    Object value = defaults.get(key);

    if (value instanceof UIDefaults.LazyValue) {
    value = ((UIDefaults.LazyValue)value).createValue(null);
    } else if (value instanceof UIDefaults.ActiveValue) {
    value = ((UIDefaults.ActiveValue)value).createValue(null);
    }

    System.out.println(key + " = " + value);
    }
  } // end ctor
*/

  // added by takagaki
  public String getProjectPath()
  {
    return project.getProjectPath();
  }
}