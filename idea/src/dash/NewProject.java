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
 * <p>新規プロジェクト作成画面: </p>
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

    super( frame, "新規プロジェクト", true );
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

		// 初期設定
		String defFileName  = "";
		String defDirectory = "";

		// デフォルトで表示するディレクトリを取得
		String defdir = srchpanel.getDefaultDir();

		// デフォルトで表示するプロジェクト名を取得
    // デフォルト名は Untitled(n) とする。

		try{
			//scriptsフォルダを作成
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

				//比較
				if( maxno > fileno){
					//最大値の取得
					fileno = maxno;
				}
			}
    }

		//最大値取得
		fileno++;

		// 初期値取得
		String filenoStr = new Integer(fileno).toString();
		String defname = "Untitled" + filenoStr;
		defDirectory = defdir + defname;
		defFileName  = defname;

		// 画面作成
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel(parentframe.getBilingualMsg("0142"));
    panel1.add(label1);
    panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

		//space
		String addSpaceStr = "";
		if( parentframe.getBilingualMsg("0140").equals("Name") ){
			addSpaceStr = "     ";
		}else{
			addSpaceStr = "　　　　";
		}

    JLabel label2 = new JLabel(parentframe.getBilingualMsg("0140") + addSpaceStr + "：");
    JLabel label2sub = new JLabel(".dpx　　");
    JLabel label3 = new JLabel(parentframe.getBilingualMsg("0143") + "：");
    nameArea = new JTextField(20);
    dirArea  = new JTextField(20);
		nameArea.addKeyListener(this);
    dirArea.setBackground(Color.white);
    JButton dirBtn = new JButton("…");
    dirBtn.setPreferredSize(new Dimension(50,22));
    dirBtn.addActionListener(this);
    dirBtn.setActionCommand("chicdir");

		//初期値セット
		nameArea.setText(defFileName);
		dirArea.setText(defDirectory);

    // 名前
    JPanel panel2 = new JPanel(new BorderLayout());
    panel2.add(label2,BorderLayout.WEST);
    panel2.add( nameArea,BorderLayout.CENTER );
    panel2.add(label2sub,BorderLayout.EAST);

    //ディレクトリ
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

    //OKキャンセルボタン
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
			//直接新規プロジェクトが選択された場合は戻るボタンは付けない
			//新規からの場合のみ付ける
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
	* テキスト連動処理
	*/
	private void Interlock(){

		String name = nameArea.getText();
		String dir  = dirArea.getText();

		//ディレクトリエリア連動処理
		//int divcnt = dir.lastIndexOf("\\");
    int divcnt = dir.lastIndexOf(File.separator);
		if( divcnt != -1){
			String divStr = dir.substring(0,divcnt+1) + name;
			dirArea.setText(divStr);
		}
	}

	/**
	* FileChooser表示
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
	* イベント処理
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
        //OKを押された場合
        if( !makePrjct() ){
          return;
        }

        dispose();
      }
      else if (action.equals("close")){
        //キャンセルを押された場合
        dispose();
      }
      else if (action.equals("return")){
        //戻るを押された場合
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
  * プロジェクトファイル作成処理
  */
  private boolean makePrjct( ) throws IOException , java.lang.Exception{

    try {

      Object[] options = { "OK" };
      String chkname = nameArea.getText();
      String chkdir  = dirArea.getText();

      //入力確認
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

			//ディレクトリの文字列の最後にバックスラッシュが入っていた場合は削除
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


			//フォルダの存在確認、フォルダ作成処理

			//入力されたディレクトリの最上部が存在するかを判別
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

			// 最上部チェック
			File chkfile = new File(chkTopdir);
			if (!chkfile.isDirectory() ) {
        JOptionPane.showOptionDialog(this,
            parentframe.getBilingualMsg("0148"),parentframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
			}

			// \\が連続で続いている場合はエラー
			//if( chkdir.indexOf("\\\\") != -1 ){
      if( chkdir.indexOf(File.separator + File.separator) != -1 ){
        JOptionPane.showOptionDialog(this,
            parentframe.getBilingualMsg("0148"),parentframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
			}

			//各フォルダ名のエラーチェック
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



			//フォルダ作成処理
			//int makedircnt = chkdir.lastIndexOf("\\");
      int makedircnt = chkdir.lastIndexOf(File.separator);
			String makepdirstr = chkdir.substring(0,makedircnt);

			try {
				File file = new File (chkdir);
				if (file.isDirectory() ) {
				  // フォルダは作らない

				}
				else if (file.exists() ) {
				 // 同名のファイルが存在するのでエラー
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
				// パスの入力が間違ってる?
        JOptionPane.showOptionDialog(this,
 	          e.getLocalizedMessage(),"エラー",
   	                               JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
     	                             null, options, options[0]);
       	return false;
			}

			//フォルダ作成確認ダイアログ
      //指定したディレクトリに同じ名前のプロジェクトファイルが存在するかを判別
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

      //プロジェクトファイル作成処理
      String dirpath = "";
      if (!chkdir.endsWith(File.separator)) {
          dirpath = chkdir + File.separator;
      }
      String FilePath = dirpath + jgfilename;

      File fp  = new File ( FilePath );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.close ();

      //ツリー更新
      parentframe.readProjectFile(FilePath);
      SearchPanel.setDirKeyword(jgfilename);

    } catch ( Exception e ){
      return false;
    }

    return true;
  }


	/**
	* Enterキー処理
	*/
  public void ok(){
    try {
      //ファイル作成処理
      if(!makePrjct()){
        return;
      }
    }
    catch(Exception e ) {}
    dispose();
  }

	/**
	* Escキー処理
	*/
  public void cancel(){
    dispose();
  }

	/**
	* ImageIcon処理
	* @param String path パス<BR>
	*/
  private ImageIcon getImageIcon(String path) {
    java.net.URL url = this.getClass().getResource(path);
    return new ImageIcon(url);
  }


}