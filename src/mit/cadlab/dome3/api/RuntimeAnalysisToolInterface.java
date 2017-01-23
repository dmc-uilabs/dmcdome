package mit.cadlab.dome3.api;

import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.client.DomeRuntimeClient;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.gui.objectmodel.DomeFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationInterfaceResultsPanel;
import mit.cadlab.dome3.swing.WindowTracker;

import javax.swing.*;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Mar 31, 2005
 * Time: 3:02:20 PM
 * To change this template use Options | File Templates.
 */
public class RuntimeAnalysisToolInterface
{
    private OptimizationInterfaceRuntimeClient _interfaceClient;
    private Hashtable changedParameters = new Hashtable();
    private long executionTimeLimit = 0;
    private DomeConnection _domeConn;
    private ClientPlayspaceRuntime playspaceClient;
    private DomeInterface _domeInterface;
    private Vector _clientProjectVector = null;

    private DomeFrame _resultsFrame = null;

    private Set statusChangeListeners = new HashSet();

    private PropertyChangeListener _solutionListener;

    public RuntimeAnalysisToolInterface (DomeInterface domeInterface, DomeConnection conn)
    {
        _domeInterface = domeInterface;
        _domeConn = conn;

        CompoundId interfaceCompId = new CompoundId("CID.STAT..IF" + domeInterface.getInterfaceId() + ".ML" + domeInterface.getParentAnalysisTool().getAnalysisToolId() + "\nRUN.");

        DomeRuntimeClient drClient = _domeConn.getDomeRuntimeClient();
        ServerConnection serverConn = _domeConn.getServerConnection();

        /* create transient playspace and create interface */
        playspaceClient = drClient.createTransientPlayspace(_domeConn.getServerConnection());
        interfaceCompId.setPlayspaceStaticId(playspaceClient.getCompoundId().getPlayspaceStaticId());
        interfaceCompId.setPlayspaceRuntimeId(playspaceClient.getCompoundId().getPlayspaceRuntimeId());
        _interfaceClient = drClient.createAnalysisToolInterface(interfaceCompId, playspaceClient, serverConn);

        _domeConn.addToAnalysisToolRuntimeInterfaceList(this);

        createAnalysisToolInterfaceGUI();

        createAnalysisToolInterfaceListeners();

    }

    public RuntimeAnalysisToolInterface (DomeInterface domeInterface, RuntimePlayspace playspace, DomeConnection conn)
    {
        _domeInterface = domeInterface;
        _domeConn = conn;

        CompoundId interfaceCompId = new CompoundId("CID.STAT..IF" + domeInterface.getInterfaceId() + ".ML" + domeInterface.getParentModel().getModelId() + "\nRUN.");

        DomeRuntimeClient drClient = _domeConn.getDomeRuntimeClient();
        ServerConnection serverConn = _domeConn.getServerConnection();

        /* initialize playspaceClient with given runtime playspace and create interface */
        playspaceClient = playspace.getClientPlayspaceRuntime();
        interfaceCompId.setPlayspaceStaticId(playspaceClient.getCompoundId().getPlayspaceStaticId());
        interfaceCompId.setPlayspaceRuntimeId(playspaceClient.getCompoundId().getPlayspaceRuntimeId());
        _interfaceClient = drClient.createAnalysisToolInterface(interfaceCompId, playspaceClient, serverConn);

        _domeConn.addToAnalysisToolRuntimeInterfaceList(this);

        /**
         * in the future, this should create various guis based on the type of the analysis tool being used
         */

        createAnalysisToolInterfaceGUI();

        createAnalysisToolInterfaceListeners();
    }

    protected void createAnalysisToolInterfaceGUI()
    {
        _resultsFrame = new DomeRunFrame(new OptimizationInterfaceResultsPanel(_interfaceClient),null);
        _resultsFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent event)
            {
                _resultsFrame = null;
            }
        });
        _resultsFrame.show();
    }

    protected void createAnalysisToolInterfaceListeners()
    {
        _solutionListener = new OptimizationSolutionListener();
        _interfaceClient.addPropertyChangeListener(_solutionListener);
        _interfaceClient.listenerAdded();
    }

    public void startAnalysisToolSolving()
    {
        _interfaceClient.startOptimization();
    }

    class OptimizationSolutionListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent pe)
        {
            String propName = pe.getPropertyName();
            if (propName.equals(OptimizationInterfaceRuntimeClient.RESET_PLOT))
                ((OptimizationInterfaceResultsPanel)_resultsFrame.getGui()).getParetoFrontPanel().resetPlot();
            else if (propName.equals(OptimizationInterfaceRuntimeClient.NEW_INDIVIDUAL_ADDED))
            {
                Vector v = (Vector) pe.getNewValue();
                ((OptimizationInterfaceResultsPanel)_resultsFrame.getGui()).updateParetoPlot(v);
            }
            else if (propName.equals(OptimizationInterfaceRuntimeClient.OPTIMIZATION_ANALYSIS_COMPLETE))
            {
                System.out.println("Optimization is complete!!!");
            }
            else
                return;
        }
    }

}