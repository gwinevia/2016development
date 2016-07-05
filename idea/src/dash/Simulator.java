package dash;

import dashviz.*;
import java.awt.*;
import javax.swing.JPanel;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.border.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>タイトル：動作シミュレート</p>
 * @author cosmosweb nakagawa
 * @version 1.0
 */
public class Simulator extends JPanel implements ActionListener {

	public JDesktopPane inspectorDesktop = null;

	/** タブ */
	private JTabbedPane logTabbedPaneR;

	private JTabbedPane logTabbedPaneW;

	/** エラーを表示する部品 */
	private JTextArea errorLogAreaR;

	private JScrollPane errorScrollPaneR;

	
	private int errorLogAreaLinesR;

	private JTextArea errorLogAreaW;

	private JScrollPane errorScrollPaneW;

	private int errorLogAreaLinesW;

	/** 全てのログを表示する部品 */
	private JTextArea logAreaR;

	private JScrollPane logScrollPaneR;

	private int logAreaLinesR;

	private JTextArea logAreaW;

	private JScrollPane logScrollPaneW;

	private int logAreaLinesW;

	/** ACLエディタ */
	private AclPanel aclPanelR;

	private AclPanel aclPanelW;

	/** ACLエディタへのメッセージを表示する部品 */
	private JTextArea receiveAreaR;

	private JTextArea receiveAreaW;

	/** メッセージの内容を表示する部品 */
	private JTextArea msgAreaR;

	private JTextArea msgAreaW;

	/** タブ */
	private JTabbedPane logTabbedPane;

	private BorderLayout borderLayout1 = new BorderLayout();

	/** WorkPlacePanel */
	WorkplacePanel[] wpPanel = null;

	/** RepositoryPanel */
	RepositoryPanel repPanel = null;

	/** ワークプレースビューア */
	public ViewerCanvasW2[] canvasW = null;

	/** リポジトリビューア */
	public ViewerCanvasR2 canvasR = null;

	/** 複数ワークプレースを表示するためのタブ */
	private JTabbedPane wpTab = null;

	/** ワークプレースを配置するパネル */
	private JPanel[] canvasW_BasePanel = null;

	/** ワークプレースのボタンを配置するパネル */
	private JPanel[] canvasW_BtnPanel = null;

	/** ワークプレースが初期化済みか否かのフラグ */
	private boolean[] WpInitialized = new boolean[5];

	/** プロジェクトファイル */
	private String ProjectFileName = "";

	/** プロジェクト　*/
	private Project project = null;

	/** 親フレーム */
	private IdeaMainFrame parentframe = null;

	/** リポジトリで読み込んだエージェントファイル */
	private Vector vecRepLoadAgent = new Vector();

	/** ワークプレースで読み込んだエージェントファイル */
	private Vector[] vecWpLoadAgent = new Vector[5];

	private Vector[] vecWpLoadAgent2 = new Vector[5];

	/** 未使用 */
	private Vector vecWpReadCount = new Vector();

	private TitledBorder[] titleBdr1 = null;

	private TitledBorder titleBdr2 = null;

	//----------------------------------------------------------------------------
	// メニュー
	//----------------------------------------------------------------------------
	/** メニュー */
	public JMenuBar menubar = null;

	/** ファイルメニュー*/
	private JMenu filemenu = null;

	/** ファイルメニューのメニューアイテム */
	// 新規
	private JMenuItem newFileMenuItem = null;

	// プロジェクトを開く
	private JMenuItem filemenu1 = null;

	// プロジェクトの削除
	private JMenuItem deletePrjMenuItem = null;

	// 新規プロジェクト
	private JMenuItem newProjectMenuItem = null;

	// 終了
	private JMenuItem filemenu2 = null;

	// 開き直す
	private JMenu openagainprjmenu = null;

	// プロジェクト履歴の消去
	private JMenuItem subMenuItem = null;

	/** ツールメニュー **/
	private JMenu toolMenu_DevScreen = null;

	/** ツールメニューのメニューアイテム */
	// エディタ設定
	private JMenuItem toolMenu1 = null;

	// キーワード設定
	private JMenuItem toolMenu2 = null;

	// 外部ツール
	private JMenuItem toolMenu3 = null;

	// IDEオプション
	private JMenuItem toolMenu4 = null;

	// シミュレータオプション
	private JMenuItem toolMenu5 = null;

	// 外部ツール
	private static JMenu outsideTool = null;

	private JPopupMenu openagainprjPopupMenu = new JPopupMenu();

	//add uchiya
	//メッセージメニュー
	private JMenu messageMenu_DevScreen = null;

	private JMenuItem send_acl_menu = null;

	private JMenu set_acl_menu = null;

	//add mabune
	private JMenuItem set_acl_history = null; // aclエディタの履歴

	/** ツールバー */
	public JToolBar toolbar;

	/** ツールバーで使用するボタン */
	// プロジェクト開く */
	private JButton openprjBtn = null;

	// プロジェクトを開きなおす
	private JToggleButton openagainprjBtn;

	/** 動作シミュレートのツールボタン */
	private JButton btn1 = null; //Agを全てリポジトリに読み込む

	private JButton btn2 = null; //選択されたAgをリポジトリに読み込む

	private JButton btn3 = null; //Agを全てワークプレースに読み込む

	private JButton btn4 = null; //選択されたAgをワークプレースに読み込む

	private JButton btn5 = null; //リポジトリのAgを消去

	private JButton btn6 = null; //ワークプレースのAgを消去

	private JButton btn7 = null; //リポジトリのAgを再読込

	private JButton btn8 = null; //ワークプレースのAgを再読込

	private JButton btn9 = null; //activity

	private JButton btn10 = null; //cstmiz

	// ツールバーの「開きなおす」から表示するポップアップメニュー
	private JMenuItem popMenuItem = null;

	// ワークプレースの数
	private int wp_cnt = 1;

	// ワークプレースの最大数
	public int wp_max_cnt = 5;

	private String DefaultDir = "";

	private String LoadDir = "";

