package mit.cadlab.dome3.plugin.catalog.ui;



/**
 * User: Sangmok Han
 * Date: 2006. 1. 19
 *
 */
public class RelationInputCell extends BaseCell {
    public RelationInputCell(String relAlias, String paramName, String dataType, String unit, String script, ComponentReference compRef) {
        super(BaseCell.REL_INPUT, relAlias, paramName, dataType, unit, null, script, compRef);
    }

    public RelationBar getRelationBar() {
        return (RelationBar) getBar();
    }
}