
import javax.swing.*;
import javax.swing.border.*;
import java.awt.Component;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.Icon;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class DSMListCellRenderer extends JLabel implements ListCellRenderer, Serializable
{
    protected static Border noFocusBorder;
    private Color unselectedForeground;
    private Color unselectedBackground;
    int sq;


    public DSMListCellRenderer() {
		super();
        noFocusBorder = new EmptyBorder(1, 2, 1, 2);
		setOpaque(true);
        setBorder(noFocusBorder);
    }


	// Basic operations to implement TableCellRenderer

    public void setForeground(Color c) {
        super.setForeground(c);
        unselectedForeground = c;
    }


    public void setBackground(Color c) {
        super.setBackground(c);
        unselectedBackground = c;
    }


    public void updateUI() {
        super.updateUI();
		setForeground(null);
		setBackground(null);
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
	//	Displays the correct service name on the list

		try{
			String name = (String)list.getModel().getElementAt(index);
			setText(name);
		}

		catch(Exception e)
		{
			System.out.println("error getting value in DSMCellRendererComponent "+e);
		}

		return this;
    }


    protected void setValue(Object value) {
		setText((value == null) ? "" : value.toString());
    }

}
