package dsm;

import javax.swing.JPanel;
import java.awt.*;
import java.lang.Math;
import javax.swing.JViewport;
import javax.swing.table.TableModel;
import javax.swing.event.*;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

//import javax.swing.ListSelectionModel;

public class DSMZoomPanel extends JPanel implements ChangeListener, ListSelectionListener
{
	// parameters to define the scope of the viewport as a function
	// of the total viewable area, including columns, so that scaling
	// is possible.

	int width;
	int height;

	// the view coordinates that appear in the upper left hand corner of the viewport
	int xposition;
	int yposition;

	// visible range width
	int vwidth;
	// visible range height
	int vheight;

	int n, size;
	int tx = 0;
	int ty = 0;
	int twidth = 0;
	int theight = 0;
	int x_selected;
	int y_selected;
	JViewport viewport;

	TableModel tablemodel;
	// ListSelectionModel selectionmodel;
	// ListSelectionModel selectionmodel_column;

	public DSMZoomPanel(JViewport viewport, int size)
	{
		super();
		this.viewport = viewport;
		this.tablemodel = ((JTable) viewport.getView()).getModel();
		this.n = ((JTable) viewport.getView()).getModel().getColumnCount(); //n:
		this.width = ((JTable) viewport.getView()).getColumnModel().getTotalColumnWidth();
		this.height = this.width;
		this.vheight = viewport.getExtentSize().height;  //visible range height
		this.vwidth = viewport.getExtentSize().width;    //visible range width
		this.xposition = viewport.getViewPosition().x;
		this.yposition = viewport.getViewPosition().y;  // the view coordinates that appear in the upper left hand corner of the viewport
		this.size = size;
		x_selected = -1;
		y_selected = -1;
		viewport.addChangeListener(this);

		setPreferredSize(new Dimension(size, size));
		setBackground(Color.white);
	}


	public DSMZoomPanel(JViewport viewport, JTable table, int size)
	{
		super();
		this.viewport = viewport;
		this.tablemodel = table.getModel();
		this.n = table.getModel().getColumnCount(); //n:
		this.width = table.getWidth();
		this.height = table.getHeight();
		this.vheight = viewport.getExtentSize().height;  //visible range height
		this.vwidth = viewport.getExtentSize().width;    //visible range width
		this.xposition = viewport.getViewPosition().x;
		this.yposition = viewport.getViewPosition().y;  // the view coordinates that appear in the upper left hand corner of the viewport
		this.size = size;
		x_selected = -1;
		y_selected = -1;
		viewport.addChangeListener(this);

		// add this for listen to the selection
		table.getSelectionModel().addListSelectionListener(this);
		table.getColumnModel().getSelectionModel().addListSelectionListener(this);
		setPreferredSize(new Dimension(size, size));
		setBackground(Color.white);
	}


	public boolean isVisibleNow(int row, int column)
	{
		if ((((xposition / width) * n) < row) && ((row < ((xposition + vwidth) * n / width))) && (((yposition * n / height) < column)) && ((column < ((yposition + vheight) * n / height))))
		{
			return true;
		}
		return false;
	}

//for painting the
	public void paint(Graphics g)
	{
		int b = 0;

		try
		{
			g.setColor(Color.gray);
			g.fillRect(0, 0, size, size);
			g.setColor(Color.white);
			g.fillRect((xposition * size / width), (yposition * size / height), ((2 + vwidth) * size / width), ((2 + vheight) * size / height));
			tx = (xposition * size / width);
			ty = (yposition * size / height);
			twidth = ((2 + vwidth) * size / width);
			theight = ((2 + vheight) * size / height);

			//draw each item in Matrix
			for (int i = 1; i < tablemodel.getRowCount(); i++)
			{
				g.setColor(Color.black);

				for (int j = 0; j < tablemodel.getColumnCount(); j++)
				{
					b = ((Integer) tablemodel.getValueAt(i, j)).intValue();  // b: 0 or 1
					if (b > 0)
					{
						//g.setColor(model.getColorAt(i,j));
						g.drawRect((size * j / n), (size * i / n), 1, 1);
					}
				}
			}

			//draw the diagonal line with red color
			g.setColor(Color.red);
			g.drawLine(0, 0, size, size);
			setSelected(g);
		}

		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception caught in DSMZoomPanel paint");
		}
	}


	public void setSelected(Graphics p)
	{
		if (y_selected != -1)
		{
			//	p.setColor(super.getSelectionBackground());
			p.setColor(Color.pink);
			p.drawLine(0, y_selected * size / n, size, y_selected * size / n);
		}

		if (x_selected != -1)
		{
			//	p.setColor(super.getSelectionBackground());
			p.setColor(Color.pink);
			p.drawLine(x_selected * size / n, 0, x_selected * size / n, size);
		}
	}


	public void stateChanged(ChangeEvent e)
	{
		{
			try
			{
				this.n = ((JTable) viewport.getView()).getColumnModel().getColumnCount();
				this.width = ((JTable) viewport.getView()).getColumnModel().getTotalColumnWidth();
				this.height = this.width;
				//this.width = ((JTable)viewport.getView()).getWidth();
				//this.height = ((JTable)viewport.getView()).getHeight();
				this.vheight = viewport.getExtentSize().height;
				this.vwidth = viewport.getExtentSize().width;
				this.xposition = viewport.getViewPosition().x;
				this.yposition = viewport.getViewPosition().y;
				this.paint(this.getGraphics());
				// for DEBUG
				//	System.out.println("statechanged called");

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}


/**
 public void selectionChanged(int x, int y)
 {
 x_selected = x;
 y_selected = y;
 this.paint(this.getGraphics());
 }
 */

public void valueChanged(ListSelectionEvent e)
{
	try
	{
		this.x_selected = ((JTable) viewport.getView()).getSelectedColumn();
		this.y_selected = ((JTable) viewport.getView()).getSelectedRow();

		this.paint(this.getGraphics());

	}
	catch (Exception ex)
	{
		ex.printStackTrace();
	}

}

}