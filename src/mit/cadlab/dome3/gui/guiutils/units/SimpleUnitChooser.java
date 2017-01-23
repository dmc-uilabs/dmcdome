// SimpleUnitChooser.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.units;

import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimpleUnitChooser extends JDialog
{
	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
	protected static String title = "Unit Chooser";
	protected static Dimension SIZE = new Dimension(200, 300);
	protected JList list;
	protected Unit selectedUnit = null;

	public static Unit showDialog(JComponent comp,
	                              Unit unit)
	{
		SimpleUnitChooser chooser = new SimpleUnitChooser(comp, unit);
		chooser.show();
		return chooser.selectedUnit;
	}

	protected SimpleUnitChooser(Component comp, Unit unit)
	{
		super(JOptionPane.getFrameForComponent(comp), title, true); // modal
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(makeChooser(unit), BorderLayout.CENTER);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(comp);
	}

	protected JPanel makeChooser(Unit unit)
	{
		JPanel p = new JPanel();
		list = Templates.makeList(getAllUnits().toArray());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (unit != null)
			list.setSelectedValue(unit, true);


		JButton ok = Templates.makeButton("select", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectedUnit = (Unit) list.getSelectedValue();
				dispose();
			}
		});
		JButton cancel = Templates.makeButton("cancel", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

		// component array for GridBagLayout
		JComponent[] comps = {Templates.makeLabel("Select a unit:"),
		                      new JScrollPane(list),
		                      ok, cancel};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
		};

		Templates.layoutGridBagB(p, comps, gbcs);
		p.setPreferredSize(SIZE);
		return p;
	}

	protected class UnitComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return o1.toString().compareTo(o2.toString());
		}
	}

	protected List getAllUnits()
	{
		List l = new ArrayList();
		l.addAll(UnitAtom.getAllUnits());
		Collections.sort(l, new UnitComparator());
		return l;
	}

	public static void main(String[] args)
	{
		DomeInit.loadUnits();
		System.out.println(showDialog(null, null));
	}

}
