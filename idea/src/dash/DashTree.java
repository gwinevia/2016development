package dash;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * ���b�Z�[�W��\������JTree�B�\������@�\�݂̂����B
 * ���f����DashTreeModel�ɂ܂Ƃ߂Ă���B
 */
class DashTree extends JTree {

  /** Newif */
  private Newif newif;

  /** ���f�� */
  private DashTreeModel dashTreeModel;

  /** settext�p�e�[�u�� */
  private Hashtable textTable;

  /** ACL�G�f�B�^�̃m�[�h�� */
  public static final String ACL_NODE_NAME = DashMessage.IF;

  /** ���b�Z�[�W�̕�����\���萔 */
  private final static int ERROR           =-1;
  private final static int NO_MESSAGE      = 0; // ���b�Z�[�W��\�����Ȃ�
  private final static int AGENT_TO_AGENT  = 1;
  private final static int AGENT_TO_AGENTS = 2; // �u���[�h�L���X�g
  private final static int AGENT_TO_ENV    = 3; // ���̊���
  private final static int AGENT_TO_ACL    = 4; // ���̊���
  private final static int   ENV_TO_AGENT  = 5; // ���̊�����
  private final static int   ENV_TO_AGENTS = 6; // �u���[�h�L���X�g
  private final static int   ENV_TO_ACL    = 7;
  private final static int   ACL_TO_AGENT  = 8;
  private final static int   ACL_TO_AGENTS = 9;
  private final static int   ACL_TO_ENV    =10;
  private final static int   DVM_TO_DVM    =11; //DVM���瑼��DVM�ցB
  
  /** ���b�Z�[�W�̕��� */
  private int direction;

  /** ���M�҃G�[�W�F���g */
  private String sender;

  /** ��M�҃G�[�W�F���g */
  private String receiver;

  /** �p�t�H�[�}�e�B�u */
  private String performative;

  /** �R���X�g���N�^ */
  public DashTree(Newif newif, DashTreeModel dashTreeModel) {
    super(dashTreeModel.getRootnode());
    this.newif = newif;
    this.dashTreeModel = dashTreeModel;
    direction = NO_MESSAGE;
    sender = receiver = null;
    toggleClickCount = 3;
    textTable = new Hashtable();
  }

  /** Tree���J�� */
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

  /** �\�� */
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

  /** ���b�Z�[�W�̖���\������悤�ݒ肷�� */
  void showMsg(DashMessage m, String envname, final Long key) {
    boolean fromACL = (m.from.equals(DashMessage.IF));
    boolean toACL = (m.to.equals(DashMessage.IF));
    boolean fromSomewhere =m.departure!=null && !envname.equals(m.departure);
    boolean toSomewhere   =m.arrival  !=null && !envname.equals(m.arrival);
    boolean broadcast = m.to.equals(DashMessage.BCAST);
    boolean fromDVM = (m.from.equals(DashMessage.DVM));
    boolean instantiate = (m.performative.equals(DashMessage.INSTANTIATE));
    boolean createInstance = (m.performative.equals(DashMessage.CREATEINSTANCE));

    // ���M�҂����肵�Ă���A��M�҂����肷��B
    // ���Ɋւ��锻�ʂ��ŗD��ɂ���B

    direction = ERROR;
    sender = receiver = null;
    if (fromSomewhere || fromDVM) {    // ���̊����邢��DVM����I

      if (toSomewhere) {                 // (11)DVM���瑼��DVM�ցI
        if (createInstance) {              // _createInstance
          direction = AGENT_TO_ENV;
          sender = m.getOtherAttributes(":target");
        } else
          direction = DVM_TO_DVM;          // no_such_agent�Ȃ�
      } else if (toACL) {                // (7)ACL�G�f�B�^�ցI
        direction = ENV_TO_ACL;
        receiver = ACL_NODE_NAME;
      } else if (broadcast) {            // (6)�S�ẴG�[�W�F���g�ցI
        direction = ENV_TO_AGENTS;
      } else if (instantiate) {          // _instantiate
        direction = ENV_TO_AGENT;
        receiver = m.getOtherAttributes(":target");
      } else {                           // (5)�G�[�W�F���g�ցI
        direction = ENV_TO_AGENT;
        receiver = m.to;
      }

    } else if (fromACL) {             // ACL����I

      if (toSomewhere) {                 // (10)���̊��ցI
        direction = ACL_TO_ENV;
        sender = ACL_NODE_NAME;
      } else if (broadcast) {            // (9)�S�ẴG�[�W�F���g�ցI
        direction = ACL_TO_AGENTS;
        sender = ACL_NODE_NAME;
      } else if (!toACL) {               // (8)�G�[�W�F���g�ցI
        direction = ACL_TO_AGENT;
        sender = ACL_NODE_NAME;
        receiver = m.to;
      }

    } else {                          // ���̊��̃G�[�W�F���g����I

      if (toSomewhere) {                 // (3)���̊��ցI
        direction = AGENT_TO_ENV;
        sender = m.from;
      } else if (toACL) {                // (4)ACL�ցI
        direction = AGENT_TO_ACL;
        sender = m.from;
        receiver = ACL_NODE_NAME;
      } else if (broadcast) {            // (2)�S�ẴG�[�W�F���g�ցI
        direction = AGENT_TO_AGENTS;
        sender = m.from;
      } else {                           // (1)�G�[�W�F���g�ցI
        direction = AGENT_TO_AGENT;
        sender = m.from;
        receiver = m.to;
      }
    }
    
    /* cout
    // �ړ�����Ƃ���create-instance��:from��
    // ���|�W�g���G�[�W�F���g���Ȃ̂Ń��[�N�v���[�X�ɂ͂��Ȃ��B
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
      // DASH�̃G���[�B�\�����������������Ȃ��B
      System.err.println("DashTree.showMsg(): �\���ł��Ȃ����b�Z�[�W:"+
                         m.toString2());

    } else if (direction == DVM_TO_DVM) {
      // �G���[(no_sucn_agent)�B�\�����������������Ȃ��B
      ; 
      System.out.println("����"+m);

    } else {
      // �\������B
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
                System.err.println("��sender="+sender);
            }
            if (receiver!=null) {
              rPath = getPathByName(receiver);
              if (rPath != null)
                makeVisible(rPath);
              else
                System.err.println("��receiver="+receiver);
            }
            repaint();
          }};
      SwingUtilities.invokeLater(r);

      // ��������
      newif.waitStepButton();
    }

    // ��n��
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
      return; // ���[����Ă���ꍇ�͕`���Ȃ��B

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
        return;  // 1�ł����[����Ă���ꍇ�͕`���Ȃ��B
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
      return; // �Ȃ�null������(2002/10/03)

    TreePath path = getPathByName(sender);
    if (!isVisible(path))
      return; // ���[����Ă���ꍇ�͕`���Ȃ��B

    int y1 = drawArrow(g, sender, true);
    int y2 = drawEnvArrow(g, false);
    drawVLine(g, y1, y2);
  }

  /** (5)ENV_TO_AGENT
      (7)ENV_TO_ACL    */
  private void showEnvToAgent(Graphics g) {
    if (receiver == null)
      return; // �Ȃ�null������(2002/10/03)

    TreePath path = getPathByName(receiver);
    if (!isVisible(path))
      return; // ���[����Ă���ꍇ�͕`���Ȃ��B

    int y1 = drawArrow(g, receiver, false);
    int y2 = drawEnvArrow(g, true);
    drawVLine(g, y2, y1);
  }

  /**
   * name�Ŏw�肳�ꂽ�G�[�W�F���g�̐�������`�悷��B
   * @param isSender true�̏ꍇ�A�E�����̖���`���B
   * @return �`�����ꍇ��Y���W�A�`���Ȃ��ꍇ��-1
   */
  private int drawArrow(Graphics g, String name, boolean isSender) {
    // path�͎��[����Ă��Ȃ����Ƃ��m�F���Ă���B
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
   * envname�Ŏw�肳�ꂽ���̐�������`�悷��B
   * @param isSender true�Ȃ�ォ�牺�֕`���B
   * @return Y���W
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


  /** y1����y2�ɐ����̐������� */
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

  /** �p�t�H�[�}�e�B�u�ƈ͂ݐ���`�� */
  private void drawPerformative(Graphics g, Rectangle r) {
    g.drawRect(r.x-1, r.y, r.width+1, r.height);
    g.drawString(performative, r.x+r.width+3, r.y+r.height-4);
  }

  /** �w�肳�ꂽ���O�̃G�[�W�F���g�m�[�horACL�m�[�h�̃p�X��Ԃ� */
  private TreePath getPathByName(String name) {
    DefaultTreeModel treeModel = (DefaultTreeModel)getModel();
    DefaultMutableTreeNode node = dashTreeModel.getAgentnode(name);
    TreeNode nodes[] = treeModel.getPathToRoot(node);
    if (nodes == null) {
      System.err.println("��getPathByName: nullnode: "+name+"��"+node);
      Thread.dumpStack();
      return null;
    } else if (nodes.length == 0) {
      System.err.println("��getPathByName: 0sizenode: "+name+"��"+node);
      Thread.dumpStack();
      return null;
    }
    return new TreePath(nodes);
  }

  /** �e�L�X�g���Z�b�g������������肷�� */
  void settext(String agent, String s) {
    if (s.equals(""))
      textTable.remove(agent);
    else
      textTable.put(agent, s);

    Runnable r = new Runnable() { public void run() { repaint(); }};
    SwingUtilities.invokeLater(r);
  }

  /** �G�[�W�F���g���Ȃ��Ȃ�̂Ńe�L�X�g���폜���� */
  void removeEntry(String name) {
    textTable.remove(name);
  }

}

