package DispatchModelGUIRenderer.DispatchModelGUIDataObjects;

import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

/**
 * Created by IntelliJ IDEA.
 * Name: DayDataObject
 * User: jacob
 * Date: Jul 25, 2003
 * Time: 4:33:26 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class DayDataObject
{
	private String _dayName;
	private Integer _numberOfDaysAccounting;
	private Double[] _powerDemandData = new Double[24];

	public DayDataObject(String dayName)
	{
		this._dayName = dayName;
		this._numberOfDaysAccounting = new Integer(0);
		this.populatePowerDemandData();
	}

	protected void populatePowerDemandData()
	{
		for (int i = 0; i < this._powerDemandData.length; i++)
				this._powerDemandData[i] = new Double(0.0);
	}

	public String getDayName()
	{
		return this._dayName;
	}

	public void setDayName(String newTitle)
	{
		this._dayName = newTitle;
	}

	public Integer getNumberOfDaysAccounting()
	{
		return this._numberOfDaysAccounting;
	}

	public void setNumberOfDaysAccounting(Integer newNumber)
	{
		this._numberOfDaysAccounting = newNumber;
	}

	public XYSeriesCollection getXYSeriesCollection()
	{
    	XYSeries xySeries = new XYSeries("");

		xySeries.add(0, 0.0);  // added the first element to be a place holder for JFreeChart

		for(int i = 0; i < this._powerDemandData.length; i++)
				xySeries.add(i+1, this._powerDemandData[i]);

		return new XYSeriesCollection(xySeries);
	}

	public void updatePowerDemandData(int index, double powerDemandValue)
	{
		_powerDemandData[index] = new Double(powerDemandValue);
	}

	public void setXYSeriesCollection(XYSeriesCollection newXYSeriesCollection)
	{
		XYSeries xySeries = newXYSeriesCollection.getSeries(0);

		for(int i = 0; i < this._powerDemandData.length; i++)
				this._powerDemandData[i] = (Double)xySeries.getYValue(i+1);
	}

	public void setPowerDemandData(Number[] newPowerDemandData)
	{
		for(int i=0; i<newPowerDemandData.length; i++)
			_powerDemandData[i] = (Double)newPowerDemandData[i];
	}

}
