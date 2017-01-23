package mit.cadlab.dome3.plugin.extendsim;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stateless function caller
 *
 * User: Sangmok Han
 * Date: Aug 10, 2007
 * Time: 3:26:40 AM
 */
public class ExtendSimPluginCaller {

    private static boolean isReadyForCmd = false;

    public ExtendSimPluginCaller(String libName) {
        try {
			System.loadLibrary(libName);
		}
		catch (UnsatisfiedLinkError e) {
			throw new UnsatisfiedLinkError (
			        "A link error occured when trying to load " + libName + ".DLL.\n\n"
			        + "Check that the directory containing the DLLs is in your path.");
		}

        this._initializeOLE();

        /* start up ExtendSim */
        synchronized (ExtendSimModelRuntime.EXTENDSIM_APP_LOCK) {
            if (! _isExtendSimRunning()) {
                try {
                    //extendSimProcess = Runtime.getRuntime().exec(ExtendSimPluginCaller.getExetendExeFilePath());
                    _launchExtendSim();
                    //Timer timer = new Timer(false);
//                    timer.schedule(new ExtendSimCleanupTimerTask(), 0, 1000 * 60 / 60);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    public static class ExtendSimCleanupTimerTask extends TimerTask {
//        public void run() {
//            long timeLeft = 40 * 60 * 1000 / 60 - (System.currentTimeMillis() - lastUsageOfExtendSim);
//            if (timeLeft <= 0) {
//                System.out.println("destroy it");
////                extendSimProcess.destroy();
//                this.cancel();
//            } else {
//                System.out.println("time left : " + timeLeft);
//            }
//        }
//    }

    public void executeCommand(String cmd) {
        _execute(cmd);
    }

    public void openModel(String filePath) {
        String cmd = "OpenExtendFile(\"" + filePath + "\");";
        _execute(cmd);
    }

    public void runModel() {
        String cmd = "ExecuteMenuCommand(6000);";
        _execute(cmd);
    }

    public void setRealValue(String item, double value) {
        item = fixVariableReference(item);
        _poke("system", item, Double.toString(value));
    }

    public void setIntegerValue(String item, int value) {
        item = fixVariableReference(item);
        _poke("system", item, Integer.toString(value));
    }

    public double getRealValue(String item) {
        item = fixVariableReference(item);
        String valStr = _request("system", item);

        try {
            double doubleVal = Double.parseDouble(valStr);
            return doubleVal;
        } catch (NumberFormatException e) {
            System.out.println("Error! item does not exist. return value is arbitrarily set as zero : item ref = " + item);
            return 0;
        }
    }

    public int getIntegerValue(String item) {
        item = fixVariableReference(item);

        String valStr = _request("system", item);

        try {
            double doubleVal = Double.parseDouble(valStr);
            int intVal = (int) Math.round(doubleVal);
            return intVal;
        } catch (NumberFormatException e) {
            System.out.println("Error! item does not exist. return value is arbitrarily set as zero : item ref = " + item);
            return 0;
        }
    }

    /**
     * item reference such as "VarName:#5" (which indicates VarName at block #5)
     * or "VarName:#73:0:1:8:3" (which points to a matrix with row range 0~8 and column range 1~3 of VarName at block #5)
     */
    public double[][] getRealMatrix(String item) {
        item = fixVariableReference(item);

        Pattern p = Pattern.compile("[\\w]+\\:#[\\d]+(?:\\:([\\d]+))?(?:\\:([\\d]+))?(?:\\:([\\d]+))?(?:\\:([\\d]+))?$");
        Matcher m = p.matcher(item);
        if (! m.find()) {
            return new double[0][0];
        }

        int rowStart = 0;
        int colStart = 0;
        int rowEnd = 0;
        int colEnd = 0;

        if (m.group(1) != null) {
            try {
                rowStart = Integer.parseInt(m.group(1));
            } catch (NumberFormatException e) { rowStart = 0; }
        }
        if (m.group(2) != null) {
            try {
                colStart = Integer.parseInt(m.group(2));
            } catch (NumberFormatException e) { colStart = 0; }
        }
        if (m.group(3) != null) {
            try {
                rowEnd = Integer.parseInt(m.group(3));
            } catch (NumberFormatException e) { rowEnd = 0; }
        }
        if (m.group(4) != null) {
            try {
                colEnd = Integer.parseInt(m.group(4));
            } catch (NumberFormatException e) { colEnd = 0; }
        }

        int rowSize = rowEnd - rowStart + 1;
        int colSize = colEnd - colStart + 1;

        double[][] ret = new double[rowSize][colSize];
        String valStr = _request("system", item);
//        String valStr = "1\t0\r\n3\t0\r\n1\t0.3333333333333\r\n1\t0.6666666666666";
//        String valStr = "1\t0";

        Pattern matrixPattern = Pattern.compile("([\\d\\.]+\\t)|([\\d\\.]+(?:\\n|$))");
        m = matrixPattern.matcher(valStr);

//        System.out.println("matches : " + m.matches());

        int rowIdx = 0;
        int colIdx = 0;

        while (m.find()) {
            if (m.group(1) != null) {
                if (colIdx >= colSize) {
                    throw new IndexOutOfBoundsException("the result value has a larger colume size: colume size = " + colSize + ", value = " + valStr);
                } else if (rowIdx >= rowSize) {
                    throw new IndexOutOfBoundsException("the result value has a larger row size: row size = " + rowSize + ", value = " + valStr);
                } else {
                    try {
                        ret [rowIdx][colIdx++] = Double.parseDouble(m.group(1).trim());
                    } catch (NumberFormatException e) { System.out.println("number format exception when parsing : " + m.group(1));  }
                }
            } else if (m.group(2) != null) {
                if (colIdx >= colSize) {
                    throw new IndexOutOfBoundsException("the result value has a larger colume size: colume size = " + colSize + ", value = " + valStr);
                } else if (rowIdx >= rowSize) {
                    throw new IndexOutOfBoundsException("the result value has a larger row size: row size = " + rowSize + ", value = " + valStr);
                } else {
                    try {
                        ret [rowIdx++][colIdx++] = Double.parseDouble(m.group(2).trim());
                        if (colIdx >= colSize) {
                            colIdx = 0;
                        }
                    } catch (NumberFormatException e) { System.out.println("number format exception when parsing : " + m.group(2));  }
                }
            }
        }

        for (int i =0; i < rowSize; i++) {
            for (int j =0; j < colSize; j++) {
                System.out.print(ret [i][j] + "   ");
            }
            System.out.println("");
        }

        return ret;
    }

    public String getStringValue(String item) {
        item = fixVariableReference(item);
        String valStr = _request("system", item);
        return valStr;
    }

    public double getCurrentTime() {
        executeCommand("GlobalStr0 = CurrentTime;");
        String valStr = _request("system", "GlobalStr0+:#0:0:0");
        return Double.parseDouble(valStr);
    }

    /** modify "Value:#33" into "Value:#33:0:0:0:0" by appending ":0:0:0:0"
      * if a given item is a form of "variable name:#block number" */
    private String fixVariableReference(String originItemRef) {
        String ret = originItemRef;
        Pattern p = Pattern.compile("[\\w]+\\:#[\\d]+$");
        Matcher m = p.matcher(originItemRef);
        if (m.matches()) {
            ret = ret + ":0:0:0:0";
        }
        return ret;
    }

    public void sendBlockMessage(int blockNum, String value) {
        _blockmsg(blockNum, value);
    }

    public void closeModel(String filePath) {
        String cmd = "SetDirty(0);";
        _execute(cmd);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cmd = "ExecuteMenuCommand(4);";
        _execute(cmd);
    }

    public void setDebug(boolean debug) {
        if (debug) {
            _setDebug(1);
        } else {
            _setDebug(0);
        }
    }

    public void finishUsingThisCaller() {
        _cleanupOLE();
    }

    private native boolean _isExtendSimRunning(); // check if ExtendSim is running
    private native void _launchExtendSim(); // launch ExtendSim
    private native void _execute(String cmd); // open a model, if necessary, and set focus on the model
    private native void _poke(String topic, String item, String value); // (ex) topic = system, item = MeanDist:#0:0:0, value = 35.5
    private native String _request(String topic, String item); // (ex) item  = Meanval:#1:0:0
    private native void _blockmsg(int blockNum, String value); // (ex) blockNum = 1, value = "some text"
    private native void _setDebug(int debug); // in order to print out debug messages, set this true

    private native void _initializeOLE();
    private native void _cleanupOLE();
    //public native double[] requestDoubleArray(String topic, String item); // (ex) item  = Meanval:#1:0:0

    public native void getTest(String value);

}
