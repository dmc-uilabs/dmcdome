package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CInterfaceInputParameter;
import mit.cadlab.dome3.plugin.catalog.core.CParameter;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.*;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * User: Sangmok Han
 * Date: Sep 19, 2006
 */
abstract class ValueEditorPane extends JPanel {
    protected Object value;
    protected ComponentReference compRef;
    protected String qualifiedParamName;

    public ValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        this.compRef = compRef; 
        this.qualifiedParamName = qualifiedParamName;
        this.setLayout(new GridLayout(1, 1));
    }

    public ComponentReference getComponentReference() {
        return compRef;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public Component getComponent() {
        if (this.getComponentCount() == 0) {
            return null;
        }
        return this.getComponent(0);
    }

    public String getQualifiedParamName() {
        return qualifiedParamName;
    }

    public String getRelAlias() {
        int dotIdx = qualifiedParamName.indexOf(".");
        if (dotIdx == -1) {
            throw new RuntimeException("invalid qualified name: " + qualifiedParamName);
        }
        return qualifiedParamName.substring(0, dotIdx);
    }

    public String getLocalParamName() {
        int dotIdx = qualifiedParamName.indexOf(".");
        if (dotIdx == -1) {
            throw new RuntimeException("invalid qualified name: " + qualifiedParamName);
        }
        return qualifiedParamName.substring(dotIdx + 1);
    }

    public void setComponent(Component comp) {
        this.add(comp, BorderLayout.CENTER);
    }

    abstract public void updateEditorValue();

    public synchronized void setBackground(Color color) {
        super.setBackground(color);
        if (getComponent() != null) {
            getComponent().setBackground(color);
            //getComponent().repaint();
        }
    }

    public void setBorder(Border border) {
        super.setBorder(border);
        if (getComponent() != null) {
            ((JComponent) getComponent()).setBorder(null);
        }
    }

    /**
     * 1. text modified
     * 2. try to parse the given text. when successful, accept it as editor value. keep editing
     * 3. commit editor value to data object
     */
    public void commitEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        evalContext.getDataObject(getQualifiedParamName()).setValue(getValue());
    }
}

/** Dummy value editor pane for PreviewParameterCell */
class ValueEditorPaneForPreviewParameterCell extends ValueEditorPane {
    public ValueEditorPaneForPreviewParameterCell() {
        super(null, null);
        JTextField textField = new JTextField("10.0");
        setComponent(textField);
        setValue(new Double(10.0));
    }
    public void updateEditorValue() {
        // do nothing
    }
}

class RealValueEditorPane extends ValueEditorPane {
    public RealValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        super(qualifiedParamName, compRef);
        JTextField textField = new JTextField();

        if (compRef.getCurrentCNamingService().getParameter(qualifiedParamName) instanceof CInterfaceInputParameter) {
            textField.setEditable(true);
            textField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        getComponentReference().getImplementationEditor().requestFocusInWindow();
                        e.consume();
                    }
                }
            });
            textField.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent event) {
                    acceptEditorValue();
                }
            });
            textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resetEditorValue");
            textField.getActionMap().put("resetEditorValue", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    updateEditorValue();
                    acceptEditorValue();
                    getComponentReference().getImplementationEditor().requestFocusInWindow();
                }
            });
        } else {
            textField.setEditable(false);
        }

        setComponent(textField);
        setDoubleValue(0);
    }

    public double getDoubleValue() {
        return ((Double) getValue()).doubleValue();
    }

    public JTextField getTextField() {
        return (JTextField) getComponent();
    }

    public void setDoubleValue(double value) {
        setValue(new Double(value));
    }

    /** called to update the value displayed in Editor based on the current CDataObject value */
    public synchronized void updateEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CDataObject dataObj = evalContext.getDataObject(getQualifiedParamName());
        setValue(dataObj.getValue());
        getTextField().setText(EvaluationMode.toValueDisplayString(dataObj));
    }

    public synchronized void acceptEditorValue() {
        try {
            value = new Double(EvaluationMode.parseIntOrDouble(getTextField().getText()).doubleValue());
            getTextField().setText(EvaluationMode.formatDouble(((Double) value).doubleValue()));
        } catch (ParseException e) {
            getTextField().requestFocusInWindow();
            this.setBorder(UIUtil.VALUE_EDITOR_ERROR_BORDER);
            return;
        }

        this.setBorder(UIUtil.VALUE_EDITOR_NORMAL_BORDER);

        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        Object value = evalContext.getDataObject(getQualifiedParamName()).getValue();
        if (! value.equals(getValue())) {
            EvaluationMode.addModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.stallParameter(getQualifiedParamName(), compRef);

        } else {
            EvaluationMode.removeModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.unstallParameter(getQualifiedParamName(), compRef);
        }

//        if (evalContext.isAfterFirstEvaluation()) {
            EvaluationMode.updateStatusOfParameters(compRef);
//        }
        EvaluationMode.updateStatusOfAllValueEditorPanes(compRef);
    }
}

