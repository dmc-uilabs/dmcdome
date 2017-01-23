/*
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Oct 22, 2002
 * Time: 4:36:53 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationItem;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.network.client.functions.Vectors;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

import javax.swing.table.TableCellEditor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class ObjectiveParameter extends OptimizationParameter
{
    public static final String MAXIMIZE = "maximize";
    public static final String MINIMIZE = "minimize";

	private DomeEnumeration _isMaxOrMin = ObjectiveParameter.createMaxOrMinEnumerationMenu();

    public static EnumerationData createMaxOrMinEnumerationMenu()
    {
        EnumerationData domeEnum = new EnumerationData();
        domeEnum.addElement(ObjectiveParameter.MINIMIZE, "");
        domeEnum.addElement(ObjectiveParameter.MAXIMIZE, "");
        domeEnum.setLastSelection(0);
        return domeEnum;
    }

	public ObjectiveParameter(ModelObjectScope scope, Id id, String dataType)
	{
		super(scope, id, dataType);
	}

	public ObjectiveParameter(ModelObjectScope scope, Element xmlElement)
	{
		super(scope, xmlElement);
        setIsMaxOrMin(((Element) xmlElement.selectSingleNode("direction")).getText());
	}

	public ObjectiveParameter(ModelObjectScope scope, Id id, Parameter param)
	{
        super(scope, id, param);

        // by default a ObjectiveParameter will be minimized
		_isMaxOrMin.setLastSelection(0);
	}

    public ObjectiveParameter(ModelObjectScope scope, Id id, ObjectiveParameter objectiveParameter)
    {
        super(scope, id, objectiveParameter.getParameter());
        _isMaxOrMin.setLastSelection(objectiveParameter.getIsMaxOrMin().getLastSelection());
    }


    public DomeEnumeration getIsMaxOrMin()
    {
        return this._isMaxOrMin;
    }

    public String getIsMaxOrMinForEditor()
    {
        return this._isMaxOrMin.getElementName(this._isMaxOrMin.getLastSelection());
    }

    public void setIsMaxOrMin(int index)
    {
        this._isMaxOrMin.setLastSelection(index);
    }

	public void setIsMaxOrMin(String enumerationItem)
	{
		for(int i=0; i<this._isMaxOrMin.getSize(); i++)
		{
			if(this._isMaxOrMin.getElementName(i).equals(enumerationItem))
				this._isMaxOrMin.setLastSelection(i);
		}
	}

	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		xml.addElement("direction").addText(this.getIsMaxOrMinForEditor());
		xml.addElement("active").addText(this._isActive.toString());
		return xml;
	}

}
