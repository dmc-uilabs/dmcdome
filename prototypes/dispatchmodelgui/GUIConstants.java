package dispatchmodelgui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 19, 2003
 * Time: 12:43:18 AM
 * To change this template use Options | File Templates.
 */

/**
 * GUIConstants.java
 *
 * This class contains all of the constants used inside
 * the dispatch model gui.
 *
 * Also, to create a consistent
 * look and feel, a number of static methods have been
 * written to create JComponents inside the gui.
 */

public class GUIConstants
{
    // constants common to more than one gui component

    // default dimension size
    public static final Dimension DEFAULT_SIZE = new Dimension(700, 500);

    // font for gui components
    public static final Font REGULAR_FONT = new Font("Dialog", Font.PLAIN, 10);
    public static final Font BOLD_FONT = new Font("Dialog", Font.BOLD, 10);
    public static final Font ITALIC_SMALL_FONT = new Font("Dialog", Font.ITALIC, 8);

    // GridBagConstraints constant
    public static final GridBagConstraints gbc = null;

    // empty string
    public static final String EMPTY_STRING = "";

    /**
     * constants for the InputDataSpecificationPanel
     */

    public static final String NUMBER_OF_DAYS = "number of days: ";

    // add and remove button constants
    public static final String ADD = "add";
    public static final String REMOVE = "remove";

    public static final String NAME = "name: ";
    public static final String DAY_ATTRIBUTE_LABEL = "attributes for selected day";
    public static final String NUMBER_OF_DAYS_REPRESENTED = "number of days represented: ";
    public static final String DAYS = "days";
    public static final String TOTAL_NUMBER_OF_DAYS_ACCOUNTED_FOR = "days in the year accounted for                ";
    public static final String INTEREST_RATE = "interest rate: ";
    public static final String PERCENT = "%";
    public static final String PROPERTY_TAX = "property tax: ";
    public static final String YEN = "yen";
    public static final String X_AXIS_TITLE = "hour of the day ";
    public static final String Y_AXIS_TITLE = "power demand (Wh)";
    public static final String BORDER_DAY_ATTRIBUTES = " day attributes ";
    public static final String BORDER_POWER_DEMAND = " power demand ";
    public static final String BORDER_MARKET_ATTRIBUTES = " market attributes ";
    public static final String DEVELOPED_MIT_CADLAB = "developed at the MIT - CADlab (http://cadlab.mit.edu/)";

    public static final String CHANGE_NUMBER_OF_DAYS = "number of days changed";
    public static final String CHANGE_DAY_ADDED = "day added";
    public static final String CHANGE_DAY_REMOVED = "day removed";
    public static final String CHANGE_DAY_COMBO_BOX = "day combo box selection";
    public static final String CHANGE_NAME_OF_DAY = "day name changed";
    public static final String CHANGE_DAYS_REPRESENTED = "days represented";
    public static final String CHANGE_INTEREST_RATE = "interest rate";
    public static final String CHANGE_PROPERTY_TAX = "property tax";
    public static final String CHANGE_EDIT_X_Y = "edit chart change";
    public static final String DEFAULT_NUMBER_OF_DAYS = "7";

    // parameter constants
    public static final String INTEREST_RATE_PARAMETER = "interest rate";
    public static final String PROPERTY_TAX_PARAMETER = "property tax";
    public static final String POWER_DEMAND_DATA_PARAMETER = "power demand data";
    public static final String NUMBER_OF_DAYS_REPRESENTED_PARAMETER = "number of days represented";

    public static final JLabel numberOfDaysLabel = makeLabel(NUMBER_OF_DAYS);
    public static final JLabel nameLabel = makeLabel(NAME);
    public static final JLabel numberOfDaysRepresentedLabel = makeLabel(NUMBER_OF_DAYS_REPRESENTED);
    public static final JLabel daysLabel = makeLabel(DAYS);
    public static final JLabel interestRateLabel = makeLabel(INTEREST_RATE);
    public static final JLabel percentLabel = makeLabel(PERCENT);
    public static final JLabel propertyTaxLabel = makeLabel(PROPERTY_TAX);
    public static final JLabel yenLabel = makeLabel(YEN);
    public static final JLabel percentInputLabel = makeLabel(PERCENT);
    public static final JLabel cadlabLabel = makeIconLabel(DEVELOPED_MIT_CADLAB);
    public static final JLabel dayAttributesLabel = GUIConstants.makeLabel(GUIConstants.DAY_ATTRIBUTE_LABEL);

