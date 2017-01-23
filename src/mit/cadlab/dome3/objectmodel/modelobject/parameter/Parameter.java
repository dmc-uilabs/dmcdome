// Parameter.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeCollection;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A Parameter represents a dataobject or set of dataobjects.
 */
public interface Parameter extends ModelObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Parameter");
	public static final String XML_TAG = "parameter";
	public static final String XML_MAPPED_TAG = "mappedParameter";
	public static final String DATATYPESELECTION = "dataTypeSelection";
	public static final String CURRENT_TYPE = "currentType";
	public static final String CONSTANT = "constant";
	public static final String VALUE_STATUS = "valueStatus";

	public static final String VALUE_STATUS_STALE = "STALE";
	public static final String VALUE_STATUS_CONSISTENT = "CONSISTENT";
	public static final String VALUE_STATUS_INCONSISTENT = "INCONSISTENT";
	public static final String VALUE_STATUS_WAITING_VALIDATION = "WAITING_VALIDATION";

	public boolean supportsType(String type);

	public DataTypeSelection getDataTypeSelection();

	public void setDataTypeSelection(DataTypeSelection dtSel);

	public String getCurrentType();

	public void setCurrentType(String type);

	public boolean isConstant();

	public void setConstant(boolean isConstant);

	public DataObject getCurrentDataObject();

	public DataObject getDataObjectForType(String type);

	public List getDataObjects();

	public String[] getDataObjectTypes();

	public static class DataTypeSelection
	{
		// immutable class!
		protected List validDataTypes;
		protected String selectedDataType;

		public DataTypeSelection(List validDataTypes, String selectedType)
		{
			if (validDataTypes == null || selectedType == null)
				throw new IllegalArgumentException("DataTypeSelection - null parameter");
			if (validDataTypes.isEmpty())
				throw new IllegalArgumentException("DataTypeSelection - empty valid data types");
			if (!validDataTypes.contains(selectedType))
				selectedType = (String) validDataTypes.get(0);
			this.validDataTypes = Collections.unmodifiableList(new ArrayList(validDataTypes));
			this.selectedDataType = selectedType;
		}

		public DataTypeSelection(String[] validDataTypes, String selectedType)
		{
			this(new ArrayList(Arrays.asList(validDataTypes)), selectedType);
		}

		public DataTypeSelection(String[] validDataTypes)
		{
			this(validDataTypes, "");
		}

		public DataTypeSelection(String dataType)
		{
			this(new String[]{dataType}, dataType);
		}

		public List getValidDataTypes()
		{
			return validDataTypes;
		}

		public String getSelectedDataType()
		{
			return selectedDataType;
		}

		public boolean equals(Object obj)
		{
			if ((obj == null) || !(obj instanceof DataTypeSelection))
				return false;
			DataTypeSelection dtSel = (DataTypeSelection) obj;
			if (this.validDataTypes.size() == dtSel.validDataTypes.size()
			        && this.selectedDataType.equals(dtSel.selectedDataType)) {
				// compare contents of lists
				Iterator it1 = validDataTypes.iterator();
				Iterator it2 = dtSel.validDataTypes.iterator();
				while (it1.hasNext()) {
					if (!it1.next().equals(it2.next())) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}

		public String toString()
		{
			return selectedDataType + "\t" + validDataTypes;
		}

	}

	public Element toXmlMappedRef();

	public String getValueStatus();

	public void setValueStatus(String status);

	public DomeCollection getContainerCollection();

	public void setContainerCollection(DomeCollection container);
}
