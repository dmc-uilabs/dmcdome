package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.DataObjectUtil;
import mit.cadlab.dome3.plugin.catalog.pcc.CodeBase;
import mit.cadlab.dome3.plugin.catalog.pcc.CodeCompletion;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 20.
 */
public class ImplementationEditor extends JPanel {

    private InterfaceEditor interfaceEditor;
    private RelationEditor relationEditor;
    private RelationToolPanel relationToolBar;
    private JLayeredPane layeredPane;
    private JScrollPane scrollPane;

    private JLabel draggedBar;
    private JLabel multiDraggedBar;
    private JLabel dragInfoLabel;
    private JLabel insertionMarker;
    private JLabel testMarker;
    private List sampleMarkerList = new ArrayList();

    private static final String DRAG_MSG_1_1 =  "move ";
    private static final String DRAG_MSG_1_2 =  "copy ";
    private static final String DRAG_MSG_1_3 =  "add & map ";
    private static final String DRAG_MSG_2_1 =  " cell into ";
    private static final String DRAG_MSG_2_2 =  " cells into ";

    private JFrame evalProgressWindow;

    private ComponentReference compRef;

    private CellCompletionPopup ccPopup;
    private FloatingScriptEditor scriptEditor;
    private boolean isImplLoaded = false;

    private boolean isCellWrapEnabled = true;

    /* which cell is being edited? is it a itf cell or a rel cell? */
    private BaseCell editedScriptCell; // has a reference to itf output param or rel input param
    private boolean isNarrowingCodeCompletion;
//    private Graphics graphics;
    private String interfaceName;
    private String implementationName;
    private int dragTargetBarIdx;
    private int dragSourceBarIdx;

    JPopupMenu cellPopupMenu;
    JPopupMenu relPopupMenu;
    JPopupMenu dragPopupMenu1; // map, copy, move, cancel
    Map matrixFloatEditorMap = new HashMap();;
    //JPopupMenu dragPopupMenu2; // copy, move, cancel

    /* returns if editedScriptCell is instance of InterfaceOutputCell */
    public boolean isInterfaceCell() {
        return (editedScriptCell instanceof InterfaceOutputCell);
    }

    public ImplementationEditor() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.compRef = new ComponentReference(this);

//        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//        int prefWidth = (screen.getWidth() < 1024) ? screen.width : 1024;
//        int prefHeight = (screen.getHeight() < 768) ? screen.height : 768;
//        this.setPreferredSize(new Dimension(prefWidth, prefHeight));
//        this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

        initComponents();
    }

    public boolean isCodeCompletionShown() {
        return (ccPopup != null) && ccPopup.isVisible();
    }

    public boolean isMatrixFloatingEditorVisible(String qualifiedParamName) {
        MatrixFloatingEditor floatingEditor = (MatrixFloatingEditor) matrixFloatEditorMap.get(qualifiedParamName);
        if (floatingEditor != null) {
            return floatingEditor.isVisible();
        } else {
            return false;
        }
    }

    public void showMatrixFloatingEditor(String qualifiedParamName) {
//        BaseCell cell = compRef.getCell(qualifiedParamName);
//        MatrixValueEditorPane matrixValueEditorPane = (MatrixValueEditorPane) cell.getValueEditorPane();
//        List rowList = DataObjectUtil.cloneRowList(matrixValueEditorPane.getMatrixValue());
        MatrixFloatingEditor floatingEditor = (MatrixFloatingEditor) matrixFloatEditorMap.get(qualifiedParamName);
        if (matrixFloatEditorMap.get(qualifiedParamName) == null) {
            floatingEditor = new MatrixFloatingEditor(qualifiedParamName, compRef);
            matrixFloatEditorMap.put(qualifiedParamName, floatingEditor);
            layeredPane.add(floatingEditor, new Integer(7));
        }

        floatingEditor.setVisible(true);

    }

    public void hideMatrixFloatingEditor(String qualifiedParamName) {
        MatrixFloatingEditor floatingEditor = (MatrixFloatingEditor) matrixFloatEditorMap.get(qualifiedParamName);
        floatingEditor.setVisible(false);
    }

    private void initDragPopupMenu() {
        dragPopupMenu1 = new JPopupMenu();
        dragPopupMenu1.setFont(UIUtil.MENU_BAR_FONT);

        JMenuItem [] items = new JMenuItem [4];
        items [0] = new JMenuItem(ModelEditorKit.CopyOptionPopupMenu1Action);
        items [1] = new JMenuItem(ModelEditorKit.CopyOptionPopupMenu2Action);
        items [2] = new JMenuItem(ModelEditorKit.CopyOptionPopupMenu3Action);
        items [3] = new JMenuItem(ModelEditorKit.CopyOptionPopupMenu4Action);
        dragPopupMenu1.add(items [0]);
        dragPopupMenu1.add(items [1]);
        dragPopupMenu1.add(items [2]);
        dragPopupMenu1.addSeparator();
        dragPopupMenu1.add(items [3]);

        dragPopupMenu1.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
                compRef.getImplementationEditor().hideInsertionMarker();
            }
        } );

