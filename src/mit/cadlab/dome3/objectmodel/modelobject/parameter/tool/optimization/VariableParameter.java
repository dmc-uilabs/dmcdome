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
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.OptimizationParameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.util.Regex;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

import javax.swing.table.TableCellEditor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import edu.iupui.rg.ucum.units.Unit;


public class VariableParameter extends OptimizationParameter
{
    public static final String UPPER_LIMIT_VALUE_CHANGED = "upper limit value";
    public static final String UPPER_LIMIT_UNIT_CHANGED = "upper limit unit";
    public static final String LOWER_LIMIT_VALUE_CHANGED = "lower limit value";
    public static final String LOWER_LIMIT_UNIT_CHANGED = "lower limit unit";

    private DomeReal _lowerLimit, _upperLimit;


    public VariableParameter(ModelObjectScope scope, Id id, String dataType)
    {
        super(scope, id, dataType);
	    this._lowerLimit = new RealData();
        this._upperLimit = new RealData();

        createListeners();
    }

	public VariableParameter(ModelObjectScope scope, Element xmlElement)
	{
		super(scope, xmlElement);
        Element constraints = (Element) xmlElement.selectSingleNode("constraints");
        List lowerSplit = Regex.split(" ", constraints.attribute("lower").getText());
        List upperSplit = Regex.split(" ", constraints.attribute("upper").getText());
        if (lowerSplit.size() == 1 && lowerSplit.size() == upperSplit.size())
        {
            this._lowerLimit = new RealData(new Double((String) lowerSplit.get(0)).doubleValue()); // no unit
            this._upperLimit = new RealData(new Double((String) upperSplit.get(0)).doubleValue()); // no unit
        }
        else if (lowerSplit.size() == 2 && upperSplit.size() == 2)
        {
            _lowerLimit = new RealData(new Double((String) lowerSplit.get(0)).doubleValue(), (String)lowerSplit.get(1));
            _upperLimit = new RealData(new Double((String) upperSplit.get(0)).doubleValue(), (String) upperSplit.get(1));
        }
        createListeners();
	}

	public VariableParameter(ModelObjectScope scope, Id id, Parameter param)
	{
		super(scope, id, param);

        /**
         *  when a parameter is added and mapped its default lower
         *  and upper limits are 0.0 and 2.0*value of parameter
         *  also the units are set to the parameter units that come
         *  from the mapped parameter
         */

        _lowerLimit = new RealData(0.0, _parameter.getCurrentDataObject().getUnit());
		_upperLimit = new RealData(((DomeReal)_parameter.getCurrentDataObject()).getRealValue().doubleValue()*2.0, _parameter.getCurrentDataObject().getUnit());

        createListeners();
	}

    public VariableParameter(ModelObjectScope scope, Id id, VariableParameter variableParameter)
    {
        super(scope, id, variableParameter.getParameter());
        _lowerLimit = new RealData(variableParameter.getLowerLimit().getValue(), _parameter.getCurrentDataObject().getUnit());
        _upperLimit = new RealData(variableParameter.getUpperLimit().getValue(), _parameter.getCurrentDataObject().getUnit());

        createListeners();
    }

    protected void createListeners()
    {
        _lowerLimit.addPropertyChangeListener(new RealDataListener(LOWER_LIMIT_VALUE_CHANGED, LOWER_LIMIT_UNIT_CHANGED));
        _upperLimit.addPropertyChangeListener(new RealDataListener(UPPER_LIMIT_VALUE_CHANGED, UPPER_LIMIT_UNIT_CHANGED));
        _parameter.getCurrentDataObject().addPropertyChangeListener(DomeReal.UNIT, new UnitListener());
    }

	public void setLowerLimit(Double value)
    {
        this._lowerLimit.setRealValue(value);
    }

    public DomeReal getLowerLimit()
    {
        return this._lowerLimit;
    }

    public void setUpperLimit(Double value)
    {
        this._upperLimit.setRealValue(value);
    }

    public DomeReal getUpperLimit()
    {
        return this._upperLimit;
    }

    public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		Element bounds = xml.addElement("constraints");
		bounds.addAttribute("lower", this._lowerLimit.toString());
		bounds.addAttribute("upper", this._upperLimit.toString());
		xml.addElement("active").addText(this._isActive.toString());
		return xml;
	}

    protected void synchronizeLimitUnits(Unit newUnit)
    {
        _lowerLimit.setUnit(newUnit);
        _upperLimit.setUnit(newUnit);
    }

    public boolean isValidUpperLimit(Double value)
    {
        if (value.doubleValue() < ((DomeReal)_parameter.getCurrentDataObject()).getValue())
            return false;
        else
            return true;
    }

    public boolean isValidLowerLimit(Double value)
    {
        if (value.doubleValue() > ((DomeReal)_parameter.getCurrentDataObject()).getValue())
            return false;
        else
            return true;
    }

    class RealDataListener implements PropertyChangeListener
    {
        private String _unitMsg, _valueMsg;

        public RealDataListener(String valueMsg, String unitMsg)
        {
            _unitMsg = unitMsg;
            _valueMsg = valueMsg;
        }
        public void propertyChange(PropertyChangeEvent e)
        {
            if(e.getPropertyName().equals(DomeReal.VALUE))
            {
               _parameter.firePropertyChange(_valueMsg, e.getOldValue(), e.getNewValue());
            }
            else if(e.getPropertyName().equals(DomeReal.UNIT))
            {
                _parameter.firePropertyChange(_unitMsg, e.getOldValue(), e.getNewValue());
            }
        }
    }

    class UnitListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            synchronizeLimitUnits((Unit)e.getNewValue());
        }
    }
}
