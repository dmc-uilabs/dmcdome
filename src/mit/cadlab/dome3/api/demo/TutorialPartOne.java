package mit.cadlab.dome3.api.demo;

import mit.cadlab.dome3.api.*;

import java.util.List;
import java.util.Arrays;

/**
 * User: Sangmok Han
 * Date: 2005. 1. 21.
 */
public class TutorialPartOne {
    public static void main(String[] args) {
        /* open a connection and log in to DOME server */
        DomeConnection conn = new DomeConnection("tutorialUser", "123", "localhost:8080");

        /* browse folders, models and Interfaces */
        DomeFolder publicFolder = conn.getPublicFolder();
        DomeFolder tutorialExampleFolder = publicFolder.getFolder("Tutorial examples");
        DomeModel polymerCuringModel = tutorialExampleFolder.getModelByName("Polymer curing model");
        DomeInterface polymerCuringInterface = polymerCuringModel.getInterfaceByName("polymer curing interface");

        /* [optional part start] how to browse folders other than server folders. note that ROOT don't have user's public/private folder, so it should print null. */
        System.out.println("My Public Folder= " + conn.getMyPublicFolder());
        System.out.println("My Public Folder= " + conn.getMyPrivateFolder());

        String[] userNames = conn.getUserNames();
        System.out.println("User Names= " + Arrays.asList(userNames));
        for (int i = 0; i < userNames.length; i++) {
            System.out.println("User Public Folder= " + conn.getUserPublicFolder(userNames [i]));
            System.out.println("User Public Folder= " + conn.getUserPrivateFolder(userNames [i]));
        }

        String[] groupNames = conn.getGroupNames();
        System.out.println("Group Names= " + Arrays.asList(groupNames));
        for (int i = 0; i < groupNames.length; i++) {
            System.out.println("Group Public Folder= " + conn.getGroupPublicFolder(groupNames [i]));
            System.out.println("Group Private Folder= " + conn.getGroupPrivateFolder(groupNames [i]));
        }

        System.out.println("Server Public Folder= " + conn.getServerPublicFolder()); // the same as getPublicFolder()
        System.out.println("Server Private Folder= " + conn.getServerPrivateFolder()); // the same as getPrivateFolder()
        /* [optional part end]*/


        /* instantiate Runtime Interface and retrieve the list of parameters */
        RuntimeInterface runtimeInterface = polymerCuringInterface.createRuntimeInterface();
        List paramList = runtimeInterface.getParameters();
        System.out.println("[PARAM LIST] " + paramList);

        /* changes the values of parameters */
        RuntimeParameter nominalParam = runtimeInterface.getParameterByName("nominalModulus");
        nominalParam.setRealValue(400000.0);

        /* add custom value/status listener and set execution time limit -> this step is optional */
        runtimeInterface.addParameterValueChangeListener(new MyValueChangeListener1());
        runtimeInterface.addParameterStatusChangeListener(new MyStatusChangeListener1());
        runtimeInterface.setExecutionTimeLimit(10000); // if execution time is not explicitly set, there will be no time limit.

        /* submit changes and handle execution time limit exception */
        try {
            runtimeInterface.submit();
        } catch (ExecutionTimeLimitException e) {
            System.out.println("Execution time expired.");
        }

        /* retrieve the results */
        RuntimeParameter actualParam = runtimeInterface.getParameterByName("actualModulus");
        System.out.println("[RESULT VALUE] actual modulus = " + actualParam.getRealValue());

        /* [optional] getDependency() method is used to query dependency among parameters */
        System.out.println("[CHECK DEPENDENCY] nominalModulus drives actualModulus : " + runtimeInterface.getDependency(nominalParam, actualParam));
        System.out.println("[CHECK DEPENDENCY] actualModulus drives nominalModulus : " + runtimeInterface.getDependency(actualParam, nominalParam));

        /* log out and close connection */
        conn.close();
    }
}

class MyValueChangeListener1 implements ParameterValueChangeListener {
    public void valueChanged(ParameterValueChangeEvent event) {
        System.out.println("My value listener : Parameter value changed = " + event);
    }
}

class MyStatusChangeListener1 implements ParameterStatusChangeListener {
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

