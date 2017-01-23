package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;
import mit.cadlab.dome3.util.units.Quantity;

/**
 * User: Sangmok Han
 * Date: 2006. 3. 29.
 *
 * catalog data objects that are associated with interface input/output parameters are
 * different from other catalog data objects in that they interface with DOME plugin parameter
 * at the beginning of the model evaluataion, a catalog data object associated with interface input parameter
 * is initialized with the data object of DOME plugin parameter, which contains user input;
 * a catalog data object associated with interface output parameter is copied to DOME plugin parameter,
 * whose changes are propagated through messaging systems, and finally the result value is displayed to user
 * When a CDataObject is created, some are constructed with DOME plugin parameter as an argument, and others are contructed without it.
 * Those constructed with a DOME plugin parameter are mapped to either interface input param or interface output param.
 * isAssociatedWithInterfaceInputParameter() and isAssociatedWithInterfaceOutputParameter() are used to check how a catalog data object is created.
 * getDomePluginParameter() is used to access the DOME plugin parameter used when this catalog data object is created.
 * copyFromDomePluginDataObjectToCatalogDataObject() is applicable to interface input parameters in order to copy from DOME plugin param's data object to catalog data object.
 * copyFromCatalogDataObjectToDomePluginDataObject() is applicable to interface output parameters in order to copy from catalog data object to DOME plugin param's data object.
 */
public abstract class CDataObject {
    protected Parameter domePluginParam;
    private boolean isAssociatedWithItfParam; // true for itf input and output param, false for other params
    private boolean isInput; // true for itf input param, false for itf output param
    private Object value;
    private CUnit unit;

    /**
     * boolean value of isItfParamInput is determined at CatalogModelRuntime.createCatalogDataObject()
     * because the causality information is available at that point */
    public CDataObject(Parameter domePluginParam) {
        this.domePluginParam = domePluginParam;
        this.isAssociatedWithItfParam = true;
        if (domePluginParam != null) {
            this.unit = new CUnit(domePluginParam.getCurrentDataObject().getUnit());
        }
    }

    /** create catalog data object that is not associated with interface parameter, a relation parameter */
    public CDataObject() {
        this.isAssociatedWithItfParam = false;
        this.unit = CUnit.NO_UNIT;
    }

    /** instead of having getData():DataObject and DataObject used to getUnit() and getValue(), CDataObject have getUnit() and getValue() */
    /* Double for CReal, Integer for CInteger, List for CVector, List of List for CMatrix, List of Array {String name, Object value}> for CEnumeration, String for CString, File for CFile */
    public Object getValue() {
        return value;
    }

