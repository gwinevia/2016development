package dash;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ps.EditMenuHandler;


/**
 * ACLエディタのパネル
 */
public class AclPanel extends JPanel {

  private JTextField perfField;
  private JTextField toField;
  private JTextArea contArea;
  private DVM dvm;

  JLabel label1 = null;
  JLabel label2 = null;
  JLabel label3 = null;

  public AclPanel(String[] options, EditMenuHandler emHandler, ActionListener listener, final DVM dvm) {
    super();
    this.dvm = dvm;
    //this.getParent().getParent();

    setLayout(new BorderLayout());

    GridBagLayout gbl = new GridBagLayout();
    JPanel basePanel = new JPanel();
    basePanel.setLayout(gbl);

    GridBagConstraints cons = new GridBagConstraints();
    cons.gridwidth = cons.gridheight = 1;

    perfField = new JTextField();
    perfField.addFocusListener(emHandler);
    label1 = new JLabel(":performative");
    label1.setToolTipText("過去に入力された「performative」を選択");
    label1.addMouseListener(new MouseHandler());
    addComp(label1, 0, 0, basePanel, gbl);
    addComp(perfField, 1, 0, basePanel, gbl);

    toField = new JTextField();
    toField.addFocusListener(emHandler);
    label2 = new JLabel(":to");
    label2.setToolTipText("過去に入力された「to」を選択");
    label2.addMouseListener(new MouseHandler());
    addComp(label2, 0, 1, basePanel, gbl);

    addComp(toField, 1, 1, basePanel, gbl);

    contArea = new JTextArea();
    contArea.setLineWrap(true);
    contArea.setWrapStyleWord(true);
    contArea.addFocusListener(emHandler);
    label3 = new JLabel(":content");
    label3.setToolTipText("過去に入力された「content」を選択");
    label3.addMouseListener(new MouseHandler());
    addComp(label3     , 0, 2, basePanel, gbl);
    addComp(new JScrollPane(contArea,
                            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
            1, 2, basePanel, gbl);

    JPanel buttonPanel = new JPanel();
    FlowLayout layout = new FlowLayout();
    layout.setAlignment(FlowLayout.LEFT);
    buttonPanel.setLayout(layout);
    for (int i=0; i<options.length; i++) {
      JButton button = new JButton(options[i]);
      button.addActionListener(listener);
      buttonPanel.add(button);
    }

    add(basePanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    MouseListener mouseListener = new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        JLabel l = (JLabel)e.getSource();
        l.setForeground(Color.blue);
        l.setCursor(new Cursor(Cursor.HAND_CURSOR));
        //l.setCursor(Cursor.HAND_CURSOR);
      }
      public void mouseExited(MouseEvent e) {
        JLabel l = (JLabel)e.getSource();
        l.setForeground(Color.black);
        l.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
      public void mouseClicked(MouseEvent e) {

        String kind = "";
        int kind_num =1;
        if (e.getSource() == label1 ) {
          kind = ":performative";
        }
        else if (e.getSource() == label2 ) {
          kind = ":to";
        }
        else if (e.getSource() == label3 ) {
          kind = ":content";
        }
        AclInputHistList dlg = new AclInputHistList(dvm.getParentFrame(), kind);
        //dlg.show();
		    dlg.setVisible(true);
        if (dlg.getResult() == 1 ) {
          if (e.getSource() == label1 ) {
            perfField.setText(dlg.getSelStr());
          }
          else if (e.getSource() == label2 ) {
            toField.setText(dlg.getSelStr());
          }
          else if (e.getSource() == label3 ) {
            contArea.setText(dlg.getSelStr());
          }
        }
      }
    };
    label1.addMouseListener(mouseListener);
    label2.addMouseListener(mouseListener);
    label3.addMouseListener(mouseListener);
  }

  private void addComp(Component comp, int gridx, int gridy, JPanel panel, GridBagLayout gbl) {
    GridBagConstraints cons = new GridBagConstraints();
    cons.gridwidth  = 1;
    cons.gridheight = 1;
    cons.gridx = gridx;
    cons.gridy = gridy;
    if (comp instanceof JLabel)
      cons.anchor = GridBagConstraints.WEST;
    else if (comp instanceof JTextField) {
      cons.fill = GridBagConstraints.HORIZONTAL;
      cons.weightx = 0.5;
    } else if (comp instanceof JScrollPane) {
      cons.fill = GridBagConstraints.BOTH;
      cons.weightx = cons.weighty = 0.5;
    }

    gbl.setConstraints(comp, cons);
    panel.add(comp);
  }

  public String getPerfField() {
    return perfField.getText();
  }

  public String getToField() {
    return toField.getText();
  }

  public String getContArea() {
    return contArea.getText();
  }

  public void setPerfField(String text) {
    perfField.setText(text);
  }

  public void setToField(String text) {
    toField.setText(text);
  }

  public void setContArea(String text) {
    contArea.setText(text);
  }

  class MouseHandler extends MouseAdapter{
    //マウスでクリックされた時の処理
    public void mouseEntered(MouseEvent e){
      if (System.getProperty("language").equals("japanese") ) {
        label1.setToolTipText("過去に入力された「performative」を選択");
        label2.setToolTipText("過去に入力された「to」を選択");
        label3.setToolTipText("過去に入力された「content」を選択");
      }
      else {
        label1.setToolTipText(":Performative inputted in the past is chosen.");
        label2.setToolTipText(":to inputted in the past is chosen.");
        label3.setToolTipText(":content inputted in the past is chosen.");
      }
    }
  }
}
