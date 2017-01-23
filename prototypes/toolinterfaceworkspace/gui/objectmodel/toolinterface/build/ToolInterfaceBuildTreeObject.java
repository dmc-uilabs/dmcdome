package toolinterfaceworkspace.gui.objectmodel.toolinterface.build;

import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterface;
import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterfaceBuilder;
import toolinterfaceworkspace.gui.objectmodel.toolinterface.ToolInterfaceTreeObject;
import toolinterfaceworkspace.gui.objectmodel.toolinterface.build.qmoointerface.QMOOInterfaceBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;

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

		ToolInterfaceBuilder mi = (ToolInterfaceBuilder) getDomeObject();
		gui = new DomeBuildFrame(new QMOOInterfaceBuildPanel(mi));
	}

}