//        dragPopupMenu2 = new JPopupMenu();
//        dragPopupMenu2.setFont(UIUtil.MENU_BAR_FONT);
//        dragPopupMenu2.add(items [1]);
//        dragPopupMenu2.add(items [2]);
//        dragPopupMenu2.addSeparator();
//        dragPopupMenu2.add(items [3]);
//
//        dragPopupMenu2.addPopupMenuListener(new PopupMenuListener() {
//            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//            }
//
//            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//            }
//
//            public void popupMenuCanceled(PopupMenuEvent e) {
//                compRef.getImplementationEditor().hideInsertionMarker();
//            }
//        } );
    }

    private void initCellPopupMenu() {
        cellPopupMenu = new JPopupMenu();
        cellPopupMenu.setFont(UIUtil.MENU_BAR_FONT);
        JMenuItem[] menuItems = new JMenuItem [5];
        menuItems [0] = new JMenuItem("modify this parameter...");
        menuItems [1] = new JMenuItem("copy selected parameter(s) to interface");
        menuItems [2] = new JMenuItem("delete selected parameter(s)");
        menuItems [3] = new JMenuItem("sort parameters by name: A -> Z");
        menuItems [4] = new JMenuItem("sort parameters by name: Z -> A");

        menuItems [0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BaseBar bar = compRef.getSelectedCell().getBar();
                if (bar instanceof InterfaceBar) {
                    compRef.getInterfaceEditor().editInterface();
                } else {
                    compRef.getRelationEditor().editRelation((RelationBar) bar);
                }
            }
        });

        menuItems [1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        menuItems [2].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compRef.getRelationEditor().removeRelationOrCell();
            }
        });

        menuItems [3].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BaseCell cell = compRef.getSelectedCell();
                int barIdx = (cell.getBar() instanceof InterfaceBar) ? -1 : UIUtil.indexOfComponent(cell.getBar());
                boolean isLeft = (cell.getCellType() == BaseCell.ITF_INPUT) || (cell.getCellType() == BaseCell.REL_INPUT);
                compRef.getRelationEditor().sortCells(barIdx, isLeft, true);
            }
        });

        menuItems [4].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BaseCell cell = compRef.getSelectedCell();
                int barIdx = (cell.getBar() instanceof InterfaceBar) ? -1 : UIUtil.indexOfComponent(cell.getBar());
                boolean isLeft = (cell.getCellType() == BaseCell.ITF_INPUT) || (cell.getCellType() == BaseCell.REL_INPUT);
                compRef.getRelationEditor().sortCells(barIdx, isLeft, false);
            }
        });

        for (int i = 0; i < menuItems.length; i++) {
            if (i == 1 || i == 3) {
                cellPopupMenu.addSeparator();
            }
            cellPopupMenu.add(menuItems [i]);
        }
    }

    private void initRelationPopupMenu() {
        relPopupMenu = new JPopupMenu();
        relPopupMenu.setFont(UIUtil.MENU_BAR_FONT);
        JMenuItem[] menuItems = new JMenuItem [7];
        menuItems [0] = new JMenuItem("modify this relation...");
        menuItems [1] = new JMenuItem("delete selected relation(s)");
        menuItems [2] = new JMenuItem("copy all parameters of this relation to the interface");
        menuItems [3] = new JMenuItem("sort relations by name: A -> Z");
        menuItems [4] = new JMenuItem("sort relations by name: Z -> A");
        menuItems [5] = new JMenuItem("sort relations by alias: A -> Z");
        menuItems [6] = new JMenuItem("sort relations by alias: Z -> A");

        menuItems [0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BaseBar bar = compRef.getSelectedBar();
                if (bar instanceof InterfaceBar) {
                    compRef.getInterfaceEditor().editInterface();
                } else {
                    compRef.getRelationEditor().editRelation((RelationBar) bar);
                }
            }
        });

        menuItems [1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compRef.getRelationEditor().removeRelationOrCell();
            }
        });

        menuItems [2].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        menuItems [3].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        menuItems [3].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compRef.getRelationEditor().sortRelations(RelationEditor.SORT_BY_REL_NAME, true);
            }
        });

        menuItems [4].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compRef.getRelationEditor().sortRelations(RelationEditor.SORT_BY_REL_NAME, false);
            }
        });

        menuItems [5].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compRef.getRelationEditor().sortRelations(RelationEditor.SORT_BY_REL_ALIAS, true);
            }
        });

        menuItems [6].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compRef.getRelationEditor().sortRelations(RelationEditor.SORT_BY_REL_ALIAS, false);
            }
        });



        for (int i = 0; i < menuItems.length; i++) {
            if (i == 1 || i == 3) {
                relPopupMenu.addSeparator();
            }
            relPopupMenu.add(menuItems [i]);
        }
    }

    public void showEvaluationProgressWindow() {
        evalProgressWindow.setTitle("Evaluation - [" + compRef.getCurrentCModel().getName() + "] " + compRef.getCurrentCInterface().getName());

        JFrame owner = ModelEditorFrame.getFrame();

        int x = owner.getX() + (owner.getWidth() - evalProgressWindow.getWidth()) / 2;
        int y = owner.getY() + (owner.getHeight() - evalProgressWindow.getHeight()) / 2;
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        evalProgressWindow.setLocation(x, y);
        evalProgressWindow.setVisible(true);
        evalProgressWindow.pack();
        evalProgressWindow.show();
    }

    public void clearFailureReport() {
        execDetailTextarea.setText("");
        execDetailTextarea.setVisible(false);
    }

    public void setFailureReport(String msg) {
        execDetailTextarea.setText(msg);
        execDetailTextarea.setVisible(true);
        closeBt.requestFocusInWindow();
        evalProgressWindow.pack();
    }

    public void hideEvaluationProgressWindow() {
        evalProgressWindow.setVisible(false);
        evalProgressWindow.dispose();
    }

    public void stopEvaluationAndHideEvaluationProgressWindow() {
        ModelEditorKit.StopEvaluationAction.actionPerformed(new ActionEvent(this, 0, "stop requested from evaluation progress window"));
        evalProgressWindow.setVisible(false);
        evalProgressWindow.dispose();
    }

    JTextArea execDetailTextarea;
    JButton closeBt;

    private void initEvaluationProgressWindow() {
        evalProgressWindow = new JFrame();
        evalProgressWindow.setSize(420, 100);
        evalProgressWindow.setVisible(false);
        evalProgressWindow.setIconImage(UIUtil.createImageIcon("images/greenDomeWindow.gif").getImage());
        //evalProgressWindow.setIconImage(null);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        evalProgressWindow.getContentPane().add(centerPanel, BorderLayout.CENTER);
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        JLabel msgLb = new JLabel("Running... (remaining time will be available here)", UIUtil.createImageIcon("images/Hourglass.gif"), SwingConstants.LEFT);
        msgLb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        infoPanel.add(msgLb);
        infoPanel.setPreferredSize(new Dimension(420, 40));
        centerPanel.add(infoPanel);

        JButton detailBt = new JButton("detail...");
        detailBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                execDetailTextarea.setVisible(! execDetailTextarea.isVisible());
                //evalProgressWindow.validate();
                evalProgressWindow.pack();
            }
        });
        closeBt = new JButton("hide");
        closeBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hideEvaluationProgressWindow();
            }
        } );
        JButton stopBt = new JButton("stop");
        stopBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopEvaluationAndHideEvaluationProgressWindow();
            }
        } );
        JPanel btPanel = new JPanel();
        btPanel.setPreferredSize(new Dimension(350, 35));
        btPanel.setLayout(new FlowLayout(SwingConstants.RIGHT));

        btPanel.add(detailBt);
        btPanel.add(closeBt);
        btPanel.add(stopBt);
        centerPanel.add(btPanel);

        //execDetailTextarea = UIUtil.createTextArea("which relation is running. which relation finished. how long they took. possible visulation", 3, 20);
        execDetailTextarea = UIUtil.createTextArea("(workflow style visualization will be available here to show the execution point of the current simulation)", 3, 20);
        execDetailTextarea.setPreferredSize(new Dimension(350, 100));
        execDetailTextarea.setVisible(false);
        execDetailTextarea.setWrapStyleWord(true);
        execDetailTextarea.setLineWrap(true);
        Border detailTextareaBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIUtil.EVAL_DETAIL_BORDER_COLOR, 3), BorderFactory.createEmptyBorder(3,3,3,3));
        execDetailTextarea.setBorder(detailTextareaBorder);
        centerPanel.add(execDetailTextarea);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        evalProgressWindow.getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                hideEvaluationProgressWindow();
            }
        }, "ESCAPE", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void initComponents() {
        this.removeAll();

        this.initEvaluationProgressWindow();

        layeredPane = new JLayeredPane();
        layeredPane.setBackground(UIUtil.REL_EDITOR_BG_COLOR);
        layeredPane.setOpaque(true);
        layeredPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                compRef.getImplementationEditor().requestFocusInWindow();

                if (compRef.getImplementationEditor().isScriptEditorShown()) {
                    compRef.getImplementationEditor().closeScriptEditor(true);
                }

                compRef.clearBarAndCellSelection();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                if (compRef.getImplementationEditor().isImplLoaded() && event.getClickCount() >= 2) {
                    ModelEditorKit.AddRelAction.actionPerformed(new ActionEvent(this, 0, "add-relation-action requested by double clicking the background area of implementation editor "));
                }
            }
        });

        this.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                ImplementationEditor.this.setBorder(UIUtil.IMPL_EDITOR_HL_BORDER);
            }

            public void focusLost(FocusEvent event) {
                ImplementationEditor.this.setBorder(UIUtil.IMPL_EDITOR_NM_BORDER);
            }
        });

        this.setBorder(UIUtil.IMPL_EDITOR_NM_BORDER);

        this.relationToolBar = new RelationToolPanel(compRef);
        layeredPane.add(relationToolBar, new Integer(1));

        this.relationEditor = new RelationEditor(compRef);
        layeredPane.add(relationEditor, new Integer(1));

        this.interfaceEditor = new InterfaceEditor(compRef);
        layeredPane.add(interfaceEditor, new Integer(1));

        this.scriptEditor = new FloatingScriptEditor(compRef);
        this.scriptEditor.setVisible(false);
        layeredPane.add(scriptEditor, new Integer(2));

        this.ccPopup = new CellCompletionPopup(UIUtil.PARAM_EDITOR_FONT, UIUtil.CODE_COMPLETION_POPUP_WIDTH, 8);
        this.ccPopup.setVisible(false);
        layeredPane.add(ccPopup, new Integer(3));

        insertionMarker = new JLabel();
        insertionMarker.setVisible(false);
        //insertionMarker.setBorder(UIUtil.DRAGGED_BAR_BORDER);
        //insertionMarker.setBounds(0, 0, 100, 100);
        insertionMarker.setBackground(UIUtil.INSERTION_MARKER_COLOR);
        insertionMarker.setOpaque(true);
        layeredPane.add(insertionMarker, new Integer(4));

        testMarker = new JLabel();
        testMarker.setVisible(false);
        testMarker.setBackground(Color.RED);
        testMarker.setOpaque(true);
        layeredPane.add(testMarker, new Integer(6));


        draggedBar = new JLabel();
        draggedBar.setVisible(false);
        draggedBar.setAutoscrolls(true);
        draggedBar.setBorder(UIUtil.DRAGGED_BAR_BORDER);
        //draggedBar.setBounds(0, 0, 100, 100);
        layeredPane.add(draggedBar, new Integer(5));

        multiDraggedBar = new JLabel();
        multiDraggedBar.setVisible(false);
        multiDraggedBar.setBorder(UIUtil.DRAGGED_BAR_BORDER);
        //draggedBar.setBounds(0, 0, 100, 100);
        layeredPane.add(multiDraggedBar, new Integer(5));

        dragInfoLabel = new JLabel();
        dragInfoLabel.setVisible(false);
        dragInfoLabel.setBorder(UIUtil.DRAG_INFO_LABEL_BORDER);
        dragInfoLabel.setFont(UIUtil.DRAG_INFO_LABEL_FONT);
        dragInfoLabel.setForeground(Color.BLACK);
        dragInfoLabel.setBackground(UIUtil.DRAG_INFO_LABEL_BG_COLOR);
        dragInfoLabel.setOpaque(true);
        layeredPane.add(dragInfoLabel, new Integer(5));

        this.scrollPane = new JScrollPane(layeredPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        this.add(scrollPane);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                if (! isImplLoaded) {
                    return;
                }

                /* for each relation bar, update layout constraints because of the changes in row count and column count*/
                for (int i = 0; i < compRef.getRelationEditor().getComponentCount(); i++) {
                    compRef.getRelationBar(i).updateLayoutConstraintsOfSidePanels();
                }
                compRef.getInterfaceBar().updateLayoutConstraintsOfSidePanels();

                /* reposition floating script editor if it is shown */
                if (compRef.getImplementationEditor().isScriptEditorShown()) {
                    BaseCell editedScriptCell = compRef.getImplementationEditor().getEditedScriptCell();
                    Point scriptEditorPos = UIUtil.calculateScriptEditorPosition(editedScriptCell);
                    Dimension scriptEditorSize = editedScriptCell.getScriptEditorSize();
                    FloatingScriptEditor scriptEditor = compRef.getImplementationEditor().getScriptEditor();
                    scriptEditor.setBounds(scriptEditorPos.x, scriptEditorPos.y, scriptEditorSize.width, scriptEditorSize.height);
                }

                UIUtil.updateEditorBounds(compRef);
            }
        });

        initCellPopupMenu();
        initRelationPopupMenu();
        initDragPopupMenu();

        UIUtil.updateEditorBounds(compRef);
    }

    /** remove all indexes for this CModel */
    public void unindexCurrentCModel() {
        CodeBase.getInstance().clearIndex();
        //speedup Clog.debug("clearing the index of the current CModel...");
    }

    /** index all implementations in this CMdoel */
    public void indexCurrentCModel() {
        if (! isImplLoaded) {
            /* if CModel is not loaded yet*/
            return;
        }

        //speedup Clog.debug("indexing the current CModel...");

        Set itfNames = compRef.getCurrentCModel().getInterfaceMap().keySet();
        Set implNames = compRef.getCurrentCInterface().getImplementationMap().keySet();
        for (Iterator j = itfNames.iterator(); j.hasNext();) {
            String itfName = (String) j.next();
            for (Iterator i = implNames.iterator(); i.hasNext();) {
                String implName = (String) i.next();
                indexImplementation(itfName, implName);
            }
        }
    }

    /** unindex all interface in current CModel having given itfName */
    public void unindexInterface(String itfName) {
        CodeBase.getInstance().removeIndex(itfName + "/", true);
    }

    /** unindex all implementation in current CModel having given implName */
    public void unindexImplementation(String implName) {
        Set itfNames = compRef.getCurrentCModel().getInterfaceMap().keySet();
        for (Iterator i = itfNames.iterator(); i.hasNext();) {
            String itfName = (String) i.next();
            CodeBase.getInstance().removeIndex(itfName + "/" + implName + "/", true);
        }
    }

    /** index all implementation in current CModel having given implName */
    public void indexImplementation(String implName) {
        Set itfNames = compRef.getCurrentCModel().getInterfaceMap().keySet();
        for (Iterator i = itfNames.iterator(); i.hasNext();) {
            String itfName = (String) i.next();
            indexImplementation(itfName, implName);
        }
    }

    /** index all interface in current CModel having given itfName */
    public void indexInterface(String itfName) {
        CInterface itf = compRef.getCurrentCModel().getInterface(itfName);
        Set implNames = itf.getImplementationMap().keySet();
        for (Iterator i = implNames.iterator(); i.hasNext();) {
            String implName = (String) i.next();
            indexImplementation(itfName, implName);
        }
    }

    private void indexImplementation(String itfName, String implName) {
        //speedup Clog.debug("indexing implementation... " + itfName + "/" + implName);

        CodeBase codeBase = CodeBase.getInstance();

        CImplementation impl = compRef.getCurrentCModel().getInterface(itfName).getImplementation(implName);
        CNamingService namingService = impl.getNamingService();
        Set params = namingService.getParameters();

        for (Iterator i = params.iterator(); i.hasNext();) {
            CParameter param = (CParameter) i.next();

            if (param instanceof CInterfaceOutputParameter) {
                CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) param;
                String indexedStr = ioParam.getName() + "=" + ioParam.getMapping().getMappingScript();
                codeBase.index(itfName + "/" + implName + "/" + ioParam.getQualifiedName(), indexedStr);

                //speedup Clog.debug("indexing parameter... " + itfName + "/" + implName + "/" + ioParam.getQualifiedName() + " : " + indexedStr);
            }

            if (param instanceof CRelationInputParameter) {
                CRelationInputParameter riParam = (CRelationInputParameter) param;
                String indexedStr = riParam.getName() + "=" + riParam.getMapping().getMappingScript();
                codeBase.index(itfName + "/" + implName + "/" + riParam.getQualifiedName(), indexedStr);

                //speedup Clog.debug("indexing parameter... " + itfName + "/" + implName + "/" + riParam.getQualifiedName() + " : " + indexedStr);
            }
        }
    }

    /** unindex a relation or an interface of currently loaded CModel */
    public void unindexRelationOrInterface(String itfName, String implName, String relAlias) {
        //speedup Clog.debug("clearing the index of relation or interface... " + itfName + "/" + implName + "/" + relAlias);
        CodeBase.getInstance().removeIndex(itfName + "/" + implName + "/" + relAlias + ".", true);
    }

    /** index a relation or an interface in currently loaded CModel with the given implName and relAlias */
    public void indexRelationOrInterface(String itfName, String implName, String relAlias) {
        //speedup Clog.debug("indexing relation or interface... " + itfName + "/" + implName + "/" + relAlias);
        CodeBase codeBase = CodeBase.getInstance();

        CImplementation impl = compRef.getCurrentCInterface().getImplementation(implName);
        CNamingService namingService = impl.getNamingService();
        if (CConstant.ITF_ALIAS.equals(relAlias)) {
            List oParamNames = impl.getOutputParameterNames();
            for (Iterator i = oParamNames.iterator(); i.hasNext();) {
                String paramName = (String) i.next();
                CInterfaceOutputParameter param = namingService.getInterfaceOutputParameter(relAlias + "." + paramName);
                String indexedStr = param.getName() + "=" + param.getMapping().getMappingScript();
                codeBase.index(itfName + "/" + implName + "/" + param.getQualifiedName(), indexedStr);
                //speedup Clog.debug("indexing parameter... " + itfName + "/" + implName + "/" + param.getQualifiedName() + " : " + indexedStr);
            }
        } else {
            CRelation rel = namingService.getRelation(relAlias);
            List iParamNames = rel.getInputParameterNames();
            for (Iterator i = iParamNames.iterator(); i.hasNext();) {
                String paramName = (String) i.next();
                CRelationInputParameter param = namingService.getRelationInputParameter(relAlias + "." + paramName);
                String indexedStr = param.getName() + "=" + param.getMapping().getMappingScript();
                codeBase.index(itfName + "/" + implName + "/" + param.getQualifiedName(), indexedStr);
                //speedup Clog.debug("indexing parameter... " + itfName + "/" + implName + "/" + param.getQualifiedName() + " : " + indexedStr);
            }
        }
    }

    /** unindex a param of currently loaded CModel */
    public void unindexParameter(String itfName, String implName, String relAlias, String paramName) {
        CodeBase.getInstance().removeIndex(itfName + "/" + implName + "/" + relAlias + "." + paramName + ".", true);
        //speedup Clog.debug("clearing the index of a parameter... " + itfName + "/" + implName + "/" + relAlias + "." + paramName + ".");
    }

    /** index implName of currently loaded CModel */
    public void indexParameter(String relAlias, String paramName) {
        //speedup Clog.debug("indexing a parameter... " + interfaceName + "/" + implementationName + "/" + relAlias + "." + paramName);
        CodeBase codeBase = CodeBase.getInstance();

        if (CConstant.ITF_ALIAS.equals(relAlias)) {
            CNamingService namingService = compRef.getCurrentCImplementation().getNamingService();
            CInterfaceOutputParameter ioParam = namingService.getInterfaceOutputParameter(relAlias + "." +paramName);
            String indexedStr = ioParam.getName() + "=" + ioParam.getMapping().getMappingScript();
            codeBase.index(interfaceName + "/" + implementationName + "/" + ioParam.getQualifiedName(), indexedStr);
        } else {
            CNamingService namingService = compRef.getCurrentCImplementation().getNamingService();
            CRelationInputParameter riParam = namingService.getRelationInputParameter(relAlias + "." +paramName);
            String indexedStr = riParam.getName() + "=" + riParam.getMapping().getMappingScript();
            codeBase.index(interfaceName + "/" + implementationName + "/" + riParam.getQualifiedName(), indexedStr);
        }
    }

    public ComponentReference getComponentReference() {
        return compRef;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void closeCodeCompletion() {
        isNarrowingCodeCompletion = false;
        this.ccPopup.setVisible(false);
    }

    public void showGhostBar(int x, int y, int width, int height) {
        this.draggedBar.setBorder(UIUtil.DRAGGED_BAR_BORDER);
        this.draggedBar.setBounds(x, y, width, height);
        this.draggedBar.setVisible(true);
    }

    public void showGhostCell(int x, int y, int width, int height) {
        int numberOfSelectedCells = compRef.getRelationEditor().numberOfSelectedCells;

        this.draggedBar.setBorder(UIUtil.DRAGGED_CELL_BORDER);
        this.draggedBar.setBounds(x, y, width, height);
        this.draggedBar.setVisible(true);

        this.multiDraggedBar.setBorder(UIUtil.DRAGGED_CELL_BORDER);
        this.multiDraggedBar.setBounds(x + 5, y + 5, width, height);
        this.multiDraggedBar.setVisible(numberOfSelectedCells > 1);
    }

    /* to keep current position of the label, set ghostX and ghostY as -1 */
    public void updateDragInfoLabel(int ghostX, int ghostY, int eventX, int eventY, boolean isCopyMode) {
        StringBuffer sb = new StringBuffer(" ");
        int numberOfSelectedCells = compRef.getRelationEditor().numberOfSelectedCells;

        String numberStr = "";
        if (numberOfSelectedCells == 1) {
            numberStr = "one";
        } else if (numberOfSelectedCells == 2) {
            numberStr = "two";
        } else if (numberOfSelectedCells == 3) {
            numberStr = "three";
        } else if (numberOfSelectedCells == 4) {
            numberStr = "four";
        } else {
            numberStr = Integer.toString(numberOfSelectedCells);
        }

        if (isCopyMode) {
            if (compRef.getImplementationEditor().getDragTargetBarIndex() != -1) { // copy into relation bar
                sb.append(DRAG_MSG_1_2).append(numberStr);
            } else { // copy into interface bar
                sb.append(DRAG_MSG_1_3).append(numberStr);
            }
        } else {
            sb.append(DRAG_MSG_1_1).append(numberStr);
        }

        if (numberOfSelectedCells > 1) {
            sb.append(DRAG_MSG_2_2);
        } else {
            sb.append(DRAG_MSG_2_1);
        }

        int targetBarIdx = compRef.getImplementationEditor().getDragTargetBarIndex();
        if (targetBarIdx == -1) {
            sb.append("interface");
        } else {
            sb.append("relation '" + compRef.getRelationBar(targetBarIdx).getRelAlias() + "'");
        }

        if (ghostX != -1 && ghostY != -1) {
            this.dragInfoLabel.setBounds(ghostX, ghostY - 22, 170, 18);

            if (compRef.getRelationEditor().fixedCellList.size() > 0) {
                StringBuffer cellNames = new StringBuffer();
                for (Iterator i = compRef.getRelationEditor().fixedCellList.iterator(); i.hasNext(); ) {
                    cellNames.append(i.next());
                    if (i.hasNext()) {
                        cellNames.append(", ");
                    }
                }
                this.dragInfoLabel.setBounds(ghostX, ghostY - 22, 280, 18);
                dragInfoLabel.setText(" cannot move fixed cells: \n" + cellNames);
                this.dragInfoLabel.setVisible(true);
                return;
            }

            System.out.println("startDragPoint 2 = " + compRef.getRelationEditor().startDragPoint);

            boolean isIgnorableMove = compRef.getRelationEditor().startDragPoint.distance(eventX, eventY) < RelationEditor.ACCIDENTAL_MOVE_DISTANCE;
            if (isIgnorableMove) {
                this.dragInfoLabel.setBounds(ghostX, ghostY - 22, 280, 18);
                if (isCopyMode) {
                    dragInfoLabel.setText(" COPYING: to cancel dragging, drop on this cell.");
                } else {
                    dragInfoLabel.setText(" MOVING: to cancel dragging, drop on this cell.");
                }
                this.dragInfoLabel.setVisible(true);
                return;
            }
        }

        String dragMsg = sb.toString();
        if (! dragInfoLabel.getText().equals(dragMsg)) {
            dragInfoLabel.setText(dragMsg);
        }
        this.dragInfoLabel.setVisible(true);
    }

    public void moveGhostBar(int x, int y) {
        this.draggedBar.setLocation(x, y);
    }

    public void moveGhostCell(int x, int y, boolean isControlDown) {
        this.draggedBar.setLocation(x, y);
        this.multiDraggedBar.setLocation(x + 5, y + 5);
    }

    public void hideGhostBar() {
        this.draggedBar.setVisible(false);
        this.multiDraggedBar.setVisible(false);
        this.dragInfoLabel.setVisible(false);
    }

    /** show insertion marker whose center is located at the given insertionY */
    public void showBarInsertionMarker(int insertionY) {
        int INSERTION_MARKER_LEFT_MARGIN = UIUtil.BAR_LEFT_MARGIN / 2;
        int INSERTION_MARKER_HEIGHT = 4;

        JLayeredPane layeredPane = (JLayeredPane) compRef.getRelationEditor().getParent();
        int INSERTION_MARKER_WIDTH = Math.max(layeredPane.getBounds().width, UIUtil.TOOL_PANEL_WIDTH);

        this.insertionMarker.setBounds(INSERTION_MARKER_LEFT_MARGIN, insertionY - INSERTION_MARKER_HEIGHT / 2, INSERTION_MARKER_WIDTH, INSERTION_MARKER_HEIGHT);
        if (! this.insertionMarker.isVisible()) {
            this.insertionMarker.setVisible(true);
        }
    }

    /** show insertion marker whose center is located at the given insertionY */
    public void showTestMarker(int x, int y) {
//        this.testMarker.setBounds(x - 1, y - 1, 3, 3);
//        if (! this.testMarker.isVisible()) {
//            this.testMarker.setVisible(true);
//        }
    }

    public synchronized void showSampleMarker(int[] x, int[] y) {
        //hideSampleMarker();

        for (int i = 0; i < x.length; i++) {
            JLabel sampleMarker = new JLabel();
            sampleMarker.setVisible(false);
            sampleMarker.setBackground(Color.GREEN);
            sampleMarker.setOpaque(true);

            layeredPane.add(sampleMarker, new Integer(7));

            sampleMarker.setBounds(x [i] - 1, y [i] - 1, 3, 3);
            if (! sampleMarker.isVisible()) {
                sampleMarker.setVisible(true);
            }

            sampleMarkerList.add(sampleMarker);
        }
    }

    public void hideTestMarker() {
//        this.testMarker.setVisible(false);
    }

    public synchronized void hideSampleMarker() {
        for (Iterator i = sampleMarkerList.iterator(); i.hasNext();) {
            JLabel sampleMarker = (JLabel) i.next();
            sampleMarker.setVisible(false);
            layeredPane.remove(sampleMarker);
        }
        layeredPane.revalidate();
        sampleMarkerList.clear();
    }

    /** show insertion marker whose center is located at the given insertionY */
    public void showCellInsertionMarker(int insertionX, int insertionY, int cellHeight) {
        int INSERTION_MARKER_HEIGHT = cellHeight;
        int INSERTION_MARKER_WIDTH = 3;

        this.insertionMarker.setBounds(insertionX - INSERTION_MARKER_WIDTH / 2, insertionY - INSERTION_MARKER_HEIGHT / 2, INSERTION_MARKER_WIDTH, INSERTION_MARKER_HEIGHT);
        if (! this.insertionMarker.isVisible()) {
            this.insertionMarker.setVisible(true);
        }
    }

    public void hideInsertionMarker() {
        this.insertionMarker.setVisible(false);
    }

    /** show code completion for scriptEditorPane. code completion hints can be acquired from selectedCellForScriptEditing */
    public void showCellCompletion() {
        ccPopup.setCandidates(populateCandidates(), isNarrowingCodeCompletion);
        ccPopup.setVisible(true);

        Point scriptEditorPos = UIUtil.calculateScriptEditorPosition(getEditedScriptCell());
        scriptEditorPos.setLocation(scriptEditorPos.x - 1, scriptEditorPos.y + ccPopup.HEIGHT_PER_ITEM + 2);

        /* pick right edige of the caret selection */
        Point caretPos = UIUtil.getCaretPosition("right", scriptEditor.getScriptEditorPane(), ComponentReference.getGraphics());
        //Point caretPos = scriptEditor.getScriptEditorPane().getCaret().getMagicCaretPosition();
        if (caretPos == null) {
            caretPos = new Point(0, 0);
        }

//        /* if txtPane's width is not wide enough to display cc popup, shift cc popup to left */
        int ccPopupX = scriptEditorPos.x + caretPos.x;
//        if (ccPopupX + ccPopup.POPUP_WIDTH > scrollPaneWidth && (scrollPaneWidth - ccPopup.POPUP_WIDTH) >= 0) {
//            ccPopupX = scrollPaneWidth - ccPopup.POPUP_WIDTH;
//        }

        if (ccPopup.getCropping() != null && ccPopup.hasSuggestion()) {
            FontMetrics fontMetrics = ComponentReference.getGraphics().getFontMetrics(ccPopup.POPUP_FONT);
            Rectangle2D rec = fontMetrics.getStringBounds(ccPopup.getCropping(), ComponentReference.getGraphics());
            ccPopupX -= (int) rec.getWidth();
        }

        if (isNarrowingCodeCompletion) {
            /* when narrowing keep x position. it is becuase getMagicCaretPosition() does not return up-to-date value for current caret position */
            ccPopup.setBounds(ccPopup.getX(), scriptEditorPos.y + caretPos.y, ccPopup.POPUP_WIDTH, ccPopup.getPopupHeight());
        } else {
            ccPopup.setBounds(ccPopupX, scriptEditorPos.y + caretPos.y, ccPopup.POPUP_WIDTH, ccPopup.getPopupHeight());
        }

        //Point caretPos = scriptEditor.getScriptEditorPane().getCaret().getMagicCaretPosition();

        ccPopup.setScriptEditorPane(scriptEditor.getScriptEditorPane());
    }

    public java.util.List populateCandidates() {
        JTextPane textPane = scriptEditor.getScriptEditorPane();
        Caret caret = textPane.getCaret();

        if (caret.getDot() == caret.getMark()) {
            String query = createQueryForPatternBasedCodeCompletion();
            System.out.println("query: " + query);
            return createCandidateList(query);
        } else {
            try {
                String selectedText = textPane.getText(Math.min(caret.getMark(), caret.getDot()), Math.abs(caret.getMark() - caret.getDot()));
                return createCandidateList(selectedText);
            } catch (BadLocationException e) {
                e.printStackTrace();
                return java.util.Collections.EMPTY_LIST;
            }
        }
    }

    /**
     * create query used for pattern based code completion
     */
    private String createQueryForPatternBasedCodeCompletion() {
        return getEditedScriptCell().getParamName() + (scriptEditor.getText().startsWith("=") ? "" : "=") + scriptEditor.getText();
    }

    public java.util.List createCandidateList(String query) {
        java.util.List candidates = new ArrayList();
        CodeCompletion cc = new CodeCompletion(query);
        for (int i = cc.getCandidateCount() - 1; i >= 0; i--) {
            CodeCompletion.CandidateRow row = cc.getCandidateRow(i);
            System.out.println("row : " + row);
            /* if inconsistant, skip */ 
            if (row.getInconsistancyCount() > 0) {
                continue;
            }
            String codeString = row.getCodeString();
            //String candidate = codeString.substring((scriptEditor.getText().startsWith("=") ? codeString.indexOf("=") + 1 : codeString.indexOf("=")));
            String candidate = codeString.substring(codeString.indexOf("=") + 1);
            if (! "".equals(candidate.trim())) {
                candidates.add(new CodeCompletionCandidate(CodeCompletionCandidate.PATTERN_CC_TYPE, candidate, 0));
            }
        }
        return candidates;
    }

    /* relocate the currently shown script editor */
    public void relocateCurrentlyShownScriptEditor() {
        Point scriptEditorPos = UIUtil.calculateScriptEditorPosition(editedScriptCell);
        Dimension scriptEditorSize = editedScriptCell.getScriptEditorSize();
        scriptEditor.setBounds(scriptEditorPos.x, scriptEditorPos.y, scriptEditorSize.width, scriptEditorSize.height);
    }

    /** open script editing pane. set selectedCellForScriptEditing member variable so that code editing result can be stored, and code completion can make use of it */
    public void showScriptEditor(BaseCell scriptCell, int dotPosition) {
        this.editedScriptCell = scriptCell;

        Point scriptEditorPos = UIUtil.calculateScriptEditorPosition(scriptCell);
        Dimension scriptEditorSize = editedScriptCell.getScriptEditorSize();
        scriptEditor.setBounds(scriptEditorPos.x, scriptEditorPos.y, scriptEditorSize.width, scriptEditorSize.height);
        scriptEditor.setText(scriptCell.getScript());
        scriptEditor.setVisible(true);

        try {
            setMappabilityVisualization(scriptCell, true);
        } catch (Exception e) {
            //speedup Clog.debug("error occurred at setMappabilityVisualization(): " + e);
        }

        scriptEditor.requestFocusInWindow();

        /* when no dotPosition is found, set it to the end of text */
        if (dotPosition != -1) {
            scriptEditor.getScriptEditorPane().getCaret().setDot(dotPosition);
        }

        /* close code completion */
        closeCodeCompletion();
    }

    public void setMappabilityVisualization(BaseCell scriptCell, boolean showVisualization) {
        String relAlias = scriptCell.getBar().getRelAlias();
        String paramName = scriptCell.getParamName();
        String qualifiedName = relAlias + "." + paramName;
        CParameter param = compRef.getCurrentCNamingService().getParameter(qualifiedName);

        if (param == null) {
            throw new RuntimeException("invalid qualified name: " + qualifiedName);
        }

        if (showVisualization) {
            scriptCell.setDisabledIcon();
        } else {
            scriptCell.setNormalIcon();
        }

        Set drivens = null;
        if (param instanceof CRelationInputParameter) {
            drivens = ((CRelationInputParameter) param).getDrivensBy(false);
        } else if (param instanceof CInterfaceOutputParameter) {
            drivens = ((CInterfaceOutputParameter) param).getDrivensBy(false);
        }

        for (Iterator i = drivens.iterator(); i.hasNext();) {
            String drivenQualifiedName = (String) i.next();
            String drivenParamName = CNamingService.convertToLocal(drivenQualifiedName);
            String drivenRelAlias = drivenQualifiedName.substring(0, drivenQualifiedName.lastIndexOf("." + drivenParamName));
            if (! CConstant.ITF_ALIAS.equals(drivenRelAlias)) {
                RelationBar relBar = compRef.getRelationBar(drivenRelAlias);

                /* check the validity of relAlias */
                if (relBar == null) {
                    continue;
                }

                /* drivenCell can be either RelationInputCell or RelationOutputCell. */
                BaseCell drivenCell = relBar.getRelationInputCell(drivenParamName);
                if (drivenCell == null) {
                    drivenCell = relBar.getRelationOutputCell(drivenParamName);
                }

                /* check the validity of paramName */
                if (drivenCell == null) {
                    continue;
                }

                if (showVisualization) {
                    drivenCell.setDisabledIcon();
                } else {
                    drivenCell.setNormalIcon();
                }
            } else {
                InterfaceBar itfBar = compRef.getInterfaceBar();
                /* drivenCell can be either RelationInputCell or RelationOutputCell. */
                BaseCell drivenCell = itfBar.getInterfaceInputCell(drivenParamName);
                if (drivenCell == null) {
                    drivenCell = itfBar.getInterfaceOutputCell(drivenParamName);
                }

                if (showVisualization) {
                    drivenCell.setDisabledIcon();
                } else {
                    drivenCell.setNormalIcon();
                }
            }
        }
    }

    public void closeScriptEditor(boolean storeChange) {
        FloatingScriptEditor activeScriptEditor = getScriptEditor();

        if (activeScriptEditor == null || editedScriptCell == null || ! activeScriptEditor.isVisible()) {
            /* when it is not even created or there is no edited cell, we don't have to close it */
            return;
        }

        if (compRef.getImplementationEditor().isCodeCompletionShown()) {
            compRef.getImplementationEditor().closeCodeCompletion();
        }

        try {
            setMappabilityVisualization(editedScriptCell, false);
        } catch (Exception e) {
            //speedup Clog.debug("error occurred at setMappabilityVisualization(): " + e);
        }

        editedScriptCell.setScriptEditorSize(activeScriptEditor.getWidth(), activeScriptEditor.getHeight());
        editedScriptCell.getBar().lastEditedCellIndex = UIUtil.indexOfComponent(editedScriptCell);
        editedScriptCell.dotPosition = activeScriptEditor.getScriptEditorPane().getCaret().getDot();
        activeScriptEditor.setVisible(false);

        if (storeChange) {
            /* apply script editing result to the selected cell */
            boolean isReallyChanged = ! activeScriptEditor.getText().equals(editedScriptCell.getScript());
            if (isReallyChanged) {
                editedScriptCell.setScript(activeScriptEditor.getText());
                indexParameter(editedScriptCell.getBar().getRelAlias(), editedScriptCell.getParamName());
                compRef.markUnsavedChanges();

                /* refresh evaluation context and get evaluation mode ready */
                compRef.getCurrentEvaluationContext().refresh();
                EvaluationMode.resetEditorPanes(compRef);
            }
        }

        compRef.getImplementationEditor().requestFocusInWindow();
    }

    public boolean isScriptEditorShown() {
        return (scriptEditor != null) && this.scriptEditor.isVisible();
    }

    public FloatingScriptEditor getScriptEditor() {
        return scriptEditor;
    }

    public BaseCell getEditedScriptCell() {
        return editedScriptCell;
    }

    public CellCompletionPopup getCodeCompletionPanel() {
        return ccPopup;
    }

    public boolean isImplLoaded() {
        return isImplLoaded;
    }

    public void setImplLoaded(boolean isLoaded) {
        this.isImplLoaded = isLoaded;
    }

    /** returns currently opened implmenetation name */
    public String getImplementationName() {
        return implementationName;
    }

    /** returns currently opened interface name */
    public String getInterfaceName() {
        return interfaceName;
    }

    /** should be invoked when interfaceName changes */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /** should be invoked when implementation changes */
    public void setImplementationName(String implementationName) {
        this.implementationName = implementationName;
    }

    /** load an implementation of a given model's interface */
    public void displayImplementation(CModel model, String interfaceName, String implementationName) {
        /* clear previously displayed impl */
        closeCodeCompletion();
        closeScriptEditor(true);

        if (interfaceEditor.getInterfaceBar() != null) {
            String currentKey = this.interfaceName + "|" + this.implementationName; // current interface name and implemenation name are used to generate the cache key
            compRef.getModelEditor().getInterfaceBarMap().put(currentKey, interfaceEditor.getInterfaceBar()); // cache an instance of InterfaceBar
            compRef.getModelEditor().getRelationBarsMap().put(currentKey, relationEditor.getRelationBarList()); // cache an array of RelationBars
            //speedup Clog.debug("caching: " + compRef.getModelEditor().getInterfaceBarMap() + " / " + relationEditor.getRelationBarList());
        }

        interfaceEditor.clearInterfaceBar();
        relationEditor.clearAllRelationBars();

        /* start displaying impl */
        this.isImplLoaded = true;
        this.interfaceName = interfaceName;
        this.implementationName = implementationName;

        String newKey = interfaceName + "|" + implementationName;
        InterfaceBar interfaceBar = (InterfaceBar) compRef.getModelEditor().getInterfaceBarMap().get(newKey);
        List relationBarList = (List) compRef.getModelEditor().getRelationBarsMap().get(newKey);

        //speedup Clog.debug("retrieving: " + compRef.getModelEditor().getInterfaceBarMap() + " / " + interfaceBar);

        if (interfaceBar != null && relationBarList != null) {
            /* constraints for cells and bars are updated */
            interfaceEditor.restoreInterfaceBar(interfaceBar);
            relationEditor.restoreRelationBars(relationBarList);

            /* the inner layout of cells is updated */
            UIUtil.updateCellAndBarLayout(compRef);
            UIUtil.updateEditorBounds(compRef);
        } else {
            CImplementation impl = model.getInterface(interfaceName).getImplementation(implementationName);
            CNamingService namingService = impl.getNamingService();

            List inputParamNames = impl.getInputParameterNames();
            List outputParamNames = impl.getOutputParameterNames();
            interfaceEditor.setInterfaceBar(interfaceName);
            for (Iterator i = inputParamNames.iterator(); i.hasNext(); ) {
                String paramName = (String) i.next();
                CInterfaceInputParameter param = namingService.getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + paramName);
                compRef.getInterfaceBar().addInterfaceInputCell(param.getName(), param.getDataType(), param.getUnit());
            }

            for (Iterator i = outputParamNames.iterator(); i.hasNext(); ) {
                String paramName = (String) i.next();
                CInterfaceOutputParameter param = namingService.getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + paramName);
                compRef.getInterfaceBar().addInterfaceOutputCell(param.getName(), param.getDataType(), param.getUnit(), param.getMapping().getMappingScript());
            }

            List relationAliases = impl.getRelationAliases();

            for (Iterator i = relationAliases.iterator(); i.hasNext(); ) {
                String relationAlias = (String) i.next();
                CRelation relation = namingService.getRelation(relationAlias);
                relationEditor.displayRelation(relation);
            }
        }

        if (compRef.getModelEditor().isEvaluationMode()) {
            EvaluationMode.desaturateRelationBarColor(compRef);
        } else {
            EvaluationMode.saturateRelationBarColor(compRef);
        }


        EvaluationMode.initEditorPanes(compRef);
