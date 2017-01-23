package mit.cadlab.dome3.plugin.catalog.ui;



/**
 * User: Sangmok Han
 * Date: 2006. 1. 25
 */
public class InterfaceInputCell extends BaseCell {
    public InterfaceInputCell(String relAlias, String paramName, String dataType, String unit, ComponentReference compRef) {
        super(BaseCell.ITF_INPUT, relAlias, paramName, dataType, unit, null, null, compRef);
    }

    public InterfaceBar getInterfaceBar() {
        return (InterfaceBar) getBar();
    }
}
