// Latch.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.relation;

import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeListener;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.IterationVariable;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class Latch
{
	public static final String NAME = "Latch";
	public static final String CLOCK = "Clock";
	public static final String INPUT = "In";
	public static final String OUTPUT = "Out";

	DomeModelRuntime model;
	protected List latchInputs;
	protected ModelParameterRuntime[] latchOutputs;
	protected DataObjectChangeEvent[] lastEvents;

	public Latch(Context latchContext, DomeModelRuntime model) {
		this.model = model;
		Iterator contents = latchContext.getModelObjectReferences().iterator();
		Context content;
		while (contents.hasNext()) {
			content = (Context) contents.next();
			if (content.getName().equals(CLOCK)) {
				Parameter clockParam = (Parameter)getLiveParameter(content.getModelObjectReferences().get(0));
				clockParam.getCurrentDataObject().addPropertyChangeListener(IterationVariable.TICK,
				                                                            new TickListener());
			} else if (content.getName().equals(INPUT)) {
				List originalLatchInputs = content.getModelObjectReferences();
				latchInputs = new ArrayList();
				ModelParameterRuntime latchInput;
				for (int i = 0; i < originalLatchInputs.size(); i++) {
					latchInput = (ModelParameterRuntime) getLiveParameter(originalLatchInputs.get(i));
					latchInputs.add(latchInput);
					latchInput.addChangeListener(new LatchInputListener(i)); // hack for now
				}
			} else if (content.getName().equals(OUTPUT)) {
				latchOutputs = (ModelParameterRuntime[])content.getModelObjectReferences().toArray(new ModelParameterRuntime[]{});
				for (int i = 0; i < latchOutputs.length; i++) {
					latchOutputs[i] = (ModelParameterRuntime)getLiveParameter(latchOutputs[i]);
				}
			}
		}
		lastEvents = new DataObjectChangeEvent[latchInputs.size()];
	}

	protected Parameter getLiveParameter(Object obj) {
		Parameter p = (Parameter) obj;
		if (p.getScope() instanceof DefaultSubscription) {
			return ((DefaultSubscription)p.getScope()).getInterfaceParameter(p);
		}
		return p;
	}

	protected void triggerLatch() {
		doAdvancedNotification();
		DataObjectChangeEvent event;
		for (int i = 0; i < latchOutputs.length; i++) {
			event = lastEvents[i];
			if (event != null) {
				latchOutputs[i].getSolvingQueueListener().dataObjectChanged(event);
				lastEvents[i] = null;
			}
		}
	}

	protected void doAdvancedNotification() {
		for (int i = 0; i < latchInputs.size(); i++) {
			((ModelParameterRuntime)latchInputs.get(i)).setValueStatus(Parameter.VALUE_STATUS_INCONSISTENT);
		}
//		model.markAffectedParameters(latchInputs);
	}

	class TickListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt)
		{
			System.out.println("latch triggered");
			triggerLatch();
		}
	}

	class LatchInputListener extends DataObjectChangeListener
	{
		private int inputIndex;

		public LatchInputListener(int index)
		{
			super(Latch.this);
			this.inputIndex = index;
		}

		public void dataObjectChanged(DataObjectChangeEvent event)
		{
			lastEvents[inputIndex] = event;
		}
	}

}
