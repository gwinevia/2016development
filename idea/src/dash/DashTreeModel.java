package dash;

import java.util.*;
import javax.swing.tree.*;

/**
 * DashTree�ɕ\�����郂�f���B
 * �����������s���B
 */
class DashTreeModel {

  /** ACL�G�f�B�^�m�[�h�ƃp�X */
  private DefaultMutableTreeNode aclnode;

  /** ���O�ƃm�[�h�̑Ή��BACL�G�f�B�^�͊܂܂�Ȃ��B */
  private Hashtable nodetable;

  /** ���[�g�m�[�h */
  private DefaultMutableTreeNode rootnode;

  /** ���m�[�h���i�[����e�[�u�� */
  private Hashtable envtable;

  /** wait()����I�u�W�F�N�g���i�[����L���[�B*/
  private Vector waitQueue;

  /** ���ʗp���� */
  private long lastkey;

  /**
   * �R���X�g���N�^
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
  
  /** ���[�g�m�[�h��Ԃ� */
  DefaultMutableTreeNode getRootnode() {
    return rootnode;
  }

  /** ���m�[�h��Ԃ� */
  DefaultMutableTreeNode getEnvnode() {
    Enumeration e = envtable.elements();
    return (DefaultMutableTreeNode)e.nextElement();
  }

  /** ACL�m�[�h��Ԃ� */
  DefaultMutableTreeNode getACLnode() {
    return aclnode;
  }

  /**
   * �w�肳�ꂽ���O�̃G�[�W�F���g�̃m�[�h��Ԃ��B
   * ACL�m�[�h�̏ꍇ������B
   */
  DefaultMutableTreeNode getAgentnode(String name) {
    if (name.equals(DashTree.ACL_NODE_NAME))
      return aclnode;
    else
      return (DefaultMutableTreeNode)nodetable.get(name);
  }

  /**
   * �G�[�W�F���g�̃m�[�h��Enumeration�ŕԂ��B
   */
  Enumeration getAllnodes() {
    return nodetable.keys();
  }


  /** �G�[�W�F���g�̃m�[�h��ǉ����� */
  DefaultMutableTreeNode addAgentnode(String name) {
    if (nodetable.get(name) != null) {
      System.err.println("������������");
      Thread.dumpStack();
    }

    DefaultMutableTreeNode agnode = new DefaultMutableTreeNode(name);
    DefaultMutableTreeNode envnode = getEnvnode();
    envnode.add(agnode);
    nodetable.put(name, agnode);
    return envnode;
  }

  /** �G�[�W�F���g�̃m�[�h���폜���� */
  DefaultMutableTreeNode removeAgentnode(String name) {
    DefaultMutableTreeNode agnode = getAgentnode(name);
    DefaultMutableTreeNode parent =
      (DefaultMutableTreeNode)agnode.getParent();
    parent.remove(agnode);
    nodetable.remove(name);
    return parent;
  }

  /**
   * ���Ԃ�����ė���܂Ńu���b�N����B
   * ������Ăяo�����������s������́AnextTurn()���Ăяo���B
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
      /*��������*/
      do {
        try { waitQueue.wait(); } catch (InterruptedException e) { }
      } while (waitQueue.indexOf(key) > 0);
      /*�����܂�*/
    }
    return key;
  }

  /**
   * �������I������̂ŁA���̐l�ɒm�点��B
   */
  void nextTurn(Long key) {
    synchronized (waitQueue) {
      waitQueue.remove(key);
      waitQueue.notifyAll();
    }
  }

  /**
   * ���Ԃ�����ė���܂Ńu���b�N����B
   * ������Ăяo�����������s������́AnextTurn()���Ăяo���B
   * @see nextTurn()
   */
  void _waitTurn() {
    Object key = null;

    synchronized (waitQueue) {
      if (!waitQueue.isEmpty()) {
        key = new String("k");   // wait()����L�[
        waitQueue.add(key);
      } else
        waitQueue.add("d");      // �_�~�[�̃L�[
    }

    if (key != null)
      synchronized(key) {
        try { key.wait(); } catch (InterruptedException e) { }
      }
  }

  /**
   * �������I������̂ŁA���̐l�ɒm�点��B
   */
  void _nextTurn() {
    Object key = null;

    synchronized (waitQueue) {
      waitQueue.remove(0); // �����̃L�[���폜����

      if (!waitQueue.isEmpty())
        key = waitQueue.elementAt(0); // ���̐l�̃L�[�͍폜���Ȃ�
    }

    if (key != null)
      synchronized(key) { key.notify(); } // ���̐l�̃L�[
  }  
}
