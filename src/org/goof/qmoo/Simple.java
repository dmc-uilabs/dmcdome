package org.goof.qmoo;

import org.goof.rts.AnyMap;
import org.goof.qmoo.Monitor;
import org.goof.qmoo.Population;
import org.goof.qmoo.Individual;
import org.goof.qmoo.PopIterator;

import java.util.Vector;

public class Simple
{
    int nobjs;
    private String _threadId;

    public Simple(AnyMap root_map, AnyMap monitor_map)
    {
        nobjs = root_map.getInt("objective.nobjs");
    }

    public Simple(int n, String threadId)
    {
        _threadId = threadId;
        nobjs = n;
    }

    public void go(Monitor m)
    {
        Population pop = m.getPopulation();
        System.out.println("org.goof.qmoo.Simple: " + m.numEvaluations() + " evals, pop size = " + pop.count(Individual.ALIVE) + " :");

        Vector best = new Vector();
        for (int oi = 0; oi < nobjs; ++oi)
            best.addElement(new Integer(0));


        PopIterator it = pop.newIterator();
        while (it.valid())
        {
            Individual z = it.dereference();
            if (z.getState() == Individual.ALIVE && z.getRank() == 1)
            {
                for (int oi = 0; oi < nobjs; ++oi)
                {
                    if (best.elementAt(oi) instanceof Individual)
                    {
                        Individual b = (Individual) best.elementAt(oi);
                        if (z.getObjective(oi) < b.getObjective(oi))
                            best.setElementAt(z, oi);
                    }
                    else
                        best.setElementAt(z, oi);
                }
            }
            it.next();
        }

        for (int oi = 0; oi < nobjs; ++oi)
        {
            if (best.elementAt(oi) instanceof Individual)
            {
                Individual b = (Individual) best.elementAt(oi);
                System.out.print("Thread: " + _threadId + "  best individual for objective " + oi + " : ");
                for (int oj = 0; oj < nobjs; ++oj)
                    System.out.print(b.getObjective(oj) + " ");
                System.out.println();
            }
        }

    }
}
