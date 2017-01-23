package mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation;

import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.OptimizationToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.swing.Templates;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.tree.ObjectTree;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.ContextTreePanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.run.RunContextTree;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.AnalysisToolInterfaceDefinitionRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationInterfaceDataSplitRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.AnalysisToolInterfaceTreePanel;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.ModeContexts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 28, 2003
 * Time: 1:59:28 PM
 * To change this template use Options | File Templates.
 */
public class OptimizationToolInterfaceDefinitionRunPanel extends AnalysisToolInterfaceDefinitionRunPanel
{
    public static final String VIEW_RESULTS = "view results";

    protected CardLayout2 _ifaceViewsCards;
    protected JPanel _ifaceViewsPanel;

    protected ContextTreePanel _buildViewPanel;
    protected JButton _backButton;
    protected boolean _isBackButtonEnabled = false;
    protected NameTextField _nameField;
    protected boolean _isNameFieldEditable = true;
    protected BasicComboPopup _contextPopup;
    protected JButton _rootContextsButton;
    protected int _tfHeight = 0; // initialize textfield height

    protected JComboBox _viewChoice, _viewComboBox;
    protected DefaultComboBoxModel _cbModel;

    private OptimizationInterfaceDataSplitRunPanel _runViewPanel;

    private String[] columnNames;
    private int[] columnWidths;


    private OptimizationInterfaceRuntimeClient _ti;

    public OptimizationToolInterfaceDefinitionRunPanel(ToolInterface ti, String[] columnNames, int[] columnWidths)
	{
        if(ti instanceof OptimizationInterfaceRuntimeClient)
            _ti = (OptimizationInterfaceRuntimeClient) ti;
        this.columnNames = columnNames;
        this.columnWidths = columnWidths;
		setBackground(Templates.DARKER_BACKGROUND_COLOR);
		createComponents();
	}

