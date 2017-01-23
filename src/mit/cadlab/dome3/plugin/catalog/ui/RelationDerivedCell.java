package mit.cadlab.dome3.plugin.catalog.ui;



/**
 * User: Sangmok Han
 * Date: 2006. 1. 23
 *
 * BaseCell size : 100x110
 */
public class RelationDerivedCell extends BaseCell {
    public RelationDerivedCell(String relAlias, String paramName, String newDataType, String newUnit, String srcParamName, ComponentReference compRef) {
        super(BaseCell.REL_DERIVED, relAlias, paramName, newDataType, newUnit, srcParamName, null, compRef);
    }

    public String getNewDataType() {
        return dataType;
    }

    public String getNewUnit() {
        return unit;
    }

    public RelationBar getRelationBar() {
        return (RelationBar) getBar();
    }
}