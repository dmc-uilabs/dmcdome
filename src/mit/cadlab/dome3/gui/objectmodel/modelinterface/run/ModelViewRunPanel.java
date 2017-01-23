// ModelViewRunPanel.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 5, 2003
 * Time: 2:25:53 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelinterface.run;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.ContextTreePanel;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.run.RunContextTree;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.ObjectTree;
import mit.cadlab.dome3.swing.tree.TreeHistoryEvent;
import mit.cadlab.dome3.swing.tree.TreeHistoryListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ModelViewRunPanel extends JPanel implements DomeGui
{
	public static final String WINDOWCLOSED = "windowClosed";
	public static final String TITLEPREF = "Model View for ";
	public static final Dimension DEFAULTSIZE = new Dimension(400, 300);
	protected static GridBagConstraints gbc;
	protected static Color notEditableColor = new Color(105, 105, 105);
	protected ContextTreePanel modelViewPanel;
	protected JButton backButton;
	protected boolean isBackButtonEnabled = false;
	protected NameTextField nameField;
	protected JButton closeButton;
	protected String parentModeContext = ModeContexts.RUN_MODE;       //todo check if this is right

	protected ModelInterface mInterface;

	public ModelViewRunPanel(ModelInterface mInterface)
	{
		this.mInterface = mInterface;
		createComponents();
		this.setPreferredSize(DEFAULTSIZE);
	}

	protected void createComponents()
	{
		DefaultContextBuilder cBuilder = (DefaultContextBuilder) ((DomeModelInterface) mInterface).getModelContext();
		modelViewPanel = new ContextTreePanel(cBuilder, false);
		modelViewPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		backButton = Templates.makeImageButton("mit/cadlab/dome3/icons/backArrow16.gif");
		backButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				modelViewPanel.getContextTree().setRootContextBack();
				nameField.setForeground(Color.BLACK);
			}
		});
		backButton.setEnabled(false);

		nameField = new NameTextField();
		nameField.setForeground(Color.BLACK);
		nameField.setDisabledTextColor(notEditableColor);
		nameField.setDomeObject(modelViewPanel.getContextTree().getRootContext());
		nameField.setEditable(false);

		// associate backbutton and namefield with tree root and history
		RunContextTree contextTree = modelViewPanel.getContextTree();
		contextTree.addHistoryListener(new InterfaceTreeHistoryListener());
		contextTree.addPropertyChangeListener(ObjectTree.ROOT_PROPERTY, new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String property = e.getPropertyName();
				if (property.equals(ObjectTree.ROOT_PROPERTY)) {
					nameField.setDomeObject(modelViewPanel.getContextTree().getRootContext());
				}
			}
		});

		closeButton = Templates.makeButton("close");
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ModelViewRunPanel.this.closeGUI();
			}
		});

		layoutComponent();
	}

	protected void layoutComponent()
	{
		JComponent[] comps = {makeControlPanel(), modelViewPanel, closeButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {backButton,
		                      nameField,
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	protected class InterfaceTreeHistoryListener implements TreeHistoryListener
	{
		public void historyChanged(TreeHistoryEvent event)
		{
			switch (event.getType()) {
				case TreeHistoryEvent.EVENT_QUEUE_NONEMPTY:
					backButton.setEnabled(true);
					isBackButtonEnabled = true;
					break;
				case TreeHistoryEvent.EVENT_QUEUE_EMPTY:
					backButton.setEnabled(false);
					isBackButtonEnabled = false;
			}
		}
	}

	// DomeGui interface

	public String getTitle()
	{
		return TITLEPREF + mInterface.getName();
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		MenuManager.setContext(parentModeContext);
	}

	public String getMenuContext()
	{
		return parentModeContext;
	}

	public Object getGuiObject()
	{
		return mInterface;
	}

	public void closeGUI()
	{
		DomeRunFrame frame = (DomeRunFrame) RunFocusTracker.getCurrentWindow();
		if (frame instanceof DomeRunFrame) {
			frame.close();
		}
	}

	public void close()
	{
		MenuManager.setContext(parentModeContext);
		firePropertyChange(WINDOWCLOSED, null, null);
	}

}

