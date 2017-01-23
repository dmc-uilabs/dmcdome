package mit.cadlab.dome3.plugin.javajar;

import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.gui.guiutils.customGui.classLoader.JarLoader;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.javajar.dataobject.*;
import mit.cadlab.dome3.util.AuxiliaryFileUtils;
import mit.cadlab.dome3.util.FileUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.JarURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: jmekler
 * Date: 10/3/11
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarPlugin extends AbstractPlugin {

    private String jarFile;     // jar file to load
    private String className;   // class in jar to instantiate

    private Class classToLoad;  // reference to class which will be instantiated
    private JarModelPluginInterface model;   // instance of classToLoad

    private boolean isModelLoaded = false;

    private Vector data;
    
    private ArrayList<URLConnection> jarLoaders = new ArrayList<URLConnection>(); // Keep track of the URLLoaders to close later.

    // Classpath hack
    private static final Class[] parameters = new Class[] {URL.class};


    public JarPlugin(String jarFile, String className) {
        this.jarFile = jarFile;
        this.className = className;

        data = new Vector();
    }
    public void createModel() {
        // load the jar and check to make sure the class to instantiate exists and implements JarModelPluginInterface
        try {
            String urlPath = "jar:file://localhost/" + jarFile + "!/";
            URLClassLoader child = new URLClassLoader (new URL[] {new URL(urlPath)}, this.getClass().getClassLoader());
            classToLoad = child.loadClass(className);

            // look for JarModelPluginInterface
            boolean interfaceFound = false;

            Class[] interfaces = classToLoad.getInterfaces();
            for (int i=0; i<interfaces.length; i++)
                if (interfaces[i] == JarModelPluginInterface.class) {
                    interfaceFound = true;
                    break;
                }

            if (!interfaceFound) {
                throw new Exception("Class: " + className + " does not implement the JarModelPluginInterface.");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteModel() {
        // do nothing... JVM garbage collector will do the work for us
    	// Not really, lets close all open jar files.
    	for (URLConnection c:jarLoaders) {
			try {
				((JarURLConnection)c).getJarFile().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    protected JarModelPluginInterface getModel() {
        return model;
    }

    public synchronized void loadModel() {
        try {
            // n.b. already ensured that classToLoad implements JarModelPluginInterface in createModel method
            model = (JarModelPluginInterface) classToLoad.newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isModelLoaded() {
        if (model == null)
            return false;
        else
            return ((Boolean) callMethod(JarModelPluginInterface.ISLOADED_METHOD, null)).booleanValue();
    }

    public void unloadModel() {
        model = null;
    }

    public void executeBeforeInput() {
        // nothing to implement
    }

    public synchronized void execute(List affectedOutputParams) {
        if (!isModelLoaded())
            loadModel();

        // load input parameters
        for (int i = 0; i < data.size(); i++) {
            Object obj = data.get(i);
            if (obj instanceof JarPluginData) {
                if (!((JarPluginData) obj).getIsResult())
                    ((JarPluginData) obj).setNativeData();
            }
        }

        // run the "execute" method
        callMethod(JarModelPluginInterface.EXECUTE_METHOD, null);

        // update output parameters
        for (int i = 0; i < data.size(); i++) {
            PluginData obj = (PluginData)data.get(i);
            if (isAffectedOutputParameter(obj.getParameter(),affectedOutputParams)) {
                if (obj instanceof JarPluginData) {
                    if (((JarPluginData) obj).getIsResult()) {
                        ((JarPluginData) obj).setJavaData();
                    }
                }
            }
        }
    }

    public void executeAfterOutput() {
        // nothing to implement
    }


    public JarPluginData createLinkedObject(Parameter p, String fieldName, boolean isInput) throws Exception {

        // Create the linked variable
        JarPluginData obj = null;

        // JarBoolean
        if (p.getCurrentType().equals(DomeBoolean.TYPE_INFO.getTypeName()))
            obj =  new JarBoolean(this, p, fieldName);

            // JarInteger
        else if (p.getCurrentType().equals(DomeInteger.TYPE_INFO.getTypeName()))
            obj =  new JarInteger(this, p, fieldName);

            //JarReal
        else if (p.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName()))
            obj =  new JarReal(this, p, fieldName);

            //JarString
        else if (p.getCurrentType().equals(DomeString.TYPE_INFO.getTypeName()))
            obj =  new JarString(this, p, fieldName);

            //JarFile
        else if (p.getCurrentType().equals(DomeFile.TYPE_INFO.getTypeName()))
            obj = new JarFile(this, p, fieldName);

            //JarVector
        else if (p.getCurrentType().equals(DomeVector.TYPE_INFO.getTypeName()))
            obj = new JarVector(this, p, fieldName);

            //JarMatrix
        else if (p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName()))
            obj = new JarMatrix(this, p, fieldName);

        // Add the new variable to the Vector of variables
        data.add(obj);

        // Set the isResult property of the variable
        if (!isInput)
            obj.setIsResult(true);

        // Return the new JarPluginData object
        return obj;
    }

    public Field getField(String fieldName) {
        try {
            return classToLoad.getField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public Object getFieldValue(String fieldName) {
        try {
            Field field = getField(fieldName);
            return field.get(model);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void setFieldValue(String fieldName, Object fieldValue) {
        try {
            Field field = getField(fieldName);

            // determine which method to call based on classType of fieldValue

            if (fieldValue instanceof Boolean)
                field.setBoolean(model, (Boolean) fieldValue);

            else if (fieldValue instanceof Double)
                field.setDouble(model, (Double) fieldValue);

            else if (fieldValue instanceof Integer)
                field.setInt(model, (Integer) fieldValue);

            else
                field.set(model, fieldValue);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Object callMethod(String methodName, Object[] methodParams) {
        try {
            // determine the type of input arguments
            Class[] parameterTypes = null;
            if (methodParams != null) {
                parameterTypes = new Class[methodParams.length];
                for (int i=0; i<methodParams.length; i++)
                    parameterTypes[i] = (methodParams[i]).getClass();
            }

            // get the requested method
            Method executeMethod = classToLoad.getDeclaredMethod(methodName, parameterTypes);

            // invoke the requested method
            return executeMethod.invoke(model, methodParams);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // if we failed... return null
        return null;
    }
    
    public void addURL(URL u) throws IOException, URISyntaxException
    {
    	URL jarUrl = new URL("jar", "", -1, u.toURI().toString() + "!/");
    	URLConnection c = jarUrl.openConnection();
    	jarLoaders.add(c);
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] {u});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }

    }
    
    public static void SaddURL(URL u) throws IOException
    {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] {u});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }

    }
}