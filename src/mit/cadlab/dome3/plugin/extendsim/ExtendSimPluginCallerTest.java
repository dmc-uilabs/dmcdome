package mit.cadlab.dome3.plugin.extendsim;

import mit.cadlab.dome3.api.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.List;

/**
 * ExtendSim JNI Test Class
 * User: Sangmok Han
 * Date: Aug 10, 2007
 * Time: 3:29:16 PM
 */
public class ExtendSimPluginCallerTest {

    public static void main5(String[] args) {
        ExtendSimPluginCaller caller = new ExtendSimPluginCaller("ExtendSimPlugin");
        String globalStr = null;

        Thread starterThread = new Thread() { public void run() {
            ExtendSimPluginCaller starter = new ExtendSimPluginCaller("ExtendSimPlugin");
            starter.executeCommand("ExecuteMenuCommand(6000);"); // start
        }};
        starterThread.setDaemon(false);
        starterThread.start();

        int j = 0;
        while (j  == 0) {
//            caller.setRealValue("conValue:#1", 0);
            try { Thread.sleep(1000); } catch (Exception e) { }
            caller.setRealValue("conValue:#1", 1);
//            try { Thread.sleep(100); } catch (Exception e) { }
            caller.setRealValue("conValue:#1", 0);

//            System.out.println("i = " + i + " Send!");
            double count = caller.getRealValue("count:#6");
            System.out.println("Count = " + count);
            try {
                Thread.sleep(10000);
            } catch (Exception e) { }
        }

//        caller.executeCommand("ExecuteMenuCommand(30000);"); // stop
        try {
            Thread.sleep(1000);
        } catch (Exception e) { }
//        caller.executeCommand("ExecuteMenuCommand(30000);"); // stop
        try {
            Thread.sleep(1000);
        } catch (Exception e) { }
//        caller.executeCommand("ExecuteMenuCommand(100);"); // okay
        try {
            Thread.sleep(1000);
        } catch (Exception e) { }
//        caller.executeCommand("ExecuteMenuCommand(100);"); // okay

        if (true) {
            return;
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            double currentTime = caller.getCurrentTime();
            System.out.println("currentTime = " + currentTime);
//            if (currentTime > 247) {
            if (currentTime > 5) {
//                caller.executeCommand("PauseSim();");
                caller.executeCommand("ExecuteMenuCommand(30001);"); // pause
                break;
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) { }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("100 execution took " + (endTime - startTime));

        try { Thread.sleep(3000); } catch (Exception e) {}
        caller.executeCommand("ExecuteMenuCommand(30002);"); // resume

        try { Thread.sleep(1000); } catch (Exception e) {}
        caller.executeCommand("ExecuteMenuCommand(30002);"); // resume

        caller.executeCommand("SendMsgToBlock(1, 1);");
    }

    public static void main(String[] args) {
        main5(args);
        //apiTest();

//        double[][] matrix = ExtendSimPluginCaller.getRealMatrix("data:#73:0:1:3:2");
//        System.out.println("matrix[0][0] = " + matrix[0][0] + ", matrix[3][1] = " + matrix[3][1]);
//        if (true) return;

        /*
        ExtendSimPluginCaller caller = new ExtendSimPluginCaller("ExtendSimPlugin");
        caller.openModel("C:\\CasualtyModel.mox");

        caller.setRealValue("conValue:#13", 4);
        caller.setRealValue("conValue:#12", 5);
        caller.runModel();
        System.out.println("check this meanValue out: " + caller.getStringValue("meanVal:#28:0:0:0:0"));
        System.out.println("check this Value out: " + caller.getStringValue("Value:#41:0:0:0:0"));

        System.out.println("check this meanValue out: " + caller.getStringValue("meanVal:#28"));
        System.out.println("check this Value out: " + caller.getStringValue("Value:#41"));
        */
    }

    public static void apiTest() {
        /* open a connection and log in to DOME server */
        DomeConnection conn = new DomeConnection("guest", "", "localhost:8080");
        DomeInterface polymerCuringInterface = conn.getInterfaceByPath("Server", "Public/extendsim/extendsim demo integration/default interface");

        /* instantiate Runtime Interface and retrieve the list of parameters */
        RuntimeInterface runtimeInterface = polymerCuringInterface.createRuntimeInterface();
        List paramList = runtimeInterface.getParameters();
        System.out.println("[INIT PARAM LIST] " + paramList);

//        /* changes the values of parameters */
//        RuntimeParameter xParam = runtimeInterface.getParameterByName("target X");
//        xParam.setRealValue(7);

        /* add custom value/status listener and set execution time limit -> this step is optional */
        runtimeInterface.addParameterValueChangeListener(new MyValueChangeListener1());
        runtimeInterface.addParameterStatusChangeListener(new MyStatusChangeListener1());
//        runtimeInterface.setExecutionTimeLimit(10000); // if execution time is not explicitly set, there will be no time limit.

        /* submit changes and handle execution time limit exception */
        try {
            runtimeInterface.submit();
        } catch (ExecutionTimeLimitException e) {
            System.out.println("Execution time expired.");
        }

        /* retrieve the results */
        System.out.println("[RESULT PARAM LIST] " + paramList);
        /* log out and close connection */
        conn.close();
    }

    static class MyValueChangeListener1 implements ParameterValueChangeListener {
        public void valueChanged(ParameterValueChangeEvent event) {
            System.out.println("My value listener : Parameter value changed = " + event);
        }
    }

    static class MyStatusChangeListener1 implements ParameterStatusChangeListener {
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

    public static void main4(String[] args) {
        ExtendSimPluginCaller caller = new ExtendSimPluginCaller("ExtendSimPlugin");
//        caller.openModel("C:\\MissileModel.mox");
        //caller.setRealValue("MeanDist:#0:0:0", 50);
//        Thread runThread = new Thread() {
//            public void run() {
//                ExtendSimPluginCaller caller = new ExtendSimPluginCaller("ExtendSimPlugin");
//                caller.runModel();
//                caller.finishUsingThisCaller();
//            }
//        };
//        runThread.start();
//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) { }
//        caller.runModel();

//        caller.sendBlockMessage(60, "test msg");
//        caller.setRealValue("conValue:#60", 10);

//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) { }
//
//        caller.executeCommand("ExecuteMenuCommand(6003);");
//        if (true) { return; }

//        caller.setRealValue("conValue:#56:0:0", 30);
//        caller.sendBlockMessage(56, "test msg");
//        caller.sendBlockMessage(52, "test msg");
//        double result = caller.getRealValue("conValue:#56:0:0:0:0");
//        caller.executeCommand("GlobalStr0 = GetAppPath();");

        String globalStr = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            double currentTime = caller.getCurrentTime();
            System.out.println("currentTime = " + currentTime);
//            if (currentTime > 247) {
            if (currentTime > 5) {
//                caller.executeCommand("ExecuteMenuCommand(6003);");
//                caller.executeCommand("PauseSim();");
                caller.executeCommand("ExecuteMenuCommand(30001);");
                break;
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) { }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("100 execution took " + (endTime - startTime));

        try { Thread.sleep(3000); } catch (Exception e) {}
        caller.executeCommand("ExecuteMenuCommand(30002);");

        try { Thread.sleep(1000); } catch (Exception e) {}
        caller.executeCommand("ExecuteMenuCommand(30002);");

        caller.executeCommand("SendMsgToBlock(1, 1);");

        if (true) { return; }

        String currentTime = caller.getStringValue("CurrentTime:#0:0:0");
        System.out.println("oldGlobal = " + globalStr);
        System.out.println("currentTime = " + currentTime);
//        System.out.println("result = " + result);
        //caller.closeModel("C:\\MissileModel.mox");
        //caller.finishUsingThisCaller();
    }

    public static void main3(String[] args) {
//        double[][] matrix = ExtendSimPluginCaller.getRealMatrix("data:#73:0:1:3:2");
//        System.out.println("matrix[0][0] = " + matrix[0][0] + ", matrix[3][1] = " + matrix[3][1]);
//        if (true) return;

        ExtendSimPluginCaller caller = new ExtendSimPluginCaller("ExtendSimPlugin");
        caller.openModel("C:\\Bank Link.mox");
        //caller.setRealValue("MeanDist:#0:0:0", 50);
        caller.runModel();
        double result = caller.getRealValue("num1:#7:0:0");
        System.out.println("result = " + result);

        System.out.println("check this matrix out: " + caller.getStringValue("data:#73:0:1:3:2"));
        System.out.println("check this value out: " + caller.getStringValue("statTable:#51:0:0:1:1"));
    }

    public static void main2(String[] args) {
        ExtendSimPluginCaller caller = new ExtendSimPluginCaller("ExtendSimPlugin");
        caller.openModel("C:\\VB Example.mox");
        caller.setRealValue("MeanDist:#0:0:0", 50);
        caller.runModel();
        double result = caller.getRealValue("Meanval:#1:0:0");
        System.out.println("result = " + result);
        System.out.println("check this value out: " + caller.getRealValue("conValue:#3:0:0"));
        System.out.println("check this matrix out: " + caller.getStringValue("data:#4:0:0:10:10"));

//        caller.closeModel("C:\\VB Example.mox");

//        caller.execute("OpenExtendFile(\"C:\\VB Example.mox\");");
//        caller.execute("OpenExtendFile(\"C:\\VB Example.mox\");");
//        caller.poke("system", "MeanDist:#0:0:0", "50.0");
//        caller.execute("ExecuteMenuCommand(6000);");
//        System.out.println(caller.request("system", "Meanval:#1:0:0"));
//        caller.execute("SetDirty(0);");
//        caller.execute("ExecuteMenuCommand(4);");
//        caller.execute("ExecuteMenuCommand(1);");
//        caller.poke("system", "global0:#0:0:0:0:0", "77.7");
//        System.out.println(caller.requestDouble("system", "global0:#0:0:0:0:0"));
//        caller.execute("userError(global0);");
//        caller.openModelOrSetFocus("C:\\dome3\\models\\extendsim\\VB Example.mox");
//        caller.closeModel("C:\\dome3\\models\\extendsim\\VB Example.mox");
    }
}
