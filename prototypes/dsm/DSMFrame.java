package dsm;

import mit.cadlab.dome.swing.Templates;

import java.awt.event.*;
import java.awt.GridBagLayout;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import java.util.List;
import java.util.ArrayList;



/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class DSMFrame extends JFrame
{

	int[][] originalMatrix;     //Original Matrix
	int[][] result_Matrix;   //Result Reachablity Matrix
	String[] current_list;   //Name of each element
	String[] originalNumbers;
	String[] _originalActivities;
	String[] _sortedList;


    //Main UI panel
    JPanel contentPane;
	JPanel controlPanel = new JPanel();
	DSMScrollPane scrollpane;
	JViewport viewport;
	DSMZoomPanel zoomPanel;
	JScrollPane Zoom;
	JViewport zoomview;
	JButton sortButton = Templates.makeButton("Sort");
	JButton resetButton = Templates.makeButton("Reset");
	JCheckBox showActiveCheckBox = Templates.makeCheckBox(" Show Active");
	JLabel loadDataLabel = new JLabel();
	GridBagLayout gridBagLayout = new GridBagLayout();

  public DSMFrame(int[][] nMatrix, String[] nNames, String[] headings)
 {
	 //initialize

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

	 enableEvents(AWTEvent.WINDOW_EVENT_MASK);

	 try
	 {
		 //Initialize main UI frame
		 this.setFont(Templates.FONT11);
		 this.setSize(600, 600);
		 this.setTitle("Design System Matrix");

		 contentPane = (JPanel) this.getContentPane();
		 contentPane.setLayout(gridBagLayout);

		 controlPanel.setLayout(null); controlPanel.setFont(Templates.FONT11);
		 controlPanel.setBorder(BorderFactory.createTitledBorder(null,"Control Panel",0,0,Templates.FONT11));

		 scrollpane = new DSMScrollPane(originalMatrix, originalNumbers, this._originalActivities);
		 scrollpane.drawDSM(result_Matrix, current_list, this._sortedList);
		 scrollpane.setToolTipText("Design Structure Matrix");
		 scrollpane.setBorder(BorderFactory.createTitledBorder(null,"Design Structure Matrix",0,0,Templates.FONT11));

		 Zoom = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		 Zoom.setPreferredSize(new Dimension(120, 120));

		 Zoom.setBounds(15, 25, 120, 120);
		 zoomPanel = new DSMZoomPanel(scrollpane.getViewport(), 120);
		 zoomview = new JViewport();
		 zoomview.setView(zoomPanel);
		 Zoom.setViewport(zoomview);

		 //add listener here: to fire the table selection to zoom panel
		 scrollpane.tableView.getSelectionModel().addListSelectionListener(zoomPanel);
		 scrollpane.tableView.getColumnModel().getSelectionModel().addListSelectionListener(zoomPanel);
		 sortButton.setBounds(10, 152, 60, 28);
		 sortButton.addActionListener(new java.awt.event.ActionListener()
		 {
			 public void actionPerformed(ActionEvent e)
			 {
				 sortButton_actionPerformed(e);
			 }
		 });
		 resetButton.setBounds(70, 152, 70, 28);
		 resetButton.addActionListener(new java.awt.event.ActionListener()
		 {
			 public void actionPerformed(ActionEvent e)
			 {
				 resetButton_actionPerformed(e);
			 }
		 });

		 showActiveCheckBox.setBounds(new Rectangle(10, 189, 120, 30));
		 showActiveCheckBox.addActionListener(new java.awt.event.ActionListener()
		 {
			 public void actionPerformed(ActionEvent e)
			 {
				 showActiveCheckBox_actionPerformed(e);
			 }
		 });


		 controlPanel.add(Zoom, null);
		 controlPanel.add(sortButton, null);
		 controlPanel.add(showActiveCheckBox, null);
		 controlPanel.add(resetButton, null);

		 contentPane.add(scrollpane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
			    GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(12, 12, 21, 0), 350, 337));

		 contentPane.add(controlPanel, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0,
		        GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(12, 12, 0, 12), 150, 337));
		 this.resetZoomPane();
		 contentPane.repaint();
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
	 }
 }
	//Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			System.exit(0);
		}
	}
  
 void sortButton_actionPerformed(ActionEvent e)
 {
	 DSMPartition sortMethod = new DSMPartition(originalMatrix);
	 sortMethod.Calculate_Reachability_Matrix();
	 sortMethod.Matrix_Partition();
	 sortMethod.Find_Levels();
	 sortMethod.Get_Result();
	 this.result_Matrix = sortMethod.result_Matrix;
	 for (int i = 0; i < sortMethod.elementorder.length; i++)
	 {
		 current_list[i] = originalNumbers[sortMethod.elementorder[i]];
	 	 this._sortedList[i] = this._originalActivities[sortMethod.elementorder[i]];
	 }
    scrollpane.drawDSM(result_Matrix,current_list, this._sortedList);
    this.resetZoomPane();
    contentPane.repaint();
 }

/**
  * reset dsm pane using unsorted data model
  */
public void resetButton_actionPerformed(ActionEvent e)
{
	this.redraw();
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
		this.resetZoomPane();
		contentPane.repaint();
	}
}