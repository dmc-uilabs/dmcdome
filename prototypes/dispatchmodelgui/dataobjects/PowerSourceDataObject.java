package dispatchmodelgui.dataobjects;

import dispatchmodelgui.GUIConstants;
import dispatchmodelgui.dataobjects.FuelDataObject;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 22, 2003
 * Time: 3:53:13 PM
 * To change this template use Options | File Templates.
 */
public class PowerSourceDataObject
{
    private String _name;

    private Double _conversionEfficiency, _installationCost, _runningCost,
                    _increasingConstraint, _decreasingConstraint, _upperLimit,
                    _lowerLimit, _inspectionRatio, _depreciationPeriod, _residualValue;

    private FuelDataObject _fuelDataObject;

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public Double getConversionEfficiency()
    {
        return _conversionEfficiency;
    }

    public void setConversionEfficiency(Double c)
    {
        _conversionEfficiency = c;
    }

    public Double getInstallationCost()
    {
        return _installationCost;
    }

    public void setInstallationCost(Double d)
    {
        _installationCost = d;
    }

    public Double getRunningCost()
    {
        return _runningCost;
    }

    public void setRunningCost(Double a)
    {
        _runningCost = a;
    }

    public Double getIncreasingConstraint()
    {
        return _increasingConstraint;
    }

    public void setIncreasingConstraint(Double a)
    {
        _increasingConstraint = a;
    }

    public Double getDecreasingConstraint()
    {
        return _decreasingConstraint;
    }

    public void setDecreasingConstraint(Double a)
    {
        _decreasingConstraint = a;
    }

    public Double getUpperLimit()
    {
        return _upperLimit;
    }

    public void setUpperLimit(Double a)
    {
        _upperLimit = a;
    }

    public Double getLowerLimit()
    {
        return _lowerLimit;
    }

    public void setLowerLimit(Double a)
    {
        _lowerLimit = a;
    }

    public Double getInspectionRatio()
    {
        return _inspectionRatio;
    }

    public void setInspectionRatio(Double a)
    {
        _inspectionRatio = a;
    }

    public Double getDepreciationPeriod()
    {
        return _depreciationPeriod;
    }

    public void setDepreciationPeriod(Double a)
    {
        _depreciationPeriod = a;
    }

    public Double getResidualValue()
    {
        return _residualValue;
    }

    public void setResidualValue(Double a)
    {
        _residualValue = a;
    }

    public FuelDataObject getFuelDataObject()
    {
        return _fuelDataObject;
    }

    public void setFuelDataObject(FuelDataObject f)
    {
        _fuelDataObject = f;
    }

    public PowerSourceDataObject(String name)
    {
        _name = name;
        _conversionEfficiency = new Double(0.0);
        _installationCost = new Double(0.0);
        _runningCost = new Double(0.0);
        _increasingConstraint = new Double(0.0);
        _decreasingConstraint = new Double(0.0);
        _upperLimit = new Double(0.0);
        _lowerLimit = new Double(0.0);
        _inspectionRatio = new Double(0.0);
        _depreciationPeriod = new Double(0.0);
        _residualValue = new Double(0.0);
        _fuelDataObject = new FuelDataObject(GUIConstants.NONE);
    }



}
