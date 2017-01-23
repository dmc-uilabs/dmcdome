package playspaceRun;

import mit.cadlab.dome.DomeInit;
import mit.cadlab.dome.gui.fileSystem.DomeFile;
import mit.cadlab.dome.gui.mode.run.RunMode;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.connection.LoginUtils;
import mit.cadlab.dome.network.server.DomeServer;
import mit.cadlab.dome.server.db.DbUtils;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 30, 2003
 * Time: 6:19:55 PM
 * To change this template use Options | File Templates.
 */
public class PlayspaceRunTest {

	public static void main(String[] args) {
		DomeInit.initializeDOME();

		try {
			DbUtils.setDbUrl(9001);
			ServerConnection conn = getServerConnection(LoginUtils.USER, "Charles",
			                                            DomeServer.getDefaultServerPort(), "123");
			DomeFile df = new DomeFile(DomeFile.PLAYSPACE_TYPE, "ad064030-b6c1-1004-8482-c6b06c94fe4d", "Dome Playspace", "", "", 1);

			//System.out.println(df.toString());
			//System.out.println(conn.toString());
			PlayspaceRun pr = new PlayspaceRun(conn, df);
			pr.pack();
			pr.show();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("problem");
		}
	}

	private static ServerConnection getServerConnection(String type, String user, String svrPort, String password) {
		ServerConnection m_serverConnection;
		m_serverConnection = LoginUtils.login(type, user, RunMode.getClientUrl(), svrPort, LoginUtils.encryptPassword(password));
		return m_serverConnection;
	}
}
