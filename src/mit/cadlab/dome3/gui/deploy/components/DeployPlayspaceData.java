// DeployPlayspaceData.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.

package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.DomeXmlData;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;

import javax.swing.JFileChooser;
import java.io.FileNotFoundException;

public class DeployPlayspaceData
{
	protected String _pathName;
	protected String _xmlContent;
	protected String _idXml;
	protected String _nameXml;
	protected String _playspaceDescription;
	protected String _deployId;

	public DeployPlayspaceData(String pathName, String xmlContent, String id, String name)
	{
		this._pathName = pathName;
		this._xmlContent = xmlContent;
		this._idXml = id;
		this._nameXml = name;
		this._playspaceDescription = null;
	}

	public DeployPlayspaceData(String pathName)
	{
		this._pathName = pathName;
		try {
			this._xmlContent = FileUtils.readTextFileAsString(pathName);
		} catch (FileNotFoundException e) {
			System.err.println("file not found: " + pathName);
		}
		DomeXmlData xmlData = new DomeXmlData(DomeXmlData.PLAYSPACE, this._xmlContent);
		this._idXml = xmlData.getId();
		this._nameXml = xmlData.getName();
		this._playspaceDescription = null;
	}

	public String getDeployId()
	{
		return _deployId;
	}

	public void setDeployId(String _deployId)
	{
		this._deployId = _deployId;
	}

	public void setPathName(String str)
	{
		_pathName = str;
	}

	public String getPathName()
	{
		return _pathName;
	}

	public void setXmlContent(String str)
	{
		this._xmlContent = str;
	}

	public String getXmlContent()
	{
		return this._xmlContent;
	}

	public void setId(String str)
	{
		this._idXml = str;
	}

	public String getId()
	{
		return this._idXml;
	}

	public void setName(String str)
	{
		this._nameXml = str;
	}

	public String getName()
	{
		return this._nameXml;
	}

	public String getPlayspaceDescription()
	{
		return this._playspaceDescription;
	}

	public void setPlayspaceDescription(String value)
	{
		this._playspaceDescription = value;
	}

	public static void main(String[] args)
	{
		DomeFileChooser chooser = new DomeFileChooser();
		chooser.setFilter(DomeFileChooser.DOME_PLAYSPACE_FILTER);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String fPath = chooser.getSelectedFile().getPath();//.getName();
			DeployPlayspaceData playspaceData = DeployUtilities.loadPlayspaceForDeploy(fPath);
			System.out.println(playspaceData._idXml);
			System.out.println(playspaceData._nameXml);
			System.out.println(playspaceData._pathName);
			System.out.println(playspaceData._xmlContent);
			System.out.println(playspaceData._playspaceDescription);
		}

	}
}
