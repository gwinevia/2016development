package dash;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * メッセージを表示するJTree。表示する機能のみを持つ。
 * モデルはDashTreeModelにまとめてある。
 */
class DashTree extends JTree {

  /** Newif */
  private Newif newif;

  /** モデル */
  private DashTreeModel dashTreeModel;

  /** settext用テーブル */
  private Hashtable textTable;

  /** ACLエディタのノード名 */
  public static final String ACL_NODE_NAME = DashMessage.IF;

  /** メッセージの方向を表す定数 */
  private final static int ERROR           =-1;
  private final static int NO_MESSAGE      = 0; // メッセージを表示しない
  private final static int AGENT_TO_AGENT  = 1;
  private final static int AGENT_TO_AGENTS = 2; // ブロードキャスト
  private final static int AGENT_TO_ENV    = 3; // 他の環境へ
  private final static int AGENT_TO_ACL    = 4; // 他の環境へ
  private final static int   ENV_TO_AGENT  = 5; // 他の環境から
  private final static int   ENV_TO_AGENTS = 6; // ブロードキャスト
  private final static int   ENV_TO_ACL    = 7;
  private final static int   ACL_TO_AGENT  = 8;
  private final static int   ACL_TO_AGENTS = 9;
  private final static int   ACL_TO_ENV    =10;
  private final static int   DVM_TO_DVM    =11; //DVMから他のDVMへ。
  
  /** メッセージの方向 */
  private int direction;

  /** 送信者エージェント */
  private String sender;

  /** 受信者エージェント */
  private String receiver;

  /** パフォーマティブ */
  private String performative;

  /** コンストラクタ */
  public DashTree(Newif newif, DashTreeModel dashTreeModel) {
    super(dashTreeModel.getRootnode());
    this.newif = newif;
    this.dashTreeModel = dashTreeModel;
    direction = NO_MESSAGE;
    sender = receiver = null;
    toggleClickCount = 3;
    textTable = new Hashtable();
  }

  /** Treeを開く */
  void expand() {
    DefaultTreeModel treeModel = (DefaultTreeModel)getModel();
    TreeNode nodes[] = treeModel.getPathToRoot(dashTreeModel.getEnvnode());
    TreePath path = new TreePath(nodes);
    this.expandPath(path);
  }

  private void drawText(Graphics g) {
    for (Enumeration e = textTable.keys(); e.hasMoreElements(); ) {
      String name = (String)e.nextElement();
      String text = (String)textTable.get(name);
      TreePath path = getPathByName(name);
      Rectangle rect = getPathBounds(path);
      if (rect != null) {
        if (direction!=NO_MESSAGE && sender.equals(name))
          text = performative + " " + text;
        g.drawString(text, rect.x+rect.width+3, rect.y+rect.height-4);
      }
    }
  }

  /** 表示 */
  public void paint(Graphics g) {
    super.paint(g);
    g.setColor(Color.red);
    drawText(g);
    g.setColor(Color.blue);
    switch (direction) {
    case AGENT_TO_AGENT:  showAgentToAgent(g);  break;  // (1)
    case AGENT_TO_AGENTS: showAgentToAgents(g); break;  // (2)
    case AGENT_TO_ENV:    showAgentToEnv(g);    break;  // (3)
    case AGENT_TO_ACL:    showAgentToAgent(g);  break;  // (4)
    case   ENV_TO_AGENT:  showEnvToAgent(g);    break;  // (5)
    case   ENV_TO_AGENTS: showAgentToAgents(g); break;  // (6)
    case   ENV_TO_ACL:    showEnvToAgent(g);    break;  // (7)
    case   ACL_TO_AGENT:  showAgentToAgent(g);  break;  // (8)
    case   ACL_TO_AGENTS: showAgentToAgents(g); break;  // (9)
    case   ACL_TO_ENV:    showAgentToEnv(g);    break;  // (10)
    default: break;
    }
  }

