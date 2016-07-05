package dash;

import java.awt.*;
import java.awt.image.*;

/**
 * �C���[�W(baseImage)�̏�ɁA
 * �R���X�g���N�^�ŗ^����ꂽ�C���[�W(owImage)���d�˂�t�B���^�B
 * baseImage�ɂ͓��������������Ă��悢�B
 * owImage�́A
 * �E��(Color.blue)�͓����ɂȂ�B
 * �EbaseImage�̑傫���Ɠ������Ȃ���΂Ȃ�Ȃ��B
 *
 * �t�B���^������AbaseImage�ɂ͉��̉e�����Ȃ��B
 *
 * �g����:
 * 1. ����ȃ��\�b�h�������B
 *    public Image
 *    overwrap(Image baseImage, Image owImage) {
 *      ImageProducer prod0 = baseImage.getSource();
 *      ImageFilter filter = new TranspFilter(owImage, this);
 *      ImageProducer prod1 = new FilteredImageSource(prod0, filter);
 *      Image image = createImage(prod1);
 *      return image;
 *    }
 * 2. ���\�b�h���Ăяo���B
 *    Image newImage = overwrap(image1, image2);
 */
class TranspFilter extends RGBImageFilter {

//  ColorModel cm;
  static final int BLUE = Color.blue.getRGB();

  int[] owiPixels; // owImage�̃s�N�Z���z��
  int imgW, imgH;

/**
 * �R���X�g���N�^�B
 * �����ŁAowImage���s�N�Z���z��ɕϊ�����B
 * @param ow owImage�B
 * @param ob �Ȃ�ł������H
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

  // �s�N�Z���z��ɕϊ��I
  PixelGrabber pg =
    new PixelGrabber(ow, x, y, imgW, imgH, owiPixels, offset, scansize);
  try {
    pg.grabPixels();
  } catch (InterruptedException e) { e.printStackTrace(); }
}

/**
 * �d�˂��킹
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
