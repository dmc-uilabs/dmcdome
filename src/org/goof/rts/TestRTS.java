package org.goof.rts;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 8, 2004
 * Time: 12:04:23 PM
 * To change this template use Options | File Templates.
 */

public class TestRTS
{
    public static void main(String[] args)
    {
        try
        {
            System.out.println("Hello");
            AnyMap a = new AnyMap();
            System.out.println("Hello again");
            System.out.println(a._cptr);
            a.setInt("poop", 1);
            System.out.println(a.getInt("poop"));
            a.setDouble("dub", 12.2);
            System.out.println(a.getDouble("dub"));
            a.setString("a.string", "here's a test string");
            System.out.println(a.getString("a.string"));
        }
        catch (java.lang.Exception e)
        {
            System.out.println(e.getMessage());
        }
        catch (java.lang.Throwable e)
        {
            System.out.println(e.getMessage());
        }
    }
}
