package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.util.DArrayList;

import java.util.List;

/**
 * Objects that go in the mit.cadlab.dome3.gui.fileSystem browser
 */
public abstract class FileSystemObject
{

	protected Object id;
	protected String name;
	protected boolean lookAtChildren = true;
	protected Folder parent;

	protected DArrayList content = new DArrayList();

	/**
	 * Constructor for subclasses that do not have the id, name information initially.
	 */
	protected FileSystemObject()
	{
	}

	public FileSystemObject(Object id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public Object getId()
	{
		return id;
	}

	protected void setId(Object id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	protected void setName(String name)
	{
		this.name = name;
	}


	public abstract void listChildren(ServerConnection svrConn);

	public abstract String getType();

	public String toString()
	{
		return name + ": " + id;
	}

	public void refresh(ServerConnection svrConn)
	{
		content.clear();
		this.lookAtChildren = true;
		this.listChildren(svrConn);
	}

	public Folder getParent()
	{
		return parent;
	}

}
