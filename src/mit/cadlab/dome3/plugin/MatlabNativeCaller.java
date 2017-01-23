/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 22, 2003
 * Time: 2:37:15 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.plugin;

public class MatlabNativeCaller
{
	public native long callConstructor(int clas, Object[] args);

	public native void callDestructor(int clas, long ptr);

	//return single dimensional array of doubles
	public native double[] callDoubleArrayFunc(int clas, long ptr, int func, Object[] args);

	public native int[] callIntArrayFunc(int clas, long ptr, int func, Object[] args);

	public native double[][] call2DimDoubleArrayFunc(int clas, long ptr, int func, Object[] args);

	public native int[][] call2DimIntArrayFunc(int clas, long ptr, int func, Object[] args);

	public native long callObjectFunc(int clas, long ptr, int func, Object[] args);

	public native void callVoidFunc(int clas, long ptr, int func, Object[] args);

	public native boolean callBoolFunc(int clas, long ptr, int func, Object[] args);

	public native int callIntFunc(int clas, long ptr, int func, Object[] args);

	public native double callDoubleFunc(int clas, long ptr, int func, Object[] args);

	public native String callStringFunc(int clas, long ptr, int func, Object[] args);
}
