// ContextRunPanel.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 10, 2003
 * Time: 10:05:25 AM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.run;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContext;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class ContextRunPanel extends AbstractDomeObjectGui
{

	protected static GridBagConstraints gbc;
	//protected static Dimension DEFAULT_SIZE = new Dimension(400,300);
	protected DefaultContext context;
	protected NameTextField nameField;
	protected JTabbedPane contentTabs;
	protected ContextDefinitionRunPanel defPanel;
	protected DocumentationRunPanel docPanel;
	protected static final String parentModeContext = ModeContexts.RUN_MODE;

	public ContextRunPanel(DefaultContext context)
	{
		super(context);
		this.context = context;
		createComponents();
	}

	protected void createComponents()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(context);
		nameField.setEditable(false);
		contentTabs = Templates.makeTabbedPane();
		defPanel = new ContextDefinitionRunPanel(context);
		docPanel = new DocumentationRunPanel(context.getDocumentation());
		contentTabs.addTab("definition", defPanel);
		contentTabs.addTab("documentation", docPanel);
		layoutComponent();
	}

	protected void layoutComponent()
	{
		JComponent[] comps = {makeControlPanel(), contentTabs};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {Templates.makeLabel("name:"), nameField};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	// DomeGui interface
	public String getTitlePrefix()
	{
		return "Context: ";
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		MenuManager.setContext(parentModeContext);
	}

}
