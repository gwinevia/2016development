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
 * <p>�^�C�g���F�����t�@�C���쐬 </p>
 * <p>����: �G�[�W�F���g�t�@�C���̕������쐬����_�C�A���O</p>
 * <p>���쌠: Copyright (c) 2003</p>
 * <p>��Ж�:cosmosweb </p>
 * @author nakagawa
 * @version 1.0
 */

public class CopyFile extends EnhancedDialog implements ActionListener{
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** �e�t���[�� */
  private IdeaMainFrame mainframe = null;

  /** �I���W�i���t�@�C������ */
  private String orgFileName = "";
  private String orgFileNameWithPath = "";

  /** �t�@�C���̊g���q */
  private String ext = "";

  private JPanel connectPanel = new JPanel();

  private String Mode = "CreateAliasFile";//1:�����쐬�@2:���O�̕ύX
  public String NewName = "";

  /****************************************************************************
   * �R���X�g���N�^
   * @param mainframe IdeaMainFrame���󂯎��
   * @param prj  �v���W�F�N�g
   * @param orgFileName  �I���W�i���t�@�C����
   * @return �Ȃ�
   ****************************************************************************/
  public CopyFile(IdeaMainFrame mainframe, String Mode, String Title, Project prj, String orgFileName, String orgFileNameWithPath) {
    super(mainframe, Title, true );
    project = prj;
    this.Mode = Mode;
    this.mainframe = mainframe;
    if (orgFileName.indexOf(".") != -1 ) {
      this.orgFileName = orgFileName.substring(0,orgFileName.indexOf("."));
      this.ext = orgFileName.substring(orgFileName.indexOf("."));
    }
    else {
      this.orgFileName = orgFileName;
      this.ext = "";
    }
    this.orgFileNameWithPath = orgFileNameWithPath;
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  /** �v���W�F�N�g */
  private Project project = null;

  /** �����t�@�C�������̓G���A */
  private JTextField nameArea = null;

  /** �I���W�i���t�@�C�����\���G���A */
  private JTextField orgnameArea = null;

  private Border border1;

  /****************************************************************************
   * ����������
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void jbInit() throws Exception {

    // �_�C�A���O�̃^�C�g����ݒ�
    //setTitle(mainframe.getBilingualMsg("0031"));

    // �t���[���T�C�Y
    setSize(350,160);

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

    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel(mainframe.getBilingualMsg("0160"));
    panel1.add(label1);
    panel1.setLayout(new FlowLayout(FlowLayout.LEFT));


    // �����t�@�C������\���A���A���쐬���A�t���[����ɔz�u����p�l���ɒǉ�����
    String addSpace = "";
    if( mainframe.getBilingualMsg("0161").equals("To") ){
			addSpace = "     ";
    }

    JLabel label2 = null;
    if (Mode.equals("CreateAliasFile") ) {
      label2 = new JLabel(mainframe.getBilingualMsg("0161") + addSpace + "�F");
    }
    else if (Mode.equals("ChangeName") ) {
      label2 = new JLabel(mainframe.getBilingualMsg("0213") + addSpace + "�F");
    }
    JLabel label2sub = new JLabel(ext + "�@�@");
    nameArea = new JTextField(20);
    nameArea.setText(orgFileName);
    JPanel panel2 = new JPanel(new BorderLayout());
    panel2.add(label2,BorderLayout.WEST);
    panel2.add(nameArea,BorderLayout.CENTER );
    panel2.add(label2sub,BorderLayout.EAST);

    // �I���W�i���t�@�C������\���A���A���쐬���A�t���[����ɔz�u����p�l���ɒǉ�����
    JLabel label3 = null;
    if (Mode.equals("CreateAliasFile") ) {
      label3 = new JLabel(mainframe.getBilingualMsg("0162") + "�F");
    }
    else if (Mode.equals("ChangeName") ) {
      label3 = new JLabel(mainframe.getBilingualMsg("0212") + "�F");
    }
    JLabel label3sub = new JLabel(ext+ "�@�@");
    orgnameArea = new JTextField(20);
    orgnameArea.setText(orgFileName);
    orgnameArea.setEditable(false);
    orgnameArea.setBackground(Color.lightGray);

    JPanel panel3 = new JPanel(new BorderLayout());
    panel3.add(label3,BorderLayout.WEST);
    panel3.add(orgnameArea,BorderLayout.CENTER );
    panel3.add(label3sub,BorderLayout.EAST);

    JPanel SrchPanelTop = new JPanel (new GridLayout(2,1));
    SrchPanelTop.add(panel3);
    SrchPanelTop.add(panel2);
    border1 = BorderFactory.createEmptyBorder(0,0,0,0);
    panel2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    panel3.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    SrchPanelTop.setBorder(border1);

    //OK�L�����Z���{�^��
    JPanel panel4 = new JPanel();
    JButton okbtn = new JButton("OK");
    JButton clbtn = new JButton(mainframe.getBilingualMsg("0126"));
    okbtn.setPreferredSize(new Dimension(100,25));
    clbtn.setPreferredSize(new Dimension(100,25));
    okbtn.addActionListener(this);
    okbtn.setActionCommand("makefile");
    clbtn.addActionListener(this);
    clbtn.setActionCommand("close");
    panel4.setLayout(new FlowLayout(FlowLayout.RIGHT));
    panel4.add(okbtn);
    panel4.add(clbtn);


    connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.Y_AXIS));
    connectPanel.add(panel1);
    connectPanel.add(SrchPanelTop);
    connectPanel.add(panel4);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(connectPanel, BorderLayout.NORTH);

    //this.setResizable(false);

  }


  /****************************************************************************
   * �C�x���g����
   * @param e �A�N�V�����C�x���g
   * @return �Ȃ�
   ****************************************************************************/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("makefile")){
        ok();
      }
      else if(action.equals("close")){
        cancel();
      }

    }catch(Exception e2){

    }

  }



  /****************************************************************************
   * �����t�@�C���쐬����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private boolean makeFile( ) throws IOException , java.lang.Exception{

    try {

      String chkname = nameArea.getText() + ext;//".dash";
      String chkdir  = project.getProjectPath();
      String ExstPrjfilePath = project.getProjectFileNameWithPath();
      String orgName = orgnameArea.getText() + ext;

      //���͊m�F

      // �����t�@�C���������͂���Ă��邩�H
      if( nameArea.getText().equals("") ){

				Object[] options = { "OK" };
				JOptionPane.showOptionDialog(this,
											mainframe.getBilingualMsg("0076"),mainframe.getBilingualMsg("0129"),
											JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
											null, options, options[0]);

        return false;
      }

      // �I���W�i���t�@�C�����ƕ����t�@�C�����������łȂ����H
      if (Mode.equals("CreateAliasFile") ) {
        if (chkname.equals(orgnameArea.getText() + ext)) {

          Object[] options = { "OK" };
          JOptionPane.showOptionDialog(this,
                        mainframe.getBilingualMsg("0077"),mainframe.getBilingualMsg("0129"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                        null, options, options[0]);

          return false;
        }
      }
      else {
        if (chkname.equals(orgnameArea.getText() + ext)) {
          return true;
        }

      }



      //���݁A�v���W�F�N�g�ɓo�^����Ă���t�@�C�����擾
      FileReader f_in;
      BufferedReader b_in;
      String sLine = "";

      //�ǂݍ��ݏ���
      Vector exstList = new Vector();
      Vector exstList2 = new Vector();

      try {
        f_in = new FileReader(ExstPrjfilePath);
        //b_in = new BufferedReader(f_in);
        b_in = new BufferedReader(new InputStreamReader(
                                                new FileInputStream(ExstPrjfilePath),
                                                "JISAutoDetect"));

        while((sLine = b_in.readLine()) != null) {
          exstList.addElement(sLine.toLowerCase());
          exstList2.addElement(sLine);
        }
        b_in.close();

      } catch(Exception ex) {
        return false;
      }

      //�w�肵���v���W�F�N�g���ɓ������O�̃t�@�C�������݂��邩�𔻕�
      //���݂���ꍇ�̓G���[�Ƃ���
      if(exstList.indexOf(chkname.toLowerCase()) != -1){

				String cstmStr = mainframe.getBilingualMsg("0078");
				cstmStr = cstmStr.replaceAll("filename",chkname);

				Object[] options = { "OK" };
				JOptionPane.showOptionDialog(this,
											cstmStr,mainframe.getBilingualMsg("0129"),
											JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
											null, options, options[0]);

        return false;
      }

      for (int i=0; i<exstList.size(); i++ ) {
        String filename = (String)exstList.elementAt(i);
        if (filename.endsWith(File.separator + chkname) ) {
          String cstmStr = mainframe.getBilingualMsg("0078");
          cstmStr = cstmStr.replaceAll("filename",chkname);

          Object[] options = { "OK" };
          JOptionPane.showOptionDialog(this,
                        cstmStr,mainframe.getBilingualMsg("0129"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                        null, options, options[0]);

          return false;
        }
      }

      // �v���W�F�N�g�ł͊Ǘ�����Ă��Ȃ����A�v���W�F�N�g�̊Ǘ�����t�H���_���ɓ����̃t�@�C�����������Ƃ���
      // ����
      //File file = new File (project.getProjectPath() + nameArea.getText() + ext/*".dash"*/);
      String savePath = orgFileNameWithPath.substring(0,orgFileNameWithPath.lastIndexOf(File.separator));
      if (!savePath.endsWith(File.separator) ) {
        savePath += File.separator;
      }
      File file = new File (savePath + nameArea.getText() + ext/*".dash"*/);
      if (file.exists() ) {
        Object[] options = { "OK", "CANCEL" };

				String cstmStr = mainframe.getBilingualMsg("0079");
				//cstmStr = cstmStr.replaceAll("path",project.getProjectPath());
        cstmStr = cstmStr.replaceAll("path",savePath);
				cstmStr = cstmStr.replaceFirst("filename",nameArea.getText() + ext);

        int ret = JOptionPane.showOptionDialog(this,
            cstmStr,mainframe.getBilingualMsg("0031"),
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
            null, options, options[0]);

//        int ret = JOptionPane.showOptionDialog(this,
//            nameArea.getText() + ext + " �́A����"+ project.getProjectPath()+"�ɑ��݂��܂��B�u�������܂����H","�����쐬",
//            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
//            null, options, options[0]);

        int r = ret;

        if (ret == 1 ) {
          return false;
        }

      }
      // �R�s�[�������s
      String orgfile = orgFileNameWithPath;
      String makfile = savePath + nameArea.getText() + ext;//".dash";

      if (Mode.equals("CreateAliasFile") ) {
        //String orgfile = project.getProjectPath() + orgnameArea.getText() + ext;//".dash";
        //String makfile = project.getProjectPath() + nameArea.getText() + ext;//".dash";
        //String orgfile = orgFileNameWithPath;
        //String makfile = savePath + nameArea.getText() + ext;//".dash";


        String[] copyfiles = {orgfile,makfile};
        filecopy(copyfiles);
      }
      else {
        File f = new File(orgfile);
        f.renameTo(new File(makfile) );
        if (chkname.toLowerCase().endsWith(".java")){
          String orginf = project.getProjectPath().toLowerCase() + "java_" + File.separator + orgName + "_inf";
          String newinf = project.getProjectPath().toLowerCase() + "java_" + File.separator + chkname + "_inf";

          f = new File(orginf);
          f.renameTo(new File(newinf) );

          Vector vecCont = new Vector();
          try {
            //f_in = new FileReader(newinf);
            b_in = new BufferedReader(new InputStreamReader(
                                                    new FileInputStream(newinf),
                                                    "JISAutoDetect"));

            int kind = 0;
            while((sLine = b_in.readLine()) != null) {
              if (sLine.equals("[path]") ) {
                kind = 1;
                vecCont.addElement(sLine);
              }
              else if (sLine.equals("[relation dash file]")) {
                kind = 2;
                vecCont.addElement(sLine);
              }
              else {
                if (kind == 1 ) {
                  if (sLine.equals("current") ) {
                    vecCont.addElement(sLine);
                  }
                  else {
                    vecCont.addElement(makfile);
                  }
                }
                else {
                  vecCont.addElement(sLine);
                }
              }
            }
            b_in.close();


            File fp2  = new File ( newinf );
            FileOutputStream fos2 = new FileOutputStream (fp2);
            PrintWriter pw2  = new PrintWriter (fos2);

            //�����̃t�@�C��
            for(int j=0; j < vecCont.size(); j++){
              String s = (String)vecCont.elementAt(j);
              pw2.println(s);
            }
            pw2.close ();
            fos2.close();

          } catch(Exception ex) {
            return false;
          }

        }

      }

      //�v���W�F�N�g�t�@�C���X�V
      //�����t�@�C�����v���W�F�N�g�ɒǉ�����
      File fp2  = new File ( ExstPrjfilePath );
      FileOutputStream fos2 = new FileOutputStream (fp2);
      PrintWriter pw2  = new PrintWriter (fos2);

      //�����̃t�@�C��
      for(int i=0; i < exstList.size(); i++){
        String writefilename = (String)exstList2.elementAt(i);
        if (Mode.equals("ChangeName") ) {
          if (writefilename.equals(orgfile.substring(project.getProjectPath().length())) ) {
            writefilename = makfile.substring(project.getProjectPath().length());
          }
        }
        pw2.println(writefilename);
      }

      //�V�K�t�@�C��
      //pw2.println(chkname);

      String makfilewk = makfile;
      if (Mode.equals("CreateAliasFile") ) {
        if (!chkname.toLowerCase().endsWith(".java") ) {
          makfile = makfile.substring(project.getProjectPath().length());
          pw2.println(makfile);
        }
      }
      pw2.close ();

      if (chkname.toLowerCase().endsWith(".java") && Mode.equals("CreateAliasFile") ) {
        //�����t�@�C�����v���W�F�N�g�ɒǉ�����
        fp2  = new File ( project.getProjectPath() + "java_" + File.separator + chkname + "_inf" );
        fos2 = new FileOutputStream (fp2);
        pw2  = new PrintWriter (fos2);
        pw2.println("[path]");
        if (makfile.toLowerCase().equals(project.getProjectPath().toLowerCase() + "java_" + File.separator + chkname.toLowerCase()) ) {
          pw2.println("current");
        }
        else {
          pw2.println(makfile);
        }
        pw2.println("[relation dash file]");
        pw2.close();
        //C:\Amoeba\Source\AmoebaWeb\StockInfo\myprojects\StockInfo\Utils\Utils.java
        //[relation dash file]

      }

      //�v���W�F�N�g�t�@�C���̓ǂݒ���
      //mainframe.readProjectFile(ExstPrjfilePath);

      if (Mode.equals("ChangeName") ) {
        NewName = makfile;

        Vector vecContents = new Vector();
        //String sLine = "";
        //String orgName = orgnameArea.getText() + ext;
        // Java_inf�̕ύX
        File current_dir = new File(project.getProjectPath() + "java_");
        String file_list[] = current_dir.list();
        if (file_list != null ) {
          for (int i=0; i<file_list.length; i++ ) {
            if (file_list[i].endsWith(".java_inf") ) {
              vecContents.clear();
              File current_file = new File(file_list[i]);
              try {
                f_in = new FileReader(project.getProjectPath() + "java_" + File.separator + file_list[i]);
                b_in = new BufferedReader(new InputStreamReader(
                                                        new FileInputStream(project.getProjectPath() + "java_" + File.separator + file_list[i]),
                                                        "JISAutoDetect"));

                while((sLine = b_in.readLine()) != null) {
                  //chkname.equals(orgnameArea.getText() + ext)
                  if (!sLine.toLowerCase().equals(orgName.toLowerCase()) ) {
                    vecContents.addElement(sLine);
                  }
                  else {
                    vecContents.addElement(chkname);
                  }
                }
                b_in.close();


                fp2  = new File ( project.getProjectPath() + "java_" + File.separator + file_list[i] );
                fos2 = new FileOutputStream (fp2);
                pw2  = new PrintWriter (fos2);

                //�����̃t�@�C��
                for(int j=0; j < vecContents.size(); j++){
                  String s = (String)vecContents.elementAt(j);
                  pw2.println(s);
                }
                pw2.close ();
                fos2.close();

              } catch(Exception ex) {
                return false;
              }
            }
            //File current_file = new File(f,file_list[i]);
          }


        }

      }

    } catch ( Exception e ){
      return false;
    }

    return true;
  }

  /****************************************************************************
   * �t�@�C���R�s�[
   * @param args[]�@0:�I���W�i���t�@�C�� 1:�����t�@�C��
   * @return �Ȃ�
   ****************************************************************************/
  public static void filecopy(String args[]){
    try{
      String  strBuff;
      File fp  = new File ( args[1] );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.close ();

      /* �I�u�W�F�N�g�̐��� */
      BufferedReader  brInFile = new BufferedReader(new FileReader(args[0]));
      PrintWriter     pwOutFile = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));

      /* �ǂݍ��ݏ��� */
      while((strBuff = brInFile.readLine()) != null){
        pwOutFile.println(strBuff);
      }

      /* �N���[�Y���� */
      brInFile.close();
      pwOutFile.close();
    }
    catch(Exception e){
      System.err.println("ERROR : " + e);
    }
  }

  /****************************************************************************
   * OK����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
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

  /****************************************************************************
   * Cancel����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void cancel(){
    dispose();
  }



}