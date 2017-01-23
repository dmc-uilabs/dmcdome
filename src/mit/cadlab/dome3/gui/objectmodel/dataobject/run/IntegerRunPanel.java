// IntegerRunPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.IntegerBuildPanel;

/**
 *  
 */
public class IntegerRunPanel extends IntegerBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("IntegerRunPanel");
	public static final String XML_TAG = "integerrunpanel";

	public IntegerRunPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger dInt)
	{
		super(dInt);
		convertToNotEditable();
	}

	public void convertToNotEditable()
	{
		constraintsButton.setEnabled(false);
		unitComboBox.setEnabled(false);
	}
}
