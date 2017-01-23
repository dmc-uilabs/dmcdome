package mit.cadlab.dome3.plugin.catalog.ui;

import org.exolab.ID.UUIDGenerator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 20.
 */
public class UIUnitTest {
    private static JFrame showPanel(final Container panel) {
        try {
            String laf = System.getProperty("swing.defaultlaf");
            if(laf == null) {
                laf = UIManager.getSystemLookAndFeelClassName();
            }
            UIManager.setLookAndFeel(laf);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        //JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("CModel Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane();
        //contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.add(panel, BorderLayout.CENTER);
        //contentPane.add(panel);
        frame.pack();
        //frame.setLocationByPlatform(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        //frame.setLocation(screen.width - frame.getWidth() / 2, screen.height - frame.getHeight() / 2);
        frame.setVisible(true);
        return frame;
    }

    private static void testEditorPane() {
//        Pattern p = Pattern.compile("\"(?:\\\\.|[^\"\\\\])*\""); //"<[^ ]*|[\\?]?>"
//        Matcher m = p.matcher("\"\\f\\b\\t123\"");
//        System.out.println("\"\\");
//        boolean b = m.matches();
//        System.out.println("matched? " + b);

        ColorizedDocument ed = new ColorizedDocument(new GroovyDelimiterList(null, null));
        JTextPane textPane = new JTextPane(ed);
        textPane.setPreferredSize(new Dimension(400, 500));
        JPanel panel = new JPanel();
        panel.add(textPane, BorderLayout.CENTER);
        showPanel(panel);
    }

    private static void testRegex() {
        for (int i = 0; i < 5; i++) {
            System.out.println("UUID " + UUIDGenerator.create());
        }


        //Pattern p = Pattern.compile("(?:\\b|[\\s/*-+^%!])[a-zA-Z_0-9]+\\.[a-zA-Z_0-9]*[\\s/*-+^%!]");
        //String nonCapturingQualifiedParamPattern = "(?:^|[\\s/*-+^%!])[a-zA-Z_0-9 ]+[\\.]*[a-zA-Z_0-9 ]*[\\.]*[a-zA-Z_0-9 ]*(?:$|[\\s/*-+^%!])";
        //String qualifiedParamPattern = "(?:^|[\\s/*-+^%!])([a-zA-Z_0-9 ]+)[\\.]*([a-zA-Z_0-9 ]*)[\\.]*([a-zA-Z_0-9 ]*)(?:$|[\\s/*-+^%!])";
        String nonCapturingQualifiedParamPattern = "(?:^|[/*-+^%!\\(\\) ])[a-zA-Z_0-9. ]+(?:$|[/*-+^%!\\(\\)])";
        String qualifiedParamPattern = "(?:^[\\s/*-+^%! ]*([a-zA-Z_0-9]+)[\\.]*([a-zA-Z_0-9 ]*)[\\.]*([a-zA-Z_0-9 ]*)[\\s/*-+^%!]$)";
        Pattern tokenizer = Pattern.compile(nonCapturingQualifiedParamPattern);
        Pattern parser = Pattern.compile(qualifiedParamPattern);
        //Pattern p = Pattern.compile("\\b[a-zA-Z_0-9]+\\b");
        Matcher m = tokenizer.matcher(" aaa.b bb..cc c + * ddd..eee * fff..ggg.hhh +");
        //Matcher m = p.matcher("ada.dada");

        Matcher pm;
        String token;

        m.find();
        token = m.group();
        System.out.println("token : " + token);
        pm = parser.matcher(token);
        System.out.println("matched: " + pm.matches());
        for (int i = 0; i < pm.groupCount(); i++) {
            System.out.println("group " + i + " = " + pm.group(i+1));
        }

        m.find();
        token = m.group();
        pm = parser.matcher(token);
        System.out.println("matched: " + pm.matches());
        for (int i = 0; i < pm.groupCount(); i++) {
            System.out.println("group " + i + " = " + pm.group(i+1));
        }

        m.find();
        token = m.group();
        pm = parser.matcher(token);
        System.out.println("matched: " + pm.matches());
        for (int i = 0; i < pm.groupCount(); i++) {
            System.out.println("group " + i + " = " + pm.group(i+1));
        }
    }

    private static void testRelationDialog() {
        UIUtil.initUnit();
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 400));
        JFrame frame = showPanel(panel);
        (new RelationDialog(frame, null)).setVisible(true);
    }

    private static void testCellConfigDialog() {
        String testedRelAlias = "1relA";
        Pattern p = Pattern.compile("^(?:[a-zA-Z])+(?:[a-zA-Z_0-9])*$");
        Matcher m = p.matcher(testedRelAlias);
        System.out.println("valid? : " + m.find());

        UIUtil.initUnit();
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 400));
        JFrame frame = showPanel(panel);
        final ComponentReference compRef = new ComponentReference();
        final CellConfigDialog configDialog = new CellConfigDialog(frame, null);

