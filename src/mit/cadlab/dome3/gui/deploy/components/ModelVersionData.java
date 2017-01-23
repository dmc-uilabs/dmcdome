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
public class ModelVersionData
{
	String _serverPort, _user;
	ServerConnection _serverConnection;
	String _modelId;
	Integer _version;
	HashMap _interfaces;
	HashMap _interfacesByBuildId;

	public ModelVersionData(String modelVersionFile)
	{
		try {
			this._interfaces = new HashMap();
			this._interfacesByBuildId = new HashMap();
			FileReader fileReader = new FileReader(modelVersionFile);
			BufferedReader bufferReader = new BufferedReader(fileReader);
			String[] firstLineInFile = bufferReader.readLine().split("\t");
			this._serverPort = firstLineInFile[0];
			this._user = firstLineInFile[1];
			String[] modelInformation = bufferReader.readLine().split("\t");
			this._modelId = modelInformation[1];
			this._version = new Integer(modelInformation[2]);
			String temp = bufferReader.readLine();
			String[] interfaceInformation;
			while (temp != null) {
				interfaceInformation = temp.split("\t");
				this._interfaces.put(interfaceInformation[1], new InterfaceVersionData(Vectors.create(interfaceInformation[1], new Integer(interfaceInformation[2]), interfaceInformation[3])));
				this._interfacesByBuildId.put(interfaceInformation[3], new InterfaceVersionData(Vectors.create(interfaceInformation[1], new Integer(interfaceInformation[2]), interfaceInformation[3])));
				temp = bufferReader.readLine();
			}
			bufferReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
	}

	public ModelVersionData(Vector serverData, ServerConnection svrConn)
	{
		this._serverConnection = svrConn;
		this._serverPort = this._serverConnection.getServerPort();
		this._user = this._serverConnection.getLoginName();
		this._interfaces = new HashMap();
		this._interfacesByBuildId = new HashMap();
		// this logic will check which Vector inside a Vector is the modelData or an interfaceData
		this._modelId = ((String) ((Vector) serverData.elementAt(0)).elementAt(0));
		this._version = ((Integer) ((Vector) serverData.elementAt(0)).elementAt(1));
		for (int i = 0; i < ((Vector) serverData.elementAt(1)).size(); i++) {
			this._interfaces.put((String) (((Vector) (((Vector) serverData.elementAt(1)).elementAt(i))).elementAt(0)), new InterfaceVersionData(((Vector) (((Vector) serverData.elementAt(1)).elementAt(i)))));
			this._interfacesByBuildId.put((String) (((Vector) (((Vector) serverData.elementAt(1)).elementAt(i))).elementAt(2)), new InterfaceVersionData(((Vector) (((Vector) serverData.elementAt(1)).elementAt(i)))));
		}
	}


	public String getServerPort()
	{
		return this._serverPort;
	}

	public String getUser()
	{
		return this._user;
	}

	public String getModelId()
	{
		return this._modelId;
	}

	public int getVersion()
	{
		return this._version.intValue();
	}

	public List getInterfaces()
	{
		return new ArrayList(this._interfaces.values());
	}

	public InterfaceVersionData getInterfaceVersionData(String interfaceId)
	{
		if (this._interfaces.containsKey(interfaceId)) return (InterfaceVersionData) this._interfaces.get(interfaceId);
		return null;
	}

	public String ModelVersionDataToString()
	{
		StringBuffer s = new StringBuffer();
		s.append(this._serverPort + "\t" + this._user + "\n");
		s.append("model:" + "\t" + this._modelId + "\t" + this._version + "\n");
		List interfaces = this.getInterfaces();
		ListIterator iterator = interfaces.listIterator();
		while (iterator.hasNext()) {
			InterfaceVersionData tempIVD = (InterfaceVersionData) iterator.next();
			s.append("interface:" + "\t" + tempIVD.getInterfaceId() + "\t" + tempIVD.getVersion() + "\t" + tempIVD.getBuildId() + "\n");
		}
		return s.toString();
	}

	public InterfaceVersionData getInterfaceVersionDataByBuildId(String buildId)
	{
		if (this._interfacesByBuildId.containsKey(buildId)) return (InterfaceVersionData) this._interfacesByBuildId.get(buildId);
		return null;
	}

	public int compareTo(Object o)
	{
		return 0;
	}
}
