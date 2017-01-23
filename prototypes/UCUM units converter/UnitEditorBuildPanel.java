/*
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Sep 25, 2002
 * Time: 1:06:05 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */


import mit.cadlab.dome.swing.DTable;

public class UnitEditorBuildPanel extends UnitEditorBasePanel{

    public UnitEditorBuildPanel(){
        this(null);
    }

    public UnitEditorBuildPanel(String[] keywords){
        this(null,keywords);
    }

    public UnitEditorBuildPanel(DTable table, String[] keywords){
        super(table, keywords);
    }

}
