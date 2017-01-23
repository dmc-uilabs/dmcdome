package mit.cadlab.dome3.plugin.ug;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 16, 2005
 * Time: 3:23:08 PM
 * To change this template use Options | File Templates.
 */
public class UgPluginCaller
{
	public static final int MODEL_INIT				    = 0;
	public static final int MODEL_DESTROY		        = 1;
	public static final int MODEL_LOAD		    		= 2;
	public static final int MODEL_UNLOAD		    	= 3;
	public static final int MODEL_IS_LOADED		    	= 4;
	public static final int MODEL_EXECUTE		    	= 5;
	public static final int MODEL_EXEC_BF_INPUT         = 6;
	public static final int MODEL_EXEC_AF_INPUT         = 7;
	public static final int MODEL_IMPORT                = 8;
	public static final int MODEL_EXPORT                = 9;
	public static final int MODEL_CREATE_COMPONENT      = 10;
	public static final int MODEL_CREATE_VRMLFILE       = 11;
	public static final int MODEL_CREATE_IMPORTFILE     = 12;

	public static final int COMPONENT_CREATE_DIMENSIONIN    = 13;
	public static final int COMPONENT_CREATE_DIMENSIONOUT   = 14;
	public static final int COMPONENT_CREATE_MASSPROPERTY   = 15;
	public static final int COMPONENT_CREATE_EXPORTFILE     = 16;

    public static final int MASSPROPERTY_GET_VALUE          = 17;

	public static final int DIMENSIONIN_GET_VALUE           = 18;
	public static final int DIMENSIONIN_SET_VALUE           = 19;

	public static final int DIMENSIONOUT_GET_VALUE          = 20;

    public static final int IMPORTFILE_GET_CHANGED          = 21;
	public static final int IMPORTFILE_SET_CHANGED          = 22;

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

