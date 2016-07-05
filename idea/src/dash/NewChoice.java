package dash;

import java.awt.*;
import javax.swing.JPanel;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.border.*;

/**
 * <p>NewChoice: </p>
 * <p>新規で作成したいファイルを選択する画面: </p>
 */

public class NewChoice extends EnhancedDialog implements ActionListener,MouseListener{

  private IdeaMainFrame mainframe = null;
  private Project project = null;
  private JTextField nameArea = null;
  private Border border1;
	private JLabel[] newLabel = null;
  private int kindNo = 0;
  NewProject NewPrjWin = null;
  NewFile NewFileWin = null;

  public NewChoice(IdeaMainFrame mainframe, Project prj) {

    super( mainframe, "新規プロジェクト", true );
    project = prj;
    this.mainframe = mainframe;

    try  {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    setTitle(mainframe.getBilingualMsg("0002"));
    setSize(500,180);
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

		//ファイル種類選択
		newLabel = new JLabel[5];

		String[] nameAr = new String[newLabel.length];
		nameAr[0] = mainframe.getBilingualMsg("0136");
		nameAr[1] = mainframe.getBilingualMsg("0137");
		nameAr[2] = mainframe.getBilingualMsg("0138");
    nameAr[3] = mainframe.getBilingualMsg("0206");
    nameAr[4] = mainframe.getBilingualMsg("0207");

		String[] pathAr = new String[newLabel.length];
		pathAr[0] = "resources/newfileProject.gif";
		pathAr[1] = "resources/newfileDash.gif";
		pathAr[2] = "resources/newfileRset.gif";
    pathAr[3] = "resources/newfileBp.gif";
    pathAr[4] = "resources/newfileEtc.gif";

    JLabel[] dashiconLbl = new JLabel[newLabel.length];
		JPanel[] dashPanel   = new JPanel[newLabel.length];

    JPanel kindPanel = new JPanel();
    kindPanel.setBackground(Color.WHITE);
    kindPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		for( int i=0; i < newLabel.length; i++ ){

			//文字
			newLabel[i] = new JLabel(nameAr[i]);
			newLabel[i].setOpaque(true);
			newLabel[i].setBackground(Color.white);
			newLabel[i].setForeground(Color.black);
			newLabel[i].setVerticalAlignment(JLabel.CENTER);
			newLabel[i].setHorizontalAlignment(JLabel.CENTER);

			//イメージ
	    dashiconLbl[i] = new JLabel();
	    ImageIcon dashicon = getImageIcon ( pathAr[i] );
			dashiconLbl[i].setIcon(dashicon);
			dashiconLbl[i].setVerticalAlignment(JLabel.CENTER);
			dashiconLbl[i].setHorizontalAlignment(JLabel.CENTER);

			//プロジェクトが作成されていない場合
			//Dashファイル or ルールセット
			if( i ==1 || i == 2 ){
				if( project == null ){
					//プロジェクトが作成されていない場合は、選択不可
					newLabel[i].setEnabled(false);
					dashiconLbl[i].setEnabled(false);
				}
				else{
					//プロジェクトが作成されている場合はイベント処理
					newLabel[i].addMouseListener(this);
					dashiconLbl[i].addMouseListener(this);

				}
			}
			else{
				//Dashファイル or ルールセット以外の場合
				newLabel[i].addMouseListener(this);
				dashiconLbl[i].addMouseListener(this);

			}

			//文字とイメージを組み合わせる
	    dashPanel[i] = new JPanel( new BorderLayout() );
	    dashPanel[i].setBackground(Color.WHITE);
	    dashPanel[i].add(dashiconLbl[i], BorderLayout.CENTER);
	    dashPanel[i].add(newLabel[i], BorderLayout.SOUTH);
	    dashPanel[i].setBorder(BorderFactory.createEmptyBorder(10,10,20,10));

			kindPanel.add(dashPanel[i]);

		}

    for( int i=0; i < newLabel.length; i++ ){
      newLabel[i].addKeyListener(new KeyHandler());
      dashiconLbl[i].addKeyListener(new KeyHandler());
    }

		//スクロール
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(kindPanel);

    //OKキャンセルボタン
    JPanel panel4 = new JPanel();
    JButton okbtn = new JButton("OK");
    JButton clbtn = new JButton(mainframe.getBilingualMsg("0126"));
    okbtn.setPreferredSize(new Dimension(100,25));
    clbtn.setPreferredSize(new Dimension(100,25));
    okbtn.addActionListener(this);
    okbtn.setActionCommand("ok");
    clbtn.addActionListener(this);
    clbtn.setActionCommand("close");
    panel4.setLayout(new FlowLayout(FlowLayout.RIGHT));
    panel4.add(okbtn);
    panel4.add(clbtn);

    JPanel connectPanel = new JPanel();
    connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.Y_AXIS));
		connectPanel.add(scrollPane);
    connectPanel.add(panel4);

