// ProceduralRelationRunTreeObject.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.run;

import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.run.ProceduralRelationRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.ProceduralRelationTreeObject;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.swing.WindowTracker;

public class ProceduralRelationRunTreeObject extends ProceduralRelationTreeObject
{

	public ProceduralRelationRunTreeObject(ConcreteProceduralRelation r)
	{
		super(r);
	}

	protected void makeGui()
	{
		WindowTracker parent = RunMode.getCurrentWindowTracker();
		ConcreteProceduralRelation relation = (ConcreteProceduralRelation) getDomeObject();
		gui = new DomeRunFrame(new ProceduralRelationRunPanel(relation), parent);
	}

}