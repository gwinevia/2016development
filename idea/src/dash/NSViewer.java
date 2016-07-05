package dash;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.rmi.*;

/** �l�[���T�[�o���ώ@����r���[�A */
public class NSViewer extends JFrame implements ActionListener, ListSelectionListener {

  /** �G�[�W�F���g�̃��f�� */
  NSModel model;

  /** �G�[�W�F���g�̃e�[�u�� */
  JTable table;

  /** �l�[���T�[�o�̃G�[�W�F���g�f�[�^ */
  Vector agentData;

  /** ���̃��f�� */
  EnvModel envmodel;

  /** ���̃e�[�u�� */
  JTable envtable;

  /** �l�[���T�[�o�̊��f�[�^ */
  Vector envData;

  /** �l�[���T�[�o�ł��郊���[�g�I�u�W�F�N�g */
  NSInterface nameserver;
  String servername;

  /** �e�L�X�g */
  JTextArea textArea;

  /** �Ŋ��ɑI�����ꂽ�G�[�W�F���g�� */
  String lastName;

  /** unregisterEnv�{�^�� */
  JButton unregButton;

  /** �R���X�g���N�^ */
  public NSViewer(String servername) {
    super(servername);

    this.servername = servername;

    // �{�^���̃p�l��
    JButton button = new JButton("Update");
    button.addActionListener(this);
    unregButton = new JButton("UnregisterEnv");
    unregButton.addActionListener(this);
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(button);
    buttonPanel.add(unregButton);

    // �e�L�X�g�G���A
    textArea = new JTextArea();

    // �f�[�^�擾
    updateAgentData();

    // �G�[�W�F���g�̃e�[�u��
    model = new NSModel();
    table = new JTable(model);
    table.createDefaultColumnsFromModel();

    // ���̃e�[�u��
    envmodel = new EnvModel();
    envtable = new JTable(envmodel);
    table.createDefaultColumnsFromModel();

    // �R�����̕��ݒ�
    setColumnWidth();

    // �Z���N�g
    ListSelectionModel selectionModel = table.getSelectionModel();
    selectionModel.addListSelectionListener(this);

    // ����t��
    Container container = this.getContentPane();
    container.add(BorderLayout.NORTH, buttonPanel);
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    tabbedPane.addTab("agent", new JScrollPane(table));
    tabbedPane.addTab("environment", new JScrollPane(envtable));
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                                      //new JScrollPane(table),
                                      tabbedPane,
                                      new JScrollPane(textArea));
    split.setDividerLocation(200);
    container.add(split);

    addWindowListener(new WindowEventHandler());

