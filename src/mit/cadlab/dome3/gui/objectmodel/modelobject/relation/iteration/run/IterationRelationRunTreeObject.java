// ProceduralRelationRunTreeObject.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.run;

import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.IterationRelationTreeObject;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation;
import mit.cadlab.dome3.swing.WindowTracker;

public class IterationRelationRunTreeObject extends IterationRelationTreeObject
{

	public IterationRelationRunTreeObject(ConditionIterationRelation r)
	{
		super(r);
	}

	protected void makeGui()
	{
		WindowTracker parent = RunMode.getCurrentWindowTracker();
		ConditionIterationRelation relation = (ConditionIterationRelation) getDomeObject();
		gui = new DomeRunFrame(new IterationRelationRunPanel(relation), parent);
	}

}