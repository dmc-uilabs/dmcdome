package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.Vectors;

import java.util.Vector;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 17, 2003
 * Time: 7:01:14 PM
 * To change this template use Options | File Templates.
 */
public class IModelVersionData
{

	String _modelId;
	String _buildId;

	Integer _version;
	HashMap _IModelInterfacesByBuildId = new HashMap();
	HashMap _IModelInterfacesByInterfaceId = new HashMap();


	public IModelVersionData(Vector data)
	{

		Vector iModelInfo = (Vector) data.get(0);
		Vector iModelInterfaces = (Vector) data.get(1);

		_modelId = (String) iModelInfo.get(0);
		_version = (Integer) iModelInfo.get(1);
		_buildId = (String) iModelInfo.get(2);

		Vector anInterface;
		String bid, iid;
		InterfaceVersionData ivd;
		for (int i = 0; i < iModelInterfaces.size(); i++) {
			anInterface = (Vector) iModelInterfaces.get(i);
			iid = (String) anInterface.get(0);
			bid = (String) anInterface.get(2);
			ivd = new InterfaceVersionData(anInterface);
			_IModelInterfacesByBuildId.put(bid, ivd);
			_IModelInterfacesByInterfaceId.put(iid, ivd);
		}
	}


	public IModelVersionData(String[] imodel, Vector interfaces)
	{

		_modelId = imodel[1];
		_version = new Integer(imodel[2]);
		_buildId = imodel[3];

		InterfaceVersionData ivd;
		for (int i = 0; i < interfaces.size(); i++) {
			ivd = new InterfaceVersionData((String[]) interfaces.get(i));
			_IModelInterfacesByBuildId.put(ivd.getBuildId(), ivd);
			_IModelInterfacesByInterfaceId.put(ivd.getInterfaceId(), ivd);
		}
	}


	public String getModelId()
	{
		return this._modelId;
	}

	public String getBuildId()
	{
		return this._buildId;
	}

	public int getVersion()
	{
		return this._version.intValue();
	}

	public List getInterfaces()
	{
		return new ArrayList(this._IModelInterfacesByInterfaceId.values());
	}

	public InterfaceVersionData getInterfaceVersionData(String interfaceId)
	{
		if (this._IModelInterfacesByInterfaceId.containsKey(interfaceId))
			return (InterfaceVersionData) this._IModelInterfacesByInterfaceId.get(interfaceId);
		return null;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append("model:" + "\t" + this._modelId +
		         "\t" + this._version +
		         "\t" + this._buildId +
		         "\t" + this._IModelInterfacesByInterfaceId.size() + "\n");

		ListIterator iterator = this.getInterfaces().listIterator();
		while (iterator.hasNext()) {
			s.append(iterator.next().toString());
		}
		return s.toString();
	}

	public InterfaceVersionData getInterfaceVersionDataByBuildId(String buildId)
	{
		if (this._IModelInterfacesByBuildId.containsKey(buildId))
			return (InterfaceVersionData) this._IModelInterfacesByBuildId.get(buildId);
		return null;
	}

	public int compareTo(Object o)
	{
		return 0;
	}
}
