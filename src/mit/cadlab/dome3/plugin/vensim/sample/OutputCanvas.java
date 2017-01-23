package mit.cadlab.dome3.plugin.vensim.sample;

import mit.cadlab.dome3.plugin.vensim.sample.Graph;

import java.awt.*;
//import vensim.graphics.Graph;

/**
* OutputCanvas class extends canvas to create an area
* for drawing the graph on.
*/
public class OutputCanvas extends Canvas
{
	public float xValues[], yValues[];
	public int xPos, yPos, graphwidth, graphheight;
	public String name;

	public OutputCanvas() {}

	/**
	* Draw method to be called from other classes to draw the graph
	*/
	public void draw( float xAxisValues[], float yAxisValues[], int xPos, int yPos, int graphwidth, int graphheight, String name )
	{
		xValues = xAxisValues;
		yValues = yAxisValues;

		this.xPos = xPos;
		this.yPos = yPos;
		this.graphwidth = graphwidth;
		this.graphheight = graphheight;
		this.name = name;

		repaint();

	}


	/**
	* Paint method implements the drawing, if the array is not null.
	*/
	public void paint( Graphics g )
	{
		System.out.println("paint method xValues: " + xValues );

// CANNOT SEEM TO GET THIS TO WORK.  IF COMMENT IT OUT, ONLY GET ONE ERROR ON STARTUP, AND THINGS DRAW OK LATER.
// IF LEAVE IT IN, THEN GRAPH NEVER DRAWS.
// THIS CODE WORKS FINE IN MY TEST APPLICATONS, EG IN \GRAPH OR \DOUBLE DIRECTORIES.

		if (xValues != null)
		{

			System.out.println("inside of if(xValues != null): " + xValues );

			Graph graph = new Graph(xValues, yValues, xPos, yPos, graphwidth, graphheight, name);

			System.out.println("inside of graph: " + graph );

			graph.setTheColor(Color.blue);
			graph.drawGraph(g);
		}

	}




}