// DList.java
package mit.cadlab.dome.swing;

import mit.cadlab.dome.swing.plaf.DListUI;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.JViewport;
import java.util.Vector;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;

/**
 * Customized DList supporting DListModel.
 * uses DListUI
 * Customized ListSelectionModel:
 *   insertIndexInterval sets inserted indices unselected
 * Customized ListCellRenderer:
 *   support DListModel; set icon and text based on object
 * fixed bug 4457325 (setSelectedValue does not scroll)
 *   fixed in Java 1.4
 * bug: setting selection value does not work after setting model
 */
public class DList extends JList {
  
  boolean initialized = false;

  public DList() {
    super();
    setUI(new DListUI());
    setCellRenderer(new DListCellRenderer());
  }

  public DList(ListModel dataModel) {
    super(dataModel);
    setUI(new DListUI());
    setCellRenderer(new DListCellRenderer());
  }

  public DList(Object[] listData) {
    super(listData);
    setUI(new DListUI());
    setCellRenderer(new DListCellRenderer());
  }

  public DList(Vector listData) {
    super(listData);
    setUI(new DListUI());
    setCellRenderer(new DListCellRenderer());
  }

  public void setSelectedValue(Object anObject,boolean shouldScroll) {
    if (shouldScroll && !isValid() && !initialized) { // only works for initial time
      addAncestorListener(new SetSelectionAncestorListener(anObject));
    } else {
      super.setSelectedValue(anObject,shouldScroll);
    }
  }

  public void addNotify() {
    super.addNotify();
    initialized = true;
  }

  protected void setSelectedValue(Object anObject) {
    super.setSelectedValue(anObject,true);
  }

  // overriding default selection model
  protected ListSelectionModel createSelectionModel() {
    return new DListSelectionModel();
  }

  protected class SetSelectionAncestorListener implements AncestorListener {
    protected Object obj;
    public SetSelectionAncestorListener(Object object) {
      this.obj = object;
    }

    public void ancestorAdded(AncestorEvent event) {
    }

    public void ancestorMoved(AncestorEvent event) {
      if (event.getAncestor() instanceof JViewport) {
	// Viewport is now showing and in place
	setSelectedValue(obj);
	removeAncestorListener(this);
	initialized = true;
      }
    }

    public void ancestorRemoved(AncestorEvent event) {
    }
  }

}
