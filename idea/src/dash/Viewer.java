package dash;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>タイトル:ビューアのフレーム </p>
 * <p>説明:インタフェースViewerの実装。 </p>
 * <p>著作権: Copyright (c) 2002</p>
 * <p>会社名: </p>
 * @author 未入力
 * @version 1.0
 */

public class Viewer extends JFrame implements ActionListener {
  /** DVM */
  private DVM dvm;

  /** リポジトリ型DVMならtrue */
  private boolean isRtype;

  /** フレーム */
  private JFrame ifFrame;

  /** キャンバス */
  protected ViewerCanvas canvas;

  protected JTextArea attrArea;     // メッセージ表示(content以外)
  protected JTextArea contArea;     // メッセージ表示(contentのみ)

  /**
   * コンストラクタ
   * @param dvmname  DVM名(ホスト名含まない)+DVMの起動しているホスト名
   * @param dvmparam
   */
  public Viewer(String dvmname, DVM dvmparam) {

    super(dvmname);

    this.dvm = dvmparam;
    this.isRtype = dvmparam.isRtype();

    ifFrame = this;

    // メニュー作成
    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);
    makeMenuBar(menubar);

    // キャンバス
    if (isRtype)
      //canvas = new ViewerCanvasW(dvmname, this);
			canvas = new ViewerCanvasR(dvmname, this);
    else
      canvas = new ViewerCanvasW(dvmname, this);

    BorderLayout borderLayout1 = new BorderLayout();
    this.getContentPane().setLayout(borderLayout1);
    JPanel panel0 = new JPanel();
    this.getContentPane().add("Center", canvas);
		//canvas.setToolTipText("AAA");

