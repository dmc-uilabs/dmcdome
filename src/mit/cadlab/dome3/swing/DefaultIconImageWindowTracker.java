// DefaultIconImageWindowTracker.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing;

import java.awt.Image;

public class DefaultIconImageWindowTracker extends DefaultWindowTracker
        implements IconImageWindowTracker
{

	protected FrameIconImageGenerator generator;

	public DefaultIconImageWindowTracker(String[] colorStrings)
	{
		generator = new FrameIconImageGenerator(colorStrings);
	}

	public Image getChildIconImage()
	{
		return generator.nextIconImage();
	}

}
