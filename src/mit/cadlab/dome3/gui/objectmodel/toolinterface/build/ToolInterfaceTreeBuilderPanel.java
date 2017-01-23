package mit.cadlab.dome3.gui.objectmodel.toolinterface.build;

import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardViewer;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.TreeBuilderPanel;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.util.DSet;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 29, 2003
 * Time: 9:09:29 AM
 * To change this template use Options | File Templates.
 */
public abstract class ToolInterfaceTreeBuilderPanel extends TreeBuilderPanel
{
    protected ToolInterface _iface;
	protected JComponent _panelComponent;

    public ToolInterfaceTreeBuilderPanel(ToolInterface ifaceBuilder,
	                                      String view)
	{
		_iface = ifaceBuilder;
        createComponents();
        layoutComponents();

	}

    protected void moveUpAction()
    {
    }

    protected void moveDownAction()
	{

	}

    protected abstract void createComponents();

    protected abstract void layoutComponents();

    public static final AbstractAction selectAllAction = new FocusTrackerAction("Select all")
    {
        public void actionPerformed(ActionEvent e)
        {
            TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
            if (pan instanceof ToolInterfaceTreeBuilderPanel)
            {
                ((ToolInterfaceTreeBuilderPanel) pan).selectAllVisibleRows();
            }
            else
            {
                ((ContextTreeBuilderPanel) pan).getContextTree().selectAllVisibleRows();
            }
        }
    };

    public static final AbstractAction clearSelectionAction = new FocusTrackerAction("Clear selection")
    {
        public void actionPerformed(ActionEvent e)
        {
            TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
            if (pan instanceof ToolInterfaceTreeBuilderPanel)
            {
                ((ToolInterfaceTreeBuilderPanel)pan).clearSelection();
            }
            else
            {
                ((ContextTreeBuilderPanel) pan).getContextTree().clearSelection();
            }
        }
    };

    // --- actions for menus and buttons --------------------
	public static final AbstractAction copyAction = new FocusTrackerAction("Copy")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).copySelectedModelObjects();
			} else {
				((ContextTreeBuilderPanel) pan).copySelectedModelObjects();
			}
		}
	};

	public static final AbstractAction cutAction = new FocusTrackerAction("Cut")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).cutSelectedModelObjects();
			} else {
				((ContextTreeBuilderPanel) pan).cutSelectedModelObjects();
			}
		}
	};

	public static final AbstractAction pasteCopyLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).pasteCopyLastSelection();
			} else {
				((ContextTreeBuilderPanel) pan).pasteCopyLastSelection();
			}
		}
	};

	public static final AbstractAction pasteCopyClipboardAction = new FocusTrackerAction("Clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).pasteCopyFromClipboard();
			} else {
				((ContextTreeBuilderPanel) pan).pasteCopyFromClipboard();
			}
		}
	};

	public static final AbstractAction mapLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).mapLastSelection();
			} else {
				((ContextTreeBuilderPanel) pan).mapLastSelection();
			}
		}
	};

	public static final AbstractAction mapClipboardAction = new FocusTrackerAction("Clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).mapFromClipboard();
			} else {
				((ContextTreeBuilderPanel) pan).mapFromClipboard();
			}
		}
	};

	public static final AbstractAction addAndMapLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			/*System.err.println("addAndMapStart");*/
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).addAndMapLastSelection();
			} else {
				((ContextTreeBuilderPanel) pan).addAndMapLastSelection();
			}
		}
	};

	public static final AbstractAction addAndMapClipboardAction = new FocusTrackerAction("Clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).addAndMapFromClipboard();
			} else {
				((ContextTreeBuilderPanel) pan).addAndMapFromClipboard();
			}
		}
	};

    public static final AbstractAction deleteAction = new FocusTrackerAction("Delete")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).deleteSelectedModelObjects();
			} else {
				((ContextTreeBuilderPanel) pan).deleteSelectedModelObjects();
			}
		}
	};


    // --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected final TreeBuilderPanel getToolInterfaceTreeBuilderPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof ToolInterfaceTreeBuilderPanel) {
					return (ToolInterfaceTreeBuilderPanel) o;
				}
			}
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof ToolInterfaceTreeBuilderPanel)
				return (ToolInterfaceTreeBuilderPanel) comp;
			else if (comp instanceof ContextTreeBuilderPanel)
				return (ContextTreeBuilderPanel) comp;

            System.err.println("No current ToolInterfaceTreeBuilderPanel");
			    throw new NullPointerException("No current ToolInterfaceTreeBuilderPanel");
		}
	}

    public static class AddItemAction extends FocusTrackerAction
	{
		public AddItemAction(String itemType)
		{
			super(itemType);
		}

		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getToolInterfaceTreeBuilderPanel(e);
			if (pan instanceof ToolInterfaceTreeBuilderPanel) {
				((ToolInterfaceTreeBuilderPanel) pan).addNewModelObject((String) getValue(
				        AbstractAction.NAME));
			} else {
				((ContextTreeBuilderPanel) pan).addNewModelObject((String) getValue(
				        AbstractAction.NAME));
			}
		}
	}

    public void mapLastSelection()
	{
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
		if (sel == null) return; // nothing in clipboard!
        if (sel.getItems().size() > 1)
            OneButton1Msg.showWarning(this, "Warning: Map Operation", "You are attempting to map more than one model parameter" +
                    " to a single interface parameter. \nPlease select only one model parameter when using the Map function."
            , "Ok", OneButton1Msg.DEFAULT_SIZE);
        else
		    mapItems(sel.getItems());
	}



    public void cutSelectedModelObjects()
	{
		copySelectedModelObjects();
		removeSelectedModelObjects();
	}

    public void pasteCopyLastSelection()
	{
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
		if (sel == null) return; // nothing in clipboard!
		pasteCopies(sel.getItems());
	}

    public void pasteCopyFromClipboard()
	{
		ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
        if (selections == null) return; // nothing selected in clipboard!
        ArrayList allSelections = new ArrayList(); // items can be repeated
        for (int i = 0; i < selections.length; ++i)
            allSelections.addAll(selections[i].getItems());
        pasteCopies(allSelections);
	}

    public void mapFromClipboard()
    {
        ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
        if (selections == null) return; // nothing selected in clipboard!
        DSet allSelections = new DSet(); // items can not be repeated
        for (int i = 0; i < selections.length; ++i)
            allSelections.addAll(selections[i].getItems());
        mapItems(allSelections);
    }

    public void addAndMapFromClipboard()
	{
		ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
		if (selections == null) return; // nothing selected in clipboard!
		DSet allSelections = new DSet(); // items can not be repeated
		for (int i = 0; i < selections.length; ++i)
			allSelections.addAll(selections[i].getItems());
		addAndMapItems(allSelections);
	}

	public abstract void pasteCopies(List items);

	public abstract void removeSelectedModelObjects();

    public abstract void mapItems(List items);

    public abstract void addNewModelObject(String type);

    public abstract void addAndMapLastSelection();

    public abstract void copySelectedModelObjects();

    public abstract void deleteSelectedModelObjects();

    protected abstract void addAndMapItems(List items);

    protected abstract void selectAllVisibleRows();

    protected abstract void clearSelection();

    private void setCausalityBasedonPath(TreePath selectedPath)
    {

    }
}
