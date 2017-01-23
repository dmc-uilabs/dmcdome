// Templates.java
package mit.cadlab.dome.swing;

import java.awt.*;
import java.awt.image.MemoryImageSource;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.StringTokenizer;
import java.lang.reflect.Field;
import java.net.URL;

public class Templates implements GuiConstants {
  
  // dimensions
  public static Dimension defaultPanelSize = new Dimension(400,200);

  // icons
  public static ImageIcon makeImageIcon(String relativeFilePath) {
    // file is relative to directories in classpath
    // first file found is used
    URL iconURL = ClassLoader.getSystemResource(relativeFilePath);
    if (iconURL == null) {
      throw new RuntimeException("File not found: "+relativeFilePath);
    }
    return new ImageIcon(iconURL);
  }
  // size
  public static void setFixedSize(JComponent comp, Dimension size) {
    comp.setMinimumSize(size);
    comp.setPreferredSize(size);
    comp.setMaximumSize(size);
  }
  
  public static void setFixedWidth(JComponent comp, Dimension prefSize) {
    comp.setMinimumSize(prefSize);
    comp.setPreferredSize(prefSize);
    comp.setMaximumSize(new Dimension(prefSize.width,Integer.MAX_VALUE));
  }

  
  // labels
  public static JLabel makeLabel(String text) {
    return makeLabel(text,FONT11);
  }
  
  public static JLabel makeLabel(String text, Font f) {
    JLabel l = new JLabel(text);
    l.setForeground(Color.black);
    l.setFont(f);
    return l;
  }


  // textfields
  public static UnborderedTextField makeUnborderedTextField() {
    UnborderedTextField tf = new UnborderedTextField();
    return (UnborderedTextField)formatTextField(tf);
  }
  
  public static JPasswordField makePasswordField() {
    JPasswordField tf = new JPasswordField();
    return (JPasswordField)formatTextField(tf);
  }
  
  public static JTextField makeTextField() {
    JTextField tf = new JTextField();
    return formatTextField(tf);
  }
  
  public static JTextField makeTextField(int columns) {
    JTextField tf = new JTextField(columns);
    return formatTextField(tf);
  }

  public static JTextField makeTextField(String text) {
    JTextField tf = new JTextField(text);
    return formatTextField(tf);
  }

  public static JTextField makeTextField(String text, int columns) {
    JTextField tf = new JTextField(text, columns);
    return formatTextField(tf);
  }

  public static DTextField makeDTextField() {
    DTextField tf = new DTextField();
    return (DTextField)formatTextField(tf);
  }
  
  public static DTextField makeDTextField(int columns) {
    DTextField tf = new DTextField(columns);
    return (DTextField)formatTextField(tf);
  }

  public static DTextField makeDTextField(String text) {
    DTextField tf = new DTextField(text);
    return (DTextField)formatTextField(tf);
  }

  public static DTextField makeDTextField(String text, int columns) {
    DTextField tf = new DTextField(text, columns);
    return (DTextField)formatTextField(tf);
  }

  protected static JTextField formatTextField(JTextField tf){
    tf.setFont(FONT12);
    return tf;
  }


  // textareas
  public static JTextArea makeDisplayTextArea(String text) {
    return makeDisplayTextArea(text,FONT11);
  }
  
  public static JTextArea makeDisplayTextArea(String text, Font f) {
    JTextArea ta = new JTextArea(text);
    ta.setFont(f);
    ta.setOpaque(false);
    ta.setRequestFocusEnabled(false);
    ta.setEditable(false);
    ta.setBackground(NOT_EDITABLE_COLOR);
    return ta;
  }
  
  public static DTextArea makeDTextArea() {
    DTextArea ta = new DTextArea();
    return (DTextArea)formatTextArea(ta);
  }
  
  public static DTextArea makeDTextArea(String text) {
    DTextArea ta = new DTextArea(text);
    return (DTextArea)formatTextArea(ta);
  }
  
  public static DTextArea makeDTextArea(int rows, int cols) {
    DTextArea ta = new DTextArea(rows,cols);
    return (DTextArea)formatTextArea(ta);
  }
  
  public static DTextArea makeDTextArea(String text, int rows, int cols) {
    DTextArea ta = new DTextArea(text,rows,cols);
    return (DTextArea)formatTextArea(ta);
  }
  
  protected static JTextArea formatTextArea(JTextArea ta){
    ta.setFont(FONT12);
    return ta;
  }
  

  // buttons
  public static JToggleButton makeImageToggleButton(Icon icon) {
    JToggleButton b = new JToggleButton(icon);
    b.setFocusPainted(false);
    b.setMargin(new Insets(0,0,0,0));
    return b;
  }

