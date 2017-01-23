package mit.cadlab.dome3.api.demo;

import mit.cadlab.dome3.api.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * User: Sangmok Han
 * Date: 2005. 3. 30.
 */
public class TutorialPartFive extends JFrame {

    RuntimeInterface runtimeInterface1;
    RuntimeInterface runtimeInterface2;
    JTextArea textArea = null;
    DomeConnection conn = null;
    MonitorFrame monitorFrame1;
    MonitorFrame monitorFrame2;
    JButton execButton = null;
    JScrollPane scrollPane = null;
    public static long START_TIME = System.currentTimeMillis();

    public TutorialPartFive(MonitorFrame monitorFrame1, MonitorFrame monitorFrame2) {
        setLocation(0,0);
        this.monitorFrame1 = monitorFrame1;
        this.monitorFrame2 = monitorFrame2;

        textArea = new JTextArea("PLEASE WAIT UNTIL LOADING TWO INTERFACES");
        textArea.setLineWrap(true);
        textArea.append("\r\nPREPARING TO USE 'polymer curing interface' of 'Polymer curing model'");
        textArea.append("\r\nPREPARING TO USE 'integrated model interface' of 'As cured lamina properties'");

        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        execButton = new JButton("Submit & Start Solving");
        execButton.setEnabled(false);;
        execButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                println("SUBMIT BUTTON CLICKED");
                execute();
            }
        });
        getContentPane().add(execButton, BorderLayout.SOUTH);

        setTitle("Main Flow");

        pack();
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cleanup();
                System.exit(0);
            }
        });

        prepare();
        println("BOTH INTERFACES ARE READY TO SUBMIT CHANGES");
        execButton.setEnabled(true);
    }

    public static void main(String[] args) {
        MonitorFrame monitorFrame1 = new MonitorFrame(); // used to monitor the first runtime interface
        MonitorFrame monitorFrame2 = new MonitorFrame(); // used to monitor the second runtime interface
        new TutorialPartFive(monitorFrame1, monitorFrame2);
    }

    public void prepare() {
        /* open a connection and log in to DOME server */
        conn = new DomeConnection("tutorialUser", "123", "localhost:8080");

        /* browse folders, models and Interfaces */
        DomeFolder publicFolder = conn.getPublicFolder();
        DomeFolder tutorialExampleFolder = publicFolder.getFolder("Tutorial examples");
        DomeModel polymerCuringModel = tutorialExampleFolder.getModelByName("Polymer curing model");
        DomeInterface polymerCuringInterface = polymerCuringModel.getInterfaceByName("polymer curing interface");
        DomeProject laminaProject = tutorialExampleFolder.getProjectByName("As cured lamina properties");
        DomeInterface laminaInterface = laminaProject.getInterfaceByName("integrated model interface");

        /* instantiate Runtime Interface and retrieve the list of parameters */
        runtimeInterface1 = polymerCuringInterface.createRuntimeInterface();
        runtimeInterface2 = laminaInterface.createRuntimeInterface();

        /* add custom value/status listener and set execution time limit -> this step is optional */
        runtimeInterface1.addParameterValueChangeListener(new MyValueChangeListener5(monitorFrame1));
        runtimeInterface1.addParameterStatusChangeListener(new MyStatusChangeListener5(monitorFrame1));
        runtimeInterface1.setExecutionTimeLimit(10000);

        runtimeInterface2.addParameterValueChangeListener(new MyValueChangeListener5(monitorFrame2));
        runtimeInterface2.addParameterStatusChangeListener(new MyStatusChangeListener5(monitorFrame2));
        runtimeInterface2.setExecutionTimeLimit(10000);
    }

    public void execute() {
        /* changes the values of parameters */
        RuntimeParameter nominalParam = runtimeInterface1.getParameterByName("nominalModulus");
        nominalParam.setRealValue(nominalParam.getRealValue() - 1);

        RuntimeParameter curingTemperature = runtimeInterface2.getParameterByName("lamina curing temperature");
        curingTemperature.setRealValue(curingTemperature.getRealValue() + 0.5);

        /* submit changes and proceed to the next line. get a tracker for solving thread */
        SolverStateTracker stateTracker1 = runtimeInterface1.submitAndGo();
        println("[INFO] keeping running after submitAndGo() of runtimeInterface1");
        SolverStateTracker stateTracker2 = runtimeInterface2.submitAndGo();
        println("[INFO] keeping running after submitAndGo() of runtimeInterface2");

        monitorFrame1.startMonitoring(stateTracker1);
        monitorFrame2.startMonitoring(stateTracker2);
    }

    public void println(Object input) {
        textArea.append("\r\n" + "" + (System.currentTimeMillis() - TutorialPartFive.START_TIME) + " ms - " + input);
        textArea.setCaretPosition(textArea.getDocument().getLength() - 1);
        //scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    public void cleanup() {
        /* log out and close connection */
        if (conn != null) {
            conn.close();
        }
    }
}

