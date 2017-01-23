package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CLog;

import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 20.
 */
public class RelationBarLeftPanel extends BarSidePanel {
    public RelationBarLeftPanel(BaseBar parentBar, ComponentReference compRef) {
        super(BarSidePanel.REL_LEFT, parentBar, compRef);
    }

    public void removeRelationInputCell(String paramName) {
        //speedup Clog.debug("removing relation input parameter: " + paramName);
        Component cell = compRef.getRelationInputCell(getBar().getRelAlias(), paramName);
        if (cell != null) {
            super.removeCell(cell);
        }
    }

    public void addRelationInputCell(String paramName, String dataType, String unit, String script) {
        //speedup Clog.debug("adding relation input param: " + paramName + ", " + dataType + ", " + unit);
        RelationInputCell paramCell = new RelationInputCell(getBar().getRelAlias(), paramName, dataType, unit, script, compRef);
        super.addCell(paramCell);
    }
}
