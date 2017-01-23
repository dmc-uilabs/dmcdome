package toolinterfaceworkspace.objectmodel.toolinterface;

import org.dom4j.Element;

import java.util.List;
import java.util.ArrayList;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.model.Model;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 11:56:57 AM
 * To change this template use Options | File Templates.
 */
public class InterfaceToolView extends AbstractModelObjectScope
{
    protected Context _toolCausalityViewContext;

    protected ToolInterface mi;

    public Model getModel()
    {
        return null;
    }

    public String getXmlTag()
    {
        return null;
    }

    protected void addXmlContent(Element xmlElement)
    {
    }

    protected String contentToString()
    {
        return null;
    }

    public List getValidModelObjectTypes()
    {
        return null;
    }

    public InterfaceToolView(ToolInterface mi, Element xmlElement, Element xmlMappings)
    {
        super(xmlElement);
    }

    public InterfaceToolView(ToolInterface mi, Id id)
    {
        super(id);
        this.mi = mi;
        _toolCausalityViewContext = createToolCausalityViewContext();
        modelObjects.add(_toolCausalityViewContext);
    }

    protected Context createToolCausalityViewContext()
	{
		Object[] ctrParams = new Object[]{this, ToolInterface.TOOL_CONTEXT_ID};
		Context cxt = (Context) getModelObjectFactory().newInstance("Context", ctrParams);
		if (cxt == null)
			throw new DomeObjectException("InterfaceToolView: createToolViewContext failed");
		cxt.setName(ToolInterface.CAUSALITY_VIEW);
		return cxt;
	}

    protected TypeInfo getTypeInfo()
    {
        return ToolInterface.TYPE_INFO;
    }

    public ModelObjectFactory getModelObjectFactory()
    {
		return mi.getModelObjectFactory();
    }





}
