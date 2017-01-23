// Templates.java
package mit.cadlab.dome3.swing;

import edu.stanford.ejalbert.BrowserLauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.MemoryImageSource;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.NumberFormat;
import java.util.StringTokenizer;
import java.util.Vector;

import mit.cadlab.dome3.gui.guiutils.customGui.classLoader.JarLoader;

public class Templates implements GuiConstants {

    public static void openURL(String url) throws java.io.IOException {
        BrowserLauncher.openURL(url);
    }

    // icons
    /**
     * Loads image from system class loader.
     * @param relativeFilePath
     * @return image icon for relativeFilePath or empty image icon
     */
    public static ImageIcon makeImageIcon(String relativeFilePath) {
        return makeImageIcon(relativeFilePath, ClassLoader.getSystemClassLoader());
    }

    /**
     * Loads image using specified class loader.
     * @param relativeFilePath
     * @param cl
     * @return image icon for relativeFilePath or empty image icon
     */
    public static ImageIcon makeImageIcon(String relativeFilePath, ClassLoader cl) {
        // file is relative to directories in classpath
        // first file found is used
        URL iconURL = cl.getResource(relativeFilePath);
        if (iconURL == null) {
            // try context class loader
	        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
	        if (contextLoader instanceof JarLoader) {
		        ImageIcon icon = ((JarLoader) contextLoader).getIcon(relativeFilePath);
		        if (icon != null)
		            return icon;
	        } else {
		        iconURL = contextLoader.getResource(relativeFilePath);
	        }
        }
	    if (iconURL == null) {
		    System.err.println("File not found: " + relativeFilePath);
		    return new ImageIcon();
	    }
	    return new ImageIcon(iconURL);
    }

    // size
    public static void setFixedSize(JComponent comp, Dimension size) {
        comp.setMinimumSize(size);
        comp.setPreferredSize(size);
        comp.setMaximumSize(size);
    }

    // labels
    public static JLabel makeLabel() {
        return makeLabel(null, FONT11);
    }

    public static JLabel makeLabel(String text) {
        return makeLabel(text, FONT11);
    }

    public static JLabel makeLabel(String text, Font f) {
        JLabel l = new JLabel(text);
        l.setFont(f);
        return formatLabel(l);
    }

    protected static JLabel formatLabel(JLabel l) {
        l.setForeground(Color.black);
        return l;
    }

    // textfields
    public static JTextField makeTextField(String text) {
        JTextField tf = new JTextField(text);
        return formatTextField(tf);
    }

    public static DTextField makeDTextField() {
        DTextField tf = new DTextField();
        return (DTextField) formatTextField(tf);
    }

    public static DTextField makeDTextField(String text) {
        DTextField tf = new DTextField(text);
        return (DTextField) formatTextField(tf);
    }

    public static DTextField makeDTextField(String text, int columns) {
        DTextField tf = new DTextField(text, columns);
        return (DTextField) formatTextField(tf);
    }

    public static JTextField formatTextField(JTextField tf) {
        tf.setFont(FONT11);
        return tf;
    }

