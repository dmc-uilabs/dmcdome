// ModelViewBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.modelinterface.build;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.BuildContextTree;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.ObjectTree;
import mit.cadlab.dome3.swing.tree.TreeHistoryEvent;
import mit.cadlab.dome3.swing.tree.TreeHistoryListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: May 7, 2003
 * Time: 10:23:44 AM
 * To change this template use Options | File Templates.
 */
public class ModelViewBuildPanel extends JPanel implements DomeGui
{
	public static final String WINDOWCLOSED = "BuildwindowClosed";
	public static final String TITLEPREF = "Model View for ";
	public static final Dimension DEFAULTSIZE = new Dimension(400, 300);
	protected static GridBagConstraints gbc;
	protected static Color notEditableColor = new Color(105, 105, 105);
	protected ContextTreeBuilderPanel modelViewPanel;
	protected JButton backButton;
	protected boolean isBackButtonEnabled = false;
	protected NameTextField nameField;
	protected JButton closeButton;

	protected ModelInterface mInterface;

	public ModelViewBuildPanel(ModelInterface mInterface)
	{
		this.mInterface = mInterface;
		createComponents();
		this.setPreferredSize(DEFAULTSIZE);
	}

	protected void createComponents()
	{
		DefaultContextBuilder cBuilder = (DefaultContextBuilder) ((DomeModelInterface) mInterface).getModelContext();
		modelViewPanel = new ContextTreeBuilderPanel(cBuilder);
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
		BuildContextTree contextTree = modelViewPanel.getContextTree();
		contextTree.addHistoryListener(new ModelViewBuildPanel.InterfaceTreeHistoryListener());
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
				Window w = BuildFocusTracker.getCurrentWindow();
				((DomeBuildFrame)w).selfClose();
				ModelViewBuildPanel.this.close();
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
					nameField.setEditable(true);
					nameField.setForeground(Color.black);
					break;
				case TreeHistoryEvent.EVENT_QUEUE_EMPTY:
					backButton.setEnabled(false);
					isBackButtonEnabled = false;
					nameField.setEditable(false);
					nameField.setForeground(notEditableColor);
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
		MenuManager.setContext(getMenuContext());
		BuildFocusTracker.notifyInFocus(this, mInterface);
			modelViewPanel.setEditMenusForSelection(
			        ModeContexts.BUILD_DOMEMODEL_INTERFACE_MODELVIEW,
			        ((DomeModelInterface) mInterface).isDefaultInterface());
	}

	public String getMenuContext()
	{
		Model m = mInterface.getModel();
		if (!(m instanceof IntegrationProject)) {
			if (((DomeModel) m).isIntegrationModel()) {
				//set project menu
					return ModeContexts.BUILD_PROJECT_INTERFACE_MODELVIEW;
			}
			else if (m instanceof PluginModel) {
					return ModeContexts.BUILD_PLUGINMODEL_INTERFACE_MODELVIEW;
			}
			else {
					return ModeContexts.BUILD_DOMEMODEL_INTERFACE_MODELVIEW;
 			}
		}
		else { //project interface
			return ModeContexts.BUILD_PROJECT_INTERFACE;
		}
	}

	public Object getGuiObject()
	{
		return mInterface;
	}

	public void close()
	{
		firePropertyChange(WINDOWCLOSED, null, null);
	}

	ContextTreeBuilderPanel getModelViewPanel() {
		return modelViewPanel;
	}

	protected void setView() {
		 if(mInterface instanceof ModelInterfaceBuilder)
		    ((ModelInterfaceBuilder)mInterface).setCurrentView(DomeModelInterface.MODEL_VIEW);
		 else if (mInterface instanceof SubscriptionInterface)
			 ((SubscriptionInterface) mInterface).setCurrentView(DomeModelInterface.MODEL_VIEW);
	}
}


