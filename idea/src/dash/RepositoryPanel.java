package dash;

import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.util.*;

import ps.*;

/**
 * <p>タイトル:リポジトリ表示用パネル </p>
 * <p>説明: </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class RepositoryPanel extends JPanel {

  /** リポジトリ */
  public Repository rep = null;

  /** インスペクタを表示するエリア */
  private JDesktopPane InspectorDesktopPane=null;

  /****************************************************************************
   * コンストラクタ
   * @param なし
   * @return なし
   ****************************************************************************/
  public RepositoryPanel( ) {
    String[] dmy = new String[0];

    // リポジトリ作成
    createRepository(dmy);

    // リポジトリが持つACLエディタを配置する */
    this.setLayout(new BorderLayout());
    this.add(rep.getNewIf().getThis(),BorderLayout.CENTER);

  }

  /****************************************************************************
   * インスペクタを表示する領域を設定<br>
   * 本クラスでは、インスペクタを表示する領域は作成せず、Simulator.javaで作成している。<br>
   * 本クラス自身も、Simulator.javaで作成されており、その時に、Simulator.javaで作成している<br>
   * インスペクタを表示する領域をSimulator.javaより設定するようにしている。
   * @param desktopPane インスペクタを表示する領域
   * @return なし
   ****************************************************************************/
  public void setInspectorDesktopPane (JDesktopPane desktopPane ) {
    InspectorDesktopPane = desktopPane;

    // newIfにも、教える
    rep.getNewIf().setInspectorDesktopPane(desktopPane);
  }

  /****************************************************************************
   * リポジトリ作成
   * @param
   * @return なし
   ****************************************************************************/
  private void createRepository(String args[]) {
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
    //if ((prop=System.getProperty("dash.viewer"))!=null &&
    //    prop.equalsIgnoreCase("on"))
    //  useViewer = true;
	//System.out.println(useViewer);
   
    rep = new Repository(name, msgfile, useNewif, useViewer, dashdir);
    rep.startVM();
    //this.loadAgents();
  }

  // ↓?????
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

  // ↓これが使われることはない
  private static void usage() {
    System.err.println("usage: java -jar Workplace [name] [+p *.prj]");
    System.exit(1);
  }

  /** trueを返す。 */
  public boolean isRtype() {
    return true;
  }

  /**
   * dash.loadpathで指定されたディレクトリからスクリプトを読み込む。（未使用）
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
   * エージェントを生成する。（未使用）
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
      if (!Character.isUpperCase((char)cname.charAt(0)))
        continue;

      String ext = files[i].substring(p+1);
      if (!ext.equalsIgnoreCase("dash"))
        continue;

      rep.addLoadQueue(file);
    }
  }

  /****************************************************************************
   * 指定された場所から、指定されたファイルをリポジトリに読み込む
   * @param projectpath プロジェクトパス
   * @param vecFiles 読み込むファイルの名称を格納したベクター
   * @return なし
   ****************************************************************************/
  public void loadProjectAgents(String projectpath, Vector vecFiles, String DefaultDir) {

    projectpath = "";
    for (int i=0; i<vecFiles.size(); i++ ) {

      // ルールセットファイルは読み込まない
      if (((String)vecFiles.elementAt(i)).toLowerCase().endsWith(".rset") ) {
        continue;
      }

      // コードチェック（エラーチェック）行うために、ファイルの内容を読み込む
      //File file = new File (projectpath,(String)vecFiles.elementAt(i));
      File file = new File ((String)vecFiles.elementAt(i));

      FileReader f_in;
      BufferedReader b_in;
      String sAll = "";
      String sLine = "";

      //読み込み処理
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

      // コードチェック実行
      String Code = sAll;
      try {
        // コード解析クラスを作成
        //Parser parser = new Parser((String)vecFiles.elementAt(i), Code, false);
        Parser parser = new Parser(file.getName(), Code, false);
        // インクルードファイルは、解析するファイルと同じ場所にあるものを見るため、ファイルの実体がある場所を教える
        //parser.setDefaultDir(projectpath);
        parser.setDefaultDir(DefaultDir);

        // 解析実行　ここでエラー（コードのエラー）が発生した時は、Catchに飛びます
        parser.parse();

        // インクルードファイル情報を取得
        // ルールセットをインクルードしていない場合は、以下で取得したベクターのサイズはゼロ
        Vector vecIncudeFileName = parser.getIncludeFileNames();
        Vector vecIncludesErrorMsg = parser.getIncludesErrorMsg();
        Vector vecIncludesErrorLineNo = parser.getIncludesErrorLineNo();
        if (vecIncudeFileName.size() > 0 ) {
          // ルールセットをインクルードしている場合は、ルールセットのチェックも行う
          boolean finderr = false;
          finderr = false;

          // Dashファイルの解析時に、既にルールセットファイルのエラーが発生しているか調べる
          // ここで発生するエラーの原因は、Dashファイルが存在する場所にルールセットファイルが
          // ない時である
          for (int j=0; j<vecIncudeFileName.size(); j++ ) {
            if (!((String)vecIncludesErrorMsg.elementAt(j)).equals("") ) {
              rep.getNewIf().printlnE("[Error]" +(String)vecFiles.elementAt(i) + ":" + (String)vecIncludesErrorMsg.elementAt(j) + "line：" + (String)vecIncludesErrorLineNo.elementAt(j)+"\n");
              finderr = true;
              break;
            }
          }
          if (finderr ) {
            // エラーがあった場合、現在のファイルの読み込みを行わず、次のファイルの
            // 読込処理を行う
            continue;
          }

          // ルールセットのチェック
          for (int j=0; j<vecIncudeFileName.size(); j++ ) {
            sAll = "";
            sLine = "";

            //読み込み処理
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

            // ルールセットのエラーチェック。ここでエラーが見つかった場合、catch (SyntaxException ee ) に飛ぶ
            parser.parseRuleset();
          }
        }

      }
      catch (SyntaxException ee ) {
        //System.err.println("[エラー]" +(String)vecFiles.elementAt(i) + ":" + ee.comment + "行：" + ee.lineno);
        rep.getNewIf().printlnE("[Error]" +(String)vecFiles.elementAt(i) + ":" + ee.comment + "line:" + ee.lineno + "\n");
        continue;
      }
      //File file = new File (projectpath,(String)vecFiles.elementAt(i));
      rep.addLoadQueue(file);
    }

  }

  /****************************************************************************
   * リポジトリが使用しているDVMを返す
   * @param なし
   * @return DVM
   ****************************************************************************/
  public String getDvmName() {
    return rep.getDVMname();
  }

  /****************************************************************************
   * リポジトリのビューアを設定する
   * @param canvas リポジトリのビューア
   * @return なし
   ****************************************************************************/
  public void setViewerCanvas (ViewerCanvasR2 canvas ) {
    // NewIfから、ビューアへのエージェント描画等を行うため、NewIfへビューアを引き渡す
    rep.getNewIf().setViewerCanvasR2(canvas);
  }

  /****************************************************************************
   * 読み込んでいるエージェントを全てクリアする
   * @param なし
   * @return なし
   ****************************************************************************/
  public void removeAgentAll () {
    rep.getNewIf().removeAgentAll();
  }

}