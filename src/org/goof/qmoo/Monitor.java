package org.goof.qmoo;

import org.goof.rts.AnyMap;
import org.goof.qmoo.Population;

public class Monitor
{
  static { System.loadLibrary("cmoojava"); }
  public int				_cptr;

  public Monitor(int cptr, AnyMap m)	{ _cptr = cptr; }

  public int numEvaluations()		{ return numEvaluationsNative(_cptr); }
  public Population getPopulation()	{ return new Population(getPopNative(_cptr)); }

  private native int numEvaluationsNative(int cptr);
  private native int getPopNative(int cptr);
}