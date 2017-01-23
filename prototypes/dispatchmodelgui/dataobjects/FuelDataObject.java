package dispatchmodelgui.dataobjects;

import dispatchmodelgui.GUIConstants;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 22, 2003
 * Time: 6:27:13 PM
 * To change this template use Options | File Templates.
 */
public class FuelDataObject
{
    private String _name, _unit;

    private Double _cost, _annualFlow, _hourlyFlow, _energeticValue;

    public String getName()
    {
        return _name;
    }

    public void setName(String a)
    {
        _name = a;
    }

    public String getUnit()
    {
        return _unit;
    }

    public void setUnit(String unit)
    {
        _unit = unit;
    }

    public Double getCost()
    {
        return _cost;
    }

    public void setCost(Double a)
    {
        _cost = a;
    }

    public Double getAnnualFlow()
    {
        return _annualFlow;
    }

    public void setAnnualFlow(Double flow)
    {
        _annualFlow = flow;
    }

    public Double getHourlyFlow()
    {
        return _hourlyFlow;
    }

    public void setHourlyFlow(Double a)
    {
        _hourlyFlow = a;
    }

    public Double getEnergeticValue()
    {
        return _energeticValue;
    }

    public void setEnergeticValue(Double a)
    {
        _energeticValue = a;
    }

    public FuelDataObject(String name)
    {
        _name = name;
        _unit = GUIConstants.EMPTY_STRING;
        _cost = new Double(0.0);
        _annualFlow = new Double(0.0);
        _hourlyFlow = new Double(0.0);
        _energeticValue = new Double(0.0);
    }
}
