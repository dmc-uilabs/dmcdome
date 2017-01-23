// FilterRunPanel.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 11, 2003
 * Time: 2:47:29 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.guiutils.tree.run.RunTree;
import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class FilterRunPanel extends AbstractDomeObjectGui
{

	protected static GridBagConstraints gbc;

	protected NameTextField nameField;
	protected Filter filter;
	protected RunTree m_tree;
	protected JPanel filterViewsPanel;
	protected CardLayout2 filterViewsCards;
	protected DefaultComboBoxModel cbModel;
	protected JComboBox viewComboBox;

	public FilterRunPanel(Filter filter)
	{
		super(filter);
		if (filter == null)
			throw new NullPointerException("FilterBuildPanel - null filter");
		this.filter = filter;
		layoutComponent();
	}

	protected void layoutComponent()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(filter);
		nameField.setEditable(false);
		nameField.setCurrent();

		m_tree = new RunTree(filter);
		RunTreeTable treeTable = new RunTreeTable(m_tree);
		treeTable.setEnabled(false);
		JScrollPane scrollPane = new JScrollPane(treeTable);
		scrollPane.getViewport().setBackground(Color.white);
		Dimension d = treeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));

		JComponent[] comps = {Templates.makeLabel("name:"),
		                      nameField,
		                      scrollPane};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	public String getTitlePrefix()
	{
		return "Filter: ";
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		MenuManager.setContext(ModeContexts.RUN_MODE);
	}

	public String getMenuContext()
	{
		return ModeContexts.RUN_MODE;
	}

}