// EqualRelationBuildTreeObject.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.build;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.IterationRelationTreeObject;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation;

public class IterationRelationBuildTreeObject extends IterationRelationTreeObject
{

	public IterationRelationBuildTreeObject(ConditionIterationRelation r)
	{
		super(r);
	}

	protected void makeGui()
	{
		ConditionIterationRelation relation = (ConditionIterationRelation) getDomeObject();
		gui = new DomeBuildFrame(new IterationRelationBuildPanel(relation));
	}

}
