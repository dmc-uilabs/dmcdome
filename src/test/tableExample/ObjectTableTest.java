// ObjectTableTest.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test.tableExample;

import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.treetable.Editors;
import mit.cadlab.dome3.gui.guiutils.treetable.Renderers;
import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;
import mit.cadlab.dome3.swing.table.ObjectTable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ObjectTableTest
{
	static Object[][] testData = new Object[][] {
		new Object[] {new Integer(0), new Double(4.5), new Boolean(false)},
		new Object[] {new String("abc"), new Boolean(true), new Double(2.8)},
	};

	static String[] colNames = new String[] {"col1", "col2", "col3"};

	static JTable createTable() {
		JTable t = new ObjectTable(new JavaDataObjectTableModel(testData,3,colNames));
		t.setDefaultEditor(Object.class,new TextCellEditor());
		t.setDefaultEditor(Boolean.class, new Editors.BooleanComboBoxEditor());
		t.setDefaultRenderer(Boolean.class, new Renderers.BooleanComboBoxRenderer());
		DomeTable.customizeTable(t);
		return t;
	}
	public static void main(String[] args)
	{
		JFrame f = new JFrame("Object Table Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new JScrollPane(createTable()));
		f.pack();
		f.show();
	}
}
