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
 * <p>NewProject: </p>
 * <p>�V�K�v���W�F�N�g�쐬���: </p>
 */

public class NewProject extends EnhancedDialog implements ActionListener,KeyListener{

  private IdeaMainFrame parentframe;
  private NewChoice NewChoiceWin  = null;
  private Project project = null;
  private JTextField dirArea = null;
  private JTextField nameArea = null;
  private Border border1;
  private SearchPanel srchpanel;
  JFileChooser fdlg = null;

  public NewProject(IdeaMainFrame frame, Project prj ) {

    super( frame, "�V�K�v���W�F�N�g", true );
    parentframe = frame;
	  project = prj;

    try  {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    setTitle(parentframe.getBilingualMsg("0084"));
    setSize(480,165);

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

		// �����ݒ�
		String defFileName  = "";
		String defDirectory = "";

		// �f�t�H���g�ŕ\������f�B���N�g�����擾
		String defdir = srchpanel.getDefaultDir();

		// �f�t�H���g�ŕ\������v���W�F�N�g�����擾
    // �f�t�H���g���� Untitled(n) �Ƃ���B

		try{
			//scripts�t�H���_���쐬
	 	  File f_scripts = new File(defdir);
			f_scripts.mkdirs();
		}
		catch( Exception e ){
		}

 	  File f = new File(defdir);
   	String[] FileList = f.list();

		int fileno = 0;
    for (int i=0; i<FileList.length; i++ ) {
      String chkname = FileList[i];

			if( chkname.toLowerCase().indexOf("untitled") != -1 ){
				int maxno = 0;

				try{
					maxno = new Integer( chkname.substring(8) ).intValue();
				}
				catch ( Exception e ){
					continue;
				}

				//��r
				if( maxno > fileno){
					//�ő�l�̎擾
					fileno = maxno;
				}
			}
    }

		//�ő�l�擾
		fileno++;

		// �����l�擾
		String filenoStr = new Integer(fileno).toString();
		String defname = "Untitled" + filenoStr;
		defDirectory = defdir + defname;
		defFileName  = defname;

		// ��ʍ쐬
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel(parentframe.getBilingualMsg("0142"));
    panel1.add(label1);
    panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

		//space
		String addSpaceStr = "";
		if( parentframe.getBilingualMsg("0140").equals("Name") ){
			addSpaceStr = "     ";
		}else{
			addSpaceStr = "�@�@�@�@";
		}

    JLabel label2 = new JLabel(parentframe.getBilingualMsg("0140") + addSpaceStr + "�F");
    JLabel label2sub = new JLabel(".dpx�@�@");
    JLabel label3 = new JLabel(parentframe.getBilingualMsg("0143") + "�F");
    nameArea = new JTextField(20);
    dirArea  = new JTextField(20);
		nameArea.addKeyListener(this);
    dirArea.setBackground(Color.white);
    JButton dirBtn = new JButton("�c");
    dirBtn.setPreferredSize(new Dimension(50,22));
    dirBtn.addActionListener(this);
    dirBtn.setActionCommand("chicdir");

		//�����l�Z�b�g
		nameArea.setText(defFileName);
		dirArea.setText(defDirectory);

    // ���O
    JPanel panel2 = new JPanel(new BorderLayout());
    panel2.add(label2,BorderLayout.WEST);
    panel2.add( nameArea,BorderLayout.CENTER );
    panel2.add(label2sub,BorderLayout.EAST);

    //�f�B���N�g��
    JPanel panel3 = new JPanel(new BorderLayout());
    panel3.add(label3,BorderLayout.WEST);
    panel3.add(dirArea,BorderLayout.CENTER);
    panel3.add(dirBtn,BorderLayout.EAST);

    JPanel SrchPanelTop = new JPanel (new GridLayout(2,1));
    SrchPanelTop.add(panel2);
    SrchPanelTop.add(panel3);
    border1 = BorderFactory.createEmptyBorder(0,0,0,0);
    panel2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    panel3.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    SrchPanelTop.setBorder(border1);

    //OK�L�����Z���{�^��
    JPanel panel4 = new JPanel();
    JButton rebtn = new JButton(parentframe.getBilingualMsg("0141"));
    JButton okbtn = new JButton("OK");
    JButton clbtn = new JButton(parentframe.getBilingualMsg("0126"));
    rebtn.setPreferredSize(new Dimension(100,25));
    okbtn.setPreferredSize(new Dimension(100,25));
    clbtn.setPreferredSize(new Dimension(100,25));
    rebtn.addActionListener(this);
    rebtn.setActionCommand("return");
    okbtn.addActionListener(this);
    okbtn.setActionCommand("makedpx");
    clbtn.addActionListener(this);
    clbtn.setActionCommand("close");
    panel4.setLayout(new FlowLayout(FlowLayout.RIGHT));

 		if( project != null ){
			//���ڐV�K�v���W�F�N�g���I�����ꂽ�ꍇ�͖߂�{�^���͕t���Ȃ�
			//�V�K����̏ꍇ�̂ݕt����
			panel4.add(rebtn);
 		}

    panel4.add(okbtn);
    panel4.add(clbtn);

    JPanel connectPanel = new JPanel();
    connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.Y_AXIS));
    connectPanel.add(panel1);
    connectPanel.add(SrchPanelTop);
    connectPanel.add(panel4);

