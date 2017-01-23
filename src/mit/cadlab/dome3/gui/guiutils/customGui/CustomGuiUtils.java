// CustomGuiUtils.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.customGui;

import com.javaworld.JarClassLoader;
import mit.cadlab.dome3.gui.guiutils.customGui.classLoader.JarLoader;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.toolinterface.AnalysisToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.util.FileUtils;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 */
public class CustomGuiUtils {

	public static final String URL_SEPARATOR = "/";
	public static boolean USE_CUSTOM_JAR_LOADER = true; // use URLloader or JarLoader

    //this means locally
    public static JComponent createCustomGui(CustomGuiInfo fileInfo, ModelInterface mInterface) {

        String className = fileInfo.getClassName();

        if (className == null || className.trim().equals("")) {
            return new JPanel();//empty
        }


        //  AbstractAuxFile customGuiFile = ((AbstractDomeModel) mInterface.getModel()).getAuxFileForId(fileInfo.getJarFileId());
        File customGuiFile = new File(fileInfo.getJarFilePath());
        if (!customGuiFile.exists()) {
            System.out.println("that auxiliary file doesn't exist locally.");
            return new JPanel();
        }

        JComponent comp = getLocally(customGuiFile.getPath(), className, mInterface);

        if (comp == null)
            return new JPanel();
        return comp;
    }

    public static JComponent createCustomGui(CustomGuiInfo fileInfo, ToolInterface tInterface)
    {

        String className = fileInfo.getClassName();

        if (className == null || className.trim().equals(""))
        {
            return new JPanel();//empty
        }


        //  AbstractAuxFile customGuiFile = ((AbstractDomeModel) mInterface.getModel()).getAuxFileForId(fileInfo.getJarFileId());
        File customGuiFile = new File(fileInfo.getJarFilePath());
        if (!customGuiFile.exists())
        {
            System.out.println("that auxiliary file doesn't exist locally.");
            return new JPanel();
        }

        JComponent comp = getLocally(customGuiFile.getPath(), className, tInterface);

        if (comp == null)
            return new JPanel();
        return comp;
    }


    //get it from server,
    public static JComponent createCustomGuiFromRemote(CustomGuiInfo fileInfo, ModelInterface mInterface, ServerConnection svr) {

        CompoundId interfaceId = ((ModelInterfaceRuntimeClient) mInterface).getRuntimeId();
        String modelId = interfaceId.getModelStaticId();
	    if (modelId==null) { // project interface
		    // todo: make sure this works for interfaces of projects in projects (compound ids w/multiple projects)
		    modelId = interfaceId.getCurrentProjectStaticId();
	    }
        String deployId = interfaceId.getInterfaceStaticId();

        //File file= new File(fileInfo.getJarFilePath());

        String filename = FileUtils.getFileName(fileInfo.getJarFilePath());
        // String filename = file.getName();
        if(filename.equals("")){
            System.out.println("not a valid filename");
            return new JPanel();
        }

        String jarPath = modelId+URL_SEPARATOR+deployId+URL_SEPARATOR+filename;

        String className = fileInfo.getClassName();

        if (className == null || className.trim().equals("")) {
            return new JPanel();//empty
        }
        JComponent comp = getRemotely(svr, jarPath, className, mInterface);
        if (comp == null)
            return new JPanel();
        return comp;
    }

