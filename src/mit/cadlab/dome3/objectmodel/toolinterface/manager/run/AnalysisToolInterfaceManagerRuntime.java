package mit.cadlab.dome3.objectmodel.toolinterface.manager.run;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.util.Regex;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.StringReader;
import java.util.List;
import java.util.Iterator;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 25, 2003
 * Time: 7:41:17 PM
 * To change this template use Options | File Templates.
 */
public class AnalysisToolInterfaceManagerRuntime extends AnalysisToolInterfaceManager
{
    public AnalysisToolInterfaceManagerRuntime(Model model)
	{
		super(new Id(UUIDGenerator.create()),(AnalysisTool) model);
	}

	public mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer loadInterface(String xmlContent)
	{
		return loadInterface(null, xmlContent, null);
	}

	public mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer loadInterface(CompoundId interfaceId, String xmlContent,
	                                                 String xmlMappings)
	{
		for (int i = 0; i < _interfaces.size(); i++) {
			mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer ifaceSvr = (mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer) _interfaces.get(i);
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
		mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer mi = new mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer(interfaceId, model, ifaceDoc.getRootElement(),
		                                                                                    (ifaceMaps == null) ? null : ifaceMaps.getRootElement());
		_interfaces.add(mi);
		return mi;
	}

	private static String getIdLine(String xmlContent)
	{
		List lines = Regex.split(Regex.whitespace, xmlContent);
		Iterator it = lines.iterator();
		while (it.hasNext()) {
			String line = (String) it.next();
			if (line.indexOf("id") != -1)
				return line;
		}
		return null;
	}

}
