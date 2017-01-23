// DomeIcons.java
package mit.cadlab.dome3.icons;

import mit.cadlab.dome3.swing.Templates;

import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 * Cache of Icons used in Dome.
 */
public class DomeIcons {

	public static final String USER = "mit/cadlab/dome3/icons/user.gif";
	public static final String GROUP = "mit/cadlab/dome3/icons/group.gif";
	public static final String USER_SMALL = "mit/cadlab/dome3/icons/userSmall.gif";
	public static final String GROUP_SMALL = "mit/cadlab/dome3/icons/groupSmall.gif";
	public static final String ACTIVE_USER_GROUP = "mit/cadlab/dome3/icons/active.gif";
	public static final String INACTIVE_USER_GROUP = "mit/cadlab/dome3/icons/inactive.gif";

	public static final String TREE_COLLAPSED = "mit/cadlab/dome3/icons/tree/treeCollapsed.gif";
	public static final String TREE_EXPANDED = "mit/cadlab/dome3/icons/tree/treeExpanded.gif";

    public static final String CONTEXT = "mit/cadlab/dome3/icons/tree/context.gif";
    public static final String CONTEXT_OPEN = "mit/cadlab/dome3/icons/tree/contextOpen.gif";
    public static final String FILTER = "mit/cadlab/dome3/icons/tree/filter.gif";
    public static final String FILTER_OPEN = "mit/cadlab/dome3/icons/tree/filterOpen.gif";
    public static final String INTERFACE = "mit/cadlab/dome3/icons/tree/interface.gif";
    public static final String INTERFACE_OPEN = "mit/cadlab/dome3/icons/tree/interfaceOpen.gif";
	public static final String SUBSCRIBE_INTERFACE = "mit/cadlab/dome3/icons/tree/interfaceSubscribed.gif";
    public static final String SUBSCRIBE_INTERFACE_OPEN = "mit/cadlab/dome3/icons/tree/interfaceSubscribedOpen.gif";
    public static final String MODEL = "mit/cadlab/dome3/icons/tree/model.gif";
    public static final String MODEL_OPEN = "mit/cadlab/dome3/icons/tree/modelOpen.gif";
    public static final String PARAMETER = "mit/cadlab/dome3/icons/tree/parameter.gif";
    public static final String PARAMETER_OPEN = "mit/cadlab/dome3/icons/tree/parameterOpen.gif";
	public static final String PROJECT = "mit/cadlab/dome3/icons/tree/iProject.gif";
	public static final String PROJECT_OPEN = "mit/cadlab/dome3/icons/tree/iProjectOpen.gif";
    public static final String RELATION_PARAMETER = "mit/cadlab/dome3/icons/tree/parameterRelation.gif";
    public static final String RELATION_PARAMETER_OPEN = "mit/cadlab/dome3/icons/tree/parameterRelationOpen.gif";
    public static final String INTERFACE_PARAMETER = "mit/cadlab/dome3/icons/tree/parameterInterface.gif";
    public static final String INTERFACE_PARAMETER_OPEN = "mit/cadlab/dome3/icons/tree/parameterInterfaceOpen.gif";
	public static final String SUBSCRIBE_PARAMETER = "mit/cadlab/dome3/icons/tree/parameterSubscribe.gif";
	public static final String SUBSCRIBE_PARAMETER_OPEN = "mit/cadlab/dome3/icons/tree/parameterSubscribeOpen.gif";
    public static final String PLAYSPACE = "mit/cadlab/dome3/icons/tree/playspace.gif";
    public static final String PLAYSPACE_OPEN = "mit/cadlab/dome3/icons/tree/playspaceOpen.gif";
    public static final String RELATION = "mit/cadlab/dome3/icons/tree/relation.gif";
    public static final String RELATION_OPEN = "mit/cadlab/dome3/icons/tree/relationOpen.gif";
	public static final String RELATIONEQUAL = "mit/cadlab/dome3/icons/tree/relationEquals.gif";
	public static final String RELATIONEQUAL_OPEN = "mit/cadlab/dome3/icons/tree/relationEqualsOpen.gif";
    public static final String RELATIONDO = "mit/cadlab/dome3/icons/tree/relationDo.gif";
	public static final String RELATIONDO_OPEN = "mit/cadlab/dome3/icons/tree/relationDoOpen.gif";
    public static final String RELATIONWHILE = "mit/cadlab/dome3/icons/tree/relationWhile.gif";
	public static final String RELATIONWHILE_OPEN = "mit/cadlab/dome3/icons/tree/relationWhileOpen.gif";
    public static final String RELATIONTIME = "mit/cadlab/dome3/icons/tree/relationTime.gif";
	public static final String RELATIONTIME_OPEN = "mit/cadlab/dome3/icons/tree/relationTimeOpen.gif";
    public static final String TOOL = "mit/cadlab/dome3/icons/tree/tool.gif";
    public static final String TOOL_OPEN = "mit/cadlab/dome3/icons/tree/toolOpen.gif";


    public static final String ANALYSIS_TOOL = "mit/cadlab/dome3/icons/tree/tool.gif";
    public static final String ANALYSIS_TOOL_OPEN = "mit/cadlab/dome3/icons/tree/toolOpen.gif";


    public static final String EQUALS = "mit/cadlab/dome3/icons/tree/relationEquals.gif";
    public static final String EQUALS_OPEN = "mit/cadlab/dome3/icons/tree/relationEqualsOpen.gif";
    public static final String TWIN = "mit/cadlab/dome3/icons/tree/relationTwin.gif";
    public static final String TWIN_OPEN = "mit/cadlab/dome3/icons/tree/relationTwinOpen.gif";
    public static final String VISUALIZATION = "mit/cadlab/dome3/icons/tree/visualization.gif";
    public static final String VISUALIZATION_OPEN = "mit/cadlab/dome3/icons/tree/visualizationOpen.gif";
	public static final String CHECKED = "mit/cadlab/dome3/icons/checked.gif";
	public static final String UNCHECKED = "mit/cadlab/dome3/icons/unChecked.gif";

	public static final String WINDOW = "mit/cadlab/dome3/icons/domeWindow.gif";
	public static final String PROPERTY = "mit/cadlab/dome3/icons/property.gif";


    private static HashMap icons = new HashMap();

    public static ImageIcon getIcon(String location) {
        ImageIcon icon = (ImageIcon) icons.get(location);
        if (icon == null) {
            if (location == null || location.trim().equals("")) return null;
            icon = Templates.makeImageIcon(location);
            icons.put(location, icon);
        }
        return icon;
    }

}

