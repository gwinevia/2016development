package dash;
import java.io.*;
import java.util.*;

/**
 * <p>�^�C�g��:�v���W�F�N�g�̏���ێ�����N���X </p>
 * <p>����:�v���W�F�N�g�̏���ێ�����N���X </p>
 * <p>���쌠: Copyright (c) 2003</p>
 * <p>��Ж�:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class Project {

  /** �v���W�F�N�g�t�@�C���� */
  private String ProjectFileName = "";

  /** �v���W�F�N�g�t�@�C�����i�p�X�t���j*/
  private String ProjectFileNameWithPath = "";

  /** �v���W�F�N�g�̃p�X */
  private String ProjectPath = "";

  /** �v���W�F�N�g�ŊǗ����Ă���t�@�C���� */
  private Vector vecFileNames = new Vector();
  private Vector vecFileNamesWithPath = new Vector();
  private Hashtable htFileNamesWithPath = new Hashtable();

  /** �v���W�F�N�g�ŊǗ����Ă���t�@�C��(File�N���X������) */
  private Vector vecFiles = new Vector();

  private Vector vecFolderPath = new Vector();
  private Properties properties= null;

  /****************************************************************************
   * �R���X�g���N�^
   * @param ProjectFileName �v���W�F�N�g�t�@�C����
   * @return �Ȃ�
   ****************************************************************************/
  public Project(String ProjectFileName) {
    createProjectInfo(ProjectFileName);
  }

  /****************************************************************************
   * �v���W�F�N�g���\�z
   * @param projectFileName �v���W�F�N�g�t�@�C����
   * @return �Ȃ�
   ****************************************************************************/
  public Project createProjectInfo(String projectFileName) {
    this.ProjectFileNameWithPath = projectFileName;

    // �v���W�F�N�g�����擾
    ProjectFileName = ProjectFileNameWithPath.substring(ProjectFileNameWithPath.lastIndexOf(File.separator)+1) ;

    // �p�X�����擾
    ProjectPath = ProjectFileNameWithPath.substring(0,ProjectFileNameWithPath.lastIndexOf(File.separator)) ;
    if (!ProjectPath.endsWith(File.separator)) {
      ProjectPath += File.separator;
    }

    new File(ProjectPath + "java_" ).mkdirs();
    new File(ProjectPath + "rset_" ).mkdirs();

    // �v���W�F�N�g�ŊǗ�����t�@�C���̖��̂��擾
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    vecFileNames.clear();
    vecFileNamesWithPath.clear();
    htFileNamesWithPath.clear();
    vecFiles.clear();
    //�ǂݍ��ݏ���
    try {
        f_in = new FileReader(ProjectFileNameWithPath);
        //b_in = new BufferedReader(f_in);
        b_in = new BufferedReader(new InputStreamReader(
                                                new FileInputStream(ProjectFileNameWithPath),
                                                "JISAutoDetect"));
        while((sLine = b_in.readLine()) != null) {

          File file = new File(ProjectPath, sLine);
          if (!file.canRead() || file.isDirectory())
            continue;

          int p = sLine.lastIndexOf('.');

          String FileName = "";
          if (sLine.indexOf(File.separator) != -1 ) {
            FileName = sLine.substring(sLine.lastIndexOf(File.separator)+1);
          }
          else {
            FileName = sLine;
          }
          vecFileNames.addElement(FileName);
          vecFileNamesWithPath.addElement(file.getAbsolutePath());
          vecFiles.addElement(file);

          int cnt = vecFileNames.size();

        }
        b_in.close();
        f_in.close();

        // �f�B���N�g�����t�@�C��
        String directoryInfoFile = projectFileName.substring(0,projectFileName.toLowerCase().lastIndexOf (".dpx")+1) + "directoryinfo";

        vecFolderPath.clear();
        vecFolderPath.addElement(ProjectPath);
        if (new File(directoryInfoFile).exists() ) {
          sLine = "";
          b_in = new BufferedReader(new InputStreamReader(
              new FileInputStream(directoryInfoFile),
              "JISAutoDetect"));
          while ((sLine = b_in.readLine()) != null)
          {
            String path = ProjectPath + sLine;

            File f = new File(path);
            if (f.isDirectory() ) {
              if (!path.endsWith(File.separator) ) {
                path += File.separator;
              }
              vecFolderPath.addElement(path);
            }
          }

          b_in.close();
        }

        // �v���W�F�N�g�v���p�e�B��ǂ�
        properties = new Properties();
        if (! new File(ProjectPath + "bp.property" ).exists() ) {
          properties.setProperty("BpOutputPath", "current");
          try {
            FileOutputStream fos = new FileOutputStream( ProjectPath + "bp.property"  );

            BufferedWriter awriter;
            awriter = new BufferedWriter(new OutputStreamWriter(fos, "8859_1"));

            properties.store( fos, "idea Properties" );
          
            fos.close();
          } catch (FileNotFoundException e) {
            System.err.println("warning: simulator.properties not found");
          } catch (IOException e) {
            e.printStackTrace();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        else {
          // �f�t�H���g�t�@�C��
          File defaultfile = new File(ProjectPath + "bp.property");

          // �ǂݍ���
          //Properties properties = null;
          try {
            FileInputStream fis = new FileInputStream(defaultfile);
            properties = new Properties();
            properties.load(fis);
            fis.close();
          } catch (FileNotFoundException e) {
            //System.err.println("warning: "+DEFAULTSFILE+" not found");
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        // �f�t�H���g�̐ݒ�B
        // �������A�N������-D�Ŏw�肵�����̂�D�悷��B
        for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
          String key = (String)e.nextElement();
          String defaultValue = properties.getProperty(key);
          String systemValue = System.getProperty(key);
          //System.out.println(key);
          if (systemValue == null) {
            System.setProperty(key, defaultValue);
          }
          else {
            System.getProperties().remove(key);
            System.setProperty(key, defaultValue);
          }
        }

        return this;
    } catch(Exception ex) {
        System.out.println(ex);
        return null;
    }
  }

  /****************************************************************************
   * �v���W�F�N�g�t�@�C�����i�p�X�Ȃ��j��Ԃ�
   * @param �Ȃ�
   * @return �v���W�F�N�g�t�@�C�����i�p�X�Ȃ��j
   ****************************************************************************/
  public String getProjectFileName() {
    return ProjectFileName;
  }

  /****************************************************************************
   * �v���W�F�N�g�t�@�C�����i�p�X�t���j��Ԃ�
   * @param �Ȃ�
   * @return �v���W�F�N�g�t�@�C�����i�p�X�t���j
   ****************************************************************************/
  public String getProjectFileNameWithPath() {
    return ProjectFileNameWithPath;
  }

  /****************************************************************************
   * �v���W�F�N�g�̃p�X��Ԃ�
   * @param �Ȃ�
   * @return �v���W�F�N�g�̃p�X
   ****************************************************************************/
  public String getProjectPath() {
    return ProjectPath;
  }

  /****************************************************************************
   * �v���W�F�N�g���Ǘ����Ă���t�@�C���̐���Ԃ�
   * @param �Ȃ�
   * @return �v���W�F�N�g���Ǘ����Ă���t�@�C���̐�
   ****************************************************************************/
  public int getFileCount() {
    return vecFileNames.size();
  }

  /****************************************************************************
   * �v���W�F�N�g���Ǘ����Ă���t�@�C���̖��̂�Ԃ�
   * @param index 0�`getFileCount���\�b�h�œ����t�@�C���̐�-1�@�͈̔͂Ŏw��
   * @return �v���W�F�N�g���Ǘ����Ă���t�@�C���̖���
   ****************************************************************************/
  public String getFileName(int index ) {
    return (String)vecFileNames.elementAt(index);
  }

  /****************************************************************************
   * �v���W�F�N�g���Ǘ����Ă���t�@�C����Ԃ�
   * @param index 0�`getFileCount���\�b�h�œ����t�@�C���̐�-1�@�͈̔͂Ŏw��
   * @return �v���W�F�N�g���Ǘ����Ă���t�@�C��
   ****************************************************************************/
  public File getFile(int index ) {
    return (File)vecFiles.elementAt(index);
  }

  /****************************************************************************
   * �v���W�F�N�g���Ǘ����Ă���t�@�C�����̂��i�[���Ă���x�N�^�[��Ԃ�
   * @param �Ȃ�
   * @return �v���W�F�N�g���Ǘ����Ă���t�@�C�����̂��i�[���Ă���x�N�^�[
   ****************************************************************************/
  public Vector getFileNames() {
    return vecFileNames;
  }
  public Vector getFileNamesWithPath() {
    return vecFileNamesWithPath;
  }

  /****************************************************************************
   * �v���W�F�N�g���Ǘ����Ă���t�@�C�����i�[���Ă���x�N�^�[��Ԃ�
   * @param �Ȃ�
   * @return �v���W�F�N�g���Ǘ����Ă���t�@�C�����i�[���Ă���x�N�^�[
   ****************************************************************************/
  public Vector getFiles() {
    return vecFiles;
  }

  public Vector getFolderPath(){
    return vecFolderPath;
  }


}