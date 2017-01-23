package com.ford.kbedoor.sealanalysis.sealgap;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Gui utils - stolen from all around the place.
 *
 */
public class GuiUtils {

    // Some useful GUI class variables
    public final static Dimension hpad5 = new Dimension(5, 1);
    public final static Dimension hpad10 = new Dimension(10, 1);
    public final static Dimension hpad20 = new Dimension(20, 1);
    public final static Dimension hpad25 = new Dimension(25, 1);
    public final static Dimension hpad30 = new Dimension(30, 1);
    public final static Dimension hpad40 = new Dimension(40, 1);
    public final static Dimension hpad80 = new Dimension(80, 1);

    public final static Dimension vpad5 = new Dimension(1, 5);
    public final static Dimension vpad10 = new Dimension(1, 10);
    public final static Dimension vpad20 = new Dimension(1, 20);
    public final static Dimension vpad25 = new Dimension(1, 25);
    public final static Dimension vpad30 = new Dimension(1, 30);
    public final static Dimension vpad40 = new Dimension(1, 40);
    public final static Dimension vpad80 = new Dimension(1, 80);

    public final static Insets insets0 = new Insets(0, 0, 0, 0);
    public final static Insets insets5 = new Insets(5, 5, 5, 5);
    public final static Insets insets10 = new Insets(10, 10, 10, 10);
    public final static Insets insets15 = new Insets(15, 15, 15, 15);
    public final static Insets insets20 = new Insets(20, 20, 20, 20);

    public final static Border emptyBorder0 = new EmptyBorder(0, 0, 0, 0);
    public final static Border emptyBorder5 = new EmptyBorder(5, 5, 5, 5);
    public final static Border emptyBorder10 = new EmptyBorder(10, 10, 10, 10);
    public final static Border emptyBorder15 = new EmptyBorder(15, 15, 15, 15);
    public final static Border emptyBorder20 = new EmptyBorder(20, 20, 20, 20);
    public final static Border etchedBorder10 = new CompoundBorder(new EtchedBorder(), emptyBorder10);
    public final static Border raisedBorder = new BevelBorder(BevelBorder.RAISED);
    public final static Border lightLoweredBorder = new BevelBorder(BevelBorder.LOWERED, Color.white, Color.gray);
    public final static Border loweredBorder = new SoftBevelBorder(BevelBorder.LOWERED);

    //public final static Font defaultFont = new Font("Dialog", Font.PLAIN, 12);
    public final static Font defaultFont = new Font("Times", Font.PLAIN, 12);
    public final static Font boldFont = new Font("Dialog", Font.BOLD, 12);
    public final static Font bigFont = new Font("Dialog", Font.PLAIN, 18);
    public final static Font bigBoldFont = new Font("Dialog", Font.BOLD, 18);
    public final static Font reallyBigFont = new Font("Dialog", Font.PLAIN, 18);
    public final static Font reallyBigBoldFont = new Font("Dialog", Font.BOLD, 24);
    public final static Font typeWriterFont = new Font("Courier", Font.PLAIN, 12);

    public final static Color ideasBGColor = new Color(0.16f, 0.28f, 0.37f);
    public final static Color ideasFGColor = new Color(0.99f, 0.90f, 0.57f);

    public final static String homeDir = System.getProperty("home.dir");
    public final static ImageIcon newIcon = new ImageIcon(homeDir + "/images/new.gif");
    public final static ImageIcon openIcon = new ImageIcon(homeDir + "/images/open.gif");
    public final static ImageIcon saveIcon = new ImageIcon(homeDir + "/images/save.gif");
    public final static ImageIcon exitIcon = new ImageIcon(homeDir + "/images/exit.gif");
    public final static ImageIcon plugIcon = new ImageIcon(homeDir + "/images/plug.gif");
    public final static ImageIcon unplugIcon = new ImageIcon(homeDir + "/images/unplug.gif");
    public final static ImageIcon updateIcon = new ImageIcon(homeDir + "/images/hammer.gif");
    public final static ImageIcon helpIcon = new ImageIcon(homeDir + "/images/help.gif");
    public final static ImageIcon bulbIcon = new ImageIcon(homeDir + "/images/bulb.gif");
    public final static ImageIcon compIcon = new ImageIcon(homeDir + "/images/computer.gif");
    public final static ImageIcon bugIcon = new ImageIcon(homeDir + "/images/bug.gif");
    public final static ImageIcon blankIcon = new ImageIcon(homeDir + "/images/blank-20.gif");
    public final static ImageIcon reportIcon = new ImageIcon(homeDir + "/images/report.gif");
    public final static ImageIcon runningIcon = new ImageIcon(homeDir + "/images/running.gif");
    public final static ImageIcon doneIcon = new ImageIcon(homeDir + "/images/done.gif");
    public final static ImageIcon printIcon = new ImageIcon(homeDir + "/images/print.gif");
    public final static ImageIcon stopIcon = new ImageIcon(homeDir + "/images/stop.gif");


    public final static Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    public final static Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);

    public static void setCenterLocation(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = component.getPreferredSize();
        component.setLocation(screenSize.width / 2 - size.width / 2,
                screenSize.height / 2 - size.height / 2);
    }

    public static void initUIProperties() {
        UIManager.put("TitledBorder.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("Menu.font", defaultFont);
        UIManager.put("MenuBar.font", defaultFont);
        UIManager.put("ToolBar.font", defaultFont);
        UIManager.put("MenuItem.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("RadioButton.font", defaultFont);
        UIManager.put("TabbedPane.font", defaultFont);
        UIManager.put("Tab.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Panel.font", defaultFont);
    }

}
