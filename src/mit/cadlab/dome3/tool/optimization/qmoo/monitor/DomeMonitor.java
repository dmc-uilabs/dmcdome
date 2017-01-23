package mit.cadlab.dome3.tool.optimization.qmoo.monitor;

import org.goof.qmoo.Monitor;
import org.goof.qmoo.PopIterator;
import org.goof.qmoo.Population;
import org.goof.qmoo.Individual;

import java.beans.PropertyChangeSupport;
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;

import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOTool;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 13, 2004
 * Time: 6:36:40 PM
 * To change this template use Options | File Templates.
 */
public class DomeMonitor
{
    private QMOOTool _analysisTool;

    public DomeMonitor(QMOOTool analysisTool)
    {
        _analysisTool = analysisTool;
    }

    public void go(Monitor m)
    {
        _analysisTool.startNewIterationRecord();
        Population pop = m.getPopulation();
        System.out.println("Current population size:: " + pop.count(Individual.ALIVE));
        PopIterator it = pop.newIterator();
        while (it.valid())
        {
            Individual z = it.dereference();
            if (z.getState() == Individual.ALIVE)
            {

                sendIndividualToClient(z);

            }
            it.next();
        }
    }

    private void sendIndividualToClient(Individual z)
    {
        System.out.println("***** Sending Individuals To Client: *****");
        Vector variables = new Vector();
        Vector objectives = new Vector();
        Vector isRankOne = new Vector();

        List objectiveParameters = _analysisTool.getDomeObjectiveForQMOO().getObjectives();
        ListIterator iterator = objectiveParameters.listIterator();
        for (int i = 0; iterator.hasNext(); i++)
        {
            ObjectiveParameter objectiveParameter = (ObjectiveParameter) iterator.next();
            if (objectiveParameter.getIsMaxOrMinForEditor().equals(ObjectiveParameter.MAXIMIZE))
                objectives.add(new Double((-1.0)*z.getObjective(i)));
            else
                objectives.add(new Double(z.getObjective(i)));
        }
        for (int i = 0; i < _analysisTool.getDomeObjectiveForQMOO().getVariables().size(); i++)
            variables.add(new Double(z.getVariable(i)));
        if (z.getRank() == 1)
            isRankOne.add(new Boolean(true));
        else
            isRankOne.add(new Boolean(false));

        _analysisTool.sendIndividualToClient(isRankOne, variables, objectives);
    }
}
