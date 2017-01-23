// MessageLogDialog.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.MessageArea;
import mit.cadlab.dome3.swing.MessageScrollPane;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.objectmodel.model.dome.DomeModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.plugin.PluginModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.run.ModelInterfaceRunPanel;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MessageLogDialog extends JDialog
{
	protected static GridBagConstraints gbc;
	protected static final String ERROR = "error";
	protected static final String WARNING = "warning";
	protected static final String INFO = "info";
	protected static final String TEST = "test2";
	protected static final String TITLE = "Message Log";
	protected static List formats = makeMessageFormatsList();
	protected static Dimension DEFAULT_SIZE = new Dimension(400, 200);

	protected MessageArea messageArea;
	protected MessageScrollPane messageScrollPane;
	protected JCheckBox scrollCheckBox;
	protected Component parent;
	protected NameListener nameListener;
	protected Model model;

	public MessageLogDialog(Component parent)
	{
		super((JFrame) SwingUtilities.windowForComponent(parent), TITLE, false);
		getContentPane().add(makeMessageAreaPanel());
		setSize(getPreferredSize());
		this.parent = parent;
		if (parent instanceof DomeModelBuildPanel) {
			model = ((DomeModelBuildPanel) parent).getModel();
		} else if (parent instanceof PluginModelBuildPanel) {
			model = ((PluginModelBuildPanel) parent).getModel();
		} else if (parent instanceof ModelInterfaceRunPanel) {
			ModelInterface face = ((ModelInterfaceRunPanel) parent).getModelInterface();
			nameListener = new NameListener()
			{
				public void nameChanged(String newName)
				{
					setTitle("Message Log: " + newName);
				}
			};
			face.addPropertyChangeListener(NameListener.NAME, nameListener);
			setTitle("Message Log: " + face.getName());
		}
		if (model != null) {
			nameListener = new NameListener()
			{
				public void nameChanged(String newName)
				{
					setTitle("Message Log: " + newName);
				}
			};
			model.addPropertyChangeListener(NameListener.NAME, nameListener);
			setTitle("Message Log: " + model.getName());
		}
		setLocationRelativeTo(parent);
	}

    /**
     * added for memory leakage debugging
     */
    public void clearMessages() {
        messageArea.clearAllMessages();
    }

	public void addError(String msg)
	{
		messageArea.addMessage(ERROR, msg);
	}

	public void addWarning(String msg)
	{
		messageArea.addMessage(WARNING, msg);
	}

	public void addInfo(String msg)
	{
		messageArea.addMessage(INFO, msg);
	}

	public void addTestInfo(String msg)
	{
		messageArea.addMessage(TEST, msg);
	}

	private static List makeMessageFormatsList()
	{
		List formats = new ArrayList();
		Font font = new Font("Dialog", Font.PLAIN, 12);
		formats.add(new MessageArea.MessageFormat(ERROR, Color.red, font));
		formats.add(new MessageArea.MessageFormat(WARNING, Color.green.darker(), font));
		formats.add(new MessageArea.MessageFormat(INFO, Color.gray, font));
		formats.add(new MessageArea.MessageFormat(TEST, Color.blue, font));
		return formats;
	}

	private JPanel makeMessageAreaPanel()
	{
		JPanel p = new JPanel();
		messageArea = new MessageArea(formats);
		messageScrollPane = new MessageScrollPane(messageArea);
		scrollCheckBox = Templates.makeCheckBox("scroll", messageScrollPane.isAutoScrollOn());
		scrollCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		scrollCheckBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				messageScrollPane.setAutoScrollOn(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		// Button Panel
		JButton clearAllButton = Templates.makeButton("clear all", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				messageArea.clearAllMessages();
			}
		});
		JButton clearBeforeSelectionButton = Templates.makeButton("clear before selection", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				messageArea.clearMessagesBeforeSelection();
			}
		});
		JButton closeButton = Templates.makeButton("close", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				hide();
			}
		});
		JPanel buttonPanel = new JPanel();
		JComponent[] buttonComps = {clearAllButton, clearBeforeSelectionButton, closeButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] buttonGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(buttonPanel, buttonComps, buttonGbcs);

		JComponent[] comps = {scrollCheckBox, messageScrollPane, buttonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(p, comps, gbcs);
		p.setPreferredSize(DEFAULT_SIZE);
		return p;
	}

}
