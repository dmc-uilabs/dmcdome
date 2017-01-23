package org.goof.qmoo;

import org.goof.qmoo.PopIterator;

public class Population
{
    static
    {
        System.loadLibrary("cmoojava");
    }

    public int _cptr;

    public Population(int cptr)
    {
        _cptr = cptr;
    }

    public PopIterator newIterator()
    {
        return new PopIterator(newIteratorNative(_cptr), _cptr);
    }

    public int count(int state)
    {
        return countNative(_cptr, state);
    }

    private native int newIteratorNative(int cptr);

    private native int countNative(int cptr, int state);
}