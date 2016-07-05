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

  /** エラーを表示する部品 */
  private JTextArea errorLogArea;
  private JScrollPane errorScrollPane;
  private int errorLogAreaLines;

  /** 全てのログを表示する部品 */
  private JTextArea logArea;
  private JScrollPane logScrollPane;
  private int logAreaLines;

  /** ACLエディタ */
  private AclPanel aclPanel;
  //public AclPanel aclPanel;

  /** ACLエディタへのメッセージを表示する部品 */
  private JTextArea receiveArea;

  /** メッセージの内容を表示する部品 */
  private JTextArea msgArea;

  /** タブ */
  private JTabbedPane logTabbedPane;

  /** エージェントを表すもの */
  private DashTree treePane;
  private DashTreeModel treePaneModel;

  /** リポジトリ型DVMならtrue */
  private boolean isRtype;

  /** ポップアップ */
  private JPopupMenu treePopupMenu;

  /** フレーム */
  private JFrame ifFrame;
  private JSplitPane split;

  /** アクション */
  private SendAction sendAction = new SendAction("Send", null);
  private InspectAction inspectAction = new InspectAction("Inspect", null);
  private CutAction cutAction = new CutAction("Cut", null);
  private CopyAction copyAction = new CopyAction("Copy", null);
  private PasteAction pasteAction = new PasteAction("Paste", null);

  /** メモリ表示 */
  private JLabel memoryLabel;

  /** ステップボタンなど */
  private JCheckBox nonStopCheck;
  private JButton stepButton;

  /** インデント用スペース */
  private static String INDENT = "    ";

  /** 置き換えるオブジェクト。offの場合はnull。*/
  private static ConsoleReplace consoleReplace = null;

  /** 置き換えるオブジェクト。offの場合はfalse。
   * trueの場合、ProdSysのメインループでsleepしている。
   */
  private static boolean usingConsoleReplace = false;

  /** openするデフォルトのディレクトリ */
  private File defaultOpenDir;

  /** EditMenuを表示したときのFocusedObjectを保持するため(?)の変数 */
  private EditMenuListener editMenuListener;

  /** synchronized用オブジェクト */
  ////private String objectForSync = "s";

  /** nextTurnのためのkeyを保持する変数 */
  Long keyForShowMsg;

  /** nonstopモードの待ち時間。200msがデフォルト。*/
  private int waittimeForNonstop;

  /****************************************************************************/
  /* コスモス追加分                                                             */
  /****************************************************************************/
  /** コードエディタ用デスクトップペイン **/
  public JDesktopPane CodeEditorDesktop  = null;
  public JDesktopPane CodeEditorDesktop2 = null;

  /** WIndow処理用 **/
  // カスケード表示用のインクリメント値
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
  /** コンストラクタ */
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

    // Treeペイン
    treePaneModel = new DashTreeModel(dvmname);
    treePane = new DashTree(this, treePaneModel);
    treePane.setRootVisible(false);
    treePane.setCellRenderer(new DashRenderer());
    JScrollPane treeScrollPane = new JScrollPane(treePane);
    treeScrollPane.setBorder(new TitledBorder("Env. & Agent"));
    // Treeペインのアクション
    treePane.addMouseListener(new TreeSelect());
    treePopupMenu = new JPopupMenu("Tree");
    treePopupMenu.add(inspectAction);
    treePopupMenu.add(sendAction);
    treePopupMenu.addSeparator();
    treePopupMenu.add(copyAction);
    // Treeを開く
    treePane.expand();

    // logタブ
    logArea = new JTextArea(""); //(dvmname="+dvmname+")\n");
    logArea.setLineWrap(true);
    logScrollPane = new JScrollPane(logArea);
    logAreaLines = 0;

    // errorタブ
    errorLogArea = new JTextArea();
    errorLogArea.setLineWrap(true);
    errorScrollPane = new JScrollPane(errorLogArea);
    errorLogAreaLines = 0;

    // acl-editorタブ
    String options[] = { "Send" };
    aclPanel = new AclPanel(options, null, this, null);
    aclPanel.setContArea("()");
    JScrollPane aclScrollPane = new JScrollPane(aclPanel);

    // receiveタブ
    receiveArea = new JTextArea();
    receiveArea.setLineWrap(true);
    JScrollPane receiveScrollPane = new JScrollPane(receiveArea);

    // msgタブ
    msgArea = new JTextArea();
    msgArea.setLineWrap(true);
    JScrollPane msgScrollPane = new JScrollPane(msgArea);

    // タブ張り付け
    logTabbedPane = new JTabbedPane(JTabbedPane.TOP);
    logTabbedPane.addTab("log", logScrollPane);
    logTabbedPane.addTab("error", errorScrollPane);
    logTabbedPane.addTab("acl-editor", aclScrollPane);
    logTabbedPane.addTab("receive", receiveScrollPane);
    logTabbedPane.addTab("msg", msgScrollPane);

    // メニューバー
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
    /* 修正内容：Windowメニュー（Windowの整列等の処理）                            */
    /**************************************************************************/
    JMenu windowMenu = new JMenu("Window");
    windowMenu.add(menuItem("Tile_Horizonal"));
    windowMenu.add(menuItem("Tile_Vertical"));
    windowMenu.add(menuItem("Cascade"));
    windowMenu.add(menuItem("Iconize All"));
    menubar.add(windowMenu);
    /**************************************************************************/
    /* revised by cosmos 2003/01/22                                           */
    /* 修正内容：Windowメニュー（Windowの整列等の処理）ここまで                     */
    /**************************************************************************/

    /* cout
    if (msgfile != null)
      menubar.add(makeMessageMenu(msgfile));
    */
    setJMenuBar(menubar);

    // ツールバー
    JToolBar toolbar = new JToolBar();

    /**************************************************************************/
    /* revised by cosmos 2003/01/18                                           */
    /* 修正内容：ファイルオープン・新規作成のボタンを追加                            */
    /**************************************************************************/
    JButton newfileBtn = new JButton (new actNewFile());
    JButton openfileBtn = new JButton (new actOpen());
    toolbar.add(newfileBtn);
    toolbar.add(openfileBtn);
    /**************************************************************************/
    /* revised by cosmos 2003/01/18　ここまで                                  */
    /**************************************************************************/

    nonStopCheck = new JCheckBox(getImageIcon("resources/pause.gif"));
    nonStopCheck.setSelectedIcon(getImageIcon("resources/nonstop.gif"));
    nonStopCheck.setSelected(false); // ステップ動作
    nonStopCheck.setHorizontalAlignment(SwingConstants.CENTER);
    nonStopCheck.setFocusPainted(false);
    nonStopCheck.setBorderPainted(true);
    nonStopCheck.setMargin(new Insets(0,0,1,0));
    toolbar.add(nonStopCheck);

    stepButton = new JButton(getImageIcon("resources/step.gif"));
    stepButton.setEnabled(false);      // 押下不可能
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
    /* 修正内容：ACLエディタにコードエディタを表示するデスクトップペインの領域を追加     */
    /**************************************************************************/
    Container container = this.getContentPane();
    container.add(split, BorderLayout.CENTER);
    container.add(toolbar, BorderLayout.NORTH);