  /** メッセージの矢印を表示するよう設定する */
  void showMsg(DashMessage m, String envname, final Long key) {
    boolean fromACL = (m.from.equals(DashMessage.IF));
    boolean toACL = (m.to.equals(DashMessage.IF));
    boolean fromSomewhere =m.departure!=null && !envname.equals(m.departure);
    boolean toSomewhere   =m.arrival  !=null && !envname.equals(m.arrival);
    boolean broadcast = m.to.equals(DashMessage.BCAST);
    boolean fromDVM = (m.from.equals(DashMessage.DVM));
    boolean instantiate = (m.performative.equals(DashMessage.INSTANTIATE));
    boolean createInstance = (m.performative.equals(DashMessage.CREATEINSTANCE));

    // 送信者を決定してから、受信者を決定する。
    // 環境に関する判別を最優先にする。

    direction = ERROR;
    sender = receiver = null;
    if (fromSomewhere || fromDVM) {    // 他の環境あるいはDVMから！

      if (toSomewhere) {                 // (11)DVMから他のDVMへ！
        if (createInstance) {              // _createInstance
          direction = AGENT_TO_ENV;
          sender = m.getOtherAttributes(":target");
        } else
          direction = DVM_TO_DVM;          // no_such_agentなど
      } else if (toACL) {                // (7)ACLエディタへ！
        direction = ENV_TO_ACL;
        receiver = ACL_NODE_NAME;
      } else if (broadcast) {            // (6)全てのエージェントへ！
        direction = ENV_TO_AGENTS;
      } else if (instantiate) {          // _instantiate
        direction = ENV_TO_AGENT;
        receiver = m.getOtherAttributes(":target");
      } else {                           // (5)エージェントへ！
        direction = ENV_TO_AGENT;
        receiver = m.to;
      }

    } else if (fromACL) {             // ACLから！

      if (toSomewhere) {                 // (10)他の環境へ！
        direction = ACL_TO_ENV;
        sender = ACL_NODE_NAME;
      } else if (broadcast) {            // (9)全てのエージェントへ！
        direction = ACL_TO_AGENTS;
        sender = ACL_NODE_NAME;
      } else if (!toACL) {               // (8)エージェントへ！
        direction = ACL_TO_AGENT;
        sender = ACL_NODE_NAME;
        receiver = m.to;
      }

    } else {                          // この環境のエージェントから！

      if (toSomewhere) {                 // (3)他の環境へ！
        direction = AGENT_TO_ENV;
        sender = m.from;
      } else if (toACL) {                // (4)ACLへ！
        direction = AGENT_TO_ACL;
        sender = m.from;
        receiver = ACL_NODE_NAME;
      } else if (broadcast) {            // (2)全てのエージェントへ！
        direction = AGENT_TO_AGENTS;
        sender = m.from;
      } else {                           // (1)エージェントへ！
        direction = AGENT_TO_AGENT;
        sender = m.from;
        receiver = m.to;
      }
    }
    
    /* cout
    // 移動するときのcreate-instanceの:fromは
    // リポジトリエージェント名なのでワークプレースにはいない。
    // @see Workspace.moveProcess()
    if ((direction==AGENT_TO_ENV    ||        // (3)
         direction==AGENT_TO_ACL    ||        // (4)
         direction==AGENT_TO_AGENTS ||        // (2)
         direction==AGENT_TO_AGENT    ) &&    // (1)
        nodetable.get(sender)==null     &&
        m.dummyFrom!=null)
      sender = m.dummyFrom;
    */

    if (direction == ERROR) {
      // DASHのエラー。表示も同期処理もしない。
      System.err.println("DashTree.showMsg(): 表示できないメッセージ:"+
                         m.toString2());

    } else if (direction == DVM_TO_DVM) {
      // エラー(no_sucn_agent)。表示も同期処理もしない。
      ; 
      System.out.println("★★"+m);

    } else {
      // 表示する。
      performative = m.performative;
      Runnable r = new Runnable() {
          public void run() {
            /*
            if (sender!=null)
              makeVisible(getPathByName(sender));
            if (receiver!=null)
              makeVisible(getPathByName(receiver));
            repaint();
            */
            TreePath sPath=null, rPath=null;
            if (sender!=null) {
              sPath = getPathByName(sender);
              if (sPath != null)
                makeVisible(sPath);
              else
                System.err.println("★sender="+sender);
            }
            if (receiver!=null) {
              rPath = getPathByName(receiver);
              if (rPath != null)
                makeVisible(rPath);
              else
                System.err.println("★receiver="+receiver);
            }
            repaint();
          }};
      SwingUtilities.invokeLater(r);

      // 同期処理
      newif.waitStepButton();
    }

    // 後始末
    direction = NO_MESSAGE;
    Runnable r = new Runnable() {
        public void run() {
          repaint();
          dashTreeModel.nextTurn(key);
        }};
    SwingUtilities.invokeLater(r);
  }

