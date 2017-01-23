package org.goof.qmoo;

import org.goof.rts.AnyMap;
import org.goof.qmoo.Optimiser;
import org.goof.qmoo.objectives.QV;

public class TestOptimiser
{
    public static void main(String[] args)
    {
        try
        {
//      testOne();
            System.out.println("******** starting run 1 *********");
            testTwo();
            System.out.println("******** run 1 is over *********");

            System.out.println("******** starting run 2 *********");
            testTwo();
            System.out.println("******** run 2 is over *********");

            System.out.println("*******Test over************");
        }
        catch (java.lang.Exception e)
        {
            System.out.println(e.getMessage());
        }
        catch (java.lang.Throwable e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void testOne() throws java.lang.Exception
    {
        AnyMap m = new AnyMap("/Users/jacobwronski/developer/qmoo/lib/java_settings.txt");	// defaults come from text file

        m.setString("evaluator.name", "external_evaluator");
        m.setString("run.name", "full_jni_test");
        m.setString("run.results_dir", "/Users/jacobwronski/developer/c_results");

//    m.setString("objective.eename", "evaluators/plugin");
//    m.setString("objective.name", "objectives/QV");

        m.setString("java.classpath", "/Users/jacobwronski/developer/java");
        m.setEnvironment("java.environment");

        m.setString("objective.eename", "evaluators/jni");
        m.setString("objective.java.class", "org/goof/qmoo/QV");

        m.setString("monitors[1].java.class", "org/goof/qmoo/Simple");

        System.out.println("Optimiser Input");
        System.out.println(m.toString());

        System.out.println("Creating optimiser");
        Optimiser o = new Optimiser(m);
        System.out.println("Optimiser created");
        System.out.println("Starting run");
        o.run();
        System.out.println("Run complete");
    }

    public static void testTwo() throws java.lang.Exception
    {
        AnyMap m = new AnyMap("/Users/jacobwronski/developer/qmoo/lib/java_settings.txt");	// defaults come from text file
        QV qv = new QV();
        Simple simple_monitor = new Simple(2, "Jacob");

        System.out.println("qv's class is " + qv.getClass().getName());

        m.setString("java.classpath", "/Users/jacobwronski/developer/java");
        m.setEnvironment("java.environment");

        m.setString("objective.eename", "evaluators/jni");

        m.setString("objective.java.class", "org/goof/qmoo/QV");

        m.setString("evaluator.name", "external_evaluator");
        m.setString("run.name", "full_jni_test");
        m.setString("run.results_dir", "/Users/jacobwronski/developer/c_results");

        m.setString("objective.eename", "evaluators/jni");

        m.setObject("objective.java.object", qv);

        m.setObject("monitors[1].java.object", simple_monitor);

//    System.out.println("Optimiser Input");
//    System.out.println(m.toString());

        System.out.println("Creating optimiser");
        Optimiser o = new Optimiser(m);
        System.out.println("Optimiser created");
        System.out.println("Starting run");
        o.run();
        System.out.println("Run complete");
    }

}

