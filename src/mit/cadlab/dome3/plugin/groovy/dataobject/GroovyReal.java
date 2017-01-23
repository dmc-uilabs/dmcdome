package mit.cadlab.dome3.plugin.groovy.dataobject;

import mit.cadlab.dome3.plugin.excel.ExcelPluginCaller;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.network.server.Debug;
import edu.iupui.rg.ucum.units.Unit;

/**
 * plugin data object class that corresponds to dome real
 * the biggest difference from other plugin data types like MatlabReal is that
 * there is no JNI native object. we have just one java double member variable
 * that replaces the JNI native object and keeps the native-side value.
 *
 * User: Sangmok Han
 * Date: Aug 15, 2005
 */
public class GroovyReal extends GroovyDataObject {

    private boolean isResult;
    private DomeReal javaData; // reference to dome data object
    private RealData nativeData; // a data object instance for a catalog parameter

    /** create GroovyReal instance that has the same value and unit of the given data */
    public GroovyReal(RealData data) {
        this.nativeData = new RealData(data);
    }

    /** create GroovyReal instance corresponding to DomeReal parameter */
    public GroovyReal(Parameter realParam) {
        this.parameter = realParam;
        if (parameter == null) {
            javaData = new RealData();
        } else {
            javaData = (DomeReal) parameter.getCurrentDataObject();
        }
	    /* initialize native data */
	    nativeData = new RealData(javaData);
        isResult = false;
    }

    public void destroy() {
        // empty for all catalog data types
    }

    public DataObject getData() {
        return nativeData;
    }

    public boolean getIsResult() {
        return isResult;
    }

    public void setIsResult(boolean isResult) {
        this.isResult = isResult;
    }

    public DomeReal getJavaData() {
        return javaData;
    }

    public DomeReal getNativeData() {
        return nativeData;
    }

    public void loadNativeData() {
        nativeData.setQuantity(javaData.getQuantity());
    }

    public void loadJavaData() {
        javaData.setQuantity(nativeData.getQuantity());
    }

    public void finalize() {
        destroy();
    }

    public void resetObjectPointer() {
        // empty for all catalog data types
    }

    public String toString() {
        return ("GroovyReal: value = " + nativeData.getValue() + ", unit = " + nativeData.getUnit());
    }

    public Object plus(Object obj) {
        if (obj instanceof GroovyDataObject) { obj = ((GroovyDataObject) obj).getData(); }
        return new GroovyReal((RealData) nativeData.__add__(obj));
    }

    public Object minus(Object obj) {
        if (obj instanceof GroovyDataObject) {  obj = ((GroovyDataObject) obj).getData(); }
        return new GroovyReal((RealData) nativeData.__sub__(obj));
    }

    public Object multiply(Object obj) {
        if (obj instanceof GroovyDataObject) { obj = ((GroovyDataObject) obj).getData(); }
        return new GroovyReal((RealData) nativeData.__mul__(obj));
    }

    public Object div(Object obj) {
        if (obj instanceof GroovyDataObject) { obj = ((GroovyDataObject) obj).getData(); }
        return new GroovyReal((RealData) nativeData.__div__(obj));
    }

    public Object power(Object obj) {
        if (obj instanceof GroovyDataObject) { obj = ((GroovyDataObject) obj).getData(); }
        return new GroovyReal((RealData) nativeData.__pow__(obj));
    }

    public boolean equals(Object obj) {
        if (obj instanceof GroovyDataObject) { obj = ((GroovyDataObject) obj).getData(); }
        return ((BooleanData) nativeData.__eq__(obj)).getBooleanValue().booleanValue();
    }

    public double compareTo(Object obj) {
        if (obj instanceof GroovyDataObject) { obj = ((GroovyDataObject) obj).getData(); }
        RealData result = (RealData) nativeData.__sub__(obj);
        return result.getValue();
    }

    public Object leftShift(Object obj) {
        if (obj instanceof GroovyReal) {

            Unit origNativeUnit = nativeData.getUnit();
            DomeReal objData = ((GroovyReal) obj).getNativeData();
            if (! origNativeUnit.equivalent(objData.getUnit())) {
                System.out.println("origNativeUnit: " + origNativeUnit.getName());
                System.out.println("objUnit: " + objData.getUnit().getName());
            }

            if (CConstant.NO_UNIT_STR.equals(objData.getUnit().getName())) {
                System.out.println("because obj data has no unit. just assign its value to native data");
                double tempConvertedValue = origNativeUnit.convertFrom(objData.getValue(), nativeData.getUnit());
                System.out.println("tempConvertedValue: " + tempConvertedValue);
            }

            if (CConstant.NO_UNIT_STR.equals(nativeData.getUnit().getName())) {
                System.out.println("because native data has no unit. just take the value of obj data as the value of native data");
                double tempConvertedValue = origNativeUnit.convertFrom(objData.getValue(), nativeData.getUnit());
                System.out.println("tempConvertedValue: " + tempConvertedValue);
            }
            double convertedValue = origNativeUnit.convertFrom(objData.getValue(), objData.getUnit());
            nativeData.setValue(convertedValue);

            System.out.println("<--- assign start");
            System.out.println("origin unit: " + origNativeUnit);
            System.out.println("obj value: " + objData.getValue());
            System.out.println("obj unit: " + objData.getUnit());
            System.out.println("converted value: " + convertedValue);
            System.out.println("assign end --->");


//            Unit origNativeUnit = nativeData.getUnit();
//            System.out.println("ORG1 " + origNativeUnit);
//            System.out.println("DTA1 " + nativeData);
//            nativeData.setQuantity(((GroovyReal) obj).getNativeData().getQuantity());
//            Unit newNativeUnit = nativeData.getUnit();
//            System.out.println("NEW  " + newNativeUnit);
//            System.out.println("ORG2 " + origNativeUnit);
//            System.out.println("DTA2 " + nativeData);
//
//            /* convert value back to original unit from new unit */
//            double convertedValue = origNativeUnit.convertFrom(nativeData.getValue(), newNativeUnit);
//
//            nativeData.setUnit(origNativeUnit);
//            nativeData.setValue(convertedValue);
//            System.out.println("DTA3 " + nativeData);
        } else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
            nativeData.setValue(that.intValue());
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
            nativeData.setValue(that.doubleValue());
		} else {
			throw new IllegalArgumentException("Can't assign(<<) " + obj + " to " + this);
		}
        return this;
    }

    public static void main(String[] args) {
        DomeInit.initializeDOME();

        GroovyReal a = new GroovyReal(new RealData(500, "m"));
        GroovyReal b = new GroovyReal(new RealData(2, "km"));
        GroovyReal c = (GroovyReal) a.plus(b);
        GroovyReal d = new GroovyReal(new RealData(0, "cm"));
        GroovyReal e = new GroovyReal(new RealData(100));

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        d.leftShift(c);
        System.out.println(d);
        System.out.println(d.multiply(new Double("4")));
        System.out.println(d.div(new Double("5")));
        System.out.println(e.div(new Double("10")));
    }
}
