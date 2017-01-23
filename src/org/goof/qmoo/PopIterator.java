package org.goof.qmoo;

import org.goof.qmoo.Individual;

public class PopIterator
{
    static
    {
        System.loadLibrary("cmoojava");
    }

    public int _iptr, _pptr;

    public PopIterator(int iptr, int pptr)
    {
        _iptr = iptr;
        _pptr = pptr;
    }

    public void finalize()
    {
        deleteIteratorNative(_iptr);
    }

    public Individual dereference()
    {
        return new Individual(dereferenceNative(_iptr));
    }

    public boolean next()
    {
        nextNative(_iptr);
        return validNative(_iptr, _pptr);
    }

    public boolean valid()
    {
        return validNative(_iptr, _pptr);
    }

    private native int dereferenceNative(int iptr);

    private native void nextNative(int iptr);

    private native boolean validNative(int iptr, int pptr);

    private native void deleteIteratorNative(int iptr);
}
