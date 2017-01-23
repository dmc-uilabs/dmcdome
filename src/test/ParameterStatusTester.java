// ParameterStatusTester.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.VTextIcon;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ParameterStatusTester extends JPanel
{
	PropertyChangeListener statusPrinter;

	public ParameterStatusTester(DomeModel model, boolean printStatus)
	{
		if (printStatus)
			statusPrinter = new StatusPrinter();
		List params = new ArrayList(model.getFilter(DomeModel.PARAMETERS_FILTER).getItems());
		List compList = new ArrayList();
		compList.add(new JLabel("parameter name"));
		compList.add(createParameterStatusLabels());
		Collections.sort(params, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return ((Parameter) o1).getName().compareTo(((Parameter) o2).getName());
			}
		});
		for (int i = 0; i < params.size(); i++) {
			Parameter param = (Parameter) params.get(i);
			compList.add(new JLabel(param.getName()));
			compList.add(createParameterStatusTestPanel(param));
			if (printStatus)
				param.addPropertyChangeListener(Parameter.VALUE_STATUS,statusPrinter);
		}
		List rels = new ArrayList(model.getFilter(DomeModel.RELATIONS_FILTER).getItems());
		for (int i = 0; i < rels.size(); i++) {
			Relation rel = (Relation) rels.get(i);
			Collection relParams = rel.getModelObjects();
			for (Iterator iterator = relParams.iterator(); iterator.hasNext();) {
				Parameter param = (Parameter) iterator.next();
				compList.add(new JLabel(param.getName()));
				compList.add(createParameterStatusTestPanel(param));
				if (printStatus)
					param.addPropertyChangeListener(Parameter.VALUE_STATUS, statusPrinter);
			}
		}
		JComponent[] comps = (JComponent[])compList.toArray(new JComponent[]{});
		GridBagConstraints[] gbcs = new GridBagConstraints[comps.length];
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		for (int i = 0; i < gbcs.length; i++) {
			if (i % 2 == 0)
				gbcs[i] = new GridBagConstraints(0, i / 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
			else
				gbcs[i] = new GridBagConstraints(1, i / 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0);
		}
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	private JPanel createParameterStatusLabels()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		int width = 5;
		p.add(Box.createHorizontalGlue());
		p.add(makeVerticalLabel("STALE"));
		p.add(Box.createHorizontalStrut(width));
		p.add(makeVerticalLabel("INCONSISTENT"));
		p.add(Box.createHorizontalStrut(width));
		p.add(makeVerticalLabel("WAITING"));
		p.add(Box.createHorizontalStrut(width));
		p.add(makeVerticalLabel("CONSISTENT"));
		p.add(Box.createHorizontalStrut(width));
		return p;
	}

	private JLabel makeVerticalLabel(String text)
	{
		JLabel l = new JLabel();
		VTextIcon i = new VTextIcon(l, text, VTextIcon.ROTATE_LEFT);
		l.setIcon(i);
		return l;
	}

	private JPanel createParameterStatusTestPanel(Parameter param)
	{
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 4));
		JRadioButton b1 = (JRadioButton) p.add(new JRadioButton(new StatusAction(param, Parameter.VALUE_STATUS_STALE)));
		JRadioButton b2 = (JRadioButton) p.add(new JRadioButton(new StatusAction(param, Parameter.VALUE_STATUS_INCONSISTENT)));
		JRadioButton b3 = (JRadioButton) p.add(new JRadioButton(new StatusAction(param, Parameter.VALUE_STATUS_WAITING_VALIDATION)));
		JRadioButton b4 = (JRadioButton) p.add(new JRadioButton(new StatusAction(param, Parameter.VALUE_STATUS_CONSISTENT)));
		ButtonGroup bg = new ButtonGroup();
		bg.add(b1);
		bg.add(b2);
		bg.add(b3);
		bg.add(b4);
		return p;
	}

	class StatusAction extends AbstractAction
	{
		Parameter p;
		String status;

		public StatusAction(Parameter p, String status)
		{
			this.p = p;
			this.status = status;
		}

		public void actionPerformed(ActionEvent e)
		{
			p.setValueStatus(status);
		}
	}

	static class StatusPrinter implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt)
		{
			Parameter param = (Parameter)evt.getSource();
			System.out.println(param.getName()+"\t"+param.getValueStatus());
		}
	}
}
