// DListCellRenderer.java
package mit.cadlab.dome3.swing;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class DListCellRenderer extends DefaultListCellRenderer
{
	// gets rid of focus border

	public Component getListCellRendererComponent(JList list,
	                                              Object value,
	                                              int index,
	                                              boolean isSelected,
	                                              boolean cellHasFocus)
	{
		if (list.getModel() instanceof DListModel) {
			DListModel model = (DListModel) list.getModel();
			setComponentOrientation(list.getComponentOrientation());
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setIcon(model.getIcon(index));
			String text = model.getListText(index);
			setText((text == null) ? "" : text);

			setEnabled(list.isEnabled());
			setFont(list.getFont());
			return this;
		} else {
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	}

}