    public CUnit getUnit() {
        return unit;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setUnit(CUnit unit) {
        this.unit = unit;
    }

    public void setUnit(String unitStr) {
        this.unit = new CUnit(unitStr);
    }

    public void setUnit(Unit unit) {
        this.unit = new CUnit(unit);
    }

    public boolean hasNoUnit() {
        return Quantity.NO_UNIT.equals(unit.getUnit());
    }

    /**
     * applicable when this catalog data object is associated with interface parameter
     * returns true if this catalog data object is associated with interface input param
     * equivalent to previous getIsResult()
     */
    public void setIsInput(boolean isInput) {
        if (! isAssociatedWithItfParam) {
            throw new RuntimeException("cannot set input/output of this catalog data object. it should have been mapped to either interface input parameter or interface output parameter.");
        }
        this.isInput = isInput;
    }

    public boolean isAssociatedWithInterfaceInputParameter() {
        return isAssociatedWithItfParam && isInput;
    }
    public boolean isAssociatedWithInterfaceOutputParameter() {
        return isAssociatedWithItfParam && (! isInput);
    }

    ////////////////////////////////////
    // all methods below this comment are only applicable when either isAssociatedWithInterfaceInputParameter() or isAssociatedWithInterfaceOutputParameter() are true.
    ////////////////////////////////////

    /**
     * returns dome plugin parameter with which this catalog data object is constructed
     * previously getData(). applicable when either isAssociatedWithInterfaceInputParameter() or isAssociatedWithInterfaceOutputParameter() is true.
     * Parameter is instanceof RealData(or DomeReal), IntegerData(or DomeInteger), DomeMatrixData, BooleanData, DomeVectorData, EnumerationData, StringData, TextData, FileData
     */
	public Parameter getDomePluginParameter() {
        if (! isAssociatedWithItfParam) {
            throw new RuntimeException("this catalog data object is not constructed with DOME plugin parameter. it should have been mapped to either interface input parameter or interface output parameter.");
        }
		return domePluginParam;
	}

    /** returns current data object of this DOME plugin parameter */
    public DataObject getDomePluginDataObject() {
        if (! isAssociatedWithItfParam) {
            throw new RuntimeException("this catalog data object is not constructed with DOME plugin parameter. it should have been mapped to either interface input parameter or interface output parameter.");
        }
		return domePluginParam.getCurrentDataObject();
    }

    /** previously loadNativeData(). applicable when isAssociatedWithInterfaceInputParameter() is true */
    abstract public void copyFromDomePluginDataObjectToCatalogDataObject();

    /** previously loadJavaData() applicable when isAssociatedWithInterfaceOutputParameter() is true */
    abstract public void copyFromCatalogDataObjectToDomePluginDataObject();

    abstract public Object leftShift(Object obj);

    /**
     * do unit conversion between from-value & from-unit and to-unit
     * @param fromValue from-value
     * @param fromCUnit from-unit
     * @param toCUnit to-unit
     * @return to-value
     */
    public static double convertUnit(double fromValue, CUnit fromCUnit, CUnit toCUnit) {
        /* if any of two unit is no_unit, no coversion is needed */
        if (CConstant.NO_UNIT_STR.equals(fromCUnit.getUnit().getName()) || CConstant.NO_UNIT_STR.equals(toCUnit.getUnit().getName())) {
            return fromValue;
        }
        Unit fromUnit = fromCUnit.getUnit();
        Unit toUnit = toCUnit.getUnit();
        if (fromUnit.equivalent(toUnit)) {
            return toUnit.convertFrom(fromValue, fromUnit); // toValue
        } else {
            throw new IllegalArgumentException("Unit conversion exception: fail to convert from " + fromCUnit.getDescription() + " to " + toCUnit.getDescription());
            //return fromValue;
        }
    }

    /** we don't want to lose unit information as a result of any operation. this method prevent such a case. */
    public void checkUnitConversionRule(Object obj) throws IllegalArgumentException {
        if (obj instanceof CDataObject) {
            CDataObject dataObj = (CDataObject) obj;
            if (this.hasNoUnit() && ! dataObj.hasNoUnit()) {
                throw new IllegalArgumentException("Error in LHS.plus(RHS): LHS has no unit, but RHS has unit.");
            }
        }
    }

    /** determine unit after plus or minus of two CDataObject */
    public CUnit getResultantUnitAfterPlusMinus(CDataObject dataObj) throws IllegalArgumentException {
        if (this.hasNoUnit() && ! dataObj.hasNoUnit()) {
            return dataObj.getUnit().cloneUnit();
        } else {
            return this.getUnit().cloneUnit();
        }
    }

    /** determine unit after multiplying two CDataObject */
    public CUnit getResultantUnitAfterMutiply(CDataObject dataObj) throws IllegalArgumentException {
        if (this.hasNoUnit() && ! dataObj.hasNoUnit()) {
            return dataObj.getUnit().cloneUnit();
        } else if (dataObj.hasNoUnit() && ! this.hasNoUnit()) {
            return this.getUnit().cloneUnit();
        } else if (dataObj.hasNoUnit() && this.hasNoUnit()) {
            return this.getUnit().cloneUnit();
        } else {
            CUnit ret = this.getUnit().cloneUnit();
            ret.getUnit().mul(dataObj.getUnit().getUnit());
            return ret;
        }
    }
}
