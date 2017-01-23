package dispatchmodelgui;

import dispatchmodelgui.components.InputDataSpecificationPanel;
import dispatchmodelgui.components.PowerSourceAndFuelInformationPanel;
import dispatchmodelgui.components.ModelResultsPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 18, 2003
 * Time: 3:03:37 PM
 * To change this template use Options | File Templates.
 */

/**
 * DispatchModelGUITabbedPane.java
 *
 * This class is the gui object class for the dispatch model
 * custom gui.  It consists of a tabbed pane that aggregates
 * all of the gui panel components.
 *
 * GUI Architecture:
 *
 * There are 3 tabs in the tabbed pane:
 *
 * 1. "input data" tab - this tab holds the InputDataSpecficiationPanel
 * gui object.  This panel is resonsible for representing data about
 * day data objects, and it holds the editable chart panel, which is
 * used for entering the power demand data for a given day.
 *
 * 2. "power and fuel definition" tab - this tab holds the
 * PowerSourceAndFuelInformationPanel gui object.  This panel is
 * responsible for representing data about power generation and fuel
 * types data objects.
 *
 * 3. "model results" tab - this tab holds the Model ResultsPanel gui
 * object.  This panel is responsible for displaying the results of the
 * power dispatch model in the form of a stacked area chart, which displays
 * the optimial utilization of the available power sources to meet a given
 * power demand.
 *
 */

public class DispatchModelGUITabbedPane extends JTabbedPane
{

    // constants for tab titles
    public static final String INPUT_DATA = "input data";
    public static final String POWER_AND_FUEL_DEFINITION = "power and fuel definition";
    public static final String MODEL_RESULTS = "model results";

    // panels that are added to each pane
    private dispatchmodelgui.components.InputDataSpecificationPanel _inputPanel;
    private dispatchmodelgui.components.PowerSourceAndFuelInformationPanel _powerFuelPanel;
    private dispatchmodelgui.components.ModelResultsPanel _resultsPanel;

    /**
     * default constructor for the DispatchModelGUITabbedPane
     */
    public DispatchModelGUITabbedPane()
    {
        super();

        // tabbed pane is configured for consistency
        createConfiguration();

        // gui panels are created
        createComponents();

        // each panel is added to the tabbed pane
        addTab(INPUT_DATA, _inputPanel);
        addTab(POWER_AND_FUEL_DEFINITION, _powerFuelPanel);
        addTab(MODEL_RESULTS, _resultsPanel);
    }

    /**
     * configurates the tabbed pane,
     * by setting the default size, font
     * and tab placement of the gui
     */
    protected void createConfiguration()
    {
        setSize(GUIConstants.DEFAULT_SIZE);
        setFont(GUIConstants.REGULAR_FONT);
        setTabPlacement(JTabbedPane.TOP);
    }

    /**
     * methods that creates panel objects
     * that get stored inside the tabbed pane
     */
    protected void createComponents()
    {
        // input panel
        _inputPanel = new dispatchmodelgui.components.InputDataSpecificationPanel();

        // power and fuel panel
        _powerFuelPanel = new dispatchmodelgui.components.PowerSourceAndFuelInformationPanel();

        // results panel
        _resultsPanel = new dispatchmodelgui.components.ModelResultsPanel();
    }

    /**
     * method that returns the input panel
     * @return InputDataSpecficationPanel
     */
    public dispatchmodelgui.components.InputDataSpecificationPanel getInputPanel()
    {
        return _inputPanel;
    }

    /**
     * method that returns the power generation
     * and fuel data object panel
     * @return PowerSourceandFuelInformationPanel
     */
    public dispatchmodelgui.components.PowerSourceAndFuelInformationPanel getPowerAndFuelPanel()
    {
        return _powerFuelPanel;
    }

    /**
     * method that returns the model results panel
     * @return ModelResultsPanel
     */
    public dispatchmodelgui.components.ModelResultsPanel getResultsPanel()
    {
        return _resultsPanel;
    }

}
