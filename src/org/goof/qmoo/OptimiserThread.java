//
//  OptimiserThread.java
//  QMOO
//
//  Created by wronski on Wed Jan 28 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
package org.goof.qmoo;

import org.goof.rts.AnyMap;
import org.goof.qmoo.Simple;
import org.goof.qmoo.Optimiser;
import org.goof.qmoo.objectives.QV;

import java.lang.Runnable;

public class OptimiserThread implements Runnable
{
    private String _threadId;
    
    public OptimiserThread(String threadId)
    {
	_threadId = threadId;
    }
    
    public void run()
    {
	System.out.println(" ... starting thread " + _threadId);
	
	AnyMap m = new AnyMap("/Users/jacobwronski/developer/qmoo/lib/java_settings.txt");	
	QV qv = new QV();
	Simple simple_monitor = new Simple(2, _threadId);
	
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
	System.out.println("Creating optimiser");
	Optimiser o = new Optimiser(m);
	System.out.println("Optimiser created");
	System.out.println("Starting run");
	o.run();
	System.out.println("Run complete");
	
    }
}
