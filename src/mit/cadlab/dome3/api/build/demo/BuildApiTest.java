package mit.cadlab.dome3.api.build.demo;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.api.build.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;

import edu.iupui.rg.ucum.units.UnitAtom;
import edu.iupui.rg.ucum.units.Unit;

public class BuildApiTest {
    public static void main(String[] args) {
        BuildApiTest test = new BuildApiTest();
        test.buildPluginModelDemo();
    }

    public void buildPluginModelDemo() {
    	System.out.println("was mika here?");
    	
        // initialize DOME
        if (!DomeInit.isInitialized())
            DomeInit.initializeDOME();

        // create a new Excel plugin model and set its name
        PluginModel model = new PluginModel(PluginConstants.EXCEL);
        model.setName("my api model");

        // show all available data types
        System.out.println("Valid data types: " + model.getValidDataTypes());

        // create Real parameters
        RealParameter a =  model.addRealParameter("A");
        RealParameter b = model.addRealParameter("B");
        RealParameter c = model.addRealParameter("C");

        // set parameter's name
        a.setName("AA");

        // set Real parameter's value
        b.setValue(45);

        // set Numerical parameter's unit
        b.setUnit("m");

        System.out.println("value of AA: " + a.getValue());
        System.out.println("unit of AA: " + a.getUnitLabel());

        // set parameter's mapping (e.g. to an Excel cell's reference)
        model.setParameterMapping(b, "b-link");

        // set one parameter to be dependent on another parameter
        model.setParametersDependency(a, b);

        // set one parameter to be dependent on more than one parameter
        ArrayList drivers1 = new ArrayList();
        drivers1.add(a);
        drivers1.add(b);
        model.setParametersDependency(c, drivers1);

        // doing the following will create a causal loop. an error will be thrown with a description
        // one loop:
        //model.setParametersDependency(a, b);
        // multiple loops
        //ArrayList drivers2 = new ArrayList();
        //drivers2.add(b);
        //drivers2.add(c);
        //model.setParametersDependency(a, drivers2);

        // add setup files
        FileParameter fileI = model.addSetupFile("file-I", "C:\\Documents and Settings\\ton\\Desktop\\Book1.xls");
        FileParameter fileII = model.addSetupFile("file-II", "C:\\Documents and Settings\\ton\\Desktop\\Book2.xls");

        // change setup file's path and name
        fileI.setPath("C:\\Documents and Settings\\ton\\Desktop\\NewBook1.xls");
        fileII.setName("new file-II");
        System.out.println("path of file-I: " + fileI.getPath());
        System.out.println("type of file-II: "+ fileII.getType());

        // by default, the first setup file added is chosen as the main setup file, but can choose another one
        model.setAsMainSetupFile(fileII);
        model.setAsMainSetupFile("file-I");

        // get all setup files' name and value
        System.out.println(model.getAllSetupFileNames());
        System.out.println(model.getAllSetupFileValues());

        // delete setup file
        String newMainFileName = model.deleteSetupFile("file-I");
        System.out.println("The main setup file is now: " + newMainFileName);

        // get setup parameters
        List setupParams = model.getSetupParameterNames();
        System.out.println("Setup parameters: " + setupParams);

        // get available software versions by using the method to get an enumeration setup-parameter's value
        String versionParamName = (String) setupParams.get(0);
        List vers = model.getSetupEnumerationParameterList(versionParamName);
        System.out.println("Available versions: "+vers);

        // set software version by using the method to set an enumeration setup-parameter's value
        model.setSetupEnumerationParameterSelection(versionParamName, (String) vers.get(vers.size()-1));

        // get run-in-foreground by using the method to get a boolean setup-parameter's value
        String RIFParamName = (String) setupParams.get(1);
        System.out.println("Run in foreground: " + model.getSetupBooleanParameterValue(RIFParamName));

        // set run-in-foreground by using the method to set a boolean setup-parameter's value
        model.setSetupBooleanParameterValue(RIFParamName, true);

        // for some plugin types, some setup parameters such as software version or run-in-foreground are inapplicable.
        // in such cases, empty result or "false" will be returned
        PluginModel model2 = new PluginModel(PluginConstants.IDEAS);
        System.out.println("Non-existing setup enumeration: " + model2.getSetupEnumerationParameterList(""));
        System.out.println("Non-existing setup boolean: " + model2.getSetupBooleanParameterValue(""));

        // get the default interface
        Interface defaultIface = model.getDefaultInterface();

        // get the interface's input, output, intermediate, result, and indeterminate parameters
        System.out.println("Default interface's inputs: " + defaultIface.getInputParameters());
        System.out.println("Default interface's outputs: " + defaultIface.getOutputParameters());
        System.out.println("Default interface's intermediates: " + defaultIface.getIntermediateParameters());
        System.out.println("Default interface's results: " + defaultIface.getResultParameters());
        System.out.println("Default interface's indeterminates: " + defaultIface.getIndeterminateParameters());

        model.save("C:\\Documents and Settings\\ton\\Desktop\\mytest2");

        // rename interface
        //defaultIface.setName("old interface");

        // add a new interface
        Interface newiface = model.addInterface("new interface");

        // get names of all interfaces
        System.out.println("interface names: " +  model.getInterfaceNames());

        // can't add parameters to default interface
        //defaultIface.addAndMapParameter(b);

        // but can add a parameter to another interface, and specify the interface parameter name at the same time
        RealParameter aIf = (RealParameter) newiface.addAndMapParameter(a, "length");

        // can also add a group of parameters to the interface
        ArrayList paramlist = new ArrayList();
        paramlist.add(c);
        paramlist.add(b);
        List BCifaceParam = newiface.addAndMapParameter(paramlist);
        RealParameter bIf = (RealParameter) BCifaceParam.get(1);

        // get a map of iface param to model param (names only)
        HashMap map = newiface.getInterfaceParamToModelParamNameMap();

        // get a list of units that are compatible with a parameter's current unit
        List bIfValidUnits = bIf.getCompatableUnitNames();
        System.out.println("Valid units of " + bIf.getName() + ": " + bIfValidUnits);

        // can also set name, value, and unit of interface parameters
        aIf.setValue(500);
        aIf.setName("the length of the object");
        bIf.setUnit((String) bIfValidUnits.get(0));

        // save model
        //model.save("C:\\Documents and Settings\\ton\\Desktop\\mytest-EXCEL.dml");
        // this also works (no extension specified)
        model.save("C:\\Documents and Settings\\ton\\Desktop\\mytest");
        System.out.println(model.getModelExtension());

        // close model
        model.close();

        // can't do anything to a closed model
        //model.addInterface("new interface");

        // open saved model
        PluginModel oModel = new PluginModel("C:\\Documents and Settings\\ton\\Desktop\\mytest-EXCEL.dml", true);
        // can work with the opened model as normal

        // get parameter by name
        Parameter cParam = oModel.getParameterByName("C");

        // delete model parameters
        oModel.deleteParameter(cParam);
        Parameter aParam = oModel.getParameterByName("AA");
        Parameter bParam = oModel.getParameterByName("B");
        ArrayList delList = new ArrayList();
        delList.add(aParam);
        delList.add(bParam);
        oModel.deleteParameters(delList);

        // delete interface parameters
        Interface oNewIface = oModel.getInterface("new interface");
        oNewIface.deleteParameter(oNewIface.getParameterByName("the length of the object"));
        ArrayList delIfList = new ArrayList();
        delIfList.add(oNewIface.getParameterByName("C"));
        delIfList.add(oNewIface.getParameterByName("B"));
        oNewIface.deleteParameters(delIfList);

        oModel.save("C:\\Documents and Settings\\ton\\Desktop\\mytest-empty");

        // add a matrix parameter
        MatrixParameter mat1 = oModel.addMatrixParameter("mat 1");

        // set the matrix's size and values
        mat1.setSize(3,1);
        Vector matval = new Vector();
        for (int i = 0; i<3; i++) {
            Vector row = new Vector();
            row.add(new Double(i));
            matval.add(row);
        }
        mat1.setValue(matval);
        mat1.setItem(2, 0, new Double(33));
        System.out.println("size of mat 1: " + mat1.getSize()[0] + " by " + mat1.getSize()[1]);
        System.out.println("value of mat 1: " + mat1.getValue());
        System.out.println("value of mat 1 at 0,0 : " + mat1.getItem(0, 0));
        System.out.println("unit of mat 1: " + mat1.getUnitLabel());

        // add enumeration parameter
        EnumerationParameter enum1 = oModel.addEnumerationParameter("enum 1");

        // add items to enumeration parameter
        enum1.addElement("1st element", new Integer(2));
        enum1.addElement("2nd element", new Double(2));
        enum1.addElement("3rd element", new Boolean(true));
        enum1.addElement("4th element", "Hello!");

        // set enumenration selection
        enum1.setLastSelection(1);
        enum1.setLastSelectionToName("4th element");

        // get name and value of enumeration item
        System.out.println("1st element name: " + enum1.getElementName(0));
        System.out.println("2nd element value: " + enum1.getElementValue(1));
        System.out.println("3rd element value: " + enum1.getElementValue("3rd element"));
        System.out.println("last selection of of enum 1: " + enum1.getLastSelection());


        // add vector parameter
        VectorParameter vec = oModel.addVectorParameter("vec param");

        // set vector's size and values
        vec.setSize(5);
        Vector vecval = new Vector();
        for (int i = 0; i < 5; i++) {
            vecval.add(new Double(i*2));
        }
        vec.setValue(vecval);
        vec.setItem(0, new Integer(10));
        System.out.println("size of vec param: " + vec.getSize());
        System.out.println("value of vec param: " + vec.getValue());
        System.out.println("value of vec param's 2nd item: " + vec.getItem(1));
        System.out.println("unit of vec param: " + vec.getUnitLabel());

        // add integer parameter and set value
        IntegerParameter int1 = oModel.addIntegerParameter("new integer");
        int1.setUnit("mm");
        int1.setValue(-96);
        System.out.println("value of new integer: " + int1.getValue());
        System.out.println("unit of new integer: " + int1.getUnitLabel());

        // add boolean parameter and set value
        BooleanParameter boo = oModel.addBooleanParameter("boo");
        boo.setValue(true);
        System.out.println("value of boo: " + boo.getValue());

        // add string parameter and set value
        StringParameter str = oModel.addStringParameter("my string");
        str.setValue("this is some string alright");
        System.out.println("value of my string: " + str.getValue());

        oModel.save("C:\\Documents and Settings\\ton\\Desktop\\mytest2");

        System.out.println("Done!");

    }

    public void debug() {
        if (!DomeInit.isInitialized())
            DomeInit.initializeDOME();
        PluginModel oModel = new PluginModel("C:\\Documents and Settings\\ton\\Desktop\\mytest-MATLAB.dml", true);
        System.out.println("main file: "+oModel.getMainSetupFileName());
        String newMainFileName = oModel.deleteSetupFile("Page 3 - Zip Meets Java.htm");
        System.out.println("The main setup file is now: " + newMainFileName);
    }
}