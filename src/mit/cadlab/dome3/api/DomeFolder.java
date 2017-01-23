package mit.cadlab.dome3.api;

import mit.cadlab.dome3.network.client.functions.BrowseFileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * User: Sangmok Han
 * Date: 2005. 1. 18.
 */
public class DomeFolder {

    private String folderName;
    private int folderId;
    private DomeConnection domeConn;

    public DomeFolder(String folderName, int folderId, DomeConnection domeConn) {
        this.folderName = folderName;
        this.folderId = folderId;
        this.domeConn = domeConn;
    }

    public String getFolderName() {
        return folderName;
    }

    public int getFolderId() {
        return folderId;
    }

    /**
     * retrieve the list of sub-folders in this folder
     * @return the list of sub-folders in this folder
     */
    public List getFolders() {
        /* retrieve folder & model list */
        Vector v = BrowseFileSystemFunctions.getModelFolderContents(domeConn.getServerConnection(), folderId);
        List folderList = (Vector) v.get(0);

        /* create the list of folder instance */
        List ret = new ArrayList();
        for (int i = 0; i < folderList.size(); i++)
        {
            Integer idFolder = (Integer) ((Vector) folderList.get(i)).get(0);
            String curFolderName = (String) ((Vector) folderList.get(i)).get(1);
            ret.add(new DomeFolder(curFolderName, idFolder.intValue(), domeConn));
        }
        return ret;
    }

