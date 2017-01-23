/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 22, 2003
 * Time: 2:37:15 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.plugin;

public class SolidworksNativeCaller
{
	public native long callConstructor(String clas, Object[] args);

	public native void callDestructor(String clas, long ptr);

	//return single dimensional array of doubles
	public native double[] callDoubleArrayFunc(String clas, long ptr, String func, Object[] args);

	public native int[] callIntArrayFunc(String clas, long ptr, String func, Object[] args);

	public native double[][] call2DimDoubleArrayFunc(String clas, long ptr, String func, Object[] args);

	public native int[][] call2DimIntArrayFunc(String clas, long ptr, String func, Object[] args);

	public native long callObjectFunc(String clas, long ptr, String func, Object[] args);

	public native void callVoidFunc(String clas, long ptr, String func, Object[] args);

	public native boolean callBoolFunc(String clas, long ptr, String func, Object[] args);

	public native int callIntFunc(String clas, long ptr, String func, Object[] args);

	public native double callDoubleFunc(String clas, long ptr, String func, Object[] args);

	public native String callStringFunc(String clas, long ptr, String func, Object[] args);
}
