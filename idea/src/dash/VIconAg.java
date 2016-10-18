//changed by uchiya
//2004/06/12
package dash;

import java.util.*;

/**
 * �r���[�A�p�̃G�[�W�F���g�̏���ێ�����N���X
 * 
 */
public class VIconAg extends VIcon {
	String name; // �G�[�W�F���g��
	Vector children; // �����o�G�[�W�F���g

	String parentname;

	VIconAg(String n, boolean isR) {
		super(isR);
		name = n;
		children = new Vector();
		// x��y��locate()�Ō��߂���B
	}

	/**
	 * x��y��������B
	 */
	void putxy(int x, int y) {
		this.x = x;
		this.y = y;
		rect.x = x;
		rect.y = y;
	}

	/**
	 * x��y�����߂�B
	 * @param myY �ォ�琔�����i���B�ŏ�i��1�B�e�Ɍ��߂���B
	 */
	void setxy(int myY) {

		// ������xy
		y = ViewerCanvas.AGY + (myY - 1) * 70;
		x =
			ViewerCanvas.ENVX
				+ (ViewerCanvasW.max_x - 1) * (ViewerCanvas.W_ICONW + 10);
		//80;
		rect.y = y;
		rect.x = x;

		// �q��xy
		
		for (Enumeration e = children.elements(); e.hasMoreElements();) {
			VIconAg va = (VIconAg) e.nextElement();
			va.setxy(myY + 1);
		}

		// �q�����Ȃ��ꍇ�A1�������B
		ViewerCanvasW.max_x += children.isEmpty() ? 1 : 0;
	}

	/**
	 * ���O��Ԃ�
	 */
	String getName() {
		return name;
	}

	/**
	 * "�G�[�W�F���g�� x y"�Ƃ����������Ԃ��B
	 */
	String getIconLocation() {
		return name + " " + x + " " + y;
	}

}
