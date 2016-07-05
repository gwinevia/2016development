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
import java.text.*;
import javax.swing.text.*;

/**
 * <p>SavePanel: </p>
 * <p>���|�W�g���o�^���: </p>
 */

public class SavePanel extends JPanel implements ActionListener{

  public JMenuBar menubar;
  public JToolBar toolbar;
  private BorderLayout borderLayout1 = new BorderLayout();
  private JRadioButton jRadioButton1 = new JRadioButton();
  private JRadioButton jRadioButton2 = new JRadioButton();
  private ButtonGroup buttonGroup1 = new ButtonGroup();
  private JTextField SrchKeyword = new JTextField();
  private JTextField DirKeyword = new JTextField();
  private Border border1;
  private JTable searchList = null;
  private String[] hstr = {"", "", ""};
  private static JMenu openprjMenu = null;
  private JToggleButton jToggleButton1 = new JToggleButton();
  private JPopupMenu popup = new JPopupMenu();
  private JMenuItem item2 = null;
  private JMenuItem item3 = null;
  private JMenu menu1 = null;
  private JMenuItem newProjectMenuItem = null;
  private JMenuItem toolMenu1 = null;
  private JMenuItem toolMenu2 = null;
  private JMenuItem toolMenu3 = null;
  private JMenuItem toolMenu4 = null;
  private JMenuItem toolMenu5 = null;
	private JLabel dirLabel = null;
	private TitledBorder titleBdr1 = null;
	private TitledBorder titleBdr2 = null;
	private JLabel cmntlabel = null;
	private JButton button1 = null;
	private JButton button2 = null;
	private JButton toolbtn2 = null;
	private JMenuItem subMenuItem = null;
	private JMenuItem popMenuItem = null;
  private JMenu toolMenu_DevScreen = null;
  private static JMenu outsideTool = null;
  private static JMenu outsideTool2 = null;
  private JMenuItem newFileMenuItem= null;
  private JMenuItem deletePrjMenuItem = null;
  private Project project = null;
  private IdeaMainFrame parentFrame;
  private JSplitPane splitPane1 = null;
  private String[][] rowdata = {
    {"","",""}
  };

  JFileChooser fdlg = null;
  DefaultTableModel model;
  JTextPane ResultTextPanel = null;
  SimpleAttributeSet attr1,attr2;
  Document doc;


	public SavePanel(IdeaMainFrame parentFrame) {
    this.parentFrame = parentFrame;
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
	}

