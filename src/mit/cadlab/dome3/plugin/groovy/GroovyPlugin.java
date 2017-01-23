/*
 * CatalogPlugin.java
 *
 * Created on August 19, 2005, 4:23 PM
 *
 */

package mit.cadlab.dome3.plugin.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;

import java.io.*;

import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.groovy.dataobject.GroovyReal;
import mit.cadlab.dome3.plugin.groovy.dataobject.GroovyMatrix;
import mit.cadlab.dome3.plugin.groovy.dataobject.GroovyEnumeration;
import mit.cadlab.dome3.plugin.groovy.dataobject.GroovyDataObject;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.util.*;

import org.codehaus.groovy.control.CompilationFailedException;

/**
 * User: Sangmok Han
 * Date: 2005. 9. 6.
 */
public class GroovyPlugin extends AbstractPlugin {
    GroovyScriptEngine gse;
    GroovyShell shell;
	Binding binding;
    private File groovyFile;
    Vector data;
    GroovyModelRuntime modelRuntime;
    private boolean isLoaded = false;

    public GroovyPlugin(String file, GroovyModelRuntime modelRuntime) {
        this.groovyFile = new File(file);
        if (! groovyFile.isFile()) {
            throw new UnsupportedOperationException("fail to read a script file in groovy model - the given file name is not pointing to a file: " + file);
        }
        Debug.trace(Debug.ALL, "running groovy script file: " + groovyFile.getAbsolutePath());
        this.modelRuntime = modelRuntime;
        data = new Vector();
    }
    
    public void createModel() {
        // no initialization needed
        Debug.trace(Debug.ALL, "groovy model has been created");
    }
    
    public void deleteModel() {
        Debug.trace(Debug.ALL, "groovy model has been deleted");
    }

    public void loadModel() {
        Debug.trace(Debug.ALL, "start loading groovy model");
        try {
            String[] roots = new String[] { groovyFile.getParent() };
            gse = new GroovyScriptEngine(roots);
            Debug.trace(Debug.ALL, "groovy model is loaded successfully");
            isLoaded = true;
        } catch (IOException e) {
            throw new RuntimeException("fail to load groovy model: IO exception regarding " + groovyFile.getAbsolutePath());
        }
    }
    
    public synchronized void execute(List affectedOutputParams) {
        Debug.trace(Debug.ALL, affectedOutputParams.toString());

        /* create a new binding for this execution */
        binding = new Binding();

        for (int i = 0; i < data.size(); i++) {
            GroovyDataObject catDataObj = (GroovyDataObject) data.get(i);

            /* bind a catalog data object to a script variable name */
            binding.setVariable(modelRuntime.getMappedScriptVariableName(catDataObj.getParameter()), catDataObj);

            if (! catDataObj.getIsResult()) {
                catDataObj.loadNativeData();
            }
        }

        Debug.trace(Debug.ALL, "script is ready to run. inputs are copied from java to native");

	    try {
            gse.run(groovyFile.getName(), binding);
            /* any modification to output data objects is available now. */
            /* note that, in a groovy script, << operator should be used to assign a computed value to an output variable */
		} catch (groovy.util.ScriptException se) {
			Debug.trace(Debug.ALL, "ScriptException occurred while executing a script: " + se.getCause());
		} catch (ResourceException re) {
			Debug.trace(Debug.ALL, "ResourceException occurred while executing a script: " + re.getCause());
		}

        /* copy from catalog to java(=dome) */
        for (int i = 0; i < data.size(); i++) {
            GroovyDataObject catDataObj = (GroovyDataObject) data.get(i);
            if (isAffectedOutputParameter(catDataObj.getParameter(),affectedOutputParams)) {
                if (catDataObj.getIsResult()) {
                    catDataObj.loadJavaData();
                }
            }
        }

        Debug.trace(Debug.ALL, "script is executed. results are copied from native to java");
    }

    public void executeBeforeInput() {
        /* should ask wei about what is done in C++ side */
    }

    public void executeAfterOutput() {
        /* should ask wei about what is done in C++ side */
    }

    public void unloadModel() {
        /* should ask wei about what is done in C++ side */
    }

    public Object createReal() {
        return createReal(null);
    }

    public GroovyReal createReal(Parameter realParam) {
        GroovyReal real = new GroovyReal(realParam);
        data.addElement(real);
        return real;
    }

    public Object createMatrix() {
        return createMatrix(null);
    }

    public GroovyMatrix createMatrix(Parameter matrixParam) {
        GroovyMatrix matrix = new GroovyMatrix(matrixParam);
        data.addElement(matrix);
        return matrix;
    }

    public Object createEnumeration() {
        return createEnumeration(null);
    }

    public GroovyEnumeration createEnumeration(Parameter enumParam) {
        GroovyEnumeration enm =  new GroovyEnumeration(enumParam);
        data.addElement(enm);
        return enm;
    }

    public boolean isModelLoaded() {
        return isLoaded;
    }
}
