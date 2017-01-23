package mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.gui.guiutils.customGui.*;
import mit.cadlab.dome3.gui.guiutils.msg.MessageLogDialog;
import mit.cadlab.dome3.gui.guiutils.msg.MessageLogDialogLogHandler;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.AnalysisToolInterfaceRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationInterfaceResultsPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationToolInterfaceDefinitionRunPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.toolinterface.AbstractAnalysisToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.LayeredCenterLayout;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.tool.AnalysisToolUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 27, 2003
 * Time: 11:06:56 PM
 * To change this template use Options | File Templates.
 */
public class OptimizationToolInterfaceRunPanel extends AnalysisToolInterfaceRunPanel
{
    public static final String EMPTYSTRING = "";
    public static final String STATUS = "status: ";
	public static final String PART_OF_NAME = "Analysis Tool Interface: ";
    public static final String RESULTS = "results";
    public static final String DEFINITION = "definition";
    public static final String DOCUMENTATION = "documentation";

    public static final String MESSAGE_LOG = "message log";

    private static final GridBagConstraints gbc = null;

    protected NameTextField _nameField;
    protected JButton _messageLogButton;
    protected MessageLogDialog _messageLog = null;
    protected JTabbedPane _contentTabs;
    protected DocumentationRunPanel _docPanel;
    protected CardLayout2 _definitionPanelCards;
    protected JPanel _definitionPanel;
    protected HashMap _guiToComboBoxMap = new HashMap();//key: filedata of comboboxmodel, value, gui card. except DEFAULT
    protected JComboBox _guiSelComboBox;
    protected DefaultComboBoxModel _cbModel;
    protected JToggleButton _stopButton;
    protected JButton _startSolving;
    protected JLabel _statusLabel;

    protected OptimizationToolInterfaceDefinitionRunPanel _defPanel;

    private JButton _playPauseButton;
    private ImageIcon _runIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/run/running.gif");
    private ImageIcon _pauseIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/run/pause.gif");
    private ImageIcon _runDownIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/run/runningDown.gif");
    private ImageIcon _pauseDownIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/run/pauseDown.gif");

    public static final String NAME_COLUMN = "name";
    public static final String VALUE_COLUMN = "value";
    public static final String ACTIVE_COLUMN = "active";

    public static final String[] buildColumnNames = {

        NAME_COLUMN,
        VALUE_COLUMN,
        ACTIVE_COLUMN

    };

    public static final int[] buildColumnWidths = {200, 200, 200};

    private PropertyChangeListener _statusListener;
    private PropertyChangeListener _solutionListener;
    private PropertyChangeListener _designSpaceListener;

    protected JProgressBar _progressBar;

    private boolean _runIconShowing;

    public OptimizationToolInterfaceRunPanel(OptimizationInterfaceRuntimeClient ti)
    {
        super(ti);
        addInterfaceListeners();
        createComponents();
        layoutComponent();
    }

    protected void addInterfaceListeners()
    {
        if (_ti instanceof OptimizationInterfaceRuntimeClient)
        {
            OptimizationInterfaceRuntimeClient iface = (OptimizationInterfaceRuntimeClient) _ti;
            _statusListener = new StatusChangeListener();
            iface.addPropertyChangeListener(_statusListener);
            iface.listenerAdded();
            _solutionListener = new OptimizationSolutionListener();
            iface.addPropertyChangeListener(_solutionListener);
            iface.listenerAdded();
            _designSpaceListener = new OptimizationDesignSpaceListener();
            iface.addPropertyChangeListener(_designSpaceListener);
            iface.listenerAdded();
        }

    }

