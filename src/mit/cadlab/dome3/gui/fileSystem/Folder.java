// Folder.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;

import java.util.Collection;
import java.util.Iterator;
import java.awt.Component;

public abstract class Folder extends FileSystemObject
{

	// folder types
	public static final String FOLDER = "Folder";
	public static final String USER_HOME = "User";
	public static final String GROUP_HOME = "Group";
	public static final String USERS_ROOT = "Users";
	public static final String GROUPS_ROOT = "Groups";
	public static final String SERVER_ROOT = "Server";

	public static final String PROJECT_CONTENT_FOLDER = "Project Content";
    public static final String ANALYSIS_TOOL_CONTENT_FOLDER = "Analysis Tool Content";
	public final String INTERFACE_FOLDER = "interface";

	protected String folderType;
	//protected DArrayList content = new DArrayList();


	protected Folder(String folderType, Integer id, String name)
	{
		super(id, name);
		this.folderType = folderType;
	}

	protected Folder(String folderType)
	{
		super(new Integer(DbConstants.NULL), "");
		this.folderType = folderType;
	}

	public int getIntId()
	{
		return ((Integer) id).intValue();
	}

	public String getFolderType()
	{
		return folderType;
	}

	public DArrayList getContent()
	{
		return content;
	}

	public String getType()
	{
		return folderType;
	}

	public void addContent(Object obj)
	{
		if (obj instanceof FileSystemObject)
			((FileSystemObject) obj).parent = this;
		content.add(obj);
	}

	public void addContents(Collection objs)
	{
		Iterator i = objs.iterator();
		while (i.hasNext()) {
			Object f = i.next();
			if (f instanceof FileSystemObject)
				((FileSystemObject) f).parent = this;
			content.add(f);
		}
		//content.addAll(objs);
	}

	public void deleteContent(Object obj)
	{
		content.remove(obj);
	}

	public void deleteContents(Collection objs)
	{
		content.removeAll(objs);
	}

	/*public void refresh(ServerConnection svrConn) {
		content.clear();
		this.lookAtChildren = true;
		this.listChildren(svrConn);
	}*/

    public void listChildren(ServerConnection svrConn) {
        listChildren(svrConn, null);
    }

	public void listChildren(ServerConnection svrConn, Component waitCursorParent)
	{
		if (this.lookAtChildren) {
            WaitCursorUtils.showWaitCursor(true, waitCursorParent);
			if (folderType.equals(USER_HOME) || folderType.equals(GROUP_HOME)) {
				//System.out.println("In Folder: loadUserOrGroupHome(svrConn);");
				loadUserOrGroupHome(svrConn);
			} else if (folderType.equals(USERS_ROOT)) {
				//System.out.println("In Folder: loadUsersRoot(svrConn);");
				loadUsersRoot(svrConn);
			} else if (folderType.equals(GROUPS_ROOT)) {
				//System.out.println("In Folder: loadGroupsRoot(svrConn);");
				loadGroupsRoot(svrConn);
			} else if (folderType.equals(SERVER_ROOT)) {
				//System.out.println("In Folder: loadServerRoot(svrConn);");
				loadServerRoot(svrConn);
			} else {
				//System.out.println("In Folder: loadFolder(svrConn);");
				loadFolder(svrConn);
			}
			this.lookAtChildren = false;
            WaitCursorUtils.showWaitCursor(false, waitCursorParent);
		}
	}

	protected abstract void loadUserOrGroupHome(ServerConnection svrConn);

	protected abstract void loadUsersRoot(ServerConnection svrConn);

	protected abstract void loadGroupsRoot(ServerConnection svrConn);

	protected abstract void loadServerRoot(ServerConnection svrConn);

	protected abstract void loadFolder(ServerConnection svrConn);


}