    getContentPane().add(connectPanel, BorderLayout.NORTH);

  }

	public void mouseClicked(MouseEvent e){
		/* マウスがクリックされた時の処理 */
    if (e.getClickCount() == 2 ) {
      ok();
    }
	}

	public void mouseEntered(MouseEvent e){
		/* マウスがコンポーネントの上に乗った時の処理 */
	}

	public void mouseExited(MouseEvent e){
		/* マウスがコンポーネントの外に出た時の処理 */
	}

	public void mousePressed(MouseEvent e){
		/* マウスが押された時の処理 */
    JLabel lbl = (JLabel)e.getSource();
    String lblCaption = lbl.getText();
    if (lblCaption.equals("") ) {
      // イメージ用のラベル
      JPanel panel = (JPanel)lbl.getParent();

      for (int i=0; i<panel.getComponentCount(); i++ ) {
        JLabel lbl2 = (JLabel)panel.getComponent(i);
        if (!lbl2.getText().equals("") ) {
          lblCaption = lbl2.getText();
          break;
        }
      }
    }
    cngdata( lblCaption );
	}

	public void mouseReleased(MouseEvent e){
		/* マウスが押された後、離された時の処理 */
	}

	private void cngdata( String inName ){

		for( int i=0; i < newLabel.length; i++ ){
			if( newLabel[i].getText().equals(inName) ){
				newLabel[i].setBackground(Color.blue);
				newLabel[i].setForeground(Color.white);
				kindNo = i+1;
			}
			else{
				newLabel[i].setBackground(Color.white);
				newLabel[i].setForeground(Color.black);
			}
		}

	}

	/**
	* イベント処理
	* @param ActionEvent e <BR>
	*/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("ok")){
        ok();
      }
      else if(action.equals("close")){
        //キャンセルを押された場合
        dispose();
      }

    }catch(Exception e2){
    }

  }

	/**
	* Enterキー処理
	*/
  public void ok(){

		try {
      //ファイル作成処理
      if( kindNo == 1 ){

				dispose();

        //プロジェクト
        NewPrjWin = new NewProject (this.mainframe,project);
        //NewPrjWin.show();
		    NewPrjWin.setVisible(true);
      }
      else if( kindNo == 2 || kindNo == 3 || kindNo == 4 || kindNo == 5){

				dispose();

				//Dashファイル or ルールセット or ベースプロセス or その他ファイル
        NewFileWin = new NewFile (this.mainframe,project, kindNo );
        //NewFileWin.show();
		    NewFileWin.setVisible(true);

			}
      else{
        Object[] options = { "OK" };
        JOptionPane.showOptionDialog(this,
            mainframe.getBilingualMsg("0139"),mainframe.getBilingualMsg("0129"),
                                   JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                   null, options, options[0]);
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

  class KeyHandler extends KeyAdapter
  {

    public void keyPressed(KeyEvent evt)
    {
      switch(evt.getKeyCode())
      {
      case KeyEvent.VK_UP:
      case KeyEvent.VK_DOWN:
        System.out.println("KeyEvent");
        break;
      }
    }
  }

}