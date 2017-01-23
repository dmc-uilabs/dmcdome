package mit.cadlab.dome3.plugin.ideas10.dataobject;

import com.ford.kbedoor.sealanalysis.sealgap.SealGapAnalyzer;
import com.sdrc.openideas.OI_Part;
import com.sdrc.openideas.OI_Server;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.ideas10.Ideas10Configuration;
import mit.cadlab.dome3.plugin.ideas10.Ideas10Plugin;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10VrmlFile;
import org.omg.CORBA.ORB;
import org.omg.CORBA.IntHolder;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Ideas10InterferenceAnalysis extends AbstractPluginData
{
    private OI_Server oiServer;
    private ORB orb;
    private Parameter list;
    private SealGapAnalyzer sga;
    private String door;
    private String body;
    private String seal;
    private double x_off;
    private double y_off;
    private double z_off;
    private double xsec;
    private DomeMatrixData result;
    private Ideas10Plugin plg;
    private boolean ranOnce = false;
    private Vector data;

	public Ideas10InterferenceAnalysis(Parameter list, OI_Server oiSvr, ORB orb, Ideas10Plugin plg)
	{
        this.plg = plg;
        this.list = list;
        this.oiServer = oiSvr;
        this.orb = orb;
        this.data = new Vector();
        sga = new SealGapAnalyzer(oiSvr, orb);
        List data = list.getCurrentDataObject().getValues();
        for (int i = 0; i < data.size(); i++) {
            Object o = data.get(i);
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                if (p.getName().equals(Ideas10Configuration.DOOR_NAME))
                    door = ((StringData) p.getCurrentDataObject()).getValue();
                else if (p.getName().equals(Ideas10Configuration.BODY_NAME))
                    body = ((StringData) p.getCurrentDataObject()).getValue();
                else if (p.getName().equals(Ideas10Configuration.BULB_NAME))
                    seal = ((StringData) p.getCurrentDataObject()).getValue();
                else if (p.getName().equals(Ideas10Configuration.INTF_DATA))
                    result = ((DomeMatrixData) p.getCurrentDataObject());
            }
        }
	}

    public void getData() {
        List data = list.getCurrentDataObject().getValues();
        for (int i = 0; i < data.size(); i++) {
            Object o = data.get(i);
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                if (p.getName().equals(Ideas10Configuration.X_OFF))
                    x_off = ((RealData) p.getCurrentDataObject()).getValue();
                else if (p.getName().equals(Ideas10Configuration.Y_OFF))
                    y_off = ((RealData) p.getCurrentDataObject()).getValue();
                else if (p.getName().equals(Ideas10Configuration.Z_OFF))
                    z_off = ((RealData) p.getCurrentDataObject()).getValue();
                else if (p.getName().equals(Ideas10Configuration.XSEC_DIST))
                    xsec = ((RealData) p.getCurrentDataObject()).getValue();
            }
        }
    }

    public Ideas10VrmlFile createVrmlFile(Parameter p, Ideas10Plugin plg, DomeFile f) {
        Ideas10VrmlFile _vrmlFile = new Ideas10VrmlFile(plg, p, f);
        data.addElement(_vrmlFile);
        return _vrmlFile;
    }

    public void analyze()
	{
        getData();
        if (ranOnce) {
            //plg.loadModel(); // have to reload the model
            sga = new SealGapAnalyzer(oiServer, orb);
        }
        sga.setBodyMounted(false);
        sga.setABSurfacePartName(door);
        sga.setJSurfacePartName(body);
        sga.setBulbPartName(seal);
        sga.setSectionDistance(xsec);
        sga.initializePlanes();
        String cutBulbPartName = sga.getBulbName();

        sga.setInterferenceAnalysis(true);
        sga.setCombinedAnalysis(false);
        sga.setDeflectedDoor(false);
        sga.setOffsetData(sga.makeOffsetData(x_off, y_off, z_off));
        sga.analyzeSealGap();
        Vector areas = sga.getAreaVectors();
        result.setValues(Collections.singletonList(areas));

        System.out.println("interference analysis result:" +result);
        ranOnce = true;

        OI_Part cutBulb = plg.getPart(cutBulbPartName);
        for (int i = 0; i < data.size(); i++) {
            Object obj = data.get(i);
            if (obj instanceof Ideas10VrmlFile) {
                if (((Ideas10VrmlFile) obj).getIsResult())
                    ((Ideas10VrmlFile) obj).loadData(cutBulb.GetName(new IntHolder()));
            }
        }
	}

    public void disconnect() {
        sga.disconnect();
        sga = null;
    }

    public void resetObjectPointer() {
    }
}





