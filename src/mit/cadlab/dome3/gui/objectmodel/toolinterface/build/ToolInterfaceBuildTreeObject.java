package mit.cadlab.dome3.gui.objectmodel.toolinterface.build;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.ToolInterfaceTreeObject;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.optimisation.OptimisationInterfaceBuildPanel;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.AnalysisToolInterfaceBase;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 22, 2003
 * Time: 8:18:30 AM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceBuildTreeObject extends ToolInterfaceTreeObject
{

	public ToolInterfaceBuildTreeObject(ToolInterface mi)
	{
		super(mi);
	}

	protected void makeGui()
	{
        //TODO:
        //TODO: this will be a quick fix for now,
        //TODO: but in the future it will have to
        //TODO: be changed
        //TODO:

		AnalysisToolInterfaceBase mi = (AnalysisToolInterfaceBase) getDomeObject();
		gui = new DomeBuildFrame(new OptimisationInterfaceBuildPanel(mi));
	}

}
