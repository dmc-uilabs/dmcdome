package mit.cadlab.dome3.plugin.groovy.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.excel.ExcelPluginCaller;
import mit.cadlab.dome3.plugin.AbstractPluginData;

/**
 * User: Sangmok Han
 * Date: Aug 15, 2005
 */
public class GroovyEnumeration extends GroovyDataObject {
	private DomeEnumeration data;
	private DomeEnumeration nativeData;
	private boolean isResult;

	public GroovyEnumeration(Parameter enumParam) {
		this.parameter = enumParam;
		if (parameter == null) {
			data = new EnumerationData();
		} else {
			data = (DomeEnumeration) parameter.getCurrentDataObject();
		}
		/* initialize native data */
	    nativeData = new EnumerationData((EnumerationData) parameter.getCurrentDataObject());
	}

	public String toString() {
		return ("GroovyEnumeration: " + data);
	}

    public void destroy() {
        // empty for all catalog data types
    }

    public DataObject getData() {
        return nativeData;
    }

    public boolean getIsResult() {
        return isResult;
    }

    public void setIsResult(boolean val) {
        isResult = val;
    }


    public void loadNativeData() {
        nativeData.setLastSelection(data.getLastSelection());
    }

    public void loadJavaData() {
        data.setLastSelection(nativeData.getLastSelection());
    }


    public void finalize() {
        destroy();
    }

    public void resetObjectPointer() {
        // empty for all catalog data types
    }
}
