package dispatchmodelgui;

import dispatchmodelgui.dataobjects.DayDataObject;
import dispatchmodelgui.dataobjects.PowerSourceDataObject;
import dispatchmodelgui.dataobjects.FuelDataObject;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Iterator;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 21, 2003
 * Time: 2:32:44 PM
 * To change this template use Options | File Templates.
 */

/**
 * DispatchModelGUIDataObjectManager.java
 *
 * This class is responsible for managing all data objects
 * inside the dispatch model gui.  This class is modular and
 * as a result does not take any arguments into its constructor.
 * The three data object lists that it manages are:
 *
 * 1. DayDataObject list - stores all of the DayDataObject objects that
 * are currently being used in the dispatch model.  The number of
 * DayDataObject objects is 7 by default.
 *
 * 2. PowerSourceDataObject list - stores all of the PowerSourceDataObject
 * objects that are currently being used in the dispatch model.  The number
 * of PowerSourceDataObject objects is 11 by default.
 *
 * 3. FuelDataObject list - stores all of the FuelDataObject objects that are
 * currently being used in the dispatch model.  The number of FuelDataObject
 * objects is 5 by default.
 *
 * Class also contains:
 *          Integer _totalDaysRepresented
 *          Double  _interestRate, _propertyTax
 *
 * Which are going to be explained below.
 *
 */
public class DispatchModelGUIDataObjectManager
{
    private DispatchModelGUIListeners _guiListener;

    // DayDataObject list
    private List _dayDataObjectList = new ArrayList();

    // PowerSourceDataObject list
    private List _powerSourceList = new ArrayList();

    // FuelDataObject list
    private List _fuelDataObjectList = new ArrayList();

    /**
     * variable that holds the sum of the number of
     * days in the year represented by each DayDataObject
     */
    private Integer _totalDaysRepresented;

    // variables that store the interest rate and property tax
    private Double _interestRate, _propertyTax;

    public DispatchModelGUIDataObjectManager()
    {
        // initializing the total number of days represented
        _totalDaysRepresented = new Integer(0);

        // creating default data objects
        createDefaultDataObjects();
    }

    protected void createDefaultDataObjects()
    {
        /**
         * creating default DayDataObjects
         * 7 day data objects created by default.
         */
        for(int i = 0; i < GUIConstants.dayComboBoxChoice.length; i++)
            _dayDataObjectList.add(i, new DayDataObject(GUIConstants.dayComboBoxChoice[i]));

        /**
         * creating defaul PowerSourceDataobjects
         * 11 data objects are created by default
         */
        for (int i = 0; i < GUIConstants.seriesNames.length; i++)
            _powerSourceList.add(i, new PowerSourceDataObject(GUIConstants.seriesNames[i]));

        /**
         * creating default FuelDatObjects
         * 5 fuel data bojects are created by defaul, where
         * the first fuel data objects in a "no fuel type data object"
         */
        for (int i = 0; i < GUIConstants.fuelTypeOptions.length; i++)
            _fuelDataObjectList.add(i, new FuelDataObject(GUIConstants.fuelTypeOptions[i]));

        // interest rate default value of 0.0
        _interestRate = new Double(0.0);

        // property tax default value of 0.0
        _propertyTax = new Double(0.0);
    }

    /**
     * method to get the total number of days
     * in the year, represented by the data
     * @return _totalDaysRepresented
     */
    public Integer totalDayRepresented()
    {
        return _totalDaysRepresented;
    }

    /**
     * This method returns a day data object.
     * @param index
     * @return DayDataObject
     */
    public DayDataObject getDayDataObject(int index)
    {
        return (DayDataObject) _dayDataObjectList.get(index);
    }

    /**
     * This method returns a power source data object
     * @param index
     * @return PowerSourceDataObject
     */
    public PowerSourceDataObject getPowerSourceDataObject(int index)
    {
        return (PowerSourceDataObject) _powerSourceList.get(index);
    }

    /**
     * This method returns a fuel data object
     * @param index
     * @return FuelDataObject
     */
    public FuelDataObject getFuelDataObject(int index)
    {
        return (FuelDataObject) _fuelDataObjectList.get(index);
    }

    /**
     * This method returns the total number of days
     * represented by all day data objects.
     * When a member variable that represents the number
     * of days is set, the list of day data objects is
     * iterated through and the sum of all day data objects
     * and how many days that day object represents is
     * calculated.
     * @return _totalDaysRepresented
     */
    public Integer getTotalNumberOfDaysRepresented()
    {
        // initially 0
        int sum = 0;

        Iterator iterator = _dayDataObjectList.listIterator();

        // iterate through the day data object list
        while(iterator.hasNext())
        {
            /**
             * for each day data object, get the total number of represented days
             * stored in that data object and add that number to the total.
             */
            sum += ((DayDataObject)iterator.next()).getDaysRepresented().intValue();
        }

        // the total number of days is now the variable sum
        _totalDaysRepresented = new Integer(sum);

        return _totalDaysRepresented;
    }

    /**
     * get/set methods
     */

    /**
     * Method to get the day data object list
     * @return List
     */
    public List getDayDataObjectList()
    {
        return _dayDataObjectList;
    }

    /**
     * Method to get the interest rate
     * data object.
     * @return Double
     */
    public Double getInterestRate()
    {
        return _interestRate;
    }

    /**
     * Method to set the interest rate
     * data object.
     * @param value
     */
    public void setInterestRate(Double value)
    {
        _interestRate = value;
    }

    /**
     * Method to get the property tax
     * data object.
     * @return       Double
     */
    public Double getPropertyTax()
    {
        return _propertyTax;
    }

    /**
     * method to set the property tax
     * data object
     * @param value
     */
    public void setPropertyTax(Double value)
    {
        _propertyTax = value;
    }

}
