// ModelInterfaceRunPanel.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Mar 30, 2003
 * Time: 2:17:36 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelinterface.run;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.DomeClientApplet;
import mit.cadlab.dome3.gui.guiutils.customGui.*;
import mit.cadlab.dome3.gui.guiutils.msg.MessageLogDialog;
import mit.cadlab.dome3.gui.guiutils.msg.MessageLogDialogLogHandler;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.AbstractDomeModelInterface;
import org.exolab.ID.UUIDGenerator;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.LayeredCenterLayout;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.WindowTracker;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

public class ModelInterfaceRunPanel extends AbstractDomeObjectGui
{

	public static final String EMPTYSTRING = "";
	public static final String STATUS = "status: ";
	protected ModelInterfaceRuntimeClient mInterface;

	protected static GridBagConstraints gbc;
	public static final String PARTNAME = "Interface: ";

	protected NameTextField nameField;
	protected JButton modelViewButton;
	protected DomeRunFrame modelviewDialog;
	protected boolean isModelviewDialogOPen;
	protected JComboBox guiSelComboBox;
	protected DefaultComboBoxModel cbModel;
	//protected String[] guisel = new String[]{"Dome Default", "add..."};
	protected JButton messageLogButton;
	protected MessageLogDialog messageLog = null;

	protected JTabbedPane contentTabs;
	protected ModelInterfaceDefinitionRunPanel defPanel;
	protected DocumentationRunPanel docPanel;

	protected JToggleButton stopButton;
	protected JButton submitButton;
	protected JLabel statusLabel;
	protected JProgressBar progressBar;

	protected JButton killModelButton;

	private JButton playPauseButton;
	private ImageIcon runIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/run/running.gif");
	private ImageIcon pauseIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/run/pause.gif");
	private ImageIcon runDownIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/run/runningDown.gif");
	private ImageIcon pauseDownIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/run/pauseDown.gif");
	private boolean runIconShowing = true;
    private PropertyChangeListener statusListener;
	protected CardLayout2 definitionPanelCards;
	protected JPanel definitionPanel;
	protected HashMap GuiToComboBoxMap = new HashMap();//key: filedata of comboboxmodel, value, gui card. except DEFAULT

	public ModelInterfaceRunPanel(ModelInterfaceRuntimeClient mi)
	{
		super(mi);
		this.mInterface = mi;
		createComponents();
		if (mInterface instanceof ModelInterfaceRuntimeClient) {
			ModelInterfaceRuntimeClient iface = (ModelInterfaceRuntimeClient) mInterface;
			statusListener = new StatusChangeListener();
			iface.addPropertyChangeListener(statusListener);
		}
		if (!mInterface.isLoaded()) {
			mInterface.synchronizeInterfaceState();
		}
	}


	public void close ()
	{
		mInterface.removePropertyChangeListener(statusListener);
		DomeRunFrame ifaceFrame = (DomeRunFrame)SwingUtilities.windowForComponent(this);
		if (ifaceFrame.isTopLevelRunFrame())
			RuntimeFunctionsClient.killInterfaceParent(mInterface.getServerConnection(), mInterface.getRuntimeId());
	}

	protected void createComponents()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(mInterface);
		nameField.setEditable(false);

