package dash;

import java.awt.*;
import javax.swing.JPanel;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.text.BadLocationException;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import java.io.*;

/**
 * <p>OutsideToolSub: </p>
 * <p>外部ツール編集画面</p>
 */
public class OutsideToolSub extends EnhancedDialog implements ActionListener{

  private IdeaMainFrame mainframe = null;
  private OutsideTool outsidetool = null;
  private JTextField nameArea = new JTextField("",30);
  private JTextField pathArea = new JTextField("",30);
  private JRadioButton radio1 = null;
  private JRadioButton radio2 = null;
	private String modName = "";
	private String modPath = "";
	private String modNum  = "";
	JFileChooser fdlg = null;

  public OutsideToolSub(IdeaMainFrame mainframe, OutsideTool outsidetool ) {

    super( mainframe, "追加", true );
    this.mainframe = mainframe;

		this.outsidetool = outsidetool;

    try  {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    setTitle(mainframe.getBilingualMsg("0087"));
    setSize(520,235);
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

		//修正の場合はデータの詳細を取得
		modName = outsidetool.getmodName();
		modPath = outsidetool.getmodPath();
		modNum  = outsidetool.getmodNum();

		//ラベル位置調節
		String addSpace1 = "";
		String addSpace2 = "";

		if( mainframe.getBilingualMsg("0140").equals(("Name")) ){
			addSpace1 = "               ";
			addSpace2 = "         ";
		}
		else{
			addSpace1 = "　　　　";
			addSpace2 = "　　　　";
		}

		//名前
		JPanel panel1 = new JPanel();
		JLabel label1 = new JLabel(mainframe.getBilingualMsg("0140") + addSpace1 + "：");
    panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel1.add(label1);
		panel1.add(nameArea);
		nameArea.setText(modName);

		//場所
		JPanel panel2 = new JPanel();
		JLabel label2 = new JLabel(mainframe.getBilingualMsg("0065") + "：");
		JButton selBtn = new JButton("…");
    selBtn.addActionListener(this);
    selBtn.setActionCommand("dirsrch");
    panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel2.add(label2);
		panel2.add(pathArea);
		panel2.add(selBtn);
		pathArea.setText(modPath);

		//引数
		boolean flg1 = false;
		boolean flg2 = false;
		if( modNum.equals("1") || modNum.equals("") ){
			flg1 = true;
		}
		else{
			flg2 = true;
		}

	  radio1 = new JRadioButton(mainframe.getBilingualMsg("0066"), flg1);
	  radio2 = new JRadioButton(mainframe.getBilingualMsg("0156"), flg2);

		JPanel radPane = new JPanel();
		ButtonGroup group = new ButtonGroup();
		group.add(radio1);
		group.add(radio2);
    radPane.setLayout(new BoxLayout(radPane, BoxLayout.Y_AXIS));
		radPane.add(radio1);
		radPane.add(radio2);
		Border inborder1 = new EtchedBorder(EtchedBorder.LOWERED);
		Border border1 = new TitledBorder(inborder1, "", TitledBorder.RIGHT, TitledBorder.TOP);
		radPane.setBorder(border1);

		JPanel panel3 = new JPanel(new BorderLayout());
		JLabel label3 = new JLabel(mainframe.getBilingualMsg("0155") + addSpace2 + "：");
		JPanel labPane = new JPanel(new BorderLayout());
		labPane.add(label3,BorderLayout.NORTH);
		labPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
		panel3.add(labPane, BorderLayout.WEST);
		panel3.add(radPane, BorderLayout.CENTER);
		panel3.setBorder(BorderFactory.createEmptyBorder(5,5,10,80));

		//区切り線
		JSeparator sep = new JSeparator();
		JPanel sepPane = new JPanel( new BorderLayout() );
		sepPane.add(sep, BorderLayout.CENTER);
		sepPane.setBorder(BorderFactory.createEmptyBorder(10,5,5,5));

		//ok,cancelボタン
		JPanel panel4 = new JPanel();
		JButton okBtn = new JButton("OK");
		JButton clBtn = new JButton(mainframe.getBilingualMsg("0126"));
    okBtn.addActionListener(this);
    okBtn.setActionCommand("ok");
    clBtn.addActionListener(this);
    clBtn.setActionCommand("close");
    okBtn.setPreferredSize(new Dimension(100,25));
    clBtn.setPreferredSize(new Dimension(100,25));
		panel4.add(okBtn);
		panel4.add(clBtn);
    panel4.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JPanel allPane = new JPanel();
    allPane.setLayout(new BoxLayout(allPane, BoxLayout.Y_AXIS));
		allPane.add(panel1);
		allPane.add(panel2);
		allPane.add(panel3);
		allPane.add(sepPane);
		allPane.add(panel4);

    getContentPane().add(allPane, BorderLayout.NORTH);

  }

	/**
	* イベント処理
	* @param ActionEvent e 
	*/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("dirsrch")){
        openfile();
      }
      else if(action.equals("ok")){
        //OKを押された場合
        if( deliver() ){
        	dispose();
        }

      }
      else if(action.equals("close")){
        //キャンセルを押された場合
        outsidetool.setcancelFlg();
        dispose();
      }

    }catch(Exception e2){

    }

  }

  /**
  * 外部ツール選択
  */
  public void openfile() {
    Container cont;
    cont = this.getParent();

    JFileChooser fileChooser = new JFileChooser("c:/");
    fileChooser.setDialogTitle("選択");
    int ret = fileChooser.showOpenDialog(cont);
    File file = fileChooser.getSelectedFile();

    if (ret!=JFileChooser.APPROVE_OPTION || file==null){
      return;
    }

    //設定
    String abspath = fileChooser.getSelectedFile().getAbsolutePath();
		pathArea.setText(abspath);

  }

  /**
  * 入力されたデータを親に渡す
  */
	public boolean deliver(){

		try{

			String name = nameArea.getText();
      Object[] options = { "OK" };
			if(name.equals("")){
        //JOptionPane.showMessageDialog(this,"名称を入力してください。");
        JOptionPane.showOptionDialog(this,
            mainframe.getBilingualMsg("0089"),mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
			}

			String path = pathArea.getText();
			if(path.equals("")){
        //JOptionPane.showMessageDialog(this,"実行ファイルを選択してください。");
        JOptionPane.showOptionDialog(this,
            mainframe.getBilingualMsg("0090"),mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
			}

      if (!new File(path).exists() ) {
        JOptionPane.showOptionDialog(this,
            mainframe.getBilingualMsg("0091"),mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
        return false;
      }
			String num = "";
			if(radio1.isSelected()){
				num = "1";
			}
			else if(radio2.isSelected()){
				num = "0";
			}

			//重複チェック
			if( modName.equals("") ){
				//追加の場合のみチェック
				if( outsidetool.chkNameList(name) ){
					//名称が既に存在していた場合
	        //JOptionPane.showMessageDialog(this,"名称 \"" + name + "\" は既に存在します。");

					String cstmStr = mainframe.getBilingualMsg("0092");
					cstmStr = cstmStr.replaceAll("filename",name);

          JOptionPane.showOptionDialog(this,
              cstmStr,mainframe.getBilingualMsg("0129"),
                                     JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                     null, options, options[0]);
  	      return false;
				}
			}

			//データをセット
			outsidetool.setdelivInfo(name,path,num);


		} catch ( Exception e ){
      return false;
    }

    return true;

	}

	/**
	* Enterキー処理
	*/
  public void ok(){
		if( deliver() ){
			dispose();
		}
  }

	/**
	* Escキー処理
	*/
  public void cancel(){
    dispose();
  }


}

