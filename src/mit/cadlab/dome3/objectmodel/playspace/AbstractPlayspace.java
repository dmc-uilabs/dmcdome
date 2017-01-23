/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 11, 2003
 * Time: 3:09:40 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.objectmodel.playspace;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.RuntimeObjectInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.objectmodel.dataobject.DocumentationData;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DomeJavaBean;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Iterator;

public abstract class AbstractPlayspace extends DomeJavaBean
{
	protected static final String XML_TAG = "playspace";
	protected static final String XML_TYPE = "playspace";
	public static final TypeInfo TYPE_INFO = new TypeInfo(XML_TYPE);

	protected String name;
	protected CompoundId runtimeId = new CompoundId();
	protected Version version = new Version();
	protected Documentation doc = new DocumentationData();
	protected String xmlDescription = "";

	protected RuntimeObjectInfo projectInfo = new RuntimeObjectInfo();
	protected RuntimeObjectInfo modelInfo = new RuntimeObjectInfo();
    protected RuntimeObjectInfo analysisToolInfo = new RuntimeObjectInfo();

	public AbstractPlayspace()
	{
	}

	public AbstractPlayspace(Element xmlElement)
	{
		xmlDescription = xmlElement.asXML();

		// read header
		if (xmlElement.getQName().getName().equals(XML_TAG)) {
			String type = xmlElement.attributeValue("type");
			if (type != null && !type.equals("") && !type.equals(XML_TYPE))
				throw new IllegalArgumentException(XML_TYPE + " - invalid xml type: " + type);
			String id = xmlElement.attributeValue("id");
			if (id == null)
				throw new IllegalArgumentException(XML_TYPE + " - no xml id");
			runtimeId.setPlayspaceStaticId(id);
			String name = xmlElement.attributeValue("name");
			if (name == null)
				throw new IllegalArgumentException(XML_TYPE + " - no xml name");
			this.name = name;
		} else {
			throw new IllegalArgumentException(XML_TYPE + " - illegal xmlElement: " + xmlElement.asXML());
		}

		// read version info
		Element versionElement = (Element) xmlElement.selectSingleNode("playspaceinfo/version");
		if (versionElement != null)
			version = new Version(versionElement);
		if (version == null)
			throw new IllegalArgumentException(XML_TYPE + " - no xml version: " + xmlElement.asXML());

		// read documentation
		Element docElement = (Element) xmlElement.selectSingleNode("documentation");
		if (docElement == null)
			doc = new DocumentationData();
		else
			doc = new DocumentationData(docElement);
	}


	protected String addStaticObjectInfo(Element xml, RuntimeObjectInfo info)
	{
		Id objId = XMLUtils.parseXmlRef(xml);
		String url = xml.attributeValue("url");
		String name = xml.attributeValue("name");
		String description = xml.attributeValue("description");

		info.addStaticInfo(objId.toString(), name, description, url);

		return objId.toString();
	}


	public void setName(String newName)
	{
		name = newName;
	}

	public String getName()
	{
		return name;
	}

	public String getXmlDescription()
	{
		Element xml = toXmlElement();
		xmlDescription = xml.asXML();
		return xmlDescription;
	}

	public String getStaticId()
	{
		return runtimeId.getPlayspaceStaticId();
	}

	public String getRuntimeId()
	{
		return runtimeId.getPlayspaceRuntimeId();
	}

	public CompoundId getCompoundId()
	{
		return new CompoundId(runtimeId);
	}

	public String getXmlTag()
	{
		return XML_TAG;
	}


	protected TypeInfo getTypeInfo()
	{
		return TYPE_INFO;
	}


	public Element toXmlElement()
	{
		// header
		Element xml = DocumentHelper.createElement(XML_TAG);
		xml.addAttribute("type", XML_TYPE);
		xml.addAttribute("id", runtimeId.getPlayspaceStaticId());
		xml.addAttribute("name", name);

		// version
		Element infoContainer = DocumentHelper.createElement("playspaceinfo");
		infoContainer.add(version.toXmlElement());
		xml.add(infoContainer);

		// add documentation
		Element docContainer = DocumentHelper.createElement("documentation");
		xml.add(doc.toXmlElement());
		xml.add(docContainer);

		// projects
		Element projectContainer = DocumentHelper.createElement("projects");
		for (Iterator projectIter = projectInfo.getAllStaticIds().iterator(); projectIter.hasNext();) {
			String projectId = (String) projectIter.next();
			Element projectElement = projectContainer.addElement(IntegrationProject.XML_TAG);
			projectElement.addAttribute("name", projectInfo.getName(projectId));
			projectElement.addAttribute("idRef", projectId);
			projectElement.addAttribute("url", projectInfo.getUrl(projectId));
			projectElement.addAttribute("description", projectInfo.getDescription(projectId));
		}
		xml.add(projectContainer);
		// models
		Element modelContainer = DocumentHelper.createElement("models");
		for (Iterator modelIter = modelInfo.getAllStaticIds().iterator(); modelIter.hasNext();) {
			String modelId = (String) modelIter.next();
			Element modelElement = modelContainer.addElement(DomeModelRuntime.XML_TAG);
			modelElement.addAttribute("name", modelInfo.getName(modelId));
			modelElement.addAttribute("idRef", modelId);
			modelElement.addAttribute("url", modelInfo.getUrl(modelId));
			modelElement.addAttribute("description", modelInfo.getDescription(modelId));
		}
		xml.add(modelContainer);

        // tools
		Element toolContainer = DocumentHelper.createElement("tools");
		for (Iterator toolIter = analysisToolInfo.getAllStaticIds().iterator(); toolIter.hasNext();) {
			String toolId = (String) toolIter.next();
			Element modelElement = toolContainer.addElement(OptimizationToolRuntime.XML_TAG);
			modelElement.addAttribute("name", analysisToolInfo.getName(toolId));
			modelElement.addAttribute("idRef", toolId);
			modelElement.addAttribute("url", analysisToolInfo.getUrl(toolId));
			modelElement.addAttribute("description", analysisToolInfo.getDescription(toolId));
		}
		xml.add(toolContainer);
		return xml;
	}


}
