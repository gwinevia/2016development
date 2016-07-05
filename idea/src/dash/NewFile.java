package dash;

import java.awt.*;
import javax.swing.JPanel;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.*;

/**
 * <p>NewFile: </p>
 * <p>�V�KDash�t�@�C���A���[���Z�b�g�쐬���: </p>
 */

public class NewFile extends EnhancedDialog implements ActionListener{

  private IdeaMainFrame mainframe = null;
  private NewChoice NewChoiceWin  = null;
  private int kindNo = 0;
  private Project project = null;
  private JTextField nameArea = null;
  private Border border1;
  private String kindStr = "";

  public NewFile(IdeaMainFrame mainframe, Project prj, int inkindNo) {

    super( mainframe, "�V�K�v���W�F�N�g", true );
    project = prj;
    this.mainframe = mainframe;
		kindNo = inkindNo;

    try  {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    setTitle("�V�K�쐬");
    setSize(450,145);
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

		//�g���q�ݒ�
		if( kindNo == 2 ){
			kindStr = "dash";
      setTitle (mainframe.getBilingualMsg("0002") + " - " + mainframe.getBilingualMsg("0137"));
		}
		else if( kindNo == 3 ){
			kindStr = "rset";
      setTitle (mainframe.getBilingualMsg("0002") + " - " + mainframe.getBilingualMsg("0138"));
		}
    else if (kindNo == 4 ) {
      kindStr = "java";
      setTitle (mainframe.getBilingualMsg("0002") + " - " + mainframe.getBilingualMsg("0206"));
    }
    else if (kindNo == 5 ) {
      kindStr = "";
      setTitle (mainframe.getBilingualMsg("0002") + " - " + mainframe.getBilingualMsg("0207"));
    }

    //���O
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel(mainframe.getBilingualMsg("0063"));
    panel1.add(label1);
    panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel label2 = new JLabel(mainframe.getBilingualMsg("0140") + "�F");
    JLabel label2sub = new JLabel("." + kindStr + "�@�@");
    nameArea = new JTextField(20);

    JPanel panel2 = new JPanel(new BorderLayout());
    panel2.add(label2,BorderLayout.WEST);
    panel2.add(nameArea,BorderLayout.CENTER );
    panel2.add(label2sub,BorderLayout.EAST);

    JPanel SrchPanelTop = new JPanel (new GridLayout(1,1));
    SrchPanelTop.add(panel2);
    border1 = BorderFactory.createEmptyBorder(0,0,0,0);
    panel2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    SrchPanelTop.setBorder(border1);

    //OK�L�����Z���{�^��
    JPanel panel4 = new JPanel();
    JButton rebtn = new JButton(mainframe.getBilingualMsg("0141"));
    JButton okbtn = new JButton("OK");
    JButton clbtn = new JButton(mainframe.getBilingualMsg("0126"));
    rebtn.setPreferredSize(new Dimension(100,25));
    okbtn.setPreferredSize(new Dimension(100,25));
    clbtn.setPreferredSize(new Dimension(100,25));
    rebtn.addActionListener(this);
    rebtn.setActionCommand("return");
    okbtn.addActionListener(this);
    okbtn.setActionCommand("makefile");
    clbtn.addActionListener(this);
    clbtn.setActionCommand("close");
    panel4.setLayout(new FlowLayout(FlowLayout.RIGHT));
    panel4.add(rebtn);
    panel4.add(okbtn);
    panel4.add(clbtn);

    JPanel connectPanel = new JPanel();
    connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.Y_AXIS));

    connectPanel.add(panel1);
    connectPanel.add(SrchPanelTop);
    connectPanel.add(panel4);

    JPanel imagePanel = new JPanel ();
    JLabel imageLabel = new JLabel();
    if( kindNo == 2 ){
      imageLabel.setIcon(getImageIcon ( "resources/newfileDash.gif" ) );
    }
    else if( kindNo == 3 ){
      imageLabel.setIcon(getImageIcon ( "resources/newfileRset.gif" ) );
    }
    else if (kindNo == 4 ) {
      imageLabel.setIcon(getImageIcon ( "resources/newfileBp.gif" ) );
    }
    else if (kindNo == 5 ) {
      imageLabel.setIcon(getImageIcon ( "resources/newfileEtc.gif" ) );
    }
    imagePanel.add(imageLabel);

    JPanel basePanel = new JPanel (new BorderLayout() );
    basePanel.add(connectPanel, BorderLayout.NORTH);

