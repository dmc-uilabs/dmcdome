package mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement;

import org.dom4j.Element;
import org.dom4j.DocumentHelper;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Jan 22, 2007
 * Time: 4:22:00 PM
 * To change this template use Options | File Templates.
 */
public class MappedParameter {
    private String parameterName;
    private String parameterID;
    private String parameterModelID;

    public MappedParameter(String paramName, String paramID, String paramModelID)
    {
        this.parameterName = paramName;
        this.parameterID = paramID;
        this.parameterModelID = paramModelID;
    }

    public String getName()
    {
        return parameterName;
    }

    public String getID()
    {
        return parameterID;
    }

    public String getModelID()
    {
        return parameterModelID;
    }

    public Element toXMLElement(String XML_TAG)
    {
        Element mappedParameter = DocumentHelper.createElement(XML_TAG);
        mappedParameter.addAttribute("name",parameterName);
        mappedParameter.addAttribute("id",parameterID);
        mappedParameter.addAttribute("modelID",parameterModelID);
        return mappedParameter;
    }
}
