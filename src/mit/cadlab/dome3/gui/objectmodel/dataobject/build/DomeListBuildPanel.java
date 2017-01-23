package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildListObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DataObjectPanel;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.util.DArrayList;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 11, 2003
 * Time: 10:49:30 AM
 * To change this template use Options | File Templates.
 */
public class DomeListBuildPanel extends DataObjectPanel
{
	private static final Dimension preferredSize = new Dimension(400, 300);
	public static final TypeInfo TYPE_INFO = new TypeInfo("ListBuildPanel");
	public static final String XML_TAG = "listbuildpanel";

	protected PropertyChangeListener propertyListener;
	protected DomeListData dataList;

	JTextField itemField;
	JLabel itemLabel;
	JComboBox typeCombo;
	JButton addButton;
	JButton deleteButton;
	JButton constraintsButton;
	List dataTypes;
	DomeTree tree;
	BuildTreeTable treetable;
	//int idNo = 0;

	public DomeListBuildPanel(DomeListData list)
	{
		if (list == null)
			throw new IllegalArgumentException("DomeList gui - null DomeList");

		this.dataList = list;
		propertyListener = getPropertyListener();
		dataList.addPropertyChangeListener(propertyListener);
		layoutPanel();
		setPreferredSize(preferredSize);
	}

