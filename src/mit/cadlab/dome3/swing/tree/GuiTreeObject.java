// GuiTreeObject.java
package mit.cadlab.dome3.swing.tree;


// Extension of TreeObject to support custom GUIs
// associated with each treeobject.

public interface GuiTreeObject extends TreeObject
{

	// Support for pop-up context GUIs
	public boolean showGui();
	// returns true if gui is shown;
	// otherwise, returns false (gui already shown/doesn't exist)

	public boolean hideGui();
	// returns true if gui is hidden;
	// otherwise, returns false (gui already hidden/doesn't exist)

}
