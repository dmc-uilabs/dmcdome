package mit.cadlab.dome3.objectmodel.playspace;

import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.network.CompoundId;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 31, 2003
 * Time: 7:21:56 PM
 * To change this template use Options | File Templates.
 */
public class ClientPlayspaceRecord extends ClientObjectRecord
{
	ClientPlayspaceRuntime playspace;
	protected CompoundId compoundId;

	public ClientPlayspaceRecord(ClientPlayspaceRuntime playspace)
	{
		this.playspace = playspace;
		this.name = playspace.getName();
		this.compoundId = playspace.getCompoundId();
	}

	public ClientPlayspaceRuntime getPlayspace()
	{
		return playspace;
	}

	public void listChildren()
	{
		content.addAll(playspace.getModels());
		content.addAll(playspace.getProjects());
        content.addAll(playspace.getTools());}

}
