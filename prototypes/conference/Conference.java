package conference;

import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.swing.MessageArea;
import mit.cadlab.dome.swing.MessageScrollPane;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JViewport;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Vector;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.sql.Time;
import java.text.DateFormat;


/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 30, 2003
 * Time: 11:39:32 AM
 * To change this template use Options | File Templates.
 */
public class Conference extends JPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(600, 400);
	public static final GridBagConstraints gbc = null;

	protected static final String ENTER = "Enter";
	protected static final String LEAVE = "Leave";
	protected static final String MSG = "Playspace Member";

	private MessageArea _messageArea;
	private ConferenceServerConnection _cfu;
	private String _clientName, _playspaceName;
	private JLabel _memberLabel, _onlineLabel, _transcriptLabel;
	private JScrollPane _memberPane, _onlinePane, _newMessagePane;
	private MessageScrollPane  _transcriptPane;
	private JButton submitButton;
	private JList _currentMembers;
	private DefaultListModel _onLineMembers;
	private JTextArea _messageTextArea;
	private JCheckBox _scrollBox;

	public Conference(JFrame f, String playspaceName, String clientName, ConferenceServerConnection cfu)
	{
		this._clientName = clientName;
		this._playspaceName = playspaceName;
		this._cfu = cfu;
		f.setTitle("Conference for playspace: "+playspaceName);
		f.setSize(DEFAULT_SIZE);

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

		this._messageArea = new MessageArea(Conference.makeMessageFormatsList());
		this._transcriptPane = new MessageScrollPane(this._messageArea);
		this._transcriptPane.setBackground(Color.WHITE);

		this._newMessagePane = new JScrollPane();
		this._newMessagePane.setBackground(Color.WHITE);
		this._messageTextArea = new JTextArea();
		this._messageTextArea.setEditable(true);
		this._messageTextArea.setAlignmentY(TOP_ALIGNMENT);
		this._messageTextArea.setLineWrap(true);
		this._newMessagePane.setViewportView(this._messageTextArea);

		submitButton = Templates.makeButton("submit");
		submitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String message = Conference.this._messageTextArea.getText();
				Conference.this.sendPublicMessage(message);
				Conference.this._messageTextArea.setText("");
			}
		});
		ImageIcon icon = Templates.makeImageIcon("mit/cadlab/dome/icons/conference.gif");
		JLabel iconLabel = new JLabel(icon, SwingConstants.LEFT);
		iconLabel.setOpaque(false);

		JComponent[] comps = {this._memberLabel, this._memberPane, this._onlineLabel, this._onlinePane, this._transcriptLabel,
		                        this._transcriptPane, this._newMessagePane, iconLabel, submitButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.BOTH, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(2, 5, 0, 0), 150, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(2, 5, 0, 0), 150, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 3, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 100),
			new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public void initializeConferenceGUI(Vector memberNames, Vector membersOnLine)
	{
		JList names = Templates.makeList(memberNames);
		this._onLineMembers = new DefaultListModel();
		for(int i=0; i<membersOnLine.size(); i++)
		{
			this._onLineMembers.addElement(membersOnLine.elementAt(i));
		}
		this._currentMembers = Templates.makeList(this._onLineMembers);
		this._memberPane.setViewportView(names);
		this._onlinePane.setViewportView(this._currentMembers);
	}
	public void updateOnLinePane(String name)
	{
		this._onLineMembers.addElement(name);

	}
	public void removeMember(String name)
	{
		this._onLineMembers.removeElement(name);
	}
	public void setTranscriptText(String author, String text, String messageType)
	{
		DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT);
		if(messageType.equals(Conference.ENTER))
		{
			this._messageArea.addMessage(messageType, "Administrator (" + time.format(new Date()) + ") :   " + text);
		}
		if(messageType.equals(Conference.MSG))
		{
			this._messageArea.addMessage(messageType, "" + author + "(" + time.format(new Date()) + ") :   " + text);
		}
		if(messageType.equals(Conference.LEAVE))
		{
			this._messageArea.addMessage(messageType, "Administrator (" + time.format(new Date()) + ") :   " + text);
		}
	}
	public void sendPublicMessage(String message)
	{
		ConferenceClientFunctions.sendMessageToEveryone(this._cfu, this._clientName,  message, this._playspaceName);
	}
	public static List makeMessageFormatsList()
	{
		List formats = new ArrayList();
		formats.add(new MessageArea.MessageFormat(LEAVE, Color.red, Templates.FONT11B));
		formats.add(new MessageArea.MessageFormat(ENTER, Color.blue, Templates.FONT11B));
		formats.add(new MessageArea.MessageFormat(MSG, Color.black, Templates.FONT11));
		return formats;
	}

//	public static void main(String[] args)
//	{
//		JFrame f = new JFrame();
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		f.getContentPane().add(new Conference(f, "myPlayspaceName"));
//		f.show();
//	}
}
