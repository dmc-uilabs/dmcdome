package mit.cadlab.dome3.objectmodel.model.tool;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.model.AbstractModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.build.AnalysisToolInterfaceManagerBuild;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.util.causality.AbstractCausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Sep 3, 2003
 * Time: 5:10:04 PM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractAnalysisTool extends AbstractModel
                                            implements AnalysisTool
{
    protected ModelObjectFactory moFactory;
    protected ConnectionMappingManager mappingManager;

    protected AnalysisToolInterfaceManager _interfaces;

    protected AbstractCausalityManager _analysisToolCausalityManager;

    protected ToolInterface _defaultToolInterface;

    protected String fileName = "";

    public AbstractAnalysisTool(Id id)
    {
        super(id);
    }

    public AbstractAnalysisTool(Element xmlElement)
    {
        super(xmlElement);
        if (xmlElement == null)
            throw new IllegalArgumentException(getTypeName() + " - no xml model info");
    }

    public AbstractAnalysisTool(String file, Element xmlElement)
    {
        this(xmlElement);
        this.fileName = file;
    }

    public AbstractAnalysisTool(Id id, AnalysisTool model)
    {
        super(id, model, false);
    }

    protected Context createBuildContext() {
        Object[] ctrParams = new Object[]{this, BUILD_CONTEXT_ID};
        Context cxt = (Context) getModelObjectFactory().newInstance("Context", ctrParams);
        if (cxt == null)
            throw new DomeObjectException("createBuildContext failed");
        cxt.setName(BUILD_VIEW);
        return cxt;
    }

    public ConnectionMappingManager getMappingManager()
    {
        return mappingManager;
    }

    protected void save(Document xmlDoc, String fileName) throws IOException
    {
        ((AnalysisToolInterfaceManagerBuild) _interfaces).save(fileName);
        super.save(xmlDoc, fileName);
    }

    public AnalysisToolInterfaceManager getAnalysisToolInterfacesManager()
    {
        return _interfaces;
    }

    // CausalitySupport interface
    protected CausalityManager getCausalityManager()
    {
        return _analysisToolCausalityManager;
    }

    protected abstract ConnectionMappingManager createConnectionMappingManager();

    protected abstract AnalysisToolInterfaceManager createAnalysisToolInterfacesManager();

    protected abstract AbstractCausalityManager createCausalityManager();

}
