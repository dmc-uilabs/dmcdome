/**
 * Created Jacob Wronski.
 * User: wronski
 * Date: Nov 27, 2002
 * Time: 5:15:06 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.plugin.Solidworks;

import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksAngleUnit;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksColors;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksDimension;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksFile;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksLengthUnit;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksMass;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksSurfaceArea;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksVolume;
import mit.cadlab.dome3.plugin.SolidworksNativeCaller;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class SolidworksPlugin extends AbstractPlugin
{
	public static final String MODEL = "SolidworksModel";
	public static final String LDMODEL = "SolidworksModel::loadModel";
	public static final String ULDMODEL = "SolidworksModel::unloadModel";
	public static final String ISMODELLD = "SolidworksModel::isModelLoaded";
	public static final String EXEC = "SolidworksModel::execute";
	public static final String EXECBFIP = "SolidworksModel::executeBeforeInput";
	public static final String EXECAFOP = "SolidworksModel::executeAfterOutput";
	public static final String CREATEDIM = "SolidworksModel::createDimension";
	public static final String CREATECOL = "SolidworksModel::createColors";
	public static final String CREATELU = "SolidworksModel::createLengthUnit";
	public static final String CREATEAU = "SolidworksModel::createAngleUnit";
	public static final String CREATEVOL = "SolidworksModel::createVolume";
	public static final String CREATEMAS = "SolidworksModel::createMass";
	public static final String CREATESA = "SolidworksModel::createSurfaceArea";
	public static final String CREATEFIL = "SolidworksModel::createFile";


	private Vector data;
	private Vector files;
	private SolidworksNativeCaller caller;
	private long modelPtr;
	private String file;
	private boolean isVisible;

	public SolidworksPlugin(String libname, String file, boolean isVisible)
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		this.isVisible = isVisible;
		data = new Vector();
		files = new Vector();
		System.loadLibrary(libname);
		caller = new SolidworksNativeCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[2];
		arr[0] = file;
		Boolean isVis = new Boolean(isVisible);
		arr[1] = isVis;
		modelPtr = caller.callConstructor(MODEL, arr);
	}

	public void loadModel()
	{
		caller.callVoidFunc(MODEL, modelPtr, LDMODEL, null);
	}

	public void unloadModel()
	{
		caller.callVoidFunc(MODEL, modelPtr, ULDMODEL, null);
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(MODEL, modelPtr, ISMODELLD, null);
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(MODEL, modelPtr, EXECBFIP, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(MODEL, modelPtr, EXECAFOP, null);
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof SolidworksDimension) {
				if (!((SolidworksDimension) obj).getIsResult()) ((SolidworksDimension) obj).loadNativeData();
			}
		}
		caller.callVoidFunc(MODEL, modelPtr, EXEC, null);
		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof SolidworksDimension) {
					if (((SolidworksDimension) obj).getIsResult()) ((SolidworksDimension) obj).loadJavaData();
				} else if (obj instanceof SolidworksColors) {
					if (((SolidworksColors) obj).getIsResult()) ((SolidworksColors) obj).loadJavaData();
				} else if (obj instanceof SolidworksLengthUnit) {
					if (((SolidworksLengthUnit) obj).getIsResult()) ((SolidworksLengthUnit) obj).loadJavaData();
				} else if (obj instanceof SolidworksAngleUnit) {
					if (((SolidworksAngleUnit) obj).getIsResult()) ((SolidworksAngleUnit) obj).loadJavaData();
				} else if (obj instanceof SolidworksVolume) {
					if (((SolidworksVolume) obj).getIsResult()) ((SolidworksVolume) obj).loadJavaData();
				} else if (obj instanceof SolidworksMass) {
					if (((SolidworksMass) obj).getIsResult()) ((SolidworksMass) obj).loadJavaData();
				} else if (obj instanceof SolidworksSurfaceArea) {
					if (((SolidworksSurfaceArea) obj).getIsResult()) ((SolidworksSurfaceArea) obj).loadJavaData();
				}
			}
		}
	}

	public void deleteModel()
	{
		caller.callDestructor(MODEL, modelPtr);
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData) iterator.next();
			dat.resetObjectPointer();
		}
	}

	public Object createDimension(String name)
	{
		return createDimension(name, null);
	}

	public SolidworksDimension createDimension(String name, Parameter dReal)
	{
		SolidworksDimension dim = new SolidworksDimension(caller, modelPtr, name, dReal);
		data.addElement(dim);
		return dim;
	}

	public Object createFile(String fileName)
	{
		return createFile(fileName, null);
	}

	public SolidworksFile createFile(String fileName, Parameter dFile)
	{
		SolidworksFile fil = new SolidworksFile(caller, modelPtr, fileName, dFile);
		files.addElement(fil);
		return fil;
	}

	public Object createColors()
	{
		return createColors(null);
	}

	public SolidworksColors createColors(Parameter dVector)
	{
		SolidworksColors col = new SolidworksColors(caller, modelPtr, dVector);
		data.addElement(col);
		return col;
	}

	public Object createLengthUnit()
	{
		return createLengthUnit(null);
	}

	public SolidworksLengthUnit createLengthUnit(Parameter dString)
	{
		SolidworksLengthUnit lu = new SolidworksLengthUnit(caller, modelPtr, dString);
		data.addElement(lu);
		return lu;
	}

	public Object createAngleUnit()
	{
		return createAngleUnit(null);
	}

	public SolidworksAngleUnit createAngleUnit(Parameter dString)
	{
		SolidworksAngleUnit au = new SolidworksAngleUnit(caller, modelPtr, dString);
		data.addElement(au);
		return au;
	}

	public Object createVolume()
	{
		return createVolume(null);
	}

	public SolidworksVolume createVolume(Parameter dReal)
	{
		SolidworksVolume vol = new SolidworksVolume(caller, modelPtr, dReal);
		data.addElement(vol);
		return vol;
	}

	public Object createMass()
	{
		return createMass(null);
	}

	public SolidworksMass createMass(Parameter dReal)
	{
		SolidworksMass mas = new SolidworksMass(caller, modelPtr, dReal);
		data.addElement(mas);
		return mas;
	}

	public Object createSurfaceArea()
	{
		return createSurfaceArea(null);
	}

	public SolidworksSurfaceArea createSurfaceArea(Parameter dReal)
	{
		SolidworksSurfaceArea su = new SolidworksSurfaceArea(caller, modelPtr, dReal);
		data.addElement(su);
		return su;
	}

}

