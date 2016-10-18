package dash;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.table.*;
import javax.swing.text.*;
import ps.*;

/**
 * <p>SearchPanel: </p>
 * <p>検索/取込画面: </p>
 */

public class SearchPanel extends JPanel implements ActionListener, MouseListener, ListSelectionListener{

  public JMenuBar menubar;
  public JToolBar toolbar;
  private BorderLayout borderLayout1 = new BorderLayout();
  private JRadioButton jRadioButton1 = null;
  private JRadioButton jRadioButton2 = null;
  private ButtonGroup buttonGroup1 = new ButtonGroup();
  private JTextField NameServerField = new JTextField();
  private JTextPane ResultTextPanel = null;
  private String jgIntake = "";
  private Hashtable nmFile_tb = new Hashtable();
  private static JTextField DirKeyword  = new JTextField();
  private JTextField SrchKeyword = new JTextField();
  private Border border1;
  private static JMenu openprjMenu = null;
  private static JMenuItem openprjMenuitem = null;
  private static JMenuItem deleteMenuitem = null;
  private static String DefaultDir = "";
  private JTable searchList = null;
  private String[] hstr = {"Ag名"};
  private String[][] rowdata = {
    {""}
  };
  private JPopupMenu popup = new JPopupMenu();
  private JToggleButton jToggleButton1 = new JToggleButton();
  private JTabbedPane jTabbedPane = null;
  private static Project project = null;
  private JMenu toolMenu_DevScreen = null;
  private static JMenu outsideTool = null;
  private static JMenu outsideTool2 = null;
  private JMenu menu1 = null;
  private JMenuItem item1 = null;
  private JMenuItem item2 = null;
  private JMenuItem item3 = null;
  private JMenuItem item4 = null;
  private JMenuItem toolMenu1 = null;
  private JMenuItem toolMenu2 = null;
  private JMenuItem toolMenu3 = null;
  private JMenuItem toolMenu4 = null;
  private JMenuItem toolMenu5 = null;
  private JButton toolbtn1  = null;
  private JButton toolbtn2  = null;
  private JButton toolbtn4  = null;
	private JLabel prjLabel = null;
	private JLabel dirLabel = null;
	private JButton nsBtn = null;
	private TitledBorder titleBdr1 = null;
	private TitledBorder titleBdr2 = null;
	private TitledBorder titleBdr3 = null;
	private JLabel ArrowLabel2 = null;
	private JButton intkbtn = null;
	private JMenuItem subMenuItem = null;
	private JMenuItem popMenuItem = null;
  private IdeaMainFrame prtframe = null;
  private DVM dvm = null;
  private JTextArea previewTextArea = null;
  SimpleAttributeSet attr1,attr2;
  Document doc;
  DefaultTableModel model;
  JFileChooser fdlg = null;
	NewProject NewPrjWin = null;
	NewFile NewFileWin = null;
	NewChoice NewChoiceWin = null;
  OutsideTool OutTool = null;

  private JRadioButton jRadioButton_filekind_dash = null;
  private JRadioButton jRadioButton_filekind_bp = null;
  private JRadioButton jRadioButton_bp_intakemode1 = null;
  private JRadioButton jRadioButton_bp_intakemode2 = null;
  private ButtonGroup buttonGroup2 = new ButtonGroup();
  private ButtonGroup buttonGroup3 = new ButtonGroup();

  public JSplitPane splitPane1 = null;

  public JTabbedPane search_result_tabbedpane = null; // added by takagaki
  public AgentRelationPanel agent_relation_panel = null; // added by takagaki