/**
 * This class has its own thread different from main()'s thread.
 * This class demonstrate that we can write a program that monitors the status of solving
 * even when we let the main thread continues its program flow.
 * It is possible because if we pass the tracker object (SolverStateTracker)
 * to some other class, the class can get information
 * about solving status - whether solving finished or not - using stateTracker.getStatus()
 */
class MonitorFrame extends JFrame implements Runnable {
    JTextArea textArea = null;
    SolverStateTracker stateTracker;
    Thread monitorThread = null;
    public static int Y_POS = 0;
    JScrollPane scrollPane = null;

    public MonitorFrame() {
        setLocation(450,  Y_POS);
        Y_POS += 450;
        textArea = new JTextArea("MONITOR LOG PRINTED HERE");
        textArea.setLineWrap(true);

        getContentPane().setLayout(new BorderLayout());
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setTitle("Solving Monitor");
        pack();
        setVisible(true);
    }

    public void startMonitoring(SolverStateTracker stateTracker) {
        this.stateTracker = stateTracker;
        monitorThread = new Thread(this);
        monitorThread.start();
    }

    public void run() {
        try {
            println("[INITIAL PARAM] " + stateTracker.getRuntimeInterface().getParameters());

            /* monitor runs for 10 seconds (= 50 x 200 milliseconds) */
            while (stateTracker.status() == SolverStateTracker.SUCCESS) {
                /* retrieve the state of solving from the state tracker */
                if (stateTracker.status() == SolverStateTracker.RUNNING) {
                    println("[SOLVING THREAD STATE] statue = RUNNING");
                } else if (stateTracker.status() == SolverStateTracker.SUCCESS) {
                    println("[SOLVING THREAD STATE] statue = SUCCESS");
                } else if (stateTracker.status() == SolverStateTracker.FAILURE) {
                    println("[SOLVING THREAD STATE] statue = FAILURE");
                }
                monitorThread.sleep(200);
            }
            println("[SOLVING THREAD STATE] statue = SUCCESS");
            println("[RESULT PARAM] " + stateTracker.getRuntimeInterface().getParameters());
        } catch (InterruptedException e) { System.out.println(e); }
    }

    public void println(Object input) {
        textArea.append("\r\n" + "" + (System.currentTimeMillis() - TutorialPartFive.START_TIME) + " ms - " + input);
        textArea.setCaretPosition(textArea.getDocument().getLength() - 1);
        //scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }
}


class MyValueChangeListener5 implements ParameterValueChangeListener {
    MonitorFrame monitorFrame = null;

    public MyValueChangeListener5(MonitorFrame monitorFrame) {
        this.monitorFrame = monitorFrame;
    }

    public void valueChanged(ParameterValueChangeEvent event) {
        monitorFrame.println("My value listener : Parameter value changed = " + event);
    }
}

class MyStatusChangeListener5 implements ParameterStatusChangeListener {
    MonitorFrame monitorFrame = null;

    public MyStatusChangeListener5(MonitorFrame monitorFrame) {
        this.monitorFrame = monitorFrame;
    }

    public void statusChanged(ParameterStatusChangeEvent event) {
        monitorFrame.println("My status listener : Parameter status changed = " + event);

        /* ParameterStatusChangeListener */
        if (ParameterStatusChangeEvent.SOLVING_SUCCESS_EVENT.equals(event)) {
            monitorFrame.println("My status listener : Solving succeed");
        } else if (ParameterStatusChangeEvent.SOLVING_FAILURE_EVENT.equals(event)) {
            monitorFrame.println("My status listener : Solving failed");
        }
    }
}