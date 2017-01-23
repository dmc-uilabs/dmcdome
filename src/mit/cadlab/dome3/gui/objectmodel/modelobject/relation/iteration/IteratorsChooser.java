// SimpleUnitChooser.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class IteratorsChooser extends JDialog {
    protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
    protected static String title = "Iterators Chooser";
    protected static Dimension SIZE = new Dimension(280, 300);
    protected JList list,selectedlist;
    protected JButton addButton,delButton,cancelButton,okButton;
    protected List selectedItems = null;

    public static List showDialog(JComponent comp, List availableParams,
                                  List selectedParams) {
        IteratorsChooser chooser = new IteratorsChooser(comp, availableParams, selectedParams);
        chooser.show();
        return chooser.selectedItems;
    }

    protected IteratorsChooser(Component comp, List availableParams,
                               List selectedParams) {
        super(JOptionPane.getFrameForComponent(comp), title, true); // modal

        if (selectedItems == null) selectedItems = new ArrayList();

        if (selectedParams != Collections.EMPTY_LIST) {
            selectedItems.addAll(selectedParams);
        }
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(makeChooser(availableParams, selectedParams), BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(comp);
    }

    protected JPanel makeChooser(List availableParams, List selectedParams) {
        JPanel p = new JPanel();

        addButton = Templates.makeButton("add", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (list.getSelectedValue() != null) {
                    selectedItems.add(list.getSelectedValue());
                    ((DefaultListModel) selectedlist.getModel()).addElement(list.getSelectedValue());
                 }
            }
        });
        delButton = Templates.makeButton("remove", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedlist.getSelectedValue() != null) {
                    selectedItems.remove(selectedlist.getSelectedValue());
                    ((DefaultListModel) selectedlist.getModel()).removeElement(selectedlist.getSelectedValue());
                }
            }
        });
        okButton = Templates.makeButton("ok", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        cancelButton = Templates.makeButton("cancel", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedItems = null;
                dispose();
            }
        });

        DefaultListModel listmodel = new DefaultListModel();
        for (Iterator i = availableParams.iterator(); i.hasNext();) {
            listmodel.addElement(i.next());
        }

        list = Templates.makeList(listmodel);
        list.setCellRenderer(new IterationListCellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                  if (list.getSelectedIndex() == -1) {
                       addButton.setEnabled(false);
                   } else {
                        Object param_to_be_add = list.getSelectedValue();
                        if (!selectedItems.contains(param_to_be_add))
                            addButton.setEnabled(true);
                       else
                            addButton.setEnabled(false);
                    }
                }
            }
        });


        DefaultListModel selectlistmodel = new DefaultListModel();
        for (Iterator i = selectedParams.iterator(); i.hasNext();) {
            selectlistmodel.addElement(i.next());
        }

        selectedlist = Templates.makeList(selectlistmodel);
        selectedlist.setCellRenderer(new IterationListCellRenderer());
        selectedlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedlist.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {

                    if (selectedlist.getSelectedIndex() == -1) {
//No selection, disable fire button.
                        delButton.setEnabled(false);

                    } else {
//Selection, enable the fire button.
                        delButton.setEnabled(true);
                    }
                }
            }
        });

        addButton.setEnabled(false);
        delButton.setEnabled(false);
        // component array for GridBagLayout
        JComponent[] comps = {Templates.makeLabel("Select iterators:"),
                              new JScrollPane(list), new JScrollPane(selectedlist),
                              addButton, delButton, okButton, cancelButton};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(2, 1, 2, 1, 1.0, 1.0, gbc.EAST, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBagB(p, comps, gbcs);
        p.setPreferredSize(SIZE);
        return p;
    }
}
