package editPlayspace;

import mit.cadlab.dome.network.client.connection.LoginUtils;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.server.db.DbUtils;

import javax.swing.*;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.apache.xmlrpc.XmlRpcException;


/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 13, 2003
 * Time: 6:20:15 PM
 * To change this template use Options | File Templates.
 */
public class EditPlayspaceTest extends JFrame  {


	static JFrame f = new JFrame();
	static EditPlayspaceTable table;
	private static ServerConnection m_serverConnection;

	public static void main(String[] args) {

		DbUtils.setDbUrl(9001);
		m_serverConnection = getServerConnection(LoginUtils.USER, "root", "localhost:8080", "cadlab");
		//table = new EditPlayspaceTable(m_serverConnection);
		table = new EditPlayspaceTable(null);
		/*try{
			table.initEditPlayspaceTable("0");
		}catch(Exception e){
			e.printStackTrace();
		}*/

		f.getContentPane().add(table, BorderLayout.SOUTH);
		f.getContentPane().add(makeControlPanel(), BorderLayout.NORTH);
		f.pack();
		f.setVisible(true);
	}

	private static JPanel makeControlPanel() {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());


		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.showAddDialog();
				f.show();
				f.repaint();
			}
		});

		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.deleteRow();
				f.show();
				f.repaint();
			}
		});

		JButton dumpXMLButton = new JButton("Dump XML");
		dumpXMLButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.dumpXML();
			}
		});

		p.add(addButton);
		p.add(removeButton);
		p.add(dumpXMLButton);
		return p;
	}

	private static ServerConnection getServerConnection(String type, String user, String svrPort, String password) {
		ServerConnection m_serverConnection = LoginUtils.login(type, user, svrPort, LoginUtils.encryptPassword(password));
		return m_serverConnection;
	}


}
