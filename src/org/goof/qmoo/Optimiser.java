package org.goof.qmoo;

import org.goof.rts.AnyMap;

public class Optimiser
{
  private int		_c_run_ptr;

  static { System.loadLibrary("cmoojava"); }

  public Optimiser(AnyMap m)
  {
      System.out.println("Entering Optimiser constructor");
      _c_run_ptr = newRun(m);
      System.out.println("Leaving Optimiser constructor");
  }

  public void finalize()
  {
      freeRun(_c_run_ptr);
  }

  public void run()			{ pRun(_c_run_ptr); }
  public void notes()			{ pNotes(_c_run_ptr); }

  private native int newRun(AnyMap m);
  private native void freeRun(int ptr);
  private native void pRun(int ptr);
  private native void pNotes(int ptr);
}
