package mit.cadlab.dome3.plugin.catalog.ui;

import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 19
 */
public class RelationBar extends BaseBar {

    /** construct empty relation bar. parent editor is passed to be used for layout validation */
    public RelationBar(String relName, String relAlias, ComponentReference compRef) {
        super(BaseBar.REL_BAR, relName, relAlias, compRef);
    }

    public void addRelationInputCell(String paramName, String dataType, String unit, String script) {
        ((RelationBarLeftPanel) leftPanel).addRelationInputCell(paramName, dataType, unit, script);
    }

    public void addRelationOutputCell(String paramName, String dataType, String unit) {
        ((RelationBarRightPanel) rightPanel).addRelationOutputCell(paramName, dataType, unit);
    }

    public void addRelationDerivedCell(String paramName, String newDataType, String newUnit, String srcParamName) {
        ((RelationBarRightPanel) rightPanel).addRelationDerivedCell(paramName, newDataType, newUnit, srcParamName);
    }

    public void removeRelationInputCell(String paramName) {
        ((RelationBarLeftPanel) leftPanel).removeRelationInputCell(paramName);
    }

    public void removeRelationOutputCell(String paramName) {
        ((RelationBarRightPanel) rightPanel).removeRelationOutputCell(paramName);
    }

    public void removeRelationDerivedCell(String paramName) {
        ((RelationBarRightPanel) rightPanel).removeRelationDerivedCell(paramName);
    }

    /** returns RelationInputCell with given name. returns null if not found */
    public RelationInputCell getRelationInputCell(String paramName) {
        Component[] components = leftPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            RelationInputCell cell = (RelationInputCell) components[i];
            if (paramName.equals(cell.getParamName())) {
                return cell;
            }
        }
        return null;
    }

    /** returns RelationOutputCell with given name. returns null if not found. paramName is unqualified name. (ex) "my width"  */
    public RelationOutputCell getRelationOutputCell(String paramName) {
        Component[] components = rightPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RelationOutputCell) {
                RelationOutputCell cell = (RelationOutputCell) components[i];
                if (paramName.equals(cell.getParamName())) {
                    return cell;
                }
            }
        }
        return null;
    }

    /** returns RelationDerivedCell with given name. returns null if not found paramName is unqualified name. (ex) "my width"  */
    public RelationDerivedCell getRelationDerivedCell(String paramName) {
        Component[] components = rightPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RelationDerivedCell) {
                RelationDerivedCell cell = (RelationDerivedCell) components[i];
                if (paramName.equals(cell.getParamName())) {
                    return cell;
                }
            }
        }
        return null;
    }

    /** returns the number of RelationOutputCell in this Relation Bar */
    public int getRelationOuputCellCount() {
        int counter = 0;
        Component[] components = rightPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RelationOutputCell) {
                counter++;
            }
        }
        return counter;
    }

    /** returns the number of RelationDerivedCell that has the given source param name */
    public int getRelationDerivedCellCount(String srcParamName) {
        int counter = 0;
        Component[] components = rightPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RelationDerivedCell && srcParamName.equals(((RelationDerivedCell) components[i]).getSourceParamName())) {
                counter++;
            }
        }
        return counter;
    }

    /** returns the number of RelationInputCell in this Relation Bar */
    public int getRelationInputCellCount() {
        int counter = 0;
        Component[] components = leftPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RelationInputCell) {
                counter++;
            }
        }
        return counter;
    }
}