    public static JComponent createCustomGuiFromRemote(CustomGuiInfo fileInfo, ToolInterface tInterface, ServerConnection svr) {

        CompoundId interfaceId = ((OptimizationInterfaceRuntimeClient) tInterface).getRuntimeId();
        String modelId = interfaceId.getModelStaticId();
	    if (modelId==null) { // project interface
		    // todo: make sure this works for interfaces of projects in projects (compound ids w/multiple projects)
		    modelId = interfaceId.getCurrentProjectStaticId();
	    }
        String deployId = interfaceId.getInterfaceStaticId();

        //File file= new File(fileInfo.getJarFilePath());

        String filename = FileUtils.getFileName(fileInfo.getJarFilePath());
        // String filename = file.getName();
        if(filename.equals("")){
            System.out.println("not a valid filename");
            return new JPanel();
        }

        String jarPath = modelId+URL_SEPARATOR+deployId+URL_SEPARATOR+filename;

        String className = fileInfo.getClassName();

        if (className == null || className.trim().equals("")) {
            return new JPanel();//empty
        }
        JComponent comp = getRemotely(svr, jarPath, className, tInterface);
        if (comp == null)
            return new JPanel();
        return comp;
    }

    /**
     *
     * @param jarPath  :local full path
     * @param classname
     * @param mInterface
     * @return
     */
    protected static JComponent getLocally(String jarPath, String classname, ModelInterface mInterface) {
        File jarFile = new File(jarPath);
        if (!jarFile.exists()) {
            System.out.println("not available locally");
            return null;
        }
        try {
            URLClassLoader jarLoader = new URLClassLoader(new URL[]{jarFile.toURL()});
            Thread.currentThread().setContextClassLoader(jarLoader); // todo: reset to initial value?
            Class guiClass = jarLoader.loadClass(classname);
            Constructor ctr = null;
            if (mInterface instanceof ModelInterfaceBase) {
                ctr = guiClass.getConstructor(new Class[]{ModelInterfaceBase.class});
            }
            JComponent comp = (JComponent) ctr.newInstance(new Object[]{mInterface});
            System.out.println("get it locally");
            return comp;
        } catch (Exception ee) {
            ee.printStackTrace();
            System.out.println("not available locally");
            return null;
        }
    }

    protected static JComponent getLocally(String jarPath, String classname, ToolInterface tInterface)
    {
        File jarFile = new File(jarPath);
        if (!jarFile.exists())
        {
            System.out.println("not available locally");
            return null;
        }
        try
        {
            URLClassLoader jarLoader = new URLClassLoader(new URL[]{jarFile.toURL()});
            Thread.currentThread().setContextClassLoader(jarLoader); // todo: reset to initial value?
            Class guiClass = jarLoader.loadClass(classname);
            Constructor ctr = null;
            if (tInterface instanceof AnalysisToolInterfaceBase)
            {
                ctr = guiClass.getConstructor(new Class[]{AnalysisToolInterfaceBase.class});
            }
            JComponent comp = (JComponent) ctr.newInstance(new Object[]{tInterface});
            System.out.println("get it locally");
            return comp;
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
            System.out.println("not available locally");
            return null;
        }
    }

    /**
     *
     * @param svr
     * @param jarName   : path on server
     * @param classname
     * @param mInterface
     * @return
     */
    protected static JComponent getRemotely(ServerConnection svr, String jarName, String classname, ModelInterface mInterface) {
        try {
	        ClassLoader jarLoader = null;
	        if (USE_CUSTOM_JAR_LOADER) {
		        ServerConnection conn = ((ModelInterfaceRuntimeClient) mInterface).getServerConnection();
		        Object[] jarLengthAndBytes = RuntimeFunctionsClient.getCustomGuiJar(conn, jarName);
		        int jarLength = ((Integer)jarLengthAndBytes[0]).intValue();
		        byte[] jarBytes = (byte[])jarLengthAndBytes[1];
		        jarLoader = new JarLoader(jarBytes, jarLength, CustomGuiUtils.class.getClassLoader());
	        } else {
		        String server_ip = svr.getServerPort();
		        String url = "http://" + server_ip + "/AuxFiles/" + jarName;
		        ////  url = "http://CADLAB27.MIT.EDU:8080/AuxFiles/d81f43f8-b77e-1004-8bd7-988d41388318/suspensionGui.jar";

		        URL u = new URL("jar", "", url + "!/");
		        jarLoader = new URLClassLoader(new URL[]{u}, CustomGuiUtils.class.getClassLoader());
	        }
	        Thread.currentThread().setContextClassLoader(jarLoader); // todo: reset to initial value?
	        Class guiClass = jarLoader.loadClass(classname);
	        Constructor ctr = null;
	        if (mInterface instanceof ModelInterfaceBase) {
		        ctr = guiClass.getConstructor(new Class[]{ModelInterfaceBase.class});
	        }

	        JComponent comp = (JComponent) ctr.newInstance(new Object[]{mInterface});
	        System.out.println("got gui from the server");
	        return comp;
        } catch (Exception ee) {
            ee.printStackTrace();
            System.out.println("not available on the server");
            return null;
        }
    }