		if (!isRtype) {
	    canvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
	      public void mouseMoved(MouseEvent e) {
	        contentPane_mouseMoved(e);
	      }
	    });
		}

    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
    });

    //-------------------------------------------------
    // チェックボックス
    //-------------------------------------------------
    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayout(0, 3));
    CheckboxGroup cg = new CheckboxGroup();
    JRadioButton cb1 = new JRadioButton("non-stop", true);
    JRadioButton cb2 = new JRadioButton("step", false);

    // Swingのラジオボタンのグループ化
    ButtonGroup group = new ButtonGroup();
    group.add(cb1);
    group.add(cb2);


    //cb1.addItemListener(this);
    cb1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jRadioButton_actionPerformed(e);
        }
    });
    cb1.setActionCommand ("non-stop");
    panel1.add(cb1);

    //cb2.addItemListener(this);
    cb2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jRadioButton_actionPerformed(e);
        }
    });
    cb2.setActionCommand ("step");
    panel1.add(cb2);


    JLabel stepLabel = new JLabel("0", Label.RIGHT);

    panel1.add(stepLabel);
    canvas.stepLabel = stepLabel;   // ViewerCanvasに教える

    // テキスト
    attrArea = new JTextArea("", 10, 20);
    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.getViewport().add(attrArea);
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder());

    contArea = new JTextArea("", 10, 20);
    JScrollPane jScrollPane2 = new JScrollPane();
    jScrollPane2.getViewport().add(contArea);
    jScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane2.setBorder(BorderFactory.createEtchedBorder());

    JPanel taPanel = new JPanel();
    taPanel.setLayout(new BorderLayout());
    taPanel.add("West",jScrollPane1);
    taPanel.add("Center",jScrollPane2);

    JPanel panel2 = new JPanel();

    panel2.setLayout(new BorderLayout());

    panel2.add("Center", taPanel);
    panel2.add("South", panel1);

    getContentPane().add("South", panel2);


    Dimension d = getToolkit().getScreenSize();
    if( d.getWidth() < 1024 ){
      setSize( 400, 560 );
      if( !isRtype ){
        setLocation( 400, 0 );
      }
    }
    else{
      if (isRtype)
        setSize(650, 600); // リポジトリ
      else
        setSize(600, 600); // 動作環境
    }

  }

  void contentPane_mouseMoved(MouseEvent e) {
		//System.out.println ("contentPane_mouseMoved");
		VIcon vicon = canvas.getIconAt(e.getPoint());

		VIconAg viconag = (VIconAg)vicon;
		if (vicon == null ) {
			canvas.setToolTipText("");
		}
		else {
			canvas.setToolTipText(viconag.getName());
		}
  }

  /**
   * 初期化
   */
  public void initialize() {
    canvas.initialize();

		//------------------------------------------------------------------
		// 途中起動対応
		// ビューアが途中で起動された場合に、既に環境に読み込まれている
		// エージェントをビューアに表示する。
		// 既に読み込まれているエージェントは、dvm内のハッシュテーブルagTableに
		// 格納されている。
		//------------------------------------------------------------------
		canvas.changeItem("non-stop");
		for (Enumeration ee = dvm.agTable.keys(); ee.hasMoreElements(); ) {
			Object receiverName = ee.nextElement();
			DashAgent receiver = (DashAgent)dvm.agTable.get(receiverName);
			canvas.showNewAgent(receiver.getOrigin(), receiver.getName());
		}
		canvas.changeItem("step");

  }

  /**
   * メニュー作成
   * @param JMenuBar menubar
   */
  private void makeMenuBar(JMenuBar menubar) {
    JMenu flushMenu = new JMenu("Flush");
    JMenuItem flushMenuItem1 = new JMenuItem("0");
    JMenuItem flushMenuItem2 = new JMenuItem("1");
    JMenuItem flushMenuItem3 = new JMenuItem("2");
    flushMenu.add(flushMenuItem1);
    flushMenu.add(flushMenuItem2);
    flushMenu.add(flushMenuItem3);
    flushMenuItem1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          flushMenu_actionPerformed(e);
        }
    });
    flushMenuItem2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          flushMenu_actionPerformed(e);
        }
    });
    flushMenuItem3.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          flushMenu_actionPerformed(e);
        }
    });
    menubar.add(flushMenu);


    // アイコンメニュー
    JMenu smenu = new JMenu ("scale");

    JMenuItem iconMenuItem1 = new JMenuItem("unselect");

    int bai=100;
    if (isRtype) {
       bai = canvas.r_bai;
    }
    else {
       bai = canvas.w_bai;
    }
    JRadioButtonMenuItem iconMenuItem2 = new JRadioButtonMenuItem("100%", bai==100? true : false);
    JRadioButtonMenuItem iconMenuItem3 = new JRadioButtonMenuItem("90%", bai==90? true : false);
    JRadioButtonMenuItem iconMenuItem4 = new JRadioButtonMenuItem("80%", bai==80? true : false);
    JRadioButtonMenuItem iconMenuItem5 = new JRadioButtonMenuItem("70%", bai==70? true : false);
    ButtonGroup bgroup = new ButtonGroup ( );
    bgroup.add (iconMenuItem2 );
    bgroup.add (iconMenuItem3 );
    bgroup.add (iconMenuItem4 );
    bgroup.add (iconMenuItem5 );

    //JMenuItem iconMenuItem2 = new JMenuItem("show script");
    JMenu iconMenu = new JMenu("Icon");
    iconMenu.add(iconMenuItem1);
    //iconMenu.add(iconMenuItem2);
    iconMenu.add (smenu );
    smenu.add(iconMenuItem2);
    smenu.add(iconMenuItem3);
    smenu.add(iconMenuItem4);
    smenu.add(iconMenuItem5);


    iconMenuItem1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iconMenu_actionPerformed(e);
        }
    });
    iconMenuItem2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iconMenu_actionPerformed(e);
        }
    });
    iconMenuItem3.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iconMenu_actionPerformed(e);
        }
    });
    iconMenuItem4.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iconMenu_actionPerformed(e);
        }
    });
    iconMenuItem5.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iconMenu_actionPerformed(e);
        }
    });

    menubar.add(iconMenu);


    // リポジトリメニュー
    if (isRtype) {

      JMenuItem repositoryMenutem1 = new JMenuItem("grid");
      //JMenuItem repositoryMenutem2 = new JMenuItem("show script");
      JMenuItem repositoryMenutem3 = new JMenuItem("save icon location");

      JMenu repositoryMenu = new JMenu("repository");
      repositoryMenu.add(repositoryMenutem1);
      //repositoryMenu.add(repositoryMenutem2);
      repositoryMenu.add(repositoryMenutem3);

      repositoryMenutem1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            repositoryMenu_actionPerformed(e);
          }
      });

	/*
      repositoryMenutem2.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            repositoryMenu_actionPerformed(e);
          }
      });
*/

      repositoryMenutem3.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            repositoryMenu_actionPerformed(e);
          }
      });

      menubar.add(repositoryMenu);
    }

  }


  /**
   * メニューの処理-flushメニュー
   */
  void flushMenu_actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    int num = Integer.parseInt(command);
  //  if (num <= 3)
      //canvas.setFlush(num);
  }

  /**
   * メニューの処理-iconメニュー
   */
  void iconMenu_actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("unselect"))
      canvas.unselectIcon();
    else if (command.equals("show script")) {
			//System.out.println ("show script");
      String s = canvas.getAgentNameOfSelectedIcon();
      if (s != null) {
				//System.out.println ("call dvm.showAgentScript(s)");
        dvm.showAgentScript(s);
			}
			else {
				//System.out.println ("not call dvm.showAgentScript(s)");
			}
    }
    else if (command.equals("100%")) {
       canvas.setIconScale(100);
    }
    else if (command.equals("90%")) {
       canvas.setIconScale(90);
    }
    else if (command.equals("80%")) {
       canvas.setIconScale(80);
    }
    else if (command.equals("70%")) {
       canvas.setIconScale(70);
    }
  }

  /**
   * メニューの処理-repositoryメニュー
   */
  void repositoryMenu_actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("grid")) {
      canvas.gridSort();
		}
    else if (command.equals("show script")) {
		// DEBUG
          for (Enumeration ee = dvm.agTable.keys(); ee.hasMoreElements(); ) {
            Object receiverName = ee.nextElement();
            //if (!receiverName.equals(msg.from)) {
              DashAgent receiver = (DashAgent)dvm.agTable.get(receiverName);
							//System.out.println (receiver.getFilename() + "-" + receiver.getOrigin() );
              //receiver.putMsg(msg);
            //}
          }

      //adipsEnv.showAgentScript(null);
		}
    else if (command.equals("save icon location")) {
      ((ViewerCanvasR)canvas).saveIconLocation();
		}
  }


  /**
   * jRadioButtonの処理
   */
  void jRadioButton_actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    canvas.changeItem(command);
  }


  public void actionPerformed(ActionEvent e) {
  }


  /***********************************************************************
                            環境から呼び出されるもの
  ***********************************************************************/

  /**
   * メッセージを表示する. AdipsEnv.deliverMsg(),sendMsg()から呼ばれる。
   * @param m メッセージ
   * @param toAnotherEnv 他の環境へのメッセージならtrue
   * @param isR 他の環境がリポジトリならtrue
   */
	public void showMsg(DashMessage m) {
		canvas.showMsg(m, isRtype);
  }

  /**
   * 新しいエージェントを追加する。
   * @param msg   CreateInstanceメッセージ、
   *              またはnull(リポジトリがクラスAgを生成した場合)
   * @param name  エージェント名
   */
  public void showNewAgent(String origin, String name) {
    canvas.showNewAgent(origin, name);
    //System.out.println(dvm.agTable.get(name));
  }


  void addAgent(final String name) {
  	//System.out.println ("addAgent:" + name);
  }

  /**
   * エージェントを削除する。
   * @param name  エージェント名
   */
  public void removeAgent(String name) {
    canvas.showRemoveAgent(name);
  }


/***********************************************************************
                       環境から呼び出されないもの
***********************************************************************/

/**
 * メッセージのcontentをテキストエリアに描く。
 * animeSyncさせるために、ViewerCanvasから呼ばせる。
 */
void writeMsg(DashMessage m) {
  attrArea.setText(m.toStringWithoutContent());
  attrArea.select(0, 0);
  contArea.setText(m.content.toString());
  contArea.select(0, 0);
}


public void this_windowClosing( WindowEvent e ){
  canvas.changeItem("non-stop");
	this.dispose ( );
	dvm.viewer = null;
}

public void close( ){
  canvas.changeItem("non-stop");
	this.dispose ( );
	dvm.viewer = null;
}


}