//        compRef.addDynamicComponent("update preview cell", new DynamicComponent() {
//            public void update() {
//                configDialog.updatePreviewPanel();
//            }
//        });
//
//        java.util.Timer uiTimer = new java.util.Timer(true);
//        compRef.setUITImer(uiTimer);
//        uiTimer.schedule(new UITimerTask(compRef), 1000, 2000);

        configDialog.setVisible(true);

//        JTextArea nameLabel = configDialog.getPreviewCell().getNameLabel();
//        int actualLineCount = UIUtil.getLineCount(nameLabel);
//        System.out.println("nameLabel.getLineCount() 1 = " + nameLabel.getLineCount() + " / " + actualLineCount);
//        nameLabel.setPreferredSize(new Dimension(80, 16 * actualLineCount));
//        nameLabel.invalidate();
//        configDialog.getPreviewCell().validate();
//        configDialog.setVisible(true);

        Graphics g = panel.getGraphics();
        FontMetrics fm = g.getFontMetrics(UIUtil.PARAM_NAME_FONT);
        System.out.println("fm.getDescent()= " + fm.getDescent());
        System.out.println("fm.getMaxDescent()= " + fm.getMaxDescent());
        System.out.println("fm.getAscent()= " + fm.getAscent());
        System.out.println("fm.getHeight()= " + fm.getHeight());
        System.out.println("fm.getLeading()= " + fm.getLeading());

        System.out.println("submit = " + configDialog.isSubmitted() + ", cell config = " + configDialog.getPreviewCellConfig());
    }

    private static void testInterfaceDialog() {
        UIUtil.initUnit();
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 400));
        JFrame frame = showPanel(panel);
        (new InterfaceDialog(frame, null, false)).setVisible(true);
    }

    private static void testUnitChooserDialog() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 400));
        JFrame frame = showPanel(panel);
        (new UnitChooserDialog(frame, null)).setVisible(true);
    }

    private static void testModelEditor() {
        ModelEditor modelEditor = new ModelEditor();
        showPanel(modelEditor);
    }

    private static void testNavigationPanel() {
        ModelNavigationPanel navPanel = new ModelNavigationPanel(null);
        showPanel(navPanel);
    }

    private static JLabel createLabel(String text, int w, int h, Color c) {
        JLabel ret = new JLabel(text);
        ret.setPreferredSize(new Dimension(w, h));
        ret.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        ret.setBackground(c);
        ret.setBorder(new LineBorder(Color.GREEN, 1));
        return ret;
    }

    private static void testScroll() {
        final JPanel container = new JPanel();
        container.setBackground(Color.DARK_GRAY);
        final JPanel labelPanel = new JPanel();
        labelPanel.setMinimumSize(new Dimension(100, 200));
        labelPanel.setPreferredSize(new Dimension(300, 300));
        labelPanel.setMaximumSize(new Dimension(1000, 400));
        labelPanel.setBackground(Color.YELLOW);
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setMinimumSize(new Dimension(100, 50));
        buttonPanel.setPreferredSize(new Dimension(300, 100));
        buttonPanel.setMaximumSize(new Dimension(1000, 200));
        buttonPanel.setBackground(Color.BLUE);
        final SpringLayout spLayout = new SpringLayout();
        container.setLayout(spLayout);
        container.add(labelPanel);
        container.add(buttonPanel);


        //spLayout.putConstraint(SpringLayout.NORTH, buttonPanel, 10, SpringLayout.SOUTH, labelPanel);
        spLayout.putConstraint(SpringLayout.NORTH, buttonPanel, 5, SpringLayout.SOUTH, labelPanel);
        spLayout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, container);
        //spLayout.putConstraint(SpringLayout.EAST, container, 5, SpringLayout.EAST, buttonPanel);
        spLayout.putConstraint(SpringLayout.EAST, container, 5, SpringLayout.EAST, buttonPanel);
        spLayout.putConstraint(SpringLayout.SOUTH, container, 5, SpringLayout.SOUTH, buttonPanel);

        spLayout.putConstraint(SpringLayout.NORTH, labelPanel, 5, SpringLayout.NORTH, container);
        spLayout.putConstraint(SpringLayout.WEST, labelPanel, 5, SpringLayout.WEST, container);
        spLayout.putConstraint(SpringLayout.EAST, container, 5, SpringLayout.EAST, labelPanel);


