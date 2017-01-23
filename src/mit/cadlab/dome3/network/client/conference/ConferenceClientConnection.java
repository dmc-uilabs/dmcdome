package mit.cadlab.dome3.network.client.conference;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.net.MalformedURLException;
import java.io.IOException;

import mit.cadlab.dome3.network.client.connection.UnreachableServerException;
import mit.cadlab.dome3.network.client.connection.ServerMethodException;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Apr 1, 2003
 * Time: 2:17:09 AM
 * To change this template use Options | File Templates.
 */
public class ConferenceClientConnection
{
	private XmlRpcClient _clientConnection;

	public ConferenceClientConnection(String clientURL)
	{
		try {
			System.out.println(clientURL);
			this._clientConnection = new XmlRpcClient(clientURL);
			if (this._clientConnection == null)
				System.out.println("ERROR: could not establish a connection with client");
		} catch (MalformedURLException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
	}

	public Object execute(String methodName, Vector params)
	        throws UnreachableServerException, ServerMethodException
	{
		try {
			return this._clientConnection.execute(methodName, params);
		} catch (XmlRpcException re) {
			throw new ServerMethodException(re.code, re.getMessage().replaceAll("org.apache.xmlrpc.XmlRpcException: ", ""));
		} catch (IOException ioe) {
			throw new UnreachableServerException(ioe.toString());
		}
	}
}
