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
 * <p>新規Dashファイル、ルールセット作成画面: </p>
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

    super( mainframe, "新規プロジェクト", true );
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

    setTitle("新規作成");
    setSize(450,145);
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

		//拡張子設定
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

    //名前
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel(mainframe.getBilingualMsg("0063"));
    panel1.add(label1);
    panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel label2 = new JLabel(mainframe.getBilingualMsg("0140") + "：");
    JLabel label2sub = new JLabel("." + kindStr + "　　");
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

    //OKキャンセルボタン
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
  * イベント処理
	* @param ActionEvent e<BR>
	*/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("makefile")){
        //ファイル作成処理
        if(!makeFile()){
          return;
        }
        dispose();
      }
      else if(action.equals("close")){
        //キャンセルを押された場合
        dispose();
      }
      else if(action.equals("return")){
        //戻るを押された場合
        dispose();

        NewChoiceWin = new NewChoice (this.mainframe,project );
        //NewChoiceWin.show();
     		NewChoiceWin.setVisible(true);
      }

    }catch(Exception e2){

    }

  }


  /**
  * dashファイル作成処理
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
      //入力確認
      if( chkname.equals("") ){
        JOptionPane.showOptionDialog(this,
            mainframe.getBilingualMsg("0081"),mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

      //拡張子を付ける
      if (kindNo != 5 ) {
        chkname += "." + kindStr;
      }
      else {
        //.dash、.rset、.java
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

      //現在登録されているファイルを取得
      FileReader f_in;
      BufferedReader b_in;
      String sLine = "";

      //読み込み処理
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

      //指定したプロジェクト内に同じ名前のファイルが存在するかを判別
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

      //ディレクトリに同名のファイルが存在するかを判別
      String dirpath = chkdir;
      if (!chkdir.endsWith(File.separator)) {
          dirpath = chkdir + File.separator;
      }
      String FilePath = dirpath + chkname;

      File f = new File(FilePath);
      if(f.isFile()){

				String cstmStr = mainframe.getBilingualMsg("0083");

				//[path]を入れ替える
				int chkcnt = cstmStr.indexOf("path");
				cstmStr = cstmStr.substring(0,chkcnt) + dirpath + cstmStr.substring(chkcnt+4);
				cstmStr = cstmStr.replaceFirst("filename",chkname);

        JOptionPane.showOptionDialog(this,
            cstmStr,mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }

      //ファイル作成処理
      File fp  = new File ( FilePath );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.close ();

      if (kindNo != 4 ) {
        //プロジェクトファイル更新
        File fp2  = new File ( ExstPrjfilePath );
        FileOutputStream fos2 = new FileOutputStream (fp2);
        PrintWriter pw2  = new PrintWriter (fos2);

        //既存のファイル
        for(int i=0; i < exstList.size(); i++){
          String writefilename = (String)SaveexstList.elementAt(i);
          pw2.println(writefilename);
        }

        //新規ファイル
        pw2.println(chkname);
        pw2.close ();
      }
      else {
        //プロジェクトファイル更新
        File fp2  = new File ( FilePath + "_inf" );
        FileOutputStream fos2 = new FileOutputStream (fp2);
        PrintWriter pw2  = new PrintWriter (fos2);

        //新規ファイル
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
	* Enterキー処理
	*/
  public void ok(){
    try {
      //ファイル作成処理
      if(!makeFile()){
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