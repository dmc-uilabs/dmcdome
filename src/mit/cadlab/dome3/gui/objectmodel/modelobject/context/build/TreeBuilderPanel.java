// TreeBuilderPanel.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.build;

import mit.cadlab.dome3.swing.Templates;

import mit.cadlab.dome3.objectmodel.*;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton3Msg;
import mit.cadlab.dome3.config.Registry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public abstract class TreeBuilderPanel extends JPanel
{
	// includes ScrollPane on left and buttons on right
	public static GridBagConstraints gbc;
	protected JButton upButton,downButton;
	protected boolean isMoveEnabled = true; // set to false in constructor
	protected JPanel upDownButtonPanel;

	protected TreeBuilderPanel()
	{
	}

	public TreeBuilderPanel(Component treeTable)
	{
		layoutComponent(treeTable);
	}

	protected void layoutComponent(Component treeTable)
	{
		JScrollPane scrollPane = new JScrollPane(treeTable);
		scrollPane.getViewport().setBackground(Color.white);
		Dimension d = treeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));
		upDownButtonPanel = makeButtonPanel();
		JComponent[] comps = {scrollPane, upDownButtonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(0, 1, 0, 0), 0, 0)};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	protected JPanel makeButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		upButton = Templates.makeListArrowButton("up", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				moveUpAction();
			}
		});
		downButton = Templates.makeListArrowButton("down", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				moveDownAction();
			}
		});
		buttonPanel.add(upButton);
		buttonPanel.add(Box.createVerticalStrut(3));
		buttonPanel.add(downButton);
		return buttonPanel;
	}

	protected void setMoveEnabled(boolean newIsMoveEnabled)
	{
		if (isMoveEnabled == newIsMoveEnabled) return;
		isMoveEnabled = newIsMoveEnabled;
		if (isMoveEnabled) {
			upButton.setEnabled(true);
			downButton.setEnabled(true);
		} else { // disabled
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
	}

	protected abstract void moveUpAction();

	protected abstract void moveDownAction();

	protected String getNamesOfChildren(List children)
	{
		if (children == null || children.size() == 0) return "";
		if (children.size() == 1) {
			return getObjName(children.get(0));
		}
		if (children.size() == 2)
			return getObjName(children.get(0)) +
			        " and " + getObjName(children.get(1));
		// 3 or more objects
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < children.size() - 1; ++i) {
			sb.append(getObjName(children.get(i)) + ", ");
		}
		sb.append("and " + getObjName(children.get(children.size() - 1)));
		return sb.toString();
	}

	protected String getObjName(Object obj)
	{
		if (obj instanceof DomeObject)
			return ((DomeObject) obj).getName();
		return obj.toString();
	}

	protected static final String INVALID_PASTE = "cannot be pasted into";

	protected List filterForValidItems(Object m, List items)
	{
		ArrayList validItems = new ArrayList();
		ArrayList invalidItems = new ArrayList();
		Iterator it = items.iterator();

		if (m instanceof Parameter && ((Parameter) m).getDataObjectForType("List") != null) {
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof Parameter)
					validItems.add(obj);
				else
					invalidItems.add(obj);
			}
			if (!invalidItems.isEmpty()) {
				DefaultContextBuilder.showWarning(this, INVALID_PASTE, getNamesOfChildren(invalidItems),
				                                  getObjName(m));
			}
			return validItems;
		}
        if (m instanceof Visualization) {
		while (it.hasNext()) {
			Object obj = it.next();
			if ((obj instanceof Parameter) && ((((Parameter) obj).getDataObjectForType("Vector") != null) || (((Parameter) obj).getDataObjectForType("Matrix") != null)) ) {

				validItems.add(obj);

			} else
				invalidItems.add(obj);
		}
		if (!invalidItems.isEmpty()) {
			DefaultContextBuilder.showWarning(this, "cannot be pasted into", getNamesOfChildren(invalidItems)
			);
		}

		return validItems;
        }
		while (it.hasNext()) {
			Object obj = it.next();
			if ((obj instanceof ModelObject) && (((ModelObjectScope) m).isValidModelObjectType((ModelObject) obj)))
				validItems.add(obj);
			else
				invalidItems.add(obj);
		}
		if (!invalidItems.isEmpty()) {
			DefaultContextBuilder.showWarning(this, INVALID_PASTE, getNamesOfChildren(invalidItems),
			                                  getObjName(m));
		}
		return validItems;
	}

}
