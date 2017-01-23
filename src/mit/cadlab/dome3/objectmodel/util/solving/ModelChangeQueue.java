// ModelChangeQueue.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeListener;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.OrderedHashMap;

/**
 * The ModelChangeQueue is used to receive notifications of dataobject changes from all
 * parameters in the model.
 */
public class ModelChangeQueue
{
	// todo: temporarily changed to store items in OrderedHashMap instead of List
	// todo: should be changed back once changes to subscription interfaces are gated properly
	private String name = "";
	private OrderedHashMap changeMap = new OrderedHashMap();
	private DataObjectChangeListener dataListener = new ParameterDataObjectChangeListener();

	public ModelChangeQueue(String name) {
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public DataObjectChangeListener getDataObjectChangeListener()
	{
		return dataListener;
	}

	// support items which want to be notified when changes are added/removed from the queue

	public void addQueueListener(DListListener l)
	{
		changeMap.addKeySetListener(l);
	}

	public void removeQueueListener(DListListener l)
	{
		changeMap.removeKeySetListener(l);
	}

	public DataObjectChangeEvent getChangeForObject(Object obj) {
		return (DataObjectChangeEvent) changeMap.get(obj);
	}
	
	public synchronized boolean isEmpty()
	{
		return changeMap.isEmpty();
	}

	public int size() {
		return changeMap.size();
	}

	// synchronize access to changes
	protected synchronized void addChange(DataObjectChangeEvent e)
	{
//		Debug.trace(Debug.ALL, name + " change queue received: " + e);
		changeMap.put(e.getParameter(),e);
	}

	/**
	 * Removes the first item in the change queue and returns it.
	 * @return a DataObjectChangeEvent; null, if empty
	 */
	public synchronized DataObjectChangeEvent getChange()
	{
		if (changeMap.isEmpty())
			return null;
		DataObjectChangeEvent e = (DataObjectChangeEvent) changeMap.remove(0);
		//Debug.trace(Debug.ALL, name + " change queue removed: " + e);
		return e;
	}

	public String toString() {
		return Names.getNames(changeMap.keyList());
	}

	/**
	 * ParameterDataObjectChangeListener listens to changes and adds them to the queue.
	 */
	class ParameterDataObjectChangeListener extends DataObjectChangeListener
	{
		public ParameterDataObjectChangeListener()
		{
			super(ModelChangeQueue.this);
		}

		public void dataObjectChanged(DataObjectChangeEvent e)
		{
			addChange(e);
		}
	}

}
