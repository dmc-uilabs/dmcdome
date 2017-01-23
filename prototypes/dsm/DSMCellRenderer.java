package dsm;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.io.Serializable;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class DSMCellRenderer extends JLabel implements TableCellRenderer, Serializable
{
    protected static Border noFocusBorder;

    private Color unselectedForeground;
    private Color unselectedBackground;
	private Color _backgroundColor;
    int sq;
    boolean bSelected;


    public DSMCellRenderer(Color backgroundColor) {
	    super();
	    this._backgroundColor = backgroundColor;
        noFocusBorder = new EmptyBorder(1, 2, 1, 2);
	    setOpaque(true);
        //setBorder(noFocusBorder);
        //need to either use a method here on something else that returns the string[] here and make it a
        //member of the class, or to pass it in the constructor; don't forget to change its string[] when
        //changes made in model; but will this only set one text, or will it be able to change?
//      setToolTipText("");
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


    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column)
    {
	    try
	    {
		    sq = table.getRowHeight() * 2 / 3;
		    bSelected = isSelected;

		    // selection settings

		    if (isSelected)
		    {

			    super.setForeground(table.getSelectionForeground());
			    super.setBackground(table.getSelectionBackground());
		    }

		    else
		    {
			    super.setForeground((unselectedForeground != null) ? unselectedForeground
			            : table.getForeground());
			    super.setBackground((unselectedBackground != null) ? unselectedBackground
			            : table.getBackground());
		    }

		    // focus settings
			if (row % 2 == 0 || column % 2 == 0)
			{
				if (hasFocus)
				{
					setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
				}
				else
				{
					setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
				}
			}
		    setFont(table.getFont());
		    if ((((Integer) value).intValue()) == 0)
		    {
				    this.setBackground(this._backgroundColor);
				    this.setForeground(Color.BLACK);
				    this.setOpaque(true);
				    setIcon(new PlayIcon(this._backgroundColor, sq));
				    setToolTipText("");
		    }

		    else if ((((Integer) value).intValue()) == 1)
		    {
			    if (row == column)
			    {
				    this.setBackground(this._backgroundColor);
				    this.setForeground(Color.BLACK);
				    this.setOpaque(true);
				    this.setIcon(new PlayIcon(Color.BLACK,sq));
				    setToolTipText("diagonal");
			    }
			    else if(row > column)
			    {

				    setIcon(new PlayIcon(new Color(0,200,0), sq));
				    setToolTipText("forward relationship of (" + table.getColumnName(column) + "-->" + table.getColumnName(row) + ")");
			    }
			    else
			    {
				    this.setIcon(new PlayIcon(new Color(200,0,0), sq));
				    this.setToolTipText("feedback relationship of (" + table.getColumnName(column) + "-->" + table.getColumnName(row) + ")");
			    }
		    }
	    }
	    catch (Exception e)
	    {
		    System.out.println("Error getting component");
		    System.exit(1);
	    }
	    return this;
    }

	protected void setValue(Object value)
	{
		setText((value == null) ? "" : value.toString());
	}

	public void paint(Graphics g)
	{
		Color bcolor;
		Icon currentI = getIcon();

		bcolor = bSelected ? SystemColor.textHighlight : Color.white;
		g.setColor(bcolor);

		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		super.paint(g);

	}
}