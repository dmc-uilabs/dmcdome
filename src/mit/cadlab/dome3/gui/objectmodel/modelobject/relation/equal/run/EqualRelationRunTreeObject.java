// EqualRelationRunTreeObject.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.run;

import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.run.EqualRelationRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.EqualRelationTreeObject;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation;
import mit.cadlab.dome3.swing.WindowTracker;

public class EqualRelationRunTreeObject extends EqualRelationTreeObject
{

	public EqualRelationRunTreeObject(ConcreteEqualRelation r)
	{
		super(r);
	}

	protected void makeGui()
	{
		WindowTracker parent = RunMode.getCurrentWindowTracker();
		ConcreteEqualRelation relation = (ConcreteEqualRelation) getDomeObject();
		gui = new DomeRunFrame(new EqualRelationRunPanel(relation), parent);
	}

}