// FileSystemFilters.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.swing.tree.AbstractFilterTreeSelectionModel;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFile;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;

import javax.swing.tree.TreePath;

/**
 * Filters for file system browsers. Filters possible selections.
 */
public class FileSystemFilters
{

	/**
	 * Allows only folders to be selected.
	 */
	public static class FoldersFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof Folder) {
				return ((Folder) obj).getFolderType().equals(Folder.FOLDER);
			}
			return false;
		}
	}

	/**
	 * Allows only interfaces to be selected.
	 */
	public static class InterfacesFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof DomeFile) {
				return ((DomeFile) obj).getFileType().equals(DomeFile.INTERFACE_TYPE);
			}
			return false;
		}
	}

	/**
	 * Allows only interfaces and projects to be selected.
	 */
	public static class InterfacesAndProjectsAndAnalysisToolsFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof DomeFile) {
				 String fileType = ((DomeFile) obj).getFileType();
                //Qing : change Apr 17,04... in run browser, not allow to pick project interface among all project subcomponets
                 boolean isIModelInterface=false;
                 boolean isResourceInterface=false;
                 if(fileType.equals(DomeFile.INTERFACE_TYPE)){
                     Object parent_obj = ((AbstractTreeObjectFactoryTreeNode) path.getParentPath().getParentPath().getLastPathComponent()).getTreeNodeObject();
                     if(parent_obj instanceof BrowseModelFolder){
                         String parent_fileType = ((BrowseModelFolder) parent_obj).getName();
                         if(parent_fileType.equals("iModels"))
                                isIModelInterface=true;
                          if(parent_fileType.equals("Resources"))
                                isResourceInterface=true;
                     }
                  }
                 if(isIModelInterface||isResourceInterface)
                     return (fileType.equals(DomeFile.PROJECT_TYPE) || fileType.equals(DomeFile.ANALYSIS_TOOL_TYPE));
                 else
			         return (fileType.equals(DomeFile.INTERFACE_TYPE) || fileType.equals(DomeFile.PROJECT_TYPE) || fileType.equals(DomeFile.ANALYSIS_TOOL_TYPE));
			}
			return false;
		}
	}

	/**
	 * Allows only playspace to be selected.
	 */
	public static class PlayspacesFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof DomeFile) {
				return ((DomeFile) obj).getFileType().equals(DomeFile.PLAYSPACE_TYPE);
			}
			return false;
		}
	}

	/**
	 * Allows only models/projects to be selected.
	 */
	public static class ModelsProjectsFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof DomeFile) {
				String fileType = ((DomeFile) obj).getFileType();
				return fileType.equals(DomeFile.MODEL_TYPE) || fileType.equals(DomeFile.PROJECT_TYPE);
			}
			return false;
		}
	}

    /**
	 * Allows only models/projects/tools to be selected.
	 */
	public static class ModelsProjectsToolsFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof DomeFile) {
				String fileType = ((DomeFile) obj).getFileType();
				return fileType.equals(DomeFile.MODEL_TYPE) || fileType.equals(DomeFile.PROJECT_TYPE)||fileType.equals(DomeFile.ANALYSIS_TOOL_TYPE);
			}
			return false;
		}
	}

	/**
	 * Allows only projects to be selected.
	 */
	public static class ProjectsFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof DomeFile) {
				String fileType = ((DomeFile) obj).getFileType();
				return fileType.equals(DomeFile.PROJECT_TYPE);
			}
			return false;
		}
	}

    /**
     * Allows only projects to be selected.
     */
    public static class ModelsFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
    {
        protected boolean isValidSelectionPath(TreePath path)
        {
            Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
            if (obj instanceof DomeFile) {
                String fileType = ((DomeFile) obj).getFileType();
                return fileType.equals(DomeFile.MODEL_TYPE);
            }
            return false;
        }
    }

    /**
     * allows only analysis tools to be selected.
     */
    public static class AnalysisToolFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
    {
        protected boolean isValidSelectionPath(TreePath path)
        {
            Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
            if(obj instanceof DomeFile)
            {
                String fileType = ((DomeFile)obj).getFileType();
                return fileType.equals(DomeFile.ANALYSIS_TOOL_TYPE);
            }
            return false;
        }
    }

	/**
	 * Allows only projects/interfaces to be selected.
	 */
	public static class PlayspacesInterfacesFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof DomeFile) {
				String fileType = ((DomeFile) obj).getFileType();
				//return fileType.equals(DomeFile.PLAYSPACE_TYPE) || fileType.equals(DomeFile.INTERFACE_TYPE);
                return fileType.equals(DomeFile.PLAYSPACE_TYPE);//Qing change here March 15, in playspace treeview, not allow interface to be selected, to eliminate confusion
			}
			return false;
		}
	}

	/**
	 * Allows only specified path to be selected.
	 */
	public static class FixedSelectionTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected TreePath fixedPath;

		public FixedSelectionTreeSelectionModel(TreePath fixedPath)
		{
			if (fixedPath == null)
				throw new IllegalArgumentException("FixedSelectionTreeSelectionModel: null fixedPath");
			this.fixedPath = fixedPath;
		}

		protected boolean isValidSelectionPath(TreePath path)
		{
			return (fixedPath.equals(path));
		}
	}

	public static class FoldersAndModelFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof Folder) {
				return ((Folder) obj).getFolderType().equals(Folder.FOLDER);
			} else if (obj instanceof DomeFile) {
				String fileType = ((DomeFile) obj).getFileType();
				return fileType.equals(DomeFile.MODEL_TYPE) ||
                            fileType.equals(DomeFile.PROJECT_TYPE) ||
                                fileType.equals(DomeFile.ANALYSIS_TOOL_TYPE);
			}
			return false;
		}
	}

	public static class MultipleFoldersAndModelFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof Folder) {
				return ((Folder) obj).getFolderType().equals(Folder.FOLDER);
			} else if (obj instanceof DomeFile) {
				String fileType = ((DomeFile) obj).getFileType();
				return fileType.equals(DomeFile.MODEL_TYPE) || fileType.equals(DomeFile.PROJECT_TYPE);
			}
			return false;
		}

		public void setSelectionMode(int mode) {
			this.selectionMode = mode;
		}
	}

	public static class FoldersAndPlayspaceFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
			if (obj instanceof Folder) {
				return ((Folder) obj).getFolderType().equals(Folder.FOLDER);
			} else if (obj instanceof DomeFile) {
				return ((DomeFile) obj).getFileType().equals(DomeFile.PLAYSPACE_TYPE);
			}
			return false;
		}
	}

}
