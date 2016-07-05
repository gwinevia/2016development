package dash;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * <p>タイトル: </p>
 * <p>説明: </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名: </p>
 * @author 未入力
 * @version 1.0
 */

public class AddFolderDialog extends EnhancedDialog implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  public boolean ret = false;
  public JTextField txtFieldFolderName = new JTextField(20);
  private IdeaMainFrame mainframe;
  private Project project = null;
  private String SelectedFolederPath = "";


  public AddFolderDialog(IdeaMainFrame mainframe, String title, boolean modal, Project project, String SelectedFolederPath) {
    super(mainframe, title, modal);
    this.mainframe = mainframe;
    this.project = project;
    this.SelectedFolederPath = SelectedFolederPath;
    try {
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public AddFolderDialog() {
    this(null, "", false,null,"");
  }
  private void jbInit() throws Exception {
    setSize(280,130);
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


    JPanel panelInputField = new JPanel (new GridLayout(2,1));
    panelInputField.add(new JLabel(mainframe.getBilingualMsg("0202")));
    panelInputField.add(txtFieldFolderName);

    JPanel panel2 = new JPanel() {
        Insets insets = new Insets(5,10,5,10);
        public Insets getInsets() {
          return insets;
        }
    };

    panel2.add(panelInputField);

    panel1.setLayout(borderLayout1);
    panel1.add(panel2,BorderLayout.CENTER );


    //ok,cancelボタン
    JPanel panel4 = new JPanel();
    JButton okBtn = new JButton("OK");
    JButton clBtn = new JButton(mainframe.getBilingualMsg("0126"));
    okBtn.addActionListener(this);
    okBtn.setActionCommand("ok");
    clBtn.addActionListener(this);
    clBtn.setActionCommand("cancel");
    okBtn.setPreferredSize(new Dimension(100,25));
    clBtn.setPreferredSize(new Dimension(100,25));
    panel4.add(okBtn);
    panel4.add(clBtn);
    panel4.setLayout(new FlowLayout(FlowLayout.CENTER));

    panel1.add(panel4,BorderLayout.SOUTH );

    getContentPane().add(panel1);

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
    }catch(Exception e2){

    }

  }

  public void ok () {


    // チェック
    Object[] options = { "OK" };

    if( txtFieldFolderName.getText().equals("") ){
      //名称が既に存在していた場合
      //JOptionPane.showMessageDialog(this,"名称 \"" + name + "\" は既に存在します。");


      JOptionPane.showOptionDialog(this,
          mainframe.getBilingualMsg("0202"),mainframe.getBilingualMsg("0129"),
                                 JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                 null, options, options[0]);
      return ;
    }

    //フォルダ名のエラーチェック
    String[] errStrs = { "/", ":", ";", "*", "?", "\"", "<", ">", "|" };
    int errcnt = errStrs.length;
    for( int i=0; i < errcnt; i++ ){
      if( txtFieldFolderName.getText().indexOf(errStrs[i]) != -1 ){
        JOptionPane.showOptionDialog(this,
            mainframe.getBilingualMsg("0149") + "\r\n  \\  /  :  ;  *  ?  \"  <  >  |",mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return;
      }
    }


    if (!SelectedFolederPath.endsWith(File.separator) ) {
      SelectedFolederPath = SelectedFolederPath + File.separator;
    }
    String createFolderPath = SelectedFolederPath + txtFieldFolderName.getText();

    try {
      File file = new File (createFolderPath);
      if (file.isDirectory() ) {
        // フォルダは作らない
        String cstmStr = mainframe.getBilingualMsg("0203");
        cstmStr = cstmStr.replaceAll("F_NAME",txtFieldFolderName.getText());

        JOptionPane.showOptionDialog(this,
            cstmStr,mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return;
      }
      else{
        file.mkdirs();
      }

      String ProjectPath = project.getProjectPath();
      String directoryInfoFile = "directoryinfo";
      String ProjectName = project.getProjectFileName();
      directoryInfoFile = ProjectName.substring(0,ProjectName.toLowerCase().lastIndexOf (".dpx")+1) + "directoryinfo";

      Vector vecFolderPath = new Vector();
      String sLine = "";
      // ディレクトリ情報ファイルがあるか否か
      file = new File (ProjectPath + directoryInfoFile);
      if (file.exists()) {
        BufferedReader b_in;
        b_in = new BufferedReader(new InputStreamReader(
            new FileInputStream(ProjectPath + directoryInfoFile),
            "JISAutoDetect"));
        while ((sLine = b_in.readLine()) != null)
        {
          vecFolderPath.addElement(sLine);
        }
        b_in.close();

      }

      String addFolderPath = createFolderPath.substring(ProjectPath.lastIndexOf(File.separator)+1);
      vecFolderPath.addElement(addFolderPath);

      File fp  = new File ( ProjectPath + directoryInfoFile );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      for (int i=0; i<vecFolderPath.size(); i++ ) {
        pw.println((String)vecFolderPath.elementAt(i));
      }
      pw.close ();


    }
    catch (Exception e ) {
      // パスの入力が間違ってる?
      JOptionPane.showOptionDialog(this,
           e.getLocalizedMessage(),"エラー",
                                  JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                  null, options, options[0]);
       return;
    }
    ret = true;
    //ツリー更新
    mainframe.readProjectFile(project.getProjectFileNameWithPath());
    dispose();
  }
  public void cancel () {
     ret = false;
     dispose();
  }

}