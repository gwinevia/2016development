package dash;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

/**
 * <p>タイトル: </p>
 * <p>説明: </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名: </p>
 * @author 未入力
 * @version 1.0
 */

public abstract class ViewerCanvas2 extends JPanel
implements MouseListener, MouseMotionListener, ActionListener{
  Image buffer;     // バッファ

  Hashtable agTable;    // エージェントの情報
        // key: エージェント名
        // value: ViewerAgのインスタンス

  VIcon envIcon;    // 自分の情報
  Hashtable envTable;   // 他の環境の情報(自分の情報は入ってない)

  // エージェントの楕円の大きさは、70x40。
  // whiteOvalは、これを70x50の矩形の下部に位置させている。
  // だから、楕円の中心は(35,25)でなく(35,30)。
  // 顔が見えた方がいいので、下部に位置させている。

  private int AGHC  = 30;   // 楕円の中心の高さ
  int ICONW = 70;   // アイコンの幅
  int ICONH = 50;   // アイコンの高さ

                                    // ワークプレース用
  public static int W_AGHC  = 30;   // 楕円の中心の高さ
  public static int W_ICONW = 70;   // アイコンの幅
  public static int W_ICONH = 50;   // アイコンの高さ

                                    // リポジトリ用
  public static int R_AGHC  = 30;   // 楕円の中心の高さ
  public static int R_ICONW = 70;   // アイコンの幅
  public static int R_ICONH = 50;   // アイコンの高さ

  public static final int ENVX = 10;   // 環境の絵の座標
  public static final int ENVY = 10;   // 環境の絵の座標

  public static final int OENVX = 150; // 他の環境の表示開始X座標
  public static final int AGY = 70;    // エージェントの表示開始Y座標

  public static final int MOR = 16;    // ●の直径

  public static final int SPEED = 2;   // メッセージ等を動かす速さ
               // 大きくすると速くなる
               // 12の約数にすること
  public static int w_bai = 100; // アイコンの幅
  public static int r_bai = 100; // アイコンの幅

  private int flushing = 0; // チカチカの回数

  private boolean isRepository;   // リポジトリ？

  /**
   * 動作モード.
   * RM_STEPかRM_NONSTOPのいずれかの値を取る。
   */
  private volatile String runmode;
  private static final String RM_STEP    = "step";
  private static final String RM_NONSTOP = "non-stop";

  /**
   * runmodeがRM_STEPの時に背景の上でマウスが押されると++される。
   */
  private volatile int stepCount;

  /** アニメ同期用 **/
  private volatile String animeSync = "ruriruri";

  /** つかまれたアイコン */
  private volatile VIconAg grabbedIcon;

  /** grabbedIconからつかんだ位置へのオフセット */
  private volatile Point grabOffset;

  /**
   * 最後の掴んだ位置.
   * ドラッグしない限りはnullのまま。
   */
  private volatile Point lastGrabPoint;

  /**
   * 選択されているアイコン.
   * 何も選択していない場合はnull。
   */
  private volatile VIcon selectedIcon;

  JLabel stepLabel;    // stepの状態表示用ラベル。
        // Viewerにセットされる。

//  private Hashtable imagetable;

  Image rImage;     // リポジトリの絵
  Image wImage;     // 動作環境の絵
  Image whiteOval;    // エージェントを表す白い楕円
  Image whiteOval100;   // エージェントを表す白い楕円(倍率100%)
  Image whiteOval90;    // エージェントを表す白い楕円(倍率90%)
  Image whiteOval80;    // エージェントを表す白い楕円(倍率80%)
  Image whiteOval70;    // エージェントを表す白い楕円(倍率70%)
  String envname;   // ホスト名を含まない環境名
  String envhost;   // ホスト名

  private String dvmname;   // ADD COSMOS
  Viewer viewer;    // Viewerのインスタンス
  NewifItface newif;

  Font commentFont;   // コメント用のフォント

  boolean isKane;     // Kane現在処理中のメッセージがKaneメッセージかどうかのフラグ
  final Color kaneColor = Color.blue; // Kane色
  final Color kaneColorUra = Color.red;

  /** ポップアップメニュー */
  private JPopupMenu PopupMenu = null;

  ViewerCanvas2(String dvmname, boolean isR) {
    agTable = new Hashtable();
    envTable = new Hashtable();

    isRepository = isR;

    /* デフォルトで70％の縮小率でエージェントを表示する */
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
    PopupMenu.add(menuItem("全てクリア","CodeCheckResultClear",null));

  }

  //****************************************************************************
  /** メニューアクション **/
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


  /** Imageを返す。*/
  private Image getImage(String path) {
    java.net.URL url = this.getClass().getResource(path);
    javax.swing.ImageIcon icon = new javax.swing.ImageIcon(url);
    return icon.getImage();
  }

  /**
   * ダブルバッファの初期化. show()された後に呼ばれる。
   * show()しないとpeerが作られず、peerが作れないとcreateImage()できない。
   */
  public void initialize() {
    buffer = createImage(1000, 700); // 適当な数値
  }

  /*************************************************************************
                              ウィンドウ制御系
  *************************************************************************/
  /**
   * バッファの内容をキャンバスに転送する。
   */
  public void update(Graphics g) {
    paint(g);
  }

  /**
   * バッファの内容をキャンバスに転送する。
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
   * バッファをクリアし、環境と線を書く。
   */
  void clear() {

    Graphics bufG = buffer.getGraphics();
    bufG.clearRect(0, 0, 1000, 700);
    drawEnvironments(bufG);
    bufG.drawLine(OENVX-10, 0, OENVX-10, AGY-1);
    bufG.drawLine(OENVX-10, AGY-1, 1000, AGY-1);
  }

  /**
   * 環境を全て描く。
   * @param g 描画するGraphics
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
   * エージェントを全部描く
   */
  void drawAgent() {
    Graphics bufG = buffer.getGraphics();
    for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
      VIcon icon = (VIcon)e.nextElement();
      drawIcon(bufG, icon);
    }
  }
  /***********************************************************************
                             Viewerからの呼出系
          showMsg(), showNewAgent(), showRemoveAget()は排他的動作！
  ***********************************************************************/

  /**
   * メッセージを表示する.
   * showNewAgent(), showRemoveAgent()と排他的に動く。
   * @param m メッセージ
   * @param toAnotherEnv 他の環境へのメッセージならtrue。
   *   メッセージの内容が同じでも、
   *   ・送信環境ではエージェントから環境に送る。
   *   ・受信環境では環境からエージェントに送る。 という違いがあるために必要。
   * @param isR 他の環境がリポジトリならtrue
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
   * 新しいエージェントを追加する.
   * showMsg(), showRemoveAgent()と排他的に動く。
   * @param m     CreateInstanceメッセージ、
   *              またはnull(リポジトリがクラスAgを生成した場合)
   * @param name  エージェント名
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
   * エージェントを削除する.
   * showMsg(), showNewAgent()と排他的に動く。
   * @param agName 削除するエージェント名
   */
  public void showRemoveAgent(String agName) {
    synchronized (animeSync) {
      removeAgent(agName);
    }
  }

  abstract void removeAgent(String agName);

  /**
   * エージェントのアイコンを整列する。
   * ViewerCanvasWは、これが呼ばれる。
   * ViewerCanvasRは、自前のが呼ばれる。
   */
  public void gridSort() {
    gridSortIcon();
  }

  abstract void gridSortIcon();

  /**
   * エージェントにコメントを付ける。
   * @param name エージェント名
   * @param comment コメント
   */
  public void commentAgent(String name, String comment) {
    VIconAg target = (VIconAg)agTable.get(name);
  }
  /***********************************************************************
                          サブクラス依存呼出系
  ***********************************************************************/

  /** エージェントの位置を決定する。*/
  abstract void locate(String origin, VIconAg agt);

  /**
   * マネージャを指定する
   */
  abstract void specifyManager(DashMessage m);

  /** */
  abstract void createRootChildren();

  abstract void initStartXY();

  /***********************************************************************
                         showMsg()からの呼出系
  ***********************************************************************/

  /**
   * エージェントまたは環境から他の環境へのメッセージを表示する。
   *
   * @param m メッセージ
   * @param isR 他の環境がリポジトリならtrue
   */
  private void showMsgToOtherEnv(DashMessage m, boolean isR) {
  }

  /**
   * 他の環境からエージェントへのメッセージを表示する
   * @param m メッセージ
   * @param isR 他の環境がリポジトリならtrue
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
   * エージェントからエージェントへのメッセージを表示する.
   * showUnicastMsg()、あるいはshowBroadCastMsg()を呼び出す。
   */
  protected void showAgToAgMsg(DashMessage m) {
    showUnicastMsg(m);
  }

  /**
   * エージェントから１つのエージェントへのメッセージを表示する.
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
   * エージェントから複数のエージェントへのメッセージを表示する.
   */
  void showBroadcastMsg(DashMessage m, boolean isR) {

    setIconSize ( );

    VIcon fromIcon = null;

    // 同じ環境からの場合
    if (m.departure == null || m.departure.equals(dvmname)) {
      fromIcon = (m.from.equals("_interface")||m.from.equals("_KaneHooker") ? envIcon : (VIcon)agTable.get(m.from));

    //他の環境からの場合
    } else {
      fromIcon = getEnvIcon(m.departure, isR);
    }

    String performative = m.performative;

    Graphics g = getGraphics();
    Graphics bufG = buffer.getGraphics();

    // 軌道描く
    // msgsは、環境アイコンからの時はac, エージェントからの時はac-1になる。
    int ac = agTable.size();
    int mx[][] = new int[ac][13];
    int my[][] = new int[ac][13];
    int msgs = 0;
    for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
      VIconAg toAg = (VIconAg)e.nextElement();
      if (toAg.name.equals(m.from) &&  // 同環境の自分には送らない
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
    // チカチカ３
    flushMsg(fromIcon);
    waitStep();
    // 移動させる
    for (int i=0; i<=12; i++) {
      if (i % SPEED != 0) continue;
      clear();                        // クリア
      drawAgent();          // エージェントを描く。
      for (int j=0; j<msgs; ++j)        // メッセージを描く。
        drawMsg(bufG, mx[j][i], my[j][i]);
      drawPerformative(bufG, fromIcon, performative); // パフォーマティブを描く。
      //bufG.dispose();
      paint(g);           // 再描画
      //repaint();
    }
    // ●を消すために描き直す。
    clear();     // クリア
    drawAgent(); // エージェントを描く。
    //bufG.dispose();
    paint(g);    // 再描画
    //repaint();
  }

  /***********************************************************************
                               移動系
  ***********************************************************************/

  /**
   * メッセージを移動させる.
   * @param from 移動開始位置にあるアイコン
   * @param to   移動終了位置にあるアイコン
   * @param perf パフォーマティブ
   */
  protected void moveMsg(VIcon from, VIcon to, String perf) {

    setIconSize ( );

    Graphics g = getGraphics();

    Graphics bufG = buffer.getGraphics();

    // 軌道描く
    bufG.setColor(Color.black);
    bufG.drawLine(from.x+ICONW/2, from.y+ICONH/2, to.x+ICONW/2, to.y+ICONH/2);
    drawPerformative(bufG, from, perf);
    //bufG.dispose();
    paint(g);
    //repaint();
    // チカチカ３
    flushMsg(from);
    waitStep();
    // 移動させる
    int diff_x = to.x - from.x;
    int diff_y = to.y - from.y;
    for (int i=0; i<=12; i++) {
      if (i % SPEED != 0) continue;
      clear();                          // クリア
      drawAgent();          // エージェントを描く。
      drawMsg(bufG,         // 移動するメッセージを描く。
        from.x + ICONW/2 + diff_x*i/12,
        from.y + ICONH/2 + diff_y*i/12);
      drawPerformative(bufG, from, perf); // パフォーマティブを描く。
      //bufG.dispose();
      paint(g);           // 再描画
      //repaint();
    }
    // ●を消すために描き直す。
    clear();                            // クリア
    drawAgent();            // エージェントを描く。
    //bufG.dispose();
    paint(g);           // 再描画
    //repaint();
  }

  /**
   * アイコンを移動させる.
   * @param icon 移動するイメージを持つアイコン
   * @param from 移動開始位置にあるアイコン
   * @param to   移動終了位置にあるアイコン
   * @param cap  移動開始位置に表示するキャプション
   */
  void moveIcon(VIcon icon, VIcon from, VIcon to, String cap) {
    Graphics g = getGraphics();
    Graphics bufG = buffer.getGraphics();
    drawPerformative(bufG, from, cap);
    //bufG.dispose();
    paint(g);
    //repaint();
    // チカチカ３
    flushRect(from);
    waitStep();
    // 移動させる
    int diff_x = to.x - from.x;
    int diff_y = to.y - from.y;
    for (int i=0; i<=12; i++) {
      if (i % SPEED != 0) continue;
      clear();            // クリア
      drawAgent();          // エージェントを描く。
      drawIcon(bufG, icon,        // 移動するエージェントを描く。
         from.x + diff_x*i/12,
         from.y + diff_y*i/12);
      drawPerformative(bufG, from, cap); // パフォーマティブを描く。
      //bufG.dispose();
      paint(g);           // 再描画
      //repaint();
    }
    // 移動エージェントを消すために描き直す。
    clear();                            // クリア
    drawAgent();            // エージェントを描く。
    //bufG.dispose();
    paint(g);           // 再描画
    //repaint();
  }

  /***********************************************************************
                               チカチカ系
  ***********************************************************************/

  /**
   * ●をアイコンの上でチカチカさせる.
   * @param icon チカチカさせる位置にあるアイコン
   */
  void flushMsg(VIcon icon) {

    setIconSize ( );

    Graphics g = getGraphics();
    for (int i=0; i<flushing; i++)
      try {
        // エージェントの上に●表示(直接)
        g.setColor(Color.red);
        drawMsg(g, icon.x + ICONW/2, icon.y + ICONH/2);
        // 待ち
        Thread.sleep(200);
        // エージェントのみ表示(直接)
        drawIcon(g, icon);
        // 待ち
        Thread.sleep(200);
    } catch (Exception e) {/*AdipsEnv.exit("ViewerCanvas.flushMsg()", e);*/}

    // バッファ
    Graphics bufG = buffer.getGraphics();
    drawMsg(bufG, icon.x + ICONW/2, icon.y + ICONH/2);
    //bufG.dispose();
    paint(g);
    //repaint();
  }

  /**
   * □をアイコンの上でチカチカさせる
   * @param icon チカチカさせる位置にあるアイコン
   */
  private void flushRect(VIcon icon) {

    setIconSize ( );

    Graphics g = getGraphics();
    int x = icon.x;
    int y = icon.y;
    for (int i=0; i<flushing; i++)
      try {
        // □で囲む
        g.setColor(Color.black);
        g.drawRect(x-5, y-5, ICONW+10, ICONH+10);
        Thread.sleep(200);
        // □を消す
        g.setColor(getBackground());
        g.drawRect(x-5, y-5, ICONW+10, ICONH+10);
        Thread.sleep(200);
      } catch (Exception e) {/*AdipsEnv.exit("ViewerCanvas.flushRect()", e);*/}

    // バッファ
    Graphics bufG = buffer.getGraphics();
    bufG.setColor(Color.black);
    bufG.drawRect(x-5, y-5, ICONW+10, ICONH+10);
    //bufG.dispose();
    paint(g);
    //repaint();
  }

  /**
   * ×をアイコンの上でチカチカさせる。
   * じつはViewerCanvasWからしか呼ばれない。
   * @param base チカチカさせる位置にあるアイコン
   */
  void flushPeke(VIcon icon) {

    setIconSize ( );

    Graphics g = getGraphics();
    int x = icon.x;
    int y = icon.y;
    for (int i=0; i<flushing; i++)
      try {
        // ×を描く
        g.setColor(Color.black);
        g.drawLine(x, y, x+ICONW-1, y+ICONH-1);
        g.drawLine(x, y+ICONH-1, x+ICONW-1, y);
        Thread.sleep(200);
        // ×を消す
        g.clearRect(x, y, ICONW, ICONH);
        drawIcon(g, icon);
        Thread.sleep(200);
      } catch (Exception e) {/*AdipsEnv.exit("ViewerCanvas.flushPeke()", e);*/}

    //バッファ
    Graphics bufG = buffer.getGraphics();
    bufG.setColor(Color.black);
    bufG.drawLine(x, y, x+ICONW, y+ICONH);
    bufG.drawLine(x, y+ICONH, x+ICONW, y);
    //bufG.dispose();
    paint(g);
    //repaint();
  }

  /**
   * アイコンを他のアイコンの上でチカチカさせる。
   * じつはViewerCanvasWからしか呼ばれない。
   * @param base チカチカさせる位置にあるアイコン(下)
   * @param icon チカチカさせるアイコン(上)
   */
  void flushAgent(VIcon base, VIcon icon) {

    setIconSize ( );

    Graphics g = getGraphics();
    for (int i=0; i<flushing; i++)
      try {
        // 環境の上にエージェント表示(直接)
        g.drawImage(icon.image, base.x, base.y, this);
        // 待ち
        Thread.sleep(200);
        // 下のアイコンを表示
        g.clearRect(base.x, base.y, ICONW, ICONH);
        g.drawImage(base.image, base.x, base.y, this);
        // 待ち
        Thread.sleep(200);
      } catch (Exception e) {/*AdipsEnv.exit("ViewerCanvas.flushAgent()", e);*/}

    //バッファ
    Graphics bufG = buffer.getGraphics();
    bufG.drawImage(icon.image, base.x, base.y, this);
    //bufG.dispose();
    paint(g);
    //repaint();
  }

  /***********************************************************************
                               単純描画系
  ***********************************************************************/

  /**
   * ●を描く。
   * @param g 描くグラフィックス
   * @param x メッセージの中心X座標
   * @param y メッセージの中心Y座標
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
   * ◯を描く。
   * @param g 描くグラフィックス
   * @param x メッセージの中心X座標
   * @param y メッセージの中心Y座標
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
   * アイコンを描く。
   * @param g 描くグラフィックス
   * @param icon アイコン
   */
  void drawIcon(Graphics g, VIcon icon) {
    drawIcon(g, icon, icon.x, icon.y);
  }

  /**
   * アイコンを描く。
   * @param g 描くグラフィックス
   * @param icon アイコン
   * @param x X座標
   * @param y Y座標
   */
  void drawIcon(Graphics g, VIcon icon, int x, int y) {

    setIconSize ( );

    g.drawImage(icon.image, x, y, ICONW, ICONH, this);


    //System.out.println ("アイコン描画");
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
   * パフォーマティブを描く。
   * @param g 描くグラフィックス
   * @param icon パフォーマティブに関連するアイコン
   * @param p パフォーマティブ
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
                               環境アイコン
  ***********************************************************************/

  /**
   * 環境のアイコンがあれば返す。なければ生成してenvTableに登録して返す。
   * 起動するホストによって、ホスト名が"."を含む場合と含まない場合があるため
   * 少しめんどくさいことをする。
   * @param envname ホスト名を含む環境名
   * @param isR 環境がリポジトリならtrue
   */
  VIcon getEnvIcon(String envname, boolean isR) {
    // (1)全く同名のアイコンを探す
    if (envname.equals("_interface") ) {
      return envIcon;
      //System.out.println ("_interfaceのアイコンを探す");
    }
    VIcon icon = (VIcon)envTable.get(envname);
    if (icon != null)
      return icon;

    if (envname.equals("_interface") ) {
      //System.out.println ("_interface見つからず");
    }

    // (2)"."を含まないホスト名のアイコンを探す
    int p = envname.indexOf(':');
    String env_name = envname.substring(0, p); // 環境名
    String host_name = envname.substring(p+1);
    p = host_name.indexOf(':');
    if (p > 0)
      host_name = host_name.substring(0, p); // "."を含まないホスト名

    String env_host = env_name+"."+host_name;

    for (Enumeration e = envTable.keys(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();
      if (key.startsWith(env_host))
        return (VIcon)envTable.get(key);
    }

    // (3)新しく作る
    return createEnvIcon(envname, isR);
  }


  /***********************************************************************
                               アイコン作成
  ***********************************************************************/

  /**
   * 環境のアイコンを生成してenvTableに登録して返す。
   * @param envname ホスト名を含む環境名
   * @param isR 環境がリポジトリならtrue
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
   * 環境のイメージを作って返す。
   * @param isR 環境がリポジトリならtrue
   * @param en  環境名(ホスト名含まない)
   * @param eh  環境のホスト名
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

    g.setColor(Color.black);  // 黒い文字を書く
    g.drawString(s1, x1, y1);
    g.drawString(s2, x2, y2);

    return isR ? overwrap(rImage, image) : overwrap(wImage, image);
  }

  void setEnvIcon (VIcon envicon, String envname ) {
    //VIcon env = new VIcon(ENVX, ENVY, im);
    //System.out.println( envname + "でアイコン登録" ) ;
    envTable.put(envname, envicon);
  }

  /**
   * エージェントのイメージを作って返す。
   * 透明化するのは青なので、青は絵の色として使えない。
   */
  Image createAgentImage(String name) {

    setIconSize ( );

    //System.out.println ("createAgentImage:" + name );
    // 人の絵とエージェント名を描いたImageを作る。
    Image image = createImage(ICONW, ICONH);
    Graphics g = image.getGraphics();
    g.setColor(Color.blue);
    g.fillRect(0,0,ICONW,ICONH);
    //g.drawOval(0,0,ICONW,ICONH);

    // 文字
    int bai=100;
    if (isRepository ) {
      bai = r_bai;
    }
    else {
      bai = w_bai;
    }
    g.setFont(new Font("Dialog", Font.PLAIN, 12*bai/100));
    FontMetrics fm = g.getFontMetrics();
    int sWidth = fm.stringWidth(name); // 文字の幅
    int sHeight = fm.getAscent();      // 文字の高さ


    if (sWidth <= ICONW) {  // 短い場合

      int x = (ICONW-sWidth)/2;
      int y = AGHC+sHeight/2;
      g.setColor(Color.white);  // 白抜き
      g.drawString(name, x+1, y+1);
      g.drawString(name, x+1, y-1);
      g.drawString(name, x-1, y+1);
      g.drawString(name, x-1, y-1);
      g.setColor(Color.black);  // 黒い文字
      g.drawString(name, x, y);
    } else {      // 長い場合

      // 真中近くの大文字を探して２行に分ける
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
      if (c1 != 0) {    // 分割できた！
        s1 = name.substring(0, c1);
        s2 = name.substring(c1);
      } else {      // 分割できない！
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
      g.setColor(Color.white);  // 白抜き
      g.drawString(s1, x1+1, y1+1);
      g.drawString(s1, x1+1, y1-1);
      g.drawString(s1, x1-1, y1+1);
      g.drawString(s1, x1-1, y1-1);

      g.setColor(Color.black);  // 黒い文字
      g.drawString(s1, x1, y1);


  /*
      int x1 = (ICONW-fm.stringWidth(s1))/2;
      int x2 = (ICONW-fm.stringWidth(s2))/2;
      int y1 = AGHC;
      int y2 = AGHC+sHeight;

      g.setColor(Color.white);  // 白抜き
      g.drawString(s1, x1+1, y1+1);
      g.drawString(s1, x1+1, y1-1);
      g.drawString(s1, x1-1, y1+1);
      g.drawString(s1, x1-1, y1-1);
      g.drawString(s2, x2+1, y2+1);
      g.drawString(s2, x2+1, y2-1);
      g.drawString(s2, x2-1, y2+1);
      g.drawString(s2, x2-1, y2-1);

      g.setColor(Color.black);  // 黒い文字
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
   * baseImageの上にowImageを重ねたImageを返す。
   * @param baseImage 下になるイメージ
   * @param owImage   上になるイメージ
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
              メニュー/チェックボックスに対するアクションの処理
  ***********************************************************************/

  /**
   * フラッシュの回数
   */
  void setFlush(int f) {
    flushing = f;
  }

  /**
   * 選択状態のアイコンを非選択状態にする。
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
   * 選択状態のアイコンのエージェント名を返す。
   * 選択状態のアイコンがない場合はnullを返す。
   */
  public String getAgentNameOfSelectedIcon() {
    if (selectedIcon instanceof VIconAg)
      return ((VIconAg)selectedIcon).getName();
    else
      return null;
  }

  /**
   * チェックボックスが押された時に呼ばれる。
   * @param s "step", "non-stop"
   */
  public void changeItem(String s) {
    if (s.equals(RM_STEP)) {       // stepモード
      runmode = RM_STEP;
      stepCount = 0;
      stepLabel.setText("0");
    } else if (s.equals(RM_NONSTOP)) { // non-stopモード
      runmode = RM_NONSTOP;
      stepLabel.setText("");
    }
  }

  /***********************************************************************
                      Canvasに対するアクションの処理
  ***********************************************************************/

  /**
   * キャンバスを押した時に呼ばれる.
   * STEPモードの時のみ、以下の２つの場合に対応した処理を行う。
   * 1)押した場所にアイコンがない場合。
   * 2)押した場所にアイコンがある場合。
   * ただし、
   * ・動作環境のアイコンはドラッグできない。
   * ・環境アイコンはドラッグできない。
   * ・アイコンが"選択"されるのは、離した時でなく押したとき。
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
      // "選択"を更新する。
      if (selectedIcon != null)
        selectedIcon.setSelected(false);
      selectedIcon = icon;
      icon.setSelected(true);
      clear();
      drawAgent();
      paint(getGraphics());
      // ドラッグの準備
      if (isRepository && icon instanceof VIconAg) {
        grabbedIcon = (VIconAg)icon;
        grabOffset = new Point(p.x-icon.x, p.y-icon.y);
        //addMouseMotionListener(this);
      }
    }
  }

  /**
   * マウスがドラッグした時.
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
   * 離した時の処理.
   * 1)全く移動していない場合, 動作環境の場合
   * 2)移動している場合
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
      // 書き直す。
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

  /** クリックした時 */
  public void mouseClicked(MouseEvent e) { }

  /** 入った時 */
  public void mouseEntered(MouseEvent e) { }

  /** 出た時 */
  public void mouseExited(MouseEvent e) { }

  /* マウスが動く時 */
  public void mouseMoved(MouseEvent e) {
  }

  private void showPopupMenu(MouseEvent e) {
    //PopupMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  /**
   * pがアイコンの上ならそのアイコンを返す。
   * そうでなければnullを返す。
   * ※エージェントのアイコンのみ
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
      // "選択"を更新する。
      if (selectedIcon != null)
        selectedIcon.setSelected(false);
      selectedIcon = icon;
      icon.setSelected(true);
      clear();
      drawAgent();
      paint(getGraphics());
      // ドラッグの準備
      if (isRepository && icon instanceof VIconAg) {
        grabbedIcon = (VIconAg)icon;
        grabOffset = new Point(p.x-icon.x, p.y-icon.y);
        addMouseMotionListener(this);
      }
    }
  }
  */

  /**
   * マウスボタン待ち
   */
  public void waitStep() {
    boolean labelOn = true;
    try {
      // stepでない時は、すぐ返る。
      synchronized (runmode) {
        if (!runmode.equals(RM_STEP))
    return;
      }

      // 1)2)いずれかが起こるまで待つ。
      while (true) {
        synchronized (runmode) {
    if (!runmode.equals(RM_STEP)) { // 1)途中でnon-stopになった！
      stepLabel.setText("");
      return;
    }
    if (stepCount > 0) {          // 2)Canvasが押された！
      stepCount--;
      stepLabel.setText(""+stepCount);
      return;
    }
        }
        // 返れない
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


