//IOAnalysisPlugin.java
//author: Sittha Sukkasi
//last update: 1/29/2003


package mit.cadlab.dome3.plugin.ioanalysis;

import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.NativeCaller;

import java.util.List;
import java.util.Vector;

public class IOAnalysisPlugin extends AbstractPlugin
{
	public static final String MODEL = "IOAnalysisModel";
	public static final String LDMODEL = "IOAnalysisModel::loadModel";
	public static final String ULDMODEL = "IOAnalysisModel::unloadModel";
	public static final String ISMODELLD = "IOAnalysisModel::isModelLoaded";
	public static final String EXECBFIP = "IOAnalysisModel::executeBeforeInput";
	public static final String EXEC = "IOAnalysisModel::execute";
	public static final String EXECAFOP = "IOAnalysisModel::executeAfterOutput";

	public static final String RDINFO = "IOAnalysisModel::readInfoFile";
	public static final String RDFINAL = "IOAnalysisModel::readFinalDemandsFile";
	public static final String RDLEON = "IOAnalysisModel::readLeontiefFile";
	public static final String GTTOTSEC = "IOAnalysisModel::getTotalSectors";
	public static final String GTTOTAGG = "IOAnalysisModel::getTotalAggSectors";
	public static final String GTTOTDAT = "IOAnalysisModel::getTotalDataTypes";
	public static final String GTTOTCO2 = "IOAnalysisModel::getTotalCO2Types";
	public static final String GTTOTPOL = "IOAnalysisModel::getTotalPollTypes";
	public static final String GTSECNAME = "IOAnalysisModel::getSectorName";
	public static final String GTFULLNAME = "IOAnalysisModel::getFullSectorName";
	public static final String GTAGGNAME = "IOAnalysisModel::getAggSectorName";
	public static final String GTTODAT = "IOAnalysisModel::getTOData";
	public static final String GTAGGTODAT = "IOAnalysisModel::getAggTOData";
	public static final String GTAGGFDDAT = "IOAnalysisModel::getAggFDData";
	public static final String GTAGGCO2DAT = "IOAnalysisModel::getAggCO2Data";
	public static final String GTDISDAT = "IOAnalysisModel::getDisAggCO2Data";
	public static final String STDAT = "IOAnalysisModel::setFDData";
	public static final String CLRDAT = "IOAnalysisModel::clearFDData";
	public static final String RSTRDAT = "IOAnalysisModel::restoreFDData";
	public static final String ADDFD = "IOAnalysisModel::addFDSector";
	public static final String ADDTO = "IOAnalysisModel::addTOSector";
	public static final String ADDCO2 = "IOAnalysisModel::addCO2Sector";
	public static final String ADDPOL = "IOAnalysisModel::addPollutantSector";
	public static final String STPOLCOF = "IOAnalysisModel::setPollutantCoeff";
	public static final String GTPOLCOF = "IOAnalysisModel::getPollutantCoeff";
	public static final String GTPOLDAT = "IOAnalysisModel::getPollutantData";


	private String file;
	Vector data;
	NativeCaller caller;
	long modelPtr;

	public IOAnalysisPlugin(String libname, String file)
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

	public boolean readInfoFile(String fileName)
	{
		Object[] arr = new Object[1];
		arr[0] = new String(fileName);
		return caller.callBoolFunc(MODEL, modelPtr, RDINFO, arr);
	}

	public boolean readFinalDemandsFile(String fileName)
	{
		Object[] arr = new Object[1];
		arr[0] = new String(fileName);
		return caller.callBoolFunc(MODEL, modelPtr, RDFINAL, arr);
	}

	public boolean readLeontiefFile(String fileName)
	{
		Object[] arr = new Object[1];
		arr[0] = new String(fileName);
		return caller.callBoolFunc(MODEL, modelPtr, RDLEON, arr);
	}

