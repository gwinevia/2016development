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

  /** コンストラクタ。アクション(loadBP)実行時に呼ばれる。 */
  public DammyBP(){
    super( "仮想ベースプロセス" );
  }

  /** アクション(loadBP)実行時に呼ばれる。*/
  public void setAgent(DashAgent agent){
    this.agent = agent;
  }

  /** アクション(startBP)実行時に呼ばれる。 */
  public void run(){
    setup();
  }

  /** エージェントの終了時に呼ばれる。 */
  public void finalizeBP(){
    dispose();
  }

  /** エージェントにイベントを上げる。 */
  public void actionPerformed( ActionEvent ae ){
    if(agent != null){
      agent.raiseEvent(textfield.getText());
    }
  }

  /** メソッドが呼び出されたときに呼ばれる。 */
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

    String return_value = JOptionPane.showInputDialog(this, args[0] + "(" + args_string + ") の返り値を入力して下さい。");
    textarea.append(args[0] + "(" + args_string + ") --> 返り値 " + return_value + "\n");
    return return_value;
  }
  
  /** ウィンドウを作る */
  private void setup(){

    setTitle("仮想ベースプロセス [" + agent.getAgentName() + "]");

//    JPanel panel = new JPanel(new GridLayout(1, 2));
    JPanel panel = new JPanel();
    textfield = new JTextField(30);
    textfield.setBorder( new TitledBorder( "入力イベント" ) );
    panel.add(textfield, BorderLayout.CENTER);
    button = new JButton( "エージェントに通知" );
    button.addActionListener( this );
    JPanel panel2 = new JPanel();
    panel2.add(button);
    panel.add(panel2, BorderLayout.EAST);

    textarea = new JTextArea( 12, 40 );
    JScrollPane sc = new JScrollPane( textarea );
    sc.setBorder( new TitledBorder( "ログ" ) );

    getContentPane().add( panel, BorderLayout.NORTH );
    getContentPane().add( sc,    BorderLayout.CENTER );
    
    pack();
    setVisible( true );
  }
  
  /** テスト用 */
  public static void main( String[] args ){
    DammyBP dbp = new DammyBP();
    dbp.setup();
  }
}
