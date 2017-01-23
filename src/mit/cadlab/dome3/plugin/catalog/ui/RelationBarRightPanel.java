package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CLog;

import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 20.
 */
public class RelationBarRightPanel extends BarSidePanel {

    public RelationBarRightPanel(BaseBar parentBar, ComponentReference compRef) {
        super(BarSidePanel.REL_RIGHT, parentBar, compRef);
    }

    public void removeRelationOutputCell(String paramName) {
        //speedup Clog.debug("removing relation output parameter: " + paramName);
        Component cell = compRef.getRelationOutputCell(getBar().getRelAlias(), paramName);
        if (cell != null) {
            super.removeCell(cell);
        }
    }

    public void removeRelationDerivedCell(String paramName) {
        //speedup Clog.debug("removing derived output parameter: " + paramName);
        Component cell = compRef.getRelationDerivedCell(getBar().getRelAlias(), paramName);
        if (cell != null) {
            super.removeCell(cell);
        }
    }

    public void addRelationOutputCell(String paramName, String dataType, String unit) {
        //speedup Clog.debug("adding relation output parameter: " + paramName + ", " + dataType + ", " + unit);
        RelationOutputCell paramCell = new RelationOutputCell(getBar().getRelAlias(), paramName, dataType, unit, compRef);
        super.addCell(paramCell);
    }

    public void addRelationDerivedCell(String paramName, String newDataType, String newUnit, String srcParamName) {
        //speedup Clog.debug("adding relation derived parameter: " + paramName + ", " + newDataType + ", " + newUnit + ", " + srcParamName);
        RelationDerivedCell paramCell = new RelationDerivedCell(getBar().getRelAlias(), paramName, newDataType, newUnit, srcParamName, compRef);
        super.addCell(paramCell);
    }
}
