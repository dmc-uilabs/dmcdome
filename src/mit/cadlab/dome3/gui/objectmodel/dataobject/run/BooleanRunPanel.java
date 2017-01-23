// BooleanRunPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.BooleanBuildPanel;

/**
 *  
 */
public class BooleanRunPanel extends BooleanBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("BooleanRunPanel");
	public static final String XML_TAG = "booleanrunpanel";

	public BooleanRunPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean bool)
	{
		super(bool);
		convertToNotEditable();
	}

	public void convertToNotEditable()
	{

	}

}
