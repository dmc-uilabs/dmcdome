package mit.cadlab.dome3.plugin.catalog.core;

import java.util.Set;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CDerivedParameter extends CParameter {
    private String newUnit;
    private String newDataType;
    private String newValue;
    private String transformScript;
    private String relOutputParamName;
    private CNamingService namingService;

    /**
     * only invoked by CRelationOutputParameter.addDerivedParameter()
     * also invoked by clone in this class
     *
     * @param name
     * @param relOutputParamName qualified name of relation output parameter
     * @param namingService
     */
    protected CDerivedParameter(String name, String relOutputParamName, CNamingService namingService) {
        super(relOutputParamName, name);
        this.relOutputParamName = relOutputParamName;
        this.namingService = namingService;
    }

    public Set getDriversOf(boolean findOnlyInParentRelation) {
        CRelationOutputParameter roParam = namingService.getRelationOutputParameter(relOutputParamName);
        Set ret = roParam.getDriversOf(findOnlyInParentRelation);
        ret.add(roParam.getQualifiedName());
        return ret;
    }

    public Set getDrivensOf(boolean findOnlyInParentRelation) {
        CRelationOutputParameter roParam = namingService.getRelationOutputParameter(relOutputParamName);
        Set ret = roParam.getDrivensBy(findOnlyInParentRelation);
        return ret;
    }

    public String getNewUnit() {
        return newUnit;
    }

    public void setNewUnit(String newUnit) {
        this.newUnit = newUnit;
    }

    public String getNewDataType() {
        return newDataType;
    }

    public void setNewDataType(String newDataType) {
        this.newDataType = newDataType;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getTransformScript() {
        return transformScript;
    }

    public void setTransformScript(String transformScript) {
        this.transformScript = transformScript;
    }

    public String toString() {
        return "[derived param:name=" + getQualifiedName() + "]=" + super.toString();
    }

    public Object clone() {
        CDerivedParameter ret = new CDerivedParameter(getName(), relOutputParamName, namingService);
        CParameter.copy(this, ret);
        ret.setNewUnit(newUnit);
        ret.setNewDataType(newDataType);
        ret.setNewValue(newValue);
        ret.setTransformScript(transformScript);
        return ret;
    }
    
    public CNamingService getNamingService() {
        return namingService;
    }
}
