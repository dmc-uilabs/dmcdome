package mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport;

import com.sdrc.openideas.OI_Material;
import com.sdrc.openideas.OI_Part;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import org.omg.CORBA.IntHolder;

public class Ideas10MaterialProperty  extends AbstractPluginData
{
    private StringData name;
    private OI_Material oiMat;
    private OI_Part oiPart;
    private IntHolder intHolder = new IntHolder();
    private boolean isResult;

    public Ideas10MaterialProperty(String name, OI_Part part , Parameter p) {
	    this.parameter = p;
        this.oiPart = part;
        this.name = new StringData(name);
        isResult = true;
    }

	public boolean getIsResult()
	{
		return isResult;
	}

    // get value from java object
    public String getName() {
        return name.getValue();
    }

    // get value from native object
    public String getName(boolean isNativeCall) {
        return oiMat.GetName(intHolder);
    }

    //todo
/*    public double getProperty(boolean isNativeCall) {
        return oiMat.GetProperties();
    }*/

    public void setName(String value) {
        name.setValue(value);
    }

    public void loadJavaData() {
        this.oiMat = oiPart.GetMaterial(intHolder);
        if (oiMat != null) {
            setName(getName(true));
        }
        else {
            System.out.println("Part has no material assigned.");
            setName("No material");
        }
    }

    public void resetObjectPointer() {
    }
}