class IntegerValueEditorPane extends ValueEditorPane {
    public IntegerValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        super(qualifiedParamName, compRef);
        JTextField textField = new JTextField();
        if (compRef.getCurrentCNamingService().getParameter(qualifiedParamName) instanceof CInterfaceInputParameter) {
            textField.setEditable(true);
//            textField.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent event) {
//                    acceptEditorValue();
//                    getComponentReference().getImplementationEditor().requestFocusInWindow();
//                }
//            });
            textField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        getComponentReference().getImplementationEditor().requestFocusInWindow();
                        e.consume();
                    }
                }
            });
            textField.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent event) {
                    acceptEditorValue();
                }
            });
            textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resetEditorValue");
            textField.getActionMap().put("resetEditorValue", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    updateEditorValue();
                    acceptEditorValue();
                    getComponentReference().getImplementationEditor().requestFocusInWindow();
                }
            });
        } else {
            textField.setEditable(false);
        }
        setComponent(textField);
        setIntegerValue(0);
    }

    public int getIntegerValue() {
        return ((Integer) getValue()).intValue();
    }

    public JTextField getTextField() {
        return (JTextField) getComponent();
    }

    public void setIntegerValue(int value) {
        setValue(new Integer(value));
    }

    /** called to update the value displayed in Editor based on the current CDataObject value */
    public void updateEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CDataObject dataObj = evalContext.getDataObject(getQualifiedParamName());
        setValue(dataObj.getValue());
        getTextField().setText(EvaluationMode.toValueDisplayString(dataObj));
    }

    public void acceptEditorValue() {
        try {
            value = new Integer(EvaluationMode.parseIntOrDouble(getTextField().getText()).intValue());
            getTextField().setText(EvaluationMode.formatDouble(((Integer) value).intValue()));
        } catch (ParseException e) {
            getTextField().requestFocusInWindow();
            this.setBorder(UIUtil.VALUE_EDITOR_ERROR_BORDER);
            return;
        }

//        try {
//            value = new Integer(getTextField().getText());
//        } catch (NumberFormatException e) {
//            getTextField().requestFocusInWindow();
//            this.setBorder(UIUtil.VALUE_EDITOR_ERROR_BORDER);
//            return;
//        }
        this.setBorder(UIUtil.VALUE_EDITOR_NORMAL_BORDER);

        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        Object originValue = evalContext.getDataObject(getQualifiedParamName()).getValue();
        if (! originValue.equals(getValue())) {
            EvaluationMode.addModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.stallParameter(getQualifiedParamName(), compRef);
        } else {
            EvaluationMode.removeModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.unstallParameter(getQualifiedParamName(), compRef);
        }

//        if (evalContext.isAfterFirstEvaluation()) {
            EvaluationMode.updateStatusOfParameters(compRef);
//        }
        EvaluationMode.updateStatusOfAllValueEditorPanes(compRef);
    }
}

