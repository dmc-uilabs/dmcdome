package mit.cadlab.dome3.plugin.catalog.demo;

import edu.iupui.rg.ucum.units.UnitTab;
import mit.cadlab.dome3.plugin.catalog.core.CImplementation;
import mit.cadlab.dome3.plugin.catalog.core.CInterface;
import mit.cadlab.dome3.plugin.catalog.core.CLog;
import mit.cadlab.dome3.plugin.catalog.core.CModel;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.CReal;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;
import mit.cadlab.dome3.plugin.catalog.runtime.MappingScriptExecutionFailure;
import mit.cadlab.dome3.plugin.catalog.runtime.RelationExecutionFailure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 22.
 */
public class HowToChangeInterfaceDefinition {
    public static void main(String[] args) {
        initUnitSystem();
        CLog.setDebug(false);
        /* evaluate the first model */
        //changeInterfaceDefinition_1(1 * 2.54 * 2, 2 * 2.54 * 10 * 2);
        changeInterfaceDefinition_2();
    }

    public static void changeInterfaceDefinition_1(double holeDiameter, double holeDepth) {
        CModel model = HowToCreateCatalogModel.createModel_1();
        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        /* modify interface parameter values and evaluate the model */
        System.out.println("/* first model: starting evaluate() */");
        System.out.println("itf parameters: " + impl.getNamingService().getParameters());
        context.evaluate();
        reportFailures(context);
        System.out.println("cost density: " + context.getInterfaceDataObject("cost density"));
        System.out.println("pin volume: " + context.getInterfaceDataObject("pin volume"));
        System.out.println("/* first model: finished evaluate() */");

        /* change unit of interface parameters */
        CInterface itf = impl.getParentInterface();
        itf.getParameter("hole diameter").setUnit("cm");
        itf.getParameter("hole depth").setUnit("mm");
        itf.getParameter("cost density").setUnit("[$_p_l]");
        itf.getParameter("pin volume").setUnit("[cin_i]"); // cubic inch
        itf.synchronizeInterfaceParametersOfAllImplementations();

        /* let the context know which parameters are changed and re-evaluate the model */
        ((CReal) context.getInterfaceDataObject("hole diameter")).setDoubleValue(holeDiameter);
        ((CReal) context.getInterfaceDataObject("hole depth")).setDoubleValue(holeDepth);
        System.out.println("/* first model: starting evaluate(String[] modifiedParamNames) */");
        context.evaluate(new String[] { "hole diameter", "hole depth" });
        reportFailures(context);
        System.out.println("cost density: " + context.getInterfaceDataObject("cost density"));
        System.out.println("pin volume: " + context.getInterfaceDataObject("pin volume"));
        System.out.println("/* first model: finished evaluate(String[] modifiedParamNames) */");

        /* closing the context will clean up DomeConnection and RuntimeInterface associated with it */
        context.close();
    }

    public static void changeInterfaceDefinition_2() {
        CModel model = HowToCreateCatalogModel.createModel_4();
        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        /* modify interface parameter values and evaluate the model */
        System.out.println("/* first model: starting evaluate() */");
        System.out.println("itf parameters: " + impl.getNamingService().getParameters());
        System.out.println("context map: " + context.getDataObjectMap());
        context.evaluate();
        reportFailures(context);
        System.out.println("/* first model: finished evaluate() */");

        //context.close();
        context.refresh();

        /* change unit of interface parameters */
        CInterface itf = impl.getParentInterface();
        itf.getParameter("A_panel azimuth angle").setUnit("rad");
        itf.synchronizeInterfaceParametersOfAllImplementations();

        /* let the context know which parameters are changed and re-evaluate the model */
        ((CReal) context.getInterfaceDataObject("A_panel azimuth angle")).setDoubleValue(0.17);
        System.out.println("/* first model: starting evaluate(String[] modifiedParamNames) */");
        System.out.println("itf parameters: " + impl.getNamingService().getParameters());
        System.out.println("context map: " + context.getDataObjectMap());

        //context.evaluate(new String[] { "A_panel azimuth angle" });
        context.evaluate();
        reportFailures(context);
//        System.out.println("cost density: " + context.getInterfaceDataObject("cost density"));
//        System.out.println("pin volume: " + context.getInterfaceDataObject("pin volume"));
        System.out.println("/* first model: finished evaluate(String[] modifiedParamNames) */");

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
