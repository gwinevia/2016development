package dash;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

/**
 * <p>�^�C�g��: </p>
 * <p>����: </p>
 * <p>���쌠: Copyright (c) 2003</p>
 * <p>��Ж�: </p>
 * @author ������
 * @version 1.0
 */

public abstract class ViewerCanvas2 extends JPanel
implements MouseListener, MouseMotionListener, ActionListener{
  Image buffer;     // �o�b�t�@

  Hashtable agTable;    // �G�[�W�F���g�̏��
        // key: �G�[�W�F���g��
        // value: ViewerAg�̃C���X�^���X

  VIcon envIcon;    // �����̏��
  Hashtable envTable;   // ���̊��̏��(�����̏��͓����ĂȂ�)

  // �G�[�W�F���g�̑ȉ~�̑傫���́A70x40�B
  // whiteOval�́A�����70x50�̋�`�̉����Ɉʒu�����Ă���B
  // ������A�ȉ~�̒��S��(35,25)�łȂ�(35,30)�B
  // �炪���������������̂ŁA�����Ɉʒu�����Ă���B

  private int AGHC  = 30;   // �ȉ~�̒��S�̍���
  int ICONW = 70;   // �A�C�R���̕�
  int ICONH = 50;   // �A�C�R���̍���

                                    // ���[�N�v���[�X�p
  public static int W_AGHC  = 30;   // �ȉ~�̒��S�̍���
  public static int W_ICONW = 70;   // �A�C�R���̕�
  public static int W_ICONH = 50;   // �A�C�R���̍���

                                    // ���|�W�g���p
  public static int R_AGHC  = 30;   // �ȉ~�̒��S�̍���
  public static int R_ICONW = 70;   // �A�C�R���̕�
  public static int R_ICONH = 50;   // �A�C�R���̍���

  public static final int ENVX = 10;   // ���̊G�̍��W
  public static final int ENVY = 10;   // ���̊G�̍��W

  public static final int OENVX = 150; // ���̊��̕\���J�nX���W
  public static final int AGY = 70;    // �G�[�W�F���g�̕\���J�nY���W

  public static final int MOR = 16;    // ���̒��a

  public static final int SPEED = 2;   // ���b�Z�[�W���𓮂�������
               // �傫������Ƒ����Ȃ�
               // 12�̖񐔂ɂ��邱��
  public static int w_bai = 100; // �A�C�R���̕�
  public static int r_bai = 100; // �A�C�R���̕�

  private int flushing = 0; // �`�J�`�J�̉�

  private boolean isRepository;   // ���|�W�g���H

  /**
   * ���샂�[�h.
   * RM_STEP��RM_NONSTOP�̂����ꂩ�̒l�����B
   */
  private volatile String runmode;
  private static final String RM_STEP    = "step";
  private static final String RM_NONSTOP = "non-stop";

  /**
   * runmode��RM_STEP�̎��ɔw�i�̏�Ń}�E�X����������++�����B
   */
  private volatile int stepCount;

  /** �A�j�������p **/
  private volatile String animeSync = "ruriruri";

  /** ���܂ꂽ�A�C�R�� */
  private volatile VIconAg grabbedIcon;

  /** grabbedIcon������񂾈ʒu�ւ̃I�t�Z�b�g */
  private volatile Point grabOffset;

  /**
   * �Ō�̒͂񂾈ʒu.
   * �h���b�O���Ȃ������null�̂܂܁B
   */
  private volatile Point lastGrabPoint;

  /**
   * �I������Ă���A�C�R��.
   * �����I�����Ă��Ȃ��ꍇ��null�B
   */
  private volatile VIcon selectedIcon;

  JLabel stepLabel;    // step�̏�ԕ\���p���x���B
        // Viewer�ɃZ�b�g�����B

//  private Hashtable imagetable;

  Image rImage;     // ���|�W�g���̊G
  Image wImage;     // ������̊G
  Image whiteOval;    // �G�[�W�F���g��\�������ȉ~
  Image whiteOval100;   // �G�[�W�F���g��\�������ȉ~(�{��100%)
  Image whiteOval90;    // �G�[�W�F���g��\�������ȉ~(�{��90%)
  Image whiteOval80;    // �G�[�W�F���g��\�������ȉ~(�{��80%)
  Image whiteOval70;    // �G�[�W�F���g��\�������ȉ~(�{��70%)
  String envname;   // �z�X�g�����܂܂Ȃ�����
  String envhost;   // �z�X�g��

  private String dvmname;   // ADD COSMOS
  Viewer viewer;    // Viewer�̃C���X�^���X
  NewifItface newif;

  Font commentFont;   // �R�����g�p�̃t�H���g

  boolean isKane;     // Kane���ݏ������̃��b�Z�[�W��Kane���b�Z�[�W���ǂ����̃t���O
  final Color kaneColor = Color.blue; // Kane�F
  final Color kaneColorUra = Color.red;

  /** �|�b�v�A�b�v���j���[ */
  private JPopupMenu PopupMenu = null;

  ViewerCanvas2(String dvmname, boolean isR) {
    agTable = new Hashtable();
    envTable = new Hashtable();

    isRepository = isR;

    /* �f�t�H���g��70���̏k�����ŃG�[�W�F���g��\������ */
    if (!isRepository ) {
      w_bai = 80;//70;
      W_ICONW = 70 * w_bai / 100;
      W_ICONH = 50 * w_bai / 100;
      W_AGHC =  30 * w_bai / 100;
    }
    else {
      r_bai = 80;//70;
      R_ICONW = 70 * r_bai / 100;
      R_ICONH = 50 * r_bai / 100;
      R_AGHC =  30 * r_bai / 100;
    }

    this.dvmname = dvmname;	// ADD COSMOS
    runmode = "non-step";
    stepCount = 0;

    // MediaTracker
    rImage = getImage("resources/repository.gif");
    wImage = getImage("resources/workspace.gif");
    whiteOval = getImage("resources/oval.gif");
    whiteOval100 = getImage("resources/oval.gif");
    whiteOval90  = getImage("resources/oval90.gif");
    whiteOval80  = getImage("resources/oval80.gif");
    whiteOval70  = getImage("resources/oval70.gif");

    addMouseListener(this);
    setBackground(Color.lightGray);

    PopupMenu = new JPopupMenu();
    PopupMenu.add(menuItem("�S�ăN���A","CodeCheckResultClear",null));

  }

  //****************************************************************************
  /** ���j���[�A�N�V���� **/
  //****************************************************************************
  private JMenuItem menuItem(String label, String command, Icon icon) {
   JMenuItem item = new JMenuItem(label, icon);
   item.setActionCommand(command);
   item.addActionListener(this);
   return item;
 }
 public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("Save")) {
      System.out.print("AA");
    }
 }


  /** Image��Ԃ��B*/
  private Image getImage(String path) {
    java.net.URL url = this.getClass().getResource(path);
    javax.swing.ImageIcon icon = new javax.swing.ImageIcon(url);
    return icon.getImage();
  }

  /**
   * �_�u���o�b�t�@�̏�����. show()���ꂽ��ɌĂ΂��B
   * show()���Ȃ���peer�����ꂸ�Apeer�����Ȃ���createImage()�ł��Ȃ��B
   */
  public void initialize() {
    buffer = createImage(1000, 700); // �K���Ȑ��l
  }

  /*************************************************************************
                              �E�B���h�E����n
  *************************************************************************/
  /**
   * �o�b�t�@�̓��e���L�����o�X�ɓ]������B
   */
  public void update(Graphics g) {
    paint(g);
  }

  /**
   * �o�b�t�@�̓��e���L�����o�X�ɓ]������B
   */
  public void paint(Graphics g) {
    boolean ok = false;
    while (!ok) {
      try {
        g.drawImage(buffer, 0, 0, this);
        ok = true;
      } catch (NullPointerException e) {
        try { Thread.sleep(500); } catch (InterruptedException ee) { }
      }
    }
  }

  /**
   * �o�b�t�@���N���A���A���Ɛ��������B
   */
  void clear() {

    Graphics bufG = buffer.getGraphics();
    bufG.clearRect(0, 0, 1000, 700);
    drawEnvironments(bufG);
    bufG.drawLine(OENVX-10, 0, OENVX-10, AGY-1);
    bufG.drawLine(OENVX-10, AGY-1, 1000, AGY-1);
  }

  /**
   * ����S�ĕ`���B
   * @param g �`�悷��Graphics
   */
  void drawEnvironments(Graphics g) {
    g.drawImage(envIcon.image, envIcon.x, envIcon.y, this);
    for (Enumeration e = envTable.keys(); e.hasMoreElements(); ) {
      Object o = e.nextElement();
      VIcon icon = (VIcon)(envTable.get(o));
      g.drawImage(icon.image, icon.x, icon.y, this);
    }
  }

  /**
   * �G�[�W�F���g��S���`��
   */
  void drawAgent() {
    Graphics bufG = buffer.getGraphics();
    for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
      VIcon icon = (VIcon)e.nextElement();
      drawIcon(bufG, icon);
    }
  }
  /***********************************************************************
                             Viewer����̌ďo�n
          showMsg(), showNewAgent(), showRemoveAget()�͔r���I����I
  ***********************************************************************/

  /**
   * ���b�Z�[�W��\������.
   * showNewAgent(), showRemoveAgent()�Ɣr���I�ɓ����B
   * @param m ���b�Z�[�W
   * @param toAnotherEnv ���̊��ւ̃��b�Z�[�W�Ȃ�true�B
   *   ���b�Z�[�W�̓��e�������ł��A
   *   �E���M���ł̓G�[�W�F���g������ɑ���B
   *   �E��M���ł͊�����G�[�W�F���g�ɑ���B �Ƃ����Ⴂ�����邽�߂ɕK�v�B
   * @param isR ���̊������|�W�g���Ȃ�true
   */

  public void showMsg(DashMessage m,  boolean isR) {
    synchronized (animeSync) {

      if (m.to.equals(DashMessage.BCAST)) {
        showBroadcastMsg(m, isR);
      }else if (m.departure == null )  {
        showAgToAgMsg(m);
      }else if (m.departure.equals(dvmname ) )  {
        showAgToAgMsg(m);
      }else {
        if (m.departure.equals(dvmname ) ) {
          showAgToAgMsg(m);
        }else {
          String s1 = m.departure;
          String s3 = this.dvmname;
          showEnvToAgMsg(m, isR);
        }
      }
    }
  }

  /**
   * �V�����G�[�W�F���g��ǉ�����.
   * showMsg(), showRemoveAgent()�Ɣr���I�ɓ����B
   * @param m     CreateInstance���b�Z�[�W�A
   *              �܂���null(���|�W�g�����N���XAg�𐶐������ꍇ)
   * @param name  �G�[�W�F���g��
   */
  public void showNewAgent(String origin, String name) {
    synchronized (animeSync) {
      VIconAg agt = new VIconAg(name,isRepository);
      agt.image = createAgentImage(name);
      agt.parentname = origin;
      agTable.put(name, agt);
      //System.out.println(origin);
      locate(origin, agt);
    }
  }
  /**
   * �G�[�W�F���g���폜����.
   * showMsg(), showNewAgent()�Ɣr���I�ɓ����B
   * @param agName �폜����G�[�W�F���g��
   */
  public void showRemoveAgent(String agName) {
    synchronized (animeSync) {
      removeAgent(agName);
    }
  }

  abstract void removeAgent(String agName);

  /**
   * �G�[�W�F���g�̃A�C�R���𐮗񂷂�B
   * ViewerCanvasW�́A���ꂪ�Ă΂��B
   * ViewerCanvasR�́A���O�̂��Ă΂��B
   */
  public void gridSort() {
    gridSortIcon();
  }

  abstract void gridSortIcon();

  /**
   * �G�[�W�F���g�ɃR�����g��t����B
   * @param name �G�[�W�F���g��
   * @param comment �R�����g
   */
  public void commentAgent(String name, String comment) {
    VIconAg target = (VIconAg)agTable.get(name);
  }
  /***********************************************************************
                          �T�u�N���X�ˑ��ďo�n
  ***********************************************************************/

  /** �G�[�W�F���g�̈ʒu�����肷��B*/
  abstract void locate(String origin, VIconAg agt);

  /**
   * �}�l�[�W�����w�肷��
   */
  abstract void specifyManager(DashMessage m);

  /** */
  abstract void createRootChildren();

  abstract void initStartXY();

  /***********************************************************************
                         showMsg()����̌ďo�n
  ***********************************************************************/

  /**
   * �G�[�W�F���g�܂��͊����瑼�̊��ւ̃��b�Z�[�W��\������B
   *
   * @param m ���b�Z�[�W
   * @param isR ���̊������|�W�g���Ȃ�true
   */
  private void showMsgToOtherEnv(DashMessage m, boolean isR) {
  }

  /**
   * ���̊�����G�[�W�F���g�ւ̃��b�Z�[�W��\������
   * @param m ���b�Z�[�W
   * @param isR ���̊������|�W�g���Ȃ�true
   */
  private void showEnvToAgMsg(DashMessage m, boolean isR) {
    VIcon receiver =
      (m.to.equals("_interface")||m.to.equals("_KaneHooker") ? envIcon : (VIconAg)agTable.get(m.to));
    if (receiver == null) {
    }

    if (m.departure.substring(0,1).equals("r") ) {
      isR = true;
    }
    else {
      isR = false;
    }

    VIcon env = getEnvIcon(m.departure, isR);
    if (env == null) {
    }

    moveMsg(env, receiver, m.performative);
  }

  /**
   * �G�[�W�F���g����G�[�W�F���g�ւ̃��b�Z�[�W��\������.
   * showUnicastMsg()�A���邢��showBroadCastMsg()���Ăяo���B
   */
  protected void showAgToAgMsg(DashMessage m) {
    showUnicastMsg(m);
  }

  /**
   * �G�[�W�F���g����P�̃G�[�W�F���g�ւ̃��b�Z�[�W��\������.
   */
  void showUnicastMsg(DashMessage m) {
    VIcon fromIcon =
      (m.from.equals("_interface")||m.from.equals("KaneHooker") ? getEnvIcon(m.from, isRepository) : (VIconAg)agTable.get(m.from));
    VIcon toIcon =
      (m.to.equals("_interface")||m.to.equals("KaneHooker") ? envIcon : (VIconAg)agTable.get(m.to));
    if (fromIcon == null || toIcon == null) {
      return;
    }
    moveMsg(fromIcon, toIcon, m.performative);
  }

  /**
   * �G�[�W�F���g���畡���̃G�[�W�F���g�ւ̃��b�Z�[�W��\������.
   */
  void showBroadcastMsg(DashMessage m, boolean isR) {

    setIconSize ( );

    VIcon fromIcon = null;

    // ����������̏ꍇ
    if (m.departure == null || m.departure.equals(dvmname)) {
      fromIcon = (m.from.equals("_interface")||m.from.equals("_KaneHooker") ? envIcon : (VIcon)agTable.get(m.from));

    //���̊�����̏ꍇ
    } else {
      fromIcon = getEnvIcon(m.departure, isR);
    }

    String performative = m.performative;

    Graphics g = getGraphics();
    Graphics bufG = buffer.getGraphics();

    // �O���`��
    // msgs�́A���A�C�R������̎���ac, �G�[�W�F���g����̎���ac-1�ɂȂ�B
    int ac = agTable.size();
    int mx[][] = new int[ac][13];
    int my[][] = new int[ac][13];
    int msgs = 0;
    for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
      VIconAg toAg = (VIconAg)e.nextElement();
      if (toAg.name.equals(m.from) &&  // �����̎����ɂ͑���Ȃ�
    (m.departure==null || m.departure.equals(envname+"."+envhost)))
        continue;
      int diff_x = toAg.x - fromIcon.x;
      int diff_y = toAg.y - fromIcon.y;
      for (int i=0; i<=12; i++) {
        mx[msgs][i] = fromIcon.x + ICONW/2 + diff_x*i/12;
        my[msgs][i] = fromIcon.y + ICONH/2 + diff_y*i/12;
      }
      bufG.drawLine(mx[msgs][0], my[msgs][0], mx[msgs][12], my[msgs][12]);
      msgs++;
    }
    drawPerformative(bufG, fromIcon, performative);
    //bufG.dispose();
    paint(g);
    //repaint();
    // �`�J�`�J�R
    flushMsg(fromIcon);
    waitStep();
    // �ړ�������
    for (int i=0; i<=12; i++) {
      if (i % SPEED != 0) continue;
      clear();                        // �N���A
      drawAgent();          // �G�[�W�F���g��`���B
      for (int j=0; j<msgs; ++j)        // ���b�Z�[�W��`���B
        drawMsg(bufG, mx[j][i], my[j][i]);
      drawPerformative(bufG, fromIcon, performative); // �p�t�H�[�}�e�B�u��`���B
      //bufG.dispose();
      paint(g);           // �ĕ`��
      //repaint();
    }
    // �����������߂ɕ`�������B
    clear();     // �N���A
    drawAgent(); // �G�[�W�F���g��`���B
    //bufG.dispose();
    paint(g);    // �ĕ`��
    //repaint();
  }

  /***********************************************************************
                               �ړ��n
  ***********************************************************************/

  /**
   * ���b�Z�[�W���ړ�������.
   * @param from �ړ��J�n�ʒu�ɂ���A�C�R��
   * @param to   �ړ��I���ʒu�ɂ���A�C�R��
   * @param perf �p�t�H�[�}�e�B�u
   */
  protected void moveMsg(VIcon from, VIcon to, String perf) {

    setIconSize ( );

    Graphics g = getGraphics();

    Graphics bufG = buffer.getGraphics();

    // �O���`��
    bufG.setColor(Color.black);
    bufG.drawLine(from.x+ICONW/2, from.y+ICONH/2, to.x+ICONW/2, to.y+ICONH/2);
    drawPerformative(bufG, from, perf);
    //bufG.dispose();
    paint(g);
    //repaint();
    // �`�J�`�J�R
    flushMsg(from);
    waitStep();
    // �ړ�������
    int diff_x = to.x - from.x;
    int diff_y = to.y - from.y;
    for (int i=0; i<=12; i++) {
      if (i % SPEED != 0) continue;
      clear();                          // �N���A
      drawAgent();          // �G�[�W�F���g��`���B
      drawMsg(bufG,         // �ړ����郁�b�Z�[�W��`���B
        from.x + ICONW/2 + diff_x*i/12,
        from.y + ICONH/2 + diff_y*i/12);
      drawPerformative(bufG, from, perf); // �p�t�H�[�}�e�B�u��`���B
      //bufG.dispose();
      paint(g);           // �ĕ`��
      //repaint();
    }
    // �����������߂ɕ`�������B
    clear();                            // �N���A
    drawAgent();            // �G�[�W�F���g��`���B
    //bufG.dispose();
    paint(g);           // �ĕ`��
    //repaint();
  }

  /**
   * �A�C�R�����ړ�������.
   * @param icon �ړ�����C���[�W�����A�C�R��
   * @param from �ړ��J�n�ʒu�ɂ���A�C�R��
   * @param to   �ړ��I���ʒu�ɂ���A�C�R��
   * @param cap  �ړ��J�n�ʒu�ɕ\������L���v�V����
   */
  void moveIcon(VIcon icon, VIcon from, VIcon to, String cap) {
    Graphics g = getGraphics();
    Graphics bufG = buffer.getGraphics();
    drawPerformative(bufG, from, cap);
    //bufG.dispose();
    paint(g);
    //repaint();
    // �`�J�`�J�R
    flushRect(from);
    waitStep();
    // �ړ�������
    int diff_x = to.x - from.x;
    int diff_y = to.y - from.y;
    for (int i=0; i<=12; i++) {
      if (i % SPEED != 0) continue;
      clear();            // �N���A
      drawAgent();          // �G�[�W�F���g��`���B
      drawIcon(bufG, icon,        // �ړ�����G�[�W�F���g��`���B
         from.x + diff_x*i/12,
         from.y + diff_y*i/12);
      drawPerformative(bufG, from, cap); // �p�t�H�[�}�e�B�u��`���B
      //bufG.dispose();
      paint(g);           // �ĕ`��
      //repaint();
    }
    // �ړ��G�[�W�F���g���������߂ɕ`�������B
    clear();                            // �N���A
    drawAgent();            // �G�[�W�F���g��`���B
    //bufG.dispose();
    paint(g);           // �ĕ`��
    //repaint();
  }

  /***********************************************************************
                               �`�J�`�J�n
  ***********************************************************************/

  /**
   * �����A�C�R���̏�Ń`�J�`�J������.
   * @param icon �`�J�`�J������ʒu�ɂ���A�C�R��
   */
  void flushMsg(VIcon icon) {

    setIconSize ( );

    Graphics g = getGraphics();
    for (int i=0; i<flushing; i++)
      try {
        // �G�[�W�F���g�̏�Ɂ��\��(����)
        g.setColor(Color.red);
        drawMsg(g, icon.x + ICONW/2, icon.y + ICONH/2);
        // �҂�
        Thread.sleep(200);
        // �G�[�W�F���g�̂ݕ\��(����)
        drawIcon(g, icon);
        // �҂�
        Thread.sleep(200);
    } catch (Exception e) {/*AdipsEnv.exit("ViewerCanvas.flushMsg()", e);*/}

    // �o�b�t�@
    Graphics bufG = buffer.getGraphics();
    drawMsg(bufG, icon.x + ICONW/2, icon.y + ICONH/2);
    //bufG.dispose();
    paint(g);
    //repaint();
  }

  /**
   * �����A�C�R���̏�Ń`�J�`�J������
   * @param icon �`�J�`�J������ʒu�ɂ���A�C�R��
   */
  private void flushRect(VIcon icon) {

    setIconSize ( );

    Graphics g = getGraphics();
    int x = icon.x;
    int y = icon.y;
    for (int i=0; i<flushing; i++)
      try {
        // ���ň͂�
        g.setColor(Color.black);
        g.drawRect(x-5, y-5, ICONW+10, ICONH+10);
        Thread.sleep(200);
        // ��������
        g.setColor(getBackground());
        g.drawRect(x-5, y-5, ICONW+10, ICONH+10);
        Thread.sleep(200);
      } catch (Exception e) {/*AdipsEnv.exit("ViewerCanvas.flushRect()", e);*/}

    // �o�b�t�@
    Graphics bufG = buffer.getGraphics();
    bufG.setColor(Color.black);
    bufG.drawRect(x-5, y-5, ICONW+10, ICONH+10);
    //bufG.dispose();
    paint(g);
    //repaint();
  }

  /**
   * �~���A�C�R���̏�Ń`�J�`�J������B
   * ����ViewerCanvasW���炵���Ă΂�Ȃ��B
   * @param base �`�J�`�J������ʒu�ɂ���A�C�R��
   */
  void flushPeke(VIcon icon) {

    setIconSize ( );

    Graphics g = getGraphics();
    int x = icon.x;
    int y = icon.y;
    for (int i=0; i<flushing; i++)
      try {
        // �~��`��
        g.setColor(Color.black);
        g.drawLine(x, y, x+ICONW-1, y+ICONH-1);
        g.drawLine(x, y+ICONH-1, x+ICONW-1, y);
        Thread.sleep(200);
        // �~������
        g.clearRect(x, y, ICONW, ICONH);
        drawIcon(g, icon);
        Thread.sleep(200);
      } catch (Exception e) {/*AdipsEnv.exit("ViewerCanvas.flushPeke()", e);*/}

    //�o�b�t�@
    Graphics bufG = buffer.getGraphics();
    bufG.setColor(Color.black);
    bufG.drawLine(x, y, x+ICONW, y+ICONH);
    bufG.drawLine(x, y+ICONH, x+ICONW, y);
    //bufG.dispose();
    paint(g);
    //repaint();
  }

  /**
   * �A�C�R���𑼂̃A�C�R���̏�Ń`�J�`�J������B
   * ����ViewerCanvasW���炵���Ă΂�Ȃ��B
   * @param base �`�J�`�J������ʒu�ɂ���A�C�R��(��)
   * @param icon �`�J�`�J������A�C�R��(��)
   */
  void flushAgent(VIcon base, VIcon icon) {

    setIconSize ( );

    Graphics g = getGraphics();
    for (int i=0; i<flushing; i++)
      try {
        // ���̏�ɃG�[�W�F���g�\��(����)
        g.drawImage(icon.image, base.x, base.y, this);
        // �҂�
        Thread.sleep(200);
        // ���̃A�C�R����\��
        g.clearRect(base.x, base.y, ICONW, ICONH);
        g.drawImage(base.image, base.x, base.y, this);
        // �҂�
        Thread.sleep(200);
      } catch (Exception e) {/*AdipsEnv.exit("ViewerCanvas.flushAgent()", e);*/}

    //�o�b�t�@
    Graphics bufG = buffer.getGraphics();
    bufG.drawImage(icon.image, base.x, base.y, this);
    //bufG.dispose();
    paint(g);
    //repaint();
  }

  /***********************************************************************
                               �P���`��n
  ***********************************************************************/

  /**
   * ����`���B
   * @param g �`���O���t�B�b�N�X
   * @param x ���b�Z�[�W�̒��SX���W
   * @param y ���b�Z�[�W�̒��SY���W
   */
  void drawMsg(Graphics g, int x, int y) {

    if( isKane ){
      g.setColor( kaneColor );
    }
    else{
      g.setColor(Color.red);
    }
    g.fillOval(x-MOR/2, y-MOR/2, MOR, MOR);
  }

  /**
   * ����`���B
   * @param g �`���O���t�B�b�N�X
   * @param x ���b�Z�[�W�̒��SX���W
   * @param y ���b�Z�[�W�̒��SY���W
   */
  void drawMsg0(Graphics g, int x, int y) {
    if( isKane ){
      g.setColor( kaneColor );
    }
    else{
      g.setColor(Color.red);
    }
    g.drawOval(x-MOR/2, y-MOR/2, MOR, MOR);
  }

  /**
   * �A�C�R����`���B
   * @param g �`���O���t�B�b�N�X
   * @param icon �A�C�R��
   */
  void drawIcon(Graphics g, VIcon icon) {
    drawIcon(g, icon, icon.x, icon.y);
  }

  /**
   * �A�C�R����`���B
   * @param g �`���O���t�B�b�N�X
   * @param icon �A�C�R��
   * @param x X���W
   * @param y Y���W
   */
  void drawIcon(Graphics g, VIcon icon, int x, int y) {

    setIconSize ( );

    g.drawImage(icon.image, x, y, ICONW, ICONH, this);


    //System.out.println ("�A�C�R���`��");
    if (icon.comment != null) {
      Font currentFont = g.getFont();
      g.setFont(commentFont);
      g.setColor(Color.gray);

      g.drawString(icon.comment, icon.x, icon.y+10);

      g.setFont(currentFont);
    }
    if (icon.isSelected()) {
      g.setColor(Color.gray);
      g.drawRect(x-3, y-3, ICONW+6, ICONH+6);
    }
  }

  /**
   * �p�t�H�[�}�e�B�u��`���B
   * @param g �`���O���t�B�b�N�X
   * @param icon �p�t�H�[�}�e�B�u�Ɋ֘A����A�C�R��
   * @param p �p�t�H�[�}�e�B�u
   */
  void drawPerformative(Graphics g, VIcon icon, String p) {
	
    setIconSize ( );

    Font currentFont = g.getFont();

    g.setFont(new Font("TimesRoman", Font.BOLD, 16));

    if( isKane ){
      g.setColor( kaneColorUra );
      g.drawString( p, icon.x+ICONW-10, icon.y+ICONH/4);
    }
    else{
      g.setColor(Color.blue);
      g.drawString(p, icon.x+ICONW-10, icon.y+ICONH/4);
    }
    g.setFont(currentFont);
    
  }

  /***********************************************************************
                               ���A�C�R��
  ***********************************************************************/

  /**
   * ���̃A�C�R��������ΕԂ��B�Ȃ���ΐ�������envTable�ɓo�^���ĕԂ��B
   * �N������z�X�g�ɂ���āA�z�X�g����"."���܂ޏꍇ�Ɗ܂܂Ȃ��ꍇ�����邽��
   * �����߂�ǂ��������Ƃ�����B
   * @param envname �z�X�g�����܂ފ���
   * @param isR �������|�W�g���Ȃ�true
   */
  VIcon getEnvIcon(String envname, boolean isR) {
    // (1)�S�������̃A�C�R����T��
    if (envname.equals("_interface") ) {
      return envIcon;
      //System.out.println ("_interface�̃A�C�R����T��");
    }
    VIcon icon = (VIcon)envTable.get(envname);
    if (icon != null)
      return icon;

    if (envname.equals("_interface") ) {
      //System.out.println ("_interface�����炸");
    }

    // (2)"."���܂܂Ȃ��z�X�g���̃A�C�R����T��
    int p = envname.indexOf(':');
    String env_name = envname.substring(0, p); // ����
    String host_name = envname.substring(p+1);
    p = host_name.indexOf(':');
    if (p > 0)
      host_name = host_name.substring(0, p); // "."���܂܂Ȃ��z�X�g��

    String env_host = env_name+"."+host_name;

    for (Enumeration e = envTable.keys(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();
      if (key.startsWith(env_host))
        return (VIcon)envTable.get(key);
    }

    // (3)�V�������
    return createEnvIcon(envname, isR);
  }


  /***********************************************************************
                               �A�C�R���쐬
  ***********************************************************************/

  /**
   * ���̃A�C�R���𐶐�����envTable�ɓo�^���ĕԂ��B
   * @param envname �z�X�g�����܂ފ���
   * @param isR �������|�W�g���Ȃ�true
   */
  private VIcon createEnvIcon(String envname, boolean isR) {
    int x = OENVX + (envTable.size() - 1 ) * 80;
    int y = ENVY;
    int p = envname.indexOf(":");
    
    String ename = envname.substring(0, p);
    String hname = envname.substring(p+1);
    System.out.println ("ViewerCanvas: " + ename + " hname: "  + hname);
    Image im = createEnvImage(isR, envname);
    VIcon env = new VIcon(x, y, im,isRepository);
    
    envTable.put(envname, env);
   
    Graphics g = getGraphics();
    drawEnvironments(g);
    Graphics bufG = buffer.getGraphics();
    drawEnvironments(bufG);
    System.out.println ("ViewerCanvas: " + env);
    return env;
  }

  /**
   * ���̃C���[�W������ĕԂ��B
   * @param isR �������|�W�g���Ȃ�true
   * @param en  ����(�z�X�g���܂܂Ȃ�)
   * @param eh  ���̃z�X�g��
   */
  Image createEnvImage(boolean isR, String dvmname) {
    Image image = createImage(70, 50);
    Graphics g = image.getGraphics();
    g.setColor(Color.blue);
    g.fillRect(0, 0, 70, 50);

    int p = dvmname.indexOf(":");
    String s1 = dvmname.substring(0,p);
    String s2 = dvmname.substring(p+1);

    FontMetrics fm = g.getFontMetrics();
    int sWidth1 = fm.stringWidth(s1);
    int sWidth2 = fm.stringWidth(s2);
    int sHeight = fm.getAscent();

    int x1 = (70-sWidth1)/2;
    int x2 = (70-sWidth2)/2;
    int y1 = 50/2;
    int y2 = 50/2+sHeight;

    g.setColor(Color.black);  // ��������������
    g.drawString(s1, x1, y1);
    g.drawString(s2, x2, y2);

    return isR ? overwrap(rImage, image) : overwrap(wImage, image);
  }

  void setEnvIcon (VIcon envicon, String envname ) {
    //VIcon env = new VIcon(ENVX, ENVY, im);
    //System.out.println( envname + "�ŃA�C�R���o�^" ) ;
    envTable.put(envname, envicon);
  }

  /**
   * �G�[�W�F���g�̃C���[�W������ĕԂ��B
   * ����������̂͐Ȃ̂ŁA�͊G�̐F�Ƃ��Ďg���Ȃ��B
   */
  Image createAgentImage(String name) {

    setIconSize ( );

    //System.out.println ("createAgentImage:" + name );
    // �l�̊G�ƃG�[�W�F���g����`����Image�����B
    Image image = createImage(ICONW, ICONH);
    Graphics g = image.getGraphics();
    g.setColor(Color.blue);
    g.fillRect(0,0,ICONW,ICONH);
    //g.drawOval(0,0,ICONW,ICONH);

    // ����
    int bai=100;
    if (isRepository ) {
      bai = r_bai;
    }
    else {
      bai = w_bai;
    }
    g.setFont(new Font("Dialog", Font.PLAIN, 12*bai/100));
    FontMetrics fm = g.getFontMetrics();
    int sWidth = fm.stringWidth(name); // �����̕�
    int sHeight = fm.getAscent();      // �����̍���


    if (sWidth <= ICONW) {  // �Z���ꍇ

      int x = (ICONW-sWidth)/2;
      int y = AGHC+sHeight/2;
      g.setColor(Color.white);  // ������
      g.drawString(name, x+1, y+1);
      g.drawString(name, x+1, y-1);
      g.drawString(name, x-1, y+1);
      g.drawString(name, x-1, y-1);
      g.setColor(Color.black);  // ��������
      g.drawString(name, x, y);
    } else {      // �����ꍇ

      // �^���߂��̑啶����T���ĂQ�s�ɕ�����
      int center = name.length() / 2;
      int c1 = 0;
      int leastAbs = 1000;
      for (int i=0; i<name.length(); i++) {
        if (Character.isUpperCase(name.charAt(i))) {
          int abs = center - i;
          if (abs<0) abs = -abs;
          if (abs < leastAbs) {
            c1 = i;
            leastAbs = abs;
          }
        }
      }

      String s1, s2;
      if (c1 != 0) {    // �����ł����I
        s1 = name.substring(0, c1);
        s2 = name.substring(c1);
      } else {      // �����ł��Ȃ��I
        s1 = name.substring(0, center)+"-";
        s2 = name.substring(center);
      }

      int p = name.indexOf(".");

      if (p != -1 ) {
        s1 = name.substring(0,p);
      }
      else {
        s1 = name;
      }
       int x1 = (ICONW-fm.stringWidth(s1))/2;
      int y1 = AGHC+sHeight/2;
      g.setColor(Color.white);  // ������
      g.drawString(s1, x1+1, y1+1);
      g.drawString(s1, x1+1, y1-1);
      g.drawString(s1, x1-1, y1+1);
      g.drawString(s1, x1-1, y1-1);

      g.setColor(Color.black);  // ��������
      g.drawString(s1, x1, y1);


  /*
      int x1 = (ICONW-fm.stringWidth(s1))/2;
      int x2 = (ICONW-fm.stringWidth(s2))/2;
      int y1 = AGHC;
      int y2 = AGHC+sHeight;

      g.setColor(Color.white);  // ������
      g.drawString(s1, x1+1, y1+1);
      g.drawString(s1, x1+1, y1-1);
      g.drawString(s1, x1-1, y1+1);
      g.drawString(s1, x1-1, y1-1);
      g.drawString(s2, x2+1, y2+1);
      g.drawString(s2, x2+1, y2-1);
      g.drawString(s2, x2-1, y2+1);
      g.drawString(s2, x2-1, y2-1);

      g.setColor(Color.black);  // ��������
      g.drawString(s1, x1, y1);
      g.drawString(s2, x2, y2);
  */
    }

    Image overrapIamge = null;

    if (bai == 100 ) {
      overrapIamge = overwrap(whiteOval100, image);
    }
    else if (bai == 90 ) {
      overrapIamge = overwrap(whiteOval90, image);
    }
    else if (bai == 80 ) {
      overrapIamge = overwrap(whiteOval80, image);
    }
    else if (bai == 70 ) {
      overrapIamge = overwrap(whiteOval70, image);
    }

    return overrapIamge;

    //return overwrap(whiteOval, image);
  }

  /**
   * baseImage�̏��owImage���d�˂�Image��Ԃ��B
   * @param baseImage ���ɂȂ�C���[�W
   * @param owImage   ��ɂȂ�C���[�W
   */
  public Image overwrap(Image baseImage, Image owImage) {
    ImageProducer prod0 = baseImage.getSource();
    ImageFilter filter = new TranspFilter(owImage, this);
    ImageProducer prod1 = new FilteredImageSource(prod0, filter);
    Image image = createImage(prod1);

    try {
      MediaTracker mt = new MediaTracker(this);
      mt.addImage(image, 0);
      mt.waitForAll();
    }
    catch(InterruptedException e) {
      //AdipsEnv.exit("ViewerCanvas.overwrap()", e);
    }

    return image;

  }

  private void setIconSize() {
    if (isRepository ) {
      ICONW = R_ICONW;
      ICONH = R_ICONH;
      AGHC  = R_AGHC;
    }
    else {
      ICONW = W_ICONW;
      ICONH = W_ICONH;
      AGHC  = W_AGHC;
    }
  }

  /***********************************************************************
              ���j���[/�`�F�b�N�{�b�N�X�ɑ΂���A�N�V�����̏���
  ***********************************************************************/

  /**
   * �t���b�V���̉�
   */
  void setFlush(int f) {
    flushing = f;
  }

  /**
   * �I����Ԃ̃A�C�R�����I����Ԃɂ���B
   */
  public void unselectIcon() {
    if (selectedIcon != null) {
      selectedIcon.setSelected(false);
      selectedIcon = null;
      clear();
      drawAgent();
      paint(getGraphics());
    }
  }

  /**
   * �I����Ԃ̃A�C�R���̃G�[�W�F���g����Ԃ��B
   * �I����Ԃ̃A�C�R�����Ȃ��ꍇ��null��Ԃ��B
   */
  public String getAgentNameOfSelectedIcon() {
    if (selectedIcon instanceof VIconAg)
      return ((VIconAg)selectedIcon).getName();
    else
      return null;
  }

  /**
   * �`�F�b�N�{�b�N�X�������ꂽ���ɌĂ΂��B
   * @param s "step", "non-stop"
   */
  public void changeItem(String s) {
    if (s.equals(RM_STEP)) {       // step���[�h
      runmode = RM_STEP;
      stepCount = 0;
      stepLabel.setText("0");
    } else if (s.equals(RM_NONSTOP)) { // non-stop���[�h
      runmode = RM_NONSTOP;
      stepLabel.setText("");
    }
  }

  /***********************************************************************
                      Canvas�ɑ΂���A�N�V�����̏���
  ***********************************************************************/

  /**
   * �L�����o�X�����������ɌĂ΂��.
   * STEP���[�h�̎��̂݁A�ȉ��̂Q�̏ꍇ�ɑΉ������������s���B
   * 1)�������ꏊ�ɃA�C�R�����Ȃ��ꍇ�B
   * 2)�������ꏊ�ɃA�C�R��������ꍇ�B
   * �������A
   * �E������̃A�C�R���̓h���b�O�ł��Ȃ��B
   * �E���A�C�R���̓h���b�O�ł��Ȃ��B
   * �E�A�C�R����"�I��"�����̂́A���������łȂ��������Ƃ��B
   */
  public void mousePressed(MouseEvent e) {
    if (e.isPopupTrigger()) {
      showPopupMenu(e);
      return;
    }

    if (!runmode.equals(RM_STEP))
      return;

    Point p = e.getPoint();
    VIcon icon = getIconAt(p);
    if (icon == null) {   // 1)
      stepCount++;
      stepLabel.setText(""+stepCount);
    } else {      // 2)
      //JOptionPane.showMessageDialog(null,"mousePressed");
//	System.out.println ("mousePressed");
      // "�I��"���X�V����B
      if (selectedIcon != null)
        selectedIcon.setSelected(false);
      selectedIcon = icon;
      icon.setSelected(true);
      clear();
      drawAgent();
      paint(getGraphics());
      // �h���b�O�̏���
      if (isRepository && icon instanceof VIconAg) {
        grabbedIcon = (VIconAg)icon;
        grabOffset = new Point(p.x-icon.x, p.y-icon.y);
        //addMouseMotionListener(this);
      }
    }
  }

  /**
   * �}�E�X���h���b�O������.
   */
  synchronized public void mouseDragged(MouseEvent e) {
    Point p = e.getPoint();
    Graphics g = getGraphics();
    g.setXORMode(Color.green);
    if (lastGrabPoint != null)
      drawIcon(g, grabbedIcon, lastGrabPoint.x, lastGrabPoint.y);
    drawIcon(g, grabbedIcon, p.x - grabOffset.x, p.y - grabOffset.y);
    g.setPaintMode();
    lastGrabPoint = new Point(p.x - grabOffset.x, p.y - grabOffset.y);
  }

  /**
   * ���������̏���.
   * 1)�S���ړ����Ă��Ȃ��ꍇ, ������̏ꍇ
   * 2)�ړ����Ă���ꍇ
   */
  synchronized public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      showPopupMenu(e);
      return;
    }

    //removeMouseMotionListener(this);
    if (lastGrabPoint != null) {  // 2)
    //System.out.println ("mouseReleased");
      Point p = e.getPoint();
      grabbedIcon.putxy(p.x-grabOffset.x, p.y-grabOffset.y);
      // ���������B
      Graphics g = getGraphics();
      clear();
      drawAgent();
      paint(g);
      //repaint();
    }
    grabbedIcon = null;
    grabOffset = null;
    lastGrabPoint = null;
  }

  /** �N���b�N������ */
  public void mouseClicked(MouseEvent e) { }

  /** �������� */
  public void mouseEntered(MouseEvent e) { }

  /** �o���� */
  public void mouseExited(MouseEvent e) { }

  /* �}�E�X�������� */
  public void mouseMoved(MouseEvent e) {
  }

  private void showPopupMenu(MouseEvent e) {
    //PopupMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  /**
   * p���A�C�R���̏�Ȃ炻�̃A�C�R����Ԃ��B
   * �����łȂ����null��Ԃ��B
   * ���G�[�W�F���g�̃A�C�R���̂�
   */
  public VIcon getIconAt(Point p) {
    VIcon icon = null;
    for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
      icon = (VIcon)e.nextElement();
      if (icon.contains(p))
        return icon;
    }
    return null;
  }

  public void setIconScale (int b ) {
    //bai = b;

    if (!isRepository ) {
      w_bai = b;
      W_ICONW = 70 * w_bai / 100;
      W_ICONH = 50 * w_bai / 100;
      W_AGHC =  30 * w_bai / 100;
    }
    else {
      r_bai = b;
      R_ICONW = 70 * r_bai / 100;
      R_ICONH = 50 * r_bai / 100;
      R_AGHC =  30 * r_bai / 100;
    }
    /*
    ICONW = 70*bai/100;
    ICONH = 50*bai/100;
    AGHC =  30*bai/100;
    */

    VIconAg icon = null;
    Vector vecIconName = new Vector ( );
    Vector vecParentName = new Vector ( );
    for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
      icon = (VIconAg)e.nextElement();
      vecIconName.addElement (icon.name );
      vecParentName.addElement (icon.parentname );
    }


    if (isRepository ) {
      initStartXY ( );
    }

    agTable.clear();
    createRootChildren ( ) ;
    for (int i=vecIconName.size()-1; i>=0; i-- ) {
      VIconAg agt = new VIconAg((String)vecIconName.elementAt(i), isRepository);
      agt.image = createAgentImage((String)vecIconName.elementAt(i));
      agt.parentname = (String)vecParentName.elementAt(i);

      agTable.put((String)vecIconName.elementAt(i), agt);
      locate((String)vecParentName.elementAt(i), agt);

    }


    /*
    for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
      icon = (VIconAg)e.nextElement();

      //VIconAg agt = new VIconAg(icon.name);
      icon.image = createAgentImage(icon.name);
      agTable.put(icon.name, icon);
      //locate(icon.parentname, agt);
    }
    */


    clear();
    drawAgent();
    paint(getGraphics());

    //System.out.println ("setIconSize");
  }
  /*
  public void mousePressed(MouseEvent e) {
    if (!runmode.equals(RM_STEP))
      return;

    Point p = e.getPoint();
    VIcon icon = getIconAt(p);
    if (icon == null) {   // 1)
      stepCount++;
      stepLabel.setText(""+stepCount);
    } else {      // 2)
    System.out.println ("mousePressed");
      // "�I��"���X�V����B
      if (selectedIcon != null)
        selectedIcon.setSelected(false);
      selectedIcon = icon;
      icon.setSelected(true);
      clear();
      drawAgent();
      paint(getGraphics());
      // �h���b�O�̏���
      if (isRepository && icon instanceof VIconAg) {
        grabbedIcon = (VIconAg)icon;
        grabOffset = new Point(p.x-icon.x, p.y-icon.y);
        addMouseMotionListener(this);
      }
    }
  }
  */

  /**
   * �}�E�X�{�^���҂�
   */
  public void waitStep() {
    boolean labelOn = true;
    try {
      // step�łȂ����́A�����Ԃ�B
      synchronized (runmode) {
        if (!runmode.equals(RM_STEP))
    return;
      }

      // 1)2)�����ꂩ���N����܂ő҂B
      while (true) {
        synchronized (runmode) {
    if (!runmode.equals(RM_STEP)) { // 1)�r����non-stop�ɂȂ����I
      stepLabel.setText("");
      return;
    }
    if (stepCount > 0) {          // 2)Canvas�������ꂽ�I
      stepCount--;
      stepLabel.setText(""+stepCount);
      return;
    }
        }
        // �Ԃ�Ȃ�
        Thread.sleep(200);
        if (labelOn) {
    stepLabel.setText("Pause!!");
    labelOn = false;
        } else {
    stepLabel.setText("");
    labelOn = true;
        }
      }
    } catch (Exception e ) { /*AdipsEnv.exit("ViewerCanvas.waitStep()", e); */}
  }

  public void clearEnvTable(){
    Vector vecKey = new Vector();
    for (Enumeration e = envTable.keys(); e.hasMoreElements(); ) {
      Object o = e.nextElement();
      if (!o.equals("_interface")) {
        vecKey.addElement(o);
      }
    }

    for (int i=0; i<vecKey.size(); i++ ) {
      envTable.remove(vecKey.elementAt(i));
    }

  }

}


