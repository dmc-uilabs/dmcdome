package mit.cadlab.dome3.gui.conference;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.ConferenceClientFunctions;
import mit.cadlab.dome3.swing.MessageArea;
import mit.cadlab.dome3.swing.MessageScrollPane;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;


/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 30, 2003
 * Time: 11:39:32 AM
 * To change this template use Options | File Templates.
 */
public class ConferencePanel extends JPanel {
    public static final GridBagConstraints gbc = null;

    public static final String ENTER = "Enter";
    public static final String LEAVE = "Leave";
    public static final String MSG = "Playspace Member";

    private MessageArea _messageArea;
    private ServerConnection _svrConn;
    private String _clientName, _playspaceId;
    private JLabel _memberLabel, _onlineLabel, _transcriptLabel;
    private JScrollPane _memberPane, _onlinePane, _newMessagePane;
    private MessageScrollPane _transcriptPane;
    private JButton submitButton;
    private JList _currentMembers;
    private DefaultListModel _onLineMembers;
    private JTextArea _messageTextArea;
    private JCheckBox _scrollBox;

    public ConferencePanel(String playspaceId, ServerConnection svrConn) {
        this._clientName = svrConn.getLoginName();
        this._playspaceId = playspaceId;
        this._svrConn = svrConn;

        JComponent[] comps = {makePanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(this, comps, gbcs);
    }

    private JPanel makePanel() {
        JPanel p = new JPanel();

        this._memberLabel = Templates.makeLabel("members:");
        this._memberPane = new JScrollPane();
        this._memberPane.setBackground(Color.WHITE);
        this._onlineLabel = Templates.makeLabel("currently online:");
        this._onlinePane = new JScrollPane();
        this._onlinePane.setBackground(Color.WHITE);

        this._transcriptLabel = Templates.makeLabel("conference transcript:");

        this._messageArea = new MessageArea(ConferencePanel.makeMessageFormatsList());
        this._transcriptPane = new MessageScrollPane(this._messageArea);
        this._transcriptPane.setBackground(Color.WHITE);

        this._newMessagePane = new JScrollPane();
        this._newMessagePane.setBackground(Color.WHITE);
        this._messageTextArea = new JTextArea();
        this._messageTextArea.setEditable(true);
        this._messageTextArea.setFont(Templates.FONT11);
        this._messageTextArea.setAlignmentY(TOP_ALIGNMENT);
        this._messageTextArea.setLineWrap(true);
        this._newMessagePane.setViewportView(this._messageTextArea);

        submitButton = Templates.makeButton("submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = ConferencePanel.this._messageTextArea.getText();
                ConferencePanel.this.sendPublicMessage(message);
                ConferencePanel.this._messageTextArea.setText("");
            }
        });
        ImageIcon icon = Templates.makeImageIcon("mit/cadlab/dome3/icons/conference.gif");
        JLabel iconLabel = new JLabel(icon, SwingConstants.LEFT);
        iconLabel.setOpaque(false);

        JComponent[] comps = {this._memberLabel, this._memberPane, this._onlineLabel, this._onlinePane, this._transcriptLabel,
                              this._transcriptPane, this._newMessagePane, iconLabel, submitButton};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.BOTH, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(2, 5, 0, 0), 100, 0),
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(2, 5, 0, 0), 100, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 3, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 70),
            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
        };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    public void initializeConferenceGUI(Vector memberNames, Vector membersOnLine) {
        JList names = Templates.makeList(memberNames);
        this._onLineMembers = new DefaultListModel();
        for (int i = 0; i < membersOnLine.size(); i++) {
            this._onLineMembers.addElement(membersOnLine.elementAt(i));
        }
        this._currentMembers = Templates.makeList(this._onLineMembers);
        this._memberPane.setViewportView(names);
        this._onlinePane.setViewportView(this._currentMembers);
    }

    public void updateOnLinePane(String name) {
       //Qing add here: July 25th 03 to fix  bug:" Each time a person clicks on conference they are added to the list of members on-line."
        if (!this._onLineMembers.contains(name))
            this._onLineMembers.addElement(name);

    }

    public void removeMember(String name) {
        this._onLineMembers.removeElement(name);
    }

    public void setTranscriptText(String author, String text, String messageType) {
        DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT);
        if (messageType.equals(ConferencePanel.ENTER)) {
            this._messageArea.addMessage(messageType, "Administrator (" + time.format(new Date()) + ") :   " + text);
        }
        if (messageType.equals(ConferencePanel.MSG)) {
            this._messageArea.addMessage(messageType, "" + author + "(" + time.format(new Date()) + ") :   " + text);
        }
        if (messageType.equals(ConferencePanel.LEAVE)) {
            this._messageArea.addMessage(messageType, "Administrator (" + time.format(new Date()) + ") :   " + text);
        }
    }

    public void sendPublicMessage(String message) {
        ConferenceClientFunctions.sendMessageToEveryone(this._svrConn, message, this._playspaceId);
    }

    public static List makeMessageFormatsList() {
        List formats = new ArrayList();
        formats.add(new MessageArea.MessageFormat(LEAVE, Color.red, Templates.FONT11B));
        formats.add(new MessageArea.MessageFormat(ENTER, Color.blue, Templates.FONT11B));
        formats.add(new MessageArea.MessageFormat(MSG, Color.black, Templates.FONT11));
        return formats;
    }

}
