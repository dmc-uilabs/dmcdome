package Permissions;

import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.util.DArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Feb 24, 2003
 * Time: 11:33:23 AM
 * To change this template use Options | File Templates.
 */

/**
 * This class displays users and groups for people to choose.
 */
public class UserGroupChooser extends JDialog
{
	public static final Dimension DEFAULT_SIZE = new Dimension(250, 250);
	public static final GridBagConstraints gbc = null;
	private static UserGroupChooser dialog;
	private static PermissionListModel listModel;
	private static JList list;
	private JButton cancelButton;
	private static JButton addButton;
	private static Vector SelectedNames = new Vector();

	public static void initialize(Component comp,
	                              String title,
	                              String labelText, Vector allUG, PermissionListModel permlistModel)
	{
		Frame frame = JOptionPane.getFrameForComponent(comp);
		dialog = new UserGroupChooser(frame, title, labelText, allUG, permlistModel);
		dialog.setSize(DEFAULT_SIZE);
	}

	/**
	 * Show the initialized dialog.  The first argument should
	 * be null if you want the dialog to come up in the center
	 * of the screen.  Otherwise, the argument should be the
	 * component on top of which the dialog should appear.
	 */
	public static Vector showDialog(Component comp)
	{
		if (dialog != null) {
			SelectedNames.clear();
			//listModel.clear();

			if (listModel.getSize() == 0) {
				addButton.setEnabled(false);
			}
			else {
				list.setSelectedIndex(0);
				addButton.setEnabled(true);
			}

			dialog.setLocationRelativeTo(comp);

			dialog.setVisible(true);
		}
		else {
			System.err.println("ListDialog requires you to call initialize "
			        + "before calling showDialog.");
		}
		return SelectedNames;
	}


	private UserGroupChooser(Frame frame, String title,
	                         String labelText, Vector allUG, PermissionListModel permlistModel)
	{
		super(frame, title, true);


		JPanel p = new JPanel();
		JLabel userGroupsLabel = Templates.makeLabel(labelText);
		listModel = new PermissionListModel(makeListModel(allUG, permlistModel));
		list = Templates.makeDList(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2) {
					addButton.doClick();
				}
			}
		});
		JScrollPane listScroller = new JScrollPane(list);


		JComponent[] comps = {userGroupsLabel, listScroller, makeOKPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
		};


		Templates.layoutGridBag(p, comps, gbcs);
		getContentPane().add(p);

		pack();
	}

	private JPanel makeOKPanel()
	{
		JPanel p = new JPanel();
		addButton = Templates.makeButton("add", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object[] SelectedUG;
				SelectedUG = list.getSelectedValues();
				for (int i = 0; i < SelectedUG.length; i++) {
					SelectedNames.addElement(SelectedUG[i]);
				}
				dispose();
				//ListDialog.dialog.setVisible(false) ;
			}
		});
		addButton.setMnemonic(KeyEvent.VK_A);
		cancelButton = Templates.makeButton("cancel", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
				//ListDialog.dialog.setVisible(false);
			}
		});
		cancelButton.setMnemonic(KeyEvent.VK_C);
		getRootPane().setDefaultButton(addButton);

		JComponent[] comps = {addButton, cancelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	protected DArrayList makeListModel(Vector allUG, PermissionListModel permlistModel)
	{
		Vector result = new Vector();
		boolean permitted;          //if being in the PermlistModel

		for (int i = 0; i < allUG.size(); i++) {
			permitted = false;
			for (int j = 0; j < permlistModel.size(); j++) {
				if (((UserGroupInfo) allUG.elementAt(i)).getId() == ((UserGroupInfo) permlistModel.getElementAt(j)).getId()) {
					permitted = true;
				}

			}

			if (permitted == false) {
				result.addElement(allUG.elementAt(i));
			}
		}

		return new DArrayList(result);
	}


}

