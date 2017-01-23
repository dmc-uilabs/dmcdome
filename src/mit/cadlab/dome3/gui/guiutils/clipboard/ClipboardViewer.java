// ClipboardViewer.java
package mit.cadlab.dome3.gui.guiutils.clipboard;

//import mit.DHelp;

import mit.cadlab.dome3.swing.DFrame;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.WindowTracker;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

// ActionListener, ActionEvent

public class ClipboardViewer
{

	protected static GridBagConstraints gbc;

	public static final int CANCEL_OPTION = 0;
	public static final int SELECT_OPTION = 1;

	protected JPanel gui;
	protected int answer = 0;
	protected ClipboardTree cbTree;
	protected JButton emptyButton,deleteButton,closeButton; // view/edit clipboard options
	protected JButton pasteButton,cancelButton; // select from clipboard options

	public static DFrame createClipboardViewer(WindowTracker parent, Clipboard clipboard)
	{
		DFrame df = new DFrame("Clipboard Viewer", parent);
		df.getContentPane().add(new ClipboardViewer(clipboard, true).gui);
		df.pack();
		return df;
	}

	public static void showViewFrame(WindowTracker parent, Clipboard clipboard)
	{
		DFrame df = new DFrame("Clipboard Viewer", parent);
		df.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // should get rid of viewer?
		df.getContentPane().add(new ClipboardViewer(clipboard, true).gui);
		df.pack();
		df.show();
	}

	public static ClipboardSelection[] showSelectionDialog(Component parent, Clipboard clipboard)
	{
		ClipboardViewer selector = new ClipboardViewer(clipboard, false);
		JDialog d = DialogFactory.createDialog(parent, "Paste Options", selector.gui, true, true);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		switch (selector.answer) {
			case ClipboardViewer.SELECT_OPTION:
				return selector.cbTree.getSelectedClipboardSelections(); // returns null if nothing selected
			default: // cancel
				return null;
		}
	}

	private ClipboardViewer(Clipboard clipboard, boolean isEditable)
	{
		gui = makeClipboardViewerPanel(clipboard, isEditable);
		//DHelp.enableHelp(gui,DHelp.CLIPBOARD);
	}

	private JPanel makeClipboardViewerPanel(Clipboard clipboard, boolean isEditable)
	{
		JPanel p = new JPanel();
		cbTree = new ClipboardTree(clipboard, isEditable);
		JLabel selectionsLabel = Templates.makeLabel("clipboard selections:");
		JScrollPane selectionsScrollPane = new JScrollPane(cbTree);
		JPanel buttonPanel = isEditable ? createEditClipboardButtonPanel() : createSelectFromClipboardButtonPanel();

		JComponent[] comps = {selectionsLabel, selectionsScrollPane, buttonPanel};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 0), 0, 0)};

		Templates.layoutGridBagB(p, comps, gbcs);
		p.setPreferredSize(new Dimension(350, 300));
		return p;
	}

	private JPanel createEditClipboardButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		emptyButton = Templates.makeButton("empty", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				cbTree.deleteAllClipboardSelections();
			}
		});
		deleteButton = Templates.makeButton("delete", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				cbTree.deleteSelectedClipboardSelections();
			}
		});
		closeButton = Templates.makeButton("close", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				dispose();
			}
		});

		JComponent[] buttonComps = {emptyButton, deleteButton, closeButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] buttonGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 10), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
		Templates.layoutGridBag(buttonPanel, buttonComps, buttonGbcs);
		return buttonPanel;
	}

	private JPanel createSelectFromClipboardButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		pasteButton = Templates.makeButton("paste selection", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				answer = SELECT_OPTION;
				dispose();
			}
		});
		cancelButton = Templates.makeButton("cancel", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				answer = CANCEL_OPTION;
				dispose();
			}
		});

		JComponent[] buttonComps = {pasteButton, cancelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] buttonGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
		Templates.layoutGridBag(buttonPanel, buttonComps, buttonGbcs);
		return buttonPanel;
	}

	private void dispose()
	{
		SwingUtilities.windowForComponent(gui).dispose();
	}

}
