package dash;

import java.awt.*;
import javax.swing.JPanel;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.io.*;
import javax.swing.event.*;

/**
 * <p>OutsideTool: </p>
 * <p>外部ツール構成画面: </p>
 */
public class OutsideTool extends EnhancedDialog implements ActionListener,ListSelectionListener{

  private static File dashdir = null;
	private JList list1 = null;
	private JTextArea DetailTextArea = null;
  OutsideToolSub OutToolSub = null;

	//既存の外部ツールの情報を格納
	private Vector nameChkList = new Vector();
	private Vector nameList = new Vector();
	private Vector pathList = new Vector();
	private Vector numList  = new Vector();

	//編集画面から取得する
	private String delivName = "";
	private String delivPatn = "";
	private String delivNum  = "";

	//修正の際、編集画面に送る
	private String modName = "";
	private String modPatn = "";
	private String modNum  = "";

	//編集画面でキャンセルが押されたかを判別
	private boolean cancelFlg = false;

	/**
	* 詳細取得
	* @param String name 名前<BR>
	* @param String path パス<BR>
	* @param String num <BR>
	*/
	public void setdelivInfo(String name, String path, String num){
		delivName = name;
		delivPatn = path;
		delivNum  = num;
	}

	/**
	* キャンセル判別
	*/
	public void setcancelFlg(){
		cancelFlg = true;
	}

	/**
	* 名前取得
	*/
	public String getmodName(){
		return modName;
	}

	/**
	* パス取得
	*/
	public String getmodPath(){
		return modPatn;
	}

	/**
	* 番号取得
	*/
	public String getmodNum(){
		return modNum;
	}

	/**
	* 名前判別
	* @param String instr
	*/
	public boolean chkNameList( String instr ){

		if( nameChkList.indexOf(instr.toLowerCase()) != -1 ){
			return true;
		}
		else{
			return false;
		}

	}