    getContentPane().add(imagePanel, BorderLayout.WEST);
    getContentPane().add(basePanel, BorderLayout.CENTER);

  }

	/**
  * �C�x���g����
	* @param ActionEvent e<BR>
	*/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("makefile")){
        //�t�@�C���쐬����
        if(!makeFile()){
          return;
        }
        dispose();
      }
      else if(action.equals("close")){
        //�L�����Z���������ꂽ�ꍇ
        dispose();
      }
      else if(action.equals("return")){
        //�߂�������ꂽ�ꍇ
        dispose();

        NewChoiceWin = new NewChoice (this.mainframe,project );
        //NewChoiceWin.show();
     		NewChoiceWin.setVisible(true);
      }

    }catch(Exception e2){

    }

  }


  /**
  * dash�t�@�C���쐬����
  */
  private boolean makeFile( ) throws IOException , java.lang.Exception{

    try {

      Object[] options = { "OK" };
      String chkname = nameArea.getText();
      String chkdir  = project.getProjectPath();
      String ExstPrjfilePath = project.getProjectFileNameWithPath();

      if (kindNo == 4 ) {
        chkdir += "java_" + File.separator;
        File f = new File(chkdir);
        if (!f.isDirectory() ) {
          f.mkdirs();
        }
      }
      //���͊m�F
      if( chkname.equals("") ){
        JOptionPane.showOptionDialog(this,
            mainframe.getBilingualMsg("0081"),mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

      //�g���q��t����
      if (kindNo != 5 ) {
        chkname += "." + kindStr;
      }
      else {
        //.dash�A.rset�A.java
       if (chkname.toLowerCase().endsWith(".dash") || chkname.toLowerCase().endsWith(".rset") || chkname.toLowerCase().endsWith(".java") ) {
         JOptionPane.showOptionDialog(this,
             mainframe.getBilingualMsg("0219"),mainframe.getBilingualMsg("0129"),
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                    null, options, options[0]);
         return false;
       }

       while (true ) {
         if (chkname.endsWith(".") ) {
           chkname = chkname.substring(0,chkname.length()-1);
         }
         else {
           break;
         }
       }
      }

      //���ݓo�^����Ă���t�@�C�����擾
      FileReader f_in;
      BufferedReader b_in;
      String sLine = "";

      //�ǂݍ��ݏ���
      Vector exstList = new Vector();
      Vector SaveexstList = new Vector();

      try {
        //f_in = new FileReader(ExstPrjfilePath);
        //b_in = new BufferedReader(f_in);
        b_in = new BufferedReader(new InputStreamReader(
            new FileInputStream(ExstPrjfilePath),
            "JISAutoDetect"));

        while((sLine = b_in.readLine()) != null) {
          exstList.addElement(sLine.toLowerCase());
          SaveexstList.addElement(sLine);
        }
        b_in.close();

      } catch(Exception ex) {
        return false;
      }

      //�w�肵���v���W�F�N�g���ɓ������O�̃t�@�C�������݂��邩�𔻕�
      if (kindNo != 4 ) {
        for (int i=0; i<exstList.size(); i++ ) {
          String n = (String)exstList.elementAt(i);
          
//          if(n.toLowerCase().endsWith(chkname.toLowerCase()) ){
						if(n.toLowerCase().equals(chkname.toLowerCase()) ){

            String cstmStr = mainframe.getBilingualMsg("0082");
            cstmStr = cstmStr.replaceAll("filename",chkname);

            JOptionPane.showOptionDialog(this,
                cstmStr,mainframe.getBilingualMsg("0129"),
                                       JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                       null, options, options[0]);

            return false;
          }

        }
      }

      //�f�B���N�g���ɓ����̃t�@�C�������݂��邩�𔻕�
      String dirpath = chkdir;
      if (!chkdir.endsWith(File.separator)) {
          dirpath = chkdir + File.separator;
      }
      String FilePath = dirpath + chkname;

      File f = new File(FilePath);
      if(f.isFile()){

				String cstmStr = mainframe.getBilingualMsg("0083");

				//[path]�����ւ���
				int chkcnt = cstmStr.indexOf("path");
				cstmStr = cstmStr.substring(0,chkcnt) + dirpath + cstmStr.substring(chkcnt+4);
				cstmStr = cstmStr.replaceFirst("filename",chkname);

        JOptionPane.showOptionDialog(this,
            cstmStr,mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

      //�t�@�C���쐬����
      File fp  = new File ( FilePath );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.close ();

      if (kindNo != 4 ) {
        //�v���W�F�N�g�t�@�C���X�V
        File fp2  = new File ( ExstPrjfilePath );
        FileOutputStream fos2 = new FileOutputStream (fp2);
        PrintWriter pw2  = new PrintWriter (fos2);

        //�����̃t�@�C��
        for(int i=0; i < exstList.size(); i++){
          String writefilename = (String)SaveexstList.elementAt(i);
          pw2.println(writefilename);
        }

        //�V�K�t�@�C��
        pw2.println(chkname);
        pw2.close ();
      }
      else {
        //�v���W�F�N�g�t�@�C���X�V
        File fp2  = new File ( FilePath + "_inf" );
        FileOutputStream fos2 = new FileOutputStream (fp2);
        PrintWriter pw2  = new PrintWriter (fos2);

        //�V�K�t�@�C��
        pw2.println("[path]");
        pw2.println("current");
        pw2.println("[relation dash file]");
        pw2.close ();
      }

      mainframe.readProjectFile(ExstPrjfilePath);

    } catch ( Exception e ){
      return false;
    }

    return true;
  }


	/**
	* Enter�L�[����
	*/
  public void ok(){
    try {
      //�t�@�C���쐬����
      if(!makeFile()){
        return;
      }
    }
    catch(Exception e ) {}
    dispose();
  }

	/**
	* Esc�L�[����
	*/
  public void cancel(){
    dispose();
  }

	/**
	* ImageIcon����
	* @param String path �p�X<BR>
	*/
  private ImageIcon getImageIcon(String path) {
    java.net.URL url = this.getClass().getResource(path);
    return new ImageIcon(url);
  }

}