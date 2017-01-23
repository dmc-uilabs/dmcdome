package mit.cadlab.dome3.objectmodel.toolinterface;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.MultiViewSupport;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.CausalitySupport;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.log.LogHandler;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 8:22:07 AM
 * To change this template use Options | File Templates.
 */
public interface ToolInterface extends ModelObjectScope, MultiViewSupport, ViewSupport, CausalitySupport
{
    public static final TypeInfo TYPE_INFO = new TypeInfo("Tool Interface","ToolInterface");

    public static final String XML_TAG = "modelinterface";

    public static final String BUILD_VIEW = "Build View";
    public static final String INTERFACE_CAUSALITY_VIEW = "Interface Causality View";
    public static final String SYSTEM_CAUSALITY_VIEW = "System Causality View";

    public static final String VIEWS = "views";
    public static final String VIEW = "view";

    public static final String RESULTS_DISPLAYED = "results displayed";
    public static final String RESULTS_CLOSED = "results closed";

    public static final String DESIGN_SPACE_DISPLAYED = "design space displayed";
    public static final String DESIGN_SPACE_CLOSED = "design space closed";

    public static final List viewNames = Collections.unmodifiableList
	        (Arrays.asList(new String[]{BUILD_VIEW, INTERFACE_CAUSALITY_VIEW}));

    public static final Id BUILD_CONTEXT_ID = new Id("IFACE_BUILD_CXT");

	/**
	 * Returns version of toolinterface.
	 */
	public Version getVersion();

	/**
	 * Validate the interface with tool model.
	 */
	public void validate();

	public boolean isValidated();

	/**
	 * Returns whether or not this version of the interface has been saved.
	 */
	public boolean isSaved();

    public Context getBuildContext();

	/**
	 * Returns MessageLog for ModelInterface. Never null.
	 */
	public LogHandler getLogHandler();

	public void setLogHandler(LogHandler log);

    public ArrayList getCustomGUIList();

	public void addCustomGui(CustomGuiInfo info);

	public void removeCustomGui(CustomGuiInfo info);

}
