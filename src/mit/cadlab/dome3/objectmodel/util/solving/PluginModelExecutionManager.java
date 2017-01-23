// PluginModelExecutionManager.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.plugin.PluginModelRuntime;

public class PluginModelExecutionManager extends AbstractModelExecutionManager
{
	protected PluginParameterChangeTracker tracker;
	protected PluginModelSolver solver;

	public PluginModelExecutionManager(PluginModelRuntime model)
	{
		super(model);
		tracker = new PluginParameterChangeTracker(model);
		solver = new PluginModelSolver(model, tracker);
	}

	public void cleanup()
	{
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

	/**
	 * Most plugin applications do not support being run in a thread. So this implementation does not use threads.
	 */
	public void startSolving()
	{
		if (isSolving())
			return;
		synchronized (this) {
			isSolving = true;
			setShouldStop(false); // TODO: is this the right thing to do here?
		}
		runModel();
	}

	protected void cleanupSolving()
	{
		synchronized (this) {
			isSolving = false;
		}
	}

	protected boolean executeNextStep()
	{
		if (solver.isReadyToExecutePlugin()) {
			((PluginModelRuntime) model).execute(tracker.getAffectedOutputs());
			tracker.notifyPluginModelExecuted();
			return true;
		}
		else
			return false;
	}

}