// DataObject.java
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.config.RegistrySupport;
import mit.cadlab.dome3.util.JavaBeanSupport;
import mit.cadlab.dome3.util.xml.XMLSupport;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * The DataObject interface is the base class for all DataTypes in DOME.
 */
public interface DataObject extends RegistrySupport, JavaBeanSupport, XMLSupport
{

	public static final String XML_TAG = "dataobject";
	public static final String VALUE = "value";
	public static final String RUNTIME_STATUS = "runStatus";

	public String getTypeName();

	/**
	 * Support for xmlType
	 */
	public String getXmlType();

	/**
	 * used for bean boxes
	 */
	public DataObject getDataObject();

	/**
	 * used to duplicate data
	 */
	public DataObject duplicate();

	public List getValues();

	public Object getValuesForXmlRpcUse ();

	public Unit getUnit();

	public void setValues(List values);

	public void setValues(DataObject newObj);

	public boolean isCompatibleType(DataObject newObj);

	public abstract PropertyChangeListener getValueShadowListener();

	public abstract PropertyChangeListener getValueUnitShadowListener();
}
