// DomeObject.java
package mit.cadlab.dome3.objectmodel;

import mit.cadlab.dome3.config.RegistrySupport;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.JavaBeanSupport;
import mit.cadlab.dome3.util.xml.XMLSupport;
import org.dom4j.Element;

/**
 * DomeObjects have unique IDs assigned at object creation.
 * All DomeObjects support user-friendly name.
 * DomeObjects also support bound properties via JavaBeanSupport interface.
 * DomeObjects also support the notion of notifying others when they are
 *   no longer being used via the Destructor interface
 */
public interface DomeObject extends Destructor, RegistrySupport, JavaBeanSupport, XMLSupport
{

	public static final String NAME = "name";
	public static final String XML_DESCRIPTION = "xmlDescription";

	/**
	 * @return written representation of type of DomeObject
	 */
	public String getTypeName();

	/**
	 * Support for optional xmlType
	 */
	public String getXmlType();

	/**
	 * used for bean boxes
	 */
	public DomeObject getDomeObject();

	/**
	 * @return ID of DomeObject
	 */
	public Id getId();

	/**
	 * @return name of DomeObject
	 */
	public String getName();

	/**
	 * @param name new name of DomeObject
	 */
	public void setName(String name);

	/**
	 * @return name and id of object as String
	 */
	public String getNameIdString();

	/**
	 * Support for xml references
	 */
	public Element toXmlRef();

	/**
	 * return only the header portion (e.g. tag, name, id) of the object's xml representation.
	 * @return
	 */
	public Element headerToXmlElement();
}
