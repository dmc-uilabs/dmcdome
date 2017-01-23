package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CConstant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 8.
 */
public class CellConfigDialog extends JDialog implements ActionListener {

    private ModelEditor modelEditor;
    private Container contentPane;
    private boolean isSubmitted = false;
    private CellConfig previewCellConfig;
    private BaseCell previewCell;

    private JRadioButton[] alignTypeRbs = new JRadioButton[3];
    private JRadioButton[] widthRbs = new JRadioButton[3];
    private JRadioButton[] nameRowRbs = new JRadioButton[2];
    private JRadioButton[] scriptRowRbs = new JRadioButton[2];
    private JRadioButton[] showTypeUnitRbs = new JRadioButton[2];

    public CellConfigDialog(Frame owner, ModelEditor modelEditor) {
        super(owner, true);
        setTitle("Configure Cell Display Option Dialog");
        this.modelEditor = modelEditor;
        this.previewCellConfig = CellConfig.createDefaultCellConfig();
        //this.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        this.setResizable(false);
        contentPane = this.getContentPane();

        BoxLayout mainLayout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
        contentPane.setLayout(mainLayout);


        JPanel previewPanel = createPreviewPanel();
        JPanel optionPanel = createOptionPanel();

        /* populate options in the option panel */
        if (modelEditor.getCellConfig() == null) {
            modelEditor.setCellConfig(CellConfig.createDefaultCellConfig());
        }
        initRadioButtons(modelEditor.getCellConfig());

        JPanel centerPanel = new JPanel();
        BoxLayout centerLayout = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        centerPanel.setLayout(centerLayout);
        centerPanel.add(optionPanel);
        centerPanel.add(previewPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmBt = UIUtil.createButton("Confirm Changes");
        confirmBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = true;
                dispose();
            }
        });
        JButton cancelBt = UIUtil.createButton("Cancel");
        cancelBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = false;
                dispose();
            }
        });
        buttonPanel.add(confirmBt);
        buttonPanel.add(cancelBt);

        contentPane.add(centerPanel);
        contentPane.add(buttonPanel);

        this.pack();

        int x = owner.getX() + (owner.getWidth() - this.getWidth()) / 2;
        int y = owner.getY() + (owner.getHeight() - this.getHeight()) / 2;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        this.setLocation(x, y);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = false;
                dispose();
            }
        }, "ESCAPE", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private JPanel createPreviewPanel() {
        JPanel previewPanel = new JPanel();
        previewPanel.setBorder(BorderFactory.createTitledBorder("Cell Preview"));
        //previewPanel.setMinimumSize(new Dimension(450, 210));
        previewPanel.setPreferredSize(new Dimension(600, 150));
        previewPanel.setAlignmentY(TOP_ALIGNMENT);
        previewCell = new PreviewParameterCell("relA", "sample width", CConstant.REAL_DATA_TYPE, CConstant.NO_UNIT_STR, "itf.param A + itf.param B", modelEditor.getComponentReference(), this);
        previewPanel.add(previewCell, BorderLayout.CENTER);
        return previewPanel;
    }

    public void updatePreviewPanel() {
        previewCell.updateCellLayout(previewCellConfig, false);
        previewCell.revalidate();
    }

    public BaseCell getPreviewCell() {
        return previewCell;
    }

    /** use this method to make cell config dialog visible */
    public void initRadioButtonsAndShowDialog() {
        CellConfig currentCellConfig = modelEditor.getCellConfig();
        this.previewCellConfig = new CellConfig(currentCellConfig.alignType, currentCellConfig.width, currentCellConfig.nameRow, currentCellConfig.scriptRow, currentCellConfig.showTypeUnit);
        initRadioButtons(previewCellConfig);
        updatePreviewPanel();
        this.setVisible(true);
    }

    private JPanel createOptionPanel() {
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        optionPanel.setAlignmentY(TOP_ALIGNMENT);
        optionPanel.setMinimumSize(new Dimension(500, 210));
        optionPanel.setPreferredSize(new Dimension(500, 210));
        optionPanel.setBorder(BorderFactory.createTitledBorder("Cell Display Configuration"));

        JLabel alignTypeLb = UIUtil.createItalicLabel("Cell Layout");
        JLabel widthLb = UIUtil.createItalicLabel("Cell Width");
        JLabel nameHeightLb = UIUtil.createItalicLabel("Parameter Name Display");
        JLabel scriptHeightLb = UIUtil.createItalicLabel("Script Display");
        JLabel showTypeUnitLb = UIUtil.createItalicLabel("Type and Unit Visibility");

        alignTypeLb.setPreferredSize(new Dimension(130, 12));
        widthLb.setPreferredSize(new Dimension(130, 12));
        nameHeightLb.setPreferredSize(new Dimension(130, 12));
        scriptHeightLb.setPreferredSize(new Dimension(130, 12));
        showTypeUnitLb.setPreferredSize(new Dimension(130, 12));

        alignTypeLb.setHorizontalAlignment(SwingConstants.RIGHT);
        widthLb.setHorizontalAlignment(SwingConstants.RIGHT);
        nameHeightLb.setHorizontalAlignment(SwingConstants.RIGHT);
        scriptHeightLb.setHorizontalAlignment(SwingConstants.RIGHT);
        showTypeUnitLb.setHorizontalAlignment(SwingConstants.RIGHT);

        alignTypeRbs [0] = new JRadioButton("script at bottom");
        alignTypeRbs [1] = new JRadioButton("script on left");
        alignTypeRbs [2] = new JRadioButton("a single row");
        ButtonGroup alignTypeGp = groupButtons(alignTypeRbs);
        setActionCommand("alignType", alignTypeRbs);

        widthRbs [0] = new JRadioButton("narrow");
        widthRbs [1] = new JRadioButton("medium");
        widthRbs [2] = new JRadioButton("wide");
        ButtonGroup widthGp = groupButtons(widthRbs);
        setActionCommand("width", widthRbs);

        nameRowRbs [0] = new JRadioButton("1 row");
        nameRowRbs [1] = new JRadioButton("2 rows");
        ButtonGroup nameRowGp = groupButtons(nameRowRbs);
        setActionCommand("nameRow", nameRowRbs);

        scriptRowRbs [0] = new JRadioButton("1 row");
        scriptRowRbs [1] = new JRadioButton("2 rows");
        ButtonGroup scriptRowGp = groupButtons(scriptRowRbs);
        setActionCommand("scriptRow", scriptRowRbs);

        showTypeUnitRbs [0] = new JRadioButton("always show");
        showTypeUnitRbs [1] = new JRadioButton("show as tooltip");
        ButtonGroup showTypeUnitGp = groupButtons(showTypeUnitRbs);
        setActionCommand("showTypeUnit", showTypeUnitRbs);

        JPanel[] rows = new JPanel[5];
        rows [0] = createRowPanel(alignTypeLb, alignTypeRbs);
        rows [1] = createRowPanel(widthLb, widthRbs);
        rows [2] = createRowPanel(nameHeightLb, nameRowRbs);
        rows [3] = createRowPanel(scriptHeightLb, scriptRowRbs);
        rows [4] = createRowPanel(showTypeUnitLb, showTypeUnitRbs);

        optionPanel.add(rows [0]);
        optionPanel.add(rows [1]);
        optionPanel.add(rows [2]);
        optionPanel.add(rows [3]);
        optionPanel.add(rows [4]);

        addActionListenerToRadioButtons();

        return optionPanel;
    }

    /** for example, when id is "width" and the action command of the 1st button is "width:1"*/
    private void setActionCommand(String id, JRadioButton[] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            JRadioButton button = buttons[i];
            button.setActionCommand(id + ":" + (i + 1));
        }
    }

    private ButtonGroup groupButtons(JRadioButton[] buttons) {
        ButtonGroup ret = new ButtonGroup();
        for (int i = 0; i < buttons.length; i++) {
            JRadioButton button = buttons[i];
            ret.add(button);
        }
        return ret;
    }

    private JPanel createRowPanel(JLabel titleLb, JRadioButton[] buttons) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT));
        row.add(titleLb);
        for (int i = 0; i < buttons.length; i++) {
            JRadioButton button = buttons[i];
            row.add(button);
        }
        return row;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public CellConfig getPreviewCellConfig() {
        return previewCellConfig;
    }

    public void initRadioButtons(CellConfig cellConfig) {
        alignTypeRbs [cellConfig.alignType - 1].setSelected(true);
        widthRbs [cellConfig.width - 1].setSelected(true);
        nameRowRbs [cellConfig.nameRow - 1].setSelected(true);
        scriptRowRbs [cellConfig.scriptRow - 1].setSelected(true);
        if (cellConfig.showTypeUnit) {
            showTypeUnitRbs [0].setSelected(true);
        } else {
            showTypeUnitRbs [1].setSelected(true);
        }

        if (cellConfig.alignType == 1) {
            setEnabledOfRadioButtons(true, nameRowRbs);
            setEnabledOfRadioButtons(true, scriptRowRbs);
            setEnabledOfRadioButtons(true, showTypeUnitRbs);
        } else if (cellConfig.alignType == 2) {
            setEnabledOfRadioButtons(false, nameRowRbs);
            setEnabledOfRadioButtons(false, scriptRowRbs);
            setEnabledOfRadioButtons(false, showTypeUnitRbs);
        } else if (cellConfig.alignType == 3) {
            setEnabledOfRadioButtons(false, nameRowRbs);
            setEnabledOfRadioButtons(false, scriptRowRbs);
            setEnabledOfRadioButtons(true, showTypeUnitRbs);
        }
    }

    private void setEnabledOfRadioButtons(boolean enabled, JRadioButton[] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            JRadioButton button = buttons[i];
            button.setEnabled(enabled);
        }
    }

    private void addActionListenerToRadioButtons() {
        for (int i = 0; i < 3; i++) {
            alignTypeRbs [i].addActionListener(this);
        }
        for (int i = 0; i < 3; i++) {
            widthRbs [i].addActionListener(this);
        }
        for (int i = 0; i < 2; i++) {
            nameRowRbs [i].addActionListener(this);
        }
        for (int i = 0; i < 2; i++) {
            scriptRowRbs [i].addActionListener(this);
        }
        for (int i = 0; i < 2; i++) {
            showTypeUnitRbs [i].addActionListener(this);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if ("alignType:1".equals(e.getActionCommand())) {
            previewCellConfig.alignType = 1;
            setEnabledOfRadioButtons(true, nameRowRbs);
            setEnabledOfRadioButtons(true, scriptRowRbs);
            setEnabledOfRadioButtons(true, showTypeUnitRbs);
        } else if ("alignType:2".equals(e.getActionCommand())) {
            previewCellConfig.alignType = 2;
            previewCellConfig.nameRow = 1;
            previewCellConfig.scriptRow = 2;
            previewCellConfig.showTypeUnit = true;
            nameRowRbs [0].setSelected(true);
            scriptRowRbs [1].setSelected(true);
            showTypeUnitRbs [0].setSelected(true);
            setEnabledOfRadioButtons(false, nameRowRbs);
            setEnabledOfRadioButtons(false, scriptRowRbs);
            setEnabledOfRadioButtons(false, showTypeUnitRbs);
        } else if ("alignType:3".equals(e.getActionCommand())) {
            previewCellConfig.alignType = 3;
            previewCellConfig.nameRow = 1;
            previewCellConfig.scriptRow = 1;
            nameRowRbs [0].setSelected(true);
            scriptRowRbs [0].setSelected(true);
            setEnabledOfRadioButtons(false, nameRowRbs);
            setEnabledOfRadioButtons(false, scriptRowRbs);
            setEnabledOfRadioButtons(true, showTypeUnitRbs);
        }

        if (e.getActionCommand().startsWith("width:")) {
            previewCellConfig.width = Integer.parseInt(e.getActionCommand().substring("width:".length()));
        } else if (e.getActionCommand().startsWith("nameRow:")) {
            previewCellConfig.nameRow = Integer.parseInt(e.getActionCommand().substring("nameRow:".length()));
        } else if (e.getActionCommand().startsWith("scriptRow:")) {
            previewCellConfig.scriptRow = Integer.parseInt(e.getActionCommand().substring("scriptRow:".length()));
        } else if (e.getActionCommand().startsWith("showTypeUnit:")) {
            if ("showTypeUnit:1".equals(e.getActionCommand())) {
                previewCellConfig.showTypeUnit = true;
            } else if ("showTypeUnit:2".equals(e.getActionCommand())) {
                previewCellConfig.showTypeUnit = false;
            }
        }
        updatePreviewPanel();
    }
}

