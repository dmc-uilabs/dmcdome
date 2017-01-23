package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CConstant;

import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 25
 */
public class InterfaceBar extends BaseBar {

    /** construct empty interface bar. parent editor is passed to be used for layout validation. normally invoked by InterfaceEditor.setInterfaceBar() only, so it is made protected */
    protected InterfaceBar(String itfName, ComponentReference compRef) {
        super(BaseBar.ITF_BAR, itfName, CConstant.ITF_ALIAS, compRef);
    }

    public void addInterfaceInputCell(String paramName, String dataType, String unit) {
        ((InterfaceBarLeftPanel) leftPanel).addInterfaceInputCell(paramName, dataType, unit);
    }

    public void addInterfaceOutputCell(String paramName, String dataType, String unit, String script) {
        ((InterfaceBarRightPanel) rightPanel).addInterfaceOutputCell(paramName, dataType, unit, script);
    }

    public void removeInterfaceInputCell(String paramName) {
        ((InterfaceBarLeftPanel) leftPanel).removeInterfaceInputCell(paramName);
    }

    public void removeInterfaceOutputCell(String paramName) {
        ((InterfaceBarRightPanel) rightPanel).removeInterfaceOutputCell(paramName);
    }

    /** returns RelationInputCell with given name. returns null if not found */
    public InterfaceInputCell getInterfaceInputCell(String paramName) {
        Component[] components = leftPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            InterfaceInputCell cell = (InterfaceInputCell) components[i];
            if (paramName.equals(cell.getParamName())) {
                return cell;
            }
        }
        return null;
    }

    /** returns RelationOutputCell with given name. returns null if not found. paramName is unqualified name. (ex) "my width"  */
    public InterfaceOutputCell getInterfaceOutputCell(String paramName) {
        Component[] components = rightPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof InterfaceOutputCell) {
                InterfaceOutputCell cell = (InterfaceOutputCell) components[i];
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
        Component[] components = rightPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RelationInputCell) {
                counter++;
            }
        }
        return counter;
    }

    /** returns the number of ParameterCell on the left of this Relation Bar */
    public int getRelationInutCellCount() {
        return leftPanel.getComponentCount();
    }

    public BarSidePanel getLeftPanel() {
        return leftPanel;
    }

    public BarSidePanel getRightPanel() {
        return rightPanel;
    }
}
