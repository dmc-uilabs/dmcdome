package mit.cadlab.dome3.plugin.ideas10.dataobject;

import com.sdrc.openideas.*;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.ideas10.Ideas10Plugin;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10RealValueProperty;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10VrmlFile;
import org.omg.CORBA.IntHolder;

import java.util.List;
import java.util.Vector;

public class Ideas10Assembly extends AbstractPluginData {
	private OI_ModelFile oiModel;
	private OI_Assembly oiAsmb;
	private OI_Bin oiBin;
	private Vector data;
	private StringData dataAsmbName;
	private StringData dataBinName;
	private IntHolder intHolder = new IntHolder();
	private Ideas10Plugin plg;

	private boolean isResult;

	// constructor: loading existing assembly in ideas
	public Ideas10Assembly(OI_ModelFile model, OI_Assembly asmb, Parameter p, Ideas10Plugin plg) {
		this.parameter = p;
		this.oiModel = model;
		this.oiAsmb = asmb;
		this.oiBin = getBinForAssembly(asmb);
		this.plg = plg;

		dataAsmbName = new StringData(oiAsmb.GetName(intHolder));
		dataBinName = new StringData(oiBin.GetName(intHolder));
		data = new Vector();
		isResult = true;
	}

	// get value from java object
	public String getAssemblyName() {
		return dataAsmbName.getValue();
	}

	// get value from java object
	public String getBinName() {
		return dataBinName.getValue();
	}

	// set value for java object
	public void setAssemblyName(String name) {
		dataAsmbName.setValue(name);
	}

	// set value for java object
	public void setBinName(String name) {
		dataBinName.setValue(name);
	}

	// get value from native object
	public String getAssemblyName(boolean isNativeCall) {
		return oiAsmb.GetName(intHolder);
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
		return oiModel.CreateBin(name);
	}

	public OI_Bin getBinForAssembly(OI_Assembly asmb) {
		return oiModel.GetBinById(asmb.GetBinDetails(intHolder).binId);
	}

	public boolean getIsResult() {
		return isResult;
	}

	//todo test this
	protected Vector getPartChildren() {
		Vector partChildren = new Vector();
		Vector asmbChildren = new Vector();

		OI_ModelingItem[] children = oiAsmb.GetChildModelingItems(intHolder);
		for (int i = 0; i < children.length; i++) {
			Object child = children[i];
			if (child instanceof OI_Part)
				partChildren.add(child);
			else if (child instanceof OI_Part)
				asmbChildren.add(child);
		}

		while (!asmbChildren.isEmpty()) {
			Vector tempAsmbChildren = new Vector();
			for (int i = asmbChildren.size() - 1; i >= 0; i--) {
				OI_ModelingItem[] children2 = ((OI_Assembly) asmbChildren.get(i)).GetChildModelingItems(intHolder);
				asmbChildren.remove(asmbChildren.get(i));
				for (int j = 0; j < children2.length; j++) {
					Object child2 = children2[j];
					if (child2 instanceof OI_Part)
						partChildren.add(child2);
					else if (child2 instanceof OI_Part)
						tempAsmbChildren.add(child2);
				}
			}
			asmbChildren.addAll(tempAsmbChildren);
		}
		return partChildren;
	}

	/**
	 * real value properties are mass, volume, solidSurfaceArea, openSurfaceArea
	 * @param name
	 * @param propertyType: constants defined in Ideas10RealValueProperty class
	 */
	public Ideas10RealValueProperty createRealValueProperty(String name, int propertyType, Parameter realdata) {
		Ideas10RealValueProperty realValProp = new Ideas10RealValueProperty(name, getPartChildren(), propertyType, realdata);
		data.addElement(realValProp);
		return realValProp;
	}

	public Ideas10VrmlFile createVrmlFile(Parameter p, Ideas10Plugin plg, DomeFile f) {
		Ideas10VrmlFile _vrmlFile = new Ideas10VrmlFile(plg, this, p, f);
		data.addElement(_vrmlFile);
		return _vrmlFile;
	}

	public int getNumberLabel() {
		return oiAsmb.GetLabel();
	}

	public void loadJavaData(List affectedOutputParams) {
		setAssemblyName(getAssemblyName(true));
		setBinName(getBinName(true));

		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
			if (plg.isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof Ideas10RealValueProperty) {
					((Ideas10RealValueProperty) obj).loadJavaData();
				} else if (obj instanceof Ideas10VrmlFile) {
					((Ideas10VrmlFile) obj).loadJavaData();
				}
			}
		}
	}

	public void resetObjectPointer() {
	}

	public String copyAssembly(String newAssemblyName, String newAssemblyNum) {
		try {
			//Check if the assembly is on the workbench
			OI_Assembly oldAssembly = plg.getAssembly(getAssemblyName());
			if (oldAssembly == null)
				throw new RuntimeException("Ideas10Assembly.copyAssembly: assembly '" + getAssemblyName() + "' not found");

			OI_Bin oldBin = getBin(getBinName());
			OI_Bin mainBin = getBin("Main");

			OI_Assembly newAssembly = oldBin.CopyAssembly(oldAssembly, mainBin,
			        newAssemblyName, newAssemblyNum);
			if (oldBin.GetErrorCode() != 0 || newAssembly == null) {
				throw new RuntimeException("Ideas10Assembly.copyAssembly:could not copy assembly");
			}
			return newAssembly.GetName(intHolder);
		} catch (Exception e) {
			throw new RuntimeException("Ideas10Assembly.copyAssembly:could not copy assembly");
		}
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





