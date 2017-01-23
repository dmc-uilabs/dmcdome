package mit.cadlab.dome3.plugin.LanduseAgg;

import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.NativeCaller;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Mar 15, 2003
 * Time: 11:29:51 PM
 * To change this template use Options | File Templates.
 */
public class LanduseAggPlugin extends AbstractPlugin
{
	public static final String MODEL = "LanduseAggModel";
	public static final String LDMODEL = "LanduseAggModel::loadModel";
	public static final String ULDMODEL = "LanduseAggModel::unloadModel";
	public static final String ISMODELLD = "LanduseAggModel::isModelLoaded";
	public static final String EXECBFIP = "LanduseAggModel::executeBeforeInput";
	public static final String EXEC = "LanduseAggModel::execute";
	public static final String EXECAFOP = "LanduseAggModel::executeAfterOutput";

	public static final String RDNAME = "LanduseAggModel::readNameFile";
	public static final String RDTYPE = "LanduseAggModel::readTypeFile";
	public static final String RDDATA = "LanduseAggModel::readDataFile";
	public static final String RDSMAP = "LanduseAggModel::readSelectMapFile";
	public static final String WRTPMAT = "LanduseAggModel::writeTypeMatrixData";
	public static final String WRDATA = "LanduseAggModel::writeData";
	public static final String GTTOTTP = "LanduseAggModel::getTotalTypes";
	public static final String GTTOTFD = "LanduseAggModel::getTotalFields";
	public static final String GTHRSZ = "LanduseAggModel::getHorizontalSize";
	public static final String GTVRSZ = "LanduseAggModel::getVerticalSize";
	public static final String GTSMAPGND = "LanduseAggModel::get_select_map_gndarea";
	public static final String GTSMAPFLR = "LanduseAggModel::get_select_map_flrarea";
	public static final String GTSMAPNUMBLDG = "LanduseAggModel::get_select_map_numbldg";
	public static final String GTBLDGTP = "LanduseAggModel::getBldgTypeSelect";
	public static final String GTBLDGINFO = "LanduseAggModel::getBldgInfo";
	public static final String GTADANAME = "LanduseAggModel::getAdminAreaName";
	public static final String GTTPMAT = "LanduseAggModel::getTypeMatrixData";
	public static final String GTMATTPRTOT = "LanduseAggModel::getMatrixTypeRowTot";
	public static final String GTMATTPCTOT = "LanduseAggModel::getMatrixTypeColTot";
	public static final String GTMESHGND = "LanduseAggModel::get_mesh_gndarea";
	public static final String GTMESHFLR = "LanduseAggModel::get_mesh_flrarea";
	public static final String GTMESHNUMBLDG = "LanduseAggModel::get_mesh_numbldg";
	public static final String SWBLDGTP = "LanduseAggModel::switchBldgType";
	public static final String STUSEADAREA = "LanduseAggModel::set_useAdminArea";
	public static final String STALLADAREA = "LanduseAggModel::set_all_admin_areas";
	public static final String STALLBLDGTP = "LanduseAggModel::set_all_bldg_types";
	public static final String STADAREATP = "LanduseAggModel::set_admin_area_types";
	public static final String STADAREANAME = "LanduseAggModel::set_admin_area_names";
	public static final String STSMAPALL = "LanduseAggModel::set_select_map_all";
	public static final String CALSMAP = "LanduseAggModel::calc_selectMap";
	public static final String STTPMAT = "LanduseAggModel::setTypeMatrixData";


	private String file;
	private boolean isVisible;
	Vector data;
	NativeCaller caller;
	long modelPtr;

