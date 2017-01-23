// ProjectResourceTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.subscription;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceBuildPanel;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.AbstractSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;

import javax.swing.*;

public class SubscriptionTreeObject extends DomeTreeObject
{
   	public SubscriptionTreeObject(Subscription subscr)
	{
		super(subscr);
		((ViewSupport) subscr).addViewListener(new TreeObjectDListListener());
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.SUBSCRIBE_INTERFACE);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.SUBSCRIBE_INTERFACE_OPEN);
	}

	protected void makeGui()
	{
		if(data instanceof DefaultSubscription) {
			ModelInterface iface = ((DefaultSubscription)data).getInterface();
			gui = new DomeBuildFrame(new ModelInterfaceBuildPanel(iface));
		}
	}

}
