package dash;
import javax.swing.*;

import java.io.File;
import java.util.*;

/**
 * �����j�^�̃C���^�t�F�[�X
 */
public interface NewifItface {

  /** �J�� */
  public void show();

  /** �������̊Ď����J�n���� */
  public void startMemoryWatch();

  /** settext */
  public void settext(String agent, String s);

  /**
   * ���b�Z�[�W�̏������s���B
   */
  public void showMsg(DashMessage m);

  /** ��M�������b�Z�[�W��receive�^�u�ɕ\������B*/
  public void putMsg(DashMessage msg);

  /** nonStopCheck�����������(nonstop���)�ɂ��� */
  public void setNonstop();

  /**
   * �G�[�W�F���g�����ɒǉ�����B���̏ꍇ�ɌĂ΂��B
   * 1)���|�W�g���G�[�W�F���g���t�@�C�����琶�����ꂽ�Ƃ�
   * 2)�C���X�^���X�G�[�W�F���g���������ꂽ�Ƃ�
   * @param name �G�[�W�F���g��
   */
  public void addAgent(String name);
  public void addAgent(String name, String origin);

  /** �G�[�W�F���g������ */
  public void removeAgent(String name);

  /** �{���ɏ����ꂽ�̂�҂B*/
  public void confirmSync();

  /**
   * ���O�����O�^�u�ɕ\������B
   * @param s ���O
   */
  public void println(String s);

  /**
   * �G���[���G���[�^�u�ɕ\������B
   * @param s �G���[�̐���
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
