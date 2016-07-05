package dash;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>�^�C�g��:�r���[�A�̃t���[�� </p>
 * <p>����:�C���^�t�F�[�XViewer�̎����B </p>
 * <p>���쌠: Copyright (c) 2002</p>
 * <p>��Ж�: </p>
 * @author ������
 * @version 1.0
 */

public class Viewer extends JFrame implements ActionListener {
  /** DVM */
  private DVM dvm;

  /** ���|�W�g���^DVM�Ȃ�true */
  private boolean isRtype;

  /** �t���[�� */
  private JFrame ifFrame;

  /** �L�����o�X */
  protected ViewerCanvas canvas;

  protected JTextArea attrArea;     // ���b�Z�[�W�\��(content�ȊO)
  protected JTextArea contArea;     // ���b�Z�[�W�\��(content�̂�)

  /**
   * �R���X�g���N�^
   * @param dvmname  DVM��(�z�X�g���܂܂Ȃ�)+DVM�̋N�����Ă���z�X�g��
   * @param dvmparam
   */
  public Viewer(String dvmname, DVM dvmparam) {

    super(dvmname);

    this.dvm = dvmparam;
    this.isRtype = dvmparam.isRtype();

    ifFrame = this;

    // ���j���[�쐬
    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);
    makeMenuBar(menubar);

    // �L�����o�X
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
    // �`�F�b�N�{�b�N�X
    //-------------------------------------------------
    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayout(0, 3));
    CheckboxGroup cg = new CheckboxGroup();
    JRadioButton cb1 = new JRadioButton("non-stop", true);
    JRadioButton cb2 = new JRadioButton("step", false);

    // Swing�̃��W�I�{�^���̃O���[�v��
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
    canvas.stepLabel = stepLabel;   // ViewerCanvas�ɋ�����

    // �e�L�X�g
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
        setSize(650, 600); // ���|�W�g��
      else
        setSize(600, 600); // �����
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
   * ������
   */
  public void initialize() {
    canvas.initialize();

		//------------------------------------------------------------------
		// �r���N���Ή�
		// �r���[�A���r���ŋN�����ꂽ�ꍇ�ɁA���Ɋ��ɓǂݍ��܂�Ă���
		// �G�[�W�F���g���r���[�A�ɕ\������B
		// ���ɓǂݍ��܂�Ă���G�[�W�F���g�́Advm���̃n�b�V���e�[�u��agTable��
		// �i�[����Ă���B
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
   * ���j���[�쐬
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


    // �A�C�R�����j���[
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


    // ���|�W�g�����j���[
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
   * ���j���[�̏���-flush���j���[
   */
  void flushMenu_actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    int num = Integer.parseInt(command);
  //  if (num <= 3)
      //canvas.setFlush(num);
  }

  /**
   * ���j���[�̏���-icon���j���[
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
   * ���j���[�̏���-repository���j���[
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
   * jRadioButton�̏���
   */
  void jRadioButton_actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    canvas.changeItem(command);
  }


  public void actionPerformed(ActionEvent e) {
  }


  /***********************************************************************
                            ������Ăяo��������
  ***********************************************************************/

  /**
   * ���b�Z�[�W��\������. AdipsEnv.deliverMsg(),sendMsg()����Ă΂��B
   * @param m ���b�Z�[�W
   * @param toAnotherEnv ���̊��ւ̃��b�Z�[�W�Ȃ�true
   * @param isR ���̊������|�W�g���Ȃ�true
   */
	public void showMsg(DashMessage m) {
		canvas.showMsg(m, isRtype);
  }

  /**
   * �V�����G�[�W�F���g��ǉ�����B
   * @param msg   CreateInstance���b�Z�[�W�A
   *              �܂���null(���|�W�g�����N���XAg�𐶐������ꍇ)
   * @param name  �G�[�W�F���g��
   */
  public void showNewAgent(String origin, String name) {
    canvas.showNewAgent(origin, name);
    //System.out.println(dvm.agTable.get(name));
  }


  void addAgent(final String name) {
  	//System.out.println ("addAgent:" + name);
  }

  /**
   * �G�[�W�F���g���폜����B
   * @param name  �G�[�W�F���g��
   */
  public void removeAgent(String name) {
    canvas.showRemoveAgent(name);
  }


/***********************************************************************
                       ������Ăяo����Ȃ�����
***********************************************************************/

/**
 * ���b�Z�[�W��content���e�L�X�g�G���A�ɕ`���B
 * animeSync�����邽�߂ɁAViewerCanvas����Ă΂���B
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