	public int getTotalSectors()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTTOTSEC, null);
	}

	public int getTotalAggSectors()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTTOTAGG, null);
	}

	public int getTotalDataTypes()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTTOTDAT, null);
	}

	public int getTotalCO2Types()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTTOTCO2, null);
	}

	public int getTotalPollTypes()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTTOTPOL, null);
	}

	public String getSectorName(int sectorno)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(sectorno);
		return caller.callStringFunc(MODEL, modelPtr, GTSECNAME, arr);
	}

	public String getFullSectorName(int sectorno)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(sectorno);
		return caller.callStringFunc(MODEL, modelPtr, GTFULLNAME, arr);
	}

	public String getAggSectorName(int sectorno)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(sectorno);
		return caller.callStringFunc(MODEL, modelPtr, GTAGGNAME, arr);
	}

	public double getTOData(int sectorno, int datatype)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(sectorno);
		arr[1] = new Integer(datatype);
		return caller.callDoubleFunc(MODEL, modelPtr, GTTODAT, arr);
	}

	public double getAggTOData(int sectorno, int datatype)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(sectorno);
		arr[1] = new Integer(datatype);
		return caller.callDoubleFunc(MODEL, modelPtr, GTAGGTODAT, arr);
	}

	public double getAggFDData(int sectorno, int datatype)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(sectorno);
		arr[1] = new Integer(datatype);
		return caller.callDoubleFunc(MODEL, modelPtr, GTAGGFDDAT, arr);
	}

	public double getAggCO2Data(int sectorno, int datatype)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(sectorno);
		arr[1] = new Integer(datatype);
		return caller.callDoubleFunc(MODEL, modelPtr, GTAGGCO2DAT, arr);
	}

	public double getDisAggCO2Data(int sectorno, int intext, int type)
	{
		Object[] arr = new Object[3];
		arr[0] = new Integer(sectorno);
		arr[1] = new Integer(intext);
		arr[2] = new Integer(type);
		return caller.callDoubleFunc(MODEL, modelPtr, GTDISDAT, arr);
	}

	public void setFDData(double val, int sectorno, int intext)
	{
		Object[] arr = new Object[3];
		arr[0] = new Double(val);
		arr[1] = new Integer(sectorno);
		arr[2] = new Integer(intext);
		caller.callVoidFunc(MODEL, modelPtr, STDAT, arr);
	}

	public void clearFDData()
	{
		caller.callVoidFunc(MODEL, modelPtr, CLRDAT, null);
	}

	public void restoreFDData()
	{
		caller.callVoidFunc(MODEL, modelPtr, RSTRDAT, null);
	}

	public void addFDSector(int sectorno)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(sectorno);
		caller.callVoidFunc(MODEL, modelPtr, ADDFD, arr);
	}

	public void addTOSector(int sectorno)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(sectorno);
		caller.callVoidFunc(MODEL, modelPtr, ADDTO, arr);
	}

	public void addCO2Sector(int sectorno)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(sectorno);
		caller.callVoidFunc(MODEL, modelPtr, ADDCO2, arr);
	}

	public void addPollutantSector(int sectorno)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(sectorno);
		caller.callVoidFunc(MODEL, modelPtr, ADDPOL, arr);
	}

	public void setPollutantCoeff(double val, int sectorno, int type)
	{
		Object[] arr = new Object[3];
		arr[0] = new Double(val);
		arr[1] = new Integer(sectorno);
		arr[2] = new Integer(type);
		caller.callVoidFunc(MODEL, modelPtr, STPOLCOF, arr);
	}

	public double getPollutantCoeff(int sectorno, int type)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(sectorno);
		arr[1] = new Integer(type);
		return caller.callDoubleFunc(MODEL, modelPtr, GTPOLCOF, arr);
	}

	public double getPollutantData(int sectorno, int type)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(sectorno);
		arr[1] = new Integer(type);
		return caller.callDoubleFunc(MODEL, modelPtr, GTPOLDAT, arr);
	}



