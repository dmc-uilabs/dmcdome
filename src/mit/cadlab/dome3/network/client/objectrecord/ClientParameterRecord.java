package mit.cadlab.dome3.network.client.objectrecord;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterRuntime;
import mit.cadlab.dome3.network.CompoundId;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 31, 2003
 * Time: 7:22:43 PM
 * To change this template use Options | File Templates.
 */
public class ClientParameterRecord extends ClientObjectRecord
{
	InterfaceParameterRuntime param;

	public ClientParameterRecord(CompoundId id, String name, String description,
	                             String url, InterfaceParameterRuntime param)
	{
		super(id, name, description, url);
		this.param = param;
	}

	public void setParameter(InterfaceParameterRuntime param)
	{
		this.param = param;
	}

	public InterfaceParameterRuntime getInterface()
	{
		return param;
	}
}
