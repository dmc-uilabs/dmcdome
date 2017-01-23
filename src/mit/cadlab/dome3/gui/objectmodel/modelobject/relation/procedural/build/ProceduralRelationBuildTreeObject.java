// ProceduralRelationBuildTreeObject.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.ProceduralRelationTreeObject;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;

public class ProceduralRelationBuildTreeObject extends ProceduralRelationTreeObject
{

	public ProceduralRelationBuildTreeObject(ConcreteProceduralRelation r)
	{
		super(r);
	}

	protected void makeGui()
	{
		ConcreteProceduralRelation relation = (ConcreteProceduralRelation) getDomeObject();
		gui = new DomeBuildFrame(new ProceduralRelationBuildPanel(relation));
	}

}
