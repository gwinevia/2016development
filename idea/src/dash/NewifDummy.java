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
 * �����j�^���g��Ȃ��ꍇ�Ƀ_�~�[�Ƃ��ė��p����N���X�B
 */
public class NewifDummy implements NewifItface {

  /** �J�� */
  public void show() { }

  /** �������̊Ď����J�n���� */
  public void startMemoryWatch() { }

  /** settext */
  public void settext(String agent, String s) { }

  /**
   * ���b�Z�[�W�̏������s���B
   */
  public void showMsg(DashMessage m) { }

  /** ��M�������b�Z�[�W��receive�^�u�ɕ\������B*/
  public void putMsg(DashMessage msg) {
    System.out.println("DVM: get message: "+msg);
  }

  /** nonStopCheck�����������(nonstop���)�ɂ��� */
  public void setNonstop() { }

  /**
   * �G�[�W�F���g�����ɒǉ�����B���̏ꍇ�ɌĂ΂��B
   * 1)���|�W�g���G�[�W�F���g���t�@�C�����琶�����ꂽ�Ƃ�
   * 2)�C���X�^���X�G�[�W�F���g���������ꂽ�Ƃ�
   * @param name �G�[�W�F���g��
   */
  public void addAgent(String name) {
    System.out.println("DVM: agent \""+name+"\" are created");
  }

  /** �G�[�W�F���g������ */
  public void removeAgent(String name) {
    System.out.println("DVM: agent \""+name+"\" are removed");
  }

  /** �{���ɏ����ꂽ�̂�҂B*/
  public void confirmSync() { }

  /**
   * ���O�����O�^�u�ɕ\������B
   * @param s ���O
   */
  public void println(String s) {
    System.out.println(s);
  }

  /**
   * �G���[���G���[�^�u�ɕ\������B
   * @param s �G���[�̐���
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
