package dash;

import java.util.*;
import java.net.*;
import java.io.*;

public class DashDefaults {

  // デフォルトファイルのファイル名
  private static final String DEFAULTSFILE = "defaults.txt";
  //private static final String DEFAULTSFILE = "properties/simulator.properties";


  // DASHのディレクトリ
  private File dashDir;

  /**
   * コンストラクタ。
   * デフォルトファイル、メッセージファイルのあるディレクトリを
   * dashDirにセットする。
   *
   * 存在場所は、
   * (1)JARファイルをダブルクリックした場合は、JARファイルと同じディレクトリ
   * (2)java -jarの場合も、JARファイルと同じディレクトリ
   * (3)JARファイルを指定しない場合、DASHのトップディレクトリ
   */
  public DashDefaults() {
    // jar:file:/home/hara/dash/dash-1.0/Dash.jar!/adips97/DashDefaults.class
    // または
    // file:/home/hara/dash/dash-1.0/classes/adips97/DashDefaults.class
    URL url = this.getClass().getResource("DashDefaults.class");
    String protocol = url.getProtocol();
    String classfile = null;
    if (protocol.equals("jar")) {
      try {
        URL url2 = new URL(url.getFile()); // "jar:"に続く"file:"を除外する
        classfile = url2.getFile();
      } catch (MalformedURLException e) {
        e.printStackTrace();
        classfile = null;
      }
    } else
      classfile = url.getFile();

    if (classfile != null) {
      File dir = new File(classfile);
      dir = dir.getParentFile();     // dash-1.0/classes/adips97
      dir = dir.getParentFile();     // dash-1.0/classes
      dir = dir.getParentFile();     // dash-1.0
      dashDir = dir;
    }
  }

  /**
   * デフォルトファイルを読み込み、Propertiesを返す。
   */
  public Properties loadDefaults() {

    // デフォルトファイル
    File defaultfile = new File(dashDir, DEFAULTSFILE);

    // 読み込み
    Properties properties = null;
    try {
      FileInputStream fis = new FileInputStream(defaultfile);
      properties = new Properties();
      properties.load(fis);
      fis.close();
    } catch (FileNotFoundException e) {
      System.err.println("warning: "+DEFAULTSFILE+" not found");
    } catch (IOException e) {
      e.printStackTrace();
    }

    // デフォルトの設定。
    // ただし、起動時に-Dで指定したものを優先する。
    for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();
      String defaultValue = properties.getProperty(key);
      String systemValue = System.getProperty(key);
      //System.out.println(key);
      if (systemValue == null)
        System.setProperty(key, defaultValue);
    }
    /*
String ss = properties.toString();
 try {
Properties prop = new Properties();
// プロパティの追加
prop.setProperty( "USERNAME", "SCOTT" );
prop.setProperty( "PASSWORD", "TIGER" );
// プロパティの書き出し
FileOutputStream fos = new FileOutputStream( "simulator.properties" );
properties.store( fos, "idea Properties" );
fos.close();
 } catch (FileNotFoundException e) {
   System.err.println("warning: "+DEFAULTSFILE+" not found");
 } catch (IOException e) {
   e.printStackTrace();
 }
    */
    return properties;
  }

  public File getMessageFile() {
    return new File(dashDir, "messages.txt");
  }

  public File getDashdir() {
    return dashDir;
  }

  /** テスト用 */
  public static void main(String args[]) {
    System.out.println(System.getProperty("java.class.path"));
    System.exit(1);

    Properties properties = new DashDefaults().loadDefaults();
    for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();
      String value = properties.getProperty(key);
      System.out.println(key+" = "+value);
    }

    try {
      Class c = Class.forName("baseProcess.dashSample.SimpleWindow");
      String cl = c.getClassLoader().getClass().getName();
      System.out.println(c+"(loader="+cl+")");
    } catch (Exception e) {
      e.printStackTrace();
    }

    /*
    EarthGate eg = new EarthGate(false);
    try {
      Class c = eg.loadClass("baseProcess.dashSample.SimpleWindow");
      String cl = c.getClassLoader().getClass().getName();
      System.out.println(c+"(loader="+cl+")");

      Object obj = c.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
    */
  }
}

