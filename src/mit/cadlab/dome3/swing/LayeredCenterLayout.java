// LayeredCenterLayout.java
package mit.cadlab.dome3.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.Vector;

/*
 * This layout places all items in the center of the container
 * expanded as big as possible. For use with JLayeredPane.
 * Items added to the container are placed top-to-bottom in depth.
 * Items on top should not be opaque in order for items below to
 * show through. Use setOpaque(false) on each panel to make it transparent.
 */

public class LayeredCenterLayout
        implements java.awt.LayoutManager2, java.io.Serializable
{

	protected Vector components = new Vector();

	public void addLayoutComponent(String name, Component comp)
	{
		// Adds the specified component with the specified name to the layout.
		components.add(comp);
	}

	public void layoutContainer(Container parent)
	{
		// Lays out the container in the specified panel.
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			Dimension dimensions = parent.getSize();
			int top = insets.top;
			int bottom = dimensions.height - insets.bottom;
			int left = insets.left;
			int right = dimensions.width - insets.right;

			Enumeration e = components.elements();
			while (e.hasMoreElements()) {
				Component comp = (Component) e.nextElement();
				comp.setBounds(left, top, right - left, bottom - top);
			}
		}
	}

	public Dimension minimumLayoutSize(Container parent)
	{
		// Calculates the minimum size dimensions for the specified panel
		// given the components in the specified parent container.
		Dimension d = new Dimension(0, 0);
		Enumeration e = components.elements();
		while (e.hasMoreElements()) {
			Component comp = (Component) e.nextElement();
			Dimension cd = comp.getMinimumSize();
			d.width = Math.max(d.width, cd.width);
			d.height = Math.max(d.height, cd.height);
		}
		return d;
	}

	public Dimension preferredLayoutSize(Container parent)
	{
		// Calculates the preferred size dimensions for the specified panel
		// given the components in the specified parent container.
		Dimension d = new Dimension(0, 0);
		Enumeration e = components.elements();
		while (e.hasMoreElements()) {
			Component comp = (Component) e.nextElement();
			Dimension cd = comp.getPreferredSize();
			d.width = Math.max(d.width, cd.width);
			d.height = Math.max(d.height, cd.height);
		}
		return d;
	}


	public void removeLayoutComponent(Component comp)
	{
		// Removes the specified component from the layout.
		components.remove(comp);
	}

	// LayoutManager2 interface

	public void addLayoutComponent(Component comp, Object constraints)
	{
		// Adds the specified component to the layout, using the specified constraint object.
		components.add(comp);
	}

	public float getLayoutAlignmentX(Container target)
	{
		// Returns the alignment along the x axis.
		return 0.5f;
	}

	public float getLayoutAlignmentY(Container target)
	{
		// Returns the alignment along the y axis.
		return 0.5f;
	}

	public void invalidateLayout(Container target)
	{
		// Invalidates the layout, indicating that if the layout manager
		// has cached information it should be discarded.
	}

	public Dimension maximumLayoutSize(Container target)
	{
		// Returns the maximum size of this component.
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

}
