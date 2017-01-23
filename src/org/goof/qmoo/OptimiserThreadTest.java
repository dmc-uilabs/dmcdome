//
//  OptimiserThreadTest.java
//  QMOO
//
//  Created by wronski on Wed Jan 28 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package org.goof.qmoo;

import org.goof.rts.AnyMap;
import org.goof.qmoo.objectives.QV;

class OptimiserRunner implements Runnable
{
  int number;

  public OptimiserRunner(int n)
  {
      number = n;
  }

  public void run()
  {	
      System.out.println("Starting thread " + number);
	
      AnyMap m = new AnyMap("/Users/jacobwronski/developer/qmoo/lib/java_settings.txt");	
      QV qv = new QV();
      ThreadEvaluator te = new ThreadEvaluator(qv);
      Simple simple_monitor = new Simple(2, "");
	
      m.setString("java.classpath", "/Users/jacobwronski/developer/qmoo/java");
      m.setEnvironment("java.environment");
	
      m.setString("evaluator.name", "external_evaluator");
      m.setString("run.results_dir", "/Users/jacobwronski/developer/qmoo/c_results");
      m.setString("run.name", "jni_thread_test");
	
      m.setString("objective.eename", "evaluators/jni_evaluator");
	
      m.setObject("objective.java.object", te);
      m.setObject("monitors[1].java.object", simple_monitor);

      System.out.println("Thread " + number + " : Creating optimiser");
      Optimiser o = new Optimiser(m);
	
      System.out.println("Thread " + number + " : Optimiser created");
      System.out.println("Thread " + number + " : Starting run");
	
      o.run();
	
      System.out.println("Thread " + number + " : Run complete");
  }
}

public class OptimiserThreadTest 
{
  public static void main(String[] args)
  {
	for (int i = 0; i < 5; i++)
	{
	  OptimiserRunner r = new OptimiserRunner(i);
	  Thread t = new Thread(r);
	  t.start();
	}
  }
}
