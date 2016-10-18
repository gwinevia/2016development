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

//ビューア周りの中心となるクラス
public class NewIf2 extends JPanel implements ActionListener, NewifItface {

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
  //private AclPanel aclPanel;
  //uchiya
  public AclPanel aclPanel;

  /** ACLエディタへのメッセージを表示する部品 */
  private JTextArea receiveArea;

  /** メッセージの内容を表示する部品 */
  private JTextArea msgArea;

  /** タブ */
  //uchiya
  //private JTabbedPane logTabbedPane;
  public JTabbedPane logTabbedPane;

  /** エージェントを表すもの */
  private DashTree2 treePane;
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
  private CopyAction copyAction = new CopyAction("Copy", null);

  /** メモリ表示 */
  private JLabel memoryLabel;

  /** ステップボタンなど */
  private JCheckBox nonStopCheck;
  private JButton stepButton;

  /** インデント用スペース */
  private static String INDENT = "    ";

  /** 置き換えるオブジェクト。offの場合はnull。*/
  private static ConsoleReplace consoleReplace = null;
  //uchiya
  //private ConsoleReplace consoleReplace = null;
  
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
 
  //ログ保管するかどうか create 05/02/17
  private boolean USELOG = false;
  
 
  /** コンストラクタ */
  NewIf2(String dvmname, DVM dvmparam, File msgfile, File dashdir) {

	String prop=null;
	//ログ保管の有無
	 if ((prop=System.getProperty("dash.log"))!=null &&
			prop.equalsIgnoreCase("on")){
			USELOG = true;
	//System.out.println("logを使います。"+this+" "+dvm);
			}
	//else{
  //System.out.println("logを使いません。"+this+" "+dvm);
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
    // ツールバー
    JToolBar toolbar = new JToolBar();

    /**************************************************************************/
    /* revised by cosmos 2003/01/18                                           */
    /* 修正内容：ファイルオープン・新規作成のボタンを追加                     */
    /**************************************************************************/
    /*
    JButton newfileBtn = new JButton (new actNewFile());
    JButton openfileBtn = new JButton (new actOpen());
    toolbar.add(newfileBtn);
    toolbar.add(openfileBtn);
    */
    /**************************************************************************/
    /* revised by cosmos 2003/01/18　ここまで                                  */
    /**************************************************************************/

    nonStopCheck = new JCheckBox(getImageIcon("resources/pause.gif"));
    nonStopCheck.setSelectedIcon(getImageIcon("resources/nonstop.gif"));
    nonStopCheck.setSelected(true); // ステップ動作
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
    toolbar.setFloatable(false);

    CheckBoxListener listener = new CheckBoxListener();
    nonStopCheck.addItemListener(listener);
    stepButton.addActionListener(listener);


    // Treeペイン
    treePaneModel = new DashTreeModel(dvmname);
    treePane = new DashTree2(this, treePaneModel);
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

    JPanel treePanel = new JPanel (new BorderLayout() );

    treePanel.add(toolbar,BorderLayout.NORTH);
    treePanel.add(treeScrollPane,BorderLayout.CENTER);

    //memoryLabel = new JLabel("? / ? Kb");
    //treePanel.add(memoryLabel, BorderLayout.SOUTH);

    // logタブ
    logArea = new JTextArea(""); //(dvmname="+dvmname+")\n");
    logArea.setEditable(false);
    logArea.setLineWrap(true);
    logScrollPane = new JScrollPane(logArea);
    logAreaLines = 0;

    // errorタブ
    errorLogArea = new JTextArea();
    errorLogArea.setEditable(false);
    errorLogArea.setLineWrap(true);
    errorScrollPane = new JScrollPane(errorLogArea);
    errorLogAreaLines = 0;

    // acl-editorタブ
    String options[] = { "Send" };
    aclPanel = new AclPanel(options, null, this, dvm);
    aclPanel.setContArea("()");
    JScrollPane aclScrollPane = new JScrollPane(aclPanel);

    // receiveタブ
    receiveArea = new JTextArea();
    receiveArea.setLineWrap(true);
    JScrollPane receiveScrollPane = new JScrollPane(receiveArea);

    // msgタブ
    msgArea = new JTextArea();
    msgArea.setEditable(false);
    msgArea.setLineWrap(true);
    JScrollPane msgScrollPane = new JScrollPane(msgArea);

    // タブ張り付け
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

  
  /** ImageIconを返す。*/
  private ImageIcon getImageIcon(String path) {
    java.net.URL url = this.getClass().getResource(path);
    return new ImageIcon(url);
  }
 
  static boolean allowMakeMemoryThread = true;
  /** メモリの監視を開始する */
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
      //マネージャを特定
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
    //treePaneModel.nextTurn();  →ここでは呼ばない。

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
   * メッセージの処理を行う。
   */
  private void showMessage(final DashMessage m) {
    //System.out.println ("NewIf Call showMessage()"); COSMOS

    //logTabbedPane.setSelectedIndex(0);
	  //logTabbedPane.setForegroundAt(0, eventColor);
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
      //treePaneModel.nextTurn(); はDashTree.showMsg()の中で呼ぶ。
    }
  }



