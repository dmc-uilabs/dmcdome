package mit.cadlab.dome3.network.client.objectrecord;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DomeJavaBean;


/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 31, 2003
 * Time: 7:18:38 PM
 * To change this template use Options | File Templates.
 */
public class ClientObjectRecord extends DomeJavaBean
{
	protected CompoundId compoundId;
	protected String name;
	protected String description;
	protected String url;

	protected DArrayList content = new DArrayList();

	public ClientObjectRecord()
	{

	}

	public ClientObjectRecord(CompoundId id, String name, String description, String url)
	{
		this.compoundId = new CompoundId (id);
		this.name = name;
		this.description = description;
		this.url = url;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public String getUrl()
	{
		return url;
	}

	public DArrayList getContent()
	{
		return content;
	}

	public void listChildren()
	{

	}

}
