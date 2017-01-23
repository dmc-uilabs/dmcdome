// AbstractModel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.model;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeListener;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.log.Log;
import mit.cadlab.dome3.util.log.LogHandler;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.util.DomeException;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;
import java.io.IOException;
import java.awt.*;

/**
 * Provides id generation, logging, versioning, and save support.
 * To do by subclasses:
 * public TypeId getTypeId();
 * public ModelObject getModelObjectById(Id id);
 */
public abstract class AbstractModel extends AbstractModelObjectScope implements Model
{

	protected LogHandler logHandler = Log.getDefaultLog(); // default, no logging
	protected Version version = new Version(0, 0, 1);
	protected Version lastSavedVersion = new Version(0, 0, 0);
	protected String lastSavedXml = "";

	public AbstractModel(Id id)
	{
		super(id);
	}

	public AbstractModel(Id id, Model m)
	{
		super(id, m);
	}

	public AbstractModel(Id id, Model m, boolean copyObjects)
	{
		super(id, m, copyObjects);
	}

	public AbstractModel(Element xmlElement)
	{
		super(xmlElement);
		// init
		parseModelInfoElement((Element) xmlElement.selectSingleNode("/model/modelinfo"));
	}

	public Model getModel()
	{
		return this;
	}

	// Model interface
	public LogHandler getLogHandler()
	{
		return logHandler;
	}

	public void setLogHandler(LogHandler log)
	{
		if (log == null)
			throw new AbstractDomeObject.DomeObjectException("setLogHandler", "null log handler");
		this.logHandler = log;
	}

	public Version getVersion()
	{
		return version;
	}

	public boolean isSaved()
	{
		try {
			Document xmlDoc = createXmlDocument();
			return !hasChanged(xmlDoc);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new AbstractDomeObject.DomeObjectException("isSaved", ex);
		}
	}

	public boolean hasChanged(Document modelDoc)
	{
		String xml = modelDoc.asXML();
		if (lastSavedXml.equals(xml))
			return false;
		return true;
	}

	public void save(String fileName)
	{
		Document xmlDoc = createXmlDocument();

		try {
			if (version.compareTo(lastSavedVersion) == 1) { // version is newer
				save(xmlDoc, fileName);
				lastSavedVersion = version.duplicate();
			} else { // change version if content is different
				if (hasChanged(xmlDoc)) {
					save(xmlDoc, fileName);
				} else { // increment version
					version.revSaveVersion();
					Element versionNode = (Element) xmlDoc.selectSingleNode("/model/modelinfo/version");
					versionNode.setText(version.toString());
					xmlDoc = createXmlDocument();
					save(xmlDoc, fileName);
					lastSavedVersion = version.duplicate();
				}
			}
		}
		catch(Exception e) {
			boolean exit = false;
			do {
				String msg1 = e.getMessage();
				String msg2 = "Do you want to try to save the model again?";
				int ans = TwoButton2Msg.showError(null, "Error: Save", msg2, msg1, "OK", "Cancel", new Dimension(1, 1));
				if (ans == TwoButton1Msg.LEFT_OPTION) {
					try {
						save(xmlDoc, fileName);
						exit = true;
						OneButton1Msg.showOption(null, "Model Save", "Model saved", "OK", new Dimension(1, 1));
					} catch (Exception ex) {
						exit = false;
					}
				}
				if (ans == TwoButton1Msg.RIGHT_OPTION) {
					OneButton1Msg.showOption(null, "Model Save", "Quitting without saving the Model", "OK", new Dimension(1, 1));
					exit = true;
				}
			} while (!exit);
		}
	}

    public void saveNoGui(String fileName) {
        Document xmlDoc = createXmlDocument();

        try {
            if (version.compareTo(lastSavedVersion) == 1) { // version is newer
                save(xmlDoc, fileName);
                lastSavedVersion = version.duplicate();
            } else { // change version if content is different
                if (hasChanged(xmlDoc)) {
                    save(xmlDoc, fileName);
                } else { // increment version
                    version.revSaveVersion();
                    Element versionNode = (Element) xmlDoc.selectSingleNode("/model/modelinfo/version");
                    versionNode.setText(version.toString());
                    xmlDoc = createXmlDocument();
                    save(xmlDoc, fileName);
                    lastSavedVersion = version.duplicate();
                }
            }
        } catch (Exception e) {
            throw new DomeException("Could not save file. " + e.getMessage());
        }
    }

	protected void save(Document xmlDoc, String fileName) throws IOException
	{
		XMLUtils.writeToFile(xmlDoc, fileName);
		lastSavedXml = xmlDoc.asXML();
	}

	public String getXmlTag()
	{
		return Model.XML_TAG;
	}

	// CausalitySupport interface
	protected abstract CausalityManager getCausalityManager();

	public List getItems(CausalityStatus causality)
	{
		return getCausalityManager().getItems(causality);
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		return getCausalityManager().isItemOfCausality(obj, causality);
	}

	public CausalityStatus getCausality(Object obj)
	{
		return getCausalityManager().getCausality(obj);
	}

	public void addCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(l);
	}

	public void removeCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(l);
	}

	public void addCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(obj, l);
	}

	public void removeCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(obj, l);
	}

	/**
	 * createsXMLElement and adds to Document
	 */

	protected abstract Document createXmlDocument();

	protected Element createModelInfoElement()
	{
		Element xml = DocumentHelper.createElement("modelinfo");
		xml.add(version.toXmlElement());
		return xml;
	}

	protected void parseModelInfoElement(Element xmlElement)
	{
		if (xmlElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml model info");
		XMLUtils.makeRootElement(xmlElement);
		Element versionXml = (Element) xmlElement.selectSingleNode("/modelinfo/version");
		if (versionXml == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml version: " + xmlElement.asXML());
		version = new Version();
	}

	protected String contentToString()
	{
		return "  version: " + version.toString();
	}

	protected void addXmlContent(Element xmlElement)
	{
		xmlElement.add(createModelInfoElement());
	}

}
