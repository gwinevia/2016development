package dash;

import javax.swing.UIManager;
import java.awt.*;
import java.util.Enumeration;
import javax.swing.UIDefaults;

/**
 * <p>タイトル:IDEAメイン </p>
 * <p>説明:IDEA起動用のクラス </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class Idea {
  private boolean packFrame = false;

  //アプリケーションのビルド
  public Idea() {
    System.setProperty("DashMode", "off");
    IdeaMainFrame frame = new IdeaMainFrame();
    //validate() はサイズを調整する
    //pack() は有効なサイズ情報をレイアウトなどから取得する
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    //ウィンドウを中央に配置
    long start = System.currentTimeMillis();
    //
    // 計算時間を測定したいプログラム
    //

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
    frame.initialize();
    long finish = System.currentTimeMillis();
    //System.out.println("Processing Time = " + (finish - start));
  }
  //Main メソッド
  public static void main(String[] args) {
  	//System.out.println("debug: uchiya at 2005/12/15");
    try {
	 UIDefaults uiDefaults = UIManager.getDefaults();
     Enumeration enu = uiDefaults.keys();
     while (enu.hasMoreElements())
     {
       Object key = enu.nextElement();
       Object value = uiDefaults.get(key);

       if (key.toString().startsWith("FileChooser.")) {
         //System.out.println(key.toString());
       }
     }

      //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      //UIManager.setFont
      Font font = new Font("Dialog",Font.PLAIN,12);
      UIManager.put("Button.font", font);
      UIManager.put("ToggleButton.font", font);
      UIManager.put("RadioButton.font", font);
      UIManager.put("CheckBox.font", font);
      UIManager.put("ColorChooser.font", font);
      UIManager.put("ComboBox.font", font);
      UIManager.put("Label.font", font);
      UIManager.put("List.font", font);
      UIManager.put("MenuBar.font", font);
      UIManager.put("MenuItem.font", font);
      UIManager.put("MenuItem.acceleratorFont", font);
      UIManager.put("RadioButtonMenuItem.font", font);
      UIManager.put("RadioButtonMenuItem.acceleratorFont", font);
      UIManager.put("CheckBoxMenuItem.font", font);
      UIManager.put("CheckBoxMenuItem.acceleratorFont", font);
      UIManager.put("Menu.font", font);
      UIManager.put("Menu.acceleratorFont", font);
      UIManager.put("PopupMenu.font", font);
      UIManager.put("OptionPane.font", font);
      UIManager.put("Panel.font", font);
      UIManager.put("ProgressBar.font", font);
      UIManager.put("ScrollPane.font", font);
      UIManager.put("ViewPort.font", font);
      UIManager.put("TabbedPane.font", font);
      UIManager.put("Table.font", font);
      UIManager.put("TableHeader.font", font);
      UIManager.put("TextField.font", font);
      UIManager.put("PasswordField.font", font);
      UIManager.put("TextArea.font", font);
      UIManager.put("TextPane.font", font);
      UIManager.put("EditorPane.font", font);
      UIManager.put("TitledBorder.font", font);
      UIManager.put("ToolBar.font", font);
      UIManager.put("ToolTip.font", font);
      UIManager.put("Tree.font", font);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    //SplashScreen splashScreen = new SplashScreen();

    new Idea();
  }
}