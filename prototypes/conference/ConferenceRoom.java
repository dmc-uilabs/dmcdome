package conference;

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
	private HashMap _memberConnections;

	public ConferenceRoom()
	{
		this._memberConnections = new HashMap();
	}
	public void addConferenceMember(String loginName, ConferenceClientConnection clientConn)
	{
		if(this._memberConnections != null)
			this._memberConnections.put(loginName, clientConn);
		System.out.println(loginName);
	}
	public List getConferenceMembers()
	{
		return new ArrayList(this._memberConnections.keySet());
	}
	public ConferenceClientConnection getClientConnection(String memberName)
	{
		return (ConferenceClientConnection) this._memberConnections.get(memberName);
	}
	public boolean isAMember(String memberName)
	{
		return this._memberConnections.containsKey(memberName);
	}
}
