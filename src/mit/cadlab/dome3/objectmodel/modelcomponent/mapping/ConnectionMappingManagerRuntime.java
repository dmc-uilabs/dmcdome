// ConnectionMappingManager.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.mapping;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import org.dom4j.Element;

public class ConnectionMappingManagerRuntime extends ConnectionMappingManager {

	public ConnectionMappingManagerRuntime(Model model) {
		super(model);
	}

	public ConnectionMappingManagerRuntime(Model model, Element xmlElement) {
		super(model, xmlElement);
	}

	/**
	 * Implement this method to process the mapping after it has been added successfully
	 * @param p Model object
	 * @param target Model object
	 */
	protected void addModelMappingHook(Parameter p, Parameter target) {
		// nothing here...taken care of by the queue
	}

	protected void addModelMappingHook(Visualization p, Visualization target) {
		// nothing here...taken care of by the queue
	}

	public boolean addInterfaceMapping(Parameter p, Parameter target) {
		if (target.getScope() instanceof DefaultSubscription) {
			target = ((DefaultSubscription) target.getScope()).getInterfaceParameter(target);
		}
		if (_addMapping(p, target) && connectionMgr.addConnection(target, p)) {
			//add a connection between model and interface relation
			//if the parameters are in relation
			ModelObjectScope scopeP = p.getScope();   //interface relation
			ModelObjectScope scopeTarget = target.getScope(); //model relation
			if ((scopeP instanceof Relation) && (scopeTarget instanceof Relation)) {
				Object modelRel = connectionMgr.getModelConnection((Relation) scopeP);
				if (modelRel == null) { //if the connection does not exist already
					connectionMgr.addConnection((Relation) scopeTarget, (Relation) scopeP);
				}
			}
			addInterfaceMappingHook(p, target);
			fireMappingChange(p);
		}
		return true;
	}


	/**
	 * Implement this method to process the mapping after it has been added successfully
	 * @param p Interface object
	 * @param target Model object
	 */
	protected void addInterfaceMappingHook(Parameter p, Parameter target) {
		InterfaceParameterRuntime ifaceParam = (InterfaceParameterRuntime) p;
		ModelParameterRuntime mdlParam = (ModelParameterRuntime) target;
		ifaceParam.setModelParameter(mdlParam);
	}

	protected void addInterfaceMappingHook(Visualization p, Visualization target) {
        //** add something
	}
	/**
	 * Implement this method to process the mapping after it has been removed successfully
	 * @param p
	 * @param target
	 */
	protected void removeMappingHook(Parameter p, Parameter target) {
		if (p instanceof InterfaceParameterRuntime) {
			removeInterfaceMapping(p, target);
		} else if (target instanceof InterfaceParameterRuntime) {
			removeInterfaceMapping(target, p);
		}
		// model mappings are ignored and taken care of by the queue
	}

	protected void removeMappingHook(Visualization p, Visualization target) {
		//todo
		/*if (p instanceof InterfaceParameterRuntime) {
			removeInterfaceMapping(p, target);
		} else if (target instanceof InterfaceParameterRuntime) {
			removeInterfaceMapping(target, p);
		} */
		// model mappings are ignored and taken care of by the queue
	}

	protected void removeInterfaceMapping(Parameter p, Parameter target) {
		InterfaceParameterRuntime ifaceParam = (InterfaceParameterRuntime) p;
		ModelParameterRuntime mdlParam = (ModelParameterRuntime) target;
		ifaceParam.removeModelParameter(mdlParam);
	}

	protected void removeInterfaceMapping(Visualization p, Visualization target) {
		//todo
		/*InterfaceParameterRuntime ifaceParam = (InterfaceParameterRuntime) p;
		ModelParameterRuntime mdlParam = (ModelParameterRuntime) target;
		ifaceParam.removeModelParameter(mdlParam); */
	}

}
