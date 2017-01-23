package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import java.awt.event.*;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 9.
 */
public class PreviewParameterCell extends BaseCell {
    private CellConfigDialog cellConfigDialog;

    public PreviewParameterCell(String relAlias, String paramName, String dataType, String unit, String script, ComponentReference compRef, CellConfigDialog cellConfigDialog) {
        super(BaseCell.ITF_OUTPUT, relAlias, paramName, dataType, unit, null, script, compRef);

//        MouseListener[] mouseListeners = nameLabel.getMouseListeners();
//        for (int i = 0; i < mouseListeners.length; i++) {
//            MouseListener mouseListener = mouseListeners[i];
//            nameLabel.removeMouseListener(mouseListener);
//        }
        this.cellConfigDialog = cellConfigDialog;
        clearMouseListeners(scriptDispPane);
    }

    private static void clearMouseListeners(JComponent comp) {
        MouseListener[] mouseListeners = comp.getMouseListeners();
        for (int i = 0; i < mouseListeners.length; i++) {
            MouseListener mouseListener = mouseListeners[i];
            comp.removeMouseListener(mouseListener);
        }
    }

    protected void initComponentsForAll() {
        super.initComponentsForAll();

        typeAndUnitHideTimer = new Timer(800, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CellConfig cellConfig = getPreviewCellConfig();
                updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, compRef.getModelEditor().isTypeAndUnitQueryMode(), false);
            }
        });
        typeAndUnitHideTimer.setRepeats(false);

        clearMouseListeners(nameLabel);
        nameLabel.setToolTipText(null);
        unitLabel.setToolTipText(null);

        nameLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                CellConfig cellConfig = getPreviewCellConfig();
                if (! cellConfig.showTypeUnit) {
                    typeAndUnitHideTimer.stop();
                }
                updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, true, false);
            }

            public void mouseExited(MouseEvent e) {
                CellConfig cellConfig = getPreviewCellConfig();
                if (! cellConfig.showTypeUnit) {
                    typeAndUnitHideTimer.restart();
                }
            }
        });
    }

    public CellConfig getPreviewCellConfig() {
        return cellConfigDialog.getPreviewCellConfig();
    }
}
