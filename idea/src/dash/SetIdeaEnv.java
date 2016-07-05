package dash;
import java.awt.*;
import javax.swing.JPanel;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.border.*;
import java.io.*;

/**
 * <p>�^�C�g��: </p>
 * <p>����: </p>
 * <p>���쌠: Copyright (c) 2003</p>
 * <p>��Ж�: </p>
 * @author ������
 * @version 1.0
 */

public class SetIdeaEnv extends EnhancedDialog   implements ActionListener {
  private IdeaMainFrame mainframe = null;
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JTextField txtFieldBaseProcessPlace = new JTextField(40);
  private JTextField txtFieldNameServerAdr = new JTextField(40);
  private JTextField txtFieldScriptsFolder = new JTextField(40);
  private JComboBox cmbWpCnt = new JComboBox();
  private JButton btnOk = new JButton("OK");
  private JButton btnCancel = new JButton("�L�����Z��");
  private boolean result = false;

  public SetIdeaEnv(IdeaMainFrame mainframe) {
    super( mainframe, "Idea���ݒ�", true );
    try  {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    setTitle("Idea���ݒ�");
    setSize(400,350);
    this.setResizable(false);
    // �t���[���T�C�Y
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    panel1 = new JPanel() {
        Insets insets = new Insets(0, 4, 0, 0);
        public Insets getInsets() {
          return insets;
        }
    };
    panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));

    getContentPane().setLayout(new BorderLayout());
    panel1.add(createSetBaseProcessPlacePanel());
    panel1.add(createSetNameServerAdrPanel());
    panel1.add(createSetScriptsFolderPanel());
    panel1.add(createSetWpCntPanel());

    JPanel panel2 = new JPanel() {
        Insets insets = new Insets(0, 4, 0, 0);
        public Insets getInsets() {
          return insets;
        }
    };

