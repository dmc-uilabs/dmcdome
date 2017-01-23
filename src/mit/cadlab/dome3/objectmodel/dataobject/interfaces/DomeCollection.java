package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: May 21, 2003
 * Time: 5:31:24 PM
 * To change this template use Options | File Templates.
 */
public interface DomeCollection
{
	/** return unmodifiable collectioj of contents */
    public Collection getContents();

	/** deletes all objects created in this collection's scope */
	 public void deleteContentsFromScope();
}
