package mit.cadlab.dome3.plugin.catalog.demo;

import edu.iupui.rg.ucum.units.UnitTab;
import mit.cadlab.dome3.plugin.catalog.core.CImplementation;
import mit.cadlab.dome3.plugin.catalog.core.CLog;
import mit.cadlab.dome3.plugin.catalog.core.CModel;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.CReal;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.CEnumeration;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.DataObjectUtil;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;
import mit.cadlab.dome3.plugin.catalog.runtime.MappingScriptExecutionFailure;
import mit.cadlab.dome3.plugin.catalog.runtime.RelationExecutionFailure;
import mit.cadlab.dome3.plugin.catalog.serialization.DomeSerialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This demo class use two catalog models created by HowToCreateCatalogModel class to evaluate their implementations
 * against two different input conditions: the first condition evaluates the implementation using default values of interface parameters by calling evaluate(),
 * while the second evaluates the implementation using user-specified values by calling evaluate(String[] modifiedParamNames)
 * User: Sangmok Han
 * Date: 2006. 7. 17.
 */
public class HowToEvaluateCatalogModel {

    public static void main(String[] args) {
        initUnitSystem();
        //CLog.setDebug(true);
        CLog.setDebug(false);

//        /* evaluate the first model */
//        evaluateModel_1(1, 2);
//
//        /* evaluate the second model */
//        evaluateModel_2(40);
//
//        /* evaluate the third model */
//        evaluateModel_3();

        /* evaluate the fourth model */
        evaluateModel_4();
    }

    public static void evaluateModel_1(double holeDiameter, double holeDepth) {
        CModel model = HowToCreateCatalogModel.createModel_1();
        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        /* modify interface parameter values and evaluate the model */
        System.out.println("/* first model: starting evaluate() */");
        context.evaluate();
        System.out.println("/* first model: finished evaluate() */");

        /* let the context know which parameters are changed and re-evaluate the model */
        ((CReal) context.getInterfaceDataObject("hole diameter")).setDoubleValue(holeDiameter);
        ((CReal) context.getInterfaceDataObject("hole depth")).setDoubleValue(holeDepth);
        System.out.println("/* first model: starting evaluate(String[] modifiedParamNames) */");
        context.evaluate(new String[] { "hole diameter", "hole depth" });
        System.out.println("/* first model: finished evaluate(String[] modifiedParamNames) */");

        reportFailures(context);

        /* closing the context will clean up DomeConnection and RuntimeInterface associated with it */
        context.close();
    }

    public static void evaluateModel_2(double azimuthAngle) {
        CModel model = HowToCreateCatalogModel.createModel_2();
        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        System.out.println("/* second model: starting evaluate() */");
        context.evaluate();
        System.out.println("/* second model: finished evaluate() */");

        /* let the context know which parameters are changed and re-evaluate the model */
        ((CReal) context.getInterfaceDataObject("A_panel azimuth angle")).setDoubleValue(azimuthAngle);
        System.out.println("/* second model: starting evaluate(String[] modifiedParamNames) */");
        context.evaluate(new String[] { "A_panel azimuth angle" });
        System.out.println("/* second model: finished evaluate(String[] modifiedParamNames) */");

        reportFailures(context);

        /* closing the context will clean up DomeConnection and RuntimeInterface associated with it */
        context.close();
    }

    public static void evaluateModel_3() {
        CModel model = HowToCreateCatalogModel.createModel_3();
        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        System.out.println("/* third model: starting evaluate() */");
        context.evaluate();
        reportFailures(context);
        System.out.println("/* third model: finished evaluate() */");

        /* closing the context will clean up DomeConnection and RuntimeInterface associated with it */
        context.close();
    }

    public static void evaluateModel_4() {
        DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load("C:/Documents and Settings/smhan/Desktop/research/qual/demomodels/powerwindowsystem-CATALOG.dml");
        //DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load("C:/Documents and Settings/smhan/Desktop/research/qual/demomodels/integratedminimodel-CATALOG.dml");
        CModel model = modelAndIDContainer.getModel();

        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        System.out.println("/* fourth model: starting evaluate() */");
        context.evaluate();
        reportFailures(context);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
//            ((CReal) context.getInterfaceDataObject("window width")).setDoubleValue(800);
            //List enumList = ((CEnumeration) context.getInterfaceDataObject("impl switch")).getEnumList();
            List enumList = ((CEnumeration) context.getInterfaceDataObject("model_name")).getEnumList();
            int selectedIndex = DataObjectUtil.getSelectedIndex(enumList);
            if (selectedIndex == 0) {
                DataObjectUtil.setSelectedIndex(1, enumList);
            } else {
                DataObjectUtil.setSelectedIndex(0, enumList);
            }
            ((CEnumeration) context.getInterfaceDataObject("model_name")).setEnumList(enumList);

            context.evaluate();
            reportFailures(context);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("time for five time run = " + (endTime - startTime));

        System.out.println("/* fourth model: finished evaluate() */");

        /* closing the context will clean up DomeConnection and RuntimeInterface associated with it */
        context.close();
    }

    private static void reportFailures(EvaluationContext context) {
        /* report relation execution failures */
        RelationExecutionFailure[] relFailures = context.getRelationExecutionFailures();
        if (relFailures.length > 0) {
            System.out.println("One or more than one RelationExecutionFailure occurred: ");
            for (int i = 0; i < relFailures.length; i++) {
                RelationExecutionFailure failure = relFailures[i];
                System.out.println("  rel failure[" + i + "] = " + failure);
            }
        } else {
            System.out.println("No RelationExecutionFailure occurred.");
        }

        /* report mapping script execution failures */
        MappingScriptExecutionFailure[] mapFailures = context.getMappingScriptExecutionFailures();
        if (mapFailures.length > 0) {
            System.out.println("One or more than one MappingScriptExecutionFailure occurred: ");
            for (int i = 0; i < mapFailures.length; i++) {
                MappingScriptExecutionFailure failure = mapFailures[i];
                System.out.println("  map failure[" + i + "] = " + failure);
            }
        } else {
            System.out.println("No MappingScriptExecutionFailure occurred.");
        }

    }

    private static void initUnitSystem() {
        try {
            InputStream is = new ByteArrayInputStream(edu.iupui.rg.ucum.CommonUnit.DATA.getBytes());
            UnitTab.read(is);
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading unit data");
        }
    }
}
