package dash;
import javax.swing.*;

import java.io.File;
import java.util.*;

/**
 * 環境モニタのインタフェース
 */
public interface NewifItface {

  /** 開く */
  public void show();

  /** メモリの監視を開始する */
  public void startMemoryWatch();

  /** settext */
  public void settext(String agent, String s);

  /**
   * メッセージの処理を行う。
   */
  public void showMsg(DashMessage m);

  /** 受信したメッセージをreceiveタブに表示する。*/
  public void putMsg(DashMessage msg);

  /** nonStopCheckを押した状態(nonstop状態)にする */
  public void setNonstop();

  /**
   * エージェントを環境に追加する。次の場合に呼ばれる。
   * 1)リポジトリエージェントがファイルから生成されたとき
   * 2)インスタンスエージェントが生成されたとき
   * @param name エージェント名
   */
  public void addAgent(String name);
  public void addAgent(String name, String origin);

  /** エージェントを消す */
  public void removeAgent(String name);

  /** 本当に消されたのを待つ。*/
  public void confirmSync();

  /**
   * ログをログタブに表示する。
   * @param s ログ
   */
  public void println(String s);

  /**
   * エラーをエラータブに表示する。
   * @param s エラーの説明
   */
  public void printlnE(String s);

  // ADD COSMOS
  public void addChildWindow (JInternalFrame jiFrame );
  public void initialize();

  public AclPanel getAclPanel();
  public JPanel getThis();
  public void setInspectorDesktopPane (JDesktopPane desktopPane ) ;

  public String getDvmName();
  public void setViewerCanvasW2 (ViewerCanvasW2 canvas );
  public void setViewerCanvasR2 (ViewerCanvasR2 canvas );
  public void ViewerShowMsg(DashMessage m);
  public void removeAgentAll ();
  public DVM getDVM();
  public void replaceConsole();
  public void clearLog();
  public Vector getAllAgentName();
  
}
