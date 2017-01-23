package mit.cadlab.dome3.plugin.catalog.core;

import edu.iupui.rg.ucum.units.Unit;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 13.
 */
public class CConstant {
    final public static String REAL_DATA_TYPE = "Real";
    final public static String INTEGER_DATA_TYPE = "Integer";
    final public static String VECTOR_DATA_TYPE = "Vector";
    final public static String MATRIX_DATA_TYPE = "Matrix";
    final public static String STRING_DATA_TYPE = "String";
    //final public static String TEXT_DATA_TYPE = "Text";
    final public static String ENUM_DATA_TYPE = "Enumeration";
    final public static String BOOLEAN_DATA_TYPE = "Boolean";
    final public static String FILE_DATA_TYPE = "File";

    final public static String ITF_ALIAS = "itf";
    final public static String REL_ALIAS_PREFIX = "";

    public static final String IMPL_SWITCH = "implementation switch";

    final public static String NO_UNIT_STR = "No_Unit";
    final public static Unit NO_UNIT = new Unit(NO_UNIT_STR);
    final public static String PA_UNIT = "Pa";
    final public static String VOLT_UNIT = "V";
    final public static String WATT_UNIT = "W";
    final public static String LITER_UNIT = "l";
    final public static String KELVIN_UNIT = "K";
    final public static String KG_UNIT = "kg";
    final public static String NEWTON_UNIT = "N";
    final public static String SECOND_UNIT = "s";
    final public static String MOL_UNIT = "mol";
    final public static String METER_UNIT = "m";
    final public static String MILLIMETER_UNIT = "mm";
    final public static String KILOMETER_UNIT = "km";

    public static final int WHITE_STATUS = 0;
    public static final int GREEN_STATUS = 1;
    public static final int RED_STATUS = 2;
    public static final int YELLOW_STATUS = 3;
    public static final int UNASSIGNED_STATUS = 9;


    public static final int RELATION_READY_STATUS = 0;
    public static final int RELATION_RUNNING_STATUS = 1;
    public static final int RELATION_FINISHED_STATUS = 2;

    public static final int EVALUATION_READY_STATUS = 0;
    public static final int EVALUATION_RUNNING_STATUS = 1;
    public static final int EVALUATION_FINISHED_STATUS = 2;

    public static String getStatusName(int status) {
        if (status == GREEN_STATUS) return "green";
        else if (status == WHITE_STATUS) return "white";
        else if (status == RED_STATUS) return "red";
        return "unassigned";
    }
}
