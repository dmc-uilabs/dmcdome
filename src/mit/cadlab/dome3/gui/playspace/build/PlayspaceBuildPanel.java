// PlayspaceBuildPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace.build;

import mit.cadlab.dome3.gui.guiutils.msg.Saveable;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildPlayspaceFrame;
import mit.cadlab.dome3.gui.objectmodel.DomePlayspaceGui;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.playspace.EditPlayspaceTable;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Card for adding a removing items from a playspace during deployment
 */
public class PlayspaceBuildPanel extends JPanel implements DomePlayspaceGui, Saveable {

    public static final GridBagConstraints gbc = null;

    private JButton addButton;
    private JButton removeButton;
    private JPanel tableCard;
    private JTextField nameField;
    private JTextField filePathField;
    protected FileNameListener filenameListener = new FileNameListener();

    protected EditPlayspaceTable table;


    public PlayspaceBuildPanel(ServerConnection svrConn) {
        tableCard = new JPanel();
        tableCard.setLayout(new CardLayout2());
        table = new EditPlayspaceTable(svrConn);
        tableCard.add("editTable", table);
        ((CardLayout2) (tableCard.getLayout())).last(tableCard);
        table.addPropertyChangeListener(filenameListener);

        layoutGui();

    }

    public PlayspaceBuildPanel(ServerConnection svrConn, Element XMLDescription) {
        tableCard = new JPanel();
        tableCard.setLayout(new CardLayout2());

        table = new EditPlayspaceTable(svrConn, XMLDescription);
        tableCard.add("editTable", table);
        ((CardLayout2) (tableCard.getLayout())).last(tableCard);
        table.addPropertyChangeListener(filenameListener);

        layoutGui();
    }

    public PlayspaceBuildPanel(ServerConnection svrConn, String filename, Element XMLDescription) {
        tableCard = new JPanel();
        tableCard.setLayout(new CardLayout2());

        table = new EditPlayspaceTable(svrConn, filename, XMLDescription);

        tableCard.add("editTable", table);
        ((CardLayout2) (tableCard.getLayout())).last(tableCard);
        table.addPropertyChangeListener(filenameListener);

        layoutGui();
    }

    public void layoutGui() {
        JComponent[] comps = {makePanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

        Templates.layoutGridBag(this, comps, gbcs);
    }


    /**
     *
     * @param t pass the EditPlayspaceTable to show in the GUI
     */
    public void setTable(EditPlayspaceTable t) {
        tableCard.add("editTable", t);
        ((CardLayout2) (tableCard.getLayout())).last(tableCard);
        t.addPropertyChangeListener(filenameListener);

    }

    /**
     * use to set the file path once the PS has been saved, or on an open
     * @param txt
     */
    public void setFilePathField(String txt) {
        filePathField.setText(txt);
    }


    private JPanel makePanel() {
        JPanel p = new JPanel();
        JLabel nameLabel = Templates.makeLabel("name:");
        if (table.getPlayspaceName() == null || table.getPlayspaceName().length() == 0)
            nameField = Templates.makeDTextField("Dome Playspace");
        else
            nameField = Templates.makeDTextField(table.getPlayspaceName());
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                table.setPlayspaceName(nameField.getText());
                nameField.setBackground(Color.white);
                firePropertyChange(NameListener.NAME, null, nameField.getText());
            }
        });

        addButton = Templates.makeButton("add ...");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                table.showAddDialog();
                tableCard.validate();
            }
        });
        removeButton = Templates.makeButton("remove");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                table.deleteRow();
                tableCard.validate();
            }
        });

        filePathField = Templates.makeTextField("");
        filePathField.setEditable(false);

        ImageIcon labelIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/tree/playspaceReverse.gif");
        JLabel iconLabel = new JLabel(labelIcon, SwingConstants.LEFT);
        iconLabel.setOpaque(false);

        JComponent[] comps = {nameLabel, nameField, iconLabel, addButton, removeButton, tableCard, filePathField};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 3, 3, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }


    //Savable interface
    public void save(boolean closeAfterSave) {
        table.save();
        //	table.getPlayspace().setShouldSave(true);
    }

    public void saveAs(boolean closeAfterSave) {
        table.saveAs();
//		table.getPlayspace().setShouldSave(true);
    }
