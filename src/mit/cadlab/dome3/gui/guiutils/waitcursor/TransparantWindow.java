package mit.cadlab.dome3.gui.guiutils.waitcursor;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Jan 16, 2004
 * Time: 5:41:56 PM
 * To change this template use Options | File Templates.
 *
 * we can use Robot to capture the desktop
 image, and then open a Window (or JWindow) and draw the captured
 background into it.
 */

import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;

public class TransparantWindow extends JWindow implements ImageObserver {

    Image img;//capture image
    Image image;//splash screen image
    Robot r;
    int pX,pY,width,height;

    String status = "";
    Point mp;

    public static TransparantWindow createWaitCursorWindow(Point location) {

        TransparantWindow win = new TransparantWindow("mit/cadlab/dome3/icons/waitcursor.gif", "loading...", location.x, location.y);

        return win;
    }

    public TransparantWindow(String relativeFilePath, String initialStatus) {
        this(relativeFilePath, initialStatus, -1, -1);
    }

    public TransparantWindow(String relativeFilePath, String initialStatus, int location_X, int location_Y) {
        super();
        setFont(Templates.FONT11);
        // img = Templates.makeImageIcon("mit/cadlab/dome3/icons/installerSplash.gif").getImage(); //just initiate, will be re-set to captured image
        // image = Templates.makeImageIcon("mit/cadlab/dome3/icons/installerSplash.gif").getImage();
        // file is relative to directories in classpath
        // first file found is used
        URL iconURL = ClassLoader.getSystemClassLoader().getResource(relativeFilePath);
        if (iconURL == null) {
            // try context class loader
            iconURL = Thread.currentThread().getContextClassLoader().getResource(relativeFilePath);
            if (iconURL == null) {
                System.err.println("File not found: " + relativeFilePath);
            }
        }
        img = Toolkit.getDefaultToolkit().getImage(iconURL);
        image = Toolkit.getDefaultToolkit().getImage(iconURL);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }

        status = initialStatus;

        Dimension dim = getToolkit().getScreenSize();
        width = img.getWidth(this);
        height = img.getHeight(this);
        if (location_X < 0 || location_Y < 0) {
            pX = dim.width / 2 - width / 2;
            pY = dim.height / 2 - height / 2;
        } else {
            pX = location_X;
            pY = location_Y;
        }
        //set int in the middle
        setBounds(pX, pY, width, height);

        try {
            r = new Robot();
        } catch (AWTException awe) {
            System.out.println("robot excepton occurred");
        }
        capture();

        addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent m) {
                if (mp == null) return;
                Point p = m.getPoint();
                int x = getX() + p.x - mp.x;
                int y = getY() + p.y - mp.y;
                setLocation(x, y);
                paintP(getGraphics());
            }

            public void mouseMoved(MouseEvent m) {
                mp = m.getPoint();
            }

        });
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent m) {
            }

            public void mouseEntered(MouseEvent m) {
            }

            public void mouseExited(MouseEvent m) {
            }

            public void mouseReleased(MouseEvent m) {
                mp = null;
            }

            public void mousePressed(MouseEvent m) {
                mp = m.getPoint();
            }
        });
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent fe) {
                setSize(0, 0);
                capture();
                setSize(100, 100);
            }

            public void focusLost(FocusEvent fe) {
            }
        });

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));


        setVisible(true);
    }

    public void capture() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if (r != null) {
        	img = r.createScreenCapture(new Rectangle(0, 0, d.width/4, d.height/4));
        }
        else {
        	img = new BufferedImage(d.width/4, d.height/4,
                    BufferedImage.TYPE_INT_RGB);
        }
    }

    public void captureX() {
        Rectangle rect = getBounds();
        setVisible(false);
        Thread.yield();
        Image xmg = null;
        if (r != null) {
        	xmg = r.createScreenCapture(rect);
        }
        else {
        	img = new BufferedImage(rect.width/4, rect.height/4,
                    BufferedImage.TYPE_INT_RGB);
        }
        img.getGraphics().drawImage(xmg, rect.x, rect.y, rect.width, rect.height, this);
        setVisible(true);
    }


    public static void main(String[] args) {
      //  new TransparantWindow("mit/cadlab/dome3/icons/installerSplash.gif", "loading...");
        new TransparantWindow("mit/cadlab/dome3/icons/waitcursor.gif", "loading...",10,10);
    }


    public void paint(Graphics g) {
        Rectangle rect = g.getClipBounds();

        if (!rect.getSize().equals(getSize())) {
            captureX();
        } else {
            paintP(g);
        }
        paintP(g);
    }

    public void paintP(Graphics g) {
        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bImage = WaitCursorUtils.addAlpha(bImage);
        Graphics2D bImageG2D = bImage.createGraphics();
        bImageG2D.drawImage(img, 0, 0, getWidth(), getHeight(), getX(), getY(), getX() + getWidth(), getY() + getHeight(), this);
        bImageG2D.drawImage(image, 0, 0, this);
        // bImageG2D.drawString(status,pX+100,pY+100);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(bImage, 0, 0, this); //85x62 image

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D bounds = g2.getFont().getStringBounds(status, frc);
        int x = (int) ((width - bounds.getWidth()) / 2);
        int y = (int) (height - bounds.getHeight());
        g.drawString(status, x, y);
    }

    public void update(Graphics g) {
        this.paint(g);
    }

    // overrides imageUpdate to control the animated gif's animation
    public boolean imageUpdate(Image img, int infoflags,
                               int x, int y, int width, int height) {
        if (isShowing() && (infoflags & ALLBITS) != 0)
            repaint();
        if (isShowing() && (infoflags & FRAMEBITS) != 0)
            repaint();
        return isShowing();
    }

    public void showStatus(final String _status) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        status = _status;
                        paintP(getGraphics());
                    }
                });
    }

    public void close() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(false);
                dispose();
            }
        });
    }
}








