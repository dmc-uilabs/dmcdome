package org.goof.qmoo;

public class Individual
{
    static
    {
        System.loadLibrary("cmoojava");
    }

    public int _cptr;

    public static final int ALIVE = 6;

    public Individual(int cptr)
    {
        _cptr = cptr;
    }

    public int getState()
    {
        return getStateNative(_cptr);
    }

    public int getRank()
    {
        return getRankNative(_cptr);
    }

    public int getGroup()
    {
        return getGroupNative(_cptr);
    }

    public double getObjective(int i)
    {
        return getObjectiveNative(_cptr, i);
    }

    public void setObjective(int i, double value)
    {
        setObjectiveNative(_cptr, i, value);
    }

    public double getVariable(int i)
    {
        return getVariableNative(_cptr, i);
    }

    public int getcptr()
    {
        return _cptr;
    }

    // don't call this function
    private native int getStateNative(int cptr);

    private native int getRankNative(int cptr);

    private native int getGroupNative(int cptr);

    private native double getObjectiveNative(int cptr, int i);

    private native void setObjectiveNative(int cptr, int i, double value);

    private native double getVariableNative(int cptr, int i);
}
