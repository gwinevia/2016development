package dash;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.*;
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

public class ProjectPropertyDialog extends EnhancedDialog   implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JButton btnOk = new JButton("OK");
  private JButton btnCancel = new JButton();
  private IdeaMainFrame mainframe = null;
  private Project project = null;
  JFileChooser fdlg = null;

  private JTextField txtFieldBaseProcessPlace = new JTextField();

  public ProjectPropertyDialog(IdeaMainFrame mainframe, String title, Project project) {
    super(mainframe, title, true);
    this.mainframe = mainframe;
    this.project = project;
    try {
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public ProjectPropertyDialog() {
    this(null, "", null);
  }
  private void jbInit() throws Exception {
    btnCancel = new JButton( mainframe.getBilingualMsg("0126") );

    setTitle(mainframe.getBilingualMsg("0209"));
    setSize(400,130);
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

    panel1 = new JPanel() {
        Insets insets = new Insets(0, 4, 0, 0);
        public Insets getInsets() {
          return insets;
        }
    };
    panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));

    getContentPane().setLayout(new BorderLayout());

    panel1.add(createSetBaseProcessPlacePanel());

    /*
    panel1.add(createSetNameServerAdrPanel());
    panel1.add(createSetScriptsFolderPanel());

    panel1.add(createSetReposiyotyNamePanel());
    panel1.add(createSetWorkplaceNamePanel());
    panel1.add(createSetTomeoutPanel());

    panel1.add(createSetWpCntPanel());

    createRepWpindicationPosPanel();
    */
    //panel1.add(createRepWpindicationPosPanel());

    JPanel panel2 = new JPanel() {
        Insets insets = new Insets(0, 4, 0, 0);
        public Insets getInsets() {
          return insets;
        }
    };

    btnOk.setActionCommand("ok");
    btnOk.addActionListener(this);
    btnCancel.setActionCommand("cancel");
    btnCancel.addActionListener(this);

    btnOk.setPreferredSize(new Dimension(100,25));
    btnCancel.setPreferredSize(new Dimension(100,25));

    panel2.add(btnOk);
    panel2.add(btnCancel);
    getContentPane().add(panel1, BorderLayout.CENTER);
    getContentPane().add(panel2, BorderLayout.SOUTH);
  }


  private JPanel createSetBaseProcessPlacePanel() {
    JPanel panel1 = new JPanel (new BorderLayout() );
    JPanel panel2 = new JPanel (new BorderLayout() );
    JPanel panel3 = new JPanel (new GridLayout(1,1) );

    JButton srchbtn = new JButton("…");
    srchbtn.addActionListener(this);
    srchbtn.setActionCommand("dirsrch");

    panel2.add(txtFieldBaseProcessPlace,BorderLayout.CENTER);
    panel2.add(srchbtn,BorderLayout.EAST);
    String path = (String)System.getProperty("BpOutputPath");
    if (path.equals("current") ) {
      txtFieldBaseProcessPlace.setText(project.getProjectPath() + "java_");
    }
    else {
      txtFieldBaseProcessPlace.setText(System.getProperty("BpOutputPath"));
    }
    //JLabel lbl = (JLabel)panel3.add (new JLabel("　※" + mainframe.getBilingualMsg("0170")));
    //lbl.setForeground(Color.red);

    panel1.add(panel2, BorderLayout.NORTH);
    //panel1.add(panel2, BorderLayout.CENTER);
    panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0208")));
    return panel1;
  }

  // イベント処理
  public void actionPerformed(ActionEvent e){
    try{
      String action = e.getActionCommand();
      if (action.equals("ok")){
        ok();
      }else if(action.equals("cancel")){
        cancel();
      }else if (action.equals("dirsrch")){
        //ディレクトリ検索画面表示
        boolean jgchic = createFileChooser();
        if (jgchic ) {
          txtFieldBaseProcessPlace.setText(fdlg.getSelectedFile().getAbsolutePath());
        }
      }
    }catch(Exception e2){

    }

  }

  /**
  * FileChooser作成
  */
  public boolean createFileChooser() {
    Container cont;
    cont = this.getParent();
    String DefaultDir = (String)System.getProperty("BpOutputPath");
    if (DefaultDir.equals("current") ) {
      DefaultDir = project.getProjectPath() + "java_";
    }

    fdlg = new JFileChooser(DefaultDir);
    fdlg.setDialogType(JFileChooser.OPEN_DIALOG);
    fdlg.setDialogTitle(mainframe.getBilingualMsg("0110"));
    fdlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    if(fdlg.showOpenDialog(cont) != JFileChooser.APPROVE_OPTION ){
      return false;
    }

    return true;
  }

  public void ok () {
    String path = "current";
    if (!txtFieldBaseProcessPlace.getText().equals(project.getProjectPath() + "java_") ) {
      path = txtFieldBaseProcessPlace.getText();
    }

    Properties properties = new Properties();
    properties.setProperty("BpOutputPath", path);
    try {
      FileOutputStream fos = new FileOutputStream( project.getProjectPath() + "bp.property"  );

      BufferedWriter awriter;
      awriter = new BufferedWriter(new OutputStreamWriter(fos, "8859_1"));

      properties.store( fos, "idea Properties" );
      fos.close();
    } catch (FileNotFoundException e) {
      System.err.println("warning: simulator.properties not found");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // デフォルトの設定。
    // ただし、起動時に-Dで指定したものを優先する。
    for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();
      String defaultValue = properties.getProperty(key);
      String systemValue = System.getProperty(key);
      //System.out.println(key);
      if (systemValue == null) {
        System.setProperty(key, defaultValue);
      }
      else {
        System.getProperties().remove(key);
        System.setProperty(key, defaultValue);
      }
    }

    dispose();
  }
  public void cancel () {
     dispose();
  }

}