package dash;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

/**
 * <p>�^�C�g��:�G�[�W�F���g�t�@�C���I���_�C�A���O </p>
 * <p>����:����V�~�����[�g�ŁA���|�W�g���A���[�N�v���[�X�ɓǂݍ��ރG�[�W�F���g�̑I�����s�� </p>
 * <p>���쌠: Copyright (c) 2003</p>
 * <p>��Ж�:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class SelectAgentFileDialog extends EnhancedDialog implements ActionListener {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  /** ���g�p */
  private JCheckBox clearCheckBox = null;

  /** �`�F�b�N�{�b�N�X�ƃG�[�W�F���g�t�@�C�����̑g�ݍ��킹��z�u����p�l�� */
  private JPanel agentList = null;

  /** �`�F�b�N�{�b�N�X���i�[����x�N�^�[ */
  Vector checkboxes = new Vector();

  /** ���g�p */
  Vector comboboxes = new Vector();
  //GeneratedListModel listModel;

  /** �{�^�� */
  JButton okbtn = new JButton("OK");
  JButton cancelbtn = null;
  JButton selectAllBtn = null;
  JButton selectNoneBtn = null;
  private static Dimension HGAP10 = new Dimension(10,1);
  private Project project;
  public int ret = 0;// 1:OK 0:Cancel

  /** �I�����ꂽ�t�@�C�� */
  private Vector vecSelectFile = new Vector();

  /** �ȑO�A�{�_�C�A���O�����s���ꂽ���ɑI�����ꂽ�t�@�C�� */
  private Vector prevSelectFiles  = null;

  /** ���g�p */
  private Vector prevReadCount  = null;

  /** ���g�p */
  private Vector vecReadCount = new Vector();

  /** �e�t���[�� */
  private IdeaMainFrame mainframe = null;

  /****************************************************************************
   * �R���X�g���N�^
   * @param frame IdeaMainFrame���󂯎��
   * @param title �^�C�g��
   * @param modal ���[�_���\���̏ꍇtrue�A�����łȂ���false
   * @param project �v���W�F�N�g
   * @param prevSelectFiles �ȑO�A�{�_�C�A���O�����s���ꂽ���ɑI�����ꂽ�t�@�C��
   * @param prevReadCount ���g�p�E�E�Enull��n���č\��Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public SelectAgentFileDialog(IdeaMainFrame frame, String title, boolean modal, Project project,Vector prevSelectFiles, Vector prevReadCount) {
    super(frame, title, modal);
    this.project = project;
    this.prevSelectFiles = prevSelectFiles;
    this.prevReadCount = prevReadCount;
    try {
    	mainframe = frame;
      jbInit();
      //pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public SelectAgentFileDialog() {
    this(null, "", false, null,null,null);
  }

  /****************************************************************************
   * ��������
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  private void jbInit() throws Exception {

	  cancelbtn = new JButton(mainframe.getBilingualMsg("0126"));

    // �t���[���T�C�Y
    setSize(300,350);

    // �_�C�A���O����ʒ����ɔz�u
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    //this.setSize(new Dimension(400, 300));
    panel1.setLayout(borderLayout1);

    JPanel msgPanel = new JPanel();
    JLabel msgLabel2 = new JLabel (mainframe.getBilingualMsg("0169"));
    msgPanel.add(msgLabel2);
    msgPanel.setBackground(new Color(255,255,217));
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(msgPanel,BorderLayout.NORTH);

    // �`�F�b�N�{�b�N�X�ƃ��x���̑g�ݍ��킹��z�u����p�l�����쐬���A�_�C�A���O�ɔz�u����
    getContentPane().add(createControlPanel(),BorderLayout.CENTER);
    //getContentPane().add(new JCheckBox("�N���O�Ƀ��|�W�g����Ag����������"),BorderLayout.SOUTH);


		// ���X�g�\������t�@�C�����x�N�^�[�Ɋi�[
		Vector vecFileName = new Vector();
    for (int i=0; i<project.getFileCount(); i++ ) {
      if (!((String)project.getFileName(i)).toLowerCase().endsWith(".dash") ) {
        continue;
      }
      vecFileName.addElement((String)project.getFileName(i));
    }
    
    // �x�N�^�[�ɓ��ꂽ�t�@�C�������\�[�g����
    for(int sfrom=0; sfrom<vecFileName.size()-1; sfrom++) {
      int max = sfrom;
      for(int i=sfrom+1; i<vecFileName.size(); i++) {
          if(((String)vecFileName.elementAt(i)).compareTo((String)vecFileName.elementAt(max))<0) max = i;
      }
      String temp = (String)vecFileName.elementAt(sfrom);
      vecFileName.setElementAt((String)vecFileName.elementAt(max),sfrom);
      vecFileName.setElementAt(temp,max);
     }    

    // �t�@�C�������A�`�F�b�N�{�b�N�X�Ƃ̑g�ݍ��킹�Ńp�l���ɒǉ�����
    for (int i=0; i<vecFileName.size(); i++ ) {
      boolean chkflag = false;
      String ReadCount = null;
      if (prevReadCount != null ) {
        ReadCount = "1";
      }
      
      String FileName = (String)vecFileName.elementAt(i);
      //if (((String)project.getFileName(i)).toLowerCase().endsWith(".rset") ) {
      //  continue;
      //}
      if (!FileName.toLowerCase().endsWith(".dash") ) {
        continue;
      }
      if (prevSelectFiles.indexOf(FileName) != -1 ) {
        chkflag = true;
        if (prevReadCount != null ) {
          ReadCount = (String)prevReadCount.elementAt(i);
        }
      }
      addPrefix (FileName,chkflag,ReadCount);
    }
    /*
    for (int i=0; i<project.getFileCount(); i++ ) {
      boolean chkflag = false;
      String ReadCount = null;
      if (prevReadCount != null ) {
        ReadCount = "1";
      }
      //if (((String)project.getFileName(i)).toLowerCase().endsWith(".rset") ) {
      //  continue;
      //}
      if (!((String)project.getFileName(i)).toLowerCase().endsWith(".dash") ) {
        continue;
      }
      if (prevSelectFiles.indexOf((String)project.getFileName(i)) != -1 ) {
        chkflag = true;
        if (prevReadCount != null ) {
          ReadCount = (String)prevReadCount.elementAt(i);
        }
      }
      addPrefix (project.getFileName(i),chkflag,ReadCount);
    }
    */

    JPanel btnpanel = new JPanel (new GridLayout(1,2));
    btnpanel.add(okbtn);
    btnpanel.add(cancelbtn);

    okbtn.setActionCommand("OK");
    cancelbtn.setActionCommand("CANCEL");
    okbtn.addActionListener(this);
    cancelbtn.addActionListener(this);

    JPanel btnpanel2 = new JPanel (new BorderLayout());
    btnpanel2.add(btnpanel,BorderLayout.EAST);
    getContentPane().add(btnpanel2,BorderLayout.SOUTH);

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
    if (prevReadCount == null ) {
      clearCheckBox = new JCheckBox("�N���O�Ƀ��|�W�g����Ag����������");
    }
    else {
      clearCheckBox = new JCheckBox("�N���O�Ƀ��[�N�v���[�X��Ag����������");
    }
    //controlPanel.add(clearCheckBox, BorderLayout.SOUTH);

    JPanel selPanel = new JPanel();
    selectAllBtn = new JButton(mainframe.getBilingualMsg("0225"));
    selectNoneBtn = new JButton(mainframe.getBilingualMsg("0226"));

    selectAllBtn.setActionCommand("selectAll");
    selectNoneBtn.setActionCommand("selectNone");

    selectAllBtn.addActionListener(this);
    selectNoneBtn.addActionListener(this);

    selPanel.add(selectAllBtn);
    selPanel.add(selectNoneBtn);
    controlPanel.add(selPanel, BorderLayout.NORTH);
    return controlPanel;
  }

  /****************************************************************************
   * �`�F�b�N�{�b�N�X�ƃ��x���̑g�ݍ��킹���쐬���AagentList�ɔz�u����
   * @param prefix ���x���ɕ\�����镶����
   * @param selected �`�F�b�N�{�b�N�X��I����Ԃɂ���ꍇ��true�A�����łȂ��Ƃ���false
   * @return �Ȃ�
   ****************************************************************************/
  public void addPrefix(String prefix, boolean selected, String ReadCount) {
    //if(prefixAction == null) {
    //  prefixAction = new UpdatePrefixListAction(listModel);
    //}

    JPanel wkpanel1 = new JPanel (new BorderLayout());
    JPanel wkpanel2 = new JPanel (new BorderLayout());
    Vector vecCount = new Vector();
    for (int i=1; i<=10; i++ ) {
      vecCount.addElement(new Integer(i).toString());
    }
    JCheckBox cb = null;
    if (ReadCount != null ) {
      JLabel lbl = new JLabel("��  ");
      lbl.setBackground(Color.white);
      wkpanel2.add(lbl, BorderLayout.EAST);
      JComboBox cmb = (JComboBox)wkpanel2.add(new JComboBox(vecCount));
      wkpanel2.setBackground(Color.white);
      cb = (JCheckBox) wkpanel1.add(new JCheckBox(prefix));

      wkpanel1.add(wkpanel2,BorderLayout.EAST);
      cmb.setSelectedItem(ReadCount);
      agentList.add(wkpanel1);
      comboboxes.addElement(cmb);
    }
    else {
      cb = (JCheckBox) agentList.add(new JCheckBox(prefix));
    }
    checkboxes.addElement(cb);
    cb.setSelected(selected);
    cb.setBackground(Color.white);


    //agentList.add(new JComboBox());
    //agentList.add(new Label("AAA"));
    //cb.addActionListener(prefixAction);
    //if(selected) {
    //  listModel.addPrefix(prefix);
    //}
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
      else if (action.equals("selectAll")){
        for (int i=0; i<checkboxes.size(); i++ ) {
          JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);
          bc.setSelected(true);
        }
      }
      else if (action.equals("selectNone")){
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
   * ���ʎ擾
   * @param �Ȃ�
   * @return OK�N���b�N���Ftrue�@�L�����Z���N���b�N���Ffalse
   ****************************************************************************/
  public int getResult() {
    return ret;
  }

  /****************************************************************************
   * �I�����ꂽ�t�@�C�����擾
   * @param �Ȃ�
   * @return �I�����ꂽ�t�@�C��
   ****************************************************************************/
  public Vector getSelectFiles() {
    return vecSelectFile;
  }

  /****************************************************************************
   * ���g�p
   ****************************************************************************/
  public Vector getReadCount() {
    return vecReadCount;
  }

  /****************************************************************************
   * OK����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void ok() {
    vecSelectFile.clear();
    vecReadCount.clear();
    for (int i=0; i<checkboxes.size(); i++ ) {
      JCheckBox bc = (JCheckBox)checkboxes.elementAt(i);

      if (bc.isSelected() ) {
        //String s = (String)bc.getText();
        vecSelectFile.addElement((String)bc.getText());
        if (prevReadCount != null ) {
          JComboBox cmb = (JComboBox)comboboxes.elementAt(i);
          vecReadCount.addElement((String)cmb.getSelectedItem());
        }
      }
    }

    if (vecSelectFile.size() == 0 ) {

			Object[] options = { "OK" };
			JOptionPane.showOptionDialog(this,
										mainframe.getBilingualMsg("0075"),mainframe.getBilingualMsg("0129"),
										JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
										null, options, options[0]);

//      JOptionPane.showMessageDialog(this,mainframe.getBilingualMsg("0075"));
      return;
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
  public void cancel(){
    //�L�����Z���������ꂽ�ꍇ
    ret = 0;
    //hide();
    dispose();

  }

}