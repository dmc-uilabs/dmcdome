package mit.cadlab.dome3.plugin.extendsim;

import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.util.units.Quantity;
import org.dom4j.Element;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: himosqui
 * Date: Aug 10, 2007
 * Time: 3:26:09 AM
 * To change this template use Options | File Templates.
 */
public class ExtendSimModelRuntime extends PluginModelRuntime {
    private File extendSimFile;
    List paramList; // List of all DOME wrapper parameters
    boolean isNativeModelLoaded = false;
    ExtendSimPluginCaller caller = null;
    public static boolean debug = false;
    public static String EXTENDSIM_APP_LOCK = "EXTENDSIM_APP_LOCK";

    public ExtendSimModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource) {
        super(parentId, xml, isProjectResource);
    }

    protected void executeNativePlugin(List affectedOutputParams) {
        synchronized (EXTENDSIM_APP_LOCK) {
            if (! isNativeModelLoaded) {
                initNativeApplication();
            }

            loadNativeModel();
            isNativeModelLoaded = true;

            if (debug) {
                Debug.trace(Debug.ALL, "starting execute(): affected output param=" + affectedOutputParams.toString());
            } else {
                Debug.trace(Debug.ALL, "starting running ExtendSim Plugin model");
            }

            /* copying input values from dome wrapper model to ExtendSim native model */
            for (int i = 0; i < paramList.size(); i++) {
                Parameter param = (Parameter) paramList.get(i);
                boolean isInput = this.getCausality(param).equals(CausalityStatus.INDEPENDENT);
                if (isInput) {
                    String varName = this.getMappedVariableName(param);
                    if (param.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
                        Quantity qtt = ((DomeReal) param.getCurrentDataObject()).getQuantity();
                        caller.setRealValue(varName, qtt.getMagnitude().doubleValue());
                        if (debug) {
                            System.out.println("setting... " + varName + " = " + qtt.getMagnitude());
                        }
                    } else if (param.getCurrentType().equals(DomeInteger.TYPE_INFO.getTypeName())) {
                        Quantity qtt = ((DomeInteger) param.getCurrentDataObject()).getQuantity();
                        caller.setIntegerValue(varName, qtt.getMagnitude().intValue());
                        if (debug) {
                            System.out.println("setting... " + varName + " = " + qtt.getMagnitude());
                        }
                    } else if (param.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName())) {
                        throw new RuntimeException("the input variable of the ExtendSim model cannot be matrix-type: variable " + varName + " is a matrix.");
                    }
                }
            }

            Debug.trace(Debug.ALL, "Inputs have been copied from dome wrapper model to native ExtendSim model. ExtendSim model is ready to run.");

            /* execute (=simulate in ExtendSim terminology) ExtendSim native model */
            caller.runModel();

            pause(1000);

            /* copying output values from ExtendSim native model to dome wrapper model */
            for (int i = 0; i < paramList.size(); i++) {
                Parameter param = (Parameter) paramList.get(i);
                if (isAffectedOutputParameter(param, affectedOutputParams)) {
                    boolean isInput = this.getCausality(param).equals(CausalityStatus.INDEPENDENT);
                    if (! isInput) {
                        String varName = this.getMappedVariableName(param);

                        if (param.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
                            double rval = caller.getRealValue(varName);
                            Vector realValue = Vectors.create(new Double(rval));
                            param.getCurrentDataObject().setValues(realValue);
                            if (debug) {
                                System.out.println("getting... " + varName + " = " + rval);
                            }
                        } else if (param.getCurrentType().equals(DomeInteger.TYPE_INFO.getTypeName())) {
                            int ival = caller.getIntegerValue(varName);
                            Vector intValue = Vectors.create(new Integer(ival));
                            param.getCurrentDataObject().setValues(intValue);
                            if (debug) {
                                System.out.println("getting... " + varName + " = " + ival);
                            }
                        } else if (param.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName())) {
                            double[][] rvals = caller.getRealMatrix(varName);
                            Vector matrixValue = createMatrix(rvals);
                            param.getCurrentDataObject().setValues(matrixValue);
                            if (debug) {
                                int colSize = 0;
                                if (rvals.length > 0) {
                                    colSize = rvals [0].length;
                                }
                                System.out.println("getting... = " + varName + " = a matrix of size [" + rvals.length + " x " + colSize + "]");
                            }
                        } else if (param.getCurrentType().equals(DomeString.TYPE_INFO.getTypeName())) {
                            String sval = caller.getStringValue(varName);
                            Vector stringValue = Vectors.create(sval);
                            param.getCurrentDataObject().setValues(stringValue);
                            if (debug) {
                                System.out.println("getting... = " + varName + " = " + sval);
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
    //
            Debug.trace(Debug.ALL, "ExtendSim finished solving. Results have been copied from native to java");
        }
    }

    /**
     * Halt the native model.
     */
    public void stopModel() {

    }
    private void pause(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void deleteModel() {
//        System.out.println("before entering the extend sim lock: " + ((extendSimFile == null) ? "" : extendSimFile.getName()));
        synchronized (EXTENDSIM_APP_LOCK) {
//            System.out.println("inside the extend sim lock: " + ((extendSimFile == null) ? "" : extendSimFile.getName()));
//            System.out.println("starting closeModel():" + getModelFilePath());

            if (caller != null) {
                String filePath = getModelFilePath();
                pause(500);
                caller.openModel(filePath);
                pause(1000);
                caller.closeModel(filePath);
                pause(500);
//                caller.finishUsingThisCaller();
            }

            if (solver.isSolving()) {
                solver.stopSolving();
                waitingToDie = Boolean.TRUE;
                return;
            }

            super.deleteModel();
        }
//        System.out.println("exiting the extend sim lock: " + extendSimFile.getName());
    }

    private String getModelFilePath() {
        String fileName = getMainModelFileName();
        if (fileName == null) {
            throw new UnsupportedOperationException("cannot start extend model - no filename");
        }

        /* extract fileName part from the full path */
        if (fileName.indexOf("\\") != -1) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        } else if (fileName.indexOf("/") != -1) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }

        /* special treatment for ExtendSim models, we need to modify working directory
         * by cutting "Run_XX" in the path because ExtendSim can open only one file for each file name */
        Pattern p = Pattern.compile("(.*)(?:\\\\|\\/)Run_\\d+");
        Matcher m = p.matcher(getWorkingDirectory().toString());
        m.find();
        String modifiedWorkingDir = m.group(1);



        String filePath = modifiedWorkingDir + File.separator + fileName;
        return filePath;
    }

    protected void loadNativeModel() {
        String filePath = getModelFilePath();
        extendSimFile = new File(filePath);
        if (! extendSimFile.isFile()) {
            throw new UnsupportedOperationException("fail to read the ExtendSim MOX file - the given file name is not pointing a file: " + filePath);
        }
        Debug.trace(Debug.ALL, "loading... ExtendSim model file: " + extendSimFile.getAbsolutePath());
        pause(500);
        caller.openModel(extendSimFile.getAbsolutePath());
        pause(1000);
        Debug.trace(Debug.ALL, "loaded... ExtendSim model file: " + extendSimFile.getAbsolutePath());
    }

    protected void initNativeApplication() {
        Debug.trace(Debug.ALL, "initializing... ExtendSim application");
        caller = new ExtendSimPluginCaller("ExtendSimPlugin");
        Debug.trace(Debug.ALL, "initialized... ExtendSim application");

        boolean runInForeground = ((DomeBoolean) this.getPluginConfiguration().getSetupParameter(ExtendSimConfiguration.RUN_IN_FOREGROUND).getCurrentDataObject()).getValue();
        if (runInForeground) {
            caller.executeCommand("ExtendMaximize();");
        } else {
            caller.executeCommand("ExtendMinimize();");
        }

        /* register all parameters of ExtendSimModelRuntime */
        paramList = new ArrayList();
        Iterator it = getModelObjects().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                Object map = getPluginMappingManager().getMappingObjectForParameter(p);
                if (map != null) {
                    paramList.add(p);
                }
            }
        }
    }

    /** returns a ExtendSim variable name mapped to a given parameter. */
    public String getMappedVariableName(Parameter p) {
        return (String) getPluginMappingManager().getMappingObjectForParameter(p);
    }

    /**
     * Use this method to determine if parameter is an affected output parameter
     * takes into account a null affected output parameter list
     * @param p parameter to check
     * @param affectedOutputParams list of affected output parameters; could be null
     * @return true if list is null or list contains p
     */
    protected boolean isAffectedOutputParameter(Parameter p, List affectedOutputParams) {
        return (p==null || affectedOutputParams==null || affectedOutputParams.contains(p));
    }

    private Vector createVector(double[] source) {
        Vector ret = new Vector();
        for (int i = 0; i < source.length; i++) {
            double value = source[i];
            ret.add(new Double(value));
        }
        return ret;
    }

    private Vector createMatrix(double[][] source) {
        Vector ret = new Vector();
        int rowCount = source.length;
        int columnCount = 0;
        if (rowCount > 0) {
            columnCount = source [0].length;
        }

        for (int i = 0; i < rowCount; i++) {
            Vector row = new Vector();
            for (int j = 0; j < columnCount; j++) {
                double value = source[i][j];
                row.add(new Double(value));
            }
            ret.add(row);
        }
        return ret;
    }

    public static void setDebug(boolean debug) {
        ExtendSimModelRuntime.debug = debug;
    }
}
