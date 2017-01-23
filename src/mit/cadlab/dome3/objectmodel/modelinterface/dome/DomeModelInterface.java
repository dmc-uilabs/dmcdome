/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Nov 25, 2002
 * Time: 3:24:09 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.objectmodel.modelinterface.dome;

import mit.cadlab.dome3.objectmodel.MultiViewSupport;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.InterfaceModelView;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface DomeModelInterface extends ModelInterface, MultiViewSupport, ViewSupport
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("Dome Model Interface", "DomeModelInterface");
	public static final String SYSTEM_CAUSALITY_VIEW = "System Causality View";
	public static final String INTERFACE_CAUSALITY_VIEW = "Interface Causality View";
	public static final String BUILD_VIEW = "Build View";
	public static final String MODEL_VIEW = "Model View";
	public static final List viewNames = Collections.unmodifiableList
	        (Arrays.asList(new String[]{BUILD_VIEW, INTERFACE_CAUSALITY_VIEW, SYSTEM_CAUSALITY_VIEW,
	                                    MODEL_VIEW}));

	public static final Id BUILD_CONTEXT_ID = new Id("IFACE_BUILD_CXT");
	public static final Id MODEL_CONTEXT_ID = new Id("IFACE_MODEL_CXT");

	public static final String VIEWS = "views";
	public static final String VIEW = "view";
	public static final String INPUTS = "Inputs";
	public static final String OUTPUTS = "Outputs";
	public static final String INDETERMINATES = "Indeterminates";

	public Context getBuildContext();

	public Context getModelContext();

	public boolean isDefaultInterface();

	public String getCurrentView();

	public void updateSavedStatus();

	public ModelObject getTempModelObjectById(Id id);

	public InterfaceModelView getModelView();
}