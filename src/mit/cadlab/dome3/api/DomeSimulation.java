package mit.cadlab.dome3.api;

import java.util.List;
import java.util.Date;

/**
 * Interface for DomeModel, DomeProject, DomeIModel, and DomeAnalysisTool classes
 * User: sittha
 * Date: Feb 19, 2006
 * Time: 12:41:27 AM
 */
public interface DomeSimulation {

    public List getInterfaces();
    public DomeInterface getInterfaceByName(String findingInterfaceName);
    public String getType();

    public String getSimulationName();
    public String getSimulationId();
    public int getVersion();
    public String getDescription();
    public Date getLastModified();
}
