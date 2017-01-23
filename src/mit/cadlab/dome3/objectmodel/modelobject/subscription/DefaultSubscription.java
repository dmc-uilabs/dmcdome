// DefaultSubscription.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.subscription;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import org.dom4j.Element;

public class DefaultSubscription extends AbstractSubscription
{
	public static final String SUBSCRIPTION_INTERFACE = "subscriptionInterface";

	SubscriptionInterface iface;

	public DefaultSubscription(ModelObjectScope mObjScope, Id id)
	{
		super(mObjScope, id);
	}

	public DefaultSubscription(ModelObjectScope mObjScope, Id id, Subscription s)
	{
		super(mObjScope, id, s);

//		throw new UnsupportedOperationException();
	}

	public DefaultSubscription(ModelObjectScope scope, Id id, ModelInterface mi, String ifaceDeployId,
	                           int ifaceVersion, String resourceId, ServerConnection svrConn, DirectedGraph graph)
	{
		super(scope, id, mi, ifaceDeployId, ifaceVersion, resourceId, svrConn, graph);
		Id subid = getNextId();
		if(scope instanceof Model)
			iface = new SubscriptionInterface((Model)scope, subid, mi);
	}

	public DefaultSubscription(ModelObjectScope scope, Element xmlElement)
	{
		super(scope, xmlElement);
	}

/*
	//TODO to pop up GUI after loading from XML
	protected void loadXml(Element xmlElement) {
		super.loadXml(xmlElement);
		XMLUtils.makeRootElement(xmlElement);
		List subInterfaceList = xmlElement.selectNodes("/" + getXmlTag() + "/SubscriptionInterface/" + SubscriptionInterface.XML_TAG );
		Element e = (Element)subInterfaceList.get(0);
		iface = new SubscriptionInterface(e);
	}

	public Element toXmlElement() {
		Element xml = super.toXmlElement();
		Element subscriptionInterfaceElement = DocumentHelper.createElement("SubscriptionInterface");
		Element subInterface = iface.toXmlElement();
		subscriptionInterfaceElement.add(subInterface);
		xml.add(subscriptionInterfaceElement);
		return xml;
	}
*/

	public void setInterface(SubscriptionInterface iface)
	{
		if (this.iface != null)
			throw new UnsupportedOperationException(getName() + ".setInterface - interface not null");
		this.iface = iface;
		iface.setSubscription(this);
		((DomeModelRuntime)getModel()).processNewSubscriptionInterface(iface,this);
		firePropertyChange(SUBSCRIPTION_INTERFACE, null, this.iface); // used by solver
	}

	public SubscriptionInterface getInterface()
	{
		return iface;
	}

	public Parameter getInterfaceParameter(String subParamId)
	{
		if (iface == null)
			return null;
		String ifaceParamId = (String) paramIdMap.get(subParamId);
		if (ifaceParamId == null)
			return null;
		return (Parameter) iface.getModelObjectById(new Id(ifaceParamId));
	}

	/**
	 * @param subParam parameter in Subscription
	 * @return parameter in SubscriptionInterface that corresponds to specified parameter
	 */
	public Parameter getInterfaceParameter(Parameter subParam)
	{
		return getInterfaceParameter(subParam.getId().getIdString());
	}

	/**
	 * If modelObjectId belongs to SubscriptionInterface, it will be returned as well.
	 * @param modelObjectId
	 * @return object with the specified Id in either the subscription or subscription interface
	 */
	public ModelObject getModelObjectById(Id modelObjectId)
	{
		ModelObject mObj = super.getModelObjectById(modelObjectId);
		if (mObj == null && iface != null)
			return iface.getModelObjectById(modelObjectId);
		return mObj;
	}

	public void relocate(String ifaceDeployId,
	                           int ifaceVersion, ServerConnection svrConn)
	{
		this.ifaceId = ifaceDeployId;
		this.ifaceVersion = ifaceVersion;
		if (svrConn != null) {
			this.loginType = svrConn.getLoginType();
			this.loginName = svrConn.getLoginName();
			this.encryptedPwd = svrConn.getEncryptedPassword();
			this.serverPort = svrConn.getServerPort();
		}
   	}
}
