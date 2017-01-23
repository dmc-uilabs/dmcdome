// RunTreeTable.java
package mit.cadlab.dome3.gui.guiutils.treetable;

import com.sun.java.ObjectTreeTable;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.table.DomeTableObject;
import mit.cadlab.dome3.gui.guiutils.table.DomeTableObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.run.RunTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.treetable.Editors.*;
import mit.cadlab.dome3.gui.guiutils.treetable.Renderers.*;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.OptimizationParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.OptimizationToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceConfiguration;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.treetable.TableObjectFactoryObjectTreeTableModel;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

public class RunTreeTable extends ObjectTreeTable
{

	protected static final int numberColumns = 2; // name, value
	protected static final String[] columnNames = {"name", "value"};
	protected static final int[] columnWidths = {150, 150};
	protected static DomeTableObjectFactory tableObjectFactory = makeTableObjectFactory();

	public RunTreeTable(DomeTree tree)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   numberColumns,
		                                   columnNames,
		                                   tableObjectFactory));
		DomeTable.customizeTable(this);
		setInitialColumnWidths(columnWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

	public RunTreeTable(DomeTree tree, int[] columnWidths)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   numberColumns,
		                                   columnNames,
		                                   tableObjectFactory));
		DomeTable.customizeTable(this);
		setInitialColumnWidths(columnWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

	public RunTreeTable(DomeTree tree, int noCols, String[] colNames, int[] colWidths)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   noCols,
		                                   colNames,
		                                   tableObjectFactory));
		DomeTable.customizeTable(this);
		setInitialColumnWidths(colWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

    // for optimization models
	public RunTreeTable(DomeTree tree, String qmooParameterType, int noCols, String[] colNames, int[] colWidths)
	{
		super(new DomeObjectTreeTableModel(tree,
		                                   noCols,
		                                   colNames,
		                                   RunTreeTable.makeAnalysisToolTableObjectFactory(qmooParameterType)));
		DomeTable.customizeTable(this);
		setInitialColumnWidths(colWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}


	public RunTreeTable(DomeTree tree, int noCols, String[] colNames, int[] colWidths,
	                    TableObjectFactory alternateTableObjectFactory)
	{
		super(new TableObjectFactoryObjectTreeTableModel(tree,
		                                                 noCols,
		                                                 colNames,
		                                                 alternateTableObjectFactory));
		DomeTable.customizeTable(this);
		setInitialColumnWidths(colWidths);
		setDefaultRenderer(Object.class, new NothingRenderer());
	}

	protected static DomeTableObjectFactory makeTableObjectFactory()
	{
		DomeTableObjectFactory f = new DomeTableObjectFactory("RunTreeTable.TableObjectFactory",
		                                                      "mit.cadlab.dome3.swing.table.FillerTableObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$DefaultTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$ParameterTableObject",
		                          "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$UneditableTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$UneditableTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$EqualRelationTableObject",
		                          "mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$ProceduralRelationTableObject",
		                          "mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation");
		return f;
	}

    protected static DomeTableObjectFactory makeAnalysisToolTableObjectFactory(String optimizationParameterType)
	{
		DomeTableObjectFactory f = new DomeTableObjectFactory("RunTreeTable.TableObjectFactory",
		                                                      "mit.cadlab.dome3.swing.table.FillerTableObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$DefaultTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$UneditableTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");
		f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
		                          "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$UneditableTableObject",
		                          "mit.cadlab.dome3.objectmodel.DomeObject");

        if(optimizationParameterType.equals(QMOOConfiguration.INTERFACE_VARIABLE))
			f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                              "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$InterfaceVariableOptimizationTableObject",
		                              "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
		else if(optimizationParameterType.equals(QMOOConfiguration.INTERFACE_OBJECTIVE))
			f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                              "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$InterfaceObjectiveOptimizationTableObject",
		                              "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
        else if(optimizationParameterType.equals(OptimizationToolInterfaceBase.QMOO_INTERFACE_BUILD_VIEW_PARAMETER))
            f.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
                                      "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$OptimizationParameterTableObject",
                                      "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");
		return f;
	}


	public static class DefaultTableObject extends DomeTableObject
	{
		public DefaultTableObject(DomeObject obj, boolean isEditable)
		{
			super(obj, new boolean[]{isEditable, false, false}); // is name editable?
		}

		public DefaultTableObject(DomeObject obj)
		{
			this(obj, false); // not editable
		}

		public Object getDomeObject()
		{
			return object;
		}

		public void addTableTreeObjectListener(DomeObject obj)
		{
			RunTreeObjectFactory.getTreeObject(obj).
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

	//add here for equal relation
	public static class EqualRelationTableObject extends UneditableTableObject
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

	public static class ProceduralRelationTableObject extends UneditableTableObject
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

	public static class ProjectRunParameterTableObject extends ParameterTableObject {
		public ProjectRunParameterTableObject(Parameter obj)
		{
			super(obj);
			setEditable(new boolean[] {false, false, false});    //for now, do not let users edit name, value or status
																 //of parameters in the project run GUI
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
		protected Renderers.DomeFileValueRenderer fileRenderer = new Renderers.DomeFileValueRenderer();

		protected BooleanComboBoxEditor booleanEditor = new BooleanComboBoxEditor();
		protected IntegerRunEditor integerEditor = new IntegerRunEditor();
		protected RealRunEditor realEditor = new RealRunEditor();
		protected StringBuildEditor stringEditor = new StringBuildEditor();
		protected VectorRunEditor vectorEditor = new VectorRunEditor();
		protected MatrixRunEditor matrixEditor = new MatrixRunEditor();
		protected PreferenceRunEditor preferenceEditor = new PreferenceRunEditor();
		protected EnumerationBuildEditor enumerationEditor = new EnumerationBuildEditor();
		protected Editors.fileBuildEditor fileEditor = new Editors.fileRunEditor();

		protected DataObject currentData;


		protected static final String NOTHING = "";
		protected PropertyChangeListener dataValueListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String property = e.getPropertyName();
				if (property.equals("value")) {
					fireTableObjectChanged(1);
				} else if (property.equals("size")) {//for vector and matrix
					fireTableObjectChanged(1);
				}
			}
		};

		public ParameterTableObject(Parameter obj)
		{
			super(obj);
			ModelObjectScope scope = obj.getScope();
			if (scope instanceof ModelInterfaceRuntimeClient) { //for interface run time params only
				boolean isItemofCausality = ((ModelInterfaceRuntimeClient) scope).isItemOfCausality(obj,
				                                                                                    CausalityStatus.INDEPENDENT);
				if (isItemofCausality || (obj.getCurrentDataObject() instanceof DomeFile)) {
					setEditable(new boolean[]{false, true, true}); // value is editable for independents
					//file dat type
				} else {
					setEditable(new boolean[]{false, false, false}); // value is uneditable for all others
				}
			} else {
				setEditable(new boolean[]{false, true, true}); // only value is editable
			}
			currentData = ((Parameter) object).getCurrentDataObject();
			currentData.addPropertyChangeListener(dataValueListener);
			((Parameter) object).addPropertyChangeListener(new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent e)
				{
					String property = e.getPropertyName();
					if (property.equals(Parameter.VALUE_STATUS)) {
						fireTableObjectChanged(1);
					} else if (property.equals(Parameter.DATATYPESELECTION) ||
					        property.equals(Parameter.CURRENT_TYPE)) {
						currentData.removePropertyChangeListener(dataValueListener);
						currentData = ((Parameter) object).getCurrentDataObject();
						currentData.addPropertyChangeListener(dataValueListener);
						fireTableObjectChanged(1);
					}
				}
			});
		}

		public void addTableTreeObjectListener(DomeObject obj)
		{
			RunTreeObjectFactory.getTreeObject(obj).
			        addTreeObjectListener(new TableTreeObjectListener());
		}

		public Object getValueAt(int column)
		{
			if (column == 0)
				return ((DomeObject) object).getName();
			else if (column == 1) { // value
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
			if (column == 1) {
				DataObject data = ((Parameter) object).getCurrentDataObject();
				if (data instanceof DomeBoolean)
					return defaultRenderer;
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
			}
			return null;
		}

		public TableCellEditor getEditorAt(int column)
		{
			if (column == 1) {
				DataObject data = ((Parameter) object).getCurrentDataObject();
				if (data instanceof DomeBoolean)
					return booleanEditor;
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
			}
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
        protected RealRunEditor _realEditor = new RealRunEditor();
        protected StringBuildEditor _stringEditor = new StringBuildEditor();
        protected EnumerationBuildEditor _enumerationEditor = new EnumerationBuildEditor();

        protected DataObject _currentData;

        private OptimizationParameter _optimizationParameter;

        protected static final String NOTHING = "";

        private static final int VALUE_COLUMN = 1;

        protected class QMOOListener implements PropertyChangeListener
        {
            public QMOOListener() {}

            public void propertyChange(PropertyChangeEvent e)
            {
                String property = e.getPropertyName();
                if (property.equals("value") || property.equals("unit"))
                    fireTableObjectChanged(VALUE_COLUMN);
            }
        }

        public OptimizationParameterTableObject(Parameter obj)
        {
            super(obj, new boolean[]{false, true, false});  // make all columns editable
            this._currentData = ((Parameter) object).getCurrentDataObject();
            this._currentData.addPropertyChangeListener(new QMOOListener());
            ModelObjectScope scope = obj.getScope();
            if (scope instanceof OptimizationInterfaceRuntimeClient)
            {
                if (((OptimizationInterfaceRuntimeClient) scope).getInterfaceOptimizationMap().containsKey(object))
                {
                    _optimizationParameter = (OptimizationParameter) ((OptimizationInterfaceRuntimeClient) scope).getInterfaceOptimizationMap().get(object);
                }
                else
                    OneButton1Msg.showWarning(null, "run interface error", "parameter not found", "OK", new Dimension(150, 75));
            }

            if (_optimizationParameter == null)
                OneButton1Msg.showError(null, "error", "parameter not found", "OK", new Dimension(150, 75));
        }

        public void addTableTreeObjectListener(DomeObject obj)
        {
            RunTreeObjectFactory.getTreeObject(obj).
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
        }
    }

    public static class InterfaceObjectiveOptimizationTableObject extends DomeTableObject
    {
        protected DefaultRenderer _defaultRenderer = new DefaultRenderer();
        protected DomeRealValueRenderer _realRenderer = new DomeRealValueRenderer();
        protected DomeStringValueRenderer _stringRenderer = new DomeStringValueRenderer();
        protected DomeEnumerationRenderer _enumerationRenderer = new DomeEnumerationRenderer();

        protected RealRunEditor _realEditor = new RealRunEditor();
        protected StringBuildEditor _stringEditor = new StringBuildEditor();
        protected EnumerationBuildEditor _enumerationEditor = new EnumerationBuildEditor();

        protected Renderers.BooleanCheckBoxRenderer _booleanRenderer = new Renderers.BooleanCheckBoxRenderer();
        protected Editors.BooleanCheckBoxEditor _booleanEditor = new Editors.BooleanCheckBoxEditor();

        protected DataObject _currentData;

        protected ObjectiveParameter _objectiveParameter;

        protected static final String NOTHING = "";
        protected static final int VALUE_COLUMN = 1;

        protected class ParameterPropertyListener implements PropertyChangeListener
        {
            public ParameterPropertyListener()
            {
            }

            public void propertyChange(PropertyChangeEvent e)
            {
                String property = e.getPropertyName();
                if (property.equals("value") || property.equals("unit"))
                    fireTableObjectChanged(VALUE_COLUMN);
            }
        }


        public InterfaceObjectiveOptimizationTableObject(Parameter obj)
        {
            super(obj);
            _currentData = ((Parameter) object).getCurrentDataObject();
            _currentData.addPropertyChangeListener(new ParameterPropertyListener());
            ModelObjectScope scope = obj.getScope();
            if (scope instanceof OptimizationInterfaceRuntimeClient)
            {
                OptimizationInterfaceRuntimeClient i = (OptimizationInterfaceRuntimeClient) obj.getScope();
                _objectiveParameter = (ObjectiveParameter) i.getInterfaceObjectiveMap().get(obj);
                boolean[] permissions = {
                    false,
                    false,
                    false,
                    i.getUserPermission(OptimizationInterfaceConfiguration.IS_OBJECTIVE_ACTIVE)
                };
                setEditable(permissions);
            }
        }

        public void addTableTreeObjectListener(DomeObject obj)
        {
            BuildTreeObjectFactory.getTreeObject(obj).
            addTreeObjectListener(new TableTreeObjectListener());
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

            if (column == 3)
            {
                return this._objectiveParameter.getIsActiveForEditor();
            }
            else
                return NOTHING;
        }

        public TableCellRenderer getRendererAt(int column)
        {
            if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return this._realRenderer;
                return null;
            }
            else if (column == 2)
                return this._enumerationRenderer;
            else if (column == 3)
                return this._booleanRenderer;
            else
                return null;
        }

        public TableCellEditor getEditorAt(int column)
        {
            if (column == 3)
                return _booleanEditor;
            else
                return null;
        }

        public void setValueAt(Object value, int column)
        {
            if (value == null) return;
            if (column == 3)
            {
                _objectiveParameter.setIsActive(new BooleanData((Boolean)value));
            }
        }
    }

    public static class InterfaceVariableOptimizationTableObject extends DomeTableObject
    {
        protected DefaultRenderer _defaultRenderer = new DefaultRenderer();
        protected DomeRealValueRenderer _realRenderer = new DomeRealValueRenderer();
        protected DomeStringValueRenderer _stringRenderer = new DomeStringValueRenderer();
        protected DomeEnumerationRenderer _enumerationRenderer = new DomeEnumerationRenderer();

        protected RealRunEditor _realEditor = new RealRunEditor();
        protected StringBuildEditor _stringEditor = new StringBuildEditor();
        protected EnumerationBuildEditor _enumerationEditor = new EnumerationBuildEditor();

        protected Renderers.BooleanCheckBoxRenderer _booleanRenderer = new Renderers.BooleanCheckBoxRenderer();
        protected Editors.BooleanCheckBoxEditor _booleanEditor = new Editors.BooleanCheckBoxEditor();

        protected DataObject _currentData;

        protected VariableParameter _variableParameter;

        protected static final String NOTHING = "";
        protected static final int VALUE_COLUMN = 1;

        protected boolean _hasLostFocus = false;

        protected class ParameterPropertyListener implements PropertyChangeListener
        {
            public ParameterPropertyListener()
            {
            }

            public void propertyChange(PropertyChangeEvent e)
            {
                String property = e.getPropertyName();
                if (property.equals("value") || property.equals("unit"))
                    fireTableObjectChanged(VALUE_COLUMN);
            }
        }


        public InterfaceVariableOptimizationTableObject(Parameter obj)
        {
            super(obj);
            _currentData = ((Parameter) object).getCurrentDataObject();
            _currentData.addPropertyChangeListener(new ParameterPropertyListener());
            ModelObjectScope scope = obj.getScope();
            if (scope instanceof OptimizationInterfaceRuntimeClient)
            {
                OptimizationInterfaceRuntimeClient i = (OptimizationInterfaceRuntimeClient) obj.getScope();
                _variableParameter = (VariableParameter) i.getInterfaceVariableMap().get(obj);
                boolean[] permissions = {
                    false,
                    true,
                    i.getUserPermission(OptimizationInterfaceConfiguration.ALLOW_VARIABLE_SEARCH),
                    i.getUserPermission(OptimizationInterfaceConfiguration.ALLOW_VARIABLE_SEARCH),
                    i.getUserPermission(OptimizationInterfaceConfiguration.IS_VARIABLE_ACTIVE)
                };
                setEditable(permissions);
            }
        }

        public void addTableTreeObjectListener(DomeObject obj)
        {
            BuildTreeObjectFactory.getTreeObject(obj).
            addTreeObjectListener(new TableTreeObjectListener());
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
            if(column == 4)
            {
                return this._variableParameter.getIsActiveForEditor();
            }
            else
                return NOTHING;
        }

        public TableCellRenderer getRendererAt(int column)
        {
            if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return this._realRenderer;
                return null;
            }
            else if (column == 2)
                return this._realRenderer;
            else if (column == 3)
                return this._realRenderer;
            else if (column == 4)
                return this._booleanRenderer;
            else
                return null;
        }

        public TableCellEditor getEditorAt(int column)
        {
            if (column == 1 || column == 2 || column == 3)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    return this._realEditor;
            }
            else if (column == 4)
                return _booleanEditor;
            return null;
        }

        public void setValueAt(Object value, int column)
        {
            if (value == null) return;
            if (column == 1)
            {
                DataObject data = ((Parameter) object).getCurrentDataObject();
                if (data instanceof DomeReal)
                    ((DomeReal) data).setRealValue((Double) value);
            }
            else if (column == 2)
                if (_variableParameter.isValidLowerLimit((Double)value))
                    _variableParameter.setLowerLimit((Double)value);
                else
                {
                    if (!_hasLostFocus)
                    {
                        // UGLY hack to prevent the dialog box from showing up twice when the gui is out of focus
                        _hasLostFocus = true;
                        OneButton1Msg.showWarning(null, "Run Mode Warning", "Lower limit of parameter '" + _variableParameter.getParameter().getName()
                            + "' must be less than the parameter value.", "OK", OneButton1Msg.DEFAULT_SIZE);
                        _hasLostFocus = false;
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
            else if (column == 4)
                _variableParameter.setIsActive(new BooleanData((Boolean)value));
        }
    }

}
