package mit.cadlab.dome3.plugin.vensim;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 28.
 */
import java.io.*;

import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.vensim.VensimConfiguration;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.util.units.Quantity;

import java.util.*;

import com.vensim.Vensim;

/**
 * User: Sangmok Han
 * Date: 2005. 9. 6.
 */
public class VensimPlugin extends AbstractPlugin {
    private File vensimFile;
    List paramList; // List of all DOME wrapper parameters
    VensimModelRuntime modelRuntime;
    private boolean isLoaded = false;
    Vensim vensim = null;
    public static boolean debug = false;

    public VensimPlugin(String file, VensimModelRuntime modelRuntime) {
        vensim = new Vensim("vendll32") ; /* vendml32 for the minimal dll */
        this.vensimFile = new File(file);
        if (! vensimFile.isFile()) {
            throw new UnsupportedOperationException("fail to read a vensim VPM file - the given file name is not pointing a file: " + file);
        }
        Debug.trace(Debug.ALL, "running vensim VPM file: " + vensimFile.getAbsolutePath());
        System.out.println("running vensim VPM file: " + vensimFile.getAbsolutePath());
        this.modelRuntime = modelRuntime;
        paramList = new ArrayList();
    }

    public static void setDebug(boolean debug) {
        VensimPlugin.debug = debug;
    }

    public void createModel() {
        Debug.trace(Debug.ALL, "vensim model has been created");
    }

    public void deleteModel() {
        Debug.trace(Debug.ALL, "vensim model has been deleted");
    }

    public void loadModel() {
        Debug.trace(Debug.ALL, "start loading vensim model");

        int result = vensim.command("SPECIAL>LOADMODEL|" + vensimFile.getAbsolutePath());
        if (result == 0) {
            System.out.println("SPECIAL>LOADMODEL|" + vensimFile.getAbsolutePath() + " -- failed" );
            isLoaded = false;
        } else if (result == 1) {
            System.out.println("Vensim loaded model" );
            isLoaded = true;
        }

        boolean runInForeground = ((DomeBoolean) modelRuntime.getPluginConfiguration().getSetupParameter(VensimConfiguration.RUN_IN_FOREGROUND).getCurrentDataObject()).getValue();
        if (runInForeground) {
//        vensim.be_quiet(Vensim.QUIET_NO_WIP);
            vensim.be_quiet(Vensim.QUIET_NO_WIP_NO_DIALOGS);
        } else {
            vensim.be_quiet(Vensim.QUIET_NORMAL);
        }
    }

    public synchronized void execute(List affectedOutputParams) {
        if (debug) {
            Debug.trace(Debug.ALL, "starting execute(): affected output param=" + affectedOutputParams.toString());
        } else {
            Debug.trace(Debug.ALL, "starting running Vensim Plugin model");
        }

        /* setting up where to store simulation result */
        int result = vensim.command("SIMULATE>RUNNAME|base");
        if (result == 0) {
            System.out.println("SIMULATE>RUNNAME|base -- failed" );
        }

        /* copying input values from dome wrapper model to vensim native model */
        for (int i = 0; i < paramList.size(); i++) {
            Parameter param = (Parameter) paramList.get(i);
            boolean isInput = modelRuntime.getCausality(param).equals(CausalityStatus.INDEPENDENT);
            if (isInput) {
                String varName = modelRuntime.getMappedVariableName(param);
                if (param.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
                    Quantity qtt = ((DomeReal) param.getCurrentDataObject()).getQuantity();
                    String vensimCommand = "SIMULATE>SETVAL|" + varName + " = " + qtt.getMagnitude();
                    if (debug) {
                        System.out.println("vensimCommand = " + vensimCommand );
                    }
                    result = vensim.command(vensimCommand);
                    if (result == 0) {
                        System.out.println(vensimCommand + " -- failed" );
                    }
                } else {
                    throw new RuntimeException("the input variable of the vensim model cannot be vector-type: variable " + varName + " is a vector.");
                }
            }
        }

        Debug.trace(Debug.ALL, "Inputs have been copied from dome wrapper model to native vensim model. Vensim model is ready to run.");

        /* execute (=simulate in vensim terminology) vensim native model */
        result = vensim.command("MENU>RUN|O" );
        if (result == 0) {
            System.out.println("MENU>RUN|O -- failed" );
        }

        /* check vensim model status */
        result = vensim.check_status();
        if (result != 0) {
            System.out.println("Check Status -- failed" );
        }

        /* copying output values from vensim native model to dome wrapper model */
        for (int i = 0; i < paramList.size(); i++) {
            Parameter param = (Parameter) paramList.get(i);
            if (isAffectedOutputParameter(param, affectedOutputParams)) {
                boolean isInput = modelRuntime.getCausality(param).equals(CausalityStatus.INDEPENDENT);
                if (! isInput) {
                    String varName = modelRuntime.getMappedVariableName(param);

                    float[] callbackValue = new float[1];
                    result = vensim.get_val("INITIAL TIME", callbackValue);
                    int initialTime = (int) callbackValue [0];
                    result = vensim.get_val("FINAL TIME", callbackValue);
                    int finalTime = (int) callbackValue [0];
                    result = vensim.get_val("SAVEPER", callbackValue);
                    float savePer = callbackValue [0];

                    int numberOfTimeSteps = (int) Math.ceil((double) (finalTime - initialTime) / (double) savePer) + 1;

                    float[] rval = new float[numberOfTimeSteps];
                    float[] tval = new float[numberOfTimeSteps];

                    int actualNumberOfTimeSteps = vensim.get_data("base.vdf", varName, "time", rval, tval, numberOfTimeSteps);

                    if (param.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
                        Vector realValue = Vectors.create(createDouble(rval));
                        param.getCurrentDataObject().setValues(realValue);
                        if (debug) {
                            System.out.println("setting real value = " + realValue + " : the last element of an array of size " + actualNumberOfTimeSteps);
                        }
                    } else if (param.getCurrentType().equals(DomeVector.TYPE_INFO.getTypeName())) {
                        Vector vecValue = createVector(rval);
                        param.getCurrentDataObject().setValues(vecValue);
                        if (debug) {
                            System.out.println("setting vector value = " + vecValue + " : a vector of size " + actualNumberOfTimeSteps);
                        }
                    }

//                    /* print out the result */
//                    StringBuffer sb = new StringBuffer();
//                    for (int j = 0; j < actualNumberOfTimeSteps; j++) {
//                        sb.append(Float.toString(tval[j]));
//                        sb.append("\t");
//                    }
//                    sb.append("\n");
//                    for (int j = 0; i < actualNumberOfTimeSteps; j++) {
//                        sb.append(Float.toString(rval[i]));
//                        sb.append("\t");
//                    }
//                    System.out.println("var name = " + varName + ", numberOfTimeSteps = " + numberOfTimeSteps + ", result
                }
            }
        }

        Debug.trace(Debug.ALL, "Vensim finished solving. Results have been copied from native to java");
    }

    private Double createDouble(float[] source) {
        return new Double(source[source.length - 1]);
    }

    private Vector createVector(float[] source) {
        Vector ret = new Vector();
        for (int i = 0; i < source.length; i++) {
            float value = source[i];
            ret.add(new Double(value));
        }
        return ret;
    }

    public void executeBeforeInput() {
        /* nothing needs to be done */
    }

    public void executeAfterOutput() {
        /* nothing needs to be done */
    }

    public void unloadModel() {
        /* nothing needs to be done */
    }

    public boolean isModelLoaded() {
        return isLoaded;
    }

    public void addParameter(Parameter param) {
        paramList.add(param);
    }
}
