package mit.cadlab.dome3.plugin.ideas10.dataobject;

import com.sdrc.openideas.*;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.ideas10.Ideas10Plugin;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10Dimension;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10MaterialProperty;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10RealValueProperty;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10VrmlFile;
import org.omg.CORBA.IntHolder;

import java.util.List;
import java.util.Vector;

public class Ideas10Part extends AbstractPluginData {
	private OI_ModelFile oiModel;
	private OI_Part oiPart;
	private OI_Bin oiBin;
	private Vector data;
	private StringData dataPartName;
	private StringData dataBinName;
	private IntHolder intHolder = new IntHolder();
	private boolean isResult;
	private Ideas10Plugin plg;

/*	// constructor: creating new part. this is not used yet.
	public Ideas10Part(OI_ModelFile model, String binName, String partName, Ideas10Plugin plg, Parameter p) {
		this.oiModel = model;
		this.parameter = p;
		this.plg = plg;
		if (!hasBin(binName))
			oiBin = createBin(binName);
		else
			oiBin = getBin(binName);
		OI_Part part = oiBin.CreatePart(partName, "", true); // not assigning part number
		if (part == null)
			throw new RuntimeException("Ideas10Plugin.createPart: error in creating part. The part with that name might already exist.");

		this.oiPart = part;
		dataPartName = new StringData(partName);
		dataBinName = new StringData(binName);
		data = new Vector();
		isResult = true;
	}*/

	// constructor: loading existing part
	public Ideas10Part(OI_ModelFile model, OI_Part part, Ideas10Plugin plg, Parameter p) {
		this.parameter = p;
		this.oiModel = model;
		this.oiPart = part;
		this.oiBin = getBinForPart(part);
		this.plg = plg;

		dataPartName = new StringData(oiPart.GetName(intHolder));
		dataBinName = new StringData(oiBin.GetName(intHolder));
		data = new Vector();
		isResult = true;
	}

	// get value from java object
	public String getPartName() {
		return dataPartName.getValue();
	}

	// get value from java object
	public String getBinName() {
		return dataBinName.getValue();
	}

	// set value for java object
	public void setPartName(String name) {
		dataPartName.setValue(name);
	}

	// set value for java object
	public void setBinName(String name) {
		dataBinName.setValue(name);
	}

	// get value from native object
	public String getPartName(boolean isNativeCall) {
		return oiPart.GetName(intHolder);
	}

	// get value from native object
	public String getBinName(boolean isNativeCall) {
		return oiBin.GetName(intHolder);
	}

	public OI_Bin[] getBins() {
		return oiModel.GetBins("*", intHolder);
	}

	public OI_Bin getBin(String name) {
		OI_Bin[] bins = oiModel.GetBins(name, intHolder);
		if (bins.length != 0)
			return bins[0];
		throw new RuntimeException("Ideas10Plugin.getBin: no such bin exists");
	}

	public boolean hasBin(String name) {
		OI_Bin[] bins = getBins();
		boolean isExistingBin = false;
		for (int i = 0; i < bins.length; i++) {
			if (name.equalsIgnoreCase(bins[i].GetName(intHolder))) {
				isExistingBin = true;
				break;
			}
		}
		return isExistingBin;
	}

	public OI_Bin createBin(String name) {
		if (name.equals(""))
			throw new RuntimeException("Ideas10Part.createBin: new bin's name cannot be empty");
		return oiModel.CreateBin(name);
	}

	public OI_Bin getBinForPart(OI_Part part) {
		return oiModel.GetBinById(part.GetBinDetails(intHolder).binId);
	}

	public boolean getIsResult() {
		return isResult;
	}

	public Ideas10MaterialProperty createMaterial(String name, Parameter p) {
		Ideas10MaterialProperty mat = new Ideas10MaterialProperty(name, oiPart, p);
		data.addElement(mat);
		return mat;
	}

	/**
	 * real value properties are mass, volume, solidSurfaceArea, openSurfaceArea
	 * @param name
	 * @param propertyType: constants defined in Ideas10RealValueProperty class
	 */
	public Ideas10RealValueProperty createRealValueProperty(String name, int propertyType, Parameter p) {
		Ideas10RealValueProperty realValProp = new Ideas10RealValueProperty(name, Vectors.create(oiPart), propertyType, p);
		data.addElement(realValProp);
		return realValProp;
	}