	public LanduseAggPlugin(String libname, String file)
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		data = new Vector();
		System.loadLibrary(libname);
		caller = new NativeCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[1];
		arr[0] = file;
		modelPtr = caller.callConstructor(MODEL, arr);
	}

	//TODO  deleteModel should explicitly destroy all the native objects
	public void deleteModel()
	{
		caller.callDestructor(MODEL, modelPtr);
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(MODEL, modelPtr, ISMODELLD, null);
	}

	public void loadModel()
	{
		caller.callVoidFunc(MODEL, modelPtr, LDMODEL, null);
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(MODEL, modelPtr, EXECBFIP, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(MODEL, modelPtr, EXECAFOP, null);
	}

	public void unloadModel()
	{
		caller.callVoidFunc(MODEL, modelPtr, ULDMODEL, null);
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			caller.callVoidFunc(MODEL, modelPtr, EXEC, null);
		}
	}

	public boolean readNameFile(String fileName)
	{
		Object[] arr = new Object[1];
		arr[0] = new String(fileName);
		return caller.callBoolFunc(MODEL, modelPtr, RDNAME, arr);
	}

	public boolean readTypeFile(String fileName)
	{
		Object[] arr = new Object[1];
		arr[0] = new String(fileName);
		return caller.callBoolFunc(MODEL, modelPtr, RDTYPE, arr);
	}

	public boolean readDataFile(String fileName)
	{
		Object[] arr = new Object[1];
		arr[0] = new String(fileName);
		return caller.callBoolFunc(MODEL, modelPtr, RDDATA, arr);
	}

	public boolean readSelectMapFile(String fileName, boolean flag)
	{
		Object[] arr = new Object[2];
		arr[0] = new String(fileName);
		arr[1] = new Boolean(flag);
		return caller.callBoolFunc(MODEL, modelPtr, RDSMAP, arr);
	}

	public void writeTypeMatrixData(String fileName, int type)
	{
		Object[] arr = new Object[2];
		arr[0] = new String(fileName);
		arr[1] = new Integer(type);
		caller.callVoidFunc(MODEL, modelPtr, WRTPMAT, arr);
	}

	public void writeData(String fileName)
	{
		Object[] arr = new Object[1];
		arr[0] = new String(fileName);
		caller.callVoidFunc(MODEL, modelPtr, WRDATA, arr);
	}

	public int getTotalTypes()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTTOTTP, null);
	}

	public int getTotalFields()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTTOTFD, null);
	}

	public double getHorizontalSize()
	{
		return caller.callDoubleFunc(MODEL, modelPtr, GTHRSZ, null);
	}

	public double getVerticalSize()
	{
		return caller.callDoubleFunc(MODEL, modelPtr, GTVRSZ, null);
	}

	public double get_select_map_gndarea()
	{
		return caller.callDoubleFunc(MODEL, modelPtr, GTSMAPGND, null);
	}

	public double get_select_map_flrarea()
	{
		return caller.callDoubleFunc(MODEL, modelPtr, GTSMAPFLR, null);
	}

	public double get_select_map_numbldg()
	{
		return caller.callDoubleFunc(MODEL, modelPtr, GTSMAPNUMBLDG, null);
	}

	public int getBldgTypeSelect(int type)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(type);
		return caller.callIntFunc(MODEL, modelPtr, GTBLDGTP, arr);
	}

	public String getBldgInfo(int type)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(type);
		return caller.callStringFunc(MODEL, modelPtr, GTBLDGINFO, arr);
	}

	public String getAdminAreaName(int type)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(type);
		return caller.callStringFunc(MODEL, modelPtr, GTADANAME, arr);
	}

	public int getTypeMatrixData(int iPoint, int jPoint, int kPoint)
	{
		Object[] arr = new Object[3];
		arr[0] = new Integer(iPoint);
		arr[1] = new Integer(jPoint);
		arr[2] = new Integer(kPoint);
		return caller.callIntFunc(MODEL, modelPtr, GTTPMAT, arr);
	}

	public int getMatrixTypeRowTot()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTMATTPRTOT, null);
	}

	public int getMatrixTypeColTot()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTMATTPCTOT, null);
	}

	public double get_mesh_gndarea(int iPoint, int jPoint)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(iPoint);
		arr[1] = new Integer(jPoint);
		return caller.callDoubleFunc(MODEL, modelPtr, GTMESHGND, arr);
	}

	public double get_mesh_flrarea(int iPoint, int jPoint)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(iPoint);
		arr[1] = new Integer(jPoint);
		return caller.callDoubleFunc(MODEL, modelPtr, GTMESHFLR, arr);
	}

	public int get_mesh_numbldg(int iPoint, int jPoint)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(iPoint);
		arr[1] = new Integer(jPoint);
		return caller.callIntFunc(MODEL, modelPtr, GTMESHNUMBLDG, arr);
	}

	public void switchBldgType(int type)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(type);
		caller.callVoidFunc(MODEL, modelPtr, SWBLDGTP, arr);
	}

	public boolean set_useAdminArea(boolean flag)
	{
		Object[] arr = new Object[1];
		arr[0] = new Boolean(flag);
		return caller.callBoolFunc(MODEL, modelPtr, STUSEADAREA, arr);
	}

	public void set_all_admin_areas(boolean flag)
	{
		Object[] arr = new Object[1];
		arr[0] = new Boolean(flag);
		caller.callVoidFunc(MODEL, modelPtr, STALLADAREA, arr);
	}

	public void set_all_bldg_types(boolean flag)
	{
		Object[] arr = new Object[1];
		arr[0] = new Boolean(flag);
		caller.callVoidFunc(MODEL, modelPtr, STALLBLDGTP, arr);
	}

	public void set_admin_area_types(boolean flag, int type)
	{
		Object[] arr = new Object[2];
		arr[0] = new Boolean(flag);
		arr[1] = new Integer(type);
		caller.callVoidFunc(MODEL, modelPtr, STADAREATP, arr);
	}

	public void set_admin_area_names()
	{
		caller.callVoidFunc(MODEL, modelPtr, STADAREANAME, null);
	}

	public void set_select_map_all(int type)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(type);
		caller.callVoidFunc(MODEL, modelPtr, STSMAPALL, arr);
	}

	public void calc_selectMap(boolean flag)
	{
		Object[] arr = new Object[1];
		arr[0] = new Boolean(flag);
		caller.callVoidFunc(MODEL, modelPtr, CALSMAP, arr);
	}

	public void setTypeMatrixData()
	{
		caller.callVoidFunc(MODEL, modelPtr, STTPMAT, null);
	}


}
