package org.goof.qmoo.objectives;

import org.goof.rts.AnyMap;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Dec 3, 2003
 * Time: 11:24:09 AM
 * To change this template use Options | File Templates.
 */
public class DO
{
    int nvars;

    public DO()
    {
    }

    public void start(AnyMap obj, AnyMap space)
    {
        obj.setInt("nobjs", 2);
        nvars = obj.defaultInt("nvars", 2);

        space.setString("[0].type", "real");
        space.setDouble("[0].low", -5.0);
        space.setDouble("[0].high", 5.0);
        space.setInt("[0].index", 0);

        space.setString("[1].type", "real");
        space.setDouble("[1].low", 10);
        space.setDouble("[1].high", 13);
        space.setInt("[1].index", 1);
    }

    public boolean evaluate(double[] vars, double[] objs)
    {
        System.out.println("variables ...");
        System.out.println(vars[0] + "      " + vars[1]);

        objs[0] = 2;
        objs[1] = 3;

        return true;
    }
}
