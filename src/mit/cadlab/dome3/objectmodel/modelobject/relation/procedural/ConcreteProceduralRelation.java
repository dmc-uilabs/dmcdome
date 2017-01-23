// ConcreteProceduralRelation.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.relation.procedural;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.MultiViewSupport;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.swing.PythonEditor;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.MultipleErrorsException;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ConcreteProceduralRelation extends AbstractProceduralRelation implements MultiViewSupport, ViewSupport
{
	protected int currentInsertionIndex = -1;
	protected List currentItems = Collections.EMPTY_LIST;
	protected HashMap views; // keyed by view name
	protected PythonEditor pyEditor;
	protected ModelObjectNameListener nameListener;
	protected PythonEditorListener pyEditorListener;

	public ConcreteProceduralRelation(ModelObjectScope m, Id id)
	{
		super(m, id);
		modelObjects.addDListListener(new RelationObjectsListener());
		views = new HashMap();
		nameListener = new ModelObjectNameListener();
		pyEditorListener = new PythonEditorListener();
		createViews();
		createPythonEditor();
	}

	public ConcreteProceduralRelation(ModelObjectScope m, Id id, ProceduralRelation rel)
	{
		super(m, id, rel);
		modelObjects.addDListListener(new RelationObjectsListener());
		views = new HashMap();
		nameListener = new ModelObjectNameListener();
		pyEditorListener = new PythonEditorListener();
		createViews();
		createPythonEditor();
	}

	public ConcreteProceduralRelation(ModelObjectScope m, Element xml)
	{
		super(m, xml);
		currentInsertionIndex = inputFilter.getItemCount();
		currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
		modelObjects.addDListListener(new RelationObjectsListener());
		views = new HashMap();
		nameListener = new ModelObjectNameListener();
		pyEditorListener = new PythonEditorListener();
		createViews();
		createPythonEditor();
	}

	public boolean isInputFilter(Object obj)
	{
		return inputFilter.equals(obj);
	}

	public PythonEditor getPythonEditor()
	{
		return pyEditor;
	}

	public int getParameterCount()
	{
		return modelObjects.size();
	}

	public void handleDependencyInfoException(Exception ex)
	{
//TODO later pop up a message dialog here
		System.out.println("ConcreteProceduralRelation: DependencyInfo Exception thrown");
		ex.printStackTrace();
		throw new RuntimeException();
	}

	protected void createPythonEditor()
	{
		String[] keywords = new String[getParameterCount()];
		Collection params = getModelObjects();
		Iterator it = params.iterator();
		for (int i = 0; i < getParameterCount(); i++) {
			Object obj = it.next();
			if (obj instanceof Parameter) {
				keywords[i] = ((Parameter) obj).getName();
				((Parameter) obj).addPropertyChangeListener(ModelObject.NAME, nameListener);
			}
		}
		pyEditor = new PythonEditor(null, keywords);
		pyEditor.setText(getBody());
		pyEditor.addPropertyChangeListener(PythonEditor.BODY, pyEditorListener);
	}

	protected void createViews()
	{
		// create model object type view
		List inputOutputView = new ArrayList();
		inputOutputView.add(inputFilter);
		inputOutputView.add(outputFilter);
		views.put(ProceduralRelation.INPUT_OUTPUT_VIEW, Collections.unmodifiableList(inputOutputView));
		// create model causality view
		List modelCausalityView = new ArrayList();
		modelCausalityView.add(independentFilter);
		modelCausalityView.add(intermediateFilter);
		modelCausalityView.add(resultFilter);
		views.put(ProceduralRelation.MODEL_CAUSALITY_VIEW, Collections.unmodifiableList(modelCausalityView));
	}

	// MultiViewSupport interface
	public List getViewNames()
	{
		return viewNames;
	}

	public List getView(String viewName)
	{
		if (viewNames.contains(viewName)) {
			List view = (List) views.get(viewName);
			return (view == null) ? Collections.EMPTY_LIST : view;
		}
		return Collections.EMPTY_LIST;
	}

	public void addViewListener(String viewName, DListListener l)
	{
		// do nothing, views do not change
	}

	public void removeViewListener(String viewName, DListListener l)
	{
		// do nothing, views do not change
	}

	// ViewSupport interface (override)

	public List getView()
	{
		return getView(ProceduralRelation.INPUT_OUTPUT_VIEW);
	}

	public void addViewListener(DListListener l)
	{
		// do nothing
	}

	public void removeViewListener(DListListener l)
	{
		// do nothing
	}
	/*

	//ViewSuppport interface
	//needed for tree view


	   public List getView() {
	       return Collections.unmodifiableList(modelObjects);
	   }

	   public void addViewListener(DListListener l) {
	      modelObjects.addDListListener(l);
	   }

	   public void removeViewListener(DListListener l) {
	        modelObjects.removeDListListener(l);
	   }
	*/

	public List getValidModelObjectTypes()
	{
		List types = Registry.getDataObjectTypes();
		types.add("parameter");
		return types;
	}

	public ModelObject newModelObject(String modelObjectType)
	{
		currentInsertionIndex = inputFilter.getItemCount();
		currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
		ModelObject obj = super.newModelObject(modelObjectType);
		//Qing: May 18th, add for not allow parameter has a space in name
		if(obj instanceof Parameter)
            obj.setName(getUniqueName((Parameter)obj, obj.getName()));
		chekckForFileType(obj);
		return obj;
	}

	public ModelObject newModelObject(ModelObject modelObject)
	{
		currentInsertionIndex = inputFilter.getItemCount();
		currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
		ModelObject obj = super.newModelObject(modelObject);
		//Qing: May 18th, add for not allow parameter has a space in name
        if(obj instanceof Parameter)
            obj.setName(getUniqueName((Parameter)obj, obj.getName()));
		chekckForFileType(obj);
		return obj;
	}

	public Collection newModelObjects(Collection mObjs)
	{
		currentInsertionIndex = inputFilter.getItemCount();
		currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
		Collection objs = super.newModelObjects(mObjs);
       //Qing: May 18th, add for not allow parameter has a space in name

		for(Iterator i=objs.iterator();i.hasNext();){
			ModelObject obj=(ModelObject)i.next();
			if(obj instanceof Parameter)
                obj.setName(getUniqueName((Parameter)obj, obj.getName()));
			chekckForFileType(obj);
		}
		return objs;
	}

	public ModelObject newModelObject(String modelObjectType, int index)
	{
		if (index < 0 || index > inputFilter.getItemCount())
			currentInsertionIndex = inputFilter.getItemCount();
		else
			currentInsertionIndex = index;
		currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
		ModelObject obj = super.newModelObject(modelObjectType);
		//Qing: May 18th, add for not allow parameter has a space in name
		if(obj instanceof Parameter)
            obj.setName(getUniqueName((Parameter)obj, obj.getName()));
		chekckForFileType(obj);
		return obj;
	}

	public ModelObject newModelObject(ModelObject modelObject, int index)
	{
		if (index < 0 || index > inputFilter.getItemCount())
			currentInsertionIndex = inputFilter.getItemCount();
		else
			currentInsertionIndex = index;
		currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
		ModelObject obj = super.newModelObject(modelObject);
		//Qing: May 18th, add for not allow parameter has a space in name
		if(obj instanceof Parameter)
            obj.setName(getUniqueName((Parameter)obj, obj.getName()));
		chekckForFileType(obj);
		return obj;
	}

	public Collection newModelObjects(Collection modelObjects, int index)
	{
		if (index < 0 || index > inputFilter.getItemCount())
			currentInsertionIndex = inputFilter.getItemCount();
		else
			currentInsertionIndex = index;
		currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
		Collection objs = super.newModelObjects(modelObjects);
        //Qing: May 18th, add for not allow parameter has a space in name

		for(Iterator i=objs.iterator();i.hasNext();){
			ModelObject obj=(ModelObject)i.next();
			if(obj instanceof Parameter)
                obj.setName(getUniqueName((Parameter)obj, obj.getName()));
			chekckForFileType(obj);
		}
		return objs;
	}



	// mapping shortcuts support
	public void addAndMapModelObjects(Collection origModelObjects)
	{
//		System.out.println("params: " + Names.getNameIds(origModelObjects));
		mapModelObjects(origModelObjects, newModelObjects(origModelObjects));
	}

	public void addAndMapModelObjects(Collection origModelObjects, int index)
	{
//		System.out.println("index: " + index + "\tparams: " + Names.getNameIds(origModelObjects));
		mapModelObjects(origModelObjects, newModelObjects(origModelObjects, index));
	}

	protected void mapModelObjects(Collection parameters, Collection relationParameters)
	{
//		System.out.println("relParmams: " + Names.getNameIds(relationParameters));
		if (parameters.size() != relationParameters.size()) {
			System.err.println("some objects not copied correctly -- no automapping");
			return;
		}
		ConnectionMappingManager mm = ((DomeModel) getModel()).getMappingManager();
		Iterator relParams = relationParameters.iterator();
		Iterator params = parameters.iterator();
		List errors = new ArrayList();
		while (relParams.hasNext()) {
			Parameter rp = (Parameter) relParams.next();
			Parameter p = (Parameter) params.next();
			try {
//				System.out.println("addMapping:" + Names.getNameId(rp) + " -> " + Names.getNameId(p));
				mm.addMapping(rp, p);
			} catch (RuntimeException ex) {
				/**/System.out.println(ex);
				errors.add(ex);
			}
		}
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException(errors);
	}

	class ModelObjectNameListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			if (property.equals(ModelObject.NAME)) {
				// update code editor
				Object oldValue = e.getOldValue();
				Object newValue = e.getNewValue();
				pyEditor.replaceKeyword((String) oldValue, (String) newValue);
				String text = pyEditor.getText();
				text.trim();
				setBody(text);
			}
		}
	}


	class PythonEditorListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			if (property.equals(PythonEditor.BODY)) {
				String text = pyEditor.getText().trim();
				setBody(text);
			}
		}
	}

	// change causality as a result
	public class RelationObjectsListener implements DListListener
	{
		public void intervalChanged(DListEvent e)
		{
		}

		public void intervalAdded(DListEvent e)
		{
			addItems(e.getItems());
		}

		public void intervalRemoved(DListEvent e)
		{
			removeItems(e.getItems());
		}

		public void itemsRemoved(DListEvent e)
		{
			removeItems(e.getItems());
		}

		public void itemsReplaced(DListEvent e)
		{
			throw new UnsupportedOperationException("can not set objects in Procedural Relation!");
		}
	}

	protected void addItems(List items)
	{
		currentItems.addAll(items);
		this.setDependencyInfo((new DependencyInfo(dependencyInfo, items, false)));
		currentInsertionIndex = -1; // important to reset so other filter activities are not changed
		currentItems = Collections.EMPTY_LIST;

		// insert in python editor and add object listeners
		for (Iterator objIter = items.iterator(); objIter.hasNext();) {
			ModelObject obj = (ModelObject) objIter.next();
			if (obj instanceof Parameter) {
				pyEditor.addKeyword(obj.getName());
				obj.addPropertyChangeListener(ModelObject.NAME, nameListener);
			}
		}
	}

	protected void removeItems(List items)
	{
		currentItems.removeAll(items);
		this.setDependencyInfo(new DependencyInfo(dependencyInfo, items, true));
		currentInsertionIndex = -1; // important to reset!
		currentItems = Collections.EMPTY_LIST;

		// remove from python editor and remove object listeners
		for (Iterator objIter = items.iterator(); objIter.hasNext();) {
			ModelObject obj = (ModelObject) objIter.next();
			if (obj instanceof Parameter) {
				pyEditor.removeKeyword(obj.getName());
				obj.removePropertyChangeListener(ModelObject.NAME, nameListener);
			}
		}
	}
}