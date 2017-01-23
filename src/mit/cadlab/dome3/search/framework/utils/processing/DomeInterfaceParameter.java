package mit.cadlab.dome3.search.framework.utils.processing;

import mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes.UnitAnalyzer;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Nov 29, 2005
 * Time: 8:07:29 AM
 * To change this template use Options | File TemplateRegistry.
 */
public class DomeInterfaceParameter {
    protected String id;
    protected String dataType;
    protected String name;
    protected String unit;
    protected Double dimension;
    protected Double magnitude;

    private static String DIMENSIONLESS = "DIMENSIONLESS";

    public DomeInterfaceParameter(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        this.dimension = new Double(UnitAnalyzer.getDimensionNumber(unit).doubleValue());
    }

    public Double getDimension() {
        return dimension;
    }

    //Added by ligon to be able to set the dimension when parsing iModels
    public void setDimension(Double dim) {
        dimension = dim;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public String toString() {
        return "[Param: name=" + name + ", dimension=" + dimension + ", unit=" + unit + " , datatype=" + dataType+"]";
    }


}
