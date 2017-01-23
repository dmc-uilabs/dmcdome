/*
 * CatalogConfiguration.java
 *
 * Created on August 19, 2005, 4:24 PM
 *
 */

package mit.cadlab.dome3.plugin.catalog;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import org.dom4j.Element;

/**
 * User: Sangmok Han
 * Date: Aug 15, 2005
 */
public class CatalogConfiguration  extends PluginConfiguration {
	public static final TypeInfo TYPE_INFO = new TypeInfo("Catalog Model", "CATALOG");
	public static final String[] VALID_DATA_TYPES = new String[]{DomeReal.TYPE_INFO.getTypeName(),
	                                                             DomeMatrix.TYPE_INFO.getTypeName(),
	                                                             DomeEnumeration.TYPE_INFO.getTypeName()};

	public static final String MAPPING_COLUMN_NAME = "script variable";
	public static final int MAPPING_COLUMN_SIZE = 150;

//	public static final String FILE_LOCATION = "catalog file location updated";
//	public static final String SOFTWARE_VERSION = "catalog software version";
//	public static final String RUN_IN_FOREGROUND = "catalog run in foreground";
//
//	public static final String EXCEL_97 = "catalog Excel 97";
//	public static final String EXCEL_2000 = "catalog Excel 2000";
//	public static final String EXCEL_XP = "catalog Excel XP";
//
//	public static final String EXCEL_2KXP_DLL = "ExcelPlugin2KXP";
//	public static final String EXCEL_97_DLL = "ExcelPlugin97";

	public CatalogConfiguration(PluginModel model)
	{
		super(model);

//		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), DomeEnumeration.TYPE_INFO.getTypeName());
//		softwareVersion.setName(SOFTWARE_VERSION);
//		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
//		version.addElement(EXCEL_97, "");
//		version.addElement(EXCEL_2000, "");
//		version.addElement(EXCEL_XP, ""); // todo: add more excel versions when they are available
//		version.setLastSelection(1); // default is excel 2000
//		addSetupParameter(softwareVersion);
//
//		Parameter runInForeground = new ConcreteParameter(model, new Id(RUN_IN_FOREGROUND), DomeBoolean.TYPE_INFO.getTypeName());
//		runInForeground.setName(RUN_IN_FOREGROUND);
//		addSetupParameter(runInForeground);
	}

	public CatalogConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
	{
		super(model, moFactory, xmlElement);
	}

	public TypeInfo getTypeInfo()
	{
		return TYPE_INFO;
	}

	public String getMappingColumnName()
	{
		return MAPPING_COLUMN_NAME;
	}

	public int getMappingColumnSize()
	{
		return MAPPING_COLUMN_SIZE;
	}

    public String[] getValidDataTypes() {
        return VALID_DATA_TYPES;
    }
}
