// ModelInterfaceManager.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelinterface.manager;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.AbstractDomeModelInterface;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.util.Iterator;

public class ModelInterfaceManagerRuntime extends ModelInterfaceManager
{

	public ModelInterfaceManagerRuntime(Model model)
	{
		super(new Id(UUIDGenerator.create()), model);
	}

	/**
	 * only allow one interface to be created at a time so that the first interface sets values in the model
	 */
	public synchronized ModelInterfaceRuntimeServer loadInterface(CompoundId interfaceId, String xmlContent,
	                                                 String xmlMappings)
	{
		for (int i = 0; i < interfaces.size(); i++) {
			ModelInterfaceRuntimeServer ifaceSvr = (ModelInterfaceRuntimeServer) interfaces.get(i);
			if (ifaceSvr.getRuntimeId().getInterfaceStaticId().equals(interfaceId.getInterfaceStaticId()))
				return ifaceSvr;
		}
		SAXReader reader = new SAXReader();
		Document ifaceDoc = null, ifaceMaps = null;
		// parse the iface description and mappings
		try {
			ifaceDoc = reader.read(new StringReader(xmlContent));
			if (xmlMappings == null || xmlMappings.equals("")) {
				// no mappings provieded
			} else {
				ifaceMaps = reader.read(new StringReader(xmlMappings));
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		// create the interface
		ModelInterfaceRuntimeServer mi;
		mi = new ModelInterfaceRuntimeServer(interfaceId, model, ifaceDoc.getRootElement(),
		                                     (ifaceMaps == null) ? null : ifaceMaps.getRootElement());
		interfaces.add(mi);
		return mi;
	}

    /*
     * cleanup() is moved from her to ModelInterfaceManager
     * just use the implementation in ModelInterfaceManager
     */
}