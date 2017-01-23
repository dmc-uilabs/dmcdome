package mit.cadlab.dome3.network.server.conference;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.net.MalformedURLException;
import java.util.Vector;
import java.io.IOException;

import mit.cadlab.dome3.network.client.DomeRuntimeClient;
import mit.cadlab.dome3.network.client.connection.UnreachableServerException;
import mit.cadlab.dome3.network.client.connection.ServerMethodException;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Apr 1, 2003
 * Time: 1:18:32 AM
 * To change this template use Options | File Templates.
 */
public class ConferenceServerConnection
{
	private String _serverPort;
	private String _clientURL;
	private String _serverURL;
	private String _loginName;
	private XmlRpcClient _serverConnection;


	public ConferenceServerConnection(String serverPort, String clientUrl, String loginName)
	{
		this._loginName = loginName;
		this._serverPort = serverPort;
		this._serverURL = "http://" + serverPort + "/RPC2";
		this._clientURL = "http://" + clientUrl + "/RPC2";
		try {
			System.out.println(this._serverURL);
			this._serverConnection = new XmlRpcClient(this._serverURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
	}

	public Object execute(String methodName, Vector params)
	        throws UnreachableServerException, ServerMethodException
	{
		try {
			return this._serverConnection.execute(methodName, params);
		} catch (XmlRpcException re) {
			throw new ServerMethodException(re.code, re.getMessage().replaceAll("org.apache.xmlrpc.XmlRpcException: ", ""));
		} catch (IOException ioe) {
			throw new UnreachableServerException(ioe.toString());
		}
	}

	public String getLoginName()
	{
		return this._loginName;
	}

	public String getClientUrl()
	{
		return this._clientURL;
	}

}
