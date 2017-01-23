// FileObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test.treetableexample;

import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.swing.table.AbstractTableObject;
import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.swing.treetable.TreeTableData;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;

/**
 * Example object that implements TreeTableData
 */
public class FileObject implements TreeTableData
{
	public static final String FOLDER_TYPE = "Folder";
	public static final String MODEL_TYPE = "Model";
	public static final String PLAYSPACE_TYPE = "Playspace";
	public static final String PARAMETER_TYPE = "Parameter";

	private String type;
	private String name;
	private List content = new ArrayList();
	private TreeObject treeObj;
	private TableObject tableObj;

	public static FileObject createFolder(String name) {
		return new FileObject(FOLDER_TYPE, name, true);
	}

	public static FileObject createModel(String name) {
		return new FileObject(MODEL_TYPE,name, true);
	}

	public static FileObject createPlayspace(String name) {
		return new FileObject(PLAYSPACE_TYPE,name, true);
	}

	public static FileObject createParameter(String name) {
		return new FileObject(PARAMETER_TYPE,name,false);
	}

	public FileObject(String type, String name, boolean allowsChildren)
	{
		this.type = type;
		this.name = name;
		treeObj = new FileTreeObject(allowsChildren);
		tableObj = new FileTableObject();
	}

	public String getType()
	{
		return type;
	}

	public TreeObject getTreeObject() {
		return treeObj;
	}

	public TableObject getTableObject() {
		return tableObj;
	}

	public void addContent(FileObject f) {
		content.add(f);
	}

	public class FileTreeObject extends DefaultTreeObject {
		public FileTreeObject(boolean allowsChildren) {
			super(FileObject.this,allowsChildren);
		}

		public Icon getIcon(int itemState)
		{
			if (type.equals(MODEL_TYPE))
				return DomeIcons.getIcon(itemState==OPEN_ICON?DomeIcons.MODEL_OPEN:DomeIcons.MODEL);
			else if (type.equals(PLAYSPACE_TYPE))
				return DomeIcons.getIcon(itemState==OPEN_ICON?DomeIcons.PLAYSPACE_OPEN:DomeIcons.PLAYSPACE);
			else if (type.equals(PARAMETER_TYPE))
				return DomeIcons.getIcon(DomeIcons.PARAMETER);
			else // Folder type or unknown type; return default system icons
				return super.getIcon(itemState);
		}

		public String getTreeValue()
		{
			return name;
		}

		public List getChildren()
		{
			return content;
		}
	}

	public class FileTableObject extends AbstractTableObject {
		public FileTableObject()
		{
			super(FileObject.this);
		}

		public Object getValueAt(int column)
		{
			if (column==0)
				return name; // doesn't matter if in treetable, will return tree value
			else if (column==1)
				if (!getTreeObject().allowsChildren())
					return null;
				else return new Integer(content.size());
			return null;
		}
	}

}
