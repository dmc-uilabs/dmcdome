package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.network.client.connection.ServerConnection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 17, 2003
 * Time: 7:01:14 PM
 * To change this template use Options | File Templates.
 */
public class ProjectVersionData
{
	String _serverPort, _user;
	ServerConnection _serverConnection;
	String _projectId;
	Integer _version;
	//HashMap _interfaces;

	HashMap _projInterfacesByBuildId = new HashMap();
	HashMap _projInterfacesByInterfaceId = new HashMap();

	HashMap _projIModelByBuildId = new HashMap();
	HashMap _projIModelByModelId = new HashMap();


	public ProjectVersionData(String modelVersionFile)
	{
		try {
			FileReader fileReader = new FileReader(modelVersionFile);
			BufferedReader bufferReader = new BufferedReader(fileReader);
			String aLine = bufferReader.readLine();
			String[] firstLineInFile = aLine.split("\t");

			this._serverPort = firstLineInFile[0];
			this._user = firstLineInFile[1];

			final String[] projectInformation = bufferReader.readLine().split("\t");
			this._projectId = projectInformation[1];
			this._version = new Integer(projectInformation[2]);


			//Load the Project Interfaces
			InterfaceVersionData ivd;
			String[] interfaceInformation;
			int nbInterfaces = new Integer(projectInformation[3]).intValue();
			for (int i = 0; i < nbInterfaces; i++) {
				aLine = bufferReader.readLine();
				interfaceInformation = aLine.split("\t");
				ivd = new InterfaceVersionData(interfaceInformation);
				_projInterfacesByBuildId.put(ivd.getBuildId(), ivd);
				_projInterfacesByInterfaceId.put(ivd.getInterfaceId(), ivd);
			}

			//load the project iModels
			String[] modelInformation;
			Vector interfacesInfo;
			IModelVersionData imvd;
			int nbModels = new Integer(projectInformation[4]).intValue();
			for (int i = 0; i < nbModels; i++) {
				aLine = bufferReader.readLine();
				modelInformation = aLine.split("\t");

				interfacesInfo = new Vector();
				nbInterfaces = new Integer(modelInformation[4]).intValue();
				for (int j = 0; j < nbInterfaces; j++) {
					aLine = bufferReader.readLine();
					interfaceInformation = aLine.split("\t");
					interfacesInfo.add(interfaceInformation);
				}
				imvd = new IModelVersionData(modelInformation, interfacesInfo);
				_projIModelByBuildId.put(imvd.getBuildId(), imvd);
				_projIModelByModelId.put(imvd.getModelId(), imvd);
			}

			bufferReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
	}

	public ProjectVersionData(Vector data, ServerConnection svrConn)
	{
		Vector projectInfo = (Vector) data.get(0);
		Vector projetIFaceInfo = (Vector) data.get(1);
		Vector iModelInfo = (Vector) data.get(2);

		this._serverConnection = svrConn;
		this._serverPort = this._serverConnection.getServerPort();
		this._user = this._serverConnection.getLoginName();

		_projectId = (String) projectInfo.get(0);
		_version = (Integer) projectInfo.get(1);

		Vector anInterface;
		String bid, iid;
		InterfaceVersionData ivd;
		for (int i = 0; i < projetIFaceInfo.size(); i++) {
			anInterface = (Vector) projetIFaceInfo.get(i);
			iid = (String) anInterface.get(0);
			bid = (String) anInterface.get(2);
			ivd = new InterfaceVersionData(anInterface);
			_projInterfacesByBuildId.put(bid, ivd);
			_projInterfacesByInterfaceId.put(iid, ivd);
		}

		Vector aModel, aModelInfo;
		String mid;
		IModelVersionData imvd;
		for (int i = 0; i < iModelInfo.size(); i++) {
			aModel = (Vector) iModelInfo.get(i);
			aModelInfo = (Vector) aModel.get(0);
			mid = (String) aModelInfo.get(0);
			bid = (String) aModelInfo.get(2);
			imvd = new IModelVersionData(aModel);
			_projIModelByModelId.put(mid, imvd);
			_projIModelByBuildId.put(bid, imvd);
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

	public String getProjectId()
	{
		return this._projectId;
	}

	public int getVersion()
	{
		return this._version.intValue();
	}

	public List getInterfaces()
	{
		return new ArrayList(this._projInterfacesByInterfaceId.values());
	}

	public InterfaceVersionData getInterfaceVersionData(String interfaceId)
	{
		if (this._projInterfacesByInterfaceId.containsKey(interfaceId))
			return (InterfaceVersionData) this._projInterfacesByInterfaceId.get(interfaceId);
		return null;
	}

	public List getIModels()
	{
		return new ArrayList(this._projIModelByModelId.values());
	}

	public IModelVersionData getIModelVersionData(String modelId)
	{
		if (this._projIModelByModelId.containsKey(modelId))
			return (IModelVersionData) this._projIModelByModelId.get(modelId);
		return null;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append(this._serverPort + "\t" + this._user + "\n");
		s.append("project:" + "\t" + this._projectId +
		         "\t" + this._version +
		         "\t" + this._projInterfacesByBuildId.size() +
		         "\t" + this._projIModelByBuildId.size() + "\n");


		ListIterator iterator = this.getInterfaces().listIterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			s.append(o.toString());
		}

		iterator = this.getIModels().listIterator();
		while (iterator.hasNext()) {
			s.append(iterator.next()).toString();
		}

		return s.toString();
	}

	public InterfaceVersionData getInterfaceVersionDataByBuildId(String buildId)
	{
		if (this._projInterfacesByBuildId.containsKey(buildId))
			return (InterfaceVersionData) this._projInterfacesByBuildId.get(buildId);
		return null;
	}

	public IModelVersionData getIModelVersionDataByBuildId(String buildId)
	{
		if (this._projIModelByBuildId.containsKey(buildId))
			return (IModelVersionData) this._projIModelByBuildId.get(buildId);
		return null;
	}

	public int compareTo(Object o)
	{
		return 0;
	}
}
