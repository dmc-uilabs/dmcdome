// Registry.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.config;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.DomeException;

import org.dom4j.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Usage:
 * Registry.registerModel("mit.cadlab.dome3.objectmodel.model.MyModelInterface",Registry.INTERFACE_CLS);
 * Registry.registerModel("mit.cadlab.dome3.objectmodel.model.MyModelInterfaceServer",Registry.SERVER_CLS);
 */
public class Registry
{

	// class types
	public static final String INTERFACE_CLS = "interface";
	public static final String BASE_CLS = "base";
	public static final String SERVER_CLS = "server";
	public static final String CLIENT_CLS = "client";
	public static final String BUILDER_CLS = "builder";
	public static final String SERVER_INTERFACE_CLS = "server interface";

	// constructor types
	public static final String DEFAULT_CTR = "default";
	public static final String COPY_CTR = "copy";
	public static final String XML_CTR = "xml";

	// gui constructor types
	public static final String BUILD_GUI = "build_gui";
	public static final String RUN_GUI = "run_gui";

	// all registered classes indexed by className; typeName and typeSymbol for interfaces
	private static HashMap typeNames = new HashMap(); // key = typeName, value = registryKey

	private static HashMap registryKeys = new HashMap(); // key = registryKey, value = _RegistryKeyInfo

	// categories
	private static List modelTypes = new ArrayList(); // typeNames
	private static List relationTypes = new ArrayList();
	private static List dataObjectTypes = new ArrayList();
	private static List pythonDataTypes = new ArrayList();


	//python data types related methods
	public static List getPythonDataTypes()
	{
		return Collections.unmodifiableList(pythonDataTypes);
	}

	public static void addPythonDataType(String type)
	{
		pythonDataTypes.add(type);
	}

	// retrieval functions
	public static List getModelTypes()
	{
		return Collections.unmodifiableList(modelTypes);
	}

	public static List getRelationTypes()
	{
		return Collections.unmodifiableList(relationTypes);
	}

	public static List getDataObjectTypes()
	{
		return Collections.unmodifiableList(dataObjectTypes);
	}

	public static boolean isValidModelType(String modelType)
	{
		return modelTypes.contains(modelType);
	}

	public static boolean isValidRelationType(String relationType)
	{
		return relationTypes.contains(relationType);
	}

	public static boolean isValidDataObjectType(String dataObjectType)
	{
		return dataObjectTypes.contains(dataObjectType);
	}

	public static void assertRegistryInitialized()
	{
		if (registryKeys.isEmpty())
			throw new RuntimeException("Registry has not been initialized. Be sure DomeInit.initializeDOME has been executed.");
	}

	/**
	 * obj can be either String typeName - default constructor,
	 * instance of object - copy constructor
	 * Element (xml) - xml constructor
	 */
	public static Constructor getConstructor(Object obj, String classType)
	{
		assertRegistryInitialized();
		String registryKey, constructorType;
		if (obj instanceof Element) {
			registryKey = getRegistryKey((Element) obj);
			constructorType = Registry.XML_CTR;
		} else if (obj instanceof String) {
			registryKey = (String) typeNames.get(obj);
			constructorType = Registry.DEFAULT_CTR;
		} else { // instance of object itself
			registryKey = getRegistryKey(obj.getClass());
			constructorType = Registry.COPY_CTR;
		}
		_RegistryKeyInfo rKeyInfo = (_RegistryKeyInfo) registryKeys.get(registryKey);
		_SubClassInfo cInfo = (_SubClassInfo) rKeyInfo.getClass(classType);
		return cInfo.getConstructor(constructorType);
	}

	public static _ClassInfo registerModelInterface(String className, String classType)
	{
		_ClassInfo classInfo;

		if (classType.compareTo(INTERFACE_CLS) == 0) {
			classInfo = new _ClassInfo(getObjClass(className));
			registerClass(classInfo, classType);
		} else {
			classInfo = new _ModelInterfaceClassInfo(getObjClass(className), classType);
			registerModelInterface((_ModelInterfaceClassInfo) classInfo, classType);
		}
		return classInfo;
	}

    public static _ClassInfo registerToolInterface(String className, String classType)
    {
        _ClassInfo classInfo;

        if(classType.compareTo(INTERFACE_CLS) == 0)
        {
            classInfo = new _ClassInfo(getObjClass(className));
            registerClass(classInfo, classType);
        }
        else
        {
            classInfo = new _ToolInterfaceClassInfo(getObjClass(className), classType);
            registerToolInterface((_ToolInterfaceClassInfo) classInfo, classType);
        }
        return classInfo;
    }

