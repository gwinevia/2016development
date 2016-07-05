package dash;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.*;

/**
 * <p>タイトル: </p>
 * <p>説明: </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名: </p>
 * @author 未入力
 * @version 1.0
 */

public class SetKeyMapSub extends EnhancedDialog implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JTextField KeyStroke = new JTextField();
  private JCheckBox cbCtrl = new JCheckBox("Ctrl");
  private JCheckBox cbAlt = new JCheckBox("Alt");
  private JCheckBox cbShift = new JCheckBox("Shift");
  private JCheckBox cbMeta = new JCheckBox("Meta");
  private JCheckBox cbAltGraph = new JCheckBox("Alt Graph");

  private JComboBox cmbKeyCode = new JComboBox();
  public String keystroke = "";
  public String keystroke2 = "";
  public boolean ret = false;

  public SetKeyMapSub(Frame frame, String title, boolean modal, String keystroke) {
    super(frame, title, modal);
    this.keystroke = keystroke;
    try {
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public SetKeyMapSub() {
    this(null, "", false, "");
  }
  private void jbInit() throws Exception {
    setSize(400,210);
    this.setResizable(false);
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

    JPanel panelBase = new JPanel(new BorderLayout() ) ;

    // キーストローク
    JPanel panel2 = new JPanel(new BorderLayout() ) ;
    panel2.add(new JLabel("キーストローク："),BorderLayout.WEST);
    panel2.add(KeyStroke,BorderLayout.CENTER);
    KeyStroke.setEditable(false);
    KeyStroke.setText(keystroke);
    panel2.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
    panelBase.add(panel2,BorderLayout.NORTH);

    // プロパティ
    JPanel panel3 = new JPanel(new GridLayout(2,3) ) ;
    panel3.add(cbCtrl);
    panel3.add(cbAlt);
    panel3.add(cbShift);
    panel3.add(cbMeta);
    panel3.add(cbAltGraph);

    if (keystroke.indexOf("Ctrl") != -1 ) {
      cbCtrl.setSelected(true);
    }
    if (keystroke.indexOf("Alt") != -1 ) {
      cbAlt.setSelected(true);
    }
    if (keystroke.indexOf("Shift") != -1 ) {
      cbShift.setSelected(true);
    }
    if (keystroke.indexOf("Meta") != -1 ) {
      cbMeta.setSelected(true);
    }
    if (keystroke.indexOf("Alt Graph") != -1 ) {
      cbAltGraph.setSelected(true);
    }

    cbCtrl.addActionListener(this);
    cbAlt.addActionListener(this);
    cbShift.addActionListener(this);
    cbMeta.addActionListener(this);
    cbAltGraph.addActionListener(this);

    cbCtrl.setActionCommand("Ctrl");
    cbAlt.setActionCommand("Alt");
    cbShift.setActionCommand("Shift");
    cbMeta.setActionCommand("Meta");
    cbAltGraph.setActionCommand("AltGraph");

    JPanel panel4 = new JPanel(new BorderLayout() ) ;
    panel4.add(new JLabel("キーコード："), BorderLayout.WEST);
    Vector vecKeyCode = new Vector();
    java.lang.reflect.Field[] FieldAr = KeyEvent.class.getFields();
    for (int i=0; i<FieldAr.length; i++ ) {
      String fieldname = FieldAr[i].toString();
      fieldname = fieldname.substring(fieldname.lastIndexOf(".")+1);
      if (fieldname.startsWith("VK_") ) {
        vecKeyCode.addElement(fieldname);
      }
    }

    cmbKeyCode = new JComboBox(vecKeyCode);
    cmbKeyCode.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cmb_actionPerformed(e);
      }
    });
    panel4.add(cmbKeyCode,BorderLayout.CENTER);

    String keyCode = keystroke.substring(keystroke.lastIndexOf("+")+1);
    keyCode = "VK_" + keyCode;
    int pos = vecKeyCode.indexOf(keyCode);
    if (pos != -1 ) {
      cmbKeyCode.setSelectedIndex(pos);
    }

    JPanel panel5 = new JPanel(new BorderLayout() ) ;
    panel5.add(panel3,BorderLayout.CENTER);
    panel5.add(panel4,BorderLayout.SOUTH);
    panel5.setBorder(new TitledBorder("プロパティ"));

    panelBase.add(panel5,BorderLayout.CENTER);


    panel1.setLayout(borderLayout1);

    panel1.add(panelBase,BorderLayout.CENTER);


    JPanel panel6 = new JPanel() ;

    JButton okbtn = new JButton("OK");
    JButton clbtn = new JButton("キャンセル");
    okbtn.setPreferredSize(new Dimension(100,25));
    clbtn.setPreferredSize(new Dimension(100,25));
    okbtn.addActionListener(this);
    okbtn.setActionCommand("ok");
    clbtn.addActionListener(this);
    clbtn.setActionCommand("cancel");
    panel6.setLayout(new FlowLayout(FlowLayout.RIGHT));
    panel6.add(okbtn);
    panel6.add(clbtn);

    panel1.add(panel6,BorderLayout.SOUTH);

    getContentPane().add(panel1);
  }

  void cmb_actionPerformed(ActionEvent e) {
    changeKeyStrokeText();
   //JComboBox cmb = (JComboBox)e.getSource();
   //System.out.println("jComboBox1_actionPerformed");
   //System.out.println((String)cmb.getSelectedItem());
 }

 private void changeKeyStrokeText () {
   keystroke = "";

   if (cbCtrl.isSelected() ) {
     if (keystroke.equals("") ) {
       keystroke = "Ctrl";
       keystroke2 = "C";
     }
     else {
       keystroke += "+Ctrl";
       keystroke2 += "C";
     }
   }

   if (cbAlt.isSelected() ) {
     if (keystroke.equals("") ) {
       keystroke = "Alt";
       keystroke2 = "A";
     }
     else {
       keystroke += "+Alt";
       keystroke2 += "A";
     }
   }

   if (cbShift.isSelected() ) {
     if (keystroke.equals("") ) {
       keystroke = "Shift";
       keystroke2 = "S";
     }
     else {
       keystroke += "+Shift";
       keystroke2 += "S";
     }
   }

   if (cbMeta.isSelected() ) {
     if (keystroke.equals("") ) {
       keystroke = "Meta";
       keystroke2 = "M";
     }
     else {
       keystroke += "+Meta";
       keystroke2 += "M";
     }
   }

   if (cbAltGraph.isSelected() ) {
     if (keystroke.equals("") ) {
       keystroke = "Alt Graph";
       keystroke2 = "G";
     }
     else {
       keystroke += "+Alt Graph";
       keystroke2 += "G";
     }
   }

   int index = cmbKeyCode.getSelectedIndex();
   String KeyCode = (String)cmbKeyCode.getSelectedItem();

   KeyCode = KeyCode.substring(KeyCode.indexOf("_")+1);
   if (keystroke.equals("") ) {
     keystroke = KeyCode;
     keystroke2 = KeyCode;
   }
   else {
     keystroke += "+" + KeyCode;
     keystroke2 += "+" + KeyCode;
   }

   KeyStroke.setText(keystroke);


 }
 // イベント処理
 public void actionPerformed(ActionEvent e){

   try{

     String action = e.getActionCommand();

     if (action.equals("ok")){
       ok();
     }
     else if(action.equals("cancel")){
       cancel();
     }
     else if(action.equals("Ctrl") || action.equals("Alt") ||
             action.equals("Shift") || action.equals("Meta") || action.equals("AltGraph") ){
       changeKeyStrokeText();
     }

   }catch(Exception e2){

   }

 }

 public void ok () {
    ret = true;
    dispose();
  }
  public void cancel () {
    ret = false;
    dispose();
  }

}