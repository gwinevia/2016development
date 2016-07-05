package dash;

/**
 * ベースプロセスと拡張メソッドを読み込むためのクラスローダ。
 * JARファイルからDASHを起動した場合、
 * デフォルトのクラスローダはJARファイル以外からクラスファイルを読まないために
 * このクラスが必要となる。
 *
 * クラスローダDashClassLoaderのインスタンスは各エージェントが持つが、
 * クラスローダの実際の機能はDVMが持つ。
 * DVMは次のように動作する。
 *
 * (1)自分の上で動作しているエージェントに対して、バイトコードを渡す。
 *    例えばdvm1で動いているエージェントに、dvm1が渡す場合、
 *    (a)エージェントはDashClassLoader.loadClass()を呼ぶ。
 *    (b)DashClassLoader.findClass()が呼び出される。
 *    (c)dvm.loadClassData()が呼び出される。
 *    (d)dvm.loadLocalClassData()が呼び出され、バイトコードが返される。
 *
 * (2)他のDVMで動作しているエージェントに対して、バイトコードを渡す。
 *    例えばdvm1で動いているエージェントに、dvm0が渡す場合、
 *    (a)エージェントはDashClassLoader.loadClass()を呼ぶ。
 *    (b)DashClassLoader.findClass()が呼び出される。
 *    (c)dvm.loadClassData()が呼び出される。
 *    (d)dvm.loadRemoteClassData()が呼び出される。
 *    (e)dvm1はdvm0に_getBytecodeを送る。
 *    (f)dvm0はバイトコードを読み込む。
 *    (d)dvm0はdvm1に_putBytecodeを送る。
 *    (e)dvm1はエージェントにバイトコードを渡す。
 *
 * DASH-1.1では、バイトコードは環境(AdipsEnv)のRMIサーバに直接リクエスト
 * していた(Moongateとは、このRMIサーバである)。
 *
 * DASH-2版では、バイトコードはメッセージで送信することにした。
 * メッセージさえ届く場所ならバイトコードが取得できる。
 *
 * public Class java.lang.ClassLoader.load(String name, boolean resolve)
 * をオーバーライドしてないので、
 * JARファイルから起動しない場合JAVAのデフォルトクラスローダを使う。
 */
class DashClassLoader extends ClassLoader {

  /** DVM */
  private DVM dvm;

  /** バイトコードを読み込む環境 */
  private String origin;

  /**
   * コンストラクタ
   */
  DashClassLoader(DVM dvm, String origin) {
    super();
    this.dvm = dvm;
    this.origin = origin;
  }

  /**
   * クラスを探す。
   * java.lang.ClassLoader.loadClass(String name, boolean resolve)の
   * APIdocsの解説にあるようにこのメソッドが呼び出されるのは、
   * classnameで表されるクラスが、
   * (1)すでにロードされていなく、かつ、
   * (2)クラスパス上にない場合
   * である。したがって、
   * ・JARファイルによる起動(% java -jar Workplace.jarなど)の場合、
   *   かならずこのメソッドが呼び出される(JARの中にベースプロセス/メソッドの
   *   クラスファイルがふくまれないため)
   * ・JARファイル以外の起動(% java dash.Workplace)の場合、
   *   クラスパス上にベースプロセスのファイルがあれば、
   *   このメソッドは呼び出されない(システム標準のをつかう)
   */
  public Class findClass(String name) throws ClassNotFoundException {
    byte[] bytes = null;
    try {
      bytes = dvm.loadClassData(name, origin);
      
    } catch (Exception e) {
      throw new ClassNotFoundException(name, e);
    }
	
   // if (bytes==null)
   //   throw new ClassNotFoundException(name);
   // else {
   try{
       return defineClass(name, bytes, 0, bytes.length);
   }catch(NoClassDefFoundError e1){
       return defineClass("dash.DammyBP", bytes, 0, bytes.length);
   }
  }
}
