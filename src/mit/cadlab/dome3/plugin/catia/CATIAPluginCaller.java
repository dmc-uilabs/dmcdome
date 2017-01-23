/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 22, 2003
 * Time: 2:37:15 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.plugin.catia;

public class CATIAPluginCaller
{
	public static final int MODEL_INIT				= 0;	// "CATIAModel::constructor";
	public static final int MODEL_DESTROY		    = 1;	// "CATIAModel::destructor"
	public static final int MODEL_LOAD				= 2;	// "CATIAModel::loadModel";
	public static final int MODEL_UNLOAD			= 3;	// "CATIAModel::unloadModel";
	public static final int MODEL_IS_LOADED			= 4;	// "CATIAModel::isModelLoaded";
	public static final int MODEL_EXECUTE			= 5;	// "CATIAModel::execute";
	public static final int MODEL_EXEC_BF_INPUT     = 6;    // "CATIAModel::executeBeforeInput";
	public static final int MODEL_EXEC_AF_INPUT     = 7;    // "CATIAModel::executeAfterOutput";
	public static final int MODEL_CREATE_REAL		= 8;	// "CATIAModel::createReal";
	public static final int MODEL_CREATE_INT		= 9;	// "CATIAModel::createInteger";
	public static final int MODEL_CREATE_STRING		= 10;	// "CATIAModel::createString";
	public static final int MODEL_CREATE_BOOL		= 11;	// "CATIAModel::createBoolean";
	public static final int MODEL_CREATE_LENGTH		= 12;	// "CATIAModel::createLength";
	public static final int MODEL_CREATE_ANGLE		= 13;	// "CATIAModel::createAngle";
	public static final int MODEL_CREATE_MASS		= 14;	// "CATIAModel::createMass";
	public static final int MODEL_CREATE_VOLUME		= 15;	// "CATIAModel::createVolume";
	public static final int MODEL_CREATE_DENSITY	= 16;	// "CATIAModel::createDensity";
	public static final int MODEL_CREATE_AREA	    = 17;	// "CATIAModel::createArea";
	public static final int MODEL_CREATE_VECTOR     = 18;   // "CATIAModel::createVector";
	public static final int MODEL_CREATE_FILE	    = 19;	// "CATIAModel::createFile";

	public static final int	REAL_GETVALUE			= 20;	// "CATIAReal::getValue";
	public static final int REAL_SETVALUE			= 21;	// "CATIAReal::setValue";
	public static final int	INT_GETVALUE			= 22;	// "CATIAInteger::getValue";
	public static final int INT_SETVALUE			= 23;	// "CATIAInteger::setValue";
	public static final int	STRING_GETVALUE			= 24;	// "CATIAString::getValue";
	public static final int STRING_SETVALUE			= 25;	// "CATIAString::setValue";
	public static final int	BOOL_GETVALUE			= 26;	// "CATIABoolean::getValue";
	public static final int BOOL_SETVALUE			= 27;	// "CATIABoolean::setValue";
	public static final int	LENGTH_GETVALUE			= 28;	// "CATIALength::getValue";
	public static final int LENGTH_SETVALUE			= 29;	// "CATIALength::setValue";
	public static final int	ANGLE_GETVALUE			= 30;	// "CATIAAngle::getValue";
	public static final int ANGLE_SETVALUE			= 31;	// "CATIAAngle::setValue";
	public static final int	MASS_GETVALUE			= 32;	// "CATIAMass::getValue";
	public static final int MASS_SETVALUE			= 33;	// "CATIAMass:setValue";
	public static final int	VOLUME_GETVALUE			= 34;	// "CATIAVolume::getValue";
	public static final int VOLUME_SETVALUE			= 35;	// "CATIAVolume:setValue";
	public static final int	DENSITY_GETVALUE		= 36;	// "CATIADensity::getValue";
	public static final int DENSITY_SETVALUE		= 37;	// "CATIADensity::setValue";
	public static final int	AREA_GETVALUE		    = 38;	// "CATIAArea::getValue";
	public static final int AREA_SETVALUE		    = 39;	// "CATIAArea::setValue";
	public static final int	VECTOR_GETVALUE		    = 40;	// "CATIAVector::getValue";
	public static final int VECTOR_SETVALUE		    = 41;	// "CATIAVector::setValue";
	public static final int FILE_SAVE				= 42;	// "CATIAFile::save";

	public static final int MODEL_SETUP_USERLIB         = 43;	// "CATIAModel::setupUserLibrary";
	public static final int MODEL_CREATE_STRING_USERLIB	= 44;	// "CATIAModel::createStringUserLib";
	public static final int MODEL_CREATE_VECTOR_USERLIB	= 45;	// "CATIAModel::createVectorUserLib";
	public static final int MODEL_CREATE_REAL_USERLIB   = 46;	// "CATIAModel::createRealUserLib";
	public static final int MODEL_CREATE_INTEGER_USERLIB = 47;	// "CATIAModel::createIntegerUserLib";
	public static final int MODEL_CREATE_BOOLEAN_USERLIB = 48;	// "CATIAModel::createBooleanUserLib";

	public native void callVoidFunc(long ptr, int operation, Object[] args);

	public native boolean callBoolFunc(long ptr, int operation, Object[] args);

	public native int callIntFunc(long ptr, int operation, Object[] args);

	public native double callDoubleFunc(long ptr, int operation, Object[] args);

	public native String callStringFunc(long ptr, int operation, Object[] args);

	public native long callObjectFunc(long ptr, int operation, Object[] args);

	public native double[] callDoubleArrayFunc(long ptr, int operation, Object[] args);

	public native double[][] call2DimDoubleArrayFunc(long ptr, int operation, Object[] args);
}
