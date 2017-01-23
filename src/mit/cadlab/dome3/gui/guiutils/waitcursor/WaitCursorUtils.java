package mit.cadlab.dome3.gui.guiutils.waitcursor;

import mit.cadlab.dome3.DomeClientApplication;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Jan 15, 2004
 * Time: 4:01:16 PM
 * To change this template use Options | File Templates.
 */
public class WaitCursorUtils {


//Qing add for wait cursor Jan 15th
    public static void setMainMenuBarCursors(boolean busy) {
        if (busy) {
            //SwingUtilities.invokeLater(new Runnable() {
            //   public void run() {
            DomeClientApplication.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            DomeClientApplication.getMainFrame().getGlassPane().setVisible(true);
            //    }
            // });
        } else {
            // SwingUtilities.invokeLater(new Runnable() {
            //     public void run() {
            DomeClientApplication.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            DomeClientApplication.getMainFrame().getGlassPane().setVisible(false);
            //      }
            //  });
        }
    }

    public static void setWaitCursor(final JFrame frame, boolean busy) {
        if (busy) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Component glasspane = frame.getGlassPane();
                    glasspane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    //glasspane.setCursor(WaitCursorUtils.createCustomCursor(frame));
                    glasspane.setVisible(true);
                    //System.out.println("wait cursor set");
                }
            });
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Component glasspane = frame.getGlassPane();
                    glasspane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    glasspane.setVisible(false);
                    //System.out.println("wait cursor reset");
                }
            });
        }
    }


    public static void showWaitCursor(boolean bWait, Component c) {
        final JRootPane rootPane = rootPaneForComponent(c);
        if (rootPane == null) return;
        final Component glassPane = rootPane.getGlassPane();
        if (glassPane.getMouseListeners().length == 0)
            glassPane.addMouseListener(new MouseAdapter() {
            });

        if (bWait) {
            Cursor cursorWait = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

            rootPane.setCursor(cursorWait);
            glassPane.setCursor(cursorWait);
            glassPane.setVisible(true);
            //System.out.println("wait cursor set");

        } else {

            Cursor cursorDefault = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            glassPane.setVisible(false);
            glassPane.setCursor(cursorDefault);
            rootPane.setCursor(cursorDefault);
            //System.out.println("wait cursor reset");
        }

        glassPane.invalidate();
        rootPane.validate();
    }

    public static JRootPane rootPaneForComponent(Component comp) {
        for (Component p = comp; p != null; p = p.getParent()) {
            if (p instanceof JRootPane) return (JRootPane) p;
            if (comp instanceof JFrame) return ((JFrame) comp).getRootPane();
            if (comp instanceof JDialog) return ((JDialog) comp).getRootPane();
        }
        return null;
    }

    static BufferedImage addAlpha(BufferedImage bi) {
        int w = bi.getWidth();
        int h = bi.getHeight();
        int type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage bia = new BufferedImage(w, h, type);
        Graphics2D g = bia.createGraphics();
        ImageObserver imo = null;
        g.drawImage(bi, 0, 0, imo);
        g.dispose();
        return bia;
    }

    public static Image getImage(Component comp, String relativeFilePath) {

        URL iconURL = ClassLoader.getSystemClassLoader().getResource(relativeFilePath);
        if (iconURL == null) {
            // try context class loader
            iconURL = Thread.currentThread().getContextClassLoader().getResource(relativeFilePath);
            if (iconURL == null) {
                System.err.println("File not found: " + relativeFilePath);
            }
        }
        Image img = Toolkit.getDefaultToolkit().getImage(iconURL);

        try {
            MediaTracker tracker = new MediaTracker(comp);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }


    public static Cursor createCustomCursor(Component comp, String relativeFilePath) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image im = tk.getImage(cl.getResource(relativeFilePath));
        try {
            MediaTracker tracker = new MediaTracker(comp);
            tracker.addImage(im, 0);
            tracker.waitForID(0);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        int w = im.getWidth(null);
        int h = im.getHeight(null);
        int pw = Toolkit.getDefaultToolkit().getBestCursorSize(w, h).width;
        int ph = Toolkit.getDefaultToolkit().getBestCursorSize(w, h).height;
        BufferedImage sizedTransBim = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_ARGB); //try TYPE_INT_ARGB_PRE
        Graphics2D g = sizedTransBim.createGraphics();
        g.drawImage(im, 0, 0, comp);
        g.dispose();


        Point hotspot = new Point(0, 0);


        Cursor Custom_Cursor = tk.createCustomCursor(sizedTransBim, hotspot, "my Cursor");
        return Custom_Cursor;
    }

    public static void main(String[] args) {
        TransparantWindow waitWin = new TransparantWindow("mit/cadlab/dome3/icons/installerSplash.gif", "loading...");
        showWaitCursor(true, waitWin);
    }
}