  /** (1)AGENT_TO_AGENT
      (4)AGENT_TO_ACL
      (8)  ACL_TO_AGENT   */
  private void showAgentToAgent(Graphics g) {
    TreePath sPath = getPathByName(sender);
    TreePath rPath = getPathByName(receiver);

    if(!isVisible(sPath) || !isVisible(rPath)) 
      return; // 収納されている場合は描かない。

    int y1 = drawArrow(g, sender, true);
    int y2 = drawArrow(g, receiver, false);
    drawVLine(g, y1, y2);
  }

  /** (2)AGENT_TO_AGENTS
      (6)  ENV_TO_AGENTS
      (9)  ACL_TO_AGENTS  */
  private void showAgentToAgents(Graphics g) {
    TreePath path = null;
    for (Enumeration e = dashTreeModel.getAllnodes(); e.hasMoreElements(); ) {
      path = getPathByName((String)e.nextElement());
      if (!isVisible(path))
        return;  // 1つでも収納されている場合は描かない。
    }

    int y, min, max;
    if (sender == null)
      min = max = drawEnvArrow(g, true);
    else if (sender.equals(ACL_NODE_NAME))
      min = max = drawArrow(g, sender, true);
    else {
      min = Integer.MAX_VALUE;
      max = Integer.MIN_VALUE;
    }
    for (Enumeration e = dashTreeModel.getAllnodes(); e.hasMoreElements(); ) {
      String name = (String)e.nextElement();
      if (name.equals(sender))
        y = drawArrow(g, name, true);
      else
        y = drawArrow(g, name, false);
      min = (y<min) ? y : min;
      max = (y>max) ? y : max;
    }
    if (min!=Integer.MAX_VALUE)
      g.drawLine(5, min, 5, max);
  }

  /** (3)AGENT_TO_ENV
      (10) ACL_TO_ENV */
  private void showAgentToEnv(Graphics g) {
    if (sender == null)
      return; // なんかnullだった(2002/10/03)

    TreePath path = getPathByName(sender);
    if (!isVisible(path))
      return; // 収納されている場合は描かない。

    int y1 = drawArrow(g, sender, true);
    int y2 = drawEnvArrow(g, false);
    drawVLine(g, y1, y2);
  }

  /** (5)ENV_TO_AGENT
      (7)ENV_TO_ACL    */
  private void showEnvToAgent(Graphics g) {
    if (receiver == null)
      return; // なんかnullだった(2002/10/03)

    TreePath path = getPathByName(receiver);
    if (!isVisible(path))
      return; // 収納されている場合は描かない。

    int y1 = drawArrow(g, receiver, false);
    int y2 = drawEnvArrow(g, true);
    drawVLine(g, y2, y1);
  }

