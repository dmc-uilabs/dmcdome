package org.goof.qmoo;

import org.goof.qmoo.Individual;

public class EvaluatorData
{
    static
    {
        System.loadLibrary("cmoojava");
    }

    public int _cptr;

    public EvaluatorData(int cptr)
    {
        _cptr = cptr;
    }

    public int count()
    {
        return countNative(_cptr);
    }

    public Individual next()
    {
        return new Individual(nextNative(_cptr));
    }

    public void done(Individual i, boolean feasible)
    {
        doneNative(_cptr, i.getcptr(), feasible);
    }

    public native int countNative(int cptr);

    public native int nextNative(int cptr);

    public native void doneNative(int cptr, int iptr, boolean feasible);
}
