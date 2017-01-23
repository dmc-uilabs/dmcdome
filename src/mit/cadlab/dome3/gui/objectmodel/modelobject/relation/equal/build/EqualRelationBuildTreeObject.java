// EqualRelationBuildTreeObject.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.EqualRelationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.EqualRelationTreeObject;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation;

public class EqualRelationBuildTreeObject extends EqualRelationTreeObject
{

	public EqualRelationBuildTreeObject(ConcreteEqualRelation r)
	{
		super(r);
	}

	protected void makeGui()
	{
		ConcreteEqualRelation relation = (ConcreteEqualRelation) getDomeObject();
		gui = new DomeBuildFrame(new EqualRelationBuildPanel(relation));
	}

}
