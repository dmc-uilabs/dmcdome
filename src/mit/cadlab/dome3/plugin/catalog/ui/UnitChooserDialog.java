package mit.cadlab.dome3.plugin.catalog.ui;

import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.guiutils.units.UnitChooser;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.units.Quantity;
import mit.cadlab.dome3.util.units.Units;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 3.
 */
/**
 * Used for selecting dimension and unit for parameters.
 */
public class UnitChooserDialog extends JDialog
{

	public static String unitSystem = Units.ANY_SYSTEM;
	protected static Dimension preferredSize = new Dimension(410, 500);

	// use one of four static methods to show UnitChooser
	public static Unit showDialog(JComponent comp,
	                              Unit unit)
	{
		UnitChooserDialog chooser = new UnitChooserDialog(comp, unit);
		chooser.show();
		return chooser.getUnitSelection();
	}

	// instance variables
	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
	protected static String title = "Unit Chooser";
	protected static String[] noUnitArray = {Units.NO_UNIT};
	protected String[] unitSystems = {Units.ANY_SYSTEM,
	                                  Units.METRIC, Units.CUSTOMARY};
	protected static int defaultListHeight = 250;
	protected DefaultListModel dimensionsModel;
	protected DefaultListModel unitsModel;
	protected JList dimensionsList, unitsList;
	protected JTextField dimensionField;
	protected DComboBox unitSystemComboBox;
	protected JButton commitButton, cancelButton;
	protected Dimension listPrefSize, buttonsPrefSize;
	protected Unit unitSelection = null;

	public UnitChooserDialog(Component comp, Unit unit) {
		super(JOptionPane.getFrameForComponent(comp), title, true); // modal
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(makeChooser(unit), BorderLayout.CENTER);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(comp);

	}

	protected JPanel makeChooser(Unit unit) {
		if (unit == null)
			unit = Quantity.NO_UNIT;
		JPanel p = new JPanel();
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source == unitSystemComboBox) {
					changeSystemAction();
				} else if (source == commitButton) {
					commitAction();
				} else if (source == cancelButton) {
					cancelAction();
				} else {
                    if ("ESCAPE".equals(e.getActionCommand())) {
                        cancelAction();
                    }
                }
			}
		};

		// create Buttons
		commitButton = Templates.makeButton("ok", actionListener);
		cancelButton = Templates.makeButton("cancel", actionListener);

		// create system combobox
		unitSystemComboBox = Templates.makeDComboBox(unitSystems);
		unitSystemComboBox.setSelectedItem(unitSystem);
		unitSystemComboBox.addActionListener(actionListener);

		// create dimensionsModel
		dimensionsModel = new DefaultListModel();
		java.util.List dimensions = Units.getKindsOfQuantities();
		Iterator it = dimensions.iterator();
		while (it.hasNext())
			dimensionsModel.addElement(it.next());
		dimensionsList = Templates.makeList(dimensionsModel);
		dimensionsList.setSelectedValue(UnitAtom.getUnitCategory(unit.toString()), true);
		dimensionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dimensionsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				if (dimensionsList.isSelectionEmpty())
					return;
				String choice = (String) dimensionsList.getSelectedValue();
				setDimension(choice);
			}
		});

		// create dimensionField
		dimensionField = Templates.makeTextField(UnitAtom.getUnitCategory(unit.toString()));
		dimensionField.setEditable(false);

		// create unitsModel