class StringValueEditorPane extends ValueEditorPane {
    public StringValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        super(qualifiedParamName, compRef);
        JTextField textField = new JTextField();
        if (compRef.getCurrentCNamingService().getParameter(qualifiedParamName) instanceof CInterfaceInputParameter) {
//            textField.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent event) {
//                    acceptEditorValue();
//                    getComponentReference().getImplementationEditor().requestFocusInWindow();
//                }
//            });
            textField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        getComponentReference().getImplementationEditor().requestFocusInWindow();
                        e.consume();
                    }
                }
            });
            textField.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent event) {
                    acceptEditorValue();
                }
            });
        } else {
            textField.setEditable(false);
        }
        setComponent(textField);
        setStringValue("");
    }

    public String getStringValue() {
        return (String) getValue();
    }

    public JTextField getTextField() {
        return (JTextField) getComponent();
    }

    public void setStringValue(String value) {
        setValue(value);
    }

    /** called to update the value displayed in Editor based on the current CDataObject value
     * see dataobject.value as source */
    public void updateEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CDataObject dataObj = evalContext.getDataObject(getQualifiedParamName());
        setValue(dataObj.getValue());
        getTextField().setText(EvaluationMode.toValueDisplayString(dataObj));
    }

    /** called to confirm any change made to Editor
     * see textfield.value as source */
    public void acceptEditorValue() {
        value = getTextField().getText();
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        Object value = evalContext.getDataObject(getQualifiedParamName()).getValue();
        if (! value.equals(getValue())) {
            EvaluationMode.addModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.stallParameter(getQualifiedParamName(), compRef);

        } else {
            EvaluationMode.removeModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.unstallParameter(getQualifiedParamName(), compRef);
        }

//        if (evalContext.isAfterFirstEvaluation()) {
            EvaluationMode.updateStatusOfParameters(compRef);
//        }
        EvaluationMode.updateStatusOfAllValueEditorPanes(compRef);
    }
}

class FileValueEditorPane extends ValueEditorPane {
    public FileValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        super(qualifiedParamName, compRef);
        JTextArea textArea = new JTextArea();
        if (compRef.getCurrentCNamingService().getParameter(qualifiedParamName) instanceof CInterfaceInputParameter) {
            textArea.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent event) {
                    acceptEditorValue();
                }
            });
        } else {
            textArea.setEditable(false);
        }
        setComponent(textArea);
        setFileValue("".getBytes());
    }

    public byte[] getFileValue() {
        return (byte[]) getValue();
    }

    public JTextArea getTextArea() {
        return (JTextArea) getComponent();
    }

    public void setFileValue(byte[] value) {
        setValue(value);
    }

    /** called to update the value displayed in Editor based on the current CDataObject value
     * see dataobject.value as source */
    public void updateEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CDataObject dataObj = evalContext.getDataObject(getQualifiedParamName());
        setValue(dataObj.getValue());
        getTextArea().setText(EvaluationMode.toValueDisplayString(dataObj));
    }

    /** see textarea.value as source */
    public void acceptEditorValue() {
        value = getTextArea().getText();
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        Object value = evalContext.getDataObject(getQualifiedParamName()).getValue();
        if (! value.equals(getValue())) {
            EvaluationMode.addModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.stallParameter(getQualifiedParamName(), compRef);
        } else {
            EvaluationMode.removeModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.unstallParameter(getQualifiedParamName(), compRef);
        }

//        if (evalContext.isAfterFirstEvaluation()) {
            EvaluationMode.updateStatusOfParameters(compRef);
//        }
        EvaluationMode.updateStatusOfAllValueEditorPanes(compRef);
    }
}

