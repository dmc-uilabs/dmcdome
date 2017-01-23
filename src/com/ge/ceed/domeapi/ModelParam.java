package com.ge.ceed.domeapi;

import java.util.List;
import org.slf4j.Logger;

import edu.iupui.rg.ucum.units.Unit;

import mit.cadlab.dome3.api.RuntimeParameter;
import mit.cadlab.dome3.objectmodel.util.id.Id;

/**
 * Model Parameter 
 * Holds parameter information for interfaces to be transferred from {@link RuntimeParameter}
 * 
 * @author emacdonald
 *
 */
public class ModelParam {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ModelParam.class);
	
	public static final String FILE_TYPE = "File";
	public static final String MATRIX_TYPE = "Matrix";
	public static final String VECTOR_TYPE = "Vector";
	public static final String ENUM_TYPE = "Enumeration";
	private String name; // friendly name of the parameter
	private String type; // parameter's type, e.g. integer, real, float, complex
	private String unit; // parameter units, e.g. cm, in, mi, cfm
	private String category; // unit's category
	private Object value; // value for this instance
	private String parameterid; // id for this parameter
	private String instancename; // instance of an interface for this parameter, if any
	
	@SuppressWarnings("unused") //for Gson
	private ModelParam() {
		
	}
	
	/**
	 * Constructor used during deserialization
	 * 
	 * @param paramName
	 * @param dataType
	 * @param unit
	 */
	public ModelParam(String paramName, String dataType, String unit) {
		this.name = paramName;
		this.type = dataType;
		this.unit = unit;
	}

	/**
	 * Constructor used during serialization
	 * @param proxy
	 * @param p
	 */
	public ModelParam(DomeProxy proxy, RuntimeParameter p) {
		this.name = p.getParamName();
		this.type = p.getDataType();
		this.unit = p.getUnitDesc();
		this.parameterid = p.getParamId();
		Unit uni = p.getUnit();
		this.category = (uni != null?uni.getCategory():null);

		@SuppressWarnings("rawtypes")
		List rawValue = p.getRawValue();

		if (rawValue != null && !rawValue.isEmpty()) {
			if (VECTOR_TYPE.equals(this.type)) {
				logger.debug("vector: {}", p.getVectorValue());
				setValue(p.getVectorValue());
			} else if (MATRIX_TYPE.equals(this.type)) {
				logger.debug("matrix: {}", p.getMatrixValue());
				setValue(p.getMatrixValue());
			} else if (FILE_TYPE.equalsIgnoreCase(this.type)) {
				String pFilename = p.getFileName();
				logger.debug("file name: {}", pFilename);
				setValue(pFilename);
			} else if (ENUM_TYPE.equalsIgnoreCase(this.type)) {
				logger.debug("Enum: {}", p.getEnumerationValue());
				setValue(p.getEnumerationValue());
			} else {
				// real, integer, enumeration, boolean, string
				logger.debug("{} {} primitive: {}",new Object[]{this.name, this.type, p.getRawValue()});
				//TODO: what about the rest of the list??
				setValue(p.getRawValue().get(0));
			}
		}
	}
	
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getUnit() {
		return unit;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getId() {
		return parameterid;
	}

	public void setId(String id) {
		this.parameterid = id;
	}

	public String getInstancename() {
		return instancename;
	}
	public void setInstancename(String instancename) {
		this.instancename = instancename;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@Override
	public String toString() {
		return "ModelParam [name=" + name + ", type=" + type + ", unit=" + unit + ", category=" + category + ", value=" + value + ", parameterid="
				+ parameterid + ", instancename=" + instancename + "]";
	}

	
}