    // times of year data was taken - also used by ModelResultsPanel
    public static final String AVERAGE_SPRING_FALL_WEEKDAY = "average spring/fall weekday";
    public static final String AVERAGE_SPRING_FALL_WEEKEND = "average spring/fall weekend";
    public static final String AVERAGE_SUMMER_WEEKDAY = "average summer weekday";
    public static final String AVERAGE_SUMMER_WEEKEND = "average summer weekend";
    public static final String AVERAGE_WINTER_WEEKDAY = "average winter weekday";
    public static final String AVERAGE_WINTER_WEEKEND = "average winter weekend";
    public static final String PEAK_DEMAND = "peak power demand";

    // season combo box choices
    public static final String[] dayComboBoxChoice =
            {
                PEAK_DEMAND,
                AVERAGE_SUMMER_WEEKDAY,
                AVERAGE_SUMMER_WEEKEND,
                AVERAGE_SPRING_FALL_WEEKDAY,
                AVERAGE_SPRING_FALL_WEEKEND,
                AVERAGE_WINTER_WEEKDAY,
                AVERAGE_WINTER_WEEKEND
            };

    public static final Integer NUMBER_OF_HOURS = new Integer(24);

    // globe icon
    public static final String GLOBE_IMAGE_PATH = "dispatchmodelgui/dispatchmodelguicomponents/globe.gif";
    public static final Icon GLOBE_ICON = makeIcon(GLOBE_IMAGE_PATH);


    /**
     * constants for the PowerSourceAndFuelInformationPanel
     */

    public static Integer DEFAULT_NUMBER_OF_POWER_GENERATION_TYPES = new Integer(11);
    public static final String POWER_GENERATION_TYPE_ATTRIBUTES = "attributes for ";
    public static final String LIMIT_DOES_NOT_EXIST = "limit does not exist";


    public static final String STORAGE = "storage";
    public static final String NUCLEAR = "nuclear";
    public static final String COAL = "coal";
    public static final String LNG = "LNG";
    public static final String OIL = "oil";
    public static final String GCC = "GCC";
    public static final String SOFC = "SOFC";
    public static final String PUMP = "pump";
    public static final String XXX = "XXX";
    public static final String YYY = "YYY";
    public static final String HYDRO = "hydro";


    // type of power generation processes

    /*
     *  not final because we change legend names if the
     *  users changes power generation name
     */

    public static String[] seriesNames =
            {
                STORAGE,
                NUCLEAR,
                COAL,
                LNG,
                OIL,
                GCC,
                SOFC,
                PUMP,
                HYDRO,
                XXX,
                YYY
            };

	// types of fuel
	public static final String NONE = "no fuel type";
	public static final String URANIUM = "uranium";
	public static final String HYDROGEN = "hydrogen";
    public static final String LITERS = "liters";
    public static final String TONS = "tons";
    public static final String CUBIC_METERS = "cubic meters";

	public static final String[] fuelTypeOptions = {

		NONE,
		URANIUM,
		COAL,
		OIL,
		HYDROGEN

	};

    public static final String[] fuelUnitOptions = {

        LITERS,
        TONS,
        CUBIC_METERS
    };

    public static Integer DEFAULT_NUMBER_OF_FUEL_TYPES = new Integer(fuelTypeOptions.length);

    public static final String NUMBER_OF_POWER_SOURCES = "number of power generation types ";
    public static final String FUEL_TYPE_POWER_SOURCE = "fuel type ";
    public static final String COMBO_BOX_CHOICE_DEFAULT = "power generation type ";
    public static final String CONVERSION_EFFICIENCY = "conversion efficiency ";
    public static final String INSTALLATION_COST = "installation cost / kW capacity ";
    public static final String YEN_PER_KW = "Yen / kW";
    public static final String RUNNING_COST = "running cost / kWh of power generation";
    public static final String YEN_PER_KWH = "Yen / kWh";
    public static final String LOAD_FOLLOWING_CHARACTERISTICS = "load following characteristics: ";
    public static final String INCREASING_CONSTRAINT = "increasing constraint ";
    public static final String DECREASING_CONSTRAINT = "decreasing constraint ";
    public static final String CAPACITY = "capacity: ";
    public static final String UPPER_LIMIT = "upper limit";
    public static final String LOWER_LIMIT = "lower limit";
    public static final String GW = "GW";
    public static final String ENERGY_PLAN_PARAMETERS = "energy plan parameters for each centralized power source: ";
    public static final String INSPECTION_RATIO = "inspection ratio";
    public static final String DEPRECIATION_PERIOD = "depreciation period";
    public static final String YEARS = "years";
    public static final String RESIDUAL_VALUE = "residual value";
    public static final String ATTRIBUTE_FUEL_TYPE = "attributes: ";

