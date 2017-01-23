package mit.cadlab.dome3.plugin.catia.dataobject;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Oct 3, 2003
 * Time: 2:34:26 PM
 * To change this template use Options | File Templates.
 */
public interface CATIADataObjects
{
    public static final int CLASS_MODEL   = 0;   //"CATIAModel";
    public static final int CLASS_REAL    = 1;   //"CATIAReal";
	public static final int CLASS_INT     = 2;   //"CATIAInteger";
	public static final int CLASS_STRING  = 3;   //"CATIAString";
	public static final int CLASS_BOOL    = 4;   //"CATIABoolean";
	public static final int CLASS_LENGTH  = 5;   //"CATIAReal";
	public static final int CLASS_ANGLE   = 6;   //"CATIAReal";
	public static final int CLASS_MASS    = 7;   //"CATIAReal";
	public static final int CLASS_VOLUME  = 8;   //"CATIAReal";
	public static final int CLASS_DENSITY = 9;   //"CATIAReal";
	public static final int CLASS_AREA    = 10;  //"CATIAReal";
	public static final int CLASS_VECTOR  = 11;  //"CATIAVector";
	public static final int CLASS_FILE    = 12;  //"CATIAFile";
}