	/** ログの記録　　
	 * @logText ログの内容
	 * @logKind ログの種類
	 * */
	private void writeLog (String logText, String logKind ) {
		//	ここからメッセージログ保存
		if(USELOG){
 	  String logFileName = "";
		// システムプロパティからパス付きログファイル名を取得する
		if (logKind.equals("msg") ) {
			logFileName = (String)System.getProperty("msgfilename");
			// MSGの場合、メッセージ番号も取得する
			String msgnoStr = (String)System.getProperty("msgno");
			// これを書き込むメッセージの先頭に追加
			logText = msgnoStr + " " + logText;
			
			// メッセージ番号を１増分して、システムプロパティに再書き込み
			System.setProperty("", new Integer(new Integer(msgnoStr).intValue() + 1).toString());			
		}
		else if (logKind.equals("err") ) {
			logFileName = (String)System.getProperty("errfilename");
		}
		else if (logKind.equals("log") ) {
			logFileName = (String)System.getProperty("logfilename");
		}
    
		// 追加書き込みのため、現在のログファイルの内容をベクターに取得し、
		// ベクターの最後の要素に追加するログ内容を追加する。
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

		// 上記で作成したベクターの内容を再度ログファイルに書き込む
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
      // 内部フレームの配列を取得する
      /*
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

  /** ACLエディタに入力されたメッセージを送信する */
  public void sendMessageFromACLeditor() {
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

    createACLHist (performative,1);
    createACLHist (to,2);
    createACLHist (content,3);
    
    // add mabune
    createAllACLHist (performative, to, content);   /* ACLエディタの全ての履歴を保存 */
    
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
    /*
    TreePath path = treePane.getSelectionPath();
    if (path == null) return; //念のため

    String name = path.getLastPathComponent().toString();
    Object[] msg = { "Kill "+name+". OK?" };
    int ans = JOptionPane.showConfirmDialog(getContentPane(), msg, "Confirm Dialog", JOptionPane.OK_CANCEL_OPTION);

    if (ans==JOptionPane.OK_OPTION)
      dvm.stopAgent(name);
    */
  }

  /**
   * エージェント記述ファイルまたはプロジェクトファイルを開く。
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


  /**
   * プロジェクトファイル(*.prj)を読み込み、何かする。
   */
  /* DVMに持っていった
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
      System.err.println("ファイル"+file+"がありません");
    } catch (IOException e) {
      e.printStackTrace();
    }
    ps.Parser parser = new ps.Parser(file.getName(), buffer.toString(), false);
    Vector lists = null;
    try {
      lists = parser.parseOAVinProject(file.getParentFile());
    } catch (ps.SyntaxException e) {
      System.err.println("警告: ファイル "+file+":"+e.lineno+": "+e.comment);
      lists.removeAllElements();
    }
    boolean error = false;
    for (Enumeration e = lists.elements(); e.hasMoreElements(); ) {
      Object element = e.nextElement();
      if (element instanceof String) {
        System.err.println(element); // エラー
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


    // 送信元エージェント名
    String from =msg.from;
    // 送信先エージェント名 
    String to = msg.to;
    // メッセージの送信元の環境名、またはnull。
    // nullの場合は、同じ環境内へのメッセージであることを表す。
    String departure = msg.departure;
    // メッセージの到着する環境名、またはnull。
    //  nullの場合は、同じ環境内へのメッセージであることを表す。
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
  //  ログ関係  ///////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////
  /**
   * ログをログタブに表示する。
   * 200行を越えたら、最後の100行にする。
   * @param s ログ
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
             //logArea.append("「"+dvm.getDVMname()+" str="+sb.toString()+s+"」\n");
             sb.delete(0,sb.length());
              logAreaLines++;
              String text = logArea.getText();
              if (logAreaLines > 200) {
                StringTokenizer st = new StringTokenizer(text,"\n");
                // 捨てる行
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
                // 捨てる行
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
      			logTabbedPane.setForegroundAt(i, eventColor);
            //logTabbedPane.setSelectedIndex(i);
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
              //logTabbedPane.setSelectedIndex(i);
			  logTabbedPane.setForegroundAt(i, eventColor);
   
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
   * プロパティdash.noconsoleがonの場合、
   * 標準出力をallタブに、標準エラー出力をerrorタブに切替える。
   * Repository.main()から呼び出すと、
   * main()を実行していたスレッドが消滅したときにパイプが壊れる。
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

    /** System.setOut(), System.setErr()するStream */
    private PrintStream stdoutPrintStream, stderrPrintStream;

    
    /** System.{out|err}.println()したバイト列を格納するStream*/
    ByteArrayOutputStream stdoutStream, stderrStream;

    int num=0;
    /** コンストラクタ */
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
	 //System.err.println("replaceConsoleのコンストラクタ起動回数"+num);
    }
    
    NewIf2 targetif = null;
    
    void replace(NewIf2 if2) {
     //System.setOut(stdoutPrintStream);
      //System.setErr(stderrPrintStream);
      if(targetif==if2) return;
      
      	synchronized(stdoutPrintStream){
      	//System.out.println("旧if= "+targetif.getDvmName()+" 新if="+if2.getDvmName());
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
    /** 表示 */
    synchronized void print() {
		//targetif.println("前回の情報");
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
  /** 以下、コスモス追加分                                                       */
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

  // Windowメニュー更新
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
	// System.out.println(name+ "エージェントにkillメッセージ送信終了");
     ((DashAgent)dvm.agTable.get(name)).killForce();
     //dvm.killAgent(name,null);

      // 消す。
      DefaultMutableTreeNode parent = treePaneModel.removeAgentnode(name);
      treePane.removeEntry(name);

      DefaultTreeModel treeModel = (DefaultTreeModel)treePane.getModel();
      treeModel.nodeStructureChanged(treePaneModel.getRootnode());

      // 見せる。
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

    //読み込み処理
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

    
    
    //読み込み処理
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
    
    //書き込み処理
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