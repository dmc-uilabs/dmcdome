package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.util.xml.DomeXmlData;

import java.util.Collections;
import java.util.List;

import org.dom4j.Element;


/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 17, 2003
 * Time: 6:10:02 PM
 * To change this template use Options | File Templates.
 */
public class BrowseInterfaceDomeFile extends BrowseDomeFile
{

	private ModelInterfaceRuntimeClient _interfaceRuntime = null; //used for interfaces
    private mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient _optimizationInterfaceRuntime = null; // used for optimization tool interfaces
	private List interfaceCausalityView;
	private List systemCausalityView;
	private List buildView;
	private List currentInterfaceView;


	public BrowseInterfaceDomeFile(String id, String name, String desc, String url, String modified, int version)
	{
		super(INTERFACE_TYPE, id, name, desc, url, modified, version);
	}

	public BrowseInterfaceDomeFile(String id, String name, String desc, String url, String modified, int version, ProjectResourceInfo pri)
	{
		super(INTERFACE_TYPE, id, name, desc, url, modified, version);
		this.pri = pri;
	}


	public void listChildren(ServerConnection svrConn)
	{

		if (this.lookAtChildren) {
			addInterfaceDescription(svrConn);
		}
		this.lookAtChildren = false;
	}

	private void addInterfaceDescription(ServerConnection svrConn)
	{
		String definition = FileSystemFunctions.getInterfaceDescription(svrConn, getId().toString());
		if (!definition.equals("")) {
			loadXml(definition);
			content.addAll(getFilters());
		}
	}

	/**
	 * To transform an interfaceXml into an interface object
	 */
	public void loadXml(String xmlContent)
	{
        if (_interfaceRuntime == null && _optimizationInterfaceRuntime == null)
        {
            Element xmlElement = XMLUtils.stringToXmlElement(xmlContent);
            String interfaceType = xmlElement.attributeValue("type");
            if(interfaceType.equals(ToolInterface.TYPE_INFO.getXmlType()))
                _optimizationInterfaceRuntime = new mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient(xmlElement);
            else
                _interfaceRuntime = new ModelInterfaceRuntimeClient(xmlElement, true);
            // will not calculate system causality
        }
	}

	/**
	 * Return a list of filetrs which contain parameters
	 * @return
	 */
	public List getFilters()
	{
		if (_interfaceRuntime == null && _optimizationInterfaceRuntime == null) return Collections.EMPTY_LIST;

		if(_interfaceRuntime != null)
        {
            interfaceCausalityView = _interfaceRuntime.getView(ModelInterfaceRuntimeClient.INTERFACE_CAUSALITY_VIEW);
            systemCausalityView = _interfaceRuntime.getView(ModelInterfaceRuntimeClient.SYSTEM_CAUSALITY_VIEW);
            buildView = _interfaceRuntime.getView(ModelInterfaceRuntimeClient.BUILD_VIEW);
		}
        else
        {
            interfaceCausalityView = _optimizationInterfaceRuntime.getView(ToolInterface.INTERFACE_CAUSALITY_VIEW);
            systemCausalityView = _optimizationInterfaceRuntime.getView(ToolInterface.SYSTEM_CAUSALITY_VIEW);
            buildView = _optimizationInterfaceRuntime.getView(ToolInterface.BUILD_VIEW);
        }
		currentInterfaceView = interfaceCausalityView;

		return currentInterfaceView;
	}

	protected void switchView(ServerConnection svrConn, String view)
	{

		if (currentInterfaceView == null) {
			//create views
			listChildren(svrConn);
		}

		//those views already defined...
		boolean sv = false;
		if (view.equalsIgnoreCase("Interface") && !currentInterfaceView.equals(interfaceCausalityView)) {
			currentInterfaceView = interfaceCausalityView;
			sv = true;

			//check view menu
			//RunMenus.checkViewMenu(RunMode.INTERFACECAUSALITYVIEW);
		} else if (view.equalsIgnoreCase("System") && !currentInterfaceView.equals(systemCausalityView)) {
			currentInterfaceView = systemCausalityView;
			sv = true;

			//check view menu
			//RunMenus.checkViewMenu(RunMode.SYSTEMCAUSALITYVIEW);
		} else if (view.equalsIgnoreCase("Build") && !currentInterfaceView.equals(buildView)) {
			currentInterfaceView = buildView;
			sv = true;

			//check view menu
			//RunMenus.checkViewMenu(RunMode.BUILDVIEW);
		}
		if (sv) {
			System.out.println("BrowseInterfaceDomeFile: switchView");
			content.clear();
			content.addAll(currentInterfaceView);
		}
	}

	protected String getView()
	{
		if (currentInterfaceView.equals(interfaceCausalityView)) {
			return new String("Interface");
		} else if (currentInterfaceView.equals(systemCausalityView)) {
			return new String("System");
		} else if (currentInterfaceView.equals(buildView)) {
			return new String("Build");
		}
		return new String("");
	}

}
