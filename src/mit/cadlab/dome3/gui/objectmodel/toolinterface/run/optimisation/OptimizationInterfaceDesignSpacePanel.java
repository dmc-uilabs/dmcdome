package mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation;

import mit.cadlab.dome3.gui.objectmodel.ModelComponentPanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.component.VariableVisualizationPanel;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceConfiguration;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.DocumentationData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.network.client.functions.Vectors;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jun 1, 2004
 * Time: 10:33:48 PM
 * To change this template use Options | File Templates.
 */
public class OptimizationInterfaceDesignSpacePanel extends ModelComponentPanel
                                                        implements ActionListener
{
    public static final GridBagConstraints gbc = null;

    public static final String _EMPTY_STRING = "";
    public static final String DESIGN_SPACE = "Design Space: ";
    public static final String PANEL_CLOSED = "Panel Closed";
    public static final String UPDATE_INTERVAL = "update every: ";
    public static final String ITERATIONS = "individual evaluations";
    public static final String CLOSE = "close";
    public static final String NON_DOMINATED_INDIVIDUAL = "non-dominated individual";
    public static final String DOMINATED_INDIVIDUAL = "dominated individual";

    public static final JLabel updateIntervalLabel = Templates.makeLabel(UPDATE_INTERVAL);
    public static final JLabel iterationsLabel = Templates.makeLabel(ITERATIONS);

    private OptimizationInterfaceRuntimeClient _ti;
    private Vector _comboBoxIds;
    private VariableVisualizationPanel _designSpacePanel;

    private JTextField _numberOfIterationsInterval;

    private JComboBox _verticalComboBox, _horizontalComboBox;

    private JButton _closeButton;

    private PropertyChangeListener _designSpaceListener;


    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == _closeButton)
        {
            close();
        }
        else if ((event.getSource() == _verticalComboBox) || (event.getSource() == _horizontalComboBox))
        {
            String vp = (String)_comboBoxIds.get(_verticalComboBox.getSelectedIndex());
            String hp = (String)_comboBoxIds.get(_horizontalComboBox.getSelectedIndex());

            _designSpacePanel.setAxesNames((String) _horizontalComboBox.getSelectedItem(), (String) _verticalComboBox.getSelectedItem());

            Vector allVariables = _ti.getVariableMaps();
            Vector yValues = (Vector) _ti.getParameterHistory().get(vp);
            Vector xValues = (Vector) _ti.getParameterHistory().get(hp);
            Vector ranks =  _ti.getIndividualRanks();

            _designSpacePanel.resetPlot();

            if (allVariables.size() == xValues.size())
            {
                for (int i = 0; i < allVariables.size(); i++)
                {
                    Hashtable variables = (Hashtable) allVariables.get(i);
                    _designSpacePanel.storePointInMap((Double)xValues.get(i), (Double)yValues.get(i),Vectors.create(ranks.get(i), variables));
                    _designSpacePanel.updateXYSeries(((Boolean)ranks.get(i)).booleanValue(),(Double) xValues.get(i), (Double) yValues.get(i));
                }
            }
        }
    }

    public OptimizationInterfaceDesignSpacePanel(OptimizationInterfaceRuntimeClient ti)
    {
        super(ti, DESIGN_SPACE + ti.getName());
        _ti = ti;
        _comboBoxIds = new Vector();
        createComponents();
        createListeners();
        JComponent[] comps = {

            layoutComponents(),

            makeCloseButtonPanel()
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);
    }

    protected void createComponents()
    {
        _designSpacePanel = new VariableVisualizationPanel();

        _numberOfIterationsInterval = Templates.makeTextField(_EMPTY_STRING);
        _numberOfIterationsInterval.setText(((DomeInteger)_ti.getOptimizationInterfaceConfiguration().getSetupParameter
                (OptimizationInterfaceConfiguration.SOLUTION_UPDATE_INTERVAL).getCurrentDataObject()).getIntegerValue().toString());
        _numberOfIterationsInterval.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        _numberOfIterationsInterval.setEditable(false);

        _verticalComboBox = Templates.makeComboBox(createComboBoxItems());
        _verticalComboBox.setSelectedIndex(0);
        _verticalComboBox.addActionListener(this);

        _horizontalComboBox = Templates.makeComboBox(createComboBoxItems());
        if (_horizontalComboBox.getItemCount() > 1)
            _horizontalComboBox.setSelectedIndex(1);
        else
            _horizontalComboBox.setSelectedIndex(0);
        _horizontalComboBox.addActionListener(this);

        _designSpacePanel.setAxesNames((String)_horizontalComboBox.getSelectedItem(), (String) _verticalComboBox.getSelectedItem());

        _closeButton = Templates.makeButton(CLOSE, this);

        _verticalComboBox.setSelectedIndex(0);
    }

    protected void createListeners()
    {
        _designSpaceListener = new DesignSpacePlotListener();
        _designSpacePanel.addPropertyChangeListener(_designSpaceListener);
    }

    protected JTabbedPane layoutComponents()
    {
        JTabbedPane j = Templates.makeTabbedPane();
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createBevelBorder(4));

		JComponent[] comps = { makeTopSectionPanel(), _designSpacePanel, makeBottomSectionPanel()};

        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        j.add("design space", p);
        j.add("documentation", new DocumentationRunPanel(new DocumentationData()));
        return j;
    }

    private JPanel makeCloseButtonPanel()
    {
        JPanel p = new JPanel();

        JComponent[] comps = {

            new JPanel(),   // spacer panel
            _closeButton,
            new JPanel()    // spacer panel
        };

        GridBagConstraints[] gbcs = {
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(2, 5, 5, 0), 0, 0),
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(2, 5, 5, 0), 0, 0),
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(2, 5, 5, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeTopSectionPanel()
    {
        JPanel p = new JPanel();
        JComponent[] comps = {_verticalComboBox, updateIntervalLabel, _numberOfIterationsInterval, iterationsLabel};
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 30, 0, 0), 0, 0),
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(0, 5, 0, 0), 30, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 10), 0, 0)
        };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    private JPanel makeBottomSectionPanel()
    {
        JPanel p = new JPanel();

        JLabel nonDominatedLabel = Templates.makeLabel(NON_DOMINATED_INDIVIDUAL);
        nonDominatedLabel.setForeground(Color.RED);

        JLabel dominatedLabel = Templates.makeLabel(DOMINATED_INDIVIDUAL);
        dominatedLabel.setForeground(Color.GRAY);

        JComponent[] comps = {nonDominatedLabel, dominatedLabel, _horizontalComboBox};

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 30, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 15, 0, 5), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 10), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private String[] createComboBoxItems()
    {
        Collection c = _ti.getInterfaceVariableMap().values();
        int numberOfActiveVariables = 0;
        Iterator iter = c.iterator();
        while (iter.hasNext())
        {
            Object obj = iter.next();
            VariableParameter parameter = (VariableParameter) obj;
            if (parameter.getIsActive().getValue())
                numberOfActiveVariables++;
        }

        String[] comboBoxOptions = new String[numberOfActiveVariables];
        iter = c.iterator();
        for (int i = 0; iter.hasNext(); i++)
        {
            Object obj = iter.next();
            VariableParameter parameter = (VariableParameter) obj;
            if (parameter.getIsActive().getValue())
            {
                _comboBoxIds.add(parameter.getParameter().getId().toString());
                comboBoxOptions[i] = parameter.getParameter().getName();
            }
            else
                i--;
        }
        return comboBoxOptions;
    }

    public void updateDesignSpacePlot(Vector v)
    {
        Boolean isRankOne = (Boolean) v.get(0);
        Hashtable variables = (Hashtable) v.get(1);

        Double xValue = (Double) variables.get(_comboBoxIds.get(_horizontalComboBox.getSelectedIndex()));
        Double yValue = (Double) variables.get(_comboBoxIds.get(_verticalComboBox.getSelectedIndex()));

        _designSpacePanel.storePointInMap(xValue, yValue, v);
        _designSpacePanel.updateXYSeries(isRankOne.booleanValue(), xValue, yValue);
    }

    public void close()
	{
        RunMenus.checkAnalysisToolViewMenu(ToolInterface.DESIGN_SPACE_CLOSED);
        SwingUtilities.windowForComponent(this).dispose();
        firePropertyChange(PANEL_CLOSED, null, null);
	}

    public String getHelpContext()
    {
        return null;
    }

    public void setMenuContext()
    {
    }

    public VariableVisualizationPanel getDesignSpacePlot()
    {
        return _designSpacePanel;
    }

    protected class DesignSpacePlotListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent event)
        {
            if (event.getPropertyName().equals(VariableVisualizationPanel.INDIVIDUAL_CHOSEN))
            {
                Object newValue = event.getNewValue();
                if (newValue instanceof Vector)
                {
                    Vector v = (Vector) newValue;
                    _ti.handleDesignSpacePlotMouseClick(v);
                }
            }
        }
    }

}
