// EnumerationRunPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.EnumerationBuildPanel;

/**
 *
 */
public class EnumerationRunPanel extends EnumerationBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("EnumerationRunPanel");
	public static final String XML_TAG = "enumerationrunpanel";

	public EnumerationRunPanel(EnumerationData enm)
	{
		//super(enm);
		super(enm);
		convertToNotEditable();
	}

	public void convertToNotEditable()
	{
		editButton.setEnabled(false);
		constraintButton.setEnabled(false);
	}


}
