package dash;

import java.awt.*;
import javax.swing.*;
import java.util.*;

import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;

import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import com.sun.xml.tree.*;


/**
 * <p>タイトル:IDEオプション設定ダイアログ </p>
 * <p>説明:使用言語、キーマッピング設定を行う </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class IdeOption extends EnhancedDialog implements ActionListener,ListSelectionListener {

  /** 親フレーム */
  private IdeaMainFrame mainframe = null;

  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** キーバインディング一覧を表示するテーブル */
  private JTable KeyBindList = null;

  /** キーバインディング一覧を表示するテーブルのヘッダー */
  private String[] hstr = {"アクション", "キーストローク"};

  /** テーブルモデル */
  DefaultTableModel model;

  /** キーバインディングのアクションの説明を表示するテキストエリア */
  private JTextArea commentTextArea = null;

  /** キーバインディング編集ボタン */
  private JButton editBtn = new JButton ("編集");
  private JButton defaultBtn = new JButton ("規定値に戻す");

  /** 使用言語選択コンボボックス */
  private JComboBox cmbLang = null;

  /** 使用漢字コード選択コンボボックス */
  private JComboBox cmbKanjiCode = null;

  /** 結果・・・・[OK]クリック時は１[キャンセル]クリック時は０ */
  public boolean ret = false;

  /** リポジトリ・ワークプレースの表示位置のチェック */
  private JCheckBox cbRepWpIndiPos = new JCheckBox();


  /****************************************************************************
   * コンストラクタ
   * @param frame IdeaMainFrameを受け取る
   * @param title ダイアログのタイトル
   * @param modal モーダルで表示する場合、true。そうでない時、false
   * @return なし
   ****************************************************************************/
  public IdeOption(IdeaMainFrame frame, String title, boolean modal) {
    super(frame, frame.getBilingualMsg("0117"), modal);
    mainframe = frame;
    try {
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public IdeOption() {
    this(null, "", false);
  }


  /****************************************************************************
   * 初期化
   * @param なし
   * @return なし
   ****************************************************************************/
  private void jbInit() throws Exception {

    // キーバインディング一覧を表示するテーブルのヘッダーを設定
    hstr[0] = mainframe.getBilingualMsg("0122");
    hstr[1] = mainframe.getBilingualMsg("0123");

    // 編集ボタンの文字列を設定
    editBtn.setText(mainframe.getBilingualMsg("0009"));
    defaultBtn.setText(mainframe.getBilingualMsg("0124"));

    // 本フレームのサイズ変更を出来ないようにする
    this.setResizable(false);
    // フレームサイズ
    setSize(400,480);

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

    panel1.setLayout(borderLayout1);

    JPanel panelBase = new JPanel(new BorderLayout());

    // 現在のキーバインディングの設定を読み込む
    readIdeOptionXml();

    // 使用言語の選択部分を作成
    JPanel langPanel = new JPanel () ;
    langPanel.add(new JLabel(mainframe.getBilingualMsg("0120") + ":"));
    Vector vecLang = new Vector();
    vecLang.addElement("Japanese");
    vecLang.addElement("English");
    cmbLang = new JComboBox(vecLang);
    langPanel.add(cmbLang );

    if (SelectLanguage.toLowerCase().equals("japanese") ) {
      cmbLang.setSelectedIndex(0);
    }
    else {
      cmbLang.setSelectedIndex(1);
    }

    JPanel langBasePanel = new JPanel(new BorderLayout());
    langBasePanel.add(langPanel,BorderLayout.WEST);
    langBasePanel.setBorder(new TitledBorder(mainframe.getBilingualMsg("0120") + ""));



    // 漢字コード
    JPanel KanjiCodePanel = new JPanel () ;
    KanjiCodePanel.add(new JLabel(mainframe.getBilingualMsg("0214") + ":"));
    Vector vecKanjiCode = new Vector();
    vecKanjiCode.addElement("SHIFT-JIS");
    vecKanjiCode.addElement("EUC");
    cmbKanjiCode = new JComboBox(vecKanjiCode);
    KanjiCodePanel.add(cmbKanjiCode );

    if (SelectkanjiCode.equals("SHIFT-JIS") ) {
      cmbKanjiCode.setSelectedIndex(0);
    }
    else {
      cmbKanjiCode.setSelectedIndex(1);
    }

    JPanel KanjiCodeBasePanel = new JPanel(new BorderLayout());
    KanjiCodeBasePanel.add(KanjiCodePanel,BorderLayout.WEST);
    KanjiCodeBasePanel.setBorder(new TitledBorder(mainframe.getBilingualMsg("0214") + ""));





    // リポジトリとワークプレースの位置
    cbRepWpIndiPos.setText(mainframe.getBilingualMsg("0201"));
    JPanel rep_wp_posBasePanel = new JPanel(new BorderLayout());
    rep_wp_posBasePanel.add(cbRepWpIndiPos,BorderLayout.WEST);
    rep_wp_posBasePanel.setBorder(new TitledBorder(mainframe.getBilingualMsg("0200") + ""));

    // キーマップ選択コンボボックス
    // 現在、未使用
    Vector vecKeymap = new Vector();
    vecKeymap.addElement("Windows");
    vecKeymap.addElement("Emacs");
    JComboBox cmb = new JComboBox(vecKeymap);
    JPanel panel2 = new JPanel( ) ;
    panel2.add(new JLabel("キーマップ:"));
    panel2.add(cmb );
    cmb.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cmb_actionPerformed(e);
      }
    });

    if (SelectKeyBind.toLowerCase().equals("windows") ) {
      cmb.setSelectedIndex(0);
    }
    else {
      cmb.setSelectedIndex(1);
    }

    JPanel panel3 = new JPanel(new BorderLayout());
    panel3.add(panel2,BorderLayout.WEST);
    panel3.setBorder(new TitledBorder("キーマップ"));

    JPanel panel3_1 = new JPanel(new BorderLayout());
    panel3_1.add(langBasePanel, BorderLayout.NORTH);
    panel3_1.add(KanjiCodeBasePanel, BorderLayout.CENTER);
    //panel3_1.add(panel3, BorderLayout.CENTER);
    //panel3_1.add(rep_wp_posBasePanel, BorderLayout.CENTER);
    panelBase.add(panel3_1, BorderLayout.NORTH);

    // キーバインディングリスト作成
    model = new ReadOnlyTableModel();
    KeyBindList = new JTable(model);
    ListSelectionModel rowModel = KeyBindList.getSelectionModel();
    /*
     * 違う行を選択したことが通知されるListener
     */
    rowModel.addListSelectionListener(this);

    JScrollPane scrPane = new JScrollPane();
    scrPane.getViewport().setView(KeyBindList);
    scrPane.setPreferredSize(new Dimension(200, 120));
    scrPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

    // ｼｮｰﾄｶｯﾄｷｰ一覧
    ListShow();

    JPanel panel4 = new JPanel(new BorderLayout());
    panel4.add(scrPane,BorderLayout.CENTER);
    panel4.setBorder(new TitledBorder(mainframe.getBilingualMsg("0121")));//"キーバインディング"

    commentTextArea = new JTextArea();
    commentTextArea.setEditable(false);
    commentTextArea.setRows(3);
    //commentTextArea.setColumns(30);
    //commentTextArea.setWrapStyleWord(true);
    commentTextArea.setLineWrap(true);
    commentTextArea.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

    JPanel panel6 = new JPanel(new BorderLayout());

    editBtn.addActionListener(this);
    editBtn.setActionCommand("edit");
    defaultBtn.addActionListener(this);
    defaultBtn.setActionCommand("default");

    JPanel panel8 = new JPanel(new GridLayout(1,2));
    panel8.add(editBtn);
    panel8.add(defaultBtn);

    panel6.add(panel8, BorderLayout.WEST);


    panel4.add(panel6,BorderLayout.SOUTH);

    panelBase.add(panel4, BorderLayout.CENTER);

    JPanel panel5 = new JPanel(new BorderLayout());
    panel5.setBorder(new TitledBorder(mainframe.getBilingualMsg("0125")));//"説明"
    panel5.add(commentTextArea, BorderLayout.CENTER);
    panelBase.add(panel5, BorderLayout.SOUTH);



    JPanel panel7 = new JPanel() ;

    JButton okbtn = new JButton("OK");
    JButton clbtn = new JButton(mainframe.getBilingualMsg("0126"));//"キャンセル"
    okbtn.setPreferredSize(new Dimension(100,25));
    clbtn.setPreferredSize(new Dimension(100,25));
    okbtn.addActionListener(this);
    okbtn.setActionCommand("ok");
    clbtn.addActionListener(this);
    clbtn.setActionCommand("cancel");
    panel7.setLayout(new FlowLayout(FlowLayout.RIGHT));
    panel7.add(okbtn);
    panel7.add(clbtn);


    panel1.add(panelBase,BorderLayout.CENTER);
    //panelBase.setBorder(new TitledBorder("キーマップ"));
    panel1.add(panel7,BorderLayout.SOUTH);


    getContentPane().add(panel1);

  }

  // 未使用
  void cmb_actionPerformed(ActionEvent e) {
    JComboBox cmb = (JComboBox)e.getSource();
    SelectKeyBind = (String)cmb.getSelectedItem();
    ListShow();
  }

  /****************************************************************************
   * 読込専用テーブルモデル
   ****************************************************************************/
  class ReadOnlyTableModel extends DefaultTableModel {

    public int getColumnCount(){
      return 2;
    }

    public String getColumnName(int column){
      return hstr[column];
    }

    public boolean isCellEditable(int rowIndex,int colmunIndex) {
      return false;
    }
  }


  /****************************************************************************
   * JTableで違う行が選択されたときに、実行される
   * @param e ListSelectionEvent
   * @return なし
   ****************************************************************************/
  public void valueChanged(ListSelectionEvent e) {
    /*
     * 連続したイベントのうち、最後のものだけ処理する。
     */

    int SelRow = KeyBindList.getSelectedRow();
    
    if(SelRow==-1) return;
    
    String data = (String)KeyBindList.getValueAt(SelRow,1);
    if (data.equals("") ) {
      editBtn.setEnabled(false);
      commentTextArea.setText("");
      return;
    }
    editBtn.setEnabled(true);

    data = (String)KeyBindList.getValueAt(SelRow,0);

    Hashtable table = null;
    table = htKeyBind;
    /*
    if (SelectKeyBind.toLowerCase().equals("windows") ) {
      table = htKeyBindWindows;
    }
    else {
      table = htKeyBindEmacs;
    }
    */

    // 選択されているアクションの説明を、表示する
    String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};

    for (int i=0; i<NodeList.length; i++ ) {

      Vector vecKeyBind =(Vector)table.get(NodeList[i] + "-KeyBind");
      Vector vecKeyStroke =(Vector)table.get(NodeList[i] + "-KeyStroke");
      Vector vecComment =(Vector)table.get(NodeList[i] + "-Comment");
      Vector vecCommentEng =(Vector)table.get(NodeList[i] + "-CommentEng");

      boolean FindFlag = false;
      for (int j=0; j<vecKeyBind.size(); j++ ) {
        String KeyBind = (String)vecKeyBind.elementAt(j);
        if (KeyBind.equals(data) ) {
          if (mainframe.getLanguage().equals("japanese") ) {
            commentTextArea.setText((String)vecComment.elementAt(j));
          }
          else {
            commentTextArea.setText((String)vecCommentEng.elementAt(j));
          }
          FindFlag = true;
          break;
        }
      }

      if (FindFlag ) {
        break;
      }

    }

  }

  /****************************************************************************
   * JTableにキーバインディング一覧を表示する
   * @param なし
   * @return なし
   ****************************************************************************/
  private void ListShow(){

    // 表示されているリストの削除
    try {
      KeyBindList.clearSelection();
    }
    catch(Exception ee) {}
    if (model == null ) {
      return;
    }
    int rowcnt = model.getRowCount();
    for( int i=0; i < rowcnt; i++ ){
      model.removeRow(0);
    }

    String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};
    String[] NodeListDisp = {"ファイル","選択","削除","カーソル移動","クリップボード","スクロール","検索","その他"};

    Hashtable table = null;
    table = htKeyBind;
    /*
    if (SelectKeyBind.toLowerCase().equals("windows") ) {
      table = htKeyBindWindows;
    }
    else {
      table = htKeyBindEmacs;
    }
    */

    for (int i=0; i<NodeList.length; i++ ) {

      Vector vallist = new Vector();
      if (mainframe.getLanguage().equals("japanese") ) {
        vallist.addElement("【" + NodeListDisp[i] + "】");
      }
      else {
        vallist.addElement("【" + NodeList[i] + "】");
      }
      vallist.addElement("");
     // System.out.println(vallist);
     // System.out.println(model);
//     Vector test = new Vector();
//     test.addElement("test");
//     model.addRow(test);

      model.addRow(vallist);

      Vector vecKeyBind =(Vector)table.get(NodeList[i] + "-KeyBind");
      Vector vecKeyStroke =(Vector)table.get(NodeList[i] + "-KeyStroke");
      Vector vecComment =(Vector)table.get(NodeList[i] + "-Comment");
      for (int j=0; j<vecKeyBind.size(); j++ ) {
        vallist = new Vector();
        vallist.addElement((String)vecKeyBind.elementAt(j));
        vallist.addElement(parseKeyStroke((String)vecKeyStroke.elementAt(j)));
        model.addRow(vallist);
      }

    }

    /*
    for (Enumeration emu = htKeyBindEmacs.keys(); emu.hasMoreElements(); ) {
      String name = (String)emu.nextElement();
      String value = (String)htKeyBindEmacs.get(name);
      if (value == null ) {
        value = "";
      }

      Vector vallist = new Vector();
      vallist.addElement(name);
      vallist.addElement(value);
      model.addRow(vallist);
    }
    */

  }


  private String SelectKeyBind = "";
  private String SelectLanguage = "";
  private String SelectkanjiCode = "";
  private Hashtable htKeyBind = new Hashtable();
  private Hashtable htKeyBindDefault = new Hashtable();

  /****************************************************************************
   * IDEオプション設定内容を読み込む
   * @param なし
   * @return なし
   ****************************************************************************/
  private void readIdeOptionXml() {
    try
    {
      // ドキュメントビルダーファクトリを生成
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      // ドキュメントビルダーを生成
      DocumentBuilder builder = dbfactory.newDocumentBuilder();
      // パースを実行してDocumentオブジェクトを取得
      Document doc = builder.parse(new File("properties/ide-option.xml"));
      // ルート要素を取得（タグ名：site）
      Element root = doc.getDocumentElement();
      //System.out.println("ルート要素のタグ名：" + root.getTagName());
      //System.out.println("***** ページリスト *****");

      NodeList language = root.getElementsByTagName("language");
      for (int i=0; i < language.getLength() ; i++) {
        Element element = (Element)language.item(i);
        String title = element.getFirstChild().getNodeValue();
        SelectLanguage = title;
      }

      NodeList kanji = root.getElementsByTagName("kanjicode");
      for (int i=0; i < kanji.getLength() ; i++) {
        Element element = (Element)kanji.item(i);
        String title = element.getFirstChild().getNodeValue();
        SelectkanjiCode = title;
      }

      /*
      NodeList keybindlist = root.getElementsByTagName("keybind");
      for (int i=0; i < keybindlist.getLength() ; i++) {
        Element element = (Element)keybindlist.item(i);
        String title = element.getFirstChild().getNodeValue();
        SelectKeyBind = title;
        //System.out.println("keybind：" + title);
      }
      */

      String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};
      // windows要素のリストを取得
      NodeList list = root.getElementsByTagName("User-define");
      // windows要素の数だけループ
      for (int i=0; i < list.getLength() ; i++) {
        // windows要素を取得
        Element element = (Element)list.item(i);
        for (int i2 = 0; i2<NodeList.length;i2++ ) {
          NodeList list2 = element.getElementsByTagName(NodeList[i2]);
          for (int j=0; j < list2.getLength() ; j++) {
            Element element2 = (Element)list2.item(j);
            //
            NodeList list3 = element2.getElementsByTagName("property");
            Vector vecKeyBind = new Vector();
            Vector vecKeyStroke = new Vector();
            Vector vecComment = new Vector();
            Vector vecCommentEng = new Vector();
            for (int k=0; k < list3.getLength() ; k++) {
              Element element3 = (Element)list3.item(k);
              String name = element3.getAttribute("name");
              String value = element3.getAttribute("value");
              String comment = element3.getAttribute("comment");
              String commenteng = element3.getAttribute("commenteng");
              //System.out.println("name：" + name + "  " + "value:" + value + " comment:" + comment);

              vecKeyBind.addElement(name);
              vecKeyStroke.addElement(value);
              vecComment.addElement(comment);
              vecCommentEng.addElement(commenteng);

            }
            if (NodeList[i2].equals("other") ) {
              if (vecKeyBind.indexOf("codecheck") == -1 ) {
                vecKeyBind.addElement("codecheck");
                vecKeyStroke.addElement("CS+F9");
                vecComment.addElement("コードチェック");
                vecCommentEng.addElement("check code");
              }
            }
            htKeyBind.put(NodeList[i2] + "-KeyBind",vecKeyBind);
            htKeyBind.put(NodeList[i2] + "-KeyStroke",vecKeyStroke);
            htKeyBind.put(NodeList[i2] + "-Comment",vecComment);
            htKeyBind.put(NodeList[i2] + "-CommentEng",vecCommentEng);
          }
        }
      }



      // 規定値のリストを取得
      NodeList listemacs = root.getElementsByTagName("default");
      for (int i=0; i < listemacs.getLength() ; i++) {
        Element element = (Element)listemacs.item(i);
        for (int i2 = 0; i2<NodeList.length;i2++ ) {
          NodeList list2 = element.getElementsByTagName(NodeList[i2]);
          for (int j=0; j < list2.getLength() ; j++) {
            Element element2 = (Element)list2.item(j);
            //
            NodeList list3 = element2.getElementsByTagName("property");
            Vector vecKeyBind = new Vector();
            Vector vecKeyStroke = new Vector();
            Vector vecComment = new Vector();
            Vector vecCommentEng = new Vector();
            for (int k=0; k < list3.getLength() ; k++) {
              Element element3 = (Element)list3.item(k);
              String name = element3.getAttribute("name");
              String value = element3.getAttribute("value");
              String comment = element3.getAttribute("comment");
              String commenteng = element3.getAttribute("commenteng");
              //System.out.println("name：" + name + "  " + "value:" + value + " comment:" + comment);

              vecKeyBind.addElement(name);
              vecKeyStroke.addElement(value);
              vecComment.addElement(comment);
              vecCommentEng.addElement(commenteng);
            }
            if (NodeList[i2].equals("other") ) {
              if (vecKeyBind.indexOf("codecheck") == -1 ) {
                vecKeyBind.addElement("codecheck");
                vecKeyStroke.addElement("CS+F9");
                vecComment.addElement("コードチェック");
                vecCommentEng.addElement("check code");
              }
            }
            htKeyBindDefault.put(NodeList[i2] + "-KeyBind",vecKeyBind);
            htKeyBindDefault.put(NodeList[i2] + "-KeyStroke",vecKeyStroke);
            htKeyBindDefault.put(NodeList[i2] + "-Comment",vecComment);
            htKeyBindDefault.put(NodeList[i2] + "-CommentEng",vecCommentEng);

          }
        }
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /****************************************************************************
   * 設定内容を書き込む
   * @param なし
   * @return なし
   ****************************************************************************/
  private void saveIdeOptionXml() {

        //System.out.println(KeyEvent.getKeyText(KeyEvent.VK_BACK_SPACE));
    // XmlDocumentオブジェクトを構築
    XmlDocument doc = new XmlDocument();

    // ドキュメントのルートに"Component"を設定
    ElementNode root = (ElementNode) doc.createElement("xproperties");
    doc.appendChild(root);

    ElementNode language =
      (ElementNode) doc.createElement("language");
    root.appendChild(language);
    String lang = (String)cmbLang.getSelectedItem();
    language.appendChild(doc.createTextNode(lang.toLowerCase()));

    if (System.getProperty("language") != null ) {
      System.getProperties().remove("language");
    }
    //System.setProperty("language",SelectLanguage.toLowerCase());
    System.setProperty("language",lang.toLowerCase());



    ElementNode kanjicode =
      (ElementNode) doc.createElement("kanjicode");
    root.appendChild(kanjicode);
    String kanji = (String)cmbKanjiCode.getSelectedItem();
    kanjicode.appendChild(doc.createTextNode(kanji));

    if (System.getProperty("kanjicode") != null ) {
      System.getProperties().remove("kanjicode");
    }

    if (kanji.equals("SHIFT-JIS") ) {
      System.setProperty("kanjicode","SHIFT_JIS");
    }
    else if (kanji.equals("EUC") ) {
      System.setProperty("kanjicode","EUC_JP");
    }


    ElementNode KeyBind =
      (ElementNode) doc.createElement("keybind");
    root.appendChild(KeyBind);
    Text titlec = doc.createTextNode("windows");
    KeyBind.appendChild(titlec);

    String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};

    ElementNode windowsKeyBind =
      (ElementNode) doc.createElement("User-define");
    root.appendChild(windowsKeyBind);

    for (int i=0; i<NodeList.length; i++ ) {
      ElementNode node1 =
        (ElementNode) doc.createElement(NodeList[i]);

      Vector vecKeyBind =(Vector)htKeyBind.get(NodeList[i] + "-KeyBind");
      Vector vecKeyStroke =(Vector)htKeyBind.get(NodeList[i] + "-KeyStroke");
      Vector vecComment =(Vector)htKeyBind.get(NodeList[i] + "-Comment");
      Vector vecCommentEng =(Vector)htKeyBind.get(NodeList[i] + "-CommentEng");
      for (int j=0; j<vecKeyBind.size(); j++ ) {
        ElementNode property =
          (ElementNode) doc.createElement("property");
        property.setAttribute("name", (String)vecKeyBind.elementAt(j));
        property.setAttribute("value", (String)vecKeyStroke.elementAt(j));
        property.setAttribute("comment", (String)vecComment.elementAt(j));
        property.setAttribute("commenteng", (String)vecCommentEng.elementAt(j));
        node1.appendChild(property);
      }

      windowsKeyBind.appendChild(node1);
    }

    ElementNode emacsKeyBind =
      (ElementNode) doc.createElement("default");
    root.appendChild(emacsKeyBind);
    for (int i=0; i<NodeList.length; i++ ) {
      ElementNode node1 =
        (ElementNode) doc.createElement(NodeList[i]);
      emacsKeyBind.appendChild(node1);

      Vector vecKeyBind =(Vector)htKeyBindDefault.get(NodeList[i] + "-KeyBind");
      Vector vecKeyStroke =(Vector)htKeyBindDefault.get(NodeList[i] + "-KeyStroke");
      Vector vecComment =(Vector)htKeyBindDefault.get(NodeList[i] + "-Comment");
      Vector vecCommentEng =(Vector)htKeyBindDefault.get(NodeList[i] + "-CommentEng");
      for (int j=0; j<vecKeyBind.size(); j++ ) {
        ElementNode property =
          (ElementNode) doc.createElement("property");
        property.setAttribute("name", (String)vecKeyBind.elementAt(j));
        property.setAttribute("value", (String)vecKeyStroke.elementAt(j));
        property.setAttribute("comment", (String)vecComment.elementAt(j));
        property.setAttribute("commenteng", (String)vecCommentEng.elementAt(j));
        node1.appendChild(property);
      }
    }

    try{
      // XMLドキュメントをファイルに出力
      FileOutputStream fos = new FileOutputStream ("properties/ide-option.xml");
      //OutputStreamWriter osw = new OutputStreamWriter(fos, "SHIFT_JIS");
      OutputStreamWriter osw = new OutputStreamWriter(fos, "MS932");
      doc.write(osw);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /****************************************************************************
   * キーの組み合わせ文字列を解析し、分かりやすい文字列に変換する<br>
   * 例：CS+X → Ctrl+Shift+X
   * @param keyStroke　IDE設定ファイルに記述されているキーの組み合わせ文字列
   * @return キーの組み合わせ文字列を解析し、分かりやすく変換した文字列
   ****************************************************************************/
  public static String parseKeyStroke(String keyStroke)
  {
    if(keyStroke == null)
      return null;
    String KeyStroke = "";
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
          if (KeyStroke.equals("") ) {
            KeyStroke += "Alt";
          }
          else {
            KeyStroke += "+Alt";
          }
          break;
        case 'C':
          if (KeyStroke.equals("") ) {
            KeyStroke += "Ctrl";
          }
          else {
            KeyStroke += "+Ctrl";
          }
          break;
        case 'M':
          if (KeyStroke.equals("") ) {
            KeyStroke += "Meta";
          }
          else {
            KeyStroke += "+Meta";
          }
          break;
        case 'S':
          if (KeyStroke.equals("") ) {
            KeyStroke += "Shift";
          }
          else {
            KeyStroke += "+Shift";
          }
          break;
        case 'G':
          if (KeyStroke.equals("") ) {
            KeyStroke += "Alt Graph";
          }
          else {
            KeyStroke += "+Alt Graph";
          }
          break;
        }
      }
    }
    String key = keyStroke.substring(index + 1);
    if (KeyStroke.equals("") ) {
      KeyStroke += key;
    }
    else {
      KeyStroke += "+" + key;
    }
    return KeyStroke;
  }

  /****************************************************************************
   * イベント処理
   * @param e　アクションイベント
   * @return なし
   ****************************************************************************/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("edit")){
        // キーバインド設定サブ画面の表示
       int SelRow = KeyBindList.getSelectedRow();
       String KeyBind = (String)KeyBindList.getValueAt(SelRow,0);
       String KeyStroke = (String)KeyBindList.getValueAt(SelRow,1);

       IdeOptionSub dlg = new IdeOptionSub(mainframe,mainframe.getBilingualMsg("0163") + "：" + KeyBind, true, KeyStroke, htKeyBind, KeyBind);
       //dlg.show();
	      dlg.setVisible(true);
       if (dlg.ret ) {
         KeyBindList.setValueAt(dlg.keystroke,SelRow,1);

         Hashtable table = null;
         table = htKeyBind;
         /*
        if (SelectKeyBind.toLowerCase().equals("windows") ) {
          table = htKeyBindWindows;
        }
        else {
          table = htKeyBindEmacs;
        }
         */


         String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};

         for (int i=0; i<NodeList.length; i++ ) {

           Vector vecKeyBind =(Vector)table.get(NodeList[i] + "-KeyBind");
           Vector vecKeyStroke =(Vector)table.get(NodeList[i] + "-KeyStroke");
           Vector vecComment =(Vector)table.get(NodeList[i] + "-Comment");

           boolean FindFlag = false;
           for (int j=0; j<vecKeyBind.size(); j++ ) {
             String wkKeyBind = (String)vecKeyBind.elementAt(j);
             if (wkKeyBind.equals(KeyBind) ) {
               vecKeyStroke.setElementAt(dlg.keystroke2,j);
               table.put(NodeList[i] + "-KeyStroke",vecKeyStroke);
               FindFlag = true;
               break;
             }
           }

           if (FindFlag ) {
             break;
           }

         }

       }

      }
      else if(action.equals("default")){
        htKeyBind.clear();

        for (Enumeration emu = htKeyBindDefault.keys(); emu.hasMoreElements(); ) {
          String key = (String)emu.nextElement();
          Vector vec = (Vector)htKeyBindDefault.get(key);
          htKeyBind.put(key,vec.clone());
        }
        ListShow();
      }
      else if(action.equals("ok")){
        ok();
      }
      else if(action.equals("cancel")){
        cancel();
      }

    }catch(Exception e2){
      //System.out.println("eee");
    }

  }

  /****************************************************************************
   * OK処理
   * @param なし
   * @return なし
   ****************************************************************************/
  public void ok () {
    saveIdeOptionXml();
    ret = true;
    dispose();
  }

  /****************************************************************************
   * Cancel処理
   * @param なし
   * @return なし
   ****************************************************************************/
  public void cancel () {
    ret = false;
    dispose();
  }
}