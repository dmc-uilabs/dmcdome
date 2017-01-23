package mit.cadlab.dome3.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;

public class LineNumber extends JComponent
{
	private Color DEFAULT_BACKGROUND = Color.lightGray;
	private Color DEFAULT_FOREGROUND = Color.blue;
	private Font DEFAULT_FONT = new Font("monospaced", Font.PLAIN, 12);


	// LineNumber height (abends when I use MAX_VALUE)
	private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

	// Set right/left margin
	private final static int MARGIN = 5;

	// Line height of this LineNumber component
	private int lineHeight;

	// Line height of this LineNumber component
	private int fontLineHeight;

	//
	private int currentRowWidth;

	// Metrics of this LineNumber component
	private FontMetrics fontMetrics;

	//the Max line number
	private int maxLineNumber = 0;

	/**
	 * Convenience constructor for Text Components
	 */
	public LineNumber(JComponent component)
	{
		if (component == null) {

			setBackground(DEFAULT_BACKGROUND);
			setForeground(DEFAULT_FOREGROUND);
			setFont(DEFAULT_FONT);
		} else {
			setBackground(DEFAULT_BACKGROUND);
			//setForeground( component.getForeground() );
			setForeground(DEFAULT_FOREGROUND);
			setFont(component.getFont());

			//add by Qing here..
			if (component instanceof JTextComponent)
				maxLineNumber = ((JTextComponent) component).getDocument().getDefaultRootElement().getElementIndex(((JTextComponent) component).getDocument().getLength()) + 1;
		}

		setPreferredSize(99);
	}

	public void setPreferredSize(int row)
	{
		int width = fontMetrics.stringWidth(String.valueOf(row));

		if (currentRowWidth < width) {
			currentRowWidth = width;
			setPreferredSize(new Dimension(2 * MARGIN + width, HEIGHT));
		}
	}

	public void setFont(Font font)
	{
		super.setFont(font);
		fontMetrics = getFontMetrics(getFont());
		fontLineHeight = fontMetrics.getHeight();
	}

	/**
	 * The line height defaults to the line height of the font for this
	 * component. The line height can be overridden by setting it to a
	 * positive non-zero value.
	 */
	public int getLineHeight()
	{
		if (lineHeight == 0)
			return fontLineHeight;
		else
			return lineHeight;
	}

	public void setLineHeight(int lineHeight)
	{
		if (lineHeight > 0)
			this.lineHeight = lineHeight;
	}

	public int getStartOffset()
	{
		return 4;
	}

	public void paintComponent(Graphics g)
	{
		int lineHeight = getLineHeight();
		int startOffset = getStartOffset();
		Rectangle drawHere = g.getClipBounds();
		// System.out.println( drawHere );

		// Paint the background

		g.setColor(getBackground());
		g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

		// Determine the number of lines to draw in the foreground.
		// Note--by Qing Aug 12.2002
		// here number is determined by the size of the window, however we want to make it determined by document size... so some changes here..

		g.setColor(getForeground());

		int startLineNumber = (drawHere.y / lineHeight) + 1;
		int endLineNumber;
		//drawHere.height --> changed to document.length
		// if(maxLineNumber==0) //not a textPane object
		//  endLineNumber = startLineNumber + (drawHere.height / lineHeight);
		//else
		endLineNumber = maxLineNumber;

		int start = (drawHere.y / lineHeight) * lineHeight + lineHeight - startOffset;

		// System.out.println( startLineNumber + " : " + endLineNumber + " : " + start );

		for (int i = startLineNumber; i <= endLineNumber; i++) {
			String lineNumber = String.valueOf(i);
			int width = fontMetrics.stringWidth(lineNumber);
			g.drawString(lineNumber, MARGIN + currentRowWidth - width, start);

			start += lineHeight;
		}

		setPreferredSize(endLineNumber);
	}


	//turn off the line number
	public void off()
	{
		setForeground(DEFAULT_BACKGROUND);
	}

	//turn on the line number
	public void on()
	{
		setForeground(DEFAULT_FOREGROUND);
	}

	public void setBKColor(Color c)
	{
		DEFAULT_BACKGROUND = c;
	}

	public void setFRColor(Color c)
	{
		DEFAULT_FOREGROUND = c;
	}

	//setting  the line number
	public void setEndLineNumber(int n)
	{
		if (maxLineNumber == n) return;
		maxLineNumber = n;
		repaint();
	}


}