  /**
   * nameで指定されたエージェントの水平矢印を描画する。
   * @param isSender trueの場合、右←左の矢印を描く。
   * @return 描けた場合はY座標、描けない場合は-1
   */
  private int drawArrow(Graphics g, String name, boolean isSender) {
    // pathは収納されていないことを確認してある。
    TreePath path = getPathByName(name);
    Rectangle rect = getPathBounds(path);
    int arrowX = rect.x - 2;
    int arrowY = rect.y + rect.height / 2;
    g.drawLine(arrowX, arrowY, 5, arrowY);
    if (isSender) {
      drawPerformative(g, rect);
      if (!name.equals(ACL_NODE_NAME)) {
        g.drawLine(7, arrowY, 10, arrowY-3);
        g.drawLine(7, arrowY, 10, arrowY+3);
      }
    } else {
      g.drawLine(arrowX, arrowY, arrowX-3, arrowY-3);
      g.drawLine(arrowX, arrowY, arrowX-3, arrowY+3);
    }
    return arrowY;
  }
  
  /**
   * envnameで指定された環境の垂直矢印を描画する。
   * @param isSender trueなら上から下へ描く。
   * @return Y座標
   */
  private int drawEnvArrow(Graphics g, boolean isSender) {
    DefaultTreeModel treeModel = (DefaultTreeModel)getModel();
    DefaultMutableTreeNode node = dashTreeModel.getEnvnode();
    TreeNode nodes[] = treeModel.getPathToRoot(node);
    TreePath path = new TreePath(nodes);
    Rectangle rect = getPathBounds(path);
    int arrowX = 5;
    int arrowY = rect.y + rect.height + 1;
    if (isSender) {
      drawPerformative(g, rect);
    } else {
      g.drawLine(arrowX, arrowY, arrowX-3, arrowY+3);
      g.drawLine(arrowX, arrowY, arrowX+3, arrowY+3);
    }
    return arrowY;
  }


  /** y1からy2に垂直の線を引く */
  private void drawVLine(Graphics g, int y1, int y2) {
    g.drawLine(5, y1, 5, y2);
    int len = (y1>y2) ? y1-y2 : y2-y1;
    if (len > 30) {
      int yHead, yTail;
      if (y1<y2) {
        yHead = (y1+y2)/2 + 1; yTail = (y1+y2)/2 - 2;
      } else {
        yHead = (y1+y2)/2 - 2; yTail = (y1+y2)/2 + 1;
      }
      g.drawLine(5, yHead, 2, yTail);
      g.drawLine(5, yHead, 8, yTail);
    }
  }

  /** パフォーマティブと囲み線を描く */
  private void drawPerformative(Graphics g, Rectangle r) {
    g.drawRect(r.x-1, r.y, r.width+1, r.height);
    g.drawString(performative, r.x+r.width+3, r.y+r.height-4);
  }

  /** 指定された名前のエージェントノードorACLノードのパスを返す */
  private TreePath getPathByName(String name) {
    DefaultTreeModel treeModel = (DefaultTreeModel)getModel();
    DefaultMutableTreeNode node = dashTreeModel.getAgentnode(name);
    TreeNode nodes[] = treeModel.getPathToRoot(node);
    if (nodes == null) {
      System.err.println("◆getPathByName: nullnode: "+name+"◆"+node);
      Thread.dumpStack();
      return null;
    } else if (nodes.length == 0) {
      System.err.println("◆getPathByName: 0sizenode: "+name+"◆"+node);
      Thread.dumpStack();
      return null;
    }
    return new TreePath(nodes);
  }

  /** テキストをセットしたり消したりする */
  void settext(String agent, String s) {
    if (s.equals(""))
      textTable.remove(agent);
    else
      textTable.put(agent, s);

    Runnable r = new Runnable() { public void run() { repaint(); }};
    SwingUtilities.invokeLater(r);
  }

  /** エージェントがなくなるのでテキストを削除する */
  void removeEntry(String name) {
    textTable.remove(name);
  }

}

