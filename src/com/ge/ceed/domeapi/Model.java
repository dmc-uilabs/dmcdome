package com.ge.ceed.domeapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Data object to hold {@link DomeInterfaceEntity} with parameters.
 * Could ultimately be run via {@link DomeProxy#runModel(IModel, mit.cadlab.dome3.api.SolverStateTracker, String, Map)}
 * @author dliscomb
 *
 */
public class Model implements IModel {
	protected DomeInterfaceEntity interFace;
	protected Map<String, ModelParam> inParams = new HashMap<String, ModelParam>();
	protected Map<String, ModelParam> outParams = new HashMap<String, ModelParam>();
	protected String modelName;
	protected String modelDescription;
	protected Server server;
	
	/**
	 * Default constructor.
	 * @param interFace
	 */
	public Model(DomeInterfaceEntity interFace) {
		this.interFace = interFace;
		this.modelName = interFace.getName();
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IModel#getInterface()
	 */
	@Override
	public DomeInterfaceEntity getInterface() {
		return interFace;
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IModel#addInParam(com.ge.ceed.domeapi.ModelParam)
	 */
	@Override
	public void addInParam(ModelParam p) {
		inParams.put(p.getName(), p);
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IModel#setInParams(com.ge.ceed.domeapi.ModelParam[])
	 */
	@Override
	public void setInParams(ModelParam[] params) {
		inParams.clear();
		for (ModelParam modelParam : params) {
			addInParam(modelParam);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IModel#setOutParams(com.ge.ceed.domeapi.ModelParam[])
	 */
	@Override
	public void setOutParams(ModelParam[] params) {
		outParams.clear();
		for (ModelParam modelParam : params) {
			addOutParam(modelParam);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IModel#addOutParam(com.ge.ceed.domeapi.ModelParam)
	 */
	@Override
	public void addOutParam(ModelParam p) {
		Integer cnt = 0;
		String pKey = p.getName();
		while (outParams.containsKey(pKey)) {
			pKey += cnt.toString();
			cnt++;
		}
		p.setInstancename(pKey);
		outParams.put(pKey, p);
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IModel#getInParam(java.lang.String)
	 */
	@Override
	public ModelParam getInParam(String name) {
		return inParams.get(name);
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IModel#getInParam(java.lang.String)
	 */
	@Override
	public ModelParam getOutParam(String name) {
		return outParams.get(name);
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IModel#getOutParams()
	 */
	@Override
	public ModelParam[] getOutParams() {
		return outParams.values().toArray(new ModelParam[outParams.size()]);
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IModel#getInParams()
	 */
	@Override
	public ModelParam[] getInParams() {
		return inParams.values().toArray(new ModelParam[inParams.size()]);
	}

	public String getName() {
		return this.modelName;
	}

	public void setName(String friendlyName) {
		this.modelName=friendlyName;
	}
	
	public String getDescription() {
		return this.modelDescription;
	}

	public void setDescription(String description) {
		this.modelDescription = description;
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
		return "Model [interFace=" + interFace + ", inParams=" + inParams + ", outParams=" + outParams + ", modelName=" + modelName + ", modelDescription="
				+ modelDescription + "]";
	}

}
