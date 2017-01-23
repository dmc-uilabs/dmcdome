// EditPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.gui.guiutils.ModelObjectsComboBoxListModel;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.DomePreferenceData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.DomeObjectSet;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.SetItem;
import mit.cadlab.dome3.swing.DList;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Vector;


/**
 *
 */
public class EditPanel extends JPanel implements ActionListener, ListSelectionListener
{
	static GridBagConstraints gbc;
	protected PropertyChangeListener propertyListener;


	DList dataSetList;
	JButton addSetButton;
	JButton removeSetButton;
	JButton moveSetUpButton;
	JButton moveSetDownButton;

	DList seriesInSetList;
	JButton moveSeriesUpButton;
	JButton moveSeriesDownButton;


	DList seriesAvailableForSetList;
	JButton addSeriesToSetButton;
	JButton removeSeriesFromSetButton;


	//JButton okButton;
	JButton cancelButton;
	JButton doneButton;

	Visualization Vis;
	//**VisualizationBuildPanel visPanel;
	DArrayList avaiableSeries;
	//**Vector sets;
    DomeObjectSet dataSet;
    Vector series;   //keep a copy for 'cancel' action



	public static JDialog makeEditDialog(Visualization objVis, DomeObjectSet objSet)
	{

		JDialog d = Templates.makeTestDialog("Edit Data Panel");
		d.getContentPane().add(new EditPanel(objVis, objSet));
		d.pack();
		return d;
	}

