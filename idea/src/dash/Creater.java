package dash;

import java.util.*;
import java.io.*;

/**
 * Swing�̃��j���[��{�^���Ŏ��s����Ƃ��̃L���[�B
 * actionPerformed()������s�ł��Ȃ�(�f�b�h���b�N����)�̂������B
 * 2�̋@�\�����B
 *
 * (1)�t�@�C������DASH�t�@�C����ǂݍ��݁A�G�[�W�F���g�𐶐�����B
 * (2)ACL-Editor��Send�{�^�����������Ƃ��ɁA���b�Z�[�W���L���[�ɂ����B
 */
class Creater extends Thread {

  private DVM dvm;
  private Vector queue;

  Creater(ThreadGroup threadGroup, DVM dvm) {
    super(threadGroup, "dash.Creater");
    this.dvm = dvm;
    queue = new Vector();
  }

  void create(File file) {
    synchronized(queue) {
      queue.addElement(file);
      queue.notify();
    }
  }

  void send(DashMessage msg) {
    synchronized(queue) {
      queue.addElement(msg);
     /* wait() ���\�b�h�ɂ���đҋ@���̃X���b�h�� 1 �ĊJ
	����������� ���Y�I�u�W�F�N�g�̑ҋ@�v�[�����̔C�ӂ̈�̃X���b�h���ĊJ */
      queue.notify();
    }
  }

  public void run() {
    while (true) {
      if (queue.isEmpty()) {
        synchronized(queue) {
          try {
            queue.wait();
          } catch (InterruptedException e) {
            continue;
          }
        }
      } 
      Object object = queue.remove(0);
      if (object instanceof File)
        dvm.loadAgent((File)object);
      else if (object instanceof DashMessage){
        dvm.sendMessageFromUser((DashMessage)object);
      }
    }
  }
}
