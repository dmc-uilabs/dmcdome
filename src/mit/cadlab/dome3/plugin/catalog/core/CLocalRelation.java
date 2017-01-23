package mit.cadlab.dome3.plugin.catalog.core;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CLocalRelation extends CRelation {

    private String relationScript;

    /* invoked by CImplementation.addLocalRelation() */
    protected CLocalRelation(String relationName, String relAlias, CImplementation parent, CNamingService namingService) {
        super(relationName, relAlias, parent, namingService);
    }

    public String getRelationScript() {
        return relationScript;
    }

    public void setRelationScript(String relationScript) {
        this.relationScript = relationScript;
    }

    /**
     * make a copy of this instance
     * since it is doing a deep copying name, namespace, driverToDrivenMap, drivenToDriverMap, inputParameterNames, and outputParameterNames except for the implementation and namingService,
     * every change to those is made to a cloned instance will not affect the source of the copy, except for those which are not deep copied.
     */
    public Object clone() {
        CLocalRelation ret = new CLocalRelation(getRelationName(), getRelAlias(), getImplementation(), getNamingService());
        CRelation.copy(this, ret);
        ret.setRelationScript(this.relationScript);
        return ret;
    }
}
