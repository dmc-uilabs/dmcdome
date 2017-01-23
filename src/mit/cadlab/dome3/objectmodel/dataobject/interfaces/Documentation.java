// Documentation.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;

/**
 * Documentation is implemented as a complex data object.
 * Later it may appear in parameters. For now it is associated with objects.
 */
public interface Documentation extends DataObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Documentation");
	public static final String XML_TAG = "documentation";
	public static final String RTF_TEXT = "rtfText";
	public static final String TEXT = "text";
	public static final String URL = "url";

	public boolean isEmpty();

	public Documentation getDocumentation();

	public String getRtfText();

	public void setRtfText(String rtfText);

	public String getText();

	public void setText(String text);

	public String getURL();

	public void setURL(String value);

}
