package com.ge.ceed.domeapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mit.cadlab.dome3.objectmodel.project.IntegrationProject;

import org.slf4j.Logger;

/**
 * Holds the definition of a DOME IntegrationModel, which is made of other models.
 * Only used during composition, not retrieval
 * 
 * @author Drew Liscomb
 *
 */
public class IntegrationModel implements IModel {
	
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IntegrationModel.class);
	
	protected Model model; //holds this model's name, interface, and in & out params
	protected Map<String, IModel> resources; // Map<name, interface>: internal interfaces to be used to provide the IntegrationModel's functionality
	//TODO: change to List<IConnector<ModelParam>>, create IConnectorSerializer, add 'type' to IConnector
	protected Map<String,IConnector> connectors; // how the parameters are connected to one another
	protected String space; // the space in which to keep this model
	protected Server server; // the server on which this model runs

	private DomeFolderEntity folder;

	private String description;

	private DomeEntity domeEntityDelegate;
	
	@SuppressWarnings("unused") // for Gson
	private IntegrationModel() {
		
	}
		
	/**
	 * Return a new IntegrationModel object
	 * @param folder the {@link DomeFolderEntity} that should hold the IntegrationModel as an {@link IntegrationProject}
	 * @param interFace {@link DomeInterfaceEntity that will be exposed}
	 */
	public IntegrationModel(DomeFolderEntity folder, DomeInterfaceEntity interFace) {
		this.setFolder(folder);
		this.model = new Model(interFace);
		this.connectors = new HashMap<String,IConnector>();
		this.resources = new HashMap<String, IModel>();
	}

	public Map<String, IConnector> getConnectors() {
		return Collections.unmodifiableMap(this.connectors);
	}

	public void setConnectors(Map<String, IConnector> connectors) {
		this.connectors.clear();
		for (String connectorName : connectors.keySet()) {
			this.connectors.put(connectorName, connectors.get(connectorName));
		}
	}

	@Override
	public DomeInterfaceEntity getInterface() {
		return model.getInterface();
	}

	@Override
	public void addInParam(ModelParam p) {
		model.addInParam(p);
	}

	@Override
	public void setInParams(ModelParam[] params) {
		model.setInParams(params);
	}

	@Override
	public void addOutParam(ModelParam p) {
		model.addOutParam(p);
	}

	@Override
	public void setOutParams(ModelParam[] params) {
		model.setOutParams(params);
	}

	@Override
	public ModelParam getInParam(String name) {
		return model.getInParam(name);
	}

	@Override
	public ModelParam getOutParam(String name) {
		return model.getOutParam(name);
	}

	@Override
	public ModelParam[] getOutParams() {
		return model.getOutParams();
	}

	@Override
	public ModelParam[] getInParams() {
		return model.getInParams();
	}

	@Override
	public String getName() {
		return model.getName();
	}

	public void setName(String friendlyName) {
		model.setName(friendlyName);
	}
	
	public String getSpace() {
		return this.space;
	}
	
	public void setSpace(String space) {
		this.space = space;
	}

	public DomeFolderEntity getFolder() {
		return folder;
	}

	public void setFolder(DomeFolderEntity folder) {
		this.folder = folder;
	}

	public Map<String, IModel> getInternalInterfaces() {
		return resources;
	}

	public void setInternalInteraces(Map<String, IModel> interFaces) {
		this.resources = interFaces;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}
	
	@Override
	public String toString() {
		return "IntegrationModel [model=" + model + ", models=" + resources + ", connectors=" + connectors + ", space=" + space + ", server=" + server
				+ ", folder=" + folder + ", description=" + description + "]";
	}

}