  void jbInit() throws Exception {
    this.setLayout(borderLayout1);

    // �e�[�u��
		hstr[0] = parentFrame.getBilingualMsg("0133");
		hstr[1] = parentFrame.getBilingualMsg("0134");
		hstr[2] = parentFrame.getBilingualMsg("0135");

    // ���j���[
    menubar = new JMenuBar();
    menu1 = new JMenu(parentFrame.getBilingualMsg("0001") + "(F)", true);
		menu1.setMnemonic('F');
    menubar.add(menu1);

    ImageIcon Icon2 = getImageIcon("resources/openprj.gif");
    ImageIcon Icon3 = getImageIcon("resources/openagainprj.gif");
    ImageIcon Icon3_2 = getImageIcon("resources/openagainprj2.gif");

    newFileMenuItem = menuItem( parentFrame.getBilingualMsg("0002") + "(N)...","NewFile",getImageIcon("resources/newfile.gif"));
    newFileMenuItem.setMnemonic('N');
    //parentFrame.setMenuItemAccelerator ("new-file", newFileMenuItem );
    menu1.add(newFileMenuItem);

    newProjectMenuItem = menuItem( parentFrame.getBilingualMsg("0003") + "(P)...","NewProject",getImageIcon("resources/newprj.gif"));
    newProjectMenuItem.setMnemonic('P');
    menu1.add(newProjectMenuItem);

    item2 = new JMenuItem(parentFrame.getBilingualMsg("0004") + "(O)...", Icon2);
    item2.setMnemonic('O');
    item2.setHorizontalTextPosition(SwingConstants.RIGHT);
    menu1.add(item2);
    item2.addActionListener(this);
    item2.setActionCommand("openPrjct");
    //parentFrame.setMenuItemAccelerator("open-project",item2);

    openprjMenu = new JMenu(parentFrame.getBilingualMsg("0005") + "(R)");
		openprjMenu.setMnemonic('R');
    openprjMenu.setIcon(Icon3);
    openprjMenu.setEnabled(false);
    menu1.add(openprjMenu);

    deletePrjMenuItem = menuItem(parentFrame.getBilingualMsg("0116"),"DeleteProject",getImageIcon("resources/delfile.gif"));
    deletePrjMenuItem.setEnabled(false);
    menu1.add(deletePrjMenuItem);

    menu1.addSeparator();
    item3 = new JMenuItem(parentFrame.getBilingualMsg("0008") + "(X)");
    item3.setMnemonic('X');
    menu1.add(item3);
    item3.addActionListener(this);
    item3.setActionCommand("exitwindow");

    // �c�[�����j���[
    toolMenu_DevScreen = new JMenu (parentFrame.getBilingualMsg("0021") + "(T)");
    toolMenu_DevScreen.setMnemonic('T');

    // ���j���[�A�C�e���쐬
    toolMenu1 = menuItem(parentFrame.getBilingualMsg("0022") + "(E)...", "SetupEditor", null );
    toolMenu2 = menuItem(parentFrame.getBilingualMsg("0023") + "(K)...", "SetupKeyword", null );
    toolMenu4 = menuItem(parentFrame.getBilingualMsg("0117") + "...", "SetupKeymap", null );
    toolMenu5 = menuItem(parentFrame.getBilingualMsg("0118") + "...", "SimulatorOption", null );

    outsideTool = new JMenu(parentFrame.getBilingualMsg("0024") + "(O)");
    outsideTool.setMnemonic('O');
    outsideTool2 = new JMenu(parentFrame.getBilingualMsg("0024") + "(O)");
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

    toolMenu3 = menuItem( parentFrame.getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3.setMnemonic('C');
    outsideTool.add(toolMenu3);

    if (parentFrame.vecOutsideToolInfo.size() > 0 ) {
      outsideTool.addSeparator();
    }

    for (int i=0; i<parentFrame.vecOutsideToolInfo.size(); i++ ) {
      String wk = (String)parentFrame.vecOutsideToolInfo.elementAt(i) ;

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

    // �c�[���o�[
    toolbar = new JToolBar();
    toolbtn2  = new JButton(Icon2);
    jToggleButton1 = new JToggleButton(Icon3_2);
    jToggleButton1.setEnabled(false);

    toolbtn2.setToolTipText(parentFrame.getBilingualMsg("0004"));
    jToggleButton1.setToolTipText(parentFrame.getBilingualMsg("0005"));

    toolbtn2.addActionListener(this);
    toolbtn2.setActionCommand("openPrjct");

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

    // �v���W�F�N�g�� ���ݖ��g�p
    DirKeyword.setText("");
    DirKeyword.setEditable(false);
    JPanel DirKeywordPanel = new JPanel (new BorderLayout() );
    DirKeywordPanel.add(new JLabel("���|�W�g�����@�F"),BorderLayout.WEST);
    DirKeywordPanel.add(DirKeyword,BorderLayout.CENTER);

    // �f�B���N�g�����A�f�B���N�g�������{�^��
    dirLabel = new JLabel( parentFrame.getBilingualMsg("0044") + "�F" );
    SrchKeyword.setEditable(false);
    SrchKeyword.setBackground(Color.white);
    JPanel SrchKeywordPanel = new JPanel (new BorderLayout() );
    SrchKeywordPanel.add(dirLabel, BorderLayout.WEST);
    SrchKeywordPanel.add(SrchKeyword,BorderLayout.CENTER);
    JButton srchbtn = new JButton("�c");
    srchbtn.addActionListener(this);
    srchbtn.setActionCommand("dirsrch");
    SrchKeywordPanel.add(srchbtn,BorderLayout.EAST);

    JPanel SrchPanelTop = new JPanel (new GridLayout(2,1));
    SrchPanelTop.add(SrchKeywordPanel);
    border1 = BorderFactory.createEmptyBorder(0,155,0,0);
    SrchKeywordPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    DirKeywordPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    SrchPanelTop.setBorder(border1);

    // �ꗗ�\���쐬
    model = new ReadOnlyTableModel();
    searchList = new JTable(model);
    searchList.setRowSelectionAllowed(false);
    searchList.setColumnSelectionAllowed(false);

		//Table�w�b�_
		JTableHeader tableHeader = searchList.getTableHeader();
    tableHeader.setDefaultRenderer(new MyHeaderRenderer());

    JScrollPane scrPane = new JScrollPane();
    scrPane.getViewport().setView(searchList);
    scrPane.setPreferredSize(new Dimension(200, 120));
    scrPane.setBorder(BorderFactory.createEmptyBorder(0,10,20,20));

    JPanel SrchListTop = new JPanel (new BorderLayout());
    titleBdr1 = new TitledBorder(parentFrame.getBilingualMsg("0047"));
    SrchListTop.setBorder(titleBdr1);
    SrchListTop.add(scrPane,BorderLayout.CENTER);

    JPanel ResultPanel = new JPanel( new BorderLayout() );
    ResultTextPanel = new JTextPane();
    ResultTextPanel.setText("");

    // ���ʕ\���Ŏg�p����attribute
    attr1 = new SimpleAttributeSet();
    StyleConstants.setForeground(attr1,Color.blue);
    attr2 = new SimpleAttributeSet();
    StyleConstants.setForeground(attr2,Color.red);

    JScrollPane scrlPane = new JScrollPane(ResultTextPanel);
    titleBdr2 = new TitledBorder(parentFrame.getBilingualMsg("0048"));
    scrlPane.setBorder(titleBdr2);
    ResultPanel.add(scrlPane, BorderLayout.CENTER);

		splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,SrchListTop,ResultPanel);
    //splitPane1.setDividerLocation((int)(splitPane1.getMaximumDividerLocation()) * 6);
    splitPane1.setDividerLocation(parentFrame.getHeight()/10*5);


    // ��荞�݃{�^��
    JPanel getBtnPanel = new JPanel ( new GridLayout(4,1) );

		// ���쐬
		ImageIcon icon10 = getImageIcon("resources/arrowLR.gif");
    JLabel label1 = new JLabel(icon10);
    JPanel Arowpanel = new JPanel();
    Arowpanel.add(label1);

    // ���x���쐬
    cmntlabel = new JLabel("  " + parentFrame.getBilingualMsg("0132") + "  ");

    // �{�^���쐬
		button1 = new JButton(parentFrame.getBilingualMsg("0061"));
    button1.addActionListener(this);
    button1.setActionCommand("selsave");

		button2 = new JButton(parentFrame.getBilingualMsg("0062"));
    button2.addActionListener(this);
    button2.setActionCommand("allsave");

    // �p�l���ɓ����
    getBtnPanel.add(Arowpanel);
    getBtnPanel.add(cmntlabel);
    getBtnPanel.add(button1);
    getBtnPanel.add(button2);

    JPanel panel3 = new JPanel( new BorderLayout() );
    panel3.add(getBtnPanel,BorderLayout.NORTH);

    // ���������p�l���ƈꗗ�p�l���𓯂��p�l���ɓ����
    JPanel p1 = new JPanel (new BorderLayout() ) ;
    p1.add(SrchPanelTop,BorderLayout.NORTH);
    p1.add(splitPane1,BorderLayout.CENTER);
    p1.add(panel3,BorderLayout.WEST);

    this.add(p1,BorderLayout.CENTER);

  }

  public void this_componentResized(ComponentEvent e) {
    splitPane1.setDividerLocation(parentFrame.getHeight()/10*5);
    //splitPane1.setDividerLocation((int)(splitPane1.getMaximumDividerLocation()) * 6);
  }
	/**
	* ImageIcon����
	* @param String path �p�X<BR>
	*/
  private ImageIcon getImageIcon(String path) {
    java.net.URL url = this.getClass().getResource(path);
    return new ImageIcon(url);
  }

	/**
	* MenuItemAccelerator����
	*/
  public void changeMenuItemAccelerator() {
    parentFrame.setMenuItemAccelerator ("new-file", newFileMenuItem );
    parentFrame.setMenuItemAccelerator("open-project",item2);
  }

	/**
	* �C�x���g����
	* @param ActionEvent e
	*/
  public void actionPerformed(ActionEvent e) {
    try{

      String action = e.getActionCommand();

      if (action.equals("dirsrch")){
        //���|�W�g������
        boolean jgchic = createFileChooser();
         if( jgchic ){
           String rpstrypath = fdlg.getSelectedFile().getAbsolutePath();
           ListShow(rpstrypath);
         }
      }
      else if(action.equals("selsave") || action.equals("allsave")){
        //���|�W�g���ɓo�^
        saveRpstry( action );
      }
      else if (action.equals("openPrjct")){
        //�v���W�F�N�g���J��
        parentFrame.openprojectfile();
      }
      else if (action.equals("againPrjct")){
        //�v���W�F�N�g���J������
        if(e.getSource() instanceof JMenuItem){
          JMenuItem menuitem = (JMenuItem)e.getSource();
          parentFrame.readProjectFile(menuitem.getText());
        }

      }
      else if (action.equals("deletehist")) {
        parentFrame.deleteOpenHist();
      }
      else if (action.equals("exitwindow")){
        parentFrame.SystemExit();
      }
      //--------------------------------------------------------------------------
      // �G�f�B�^�ݒ�
      //--------------------------------------------------------------------------
      else if (action.equals("SetupEditor")) {
        parentFrame.SetupEditor();
      }
      //--------------------------------------------------------------------------
      // �L�[���[�h�ݒ�
      //--------------------------------------------------------------------------
      else if (action.equals("SetupKeyword")) {
        parentFrame.SetupKeyword();
      }
      //--------------------------------------------------------------------------
      // �L�[���[�h�}�b�v
      //--------------------------------------------------------------------------
      else if (action.equals("SetupKeymap")) {
        parentFrame.IdeOption();
      }
      //--------------------------------------------------------------------------
      // �O���c�[���\���_�C�A���O�̕\��
      //--------------------------------------------------------------------------
      else if (action.equals("SetOutsideTool")) {
        parentFrame.SetOutsideTool();
      }
      //--------------------------------------------------------------------------
      // �O���c�[���̎��s
      //--------------------------------------------------------------------------
      else if (action.startsWith("OutsideTool_")) {
        parentFrame.ExecOutsideTool (action );
      }
      //--------------------------------------------------------------------------
      // �V�~�����[�^�I�v�V����
      //--------------------------------------------------------------------------
      else if (action.startsWith("SimulatorOption")) {
        parentFrame.SimulatorOption( );
      }
      //--------------------------------------------------------------------------
      // �V�K��Dash�t�@�C���쐬
      //--------------------------------------------------------------------------
      else if (action.equals("NewFile")) {
        if (project == null ) {
          Object[] options = { "OK" };
          JOptionPane.showOptionDialog(this,
              parentFrame.getBilingualMsg("0093"),parentFrame.getBilingualMsg("0129"),
                                     JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                     null, options, options[0]);

          return;
        }
        NewChoice newChoice = new NewChoice(parentFrame,project);
        //newChoice.show();
		    newChoice.setVisible(true);
      }
      //--------------------------------------------------------------------------
      // �V�K�v���W�F�N�g
      //--------------------------------------------------------------------------
      else if (action.equals("NewProject")) {
        NewProject NewPrjWin = new NewProject (parentFrame,null);
        //NewPrjWin.show();
		    NewPrjWin.setVisible(true);
      }
      //--------------------------------------------------------------------------
      // �v���W�F�N�g�̍폜
      //--------------------------------------------------------------------------
      else if (action.equals("DeleteProject") ) {
        parentFrame.deleteProject();
      }

    }
    catch(Exception e2){

    }
  }

  /**
  * �f�B���N�g���I���_�C�A���O
  */
  public boolean createFileChooser() {
    Container cont;
    cont = this.getParent();
    String path = System.getProperty("dash.r.path");
    fdlg = new JFileChooser(path);
    fdlg.setDialogType(JFileChooser.OPEN_DIALOG);
    fdlg.setDialogTitle(parentFrame.getBilingualMsg("0110"));
    fdlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    if(fdlg.showOpenDialog(cont) != JFileChooser.APPROVE_OPTION ){
      return false;
    }

    return true;
  }

  /**
  * JTable�Ƀt�@�C���ꗗ��\��
  */
  private void ListShow(String dirpath){
    //�f�B���N�g���G���A�Ƀp�X��\��
    SrchKeyword.setText( dirpath );

    //�w�肳�ꂽ�f�B���N�g�����ɑ��݂���dash�t�@�C���𒊏o�A�\��
    File f = new File(dirpath);
    String[] FileList = f.list();

    // �\������Ă��郊�X�g�̍폜
    try {
      searchList.clearSelection();
    }
    catch (Exception e ) {}
    int rowcnt = model.getRowCount();
    for( int i=0; i < rowcnt; i++ ){
      model.removeRow(0);
    }

    for (int i=0; i<FileList.length; i++ ) {
      String chkval = FileList[i];

      //�����dash�t�@�C���̂�
      if( chkval.toLowerCase().endsWith(".dash") || chkval.toLowerCase().endsWith(".rset")){

        //�t�@�C���̃T�C�Y���擾
        String chkFilePath = "";
        if (!dirpath.endsWith(File.separator)) {
          chkFilePath = dirpath + File.separator;
        }
        File chkFile = new File(chkFilePath + chkval);
        long fsize = (chkFile.length()/1000);
        double dfsize = new Double(fsize).doubleValue();

        dfsize = Math.round(dfsize*1.0)/1.0;
        String sfsize = new Double(dfsize).toString();
        sfsize = sfsize.substring(0,sfsize.indexOf(".")) + "KB";

        //�t�@�C���̍X�V�������擾
        Date chkdate = new Date(chkFile.lastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("yy / MM / dd - HH : mm");
        String schkdate = sdf.format(chkdate);

        //JTalbe�ɃZ�b�g
        Vector vallist = new Vector();
        vallist.addElement(chkval);   //�t�@�C����
        vallist.addElement(schkdate); //�X�V����
        vallist.addElement(sfsize);   //�T�C�Y
        model.addRow(vallist);

      }
    }

    int rowfinlcnt = model.getRowCount();
    if(rowfinlcnt == 0){
      String[] val = {parentFrame.getBilingualMsg("0094")};
      model.addRow(val);
    }

  }

  /**
  * ���|�W�g���֓o�^
  */
  private boolean saveRpstry( String kind ) throws IOException , java.lang.Exception{

    try {
      Object[] options = { "OK" };

      if(SrchKeyword.getText().equals("")){
        JOptionPane.showOptionDialog(this,
            parentFrame.getBilingualMsg("0111"),parentFrame.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

      //�o�^����t�@�C�������擾
      Vector saveFiles = null;
      if(kind.equals("selsave")){
        saveFiles = parentFrame.getSelectFiles();
      }
      else{
        saveFiles = project.getFileNames();
      }

      if( saveFiles.size() <= 0 ){
        JOptionPane.showOptionDialog(this,
            parentFrame.getBilingualMsg("0112"),parentFrame.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

      //���|�W�g�����̃t�@�C�������擾
      String rpstrypath = SrchKeyword.getText();
      ArrayList extlowerfilelist = new ArrayList();

      File f = new File(rpstrypath);
      String[] FileList = f.list();

      for(int i=0; i < FileList.length; i++){
        //�t�@�C������ toLowerCase() �Ŏ擾
        extlowerfilelist.add(FileList[i].toLowerCase());
      }

      //�t�@�C���R�s�[����
      String OrgFilePath = "";

      OrgFilePath = project.getProjectPath();

      String MakFilePath = "";
      if (!SrchKeyword.getText().endsWith(File.separator)) {
        MakFilePath = SrchKeyword.getText() + File.separator;
      }

      //�m�F�\���̃N���A
      ResultTextPanel.setText("");

      StringBuffer textbf = new StringBuffer();
      doc = ResultTextPanel.getDocument();
      for( int i=0; i < saveFiles.size(); i++ ){
        String objfileName = (String)saveFiles.elementAt(i);

        String orgfile = OrgFilePath + objfileName;
        String makfile = MakFilePath + objfileName;

        if( extlowerfilelist.indexOf( objfileName.toLowerCase() ) != -1 ){

					String cstmStr = parentFrame.getBilingualMsg("0113");
					cstmStr = cstmStr.replaceAll("filename",objfileName);

					Object[] options2 = { parentFrame.getBilingualMsg("0191"),parentFrame.getBilingualMsg("0192"),parentFrame.getBilingualMsg("0126") };

					int jgcnt = JOptionPane.showOptionDialog(this,
											cstmStr,parentFrame.getBilingualMsg("0130"),
											JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
											null, options2, options2[0]);
          if( jgcnt == 1 ){
          	//������
            continue;
          }
          else if(jgcnt == 2 || jgcnt == -1){
            //�����
            return false;
          }
        }

        // �R�s�[�������s
        String[] copyfiles = {orgfile,makfile};
        if( filecopy(copyfiles) ){
          //�R�s�[�����������ꍇ
          ResultTextPanel.setCaretPosition(doc.getLength());
          ResultTextPanel.setParagraphAttributes(attr1,true);

					String cstmStr = parentFrame.getBilingualMsg("0114");
					cstmStr = cstmStr.replaceAll("filename",objfileName);

          doc.insertString(doc.getLength(),cstmStr + "\n",null);
        }
        else{
          //�R�s�[�����s�����ꍇ
          ResultTextPanel.setCaretPosition(doc.getLength());
          ResultTextPanel.setParagraphAttributes(attr2,true);

					String cstmStr = parentFrame.getBilingualMsg("0115");
					cstmStr = cstmStr.replaceAll("filename",objfileName);

          doc.insertString(doc.getLength(),cstmStr + "\n",null);

        }

      }

      //JTable�ĕ\��
      ListShow(rpstrypath);

    } catch ( Exception e ){
      return false;
    }

    return true;
  }


  /**
  * �t�@�C���R�s�[����
	* @param String args[]<BR>
  */
  public static boolean filecopy(String args[]){
    try{

      if (args[0].equals(args[1]) ) {
        return true;
      }
      String  strBuff;
      File fp  = new File ( args[1] );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.close ();

      /* �I�u�W�F�N�g�̐��� */
      BufferedReader  brInFile = new BufferedReader(new FileReader(args[0]));
      PrintWriter     pwOutFile = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));

      /* �ǂݍ��ݏ��� */
      while((strBuff = brInFile.readLine()) != null){
        pwOutFile.println(strBuff);
      }

      /* �N���[�Y���� */
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
	* �J���������j���[�̍X�V
	* @param Vector histlist<BR>
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

        //�T�u���j���[
        openprjMenu.add(menuItem(addname, "againPrjct", null));

        //�|�b�v�A�b�v���j���[
        popup.add(menuItem(addname, "againPrjct", null));

      }

			subMenuItem = new JMenuItem(parentFrame.getBilingualMsg("0036"));
			subMenuItem.setActionCommand("deletehist");
			subMenuItem.addActionListener(this);
			popMenuItem = new JMenuItem(parentFrame.getBilingualMsg("0036"));
			popMenuItem.setActionCommand("deletehist");
			popMenuItem.addActionListener(this);
      openprjMenu.add(subMenuItem);
      popup.add(popMenuItem);

    }

  }

	/**
	* JMenuItem����
	* @param String path �p�X<BR>
	*/
  private JMenuItem menuItem(String label, String command, Icon icon) {
   JMenuItem item = new JMenuItem(label, icon);
   item.setActionCommand(command);
   item.addActionListener(this);
   return item;
	}

	/**
	* jPopupMenu1_popupMenuWillBecomeInvisible
	*/
  void jPopupMenu1_popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    jToggleButton1.setSelected(false);
  }

	/**
	* jToggleButton1_stateChanged
	*/
  void jToggleButton1_stateChanged(ChangeEvent e) {
    try {
      popup.show(toolbar,jToggleButton1.getX(),jToggleButton1.getY()+jToggleButton1.getHeight());
    }
    catch (Exception ee ){}
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
	* �v���W�F�N�g���j���[�X�V
	* @param Project project<BR>
	*/
  public void setProject (Project project ) {
    this.project = project;
    //�v���W�F�N�g�폜���j���[�X�V
    if( project != null ){
      deletePrjMenuItem.setEnabled(true);
    }
    else{
      deletePrjMenuItem.setEnabled(false);
    }
  }

	/**
	* �O���c�[���p���j���[����蒼��
	*/
  public void ReMakeOutsideToolMenu() {

    // �O���c�[���p���j���[����蒼��
    outsideTool.removeAll();
    outsideTool2.removeAll();

    JMenuItem toolMenu3 = menuItem(parentFrame.getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3.setMnemonic('C');
    outsideTool.add(toolMenu3);

    JMenuItem toolMenu3_2 = menuItem(parentFrame.getBilingualMsg("0025") + "(C)...", "SetOutsideTool", null );
    toolMenu3_2.setMnemonic('C');
    outsideTool2.add(toolMenu3_2);


    if (parentFrame.vecOutsideToolInfo.size() > 0 ) {
      outsideTool.addSeparator();
      outsideTool2.addSeparator();
    }

    for (int i=0; i<parentFrame.vecOutsideToolInfo.size(); i++ ) {
      String wk = (String)parentFrame.vecOutsideToolInfo.elementAt(i) ;

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
	* �o�C�����K������
	*/
	public void changeFormLabel() {

    // �f�B���N�g����
		dirLabel.setText(parentFrame.getBilingualMsg("0044") + "�F");

    // �ꗗ�\���쐬
		titleBdr1.setTitle(parentFrame.getBilingualMsg("0047"));
		titleBdr2.setTitle(parentFrame.getBilingualMsg("0048"));

    // ���x���쐬
    cmntlabel.setText("  " + parentFrame.getBilingualMsg("0132") + "  ");

    // �{�^���쐬
    button1.setText(parentFrame.getBilingualMsg("0061"));
		button2.setText(parentFrame.getBilingualMsg("0062"));

	}

	/**
	* �o�C�����K������
	*/
	public void changeMenuText(){

    // ���j���[
		menu1.setText(parentFrame.getBilingualMsg("0001") + "(F)");
		newFileMenuItem.setText(parentFrame.getBilingualMsg("0002") + "(N)...");
		newProjectMenuItem.setText(parentFrame.getBilingualMsg("0003") + "(P)...");
		item2.setText(parentFrame.getBilingualMsg("0004") + "(O)...");
		openprjMenu.setText(parentFrame.getBilingualMsg("0005") + "(R)");
		deletePrjMenuItem.setText(parentFrame.getBilingualMsg("0116"));
		item3.setText(parentFrame.getBilingualMsg("0008") + "(X)");

    // �c�[�����j���[
    toolMenu_DevScreen.setText(parentFrame.getBilingualMsg("0021") + "(T)");

    // ���j���[�A�C�e���쐬
    toolMenu1.setText(parentFrame.getBilingualMsg("0022") + "(E)...");
		toolMenu2.setText(parentFrame.getBilingualMsg("0023") + "(K)...");
		toolMenu4.setText(parentFrame.getBilingualMsg("0117") + "...");
		toolMenu5.setText(parentFrame.getBilingualMsg("0118") + "...");
		outsideTool.setText(parentFrame.getBilingualMsg("0024") + "(O)");
		outsideTool2.setText(parentFrame.getBilingualMsg("0024") + "(O)");
		toolMenu3.setText(parentFrame.getBilingualMsg("0025") + "(C)...");

    // �c�[���o�[
    toolbtn2.setToolTipText(parentFrame.getBilingualMsg("0004"));
    jToggleButton1.setToolTipText(parentFrame.getBilingualMsg("0005"));

		//����
    if( subMenuItem != null ){
			subMenuItem.setText(parentFrame.getBilingualMsg("0036"));
    }

    if( popMenuItem != null ){
			popMenuItem.setText(parentFrame.getBilingualMsg("0036"));
    }

	}

	class MyHeaderRenderer extends JLabel implements TableCellRenderer{

		public Component getTableCellRendererComponent(
			JTable table, Object data, boolean isSelected, boolean hasFocus,
			int row, int column) {

			setOpaque(true); /* �w�i�F��񓧖��ɂ��� */

			this.setHorizontalAlignment(SwingConstants.CENTER);

			/* Border��ύX���� */

			//setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			setBorder(new BevelBorder(BevelBorder.RAISED));

			setBackground(Color.lightGray);
			setForeground(Color.black);

			String ColStr = "";
			if( data.equals("Ag��") || data.equals("Agent Name") ){
				ColStr = parentFrame.getBilingualMsg("0133");
			}
			else if( data.equals("���t") || data.equals("Date") ){
				ColStr = parentFrame.getBilingualMsg("0134");
			}
			else if( data.equals("�T�C�Y") || data.equals("Size") ){
				ColStr = parentFrame.getBilingualMsg("0135");
			}

			setText(ColStr);

			return this;
		}
	}


}

