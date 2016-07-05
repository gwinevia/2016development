package dash;

import java.util.*;
import javax.swing.tree.*;

/**
 * DashTreeに表示するモデル。
 * 同期処理も行う。
 */
class DashTreeModel {

  /** ACLエディタノードとパス */
  private DefaultMutableTreeNode aclnode;

  /** 名前とノードの対応。ACLエディタは含まれない。 */
  private Hashtable nodetable;

  /** ルートノード */
  private DefaultMutableTreeNode rootnode;

  /** 環境ノードを格納するテーブル */
  private Hashtable envtable;

  /** wait()するオブジェクトを格納するキュー。*/
  private Vector waitQueue;

  /** 識別用数字 */
  private long lastkey;

  /**
   * コンストラクタ
   */
  DashTreeModel(String dvmname) {
    nodetable = new Hashtable();
    
    rootnode = new DefaultMutableTreeNode("Root");

    DefaultMutableTreeNode node =new DefaultMutableTreeNode(dvmname);
    envtable = new Hashtable();
    envtable.put(dvmname, node);
    rootnode.add(node);

    aclnode = new DefaultMutableTreeNode(DashTree.ACL_NODE_NAME);
    node.add(aclnode);

    waitQueue = new Vector();
    lastkey = 0L;
  }
  
  /** ルートノードを返す */
  DefaultMutableTreeNode getRootnode() {
    return rootnode;
  }

  /** 環境ノードを返す */
  DefaultMutableTreeNode getEnvnode() {
    Enumeration e = envtable.elements();
    return (DefaultMutableTreeNode)e.nextElement();
  }

  /** ACLノードを返す */
  DefaultMutableTreeNode getACLnode() {
    return aclnode;
  }

  /**
   * 指定された名前のエージェントのノードを返す。
   * ACLノードの場合もある。
   */
  DefaultMutableTreeNode getAgentnode(String name) {
    if (name.equals(DashTree.ACL_NODE_NAME))
      return aclnode;
    else
      return (DefaultMutableTreeNode)nodetable.get(name);
  }

  /**
   * エージェントのノードをEnumerationで返す。
   */
  Enumeration getAllnodes() {
    return nodetable.keys();
  }


  /** エージェントのノードを追加する */
  DefaultMutableTreeNode addAgentnode(String name) {
    if (nodetable.get(name) != null) {
      System.err.println("★★★★★★");
      Thread.dumpStack();
    }

    DefaultMutableTreeNode agnode = new DefaultMutableTreeNode(name);
    DefaultMutableTreeNode envnode = getEnvnode();
    envnode.add(agnode);
    nodetable.put(name, agnode);
    return envnode;
  }

  /** エージェントのノードを削除する */
  DefaultMutableTreeNode removeAgentnode(String name) {
    DefaultMutableTreeNode agnode = getAgentnode(name);
    DefaultMutableTreeNode parent =
      (DefaultMutableTreeNode)agnode.getParent();
    parent.remove(agnode);
    nodetable.remove(name);
    return parent;
  }

  /**
   * 順番が回って来るまでブロックする。
   * これを呼び出し処理を実行した後は、nextTurn()を呼び出す。
   * @see nextTurn()
   */
  Long waitTurn() {
    Long key = null;
    synchronized (waitQueue) {
      key = new Long(lastkey);
      lastkey++;
      if (lastkey > Long.MAX_VALUE)
        lastkey = 0;

      waitQueue.add(key);
      if (lastkey > 2 || waitQueue.size() == 1)
        return key;
      /*ここから*/
      do {
        try { waitQueue.wait(); } catch (InterruptedException e) { }
      } while (waitQueue.indexOf(key) > 0);
      /*ここまで*/
    }
    return key;
  }

  /**
   * 処理が終わったので、次の人に知らせる。
   */
  void nextTurn(Long key) {
    synchronized (waitQueue) {
      waitQueue.remove(key);
      waitQueue.notifyAll();
    }
  }

  /**
   * 順番が回って来るまでブロックする。
   * これを呼び出し処理を実行した後は、nextTurn()を呼び出す。
   * @see nextTurn()
   */
  void _waitTurn() {
    Object key = null;

    synchronized (waitQueue) {
      if (!waitQueue.isEmpty()) {
        key = new String("k");   // wait()するキー
        waitQueue.add(key);
      } else
        waitQueue.add("d");      // ダミーのキー
    }

    if (key != null)
      synchronized(key) {
        try { key.wait(); } catch (InterruptedException e) { }
      }
  }

  /**
   * 処理が終わったので、次の人に知らせる。
   */
  void _nextTurn() {
    Object key = null;

    synchronized (waitQueue) {
      waitQueue.remove(0); // 自分のキーを削除する

      if (!waitQueue.isEmpty())
        key = waitQueue.elementAt(0); // 次の人のキーは削除しない
    }

    if (key != null)
      synchronized(key) { key.notify(); } // 次の人のキー
  }  
}
