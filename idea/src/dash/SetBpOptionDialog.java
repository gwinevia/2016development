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


public class SetBpOptionDialog extends EnhancedDialog implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private IdeaMainFrame mainframe = null;
  private String BpFileName = "";
  private String FileNameWithPath = "";


  /** チェックボックスとエージェントファイル名の組み合わせを配置するパネル */
  private JPanel agentList = null;

  /** チェックボックスを格納するベクター */
  Vector checkboxes = new Vector();

  /** 未使用 */
  Vector comboboxes = new Vector();
  //GeneratedListModel listModel;

  /** ボタン */
  JButton okbtn = new JButton("OK");
  JButton cancelbtn = null;
  JButton selectAllBtn = null;
  JButton selectNoneBtn = null;
  
  private static Dimension HGAP10 = new Dimension(10,1);
  private Project project;
  public boolean ret = false;// 1:OK 0:Cancel

  /** 選択されたファイル */
  private Vector vecSelectFile = new Vector();

  /** 以前、本ダイアログが実行された時に選択されたファイル */
  private Vector prevSelectFiles  = null;

  /** 未使用 */
  private Vector prevReadCount  = null;

  /** 未使用 */
  private Vector vecReadCount = new Vector();

  public SetBpOptionDialog(IdeaMainFrame frame, String title, Project project, String BpFileName, String FileNameWithPath) {
    super(frame, title + "-" + BpFileName, true);
    mainframe = frame;
    this.BpFileName = BpFileName;
    this.FileNameWithPath = FileNameWithPath;
    this.project = project;
    
    /*
    if (this.BpFileName.toLowerCase().endsWith(".rset") ) {
    	FileNameWithPath = this.BpFileName;
    	this.BpFileName = this.BpFileName.substring(this.BpFileName.lastIndexOf(File.separator)+1);
    }
    */

    try {
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public SetBpOptionDialog() {
    this(null, "", null, "", "");
  }
  private void jbInit() throws Exception {

    cancelbtn = new JButton(mainframe.getBilingualMsg("0126"));

    // フレームサイズ
    setSize(300,350);

    // ダイアログを画面中央に配置
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    //this.setSize(new Dimension(400, 300));
    panel1.setLayout(borderLayout1);

    JPanel msgPanel = new JPanel();
    JLabel msgLabel2 = new JLabel (mainframe.getBilingualMsg("0169"));
    msgPanel.add(msgLabel2);
    msgPanel.setBackground(new Color(255,255,217));
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(msgPanel,BorderLayout.NORTH);

    // チェックボックスとラベルの組み合わせを配置するパネルを作成し、ダイアログに配置する
    getContentPane().add(createControlPanel(),BorderLayout.CENTER);
    //getContentPane().add(new JCheckBox("起動前にリポジトリのAgを消去する"),BorderLayout.SOUTH);

    // 現時点で関連のあるDashファイルを調べる
    Vector vecDashFileName = new Vector();
    try {
      FileReader f_in;
      BufferedReader b_in;
      String sLine;

			String ParentFolderName = "";
			if (BpFileName.toLowerCase().endsWith(".java") ) {
				ParentFolderName ="java_" ;
			}
			else {
				ParentFolderName ="rset_" ;
			}
      b_in = new BufferedReader(new InputStreamReader(
                                              new FileInputStream(project.getProjectPath() + ParentFolderName + File.separator + BpFileName + "_inf"),
                                              "JISAutoDetect"));

      int inf_kind = 0;
      String javafilename = "";
      while ((sLine = b_in.readLine()) != null)
      {
        if (sLine.equals("[path]")) {
          inf_kind = 1;//Path情報
        }
        else if (sLine.equals("[relation dash file]")) {
          inf_kind = 2;//関連Dashファイル情報
        }
        else {
          if (inf_kind == 1 ) {
          }
          else {
            vecDashFileName.addElement(sLine);
          }

        }
      }
      b_in.close();

    } catch(Exception ex) { }


		// リスト表示するファイルをベクターに格納
		Vector vecFileName = new Vector();
    for (int i=0; i<project.getFileCount(); i++ ) {
      if (!((String)project.getFileName(i)).toLowerCase().endsWith(".dash") ) {
        continue;
      }
      vecFileName.addElement((String)project.getFileName(i));
    }
    
    // ベクターに入れたファイル名をソートする
    for(int sfrom=0; sfrom<vecFileName.size()-1; sfrom++) {
      int max = sfrom;
      for(int i=sfrom+1; i<vecFileName.size(); i++) {
          if(((String)vecFileName.elementAt(i)).compareTo((String)vecFileName.elementAt(max))<0) max = i;
      }
      String temp = (String)vecFileName.elementAt(sfrom);
      vecFileName.setElementAt((String)vecFileName.elementAt(max),sfrom);
      vecFileName.setElementAt(temp,max);
     }    

    // ファイル名を、チェックボックスとの組み合わせでパネルに追加する
    for (int i=0; i<vecFileName.size(); i++ ) {
      boolean chkflag = false;
      String ReadCount = null;
      if (prevReadCount != null ) {
        ReadCount = "1";
      }
      
      String FileName = (String)vecFileName.elementAt(i);
      //if (((String)project.getFileName(i)).toLowerCase().endsWith(".rset") ) {
      //  continue;
      //}
      if (!FileName.toLowerCase().endsWith(".dash") ) {
        continue;
      }
      if (vecDashFileName.indexOf(FileName) != -1 ) {
        chkflag = true;
      }
      addPrefix (FileName,chkflag,ReadCount);
    }

    // ファイル名を、チェックボックスとの組み合わせでパネルに追加する
    /*
    for (int i=0; i<project.getFileCount(); i++ ) {
      boolean chkflag = false;
      String ReadCount = null;
      if (prevReadCount != null ) {
        ReadCount = "1";
      }
      //if (((String)project.getFileName(i)).toLowerCase().endsWith(".rset") ) {
      //  continue;
      //}
      if (!((String)project.getFileName(i)).toLowerCase().endsWith(".dash") ) {
        continue;
      }
      if (vecDashFileName.indexOf(project.getFileName(i)) != -1 ) {
        chkflag = true;
      }
      addPrefix (project.getFileName(i),chkflag,ReadCount);
    }
    */

    JPanel btnpanel = new JPanel (new GridLayout(1,2));
    btnpanel.add(okbtn);
    btnpanel.add(cancelbtn);

    okbtn.setActionCommand("ok");
    cancelbtn.setActionCommand("cancel");
    okbtn.addActionListener(this);
    cancelbtn.addActionListener(this);

    JPanel btnpanel2 = new JPanel (new BorderLayout());
    btnpanel2.add(btnpanel,BorderLayout.EAST);
    getContentPane().add(btnpanel2,BorderLayout.SOUTH);
  }


  /****************************************************************************
   * チェックボックスとラベルの組み合わせを配置するパネルを作成
   * @param なし
   * @return チェックボックスとラベルの組み合わせを配置するパネル
   ****************************************************************************/
  public JPanel createControlPanel() {
    JPanel controlPanel = new JPanel() {
      Insets insets = new Insets(4, 4, 4, 4);
      public Insets getInsets() {
        return insets;
      }
    };
    controlPanel.setLayout(new BorderLayout());

    JPanel agentPanel = new JPanel();
    agentPanel.setLayout(new BoxLayout(agentPanel, BoxLayout.Y_AXIS));

    agentList = new JPanel() {
        Insets insets = new Insets(0, 4, 0, 0);
        public Insets getInsets() {
          return insets;
        }
    };
    agentList.setLayout(new BoxLayout(agentList, BoxLayout.Y_AXIS));
    //agentList.setLayout(new GridLayout(project.getFileCount(),2) );
    agentList.setBackground(Color.white);

    JScrollPane scrollPane = new JScrollPane(agentList);
    scrollPane.getVerticalScrollBar().setUnitIncrement(10);
    agentPanel.add(scrollPane);
    controlPanel.add(agentPanel, BorderLayout.CENTER);
    
    JPanel selPanel = new JPanel();
    selectAllBtn = new JButton(mainframe.getBilingualMsg("0225"));
    selectNoneBtn = new JButton(mainframe.getBilingualMsg("0226"));

    selectAllBtn.setActionCommand("selectAll");
    selectNoneBtn.setActionCommand("selectNone");

    selectAllBtn.addActionListener(this);
    selectNoneBtn.addActionListener(this);

    selPanel.add(selectAllBtn);
    selPanel.add(selectNoneBtn);
    controlPanel.add(selPanel, BorderLayout.NORTH);
    
    return controlPanel;
  }

  /****************************************************************************
   * チェックボックスとラベルの組み合わせを作成し、agentListに配置する
   * @param prefix ラベルに表示する文字列
   * @param selected チェックボックスを選択状態にする場合はtrue、そうでないときはfalse
   * @return なし
   ****************************************************************************/
  public void addPrefix(String prefix, boolean selected, String ReadCount) {
    //if(prefixAction == null) {
    //  prefixAction = new UpdatePrefixListAction(listModel);
    //}

    JPanel wkpanel1 = new JPanel (new BorderLayout());
    JPanel wkpanel2 = new JPanel (new BorderLayout());
    Vector vecCount = new Vector();
    for (int i=1; i<=10; i++ ) {
      vecCount.addElement(new Integer(i).toString());
    }
    JCheckBox cb = null;
    if (ReadCount != null ) {
      JLabel lbl = new JLabel("個  ");
      lbl.setBackground(Color.white);
      wkpanel2.add(lbl, BorderLayout.EAST);
      JComboBox cmb = (JComboBox)wkpanel2.add(new JComboBox(vecCount));
      wkpanel2.setBackground(Color.white);
      cb = (JCheckBox) wkpanel1.add(new JCheckBox(prefix));

      wkpanel1.add(wkpanel2,BorderLayout.EAST);
      cmb.setSelectedItem(ReadCount);
      agentList.add(wkpanel1);
      comboboxes.addElement(cmb);
    }
    else {
      cb = (JCheckBox) agentList.add(new JCheckBox(prefix));
    }
    checkboxes.addElement(cb);
    cb.setSelected(selected);
    cb.setBackground(Color.white);


    //agentList.add(new JComboBox());
    //agentList.add(new Label("AAA"));
    //cb.addActionListener(prefixAction);
    //if(selected) {
    //  listModel.addPrefix(prefix);
    //}
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
      else if (action.equals("selectAll")){
        for (int i=0; i<checkboxes.size(); i++ ) {
          JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);
          bc.setSelected(true);
        }
      }
      else if (action.equals("selectNone")){
        for (int i=0; i<checkboxes.size(); i++ ) {
          JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);
          bc.setSelected(false);
        }
      }
    }catch(Exception e2){

    }

  }

  public void ok() {
    Vector vecSelectFile = new Vector();
    for (int i=0; i<checkboxes.size(); i++ ) {
      JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);

      if (bc.isSelected() ) {
        vecSelectFile.addElement((String)bc.getText());
      }
    }

    /*
    if (vecSelectFile.size() == 0 ) {

      Object[] options = { "OK" };
      JOptionPane.showOptionDialog(this,
                    mainframe.getBilingualMsg("0075"),mainframe.getBilingualMsg("0129"),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                    null, options, options[0]);

//      JOptionPane.showMessageDialog(this,mainframe.getBilingualMsg("0075"));
      return;
    }
    */

		String ParentFolderName = "";
		if (BpFileName.toLowerCase().endsWith(".java") ) {
				ParentFolderName = "java_";
		}
		else {
				ParentFolderName = "rset_";
		}
	    // 現時点で関連のあるDashファイルを調べる
    Vector vecOrgData = new Vector();
    try {
      FileReader f_in;
      BufferedReader b_in;
      String sLine;

			if (ParentFolderName.equals("java_") ) {
	      b_in = new BufferedReader(new InputStreamReader(
	                                              new FileInputStream(project.getProjectPath() + ParentFolderName + File.separator + BpFileName + "_inf"),
	                                              "JISAutoDetect"));

	      int inf_kind = 0;
	      String javafilename = "";
	      while ((sLine = b_in.readLine()) != null)
	      {
	        if (sLine.equals("[path]")) {
	          inf_kind = 1;//Path情報
	          vecOrgData.addElement(sLine);
	        }
	        else if (sLine.equals("[relation dash file]")) {
	          inf_kind = 2;//関連Dashファイル情報
	          vecOrgData.addElement(sLine);
	          break;
	        }
	        else {
	          if (inf_kind == 1 ) {
	            vecOrgData.addElement(sLine);
	          }
	
	        }
	      }
	      b_in.close();
			}
			else {
	     	vecOrgData.addElement("[path]");
	     	vecOrgData.addElement(FileNameWithPath);
	     	vecOrgData.addElement("[relation dash file]");
      }

      File fp  = new File ( project.getProjectPath() + ParentFolderName + File.separator + BpFileName + "_inf" );
      FileOutputStream fos = new FileOutputStream (fp);
      PrintWriter pw  = new PrintWriter (fos);
      for (int i=0; i<vecOrgData.size(); i++ ) {
        pw.println((String)vecOrgData.elementAt(i));
      }
      for (int i=0; i<vecSelectFile.size(); i++ ) {
        pw.println((String)vecSelectFile.elementAt(i));
      }
      pw.close ();

    } catch(Exception ex) { }



    ret = true;
    //ツリー更新
    mainframe.readProjectFile(project.getProjectFileNameWithPath());
    dispose();
  }

  public void cancel() {
    ret = false;
    dispose();
  }

}