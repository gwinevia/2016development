package dash;
   
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * <p>ACL�G�f�B�^�[���͒l�̗���\�� </p>
 * <p>
 * �ߋ��AACL�G�f�B�^�[�ɓ��͂��ꂽ�f�[�^���_�C�A���O�ňꗗ�\�����A���̒��̔C�ӂ̃f�[�^��I��<br>
 * ���邱�Ƃɂ��AACL�f�[�^�ւ̓��͍�Ƃ̏ȗ���ړI�Ƃ����N���X�ł��B<br>
 * �{�N���X�́AAclPanel.class���Ă΂�܂��B
 * </p>
 * @author cosmosweb nakagawa
 * 
 * @version 1.0
 * test by uchiya test
 */

public class AclInputHistList extends EnhancedDialog  implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** [OK]�������ꂽ���A�l�͂P�A[�L�����Z��]�������ꂽ���́A�l��0 */
  /** �{�N���X�Ăяo�����́AgetResult()���\�b�h��ʂ��āA���̒l�𓾂邱�Ƃ��o����*/
  private int ret = 0;

  /** �I�����ꂽ�f�[�^���i�[ */
  /** �{�N���X�Ăяo�����́AgetSelStr()���\�b�h��ʂ��āA���̒l�𓾂邱�Ƃ��o����*/
  private String selStr = "";

  /** �����f�[�^��\������JTable */
  private JTable table = null;

  /** �����f�[�^�\��JTable�̃w�b�_�[������ */
  private String[] hstr = {"�ȉ��̃��X�g����I�����ĉ������B"/*, "�@�\��", "�݌v��", "�݌v��", "�ް�ޮ�"*/};

  /** �e�[�u�����f�� */
  DefaultTableModel model;

  /** �{�N���X�̓_�C�A���O�ł��邽�߁A�e�t���[�����K�v�BIdeaMainFrame��e�t���[���Ƃ��Ă��� */
  private IdeaMainFrame mainframe = null;

  /****************************************************************************
   * �R���X�g���N�^
   * @param frame IdeaMainFrame���󂯎��
   * @param kind  �f�[�^��ނ�\�������� ":performative" or ":to" or ":content"
   * @return �Ȃ�
   ****************************************************************************/
  public AclInputHistList(IdeaMainFrame frame, String kind) {
    super(frame, kind, true);
    try {
    	mainframe = frame;
      jbInit(kind);
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public AclInputHistList() {
    this(null, "");
  }
  private void jbInit(String kind) throws Exception {

    // �ߋ��������擾
    ArrayList vecList = getACLHist(kind );

    // ���s���܂ޕ������JTable�ɕ\������ꍇ������A���̎��̍s�̍���
    int RowHeight = 0;

    // JTable�̃w�b�_�[��������擾
		hstr[0] = mainframe.getBilingualMsg("0157");

    // �t���[���T�C�Y
    setSize(450,450);

    // �\���ʒu�̒����B��ʒ����ɕ\������
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    //----------------------------------------------------------------
    // JTable�̍쐬
    //----------------------------------------------------------------
    // �e�[�u�����f�����쐬
    model = new ReadOnlyTableModel();
    table = new JTable(model);
    // �����s�̓����I����s�ɂ���
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // JTable�̍s�̍����̋K��l���擾
    RowHeight = table.getRowHeight();
    // Column�̃����_�����O��ݒ�
    table.setDefaultRenderer( Object.class, new MyCellRenderer() );
    // �����f�[�^���e�[�u���ɒǉ����܂�
    for (int i=0; i<vecList.size(); i++) {
      String[] val = {(String)vecList.get(i)};
      model.addRow(val);
    }

    if (vecList.size() == 0 ) {
      //�����f�[�^�����݂��Ȃ��ꍇ

      // �����f�[�^�����݂��Ȃ��ꍇ�́A���b�Z�[�W���擾
      String cstmStr = mainframe.getBilingualMsg("0158");
      cstmStr = cstmStr.replaceAll("title",kind);
      String[] val = {cstmStr/*"�ߋ��ɓ��͂��ꂽ�u" + title + "�v�͂���܂���B"*/};
      model.addRow(val);
    }

    // JTable���t���[���ɔz�u����
    panel1.setLayout(borderLayout1);
    getContentPane().add(panel1);
    JScrollPane spane = new JScrollPane (table);
    panel1.add(spane,BorderLayout.CENTER);

    // JTbale���i�[����Ă���AJScrollPane�Ƀ^�C�g���{�[�_�[��ݒ肷��
		String cstmStr2 = mainframe.getBilingualMsg("0159");
		cstmStr2 = cstmStr2.replaceAll("title",kind);
    spane.setBorder(new TitledBorder(cstmStr2/*"�ߋ��ɓ��͂��ꂽ�u" + title + "�v"*/));

    // [OK][�L�����Z��]�{�^�����t���[���ɒǉ�
    JButton okbtn = new JButton("OK");
    JButton cancelbtn = new JButton(mainframe.getBilingualMsg("0126"));
    JPanel btnpanel = new JPanel (new GridLayout(1,2));
    btnpanel.add(okbtn);
    btnpanel.add(cancelbtn);
    okbtn.setActionCommand("OK");
    cancelbtn.setActionCommand("CANCEL");
    okbtn.addActionListener(this);
    cancelbtn.addActionListener(this);
    JPanel panel2 = new JPanel (new BorderLayout());
    panel2.add(btnpanel,BorderLayout.EAST);
    panel1.add(panel2,BorderLayout.SOUTH);

    // JTable�̊e�s�̍����𒲐�
    // �i�f�[�^�Ɋ܂܂����s�̐��{�P�j�~�@�s�̍����̋K��l
    for (int i=0; i<vecList.size(); i++) {
      String s = (String)vecList.get(i);
      char[] c = s.toCharArray();
      int cnt = 0;
      for (int j=0; j<c.length; j++) {
        if (c[j] == '\n' ) {
          cnt ++;
        }
      }
      table.setRowHeight(i,RowHeight*(cnt+1));
    }


    // JTable�Ƀ}�E�X�C�x���g��ǉ�
    MouseListener mouseListener = new MouseAdapter() {
      /** Linux�p */
      public void mousePressed(MouseEvent e) {
      }

      /** Windows�p */
      public void mouseReleased(MouseEvent e) {
      }

      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int row = table.getSelectedRow();
          if (row == -1 ) {
            return;
          }
          ret = 1;
          selStr = (String)table.getValueAt(row,0);
          //hide();
          dispose();
        }
      }
    };
    table.addMouseListener(mouseListener);

  }

  /****************************************************************************
   * �Ǎ���p�e�[�u�����f��
   ****************************************************************************/
  class ReadOnlyTableModel extends DefaultTableModel {

    public int getColumnCount(){
      return 1;
    }

    public String getColumnName(int column){
      return hstr[column];
    }

    public boolean isCellEditable(int rowIndex,int colmunIndex) {
      return false;
    }
  }

  /****************************************************************************
   * �Z�������_�����O�N���X
   ****************************************************************************/
  class MyCellRenderer extends JTextArea implements TableCellRenderer {
     MyCellRenderer() {
         super();
         setEditable( false );
     }
     public Component getTableCellRendererComponent(
                             JTable table, Object data,
                             boolean isSelected, boolean hasFocus,
                             int row, int column) {
         setText( (String)data );


         if (isSelected ) {
           this.setForeground(table.getSelectionForeground());
           this.setBackground(table.getSelectionBackground());
         }
         else {
           this.setForeground(table.getForeground());
           this.setBackground(table.getBackground());
         }
        return this;
     }
 }

 /****************************************************************************
  * �C�x���g����
  * @param e�@�A�N�V�����C�x���g
  * @return �Ȃ�
  ****************************************************************************/
  public void actionPerformed(ActionEvent e) /*throws java.io.IOException, java.lang.Exception*/{
    try{
      String action = e.getActionCommand();

      if (action.equals("OK")){
        ok();
      }
      else if (action.equals("CANCEL")){
        cancel();
      }
    }
    catch(Exception e2){
			e2.printStackTrace();
    }
  }

  /****************************************************************************
   * ���ʎ擾
   * @param �Ȃ�
   * @return [OK]�N���b�N���͂P�A[�L�����Z��]�N���b�N���͂O
   ****************************************************************************/
  public int getResult() {
    return ret;
  }

  /****************************************************************************
   * �I�����ꂽ�f�[�^�擾
   * @param �Ȃ�
   * @return �I�����ꂽ�f�[�^
   ****************************************************************************/
  public String getSelStr() {
    return selStr;
  }

  /****************************************************************************
   * OK
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void ok() {

    int row = table.getSelectedRow();
    if (row != -1 ) {
      selStr = (String)table.getValueAt(row,0);
      ret = 1;
      //hide();
      dispose();
    }
    else {
      //JOptionPane.showMessageDialog(this,"�I������Ă��܂���B");
      Object[] options = { "OK" };
      JOptionPane.showOptionDialog(null,
          mainframe.getBilingualMsg("0075"),mainframe.getBilingualMsg("0129"),
                                 JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                 null, options, options[0]);
    }

  }

  /****************************************************************************
   * �L�����Z��
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void cancel() {
    //�L�����Z���������ꂽ�ꍇ
    ret = 0;
    //hide();
    dispose();
  }

  /****************************************************************************
   * �ߋ������f�[�^�̎擾
   * @param kind  �f�[�^��ނ�\�������� ":performative" or ":to" or ":content"
   * @return �ߋ��������i�[����Vector
   ****************************************************************************/
  private ArrayList getACLHist(String kind) {
    // �f�t�H���g�t�@�C���̓ǂݍ���
    DashDefaults dashDefaults = new DashDefaults();
    dashDefaults.loadDefaults();
    //File msgfile = dashDefaults.getMessageFile();
    File dashdir = dashDefaults.getDashdir();

    //String dirpath = "";
    StringBuffer dirpath= new StringBuffer();
    if (!dashdir.toString().endsWith(File.separator)) {
        //dirpath = dashdir.toString() + File.separator;
        dirpath.append(dashdir.toString());
        dirpath.append(File.separator);
    }
    
    //String FilePath = dirpath;
	    StringBuffer filePath = dirpath;
    int kind_num = 0;
    if (kind.equals(":performative")) {
      kind_num = 1;
    }
    else if (kind.equals(":to")) {
      kind_num = 2;
    }
    else if (kind.equals(":content")) {
      kind_num = 3;
    }

    if (kind_num == 1 ) {
      //FilePath += "properties" + File.separator +"perfomativeHist";
	    filePath.append("properties");
	    filePath.append(File.separator);
	    filePath.append("perfomativeHist");
    }
    else if (kind_num == 2 ) {
      //FilePath += "properties" + File.separator +"toHist";
	  filePath.append("properties");
	  filePath.append(File.separator);
	  filePath.append("toHist");

    }
    else if (kind_num == 3 ) {
      //filePath += "properties" + File.separator +"contentHist";
	      filePath.append("properties");
	      filePath.append(File.separator);
	      filePath.append("contentHist");
		  
    }
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    //�ǂݍ��ݏ���
    //	vector --> arraylist 05/02/17
    ArrayList vecList = new ArrayList();
    String content = "";
    try {
        //f_in = new FileReader(FilePath);
        //b_in = new BufferedReader(f_in);
        b_in = new BufferedReader(new InputStreamReader(
                                                new FileInputStream(filePath.toString()),
                                                "JISAutoDetect"));

        while((sLine = b_in.readLine()) != null) {
          if (kind_num != 3 ) {
            if (vecList.size()<30 ) {
              vecList.add(sLine);
            }
          }
          else {
            if (sLine.equals("----------")) {
              vecList.add(content);
              content = "";
            }
            else {
              if (content.equals("") ) {
                content += sLine;
              }
              else {
                content += '\n' + sLine;
              }
            }
          }
        }
        if (kind_num == 3 ) {
          if (!content.equals("")) {
            vecList.add(content);
          }

        }
        b_in.close();
        //f_in.close();

    } catch(Exception ex) {
    	ex.printStackTrace();
    }

    return vecList;
  }


}