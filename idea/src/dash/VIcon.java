package dash;

import java.awt.*;

/**
 * �r���[�A�p�̃A�C�R���̏���ێ�����N���X
 */
public class VIcon {
  int x;
  int y;
  Image image;			// ���̊G
  String comment;		// �R�����g
  protected Rectangle rect;
  private boolean isSelected;	// �I������Ă����true;
  private boolean isRepository;

/**
 * �R���X�g���N�^.
 */
public
VIcon(boolean isRepository) {
  this.isRepository = isRepository;
  rect = new Rectangle();
  if (isRepository ) {
    rect.width = ViewerCanvas.R_ICONW;
    rect.height = ViewerCanvas.R_ICONH;
  }
  else {
    rect.width = ViewerCanvas.W_ICONW;
    rect.height = ViewerCanvas.W_ICONH;
  }
}


/**
 * �R���X�g���N�^.
 * ViewerCanvas.showAgToEnvMsg()����Ă΂��B
 */
public
VIcon(int x, int y, Image image, boolean isRepository) {
  this.isRepository = isRepository;
  this.x = x;
  this.y = y;
  this.image = image;
  if (isRepository ) {
    rect = new Rectangle(x, y, ViewerCanvas.R_ICONW, ViewerCanvas.R_ICONH);
  }
  else {
    rect = new Rectangle(x, y, ViewerCanvas.W_ICONW, ViewerCanvas.W_ICONH);
  }
}

/** p���A�C�R���̓����ɂ��邩��Ԃ� */
public boolean contains(Point p) {
  return rect.contains(p);
}

/** �I����Ԃ�ύX���� */
public void setSelected(boolean b) {
  isSelected = b;
}

/** �I����Ԃ�Ԃ� */
public boolean isSelected() {
  return isSelected;
}
}
