package dash;

/**
 * �x�[�X�v���Z�X�Ɗg�����\�b�h��ǂݍ��ނ��߂̃N���X���[�_�B
 * JAR�t�@�C������DASH���N�������ꍇ�A
 * �f�t�H���g�̃N���X���[�_��JAR�t�@�C���ȊO����N���X�t�@�C����ǂ܂Ȃ����߂�
 * ���̃N���X���K�v�ƂȂ�B
 *
 * �N���X���[�_DashClassLoader�̃C���X�^���X�͊e�G�[�W�F���g�������A
 * �N���X���[�_�̎��ۂ̋@�\��DVM�����B
 * DVM�͎��̂悤�ɓ��삷��B
 *
 * (1)�����̏�œ��삵�Ă���G�[�W�F���g�ɑ΂��āA�o�C�g�R�[�h��n���B
 *    �Ⴆ��dvm1�œ����Ă���G�[�W�F���g�ɁAdvm1���n���ꍇ�A
 *    (a)�G�[�W�F���g��DashClassLoader.loadClass()���ĂԁB
 *    (b)DashClassLoader.findClass()���Ăяo�����B
 *    (c)dvm.loadClassData()���Ăяo�����B
 *    (d)dvm.loadLocalClassData()���Ăяo����A�o�C�g�R�[�h���Ԃ����B
 *
 * (2)����DVM�œ��삵�Ă���G�[�W�F���g�ɑ΂��āA�o�C�g�R�[�h��n���B
 *    �Ⴆ��dvm1�œ����Ă���G�[�W�F���g�ɁAdvm0���n���ꍇ�A
 *    (a)�G�[�W�F���g��DashClassLoader.loadClass()���ĂԁB
 *    (b)DashClassLoader.findClass()���Ăяo�����B
 *    (c)dvm.loadClassData()���Ăяo�����B
 *    (d)dvm.loadRemoteClassData()���Ăяo�����B
 *    (e)dvm1��dvm0��_getBytecode�𑗂�B
 *    (f)dvm0�̓o�C�g�R�[�h��ǂݍ��ށB
 *    (d)dvm0��dvm1��_putBytecode�𑗂�B
 *    (e)dvm1�̓G�[�W�F���g�Ƀo�C�g�R�[�h��n���B
 *
 * DASH-1.1�ł́A�o�C�g�R�[�h�͊�(AdipsEnv)��RMI�T�[�o�ɒ��ڃ��N�G�X�g
 * ���Ă���(Moongate�Ƃ́A����RMI�T�[�o�ł���)�B
 *
 * DASH-2�łł́A�o�C�g�R�[�h�̓��b�Z�[�W�ő��M���邱�Ƃɂ����B
 * ���b�Z�[�W�����͂��ꏊ�Ȃ�o�C�g�R�[�h���擾�ł���B
 *
 * public Class java.lang.ClassLoader.load(String name, boolean resolve)
 * ���I�[�o�[���C�h���ĂȂ��̂ŁA
 * JAR�t�@�C������N�����Ȃ��ꍇJAVA�̃f�t�H���g�N���X���[�_���g���B
 */
class DashClassLoader extends ClassLoader {

  /** DVM */
  private DVM dvm;

  /** �o�C�g�R�[�h��ǂݍ��ފ� */
  private String origin;

  /**
   * �R���X�g���N�^
   */
  DashClassLoader(DVM dvm, String origin) {
    super();
    this.dvm = dvm;
    this.origin = origin;
  }

  /**
   * �N���X��T���B
   * java.lang.ClassLoader.loadClass(String name, boolean resolve)��
   * APIdocs�̉���ɂ���悤�ɂ��̃��\�b�h���Ăяo�����̂́A
   * classname�ŕ\�����N���X���A
   * (1)���łɃ��[�h����Ă��Ȃ��A���A
   * (2)�N���X�p�X��ɂȂ��ꍇ
   * �ł���B���������āA
   * �EJAR�t�@�C���ɂ��N��(% java -jar Workplace.jar�Ȃ�)�̏ꍇ�A
   *   ���Ȃ炸���̃��\�b�h���Ăяo�����(JAR�̒��Ƀx�[�X�v���Z�X/���\�b�h��
   *   �N���X�t�@�C�����ӂ��܂�Ȃ�����)
   * �EJAR�t�@�C���ȊO�̋N��(% java dash.Workplace)�̏ꍇ�A
   *   �N���X�p�X��Ƀx�[�X�v���Z�X�̃t�@�C��������΁A
   *   ���̃��\�b�h�͌Ăяo����Ȃ�(�V�X�e���W���̂�����)
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
