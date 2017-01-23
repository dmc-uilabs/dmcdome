package com.ge.ceed.domeapi;

import java.io.PrintStream;
import java.util.Date;

/* 
 * This class is used to re-assign standard output stream and error output stream
 * for a temporary period so the accumulated messages in stdout and stderr can be 
 * used for alerting end user, eg via a GUI
 * The messages are stored in a custom output stream - a String buffer in this case
 * After a temporary period ends the caller has to 
 * terminateRedirectThread() and 
 * resetRedirectedStreams(); - where the original stdout and  stderr stream is recovered
 * author: Meena 
 *  
 */


public class RedirectStdOutStdErr {
	   private PrintStream m_StandardOut;
	   private PrintStream m_StandardErr;
	   private volatile boolean m_Running = true;
	   private StringBuffer m_strBuffer;
	   
	   
	   public RedirectStdOutStdErr() {
	    	
	   }
	    
	    /**
	     * Redirect stdout and stderr to a custom stream
	     * Save the original stdout and stderr stream
	     * 
	     */	    
	    public void startRedirect() {
	    	
            m_strBuffer = new StringBuffer(10);
	        
	        PrintStream printStream = new PrintStream(new CustomOutputStream(m_strBuffer));
	        
	        // keeps reference of standard output, and standard error  stream
	        m_StandardOut = System.out;
	        m_StandardErr = System.err;
	         
	        // re-assigns standard output stream and error output stream
	        System.setOut(printStream);
	        System.setErr(printStream);

	        Thread thread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                while (m_Running) {
                    }
	            }
	        });
	        thread.start();    	
	    	
	    	
	
	        
	        
	    }
	    /**
	     * Reset standard output stream and error output stream
	     * 
	     */
	    public StringBuffer getAccumulatedMessages() {
	       return m_strBuffer;
	    }
	    
	    /**
	     * Reset standard output stream and error output stream
	     * 
	     */
	    public void resetRedirectedStreams() {
	       System.setOut(m_StandardOut);
	       System.setErr(m_StandardErr);
	    }
    
	    
	    /**
	     * Used for testing 
	     */	    
	    
	    public void terminateRedirectThread() {
	        m_Running = false;
	    }  	    
	    
	    public boolean isRedirectThreadRunning() {
	    	return m_Running;
	    }
	    
	    /**
	     * Prints all the messages that was redirected to the console
	     * Used for testing 
	     */
	    private void clearBuffer() {
	       	m_StandardOut.println(m_strBuffer);
	        m_StandardOut.println("Buffer is cleared");   

	    }

	     
	    /**
	     * Prints log statements for testing in a thread 
	     * Used for testing 
	     */
	    private void printLog() {
	        Thread thread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                while (m_Running) {
	                    System.out.println("Out -Time now is " + (new Date()));
	                    System.err.println("Err -Time now is " + (new Date()));
	                    try {
	                        Thread.sleep(1000);
	                    } catch (InterruptedException ex) {
	                        ex.printStackTrace();
	                    }
	                }
	            }
	        });
	        thread.start();
	    }
	     
	    /**
	     * Runs the program
	     */
	    public static void main(String[] args) throws InterruptedException {
	         
	       RedirectStdOutStdErr tt = new RedirectStdOutStdErr();
	       tt.startRedirect();
	       tt.printLog(); //Needed for testing
	       //Pause for 3 seconds
	       Thread.sleep(3000);
	       tt.terminateRedirectThread();
	       tt.clearBuffer();
	       tt.resetRedirectedStreams();
	       StringBuffer s = tt.getAccumulatedMessages();
	       System.out.println(" Check1");
	       System.out.println(s);
	       
	    }

	}
