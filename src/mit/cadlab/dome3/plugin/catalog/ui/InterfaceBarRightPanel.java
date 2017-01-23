package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CLog;

import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 25.
 */
public class InterfaceBarRightPanel extends BarSidePanel {

    public InterfaceBarRightPanel(BaseBar parentBar, ComponentReference compRef) {
        super(BarSidePanel.ITF_RIGHT, parentBar, compRef);
    }

    public void removeInterfaceOutputCell(String paramName) {
        //speedup Clog.debug("removing interface output parameter: " + paramName);
        Component cell = compRef.getInterfaceOutputCell(paramName);
        if (cell != null) {
            super.removeCell(cell);
        }
    }

    public void addInterfaceOutputCell(String paramName, String dataType, String unit, String script) {
        //speedup Clog.debug("adding interface output parameter: " + paramName + ", " + dataType + ", " + unit + ", " + script);
        InterfaceOutputCell paramCell = new InterfaceOutputCell(getBar().getRelAlias(), paramName, dataType, unit, script, compRef);
        super.addCell(paramCell);
    }
}
