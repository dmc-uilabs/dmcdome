package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CMapping;
import mit.cadlab.dome3.plugin.catalog.core.CRelation;
import mit.cadlab.dome3.plugin.catalog.core.CLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 1.
 */
public class ModelEditorKit {
    public static final BaseAction NewModelAction = new NewModelAction();
    public static final BaseAction EditModelAction = new EditModelAction();
    public static final BaseAction OpenAction = new OpenAction();
    public static final BaseAction SaveAction = new SaveAction();
    public static final BaseAction SaveAsAction = new SaveAsAction();
    public static final BaseAction CloseAction = new CloseAction();
    public static final BaseAction ExitAction = new ExitAction();
    public static final BaseAction AddRelAction = new AddRelationAction();
    public static final BaseAction DeleteRelationOrCellAction = new DeleteRelationOrCellAction();
    public static final BaseAction EditRelationAction = new EditRelationAction();
    public static final BaseAction EditInterfaceAction = new EditInterfaceAction();

    public static final BaseAction MoveDownRelationAction = new MoveDownRelationAction();
    public static final BaseAction MoveUpRelationAction = new MoveUpRelationAction();
    public static final BaseAction MoveDownSelectionCursorAction = new MoveDownSelectionCursorAction();
    public static final BaseAction MoveUpSelectionCursorAction = new MoveUpSelectionCursorAction();
    public static final BaseAction MoveLeftSelectionCursorAction = new MoveLeftSelectionCursorAction();
    public static final BaseAction MoveRightSelectionCursorAction = new MoveRightSelectionCursorAction();

    public static final BaseAction OpenMappingScriptEditorAction = new OpenMappingScriptEditorAction();

    public static final BaseAction ExpandUpwardRelationAction = new ExpandUpwardRelationAction();
    public static final BaseAction ExpandDownwardRelationAction = new ExpandDownwardRelationAction();
    public static final BaseAction SelectAllRelationsAction = new SelectAllRelationsAction();
    public static final BaseAction UnselectAllRelationsAction = new UnselectAllRelationsAction();

    public static final BaseAction CopyOptionPopupMenu1Action = new CopyOptionPopupMenuAction(CopyOptionPopupMenuAction.MAP_OPTION, KeyEvent.VK_M);
    public static final BaseAction CopyOptionPopupMenu2Action = new CopyOptionPopupMenuAction(CopyOptionPopupMenuAction.COPY_OPTION, KeyEvent.VK_C);
    public static final BaseAction CopyOptionPopupMenu3Action = new CopyOptionPopupMenuAction(CopyOptionPopupMenuAction.MOVE_OPTION, KeyEvent.VK_V);
    public static final BaseAction CopyOptionPopupMenu4Action = new CopyOptionPopupMenuAction(CopyOptionPopupMenuAction.CANCEL_OPTION, KeyEvent.VK_E);

    public static final BaseAction AddImplAction = new AddImplAction();
//    public static final BaseAction RemoveImplAction = new RemoveImplAction();
    public static final BaseAction AddItfAction = new AddItfAction();
//    public static final BaseAction RemoveItfAction = new RemoveItfAction();
    public static final BaseAction RemoveImplOrItfAction = new RemoveImplOrItfAction();
    public static final BaseAction ExecuteImplAction = new ExecuteImplAction();
    public static final BaseAction EditCellConfigAction = new EditCellConfigAction();

    public static final BaseAction SwitchEvaluationModeAction = new SwitchEvaluationModeAction();
    public static final BaseAction RunEvaluationAction = new RunEvaluationAction();
    public static final BaseAction StopEvaluationAction = new StopEvaluationAction();

    public static final BaseAction SwitchCellWrapAction = new SwitchCellWrapAction();
    public static final BaseAction SwitchNavigationPanelAction = new SwitchNavigationPanelAction();
    public static final BaseAction UseLargerCellAction = new UseLargerCellAction();
    public static final BaseAction UseSmallerCellAction = new UseSmallerCellAction();
    public static final BaseAction SwitchTypeAndUnitQueryModeAction = new SwitchTypeAndUnitQueryModeAction();
    public static final BaseAction StartTypeAndUnitQueryModeAction = new StartTypeAndUnitQueryModeAction();
    public static final BaseAction ExitTypeAndUnitQueryModeAction = new ExitTypeAndUnitQueryModeAction();

    public static final BaseAction SwitchDebugConsoleAction = new SwitchDebugConsoleAction();

    public static ComponentReference compRef = null;

