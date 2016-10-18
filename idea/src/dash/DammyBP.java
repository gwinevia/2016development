package dash;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class DammyBP extends JFrame implements DashBP, ActionListener {
  
  private DashAgent agent;
  
  JTextField textfield;
  JTextArea  textarea;
  JButton button;
  JPanel panel;

  /** �R���X�g���N�^�B�A�N�V����(loadBP)���s���ɌĂ΂��B */
  public DammyBP(){
    super( "���z�x�[�X�v���Z�X" );
  }

  /** �A�N�V����(loadBP)���s���ɌĂ΂��B*/
  public void setAgent(DashAgent agent){
    this.agent = agent;
  }

  /** �A�N�V����(startBP)���s���ɌĂ΂��B */
  public void run(){
    setup();
  }

  /** �G�[�W�F���g�̏I�����ɌĂ΂��B */
  public void finalizeBP(){
    dispose();
  }

  /** �G�[�W�F���g�ɃC�x���g���グ��B */
  public void actionPerformed( ActionEvent ae ){
    if(agent != null){
      agent.raiseEvent(textfield.getText());
    }
  }

  /** ���\�b�h���Ăяo���ꂽ�Ƃ��ɌĂ΂��B */
  public String encourage(Object[] args){
    StringBuffer buffer = new StringBuffer("");
    for (int i=1; i<args.length; i++) {
      buffer.append(args[i] + ", ");
    }
    String args_string;
    if (args.length > 1) {
      args_string = buffer.substring(0, buffer.length()-2);
    }
    else {
      args_string = "";
    }

    String return_value = JOptionPane.showInputDialog(this, args[0] + "(" + args_string + ") �̕Ԃ�l����͂��ĉ������B");
    textarea.append(args[0] + "(" + args_string + ") --> �Ԃ�l " + return_value + "\n");
    return return_value;
  }
  
  /** �E�B���h�E����� */
  private void setup(){

    setTitle("���z�x�[�X�v���Z�X [" + agent.getAgentName() + "]");

//    JPanel panel = new JPanel(new GridLayout(1, 2));
    JPanel panel = new JPanel();
    textfield = new JTextField(30);
    textfield.setBorder( new TitledBorder( "���̓C�x���g" ) );
    panel.add(textfield, BorderLayout.CENTER);
    button = new JButton( "�G�[�W�F���g�ɒʒm" );
    button.addActionListener( this );
    JPanel panel2 = new JPanel();
    panel2.add(button);
    panel.add(panel2, BorderLayout.EAST);

    textarea = new JTextArea( 12, 40 );
    JScrollPane sc = new JScrollPane( textarea );
    sc.setBorder( new TitledBorder( "���O" ) );

    getContentPane().add( panel, BorderLayout.NORTH );
    getContentPane().add( sc,    BorderLayout.CENTER );
    
    pack();
    setVisible( true );
  }
  
  /** �e�X�g�p */
  public static void main( String[] args ){
    DammyBP dbp = new DammyBP();
    dbp.setup();
  }
}
