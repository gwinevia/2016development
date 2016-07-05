package dash;

import java.awt.*;
import java.util.*;

/**
 * 動作環境用キャンバス
 */
public class ViewerCanvasW extends ViewerCanvas {
	public VIconAg root; // 頂上にいる仮想のエージェント
	static int max_x = 1; // ^^;
	private String dvmname = "";

	/**
	 * コンストラクタ
	 * @param envname ホスト名を含まない環境名
	 * @param envhost ホスト名
	 */
	ViewerCanvasW(String dvmname, Viewer v) {
		super(dvmname, false, v);
		this.dvmname = dvmname;
		this.root = new VIconAg("root", false); // 位置決めをするためのダミー情報
		this.max_x = 1;
	}

	/**
	 * 絵の初期化. show()された後に呼ばれる。
	 * show()しないとpeerが作られず、peerが作れないとcreateImage()できない。
	 */
	public void initialize() {
		super.initialize();
		Image envImage = createEnvImage(false, this.dvmname);
		envIcon = new VIcon(ENVX, ENVY, envImage, false);
		clear();
		repaint();
	}

	/**
	 * 新たに生成されたエージェントの位置を決定する。
	 * 生成されたての時は、まだマネージャが生成されていないので
	 * 取りあえずrootの子になる。
	 * マネージャが生成されると、specifyManager()がどこからか呼ばれて
	 * 木になる。
	 * @param msg CreateInstanceメッセージ
	 * @param agt 名前とイメージだけ決まっているエージェントのアイコン
	 */
	//void locate(DashMessage msg, VIconAg agt) {
	void locate(String origin, VIconAg agt) {

		root.children.addElement(agt);

		VIcon env = null;
		agt.x = 0;
		agt.y = 0;

		if (!origin.equals(dvmname)) {
			env = getEnvIcon(origin, true);
			agt.x = env.x;
			agt.y = env.y;
		}

		moveAnime(agt, env);

	}

	/**
	 * 全てのエージェントを移動させる。呼ばれるのは、
	 * 1)エージェントが生成された直後にlocate()から。
	 * 2)マネージャからspecifyManagerが届いたときにspecifyManager()から。
	 * 3)エージェントが消滅した直後にremoveAgent()から。
	 * @param agt 1)の場合、生成されたエージェントのアイコン
	 *            2)の場合、specifyManagerを受け取ったメンバのアイコン
	 * @param env 1)の場合なら環境のアイコン
	 *            2)の場合ならnull
	 */
	private void moveAnime(VIconAg agt, VIcon env) {

		Graphics g = getGraphics();
		Graphics bufG = buffer.getGraphics();

		// チカチカ３。(specifyManager, removeAgentの時はしない)
		if (env != null) {
			drawPerformative(bufG, env, "Instantiation");
			paint(g);
			flushAgent(env, agt);
			waitStep();
		}

		String names[] = new String[agTable.size()]; // 名前
		int dx[] = new int[agTable.size()]; // xの変化量
		int dy[] = new int[agTable.size()]; // yの変化量
		int x1[] = new int[agTable.size()]; // 移動前のx
		int y1[] = new int[agTable.size()]; // 移動前のy

		// 移動前の位置を出す。
		int ac = 0;
		for (Enumeration e = agTable.elements(); e.hasMoreElements();) {
			VIconAg a = (VIconAg) e.nextElement();
			names[ac] = a.name;
			x1[ac] = a.x;
			y1[ac] = a.y;
			ac++;

		}

		// 移動後の位置を計算する。
		ViewerCanvasW.max_x = 1;
		root.setxy(0);

		// 移動前後の変化量を出す。
		boolean notMove = true;
		for (int i = 0; i < ac; i++) {
			VIconAg a = (VIconAg) agTable.get(names[i]);
			dx[i] = a.x - x1[i];
			dy[i] = a.y - y1[i];

			if (dx[i] != 0 || dy[i] != 0)
				notMove = false;
		}
		if (notMove) // removeAgentの場合起こりうる。
			return;

		// 移動アニメ
		for (int c = 0; c <= 12; c++) {
			if (c % SPEED != 0)
				continue;

			// 位置セット
			for (int i = 0; i < ac; i++)
				((VIconAg) agTable.get(names[i])).putxy(
					x1[i] + dx[i] * c / 12,
					y1[i] + dy[i] * c / 12);
			// クリア
			clear();
			// エージェントを描く。
			drawAgent();
			if (env != null && c < 12)
				drawPerformative(bufG, env, "Instantiation");
			// 再描画
			paint(g);
		}

	}

	/**
	 * マネージャを指定する。
	 * locate()でrootの子になっていたメンバを、
	 * マネージャの子にする。
	 */
	void specifyManager(DashMessage m) {
		VIconAg childAgt = (VIconAg) agTable.get(m.to);
		VIconAg parentAgt = (VIconAg) agTable.get(m.from);

		root.children.removeElement(childAgt);
		parentAgt.children.addElement(childAgt);

		moveAnime(childAgt, null);
	}

	/**
	 * is_a線を引いてからえージェントを描く
	 */
	void drawAgent() {
		Graphics bufG = buffer.getGraphics();
		bufG.setColor(Color.yellow);

		for (Enumeration e1 = agTable.elements(); e1.hasMoreElements();) {
			VIconAg p = (VIconAg) e1.nextElement(); // 親
			for (Enumeration e2 = p.children.elements();
				e2.hasMoreElements();
				) {
				VIconAg c = (VIconAg) e2.nextElement(); // 子

				bufG.drawLine(
					p.x + W_ICONW / 2,
					p.y + W_ICONH / 2,
					c.x + W_ICONW / 2,
					c.y + W_ICONH / 2);
			}
		}
		bufG.setColor(Color.black);

		super.drawAgent();
	}

	/**
	 * エージェントを削除する。
	 */
	void removeAgent(String agName) {
		VIconAg ra = (VIconAg) agTable.remove(agName);

		Graphics g = getGraphics();
		drawPerformative(g, ra, "removeAgent");
		flushPeke(ra);
		waitStep();

		root.children.removeElement(ra);

		// 親子関係を破棄する。
		for (Enumeration e = agTable.elements(); e.hasMoreElements();) {
			VIconAg p = (VIconAg) e.nextElement();
			p.children.removeElement(ra);
		}
		// 移動させる(必要無い場合もある)
		moveAnime(null, null);
		// クリア
		clear();
		// エージェントを描く。
		drawAgent();
		// 再描画
		paint(g);
	}

	/*
	void changeScale() {
	
	  Graphics g = getGraphics();
	  // 移動させる(必要無い場合もある)
	  moveAnime(null, null);
	  // クリア
	  clear();
	  // エージェントを描く。
	  drawAgent();
	  // 再描画
	  paint(g);
	}
	*/

	/**
	 * エージェントから１つのエージェントへのメッセージを表示する.
	 */
	/*
	void showUnicastMsg(Message m) {
	  VIconAg fromIcon = (VIconAg)agTable.get(m.from); // 送信Ag
	  VIconAg toIcon = (VIconAg)agTable.get(m.to);     // 受信Ag
	  if (fromIcon == null || toIcon == null)
	    AdipsEnv.exit("ViewerCanvasW.showUnicastMsg(): cannot move msg:\n"+
			  m.printString());
	  moveMsg(fromIcon, toIcon, m.performative());
	}
	*/

	/** 動作環境は使わない */
	void gridSortIcon() {
		//AdipsEnv.exit("ViewerCanvasW.gridSortIcon(): implementation error");
	}

	void createRootChildren() {
		root.children.clear();
	}

	// ダミー
	void initStartXY() {
	}

}
