package com.ge.ceed.domeapi;

/**
 * Interface for connectors between {@link ModelParam} objects from {@link Model}s
 * @author dliscomb
 *
 */
public interface IConnector {

	/**
	 * Return this connector in 'eqn' format
	 * @return
	 */
	public String toEqn();
	
	/**
	 * Return the {@link ModelParam} of the 'from' end
	 * @return
	 */
	public ModelParam getFrom();
	 
	/**
	 * Return the {@link ModelParam} of the 'to' end
	 * @return
	 */
	public ModelParam getTo();

}
