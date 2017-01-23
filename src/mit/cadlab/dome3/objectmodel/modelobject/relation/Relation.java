// Relation.java
package mit.cadlab.dome3.objectmodel.modelobject.relation;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalitySupport;

/**
 * A Relation is a collection of ModelObjects and associated
 * special behavior. A Relation defines a ModelObjectScope that exists in a Model.
 */
public interface Relation extends ModelObject, ModelObjectScope, CausalitySupport
{

	public static final String XML_TAG = "relation";
	public static final String XML_MAPPED_TAG = "mappedRelation";

	public CausalityManager getCausalityManager();


//    public CausalityManager getModelScopeCausalityManager();

}
