package mit.cadlab.dome3.plugin.catalog.ui;



/**
 * User: Sangmok Han
 * Date: 2006. 1. 19
 */
public class RelationOutputCell extends BaseCell {
    public RelationOutputCell(String relAlias, String paramName, String dataType, String unit, ComponentReference compRef) {
        super(BaseCell.REL_OUTPUT, relAlias, paramName, dataType, unit, null, null, compRef);
    }

    public RelationBar getRelationBar() {
        return (RelationBar) getBar();
    }
}
