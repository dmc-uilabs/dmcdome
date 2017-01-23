// ParameterRunPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.run;

import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DocumentationBasePanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build.ParameterBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.DataObjectCards;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

public class ParameterRunPanel extends ParameterBuildPanel
{
	public ParameterRunPanel(Parameter param)
	{
		super(param);
		convertToNotEditable();
	}

	public void convertToNotEditable()
	{
		nameField.setEditable(false);
		dataTypeComboBox.setEnabled(false);
	}

	protected DataObjectCards createDataObjectCards()
	{
		return new DataObjectCards(2); // run
	}

	protected DocumentationBasePanel createDocumentationPanel()
	{
		return new DocumentationRunPanel(dataModel.getDocumentation());
	}

	protected String getMenuContext()
	{
		return ModeContexts.RUN_MODE;
	}

}