    protected static JComponent getRemotely(ServerConnection svr, String jarName, String classname, ToolInterface tInterface)
    {
        try
        {
            //move it to domeserver
            String server_ip = svr.getServerPort();
            String url = "http://" + server_ip + "/AuxFiles/" + jarName;
            ////  url = "http://CADLAB27.MIT.EDU:8080/AuxFiles/d81f43f8-b77e-1004-8bd7-988d41388318/suspensionGui.jar";

            URL u = new URL("jar", "", url + "!/");
            // URL u = new URL("jar:"+url+"!/");
            // JarURLConnection jarConnection = (JarURLConnection)u.openConnection();

            URLClassLoader jarLoader = new URLClassLoader(new URL[]{u});
            Thread.currentThread().setContextClassLoader(jarLoader); // todo: reset to initial value?
            Class guiClass = jarLoader.loadClass(classname);
            Constructor ctr = null;
            if (tInterface instanceof AnalysisToolInterfaceBase)
            {
                ctr = guiClass.getConstructor(new Class[]{AnalysisToolInterfaceBase.class});
            }
            JComponent comp = (JComponent) ctr.newInstance(new Object[]{tInterface});
            System.out.println("get it on the server");
            return comp;
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
            System.out.println("not available on the server");
            return null;
        }
    }

    protected static JComponent getRemotely(String serverlocation, String classname, ModelInterface mInterface) {
        try {
            //move it to domeserver
            //String server_ip = svr.getServerPort();
            String url = "http://" + serverlocation;
            //this should be getting from database
            URL u = new URL("jar", "", url + "!/");
            URLClassLoader jarLoader = new URLClassLoader(new URL[]{u});
            Thread.currentThread().setContextClassLoader(jarLoader); // todo: reset to initial value?
            Class guiClass = jarLoader.loadClass(classname);
            Constructor ctr = null;
            if (mInterface instanceof ModelInterfaceBase) {
                ctr = guiClass.getConstructor(new Class[]{ModelInterfaceBase.class});
            }
            JComponent comp = (JComponent) ctr.newInstance(new Object[]{mInterface});
            System.out.println("get it on the server");
            return comp;
        } catch (Exception ee) {
            ee.printStackTrace();
            System.out.println("not available on the server");
            return null;
        }
    }

    /**
     * assuming the jar path and class path are all valid path
     * only need to check if the class is a subclass of JComponent or not
     * @param jarPath: local path
     * @param classname
     * @return
     */
    public static boolean isCustomGuiJcomponent(String jarPath, String classname) {
	    try {
            JarClassLoader jarLoader = new JarClassLoader(jarPath);
            Class guiClass = jarLoader.loadClass(classname);
		    return JComponent.class.isAssignableFrom(guiClass);
        } catch (Exception ee) {
            return false;
        }
    }

    /**
     * this will be called to change class filepath to class name with package
     * @param classfilepath
     * @return
     */
    public static String parse(String classfilepath) {
        //   String seperator=File.separator;
        String seperator = "/";
        if (classfilepath.indexOf(seperator) != -1) {
            classfilepath = classfilepath.replaceAll(seperator, ".");
            //  System.out.println("replace after:"+classfilepath);
        }
        return classfilepath;
    }

}