//        SpringLayout.Constraints containerCons = spLayout.getConstraints(container);
//        containerCons.setWidth(Spring.constant(300, 400, 500));
//        containerCons.setHeight(Spring.constant(300, 400, 500));
//
//        SpringLayout.Constraints labelPanelCons = spLayout.getConstraints(labelPanel);
//        labelPanelCons.setX(Spring.constant(5));
//        labelPanelCons.setY(Spring.constant(5));
//        labelPanelCons.setWidth(Spring.constant(100, 200, 300));
//        labelPanelCons.setHeight(Spring.constant(300));
        //labelPanelCons.setWidth(Spring.constant(300));
        //print(containerCons.getConstraint(SpringLayout.EAST));
        //labelPanelCons.setConstraint(SpringLayout.EAST, containerCons.getConstraint(SpringLayout.EAST));


//        SpringLayout.Constraints buttonPanelCons = spLayout.getConstraints(buttonPanel);
//        buttonPanelCons.setX(Spring.constant(5));
//        buttonPanelCons.setY(Spring.sum(Spring.constant(5), labelPanelCons.getConstraint(SpringLayout.SOUTH)));
//        //buttonPanelCons.setConstraint(SpringLayout.EAST, containerCons.getConstraint(SpringLayout.EAST));
//        buttonPanelCons.setHeight(Spring.constant(50));


        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        //JLabel label = new JLabel("<html>how are you<br></html>");

        JButton add = new JButton("add");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                labelPanel.add(createLabel("A", 300, 40, Color.RED));
                labelPanel.revalidate();
            }
        });

        JButton remove = new JButton("remove");
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (labelPanel.getComponentCount() > 0) {
                    labelPanel.remove(0);
                    labelPanel.revalidate();
                    labelPanel.repaint();
                } else {
                    print("no component to remove");
                }
            }
        });
        JButton layout = new JButton("layout");
        layout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                container.revalidate();
                print(spLayout.getConstraints(labelPanel).getWidth().getValue() + "");
                print(spLayout.getConstraints(labelPanel).getHeight().getValue() + "");
                print(spLayout.getConstraints(labelPanel).getX());
                print(spLayout.getConstraints(labelPanel).getY());
                print(spLayout.getConstraints(labelPanel).getConstraint(SpringLayout.EAST));
                print(spLayout.getConstraints(labelPanel).getConstraint(SpringLayout.WEST));
                print(spLayout.getConstraints(labelPanel).getConstraint(SpringLayout.SOUTH));
                print(spLayout.getConstraints(labelPanel).getConstraint(SpringLayout.NORTH));
            }
        });
        buttonPanel.add(add);
        buttonPanel.add(remove);
        buttonPanel.add(layout);


        showPanel(container);
