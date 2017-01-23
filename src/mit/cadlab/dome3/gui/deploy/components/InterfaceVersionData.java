package mit.cadlab.dome3.gui.deploy.components;

import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 17, 2003
 * Time: 7:46:09 PM
 * To change this template use Options | File Templates.
 */
public class InterfaceVersionData implements Comparable
{
	private String _interfaceId, _buildId;
	private Integer _version;

	public InterfaceVersionData(Vector interfaceData)
	{
		this._interfaceId = ((String) interfaceData.elementAt(0));
		this._version = ((Integer) interfaceData.elementAt(1));
		this._buildId = ((String) interfaceData.elementAt(2));
	}

	public InterfaceVersionData(String[] info)
	{
		//String[] info = line.split("\t");
		this._interfaceId = (info[1]);
		this._version = (new Integer(info[2]));
		this._buildId = (info[3]);
	}


	public String getInterfaceId()
	{
		return this._interfaceId;
	}

	public String getBuildId()
	{
		return this._buildId;
	}

	public int getVersion()
	{
		return this._version.intValue();
	}

	public int compareTo(Object o)
	{
		return 0;
	}

	public String toString()
	{
		return "interface:" + "\t" + _interfaceId + "\t" + _version + "\t" + _buildId + "\n";
	}
}
