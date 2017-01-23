// BrowseTreeObjectFactory.java
package mit.cadlab.dome3.gui.guiutils.tree.browse;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactoryUtils;
import mit.cadlab.dome3.swing.tree.TreeObject;

public class BrowseTreeObjectFactory
{

	protected static DomeTreeObjectFactory tObjFactory = createBrowseTreeObjectFactory();

	protected static DomeTreeObjectFactory createBrowseTreeObjectFactory()
	{
		DomeTreeObjectFactory f = new DomeTreeObjectFactory("BrowseTreeObjectFactory");
		registerDomeBrowseTreeObjects(f);
		return f;
	}

	public static void registerDomeBrowseTreeObjects(DomeTreeObjectFactory f)
	{
		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                         "mit.cadlab.dome3.gui.guiutils.tree.DefaultDomeTreeObject",
		                         "mit.cadlab.dome3.objectmodel.DomeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContext",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.context.ContextTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelobject.context.Context");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.ParameterTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.ProceduralRelationTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.EqualRelationTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilter",
		                         "mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter.FilterTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
		                         "mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter.FilterTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.visualization.ConcreteVisualization",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.VisualizationTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelinterface.AbstractModelInterface",
		                         "mit.cadlab.dome3.gui.objectmodel.modelinterface.ModelInterfaceTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface");

		DomeTreeObjectFactoryUtils.registerListTreeObjects(f);
	}

    // for qmoo models to view variables in the qmoo interface variable view

    public static void registerVariablesBrowseTreeObjects(DomeTreeObjectFactory f)
	{
        f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilter",
                                         "mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter.FilterTreeObject",
                                         "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

        f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
                "mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build.tool.VariableParameterTreeObject",
                "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
	}

    public static void registerObjectivesBrowseTreeObjects(DomeTreeObjectFactory f)
	{
        f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilter",
                                         "mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter.FilterTreeObject",
                                         "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

        f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
                "mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build.tool.ObjectiveParameterTreeObject",
                "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
	}

	public static TreeObject getTreeObject(Object obj)
	{
		return tObjFactory.getTreeObject(obj);
	}

}
