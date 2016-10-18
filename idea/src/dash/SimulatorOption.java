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
 * <p>タイトル:シミュレータ設定ダイアログ </p>
 * <p>説明:動作シミュレートに関する、設定を行う </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名:cosmos </p>
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

	/** リポジトリ・ワークプレースの表示位置のチェック */
	private JCheckBox cbRepWpIndiPos = new JCheckBox();

	public SimulatorOption(IdeaMainFrame mainframe) {
		super(mainframe, "シミュレーター動作環境設定", true);
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
		// フレームサイズ
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
	* ビューアを利用するかどうか設定します。
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
				new JLabel("※" + mainframe.getBilingualMsg("0178")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);

		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0227")));
		return panel1;
	}
	
	/**
	* ログ保管機能を利用するかどうか設定します。
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
				new JLabel("※" + mainframe.getBilingualMsg("0178")));
		lbl.setForeground(Color.red);

		panel1.add(panel3, BorderLayout.NORTH);

		panel1.add(panel2, BorderLayout.CENTER);
		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0228")));
		return panel1;
	}

	/**
	* インスペクタを外部ウィンドウ化するかどうか設定します。
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
				new JLabel("※" + mainframe.getBilingualMsg("0178")));
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
				new JLabel("　※" + mainframe.getBilingualMsg("0178")));
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
				new JLabel("　※" + mainframe.getBilingualMsg("0170")));
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

		panel3.add(new JLabel("【" + mainframe.getBilingualMsg("0172") + "】"));
		panel3.add(
			new JLabel(
				"　"
					+ mainframe.getBilingualMsg("0173")
					+ "：leo.suga.net.it-chiba.ac.jp"));
		panel3.add(
			new JLabel(
				"　" + mainframe.getBilingualMsg("0174") + "：192.168.101.xxx"));
		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("　※" + mainframe.getBilingualMsg("0175")));
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

		//panel3.add (new JLabel("【設定例】"));
		//panel3.add (new JLabel("　Y\\ホスト名：leo.suga.net.it-chiba.ac.jp"));
		//panel3.add (new JLabel("　IPアドレス：192.168.101.xxx"));
		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("　※" + mainframe.getBilingualMsg("0175")));
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
				new JLabel("　※" + mainframe.getBilingualMsg("0178")));
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
					panel5.add(new JLabel(" WP1："), BorderLayout.WEST);
					panel5.add(txtFieldWorkplaceName1, BorderLayout.CENTER);
					panel4.add(panel5);
					break;
				case 2 :
					txtFieldWorkplaceName2.setText(data);
					panel6.add(new JLabel(" WP2："), BorderLayout.WEST);
					panel6.add(txtFieldWorkplaceName2, BorderLayout.CENTER);
					panel4.add(panel6);
					break;
				case 3 :
					txtFieldWorkplaceName3.setText(data);
					panel7.add(new JLabel(" WP3："), BorderLayout.WEST);
					panel7.add(txtFieldWorkplaceName3, BorderLayout.CENTER);
					panel4.add(panel7);
					break;
				case 4 :
					txtFieldWorkplaceName4.setText(data);
					panel8.add(new JLabel(" WP4："), BorderLayout.WEST);
					panel8.add(txtFieldWorkplaceName4, BorderLayout.CENTER);
					panel4.add(panel8);
					break;
				case 5 :
					txtFieldWorkplaceName5.setText(data);
					panel9.add(new JLabel(" WP5："), BorderLayout.WEST);
					panel9.add(txtFieldWorkplaceName5, BorderLayout.CENTER);
					panel4.add(panel9);
					break;
			}

		}

		panel2.add(panel4, BorderLayout.NORTH);

		JLabel lbl =
			(JLabel) panel3.add(
				new JLabel("　※" + mainframe.getBilingualMsg("0178")));
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
		//panel3.add(new JLabel("個"),BorderLayout.NORTH);

		//panel1.add(panel2, BorderLayout.WEST);
		//panel1.add(panel3, BorderLayout.CENTER);

		panel1.add(cmbWpCnt);
		panel1.add(new JLabel(mainframe.getBilingualMsg("0182")));

		panel1.setBorder(new TitledBorder(mainframe.getBilingualMsg("0183")));
		return panel1;
	}

	// イベント処理
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
		/**@todo この dash.EnhancedDialog abstract メソッドを実装*/
		//private JTextField txtFieldNameServerAdr = new JTextField(40);
		//private JTextField txtFieldScriptsFolder = new JTextField(40);
		//private JComboBox cmbWpCnt = new JComboBox();

		// デフォルトファイルの読み込み
		DashDefaults dashDefaults = new DashDefaults();
		Properties properties = dashDefaults.loadDefaults();

		String s = properties.getProperty("dash.userClassPath");
		if (properties.remove("dash.r.path111") == null) {
			int a = 1;
		}
		Object[] options = { "OK" };
		//---------------------------------------------------------------------------
		// 「ベースプロセスと拡張メソッドを読み込む場所」のチェック
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
				//           "\"ベースプロセスと拡張メソッドを読み込む場所\"が未入力です。","エラー",
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
			//         "\"ベースプロセスと拡張メソッドを読み込む場所\"を参照することが出来ません。\n入力に誤りがないか確認して下さい。","エラー",
			JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				options,
				options[0]);

			return;
		}
		//---------------------------------------------------------------------------
		// 「ネームサーバのホスト名またはIPアドレス」のチェック
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
		// 「リポジトリ稼動PCのエージェントファイルのある場所」のチェック
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
					//             "\"リポジトリ稼動PCのエージェントファイルのある場所\"を参照することが出来ません。\n入力に誤りがないか確認して下さい。","エラー",
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
			//         "\"リポジトリ稼動PCのエージェントファイルのある場所\"を参照することが出来ません。\n入力に誤りがないか確認して下さい。","エラー",
			JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				options,
				options[0]);

			return;
		}

		// リポジトリの名前
		if (!txtFieldRepositoryName.getText().equals("")) {
			properties.setProperty(
				"dash.r.name",
				txtFieldRepositoryName.getText());
		} else {
			properties.setProperty("dash.r.name", "r1");
		}

		// ワークプレースの名前
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
				//"\"nonstopモードの待ち時間\"には数値のみ入力して下さい。","エラー",
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
		// 「ワークプレースの数」のチェック
		//---------------------------------------------------------------------------
		properties.setProperty(
			"dash.wp.cnt",
			(String) cmbWpCnt.getSelectedItem());

		//---------------------------------------------------------------------------
		// リポジトリとワークプレースの位置
		//---------------------------------------------------------------------------
		if (cbRepWpIndiPos.isSelected()) {
			properties.setProperty("dash.r_w.pos", "fixed");
		} else {
			properties.setProperty("dash.r_w.pos", "floatable");
		}
		//---------------------------------------------------------------------------
		// ビューアあり・なし
		//---------------------------------------------------------------------------
		if (rb1.isSelected()) {
			properties.setProperty("dash.viewer", "on");
		} else {
			properties.setProperty("dash.viewer", "off");
		}
		//---------------------------------------------------------------------------
		// ログあり・なし
		//---------------------------------------------------------------------------
			  if (rb3.isSelected()) {
				  properties.setProperty("dash.log", "on");
			  } else {
				  properties.setProperty("dash.log", "off");
			  }
		//---------------------------------------------------------------------------
		// インスペクタを外部ウィンドウ化する・しない
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
		// デフォルトファイルの読み直し

		//System.getProperties().clear();
		//dashDefaults = new DashDefaults();
		dashDefaults.loadDefaults();
		result = true;
		dispose();

	}
	public void cancel() {
		/**@todo この dash.EnhancedDialog abstract メソッドを実装*/
		result = false;
		dispose();
	}

	public boolean getResult() {
		return result;
	}

}