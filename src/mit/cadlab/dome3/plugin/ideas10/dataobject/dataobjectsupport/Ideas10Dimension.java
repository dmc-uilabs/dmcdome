package mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport;

import com.sdrc.openideas.OI_Dimension;
import com.sdrc.openideas.OI_Part;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import org.omg.CORBA.IntHolder;

public class Ideas10Dimension extends AbstractPluginData
{
    private OI_Part oiPart;
    private OI_Dimension oiDim;
    private IntHolder intHolder = new IntHolder();
    private StringData dimName;
	private DomeReal dimValue;
	private boolean isResult;

	public Ideas10Dimension(String name, OI_Part part, Parameter value)
	{
		this.parameter = value;
        this.oiPart = part;
        OI_Dimension dim = oiPart.GetKeyDimension(name);
        if (dim == null)
            throw new RuntimeException("Ideas10Dimension: dimension '"+ name+"' does not exist in the part");
        else
            oiDim = dim;
		dimName = new StringData(name);
		if (value == null)
			dimValue = new RealData();
		else
			dimValue = (RealData)value.getCurrentDataObject();
		isResult = true;
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	public void setIsResult(boolean val)
	{
		isResult = val;
	}

	public String getDimName()
	{
		return dimName.getValue();
	}

	public double getDimValue()
	{
		return dimValue.getValue();
	}

    public void setDimName(String value) {
        dimName.setValue(value);
    }

    public void setDimValue(double value) {
        dimValue.setValue(value);
    }

	public String getDimName(boolean isNativeCall)
	{
		return oiDim.GetName(intHolder);
	}

	public double getDimValue(boolean isNativeCall)
	{
		return oiDim.GetValue(intHolder);
	}

    public void setDimName(String name, boolean isNativeCall) {
        oiDim.ModifyName(name);
    }

	public void setDimValue(double value, boolean isNativeCall)
	{
        int error = oiDim.ModifyValue(value);
        if (error != 0) {
	        String msg = oiDim.GetErrorMessage(error);
            System.err.println(msg);
	        throw new RuntimeException(msg);
        }
        oiPart.Update(true);
	}

	public void loadJavaData()
	{
		//setDimName(getDimName(true));
		setDimValue(getDimValue(true));
	}

	public void loadNativeData()
	{
		setDimValue(getDimValue(), true);
        //setDimName(getDimName(), true);
	}

	public void resetObjectPointer() {
	}
}