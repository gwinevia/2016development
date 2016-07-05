package dash;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import ps.*;

/**
 * 環境モニタを使わない場合にダミーとして利用するクラス。
 */
public class NewifDummy implements NewifItface {

  /** 開く */
  public void show() { }

  /** メモリの監視を開始する */
  public void startMemoryWatch() { }

  /** settext */
  public void settext(String agent, String s) { }

  /**
   * メッセージの処理を行う。
   */
  public void showMsg(DashMessage m) { }

  /** 受信したメッセージをreceiveタブに表示する。*/
  public void putMsg(DashMessage msg) {
    System.out.println("DVM: get message: "+msg);
  }

  /** nonStopCheckを押した状態(nonstop状態)にする */
  public void setNonstop() { }

  /**
   * エージェントを環境に追加する。次の場合に呼ばれる。
   * 1)リポジトリエージェントがファイルから生成されたとき
   * 2)インスタンスエージェントが生成されたとき
   * @param name エージェント名
   */
  public void addAgent(String name) {
    System.out.println("DVM: agent \""+name+"\" are created");
  }

  /** エージェントを消す */
  public void removeAgent(String name) {
    System.out.println("DVM: agent \""+name+"\" are removed");
  }

  /** 本当に消されたのを待つ。*/
  public void confirmSync() { }

  /**
   * ログをログタブに表示する。
   * @param s ログ
   */
  public void println(String s) {
    System.out.println(s);
  }

  /**
   * エラーをエラータブに表示する。
   * @param s エラーの説明
   */
  public void printlnE(String s) {
    System.err.println(s);
  }
  // ADD COSMOS
  public void addChildWindow (JInternalFrame jiFrame ){
  }
  public void initialize(){
  }

  public AclPanel getAclPanel() {
    return null;
  }
  public JPanel getThis(){
    return null;
  }
  public void setInspectorDesktopPane (JDesktopPane desktopPane ) {
  }
  public String getDvmName(){
    return "";
  }
  public void setViewerCanvasW2 (ViewerCanvasW2 canvas ){
  }
  public void setViewerCanvasR2 (ViewerCanvasR2 canvas ){
  }
  public void addAgent(String name, String origin){
  }
  public void ViewerShowMsg(DashMessage m){
  }
  public void removeAgentAll (){
  }
  public DVM getDVM(){
    return null;
  }

  public void replaceConsole(){
    ;
  }
  public void clearLog(){}
  public Vector getAllAgentName(){
    return null;
  }

}