    JPanel imagePanel = new JPanel ();
    JLabel imageLabel = new JLabel();
    imageLabel.setIcon(getImageIcon ( "resources/newfileProject.gif" ) );
    imagePanel.add(imageLabel);

    JPanel basePanel = new JPanel (new BorderLayout() );
    basePanel.add(connectPanel, BorderLayout.NORTH);

    getContentPane().add(imagePanel, BorderLayout.WEST);
    getContentPane().add(basePanel, BorderLayout.CENTER);

  }

	public void keyTyped(KeyEvent e){
    String KeyCharStr = "";
    KeyCharStr = new String().valueOf(e.getKeyChar());
    if (KeyCharStr != null ) {
      if(KeyCharStr.equals(File.separator)){
        e.consume();
      }
    }
  }
  public void keyPressed(KeyEvent e){
    String KeyCharStr = "";
    KeyCharStr = new String().valueOf(e.getKeyChar());
    if (KeyCharStr != null ) {
      if(KeyCharStr.equals(File.separator)){
        e.consume();
      }
    }
	}
	public void keyReleased(KeyEvent e){
    String KeyCharStr = "";
    KeyCharStr = new String().valueOf(e.getKeyChar());
    if (KeyCharStr != null ) {
      if(KeyCharStr.equals(File.separator)){
        e.consume();
      }
    }

		Interlock();
	}

	/**
	* �e�L�X�g�A������
	*/
	private void Interlock(){

		String name = nameArea.getText();
		String dir  = dirArea.getText();

		//�f�B���N�g���G���A�A������
		//int divcnt = dir.lastIndexOf("\\");
    int divcnt = dir.lastIndexOf(File.separator);
		if( divcnt != -1){
			String divStr = dir.substring(0,divcnt+1) + name;
			dirArea.setText(divStr);
		}
	}

	/**
	* FileChooser�\��
	*/
  public boolean createFileChooser() {

    fdlg = new JFileChooser(SearchPanel.getDefaultDir());
    fdlg.setDialogType(JFileChooser.OPEN_DIALOG);
    fdlg.setDialogTitle(parentframe.getBilingualMsg("0110"));
    fdlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    if(fdlg.showOpenDialog(this.getContentPane()) != JFileChooser.APPROVE_OPTION ){
      return false;
    }

    return true;
  }

	/**
	* �C�x���g����
	* @param ActionEvent e
	*/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("chicdir")){
        boolean jgchic = createFileChooser();
        if( jgchic ){
          dirArea.setText( fdlg.getSelectedFile().getAbsolutePath() );
        }

      }
      else if (action.equals("makedpx")){
        //OK�������ꂽ�ꍇ
        if( !makePrjct() ){
          return;
        }

        dispose();
      }
      else if (action.equals("close")){
        //�L�����Z���������ꂽ�ꍇ
        dispose();
      }
      else if (action.equals("return")){
        //�߂�������ꂽ�ꍇ
        dispose();

        NewChoiceWin = new NewChoice (parentframe,project );
        //NewChoiceWin.show();
		    NewChoiceWin.setVisible(true);
      }
      else{

      }

    }
    catch(Exception e2){

    }

  }

  /**
  * �v���W�F�N�g�t�@�C���쐬����
  */
  private boolean makePrjct( ) throws IOException , java.lang.Exception{

    try {

      Object[] options = { "OK" };
      String chkname = nameArea.getText();
      String chkdir  = dirArea.getText();

      //���͊m�F
      if( chkname.equals("") && chkdir.equals("") ){
        JOptionPane.showOptionDialog(this,
            parentframe.getBilingualMsg("0144"),parentframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

      if( chkname.equals("") ){
        JOptionPane.showOptionDialog(this,
            parentframe.getBilingualMsg("0145"),parentframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

      if( chkdir.equals("") ){
        JOptionPane.showOptionDialog(this,
            parentframe.getBilingualMsg("0146"),parentframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

			//�f�B���N�g���̕�����̍Ō�Ƀo�b�N�X���b�V���������Ă����ꍇ�͍폜
			//if( chkdir.endsWith("\\") ){
      if( chkdir.endsWith(File.separator) ){
				//int delcnt = chkdir.lastIndexOf("\\");
        int delcnt = chkdir.lastIndexOf(File.separator);
				chkdir = chkdir.substring(0,delcnt);
				if(chkdir.equals("")){
	        JOptionPane.showOptionDialog(this,
  	          parentframe.getBilingualMsg("0147"),parentframe.getBilingualMsg("0129"),
    	                               JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
      	                             null, options, options[0]);
        	return false;
				}
			}


			//�t�H���_�̑��݊m�F�A�t�H���_�쐬����

			//���͂��ꂽ�f�B���N�g���̍ŏ㕔�����݂��邩�𔻕�
			//int chktopdirno = chkdir.indexOf("\\");
      int chktopdirno = chkdir.indexOf(File.separator);
			if( chktopdirno == -1 ){
        JOptionPane.showOptionDialog(this,
            parentframe.getBilingualMsg("0148"),parentframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
			}

      String chkTopdir = chkdir.substring(0,chktopdirno+1);

			// �ŏ㕔�`�F�b�N
			File chkfile = new File(chkTopdir);
			if (!chkfile.isDirectory() ) {
        JOptionPane.showOptionDialog(this,
            parentframe.getBilingualMsg("0148"),parentframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
			}

			// \\���A���ő����Ă���ꍇ�̓G���[
			//if( chkdir.indexOf("\\\\") != -1 ){
      if( chkdir.indexOf(File.separator + File.separator) != -1 ){
        JOptionPane.showOptionDialog(this,
            parentframe.getBilingualMsg("0148"),parentframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
			}

			//�e�t�H���_���̃G���[�`�F�b�N
			//String[] errStrs = { "/", ".", ":", ";", "*", "?", "\"", "<", ">", "|" };
      String[] errStrs = { "/", ":", ";", "*", "?", "\"", "<", ">", "|" };
			int errcnt = errStrs.length;
			//StringTokenizer st = new StringTokenizer (chkdir, "\\" );
      StringTokenizer st = new StringTokenizer (chkdir, File.separator );
			ArrayList vecSearchKeyword = new ArrayList();
			boolean fstflag = false;
			while (st.hasMoreElements() ) {
				String errObj = st.nextToken();

				if(!fstflag){
					fstflag = true;
					continue;
				}

				for( int i=0; i < errcnt; i++ ){
					if( errObj.indexOf(errStrs[i]) != -1 ){
		        JOptionPane.showOptionDialog(this,
  		          parentframe.getBilingualMsg("0149") + "\r\n  \\  /  :  .  ;  *  ?  \"  <  >  |",parentframe.getBilingualMsg("0129"),
    		                               JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
      		                             null, options, options[0]);
        		return false;
					}
				}
			}



			//�t�H���_�쐬����
			//int makedircnt = chkdir.lastIndexOf("\\");
      int makedircnt = chkdir.lastIndexOf(File.separator);
			String makepdirstr = chkdir.substring(0,makedircnt);

			try {
				File file = new File (chkdir);
				if (file.isDirectory() ) {
				  // �t�H���_�͍��Ȃ�

				}
				else if (file.exists() ) {
				 // �����̃t�@�C�������݂���̂ŃG���[
	        JOptionPane.showOptionDialog(this,
  	          parentframe.getBilingualMsg("0150"),parentframe.getBilingualMsg("0129"),
    	                               JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
      	                             null, options, options[0]);
        	return false;
				}
				else{
					file.mkdirs();
				}

			}
			catch (Exception e ) {
				// �p�X�̓��͂��Ԉ���Ă�?
        JOptionPane.showOptionDialog(this,
 	          e.getLocalizedMessage(),"�G���[",
   	                               JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
     	                             null, options, options[0]);
       	return false;
			}

			//�t�H���_�쐬�m�F�_�C�A���O
      //�w�肵���f�B���N�g���ɓ������O�̃v���W�F�N�g�t�@�C�������݂��邩�𔻕�
      File f = new File(chkdir);
      String[] FileList = f.list();

      String jgfilename = chkname + ".dpx";
      int jgcnt = 0;
      for (int i=0; i<FileList.length; i++ ) {
        String existname = FileList[i];

        if( existname.toLowerCase().equals(jgfilename.toLowerCase()) ){

					String cstmStr = parentframe.getBilingualMsg("0086");
					cstmStr = cstmStr.replaceAll("filename",existname);

					Object[] options2 = { parentframe.getBilingualMsg("0191"),parentframe.getBilingualMsg("0192") };

					jgcnt = JOptionPane.showOptionDialog(this,
											cstmStr,parentframe.getBilingualMsg("0130"),
											JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
											null, options2, options2[0]);

          if( jgcnt != 0 ){
            return false;
          }
        }
      }

      //�v���W�F�N�g�t�@�C���쐬����
      String dirpath = "";
      if (!chkdir.endsWith(File.separator)) {
          dirpath = chkdir + File.separator;
      }
      String FilePath = dirpath + jgfilename;

      File fp  = new File ( FilePath );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.close ();

      //�c���[�X�V
      parentframe.readProjectFile(FilePath);
      SearchPanel.setDirKeyword(jgfilename);

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
      if(!makePrjct()){
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