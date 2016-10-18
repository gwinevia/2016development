/***********************************************************************
���|�W�g���p�L�����o�X
***********************************************************************/
package dash;

import java.awt.*;
import java.io.*;
import java.util.*;

public class ViewerCanvasR
  extends ViewerCanvas
{
  /** �ʒu���ߗp */
  private int lastY;

  /** �ʒu���ߗp */
  private int lastX;

  /** lastY�̍ő�l. ������z����ƁAlastY��0�ɂȂ�AlastX��++�����.*/
  private int maxY;

  /** �ʒu�̏�� */
  private Hashtable iconLocation;

  MediaTracker mt;
	String dvmname;

/**
 * �R���X�g���N�^
 * @param envname �z�X�g�����܂܂Ȃ�����
 * @param envhost �z�X�g��
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
 * �G�̏�����. show()���ꂽ��ɌĂ΂��B
 * show()���Ȃ���peer�����ꂸ�Apeer�����Ȃ���createImage()�ł��Ȃ��B
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
 * �V���ɒǉ����ꂽ�G�[�W�F���g�̈ʒu�����肷��B
 * @param msg null
 * @param agt ���O�ƃC���[�W�������܂��Ă���G�[�W�F���g�̃A�C�R��
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
�}�l�[�W�����w�肷��(R�͂ł��Ȃ�����)
***********************************************************************/
void specifyManager(DashMessage m) {
  //AdipsEnv.exit("ViewerCanvasR.specifyManager(): implementation error");
}

/***********************************************************************
�G�[�W�F���g���폜���郁�\�b�h(���g�p)
***********************************************************************/
void removeAgent(String agName) {
  VIconAg ra = (VIconAg)agTable.remove(agName);

  Graphics g = getGraphics();
  // �N���A
  clear();
  // �G�[�W�F���g��`���B
  drawAgent();
  // �ĕ`��
  paint(g);
}

/**
 * �G�[�W�F���g����P�̃G�[�W�F���g�ւ̃��b�Z�[�W��\������.
 */
/*
void showUnicastMsg(Message m) {
  VIconAg fromIcon = (VIconAg)agTable.get(m.from); // ���MAg
  VIconAg toIcon = (VIconAg)agTable.get(m.to);     // ��MAg
  if (fromIcon == null || toIcon == null)
    AdipsEnv.exit("ViewerCanvasR.showUnicastMsg(): cannot move msg:\n"+
		  m.printString());
  moveMsg(fromIcon, toIcon, m.performative());
}
*/

/***********************************************************************
                   ���j���[�ɑ΂���A�N�V�����̏���
***********************************************************************/

/**
 * �i�q��ɐ��񂳂���B
 */
public void gridSortIcon() {
  Graphics g = getGraphics();
  Graphics bufG = buffer.getGraphics();

  String names[] = new String[agTable.size()]; // ���O
  int dx[]  = new int[agTable.size()];	 // x�̕ω���
  int dy[]  = new int[agTable.size()];	 // y�̕ω���
  int x1[] = new int[agTable.size()];	 // �ړ��O��x
  int y1[] = new int[agTable.size()];	 // �ړ��O��y

  // �ړ��O�̈ʒu���o���B
  int ac = 0;
  for (Enumeration e = agTable.elements(); e.hasMoreElements(); ) {
    VIconAg a = (VIconAg)e.nextElement();
    names[ac] = a.name;
    x1[ac] = a.x;
    y1[ac] = a.y;
    ac++;
  }
  
  // �ړ���̈ʒu���v�Z����B
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

  // �ړ��O��̕ω��ʂ��o���B
  boolean notMove = true;
  for (int i=0; i<ac; i++) {
    VIconAg a = (VIconAg)agTable.get(names[i]);
    dx[i] = a.x - x1[i];
    dy[i] = a.y - y1[i];
    if (dx[i]!=0 || dy[i]!=0)
      notMove = false;
  }
  if (notMove) // ���񒼌�̏ꍇ�N���肤��B
    return;

  // �ړ��A�j��
  for (int c=0; c<=12; c++) {
    if (c % SPEED != 0) continue;

    // �ʒu�Z�b�g
    for (int i=0; i<ac; i++)
      ((VIconAg)agTable.get(names[i]))
	.putxy(x1[i] + dx[i]*c/12, y1[i] + dy[i]*c/12);
    // �N���A
    clear();
    // �G�[�W�F���g��`���B
    drawAgent();
    // �ĕ`��
    paint(g);
  }
}

/**
 * �A�C�R���̈ʒu�����t�@�C���ɕۑ�����B
 * �t�@�C�����́A"location.dat"�B
 * �A�C�R���̏��́A"�G�[�W�F���g�� x���W y���W"�Ƃ����s�ŕ\�����B
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
 * �A�C�R���̈ʒu�����t�@�C������ǂݍ��݁AiconLocation�ɃZ�b�g����B
 * iconLocation�́Akey:�N���X�G�[�W�F���g�� / val:"x y"
 * (�t�@�C���͂Ȃ��ꍇ������)
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
      table.put(line.substring(0, p), // "�G�[�W�F���g��"
		line.substring(p+1)); // "x���W y���W"
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
	// �������Ȃ�
}

void initStartXY ( ) {
  lastX = 0;
  lastY = 0;
}

}

