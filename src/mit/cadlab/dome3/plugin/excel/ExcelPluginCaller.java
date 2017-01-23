package mit.cadlab.dome3.plugin.excel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 2, 2005
 * Time: 11:19:48 AM
 * To change this template use Options | File Templates.
 */
public class ExcelPluginCaller
{
	public static final int MODEL_INIT				= 0;
	public static final int MODEL_DESTROY		    = 1;
	public static final int MODEL_LOAD				= 2;
	public static final int MODEL_UNLOAD			= 3;
	public static final int MODEL_IS_LOADED			= 4;
	public static final int MODEL_EXECUTE			= 5;
	public static final int MODEL_EXEC_BF_INPUT     = 6;
	public static final int MODEL_EXEC_AF_INPUT     = 7;
	public static final int MODEL_CREATE_REAL		= 8;
	public static final int MODEL_CREATE_STRING     = 9;
	public static final int MODEL_CREATE_MATRIX     = 10;

	public static final int REAL_GET_VALUE          = 11;
	public static final int REAL_SET_VALUE          = 12;
	
	public static final int STRING_GET_VALUE        = 13;
	public static final int STRING_SET_VALUE        = 14;

	public static final int MATRIX_SET_VALUES       = 15;
	public static final int MATRIX_SET_ELEMENT      = 16;
	public static final int MATRIX_SET_ROWS         = 17;
	public static final int MATRIX_SET_COLS         = 18;
	public static final int MATRIX_SET_DIM          = 19;
	public static final int MATRIX_GET_VALUES       = 20;
	public static final int MATRIX_GET_ELEMENT      = 21;
	public static final int MATRIX_GET_ROWS         = 22;
	public static final int MATRIX_GET_COLS         = 23;

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
