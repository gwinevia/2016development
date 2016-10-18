//changed by uchiya
//2004/06/12
package dash;

import java.util.*;

/**
 * ビューア用のエージェントの情報を保持するクラス
 * 
 */
public class VIconAg extends VIcon {
	String name; // エージェント名
	Vector children; // メンバエージェント

	String parentname;

	VIconAg(String n, boolean isR) {
		super(isR);
		name = n;
		children = new Vector();
		// xとyはlocate()で決められる。
	}

	/**
	 * xとyを代入する。
	 */
	void putxy(int x, int y) {
		this.x = x;
		this.y = y;
		rect.x = x;
		rect.y = y;
	}

	/**
	 * xとyを決める。
	 * @param myY 上から数えた段数。最上段は1。親に決められる。
	 */
	void setxy(int myY) {

		// 自分のxy
		y = ViewerCanvas.AGY + (myY - 1) * 70;
		x =
			ViewerCanvas.ENVX
				+ (ViewerCanvasW.max_x - 1) * (ViewerCanvas.W_ICONW + 10);
		//80;
		rect.y = y;
		rect.x = x;

		// 子のxy
		
		for (Enumeration e = children.elements(); e.hasMoreElements();) {
			VIconAg va = (VIconAg) e.nextElement();
			va.setxy(myY + 1);
		}

		// 子がいない場合、1をたす。
		ViewerCanvasW.max_x += children.isEmpty() ? 1 : 0;
	}

	/**
	 * 名前を返す
	 */
	String getName() {
		return name;
	}

	/**
	 * "エージェント名 x y"という文字列を返す。
	 */
	String getIconLocation() {
		return name + " " + x + " " + y;
	}

}
