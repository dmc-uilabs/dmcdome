package DispatchModelGUIRenderer.DispatchModelGUIDataObjects;

/**
 * Created by IntelliJ IDEA.
 * Name: FuelTypeDataObject
 * User: jacob
 * Date: Jul 31, 2003
 * Time: 8:55:32 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class FuelTypeDataObject
{

	public static final String LITERS = "liters";

	private String _fuelName, _fuelUnit;
	private Double _costOfFuel, _maximumAnnualFlow, _maximumHourlyFlow,
						_energeticValue;

	public FuelTypeDataObject(String fuelTypeName)
	{
		this._fuelName = fuelTypeName;
		_fuelUnit = LITERS;
		_costOfFuel = new Double(0.0);
		_maximumAnnualFlow = new Double(0.0);
		_maximumHourlyFlow = new Double(0.0);
		_energeticValue = new Double(0.0);
	}

	public String getFuelName()
	{
		return this._fuelName;
	}

	public void setFuelName(String newName)
	{
		this._fuelName = newName;
	}

	public String getFuelUnit()
	{
		return this._fuelUnit;
	}

	public void setFuelUnit(String newUnit)
	{
		this._fuelUnit = newUnit;
	}

	public void setCostOfFuel(Double newCostOfFuel)
	{
		_costOfFuel = newCostOfFuel;
	}

	public Double getCostOfFuel()
	{
		return _costOfFuel;
	}

	public Double getMaximumAnnualFlow()
	{
		return _maximumAnnualFlow;
	}

	public void setMaximumAnnualFlow(Double newMaximumAnnualFlow)
	{
		_maximumAnnualFlow = newMaximumAnnualFlow;
	}

	public Double getMaximumHourlyFlow()
	{
		return _maximumHourlyFlow;
	}

	public void setMaximumHourlyFlow(Double newMaximumHourlyFlow)
	{
		_maximumHourlyFlow = newMaximumHourlyFlow;
	}

	public Double getEnergeticValue()
	{
		return _energeticValue;
	}

	public void setEnergeticValue(Double newEnergeticValue)
	{
		_energeticValue = newEnergeticValue;
	}
}
