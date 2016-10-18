package dash;

import java.util.*;
import java.io.*;

/**
 * Swingのメニューやボタンで実行するときのキュー。
 * actionPerformed()から実行できない(デッドロックする)のを避ける。
 * 2つの機能を持つ。
 *
 * (1)ファイルからDASHファイルを読み込み、エージェントを生成する。
 * (2)ACL-EditorのSendボタンを押したときに、メッセージをキューにいれる。
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
     /* wait() メソッドによって待機中のスレッドを 1 つ再開
	言い換えると 当該オブジェクトの待機プール内の任意の一つのスレッドを再開 */
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
