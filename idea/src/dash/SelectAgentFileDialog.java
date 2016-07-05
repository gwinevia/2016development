package dash;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

/**
 * <p>タイトル:エージェントファイル選択ダイアログ </p>
 * <p>説明:動作シミュレートで、リポジトリ、ワークプレースに読み込むエージェントの選択を行う </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class SelectAgentFileDialog extends EnhancedDialog implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** 未使用 */
  private JCheckBox clearCheckBox = null;

  /** チェックボックスとエージェントファイル名の組み合わせを配置するパネル */
  private JPanel agentList = null;

  /** チェックボックスを格納するベクター */
  Vector checkboxes = new Vector();

  /** 未使用 */
  Vector comboboxes = new Vector();
  //GeneratedListModel listModel;

  /** ボタン */
  JButton okbtn = new JButton("OK");
  JButton cancelbtn = null;
  JButton selectAllBtn = null;
  JButton selectNoneBtn = null;
  private static Dimension HGAP10 = new Dimension(10,1);
  private Project project;
  public int ret = 0;// 1:OK 0:Cancel

  /** 選択されたファイル */
  private Vector vecSelectFile = new Vector();

  /** 以前、本ダイアログが実行された時に選択されたファイル */
  private Vector prevSelectFiles  = null;

  /** 未使用 */
  private Vector prevReadCount  = null;

  /** 未使用 */
  private Vector vecReadCount = new Vector();

  /** 親フレーム */
  private IdeaMainFrame mainframe = null;

  /****************************************************************************
   * コンストラクタ
   * @param frame IdeaMainFrameを受け取る
   * @param title タイトル
   * @param modal モーダル表示の場合true、そうでない時false
   * @param project プロジェクト
   * @param prevSelectFiles 以前、本ダイアログが実行された時に選択されたファイル
   * @param prevReadCount 未使用・・・nullを渡して構わない
   * @return なし
   ****************************************************************************/
  public SelectAgentFileDialog(IdeaMainFrame frame, String title, boolean modal, Project project,Vector prevSelectFiles, Vector prevReadCount) {
    super(frame, title, modal);
    this.project = project;
    this.prevSelectFiles = prevSelectFiles;
    this.prevReadCount = prevReadCount;
    try {
    	mainframe = frame;
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public SelectAgentFileDialog() {
    this(null, "", false, null,null,null);
  }

  /****************************************************************************
   * 初期処理
   * @param なし
   * @return なし
   ****************************************************************************/
  private void jbInit() throws Exception {

	  cancelbtn = new JButton(mainframe.getBilingualMsg("0126"));

    // フレームサイズ
    setSize(300,350);

    // ダイアログを画面中央に配置
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    //this.setSize(new Dimension(400, 300));
    panel1.setLayout(borderLayout1);

    JPanel msgPanel = new JPanel();
    JLabel msgLabel2 = new JLabel (mainframe.getBilingualMsg("0169"));
    msgPanel.add(msgLabel2);
    msgPanel.setBackground(new Color(255,255,217));
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(msgPanel,BorderLayout.NORTH);

    // チェックボックスとラベルの組み合わせを配置するパネルを作成し、ダイアログに配置する
    getContentPane().add(createControlPanel(),BorderLayout.CENTER);
    //getContentPane().add(new JCheckBox("起動前にリポジトリのAgを消去する"),BorderLayout.SOUTH);


		// リスト表示するファイルをベクターに格納
		Vector vecFileName = new Vector();
    for (int i=0; i<project.getFileCount(); i++ ) {
      if (!((String)project.getFileName(i)).toLowerCase().endsWith(".dash") ) {
        continue;
      }
      vecFileName.addElement((String)project.getFileName(i));
    }
    
    // ベクターに入れたファイル名をソートする
    for(int sfrom=0; sfrom<vecFileName.size()-1; sfrom++) {
      int max = sfrom;
      for(int i=sfrom+1; i<vecFileName.size(); i++) {
          if(((String)vecFileName.elementAt(i)).compareTo((String)vecFileName.elementAt(max))<0) max = i;
      }
      String temp = (String)vecFileName.elementAt(sfrom);
      vecFileName.setElementAt((String)vecFileName.elementAt(max),sfrom);
      vecFileName.setElementAt(temp,max);
     }    

    // ファイル名を、チェックボックスとの組み合わせでパネルに追加する
    for (int i=0; i<vecFileName.size(); i++ ) {
      boolean chkflag = false;
      String ReadCount = null;
      if (prevReadCount != null ) {
        ReadCount = "1";
      }
      
      String FileName = (String)vecFileName.elementAt(i);
      //if (((String)project.getFileName(i)).toLowerCase().endsWith(".rset") ) {
      //  continue;
      //}
      if (!FileName.toLowerCase().endsWith(".dash") ) {
        continue;
      }
      if (prevSelectFiles.indexOf(FileName) != -1 ) {
        chkflag = true;
        if (prevReadCount != null ) {
          ReadCount = (String)prevReadCount.elementAt(i);
        }
      }
      addPrefix (FileName,chkflag,ReadCount);
    }
    /*
    for (int i=0; i<project.getFileCount(); i++ ) {
      boolean chkflag = false;
      String ReadCount = null;
      if (prevReadCount != null ) {
        ReadCount = "1";
      }
      //if (((String)project.getFileName(i)).toLowerCase().endsWith(".rset") ) {
      //  continue;
      //}
      if (!((String)project.getFileName(i)).toLowerCase().endsWith(".dash") ) {
        continue;
      }
      if (prevSelectFiles.indexOf((String)project.getFileName(i)) != -1 ) {
        chkflag = true;
        if (prevReadCount != null ) {
          ReadCount = (String)prevReadCount.elementAt(i);
        }
      }
      addPrefix (project.getFileName(i),chkflag,ReadCount);
    }
    */

    JPanel btnpanel = new JPanel (new GridLayout(1,2));
    btnpanel.add(okbtn);
    btnpanel.add(cancelbtn);

    okbtn.setActionCommand("OK");
    cancelbtn.setActionCommand("CANCEL");
    okbtn.addActionListener(this);
    cancelbtn.addActionListener(this);

    JPanel btnpanel2 = new JPanel (new BorderLayout());
    btnpanel2.add(btnpanel,BorderLayout.EAST);
    getContentPane().add(btnpanel2,BorderLayout.SOUTH);

  }

  /****************************************************************************
   * チェックボックスとラベルの組み合わせを配置するパネルを作成
   * @param なし
   * @return チェックボックスとラベルの組み合わせを配置するパネル
   ****************************************************************************/
  public JPanel createControlPanel() {
    JPanel controlPanel = new JPanel() {
      Insets insets = new Insets(4, 4, 4, 4);
      public Insets getInsets() {
        return insets;
      }
    };
    controlPanel.setLayout(new BorderLayout());

    JPanel agentPanel = new JPanel();
    agentPanel.setLayout(new BoxLayout(agentPanel, BoxLayout.Y_AXIS));

    agentList = new JPanel() {
        Insets insets = new Insets(0, 4, 0, 0);
        public Insets getInsets() {
          return insets;
        }
    };
    agentList.setLayout(new BoxLayout(agentList, BoxLayout.Y_AXIS));
    //agentList.setLayout(new GridLayout(project.getFileCount(),2) );
    agentList.setBackground(Color.white);

    JScrollPane scrollPane = new JScrollPane(agentList);
    scrollPane.getVerticalScrollBar().setUnitIncrement(10);
    agentPanel.add(scrollPane);
    controlPanel.add(agentPanel, BorderLayout.CENTER);
    if (prevReadCount == null ) {
      clearCheckBox = new JCheckBox("起動前にリポジトリのAgを消去する");
    }
    else {
      clearCheckBox = new JCheckBox("起動前にワークプレースのAgを消去する");
    }
    //controlPanel.add(clearCheckBox, BorderLayout.SOUTH);

    JPanel selPanel = new JPanel();
    selectAllBtn = new JButton(mainframe.getBilingualMsg("0225"));
    selectNoneBtn = new JButton(mainframe.getBilingualMsg("0226"));

    selectAllBtn.setActionCommand("selectAll");
    selectNoneBtn.setActionCommand("selectNone");

    selectAllBtn.addActionListener(this);
    selectNoneBtn.addActionListener(this);

    selPanel.add(selectAllBtn);
    selPanel.add(selectNoneBtn);
    controlPanel.add(selPanel, BorderLayout.NORTH);
    return controlPanel;
  }

  /****************************************************************************
   * チェックボックスとラベルの組み合わせを作成し、agentListに配置する
   * @param prefix ラベルに表示する文字列
   * @param selected チェックボックスを選択状態にする場合はtrue、そうでないときはfalse
   * @return なし
   ****************************************************************************/
  public void addPrefix(String prefix, boolean selected, String ReadCount) {
    //if(prefixAction == null) {
    //  prefixAction = new UpdatePrefixListAction(listModel);
    //}

    JPanel wkpanel1 = new JPanel (new BorderLayout());
    JPanel wkpanel2 = new JPanel (new BorderLayout());
    Vector vecCount = new Vector();
    for (int i=1; i<=10; i++ ) {
      vecCount.addElement(new Integer(i).toString());
    }
    JCheckBox cb = null;
    if (ReadCount != null ) {
      JLabel lbl = new JLabel("個  ");
      lbl.setBackground(Color.white);
      wkpanel2.add(lbl, BorderLayout.EAST);
      JComboBox cmb = (JComboBox)wkpanel2.add(new JComboBox(vecCount));
      wkpanel2.setBackground(Color.white);
      cb = (JCheckBox) wkpanel1.add(new JCheckBox(prefix));

      wkpanel1.add(wkpanel2,BorderLayout.EAST);
      cmb.setSelectedItem(ReadCount);
      agentList.add(wkpanel1);
      comboboxes.addElement(cmb);
    }
    else {
      cb = (JCheckBox) agentList.add(new JCheckBox(prefix));
    }
    checkboxes.addElement(cb);
    cb.setSelected(selected);
    cb.setBackground(Color.white);


    //agentList.add(new JComboBox());
    //agentList.add(new Label("AAA"));
    //cb.addActionListener(prefixAction);
    //if(selected) {
    //  listModel.addPrefix(prefix);
    //}
  }

  /****************************************************************************
   * イベント処理
   * @param e アクションイベント
   * @return なし
   ****************************************************************************/
  public void actionPerformed(ActionEvent e) /*throws java.io.IOException, java.lang.Exception*/{

    try{

      String action = e.getActionCommand();

      if (action.equals("OK")){
        ok();
      }
      else if (action.equals("CANCEL")){
        cancel();
      }
      else if (action.equals("selectAll")){
        for (int i=0; i<checkboxes.size(); i++ ) {
          JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);
          bc.setSelected(true);
        }
      }
      else if (action.equals("selectNone")){
        for (int i=0; i<checkboxes.size(); i++ ) {
          JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);
          bc.setSelected(false);
        }
      }

    }
    catch(Exception e2){

    }

  }

  /****************************************************************************
   * 結果取得
   * @param なし
   * @return OKクリック時：true　キャンセルクリック時：false
   ****************************************************************************/
  public int getResult() {
    return ret;
  }

  /****************************************************************************
   * 選択されたファイルを取得
   * @param なし
   * @return 選択されたファイル
   ****************************************************************************/
  public Vector getSelectFiles() {
    return vecSelectFile;
  }

  /****************************************************************************
   * 未使用
   ****************************************************************************/
  public Vector getReadCount() {
    return vecReadCount;
  }

  /****************************************************************************
   * OK処理
   * @param なし
   * @return なし
   ****************************************************************************/
  public void ok() {
    vecSelectFile.clear();
    vecReadCount.clear();
    for (int i=0; i<checkboxes.size(); i++ ) {
      JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);

      if (bc.isSelected() ) {
        //String s = (String)bc.getText();
        vecSelectFile.addElement((String)bc.getText());
        if (prevReadCount != null ) {
          JComboBox cmb = (JComboBox)comboboxes.elementAt(i);
          vecReadCount.addElement((String)cmb.getSelectedItem());
        }
      }
    }

    if (vecSelectFile.size() == 0 ) {

			Object[] options = { "OK" };
			JOptionPane.showOptionDialog(this,
										mainframe.getBilingualMsg("0075"),mainframe.getBilingualMsg("0129"),
										JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
										null, options, options[0]);

//      JOptionPane.showMessageDialog(this,mainframe.getBilingualMsg("0075"));
      return;
    }
    ret = 1;
    //hide();
    dispose();

  }

  /****************************************************************************
   * Cancel処理
   * @param なし
   * @return なし
   ****************************************************************************/
  public void cancel(){
    //キャンセルを押された場合
    ret = 0;
    //hide();
    dispose();

  }

}