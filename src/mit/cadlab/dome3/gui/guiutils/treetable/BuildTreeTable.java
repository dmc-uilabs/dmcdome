// BuildTreeTable.java
package mit.cadlab.dome3.gui.guiutils.treetable;

import com.sun.java.ObjectTreeTable;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.table.DomeTableObject;
import mit.cadlab.dome3.gui.guiutils.table.DomeTableObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.treetable.Editors.*;
import mit.cadlab.dome3.gui.guiutils.treetable.Renderers.*;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.plugin.PluginMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.tool.ToolMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.OptimizationParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.AbstractSubscription;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.info.*;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeEvent;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeListener;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingNameChangeEvent;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingNameChangeListener;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.table.AbstractTableObject;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

public class BuildTreeTable extends ObjectTreeTable
{

	protected int numberColumns = 3; // name, value, mapping
	protected String[] columnNames = {"name", "value", "mapping"};
	protected int[] columnWidths = {150, 150, 200};
	protected static DomeTableObjectFactory tableObjectFactory = makeTableObjectFactory();

	public BuildTreeTable(DomeTree tree)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   3,
		                                   new String[]{"name", "value", "mapping"},
		                                   tableObjectFactory));
		DomeTable.customizeTable(this);
		setInitialColumnWidths(columnWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

	public BuildTreeTable(DomeTree tree, int[] columnWidths)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   3,
		                                   new String[]{"name", "value", "mapping"},
		                                   tableObjectFactory));
		DomeTable.customizeTable(this);
		setInitialColumnWidths(columnWidths);
		this.columnWidths = columnWidths;
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

	public BuildTreeTable(DomeTree tree, int noCols, String[] colNames, int[] colWidths)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   noCols,
		                                   colNames,
		                                   tableObjectFactory));
		numberColumns = noCols;
		columnNames = colNames;
		DomeTable.customizeTable(this);
		columnWidths = colWidths;
		setInitialColumnWidths(columnWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

	// for optimization models
	public BuildTreeTable(DomeTree tree, String qmooParameterType, int noCols, String[] colNames, int[] colWidths)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   noCols,
		                                   colNames,
		                                   BuildTreeTable.makeToolTableObjectFactory(qmooParameterType)));
		numberColumns = noCols;
		columnNames = colNames;
		DomeTable.customizeTable(this);
		columnWidths = colWidths;
		setInitialColumnWidths(columnWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

	public BuildTreeTable(DomeTree tree, int noCols, String[] colNames, int[] colWidths, boolean isPluginModel)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   noCols,
		                                   colNames,
		                                   tableObjectFactory,
		                                   isPluginModel));
		numberColumns = noCols;
		columnNames = colNames;
		DomeTable.customizeTable(this);
		columnWidths = colWidths;
		setInitialColumnWidths(columnWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}



	public BuildTreeTable(DomeTree tree, int noCols, String[] colNames,
	                      int[] colWidths, boolean defaultInterfaceCellExists,
	                      boolean isModelView)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   noCols,
		                                   colNames,
		                                   tableObjectFactory,
		                                   defaultInterfaceCellExists,
		                                   isModelView));
		numberColumns = noCols;
		columnNames = colNames;
		DomeTable.customizeTable(this);
		columnWidths = colWidths;
		setInitialColumnWidths(columnWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

	public BuildTreeTable(DomeTree tree, boolean isModelView)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   3,
		                                   new String[]{"name", "value", "mapping"},
		                                   tableObjectFactory,
		                                   false, //defaultInterfaceCellExists
		                                   isModelView));
		DomeTable.customizeTable(this);
		setInitialColumnWidths(columnWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

	protected static DomeTableObjectFactory makeTableObjectFactory()
	{
		DomeTableObjectFactory f = new DomeTableObjectFactory("BuildTreeTable.TableObjectFactory",
		                                                      "mit.cadlab.dome3.swing.table.FillerTableObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$DefaultTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$ParameterTableObject",
		                          "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$UneditableTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$UneditableTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$EqualRelationTableObject",
		                          "mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation");
        f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$IterationRelationTableObject",
		                          "mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation");

		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$ProceduralRelationTableObject",
		                          "mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$ProjectResourceInfoTableObject",
		                          "mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.subscription.AbstractSubscription",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$SubscriptionTableObject",
		                          "mit.cadlab.dome3.objectmodel.modelobject.subscription.AbstractSubscription");
		//to use folders in project resources
		f.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.Folder",
		                          "mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject",
		                          "mit.cadlab.dome3.gui.fileSystem.FileSystemObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFile",
		                          "mit.cadlab.dome3.gui.objectmodel.project.build.BrowseModelTableObject",
		                          "mit.cadlab.dome3.gui.fileSystem.DomeFile");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$ProjectIntegrationModelInfoTableObject",
		                          "mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo");
		return f;
	}

	protected static DomeTableObjectFactory makeToolTableObjectFactory(String optimizationParameterType)
	{
		DomeTableObjectFactory f = new DomeTableObjectFactory("BuildTreeTable.TableObjectFactory",
		                                                      "mit.cadlab.dome3.swing.table.FillerTableObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$DefaultTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$UneditableTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$UneditableTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");

		//to use folders in project resources
		f.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.Folder",
		                          "mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject",
		                          "mit.cadlab.dome3.gui.fileSystem.FileSystemObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFile",
		                          "mit.cadlab.dome3.gui.objectmodel.project.build.BrowseModelTableObject",
		                          "mit.cadlab.dome3.gui.fileSystem.DomeFile");

        if(optimizationParameterType.equals(QMOOConfiguration.MODEL_VARIABLE))
            f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
                                      "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$ModelVariableOptimizationTableObject",
                                      "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
        else if(optimizationParameterType.equals(QMOOConfiguration.MODEL_OBJECTIVE))
            f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
                                      "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$ModelObjectiveOptimizationTableObject",
                                      "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
		else if(optimizationParameterType.equals(QMOOConfiguration.INTERFACE_VARIABLE))
			f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                              "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$InterfaceVariableOptimizationTableObject",
		                              "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
		else if(optimizationParameterType.equals(QMOOConfiguration.INTERFACE_OBJECTIVE))
			f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                              "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$InterfaceObjectiveOptimizationTableObject",
		                              "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
        else if(optimizationParameterType.equals(OptimizationInterfaceBuild.QMOO_INTERFACE_BUILD_VIEW_PARAMETER))
            f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
                                      "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$OptimizationParameterTableObject",
                                      "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
		return f;
	}


	public static DomeTableObjectFactory getTableObjectFactory()
	{
		return tableObjectFactory;
	}

	public static class DefaultTableObject extends DomeTableObject
	{
		public DefaultTableObject(DomeObject obj, boolean isEditable)
		{
			super(obj, new boolean[]{isEditable, false, false}); // is name editable?
		}

		public DefaultTableObject(DomeObject obj)
		{
			this(obj, true); // is editable
		}

		public Object getDomeObject()
		{
			return object;
		}

		public void addTableTreeObjectListener(DomeObject obj)
		{
			BuildTreeObjectFactory.getTreeObject(obj).
			        addTreeObjectListener(new TableTreeObjectListener());
		}
	}

	public static class UneditableTableObject extends DefaultTableObject
	{
		public UneditableTableObject(DomeObject obj)
		{
			super(obj, false); // not editable
		}
	}

	public static class SubscriptionTableObject extends AbstractTableObject
	{
		public SubscriptionTableObject(AbstractSubscription obj)
		{
			super(obj, new boolean[]{false, false, false}); //nothing is editable
		}

		public Object getValueAt(int column)
		{
			if (column == 0)  //name
				return ((AbstractSubscription) object).getName();
			else if (column == 1) { // value i.e. name of resource model
				return ((AbstractSubscription) object).getResourceName();
			}
			return "";
		}
	}

	public static class ProjectResourceInfoTableObject extends AbstractTableObject
	{
		public static final String NOTHING = "";
		public ProjectResourceInfoTableObject(ProjectResourceInfo obj)
		{
			super(obj, new boolean[] {true, false, false}); //only name is editable
		}

		public Object getValueAt(int column)
		{
			if (column == 0)  //name
				return ((BuildProjectResourceInfo) object).getName();
			else if (column == 1) { // value
				return NOTHING;
			}
			else if (column == 2) { // location
				return ((BuildProjectResourceInfo) object).getResourceHostName() +
				        ":" + ((BuildProjectResourceInfo) object).getResourcePort();
			}
			return NOTHING;
		}

		public void setValueAt(Object value, int column)
		{
			if(column == 0) {
				((BuildProjectResourceInfo) object).setName((String)value);
			}
		}
	}

	public static class ProjectIntegrationModelInfoTableObject extends AbstractTableObject
	{
		private DomeModelBuilder model;
		private PropertyChangeListener resourceChangeListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if(event.getPropertyName().equals(DomeModel.RESOURCE_SUBSCRIBED) ||
				   event.getPropertyName().equals(DomeModel.RESOURCE_UNSUBSCRIBED))
				fireTableObjectChanged(2);
			}
		};
		private PropertyChangeListener resourceNameChangeListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (event.getPropertyName().equals(ProjectResourceInfo.NAME)) {
					fireTableObjectChanged(2);
				}
			}
		};

		public ProjectIntegrationModelInfoTableObject(ProjectIntegrationModelInfo obj)
		{
			super(obj, new boolean[]{true, false, false}); //only name is editable
			model = (DomeModelBuilder) ((BuildProjectIntegrationModelInfo) object).getModel();
			model.addPropertyChangeListener(resourceChangeListener);
			model.addResourceNameChangeListener(resourceNameChangeListener);
		}

		public Object getValueAt(int column)
		{
			if (column == 0)  //name
				return ((BuildProjectIntegrationModelInfo) object).getName();
			else if (column == 1) { // value
				return ProjectResourceInfoTableObject.NOTHING;
			} else if (column == 2) { // resources
				Collection set = model.getResourcesSubscribed();
				return getNames(set);
			}
			return ProjectResourceInfoTableObject.NOTHING;
		}

		public void setValueAt(Object value, int column)
		{
			if (column == 0) {
				((ProjectIntegrationModelInfo) object).setName((String) value);
			}
		}

		public static String getNames(Collection items)
		{
			if (items == null || items.size() == 0) return "";
			Object[] objs = items.toArray();
			if (objs.length == 1) {
				return ((InfoInterface) objs[0]).getName();
			}
			if (objs.length == 2)
				return ((InfoInterface) objs[0]).getName() +
				        " and " + ((InfoInterface) objs[1]).getName();
			// 3 or more objects
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < objs.length - 1; ++i) {
				sb.append(((InfoInterface) objs[i]).getName() + ", ");
			}
			sb.append("and " + ((InfoInterface) objs[objs.length - 1]).getName());
			return sb.toString();
		}
	}

	//add here for equal relation
	public static class EqualRelationTableObject extends DefaultTableObject
	{
		public EqualRelationTableObject(EqualRelation rel)
		{
			super(rel.getDomeObject());
		}

		public Object getValueAt(int column)
		{
			if (column == 0)
				return ((DomeObject) object).getName();
			else if (column == 1) { // value
				return ((EqualRelation) object).getBody();
			}

			return NOTHING;
		}
	}

	public static class IterationRelationTableObject extends DefaultTableObject
	{
		public IterationRelationTableObject(IterationRelation rel)
		{
			super(rel.getDomeObject());

		}

		public Object getValueAt(int column)
		{
			if (column == 0)
				return ((DomeObject) object).getName();
			else if (column == 1) { // value
                if(((IterationRelation) object).getBody().trim().equals("")&&((IterationRelation) object).getCondition().trim().equals(""))
                    return  ((IterationRelation) object).getCondition()+((IterationRelation) object).getBody();

                if(((IterationRelation) object).getIterationType().equals(IterationRelation.WHILE_LOOP)){
                    return "while "+((IterationRelation) object).getCondition()+" do "+((IterationRelation) object).getBody();
                   }
                else if(((IterationRelation) object).getIterationType().equals(IterationRelation.DO_WHILE_LOOP)){
                    return "do "+((IterationRelation) object).getBody()+" while "+((IterationRelation) object).getCondition();
                }
                else if(((IterationRelation) object).getIterationType().equals(IterationRelation.Timestep_LOOP)){
                   return "timestep "+((IterationRelation) object).getCondition()+" repeat "+((IterationRelation) object).getBody();
                }
			}

			return NOTHING;
		}
	}

    public static class ProceduralRelationTableObject extends DefaultTableObject
	{
		public ProceduralRelationTableObject(ProceduralRelation rel)
		{
			super(rel.getDomeObject());

		}

		public Object getValueAt(int column)
		{
			if (column == 0)
				return ((DomeObject) object).getName();
			else if (column == 1) { // value
				return ((ProceduralRelation) object).getBody();
			}

			return NOTHING;
		}
	}

	public static class ParameterTableObject extends DomeTableObject
	{
		protected DefaultRenderer defaultRenderer = new DefaultRenderer();
		protected DomeRealValueRenderer realRenderer = new DomeRealValueRenderer();
		protected DomeIntegerValueRenderer integerRenderer = new DomeIntegerValueRenderer();
		protected DomeStringValueRenderer stringRenderer = new DomeStringValueRenderer();
		protected DomeVectorValueRenderer vectorRenderer = new DomeVectorValueRenderer();
		protected DomeMatrixRenderer matrixRenderer = new DomeMatrixRenderer();
		protected DomePreferenceRenderer preferenceRenderer = new DomePreferenceRenderer();
		protected DomeEnumerationRenderer enumerationRenderer = new DomeEnumerationRenderer();
		protected DomeFileValueRenderer fileRenderer = new DomeFileValueRenderer();
		protected DomeListValueRenderer listRenderer = new DomeListValueRenderer();
        protected Renderers.BooleanCheckBoxLeftRenderer checkBoxBooleanRenderer = new Renderers.BooleanCheckBoxLeftRenderer();

		protected BooleanComboBoxEditor booleanEditor = new BooleanComboBoxEditor();

        protected Editors.BooleanCheckBoxLeftEditor checkBoxBooleanEditor = new Editors.BooleanCheckBoxLeftEditor();

		protected IntegerBuildEditor integerEditor = new IntegerBuildEditor();
		protected RealBuildEditor realEditor = new RealBuildEditor();
		protected StringBuildEditor stringEditor = new StringBuildEditor();
		protected vectorBuildEditor vectorEditor = new vectorBuildEditor();
		protected matrixBuildEditor matrixEditor = new matrixBuildEditor();
		protected PreferenceBuildEditor preferenceEditor = new PreferenceBuildEditor();
		protected EnumerationBuildEditor enumerationEditor = new EnumerationBuildEditor();
		protected fileBuildEditor fileEditor = new fileBuildEditor();
		protected listBuildEditor listEditor = new listBuildEditor();

		protected DefaultCellEditor defaultCellEditor = new DefaultCellEditor(Templates.makeTextField(""));

		protected MappingCellEditor mappingEditor = new MappingCellEditor(object);

		protected static final String NOTHING = "";


        /**
         * sangmok: fix memory problem
         * diagnosis: ParameterTableObject is not release properly. However, it is too hard to fix because there are so many instances which forget releasing this instance.
         * temporary solution: even though ParameterTableObject is not released, editor instances in ParameterTableObject should be released because they contain references to Data Objects, which take up most of memory space.
         * solution implemented by : invoke close() method, which removes the reference to data object of each editor, of every Editor instance. or just set each editor variable as null
         */
        public void releaseDataObjectReferenceOfEditors() {
//            integerEditor = null;
//            realEditor = null;
//            stringEditor = null;
//            vectorEditor = null;
//            matrixEditor = null;
//            preferenceEditor = null;
//            enumerationEditor = null;
//            fileEditor = null;
//            listEditor = null;

            vectorEditor.clearDataObject();
            matrixEditor.clearDataObject();
            preferenceEditor.clearDataObject();
            enumerationEditor.clearDataObject();
            fileEditor.clearDataObject();
            listEditor.clearDataObject();
        }


        protected PropertyChangeListener dataValueListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String property = e.getPropertyName();
				if (property.equals("value") || property.equals("unit")) {
					fireTableObjectChanged(1);
				} else if (property.equals("size")) {//for vector and matrix
					fireTableObjectChanged(1);
				} else if (property.equals("filepath")) {//for file
					fireTableObjectChanged(1);
				} else if (property.equals(DomeEnumeration.LASTSELECTION)) {//for enumeration
                    fireTableObjectChanged(1);
                }
			}
		};
		protected MappingChangeListener mappingChangeListener = new MappingChangeListener()
		{
			public void mappingChanged(MappingChangeEvent event)
			{
				fireTableObjectChanged(2);
			}
		};
		protected MappingNameChangeListener mappingNameChangeListener = new MappingNameChangeListener()
		{
			public void mappingNameChanged(MappingNameChangeEvent event)
			{
				fireTableObjectChanged(2);
			}
		};

		public ParameterTableObject(Parameter obj)
		{
			super(obj, new boolean[]{true, true, true}); // name/value is editable
			DataObject currentData = ((Parameter) object).getCurrentDataObject();
			currentData.addPropertyChangeListener(dataValueListener);
			((Parameter) object).addPropertyChangeListener(new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent e)
				{
					DataObject currentData = ((Parameter) object).getCurrentDataObject();
                    String property = e.getPropertyName();
					if (property.equals(Parameter.DATATYPESELECTION)) {
						DomeModel m = (DomeModel) ((Parameter) object).getModel();
						DataObject oldObject = currentData;
						m.getMappingManager().updateMappingListeners((Parameter) object, oldObject);
					}

					if (property.equals(Parameter.DATATYPESELECTION) ||
					        property.equals(Parameter.CURRENT_TYPE)) {
						currentData.removePropertyChangeListener(dataValueListener);
						currentData = ((Parameter) object).getCurrentDataObject();
						currentData.addPropertyChangeListener(dataValueListener);
						fireTableObjectChanged(1);
					}
				}
			});
			Model m = obj.getModel();
			if (m != null) {
				if (m instanceof Relation)
					m = m.getModel();
				if (m instanceof DomeModelBuilder && !(m instanceof PluginModelBuilder)) {
					((DomeModel) m).getMappingManager().addMappingChangeListener(obj, mappingChangeListener);
					((DomeModel) m).getMappingManager().addMappingNameChangeListener(obj, mappingNameChangeListener);
				}
				else if (m instanceof IntegrationProject) {
					((IntegrationProject) m).getMappingManager().addMappingChangeListener(obj, mappingChangeListener);
					((IntegrationProject) m).getMappingManager().addMappingNameChangeListener(obj, mappingNameChangeListener);
				}
			}
		}

		public void addTableTreeObjectListener(DomeObject obj)
		{
            BuildTreeObjectFactory.getTreeObject(obj).
                    addTreeObjectListener(new TableTreeObjectListener());
		}

		public Object getValueAt(int column)
		{
			if (column == 0)
				return ((DomeObject) object).getName();
			else if (column == 1)
			{ // value
				DataObject data = ((Parameter) object).getCurrentDataObject();
				if (data instanceof DomeBoolean)
					return ((DomeBoolean) data).getBooleanValue();
				else if (data instanceof DomeReal)
					return data;
				//return ((DomeReal)data).getRealValue();
				else if (data instanceof DomeInteger)
					return data;
				//return ((DomeInteger)data).getIntegerValue();
				else if (data instanceof DomeString)
					return data;
				else if (data instanceof DomeVector)
					return data;
				else if (data instanceof DomeMatrix)
					return data;
				else if (data instanceof DomePreference)
					return data;
				else if (data instanceof DomeEnumeration)
					return data;
				else if (data instanceof DomeFile)
					return data;
				else if (data instanceof DomeList)
					return data;
			}
			else if (column == 2)
			{ // mapping
				Parameter p = (Parameter) object;
				Model m = p.getModel();
				if (m instanceof PluginModel)
				{
					if (p.getScope() instanceof PluginModel)
					{
						PluginModelBuilder pmodel = (PluginModelBuilder) m;
						PluginMappingManager pmgr = pmodel.getPluginMappingManager();
						Object retObject = pmgr.getMappingObjectForParameter(p);
						return retObject;
					}
					else
					{
						Collection c = ((DomeModel) m).getMappingManager().getMappingsForParameter(p);
						if (c.isEmpty()) return NOTHING;
						return getNames(c);
					}
				}
				else
				{
					if (m instanceof Relation)
						m = m.getModel();
					if (m != null)
					{
						Collection c = null;
						if (m instanceof DomeModel)
						{
							c = ((DomeModel) m).getMappingManager().getMappingsForParameter(p);
						}
						else
						{
							c = ((IntegrationProject) m).getMappingManager().getMappingsForParameter(p);
						}
						if (c.isEmpty()) return NOTHING;
						return getNames(c);
					}
					else
					{
						return NOTHING;
					}
				}
			}
			return NOTHING;
		}



		public TableCellRenderer getRendererAt(int column)
		{
			if (column == 1) {
				DataObject data = ((Parameter) object).getCurrentDataObject();
				if (data instanceof DomeBoolean)
                {
                    ModelObjectScope scope = ((Parameter) object).getModel();
                    if (scope.getModelObjectById(((Parameter) object).getId()) == null)
                        return checkBoxBooleanRenderer;
                    else
                        return defaultRenderer;
                }
				else if (data instanceof DomeReal)
					return realRenderer;
				else if (data instanceof DomeInteger)
					return integerRenderer;
				else if (data instanceof DomeString)
					return stringRenderer;
				else if (data instanceof DomeVector)
					return vectorRenderer;
				else if (data instanceof DomeMatrix)
					return matrixRenderer;
				else if (data instanceof DomePreference)
					return preferenceRenderer;
				else if (data instanceof DomeEnumeration)
					return enumerationRenderer;
				else if (data instanceof DomeFile)
					return fileRenderer;
				else if (data instanceof DomeList)
					return listRenderer;

			}
			return null;
		}

		public TableCellEditor getEditorAt(int column)
		{
			if (column == 1)
			{
				DataObject data = ((Parameter) object).getCurrentDataObject();
				if (data instanceof DomeBoolean)
                {
                    ModelObjectScope scope = ((Parameter)object).getModel();
                    if (scope.getModelObjectById(((Parameter)object).getId()) == null)
                        return checkBoxBooleanEditor;
                    else
                        return booleanEditor;
                }
				else if (data instanceof DomeReal)
					return realEditor;
				else if (data instanceof DomeInteger)
					return integerEditor;
				else if (data instanceof DomeString)
					return stringEditor;
				else if (data instanceof DomeVector)
					return vectorEditor;
				else if (data instanceof DomeMatrix)
					return matrixEditor;
				else if (data instanceof DomePreference)
					return preferenceEditor;
				else if (data instanceof DomeEnumeration)
					return enumerationEditor;
				else if (data instanceof DomeFile)
					return fileEditor;
				else if (data instanceof DomeList)
					return listEditor;
			}
			else if (column == 2)
			{
				Object model = BuildMode.getCurrentModelFrame().getGui().getGuiObject();
				if (model instanceof PluginModel)
				{
					defaultCellEditor.setClickCountToStart(1);
					return defaultCellEditor;
				}
				else
				{
					return mappingEditor;
				}
			}
			return null;
		}

		public void setValueAt(Object value, int column)
		{
			if (value == null) return;
			if (column == 0) // name
				((DomeObject) object).setName(value.toString());
			else if (column == 1) {
				DataObject data = ((Parameter) object).getCurrentDataObject();
				if (data instanceof DomeBoolean)
					((DomeBoolean) data).setBooleanValue((Boolean) value);
				else if (data instanceof DomeReal)
					((DomeReal) data).setRealValue((Double) value);
				else if (data instanceof DomeInteger)
					((DomeInteger) data).setIntegerValue((Integer) value);
				else if (data instanceof DomeString)
					((DomeString) data).setValue((String) value);
			} else if (column == 2) {
				Object model = BuildMode.getCurrentModelFrame().getGui().getGuiObject();
				if (model instanceof PluginModel) {
					PluginMappingManager pmgr = ((PluginModelBuilder) model).getPluginMappingManager();
					pmgr.addMapping((Parameter) object, value);
				}
			}
		}

		public static String getNames(Collection items)
		{
			if (items == null || items.size() == 0) return "";
			Object[] objs = items.toArray();
			if (objs.length == 1) return ((DomeObject) objs[0]).getName();
			if (objs.length == 2)
				return ((DomeObject) objs[0]).getName() +
				        " and " + ((DomeObject) objs[1]).getName();
			// 3 or more objects
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < objs.length - 1; ++i) {
				sb.append(((DomeObject) objs[i]).getName() + ", ");
			}
			sb.append("and " + ((DomeObject) objs[objs.length - 1]).getName());
			return sb.toString();
		}
	}

    public static class VariableOptimizationTableObject extends DomeTableObject
    {
        protected DefaultRenderer _defaultRenderer = new DefaultRenderer();
        protected DomeRealValueRenderer _realRenderer = new DomeRealValueRenderer();
        protected DomeStringValueRenderer _stringRenderer = new DomeStringValueRenderer();
        protected RealBuildEditor _realEditor = new RealBuildEditor();
        protected StringBuildEditor _stringEditor = new StringBuildEditor();

        protected DefaultCellEditor _defaultCellEditor = new DefaultCellEditor(Templates.makeTextField(""));

        protected MappingCellEditor _mappingEditor = new MappingCellEditor(object);
        protected DataObject _currentData;

        protected static final String NOTHING = "";

        protected VariableParameter _variableParameter;

        protected static final int VALUE_COLUMN = 1;
        protected static final int LOWER_LIMIT_COLUMN = 2;
        protected static final int UPPER_LIMIT_COLUMN = 3;

        protected boolean _hasLostFocus = false; // this is a fix for text messages showing up twice

        protected PropertyChangeListener dataValueListener = new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent e)
            {
                String property = e.getPropertyName();
                if (property.equals("value") || property.equals("unit"))
                {
                    fireTableObjectChanged(VALUE_COLUMN);
                }
            }
        };

        protected class ParameterPropertyListener implements PropertyChangeListener
        {
            private int _column;

            public ParameterPropertyListener(int column)
            {
                this._column = column;
            }

            public void propertyChange(PropertyChangeEvent e)
            {
                String property = e.getPropertyName();
                if(property.equals("value") || property.equals("unit"))
                    fireTableObjectChanged(this._column);
            }
        }

        public VariableOptimizationTableObject(Parameter obj, boolean[] isEditable)
        {
            super(obj, isEditable);  // make all columns editable
	        _currentData = ((Parameter) object).getCurrentDataObject();
            _currentData.addPropertyChangeListener(dataValueListener);
            ((Parameter)object).addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent e)
                {
                    String property = e.getPropertyName();
                    if(property.equals(Parameter.DATATYPESELECTION))
                    {
                        DomeModel m = (DomeModel) ((Parameter)object).getModel();
                        DataObject oldObject = _currentData;
                        m.getMappingManager().updateMappingListeners((Parameter)object, oldObject);
                    }

                    if(property.equals(Parameter.DATATYPESELECTION) || property.equals(Parameter.CURRENT_TYPE))
                    {
                        _currentData.removePropertyChangeListener(dataValueListener);
						_currentData = ((Parameter) object).getCurrentDataObject();
						_currentData.addPropertyChangeListener(dataValueListener);
						fireTableObjectChanged(VALUE_COLUMN);
                    }
                }
            });
        }

        public void addTableTreeObjectListener(DomeObject obj)
        {
            BuildTreeObjectFactory.getTreeObject(obj).
            addTreeObjectListener(new TableTreeObjectListener());
        }

        public Object getValueAt(int column)
        {
            if (column == 0)
                return ((DomeObject) object).getName();
            else if (column == 1)
            { // value
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return data;
            }
            else if (column == 2)
            {
                return this._variableParameter.getLowerLimit();
            }
            else if (column == 3)
            {
                return this._variableParameter.getUpperLimit();
            }
            return NOTHING;
        }

        protected String getNames(Collection items)
        {
            if (items == null || items.size() == 0) return "";
            Object[] objs = items.toArray();
            if (objs.length == 1) return ((DomeObject) objs[0]).getName();
            if (objs.length == 2)
                return ((DomeObject) objs[0]).getName() +
                        " and " + ((DomeObject) objs[1]).getName();
            // 3 or more objects
            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < objs.length - 1; ++i) {
                sb.append(((DomeObject) objs[i]).getName() + ", ");
            }
            sb.append("and " + ((DomeObject) objs[objs.length - 1]).getName());
            return sb.toString();
        }

        public TableCellRenderer getRendererAt(int column)
        {
            if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return this._realRenderer;
            }
            else if (column == 2)
                return this._realRenderer;
            else if (column == 3)
                return this._realRenderer;
            return null;
        }

        public TableCellEditor getEditorAt(int column)
        {
            if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return this._realEditor;
            }
            else if (column == 2)
                return this._realEditor;
            else if (column == 3)
                return this._realEditor;
            return null;
        }

		public void setValueAt(Object value, int column)
        {
            if (value == null) return;
            if (column == 0) // name
                ((DomeObject) object).setName(value.toString());
            else if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    ((DomeReal) data).setRealValue((Double) value);
            }
            else if (column == 2)
            {
                if (_variableParameter.isValidLowerLimit((Double)value))
                    _variableParameter.setLowerLimit((Double)value);
                else
                {
                    if (!_hasLostFocus)
                    {
                        // UGLY hack to prevent the dialog box from showing up twice when the gui is out of focus
                        _hasLostFocus = true;
                        OneButton1Msg.showWarning(null, "Build Mode Warning", "Lower limit of parameter '" + _variableParameter.getParameter().getName()
                                + "' must be less than the parameter value.", "OK", OneButton1Msg.DEFAULT_SIZE);
                        _hasLostFocus = false;
                    }
                }
            }
            else if (column == 3)
            {
                if (_variableParameter.isValidUpperLimit((Double)value))
                    _variableParameter.setUpperLimit((Double)value);
                else
                {
                    if (!_hasLostFocus)
                    {
                        // UGLY hack to prevent the dialog box from showing up twice when the gui is out of focus
                        _hasLostFocus = true;
                        OneButton1Msg.showWarning(null, "Build Mode Warning", "Upper limit of parameter '" + _variableParameter.getParameter().getName()
                            + "' must be greater than the parameter value.", "OK", OneButton1Msg.DEFAULT_SIZE);
                        _hasLostFocus = false;
                    }
                }
            }
        }
    }

    public static class ModelVariableOptimizationTableObject extends VariableOptimizationTableObject
    {
        public static final int MAPPING_COLUMN = 4;

        protected MappingChangeListener mappingChangeListener = new MappingChangeListener()
		{
			public void mappingChanged(MappingChangeEvent event)
			{
				fireTableObjectChanged(MAPPING_COLUMN);
			}
		};

	    protected MappingNameChangeListener mappingNameChangeListener = new MappingNameChangeListener()
		{
			public void mappingNameChanged(MappingNameChangeEvent event)
			{
				fireTableObjectChanged(MAPPING_COLUMN);
			}
		};

        public ModelVariableOptimizationTableObject(Parameter obj)
        {
            super(obj, new boolean[]{true, true, true, true, true});
            Model m = obj.getModel();
            if (m != null)
            {
                _variableParameter = (VariableParameter) ((OptimizationToolBuild) m).getOptimizationToolVariableParameterMap().get(obj);
                _variableParameter.getUpperLimit().addPropertyChangeListener(new ParameterPropertyListener(UPPER_LIMIT_COLUMN));
                _variableParameter.getLowerLimit().addPropertyChangeListener(new ParameterPropertyListener(LOWER_LIMIT_COLUMN));
                ((OptimizationToolBuild) m).getMappingManager().addMappingChangeListener(obj, mappingChangeListener);
                ((OptimizationToolBuild) m).getMappingManager().addMappingNameChangeListener(obj, mappingNameChangeListener);
            }
        }

        public Object getValueAt(int column)
        {
            if(column == 4)
            {
                Collection c = ((AnalysisTool)((Parameter)this.object).getModel()).getMappingManager().getMappingsForParameter((Parameter)this.object);
	            if (c.isEmpty()) return NOTHING;
	            return getNames(c);
            }
            else
               return super.getValueAt(column);
        }
    }

    public static class InterfaceVariableOptimizationTableObject extends VariableOptimizationTableObject
    {
        protected Renderers.BooleanCheckBoxRenderer _booleanRenderer = new Renderers.BooleanCheckBoxRenderer();
        protected Editors.BooleanCheckBoxEditor _booleanEditor = new Editors.BooleanCheckBoxEditor();

        private static final int ACTIVE_COLUMN = 4;
        private static final int MAPPING_COLUMN = 5;

        protected MappingChangeListener mappingChangeListener = new MappingChangeListener()
		{
			public void mappingChanged(MappingChangeEvent event)
			{
				fireTableObjectChanged(MAPPING_COLUMN);
			}
		};

	    protected MappingNameChangeListener mappingNameChangeListener = new MappingNameChangeListener()
		{
			public void mappingNameChanged(MappingNameChangeEvent event)
			{
				fireTableObjectChanged(MAPPING_COLUMN);
			}
		};

        public InterfaceVariableOptimizationTableObject(Parameter obj)
        {
            super(obj, new boolean[]{true, true, true, true, true, true});  // make all columns editable
	        Model m = obj.getModel();
            if (m != null)
            {
	            OptimizationInterfaceBuild i = (OptimizationInterfaceBuild) obj.getScope();
                _variableParameter = (VariableParameter) i.getInterfaceVariableMap().get(obj);
                this._variableParameter.getUpperLimit().addPropertyChangeListener(new ParameterPropertyListener(UPPER_LIMIT_COLUMN));
                this._variableParameter.getLowerLimit().addPropertyChangeListener(new ParameterPropertyListener(LOWER_LIMIT_COLUMN));
                this._variableParameter.getIsActive().addPropertyChangeListener(new ParameterPropertyListener(ACTIVE_COLUMN));
	            ((OptimizationToolBuild)m).getMappingManager().addMappingChangeListener(obj, mappingChangeListener);
	            ((OptimizationToolBuild) m).getMappingManager().addMappingNameChangeListener(obj, mappingNameChangeListener);
            }
        }

        public Object getValueAt(int column)
        {
            if(column == 4)
            {
                return this._variableParameter.getIsActiveForEditor();
            }
            else if (column == 5)
            {
	            Collection c = ((AnalysisTool)((Parameter)this.object).getModel()).getMappingManager().getMappingsForParameter((Parameter)this.object);
	            if (c.isEmpty()) return NOTHING;
	            return getNames(c);
            }
            else
                return super.getValueAt(column);
        }

        public TableCellRenderer getRendererAt(int column)
        {
            if (column == 4)
                return this._booleanRenderer;
            else
                return super.getRendererAt(column);
        }

		public TableCellEditor getEditorAt(int column)
        {
            if (column == 4)
                return this._booleanEditor;
            else
                return super.getEditorAt(column);
        }

		public void setValueAt(Object value, int column)
        {
            if (value == null) return;
            if (column == 4)
            {
                this._variableParameter.setIsActive(new BooleanData((Boolean)value));
            }
            else
                super.setValueAt(value,column);
        }
    }

    public static class ObjectiveOptimizationTableObject extends DomeTableObject
    {
        protected DefaultRenderer _defaultRenderer = new DefaultRenderer();
        protected DomeRealValueRenderer _realRenderer = new DomeRealValueRenderer();
        protected DomeStringValueRenderer _stringRenderer = new DomeStringValueRenderer();
        protected DomeEnumerationRenderer _enumerationRenderer = new DomeEnumerationRenderer();

        protected RealBuildEditor _realEditor = new RealBuildEditor();
        protected StringBuildEditor _stringEditor = new StringBuildEditor();
        protected EnumerationBuildEditor _enumerationEditor = new EnumerationBuildEditor();

        protected DefaultCellEditor _defaultCellEditor = new DefaultCellEditor(Templates.makeTextField(""));

        protected MappingCellEditor _mappingEditor = new MappingCellEditor(object);
        protected DataObject _currentData;

        protected ObjectiveParameter _objectiveParameter;

        protected static final String NOTHING = "";
        protected static final int VALUE_COLUMN = 1;

        protected class ParameterPropertyListener implements PropertyChangeListener
        {
            private int _column;

            public ParameterPropertyListener(int column)
            {
                this._column = column;
            }

            public void propertyChange(PropertyChangeEvent e)
            {
                String property = e.getPropertyName();
                if (property.equals("value") || property.equals("unit"))
                    fireTableObjectChanged(this._column);
            }
        }

        public ObjectiveOptimizationTableObject(Parameter obj, boolean[] isEditable)
        {
            super(obj, isEditable);
            this._currentData = ((Parameter) object).getCurrentDataObject();
            this._currentData.addPropertyChangeListener(new ParameterPropertyListener(VALUE_COLUMN));
        }

        public void addTableTreeObjectListener(DomeObject obj)
        {
            BuildTreeObjectFactory.getTreeObject(obj).
            addTreeObjectListener(new TableTreeObjectListener());
        }

        public Object getValueAt(int column)
        {
            if (column == 0)
                return ((DomeObject) object).getName();
            else if (column == 1)
            { // value
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return data;
            }
            else if (column == 2)
            {
                return this._objectiveParameter.getIsMaxOrMin();
            }
            return NOTHING;
        }

        protected String getNames(Collection items)
        {
            if (items == null || items.size() == 0) return "";
            Object[] objs = items.toArray();
            if (objs.length == 1) return ((DomeObject) objs[0]).getName();
            if (objs.length == 2)
                return ((DomeObject) objs[0]).getName() +
                        " and " + ((DomeObject) objs[1]).getName();
            // 3 or more objects
            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < objs.length - 1; ++i)
            {
                sb.append(((DomeObject) objs[i]).getName() + ", ");
            }
            sb.append("and " + ((DomeObject) objs[objs.length - 1]).getName());
            return sb.toString();
        }

        public TableCellRenderer getRendererAt(int column)
        {
            if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return this._realRenderer;
            }
            else if (column == 2)
                return this._enumerationRenderer;
            return null;
        }

        public TableCellEditor getEditorAt(int column)
        {
            if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return this._realEditor;
            }
            else if (column == 2)
                return this._enumerationEditor;
            return null;
        }

        public void setValueAt(Object value, int column)
        {
            if (value == null) return;
            if (column == 0) // name
                ((DomeObject) object).setName(value.toString());
            else if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    ((DomeReal) data).setRealValue((Double) value);
            }
            else if (column == 2)
            {
                this._objectiveParameter.setIsMaxOrMin(((DomeEnumeration)value).getLastSelection());
            }
        }
    }

    public static class ModelObjectiveOptimizationTableObject extends ObjectiveOptimizationTableObject
    {
        private static final int MAPPING_COLUMN = 3;

        protected MappingChangeListener mappingChangeListener = new MappingChangeListener()
	    {
		    public void mappingChanged(MappingChangeEvent event)
		    {
			    fireTableObjectChanged(MAPPING_COLUMN);
		    }
	    };

	    protected MappingNameChangeListener mappingNameChangeListener = new MappingNameChangeListener()
	    {
		    public void mappingNameChanged(MappingNameChangeEvent event)
		    {
			    fireTableObjectChanged(MAPPING_COLUMN);
		    }
	    };

        public ModelObjectiveOptimizationTableObject(Parameter obj)
        {
            super(obj, new boolean[]{true, true, true, true});
            Model m = obj.getModel();
            if (m != null)
            {
                _objectiveParameter = (ObjectiveParameter) ((OptimizationToolBuild) m).getOptimizationToolObjectiveParameterMap().get(obj);
                ((OptimizationToolBuild) m).getMappingManager().addMappingChangeListener(obj, mappingChangeListener);
                ((OptimizationToolBuild) m).getMappingManager().addMappingNameChangeListener(obj, mappingNameChangeListener);
            }
        }

        public Object getValueAt(int column)
        {
            if (column == MAPPING_COLUMN)
            {
                Collection c = ((AnalysisTool)((Parameter)this.object).getModel()).getMappingManager().getMappingsForParameter((Parameter)this.object);
	            if(c.isEmpty())
		            return NOTHING;
	            return getNames(c);
            }
            else
                return super.getValueAt(column);
        }
    }

    public static class InterfaceObjectiveOptimizationTableObject extends ObjectiveOptimizationTableObject
    {
        protected Renderers.BooleanCheckBoxRenderer _booleanRenderer = new Renderers.BooleanCheckBoxRenderer();
        protected Editors.BooleanCheckBoxEditor _booleanEditor = new Editors.BooleanCheckBoxEditor();

        private static final int ACTIVE_COLUMN = 3;
        private static final int MAPPING_COLUMN = 4;

	    protected MappingChangeListener mappingChangeListener = new MappingChangeListener()
	    {
		    public void mappingChanged(MappingChangeEvent event)
		    {
			    fireTableObjectChanged(MAPPING_COLUMN);
		    }
	    };

	    protected MappingNameChangeListener mappingNameChangeListener = new MappingNameChangeListener()
	    {
		    public void mappingNameChanged(MappingNameChangeEvent event)
		    {
			    fireTableObjectChanged(MAPPING_COLUMN);
		    }
	    };

        public InterfaceObjectiveOptimizationTableObject(Parameter obj)
        {
            super(obj, new boolean[]{true, true, true, true, true});  // make all columns editable
            Model m = obj.getModel();
            if (m != null)
            {
                OptimizationInterfaceBuild i = (OptimizationInterfaceBuild) obj.getScope();
                _objectiveParameter = (ObjectiveParameter) i.getInterfaceObjectiveMap().get(obj);
                _objectiveParameter.getIsActive().addPropertyChangeListener(new ParameterPropertyListener(ACTIVE_COLUMN));
	            ((OptimizationToolBuild) m).getMappingManager().addMappingChangeListener(obj, mappingChangeListener);
	            ((OptimizationToolBuild) m).getMappingManager().addMappingNameChangeListener(obj, mappingNameChangeListener);
            }
        }

        public Object getValueAt(int column)
        {
            if (column == 3)
            {
                 return this._objectiveParameter.getIsActiveForEditor();
            }
            else if (column == 4)
            {
                Collection c = ((AnalysisTool)((Parameter)this.object).getModel()).getMappingManager().getMappingsForParameter((Parameter)this.object);
	            if(c.isEmpty())
		            return NOTHING;
	            return getNames(c);
            }
            else
                return super.getValueAt(column);
        }

        public TableCellRenderer getRendererAt(int column)
        {
            if (column == 3)
                return this._booleanRenderer;
            else
                return super.getRendererAt(column);
        }

        public TableCellEditor getEditorAt(int column)
        {
            if (column == 3)
                return this._booleanEditor;
            else if (column == 4)
            {
                this._defaultCellEditor.setClickCountToStart(1);
                return this._defaultCellEditor;
            }
            else
                return super.getEditorAt(column);
        }

        public void setValueAt(Object value, int column)
        {
            if (column == 3)
            {
                this._objectiveParameter.setIsActive(new BooleanData((Boolean) value));
            }
            else if (column == 4)
            {
                Object model = BuildMode.getCurrentModelFrame().getGui().getGuiObject();
                ToolMappingManager tmgr = ((OptimizationToolBuild) model).getToolMappingManager();
                tmgr.addMapping((Parameter) object, value);
            }
            else
                super.setValueAt(value, column);
        }
    }

    public static class OptimizationParameterTableObject extends DomeTableObject
    {
        protected DefaultRenderer _defaultRenderer = new DefaultRenderer();
        protected DomeRealValueRenderer _realRenderer = new DomeRealValueRenderer();
        protected DomeStringValueRenderer _stringRenderer = new DomeStringValueRenderer();
        protected DomeEnumerationRenderer _enumerationRenderer = new DomeEnumerationRenderer();
        protected Renderers.BooleanCheckBoxRenderer _booleanRenderer = new Renderers.BooleanCheckBoxRenderer();

        protected Editors.BooleanCheckBoxEditor _booleanEditor = new Editors.BooleanCheckBoxEditor();
        protected RealBuildEditor _realEditor = new RealBuildEditor();
        protected StringBuildEditor _stringEditor = new StringBuildEditor();
        protected EnumerationBuildEditor _enumerationEditor = new EnumerationBuildEditor();

        protected DefaultCellEditor _defaultCellEditor = new DefaultCellEditor(Templates.makeTextField(""));

        protected MappingCellEditor _mappingEditor = new MappingCellEditor(object);
        protected DataObject _currentData;

        private OptimizationParameter _optimizationParameter;

        protected static final String NOTHING = "";

        private static final int VALUE_COLUMN = 1;
        private static final int ACTIVE_COLUMN = 2;

        protected class QMOOListener implements PropertyChangeListener
        {
            private int _column;

            public QMOOListener(int column)
            {
                this._column = column;
            }

            public void propertyChange(PropertyChangeEvent e)
            {
                String property = e.getPropertyName();
                if (property.equals("value") || property.equals("unit"))
                    fireTableObjectChanged(this._column);
            }
        }

        protected MappingChangeListener mappingChangeListener = new MappingChangeListener()
        {
            public void mappingChanged(MappingChangeEvent event)
            {
                fireTableObjectChanged(3);
            }
        };

        protected MappingNameChangeListener mappingNameChangeListener = new MappingNameChangeListener()
        {
            public void mappingNameChanged(MappingNameChangeEvent event)
            {
                fireTableObjectChanged(3);
            }
        };

        public OptimizationParameterTableObject(Parameter obj)
        {
            super(obj, new boolean[]{true, true, true, true});  // make all columns editable
            this._currentData = ((Parameter) object).getCurrentDataObject();
            this._currentData.addPropertyChangeListener(new QMOOListener(VALUE_COLUMN));
            ModelObjectScope scope = obj.getScope();
            if (scope instanceof OptimizationInterfaceBuild)
            {
                if (((OptimizationInterfaceBuild) scope).getInterfaceOptimizationMap().containsKey(object))
                {
                    _optimizationParameter = (OptimizationParameter) ((OptimizationInterfaceBuild) scope).getInterfaceOptimizationMap().get(object);
                }
                else
                    OneButton1Msg.showWarning(null, "build interface error", "parameter not found", "OK", new Dimension(150, 75));
            }

            Model m = obj.getModel();
            if (m != null)
            {
                _optimizationParameter.getIsActive().addPropertyChangeListener(new QMOOListener(ACTIVE_COLUMN));
                ((OptimizationToolBuild) m).getMappingManager().addMappingChangeListener(obj, mappingChangeListener);
                ((OptimizationToolBuild) m).getMappingManager().addMappingNameChangeListener(obj, mappingNameChangeListener);
            }
        }

        public void addTableTreeObjectListener(DomeObject obj)
        {
            BuildTreeObjectFactory.getTreeObject(obj).
                    addTreeObjectListener(new TableTreeObjectListener());
        }

        public Object getValueAt(int column)
        {
            if (column == 0)
                return ((DomeObject) object).getName();
            else if (column == 1)
            { // value
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return data;
            }
            else if (column == 2)
            {
                return _optimizationParameter.getIsActiveForEditor();
            }
            else if (column == 3)
            {
                Collection c = ((AnalysisTool) ((Parameter) this.object).getModel()).getMappingManager().getMappingsForParameter((Parameter) this.object);
                if (c.isEmpty()) return NOTHING;
                return getNames(c);
            }
            return NOTHING;
        }

        protected String getNames(Collection items)
        {
            if (items == null || items.size() == 0) return "";
            Object[] objs = items.toArray();
            if (objs.length == 1) return ((DomeObject) objs[0]).getName();
            if (objs.length == 2)
                return ((DomeObject) objs[0]).getName() +
                        " and " + ((DomeObject) objs[1]).getName();
            // 3 or more objects
            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < objs.length - 1; ++i)
            {
                sb.append(((DomeObject) objs[i]).getName() + ", ");
            }
            sb.append("and " + ((DomeObject) objs[objs.length - 1]).getName());
            return sb.toString();
        }

        public TableCellRenderer getRendererAt(int column)
        {
            if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeBoolean)
                    return this._defaultRenderer;
                else if (data instanceof DomeReal)
                    return this._realRenderer;
                else if (data instanceof DomeString)
                    return this._stringRenderer;
            }
            else if (column == 2)
                return this._booleanRenderer;
            return null;
        }

        public TableCellEditor getEditorAt(int column)
        {
            if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeBoolean)
                    return this._booleanEditor;
                else if (data instanceof DomeReal)
                    return this._realEditor;
                else if (data instanceof DomeString)
                    return this._stringEditor;
            }
            else if (column == 2)
                return this._booleanEditor;
            else if (column == 3)
            {
                this._defaultCellEditor.setClickCountToStart(1);
                return this._defaultCellEditor;
            }
            return null;
        }

        public void setValueAt(Object value, int column)
        {
            if (value == null) return;
            if (column == 0) // name
                ((DomeObject) object).setName(value.toString());
            else if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    ((DomeReal) data).setRealValue((Double) value);
            }
            else if (column == 2)
            {
                _optimizationParameter.setIsActive(new BooleanData((Boolean) value));
            }
            else if (column == 3)
            {
                Object model = BuildMode.getCurrentModelFrame().getGui().getGuiObject();
                ToolMappingManager tmgr = ((OptimizationToolBuild) model).getToolMappingManager();
                tmgr.addMapping((Parameter) object, value);
            }
        }
    }
}
