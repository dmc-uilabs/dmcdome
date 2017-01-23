// DomeText.java
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;

public interface DomeText extends DataObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Text", "Text");

	public DomeText getDomeText();

	public String getValue();

	public void setValue(String value);

}