	private void layoutPanel()
	{
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		itemField = Templates.makeTextField("");
		itemField.setEditable(false);
		Integer objectNo = new Integer(dataList.getSize());
		itemField.setText(objectNo.toString());
		itemLabel = Templates.makeLabel("items");
		dataTypes = Registry.getDataObjectTypes();
		typeCombo = Templates.makeComboBox(dataTypes.toArray());   //(new Object[] {"real", "list dome datatypes"});
        setItemType_GUI();
		tree = new DomeTree(new BuildListObjectTreeNode(dataList.getValues()), true);
		treetable = new BuildTreeTable(tree, 2, new String[]{"name", "value"}, new int[]{150, 200});
		JScrollPane tablePane = new JScrollPane(treetable);
		tablePane.getViewport().setBackground(Color.WHITE);
		typeCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                dataList.setItemType(dataTypes.get(typeCombo.getSelectedIndex()).toString());
			}
		});
		addButton = Templates.makeButton("add", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (tree.getSelectionCount() == 0) {
					dataList.addItem(dataTypes.get(typeCombo.getSelectedIndex()).toString());
				} else if (tree.getSelectionCount() == 1) {
					TreePath selectedPath;
					selectedPath = tree.getSelectionPath();
					DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
					if (tree.isExpanded(selectedPath)) {
						Parameter dObj = (Parameter) ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
						((DomeListData) dObj.getCurrentDataObject()).addItem(dataTypes.get(typeCombo.getSelectedIndex()).toString());
					} else {
						DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
						int childIndex = parentNode.getIndex(selectedNode);
						if (selectedPath.getPath().length == 2) {
							dataList.addItem(childIndex, dataTypes.get(typeCombo.getSelectedIndex()).toString());
						} else {
							Parameter dObj = (Parameter) ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
							((DomeListData) dObj.getCurrentDataObject()).addItem(childIndex, dataTypes.get(typeCombo.getSelectedIndex()).toString());
						}
					}
				}
				Integer objectNo = new Integer(dataList.getSize());
				itemField.setText(objectNo.toString());
			}
		});
		deleteButton = Templates.makeButton("delete", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selectedRows = tree.getSelectionRows();
				int[] sortedRows = sortArray(selectedRows);
				for (int i = 0; i < sortedRows.length; i++) {
					TreePath selectedPath;
					selectedPath = tree.getPathForRow(selectedRows[i]);
					DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
					DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
					int childIndex = parentNode.getIndex(selectedNode);
					if (selectedPath.getPath().length == 2) {
						dataList.deleteElementAt(childIndex);
					} else {
						Parameter dObj = (Parameter) ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
						((DomeListData) dObj.getCurrentDataObject()).deleteElementAt(childIndex);
					}
				}
				Integer objectNo = new Integer(dataList.getSize());
				itemField.setText(objectNo.toString());

				/*if(tree.getSelectionCount()!=0){
					int[] selected = tree.getSelectionRows();
					dataList.removeAll(selected);
					Integer objectNo = new Integer(dataList.getSize());
					itemField.setText(objectNo.toString());
				} */
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				if (tree.getSelectionCount() == 0)
					deleteButton.setEnabled(false);
				else
					deleteButton.setEnabled(true);
				if (tree.getSelectionCount() > 1)
					addButton.setEnabled(false);
				else
					addButton.setEnabled(true);

			}
		});

		deleteButton.setEnabled(false);
		constraintsButton = Templates.makeButton("constraints...");

		JComponent[] comps = {itemField, itemLabel, typeCombo, tablePane, makeAddDelete(), constraintsButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 30, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
			new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

    protected void setItemType_GUI() {
	    int index = dataTypes.indexOf(dataList.getItemType());
	    if (index!=-1 )	typeCombo.setSelectedIndex(index);
	    else typeCombo.setSelectedIndex(0);
    }

	protected Parameter createParameter(String type)
	{
		DomeModel m = (DomeModel) BuildFocusTracker.getCurrentModel();
		Object[] ctrParams = new Object[]{m, new Id(UUIDGenerator.create())};
		Parameter p = (Parameter) m.getModelObjectFactory().newInstance(type, ctrParams);
		if (p == null)
			throw new RuntimeException("DomeList: new parameter - object is null: " + type);
		return p;
	}

	private JPanel makeAddDelete()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {addButton, deleteButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		DomeInit.initializeDOME();
		JFrame f = new JFrame("List datatype definition panel");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DArrayList objectArray = new DArrayList();
		DomeListData myDomeList = new DomeListData(objectArray);
		f.getContentPane().add(new DomeListBuildPanel(myDomeList));
		f.setSize(f.getPreferredSize());
		f.show();
	}

	public void setDataObject(DataObject data)
	{
		// sangmok: added code to fix memory problem
        // setDataObject(null) is invoked during the executino of releaseDataObjectReferenceOfDataObjectPanel() in DataObjectCards class
        // when DomeMatrixData is null, codes like setDataModel_GUI() should be skipped
        // instead setDataModel_Null() should be invoked
        if (data == null) {
            setDataModel_Null();
            return;
        }
        // sangmok: added code ends

		if (data instanceof DomeListData)
			dataList = (DomeListData) data;
		else
			throw new IllegalArgumentException("DomeList gui - bad parameter");
	}

    /**
     * sangmok : a new method to fix memory leakage problem
     * set data object reference (=dataMatrix) as null
     * also release data object reference in TableModel object
     */
	protected void setDataModel_Null()
	{
		if (dataList != null) {
			dataList.removePropertyChangeListener(propertyListener);
		}
		dataList = null;

        // need codes to call releaseReferenceToDataObject(); of each TableModel
	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new ListPanelPropertyChangeListener();
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return new ListPanelPropertyChangeListener();
	}

	protected class ListPanelPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();

            if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList.SIZE)) {
                Integer objectNo = new Integer(dataList.getSize());
                itemField.setText(objectNo.toString());
            } else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList.LIST)) {

            } else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList.ITEMTYPE)) {
	            setItemType_GUI();
            }
		}
	}

	protected int[] sortArray(int[] arr)
	{
		int[] sortedArray = arr;
		for (int i = 0; i < sortedArray.length; i++) {
			for (int j = i + 1; j < sortedArray.length; j++) {
				if (sortedArray[i] < sortedArray[j]) {
					int exchange = sortedArray[i];
					sortedArray[i] = sortedArray[j];
					sortedArray[j] = exchange;
				}
			}
		}
		return sortedArray;
	}

}
