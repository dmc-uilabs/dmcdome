package com.ge.ceed.domeapi;

/**
 * Interface IModel has the requirements for a 'model' that can be run.
 * It maps to DomeInterface and "integration model"
 * @author dliscomb
 *
 */
public interface IModel {

	/**
	 * Returns the DomeInterfaceEntity that describes this IntegrationModel's public interface
	 * @return
	 */
	public DomeInterfaceEntity getInterface();

	/**
	 * Adds a single IN parameter
	 * @param p
	 */
	public void addInParam(ModelParam p);

	/**
	 * Sets the collection of IN parameters, replacing any existing previously
	 * @param p
	 */
	public void setInParams(ModelParam[] params);

	/**
	 * Adds a single OUT parameter
	 * @param p
	 */
	public void addOutParam(ModelParam p);

	/**
	 * Sets the collection of OUT parameters, replacing any existing previously
	 * @param p
	 */
	public void setOutParams(ModelParam[] params);
	
	/**
	 * Returns the input ModelParam object whose getName() matches the name specified
	 * @param name
	 * @return
	 */
	public ModelParam getInParam(String name);

	/**
	 * Returns the output ModelParam object whose getName() matches the name specified
	 * @param name
	 * @return
	 */
	public ModelParam getOutParam(String name);

	/**
	 * Returns the array of OUT ModelParam parameter objects
	 * @return
	 */
	public ModelParam[] getOutParams();

	/**
	 * Returns the array of IN ModelParam parameter objects
	 * @return
	 */
	public ModelParam[] getInParams();

	/**
	 * Returns the public description of the model
	 * @return
	 */
	public String getDescription();

	/**
	 * Returns the friendly name of the model
	 * @param modelName
	 */
	public String getName();
	
	/**
	 * Returns the server connection information
	 * @return
	 */
	public Server getServer();

}