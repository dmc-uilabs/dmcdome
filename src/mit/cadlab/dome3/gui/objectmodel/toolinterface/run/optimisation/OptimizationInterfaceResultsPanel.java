package mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation;

import mit.cadlab.dome3.gui.objectmodel.DomeFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.ModelComponentPanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.component.ObjectiveVisualizationPanel;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.objectmodel.dataobject.DocumentationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceConfiguration;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.tool.AnalysisToolUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.io.File;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 5, 2003
 * Time: 10:35:31 AM
 * To change this template use Options | File Templates.
 */
public class OptimizationInterfaceResultsPanel extends ModelComponentPanel
                                                    implements ActionListener
{
    public static final GridBagConstraints gbc = null;

    public static final String _EMPTY_STRING = "";
    public static final String UPDATE_INTERVAL = "update every: ";
    public static final String ITERATIONS = "individual evaluations";
    public static final String CLOSE = "close";
    public static final String OPTIMIZATION_RESULTS = "Optimization Results: ";
    public static final String PANEL_CLOSED = "panel closed";
    public static final String NON_DOMINATED_INDIVIDUAL = "non-dominated individual";
    public static final String DOMINATED_INDIVIDUAL = "dominated individual";

    public static final JLabel updateIntervalLabel = Templates.makeLabel(UPDATE_INTERVAL);
    public static final JLabel iterationsLabel = Templates.makeLabel(ITERATIONS);

    private JTextField _numberOfIterationsInterval;

    private JComboBox _verticalComboBox, _horizontalComboBox;
    private Vector _comboBoxIds;
    private JButton _closeButton;
    private OptimizationInterfaceRuntimeClient _ti;
    private ObjectiveVisualizationPanel _paretoFrontPanel;

    private PropertyChangeListener _paretoListener;

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

            _paretoFrontPanel.setAxesNames((String) _horizontalComboBox.getSelectedItem(), (String) _verticalComboBox.getSelectedItem());

            Vector allVariables = _ti.getVariableMaps();
            Vector allObjectives = _ti.getObjectiveMaps();
            Vector yValues = (Vector) _ti.getParameterHistory().get(vp);
            Vector xValues = (Vector) _ti.getParameterHistory().get(hp);
            Vector ranks =  _ti.getIndividualRanks();

            _paretoFrontPanel.resetPlot();

            if (allVariables.size() == xValues.size())
            {
                for (int i = 0; i < allVariables.size(); i++)
                {
                    Hashtable variables = (Hashtable) allVariables.get(i);
                    Hashtable objectives = (Hashtable) allObjectives.get(i);
                    _paretoFrontPanel.storePointInMap((Double)xValues.get(i), (Double)yValues.get(i),Vectors.create(ranks.get(i), variables, objectives));
                    _paretoFrontPanel.updateXYSeries(((Boolean)ranks.get(i)).booleanValue(),(Double) xValues.get(i), (Double) yValues.get(i));
                }
            }
        }
    }

    public OptimizationInterfaceResultsPanel(OptimizationInterfaceRuntimeClient ti)
    {
        super(ti, OPTIMIZATION_RESULTS + ti.getName());
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
        _paretoFrontPanel = new ObjectiveVisualizationPanel();

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

        _paretoFrontPanel.setAxesNames((String)_horizontalComboBox.getSelectedItem(), (String) _verticalComboBox.getSelectedItem());

        _closeButton = Templates.makeButton(CLOSE, this);

        _verticalComboBox.setSelectedIndex(0);
    }

    protected void createListeners()
    {
        _paretoListener = new ParetoPlotListener();
        _paretoFrontPanel.addPropertyChangeListener(_paretoListener);
    }
    protected JTabbedPane layoutComponents()
    {
        JTabbedPane j = Templates.makeTabbedPane();
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createBevelBorder(4));

		JComponent[] comps = { makeTopSectionPanel(), _paretoFrontPanel, makeBottomSectionPanel()};

        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        j.add("results", p);
        j.add("documentation", new DocumentationRunPanel(new DocumentationData()));
        return j;
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

    public void close()
	{
        RunMenus.checkAnalysisToolViewMenu(ToolInterface.RESULTS_CLOSED);
        _paretoFrontPanel.removePropertyChangeListener(_paretoListener);
        SwingUtilities.windowForComponent(this).dispose();
        firePropertyChange(PANEL_CLOSED, null, null);
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

    private String[] createComboBoxItems()
    {
        Collection c = _ti.getInterfaceObjectiveMap().values();
        int numberOfActiveObjectives = 0;
        Iterator iter = c.iterator();
        while (iter.hasNext())
        {
            Object obj = iter.next();
            ObjectiveParameter parameter = (ObjectiveParameter) obj;
            if (parameter.getIsActive().getValue())
                numberOfActiveObjectives++;
        }

        String[] comboBoxOptions = new String[numberOfActiveObjectives];
        iter = c.iterator();
        for (int i = 0; iter.hasNext(); i++)
        {
            Object obj = iter.next();
            ObjectiveParameter parameter = (ObjectiveParameter) obj;
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

    public ObjectiveVisualizationPanel getParetoFrontPanel()
    {
        return _paretoFrontPanel;
    }

    public String getHelpContext()
    {
        return null;
    }

    public void setMenuContext()
    {
        MenuManager.setContext(ModeContexts.RUN_ANALYSIS_TOOL_INTERFACE_RESULTS_VIEW);
		BuildFocusTracker.notifyInFocus(this,_ti);
    }

    public void disableClose()
    {
        _closeButton.setEnabled(false);
    }

    public void enableClose()
    {
        _closeButton.setEnabled(true);
    }

    public void updateParetoPlot(Vector v)
    {
        Boolean isRankOne = (Boolean) v.get(0);
        Hashtable objectives = (Hashtable) v.get(2);

        Double xValue = (Double) objectives.get(_comboBoxIds.get(_horizontalComboBox.getSelectedIndex()));
        Double yValue = (Double) objectives.get(_comboBoxIds.get(_verticalComboBox.getSelectedIndex()));

        _paretoFrontPanel.storePointInMap(xValue, yValue, v);
        _paretoFrontPanel.updateXYSeries(isRankOne.booleanValue(), xValue, yValue);
    }

    // --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
    {

        public FocusTrackerAction(String name)
        {
            super(name);
        }

        protected OptimizationInterfaceResultsPanel getOptimizationToolInterfaceResultsPanel(ActionEvent e)
        {
            if (e != null)
            {
                Object o = e.getSource();
                if (o instanceof OptimizationToolInterfaceRunPanel)
                {
                    return (OptimizationInterfaceResultsPanel) o;
                }
            }
            JComponent comp = RunFocusTracker.getCurrentComponent();
            if (comp instanceof OptimizationInterfaceResultsPanel)
                return (OptimizationInterfaceResultsPanel) comp;
            throw new NullPointerException("No current OptimizationToolInterfaceResultsPanel");
        }
    }

    public static final AbstractAction saveResultsAction = new FocusTrackerAction("save results")
    {
        public void actionPerformed(ActionEvent e)
        {
            getOptimizationToolInterfaceResultsPanel(e).saveResults();
        }
    };

    public static final AbstractAction loadResultsAction = new FocusTrackerAction("load results")
    {
        public void actionPerformed(ActionEvent e)
        {
            getOptimizationToolInterfaceResultsPanel(e).loadResults();
        }
    };

    public void saveResults()
    {
        JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, _ti.getName(), getStatusWindowLocation());
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

    static class SaveInterfaceResultsWorker extends SwingWorker
    {
        ToolInterface _ti;
        JFrame waitWin;

        public SaveInterfaceResultsWorker(ToolInterface ti, JFrame waitWin)
        {
            _ti = ti;
            this.waitWin = waitWin;
        }

        public Object construct()
        {
            if (_ti instanceof OptimizationInterfaceRuntimeClient)
            {
                ((mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient) _ti).saveResults();
            }
            return new Object();
        }

        public void finished()
        {
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
            if (_ti instanceof mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient)
            {
                ((mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient) _ti).loadResults(fn);
            }
            return new Object();
        }

        public void finished()
        {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    protected class ParetoPlotListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent event)
        {
            if (event.getPropertyName().equals(ObjectiveVisualizationPanel.INDIVIDUAL_CHOSEN))
            {
                Object newValue = event.getNewValue();
                if (newValue instanceof Vector)
                {
                    Vector v = (Vector) newValue;
                    _ti.handleParetoPlotMouseClick(v);
                }
            }
        }
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
}
