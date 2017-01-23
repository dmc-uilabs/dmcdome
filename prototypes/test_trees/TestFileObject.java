package test_trees;

import mit.cadlab.dome.objectmodel.dataobject.FileData;
import mit.cadlab.dome.objectmodel.dataobject.DomeFile;
import mit.cadlab.dome.objectmodel.util.TypeInfo;
import mit.cadlab.dome.objectmodel.Parameter;
import mit.cadlab.dome.swing.tree.TreeObject;
import mit.cadlab.dome.swing.tree.DefaultTreeObject;
import mit.cadlab.dome.swing.table.TableObject;
import mit.cadlab.dome.swing.table.AbstractTableObject;
import mit.cadlab.dome.swing.treetable.TreeTableData;
import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.icons.DomeIcons;
import mit.cadlab.dome.gui.objectmodel.model.dome.DomeModelBuildPanel;
import mit.cadlab.dome.gui.mode.build.BuildMode;
import org.dom4j.Element;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.DefaultCellEditor;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 25, 2003
 * Time: 6:13:54 PM
 * To change this template use Options | File Templates.
 */
public class TestFileObject extends FileData implements TreeTableData
{

	private TreeObject treeObj;
	private TableObject tableObj;
	private FileEditor editor;

	public TestFileObject()
	{
		treeObj = new FileTreeObject();
		tableObj = new FileTableObject();
		editor = new FileEditor(getDomeFile());

	}

	public TestFileObject(String v)
	{
		super(v);
		treeObj = new FileTreeObject();
		tableObj = new FileTableObject();
		editor = new FileEditor(getDomeFile());
	}

	public TreeObject getTreeObject()
	{
		return treeObj;
	}

	public TableObject getTableObject()
	{
		return tableObj;
	}


	public class FileTreeObject extends DefaultTreeObject
	{
		public FileTreeObject()
		{
			super(TestFileObject.this, false);
		}

		public Icon getIcon(int itemState)
		{
			return null;
		}

		public String getTreeValue()
		{
			return getFilePath().substring((getFilePath().lastIndexOf('\\')) + 1);
		}

		public List getChildren()
		{
			return Collections.EMPTY_LIST;
		}
	}

	public class FileTableObject extends AbstractTableObject
	{

		private final String slash = System.getProperty("file.separator");

		public FileTableObject()
		{
			super(TestFileObject.this);
			TestFileObject.this.addPropertyChangeListener(DomeFile.FILEPATH, dataValueListener);
		}

		protected PropertyChangeListener dataValueListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String property = e.getPropertyName();

				if (property.equals(DomeFile.FILEPATH))
					fireTableObjectChanged();


			}
		};


		public Object getValueAt(int column)
		{
			if (column == 1)
				return getFilePath();
			else
				return "";
		}

		// TableObject interface
		public boolean isEditableAt(int column)
		{
			if (column == 1)
				return true;
			else
				return false;
		}

		public void setValueAt(Object value, int column)
		{

			if (column == 1) {
				setFilePath((String) value);
				fireTableObjectChanged();
			}
			else
				return;

		}

		public TableCellEditor getEditorAt(int column)

		{
			if (column == 1)
				return editor;
			else
				return null;
		}
	}

	public static class FileEditor extends DefaultCellEditor
	{
		DomeFile file;

		/**
		 * Store the object that the editor is associated with.
		 */
		public FileEditor(DomeFile file)
		{
			super(Templates.makeDTextField());
			this.file = file;
		}

		/**
		 * Override of the javax.swing.DefaultCellEditor method. Used to capture
		 * and translate the mouse event for this editor.
		 * @param e Editor event.
		 * @return false indicating that the cell is not editable
		 */
		public boolean isCellEditable(EventObject e)
		{
			if (e instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) e;
				if (SwingUtilities.isLeftMouseButton(me) && me.getClickCount() == 1) {

					JFileChooser openFile = new JFileChooser(file.getFilePath());
					try {

						File f = new File(new File((new File(file.getFilePath())).getName()).getCanonicalPath());
						openFile.setSelectedFile(f);
					}
					catch (IOException g) {
						g.printStackTrace();
					}

					if (openFile.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
						return false;

					file.setFilePath(openFile.getSelectedFile().getAbsolutePath());
				}
			}
			return false;
		}
	}


}


