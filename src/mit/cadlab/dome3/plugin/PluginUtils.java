// PluginUtils.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin;

import edu.iupui.rg.ucum.units.UnitAtom;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.objectmodel.model.plugin.PluginBuildMenus;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksConfiguration;
import mit.cadlab.dome3.plugin.catia.CATIAConfiguration;
import mit.cadlab.dome3.plugin.ideas.IdeasConfiguration;
import mit.cadlab.dome3.plugin.ideas10.Ideas10Configuration;
import mit.cadlab.dome3.plugin.ug.UgConfiguration;
import org.dom4j.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PluginUtils {

    // todo: use registry instead of this class

    private static List pluginNames = new ArrayList();

    private static HashMap configCtrs = new HashMap();      // keyed by type name
    private static HashMap configXmlCtrs = new HashMap();   // keyed by xml type
    private static HashMap runtimeCtrs = new HashMap();     // keyed by xml type/dbConstants

    //qing add here for copy/paste support for pluginmodel
    private static HashMap pluginDataTypeMap = new HashMap();//keyed by plugin type

    public static void registerPluginModel(String pluginConfigurationClassName,
                                           String pluginRuntimeClassName) {
        String typeName = null;
        String xmlType = null;
        Constructor ctr = null;
        try {
            Class configClass = null;
            try {
                configClass = Class.forName(pluginConfigurationClassName);
            } catch (ClassNotFoundException e) {
                throw e;
            }

            TypeInfo typeInfo = null;
            try {
                Field field = configClass.getField("TYPE_INFO");
                typeInfo = (TypeInfo) field.get(null);
            } catch (NoSuchFieldException e) {
                throw new NoSuchFieldException(pluginConfigurationClassName + " missing public static TYPE_INFO field.");
            } catch (ClassCastException e) {
                throw new ClassCastException(pluginConfigurationClassName + " invalid TYPE_INFO field. Must be TypeInfo.");
            }
            typeName = typeInfo.getTypeName();
            xmlType = typeInfo.getXmlType();

            if (pluginNames.contains(typeName)) {
                System.err.println("duplicate plugin registration ignored: " + typeName + "\t" + pluginConfigurationClassName);
                return;
            }
            pluginNames.add(typeName);

            String[] validDataTypes = null;
            try {
                Field field = configClass.getField("VALID_DATA_TYPES");
                validDataTypes = (String[]) field.get(null);
            } catch (NoSuchFieldException e) {
                throw new NoSuchFieldException(pluginConfigurationClassName + " missing public static VALID_DATA_TYPES field.");
            } catch (ClassCastException e) {
                throw new ClassCastException(pluginConfigurationClassName + " invalid VALID_DATA_TYPES field. Must be String[].");
            }

            DomeFileChooser.registerPluginFileFilter(typeName, typeInfo.getXmlType());
            if (! DomeClientApplication.DOME_SERVER_MODE)
                PluginBuildMenus.registerDataTypesForPlugin(typeName, validDataTypes);

//add the valid data type info here
            ArrayList validDataTypesArraylist = new ArrayList(validDataTypes.length);
            for (int i = 0; i < validDataTypes.length; ++i)
                validDataTypesArraylist.add(validDataTypes[i]);
            pluginDataTypeMap.put(typeName, validDataTypesArraylist);


            try {
                ctr = configClass.getConstructor(new Class[]{PluginModel.class});
                configCtrs.put(typeName, ctr);
            } catch (NoSuchMethodException ex) {
                throw new NoSuchMethodException(pluginConfigurationClassName + "(PluginModel model) constructor not found.");
            }

            try {
                ctr = configClass.getConstructor(new Class[]{PluginModel.class, ModelObjectFactory.class, Element.class});
                configXmlCtrs.put(xmlType, ctr);
            } catch (NoSuchMethodException ex) {
                throw new NoSuchMethodException(pluginConfigurationClassName + "(PluginModel model, ModelObjectFactory moFactory, Element xmlElement) constructor not found.");
            }
        } catch (Exception ex) {
            System.err.println("Error registering plugin " + pluginConfigurationClassName + ":\n\t" + ex);
        }

        if (pluginRuntimeClassName == null) {
            System.out.println("Warning: no runtime class registered for " + pluginConfigurationClassName);
            return;
        }
        try {
            Class runtimeClass = null;
            try {
                runtimeClass = Class.forName(pluginRuntimeClassName);
            } catch (ClassNotFoundException e) {
                throw e;
            }

            try {
                ctr = runtimeClass.getConstructor(new Class[]{CompoundId.class, Element.class, boolean.class});
                runtimeCtrs.put(xmlType, ctr);
            } catch (NoSuchMethodException ex) {
                throw new NoSuchMethodException(pluginRuntimeClassName + "(CompoundId id, Element xmlElement, boolean isProjectResource) constructor not found.");
            }
        } catch (Exception ex) {
            System.err.println("Error registering plugin " + pluginRuntimeClassName + ":\n\t" + ex);
        }
    }

    public static List getPluginNames() {
        return Collections.unmodifiableList(pluginNames);
    }

    public static PluginConfiguration createPluginConfiguration(String pluginTypeName, PluginModel model) {
        Constructor ctr = (Constructor) configCtrs.get(pluginTypeName);
        if (ctr == null)
            throw new IllegalArgumentException("PluginUtils.createPluginConfiguration - invalid pluginTypeName: " + pluginTypeName);
        try {
            return (PluginConfiguration) ctr.newInstance(new Object[]{model});
        } catch (Exception e) {
            System.err.println("PluginUtils.createPluginConfiguration error: " + pluginTypeName + "\t" + model.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static PluginConfiguration createPluginConfiguration(String pluginXmlType, PluginModel model, ModelObjectFactory moFactory, Element xmlElement) {
        Constructor ctr = (Constructor) configXmlCtrs.get(pluginXmlType);
        if (ctr == null)
            throw new IllegalArgumentException("PluginUtils.createPluginConfiguration - invalid pluginXmlType: " + pluginXmlType);
        try {
            return (PluginConfiguration) ctr.newInstance(new Object[]{model, moFactory, xmlElement});
        } catch (Exception e) {
            System.err.println("PluginUtils.createPluginConfiguration error: " + pluginXmlType + "\t" + model.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static ModelRuntime getPluginRuntime(String pluginDbType, CompoundId id, Element xmlElement, boolean isProjectResource) {
        Constructor ctr = (Constructor) runtimeCtrs.get(pluginDbType);
        if (ctr == null)
            throw new IllegalArgumentException("PluginUtils.getPluginRuntime - invalid pluginDbType: " + pluginDbType);
        ModelRuntime model = null;

        try {
            model = (ModelRuntime) ctr.newInstance(new Object[]{id, xmlElement, new Boolean(isProjectResource)});
        } catch (InvocationTargetException e) {
            System.err.println("PluginUtils.getPluginRuntime error: " + pluginDbType + "\t" + id);
            System.err.println(e.getTargetException());
            throw new RuntimeException(e.getTargetException());
        } catch (Exception e) {
            System.err.println("PluginUtils.getPluginRuntime error: " + pluginDbType + "\t" + id);
            throw new RuntimeException(e);
        }

        return model;
    }


    /**
     * Qing add here for plugin copy/paste support
     * PluginModel only allows Context and Parameters to be copy/paste
     * The method will filter:
     * 1) non-DomeObject
     * 2) invalid basic dome parameter types
     * 3) inside context, go through all paramters and prevent invalid parameters to get through
     * 4) among all parameters, check out invalid customized paramter types
     *
     * note: in build mode, the copy/paste won't have problems but at runtime, there will be errors for those not-supportted parameters
     * return List[2]: first is validate list,second is invalidate list
     */
    public static List[] FilterValidDataTypes(String pluginType, List objs) {
        ArrayList validated = new ArrayList(objs.size());//won't be more than the input list's size
        ArrayList invalidated = new ArrayList(objs.size());//won't be more than the input list's size
        ArrayList validDataTypes = (ArrayList) pluginDataTypeMap.get(pluginType);
        if (validDataTypes == null || validDataTypes.size() == 0) return null;//not in map
        //go through each one in the list
        for (Iterator i = objs.iterator(); i.hasNext();) {
            Object obj = i.next();
            if (obj instanceof DomeObject) {
                //first check whether it is context or a parameter
                if (obj instanceof Context) {
                    if (isValidContext(pluginType, (Context) obj))
                        validated.add(obj);
                    else
                        invalidated.add(obj);
                } else if (obj instanceof Parameter) {
                    if (isValidParameter(pluginType, (Parameter) obj))
                        validated.add(obj);
                    else
                        invalidated.add(obj);
                } else
                    invalidated.add(obj);
            }

        }
        validated.trimToSize();
        invalidated.trimToSize();
        return new List[]{validated, invalidated};
    }


    public static boolean isValidContext(String pluginType, Context cont) {
        ArrayList validDataTypes = (ArrayList) pluginDataTypeMap.get(pluginType);
        if (validDataTypes == null || validDataTypes.size() == 0) return false;//not in map

        Collection contextobjs = cont.getFlattenedContentSet();
        boolean isValid = true;
        for (Iterator j = contextobjs.iterator(); j.hasNext();) {
            Object obj = j.next();
            if (!(obj instanceof Context) && !(obj instanceof Parameter)) {
                isValid = false;
                break;
            } else if (obj instanceof Parameter) {
                if (!isValidParameter(pluginType, (Parameter) obj)) {
                    isValid = false;
                    break;
                }
            }
        }
        if (isValid) return true;
        return false;
    }


    public static boolean isValidParameter(String pluginType, Parameter param) {
        ArrayList validDataTypes = (ArrayList) pluginDataTypeMap.get(pluginType);
        if (validDataTypes == null || validDataTypes.size() == 0) return false;//not in map

        String pType = param.getCurrentDataObject().getTypeName();

        if (validDataTypes.contains(pType))
            return true;

        else {//in this case, the pType is a customized datatype,it probabally fit an basic data type
            //is it a real?
            if (param.getDataObjectForType(DomeReal.TYPE_INFO.getTypeName()) != null) {
                if (validDataTypes.contains(DomeReal.TYPE_INFO.getTypeName()))
                    return true;
                else if(pluginType.equals(CATIAConfiguration.TYPE_INFO.getTypeName())||pluginType.equals(SolidworksConfiguration.TYPE_INFO.getTypeName())||
                    pluginType.equals(IdeasConfiguration.TYPE_INFO.getTypeName())||pluginType.equals(Ideas10Configuration.TYPE_INFO.getTypeName())||
                       pluginType.equals(UgConfiguration.TYPE_INFO.getTypeName()))
                {
                    String categoryname=UnitAtom.getUnitCategory(param.getCurrentDataObject().getUnit().toString());
                    if(categoryname.equalsIgnoreCase("area")||categoryname.equalsIgnoreCase("mass")||categoryname.equalsIgnoreCase("density")||categoryname.equalsIgnoreCase("volume")||categoryname.equalsIgnoreCase("length"))
                       return true;
                }
                else  return false;
            }
            //is it a String?
            else if (param.getDataObjectForType(DomeString.TYPE_INFO.getTypeName()) != null) {
                if (validDataTypes.contains(DomeString.TYPE_INFO.getTypeName()))
                    return true;
                else
                    return false;
            }
            //is it a boolean
           else if (param.getDataObjectForType(DomeBoolean.TYPE_INFO.getTypeName()) != null) {
                if (validDataTypes.contains(DomeBoolean.TYPE_INFO.getTypeName()))
                    return true;
                else
                    return false;
            }
            //is it a Integer?
            else if (param.getDataObjectForType(DomeInteger.TYPE_INFO.getTypeName()) != null) {
                if (validDataTypes.contains(DomeInteger.TYPE_INFO.getTypeName()))
                    return true;
                else
                    return false;
            }
            //is it a file?
            else if (param.getDataObjectForType(DomeFile.TYPE_INFO.getTypeName()) != null) {
                if (validDataTypes.contains(DomeFile.TYPE_INFO.getTypeName()))
                    return true;
                else if(pluginType.equals(CATIAConfiguration.TYPE_INFO.getTypeName())||pluginType.equals(SolidworksConfiguration.TYPE_INFO.getTypeName())||
                    pluginType.equals(IdeasConfiguration.TYPE_INFO.getTypeName())||pluginType.equals(Ideas10Configuration.TYPE_INFO.getTypeName())||
                       pluginType.equals(UgConfiguration.TYPE_INFO.getTypeName()))
                    return true;
                else
                    return false;
            }
            //is it a list?
            else if (param.getDataObjectForType(DomeList.TYPE_INFO.getTypeName()) != null) {
                if (validDataTypes.contains(DomeList.TYPE_INFO.getTypeName()))
                    return true;
                else
                    return false;
            }
            //is it a vector?
            else if (param.getDataObjectForType(DomeVector.TYPE_INFO.getTypeName()) != null) {
                if (validDataTypes.contains(DomeVector.TYPE_INFO.getTypeName()))
                    return true;
                else
                    return false;
            }
            //is it a matrix?
            else if (param.getDataObjectForType(DomeMatrix.TYPE_INFO.getTypeName()) != null) {
                if (validDataTypes.contains(DomeMatrix.TYPE_INFO.getTypeName()))
                    return true;
                else
                    return false;
            }
        }

        return false;
    }
}
