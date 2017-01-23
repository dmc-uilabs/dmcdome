// Subscription.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.subscription;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filterable;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.causality.CausalitySupport;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.util.DListListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public interface Subscription extends ModelObject, ModelObjectScope, CausalitySupport, Filterable
{

	public static final String XML_TAG = "subscription";
	public static final TypeInfo TYPE_INFO = new TypeInfo("Subscription", "Subscription");
	public static final Id BUILD_CONTEXT_ID = new Id("SUBSCRIPTION_BUILD_CXT");
	public static final String BUILD_VIEW = "Build View";
	public static final String INPUT_OUTPUT_VIEW = "Subscription Causality View";
	public static final String MODEL_CAUSALITY_VIEW = "Model Causality View";
	public static final List viewNames = Collections.unmodifiableList
	        (Arrays.asList(new String[]{INPUT_OUTPUT_VIEW, MODEL_CAUSALITY_VIEW}));

	// Internal Causality Filter names
	public static final String INPUTS = "Inputs";
	public static final String OUTPUTS = "Outputs";
	public static final String INDETERMINATES = "Indeterminates";

	public static final String GRAPH = "graph";

	public String getIfaceId();

	public int getIfaceVersion();

	public String getResourceId();

	public DirectedGraph getGraph();

	public void setGraph(DirectedGraph graph);

	public Collection addItemsToFilterListener(DListListener l);

	public Collection removeItemsToFilterListener(DListListener l);

	public HashMap getParamIdMap();

	public String getCurrentView();

    public Collection getModelObjectParameters();
}
