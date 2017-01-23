package mit.cadlab.dome3.api.demo;

import mit.cadlab.dome3.api.*;

import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2005. 1. 30.
 */
public class TutorialPartTwo {
    public static void main(String[] args) {
        /* open a connection and log in to DOME server */
        DomeConnection conn = new DomeConnection("root", "cadlab", "localhost:8080");

        /* browse folders, projects */
        DomeFolder publicFolder = conn.getPublicFolder();
        DomeFolder tutorialExampleFolder = publicFolder.getFolder("Tutorial examples");
        DomeProject laminaProject = tutorialExampleFolder.getProjectByName("As cured lamina properties");

        /* retrieve interfaces, resources and imodels of the 'As cured lamina properties' project */
        List projectInterfaces = laminaProject.getInterfaces(); // java.util.List of DomeInterface returned
        List resources = laminaProject.getResources(); // java.util.List of DomeModel and DomeProject returned
        List iModels = laminaProject.getIModels(); // java.util.List of DomeIModel returned

        /* locate the project interface 'integrated model interface' */
        DomeInterface laminaInterface = laminaProject.getInterfaceByName("integrated model interface");

        /* your may also locate the model interface 'polymer curing interface' of a resource in this way */
        DomeModel polymerCuringModel = laminaProject.getDomeModelResourceByName("Polymer curing model");
        DomeInterface modelInterface = polymerCuringModel.getInterfaceByName("polymer curing interface");

//        // April 1, 2005. This code doesnot work. the reason is 'DomeIModel RuntimeInterface CompoundId problem' 
//        /* your may also locate the model interface 'Default Interface' of a imodel in this way */
//        DomeIModel iModel = laminaProject.getDomeIModelByName("iModel");
//        DomeInterface iModelInterface = iModel.getInterfaceByName("Default Interface");
//
//        RuntimeInterface runtimeInterfaceOfIModel = iModelInterface.createRuntimeInterface();
//        List paramListOfIModel = runtimeInterfaceOfIModel.getParameters(); // java.util.List of DomeParameter returned
//        System.out.println("[PARAM LIST OF IMODEL] " + paramListOfIModel);
//
//        RuntimeParameter fiberVolumeFaction = runtimeInterfaceOfIModel.getParameterByName("fiber volume fraction");
//        fiberVolumeFaction.setValue(new Double(0.2)); // you may change additional parameters in the same way
//
//        /* add custom value/status listener and set execution time limit -> this step is optional */
//        runtimeInterfaceOfIModel.addParameterValueChangeListener(new MyValueChangeListener2());
//        runtimeInterfaceOfIModel.addParameterStatusChangeListener(new MyStatusChangeListener2());
//        runtimeInterfaceOfIModel.setExecutionTimeLimit(60000);
//
//        /* submit changes and handle execution time limit exception */
//        try {
//            runtimeInterfaceOfIModel.submit();
//        } catch (ExecutionTimeLimitException e) {
//            System.out.println("Execution time expired.");
//        }
//
//        /* retrieve the results */
//        RuntimeParameter e1ModulusParam = runtimeInterfaceOfIModel.getParameterByName("lamina E1 modulus");
//        System.out.println("[IMODEL RESULT VALUE] lamina E1 modulus = " + e1ModulusParam);
//

        /* instantiate Runtime Interface and retrieve the list of parameters */
        RuntimeInterface runtimeInterface = laminaInterface.createRuntimeInterface();
        List paramList = runtimeInterface.getParameters(); // java.util.List of DomeParameter returned
        System.out.println("[PARAM LIST] " + paramList);

        /* changes the values of parameters */
        RuntimeParameter curingTemperature = runtimeInterface.getParameterByName("lamina curing temperature");
        curingTemperature.setRealValue(120.0); // you may change additional parameters in the same way

        /* add custom value/status listener and set execution time limit -> this step is optional */
        runtimeInterface.addParameterValueChangeListener(new MyValueChangeListener2());
        runtimeInterface.addParameterStatusChangeListener(new MyStatusChangeListener2());
        runtimeInterface.setExecutionTimeLimit(60000);

        /* submit changes and handle execution time limit exception */
        try {
            runtimeInterface.submit();
        } catch (ExecutionTimeLimitException e) {
            System.out.println("Execution time expired.");
        }

        /* retrieve the results */
        RuntimeParameter e2ModulusParam = runtimeInterface.getParameterByName("lamina E2 modulus");
        System.out.println("[RESULT VALUE] lamina E2 modulus = " + e2ModulusParam.getRealValue());

        /* log out and close connection */
        conn.close();
    }
}

class MyValueChangeListener2 implements ParameterValueChangeListener {
    public void valueChanged(ParameterValueChangeEvent event) {
        System.out.println("My value listener : Parameter value changed = " + event);
    }
}

class MyStatusChangeListener2 implements ParameterStatusChangeListener {
    public void statusChanged(ParameterStatusChangeEvent event) {
        System.out.println("My status listener : Parameter status changed = " + event);

        /* ParameterStatusChangeListener */
        if (ParameterStatusChangeEvent.SOLVING_SUCCESS_EVENT.equals(event)) {
            System.out.println("My status listener : Solving succeed");
        } else if (ParameterStatusChangeEvent.SOLVING_FAILURE_EVENT.equals(event)) {
            System.out.println("My status listener : Solving failed");
        }
    }
}