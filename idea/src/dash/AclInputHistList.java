package dash;
   
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * <p>ACLエディター入力値の履歴表示 </p>
 * <p>
 * 過去、ACLエディターに入力されたデータをダイアログで一覧表示し、その中の任意のデータを選択<br>
 * することにより、ACLデータへの入力作業の省略を目的としたクラスです。<br>
 * 本クラスは、AclPanel.classより呼ばれます。
 * </p>
 * @author cosmosweb nakagawa
 * 
 * @version 1.0
 * test by uchiya test
 */

public class AclInputHistList extends EnhancedDialog  implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** [OK]が押された時、値は１、[キャンセル]が押された時は、値は0 */
  /** 本クラス呼び出し側は、getResult()メソッドを通じて、この値を得ることが出来る*/
  private int ret = 0;

  /** 選択されたデータを格納 */
  /** 本クラス呼び出し側は、getSelStr()メソッドを通じて、この値を得ることが出来る*/
  private String selStr = "";

  /** 履歴データを表示するJTable */
  private JTable table = null;

  /** 履歴データ表示JTableのヘッダー文字列 */
  private String[] hstr = {"以下のリストから選択して下さい。"/*, "機能名", "設計者", "設計日", "ﾊﾞｰｼﾞｮﾝ"*/};

  /** テーブルモデル */
  DefaultTableModel model;

  /** 本クラスはダイアログであるため、親フレームが必要。IdeaMainFrameを親フレームとしている */
  private IdeaMainFrame mainframe = null;

  /****************************************************************************
   * コンストラクタ
   * @param frame IdeaMainFrameを受け取る
   * @param kind  データ種類を表す文字列 ":performative" or ":to" or ":content"
   * @return なし
   ****************************************************************************/
  public AclInputHistList(IdeaMainFrame frame, String kind) {
    super(frame, kind, true);
    try {
    	mainframe = frame;
      jbInit(kind);
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public AclInputHistList() {
    this(null, "");
  }
  private void jbInit(String kind) throws Exception {

    // 過去履歴を取得
    ArrayList vecList = getACLHist(kind );

    // 改行を含む文字列をJTableに表示する場合があり、その時の行の高さ
    int RowHeight = 0;

    // JTableのヘッダー文字列を取得
		hstr[0] = mainframe.getBilingualMsg("0157");

    // フレームサイズ
    setSize(450,450);

    // 表示位置の調整。画面中央に表示する
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    //----------------------------------------------------------------
    // JTableの作成
    //----------------------------------------------------------------
    // テーブルモデルを作成
    model = new ReadOnlyTableModel();
    table = new JTable(model);
    // 複数行の同時選択を不可にする
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // JTableの行の高さの規定値を取得
    RowHeight = table.getRowHeight();
    // Columnのレンダリングを設定
    table.setDefaultRenderer( Object.class, new MyCellRenderer() );
    // 履歴データをテーブルに追加します
    for (int i=0; i<vecList.size(); i++) {
      String[] val = {(String)vecList.get(i)};
      model.addRow(val);
    }

    if (vecList.size() == 0 ) {
      //履歴データが存在しない場合

      // 履歴データが存在しない場合の、メッセージを取得
      String cstmStr = mainframe.getBilingualMsg("0158");
      cstmStr = cstmStr.replaceAll("title",kind);
      String[] val = {cstmStr/*"過去に入力された「" + title + "」はありません。"*/};
      model.addRow(val);
    }

    // JTableをフレームに配置する
    panel1.setLayout(borderLayout1);
    getContentPane().add(panel1);
    JScrollPane spane = new JScrollPane (table);
    panel1.add(spane,BorderLayout.CENTER);

    // JTbaleが格納されている、JScrollPaneにタイトルボーダーを設定する
		String cstmStr2 = mainframe.getBilingualMsg("0159");
		cstmStr2 = cstmStr2.replaceAll("title",kind);
    spane.setBorder(new TitledBorder(cstmStr2/*"過去に入力された「" + title + "」"*/));

    // [OK][キャンセル]ボタンをフレームに追加
    JButton okbtn = new JButton("OK");
    JButton cancelbtn = new JButton(mainframe.getBilingualMsg("0126"));
    JPanel btnpanel = new JPanel (new GridLayout(1,2));
    btnpanel.add(okbtn);
    btnpanel.add(cancelbtn);
    okbtn.setActionCommand("OK");
    cancelbtn.setActionCommand("CANCEL");
    okbtn.addActionListener(this);
    cancelbtn.addActionListener(this);
    JPanel panel2 = new JPanel (new BorderLayout());
    panel2.add(btnpanel,BorderLayout.EAST);
    panel1.add(panel2,BorderLayout.SOUTH);

    // JTableの各行の高さを調整
    // （データに含まれる改行の数＋１）×　行の高さの規定値
    for (int i=0; i<vecList.size(); i++) {
      String s = (String)vecList.get(i);
      char[] c = s.toCharArray();
      int cnt = 0;
      for (int j=0; j<c.length; j++) {
        if (c[j] == '\n' ) {
          cnt ++;
        }
      }
      table.setRowHeight(i,RowHeight*(cnt+1));
    }


    // JTableにマウスイベントを追加
    MouseListener mouseListener = new MouseAdapter() {
      /** Linux用 */
      public void mousePressed(MouseEvent e) {
      }

      /** Windows用 */
      public void mouseReleased(MouseEvent e) {
      }

      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int row = table.getSelectedRow();
          if (row == -1 ) {
            return;
          }
          ret = 1;
          selStr = (String)table.getValueAt(row,0);
          //hide();
          dispose();
        }
      }
    };
    table.addMouseListener(mouseListener);

  }

  /****************************************************************************
   * 読込専用テーブルモデル
   ****************************************************************************/
  class ReadOnlyTableModel extends DefaultTableModel {

    public int getColumnCount(){
      return 1;
    }

    public String getColumnName(int column){
      return hstr[column];
    }

    public boolean isCellEditable(int rowIndex,int colmunIndex) {
      return false;
    }
  }

  /****************************************************************************
   * セルレンダリングクラス
   ****************************************************************************/
  class MyCellRenderer extends JTextArea implements TableCellRenderer {
     MyCellRenderer() {
         super();
         setEditable( false );
     }
     public Component getTableCellRendererComponent(
                             JTable table, Object data,
                             boolean isSelected, boolean hasFocus,
                             int row, int column) {
         setText( (String)data );


         if (isSelected ) {
           this.setForeground(table.getSelectionForeground());
           this.setBackground(table.getSelectionBackground());
         }
         else {
           this.setForeground(table.getForeground());
           this.setBackground(table.getBackground());
         }
        return this;
     }
 }

 /****************************************************************************
  * イベント処理
  * @param e　アクションイベント
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
    }
    catch(Exception e2){
			e2.printStackTrace();
    }
  }

  /****************************************************************************
   * 結果取得
   * @param なし
   * @return [OK]クリック時は１、[キャンセル]クリック時は０
   ****************************************************************************/
  public int getResult() {
    return ret;
  }

  /****************************************************************************
   * 選択されたデータ取得
   * @param なし
   * @return 選択されたデータ
   ****************************************************************************/
  public String getSelStr() {
    return selStr;
  }

  /****************************************************************************
   * OK
   * @param なし
   * @return なし
   ****************************************************************************/
  public void ok() {

    int row = table.getSelectedRow();
    if (row != -1 ) {
      selStr = (String)table.getValueAt(row,0);
      ret = 1;
      //hide();
      dispose();
    }
    else {
      //JOptionPane.showMessageDialog(this,"選択されていません。");
      Object[] options = { "OK" };
      JOptionPane.showOptionDialog(null,
          mainframe.getBilingualMsg("0075"),mainframe.getBilingualMsg("0129"),
                                 JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                 null, options, options[0]);
    }

  }

  /****************************************************************************
   * キャンセル
   * @param なし
   * @return なし
   ****************************************************************************/
  public void cancel() {
    //キャンセルを押された場合
    ret = 0;
    //hide();
    dispose();
  }

  /****************************************************************************
   * 過去履歴データの取得
   * @param kind  データ種類を表す文字列 ":performative" or ":to" or ":content"
   * @return 過去履歴を格納したVector
   ****************************************************************************/
  private ArrayList getACLHist(String kind) {
    // デフォルトファイルの読み込み
    DashDefaults dashDefaults = new DashDefaults();
    dashDefaults.loadDefaults();
    //File msgfile = dashDefaults.getMessageFile();
    File dashdir = dashDefaults.getDashdir();

    //String dirpath = "";
    StringBuffer dirpath= new StringBuffer();
    if (!dashdir.toString().endsWith(File.separator)) {
        //dirpath = dashdir.toString() + File.separator;
        dirpath.append(dashdir.toString());
        dirpath.append(File.separator);
    }
    
    //String FilePath = dirpath;
	    StringBuffer filePath = dirpath;
    int kind_num = 0;
    if (kind.equals(":performative")) {
      kind_num = 1;
    }
    else if (kind.equals(":to")) {
      kind_num = 2;
    }
    else if (kind.equals(":content")) {
      kind_num = 3;
    }

    if (kind_num == 1 ) {
      //FilePath += "properties" + File.separator +"perfomativeHist";
	    filePath.append("properties");
	    filePath.append(File.separator);
	    filePath.append("perfomativeHist");
    }
    else if (kind_num == 2 ) {
      //FilePath += "properties" + File.separator +"toHist";
	  filePath.append("properties");
	  filePath.append(File.separator);
	  filePath.append("toHist");

    }
    else if (kind_num == 3 ) {
      //filePath += "properties" + File.separator +"contentHist";
	      filePath.append("properties");
	      filePath.append(File.separator);
	      filePath.append("contentHist");
		  
    }
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    //読み込み処理
    //	vector --> arraylist 05/02/17
    ArrayList vecList = new ArrayList();
    String content = "";
    try {
        //f_in = new FileReader(FilePath);
        //b_in = new BufferedReader(f_in);
        b_in = new BufferedReader(new InputStreamReader(
                                                new FileInputStream(filePath.toString()),
                                                "JISAutoDetect"));

        while((sLine = b_in.readLine()) != null) {
          if (kind_num != 3 ) {
            if (vecList.size()<30 ) {
              vecList.add(sLine);
            }
          }
          else {
            if (sLine.equals("----------")) {
              vecList.add(content);
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
        if (kind_num == 3 ) {
          if (!content.equals("")) {
            vecList.add(content);
          }

        }
        b_in.close();
        //f_in.close();

    } catch(Exception ex) {
    	ex.printStackTrace();
    }

    return vecList;
  }


}