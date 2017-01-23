// DSMPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.dsm;

import mit.cadlab.dome3.swing.Templates;


import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;


/**
 *  rewrite DSMFrame class to put content into JPanel for DOME use
 */
public class DSMPanel extends JScrollPane
{
	protected static GridBagConstraints gbc;
	//data classes
	int[][] originalMatrix;     //Original Matrix
	int[][] result_Matrix;   //Result Reachablity Matrix
	String[] current_list;   //Name of each element
	String[] originalNumbers;
	String[] _originalActivities;
	String[] _sortedList;


	//Main UI panel
	JPanel controlPanel = new JPanel();
	DSMScrollPane scrollpane;
	JViewport viewport;
	DSMZoomPanel zoomPanel;
	JScrollPane Zoom;
	JViewport zoomview;
	JButton sortButton = Templates.makeButton("sort");
	JButton resetButton = Templates.makeButton("reset");
	GridBagLayout gridBagLayout = new GridBagLayout();

	public static DSMPanel createExampleDSMPanel()
	{
		//use example datas
		/*int reach[][] = {{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		                 {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		                 {1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
		                 {1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
		                 {0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		                 {0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		                 {1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0},
		                 {1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
		                 {1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
		                 {0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
		                 {1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0},
		                 {1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0},
		                 {1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0},
		                 {0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0},
		                 {0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1}}; */

        int reach[][] = {{0, 0, 1, 1, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 1},
                            {1, 1, 0, 0, 0, 1, 1},
                            {0, 1, 0, 0, 0, 0, 0},
                            {0, 0, 1, 0, 0, 1, 0},
                            {0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 1, 1, 0}};

		/*String title[] = {"Jacob Wronski", "Charles Dumont", "Elaine Yang", "Qing Cao", "David Wallace", "Jacob Wronski", "Charles Dumont", "Elaine Yang", "Keith Thoresz", "Jacob Wronski", "Jacob Wronski", "Jacob Wronski", "Jacob Wronski", "Charles Dumont", "Elaine Yang"}; */
        String title[] = {"Jacob Wronski", "Charles Dumont", "Elaine Yang", "Qing Cao", "David Wallace","Keith Thoresz","Renu"};
		String[] names = new String[title.length];
		for (int k = 0; k < title.length; k++) {
			names[k] = new Integer(k + 1).toString();
		}
		DSMPanel exampleDSMPanel = new DSMPanel(reach, names, title);
		return exampleDSMPanel;
	}

	public static DSMPanel createEmptyMatrixDSMPanel()
	{
		int[][] nMatrix = new int[][]{};
		String[] headings = new String[]{};
		return new DSMPanel(nMatrix, headings);
	}

	public DSMPanel(int[][] nMatrix, String[] nNames, String[] headings)
	{
		//initialize
		initData(nMatrix, nNames, headings);
		//enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		initGuiComponent();
		configComponent();
		setViewportView(makeGui());
	}

	public DSMPanel(int[][] nMatrix, String[] headings)
	{
		//initialize
		initData(nMatrix, headings);
		//enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		initGuiComponent();
		configComponent();
		setViewportView(makeGui());
	}

	public void initData(int[][] nMatrix, String[] nNames, String[] headings)
	{
		if (nMatrix.length != nNames.length)
			System.out.println("incompatiable!!!" + nMatrix.length + "," + nNames.length);
		int n = nMatrix.length;
		originalMatrix = new int[n][n];
		result_Matrix = new int[n][n];
		originalNumbers = new String[n];
		current_list = new String[n];
		this._originalActivities = new String[n];
		this._sortedList = new String[n];

		this.originalMatrix = nMatrix;
		this.originalNumbers = nNames;
		this._originalActivities = headings;
	}

	public void initData(int[][] nMatrix, String[] headings)
	{
		if (nMatrix.length != headings.length)
			System.out.println("incompatiable!!!" + nMatrix.length + "," + headings.length);
		int n = nMatrix.length;
		String[] ns = new String[n];
		for (int k = 0; k < n; k++) {
			ns[k] = new Integer(k + 1).toString();
		}
		originalMatrix = new int[n][n];
		result_Matrix = new int[n][n];
		originalNumbers = new String[n];
		current_list = new String[n];
		this._originalActivities = new String[n];
		this._sortedList = new String[n];

		this.originalMatrix = nMatrix;
		this.originalNumbers = ns;
		this._originalActivities = headings;

	}

