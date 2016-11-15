public class AutoDirCheckClient {
  public static void main(String[] args) {
    AutoDirCheck check = new AutoDirCheck();
    check.start();
    while(true){
      try {
        // 1分間プログラムを走らせる
        Thread.sleep(30 * 1000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // check.stop();
    }
  }
}