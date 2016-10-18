package dash;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.rmi.*;

/** ネームサーバを観察するビューア */
public class NSViewer extends JFrame implements ActionListener, ListSelectionListener {

  /** エージェントのモデル */
  NSModel model;

  /** エージェントのテーブル */
  JTable table;

  /** ネームサーバのエージェントデータ */
  Vector agentData;

  /** 環境のモデル */
  EnvModel envmodel;

  /** 環境のテーブル */
  JTable envtable;

  /** ネームサーバの環境データ */
  Vector envData;

  /** ネームサーバであるリモートオブジェクト */
  NSInterface nameserver;
  String servername;

  /** テキスト */
  JTextArea textArea;

  /** 最期に選択されたエージェント名 */
  String lastName;

  /** unregisterEnvボタン */
  JButton unregButton;

  /** コンストラクタ */
  public NSViewer(String servername) {
    super(servername);

    this.servername = servername;

    // ボタンのパネル
    JButton button = new JButton("Update");
    button.addActionListener(this);
    unregButton = new JButton("UnregisterEnv");
    unregButton.addActionListener(this);
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(button);
    buttonPanel.add(unregButton);

    // テキストエリア
    textArea = new JTextArea();

    // データ取得
    updateAgentData();

    // エージェントのテーブル
    model = new NSModel();
    table = new JTable(model);
    table.createDefaultColumnsFromModel();

    // 環境のテーブル
    envmodel = new EnvModel();
    envtable = new JTable(envmodel);
    table.createDefaultColumnsFromModel();

    // コラムの幅設定
    setColumnWidth();

    // セレクト
    ListSelectionModel selectionModel = table.getSelectionModel();
    selectionModel.addListSelectionListener(this);

    // 張り付け
    Container container = this.getContentPane();
    container.add(BorderLayout.NORTH, buttonPanel);
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    tabbedPane.addTab("agent", new JScrollPane(table));
    tabbedPane.addTab("environment", new JScrollPane(envtable));
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                                      //new JScrollPane(table),
                                      tabbedPane,
                                      new JScrollPane(textArea));
    split.setDividerLocation(200);
    container.add(split);

    addWindowListener(new WindowEventHandler());

