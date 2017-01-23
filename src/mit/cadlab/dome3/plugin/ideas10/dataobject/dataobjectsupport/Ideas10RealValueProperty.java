package mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport;

import com.sdrc.openideas.OI_Accuracy;
import com.sdrc.openideas.OI_Part;
import com.sdrc.openideas.OI_PartProperties;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import org.omg.CORBA.IntHolder;

import java.util.Vector;

public class Ideas10RealValueProperty  extends AbstractPluginData
{
    public static final int MASS = 1;
    public static final int VOLUME = 2;
    public static final int SOLID_SURFACE_AREA = 3;
    public static final int OPEN_SURFACE_AREA = 4;

    private String name;
    private OI_Part[] oiParts;
    private IntHolder intHolder = new IntHolder();
    private boolean isResult;
    private RealData value;
    private int type;

    /**
     * real value properties are mass, volume, solidSurfaceArea, openSurfaceArea
     * @param name
     * @param propertyType: constants defined in Ideas10RealValueProperty class
     */
    public Ideas10RealValueProperty(String name, Vector parts, int propertyType, Parameter p) {
	    this.parameter = p;
        oiParts = new OI_Part[parts.size()];
        for (int i = 0; i < parts.size(); i++) {
            oiParts[i] = (OI_Part) parts.get(i);

        }
        this.name = name;
	    if(p == null)
			this.value = new RealData();
	    else
            this.value = (RealData) p.getCurrentDataObject();
        this.type = propertyType;
        isResult = true;
    }

	public boolean getIsResult()
	{
		return isResult;
	}

    // get value from java object
    public String getName() {
        return name;
    }

    // get value from native object
    public double getValue(boolean isNativeCall) {
        double value = 0.0;
        for (int i = 0; i < oiParts.length; i++) {
            OI_PartProperties prop = oiParts[i].EvaluateProperties(OI_Accuracy.from_int(OI_Accuracy._OIE_HighAccuracy), 1, intHolder);
            switch (type) {
                case MASS:
                    value = value + prop.mass;
                    break;
                case VOLUME:
                    value = value + prop.volume;
                    break;
                case SOLID_SURFACE_AREA:
                    value = value + prop.solidSurfaceArea;
                    break;
                case OPEN_SURFACE_AREA:
                    value = value + prop.openSurfaceArea;
                    break;
                default:
                    throw new RuntimeException("Ideas10RealValueProperty.getValue: invalid type of property");
            }
        }
        return value;
    }

    public void setValue(double val) {
        value.setValue(val);
    }

    public void loadJavaData() {
        setValue(getValue(true));
    }

    public void resetObjectPointer() {
    }

}