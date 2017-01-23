package sofcgui;

import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

import sofcgui.sofcguipanels.SOFCModelInputPanel;

/**
 * Created by IntelliJ IDEA.
 * Name: SOFCGUI
 * User: jacob
 * Date: Aug 14, 2003
 * Time: 5:51:36 PM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class SOFCGUI extends JPanel
{
	private final static GridBagConstraints gbc = null;

	private JTabbedPane _guiTabs = Templates.makeTabbedPane();

	private SOFCModelInputPanel _inputPanel;

	public SOFCGUI()
	{
		initalizeParameters();
		createComponents();
		layoutComponents();
		registerListeners();
	}

	protected void initalizeParameters()
	{

	}

	protected void createComponents()
	{
    	_inputPanel = new SOFCModelInputPanel();
	}

	protected void layoutComponents()
	{

	}

	protected void registerListeners()
	{

	}
}