    static abstract class BaseAction extends AbstractAction {
        protected ComponentReference compRef;
        public BaseAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(ACCELERATOR_KEY, accelerator);
        }
        public void setComponentReference(ComponentReference compRef) {
            this.compRef = compRef;
        }
        abstract public void actionPerformed(ActionEvent e);
    }

    static class NewModelAction extends BaseAction {
        public NewModelAction() {
            super("New Model...", UIUtil.createImageIcon("images/New.gif"), "create a new model", new Integer(KeyEvent.VK_N), KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().createNewModel();
        }
    }

    static class EditModelAction extends BaseAction {
        public EditModelAction() {
            super("Edit Model Name and Description...", null, "edit model name and description", null, null);
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().editModel();
        }
    }

    static class OpenAction extends BaseAction {

        public OpenAction() {
            super("Open...", UIUtil.createImageIcon("images/Open.gif"), "open a new catalog model", new Integer(KeyEvent.VK_O), KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().openModelFromFile();
        }
    }

    static class SaveAction extends BaseAction {
        public SaveAction() {
            super("Save", UIUtil.createImageIcon("images/Save.gif"), "save the current model to disk", new Integer(KeyEvent.VK_S), KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getImplementationEditor().closeScriptEditor(true);
            if (compRef.getModelEditor().getFilePath() == null) {
                compRef.getModelEditor().saveModelAsNewFile();
            } else {
                compRef.getModelEditor().saveModelAsCurrentFile();
            }
        }
    }

    static class SaveAsAction extends BaseAction {
        public SaveAsAction() {
            super("Save As...", UIUtil.createImageIcon("images/SaveAs.gif"), "save the current model as a different name", new Integer(KeyEvent.VK_A), KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().saveModelAsNewFile();
        }
    }



    static class CloseAction extends BaseAction {
        public CloseAction() {
            super("Close", UIUtil.createImageIcon("images/Close.gif"), "close the current model", new Integer(KeyEvent.VK_C), null);
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().closeModel();
        }
    }

    static class ExitAction extends BaseAction {
        public ExitAction() {
            super("Exit", null, "quit catalog model editor", new Integer(KeyEvent.VK_X), KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().quitEditor();
        }
    }

    static class AddRelationAction extends BaseAction {
        public AddRelationAction() {
            super("Add Relation...", UIUtil.createImageIcon("images/AddRelation.gif"), "add relation", new Integer(KeyEvent.VK_A), KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getImplementationEditor().closeScriptEditor(true);
            compRef.getImplementationEditor().getRelationEditor().addRelation();
        }
    }

    static class DeleteRelationOrCellAction extends BaseAction {
        public DeleteRelationOrCellAction() {
            super("Delete Selected Relations or Cells...", UIUtil.createImageIcon("images/RemoveRelation.gif"), "delete selected relations or cells", new Integer(KeyEvent.VK_D), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getRelationEditor().removeRelationOrCell();
        }
    }

    static class EditInterfaceAction extends BaseAction {
        public EditInterfaceAction() {
            super("Edit Interface...", UIUtil.createImageIcon("images/Draw.gif"), "edit interface", new Integer(KeyEvent.VK_I), null);
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getInterfaceEditor().editInterface();
        }
    }

    static class EditRelationAction extends BaseAction {
        public EditRelationAction() {
            super("Edit Selected Relation...", UIUtil.createImageIcon("images/EditRelation.gif"), "edit selected relation", new Integer(KeyEvent.VK_E), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        }

        public void actionPerformed(ActionEvent e) {
            if (compRef.getInterfaceBar().isSelected()) {
                /* edit interface bar */
                compRef.getInterfaceEditor().editInterface();
                return;
            }

            RelationBar[] selectedBars = compRef.getSelectedRelationBars();

            if (selectedBars.length == 0) {
                Object[] options = { "OK" };
                JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(compRef.getInterfaceBar()), "Please select a relation to be modified by clicking on its name.", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                return;
            } else if (selectedBars.length > 1) {
                Object[] options = { "OK" };
                JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(compRef.getInterfaceBar()), "Multiple relations are selected. Please select one relation to be modified.", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                return;
            } else {
                /* edit first selected bar */
                compRef.getRelationEditor().editRelation(selectedBars[0]);
            }
        }
    }

    static class MoveDownRelationAction extends BaseAction {
        public MoveDownRelationAction() {
            super("Move Down Selected Relation", UIUtil.createImageIcon("images/MoveDownRelation.gif"), "move down selected relation(s)", new Integer(KeyEvent.VK_E), KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if (compRef.getSelectedRelationBars().length == 0) {
                Object[] options = { "OK" };
                JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(compRef.getInterfaceBar()), "No relation is selected. Please select a relation to be moved by clicking on its name.", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                return;
            }

            if (! compRef.getInterfaceBar().isSelected()) {
                compRef.getRelationEditor().moveDownSelectedRelation();
                compRef.getRelationEditor().updateLayoutConstraints();
                UIUtil.updateEditorBounds(compRef);
            }
        }
    }

    static class MoveUpRelationAction extends BaseAction {
        public MoveUpRelationAction() {
            super("Move Up Selected Relation", UIUtil.createImageIcon("images/MoveUpRelation.gif"), "move up selected relation(s)", new Integer(KeyEvent.VK_E), KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if (compRef.getSelectedRelationBars().length == 0) {
                Object[] options = { "OK" };
                JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(compRef.getInterfaceBar()), "No relation is selected. Please select a relation to be moved by clicking on its name.", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                return;
            }

            if (! compRef.getInterfaceBar().isSelected()) {
                compRef.getRelationEditor().moveUpSelectedRelation();
                compRef.getRelationEditor().updateLayoutConstraints();
                UIUtil.updateEditorBounds(compRef);
            }
        }
    }

    static class MoveUpSelectionCursorAction extends BaseAction {
        public MoveUpSelectionCursorAction() {
            super("Move Up Selection Cursor", null, "move up selection cursor", new Integer(KeyEvent.VK_U), KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().stopExpandingSelection();
            int barCount = compRef.getRelationBarCount();
            Point barLocation = null;
            Dimension barSize = null;

            if (compRef.isAnyCellSelected()) {
                BaseCell cell = compRef.getSelectedCell();

                if (cell == null) {
                    return;
                }

                BaseBar bar = cell.getBar();
                int[] rowAndColIdx = cell.getRowAndColumnIndex();

                int initColIdx = rowAndColIdx [1];
                int newRowIdx = rowAndColIdx [0] - 1;
                int newColIdx = rowAndColIdx [1];

                boolean isLeftPanel = cell.isOnLeftPanel();
                /* move to a closest cell in the prev relation */
                while (! bar.getPanel(isLeftPanel).isValidCellIndex(newRowIdx, newColIdx)) {
                    bar = bar.getPreviousBar();
                    newColIdx = Math.min((bar.getPanel(isLeftPanel).getColumnCount() - 1), initColIdx);
                    newRowIdx = bar.getPanel(isLeftPanel).getRowCount() - 1;
                    if (! bar.getPanel(isLeftPanel).isValidCellIndex(newRowIdx, newColIdx) && bar.getPanel(isLeftPanel).isValidCellIndex(newRowIdx - 1, newColIdx)) {
                        newRowIdx = newRowIdx - 1;
                    }
                }
                bar.getPanel(isLeftPanel).getCell(newRowIdx, newColIdx).cellClicked(false);
                return;
            }

            if (! compRef.isAnyBarSelected()) {
                /* when nothing selected at the beginning */
                if (barCount > 0) {
                    compRef.getRelationBar(barCount - 1).setSelected(true);
                } else {
                    compRef.getInterfaceBar().setSelected(true);
                }
            } else if (compRef.getInterfaceBar().isSelected()) {
                /* when interface selected at the beginning */
                if (barCount > 0) {
                    compRef.clearBarAndCellSelection();
                    compRef.getRelationBar(barCount - 1).setSelected(true);
                }
            } else {
                /* when relations selected at the beginning */
                RelationBar[] selectedBars = compRef.getSelectedRelationBars();
                int currentIdx = UIUtil.indexOfComponent(selectedBars [0]);
                if (currentIdx > 0) {
                    compRef.clearBarAndCellSelection();
                    compRef.getRelationBar(currentIdx - 1).setSelected(true);
                } else {
                    compRef.clearBarAndCellSelection();
                    compRef.getInterfaceBar().setSelected(true);
                    //barLocation = compRef.getInterfaceBar().getLocation();
                    //UIUtil.getRelationBarOrigin(
                    barLocation = UIUtil.getInterfaceBarOrigin();
                    System.out.println("compRef.getInterfaceBar().getLocation(); = " + compRef.getInterfaceBar().getLocation());
                    System.out.println("UIUtil.getInterfaceBarOrigin(); = " + UIUtil.getInterfaceBarOrigin());
                    barSize = compRef.getInterfaceBar().getBarSize();
                    System.out.println("barLocation = " + barLocation);
                    System.out.println("barSize = " + barSize);

                    Rectangle rect = new Rectangle(barLocation.x, barLocation.y, barSize.width, barSize.height);
                    System.out.println(rect);

                    System.out.println("current view rect: " + compRef.getImplementationEditor().getScrollPane().getViewport().getViewRect());

                    System.out.println("is currently valid : " + compRef.getImplementationEditor().getScrollPane().getViewport().isValid());
                    compRef.getImplementationEditor().getScrollPane().getViewport().scrollRectToVisible(rect);
                    System.out.println("is updated valid : " + compRef.getImplementationEditor().getScrollPane().getViewport().isValid());

                    System.out.println("updated view rect: " + compRef.getImplementationEditor().getScrollPane().getViewport().getViewRect());
                    compRef.getImplementationEditor().getScrollPane().getViewport().validate();
                }
            }
        }
    }

    static class MoveDownSelectionCursorAction extends BaseAction {
        public MoveDownSelectionCursorAction() {
            super("Move Down Selection Cursor", null, "move down selection cursor", new Integer(KeyEvent.VK_U), KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().stopExpandingSelection();
            int barCount = compRef.getRelationBarCount();

            if (compRef.isAnyCellSelected()) {
                BaseCell cell = compRef.getSelectedCell();

                if (cell == null) {
                    return;
                }

                BaseBar bar = cell.getBar();
                int[] rowAndColIdx = cell.getRowAndColumnIndex();

                int initColIdx = rowAndColIdx [1];
                int newRowIdx = rowAndColIdx [0] + 1;
                int newColIdx = rowAndColIdx [1];

                boolean isLeftPanel = cell.isOnLeftPanel();
                /* move to a closest cell in the prev relation */
                while (! bar.getPanel(isLeftPanel).isValidCellIndex(newRowIdx, newColIdx)) {
                    bar = bar.getNextBar();
                    newColIdx = Math.min((bar.getPanel(isLeftPanel).getColumnCount() - 1), initColIdx);
                    newRowIdx = 0;
                }
                bar.getPanel(isLeftPanel).getCell(newRowIdx, newColIdx).cellClicked(false);
                return;
            }

            if (! compRef.isAnyBarSelected()) {
                compRef.getInterfaceBar().setSelected(true);
            } else if (compRef.getInterfaceBar().isSelected()) {
                if (barCount > 0) {
                    compRef.clearBarAndCellSelection();
                    compRef.getRelationBar(0).setSelected(true);
                }
            } else {
                RelationBar[] selectedBars = compRef.getSelectedRelationBars();
                int currentIdx = UIUtil.indexOfComponent(selectedBars [selectedBars.length - 1]);
                if (currentIdx < (barCount - 1)) {
                    compRef.clearBarAndCellSelection();
                    compRef.getRelationBar(currentIdx + 1).setSelected(true);
                } else {
                    compRef.clearBarAndCellSelection();
                    compRef.getInterfaceBar().setSelected(true);
                }
            }
        }
    }

    static class MoveLeftSelectionCursorAction extends BaseAction {
        public MoveLeftSelectionCursorAction() {
            super("Move Left Selection Cursor", null, "move left selection cursor", new Integer(KeyEvent.VK_L), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().stopExpandingSelection();
            int barCount = compRef.getRelationBarCount();
            Point barLocation = null;
            Dimension barSize = null;

            if (compRef.isAnyCellSelected()) {
                BaseCell cell = compRef.getSelectedCell();

                if (cell == null) {
                    return;
                }

                BaseBar bar = cell.getBar();
                int[] rowAndColIdx = cell.getRowAndColumnIndex();

                int newRowIdx = rowAndColIdx [0];
                int newColIdx = rowAndColIdx [1] - 1;

                boolean isLeftPanel = cell.isOnLeftPanel();
                /* move to a closest cell in the prev relation */
                if (! bar.getPanel(isLeftPanel).isValidCellIndex(newRowIdx, newColIdx)) {
                    if (! isLeftPanel) {
                        bar.getCenterPanel().clicked(false, false);
                        return;
                    } else {
                        isLeftPanel = ! isLeftPanel;
                        int nextPanelColCount = bar.getPanel(isLeftPanel).getColumnCountOfRow(newRowIdx);
                        if (nextPanelColCount == 0) {
                            bar.getCenterPanel().clicked(false, false);
                            return;
                        } else {
                            newColIdx = nextPanelColCount - 1;
                        }
                    }
                }
                bar.getPanel(isLeftPanel).getCell(newRowIdx, newColIdx).cellClicked(false);
                return;
            }

            if (compRef.isAnyBarSelected()) {
                /* try row index of 0 in the left panel */
                BaseBar bar = compRef.getSelectedBar();
                boolean isLeftPanel = true;
                int newRowIdx = 0;
                int newColIdx = -1;

                int nextPanelColCount = bar.getPanel(isLeftPanel).getColumnCountOfRow(0);
                if (nextPanelColCount == 0) {
                    isLeftPanel = ! isLeftPanel;
                    nextPanelColCount = bar.getPanel(isLeftPanel).getColumnCountOfRow(0);
                    if (nextPanelColCount == 0) {
                        bar.getCenterPanel().clicked(false, false);
                        return;
                    } else {
                        newColIdx = nextPanelColCount - 1;
                    }
                } else {
                    newColIdx = nextPanelColCount - 1;
                }
                bar.getPanel(isLeftPanel).getCell(newRowIdx, newColIdx).cellClicked(false);
                return;
            }
        }
    }

    static class MoveRightSelectionCursorAction extends BaseAction {
        public MoveRightSelectionCursorAction() {
            super("Move Right Selection Cursor", null, "move right selection cursor", new Integer(KeyEvent.VK_R), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().stopExpandingSelection();
            int barCount = compRef.getRelationBarCount();
            Point barLocation = null;
            Dimension barSize = null;

            if (compRef.isAnyCellSelected()) {
                BaseCell cell = compRef.getSelectedCell();

                if (cell == null) {
                    System.out.println("no cell is selected");
                    return;
                }

                BaseBar bar = cell.getBar();
                int[] rowAndColIdx = cell.getRowAndColumnIndex();

                int newRowIdx = rowAndColIdx [0];
                int newColIdx = rowAndColIdx [1] + 1;

                boolean isLeftPanel = cell.isOnLeftPanel();
                /* move to a closest cell in the prev relation */
                if (! bar.getPanel(isLeftPanel).isValidCellIndex(newRowIdx, newColIdx)) {
                    if (isLeftPanel) {
                        bar.getCenterPanel().clicked(false, false);
                        return;
                    } else {
                        isLeftPanel = ! isLeftPanel;
                        if (! bar.getPanel(isLeftPanel).isValidCellIndex(newRowIdx, 0)) {
                            bar.getCenterPanel().clicked(false, false);
                            return;
                        } else {
                            newColIdx = 0;
                        }
                    }
                }
                bar.getPanel(isLeftPanel).getCell(newRowIdx, newColIdx).cellClicked(false);
                return;
            }

            if (compRef.isAnyBarSelected()) {
                /* try row index of 0 in the right panel */
                BaseBar bar = compRef.getSelectedBar();
                boolean isLeftPanel = false;
                int newRowIdx = 0;
                int newColIdx = -1;

                int nextPanelColCount = bar.getPanel(isLeftPanel).getColumnCountOfRow(0);
                if (nextPanelColCount == 0) {
                    isLeftPanel = ! isLeftPanel;
                    nextPanelColCount = bar.getPanel(isLeftPanel).getColumnCountOfRow(0);
                    if (nextPanelColCount == 0) {
                        bar.getCenterPanel().clicked(false, false);
                        return;
                    } else {
                        newColIdx = 0;
                    }
                } else {
                    newColIdx = 0;
                }
                bar.getPanel(isLeftPanel).getCell(newRowIdx, newColIdx).cellClicked(false);
                return;
            }
        }
    }

    static class OpenMappingScriptEditorAction extends BaseAction {
        public OpenMappingScriptEditorAction() {
            super("Open Mapping Script Editor", null, "open mapping script editor", new Integer(KeyEvent.VK_M), KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        }

        public void actionPerformed(ActionEvent e) {
            BaseCell cell = compRef.getSelectedCell();
            if (cell != null && (cell.getCellType() == BaseCell.ITF_OUTPUT || cell.getCellType() == BaseCell.REL_INPUT)) {
                compRef.getImplementationEditor().showScriptEditor(cell, cell.dotPosition);
                // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                //compRef.getImplementationEditor().getScriptEditor().getScriptEditorPane().selectAll();
            }
        }
    }

    static class ExpandUpwardRelationAction extends BaseAction {
        public ExpandUpwardRelationAction() {
            super("Expand Upward Selected Relation", null, "expand upward selected relation(s)", new Integer(KeyEvent.VK_U), KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.SHIFT_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if (compRef.isAnyBarSelected() && ! compRef.getInterfaceBar().isSelected()) {
                /* when relations selected at the beginning */
                if (compRef.getModelEditor().isUpwardExpandingSelection()) {
                    RelationBar[] selectedBars = compRef.getSelectedRelationBars();
                    int firstIdx = UIUtil.indexOfComponent(selectedBars [0]);
                    if (firstIdx > 0) {
                        compRef.getRelationBar(firstIdx - 1).setSelected(true);
                    }
                } else if (compRef.getModelEditor().isDownwardExpandingSelection()) {
                    RelationBar[] selectedBars = compRef.getSelectedRelationBars();
                    selectedBars [selectedBars.length - 1].setSelected(false); // unselect the last relation
                    if (selectedBars.length == 2) { // now only one selected relation remained == stopExpanding
                        compRef.getModelEditor().stopExpandingSelection();
                    }
                } else {
                    RelationBar[] selectedBars = compRef.getSelectedRelationBars();
                    int firstIdx = UIUtil.indexOfComponent(selectedBars [0]);
                    for (int i = 1; i < selectedBars.length; i++) {
                        selectedBars [i].setSelected(false);
                    }
                    if (firstIdx > 0) {
                        compRef.getModelEditor().setUpwardExpandingSelection();
                        compRef.getRelationBar(firstIdx - 1).setSelected(true);
                    }
                }
            }
        }
    }

    static class ExpandDownwardRelationAction extends BaseAction {
        public ExpandDownwardRelationAction() {
            super("Expand Downward Selected Relation", null, "expand downward selected relation(s)", new Integer(KeyEvent.VK_D), KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.SHIFT_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            int barCount = compRef.getRelationBarCount();
            if (compRef.isAnyBarSelected() && ! compRef.getInterfaceBar().isSelected()) {
                if (compRef.getModelEditor().isUpwardExpandingSelection()) {
                    RelationBar[] selectedBars = compRef.getSelectedRelationBars();

                    selectedBars [0].setSelected(false); // unselect the first relation
                    if (selectedBars.length == 2) { // now only one selected relation remained == stopExpanding
                        compRef.getModelEditor().stopExpandingSelection();
                    }
                } else if (compRef.getModelEditor().isDownwardExpandingSelection()) {
                    RelationBar[] selectedBars = compRef.getSelectedRelationBars();
                    int lastIdx = UIUtil.indexOfComponent(selectedBars [selectedBars.length - 1]);
                    if (lastIdx < (barCount - 1)) {
                        compRef.getRelationBar(lastIdx + 1).setSelected(true);
                    }
                } else {
                    RelationBar[] selectedBars = compRef.getSelectedRelationBars();
                    int lastIdx = UIUtil.indexOfComponent(selectedBars [selectedBars.length - 1]);
                    for (int i = 0; i < selectedBars.length - 1; i++) {
                        selectedBars [i].setSelected(false);
                    }
                    if (lastIdx < (barCount - 1)) {
                        compRef.getModelEditor().setDownwardExpandingSelection();
                        compRef.getRelationBar(lastIdx + 1).setSelected(true);
                    }
                }
            }
        }
    }

    static class SelectAllRelationsAction extends BaseAction {
        public SelectAllRelationsAction() {
            super("Select All Relations", null, "select all relations", new Integer(KeyEvent.VK_A), KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            /* if any cell is selected, select all cells in the same panel the first cell of the selected belongs to */
            if (compRef.isAnyCellSelected()) {
                BaseCell firstCellOfSelected = compRef.getSelectedCell();
                compRef.clearBarAndCellSelection();
                BarSidePanel panel = (BarSidePanel) firstCellOfSelected.getParent();
                for (int i = 0; i < panel.getComponentCount(); i++) {
                    BaseCell cell = (BaseCell) panel.getComponent(i);
                    cell.setSelected(true);
                }
                return;
            } else {
                compRef.clearBarAndCellSelection();
                List barList = compRef.getRelationBars();
                for (int i = 0; i < barList.size(); i++) {
                    RelationBar bar = (RelationBar) barList.get(i);
                    bar.setSelected(true);
                }
                return;
            }
        }
    }

    static class UnselectAllRelationsAction extends BaseAction {
        public UnselectAllRelationsAction() {
            super("Clear All Selections", null, "clear all selections", new Integer(KeyEvent.VK_D), KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.clearBarAndCellSelection();
        }
    }


    static class CopyOptionPopupMenuAction extends BaseAction {
        public static final String MAP_OPTION = "create a copy here [auto-mapping ON]";
        public static final String COPY_OPTION = "create a copy here [auto-mapping OFF]";
        public static final String MOVE_OPTION = "move the selected here";
        public static final String CANCEL_OPTION = "cancel";

        private String option;

        public CopyOptionPopupMenuAction(String option, int mnemonic) {
            super(option, null, "select a copy option for the selected cell(s)", new Integer(mnemonic), null);
            this.option = option;
        }

        public void actionPerformed(ActionEvent e) {
            showOptionAndCopy(option);
        }

        public void showOptionAndCopy(String optionName) {
            int targetBarIdx = compRef.getImplementationEditor().getDragTargetBarIndex();
            BaseCell[] cells = compRef.getSelectedCells();

            if (compRef.getRelationEditor().cellInsertionIdx != -1) {
                if (option.equals(MAP_OPTION)) {
                    compRef.getImplementationEditor().moveSelectedCells(cells, targetBarIdx, true, true);
                } else if (option.equals(COPY_OPTION)) {
                    compRef.getImplementationEditor().moveSelectedCells(cells, targetBarIdx, true, false);
                } else if (option.equals(MOVE_OPTION)) {
                    compRef.getImplementationEditor().moveSelectedCells(cells, targetBarIdx, false, false);
                } else if (option.equals(CANCEL_OPTION)) {
                    /* does nothing */
                }
            } else {
                for (int i = 0; i < cells.length; i++) {
                    cells [i].setBorder(UIUtil.CELL_HL_BORDER);
                }
            }

            /* cleanup insertion markers */
            compRef.getImplementationEditor().hideInsertionMarker();
        }
    }



    static class AddImplAction extends BaseAction {
        public AddImplAction() {
            super("Add Implementation...", UIUtil.createImageIcon("images/Plus.gif"), "add implementation", null, null);
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getImplementationEditor().closeScriptEditor(true);
            compRef.getModelEditor().addImplementation();
        }
    }

//    static class RemoveImplAction extends BaseAction {
//        public RemoveImplAction() {
//            super("Remove Selected Implementation", UIUtil.createImageIcon("images/Minus.gif"), "remove selected implementation", null, null);
//        }
//
//        public void actionPerformed(ActionEvent e) {
//            compRef.getModelEditor().removeImplementation();
//        }
//    }

    static class RemoveImplOrItfAction extends BaseAction {
        public RemoveImplOrItfAction() {
            super("Remove Selected Implementation or Interface...", UIUtil.createImageIcon("images/Minus.gif"), "remove selected implementation or interface", null, null);
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getImplementationEditor().closeScriptEditor(true);
            compRef.getModelEditor().removeImplementationOrInterface();
        }
    }

    static class EditCellConfigAction extends BaseAction {
        public EditCellConfigAction() {
            super("Edit the Display Options of Parameter Cells...", UIUtil.createImageIcon("images/CellConfig.gif"), "edit the display options of parameter cells", null, null);
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().editCellConfig();
        }
    }

    static class SwitchEvaluationModeAction extends BaseAction {
        public SwitchEvaluationModeAction() {
            super("Simulation Mode", UIUtil.createImageIcon("images/Plug.gif"), "start simulation mode", new Integer(KeyEvent.VK_V), KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if (compRef.getModelEditor().isEvaluationMode()) {
                compRef.getModelEditor().exitEvaluationMode();
                EvaluationMode.saturateRelationBarColor(compRef);
                ModelEditorFrame.switchEvalutionModeMenuItemModel.setSelected(false);
                ModelEditorFrame.switchEvalutionModeToggleButtonModel.setSelected(false);
                RunEvaluationAction.setEnabled(false);
                StopEvaluationAction.setEnabled(false);
            } else {
                compRef.getImplementationEditor().closeScriptEditor(true);
                compRef.getModelEditor().startEvaluationMode();
                EvaluationMode.desaturateRelationBarColor(compRef);
                //EvaluationMode.reset(compRef); //todo: check if it is good to comment
                ModelEditorFrame.switchEvalutionModeMenuItemModel.setSelected(true);
                ModelEditorFrame.switchEvalutionModeToggleButtonModel.setSelected(true);
                RunEvaluationAction.setEnabled(true);
                StopEvaluationAction.setEnabled(true);
            }
        }
    }

    static class RunEvaluationAction extends BaseAction {
        public RunEvaluationAction() {
            super("Run It", UIUtil.createImageIcon("images/Run.gif"), "simulate this implementation", new Integer(KeyEvent.VK_R), KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            EvaluationMode.run(compRef);
        }
    }

    static class StopEvaluationAction extends BaseAction {
        public StopEvaluationAction() {
            super("Stop It", UIUtil.createImageIcon("images/Stop.gif"), "stop the current simulation", new Integer(KeyEvent.VK_S), KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            EvaluationMode.stop(compRef);
            ModelEditorKit.RunEvaluationAction.setEnabled(true);
        }
    }

    static class SwitchCellWrapAction extends BaseAction {
        public SwitchCellWrapAction() {
            super("Cell-Wrap Mode", UIUtil.createImageIcon("images/Draw.gif"), "switch cell-wrap mode", new Integer(KeyEvent.VK_C), null);
        }

        public void actionPerformed(ActionEvent e) {
            if (compRef.getImplementationEditor().isCellWrapEnabled()) {
                compRef.getImplementationEditor().setCellWrapEnabled(false);
            } else {
                compRef.getImplementationEditor().setCellWrapEnabled(true);
            }

            for (int i = 0; i < compRef.getRelationEditor().getComponentCount(); i++) {
                compRef.getRelationBar(i).updateLayoutConstraintsOfSidePanels();
            }
            compRef.getInterfaceBar().updateLayoutConstraintsOfSidePanels();

            UIUtil.updateEditorBounds(compRef);
        }
    }

    static class SwitchDebugConsoleAction extends BaseAction {
        public SwitchDebugConsoleAction() {
            super("Print Debug Message", UIUtil.createImageIcon("images/Draw.gif"), "print debug message", null, null);
            CLog.setDebug(false);
        }

        public void actionPerformed(ActionEvent e) {
            CLog.setDebug(! CLog.debug);
            if (CLog.debug) {
                CLog.println("debug is on now.");
            } else {
                CLog.println("debug is off now.");
            }
        }
    }

    static class UseLargerCellAction extends BaseAction {
        public UseLargerCellAction() {
            super("Use Larger Cells", UIUtil.createImageIcon("images/MagnifyPlus.gif"), "use larger cells", new Integer(KeyEvent.VK_L), KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            int newLevelOfDetail = compRef.getModelEditor().getCellConfig().levelOfDetail + 1;
            if (newLevelOfDetail <= CellConfig.MAX_LEVEL_OF_DETAIL) {
                ModelEditorFrame.getCellConfigSlider().setValue(newLevelOfDetail);
            } else {
                ModelEditorFrame.setStatusMessage("The cell config has reached the maximum level of detail.");
            }
        }
    }

    static class UseSmallerCellAction extends BaseAction {
        public UseSmallerCellAction() {
            super("Use Smaller Cells", UIUtil.createImageIcon("images/MagnifyMinus.gif"), "use smaller cells", new Integer(KeyEvent.VK_S), KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            int newLevelOfDetail = compRef.getModelEditor().getCellConfig().levelOfDetail - 1;
            if (newLevelOfDetail >= CellConfig.MIN_LEVEL_OF_DETAIL) {
                ModelEditorFrame.getCellConfigSlider().setValue(newLevelOfDetail);
            } else {
                ModelEditorFrame.setStatusMessage("The cell config has reached the minimum level of detail.");
            }
        }
    }

    static class SwitchTypeAndUnitQueryModeAction extends BaseAction {
        public SwitchTypeAndUnitQueryModeAction() {
            super("Show Type and Unit Information", UIUtil.createImageIcon("images/CellConfigShift.gif"), "show type and unit information", new Integer(KeyEvent.VK_S), KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if (! compRef.getModelEditor().isTypeAndUnitQueryMode()) {
                compRef.getModelEditor().startTypeAndUnitQueryMode();
                ModelEditorFrame.switchTypeAndUnitQueryModeMenuItemModel.setSelected(true);
                ModelEditorFrame.switchTypeAndUnitQueryModeToggleButtonModel.setSelected(true);
            } else {
                compRef.getModelEditor().exitTypeAndUnitQueryMode();
                ModelEditorFrame.switchTypeAndUnitQueryModeMenuItemModel.setSelected(false);
                ModelEditorFrame.switchTypeAndUnitQueryModeToggleButtonModel.setSelected(false);
            }
        }
    }

    static class StartTypeAndUnitQueryModeAction extends BaseAction {
        public StartTypeAndUnitQueryModeAction() {
            super("Show Type and Unit Information", UIUtil.createImageIcon("images/CellConfigShift.gif"), "show type and unit information", null, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK, false));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().setTypeAndUnitQueryMode(false); // force the current type-unit-query mode setting as false
            ModelEditorKit.SwitchTypeAndUnitQueryModeAction.actionPerformed(new ActionEvent(this, 0, "switch on the type-and-unit-query mode"));
        }
    }

    static class ExitTypeAndUnitQueryModeAction extends BaseAction {
        public ExitTypeAndUnitQueryModeAction() {
            super("Hide Type and Unit Information", UIUtil.createImageIcon("images/CellConfigShift.gif"), "hide type and unit information", null, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK, true));
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getModelEditor().setTypeAndUnitQueryMode(true); // force the current type-unit-query mode setting as false
            ModelEditorKit.SwitchTypeAndUnitQueryModeAction.actionPerformed(new ActionEvent(this, 0, "switch on the type-and-unit-query mode"));
        }
    }

    static class SwitchNavigationPanelAction extends BaseAction {
        public SwitchNavigationPanelAction() {
            super("Show Navigation Panel", UIUtil.createImageIcon("images/NaviPanelSwitch.gif"), "show navigation panel", new Integer(KeyEvent.VK_N), KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if (compRef.getModelEditor().isNavigationPanelVisible()) {
                compRef.getModelEditor().setNavigationPanelVisible(false);
                ModelEditorFrame.switchNavigationPanelMenuItemModel.setSelected(false);
                ModelEditorFrame.switchNavigationPanelToggleButtonModel.setSelected(false);
            } else {
                compRef.getModelEditor().setNavigationPanelVisible(true);
                ModelEditorFrame.switchNavigationPanelMenuItemModel.setSelected(true);
                ModelEditorFrame.switchNavigationPanelToggleButtonModel.setSelected(true);
            }
        }
    }

    static class ExecuteImplAction extends BaseAction {
        public ExecuteImplAction() {
            super("Show Execution Plan...", UIUtil.createImageIcon("images/Minus.gif"), "show execution plan of this implementation", null, null);
        }

        public void actionPerformed(ActionEvent e) {
            java.util.List inputParamNames = compRef.getCurrentCImplementation().getInputParameterNames();
            java.util.List execSequence = compRef.getCurrentCImplementation().getExecutionSequence(new HashSet(inputParamNames));
            CLog.println("execution sequence: " + execSequence);

            String disp = "";
            for (int i = 0; i < execSequence.size(); i++) {
                String itemStr = "";
                if (execSequence.get(i) instanceof CMapping) {
                    itemStr = "[param mapping] " + ((CMapping) execSequence.get(i)).getOutputNode().getMappedParameterName();
                } else if (execSequence.get(i) instanceof CRelation) {
                    itemStr = "[relation] " + ((CRelation) execSequence.get(i)).getQualifiedName();
                }
                disp += "(" + (i + 1) + ") " + itemStr + "\n";
            }
            JOptionPane.showMessageDialog(compRef.getModelEditor(), disp);
        }
    }



    static class AddItfAction extends BaseAction {
        public AddItfAction() {
            super("Add Interface...", UIUtil.createImageIcon("images/Plus.gif"), "add interface", null, null);
        }

        public void actionPerformed(ActionEvent e) {
            compRef.getImplementationEditor().closeScriptEditor(true);
            compRef.getModelEditor().addInterface();
        }
    }

//    static class RemoveItfAction extends BaseAction {
//        public RemoveItfAction() {
//            super("Remove Selected Interface...", UIUtil.createImageIcon("images/Minus.gif"), "remove selected interface", null, null);
//        }
//
//        public void actionPerformed(ActionEvent e) {
//            compRef.getInterfaceEditor().removeInterface();
//        }
//    }

    /** should be invoked whenever new editor instance is placed. it does not occur. */
    public static void setComponentReferenceOfAllActions(ComponentReference compRef) {
        ModelEditorKit.NewModelAction.setComponentReference(compRef);
        ModelEditorKit.EditModelAction.setComponentReference(compRef);
        ModelEditorKit.OpenAction.setComponentReference(compRef);
        ModelEditorKit.SaveAction.setComponentReference(compRef);
        ModelEditorKit.SaveAsAction.setComponentReference(compRef);
        ModelEditorKit.CloseAction.setComponentReference(compRef);
        ModelEditorKit.ExitAction.setComponentReference(compRef);
        ModelEditorKit.AddRelAction.setComponentReference(compRef);
        ModelEditorKit.DeleteRelationOrCellAction.setComponentReference(compRef);
        ModelEditorKit.AddImplAction.setComponentReference(compRef);
        ModelEditorKit.AddItfAction.setComponentReference(compRef);
        ModelEditorKit.RemoveImplOrItfAction.setComponentReference(compRef);
        ModelEditorKit.EditInterfaceAction.setComponentReference(compRef);
        ModelEditorKit.EditRelationAction.setComponentReference(compRef);

        ModelEditorKit.ExecuteImplAction.setComponentReference(compRef);
        ModelEditorKit.EditCellConfigAction.setComponentReference(compRef);

        ModelEditorKit.SwitchEvaluationModeAction.setComponentReference(compRef);
        ModelEditorKit.RunEvaluationAction.setComponentReference(compRef);
        ModelEditorKit.StopEvaluationAction.setComponentReference(compRef);

        ModelEditorKit.SwitchCellWrapAction.setComponentReference(compRef);
        ModelEditorKit.SwitchNavigationPanelAction.setComponentReference(compRef);

        ModelEditorKit.MoveUpRelationAction.setComponentReference(compRef);
        ModelEditorKit.MoveDownRelationAction.setComponentReference(compRef);

        ModelEditorKit.MoveDownSelectionCursorAction.setComponentReference(compRef);
        ModelEditorKit.MoveUpSelectionCursorAction.setComponentReference(compRef);
        ModelEditorKit.MoveLeftSelectionCursorAction.setComponentReference(compRef);
        ModelEditorKit.MoveRightSelectionCursorAction.setComponentReference(compRef);

        ModelEditorKit.OpenMappingScriptEditorAction.setComponentReference(compRef);

        ModelEditorKit.ExpandDownwardRelationAction.setComponentReference(compRef);
        ModelEditorKit.ExpandUpwardRelationAction.setComponentReference(compRef);
        ModelEditorKit.SelectAllRelationsAction.setComponentReference(compRef);
        ModelEditorKit.UnselectAllRelationsAction.setComponentReference(compRef);

        ModelEditorKit.CopyOptionPopupMenu1Action.setComponentReference(compRef);
        ModelEditorKit.CopyOptionPopupMenu2Action.setComponentReference(compRef);
        ModelEditorKit.CopyOptionPopupMenu3Action.setComponentReference(compRef);
        ModelEditorKit.CopyOptionPopupMenu4Action.setComponentReference(compRef);

        ModelEditorKit.UseLargerCellAction.setComponentReference(compRef);
        ModelEditorKit.UseSmallerCellAction.setComponentReference(compRef);
        ModelEditorKit.SwitchTypeAndUnitQueryModeAction.setComponentReference(compRef);
        ModelEditorKit.StartTypeAndUnitQueryModeAction.setComponentReference(compRef);
        ModelEditorKit.ExitTypeAndUnitQueryModeAction.setComponentReference(compRef);
        ModelEditorKit.compRef = compRef;

    }

    public static void setEnabledOfAllActions(boolean isEnabled) {
        AddRelAction.setEnabled(isEnabled);
        DeleteRelationOrCellAction.setEnabled(isEnabled);
        DeleteRelationOrCellAction.setEnabled(isEnabled);
        AddImplAction.setEnabled(isEnabled);
        AddItfAction.setEnabled(isEnabled);
        RemoveImplOrItfAction.setEnabled(isEnabled);
        SaveAction.setEnabled(isEnabled);
        SaveAction.setEnabled(isEnabled);
        CloseAction.setEnabled(isEnabled);

        EditInterfaceAction.setEnabled(isEnabled);
        EditRelationAction.setEnabled(isEnabled);
        RemoveImplOrItfAction.setEnabled(isEnabled);
        EditModelAction.setEnabled(isEnabled);

        MoveUpRelationAction.setEnabled(isEnabled);
        MoveDownRelationAction.setEnabled(isEnabled);

        MoveUpSelectionCursorAction.setEnabled(isEnabled);
        MoveDownSelectionCursorAction.setEnabled(isEnabled);
        MoveLeftSelectionCursorAction.setEnabled(isEnabled);
        MoveRightSelectionCursorAction.setEnabled(isEnabled);

        OpenMappingScriptEditorAction.setEnabled(isEnabled);

        ExpandUpwardRelationAction.setEnabled(isEnabled);
        ExpandDownwardRelationAction.setEnabled(isEnabled);
        SelectAllRelationsAction.setEnabled(isEnabled);
        UnselectAllRelationsAction.setEnabled(isEnabled);

        CopyOptionPopupMenu1Action.setEnabled(isEnabled);
        CopyOptionPopupMenu2Action.setEnabled(isEnabled);
        CopyOptionPopupMenu3Action.setEnabled(isEnabled);
        CopyOptionPopupMenu4Action.setEnabled(isEnabled);

        ExecuteImplAction.setEnabled(isEnabled);
        EditCellConfigAction.setEnabled(isEnabled);

        SwitchEvaluationModeAction.setEnabled(isEnabled);
        //RunEvaluationAction.setEnabled(isEnabled);
        RunEvaluationAction.setEnabled(false);
        StopEvaluationAction.setEnabled(false);

        SwitchCellWrapAction.setEnabled(isEnabled);
        SwitchNavigationPanelAction.setEnabled(isEnabled);

        UseLargerCellAction.setEnabled(isEnabled);
        UseSmallerCellAction.setEnabled(isEnabled);
        SwitchTypeAndUnitQueryModeAction.setEnabled(isEnabled);
        StartTypeAndUnitQueryModeAction.setEnabled(isEnabled);
        ExitTypeAndUnitQueryModeAction.setEnabled(isEnabled);
        ModelEditorFrame.getCellConfigSlider().setEnabled(isEnabled);
    }
}
