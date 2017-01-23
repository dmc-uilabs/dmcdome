/**
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
package mit.cadlab.dome3.config;

/**
 * This is a class that provides common configuration options to DOME
 * 
 * @author Alex Iankoulski
 *
 */
public class DomeOptions {

	/**
	 * Define this system property to alter the behavior of DomeModelSolver and AbstractModelExecutionManager 
	 * When this property is set, models that contain an iteration relation will stop the solver 
	 * and end the model run even if the model has a feedback loop connecting the output of the iteration relation 
	 * to another model's input.   
	 */
	private static final String propStopInfiniteLoops = "dome.option.StopInfiniteLoops";
	
	public static final boolean STOP_INFINITE_LOOPS = System.getProperties().containsKey(propStopInfiniteLoops);

}
