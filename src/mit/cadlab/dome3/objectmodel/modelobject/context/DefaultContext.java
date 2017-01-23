// DefaultContext.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.context;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DListListener;
import org.dom4j.Element;

import java.util.Collections;
import java.util.List;

public class DefaultContext extends AbstractContext implements ViewSupport
{
	public DefaultContext(ModelObjectScope scope, Id id)
	{
		super(scope, id);
	}

	public DefaultContext(ModelObjectScope scope, Id id, Context context)
	{
		super(scope, id, context);
	}

	public DefaultContext(ModelObjectScope scope, Element xmlElement)
	{
		this(scope, xmlElement, true);
	}

	public DefaultContext(ModelObjectScope scope, Element xmlElement, boolean loadReferences)
	{
		super(scope, xmlElement, loadReferences);
	}

	//ViewSuppport interface
	public List getView()
	{
		return Collections.unmodifiableList(modelObjectReferences);
	}

	public void addViewListener(DListListener l)
	{
		modelObjectReferences.addDListListener(l);
	}

	public void removeViewListener(DListListener l)
	{
		modelObjectReferences.removeDListListener(l);
	}

}