//DomeGui interface
    public String getTitle() {
        return nameField.getText();
    }

    public String getHelpContext() {
        return null;
    }

    public void setMenuContext() {
        MenuManager.setContext(ModeContexts.BUILD_PLAYSPACE);
    }

    protected static final Dimension NOT_SAVED_WARNING_SIZE = new Dimension(280, 90);

    public void close() {
        Component Parent = SwingUtilities.getRoot(this);
        if (Parent instanceof JFrame)
            WaitCursorUtils.setWaitCursor((JFrame) Parent, true);

//can ask whether it need to be saved
        if ((table.getPlayspace().getShouldSave()) && table.getPlayspace().isSaved()) return;
        int answer = TwoButton2Msg.showWarning(this, "Warning: unsaved changes",
                "has not been saved", table.getPlayspaceName(),
                "save now", "skip save", NOT_SAVED_WARNING_SIZE);
        switch (answer) {
            case TwoButton2Msg.LEFT_OPTION:
                // in case of Playspace it seems that there is no need to call deleteAllConcreteParameter(). Therefore closeAfterSave will be not ignored in save() method
                save(true);
                break;
            default: // skip save
                table.getPlayspace().setShouldSave(false);
                if (Parent instanceof JFrame)
                    WaitCursorUtils.setWaitCursor((JFrame) Parent, false);
                return;
        }
        if (Parent instanceof JFrame)
            WaitCursorUtils.setWaitCursor((JFrame) Parent, false);

    }


    public Object getGuiObject() {
        return table.getPlayspace();
    }




// --- focus tracking support --------------------
    public static abstract class FocusTrackerAction extends AbstractAction {

        public FocusTrackerAction(String name) {
            super(name);
        }

        /**
         * Determines the component to use for the action.
         * This if fetched from the source of the ActionEvent
         * if it's not null and can be narrowed.  Otherwise,
         * the last focused component is used.
         *
         * @param e the ActionEvent
         * @return the component
         */
        protected final DomeBuildPlayspaceFrame getDomeModelBuildFrame(ActionEvent e) {
            DomeBuildPlayspaceFrame playspaceFrame = BuildMode.getCurrentPlayspaceFrame();
            DomePlayspaceGui playspaceGui = playspaceFrame.getGui();
            if (playspaceGui instanceof PlayspaceBuildPanel)
                return playspaceFrame;
            throw new NullPointerException("No current DomeplayspaceFrame");
        }

        protected final PlayspaceBuildPanel getBuildPlayspaceGui(ActionEvent e) {
            DomeBuildPlayspaceFrame playspaceFrame = BuildMode.getCurrentPlayspaceFrame();
            DomePlayspaceGui playspaceGui = playspaceFrame.getGui();
            if (playspaceGui instanceof PlayspaceBuildPanel)
                return (PlayspaceBuildPanel) playspaceGui;
            throw new NullPointerException("No current PlayspaceBuildPanel");
        }
    }




    // --- actions for menus and buttons --------------------

    public static final AbstractAction saveAction = new FocusTrackerAction("Save") {
        public void actionPerformed(ActionEvent e) {
            getBuildPlayspaceGui(e).save(false);
        }
    };

    public static final AbstractAction saveAsAction = new FocusTrackerAction("Save as...") {
        public void actionPerformed(ActionEvent e) {
            getBuildPlayspaceGui(e).saveAs(false);
        }
    };

    public static final AbstractAction closeAction = new FocusTrackerAction("Close") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildFrame(e).selfClose();
        }
    };

    protected class FileNameListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            Object newValue = e.getNewValue();
            if (property.equals(EditPlayspaceTable.FILENAME)) {
                setFilePathField((String) newValue);
            }
            if (property.equals(NameListener.NAME)) {
                firePropertyChange(NameListener.NAME, null, newValue);
                if (nameField.getText().equals(newValue.toString())) return;
                nameField.setText(newValue.toString());

            }
        }
    }
}
