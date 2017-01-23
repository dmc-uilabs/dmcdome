package mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.*;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

import java.awt.*;

import edu.iupui.rg.ucum.units.Unit;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Sep 11, 2003
 * Time: 5:01:35 PM
 * To change this template use Options | File Templates.
 */
public abstract class OptimizationParameter
{
    protected DataObjectFactory factory;
    protected AbstractParameter _parameter;
    protected DomeBoolean _isActive;

    public OptimizationParameter(ModelObjectScope scope, Id id, String dataType)
	{
        this._parameter = new ConcreteParameter(scope, id, dataType);

        this._isActive = new BooleanData(true);
    }

    public OptimizationParameter(ModelObjectScope scope, Element xmlElement)
    {
        _parameter = createOptimizationParameter(scope, xmlElement);

        _parameter.setName(xmlElement.attribute("name").getText());

        Element dataValue = (Element) xmlElement.selectSingleNode("quantity");

        ((DomeReal)_parameter.getCurrentDataObject()).setRealValue(new Double(dataValue.attribute("magnitude").getText()));
        ((DomeReal)_parameter.getCurrentDataObject()).setUnit(new Unit(dataValue.attribute("unit").getText()));
        _isActive = new BooleanData(xmlElement.selectSingleNode("active").getText());
    }

    public OptimizationParameter(ModelObjectScope scope, Id id, Parameter param)
    {
        _parameter = new ConcreteParameter(scope, id, param);

        // by default the ObjectiveParameter is not active
		_isActive = new BooleanData(true);
    }

    public Parameter getParameter()
    {
        return _parameter;
    }

    public void setIsActive(BooleanData value)
    {
        this._isActive = value;
    }

    public DomeBoolean getIsActive()
    {
        return this._isActive;
    }

    public Boolean getIsActiveForEditor()
    {
        return this._isActive.getBooleanValue();
    }

    public  Element toXmlElement()
    {
        Element xml = DocumentHelper.createElement(Parameter.XML_TAG);
        xml.addAttribute("id", this._parameter.getId().getIdString());
        xml.addAttribute("name", this._parameter.getName());
        xml.addElement("type").addText(this._parameter.getCurrentDataObject().getTypeName());
        Element value = xml.addElement("quantity");
        value.addAttribute("magnitude", ((DomeReal) this._parameter.getCurrentDataObject()).getQuantity().getMagnitude().toString());
        value.addAttribute("unit", this._parameter.getCurrentDataObject().getUnit().toString());
        return xml;
    }

    public Element toBuildViewXmlElement()
    {
        Element xml = DocumentHelper.createElement(Parameter.XML_TAG);
        xml.addAttribute("id", this._parameter.getId().getIdString());
        xml.addAttribute("name", this._parameter.getName());
        xml.addElement("type").addText(this._parameter.getCurrentDataObject().getTypeName());
        Element value = xml.addElement("quantity");
        value.addAttribute("magnitude", ((DomeReal) this._parameter.getCurrentDataObject()).getQuantity().getMagnitude().toString());
        value.addAttribute("unit", this._parameter.getCurrentDataObject().getUnit().toString());
        xml.addElement("active").addText(this._isActive.toString());
        return xml;
    }

    protected AbstractParameter createOptimizationParameter(ModelObjectScope scope, Element xmlElement)
    {
        /*
         * this method creates the appropriate parameter depending on
         * whether the scope is a tool model or an interface
         * and whether we are in the build, deploy, run or server mode
         */

        AbstractParameter parameter = null;
        if(scope instanceof AnalysisTool)
        {
            switch(DomeClientApplication.getMode())
            {
                case -1:
                    parameter = new ModelParameterRuntime(scope,
                            new Id(xmlElement.attribute("id").getText()), xmlElement.selectSingleNode("type").getText());
                    break;
                case 0:
                    parameter = new ConcreteParameter(scope,
                            new Id(xmlElement.attribute("id").getText()), xmlElement.selectSingleNode("type").getText());
                    break;
                case 4:
                    parameter = new ModelParameterRuntime(scope, new Id(xmlElement.attribute("id").getText()), xmlElement.selectSingleNode("type").getText());
                    break;
            }
        }
        else if(scope instanceof ToolInterface)
        {
            switch(DomeClientApplication.getMode())
            {
                case -1:
                    parameter = new InterfaceParameterRuntime(scope,
                            new Id(xmlElement.attribute("id").getText()), xmlElement.selectSingleNode("type").getText());
                    break;
                case 0:
                    parameter = new ConcreteParameter(scope,
                            new Id(xmlElement.attribute("id").getText()), xmlElement.selectSingleNode("type").getText());
                    break;
                case 1:
                    parameter = new InterfaceParameterClient(scope,
                            new Id(xmlElement.attribute("id").getText()), xmlElement.selectSingleNode("type").getText());
                    break;
                case 2:
                    parameter = new InterfaceParameterClient(scope,
                            new Id(xmlElement.attribute("id").getText()), xmlElement.selectSingleNode("type").getText());
                    break;
                case 3:
                    parameter = new InterfaceParameterRuntime(scope,
                            new Id(xmlElement.attribute("id").getText()), xmlElement.selectSingleNode("type").getText());
                    break;
                case 4:
                    parameter = new InterfaceParameterClient(scope,
                            new Id(xmlElement.attribute("id").getText()), xmlElement.selectSingleNode("type").getText());
                    break;
            }
        }
        else
            OneButton1Msg.showError(null, "error - create parameter failed",
                    "scope of parameter is not recognized", "OK", new Dimension(150, 75));

        return parameter;
    }

}
