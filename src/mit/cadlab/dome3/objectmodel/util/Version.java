// Version.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util;

import mit.cadlab.dome3.util.xml.XMLSupport;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.StringTokenizer;

public class Version implements XMLSupport, Comparable
{

	private static final String XML_TAG = "version";

	protected int[] version = {0, 0, 0};

	public Version(Element xmlElement)
	{
		if (xmlElement.getQName().getName().equals(XML_TAG)) {
			String vString = xmlElement.getText();
			StringTokenizer st = new StringTokenizer(vString, ".");
			if (st.countTokens() != 3) {
				throw new IllegalArgumentException("Version - invalid xml version: " + xmlElement.asXML());
			}
			try {
				version[0] = Integer.parseInt(st.nextToken());
				version[1] = Integer.parseInt(st.nextToken());
				version[2] = Integer.parseInt(st.nextToken());
			} catch (Exception ex) {
				throw new IllegalArgumentException("Version - invalid xml verions: " + xmlElement.asXML());
			}
		} else {
			throw new IllegalArgumentException("Version - illegal xmlElement: " + xmlElement.asXML());
		}
	}

	public Version()
	{
		// use default values
	}

	public Version(int majorRev, int minorRev, int saveRev)
	{
		version[0] = majorRev;
		version[1] = minorRev;
		version[2] = saveRev;
	}

	public Version duplicate()
	{
		return new Version(version[0], version[1], version[2]);
	}

	public int getMajorVersion()
	{
		return version[0];
	}

	public int getMinorVersion()
	{
		return version[1];
	}

	public double getMajorMinorVersion()
	{
		return Double.parseDouble(version[0] + "." + version[1]);
	}

	public int getSaveVersion()
	{
		return version[2];
	}

	public void revMajorVersion()
	{
		if (version[0] == Integer.MAX_VALUE) {
			throw new UnsupportedOperationException("Version overflow error: " + this.toString());
		} else {
			version[0]++;
			version[1] = 0;
			version[2] = 0;
		}
	}

	public void revMinorVersion()
	{
		if (version[1] == Integer.MAX_VALUE) {
			revMajorVersion();
		} else {
			version[1]++;
			version[2] = 0;
		}
	}

	public void revSaveVersion()
	{
		if (version[2] == Integer.MAX_VALUE) {
			revMinorVersion();
		} else {
			version[2]++;
		}
	}

	public String toString()
	{
		return version[0] + "." + version[1] + "." + version[2];
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof Version) {
			Version v2 = (Version) obj;
			return this.getMajorVersion() == v2.getMajorVersion() &&
			        this.getMinorVersion() == v2.getMinorVersion() &&
			        this.getSaveVersion() == v2.getSaveVersion();
		} else {
			return false;
		}
	}

	// Comparable interface

	public int compareTo(Object obj)
	{
		if (obj instanceof Version) {
			Version v2 = (Version) obj;
			int majorCompare = new Integer(this.getMajorVersion()).compareTo(new Integer(v2.getMajorVersion()));
			if (majorCompare == 0) {
				int minorCompare = new Integer(this.getMinorVersion()).compareTo(new Integer(v2.getMinorVersion()));
				if (minorCompare == 0) {
					return new Integer(this.getSaveVersion()).compareTo(new Integer(v2.getSaveVersion()));
				} else {
					return minorCompare;
				}
			} else {
				return majorCompare;
			}
		} else {
			throw new ClassCastException("can not compare Version to " + obj);
		}
	}

	public String getXmlTag()
	{
		return XML_TAG;
	}

	public Element toXmlElement()
	{
		return DocumentHelper.createElement(XML_TAG).addText(this.toString());
	}

}
