// AuxFile.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles;

import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.dom4j.Element;

/**
 *  
 */
public interface AuxFile extends ModelComponent, DomeObject
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("Auxfile");
	public static final String XML_TAG = "auxfile";

	//interfaces of ModelComponenent :

	public Model getModel();

	//interfaces of DomeObject  :
	public String getTypeName();

	public String getXmlType();

	public DomeObject getDomeObject();

	public Id getId();

	public String getName();

	public void setName(String name);

    public String getNameIdString();

	public Element toXmlRef();

	public Element headerToXmlElement();


}
