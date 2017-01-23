package com.ge.ceed.domeapi;

import org.slf4j.Logger;

/**
 * Class to hold a simple connection between two single parameters
 * @author dliscomb
 *
 */
public class SimplePassThroughConnector implements IConnector {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SimplePassThroughConnector.class);
	
	private static final String CONNECT = "=";
	private ModelParam from;
	private ModelParam to;
	
	public SimplePassThroughConnector(ModelParam from, ModelParam to){
		this.from=from;
		this.to=to;
	}

	public ModelParam getFrom() {
		return from;
	}

	public ModelParam getTo() {
		return to;
	}

	@Override
	public String toEqn() {
		return this.to.getInstancename()+"."+this.to.getName() + CONNECT + this.from.getInstancename()+ "." + this.from.getName();
	}

	@Override
	public String toString() {
		return SimplePassThroughConnector.class.getSimpleName()+" [from=" + from + ", to=" + to + "]";
	}

}
