package mit.cadlab.dome3.gui.fileSystem.subscribe;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.BrowseFileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 29, 2003
 * Time: 7:34:51 PM
 * To change this template use Options | File Templates.
 */


public class SubscribeModelFolder extends BrowseModelFolder
{

	public static Folder createFolder(Integer id, String name)
	{
		return new SubscribeModelFolder(FOLDER, id, name);
	}

	public static Folder createUserFolder(Integer id, String name)
	{
		return new SubscribeModelFolder(USER_HOME, id, name);
	}

	public static Folder createGroupFolder(Integer id, String name)
	{
		return new SubscribeModelFolder(GROUP_HOME, id, name);
	}

	public static Folder createUserRootFolder(ServerConnection svrConn)
	{
		return new SubscribeModelFolder(USER_HOME, svrConn);
	}

	public static Folder createUsersRootFolder(ServerConnection svrConn)
	{
		return new SubscribeModelFolder(USERS_ROOT, svrConn);
	}

	public static Folder createGroupsRootFolder(ServerConnection svrConn)
	{
		return new SubscribeModelFolder(GROUPS_ROOT, svrConn);
	}

	public static Folder createServerRootFolder(ServerConnection svrConn)
	{
		return new SubscribeModelFolder(SERVER_ROOT, svrConn);
	}

	protected SubscribeModelFolder(String folderType, Integer id, String name)
	{
		super(folderType, id, name);
	}

	protected SubscribeModelFolder(String folderType, ServerConnection svrConn)
	{
		super(folderType, svrConn);
	}


	protected void loadFolder(ServerConnection svrConn)
	{
		//folders and files (model)
		String name, modelType;
		String idModel, description;
		/*CHANGE TO SUBSCRIBE FROM BROWSE*/
		Vector v = BrowseFileSystemFunctions.getModelFolderContents(svrConn, getIntId());
		Vector vecFolder = (Vector) v.get(0);
		Vector vecModel = (Vector) v.get(1);
		Vector modifiedVec = new Vector();
		for (int i = 0; i < vecFolder.size(); i++) {
			Integer idFolder = (Integer) ((Vector) vecFolder.get(i)).get(0);
			name = (String) ((Vector) vecFolder.get(i)).get(1);
			this.addContent(createFolder(idFolder, name));
		}
		for (int i = 0; i < vecModel.size(); i++) {
			Vector modelInfo = (Vector) vecModel.get(i);
			idModel = (String) modelInfo.get(0);
			name = (String) modelInfo.get(1);
			description = (String) modelInfo.get(2);
			modelType = (String) modelInfo.get(3);
			modifiedVec = FileSystemFunctions.getModelLastModified(svrConn, idModel);
			String lastModified = modifiedVec.get(0).toString();
			int version = ((Integer) modifiedVec.get(1)).intValue();
			this.addContent(new DomeFile(DomeFile.MODEL_TYPE, idModel, name, description, lastModified, version));
		}
	}

}