  public SearchPanel( IdeaMainFrame frame ) {
    try {

      this.prtframe = frame;
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    this.setLayout(borderLayout1);

    // メニューバー
    menubar = new JMenuBar();
    menu1 = new JMenu(prtframe.getBilingualMsg("0001") + "(F)", true);
    menu1.setMnemonic('F');

    menubar.add(menu1);
    ImageIcon Icon1 = getImageIcon("resources/newprj.gif");
    ImageIcon Icon2 = getImageIcon("resources/openprj.gif");
    ImageIcon Icon3 = getImageIcon("resources/openagainprj.gif");
    ImageIcon Icon3_2 = getImageIcon("resources/openagainprj2.gif");
    ImageIcon Icon4 = getImageIcon("resources/newfile.gif");

    item4 = new JMenuItem(prtframe.getBilingualMsg("0002") + "(N)...", Icon4);
    item4.setHorizontalTextPosition(SwingConstants.RIGHT);
    item4.setMnemonic('N');
    menu1.add(item4);
    item4.addActionListener(this);
    item4.setActionCommand("newFile");

    prtframe.setMenuItemAccelerator("new-file",item4);

    item1 = new JMenuItem(prtframe.getBilingualMsg("0003") + "(P)...", Icon1);
    item1.setHorizontalTextPosition(SwingConstants.RIGHT);
    item1.setMnemonic('P');
    menu1.add(item1);
    item1.addActionListener(this);
    item1.setActionCommand("newPrjct");

    item2 = new JMenuItem(prtframe.getBilingualMsg("0004") + "(O)...", Icon2);
    item2.setHorizontalTextPosition(SwingConstants.RIGHT);
    item2.setMnemonic('O');
    menu1.add(item2);
    item2.addActionListener(this);
    item2.setActionCommand("openPrjct");

    prtframe.setMenuItemAccelerator("open-project",item2);

    openprjMenu = new JMenu(prtframe.getBilingualMsg("0005") + "(R)");
    openprjMenu.setMnemonic('R');
    openprjMenu.setIcon(Icon3);
    openprjMenu.setEnabled(false);
    menu1.add(openprjMenu);

		deleteMenuitem = new JMenuItem(prtframe.getBilingualMsg("0116"));
    deleteMenuitem.setEnabled(false);
    menu1.add(deleteMenuitem);
    deleteMenuitem.addActionListener(this);
    deleteMenuitem.setActionCommand("delPrjct");

    menu1.addSeparator();
    item3 = new JMenuItem(prtframe.getBilingualMsg("0008") + "(X)");
    item3.setMnemonic('X');
    menu1.add(item3);
    item3.addActionListener(this);
    item3.setActionCommand("exitwindow");

    // ツールメニュー
    toolMenu_DevScreen = new JMenu (prtframe.getBilingualMsg("0021") + "(T)");
    toolMenu_DevScreen.setMnemonic('T');

    // メニューアイテム作成
    toolMenu1 = menuItem(prtframe.getBilingualMsg("0022") + "(E)...", "SetupEditor", null );
    toolMenu2 = menuItem(prtframe.getBilingualMsg("0023") + "(K)...", "SetupKeyword", null );
    toolMenu4 = menuItem(prtframe.getBilingualMsg("0117") + "...", "SetupKeymap", null );
    toolMenu5 = menuItem(prtframe.getBilingualMsg("0118") + "...", "SimulatorOption", null );

    outsideTool = new JMenu(prtframe.getBilingualMsg("0024") + "(O)");
    outsideTool.setMnemonic('O');
    outsideTool2 = new JMenu(prtframe.getBilingualMsg("0024") + "(O)");
    outsideTool2.setMnemonic('O');
    outsideTool2.setIcon(getImageIcon("resources/permeation.gif"));

    // ニーモニック設定
    toolMenu1.setMnemonic('E');
    toolMenu2.setMnemonic('K');

    toolMenu_DevScreen.add(toolMenu4);
    toolMenu_DevScreen.add(toolMenu1);
    toolMenu_DevScreen.add(toolMenu2);
    toolMenu_DevScreen.add(toolMenu5);
    toolMenu_DevScreen.addSeparator();
    toolMenu_DevScreen.add(outsideTool);

    toolMenu3 = menuItem(prtframe.getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3.setMnemonic('C');
    outsideTool.add(toolMenu3);

    if (prtframe.vecOutsideToolInfo.size() > 0 ) {
      outsideTool.addSeparator();
    }

    for (int i=0; i<prtframe.vecOutsideToolInfo.size(); i++ ) {
      String wk = (String)prtframe.vecOutsideToolInfo.elementAt(i) ;

      StringTokenizer st = new StringTokenizer(wk,",");
      int cnt = 0;
      while (st.hasMoreTokens()) {
        String data = st.nextToken();

        JMenuItem toolMenu6 = menuItem(new Integer(i+1).toString() + ":" + data, "OutsideTool_" + new Integer(i).toString(), null );
        outsideTool.add(toolMenu6);
        break;
      }
    }
    menubar.add(toolMenu_DevScreen);

    // ツールバー
    toolbar = new JToolBar();
    toolbtn1  = new JButton(Icon1);
    toolbtn2  = new JButton(Icon2);
    toolbtn4  = new JButton(Icon4);
    jToggleButton1 = new JToggleButton(Icon3_2);
    jToggleButton1.setEnabled(false);

    toolbtn1.setToolTipText(prtframe.getBilingualMsg("0003"));
    toolbtn2.setToolTipText(prtframe.getBilingualMsg("0004"));
    jToggleButton1.setToolTipText(prtframe.getBilingualMsg("0005"));
    toolbtn4.setToolTipText(prtframe.getBilingualMsg("0002"));

    toolbtn1.addActionListener(this);
    toolbtn1.setActionCommand("newPrjct");
    toolbtn2.addActionListener(this);
    toolbtn2.setActionCommand("openPrjct");

    toolbtn4.addActionListener(this);
    toolbtn4.setActionCommand("newFile");

    toolbar.add(toolbtn4);
    toolbar.add(toolbtn1);
    toolbar.add(toolbtn2);
    toolbar.add(jToggleButton1);

    jToggleButton1.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jToggleButton1_stateChanged(e);
      }
    });
    popup.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        jPopupMenu1_popupMenuWillBecomeInvisible(e);
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });

    // 検索文字列入力エリア作成
    // プロジェクト名
    prjLabel = new JLabel( prtframe.getBilingualMsg("0041") + "：");
    dirLabel = new JLabel( prtframe.getBilingualMsg("0044") + "：" );

    DirKeyword.setText("");
    DirKeyword.setEditable(false);
    JPanel DirKeywordPanel = new JPanel (new BorderLayout() );
    DirKeywordPanel.add(prjLabel,BorderLayout.WEST);
    DirKeywordPanel.add(DirKeyword,BorderLayout.CENTER);

    // ディレクトリ名、ディレクトリ検索ボタン
    SrchKeyword.setEditable(false);
    SrchKeyword.setBackground(Color.white);

    JPanel SrchKeywordPanel = new JPanel (new BorderLayout() );
    SrchKeywordPanel.add(dirLabel,BorderLayout.WEST);
    SrchKeywordPanel.add(SrchKeyword,BorderLayout.CENTER);
    JButton srchbtn = new JButton("…");
    srchbtn.addActionListener(this);
    srchbtn.setActionCommand("dirsrch");
    SrchKeywordPanel.add(srchbtn,BorderLayout.EAST);

    JPanel fileKindPanel = new JPanel();
    jRadioButton_filekind_dash = new JRadioButton(prtframe.getBilingualMsg("0137"),true);
    jRadioButton_filekind_bp = new JRadioButton(prtframe.getBilingualMsg("0216"),false);
    jRadioButton_bp_intakemode1 = new JRadioButton(prtframe.getBilingualMsg("0217"),true);
    jRadioButton_bp_intakemode2 = new JRadioButton(prtframe.getBilingualMsg("0218"),false);
    jRadioButton_bp_intakemode1.setEnabled(false);
    jRadioButton_bp_intakemode2.setEnabled(false);

    jRadioButton_filekind_dash.addActionListener(this);
    jRadioButton_filekind_dash.setActionCommand("filekind_dash");
    jRadioButton_filekind_bp.addActionListener(this);
    jRadioButton_filekind_bp.setActionCommand("filekind_bp");
    jRadioButton_bp_intakemode1.addActionListener(this);
    jRadioButton_bp_intakemode1.setActionCommand("bp_intakemode1");
    jRadioButton_bp_intakemode2.addActionListener(this);
    jRadioButton_bp_intakemode2.setActionCommand("bp_intakemode2");

    buttonGroup2.add(jRadioButton_filekind_dash);
    buttonGroup2.add(jRadioButton_filekind_bp);

    buttonGroup3.add(jRadioButton_bp_intakemode1);
    buttonGroup3.add(jRadioButton_bp_intakemode2);

    fileKindPanel.add(jRadioButton_filekind_dash);
    fileKindPanel.add(new JLabel(" "));
    fileKindPanel.add(jRadioButton_filekind_bp);
    fileKindPanel.add(new JLabel("["));
    fileKindPanel.add(jRadioButton_bp_intakemode1);
    fileKindPanel.add(jRadioButton_bp_intakemode2);
    fileKindPanel.add(new JLabel("]"));

    JPanel fileKindPanel2 = new JPanel(new BorderLayout());
    fileKindPanel2.add(fileKindPanel, BorderLayout.WEST);

    JPanel SrchKeywordPanel2 = new JPanel (new BorderLayout() );
    SrchKeywordPanel2.add(SrchKeywordPanel,BorderLayout.NORTH);
    SrchKeywordPanel2.add(fileKindPanel2,BorderLayout.CENTER);
    SrchKeywordPanel2.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    // NameServer検索　検索対象を選択するラジオボタン作成
    JPanel RadioBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    jRadioButton1 = new JRadioButton(prtframe.getBilingualMsg("0045"),true);
    jRadioButton2 = new JRadioButton(prtframe.getBilingualMsg("0046"),false);
    buttonGroup1.add(jRadioButton1);
    buttonGroup1.add(jRadioButton2);
    RadioBtnPanel.add(jRadioButton1);
    RadioBtnPanel.add(jRadioButton2);

    JPanel nmFieldBtnPanelsub = new JPanel(new BorderLayout());
    nsBtn = new JButton(prtframe.getBilingualMsg("0016"));
    nsBtn.addActionListener(this);
    nsBtn.setActionCommand("nssrch");
    nmFieldBtnPanelsub.add(NameServerField,BorderLayout.CENTER);
    nmFieldBtnPanelsub.add(nsBtn,BorderLayout.EAST);
    JPanel nmFieldBtnPanel  = new JPanel(new BorderLayout());
    nmFieldBtnPanel.add(nmFieldBtnPanelsub,BorderLayout.NORTH);

    JPanel FieldPanel = new JPanel(new BorderLayout());
    FieldPanel.add(RadioBtnPanel,BorderLayout.NORTH);
    FieldPanel.add(nmFieldBtnPanel,BorderLayout.CENTER);
    FieldPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

    // タブを作成
    jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
    jTabbedPane.addTab(prtframe.getBilingualMsg("0042"), SrchKeywordPanel2);
    jTabbedPane.addTab(prtframe.getBilingualMsg("0043"), FieldPanel);
    jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent e) {

      }
    });

    JPanel SrchPanelTop = new JPanel ();
    SrchPanelTop.setLayout(new BoxLayout(SrchPanelTop, BoxLayout.Y_AXIS));
    SrchPanelTop.add(DirKeywordPanel);
    SrchPanelTop.add(jTabbedPane);
    border1 = BorderFactory.createEmptyBorder(0,110,0,0);
    DirKeywordPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,10));
    jTabbedPane.setBorder(BorderFactory.createEmptyBorder(0,0,20,10));
    SrchPanelTop.setBorder(border1);

    // プレビュー画面
    previewTextArea = new JTextArea();
    previewTextArea.setBackground(new Color(255,255,244));
    previewTextArea.setEditable(false);
    JScrollPane previewScrPane = new JScrollPane();
    previewScrPane.getViewport().setView(previewTextArea);
    previewScrPane.setPreferredSize(new Dimension(200, 120));
    previewScrPane.setBorder(BorderFactory.createEmptyBorder(0,10,20,20));
    JPanel prviewPanel = new JPanel (new BorderLayout());
    prviewPanel.add(previewScrPane,BorderLayout.CENTER);
    titleBdr1 = new TitledBorder(prtframe.getBilingualMsg("0049"));
    prviewPanel.setBorder(titleBdr1);

    // 一覧表示作成
    Vector header = new Vector (Arrays.asList(hstr));
    Vector data = new Vector();

    model = new ReadOnlyTableModel();
		searchList = new JTable(model);
    ListSelectionModel rowModel = searchList.getSelectionModel();

    JTableHeader tableHeader = searchList.getTableHeader();
    tableHeader.setDefaultRenderer(new MyHeaderRenderer());

    //違う行を選択したことが通知されるListener
    rowModel.addListSelectionListener(this);

    JScrollPane scrPane = new JScrollPane();
    scrPane.getViewport().setView(searchList);
    scrPane.setPreferredSize(new Dimension(200, 120));
    scrPane.setBorder(BorderFactory.createEmptyBorder(0,10,20,20));

    /////////////////////////////////////////////////////////////////
    // added by takagaki from here
    search_result_tabbedpane = new JTabbedPane();
    search_result_tabbedpane.addTab(prtframe.getBilingualMsg("0220"), scrPane);
    agent_relation_panel = new AgentRelationPanel();
    agent_relation_panel.setPreviewArea(previewTextArea);
    search_result_tabbedpane.addTab(prtframe.getBilingualMsg("0221"), agent_relation_panel);
    search_result_tabbedpane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
      //System.out.println("タブのチェンジ");
      //boolean flag = true;
     	if (SrchKeyword.getText().equals("")) {
            //uchiya
            /**
            boolean jgchic = createFileChooser();
            if(!jgchic) {
              flag = false;
            }
            */
            return;
          }
        //  if (flag) {
		//検索結果のタブが選択された場合
            if (search_result_tabbedpane.getSelectedIndex() == 0) {
				  ListShow();
              if (jRadioButton_filekind_dash.isSelected() ) {
                getIncludeFileInfo();
              }
            }
		//組織関係のタブが選択された場合
            else {
              jTabbedPane.setSelectedIndex(0);
              AgentRelationShow();
            }
          //}
        }
    });
    
    // added by takagaki to here
    /////////////////////////////////////////////////////////////////

    JPanel SrchListTop = new JPanel (new BorderLayout());
    //    SrchListTop.add(scrPane,BorderLayout.CENTER);  // original (modified by takagaki)
    SrchListTop.add(search_result_tabbedpane,BorderLayout.CENTER); // modified by takagaki
    titleBdr2 = new TitledBorder(prtframe.getBilingualMsg("0047"));
    SrchListTop.setBorder(titleBdr2);

    JSplitPane list_preview_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,SrchListTop,prviewPanel);
    list_preview_split.setDividerSize(3);

    // 取り込みボタン
    JLabel ArrowLabel = new JLabel();
    ImageIcon icon1 = getImageIcon ("resources/arrrowRL.gif");
    ArrowLabel.setIcon(icon1);
		JPanel Arowpanel = new JPanel();
		Arowpanel.add(ArrowLabel);

    ArrowLabel2 = new JLabel();
    ArrowLabel2.setText("  " + prtframe.getBilingualMsg("0127") + "  ");

    intkbtn = new JButton(prtframe.getBilingualMsg("0128"));
    intkbtn.addActionListener(this);
    intkbtn.setActionCommand("intake");

    JPanel getBtnPanel = new JPanel (new GridLayout(3,1) );

		getBtnPanel.add(Arowpanel);
    getBtnPanel.add(ArrowLabel2);
    getBtnPanel.add(intkbtn);

    JPanel getBtnPanelTop = new JPanel (new BorderLayout() );

    getBtnPanelTop.add(getBtnPanel,BorderLayout.NORTH);

    //結果表示で使用するattribute
    attr1 = new SimpleAttributeSet();
    StyleConstants.setForeground(attr1,Color.blue);
    attr2 = new SimpleAttributeSet();
    StyleConstants.setForeground(attr2,Color.red);

    JPanel ResultPanel = new JPanel( new BorderLayout() );
    ResultTextPanel = new JTextPane();
    ResultTextPanel.setText("");
    JScrollPane scrlPane = new JScrollPane(ResultTextPanel);
    titleBdr3 = new TitledBorder(prtframe.getBilingualMsg("0048"));
    scrlPane.setBorder(titleBdr3);
    ResultPanel.add(scrlPane, BorderLayout.CENTER);

    splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,list_preview_split,ResultPanel);
    //splitPane1.setDividerLocation((int)(splitPane1.getMaximumDividerLocation()) * 6);
    splitPane1.setDividerLocation(prtframe.getHeight()/10*5);

    // 検索条件パネルと一覧パネルを同じパネルに入れる
    JPanel p1 = new JPanel (new BorderLayout() ) ;
    p1.add(SrchPanelTop,BorderLayout.NORTH);
    p1.add(splitPane1,BorderLayout.CENTER);
    p1.add(getBtnPanelTop,BorderLayout.WEST);
    this.add(p1,BorderLayout.CENTER);

  }

  public void this_componentResized(ComponentEvent e) {
    splitPane1.setDividerLocation(prtframe.getHeight()/10*5);
    //splitPane1.setDividerLocation((int)(splitPane1.getMaximumDividerLocation()) * 6);
  }
	/**
	* ImageIcon処理
	* @param String path パス<BR>
	*/
  private ImageIcon getImageIcon(String path) {
    java.net.URL url = this.getClass().getResource(path);
    return new ImageIcon(url);
  }

	/**
	* FileChooser作成
	*/
  public boolean createFileChooser() {
    Container cont;
    cont = this.getParent();
	//UIManager.getDefaults().put("FileChooser.fileNameLabelText","ディレクトリ名");
    fdlg = new JFileChooser(DefaultDir);
    fdlg.setDialogType(JFileChooser.OPEN_DIALOG);
    fdlg.setDialogTitle(prtframe.getBilingualMsg("0110"));
    fdlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    if(fdlg.showOpenDialog(cont) != JFileChooser.APPROVE_OPTION ){
      return false;
    }

    return true;
  }

	/**
	* イベント処理
	* @param ActionEvent e
	*/
  public void actionPerformed(ActionEvent e) {
    try{

      String action = e.getActionCommand();

      if (action.equals("newPrjct")){
        // 新規プロジェクト画面表示
        NewPrjWin = new NewProject (prtframe, null);
        //NewPrjWin.show();
		    NewPrjWin.setVisible(true);
      }
      else if (action.equals("openPrjct")){
        //プロジェクトを開く
        openprojectfile();
      }
      else if (action.equals("againPrjct")){
        //プロジェクトを開き直す
        if(e.getSource() instanceof JMenuItem){
          JMenuItem menuitem = (JMenuItem)e.getSource();
          prtframe.readProjectFile(menuitem.getText());
        }
      }
      else if (action.equals("delPrjct") ) {
        //プロジェクトの削除
				prtframe.deleteProject();
      }
      else if (action.equals("deletehist") ) {
        prtframe.deleteOpenHist();
      }
      else if (action.equals("dirsrch")){
      	
        //ディレクトリ検索画面表示
        boolean jgchic = createFileChooser();
        
        if( jgchic ){
           if (search_result_tabbedpane.getSelectedIndex() == 0) { // added by takagaki
          
             ListShow();
			      //System.out.println("listshow finished");
            
             if (jRadioButton_filekind_dash.isSelected() ) {
               getIncludeFileInfo();
             }
           } 
           else { // added by takagaki
             AgentRelationShow(); // added by takagaki
           } 
         }
         
      }
      else if (action.equals("nssrch")){
        //NameServer検索処理
        NameServerSrch();
        search_result_tabbedpane.setSelectedIndex(0); // added by takagaki
      }
      else if( action.equals("intake") ){
        //リポジトリから取り込み
        intakefile();
      }
      else if( action.equals("newFile") ){
        //新規ファイル作成
        NewChoiceWin = new NewChoice (prtframe,project );
        //NewChoiceWin.show();
		      NewChoiceWin.setVisible(true);
      }
      else if( action.equals("againtPrjctBtn") ){

      }
      else if( action.equals("outside") ){
        OutTool = new OutsideTool (prtframe);
        //OutTool.show();
		    OutTool.setVisible(true);
      }
      else if (action.equals("exitwindow")) {
        prtframe.SystemExit();
      }
      //--------------------------------------------------------------------------
      // エディタ設定
      //--------------------------------------------------------------------------
      else if (action.equals("SetupEditor")) {
        prtframe.SetupEditor();
      }
      //--------------------------------------------------------------------------
      // キーワード設定
      //--------------------------------------------------------------------------
      else if (action.equals("SetupKeyword")) {
        prtframe.SetupKeyword();
      }
      //--------------------------------------------------------------------------
      // キーワードマップ
      //--------------------------------------------------------------------------
      else if (action.equals("SetupKeymap")) {
        prtframe.IdeOption();
      }
      //--------------------------------------------------------------------------
      // 外部ツール構成ダイアログの表示
      //--------------------------------------------------------------------------
      else if (action.equals("SetOutsideTool")) {
        prtframe.SetOutsideTool();
      }
      //--------------------------------------------------------------------------
      // 外部ツールの実行
      //--------------------------------------------------------------------------
      else if (action.startsWith("OutsideTool_")) {
        prtframe.ExecOutsideTool (action );
      }
      //--------------------------------------------------------------------------
      // シミュレータオプション
      //--------------------------------------------------------------------------
      else if (action.startsWith("SimulatorOption")) {
        prtframe.SimulatorOption( );
      }
      else if (action.equals("filekind_dash")) {
        jRadioButton_bp_intakemode1.setEnabled(false);
        jRadioButton_bp_intakemode2.setEnabled(false);
        ListShow();
        getIncludeFileInfo();
      }
      else if (action.equals("filekind_bp")) {
        jRadioButton_bp_intakemode1.setEnabled(true);
        jRadioButton_bp_intakemode2.setEnabled(true);
        ListShow();
      }


    }
    catch(Exception e2){

    }
  }

	/**
	* mousePressed処理
	* @param MouseEvent e
	*/
  public void mousePressed(MouseEvent e){
  }

	/**
	* mouseReleased処理
	* @param MouseEvent e
	*/
  public void mouseReleased(MouseEvent e){
  }

	/**
	* mouseClicked処理
	* @param MouseEvent e
	*/
  public void mouseClicked(MouseEvent e){
    if (!e.isPopupTrigger()) {
      popup.show (e.getComponent(), e.getX(), e.getY());
    }
  }

	/**
	* mouseEntered処理
	* @param MouseEvent e
	*/
  public void mouseEntered(MouseEvent e){}

	/**
	* mouseExited処理
	* @param MouseEvent e
	*/
	public void mouseExited(MouseEvent e){}


  private void AgentRelationShow(){
    String dirpath = fdlg.getSelectedFile().getAbsolutePath(); // added by takagaki
    SrchKeyword.setText(dirpath); // added by takagaki
    agent_relation_panel.setRepositoryDirPath(dirpath); // added by takagaki
    //ここを直す
    agent_relation_panel.showAgentRelation(); // added by takagaki
  } 

  /**
  * JTableにファイル一覧を表示
  */
  private void ListShow(){
    //ディレクトリエリアにパスを表示
    String dirpath = fdlg.getSelectedFile().getAbsolutePath();
    if (dirpath == null ) {
      return;
    }
    SrchKeyword.setText( dirpath );
    
    //指定されたディレクトリ内に存在するdashファイルを抽出、表示
    File f = new File(dirpath);
    String[] FileList = f.list();
    //for(int i=0;i<FileList.length;i++){
    //	System.out.println(FileList[i]);
    //}
    
    // 表示されているリストの削除
    try {
      searchList.clearSelection();
    }
    catch(Exception ee) {}
    
    int rowcnt = model.getRowCount();
    for( int i=0; i < rowcnt; i++ ){
      model.removeRow(0);
    }
	
    for (int i=0; i<FileList.length; i++ ) {
      String chkval = FileList[i];

      //現状はdashファイルのみ
      if (jRadioButton_filekind_dash.isSelected() ) {
        if( chkval.toLowerCase().endsWith(".dash") || chkval.toLowerCase().endsWith(".rset")){
          String[] val = {chkval};
          try{
          model.addRow(val);
          }catch(Exception e){
          	e.printStackTrace();
          }
    		  

        }
      }
      else {
        
        if( chkval.toLowerCase().endsWith(".java")){
          String[] val = {chkval};
          model.addRow(val);
        }
      }
    }

	  int rowfinlcnt = model.getRowCount();
    if(rowfinlcnt == 0){
      String[] val = {prtframe.getBilingualMsg("0096")};
      model.addRow(val);
    }

		//取り込みの際に必要な実体ファイルが存在するディレクトリをセット
		setjgIntake("dir");

	}


  /**
  * リポジトリからの取り込み処理
  */
  private boolean intakefile() throws IOException , java.lang.Exception{

    try {
      Object[] options = { "OK" };
      String ExstPrjfilePath = project.getProjectFileNameWithPath();
      String ExstPrjPath     = project.getProjectPath();

      if(DirKeyword.getText().equals("")){
        JOptionPane.showOptionDialog(this,
            prtframe.getBilingualMsg("0095"),prtframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

      String[] selfiles; // added by takagaki
      Vector vecCopyEndRulesetFile = new Vector(); // added by takagaki
      int rowcnt; // added by takagaki

      if (search_result_tabbedpane.getSelectedIndex() == 0) { // added by takagaki

        //選択された行のインデックスを取得
        int[] rows = searchList.getSelectedRows();
        // int rowcnt = rows.length; // original (modified by takagaki)
        rowcnt = rows.length; // modified by takagaki

        if( rowcnt == 0 || (rowcnt == 1 && searchList.getValueAt(0,0).equals(prtframe.getBilingualMsg("0096")) )){
          JOptionPane.showOptionDialog(this,
                                       prtframe.getBilingualMsg("0097"),prtframe.getBilingualMsg("0129"),
                                       JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                       null, options, options[0]);
          return false;
        }

        // ADD NAKA
        // Vector vecCopyEndRulesetFile = new Vector(); // removed by takagaki

        //選択されたファイル名を取得
        // String[] selfiles = new String[rowcnt]; // original (modified by takagaki)
        selfiles = new String[rowcnt]; // modified by takagaki
        for(int i=0; i < rowcnt; i++){
          selfiles[i] = (String)searchList.getValueAt(rows[i],0);
          vecCopyEndRulesetFile.addElement(selfiles[i]);
        }

      } // added by takagaki
      else { // added by takagaki
        selfiles = agent_relation_panel.getSelectedFileNames(); // added by takagaki
        rowcnt = selfiles.length; // added by takagaki
        for (int i=0; i<rowcnt; i++) { // added by takagaki
          vecCopyEndRulesetFile.addElement(selfiles[i]); // added by takagaki
        } // added by takagaki
      } // added by takagaki

      //現在登録されているファイルを取得
      FileReader f_in;
      BufferedReader b_in;
      String sLine = "";

      if(ExstPrjfilePath == null || ExstPrjfilePath.equals(""))
          return false;

      String rootName = ExstPrjfilePath.substring(ExstPrjfilePath.lastIndexOf("\\")+1);

      //読み込み処理
      Vector exstList = new Vector();
      Vector writeexstList = new Vector();
      String exstfilename = DirKeyword.getText();

      try {
          f_in = new FileReader(ExstPrjfilePath);
          b_in = new BufferedReader(f_in);
          while((sLine = b_in.readLine()) != null) {

            if(!exstfilename.toLowerCase().equals(sLine.toLowerCase())){
              exstList.addElement(sLine.toLowerCase());
              writeexstList.addElement(sLine);
            }

          }
          b_in.close();

      } catch(Exception ex) {
          return false;
      }

      //ファイルコピー処理
      Vector ReplacefileList = new Vector();
      Vector writeprjfileList = new Vector();

      String OrgFilePath = SrchKeyword.getText();
      if (!SrchKeyword.getText().endsWith(File.separator)) {
        OrgFilePath = SrchKeyword.getText() + File.separator;
      }

      String MakFilePath = ExstPrjPath;
      if (!ExstPrjPath.endsWith(File.separator)) {
        MakFilePath = ExstPrjPath + File.separator;
      }

      //確認表示のクリア
      ResultTextPanel.setText("");
      doc = ResultTextPanel.getDocument();

      for(int i=0; i < rowcnt; i++){

        String orgfile = "";

        //実体ファイルが存在するディレクトリを指定
        if( jgIntake.equals("dir") ){
        	//ディレクトリ検索の場合
	        orgfile = OrgFilePath + selfiles[i];
        }
        else if( jgIntake.equals("nm") ){
        	//NameServer検索の場合
					String AbsolutePath = (String)nmFile_tb.get(selfiles[i]);

					if(AbsolutePath == null){

		        //結果表示
						ResultTextPanel.setCaretPosition(doc.getLength());
						ResultTextPanel.setParagraphAttributes(attr2,true);

						String cstmStr = prtframe.getBilingualMsg("0098");
						cstmStr = cstmStr.replaceAll("filename",selfiles[i]);

						doc.insertString(doc.getLength(),cstmStr + "\n",null);

						continue;
					}
	        orgfile = AbsolutePath;
        }

        if (jRadioButton_filekind_bp.isSelected() ) {
          if (!MakFilePath.endsWith("java_" + File.separator) ) {
            MakFilePath += "java_" + File.separator;
          }
        }
        String makfile = MakFilePath + selfiles[i];

        int copytype = 0;

        if (jRadioButton_filekind_dash.isSelected() ) {
        	boolean exists = false;
        	for (int j = 0; j<exstList.size(); j++ ) {
        		String filename = (String)exstList.elementAt(j);
        		if (filename.equals("") ) {
        			continue;
        		}
        		String filenamewk = filename.substring(filename.lastIndexOf(File.separator)+1);
        		if (filenamewk.equals(selfiles[i].toLowerCase()) ) {
        			exists = true;
        			makfile = MakFilePath + filename;
        			break;
        		}
        	}
          //if(exstList.indexOf(selfiles[i].toLowerCase()) != -1){
          if(exists){
            //既にファイルが存在している場合
            String cstmStr = prtframe.getBilingualMsg("0099");
            cstmStr = cstmStr.replaceAll("filename",selfiles[i]);

            Object[] options2 = { prtframe.getBilingualMsg("0191"),prtframe.getBilingualMsg("0192"),prtframe.getBilingualMsg("0126") };

            int jgcnt = JOptionPane.showOptionDialog(this,
                        cstmStr,prtframe.getBilingualMsg("0130"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options2, options2[0]);

            if( jgcnt == 1 ){
              //いいえ
              continue;
            }
            else if(jgcnt == 2 || jgcnt == -1){
              //取消し
              return false;
            }
          }
          else{

            //プロジェクトファイルに新規に書き込むファイルを取得
            writeprjfileList.addElement(selfiles[i]);

            //ディレクトリに同名のファイルが存在するかを判別
            File f = new File(makfile);
            if(f.isFile()){

              String bgc_list[] = {prtframe.getBilingualMsg("0100"),prtframe.getBilingualMsg("0101"),prtframe.getBilingualMsg("0126")};
              String cstmStr = prtframe.getBilingualMsg("0102");

              //[path]を入れ替える
              int chkcnt = cstmStr.indexOf("path");
              cstmStr = cstmStr.substring(0,chkcnt) + MakFilePath + cstmStr.substring(chkcnt+4);

              //[filename]を入れ替える
              cstmStr = cstmStr.replaceFirst("filename",selfiles[i]);

              int ans = JOptionPane.showOptionDialog(this,cstmStr,
                                                     prtframe.getBilingualMsg("0130"),JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,bgc_list,null);

              if(ans == 0){
                //ディレクトリ内にある実体ファイルを置き換え、プロジェクトファイルに追加。
                copytype = 1;
              }
              else if(ans == 1){
                //ディレクトリ内にある実体ファイルをプロジェクトファイルに追加（復活）
                copytype = 2;
                //結果表示
                ResultTextPanel.setCaretPosition(doc.getLength());
                ResultTextPanel.setParagraphAttributes(attr1,true);

                String cstmStr2 = prtframe.getBilingualMsg("0103");
                cstmStr2 = cstmStr2.replaceAll("filename",selfiles[i]);

                doc.insertString(doc.getLength(),cstmStr2 + "\n",null);

              }
              else{
                //取消し
                return false;
              }

            }

          }
        }
        else {
          if (new File(makfile).exists() ) {
            //既にファイルが存在している場合
            String cstmStr = prtframe.getBilingualMsg("0099");
            cstmStr = cstmStr.replaceAll("filename",selfiles[i]);

            Object[] options2 = { prtframe.getBilingualMsg("0191"),prtframe.getBilingualMsg("0192"),prtframe.getBilingualMsg("0126") };

            int jgcnt = JOptionPane.showOptionDialog(this,
                        cstmStr,prtframe.getBilingualMsg("0130"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options2, options2[0]);

            if( jgcnt == 1 ){
              //いいえ
              continue;
            }
            else if(jgcnt == 2 || jgcnt == -1){
              //取消し
              return false;
            }
          }
        }

        // コピー処理実行
        if( copytype == 0 || copytype == 1){
          //通常
          if (jRadioButton_filekind_dash.isSelected() ) {
            String[] copyfiles = {orgfile,makfile};
            if( !filecopy(copyfiles) ){
              //結果表示 コピー
              ResultTextPanel.setCaretPosition(doc.getLength());
              ResultTextPanel.setParagraphAttributes(attr2,true);

              String cstmStr = prtframe.getBilingualMsg("0104");
              cstmStr = cstmStr.replaceAll("filename",selfiles[i]);

               doc.insertString(doc.getLength(),cstmStr + "\n",null);
              continue;
            }

            //結果表示
             ResultTextPanel.setCaretPosition(doc.getLength());
             ResultTextPanel.setParagraphAttributes(attr1,true);

            String cstmStr = prtframe.getBilingualMsg("0105");
            cstmStr = cstmStr.replaceAll("filename",selfiles[i]);

             doc.insertString(doc.getLength(),cstmStr + "\n",null);

            //上書きするファイル名を取得
            ReplacefileList.addElement(selfiles[i]);
          }
          else {
            if (jRadioButton_bp_intakemode1.isSelected() ) {
              String[] copyfiles = {orgfile,makfile};
              if( !filecopy(copyfiles) ){
                //結果表示 コピー
                ResultTextPanel.setCaretPosition(doc.getLength());
                ResultTextPanel.setParagraphAttributes(attr2,true);

                String cstmStr = prtframe.getBilingualMsg("0104");
                cstmStr = cstmStr.replaceAll("filename",selfiles[i]);

                 doc.insertString(doc.getLength(),cstmStr + "\n",null);
                continue;
              }

              if( copytype == 0){
                //プロジェクトファイル更新
                File fp2  = new File ( makfile + "_inf" );
                FileOutputStream fos2 = new FileOutputStream (fp2);
                PrintWriter pw2  = new PrintWriter (fos2);

                //新規ファイル
                pw2.println("[path]");
                pw2.println("current");
                pw2.println("[relation dash file]");
                pw2.close ();
              }
              //結果表示
               ResultTextPanel.setCaretPosition(doc.getLength());
               ResultTextPanel.setParagraphAttributes(attr1,true);

              String cstmStr = prtframe.getBilingualMsg("0105");
              cstmStr = cstmStr.replaceAll("filename",selfiles[i]);

               doc.insertString(doc.getLength(),cstmStr + "\n",null);

              //上書きするファイル名を取得
              ReplacefileList.addElement(selfiles[i]);
            }
            else {
              if( copytype == 0){
                //プロジェクトファイル更新
                File fp2  = new File ( makfile + "_inf" );
                FileOutputStream fos2 = new FileOutputStream (fp2);
                PrintWriter pw2  = new PrintWriter (fos2);

                //新規ファイル
                pw2.println("[path]");
                pw2.println(orgfile);
                pw2.println("[relation dash file]");
                pw2.close ();
              }
              else {

                b_in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(makfile + "_inf"),"JISAutoDetect"));

                int inf_kind = 0;
                String javafilename = "";
                Vector vecOrgData = new Vector();
                while ((sLine = b_in.readLine()) != null)
                {
                  if (sLine.equals("[path]")) {
                    inf_kind = 1;//Path情報
                    vecOrgData.addElement(sLine);
                  }
                  else if (sLine.equals("[relation dash file]")) {
                    inf_kind = 2;//関連Dashファイル情報
                    vecOrgData.addElement(sLine);
                  }
                  else {
                    if (inf_kind == 1 ) {
                      vecOrgData.addElement(makfile);
                    }
                    else {
                      vecOrgData.addElement(sLine);
                    }

                  }
                }
                b_in.close();

                //プロジェクトファイル更新
                File fp2  = new File ( makfile + "_inf" );
                FileOutputStream fos2 = new FileOutputStream (fp2);
                PrintWriter pw2  = new PrintWriter (fos2);

                //新規ファイル
                for (int j=0; j<vecOrgData.size(); j++ ) {
                  pw2.println("[path]");

                }
                pw2.println("[path]");
                pw2.println(orgfile);
                pw2.println("[relation dash file]");
                pw2.close ();


              }
              //結果表示
               ResultTextPanel.setCaretPosition(doc.getLength());
               ResultTextPanel.setParagraphAttributes(attr1,true);

              String cstmStr = prtframe.getBilingualMsg("0105");
              cstmStr = cstmStr.replaceAll("filename",selfiles[i]);

               doc.insertString(doc.getLength(),cstmStr + "\n",null);

              //上書きするファイル名を取得
              ReplacefileList.addElement(selfiles[i]);

            }
          }
        }

        // ADD NAKA
        if (selfiles[i].toLowerCase().endsWith(".rset") ) {
          if (vecCopyEndRulesetFile.indexOf(selfiles[i]) != -1 ) {
            vecCopyEndRulesetFile.addElement(selfiles[i]);
          }
        }
        else {
          // ルールセットを使用しているかチェックする
          if ((Vector)htIncludeFileName.get(selfiles[i]) != null ) {
            // 使用している
            Vector vec1 = (Vector)htIncludeFileName.get(selfiles[i]);
            Vector vec2 = (Vector)htIncludeFileName_with_Path.get(selfiles[i]);

            for (int j=0; j<vec1.size(); j++ ) {
              String RsetFileName = (String)vec1.elementAt(j);
              String RsetFileNameWithPath = (String)vec2.elementAt(j);
              if (vecCopyEndRulesetFile.indexOf(RsetFileName) != -1 ) {
                continue;
              }

              if (RsetFileNameWithPath.equals("") ) {
                //結果表示 コピー
                ResultTextPanel.setCaretPosition(doc.getLength());
                ResultTextPanel.setParagraphAttributes(attr2,true);

								String cstmStr = prtframe.getBilingualMsg("0131");
								cstmStr = cstmStr.replaceAll("filename1",selfiles[i]);
								cstmStr = cstmStr.replaceAll("filename2",RsetFileName);

                doc.insertString(doc.getLength(), cstmStr + "\n",null);
                continue;
              }

              if (!new File(RsetFileNameWithPath).isFile() ) {
                //結果表示 コピー
                ResultTextPanel.setCaretPosition(doc.getLength());
                ResultTextPanel.setParagraphAttributes(attr2,true);

								String cstmStr = prtframe.getBilingualMsg("0131");
								cstmStr = cstmStr.replaceAll("filename1",selfiles[i]);
								cstmStr = cstmStr.replaceAll("filename2",RsetFileName);

                doc.insertString(doc.getLength(), cstmStr + "\n",null);
                continue;
              }

              // ルールセットファイルのコピー実行
              makfile = MakFilePath + RsetFileName;
              copytype = 0;

		        	boolean exists = false;
		        	for (int k = 0; k<exstList.size(); k++ ) {
		        		String filename = (String)exstList.elementAt(k);
		        		if (filename.equals("") ) {
		        			continue;
		        		}
		        		String filenamewk = filename.substring(filename.lastIndexOf(File.separator)+1);
		        		if (filenamewk.equals(RsetFileName.toLowerCase()) ) {
		        			exists = true;
		        			makfile = MakFilePath + filename;
		        			break;
		        		}
		        	}


							if(exists){
              //if(exstList.indexOf(RsetFileName.toLowerCase()) != -1){
                //既にファイルが存在している場合

								String cstmStr = prtframe.getBilingualMsg("0099");
								cstmStr = cstmStr.replaceAll("filename",RsetFileName);

								Object[] options2 = { prtframe.getBilingualMsg("0191"),prtframe.getBilingualMsg("0192"),prtframe.getBilingualMsg("0126") };

								int jgcnt = JOptionPane.showOptionDialog(this,
														cstmStr,prtframe.getBilingualMsg("0130"),
														JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
														null, options2, options2[0]);

                if( jgcnt == 1 ){
                  //いいえ
                  continue;
                }
                else if(jgcnt == 2 || jgcnt == -1){
                  //取消し
                  return false;
                }
              }
              else{
                //プロジェクトファイルに新規に書き込むファイルを取得
                writeprjfileList.addElement(RsetFileName);

                //ディレクトリに同名のファイルが存在するかを判別
                File f = new File(makfile);
                if(f.isFile()){

				          String bgc_list[] = {prtframe.getBilingualMsg("0100"),prtframe.getBilingualMsg("0101"),prtframe.getBilingualMsg("0126")};

									String cstmStr = prtframe.getBilingualMsg("0102");

									//[path]を入れ替える
									int chkcnt = cstmStr.indexOf("path");
									cstmStr = cstmStr.substring(0,chkcnt) + MakFilePath + cstmStr.substring(chkcnt+4);

									//[filename]を入れ替える
									cstmStr = cstmStr.replaceFirst("filename",RsetFileName);

			  	        int ans = JOptionPane.showOptionDialog(this,cstmStr,
                                                   prtframe.getBilingualMsg("0130"),JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,bgc_list,null);

                  if(ans == 0){
                    //ディレクトリ内にある実体ファイルを置き換え、プロジェクトファイルに追加。
                    copytype = 1;
                  }
                  else if(ans == 1){
                    //ディレクトリ内にある実体ファイルをプロジェクトファイルに追加（復活）
                    copytype = 2;
                    //結果表示
                    ResultTextPanel.setCaretPosition(doc.getLength());
                    ResultTextPanel.setParagraphAttributes(attr1,true);

										String cstmStr2 = prtframe.getBilingualMsg("0103");
										cstmStr2 = cstmStr2.replaceFirst("filename",RsetFileName);

                    doc.insertString(doc.getLength(), cstmStr2 + "\n",null);
                  }
                  else{
                    //取消し
                    return false;
                  }

                }

              }

              // コピー処理実行
              if( copytype == 0 || copytype == 1){
                //通常
                String[] copyfiles = {RsetFileNameWithPath,makfile};
                if( !filecopy(copyfiles) ){
                  //結果表示 コピー
                  ResultTextPanel.setCaretPosition(doc.getLength());
                  ResultTextPanel.setParagraphAttributes(attr2,true);

									String cstmStr = prtframe.getBilingualMsg("0104");
									cstmStr = cstmStr.replaceAll("filename",RsetFileName);

			      	   	doc.insertString(doc.getLength(),cstmStr + "\n",null);

                  continue;
                }

                //結果表示
                ResultTextPanel.setCaretPosition(doc.getLength());
                ResultTextPanel.setParagraphAttributes(attr1,true);

								String cstmStr = prtframe.getBilingualMsg("0105");
								cstmStr = cstmStr.replaceAll("filename",RsetFileName);

								doc.insertString(doc.getLength(),cstmStr + "\n",null);

                //上書きするファイル名を取得
                ReplacefileList.addElement(RsetFileName);
              }

              vecCopyEndRulesetFile.addElement(RsetFileName);

            }
          }
        }
      }

			//上書きするファイルを送る
      if (jRadioButton_filekind_dash.isSelected() ) {
        prtframe.setReplaceFiles(ReplacefileList);
        //プロジェクトファイル更新
        File fp  = new File ( ExstPrjfilePath );
        FileOutputStream fos = new FileOutputStream (fp);
        PrintWriter pw  = new PrintWriter (fos);

        //既存のファイル
        for(int i=0; i < writeexstList.size(); i++){
          String writefilename = (String)writeexstList.elementAt(i);
          pw.println(writefilename);
        }

        //新規ファイル
        for(int i=0; i < writeprjfileList.size(); i++){
          String writefilename = (String)writeprjfileList.elementAt(i);
          pw.println(writefilename);
        }
        pw.close ();
      }
      //ツリー更新
      prtframe.readProjectFile(ExstPrjfilePath);

    } catch ( Exception e ){
      return false;
    }

    return true;
  }

  /**
  * ファイルコピー処理
  */
  public static boolean filecopy(String args[]){
    try{
      // コピー元、コピー先が同じファイルの場合、何もしない
      if (args[0].equals(args[1]) ) {
        return true;
      }

      String  strBuff;
      File fp  = new File ( args[1] );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.close ();

      /* オブジェクトの生成 */
      BufferedReader  brInFile = new BufferedReader(new FileReader(args[0]));
      PrintWriter     pwOutFile = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));

      /* 読み込み処理 */
      while((strBuff = brInFile.readLine()) != null){
        pwOutFile.println(strBuff);
      }

      /* クローズ処理 */
      brInFile.close();
      pwOutFile.close();
    }
    catch(Exception e){
      System.err.println("ERROR : " + e);
      return false;
    }

    return true;
  }


  /**
  * プロジェクトを開く
  */
  public void openprojectfile() {
    Container cont;
    cont = this.getParent();
    JFileChooser fileChooser = new JFileChooser(DefaultDir);
    fileChooser.setDialogTitle(prtframe.getBilingualMsg("0004"));
    fileChooser.addChoosableFileFilter(new DashFileFilter());
    fileChooser.setLocale(Locale.ENGLISH);

    int ret = fileChooser.showOpenDialog(cont);
    File file = fileChooser.getSelectedFile();

    if (ret!=JFileChooser.APPROVE_OPTION || file==null){
      return;
    }

    if (!file.getName().toLowerCase().endsWith(".dpx")){
      Object[] options = { "OK" };
      JOptionPane.showOptionDialog(this,
            prtframe.getBilingualMsg("0106"),prtframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
      return;
    }

    //設定
    String abspath = fileChooser.getSelectedFile().getAbsolutePath();
    setDirKeyword(file.getName());

    //ツリー更新
    prtframe.readProjectFile(abspath);

  }


  /**
  * NameServer検索
  */
  public void NameServerSrch() {

		Object[] options = { "OK" };

    // ネームサーバが起動しているかチェックする
    if (dvm.comInt.getNameServer() == null ) {
      // nullの場合、起動していないのでエラーを表示して終了
      if (dvm.comInt.getNameServerErrMsg().equals("") ) {
        JOptionPane.showOptionDialog(this,
            prtframe.getBilingualMsg("0107"),prtframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
      }
      else {
        JOptionPane.showOptionDialog(this,
            dvm.comInt.getNameServerErrMsg(),prtframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
      }
      return;
    }

    // 検索キーワードの取得
    String searchKeyword = NameServerField.getText();

    if (searchKeyword.equals("") ) {
      JOptionPane.showOptionDialog(this,
            prtframe.getBilingualMsg("0108"),prtframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
      return;
    }

    // 検索キーワードの全角文字列を半角文字列に変換
    searchKeyword = searchKeyword.replaceAll("　", " ");

    // 半角文字列が連続している可能性があるので、連続スペースをひとつにまとめる
    char[] c1 = searchKeyword.toCharArray();
    char[] c2 = new char[c1.length];
    boolean prevSpace = false;
    int c2_index = 0;
    for (int i=0;i<c1.length; i++ ) {
      if (c1[i] == ' ' ) {
        if (!prevSpace ) {
          prevSpace = true;
          c2[c2_index++] = c1[i];
        }
      }
      else {
        prevSpace = false;
        c2[c2_index++] = c1[i];
      }
    }

    // 仮に連続スペースがあった場合、c2の要素が減少しているため、
    // そのサイズに合わせて、char配列を作成しなおす
    char[] c3 = new char[c2_index];
    for (int i=0; i<c2_index; i++ ) {
      c3[i] = c2[i];
    }

    // 検索キーワード文字列の作り直し
    searchKeyword = new String(c3);

    // 半角スペースを区切り文字として、キーワード一つ一つをベクターに格納
    StringTokenizer st = new StringTokenizer (searchKeyword, " " );
    Vector vecSearchKeyword = new Vector();
    while (st.hasMoreElements() ) {
      vecSearchKeyword.addElement(st.nextToken());
    }

    // 検索キーワードの数分、lookupを実行
    Vector vecResult = new Vector();
    Vector vecChkName = new Vector();

    for (int i=0; i<vecSearchKeyword.size(); i++ ) {

      Vector v = new Vector();

      if(jRadioButton1.isSelected()){
        // 名称で検索する場合
        String [][] selecter = { {":name",(String)vecSearchKeyword.elementAt(i)} };
        v = dvm.comInt.lookup(selecter);
      }
      else if(jRadioButton2.isSelected()){
        // 機能で検索する場合
        String [][] selecter = { {":function",(String)vecSearchKeyword.elementAt(i)} };
        v = dvm.comInt.lookup(selecter);
      }

      for (Enumeration e1 = v.elements(); e1.hasMoreElements(); ) {

				//重複チェック
	      String[] result = (String[])e1.nextElement();

	      if(vecChkName.indexOf(result[1]) == -1){
	  			//追加
		      vecResult.addElement(result);
	      	vecChkName.addElement(result[1]);
	      }

      }
    }

		//ファイルが存在したかを判別
		//結果の見方
		//結果は要素9の配列で返されるため、以下のようにして取り出す。
    for (Enumeration e1 = vecResult.elements(); e1.hasMoreElements(); ) {
      String[] result = (String[])e1.nextElement();

      for(int i=0; i < result.length; i++){
//        System.out.println("ans" + i +" : "+result[i]);
      }
    }

    // 表示されているリストの削除
    int rowcnt = model.getRowCount();
    for( int i=0; i < rowcnt; i++ ){
      model.removeRow(0);
    }

		//絞込み検索
		int chkcnt = 0;
    if(jRadioButton1.isSelected()){
    	// 名称で検索する場合
			chkcnt = 0;
    }
    else if(jRadioButton2.isSelected()){
    	// 機能で検索する場合
			chkcnt = 5;
    }

    if(jRadioButton2.isSelected()){
      Vector vecdelNo = new Vector();
      for (int i=0; i<vecResult.size(); i++ ) {
        String[] result = (String[])vecResult.elementAt(i);

        for (int j=0; j<vecSearchKeyword.size(); j++ ) {
          String moreChkStr = (String)vecSearchKeyword.elementAt(j);

          if( result[chkcnt].indexOf(moreChkStr) == -1 ){
            //AND検索に引っ掛からない場合は削除noを取得
            vecdelNo.addElement(result[1]);
            continue;
          }
        }
      }

      //取得した削除noをもとに削除処理
      for (int i=0; i<vecdelNo.size(); i++ ) {
        String delStr = (String)vecdelNo.elementAt(i);

        for( int j=0; j < vecResult.size(); j++ ){
          String[] result = (String[])vecResult.elementAt(j);
          if( result[0].equals(delStr) ){
            vecResult.remove(j);
            break;
          }
        }
      }
    }

    //指定したディレクトリ以下に含まれる全てのファイルの一覧を作成する
    nmFile_tb.clear();

    //リポジトリのパスを取得
    String path = System.getProperty("dash.r.path");
    File f  = new File (path);
    createFileList(f,nmFile_tb);

		//テーブル追加
    for (int i=0; i<vecResult.size(); i++ ) {
      String[] result = (String[])vecResult.elementAt(i);
      if (nmFile_tb.get(result[1] + ".dash" ) == null ) {
        continue;
      }

      //現状はdashファイルのみ
      String[] val = {result[1] + ".dash"};
      model.addRow(val);
    }

    int rowfinlcnt = model.getRowCount();
    if(rowfinlcnt == 0){
      String[] val = {prtframe.getBilingualMsg("0094")};
      model.addRow(val);
    }




		//取り込みの際に必要な実体ファイルが存在するディレクトリをセット
		setjgIntake("nm");

  }

	/**
	* DVM(Dashバーチャルマシン設定
	* @param DVM dvm
	*/
  public void setDVM (DVM dvm) {
    this.dvm = dvm;
  }

	/**
	* プロジェクトメニュー更新
	* @param Project project
	*/
  public void setProject (Project project ) {
    this.project = project;
    DefaultDir = project.getProjectPath();

    //プロジェクト削除メニュー更新
    if( project != null ){
	    deleteMenuitem.setEnabled(true);
	    setDirKeyword(project.getProjectFileName());
    }
    else{
	    deleteMenuitem.setEnabled(false);
	    setDirKeyword("");
    }

  }

	/**
	* パス取得
	* @param Project project
	*/
  public static void setDirKeyword( String inmsg ){
    DirKeyword.setText(inmsg);
  }

	/**
	* プロジェクトを開く際のデフォルトのパス情報
	* @param String indir
	*/
  public void setDefaultDir( String indir ){
    DefaultDir = indir;
  }

	/**
	* 取り込みの際に必要な実体ファイルが存在するディレクトリをセット
	* @param String kind
	*/
	public void setjgIntake( String kind ){
		jgIntake = kind;
	}

	/**
	* 開き直すメニューの更新
	*/
  public void setOpenHist( Vector histlist ){

    if(histlist.size() <= 0){
      openprjMenu.removeAll();
      popup.removeAll();
      openprjMenu.setEnabled(false);
      jToggleButton1.setEnabled(false);
    }
    else{
      openprjMenu.removeAll();
      popup.removeAll();
      openprjMenu.setEnabled(true);
      jToggleButton1.setEnabled(true);
      for(int i=0; i < histlist.size(); i++){
        String addname = (String)histlist.elementAt(i);

        //サブメニュー
        openprjMenu.add(menuItem(addname, "againPrjct", null));

        //ポップアップメニュー
        popup.add(menuItem(addname, "againPrjct", null));

      }
      //サブメニュー

			subMenuItem = new JMenuItem(prtframe.getBilingualMsg("0036"));
			subMenuItem.setActionCommand("deletehist");
			subMenuItem.addActionListener(this);

			popMenuItem = new JMenuItem(prtframe.getBilingualMsg("0036"));
			popMenuItem.setActionCommand("deletehist");
			popMenuItem.addActionListener(this);

      openprjMenu.add(subMenuItem);
      popup.add(popMenuItem);

    }

  }


	/**
	* プロジェクトを開く際のデフォルトのパス情報取得
	*/
  public static String getDefaultDir(){
    return DefaultDir;
  }

	/**
	* MenuItemAccelerator
	*/
  public void changeMenuItemAccelerator() {
    prtframe.setMenuItemAccelerator("new-file",item4);
    prtframe.setMenuItemAccelerator("open-project",item2);
  }

	/**
	* JMenuItem処理
	*/
  private JMenuItem menuItem(String label, String command, Icon icon) {
   JMenuItem item = new JMenuItem(label, icon);
   item.setActionCommand(command);
   item.addActionListener(this);
   return item;
	}

	/**
	* jPopupMenu1_popupMenuWillBecomeInvisible
	* @param PopupMenuEvent e
	*/
	void jPopupMenu1_popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		jToggleButton1.setSelected(false);
	}

	/**
	* jToggleButton1_stateChanged
	* @param ChangeEvent e
	*/
	void jToggleButton1_stateChanged(ChangeEvent e) {
		try {
  		popup.show(toolbar,jToggleButton1.getX(),jToggleButton1.getY()+jToggleButton1.getHeight());
		}
		catch (Exception ee ) {}
	}

	/**
	* 指定したディレクトリ以下に含まれる全てのファイルの一覧を作成する
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
				htFileTable.put(current_file.getName(),current_file.getAbsolutePath());
      }
    }
  }


  class ReadOnlyTableModel extends DefaultTableModel {

    public int getColumnCount(){
      return rowdata[0].length;
    }

    public String getColumnName(int column){
      return hstr[column];
    }

    public boolean isCellEditable(int rowIndex,int colmunIndex) {
      return false;
    }
  }

	/**
	* 詳細表示
	* @param ListSelectionEvent e
	*/
  public void valueChanged(ListSelectionEvent e) {
		//System.out.println("jgIntake= "+jgIntake);
    // 連続したイベントのうち、最後のものだけ処理する。
    String OrgFilePath = null;
    String filename =null;
    if( jgIntake.equals("dir") ){
    	
      OrgFilePath = SrchKeyword.getText();
      if (!OrgFilePath.endsWith(File.separator)) {
        OrgFilePath = OrgFilePath + File.separator;
      }
      if(searchList.getSelectedRow()==-1) return;
      filename = OrgFilePath + (String)searchList.getValueAt(searchList.getSelectedRow(),0);
    }
    else {
    	//add uchiya
      if(searchList.getSelectedRow()==-1) return;
      
      filename = (String)nmFile_tb.get((String)searchList.getValueAt(searchList.getSelectedRow(),0));
    }

    // ファイルを開いて、テキストペインに読み取る
    FileReader freader ;
    BufferedReader breader ;
    try {
      // バッファリングしたFileReaderを作成する
      freader = new FileReader (filename);
      breader = new BufferedReader (freader );
      // ファイルを読み取る
      previewTextArea.read(breader,null);
      previewTextArea.setCaretPosition(0);

      // ファイルをクローズする
      freader.close();
    }
    catch (Exception ee) {}

  }

  private Vector vecIncludeFileName = new Vector();
  private Hashtable htIncludeFileName = new Hashtable();
  private Hashtable htIncludeFileName_with_Path = new Hashtable();

  private void getIncludeFileInfo() {
    htIncludeFileName.clear();
    htIncludeFileName_with_Path.clear();
    Hashtable wkhtIncludeFileName = new Hashtable();
    int FileCount=searchList.getRowCount();

    String OrgFilePath = null;
    String filename =null;
    String filename_no_path =null;
    for (int i=0; i<FileCount; i++ ) {
      vecIncludeFileName.clear();
      if( jgIntake.equals("dir") ){
        OrgFilePath = SrchKeyword.getText();
        if (!OrgFilePath.endsWith(File.separator)) {
          OrgFilePath = OrgFilePath + File.separator;
        }
        filename = OrgFilePath + (String)searchList.getValueAt(i,0);
        filename_no_path = (String)searchList.getValueAt(i,0);
      }
      else {
        filename = (String)nmFile_tb.get((String)searchList.getValueAt(i,0));
        filename_no_path = (String)searchList.getValueAt(i,0);
      }

      // ファイルを開いて、テキストペインに読み取る
      FileReader freader ;
      BufferedReader breader ;
      try {
        // バッファリングしたFileReaderを作成する
        freader = new FileReader (filename);
        breader = new BufferedReader (freader );
        String Code = "";
        // ファイルを読み取る
        String sLine="";
        while((sLine = breader.readLine()) != null) {
            Code += sLine + "\n";
        }

        // ファイルをクローズする
        freader.close();
        try {
          Parser parser = new Parser(filename_no_path, Code, false);
          //parser.setDefaultDir(OrgFilePath);
          parser.parse();

          Vector vecIncudeFileName = parser.getIncludeFileNames2();
          wkhtIncludeFileName.put(filename_no_path,vecIncudeFileName);
          if (vecIncudeFileName.size() > 0 )  {
            int a = 1;
          }
        }
        catch (Exception e2 ) {}

      }
      catch (Exception ee) {}
    }

    for (Enumeration e = wkhtIncludeFileName.keys(); e.hasMoreElements(); ) {
      String FileName = (String)e.nextElement();
      Vector vec = (Vector)wkhtIncludeFileName.get(FileName);
      Vector vec1 = new Vector();
      Vector vec2 = new Vector();
      if (vec.size() > 0 )  {
        if( jgIntake.equals("dir") ){
          for (int i=0; i<vec.size(); i++ ) {
            String wk = OrgFilePath + (String)vec.elementAt(i);

            File file = new File (wk );
            if (file.exists() ) {
              vec1.addElement((String)vec.elementAt(i));
              vec2.addElement(wk);
            }
            else {
              // 検索する
              // 指定したディレクトリ以下に含まれる全てのファイルの一覧を作成する
              Hashtable htFileList = new Hashtable();

              String s = OrgFilePath;
              if (s.toLowerCase().indexOf("scripts") != -1 ) {
                s = s.substring(0,s.toLowerCase().indexOf("scripts"));
                s = "Scripts" + File.separator;
              }
              else {
                s = OrgFilePath;
              }
              File f  = new File (s);
              createFileList(f,htFileList);

              String filenamr_with_path = (String)htFileList.get((String)vec.elementAt(i));
              if (filenamr_with_path != null ) {
                vec1.addElement((String)vec.elementAt(i));
                vec2.addElement(filenamr_with_path);
              }
              else {
                vec1.addElement((String)vec.elementAt(i));
                vec2.addElement("");

              }
            }
          }
        }
        else {
          for (int i=0; i<vec.size(); i++ ) {
            // 検索する
            // 指定したディレクトリ以下に含まれる全てのファイルの一覧を作成する
            String filenamr_with_path = (String)nmFile_tb.get((String)vec.elementAt(i));
            if (filenamr_with_path != null ) {
              vec1.addElement((String)vec.elementAt(i));
              vec2.addElement(filenamr_with_path);
            }
            else {
              vec1.addElement((String)vec.elementAt(i));
              vec2.addElement("");
            }
          }
        }

        htIncludeFileName.put(FileName,vec1);
        htIncludeFileName_with_Path.put(FileName,vec2);
      }
    }

    for (Enumeration e = htIncludeFileName.keys(); e.hasMoreElements(); ) {
      String FileName = (String)e.nextElement();
      Vector vec = (Vector)htIncludeFileName.get(FileName);
      Vector vec2 = (Vector)htIncludeFileName_with_Path.get(FileName);
      if (vec.size() > 0 )  {

        for (int i=0; i<vec.size(); i++ ) {
          //System.out.println((String)vec.elementAt(i) );
          //System.out.println((String)vec2.elementAt(i) );
        }
      }
    }
  }

	/**
	* 外部ツール用メニューを作り直す
	*/
  public void ReMakeOutsideToolMenu() {

    // 外部ツール用メニューを作り直す
    outsideTool.removeAll();
    outsideTool2.removeAll();

    JMenuItem toolMenu3 = menuItem(prtframe.getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3.setMnemonic('C');
    outsideTool.add(toolMenu3);

    JMenuItem toolMenu3_2 = menuItem(prtframe.getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3_2.setMnemonic('C');
    outsideTool2.add(toolMenu3_2);

    if (prtframe.vecOutsideToolInfo.size() > 0 ) {
      outsideTool.addSeparator();
      outsideTool2.addSeparator();
    }

    for (int i=0; i<prtframe.vecOutsideToolInfo.size(); i++ ) {
      String wk = (String)prtframe.vecOutsideToolInfo.elementAt(i) ;

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
  }

	/**
	* バイリンガル処理
	*/
	public void changeFormLabel() {

    // ツールバー
    toolbtn1.setToolTipText(prtframe.getBilingualMsg("0003"));
    toolbtn2.setToolTipText(prtframe.getBilingualMsg("0004"));
    jToggleButton1.setToolTipText(prtframe.getBilingualMsg("0005"));
    toolbtn4.setToolTipText(prtframe.getBilingualMsg("0002"));

    // 検索文字列入力エリア作成
    // プロジェクト名
    prjLabel.setText(prtframe.getBilingualMsg("0041") + "：");
    dirLabel.setText(prtframe.getBilingualMsg("0044") + "：");

    // NameServer検索　検索対象を選択するラジオボタン作成
		jRadioButton1.setText(prtframe.getBilingualMsg("0045"));
		jRadioButton2.setText(prtframe.getBilingualMsg("0046"));
		nsBtn.setText(prtframe.getBilingualMsg("0016"));

    // タブを作成
		jTabbedPane.setTitleAt(0,prtframe.getBilingualMsg("0042"));
		jTabbedPane.setTitleAt(1,prtframe.getBilingualMsg("0043"));

    // プレビュー画面
		titleBdr1.setTitle(prtframe.getBilingualMsg("0049"));
		titleBdr2.setTitle(prtframe.getBilingualMsg("0047"));

    // 取り込みボタン
    ArrowLabel2.setText("  " + prtframe.getBilingualMsg("0127") + "  ");
		intkbtn.setText(prtframe.getBilingualMsg("0128"));

    //結果表示で使用するattribute
		titleBdr3.setTitle(prtframe.getBilingualMsg("0048"));


    jRadioButton_filekind_dash.setText(prtframe.getBilingualMsg("0137"));
    jRadioButton_filekind_bp.setText(prtframe.getBilingualMsg("0216"));
    jRadioButton_bp_intakemode1.setText(prtframe.getBilingualMsg("0217"));
    jRadioButton_bp_intakemode2.setText(prtframe.getBilingualMsg("0218"));
    
    //add uchiya
	search_result_tabbedpane.setTitleAt(0,prtframe.getBilingualMsg("0220"));
	search_result_tabbedpane.setTitleAt(1,prtframe.getBilingualMsg("0221"));
	}


	/**
	* バイリンガル処理
	*/
	public void changeMenuText(){

    // ファイルメニュー
		menu1.setText(prtframe.getBilingualMsg("0001") + "(F)");
		item4.setText(prtframe.getBilingualMsg("0002") + "(N)...");
		item1.setText(prtframe.getBilingualMsg("0003") + "(P)...");
		item2.setText(prtframe.getBilingualMsg("0004") + "(O)...");
		openprjMenu.setText(prtframe.getBilingualMsg("0005") + "(R)");
		deleteMenuitem.setText(prtframe.getBilingualMsg("0116"));
		item3.setText(prtframe.getBilingualMsg("0008") + "(X)");

    // ツールメニュー
    toolMenu_DevScreen.setText(prtframe.getBilingualMsg("0021") + "(T)");

    // メニューアイテム作成
    toolMenu1.setText(prtframe.getBilingualMsg("0022") + "(E)...");
    toolMenu2.setText(prtframe.getBilingualMsg("0023") + "(K)...");
    toolMenu4.setText(prtframe.getBilingualMsg("0117") + "...");
    toolMenu5.setText(prtframe.getBilingualMsg("0118") + "...");
		outsideTool.setText(prtframe.getBilingualMsg("0024") + "(O)");
		outsideTool2.setText(prtframe.getBilingualMsg("0024") + "(O)");
		toolMenu3.setText(prtframe.getBilingualMsg("0025") + "(C)...");

		//履歴
    if( subMenuItem != null ){
			subMenuItem.setText(prtframe.getBilingualMsg("0036"));
    }
    if( popMenuItem != null ){
			popMenuItem.setText(prtframe.getBilingualMsg("0036"));
    }

	}

  class MyHeaderRenderer extends JLabel implements TableCellRenderer{

    public Component getTableCellRendererComponent(
      JTable table, Object data, boolean isSelected, boolean hasFocus,
      int row, int column) {

      setOpaque(true);

			this.setHorizontalAlignment(SwingConstants.CENTER);

      setBorder(new BevelBorder(BevelBorder.RAISED));

      setBackground(Color.lightGray);
      setForeground(Color.black);

      setText(prtframe.getBilingualMsg("0133"));

      return this;
    }
  }


}

/**
* ファイルフィルタ
*/
class DashFileFilter extends javax.swing.filechooser.FileFilter {
  public boolean accept(File file) {
    if (file.isDirectory())
      return true;
    boolean accepted =false;
    String filename = file.getName().toLowerCase();
    if (filename.endsWith(".dpx") )
      accepted = true;
    return accepted;
  }

  public String getDescription() {
    return "Dash Project Files (*.dpx)";
  }
}

