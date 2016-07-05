package dash;

import java.awt.*;
import javax.swing.JPanel;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.border.*;

/**
 * <p>NewChoice: </p>
 * <p>�V�K�ō쐬�������t�@�C����I��������: </p>
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

    super( mainframe, "�V�K�v���W�F�N�g", true );
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
    // �t���[���T�C�Y
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

		//�t�@�C����ޑI��
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

			//����
			newLabel[i] = new JLabel(nameAr[i]);
			newLabel[i].setOpaque(true);
			newLabel[i].setBackground(Color.white);
			newLabel[i].setForeground(Color.black);
			newLabel[i].setVerticalAlignment(JLabel.CENTER);
			newLabel[i].setHorizontalAlignment(JLabel.CENTER);

			//�C���[�W
	    dashiconLbl[i] = new JLabel();
	    ImageIcon dashicon = getImageIcon ( pathAr[i] );
			dashiconLbl[i].setIcon(dashicon);
			dashiconLbl[i].setVerticalAlignment(JLabel.CENTER);
			dashiconLbl[i].setHorizontalAlignment(JLabel.CENTER);

			//�v���W�F�N�g���쐬����Ă��Ȃ��ꍇ
			//Dash�t�@�C�� or ���[���Z�b�g
			if( i ==1 || i == 2 ){
				if( project == null ){
					//�v���W�F�N�g���쐬����Ă��Ȃ��ꍇ�́A�I��s��
					newLabel[i].setEnabled(false);
					dashiconLbl[i].setEnabled(false);
				}
				else{
					//�v���W�F�N�g���쐬����Ă���ꍇ�̓C�x���g����
					newLabel[i].addMouseListener(this);
					dashiconLbl[i].addMouseListener(this);

				}
			}
			else{
				//Dash�t�@�C�� or ���[���Z�b�g�ȊO�̏ꍇ
				newLabel[i].addMouseListener(this);
				dashiconLbl[i].addMouseListener(this);

			}

			//�����ƃC���[�W��g�ݍ��킹��
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

		//�X�N���[��
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(kindPanel);

    //OK�L�����Z���{�^��
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
		/* �}�E�X���N���b�N���ꂽ���̏��� */
    if (e.getClickCount() == 2 ) {
      ok();
    }
	}

	public void mouseEntered(MouseEvent e){
		/* �}�E�X���R���|�[�l���g�̏�ɏ�������̏��� */
	}

	public void mouseExited(MouseEvent e){
		/* �}�E�X���R���|�[�l���g�̊O�ɏo�����̏��� */
	}

	public void mousePressed(MouseEvent e){
		/* �}�E�X�������ꂽ���̏��� */
    JLabel lbl = (JLabel)e.getSource();
    String lblCaption = lbl.getText();
    if (lblCaption.equals("") ) {
      // �C���[�W�p�̃��x��
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
		/* �}�E�X�������ꂽ��A�����ꂽ���̏��� */
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
	* �C�x���g����
	* @param ActionEvent e <BR>
	*/
  public void actionPerformed(ActionEvent e){

    try{

      String action = e.getActionCommand();

      if (action.equals("ok")){
        ok();
      }
      else if(action.equals("close")){
        //�L�����Z���������ꂽ�ꍇ
        dispose();
      }

    }catch(Exception e2){
    }

  }

	/**
	* Enter�L�[����
	*/
  public void ok(){

		try {
      //�t�@�C���쐬����
      if( kindNo == 1 ){

				dispose();

        //�v���W�F�N�g
        NewPrjWin = new NewProject (this.mainframe,project);
        //NewPrjWin.show();
		    NewPrjWin.setVisible(true);
      }
      else if( kindNo == 2 || kindNo == 3 || kindNo == 4 || kindNo == 5){

				dispose();

				//Dash�t�@�C�� or ���[���Z�b�g or �x�[�X�v���Z�X or ���̑��t�@�C��
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
	* Esc�L�[����
	*/
  public void cancel(){
    dispose();
  }

	/**
	* ImageIcon����
	* @param String path �p�X<BR>
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