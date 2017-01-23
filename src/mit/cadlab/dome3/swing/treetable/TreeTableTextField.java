// TreeTableTextField.java
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.swing.DTextField;

/**
 * Component used by TreeTableCellEditor. The only thing this does
 * is to override the <code>reshape</code> method, and to ALWAYS
 * make the x location be <code>offset</code>.
 */
public class TreeTableTextField extends DTextField
{
	public int offset;

	public void reshape(int x, int y, int w, int h)
	{
		int newX = Math.max(x, offset);
		super.reshape(newX, y, w - (newX - x), h);
	}

}
