package org.goof.qmoo;

import org.goof.rts.AnyMap;

import java.util.Vector;

import org.goof.qmoo.Individual;
import org.goof.qmoo.EvaluatorData;

public class JEvaluator
{
    public JEvaluator()
    {
    }

    public void start(AnyMap root, AnyMap objective, AnyMap space)
    {
        System.out.println("Root");
        System.out.println(root.toString());

        System.out.println("Objective");
        System.out.println(objective.toString());

        System.out.println("space");
        System.out.println(space.toString());
    }

    public void Process(EvaluatorData e)
    {
        while (e.count() > 0)
        {
            Individual i = e.next();
            i.setObjective(1, i.getVariable(1));
            e.done(i, true);
        }
    }
}
