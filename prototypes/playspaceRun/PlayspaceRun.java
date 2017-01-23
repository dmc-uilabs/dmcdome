package playspaceRun;

import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.connection.LoginUtils;
import mit.cadlab.dome.gui.fileSystem.DomeFile;
import mit.cadlab.dome.DomeInit;
import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.server.db.DbUtils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import conference.ConferenceClient;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 30, 2003
 * Time: 2:51:58 PM
 * To change this template use Options | File Templates.
 */
public class PlayspaceRun extends JFrame{
	public static final GridBagConstraints gbc = null;
	private PlayspaceRunFileSystemTable prfst;
	private JButton conferenceButton = new JButton("Conference");
	private JButton submitButton = new JButton("Submit");

	private String psName;
	private String clientName;
	private String clientPort = "9000";
    private String serverPort = "8082";


	public PlayspaceRun(ServerConnection conn, DomeFile playspaceStaticInfo){
		super(playspaceStaticInfo.getName());
		psName = playspaceStaticInfo.getName();
		clientName = conn.getLoginName();

		init(conn, playspaceStaticInfo);

	}

	private void init(ServerConnection conn, DomeFile playspaceStaticInfo){

		JPanel p = new JPanel();
		prfst = new PlayspaceRunFileSystemTable(conn, playspaceStaticInfo);

		conferenceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ConferenceClient clientWebServer = new ConferenceClient(Integer.parseInt(clientPort), Integer.parseInt(serverPort));
				clientWebServer.createConferenceGUI(clientPort, serverPort, clientName, psName);
			}
		});
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prfst.getClientPlayspaceRuntime().submitChanges();
			}
		});

		JComponent[] comps = {conferenceButton, prfst, submitButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setPreferredSize(new Dimension(400,300));
		this.getContentPane().add(p);

	}

}
