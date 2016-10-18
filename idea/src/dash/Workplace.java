package dash;

import java.io.*;

/**
 * ���[�N�v���[�X����������N���X�B
 */
public class Workplace extends DVM {

  /**
   * �N���XDVM�̃R���X�g���N�^���Ăяo��
   */
  /*
  public Workplace(String name, File msgfile, boolean useNewif, File dashdir) {
    super(name, msgfile, useNewif, dashdir);
  }
  */

  //public static Workplace wp;
  // UPDATE COSMOS
  public Workplace(String name, File msgfile, boolean useNewif,	 boolean useViewer, File dashdir) {
    super(name, msgfile, useNewif, useViewer,  dashdir);
  }



  /** false��Ԃ��B */
  public boolean isRtype() {
    return false;
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
    File prjFile = null;       // �v���W�F�N�g�t�@�C��
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

      // �v���W�F�N�g�t�@�C��
      if (args[idx].equals("+p")) {
        idx++;
        if (idx<args.length) {
          prjFile = getProjectFile(args[idx]);
          idx++;
          continue;
        } else
          usage();
      }

      // NO_GUI
      if (args[idx].equals("+ng")) {
        System.setProperty("dash.noGUI", "on");
        idx++;
        continue;
      }

      // �G���[
      usage();
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

    //Workplace wp = new Workplace(name, msgfile, useNewif, dashdir);
		// UPDATE COSMOS
    System.setProperty("DashMode", "on");
    Workplace wp = new Workplace(name, msgfile, useNewif, useViewer, dashdir);
    wp.startVM();
    if (prjFile != null)
      wp.loadProject(prjFile);
  }


  private static File getProjectFile(String filename) {
    if (!filename.toLowerCase().endsWith(".prj")) {
      System.err.println("error: +p �ɑ�����t�@�C������ *.prj �ł��B�B");
      System.exit(1);
    }

    File file = new File(filename);
    if (!file.exists()) {
      System.err.println("error: "+filename+"�Ƃ����t�@�C���͂���܂���B");
      System.exit(1);
    }

    if (!file.canRead()) {
      System.err.println("error: "+filename+"�Ƃ����t�@�C���͓ǂ߂܂���B");
      System.exit(1);
    }

    return file;
  }

  private static void usage() {
    System.err.println("usage: java -jar Workplace [name] [+p *.prj]");
    System.exit(1);
  }

}