    protected void createComponents()
    {
        _nameField = new NameTextField();
        _nameField.setDomeObject(_ti);
        _nameField.setEditable(false);
        _messageLogButton = Templates.makeButton(MESSAGE_LOG, new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                _messageLog.show();
            }
        });
        _cbModel = new CustomGUIComboBoxModel(_ti, false);
        _guiSelComboBox = Templates.makeDComboBox(_cbModel);
        _guiSelComboBox.addItemListener(
                new ItemListener()
                {
                    public void itemStateChanged(ItemEvent e)
                    {

                        if (_guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.CHANGE_ADD))
                        { //change
                            CustomGuiChooser.showDialog(OptimizationToolInterfaceRunPanel.this, _ti);
                            _guiSelComboBox.setModel(new CustomGUIComboBoxModel(_ti, true));
                        }
                        else if (_guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.CHANGE_EDIT))
                        { //change
                            CustomGuiPicker.showDialog(OptimizationToolInterfaceRunPanel.this, _ti);
                            _guiSelComboBox.setModel(new CustomGUIComboBoxModel(_ti, true));
                        }
                        else if (_guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.DEFAULT))
                        {
                            _definitionPanelCards.show(_definitionPanel, CustomGUIComboBoxModel.DEFAULT);
                        }
                        else
                        {
                            CustomGuiInfo item = (CustomGuiInfo) _guiSelComboBox.getSelectedItem();
                            _definitionPanelCards.show(_definitionPanel, item.toString());
                        }
                    }
                }
        );
        _ti.addPropertyChangeListener(AbstractAnalysisToolInterface.CUSTOMGUICHANGE, new CustomGuiListener());
		_contentTabs = Templates.makeTabbedPane();
		_docPanel = new DocumentationRunPanel(_ti.getDocumentation());
        _defPanel = new OptimizationToolInterfaceDefinitionRunPanel(_ti, buildColumnNames, buildColumnWidths);
        _definitionPanelCards = new CardLayout2();
        _definitionPanel = new JPanel();
		_definitionPanel.setLayout(_definitionPanelCards);
        _definitionPanel.add(CustomGUIComboBoxModel.DEFAULT, _defPanel);

        addAllCustomGUIs();

		_contentTabs.addTab(DEFINITION, _definitionPanel);
        _contentTabs.addTab(DOCUMENTATION, _docPanel);
		_contentTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				setMenuContext();
			}
		});
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
		_contentTabs.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0),
		                                                         _contentTabs.getBorder()));
		JComponent[] comps = {makeControlPanel(), _contentTabs};
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

        JComponent[] comps = {

            Templates.makeLabel("name:"),
            _nameField,
            _guiSelComboBox,
            _messageLogButton
        };

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs =  {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);
        return p;
	}

    private JPanel makeButtonPanel()
	{
		JPanel p = new JPanel();
		p.setOpaque(false);

		_playPauseButton = Templates.makeImageButton(_runIcon);
		_playPauseButton.setPressedIcon(_runDownIcon);
		_playPauseButton.setOpaque(false);
		_playPauseButton.setBorderPainted(false);
		_playPauseButton.setEnabled(false);
		_playPauseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

			}
		});

		_startSolving = Templates.makeButton("start optimization", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
            {
                startOptimizationAction();
            }
		});

        enableSubmit();   // different from other models

		JPanel compPanel = new JPanel();
		compPanel.setOpaque(false);
		JComponent[] comps = {makeStatusPanel(), _startSolving, _playPauseButton};

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
        _progressBar = new JProgressBar(0, 50);
        _progressBar.setVisible(false);
        _progressBar.setPreferredSize(new Dimension(100, 8));

        _progressBar.setForeground(Templates.DARKER_BACKGROUND_COLOR);
        _statusLabel = Templates.makeLabel("");
        JPanel barHolder = new JPanel();
        JComponent[] barComp = {_progressBar};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] barGbcs = {// 25 inset
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBagB(barHolder, barComp, barGbcs);

        JPanel statusPanel = new JPanel();
        JComponent[] statusComps = {_statusLabel, barHolder};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] statusGbcs = {// 25 inset
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};
        Templates.layoutGridBagB(statusPanel, statusComps, statusGbcs);
        return statusPanel;
    }


    private void enableSubmit()
	{
		_startSolving.setEnabled(true);
		_defPanel.enableSubmitAction();
	}

    private void disableSubmit()
	{
		_startSolving.setEnabled(false);
        _defPanel.disableSubmitAction();
	}

    protected boolean areActiveConditionsSatisfied()
    {
        switch(((OptimizationInterfaceRuntimeClient)_ti).activeCondition())
        {
            case 0:
                OneButton1Msg.showWarning(this, "Runtime Warning", "There are no variables and objectives active. \nTo run an optimisation please" +
                        " select at least one variable and objective as active", "OK", OneButton1Msg.DEFAULT_SIZE);
                return false;
            case 1:
                OneButton1Msg.showWarning(this, "Runtime Warning", "There are no variables or objectives active.  \nTo run an optimisation please" +
                        " select at least one variable and objective as active", "OK", OneButton1Msg.DEFAULT_SIZE);
                return false;
            default:
                return true;
        }
    }

    public void startOptimizationAction()
    {
        /**
         * The DOME model submit action calculates the number of changed
         * parameters by the user.  If this number is 0, then the submit
         * action is terminated since no solving is required.  However,
         * in optimization models, parameter changes are irrelevant to
         * the solving process, so this will not be performed here.
         */

        if (areActiveConditionsSatisfied())
        {
            if (_defPanel.getSplitRunPanel().getResultsPanel() == null)
                _defPanel.getSplitRunPanel().showParetoFrontPanel();
            if (_defPanel.getSplitRunPanel().getDesignSpacePanel() == null)
                _defPanel.getSplitRunPanel().showDesignSpacePanel();
            _defPanel.getSplitRunPanel().getResultsPanel().getParetoFrontPanel().resetPlot();
            _defPanel.getSplitRunPanel().getDesignSpacePanel().getDesignSpacePlot().resetPlot();
            _defPanel.getSplitRunPanel().getResultsPanel().addPropertyChangeListener(
                    OptimizationInterfaceResultsPanel.PANEL_CLOSED, new OptimizationResultsPanelListener());
            _defPanel.getSplitRunPanel().getDesignSpacePanel().addPropertyChangeListener(
                    OptimizationInterfaceDesignSpacePanel.PANEL_CLOSED, new OptimizationDesignSpacePanelListener());
            _defPanel.enablePlayPauseKillActions();
            _playPauseButton.setEnabled(true);
            _runIconShowing = false;
            _playPauseButton.setIcon(_pauseIcon);
            _playPauseButton.setPressedIcon(_pauseDownIcon);
            if (_ti instanceof OptimizationInterfaceRuntimeClient)
            {
                if (((OptimizationInterfaceRuntimeClient) _ti).getNumberOfListeners() == 1)
                    _ti.addPropertyChangeListener(_solutionListener);
                ((OptimizationInterfaceRuntimeClient) _ti).startOptimization();
            }
            RunMenus.checkAnalysisToolViewMenu(ToolInterface.RESULTS_DISPLAYED);
            RunMenus.checkAnalysisToolViewMenu(ToolInterface.DESIGN_SPACE_DISPLAYED);
        }
    }

    public void saveResults()
    {
        JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, getDomeObject().getName(), getStatusWindowLocation());
	    SaveInterfaceResultsWorker worker = new SaveInterfaceResultsWorker(_ti, waitWin);
	    worker.start();
    }

    public void loadResults()
    {
        String localFileName = RunMode.runFileChooser.showOpenDialog(null, AnalysisToolUtils.INTERFACE_RESULTS_FILE);
        if (localFileName == null) return; // cancelled
        File f = new File(localFileName);
        JFrame waitWin = StatusWindow.show(StatusWindow.OPENING_FILE, f.getName(), getStatusWindowLocation());
        OpenModelWorker worker = new OpenModelWorker(_ti, localFileName, AnalysisToolUtils.INTERFACE_RESULTS_FILE, waitWin);
        worker.start();
    }

    public void showResults()
    {
        if (_defPanel.getSplitRunPanel().getResultsPanelFrame() == null)
        {
            _defPanel.getSplitRunPanel().showParetoFrontPanel();
            RunMenus.checkAnalysisToolViewMenu(ToolInterface.RESULTS_DISPLAYED);
        }
        else
            _defPanel.getSplitRunPanel().getResultsPanel().close();
    }

    public void showDesignSpace()
    {
        if (_defPanel.getSplitRunPanel().getDesignSpacePanelFrame() == null)
        {
            _defPanel.getSplitRunPanel().showDesignSpacePanel();
            RunMenus.checkAnalysisToolViewMenu(ToolInterface.DESIGN_SPACE_DISPLAYED);
        }
        else
            _defPanel.getSplitRunPanel().getDesignSpacePanel().close();
    }

    // DomeObjectGui interface
    public String getTitlePrefix()
    {
        return PART_OF_NAME;
    }

    public String getTitle()
    {
        return _ti.getName();
    }

    public String getHelpContext()
    {
        return "";
    }

    public void setMenuContext()
	{
		MenuManager.setContext(ModeContexts.RUN_ANALYSIS_TOOL_INTERFACE);

        //to direct messages to correct message log area
		BuildFocusTracker.notifyInFocus(this,_ti);
	}

    public String getMenuContext()
    {
        return ModeContexts.RUN_ANALYSIS_TOOL_INTERFACE;
    }

    protected void createMessageLog()
	{
		_messageLog = new MessageLogDialog(this);
		_messageLog.addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				MenuManager.setContext(ModeContexts.RUN_ANALYSIS_TOOL_INTERFACE);
			}
		});
		_ti.setLogHandler(new MessageLogDialogLogHandler(_ti, _messageLog));

        //to direct messages to correct message log area
		BuildFocusTracker.notifyInFocus(this, _ti);
	}

    public void addNotify()
	{
		super.addNotify();
		createMessageLog(); // at this time, frame will be available
	}

    public void addCustomGUIPanel(CustomGuiInfo file)
	{
		ServerConnection svrConn = null;
		if (_ti instanceof OptimizationInterfaceRuntimeClient) {
			svrConn = ((OptimizationInterfaceRuntimeClient) _ti).getServerConnection();
		}
		//should created by server
		JComponent customGui = CustomGuiUtils.createCustomGuiFromRemote(file, _ti, svrConn);
		_definitionPanel.add(file.toString(), customGui);
		_guiToComboBoxMap.put(file, customGui);
	}

    public void addAllCustomGUIs()
    {
        ArrayList files = _ti.getCustomGUIList();
        for (int i = 0; i < files.size(); i++)
        {
            addCustomGUIPanel((CustomGuiInfo) files.get(i));
        }
    }

    public void close ()
	{
        OptimizationInterfaceRuntimeClient iface = (OptimizationInterfaceRuntimeClient) _ti;
        iface.removePropertyChangeListener(_statusListener);
        iface.listenerRemoved();
        iface.removePropertyChangeListener(_solutionListener);
        iface.listenerRemoved();
        iface.removePropertyChangeListener(_designSpaceListener);
        iface.listenerRemoved();
		DomeRunFrame ifaceFrame = (DomeRunFrame) SwingUtilities.windowForComponent(this);
		if (ifaceFrame.isTopLevelRunFrame())
			RuntimeFunctionsClient.killInterfaceParent(iface.getServerConnection(), iface.getRuntimeId());
	}

    protected void setStatusText(String text)
    {
        _statusLabel.setText(text);
    }

    protected void startProgressBar()
    {
        _progressBar.setVisible(true);
        _progressBar.setIndeterminate(true);

    }

    protected void stopProgressBar()
    {
        _progressBar.setIndeterminate(false);
        _progressBar.setVisible(false);
    }

    /**
	 * for determine the status window location
	 * @return
	 */
	public static Point getStatusWindowLocation()
    {
        JComponent comp = BuildFocusTracker.getCurrentComponent();
        if (comp == null)
        { // place in top left corner
            return new Point(0, DomeClientApplication.getBottomCoordinate());
        }
        else
        { // place offset to window of component
            Window win = BuildFocusTracker.getCurrentWindow();
            if (win instanceof DomeRunFrame && win.isShowing())
            {
                Point p = win.getLocationOnScreen();
                return new Point(p.x + 25, p.y + 25);
            }
            else
            { // what is it? place in top left corner
                return new Point(0, DomeClientApplication.getBottomCoordinate());
            }
        }
    }

    class CustomGuiListener implements PropertyChangeListener
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
				JComponent gui = (JComponent) _guiToComboBoxMap.get(file);
				_definitionPanel.remove(gui);
				_guiToComboBoxMap.remove(file);
			}
		}
    }

    class StatusChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent pe)
		{
			String propName = pe.getPropertyName();
            if (propName.equals(OptimizationInterfaceRuntimeClient.INTERFACEMODELSTATUS))
            {
                String newStatus = (String) pe.getNewValue();
                if (newStatus.equals(EMPTYSTRING))
                {
                    setStatusText(newStatus);
                }
                else
                {
                    setStatusText(STATUS + newStatus);
                    if (newStatus.equals(ModelRuntime.STATUS_DONE))
                    {
                        enableSubmit();
                        stopProgressBar();
                        _defPanel.disablePlayPauseKillActions();
                        if (_defPanel.getSplitRunPanel().getResultsPanelFrame() != null)
                            _defPanel.getSplitRunPanel().getResultsPanel().enableClose();
                        _playPauseButton.setEnabled(false);
                    }
                    else if (newStatus.equals(ModelRuntime.STATUS_RUNNING) ||
                            newStatus.equals(OptimizationToolRuntime.STATUS_LOADING_PROJECT))
                    {
	                    disableSubmit();
	                    startProgressBar();
	                    _defPanel.enablePlayPauseKillActions();
	                    if (_defPanel.getSplitRunPanel().getResultsPanelFrame() != null)
		                    _defPanel.getSplitRunPanel().getResultsPanel().disableClose();
	                    _playPauseButton.setEnabled(true);
                    }
                }
            }
            else if (propName.equals(OptimizationInterfaceRuntimeClient.INTERFACEINPUT))
            {
                enableSubmit();
            }
		}
	}

    class OptimizationSolutionListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent pe)
		{
			String propName = pe.getPropertyName();
            if (propName.equals(OptimizationInterfaceRuntimeClient.RESET_PLOT))
                _defPanel.getSplitRunPanel().getResultsPanel().getParetoFrontPanel().resetPlot();
            else if (propName.equals(OptimizationInterfaceRuntimeClient.NEW_INDIVIDUAL_ADDED))
            {
                Vector v = (Vector) pe.getNewValue();
                _defPanel.getSplitRunPanel().getResultsPanel().updateParetoPlot(v);

            }
            else if (propName.equals(OptimizationInterfaceRuntimeClient.RESULTS_LOADED))
            {
                _defPanel.getSplitRunPanel().showParetoFrontPanel();
            }
		}
	}

    class OptimizationDesignSpaceListener implements PropertyChangeListener
    {
        public void propertyChange (PropertyChangeEvent pe)
        {
            String propName = pe.getPropertyName();
            if (propName.equals(OptimizationInterfaceRuntimeClient.RESET_PLOT))
                _defPanel.getSplitRunPanel().getDesignSpacePanel().getDesignSpacePlot().resetPlot();
            else if (propName.equals(OptimizationInterfaceRuntimeClient.NEW_INDIVIDUAL_ADDED))
            {
                Vector v= (Vector) pe.getNewValue();
                _defPanel.getSplitRunPanel().getDesignSpacePanel().updateDesignSpacePlot(v);
            }
            else if (propName.equals(OptimizationInterfaceRuntimeClient.RESULTS_LOADED))
                _defPanel.getSplitRunPanel().showParetoFrontPanel();
        }
    }

    class OptimizationResultsPanelListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String propertyName = e.getPropertyName();
            if (propertyName.equals(OptimizationInterfaceResultsPanel.PANEL_CLOSED))
            {
                _ti.removePropertyChangeListener(_solutionListener);
                ((OptimizationInterfaceRuntimeClient)_ti).listenerRemoved();
            }
        }
    }

    class OptimizationDesignSpacePanelListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String propertyName = e.getPropertyName();
            if (propertyName.equals(OptimizationInterfaceDesignSpacePanel.PANEL_CLOSED))
            {
                _ti.removePropertyChangeListener(_designSpaceListener);
                ((OptimizationInterfaceRuntimeClient)_ti).listenerRemoved();
            }
        }
    }

    static class SaveInterfaceResultsWorker extends SwingWorker {
		ToolInterface _ti;
		JFrame waitWin;

		public SaveInterfaceResultsWorker(ToolInterface ti, JFrame waitWin) {
			_ti = ti;
			this.waitWin = waitWin;
		}

		public Object construct()
        {
            if (_ti instanceof OptimizationInterfaceRuntimeClient)
            {
                ((OptimizationInterfaceRuntimeClient)_ti).saveResults();
            }
            return new Object();
        }

		public void finished() {
			waitWin.setVisible(false);
			waitWin.dispose();
		}
	}

    static class OpenModelWorker extends SwingWorker
    {
        ToolInterface _ti;
        String fn,modelType;
        JFrame waitWin;

        public OpenModelWorker(ToolInterface ti, String fn, String modelType, JFrame waitWin)
        {
            _ti = ti;
            this.fn = fn;
            this.modelType = modelType;
            this.waitWin = waitWin;
        }

        public Object construct()
        {
            if (_ti instanceof OptimizationInterfaceRuntimeClient)
            {
                ((OptimizationInterfaceRuntimeClient) _ti).loadResults(fn);
            }
            return new Object();
        }

        public void finished()
        {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }
}
