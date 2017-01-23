package test.model;

import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.DomeInit;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Feb 25, 2003
 * Time: 1:38:46 PM
 * To change this template use Options | File Templates.
 */
public class RuntimeModelTest
{
	public static void main (String []args)
	{
		DomeInit.initializeDOME();

		String localFileName = "model.dml";

		SAXReader reader = new SAXReader();
		Document modelDoc = null;
		File modelFile = null;

		// parse the model file
		try {
			modelFile = new File (localFileName);
			modelDoc = reader.read(modelFile);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// construct the model
		Element modelElement = modelDoc.getRootElement();
		DomeModelRuntime model = new DomeModelRuntime (null, modelElement, false);

		// get the parameters
		Collection modelObjects = model.getModelObjects();
		for (Iterator modelObjIter = modelObjects.iterator(); modelObjIter.hasNext();)
		{
			ModelObject mObj = (ModelObject) modelObjIter.next();
			if (mObj instanceof Parameter)
			{
				// set item
				Parameter p = (Parameter) mObj;
				String idString = p.getId().getIdString();
				if (p.getCurrentDataObject() instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger) {
					List params = Collections.singletonList(new Integer(5));
					model.setItem(idString, "setValue", params);
					Object result = model.getItem(idString, "getValue", Collections.EMPTY_LIST);
				}
				else {
					Object data = model.getItem(idString, "getData", Collections.EMPTY_LIST);
				}
			}
		}
	}
}
