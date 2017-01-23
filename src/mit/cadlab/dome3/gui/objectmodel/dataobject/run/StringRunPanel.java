// StringRunPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.StringBuildPanel;

/**
 *  
 */
public class StringRunPanel extends StringBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("StringRunPanel");
	public static final String XML_TAG = "stringrunpanel";

	public StringRunPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString str)
	{
		super(str);
		convertToNotEditable();
	}

	public void convertToNotEditable()
	{
		constraintsButton.setEnabled(false);
	}
}
