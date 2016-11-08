import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AutoDirCheck {

  // 更新の監視対象となるディレクトリ
  private static final File TARGET_DIR = new File("/home/mmk/krswmmk/Java_programming");

  protected boolean fStop;          //（2）スレッドの停止フラグ
  protected List    fRegistereds;   //（3）登録されているファイルのリスト
  protected Map     fLastModifieds; //（4）登録されているファイルの最終更新時刻

  /**
   * 更新チェックの開始
   */
  public void start() {
    fStop = false;
    fRegistereds = new ArrayList();
    fLastModifieds = new HashMap();
    Thread thread = new Thread(new AutoChecker());
    thread.setDaemon(true); //（5）デーモンスレッドにする
    thread.start();
  }

  /**
   * 更新チェックの終了
   */
  public void stop() {
    fStop = true;
  }

  /**
   * 1回のチェック処理
   */
  protected void check() {
    checkRemoved();  // 削除チェック
    checkNew();      // 新規チェック
    checkModified(); // 更新チェック
  }

  /**
   * 削除されたファイルのチェック
   */
  protected void checkRemoved() {
    Iterator it = fRegistereds.iterator();
    while (it.hasNext()) {
      String filename = (String) it.next();
      File file = new File(TARGET_DIR, filename);
      if (!file.exists()) {
        // 削除処理
        it.remove();
        System.out.println(filename + " が削除されました");
      }
    }
  }

  /**
   * 新たに追加されたファイルのチェック
   */
  protected void checkNew() {
    String[] files = TARGET_DIR.list();
    for (int i = 0; i < files.length; i++) {
      if (!fRegistereds.contains(files[i])) {
        // 追加処理
        fRegistereds.add(files[i]);
        System.out.println(files[i] + " が追加されました");
      }
    }
  }

  /**
   * 更新されたファイルのチェック
   */
  protected void checkModified() {
    Iterator it = fRegistereds.iterator();
    while (it.hasNext()) {
      String filename = (String) it.next();
      File file = new File(TARGET_DIR, filename);
      Long lastModified = (Long) fLastModifieds.get(filename);
      long newLastModified = file.lastModified();
      if (lastModified == null) {
        fLastModifieds.put(filename, new Long(newLastModified));
      } else {
        // 更新処理
        if (lastModified.longValue() < newLastModified) {
          fLastModifieds.put(filename, new Long(newLastModified));
          System.out.println(filename + " が更新されました");
        }
      }
    }
  }

  /**
   * （1）バックグラウンドで走る更新チェックスレッド
   */
  protected class AutoChecker implements Runnable {

    public void run() {
      while (!fStop) {
        try {
          Thread.sleep(1000L); // チェック間隔
        } catch (InterruptedException e) {}
        check();
      }
    }
  }
}