class BooleanValueEditorPane extends ValueEditorPane {
    public BooleanValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        super(qualifiedParamName, compRef);
        JCheckBox checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(JCheckBox.CENTER);
        if (compRef.getCurrentCNamingService().getParameter(qualifiedParamName) instanceof CInterfaceInputParameter) {
            checkBox.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent event) {
                    acceptEditorValue();
                }
            });
        } else {
            // checkBox.setEditable(false); // todo: make it not-editable
        }
        setComponent(checkBox);
        setBooleanValue(false);
    }

    public boolean getBooleanValue() {
        return ((Boolean) getValue()).booleanValue();
    }

    public JCheckBox getCheckBox() {
        return (JCheckBox) getComponent();
    }

    public void setBooleanValue(boolean value) {
        setValue(new Boolean(value));
    }

    /** called to update the value displayed in Editor based on the current CDataObject value */
    public void updateEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CDataObject dataObj = evalContext.getDataObject(getQualifiedParamName());
        setValue(dataObj.getValue());
        getCheckBox().setSelected(((Boolean) dataObj.getValue()).booleanValue());
    }

    public void acceptEditorValue() {
        value = new Boolean(getCheckBox().isSelected());
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        Object value = evalContext.getDataObject(getQualifiedParamName()).getValue();
        if (! value.equals(getValue())) {
            EvaluationMode.addModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.stallParameter(getQualifiedParamName(), compRef);
        } else {
            EvaluationMode.removeModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.unstallParameter(getQualifiedParamName(), compRef);
        }

//        if (evalContext.isAfterFirstEvaluation()) {
            EvaluationMode.updateStatusOfParameters(compRef);
//        }
        EvaluationMode.updateStatusOfAllValueEditorPanes(compRef);
    }
}

class ComboBoxWithFixedArrowButtonWidthUI extends BasicComboBoxUI {
    int buttonWidth;
    ComboBoxWithFixedArrowButtonWidthUI(int buttonWidth) {
        super();
        this.buttonWidth = buttonWidth;
    }

    protected LayoutManager createLayoutManager() {
        return new ComboBoxWithFixedArrowButtonWidthLayoutManager();
    }

    class ComboBoxWithFixedArrowButtonWidthLayoutManager implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {}

        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            JComboBox cb = (JComboBox)parent;
            return parent.getPreferredSize();
        }

        public Dimension minimumLayoutSize(Container parent) {
            JComboBox cb = (JComboBox)parent;
            return parent.getMinimumSize();
        }

        public void layoutContainer(Container parent) {
            JComboBox cb = (JComboBox)parent;
            int width = cb.getWidth();
            int height = cb.getHeight();

            Insets insets = getInsets();
            int buttonSize = height - (insets.top + insets.bottom);
            Rectangle cvb;

            if ( arrowButton != null ) {
                if(cb.getComponentOrientation().isLeftToRight()) {
                    arrowButton.setBounds( width - (insets.right + buttonWidth), insets.top, buttonWidth, buttonSize);
                }
                else {
                    arrowButton.setBounds( insets.left, insets.top, buttonWidth, buttonSize);
                }
            }
            if ( editor != null ) {
                cvb = rectangleForCurrentValue();
                editor.setBounds(cvb);
            }
        }
    }
}



class EnumValueEditorPane extends ValueEditorPane {
    public EnumValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        super(qualifiedParamName, compRef);
        JComboBox comboBox = new JComboBox();
        CParameter param = compRef.getCurrentCNamingService().getParameter(qualifiedParamName);
        //comboBox
        ComboBoxUI comboBoxUI = new ComboBoxWithFixedArrowButtonWidthUI(UIUtil.CG_NAME_ROW_HEIGHT);
        comboBox.setUI(comboBoxUI);


        java.util.List enumList = DataObjectUtil.createEnumList(param.getDefaultValue());
        for (int i = 0; i < enumList.size(); i++) {
            Object[] enumItem = (Object[]) enumList.get(i);
            String name = (String) enumItem [0];
            //Object value = enumItem [1];
            Boolean selected = (Boolean) enumItem [2];

            comboBox.addItem(name);
            if (selected.booleanValue()) {
                comboBox.setSelectedIndex(i);
            }
        }

        if (compRef.getCurrentCNamingService().getParameter(qualifiedParamName) instanceof CInterfaceInputParameter) {
            comboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    acceptEditorValue();
                }
            });
            comboBox.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        getComponentReference().getImplementationEditor().requestFocusInWindow();
                        e.consume();
                    }
                }
            });

