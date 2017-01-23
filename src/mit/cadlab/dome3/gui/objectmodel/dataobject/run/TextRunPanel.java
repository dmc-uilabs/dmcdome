// TextRunPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.TextBuildPanel;

/**
 *
 */
public class TextRunPanel extends TextBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("TextRunPanel");
	public static final String XML_TAG = "textrunpanel";

	public TextRunPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText txt)
	{
		super(txt);
		convertToNotEditable();
	}

	public void convertToNotEditable()
	{
		constraintsButton.setEnabled(false);
	}
}