	public static void registerModelInterface(_ModelInterfaceClassInfo mInfo, String classType)
	{
		registerClass(mInfo, classType);

		if (classType.equals(BUILD_GUI) || classType.equals(RUN_GUI))
			return;

		if (!modelTypes.contains(mInfo.getTypeName()))
			modelTypes.add(mInfo.getTypeName());
	}

    public static void registerToolInterface(_ToolInterfaceClassInfo tInfo, String classType)
    {
        registerClass(tInfo, classType);

        if(classType.equals(BUILD_GUI) || classType.equals(RUN_GUI))
            return;
        if(!modelTypes.contains(tInfo.getTypeName()))
            modelTypes.add(tInfo.getTypeName());
    }


	// registration functions
	public static _ClassInfo registerModel(String className, String classType)
	{
		_ClassInfo classInfo;

		if (classType.compareTo(INTERFACE_CLS) == 0) {
			classInfo = new _ClassInfo(getObjClass(className));
			registerClass(classInfo, classType);
		} else {
			classInfo = new _ModelClassInfo(getObjClass(className), classType);
			registerModel((_ModelClassInfo) classInfo, classType);
		}
		return classInfo;
	}

	public static void registerModel(_ModelClassInfo mInfo, String classType)
	{
		registerClass(mInfo, classType);
		if (classType.equals(BUILD_GUI) || classType.equals(RUN_GUI))
			return;
		if (!modelTypes.contains(mInfo.getTypeName()))
			modelTypes.add(mInfo.getTypeName());
	}

	public static _ClassInfo registerRelation(String className, String classType)
	{
		_ClassInfo classInfo;

		if (classType.compareTo(INTERFACE_CLS) == 0 ||
		        classType.equals(BUILD_GUI) || classType.equals(RUN_GUI)) {
			classInfo = new _ClassInfo(getObjClass(className));
			registerClass(classInfo, classType);
		} else {
			classInfo = new _ModelObjectClassInfo(getObjClass(className));
			registerRelation((_ModelObjectClassInfo) classInfo, classType);
		}
		return classInfo;
	}

	public static void registerRelation(_ModelObjectClassInfo rInfo, String classType)
	{
		registerClass(rInfo, classType);
		if (!relationTypes.contains(rInfo.getTypeName()))
			relationTypes.add(rInfo.getTypeName());
	}

	public static _ClassInfo registerDataObject(String className, String classType)
	{
		_ClassInfo classInfo;

		if (classType.compareTo(INTERFACE_CLS) == 0) {
			classInfo = new _ClassInfo(getObjClass(className));
			registerClass(classInfo, classType);
		} else {
			classInfo = new _DataObjectClassInfo(getObjClass(className), classType);
			registerDataObject((_DataObjectClassInfo) classInfo, classType);
		}
		return classInfo;
	}

	public static void registerDataObject(_DataObjectClassInfo dInfo, String classType)
	{
		registerClass(dInfo, classType);
		if (classType.equals(BUILD_GUI) || classType.equals(RUN_GUI))
			return;
		if (!dataObjectTypes.contains(dInfo.getTypeName()))
			dataObjectTypes.add(dInfo.getTypeName());
	}

	public static _ClassInfo registerModelObject(String className, String classType)
	{
		_ClassInfo classInfo;

		if (classType.compareTo(INTERFACE_CLS) == 0 ||
		        classType.equals(BUILD_GUI) || classType.equals(RUN_GUI)) {
			classInfo = new _ClassInfo(getObjClass(className));
			registerClass(classInfo, classType);
		} else {
			classInfo = new _ModelObjectClassInfo(getObjClass(className));
			registerModelObject((_ModelObjectClassInfo) classInfo, classType);
		}
		return classInfo;
	}

	public static void registerModelObject(_ModelObjectClassInfo mInfo, String classType)
	{
		registerClass(mInfo, classType);
	}

	private static void registerClass(_ClassInfo cInfo, String classType)
	{
		_RegistryKeyInfo regKeyInfo = cInfo.getRegistryKeyInfo();
		regKeyInfo.addClass(cInfo, classType);
		typeNames.put(cInfo.getTypeName(), cInfo.getRegistryKey());
	}