	public EditPanel(Visualization objVis, DomeObjectSet objSet)
	{
		Vis = objVis;
        dataSet = objSet;
        // do not have to clone setitems because setitems will only be added to or deleted from dataSet, and will not be modified,
		// not like the behavior in DataPanel
		series = (Vector) dataSet.getData().clone();
		propertyListener = getPropertyListener();
		Vis.addPropertyChangeListener(propertyListener);
		avaiableSeries = (DArrayList) Vis.getAvailableList();
		//**sets = (Vector) Vis.getSetsList().clone();

		//make gui
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		cancelButton = Templates.makeButton("Cancel");
		cancelButton.addActionListener(this);
		doneButton = Templates.makeButton("Done");
		doneButton.addActionListener(this);

		JComponent[] bComps = {doneButton, cancelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] bGbcs = {
			// new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
		};


		Templates.layoutGridBag(buttonPanel, bComps, bGbcs);

		JLabel seriesInSetLabel = Templates.makeLabel("series in set:");
		seriesInSetList = Templates.makeDList(makeSeriesInSetListModel());
		seriesInSetList.setSelectedIndex(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane seriesInSetPane = new JScrollPane(seriesInSetList);
		moveSeriesUpButton = Templates.makeListArrowButton("up", this);
		moveSeriesDownButton = Templates.makeListArrowButton("down", this);
		moveSeriesUpButton.setEnabled(false);
		moveSeriesDownButton.setEnabled(false);

		JLabel seriesAvailableLabel = Templates.makeLabel("other series available:");
		seriesAvailableForSetList = Templates.makeDList(makeAvailableSeriesListModel());
		seriesAvailableForSetList.setSelectedIndex(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane seriesAvailablePane = new JScrollPane(seriesAvailableForSetList);
		//at initialization, this two buttons should not be disabled since no set is selected
		addSeriesToSetButton = Templates.makeListArrowButton("left", this);
		removeSeriesFromSetButton = Templates.makeListArrowButton("right", this);
		addSeriesToSetButton.setEnabled(false);
		removeSeriesFromSetButton.setEnabled(false);

		JComponent[] comps = {
		                      moveSeriesUpButton, moveSeriesDownButton, seriesInSetLabel, seriesInSetPane,
		                      addSeriesToSetButton, removeSeriesFromSetButton, seriesAvailableLabel, seriesAvailablePane,
		                      buttonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(2, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(2, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 2, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 2, 1, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 2, 0, 0), 0, 0),
			new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(2, 2, 0, 0), 0, 0),
			new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(2, 2, 0, 0), 0, 0),
			new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 2, 0, 5), 0, 0),
			new GridBagConstraints(3, 1, 1, 2, 1, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 2, 0, 5), 0, 0),
			new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 10, 5), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);

		seriesInSetList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e) {
			    if (e.getValueIsAdjusting() == false) {

			        if (seriesInSetList.getSelectedIndex() == -1) {
			        //No selection.
                         moveSeriesUpButton.setEnabled(false);
				         moveSeriesDownButton.setEnabled(false);
				         removeSeriesFromSetButton.setEnabled(false);
			        } else {
 			        //Selection.
                        removeSeriesFromSetButton.setEnabled(true);
				        int[] selectedIndices = seriesInSetList.getSelectedIndices();
                        if (selectedIndices.length > 1) {
	                        moveSeriesUpButton.setEnabled(false);
					        moveSeriesDownButton.setEnabled(false);
                        } else {
	                        if (selectedIndices[0] == 0) moveSeriesUpButton.setEnabled(false);
	                        else moveSeriesUpButton.setEnabled(true);
	                        if (selectedIndices[0] == seriesInSetList.getModel().getSize() - 1) moveSeriesDownButton.setEnabled(false);
	                        else moveSeriesDownButton.setEnabled(true);
                        }
			        }
			    }
			}
		});

		seriesAvailableForSetList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e) {
			    if (e.getValueIsAdjusting() == false) {
				    if (seriesAvailableForSetList.getSelectedIndex() == -1)  addSeriesToSetButton.setEnabled(false);
				    else addSeriesToSetButton.setEnabled(true);
			    }
			}
	});

		if (seriesInSetList.getModel().getSize() != 0) seriesInSetList.setSelectedIndex(0);
		else seriesInSetList.setSelectedIndex(-1);
		if (seriesAvailableForSetList.getModel().getSize() !=0) seriesAvailableForSetList.setSelectedIndex(0);
		else seriesAvailableForSetList.setSelectedIndex(-1);
		//Dimension d = new Dimension(DomeBuildFrame.DEFAULT_SIZE);
		//this.setPreferredSize(d);
        Dimension d = new Dimension(650, 450);
        this.setPreferredSize(d);
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return new EditPanel.EditPanelPropertyChangeListener();
	}

	class EditPanelPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{

		}

	}


	public void actionPerformed(ActionEvent e)
	{
		//if (e.getSource() == okButton) {
		//    okButton_clicked();//save to changes to the dataset list
		//} else if (e.getSource() == cancelButton) {
		//    cancelButton_clicked();
		//} else if (e.getSource() == addSetButton) {
        if (e.getSource() == moveSeriesUpButton) {
			moveSeriesUpButton_clicked();
		} else if (e.getSource() == moveSeriesDownButton) {
			moveSeriesDownButton_clicked();
		} else if (e.getSource() == addSeriesToSetButton) {
			addSeriesToSetButton_clicked();
		} else if (e.getSource() == removeSeriesFromSetButton) {
			removeSeriesFromSetButton_clicked();
		}  else if (e.getSource() == doneButton) {
			doneButton_clicked();
		}  else if (e.getSource() == cancelButton) {
			cancelButton_clicked();
		}


	}


	protected void cancelButton_clicked()
	{
		this.dispose();
	}

	protected void doneButton_clicked()
	{
		dataSet.setData(series);
		this.dispose();
	}

	protected void moveSeriesUpButton_clicked()
	{

		if (seriesInSetList.getSelectedIndex() == -1) {
			debug("pls select a serie");
			return;
		} else if (seriesInSetList.getSelectedIndex() == 0) {
			debug("already top of the list");
			return;
		} else {


			int index = seriesInSetList.getSelectedIndex();
			SetItem obj = (SetItem) series.get(index);

			series.removeElementAt(index);
			series.insertElementAt(obj, index - 1);

			seriesInSetList.setModel(makeSeriesInSetListModel());
			seriesInSetList.setSelectedIndex(index - 1);

		}
	}

	protected void moveSeriesDownButton_clicked()
	{

		if (seriesInSetList.getSelectedIndex() == -1) {
			debug("pls select a serie");
			return;
		} else if (seriesInSetList.getSelectedIndex() == seriesInSetList.getModel().getSize() - 1) {
			debug("already bottom of the list");
			return;
		} else {


			int index = seriesInSetList.getSelectedIndex();
			SetItem obj = (SetItem) series.get(index);

			series.removeElementAt(index);

			if (index + 1 > (series.size() - 1)) {
				series.add(obj);
			} else {
				series.insertElementAt(obj, index + 1);
			}

			seriesInSetList.setModel(makeSeriesInSetListModel());
			seriesInSetList.setSelectedIndex(index + 1);

		}
	}

	protected void addSeriesToSetButton_clicked()
	{
		if (seriesAvailableForSetList.getSelectedIndex() == -1) //nothing selected
		{
			debug("select List pls!");
			return;
		} else {
			//repaint seriesInSetList
			//DomeObject newSerie = (DomeObject) avaiableSeries.get(seriesAvailableForSetList.getSelectedIndex());
			Object[] selectedObjs = seriesAvailableForSetList.getSelectedValues();
			DomeObject[] newSeries = new DomeObject[selectedObjs.length];
			for (int i =0;i<selectedObjs.length ; i++) {
				newSeries[i] = (DomeObject) selectedObjs[i];
			}

			Vector testSeries = (Vector)series.clone();
			for(int i=0; i<newSeries.length; i ++) {
				if(isMatched(testSeries,newSeries[i])) testSeries.add(new SetItem(newSeries[i], newSeries[i].getName(), true));
				else {
					OneButton1Msg.show(this, "warning", "Consistent",
					        "The selected items are not consistent in structure! Please select again.", "ok", new Dimension(180, 80));
					return;
				}
			}

			int[] selectedIndices = new int[newSeries.length];
			for (int i = 0; i < newSeries.length; i ++){
				series.add(new SetItem(newSeries[i], newSeries[i].getName(), true));
				selectedIndices[i] = series.size() -1;
			}

			seriesInSetList.setModel(makeSeriesInSetListModel());
			seriesAvailableForSetList.setModel(makeAvailableSeriesListModel());
			seriesInSetList.setSelectedIndices(selectedIndices);
			if (seriesAvailableForSetList.getModel().getSize() !=0) seriesAvailableForSetList.setSelectedIndex(0);
			else seriesAvailableForSetList.setSelectedIndex(-1);

		}

	}


	protected void removeSeriesFromSetButton_clicked()
	{
		if (seriesInSetList.getSelectedIndex() == -1) //nothing selected
		{
			debug("select List pls!");
			return;
		} else {
			//repaint seriesInSetList
			Object[] selectedObjs = seriesInSetList.getSelectedValues();
			DomeObject[] removeSeries = new DomeObject[selectedObjs.length];
			for (int i =0;i<selectedObjs.length ; i++) {
				removeSeries[i] = (DomeObject) selectedObjs[i];
			}

			int[] selectedIndices = seriesInSetList.getSelectedIndices();
			for (int i = 0; i < selectedIndices.length; i ++){
				series.removeElementAt(selectedIndices[selectedIndices.length-i-1]);
			}

			seriesInSetList.setModel(makeSeriesInSetListModel());
			seriesAvailableForSetList.setModel(makeAvailableSeriesListModel());
			if (seriesInSetList.getModel().getSize() !=0) seriesInSetList.setSelectedIndex(0);
			else seriesInSetList.setSelectedIndex(-1);
			for (int i = 0; i < selectedIndices.length; i ++){
				for (int j = 0; j<seriesAvailableForSetList.getModel().getSize() ; j++){
					DomeObject obj = (DomeObject)seriesAvailableForSetList.getModel().getElementAt(j);
					if (removeSeries[i].equals(obj)) selectedIndices[i] = j;
				}
			}
			seriesAvailableForSetList.setSelectedIndices(selectedIndices);

		}


	}

	public void valueChanged(ListSelectionEvent e)
	{
/**		if (e.getValueIsAdjusting())
			return;


		if (e.getSource() == dataSetList) {

			//set selected make two button workable
			if (!addSeriesToSetButton.isEnabled())
				addSeriesToSetButton.setEnabled(true);
			if (!removeSeriesFromSetButton.isEnabled())
				removeSeriesFromSetButton.setEnabled(true);

			//refresh seriesInSet list
			int index = dataSetList.getSelectedIndex();
			this.seriesInSetList.setModel(makeSeriesInSetListModel());
			this.seriesAvailableForSetList.setModel(makeAvailableSeriesListModel());
		} else if (e.getSource() == seriesInSetList) {
			//do nothing
		} else if (e.getSource() == seriesAvailableForSetList) {
			//do nothing}
		}

 **/
	}


	protected ListModel makeSeriesInSetListModel()
	{

/**		int index = dataSetList.getSelectedIndex();
		if (index == -1) return new ModelObjectsComboBoxListModel();

		DomeObjectSet obs = (DomeObjectSet) sets.get(index);
 **/
		DomeObject[] allItem = new DomeObject[series.size()];
		for (int i = 0; i<series.size() ;i++) allItem[i] = ((SetItem) series.get(i)).data;
		return new ModelObjectsComboBoxListModel(allItem);


	}

	protected ListModel makeAvailableSeriesListModel()
	{
			if (series.size() == 0)
				return new ModelObjectsComboBoxListModel(avaiableSeries.toArray());

			DomeObject firstObj = ((SetItem) series.get(0)).data;

			ArrayList matched = new ArrayList();
    		for (int i = 0; i < avaiableSeries.size(); i++){
			    boolean isExist = false;
                for (int j = 0; j< series.size(); j++){
                    if(((SetItem) series.get(j)).data.equals( avaiableSeries.get(i))) isExist = true;
                }
			    if(isExist == false) {
				    if(isMatched(series,(DomeObject) avaiableSeries.get(i))) matched.add((DomeObject) avaiableSeries.get(i));
			    }
		    }

			return new ModelObjectsComboBoxListModel(matched.toArray());


	}

	public boolean isMatched(Vector vec, DomeObject obj)
	{
		if(vec.size() ==0 ) return true;

		boolean hasVector = false;
	    boolean rowsAndColumns = false;   //**  it can be in either of rows and columns(same with DomeObjectSet.isRowsAndColumns())
        boolean rowsOrColumns = false;
		int rowsSize = 0;
		int columnsSize = 0;

		//** to find out the format of vec
		//** if there is a DomeVector in vec, DomeObjectSet can only be in rows or columns
		for(int i = 0; i<vec.size(); i++) {
			Parameter objInVec = (Parameter)((SetItem)vec.get(i)).data;
			if (objInVec.getDataObjectForType("Vector") != null) {
				hasVector = true;
				rowsOrColumns = ((DomeVectorData) objInVec.getDataObjectForType("Vector")).isRowVector();
				if(rowsOrColumns == true) columnsSize = ((DomeVectorData) objInVec.getDataObjectForType("Vector")).getSize();
				else rowsSize = ((DomeVectorData) objInVec.getDataObjectForType("Vector")).getSize();
			}
		}

		//** no DomeVector in vec(all are DomeMatrix)
        if(hasVector == false) {
            boolean rows = true;
	        boolean columns = true;
	        Parameter firstObj = (Parameter)((SetItem)vec.get(0)).data;
	        rowsSize = ((DomeMatrixData)firstObj.getDataObjectForType("Matrix")).getRowCount();
	        columnsSize = ((DomeMatrixData)firstObj.getDataObjectForType("Matrix")).getColumnCount();
	        for(int i = 1; i<vec.size(); i++) {
		        Parameter otherObj = (Parameter)((SetItem)vec.get(i)).data;
		        if(((DomeMatrixData)otherObj.getDataObjectForType("Matrix")).getRowCount() != rowsSize) columns = false;
		        if(((DomeMatrixData)otherObj.getDataObjectForType("Matrix")).getColumnCount() != columnsSize) rows = false;
	        }

	        if(rows == true && columns == true) rowsAndColumns = true;
	        if(rows == true && columns == false) rowsOrColumns = true;
	        if(rows == false && columns == true) rowsOrColumns = false;
	        if(rows == false && columns == false) return false;  //** should be no such a case
        }

        //** to see if obj matches with vec
        if(hasVector == true || (hasVector == false && rowsAndColumns == false)) {   //** DomeObjectSet can only be in rows or columns
	        if (((Parameter)obj).getDataObjectForType("Vector") != null) {
		        DomeVectorData data = ((DomeVectorData) ((Parameter)obj).getDataObjectForType("Vector"));
		        if(data.isRowVector() == rowsOrColumns && rowsOrColumns == true && data.getSize() == columnsSize) return true;
		        if(data.isRowVector() == rowsOrColumns && rowsOrColumns == false && data.getSize() == rowsSize) return true;

		        return false;
	        } else {
		        DomeMatrixData data = (DomeMatrixData) ((Parameter) obj).getDataObjectForType("Matrix");
                if(rowsOrColumns == true && data.getColumnCount() == columnsSize) return true;
		        if(rowsOrColumns == false && data.getRowCount() == rowsSize) return true;

		        return false;
	        }
        } else if(hasVector == false && rowsAndColumns == true) {  //** DomeObjectSet can be in either of rows and columns
	        if (((Parameter)obj).getDataObjectForType("Vector") != null) {
		        DomeVectorData data = ((DomeVectorData) ((Parameter)obj).getDataObjectForType("Vector"));
		        if(data.isRowVector() == true && data.getSize() == columnsSize) return true;
		        if(data.isRowVector() == false && data.getSize() == rowsSize) return true;

		        return false;
	        } else {
		        DomeMatrixData data = (DomeMatrixData) ((Parameter) obj).getDataObjectForType("Matrix");
		        if(data.getColumnCount() == columnsSize) return true;
		        if(data.getRowCount() == rowsSize) return true;

		        return false;
	        }

        }

		return false;
	}

	private void dispose()
	{
		SwingUtilities.windowForComponent(this).dispose();
	}

	private void debug(String msg)
	{
		boolean debug = true;
		if (debug)
			System.out.println("EditDialog: " + msg);
	}


	public void repaintToSetChange(String changeoption)
	{
		if (changeoption.equals(DomeObjectSet.SELECTIONCHANGED)) {
			//do nothing

		} else if (changeoption.equals(DomeObjectSet.SERIESNAMECHANGED)) {
			//do nothing
		} else if (changeoption.equals(DomeObjectSet.SETCHANGED)) {
			seriesInSetList.setModel(makeSeriesInSetListModel());
		}


	}

}
