package com.ge.ceed.domeapi;

import java.io.IOException;

import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.xml.XMLUtils;

import org.dom4j.Element;

/**
 * IntegrationProjectBuilder violates several SOLID rules.
 * This is a wrapper kludge to work around unwanted GUI code in IntegrationProjectBuilder.save() and
 * to get at protected method IntegrationProjectBuilder.saveInterfacesAndIntegrationModels()
 * 
 * @author dliscomb
 *
 */
class CeedIntegrationProjectBuilder extends IntegrationProjectBuilder {
	
	public CeedIntegrationProjectBuilder(String fileName, Element xmlElement) {
		super(fileName, xmlElement);
	}

	public CeedIntegrationProjectBuilder(Id id) {
		super(id);
	}

	public CeedIntegrationProjectBuilder(Element xmlElement) {
		super(xmlElement);
	}
	// end ctors
	
	/**
	 * Does what {@link IntegrationProjectBuilder#save()} would do, but without GUI 
	 * @param fileName name of the file to save
	 * @throws IOException
	 */
	public void saveQuietly(String fileName) throws IOException {
		// Get the XML and save it
		Element currentXml = toXmlElement();
		if (version.compareTo(lastSavedVersion) > 0) { // version is newer
			lastSavedVersion = version.duplicate();
		}
		else { // change version if content is different
			if (!currentXml.asXML().equals(lastSavedXml)) { // increment version
				version.revSaveVersion();
				Element versionNode = (Element) currentXml.selectSingleNode("/project/projectinfo/version");
				versionNode.setText(version.toString());
				lastSavedVersion = version.duplicate();
			}
		}
		setFileName(fileName);
		XMLUtils.writeToFile(currentXml, fileName);
		saveInterfacesAndIntegrationModels();

	}

}