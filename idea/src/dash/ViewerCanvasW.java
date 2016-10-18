package dash;

import java.awt.*;
import java.util.*;

/**
 * ������p�L�����o�X
 */
public class ViewerCanvasW extends ViewerCanvas {
	public VIconAg root; // ����ɂ��鉼�z�̃G�[�W�F���g
	static int max_x = 1; // ^^;
	private String dvmname = "";

	/**
	 * �R���X�g���N�^
	 * @param envname �z�X�g�����܂܂Ȃ�����
	 * @param envhost �z�X�g��
	 */
	ViewerCanvasW(String dvmname, Viewer v) {
		super(dvmname, false, v);
		this.dvmname = dvmname;
		this.root = new VIconAg("root", false); // �ʒu���߂����邽�߂̃_�~�[���
		this.max_x = 1;
	}

	/**
	 * �G�̏�����. show()���ꂽ��ɌĂ΂��B
	 * show()���Ȃ���peer�����ꂸ�Apeer�����Ȃ���createImage()�ł��Ȃ��B
	 */
	public void initialize() {
		super.initialize();
		Image envImage = createEnvImage(false, this.dvmname);
		envIcon = new VIcon(ENVX, ENVY, envImage, false);
		clear();
		repaint();
	}

	/**
	 * �V���ɐ������ꂽ�G�[�W�F���g�̈ʒu�����肷��B
	 * �������ꂽ�Ă̎��́A�܂��}�l�[�W������������Ă��Ȃ��̂�
	 * ��肠����root�̎q�ɂȂ�B
	 * �}�l�[�W�������������ƁAspecifyManager()���ǂ����炩�Ă΂��
	 * �؂ɂȂ�B
	 * @param msg CreateInstance���b�Z�[�W
	 * @param agt ���O�ƃC���[�W�������܂��Ă���G�[�W�F���g�̃A�C�R��
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
	 * �S�ẴG�[�W�F���g���ړ�������B�Ă΂��̂́A
	 * 1)�G�[�W�F���g���������ꂽ�����locate()����B
	 * 2)�}�l�[�W������specifyManager���͂����Ƃ���specifyManager()����B
	 * 3)�G�[�W�F���g�����ł��������removeAgent()����B
	 * @param agt 1)�̏ꍇ�A�������ꂽ�G�[�W�F���g�̃A�C�R��
	 *            2)�̏ꍇ�AspecifyManager���󂯎���������o�̃A�C�R��
	 * @param env 1)�̏ꍇ�Ȃ���̃A�C�R��
	 *            2)�̏ꍇ�Ȃ�null
	 */
	private void moveAnime(VIconAg agt, VIcon env) {

		Graphics g = getGraphics();
		Graphics bufG = buffer.getGraphics();

		// �`�J�`�J�R�B(specifyManager, removeAgent�̎��͂��Ȃ�)
		if (env != null) {
			drawPerformative(bufG, env, "Instantiation");
			paint(g);
			flushAgent(env, agt);
			waitStep();
		}

		String names[] = new String[agTable.size()]; // ���O
		int dx[] = new int[agTable.size()]; // x�̕ω���
		int dy[] = new int[agTable.size()]; // y�̕ω���
		int x1[] = new int[agTable.size()]; // �ړ��O��x
		int y1[] = new int[agTable.size()]; // �ړ��O��y

		// �ړ��O�̈ʒu���o���B
		int ac = 0;
		for (Enumeration e = agTable.elements(); e.hasMoreElements();) {
			VIconAg a = (VIconAg) e.nextElement();
			names[ac] = a.name;
			x1[ac] = a.x;
			y1[ac] = a.y;
			ac++;

		}

		// �ړ���̈ʒu���v�Z����B
		ViewerCanvasW.max_x = 1;
		root.setxy(0);

		// �ړ��O��̕ω��ʂ��o���B
		boolean notMove = true;
		for (int i = 0; i < ac; i++) {
			VIconAg a = (VIconAg) agTable.get(names[i]);
			dx[i] = a.x - x1[i];
			dy[i] = a.y - y1[i];

			if (dx[i] != 0 || dy[i] != 0)
				notMove = false;
		}
		if (notMove) // removeAgent�̏ꍇ�N���肤��B
			return;

		// �ړ��A�j��
		for (int c = 0; c <= 12; c++) {
			if (c % SPEED != 0)
				continue;

			// �ʒu�Z�b�g
			for (int i = 0; i < ac; i++)
				((VIconAg) agTable.get(names[i])).putxy(
					x1[i] + dx[i] * c / 12,
					y1[i] + dy[i] * c / 12);
			// �N���A
			clear();
			// �G�[�W�F���g��`���B
			drawAgent();
			if (env != null && c < 12)
				drawPerformative(bufG, env, "Instantiation");
			// �ĕ`��
			paint(g);
		}

	}

