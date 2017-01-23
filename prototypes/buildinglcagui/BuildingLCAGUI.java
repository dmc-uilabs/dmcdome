package buildinglcagui;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;

import javax.swing.*;
import java.awt.*;

import buildinglcagui.buildinglcaguipanels.BuildingLCAInputSpecificationPanel;
import buildinglcagui.buildinglcaguipanels.ResultsPanel;

/**
 * Created by IntelliJ IDEA.
 * Name: BuildingLCAGUI
 * User: jacob
 * Date: Aug 13, 2003
 * Time: 2:37:35 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class BuildingLCAGUI extends JPanel
{
	public static final String INPUT_SPECIFICATION = "building specification";
	public static final String RESULTS = "results";

	private static final GridBagConstraints gbc = null;

	private JTabbedPane _guiTabs = Templates.makeTabbedPane();

	private ModelInterfaceBase _iface;

	public BuildingLCAGUI(ModelInterfaceBase iface)
	{
		_iface = iface;

		makeComponents();
		layoutComponents();
	}

	protected void makeComponents()
	{
		_guiTabs.setTabPlacement(SwingConstants.TOP);
		_guiTabs.addTab(INPUT_SPECIFICATION, makeBuildingLCAInputSpecificationPanel());
		_guiTabs.addTab(RESULTS, makeOutput1Panel());
	}

	protected void layoutComponents()
	{
		JComponent[] comps = {

			_guiTabs

		};

		GridBagConstraints[] gbcs = {

			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)

		};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makeBuildingLCAInputSpecificationPanel()
	{
		return new BuildingLCAInputSpecificationPanel(_iface);
	}

	private JPanel makeOutput1Panel()
	{
		return new ResultsPanel(_iface);
	}

}
