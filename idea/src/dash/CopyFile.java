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
 * <p>タイトル：複製ファイル作成 </p>
 * <p>説明: エージェントファイルの複製を作成するダイアログ</p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名:cosmosweb </p>
 * @author nakagawa
 * @version 1.0
 */

public class CopyFile extends EnhancedDialog implements ActionListener{
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** 親フレーム */
  private IdeaMainFrame mainframe = null;

  /** オリジナルファイル名称 */
  private String orgFileName = "";
  private String orgFileNameWithPath = "";

  /** ファイルの拡張子 */
  private String ext = "";

  private JPanel connectPanel = new JPanel();

  private String Mode = "CreateAliasFile";//1:複製作成　2:名前の変更
  public String NewName = "";

  /****************************************************************************
   * コンストラクタ
   * @param mainframe IdeaMainFrameを受け取る
   * @param prj  プロジェクト
   * @param orgFileName  オリジナルファイル名
   * @return なし
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

  /** プロジェクト */
  private Project project = null;

  /** 複製ファイル名入力エリア */
  private JTextField nameArea = null;

  /** オリジナルファイル名表示エリア */
  private JTextField orgnameArea = null;

  private Border border1;

  /****************************************************************************
   * 初期化処理
   * @param なし
   * @return なし
   ****************************************************************************/
  private void jbInit() throws Exception {

    // ダイアログのタイトルを設定
    //setTitle(mainframe.getBilingualMsg("0031"));

    // フレームサイズ
    setSize(350,160);

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

    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel(mainframe.getBilingualMsg("0160"));
    panel1.add(label1);
    panel1.setLayout(new FlowLayout(FlowLayout.LEFT));


    // 複製ファイル名を表示アリアを作成し、フレーム上に配置するパネルに追加する
    String addSpace = "";
    if( mainframe.getBilingualMsg("0161").equals("To") ){
			addSpace = "     ";
    }

    JLabel label2 = null;
    if (Mode.equals("CreateAliasFile") ) {
      label2 = new JLabel(mainframe.getBilingualMsg("0161") + addSpace + "：");
    }
    else if (Mode.equals("ChangeName") ) {
      label2 = new JLabel(mainframe.getBilingualMsg("0213") + addSpace + "：");
    }
    JLabel label2sub = new JLabel(ext + "　　");
    nameArea = new JTextField(20);
    nameArea.setText(orgFileName);
    JPanel panel2 = new JPanel(new BorderLayout());
    panel2.add(label2,BorderLayout.WEST);
    panel2.add(nameArea,BorderLayout.CENTER );
    panel2.add(label2sub,BorderLayout.EAST);

    // オリジナルファイル名を表示アリアを作成し、フレーム上に配置するパネルに追加する
    JLabel label3 = null;
    if (Mode.equals("CreateAliasFile") ) {
      label3 = new JLabel(mainframe.getBilingualMsg("0162") + "：");
    }
    else if (Mode.equals("ChangeName") ) {
      label3 = new JLabel(mainframe.getBilingualMsg("0212") + "：");
    }
    JLabel label3sub = new JLabel(ext+ "　　");
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

    //OKキャンセルボタン
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
   * イベント処理
   * @param e アクションイベント
   * @return なし
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
   * 複製ファイル作成処理
   * @param なし
   * @return なし
   ****************************************************************************/
  private boolean makeFile( ) throws IOException , java.lang.Exception{

    try {

      String chkname = nameArea.getText() + ext;//".dash";
      String chkdir  = project.getProjectPath();
      String ExstPrjfilePath = project.getProjectFileNameWithPath();
      String orgName = orgnameArea.getText() + ext;

      //入力確認

      // 複製ファイル名が入力されているか？
      if( nameArea.getText().equals("") ){

				Object[] options = { "OK" };
				JOptionPane.showOptionDialog(this,
											mainframe.getBilingualMsg("0076"),mainframe.getBilingualMsg("0129"),
											JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
											null, options, options[0]);

        return false;
      }

      // オリジナルファイル名と複製ファイル名が同じでないか？
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



      //現在、プロジェクトに登録されているファイルを取得
      FileReader f_in;
      BufferedReader b_in;
      String sLine = "";

      //読み込み処理
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

      //指定したプロジェクト内に同じ名前のファイルが存在するかを判別
      //存在する場合はエラーとする
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

      // プロジェクトでは管理されていないが、プロジェクトの管理するフォルダ内に同名のファイルがあったときの
      // 処理
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
//            nameArea.getText() + ext + " は、既に"+ project.getProjectPath()+"に存在します。置き換えますか？","複製作成",
//            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
//            null, options, options[0]);

        int r = ret;

        if (ret == 1 ) {
          return false;
        }

      }
      // コピー処理実行
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

            //既存のファイル
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

      //プロジェクトファイル更新
      //複製ファイルをプロジェクトに追加する
      File fp2  = new File ( ExstPrjfilePath );
      FileOutputStream fos2 = new FileOutputStream (fp2);
      PrintWriter pw2  = new PrintWriter (fos2);

      //既存のファイル
      for(int i=0; i < exstList.size(); i++){
        String writefilename = (String)exstList2.elementAt(i);
        if (Mode.equals("ChangeName") ) {
          if (writefilename.equals(orgfile.substring(project.getProjectPath().length())) ) {
            writefilename = makfile.substring(project.getProjectPath().length());
          }
        }
        pw2.println(writefilename);
      }

      //新規ファイル
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
        //複製ファイルをプロジェクトに追加する
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

      //プロジェクトファイルの読み直し
      //mainframe.readProjectFile(ExstPrjfilePath);

      if (Mode.equals("ChangeName") ) {
        NewName = makfile;

        Vector vecContents = new Vector();
        //String sLine = "";
        //String orgName = orgnameArea.getText() + ext;
        // Java_infの変更
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

                //既存のファイル
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
   * ファイルコピー
   * @param args[]　0:オリジナルファイル 1:複製ファイル
   * @return なし
   ****************************************************************************/
  public static void filecopy(String args[]){
    try{
      String  strBuff;
      File fp  = new File ( args[1] );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      pw.close ();

      /* オブジェクトの生成 */
      BufferedReader  brInFile = new BufferedReader(new FileReader(args[0]));
      PrintWriter     pwOutFile = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));

      /* 読み込み処理 */
      while((strBuff = brInFile.readLine()) != null){
        pwOutFile.println(strBuff);
      }

      /* クローズ処理 */
      brInFile.close();
      pwOutFile.close();
    }
    catch(Exception e){
      System.err.println("ERROR : " + e);
    }
  }

  /****************************************************************************
   * OK処理
   * @param なし
   * @return なし
   ****************************************************************************/
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

  /****************************************************************************
   * Cancel処理
   * @param なし
   * @return なし
   ****************************************************************************/
  public void cancel(){
    dispose();
  }



}