	/****************************************************************************
	 * コンストラクタ
	 * @param frame IdeaMainFrameを受け取る
	 * @return なし
	 ****************************************************************************/
	public Simulator(IdeaMainFrame parentframe) {
		try {
			this.parentframe = parentframe;

			//treeScrollPane = tree;
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/****************************************************************************
	 * 初期処理
	 * @param frame IdeaMainFrameを受け取る
	 * @return なし
	 ****************************************************************************/
	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);

		String wpCntStr = System.getProperty("dash.wp.cnt");
		//System.out.println("wpcntstr= "+wpCntStr);
		wp_cnt = new Integer(wpCntStr).intValue();
		//----------------------
		// メニューを作成
		//----------------------
		createMenu();

		//----------------------
		// ツールバーを作成
		//----------------------
		createToolBar1();
		JToolBar tbar = createToolBar2();
		this.add(tbar, BorderLayout.NORTH);

		//----------------------
		// インスペクタを表示するDesktopPaneを作成する
		//----------------------
		inspectorDesktop = new JDesktopPane();
		inspectorDesktop.setBackground(Color.gray);

		//----------------------
		// リポジトリを作成
		//----------------------
		repPanel = new RepositoryPanel();
		// リポジトリが使用するDVMにリポジトリの操作するメインフレームを設定
		repPanel.rep.getNewIf().getDVM().setParentFrame(parentframe);
		// インスペクタを表示するDesktopPaneを教える
		repPanel.setInspectorDesktopPane(inspectorDesktop);

		//----------------------
		// ワークプレース作成
		//----------------------
		/*
		 wpPanel = new WorkplacePanel[wp_cnt];
		 for (int i=0; i<wp_cnt; i++ ) {
		 wpPanel[i] = new WorkplacePanel();
		 // ワークプレースが使用するDVMにワークプレースの操作するメインフレームを設定
		 wpPanel[i].wp.getNewIf().getDVM().setParentFrame(parentframe);
		 
		 wpPanel[i].wp.getNewIf().getDVM().setWpIndex(i);
		 
		 // インスペクタを表示するDesktopPaneを教える
		 wpPanel[i].setInspectorDesktopPane(inspectorDesktop);
		 }
		 */
		wpPanel = new WorkplacePanel[wp_max_cnt];
		for (int i = 0; i < wp_max_cnt; i++) {
			wpPanel[i] = new WorkplacePanel();
			// ワークプレースが使用するDVMにワークプレースの操作するメインフレームを設定
			wpPanel[i].wp.getNewIf().getDVM().setParentFrame(parentframe);

			wpPanel[i].wp.getNewIf().getDVM().setWpIndex(i);

			// インスペクタを表示するDesktopPaneを教える
			wpPanel[i].setInspectorDesktopPane(inspectorDesktop);
		}

		//--------------------------------------------------------------------------
		// ビューア作成
		//--------------------------------------------------------------------------
		// ワークプレース
		//--------------------------------------------------------------------------
		canvasW = new ViewerCanvasW2[wp_max_cnt];
		canvasW_BasePanel = new JPanel[wp_max_cnt];
		canvasW_BtnPanel = new JPanel[wp_max_cnt];
		titleBdr1 = new TitledBorder[wp_max_cnt];

		for (int i = 0; i < wp_max_cnt; i++) {
			vecWpLoadAgent[i] = new Vector();
			vecWpLoadAgent2[i] = new Vector();
			canvasW[i] = new ViewerCanvasW2(wpPanel[i].getDvmName());

			canvasW_BtnPanel[i] = new JPanel();
			//ラジオボタンを使う場合
			//canvasW_BtnPanel[i].setLayout(new GridLayout(1, 2));
			canvasW_BtnPanel[i].setLayout(new GridLayout(1, 2));
			//canvasW_BtnPanel[i].setLayout(new GridLayout(3,1));

			// Swingのラジオボタンのグループ化
			ButtonGroup canvasW_BtnGroup = new ButtonGroup();
			JRadioButton canvasW_cb1 = new JRadioButton("non-stop", true);
			JRadioButton canvasW_cb2 = new JRadioButton("step", false);
			canvasW_cb1.setActionCommand("wp" + new Integer(i).toString()
					+ "_non-stop");
			canvasW_cb2.setActionCommand("wp" + new Integer(i).toString()
					+ "_step");
			canvasW_cb1.addActionListener(this);
			canvasW_cb2.addActionListener(this);
			// ラジオボタンのグループ化
			canvasW_BtnGroup.add(canvasW_cb1);
			canvasW_BtnGroup.add(canvasW_cb2);

			JCheckBox canvasW_chkbox1 = new JCheckBox("step");
			canvasW_chkbox1.addActionListener(this);
			canvasW_chkbox1.setActionCommand("wp" + new Integer(i).toString()
					+ "_step-check");
			// ラジオボタンをパネルに入れる
			//canvasW_BtnPanel[i].add(canvasW_cb1);
			//canvasW_BtnPanel[i].add(canvasW_cb2);
			canvasW_BtnPanel[i].add(canvasW_chkbox1);

			JLabel stepLabelW = new JLabel("", Label.RIGHT);
			//stepLabelW.setVisible(false);
			canvasW_BtnPanel[i].add(stepLabelW);
			canvasW[i].stepLabel = stepLabelW; // ViewerCanvasに教える

			canvasW_BasePanel[i] = new JPanel();

			JLabel labal5 = new JLabel();
			JLabel labal6 = new JLabel();
			JLabel labal7 = new JLabel();
			JLabel labal8 = new JLabel();

			ImageIcon iconWidth = getImageIcon("resources/width.gif");
			ImageIcon iconHeight = getImageIcon("resources/height.gif");
			labal5.setIcon(iconWidth);
			labal6.setIcon(iconHeight);
			labal7.setIcon(iconWidth);
			labal8.setIcon(iconHeight);

			JPanel p2 = new JPanel(new BorderLayout());
			p2.add(canvasW[i], BorderLayout.CENTER);
			p2.add(labal5, BorderLayout.NORTH);
			p2.add(labal6, BorderLayout.EAST);
			p2.add(labal7, BorderLayout.SOUTH);
			p2.add(labal8, BorderLayout.WEST);
			JScrollPane canvasW_spane0 = new JScrollPane(p2);

			canvasW_BasePanel[i].setLayout(new BorderLayout());
			canvasW_BasePanel[i].add("Center", canvasW_spane0);
			//canvasW_BasePanel[i].add("Center", p2);
			canvasW_BasePanel[i].add("South", canvasW_BtnPanel[i]);

			titleBdr1[i] = new TitledBorder(parentframe.getBilingualMsg("0050"));
			canvasW_BasePanel[i].setBorder(titleBdr1[i]);
		}

		//--------------------------------------------------------------------------
		// ビューア作成
		//--------------------------------------------------------------------------
		// リポジトリ
		//--------------------------------------------------------------------------
		canvasR = new ViewerCanvasR2(repPanel.getDvmName());
		//canvasR.setOpaque(false);
		//canvasR.setVisible(false);

		JPanel canvasR_BtnPanel = new JPanel();
		// ラジオボタンを使う場合canvasR_BtnPanel.setLayout(new GridLayout(0, 3));
		canvasR_BtnPanel.setLayout(new GridLayout(1, 2));

		// Swingのラジオボタンのグループ化
		ButtonGroup canvasR_BtnGroup = new ButtonGroup();
		JRadioButton canvasR_cb1 = new JRadioButton("non-stop", true);
		JRadioButton canvasR_cb2 = new JRadioButton("step", false);
		canvasR_cb1.setActionCommand("rep_non-stop");
		canvasR_cb2.setActionCommand("rep_step");
		canvasR_cb1.addActionListener(this);
		canvasR_cb2.addActionListener(this);
		// ラジオボタンのグループ化
		canvasR_BtnGroup.add(canvasR_cb1);
		canvasR_BtnGroup.add(canvasR_cb2);
		// ラジオボタンをパネルに入れる
		//canvasR_BtnPanel.add(canvasR_cb1);
		//canvasR_BtnPanel.add(canvasR_cb2);

		JCheckBox canvasR_chkbox1 = new JCheckBox("step");
		canvasR_chkbox1.addActionListener(this);
		canvasR_chkbox1.setActionCommand("rep_step-check");
		// ラジオボタンをパネルに入れる
		//canvasW_BtnPanel[i].add(canvasW_cb1);
		//canvasW_BtnPanel[i].add(canvasW_cb2);
		canvasR_BtnPanel.add(canvasR_chkbox1);

		JLabel stepLabelR = new JLabel("", Label.RIGHT);
		//stepLabelR.setVisible(false);
		canvasR_BtnPanel.add(stepLabelR);
		canvasR.stepLabel = stepLabelR; // ViewerCanvasに教える

		JPanel canvasR_BasePanel = new JPanel();
		canvasR_BasePanel.setLayout(new BorderLayout());

		JLabel labal1 = new JLabel();
		JLabel labal2 = new JLabel();
		JLabel labal3 = new JLabel();
		JLabel labal4 = new JLabel();

		ImageIcon iconWidth = getImageIcon("resources/width.gif");
		ImageIcon iconHeight = getImageIcon("resources/height.gif");
		labal1.setIcon(iconWidth);
		labal2.setIcon(iconHeight);
		labal3.setIcon(iconWidth);
		labal4.setIcon(iconHeight);

		JPanel p = new JPanel(new BorderLayout());
		p.add(canvasR, BorderLayout.CENTER);
		p.add(labal1, BorderLayout.NORTH);
		p.add(labal2, BorderLayout.EAST);
		p.add(labal3, BorderLayout.SOUTH);
		p.add(labal4, BorderLayout.WEST);
		JScrollPane canvasR_spane0 = new JScrollPane(p);

		canvasR_BasePanel.add("Center", canvasR_spane0);
		canvasR_BasePanel.add("South", canvasR_BtnPanel);

		titleBdr2 = new TitledBorder(parentframe.getBilingualMsg("0047"));
		canvasR_BasePanel.setBorder(titleBdr2);

		//----------------------
		// スプリットペインを作成
		//----------------------
		boolean RepWpChildWinMode = true;
		if (System.getProperty("dash.r_w.pos") == null) {
			RepWpChildWinMode = false;
		} else {
			if (System.getProperty("dash.r_w.pos").equals("fixed")) {
				RepWpChildWinMode = false;
			} else {
				RepWpChildWinMode = true;
			}
		}
		if (!RepWpChildWinMode) {
			JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					true, canvasR_BasePanel, repPanel);
			JSplitPane splitPane3 = null;
			if (wp_cnt > 0) {
				wpTab = new JTabbedPane(JTabbedPane.TOP);
				for (int i = 0; i < wp_cnt; i++) {
					JSplitPane splitPane2 = new JSplitPane(
							JSplitPane.VERTICAL_SPLIT, true,
							canvasW_BasePanel[i], wpPanel[i]);
					// for debug
					//JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,canvasW_BasePanel[i],new JPanel());
					splitPane2.setDividerLocation(333);
					splitPane2.setDividerSize(5);
					wpTab.add("WP" + new Integer(i + 1).toString(), splitPane2);
					wpPanel[i].wp.getNewIf().getDVM().setWpTab(wpTab);
				}
				//splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,splitPane1,wpTab);
				splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
						wpTab, inspectorDesktop);
			} else {
				JSplitPane splitPane2 = new JSplitPane(
						JSplitPane.VERTICAL_SPLIT, true, canvasW_BasePanel[0],
						wpPanel[0]);
				splitPane2.setDividerLocation(360);
				splitPane2.setDividerSize(5);
				splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
						splitPane1, splitPane2);
			}

			splitPane3.setOneTouchExpandable(true);
			//JSplitPane splitPane4 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,splitPane3,inspectorDesktop);
			JSplitPane splitPane4 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					true, splitPane1, splitPane3);
			splitPane4.setOneTouchExpandable(true);
			//viewer on/off
			if (System.getProperty("dash.viewer") != null
					&& System.getProperty("dash.viewer").equals("on")) {
				repPanel.setViewerCanvas(canvasR);
			}
			if (System.getProperty("dash.viewer") != null
					&& System.getProperty("dash.viewer").equals("on")) {
				for (int i = 0; i < wp_max_cnt; i++) {
					wpPanel[i].setViewerCanvas(canvasW[i]);
				}
			}
			splitPane1.setDividerLocation(360);
			//splitPane2.setDividerLocation(360);
			splitPane3.setDividerLocation(250);
			splitPane4.setDividerLocation(250);

			splitPane1.setDividerSize(5);
			//splitPane2.setDividerSize(5);
			//splitPane3.setDividerSize(5);
			//splitPane4.setDividerSize(5);

			//        this.add(splitPane,BorderLayout.SOUTH);
			this.add(splitPane4);
		} else {
			// リポジトリのChildWindow作成
			JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					true, canvasR_BasePanel, repPanel);
			//uchiya
			//viewer on/off
			//if(System.getProperty("dash.viewer")!=null&&System.getProperty("dash.viewer").equals("on")){
			repPanel.setViewerCanvas(canvasR);
			//}
			splitPane1.setDividerLocation(360);
			JInternalFrame iframe_rep = new JInternalFrame(parentframe
					.getBilingualMsg("0047"));
			Container con = iframe_rep.getContentPane(); //フレームの表示領域をとってくる
			con.add(splitPane1);
			iframe_rep.setResizable(true);
			iframe_rep.setMaximizable(true);
			iframe_rep.setClosable(true);
			iframe_rep.setIconifiable(true);
			Dimension desktopSize = inspectorDesktop.getSize();
			//iframe_rep.setSize (inspectorDesktop.getWidth(), inspectorDesktop.getHeight());
			iframe_rep.setSize(250, 620);
			iframe_rep.setLocation(0, 0);
			inspectorDesktop.add(iframe_rep);
			iframe_rep.setVisible(true);
			inspectorDesktop.setSelectedFrame(iframe_rep);
			iframe_rep.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			iframe_rep.setToolTipText(" ");
			/*
			 iframe_rep.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
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
			 */

			// ワークプレースのChildWindow作成
			JSplitPane splitPane3 = null;
			if (wp_cnt > 0) {
				wpTab = new JTabbedPane(JTabbedPane.TOP);
				for (int i = 0; i < wp_cnt; i++) {
					JSplitPane splitPane2 = new JSplitPane(
							JSplitPane.VERTICAL_SPLIT, true,
							canvasW_BasePanel[i], wpPanel[i]);
					// for debug
					//JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,canvasW_BasePanel[i],new JPanel());
					splitPane2.setDividerLocation(333);
					splitPane2.setDividerSize(5);
					wpTab.add("WP" + new Integer(i + 1).toString(), splitPane2);
					wpPanel[i].wp.getNewIf().getDVM().setWpTab(wpTab);
				}
				//splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,splitPane1,wpTab);
				//splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,wpTab,inspectorDesktop);
			} else {
				JSplitPane splitPane2 = new JSplitPane(
						JSplitPane.VERTICAL_SPLIT, true, canvasW_BasePanel[0],
						wpPanel[0]);
				splitPane2.setDividerLocation(360);
				splitPane2.setDividerSize(5);
				splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
						splitPane1, splitPane2);
			}

			JInternalFrame iframe_wp = new JInternalFrame(parentframe
					.getBilingualMsg("0050"));
			con = iframe_wp.getContentPane(); //フレームの表示領域をとってくる
			con.add(wpTab);
			iframe_wp.setResizable(true);
			iframe_wp.setMaximizable(true);
			iframe_wp.setClosable(false);
			iframe_wp.setIconifiable(true);
			//iframe_wp.setSize (inspectorDesktop.getWidth(), inspectorDesktop.getHeight());
			iframe_wp.setSize(250, 620);
			iframe_wp.setLocation(250, 0);
			inspectorDesktop.add(iframe_wp);
			iframe_wp.setVisible(true);
			inspectorDesktop.setSelectedFrame(iframe_wp);
			iframe_wp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			iframe_wp.setToolTipText(" ");

			if (System.getProperty("dash.viewer") != null
					&& System.getProperty("dash.viewer").equals("on")) {
				for (int i = 0; i < wp_max_cnt; i++) {
					wpPanel[i].setViewerCanvas(canvasW[i]);
				}
			}

			/*
			 iframe_rep.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
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
			 */
			this.add(inspectorDesktop);
		}

		//add uchiya
		//--------------------------------------------------------------------------
		// メッセージメニュー
		//--------------------------------------------------------------------------
		messageMenu_DevScreen = new JMenu(parentframe.getBilingualMsg("0222")
				+ "(M)");
		messageMenu_DevScreen.setMnemonic('M');
		//追加機能１（idea-1.2.2）
		//メッセージをファイルから送信する機能
		//旧dashのdirectedawardfile読み込み機能を拡張
		//ファイルからメッセージ群を読み込み、ACLエディタにセットして送信する。
		send_acl_menu = menuItem(parentframe.getBilingualMsg("0223"),
				"OpenACLFile", null);

		messageMenu_DevScreen.add(send_acl_menu);

		
		
		
		
		//ACLエディタへメッセージをセットする機能
		//NewIf2から移植
		set_acl_menu = new JMenu(parentframe.getBilingualMsg("0224"));
		// よみこみ
		StringBuffer buffer = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(((NewIf2) wpPanel[0].wp
					.getNewIf()).msgfile));
			while (br.ready()) {
				String line = br.readLine();
				if (line != null)
					buffer.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			JMenuItem item = new JMenuItem("messages.txt not found");
			set_acl_menu.add(item).setEnabled(false);
		} catch (IOException e) {
			e.printStackTrace();
			JMenuItem item = new JMenuItem("IOException occured.");
			set_acl_menu.add(item).setEnabled(false);
		} finally {
			try {
				if (br != null) {
					br.close();
					br = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		replaceMeta(buffer);

		// ぱーず
		ps.Parser parser = new ps.Parser("messages.txt", buffer.toString(),
				false);
		Vector lists = null;
		try {
			lists = parser.parseMessages();
		} catch (ps.SyntaxException e) {
			System.err.println(e);
		}

		if (lists == null)
			return;

		// メニュー作成
		MessageAction msgAct = new MessageAction();
		for (Enumeration e = lists.elements(); e.hasMoreElements();) {
			String item = (String) e.nextElement();
			JMenu menuItem = new JMenu(item);
			Vector subItems = (Vector) e.nextElement();
			for (Enumeration ee = subItems.elements(); ee.hasMoreElements();) {
				Hashtable hash = (Hashtable) ee.nextElement();
				String subItem = (String) hash.get("submenu");

				JMenuItem subMenuItem2 = new JMenuItem(subItem);
				subMenuItem2.addActionListener(msgAct);
				menuItem.add(subMenuItem2);
				String actionCommand = item + "." + subItem;
				subMenuItem2.setActionCommand(actionCommand);
				msgAct.putHash(actionCommand, hash);
			}
			set_acl_menu.add(menuItem);
		}
		messageMenu_DevScreen.add(set_acl_menu);

		
		
		
		
		
		
		
		
		
		
		// add mabune
		set_acl_history = new JMenuItem(parentframe.getBilingualMsg("0229"));

		HistoryAction histAct = new HistoryAction();
		set_acl_history.addActionListener(histAct);

		messageMenu_DevScreen.add(set_acl_history);

		// add uchiya 060607
		JMenuItem analyze_message = new JMenuItem(parentframe.getBilingualMsg("0231"));
		
		AnalyzeAction aa = new AnalyzeAction();
		analyze_message.addActionListener(aa);
		
		messageMenu_DevScreen.add(analyze_message);
		
		menubar.add(messageMenu_DevScreen);
	}

	/** "<?Repository>"などを置き換える。*/
	private void replaceMeta(StringBuffer buffer) {
		try {
			replaceMeta(buffer, "\"<?Repository>\"", System
					.getProperty("dash.r.name")
					+ ":" + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		replaceMeta(buffer, "\"<?Here>\"", wpPanel[wpTab.getSelectedIndex()].wp
				.getNewIf().getDvmName());
	}

	private void replaceMeta(StringBuffer buffer, String oldstr, String newstr) {
		int length = oldstr.length();
		while (true) {
			int start = buffer.toString().indexOf(oldstr);
			if (start < 0)
				break;
			buffer.replace(start, start + length, newstr);
		}
	}

	//プロジェクトを開く際のデフォルトのパス情報
	public void setDefaultDir(String indir) {
		DefaultDir = indir;
	}

	/****************************************************************************
	 * メニューバーを作成
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	private void createMenu() {
		menubar = new JMenuBar();

		filemenu = new JMenu(parentframe.getBilingualMsg("0001") + "(F)");
		filemenu.setMnemonic('F');

		newFileMenuItem = menuItem(parentframe.getBilingualMsg("0002")
				+ "(N)...", "NewFile", getImageIcon("resources/newfile.gif"));
		newFileMenuItem.setMnemonic('N');
		//parentframe.setMenuItemAccelerator ("new-file", newFileMenuItem );
		filemenu.add(newFileMenuItem);

		newProjectMenuItem = menuItem(parentframe.getBilingualMsg("0003")
				+ "(P)...", "NewProject", getImageIcon("resources/newprj.gif"));
		newProjectMenuItem.setMnemonic('P');
		filemenu.add(newProjectMenuItem);

		filemenu1 = menuItem(parentframe.getBilingualMsg("0004") + "(O)...",
				"OpenProject", getImageIcon("resources/openprj.gif"));
		filemenu2 = menuItem(parentframe.getBilingualMsg("0008") + "(X)",
				"Exit", null);
		filemenu1.setMnemonic('O');
		filemenu2.setMnemonic('X');

		//parentframe.setMenuItemAccelerator("open-project",filemenu1);
		//filemenu1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		filemenu.add(filemenu1);

		openagainprjmenu = new JMenu(parentframe.getBilingualMsg("0005")
				+ "(R)");
		openagainprjmenu.setMnemonic('R');
		openagainprjmenu.setIcon(getImageIcon("resources/openagainprj.gif"));
		openagainprjmenu.setEnabled(false);
		filemenu.add(openagainprjmenu);

		deletePrjMenuItem = menuItem(parentframe.getBilingualMsg("0116"),
				"DeleteProject", getImageIcon("resources/delfile.gif"));
		deletePrjMenuItem.setEnabled(false);
		filemenu.add(deletePrjMenuItem);

		filemenu.addSeparator();
		filemenu.add(filemenu2);
		menubar.add(filemenu);

		//--------------------------------------------------------------------------
		// ツールメニュー
		//--------------------------------------------------------------------------
		toolMenu_DevScreen = new JMenu(parentframe.getBilingualMsg("0021")
				+ "(T)");
		toolMenu_DevScreen.setMnemonic('T');

		// メニューアイテム作成
		toolMenu1 = menuItem(parentframe.getBilingualMsg("0022") + "(E)...",
				"SetupEditor", null);
		toolMenu2 = menuItem(parentframe.getBilingualMsg("0023") + "(K)...",
				"SetupKeyword", null);
		toolMenu4 = menuItem(parentframe.getBilingualMsg("0117") + "...",
				"SetupKeymap", null);
		toolMenu5 = menuItem(parentframe.getBilingualMsg("0118") + "...",
				"SimulatorOption", null);

		outsideTool = new JMenu(parentframe.getBilingualMsg("0024") + "(O)");
		outsideTool.setMnemonic('O');
		//outsideTool2 = new JMenu(parentframe.getBilingualMsg("0024") + "(O)");
		//outsideTool2.setMnemonic('O');
		//outsideTool2.setIcon(getImageIcon("resources/permeation.gif"));

		// ニーモニック設定
		toolMenu1.setMnemonic('E');
		toolMenu2.setMnemonic('K');

		toolMenu_DevScreen.add(toolMenu4);
		toolMenu_DevScreen.add(toolMenu1);
		toolMenu_DevScreen.add(toolMenu2);
		toolMenu_DevScreen.add(toolMenu5);
		toolMenu_DevScreen.addSeparator();
		toolMenu_DevScreen.add(outsideTool);

		toolMenu3 = menuItem(parentframe.getBilingualMsg("0025") + "(C)...",
				"SetOutsideTool", null);
		toolMenu3.setMnemonic('C');
		outsideTool.add(toolMenu3);

		//parentframe.readOutsideToolInfo ();

		if (parentframe.vecOutsideToolInfo.size() > 0) {
			outsideTool.addSeparator();
		}

		for (int i = 0; i < parentframe.vecOutsideToolInfo.size(); i++) {
			String wk = (String) parentframe.vecOutsideToolInfo.elementAt(i);

			StringTokenizer st = new StringTokenizer(wk, ",");
			int cnt = 0;
			while (st.hasMoreTokens()) {
				String data = st.nextToken();

				JMenuItem toolMenu6 = menuItem(new Integer(i + 1).toString()
						+ ":" + data, "OutsideTool_"
						+ new Integer(i).toString(), null);
				outsideTool.add(toolMenu6);
				break;
			}
		}
		menubar.add(toolMenu_DevScreen);
	}

	public void changeMenuItemAccelerator() {
		parentframe.setMenuItemAccelerator("new-file", newFileMenuItem);
		parentframe.setMenuItemAccelerator("open-project", filemenu1);
	}

	/****************************************************************************
	 * ツールバーを作成<br>
	 * プロジェクトを開く・開きなおす機能を持つツールバー
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	private void createToolBar1() {
		toolbar = new JToolBar();
		openprjBtn = new JButton(null, getImageIcon("resources/openprj.gif"));
		openagainprjBtn = new JToggleButton(null,
				getImageIcon("resources/openagainprj2.gif"));
		openagainprjBtn.setEnabled(false);

		openprjBtn.setActionCommand("OpenProject");
		openprjBtn.addActionListener(this);

		openprjBtn.setToolTipText(parentframe.getBilingualMsg("0004"));
		openagainprjBtn.setToolTipText(parentframe.getBilingualMsg("0005"));

		toolbar.add(openprjBtn);
		toolbar.add(openagainprjBtn);
		toolbar.setFloatable(false);
		openagainprjBtn
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						openagainprjBtn_stateChanged(e);
					}
				});

		openagainprjPopupMenu
				.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
					public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					}

					public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
						openagainprjPopupMenu_popupMenuWillBecomeInvisible(e);
					}

					public void popupMenuCanceled(PopupMenuEvent e) {
					}
				});
	}

	/****************************************************************************
	 * ツールバーを作成<br>
	 * 動作シミュレーションで使用するツールバー
	 * @param なし
	 * @return 作成されたツールバー
	 ****************************************************************************/
	private JToolBar createToolBar2() {
		JToolBar tbar = new JToolBar();
		tbar.setFloatable(false);

		// ボタン用イメージアイコンを作成する
		ImageIcon icon1 = getImageIcon("resources/rgall.gif");
		ImageIcon icon2 = getImageIcon("resources/rgsel.gif");
		ImageIcon icon3 = getImageIcon("resources/wpall.gif");
		ImageIcon icon4 = getImageIcon("resources/wpsel.gif");
		ImageIcon icon5 = getImageIcon("resources/rgdel.gif");
		ImageIcon icon6 = getImageIcon("resources/wpdel.gif");
		ImageIcon icon7 = getImageIcon("resources/rgre.gif");
		ImageIcon icon8 = getImageIcon("resources/wpre.gif");
		ImageIcon icon9 = getImageIcon("resources/activity.gif");
		ImageIcon icon10 = getImageIcon("resources/cstmiz.gif");

		// ボタン作成
		btn1 = new JButton(icon1);
		btn2 = new JButton(icon2);
		btn3 = new JButton(icon3);
		btn4 = new JButton(icon4);
		btn5 = new JButton(icon5);
		btn6 = new JButton(icon6);
		btn7 = new JButton(icon7);
		btn8 = new JButton(icon8);
		btn9 = new JButton(icon9);
		btn10 = new JButton(icon10);

		btn9.setEnabled(false);
		//btn10.setEnabled(false);

		btn1.addActionListener(this);
		btn2.addActionListener(this);
		btn3.addActionListener(this);
		btn4.addActionListener(this);
		btn5.addActionListener(this);
		btn6.addActionListener(this);
		btn7.addActionListener(this);
		btn8.addActionListener(this);
		btn9.addActionListener(this);
		btn10.addActionListener(this);

		btn1.setActionCommand("rep_all_load_exec");
		btn2.setActionCommand("rep_sel_load_exec");
		btn3.setActionCommand("wp_all_load_exec");
		btn4.setActionCommand("wp_sel_load_exec");
		btn5.setActionCommand("rep_del");
		btn6.setActionCommand("wp_del");
		btn7.setActionCommand("rep_reload");
		btn8.setActionCommand("wp_reload");
		btn9.setActionCommand("activity");
		btn10.setActionCommand("cstmiz");

		/*
		 btn1.addActionListener(new java.awt.event.ActionListener() {
		 public void actionPerformed(ActionEvent e) {
		 btn1_actionPerformed(e);
		 }
		 });
		 */

		btn1.setToolTipText(parentframe.getBilingualMsg("0051"));
		btn2.setToolTipText(parentframe.getBilingualMsg("0052"));
		btn3.setToolTipText(parentframe.getBilingualMsg("0053"));
		btn4.setToolTipText(parentframe.getBilingualMsg("0054"));
		btn5.setToolTipText(parentframe.getBilingualMsg("0055"));
		btn6.setToolTipText(parentframe.getBilingualMsg("0056"));
		btn7.setToolTipText(parentframe.getBilingualMsg("0057"));
		btn8.setToolTipText(parentframe.getBilingualMsg("0058"));
		btn9.setToolTipText(parentframe.getBilingualMsg("0059"));
		btn10.setToolTipText(parentframe.getBilingualMsg("0060"));

		// ツールバーにボタンを追加
		tbar.add(btn1);
		tbar.add(btn2);
		tbar.add(btn5);
		tbar.add(btn7);

		tbar.addSeparator();

		tbar.add(btn3);
		tbar.add(btn4);
		tbar.add(btn6);
		tbar.add(btn8);
		//tbar.add(btn9);
		tbar.add(btn10);
		return tbar;
	}

	/****************************************************************************
	 * イメージアイコンを取得
	 * @param path イメージファイルへのパス
	 * @return イメージアイコン
	 ****************************************************************************/
	private ImageIcon getImageIcon(String path) {
		java.net.URL url = this.getClass().getResource(path);
		return new ImageIcon(url);
	}

	/****************************************************************************
	 * ビューアの初期化
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	public void initialize() {
		for (int i = 0; i < wp_max_cnt; i++) {
			WpInitialized[i] = false;
		}
		for (int i = 0; i < wp_cnt; i++) {
			canvasW[i].initialize();
			WpInitialized[i] = true;
		}
		canvasR.initialize();
	}

	/****************************************************************************
	 * イベント処理
	 * @param e アクションイベント
	 * @return なし
	 ****************************************************************************/
	public void actionPerformed(ActionEvent e) /*throws java.io.IOException, java.lang.Exception*/{

		try {

			String action = e.getActionCommand();
			//System.out.println("action is "+action);
			//add uchiya
			if (action.equals("OpenACLFile")) {
				DashDefaults dd = new DashDefaults();
				JFileChooser c1 = new JFileChooser(dd.getDashdir());
				int ret = c1.showOpenDialog(this);
				File file = c1.getSelectedFile();
				if (ret != JFileChooser.APPROVE_OPTION || file == null)
					return;

				StringBuffer buffer = new StringBuffer();
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					while (br.ready()) {
						String line = br.readLine();
						if (line != null)
							buffer.append(line + "\n");
					}
				} catch (IOException ee) {
					ee.printStackTrace();
				}
				buffer.insert(0, "(ACL ");
				buffer.append(")");

				ps.Parser parser = new ps.Parser(file.toString(), buffer
						.toString(), false);
				Vector lists = null;
				try {
					lists = parser.parseMessagesForSendDA();
				} catch (ps.SyntaxException eee) {
					System.err.println(eee);
				}

				if (lists == null)
					return;
				for (int i = 0; i < lists.size(); i++) {
					Hashtable msgtable = (Hashtable) lists.elementAt(i);
					String performative = (String) msgtable.get("performative");
					String to = (String) msgtable.get("to");
					String arrival = (String) msgtable.get("arrival");
					String content = (String) msgtable.get("content");

					if (arrival != null)
						to = to + "@" + arrival;
					((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
							.setPerfField(performative);
					((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
							.setToField(to);
					((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
							.setContArea(content);
					((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf())
							.sendMessageFromACLeditor();
				}
			}

			// ↓未使用
			if (action.endsWith("non-stop") || action.endsWith("step")) {
				if (action.indexOf("wp") != -1) {
					String s = action.substring(action.indexOf("_") + 1);
					s = action.substring(2, action.indexOf("_"));

					int wpIndex = wpTab.getSelectedIndex();

					canvasW[wpIndex].changeItem(action.substring(action
							.indexOf("_") + 1));
				} else {
					canvasR.changeItem(action
							.substring(action.indexOf("_") + 1));
				}
			}
			// ↑未使用部分ここまで

			//-----------------------------------------------------------------------
			// ビューアの「Step」チェックボックスがクリックされたとき
			//-----------------------------------------------------------------------
			if (action.endsWith("step-check") || action.endsWith("step")) {
				JCheckBox cb = (JCheckBox) e.getSource();
				String changeitem = "";
				if (cb.isSelected()) {
					changeitem = "step";
				} else {
					changeitem = "non-stop";
				}
				if (action.indexOf("wp") != -1) {
					String s = action.substring(action.indexOf("_") + 1);
					s = action.substring(2, action.indexOf("_"));

					int wpIndex = wpTab.getSelectedIndex();

					canvasW[wpIndex].changeItem(changeitem);
				} else {
					if (cb.isSelected()) {
						changeitem = "step";
					} else {
						changeitem = "non-stop";
					}
					canvasR.changeItem(changeitem);
				}
			}
			//-----------------------------------------------------------------------
			// ﾘﾎﾟｼﾞﾄﾘAgとして全てのAgを起動
			//-----------------------------------------------------------------------
			else if (action.equals("rep_all_load_exec")) {
				//clear_Rep();
				//repExec ("ALL_AGENT", parentframe.getBilingualMsg("0051"));
				repExec("ALL_AGENT");

			}
			//-----------------------------------------------------------------------
			// ﾘﾎﾟｼﾞﾄﾘAgとして指定したAgを起動
			//-----------------------------------------------------------------------
			else if (action.equals("rep_sel_load_exec")) {
				//clear_Rep();
				//repExec ("SELECT_AGENT", parentframe.getBilingualMsg("0052"));
				repExec("SELECT_AGENT");
			}
			//-----------------------------------------------------------------------
			// ﾜｰｸﾌﾟﾚｰｽAgとして全てのAgを起動
			//-----------------------------------------------------------------------
			else if (action.equals("wp_all_load_exec")) {
				//clear_Wp();
				//wpExec ("ALL_AGENT", parentframe.getBilingualMsg("0053"));
				wpExec("ALL_AGENT");
			}
			//-----------------------------------------------------------------------
			// ﾜｰｸﾌﾟﾚｰｽAgとして指定したAgを起動
			//-----------------------------------------------------------------------
			else if (action.equals("wp_sel_load_exec")) {
				//clear_Wp();
				//wpExec ("SELECT_AGENT", parentframe.getBilingualMsg("0054"));
				wpExec("SELECT_AGENT");
			}
			//-----------------------------------------------------------------------
			// 仮想ﾘﾎﾟｼﾞﾄﾘのAgを全て消去
			//-----------------------------------------------------------------------
			else if (action.equals("rep_del")) {
				clear_Rep();
			}
			//-----------------------------------------------------------------------
			// 仮想ﾜｰｸﾌﾟﾚｰｽのAgを全て消去
			//-----------------------------------------------------------------------
			else if (action.equals("wp_del")) {
				clear_Wp();
			}
			//-----------------------------------------------------------------------
			// 仮想ﾘﾎﾟｼﾞﾄﾘのAgをﾘﾛｰﾄﾞ
			//-----------------------------------------------------------------------
			else if (action.equals("rep_reload")) {
				if (project == null)
					return;
				clear_Rep();

				Vector vecFilenameWithPath = (Vector) project
						.getFileNamesWithPath();
				Vector vecSelectFileWk = new Vector();
				for (int i = 0; i < vecRepLoadAgent.size(); i++) {
					String SelectFileName = (String) vecRepLoadAgent
							.elementAt(i);
					boolean flag = false;
					for (int j = 0; j < vecFilenameWithPath.size(); j++) {
						String FilenameWithPath = (String) vecFilenameWithPath
								.elementAt(j);
						if (FilenameWithPath.endsWith(File.separator
								+ SelectFileName)) {
							vecSelectFileWk.addElement(FilenameWithPath);
						}
					}
				}

				//repPanel.loadProjectAgents(project.getProjectPath(),vecRepLoadAgent, LoadDir );
				repPanel.loadProjectAgents(project.getProjectPath(),
						vecSelectFileWk, LoadDir);
			}
			//-----------------------------------------------------------------------
			// 仮想ﾜｰｸﾌﾟﾚｰｽのAgをﾘﾛｰﾄﾞ
			//-----------------------------------------------------------------------
			else if (action.equals("wp_reload")) {
				if (project == null)
					return;
				int wpIndex = wpTab.getSelectedIndex();
				//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecWpLoadAgent, vecWpReadCount);
				Vector vecAgentName = wpPanel[wpIndex].wp.getNewIf()
						.getAllAgentName();
				for (int i = 0; i < vecAgentName.size(); i++) {
					String AgentName = (String) vecAgentName.elementAt(i);
					AgentName = AgentName.substring(0, AgentName.indexOf("."));
					AgentName = AgentName + ".dash";
					vecAgentName.setElementAt(AgentName, i);
				}

				clear_Wp();
				//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecWpLoadAgent[wpIndex], null,DefaultDir);

				Vector vecFilenameWithPath = (Vector) project
						.getFileNamesWithPath();
				Vector vecSelectFileWk = new Vector();
				for (int i = 0; i < vecAgentName.size(); i++) {
					String SelectFileName = (String) vecAgentName.elementAt(i);
					boolean flag = false;
					for (int j = 0; j < vecFilenameWithPath.size(); j++) {
						String FilenameWithPath = (String) vecFilenameWithPath
								.elementAt(j);
						if (FilenameWithPath.endsWith(File.separator
								+ SelectFileName)) {
							vecSelectFileWk.addElement(FilenameWithPath);
						}
					}
				}

				//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecAgentName, null,LoadDir);
				wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),
						vecSelectFileWk, null, LoadDir);
			}
			//-----------------------------------------------------------------------
			// ｱｸﾃｨﾋﾞﾃｨ図生成
			//-----------------------------------------------------------------------
			else if (action.equals("activity")) {
				int wpIndex = wpTab.getSelectedIndex();
				if (wpPanel[wpIndex].wp.getNewIf().getDVM().comInt
						.getNameServer() == null) {
					JOptionPane.showMessageDialog(null, wpPanel[wpIndex].wp
							.getNewIf().getDVM().comInt.getNameServerErrMsg());
					return;
				}
				//String [][] selecter = { {"name","CnpManager"},{"name","DB-access"} };
				String[][] selecter = { { "name", "Sample010" } };
				Vector v = wpPanel[wpIndex].wp.getNewIf().getDVM().comInt
						.lookup(selecter);
				//wpPanel.wp.newif.getDVM().comInt.
				int size = v.size();
				System.out.println(size);
				for (Enumeration e1 = v.elements(); e1.hasMoreElements();) {
					String[] data = (String[]) e1.nextElement();
					for (int i = 0; i < data.length; i++) {
						System.out.println(data[i]);
					}
				}
			}
			//-----------------------------------------------------------------------
			// シミュレーター設定を開く
			//-----------------------------------------------------------------------
			else if (action.equals("cstmiz")
					|| action.equals("SimulatorOption")) {
				//	System.out.println("option");
				SimulatorOption dlg = new SimulatorOption(parentframe);
				//dlg.show();
				dlg.setVisible(true);
				if (dlg.getResult()) {
					// 動作シミュレータ再設定
					getRepDVM().finalizeDVM();
					getWpDVM(0).finalizeDVM();
					getWpDVM(1).finalizeDVM();
					getWpDVM(2).finalizeDVM();
					getWpDVM(3).finalizeDVM();
					getWpDVM(4).finalizeDVM();

					getRepDVM().RmiModule_ReCreate();
					getWpDVM(0).RmiModule_ReCreate();
					getWpDVM(1).RmiModule_ReCreate();
					getWpDVM(2).RmiModule_ReCreate();
					getWpDVM(3).RmiModule_ReCreate();
					getWpDVM(4).RmiModule_ReCreate();

					//getDVM().RmiModule_ReCreate();
					String wpCntStr = System.getProperty("dash.wp.cnt");
					wp_cnt = new Integer(wpCntStr).intValue();
					int cnt = wpTab.getComponentCount();

					if (wp_cnt > cnt) {
						for (int i = cnt; i < wp_cnt; i++) {
							JSplitPane splitPane2 = new JSplitPane(
									JSplitPane.VERTICAL_SPLIT, true,
									canvasW_BasePanel[i], wpPanel[i]);
							splitPane2.setDividerLocation(360);
							splitPane2.setDividerSize(5);
							wpTab.add("WP" + new Integer(i + 1).toString(),
									splitPane2);
							wpPanel[i].wp.getNewIf().getDVM().setWpTab(wpTab);
						}
					} else if (wp_cnt < cnt) {
						for (int i = wp_cnt; i < cnt; i++) {
							int wpIndex = i;
							canvasW[wpIndex].removeAgentAll();
							wpPanel[wpIndex].removeAgentAll();
							wpPanel[wpIndex].clearLog();
							canvasW[wpIndex].repaint();
							vecWpLoadAgent[wpIndex].clear();
							vecWpLoadAgent2[wpIndex].clear();

							JInternalFrame[] iframes = inspectorDesktop
									.getAllFrames();
							for (int j = 0; j < iframes.length; j++) {

								if (iframes[j].getToolTipText().equals(
										"Workplace")) {
									inspectorDesktop.remove(iframes[j]);
								}
							}

							inspectorDesktop.repaint();

							wpTab.remove(wp_cnt);
						}
					}
					wpTab.repaint();
					for (int i = 0; i < wp_cnt; i++) {
						if (!WpInitialized[i]) {
							canvasW[i].initialize();
							WpInitialized[i] = true;
						}
					}
					//for (int i=0; i<wp_cnt; i++ ) {
					//  wpTab.getComponentCount()
					//}
				}

				//dlg.dispose();
				// 設定ﾌｧｲﾙを開く
			}
			//-----------------------------------------------------------------------
			// プロジェクトを開く
			//-----------------------------------------------------------------------
			else if (action.equals("OpenProject")) {
				parentframe.openprojectfile();
			}
			//-----------------------------------------------------------------------
			// プロジェクトを開き直す
			//-----------------------------------------------------------------------
			else if (action.equals("againPrjct")) {
				if (e.getSource() instanceof JMenuItem) {
					JMenuItem menuitem = (JMenuItem) e.getSource();
					parentframe.readProjectFile(menuitem.getText());
				}

			}
			//-----------------------------------------------------------------------
			// プロジェクト履歴削除
			//-----------------------------------------------------------------------
			else if (action.equals("deletehist")) {
				parentframe.deleteOpenHist();
			}
			//-----------------------------------------------------------------------
			// 終了
			//-----------------------------------------------------------------------
			else if (action.equals("Exit")) {
				parentframe.SystemExit();
			}
			//--------------------------------------------------------------------------
			// エディタ設定
			//--------------------------------------------------------------------------
			else if (action.equals("SetupEditor")) {
				parentframe.SetupEditor();
			}
			//--------------------------------------------------------------------------
			// キーワード設定
			//--------------------------------------------------------------------------
			else if (action.equals("SetupKeyword")) {
				parentframe.SetupKeyword();
			}
			//--------------------------------------------------------------------------
			// IDEオプション
			//--------------------------------------------------------------------------
			else if (action.equals("SetupKeymap")) {
				parentframe.IdeOption();
			}
			//--------------------------------------------------------------------------
			// 外部ツール構成ダイアログの表示
			//--------------------------------------------------------------------------
			else if (action.equals("SetOutsideTool")) {
				parentframe.SetOutsideTool();
			}
			//--------------------------------------------------------------------------
			// 外部ツールの実行
			//--------------------------------------------------------------------------
			else if (action.startsWith("OutsideTool_")) {
				parentframe.ExecOutsideTool(action);
			}
			//--------------------------------------------------------------------------
			// 新規のDashファイル作成
			//--------------------------------------------------------------------------
			else if (action.equals("NewFile")) {
				if (project == null) {
					Object[] options = { "OK" };
					JOptionPane.showOptionDialog(this, parentframe
							.getBilingualMsg("0068"), parentframe
							.getBilingualMsg("0129"),
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE, null, options,
							options[0]);

					return;
				}
				NewChoice newChoice = new NewChoice(parentframe, project);
				//newChoice.show();
				newChoice.setVisible(true);
				//NewFile newfile = new NewFile(this,project);
				//newfile.show();
			}
			//--------------------------------------------------------------------------
			// 新規プロジェクト
			//--------------------------------------------------------------------------
			else if (action.equals("NewProject")) {
				NewProject NewPrjWin = new NewProject(parentframe, null);
				//NewPrjWin.show();
				NewPrjWin.setVisible(true);
			}
			//--------------------------------------------------------------------------
			// プロジェクトの削除
			//--------------------------------------------------------------------------
			else if (action.equals("DeleteProject")) {
				parentframe.deleteProject();
			}

		} catch (Exception e2) {

		}

	}

	// 未使用
	void btn1_actionPerformed(ActionEvent e) {
		canvasR.removeAgentAll();
		repPanel.removeAgentAll();
		canvasW[0].removeAgentAll();
		wpPanel[0].removeAgentAll();

		canvasW[0].repaint();
		canvasR.repaint();
		//inspectorDesktop.getAllFrames();
		inspectorDesktop.removeAll();
		inspectorDesktop.repaint();

		//repPanel.loadProjectAgents("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
		//repPanel.loadProjectAgents(ProjectFileName);
	}

	/****************************************************************************
	 * プロジェクトファイル名設定
	 * @param ProjectFileName 現在、開かれているプロジェクトファイル名。<br>
	 * IdeaMainFrameより呼ばれる
	 * @return なし
	 ****************************************************************************/
	public void setProjectFileName(String ProjectFileName) {
		this.ProjectFileName = ProjectFileName;
	}

	/****************************************************************************
	 * プロジェクト設定
	 * @param pro 現在、開かれているプロジェクトファイル。<br>
	 * IdeaMainFrameより呼ばれる
	 * @return なし
	 ****************************************************************************/
	public void setProject(Project project) {
		this.project = project;
		//プロジェクト削除メニュー更新
		if (project != null) {
			deletePrjMenuItem.setEnabled(true);
		} else {
			deletePrjMenuItem.setEnabled(false);
		}

		vecRepLoadAgent.clear();

		for (int i = 0; i < wp_max_cnt; i++) {
			vecWpLoadAgent[i].clear();
			vecWpLoadAgent2[i].clear();
		}

		LoadDir = "";
		for (int j = 0; j < project.getFolderPath().size(); j++) {
			if (LoadDir.equals("")) {
				LoadDir = (String) project.getFolderPath().elementAt(j);
			} else {
				LoadDir += ";" + (String) project.getFolderPath().elementAt(j);
			}
		}

		readLoadAgentFileInfo();
		System.setProperty("LoadDir", LoadDir);
		//vecWpReadCount.clear();
	}

	/****************************************************************************
	 * リポジトリへのAg読込処理
	 * @param mode "ALL_AGENT":全Ag読込 "SELECT_AGENT":指定したAg読込
	 * @return なし
	 ****************************************************************************/
	private void repExec(String mode) {

		Vector vecSelectFiles = null;
		Vector vecReadCount = null;
		Object[] options = { "OK" };

		if (project == null) {
			// プロジェクトが開かれていない時
			JOptionPane.showOptionDialog(this, parentframe
					.getBilingualMsg("0073"), parentframe
					.getBilingualMsg("0129"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);

			return;
		}

		if (project.getFileCount() == 0) {
			// プロジェクトにファイルが存在しない時
			JOptionPane.showOptionDialog(this, parentframe
					.getBilingualMsg("0074"), parentframe
					.getBilingualMsg("0129"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);

			return;
		}
		if (mode.equals("SELECT_AGENT")) {
			// 指定されたAgの読込
			SelectAgentFileDialog dlg = new SelectAgentFileDialog(parentframe,
					parentframe.getBilingualMsg("0052"), true, project,
					vecRepLoadAgent, null);
			//dlg.show();
			dlg.setVisible(true);
			if (dlg.getResult() == 0) {
				return;
				//System.out.println("キャンセルでっす");
			}

			vecSelectFiles = dlg.getSelectFiles();
			vecReadCount = dlg.getReadCount();
			//System.out.println("選択数：" + vecSelectFiles.size());
		} else {
			vecSelectFiles = (Vector) project.getFileNames().clone();
			vecSelectFiles = (Vector) project.getFileNamesWithPath().clone();
		}

		//リポジトリをクリア
		clear_Rep();

		if (!mode.equals("SELECT_AGENT")) {
			repPanel.loadProjectAgents(project.getProjectPath(),
					vecSelectFiles, LoadDir);
		} else {
			Vector vecFilenameWithPath = (Vector) project
					.getFileNamesWithPath();
			Vector vecSelectFileWk = new Vector();
			for (int i = 0; i < vecSelectFiles.size(); i++) {
				String SelectFileName = (String) vecSelectFiles.elementAt(i);
				boolean flag = false;
				for (int j = 0; j < vecFilenameWithPath.size(); j++) {
					String FilenameWithPath = (String) vecFilenameWithPath
							.elementAt(j);
					if (FilenameWithPath.endsWith(File.separator
							+ SelectFileName)) {
						vecSelectFileWk.addElement(FilenameWithPath);
					}
				}

			}

			repPanel.loadProjectAgents(project.getProjectPath(),
					vecSelectFileWk, LoadDir);
			//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecSelectFiles, null, DefaultDir );
		}

		//読込実行
		//repPanel.loadProjectAgents(project.getProjectPath(),vecSelectFiles, LoadDir );
		if (mode.equals("SELECT_AGENT")) {
			// 指定されたAgの読込の時のみ、読み込んだファイルを覚えておく
			// これが、リロード処理で使われる
			vecRepLoadAgent = vecSelectFiles;

			Vector vecOrgData = new Vector();
			try {
				FileReader f_in;
				BufferedReader b_in;
				String sLine;
				boolean FindFlag = false;

				if (new File(project.getProjectPath() + "loadagentinfo")
						.exists()) {
					b_in = new BufferedReader(new InputStreamReader(
							new FileInputStream(project.getProjectPath()
									+ "loadagentinfo"), "JISAutoDetect"));

					String inf_kind = "";
					String javafilename = "";
					while ((sLine = b_in.readLine()) != null) {
						if (sLine.equals("[rep]")) {
							inf_kind = sLine; //リポジトリ
							vecOrgData.addElement(sLine);
						} else if (sLine.startsWith("[wp")) {
							inf_kind = sLine; //関連Dashファイル情報
							vecOrgData.addElement(sLine);
						} else {
							if (inf_kind.equals("[rep]")) {
								if (!FindFlag) {
									FindFlag = true;
									for (int i = 0; i < vecRepLoadAgent.size(); i++) {
										//if (vecOrgData.indexOf((String)vecRepLoadAgent.elementAt(i))==-1){
										vecOrgData
												.addElement((String) vecRepLoadAgent
														.elementAt(i));
										//}
									}
								}
							} else {
								vecOrgData.addElement(sLine);
							}
						}
					}
					b_in.close();
				}

				if (!FindFlag) {
					vecOrgData.addElement("[rep]");
					for (int i = 0; i < vecRepLoadAgent.size(); i++) {
						vecOrgData.addElement((String) vecRepLoadAgent
								.elementAt(i));
					}
				}

				File fp = new File(project.getProjectPath() + "loadagentinfo");
				FileOutputStream fos = new FileOutputStream(fp);
				PrintWriter pw = new PrintWriter(fos);
				for (int i = 0; i < vecOrgData.size(); i++) {
					pw.println((String) vecOrgData.elementAt(i));
				}
				pw.close();

			} catch (Exception ex) {
			}

		}
	}

	/****************************************************************************
	 * ワークプレースへのAg読込処理
	 * @param mode "ALL_AGENT":全Ag読込 "SELECT_AGENT":指定したAg読込
	 * @return なし
	 ****************************************************************************/
	private void wpExec(String mode) {
		int wpIndex = wpTab.getSelectedIndex();
		Vector vecSelectFiles = null;
		Vector vecReadCount = null;
		Object[] options = { "OK" };

		if (project == null) {
			// プロジェクトが開かれていない時
			JOptionPane.showOptionDialog(this, parentframe
					.getBilingualMsg("0073"), parentframe
					.getBilingualMsg("0129"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			return;
		}

		if (project.getFileCount() == 0) {
			// プロジェクトにファイルが存在しない時
			JOptionPane.showOptionDialog(this, parentframe
					.getBilingualMsg("0074"), parentframe
					.getBilingualMsg("0129"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			return;
		}
		if (mode.equals("SELECT_AGENT")) {
			// 指定されたAgの読込
			//SelectAgentFileDialog dlg = new SelectAgentFileDialog(parentframe,title,true, project,vecWpLoadAgent,vecWpReadCount);
			//SelectAgentFileDialog dlg = new SelectAgentFileDialog(parentframe,parentframe.getBilingualMsg("0054"),true, project,vecWpLoadAgent[wpIndex],null);
			SelectAgentFileDialog dlg = new SelectAgentFileDialog(parentframe,
					parentframe.getBilingualMsg("0054"), true, project,
					vecWpLoadAgent2[wpIndex], null);
			//dlg.show();
			dlg.setVisible(true);
			if (dlg.getResult() == 0) {
				return;
				//System.out.println("キャンセルでっす");
			}

			vecSelectFiles = dlg.getSelectFiles();
			for (int i = 0; i < vecSelectFiles.size(); i++) {
				String s = (String) vecSelectFiles.elementAt(i);
				String ss = s;
			}
			//vecReadCount = dlg.getReadCount();
			//System.out.println("選択数：" + vecSelectFiles.size());
		} else {
			//vecSelectFiles = (Vector)project.getFileNames().clone();
			vecSelectFiles = (Vector) project.getFileNamesWithPath().clone();
			//for (int i=0;i<vecSelectFiles.size(); i++ ) {
			//  vecReadCount.addElement("1");
			//}
			vecWpLoadAgent[wpIndex].clear();
			vecWpLoadAgent2[wpIndex].clear();
		}

		// ワークプレースへのAg読込実行
		if (!mode.equals("SELECT_AGENT")) {
			//指定されたAgの読込では、ワークプレースは初期化しない
			clear_Wp();
			wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),
					vecSelectFiles, null, LoadDir);
		} else {
			Vector vecFilenameWithPath = (Vector) project
					.getFileNamesWithPath();
			Vector vecSelectFileWk = new Vector();
			for (int i = 0; i < vecSelectFiles.size(); i++) {
				String SelectFileName = (String) vecSelectFiles.elementAt(i);
				boolean flag = false;
				for (int j = 0; j < vecFilenameWithPath.size(); j++) {
					String FilenameWithPath = (String) vecFilenameWithPath
							.elementAt(j);
					if (FilenameWithPath.endsWith(File.separator
							+ SelectFileName)) {
						vecSelectFileWk.addElement(FilenameWithPath);
					}
				}

			}

			wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),
					vecSelectFileWk, null, LoadDir);
			//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecSelectFiles, null, DefaultDir );
		}

		if (mode.equals("SELECT_AGENT")) {
			//指定されたAgの読込時のみ、読み込んだファイルを覚えておく
			//これが、リロード処理で使われる
			vecWpLoadAgent2[wpIndex].clear();
			for (int i = 0; i < vecSelectFiles.size(); i++) {
				vecWpLoadAgent[wpIndex].addElement(vecSelectFiles.elementAt(i));
				vecWpLoadAgent2[wpIndex]
						.addElement(vecSelectFiles.elementAt(i));
			}

			Vector vecOrgData = new Vector();
			try {
				FileReader f_in;
				BufferedReader b_in;
				String sLine;

				boolean FindFlag = false;
				if (new File(project.getProjectPath() + "loadagentinfo")
						.exists()) {
					b_in = new BufferedReader(new InputStreamReader(
							new FileInputStream(project.getProjectPath()
									+ "loadagentinfo"), "JISAutoDetect"));

					String inf_kind = "";
					String javafilename = "";
					Vector vecEndWpName = new Vector();
					while ((sLine = b_in.readLine()) != null) {
						if (sLine.equals("[rep]")) {
							inf_kind = sLine; //リポジトリ
							vecOrgData.addElement(sLine);
						} else if (sLine.startsWith("[wp")) {
							inf_kind = sLine; //関連Dashファイル情報
							vecOrgData.addElement(sLine);
						} else {
							if (inf_kind
									.equals("[wp"
											+ new Integer(wpIndex + 1)
													.toString() + "]")) {
								if (vecEndWpName.indexOf("[wp"
										+ new Integer(wpIndex + 1).toString()
										+ "]") == -1) {
									FindFlag = true;
									for (int i = 0; i < vecWpLoadAgent2[wpIndex]
											.size(); i++) {
										vecOrgData
												.addElement((String) vecWpLoadAgent2[wpIndex]
														.elementAt(i));
									}
									vecEndWpName.addElement("[wp"
											+ new Integer(wpIndex + 1)
													.toString() + "]");
								}
							} else {
								vecOrgData.addElement(sLine);
							}
						}
					}
					b_in.close();
				}
				if (!FindFlag) {
					vecOrgData.addElement("[wp"
							+ new Integer(wpIndex + 1).toString() + "]");
					for (int i = 0; i < vecWpLoadAgent2[wpIndex].size(); i++) {
						vecOrgData.addElement((String) vecWpLoadAgent2[wpIndex]
								.elementAt(i));
					}
				}

				File fp = new File(project.getProjectPath() + "loadagentinfo");
				FileOutputStream fos = new FileOutputStream(fp);
				PrintWriter pw = new PrintWriter(fos);
				for (int i = 0; i < vecOrgData.size(); i++) {
					pw.println((String) vecOrgData.elementAt(i));
				}
				pw.close();

			} catch (Exception ex) {
			}

		}
		//vecWpLoadAgent = vecSelectFiles;
		//vecWpReadCount = vecReadCount;
	}

	/****************************************************************************
	 * リポジトリ、ワークプレースへのAgを消去
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	public void clear_Wp_Rep() {
		JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
		if (iframes.length == 0)
			return;

		// リポジトリから消す
		canvasR.removeAgentAll();
		repPanel.removeAgentAll();
		canvasR.repaint();

		// ワークプレースから消す
		for (int i = 0; i < wp_cnt; i++) {
			canvasW[i].removeAgentAll();
			wpPanel[i].removeAgentAll();
			canvasW[i].repaint();
		}

		// インスペクタを消す
		//inspectorDesktop.removeAll();
		//inspectorDesktop.repaint();

		// 現在アクティブなワークプレースから開かれた、インスペクタを消す
		//JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
		if (iframes.length == 0)
			return;

		for (int i = 0; i < iframes.length; i++) {

			if (iframes[i].getToolTipText().equals("Workplace")
					|| iframes[i].getToolTipText().equals("Repository")) {
				inspectorDesktop.remove(iframes[i]);
			}
		}

		inspectorDesktop.repaint();
		System.gc();

	}

	/****************************************************************************
	 * ワークプレースへのAgを消去
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	public void clear_Wp() {
		// ワークプレースは複数あるため、現在アクティブなワークプレースを調べる
		int wpIndex = wpTab.getSelectedIndex();

		// 現在アクティブなワークプレースのAgを消す
		canvasW[wpIndex].removeAgentAll();
		wpPanel[wpIndex].removeAgentAll();
		canvasW[wpIndex].repaint();

		// 現在アクティブなワークプレースから開かれた、インスペクタを消す
		JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
		if (iframes.length == 0)
			return;

		for (int i = 0; i < iframes.length; i++) {

			if (iframes[i].getToolTipText().equals("Workplace")) {
				inspectorDesktop.remove(iframes[i]);
			}
		}

		inspectorDesktop.repaint();
		System.gc();
	}

	/****************************************************************************
	 * リポジトリへのAgを消去
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	public void clear_Rep() {
		canvasR.removeAgentAll();
		repPanel.removeAgentAll();
		canvasR.repaint();

		// リポジトリから開かれたインスペクタを消す
		JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
		if (iframes.length == 0)
			return;

		for (int i = 0; i < iframes.length; i++) {

			if (iframes[i].getToolTipText().equals("Repository")) {
				inspectorDesktop.remove(iframes[i]);
			}
		}

		inspectorDesktop.repaint();
		System.gc();
	}

	/****************************************************************************
	 * メニューアイテムの作成
	 * @param label　メニュー文字列
	 * @param command　アクションコマンド
	 * @param icon　アイコン
	 * @return メニューアイテム
	 ****************************************************************************/
	private JMenuItem menuItem(String label, String command, Icon icon) {
		JMenuItem item = new JMenuItem(label, icon);
		item.setActionCommand(command);
		item.addActionListener(this);
		return item;
	}

	/****************************************************************************
	 * DVM取得
	 * @param なし
	 * @return DVM
	 ****************************************************************************/
	public DVM getDVM() {
		return wpPanel[0].wp.getNewIf().getDVM();
	}

	/****************************************************************************
	 * リポジトリのDVM取得
	 * @param なし
	 * @return リポジトリのDVM
	 ****************************************************************************/
	public DVM getRepDVM() {
		return repPanel.rep.getNewIf().getDVM();
	}

	/****************************************************************************
	 * ワークプレースのDVM取得（未使用）
	 * @param なし
	 * @return ワークプレースのDVM
	 ****************************************************************************/
	/*
	 public DVM getWpDVM() {
	 return wpPanel[0].wp.getNewIf().getDVM();
	 }
	 */

	/****************************************************************************
	 * ワークプレースのDVM取得（未使用）
	 * @param index ワークプレースの番号
	 * @return ワークプレースのDVM
	 ****************************************************************************/
	public DVM getWpDVM(int index) {
		return wpPanel[index].wp.getNewIf().getDVM();
	}

	/****************************************************************************
	 * プロジェクト履歴のメニュー作成
	 * @param histlist 過去に開かれたプロジェクトファイル名（パスつき）
	 * @return なし
	 ****************************************************************************/
	public void setOpenHist(Vector histlist) {

		if (histlist.size() <= 0) {
			openagainprjmenu.removeAll();
			openagainprjPopupMenu.removeAll();
			openagainprjmenu.setEnabled(false);
			openagainprjBtn.setEnabled(false);
		} else {
			openagainprjmenu.removeAll();
			openagainprjmenu.setEnabled(true);
			openagainprjBtn.setEnabled(true);
			openagainprjPopupMenu.removeAll();
			for (int i = 0; i < histlist.size(); i++) {
				String addname = (String) histlist.elementAt(i);

				//サブメニュー
				openagainprjmenu.add(menuItem(addname, "againPrjct", null));

				//ポップアップメニュー
				openagainprjPopupMenu
						.add(menuItem(addname, "againPrjct", null));

			}

			subMenuItem = new JMenuItem(parentframe.getBilingualMsg("0036"));
			subMenuItem.setActionCommand("deletehist");
			subMenuItem.addActionListener(this);

			popMenuItem = new JMenuItem(parentframe.getBilingualMsg("0036"));
			popMenuItem.setActionCommand("deletehist");
			popMenuItem.addActionListener(this);

			//サブメニュー
			//openagainprjmenu.add(menuItem(parentframe.getBilingualMsg("0036"), "deletehist", null));
			openagainprjmenu.add(subMenuItem);

			//ポップアップメニュー
			//openagainprjPopupMenu.add(menuItem(parentframe.getBilingualMsg("0036"), "deletehist", null));
			openagainprjPopupMenu.add(popMenuItem);

		}

	}

	void openagainprjBtn_stateChanged(ChangeEvent e) {
		try {
			openagainprjPopupMenu.show(toolbar, openagainprjBtn.getX(),
					openagainprjBtn.getY() + openagainprjBtn.getHeight());
		} catch (Exception ee) {
		}
	}

	void openagainprjPopupMenu_popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		openagainprjBtn.setSelected(false);
	}

	/****************************************************************************
	 * 他のフェーズでシミュレータの設定が変更された後、動作シミュレートフェースが開かれた時に<br>
	 * シミュレータの設定のチェックを行う。<br>
	 * IdeaMainFrameから呼ばれる
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	public void SettingCheck() {
		//System.out.println("SettingCheck");
		getRepDVM().finalizeDVM();
		getWpDVM(0).finalizeDVM();
		getWpDVM(1).finalizeDVM();
		getWpDVM(2).finalizeDVM();
		getWpDVM(3).finalizeDVM();
		getWpDVM(4).finalizeDVM();

		getRepDVM().RmiModule_ReCreate();
		getWpDVM(0).RmiModule_ReCreate();
		getWpDVM(1).RmiModule_ReCreate();
		getWpDVM(2).RmiModule_ReCreate();
		getWpDVM(3).RmiModule_ReCreate();
		getWpDVM(4).RmiModule_ReCreate();

		//getDVM().RmiModule_ReCreate();
		String wpCntStr = System.getProperty("dash.wp.cnt");
		wp_cnt = new Integer(wpCntStr).intValue();
		int cnt = wpTab.getComponentCount();

		if (wp_cnt > cnt) {
			for (int i = cnt; i < wp_cnt; i++) {
				JSplitPane splitPane2 = new JSplitPane(
						JSplitPane.VERTICAL_SPLIT, true, canvasW_BasePanel[i],
						wpPanel[i]);
				splitPane2.setDividerLocation(360);
				splitPane2.setDividerSize(5);
				wpTab.add("WP" + new Integer(i + 1).toString(), splitPane2);
				wpPanel[i].wp.getNewIf().getDVM().setWpTab(wpTab);
			}
		} else if (wp_cnt < cnt) {
			for (int i = wp_cnt; i < cnt; i++) {
				int wpIndex = i;
				canvasW[wpIndex].removeAgentAll();
				wpPanel[wpIndex].removeAgentAll();
				wpPanel[wpIndex].clearLog();
				canvasW[wpIndex].repaint();
				vecWpLoadAgent[wpIndex].clear();
				vecWpLoadAgent2[wpIndex].clear();

				JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
				for (int j = 0; j < iframes.length; j++) {

					if (iframes[j].getToolTipText().equals("Workplace")) {
						inspectorDesktop.remove(iframes[j]);
					}
				}

				inspectorDesktop.repaint();

				wpTab.remove(wp_cnt);
			}
		}
		wpTab.repaint();
		for (int i = 0; i < wp_cnt; i++) {
			if (!WpInitialized[i]) {
				canvasW[i].initialize();
				WpInitialized[i] = true;
			}
		}
		//for (int i=0; i<wp_cnt; i++ ) {
		//  wpTab.getComponentCount()
		//}

	}

	/****************************************************************************
	 * 外部ツールメニューの再作成<br>
	 * IdeaMainFrameから呼ばれる
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	public void ReMakeOutsideToolMenu() {

		// 外部ツール用メニューを作り直す
		outsideTool.removeAll();
		//outsideTool2.removeAll();

		JMenuItem toolMenu3 = menuItem(parentframe.getBilingualMsg("0025")
				+ "(C)...", "SetOutsideTool", null);
		toolMenu3.setMnemonic('C');
		outsideTool.add(toolMenu3);

		JMenuItem toolMenu3_2 = menuItem(parentframe.getBilingualMsg("0025")
				+ "(C)...", "SetOutsideTool", null);
		toolMenu3_2.setMnemonic('C');
		//outsideTool2.add(toolMenu3_2);

		if (parentframe.vecOutsideToolInfo.size() > 0) {
			outsideTool.addSeparator();
			//outsideTool2.addSeparator();
		}

		for (int i = 0; i < parentframe.vecOutsideToolInfo.size(); i++) {
			String wk = (String) parentframe.vecOutsideToolInfo.elementAt(i);

			StringTokenizer st = new StringTokenizer(wk, ",");
			while (st.hasMoreTokens()) {
				String data = st.nextToken();

				JMenuItem toolMenu4 = menuItem(new Integer(i + 1).toString()
						+ ":" + data, "OutsideTool_"
						+ new Integer(i).toString(), null);
				outsideTool.add(toolMenu4);

				JMenuItem toolMenu4_2 = menuItem(new Integer(i + 1).toString()
						+ ":" + data, "OutsideTool_"
						+ new Integer(i).toString(), null);
				//outsideTool2.add(toolMenu4_2);
				break;
			}
		}
	}

	/****************************************************************************
	 * フォームに表示されているラベル等の文字列を変更<br>
	 * IDEオプションで使用言語が変更された場合に、IdeaMainFrameより呼ばれる
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	public void changeFormLabel() {

		for (int i = 0; i < wp_max_cnt; i++) {
			titleBdr1[i].setTitle(parentframe.getBilingualMsg("0050"));
		}

		titleBdr2.setTitle(parentframe.getBilingualMsg("0047"));

		btn1.setToolTipText(parentframe.getBilingualMsg("0051"));
		btn2.setToolTipText(parentframe.getBilingualMsg("0052"));
		btn3.setToolTipText(parentframe.getBilingualMsg("0053"));
		btn4.setToolTipText(parentframe.getBilingualMsg("0054"));
		btn5.setToolTipText(parentframe.getBilingualMsg("0055"));
		btn6.setToolTipText(parentframe.getBilingualMsg("0056"));
		btn7.setToolTipText(parentframe.getBilingualMsg("0057"));
		btn8.setToolTipText(parentframe.getBilingualMsg("0058"));
		btn9.setToolTipText(parentframe.getBilingualMsg("0059"));
		btn10.setToolTipText(parentframe.getBilingualMsg("0060"));

	}

	/****************************************************************************
	 * メニュー文字列を変更<br>
	 * IDEオプションで使用言語が変更された場合に、IdeaMainFrameより呼ばれる
	 * @param なし
	 * @return なし
	 ****************************************************************************/
	public void changeMenuText() {

		filemenu.setText(parentframe.getBilingualMsg("0001") + "(F)");

		newFileMenuItem.setText(parentframe.getBilingualMsg("0002") + "(N)...");
		newProjectMenuItem.setText(parentframe.getBilingualMsg("0003")
				+ "(P)...");

		filemenu1.setText(parentframe.getBilingualMsg("0004") + "(O)...");
		filemenu2.setText(parentframe.getBilingualMsg("0008") + "(X)");

		openagainprjmenu.setText(parentframe.getBilingualMsg("0005") + "(R)");
		deletePrjMenuItem.setText(parentframe.getBilingualMsg("0116"));

		//--------------------------------------------------------------------------
		// ツールメニュー
		//--------------------------------------------------------------------------
		toolMenu_DevScreen.setText(parentframe.getBilingualMsg("0021") + "(T)");

		// メニューアイテム作成
		toolMenu1.setText(parentframe.getBilingualMsg("0022") + "(E)...");
		toolMenu2.setText(parentframe.getBilingualMsg("0023") + "(K)...");
		toolMenu4.setText(parentframe.getBilingualMsg("0117") + "...");
		toolMenu5.setText(parentframe.getBilingualMsg("0118") + "...");

		outsideTool.setText(parentframe.getBilingualMsg("0024") + "(O)");
		//outsideTool2.setText(parentframe.getBilingualMsg("0024") + "(O)");

		toolMenu3.setText(parentframe.getBilingualMsg("0025") + "(C)...");

		openprjBtn.setToolTipText(parentframe.getBilingualMsg("0004"));
		openagainprjBtn.setToolTipText(parentframe.getBilingualMsg("0005"));

		openagainprjmenu.setText(parentframe.getBilingualMsg("0036"));

		//add uchiya
		//--------------------------------------------------------------------------
		// メッセージメニュー
		//--------------------------------------------------------------------------
		messageMenu_DevScreen.setText(parentframe.getBilingualMsg("0222")
				+ "(M)");
		send_acl_menu.setText(parentframe.getBilingualMsg("0223"));
		set_acl_menu.setText(parentframe.getBilingualMsg("0224"));

		//--------------------------------------------------------------------------
		//履歴
		//--------------------------------------------------------------------------
		if (subMenuItem != null) {
			subMenuItem.setText(parentframe.getBilingualMsg("0036"));
		}
		if (popMenuItem != null) {
			popMenuItem.setText(parentframe.getBilingualMsg("0036"));
		}

	}

	private void readLoadAgentFileInfo() {
		try {
			FileReader f_in;
			BufferedReader b_in;
			String sLine;
			boolean FindFlag = false;
			Vector vecRepAgent = new Vector();
			Vector[] vecWpAgent = new Vector[5];
			for (int i = 0; i < 5; i++) {
				vecWpAgent[i] = new Vector();
			}

			if (new File(project.getProjectPath() + "loadagentinfo").exists()) {
				b_in = new BufferedReader(new InputStreamReader(
						new FileInputStream(project.getProjectPath()
								+ "loadagentinfo"), "JISAutoDetect"));

				String inf_kind = "";
				String javafilename = "";
				while ((sLine = b_in.readLine()) != null) {
					if (sLine.equals("[rep]")) {
						inf_kind = sLine; //リポジトリ
					} else if (sLine.startsWith("[wp")) {
						inf_kind = sLine;
					} else {
						if (inf_kind.equals("[rep]")) {
							vecRepAgent.addElement(sLine);
						} else if (inf_kind.startsWith("[wp")) {
							int index = new Integer(inf_kind.substring(3,
									inf_kind.lastIndexOf("]"))).intValue() - 1;
							vecWpAgent[index].addElement(sLine);
						}
					}
				}
				b_in.close();
			}
			setRepLoadAgent(vecRepAgent);
			for (int i = 0; i < 5; i++) {
				setWpLoadAgent(vecWpAgent[i], i);
			}
		} catch (Exception ex) {
			String err = ex.getMessage();
			String err1 = err;
		}

	}

	private void setRepLoadAgent(Vector vecRepLoadAgent) {

		for (int i = 0; i < vecRepLoadAgent.size(); i++) {
			this.vecRepLoadAgent.addElement((String) vecRepLoadAgent
					.elementAt(i));
		}
	}

	private void setWpLoadAgent(Vector vecWpLoadAgent, int index) {

		for (int i = 0; i < vecWpLoadAgent.size(); i++) {
			this.vecWpLoadAgent2[index].addElement((String) vecWpLoadAgent
					.elementAt(i));
		}
	}

	/** add uchiya
	 * MessageメニューのACLテンプレートを選択したときに呼び出されるActionListener
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
			Hashtable hash = (Hashtable) messageHash.get(command);

			int i = ((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).logTabbedPane
					.indexOfTab("acl-editor");
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).logTabbedPane
					.setSelectedIndex(i);

			String performative = (String) hash.get("performative");
			String to = (String) hash.get("to");
			String arrival = (String) hash.get("arrival");
			String content = (String) hash.get("content");

			if (arrival != null)
				to = to + "@" + arrival;
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setPerfField(performative);
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setToField(to);
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setContArea(content);
		}
	}

	/** add mabune
	 * MessageメニューのACLの履歴を使用を選択したときに呼び出されるActionListener
	 */
	class HistoryAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String[] acls = { "", "", "" };
			String perfomative = "";
			String to = "";
			String content = "";

			AllAclInputHistList dlg = new AllAclInputHistList(parentframe,
					"History of ACL");
			dlg.setVisible(true);
			acls = dlg.getSelStr();
			perfomative = acls[0];
			to = acls[1];
			content = acls[2];

			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setPerfField(perfomative);
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setToField(to);
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setContArea(content);
		}
	}

	/** add uchiya
	 * Messageメニューの「Analyze message log」を選択したときに呼び出されるActionListener
	 */
	class AnalyzeAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String[] str = new String[0];
			Main.main(str);
		}
	}
}