//
//                    {
//                public void actionPerformed(ActionEvent event) {
//                    System.out.println("any action performed????");
//
//                }
//            });
//            comboBox.addFocusListener(new FocusAdapter() {
//                public void focusLost(FocusEvent event) {
//                    System.out.println("focus lost????");
//                    acceptEditorValue();
//                }
//            });
            comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resetEditorValue");
            comboBox.getActionMap().put("resetEditorValue", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    updateEditorValue();
                    acceptEditorValue();
                    getComponentReference().getImplementationEditor().requestFocusInWindow();
                }
            });
        } else {
            comboBox.setEditable(false);
        }
        setComponent(comboBox);
        setEnumList(enumList);
    }

    public List getEnumList() {
        return (List) getValue();
    }

    public JComboBox getComboBox() {
        return (JComboBox) getComponent();
    }

    public void setEnumList(List value) {
        setValue(value);
    }

    public int getSelected() {
        return DataObjectUtil.getSelectedIndex(getEnumList());
    }

    public void setSelected(int enumIdx) {
        DataObjectUtil.setSelectedIndex(enumIdx, getEnumList());
    }

    public void setSelected(String enumName) {
        DataObjectUtil.setSelectedIndex(enumName, getEnumList());
    }

    /** called to update the value displayed in Editor based on the current CDataObject value
     * see the index of dataobject.value as source. copy the index to this.value and combobox */
    public void updateEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CEnumeration dataObj = (CEnumeration) evalContext.getDataObject(getQualifiedParamName());

        int selectedIdx = dataObj.getSelectedIndex();
        // note. setValue(dataObj.getValue()); will not work
        this.setSelected(selectedIdx);

        if (selectedIdx != -1) {
            getComboBox().setSelectedIndex(selectedIdx);
        } else {
            getComboBox().setSelectedIndex(0);
        }
    }

    /** see combobox index as source. copy the index to this.value and dataobject.value */
    public void acceptEditorValue() {
        int selectedIdx = getComboBox().getSelectedIndex();
        this.setSelected(selectedIdx);

        //System.out.println("selectedIdx :" + selectedIdx + " / " + (List) getValue());

        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        int originSelectedIdx = ((CEnumeration) evalContext.getDataObject(getQualifiedParamName())).getSelectedIndex();
        //System.out.println("originSelectedIdx :" + originSelectedIdx);
        if (originSelectedIdx != selectedIdx) {
            EvaluationMode.addModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.stallParameter(getQualifiedParamName(), compRef);
        } else {
            EvaluationMode.removeModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.unstallParameter(getQualifiedParamName(), compRef);
        }

//        if (evalContext.isAfterFirstEvaluation()) {
            EvaluationMode.updateStatusOfParameters(compRef);
//        }
        EvaluationMode.updateStatusOfAllValueEditorPanes(compRef);
    }

    /** see the index of this.value as source. copy the index to dataobject.value */
    public void commitEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CEnumeration dataObj = (CEnumeration) evalContext.getDataObject(this.getQualifiedParamName());
        DataObjectUtil.setSelectedIndex(this.getSelected(), dataObj.getEnumList());
    }
}

class MatrixValueEditorPane extends ValueEditorPane {
    MatrixPopupEditor popupEditor = null;

