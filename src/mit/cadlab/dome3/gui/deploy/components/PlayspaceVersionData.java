package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.network.client.connection.ServerConnection;

import java.util.Vector;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 23, 2003
 * Time: 3:07:52 PM
 * To change this template use Options | File Templates.
 */
public class PlayspaceVersionData
{
	private String _serverPort;
	private String _user;
	private String _deployId;
	private Integer _version;

	public PlayspaceVersionData(String playspaceVersionFile)
	{
		try {
			FileReader fileReader = new FileReader(playspaceVersionFile);
			BufferedReader bufferReader = new BufferedReader(fileReader);
			String[] firstLineInFile = bufferReader.readLine().split("\t");
			this._serverPort = firstLineInFile[0];
			this._user = firstLineInFile[1];
			String[] playspaceInformation = bufferReader.readLine().split("\t");
			this._deployId = playspaceInformation[1];
			this._version = new Integer(playspaceInformation[2]);
			bufferReader.close();
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
	}

	public PlayspaceVersionData(Vector serverData, ServerConnection svrConn)
	{
		this._serverPort = svrConn.getServerPort();
		this._user = svrConn.getLoginName();
		this._deployId = (String) serverData.elementAt(0);
		this._version = (Integer) serverData.elementAt(1);
	}

	public String getServerPort()
	{
		return this._serverPort;
	}

	public String getUser()
	{
		return this._user;
	}

	public String getDeployId()
	{
		return this._deployId;
	}

	public int getVersion()
	{
		return this._version.intValue();
	}

	public String writePlayspaceVersionDataToString()
	{
		StringBuffer s = new StringBuffer();
		s.append(this._serverPort + "\t" + this._user + "\n");
		s.append("playspace: " + "\t" + this._deployId + "\t" + this._version.intValue() + "\n");
		return s.toString();
	}
}