	protected void initGuiComponent()
	{
		try {


			scrollpane = new DSMScrollPane(originalMatrix, originalNumbers, this._originalActivities);
			scrollpane.drawDSM(originalMatrix, originalNumbers, this._originalActivities);
			scrollpane.setToolTipText("Design Structure Matrix");
			scrollpane.setBorder(BorderFactory.createTitledBorder(null, "Design Structure Matrix", 0, 0, Templates.FONT11));

			Zoom = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			zoomPanel = new DSMZoomPanel(scrollpane.getViewport(), 120);
			zoomview = new JViewport();
			zoomview.setView(zoomPanel);
			Zoom.setViewport(zoomview);

			//Initialize main UI frame
			controlPanel.setLayout(gridBagLayout);
			controlPanel.setFont(Templates.FONT11);
			controlPanel.setBorder(BorderFactory.createTitledBorder(null, "Control Panel", 0, 0, Templates.FONT11));
			layoutControlPanel();

			scrollpane.tableView.getSelectionModel().addListSelectionListener(zoomPanel);
			scrollpane.tableView.getColumnModel().getSelectionModel().addListSelectionListener(zoomPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected JPanel makeGui()
	{
		JPanel contentPane = new JPanel();
		contentPane.setFont(Templates.FONT11);
		contentPane.setLayout(gridBagLayout);

		JComponent[] comps = {scrollpane, controlPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// 25 inset
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(contentPane, comps, gbcs);
		return contentPane;
	}

	protected void configComponent()
	{
		sortButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sortButton_actionPerformed(e);
			}
		});
		resetButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				resetButton_actionPerformed(e);
			}
		});

		/*showActiveCheckBox.addActionListener(new java.awt.event.ActionListener()
		     {
			     public void actionPerformed(ActionEvent e)
			     {
				     showActiveCheckBox_actionPerformed(e);
			     }
		     });*/

	}

	protected void layoutControlPanel()
	{

		JComponent[] comps = {Zoom, sortButton, resetButton, /*showActiveCheckBox*/};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// 25 inset
			new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, gbc.NORTH, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),
			//new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(controlPanel, comps, gbcs);
	}

	void sortButton_actionPerformed(ActionEvent e)
	{
		DSMPartition sortMethod = new DSMPartition(originalMatrix);
		sortMethod.Calculate_Reachability_Matrix();
		sortMethod.Matrix_Partition();
		sortMethod.Find_Levels();
		sortMethod.Get_Result();
		this.result_Matrix = sortMethod.result_Matrix;
		for (int i = 0; i < sortMethod.elementorder.length; i++) {
			current_list[i] = originalNumbers[sortMethod.elementorder[i]];
			this._sortedList[i] = this._originalActivities[sortMethod.elementorder[i]];
		}
		scrollpane.drawDSM(result_Matrix, current_list, this._sortedList);
		this.resetZoomPane();
		repaint();
	}

	/**
	 * reset mit.cadlab.dome3.gui.dsm pane using unsorted data model
	 */
	public void resetButton_actionPerformed(ActionEvent e)
	{
		redraw();
	}


	void showActiveCheckBox_actionPerformed(ActionEvent e)
	{

		resetZoomPane();// don't forget to add resetZoomPane() HERE!
	}

	/**
	 *   Must add this to make sure after repaint(), zoompanel still listen to selection change
	 */
	void resetZoomPane()
	{
		zoomPanel.x_selected = -1;
		zoomPanel.y_selected = -1;
		scrollpane.tableView.getSelectionModel().addListSelectionListener(zoomPanel);
		scrollpane.tableView.getColumnModel().getSelectionModel().addListSelectionListener(zoomPanel);
	}

	public void redraw()
	{
		scrollpane.drawDSM(this.originalMatrix, this.originalNumbers, this._originalActivities);
		resetZoomPane();
		repaint();
	}


	public void setData(int[][] nMatrix, String[] activities)
	{
		initData(nMatrix, activities);
		scrollpane.setData(nMatrix, activities);
		resetZoomPane();
		repaint();
	}


	public static void main(String[] args)
	{
		JFrame f = new JFrame("Custom GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(DSMPanel.createExampleDSMPanel());
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{

				System.exit(0);
			}
		});
		f.pack();
		f.show();
	}

}
