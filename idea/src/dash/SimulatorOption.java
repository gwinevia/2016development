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
 * <p>�^�C�g��:�V�~�����[�^�ݒ�_�C�A���O </p>
 * <p>����:����V�~�����[�g�Ɋւ���A�ݒ���s�� </p>
 * <p>���쌠: Copyright (c) 2003</p>
 * <p>��Ж�:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class SimulatorOption extends EnhancedDialog implements ActionListener {
	private IdeaMainFrame mainframe = null;
	private JPanel panel1 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JTextField txtFieldBaseProcessPlace = new JTextField(40);
	private JTextField txtFieldNameServerAdr = new JTextField(40);
	private JTextField txtFieldScriptsFolder = new JTextField(40);
	private JTextField txtFieldRepositoryName = new JTextField(40);
	private JTextField txtFieldWorkplaceName1 = new JTextField(40);
	private JTextField txtFieldWorkplaceName2 = new JTextField(40);
	private JTextField txtFieldWorkplaceName3 = new JTextField(40);
	private JTextField txtFieldWorkplaceName4 = new JTextField(40);
	private JTextField txtFieldWorkplaceName5 = new JTextField(40);
	private JTextField txtFieldTimeout = new JTextField(40);
	private JComboBox cmbWpCnt = new JComboBox();
	private JButton btnOk = new JButton("OK");
	private JButton btnCancel = null;
	private boolean result = false;

	/** ���|�W�g���E���[�N�v���[�X�̕\���ʒu�̃`�F�b�N */
	private JCheckBox cbRepWpIndiPos = new JCheckBox();

	public SimulatorOption(IdeaMainFrame mainframe) {
		super(mainframe, "�V�~�����[�^�[������ݒ�", true);
		try {
			this.mainframe = mainframe;
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {

		btnCancel = new JButton(mainframe.getBilingualMsg("0126"));

		setTitle(mainframe.getBilingualMsg("0118"));
		setSize(400, 620);
		this.setResizable(false);
		// �t���[���T�C�Y
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation(
			(screenSize.width - frameSize.width) / 2,
			(screenSize.height - frameSize.height) / 2);

		panel1 = new JPanel() {
			Insets insets = new Insets(0, 4, 0, 0);
			public Insets getInsets() {
				return insets;
			}
		};
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));

		getContentPane().setLayout(new BorderLayout());

		panel1.add(createSetBaseProcessPlacePanel());
		panel1.add(createSetNameServerAdrPanel());
		panel1.add(createSetScriptsFolderPanel());

		panel1.add(createSetReposiyotyNamePanel());
		panel1.add(createSetWorkplaceNamePanel());
		panel1.add(createSetTimeoutPanel());

		panel1.add(createSetWpCntPanel());

JPanel grid = new JPanel();
grid.setLayout(new GridLayout(1,2));
	//	panel1.add(createSetViewerPanel());
//		panel1.add(createSetLogPanel());
		grid.add(createSetViewerPanel());
		grid.add(createSetLogPanel());
		grid.add(createSetExternalInspectorPanel());
		panel1.add(grid);
				
		createRepWpindicationPosPanel();
		//panel1.add(createRepWpindicationPosPanel());

		JPanel panel2 = new JPanel() {
			Insets insets = new Insets(0, 4, 0, 0);
			public Insets getInsets() {
				return insets;
			}
		};

		btnOk.setActionCommand("OK");
		btnOk.addActionListener(this);
		btnCancel.setActionCommand("CANCEL");
		btnCancel.addActionListener(this);
		panel2.add(btnOk);
		panel2.add(btnCancel);
		getContentPane().add(panel1, BorderLayout.CENTER);
		getContentPane().add(panel2, BorderLayout.SOUTH);

	}
	
	private JRadioButton rb1;
	private JRadioButton rb2;
	private JRadioButton rb3;
	private JRadioButton rb4;
	private JRadioButton rb5;
	private JRadioButton rb6;
	
	/**
	* �r���[�A�𗘗p���邩�ǂ����ݒ肵�܂��B
	*/
	private Component createSetViewerPanel() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new GridLayout(1, 2));
		JPanel panel3 = new JPanel(new GridLayout(1, 1));

		String s = System.getProperty("dash.viewer");
		if (s == null)
			s = "off";
		if (s.equals("on")) {
			rb1 = new JRadioButton("on", true);
			rb2 = new JRadioButton("off");
		} else {
			rb1 = new JRadioButton("on");
			rb2 = new JRadioButton("off", true);
		}
		ButtonGroup bg = new ButtonGroup();
		bg.add(rb1);
		bg.add(rb2);
		panel2.add(rb1);
		panel2.add(rb2);
		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("��" + mainframe.getBilingualMsg("0178")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);

		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0227")));
		return panel1;
	}
	
	/**
	* ���O�ۊǋ@�\�𗘗p���邩�ǂ����ݒ肵�܂��B
	* create 05/02/17
	*/
	private Component createSetLogPanel() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new GridLayout(1, 2));
		JPanel panel3 = new JPanel(new GridLayout(1, 1));

		String s = System.getProperty("dash.log");
		if (s == null)
			s = "off";
		if (s.equals("on")) {
			rb3 = new JRadioButton("on", true);
			rb4 = new JRadioButton("off");
		} else {
			rb3 = new JRadioButton("on");
			rb4 = new JRadioButton("off", true);
		}
		ButtonGroup bg = new ButtonGroup();
		bg.add(rb3);
		bg.add(rb4);
		panel2.add(rb3);
		panel2.add(rb4);
		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("��" + mainframe.getBilingualMsg("0178")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);

		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0228")));
		return panel1;
	}

	/**
	* �C���X�y�N�^���O���E�B���h�E�����邩�ǂ����ݒ肵�܂��B
	* create 05/02/17
	*/
	private Component createSetExternalInspectorPanel() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new GridLayout(1, 2));
		JPanel panel3 = new JPanel(new GridLayout(1, 1));

		String s = System.getProperty("dash.inspector");
		if (s == null)
			s = "off";
		if (s.equals("on")) {
			rb5 = new JRadioButton("on", true);
			rb6 = new JRadioButton("off");
		} else {
			rb5 = new JRadioButton("on");
			rb6 = new JRadioButton("off", true);
		}
		ButtonGroup bg = new ButtonGroup();
		bg.add(rb5);
		bg.add(rb6);
		panel2.add(rb5);
		panel2.add(rb6);
		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("��" + mainframe.getBilingualMsg("0178")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);

		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0230")));
		return panel1;
	}
	
	private JPanel createRepWpindicationPosPanel() {

		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel(new GridLayout(1, 1));

		if (System.getProperty("dash.r_w.pos") == null) {
			cbRepWpIndiPos.setSelected(true);
		} else {
			if (System.getProperty("dash.r_w.pos").equals("fixed")) {
				cbRepWpIndiPos.setSelected(true);
			} else {
				cbRepWpIndiPos.setSelected(false);
			}
		}
		cbRepWpIndiPos.setText(mainframe.getBilingualMsg("0201"));
		panel2.add(cbRepWpIndiPos, BorderLayout.NORTH);

		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("�@��" + mainframe.getBilingualMsg("0178")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);
		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0200")));
		return panel1;

	}

	private JPanel createSetBaseProcessPlacePanel() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new BorderLayout());
		JPanel panel3 = new JPanel(new GridLayout(1, 1));

		panel2.add(txtFieldBaseProcessPlace, BorderLayout.NORTH);
		txtFieldBaseProcessPlace.setText(
			System.getProperty("dash.userClassPath"));
		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("�@��" + mainframe.getBilingualMsg("0170")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);
		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0171")));
		return panel1;
	}

	private JPanel createSetNameServerAdrPanel() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new BorderLayout());
		JPanel panel3 = new JPanel(new GridLayout(4, 1));

		panel2.add(txtFieldNameServerAdr, BorderLayout.NORTH);
		txtFieldNameServerAdr.setText(System.getProperty("dash.nameserver"));

		panel3.add(new JLabel("�y" + mainframe.getBilingualMsg("0172") + "�z"));
		panel3.add(
			new JLabel(
				"�@"
					+ mainframe.getBilingualMsg("0173")
					+ "�Fleo.suga.net.it-chiba.ac.jp"));
		panel3.add(
			new JLabel(
				"�@" + mainframe.getBilingualMsg("0174") + "�F192.168.101.xxx"));
		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("�@��" + mainframe.getBilingualMsg("0175")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);
		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0176")));
		return panel1;
	}

	private JPanel createSetScriptsFolderPanel() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new BorderLayout());
		JPanel panel3 = new JPanel(new GridLayout(2, 1));

		txtFieldScriptsFolder.setText(System.getProperty("dash.r.path"));
		panel2.add(txtFieldScriptsFolder, BorderLayout.NORTH);

		//panel3.add (new JLabel("�y�ݒ��z"));
		//panel3.add (new JLabel("�@Y\\�z�X�g���Fleo.suga.net.it-chiba.ac.jp"));
		//panel3.add (new JLabel("�@IP�A�h���X�F192.168.101.xxx"));
		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("�@��" + mainframe.getBilingualMsg("0175")));
		lbl.setForeground(Color.red);

		//panel1.add(panel3, BorderLayout.NORTH);
		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0177")));
		return panel1;
	}

	private JPanel createSetReposiyotyNamePanel() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new BorderLayout());
		JPanel panel3 = new JPanel(new GridLayout(1, 1));

		txtFieldRepositoryName.setText(System.getProperty("dash.r.name"));
		panel2.add(txtFieldRepositoryName, BorderLayout.NORTH);

		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("�@��" + mainframe.getBilingualMsg("0178")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);
		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0179")));
		return panel1;
	}
	private JPanel createSetWorkplaceNamePanel() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new BorderLayout());
		JPanel panel3 = new JPanel(new GridLayout(1, 1));
		JPanel panel4 = new JPanel(new GridLayout(2, 3));

		JPanel panel5 = new JPanel(new BorderLayout());
		JPanel panel6 = new JPanel(new BorderLayout());
		JPanel panel7 = new JPanel(new BorderLayout());
		JPanel panel8 = new JPanel(new BorderLayout());
		JPanel panel9 = new JPanel(new BorderLayout());

		String WpName = System.getProperty("dash.w.name");
		StringTokenizer st = new StringTokenizer(WpName, ",");
		int cnt = 0;
		while (st.hasMoreTokens()) {
			String data = st.nextToken();
			cnt++;
			switch (cnt) {
				case 1 :
					txtFieldWorkplaceName1.setText(data);
					panel5.add(new JLabel(" WP1�F"), BorderLayout.WEST);
					panel5.add(txtFieldWorkplaceName1, BorderLayout.CENTER);
					panel4.add(panel5);
					break;
				case 2 :
					txtFieldWorkplaceName2.setText(data);
					panel6.add(new JLabel(" WP2�F"), BorderLayout.WEST);
					panel6.add(txtFieldWorkplaceName2, BorderLayout.CENTER);
					panel4.add(panel6);
					break;
				case 3 :
					txtFieldWorkplaceName3.setText(data);
					panel7.add(new JLabel(" WP3�F"), BorderLayout.WEST);
					panel7.add(txtFieldWorkplaceName3, BorderLayout.CENTER);
					panel4.add(panel7);
					break;
				case 4 :
					txtFieldWorkplaceName4.setText(data);
					panel8.add(new JLabel(" WP4�F"), BorderLayout.WEST);
					panel8.add(txtFieldWorkplaceName4, BorderLayout.CENTER);
					panel4.add(panel8);
					break;
				case 5 :
					txtFieldWorkplaceName5.setText(data);
					panel9.add(new JLabel(" WP5�F"), BorderLayout.WEST);
					panel9.add(txtFieldWorkplaceName5, BorderLayout.CENTER);
					panel4.add(panel9);
					break;
			}

		}

		panel2.add(panel4, BorderLayout.NORTH);

		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("�@��" + mainframe.getBilingualMsg("0178")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);
		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0180")));
		return panel1;
	}

	private JPanel createSetTimeoutPanel() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new BorderLayout());

		txtFieldTimeout.setText(System.getProperty("dash.waittimeForNonstop"));
		panel2.add(txtFieldTimeout, BorderLayout.NORTH);

		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0181")));
		return panel1;
	}

	private JPanel createSetWpCntPanel() {
		//JPanel panel1 = new JPanel (new BorderLayout() );
		JPanel panel1 = new JPanel();

		//JPanel panel2 = new JPanel (new BorderLayout() );
		//JPanel panel3 = new JPanel (new GridLayout(2,1) );

		Vector vecCnt = new Vector();
		for (int i = 1; i <= 5; i++) {
			vecCnt.addElement(new Integer(i).toString());
		}

		cmbWpCnt = new JComboBox(vecCnt);
		cmbWpCnt.setSelectedItem(System.getProperty("dash.wp.cnt"));
		//panel2.add(cmbWpCnt,BorderLayout.NORTH);
		//panel3.add(new JLabel("��"),BorderLayout.NORTH);

		//panel1.add(panel2, BorderLayout.WEST);
		//panel1.add(panel3, BorderLayout.CENTER);

		panel1.add(cmbWpCnt);
		panel1.add(new JLabel(mainframe.getBilingualMsg("0182")));

		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0183")));
		return panel1;
	}

	// �C�x���g����
	public void actionPerformed(ActionEvent e) /*throws java.io.IOException, java.lang.Exception*/ {

		try {

			String action = e.getActionCommand();

			if (action.equals("OK")) {
				ok();
			} else if (action.equals("CANCEL")) {
				cancel();
			}
		} catch (Exception e2) {
			String s = e2.getLocalizedMessage();
			s = e2.getMessage();
			s = e2.toString();
			s = "!";
		}

	}

	public void ok() {
		/**@todo ���� dash.EnhancedDialog abstract ���\�b�h������*/
		//private JTextField txtFieldNameServerAdr = new JTextField(40);
		//private JTextField txtFieldScriptsFolder = new JTextField(40);
		//private JComboBox cmbWpCnt = new JComboBox();

		// �f�t�H���g�t�@�C���̓ǂݍ���
		DashDefaults dashDefaults = new DashDefaults();
		Properties properties = dashDefaults.loadDefaults();

		String s = properties.getProperty("dash.userClassPath");
		if (properties.remove("dash.r.path111") == null) {
			int a = 1;
		}
		Object[] options = { "OK" };
		//---------------------------------------------------------------------------
		// �u�x�[�X�v���Z�X�Ɗg�����\�b�h��ǂݍ��ޏꏊ�v�̃`�F�b�N
		//---------------------------------------------------------------------------
		try {
			if (!txtFieldBaseProcessPlace.getText().equals("")) {
				StringTokenizer st =
					new StringTokenizer(
						txtFieldBaseProcessPlace.getText(),
						";");
				int cnt = 0;
				while (st.hasMoreTokens()) {
					String data = st.nextToken();

					File file = new File(data);
					if (!file.isDirectory()) {

						String cstmStr = mainframe.getBilingualMsg("0184");
						cstmStr = cstmStr.replaceAll("-", "\n");

						JOptionPane.showOptionDialog(
							this,
							cstmStr + "\n" + data,
							mainframe.getBilingualMsg("0129"),
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE,
							null,
							options,
							options[0]);
						return;

					}
				}
				properties.setProperty(
					"dash.userClassPath",
					txtFieldBaseProcessPlace.getText());
			} else {
				JOptionPane
					.showOptionDialog(
						this,
						mainframe.getBilingualMsg("0185"),
						mainframe.getBilingualMsg("0129"),
				//           "\"�x�[�X�v���Z�X�Ɗg�����\�b�h��ǂݍ��ޏꏊ\"�������͂ł��B","�G���[",
				JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE,
					null,
					options,
					options[0]);
				return;

			}
		} catch (Exception e) {
			e.getLocalizedMessage();

			//this--------------------------------------------------------------------------------------------------------------
			//this--------------------------------------------------------------------------------------------------------------
			//this--------------------------------------------------------------------------------------------------------------
			String cstmStr = mainframe.getBilingualMsg("0186");
			cstmStr = cstmStr.replaceAll("-", "\n");

			JOptionPane
				.showOptionDialog(
					this,
					cstmStr,
					mainframe.getBilingualMsg("0129"),
			//         "\"�x�[�X�v���Z�X�Ɗg�����\�b�h��ǂݍ��ޏꏊ\"���Q�Ƃ��邱�Ƃ��o���܂���B\n���͂Ɍ�肪�Ȃ����m�F���ĉ������B","�G���[",
			JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				options,
				options[0]);

			return;
		}
		//---------------------------------------------------------------------------
		// �u�l�[���T�[�o�̃z�X�g���܂���IP�A�h���X�v�̃`�F�b�N
		//---------------------------------------------------------------------------

		if (!txtFieldNameServerAdr.getText().equals("")) {
			properties.setProperty(
				"dash.nameserver",
				txtFieldNameServerAdr.getText());
		} else {
			if (properties.getProperty("dash.nameserver") != null) {
				properties.remove("dash.nameserver");
				System.getProperties().remove("dash.nameserver");
			}
		}
		//---------------------------------------------------------------------------
		// �u���|�W�g���ғ�PC�̃G�[�W�F���g�t�@�C���̂���ꏊ�v�̃`�F�b�N
		//---------------------------------------------------------------------------
		try {
			if (!txtFieldScriptsFolder.getText().equals("")) {
				File file = new File(txtFieldScriptsFolder.getText());
				if (!file.isDirectory()) {

					String cstmStr = mainframe.getBilingualMsg("0187");
					cstmStr = cstmStr.replaceAll("-", "\n");

					JOptionPane
						.showOptionDialog(
							this,
							cstmStr,
							mainframe.getBilingualMsg("0129"),
					//             "\"���|�W�g���ғ�PC�̃G�[�W�F���g�t�@�C���̂���ꏊ\"���Q�Ƃ��邱�Ƃ��o���܂���B\n���͂Ɍ�肪�Ȃ����m�F���ĉ������B","�G���[",
					JOptionPane.DEFAULT_OPTION,
						JOptionPane.ERROR_MESSAGE,
						null,
						options,
						options[0]);
					return;

				}
				properties.setProperty(
					"dash.r.path",
					txtFieldScriptsFolder.getText());
			} else {

				if (properties.remove("dash.r.path") != null) {
					properties.remove("dash.r.path");
					System.getProperties().remove("dash.r.path");
				}
			}

		} catch (Exception e) {
			e.getLocalizedMessage();

			String cstmStr = mainframe.getBilingualMsg("0187");
			cstmStr = cstmStr.replaceAll("-", "\n");

			JOptionPane
				.showOptionDialog(
					this,
					cstmStr,
					mainframe.getBilingualMsg("0129"),
			//         "\"���|�W�g���ғ�PC�̃G�[�W�F���g�t�@�C���̂���ꏊ\"���Q�Ƃ��邱�Ƃ��o���܂���B\n���͂Ɍ�肪�Ȃ����m�F���ĉ������B","�G���[",
			JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				options,
				options[0]);

			return;
		}

		// ���|�W�g���̖��O
		if (!txtFieldRepositoryName.getText().equals("")) {
			properties.setProperty(
				"dash.r.name",
				txtFieldRepositoryName.getText());
		} else {
			properties.setProperty("dash.r.name", "r1");
		}

		// ���[�N�v���[�X�̖��O
		String wpname = "";
		if (!txtFieldWorkplaceName1.getText().equals("")) {
			wpname = txtFieldWorkplaceName1.getText();
		} else {
			wpname = "w1";
		}
		if (!txtFieldWorkplaceName2.getText().equals("")) {
			wpname += "," + txtFieldWorkplaceName2.getText();
		} else {
			wpname = ",w2";
		}
		if (!txtFieldWorkplaceName3.getText().equals("")) {
			wpname += "," + txtFieldWorkplaceName3.getText();
		} else {
			wpname = ",w3";
		}
		if (!txtFieldWorkplaceName4.getText().equals("")) {
			wpname += "," + txtFieldWorkplaceName4.getText();
		} else {
			wpname = ",w4";
		}
		if (!txtFieldWorkplaceName5.getText().equals("")) {
			wpname += "," + txtFieldWorkplaceName5.getText();
		} else {
			wpname = ",w5";
		}

		properties.setProperty("dash.w.name", wpname);

		// TimeOut

		if (!txtFieldTimeout.getText().equals("")) {
			try {
				int i = new Integer(txtFieldTimeout.getText()).intValue();
			} catch (Exception e) {
				JOptionPane
					.showOptionDialog(
						this,
						mainframe.getBilingualMsg("0188"),
						mainframe.getBilingualMsg("0129"),
				//"\"nonstop���[�h�̑҂�����\"�ɂ͐��l�̂ݓ��͂��ĉ������B","�G���[",
				JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE,
					null,
					options,
					options[0]);
				return;
			}
			properties.setProperty(
				"dash.waittimeForNonstop",
				txtFieldTimeout.getText());
		} else {
			properties.setProperty("dash.waittimeForNonstop", "200");
		}

		if (System.getProperty("dash.userClassPath") != null) {
			System.getProperties().remove("dash.userClassPath");
		}
		if (System.getProperty("dash.r.path") != null) {
			System.getProperties().remove("dash.r.path");
		}
		if (System.getProperty("dash.nameserver") != null) {
			System.getProperties().remove("dash.nameserver");
		}
		if (System.getProperty("dash.wp.cnt") != null) {
			System.getProperties().remove("dash.wp.cnt");
		}
		if (System.getProperty("dash.r.name") != null) {
			System.getProperties().remove("dash.r.name");
		}
		if (System.getProperty("dash.w.name") != null) {
			System.getProperties().remove("dash.w.name");
		}
		if (System.getProperty("dash.waittimeForNonstop") != null) {
			System.getProperties().remove("dash.waittimeForNonstop");
		}
		if (System.getProperty("dash.r_w.pos") != null) {
			System.getProperties().remove("dash.r_w.pos");
		}

		//---------------------------------------------------------------------------
		// �u���[�N�v���[�X�̐��v�̃`�F�b�N
		//---------------------------------------------------------------------------
		properties.setProperty(
			"dash.wp.cnt",
			(String) cmbWpCnt.getSelectedItem());

		//---------------------------------------------------------------------------
		// ���|�W�g���ƃ��[�N�v���[�X�̈ʒu
		//---------------------------------------------------------------------------
		if (cbRepWpIndiPos.isSelected()) {
			properties.setProperty("dash.r_w.pos", "fixed");
		} else {
			properties.setProperty("dash.r_w.pos", "floatable");
		}
		//---------------------------------------------------------------------------
		// �r���[�A����E�Ȃ�
		//---------------------------------------------------------------------------
		if (rb1.isSelected()) {
			properties.setProperty("dash.viewer", "on");
		} else {
			properties.setProperty("dash.viewer", "off");
		}
		//---------------------------------------------------------------------------
		// ���O����E�Ȃ�
		//---------------------------------------------------------------------------
			  if (rb3.isSelected()) {
				  properties.setProperty("dash.log", "on");
			  } else {
				  properties.setProperty("dash.log", "off");
			  }
		//---------------------------------------------------------------------------
		// �C���X�y�N�^���O���E�B���h�E������E���Ȃ�
		//---------------------------------------------------------------------------
		if (rb5.isSelected()) {
			properties.setProperty("dash.inspector", "on");
		} else {
			properties.setProperty("dash.inspector", "off");
		}

		s = properties.getProperty("dash.userClassPath");
		try {
			FileOutputStream fos =
				new FileOutputStream("properties/simulator.properties");

			BufferedWriter awriter;
			awriter = new BufferedWriter(new OutputStreamWriter(fos, "8859_1"));

			properties.store(fos, "idea Properties");
			//properties.store( fos, "idea Properties" );
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("warning: simulator.properties not found!");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// �f�t�H���g�t�@�C���̓ǂݒ���

		//System.getProperties().clear();
		//dashDefaults = new DashDefaults();
		dashDefaults.loadDefaults();
		result = true;
		dispose();

	}
	public void cancel() {
		/**@todo ���� dash.EnhancedDialog abstract ���\�b�h������*/
		result = false;
		dispose();
	}

	public boolean getResult() {
		return result;
	}

}