// MappingsBuildPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.tools;

import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardViewer;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton3Msg;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeFrame;
import mit.cadlab.dome3.gui.objectmodel.ModelComponentPanel;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomePreference;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.OptimizationToolBase;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeEvent;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeListener;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.DList;
import mit.cadlab.dome3.swing.DListModel;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

public class MappingsBuildPanel extends ModelComponentPanel
{
    public static final Dimension DEFAULT_SIZE = new Dimension(400, 250);
	protected static GridBagConstraints gbc;
	private ConnectionMappingManager mappingManager;
	private MappingChangeListener mappingChangeListener;
	private DefaultComboBoxModel relationsCbModel = null;
	private DefaultComboBoxModel parametersCbModel = null;
	private DefaultComboBoxModel mappingsCbModel = null;
	private DComboBox relationsComboBox, parametersComboBox;
	private DList parameterMappingsList;
	//private Model lastModel = null;
	private Parameter lastParameter = null;
	Map listeners = new HashMap();  //key - interface obj, value - listener

	public static DomeFrame createMappingTool(ConnectionMappingManager manager)
	{
		return new DomeBuildFrame(new MappingsBuildPanel(manager));
	}

	private MappingsBuildPanel(ConnectionMappingManager manager)
	{
		super(manager, "Mappings: " + manager.getModel().getName());
		this.mappingManager = manager;
		mappingChangeListener = new ParameterMappingChangeListener();
		Model model = manager.getModel();
        if (model instanceof DomeModel)
        {
            init((DomeModel) model);
        }
        else if (model instanceof IntegrationProject)
        {
            init((IntegrationProject) model);
        }
        else if (model instanceof AnalysisTool)
        {
            init((AnalysisTool)model);
        }
	}

