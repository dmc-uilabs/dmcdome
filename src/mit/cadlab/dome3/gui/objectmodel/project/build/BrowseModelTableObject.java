package mit.cadlab.dome3.gui.objectmodel.project.build;

import mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Jun 19, 2003
 * Time: 10:37:02 AM
 * To change this template use Options | File Templates.
 */
public class BrowseModelTableObject extends FileSystemObjectTableObject
{
		protected DomeFile domeFile;

		public BrowseModelTableObject(DomeFile domeFile)
		{
			super(domeFile);
			this.domeFile = domeFile;
		}

		public Object getValueAt(int column)
		{
            if(column == 0)
	            return fObj.getName();
			else
	            return "";
		}
}
