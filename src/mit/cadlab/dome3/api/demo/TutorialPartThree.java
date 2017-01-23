package mit.cadlab.dome3.api.demo;

import mit.cadlab.dome3.api.*;

import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2005. 1. 30.
 */
public class TutorialPartThree {
    public static void main(String[] args) {
        /* open a connection and log in to DOME server */
        DomeConnection conn = new DomeConnection("guest", "", "localhost:8080");

        /* browse folders, projects */
        DomeFolder publicFolder = conn.getPublicFolder();
        DomeFolder tutorialExampleFolder = publicFolder.getFolder("Tutorial examples");
        DomePlayspace teamPlayspace = tutorialExampleFolder.getPlayspace("Polymer curing team");

        /* instantiate runtime playspace and imodels of the 'As cured lamina properties' project */
        RuntimePlayspace runtimePlayspace = teamPlayspace.createRuntimePlayspace();

        /* you can also instantiate the runtime playspace quicker by using joinPlayspace() method as follows */
        RuntimePlayspace sameRuntimePlayspace = tutorialExampleFolder.joinPlayspace("Polymer curing team");

        /* retrieve models and projects in the playspace and locate 'Polymer curing model' model */
        List modelList = runtimePlayspace.getModels(); // java.util.List of DomeModel returned
        List projectList = runtimePlayspace.getProjects(); // java.util.List of DomeProject returned
        DomeModel polymerModel = runtimePlayspace.getModelByName("Polymer curing model");

        /* locate the model interface 'polymer curing interface' */
        DomeInterface polymerInterface = polymerModel.getInterfaceByName("polymer curing interface");

        /* instantiate Runtime Interface and retrieve the list of parameters */
        RuntimeInterface runtimeInterface = polymerInterface.createRuntimeInterface();
        List paramList = runtimeInterface.getParameters(); // java.util.List of DomeParameter returned
        System.out.println("[PARAM LIST] " + paramList);

        /* changes the values of parameters */
        RuntimeParameter curingTemperature = runtimeInterface.getParameterByName("curingTemperature");
        curingTemperature.setRealValue(60.0); // you may change additional parameters in the same way

        /* add custom value/status listener and set execution time limit -> this step is optional */
        runtimeInterface.addParameterValueChangeListener(new MyValueChangeListener3());
        runtimeInterface.addParameterStatusChangeListener(new MyStatusChangeListener3());
        runtimeInterface.setExecutionTimeLimit(60000);

        /* submit changes and handle execution time limit exception */
        try {
            runtimeInterface.submit();
        } catch (ExecutionTimeLimitException e) {
            System.out.println("Execution time expired.");
        }


        /* retrieve the results */
        RuntimeParameter actualParam = runtimeInterface.getParameterByName("actualModulus");
        System.out.println("[RESULT VALUE] actual modulus = " + actualParam.getRealValue());

        /* log out and close connection */
        conn.close();
    }
}

class MyValueChangeListener3 implements ParameterValueChangeListener {
    public void valueChanged(ParameterValueChangeEvent event) {
        System.out.println("My value listener : Parameter value changed = " + event);
    }
}

class MyStatusChangeListener3 implements ParameterStatusChangeListener {
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