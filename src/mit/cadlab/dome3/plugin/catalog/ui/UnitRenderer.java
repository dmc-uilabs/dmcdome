package mit.cadlab.dome3.plugin.catalog.ui;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 3.
 */
import mit.cadlab.dome3.plugin.catalog.core.CUnit;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class UnitRenderer extends JLabel implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public UnitRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(JTable table, Object unit, boolean isSelected, boolean hasFocus, int row, int column) {
//        Color newColor = (Color)color;
//        setBackground(newColor);
        CUnit newUnit = (CUnit) unit;
        setText(newUnit.getDescription());
        setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 0));

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        return this;
    }
}
