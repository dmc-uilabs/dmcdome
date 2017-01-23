// CustomGuiPicker.java
package mit.cadlab.dome3.gui.guiutils.customGui;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.swing.DList;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Used for selecting dimension and unit for parameters.
 */
public class CustomGuiPicker extends JDialog
{

	protected static Dimension DEFAULT_SIZE = new Dimension(200, 300);

	// use one of four static methods to show UnitChooser
	public static void showDialog(JComponent comp, ModelInterface mInterface)
	{
		CustomGuiPicker chooser = new CustomGuiPicker(comp, mInterface);
		chooser.show();
	}

    public static void showDialog(JComponent comp, ToolInterface tInterface)
    {
        CustomGuiPicker chooser = new CustomGuiPicker(comp, tInterface);
        chooser.show();
    }

	// instance variables
	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
	protected static String title = "Edit Custom GUI";
	JButton removeButton,doneButton,editButton;
	DList guiList;

	ModelInterface mInterface = null;

    ToolInterface _tInterface = null;

	protected CustomGuiPicker(Component comp, ModelInterface mInterface)
	{
		super(JOptionPane.getFrameForComponent(comp), title + " from interface:" + mInterface.getName(), true); // interface
		this.mInterface = mInterface;
		//super(JOptionPane.getFrameForComponent(comp), title, true); // interface
		createComponents(comp);
	}

    protected CustomGuiPicker(Component comp, ToolInterface tInterface)
    {
        super(JOptionPane.getFrameForComponent(comp), title + " from interface: " + tInterface.getName(), true);
        _tInterface = tInterface;
        createComponents(comp);
    }

    protected void createComponents(Component comp)
    {
        Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(makeChooser(), BorderLayout.CENTER);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(comp);
    }

	protected JPanel makeChooser()
	{
		JPanel p = new JPanel();
		ActionListener actionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object source = e.getSource();
				if (source == removeButton) {
					delAction();
				}
				if (source == editButton) {
					editAction();
				}
				else if (source == doneButton) {
					dispose();
				}
			}
		};

		// create Buttons
		removeButton = Templates.makeButton("remove", actionListener);
		removeButton.setEnabled(false);
		doneButton = Templates.makeButton("done", actionListener);
		editButton = Templates.makeButton("edit...", actionListener);
		editButton.setEnabled(false);
		//doneButton.setPreferredSize(removeButton.getPreferredSize());

		guiList = Templates.makeDList(new CustomGUIComboBoxModel(mInterface, false));
		guiList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		guiList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (guiList.getSelectedIndex() == -1 || guiList.getSelectedIndex() == 0) {
					if (removeButton.isEnabled()) removeButton.setEnabled(false);
					if (editButton.isEnabled()) editButton.setEnabled(false);
				}
				else {
					if (!removeButton.isEnabled()) removeButton.setEnabled(true);
					if (!editButton.isEnabled()) editButton.setEnabled(true);

				}
			}
		});

		JScrollPane listScroller = new JScrollPane(guiList);
		// component array for GridBagLayout
		JComponent[] comps = {listScroller,
		                      editButton,
		                      removeButton,
		                      doneButton
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
		};

		Templates.layoutGridBagB(p, comps, gbcs);
		p.setPreferredSize(DEFAULT_SIZE);
		return p;
	}


	protected void delAction()
	{
		if (guiList.getSelectedIndex() == -1 || guiList.getSelectedIndex() == 0) return;

		CustomGuiInfo fileInfo = (CustomGuiInfo) guiList.getSelectedValue();
		if (mInterface != null)
            mInterface.removeCustomGui(fileInfo);
        if (_tInterface != null)
            _tInterface.removeCustomGui(fileInfo);

		//	repaint
        if (mInterface != null)
		    guiList.setModel(new CustomGUIComboBoxModel(mInterface, false));
        if (_tInterface != null)
            guiList.setModel(new CustomGUIComboBoxModel(_tInterface, false));
		guiList.setSelectedIndex(-1);

	}

	protected void editAction()
	{
		if (guiList.getSelectedIndex() == -1 || guiList.getSelectedIndex() == 0) {
			return;
		}
		CustomGuiInfo fileInfo = (CustomGuiInfo) guiList.getSelectedValue();

        if (mInterface != null)
        {
            CustomGuiEditor.showDialog(null, mInterface, fileInfo);
            guiList.setModel(new CustomGUIComboBoxModel(mInterface, false));
        }
        if (_tInterface != null)
        {
            CustomGuiEditor.showDialog(null, _tInterface, fileInfo);
            guiList.setModel(new CustomGUIComboBoxModel(_tInterface, false));
        }
		//	repaint
		guiList.setSelectedIndex(-1);
	}

	public static void main(String[] args)
	{

		CustomGuiPicker.showDialog(null, (ModelInterface) null);

	}


}