	private void init(DomeModel mod)
	{
		relationsCbModel = new ModelObjectsComboBoxModel(mod.getFilter(DomeModel.RELATIONS_FILTER));
		relationsComboBox = Templates.makeDComboBox(relationsCbModel);
		relationsComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				setParametersComboBox();
				setRelationComboBoxSize();
			}
		});
		//add a listener on interfaces list
		ModelInterfaceManager ifaces = ((DomeModelBuilder) mod).getModelInterfacesManager();
		ifaces.addInterfacesListener(new InterfaceListListner());
		Collection interfaceCol = ifaces.getInterfaces();
		for (Iterator i = interfaceCol.iterator(); i.hasNext();) {
			ModelInterface interfce = (ModelInterface) i.next();
			DListListener l = new InterfaceObjectsListner();
			interfce.addModelObjectsListener(l);
			listeners.put(interfce, l);
		}
		parametersComboBox = Templates.makeDComboBox();
		setParametersComboBox();
		parametersComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				setParameterMappingsList();
			}
		});
		parameterMappingsList = Templates.makeDList();
		setParameterMappingsList();
		layoutComponents();
	}

	private void init(IntegrationProject mod)
	{
		ArrayList interfaces = new ArrayList();
		//add a listener on interfaces list
		ModelInterfaceManager ifaces = mod.getProjectInterfacesManager();
		ifaces.addInterfacesListener(new InterfaceListListner());
		Collection interfaceCol = ifaces.getInterfaces();
		for (Iterator i = interfaceCol.iterator(); i.hasNext();) {
			ModelInterface interfce = (ModelInterface) i.next();
			DListListener l = new InterfaceObjectsListner();
			interfce.addModelObjectsListener(l);
			listeners.put(interfce, l);
			interfaces.add(interfce);
		}
		relationsCbModel = new ModelObjectsComboBoxModel(interfaces);
		relationsComboBox = Templates.makeDComboBox(relationsCbModel);
		relationsComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				setParametersComboBox();
				setRelationComboBoxSize();
			}
		});
		parametersComboBox = Templates.makeDComboBox();
		setParametersComboBox();
		parametersComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setParameterMappingsList();
			}
		});
		parameterMappingsList = Templates.makeDList();
		setParameterMappingsList();
		layoutComponents();
	}

    private void init(AnalysisTool mod)
    {
        ArrayList interfaces = new ArrayList();
        AnalysisToolInterfaceManager ifaces = mod.getAnalysisToolInterfacesManager();
        ifaces.addInterfacesListener(new InterfaceListListner());
        Collection interfaceCol = ifaces.getInterfaces();
        for (Iterator i = interfaceCol.iterator(); i.hasNext();)
        {
            ToolInterface toolInterface = (ToolInterface) i.next();
            DListListener l = new InterfaceObjectsListner();
            toolInterface.addModelObjectsListener(l);
            listeners.put(toolInterface, l);
            interfaces.add(toolInterface);
        }
        relationsCbModel = new ModelObjectsComboBoxModel(interfaces);
        relationsComboBox = Templates.makeDComboBox(relationsCbModel);
        relationsComboBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                setParametersComboBox();
                setRelationComboBoxSize();
            }
        });
        parametersComboBox = Templates.makeDComboBox();
        setParametersComboBox();
        parametersComboBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                setParameterMappingsList();
            }
        });
        parameterMappingsList = Templates.makeDList();
        setParameterMappingsList();
        layoutComponents();
    }

	private void layoutComponents()
	{
		JButton closeButton = Templates.makeButton("close", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				dispose();
			}
		});

		JComponent[] comps = {Templates.makeLabel("model/relation/interface"),
		                      relationsComboBox,
		                      Templates.makeLabel("mappings for"),
		                      parametersComboBox,
		                      new JScrollPane(parameterMappingsList),
		                      closeButton
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)};
		Templates.layoutGridBagB(this, comps, gbcs);

		setPreferredSize(DEFAULT_SIZE);
		setRelationComboBoxSize();
	}

	private class InterfaceListListner implements DListListener
	{
		public void intervalAdded(DListEvent e)
		{
			java.util.List objs = e.getItems();
			for (Iterator i = objs.iterator(); i.hasNext();) {
				Object object = i.next();
				relationsCbModel.addElement(object);
				DListListener listener = new InterfaceObjectsListner();
				if (object instanceof ModelInterface) {
					((ModelInterface) object).addModelObjectsListener(listener);
				}
				listeners.put(object, listener);
			}
		}

		public void intervalRemoved(DListEvent e)
		{
			java.util.List objs = e.getItems();
			for (Iterator i = objs.iterator(); i.hasNext();) {
				Object object = i.next();
				relationsCbModel.removeElement(object);
				DListListener listener = (DListListener) listeners.get(object);
				if (object instanceof ModelInterface) {
					((ModelInterface) object).removeModelObjectsListener(listener);
				}
				listeners.remove(object);
			}
		}

		public void itemsRemoved(DListEvent e)
		{
			intervalRemoved(e);
		}

		public void intervalChanged(DListEvent e)
		{
		}

		public void itemsReplaced(DListEvent e)
		{
		}
	}

	private class InterfaceObjectsListner implements DListListener
	{
		public void intervalAdded(DListEvent e)
		{
			updateParameterComboBox(e);
		}

		public void intervalRemoved(DListEvent e)
		{
			updateParameterComboBox(e);
		}

		private void updateParameterComboBox(DListEvent e)
		{
			java.util.List objs = e.getItems();
			for (Iterator i = objs.iterator(); i.hasNext();) {
				Object object = i.next();
				Object boxobj = relationsComboBox.getSelectedItem();
				if (object instanceof Parameter) {
					ModelObjectScope scope = ((Parameter) object).getScope();
					if (boxobj.equals(scope)) {
						setParametersComboBox();
						break;
					}
				}
			}
		}

		public void itemsRemoved(DListEvent e)
		{
			intervalRemoved(e);
		}

		public void intervalChanged(DListEvent e)
		{
		}

		public void itemsReplaced(DListEvent e)
		{
		}
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
    {
        if (mappingManager.getModel() instanceof IntegrationProject)
        {
            MenuManager.setContext(ModeContexts.BUILD_PROJECT_MAPPING_TOOL);
        }
        else if (mappingManager.getModel() instanceof OptimizationToolBase)
        {
            MenuManager.setContext(ModeContexts.BUILD_ANALYSIS_TOOL_MAPPING_TOOL);
        }
        else if (((DomeModel) mappingManager.getModel()).isIntegrationModel())
        {//inside integration project
            MenuManager.setContext(ModeContexts.BUILD_PROJECT_MAPPING_TOOL);
        }
        else if (mappingManager.getModel() instanceof PluginModel)
        {//plugin model
            MenuManager.setContext(ModeContexts.BUILD_PLUGIN_MAPPING_TOOL);
        }
        else
        {//dome model
            MenuManager.setContext(ModeContexts.BUILD_MAPPING_TOOL);
        }
        BuildFocusTracker.notifyInFocus(this, mappingManager);
    }

	public void close()
	{
		if (relationsCbModel != null && relationsCbModel instanceof ModelObjectsComboBoxModel)
			((ModelObjectsComboBoxModel) relationsCbModel).destroy();
		if (parametersCbModel != null && parametersCbModel instanceof ModelObjectsComboBoxModel)
			((ModelObjectsComboBoxModel) parametersCbModel).destroy();
		if (mappingsCbModel != null && mappingsCbModel instanceof ModelObjectsComboBoxModel)
			((ModelObjectsComboBoxModel) mappingsCbModel).destroy();
	}

	public void setCurrentParameter(Parameter p)
	{
		if (p == null) return;
		relationsComboBox.setSelectedItem(p.getScope());
		setRelationComboBoxSize();
	}

//    public void setCurrentRelation(GenericRelation r) {
	public void setCurrentRelation(Relation r)
	{
		if (r == null) return;
		relationsComboBox.setSelectedItem(r);

		setRelationComboBoxSize();
	}

	private void setParametersComboBox()
	{
		Object rel = relationsCbModel.getSelectedItem();
		DefaultComboBoxModel oldCbModel = parametersCbModel;
		if (rel == null)
			parametersCbModel = new DefaultComboBoxModel();
		else if (rel instanceof Model) {
			parametersCbModel = new ModelObjectsComboBoxModel((Model) rel);
		} else if (rel instanceof Relation) {
			parametersCbModel = new ModelObjectsComboBoxModel(((Relation) rel));
		} else if (rel instanceof Subscription) {
			Collection coll = ((Subscription) rel).getModelObjectParameters();
			parametersCbModel = new ModelObjectsComboBoxModel(coll);
		} else if (rel instanceof ModelInterface) {
			Collection coll = ((ModelInterfaceBuilder) rel).getModelObjectParameters();
			parametersCbModel = new ModelObjectsComboBoxModel(coll);
		}
		parametersComboBox.setModel(parametersCbModel);
		if (oldCbModel != null && oldCbModel instanceof ModelObjectsComboBoxModel)
			((ModelObjectsComboBoxModel) oldCbModel).destroy();
		if (parametersCbModel.getSize() > 0)
			parametersComboBox.setSelectedIndex(0);
	}

	private void setParameterMappingsList()
	{
		if (lastParameter != null) {
			mappingManager.removeMappingChangeListener(lastParameter, mappingChangeListener);
			lastParameter = null;
		}
		Parameter param = (Parameter) parametersCbModel.getSelectedItem();
		DefaultComboBoxModel oldCbModel = mappingsCbModel;
		if (param == null) {
			mappingsCbModel = new DefaultComboBoxModel();
		} else {
			lastParameter = param;
			mappingManager.addMappingChangeListener(lastParameter, mappingChangeListener);
			mappingsCbModel = new ModelObjectsComboBoxModel(mappingManager.getMappingsForParameter(param));
		}
		parameterMappingsList.setModel(mappingsCbModel);
		if (oldCbModel != null && oldCbModel instanceof ModelObjectsComboBoxModel)
			((ModelObjectsComboBoxModel) oldCbModel).destroy();
	}

	private void updateParameterMappingsList(Collection mappings)
	{
		DefaultComboBoxModel oldCbModel = mappingsCbModel;
		mappingsCbModel = new ModelObjectsComboBoxModel(mappings);
		parameterMappingsList.setModel(mappingsCbModel);
		if (oldCbModel != null && oldCbModel instanceof ModelObjectsComboBoxModel)
			((ModelObjectsComboBoxModel) oldCbModel).destroy();
	}

	public void addMappingsLastSelection()
	{
		Parameter param = (Parameter) parametersCbModel.getSelectedItem();
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();

		if (param == null || sel == null) return;

		addMappings(param, sel.getItems().iterator());
	}

	public void addMappingsFromClipboard()
	{
		Parameter param = (Parameter) parametersCbModel.getSelectedItem();
		ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);

		if (param == null || selections == null) return;

		// check that the mappings are valid
		DSet allSelections = new DSet(); // items can not be repeated
		for (int i = 0; i < selections.length; ++i)
			allSelections.addAll(selections[i].getItems());

		addMappings(param, allSelections.iterator());
	}

	private void addMappings(Parameter param, Iterator items)
	{
		Model model = mappingManager.getModel();
		while (items.hasNext()) {
			Object obj = items.next();
			if (obj instanceof Parameter) {
				Parameter p = (Parameter) obj;
				if (model.equals(p.getModel()) ||
				        ((p.getModel() instanceof Relation) && model.equals((p.getModel()).getModel()))) {
					mappingManager.addMapping(param, p);
				} else {
//					System.out.println("can only map parameters within same model");
					OneButton1Msg.showWarning(this, "Warning: mapping",
					                          "can only map parameters within same model", "OK", new Dimension(150, 100));
				}
			} else {
//				System.out.println("can only map parameters to parameter");
				OneButton1Msg.showWarning(this, "Warning: mapping",
				                          "can only map parameters to parameters", "OK", new Dimension(150, 100));
			}
		}
	}

	public void clearSelection()
	{
		parameterMappingsList.clearSelection();
	}

	public void selectAllRows()
	{
		int nMaps = mappingsCbModel.getSize();
		if (nMaps == 0) return;
		parameterMappingsList.setSelectionInterval(0, nMaps - 1);
	}

	public void deleteMappings()
	{
		Parameter relParam = (Parameter) parametersCbModel.getSelectedItem();
		if (relParam == null) return;
		Object[] items = parameterMappingsList.getSelectedValues();
		mappingManager.removeMappings(relParam, Arrays.asList(items));

	}

	protected void dispose()
	{
		SwingUtilities.windowForComponent(this).dispose();
	}

	protected void setRelationComboBoxSize()
	{
		if (relationsComboBox.getPreferredSize() != parametersComboBox.getPreferredSize()) {
			relationsComboBox.setMaximumSize(parametersComboBox.getPreferredSize());
			relationsComboBox.setMinimumSize(parametersComboBox.getPreferredSize());
			repaint();
		}

	}

	protected class ParameterMappingChangeListener implements MappingChangeListener
	{
		public void mappingChanged(MappingChangeEvent event)
		{
			if (lastParameter != null && lastParameter.equals(event.getParameter()))
				updateParameterMappingsList(event.getMappings());
		}
	}

	protected static class ModelObjectsComboBoxModel extends DefaultComboBoxModel
	        implements DListModel
	{
		protected ModelObjectNameListener nameListener = new ModelObjectNameListener();
		private DListListener listListener = new ModelObjectsListListener();
		private Model model = null;
		private Filter filter = null;
		private Relation relation = null;

		public ModelObjectsComboBoxModel(Filter relationFilter)
		{
			// first item is model
			Model m = relationFilter.getModel();
			addElement(m);
			Collection interfaces = ((AbstractDomeModel) m).getModelInterfacesManager().getInterfaces();
			for (Iterator i = interfaces.iterator(); i.hasNext();) {
				addElement(i.next());
			}
			//add subscription here
			Collection subsriptions = ((AbstractDomeModel) m).getSubscriptions();
			for (Iterator i = subsriptions.iterator(); i.hasNext();) {
				addElement(i.next());
			}
			Iterator relations = relationFilter.getItems().iterator();
			while (relations.hasNext())
				addElement(relations.next());
			relationFilter.addFilterListener(listListener);
			filter = relationFilter;
		}

		public ModelObjectsComboBoxModel(Model mdl)
		{
			if (mdl instanceof DomeModel) {
				filter = ((DomeModel) mdl).getFilter(DomeModel.PARAMETERS_FILTER);
				Iterator mObjs = filter.getItems().iterator();
				while (mObjs.hasNext())
					addElement(mObjs.next());
				filter.addFilterListener(listListener);
			} else {
				model = mdl;
				Iterator mObjs = mdl.getModelObjects().iterator();
				while (mObjs.hasNext())
					addElement(mObjs.next());
				mdl.addModelObjectsListener(listListener);
			}
		}

		public ModelObjectsComboBoxModel(Relation rel){
			relation =rel;
			Iterator mObjs = rel.getModelObjects().iterator();
			while (mObjs.hasNext())
					addElement(mObjs.next());
			rel.addModelObjectsListener(listListener);
		}

		public ModelObjectsComboBoxModel(Collection items)
		{
			Iterator mObjs = items.iterator();
			while (mObjs.hasNext())
				addElement(mObjs.next());
		}

		public void destroy()
		{
			int numberItems = getSize();
			for (int i = 0; i < numberItems; ++i)
				((DomeObject) getElementAt(i)).removePropertyChangeListener("name", nameListener);
			if (model != null)
				model.removeModelObjectsListener(listListener);
			if (filter != null)
				filter.removeFilterListener(listListener);
			if (relation != null)
				relation.removeModelObjectsListener(listListener);
		}

		public void addElement(Object obj)
		{
			if (obj instanceof DomeObject) {
				super.addElement(obj);
				((DomeObject) obj).addPropertyChangeListener("name", nameListener);
			}
		}

		public void removeElement(Object obj)
		{
			if (obj instanceof DomeObject) {
				super.removeElement(obj);
				((DomeObject) obj).removePropertyChangeListener("name", nameListener);
			}
		}

		//Qing : change here to add icons
		public Icon getIcon(int index)
		{
			Object obj;
			if (index == -1)
				obj = getSelectedItem();
			else
				obj = getElementAt(index);
			if (obj == null) return null;
			DomeObject dobj = ((DomeObject) obj).getDomeObject();
			if (dobj instanceof Model)
				return DomeIcons.getIcon(DomeIcons.MODEL);
			else if (dobj instanceof ProceduralRelation)
				return DomeIcons.getIcon(DomeIcons.RELATION);
			else if (dobj instanceof EqualRelation)
				return DomeIcons.getIcon(DomeIcons.RELATIONEQUAL);
			else if (dobj instanceof Subscription)
				return DomeIcons.getIcon(DomeIcons.SUBSCRIBE_INTERFACE);
			else if (dobj instanceof ModelInterface)
				return DomeIcons.getIcon(DomeIcons.INTERFACE);
			if (dobj instanceof Parameter && ((Parameter) dobj).getScope() instanceof Relation)
				return DomeIcons.getIcon(DomeIcons.RELATION_PARAMETER);
			else if (dobj instanceof Parameter && ((Parameter) dobj).getScope() instanceof Subscription)
				return DomeIcons.getIcon(DomeIcons.SUBSCRIBE_PARAMETER);
			else if (dobj instanceof Parameter && ((Parameter) dobj).getScope() instanceof ModelInterface)
				return DomeIcons.getIcon(DomeIcons.INTERFACE_PARAMETER);
			else if (dobj instanceof Parameter && ((Parameter) dobj).getScope() instanceof Model)
				return DomeIcons.getIcon(DomeIcons.PARAMETER);

			return null;
		}

		public String getListText(int index)
		{
			Object obj;
			if (index == -1)
				obj = getSelectedItem();
			else
				obj = getElementAt(index);
			if (obj == null) return " ";
			return ((DomeObject) obj).getName();
		}

		class ModelObjectsListListener implements DListListener
		{
			public void intervalChanged(DListEvent e)
			{
			}

			public void intervalAdded(DListEvent e)
			{
				addModelObjects(e.getItems().iterator());
			}

			public void intervalRemoved(DListEvent e)
			{
				removeModelObjects(e.getItems().iterator());
				}

			public void itemsRemoved(DListEvent e)
			{
				removeModelObjects(e.getItems().iterator());
			}

			public void itemsReplaced(DListEvent e)
			{
			}

			private void addModelObjects(Iterator mObjs)
			{
				while (mObjs.hasNext())
					addElement(mObjs.next());
			}

			private void removeModelObjects(Iterator mObjs)
			{
				while (mObjs.hasNext())
					removeElement(mObjs.next());
			}
		}

		class ModelObjectNameListener implements PropertyChangeListener
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String property = e.getPropertyName();
				if (property.equals("name")) {
					int index = getIndexOf(e.getSource());
					fireContentsChanged(ModelObjectsComboBoxModel.this, index, index);
				}
			}
		}

	} // end ModelObjectsComboBoxModel

	// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected final MappingsBuildPanel getMappingTool(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof MappingsBuildPanel) {
					return (MappingsBuildPanel) o;
				}
			}
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof MappingsBuildPanel)
				return (MappingsBuildPanel) comp;
			throw new NullPointerException("No current MappingsBuildPanel");
		}
	}

	// --- actions for menus and buttons --------------------
	public static final AbstractAction addMappingsLastSelectionAction = new FocusTrackerAction("Map last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getMappingTool(e).addMappingsLastSelection();
		}
	};

	public static final AbstractAction addMappingsClipboardAction = new FocusTrackerAction("Map from clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getMappingTool(e).addMappingsFromClipboard();
		}
	};

	public static final AbstractAction clearSelectionAction = new FocusTrackerAction("Clear selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getMappingTool(e).clearSelection();
		}
	};

	public static final AbstractAction selectAllAction = new FocusTrackerAction("Select all")
	{
		public void actionPerformed(ActionEvent e)
		{
			getMappingTool(e).selectAllRows();
		}
	};

	public static final AbstractAction deleteAction = new FocusTrackerAction("Delete")
	{
		public void actionPerformed(ActionEvent e)
		{
			getMappingTool(e).deleteMappings();
		}
	};


	public static final JMenu menu = createMappingMenu();

	protected static JMenu createMappingMenu()
	{
		JMenu menu = MenuUtils.makeBoldMenu("Edit mapping");
		menu.add(MenuUtils.makeMenuItem(MappingsBuildPanel.addMappingsLastSelectionAction));
		menu.add(MenuUtils.makeMenuItem(MappingsBuildPanel.addMappingsClipboardAction));
		menu.addSeparator();
		menu.add(MenuUtils.makeMenuItem(MappingsBuildPanel.clearSelectionAction));
		menu.add(MenuUtils.makeMenuItem(MappingsBuildPanel.selectAllAction));
		menu.addSeparator();
		menu.add(MenuUtils.makeMenuItem(MappingsBuildPanel.deleteAction));
		return menu;
	}

	// deprecated methods below

	/**
	 * Check if the mapping is valid.
	 * @param selectedItems Items selected for mapping
	 * @return Mapping validity status (true = valid, false = invalid)
	 */
	private boolean isValidMapping(Collection selectedItems)
	{
		Model mod = null;
		Relation relation = null;
		Object modOrRel = relationsCbModel.getSelectedItem();
		Parameter param = (Parameter) parametersCbModel.getSelectedItem();

		if (param == null) return false;

		if (modOrRel instanceof Model) {
			mod = (Model) modOrRel;
			if (mod == null) return false;
			if (checkModelMapping(param, selectedItems) == false)
				return false;
		} else if (modOrRel instanceof Relation) {
			relation = (Relation) modOrRel;
			if (relation == null) return false;
			// check that the relation mapping is valid
			if (checkRelationMapping(relation, param, selectedItems) == false)
				return false;
		}

		// check the list of selected items
		Iterator iter = selectedItems.iterator();
		while (iter.hasNext()) {
			Object selectedObj = iter.next();
			if (selectedObj instanceof Parameter) {
				Collection params = Collections.singletonList(param);
				ModelObjectScope scope = ((Parameter) selectedObj).getScope();
				// check for valid model mappings
				if (scope instanceof Model) {
					if (checkModelMapping((Parameter) selectedObj, params) == false)
						return false;
				} else
				// check for valid relation mappings
					if (scope instanceof Relation) {
						if (checkRelationMapping((Relation) scope, (Parameter) selectedObj, params) == false)
							return false;
					}
			}
		}

		// check duplicate mappings
		if (isDuplicateMapping(param, selectedItems) == true)
			return false;

		// check compatible parameters
		if (isCompatibleMapping(param, selectedItems) == false)
			return false;

		return true;
	}


	/**
	 * Check whether a relation parameter can be mapped to a model object.
	 * Mapping is not possible when the relation parameter is independent
	 * (i.e., an input parameter) or is already mapped to another object.
	 * All model parameters are automatically mapped to corresponding
	 * default interface parameters. Therefore, there is always one mapping
	 * which is to be ignored.
	 * @param rel Relation
	 * @param relParam Relation parameter
	 * @param selectedItems List of model objects
	 * @return Mapping validity status (true = valid, false = invalid)
	 */
	private boolean checkRelationMapping(Relation rel, Parameter relParam, Collection selectedItems)
	{
		Iterator iter = selectedItems.iterator();
		CausalityStatus causality = rel.getCausality(relParam);
		Collection mappings = mappingManager.getMappingsForParameter(relParam);
		if (iter.hasNext() &&
		        causality.equals(CausalityStatus.INDEPENDENT) &&
		        mappings != Collections.EMPTY_LIST && mappings.size() > 1) {
			DomeObject selectedObj = (DomeObject) iter.next();
			String msg = "can only be mapped to one item. Therefore it cannot be mapped to";
			OneButton3Msg.showWarning(this, "Warning: mapping", msg, relParam.getName(),
			                          selectedObj.getName(), "OK", new Dimension(1, 1));
			return false;
		}
		return true;
	}

	/**
	 * Check whether a model parameter is being mapped to a relation parameter or interface parameter.
	 * Mapping is invalid otherwise.
	 * @param modelParam Model parameter
	 * @param selectedItems List of objects to be mapped.
	 * @return Mapping validity status (true = valid, false = invalid)
	 */
	private boolean checkModelMapping(Parameter modelParam, Collection selectedItems)
	{
		Iterator iter = selectedItems.iterator();
		while (iter.hasNext()) {
			DomeObject selectedObj = (DomeObject) iter.next();
			ModelObjectScope scope = ((Parameter) selectedObj).getScope();
			if (!(selectedObj instanceof Parameter) &&
			        !(scope instanceof Relation || scope instanceof ModelInterface)) {
				String msg = "is a model parameter and can only be mapped to relation parameters. ";
				msg += "A twin or equal relation may be used to equate it with the parameter";
				OneButton3Msg.showWarning(this, "Warning: mapping", msg, modelParam.getName(),
				                          selectedObj.getName(), "OK", new Dimension(1, 1));
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether a mapping is valid between two objects. Mapping is invalid
	 * when the objects are already mapped to one another.
	 * @param targetParam Parameter to be mapped
	 * @param selectedItems Items to maps to the target parameter
	 * @return Mapping validity status (true = valid, false = invalid)
	 */
	private boolean isDuplicateMapping(Parameter targetParam, Collection selectedItems)
	{
		Collection mappings = mappingManager.getMappingsForParameter(targetParam);

		Iterator iter = selectedItems.iterator();
		while (iter.hasNext()) {
			DomeObject selectedObj = (DomeObject) iter.next();
			Iterator mappedParams = mappings.iterator();
			while (mappedParams.hasNext()) {
				Parameter mappedParam = (Parameter) mappedParams.next();
				if (mappedParam.equals(selectedObj)) {
					String msg = "is already mapped to";
					OneButton3Msg.showWarning(this, "Warning: mapping", msg, targetParam.getName(),
					                          selectedObj.getName(), "OK", new Dimension(1, 1));
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Check that the mapped data types and the models are compatible.
	 * @param targetParam Parameter to be mapped
	 * @param selectedItems Items to maps to the target parameter
	 * @return Mapping validity status (true = valid, false = invalid)
	 */
	private boolean isCompatibleMapping(Parameter targetParam, Collection selectedItems)
	{
		Iterator iter = selectedItems.iterator();
		while (iter.hasNext()) {
			DomeObject selectedObj = (DomeObject) iter.next();

			// check data types
			if (selectedObj instanceof Parameter &&
			        isCompatibleType(targetParam, (Parameter) selectedObj) == false) {
				String msg = "cannot be mapped because they have incompatible data types.";
				String item = targetParam.getName() + " and " + selectedObj.getName();
				OneButton2Msg.showWarning(this, "Warning: mapping", msg, item, "OK", new Dimension(1, 1));
				return false;
			}

			// check models
			if (selectedObj instanceof Parameter) {
				if (targetParam.getModel() != ((Parameter) selectedObj).getModel()) {
					String msg = "are in different models. Mapping can only be performed within";
					msg += "the same model. Use subscribe between models.";
					String item = targetParam.getName() + " and " + selectedObj.getName();
					OneButton2Msg.showWarning(this, "Warning: mapping", msg, item, "OK", new Dimension(1, 1));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Determine whether two dome parameters are compatible.
	 * @return Compatibility status (true = compatible, false = incompatible)
	 */
	private boolean isCompatibleType(Parameter p1, Parameter p2)
	{
		DataObject p1Type = p1.getCurrentDataObject();
		DataObject p2Type = p2.getCurrentDataObject();
		if (p1Type instanceof DomeReal || p1Type instanceof DomeInteger) {
			return (p2Type instanceof DomeReal || p2Type instanceof DomeInteger);
		} else if (p1Type instanceof DomeString || p1Type instanceof DomeText)
			return (p2Type instanceof DomeString || p2Type instanceof DomeText);
		else if (p1Type instanceof DomeBoolean)
			return (p2Type instanceof DomeBoolean);
		else if (p1Type instanceof DomeMatrix)
			return (p2Type instanceof DomeMatrix);
		else if (p1Type instanceof DomePreference)
			return (p2Type instanceof DomePreference);
		else if (p1Type instanceof DomeVector)
			return (p2Type instanceof DomeVector);
		else if (p1Type instanceof DomeEnumeration)
			return (p2Type instanceof DomeEnumeration);
		else if (p1Type instanceof DomeFile)
			return (p2Type instanceof DomeFile);

		return false;
	}
}