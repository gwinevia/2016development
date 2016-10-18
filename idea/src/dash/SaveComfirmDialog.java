package dash;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

/**
 * <p>タイトル:ファイル保存確認ダイアログ </p>
 * <p>説明:IDEA終了時に、未保存のファイルがあった場合、表示される </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class SaveComfirmDialog extends EnhancedDialog implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** 変更のあったファイル */
  private Vector vecChgFileList = null;

  /** 保存するファイル */
  private Vector vecSelectFile = new Vector();

  /** 保存対象のファイルを選択する際のチェックボックス */
  private JCheckBox clearCheckBox = null;

  /** 変更ファイルを表示するパネル */
  private JPanel agentList = null;

  /** 保存対象のファイルを選択する際のチェックボックスを格納するベクター */
  Vector checkboxes = new Vector();

  /** ボタン */
  JButton okbtn = new JButton("OK");
  JButton cancelbtn = null;
  JButton AllSelectbtn = null;
  JButton AllResetbtn = null;

  private static Dimension HGAP10 = new Dimension(10,1);
  public int ret = 0;// 1:OK 0:Cancel

  /** 親フレーム*/
  private IdeaMainFrame mainframe = null;

  /****************************************************************************
   * コンストラクタ
   * @param frame IdeaMainFrameを受け取る
   * @param vecChgFileList  変更のあったファイル
   * @return なし
   ****************************************************************************/
  public SaveComfirmDialog(IdeaMainFrame frame, Vector vecChgFileList) {
    super(frame, "ファイル保存", true);
    try {
      this.vecChgFileList = vecChgFileList;
      mainframe = frame;
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public SaveComfirmDialog() {
    this(null, null);
  }


  /****************************************************************************
   * 初期処理
   * @param なし
   * @return なし
   ****************************************************************************/
  private void jbInit() throws Exception {

    // ダイアログタイトル設定
    setTitle(mainframe.getBilingualMsg("0166"));

    // ボタンの文字列設定
	  cancelbtn    = new JButton(mainframe.getBilingualMsg("0126"));
  	AllSelectbtn = new JButton(mainframe.getBilingualMsg("0015"));
  	AllResetbtn  = new JButton(mainframe.getBilingualMsg("0168"));

    // フレームサイズ
    setSize(400,350);

    // 本ダイアログを画面中央に来るように位置調整
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    panel1.setLayout(borderLayout1);

    JPanel msgPanel = new JPanel();
    JLabel msgLabel2 = new JLabel (mainframe.getBilingualMsg("0167"));
    msgPanel.add(msgLabel2);
    msgPanel.setBackground(new Color(255,255,217));
    getContentPane().setLayout(new BorderLayout());

    // チェックボックスとラベルの組み合わせを配置するパネルを作成し、ダイアログに配置する
    getContentPane().add(createControlPanel(),BorderLayout.CENTER);
    //getContentPane().add(new JCheckBox("起動前にリポジトリのAgを消去する"),BorderLayout.SOUTH);

    // 変更ファイルを、チェックボックスとの組み合わせでパネルに追加する
    for (int i=0; i<vecChgFileList.size(); i++ ) {
      boolean chkflag = true;
      addPrefix ((String)vecChgFileList.elementAt(i),chkflag);
    }

    JPanel btnpanel1 = new JPanel (new GridLayout(1,2));
    btnpanel1.add(okbtn);
    btnpanel1.add(cancelbtn);

    JPanel btnpanel2 = new JPanel (new GridLayout(1,2));
    btnpanel2.add(AllSelectbtn);
    btnpanel2.add(AllResetbtn);

    // ボタンを配置
    okbtn.setActionCommand("OK");
    cancelbtn.setActionCommand("CANCEL");
    AllSelectbtn.setActionCommand("ALLSELECT");
    AllResetbtn.setActionCommand("ALLRESET");

    okbtn.addActionListener(this);
    cancelbtn.addActionListener(this);
    AllSelectbtn.addActionListener(this);
    AllResetbtn.addActionListener(this);

    JPanel btnpanel3 = new JPanel (new BorderLayout());
    JPanel dammypanel = new JPanel ();
    btnpanel3.add(btnpanel1,BorderLayout.EAST);
    btnpanel3.add(btnpanel2,BorderLayout.WEST);
    btnpanel3.add(dammypanel,BorderLayout.CENTER);
    getContentPane().add(btnpanel3,BorderLayout.SOUTH);
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
    return controlPanel;
  }

  /****************************************************************************
   * チェックボックスとラベルの組み合わせを作成し、agentListに配置する
   * @param prefix ラベルに表示する文字列
   * @param selected チェックボックスを選択状態にする場合はtrue、そうでないときはfalse
   * @return なし
   ****************************************************************************/
  public void addPrefix(String prefix, boolean selected) {

    JPanel wkpanel1 = new JPanel (new BorderLayout());
    JPanel wkpanel2 = new JPanel (new BorderLayout());
    JCheckBox cb = null;
    cb = (JCheckBox) agentList.add(new JCheckBox(prefix));
    checkboxes.addElement(cb);
    cb.setSelected(selected);
    cb.setBackground(Color.white);


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
      else if (action.equals("ALLSELECT")){
        for (int i=0; i<checkboxes.size(); i++ ) {
          JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);
          bc.setSelected(true);
        }
      }
      else if (action.equals("ALLRESET")){
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
   * OK処理
   * @param なし
   * @return なし
   ****************************************************************************/
  public void ok() {
    vecSelectFile.clear();
   for (int i=0; i<checkboxes.size(); i++ ) {
     JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);

     if (bc.isSelected() ) {
       vecSelectFile.addElement((String)bc.getText());
     }
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
  public void cancel() {
    ret = 0;
    //hide();
    dispose();

  }

  /****************************************************************************
   * 保存対象として選択されたファイルを返す
   * @param なし
   * @return 保存対象として選択されたファイル
   ****************************************************************************/
  public Vector getSelectFiles() {
    return vecSelectFile;
  }

}