//		List unitList = CUnit.createCUnitList(Units.getUnitsForQuantity(UnitAtom.getUnitCategory(unit.toString()), unitSystem));
//        unitsModel = new DefaultListModel();
//        unitsModel.ensureCapacity(unitList.size());
//        unitsModel.copyInto(unitList.toArray(new Object[unitList.size()]));
        unitsModel = createDefaultListModel(Units.getUnitsForQuantity(UnitAtom.getUnitCategory(unit.toString()), unitSystem));
		unitsList = Templates.makeList(unitsModel);
        unitsList.setCellRenderer(new UnitsListRenderer());
		unitsList.setSelectedValue(unit, true);
		unitsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					System.out.println("double click");
					commitAction();
				}
			}
		});

		// component array for GridBagLayout
		JComponent[] comps = {Templates.makeLabel("dimensions:", Templates.FONT11B),
		                      Templates.makeLabel("units:", Templates.FONT11B),
		                      new JScrollPane(dimensionsList),
		                      new JScrollPane(unitsList),
		                      dimensionField,
		                      unitSystemComboBox,
		                      makeButtonPanel()};

		// match height of combo box to height of dimensionfield
		Dimension cbSize = unitSystemComboBox.getPreferredSize();
		Dimension dimSize = dimensionField.getPreferredSize();
		unitSystemComboBox.setPreferredSize(new Dimension(cbSize.width, dimSize.height));

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 5, 10), 0, 0), // dimensions label
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 5, 0), 0, 0), // units label
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 5, 10), 150, 0), // dimensions list
			new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 5, 0), 0, 0), // units list
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 10), 0, 0), // dimension field
			new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0), // systemComboBox
			new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0) // buttonPanel
		};

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(actionListener, "ESCAPE", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		Templates.layoutGridBagB(p, comps, gbcs);
		p.setPreferredSize(preferredSize);
		return p;
	}

	protected JPanel makeButtonPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(commitButton);
		p.add(Box.createHorizontalStrut(5));
		p.add(cancelButton);
		buttonsPrefSize = p.getPreferredSize();
		return p;
	}

	public void setDimension(String dimension) {
		if (dimension != null && !dimension.equals(dimensionField.getText())) {
			dimensionField.setText(dimension);
//            List unitList = CUnit.createCUnitList(Units.getUnitsForQuantity(dimension, unitSystem));
//            System.out.println(Units.getUnitsForQuantity(dimension, unitSystem));
//            System.out.println(unitList);
//            unitsModel = new DefaultListModel();
//            unitsModel.ensureCapacity(unitList.size());
//            unitsModel.copyInto(unitList.toArray(new Object[unitList.size()]));
			unitsList.setModel(createDefaultListModel(Units.getUnitsForQuantity(dimension, unitSystem)));
		}
	}

	// actions for button and list selection events
	protected void changeSystemAction() {
		unitSystem = (String) unitSystemComboBox.getSelectedItem();
		String dimension = dimensionField.getText();
		Unit unitPicked = (Unit) unitsList.getSelectedValue();
//		List unitList = CUnit.createCUnitList(Units.getUnitsForQuantity(dimension, unitSystem));
//        unitsModel = new DefaultListModel();
//        unitsModel.ensureCapacity(unitList.size());
//        unitsModel.copyInto(unitList.toArray(new Object[unitList.size()]));
		unitsList.setModel(createDefaultListModel(Units.getUnitsForQuantity(dimension, unitSystem)));
		if (unitsModel.contains(unitPicked)) {
			unitsList.setSelectedValue(unitPicked, true);
		}
	}

	protected void commitAction() {

		String dimension = dimensionField.getText();
		//System.out.println("dimension: " + dimension);
		if (dimension.equals(Units.NO_DIMENSION)) {
			unitSelection = Quantity.NO_UNIT;
		} else {
			unitSelection = (Unit) (unitsList.isSelectionEmpty() ? unitsModel.get(0) : unitsList.getSelectedValue());
			//System.out.println("unitSelection: " + unitSelection);
		}
		dispose();
	}

	protected void cancelAction() {
		dispose();
	}

	public Unit getUnitSelection() {
		return unitSelection;
	}

	public static void main(String[] args) {
		DomeInit.loadUnits();
		Unit answer = UnitChooserDialog.showDialog(null, Quantity.NO_UNIT);
		if (answer == null)
			System.out.println("cancelled 1");
		else
			System.out.println("picked: " + answer);
		Unit answer2 = UnitChooser.showDialog(null, answer);
		if (answer2 == null)
			System.out.println("cancelled 2");
		else
			System.out.println("picked: " + answer2);
		System.exit(0);
	}

    protected DefaultListModel createDefaultListModel(List items) {
        DefaultListModel model = new DefaultListModel();
        for (Iterator i = items.iterator(); i.hasNext();) {
            model.addElement(i.next());
        }
        return model;
    }

    class UnitsListRenderer extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Unit unit = (Unit) value;
            String abbrev = unit.toString();
            if (abbrev.startsWith("<html>")) {
                abbrev = abbrev.substring(6, abbrev.length() - 7);
                setText("<html>" + UnitAtom.getUnitDescription(unit.toString()) + "    (" + abbrev +")</html>");
            } else {
                setText(UnitAtom.getUnitDescription(unit.toString()) + "    (" + abbrev+")");
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;

        }
    }
}
