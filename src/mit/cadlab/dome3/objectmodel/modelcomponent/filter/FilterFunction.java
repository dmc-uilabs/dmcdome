// FilterFunction.java
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

/**
 * Function intended to be used by Filter.
 */
public interface FilterFunction
{

	public String getName();

	public boolean keepInFilter(Object obj);

}