/*
    // ACLエディタを格納するパネルを作成します
    JPanel AclEditorPanel = new JPanel (new BorderLayout() );
    AclEditorPanel.add(split, BorderLayout.CENTER);

    // コードエディタを表示するデスクトップペインを作成します
    CodeEditorDesktop = new JDesktopPane();
    CodeEditorDesktop2 = new JDesktopPane();
    CodeEditorDesktop.setBackground(Color.gray);
    CodeEditorDesktop2.setBackground(Color.gray);

    JPanel ViewerPanel = new JPanel();
    Viewer viewer = new Viewer (dvmname,dvm);
    canvas  = new ViewerCanvasW2(dvmname);

    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayout(0, 3));

    // Swingのラジオボタンのグループ化
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
    canvas.stepLabel = stepLabel;   // ViewerCanvasに教える

    JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout());
    //panel2.add("Center", canvas);
    panel2.add("South", panel1);

    //JSplitPane split3 = new JSplitPane (JSplitPane.VERTICAL_SPLIT,true,CodeEditorDesktop,canvas);
    JSplitPane split3 = new JSplitPane (JSplitPane.VERTICAL_SPLIT,true,CodeEditorDesktop,panel2);
    split3.setDividerLocation(300);
    split3.setOneTouchExpandable(true);
    //ViewerPanel.setVisible(false);

    // ACLエディタパネルとデスクトップペインを左右に分割します
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
    // 検索・取り込み画面作成
    //--------------------------------------------------------------------------
    // プロジェクトツリー作成
    projectTree1 = new ProjectTree("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
    // 検索パネル作成
    //searchPanel = new SearchPanel(this);
    searchPanel = new SearchPanel(null);
    // ツリーと検索パネルを左右に分割する
    JSplitPane spane = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree1.TreePanel,searchPanel );
    spane.setDividerLocation(150);
    spane.setOneTouchExpandable(true);

    //--------------------------------------------------------------------------
    // 開発画面作成
    //--------------------------------------------------------------------------
    projectTree2 = new ProjectTree("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
    projectTree2.addMouseListener(new TreeSelect2());
    JSplitPane spaneDevScreen = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree2.TreePanel,CodeEditorDesktop2 );
    spaneDevScreen.setDividerLocation(150);
    spaneDevScreen.setOneTouchExpandable(true);

    // 動作シミュレータ画面作成
    projectTree3 = new ProjectTree("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
    //simulatorPanel = new Simulator(treeScrollPane);
    simulatorPanel = new Simulator(null);
    JSplitPane spaneSimulator = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree3.TreePanel,simulatorPanel );
    spaneSimulator.setDividerLocation(150);
    spaneSimulator.setOneTouchExpandable(true);

    // 登録画面作成
    projectTree4 = new ProjectTree("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
    savePanel = new SavePanel(null);
    JSplitPane spaneSave = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,true,projectTree4.TreePanel,savePanel );
    spaneSave.setDividerLocation(150);
    spaneSave.setOneTouchExpandable(true);

    JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
    jTabbedPane.addTab("検索/取り込み", spane);
    jTabbedPane.addTab("開発", spaneDevScreen);
    jTabbedPane.addTab("開発/動作ｼﾐｭﾚｰﾄ", spaneSimulator);
    //jTabbedPane.addTab("開発/動作ｼﾐｭﾚｰﾄ", p2);
    jTabbedPane.addTab("登録/更新", spaneSave);
    //jTabbedPane.setSelectedIndex(1);
    container.add(jTabbedPane, BorderLayout.CENTER);

    //File file2 = new File("C:\\dash-1.9.7h\\scripts\\cnp\\CnpManager.dash");
    //dvm.addLoadQueue(file2);

    //↓↓↓↓　Old Code　↓↓↓↓
    //Container container = this.getContentPane();
    //container.add(split, BorderLayout.CENTER);
    //↑↑↑↑　Old Code　↑↑↑↑

    //************************************************************************
    // revised by cosmos 2003/01/18 ここまで
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
    /* 修正内容：初期表示のサイズを大きくした                                       */
    /**************************************************************************/
    /** 現在の画面サイズを取得 **/
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
    //↓↓↓↓　Old Code　↓↓↓↓
    setSize(300,400);
    //↑↑↑↑　Old Code　↑↑↑↑
    /**************************************************************************/
    /* revised by cosmos 2003/01/18 ここまで                                   */
    /**************************************************************************/

  }

  private JMenuItem menuItem(String label) {
    JMenuItem item = new JMenuItem(label);
    item.addActionListener(this);
    return item;
  }




  /** "<?Repository>"などを置き換える。*/
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

  /** ImageIconを返す。*/
  private ImageIcon getImageIcon(String path) {
    java.net.URL url = this.getClass().getResource(path);
    return new ImageIcon(url);
  }

  /** メモリの監視を開始する */
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


  /** nonStopCheckを押した状態(nonstop状態)にする */
  public void setNonstop() {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if (!nonStopCheck.isSelected()) {
            nonStopCheck.doClick();
          }}});
  }

  /**
   * エージェントを環境に追加する。次の場合に呼ばれる。
   * 1)リポジトリエージェントがファイルから生成されたとき
   * 2)インスタンスエージェントが生成されたとき
   *
   * @param name エージェント名
   */
  public void addAgent(final String name) {
    final Long key = treePaneModel.waitTurn();

    // 追加する。
    Runnable r = new Runnable() {
        public void run() {
          DefaultMutableTreeNode envnode = treePaneModel.addAgentnode(name);
          DefaultTreeModel treeModel = (DefaultTreeModel)treePane.getModel();
          treeModel.nodeStructureChanged(treePaneModel.getRootnode());

          // 見せる。
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

  /** エージェントを消す */
  public void removeAgent(final String name) {
    final Long key = treePaneModel.waitTurn();

    Runnable r = new Runnable() {
        public void run() {
          // 消す。
          DefaultMutableTreeNode parent = treePaneModel.removeAgentnode(name);
          treePane.removeEntry(name);

          DefaultTreeModel treeModel = (DefaultTreeModel)treePane.getModel();
          treeModel.nodeStructureChanged(treePaneModel.getRootnode());

          // 見せる。
          TreeNode nodes[] = treeModel.getPathToRoot(parent);
          TreePath path = new TreePath(nodes);
          treePane.expandPath(path);
          treePaneModel.nextTurn(key);
        }
      };
    SwingUtilities.invokeLater(r);
    //treePaneModel.nextTurn();
  }

  /** 本当に消されたのを待つ。*/
  public void confirmSync() {
    Long key = treePaneModel.waitTurn(); // キューの最後で待つ。
    treePaneModel.nextTurn(key); //
  }

  /**
   * メッセージの処理を行う。
   */
  public void showMsg(DashMessage m) {
    //System.out.println ("NewIf Call ShowMsg()");  COSMOS

    keyForShowMsg = treePaneModel.waitTurn();
    showMessage(m);

    canvas.showMsg(m, isRtype);
    //treePaneModel.nextTurn();  →ここでは呼ばない。

    /*DTM
    synchronized (objectForSync) {
      showMessage(m);
    }
    */
  }

  /**
   * メッセージの処理を行う。
   */
  private void showMessage(final DashMessage m) {
    //System.out.println ("NewIf Call showMessage()"); COSMOS

    // specify_managerなら、木を作る。
    if (m.isSpecifyManager()) {
      //System.out.println ("NewIf Call showMessage()-specify_manager"); COSMOS
      Runnable r = new Runnable() {
          public void run() {
            // 対象
            DefaultMutableTreeNode manager =
              treePaneModel.getAgentnode(m.from);
            DefaultMutableTreeNode contractor =
              treePaneModel.getAgentnode(m.to);

            // 付け替える
            manager.add(contractor);

            DefaultTreeModel treeModel = (DefaultTreeModel)treePane.getModel();
            treeModel.nodeStructureChanged(treePaneModel.getRootnode());

            // 見せる。
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

      //treePaneModel.nextTurn(); はDashTree.showMsg()の中で呼ぶ。
    }
  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (e.getSource() instanceof JButton && command.equals("Send")) {
      // ADD COSMOS
      // 内部フレームの配列を取得する
      JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
      if (iframes.length == 0 )
        return;

      for (int i=0; i<iframes.length; i++ ) {
        if (((ps.InspectorItface)iframes[i]).isChanenged() ) {
        //if (iframes[i].getTitle().indexOf("*") != -1 ) {
          Object[] options = { "OK", "CANCEL" };

          int ret = JOptionPane.showOptionDialog(null,
              "編集されているファイルが存在します。\n編集内容をリロードしてよろしいですか？", "Warning",
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

  /** ACLエディタに入力されたメッセージを送信する */
  private void sendMessageFromACLeditor() {
    String performative = aclPanel.getPerfField();
    String to = aclPanel.getToField();
    String content = aclPanel.getContArea();

    if (performative.equals("") || to.equals("") || content.equals("")) {
      printlnE("[エラー]: acl-editorに入力していない欄があります。");
      return;
    }

    if (!content.startsWith("(") || !content.endsWith(")")) {
      printlnE("[エラー]: acl-editorの:contentの入力が、\"(\"で始まっていないか、\")\"で終っていません。");
      return;
    }

    if (to.equals(DashMessage.IF)) {
      printlnE("[エラー]: 「_interface@環境名」ならOK。");
      return;
    }

    // 両端の( )を取り除く(TAFとの互換性のため)→2.0では取り除かない。
    /*
    int length = content.length();
    content = content.substring(1,length-1);
    */

    // toに@が付いているなら、arrivalを設定する。
    String arrival = null;
    int p = to.indexOf('@');
    int q = to.lastIndexOf('@');
    if (p>0)
      if (p==q) {
        arrival = to.substring(p+1);
        to = to.substring(0, p);
        if (arrival.equals("") || to.equals("")) {
          printlnE("[エラー]: acl-editorの :toの値が不正です。");
          return;
        }
      } else {
        printlnE("[エラー]: acl-editorの :toの値が不正です(複数の@)。");
        return;
      }

    try {
      // Createrのキューに入れる。
      dvm.sendMessageFromUser(null, performative, null, to, arrival, content, null);
    } catch (DashException e) {
      printlnE("[エラー]: ACLエディタに入力した:contentの値が不正です。");
    }

  }

  /** エージェントを終了させる。
      releaseの処理については、CM_J.finalizeInstAg()参照！*/
  private void killAgent() {
    TreePath path = treePane.getSelectionPath();
    if (path == null) return; //念のため

    String name = path.getLastPathComponent().toString();
    Object[] msg = { "Kill "+name+". OK?" };
    int ans = JOptionPane.showConfirmDialog(getContentPane(), msg, "Confirm Dialog", JOptionPane.OK_CANCEL_OPTION);

    if (ans==JOptionPane.OK_OPTION)
      dvm.stopAgent(name);
  }

  /**
   * エージェント記述ファイルまたはプロジェクトファイルを開く。
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

  /** ファイルフィルタ */
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


 

  /** メモリに関する情報を表示する */
  private void printMemory() {
    Runtime runtime = Runtime.getRuntime();
    long total = runtime.totalMemory();
    long free = runtime.freeMemory();
    println("[Memory]");
    println("total: "+total/1024+"Kb\n"+
            "use: "+(total - free)/1024+"Kb\n"+
            "free: "+free/1024+"Kb\n");

  }

  /** VM上の全てのスレッドを標準出力に表示する。
      http://java-house.etl.go.jp/ml/archive/j-h-b/001346.html#bodyより。*/
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

  /** DASHで起動した全てのスレッドを表示する */
  private void printThreads(boolean allSystem) {
    // まずtopあるいはmainまで遡る
    ThreadGroup root = dvm.dashThreads;
    if (allSystem)
      while (root.getParent()!=null) root = root.getParent();
    else
      while (!root.getName().equals("main")) root = root.getParent();

    //降りて行く。
    Hashtable hash = getThreadHash(root);

    // 表示
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

    // まずスレッド
    Thread[] threads = new Thread[root.activeCount()];
    root.enumerate(threads, false);
    for (int i=0; i<threads.length; i++)
      if (threads[i] != null)
        vector.add(threads[i].getName()); // .getName()を消すと詳細になる。

    // 次にグループ
    ThreadGroup[] groups = new ThreadGroup[root.activeGroupCount()];
    root.enumerate(groups, false);
    for (int i=0; i<groups.length; i++)
      if (groups[i] != null)
        vector.add(getThreadHash(groups[i]));

    Hashtable hash = new Hashtable();
    hash.put(root.getName(), vector); // .getName()を消すと詳細になる。
    return hash;
  }

  /**
   * このホストで起動されているRMIレジストリに登録されている名前を表示する。
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

  /** 旧インスペクタのコマンド */
  /* cout
  private void printAgentInfo() {
    TreePath path = treePane.getSelectionPath();
    if (path != null) { // 念のため
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

  /** 旧インスペクタのコマンド */
  /* cout
  private void printAgentLog() {
    TreePath path = treePane.getSelectionPath();
    if (path != null) { // 念のため
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

  /** 受信したメッセージをreceiveタブに表示する。*/
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
  //  ログ関係  ///////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////


  /**
   * ログをログタブに表示する。
   * 200行を越えたら、最後の100行にする。
   * @param s ログ
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
                // 捨てる行
                int loops = st.countTokens() - 100;
                for (int i=0; i<loops; i++)
                  st.nextToken();
                // 残す行
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
   * エラーをエラータブに表示する。
   * @param s エラーの説明
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
                // 捨てる行
                int loops = st.countTokens() - 100;
                for (int i=0; i<loops; i++)
                  st.nextToken();
                // 残す行
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
            // タブを表示させる
            int i = logTabbedPane.indexOfTab("error");
            logTabbedPane.setSelectedIndex(i);
          }
        };
      SwingUtilities.invokeLater(r);
    } else
      System.out.println(s);
  }

  /**
   * 例外とその説明をエラータブに表示する。
   * @param s 説明
   * @param e 例外
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

  /** 木を表示のための内部クラス */
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

      if (node.getLevel()==1) {                        // 環境
        if (expanded)
          this.setIcon(openedIcon);
        else
          this.setIcon(closedIcon);
      } else if (value == treePaneModel.getACLnode()) {// ACLエディタ
        this.setIcon(aclIcon);
      } else {                                         // エージェント
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

  /** Editメニューに関する内部クラス */
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

  /** Agentメニューに関する内部クラス */
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

  /** 木のポップアップメニューに関する内部クラス */
  class TreeSelect extends MouseAdapter {

    /** Linux用 */
    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger())
        showPopupMenu(e);
      else if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        deselect(e);
    }

    /** Windows用 */
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

    /** インスペクタの表示 */
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
     * ポップアップメニューを開く。
     * エージェント名の上で開いた場合、そのエージェントを選択してから開く。
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

  /** Sendアクション */
  class SendAction extends AbstractAction {
    public SendAction(String label, Icon icon) { super(label, icon); }
    /** ACLエディタを開く */
    public void actionPerformed(ActionEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            TreePath path = treePane.getSelectionPath();
            if (path != null) { // 念のため
              String receiver = path.getLastPathComponent().toString();
              int i = logTabbedPane.indexOfTab("acl-editor");
              logTabbedPane.setSelectedIndex(i);
              aclPanel.setToField(receiver);
            }
          }};
      SwingUtilities.invokeLater(r);
    }
  }

  /** Inspectアクション */
  class InspectAction extends AbstractAction {
    public InspectAction(String label, Icon icon) { super(label, icon); }
    //インスペクタを開く
    public void actionPerformed(ActionEvent e) {
      TreePath path = treePane.getSelectionPath();
      if (path != null) { // 念のため
        String receiver = path.getLastPathComponent().toString();
        dvm.openInspector(receiver);
      }
    }
  }

  /** Cutアクション */
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

  /** Pasteアクション */
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

  /** Copyアクション */
  class CopyAction extends AbstractAction {
    public CopyAction(String label, Icon icon) { super(label, icon); }

    /**
     * システムクリップボードにコピーする。
     * JTreeが対象なら、選択されたエージェント名をコピーする。
     * JTextComponentが対象なら、テキストをコピーする。
     */
    public void actionPerformed(ActionEvent e) {
      Runnable r = new Runnable() {
          public void run() {
            //Object focusedObj = ifFrame.getFocusOwner();
            Object focusedObj = editMenuListener.focusedObj;
            if (focusedObj instanceof JTree) {
              TreePath path = ((JTree)focusedObj).getSelectionPath();
              if (path != null) { //念のため
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
   * Messageメニューを選択したときに呼び出されるActionListener
   */
  class MessageAction implements ActionListener {

    /**
     * key   = メニューアイテム.サブメニューアイテム。
     * value = メッセージ
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



  /** チェックボックス/ボタン用 */
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

  /** ステップボタンが押されるのを待つ **/
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
  //  NoConsole関係  //////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////

  /**
   * プロパティadips97.noconsoleがonの場合、
   * 標準出力をallタブに、標準エラー出力をerrorタブに切替える。
   * Repository.main()から呼び出すと、
   * main()を実行していたスレッドが消滅したときにパイプが壊れる。
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

    /** System.setOut(), System.setErr()するStream */
    private PrintStream stdoutPrintStream, stderrPrintStream;

    /** System.{out|err}.println()したバイト列を格納するStream*/
    ByteArrayOutputStream stdoutStream, stderrStream;

    /** コンストラクタ */
    ConsoleReplace() {
      stdoutStream = new ByteArrayOutputStream();
      stdoutPrintStream = new PrintStream(stdoutStream);
      System.setOut(stdoutPrintStream);

      stderrStream = new ByteArrayOutputStream();
      stderrPrintStream = new PrintStream(stderrStream);
      System.setErr(stderrPrintStream);
    }

    /** 表示 */
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
  /** 以下、コスモス追加分                                                       */
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
  // Window処理メニュー
  //***************************************************************************
  // 水平タイルメソッド
  void setTile_H (int optionitems) {
    // 内部フレームの配列を取得する
    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;

    int itemcount = 0;
    // アイコン化されていない内部フレーム数を調べる
    for (int i=0; i<iframes.length; i++ ) {
      if (!iframes[i].isIcon() ) {
        itemcount++;
      }
    }

    // デスクトップの内部境界矩形を求める
    Insets is = CodeEditorDesktop.getInsets();
    // デスクトップの内部幅を求める
    int width = CodeEditorDesktop.getWidth() - is.left - is.right;
    // デスクトップの内部高を求める
    int height = CodeEditorDesktop.getHeight() - is.top - is.bottom;

    // 必要なタイル数を求める
    // 1.余りが出ないケースでは、「平方根×平方根」とする
    // 2.余りが出るケースでは、「平方根×（平方根＋２）」とする
    int hitems, vitems, excess;

    // optoinitemsオプションを使った絞込みがあるケース
    if (optionitems != 0 && optionitems <= itemcount ) {
      itemcount = optionitems;
    }

    int tempi = (int)Math.sqrt(itemcount);
    // 残りのアイテム数を算出する
    excess = itemcount - (int)Math.pow(tempi,2);
    // 余りの出ないケース
    if (excess == 0 ) {
      hitems = vitems = tempi;
    }
    else { // 余りの出るケース
      hitems = tempi;
      vitems = tempi + 2; // プラス２は余りの出ない平方根の累乗値－１を意味する
      // 余分な行をなくす
      if (itemcount <= hitems * (vitems-1) ) {
        vitems--;
      }
    }

    // タイルサイズを求める
    int itemwidth = width / hitems;
    int itemheight = height / vitems;
    // タイルを右へ、下へとレイアウトする
    for (int counter = 0, rows = 0; rows < vitems && counter < itemcount; rows++ ) {
      // こちらのループが先に回る
      for (int cols = 0; cols < hitems && counter < itemcount; cols++ ) {
        iframes[counter++].reshape(is.left + itemwidth * cols,
          is.top + itemheight * rows, itemwidth, itemheight );
      }
    }
    updateWindowMenu ( );

  }

  // 垂直タイル表示メソッド
  void setTile_V (int optionitems ) {
    // 内部フレームの配列を取得する
    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;

    int itemcount = 0;
    // アイコン化されていない内部フレーム数を調べる
    for (int i=0; i<iframes.length; i++ ) {
      if (!iframes[i].isIcon() )
        itemcount++;
    }

    // デスクトップの内部境界矩形を求める
    Insets is = CodeEditorDesktop.getInsets();
    // デスクトップの内部幅を求める
    int width = CodeEditorDesktop.getWidth() - is.left - is.right;
    // デスクトップの内部高を求める
    int height = CodeEditorDesktop.getHeight() - is.top - is.bottom;

    // 必要なタイル数を求める
    // 1.余りの出ないケースでは、「平方根×平方根」とする
    // 2.余りの出るケースでは、「平方根×（平方根＋２）」とする
    int hitems, vitems, excess;

    // optionitemオプションを使った絞込みがあるケース
    if (optionitems != 0 && optionitems < itemcount )
      itemcount = optionitems;

    int tempi = (int)Math.sqrt (itemcount );
    // 残りのアイテム数を算出する
    excess = itemcount - (int)Math.pow(tempi, 2 );
    // 余りの出ないケース
    if (excess == 0 ) {
      hitems = vitems = tempi;
    }
    else { // 余りの出るケース
      vitems = tempi;
      hitems = tempi + 2; // プラス２は余りの出ない平方根の累乗値－１を意味する
      // 余分な行をなくす
      if (itemcount <= vitems *(hitems-1) ) {
        hitems--;
      }
    }

    // タイルサイズを求める
    int itemwidth = width / hitems;
    int itemheight = height / vitems;
    // タイルを下へ、右へとレイアウトする
    for (int counter = 0, cols=0; cols<hitems && counter < itemcount; cols++ ) {
      // こちらのループが先に回る
      for (int rows=0; rows<vitems && counter < itemcount; rows++ ) {
        iframes[counter++].reshape(is.left + itemwidth * cols,
           is.top + itemheight * rows, itemwidth, itemheight );
      }
    }
    updateWindowMenu ( );
  }

  // カスケード表示メソッド
  void setCascade ( ) {
    // 内部フレームの配列を取得する
    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;
    // 現在のxnewとynewの値を保存しておく
    int savexnew = xnew, saveynew = ynew;
    xnew = xincrement; ynew = yincrement;
    // 逆順に表示する
    for (int i=iframes.length-1; i>=0; i--  ) {
      if (!iframes[i].isIcon() ) {
        iframes[i].setSize(newwidth, newheight );
        iframes[i].setLocation (xnew, ynew );
        updateLocation (iframes[i] );
      }
    }

    // xnewとynewの値を復元する
    xnew = savexnew; ynew = saveynew;
    updateWindowMenu ( );
  }

  void updateLocation (JInternalFrame frame ) {
    // デスクトップの内部境界矩形を求める
    Insets is = CodeEditorDesktop.getInsets();
    // デスクトップの内部幅を求める
    int width = CodeEditorDesktop.getWidth() - is.left - is.right;
    // デスクトップの内部高を求める
    int height = CodeEditorDesktop.getHeight() - is.top - is.bottom;

    if (width == 0 ) {
      width = 470;
      height = 330;
    }

    // 溢れれば、初期位置に戻す
    if (frame.getX() + newwidth + xincrement > width ||
        frame.getY() + newheight + yincrement > height ) {
      xnew = xincrement; ynew = yincrement;
    }
    else {
      xnew += xincrement; ynew += yincrement;
    }
    updateWindowMenu ( );
  }

  // アイコン化
  private void setIconize ( ) {
    /*
    JMenuBar menubar = this.getJMenuBar();
    //JOptionPane.showMessageDialog(null, new Integer(menubar.getMenuCount()).toString());
    JMenu menu = menubar.getMenu(5);
    menu.getMenuComponent(0).setEnabled(false);
    menu.getMenuComponent(1).setEnabled(false);
    menu.getMenuComponent(2).setEnabled(false);
    */
    // 内部フレームの配列を取得する
    JInternalFrame[] iframes = CodeEditorDesktop.getAllFrames();
    if (iframes.length == 0 )
      return;

    for (int i=0; i<iframes.length; i++ ) {
      // 内部フレームをアイコン化する
      try {
        iframes[i].setIcon (true);
      }
      catch (Exception e ) {}
     }
     updateWindowMenu ( );
  }

  // WIndowメニュー更新
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


  // ファイル新規作成アクション
  class actNewFile extends AbstractAction {
    // コンストラクタ
    actNewFile () { super ("",getImageIcon ("resources/new.gif")); }
    public void actionPerformed (ActionEvent e ) {
      JOptionPane.showMessageDialog(null, "現在、この機能は使用できません。\r作成中です...");
    }
  }

  // ファイルオープンアクション
  class actOpen extends AbstractAction {
    // コンストラクタ
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

    /** Linux用 */
    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger())
        JOptionPane.showMessageDialog(null, "mousePressed");
      else if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        deselect (e );
    }

    /** Windows用 */
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

    /** インスペクタの表示 */
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        TreePath path = projectTree2.getPathForLocation(e.getX(), e.getY());

        String receiver = path.getLastPathComponent().toString();

        //JOptionPane.showMessageDialog(null, receiver);
        //myThread thread = new myThread();
        //thread.setDesktopPane(CodeEditorDesktop2);
        //thread.start();
        JInternalFrame iframe = new JInternalFrame(receiver);
        Container con = iframe.getContentPane(); //フレームの表示領域をとってくる
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

    // 追加する。
    Runnable r = new Runnable() {
        public void run() {
          DefaultMutableTreeNode envnode = treePaneModel.addAgentnode(name);
          DefaultTreeModel treeModel = (DefaultTreeModel)treePane.getModel();
          treeModel.nodeStructureChanged(treePaneModel.getRootnode());

          // 見せる。
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