  private IdeaMainFrame mainframe = null;
  public OutsideTool(IdeaMainFrame mainframe ) {

    super( mainframe, "外部ツール構成", true );
    this.mainframe = mainframe;

    try  {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    setTitle(mainframe.getBilingualMsg("0087"));

    setSize(550,350);
		setResizable(false);

    //フレームサイズ
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

		//outsidetoolファイルから既存のツールの情報を取得
    DashDefaults dashDefaults = new DashDefaults();
    dashDefaults.loadDefaults();
    dashdir = dashDefaults.getDashdir();

    String dirpath = "";
    if (!dashdir.toString().endsWith(File.separator)) {
        dirpath = dashdir.toString() + File.separator;
    }
    String FilePath = dirpath + "properties" + File.separator + "outsidetool";

		FileReader f_in;
		BufferedReader b_in;
		String sLine = "";

		try {
/*
			f_in = new FileReader(FilePath);
			b_in = new BufferedReader(f_in);
*/			

			b_in = new BufferedReader(new InputStreamReader(
									new FileInputStream(FilePath), "JISAutoDetect"));
		
			while((sLine = b_in.readLine()) != null) {

				StringTokenizer st = new StringTokenizer(sLine,",");
				int cnt = 0;
				while (st.hasMoreTokens()) {
					String data = st.nextToken();

					switch (cnt){
						case 0: nameList.addElement(data); nameChkList.addElement(data.toLowerCase()); break;
						case 1: pathList.addElement(data); break;
						case 2: numList.addElement(data);  break;
					}
					cnt++;
				}

			}
			b_in.close();

		} catch(Exception ex) {
		}

		//ツール側
		JPanel listPnl = new JPanel(new BorderLayout());
		String[] listData = new String[nameList.size()];
		for( int i=0; i < nameList.size(); i++ ){
			listData[i] = (String)nameList.elementAt(i);
		}

		list1 = new JList(listData);
		list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		list1.addListSelectionListener(this);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(list1);
		scrollPane.setPreferredSize(new Dimension(200, 80));
		listPnl.add(scrollPane, BorderLayout.CENTER);

		String addSpaceStr = "";
		if(mainframe.getBilingualMsg("0151").equals("Add")){
			addSpaceStr = "    ";
		}

		JButton editBtn1 = new JButton(mainframe.getBilingualMsg("0151") + addSpaceStr);
		editBtn1.addActionListener(this);
		editBtn1.setActionCommand("add");

		JButton editBtn2 = new JButton(mainframe.getBilingualMsg("0152"));
		editBtn2.addActionListener(this);
		editBtn2.setActionCommand("mod");

		JButton editBtn3 = new JButton(mainframe.getBilingualMsg("0153"));
		editBtn3.addActionListener(this);
		editBtn3.setActionCommand("del");

		JPanel editBtnPnl = new JPanel();
    editBtnPnl.setLayout(new BoxLayout(editBtnPnl, BoxLayout.Y_AXIS));
		editBtnPnl.add(editBtn1);
		editBtnPnl.add(editBtn2);
		editBtnPnl.add(editBtn3);
		editBtnPnl.add(new JLabel(" "));
		editBtnPnl.add(new JLabel(" "));
		editBtnPnl.add(new JLabel(" "));
		editBtnPnl.add(new JLabel(" "));

    JPanel panel1 = new JPanel(new BorderLayout());
    panel1.add(new JLabel(mainframe.getBilingualMsg("0021") + "："),BorderLayout.NORTH);
    panel1.add(listPnl,BorderLayout.CENTER );
    panel1.add(editBtnPnl,BorderLayout.EAST);
    panel1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//詳細、ok、cancelボタン側
		DetailTextArea = new JTextArea();
		DetailTextArea.setEditable(false);
    DetailTextArea.setText("");
    DetailTextArea.setRows(4);
    DetailTextArea.setBackground(Color.lightGray);
    JScrollPane scrollPane2 = new JScrollPane(DetailTextArea);

		JSeparator sep = new JSeparator();
		JPanel sepPane = new JPanel( new BorderLayout() );
		sepPane.add(sep, BorderLayout.CENTER);
		sepPane.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.add(new JLabel(mainframe.getBilingualMsg("0154") + "："), BorderLayout.NORTH);
		panel2.add(scrollPane2, BorderLayout.CENTER);
		panel2.add(sepPane, BorderLayout.SOUTH);
		panel2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		JPanel DecBtnPane = new JPanel();
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(this);
		okBtn.setActionCommand("ok");

		JButton clBtn = new JButton(mainframe.getBilingualMsg("0126"));
		clBtn.addActionListener(this);
		clBtn.setActionCommand("close");

    okBtn.setPreferredSize(new Dimension(100,25));
    clBtn.setPreferredSize(new Dimension(100,25));
		DecBtnPane.add(okBtn);
		DecBtnPane.add(clBtn);
    DecBtnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JPanel allPane = new JPanel();
    allPane.setLayout(new BoxLayout(allPane, BoxLayout.Y_AXIS));
		allPane.add(panel1);
		allPane.add(panel2);
		allPane.add(DecBtnPane);

    getContentPane().add(allPane, BorderLayout.NORTH);

  }



	/**
	* イベント処理
	* @param ActionEvent e
	*/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("add") || action.equals("mod")){
        //追加、修正
				modName = "";
				modPatn = "";
				modNum  = "";
				cancelFlg = false;

      	if( action.equals("mod") ){
      		String objdata = (String)list1.getSelectedValue();

					//修正するデータの詳細をセット
					int modindex = nameList.indexOf(objdata);
					modName = (String)nameList.elementAt(modindex);
					modPatn = (String)pathList.elementAt(modindex);
					modNum  = (String)numList.elementAt(modindex);
      	}

        OutToolSub = new OutsideToolSub (mainframe, this );
        //OutToolSub.show();
		    OutToolSub.setVisible(true);
				if(!cancelFlg){
			    DetailTextArea.setText("");
          listload();
				}
      }
      else if( action.equals("del") ){
        //削除
     		String objdata = (String)list1.getSelectedValue();

				if( objdata.equals("") ){
  	      return;
				}
				else{

					String cstmStr = mainframe.getBilingualMsg("0088");
					cstmStr = cstmStr.replaceAll("filename",objdata);

					Object[] options2 = { mainframe.getBilingualMsg("0191"),mainframe.getBilingualMsg("0192") };

					int jgcnt = JOptionPane.showOptionDialog(this,
											cstmStr,mainframe.getBilingualMsg("0130"),
											JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
											null, options2, options2[0]);
          if( jgcnt != 0 ){
            return;
          }
				}

				//削除するデータのインデックスを取得
				int delindex = nameList.indexOf(objdata);

				nameList.remove(delindex);
				pathList.remove(delindex);
				numList.remove(delindex);

				//リスト更新
		    DetailTextArea.setText("");
				list1.setListData(nameList);

      }
      else if( action.equals("ok") ){
      	writeFile();
      	dispose();
      }
      else if(action.equals("close")){
        //キャンセルを押された場合
        dispose();
      }

    }catch(Exception e2){

    }

  }

	/**
	* リストを更新
	**/
	public void listload(){

		//編集された名称が存在するかを判別

		//delivNameが""の場合はキャンセルを押されている
		if( !delivName.equals("") ){

			if( !modName.equals("") ){
				//存在する場合(修正の場合)
				int upno = nameChkList.indexOf(modName);
				nameList.setElementAt(delivName,upno);
				nameChkList.setElementAt(delivName.toLowerCase(),upno);
				pathList.setElementAt(delivPatn,upno);
				numList.setElementAt(delivNum,upno);

			}
			else{
				//存在しない場合(追加の場合)
				nameList.addElement(delivName);
				nameChkList.addElement(delivName.toLowerCase());
				pathList.addElement(delivPatn);
				numList.addElement(delivNum);
			}

		}

		//リスト更新
		list1.setListData(nameList);

	}

  /**
  * outsidetoolファイルに書込み
  */
  private boolean writeFile() throws IOException , java.lang.Exception{

    try {

	    String dirpath = "";
	    if (!dashdir.toString().endsWith(File.separator)) {
	        dirpath = dashdir.toString() + File.separator;
	    }
	    String FilePath = dirpath + "properties" + File.separator + "outsidetool";

			//ファイル書込み処理
			File fp  = new File ( FilePath );
			FileOutputStream fos = new FileOutputStream (fp);
			PrintWriter pw  = new PrintWriter (fos);

			//既存のファイル
			for(int i=0; i < nameList.size(); i++){
				String writeName = (String)nameList.elementAt(i);
				String writePath = (String)pathList.elementAt(i);
				String writeNum  = (String)numList.elementAt(i);

				String writeStr = writeName + "," + writePath + "," + writeNum;
        pw.println(writeStr);
      }

      pw.close ();

    } catch ( Exception e ){
      return false;
    }

    return true;
  }

	/**
	* 詳細表示
	* @param ListSelectionEvent e
	*/
	public void valueChanged(ListSelectionEvent e) {

		String objdata = (String)list1.getSelectedValue();

		//データの詳細を取得
		int modindex = nameList.indexOf(objdata);
		String showName = (String)nameList.elementAt(modindex);
		String showPatn = (String)pathList.elementAt(modindex);
		String chkNum   = (String)numList.elementAt(modindex);
		String showNum = "";
		if( chkNum.equals("1") ){
			showNum = mainframe.getBilingualMsg("0066");
		}
		else{
			showNum = mainframe.getBilingualMsg("0156");
		}

		String showMsg = mainframe.getBilingualMsg("0140") + "：" + showName + "\r\n"
                   + mainframe.getBilingualMsg("0065") + "：" + showPatn + "\r\n"
                   + mainframe.getBilingualMsg("0155") + "：" + showNum;

    DetailTextArea.setText(showMsg);

	}

	/**
	* Enterキー処理
	*/
  public void ok(){
    try{

			writeFile();
  	  dispose();

    } catch ( Exception e2 ){
    }
  }

	/**
	* Escキー処理
	*/
  public void cancel(){
    dispose();
  }


}

