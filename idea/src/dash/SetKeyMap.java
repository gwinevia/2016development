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
 * <p>タイトル: </p>
 * <p>説明: </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名: </p>
 * @author 未入力
 * @version 1.0
 */

public class SetKeyMap extends EnhancedDialog implements ActionListener,ListSelectionListener {
  private IdeaMainFrame mainframe = null;
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  private JTable KeyBindList = null;
  private String[] hstr = {"アクション", "キーストローク"};
  DefaultTableModel model;
  private String[][] rowdata = {
    {"",""}
  };

  private JTextArea commentTextArea = null;
  private JButton editBtn = new JButton ("編集");
  public SetKeyMap(IdeaMainFrame frame, String title, boolean modal) {
    super(frame, title, modal);
    mainframe = frame;
    try {
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public SetKeyMap() {
    this(null, "", false);
  }
  private void jbInit() throws Exception {
    setSize(400,450);
    // フレームサイズ
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
    readKeyBindXml();

    // キーマップ選択コンボボックス

    Vector vecKeymap = new Vector();
    vecKeymap.addElement("Windows");
    vecKeymap.addElement("Emacs");
    JComboBox cmb = new JComboBox(vecKeymap);
    JPanel panel2 = new JPanel();
    panel2.add(new JLabel("キーマップ:"));
    panel2.add(cmb);
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
    panelBase.add(panel3, BorderLayout.NORTH);

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



    ListShow();

    JPanel panel4 = new JPanel(new BorderLayout());
    panel4.add(scrPane,BorderLayout.CENTER);
    panel4.setBorder(new TitledBorder("キーバインディング"));

    commentTextArea = new JTextArea();
    commentTextArea.setEditable(false);
    commentTextArea.setRows(5);
    //commentTextArea.setColumns(30);
    //commentTextArea.setWrapStyleWord(true);
    commentTextArea.setLineWrap(true);
    commentTextArea.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

    JPanel panel6 = new JPanel(new BorderLayout());
    panel6.add(editBtn, BorderLayout.WEST);
    editBtn.addActionListener(this);
    editBtn.setActionCommand("edit");

    panel4.add(panel6,BorderLayout.SOUTH);

    panelBase.add(panel4, BorderLayout.CENTER);

    JPanel panel5 = new JPanel(new BorderLayout());
    panel5.setBorder(new TitledBorder("説明"));
    panel5.add(commentTextArea, BorderLayout.CENTER);
    panelBase.add(panel5, BorderLayout.SOUTH);



    JPanel panel7 = new JPanel() ;

    JButton okbtn = new JButton("OK");
    JButton clbtn = new JButton("キャンセル");
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
    panel1.add(panel7,BorderLayout.SOUTH);


    getContentPane().add(panel1);

  }

  void cmb_actionPerformed(ActionEvent e) {
    JComboBox cmb = (JComboBox)e.getSource();
    SelectKeyBind = (String)cmb.getSelectedItem();
    ListShow();
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
  public void valueChanged(ListSelectionEvent e) {
    /*
     * 連続したイベントのうち、最後のものだけ処理する。
     */

    int SelRow = KeyBindList.getSelectedRow();
    String data = (String)KeyBindList.getValueAt(SelRow,1);
    if (data.equals("") ) {
      editBtn.setEnabled(false);
      commentTextArea.setText("");
      return;
    }
    editBtn.setEnabled(true);

    data = (String)KeyBindList.getValueAt(SelRow,0);

    Hashtable table = null;
    if (SelectKeyBind.toLowerCase().equals("windows") ) {
      table = htKeyBindWindows;
    }
    else {
      table = htKeyBindEmacs;
    }


    String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};

    for (int i=0; i<NodeList.length; i++ ) {

      Vector vecKeyBind =(Vector)table.get(NodeList[i] + "-KeyBind");
      Vector vecKeyStroke =(Vector)table.get(NodeList[i] + "-KeyStroke");
      Vector vecComment =(Vector)table.get(NodeList[i] + "-Comment");

      boolean FindFlag = false;
      for (int j=0; j<vecKeyBind.size(); j++ ) {
        String KeyBind = (String)vecKeyBind.elementAt(j);
        if (KeyBind.equals(data) ) {
          commentTextArea.setText((String)vecComment.elementAt(j));
          FindFlag = true;
          break;
        }
      }

      if (FindFlag ) {
        break;
      }

    }

  }

  /**
  * JTableにファイル一覧を表示
  */
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
    if (SelectKeyBind.toLowerCase().equals("windows") ) {
      table = htKeyBindWindows;
    }
    else {
      table = htKeyBindEmacs;
    }

    for (int i=0; i<NodeList.length; i++ ) {

      Vector vallist = new Vector();
      vallist.addElement("【" + NodeListDisp[i] + "】");
      vallist.addElement("");
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
  private Hashtable htKeyBindWindows = new Hashtable();
  private Hashtable htKeyBindEmacs = new Hashtable();

  private void readKeyBindXml() {
    try
    {
      // ドキュメントビルダーファクトリを生成
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      // ドキュメントビルダーを生成
      DocumentBuilder builder = dbfactory.newDocumentBuilder();
      // パースを実行してDocumentオブジェクトを取得
      Document doc = builder.parse(new File("properties/keybind.xml"));
      // ルート要素を取得（タグ名：site）
      Element root = doc.getDocumentElement();
      //System.out.println("ルート要素のタグ名：" + root.getTagName());
      //System.out.println("***** ページリスト *****");

      NodeList keybindlist = root.getElementsByTagName("keybind");
      for (int i=0; i < keybindlist.getLength() ; i++) {
        Element element = (Element)keybindlist.item(i);
        String title = element.getFirstChild().getNodeValue();
        SelectKeyBind = title;
        //System.out.println("keybind：" + title);
      }

      String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};
      // windows要素のリストを取得
      NodeList list = root.getElementsByTagName("windows");
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
            for (int k=0; k < list3.getLength() ; k++) {
              Element element3 = (Element)list3.item(k);
              String name = element3.getAttribute("name");
              String value = element3.getAttribute("value");
              String comment = element3.getAttribute("comment");
              //System.out.println("name：" + name + "  " + "value:" + value + " comment:" + comment);

              vecKeyBind.addElement(name);
              vecKeyStroke.addElement(value);
              vecComment.addElement(comment);

            }
            htKeyBindWindows.put(NodeList[i2] + "-KeyBind",vecKeyBind);
            htKeyBindWindows.put(NodeList[i2] + "-KeyStroke",vecKeyStroke);
            htKeyBindWindows.put(NodeList[i2] + "-Comment",vecComment);
          }
        }
      }

      // emacs要素のリストを取得
      NodeList listemacs = root.getElementsByTagName("emacs");
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
            for (int k=0; k < list3.getLength() ; k++) {
              Element element3 = (Element)list3.item(k);
              String name = element3.getAttribute("name");
              String value = element3.getAttribute("value");
              String comment = element3.getAttribute("comment");
              //System.out.println("name：" + name + "  " + "value:" + value + " comment:" + comment);

              vecKeyBind.addElement(name);
              vecKeyStroke.addElement(value);
              vecComment.addElement(comment);
            }
            htKeyBindEmacs.put(NodeList[i2] + "-KeyBind",vecKeyBind);
            htKeyBindEmacs.put(NodeList[i2] + "-KeyStroke",vecKeyStroke);
            htKeyBindEmacs.put(NodeList[i2] + "-Comment",vecComment);

          }
        }
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void saveKeyBindXml() {

        //System.out.println(KeyEvent.getKeyText(KeyEvent.VK_BACK_SPACE));
    // XmlDocumentオブジェクトを構築
    XmlDocument doc = new XmlDocument();

    // ドキュメントのルートに"Component"を設定
    ElementNode root = (ElementNode) doc.createElement("xproperties");
    doc.appendChild(root);

    ElementNode KeyBind =
      (ElementNode) doc.createElement("keybind");
    root.appendChild(KeyBind);
    Text titlec = doc.createTextNode("windiws");
    KeyBind.appendChild(titlec);

    String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};

    ElementNode windowsKeyBind =
      (ElementNode) doc.createElement("windows");
    root.appendChild(windowsKeyBind);

    for (int i=0; i<NodeList.length; i++ ) {
      ElementNode node1 =
        (ElementNode) doc.createElement(NodeList[i]);

      Vector vecKeyBind =(Vector)htKeyBindWindows.get(NodeList[i] + "-KeyBind");
      Vector vecKeyStroke =(Vector)htKeyBindWindows.get(NodeList[i] + "-KeyStroke");
      Vector vecComment =(Vector)htKeyBindWindows.get(NodeList[i] + "-Comment");
      for (int j=0; j<vecKeyBind.size(); j++ ) {
        ElementNode property =
          (ElementNode) doc.createElement("property");
        property.setAttribute("name", (String)vecKeyBind.elementAt(j));
        property.setAttribute("value", (String)vecKeyStroke.elementAt(j));
        property.setAttribute("comment", (String)vecComment.elementAt(j));
        node1.appendChild(property);
      }

      windowsKeyBind.appendChild(node1);
    }

    ElementNode emacsKeyBind =
      (ElementNode) doc.createElement("emacs");
    root.appendChild(emacsKeyBind);
    for (int i=0; i<NodeList.length; i++ ) {
      ElementNode node1 =
        (ElementNode) doc.createElement(NodeList[i]);
      emacsKeyBind.appendChild(node1);

      Vector vecKeyBind =(Vector)htKeyBindEmacs.get(NodeList[i] + "-KeyBind");
      Vector vecKeyStroke =(Vector)htKeyBindEmacs.get(NodeList[i] + "-KeyStroke");
      Vector vecComment =(Vector)htKeyBindEmacs.get(NodeList[i] + "-Comment");
      for (int j=0; j<vecKeyBind.size(); j++ ) {
        ElementNode property =
          (ElementNode) doc.createElement("property");
        property.setAttribute("name", (String)vecKeyBind.elementAt(j));
        property.setAttribute("value", (String)vecKeyStroke.elementAt(j));
        property.setAttribute("comment", (String)vecComment.elementAt(j));
        node1.appendChild(property);
      }
    }

    try{
      // XMLドキュメントをファイルに出力
      FileOutputStream fos = new FileOutputStream ("properties/keybind3.xml");
      OutputStreamWriter osw = new OutputStreamWriter(fos, "SHIFT_JIS");
      doc.write(osw);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

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

  // イベント処理
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("edit")){
       int SelRow = KeyBindList.getSelectedRow();
       String KeyBind = (String)KeyBindList.getValueAt(SelRow,0);
       String KeyStroke = (String)KeyBindList.getValueAt(SelRow,1);

       SetKeyMapSub dlg = new SetKeyMapSub(mainframe,"キーストロークの編集",true, KeyStroke);
       dlg.show();
       // dlg.setAlwaysOnTop(true);
       if (dlg.ret ) {
         KeyBindList.setValueAt(dlg.keystroke,SelRow,1);

         Hashtable table = null;
        if (SelectKeyBind.toLowerCase().equals("windows") ) {
          table = htKeyBindWindows;
        }
        else {
          table = htKeyBindEmacs;
        }


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
      else if(action.equals("ok")){
        ok();
      }
      else if(action.equals("cancel")){
        cancel();
      }

    }catch(Exception e2){

    }

  }

  public void ok () {
    saveKeyBindXml();
    dispose();
  }
  public void cancel () {
    dispose();
  }
}