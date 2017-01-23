//
//  TestOptimiserInJavaThreads.java
//  QMOO
//
//  Created by wronski on Wed Jan 28 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package org.goof.qmoo;

import org.goof.qmoo.OptimiserThread;

import java.lang.Thread;

public class TestOptimiserInJavaThreads 
{
    public static void main(String[] args)
    {
	System.out.println("... creating thread Geoff ...");
	OptimiserThread geoff = new OptimiserThread("Geoff");
	Thread geoffThread = new Thread(geoff);
	
	System.out.println("... creating thread Jacob ...");
	OptimiserThread jacob = new OptimiserThread("Jacob");
	Thread jacobThread = new Thread(jacob);
	
	System.out.println("... running thread Geoff ...");
	geoffThread.start();
	
	System.out.println("... running thread Jacob ...");
	jacobThread.start();
    }
}