    public MatrixValueEditorPane(final String qualifiedParamName, final ComponentReference compRef) {
        super(qualifiedParamName, compRef);
        JButton button = new JButton();
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (! compRef.getImplementationEditor().isMatrixFloatingEditorVisible(qualifiedParamName)) {
                    compRef.getImplementationEditor().showMatrixFloatingEditor(qualifiedParamName);
                } else {
                    compRef.getImplementationEditor().hideMatrixFloatingEditor(qualifiedParamName);
                }

//                if (! popupEditor.isVisible()) {
//                    EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
//                    CMatrix matrixObj = (CMatrix) evalContext.getDataObject(getQualifiedParamName());
//                    popupEditor.setMatrixValue(DataObjectUtil.cloneRowList(matrixObj.getMatrixValue()));
//                    popupEditor.setVisible(true);
//                } else {
//                    popupEditor.setVisible(true);
//                }
            }
        });

        setComponent(button);
        setMatrixValue(Collections.EMPTY_LIST);

        /* init popup editor */
        popupEditor = new MatrixPopupEditor(qualifiedParamName, this, getMatrixValue(), compRef);
        popupEditor.setSize(430, 280);
        Window owner = ModelEditorFrame.getFrame();
        int x = owner.getX() + (owner.getWidth() - popupEditor.getWidth()) / 2;
        int y = owner.getY() + (owner.getHeight() - popupEditor.getHeight()) / 2;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        popupEditor.setLocation(x, y);
    }

    public List getMatrixValue() {
        return (List) getValue();
    }

    public JButton getButton() {
        return (JButton) getComponent();
    }

    public void setMatrixValue(List rowList) {
        setValue(rowList);
    }

    public MatrixPopupEditor getPopupEditor() {
        return popupEditor;
    }

    /** called to update the value displayed in Editor based on the current CDataObject value */
    public void updateEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CMatrix matrixObj = (CMatrix) evalContext.getDataObject(getQualifiedParamName());
        setValue(matrixObj.getValue());
        getButton().setText("[" + matrixObj.getRowSize() + " x " + matrixObj.getColumnSize() + " matrix]");
        if (getPopupEditor().isVisible()) {
            getPopupEditor().setMatrixValue(DataObjectUtil.cloneRowList(matrixObj.getMatrixValue()));
        }
    }

    public void commitEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        evalContext.getDataObject(getQualifiedParamName()).setValue(DataObjectUtil.cloneRowList(getMatrixValue()));
    }

    public void acceptEditorValue() {
        this.value = popupEditor.getMatrixValue();
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        Object contextValue = evalContext.getDataObject(getQualifiedParamName()).getValue();

        int[] rowColumnSizes = DataObjectUtil.getMatrixSize(this.getMatrixValue());
        getButton().setText("[" + rowColumnSizes [0] + " x " + rowColumnSizes [1] + " matrix]");

        System.out.println("context value = " + contextValue + " vs " + value);
        if (! contextValue.equals(getValue())) {
            EvaluationMode.addModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.stallParameter(getQualifiedParamName(), compRef);
        } else {
            EvaluationMode.removeModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.unstallParameter(getQualifiedParamName(), compRef);
        }

//        if (evalContext.isAfterFirstEvaluation()) {
            EvaluationMode.updateStatusOfParameters(compRef);
//        }
        EvaluationMode.updateStatusOfAllValueEditorPanes(compRef);
    }
}

class VectorValueEditorPane extends ValueEditorPane {
    JPanel popupEditor = new JPanel(); // todo : init & make this an popup editor

    public VectorValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        super(qualifiedParamName, compRef);
        JButton button = new JButton();
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.out.println("open vector value editor");
            }
        });
        setComponent(button);
        setVectorValue(Collections.EMPTY_LIST);
    }

    public List getVectorValue() {
        return (List) getValue();
    }

    public JButton getButton() {
        return (JButton) getComponent();
    }

    public void setVectorValue(List elements) {
        setValue(elements);

    }

    public JPanel getPopupEditor() {
        return popupEditor;
    }

    /** called to update the value displayed in Editor based on the current CDataObject value */
    public void updateEditorValue() {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CVector vectorObj = (CVector) evalContext.getDataObject(getQualifiedParamName());
        setValue(vectorObj.getValue());
        getButton().setText("[" + vectorObj.size() + " elements]");
        //getPopupEditor().setElements(vectorObj.getVectorValue()); // todo: uncomment
    }

    public void acceptEditorValue() {
        value = Collections.EMPTY_LIST;
        //value = popupEditor.getElements(); // todo: uncomment
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        Object value = evalContext.getDataObject(getQualifiedParamName()).getValue();
        if (! value.equals(getValue())) {
            EvaluationMode.addModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.stallParameter(getQualifiedParamName(), compRef);
        } else {
            EvaluationMode.removeModifiedParam(getLocalParamName(), compRef);
            EvaluationMode.unstallParameter(getQualifiedParamName(), compRef);
        }

//        if (evalContext.isAfterFirstEvaluation()) {
            EvaluationMode.updateStatusOfParameters(compRef);
//        }
        EvaluationMode.updateStatusOfAllValueEditorPanes(compRef);
    }
}

