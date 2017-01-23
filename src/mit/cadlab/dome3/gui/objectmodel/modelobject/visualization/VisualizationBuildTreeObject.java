// VisualizationBuildTreeObject.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;

public class VisualizationBuildTreeObject extends VisualizationTreeObject
{

	public VisualizationBuildTreeObject(Visualization vis)
	{
		super(vis);
	}

	protected void makeGui()
	{
		Visualization vis = (Visualization) getDomeObject();
		gui = new DomeBuildFrame(new VisualizationBuildPanel(vis));
	}

}
