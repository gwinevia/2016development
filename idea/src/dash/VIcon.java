package dash;

import java.awt.*;

/**
 * ビューア用のアイコンの情報を保持するクラス
 */
public class VIcon {
  int x;
  int y;
  Image image;			// 環境の絵
  String comment;		// コメント
  protected Rectangle rect;
  private boolean isSelected;	// 選択されていればtrue;
  private boolean isRepository;

/**
 * コンストラクタ.
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
 * コンストラクタ.
 * ViewerCanvas.showAgToEnvMsg()から呼ばれる。
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

/** pがアイコンの内側にあるかを返す */
public boolean contains(Point p) {
  return rect.contains(p);
}

/** 選択状態を変更する */
public void setSelected(boolean b) {
  isSelected = b;
}

/** 選択状態を返す */
public boolean isSelected() {
  return isSelected;
}
}
