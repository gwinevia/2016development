package dash;

import java.awt.*;
import java.awt.image.*;

/**
 * イメージ(baseImage)の上に、
 * コンストラクタで与えられたイメージ(owImage)を重ねるフィルタ。
 * baseImageには透明部分があってもよい。
 * owImageは、
 * ・青(Color.blue)は透明になる。
 * ・baseImageの大きさと等しくなければならない。
 *
 * フィルタだから、baseImageには何の影響もない。
 *
 * 使い方:
 * 1. こんなメソッドを書く。
 *    public Image
 *    overwrap(Image baseImage, Image owImage) {
 *      ImageProducer prod0 = baseImage.getSource();
 *      ImageFilter filter = new TranspFilter(owImage, this);
 *      ImageProducer prod1 = new FilteredImageSource(prod0, filter);
 *      Image image = createImage(prod1);
 *      return image;
 *    }
 * 2. メソッドを呼び出す。
 *    Image newImage = overwrap(image1, image2);
 */
class TranspFilter extends RGBImageFilter {

//  ColorModel cm;
  static final int BLUE = Color.blue.getRGB();

  int[] owiPixels; // owImageのピクセル配列
  int imgW, imgH;

/**
 * コンストラクタ。
 * ここで、owImageをピクセル配列に変換する。
 * @param ow owImage。
 * @param ob なんでもいい？
 */
public
TranspFilter(Image ow, ImageObserver ob) {
  canFilterIndexColorModel = false;
//  cm = ColorModel.getRGBdefault();

  int x=0, y=0;
  imgW = ow.getWidth(ob);
  imgH = ow.getHeight(ob);
  owiPixels = new int[imgW*imgH];
  int scansize = imgW, offset=0;

  // ピクセル配列に変換！
  PixelGrabber pg =
    new PixelGrabber(ow, x, y, imgW, imgH, owiPixels, offset, scansize);
  try {
    pg.grabPixels();
  } catch (InterruptedException e) { e.printStackTrace(); }
}

/**
 * 重ねあわせ
 */
public int
filterRGB(int x, int y, int pixel) {
  int owiPixel = owiPixels[y*imgW + x];
  if (owiPixel == BLUE)
    return pixel;
  else
    return owiPixel;
}

}
