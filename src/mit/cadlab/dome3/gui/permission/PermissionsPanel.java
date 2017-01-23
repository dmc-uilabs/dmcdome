// PermissionsPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.permission;

import mit.cadlab.dome3.gui.servermode.usergroup.MutableUserGroupListModel;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.swing.DList;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * This gui component is used for setting permissions on objects in DOME.
 */
public class PermissionsPanel {

    public static final Dimension DEFAULT_SIZE = new Dimension(400, 400);
    public static final Dimension DEFAULT_ERROR_SIZE = new Dimension(300, 200);
    public static final GridBagConstraints gbc = null;

    private ServerConnection svrConn;
    private String objectName;

    private DArrayList userGroupWithPermissions;      //store the information of users or groups
    private DArrayList remainingUsersAndGroups = null; // initialized when add button is clicked


    private Vector permLabels = new Vector();      //Permission Label componets
    private Vector permBoxes = new Vector();       //Permission CheckBox componets
    private Vector permTypes;      //Permission types
    private Vector permDepd;      //Permission dependencies

    private JPanel gui;
    private JButton addButton, removeButton;
    private int whichBox = 0;
    private DList permUGList;


    /**
     * Creates panel which allows one to configure a particular type of permission on an object.
     * @param parent the parent component to this panel
     * @param svrConn the ServerConnection to the server the object is on
     * @param permissionCategory permission category from PermissionUtils
     * @param objectId the id for the object we are setting the permissions on
     * @param objectName the name for the object we are setting the permissions on
     * @param isObjectOnServer true if object is already on server; false otherwise
     * @return the PermissionsPanel (which can be shown again or queried for the set permissions)
     */
    public static PermissionsPanel showPanel(Component parent, ServerConnection svrConn, String permissionCategory,
                                             String objectId, String objectName, boolean isObjectOnServer) {
        PermissionsPanel p = null;
        try {
            p = new PermissionsPanel(svrConn, permissionCategory, objectId, objectName, isObjectOnServer, true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        JDialog d = DialogFactory.createDialog(parent, "Permissions for " + objectName, p.gui, true, true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setSize(DEFAULT_SIZE);
        d.show();
        return p;
        //return p.answer;
    }

    /**
     * Shows this permissions dialog in a dialog again.
     */
    public void show(Component parent) {
        JDialog d = DialogFactory.createDialog(parent, "Permissions for " + objectName, gui, true, true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setSize(DEFAULT_SIZE);
        d.show();
    }


    public JPanel getGui() {
        return gui;
    }

    /**
     * @return data structure containing users and which permissions they have set (userId,permissionId)
     */
    public Vector getSetPermissions() {
        //System.out.println("In PermissionsPanel.getSetPermissions");
        Vector setPermissions = new Vector();
        //System.out.println(userGroupWithPermissions.size());
        for (int i = 0; i < userGroupWithPermissions.size(); i++) {
            UserGroupInfo ugInfo = (UserGroupInfo) userGroupWithPermissions.get(i);
            //System.out.println(ugInfo.getPermissionID().length);
            for (int j = 0; j < ugInfo.getPermissionID().length; j++) {
                if (ugInfo.getPermissions()[j] == true) {
                    Vector temp = new Vector();
                    temp.addElement(new Integer(ugInfo.getId()));
                    temp.addElement(new Integer(ugInfo.getPermissionID()[j]));
                    setPermissions.addElement(temp);
                    //System.out.println("Temp: " + temp);
                }
            }
        }
        //System.out.println("SetPermissions: " + setPermissions);
        return setPermissions;
    }

    public PermissionsPanel(ServerConnection svrConn, String permissionCategory, String objectId,
                            String objectName, boolean isObjectOnServer, boolean showOkCancelButtons) {
        this.svrConn = svrConn;
        this.objectName = objectName;

        initializeData(permissionCategory, objectId, isObjectOnServer);
        gui = makePanel(showOkCancelButtons);
    }

    private void initializeData(String permissionCategory, String objectId, boolean isObjectOnServer) {
        // get the permission category information
        Vector permissionInfo = PermissionUtils.getCategoryInfo(svrConn, permissionCategory);
        if (permissionInfo == null) {
            OneButton1Msg.showError(gui, "Permissions error", "Invalid permission category: " + permissionCategory, "OK", DEFAULT_ERROR_SIZE);
            return;
        }
        permTypes = (Vector) permissionInfo.get(0);
        permDepd = (Vector) permissionInfo.get(1);

        // get permissions for object information
        if (isObjectOnServer) {
            Vector objPerms = PermissionFunctions.getObjectPermissionInfo(svrConn, objectId, permissionCategory);
            Vector usersAndGroups = (Vector) objPerms.get(0);
            Vector permissions = (Vector) objPerms.get(1);
            userGroupWithPermissions = PermissionUtils.convertToUserGroupInfo(permTypes, permissions, usersAndGroups);
            Collections.sort(userGroupWithPermissions); // alphabetical order, users first
        } else {
            Vector allUserGroup = UserGroupFunctions.getSimpleActiveUsersAndGroupsList(svrConn);
            remainingUsersAndGroups = new DArrayList(PermissionUtils.convertToUserGroupInfo(permTypes, allUserGroup));
            //get session user from the connection
            String userName = svrConn.getLoginName();
            UserGroupInfo userInfo = null;
            for (int i = 0; i < remainingUsersAndGroups.size(); i++) {
                UserGroupInfo info = (UserGroupInfo) remainingUsersAndGroups.get(i);
                if (info.getName().equals(userName)) {
                    userInfo = info;
                    break;
                }
            }
            if (userInfo != null) {
                remainingUsersAndGroups.remove(userInfo);
                userGroupWithPermissions = new DArrayList();
                boolean[] oldPermission = userInfo.getPermissions();

                //set permission
                for (int j = 0; j < oldPermission.length; j++) {
                    oldPermission[j] = true;
                }
                userGroupWithPermissions.add(userInfo);


            }
            if (remainingUsersAndGroups.isEmpty()) {
                addButton.setEnabled(false);
            }
        }


    }

    private JPanel makePanel(boolean showOkCancelButtons) {
        JPanel p = new JPanel();
        // add GUI items here
        JLabel userGroupsLabel = Templates.makeLabel("user or groups:");
        permUGList = Templates.makeDList(new MutableUserGroupListModel(userGroupWithPermissions));
        permUGList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userGroupScroll = new JScrollPane(permUGList);

        JScrollPane permissionsScroll = new JScrollPane(makePermissionPanel());
        JSplitPane dataPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        dataPane.add(userGroupScroll, JSplitPane.TOP);
        dataPane.add(permissionsScroll, JSplitPane.BOTTOM);
        dataPane.setDividerLocation(180);

        permUGList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {

                    if (permUGList.getSelectedIndex() == -1) {
                        //No selection, disable button.
                        removeButton.setEnabled(false);
                        for (int i = 0; i < permBoxes.size(); i++) {
                            ((JCheckBox) permBoxes.elementAt(i)).setEnabled(false);
                            ((JCheckBox) permBoxes.elementAt(i)).setSelected(false);
                        }

                    } else {
                        //Selection, update text field.
                        removeButton.setEnabled(true);
                        //int i = PermissionUtils.getUserGroup(allUserGroup, permUGList.getSelectedValue().toString());
                        for (int j = 0; j < permBoxes.size(); j++) {
                            ((JCheckBox) permBoxes.elementAt(j)).setEnabled(true);
                            ((JCheckBox) permBoxes.elementAt(j)).setSelected(((UserGroupInfo) permUGList.getSelectedValue()).getPermissions()[j]);
                        }

                    }
                }
            }
        });

        //format
        JComponent[] comps = null;
        GridBagConstraints[] gbcs = null;
        if (showOkCancelButtons) {
            comps = new JComponent[]{userGroupsLabel, makeAddRemovePanel(), dataPane, makeOKPanel()};
            // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
            gbcs = new GridBagConstraints[]{
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
                new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
                new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
            };
        } else {
            comps = new JComponent[]{userGroupsLabel, makeAddRemovePanel(), dataPane};
            // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
            gbcs = new GridBagConstraints[]{
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
                new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0)
            };
        }
        Templates.layoutGridBag(p, comps, gbcs);

        if (userGroupWithPermissions.size() == 0) {
            removeButton.setEnabled(false);
            for (int i = 0; i < permBoxes.size(); i++) {
                ((JCheckBox) permBoxes.elementAt(i)).setEnabled(false);
                ((JCheckBox) permBoxes.elementAt(i)).setSelected(false);
            }
        } else {
            permUGList.setSelectedIndex(0);
        }

        return p;
    }

