package mit.cadlab.dome3.plugin.Solidworks;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.*;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 13, 2005
 * Time: 2:19:52 PM
 * To change this template use Options | File Templates.
 */
public class SolidworksPluginNew extends AbstractPlugin
{
	private Vector data;
	private Vector files;
	private SolidworksPluginCaller caller;
	private long modelPtr;
	private String file;
	private boolean isVisible;
	private static Integer numberModelsRemaining = new Integer(0);

	public SolidworksPluginNew(String libname, String file, boolean isVisible)
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		this.isVisible = isVisible;
		data = new Vector();
		files = new Vector();
		System.loadLibrary(libname);
		caller = new SolidworksPluginCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[2];
		arr[0] = file;
		Boolean isVis = new Boolean(isVisible);
		arr[1] = isVis;
		modelPtr = caller.callObjectFunc(0, SolidworksPluginCaller.MODEL_INIT, arr);
	}

	public void loadModel()
	{
		synchronized (numberModelsRemaining) {
		caller.callVoidFunc(modelPtr, SolidworksPluginCaller.MODEL_LOAD, null);
			numberModelsRemaining = new Integer(numberModelsRemaining.intValue()+1);
		}
	}

	public void unloadModel()
	{
		synchronized (numberModelsRemaining) {
			boolean isLoaded = this.isModelLoaded();
            Object[] arr = {numberModelsRemaining};
			caller.callVoidFunc(modelPtr, SolidworksPluginCaller.MODEL_UNLOAD, arr);
			//this is because numberModelsRemaining only increases when loadModel is called
			if(isLoaded) numberModelsRemaining = new Integer(numberModelsRemaining.intValue()-1);
		}
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(modelPtr, SolidworksPluginCaller.MODEL_IS_LOADED, null);
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(modelPtr, SolidworksPluginCaller.MODEL_EXEC_BF_INPUT, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(modelPtr, SolidworksPluginCaller.MODEL_EXEC_AF_INPUT, null);
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof SolidworksDimensionNew) {
				if (!((SolidworksDimensionNew) obj).getIsResult()) ((SolidworksDimensionNew) obj).loadNativeData();
			}
		}
		caller.callVoidFunc(modelPtr, SolidworksPluginCaller.MODEL_EXECUTE, null);
		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof SolidworksDimensionNew) {
					if (((SolidworksDimensionNew) obj).getIsResult()) ((SolidworksDimensionNew) obj).loadJavaData();
				} else if (obj instanceof SolidworksColorsNew) {
					if (((SolidworksColorsNew) obj).getIsResult()) ((SolidworksColorsNew) obj).loadJavaData();
				} else if (obj instanceof SolidworksLengthUnitNew) {
					if (((SolidworksLengthUnitNew) obj).getIsResult()) ((SolidworksLengthUnitNew) obj).loadJavaData();
				} else if (obj instanceof SolidworksAngleUnitNew) {
					if (((SolidworksAngleUnitNew) obj).getIsResult()) ((SolidworksAngleUnitNew) obj).loadJavaData();
				} else if (obj instanceof SolidworksVolumeNew) {
					if (((SolidworksVolumeNew) obj).getIsResult()) ((SolidworksVolumeNew) obj).loadJavaData();
				} else if (obj instanceof SolidworksMassNew) {
					if (((SolidworksMassNew) obj).getIsResult()) ((SolidworksMassNew) obj).loadJavaData();
				} else if (obj instanceof SolidworksSurfaceAreaNew) {
					if (((SolidworksSurfaceAreaNew) obj).getIsResult()) ((SolidworksSurfaceAreaNew) obj).loadJavaData();
				}
			}
		}
	}

	public void deleteModel()
	{
		caller.callVoidFunc(modelPtr, SolidworksPluginCaller.MODEL_DESTROY, null);
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData) iterator.next();
			dat.resetObjectPointer();
		}
	}

	public Object createDimension(String name)
	{
		return createDimension(name, null);
	}

	public SolidworksDimensionNew createDimension(String name, Parameter dReal)
	{
		SolidworksDimensionNew dim = new SolidworksDimensionNew(caller, modelPtr, name, dReal);
		data.addElement(dim);
		return dim;
	}

	public Object createFile(String fileName)
	{
		return createFile(fileName, null);
	}

	public SolidworksFileNew createFile(String fileName, Parameter dFile)
	{
		SolidworksFileNew fil = new SolidworksFileNew(caller, modelPtr, fileName, dFile);
		files.addElement(fil);
		return fil;
	}

	public Object createColors()
	{
		return createColors(null);
	}

	public SolidworksColorsNew createColors(Parameter dVector)
	{
		SolidworksColorsNew col = new SolidworksColorsNew(caller, modelPtr, dVector);
		data.addElement(col);
		return col;
	}

	public Object createLengthUnit()
	{
		return createLengthUnit(null);
	}

	public SolidworksLengthUnitNew createLengthUnit(Parameter dString)
	{
		SolidworksLengthUnitNew lu = new SolidworksLengthUnitNew(caller, modelPtr, dString);
		data.addElement(lu);
		return lu;
	}

	public Object createAngleUnit()
	{
		return createAngleUnit(null);
	}

	public SolidworksAngleUnitNew createAngleUnit(Parameter dString)
	{
		SolidworksAngleUnitNew au = new SolidworksAngleUnitNew(caller, modelPtr, dString);
		data.addElement(au);
		return au;
	}

	public Object createVolume()
	{
		return createVolume(null);
	}

	public SolidworksVolumeNew createVolume(Parameter dReal)
	{
		SolidworksVolumeNew vol = new SolidworksVolumeNew(caller, modelPtr, dReal);
		data.addElement(vol);
		return vol;
	}

	public Object createMass()
	{
		return createMass(null);
	}

	public SolidworksMassNew createMass(Parameter dReal)
	{
		SolidworksMassNew mas = new SolidworksMassNew(caller, modelPtr, dReal);
		data.addElement(mas);
		return mas;
	}

	public Object createSurfaceArea()
	{
		return createSurfaceArea(null);
	}

	public SolidworksSurfaceAreaNew createSurfaceArea(Parameter dReal)
	{
		SolidworksSurfaceAreaNew su = new SolidworksSurfaceAreaNew(caller, modelPtr, dReal);
		data.addElement(su);
		return su;
	}

}

