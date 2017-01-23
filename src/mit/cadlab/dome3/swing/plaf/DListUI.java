// DListUI.java
package mit.cadlab.dome3.swing.plaf;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.basic.BasicListUI;

/*
 * Customized ListUI:
 * fix bug 4300224 (intervalAdded updates selections improperly)
 *  which is fixed in Java 1.4
 * modified contentsChanged to clear selections
 */

public class DListUI extends BasicListUI
{

	protected ListDataListener createListDataListener()
	{
		return new DListDataListener();
	}

	public class DListDataListener extends ListDataHandler
	{
		// interval sent to selection model is length of interval
		// which, for a closed interval, is the difference between
		// the bounds + 1
		public void intervalAdded(ListDataEvent e)
		{
			updateLayoutStateNeeded = modelChanged;

			int minIndex = Math.min(e.getIndex0(), e.getIndex1());
			int maxIndex = Math.max(e.getIndex0(), e.getIndex1());

			/* Sync the SelectionModel with the DataModel.
			 */

			ListSelectionModel sm = list.getSelectionModel();
			if (sm != null) {
				sm.insertIndexInterval(minIndex, maxIndex - minIndex + 1, true);
			}

			/* Repaint the entire list, from the origin of
			 * the first added cell, to the bottom of the
			 * component.
			 */

			int y = Math.max(0, convertRowToY(minIndex));
			int h = list.getHeight() - y;
			list.revalidate();
			list.repaint(0, y, list.getWidth(), h);
		}

		public void contentsChanged(ListDataEvent e)
		{
			// clear selection (don't know how to deal otherwise)
			ListSelectionModel sm = list.getSelectionModel();
			if (sm != null) {
				sm.clearSelection();
			}

			updateLayoutStateNeeded = modelChanged;
			list.revalidate();
			list.repaint();
		}

	} // end DListDataHandler

}