    private JPanel makePermissionPanel() {
        JPanel p = new JPanel();

        JLabel permissionTitle = Templates.makeLabel("permissions", Templates.FONT11I);
        JLabel allowTitle = Templates.makeLabel("allow", Templates.FONT11I);

        JComponent[] comps = new JComponent[permTypes.size() * 2 + 3];
        comps[0] = permissionTitle;
        comps[1] = allowTitle;
        GridBagConstraints[] gbcs = new GridBagConstraints[permTypes.size() * 2 + 3];
        gbcs[0] = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[1] = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0);

        for (int i = 0; i < permTypes.size(); i++) {
            permLabels.addElement(Templates.makeLabel(((Vector) permTypes.elementAt(i)).elementAt(1).toString()));
            permBoxes.addElement(Templates.makeCheckBox());
            comps[i * 2 + 2] = (JComponent) permLabels.elementAt(i);
            comps[i * 2 + 3] = (JComponent) permBoxes.elementAt(i);
            gbcs[i * 2 + 2] = new GridBagConstraints(0, i + 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
            gbcs[i * 2 + 3] = new GridBagConstraints(1, i + 1, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0);

        }
        int lastIndex = permTypes.size() * 2 + 2;
        JPanel fill = new JPanel();
        comps[lastIndex] = fill;
        gbcs[lastIndex] = new GridBagConstraints(0, lastIndex, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0);

        Templates.layoutGridBag(p, comps, gbcs);

        for (whichBox = 0; whichBox < permBoxes.size(); whichBox++) {
            ((JCheckBox) permBoxes.elementAt(whichBox)).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int currentBox = permBoxes.indexOf(e.getSource());
                    ((UserGroupInfo) permUGList.getSelectedValue()).getPermissions()[currentBox] =
                            ((JCheckBox) permBoxes.elementAt(currentBox)).isSelected();
                    //handle permission dependencies

                    for (int m = 0; m < permDepd.size(); m++) {
                        if (((UserGroupInfo) permUGList.getSelectedValue()).getPermissionID()[currentBox] ==
                                ((Integer) ((Vector) permDepd.elementAt(m)).elementAt(0)).intValue()) {
                            int dependedIndex = PermissionUtils.getArrayIndex(((UserGroupInfo) permUGList.getSelectedValue()).getPermissionID(),
                                    ((Integer) ((Vector) permDepd.elementAt(m)).elementAt(1)).intValue());
                            if (dependedIndex != -1) {
                                if (((JCheckBox) permBoxes.elementAt(currentBox)).isSelected() == true) {
                                    ((UserGroupInfo) permUGList.getSelectedValue()).getPermissions()[dependedIndex] = true;
                                    ((JCheckBox) permBoxes.elementAt(dependedIndex)).setSelected(true);
                                    ((JCheckBox) permBoxes.elementAt(dependedIndex)).setEnabled(false);
                                } else {
                                    ((JCheckBox) permBoxes.elementAt(dependedIndex)).setEnabled(true);
                                }
                            }
                        }
                    }

                }
            });
        }

        return p;
    }

    private JPanel makeOKPanel() {
        JPanel p = new JPanel();
        JButton okButton = Templates.makeButton("ok", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        okButton.setMnemonic(KeyEvent.VK_O);
        JButton cancelButton = Templates.makeButton("cancel", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        cancelButton.setMnemonic(KeyEvent.VK_C);

        JComponent[] comps = {okButton, cancelButton};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    private JPanel makeAddRemovePanel() {
        JPanel p = new JPanel();
        addButton = Templates.makeButton("add", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (remainingUsersAndGroups == null) { // get from database
                    Vector allUserGroup = UserGroupFunctions.getSimpleActiveUsersAndGroupsList(svrConn);
                    remainingUsersAndGroups = new DArrayList(DSet.removeSet(PermissionUtils.convertToUserGroupInfo(permTypes, allUserGroup),
                            userGroupWithPermissions));
                    if (remainingUsersAndGroups.isEmpty()) {
                        // show dialog
                        addButton.setEnabled(false);
                        return;
                    }
                }
                Collections.sort(remainingUsersAndGroups); // put into alphabetical order; users first
                List selectedNames = UserGroupChooser.showDialog(PermissionsPanel.this.gui, remainingUsersAndGroups);
                if (selectedNames != null) {
                    if (selectedNames.size() != 0) {
                        for (int i = 0; i < selectedNames.size(); i++) {
                            UserGroupInfo item = (UserGroupInfo) selectedNames.get(i);
                            boolean[] perms = item.getPermissions();
                            for (int j = 0; j < perms.length; j++) {
                                perms[j] = true;
                            }
                        }
                        remainingUsersAndGroups.removeAll(selectedNames);
                        userGroupWithPermissions.addAll(selectedNames);
                        Collections.sort(userGroupWithPermissions); // put into alphabetical order; users first
                        permUGList.repaint();
                        Object firstItem = selectedNames.get(0);
                        int index = userGroupWithPermissions.indexOf(firstItem);
                        permUGList.setSelectedIndex(index); // start at the first one being added into list
                        addButton.setEnabled(!remainingUsersAndGroups.isEmpty());
                        removeButton.setEnabled(true); // be sure it is enabled
                    }
                }
            }
        });
        addButton.setMnemonic(KeyEvent.VK_A);

        removeButton = Templates.makeButton("remove", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = permUGList.getSelectedIndex();
                //clear the permissions of the selected item
                boolean[] perms = ((UserGroupInfo) permUGList.getSelectedValue()).getPermissions();
                for (int j = 0; j < perms.length; j++) {
                    perms[j] = false;
                }
                Object userOrGroup = userGroupWithPermissions.remove(index);
                remainingUsersAndGroups.add(userOrGroup);

                if (userGroupWithPermissions.isEmpty()) {
                    removeButton.setEnabled(false);
                    for (int i = 0; i < permBoxes.size(); i++) {
                        ((JCheckBox) permBoxes.elementAt(i)).setEnabled(false);
                        ((JCheckBox) permBoxes.elementAt(i)).setSelected(false);
                    }
                } else {
                    //Adjust the selection. last component select nothing after removing an item.
                    if (index == userGroupWithPermissions.size()) //removed item in last position
                        index--;
                    permUGList.setSelectedIndex(index);   //otherwise select same index
                }
                addButton.setEnabled(!remainingUsersAndGroups.isEmpty());
            }
        });
        removeButton.setMnemonic(KeyEvent.VK_R);

        JComponent[] comps = {addButton, removeButton};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    private void dispose() {
        SwingUtilities.windowForComponent(gui).dispose();
    }

}
