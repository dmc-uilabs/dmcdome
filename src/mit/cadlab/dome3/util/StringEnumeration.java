// StringEnumeration.java
package mit.cadlab.dome3.util;

import java.util.Enumeration;

public interface StringEnumeration extends Enumeration
{

	public int countStrings();

	public String nextString();

}
