package mit.cadlab.dome3.plugin.Solidworks;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 11, 2005
 * Time: 4:13:40 PM
 * To change this template use Options | File Templates.
 */
public class SolidworksPluginCaller
{
	public static final int MODEL_INIT				    = 0;
	public static final int MODEL_DESTROY		        = 1;
	public static final int MODEL_LOAD		    		= 2;
	public static final int MODEL_UNLOAD		    	= 3;
	public static final int MODEL_IS_LOADED		    	= 4;
	public static final int MODEL_EXECUTE		    	= 5;
	public static final int MODEL_EXEC_BF_INPUT         = 6;
	public static final int MODEL_EXEC_AF_INPUT         = 7;
	public static final int MODEL_CREATE_ANGLEUNIT	    = 8;
	public static final int MODEL_CREATE_COLORS         = 9;
	public static final int MODEL_CREATE_DIMENSION	    = 10;
	public static final int MODEL_CREATE_FILE           = 11;
	public static final int MODEL_CREATE_LENGTHUNIT     = 12;
	public static final int MODEL_CREATE_MASS           = 13;
	public static final int MODEL_CREATE_SURFACEAREA    = 14;
	public static final int MODEL_CREATE_VOLUME         = 15;

	public static final int ANGLEUNIT_GET_VALUE         = 16;

	public static final int COLORS_GET_VALUES           = 17;
	public static final int COLORS_GET_RED              = 18;
	public static final int COLORS_GET_GREEN            = 19;
	public static final int COLORS_GET_BLUE             = 20;

	public static final int DIMENSION_GET_VALUE         = 21;
	public static final int DIMENSION_SET_VALUE         = 22;

	public static final int FILE_SAVE                   = 23;

	public static final int LENGTHUNIT_GET_VALUE        = 24;

	public static final int MASS_GET_VALUE              = 25;

	public static final int SURFACEAREA_GET_VALUE       = 26;

	public static final int VOLUME_GET_VALUE            = 27;


	public native int       callIntFunc(long ptr, int operation, Object[] args);

	public native void      callVoidFunc(long ptr, int operation, Object[] args);

	public native long      callObjectFunc(long ptr, int operation, Object[] args);

	public native boolean   callBoolFunc(long ptr, int operation, Object[] args);

	public native double    callDoubleFunc(long ptr, int operation, Object[] args);

	public native String    callStringFunc(long ptr, int operation, Object[] args);

	public native int[]     callIntArrayFunc(long ptr, int operation, Object[] args);

	public native int[][]   call2DimIntArrayFunc(long ptr, int operation, Object[] args);

	public native double[]  callDoubleArrayFunc(long ptr, int operation, Object[] args);

	public native double[][] call2DimDoubleArrayFunc(long ptr, int operation, Object[] args);
}
