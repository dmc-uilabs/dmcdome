/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 22, 2003
 * Time: 2:37:15 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.plugin.mathcad;

public class MathcadPluginCaller
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

	public static final int REAL_GET_VALUE          = 10;
	public static final int REAL_SET_VALUE          = 11;

	public static final int MATRIX_SET_VALUES       = 12;
	public static final int MATRIX_SET_ELEMENT      = 13;
	public static final int MATRIX_SET_ROWS         = 14;
	public static final int MATRIX_SET_COLS         = 15;
	public static final int MATRIX_SET_DIM          = 16;
	public static final int MATRIX_GET_VALUES       = 17;
	public static final int MATRIX_GET_ELEMENT      = 18;
	public static final int MATRIX_GET_ROWS         = 19;
	public static final int MATRIX_GET_COLS         = 20;

	public static final int MATRIX_TEST_INT_MATRIX  = 21;



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
