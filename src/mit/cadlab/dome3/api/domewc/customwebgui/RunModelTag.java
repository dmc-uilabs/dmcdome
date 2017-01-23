package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.api.*;
import mit.cadlab.dome3.api.domewc.DomeSession;

import javax.servlet.jsp.JspException;
import java.util.List;
import java.util.Vector;
import java.lang.reflect.Constructor;
import java.io.PrintWriter;

public class RunModelTag extends DomeWebTagSupport {
    private String user, password, server, space, path, parammap, downloadfolder;
    private boolean debugListener = false;
    private ParameterStatusChangeListener statusListener;
    private ParameterValueChangeListener valueListener;

    public int doStartTag() throws JspException
    {
        try {
            logNewAction();
            ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap); // get param map from session
            DomeConnection domeCon = getDomeConnection(server, user, password);
            RuntimeInterface runIface = getRuntimeIface(domeCon);

            log("sets values of independent parameters");
            List inputs = runIface.getIndependentParameters();
            for (int i = 0; i < inputs.size(); i++) {
                RuntimeParameter p = (RuntimeParameter) inputs.get(i);
                String paramName = p.getParamName();
                if (map.containsKey(paramName)) {
                    // ask for parameter type from user-defined map b/c some models (e.g. matlab) cannot distinguish vectors from matrices
                    if (isVector(map.getParamType(paramName))) {
                        log("setting Vector " + paramName);
                        Vector val = map.getVectorMatrixValue(paramName);
                        p.setVectorValue(val);
                        log("to " + paramName + " to " + val + " in the interface");
                    } else if (isMatrix(map.getParamType(paramName))) {
                        log("setting Matrix " + paramName);
                        Vector val = map.getVectorMatrixValue(paramName);
                        p.setMatrixValue(val);
                        log("to " + paramName + " to " + val + " in the interface");
                    } else {
                        log("setting " + paramName);
                        Object val = map.getRawValue(paramName);
                        p.setValueAuto(val);
                        log("to " + paramName + " to " + val + " in the interface");
                    }
                } else
                    log("WARNING:  value of independent parameter '" + paramName + "' is not set");
            }

            if (downloadfolder != null) {
                log("set download folder at " + downloadfolder);
                runIface.setDownloadFolder(downloadfolder);
            }

            log("runs model");
            if (statusListener != null && valueListener != null) {
                log("adding listeners ...");
                runIface.addParameterStatusChangeListener(statusListener);
                log("    status listener added");
                runIface.addParameterValueChangeListener(valueListener);
                log("    value listener added");
            }
            runIface.submit();

            log("gets values of output parameters");
            List outputs = runIface.getResultParameters();
            outputs.addAll(runIface.getIntermediateParameters());
            for (int i = 0; i < outputs.size(); i++) {
                RuntimeParameter p = (RuntimeParameter) outputs.get(i);
                String paramName = p.getParamName();
                List val = p.getRawValue();
                if (p.isVector())
                    map.setVectorMatrixValue(paramName, (Vector) val, VECTOR);
                else if (p.isMatrix())
                    map.setVectorMatrixValue(paramName, (Vector) val, MATRIX);
                else
                    map.setStringValue(paramName, p.getRawValue().get(0).toString());
                log("set " + paramName + " to " + val + " in the page map");
            }
            setSesAttribute(map.getSesName(), map);

            runIface.close();
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDownloadfolder(String downloadfolder) {
        this.downloadfolder = downloadfolder;
    }

    public void setDebug(String debugListener) {
        if ("true".equalsIgnoreCase(debugListener)) {
            this.debugListener = true;
        } else {
            this.debugListener = false;
        }
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }

    public void setStatusListener(String statusListenerClassName) {
        try {
            Constructor listenerConst = Class.forName(statusListenerClassName).getConstructor(new Class[] { PrintWriter.class, Boolean.TYPE });
            this.statusListener = (ParameterStatusChangeListener) listenerConst.newInstance(new Object[] { pageContext.getResponse().getWriter(), new Boolean(debugListener) });
            System.out.println("current status change listener: " + this.statusListener.getClass().getName());
        } catch (ClassNotFoundException e) {
            System.err.println("Class named '" + statusListener + "' not found. status listener is set to null.");
        } catch (NoSuchMethodException e) {
            System.err.println("Contructor named '" + statusListener + "(String className)' not found. status listener is set to null.");
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
    }

    public void setValueListener(String valueListenerClassName) {
        try {
            Constructor listenerConst = Class.forName(valueListenerClassName).getConstructor(new Class[] { PrintWriter.class, Boolean.TYPE });
            this.valueListener = (ParameterValueChangeListener) listenerConst.newInstance(new Object[] { pageContext.getResponse().getWriter(), new Boolean(debugListener) });
            System.out.println("current value change listener: " + this.valueListener.getClass().getName());
        } catch (ClassNotFoundException e) {
            System.err.println("Class named '" + valueListener + "' not found. value listener is set to null.");
        } catch (NoSuchMethodException e) {
            System.err.println("Contructor named '" + valueListener + "(String className)' not found. value listener is set to null.");
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
    }

    private void logNewAction() {
        log("### starts a new action ###");
        log("server: " + server);
        log("user: " + user);
    }

    private DomeConnection getDomeConnection(String svr, String usr, String pwd) {
        if (getSesAttribute(DOME_SESSION_NAME) != null) {
            log("DOME session already established");
            DomeSession domeSes = (DomeSession) getSesAttribute(DOME_SESSION_NAME);
            if (svr.equalsIgnoreCase(domeSes.getServer())
                    && usr.equalsIgnoreCase(domeSes.getUserId())
                    && domeSes.isConnected()) {
                log("uses previous login info");
                return domeSes.getDomeConnection();
            } else {
                log("uses new login info");
                domeSes.close();	// disconnect the current connection
                DomeConnection conn = new DomeConnection(usr, pwd, svr); // and make a new connection
                domeSes.setDomeConnection(conn);
                domeSes.setUserId(usr);
                domeSes.setServer(svr);
                return conn;
            }
        } else {
            log("no session established. create a new session and login.");
            DomeSession domeSes = new DomeSession();
            DomeConnection conn = new DomeConnection(usr, pwd, svr);
            domeSes.setDomeConnection(conn);
            domeSes.setUserId(usr);
            domeSes.setServer(svr);
            setSesAttribute(DOME_SESSION_NAME, domeSes);
            return conn;
        }
    }

    private RuntimeInterface getRuntimeIface(DomeConnection conn) {
        log("getting an interface from: " + space + ": " + path);
        DomeInterface diface = null;
        try {
            diface = conn.getInterfaceByPath(space, path);
            log("got the interface");
        } catch (NullPointerException e) {
            throw new RuntimeException("Error getting the specified interface. Make sure that the path " + space + ": "
                    + path + " is valid", e);
        }
        setSesAttribute("interface", diface);
        log("creating the runtime interface");
        RuntimeInterface runiface = diface.createRuntimeInterface();
        log("got the runtim interface");
        return runiface;
    }
}
