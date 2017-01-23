package org.goof.rts;

import java.lang.String;

public class AnyMap
{
    static
    {
        System.loadLibrary("rtsjava");
    }

    public int _cptr;
    private boolean freeMe;

    private AnyMap(int cptr)
    {
        getClassInfo();
        _cptr = cptr;
        freeMe = false;
    }

    public AnyMap()
    {
        getClassInfo();
        _cptr = newMap();
        freeMe = true;
    }

    public AnyMap(String filename)
    {
        getClassInfo();
        _cptr = newFileMap(filename);
        freeMe = true;
    }

    public void finalize()
    {
        if (freeMe) freeMap();
        releaseClassInfo();
    }

    public native String toString();

    public native void print();

    public native boolean exists(String name);

    public native int getInt(String name);

    public native double getDouble(String name);

    public native boolean getBoolean(String name);

    public native String getString(String name);

    public AnyMap getMap(String name)
    {
        return new AnyMap(getPtr(name));
    }

    public native void setInt(String name, int value);

    public native void setDouble(String name, double value);

    public native void setBoolean(String name, boolean value);

    public native void setObject(String name, Object value);

    public native void setString(String name, String value);

    public native void setEnvironment(String name);

    public native int defaultInt(String name, int value);

    public native double defaultDouble(String name, double value);

    public native boolean defaultBoolean(String name, boolean value);

    public native String defaultString(String name, String value);

    private native int getPtr(String name);

    private native void getClassInfo();

    private native void releaseClassInfo();

    private native int newMap();

    private native int newFileMap(String filename);

    private native void freeMap();
}
