package mit.cadlab.dome3.objectmodel.model.tool;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: jacob
 * Date: Jun 6, 2003
 * Time: 1:56:33 PM
 * To change this template use Options | File Templates.
 */

public interface AnalysisTool extends Model
{
    public static final TypeInfo TYPE_INFO = new TypeInfo("AnalysisTool", "AnalysisTool");

    public static final String BUILD_VIEW = "build view";

    public static final Id BUILD_CONTEXT_ID = new Id("BUILD_CXT");

    public static final String FILENAME = "fileName";

    public static final String DEPENDENCY_INFO = "dependencyInfo";

    public String getToolTypeName();

	public String getToolXmlType();

    public Collection getAnalysisToolInterfaces();

    public ConnectionMappingManager getMappingManager();

    public AnalysisToolInterfaceManager getAnalysisToolInterfacesManager();

}
