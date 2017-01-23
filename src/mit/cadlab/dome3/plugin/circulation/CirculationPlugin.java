//CirculationPlugin.java
//author: Sittha Sukkasi
//last update: 1/28/2003

package mit.cadlab.dome3.plugin.circulation;

import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.NativeCaller;

import java.util.List;
import java.util.Vector;

public class CirculationPlugin extends AbstractPlugin
{
	public static final String MODEL = "CirculationModel";
	public static final String LDMODEL = "CirculationModel::loadModel";
	public static final String ULDMODEL = "CirculationModel::unloadModel";
	public static final String ISMODELLD = "CirculationModel::isModelLoaded";
	public static final String EXECBFIP = "CirculationModel::executeBeforeInput";
	public static final String EXEC = "CirculationModel::execute";
	public static final String EXECAFOP = "CirculationModel::executeAfterOutput";
	public static final String INDATA = "CirculationModel::input_data";
	public static final String SETNT = "CirculationModel::set_nt";
	public static final String GTNT = "CirculationModel::get_nt";
	public static final String GTNTSTART = "CirculationModel::get_nt_start";
	public static final String GTNTEND = "CirculationModel::get_nt_end";
	public static final String GTDT = "CirculationModel::get_dt";
	public static final String INUP = "CirculationModel::initial_update";
	public static final String CALDEN = "CirculationModel::cal_density";
	public static final String CALVEL = "CirculationModel::cal_velocity";
	public static final String CALVERW = "CirculationModel::cal_vertical_w";
	public static final String CALTEMP = "CirculationModel::cal_temperature";
	public static final String CALSAL = "CirculationModel::cal_salinity";
	public static final String CALSUS = "CirculationModel::cal_suspend";
	public static final String GTEF = "CirculationModel::get_ef";
	public static final String INDXDY = "CirculationModel::input_dx_dy";
	public static final String INMAX = "CirculationModel::input_max";
	public static final String INCTRL = "CirculationModel::input_controls";
	public static final String INCOEFF = "CirculationModel::input_coeff";
	public static final String INTOP = "CirculationModel::input_data_pythonTestTop";
	public static final String INFLAG = "CirculationModel::input_data_pythonTestFlag";
	public static final String OUTEL = "CirculationModel::OutputElevation_python";
	public static final String OUTUVR = "CirculationModel::OutputUVResidual_python";
	public static final String OUTTSA = "CirculationModel::OutputTSAverage_python";
	public static final String OUTFF = "CirculationModel::out_flowfield_python";
	public static final String OUTUVZ = "CirculationModel::OutputUVinZlevel_python";
	public static final String TOPOVEC = "CirculationModel::input_topography_vector";
	public static final String FLAG = "CirculationModel::input_flags";

	private String file;
	Vector data;
	NativeCaller caller;
	long modelPtr;

