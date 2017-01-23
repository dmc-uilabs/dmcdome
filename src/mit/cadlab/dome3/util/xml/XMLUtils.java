// XMLUtils.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util.xml;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.network.server.Debug;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import java.util.Comparator;
import java.util.Map;

public class XMLUtils
{

	protected static final XMLWriter writer = createXMLWriter();

	protected static XMLWriter createXMLWriter()
	{
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setIndentSize(4);
		try {
			return new XMLWriter(System.out, format);
		} catch (UnsupportedEncodingException e) {
			throw new NullPointerException();
		}
	}

	public static void print(Node n)
	{
		try {
			writer.write(n);
			System.out.println();
		} catch (IOException e) {
			System.out.println("\nXMLUtils.print error: " + e + "\n\t" + n);
		}
	}

	public static String toPrettyString(Node n)
	{
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setIndentSize(4);
			StringWriter sw = new StringWriter();
			XMLWriter w = new XMLWriter(sw, format);
			w.write(n);
			return sw.toString();
		} catch (IOException e) {
			throw new RuntimeException("\nXMLUtils.print error: " + e + "\n\t" + n);
		}
	}

	public static void makeRootElement(Element xmlElement)
	{
		if (xmlElement.getDocument() != null) {
			xmlElement.detach();
		}
		Document doc = DocumentHelper.createDocument();
		doc.add(xmlElement);
	}

	public static void addCollection(Element xmlElement, String xmlTag, Collection objs)
	{
		if (objs == null || objs.isEmpty())
			return;
		Element xml = xmlElement;
		if (xmlTag != null && xmlTag.length() != 0)
			xml = xmlElement.addElement(xmlTag);
		Iterator it = objs.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof XMLSupport) {
				xml.add(((XMLSupport) obj).toXmlElement());
			} else {
				System.err.println("xml error - unable to create xml for " + obj);
			}
		}
	}

	public static void addCollectionRef(Element xmlElement, String xmlTag, Collection objs)
	{
		if (objs == null || objs.isEmpty())
			return;
		Element xml = xmlElement;
		if (xmlTag != null && xmlTag.length() != 0)
			xml = xmlElement.addElement(xmlTag);
		Iterator it = objs.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof DomeObject) {
				xml.add(((DomeObject) obj).toXmlRef());
			} else {
				System.err.println("xml error - unable to create xml ref for " + obj);
			}
		}
	}

	public static void addStringCollection(Element xmlElement, String xmlTag, String itemTag, Collection objs)
	{
		if (objs == null || objs.isEmpty())
			return;
		Element xml = xmlElement;
		if (xmlTag != null && xmlTag.length() != 0)
			xml = xmlElement.addElement(xmlTag);
		Iterator it = objs.iterator();
		while (it.hasNext()) {
			xml.addElement(itemTag).addText(it.next().toString());
		}
	}

	public static List parseStringCollection(Element xmlElement, String xmlTag, String itemTag)
	{
		List l = new ArrayList();
		if (xmlElement == null)
			return l;
		XMLUtils.makeRootElement(xmlElement);
		List entries = xmlElement.selectNodes("/" + xmlTag + "/" + itemTag);
		if (entries == null || entries.isEmpty())
			return l;
		for (int i = 0; i < entries.size(); i++) {
			Element element = (Element) entries.get(i);
			l.add(element.getText());
		}
		return l;
	}

	/**
	 * @return the new xml element created or the original xml element if none created
	 */
	public static Element addStringMap(Element xmlElement, String xmlTag, String entryName, String keyName, String valueName, Map objs)
	{
		if (objs == null || objs.isEmpty())
			return xmlElement;
		Element xml = xmlElement;
		if (xmlTag != null && xmlTag.length() != 0)
			xml = xmlElement.addElement(xmlTag);
		List keys = new ArrayList(objs.keySet());
		Collections.sort(keys, new DomeObjectComparator());
		Iterator it = keys.iterator();
		Object key, value;
		String keyString, valueString;
		while (it.hasNext()) {
			key = it.next();
			value = objs.get(key);
			keyString = (key instanceof DomeObject) ? ((DomeObject) key).getId().getIdString() : key.toString();
			valueString = (value instanceof DomeObject) ? ((DomeObject) value).getId().getIdString() : value.toString();
			xml.addElement(entryName).addAttribute(keyName, keyString).addAttribute(valueName, valueString);
		}
		return xml;
	}

	public static HashMap parseStringMap(Element xmlElement, String xmlTag, String entryName, String keyName, String valueName)
	{
		HashMap map = new HashMap();
		if (xmlElement == null)
			return map;
		XMLUtils.makeRootElement(xmlElement);
		List entries = xmlElement.selectNodes("/" + xmlTag + "/" + entryName);
		if (entries == null || entries.isEmpty())
			return map;
		Element entry;
		String key, value;
		for (int i = 0; i < entries.size(); i++) {
			entry = (Element) entries.get(i);
			key = entry.attributeValue(keyName);
			value = entry.attributeValue(valueName);
			map.put(key, value);
		}
		return map;
	}

	public static Hashtable parseStringTable(Element xmlElement, String xmlTag, String entryName, String keyName, String valueName)
	{
		Hashtable map = new Hashtable();
		if (xmlElement == null)
			return map;
		XMLUtils.makeRootElement(xmlElement);
		List entries = xmlElement.selectNodes("/" + xmlTag + "/" + entryName);
		if (entries == null || entries.isEmpty())
			return map;
		Element entry;
		String key, value;
		for (int i = 0; i < entries.size(); i++) {
			entry = (Element) entries.get(i);
			key = entry.attributeValue(keyName);
			value = entry.attributeValue(valueName);
			map.put(key, value);
		}
		return map;
	}

	public static class DomeObjectComparator implements Comparator
	{
		/**
		 * Null objects are at the end of the list sorted by this comparator.
		 * Non-Dome objects are also at the end of the list.
		 * @param o1
		 * @param o2
		 * @return
		 */
		public int compare(Object o1, Object o2)
		{
			if ((o1==null) && (o2==null))
				return 0;
			if (o1==null)
				return 1;
			if (o2==null)
				return -1;
			if (o1 instanceof DomeObject && o2 instanceof DomeObject)
				return ((DomeObject) o1).getId().getIdString().compareTo(((DomeObject) o2).getId().getIdString());
			if (o1 instanceof DomeObject)
				return -1;
			if (o2 instanceof DomeObject)
				return 1;
			return o1.toString().compareTo(o2.toString());
		}
	}

	/**
	 * Write xml to file. If node is an Element, creates a default document and prints that to file.
	 * @param n
	 * @param fileName
	 * @throws IOException
	 */
	public static void writeToFile(Node n, String fileName) throws IOException {
		writeToFile(n, new File(fileName));
	}

	/**
	 * Write xml to file. If node is an Element, creates a default document and prints that to file.
	 * @param n
	 * @param file
	 * @throws IOException
	 */
	public static void writeToFile(Node n, File file) throws IOException
	{
			n = createXmlDocument(n); // ensure there is a document
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setIndentSize(4);
			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
			writer.write(n);
			writer.close();
	}

	public static Id parseXmlRef(Element xmlElement)
	{
		String idString = xmlElement.attributeValue("idRef");
		if (idString == null)
			throw new IllegalArgumentException("no xml idRef: " + xmlElement.asXML());
		return new Id(idString);
	}

	public static Element fileToXmlElement(String fileName)
	{
		return fileToXmlElement(new File(fileName));
	}

	public static Element fileToXmlElement(File file)
	{
		SAXReader reader = new SAXReader();
		Document xmlDoc = null;
		try {
			xmlDoc = reader.read(file);
			return xmlDoc.getRootElement();
		} catch (DocumentException e) {
			Debug.trace (Debug.ALL, e.toString());
		} catch (MalformedURLException e) {
            Debug.trace (Debug.ALL, e.toString());
		}
		return null;
	}

	public static Element stringToXmlElement(String xmlContent)
	{
		SAXReader reader = new SAXReader();
		Document xmlDoc = null;
		try {
			xmlDoc = reader.read(new StringReader(xmlContent));
			return xmlDoc.getRootElement();
		} catch (DocumentException e) {
			System.err.println(e);
		}
		return null;
	}

	/**
	 * If the node is in a document, returns document for node.
	 * Otherwise, places node in document and returns document.
	 * @param xmlNode
	 * @return
	 */
	public static Document createXmlDocument(Node xmlNode)
	{
		if (xmlNode.getDocument() != null)
			return xmlNode.getDocument();
		Document doc = DocumentFactory.getInstance().createDocument();
		doc.add(xmlNode);
		return doc;
	}
}