		if(!((ModelInterfaceRuntimeClient)mInterface).isProjectinterface())
		{
			ImageIcon modelIcon = Templates.makeImageIcon(DomeIcons.MODEL);
			modelViewButton = Templates.makeImageButton(modelIcon);
			int numModelViewObjects = 0;
			if (mInterface instanceof ModelInterfaceRuntimeClient) {
				ModelInterfaceRuntimeClient iface = (ModelInterfaceRuntimeClient) mInterface;
				numModelViewObjects = iface.getModelViewObjects().size();
			}
			if (numModelViewObjects == 0) {
				modelViewButton.setEnabled(false);
			} else {
				modelViewButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						//show the interface model view in a spearate frame
						if (!isModelviewDialogOPen) {
							final ModelViewRunPanel modelPanel = new ModelViewRunPanel(mInterface);
							modelviewDialog = new DomeRunFrame(modelPanel,
							                                   (WindowTracker)SwingUtilities.windowForComponent(ModelInterfaceRunPanel.this));
							modelPanel.addPropertyChangeListener(new PropertyChangeListener()
							{
								public void propertyChange(PropertyChangeEvent e)
								{
									if (e.getPropertyName().equals(ModelViewRunPanel.WINDOWCLOSED)) {
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
		}
		cbModel = new CustomGUIComboBoxModel(mInterface, false);
		guiSelComboBox = Templates.makeDComboBox(cbModel);
        guiSelComboBox.addItemListener(
		        new ItemListener()
		        {
			        public void itemStateChanged(ItemEvent e)
			        {
				        if (guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.DEFAULT)) {
					        definitionPanelCards.show(definitionPanel, CustomGUIComboBoxModel.DEFAULT);
				        }
				        else {
					        CustomGuiInfo item = (CustomGuiInfo) guiSelComboBox.getSelectedItem();
					        definitionPanelCards.show(definitionPanel, item.toString());
				        }
				        setIfGraphShouldPaint();
			        }
		        }
		);
		mInterface.addPropertyChangeListener(AbstractDomeModelInterface.CUSTOMGUICHANGE, new customGuiListener());

		messageLogButton = Templates.makeButton("message log", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				messageLog.show();
			}
		});

		contentTabs = Templates.makeTabbedPane();
		defPanel = new ModelInterfaceDefinitionRunPanel(mInterface);
		docPanel = new DocumentationRunPanel(mInterface.getDocumentation());

		definitionPanelCards = new CardLayout2();
		definitionPanel = new JPanel();
		definitionPanel.setLayout(definitionPanelCards);
		definitionPanel.add(CustomGUIComboBoxModel.DEFAULT, defPanel);
		addAllCustomGUIs();

		contentTabs.addTab("definition", definitionPanel);
		contentTabs.addTab("documentation", docPanel);
		contentTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				setIfGraphShouldPaint();
				setMenuContext();
				ModelInterfaceRunPanel.this.repaint();
			}
		});

		layoutComponent();

