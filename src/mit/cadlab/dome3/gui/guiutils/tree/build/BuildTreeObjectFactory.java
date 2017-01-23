// BuildTreeObjectFactory.java
package mit.cadlab.dome3.gui.guiutils.tree.build;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactoryUtils;
import mit.cadlab.dome3.swing.tree.TreeObject;

public class BuildTreeObjectFactory
{

	protected static DomeTreeObjectFactory tObjFactory = createBuildTreeObjectFactory();

	protected static DomeTreeObjectFactory createBuildTreeObjectFactory()
	{
		DomeTreeObjectFactory f = new DomeTreeObjectFactory("BuildTreeObjectFactory");
		registerDomeBuildTreeObjects(f);
		return f;
	}

	public static void registerDomeBuildTreeObjects(DomeTreeObjectFactory f)
	{
		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                         "mit.cadlab.dome3.gui.guiutils.tree.DefaultDomeTreeObject",
		                         "mit.cadlab.dome3.objectmodel.DomeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build.BuildParameterTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationBuildTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.EqualRelationBuildTreeObject");

        f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.build.IterationRelationBuildTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.visualization.ConcreteVisualization",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.VisualizationBuildTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilter",
		                         "mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter.FilterBuildTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
		                         "mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter.FilterBuildTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextBuilderTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.subscription.AbstractSubscription",
		                         "mit.cadlab.dome3.gui.objectmodel.modelobject.subscription.SubscriptionTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager",
		                         "mit.cadlab.dome3.gui.objectmodel.modelinterface.ModelInterfaceManagerTreeObject");

        f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager",
                                 "mit.cadlab.dome3.gui.objectmodel.toolinterface.ToolInterfaceManagerTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelinterface.AbstractModelInterface",
		                         "mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceBuildTreeObject",
		                         "mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface");

        f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.toolinterface.AbstractAnalysisToolInterface",
                                 "mit.cadlab.dome3.gui.objectmodel.toolinterface.build.ToolInterfaceBuildTreeObject",
                                 "mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface");

		f.registerTreeObjectInfo("mit.cadlab.dome3.gui.guiutils.clipboard.Clipboard",
		                         "mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection",
		                         "mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelectionTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo",
		                          "mit.cadlab.dome3.gui.objectmodel.project.build.BuildProjectResourceTreeObject",
		                          "mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo$LocationInfo",
		                               "mit.cadlab.dome3.gui.objectmodel.project.ProjectResourceLocationTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.project.BrowseInterface",
		                               "mit.cadlab.dome3.gui.objectmodel.project.BrowseInterfaceTreeObject");

		f.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo",
		                               "mit.cadlab.dome3.gui.objectmodel.project.build.BuildProjectIntegrationModelTreeObject",
		                               "mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo");

		DomeTreeObjectFactoryUtils.registerListTreeObjects(f);
	}

	public static TreeObject getTreeObject(Object obj)
	{
		return tObjFactory.getTreeObject(obj);
	}

}
