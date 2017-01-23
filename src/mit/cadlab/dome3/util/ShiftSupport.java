// ShiftSupport.java
package mit.cadlab.dome3.util;

/**
 * items which permit users to order elements within it should implement this interface
 */
public interface ShiftSupport
{

	public void shiftLeft(int[] indices);

	public void shiftRight(int[] indices);

}
