package dsm;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.lang.UnsupportedOperationException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class PlayIcon implements Icon {
    Color color1;
    int[] xPoints = new int[4];
    int[] yPoints = new int[4];
    int Points = 4;
    int sq;
  public PlayIcon(Color c1, int sq) {
    color1 = c1;
    this.sq = sq;
  }
  public void paintIcon(Component c, Graphics g, int x, int y)
  {
        g.setColor(color1);
	    g.fillOval(getIconWidth()/2 - 2,getIconHeight()/2 ,getIconWidth()-3,getIconHeight()-3);
  }
  
  public int getIconWidth()
  {
    return sq;
  }
  public int getIconHeight()
  {
    return sq;
  }

  public void setColor(Color cl)
  {
	    color1 = cl;
  }

  public Color getColor()
  {
	  return color1;
  }
}

