package dash;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

/**
 * <p>�^�C�g��:�t�@�C���ۑ��m�F�_�C�A���O </p>
 * <p>����:IDEA�I�����ɁA���ۑ��̃t�@�C�����������ꍇ�A�\������� </p>
 * <p>���쌠: Copyright (c) 2003</p>
 * <p>��Ж�:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class SaveComfirmDialog extends EnhancedDialog implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** �ύX�̂������t�@�C�� */
  private Vector vecChgFileList = null;

  /** �ۑ�����t�@�C�� */
  private Vector vecSelectFile = new Vector();

  /** �ۑ��Ώۂ̃t�@�C����I������ۂ̃`�F�b�N�{�b�N�X */
  private JCheckBox clearCheckBox = null;

  /** �ύX�t�@�C����\������p�l�� */
  private JPanel agentList = null;

  /** �ۑ��Ώۂ̃t�@�C����I������ۂ̃`�F�b�N�{�b�N�X���i�[����x�N�^�[ */
  Vector checkboxes = new Vector();

  /** �{�^�� */
  JButton okbtn = new JButton("OK");
  JButton cancelbtn = null;
  JButton AllSelectbtn = null;
  JButton AllResetbtn = null;

  private static Dimension HGAP10 = new Dimension(10,1);
  public int ret = 0;// 1:OK 0:Cancel

  /** �e�t���[��*/
  private IdeaMainFrame mainframe = null;

  /****************************************************************************
   * �R���X�g���N�^
   * @param frame IdeaMainFrame���󂯎��
   * @param vecChgFileList  �ύX�̂������t�@�C��
   * @return �Ȃ�
   ****************************************************************************/
  public SaveComfirmDialog(IdeaMainFrame frame, Vector vecChgFileList) {
    super(frame, "�t�@�C���ۑ�", true);
    try {
      this.vecChgFileList = vecChgFileList;
      mainframe = frame;
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public SaveComfirmDialog() {
    this(null, null);
  }


  /****************************************************************************
   * ��������
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void jbInit() throws Exception {

    // �_�C�A���O�^�C�g���ݒ�
    setTitle(mainframe.getBilingualMsg("0166"));

    // �{�^���̕�����ݒ�
	  cancelbtn    = new JButton(mainframe.getBilingualMsg("0126"));
  	AllSelectbtn = new JButton(mainframe.getBilingualMsg("0015"));
  	AllResetbtn  = new JButton(mainframe.getBilingualMsg("0168"));

    // �t���[���T�C�Y
    setSize(400,350);

    // �{�_�C�A���O����ʒ����ɗ���悤�Ɉʒu����
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    panel1.setLayout(borderLayout1);

    JPanel msgPanel = new JPanel();
    JLabel msgLabel2 = new JLabel (mainframe.getBilingualMsg("0167"));
    msgPanel.add(msgLabel2);
    msgPanel.setBackground(new Color(255,255,217));
    getContentPane().setLayout(new BorderLayout());

    // �`�F�b�N�{�b�N�X�ƃ��x���̑g�ݍ��킹��z�u����p�l�����쐬���A�_�C�A���O�ɔz�u����
    getContentPane().add(createControlPanel(),BorderLayout.CENTER);
    //getContentPane().add(new JCheckBox("�N���O�Ƀ��|�W�g����Ag����������"),BorderLayout.SOUTH);

    // �ύX�t�@�C�����A�`�F�b�N�{�b�N�X�Ƃ̑g�ݍ��킹�Ńp�l���ɒǉ�����
    for (int i=0; i<vecChgFileList.size(); i++ ) {
      boolean chkflag = true;
      addPrefix ((String)vecChgFileList.elementAt(i),chkflag);
    }

    JPanel btnpanel1 = new JPanel (new GridLayout(1,2));
    btnpanel1.add(okbtn);
    btnpanel1.add(cancelbtn);

    JPanel btnpanel2 = new JPanel (new GridLayout(1,2));
    btnpanel2.add(AllSelectbtn);
    btnpanel2.add(AllResetbtn);

    // �{�^����z�u
    okbtn.setActionCommand("OK");
    cancelbtn.setActionCommand("CANCEL");
    AllSelectbtn.setActionCommand("ALLSELECT");
    AllResetbtn.setActionCommand("ALLRESET");

    okbtn.addActionListener(this);
    cancelbtn.addActionListener(this);
    AllSelectbtn.addActionListener(this);
    AllResetbtn.addActionListener(this);

    JPanel btnpanel3 = new JPanel (new BorderLayout());
    JPanel dammypanel = new JPanel ();
    btnpanel3.add(btnpanel1,BorderLayout.EAST);
    btnpanel3.add(btnpanel2,BorderLayout.WEST);
    btnpanel3.add(dammypanel,BorderLayout.CENTER);
    getContentPane().add(btnpanel3,BorderLayout.SOUTH);
  }

  /****************************************************************************
   * �`�F�b�N�{�b�N�X�ƃ��x���̑g�ݍ��킹��z�u����p�l�����쐬
   * @param �Ȃ�
   * @return �`�F�b�N�{�b�N�X�ƃ��x���̑g�ݍ��킹��z�u����p�l��
   ****************************************************************************/
  public JPanel createControlPanel() {
    JPanel controlPanel = new JPanel() {
      Insets insets = new Insets(4, 4, 4, 4);
      public Insets getInsets() {
        return insets;
      }
    };
    controlPanel.setLayout(new BorderLayout());

    JPanel agentPanel = new JPanel();
    agentPanel.setLayout(new BoxLayout(agentPanel, BoxLayout.Y_AXIS));

    agentList = new JPanel() {
        Insets insets = new Insets(0, 4, 0, 0);
        public Insets getInsets() {
          return insets;
        }
    };
    agentList.setLayout(new BoxLayout(agentList, BoxLayout.Y_AXIS));
    //agentList.setLayout(new GridLayout(project.getFileCount(),2) );
    agentList.setBackground(Color.white);

    JScrollPane scrollPane = new JScrollPane(agentList);
    scrollPane.getVerticalScrollBar().setUnitIncrement(10);
    agentPanel.add(scrollPane);
    controlPanel.add(agentPanel, BorderLayout.CENTER);
    return controlPanel;
  }

  /****************************************************************************
   * �`�F�b�N�{�b�N�X�ƃ��x���̑g�ݍ��킹���쐬���AagentList�ɔz�u����
   * @param prefix ���x���ɕ\�����镶����
   * @param selected �`�F�b�N�{�b�N�X��I����Ԃɂ���ꍇ��true�A�����łȂ��Ƃ���false
   * @return �Ȃ�
   ****************************************************************************/
  public void addPrefix(String prefix, boolean selected) {

    JPanel wkpanel1 = new JPanel (new BorderLayout());
    JPanel wkpanel2 = new JPanel (new BorderLayout());
    JCheckBox cb = null;
    cb = (JCheckBox) agentList.add(new JCheckBox(prefix));
    checkboxes.addElement(cb);
    cb.setSelected(selected);
    cb.setBackground(Color.white);


  }

  /****************************************************************************
   * �C�x���g����
   * @param e �A�N�V�����C�x���g
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
      else if (action.equals("ALLSELECT")){
        for (int i=0; i<checkboxes.size(); i++ ) {
          JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);
          bc.setSelected(true);
        }
      }
      else if (action.equals("ALLRESET")){
        for (int i=0; i<checkboxes.size(); i++ ) {
          JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);
          bc.setSelected(false);
        }
      }
    }
    catch(Exception e2){

    }

  }

  /****************************************************************************
   * OK����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void ok() {
    vecSelectFile.clear();
   for (int i=0; i<checkboxes.size(); i++ ) {
     JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);

     if (bc.isSelected() ) {
       vecSelectFile.addElement((String)bc.getText());
     }
   }

   ret = 1;
   //hide();
   dispose();

  }

  /****************************************************************************
   * Cancel����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void cancel() {
    ret = 0;
    //hide();
    dispose();

  }

  /****************************************************************************
   * �ۑ��ΏۂƂ��đI�����ꂽ�t�@�C����Ԃ�
   * @param �Ȃ�
   * @return �ۑ��ΏۂƂ��đI�����ꂽ�t�@�C��
   ****************************************************************************/
  public Vector getSelectFiles() {
    return vecSelectFile;
  }

}