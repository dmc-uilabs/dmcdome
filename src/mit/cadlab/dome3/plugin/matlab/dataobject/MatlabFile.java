package mit.cadlab.dome3.plugin.matlab.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;

public class MatlabFile extends AbstractPluginData
{
	private FileData data;
	protected boolean isResult;

	public MatlabFile(Parameter realParam, boolean isResult)
	{
		this.parameter = realParam;
		if (parameter == null)
			data = new FileData();
		else
			data = (FileData) parameter.getCurrentDataObject();
		this.isResult = isResult;
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	public void setIsResult(boolean val)
	{
		isResult = val;
	}

    public void notifyFileChanged() {
        data.notifyFileChanged();
    }

	// destroy native object if it's still around
	// when finalize called from garbage collection
	public void finalize()
	{
	}

	public void resetObjectPointer() {
	}

	public String toString()
	{
		return ("MatlabFile: " + data);
	}
}
