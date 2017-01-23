// FrameIconImageGenerator.java
package mit.cadlab.dome3.swing;

import java.awt.Color;
import java.awt.Image;
import java.util.Vector;

public class FrameIconImageGenerator
{

	protected int index = 0;
	protected Image[] images = {};

	public FrameIconImageGenerator(String[] colorStrings)
	{
		Vector colors = new Vector();
		for (int i = 0; i < colorStrings.length; ++i) {
			Color c = Templates.getColorFromString(colorStrings[i]);
			if (c != null) colors.add(c);
		}
		images = new Image[colors.size()];
		for (int i = 0; i < colors.size(); ++i) {
			Color c = (Color) colors.elementAt(i);
			images[i] = Templates.makeRectangleImage(16, 16, c);
		}
	}

	public Image nextIconImage()
	{
		index = index % images.length;
		return images[index++];
	}

	public int countImages()
	{
		return images.length;
	}

}