/*    public void input_data() {
         caller.callVoidFunc(MODEL, modelPtr, INDATA, null);
    }

    public void set_nt(int given_nt) {
        Object[] arr = new Object[1];
        arr[0] = new Integer(given_nt);
        caller.callVoidFunc(MODEL, modelPtr, SETNT, arr);
    }

    public int get_nt() {
        return caller.callIntFunc(MODEL, modelPtr, GTNT, null);
    }

    public int get_nt_start() {
        return caller.callIntFunc(MODEL, modelPtr, GTNTSTART, null);
    }

    public int get_nt_end() {
        return caller.callIntFunc(MODEL, modelPtr, GTNTEND, null);
    }

    public double get_dt() {
        return caller.callDoubleFunc(MODEL, modelPtr, GTDT, null);
    }

    public void initial_update() {
         caller.callVoidFunc(MODEL, modelPtr, INUP, null);
    }

    public void cal_density() {
         caller.callVoidFunc(MODEL, modelPtr, CALDEN, null);
    }

    public void cal_velocity() {
         caller.callVoidFunc(MODEL, modelPtr, CALVEL, null);
    }

    public void cal_vertical_w() {
         caller.callVoidFunc(MODEL, modelPtr, CALVERW, null);
    }

    public void cal_temperature() {
         caller.callVoidFunc(MODEL, modelPtr, CALTEMP, null);
    }

    public void cal_salinity() {
         caller.callVoidFunc(MODEL, modelPtr, CALSAL, null);
    }

    public void cal_suspend() {
         caller.callVoidFunc(MODEL, modelPtr, CALSUS, null);
    }

    public double get_ef(int iPoint, int jPoint) {
        Object[] arr = new Object[2];
        arr[0] = new Integer(iPoint);
        arr[1] = new Integer(jPoint);
        return caller.callDoubleFunc(MODEL, modelPtr, GTEF, arr);
    }

    public void input_dx_dy(double dx, double dy) {
        Object[] arr = new Object[2];
        arr[0] = new Double(dx);
        arr[1] = new Double(dy);
        caller.callVoidFunc(MODEL, modelPtr, INDXDY, arr);
    }

    public void input_max(int imax_set, int jmax_set, int kmax_set) {
        Object[] arr = new Object[3];
        arr[0] = new Integer(imax_set);
        arr[1] = new Integer(jmax_set);
        arr[2] = new Integer(kmax_set);
        caller.callVoidFunc(MODEL, modelPtr, INMAX, arr);
    }

    public void input_controls(double dt, double Cf, int nt_print, int nt_start, int nt_end) {
        Object[] arr = new Object[5];
        arr[0] = new Double(dt);
        arr[1] = new Double(Cf);
        arr[2] = new Integer(nt_print);
        arr[3] = new Integer(nt_start);
        arr[4] = new Integer(nt_end);
        caller.callVoidFunc(MODEL, modelPtr, INCTRL, arr);
    }

    public void input_coeff(double Ax, double Ay, double Az, double Kx, double Ky, double Kz) {
        Object[] arr = new Object[6];
        arr[0] = new Double(Ax);
        arr[1] = new Double(Ay);
        arr[2] = new Double(Az);
        arr[3] = new Double(Kx);
        arr[4] = new Double(Ky);
        arr[5] = new Double(Kz);
        caller.callVoidFunc(MODEL, modelPtr, INCOEFF, arr);
    }

    public void input_data_pythonTestTop() {
        caller.callVoidFunc(MODEL, modelPtr, INTOP, null);
    }

    public void input_data_pythonTestFlag() {
        caller.callVoidFunc(MODEL, modelPtr, INFLAG, null);
    }

    public void input_topography_vector(boolean Bed_change_input, double[][] topographyVector) {
        Object[] arr = new Object[4];
        arr[0] = topographyVector;
        arr[1] = new Integer(topographyVector.length);
        arr[2] = new Integer(topographyVector[0].length);
        arr[3] = new Boolean(Bed_change_input);
        caller.callVoidFunc(MODEL, modelPtr, TOPOVEC, arr);
    }

    public double[][] OutputElevation_python() {
        return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTEL, null);
    }

    public double[][] OutputUVResidual_python() {
        return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTUVR, null);
    }

    public double[][] OutputTSAverage_python() {
        return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTTSA, null);
    }

    public double[][] out_flowfield_python(int field_type) {
        Object[] arr = new Object[1];
        arr[0] = new Integer(field_type);
        return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTFF, arr);
    }

    public double[][] OutputUVinZlevel_python(int nSigmaLevel, double dZlevel ) {
        Object[] arr = new Object[2];
        arr[0] = new Integer(nSigmaLevel);
        arr[1] = new Double(dZlevel);
        return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTUVZ, arr);
    }

    */


}

