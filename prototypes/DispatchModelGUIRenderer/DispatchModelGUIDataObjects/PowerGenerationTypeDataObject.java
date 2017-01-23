package DispatchModelGUIRenderer.DispatchModelGUIDataObjects;

/**
 * Created by IntelliJ IDEA.
 * Name: PowerGenerationTypeDataObject
 * User: jacob
 * Date: Jul 28, 2003
 * Time: 7:27:30 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class PowerGenerationTypeDataObject
{
	private String _powerGenerationTypeName;

	public PowerGenerationTypeDataObject(String name)
	{
		_powerGenerationTypeName = name;
	}

	public void setPowerGenerationTypeName(String newName)
	{
		this._powerGenerationTypeName = newName;
	}

	public String getPowerGenerationTypeName()
	{
		return this._powerGenerationTypeName;
	}
}
