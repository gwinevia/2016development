package dash;

import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.util.*;

import ps.*;

/**
 * <p>�^�C�g��:���|�W�g���\���p�p�l�� </p>
 * <p>����: </p>
 * <p>���쌠: Copyright (c) 2003</p>
 * <p>��Ж�:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class RepositoryPanel extends JPanel {

  /** ���|�W�g�� */
  public Repository rep = null;

  /** �C���X�y�N�^��\������G���A */
  private JDesktopPane InspectorDesktopPane=null;

  /****************************************************************************
   * �R���X�g���N�^
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public RepositoryPanel( ) {
    String[] dmy = new String[0];

    // ���|�W�g���쐬
    createRepository(dmy);

    // ���|�W�g��������ACL�G�f�B�^��z�u���� */
    this.setLayout(new BorderLayout());
    this.add(rep.getNewIf().getThis(),BorderLayout.CENTER);

  }

  /****************************************************************************
   * �C���X�y�N�^��\������̈��ݒ�<br>
   * �{�N���X�ł́A�C���X�y�N�^��\������̈�͍쐬�����ASimulator.java�ō쐬���Ă���B<br>
   * �{�N���X���g���ASimulator.java�ō쐬����Ă���A���̎��ɁASimulator.java�ō쐬���Ă���<br>
   * �C���X�y�N�^��\������̈��Simulator.java���ݒ肷��悤�ɂ��Ă���B
   * @param desktopPane �C���X�y�N�^��\������̈�
   * @return �Ȃ�
   ****************************************************************************/
  public void setInspectorDesktopPane (JDesktopPane desktopPane ) {
    InspectorDesktopPane = desktopPane;

    // newIf�ɂ��A������
    rep.getNewIf().setInspectorDesktopPane(desktopPane);
  }

  /****************************************************************************
   * ���|�W�g���쐬
   * @param
   * @return �Ȃ�
   ****************************************************************************/
  private void createRepository(String args[]) {
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
    //if ((prop=System.getProperty("dash.viewer"))!=null &&
    //    prop.equalsIgnoreCase("on"))
    //  useViewer = true;
	//System.out.println(useViewer);
   
    rep = new Repository(name, msgfile, useNewif, useViewer, dashdir);
    rep.startVM();
    //this.loadAgents();
  }

  // ��?????
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

  // �����ꂪ�g���邱�Ƃ͂Ȃ�
  private static void usage() {
    System.err.println("usage: java -jar Workplace [name] [+p *.prj]");
    System.exit(1);
  }

  /** true��Ԃ��B */
  public boolean isRtype() {
    return true;
  }

  /**
   * dash.loadpath�Ŏw�肳�ꂽ�f�B���N�g������X�N���v�g��ǂݍ��ށB�i���g�p�j
   */
  //private void loadAgents() {
  public void loadAgents() {
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
   * �G�[�W�F���g�𐶐�����B�i���g�p�j
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
      if (!Character.isUpperCase((char)cname.charAt(0)))
        continue;

      String ext = files[i].substring(p+1);
      if (!ext.equalsIgnoreCase("dash"))
        continue;

      rep.addLoadQueue(file);
    }
  }

  /****************************************************************************
   * �w�肳�ꂽ�ꏊ����A�w�肳�ꂽ�t�@�C�������|�W�g���ɓǂݍ���
   * @param projectpath �v���W�F�N�g�p�X
   * @param vecFiles �ǂݍ��ރt�@�C���̖��̂��i�[�����x�N�^�[
   * @return �Ȃ�
   ****************************************************************************/
  public void loadProjectAgents(String projectpath, Vector vecFiles, String DefaultDir) {

    projectpath = "";
    for (int i=0; i<vecFiles.size(); i++ ) {

      // ���[���Z�b�g�t�@�C���͓ǂݍ��܂Ȃ�
      if (((String)vecFiles.elementAt(i)).toLowerCase().endsWith(".rset") ) {
        continue;
      }

      // �R�[�h�`�F�b�N�i�G���[�`�F�b�N�j�s�����߂ɁA�t�@�C���̓��e��ǂݍ���
      //File file = new File (projectpath,(String)vecFiles.elementAt(i));
      File file = new File ((String)vecFiles.elementAt(i));

      FileReader f_in;
      BufferedReader b_in;
      String sAll = "";
      String sLine = "";

      //�ǂݍ��ݏ���
      try {
          //f_in = new FileReader(projectpath + (String)vecFiles.elementAt(i));
          //b_in = new BufferedReader(f_in);
          b_in = new BufferedReader(new InputStreamReader(
              new FileInputStream((String)vecFiles.elementAt(i)),
              "JISAutoDetect"));

          while((sLine = b_in.readLine()) != null) {
              sAll += sLine + "\n";
          }
          b_in.close();

      } catch(Exception ex) {
          System.out.println(ex);
          return ;
      }

      // �R�[�h�`�F�b�N���s
      String Code = sAll;
      try {
        // �R�[�h��̓N���X���쐬
        //Parser parser = new Parser((String)vecFiles.elementAt(i), Code, false);
        Parser parser = new Parser(file.getName(), Code, false);
        // �C���N���[�h�t�@�C���́A��͂���t�@�C���Ɠ����ꏊ�ɂ�����̂����邽�߁A�t�@�C���̎��̂�����ꏊ��������
        //parser.setDefaultDir(projectpath);
        parser.setDefaultDir(DefaultDir);

        // ��͎��s�@�����ŃG���[�i�R�[�h�̃G���[�j�������������́ACatch�ɔ�т܂�
        parser.parse();

        // �C���N���[�h�t�@�C�������擾
        // ���[���Z�b�g���C���N���[�h���Ă��Ȃ��ꍇ�́A�ȉ��Ŏ擾�����x�N�^�[�̃T�C�Y�̓[��
        Vector vecIncudeFileName = parser.getIncludeFileNames();
        Vector vecIncludesErrorMsg = parser.getIncludesErrorMsg();
        Vector vecIncludesErrorLineNo = parser.getIncludesErrorLineNo();
        if (vecIncudeFileName.size() > 0 ) {
          // ���[���Z�b�g���C���N���[�h���Ă���ꍇ�́A���[���Z�b�g�̃`�F�b�N���s��
          boolean finderr = false;
          finderr = false;

          // Dash�t�@�C���̉�͎��ɁA���Ƀ��[���Z�b�g�t�@�C���̃G���[���������Ă��邩���ׂ�
          // �����Ŕ�������G���[�̌����́ADash�t�@�C�������݂���ꏊ�Ƀ��[���Z�b�g�t�@�C����
          // �Ȃ����ł���
          for (int j=0; j<vecIncudeFileName.size(); j++ ) {
            if (!((String)vecIncludesErrorMsg.elementAt(j)).equals("") ) {
              rep.getNewIf().printlnE("[Error]" +(String)vecFiles.elementAt(i) + ":" + (String)vecIncludesErrorMsg.elementAt(j) + "line�F" + (String)vecIncludesErrorLineNo.elementAt(j)+"\n");
              finderr = true;
              break;
            }
          }
          if (finderr ) {
            // �G���[���������ꍇ�A���݂̃t�@�C���̓ǂݍ��݂��s�킸�A���̃t�@�C����
            // �Ǎ��������s��
            continue;
          }

          // ���[���Z�b�g�̃`�F�b�N
          for (int j=0; j<vecIncudeFileName.size(); j++ ) {
            sAll = "";
            sLine = "";

            //�ǂݍ��ݏ���
            try {
              //f_in = new FileReader(projectpath + (String)vecIncudeFileName.elementAt(j));
              //b_in = new BufferedReader(f_in);
              b_in = new BufferedReader(new InputStreamReader(
                  new FileInputStream((String)vecIncudeFileName.elementAt(j)),
                  "JISAutoDetect"));

              while((sLine = b_in.readLine()) != null) {
                  sAll += sLine + "\n";
              }
              b_in.close();

            } catch(Exception ex) {
              System.out.println(ex);
              return ;
            }

            Code = sAll;
            File f = new File ((String)vecIncudeFileName.elementAt(j));

            //parser = new Parser((String)vecIncudeFileName.elementAt(j), Code, false);
            parser = new Parser(f.getName(), Code, false);

            // ���[���Z�b�g�̃G���[�`�F�b�N�B�����ŃG���[�����������ꍇ�Acatch (SyntaxException ee ) �ɔ��
            parser.parseRuleset();
          }
        }

      }
      catch (SyntaxException ee ) {
        //System.err.println("[�G���[]" +(String)vecFiles.elementAt(i) + ":" + ee.comment + "�s�F" + ee.lineno);
        rep.getNewIf().printlnE("[Error]" +(String)vecFiles.elementAt(i) + ":" + ee.comment + "line:" + ee.lineno + "\n");
        continue;
      }
      //File file = new File (projectpath,(String)vecFiles.elementAt(i));
      rep.addLoadQueue(file);
    }

  }

  /****************************************************************************
   * ���|�W�g�����g�p���Ă���DVM��Ԃ�
   * @param �Ȃ�
   * @return DVM
   ****************************************************************************/
  public String getDvmName() {
    return rep.getDVMname();
  }

  /****************************************************************************
   * ���|�W�g���̃r���[�A��ݒ肷��
   * @param canvas ���|�W�g���̃r���[�A
   * @return �Ȃ�
   ****************************************************************************/
  public void setViewerCanvas (ViewerCanvasR2 canvas ) {
    // NewIf����A�r���[�A�ւ̃G�[�W�F���g�`�擙���s�����߁ANewIf�փr���[�A�������n��
    rep.getNewIf().setViewerCanvasR2(canvas);
  }

  /****************************************************************************
   * �ǂݍ���ł���G�[�W�F���g��S�ăN���A����
   * @param �Ȃ�
   * @return �Ȃ�
   ****************************************************************************/
  public void removeAgentAll () {
    rep.getNewIf().removeAgentAll();
  }

}