    setSize(400, 400);
    //show();
    setVisible(true);
  }

  /**
   * �l�[���T�[�o�ɃG�[�W�F���g�Ɗ��̏���₢���킹��
   */
  private void updateAgentData() {
    try {
      nameserver=(NSInterface)Naming.lookup("rmi://"+servername+"/nameserver");
      agentData = nameserver.getAgentData();
      envData = nameserver.getEnvData();
      lastName = "";
      textArea.setText("");

      if (model != null && envmodel != null) {
        model.fireTableStructureChanged();
        envmodel.fireTableStructureChanged();
        setColumnWidth();
      }

    } catch (Exception e) {
      System.err.println("e "+e);
    }
  }

  private void setColumnWidth() {
    // �G�[�W�F���g�\��#
    DefaultTableColumnModel cmodel =
      (DefaultTableColumnModel)table.getColumnModel();
    TableColumn col = cmodel.getColumn(0);
    col.setMinWidth(40);
    col.setMaxWidth(40);

    // ���\��#�Ǝ��
    cmodel = (DefaultTableColumnModel)envtable.getColumnModel();
    col = cmodel.getColumn(0);
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col = cmodel.getColumn(1);
    col.setMinWidth(40);
    col.setMaxWidth(40);
  }

  public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    if (command.equals("Update")) {
      updateAgentData();
    } else if (command.equals("UnregisterEnv")) {
      int index = envtable.getSelectedRow();
      if (index>-1) {
        String[] data = (String[])envData.elementAt(index);
        String envname = data[0];
        int ans = JOptionPane.showConfirmDialog(this,
                                                envname+"���폜����H", "�m�F",
                                                JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
          try {
            nameserver.unregisterEnv(envname);
            updateAgentData();
          } catch (Exception e) { e.printStackTrace(); }
        }
      }
    }
  }

  public void valueChanged(ListSelectionEvent event) {
    int [] selectedRow = table.getSelectedRows();
    if (selectedRow.length == 1) {
      String name =
        (String)table.getValueAt(selectedRow[0], 1); // �G�[�W�F���g��
      if (!lastName.equals(name)) {
        lastName = name;
        String[] data = searchData(name);
        if (data != null)
          textArea.setText("name: "+data[0]+
                           "\nrname: "+data[1]+
                           "\nbirthday: "+data[2]+
                           "\nbirthplace: "+data[3]+
                           "\nenvironment: "+data[4]+
                           "\nfunction: "+data[5]+
                           "\ncomment: "+data[6]+
                           "\norigin: "+data[7]+
                           "\ntype: "+data[8]);
      }
    }
  }

  private String[] searchData(String name) {
    for (Enumeration e = agentData.elements(); e.hasMoreElements(); ) {
      String[] data = (String[])e.nextElement();
      if (data[0].equals(name))
        return data;
    }
    return null;
  }

  /** �E�B���h�E���鏈�� */
  class WindowEventHandler extends WindowAdapter {
    public void windowClosing(WindowEvent evt) {
      System.exit(0);
    }
  }


  /** �G�[�W�F���g�e�[�u�����f�� */
  class NSModel extends AbstractTableModel {

    /** �R���X�g���N�^ */
    public NSModel() {
    }

    /** �e�[�u���̍s�������o�� */
    public int getRowCount() {
      return agentData.size();
    }

    /** �e�[�u���̌��������o�� */
    public int getColumnCount() {
      return 4;
    }

    /** �w��̍s/���ʒu�̒l�����o�� */
    public Object getValueAt(int row, int column) {


      String[] data = (String[])agentData.elementAt(row);
      switch (column) {
      case 0:
        return new Integer(row);
      case 1:
        return data[0]; // ���S�ȃG�[�W�F���g��
      case 2:
        return data[4]; // ���݋������
      case 3:
        return data[5]; // �@�\��
      default:
        return "";
      }
    }

    /** �w�b�_ */
    public String getColumnName(int column) {
      switch (column) {
      case 0: return "#";
      case 1: return "���S�ȃG�[�W�F���g��";
      case 2: return "����";
      case 3: return "�@�\��";
      default: return "";
      }
    }
  }

  /** ���e�[�u�����f�� */
  class EnvModel extends AbstractTableModel {

    /** �R���X�g���N�^ */
    public EnvModel() {
    }

    /** �e�[�u���̍s�������o�� */
    public int getRowCount() {
      return envData.size();
    }

    /** �e�[�u���̌��������o�� */
    public int getColumnCount() {
      return 4;
    }

    /** �w��̍s/���ʒu�̒l�����o�� */
    public Object getValueAt(int row, int column) {
      String[] data = (String[])envData.elementAt(row);
      switch (column) {
      case 0:
        return new Integer(row);
      case 1:
        return data[1]; // ���
      case 2:
        return data[0]; // ����
      case 3:
        return data[2]; // �R�����g
      default:
        return "";
      }
    }

    /** �w�b�_ */
    public String getColumnName(int column) {
      switch (column) {
      case 0: return "#";
      case 1: return "���";
      case 2: return "����";
      case 3: return "�R�����g";
      default: return "";
      }
    }
  }

  /** ���C�� */
  public static void main(String args[]) {
    DashDefaults dashDefaults = new DashDefaults();
    dashDefaults.loadDefaults();

    String server = null;
    switch (args.length) {
    case 0:
      server = System.getProperty("dash.nameserver");
      break;
    case 1:
      server = args[0];
      break;
    default:
      break;
    }
    if (server == null) {
      System.err.println("�l�[���T�[�o���w�肳��Ă��܂���B"+
                         "defaults.txt��dash.nameserver���w�肷�邩�A"+
                         "�N�����̑������Ɏw�肷�邩���Ă��������B");
      System.exit(1);
    }
      
    new NSViewer(server);
  }
}
