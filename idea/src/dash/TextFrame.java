package dash;

import javax.swing.*;
import java.awt.*;

public class TextFrame extends JInternalFrame {
    private JTextArea textArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane();

    public TextFrame() {
        setSize(2000,3000);
        setTitle("Edit Text");
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        setResizable(true);
        setVisible (true);
        scrollPane.getViewport().add(textArea);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane,BorderLayout.CENTER);
    }
}