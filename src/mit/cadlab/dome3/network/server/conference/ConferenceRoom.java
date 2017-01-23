package mit.cadlab.dome3.network.server.conference;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.client.conference.ConferenceClientConnection;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Apr 1, 2003
 * Time: 3:21:24 AM
 * To change this template use Options | File Templates.
 */
public class ConferenceRoom
{
	private HashMap memberConnections;

	public ConferenceRoom()
	{
		this.memberConnections = new HashMap();
	}

	public void addConferenceMember(String loginName, ConferenceClientConnection clientConn)
	{
		if (this.memberConnections != null)
			this.memberConnections.put(loginName, clientConn);
		//Debug.trace(Debug.ALL, loginName);
	}

    public void removeConferenceMember(String loginName)
	{
		if (this.memberConnections != null)
			this.memberConnections.remove(loginName);
		//Debug.trace(Debug.ALL, loginName);
	}

	public List getConferenceMembers()
	{
		return new ArrayList(memberConnections.keySet());
	}

	public ConferenceClientConnection getClientConnection(String memberName)
	{
		return (ConferenceClientConnection) memberConnections.get(memberName);
	}

	public boolean isAMember(String memberName)
	{
		return memberConnections.containsKey(memberName);
	}
}
