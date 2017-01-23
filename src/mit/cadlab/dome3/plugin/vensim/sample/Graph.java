package mit.cadlab.dome3.plugin.vensim.sample;

import java.awt.*;
import java.text.DecimalFormat;    //used for number formatting

/**
* Creates a line graph of x,y points with x and y axis drawn
* Need to create a scale mechanism.  Pass in arguments for size
* of graph, then use this size minus pixeloffset to divide by the
* values of x and y to create the graph.
*
* NEEDS A PAIR OF ARRAYS THAT ARE MATCHED IN LENGTH!
*/
public class Graph
{
	private float xValues[], yValues[],  minXValue, minYValue, maxXValue, maxYValue, xLength, yLength;
	private int xPos, yPos, width, height, minXLabel, maxXLabel;
	private Color color;
	private String name;

	/**
	* Need to pass in values to draw axes.  Need to pass in values to draw graph line.
	* Do this either in the constructor or in set methods.
	* First constructor is empty.
	*/
	public Graph() { }

	/**
	* Constructor: create new xValues[] and yValues[] arrays and populate them, may be dangerous
	* because of the setXValues() and setYValues() below
	*/
	public Graph( float xAxisValues[], float yAxisValues[], int xPos, int yPos, int width, int height, String name )
	{
		xValues = new float[xAxisValues.length];
		for (int i = 0; i < xAxisValues.length; i++)
		{
			xValues[i] = xAxisValues[i];
		}

		yValues = new float[yAxisValues.length];
		for (int i = 0; i < yAxisValues.length; i++)
		{
			yValues[i] = yAxisValues[i];
		}

		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.name = name;

	/**
	* Find min and max points on the axes
	*/
		// Set the min and max values to first value in array
		minXValue = xValues[0];
		maxXValue = xValues[0];
		minYValue = yValues[0];
		maxYValue = yValues[0];

		// Look for smaller or larger values
		try
		{
			for (int i = 0; i < xValues.length; i++)
			{
				if (xValues[i] < minXValue)
				{
					minXValue = xValues[i];
				}
				if (xValues[i] > maxXValue)
				{
					maxXValue = xValues[i];
				}
			}
			for (int i = 0; i < yValues.length; i++)
			{
				if (yValues[i] < minYValue)
				{
					minYValue = yValues[i];
				}
				if (yValues[i] > maxYValue)
				{
					maxYValue = yValues[i];
				}
			}
		}
		catch( ArrayIndexOutOfBoundsException ae )
		{
			System.out.println( "Array out of bounds setting max and min values" );
		}

	}

	/**
	* create new xValues[] and populate them, may be dangerous
	* because of the constructor above

	public void setXValues( float xAxisValues[] )
	{
		xValues = new float[xAxisValues.length];
		for (int i = 0; i < xAxisValues.length; i++)
		{
			xValues[i] = xAxisValues[i];
		}
	}

	/**
	* create new yValues[] and populate them, may be dangerous
	* because of the constructor above

	public void setYValues( float yAxisValues[] )
	{
		yValues = new float[yAxisValues.length];
		for (int i = 0; i < yAxisValues.length; i++)
		{
			yValues[i] = yAxisValues[i];
		}
	}

	*/


	/**
	* set the color of the line
	*/
	public void setTheColor (Color c)
	{
		this.color = c;
	}




	/**
	* Draw a graph.
	*/
	public void drawGraph( Graphics g )
	{
		// Set the graph line color
		g.setColor(color);

		// Determine the length of x and y, and the scaling factors
		xLength = maxXValue - minXValue;
		yLength = maxYValue - minYValue;
		float xScaler = (float) width/xLength;
		float yScaler = (float) height/yLength;

		// Draw the graph line based on pixeloffset
		try
		{
			//need to prevent array out of bounds at array end[x+1]. subtract one in the for statement below
			for (int x = 0; x < xValues.length - 1; x++)
			{
				g.drawLine( xPos + (int) ((xValues[x] - minXValue) * xScaler), yPos + height - (int) ((yValues[x] - minYValue) * yScaler), xPos + (int) ((xValues[x+1] - minXValue) * xScaler), yPos + height - (int) ((yValues[x+1] - minYValue) * yScaler) );
			}
		}
		catch( ArrayIndexOutOfBoundsException ae )
		{
			System.out.println( "Array out of bounds drawing the graph line" );
		}

		// Set the axes color to black
		g.setColor(Color.black);
		// Draw the graph axes based on pixeloffset
		g.drawLine( xPos, yPos + height, xPos + width, yPos + height ) ;	// X axis
		g.drawLine( xPos, yPos, xPos, yPos + height);						// Y axis

		// Draw the min and max values on the axes
		g.drawString( Float.toString(minXValue), xPos, yPos + height + 15);
		g.drawString( Float.toString(maxXValue), xPos + width - 10, yPos + height + 15);
		g.drawString( Float.toString(minYValue), xPos - 90, yPos + height);
		g.drawString( Float.toString(maxYValue), xPos - 90, yPos + 10);

		g.drawString( name, xPos + 20, yPos - 10 );


	/**

	THIS IS MAKING ONLY DECIMAL NUMBERS, NOT SCI NOTATION.

		minXLabel = (int) minXValue;
		maxXLabel = (int) maxXValue;

        DecimalFormat df = new DecimalFormat("00");

//		df.setDecimalSeparatorAlwaysShown(false);


		g.drawString( df.format(minXLabel), xPos, yPos + height + 15);
		g.drawString( df.format(maxXLabel), xPos + width - 10, yPos + height + 15);
		g.drawString( df.format(minYValue), xPos - 40, yPos + height);
		g.drawString( df.format(maxYValue), xPos - 40, yPos + 10);

	*/


	}




} //Graph.java