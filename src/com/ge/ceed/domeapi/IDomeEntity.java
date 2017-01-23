package com.ge.ceed.domeapi;

/**
 * Interface for data objects that will be streamed in JSON format by Gson
 * 
 * @author dliscomb
 *
 */
public interface IDomeEntity {

	/**
	 * On deserialization, we need to know what the concrete type is, so this method returns an identifier that matches the concrete type
	 * @return String identifier which is unique to the concrete type 
	 */
	public abstract String getType();

	/**
	 * 'Name' field of the data object
	 * @return
	 */
	public abstract String getName();

	/**
	 * Add a child IDomeEntity
	 * @param child
	 */
	public abstract void addChild(IDomeEntity child);

	/**
	 * Returns the array of Integer with the path to the object
	 * @return Array of Integer which is the path to the object 
	 */
	public abstract Integer[] getPath();

}