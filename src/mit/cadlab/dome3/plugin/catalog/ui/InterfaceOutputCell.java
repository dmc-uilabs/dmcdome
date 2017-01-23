package mit.cadlab.dome3.plugin.catalog.ui;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 24
 *
 */
public class InterfaceOutputCell extends BaseCell {
    public InterfaceOutputCell(String relAlias, String paramName, String dataType, String unit, String script, ComponentReference compRef) {
        super(BaseCell.ITF_OUTPUT, relAlias, paramName, dataType, unit, null, script, compRef);
    }

    public InterfaceBar getInterfaceBar() {
        return (InterfaceBar) getBar();
    }    
}