//        label.setBackground(Color.CYAN);
//        label.setPreferredSize(new Dimension(100, 100));
//        print("1:" + panel.getPreferredSize());
//        panel.add(label);
//        print("2:" + panel.getPreferredSize());
//        label.setPreferredSize(new Dimension(200, 200));
//        panel.revalidate();
//        print("3:" + panel.getPreferredSize());
//        label.setPreferredSize(new Dimension(300, 300));
//        print("3.5:" + panel.getPreferredSize());
//        panel.revalidate();
//        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPane.setBorder(null);
//        scrollPane.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
//        scrollPane.setPreferredSize(new Dimension(UIUtil.SCRIPT_EDITOR_WIDTH, UIUtil.SCRIPT_EDITOR_HEIGHT));
//        scrollPane.setMinimumSize(scrollPane.getPreferredSize());
//
//        showPanel(scrollPane);
//        print("4:" + panel.getPreferredSize());
//        label.setPreferredSize(new Dimension(400, 400));
//        //label.setMinimumSize(new Dimension(400, 400));
//        panel.revalidate();
//        print("4.5:" + panel.getPreferredSize());
//        print("5:" + scrollPane.getPreferredSize());

    }

    private static void print(Object obj) {
        System.out.println(obj);
    }

    private static void testRelationEditorPanel() {
        ImplementationEditor implEditor = new ImplementationEditor();
        ComponentReference compRef = new ComponentReference(implEditor);
        RelationEditor editorPanel = new RelationEditor(compRef);

        Frame frame = showPanel(editorPanel);

        RelationBar bar = editorPanel.addRelation("RelationA");

        bar.addRelationInputCell("My Width", "Real", "mm", "");
        bar.addRelationInputCell("My Height", "Integer", "cm", "");
        bar.addRelationInputCell("My Name", "Real", "mm", "");
        bar.addRelationInputCell("My Tiger", "Real", "mm", "");

        bar.addRelationOutputCell("Weight", "Real", "kg");
        bar.addRelationOutputCell("Volumn", "Integer", "m^3");
        bar.addRelationOutputCell("Material Cost", "Real", "dollar");

        bar = editorPanel.addRelation("RelationB");
        bar.addRelationInputCell("My Name", "Real", "mm", "");
        bar.addRelationInputCell("My Tiger", "Real", "mm", "");

        bar.addRelationOutputCell("Weight", "Real", "kg");
        bar.addRelationOutputCell("Volumn", "Integer", "m^3");

        bar = editorPanel.addRelation("RelationC");
        bar.addRelationInputCell("My Name", "Real", "mm", "");
        bar.addRelationInputCell("My Tiger", "Real", "mm", "");

        bar.addRelationOutputCell("Weight", "Real", "kg");
        bar.addRelationOutputCell("Volumn", "Integer", "m^3");
        bar.addRelationOutputCell("Weight", "Real", "kg");
        bar.addRelationOutputCell("Volumn", "Integer", "m^3");

        editorPanel.validate();
    }

    private static void testRelationEditorPanel2() {
        ImplementationEditor implEditor = new ImplementationEditor();
        ComponentReference compRef = new ComponentReference(implEditor);
        RelationEditor editorPanel = new RelationEditor(compRef);

        Frame frame = showPanel(editorPanel);

        RelationBar bar = editorPanel.addRelation("Rel 1");

        bar.addRelationInputCell("A", "Real", "No_Unit", "");
        bar.addRelationInputCell("B", "Real", "No_Unit", "");
        bar.addRelationOutputCell("C", "Real", "No_Unit");

        bar = editorPanel.addRelation("Rel 2");
        bar.addRelationInputCell("D", "Real", "No_Unit", "");
        bar.addRelationInputCell("E", "Real", "No_Unit", "");

        bar.addRelationOutputCell("E", "Real", "No_Unit");

        editorPanel.validate();
    }

    public static void testCModelIntegration() {
        ImplementationEditor implEditor = new ImplementationEditor();
        Frame frame = showPanel(implEditor);

        //implEditor.load(model, "My Interface", "My Impl");


        implEditor.validate();
        //Frame frame = showPanel(implEditor.getRelationEditor());

        //implEditor.getRelationEditor().validate();


        //frame.pack();
    }

    public static void main(String[] args) {
//        String plainText = UIUtil.getPlainText("<html>b dkd <font blue=\"3\"> sample </font> </html>");
//        System.out.println("plainText = "  + plainText);

        //Pattern p = Pattern.compile("\\[(?:\\d+(?:.\\d+)\\s+)+\\]");
//        Pattern p = Pattern.compile("\\[(?:\\s*\\d+(?:.\\d+)?\\s*)+\\]");
//        Matcher m = p.matcher("[ 4.4   4.5 ]");
//        System.out.println("h " + m.find());
//        if (true) return;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                testCellConfigDialog();
                //testEditorPane();
                //testRelationEditorPanel2();
                //testRelationEditorPanel();
                //testCModelIntegration();
                //testScroll();
                //testNavigationPanel();
                //testModelEditor();


//                String cuePattern = TokenMatrix.createCodeTokenSignature("rel11.cc=rel.a+rel.ddl+1*rel.a");
//                CodeToken[] tokens = TokenMatrix.createCodeTokenArray("rel11.cc=rel.a+rel.ddl+1*rel.a");
//                System.out.println(cuePattern);
//                System.out.println(Arrays.asList(tokens));
//                Object[] objs = UIUtil.getRow(3, "\nbbbb\nccc\n");
//                //Object[] objs = UIUtil.getRow(3, "\r\nbbbb\r\nccc\r\n");
//                System.out.println(objs[0] + " , " + objs[1]);


                //testRelationDialog();
                //testInterfaceDialog();
                //testUnitChooserDialog();

//                UIUtil.initUnit();
//                String NO_UNIT_STR = "No_Unit";
//                UnitChooser.showDialog(null, new Unit(NO_UNIT_STR));
            }
        });
    }
}
