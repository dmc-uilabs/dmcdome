package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 24.
 */
public class FloatingScriptEditor extends JPanel {

    //private JEditorPane scriptEditorPane;
    private JTextPane scriptEditorPane;
    private JScrollPane scriptEditorScrollPane;
    private JPanel buttonPanel;
    private JLabel dragLabel;
    private JLabel cancelLabel;
    private JLabel acceptLabel;
    private ComponentReference compRef;
    private boolean startDrag = false;
    private final int RUBBER_BAND_SIZE = 14;
    private boolean isNarrowingCodeCompletion = false;
    public static final String TAB = "    ";


    public FloatingScriptEditor(ComponentReference compRef) {
        this.compRef = compRef;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initComponents();
    }

    protected void addBindings() {

//        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "cancelNarrowing");
//        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "cancelNarrowing");
//        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "cancelNarrowing");
//        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "cancelNarrowing");
//        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "cancelNarrowing");
//        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), "cancelNarrowing");
//        scriptEditorPane.getActionMap().put("cancelNarrowing", new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                if (compRef.getImplementationEditor().isCodeCompletionShown()) { compRef.getImplementationEditor().closeCodeCompletion(); }
//            }
//        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_MASK), "cellNavRight");
        scriptEditorPane.getActionMap().put("cellNavRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ImplementationEditor implEditor = compRef.getImplementationEditor();
                implEditor.closeScriptEditor(true);
                /* move to the next cell */
                BaseCell editedScriptCell = implEditor.getEditedScriptCell();
                BaseCell nextCell = (BaseCell) UIUtil.getNextComponent(editedScriptCell, true);
                implEditor.showScriptEditor(nextCell, nextCell.dotPosition);
                // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
            }
        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_MASK), "cellNavLeft");
        scriptEditorPane.getActionMap().put("cellNavLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ImplementationEditor implEditor = compRef.getImplementationEditor();
                implEditor.closeScriptEditor(true);
                /* move to the prev cell */
                BaseCell editedScriptCell = implEditor.getEditedScriptCell();
                BaseCell prevCell = (BaseCell) UIUtil.getPreviousComponent(editedScriptCell, true);
                implEditor.showScriptEditor(prevCell, prevCell.dotPosition);
                // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
            }
        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "caretDown");
        scriptEditorPane.getActionMap().put("caretDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (compRef.getImplementationEditor().isCodeCompletionShown()) { compRef.getImplementationEditor().getCodeCompletionPanel().moveSelection(false); }
                else { scriptEditorPane.getActionMap().get(DefaultEditorKit.downAction).actionPerformed(e);                }
            }
        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_MASK), "cellNavDown");
        scriptEditorPane.getActionMap().put("cellNavDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
//                System.out.println("down during normal editing detected. close script editor and save changes. move to down relation or interface");
                ImplementationEditor implEditor = compRef.getImplementationEditor();
                implEditor.closeScriptEditor(true);
                /* move to the lower relation */
                BaseBar currentBar = implEditor.getEditedScriptCell().getBar();
                if (currentBar instanceof RelationBar) {
                    /* first try find an lower relation with input cells */
                    RelationBar lowerRelation = (RelationBar) UIUtil.getNextComponent(currentBar, false);
                    while (lowerRelation != null && lowerRelation.getLeftPanel().getComponentCount() == 0) {
                        lowerRelation = (RelationBar) UIUtil.getNextComponent(lowerRelation, false);
                    }

                    if (lowerRelation != null) {
                        /* if an lower relation found, show script editor on it */
                        BaseCell lastEditedCell = (BaseCell) lowerRelation.getLeftPanel().getComponent(lowerRelation.lastEditedCellIndex);
                        implEditor.showScriptEditor(lastEditedCell, lastEditedCell.dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                    } else {
                        /* if not found, show script editor on interface output cell */
                        InterfaceBar interfaceBar = compRef.getInterfaceBar();
                        BaseCell lastEditedCell = (BaseCell) interfaceBar.getRightPanel().getComponent(interfaceBar.lastEditedCellIndex);
                        implEditor.showScriptEditor(lastEditedCell, lastEditedCell.dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                    }
                } else if (currentBar instanceof InterfaceBar) {
                    if (compRef.getRelationEditor().getComponentCount() == 0) {
                        implEditor.showScriptEditor(implEditor.getEditedScriptCell(), implEditor.getEditedScriptCell().dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                        return;
                    }

                    /* first try find an lower relation with input cells */
                    RelationBar lowerRelation = (RelationBar) compRef.getRelationEditor().getComponent(0);
                    while (lowerRelation != null && lowerRelation.getLeftPanel().getComponentCount() == 0) {
                        lowerRelation = (RelationBar) UIUtil.getNextComponent(lowerRelation, false);
                    }

                    if (lowerRelation == null) {
                        implEditor.showScriptEditor(implEditor.getEditedScriptCell(), implEditor.getEditedScriptCell().dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                        return;
                    } else {
                        BaseCell lastEditedCell = (BaseCell) lowerRelation.getLeftPanel().getComponent(lowerRelation.lastEditedCellIndex);
                        implEditor.showScriptEditor(lastEditedCell, lastEditedCell.dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                    }
                }
            }
        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "caretUp");
        scriptEditorPane.getActionMap().put("caretUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (compRef.getImplementationEditor().isCodeCompletionShown()) { compRef.getImplementationEditor().getCodeCompletionPanel().moveSelection(true); }
                else { scriptEditorPane.getActionMap().get(DefaultEditorKit.upAction).actionPerformed(e);                }
            }
        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.ALT_MASK), "cellNavUp");
        scriptEditorPane.getActionMap().put("cellNavUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
//                System.out.println("up during normal editing detected. close script editor and save changes. move to upper relation or interface");
                ImplementationEditor implEditor = compRef.getImplementationEditor();
                implEditor.closeScriptEditor(true);
                /* move to the upper relation */
                BaseBar currentBar = implEditor.getEditedScriptCell().getBar();
                if (currentBar instanceof RelationBar) {
                    /* first try find an upper relation with input cells */
                    RelationBar upperRelation = (RelationBar) UIUtil.getPreviousComponent(currentBar, false);
                    while (upperRelation != null && upperRelation.getLeftPanel().getComponentCount() == 0) {
                        upperRelation = (RelationBar) UIUtil.getPreviousComponent(upperRelation, false);
                    }

                    if (upperRelation != null) {
                        /* if an upper relation found, show script editor on it */
                        BaseCell lastEditedCell = (BaseCell) upperRelation.getLeftPanel().getComponent(upperRelation.lastEditedCellIndex);
                        implEditor.showScriptEditor(lastEditedCell, lastEditedCell.dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                    } else {
                        /* if not found, show script editor on interface output cell */
                        InterfaceBar interfaceBar = compRef.getInterfaceBar();
                        BaseCell lastEditedCell = (BaseCell) interfaceBar.getRightPanel().getComponent(interfaceBar.lastEditedCellIndex);
                        implEditor.showScriptEditor(lastEditedCell, lastEditedCell.dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                    }
                } else if (currentBar instanceof InterfaceBar) {
                    if (compRef.getRelationEditor().getComponentCount() == 0) {
                        implEditor.showScriptEditor(implEditor.getEditedScriptCell(), implEditor.getEditedScriptCell().dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                        return;
                    }

                    /* first try find an uppper relation with input cells */
                    RelationBar upperRelation = (RelationBar) compRef.getRelationEditor().getComponent(compRef.getRelationEditor().getComponentCount() - 1);
                    while (upperRelation != null && upperRelation.getLeftPanel().getComponentCount() == 0) {
                        upperRelation = (RelationBar) UIUtil.getNextComponent(upperRelation, false);
                    }

                    if (upperRelation == null) {
                        implEditor.showScriptEditor(implEditor.getEditedScriptCell(), implEditor.getEditedScriptCell().dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                        return;
                    } else {
                        BaseCell lastEditedCell = (BaseCell) upperRelation.getLeftPanel().getComponent(upperRelation.lastEditedCellIndex);
                        implEditor.showScriptEditor(lastEditedCell, lastEditedCell.dotPosition);
                        // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                        // implEditor.getScriptEditor().getScriptEditorPane().selectAll();
                    }
                }
            }
        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "acceptCCOrInsertTab");
        scriptEditorPane.getActionMap().put("acceptCCOrInsertTab", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (compRef.getImplementationEditor().isCodeCompletionShown()) { compRef.getImplementationEditor().getCodeCompletionPanel().acceptSelection(); compRef.getImplementationEditor().closeCodeCompletion(); }
                else {
                    try {
                        scriptEditorPane.getDocument().insertString(scriptEditorPane.getCaret().getMark(), TAB, SimpleAttributeSet.EMPTY);
                    } catch (BadLocationException ble) { ble.printStackTrace(); }
                }
            }
        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "acceptCCOrCloseScriptEditor");
        scriptEditorPane.getActionMap().put("acceptCCOrCloseScriptEditor", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (compRef.getImplementationEditor().isCodeCompletionShown()) { compRef.getImplementationEditor().getCodeCompletionPanel().acceptSelection(); compRef.getImplementationEditor().closeCodeCompletion(); }
                else { compRef.getImplementationEditor().closeScriptEditor(true); }
            }
        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.ALT_MASK), DefaultEditorKit.insertBreakAction);
        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK), DefaultEditorKit.insertBreakAction);

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, Event.CTRL_MASK), "showCellCompletion");
        scriptEditorPane.getActionMap().put("showCellCompletion", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { compRef.getImplementationEditor().showCellCompletion(); }
        });

        scriptEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeCodeCompletionOrCloseScriptEditor");
        scriptEditorPane.getActionMap().put("closeCodeCompletionOrCloseScriptEditor", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (compRef.getImplementationEditor().isCodeCompletionShown()) { compRef.getImplementationEditor().closeCodeCompletion(); }
                else { compRef.getImplementationEditor().closeScriptEditor(false); }
            }
        });

        scriptEditorPane.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent event) {
                /* during code completion mode */
                if (compRef.getImplementationEditor().isCodeCompletionShown()) {
                    if (Character.isLetterOrDigit(event.getKeyChar()) || event.getKeyChar() == KeyEvent.VK_BACK_SPACE
                            || event.getKeyChar() == '+' || event.getKeyChar() == '-' || event.getKeyChar() == '(' || event.getKeyChar() == ')'
                            || event.getKeyChar() == '*' || event.getKeyChar() == '/' || event.getKeyChar() == '[' || event.getKeyChar() == ']'
                            || event.getKeyChar() == '<' || event.getKeyChar() == '>' || event.getKeyChar() == '.' || event.getKeyChar() == ','
                            || event.getKeyChar() == '^' || event.getKeyChar() == '%' || event.getKeyChar() == '{' || event.getKeyChar() == '}'
                            || event.getKeyChar() == '=' || event.getKeyChar() == ' ') {
                        isNarrowingCodeCompletion = true;
                    } else {
                        isNarrowingCodeCompletion = false;
                    }
                }
            }
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_HOME || event.getKeyCode() == KeyEvent.VK_END
                        || event.getKeyCode() == KeyEvent.VK_PAGE_UP || event.getKeyCode() == KeyEvent.VK_PAGE_UP
                        || event.getKeyCode() == KeyEvent.VK_LEFT || event.getKeyCode() == KeyEvent.VK_RIGHT ) {
                    compRef.getImplementationEditor().closeCodeCompletion();
                }
            }
        });

        scriptEditorPane.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent event) {
                if (compRef.getImplementationEditor().isCodeCompletionShown()) {
                    if (isNarrowingCodeCompletion) {
                        compRef.getImplementationEditor().showCellCompletion();
                    } else {
                        compRef.getImplementationEditor().closeCodeCompletion();
                    }
                }
            }
        });

        scriptEditorPane.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                isNarrowingCodeCompletion = false;
            }

            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON3) {
                    compRef.getImplementationEditor().closeScriptEditor(true);
                    return;
                }
                if (event.isControlDown()) {
                    // note. actually, compRef.getImplementationEditor().getScriptEditor().getScriptEditorPane() == scriptEditorPane
                    if (event.isShiftDown()) {
                        compRef.getImplementationEditor().getScriptEditor().getScriptEditorPane().setText("");
                    }
                    compRef.getImplementationEditor().getScriptEditor().getScriptEditorPane().getActionMap().get("showCellCompletion").actionPerformed(new ActionEvent(this, 0, "show cell completion by clicking with control-down in the script editor"));
                }
            }
        });

    }

    private void initComponents() {
        //scriptEditorPane = new JEditorPane("text/html", "<html><font color=#000000>no script now</font></html>");
        scriptEditorPane = new JTextPane(new ColorizedDocument(new CellScriptDelimiterList(compRef)));
        scriptEditorPane.setFont(UIUtil.PARAM_EDITOR_FONT);
        addBindings();
        scriptEditorPane.setBorder(null);

        buttonPanel = new JPanel();
        cancelLabel = new JLabel("cancel", UIUtil.createImageIcon("images/cancel_x.gif", "drag here to resize"), JLabel.RIGHT);
        cancelLabel.setToolTipText("(esc)");
        cancelLabel.setFont(UIUtil.TINY_DIALOG_FONT);
        cancelLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                compRef.getImplementationEditor().closeScriptEditor(false);
            }

            public void mouseEntered(MouseEvent event) {
                cancelLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent event) {
                cancelLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        acceptLabel = new JLabel("ok  ", UIUtil.createImageIcon("images/accept_v.gif", "drag here to resize"), JLabel.RIGHT);
        acceptLabel.setFont(UIUtil.TINY_DIALOG_FONT);
        acceptLabel.setToolTipText("(enter)");
//        acceptLabel.setPreferredSize(new Dimension(0, RUBBER_BAND_SIZE));
//        acceptLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, RUBBER_BAND_SIZE));
//        acceptLabel.setMinimumSize(new Dimension(0, RUBBER_BAND_SIZE));
        acceptLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                compRef.getImplementationEditor().closeScriptEditor(true);
            }

            public void mouseEntered(MouseEvent event) {
                acceptLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent event) {
                acceptLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        dragLabel = new JLabel(UIUtil.createImageIcon("images/drag_rubber.gif", "drag here to resize"));
//        dragLabel.setPreferredSize(new Dimension(RUBBER_BAND_SIZE, RUBBER_BAND_SIZE));
//        dragLabel.setMaximumSize(dragLabel.getPreferredSize());
//        dragLabel.setMinimumSize(dragLabel.getPreferredSize());

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(acceptLabel);
        buttonPanel.add(cancelLabel);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(dragLabel);
        buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, UIUtil.SCRIPT_EDITOR_BT_HEIGHT));
        buttonPanel.setPreferredSize(new Dimension(UIUtil.CG_SCRIPT_DISP_WIDTH, UIUtil.SCRIPT_EDITOR_BT_HEIGHT));
        buttonPanel.setMinimumSize(new Dimension(UIUtil.CG_SCRIPT_DISP_WIDTH, UIUtil.SCRIPT_EDITOR_BT_HEIGHT));

        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent event) {
                startDrag = false;
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            int dragInsetX;
            int dragInsetY;

            public void mouseMoved(MouseEvent event) {
                if (event.getX() > (getWidth() - RUBBER_BAND_SIZE) && event.getY() > (getHeight() - RUBBER_BAND_SIZE)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }

            /* for dependency feedback visualization */
            public void mouseDragged(MouseEvent event) {
                if (event.getX() > (getWidth() - RUBBER_BAND_SIZE) && event.getY() > (getHeight() - RUBBER_BAND_SIZE)) {
                    if (! startDrag) {
                        startDrag = true;
                        dragInsetX = getWidth() - event.getX();
                        dragInsetY = getHeight() - event.getY();
                    }
                }

                if (startDrag) {
                    int width = event.getX() + dragInsetX;
                    int height = event.getY() + dragInsetY;
                    width = (width > UIUtil.CG_SCRIPT_DISP_WIDTH) ? width : UIUtil.CG_SCRIPT_DISP_WIDTH;
                    height = (height > UIUtil.CG_SCRIPT_DISP_HEIGHT + UIUtil.SCRIPT_EDITOR_BT_HEIGHT) ? height: UIUtil.CG_SCRIPT_DISP_HEIGHT + UIUtil.SCRIPT_EDITOR_BT_HEIGHT;
                    FloatingScriptEditor.this.setBounds(FloatingScriptEditor.this.getX(), FloatingScriptEditor.this.getY(), width, height);
                    validate();
                }
            }
        });

        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        scriptEditorScrollPane = new JScrollPane(scriptEditorPane);
        scriptEditorScrollPane.setBorder(null);
        scriptEditorScrollPane.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        scriptEditorScrollPane.setPreferredSize(new Dimension(UIUtil.CG_SCRIPT_DISP_WIDTH, UIUtil.CG_SCRIPT_DISP_HEIGHT));
        scriptEditorScrollPane.setMinimumSize(scriptEditorScrollPane.getPreferredSize());
        scriptEditorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scriptEditorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(scriptEditorScrollPane);
        this.add(buttonPanel);
    }

    public Point getCodeCompletionOrigin() {
        return new Point(100, 100);
    }

    public void setText(String script) {
        scriptEditorPane.setText(script);
    }

    public String getText() {
        return scriptEditorPane.getText();
    }

    public boolean requestFocusInWindow() {
        return scriptEditorPane.requestFocusInWindow();
    }

    public JTextPane getScriptEditorPane() {
        return scriptEditorPane;
    }

    public Rectangle getVisibleRect() {
        return scriptEditorPane.getVisibleRect();
    }
}