        if(cbModel.getSize()>=2)  guiSelComboBox.setSelectedIndex(1);
	}

	protected void layoutComponent()
	{
		this.setLayout(new LayeredCenterLayout());
		add(makeButtonPanel());
		add(makeMainPanel());
	}

	protected JPanel makeMainPanel()
	{
		JPanel p = new JPanel();
		contentTabs.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0),
		                                                         contentTabs.getBorder()));
		JComponent[] comps = {makeControlPanel(), contentTabs};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(p, comps, gbcs);
		return p;
	}

	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = null;
		GridBagConstraints[] gbcs = null;
		if (!((ModelInterfaceRuntimeClient) mInterface).isProjectinterface())
		{
			comps = new JComponent[] {Templates.makeLabel("name:"),
								  nameField,
								  modelViewButton,
								  guiSelComboBox,
								  messageLogButton
			};
			// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
			gbcs = new GridBagConstraints[] {
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
				new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0),
				new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
				new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
				new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
			};
		}
		else {
			comps = new JComponent[]{Templates.makeLabel("name:"),
			                         nameField,
			                         guiSelComboBox,
			                         messageLogButton
			};
			// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
			gbcs = new GridBagConstraints[]{
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
				new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0),
				new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
				new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
			};
		}
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JPanel makeButtonPanel()
	{
		JPanel p = new JPanel();
		p.setOpaque(false);

		playPauseButton = Templates.makeImageButton(runIcon);
		playPauseButton.setPressedIcon(runDownIcon);
		playPauseButton.setOpaque(false);
		playPauseButton.setBorderPainted(false);
		playPauseButton.setEnabled(false);
		playPauseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				doPlayPauseAction();
			}
		});

		submitButton = Templates.makeButton("submit", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				doSubmitAction();
			}
		});
		if (mInterface.isInterfaceConsistent())
			disableSubmit();
		else
			enableSubmit();

		defPanel.disablePlayPauseKillActions();

		JPanel compPanel = new JPanel();
		compPanel.setOpaque(false);
		//JLabel statusLabel = Templates.makeLabel("");	//just a filler - actual status in  defPanel
		JComponent[] comps = {makeStatusPanel(), submitButton, playPauseButton};// killModelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(2, 5, -5, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(2, 5, 5, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(2, 5, 5, 5), 0, 0),
		};
		Templates.layoutGridBag(compPanel, comps, gbcs);

		JPanel filler1 = new JPanel();
		filler1.setOpaque(false);
		JComponent[] comps1 = {filler1, compPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs1 = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0), // center filler
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps1, gbcs1);

		return p;
	}

	private JPanel makeStatusPanel()
	{
		progressBar = new JProgressBar(0, 50);
		progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(100, 8));

		progressBar.setForeground(Templates.DARKER_BACKGROUND_COLOR);
		statusLabel = Templates.makeLabel("");
		JPanel barHolder = new JPanel();
		JComponent[] barComp = {progressBar};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] barGbcs = {// 25 inset
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(barHolder, barComp, barGbcs);

		JPanel statusPanel = new JPanel();
		JComponent[] statusComps = {statusLabel, barHolder};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] statusGbcs = {// 25 inset
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};
		Templates.layoutGridBagB(statusPanel, statusComps, statusGbcs);
		return statusPanel;
	}

	public void doSubmitAction()
	{
		if (mInterface.isInterfaceConsistent())
			return;
		disableSubmit();
		mInterface.submitChanges();
	}

	private void doSubmitButtonConfiguration() {
		defPanel.enablePlayPauseKillActions();
		playPauseButton.setEnabled(true);
		runIconShowing = false;
		playPauseButton.setIcon(pauseIcon);
		playPauseButton.setPressedIcon(pauseDownIcon);
	}

	public void doPlayPauseAction()
	{
		if (runIconShowing) {
			runIconShowing = false;
			playPauseButton.setIcon(pauseIcon);
			playPauseButton.setPressedIcon(pauseDownIcon);
			if (mInterface instanceof ModelInterfaceRuntimeClient) {
				((ModelInterfaceRuntimeClient) mInterface).resumeSolving();
			}
		} else {
			runIconShowing = true;
			playPauseButton.setIcon(runIcon);
			playPauseButton.setPressedIcon(runDownIcon);
			if (mInterface instanceof ModelInterfaceRuntimeClient) {
				((ModelInterfaceRuntimeClient) mInterface).pauseSolving();
			}
		}
		disableSubmit();
	}

	protected void createMessageLog()
	{
		messageLog = new MessageLogDialog(this);
		messageLog.addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				MenuManager.setContext(ModeContexts.RUN_DOMEMODEL_INTERFACE);
			}
		});
		mInterface.setLogHandler(new MessageLogDialogLogHandler(mInterface, messageLog));
		//to direct messages to correct message log area
		BuildFocusTracker.notifyInFocus(this, (ModelComponent) mInterface);
	}

	public void addNotify()
	{
		super.addNotify();
		createMessageLog(); // at this time, frame will be available
	}

	// DomeObjectGui interface
	public String getTitlePrefix()
	{
		return PARTNAME;
	}

	public String getTitle()
	{
		return mInterface.getName();
	}


	public String getHelpContext()
	{
		return null;
	}

	protected void setIfGraphShouldPaint() {
		switch (contentTabs.getSelectedIndex()) {
			case 0: // definition
				defPanel.setIsDefinitionPanelOnTop(guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.DEFAULT));
				return;
			default: // default for other tabs
				defPanel.setIsDefinitionPanelOnTop(false);
		}
	}

	public void setMenuContext()
	{
		MenuManager.setContext(ModeContexts.RUN_DOMEMODEL_INTERFACE);
		//to direct messages to correct message log area
		BuildFocusTracker.notifyInFocus(this, (ModelComponent) mInterface);
	}

	public String getMenuContext()
	{
		return ModeContexts.RUN_DOMEMODEL_INTERFACE;
	}

	public ModelInterface getModelInterface()
	{
		return mInterface;
	}


	private void enableSubmit()
	{
		submitButton.setEnabled(true);
		defPanel.enableSubmitAction();
	}

	private void disableSubmit()
	{
		submitButton.setEnabled(false);
		defPanel.disableSubmitAction();
	}

	protected void setStatusText(String text) {
		statusLabel.setText(text);
	}

	protected void startProgressBar() {
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);

	}

	protected void stopProgressBar() {
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
	}

	protected void pauseProgressBar() {
		progressBar.setIndeterminate(false);

	}

	class StatusChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent pe)
		{
			String propName = pe.getPropertyName();
			if (propName.equals(ModelInterfaceRuntimeClient.INTERFACEMODELSTATUS)) {
				String newStatus = (String) pe.getNewValue();
				if (ModelRuntime.STATUS_INCONSISTENT.equals(newStatus)) {
					enableSubmit();
				} else if (newStatus.equals(EMPTYSTRING)) {
					setStatusText(newStatus);
				} else {
					setStatusText(STATUS + newStatus);
					if (newStatus.equals(ModelRuntime.STATUS_DONE) || ModelRuntime.STATUS_ABORTED.equals(newStatus)) {
						disableSubmit();
						stopProgressBar();
						defPanel.disablePlayPauseKillActions();
						playPauseButton.setEnabled(false);
					} else if (newStatus.equals(ModelInterfaceRuntimeClient.SUBMITTING_CHANGES)) {
						disableSubmit();
						startProgressBar();
					} else if (newStatus.equals(ModelRuntime.STATUS_RUNNING)) {
						doSubmitButtonConfiguration();
						if (!progressBar.isVisible()) { // items not submitted in this interface
							disableSubmit();
							startProgressBar();
						}
						defPanel.enablePlayPauseKillActions();
						playPauseButton.setEnabled(true);
					} else if (newStatus.equals(ModelRuntime.STATUS_PAUSED)) {
						pauseProgressBar();
					} else if (newStatus.equals(ModelRuntime.STATUS_IFACE_PARENT_STARTING) ||
					        newStatus.equals(ModelRuntime.STATUS_IFACE_STARTING)) {
						startProgressBar();
					} else if (newStatus.equals(ModelRuntime.STATUS_IFACE_CREATED)) {
						if (!mInterface.areChangesWaiting())
							stopProgressBar();
					}
				}
			} else if (propName.equals(ModelInterfaceRuntimeClient.INTERFACEINPUT)) {
				enableSubmit();
			}
		}
	}

	public static void main(String[] args)
	{
		DomeInit.initializeDOME();
		ModelInterfaceRuntimeClient iface = new ModelInterfaceRuntimeClient(UUIDGenerator.create());
		ModelInterfaceRunPanel runPanel = new ModelInterfaceRunPanel(iface);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(runPanel);
		frame.pack();
		frame.setVisible(true);
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
		ServerConnection svrConn = null;
		if (mInterface instanceof ModelInterfaceRuntimeClient) {
            svrConn = ((ModelInterfaceRuntimeClient) mInterface).getServerConnection();
		}
		//should created by server
		JComponent customGui = CustomGuiUtils.createCustomGuiFromRemote(file, mInterface, svrConn);
        if(customGui instanceof JPanel) definitionPanel.setMinimumSize(customGui.getPreferredSize());
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
		}


	}
}

