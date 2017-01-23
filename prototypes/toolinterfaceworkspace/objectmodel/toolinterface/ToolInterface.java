package toolinterfaceworkspace.objectmodel.toolinterface;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.MultiViewSupport;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.CausalitySupport;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.log.LogHandler;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 8:22:07 AM
 * To change this template use Options | File Templates.
 */
public interface ToolInterface extends ModelObjectScope, MultiViewSupport, CausalitySupport
{
    public static final TypeInfo TYPE_INFO = new TypeInfo("tool interface","toolinterface");
    public static final String TOOL_INTERFACE_TAG = "tool interface";
	public static final String XML_TAG = "toolinterface";
    public static final String BUILD_VIEW = "tool build view";
    public static final String RUN_VIEW = "tool run view";
    public static final String CAUSALITY_VIEW = "tool causality view";
    public static final String VIEWS = "views";

    public static final List viewNames = Collections.unmodifiableList
	        (Arrays.asList(new String[]{BUILD_VIEW, RUN_VIEW, CAUSALITY_VIEW }));

    public static final Id BUILD_CONTEXT_ID = new Id("IFACE_BUILD_CXT");
    public static final Id TOOL_CONTEXT_ID = new Id("TOOL_CONTEXT_ID");

//    public Version getModelVersion();

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
//	public LogHandler getLogHandler();

//	public void setLogHandler(LogHandler log);

}
