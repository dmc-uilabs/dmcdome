// RealRunPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.RealBuildPanel;

/**
 *  
 */
public class RealRunPanel extends RealBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("RealRunPanel");
	public static final String XML_TAG = "realrunpanel";

	public RealRunPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal real)
	{
		super(real);
		convertToNotEditable();
	}

	public void convertToNotEditable()
	{
		constraintsButton.setEnabled(false);
		unitComboBox.setEnabled(false);
	}
}
