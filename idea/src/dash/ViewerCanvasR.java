/***********************************************************************
リポジトリ用キャンバス
***********************************************************************/
package dash;

import java.awt.*;
import java.io.*;
import java.util.*;

public class ViewerCanvasR
  extends ViewerCanvas
{
  /** 位置決め用 */
  private int lastY;

  /** 位置決め用 */
  private int lastX;

  /** lastYの最大値. これを越えると、lastYは0になり、lastXが++される.*/
  private int maxY;

  /** 位置の情報 */
  private Hashtable iconLocation;

  MediaTracker mt;
	String dvmname;

/**
 * コンストラクタ
 * @param envname ホスト名を含まない環境名
 * @param envhost ホスト名
 */
ViewerCanvasR(String dvmname, Viewer v)
{
  super(dvmname, true, v);
	this.dvmname = dvmname;
  lastX = 0;
  lastY = 0;
  maxY = 4;
  iconLocation = loadIconLocation();
}

/**
 * 絵の初期化. show()された後に呼ばれる。
 * show()しないとpeerが作られず、peerが作れないとcreateImage()できない。
 */
public void initialize() {
  super.initialize();
  //Image envImage = createEnvImage(true, envname, envhost);
  Image envImage = createEnvImage(true, this. dvmname);
  envIcon = new VIcon(ENVX, ENVY, envImage, true);
  setEnvIcon (envIcon, "_interface");
  clear();
  repaint();
}

/**
 * 新たに追加されたエージェントの位置を決定する。
 * @param msg null
 * @param agt 名前とイメージだけ決まっているエージェントのアイコン
 */
void locate(String origin, VIconAg agt) {
  String name = agt.getName();
  String xy = (String)iconLocation.get(name);

  if (xy != null) {
    int p = xy.indexOf(' ');
    int x = Integer.parseInt(xy.substring(0, p));
    int y = Integer.parseInt(xy.substring(p+1));
    x = x*r_bai/100;
    y = y*r_bai/100;
    agt.putxy(x, y);
  } else {
    agt.putxy(ENVX + lastX*(R_ICONW+30),//*100,
	      AGY  + lastY*(R_ICONH+20));//70);
    ++lastY;
    if (lastY == maxY) {
      lastY = 0;
      lastX++;
    }
  }

  clear();
  drawAgent();
  repaint();
}


/***********************************************************************
マネージャを指定する(Rはできないけど)
***********************************************************************/
void specifyManager(DashMessage m) {
  //AdipsEnv.exit("ViewerCanvasR.specifyManager(): implementation error");
}

/***********************************************************************
エージェントを削除するメソッド(未使用)
***********************************************************************/
void removeAgent(String agName) {
  VIconAg ra = (VIconAg)agTable.remove(agName);

  Graphics g = getGraphics();
  // クリア
  clear();
  // エージェントを描く。
  drawAgent();
  // 再描画
  paint(g);
}

/**
 * エージェントから１つのエージェントへのメッセージを表示する.
 */
/*
void showUnicastMsg(Message m) {
  VIconAg fromIcon = (VIconAg)agTable.get(m.from); // 送信Ag
  VIconAg toIcon = (VIconAg)agTable.get(m.to);     // 受信Ag
  if (fromIcon == null || toIcon == null)
    AdipsEnv.exit("ViewerCanvasR.showUnicastMsg(): cannot move msg:\n"+
		  m.printString());
  moveMsg(fromIcon, toIcon, m.performative());
}
*/

/***********************************************************************
                   メニューに対するアクションの処理
***********************************************************************/

/**
 * 格子状に整列させる。
 */
public void gridSortIcon() {
  Graphics g = getGraphics();
  Graphics bufG = buffer.getGraphics();

  String names[] = new String[agTable.size()]; // 名前
  int dx[]  = new int[agTable.size()];	 // xの変化量
  int dy[]  = new int[agTable.size()];	 // yの変化量
  int x1[] = new int[agTable.size()];	 // 移動前のx
  int y1[] = new int[agTable.size()];	 // 移動前のy

  // 移動前の位置を出す。
  int ac = 0;
  for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
    VIconAg a = (VIconAg)e.nextElement();
    names[ac] = a.name;
    x1[ac] = a.x;
    y1[ac] = a.y;
    ac++;
  }
  
  // 移動後の位置を計算する。
  lastX = 0;
  lastY = 0;
  for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
    VIconAg icon = (VIconAg)e.nextElement();
    icon.putxy(ENVX + lastX*(ICONW+30),//100,
	       AGY  + lastY*(ICONH+20));//70);
    ++lastY;
    if (lastY == maxY) {
      lastY = 0;
      lastX++;
    }
  }

  // 移動前後の変化量を出す。
  boolean notMove = true;
  for (int i=0; i<ac; i++) {
    VIconAg a = (VIconAg)agTable.get(names[i]);
    dx[i] = a.x - x1[i];
    dy[i] = a.y - y1[i];
    if (dx[i]!=0 || dy[i]!=0)
      notMove = false;
  }
  if (notMove) // 整列直後の場合起こりうる。
    return;

  // 移動アニメ
  for (int c=0; c<=12; c++) {
    if (c % SPEED != 0) continue;

    // 位置セット
    for (int i=0; i<ac; i++)
      ((VIconAg)agTable.get(names[i]))
	.putxy(x1[i] + dx[i]*c/12, y1[i] + dy[i]*c/12);
    // クリア
    clear();
    // エージェントを描く。
    drawAgent();
    // 再描画
    paint(g);
  }
}

/**
 * アイコンの位置情報をファイルに保存する。
 * ファイル名は、"location.dat"。
 * アイコンの情報は、"エージェント名 x座標 y座標"という行で表される。
 */
public void saveIconLocation() {
  try {
    FileOutputStream fout = new FileOutputStream("location.dat");
    DataOutputStream dout = new DataOutputStream(fout);

    for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
      VIconAg icon = (VIconAg)e.nextElement();
      /*
      String locationStr = icon.name;
      locationStr += " " + icon.y / bai * bai;
      locationStr += " " + icon.y / bai * bai;
      dout.writeBytes(locationStr+"\n");
      */
      dout.writeBytes(icon.getIconLocation()+"\n");
    }

    dout.close();
  } catch (Exception e) {
    //AdipsEnv.exit("ViewerCanvasR.saveIconLocation()", e);
  }
}

/**
 * アイコンの位置情報をファイルから読み込み、iconLocationにセットする。
 * iconLocationは、key:クラスエージェント名 / val:"x y"
 * (ファイルはない場合もある)
 */
private Hashtable loadIconLocation() {
  Hashtable table = new Hashtable();

  BufferedReader br = null;
  try {
    FileReader fr = new FileReader("location.dat");
    br = new BufferedReader(fr);

    while (true) {
      String line = br.readLine();
      if (line == null)
	break;
      int p = line.indexOf(' ');
      table.put(line.substring(0, p), // "エージェント名"
		line.substring(p+1)); // "x座標 y座標"
    }
  } catch (FileNotFoundException e) {
    ;
  } catch (Exception e) {
    //AdipsEnv.exit("ViewerCanvasR.loadIconLocation()", e);
  } finally {
		try {
			if (br != null) {
				br.close();
				br = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
  return table;
}

void createRootChildren() {
	// 何もしない
}

void initStartXY ( ) {
  lastX = 0;
  lastY = 0;
}

}

