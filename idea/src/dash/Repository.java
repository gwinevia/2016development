package dash;

import java.io.*;
import java.util.*;

/**
 * ���|�W�g������������N���X�B
 */
public class Repository extends DVM {

  /**
   * �N���XDVM�̃R���X�g���N�^���Ăяo��
   */
  /*
  public Repository(String name, File msgfile, boolean useNewif, File dashdir){
    super(name, msgfile, useNewif, dashdir);
  }
  */
  // UPDATE COSMOS
  public Repository(String name, File msgfile, boolean useNewif, boolean useViewer, File dashdir) {
    super(name, msgfile, useNewif, useViewer,  dashdir);
  }


  /** true��Ԃ��B */
  public boolean isRtype() {
    return true;
  }

  /**
   * ���C��
   */
  public static void main(String args[]) {

    // �f�t�H���g�t�@�C���̓ǂݍ���
    DashDefaults dashDefaults = new DashDefaults();
    dashDefaults.loadDefaults();
    File msgfile = dashDefaults.getMessageFile();
    File dashdir = dashDefaults.getDashdir();

    String name = null;          // �z�X�g�����܂܂Ȃ�����
    boolean useNewif = false;    // �V�C���^�t�F�[�X���g���Ȃ�true
    // ADD COSMOS
    boolean useViewer = false;   // �V�r���[�A���g���Ȃ�true
    int idx = 0;

    // ����
    if (args.length > 0 && !args[idx].startsWith("+")) {
      name = args[idx];
      idx++;
    }

    while (idx < args.length) {

      // NO_GUI
      if (args[idx].equals("+ng")) {
        System.setProperty("dash.noGUI", "on");
        idx++;
        continue;
      }

    }

    // �f�t�H���g
    String prop=null;
    if ((prop=System.getProperty("dash.interface"))!=null &&
        prop.equalsIgnoreCase("on"))
      useNewif = true;

    // ADD COSMOS
    if ((prop=System.getProperty("dash.viewer"))!=null &&
        prop.equalsIgnoreCase("on"))
      useViewer = true;

    //Repository rep = new Repository(name, msgfile, useNewif, dashdir);
    // UPDATE COSMOS
    System.setProperty("DashMode", "on");
    Repository rep=new Repository(name, msgfile, useNewif, useViewer, dashdir);
    rep.startVM();
    rep.loadAgents();
  }

  /**
   * dash.loadpath�Ŏw�肳�ꂽ�f�B���N�g������X�N���v�g��ǂݍ��ށB
   */
  private void loadAgents() {
    String dirnames = System.getProperty("dash.loadpath");
    StringTokenizer st = new StringTokenizer(dirnames, File.pathSeparator);

    while (st.hasMoreTokens()) {
      String dirname = st.nextToken();
      if (!dirname.endsWith(File.separator))
        dirname += File.separator;
      loadAgents(dirname);
    }
  }

  /**
   * �G�[�W�F���g�𐶐�����B
   * �w�肳�ꂽ�f�B���N�g���ɂ���G�[�W�F���g�L�q�t�@�C����ǂݍ���Ő�������B
   * �t�@�C�����͉p�啶���Ŏn�܂�A�g���q��.dash�̃t�@�C���B
   * @param dirname �f�B���N�g����(�Ō�� / or \ )
   */
  private void loadAgents(String dirname) {
    // �t�@�C�����̃��X�g���쐬����B
    File dir = new File(dirname);
    if (!dir.isDirectory()) // �f�B���N�g�����Ȃ��ꍇ�͖���
      return;
    String files[] = dir.list();
    Arrays.sort(files);

    for (int i=0; i<files.length; i++) {
      File file = new File(dirname, files[i]);
      if (!file.canRead() || file.isDirectory())
        continue;

      int p = files[i].lastIndexOf('.');
      if (p == -1)
        continue;
						
      String cname = files[i].substring(0, p);
      
	  if(!cname.equals(null))
		continue;
	   
      if (!Character.isUpperCase((char)cname.charAt(0)))
        continue;

      String ext = files[i].substring(p+1);
      if (!ext.equalsIgnoreCase("dash"))
        continue;

      addLoadQueue(file);
    }
  }
}
