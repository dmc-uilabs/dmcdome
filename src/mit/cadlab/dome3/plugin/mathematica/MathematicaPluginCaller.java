package mit.cadlab.dome3.plugin.mathematica;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 6, 2005
 * Time: 2:00:35 PM
 * To change this template use Options | File Templates.
 */
public class MathematicaPluginCaller
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
	public static final int MODEL_CREATE_MATRIX     = 9;
	public static final int MODEL_CREATE_INTEGER	= 10;
	public static final int MODEL_CREATE_VECTOR     = 11;

	public static final int REAL_GET_VALUE          = 12;
	public static final int REAL_SET_VALUE          = 13;

	public static final int MATRIX_SET_VALUES       = 14;
	public static final int MATRIX_SET_ELEMENT      = 15;
	public static final int MATRIX_SET_ROWS         = 16;
	public static final int MATRIX_SET_COLS         = 17;
	public static final int MATRIX_SET_DIM          = 18;
	public static final int MATRIX_GET_VALUES       = 19;
	public static final int MATRIX_GET_ELEMENT      = 20;
	public static final int MATRIX_GET_ROWS         = 21;
	public static final int MATRIX_GET_COLS         = 22;
	public static final int MATRIX_GET_DIM          = 23;

	public static final int INTEGER_GET_VALUE       = 24;
	public static final int INTEGER_SET_VALUE       = 25;

	public static final int VECTOR_GET_LENGTH       = 26;
	public static final int VECTOR_GET_ELEMENT      = 27;
	public static final int VECTOR_GET_VALUES       = 28;
	public static final int VECTOR_SET_ELEMENT      = 29;
	public static final int VECTOR_SET_VALUES       = 30;


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
