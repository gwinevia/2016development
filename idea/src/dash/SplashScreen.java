package dash;

import java.awt.*;

/**
 * スプラッシュ画面
 */
public class SplashScreen extends Canvas {
	public SplashScreen() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setBackground(Color.white);

		Font font = new Font("Dialog", Font.PLAIN, 10);
		setFont(font);
		fm = getFontMetrics(font);

		image = getToolkit().getImage(
				getClass().getResource("resources/idea-ver1.3.1.gif"));
		MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(image, 0);

		try {
			tracker.waitForAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

		win = new Window(new Frame());

		Dimension screen = getToolkit().getScreenSize();
		Dimension size = new Dimension(image.getWidth(this) + 2, image
				.getHeight(this)
				+ 2 + PROGRESS_HEIGHT);
		win.setSize(size);

		win.setLayout(new BorderLayout());
		win.add(BorderLayout.CENTER, this);

		win.setLocation((screen.width - size.width) / 2,
				(screen.height - size.height) / 2);
		win.validate();
		// win.show();
		win.setVisible(true);
		/*
		 * synchronized(this) { try { wait(); } catch(InterruptedException ie) {
		 * Log.log(Log.ERROR,this,ie); } }
		 */
	}

	public void dispose() {
		win.dispose();
	}

	public synchronized void advance() {
		progress++;
		repaint();

		// wait for it to be painted to ensure progress is updated
		// continuously
		try {
			wait();
		} catch (InterruptedException ie) {
			// Log.log(Log.ERROR,this,ie);
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public synchronized void paint(Graphics g) {
		Dimension size = getSize();

		if (offscreenImg == null) {
			offscreenImg = createImage(size.width, size.height);
			offscreenGfx = offscreenImg.getGraphics();
			offscreenGfx.setFont(getFont());
		}

		offscreenGfx.setColor(Color.gray);
		offscreenGfx.drawRect(0, 0, size.width - 1, size.height - 1);

		offscreenGfx.drawImage(image, 1, 1, this);

		offscreenGfx.setColor(new Color(168, 173, 189));
		offscreenGfx.fillRect(1, image.getHeight(this) + 1,
				((win.getWidth() - 2) * progress) / 7, PROGRESS_HEIGHT);

		offscreenGfx.setColor(Color.gray);

		String str = "";// "Version ";// + jEdit.getVersion();

		offscreenGfx.drawString(str, (getWidth() - fm.stringWidth(str)) / 2,
				image.getHeight(this) + 1 - fm.getDescent() - 10);

		g.drawImage(offscreenImg, 0, 0, this);

		notify();
	}

	// private members
	private FontMetrics fm;

	private Window win;

	private Image image;

	private Image offscreenImg;

	private Graphics offscreenGfx;

	private int progress;

	private static final int PROGRESS_HEIGHT = 20;
}