//        if (compRef.getCurrentEvaluationContext().isBeforeInitialization()) {
//            EvaluationMode.initEditorPanes(compRef);
//        } else {
//            EvaluationMode.initEditorPanes(compRef);
//            //EvaluationMode.updateEditorPanes(compRef);
//        }
    }

    /** move selected cells into a target bar idx */
    protected void moveSelectedCells(BaseCell[] cells, int targetBarIdx, boolean copyMode, boolean copyWithMapping) {
        /* start setting up barAliasToCellListMap (kept) & targetParamNameList & paramNameToCellMap (moved) & renameMap (old name of moved -> new name of moved) */
        BaseBar targetBar = null;
        if (targetBarIdx == -1) {
            targetBar = compRef.getInterfaceBar();
        } else {
            targetBar = compRef.getRelationBar(targetBarIdx);
        }
        BarSidePanel targetPanel = null;
        BarSidePanel targetSiblingPanel = null;
        boolean targetOnTheLeft = compRef.getRelationEditor().insertionIntoLeftPanel;
        if (targetOnTheLeft) {
            targetPanel = targetBar.getLeftPanel();
            targetSiblingPanel = targetBar.getRightPanel();
        } else {
            targetPanel = targetBar.getRightPanel();
            targetSiblingPanel = targetBar.getLeftPanel();
        }

        Map relAliasToCellListMap = new HashMap();
        Map qualifiedParamNameToCellMap = new HashMap();
        List targetParamNameList = new ArrayList();
        List targetParamSideList = new ArrayList();
        Map renameMap = new HashMap();
        List qualifiedParamNames = new ArrayList();

        List targetKey = Arrays.asList(new Object[] { targetBar.getRelAlias(), Boolean.valueOf(targetOnTheLeft) });
        List targetCellList = new ArrayList();
        targetCellList.addAll(Arrays.asList(targetPanel.getComponents()));
        relAliasToCellListMap.put(targetKey, targetCellList);

        /* populate qualifiedParamNameToCellMap */
        for (int i = 0; i < cells.length; i++) {
            BaseBar bar = cells [i].getBar();
            qualifiedParamNameToCellMap.put(bar.getRelAlias() + "." + cells [i].getParamName(), cells [i]);
            qualifiedParamNames.add(bar.getRelAlias() + "." + cells [i].getParamName());
        }

        /* populate targetParamNameList containing param names of target panel */
        for (int i = 0; i < targetPanel.getComponentCount(); i++) {
            BaseCell cell = (BaseCell) targetPanel.getComponent(i);
            targetParamNameList.add(targetBar.getRelAlias() + "." + cell.getParamName());
            targetParamSideList.add(Boolean.valueOf(cell.isOnLeftPanel()));
        }

        /* populate existingLocalParamNameSet containing all param names of the target relation */
        Set existingLocalParamNameSet = CNamingService.convertToLocal(new HashSet(targetParamNameList));
        for (int i = 0; i < targetSiblingPanel.getComponentCount(); i++) {
            BaseCell cell = (BaseCell) targetSiblingPanel.getComponent(i);
            if (! cell.isSelected() || copyMode) { //todo: when two is
                existingLocalParamNameSet.add(cell.getParamName());
            }
        }

        System.out.println("existingLocalParamNameSet = " + existingLocalParamNameSet);

        int numberOfRearrangedCells = 0;
        for (int i = 0; i < cells.length; i++) {
            BaseBar bar = cells [i].getBar();
            String relAlias = bar.getRelAlias();
            String paramName = cells [i].getParamName();
            boolean isLeft = cells [i].isOnLeftPanel();
            List key = Arrays.asList(new Object[] { relAlias, Boolean.valueOf(isLeft) });
            String movedParamName = relAlias + "." + paramName;

            /* adjust targetParamNameList when some cells in the target bar are selected, they are first removed from targetParamNameList and then re-inserted at their insertion index. cellInsertionIdx is  */
            if (targetParamNameList.contains(movedParamName) && ! copyMode) {
                int movedParamNameIdx = targetParamNameList.indexOf(movedParamName);
                targetParamNameList.remove(movedParamNameIdx);
                targetParamSideList.remove(movedParamNameIdx);
                existingLocalParamNameSet.remove(CNamingService.convertToLocal(movedParamName));

                /* if the index of the removed param is in front of cell insertion idx, cellInsertionIdx is decreased by 1 */
                if (movedParamNameIdx < compRef.getRelationEditor().cellInsertionIdx) {
                    numberOfRearrangedCells++;
                }
            } else if (existingLocalParamNameSet.contains(paramName)) { // paramName is a local param name of the moved param
                String uniqueLocalName = getUniqueName(paramName, existingLocalParamNameSet, renameMap);
                Integer renameIdx = new Integer(compRef.getRelationEditor().cellInsertionIdx - numberOfRearrangedCells + i);
                List renameKey = Arrays.asList(new Object[] { movedParamName, renameIdx });
//                System.out.println("add renameMap entry : { movedParamName, renameIdx } = " + movedParamName + ", " + renameIdx + " = " + uniqueLocalName);
                renameMap.put(renameKey, uniqueLocalName); // we can use this map to find out the source param of a given unique name
            }

            if ((compRef.getRelationEditor().cellInsertionIdx - numberOfRearrangedCells + i) == targetParamNameList.size()) {
                targetParamNameList.add(movedParamName);
                targetParamSideList.add(Boolean.valueOf(isLeft));
                existingLocalParamNameSet.add(CNamingService.convertToLocal(movedParamName));
            } else {
                targetParamNameList.add((compRef.getRelationEditor().cellInsertionIdx - numberOfRearrangedCells + i), movedParamName);
                targetParamSideList.add((compRef.getRelationEditor().cellInsertionIdx - numberOfRearrangedCells + i), Boolean.valueOf(isLeft));
                existingLocalParamNameSet.add(CNamingService.convertToLocal(movedParamName));
            }

            /* populate barAliasToCellListMap */
            List cellList = (List) relAliasToCellListMap.get(key);
            if (cellList == null) {
                cellList = new ArrayList();
                BarSidePanel sidePanel = null;
                if (isLeft) {
                    sidePanel = bar.getLeftPanel();
                } else {
                    sidePanel = bar.getRightPanel();
                }
                Component[] comps = sidePanel.getComponents();
                cellList.addAll(Arrays.asList(comps));
                if (! copyMode) {
                    cellList.remove(cells [i]);
                }
                relAliasToCellListMap.put(key, cellList);
            } else {
                if (! copyMode) {
                    cellList.remove(cells [i]);
                }
            }
        }
        /* finish setting up barAliasToCellListMap (kept) & targetParamNameList & paramNameToCellMap (moved) */

        /* update CModel object - move parameters */
        compRef.getCurrentCNamingService().moveParameters(qualifiedParamNames, targetBar.getRelAlias(), targetOnTheLeft, compRef.getRelationEditor().cellInsertionIdx, renameMap, copyMode, copyWithMapping);

        targetPanel.removeAll();
        for (int i = 0; i < targetParamNameList.size(); i++) {
            String originParamName = (String) targetParamNameList.get(i);

            Integer uniqueLocalNameIdx = new Integer(i);
            List renameKey = Arrays.asList(new Object[] { originParamName, uniqueLocalNameIdx });
            String uniqueLocalName = (String) renameMap.get(renameKey);
            if (uniqueLocalName == null) {
                uniqueLocalName = CNamingService.convertToLocal(originParamName);
            }

            CParameter targetParam = compRef.getCurrentCNamingService().getParameter(targetBar.getRelAlias() + "." + uniqueLocalName);
            BaseCell targetCell = null;

            /* try reusing cell if origin param is in the same relation */
            String originRelAlias = CNamingService.parseRelAlias(originParamName);
            String originLocalName = CNamingService.convertToLocal(originParamName);

            boolean originOnLeft = ((Boolean) targetParamSideList.get(i)).booleanValue();

            if (originRelAlias.equals(targetBar.getRelAlias()) && targetOnTheLeft == originOnLeft) {
                targetCell = findReusableCell(originParamName, targetOnTheLeft, qualifiedParamNameToCellMap, relAliasToCellListMap);

                if (targetCell != null) {
                    if (targetCell.getParent() == null) {
                        targetCell.setParamName(uniqueLocalName);
                    } else {
                        targetCell = null;
                    }
                }
            }

            if (targetCell == null) {
                if (targetOnTheLeft) {
                    if (CConstant.ITF_ALIAS.equals(targetBar.getRelAlias())) {
                        targetCell = new InterfaceInputCell(targetBar.getRelAlias(), uniqueLocalName, targetParam.getDataType(), targetParam.getUnit(), compRef);
                        /* when the origin is relation input cell, */
                        if (! CConstant.ITF_ALIAS.equals(originRelAlias) && originOnLeft) {
                            CRelationInputParameter originParam = compRef.getCurrentCNamingService().getRelationInputParameter(originParamName);
                            /* if the current mapping script is empty or the same as the default value, set the cell's mapping script could have been updated by CNamingService.moveParameters(). so we reload the mapping script of CParameter */
                            compRef.getRelationInputCell(originRelAlias, originLocalName).setScript(originParam.getMapping().getMappingScript());
                            indexParameter(originRelAlias, originLocalName);
                        }
                    } else {
                        targetCell = new RelationInputCell(targetBar.getRelAlias(), uniqueLocalName, targetParam.getDataType(), targetParam.getUnit(), "", compRef);
                        targetCell.setScript(((CRelationInputParameter) targetParam).getMapping().getMappingScript());
                        indexParameter(targetBar.getRelAlias(), targetCell.getParamName());
                    }
                } else {
                    if (CConstant.ITF_ALIAS.equals(targetBar.getRelAlias())) {
                        targetCell = new InterfaceOutputCell(targetBar.getRelAlias(), uniqueLocalName, targetParam.getDataType(), targetParam.getUnit(), "", compRef);
                        targetCell.setScript(((CInterfaceOutputParameter) targetParam).getMapping().getMappingScript());
                        indexParameter(targetBar.getRelAlias(), targetCell.getParamName());
                    } else {
                        targetCell = new RelationOutputCell(targetBar.getRelAlias(), uniqueLocalName, targetParam.getDataType(), targetParam.getUnit(), compRef);
                        /* when the origin is interface output cell, */
                        if (CConstant.ITF_ALIAS.equals(originRelAlias) && ! originOnLeft) {
                            CInterfaceOutputParameter originParam = compRef.getCurrentCNamingService().getInterfaceOutputParameter(originParamName);
                            /* if the current mapping script is empty or the same as the default value, set the cell's mapping script could have been updated by CNamingService.moveParameters(). so we reload the mapping script of CParameter */
                            compRef.getInterfaceOutputCell(originLocalName).setScript(originParam.getMapping().getMappingScript());
                            indexParameter(originRelAlias, originLocalName);
                        }
                    }
                }
            }
            targetPanel.add(targetCell);
        }
        targetPanel.updateLayoutConstraints();
        targetPanel.invalidate();
        targetBar.revalidate();
        targetBar.repaint();

        for (Iterator i = relAliasToCellListMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            List key = (List) entry.getKey();
            String relAlias = (String) key.get(0);
            boolean isLeft = ((Boolean) key.get(1)).booleanValue();

            /* we already have added cells for target bar, so skip onto the next. */
            if (relAlias.equals(targetBar.getRelAlias()) && targetOnTheLeft == isLeft) {
                continue;
            }

            BaseBar bar = null;
            if (CConstant.ITF_ALIAS.equals(relAlias)) {
                bar = compRef.getInterfaceBar();
            } else {
                bar = compRef.getRelationBar(relAlias);
            }

            BarSidePanel panel = null;
            if (isLeft) {
                panel = bar.getLeftPanel();
            } else {
                panel = bar.getRightPanel();
            }
            panel.removeAll();

            List cellList = (List) entry.getValue();
            for (int j = 0; j < cellList.size(); j++) {
                panel.add((BaseCell) cellList.get(j));
            }

            panel.updateLayoutConstraints();
            panel.invalidate();
            bar.revalidate();
            bar.repaint();
        }

        /* temporarily highlight the change just made */
        compRef.clearBarAndCellSelection();
        for (int i = 0; i < cells.length; i++) {
            ((BaseCell) targetPanel.getComponent(compRef.getRelationEditor().cellInsertionIdx - numberOfRearrangedCells + i)).setSelected(true);
        }

        compRef.getCurrentEvaluationContext().refresh();
        EvaluationMode.initEditorPanes(compRef);
        
        UIUtil.updateEditorBounds(compRef);
    }

    private static BaseCell findReusableCell(String qualifiedParamName, boolean isLeft, Map qualifiedParamNameToCellMap, Map relAliasToCellListMap) {
        /* a case when moving a param in the current bar */
        BaseCell foundCell = (BaseCell) qualifiedParamNameToCellMap.get(qualifiedParamName);

        /* a case when keeping a param in the current bar */
        if (foundCell == null) {
            String relAlias = CNamingService.parseRelAlias(qualifiedParamName);
            List relAliasKey = Arrays.asList(new Object[] { relAlias, Boolean.valueOf(isLeft) });
            foundCell = findCellWithParamName(qualifiedParamName, (List) relAliasToCellListMap.get(relAliasKey));
        }
        return foundCell;
    }

    private static BaseCell findCellWithParamName(String qualifiedParamName, List cellList) {
        String paramName = CNamingService.convertToLocal(qualifiedParamName);
        for (int i = 0; i < cellList.size(); i++) {
            BaseCell cell = (BaseCell) cellList.get(i);
            if (cell.getParamName().equals(paramName)) {
                return cell;
            }
        }
        throw new RuntimeException("given param name " + paramName + " is not in the list");
    }

    private static String getUniqueName(String originParamName, Set existingLocalNameSet, Map renameMap) {
        System.out.println("originParamName = " + originParamName + " , existingParamNames = " + existingLocalNameSet);
        boolean isUnique = false;
        int idx = 0;
        String rootPart = originParamName;
        Pattern nameExtractPt = Pattern.compile("(.*)_(\\d+)");
        Matcher nameExtractMt = nameExtractPt.matcher(originParamName);
        if (nameExtractMt.find()) {
            rootPart = nameExtractMt.group(1);
            idx = Integer.parseInt(nameExtractMt.group(2));
        }
        while (! isUnique) {
            String tryThisName = rootPart + "_" + (++idx);
            if (! existingLocalNameSet.contains(tryThisName) && ! renameMap.values().contains(tryThisName)) {
                isUnique = true;
                return tryThisName;
            }
        }
        return null; // never reach here
    }


    public RelationEditor getRelationEditor() {
        return relationEditor;
    }

    public InterfaceEditor getInterfaceEditor() {
        return interfaceEditor;
    }

    public RelationToolPanel getRelationToolBar() {
        return relationToolBar;
    }

    public boolean isCellWrapEnabled() {
        return isCellWrapEnabled;
    }

    public void setCellWrapEnabled(boolean cellWrapEnabled) {
        isCellWrapEnabled = cellWrapEnabled;
    }

    public void setDragTargetBarIndex(int dragTargetBarIdx) {
        this.dragTargetBarIdx = dragTargetBarIdx;
    }

    public int getDragTargetBarIndex() {
        return dragTargetBarIdx;
    }

    public void setDragSourceBarIndex(int dragSourceBarIdx) {
        this.dragSourceBarIdx = dragSourceBarIdx;
    }

    public int getDragSourceBarIndex() {
        return dragSourceBarIdx;
    }

//    public void paint(Graphics g) {
//        super.paint(g);
//        if (this.graphics == null) {
//            this.graphics = g;
//        }
//        if (ccPopup.HEIGHT_PER_ITEM == -1) {
//            ccPopup.HEIGHT_PER_ITEM = g.getFontMetrics(ccPopup.POPUP_FONT).getHeight();
//        }
//
//    }

}
