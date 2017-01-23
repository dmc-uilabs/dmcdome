package dispatchmodelgui.dataobjects;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 21, 2003
 * Time: 2:04:40 PM
 * To change this template use Options | File Templates.
 */
public class DayDataObject
{
    private String _name;
    private Integer _daysRepresented;
    private Double[] _powerDemandData = new Double[24];


    public DayDataObject(String name)
    {
        _daysRepresented = new Integer(0);
        _name = name;

        for (int i = 0; i < this._powerDemandData.length; i++)
				this._powerDemandData[i] = new Double(0.0);
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String newName)
    {
        _name = newName;
    }

    public Integer getDaysRepresented()
    {
        return _daysRepresented;
    }

    public void setDaysRepresented(Integer daysRepresented)
    {
        _daysRepresented = daysRepresented;
    }

    public void setPowerDemandData(Double[] d)
    {
        _powerDemandData = d;
    }

    public Double[] getPowerDemandData()
    {
        return _powerDemandData;
    }
}