  public static JButton makeButton(String text) {
    return makeButton(text,FONT11);
  }
  
  public static JButton makeButton(String text, Font f) {
    JButton b = new JButton(text);
    b.setFocusPainted(false);
    b.setFont(f);
    b.setMargin(new Insets(2,10,2,10));
    return b;
  }
  
  public static JButton makeButton(String text, ActionListener l) {
    return makeButton(text,FONT11,l);
  }
  
  public static JButton makeButton(String text, Font f, ActionListener l) {
    JButton b = makeButton(text,f);
    b.addActionListener(l);
    return b;
  }
  
  public static JButton makeImageButton(String filename) {
    ImageIcon icon = new ImageIcon(filename);
    return makeImageButton(icon);
  }

  public static JButton makeImageButton(Icon icon) {
    JButton b = new JButton(icon);
    b.setFocusPainted(false);
    b.setMargin(new Insets(0,0,0,0));
    return b;
  }
  
  public static JButton makeImageButton(String filename, ActionListener l) {
    ImageIcon icon = new ImageIcon(filename);
    return makeImageButton(icon,l);
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
    JButton b = makeImageButton(icon,pressedIcon,rolloverIcon,selectedIcon);
    b.addActionListener(l);
    return b;
  }  
  
  public static JButton makeListArrowButton(String direction) {
    // valid directions are "up","down","left","right"
    direction = direction.substring(0,1).toUpperCase()+direction.substring(1).toLowerCase();
    ImageIcon icon = new ImageIcon("mit/cadlab/dome/icons/arrow/list"+direction+".gif");
    ImageIcon iconOver = new ImageIcon("mit/cadlab/dome/icons/arrow/list"+direction+"Over.gif");
    return makeImageButton(icon,icon,iconOver,icon);
  }
  
  public static JButton makeListArrowButton(String direction, ActionListener l) {
    JButton b = makeListArrowButton(direction);
    b.addActionListener(l);
    return b;
  }


  // check boxes
  public static JCheckBox makeCheckBox() {
    return makeCheckBox(null,false);
  }
  
  public static JCheckBox makeCheckBox(String text) {
    return makeCheckBox(text,false);
  }
  
  public static JCheckBox makeCheckBox(String text, boolean selected) {
    JCheckBox cb = new JCheckBox(text,selected);
    cb.setFocusPainted(false);
    cb.setFont(FONT11);
    return cb;
  }
  
  
  // radio buttons
  public static JRadioButton makeRadioButton() {
    return makeRadioButton(null,false);
  }
  
  public static JRadioButton makeRadioButton(String text) {
    return makeRadioButton(text,false);
  }
  
  public static JRadioButton makeRadioButton(String text, boolean selected) {
    JRadioButton rb = new JRadioButton(text,selected);
    rb.setFocusPainted(false);
    rb.setFont(FONT11);
    return rb;
  }
  
  
  // combo boxes
  public static JComboBox makeComboBox() {
    JComboBox cb = new JComboBox();
    cb.setFont(FONT11);
    return cb;
  }
  
  public static JComboBox makeComboBox(ComboBoxModel model) {
    JComboBox cb = new JComboBox(model);
    return formatComboBox(cb);
  }

  public static JComboBox makeComboBox(Object[] choices) {
    JComboBox cb = new JComboBox(choices);
    return formatComboBox(cb);
  }
  
  public static JComboBox makeComboBox(Vector choices) {
    JComboBox cb = new JComboBox(choices);
    return formatComboBox(cb);
  }