    setSize(400, 400);
    //show();
    setVisible(true);
  }

  /**
   * ネームサーバにエージェントと環境の情報を問い合わせる
   */
  private void updateAgentData() {
    try {
      nameserver=(NSInterface)Naming.lookup("rmi://"+servername+"/nameserver");
      agentData = nameserver.getAgentData();
      envData = nameserver.getEnvData();
      lastName = "";
      textArea.setText("");

      if (model != null && envmodel != null) {
        model.fireTableStructureChanged();
        envmodel.fireTableStructureChanged();
        setColumnWidth();
      }

    } catch (Exception e) {
      System.err.println("e "+e);
    }
  }

  private void setColumnWidth() {
    // エージェント表の#
    DefaultTableColumnModel cmodel =
      (DefaultTableColumnModel)table.getColumnModel();
    TableColumn col = cmodel.getColumn(0);
    col.setMinWidth(40);
    col.setMaxWidth(40);

    // 環境表の#と種別
    cmodel = (DefaultTableColumnModel)envtable.getColumnModel();
    col = cmodel.getColumn(0);
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col = cmodel.getColumn(1);
    col.setMinWidth(40);
    col.setMaxWidth(40);
  }

  public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    if (command.equals("Update")) {
      updateAgentData();
    } else if (command.equals("UnregisterEnv")) {
      int index = envtable.getSelectedRow();
      if (index>-1) {
        String[] data = (String[])envData.elementAt(index);
        String envname = data[0];
        int ans = JOptionPane.showConfirmDialog(this,
                                                envname+"を削除する？", "確認",
                                                JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
          try {
            nameserver.unregisterEnv(envname);
            updateAgentData();
          } catch (Exception e) { e.printStackTrace(); }
        }
      }
    }
  }

  public void valueChanged(ListSelectionEvent event) {
    int [] selectedRow = table.getSelectedRows();
    if (selectedRow.length == 1) {
      String name =
        (String)table.getValueAt(selectedRow[0], 1); // エージェント名
      if (!lastName.equals(name)) {
        lastName = name;
        String[] data = searchData(name);
        if (data != null)
          textArea.setText("name: "+data[0]+
                           "\nrname: "+data[1]+
                           "\nbirthday: "+data[2]+
                           "\nbirthplace: "+data[3]+
                           "\nenvironment: "+data[4]+
                           "\nfunction: "+data[5]+
                           "\ncomment: "+data[6]+
                           "\norigin: "+data[7]+
                           "\ntype: "+data[8]);
      }
    }
  }

  private String[] searchData(String name) {
    for (Enumeration e = agentData.elements(); e.hasMoreElements(); ) {
      String[] data = (String[])e.nextElement();
      if (data[0].equals(name))
        return data;
    }
    return null;
  }

  /** ウィンドウ閉じる処理 */
  class WindowEventHandler extends WindowAdapter {
    public void windowClosing(WindowEvent evt) {
      System.exit(0);
    }
  }


  /** エージェントテーブルモデル */
  class NSModel extends AbstractTableModel {

    /** コンストラクタ */
    public NSModel() {
    }

    /** テーブルの行数を取り出す */
    public int getRowCount() {
      return agentData.size();
    }

    /** テーブルの桁数を取り出す */
    public int getColumnCount() {
      return 4;
    }

    /** 指定の行/桁位置の値を取り出す */
    public Object getValueAt(int row, int column) {


      String[] data = (String[])agentData.elementAt(row);
      switch (column) {
      case 0:
        return new Integer(row);
      case 1:
        return data[0]; // 完全なエージェント名
      case 2:
        return data[4]; // 現在居る環境名
      case 3:
        return data[5]; // 機能名
      default:
        return "";
      }
    }

    /** ヘッダ */
    public String getColumnName(int column) {
      switch (column) {
      case 0: return "#";
      case 1: return "完全なエージェント名";
      case 2: return "環境名";
      case 3: return "機能名";
      default: return "";
      }
    }
  }

  /** 環境テーブルモデル */
  class EnvModel extends AbstractTableModel {

    /** コンストラクタ */
    public EnvModel() {
    }

    /** テーブルの行数を取り出す */
    public int getRowCount() {
      return envData.size();
    }

    /** テーブルの桁数を取り出す */
    public int getColumnCount() {
      return 4;
    }

    /** 指定の行/桁位置の値を取り出す */
    public Object getValueAt(int row, int column) {
      String[] data = (String[])envData.elementAt(row);
      switch (column) {
      case 0:
        return new Integer(row);
      case 1:
        return data[1]; // 種別
      case 2:
        return data[0]; // 環境名
      case 3:
        return data[2]; // コメント
      default:
        return "";
      }
    }

    /** ヘッダ */
    public String getColumnName(int column) {
      switch (column) {
      case 0: return "#";
      case 1: return "種別";
      case 2: return "環境名";
      case 3: return "コメント";
      default: return "";
      }
    }
  }

  /** メイン */
  public static void main(String args[]) {
    DashDefaults dashDefaults = new DashDefaults();
    dashDefaults.loadDefaults();

    String server = null;
    switch (args.length) {
    case 0:
      server = System.getProperty("dash.nameserver");
      break;
    case 1:
      server = args[0];
      break;
    default:
      break;
    }
    if (server == null) {
      System.err.println("ネームサーバが指定されていません。"+
                         "defaults.txtにdash.nameserverを指定するか、"+
                         "起動時の第一引数に指定するかしてください。");
      System.exit(1);
    }
      
    new NSViewer(server);
  }
}
