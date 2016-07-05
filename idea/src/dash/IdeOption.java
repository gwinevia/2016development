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
 * <p>�^�C�g��:IDE�I�v�V�����ݒ�_�C�A���O </p>
 * <p>����:�g�p����A�L�[�}�b�s���O�ݒ���s�� </p>
 * <p>���쌠: Copyright (c) 2003</p>
 * <p>��Ж�:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class IdeOption extends EnhancedDialog implements ActionListener,ListSelectionListener {

  /** �e�t���[�� */
  private IdeaMainFrame mainframe = null;

  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** �L�[�o�C���f�B���O�ꗗ��\������e�[�u�� */
  private JTable KeyBindList = null;

  /** �L�[�o�C���f�B���O�ꗗ��\������e�[�u���̃w�b�_�[ */
  private String[] hstr = {"�A�N�V����", "�L�[�X�g���[�N"};

  /** �e�[�u�����f�� */
  DefaultTableModel model;

  /** �L�[�o�C���f�B���O�̃A�N�V�����̐�����\������e�L�X�g�G���A */
  private JTextArea commentTextArea = null;

  /** �L�[�o�C���f�B���O�ҏW�{�^�� */
  private JButton editBtn = new JButton ("�ҏW");
  private JButton defaultBtn = new JButton ("�K��l�ɖ߂�");

  /** �g�p����I���R���{�{�b�N�X */
  private JComboBox cmbLang = null;

  /** �g�p�����R�[�h�I���R���{�{�b�N�X */
  private JComboBox cmbKanjiCode = null;

  /** ���ʁE�E�E�E[OK]�N���b�N���͂P[�L�����Z��]�N���b�N���͂O */
  public boolean ret = false;

  /** ���|�W�g���E���[�N�v���[�X�̕\���ʒu�̃`�F�b�N */
  private JCheckBox cbRepWpIndiPos = new JCheckBox();


  /****************************************************************************
   * �R���X�g���N�^
   * @param frame IdeaMainFrame���󂯎��
   * @param title �_�C�A���O�̃^�C�g��
   * @param modal ���[�_���ŕ\������ꍇ�Atrue�B�����łȂ����Afalse
   * @return �Ȃ�
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
   * ������
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void jbInit() throws Exception {

    // �L�[�o�C���f�B���O�ꗗ��\������e�[�u���̃w�b�_�[��ݒ�
    hstr[0] = mainframe.getBilingualMsg("0122");
    hstr[1] = mainframe.getBilingualMsg("0123");

    // �ҏW�{�^���̕������ݒ�
    editBtn.setText(mainframe.getBilingualMsg("0009"));
    defaultBtn.setText(mainframe.getBilingualMsg("0124"));

    // �{�t���[���̃T�C�Y�ύX���o���Ȃ��悤�ɂ���
    this.setResizable(false);
    // �t���[���T�C�Y
    setSize(400,480);

    // �\���ʒu�̒����B��ʒ����ɕ\������
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

    // ���݂̃L�[�o�C���f�B���O�̐ݒ��ǂݍ���
    readIdeOptionXml();

    // �g�p����̑I�𕔕����쐬
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



    // �����R�[�h
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





    // ���|�W�g���ƃ��[�N�v���[�X�̈ʒu
    cbRepWpIndiPos.setText(mainframe.getBilingualMsg("0201"));
    JPanel rep_wp_posBasePanel = new JPanel(new BorderLayout());
    rep_wp_posBasePanel.add(cbRepWpIndiPos,BorderLayout.WEST);
    rep_wp_posBasePanel.setBorder(new TitledBorder(mainframe.getBilingualMsg("0200") + ""));

    // �L�[�}�b�v�I���R���{�{�b�N�X
    // ���݁A���g�p
    Vector vecKeymap = new Vector();
    vecKeymap.addElement("Windows");
    vecKeymap.addElement("Emacs");
    JComboBox cmb = new JComboBox(vecKeymap);
    JPanel panel2 = new JPanel( ) ;
    panel2.add(new JLabel("�L�[�}�b�v:"));
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
    panel3.setBorder(new TitledBorder("�L�[�}�b�v"));

    JPanel panel3_1 = new JPanel(new BorderLayout());
    panel3_1.add(langBasePanel, BorderLayout.NORTH);
    panel3_1.add(KanjiCodeBasePanel, BorderLayout.CENTER);
    //panel3_1.add(panel3, BorderLayout.CENTER);
    //panel3_1.add(rep_wp_posBasePanel, BorderLayout.CENTER);
    panelBase.add(panel3_1, BorderLayout.NORTH);

    // �L�[�o�C���f�B���O���X�g�쐬
    model = new ReadOnlyTableModel();
    KeyBindList = new JTable(model);
    ListSelectionModel rowModel = KeyBindList.getSelectionModel();
    /*
     * �Ⴄ�s��I���������Ƃ��ʒm�����Listener
     */
    rowModel.addListSelectionListener(this);

    JScrollPane scrPane = new JScrollPane();
    scrPane.getViewport().setView(KeyBindList);
    scrPane.setPreferredSize(new Dimension(200, 120));
    scrPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

    // ���Ķ�ķ��ꗗ
    ListShow();

    JPanel panel4 = new JPanel(new BorderLayout());
    panel4.add(scrPane,BorderLayout.CENTER);
    panel4.setBorder(new TitledBorder(mainframe.getBilingualMsg("0121")));//"�L�[�o�C���f�B���O"

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
    panel5.setBorder(new TitledBorder(mainframe.getBilingualMsg("0125")));//"����"
    panel5.add(commentTextArea, BorderLayout.CENTER);
    panelBase.add(panel5, BorderLayout.SOUTH);



    JPanel panel7 = new JPanel() ;

    JButton okbtn = new JButton("OK");
    JButton clbtn = new JButton(mainframe.getBilingualMsg("0126"));//"�L�����Z��"
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
    //panelBase.setBorder(new TitledBorder("�L�[�}�b�v"));
    panel1.add(panel7,BorderLayout.SOUTH);


    getContentPane().add(panel1);

  }

  // ���g�p
  void cmb_actionPerformed(ActionEvent e) {
    JComboBox cmb = (JComboBox)e.getSource();
    SelectKeyBind = (String)cmb.getSelectedItem();
    ListShow();
  }

  /****************************************************************************
   * �Ǎ���p�e�[�u�����f��
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
   * JTable�ňႤ�s���I�����ꂽ�Ƃ��ɁA���s�����
   * @param e ListSelectionEvent
   * @return �Ȃ�
   ****************************************************************************/
  public void valueChanged(ListSelectionEvent e) {
    /*
     * �A�������C�x���g�̂����A�Ō�̂��̂�����������B
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

    // �I������Ă���A�N�V�����̐������A�\������
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
   * JTable�ɃL�[�o�C���f�B���O�ꗗ��\������
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void ListShow(){

    // �\������Ă��郊�X�g�̍폜
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
    String[] NodeListDisp = {"�t�@�C��","�I��","�폜","�J�[�\���ړ�","�N���b�v�{�[�h","�X�N���[��","����","���̑�"};

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
        vallist.addElement("�y" + NodeListDisp[i] + "�z");
      }
      else {
        vallist.addElement("�y" + NodeList[i] + "�z");
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
   * IDE�I�v�V�����ݒ���e��ǂݍ���
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void readIdeOptionXml() {
    try
    {
      // �h�L�������g�r���_�[�t�@�N�g���𐶐�
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      // �h�L�������g�r���_�[�𐶐�
      DocumentBuilder builder = dbfactory.newDocumentBuilder();
      // �p�[�X�����s����Document�I�u�W�F�N�g���擾
      Document doc = builder.parse(new File("properties/ide-option.xml"));
      // ���[�g�v�f���擾�i�^�O���Fsite�j
      Element root = doc.getDocumentElement();
      //System.out.println("���[�g�v�f�̃^�O���F" + root.getTagName());
      //System.out.println("***** �y�[�W���X�g *****");

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
        //System.out.println("keybind�F" + title);
      }
      */

      String[] NodeList = {"file","select","delete","caret-move","clipboard","scroll","search","other"};
      // windows�v�f�̃��X�g���擾
      NodeList list = root.getElementsByTagName("User-define");
      // windows�v�f�̐��������[�v
      for (int i=0; i < list.getLength() ; i++) {
        // windows�v�f���擾
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
              //System.out.println("name�F" + name + "  " + "value:" + value + " comment:" + comment);

              vecKeyBind.addElement(name);
              vecKeyStroke.addElement(value);
              vecComment.addElement(comment);
              vecCommentEng.addElement(commenteng);

            }
            if (NodeList[i2].equals("other") ) {
              if (vecKeyBind.indexOf("codecheck") == -1 ) {
                vecKeyBind.addElement("codecheck");
                vecKeyStroke.addElement("CS+F9");
                vecComment.addElement("�R�[�h�`�F�b�N");
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



      // �K��l�̃��X�g���擾
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
              //System.out.println("name�F" + name + "  " + "value:" + value + " comment:" + comment);

              vecKeyBind.addElement(name);
              vecKeyStroke.addElement(value);
              vecComment.addElement(comment);
              vecCommentEng.addElement(commenteng);
            }
            if (NodeList[i2].equals("other") ) {
              if (vecKeyBind.indexOf("codecheck") == -1 ) {
                vecKeyBind.addElement("codecheck");
                vecKeyStroke.addElement("CS+F9");
                vecComment.addElement("�R�[�h�`�F�b�N");
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
   * �ݒ���e����������
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void saveIdeOptionXml() {

        //System.out.println(KeyEvent.getKeyText(KeyEvent.VK_BACK_SPACE));
    // XmlDocument�I�u�W�F�N�g���\�z
    XmlDocument doc = new XmlDocument();

    // �h�L�������g�̃��[�g��"Component"��ݒ�
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
      // XML�h�L�������g���t�@�C���ɏo��
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
   * �L�[�̑g�ݍ��킹���������͂��A������₷��������ɕϊ�����<br>
   * ��FCS+X �� Ctrl+Shift+X
   * @param keyStroke�@IDE�ݒ�t�@�C���ɋL�q����Ă���L�[�̑g�ݍ��킹������
   * @return �L�[�̑g�ݍ��킹���������͂��A������₷���ϊ�����������
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
   * �C�x���g����
   * @param e�@�A�N�V�����C�x���g
   * @return �Ȃ�
   ****************************************************************************/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("edit")){
        // �L�[�o�C���h�ݒ�T�u��ʂ̕\��
       int SelRow = KeyBindList.getSelectedRow();
       String KeyBind = (String)KeyBindList.getValueAt(SelRow,0);
       String KeyStroke = (String)KeyBindList.getValueAt(SelRow,1);

       IdeOptionSub dlg = new IdeOptionSub(mainframe,mainframe.getBilingualMsg("0163") + "�F" + KeyBind, true, KeyStroke, htKeyBind, KeyBind);
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
   * OK����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void ok () {
    saveIdeOptionXml();
    ret = true;
    dispose();
  }

  /****************************************************************************
   * Cancel����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void cancel () {
    ret = false;
    dispose();
  }
}