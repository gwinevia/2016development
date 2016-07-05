package dash;

import java.util.*;
import java.net.*;
import java.io.*;

public class DashDefaults {

  // �f�t�H���g�t�@�C���̃t�@�C����
  private static final String DEFAULTSFILE = "defaults.txt";
  //private static final String DEFAULTSFILE = "properties/simulator.properties";


  // DASH�̃f�B���N�g��
  private File dashDir;

  /**
   * �R���X�g���N�^�B
   * �f�t�H���g�t�@�C���A���b�Z�[�W�t�@�C���̂���f�B���N�g����
   * dashDir�ɃZ�b�g����B
   *
   * ���ݏꏊ�́A
   * (1)JAR�t�@�C�����_�u���N���b�N�����ꍇ�́AJAR�t�@�C���Ɠ����f�B���N�g��
   * (2)java -jar�̏ꍇ���AJAR�t�@�C���Ɠ����f�B���N�g��
   * (3)JAR�t�@�C�����w�肵�Ȃ��ꍇ�ADASH�̃g�b�v�f�B���N�g��
   */
  public DashDefaults() {
    // jar:file:/home/hara/dash/dash-1.0/Dash.jar!/adips97/DashDefaults.class
    // �܂���
    // file:/home/hara/dash/dash-1.0/classes/adips97/DashDefaults.class
    URL url = this.getClass().getResource("DashDefaults.class");
    String protocol = url.getProtocol();
    String classfile = null;
    if (protocol.equals("jar")) {
      try {
        URL url2 = new URL(url.getFile()); // "jar:"�ɑ���"file:"�����O����
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
   * �f�t�H���g�t�@�C����ǂݍ��݁AProperties��Ԃ��B
   */
  public Properties loadDefaults() {

    // �f�t�H���g�t�@�C��
    File defaultfile = new File(dashDir, DEFAULTSFILE);

    // �ǂݍ���
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

    // �f�t�H���g�̐ݒ�B
    // �������A�N������-D�Ŏw�肵�����̂�D�悷��B
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
// �v���p�e�B�̒ǉ�
prop.setProperty( "USERNAME", "SCOTT" );
prop.setProperty( "PASSWORD", "TIGER" );
// �v���p�e�B�̏����o��
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

  /** �e�X�g�p */
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

