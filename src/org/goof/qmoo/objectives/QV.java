package org.goof.qmoo.objectives;

import java.util.*;
import java.lang.Math;

import org.goof.rts.AnyMap;

import org.goof.qmoo.Objective;
import org.goof.qmoo.Individual;

public class QV implements Objective
{
    int nvars;

    public QV()
    {
    }

    public void start(AnyMap obj, AnyMap space)
    {
        System.out.println("In QV.start");
        obj.setInt("nobjs", 2);
        nvars = obj.defaultInt("nvars", 30);

        space.setString("[0].type", "real");
        space.setDouble("[0].low", -5.0);
        space.setDouble("[0].high", 5.0);
        space.setInt("[0].range[0]", 0);
        space.setInt("[0].range[1]", nvars - 1);
    }

    public boolean evaluate(double[] vars, double[] objs)
    {
        //    System.out.print("Evaluating : first var = ");
        //    System.out.println(vars[0]);

        double y1 = 0.0, y2 = 0.0;
        for (int i = 0; i < nvars; ++i)
        {
            double x = vars[i];
            y1 += x * x - 10.0 * Math.cos(2.0 * Math.PI * x) + 10.0;
            x -= 1.5;
            y2 += x * x - 10.0 * Math.cos(2.0 * Math.PI * x) + 10.0;
        }
        objs[0] = Math.pow(y1 / 10.0, 0.25);
        objs[1] = Math.pow(y2 / 10.0, 0.25);
        return true;
    }

    public boolean evaluate(Individual z)
    {
        //    System.out.print("Evaluating : first var = ");
        //    System.out.println(vars[0]);

        double y1 = 0.0, y2 = 0.0;
        for (int i = 0; i < nvars; ++i)
        {
            double x = z.getVariable(i);
            y1 += x * x - 10.0 * Math.cos(2.0 * Math.PI * x) + 10.0;
            x -= 1.5;
            y2 += x * x - 10.0 * Math.cos(2.0 * Math.PI * x) + 10.0;
        }
        z.setObjective(0, Math.pow(y1 / 10.0, 0.25));
        z.setObjective(1, Math.pow(y2 / 10.0, 0.25));
        return true;
    }
}