    protected void createComponents()
	{
        _ifaceViewsCards = new CardLayout2();
        _ifaceViewsPanel = new JPanel();
        _ifaceViewsPanel.setLayout(_ifaceViewsCards);

        DefaultContextBuilder conBuilder = null;

        if (_ti instanceof ToolInterface)
        {
            conBuilder = (DefaultContextBuilder) _ti.getBuildContext();
        }

        _buildViewPanel = new ContextTreePanel(conBuilder, OptimizationToolInterfaceBase.QMOO_INTERFACE_BUILD_VIEW_PARAMETER, columnNames.length, columnNames, columnWidths);
        _ifaceViewsPanel.add(ToolInterface.BUILD_VIEW, _buildViewPanel);

        _runViewPanel = new OptimizationInterfaceDataSplitRunPanel( _ti); //TODO: clean this up
        _ifaceViewsPanel.add(ToolInterface.INTERFACE_CAUSALITY_VIEW, _runViewPanel);

        _backButton = Templates.makeImageButton("mit/cadlab/dome3/icons/backArrow16.gif");
		_backButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		_backButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String currentView = _ifaceViewsCards.getActiveName();
                if (currentView.equals(ToolInterface.BUILD_VIEW))
                {
                    _buildViewPanel.getContextTree().setRootContextBack();
                }
                _nameField.setForeground(Color.BLACK);
            }
        });

        _backButton.setEnabled(false);

        _nameField = new NameTextField();
		_nameField.setForeground(Color.BLACK);
		_nameField.setDisabledTextColor(notEditableColor);
		_nameField.setDomeObject(_buildViewPanel.getContextTree().getRootContext());
		_nameField.setEditable(false);

		_isNameFieldEditable = false;

        _nameField.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		// associate backbutton and namefield with tree root and history
		RunContextTree contextTree = _buildViewPanel.getContextTree();
		contextTree.addPropertyChangeListener(ObjectTree.ROOT_PROPERTY, new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent e)
            {
                String property = e.getPropertyName();
                if (property.equals(ObjectTree.ROOT_PROPERTY))
                {
                    _nameField.setDomeObject(_buildViewPanel.getContextTree().getRootContext());
                }
            }
        });

		layoutComponent();
	}

    protected void layoutComponent()
    {
        JComponent[] comps = {makeControlPanel(), _ifaceViewsPanel};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {// 25 inset

            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBagB(this, comps, gbcs);
    }

    protected JPanel makeControlPanel()
    {
        JPanel p = new JPanel();
        p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        if (_ti instanceof ToolInterface)
        {
            _cbModel = new DefaultComboBoxModel(_ti.getViewNames().toArray());
        }

        _viewComboBox = Templates.makeComboBox(_cbModel);
        _viewComboBox.setForeground(Color.BLACK);
        _viewComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent evt)
            {
                switchView();
                _contextPopup.hide();
            }
        });

        _contextPopup = new BasicComboPopup(_viewComboBox);
        _viewComboBox.setSelectedItem(ToolInterface.INTERFACE_CAUSALITY_VIEW);

        _rootContextsButton = Templates.makeImageButton(comboArrow, comboArrow, comboArrowOver, comboArrow);
        _rootContextsButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        _rootContextsButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (_tfHeight == 0)
                {
                    Dimension tfSize = _nameField.getSize();
                    _tfHeight = tfSize.height;
                    Dimension popupSize = new Dimension(tfSize.width, _tfHeight * _cbModel.getSize());
                    Templates.setFixedSize(_contextPopup, popupSize);
                }
                _contextPopup.show(_nameField, 0, _tfHeight);
            }
        });

        // filler panel size of button
        JButton listButton = Templates.makeListArrowButton("up");

        JComponent[] comps = {

            _backButton,
            _nameField,
            _rootContextsButton
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    protected void switchView()
    {
        String newView = _cbModel.getSelectedItem().toString();
        _ifaceViewsCards.show(_ifaceViewsPanel, newView);
        setMenuContext();
        synchronizeViewControls();
        if (newView.equals(ToolInterface.BUILD_VIEW))
        {
            if (_ti instanceof OptimizationToolInterfaceBase)
                _ti.setCurrentView(ToolInterface.BUILD_VIEW);
            return;
        }
		if (newView.equals(ToolInterface.INTERFACE_CAUSALITY_VIEW))
        {
            if (_ti instanceof OptimizationToolInterfaceBase)
                _ti.setCurrentView(ToolInterface.INTERFACE_CAUSALITY_VIEW);
            return;
        }
    }

    public void setMenuContext()
	{
		MenuManager.setContext(getMenuContext());
		JComponent comp = (JComponent) _ifaceViewsCards.getActiveComponent();

        //to direct messages to correct message log area
		BuildFocusTracker.notifyInFocus(comp, (ModelComponent) _ti);
	}

	protected String getMenuContext()
    {
        return ModeContexts.RUN_ANALYSIS_TOOL_INTERFACE;
    }

	protected void synchronizeViewControls()
    {
        String currentView = _ifaceViewsCards.getActiveName();
        if (currentView.equals(ToolInterface.BUILD_VIEW))
        {
            _nameField.setDomeObject(_buildViewPanel.getContextTree().getRootContext());
            _nameField.setEditable(_isNameFieldEditable);
            _backButton.setEnabled(_isBackButtonEnabled);
            return;
        }
        if (currentView.equals(ToolInterface.INTERFACE_CAUSALITY_VIEW))
        {
            setViewName(currentView);
            return;
        }
    }

    public void enablePlayPauseKillActions()
    {
        AnalysisToolInterfaceTreePanel.pauseResumeAction.setEnabled(true);
        //ModelInterfaceTreePanel.killAction.setEnabled(true);
    }

    public void disablePlayPauseKillActions()
    {
        AnalysisToolInterfaceTreePanel.pauseResumeAction.setEnabled(false);
        //ModelInterfaceTreePanel.killAction.setEnabled(false);
    }

    public void disableSubmitAction()
	{
		AnalysisToolInterfaceTreePanel.submitAction.setEnabled(false);
	}

	public void enableSubmitAction()
	{
		AnalysisToolInterfaceTreePanel.submitAction.setEnabled(true);
	}


    private void setViewName(String currentView)
	{
		_nameField.setText(currentView);
		_nameField.setEditable(false);
		_nameField.setCurrent();
		_backButton.setEnabled(false);
	}

    protected OptimizationInterfaceDataSplitRunPanel getSplitRunPanel()
    {
        return _runViewPanel;
    }
}