	public Ideas10Dimension createDimension(String name, Parameter p) {
		Ideas10Dimension dim = new Ideas10Dimension(name, oiPart, p);
		data.addElement(dim);
		return dim;
	}

	public Ideas10VrmlFile createVrmlFile(Parameter p, Ideas10Plugin plg, DomeFile file) {
		Ideas10VrmlFile _vrmlFile = new Ideas10VrmlFile(plg, this, p, file);
		data.addElement(_vrmlFile);
		return _vrmlFile;
	}

	public String[] getAllDimensionNames() {
		OI_Dimension[] dims = oiPart.GetAllKeyDimensions(intHolder);
		String[] dimNames = new String[dims.length];
		for (int i = 0; i < dims.length; i++) {
			dimNames[i] = dims[i].GetName(intHolder);
		}
		return dimNames;
	}

	public int getNumberLabel() {
		return oiPart.GetLabel();
	}

	/*
	 * make a copy of the OI_Part
	 * @param newPartName
	 * @param newPartNum
	 * @return name of the new part
	 */
	public String copyPart(String newPartName, String newPartNum) {
		try {
			//Check if the part is on the workbench
			OI_Part oldPart = plg.getPart(getPartName());
			if (oldPart == null)
				throw new RuntimeException("Ideas10Part.copyPart: part '"+ getPartName()+"' not found");

			OI_Bin oldBin = getBin(getBinName());
			OI_Bin mainBin = getBin("Main");

			OI_Part newPart = oldBin.CopyPart(oldPart, mainBin,
			        newPartName, newPartNum);
			if (oldBin.GetErrorCode() != 0 || newPart == null) {
				throw new RuntimeException("Ideas10Part.copyPart:could not copy part");
			}
			return newPart.GetName(intHolder);
		} catch (Exception e) {
			throw new RuntimeException("Ideas10Part.copyPart:could not copy part");
		}
	}

	public void load(OI_Root modelItem) {
		OI_WorkbenchServer _wbServer = plg.getOiServer().GetWorkbenchServer();

		if (!_wbServer.IsOnWorkbench((OI_ModelingItem) modelItem)) {
			int err = _wbServer.GetOnto((OI_ModelingItem) modelItem);
			if (err != OI_ErrorCodes.C_NO_ERROR) {
				String msg = "Unable to bring model onto work bench";
				msg += _wbServer.GetErrorMessage(err);
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
		}
	}

	public void loadNativeData() {
		load(oiPart);
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof Ideas10Dimension) {
				if (!((Ideas10Dimension) obj).getIsResult())
					((Ideas10Dimension) obj).loadNativeData();
			}
		}
	}

	public void loadJavaData(List affectedOutputParams) {
		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
			if (plg.isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof Ideas10Dimension) {
					((Ideas10Dimension) obj).loadJavaData();
				} else if (obj instanceof Ideas10MaterialProperty) {
					((Ideas10MaterialProperty) obj).loadJavaData();
				} else if (obj instanceof Ideas10RealValueProperty) {
					((Ideas10RealValueProperty) obj).loadJavaData();
				} else if (obj instanceof Ideas10VrmlFile) {
					((Ideas10VrmlFile) obj).loadJavaData();
				}
			}
		}
	}

	public void resetObjectPointer() {
	}






















/*	public Object createExportFile(String fileType)
	{
		Object _ptexp = new IdeasExportFile(caller, exPartObj, fileType, CREATEEXPORTFILE, CLASS);
		data.addElement(_ptexp);
		return _ptexp;
	}

	public Object createDimensionOut(String dimName)
	{
		return this.createDimensionOut(dimName, null);
	}

	public IdeasDimensionOut createDimensionOut(String dimName, DomeReal real)
	{
		IdeasDimensionOut _ptdimOut = new IdeasDimensionOut(caller, exPartObj, dimName, CREATEDIMOUT, real);
		data.addElement(_ptdimOut);
		return _ptdimOut;
	}

	public Object createDimensionIn(String dimName)
	{
		return this.createDimensionIn(dimName, null);
	}

	public IdeasDimensionIn createDimensionIn(String dimName, DomeReal real)
	{
		IdeasDimensionIn _ptdimIn = new IdeasDimensionIn(caller, exPartObj, dimName, CREATEDIMIN, real);
		data.addElement(_ptdimIn);
		return _ptdimIn;
	}*/


}