    public static final String POWER_GENERATION = "power generation types";
    public static final String FUEL_TYPES = "fuel types";
    public static final String NUMBER_FUEL_TYPES = "number of fuel types";
    public static final String FUEL_ATTRIBUTES_FOR = "attributes for ";
    public static final String COST_OF_FUEL = "cost of fuel";
    public static final String YEN_PER_UNIT = "yen / unit";
    public static final String MAXIMUM_ANNUAL_FUEL_RATE = "maximum annual fuel supply rate";
    public static final String UNITS_PER_YEAR = "units / year";
    public static final String MAXIMUM_HOURLY_FUEL_RATE = "maximum hourly fuel supply rate";
    public static final String UNITS_PER_HOUR = "units / hour";
    public static final String ENERGETIC_VALUE = "energetic value";
    public static final String JOULES_PER_UNIT = "Joules / unit";
    public static final String FUEL_UNITS = "fuel unit";

    public static final String CHANGE_POWER_SOURCE = "power source change";
    public static final String CHANGE_POWER_SOURCE_ADDED = "power source added";
    public static final String CHANGE_POWER_SOURCE_REMOVED = "power source removed";
    public static final String CHANGE_POWER_SOURCE_COMBO_BOX = "power source combo box";
    public static final String CHANGE_POWER_SOURCE_NAME = "power source name";
    public static final String CHANGE_POWER_FUEL_TYPE = "power fuel type";
    public static final String CHANGE_POWER_CONVERSION_EFFICIENCY = "power conversion efficiency";
    public static final String CHANGE_POWER_INSTALLATION_COST = "power installation cost";
    public static final String CHANGE_POWER_RUNNING_COST = "power running cost";
    public static final String CHANGE_POWER_INCREASING_CONSTRAINT = "power increasing constraint";
    public static final String CHANGE_POWER_DECREASING_CONSTRAINT = "power decreasing constraint";
    public static final String CHANGE_POWER_UPPER_LIMIT = "power upper limit";
    public static final String CHANGE_POWER_LOWER_LIMIT = "power lower limit";
    public static final String CHANGE_POWER_NO_UPPER_LIMIT = "power no upper limit";
    public static final String CHANGE_POWER_INSPECTION_RATIO = "power inspection ratio";
    public static final String CHANGE_POWER_DEPRECIATION_PERIOD = "power depreciation period";
    public static final String CHANGE_POWER_RESIDUAL_VALUE = "power residual value";
    public static final String CHANGE_FUEL_NUMBER = "fuel number";
    public static final String CHANGE_FUEL_ADDED = "fuel added";
    public static final String CHANGE_FUEL_REMOVED = "fuel removed";
    public static final String CHANGE_FUEL_COMBO_BOX = "fuel combo box";
    public static final String CHANGE_FUEL_UNIT_OPTIONS = "fuel unit options";
    public static final String CHANGE_FUEL_NAME = "fuel name";
    public static final String CHANGE_FUEL_COST = "fuel cost";
    public static final String CHANGE_FUEL_ANNUAL_FLOW = "fuel annual flow";
    public static final String CHANGE_FUEL_HOURLY_FLOW = "fuel hourly flow";
    public static final String CHANGE_FUEL_ENERGY_VALUE = "fuel energy value";

