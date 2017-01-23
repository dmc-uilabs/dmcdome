// DomeModelExecutionManager.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.util.ClassUtils;

import java.util.List;

/**
 * This class manages changes between mapped parameters in the model.
 */
public class DomeModelExecutionManager extends AbstractModelExecutionManager
{

	protected DomeModelParameterChangeTracker tracker;
	protected DomeModelSolver solver;
	volatile SolvingThread solverThread = null;

	/**
	 * Solver should be created only after model has all parameters created
	 * @param model
	 */
	public DomeModelExecutionManager(DomeModelRuntime model)
	{
		super(model);
		tracker = new DomeModelParameterChangeTracker();
		solver = new DomeModelSolver(model, tracker);
	}

	public void cleanup() {
		solver.cleanup();
	}
	
	protected AbstractModelSolver getSolver()
	{
		return solver;
	}

	protected AbstractParameterChangeTracker getParameterChangeTracker()
	{
		return tracker;
	}

	public void startSolving()
	{
//		System.out.println(new Date() + "\tstartSolving: " + this.model.getName());
		if (isSolving())
			return;
		synchronized (this) {
			isSolving = true;
			setShouldStop(false); // todo: is this the right thing to do here?
			solverThread = new SolvingThread();
			solverThread.start();
		}
	}

	protected void cleanupSolving()
	{
		synchronized (this) {
			solverThread = null;
			isSolving = false;
		}
	}


	// todo reorganize location of relation execution code
	/**
	 * Finds the next best relation to execute and executes it.
	 * @return true if a relation is executed; otherwise, returns false
	 */
	protected boolean executeNextStep()
	{
		Object[] relData = solver.getNextRelationToFire();
		if (relData == null) {
			tracker.setLoopEntryPoint(null);//means finish execution
            tracker.setexit_loop(false);
			return false;
		}
		Object rel = relData[0];
		if (rel instanceof AbstractProceduralRelation) {
			Debug.trace(Debug.ALL, "executing RELATION: " + ((AbstractProceduralRelation) rel).getName() + " " +
			                       Names.getNames((List) relData[1]));
			((AbstractProceduralRelation) rel).execute((List) relData[1]);
		}
		else if (rel instanceof SubscriptionInterface) {
			Debug.trace(Debug.ALL, "submitting SUBSCRIPTION: " + ((SubscriptionInterface) rel).getName() + " " +
			                       Names.getNames((List) relData[1]));
			((SubscriptionInterface) rel).submitChanges();
		}
		else {
			System.err.println("can not execute relation type " + ClassUtils.getClassName(rel));
			// pretend we executed and go on
		}
		tracker.notifyRelationExecuted(rel, (List) relData[1]);
		return true;
	}


	/**
	 * The SolvingThread is used to put the execution of the model in a separate thread from the main model.
	 */
	class SolvingThread extends Thread
	{
		public void run()
		{
			runModel();
		}
	}

}
