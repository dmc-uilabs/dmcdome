package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.util.DomeJavaBean;
import mit.cadlab.dome3.network.server.Debug;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 11, 2004
 * Time: 9:02:08 PM
 * To change this template use Options | File Templates.
 */
public class AnalysisToolExecutionManager extends DomeJavaBean
{
    public static String SOLVING_STATUS = "solving status";
	public static String SOLVING_STARTED = "solving started";
	public static String SOLVING_PAUSED = "solving paused";
	public static String SOLVING_STOPPED = "solving stopped";

    private AnalysisToolBase _analysisTool;

    protected ModelChangeQueue changeQueue;

    public AnalysisToolExecutionManager(AnalysisToolBase analysisTool)
    {
        _analysisTool = analysisTool;
        changeQueue = new ModelChangeQueue(_analysisTool.getName());
    }

    public void startSolving()
	{
		runModel();
	}

    public void runModel()
    {
        firePropertyChange(SOLVING_STATUS, null, SOLVING_STARTED);
        try
        {
            executeAnalysisTool();
            firePropertyChange(SOLVING_STATUS, null, SOLVING_STOPPED);
        }
        catch (Exception e)
        {
            System.err.println("Solving process stopped due to exception: " + e.getMessage());
        }
    }

    protected boolean executeAnalysisTool()
	{
		Debug.trace(Debug.ALL, "executing analysis tool:" + _analysisTool.getName());

        if (_analysisTool instanceof OptimizationToolRuntime)
            ((OptimizationToolRuntime) _analysisTool).executeNativeAnalysisTool();
		return true;
	}
}
