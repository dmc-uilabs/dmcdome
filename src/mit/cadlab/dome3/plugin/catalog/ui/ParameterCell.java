package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 23
 *
 */
public class ParameterCell extends JPanel {

    protected boolean isSelected;

    protected ComponentReference compRef;

    /** cellType is one of ParameterCell.REL_INPUT_TYPE, ParameterCell.REL_OUTPUT_TYPE, and ParameterCell.REL_DERIVED_TYPE */
    public ParameterCell(ComponentReference compRef) {
        super();
        this.compRef = compRef;
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public ComponentReference getComponentReference() {
        return compRef;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

//    public RelationBar getRelationBar() {
//        if (this.getParent() instanceof RelationBarLeftPanel) {
//            return ((RelationBarLeftPanel) this.getParent()).relationBar;
//        }
//        if (this.getParent() instanceof RelationBarRightPanel) {
//            return ((RelationBarRightPanel) this.getParent()).relationBar;
//        }
//        /* never reaches here */
//        return null;
//    }
//
//    public InterfaceBar getInterfaceBar() {
//        if (this.getParent() instanceof InterfaceBarLeftPanel) {
//            return ((InterfaceBarLeftPanel) this.getParent()).interfaceBar;
//        }
//        if (this.getParent() instanceof InterfaceBarRightPanel) {
//            return ((InterfaceBarRightPanel) this.getParent()).interfaceBar;
//        }
//        /* never reaches here */
//        return null;
//    }

}
