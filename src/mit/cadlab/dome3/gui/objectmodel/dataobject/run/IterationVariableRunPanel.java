// IterationVariableRunPanel.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.gui.objectmodel.dataobject.build.IterationVariableBuildPanel;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.RealIterationVariable;

public class IterationVariableRunPanel extends IterationVariableBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("IterationVariableRunPanel");
	public static final String XML_TAG = "iterationvariablerunpanel";

	public IterationVariableRunPanel(RealIterationVariable real)
	{
		super(real);
		convertToNotEditable();
	}

	protected void convertToNotEditable() {
		incrementField.setEditable(false);
		limitField.setEditable(false);
	}

}
