package mit.cadlab.dome3.api.demo;

import mit.cadlab.dome3.api.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Mar 30, 2005
 * Time: 4:29:19 PM
 * To change this template use Options | File Templates.
 */
public class TutorialPartSix
{
    public static void main(String[] args)
    {
        /* open a connection and log in to DOME server */
        DomeConnection conn = new DomeConnection("jacob", "keiko", "localhost:8080");

        /* browse folders, analysis tools and Interfaces */
        DomeFolder publicFolder = conn.getMyPublicFolder();
        DomeFolder tutorialExampleFolder = publicFolder.getFolder("optimization");

        System.out.println("Creating analysis tool ...");
        DomeAnalysisTool analysisTool = tutorialExampleFolder.getAnalysisToolByName("Optimization");
//        DomeProject domeProject = analysisTool.getProject();

//        DomeInterface projectInterface = domeProject.getInterfaceByName("Hybrid Electricity Extended Interface");

//        RuntimeInterface runtimeInterface = projectInterface.createRuntimeInterface();

//        RuntimeParameter pvLoadFraction = runtimeInterface.getParameterByName("PV load fraction");
//        pvLoadFraction.setValue(new Double(0.8)); // you may change additional parameters in the same way

        /* add custom value/status listener and set execution time limit -> this step is optional */
//        runtimeInterface.addParameterValueChangeListener(new MyValueChangeListener2());
//        runtimeInterface.addParameterStatusChangeListener(new MyStatusChangeListener2());

        /* submit changes and handle execution time limit exception */
//        try {
//            runtimeInterface.submit();
//        } catch (ExecutionTimeLimitException e) {
//            System.out.println("Execution time expired.");
//        }

        /* retrieve the results */
//        RuntimeParameter netElectricityCost = runtimeInterface.getParameterByName("net electricity cost");
//        System.out.println("[RESULT VALUE] net electricity cost = " + netElectricityCost);

        System.out.println("Creating analysis tool interface ...");
        DomeInterface analysisToolInterface = analysisTool.getInterfaceByName("Optimization Interface");

        System.out.println("Creating analysis tool runtime interface ...");
        RuntimeAnalysisToolInterface runtimeAnalysisToolInterface = analysisToolInterface.createRuntimeAnalysisToolInterface();

        System.out.println("Starting solving ...");
        runtimeAnalysisToolInterface.startAnalysisToolSolving();

    }
}