    public static JLabel numberPowerSourcesLabel = GUIConstants.makeLabel(NUMBER_OF_POWER_SOURCES);
    public static JLabel powerGenerationNameLabel = GUIConstants.makeLabel(NAME);
    public static JLabel fuelTypeForPowerSourceLabel = GUIConstants.makeLabel(FUEL_TYPE_POWER_SOURCE);
    public static JLabel conversionEfficiencyLabel = GUIConstants.makeLabel(CONVERSION_EFFICIENCY);
    public static JLabel installationCostLabel = GUIConstants.makeLabel(INSTALLATION_COST);
    public static JLabel yenPerKw = GUIConstants.makeLabel(YEN_PER_KW);
    public static JLabel runningCostLabel = GUIConstants.makeLabel(RUNNING_COST);
    public static JLabel yenPerKwh = GUIConstants.makeLabel(YEN_PER_KWH);
    public static JLabel loadFollowingCharacteristicsLabel = GUIConstants.makeLabel(LOAD_FOLLOWING_CHARACTERISTICS);
    public static JLabel increasingConstraintLabel = GUIConstants.makeLabel(INCREASING_CONSTRAINT);
    public static JLabel decreasingConstraintLabel = GUIConstants.makeLabel(DECREASING_CONSTRAINT);
    public static JLabel capacityLabel = GUIConstants.makeLabel(CAPACITY);
    public static JLabel upperLimitLabel = GUIConstants.makeLabel(UPPER_LIMIT);
    public static JLabel lowerLimitLabel = GUIConstants.makeLabel(LOWER_LIMIT);
    public static JLabel gwUpperLabel = GUIConstants.makeLabel(GW);
    public static JLabel gwLowerLabel = GUIConstants.makeLabel(GW);
    public static JLabel energyPlanLabel = GUIConstants.makeLabel(ENERGY_PLAN_PARAMETERS);
    public static JLabel inspectionRatioLabel = GUIConstants.makeLabel(INSPECTION_RATIO);
    public static JLabel inspectionPercentLabel = GUIConstants.makeLabel(PERCENT);
    public static JLabel depreciationLabel = GUIConstants.makeLabel(DEPRECIATION_PERIOD);
    public static JLabel yearsLabel = GUIConstants.makeLabel(YEARS);
    public static JLabel residualValueLabel = GUIConstants.makeLabel(RESIDUAL_VALUE);
    public static JLabel residualPercentLabel = GUIConstants.makeLabel(PERCENT);

    // fuel types labels
    public static JLabel numberFuelTypesLabel = GUIConstants.makeLabel(NUMBER_FUEL_TYPES);
    public static JLabel fuelNameLabel = GUIConstants.makeLabel(NAME);
    public static JLabel costOfFuelLabel = GUIConstants.makeLabel(COST_OF_FUEL);
    public static JLabel yenPerUnitLabel = GUIConstants.makeLabel(YEN_PER_UNIT);
    public static JLabel maximumAnnualFuelSupplyRateLabel = GUIConstants.makeLabel(MAXIMUM_ANNUAL_FUEL_RATE);
    public static JLabel unitsPerYearLabel = GUIConstants.makeLabel(UNITS_PER_YEAR);
    public static JLabel maximumHourlyFuelSupplyRateLabel = GUIConstants.makeLabel(MAXIMUM_HOURLY_FUEL_RATE);
    public static JLabel unitsPerHourLabel = GUIConstants.makeLabel(UNITS_PER_HOUR);
    public static JLabel energeticValueLabel = GUIConstants.makeLabel(ENERGETIC_VALUE);
    public static JLabel joulesPerUnitLabel = GUIConstants.makeLabel(JOULES_PER_UNIT);
    public static JLabel fuelUnitsLabel = GUIConstants.makeLabel(FUEL_UNITS);

    // model results label
    public static final String SELECT_A_SEASON = "selected season: ";
    public static final String BORDER_RESULTS_PANEL = "Tokyo City power demand delivered: ";
    public static final String TITLE = "Power Demand Delivery Curves";
    public static final String DAY = "day";

    public static final String[] comboBoxChoice =
            {
                PEAK_DEMAND,
                AVERAGE_SUMMER_WEEKDAY,
                AVERAGE_SUMMER_WEEKEND,
                AVERAGE_SPRING_FALL_WEEKDAY,
                AVERAGE_SPRING_FALL_WEEKEND,
                AVERAGE_WINTER_WEEKDAY,
                AVERAGE_WINTER_WEEKEND
            };

    public static final String X_AXIS = "Hour of the Day";
	public static final String Y_AXIS = "KW/H";

