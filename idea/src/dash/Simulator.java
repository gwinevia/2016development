package dash;

import dashviz.*;
import java.awt.*;
import javax.swing.JPanel;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.border.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>�^�C�g���F����V�~�����[�g</p>
 * @author cosmosweb nakagawa
 * @version 1.0
 */
public class Simulator extends JPanel implements ActionListener {

	public JDesktopPane inspectorDesktop = null;

	/** �^�u */
	private JTabbedPane logTabbedPaneR;

	private JTabbedPane logTabbedPaneW;

	/** �G���[��\�����镔�i */
	private JTextArea errorLogAreaR;

	private JScrollPane errorScrollPaneR;

	
	private int errorLogAreaLinesR;

	private JTextArea errorLogAreaW;

	private JScrollPane errorScrollPaneW;

	private int errorLogAreaLinesW;

	/** �S�Ẵ��O��\�����镔�i */
	private JTextArea logAreaR;

	private JScrollPane logScrollPaneR;

	private int logAreaLinesR;

	private JTextArea logAreaW;

	private JScrollPane logScrollPaneW;

	private int logAreaLinesW;

	/** ACL�G�f�B�^ */
	private AclPanel aclPanelR;

	private AclPanel aclPanelW;

	/** ACL�G�f�B�^�ւ̃��b�Z�[�W��\�����镔�i */
	private JTextArea receiveAreaR;

	private JTextArea receiveAreaW;

	/** ���b�Z�[�W�̓��e��\�����镔�i */
	private JTextArea msgAreaR;

	private JTextArea msgAreaW;

	/** �^�u */
	private JTabbedPane logTabbedPane;

	private BorderLayout borderLayout1 = new BorderLayout();

	/** WorkPlacePanel */
	WorkplacePanel[] wpPanel = null;

	/** RepositoryPanel */
	RepositoryPanel repPanel = null;

	/** ���[�N�v���[�X�r���[�A */
	public ViewerCanvasW2[] canvasW = null;

	/** ���|�W�g���r���[�A */
	public ViewerCanvasR2 canvasR = null;

	/** �������[�N�v���[�X��\�����邽�߂̃^�u */
	private JTabbedPane wpTab = null;

	/** ���[�N�v���[�X��z�u����p�l�� */
	private JPanel[] canvasW_BasePanel = null;

	/** ���[�N�v���[�X�̃{�^����z�u����p�l�� */
	private JPanel[] canvasW_BtnPanel = null;

	/** ���[�N�v���[�X���������ς݂��ۂ��̃t���O */
	private boolean[] WpInitialized = new boolean[5];

	/** �v���W�F�N�g�t�@�C�� */
	private String ProjectFileName = "";

	/** �v���W�F�N�g�@*/
	private Project project = null;

	/** �e�t���[�� */
	private IdeaMainFrame parentframe = null;

	/** ���|�W�g���œǂݍ��񂾃G�[�W�F���g�t�@�C�� */
	private Vector vecRepLoadAgent = new Vector();

	/** ���[�N�v���[�X�œǂݍ��񂾃G�[�W�F���g�t�@�C�� */
	private Vector[] vecWpLoadAgent = new Vector[5];

	private Vector[] vecWpLoadAgent2 = new Vector[5];

	/** ���g�p */
	private Vector vecWpReadCount = new Vector();

	private TitledBorder[] titleBdr1 = null;

	private TitledBorder titleBdr2 = null;

	//----------------------------------------------------------------------------
	// ���j���[
	//----------------------------------------------------------------------------
	/** ���j���[ */
	public JMenuBar menubar = null;

	/** �t�@�C�����j���[*/
	private JMenu filemenu = null;

	/** �t�@�C�����j���[�̃��j���[�A�C�e�� */
	// �V�K
	private JMenuItem newFileMenuItem = null;

	// �v���W�F�N�g���J��
	private JMenuItem filemenu1 = null;

	// �v���W�F�N�g�̍폜
	private JMenuItem deletePrjMenuItem = null;

	// �V�K�v���W�F�N�g
	private JMenuItem newProjectMenuItem = null;

	// �I��
	private JMenuItem filemenu2 = null;

	// �J������
	private JMenu openagainprjmenu = null;

	// �v���W�F�N�g�����̏���
	private JMenuItem subMenuItem = null;

	/** �c�[�����j���[ **/
	private JMenu toolMenu_DevScreen = null;

	/** �c�[�����j���[�̃��j���[�A�C�e�� */
	// �G�f�B�^�ݒ�
	private JMenuItem toolMenu1 = null;

	// �L�[���[�h�ݒ�
	private JMenuItem toolMenu2 = null;

	// �O���c�[��
	private JMenuItem toolMenu3 = null;

	// IDE�I�v�V����
	private JMenuItem toolMenu4 = null;

	// �V�~�����[�^�I�v�V����
	private JMenuItem toolMenu5 = null;

	// �O���c�[��
	private static JMenu outsideTool = null;

	private JPopupMenu openagainprjPopupMenu = new JPopupMenu();

	//add uchiya
	//���b�Z�[�W���j���[
	private JMenu messageMenu_DevScreen = null;

	private JMenuItem send_acl_menu = null;

	private JMenu set_acl_menu = null;

	//add mabune
	private JMenuItem set_acl_history = null; // acl�G�f�B�^�̗���

	/** �c�[���o�[ */
	public JToolBar toolbar;

	/** �c�[���o�[�Ŏg�p����{�^�� */
	// �v���W�F�N�g�J�� */
	private JButton openprjBtn = null;

	// �v���W�F�N�g���J���Ȃ���
	private JToggleButton openagainprjBtn;

	/** ����V�~�����[�g�̃c�[���{�^�� */
	private JButton btn1 = null; //Ag��S�ă��|�W�g���ɓǂݍ���

	private JButton btn2 = null; //�I�����ꂽAg�����|�W�g���ɓǂݍ���

	private JButton btn3 = null; //Ag��S�ă��[�N�v���[�X�ɓǂݍ���

	private JButton btn4 = null; //�I�����ꂽAg�����[�N�v���[�X�ɓǂݍ���

	private JButton btn5 = null; //���|�W�g����Ag������

	private JButton btn6 = null; //���[�N�v���[�X��Ag������

	private JButton btn7 = null; //���|�W�g����Ag���ēǍ�

	private JButton btn8 = null; //���[�N�v���[�X��Ag���ēǍ�

	private JButton btn9 = null; //activity

	private JButton btn10 = null; //cstmiz

	// �c�[���o�[�́u�J���Ȃ����v����\������|�b�v�A�b�v���j���[
	private JMenuItem popMenuItem = null;

	// ���[�N�v���[�X�̐�
	private int wp_cnt = 1;

	// ���[�N�v���[�X�̍ő吔
	public int wp_max_cnt = 5;

	private String DefaultDir = "";

	private String LoadDir = "";

	/****************************************************************************
	 * �R���X�g���N�^
	 * @param frame IdeaMainFrame���󂯎��
	 * @return �Ȃ�
	 ****************************************************************************/
	public Simulator(IdeaMainFrame parentframe) {
		try {
			this.parentframe = parentframe;

			//treeScrollPane = tree;
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/****************************************************************************
	 * ��������
	 * @param frame IdeaMainFrame���󂯎��
	 * @return �Ȃ�
	 ****************************************************************************/
	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);

		String wpCntStr = System.getProperty("dash.wp.cnt");
		//System.out.println("wpcntstr= "+wpCntStr);
		wp_cnt = new Integer(wpCntStr).intValue();
		//----------------------
		// ���j���[���쐬
		//----------------------
		createMenu();

		//----------------------
		// �c�[���o�[���쐬
		//----------------------
		createToolBar1();
		JToolBar tbar = createToolBar2();
		this.add(tbar, BorderLayout.NORTH);

		//----------------------
		// �C���X�y�N�^��\������DesktopPane���쐬����
		//----------------------
		inspectorDesktop = new JDesktopPane();
		inspectorDesktop.setBackground(Color.gray);

		//----------------------
		// ���|�W�g�����쐬
		//----------------------
		repPanel = new RepositoryPanel();
		// ���|�W�g�����g�p����DVM�Ƀ��|�W�g���̑��삷�郁�C���t���[����ݒ�
		repPanel.rep.getNewIf().getDVM().setParentFrame(parentframe);
		// �C���X�y�N�^��\������DesktopPane��������
		repPanel.setInspectorDesktopPane(inspectorDesktop);

		//----------------------
		// ���[�N�v���[�X�쐬
		//----------------------
		/*
		 wpPanel = new WorkplacePanel[wp_cnt];
		 for (int i=0; i<wp_cnt; i++ ) {
		 wpPanel[i] = new WorkplacePanel();
		 // ���[�N�v���[�X���g�p����DVM�Ƀ��[�N�v���[�X�̑��삷�郁�C���t���[����ݒ�
		 wpPanel[i].wp.getNewIf().getDVM().setParentFrame(parentframe);
		 
		 wpPanel[i].wp.getNewIf().getDVM().setWpIndex(i);
		 
		 // �C���X�y�N�^��\������DesktopPane��������
		 wpPanel[i].setInspectorDesktopPane(inspectorDesktop);
		 }
		 */
		wpPanel = new WorkplacePanel[wp_max_cnt];
		for (int i = 0; i < wp_max_cnt; i++) {
			wpPanel[i] = new WorkplacePanel();
			// ���[�N�v���[�X���g�p����DVM�Ƀ��[�N�v���[�X�̑��삷�郁�C���t���[����ݒ�
			wpPanel[i].wp.getNewIf().getDVM().setParentFrame(parentframe);

			wpPanel[i].wp.getNewIf().getDVM().setWpIndex(i);

			// �C���X�y�N�^��\������DesktopPane��������
			wpPanel[i].setInspectorDesktopPane(inspectorDesktop);
		}

		//--------------------------------------------------------------------------
		// �r���[�A�쐬
		//--------------------------------------------------------------------------
		// ���[�N�v���[�X
		//--------------------------------------------------------------------------
		canvasW = new ViewerCanvasW2[wp_max_cnt];
		canvasW_BasePanel = new JPanel[wp_max_cnt];
		canvasW_BtnPanel = new JPanel[wp_max_cnt];
		titleBdr1 = new TitledBorder[wp_max_cnt];

		for (int i = 0; i < wp_max_cnt; i++) {
			vecWpLoadAgent[i] = new Vector();
			vecWpLoadAgent2[i] = new Vector();
			canvasW[i] = new ViewerCanvasW2(wpPanel[i].getDvmName());

			canvasW_BtnPanel[i] = new JPanel();
			//���W�I�{�^�����g���ꍇ
			//canvasW_BtnPanel[i].setLayout(new GridLayout(1, 2));
			canvasW_BtnPanel[i].setLayout(new GridLayout(1, 2));
			//canvasW_BtnPanel[i].setLayout(new GridLayout(3,1));

			// Swing�̃��W�I�{�^���̃O���[�v��
			ButtonGroup canvasW_BtnGroup = new ButtonGroup();
			JRadioButton canvasW_cb1 = new JRadioButton("non-stop", true);
			JRadioButton canvasW_cb2 = new JRadioButton("step", false);
			canvasW_cb1.setActionCommand("wp" + new Integer(i).toString()
					+ "_non-stop");
			canvasW_cb2.setActionCommand("wp" + new Integer(i).toString()
					+ "_step");
			canvasW_cb1.addActionListener(this);
			canvasW_cb2.addActionListener(this);
			// ���W�I�{�^���̃O���[�v��
			canvasW_BtnGroup.add(canvasW_cb1);
			canvasW_BtnGroup.add(canvasW_cb2);

			JCheckBox canvasW_chkbox1 = new JCheckBox("step");
			canvasW_chkbox1.addActionListener(this);
			canvasW_chkbox1.setActionCommand("wp" + new Integer(i).toString()
					+ "_step-check");
			// ���W�I�{�^�����p�l���ɓ����
			//canvasW_BtnPanel[i].add(canvasW_cb1);
			//canvasW_BtnPanel[i].add(canvasW_cb2);
			canvasW_BtnPanel[i].add(canvasW_chkbox1);

			JLabel stepLabelW = new JLabel("", Label.RIGHT);
			//stepLabelW.setVisible(false);
			canvasW_BtnPanel[i].add(stepLabelW);
			canvasW[i].stepLabel = stepLabelW; // ViewerCanvas�ɋ�����

			canvasW_BasePanel[i] = new JPanel();

			JLabel labal5 = new JLabel();
			JLabel labal6 = new JLabel();
			JLabel labal7 = new JLabel();
			JLabel labal8 = new JLabel();

			ImageIcon iconWidth = getImageIcon("resources/width.gif");
			ImageIcon iconHeight = getImageIcon("resources/height.gif");
			labal5.setIcon(iconWidth);
			labal6.setIcon(iconHeight);
			labal7.setIcon(iconWidth);
			labal8.setIcon(iconHeight);

			JPanel p2 = new JPanel(new BorderLayout());
			p2.add(canvasW[i], BorderLayout.CENTER);
			p2.add(labal5, BorderLayout.NORTH);
			p2.add(labal6, BorderLayout.EAST);
			p2.add(labal7, BorderLayout.SOUTH);
			p2.add(labal8, BorderLayout.WEST);
			JScrollPane canvasW_spane0 = new JScrollPane(p2);

			canvasW_BasePanel[i].setLayout(new BorderLayout());
			canvasW_BasePanel[i].add("Center", canvasW_spane0);
			//canvasW_BasePanel[i].add("Center", p2);
			canvasW_BasePanel[i].add("South", canvasW_BtnPanel[i]);

			titleBdr1[i] = new TitledBorder(parentframe.getBilingualMsg("0050"));
			canvasW_BasePanel[i].setBorder(titleBdr1[i]);
		}

		//--------------------------------------------------------------------------
		// �r���[�A�쐬
		//--------------------------------------------------------------------------
		// ���|�W�g��
		//--------------------------------------------------------------------------
		canvasR = new ViewerCanvasR2(repPanel.getDvmName());
		//canvasR.setOpaque(false);
		//canvasR.setVisible(false);

		JPanel canvasR_BtnPanel = new JPanel();
		// ���W�I�{�^�����g���ꍇcanvasR_BtnPanel.setLayout(new GridLayout(0, 3));
		canvasR_BtnPanel.setLayout(new GridLayout(1, 2));

		// Swing�̃��W�I�{�^���̃O���[�v��
		ButtonGroup canvasR_BtnGroup = new ButtonGroup();
		JRadioButton canvasR_cb1 = new JRadioButton("non-stop", true);
		JRadioButton canvasR_cb2 = new JRadioButton("step", false);
		canvasR_cb1.setActionCommand("rep_non-stop");
		canvasR_cb2.setActionCommand("rep_step");
		canvasR_cb1.addActionListener(this);
		canvasR_cb2.addActionListener(this);
		// ���W�I�{�^���̃O���[�v��
		canvasR_BtnGroup.add(canvasR_cb1);
		canvasR_BtnGroup.add(canvasR_cb2);
		// ���W�I�{�^�����p�l���ɓ����
		//canvasR_BtnPanel.add(canvasR_cb1);
		//canvasR_BtnPanel.add(canvasR_cb2);

		JCheckBox canvasR_chkbox1 = new JCheckBox("step");
		canvasR_chkbox1.addActionListener(this);
		canvasR_chkbox1.setActionCommand("rep_step-check");
		// ���W�I�{�^�����p�l���ɓ����
		//canvasW_BtnPanel[i].add(canvasW_cb1);
		//canvasW_BtnPanel[i].add(canvasW_cb2);
		canvasR_BtnPanel.add(canvasR_chkbox1);

		JLabel stepLabelR = new JLabel("", Label.RIGHT);
		//stepLabelR.setVisible(false);
		canvasR_BtnPanel.add(stepLabelR);
		canvasR.stepLabel = stepLabelR; // ViewerCanvas�ɋ�����

		JPanel canvasR_BasePanel = new JPanel();
		canvasR_BasePanel.setLayout(new BorderLayout());

		JLabel labal1 = new JLabel();
		JLabel labal2 = new JLabel();
		JLabel labal3 = new JLabel();
		JLabel labal4 = new JLabel();

		ImageIcon iconWidth = getImageIcon("resources/width.gif");
		ImageIcon iconHeight = getImageIcon("resources/height.gif");
		labal1.setIcon(iconWidth);
		labal2.setIcon(iconHeight);
		labal3.setIcon(iconWidth);
		labal4.setIcon(iconHeight);

		JPanel p = new JPanel(new BorderLayout());
		p.add(canvasR, BorderLayout.CENTER);
		p.add(labal1, BorderLayout.NORTH);
		p.add(labal2, BorderLayout.EAST);
		p.add(labal3, BorderLayout.SOUTH);
		p.add(labal4, BorderLayout.WEST);
		JScrollPane canvasR_spane0 = new JScrollPane(p);

		canvasR_BasePanel.add("Center", canvasR_spane0);
		canvasR_BasePanel.add("South", canvasR_BtnPanel);

		titleBdr2 = new TitledBorder(parentframe.getBilingualMsg("0047"));
		canvasR_BasePanel.setBorder(titleBdr2);

		//----------------------
		// �X�v���b�g�y�C�����쐬
		//----------------------
		boolean RepWpChildWinMode = true;
		if (System.getProperty("dash.r_w.pos") == null) {
			RepWpChildWinMode = false;
		} else {
			if (System.getProperty("dash.r_w.pos").equals("fixed")) {
				RepWpChildWinMode = false;
			} else {
				RepWpChildWinMode = true;
			}
		}
		if (!RepWpChildWinMode) {
			JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					true, canvasR_BasePanel, repPanel);
			JSplitPane splitPane3 = null;
			if (wp_cnt > 0) {
				wpTab = new JTabbedPane(JTabbedPane.TOP);
				for (int i = 0; i < wp_cnt; i++) {
					JSplitPane splitPane2 = new JSplitPane(
							JSplitPane.VERTICAL_SPLIT, true,
							canvasW_BasePanel[i], wpPanel[i]);
					// for debug
					//JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,canvasW_BasePanel[i],new JPanel());
					splitPane2.setDividerLocation(333);
					splitPane2.setDividerSize(5);
					wpTab.add("WP" + new Integer(i + 1).toString(), splitPane2);
					wpPanel[i].wp.getNewIf().getDVM().setWpTab(wpTab);
				}
				//splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,splitPane1,wpTab);
				splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
						wpTab, inspectorDesktop);
			} else {
				JSplitPane splitPane2 = new JSplitPane(
						JSplitPane.VERTICAL_SPLIT, true, canvasW_BasePanel[0],
						wpPanel[0]);
				splitPane2.setDividerLocation(360);
				splitPane2.setDividerSize(5);
				splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
						splitPane1, splitPane2);
			}

			splitPane3.setOneTouchExpandable(true);
			//JSplitPane splitPane4 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,splitPane3,inspectorDesktop);
			JSplitPane splitPane4 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					true, splitPane1, splitPane3);
			splitPane4.setOneTouchExpandable(true);
			//viewer on/off
			if (System.getProperty("dash.viewer") != null
					&& System.getProperty("dash.viewer").equals("on")) {
				repPanel.setViewerCanvas(canvasR);
			}
			if (System.getProperty("dash.viewer") != null
					&& System.getProperty("dash.viewer").equals("on")) {
				for (int i = 0; i < wp_max_cnt; i++) {
					wpPanel[i].setViewerCanvas(canvasW[i]);
				}
			}
			splitPane1.setDividerLocation(360);
			//splitPane2.setDividerLocation(360);
			splitPane3.setDividerLocation(250);
			splitPane4.setDividerLocation(250);

			splitPane1.setDividerSize(5);
			//splitPane2.setDividerSize(5);
			//splitPane3.setDividerSize(5);
			//splitPane4.setDividerSize(5);

			//        this.add(splitPane,BorderLayout.SOUTH);
			this.add(splitPane4);
		} else {
			// ���|�W�g����ChildWindow�쐬
			JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					true, canvasR_BasePanel, repPanel);
			//uchiya
			//viewer on/off
			//if(System.getProperty("dash.viewer")!=null&&System.getProperty("dash.viewer").equals("on")){
			repPanel.setViewerCanvas(canvasR);
			//}
			splitPane1.setDividerLocation(360);
			JInternalFrame iframe_rep = new JInternalFrame(parentframe
					.getBilingualMsg("0047"));
			Container con = iframe_rep.getContentPane(); //�t���[���̕\���̈���Ƃ��Ă���
			con.add(splitPane1);
			iframe_rep.setResizable(true);
			iframe_rep.setMaximizable(true);
			iframe_rep.setClosable(true);
			iframe_rep.setIconifiable(true);
			Dimension desktopSize = inspectorDesktop.getSize();
			//iframe_rep.setSize (inspectorDesktop.getWidth(), inspectorDesktop.getHeight());
			iframe_rep.setSize(250, 620);
			iframe_rep.setLocation(0, 0);
			inspectorDesktop.add(iframe_rep);
			iframe_rep.setVisible(true);
			inspectorDesktop.setSelectedFrame(iframe_rep);
			iframe_rep.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			iframe_rep.setToolTipText(" ");
			/*
			 iframe_rep.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
			 public void internalFrameClosing(InternalFrameEvent e) {
			 this_internalFrameClosing(e);
			 }
			 public void internalFrameDeiconified(InternalFrameEvent e) {
			 this_internalFrameDeiconified(e);
			 }
			 public void internalFrameActivated(InternalFrameEvent e) {
			 this_internalFrameActivated(e);
			 }
			 });
			 */

			// ���[�N�v���[�X��ChildWindow�쐬
			JSplitPane splitPane3 = null;
			if (wp_cnt > 0) {
				wpTab = new JTabbedPane(JTabbedPane.TOP);
				for (int i = 0; i < wp_cnt; i++) {
					JSplitPane splitPane2 = new JSplitPane(
							JSplitPane.VERTICAL_SPLIT, true,
							canvasW_BasePanel[i], wpPanel[i]);
					// for debug
					//JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,canvasW_BasePanel[i],new JPanel());
					splitPane2.setDividerLocation(333);
					splitPane2.setDividerSize(5);
					wpTab.add("WP" + new Integer(i + 1).toString(), splitPane2);
					wpPanel[i].wp.getNewIf().getDVM().setWpTab(wpTab);
				}
				//splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,splitPane1,wpTab);
				//splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,wpTab,inspectorDesktop);
			} else {
				JSplitPane splitPane2 = new JSplitPane(
						JSplitPane.VERTICAL_SPLIT, true, canvasW_BasePanel[0],
						wpPanel[0]);
				splitPane2.setDividerLocation(360);
				splitPane2.setDividerSize(5);
				splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
						splitPane1, splitPane2);
			}

			JInternalFrame iframe_wp = new JInternalFrame(parentframe
					.getBilingualMsg("0050"));
			con = iframe_wp.getContentPane(); //�t���[���̕\���̈���Ƃ��Ă���
			con.add(wpTab);
			iframe_wp.setResizable(true);
			iframe_wp.setMaximizable(true);
			iframe_wp.setClosable(false);
			iframe_wp.setIconifiable(true);
			//iframe_wp.setSize (inspectorDesktop.getWidth(), inspectorDesktop.getHeight());
			iframe_wp.setSize(250, 620);
			iframe_wp.setLocation(250, 0);
			inspectorDesktop.add(iframe_wp);
			iframe_wp.setVisible(true);
			inspectorDesktop.setSelectedFrame(iframe_wp);
			iframe_wp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			iframe_wp.setToolTipText(" ");

			if (System.getProperty("dash.viewer") != null
					&& System.getProperty("dash.viewer").equals("on")) {
				for (int i = 0; i < wp_max_cnt; i++) {
					wpPanel[i].setViewerCanvas(canvasW[i]);
				}
			}

			/*
			 iframe_rep.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
			 public void internalFrameClosing(InternalFrameEvent e) {
			 this_internalFrameClosing(e);
			 }
			 public void internalFrameDeiconified(InternalFrameEvent e) {
			 this_internalFrameDeiconified(e);
			 }
			 public void internalFrameActivated(InternalFrameEvent e) {
			 this_internalFrameActivated(e);
			 }
			 });
			 */
			this.add(inspectorDesktop);
		}

		//add uchiya
		//--------------------------------------------------------------------------
		// ���b�Z�[�W���j���[
		//--------------------------------------------------------------------------
		messageMenu_DevScreen = new JMenu(parentframe.getBilingualMsg("0222")
				+ "(M)");
		messageMenu_DevScreen.setMnemonic('M');
		//�ǉ��@�\�P�iidea-1.2.2�j
		//���b�Z�[�W���t�@�C�����瑗�M����@�\
		//��dash��directedawardfile�ǂݍ��݋@�\���g��
		//�t�@�C�����烁�b�Z�[�W�Q��ǂݍ��݁AACL�G�f�B�^�ɃZ�b�g���đ��M����B
		send_acl_menu = menuItem(parentframe.getBilingualMsg("0223"),
				"OpenACLFile", null);

		messageMenu_DevScreen.add(send_acl_menu);

		
		
		
		
		//ACL�G�f�B�^�փ��b�Z�[�W���Z�b�g����@�\
		//NewIf2����ڐA
		set_acl_menu = new JMenu(parentframe.getBilingualMsg("0224"));
		// ��݂���
		StringBuffer buffer = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(((NewIf2) wpPanel[0].wp
					.getNewIf()).msgfile));
			while (br.ready()) {
				String line = br.readLine();
				if (line != null)
					buffer.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			JMenuItem item = new JMenuItem("messages.txt not found");
			set_acl_menu.add(item).setEnabled(false);
		} catch (IOException e) {
			e.printStackTrace();
			JMenuItem item = new JMenuItem("IOException occured.");
			set_acl_menu.add(item).setEnabled(false);
		} finally {
			try {
				if (br != null) {
					br.close();
					br = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		replaceMeta(buffer);

		// �ρ[��
		ps.Parser parser = new ps.Parser("messages.txt", buffer.toString(),
				false);
		Vector lists = null;
		try {
			lists = parser.parseMessages();
		} catch (ps.SyntaxException e) {
			System.err.println(e);
		}

		if (lists == null)
			return;

		// ���j���[�쐬
		MessageAction msgAct = new MessageAction();
		for (Enumeration e = lists.elements(); e.hasMoreElements();) {
			String item = (String) e.nextElement();
			JMenu menuItem = new JMenu(item);
			Vector subItems = (Vector) e.nextElement();
			for (Enumeration ee = subItems.elements(); ee.hasMoreElements();) {
				Hashtable hash = (Hashtable) ee.nextElement();
				String subItem = (String) hash.get("submenu");

				JMenuItem subMenuItem2 = new JMenuItem(subItem);
				subMenuItem2.addActionListener(msgAct);
				menuItem.add(subMenuItem2);
				String actionCommand = item + "." + subItem;
				subMenuItem2.setActionCommand(actionCommand);
				msgAct.putHash(actionCommand, hash);
			}
			set_acl_menu.add(menuItem);
		}
		messageMenu_DevScreen.add(set_acl_menu);

		
		
		
		
		
		
		
		
		
		
		// add mabune
		set_acl_history = new JMenuItem(parentframe.getBilingualMsg("0229"));

		HistoryAction histAct = new HistoryAction();
		set_acl_history.addActionListener(histAct);

		messageMenu_DevScreen.add(set_acl_history);

		// add uchiya 060607
		JMenuItem analyze_message = new JMenuItem(parentframe.getBilingualMsg("0231"));
		
		AnalyzeAction aa = new AnalyzeAction();
		analyze_message.addActionListener(aa);
		
		messageMenu_DevScreen.add(analyze_message);
		
		menubar.add(messageMenu_DevScreen);
	}

	/** "<?Repository>"�Ȃǂ�u��������B*/
	private void replaceMeta(StringBuffer buffer) {
		try {
			replaceMeta(buffer, "\"<?Repository>\"", System
					.getProperty("dash.r.name")
					+ ":" + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		replaceMeta(buffer, "\"<?Here>\"", wpPanel[wpTab.getSelectedIndex()].wp
				.getNewIf().getDvmName());
	}

	private void replaceMeta(StringBuffer buffer, String oldstr, String newstr) {
		int length = oldstr.length();
		while (true) {
			int start = buffer.toString().indexOf(oldstr);
			if (start < 0)
				break;
			buffer.replace(start, start + length, newstr);
		}
	}

	//�v���W�F�N�g���J���ۂ̃f�t�H���g�̃p�X���
	public void setDefaultDir(String indir) {
		DefaultDir = indir;
	}

	/****************************************************************************
	 * ���j���[�o�[���쐬
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	private void createMenu() {
		menubar = new JMenuBar();

		filemenu = new JMenu(parentframe.getBilingualMsg("0001") + "(F)");
		filemenu.setMnemonic('F');

		newFileMenuItem = menuItem(parentframe.getBilingualMsg("0002")
				+ "(N)...", "NewFile", getImageIcon("resources/newfile.gif"));
		newFileMenuItem.setMnemonic('N');
		//parentframe.setMenuItemAccelerator ("new-file", newFileMenuItem );
		filemenu.add(newFileMenuItem);

		newProjectMenuItem = menuItem(parentframe.getBilingualMsg("0003")
				+ "(P)...", "NewProject", getImageIcon("resources/newprj.gif"));
		newProjectMenuItem.setMnemonic('P');
		filemenu.add(newProjectMenuItem);

		filemenu1 = menuItem(parentframe.getBilingualMsg("0004") + "(O)...",
				"OpenProject", getImageIcon("resources/openprj.gif"));
		filemenu2 = menuItem(parentframe.getBilingualMsg("0008") + "(X)",
				"Exit", null);
		filemenu1.setMnemonic('O');
		filemenu2.setMnemonic('X');

		//parentframe.setMenuItemAccelerator("open-project",filemenu1);
		//filemenu1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		filemenu.add(filemenu1);

		openagainprjmenu = new JMenu(parentframe.getBilingualMsg("0005")
				+ "(R)");
		openagainprjmenu.setMnemonic('R');
		openagainprjmenu.setIcon(getImageIcon("resources/openagainprj.gif"));
		openagainprjmenu.setEnabled(false);
		filemenu.add(openagainprjmenu);

		deletePrjMenuItem = menuItem(parentframe.getBilingualMsg("0116"),
				"DeleteProject", getImageIcon("resources/delfile.gif"));
		deletePrjMenuItem.setEnabled(false);
		filemenu.add(deletePrjMenuItem);

		filemenu.addSeparator();
		filemenu.add(filemenu2);
		menubar.add(filemenu);

		//--------------------------------------------------------------------------
		// �c�[�����j���[
		//--------------------------------------------------------------------------
		toolMenu_DevScreen = new JMenu(parentframe.getBilingualMsg("0021")
				+ "(T)");
		toolMenu_DevScreen.setMnemonic('T');

		// ���j���[�A�C�e���쐬
		toolMenu1 = menuItem(parentframe.getBilingualMsg("0022") + "(E)...",
				"SetupEditor", null);
		toolMenu2 = menuItem(parentframe.getBilingualMsg("0023") + "(K)...",
				"SetupKeyword", null);
		toolMenu4 = menuItem(parentframe.getBilingualMsg("0117") + "...",
				"SetupKeymap", null);
		toolMenu5 = menuItem(parentframe.getBilingualMsg("0118") + "...",
				"SimulatorOption", null);

		outsideTool = new JMenu(parentframe.getBilingualMsg("0024") + "(O)");
		outsideTool.setMnemonic('O');
		//outsideTool2 = new JMenu(parentframe.getBilingualMsg("0024") + "(O)");
		//outsideTool2.setMnemonic('O');
		//outsideTool2.setIcon(getImageIcon("resources/permeation.gif"));

		// �j�[���j�b�N�ݒ�
		toolMenu1.setMnemonic('E');
		toolMenu2.setMnemonic('K');

		toolMenu_DevScreen.add(toolMenu4);
		toolMenu_DevScreen.add(toolMenu1);
		toolMenu_DevScreen.add(toolMenu2);
		toolMenu_DevScreen.add(toolMenu5);
		toolMenu_DevScreen.addSeparator();
		toolMenu_DevScreen.add(outsideTool);

		toolMenu3 = menuItem(parentframe.getBilingualMsg("0025") + "(C)...",
				"SetOutsideTool", null);
		toolMenu3.setMnemonic('C');
		outsideTool.add(toolMenu3);

		//parentframe.readOutsideToolInfo ();

		if (parentframe.vecOutsideToolInfo.size() > 0) {
			outsideTool.addSeparator();
		}

		for (int i = 0; i < parentframe.vecOutsideToolInfo.size(); i++) {
			String wk = (String) parentframe.vecOutsideToolInfo.elementAt(i);

			StringTokenizer st = new StringTokenizer(wk, ",");
			int cnt = 0;
			while (st.hasMoreTokens()) {
				String data = st.nextToken();

				JMenuItem toolMenu6 = menuItem(new Integer(i + 1).toString()
						+ ":" + data, "OutsideTool_"
						+ new Integer(i).toString(), null);
				outsideTool.add(toolMenu6);
				break;
			}
		}
		menubar.add(toolMenu_DevScreen);
	}

	public void changeMenuItemAccelerator() {
		parentframe.setMenuItemAccelerator("new-file", newFileMenuItem);
		parentframe.setMenuItemAccelerator("open-project", filemenu1);
	}

	/****************************************************************************
	 * �c�[���o�[���쐬<br>
	 * �v���W�F�N�g���J���E�J���Ȃ����@�\�����c�[���o�[
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	private void createToolBar1() {
		toolbar = new JToolBar();
		openprjBtn = new JButton(null, getImageIcon("resources/openprj.gif"));
		openagainprjBtn = new JToggleButton(null,
				getImageIcon("resources/openagainprj2.gif"));
		openagainprjBtn.setEnabled(false);

		openprjBtn.setActionCommand("OpenProject");
		openprjBtn.addActionListener(this);

		openprjBtn.setToolTipText(parentframe.getBilingualMsg("0004"));
		openagainprjBtn.setToolTipText(parentframe.getBilingualMsg("0005"));

		toolbar.add(openprjBtn);
		toolbar.add(openagainprjBtn);
		toolbar.setFloatable(false);
		openagainprjBtn
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						openagainprjBtn_stateChanged(e);
					}
				});

		openagainprjPopupMenu
				.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
					public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					}

					public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
						openagainprjPopupMenu_popupMenuWillBecomeInvisible(e);
					}

					public void popupMenuCanceled(PopupMenuEvent e) {
					}
				});
	}

	/****************************************************************************
	 * �c�[���o�[���쐬<br>
	 * ����V�~�����[�V�����Ŏg�p����c�[���o�[
	 * @param �Ȃ�
	 * @return �쐬���ꂽ�c�[���o�[
	 ****************************************************************************/
	private JToolBar createToolBar2() {
		JToolBar tbar = new JToolBar();
		tbar.setFloatable(false);

		// �{�^���p�C���[�W�A�C�R�����쐬����
		ImageIcon icon1 = getImageIcon("resources/rgall.gif");
		ImageIcon icon2 = getImageIcon("resources/rgsel.gif");
		ImageIcon icon3 = getImageIcon("resources/wpall.gif");
		ImageIcon icon4 = getImageIcon("resources/wpsel.gif");
		ImageIcon icon5 = getImageIcon("resources/rgdel.gif");
		ImageIcon icon6 = getImageIcon("resources/wpdel.gif");
		ImageIcon icon7 = getImageIcon("resources/rgre.gif");
		ImageIcon icon8 = getImageIcon("resources/wpre.gif");
		ImageIcon icon9 = getImageIcon("resources/activity.gif");
		ImageIcon icon10 = getImageIcon("resources/cstmiz.gif");

		// �{�^���쐬
		btn1 = new JButton(icon1);
		btn2 = new JButton(icon2);
		btn3 = new JButton(icon3);
		btn4 = new JButton(icon4);
		btn5 = new JButton(icon5);
		btn6 = new JButton(icon6);
		btn7 = new JButton(icon7);
		btn8 = new JButton(icon8);
		btn9 = new JButton(icon9);
		btn10 = new JButton(icon10);

		btn9.setEnabled(false);
		//btn10.setEnabled(false);

		btn1.addActionListener(this);
		btn2.addActionListener(this);
		btn3.addActionListener(this);
		btn4.addActionListener(this);
		btn5.addActionListener(this);
		btn6.addActionListener(this);
		btn7.addActionListener(this);
		btn8.addActionListener(this);
		btn9.addActionListener(this);
		btn10.addActionListener(this);

		btn1.setActionCommand("rep_all_load_exec");
		btn2.setActionCommand("rep_sel_load_exec");
		btn3.setActionCommand("wp_all_load_exec");
		btn4.setActionCommand("wp_sel_load_exec");
		btn5.setActionCommand("rep_del");
		btn6.setActionCommand("wp_del");
		btn7.setActionCommand("rep_reload");
		btn8.setActionCommand("wp_reload");
		btn9.setActionCommand("activity");
		btn10.setActionCommand("cstmiz");

		/*
		 btn1.addActionListener(new java.awt.event.ActionListener() {
		 public void actionPerformed(ActionEvent e) {
		 btn1_actionPerformed(e);
		 }
		 });
		 */

		btn1.setToolTipText(parentframe.getBilingualMsg("0051"));
		btn2.setToolTipText(parentframe.getBilingualMsg("0052"));
		btn3.setToolTipText(parentframe.getBilingualMsg("0053"));
		btn4.setToolTipText(parentframe.getBilingualMsg("0054"));
		btn5.setToolTipText(parentframe.getBilingualMsg("0055"));
		btn6.setToolTipText(parentframe.getBilingualMsg("0056"));
		btn7.setToolTipText(parentframe.getBilingualMsg("0057"));
		btn8.setToolTipText(parentframe.getBilingualMsg("0058"));
		btn9.setToolTipText(parentframe.getBilingualMsg("0059"));
		btn10.setToolTipText(parentframe.getBilingualMsg("0060"));

		// �c�[���o�[�Ƀ{�^����ǉ�
		tbar.add(btn1);
		tbar.add(btn2);
		tbar.add(btn5);
		tbar.add(btn7);

		tbar.addSeparator();

		tbar.add(btn3);
		tbar.add(btn4);
		tbar.add(btn6);
		tbar.add(btn8);
		//tbar.add(btn9);
		tbar.add(btn10);
		return tbar;
	}

	/****************************************************************************
	 * �C���[�W�A�C�R�����擾
	 * @param path �C���[�W�t�@�C���ւ̃p�X
	 * @return �C���[�W�A�C�R��
	 ****************************************************************************/
	private ImageIcon getImageIcon(String path) {
		java.net.URL url = this.getClass().getResource(path);
		return new ImageIcon(url);
	}

	/****************************************************************************
	 * �r���[�A�̏�����
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	public void initialize() {
		for (int i = 0; i < wp_max_cnt; i++) {
			WpInitialized[i] = false;
		}
		for (int i = 0; i < wp_cnt; i++) {
			canvasW[i].initialize();
			WpInitialized[i] = true;
		}
		canvasR.initialize();
	}

	/****************************************************************************
	 * �C�x���g����
	 * @param e �A�N�V�����C�x���g
	 * @return �Ȃ�
	 ****************************************************************************/
	public void actionPerformed(ActionEvent e) /*throws java.io.IOException, java.lang.Exception*/{

		try {

			String action = e.getActionCommand();
			//System.out.println("action is "+action);
			//add uchiya
			if (action.equals("OpenACLFile")) {
				DashDefaults dd = new DashDefaults();
				JFileChooser c1 = new JFileChooser(dd.getDashdir());
				int ret = c1.showOpenDialog(this);
				File file = c1.getSelectedFile();
				if (ret != JFileChooser.APPROVE_OPTION || file == null)
					return;

				StringBuffer buffer = new StringBuffer();
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					while (br.ready()) {
						String line = br.readLine();
						if (line != null)
							buffer.append(line + "\n");
					}
				} catch (IOException ee) {
					ee.printStackTrace();
				}
				buffer.insert(0, "(ACL ");
				buffer.append(")");

				ps.Parser parser = new ps.Parser(file.toString(), buffer
						.toString(), false);
				Vector lists = null;
				try {
					lists = parser.parseMessagesForSendDA();
				} catch (ps.SyntaxException eee) {
					System.err.println(eee);
				}

				if (lists == null)
					return;
				for (int i = 0; i < lists.size(); i++) {
					Hashtable msgtable = (Hashtable) lists.elementAt(i);
					String performative = (String) msgtable.get("performative");
					String to = (String) msgtable.get("to");
					String arrival = (String) msgtable.get("arrival");
					String content = (String) msgtable.get("content");

					if (arrival != null)
						to = to + "@" + arrival;
					((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
							.setPerfField(performative);
					((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
							.setToField(to);
					((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
							.setContArea(content);
					((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf())
							.sendMessageFromACLeditor();
				}
			}

			// �����g�p
			if (action.endsWith("non-stop") || action.endsWith("step")) {
				if (action.indexOf("wp") != -1) {
					String s = action.substring(action.indexOf("_") + 1);
					s = action.substring(2, action.indexOf("_"));

					int wpIndex = wpTab.getSelectedIndex();

					canvasW[wpIndex].changeItem(action.substring(action
							.indexOf("_") + 1));
				} else {
					canvasR.changeItem(action
							.substring(action.indexOf("_") + 1));
				}
			}
			// �����g�p���������܂�

			//-----------------------------------------------------------------------
			// �r���[�A�́uStep�v�`�F�b�N�{�b�N�X���N���b�N���ꂽ�Ƃ�
			//-----------------------------------------------------------------------
			if (action.endsWith("step-check") || action.endsWith("step")) {
				JCheckBox cb = (JCheckBox) e.getSource();
				String changeitem = "";
				if (cb.isSelected()) {
					changeitem = "step";
				} else {
					changeitem = "non-stop";
				}
				if (action.indexOf("wp") != -1) {
					String s = action.substring(action.indexOf("_") + 1);
					s = action.substring(2, action.indexOf("_"));

					int wpIndex = wpTab.getSelectedIndex();

					canvasW[wpIndex].changeItem(changeitem);
				} else {
					if (cb.isSelected()) {
						changeitem = "step";
					} else {
						changeitem = "non-stop";
					}
					canvasR.changeItem(changeitem);
				}
			}
			//-----------------------------------------------------------------------
			// ��߼���Ag�Ƃ��đS�Ă�Ag���N��
			//-----------------------------------------------------------------------
			else if (action.equals("rep_all_load_exec")) {
				//clear_Rep();
				//repExec ("ALL_AGENT", parentframe.getBilingualMsg("0051"));
				repExec("ALL_AGENT");

			}
			//-----------------------------------------------------------------------
			// ��߼���Ag�Ƃ��Ďw�肵��Ag���N��
			//-----------------------------------------------------------------------
			else if (action.equals("rep_sel_load_exec")) {
				//clear_Rep();
				//repExec ("SELECT_AGENT", parentframe.getBilingualMsg("0052"));
				repExec("SELECT_AGENT");
			}
			//-----------------------------------------------------------------------
			// ܰ���ڰ�Ag�Ƃ��đS�Ă�Ag���N��
			//-----------------------------------------------------------------------
			else if (action.equals("wp_all_load_exec")) {
				//clear_Wp();
				//wpExec ("ALL_AGENT", parentframe.getBilingualMsg("0053"));
				wpExec("ALL_AGENT");
			}
			//-----------------------------------------------------------------------
			// ܰ���ڰ�Ag�Ƃ��Ďw�肵��Ag���N��
			//-----------------------------------------------------------------------
			else if (action.equals("wp_sel_load_exec")) {
				//clear_Wp();
				//wpExec ("SELECT_AGENT", parentframe.getBilingualMsg("0054"));
				wpExec("SELECT_AGENT");
			}
			//-----------------------------------------------------------------------
			// ���z��߼��؂�Ag��S�ď���
			//-----------------------------------------------------------------------
			else if (action.equals("rep_del")) {
				clear_Rep();
			}
			//-----------------------------------------------------------------------
			// ���zܰ���ڰ���Ag��S�ď���
			//-----------------------------------------------------------------------
			else if (action.equals("wp_del")) {
				clear_Wp();
			}
			//-----------------------------------------------------------------------
			// ���z��߼��؂�Ag���۰��
			//-----------------------------------------------------------------------
			else if (action.equals("rep_reload")) {
				if (project == null)
					return;
				clear_Rep();

				Vector vecFilenameWithPath = (Vector) project
						.getFileNamesWithPath();
				Vector vecSelectFileWk = new Vector();
				for (int i = 0; i < vecRepLoadAgent.size(); i++) {
					String SelectFileName = (String) vecRepLoadAgent
							.elementAt(i);
					boolean flag = false;
					for (int j = 0; j < vecFilenameWithPath.size(); j++) {
						String FilenameWithPath = (String) vecFilenameWithPath
								.elementAt(j);
						if (FilenameWithPath.endsWith(File.separator
								+ SelectFileName)) {
							vecSelectFileWk.addElement(FilenameWithPath);
						}
					}
				}

				//repPanel.loadProjectAgents(project.getProjectPath(),vecRepLoadAgent, LoadDir );
				repPanel.loadProjectAgents(project.getProjectPath(),
						vecSelectFileWk, LoadDir);
			}
			//-----------------------------------------------------------------------
			// ���zܰ���ڰ���Ag���۰��
			//-----------------------------------------------------------------------
			else if (action.equals("wp_reload")) {
				if (project == null)
					return;
				int wpIndex = wpTab.getSelectedIndex();
				//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecWpLoadAgent, vecWpReadCount);
				Vector vecAgentName = wpPanel[wpIndex].wp.getNewIf()
						.getAllAgentName();
				for (int i = 0; i < vecAgentName.size(); i++) {
					String AgentName = (String) vecAgentName.elementAt(i);
					AgentName = AgentName.substring(0, AgentName.indexOf("."));
					AgentName = AgentName + ".dash";
					vecAgentName.setElementAt(AgentName, i);
				}

				clear_Wp();
				//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecWpLoadAgent[wpIndex], null,DefaultDir);

				Vector vecFilenameWithPath = (Vector) project
						.getFileNamesWithPath();
				Vector vecSelectFileWk = new Vector();
				for (int i = 0; i < vecAgentName.size(); i++) {
					String SelectFileName = (String) vecAgentName.elementAt(i);
					boolean flag = false;
					for (int j = 0; j < vecFilenameWithPath.size(); j++) {
						String FilenameWithPath = (String) vecFilenameWithPath
								.elementAt(j);
						if (FilenameWithPath.endsWith(File.separator
								+ SelectFileName)) {
							vecSelectFileWk.addElement(FilenameWithPath);
						}
					}
				}

				//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecAgentName, null,LoadDir);
				wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),
						vecSelectFileWk, null, LoadDir);
			}
			//-----------------------------------------------------------------------
			// ��è��è�}����
			//-----------------------------------------------------------------------
			else if (action.equals("activity")) {
				int wpIndex = wpTab.getSelectedIndex();
				if (wpPanel[wpIndex].wp.getNewIf().getDVM().comInt
						.getNameServer() == null) {
					JOptionPane.showMessageDialog(null, wpPanel[wpIndex].wp
							.getNewIf().getDVM().comInt.getNameServerErrMsg());
					return;
				}
				//String [][] selecter = { {"name","CnpManager"},{"name","DB-access"} };
				String[][] selecter = { { "name", "Sample010" } };
				Vector v = wpPanel[wpIndex].wp.getNewIf().getDVM().comInt
						.lookup(selecter);
				//wpPanel.wp.newif.getDVM().comInt.
				int size = v.size();
				System.out.println(size);
				for (Enumeration e1 = v.elements(); e1.hasMoreElements();) {
					String[] data = (String[]) e1.nextElement();
					for (int i = 0; i < data.length; i++) {
						System.out.println(data[i]);
					}
				}
			}
			//-----------------------------------------------------------------------
			// �V�~�����[�^�[�ݒ���J��
			//-----------------------------------------------------------------------
			else if (action.equals("cstmiz")
					|| action.equals("SimulatorOption")) {
				//	System.out.println("option");
				SimulatorOption dlg = new SimulatorOption(parentframe);
				//dlg.show();
				dlg.setVisible(true);
				if (dlg.getResult()) {
					// ����V�~�����[�^�Đݒ�
					getRepDVM().finalizeDVM();
					getWpDVM(0).finalizeDVM();
					getWpDVM(1).finalizeDVM();
					getWpDVM(2).finalizeDVM();
					getWpDVM(3).finalizeDVM();
					getWpDVM(4).finalizeDVM();

					getRepDVM().RmiModule_ReCreate();
					getWpDVM(0).RmiModule_ReCreate();
					getWpDVM(1).RmiModule_ReCreate();
					getWpDVM(2).RmiModule_ReCreate();
					getWpDVM(3).RmiModule_ReCreate();
					getWpDVM(4).RmiModule_ReCreate();

					//getDVM().RmiModule_ReCreate();
					String wpCntStr = System.getProperty("dash.wp.cnt");
					wp_cnt = new Integer(wpCntStr).intValue();
					int cnt = wpTab.getComponentCount();

					if (wp_cnt > cnt) {
						for (int i = cnt; i < wp_cnt; i++) {
							JSplitPane splitPane2 = new JSplitPane(
									JSplitPane.VERTICAL_SPLIT, true,
									canvasW_BasePanel[i], wpPanel[i]);
							splitPane2.setDividerLocation(360);
							splitPane2.setDividerSize(5);
							wpTab.add("WP" + new Integer(i + 1).toString(),
									splitPane2);
							wpPanel[i].wp.getNewIf().getDVM().setWpTab(wpTab);
						}
					} else if (wp_cnt < cnt) {
						for (int i = wp_cnt; i < cnt; i++) {
							int wpIndex = i;
							canvasW[wpIndex].removeAgentAll();
							wpPanel[wpIndex].removeAgentAll();
							wpPanel[wpIndex].clearLog();
							canvasW[wpIndex].repaint();
							vecWpLoadAgent[wpIndex].clear();
							vecWpLoadAgent2[wpIndex].clear();

							JInternalFrame[] iframes = inspectorDesktop
									.getAllFrames();
							for (int j = 0; j < iframes.length; j++) {

								if (iframes[j].getToolTipText().equals(
										"Workplace")) {
									inspectorDesktop.remove(iframes[j]);
								}
							}

							inspectorDesktop.repaint();

							wpTab.remove(wp_cnt);
						}
					}
					wpTab.repaint();
					for (int i = 0; i < wp_cnt; i++) {
						if (!WpInitialized[i]) {
							canvasW[i].initialize();
							WpInitialized[i] = true;
						}
					}
					//for (int i=0; i<wp_cnt; i++ ) {
					//  wpTab.getComponentCount()
					//}
				}

				//dlg.dispose();
				// �ݒ�̧�ق��J��
			}
			//-----------------------------------------------------------------------
			// �v���W�F�N�g���J��
			//-----------------------------------------------------------------------
			else if (action.equals("OpenProject")) {
				parentframe.openprojectfile();
			}
			//-----------------------------------------------------------------------
			// �v���W�F�N�g���J������
			//-----------------------------------------------------------------------
			else if (action.equals("againPrjct")) {
				if (e.getSource() instanceof JMenuItem) {
					JMenuItem menuitem = (JMenuItem) e.getSource();
					parentframe.readProjectFile(menuitem.getText());
				}

			}
			//-----------------------------------------------------------------------
			// �v���W�F�N�g�����폜
			//-----------------------------------------------------------------------
			else if (action.equals("deletehist")) {
				parentframe.deleteOpenHist();
			}
			//-----------------------------------------------------------------------
			// �I��
			//-----------------------------------------------------------------------
			else if (action.equals("Exit")) {
				parentframe.SystemExit();
			}
			//--------------------------------------------------------------------------
			// �G�f�B�^�ݒ�
			//--------------------------------------------------------------------------
			else if (action.equals("SetupEditor")) {
				parentframe.SetupEditor();
			}
			//--------------------------------------------------------------------------
			// �L�[���[�h�ݒ�
			//--------------------------------------------------------------------------
			else if (action.equals("SetupKeyword")) {
				parentframe.SetupKeyword();
			}
			//--------------------------------------------------------------------------
			// IDE�I�v�V����
			//--------------------------------------------------------------------------
			else if (action.equals("SetupKeymap")) {
				parentframe.IdeOption();
			}
			//--------------------------------------------------------------------------
			// �O���c�[���\���_�C�A���O�̕\��
			//--------------------------------------------------------------------------
			else if (action.equals("SetOutsideTool")) {
				parentframe.SetOutsideTool();
			}
			//--------------------------------------------------------------------------
			// �O���c�[���̎��s
			//--------------------------------------------------------------------------
			else if (action.startsWith("OutsideTool_")) {
				parentframe.ExecOutsideTool(action);
			}
			//--------------------------------------------------------------------------
			// �V�K��Dash�t�@�C���쐬
			//--------------------------------------------------------------------------
			else if (action.equals("NewFile")) {
				if (project == null) {
					Object[] options = { "OK" };
					JOptionPane.showOptionDialog(this, parentframe
							.getBilingualMsg("0068"), parentframe
							.getBilingualMsg("0129"),
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE, null, options,
							options[0]);

					return;
				}
				NewChoice newChoice = new NewChoice(parentframe, project);
				//newChoice.show();
				newChoice.setVisible(true);
				//NewFile newfile = new NewFile(this,project);
				//newfile.show();
			}
			//--------------------------------------------------------------------------
			// �V�K�v���W�F�N�g
			//--------------------------------------------------------------------------
			else if (action.equals("NewProject")) {
				NewProject NewPrjWin = new NewProject(parentframe, null);
				//NewPrjWin.show();
				NewPrjWin.setVisible(true);
			}
			//--------------------------------------------------------------------------
			// �v���W�F�N�g�̍폜
			//--------------------------------------------------------------------------
			else if (action.equals("DeleteProject")) {
				parentframe.deleteProject();
			}

		} catch (Exception e2) {

		}

	}

	// ���g�p
	void btn1_actionPerformed(ActionEvent e) {
		canvasR.removeAgentAll();
		repPanel.removeAgentAll();
		canvasW[0].removeAgentAll();
		wpPanel[0].removeAgentAll();

		canvasW[0].repaint();
		canvasR.repaint();
		//inspectorDesktop.getAllFrames();
		inspectorDesktop.removeAll();
		inspectorDesktop.repaint();

		//repPanel.loadProjectAgents("C:\\dash-1.9.7h\\scripts\\cnp\\Onsen.dpx");
		//repPanel.loadProjectAgents(ProjectFileName);
	}

	/****************************************************************************
	 * �v���W�F�N�g�t�@�C�����ݒ�
	 * @param ProjectFileName ���݁A�J����Ă���v���W�F�N�g�t�@�C�����B<br>
	 * IdeaMainFrame���Ă΂��
	 * @return �Ȃ�
	 ****************************************************************************/
	public void setProjectFileName(String ProjectFileName) {
		this.ProjectFileName = ProjectFileName;
	}

	/****************************************************************************
	 * �v���W�F�N�g�ݒ�
	 * @param pro ���݁A�J����Ă���v���W�F�N�g�t�@�C���B<br>
	 * IdeaMainFrame���Ă΂��
	 * @return �Ȃ�
	 ****************************************************************************/
	public void setProject(Project project) {
		this.project = project;
		//�v���W�F�N�g�폜���j���[�X�V
		if (project != null) {
			deletePrjMenuItem.setEnabled(true);
		} else {
			deletePrjMenuItem.setEnabled(false);
		}

		vecRepLoadAgent.clear();

		for (int i = 0; i < wp_max_cnt; i++) {
			vecWpLoadAgent[i].clear();
			vecWpLoadAgent2[i].clear();
		}

		LoadDir = "";
		for (int j = 0; j < project.getFolderPath().size(); j++) {
			if (LoadDir.equals("")) {
				LoadDir = (String) project.getFolderPath().elementAt(j);
			} else {
				LoadDir += ";" + (String) project.getFolderPath().elementAt(j);
			}
		}

		readLoadAgentFileInfo();
		System.setProperty("LoadDir", LoadDir);
		//vecWpReadCount.clear();
	}

	/****************************************************************************
	 * ���|�W�g���ւ�Ag�Ǎ�����
	 * @param mode "ALL_AGENT":�SAg�Ǎ� "SELECT_AGENT":�w�肵��Ag�Ǎ�
	 * @return �Ȃ�
	 ****************************************************************************/
	private void repExec(String mode) {

		Vector vecSelectFiles = null;
		Vector vecReadCount = null;
		Object[] options = { "OK" };

		if (project == null) {
			// �v���W�F�N�g���J����Ă��Ȃ���
			JOptionPane.showOptionDialog(this, parentframe
					.getBilingualMsg("0073"), parentframe
					.getBilingualMsg("0129"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);

			return;
		}

		if (project.getFileCount() == 0) {
			// �v���W�F�N�g�Ƀt�@�C�������݂��Ȃ���
			JOptionPane.showOptionDialog(this, parentframe
					.getBilingualMsg("0074"), parentframe
					.getBilingualMsg("0129"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);

			return;
		}
		if (mode.equals("SELECT_AGENT")) {
			// �w�肳�ꂽAg�̓Ǎ�
			SelectAgentFileDialog dlg = new SelectAgentFileDialog(parentframe,
					parentframe.getBilingualMsg("0052"), true, project,
					vecRepLoadAgent, null);
			//dlg.show();
			dlg.setVisible(true);
			if (dlg.getResult() == 0) {
				return;
				//System.out.println("�L�����Z���ł���");
			}

			vecSelectFiles = dlg.getSelectFiles();
			vecReadCount = dlg.getReadCount();
			//System.out.println("�I�𐔁F" + vecSelectFiles.size());
		} else {
			vecSelectFiles = (Vector) project.getFileNames().clone();
			vecSelectFiles = (Vector) project.getFileNamesWithPath().clone();
		}

		//���|�W�g�����N���A
		clear_Rep();

		if (!mode.equals("SELECT_AGENT")) {
			repPanel.loadProjectAgents(project.getProjectPath(),
					vecSelectFiles, LoadDir);
		} else {
			Vector vecFilenameWithPath = (Vector) project
					.getFileNamesWithPath();
			Vector vecSelectFileWk = new Vector();
			for (int i = 0; i < vecSelectFiles.size(); i++) {
				String SelectFileName = (String) vecSelectFiles.elementAt(i);
				boolean flag = false;
				for (int j = 0; j < vecFilenameWithPath.size(); j++) {
					String FilenameWithPath = (String) vecFilenameWithPath
							.elementAt(j);
					if (FilenameWithPath.endsWith(File.separator
							+ SelectFileName)) {
						vecSelectFileWk.addElement(FilenameWithPath);
					}
				}

			}

			repPanel.loadProjectAgents(project.getProjectPath(),
					vecSelectFileWk, LoadDir);
			//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecSelectFiles, null, DefaultDir );
		}

		//�Ǎ����s
		//repPanel.loadProjectAgents(project.getProjectPath(),vecSelectFiles, LoadDir );
		if (mode.equals("SELECT_AGENT")) {
			// �w�肳�ꂽAg�̓Ǎ��̎��̂݁A�ǂݍ��񂾃t�@�C�����o���Ă���
			// ���ꂪ�A�����[�h�����Ŏg����
			vecRepLoadAgent = vecSelectFiles;

			Vector vecOrgData = new Vector();
			try {
				FileReader f_in;
				BufferedReader b_in;
				String sLine;
				boolean FindFlag = false;

				if (new File(project.getProjectPath() + "loadagentinfo")
						.exists()) {
					b_in = new BufferedReader(new InputStreamReader(
							new FileInputStream(project.getProjectPath()
									+ "loadagentinfo"), "JISAutoDetect"));

					String inf_kind = "";
					String javafilename = "";
					while ((sLine = b_in.readLine()) != null) {
						if (sLine.equals("[rep]")) {
							inf_kind = sLine; //���|�W�g��
							vecOrgData.addElement(sLine);
						} else if (sLine.startsWith("[wp")) {
							inf_kind = sLine; //�֘ADash�t�@�C�����
							vecOrgData.addElement(sLine);
						} else {
							if (inf_kind.equals("[rep]")) {
								if (!FindFlag) {
									FindFlag = true;
									for (int i = 0; i < vecRepLoadAgent.size(); i++) {
										//if (vecOrgData.indexOf((String)vecRepLoadAgent.elementAt(i))==-1){
										vecOrgData
												.addElement((String) vecRepLoadAgent
														.elementAt(i));
										//}
									}
								}
							} else {
								vecOrgData.addElement(sLine);
							}
						}
					}
					b_in.close();
				}

				if (!FindFlag) {
					vecOrgData.addElement("[rep]");
					for (int i = 0; i < vecRepLoadAgent.size(); i++) {
						vecOrgData.addElement((String) vecRepLoadAgent
								.elementAt(i));
					}
				}

				File fp = new File(project.getProjectPath() + "loadagentinfo");
				FileOutputStream fos = new FileOutputStream(fp);
				PrintWriter pw = new PrintWriter(fos);
				for (int i = 0; i < vecOrgData.size(); i++) {
					pw.println((String) vecOrgData.elementAt(i));
				}
				pw.close();

			} catch (Exception ex) {
			}

		}
	}

	/****************************************************************************
	 * ���[�N�v���[�X�ւ�Ag�Ǎ�����
	 * @param mode "ALL_AGENT":�SAg�Ǎ� "SELECT_AGENT":�w�肵��Ag�Ǎ�
	 * @return �Ȃ�
	 ****************************************************************************/
	private void wpExec(String mode) {
		int wpIndex = wpTab.getSelectedIndex();
		Vector vecSelectFiles = null;
		Vector vecReadCount = null;
		Object[] options = { "OK" };

		if (project == null) {
			// �v���W�F�N�g���J����Ă��Ȃ���
			JOptionPane.showOptionDialog(this, parentframe
					.getBilingualMsg("0073"), parentframe
					.getBilingualMsg("0129"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			return;
		}

		if (project.getFileCount() == 0) {
			// �v���W�F�N�g�Ƀt�@�C�������݂��Ȃ���
			JOptionPane.showOptionDialog(this, parentframe
					.getBilingualMsg("0074"), parentframe
					.getBilingualMsg("0129"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			return;
		}
		if (mode.equals("SELECT_AGENT")) {
			// �w�肳�ꂽAg�̓Ǎ�
			//SelectAgentFileDialog dlg = new SelectAgentFileDialog(parentframe,title,true, project,vecWpLoadAgent,vecWpReadCount);
			//SelectAgentFileDialog dlg = new SelectAgentFileDialog(parentframe,parentframe.getBilingualMsg("0054"),true, project,vecWpLoadAgent[wpIndex],null);
			SelectAgentFileDialog dlg = new SelectAgentFileDialog(parentframe,
					parentframe.getBilingualMsg("0054"), true, project,
					vecWpLoadAgent2[wpIndex], null);
			//dlg.show();
			dlg.setVisible(true);
			if (dlg.getResult() == 0) {
				return;
				//System.out.println("�L�����Z���ł���");
			}

			vecSelectFiles = dlg.getSelectFiles();
			for (int i = 0; i < vecSelectFiles.size(); i++) {
				String s = (String) vecSelectFiles.elementAt(i);
				String ss = s;
			}
			//vecReadCount = dlg.getReadCount();
			//System.out.println("�I�𐔁F" + vecSelectFiles.size());
		} else {
			//vecSelectFiles = (Vector)project.getFileNames().clone();
			vecSelectFiles = (Vector) project.getFileNamesWithPath().clone();
			//for (int i=0;i<vecSelectFiles.size(); i++ ) {
			//  vecReadCount.addElement("1");
			//}
			vecWpLoadAgent[wpIndex].clear();
			vecWpLoadAgent2[wpIndex].clear();
		}

		// ���[�N�v���[�X�ւ�Ag�Ǎ����s
		if (!mode.equals("SELECT_AGENT")) {
			//�w�肳�ꂽAg�̓Ǎ��ł́A���[�N�v���[�X�͏��������Ȃ�
			clear_Wp();
			wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),
					vecSelectFiles, null, LoadDir);
		} else {
			Vector vecFilenameWithPath = (Vector) project
					.getFileNamesWithPath();
			Vector vecSelectFileWk = new Vector();
			for (int i = 0; i < vecSelectFiles.size(); i++) {
				String SelectFileName = (String) vecSelectFiles.elementAt(i);
				boolean flag = false;
				for (int j = 0; j < vecFilenameWithPath.size(); j++) {
					String FilenameWithPath = (String) vecFilenameWithPath
							.elementAt(j);
					if (FilenameWithPath.endsWith(File.separator
							+ SelectFileName)) {
						vecSelectFileWk.addElement(FilenameWithPath);
					}
				}

			}

			wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),
					vecSelectFileWk, null, LoadDir);
			//wpPanel[wpIndex].loadProjectAgents(project.getProjectPath(),vecSelectFiles, null, DefaultDir );
		}

		if (mode.equals("SELECT_AGENT")) {
			//�w�肳�ꂽAg�̓Ǎ����̂݁A�ǂݍ��񂾃t�@�C�����o���Ă���
			//���ꂪ�A�����[�h�����Ŏg����
			vecWpLoadAgent2[wpIndex].clear();
			for (int i = 0; i < vecSelectFiles.size(); i++) {
				vecWpLoadAgent[wpIndex].addElement(vecSelectFiles.elementAt(i));
				vecWpLoadAgent2[wpIndex]
						.addElement(vecSelectFiles.elementAt(i));
			}

			Vector vecOrgData = new Vector();
			try {
				FileReader f_in;
				BufferedReader b_in;
				String sLine;

				boolean FindFlag = false;
				if (new File(project.getProjectPath() + "loadagentinfo")
						.exists()) {
					b_in = new BufferedReader(new InputStreamReader(
							new FileInputStream(project.getProjectPath()
									+ "loadagentinfo"), "JISAutoDetect"));

					String inf_kind = "";
					String javafilename = "";
					Vector vecEndWpName = new Vector();
					while ((sLine = b_in.readLine()) != null) {
						if (sLine.equals("[rep]")) {
							inf_kind = sLine; //���|�W�g��
							vecOrgData.addElement(sLine);
						} else if (sLine.startsWith("[wp")) {
							inf_kind = sLine; //�֘ADash�t�@�C�����
							vecOrgData.addElement(sLine);
						} else {
							if (inf_kind
									.equals("[wp"
											+ new Integer(wpIndex + 1)
													.toString() + "]")) {
								if (vecEndWpName.indexOf("[wp"
										+ new Integer(wpIndex + 1).toString()
										+ "]") == -1) {
									FindFlag = true;
									for (int i = 0; i < vecWpLoadAgent2[wpIndex]
											.size(); i++) {
										vecOrgData
												.addElement((String) vecWpLoadAgent2[wpIndex]
														.elementAt(i));
									}
									vecEndWpName.addElement("[wp"
											+ new Integer(wpIndex + 1)
													.toString() + "]");
								}
							} else {
								vecOrgData.addElement(sLine);
							}
						}
					}
					b_in.close();
				}
				if (!FindFlag) {
					vecOrgData.addElement("[wp"
							+ new Integer(wpIndex + 1).toString() + "]");
					for (int i = 0; i < vecWpLoadAgent2[wpIndex].size(); i++) {
						vecOrgData.addElement((String) vecWpLoadAgent2[wpIndex]
								.elementAt(i));
					}
				}

				File fp = new File(project.getProjectPath() + "loadagentinfo");
				FileOutputStream fos = new FileOutputStream(fp);
				PrintWriter pw = new PrintWriter(fos);
				for (int i = 0; i < vecOrgData.size(); i++) {
					pw.println((String) vecOrgData.elementAt(i));
				}
				pw.close();

			} catch (Exception ex) {
			}

		}
		//vecWpLoadAgent = vecSelectFiles;
		//vecWpReadCount = vecReadCount;
	}

	/****************************************************************************
	 * ���|�W�g���A���[�N�v���[�X�ւ�Ag������
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	public void clear_Wp_Rep() {
		JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
		if (iframes.length == 0)
			return;

		// ���|�W�g���������
		canvasR.removeAgentAll();
		repPanel.removeAgentAll();
		canvasR.repaint();

		// ���[�N�v���[�X�������
		for (int i = 0; i < wp_cnt; i++) {
			canvasW[i].removeAgentAll();
			wpPanel[i].removeAgentAll();
			canvasW[i].repaint();
		}

		// �C���X�y�N�^������
		//inspectorDesktop.removeAll();
		//inspectorDesktop.repaint();

		// ���݃A�N�e�B�u�ȃ��[�N�v���[�X����J���ꂽ�A�C���X�y�N�^������
		//JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
		if (iframes.length == 0)
			return;

		for (int i = 0; i < iframes.length; i++) {

			if (iframes[i].getToolTipText().equals("Workplace")
					|| iframes[i].getToolTipText().equals("Repository")) {
				inspectorDesktop.remove(iframes[i]);
			}
		}

		inspectorDesktop.repaint();
		System.gc();

	}

	/****************************************************************************
	 * ���[�N�v���[�X�ւ�Ag������
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	public void clear_Wp() {
		// ���[�N�v���[�X�͕������邽�߁A���݃A�N�e�B�u�ȃ��[�N�v���[�X�𒲂ׂ�
		int wpIndex = wpTab.getSelectedIndex();

		// ���݃A�N�e�B�u�ȃ��[�N�v���[�X��Ag������
		canvasW[wpIndex].removeAgentAll();
		wpPanel[wpIndex].removeAgentAll();
		canvasW[wpIndex].repaint();

		// ���݃A�N�e�B�u�ȃ��[�N�v���[�X����J���ꂽ�A�C���X�y�N�^������
		JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
		if (iframes.length == 0)
			return;

		for (int i = 0; i < iframes.length; i++) {

			if (iframes[i].getToolTipText().equals("Workplace")) {
				inspectorDesktop.remove(iframes[i]);
			}
		}

		inspectorDesktop.repaint();
		System.gc();
	}

	/****************************************************************************
	 * ���|�W�g���ւ�Ag������
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	public void clear_Rep() {
		canvasR.removeAgentAll();
		repPanel.removeAgentAll();
		canvasR.repaint();

		// ���|�W�g������J���ꂽ�C���X�y�N�^������
		JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
		if (iframes.length == 0)
			return;

		for (int i = 0; i < iframes.length; i++) {

			if (iframes[i].getToolTipText().equals("Repository")) {
				inspectorDesktop.remove(iframes[i]);
			}
		}

		inspectorDesktop.repaint();
		System.gc();
	}

	/****************************************************************************
	 * ���j���[�A�C�e���̍쐬
	 * @param label�@���j���[������
	 * @param command�@�A�N�V�����R�}���h
	 * @param icon�@�A�C�R��
	 * @return ���j���[�A�C�e��
	 ****************************************************************************/
	private JMenuItem menuItem(String label, String command, Icon icon) {
		JMenuItem item = new JMenuItem(label, icon);
		item.setActionCommand(command);
		item.addActionListener(this);
		return item;
	}

	/****************************************************************************
	 * DVM�擾
	 * @param �Ȃ�
	 * @return DVM
	 ****************************************************************************/
	public DVM getDVM() {
		return wpPanel[0].wp.getNewIf().getDVM();
	}

	/****************************************************************************
	 * ���|�W�g����DVM�擾
	 * @param �Ȃ�
	 * @return ���|�W�g����DVM
	 ****************************************************************************/
	public DVM getRepDVM() {
		return repPanel.rep.getNewIf().getDVM();
	}

	/****************************************************************************
	 * ���[�N�v���[�X��DVM�擾�i���g�p�j
	 * @param �Ȃ�
	 * @return ���[�N�v���[�X��DVM
	 ****************************************************************************/
	/*
	 public DVM getWpDVM() {
	 return wpPanel[0].wp.getNewIf().getDVM();
	 }
	 */

	/****************************************************************************
	 * ���[�N�v���[�X��DVM�擾�i���g�p�j
	 * @param index ���[�N�v���[�X�̔ԍ�
	 * @return ���[�N�v���[�X��DVM
	 ****************************************************************************/
	public DVM getWpDVM(int index) {
		return wpPanel[index].wp.getNewIf().getDVM();
	}

	/****************************************************************************
	 * �v���W�F�N�g�����̃��j���[�쐬
	 * @param histlist �ߋ��ɊJ���ꂽ�v���W�F�N�g�t�@�C�����i�p�X���j
	 * @return �Ȃ�
	 ****************************************************************************/
	public void setOpenHist(Vector histlist) {

		if (histlist.size() <= 0) {
			openagainprjmenu.removeAll();
			openagainprjPopupMenu.removeAll();
			openagainprjmenu.setEnabled(false);
			openagainprjBtn.setEnabled(false);
		} else {
			openagainprjmenu.removeAll();
			openagainprjmenu.setEnabled(true);
			openagainprjBtn.setEnabled(true);
			openagainprjPopupMenu.removeAll();
			for (int i = 0; i < histlist.size(); i++) {
				String addname = (String) histlist.elementAt(i);

				//�T�u���j���[
				openagainprjmenu.add(menuItem(addname, "againPrjct", null));

				//�|�b�v�A�b�v���j���[
				openagainprjPopupMenu
						.add(menuItem(addname, "againPrjct", null));

			}

			subMenuItem = new JMenuItem(parentframe.getBilingualMsg("0036"));
			subMenuItem.setActionCommand("deletehist");
			subMenuItem.addActionListener(this);

			popMenuItem = new JMenuItem(parentframe.getBilingualMsg("0036"));
			popMenuItem.setActionCommand("deletehist");
			popMenuItem.addActionListener(this);

			//�T�u���j���[
			//openagainprjmenu.add(menuItem(parentframe.getBilingualMsg("0036"), "deletehist", null));
			openagainprjmenu.add(subMenuItem);

			//�|�b�v�A�b�v���j���[
			//openagainprjPopupMenu.add(menuItem(parentframe.getBilingualMsg("0036"), "deletehist", null));
			openagainprjPopupMenu.add(popMenuItem);

		}

	}

	void openagainprjBtn_stateChanged(ChangeEvent e) {
		try {
			openagainprjPopupMenu.show(toolbar, openagainprjBtn.getX(),
					openagainprjBtn.getY() + openagainprjBtn.getHeight());
		} catch (Exception ee) {
		}
	}

	void openagainprjPopupMenu_popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		openagainprjBtn.setSelected(false);
	}

	/****************************************************************************
	 * ���̃t�F�[�Y�ŃV�~�����[�^�̐ݒ肪�ύX���ꂽ��A����V�~�����[�g�t�F�[�X���J���ꂽ����<br>
	 * �V�~�����[�^�̐ݒ�̃`�F�b�N���s���B<br>
	 * IdeaMainFrame����Ă΂��
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	public void SettingCheck() {
		//System.out.println("SettingCheck");
		getRepDVM().finalizeDVM();
		getWpDVM(0).finalizeDVM();
		getWpDVM(1).finalizeDVM();
		getWpDVM(2).finalizeDVM();
		getWpDVM(3).finalizeDVM();
		getWpDVM(4).finalizeDVM();

		getRepDVM().RmiModule_ReCreate();
		getWpDVM(0).RmiModule_ReCreate();
		getWpDVM(1).RmiModule_ReCreate();
		getWpDVM(2).RmiModule_ReCreate();
		getWpDVM(3).RmiModule_ReCreate();
		getWpDVM(4).RmiModule_ReCreate();

		//getDVM().RmiModule_ReCreate();
		String wpCntStr = System.getProperty("dash.wp.cnt");
		wp_cnt = new Integer(wpCntStr).intValue();
		int cnt = wpTab.getComponentCount();

		if (wp_cnt > cnt) {
			for (int i = cnt; i < wp_cnt; i++) {
				JSplitPane splitPane2 = new JSplitPane(
						JSplitPane.VERTICAL_SPLIT, true, canvasW_BasePanel[i],
						wpPanel[i]);
				splitPane2.setDividerLocation(360);
				splitPane2.setDividerSize(5);
				wpTab.add("WP" + new Integer(i + 1).toString(), splitPane2);
				wpPanel[i].wp.getNewIf().getDVM().setWpTab(wpTab);
			}
		} else if (wp_cnt < cnt) {
			for (int i = wp_cnt; i < cnt; i++) {
				int wpIndex = i;
				canvasW[wpIndex].removeAgentAll();
				wpPanel[wpIndex].removeAgentAll();
				wpPanel[wpIndex].clearLog();
				canvasW[wpIndex].repaint();
				vecWpLoadAgent[wpIndex].clear();
				vecWpLoadAgent2[wpIndex].clear();

				JInternalFrame[] iframes = inspectorDesktop.getAllFrames();
				for (int j = 0; j < iframes.length; j++) {

					if (iframes[j].getToolTipText().equals("Workplace")) {
						inspectorDesktop.remove(iframes[j]);
					}
				}

				inspectorDesktop.repaint();

				wpTab.remove(wp_cnt);
			}
		}
		wpTab.repaint();
		for (int i = 0; i < wp_cnt; i++) {
			if (!WpInitialized[i]) {
				canvasW[i].initialize();
				WpInitialized[i] = true;
			}
		}
		//for (int i=0; i<wp_cnt; i++ ) {
		//  wpTab.getComponentCount()
		//}

	}

	/****************************************************************************
	 * �O���c�[�����j���[�̍č쐬<br>
	 * IdeaMainFrame����Ă΂��
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	public void ReMakeOutsideToolMenu() {

		// �O���c�[���p���j���[����蒼��
		outsideTool.removeAll();
		//outsideTool2.removeAll();

		JMenuItem toolMenu3 = menuItem(parentframe.getBilingualMsg("0025")
				+ "(C)...", "SetOutsideTool", null);
		toolMenu3.setMnemonic('C');
		outsideTool.add(toolMenu3);

		JMenuItem toolMenu3_2 = menuItem(parentframe.getBilingualMsg("0025")
				+ "(C)...", "SetOutsideTool", null);
		toolMenu3_2.setMnemonic('C');
		//outsideTool2.add(toolMenu3_2);

		if (parentframe.vecOutsideToolInfo.size() > 0) {
			outsideTool.addSeparator();
			//outsideTool2.addSeparator();
		}

		for (int i = 0; i < parentframe.vecOutsideToolInfo.size(); i++) {
			String wk = (String) parentframe.vecOutsideToolInfo.elementAt(i);

			StringTokenizer st = new StringTokenizer(wk, ",");
			while (st.hasMoreTokens()) {
				String data = st.nextToken();

				JMenuItem toolMenu4 = menuItem(new Integer(i + 1).toString()
						+ ":" + data, "OutsideTool_"
						+ new Integer(i).toString(), null);
				outsideTool.add(toolMenu4);

				JMenuItem toolMenu4_2 = menuItem(new Integer(i + 1).toString()
						+ ":" + data, "OutsideTool_"
						+ new Integer(i).toString(), null);
				//outsideTool2.add(toolMenu4_2);
				break;
			}
		}
	}

	/****************************************************************************
	 * �t�H�[���ɕ\������Ă��郉�x�����̕������ύX<br>
	 * IDE�I�v�V�����Ŏg�p���ꂪ�ύX���ꂽ�ꍇ�ɁAIdeaMainFrame���Ă΂��
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	public void changeFormLabel() {

		for (int i = 0; i < wp_max_cnt; i++) {
			titleBdr1[i].setTitle(parentframe.getBilingualMsg("0050"));
		}

		titleBdr2.setTitle(parentframe.getBilingualMsg("0047"));

		btn1.setToolTipText(parentframe.getBilingualMsg("0051"));
		btn2.setToolTipText(parentframe.getBilingualMsg("0052"));
		btn3.setToolTipText(parentframe.getBilingualMsg("0053"));
		btn4.setToolTipText(parentframe.getBilingualMsg("0054"));
		btn5.setToolTipText(parentframe.getBilingualMsg("0055"));
		btn6.setToolTipText(parentframe.getBilingualMsg("0056"));
		btn7.setToolTipText(parentframe.getBilingualMsg("0057"));
		btn8.setToolTipText(parentframe.getBilingualMsg("0058"));
		btn9.setToolTipText(parentframe.getBilingualMsg("0059"));
		btn10.setToolTipText(parentframe.getBilingualMsg("0060"));

	}

	/****************************************************************************
	 * ���j���[�������ύX<br>
	 * IDE�I�v�V�����Ŏg�p���ꂪ�ύX���ꂽ�ꍇ�ɁAIdeaMainFrame���Ă΂��
	 * @param �Ȃ�
	 * @return �Ȃ�
	 ****************************************************************************/
	public void changeMenuText() {

		filemenu.setText(parentframe.getBilingualMsg("0001") + "(F)");

		newFileMenuItem.setText(parentframe.getBilingualMsg("0002") + "(N)...");
		newProjectMenuItem.setText(parentframe.getBilingualMsg("0003")
				+ "(P)...");

		filemenu1.setText(parentframe.getBilingualMsg("0004") + "(O)...");
		filemenu2.setText(parentframe.getBilingualMsg("0008") + "(X)");

		openagainprjmenu.setText(parentframe.getBilingualMsg("0005") + "(R)");
		deletePrjMenuItem.setText(parentframe.getBilingualMsg("0116"));

		//--------------------------------------------------------------------------
		// �c�[�����j���[
		//--------------------------------------------------------------------------
		toolMenu_DevScreen.setText(parentframe.getBilingualMsg("0021") + "(T)");

		// ���j���[�A�C�e���쐬
		toolMenu1.setText(parentframe.getBilingualMsg("0022") + "(E)...");
		toolMenu2.setText(parentframe.getBilingualMsg("0023") + "(K)...");
		toolMenu4.setText(parentframe.getBilingualMsg("0117") + "...");
		toolMenu5.setText(parentframe.getBilingualMsg("0118") + "...");

		outsideTool.setText(parentframe.getBilingualMsg("0024") + "(O)");
		//outsideTool2.setText(parentframe.getBilingualMsg("0024") + "(O)");

		toolMenu3.setText(parentframe.getBilingualMsg("0025") + "(C)...");

		openprjBtn.setToolTipText(parentframe.getBilingualMsg("0004"));
		openagainprjBtn.setToolTipText(parentframe.getBilingualMsg("0005"));

		openagainprjmenu.setText(parentframe.getBilingualMsg("0036"));

		//add uchiya
		//--------------------------------------------------------------------------
		// ���b�Z�[�W���j���[
		//--------------------------------------------------------------------------
		messageMenu_DevScreen.setText(parentframe.getBilingualMsg("0222")
				+ "(M)");
		send_acl_menu.setText(parentframe.getBilingualMsg("0223"));
		set_acl_menu.setText(parentframe.getBilingualMsg("0224"));

		//--------------------------------------------------------------------------
		//����
		//--------------------------------------------------------------------------
		if (subMenuItem != null) {
			subMenuItem.setText(parentframe.getBilingualMsg("0036"));
		}
		if (popMenuItem != null) {
			popMenuItem.setText(parentframe.getBilingualMsg("0036"));
		}

	}

	private void readLoadAgentFileInfo() {
		try {
			FileReader f_in;
			BufferedReader b_in;
			String sLine;
			boolean FindFlag = false;
			Vector vecRepAgent = new Vector();
			Vector[] vecWpAgent = new Vector[5];
			for (int i = 0; i < 5; i++) {
				vecWpAgent[i] = new Vector();
			}

			if (new File(project.getProjectPath() + "loadagentinfo").exists()) {
				b_in = new BufferedReader(new InputStreamReader(
						new FileInputStream(project.getProjectPath()
								+ "loadagentinfo"), "JISAutoDetect"));

				String inf_kind = "";
				String javafilename = "";
				while ((sLine = b_in.readLine()) != null) {
					if (sLine.equals("[rep]")) {
						inf_kind = sLine; //���|�W�g��
					} else if (sLine.startsWith("[wp")) {
						inf_kind = sLine;
					} else {
						if (inf_kind.equals("[rep]")) {
							vecRepAgent.addElement(sLine);
						} else if (inf_kind.startsWith("[wp")) {
							int index = new Integer(inf_kind.substring(3,
									inf_kind.lastIndexOf("]"))).intValue() - 1;
							vecWpAgent[index].addElement(sLine);
						}
					}
				}
				b_in.close();
			}
			setRepLoadAgent(vecRepAgent);
			for (int i = 0; i < 5; i++) {
				setWpLoadAgent(vecWpAgent[i], i);
			}
		} catch (Exception ex) {
			String err = ex.getMessage();
			String err1 = err;
		}

	}

	private void setRepLoadAgent(Vector vecRepLoadAgent) {

		for (int i = 0; i < vecRepLoadAgent.size(); i++) {
			this.vecRepLoadAgent.addElement((String) vecRepLoadAgent
					.elementAt(i));
		}
	}

	private void setWpLoadAgent(Vector vecWpLoadAgent, int index) {

		for (int i = 0; i < vecWpLoadAgent.size(); i++) {
			this.vecWpLoadAgent2[index].addElement((String) vecWpLoadAgent
					.elementAt(i));
		}
	}

	/** add uchiya
	 * Message���j���[��ACL�e���v���[�g��I�������Ƃ��ɌĂяo�����ActionListener
	 */
	class MessageAction implements ActionListener {

		/**
		 * key   = ���j���[�A�C�e��.�T�u���j���[�A�C�e���B
		 * value = ���b�Z�[�W
		 */
		private Hashtable messageHash = new Hashtable();

		void putHash(String command, Hashtable hash) {
			messageHash.put(command, hash);
		}

		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			Hashtable hash = (Hashtable) messageHash.get(command);

			int i = ((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).logTabbedPane
					.indexOfTab("acl-editor");
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).logTabbedPane
					.setSelectedIndex(i);

			String performative = (String) hash.get("performative");
			String to = (String) hash.get("to");
			String arrival = (String) hash.get("arrival");
			String content = (String) hash.get("content");

			if (arrival != null)
				to = to + "@" + arrival;
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setPerfField(performative);
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setToField(to);
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setContArea(content);
		}
	}

	/** add mabune
	 * Message���j���[��ACL�̗������g�p��I�������Ƃ��ɌĂяo�����ActionListener
	 */
	class HistoryAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String[] acls = { "", "", "" };
			String perfomative = "";
			String to = "";
			String content = "";

			AllAclInputHistList dlg = new AllAclInputHistList(parentframe,
					"History of ACL");
			dlg.setVisible(true);
			acls = dlg.getSelStr();
			perfomative = acls[0];
			to = acls[1];
			content = acls[2];

			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setPerfField(perfomative);
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setToField(to);
			((NewIf2) wpPanel[wpTab.getSelectedIndex()].wp.getNewIf()).aclPanel
					.setContArea(content);
		}
	}

	/** add uchiya
	 * Message���j���[�́uAnalyze message log�v��I�������Ƃ��ɌĂяo�����ActionListener
	 */
	class AnalyzeAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String[] str = new String[0];
			Main.main(str);
		}
	}
}