    /**
     * retrieve list of models in this folder
     * @return list of models in this folder
     */
    public List getModels() {
        List ret = new ArrayList();

        /* retrieve folder & model list */
        Vector v = BrowseFileSystemFunctions.getModelFolderContents(domeConn.getServerConnection(), folderId);
        List modelList = (Vector) v.get(1);

        for (int i = 0; i < modelList.size(); i++) {
            Vector modelInfo = (Vector) modelList.get(i);
            String modelId = (String) modelInfo.get(0);
            String modelName = (String) modelInfo.get(1);
            String modelDesc = (String) modelInfo.get(2);
            String modelType = (String) modelInfo.get(3);

            Vector modifiedVec = FileSystemFunctions.getModelLastModified(domeConn.getServerConnection(), modelId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            // Here new DomeModel is not a resource of a project, so isProjectResourse is set to false.
            ret.add(new DomeModel(modelId, modelName, modelDesc, modelType, lastModified, version, false, null, domeConn));
        }
        return ret;
    }

    /**
     * retrieve list of projects in this folder
     * @return list of models in this folder
     */
    public List getProjects() {
        List ret = new ArrayList();
        Vector v = FileSystemFunctions.getProjectFolderContents(domeConn.getServerConnection(), folderId);

        for (int i = 0; i < v.size(); i++)
        {
            Vector projectInfo = (Vector) v.get(i);
            String projectId = (String) projectInfo.get(0);
            String projectName = (String) projectInfo.get(1);
            String description = (String) projectInfo.get(2);
            Vector modifiedVec = FileSystemFunctions.getProjectLastModified(domeConn.getServerConnection(), projectId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            DomeProject domeProject = new DomeProject(projectId, projectName, description, lastModified, version, false, null, domeConn);


            ret.add(domeProject);
        }

        return ret;
    }

    /**
     * retrieve list of folders and playspaces in this folder
     * @return list of folders and playspaces in this folder
     */
    public List getPlayspaces() {
        List ret = new ArrayList();

        /* retrieve folder & model list */
		Vector v = BrowseFileSystemFunctions.getPlayspaceFolderContents(domeConn.getServerConnection(), folderId);
		Vector playspaceVec = (Vector) v.get(1);

		for (int i = 0; i < playspaceVec.size(); i++) {
			Vector psInfo = (Vector) playspaceVec.get(i);
			String playspaceId = (String) psInfo.get(0);
			String playspaceName = (String) psInfo.get(1);
			String description = (String) psInfo.get(2);
			Vector modifiedVec = FileSystemFunctions.getPlayspaceLastModified(domeConn.getServerConnection(), playspaceId);
			Date lastModified = (Date) modifiedVec.get(0);
			int version = ((Integer) modifiedVec.get(1)).intValue();

            ret.add(new DomePlayspace(playspaceId, playspaceName, description, lastModified, version, domeConn));
		}

        return ret;
    }

    /**
     * retrieve playspace with given name in this folder
     * returns null if there is no match.
     */
    public DomePlayspace getPlayspace(String findingPlayspaceName) {
        /* retrieve folder & model list */
		Vector v = BrowseFileSystemFunctions.getPlayspaceFolderContents(domeConn.getServerConnection(), folderId);
		Vector playspaceVec = (Vector) v.get(1);

		for (int i = 0; i < playspaceVec.size(); i++) {
			Vector psInfo = (Vector) playspaceVec.get(i);
			String playspaceId = (String) psInfo.get(0);
			String playspaceName = (String) psInfo.get(1);
			String description = (String) psInfo.get(2);
			Vector modifiedVec = FileSystemFunctions.getPlayspaceLastModified(domeConn.getServerConnection(), playspaceId);
			Date lastModified = (Date) modifiedVec.get(0);
			int version = ((Integer) modifiedVec.get(1)).intValue();

            if (findingPlayspaceName.equals(playspaceName)) {
                return new DomePlayspace(playspaceId, playspaceName, description, lastModified, version, domeConn);
            }
		}

        return null;
    }

    /**
     * find the DomePlayspace with given name and returns a RuntimePlayspace instance of it.
     * RuntimePlayspace is instantiated by invoking createRuntimePlayspace() method of the DomePlayspace.
     * returns null if there is no match.
     */
    public RuntimePlayspace joinPlayspace(String findingPlayspaceName) {
        DomePlayspace foundPlayspace = getPlayspace(findingPlayspaceName);
        if (foundPlayspace != null) {
            return foundPlayspace.createRuntimePlayspace();
        }
        return null;
    }

    /**
     * retrieve the DomeFolder instance for a sub-folder with the given name. returns null when there is no match.
     */
    public DomeFolder getFolder(String folderName) {
        /* retrieve folder & model list */
        Vector v = BrowseFileSystemFunctions.getModelFolderContents(domeConn.getServerConnection(), folderId);
        List folderList = (Vector) v.get(0);

        /* find a folder with given name, and then create a folder instance for that folder */
        for (int i = 0; i < folderList.size(); i++)
        {
            Integer idFolder = (Integer) ((Vector) folderList.get(i)).get(0);
            String curFolderName = (String) ((Vector) folderList.get(i)).get(1);
            if (folderName.equals(curFolderName)) {
                return new DomeFolder(curFolderName, idFolder.intValue(), domeConn);
            }
        }
        return null;
    }

    /**
     * retrieve the DomeFolder instance for a sub-folder with the given folder id. returns null when there is no match.
     */
    public DomeFolder getFolderById(int findingFolderId) {
        /* retrieve folder & model list */
        Vector v = BrowseFileSystemFunctions.getModelFolderContents(domeConn.getServerConnection(), folderId);
        List folderList = (Vector) v.get(0);

        /* find a folder with given name, and then create a folder instance for that folder */
        for (int i = 0; i < folderList.size(); i++)
        {
            Integer idFolder = (Integer) ((Vector) folderList.get(i)).get(0);
            String curFolderName = (String) ((Vector) folderList.get(i)).get(1);
            if (findingFolderId == idFolder.intValue()) {
                return new DomeFolder(curFolderName, idFolder.intValue(), domeConn);
            }
        }
        return null;
    }


    /**
     * retrieve the DomeModel instance with the given name. returns null when there is no match.
     */
    public DomeModel getModelByName(String findingModelName) {
        /* retrieve folder & model list */
        Vector v = BrowseFileSystemFunctions.getModelFolderContents(domeConn.getServerConnection(), folderId);
        List modelList = (Vector) v.get(1);

        /* find a model with given name, and then create a dome model instance */
        for (int i = 0; i < modelList.size(); i++) {
            Vector modelInfo = (Vector) modelList.get(i);
            String modelId = (String) modelInfo.get(0);
            String modelName = (String) modelInfo.get(1);
            String modelDesc = (String) modelInfo.get(2);
            String modelType = (String) modelInfo.get(3);

            Vector modifiedVec = FileSystemFunctions.getModelLastModified(domeConn.getServerConnection(), modelId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            if (findingModelName.equals(modelName)) {
                return new DomeModel(modelId, modelName, modelDesc, modelType, lastModified, version, false, null, domeConn);
            }
        }
        return null;
    }


    /**
     * retrieve the DomeModel instance with the given name. returns null when there is no match.
     */
    public DomeModel getModelById(String findingModelId) {
        /* retrieve folder & model list */
        Vector v = BrowseFileSystemFunctions.getModelFolderContents(domeConn.getServerConnection(), folderId);
        List modelList = (Vector) v.get(1);

        /* find a model with given name, and then create a dome model instance */
        for (int i = 0; i < modelList.size(); i++) {
            Vector modelInfo = (Vector) modelList.get(i);
            String modelId = (String) modelInfo.get(0);
            String modelName = (String) modelInfo.get(1);
            String modelDesc = (String) modelInfo.get(2);
            String modelType = (String) modelInfo.get(3);

            Vector modifiedVec = FileSystemFunctions.getModelLastModified(domeConn.getServerConnection(), modelId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            if (findingModelId.equals(modelId)) {
                return new DomeModel(modelId, modelName, modelDesc, modelType, lastModified, version, false, null, domeConn);
            }
        }
        return null;
    }

    /**
     * retrieve the DomeProject instance with the given name. returns null when there is no match.
     */
    public DomeProject getProjectByName(String findingProjectName) {
        Vector v = FileSystemFunctions.getProjectFolderContents(domeConn.getServerConnection(), folderId);

        for (int i = 0; i < v.size(); i++)
        {
            Vector projectInfo = (Vector) v.get(i);
            String projectId = (String) projectInfo.get(0);
            String projectName = (String) projectInfo.get(1);
            String description = (String) projectInfo.get(2);

            Vector modifiedVec = FileSystemFunctions.getProjectLastModified(domeConn.getServerConnection(), projectId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            if (findingProjectName.equals(projectName)) {
                return new DomeProject(projectId, projectName, description, lastModified, version, false, null, domeConn);
            }
        }

        return null;
    }

    /**
     * retrieve the DomeAnalysisTool instance with the given name.  returns null when there is no match.
     */
    public DomeAnalysisTool getAnalysisToolByName(String findingAnalysisToolName)
    {
        Vector v = FileSystemFunctions.getAnalysisToolFolderContents(domeConn.getServerConnection(), folderId);

        for (int i=0; i < v.size(); i++)
        {
            Vector analysisToolInfo = (Vector) v.get(i);
            String analysisToolId = (String) analysisToolInfo.get(0);
            String analysisToolName = (String) analysisToolInfo.get(1);
            String description = (String) analysisToolInfo.get(2);

            Vector modifiedVec = FileSystemFunctions.getAnalysisToolLastModified(domeConn.getServerConnection(), analysisToolId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            if (findingAnalysisToolName.equals(analysisToolName))
            {
                return new DomeAnalysisTool(analysisToolId, analysisToolName, description, lastModified, version, null, domeConn);
            }
        }

        return null;
    }

    /**
     * retrieve the DomeProject instance with the given id. returns null when there is no match.
     */
    public DomeProject getProjectById(String findingProjectId) {
        Vector v = FileSystemFunctions.getProjectFolderContents(domeConn.getServerConnection(), folderId);

        for (int i = 0; i < v.size(); i++)
        {
            Vector projectInfo = (Vector) v.get(i);
            String projectId = (String) projectInfo.get(0);
            String projectName = (String) projectInfo.get(1);
            String description = (String) projectInfo.get(2);

            Vector modifiedVec = FileSystemFunctions.getProjectLastModified(domeConn.getServerConnection(), projectId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            if (findingProjectId.equals(projectName)) {
                return new DomeProject(projectId, projectName, description, lastModified, version, false, null, domeConn);
            }
        }

        return null;
    }

    /**
     * retrieve the DomeAnalysisTool instance with the given id ... returns null when no match is found
     */
    public DomeAnalysisTool getAnalysisToolById(String findingAnalysisToolId)
    {
        Vector v = FileSystemFunctions.getAnalysisToolFolderContents(domeConn.getServerConnection(), folderId);

        for (int i = 0; i < v.size(); i++)
        {
            Vector analysisToolInfo = (Vector) v.get(i);
            String analysisToolId = (String) analysisToolInfo.get(0);
            String analysisToolName = (String) analysisToolInfo.get(1);
            String analysisToolDescription = (String) analysisToolInfo.get(2);

            Vector modifiedVec = FileSystemFunctions.getAnalysisToolLastModified(domeConn.getServerConnection(), analysisToolId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            if (findingAnalysisToolId.equals(analysisToolId))
                return new DomeAnalysisTool(analysisToolId, analysisToolName, analysisToolDescription, lastModified, version, null, domeConn);
        }

        return null;
    }


    /**
     * returns string description of this folder
     */
    public String toString() {
        /* retrieve folder & model list */
        Vector v = BrowseFileSystemFunctions.getModelFolderContents(domeConn.getServerConnection(), folderId);
        List folderList = (Vector) v.get(0);
        List modelList = (Vector) v.get(1);

        return "[DOME FOLDER: '" + folderName + "' (ID = " + folderId + ") with " + folderList.size() + " sub-folders, " + modelList.size() + " models]";
    }
}