    btnOk.setActionCommand("OK");
    btnOk.addActionListener(this);
    btnCancel.setActionCommand("CANCEL");
    btnCancel.addActionListener(this);
    panel2.add(btnOk);
    panel2.add(btnCancel);
    getContentPane().add(panel1, BorderLayout.CENTER);
    getContentPane().add(panel2, BorderLayout.SOUTH);

  }

  private JPanel createSetBaseProcessPlacePanel() {
    JPanel panel1 = new JPanel (new BorderLayout() );
    JPanel panel2 = new JPanel (new BorderLayout() );
    JPanel panel3 = new JPanel (new GridLayout(1,1) );


    panel2.add(txtFieldBaseProcessPlace,BorderLayout.NORTH);
    txtFieldBaseProcessPlace.setText(System.getProperty("dash.userClassPath"));
    JLabel lbl = (JLabel)panel3.add (new JLabel("�@���K�{���ڂł�"));
    lbl.setForeground(Color.red);

    panel1.add(panel3, BorderLayout.NORTH);
    panel1.add(panel2, BorderLayout.CENTER);
    panel1.setBorder(new TitledBorder("�x�[�X�v���Z�X�Ɗg�����\�b�h��ǂݍ��ޏꏊ"));
    return panel1;
  }

  private JPanel createSetNameServerAdrPanel() {
    JPanel panel1 = new JPanel (new BorderLayout() );
    JPanel panel2 = new JPanel (new BorderLayout() );
    JPanel panel3 = new JPanel (new GridLayout(4,1) );

    panel2.add(txtFieldNameServerAdr,BorderLayout.NORTH);
    txtFieldNameServerAdr.setText(System.getProperty("dash.nameserver"));


    panel3.add (new JLabel("�y�ݒ��z"));
    panel3.add (new JLabel("�@�z�X�g���Fleo.suga.net.it-chiba.ac.jp"));
    panel3.add (new JLabel("�@IP�A�h���X�F192.168.101.xxx"));
    JLabel lbl = (JLabel)panel3.add (new JLabel("�@���l�[���T�[�o�[�𗘗p���Ȃ��ꍇ�́A���͂��Ȃ��ŉ�����"));
    lbl.setForeground(Color.red);


    panel1.add(panel3, BorderLayout.NORTH);
    panel1.add(panel2, BorderLayout.CENTER);
    panel1.setBorder(new TitledBorder("�l�[���T�[�o�̃z�X�g���܂���IP�A�h���X"));
    return panel1;
  }

  private JPanel createSetScriptsFolderPanel() {
    JPanel panel1 = new JPanel (new BorderLayout() );
    JPanel panel2 = new JPanel (new BorderLayout() );
    JPanel panel3 = new JPanel (new GridLayout(2,1) );

    txtFieldScriptsFolder.setText(System.getProperty("dash.r.path"));
    panel2.add(txtFieldScriptsFolder,BorderLayout.NORTH);


    //panel3.add (new JLabel("�y�ݒ��z"));
    //panel3.add (new JLabel("�@Y\\�z�X�g���Fleo.suga.net.it-chiba.ac.jp"));
    //panel3.add (new JLabel("�@IP�A�h���X�F192.168.101.xxx"));
    JLabel lbl = (JLabel)panel3.add (new JLabel("�@���l�[���T�[�o�[�𗘗p���Ȃ��ꍇ�́A���͂��Ȃ��ŉ�����"));
    lbl.setForeground(Color.red);


    //panel1.add(panel3, BorderLayout.NORTH);
    panel1.add(panel2, BorderLayout.CENTER);
    panel1.setBorder(new TitledBorder("���|�W�g���ғ�PC�̃G�[�W�F���g�t�@�C���̂���ꏊ"));
    return panel1;
  }

  private JPanel createSetWpCntPanel() {
    //JPanel panel1 = new JPanel (new BorderLayout() );
    JPanel panel1 = new JPanel ( );

    //JPanel panel2 = new JPanel (new BorderLayout() );
    //JPanel panel3 = new JPanel (new GridLayout(2,1) );

    Vector vecCnt = new Vector();
    for(int i=1; i<=5; i++ ) {
      vecCnt.addElement(new Integer(i).toString());
    }

    cmbWpCnt = new JComboBox(vecCnt);
    cmbWpCnt.setSelectedItem(System.getProperty("dash.wp.cnt"));
    //panel2.add(cmbWpCnt,BorderLayout.NORTH);
    //panel3.add(new JLabel("��"),BorderLayout.NORTH);


    //panel1.add(panel2, BorderLayout.WEST);
    //panel1.add(panel3, BorderLayout.CENTER);

    panel1.add(cmbWpCnt);
    panel1.add(new JLabel("��"));

    panel1.setBorder(new TitledBorder("���[�N�v���[�X�̐��i�ő�T�܂Ŏw��\�j"));
    return panel1;
  }

  // �C�x���g����
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
     String s = e2.getLocalizedMessage();
     s = e2.getMessage();
     s = e2.toString();
     s = "!";
   }

 }

 public void ok() {
    /**@todo ���� dash.EnhancedDialog abstract ���\�b�h������*/
   //private JTextField txtFieldNameServerAdr = new JTextField(40);
   //private JTextField txtFieldScriptsFolder = new JTextField(40);
   //private JComboBox cmbWpCnt = new JComboBox();

   // �f�t�H���g�t�@�C���̓ǂݍ���
   DashDefaults dashDefaults = new DashDefaults();
   Properties properties = dashDefaults.loadDefaults();

String s = properties.getProperty("dash.userClassPath");
if (properties.remove("dash.r.path111") == null ) {
  int a = 1;
}
   Object[] options = { "OK" };
   //---------------------------------------------------------------------------
   // �u�x�[�X�v���Z�X�Ɗg�����\�b�h��ǂݍ��ޏꏊ�v�̃`�F�b�N
   //---------------------------------------------------------------------------
   try {
     if (!txtFieldBaseProcessPlace.getText().equals("") ) {
       StringTokenizer st = new StringTokenizer(txtFieldBaseProcessPlace.getText(),";");
       int cnt = 0;
       while (st.hasMoreTokens()) {
         String data = st.nextToken();


         File file = new File(data);
         if (!file.isDirectory()){
           JOptionPane.showOptionDialog(this,
                "\"�x�[�X�v���Z�X�Ɗg�����\�b�h��ǂݍ��ޏꏊ\"���Q�Ƃ��邱�Ƃ��o���܂���B\n���͂Ɍ�肪�Ȃ����m�F���ĉ������B\n" + data ,"�G���[",
                 JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                 null, options, options[0]);
           return;

         }
       }
       properties.setProperty("dash.userClassPath",txtFieldBaseProcessPlace.getText());
     }
     else {
       JOptionPane.showOptionDialog(this,
           "\"�x�[�X�v���Z�X�Ɗg�����\�b�h��ǂݍ��ޏꏊ\"�������͂ł��B","�G���[",
                                  JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                  null, options, options[0]);
       return;

     }
   }
   catch (Exception e ) {
     e.getLocalizedMessage();
     JOptionPane.showOptionDialog(this,
         "\"�x�[�X�v���Z�X�Ɗg�����\�b�h��ǂݍ��ޏꏊ\"���Q�Ƃ��邱�Ƃ��o���܂���B\n���͂Ɍ�肪�Ȃ����m�F���ĉ������B","�G���[",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                null, options, options[0]);

     return;
   }
   //---------------------------------------------------------------------------
   // �u�l�[���T�[�o�̃z�X�g���܂���IP�A�h���X�v�̃`�F�b�N
   //---------------------------------------------------------------------------

   if (!txtFieldNameServerAdr.getText().equals("") ) {
     properties.setProperty("dash.nameserver",txtFieldNameServerAdr.getText());
   }
   else {
     if (properties.getProperty("dash.nameserver") != null ) {
       properties.remove("dash.nameserver");
       System.getProperties().remove("dash.nameserver");
     }
   }
   //---------------------------------------------------------------------------
   // �u���|�W�g���ғ�PC�̃G�[�W�F���g�t�@�C���̂���ꏊ�v�̃`�F�b�N
   //---------------------------------------------------------------------------
   try {
     if (!txtFieldScriptsFolder.getText().equals("") ) {
       File file = new File(txtFieldScriptsFolder.getText());
       if (!file.isDirectory()){
         JOptionPane.showOptionDialog(this,
             "\"���|�W�g���ғ�PC�̃G�[�W�F���g�t�@�C���̂���ꏊ\"���Q�Ƃ��邱�Ƃ��o���܂���B\n���͂Ɍ�肪�Ȃ����m�F���ĉ������B","�G���[",
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                    null, options, options[0]);
         return;

       }
       properties.setProperty("dash.r.path",txtFieldScriptsFolder.getText());
     }
     else {

       if (properties.remove("dash.r.path") != null ) {
         properties.remove("dash.r.path");
         System.getProperties().remove("dash.r.path");
       }
     }

   }
   catch (Exception e ) {
     e.getLocalizedMessage();
     JOptionPane.showOptionDialog(this,
         "\"���|�W�g���ғ�PC�̃G�[�W�F���g�t�@�C���̂���ꏊ\"���Q�Ƃ��邱�Ƃ��o���܂���B\n���͂Ɍ�肪�Ȃ����m�F���ĉ������B","�G���[",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                null, options, options[0]);

     return;
   }

   if (System.getProperty("dash.userClassPath") != null ) {
     System.getProperties().remove("dash.userClassPath");
   }
   if (System.getProperty("dash.r.path") != null ) {
     System.getProperties().remove("dash.r.path");
   }
   if (System.getProperty("dash.nameserver") != null ) {
     System.getProperties().remove("dash.nameserver");
   }
   if (System.getProperty("dash.wp.cnt") != null ) {
     System.getProperties().remove("dash.wp.cnt");
   }
   //---------------------------------------------------------------------------
   // �u���[�N�v���[�X�̐��v�̃`�F�b�N
   //---------------------------------------------------------------------------
   properties.setProperty("dash.wp.cnt", (String)cmbWpCnt.getSelectedItem());

s = properties.getProperty("dash.userClassPath");
   try {
     FileOutputStream fos = new FileOutputStream( "properties/idea.properties" );

     BufferedWriter awriter;
     awriter = new BufferedWriter(new OutputStreamWriter(fos, "8859_1"));

     properties.store( fos, "idea Properties" );
     //properties.store( fos, "idea Properties" );
     fos.close();
   } catch (FileNotFoundException e) {
     System.err.println("warning: idea.properties not found");
   } catch (IOException e) {
     e.printStackTrace();
   } catch (Exception e) {
     e.printStackTrace();
   }

   // �f�t�H���g�t�@�C���̓ǂݒ���

   //System.getProperties().clear();
   //dashDefaults = new DashDefaults();
   dashDefaults.loadDefaults();
   result = true;
   dispose();

 }
 public void cancel() {
    /**@todo ���� dash.EnhancedDialog abstract ���\�b�h������*/
    result = false;
    dispose();
 }

 public boolean getResult() {
   return result;
 }

}