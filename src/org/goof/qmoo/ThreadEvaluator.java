package org.goof.qmoo;

import org.goof.rts.AnyMap;

import java.util.Vector;

public class ThreadEvaluator
{
    Vector threads;
    Objective o;

    public ThreadEvaluator(Objective oo)
    {
        o = oo;
    }

    public void start(AnyMap root, AnyMap objective, AnyMap space)
    {
        threads = new Vector();
        o.start(objective, space);
        for (int i = 0; i < 5; ++i)
        {
            EvaluatorThread t = new EvaluatorThread(o);
            t.start();
            threads.addElement(t);
        }
    }

    public void process(EvaluatorData e)
    {
        boolean stop = false;
        while (!stop)
        {
            boolean done_something = false;
            for (int i = 0; i < threads.size(); ++i)
            {
                EvaluatorThread t = (EvaluatorThread) threads.elementAt(i);
                if (t.done)
                {
                    done_something = true;
                    if (t.i != null)
                        e.done(t.i, true);
                    if (e.count() > 0)
                    {
                        t.i = e.next();
                        t.done = false;
                    }
                    else
                        stop = true;
                }
            }
            if (!done_something)
                stop = true;
        }
    }
}

class EvaluatorThread extends Thread
{
    Objective o;
    public Individual i;
    public boolean done;
    boolean stop;

    EvaluatorThread(Objective oo)
    {
        o = oo;
        done = true;
        stop = false;
    }

    public void run()
    {
        while (!stop)
        {
            if (!done)
            {
                o.evaluate(i);
                done = true;
            }
            else
                yield();
        }
    }
}