    public static final JLabel selectSeasonLabel = GUIConstants.makeLabel(SELECT_A_SEASON);

    // property changes for gui listener -> notifies dome object manager
    public static final String DOME_CHANGE_INTEREST_RATE = "dome interest rate";
    public static final String DOME_CHANGE_PROPERTY_TAX = "dome property tax";
    public static final String DOME_CHANGE_EDITABLE_CHART = "dome editable chart";
    public static final String DOME_CHANGE_DAYS_REPRESENTED = "dome days represented";
    public static final String DOME_CHANGE_CONVERSION_EFFICIENCY = "dome conversion efficiency";
    public static final String DOME_CHANGE_INSTALLATION_COST = "dome installation cost";
    public static final String DOME_CHANGE_RUNNING_COST = "dome running cost";
    public static final String DOME_CHANGE_UPPER_LIMIT = "dome upper limit";
    public static final String DOME_CHANGE_LOWER_LIMIT = "dome lower limit";

    // dome parameters
    public static final String CONVERSION_EFFICIENCY_PARAMETER = "conversion efficiency";
    public static final String INSTALLATION_COST_PARAMETER = "installation cost";
    public static final String RUNNING_COST_PARAMETER = "running cost";
    public static final String INCREASING_CONSTRAINT_PARAMETER = "increasing constraint";
    public static final String DECREASING_CONSTRAINT_PARAMETER = "decreasing constraint";
    public static final String UPPER_LIMIT_PARAMETER = "upper limit";
    public static final String LOWER_LIMIT_PARAMETER = "lower limit";

    // parameter names
    public static final String GENERATED_POWER_SUPPLY_PARAMETER = "generated power supply";

    /**
     *  note: this method was borred from the dome3 source code
     *  developed at the MIT Cadlab (author: Elaine Yang)
     *  Massachusetts Institute of Technology
     *  (c) 2003 - all rights reserved.
     */

    public static void layoutGridBag(JPanel c, JComponent[] comps,
                                     GridBagConstraints[] gbcs)
    {
        GridBagLayout gridbag = new GridBagLayout();
        c.setLayout(gridbag);
        for (int i = 0; i < gbcs.length; ++i)
        {
            gridbag.setConstraints(comps[i], gbcs[i]);
            c.add(comps[i]);
        }
    }

    /**
     * method for creating a label with correct configuration
     * @param label
     * @return JLabel
     */
    public static JLabel makeLabel(String label)
    {
        JLabel l = new JLabel(label);
        l.setFont(REGULAR_FONT);
        return l;
    }

    public static JLabel makeIconLabel(String label)
    {
        JLabel l = new JLabel(label);
        l.setFont(ITALIC_SMALL_FONT);
        return l;
    }

    /**
     * method for creating a button with correct configuration
     * @param buttonLabel
     * @return JButton
     */
    public static JButton makeButton(String buttonLabel)
    {
        JButton b = new JButton(buttonLabel);
        b.setFont(REGULAR_FONT);
        return b;
    }

    /**
     * method for creating a combo box with correct configuration
     * @param comboBoxContent
     * @return JComobBox
     */
    public static JComboBox makeComboBox(String[] comboBoxContent)
    {
        JComboBox cb = new JComboBox(comboBoxContent);
        cb.setFont(REGULAR_FONT);
        return cb;
    }

    /**
     * method for creating a text field with consisten gui configuration
     * @return JTextField
     */
    public static JTextField makeTextField()
    {
        JTextField tf = new JTextField();
        tf.setFont(REGULAR_FONT);
        tf.setEditable(true);
        tf.setHorizontalAlignment(SwingConstants.RIGHT);
        return tf;
    }

    /**
     * makes the globe icon that appears at the InputDataSpecficationPanel
     */
    public static ImageIcon makeIcon(String iconPath)
    {
        URL iconURL = ClassLoader.getSystemResource(iconPath);
        if (iconURL == null) {
            throw new RuntimeException("File not found: " + iconPath);
        }
        return new ImageIcon(iconURL);
    }

    public static JRadioButton makeRadioButton()
	{
		JRadioButton rb = new JRadioButton();
		rb.setFocusPainted(false);
		rb.setFont(REGULAR_FONT);
		return rb;
	}

    public static final JLabel globeIconLabel = new JLabel(GLOBE_ICON);
}
