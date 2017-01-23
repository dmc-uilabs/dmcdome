// Filter.java
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.util.DListListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Filters are collections of items resulting from filtering some collection(s) of items.
 * Filters are chainable (you can filter a filter)
 */
public interface Filter extends ModelComponent, DomeObject, Filterable
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Filter");
	public static final String XML_TAG = "filter";
	public static final String INPUT_OUTPUT_VIEW = "Input/Output View";
	public static final String MODEL_CAUSALITY_VIEW = "Model Causality View";
	public static final List viewNames = Collections.unmodifiableList
	        (Arrays.asList(new String[]{INPUT_OUTPUT_VIEW, MODEL_CAUSALITY_VIEW}));

	public static String parametersXmlTag = "parameters";

	public int getItemCount();

	public List getItems(); // unmodifiable List...maybe Collection in future?

	public boolean containsItem(Object obj);

	public void addFilterListener(DListListener l);

	public void removeFilterListener(DListListener l);

	public List getListsToFilter(); // list of Filterable items being filtered by this filter

	public void addListToFilter(Filterable listOfItems);

	public void removeListToFilter(Filterable listOfItems);
}
