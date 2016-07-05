package dash;

import java.io.*;
import java.util.*;

/**
 * リポジトリを実現するクラス。
 */
public class Repository extends DVM {

  /**
   * クラスDVMのコンストラクタを呼び出す
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


  /** trueを返す。 */
  public boolean isRtype() {
    return true;
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

      // NO_GUI
      if (args[idx].equals("+ng")) {
        System.setProperty("dash.noGUI", "on");
        idx++;
        continue;
      }

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

    //Repository rep = new Repository(name, msgfile, useNewif, dashdir);
    // UPDATE COSMOS
    System.setProperty("DashMode", "on");
    Repository rep=new Repository(name, msgfile, useNewif, useViewer, dashdir);
    rep.startVM();
    rep.loadAgents();
  }

  /**
   * dash.loadpathで指定されたディレクトリからスクリプトを読み込む。
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
   * エージェントを生成する。
   * 指定されたディレクトリにあるエージェント記述ファイルを読み込んで生成する。
   * ファイル名は英大文字で始まり、拡張子が.dashのファイル。
   * @param dirname ディレクトリ名(最後は / or \ )
   */
  private void loadAgents(String dirname) {
    // ファイル名のリストを作成する。
    File dir = new File(dirname);
    if (!dir.isDirectory()) // ディレクトリがない場合は無視
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