    public static JFormattedTextField makeDIntegerField() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        DNumberTextField tf = new DNumberTextField(format);
        return tf;
    }

    public static JFormattedTextField makeDRealField() {
        NumberFormat format = NumberFormat.getNumberInstance();
        DNumberTextField tf = new DNumberTextField(format);
        return tf;
    }

    // textareas
    public static JTextArea makeDisplayTextArea(String text) {
        return makeDisplayTextArea(text, FONT11);
    }

    public static JTextArea makeDisplayTextArea(String text, Font f) {
        JTextArea ta = new JTextArea(text);
        ta.setFont(f);
        ta.setLineWrap(false);
        ta.setWrapStyleWord(true);
        ta.setOpaque(false);
        ta.setRequestFocusEnabled(false);
        ta.setEditable(false);
        ta.setBackground(NOT_EDITABLE_COLOR);
        return ta;
    }

    public static DTextArea makeDTextArea(String text) {
        DTextArea ta = new DTextArea(text);
        return (DTextArea) formatTextArea(ta);
    }

    public static DTextArea makeDTextArea(int rows, int cols) {
        DTextArea ta = new DTextArea(rows, cols);
        return (DTextArea) formatTextArea(ta);
    }

    public static DTextArea makeDTextArea(String text, int rows, int cols) {
        DTextArea ta = new DTextArea(text, rows, cols);
        return (DTextArea) formatTextArea(ta);
    }

    protected static JTextArea formatTextArea(JTextArea ta) {
        ta.setFont(FONT11);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        return ta;
    }

    // textpane
    public static DTextPane makeDTextPane() {
        DTextPane tp = new DTextPane();
        tp.setFont(FONT12);
        return tp;
    }

    // buttons
    public static JToggleButton makeImageToggleButton(Icon icon) {
        JToggleButton b = new JToggleButton(icon);
        b.setFocusPainted(false);
        b.setMargin(new Insets(0, 0, 0, 0));
        return b;
    }

    public static JButton makeButton(Action action) {
        return makeButton(action, FONT11);
    }

    public static JButton makeButton(Action action, Font f) {
        JButton b = new JButton(action);
        return formatButton(b, f);
    }

    public static JButton makeButton(String text) {
        return makeButton(text, FONT11);
    }

    public static JButton makeButton(String text, Font f) {
        JButton b = new JButton(text);
        return formatButton(b, f);
    }

    public static JButton makeButton(String text, ActionListener l) {
        return makeButton(text, FONT11, l);
    }

    public static JButton makeButton(String text, Font f, ActionListener l) {
        JButton b = makeButton(text, f);
        b.addActionListener(l);
        return b;
    }

    public static JButton makeImageButton(String filename) {
        ImageIcon icon = makeImageIcon(filename);
        return makeImageButton(icon);
    }

    public static JButton makeImageButton(Icon icon) {
        JButton b = new JButton(icon);
        b.setFocusPainted(false);
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        Dimension d = new Dimension(width + 5, height + 5);
        setFixedSize(b, d);
        b.setMargin(new Insets(0, 0, 0, 0));
        return b;
    }

    public static JButton makeImageButton(String filename, ActionListener l) {
        ImageIcon icon = makeImageIcon(filename);
        return makeImageButton(icon, l);
    }

    public static JButton makeImageButton(Icon icon, ActionListener l) {
        JButton b = makeImageButton(icon);
        b.addActionListener(l);
        return b;
    }

    public static JButton makeImageButton(Icon icon, Icon pressedIcon,
                                          Icon rolloverIcon, Icon selectedIcon) {
        JButton b = makeImageButton(icon);
        b.setPressedIcon(pressedIcon);
        b.setRolloverIcon(rolloverIcon);
        b.setSelectedIcon(selectedIcon);
        return b;
    }

    public static JButton makeImageButton(Icon icon, Icon pressedIcon,
                                          Icon rolloverIcon, Icon selectedIcon,
                                          ActionListener l) {
        JButton b = makeImageButton(icon, pressedIcon, rolloverIcon, selectedIcon);
        b.addActionListener(l);
        return b;
    }

    public static JButton makeListArrowButton(String direction) {
        // valid directions are "up","down","left","right"
        direction = direction.substring(0, 1).toUpperCase() + direction.substring(1).toLowerCase();
        ImageIcon icon = makeImageIcon("mit/cadlab/dome3/icons/arrow/list" + direction + ".gif");
        ImageIcon iconOver = makeImageIcon("mit/cadlab/dome3/icons/arrow/list" + direction + "Over.gif");
        return makeImageButton(icon, icon, iconOver, icon);
    }

    public static JButton makeListArrowButton(String direction, ActionListener l) {
        JButton b = makeListArrowButton(direction);
        b.addActionListener(l);
        return b;
    }

    protected static JButton formatButton(JButton b, Font f) {
        b.setFocusPainted(false);
        b.setFont(f);
        b.setMargin(new Insets(2, 10, 2, 10));
        return b;
    }

    // checkboxes
    public static JCheckBox makeCheckBox() {
        return makeCheckBox(null, false);
    }

    public static JCheckBox makeCheckBox(boolean selected) {
        return makeCheckBox(null, selected);
    }

    public static JCheckBox makeCheckBox(String text) {
        return makeCheckBox(text, false);
    }

    public static JCheckBox makeCheckBox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        return formatCheckBox(cb);
    }

    public static JCheckBox makeCheckBox(String text, boolean selected, boolean textOnLeft) {
        JCheckBox cb = new JCheckBox(text, selected);
        if (textOnLeft)
            cb.setHorizontalTextPosition(SwingConstants.LEFT);
        return formatCheckBox(cb);
    }

    public static JCheckBox formatCheckBox(JCheckBox cb) {
        cb.setFocusPainted(false);
        cb.setFont(FONT11);
        return cb;
    }

    // radio buttons
    public static JRadioButton makeRadioButton() {
        return makeRadioButton(null, false);
    }

    public static JRadioButton makeRadioButton(String text) {
        return makeRadioButton(text, false);
    }

    public static JRadioButton makeRadioButton(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text, selected);
        rb.setFocusPainted(false);
        rb.setFont(FONT11);
        return rb;
    }

    // combo boxes
    public static JComboBox makeComboBox(ComboBoxModel model) {
        JComboBox cb = new JComboBox(model);
        return formatComboBox(cb);
    }

    public static JComboBox makeComboBox(Object[] choices) {
        JComboBox cb = new JComboBox(choices);
        return formatComboBox(cb);
    }

    public static DComboBox makeDComboBox() {
        DComboBox cb = new DComboBox();
        return (DComboBox) formatComboBox(cb);
    }

    public static DComboBox makeDComboBox(ComboBoxModel model) {
        DComboBox cb = new DComboBox(model);
        return (DComboBox) formatComboBox(cb);
    }

    public static DComboBox makeDComboBox(Object[] choices) {
        DComboBox cb = new DComboBox(choices);
        return (DComboBox) formatComboBox(cb);
    }

    public static JComboBox formatComboBox(JComboBox cb) {
        cb.setFont(FONT11);
        setFixedSize(cb, cb.getPreferredSize());
        return cb;
    }

    // lists
    public static JList makeList() {
        JList l = new JList();
        return formatList(l);
    }

    public static JList makeList(ListModel model) {
        JList l = new JList(model);
        return formatList(l);
    }

    public static JList makeList(Object[] choices) {
        JList l = new JList(choices);
        return formatList(l);
    }

    public static JList makeList(Vector choices) {
        JList l = new JList(choices);
        return formatList(l);
    }

    public static DList makeDList() {
        DList l = new DList();
        return (DList) formatList(l);
    }

    public static DList makeDList(ListModel model) {
        DList l = new DList(model);
        return (DList) formatList(l);
    }

    public static DList makeDList(Object[] choices) {
        DList l = new DList(choices);
        return (DList) formatList(l);
    }

    public static DList makeDList(Vector choices) {
        DList l = new DList(choices);
        return (DList) formatList(l);
    }

    protected static JList formatList(JList l) {
        l.setFont(FONT11);
        return l;
    }

    // tabbed panes
    public static JTabbedPane makeTabbedPane() { // default tabs on bottom
        JTabbedPane tabs = makeTabbedPaneTop();
        tabs.setTabPlacement(JTabbedPane.BOTTOM);
        return tabs;
    }

    public static JTabbedPane makeTabbedPaneTop() {
        JTabbedPane tabs = new JTabbedPane() {
            public boolean hasFocus() {
                return false;
            }
        };
        tabs.setFont(FONT11);
        tabs.setRequestFocusEnabled(false);
        return tabs;
    }

    // Borders

    public static void setEmptyBorder(JComponent comp) { // default size 5
        setEmptyBorder(comp, 5);
    }

    public static void setEmptyBorder(JComponent comp, int size) {
        setEmptyBorder(comp, size, size, size, size);
    }

    public static void setEmptyBorder(JComponent comp, int top, int left,
                                      int bottom, int right) {
        comp.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    // Images
    public static Image makeRectangleImage(int w, int h, String colorString) {
        return makeRectangleImage(w, h, getColorFromString(colorString));
    }

    public static Image makeRectangleImage(int w, int h, Color c) {
        if (c == null) return null;
        int colorInt = c.getRGB();
        int pix[] = new int[w * h];
        for (int i = 0; i < pix.length; ++i)
            pix[i] = colorInt;
        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w, h, pix, 0, w));
    }


    // GridBagLayout
    public static void layoutGridBag(JPanel c, JComponent[] comps,
                                     GridBagConstraints[] gbcs) {
        GridBagLayout gridbag = new GridBagLayout();
        c.setLayout(gridbag);
        for (int i = 0; i < gbcs.length; ++i) {
            gridbag.setConstraints(comps[i], gbcs[i]);
            c.add(comps[i]);
        }
    }

    public static void layoutGridBagB(JPanel c, JComponent[] comps,
                                      GridBagConstraints[] gbcs) {
        layoutGridBag(c, comps, gbcs);
        setEmptyBorder(c); // adds default EmptyBorder around panel
    }

    // pause
    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
        }
    }


    // converters
    public static Color getColorFromString(String colorString) {
        Color c = null;
        try {
            if (colorString.startsWith("#")) { // hex representation "#008080"
                c = Color.decode(colorString);
            } else if (colorString.indexOf(',') != -1) { // numeric representation "255,0,128"
                StringTokenizer st = new StringTokenizer(colorString, ", \t\n\r\f");
                int r = Integer.parseInt(st.nextToken());
                int g = Integer.parseInt(st.nextToken());
                int b = Integer.parseInt(st.nextToken());
                c = new Color(r, g, b);
            } else { // try if it is a static Color constant "red"
                Field f = Color.class.getField(colorString);
                c = (Color) f.get(null);
            }
        } catch (Exception ex) {
            System.err.println("invalid color " + colorString + ": " + ex.getMessage());
        }
        return c;
    }

    // components for testing
    public static JFrame makeTestFrame(String title) {
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return f;
    }

    public static JDialog makeTestDialog(String title) {
        JDialog d = new JDialog();
        d.setTitle(title);
        d.setSize(200, 200);
        d.pack();
        return d;
    }

    public static JPanel makeTestPanel(String text) {
        JPanel p = new JPanel();
        p.add(makeLabel(text));
        p.setMinimumSize(new Dimension(400, 250));
        return p;
    }

}
