 
public class lastIndexOf_test {
  public static void main(String[] args) {

    String str = "/home/mmk/DASH/dash/idea/scripts/SF_Lab_Test";
    int p = str.lastIndexOf("idea");
    String str2 = str.substring(0, p+5);
    System.out.print(str2 + "\n");
  }
}