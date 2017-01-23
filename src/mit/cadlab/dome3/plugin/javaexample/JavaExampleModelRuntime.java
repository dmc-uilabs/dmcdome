package mit.cadlab.dome3.plugin.javaexample;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;

public class JavaExampleModelRuntime extends PluginModelRuntime
{
    CustomDivisionCode customCode;
    private Parameter numeratorParam, denominatorParam, resultParam;

	public JavaExampleModelRuntime(CompoundId id, Element xml, boolean isProjectResource)
	{
		super(id, xml, isProjectResource);
		loadCustomModel();
	}

    protected void loadCustomModel() {
        customCode = new CustomDivisionCode();

        // create map of dome object to corresponding custom code's object
        Iterator it = getBuildContext().getModelObjectReferences().listIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                Object map = getPluginMappingManager().getMappingObjectForParameter(p);
                if (map != null) {
                    createLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
                }
            }
        }
    }

    protected void createLink(Parameter p, String map, boolean isInput) {
        if (isInput) {
            if (map.equalsIgnoreCase(JavaExampleConfiguration.NUMERATOR))
                numeratorParam = p;
            else if (map.equalsIgnoreCase(JavaExampleConfiguration.DENOMINATOR))
                denominatorParam = p;
            else
                throw new RuntimeException("Input parameter in this custom model can only be either 'numerator' or 'denominator'");
        } else {
            if (map.equalsIgnoreCase(JavaExampleConfiguration.RESULT))
                resultParam = p;
            else
                throw new RuntimeException("Out parameter in this custom model can only be 'result'");
        }
    }

	protected void executeNativePlugin(List affectedOutputParams)
	{
        if (writeInputParameters()) {
            if (runModel()) {
                readOutputParameters(affectedOutputParams);
            }
        }
	}

    protected boolean writeInputParameters() {
        customCode.setNumerator(((DomeReal) numeratorParam.getCurrentDataObject()).getRealValue().doubleValue());
        customCode.setDenominator(((DomeReal) denominatorParam.getCurrentDataObject()).getRealValue().doubleValue());
        return true;
    }

    protected boolean runModel() {
        customCode.doDivision();
        return true;
    }

    protected void readOutputParameters(List affectedOutputParams) {
        ((DomeReal) resultParam.getCurrentDataObject()).setRealValue(new Double(customCode.getResult()));
        // in this plugin the list affectedOutputParams is not used, since since it's already known beforehand what the (only) output is
    }

	public void stopModel()
	{
		//
	}

	public void deleteModel()
	{
		super.deleteModel();
	}

}