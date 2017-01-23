// RunTreeObjectFactory.java
package mit.cadlab.dome3.gui.guiutils.tree.run;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactoryUtils;
import mit.cadlab.dome3.swing.tree.TreeObject;

public class RunTreeObjectFactory
{

	protected static DomeTreeObjectFactory tObjFactory = createRunTreeObjectFactory();

	protected static DomeTreeObjectFactory createRunTreeObjectFactory()
	{
		DomeTreeObjectFactory f = new DomeTreeObjectFactory("RunTreeObjectFactory");
		registerDomeRunTreeObjects(f);
		return f;
	}

	public static void registerDomeRunTreeObjects(DomeTreeObjectFactory f)
	{
		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                         "mit.cadlab.dome3.gui.guiutils.tree.DefaultDomeTreeObject",
		                         "mit.cadlab.dome3.objectmodel.DomeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContext",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.context.run.ContextRunTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.run.RunParameterTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.run.ProceduralRelationRunTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.run.EqualRelationRunTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilter",
		                         "mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter.FilterRunTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
		                         "mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter.FilterRunTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.visualization.ConcreteVisualization",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.VisualizationRunTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization");

		//todo the two entries below need to be updated for run mode
		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelinterface.AbstractModelInterface",
		                         "mit.cadlab.dome3.gui.objectmodel.modelinterface.ModelInterfaceTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface");

		DomeTreeObjectFactoryUtils.registerListTreeObjects(f);
	}

	public static TreeObject getTreeObject(Object obj)
	{
		return tObjFactory.getTreeObject(obj);
	}

}
