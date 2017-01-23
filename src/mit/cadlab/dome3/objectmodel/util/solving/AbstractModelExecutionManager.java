// AbstractModelExecutionManager.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.config.DomeOptions;
import mit.cadlab.dome3.network.server.functions.MessageFunctions;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.causality.CausalitySupport;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.objectmodel.exceptions.ModelExecutionException;
import mit.cadlab.dome3.objectmodel.exceptions.RelationExecutionException;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DomeJavaBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractModelExecutionManager extends DomeJavaBean
{
	public static String SOLVING_STATUS = "solving status";
	public static String SOLVING_STARTED = "solving started";
	public static String SOLVING_PAUSED = "solving paused";
	public static String SOLVING_WAITING = "solving waiting for more changes"; // imodels
	public static String SOLVING_ABORTED = "solving terminated due to error in model execution"; // abnormal completion
	public static String SOLVING_INCOMPLETE = "solving finished early; more changes needed"; // no more values to change or items to execute, but more changes need to come in
	public static String SOLVING_STOPPED = "solving stopped";

	protected DomeModel model;
	protected ModelChangeQueue changeQueue;
	protected boolean isSolving = false;
	private volatile Boolean shouldStop = Boolean.FALSE;
	private boolean shouldMarkAffectedParameters = true;

	// variables used to keep solving thread alive to wait for changes from subscriptions
	private int keepAliveCount = 0;    // reinitialize each time solving starts
	private int maxKeepAliveCount = 4; // how many seconds to keep the thread alive

	public AbstractModelExecutionManager(DomeModel model)
	{
		this.model = model;
		this.changeQueue = new ModelChangeQueue(model.getName());
		this.changeQueue.addQueueListener(new StatusPropagationListener());
		registerListenersOnParameters(model);
	}

	public void suspendStatusPropagation() {
		this.shouldMarkAffectedParameters = false;
	}

	public void resumeStatusPropagation() {
		this.shouldMarkAffectedParameters = true;
	}

	protected void registerListenersOnParameters(ModelObjectScope scope)
	{
		Iterator it = scope.getModelObjects().iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof ModelParameterRuntime) {
				// listen to parameter changes
				((ModelParameterRuntime) obj).addModelQueueListener(changeQueue.getDataObjectChangeListener());
			}
			else if (obj instanceof Subscription) {
				registerListenersOnParameters(((DefaultSubscription) obj).getInterface());
			}
			else if (obj instanceof ModelObjectScope) {
				registerListenersOnParameters((ModelObjectScope) obj);
			}
		}
	}

	protected abstract AbstractModelSolver getSolver();

	protected abstract AbstractParameterChangeTracker getParameterChangeTracker();

	public abstract void startSolving();

	protected abstract void cleanupSolving();

	/**
	 * @return true if something was executed
	 */
	protected abstract boolean executeNextStep();

	/**
	 * @param sourceId
	 * @param graph graph is in model parameters
	 */
	public void addExternalGraph(String sourceId, DirectedGraph graph)
	{
		getSolver().addExternalGraph(sourceId, graph);
	}

	// for calculating system causality of a runtime model
	public DirectedGraph getCompleteGraph()
	{
		return getSolver().getCompleteGraph();
	}

	public boolean isSolving()
	{
		synchronized (this) { // synchronize on this to synchronize with when solving starts and stops
			return isSolving;
		}
	}

	public void stopSolving()
	{
		setShouldStop(true);
	}

	protected void setShouldStop(boolean newStopValue)
	{
		synchronized (shouldStop) {
			shouldStop = (newStopValue ? Boolean.TRUE : Boolean.FALSE);
		}
	}

	protected boolean shouldStop()
	{
		synchronized (shouldStop) {
			return shouldStop.booleanValue();
		}
	}

	public boolean runModel()
	{
		firePropertyChange(SOLVING_STATUS, null, SOLVING_STARTED);
		boolean isProcessing = true;
		boolean isPaused = false;
		boolean exceptionThrown = false;
		this.keepAliveCount = 0; // resetting for this run
		try {
			while (!(isPaused = shouldStop()) && (isProcessing || waitForMoreChanges())) {
				isProcessing = processChanges();
				isProcessing |= executeNextStep();
			}
		}
		catch (Exception e) {
			exceptionThrown = true;
			handleModelExecutionError(e);
		}

		cleanupSolving();

		if (isPaused == true) { // paused
			firePropertyChange(SOLVING_STATUS, null, SOLVING_PAUSED);
		}
		else if (exceptionThrown) {
			firePropertyChange(SOLVING_STATUS, null, SOLVING_ABORTED); // stopped via exception thrown during model execution
		}
		else if (stillHasMoreChangesToProcess() && !DomeOptions.STOP_INFINITE_LOOPS) { // 20140205 Alex Iankoulski - Added option to stop infinite loops gracefully
			if (model.isIntegrationModel() // TODO: early notification to project resource
			/*|| ((ModelRuntime) model).isProjectResource()*/) // TODO: use split graphs to make finer distinction
				firePropertyChange(SOLVING_STATUS, null, SOLVING_WAITING);
			else { // stand alone models should complete all execution within one solving cycle
				handleModelExecutionError(new ModelExecutionException(
				        "Solving finished without updating all parameters.\n" +
				        "This is often caused by a plugin model running an external application that exited abnormally\n" +
				        "or mis-mapped output parameters."
				));
				firePropertyChange(SOLVING_STATUS, null, SOLVING_INCOMPLETE);
			}
		}
		else { // stopped normally
			firePropertyChange(SOLVING_STATUS, null, SOLVING_STOPPED);
		}
		return isPaused;
	}

	/**
	 * Processes all changes in the change queue until there are none left.
	 * @return true if any changes processed; otherwise, returns false
	 */
	protected boolean processChanges()
	{
		if (changeQueue.isEmpty()) {
			return false;
		}
		while (!changeQueue.isEmpty()) {
			DataObjectChangeEvent changeMsg = changeQueue.getChange();
			Parameter changedParam = changeMsg.getParameter();
			if (Parameters.isSubscriptionInput(changedParam) && (changeMsg.getCause() == null)) {
				// change from resource, do not notify parameter change tracker of the change
			}
			else {
				getParameterChangeTracker().notifyParamChange(changedParam); // mark parameter changed in solver
				// TODO: change algorithm to support multiple mappings to a subscription/relation input
				List mappedParams = new ArrayList(model.getMappingManager().getMappingsForParameter(changedParam));
				mappedParams.remove(changeMsg.getCause()); // do not send change to parameter that caused change!
				for (Iterator iterator = mappedParams.iterator(); iterator.hasNext();) {
					Parameter parameter = (Parameter) iterator.next();
					if (parameter instanceof ModelParameterRuntime) {
						((ModelParameterRuntime) parameter).getSolvingQueueListener().dataObjectChanged(changeMsg);
					}
					else {
						throw new IllegalArgumentException("ModelExecutionManager.processChanges: expected ModelParameterRuntime instance; got " +
						                                   ClassUtils.getClassName(parameter));
					}
				}
			}
		}
		return true;
	}

	protected void handleModelExecutionError(Exception ex)
	{
		System.err.println(model.getName() + " EXECUTION ERROR:");
		if (ex instanceof ModelExecutionException || ex instanceof RelationExecutionException) {
			System.err.println(ex.getMessage());
		}
		else {
			System.err.println("HANDLED EXCEPTION STACK TRACE--MESSAGE SENT TO CLIENTS");
			ex.printStackTrace();
			System.err.println("...END HANDLED EXCEPTION STACK TRACE");
		}
		CompoundId modelId = ((ModelRuntime) model).getRuntimeId();
		MessageFunctions.errorMessageToClient(modelId, modelId.getModelRuntimeId(), model.getName(), ex.getMessage());
		if (model.isIntegrationModel()) {
			// notify project of error
			IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime)model.getIntegrationProject();
			project.handleImodelAndResourceExecutionError(ex.getMessage());
		}
	}

	/**
	 * This method keeps the solving process alive until all changes have been submitted
	 * @return
	 */
	protected boolean stillHasMoreChangesToProcess()
	{
		return (getParameterChangeTracker().hasChangedParametersLeftToProcess() || hasInconsistentOutputs());
	}

	/**
	 * This method keeps the solving process alive for a bit just in case more changes come in.
	 * This is only applicable for integration models.
	 * @return
	 */
	protected boolean waitForMoreChanges() {
		if (stillHasMoreChangesToProcess()) {
			if (model.isIntegrationModel() && (this.keepAliveCount < this.maxKeepAliveCount)) {
				try {
					Thread.sleep(1000); // wait a second
				}
				catch (InterruptedException e) {
					// doesn't matter
				}
				this.keepAliveCount++;
				return true;
			}
		}
		return false;
	}

	protected boolean hasInconsistentOutputs()
	{
		Iterator outputs = getSolver().getLocalGraph().getOutputs().iterator();
		while (outputs.hasNext()) {
			if (Parameter.VALUE_STATUS_INCONSISTENT.equals(((Parameter) outputs.next()).getValueStatus()))
				return true;
		}
		return false;
	}

	/**
	 * To be called by model when model is done running
	 */
	public void markChangesConsistent()
	{
		Collection changedParams = getParameterChangeTracker().resetTracker();
		for (Iterator iterator = changedParams.iterator(); iterator.hasNext();) {
			((Parameter) iterator.next()).setValueStatus(Parameter.VALUE_STATUS_CONSISTENT);

		}
	}

	protected void setParameterStatusAndMarkAffectedParameters(Parameter p)
	{
		DataObjectChangeEvent event = changeQueue.getChangeForObject(p);
		if (event.getParameter().getScope() instanceof SubscriptionInterface)
			if (event.getCause() == null)
			{ // change from resource
				p.setValueStatus(Parameter.VALUE_STATUS_WAITING_VALIDATION);
				if (!(Parameter.VALUE_STATUS_STALE.equals(p.getValueStatus()) ||
						Parameter.VALUE_STATUS_INCONSISTENT.equals(p.getValueStatus())) &&
						CausalityStatus.INDEPENDENT.equals(((CausalitySupport) p.getScope()).getCausality(p)))
				{
					// to handle case of changes submitted from resource interface
					// tell parameter tracker about the change
					((DomeModelParameterChangeTracker) getParameterChangeTracker()).notifyResourceParamChange(p);
				}
			}
			else { // do not mark stale changes from mappings
				// since change from resource will arrive shortly
				if (shouldMarkAffectedParameters && getSolver().isLocalInput(p))
					((DomeModelBase) model).markAffectedParameters(p);
			}
		else {
			p.setValueStatus(Parameter.VALUE_STATUS_WAITING_VALIDATION);
			if (shouldMarkAffectedParameters && getSolver().isLocalInput(p))
				((DomeModelBase) model).markAffectedParameters(p);
		}
	}

	/**
	 * used to propagate inconsistent status to downstream parameters
	 */
	protected class StatusPropagationListener implements DListListener
	{
		public void intervalAdded(DListEvent e)
		{
			addItems(e.getItems());
		}

		public void intervalChanged(DListEvent e)
		{
			addItems(e.getItems());
		}

		public void intervalRemoved(DListEvent e)
		{
		}

		public void itemsRemoved(DListEvent e)
		{
		}

		public void itemsReplaced(DListEvent e)
		{
		}

		protected void addItems(List items)
		{
			for (Iterator iterator = items.iterator(); iterator.hasNext();) {
				setParameterStatusAndMarkAffectedParameters((Parameter) iterator.next());
			}
		}
	}

}
