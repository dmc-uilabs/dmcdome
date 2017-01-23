package test.model;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksConfiguration;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksModelRuntime;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 3, 2003
 * Time: 6:10:38 PM
 * To change this template use Options | File Templates.
 */
public class RunSolidworksModel
{
	public static DomeFileChooser fileChooser = new DomeFileChooser();

	public static SolidworksModelRuntime loadSolidworksModel() {
		DomeFileChooser fc = new DomeFileChooser();
		String localFileName = fc.showOpenDialog(null, SolidworksConfiguration.TYPE_INFO.getTypeName());
		if (localFileName == null)
			return null; // cancelled
		// construct the model and a frame for it
		Element modelElement = XMLUtils.fileToXmlElement(localFileName);
		SolidworksModelRuntime model = new SolidworksModelRuntime(new CompoundId(), modelElement, false);
		return model;
	}

	public static ModelInterfaceRuntimeServer loadInterfaceForModel(SolidworksModelRuntime model) {
		String localFileName = fileChooser.showOpenDialog(null,DomeFileChooser.DOME_INTERFACE_FILTER);
		String interfaceContent = null;
		String mappingsContent = null;
		if (localFileName == null)
			return null; // cancelled
		try {
			interfaceContent = FileUtils.readTextFileAsString(localFileName);
		}
		catch (FileNotFoundException e) {
			System.err.println(e);
			return null;
		}
		try {
			mappingsContent = FileUtils.readTextFileAsString(localFileName.substring(0, localFileName.length() - 4) +
			        "-mappings");
		}
		catch (FileNotFoundException e) {
			// ignore
		}
		return ((ModelInterfaceManagerRuntime) model.getModelInterfacesManager()).loadInterface(null, interfaceContent,mappingsContent);
	}

	public static void testSolidworksModel() {
		SolidworksModelRuntime model = loadSolidworksModel();
		if (model==null)
			return;
		ModelInterfaceRuntimeServer mdlInterface = loadInterfaceForModel(model);
		if (mdlInterface==null)
			return;
		Collection params = mdlInterface.getModelObjectParameters();
		// prompt for new input
		List inputs = new ArrayList(), outputs = new ArrayList();
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			for (Iterator iterator = params.iterator(); iterator.hasNext();) {
				Parameter p = (Parameter) iterator.next();
				if (mdlInterface.getCausality(p).equals(CausalityStatus.INDEPENDENT)) {
					inputs.add(p);
					setNewValue(p, stdin);
				} else
					outputs.add(p);
			}
			stdin.close();
		}
		catch (IOException e) {
			System.err.println(e);
		}
		model.startModel();
		StringBuffer sb = new StringBuffer("\nInputs");
		for (Iterator iterator = inputs.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter)iterator.next();
			DataObject dObj = p.getCurrentDataObject();
			sb.append("\n  "+p.getName()+"\t"+dObj);
		}
		sb.append("\n\nOutputs");
		for (Iterator iterator = outputs.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			DataObject dObj = p.getCurrentDataObject();
			sb.append("\n  " + p.getName() + "\t" + dObj);
		}
		System.out.println(sb);
		model.stopModel();
	}

	public static void setNewValue(Parameter p, BufferedReader stdin) throws IOException {
		DataObject dObj = p.getCurrentDataObject();
		System.out.print("New value for "+p.getName()+"("+dObj+"): ");
		String line = stdin.readLine();
		if (line == null || line.length()==0)
			return;
		if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean)
			((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean)dObj).setBooleanValue(new Boolean(line));
			//dObj.setValues(Collections.singletonList(new Boolean(line)));
		else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal)
			((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal)dObj).setRealValue(new Double(line));
			//dObj.setValues(Collections.singletonList(new Double(line)));
		else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger)
			((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger)dObj).setIntegerValue(new Integer(line));
			//dObj.setValues(Collections.singletonList(new Integer(line)));
		else
			System.err.println("warning: can not deal with this type yet");
	}

	public static void main(String[] args)
	{
		DomeInit.initializeDOME();
		testSolidworksModel();
		System.exit(0);
	}
}
