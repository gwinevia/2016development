package dash;

import java.util.*;

/**
 * DVM�̒ʐM���W���[���̃C���^�t�F�[�X�B
 * �������������N���X�́A
 * (1)�N���XRmiModule   (DASH-1.1�����̒ʐM���W���[��)
 * (2)�N���XCysolModule (�T�C�o�[�\�����[�V�����Y�̋@�\�����ʐM���W���[��)
 */
interface ComInterface {

  /**
   * ����DVM�̏����o�͂���B
   */
  void printInfo();

  /**
   * ����DVM���邢�͑���DVM�Ƀ��b�Z�[�W�𑗐M����B
   * @return ����������true
   */
  boolean sendMsg(DashMessage dm);

  /** ���b�Z�[�W������̂�҂�(�u���b�L���O) */
  DashMessage waitMsg();

  /** ������~���� */
  void finalizeModule();

  /**
   * DVM�̖��O��Ԃ��B
   * DVM�̖��O�͒ʐM���W���[�������߂�B
   */
  String getDVMname();

  /**
   * dvmname�Ŏw�肳�ꂽDVM�����݂��邩�𒲂ׂ�B
   * @return ���݂���ꍇ�Atrue�B
   */
  boolean checkDVM(String dvmname);

  /**
   * �G�[�W�F���g�����l�[���T�[�o�ɓo�^����B
   */
  int registerAgent(String name, String envname, String origin, String function, String comment);


  /**
   * �G�[�W�F���g�����l�[���T�[�o����폜����B
   */
  int unregisterAgent(String name, String envname);


  /**
   * �G�[�W�F���g���ړ��������Ƃ��l�[���T�[�o�ɒʒm����B
   */
  int moveAgent(String name, String oldEnvname, String newEnvname);


  /**
   * selector�Ŏw�肳�ꂽ�G�[�W�F���g�̏���Ԃ��B
   * @param selector ��̗v�f�́AString[2]�B
   * [0]�́A�����w��q�B":name", ":environment"�ȂǁB
   * [1]�́A���̑����l�B
   * @return �}�b�`������̂��Ȃ��ꍇ�A���Vector�B
   * �}�b�`�����ꍇ�AString[9]��v�f�Ƃ���Vector�B
   * �l�[���T�[�o���g��Ȃ��ꍇ��A
   * �w�肵���l�[���T�[�o�����݂��Ȃ����A���̃z�X�g�Ńl�[���T�[�o��
   * �N�����Ă��Ȃ��ꍇ�Anull�B
   * �z��̓Y���̈Ӗ��ɂ��ẮANSInterface�Q�ƁB
   */
  public Vector lookup(String[][] selector);

  /** ADD COSMOS */
  public NSInterface getNameServer() ;
  public String getNameServerErrMsg() ;

}