	private static Class getObjClass(String className)
	{
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException ex) {
			throw new DomeException("Registry - class not found: " + className);
		}
	}

	public static String getRegistryKey(String typeName)
	{
		assertRegistryInitialized();
		return (String) typeNames.get(typeName);
	}

	/**
	 * Classes to be registered must all have defined two public static variables:
	 * TYPE_INFO - has type name and xml type information
	 * XML_TAG - has xml tag information
	 * registry key is XML_TAG if no xml type
	 * else it is XML_TAG.xmlType
	 */
	public static String getRegistryKey(Class objClass)
	{
		try {
			Field xmlTagField = objClass.getField("XML_TAG");
			if (xmlTagField == null)
				throw new NoSuchFieldException("Registry - registered classes must have public static XML_TAG field");
			String xmlTag = (String) xmlTagField.get(null);
			Field typeInfoField = objClass.getField("TYPE_INFO");
			if (typeInfoField == null)
				throw new NoSuchFieldException("Registry - registered classes must have public static TYPE_INFO field");
			TypeInfo typeInfo = (TypeInfo) typeInfoField.get(null);
			String xmlType = typeInfo.getXmlType();
			if (xmlType.equals(""))
				return xmlTag;
			else
				return xmlTag + "." + xmlType;
		} catch (Exception ex) {
			throw new DomeException("Registry.getRegistryKey(" + objClass.getName() + "): " +
			                        ClassUtils.getClassName(ex) + " - " + ex.getMessage());
		}
	}

	public static String getRegistryKey(Element xml)
	{
		String xmlTag = xml.getQName().getName();
		String xmlType = xml.attributeValue("type");
		if (xmlType == null || xmlType.equals(""))
			return xmlTag;
		return xmlTag + "." + xmlType;
	}

	public static String getTypeName(Class objClass)
	{
		try {
			Field typeInfoField = objClass.getField("TYPE_INFO");
			if (typeInfoField == null)
				throw new NoSuchFieldException("Registry - registered classes must have public static TYPE_INFO field");
			TypeInfo typeInfo = (TypeInfo) typeInfoField.get(null);
			return typeInfo.getTypeName();
		} catch (Exception ex) {
			throw new DomeException("Registry.getTypeName(" + objClass.getName() + "): " +
			                        ClassUtils.getClassName(ex) + " - " + ex.getMessage());
		}
	}

	/**
	 * The _RegistryKeyInfo class keeps track of each registryKey
	 * including all the classes associated with the registry key.
	 */
	private static class _RegistryKeyInfo
	{
		private String registryKey;
		private String typeName;
		private HashMap classInfoMap = new HashMap(); // key = classType; value = classInfo

		public _RegistryKeyInfo(String registryKey, String typeName)
		{
			this.registryKey = registryKey;
			this.typeName = typeName;
			registryKeys.put(registryKey, this);
		}

		public void addClass(_ClassInfo classInfo, String classType)
		{
			if (classInfoMap.containsKey(classType))
				throw new UnsupportedOperationException(registryKey + " - duplicate entry for " +
				                                        classType + ": " + classInfo);
			classInfoMap.put(classType, classInfo);
		}

		public _ClassInfo getClass(String classType)
		{
			return (_ClassInfo) classInfoMap.get(classType);
		}

		public String getRegistryKey()
		{
			return registryKey;
		}

		public String getTypeName()
		{
			return typeName;
		}

		public String toString()
		{
			return "_RegistryKey: " + registryKey;
		}
	}

	private static class _ClassInfo
	{
		protected Class objClass;
		private _RegistryKeyInfo registryKeyInfo;

		public _ClassInfo(Class objClass)
		{
			this.objClass = objClass;
			setRegistryKey();
		}

		protected void setRegistryKey()
		{
			String registryKey = Registry.getRegistryKey(objClass);
			_RegistryKeyInfo regKeyInfo = (_RegistryKeyInfo) registryKeys.get(registryKey);
			if (regKeyInfo == null) {
				regKeyInfo = new _RegistryKeyInfo(registryKey, Registry.getTypeName(objClass));
			}
			this.registryKeyInfo = regKeyInfo;
		}

		protected _RegistryKeyInfo getRegistryKeyInfo()
		{
			return registryKeyInfo;
		}

		public String getRegistryKey()
		{
			return registryKeyInfo.registryKey;
		}

		public String getTypeName()
		{
			return registryKeyInfo.getTypeName();
		}

		protected Class getInterfaceClass()
		{
			_ClassInfo interfaceClassInfo = registryKeyInfo.getClass(Registry.INTERFACE_CLS);
			if (interfaceClassInfo == null)
				return null;
			return interfaceClassInfo.objClass;
		}

		protected Class getBaseClass()
		{
			_ClassInfo baseClassInfo = registryKeyInfo.getClass(Registry.BASE_CLS);
			if (baseClassInfo == null)
				return getInterfaceClass();
			return baseClassInfo.objClass;
		}

		public String toString()
		{
			return "_ClassInfo: " + objClass.getName();
		}
	}

	/**
	 * The _ClassInfo class keeps track of all the constructors for the class
	 * Should not be mutable outside of the Registry.
	 */
	public static class _SubClassInfo extends _ClassInfo
	{

		private HashMap constructors = new HashMap();

		public _SubClassInfo(Class objClass)
		{
			super(objClass);
		}

		protected void addConstructor(Constructor constructor, String constructorType)
		{
			if (constructors.containsKey(constructorType))
				throw new UnsupportedOperationException(objClass.getName() + " - duplicate entry for constructor type: " +
				                                        constructorType);
			constructors.put(constructorType, constructor);
		}

		public Constructor getConstructor(String constructorType)
		{
			Constructor constructor = (Constructor) constructors.get(constructorType);
			if (constructor == null)
				throw new NullPointerException(objClass.getName() + " - constructor type not found: " + constructorType);
			return constructor;
		}

	}

    public static class _ToolInterfaceClassInfo extends _SubClassInfo
    {
        public _ToolInterfaceClassInfo(Class objClass, String classType)
        {
            super(objClass);
            if(classType.equals(BUILD_GUI) || classType.equals(RUN_GUI))
            {
                Constructor[] cons = null;
                try
                {
                    cons = objClass.getConstructors();
                }
                catch (SecurityException nme)
                {

                }
                addConstructor(cons[0], Registry.DEFAULT_CTR);
            }
            else
            {
                addConstructor(getDefaultConstructor(objClass), Registry.DEFAULT_CTR);
            }
        }

        protected Constructor getDefaultConstructor(Class objClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Model.class, Id.class}); // default constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ToolInterfaceClassInfo - default constructor not found for " + objClass.getName());
			}
		}

		protected Constructor getCopyConstructor(Class objClass, Class baseClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Model.class, Id.class, ModelObjectScope.class}); // copy constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ToolInterfaceClassInfo - copy constructor not found for " + objClass.getName() + "/" + baseClass.getName());
			}
		}

		protected Constructor getXmlConstructor(Class objClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Model.class, Element.class}); // xml constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ToolInterfaceClassInfo - XML constructor not found for " + objClass.getName());
			}
		}
    }
	public static class _ModelInterfaceClassInfo extends _SubClassInfo
	{

		public _ModelInterfaceClassInfo(Class objClass, String classType)
		{
			super(objClass);
			if (classType.equals(BUILD_GUI) || classType.equals(RUN_GUI)) {
				Constructor[] cons = null;
				try {
					cons = objClass.getConstructors();
				} catch (SecurityException nme) {
				}
				addConstructor(cons[0], Registry.DEFAULT_CTR);
			} else {
				addConstructor(getDefaultConstructor(objClass), Registry.DEFAULT_CTR);
				addConstructor(getCopyConstructor(objClass, getInterfaceClass()), Registry.COPY_CTR);
				addConstructor(getXmlConstructor(objClass), Registry.XML_CTR);
			}
		}

		protected Constructor getDefaultConstructor(Class objClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Model.class, Id.class}); // default constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ModelInterfaceClassInfo - default constructor not found for " + objClass.getName());
			}
		}

		protected Constructor getCopyConstructor(Class objClass, Class baseClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Model.class, Id.class, ModelObjectScope.class}); // copy constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ModelInterfaceClassInfo - copy constructor not found for " + objClass.getName() + "/" + baseClass.getName());
			}
		}

		protected Constructor getXmlConstructor(Class objClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Model.class, Element.class}); // xml constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ModelInterfaceClassInfo - XML constructor not found for " + objClass.getName());
			}
		}
	}


	public static class _ModelClassInfo extends _SubClassInfo
	{

		public _ModelClassInfo(Class objClass, String classType)
		{
			super(objClass);
			if (classType.equals(BUILD_GUI) || classType.equals(RUN_GUI)) {
				Constructor[] cons = null;
				try {
					cons = objClass.getConstructors();
				} catch (SecurityException nme) {
				}
				addConstructor(cons[0], Registry.DEFAULT_CTR);
			} else {
				addConstructor(getDefaultConstructor(objClass), Registry.DEFAULT_CTR);
				addConstructor(getCopyConstructor(objClass, getInterfaceClass()), Registry.COPY_CTR);
				addConstructor(getXmlConstructor(objClass), Registry.XML_CTR);
			}
		}

		protected Constructor getDefaultConstructor(Class objClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Id.class}); // default constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ModelClassInfo - default constructor not found for " + objClass.getName());
			}
		}

		protected Constructor getCopyConstructor(Class objClass, Class baseClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Id.class, baseClass}); // copy constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ModelClassInfo - copy constructor not found for " + objClass.getName() + "/" + baseClass.getName());
			}
		}

		protected Constructor getXmlConstructor(Class objClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Element.class}); // xml constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ModelClassInfo - XML constructor not found for " + objClass.getName());
			}
		}
	}

	public static class _ModelObjectClassInfo extends _SubClassInfo
	{

		public _ModelObjectClassInfo(Class objClass)
		{
			super(objClass);
			addConstructor(getDefaultConstructor(objClass), Registry.DEFAULT_CTR);
			addConstructor(getCopyConstructor(objClass, getInterfaceClass()), Registry.COPY_CTR);
			addConstructor(getXmlConstructor(objClass), Registry.XML_CTR);
		}

		protected Constructor getDefaultConstructor(Class objClass)
		{
			try {
				return objClass.getConstructor(new Class[]{ModelObjectScope.class, Id.class}); // default constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ModelObjectClassInfo - default constructor not found for " + objClass.getName());
			}
		}

		protected Constructor getCopyConstructor(Class objClass, Class baseClass)
		{
			try {
				return objClass.getConstructor(new Class[]{ModelObjectScope.class, Id.class, baseClass}); // copy constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ModelObjectClassInfo - copy constructor not found for " + objClass.getName() + "/" + baseClass.getName());
			}
		}

		protected Constructor getXmlConstructor(Class objClass)
		{
			try {
				return objClass.getConstructor(new Class[]{ModelObjectScope.class, Element.class}); // xml constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_ModelObjectClassInfo - XML constructor not found for " + objClass.getName());
			}
		}
	}

	public static class _DataObjectClassInfo extends _SubClassInfo
	{

		public _DataObjectClassInfo(Class objClass, String classType)
		{
			super(objClass);
			addConstructor(getDefaultConstructor(objClass, classType), Registry.DEFAULT_CTR);
			if (!(classType.equals(BUILD_GUI) || classType.equals(RUN_GUI))) {
				addConstructor(getCopyConstructor(objClass, getInterfaceClass()), Registry.COPY_CTR);
				addConstructor(getXmlConstructor(objClass), Registry.XML_CTR);
			}
		}

		protected Constructor getDefaultConstructor(Class objClass, String classType)
		{
			if (!(classType.equals(BUILD_GUI) || classType.equals(RUN_GUI))) {
				try {
					return objClass.getConstructor(new Class[]{}); // default constructor
				} catch (NoSuchMethodException ex) {
					throw new DomeException("_DataObjectClassInfo - default constructor not found for " + objClass.getName());
				}
			}
			Constructor[] cons = null;
			try {
				cons = objClass.getConstructors();
			} catch (SecurityException nme) {
			}
			return cons[0];
		}

		protected Constructor getCopyConstructor(Class objClass, Class baseClass)
		{
			try {
				return objClass.getConstructor(new Class[]{baseClass}); // copy constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_DataObjectClassInfo - copy constructor not found for " + objClass.getName() + "/" + baseClass.getName());
			}
		}

		protected Constructor getXmlConstructor(Class objClass)
		{
			try {
				return objClass.getConstructor(new Class[]{Element.class}); // xml constructor
			} catch (NoSuchMethodException ex) {
				throw new DomeException("_DataObjectClassInfo - XML constructor not found for " + objClass.getName());
			}
		}
	}

}
