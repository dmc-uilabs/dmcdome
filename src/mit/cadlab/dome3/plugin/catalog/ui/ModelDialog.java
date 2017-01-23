package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * User: Sangmok Han
 * Date: 2006. 2. 15.
 */
public class ModelDialog extends JDialog {
    private ComponentReference compRef;

    private static int DIALOG_WIDTH = 610;
    private static int DIALOG_HEIGHT = 320;
    private static int FORM_HEIGHT = 400;

    private static int ROW_HEIGHT = 27;
    private static int LEFT_COLUMN_WIDTH = 65;
    private static int RIGHT_COLUMN_WIDTH = 500 - 6;
    private static int GAP_BETWEEN_COLUMNS = 10;
    private static int LEFT_MARGIN = 10;
    private static int GAP_BETWEEN_COMPS = 5;

    private JLabel modelNameLb;
    private JTextField modelNameFd;

    private JLabel modelDescLb;
    private JTextArea modelDescTa;

    private JButton okayBt;
    private JButton cancelBt;

    private Container contentPane;

    private boolean isSubmitted;

    /** this constructor should be used */
    public ModelDialog(ComponentReference compRef, boolean isForEditing) {
        this(ModelEditorFrame.getFrame(), compRef, isForEditing);
    }

    /** change label text in accordance with isAddingRelation
     * if it is true, label will be set for adding
     * if it is false, label will be set for editing */
    public void updateLabelsForEditing(boolean isEditing) {
        if (! isEditing) {
            okayBt.setText("Create a Model");
        } else {
            okayBt.setText("Confirm Changes");
        }
    }

    /** constructor for testing */
    public ModelDialog(Frame owner, ComponentReference compRef, boolean isForEditing) {
        super(owner, true);

        if (isForEditing) {
            setTitle("Modify the current model");
        } else {
            setTitle("Create a new model");
        }

        this.compRef = compRef;
        this.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        this.setResizable(false);
        contentPane = this.getContentPane();

        contentPane.add(makeCenterPanel(), BorderLayout.CENTER);;

        int x = owner.getX() + (owner.getWidth() - this.getWidth()) / 2;
        int y = owner.getY() + (owner.getHeight() - this.getHeight()) / 2;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        this.setLocation(x, y);

        updateLabelsForEditing(isForEditing);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = false;
                dispose();
            }
        }, "ESCAPE", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = true;
                dispose();
            }
        }, "SUBMIT", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private JPanel makeCenterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        modelNameLb = UIUtil.createLabel("Model Name");
        modelNameFd = UIUtil.createTextField("", 61);
        modelNameFd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = true;
                dispose();
            }
        });

        modelDescLb = UIUtil.createLabel("Description");
        modelDescTa = UIUtil.createTextArea("", 12, 61);
        JScrollPane modelDescTaScrollPane = new JScrollPane(modelDescTa, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //modelDescTaScrollPane.setPreferredSize(new Dimension());

        okayBt = UIUtil.createButton("Confirm Changes");
        okayBt.setPreferredSize(new Dimension(120, okayBt.getPreferredSize().height));
        cancelBt = UIUtil.createButton("Cancel");

        okayBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = true;
                dispose();
            }
        });

        cancelBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = false;
                dispose();
            }
        });

        UIUtil.addComponentsToPanel(new Component[] {
                modelNameLb, modelNameFd, modelDescLb, modelDescTaScrollPane, okayBt, cancelBt
            }, panel);


        UIUtil.setTwoColumnConstraints(modelNameLb, modelNameFd, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 0 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(modelDescLb, modelDescTaScrollPane, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 1 + 5, GAP_BETWEEN_COLUMNS, "right", "left");

        UIUtil.setFlowConstraints(new Component[] { okayBt, cancelBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 9 + 10, GAP_BETWEEN_COMPS,  "right");

        panel.setPreferredSize(new Dimension(DIALOG_WIDTH, FORM_HEIGHT));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        panel.setMinimumSize(panel.getPreferredSize());
        panel.validate();

        modelNameFd.requestFocusInWindow();

        return panel;
    }

    public void setModelName(String modelName) {
        modelNameFd.setText(modelName);
    }

    public String getModelName() {
        return modelNameFd.getText();
    }

    public void setModelDescription(String modelDesc) {
        System.out.println("setting model desc: " + modelDesc);
        modelDescTa.setText(modelDesc);
    }

    public String getModelDescription() {
        return modelDescTa.getText();
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }


    public ComponentReference getComponentReference() {
        return compRef;
    }
}