  protected static JComboBox formatComboBox(JComboBox cb) {
    cb.setFont(FONT11);
    setFixedSize(cb,cb.getPreferredSize());
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
  
  protected static JList formatList(JList l) {
    l.setFont(FONT11);
    return l;
  }
  

  // tabbed panes
  public static JTabbedPane makeTabbedPane() {
    JTabbedPane tabs = new JTabbedPane();
    tabs.setTabPlacement(JTabbedPane.BOTTOM);
    tabs.setFont(FONT11);
    tabs.setRequestFocusEnabled(false);
    return tabs;
  }
  
  public static JTabbedPane makeTopTabbedPane() {
    JTabbedPane tabs = new JTabbedPane();
    tabs.setTabPlacement(JTabbedPane.TOP);
    tabs.setFont(FONT11);
    tabs.setRequestFocusEnabled(false);
    return tabs;
  }
  // JLayeredPane
  public static JLayeredPane makeLayeredPane(int width, int height) {
    // objects added to layered pane from top to bottom
    JLayeredPane p = new JLayeredPane();
    p.setLayout(new LayeredCenterLayout());
    p.setPreferredSize(new Dimension(width,height));
    return p;
  }
  
  
  // Borders
  public static void setLineBorder(JComponent comp) {
    setLineBorder(comp,1);
  }
  
  public static void setLineBorder(JComponent comp, int size) {
    setLineBorder(comp,size,Color.black);
  }
  
  public static void setLineBorder(JComponent comp, int size, Color c) {
    comp.setBorder(BorderFactory.createLineBorder(c,size));
  }
  
  public static void setEmptyBorder(JComponent comp) { // default size 5
    setEmptyBorder(comp,5);
  }
  
  public static void setEmptyBorder(JComponent comp, int size) {
    setEmptyBorder(comp,size,size,size,size);
  }
  
  public static void setEmptyBorder(JComponent comp, int top, int left,
				    int bottom, int right) {
    comp.setBorder(BorderFactory.createEmptyBorder(top,left,bottom,right));
  }
  

  // Images
  public static Image makeRectangleImage(int w, int h, String colorString) {
    return makeRectangleImage(w,h,getColorFromString(colorString));
  }
  
  public static Image makeRectangleImage(int w, int h, Color c) {
    if (c == null) return null;
    int colorInt = c.getRGB();
    int pix[] = new int[w * h];
    for (int i=0; i<pix.length; ++i)
      pix[i] = colorInt;
    return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w, h, pix, 0, w));
  }
  
  // GridBagLayout
  public static void layoutGridBag(JPanel c, JComponent[] comps,
				   GridBagConstraints[] gbcs) {
    GridBagLayout gridbag = new GridBagLayout();
    c.setLayout(gridbag);
    for (int i=0; i<gbcs.length; ++i) {
      gridbag.setConstraints(comps[i],gbcs[i]);
      c.add(comps[i]);
    }
  }
  
  public static void layoutGridBagB(JPanel c, JComponent[] comps,
				    GridBagConstraints[] gbcs) {
    layoutGridBag(c,comps,gbcs);
    setEmptyBorder(c); // adds default EmptyBorder around panel
  }
  
  // for JFrame and JDialog
  public static void layoutGridBag(RootPaneContainer rpc, JComponent[] comps,
				   GridBagConstraints[] gbcs) {
    JPanel p = new JPanel();
    layoutGridBag(p,comps,gbcs);
    rpc.getContentPane().add(p);
  }

  public static void layoutGridBagB(RootPaneContainer rpc, JComponent[] comps,
				   GridBagConstraints[] gbcs) {
    JPanel p = new JPanel();
    layoutGridBagB(p,comps,gbcs);
    rpc.getContentPane().add(p);
  }

  
  // pause
  public static void sleep(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (Exception e) {}
  }
  

  // converters
  public static String[] objectToStringArray(Object[] objects) {
    if (objects==null) return null;
    String[] result = new String[objects.length];
    for (int i=0; i<objects.length; ++i)
      result[i] = objects[i].toString();
    return result;
  }
  
  public static Color getColorFromString(String colorString) {
    Color c = null;
    try {
      if (colorString.startsWith("#")){ // hex representation "#008080"
	c = Color.decode(colorString);
      } else if (colorString.indexOf(',') != -1) { // numeric representation "255,0,128"
	StringTokenizer st = new StringTokenizer(colorString,", \t\n\r\f");
	int r=Integer.parseInt(st.nextToken());
	int g=Integer.parseInt(st.nextToken());
	int b=Integer.parseInt(st.nextToken());
	c = new Color(r, g, b);
      } else { // try if it is a static Color constant "red"
	Field f = Color.class.getField(colorString);
	c = (Color) f.get(null);
      }
    } catch (Exception ex) {
      System.out.println("invalid color "+colorString+": "+ex.getMessage());
    }
    return c;
  }

  // printers
  public static void printArray(int[] a){
    if (a==null) {
      System.out.println("null");
      return;
    }
    for (int i=0; i<a.length; ++i) {
      System.out.print(" "+a[i]);
    }
    System.out.println();
  }

  public static void printArray(Object[] a){
    if (a==null) {
      System.out.println("null");
      return;
    }
    for (int i=0; i<a.length; ++i) {
      System.out.print(" "+a[i]);
    }
    System.out.println();
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
    d.setSize(200,200);
    d.pack();
    return d;
  }

  public static JPanel makeTestPanel(String text) {
    JPanel p = new JPanel();
    p.add(makeLabel(text));
    p.setMinimumSize(new Dimension(400,250));
    return p;
  }    
  
}
