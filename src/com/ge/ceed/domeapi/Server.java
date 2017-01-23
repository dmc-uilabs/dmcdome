package com.ge.ceed.domeapi;

import org.slf4j.Logger;

import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.deploy.DeployServerConnection;

/**
 * Data object holding information on how to get to a DOME server
 * 
 * @author ?
 * 
 */
public class Server {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Server.class);
	private static final int hashprime = 31;

	private String name;
	private String port;
	private String user;
	private String pw; // removed transient.
	//TODO: If playspaces get implemented, add support for them
	private String space = DeployServerConnection.USER;

	public Server(String name, String port, String user, String pw) {
		this.name = name;
		this.port = port;
		this.user = user;
		this.pw = pw;
	}

	public Server(String name, String port, String user) {
		this.name = name;
		this.port = port;
		this.user = user;
	}

	public String getName() {
		return this.name;
	}

	public String getPort() {
		return this.port;
	}

	public String getUser() {
		return this.user;
	}

	public String getPw() {
		return this.pw;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public String getKey() {
		return this.user + "@" + this.name + ":" + this.port;
	}

	public String toString() {
		return "Server " + this.name + ":" + this.port + " as " + this.user + (this.pw == null ? "" : "/" + this.pw);
	}

	public DomeConnection openConnection() {
		logger.debug("\nAttempting DOME login to {}:{} as '{}', password='{}'", new Object[]{name, port, user, pw});
		return new DomeConnection(this.user, this.pw, this.name + ":" + this.port);
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = hashprime * result + ((name == null) ? 0 : name.toUpperCase().hashCode());
		result = hashprime * result + ((port == null) ? 0 : port.hashCode());
		result = hashprime * result + ((pw == null) ? 0 : pw.hashCode());
		result = hashprime * result + ((space == null) ? 0 : space.hashCode());
		result = hashprime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Server other = (Server) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (pw == null) {
			if (other.pw != null)
				return false;
		} else if (!pw.equals(other.pw))
			return false;
		if (space == null) {
			if (other.space != null)
				return false;
		} else if (!space.equals(other.space))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
