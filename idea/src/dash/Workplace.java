package dash;

import java.io.*;

/**
 * ワークプレースを実現するクラス。
 */
public class Workplace extends DVM {

  /**
   * クラスDVMのコンストラクタを呼び出す
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



  /** falseを返す。 */
  public boolean isRtype() {
    return false;
  }

  /**
   * メイン
   */
  public static void main(String args[]) {

    // デフォルトファイルの読み込み
    DashDefaults dashDefaults = new DashDefaults();
    dashDefaults.loadDefaults();
    File msgfile = dashDefaults.getMessageFile();
    File dashdir = dashDefaults.getDashdir();

    String name = null;          // ホスト名を含まない環境名
    File prjFile = null;       // プロジェクトファイル
    boolean useNewif = false;    // 新インタフェースを使うならtrue

    // ADD COSMOS
    boolean useViewer = false;   // 新ビューアを使うならtrue

    int idx = 0;

    // 環境名
    if (args.length > 0 && !args[idx].startsWith("+")) {
      name = args[idx];
      idx++;
    }

    while (idx < args.length) {

      // プロジェクトファイル
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

      // エラー
      usage();
    }

    // デフォルト
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
      System.err.println("error: +p に続けるファイル名は *.prj です。。");
      System.exit(1);
    }

    File file = new File(filename);
    if (!file.exists()) {
      System.err.println("error: "+filename+"というファイルはありません。");
      System.exit(1);
    }

    if (!file.canRead()) {
      System.err.println("error: "+filename+"というファイルは読めません。");
      System.exit(1);
    }

    return file;
  }

  private static void usage() {
    System.err.println("usage: java -jar Workplace [name] [+p *.prj]");
    System.exit(1);
  }

}
