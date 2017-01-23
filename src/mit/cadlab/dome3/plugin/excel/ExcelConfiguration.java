// ExcelConfiguration.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin.excel;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.util.FileUtils;

import org.dom4j.Element;

/**
 * Setup parameters for Excel plugin.
 */
public class ExcelConfiguration extends PluginConfiguration
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Excel Model", "EXCEL");
	public static final String[] VALID_DATA_TYPES = new String[]{DomeReal.TYPE_INFO.getTypeName(),
																 DomeString.TYPE_INFO.getTypeName(),
	                                                             DomeMatrix.TYPE_INFO.getTypeName(),
	                                                             DomeEnumeration.TYPE_INFO.getTypeName()};

	public static final String MAPPING_COLUMN_NAME = "sheet_name ! cell_reference";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String FILE_LOCATION = "xls file location";
	public static final String SOFTWARE_VERSION = "software version";
	public static final String RUN_IN_FOREGROUND = "run in foreground";

	public static final String EXCEL_97 = "Excel 97";
	public static final String EXCEL_2000 = "Excel 2000";
	public static final String EXCEL_XP = "Excel XP";
	/* MG- Added Excel 2010 and 2013 */
	public static final String EXCEL_2010 = "Excel 2010";
	public static final String EXCEL_2013 = "Excel 2013";

	public static final String EXCEL_2KXP_DLL = "ExcelPlugin2KXP";
	public static final String EXCEL_97_DLL = "ExcelPlugin97";
	public static final String EXCEL_2010_DLL = "ExcelPlugin2010"; //Added in 2015 July

	public ExcelConfiguration(PluginModel model)
	{
		super (model);

		//Parameter fileLocation = new ConcreteParameter(model, new Id(FILE_LOCATION), DomeFile.TYPE_INFO.getTypeName());
		//String fileDataType = DomeFile.TYPE_INFO.getTypeName();
		//DomeFile file = (mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile) fileLocation.getDataObjectForType(fileDataType);
		//if (file != null)
		//	file.setFileType(FileUtils.EXCEL);
		//fileLocation.setName(FILE_LOCATION);
		//addSetupParameter(fileLocation);

		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		version.addElement(EXCEL_97, "");
		version.addElement(EXCEL_2000, "");
		version.addElement(EXCEL_XP, ""); // TODO: add more excel versions when they are available

		version.addElement(EXCEL_2013, "");
		version.addElement(EXCEL_2010, "");

		version.setLastSelection(1); // default is excel 2010  Changed in year 2015
		addSetupParameter(softwareVersion);

		Parameter runInForeground = new ConcreteParameter(model, new Id(RUN_IN_FOREGROUND), DomeBoolean.TYPE_INFO.getTypeName());
		runInForeground.setName(RUN_IN_FOREGROUND);
		addSetupParameter(runInForeground);
	}

	public ExcelConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
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
