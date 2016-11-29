import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CallShellScript {
  public static void main (String args[]) {
    try {
      Process process = new ProcessBuilder("sh", "/home/mmk/krswmmk/Java_programming/shelltest.sh","647acd72173213aa55e17cf280f895319d7668e8", "test.txt").start();
      String text;
      InputStream is = process.getInputStream();
      InputStreamReader isr = new InputStreamReader(is, "UTF-8");
      BufferedReader reader = new BufferedReader(isr);
      StringBuilder builder = new StringBuilder();
      int c;
      while ((c = reader.read()) != -1) {
        builder.append((char)c);
      }
      // 実行結果を格納
      text = builder.toString();
      int ret = process.waitFor();

      System.out.println(text);

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}