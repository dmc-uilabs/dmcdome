package test.model;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.matlab.MatlabConfiguration;
import mit.cadlab.dome3.plugin.matlab.MatlabModelRuntime;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import javax.swing.JOptionPane;
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
 * Date: Mar 11, 2003
 * Time: 8:15:31 PM
 * To change this template use Options | File Templates.
 */
public class RunMatlabModel
{
	public static DomeFileChooser fileChooser = new DomeFileChooser();

	public static MatlabModelRuntime loadMatlabModel() {
		String localFileName = fileChooser.showOpenDialog(null, MatlabConfiguration.TYPE_INFO.getTypeName());
		if (localFileName == null)
			return null; // cancelled
		// construct the model and a frame for it
		Element modelElement = XMLUtils.fileToXmlElement(localFileName);
		MatlabModelRuntime model = new MatlabModelRuntime(new CompoundId(), modelElement, false);
		return model;
	}

	public static ModelInterfaceRuntimeServer loadInterfaceForModel(MatlabModelRuntime model) {
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

	public static void testMatlabModel() {
		MatlabModelRuntime model = loadMatlabModel();
		if (model==null)
			return;
		model.startModel();
		ModelInterfaceRuntimeServer mdlInterface = loadInterfaceForModel(model);
		if (mdlInterface==null)
			return;
		Collection params = mdlInterface.getModelObjectParameters();
		// sort inputs/outputs
		List inputs = new ArrayList(), outputs = new ArrayList();
		for (Iterator iterator = params.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			if (mdlInterface.getCausality(p).equals(CausalityStatus.INDEPENDENT))
				inputs.add(p);
			else
				outputs.add(p);
		}

		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			do {
				getNewValuesAndRunModel(model,inputs,outputs,stdin);
			} while (JOptionPane.showConfirmDialog(null,"Do you want to run the model again?","Continue",
			                                       JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==0);
			stdin.close();
		}
		catch (IOException e) {
			System.err.println(e);
		}
		model.stopModel();
	}

	public static void getNewValuesAndRunModel(PluginModelRuntime model, List inputs, List outputs, BufferedReader stdin)
	{
		for (Iterator iterator = inputs.iterator(); iterator.hasNext();) {
			try {
				setNewValue((Parameter) iterator.next(), stdin);
			}
			catch (IOException e) {
				System.err.println(e);
			}
		}
		model.startModel();
		StringBuffer sb = new StringBuffer("\nInputs");
		for (Iterator iterator = inputs.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			DataObject dObj = p.getCurrentDataObject();
			sb.append("\n  " + p.getName() + "\t" + dObj);
		}
		sb.append("\n\nOutputs");
		for (Iterator iterator = outputs.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			DataObject dObj = p.getCurrentDataObject();
			sb.append("\n  " + p.getName() + "\t" + dObj);
		}
		System.out.println(sb);
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
		DbUtils.setDbUrl(9001);
		testMatlabModel();
		System.exit(0);
	}
}
