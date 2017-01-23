// GenericObjectTreeNode.java
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.swing.tree.TreeObjectFactory;
import mit.cadlab.dome3.util.ClassUtils;

import javax.swing.tree.MutableTreeNode;
import java.lang.reflect.Constructor;

/**
 * Subclass each this node for each type of tree.
 * TreeObject is looked up from treefactory.
 */
public class GenericObjectTreeNode extends DefaultObjectTreeNode
{
	protected TreeObjectFactory factory;
	protected Object o;

	// remember that a tree node can only be in one tree model
	// in only one place in a tree model at a time
	public GenericObjectTreeNode(Object o, TreeObjectFactory factory)
	{
		this.factory = factory;
		if (o == null)
			throw new IllegalArgumentException(ClassUtils.getClassName(this) + " - null Object");
		this.o = o;
		TreeObject tObj = createTreeObject(o);
		if (tObj == null)
			throw new NullPointerException("TreeObject creation failed for " + o.getClass().getName());
		setUserObject(tObj);
		listener = new DefaultTreeObjectListener();
		getTreeObject().addTreeObjectListener(listener);
		loadChildren();
	}

	public Object getObject()
	{
		return o;
	}

	// use factory for specific tree
	protected TreeObject createTreeObject(Object obj)
	{
		return factory.getTreeObject(obj);
	}

	// use reflection to determine correct constructor to use
	protected MutableTreeNode makeTreeNode(Object obj)
	{
		try {
			Class[] clsArray = new Class[]{Object.class, TreeObjectFactory.class};
			Constructor ctr = getClass().getConstructor(clsArray);
			Object[] objArray = new Object[]{obj, factory};
			return (MutableTreeNode) ctr.newInstance(objArray);
		} catch (Exception ex) {
			System.err.println(ClassUtils.getClassName(this) + " invalid Object: " + ClassUtils.getClassName(obj) + "\n" + obj);
			throw new IllegalArgumentException(ClassUtils.getClassName(this) + " invalid Object: " + obj);
		}
	}

}
