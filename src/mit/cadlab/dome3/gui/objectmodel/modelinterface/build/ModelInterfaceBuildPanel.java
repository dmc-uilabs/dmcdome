// DomeModelBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.modelinterface.build;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGUIComboBoxModel;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiChooser;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiPicker;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiUtils;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.AbstractDomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.project.AbstractIntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.WindowTracker;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

public class ModelInterfaceBuildPanel extends AbstractDomeObjectGui
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("ModelInterfaceBuildPanel");
	public static final String XML_TAG = "modelinterfacebuildpanel";
	public static final String PARTNAME = ", for ";

	protected static GridBagConstraints gbc;
	protected ModelInterface mInterface;
	protected NameTextField nameField;
	protected JComboBox guiSelComboBox;
	protected DefaultComboBoxModel cbModel;
	protected JTabbedPane contentTabs;
	protected ModelInterfaceDefinitionBuildPanel defPanel;
	protected JButton modelViewButton;
	protected DomeBuildFrame modelviewDialog;
	protected boolean isModelviewDialogOPen;
	protected DocumentationBuildPanel docPanel;
	protected CardLayout2 definitionPanelCards;
	protected JPanel definitionPanel;
	protected HashMap GuiToComboBoxMap = new HashMap();//key: filedata of comboboxmodel, value, gui card. except DEFAULT

	public ModelInterfaceBuildPanel(ModelInterface mi)
	{
		super(mi);
		this.mInterface = mi;
		createComponents();
	}

	protected void createComponents()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(mInterface);
		NameListener ifaceNameListener = new NameListener()
		{
			public void nameChanged(String newName)
			{
				Container cont = getTopLevelAncestor();
				((DomeBuildFrame) cont).setTitle(getTitlePrefix() + newName +
				                                 PARTNAME + mInterface.getModel().getName());
			}
		};

		mInterface.addPropertyChangeListener(NameListener.NAME, ifaceNameListener);
		NameListener modelNameListener = new NameListener()
		{
			public void nameChanged(String newName)
			{
				Container cont = getTopLevelAncestor();
				((DomeBuildFrame) cont).setTitle(getTitlePrefix() + mInterface.getName() +
				                                 PARTNAME + newName);
			}
		};
		mInterface.getModel().addPropertyChangeListener(NameListener.NAME, modelNameListener);

		//get custom gui put them into combobox
		cbModel = new CustomGUIComboBoxModel(mInterface, true);
		guiSelComboBox = Templates.makeDComboBox(cbModel);
		guiSelComboBox.addItemListener(
		        new ItemListener()
		        {
			        public void itemStateChanged(ItemEvent e)
			        {

				        if (guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.CHANGE_ADD)) { //change
					        CustomGuiChooser.showDialog(ModelInterfaceBuildPanel.this, mInterface);
					        guiSelComboBox.setModel(new CustomGUIComboBoxModel(mInterface, true));
				        }
				        else if (guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.CHANGE_EDIT)) { //change
					        //customGuiEditor.showDialog(ModelInterfaceBuildPanel.this, mInterface);
                            CustomGuiPicker.showDialog(ModelInterfaceBuildPanel.this, mInterface);
					        guiSelComboBox.setModel(new CustomGUIComboBoxModel(mInterface, true));
				        }
				     //   else if (guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.CHANGE_DEL)) { //change
					  //      GuiRemover.showDialog(ModelInterfaceBuildPanel.this, mInterface);
					  //      guiSelComboBox.setModel(new CustomGUIComboBoxModel(mInterface, true));
				      //  }
				        else if (guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.DEFAULT)) {
					        definitionPanelCards.show(definitionPanel, CustomGUIComboBoxModel.DEFAULT);
				        } else {
					        CustomGuiInfo item = (CustomGuiInfo) guiSelComboBox.getSelectedItem();
					        definitionPanelCards.show(definitionPanel, item.toString());
				        }
			        }
		        }
		);
		mInterface.addPropertyChangeListener(AbstractDomeModelInterface.CUSTOMGUICHANGE, new customGuiListener());
		contentTabs = Templates.makeTabbedPane();
		defPanel = new ModelInterfaceDefinitionBuildPanel(mInterface);
		docPanel = new DocumentationBuildPanel(mInterface.getDocumentation());

		definitionPanelCards = new CardLayout2();
		definitionPanel = new JPanel();
		definitionPanel.setLayout(definitionPanelCards);
		definitionPanel.add(CustomGUIComboBoxModel.DEFAULT, defPanel);
		addAllCustomGUIs();
		contentTabs.addTab("definition", definitionPanel);
		//contentTabs.addTab("gui files",new JPanel());
		contentTabs.addTab("documentation", docPanel);
		contentTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				setMenuContext();
			}
		});

		if((mInterface instanceof ModelInterfaceBuilder &&
		        !(mInterface.getModel() instanceof IntegrationProject))) {
			createModelView();
		}
		else if (mInterface instanceof SubscriptionInterface) {
			createModelView();
		}

		layoutComponent();
	}


	private void createModelView() {
		ImageIcon modelIcon = Templates.makeImageIcon(DomeIcons.MODEL);
		modelViewButton = Templates.makeImageButton(modelIcon);
		if (mInterface instanceof ModelInterfaceBuilder && ((ModelInterfaceBuilder) mInterface).isDefaultInterface()) {
			//InterfaceModelView will contain modelViewContext so button should not be enabled if size is 1
			if (((ModelInterfaceBuilder) mInterface).getModelViewObjects().size() <= 1) {
				modelViewButton.setEnabled(false);
			} else {
				modelViewButton.setEnabled(true);
			}
		} else {
			modelViewButton.setEnabled(true);
		}
		if(mInterface instanceof ModelInterfaceBuilder)   {
			((ModelInterfaceBuilder) mInterface).addDModelViewDListListener(new DListListener()
			{
				public void intervalAdded(DListEvent e)
				{
					changestatus();
				}

				public void intervalRemoved(DListEvent e)
				{
					changestatus();
				}

				public void itemsAdded(DListEvent e)
				{
					intervalAdded(e);
				}

				public void itemsRemoved(DListEvent e)
				{
					intervalRemoved(e);
				}

				public void intervalChanged(DListEvent e)
				{
				}

				public void itemsReplaced(DListEvent e)
				{
				}

				private void changestatus()
				{
					int numModelViewObjects = ((ModelInterfaceBuilder) mInterface).getModelViewObjects().size();
					if (numModelViewObjects == 0) {
						modelViewButton.setEnabled(false);
					} else {
						modelViewButton.setEnabled(true);
					}
				}
			});
		}
		modelViewButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//show the interface model view in a spearate frame
				if (!isModelviewDialogOPen) {
					final ModelViewBuildPanel modelPanel = new ModelViewBuildPanel(mInterface);
					WindowTracker ifaceFrame = (WindowTracker) BuildFocusTracker.getCurrentWindow();
					modelviewDialog = new DomeBuildFrame(modelPanel, ifaceFrame);
					modelviewDialog.addWindowListener(new WindowAdapter()
					{
						public void windowActivated(WindowEvent e)
						{
							modelPanel.setView();
						}
					});
					modelPanel.addPropertyChangeListener(new PropertyChangeListener()
					{
						public void propertyChange(PropertyChangeEvent e)
						{
							if (e.getPropertyName().equals(ModelViewBuildPanel.WINDOWCLOSED)) {
								isModelviewDialogOPen = false;
							}
						}
					});
					isModelviewDialogOPen = true;
					modelviewDialog.show();
				} else {
					isModelviewDialogOPen = true;
					modelviewDialog.show();
				}
			}
		});
	}


	protected void layoutComponent()
	{
		JComponent[] comps = {makeControlPanel(), contentTabs};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = null;
		GridBagConstraints[] gbcs = null;
       //for subscription interface or model interface
		if(mInterface.getModel() instanceof DomeModel) {
			comps = new JComponent[] { Templates.makeLabel("name:"),
								        nameField,
			                           modelViewButton,
			                           Templates.makeLabel("graphical interface:"), guiSelComboBox
			};
			// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
			 gbcs = new GridBagConstraints[] {
				new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
				new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
				new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 5), 0, 0),
				new GridBagConstraints(3, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 5), 0, 0),
				new GridBagConstraints(4, 0, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)

			};
		}
		else if(mInterface.getModel() instanceof IntegrationProject)  {
			comps = new JComponent[] { Templates.makeLabel("name:"),
								  nameField, Templates.makeLabel("graphical interface:"), guiSelComboBox
			};
			// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
			 gbcs = new GridBagConstraints[] {
				new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
				new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
				new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 30, 0, 5), 0, 0),
				new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
			};
		}
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public void addNotify()
	{
		super.addNotify();
		//To switch between model view window and interface window
		if(mInterface instanceof ModelInterfaceBuilder)	{
			Window w = SwingUtilities.windowForComponent(this);
			w.addWindowListener(new WindowAdapter() {
				public void windowActivated(WindowEvent e) {
	                String view = ((ModelInterfaceBuilder)mInterface).getCurrentView();
					if(view.equals(DomeModelInterface.MODEL_VIEW)) {
						defPanel.restoreView();
						defPanel.switchView();
					}
				}
			});
		}
	}

	// DomeObjectGui interface
	public String getTitlePrefix()
	{
		return "Interface: ";
	}

	public String getTitle()
	{
		return getTitlePrefix() + mInterface.getName() + PARTNAME + mInterface.getModel().getName();
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		switch (contentTabs.getSelectedIndex()) {
			case 0: // definition
				defPanel.setMenuContext();
				return;
			case 1: // documentation
                if(mInterface.getModel() instanceof AbstractIntegrationProject) {
                    MenuManager.setContext(ModeContexts.BUILD_PROJECT_DOCUMENTATION);
		        }
				else if (((DomeModel) mInterface.getModel()).isIntegrationModel())
					MenuManager.setContext(ModeContexts.BUILD_PROJECT_DOCUMENTATION);
				else
					MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL_DOCUMENTATION);
				break;
			default: // default for other tabs
                if(mInterface.getModel() instanceof AbstractIntegrationProject) {
                    MenuManager.setContext(ModeContexts.BUILD_PROJECT);
		        }
				else if (((DomeModel) mInterface.getModel()).isIntegrationModel())
					MenuManager.setContext(ModeContexts.BUILD_PROJECT);
				else
					MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL);
		}
		BuildFocusTracker.notifyInFocus(this, mInterface);
	}

	public void close()
	{
		// unhook guis
	}


	public void addAllCustomGUIs()
	{
		ArrayList files = mInterface.getCustomGUIList();
		for (int i = 0; i < files.size(); i++) {
			addCustomGUIPanel((CustomGuiInfo) files.get(i));
		}
	}

	public void addCustomGUIPanel(CustomGuiInfo file)
	{
		JComponent customGui = CustomGuiUtils.createCustomGui(file, mInterface);
		definitionPanel.add(file.toString(), customGui);
		GuiToComboBoxMap.put(file, customGui);
	}

	protected class customGuiListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			Object newValue = evt.getNewValue();
			Object oldValue = evt.getOldValue();
			if (oldValue == null)//add
			{
				CustomGuiInfo file = (CustomGuiInfo) newValue;
				addCustomGUIPanel(file);
			} else if (newValue == null)//remove
			{
				CustomGuiInfo file = (CustomGuiInfo) oldValue;
				JComponent gui = (JComponent) GuiToComboBoxMap.get(file);
				definitionPanel.remove(gui);
				GuiToComboBoxMap.remove(file);
			}
            else{//edit
                CustomGuiInfo file = (CustomGuiInfo) oldValue;
                JComponent gui = (JComponent) GuiToComboBoxMap.get(file);
				definitionPanel.remove(gui);
				GuiToComboBoxMap.remove(file);
                addCustomGUIPanel(file);
            }
		}
	}
}
