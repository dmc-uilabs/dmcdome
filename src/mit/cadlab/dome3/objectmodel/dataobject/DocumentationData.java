// DocumentationData.java
package mit.cadlab.dome3.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

public class DocumentationData extends AbstractDataObject implements Documentation
{

	protected String rtfText, plainText, url;

	public DocumentationData(Element xmlElement)
	{
		String xmltag = getXmlTag();
		String name = xmlElement.getQName().getName();
		if (xmlElement.getQName().getName().equals(getXmlTag())) {
			XMLUtils.makeRootElement(xmlElement); // necessary to use XPath locally
			List nodes = xmlElement.selectNodes("/documentation/text");
			rtfText = "";
			plainText = "";
			Iterator it = nodes.iterator();
			while (it.hasNext()) {
				Element textNode = (Element) it.next();
				String format = textNode.attributeValue("format");
				if (format.equals("rtf")) {
					rtfText = textNode.getText();
				} else if (format.equals("plain")) {
					plainText = textNode.getText();
				} else {
					throw new IllegalArgumentException("Documentation text - invalid format: " + format);
				}
			}
			Element urlNode = (Element) xmlElement.selectSingleNode("/documentation/url");
			if (urlNode == null) {
				url = "";
			} else {
				url = urlNode.getTextTrim();
				if (url == null)
					throw new IllegalArgumentException("Documentation url - invalid xml value: " + urlNode.asXML());
			}
		} else {
			throw new IllegalArgumentException("Documentation - illegal xmlElement: " + xmlElement.asXML());
		}
	}

	public DocumentationData()
	{
		rtfText = "";
		plainText = "";
		url = "";
	}

	public DocumentationData(String docText, String docURL)
	{
		if (isRtf(docText)) {
			rtfText = docText;
			this.plainText = "";
		} else {
			rtfText = "";
			this.plainText = docText;
		}
		this.url = docURL;
	}

	public DocumentationData(Documentation doc)
	{
		if (doc == null)
			throw new IllegalArgumentException("Documentation - null parameter");
		this.rtfText = doc.getRtfText();
		this.plainText = doc.getText();
		this.url = doc.getURL();
	}

	public boolean isCompatibleType(DataObject newObj)
	{
		return (newObj instanceof DocumentationData);
	}

	protected boolean isRtf(String text)
	{
		return true; // figure this out later
	}

	// DataObject interface
	protected TypeInfo getTypeInfo()
	{
		return Documentation.TYPE_INFO;
	}

	public DataObject duplicate()
	{
		return new DocumentationData(this);
	}

	protected PropertyChangeListener createValueShadowListener()
	{
		return null;
	}

	protected PropertyChangeListener createValueUnitShadowListener()
	{
		return null;
	}

	// Documentation interface
	public boolean isEmpty()
	{
		return rtfText.equals("") && plainText.equals("") && url.equals("");
	}

	public Documentation getDocumentation()
	{
		return this;
	}

	public String getRtfText()
	{
		return rtfText;
	}

	public void setRtfText(String text)
	{
		String oldText = rtfText;
		rtfText = text;
		firePropertyChange(RTF_TEXT, oldText, rtfText);
	}

	public String getText()
	{
		return plainText;
	}

	public void setText(String text)
	{
		String oldText = plainText;
		plainText = text;
		firePropertyChange(TEXT, oldText, plainText);
	}

	public String getURL()
	{
		return url;
	}

	public void setURL(String value)
	{
		String oldURL = url;
		url = value;
		firePropertyChange(URL, oldURL, url);
	}

	public String toString()
	{
		return "Documentation: " + ((url == null) ? "" : url) +
		        ((rtfText == null) ? "" : "\nrtfText: " + rtfText) +
		        ((plainText == null) ? "" : "\nplainText: " + plainText);
	}

	public String getXmlTag()
	{
		return Documentation.XML_TAG;
	}

	public Element toXmlElement()
	{
		Element doc = DocumentHelper.createElement(getXmlTag());
		if (rtfText != null && !rtfText.equals(""))
			doc.addElement("text").addAttribute("format", "rtf").addCDATA(rtfText);
		if (plainText != null && !plainText.equals(""))
			doc.addElement("text").addAttribute("format", "plain").addCDATA(plainText);
		if (url != null && !url.equals(""))
			doc.addElement("url").addText(url);
		return doc;
	}

}
