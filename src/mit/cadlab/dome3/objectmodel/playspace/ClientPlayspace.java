package mit.cadlab.dome3.objectmodel.playspace;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 4, 2003
 * Time: 3:30:45 PM
 * To change this template use Options | File Templates.
 */
public abstract class ClientPlayspace extends AbstractPlayspace
{
	protected ServerConnection serverConnection = null;
	protected Version version = new Version(0, 0, 1);
	protected Version lastSavedVersion = new Version(0, 0, 0);
	protected String lastSavedXml = "";

	/**
	 * Create a transient playspace.
	 * @param serverConnection Server connection
	 */
	public ClientPlayspace(ServerConnection serverConnection)
	{
		this.serverConnection = serverConnection;
	}

	/**
	 * Create a playspace from an XML description. Runtime playspaces are supplied an id from the server.
	 */
	public ClientPlayspace(ServerConnection serverConnection, Element xmlElement)
	{
		super(xmlElement);
		this.serverConnection = serverConnection;
	}

	public Element toXmlElement()
	{
		return super.toXmlElement();
	}

	public Version getVersion()
	{
		return version;
	}

	public boolean isSaved()
	{
		try {
			Document xmlDoc = XMLUtils.createXmlDocument(toXmlElement());
			return !hasChanged(xmlDoc);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean hasChanged(Document playspaceDoc)
	{
		String xml = playspaceDoc.asXML();
		if (lastSavedXml.equals(xml))
			return false;
		return true;
	}

	protected void save(Document xmlDoc, String fileName)
	{
		try {
			XMLUtils.writeToFile(xmlDoc, fileName);
			lastSavedXml = xmlDoc.asXML();
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}


	public void save(String fileName)
	{
		Document xmlDoc = XMLUtils.createXmlDocument(toXmlElement());

		if (version.compareTo(lastSavedVersion) == 1) { // version is newer
			save(xmlDoc, fileName);
			lastSavedVersion = version.duplicate();
		} else { // change version if content is different
			if (hasChanged(xmlDoc)) {
				save(xmlDoc, fileName);
			} else { // increment version
				version.revSaveVersion();
				Element versionNode = (Element) xmlDoc.selectSingleNode("playspace/playspaceinfo/version");
				versionNode.setText(version.toString());
				xmlDoc = XMLUtils.createXmlDocument(toXmlElement());
				save(xmlDoc, fileName);
				lastSavedVersion = version.duplicate();
			}
		}
	}
}