	/**
	 * �}�l�[�W�����w�肷��B
	 * locate()��root�̎q�ɂȂ��Ă��������o���A
	 * �}�l�[�W���̎q�ɂ���B
	 */
	void specifyManager(DashMessage m) {
		VIconAg childAgt = (VIconAg) agTable.get(m.to);
		VIconAg parentAgt = (VIconAg) agTable.get(m.from);

		root.children.removeElement(childAgt);
		parentAgt.children.addElement(childAgt);

		moveAnime(childAgt, null);
	}

	/**
	 * is_a���������Ă��炦�[�W�F���g��`��
	 */
	void drawAgent() {
		Graphics bufG = buffer.getGraphics();
		bufG.setColor(Color.yellow);

		for (Enumeration e1 = agTable.elements(); e1.hasMoreElements();) {
			VIconAg p = (VIconAg) e1.nextElement(); // �e
			for (Enumeration e2 = p.children.elements();
				e2.hasMoreElements();
				) {
				VIconAg c = (VIconAg) e2.nextElement(); // �q

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
	 * �G�[�W�F���g���폜����B
	 */
	void removeAgent(String agName) {
		VIconAg ra = (VIconAg) agTable.remove(agName);

		Graphics g = getGraphics();
		drawPerformative(g, ra, "removeAgent");
		flushPeke(ra);
		waitStep();

		root.children.removeElement(ra);

		// �e�q�֌W��j������B
		for (Enumeration e = agTable.elements(); e.hasMoreElements();) {
			VIconAg p = (VIconAg) e.nextElement();
			p.children.removeElement(ra);
		}
		// �ړ�������(�K�v�����ꍇ������)
		moveAnime(null, null);
		// �N���A
		clear();
		// �G�[�W�F���g��`���B
		drawAgent();
		// �ĕ`��
		paint(g);
	}

	/*
	void changeScale() {
	
	  Graphics g = getGraphics();
	  // �ړ�������(�K�v�����ꍇ������)
	  moveAnime(null, null);
	  // �N���A
	  clear();
	  // �G�[�W�F���g��`���B
	  drawAgent();
	  // �ĕ`��
	  paint(g);
	}
	*/

	/**
	 * �G�[�W�F���g����P�̃G�[�W�F���g�ւ̃��b�Z�[�W��\������.
	 */
	/*
	void showUnicastMsg(Message m) {
	  VIconAg fromIcon = (VIconAg)agTable.get(m.from); // ���MAg
	  VIconAg toIcon = (VIconAg)agTable.get(m.to);     // ��MAg
	  if (fromIcon == null || toIcon == null)
	    AdipsEnv.exit("ViewerCanvasW.showUnicastMsg(): cannot move msg:\n"+
			  m.printString());
	  moveMsg(fromIcon, toIcon, m.performative());
	}
	*/

	/** ������͎g��Ȃ� */
	void gridSortIcon() {
		//AdipsEnv.exit("ViewerCanvasW.gridSortIcon(): implementation error");
	}

	void createRootChildren() {
		root.children.clear();
	}

	// �_�~�[
	void initStartXY() {
	}

}
