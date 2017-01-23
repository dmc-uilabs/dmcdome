// VisualizationBuildTreeObject.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.run.VisualizationRunPanel;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.swing.WindowTracker;

public class VisualizationRunTreeObject extends VisualizationTreeObject
{

	public VisualizationRunTreeObject(Visualization vis)
	{
		super(vis);
	}

	protected void makeGui()
	{
		WindowTracker parent = RunMode.getCurrentWindowTracker();
		Visualization vis = (Visualization) getDomeObject();
		gui = new DomeRunFrame(new VisualizationRunPanel(vis), parent);
	}

}