	public CirculationPlugin(String libname, String file)
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

/*          if(obj instanceof MathematicaReal) {
              if(!((MathematicaReal)obj).getIsResult()) {
              ((MathematicaReal)obj).loadNativeData();   }
          }
          else if(obj instanceof MathematicaMatrix) {
                if(!((MathematicaMatrix)obj).getIsResult()) {
              ((MathematicaMatrix)obj).loadNativeData();}
          }
          else if(obj instanceof MathematicaInteger) {
                if(!((MathematicaInteger)obj).getIsResult()) {
              ((MathematicaInteger)obj).loadNativeData(); }
          }
          else if(obj instanceof MathematicaVector) {
                System.out.println("checkpoint1");
                if(!((MathematicaVector)obj).getIsResult()) {
                     System.out.println("checkpoint2");
              ((MathematicaVector)obj).loadNativeData();
                 System.out.println("checkpoint3");   }
          }
      }
         System.out.println("checkpoint4");
*/      caller.callVoidFunc(MODEL, modelPtr, EXEC, null);
/*         System.out.println("checkpoint5");
      for(int i = 0; i < data.size(); i++) {
            Object obj = data.get(i);
            if(obj instanceof MathematicaReal) {
                if(((MathematicaReal)obj).getIsResult()) {
                    ((MathematicaReal)obj).loadJavaData();
                }
            }
            else if(obj instanceof MathematicaMatrix) {
                if(((MathematicaMatrix)obj).getIsResult()) {
                    ((MathematicaMatrix)obj).loadJavaData();
                }
            }
            else if(obj instanceof MathematicaInteger) {
                if(((MathematicaInteger)obj).getIsResult()) {
                    ((MathematicaInteger)obj).loadJavaData();
                }
            }
            else if(obj instanceof MathematicaVector) {
                if(((MathematicaVector)obj).getIsResult()) {
                    ((MathematicaVector)obj).loadJavaData();
                }
            }
*/
		}
	}

	public void input_data()
	{
		caller.callVoidFunc(MODEL, modelPtr, INDATA, null);
	}

	public void set_nt(int given_nt)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(given_nt);
		caller.callVoidFunc(MODEL, modelPtr, SETNT, arr);
	}

	public int get_nt()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTNT, null);
	}

	public int get_nt_start()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTNTSTART, null);
	}

	public int get_nt_end()
	{
		return caller.callIntFunc(MODEL, modelPtr, GTNTEND, null);
	}

	public double get_dt()
	{
		return caller.callDoubleFunc(MODEL, modelPtr, GTDT, null);
	}

	public void initial_update()
	{
		caller.callVoidFunc(MODEL, modelPtr, INUP, null);
	}

	public void cal_density()
	{
		caller.callVoidFunc(MODEL, modelPtr, CALDEN, null);
	}

	public void cal_velocity()
	{
		caller.callVoidFunc(MODEL, modelPtr, CALVEL, null);
	}

	public void cal_vertical_w()
	{
		caller.callVoidFunc(MODEL, modelPtr, CALVERW, null);
	}

	public void cal_temperature()
	{
		caller.callVoidFunc(MODEL, modelPtr, CALTEMP, null);
	}

	public void cal_salinity()
	{
		caller.callVoidFunc(MODEL, modelPtr, CALSAL, null);
	}

	public void cal_suspend()
	{
		caller.callVoidFunc(MODEL, modelPtr, CALSUS, null);
	}

	public double get_ef(int iPoint, int jPoint)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(iPoint);
		arr[1] = new Integer(jPoint);
		return caller.callDoubleFunc(MODEL, modelPtr, GTEF, arr);
	}

	public void input_dx_dy(double dx, double dy)
	{
		Object[] arr = new Object[2];
		arr[0] = new Double(dx);
		arr[1] = new Double(dy);
		caller.callVoidFunc(MODEL, modelPtr, INDXDY, arr);
	}

	public void input_max(int imax_set, int jmax_set, int kmax_set)
	{
		Object[] arr = new Object[3];
		arr[0] = new Integer(imax_set);
		arr[1] = new Integer(jmax_set);
		arr[2] = new Integer(kmax_set);
		caller.callVoidFunc(MODEL, modelPtr, INMAX, arr);
	}

	public void input_controls(double dt, double Cf, int nt_print, int nt_start, int nt_end)
	{
		Object[] arr = new Object[5];
		arr[0] = new Double(dt);
		arr[1] = new Double(Cf);
		arr[2] = new Integer(nt_print);
		arr[3] = new Integer(nt_start);
		arr[4] = new Integer(nt_end);
		caller.callVoidFunc(MODEL, modelPtr, INCTRL, arr);
	}

	public void input_coeff(double Ax, double Ay, double Az, double Kx, double Ky, double Kz)
	{
		Object[] arr = new Object[6];
		arr[0] = new Double(Ax);
		arr[1] = new Double(Ay);
		arr[2] = new Double(Az);
		arr[3] = new Double(Kx);
		arr[4] = new Double(Ky);
		arr[5] = new Double(Kz);
		caller.callVoidFunc(MODEL, modelPtr, INCOEFF, arr);
	}

	public void input_data_pythonTestTop()
	{
		caller.callVoidFunc(MODEL, modelPtr, INTOP, null);
	}

	public void input_data_pythonTestFlag()
	{
		caller.callVoidFunc(MODEL, modelPtr, INFLAG, null);
	}

	public void input_topography_vector(boolean Bed_change_input, double[][] topographyVector)
	{
		Object[] arr = new Object[4];
		arr[0] = topographyVector;
		arr[1] = new Integer(topographyVector.length);
		arr[2] = new Integer(topographyVector[0].length);
		arr[3] = new Boolean(Bed_change_input);
		caller.callVoidFunc(MODEL, modelPtr, TOPOVEC, arr);
	}

	public void input_flags(int[][] flagE, int[][] flagU, int[][] flagV,
	                        int[][] flagT, int[][] flagS, int[][] flagSus)
	{
		Object[] arr = new Object[18];
		arr[0] = flagE;
		arr[1] = new Integer(flagE.length);
		arr[2] = new Integer(flagE[0].length);
		arr[3] = flagU;
		arr[4] = new Integer(flagU.length);
		arr[5] = new Integer(flagU[0].length);
		arr[6] = flagV;
		arr[7] = new Integer(flagV.length);
		arr[8] = new Integer(flagV[0].length);
		arr[9] = flagT;
		arr[10] = new Integer(flagT.length);
		arr[11] = new Integer(flagT[0].length);
		arr[12] = flagS;
		arr[13] = new Integer(flagS.length);
		arr[14] = new Integer(flagS[0].length);
		arr[15] = flagSus;
		arr[16] = new Integer(flagSus.length);
		arr[17] = new Integer(flagSus[0].length);
		caller.callVoidFunc(MODEL, modelPtr, FLAG, arr);
	}

	public double[][] OutputElevation_python()
	{
		return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTEL, null);
	}

	public double[][] OutputUVResidual_python()
	{
		return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTUVR, null);
	}

	public double[][] OutputTSAverage_python()
	{
		return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTTSA, null);
	}

	public double[][] out_flowfield_python(int field_type)
	{
		Object[] arr = new Object[1];
		arr[0] = new Integer(field_type);
		return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTFF, arr);
	}

	public double[][] OutputUVinZlevel_python(int nSigmaLevel, double dZlevel)
	{
		Object[] arr = new Object[2];
		arr[0] = new Integer(nSigmaLevel);
		arr[1] = new Double(dZlevel);
		return caller.call2DimDoubleArrayFunc(MODEL, modelPtr, OUTUVZ, arr);
	}


}

