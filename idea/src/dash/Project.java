package dash;
import java.io.*;
import java.util.*;

/**
 * <p>タイトル:プロジェクトの情報を保持するクラス </p>
 * <p>説明:プロジェクトの情報を保持するクラス </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class Project {

  /** プロジェクトファイル名 */
  private String ProjectFileName = "";

  /** プロジェクトファイル名（パス付き）*/
  private String ProjectFileNameWithPath = "";

  /** プロジェクトのパス */
  private String ProjectPath = "";

  /** プロジェクトで管理しているファイル名 */
  private Vector vecFileNames = new Vector();
  private Vector vecFileNamesWithPath = new Vector();
  private Hashtable htFileNamesWithPath = new Hashtable();

  /** プロジェクトで管理しているファイル(Fileクラスを持つ) */
  private Vector vecFiles = new Vector();

  private Vector vecFolderPath = new Vector();
  private Properties properties= null;

  /****************************************************************************
   * コンストラクタ
   * @param ProjectFileName プロジェクトファイル名
   * @return なし
   ****************************************************************************/
  public Project(String ProjectFileName) {
    createProjectInfo(ProjectFileName);
  }

  /****************************************************************************
   * プロジェクト情報構築
   * @param projectFileName プロジェクトファイル名
   * @return なし
   ****************************************************************************/
  public Project createProjectInfo(String projectFileName) {
    this.ProjectFileNameWithPath = projectFileName;

    // プロジェクト名を取得
    ProjectFileName = ProjectFileNameWithPath.substring(ProjectFileNameWithPath.lastIndexOf(File.separator)+1) ;

    // パス情報を取得
    ProjectPath = ProjectFileNameWithPath.substring(0,ProjectFileNameWithPath.lastIndexOf(File.separator)) ;
    if (!ProjectPath.endsWith(File.separator)) {
      ProjectPath += File.separator;
    }

    new File(ProjectPath + "java_" ).mkdirs();
    new File(ProjectPath + "rset_" ).mkdirs();

    // プロジェクトで管理するファイルの名称を取得
    FileReader f_in;
    BufferedReader b_in;
    String sLine = "";

    vecFileNames.clear();
    vecFileNamesWithPath.clear();
    htFileNamesWithPath.clear();
    vecFiles.clear();
    //読み込み処理
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

        // ディレクトリ情報ファイル
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

        // プロジェクトプロパティを読む
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
          // デフォルトファイル
          File defaultfile = new File(ProjectPath + "bp.property");

          // 読み込み
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

        // デフォルトの設定。
        // ただし、起動時に-Dで指定したものを優先する。
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
   * プロジェクトファイル名（パスなし）を返す
   * @param なし
   * @return プロジェクトファイル名（パスなし）
   ****************************************************************************/
  public String getProjectFileName() {
    return ProjectFileName;
  }

  /****************************************************************************
   * プロジェクトファイル名（パス付き）を返す
   * @param なし
   * @return プロジェクトファイル名（パス付き）
   ****************************************************************************/
  public String getProjectFileNameWithPath() {
    return ProjectFileNameWithPath;
  }

  /****************************************************************************
   * プロジェクトのパスを返す
   * @param なし
   * @return プロジェクトのパス
   ****************************************************************************/
  public String getProjectPath() {
    return ProjectPath;
  }

  /****************************************************************************
   * プロジェクトが管理しているファイルの数を返す
   * @param なし
   * @return プロジェクトが管理しているファイルの数
   ****************************************************************************/
  public int getFileCount() {
    return vecFileNames.size();
  }

  /****************************************************************************
   * プロジェクトが管理しているファイルの名称を返す
   * @param index 0～getFileCountメソッドで得たファイルの数-1　の範囲で指定
   * @return プロジェクトが管理しているファイルの名称
   ****************************************************************************/
  public String getFileName(int index ) {
    return (String)vecFileNames.elementAt(index);
  }

  /****************************************************************************
   * プロジェクトが管理しているファイルを返す
   * @param index 0～getFileCountメソッドで得たファイルの数-1　の範囲で指定
   * @return プロジェクトが管理しているファイル
   ****************************************************************************/
  public File getFile(int index ) {
    return (File)vecFiles.elementAt(index);
  }

  /****************************************************************************
   * プロジェクトが管理しているファイル名称を格納しているベクターを返す
   * @param なし
   * @return プロジェクトが管理しているファイル名称を格納しているベクター
   ****************************************************************************/
  public Vector getFileNames() {
    return vecFileNames;
  }
  public Vector getFileNamesWithPath() {
    return vecFileNamesWithPath;
  }

  /****************************************************************************
   * プロジェクトが管理しているファイルを格納しているベクターを返す
   * @param なし
   * @return プロジェクトが管理しているファイルを格納しているベクター
   ****************************************************************************/
  public Vector getFiles() {
    return vecFiles;
  }

  public Vector getFolderPath(){
    return vecFolderPath;
  }


}