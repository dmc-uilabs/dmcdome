package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CLog;

import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 25.
 */
public class InterfaceBarLeftPanel extends BarSidePanel {

    public InterfaceBarLeftPanel(BaseBar parentBar, ComponentReference compRef) {
        super(BarSidePanel.ITF_LEFT, parentBar, compRef);
    }

    public void removeInterfaceInputCell(String paramName) {
        //speedup Clog.debug("removing interface input parameter: " + paramName);
        Component cell = compRef.getInterfaceInputCell(paramName);
        if (cell != null) {
            super.removeCell(cell);
        }
    }

    public void addInterfaceInputCell(String paramName, String dataType, String unit) {
        //speedup Clog.debug("adding interface input parameter: " + paramName + ", " + dataType + ", " + unit);
        InterfaceInputCell paramCell = new InterfaceInputCell(getBar().getRelAlias(), paramName, dataType, unit, compRef);
        super.addCell(paramCell);
    }
}
