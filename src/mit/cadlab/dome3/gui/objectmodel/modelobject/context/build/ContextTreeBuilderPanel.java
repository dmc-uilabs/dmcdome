// ContextTreeBuilderPanel.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.build;

import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardViewer;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.ShiftSupport;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.dome.DomeModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.plugin.PluginBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.AnalysisToolBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.BuildContextTree;
import mit.cadlab.dome3.gui.objectmodel.modelobject.subscription.SubscriptionTreeObject;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.InterfaceModelView;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.ContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.ConcreteVisualization;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.plugin.PluginUtils;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.integrationwizards.integrationwizard.IntegrationWizardFrame;
import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ModelMapping;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ParameterPair;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class ContextTreeBuilderPanel extends TreeBuilderPanel {

    protected BuildContextTree tree;
    protected BuildTreeTable treeTable;
    protected IntegrationWizardFrame wizardFrame = null;

    public ContextTreeBuilderPanel(DefaultContextBuilder contextBuilder) {
        makeTreeTable(contextBuilder);
        layoutComponent(treeTable);
    }

    //for plugin models
    public ContextTreeBuilderPanel(DefaultContextBuilder contextBuilder,
                                   int noCols, String[] colNames, int[] colWidths) {
        makeTreeTable(contextBuilder, noCols, colNames, colWidths);
        layoutComponent(treeTable);
    }

    //for optimization models
    public ContextTreeBuilderPanel(DefaultContextBuilder contextBuilder, String qmooParameterType,
                                   int noCols, String[] colNames, int[] colWidths) {
        makeTreeTable(contextBuilder, qmooParameterType, noCols, colNames, colWidths);
        layoutComponent(treeTable);
    }

    protected void makeTreeTable(DefaultContextBuilder builder) {
        tree = new BuildContextTree(builder);
        tree.addTreeSelectionListener(new ContextBuilderTreeSelectionListener());
        if (builder.getScope() instanceof InterfaceModelView) {
            InterfaceModelView iface = (InterfaceModelView) builder.getScope();
            Context con = iface.getModelViewContext();
            if (con.equals(builder)) {  //if model view
                treeTable = new BuildTreeTable(tree, true);   //isModelView = true
            } else {
                treeTable = new BuildTreeTable(tree);
                if (iface.isDefaultInterface()) {
                    treeTable.setEnabled(false);  //default interface uneditable
                }
            }
        } else {
            treeTable = new BuildTreeTable(tree);
        }
    }

    //for plugin models
    protected void makeTreeTable(DefaultContextBuilder builder,
                                 int noCols, String[] colNames, int[] colWidths) {
        tree = new BuildContextTree(builder);
        tree.addTreeSelectionListener(new ContextBuilderTreeSelectionListener());
        if (builder.getScope() instanceof InterfaceModelView) {
            InterfaceModelView iface = (InterfaceModelView) builder.getScope();
            Context con = iface.getModelViewContext();
            if (con.equals(builder)) {  //if model view
                treeTable = new BuildTreeTable(tree, noCols, colNames,
                        colWidths,
                        false, //defaultInterfaceExists = false;
                        true);   //isModelView = true
            } else {
                treeTable = new BuildTreeTable(tree, noCols, colNames, colWidths);
                if (iface.isDefaultInterface()) {
                    treeTable.setEnabled(false);  //default interface uneditable
                }
            }
        } else {
            treeTable = new BuildTreeTable(tree, noCols, colNames, colWidths);
        }
    }

    //for optimization models
    protected void makeTreeTable(DefaultContextBuilder builder, String qmooParameterType,
                                 int noCols, String[] colNames, int[] colWidths) {
        tree = new BuildContextTree(builder);
        tree.addTreeSelectionListener(new ContextBuilderTreeSelectionListener());
        this.treeTable = new BuildTreeTable(tree, qmooParameterType, noCols, colNames, colWidths);
    }

    public void setUpDownBackgroundColour(Color c) {
        upButton.setBackground(c);
        downButton.setBackground(c);
    }

    public BuildContextTree getContextTree() {
        return tree;
    }

    public void setBackground(Color c) {
        super.setBackground(c);
        if (upDownButtonPanel != null)
            upDownButtonPanel.setBackground(c);
    }

    public DefaultContextBuilder getCurrentContextBuilder() {
        return (DefaultContextBuilder) tree.getRootContext();
    }

    protected static final String INVALID_ADD = "cannot be added to";

    protected boolean isOktoModifyList(Parameter p) {
        Object topScope = ConnectionMappingManager.getObjectTopScope(p);
        if (topScope instanceof ModelInterface || topScope instanceof InterfaceModelView) {
            Collection c = ((DomeModel) p.getModel()).getMappingManager().getMappingsForParameter(p);
            return c.isEmpty();
        } else
            return true;
    }

    public void addNewModelObject(String type) {
        treeTable.stopEditing();
        if (tree.isSelectionEmpty()) { // nothing selected, add to tree
            getCurrentContextBuilder().addNewModelObject(type);
        } else { // find insertion point
            int[] selectedIndices = tree.getSelectionRows();
            Arrays.sort(selectedIndices);
            int selectedIndex = selectedIndices[0]; // first item selected
            TreePath selectedPath = tree.getPathForRow(selectedIndex);
            DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
            if (tree.isExpanded(selectedPath)) {
                DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();

                if (dObj instanceof ContextBuilder) {
                    ((ContextBuilder) dObj).addNewModelObject(type);
                }
//Qing change Sep 22, should check whether it is equal relation first,since EqualRelation is a subinterface of ProceduralRelation
                else if (dObj instanceof EqualRelation) {
                    System.out.println("can't add into a Equal relation, use MAP instead");
                    DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                    DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                    ((ContextBuilder) parentObj).addNewModelObject(type);
                } else if (dObj instanceof ProceduralRelation) {
                    ((Relation) dObj).newModelObject(type);
                } else if (dObj instanceof Filter) {
                    DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                    DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                    if (parentObj instanceof Relation)
                        ((Relation) parentObj).newModelObject(type);
                } else if (dObj instanceof ModelInterface) {
                    ((ModelInterface) dObj).newModelObject(type);
                } else if (dObj instanceof InterfaceModelView) {
                    ModelInterface mi = ((InterfaceModelView) dObj).getContainerInterface();
                    mi.newModelObject(type);
                } else if (dObj instanceof Parameter) {             // DomeList parameter
                    if (((Parameter) dObj).getDataObjectForType("List") != null) {
                        if (isOktoModifyList((Parameter) dObj))
                            ((DomeListData) ((Parameter) dObj).getCurrentDataObject()).addItem(type);
                    }
                } else {
                    DefaultContextBuilder.showWarning(this, INVALID_ADD, type,
                            getObjName(dObj));
                }
            } else { // selection is insertion point (skip immutable folder)
                DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                int childIndex = parentNode.getIndex(selectedNode);
                DomeObject dObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                if (dObj instanceof ContextBuilder) {
                    ((ContextBuilder) dObj).addNewModelObject(type, childIndex);
                } else if (dObj instanceof ProceduralRelation) { // selected a filter
                    ((ProceduralRelation) dObj).newModelObject(type);
                } else if (dObj instanceof ModelInterface) { // selected a filter
                    ((ModelInterface) dObj).newModelObject(type);
                } else if (dObj instanceof InterfaceModelView) {
                    ModelInterface mi = ((InterfaceModelView) dObj).getContainerInterface();
                    mi.newModelObject(type);
                } else if (dObj instanceof Filter) {
                    DefaultObjectTreeNode filterParentNode = (DefaultObjectTreeNode) parentNode.getParent();
                    DomeObject filterParentObj = ((DomeTreeObject) filterParentNode.getTreeObject()).getDomeObject();
                    if (filterParentObj instanceof ConcreteProceduralRelation) {
                        ConcreteProceduralRelation rBuilder = (ConcreteProceduralRelation) filterParentObj;
                        if (rBuilder.isInputFilter(dObj)) { // input filter
                            rBuilder.newModelObject(type, childIndex);
                        } else {
                            rBuilder.newModelObject(type);
                        }
                    } else {
                        DefaultContextBuilder.showWarning(this, INVALID_ADD, type,
                                getObjName(filterParentObj));
                    }
                } else if (dObj instanceof Parameter) {          //DomeList parameter
                    if (((Parameter) dObj).getDataObjectForType("List") != null) {
                        if (isOktoModifyList((Parameter) dObj))
                            ((DomeListData) ((Parameter) dObj).getCurrentDataObject()).addItem(childIndex, type);
                    }
                } else {
                    DefaultContextBuilder.showWarning(this, INVALID_ADD, type,
                            getObjName(dObj));
                }
            }
        }
    }

    public void deleteSelectedModelObjects() {
        treeTable.stopEditing();
        if (tree.isSelectionEmpty()) return;
        TreePath[] selectedPaths = tree.getSelectionPaths();
        HashMap selectedModelObjects = new HashMap(); // key is scope; objs in list
        for (int i = 0; i < selectedPaths.length; ++i) {
            DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPaths[i].getLastPathComponent();
            ModelObject mObj = (ModelObject) ((DomeTreeObject) node.getTreeObject()).getDomeObject();
            if (mObj instanceof Parameter) {
                DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) node.getParent();
                int childIndex = parentNode.getIndex(node);
                DomeTreeObject treeObj = (DomeTreeObject) parentNode.getTreeObject();
                if (treeObj.getDomeObject() instanceof ModelObject) {
                    ModelObject parent_mObj = (ModelObject) treeObj.getDomeObject();
                    if (parent_mObj instanceof Parameter && ((Parameter) parent_mObj).getCurrentType().equals("List")) {
                        if (isOktoModifyList((Parameter) parent_mObj))
                            ((DomeListData) ((Parameter) parent_mObj).getCurrentDataObject()).deleteElementAt(childIndex);
                        continue;
                    }				//get parent node info
                }
            }
            if (mObj instanceof Filter)
                continue;
            List scopeList = (List) selectedModelObjects.get(mObj.getScope());
            if (scopeList == null) {
                scopeList = new ArrayList();
                selectedModelObjects.put(mObj.getScope(), scopeList);
            } else if (scopeList.contains(mObj)) {
                continue;
            }
            scopeList.add(mObj);
        }

//   ModelObjectScope sc = getCurrentContextBuilder().getScope();
        for (Iterator iter = selectedModelObjects.keySet().iterator(); iter.hasNext();) {
            ModelObjectScope sc = (ModelObjectScope) iter.next();
            List mObjs = new ArrayList();
            if (sc instanceof DomeModelBuilder) {
                DomeModelBuilder mBuilder = (DomeModelBuilder) sc;
                List mObjects = (List) selectedModelObjects.remove(mBuilder);
                ConnectionMappingManager mgr = mBuilder.getMappingManager();
                for (Iterator it = mObjects.iterator(); it.hasNext();) {
                    ModelObject mobject = (ModelObject) it.next();
                    if (mobject instanceof Parameter) {
                        Collection conn = mgr.getInterfaceConnections((Parameter) mobject);
                        Collection maps = mgr.getMappingsForParameter((Parameter) mobject);
                        Collection projectMaps = null;
                        Collection projectConns = null;
                        ConnectionMappingManager projectMgr = null;
                        if (((DomeModelBuilder) sc).isIntegrationModel()) {
                            IntegrationProject proj = ((DomeModelBuilder) sc).getIntegrationProject();
                            projectMgr = proj.getMappingManager();
                            projectMaps = projectMgr.getMappingsForParameter((Parameter) mobject);
                            projectConns = projectMgr.getInterfaceConnections((Parameter) mobject);
                        }
                        if ((conn.size() > 2 || maps.size() > 0) ||
                                (projectMaps != null && projectMaps.size() > 0) ||
                                (projectConns != null && projectConns.size() > 0)) {
//assumption - model parameter has at least two connections,
//both of which are in the default interface (model view and build view).
//if those are the only connections then do not show dialog
//only show the dialog if referenced elsewhere.
//if user wants to delete a particular obj
//then put it in different list
                            boolean wantTodelete = showDeleteMapDialog(mobject);
                            if (wantTodelete) {
                                if (((DomeModelBuilder) sc).isIntegrationModel()) {
                                    List temp = new ArrayList(1);
                                    temp.add(mobject);
                                    removeMappingsandConnections(projectMgr, temp);   //remove from project's mapping mgr
                                }
                                mObjs.add(mobject);
                            }
                        } else {
                            mObjs.add(mobject);
                        }
                    } else if (mobject instanceof Relation) {
                        Collection conn = mgr.getInterfaceConnections((Relation) mobject);
                        if (conn.size() > 1) {
//assumption - model relation has at least one connection,
//which is in the default interface (model view).
//if that is the only connection then do not show dialog
//only show the dialog if referenced elsewhere.
//if user wants to delete a particular obj
//then put it in different list
                            boolean wantTodelete = showDeleteRefDialog(mobject);
                            if (wantTodelete) {
                                mObjs.add(mobject);
                                if (mobject instanceof ProceduralRelation) {
                                    mBuilder.removeListFromCausalFilters((ProceduralRelation) mobject);
                                }
                            }
                        } else {
                            mObjs.add(mobject);
                            if (mobject instanceof ProceduralRelation) {
                                mBuilder.removeListFromCausalFilters((ProceduralRelation) mobject);
                            }
                        }
                    } else if (mobject instanceof Subscription) {
                        IntegrationProject p = mBuilder.getIntegrationProject();
                        ConnectionMappingManager cmg = p.getMappingManager();
                        Collection objs = ((Subscription) mobject).getModelObjects();
                        removeMappingsandConnections(cmg, objs);   //remove from project's mapping mgr
                        removeMappingsandConnections(mgr, objs);  //remove from iModel's mapping mgr
                        mObjs.add(mobject); //so the object gets removed from the model
                        mBuilder.removeListFromCausalFilters((Subscription) mobject);
                    } else {//delete context
                        mObjs.add(mobject);
                    }
                }
//remove mappings before removing objects
                removeMappingsandConnections(mgr, mObjs);
// delete model objects
                mBuilder.deleteModelObjects(mObjs);
            } else if (sc instanceof ModelInterfaceBuilder || sc instanceof InterfaceModelView) {
                ModelInterfaceBuilder mIfaceBuilder = null;
                if (sc instanceof ModelInterfaceBuilder) {
                    mIfaceBuilder = (ModelInterfaceBuilder) sc;
                } else {
                    ModelInterface mi = ((InterfaceModelView) sc).getContainerInterface();
                    mIfaceBuilder = (ModelInterfaceBuilder) mi;
                }
                mObjs = (List) selectedModelObjects.remove(sc);
//remove mappings before removing objects
                Model mod = mIfaceBuilder.getModel();
                ConnectionMappingManager mgr = null;
                if (mod instanceof DomeModel)
                    mgr = ((DomeModel) mod).getMappingManager();
                if (mod instanceof IntegrationProject)
                    mgr = ((IntegrationProject) mod).getMappingManager();
                removeMappingsandConnections(mgr, mObjs);
                mIfaceBuilder.deleteModelObjects(mObjs);
            } else if (sc instanceof Relation) {
                Relation rel = (Relation) sc;
                mObjs = (List) selectedModelObjects.remove(rel);
//remove mappings before removing objects
                Model mod = rel.getModel();
                ConnectionMappingManager mgr = null;
                if (mod instanceof DomeModel) {
                    mgr = ((DomeModel) mod).getMappingManager();
                } else if (mod instanceof IntegrationProject) {
                    mgr = ((IntegrationProject) mod).getMappingManager();
                }
                removeMappingsandConnections(mgr, mObjs);
// delete relation objects
                rel.deleteModelObjects(mObjs);
            } else if (sc instanceof OptimizationToolBuild) {
                OptimizationToolBuild analysisTool = (OptimizationToolBuild) sc;
                List mObjects = (List) selectedModelObjects.remove(analysisTool);
                ConnectionMappingManager mgr = analysisTool.getMappingManager();
                for (Iterator it = mObjects.iterator(); it.hasNext();) {
                    ModelObject mobject = (ModelObject) it.next();
                    if (mobject instanceof Parameter) {
                        Collection conn = mgr.getInterfaceConnections((Parameter) mobject);
                        Collection maps = mgr.getMappingsForParameter((Parameter) mobject);
                        if ((conn.size() > 2 || maps.size() > 0)) {
                            boolean wantTodelete = showDeleteMapDialog(mobject);
                            if (wantTodelete)
                                mObjs.add(mobject);
                        } else
                            mObjs.add(mobject);
                    } else {//delete context
                        mObjs.add(mobject);
                    }
                }

//remove mappings before removing objects
                removeMappingsandConnections(mgr, mObjs);
// delete model objects
                analysisTool.deleteModelObjects(mObjs);

            }else if (sc instanceof ToolInterface)
            {
                OptimizationInterfaceBuild tface = null;
                ConnectionMappingManager mgr = null;
                if (sc instanceof OptimizationInterfaceBuild)
                {
                    tface = (OptimizationInterfaceBuild) sc;
                    mObjs = (List) selectedModelObjects.remove(sc);
                    mgr = ((OptimizationToolBuild) tface.getModel()).getMappingManager();
                }
                removeMappingsandConnections(mgr, mObjs);
                tface.deleteModelObjects(mObjs);
            }
            else
            {
                DefaultContextBuilder.showWarning(this, "Warning: Cannot be deleted.", getNamesOfChildren(mObjs),
                        getObjName(sc));
             }
        }
    }

    protected static final String REF_TITLE = "Warning: Delete";
    protected static final String REF_MSG = "is referenced elsewhere.  Delete it?";
    protected static final String REF_MAPPED_MSG = "is mapped to other parameters.  Delete it?";
    protected static final String REF_OK = "OK";
    protected static final String REF_CANCEL = "Cancel";
    protected static final Dimension REF_SIZE = new Dimension(300, 100);

    private void showAddandMapDialog(DomeObject dObj) {
        String msg = "A model parameter can only be mapped to relation/subscription parameters or vice-versa. ";
        msg += "A twin or equal relation may be used to equate it with the model parameter.";
        OneButton1Msg.showWarning(null, "Warning: Add and Map", msg, "OK", new Dimension(1, 1));
    }

    private boolean showDeleteRefDialog(ModelObject mObj) {
        int answer = TwoButton2Msg.showWarning(this, REF_TITLE, REF_MSG, mObj.getName(),
                REF_OK, REF_CANCEL, REF_SIZE);
        if (answer == TwoButton2Msg.LEFT_OPTION) {
            return true;
        } else {// otherwise, keep it
            return false;
        }
    }

    private boolean showDeleteMapDialog(ModelObject mObj) {
        int answer = TwoButton2Msg.showWarning(this, REF_TITLE, REF_MAPPED_MSG, mObj.getName(),
                REF_OK, REF_CANCEL, REF_SIZE);
        if (answer == TwoButton2Msg.LEFT_OPTION) {
            return true;
        } else {// otherwise, keep it
            return false;
        }
    }

    private void removeMappingsandConnections(ConnectionMappingManager mgr,
                                              Collection mObjs) {
        for (Iterator i = mObjs.iterator(); i.hasNext();) {
            Object obj = i.next();
            if (obj instanceof Parameter) {
                mgr.removeAllMappings((Parameter) obj);
            } else if (obj instanceof Relation) {
                //done implicitly in removeAllMappings for a parameter
//				mgr.removeAllConnections((Relation)obj);
                Collection relobjs = ((Relation) obj).getModelObjects();
                for (Iterator j = relobjs.iterator(); j.hasNext();) {
                    Object object = j.next();
                    if (object instanceof Parameter) {
                        mgr.removeAllMappings((Parameter) object);
                    }
                }
            }
        }
    }

    public void removeSelectedModelObjects() {
        treeTable.stopEditing();
        SelectionInfo selectionInfo = getSelectionObjectsByParent();
        if (selectionInfo == null) return;
        HashMap selections = selectionInfo.selections;
        int[] parentIndices = selectionInfo.parentIndices;
        for (int i = parentIndices.length - 1; i >= 0; --i) { // delete bottom up
            List selectedChildren = (List) selections.get(new Integer(parentIndices[i]));
            DomeObject parentObj = (DomeObject) selectedChildren.remove(0);
            if (parentObj instanceof DefaultContextBuilder) {  // if filter, get parent of filter
                ((DefaultContextBuilder) parentObj).removeModelObjectReferences(selectedChildren);
                ModelObjectScope scope = ((DefaultContextBuilder) parentObj).getScope();
                if (scope instanceof ModelInterfaceBuilder) {
                    ((ModelInterfaceBuilder) scope).removeModelObjects(selectedChildren);
                }
            } else if (parentObj instanceof Relation &&
                    !(parentObj instanceof ProceduralRelation)) {
                String nameString = getNamesOfChildren(selectedChildren) + " of " + parentObj.getName();
                int answer = showRelationRemoveOptions(this, nameString, selectedChildren.size() == 1);
                if (answer == TwoButton2Msg.LEFT_OPTION) { // delete object from relation
                    ((Relation) parentObj).deleteModelObjects(selectedChildren);
                } // else keep it
            } else if (parentObj instanceof Filter) {
                Model model = ((ModelComponent) parentObj).getModel();
                String nameString = getNamesOfChildren(selectedChildren) + " of " + model.getName();
                int answer = showRelationRemoveOptions(this, nameString, selectedChildren.size() == 1);
                if (answer == TwoButton2Msg.LEFT_OPTION) { // delete object from relation
                    model.deleteModelObjects(selectedChildren);
                } // else keep it
            } else if (parentObj instanceof ModelInterface) {
                ((ModelInterfaceBuilder) parentObj).removeModelObjects(selectedChildren);
            } else if (parentObj instanceof InterfaceModelView) {
                ModelInterface mi = ((InterfaceModelView) parentObj).getContainerInterface();
                ((ModelInterfaceBuilder) mi).removeModelObjects(selectedChildren);
            } else if (parentObj instanceof Visualization) {
                ((Visualization) parentObj).removeFromAvailableList(selectedChildren);
            } else if (parentObj instanceof Parameter) {
                if (((Parameter) parentObj).getDataObjectForType("List") != null) {
                    if (isOktoModifyList((Parameter) parentObj))
                        ((DomeListData) ((Parameter) parentObj).getCurrentDataObject()).removeObjects(selectedChildren);
                }
            } else {
                DefaultContextBuilder.showWarning(this, "can not be removed from", getNamesOfChildren(selectedChildren),
                        getObjName(parentObj));
            }
        }
    }

    protected static final String RELREMOVE_TITLE = "Options: Relation remove";
    protected static final String RELREMOVE_MSG1 = "is in a relation and cannot be removed.";
    protected static final String RELREMOVE_MSG2 = "are in a relation and cannot be removed.";
    protected static final String RELREMOVE_OK = "delete instead";
    protected static final String RELREMOVE_CANCEL = "cancel";
    protected static final Dimension RELREMOVE_SIZE = new Dimension(290, 90);

    private int showRelationRemoveOptions(Component parent, String objectNames, boolean isSingleObject) {
        return TwoButton2Msg.showOption(getParent(), RELREMOVE_TITLE,
                isSingleObject ? RELREMOVE_MSG1 : RELREMOVE_MSG2,
                objectNames,
                RELREMOVE_OK, RELREMOVE_CANCEL, RELREMOVE_SIZE);
    }

    public void copySelectedModelObjects() {
        if (tree.isSelectionEmpty()) return;
        // copy in order in tree
        int[] selectionRows = tree.getSelectionRows();
        Arrays.sort(selectionRows);
        List selectedDomeObjects = new ArrayList();
        for (int i = 0; i < selectionRows.length; ++i) {
            TreePath selectedPath = tree.getPathForRow(selectionRows[i]);
            DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
            DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
            if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter))
                selectedDomeObjects.add(dObj);
        }
        BuildMode.clipboard.addSelection(selectedDomeObjects);
    }

    public void cutSelectedModelObjects() {
        // copy, then remove
        copySelectedModelObjects();
        removeSelectedModelObjects();
    }

    public void pasteReferenceLastSelection() {
        ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
        if (sel == null) return; // nothing in clipboard!
        //filter for plugin model here
        if (isInPluginModel())
            pasteReferences(filterValidDataTypes(sel.getItems()));
        else
            pasteReferences(sel.getItems());
    }

    public void pasteReferenceFromClipboard() {
        ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
        if (selections == null) return; // nothing selected in clipboard!
        DSet allSelections = new DSet(); // items can not be repeated
        for (int i = 0; i < selections.length; ++i)
            allSelections.addAll(selections[i].getItems());
        //filter for plugin model here
        if (isInPluginModel())
            pasteReferences(filterValidDataTypes(allSelections));
        else
            pasteReferences(allSelections);
    }

    protected static final String INVALID_ADD_REF = "cannot be added as references to";

    protected void pasteReferences(List items) {
        treeTable.stopEditing();
        if (tree.isSelectionEmpty()) { // paste in top container
            getCurrentContextBuilder().addModelObjectReferences(items);
        } else { // find insertion point
            int[] selectedIndices = tree.getSelectionRows();
            Arrays.sort(selectedIndices);
            int selectedIndex = selectedIndices[0]; // first item selected
            TreePath selectedPath = tree.getPathForRow(selectedIndex);
            DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
            if (tree.isExpanded(selectedPath)) {
                DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
                if (dObj instanceof ContextBuilder) {
                    ((ContextBuilder) dObj).addModelObjectReferences(items);
                } else if (dObj instanceof Parameter) {
                    if (((Parameter) dObj).getDataObjectForType("List") != null) {
                        if (isOktoModifyList((Parameter) dObj)) {
                            items = filterForValidItems(dObj, items);
                            ((DomeListData) ((Parameter) dObj).getCurrentDataObject()).addItems(items);
                        }
                    }
                } else if (dObj instanceof ConcreteVisualization) {
                    ((ConcreteVisualization) dObj).addToAvailabelList(items);
                } else {
                    DefaultContextBuilder.showWarning(this, INVALID_ADD_REF, getNamesOfChildren(items),
                            getObjName(dObj));
                }
            } else { // selection is insertion point (skip immutable folder)
                DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                int childIndex = parentNode.getIndex(selectedNode);
                DomeObject dObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                if (dObj instanceof ContextBuilder) {
                    ((ContextBuilder) dObj).addModelObjectReferences(items, childIndex);
                } else if (dObj instanceof Parameter) {
                    if (((Parameter) dObj).getDataObjectForType("List") != null) {
                        if (isOktoModifyList((Parameter) dObj)) {
                            items = filterForValidItems(dObj, items);
                            ((DomeListData) ((Parameter) dObj).getCurrentDataObject()).addItems(childIndex, items);
                        }
                    }
                } else if (dObj instanceof Visualization) {
                    items = filterForValidItems(dObj, items);
                    ((Visualization) dObj).addToAvailabelList(items);
                } else { // relation == mapping
                    DefaultContextBuilder.showWarning(this, INVALID_ADD_REF, getNamesOfChildren(items),
                            getObjName(dObj));
                }
            }
        }
    }

    public void pasteCopyLastSelection() {
        ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
        if (sel == null) return; // nothing in clipboard!
        //filter for plugin model here
        if (isInPluginModel())
            pasteCopies(filterValidDataTypes(sel.getItems()));
        else
            pasteCopies(sel.getItems());
    }

    public void pasteCopyFromClipboard() {
        ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
        if (selections == null) return; // nothing selected in clipboard!
        ArrayList allSelections = new ArrayList(); // items can be repeated
        for (int i = 0; i < selections.length; ++i)
            allSelections.addAll(selections[i].getItems());
        //filter for plugin model here
        if (isInPluginModel())
            pasteCopies(filterValidDataTypes(allSelections));
        else
            pasteCopies(allSelections);
    }

    protected void pasteCopies(List items) {
        treeTable.stopEditing();
        boolean deepCopy = shouldDeepCopy(items);
        if (tree.isSelectionEmpty()) { // paste in top container
            getCurrentContextBuilder().addModelObjectCopies(items, deepCopy);
        } else { // find insertion point
            int[] selectedIndices = tree.getSelectionRows();
            Arrays.sort(selectedIndices);
            int selectedIndex = selectedIndices[0]; // first item selected
            TreePath selectedPath = tree.getPathForRow(selectedIndex);
            DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
            if (tree.isExpanded(selectedPath)) {
                DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
                if (dObj instanceof ContextBuilder) {
                    ((ContextBuilder) dObj).addModelObjectCopies(items, deepCopy);
                } else if (dObj instanceof Relation) {
                    items = filterForValidItems(dObj, items);
                    ((Relation) dObj).newModelObjects(items);
                } else if (dObj instanceof ModelInterface) {
                    items = filterForValidItems(dObj, items);
                    ((ModelInterface) dObj).newModelObjects(items);
                } else if (dObj instanceof InterfaceModelView) {
                    items = filterForValidItems(dObj, items);
                    ModelInterface mi = ((InterfaceModelView) dObj).getContainerInterface();
                    mi.newModelObjects(items);
                } else if (dObj instanceof Filter) {
                    DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                    DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                    if (parentObj instanceof Relation) {
                        items = filterForValidItems(parentObj, items);
                        ((Relation) parentObj).newModelObjects(items);
                    }
                } else if (dObj instanceof Parameter) {
                    if (((Parameter) dObj).getDataObjectForType("List") != null) {
                        if (isOktoModifyList((Parameter) dObj)) {
                            items = filterForValidItems(dObj, items);
                            ((DomeListData) ((Parameter) dObj).getCurrentDataObject()).addCopies(items);
                        }
                    }
                } else if (dObj instanceof Visualization) {
                    items = filterForValidItems(dObj, items);
                    ((Visualization) dObj).addToAvailabelList(items);
                } else {
                    DefaultContextBuilder.showWarning(this, INVALID_PASTE, getNamesOfChildren(items),
                            getObjName(dObj));
                }
            } else { // selection is insertion point (skip immutable folder)
                DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                int childIndex = parentNode.getIndex(selectedNode);
                DomeObject dObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                if (dObj instanceof ContextBuilder) {
                    ((ContextBuilder) dObj).addModelObjectCopies(items, childIndex, deepCopy);
                } else if (dObj instanceof ProceduralRelation) { // selected a filter
                    items = filterForValidItems(dObj, items);
                    ((ProceduralRelation) dObj).newModelObjects(items);
                } else if (dObj instanceof ModelInterface) {
                    items = filterForValidItems(dObj, items);
                    ((ModelInterface) dObj).newModelObjects(items);
                } else if (dObj instanceof InterfaceModelView) {
                    items = filterForValidItems(dObj, items);
                    ModelInterface mi = ((InterfaceModelView) dObj).getContainerInterface();
                    mi.newModelObjects(items);
                } else if (dObj instanceof Filter) {
                    DefaultObjectTreeNode filterParentNode = (DefaultObjectTreeNode) parentNode.getParent();
                    DomeObject filterParentObj = ((DomeTreeObject) filterParentNode.getTreeObject()).getDomeObject();
                    if (filterParentObj instanceof ConcreteProceduralRelation) {
                        ConcreteProceduralRelation rBuilder = (ConcreteProceduralRelation) filterParentObj;
                        items = filterForValidItems(rBuilder, items);
                        if (rBuilder.isInputFilter(dObj)) { // input filter
                            rBuilder.newModelObjects(items, childIndex);
                        } else {
                            rBuilder.newModelObjects(items);
                        }
                    } else {
                        DefaultContextBuilder.showWarning(this, INVALID_PASTE, getNamesOfChildren(items),
                                getObjName(filterParentObj));
                    }
                } else if (dObj instanceof Parameter) {
                    if (((Parameter) dObj).getDataObjectForType("List") != null) {
                        if (isOktoModifyList((Parameter) dObj)) {
                            items = filterForValidItems(dObj, items);
                            ((DomeListData) ((Parameter) dObj).getCurrentDataObject()).addCopies(childIndex, items);
                        }
                    }
                } else {
                    DefaultContextBuilder.showWarning(this, INVALID_PASTE, getNamesOfChildren(items),
                            getObjName(dObj));
                }
            }
        }
    }

    protected static final String CONTEXT_COPY_MSG = "context is about to be copied.\n" +
            "Make copies of all contents as well (deep copy)\n" +
            "or make references to original contents (shallow copy).";
    protected static final String CONTEXTS_COPY_MSG = "contexts are about to be copied.\n" +
            "Make copies of all contents as well (deep copy)\n" +
            "or make references to original contents (shallow copy).";
    protected static final String DEEP_COPY_OPTION = "deep copy";
    protected static final String SHALLOW_COPY_OPTION = "shallow copy";

    protected boolean shouldDeepCopy(List items) {
        List contexts = new ArrayList();
        Iterator it = items.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Context) {
                if (!((Context) obj).getScope().equals(getCurrentContextBuilder().getScope()))
                    return true;
                if (((Context) obj).isEmpty())
                    continue;
                contexts.add(obj);
            }
        }
        if (contexts.isEmpty())
            return false;
        int answer = TwoButton2Msg.showOption(this, "Options: context copy",
                (contexts.size() == 1) ? CONTEXT_COPY_MSG : CONTEXTS_COPY_MSG,
                getNamesOfChildren(contexts),
                DEEP_COPY_OPTION, SHALLOW_COPY_OPTION, TwoButton2Msg.DEFAULT_SIZE);
        return (answer == TwoButton2Msg.LEFT_OPTION); // deepcopy
    }

    protected void moveUpAction() { // implementation for multiple parents
        treeTable.stopEditing();
        SelectionInfo selectionInfo = getSelectionChildIndicesByParent();
        if (selectionInfo == null) return;
        HashMap selections = selectionInfo.selections;
        int[] parentIndices = selectionInfo.parentIndices;
        HashMap newChildIndicesByParent = new HashMap();
        for (int i = parentIndices.length - 1; i >= 0; --i) { // move items bottom up
            int parentIndex = parentIndices[i];
            List selectedChildren = (List) selections.get(new Integer(parentIndex));
            ObjectTreeNode parentNode = (ObjectTreeNode) selectedChildren.remove(0);
            DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
            int[] childIndices = listToIntArray(selectedChildren);
            if (parentObj instanceof ShiftSupport) {
                ((ShiftSupport) parentObj).shiftLeft(childIndices);
            } else {
                DefaultContextBuilder.showWarning(this, "does not support reordering items", getObjName(parentObj));
                continue;
            }
            // calculate new child indices
            Arrays.sort(childIndices);
            int[] newIndices;
            if (childIndices[0] == 0) { // skip
                newIndices = new int[childIndices.length - 1];
                for (int j = 1; j < childIndices.length; ++j)
                    newIndices[j - 1] = childIndices[j] - 1;
            } else { // shift all indices up
                newIndices = new int[childIndices.length];
                for (int j = 0; j < childIndices.length; ++j)
                    newIndices[j] = childIndices[j] - 1;
            }
            newChildIndicesByParent.put(parentNode, newIndices);
        }
        setSelectionsByParent(newChildIndicesByParent);
    }

    protected void moveDownAction() { // implementation for multiple parents
        treeTable.stopEditing();
        SelectionInfo selectionInfo = getSelectionChildIndicesByParent();
        if (selectionInfo == null) return;
        HashMap selections = selectionInfo.selections;
        int[] parentIndices = selectionInfo.parentIndices;
        HashMap newChildIndicesByParent = new HashMap();
        for (int i = parentIndices.length - 1; i >= 0; --i) { // move items bottom up
            int parentIndex = parentIndices[i];
            List selectedChildren = (List) selections.get(new Integer(parentIndex));
            ObjectTreeNode parentNode = (ObjectTreeNode) selectedChildren.remove(0);
            DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
            int[] childIndices = listToIntArray(selectedChildren);
            if (parentObj instanceof ShiftSupport) {
                ((ShiftSupport) parentObj).shiftRight(childIndices);
            } else {
                DefaultContextBuilder.showWarning(this, "does not support reordering items", getObjName(parentObj));
                continue;
            }
            // calculate new child indices
            Arrays.sort(childIndices);
            int[] newIndices;
            if (childIndices[childIndices.length - 1] == parentNode.getChildCount() - 1) { // skip
                newIndices = new int[childIndices.length - 1];
                for (int j = 0; j < (childIndices.length - 1); ++j)
                    newIndices[j] = childIndices[j] + 1;
            } else { // shift all indices down
                newIndices = new int[childIndices.length];
                for (int j = 0; j < childIndices.length; ++j)
                    newIndices[j] = childIndices[j] + 1;
            }
            newChildIndicesByParent.put(parentNode, newIndices);
        }
        setSelectionsByParent(newChildIndicesByParent);
    }

    protected SelectionInfo getSelectionObjectsByParent() {
        if (tree.isSelectionEmpty()) return null;
        int[] selectedRows = tree.getSelectionRows();
        HashMap selections = sortSelectedObjectsByParent(selectedRows);
        int[] parentIndices = new int[selections.size()];
        Iterator it = selections.keySet().iterator();
        int index = 0;
        while (it.hasNext()) {
            parentIndices[index++] = ((Integer) it.next()).intValue();
        }
        Arrays.sort(parentIndices);
        return new SelectionInfo(selections, parentIndices);
    }

    protected SelectionInfo getSelectionChildIndicesByParent() {
        if (tree.isSelectionEmpty()) return null;
        int[] selectedRows = tree.getSelectionRows();
        HashMap selections = sortSelectedChildIndicesByParent(selectedRows);
        int[] parentIndices = new int[selections.size()];
        Iterator it = selections.keySet().iterator();
        int index = 0;
        while (it.hasNext()) {
            parentIndices[index++] = ((Integer) it.next()).intValue();
        }
        Arrays.sort(parentIndices);
        return new SelectionInfo(selections, parentIndices);
    }

    class SelectionInfo {
        public HashMap selections;
        public int[] parentIndices;

        public SelectionInfo(HashMap selections, int[] parentIndices) {
            this.selections = selections;
            this.parentIndices = parentIndices;
        }
    }

    protected HashMap sortSelectedObjectsByParent(int[] selectedRows) {
        HashMap selections = new HashMap();
        for (int i = 0; i < selectedRows.length; ++i) {
            int currentRow = selectedRows[i];
            TreePath currentTreePath = tree.getPathForRow(currentRow);
            DefaultObjectTreeNode currentNode = (DefaultObjectTreeNode) currentTreePath.getLastPathComponent();
            TreePath parentPath = currentTreePath.getParentPath();
            int parentRow = tree.getRowForPath(parentPath);
            DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) currentNode.getParent();
            // if parent is filter, get context
            DomeObject childObj = ((DomeTreeObject) currentNode.getTreeObject()).getDomeObject();
            Integer key = new Integer(parentRow);
            if (selections.containsKey(key)) { // add to it
                List selectedChildren = (List) selections.get(key);
                selectedChildren.add(childObj);
            } else { // create new key
                List selectedChildren = new ArrayList();
                DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                selectedChildren.add(parentObj); // parent first
                selectedChildren.add(childObj);
                selections.put(key, selectedChildren);
            }
        }
        return selections;
    }

    protected HashMap sortSelectedChildIndicesByParent(int[] selectedRows) {
        HashMap selections = new HashMap();
        for (int i = 0; i < selectedRows.length; ++i) {
            int currentRow = selectedRows[i];
            TreePath currentTreePath = tree.getPathForRow(currentRow);
            DefaultObjectTreeNode currentNode = (DefaultObjectTreeNode) currentTreePath.getLastPathComponent();
            TreePath parentPath = currentTreePath.getParentPath();
            int parentRow = tree.getRowForPath(parentPath);
            DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) currentNode.getParent();
            int childIndex = parentNode.getIndex(currentNode);
            Integer key = new Integer(parentRow);
            if (selections.containsKey(key)) { // add to it
                List selectedChildren = (List) selections.get(key);
                selectedChildren.add(new Integer(childIndex));
            } else { // create new key
                List selectedChildren = new ArrayList();
                selectedChildren.add(parentNode); // parent first
                selectedChildren.add(new Integer(childIndex));
                selections.put(key, selectedChildren);
            }
        }
        return selections;
    }

    protected void setSelectionsByParent(HashMap selections) {
        List newSelections = new ArrayList();
        Iterator it = selections.keySet().iterator();
        while (it.hasNext()) {
            DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) it.next();
            int[] childIndices = (int[]) selections.get(parentNode);
            int parentIndex = tree.getRowForPath(new TreePath(parentNode.getPath()));
            for (int i = 0; i < childIndices.length; ++i)
                newSelections.add(new Integer(parentIndex + childIndices[i] + 1));
        }
        int[] newSelectionsArray = listToIntArray(newSelections);
        Arrays.sort(newSelectionsArray);
        tree.setSelectionRows(newSelectionsArray);
    }

    protected int[] listToIntArray(List v) {
        int[] ints = new int[v.size()];
        for (int i = 0; i < ints.length; ++i)
            ints[i] = ((Integer) v.get(i)).intValue();
        Arrays.sort(ints);
        return ints;
    }

    //Qing, Equal relation should be treated differently
    protected boolean isEqualRelationInsertion() {
        if (tree.isSelectionEmpty()) return false;
        TreePath path = tree.getSelectionPath();
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) path.getLastPathComponent();
        DomeObject dObj;
        boolean isEqualRelation = false;

        if (tree.isExpanded(path)) {
            dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();

            if (dObj instanceof EqualRelation)
                isEqualRelation = true;
            else if (dObj instanceof Filter) {
                DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                DomeObject dParentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();


                if (dParentObj instanceof EqualRelation) isEqualRelation = true;
            }
        } else {
            DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
            dObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();

            if (dObj instanceof EqualRelation)
                isEqualRelation = true;
            else if (dObj instanceof Filter) {
                DefaultObjectTreeNode parentparentNode = (DefaultObjectTreeNode) parentNode.getParent();
                DomeObject dParentObj = ((DomeTreeObject) parentparentNode.getTreeObject()).getDomeObject();


                if (dParentObj instanceof EqualRelation) isEqualRelation = true;
            }
        }

        return isEqualRelation;
    }

    protected boolean isRelationInsertion() {
        if (tree.isSelectionEmpty()) return false;
        TreePath path = tree.getSelectionPath();
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) path.getLastPathComponent();
        DomeObject dObj;
        boolean isRelation = false;

        if (tree.isExpanded(path)) {
            dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();

            if (dObj instanceof Relation)
                isRelation = true;
            else if (dObj instanceof Filter) {
                DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                DomeObject dParentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();


                if (dParentObj instanceof Relation) isRelation = true;
            }
        } else {
            DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
            dObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();

            if (dObj instanceof Relation)
                isRelation = true;
            else if (dObj instanceof Filter) {
                DefaultObjectTreeNode parentparentNode = (DefaultObjectTreeNode) parentNode.getParent();
                DomeObject dParentObj = ((DomeTreeObject) parentparentNode.getTreeObject()).getDomeObject();


                if (dParentObj instanceof Relation) isRelation = true;
            }
        }

        return isRelation;
    }

    //change here for equal relation
    public void setEditMenusForSelection() {
        if (!validateTreeSelection()) return; // update at next tree selection event
        // add/insert only for single/no selection
        // delete/remove for one or more selection
        // copy to clipboard for one or more selection
        // paste only for single/no selection
        // all within non-filtered context -- move up/down

        int selectionCount = tree.getSelectionCount();

        // equal relation should allow delete whole relation, not any subitem inside

        if (selectionCount > 1) {
            ContextBuilderMenus.menus.disableAddMenus();
        } else if (selectionCount == 1) {

            ContextBuilderMenus.menus.enableAddMenus(isRelationInsertion(), isEqualRelationInsertion());
            //System.out.println("isModelEqualRelationParameterSelected=" + isModelEqualRelationParameterSelected());


            if (isModelEqualRelationParameterSelected()) {
                ContextBuilderMenus.menus.enableMapMenusForEquals();
            } else if (isSubscriptionSelected()) {
                ContextBuilderMenus.menus.disableAddMenus();
                if (ContextBuilderMenus.menus.copyMI.isEnabled())
                    ContextBuilderMenus.menus.copyMI.setEnabled(false);
            } else if (isModelSubscriptionParameterSelected()) {
                ContextBuilderMenus.menus.disableAddMenus();
                ContextBuilderMenus.menus.disableDeleteMenus();
                if (!ContextBuilderMenus.menus.copyMI.isEnabled())
                    ContextBuilderMenus.menus.copyMI.setEnabled(true);
                if (!BuildMode.clipboard.isEmpty())
                    ContextBuilderMenus.menus.enableMapMIs();
            } else {
                if ((isModelParameterSelected() || isModelRelationParameterSelected()) &&
                        !(BuildMode.clipboard.isEmpty())) {

                    ContextBuilderMenus.menus.enableMapMIs();
                } else {
                    ContextBuilderMenus.menus.disableMapMIs();
                }
            }
        } else { // no selection, insert into context
            ContextBuilderMenus.menus.enableAddMenus(false);
        }

        if (selectionCount == 0) {
            ContextBuilderMenus.menus.disableRemoveMenus();
        } else {
            //if it is equal relation:
            if (isModelEqualRelationSubItemSelected() || isModelEqualRelationParameterSelected()) {
                //not enable
                System.out.println("equal subitem selected=" + isModelEqualRelationSubItemSelected());
                System.out.println("equal param selected=" + isModelEqualRelationParameterSelected());
                ContextBuilderMenus.menus.disableRemoveMenus();
                if (isModelEqualRelationParameterReferenceSelected()) {
//enable remove
                    ContextBuilderMenus.menus.enableRemoveOnlyMenus();
                }
                if (isModelEqualRelationParameterSelected()) {
                    if (!ContextBuilderMenus.menus.copyMI.isEnabled())
                        ContextBuilderMenus.menus.copyMI.setEnabled(true);
                } else {
                    if (ContextBuilderMenus.menus.copyMI.isEnabled())
                        ContextBuilderMenus.menus.copyMI.setEnabled(false);
                }
            } else if (!isModelSubscriptionParameterSelected()) {
                ContextBuilderMenus.menus.enableRemoveMenus();
                if (isSubscriptionSelected() || isModelSubscriptionContextSelected()) {
                    if (ContextBuilderMenus.menus.copyMI.isEnabled())
                        ContextBuilderMenus.menus.copyMI.setEnabled(false);
                    if (isModelSubscriptionContextSelected()) {
                        ContextBuilderMenus.menus.disableRemoveMenus();
                    }
                }
            }
        }
        if (selectionCount == 0) {
            setMoveEnabled(false);
        } else if (selectionCount == 1) {
            setMoveEnabled(canMoveSingleSelection());
        } else {
            setMoveEnabled(canMoveMultipleSelections());
        }
    }

    public void setEditMenusForSelection(String interfaceMenuContext, boolean isDefault) {
        if (isDefault) {
            ModelInterfaceBuildMenus.menus.disableContextRelationMIs();
            ModelInterfaceBuildMenus.menus.disableAddMenus();
            ModelInterfaceBuildMenus.menus.disableMapMIs();
            ModelInterfaceBuildMenus.menus.disableRemoveMenus();
            ModelInterfaceBuildMenus.menus.disableSelectionMIs();
            setMoveEnabled(false);
            return;
        }
        if (interfaceMenuContext.equals(ModeContexts.BUILD_DOMEMODEL_INTERFACE_BUILDVIEW)
                || interfaceMenuContext.equals(ModeContexts.BUILD_DOMEMODEL_INTERFACE_MODELVIEW)) {
            if (!validateTreeSelection()) return; // update at next tree selection event
            // add/insert only for single/no selection
            // delete/remove for one or more selection
            // copy to clipboard for one or more selection
            // paste only for single/no selection
            // all within non-filtered context -- move up/down
            boolean isBuildview = false;
            boolean isModelview = false;
            boolean isClipBoardEmpty = BuildMode.clipboard.isEmpty();

            if (interfaceMenuContext.equals(ModeContexts.BUILD_DOMEMODEL_INTERFACE_BUILDVIEW)) {
                isBuildview = true;
            } else {
                isModelview = true;
            }
            int selectionCount = tree.getSelectionCount();
            if (selectionCount > 1) {
                ModelInterfaceBuildMenus.menus.disableAddMenus();
                if (isModelview) {
                    ModelInterfaceBuildMenus.menus.disableAddAndMapMIs();
                }
            } else if (selectionCount == 1) {
                if (isBuildview) {
                    ModelInterfaceBuildMenus.menus.enableAddMenus();
                    ModelInterfaceBuildMenus.menus.disableRelationMIs();
                    ModelInterfaceBuildMenus.menus.enableContextMIs();
                } else if (isModelview) {
                    ModelInterfaceBuildMenus.menus.disableAddMenus();
                    ModelInterfaceBuildMenus.menus.enableContextMIs();
                    if (!isClipBoardEmpty) {
                        ModelInterfaceBuildMenus.menus.enableAddAndMapMIs();
                    }
                }
                if (isInterfaceParameterSelected() &&
                        !(BuildMode.clipboard.isEmpty())) {
                    ModelInterfaceBuildMenus.menus.enableMapMIs();
                } else {
                    ModelInterfaceBuildMenus.menus.disableMapMIs();
                }
            } else { // no selection, insert into interface
                if (isBuildview) {
                    ModelInterfaceBuildMenus.menus.enableAddMenus();
                    ModelInterfaceBuildMenus.menus.disableRelationMIs();
//We will do this explicitly so if something else has disabled
//only the context menu, the following stmt will enable it again
                    ModelInterfaceBuildMenus.menus.enableContextMIs();
                } else if (isModelview) {
                    ModelInterfaceBuildMenus.menus.disableAddMenus();
                    ModelInterfaceBuildMenus.menus.enableContextMIs();
                    if (!isClipBoardEmpty) {
                        ModelInterfaceBuildMenus.menus.enableAddAndMapMIs();
                    }
                }
                ModelInterfaceBuildMenus.menus.disableMapMIs();
            }
            if (selectionCount == 0) {
                ModelInterfaceBuildMenus.menus.disableRemoveMenus();
            } else {
                ModelInterfaceBuildMenus.menus.enableRemoveMenus();
            }
            if (selectionCount == 0) {
                setMoveEnabled(false);
            } else if (selectionCount == 1) {
                setMoveEnabled(canMoveSingleSelection());
            } else {
                setMoveEnabled(canMoveMultipleSelections());
            }
        }
    }

    public void setPluginModelEditMenusForSelection() {
        if (!validateTreeSelection()) return; // update at next tree selection event
        // add/insert only for single/no selection
        // delete/remove for one or more selection
        // copy to clipboard for one or more selection
        // paste only for single/no selection
        // all within non-filtered context -- move up/down

        int selectionCount = tree.getSelectionCount();

        if (selectionCount > 1) {
            PluginBuildMenus.menus.disableAddMenus();
        } else {
            PluginBuildMenus.menus.enableAddMenus();
        }

        if (selectionCount == 0) {
            PluginBuildMenus.menus.disableRemoveMenus();
        } else {
            PluginBuildMenus.menus.enableRemoveMenus();
        }

        if (selectionCount == 0) {
            setMoveEnabled(false);
        } else if (selectionCount == 1) {
            setMoveEnabled(canMoveSingleSelection());
        } else {
            setMoveEnabled(canMoveMultipleSelections());
        }
    }

    public void setQMOOModelEditMenusForSelection() {
        if (!validateTreeSelection()) return;

        int selectionCount = tree.getSelectionCount();
        if (selectionCount > 1) {
            AnalysisToolBuildMenus.menu.disableAddMenus();
        } else {
            AnalysisToolBuildMenus.menu.enableAddMenus();
        }
        if (selectionCount == 0) {
            AnalysisToolBuildMenus.menu.disableRemoveMenus();
        } else {
            AnalysisToolBuildMenus.menu.enableRemoveMenus();
        }
        if (selectionCount == 0) {
            setMoveEnabled(false);
        } else if (selectionCount == 1) {
            setMoveEnabled(canMoveSingleSelection());
        } else {
            setMoveEnabled(canMoveMultipleSelections());
        }
    }

    protected boolean isInterfaceParameterSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
        return (dObj instanceof Parameter && ((((Parameter) dObj).getScope() instanceof ModelInterface) ||
                (((Parameter) dObj).getScope() instanceof InterfaceModelView)));
    }

    protected boolean isModelParameterSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
        return (dObj instanceof Parameter && ((Parameter) dObj).getScope() instanceof Model);
    }

    protected boolean isModelEqualRelationSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();

        return (dObj instanceof EqualRelation && ((Relation) dObj).getScope() instanceof Model);

    }


    protected boolean isModelEqualRelationSubItemSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();

        DomeObject dObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();

        return (dObj instanceof EqualRelation && ((Relation) dObj).getScope() instanceof Model);

    }


    protected boolean isModelRelationParameterSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
        if (dObj instanceof Parameter) {
            ModelObjectScope scope = ((Parameter) dObj).getScope();
            return (scope instanceof Relation && ((Relation) scope).getScope() instanceof Model);
        }
        return false;
    }

    protected boolean isModelEqualRelationParameterSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();


        DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
        DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();

//check if it's parent is a list -- for paste reference
// if(parentObj instanceof Parameter &&(((Parameter) parentObj).getDataObjectForType("List") != null)){
//    return false;
// }
        if (dObj instanceof Parameter) {
            ModelObjectScope scope = ((Parameter) dObj).getScope();
            return (scope instanceof EqualRelation && ((Relation) scope).getScope() instanceof Model);
        }
        return false;
    }

    protected boolean isSubscriptionSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
        if (dObj instanceof Subscription)
            return true;
        else
            return false;
    }

    protected boolean isModelEqualRelationParameterReferenceSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();


        DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
        DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();

        if (dObj instanceof Parameter && parentObj instanceof Parameter) {
            ModelObjectScope scope = ((Parameter) dObj).getScope();
            return (scope instanceof EqualRelation) && (((Relation) scope).getScope() instanceof Model) && (((Parameter) parentObj).getDataObjectForType("List") != null);
        }
        return false;
    }

    protected boolean isModelSubscriptionParameterSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
        if (dObj instanceof Parameter) {
            ModelObjectScope scope = ((Parameter) dObj).getScope();
            return (scope instanceof Subscription && ((Subscription) scope).getScope() instanceof Model);
        }
        return false;
    }

    protected boolean isModelSubscriptionContextSelected() {
        DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
        if (dObj instanceof Context) {
            ModelObjectScope scope = ((Context) dObj).getScope();
            return (scope instanceof Subscription && ((Subscription) scope).getScope() instanceof Model);
        }
        return false;
    }

    public void mapLastSelection() {
        ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
        if (sel == null) return; // nothing in clipboard!
        mapItems(sel.getItems());
    }

    public void mapFromClipboard() {
        ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
        if (selections == null) return; // nothing selected in clipboard!
        DSet allSelections = new DSet(); // items can not be repeated
        for (int i = 0; i < selections.length; ++i)
            allSelections.addAll(selections[i].getItems());
        mapItems(allSelections);
    }

    protected void mapItems(List items) {
        treeTable.stopEditing();
        if (tree.isSelectionEmpty()) { // nothing to map to
            return;
        } else if (tree.getSelectionCount() != 1) { // to many selected
            return;
        }
        ModelObjectScope scope = getCurrentContextBuilder().getScope();
        if (scope instanceof DomeModelBuilder) {
            DomeModelBuilder mod = (DomeModelBuilder) scope;
            items = filterForValidItems(mod, items);
            TreePath selectedPath = tree.getSelectionPath();
            DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
            DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
            if (dObj instanceof Parameter) {
                if (((Parameter) dObj).getScope() instanceof Model) {
                    //only one param can be mapped to another at a time
                    Object target = items.get(0);
                    if (target instanceof Parameter) {
                        if (((Parameter) target).getScope() instanceof Model) {
                            OneButton1Msg.showWarning(this, "Warning: mapping", "Model parameters cannot" +
                                    " be directly mapped to each other.  Use a relation parameter instead.", "OK",
                                    new Dimension(150, 100));
                            return;
                        }
                    }
                } else if (((Parameter) dObj).getScope() instanceof Relation) {

                    //see if mapping are within one relation
                    boolean isInSameRelation = false;

                    Relation rel = (Relation) ((Parameter) dObj).getScope();
                    Collection dobjs = rel.getModelObjects();
                    for (int i = 0; i < items.size(); i++) {
                        Object target = items.get(i);
                        if (target instanceof Parameter) {
                            if (dobjs.contains(target)) {
                                isInSameRelation = true;
                                break;
                            }
                        }
                    }

                    if (isInSameRelation) {
                        OneButton1Msg.showWarning(this, "Warning: mapping", "Relation parameters cannot" +
                                " be mapped to other paramters within the same relation.", "OK",
                                new Dimension(150, 100));
                        return;
                    }
                }
                try {
                    ConnectionMappingManager mm = mod.getMappingManager();
                    mm.addMappings((Parameter) dObj, items);
                } catch (RuntimeException e) {
                    handleMappingErrors(e);
                }

            }


            // else can not map
        } else if (scope instanceof AnalysisToolBase) {
            AnalysisToolBase model = (AnalysisToolBase) scope;
            items = filterForValidItems(model, items);
            TreePath selectedPath = tree.getSelectionPath();
            DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
            DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
            if (scope instanceof OptimizationToolBuild) {
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    Parameter p = (Parameter) iterator.next();
                    if (((OptimizationToolBuild) scope).getOptimizationToolObjectiveParameterMap().containsKey(dObj)) {
                        if (((OptimizationToolBuild) scope).getOptimizationToolVariableParameterMap().containsKey(p)) {
                            OneButton1Msg.showWarning(this, "Warning: Map Operation", "You are attempting to map a variable parameter " +
                                    "to an objective parameter. \nPlease deselect the variable parameter and try to map again.",
                                    "Ok", OneButton1Msg.DEFAULT_SIZE);
                            return;
                        }
                    } else if (((OptimizationToolBuild) scope).getOptimizationToolVariableParameterMap().containsKey(dObj)) {
                        if (((OptimizationToolBuild) scope).getOptimizationToolObjectiveParameterMap().containsKey(p)) {
                            OneButton1Msg.showWarning(this, "Warning: Map Operation", "You are attempting to map an objective parameter " +
                                    "to a variable param" +
                                    "eter. \nPlease deselect the objective parameter and try to map again.",
                                    "Ok", OneButton1Msg.DEFAULT_SIZE);
                            return;
                        }
                    }
                }
            }
            try {
                ConnectionMappingManager mm = model.getMappingManager();
                mm.addMappings((Parameter) dObj, items);

            } catch (RuntimeException e) {
                handleMappingErrors(e);
            }
        } else if (scope instanceof ToolInterface) {
            ToolInterface ifaceBuilder = (ToolInterface) scope;
            items = filterForValidItems(scope, items);
            TreePath selectedPath = tree.getSelectionPath();
            DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
            DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
            if (dObj instanceof Parameter) {
                try {
                    ConnectionMappingManager mgr = null;
                    Model model = ifaceBuilder.getModel();
                    if (model instanceof OptimizationToolBuild) {
                        mgr = ((OptimizationToolBuild) model).getMappingManager();
                    }
                    Collection mappings = mgr.getMappingsForParameter((Parameter) dObj);
                    if (mappings == null || mappings.size() == 0)
                        mgr.addMappings((Parameter) dObj, items);
                    else
                        OneButton1Msg.showWarning(this, "Warning: Map Operation",
                                "Interface parameter cannot be mapped to two analysis tool parameters.",
                                "Ok", new Dimension(150, 100));
                } catch (RuntimeException e) {
                    handleMappingErrors(e);
                }
            }
        } else if (scope instanceof ModelInterface || scope instanceof InterfaceModelView) {
            ModelInterface ifaceBuilder = null;
            if (scope instanceof ModelInterface) {
                ifaceBuilder = (ModelInterface) scope;
            } else {
                ifaceBuilder = ((InterfaceModelView) scope).getContainerInterface();

            }
            items = filterForValidItems(scope, items);
            TreePath selectedPath = tree.getSelectionPath();
            DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
            DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
            if (dObj instanceof Parameter) {
                try {
                    ConnectionMappingManager mm = null;
                    Model mo = ifaceBuilder.getModel();
                    if (mo instanceof DomeModel) {
                        mm = ((DomeModel) mo).getMappingManager();
                    } else if (mo instanceof IntegrationProject) {
                        mm = ((IntegrationProject) mo).getMappingManager();
                    }
                    Collection mappings = mm.getMappingsForParameter((Parameter) dObj);
                    //test2 if interface parameter rp is already mapped to another model parameter
                    if (mappings == null || mappings.size() == 0) {
                        mm.addMappings((Parameter) dObj, items);
                    } else { //pop a warning dialog
                        OneButton1Msg.showWarning(this, "Warning: mapping",
                                "Interface parameter cannot be mapped to two model parameters.",
                                "OK", new Dimension(150, 100));
                    }
//              ((DomeModel)ifaceBuilder.getModel()).getMappingManager().addMappings((Parameter)dObj,items);
                } catch (RuntimeException e) {
                    handleMappingErrors(e);
                }
            } // else can not map
        }
    }

    //used only in interface tree object
    public void addAndMapLastSelection() {
//        System.out.println("addAndMapLastSelection");
        ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
//        System.out.println("selected:"+sel);
        if (sel == null) return; // nothing in clipboard!
        addAndMapItems(sel.getItems());
    }

    //used only in interface tree object
    public void addAndMapFromClipboard() {
//        System.out.println("addAndMapFromClipboard");
        ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
        if (selections == null) return; // nothing selected in clipboard!
//        System.out.println("selected: "+Names.getNameIds(Arrays.asList(selections)));
        DSet allSelections = new DSet(); // items can not be repeated
        for (int i = 0; i < selections.length; ++i)
            allSelections.addAll(selections[i].getItems());
        addAndMapItems(allSelections);
    }

    //used only in interface tree object
    protected void addAndMapItems(List items) {
        /*System.out.println("addAndMapItems: "+Names.getNameIds(items)); */
        treeTable.stopEditing();
        DefaultContextBuilder cxt = getCurrentContextBuilder();
        ModelObjectScope scope = cxt.getScope();
        if (scope instanceof DomeModelBuilder) {
            DomeModelBuilder mod = (DomeModelBuilder) scope;
            items = filterForValidItems(mod, items);
            try {
                if (tree.isSelectionEmpty()) { // paste in top container i.e. model and its build context
                    ConnectionMappingManager mgr = mod.getMappingManager();
                    List modObjs = (List) mod.newModelObjects(items);
                    cxt.addModelObjectReferences(modObjs);
                    Iterator iter = modObjs.iterator();
                    for (Iterator it = items.iterator(); it.hasNext();) {
                        Object origObj = it.next();
                        Object newObj = iter.next();
                        if (origObj instanceof Parameter && newObj instanceof Parameter) {
                            // delete the new object if the mapping failed
                            if (!mgr.addMapping((Parameter) origObj, (Parameter) newObj))
                                mod.deleteModelObject((Parameter) origObj);
                        } else if (origObj instanceof Visualization && newObj instanceof Visualization) {
                            if (!mgr.addMapping((Visualization) origObj, (Visualization) newObj))
                                mod.deleteModelObject((Visualization) origObj);
                        }
                    }
                } else { // find insertion point
                    int[] selectedIndices = tree.getSelectionRows();
                    Arrays.sort(selectedIndices);
                    int selectedIndex = selectedIndices[0]; // first item selected
                    TreePath selectedPath = tree.getPathForRow(selectedIndex);
                    DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                    DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
                    if (dObj instanceof ProceduralRelation) {
                        addAndMapItems((ProceduralRelation) dObj, items);
                    } else if (dObj instanceof Filter) {
                        DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                        DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                        if (parentObj instanceof ProceduralRelation) {
                            addAndMapItems((ProceduralRelation) parentObj, items);
                        }
                    } else {
                        showAddandMapDialog(dObj);
                    }
                }
            } catch (RuntimeException e) {
                handleMappingErrors(e);
            }
        } else if (scope instanceof AnalysisToolBase) {
            AnalysisToolBase mod = (AnalysisToolBase) scope;
            items = filterForValidItems(mod, items);

            /**
             * First, check if the Add and Map operation will
             * not couple a variable - objective for an
             * OptimizationToolBuild object.
             * The quick check is done below.
             */

            if (scope instanceof OptimizationToolBuild) {
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    Object object = iterator.next();
                    if (((OptimizationToolBuild) scope).getOptimizationToolObjectiveParameterMap().containsKey(object) &&
                            ((OptimizationToolBuild) scope).getCurrentView().equals(OptimizationToolBuild.VARIABLE_VIEW)) {
                        OneButton1Msg.showWarning(this, "Warning: Add and Map Operation", "You are attempting to Add and Map" +
                                " an objective parameter to a variable parameter. \nYou can only use Add and Map between two parameters " +
                                " of the same type.", "Ok", OneButton1Msg.DEFAULT_SIZE);
                        return;
                    } else if (((OptimizationToolBuild) scope).getOptimizationToolVariableParameterMap().containsKey(object) &&
                            ((OptimizationToolBuild) scope).getCurrentView().equals(OptimizationToolBuild.OBJECTIVE_VIEW)) {
                        OneButton1Msg.showWarning(this, "Warning: Add and Map Operation", "You are attempting to Add and Map " +
                                "a variable parameter to an objective parameter. \nYou can only use Add and Map between two parameters " +
                                "of the same type.", "Ok", OneButton1Msg.DEFAULT_SIZE);
                        return;
                    }
                }
            }

            try {
                if (tree.isSelectionEmpty()) { // paste in top container i.e. model and its build context
                    ConnectionMappingManager mgr = mod.getMappingManager();
                    List modObjs = (List) mod.newModelObjects(items);
                    cxt.addModelObjectReferences(modObjs);
                    Iterator iter = modObjs.iterator();
                    for (Iterator it = items.iterator(); it.hasNext();) {
                        Object origObj = it.next();
                        Object newObj = iter.next();
                        if (origObj instanceof Parameter && newObj instanceof Parameter) {
// delete the new object if the mapping failed
                            if (!mgr.addMapping((Parameter) origObj, (Parameter) newObj))
                                mod.deleteModelObject((Parameter) origObj);
                        } else if (origObj instanceof Visualization && newObj instanceof Visualization) {
                            if (!mgr.addMapping((Visualization) origObj, (Visualization) newObj))
                                mod.deleteModelObject((Visualization) origObj);
                        }
                    }
                } else { // find insertion point
                    int[] selectedIndices = tree.getSelectionRows();
                    Arrays.sort(selectedIndices);
                    int selectedIndex = selectedIndices[0]; // first item selected
                    TreePath selectedPath = tree.getPathForRow(selectedIndex);
                    DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                    DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
                    if (dObj instanceof ProceduralRelation) {
                        addAndMapItems((ProceduralRelation) dObj, items);
                    } else if (dObj instanceof Filter) {
                        DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                        DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                        if (parentObj instanceof ProceduralRelation) {
                            addAndMapItems((ProceduralRelation) parentObj, items);
                        }
                    } else {
                        showAddandMapDialog(dObj);
                    }
                }
            } catch (RuntimeException e) {
                handleMappingErrors(e);
            }

        } else if (scope instanceof ToolInterface) {
            ToolInterface ifaceBuilder = (ToolInterface) scope;
            try {
                if (tree.isSelectionEmpty()) {
                    if (ifaceBuilder instanceof OptimizationInterfaceBuild) {
                        Collection c = ((OptimizationInterfaceBuild) ifaceBuilder).addAndMapModelObjects(items);
                        cxt.addModelObjectReferences(c);
                    }
                }
            } catch (RuntimeException e) {
                handleMappingErrors(e);
            }
        } else if (scope instanceof ModelInterface || scope instanceof InterfaceModelView) {
            ModelInterface ifaceBuilder = null;
            if (scope instanceof InterfaceModelView) {
                ifaceBuilder = ((InterfaceModelView) scope).getContainerInterface();
            } else {
                ifaceBuilder = (ModelInterface) scope;
            }
            items = filterForValidItems(scope, items);
            try {
                if (tree.isSelectionEmpty()) { // paste in top container
                    Collection c = ((ModelInterfaceBuilder) ifaceBuilder).addAndMapModelObjects(items);
                    cxt.addModelObjectReferences(c);
                } else { // find insertion point
                    int[] selectedIndices = tree.getSelectionRows();
                    Arrays.sort(selectedIndices);
                    int selectedIndex = selectedIndices[0]; // first item selected
                    TreePath selectedPath = tree.getPathForRow(selectedIndex);
                    DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                    if (tree.isExpanded(selectedPath)) {
                        DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
                        Collection c = ((ModelInterfaceBuilder) ifaceBuilder).addAndMapModelObjects(items);
                        if (dObj instanceof Context) {  //if nested context
                            ((Context) dObj).addModelObjectReferences(c); //add object references to it
                        } else {
                            cxt.addModelObjectReferences(c); //add to top level container
                        }
                        //Note that interface build view does not have relations.
                        //Also so not directly add and map to interface model view relations
                    } else {
                        DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
//                    DomeObject parentObj = ((DomeTreeObject)parentNode.getTreeObject()).getDomeObject();
//						                   if (ifaceBuilder.isInputFilter(parentObj)) { // selection is insertion point
                        int childIndex = parentNode.getIndex(selectedNode);
                        Collection c = ((ModelInterfaceBuilder) ifaceBuilder).addAndMapModelObjects(items, childIndex);
                        cxt.addModelObjectReferences(c);
//                    } else {
//                        ifaceBuilder.addAndMapModelObjects(items);
//						                   }
                    }
                }
            } catch (RuntimeException e) {
                handleMappingErrors(e);
            }
        }
    }

    private void addAndMapItems(ProceduralRelation rel, List items) {
        //add and map items
        Model m = rel.getModel();
        ConnectionMappingManager mgr = null;
        if (m instanceof DomeModel) {
            mgr = ((DomeModel) m).getMappingManager();
        } else if (m instanceof IntegrationProject) {
            mgr = ((IntegrationProject) m).getMappingManager();
        }
        List relObjs = new ArrayList();
        for (Iterator i = items.iterator(); i.hasNext();) {
            Object item = i.next();
            if (item instanceof Parameter) {
                Parameter relParam = (Parameter) rel.newModelObject((Parameter) item);
                relObjs.add(relParam);
                // delete the new object if the mapping failed
                if (!mgr.addMapping(relParam, (Parameter) item))
                    rel.deleteModelObject(relParam);
            }
        }
    }

    private void addAndMapItems(Subscription rel, List items) {
        //add and map items
        Model m = rel.getModel();
        ConnectionMappingManager mgr = null;
        if (m instanceof DomeModel) {
            mgr = ((DomeModel) m).getMappingManager();
        } else if (m instanceof IntegrationProject) {
            mgr = ((IntegrationProject) m).getMappingManager();
        }
        List relObjs = new ArrayList();
        for (Iterator i = items.iterator(); i.hasNext();) {
            Object item = i.next();
            if (item instanceof Parameter) {
                Parameter relParam = (Parameter) rel.newModelObject((Parameter) item);
                relObjs.add(relParam);
                // delete the new object if the mapping failed
                if (!mgr.addMapping(relParam, (Parameter) item))
                    rel.deleteModelObject(relParam);
            }
        }
    }

    protected void handleMappingErrors(RuntimeException ex) {
        System.err.println(ex);
        ex.printStackTrace();
    }

    protected boolean canMoveSingleSelection() {
        // if parent has more than one child and parent instanceof ShiftSupport
        // doesn't not enable one button and not the other
        TreePath selectedPath = tree.getSelectionPath();
        DefaultObjectTreeNode parent = (DefaultObjectTreeNode) ((TreeNode) selectedPath.getLastPathComponent()).getParent();
        DomeObject dObj = ((DomeTreeObject) parent.getTreeObject()).getDomeObject();
        return (parent.getChildCount() > 1 && (dObj instanceof ShiftSupport));
    }

    protected boolean canMoveMultipleSelections() {
        // if from same parent and parent instance of ShiftSupport
        TreePath[] selectedPaths = tree.getSelectionPaths();
        DefaultObjectTreeNode firstParent = (DefaultObjectTreeNode) ((TreeNode) selectedPaths[0].getLastPathComponent()).getParent();
        for (int i = 1; i < selectedPaths.length; ++i) {
            if (!firstParent.equals(((TreeNode) selectedPaths[i].getLastPathComponent()).getParent()))
                return false;
        }
        DomeObject dObj = ((DomeTreeObject) firstParent.getTreeObject()).getDomeObject();
        return dObj instanceof ShiftSupport;
    }

    // returns true if tree is valid, false if not
    protected boolean validateTreeSelection() {
        // if tree selection is in inconsisten state, clear Selection
        int count = tree.getSelectionCount();
        int[] selectedRows = tree.getSelectionRows();
        if ((selectedRows == null && count != 0) ||
                (selectedRows != null && (selectedRows.length != count))) {
            tree.clearSelection();
            return false;
        }
        return true;
    }

    protected void printTreeSelection() {
        int count = tree.getSelectionCount();
        System.out.println("tree selection count: " + count);
        int[] selectedRows = tree.getSelectionRows();
        if (selectedRows != null) {
            for (int i = 0; i < selectedRows.length; ++i)
                System.out.print(selectedRows[i] + "  ");
        }
        System.out.println();
    }

    class ContextBuilderTreeSelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            ModelObjectScope scope = getCurrentContextBuilder().getScope();
            if (scope instanceof DomeModelInterface || scope instanceof InterfaceModelView) {
                String context = null;
                String view = null;
                if (scope instanceof InterfaceModelView) {
                    DomeModelInterface iface = (DomeModelInterface) ((InterfaceModelView) scope).getContainerInterface();
                    view = iface.getCurrentView();
                } else {
                    view = ((DomeModelInterface) scope).getCurrentView();
                }
                if (view.equals(DomeModelInterface.BUILD_VIEW)) {
                    context = ModeContexts.BUILD_DOMEMODEL_INTERFACE_BUILDVIEW;
                } else if (view.equals(DomeModelInterface.MODEL_VIEW)) {
                    context = ModeContexts.BUILD_DOMEMODEL_INTERFACE_MODELVIEW;
                }
                if (scope instanceof InterfaceModelView) {
                    setEditMenusForSelection(context,
                            ((InterfaceModelView) scope).isDefaultInterface());
                } else {
                    setEditMenusForSelection(context,
                            ((DomeModelInterface) scope).isDefaultInterface());
                }
            } else if (scope instanceof AnalysisTool) {
                setQMOOModelEditMenusForSelection();
            } else if (scope instanceof PluginModel) {
                setPluginModelEditMenusForSelection();
            } else { //Dome Model
                TreePath path = e.getNewLeadSelectionPath();
                if (path != null) {
                    Object obj = path.getLastPathComponent();
                    if (obj != null && obj instanceof BuildObjectTreeNode) {
                        DomeObject dobject = ((BuildObjectTreeNode) obj).getDomeObject();
                        if (dobject instanceof Relation) {
                            ((DomeModelBuilder) scope).setRelationToTest((Relation) dobject);
                            DomeModelBuildPanel.testRelationAction.setEnabled(true);
                            DomeModelBuildPanel.disableViewMenu();
                        } else {
//System.out.println("disable test2 relation");
//((DomeModelBuilder) scope).setRelationToTest(null);
//todo line above causes exception if select a parameter in an open context GUI that is from a subscription
                            DomeModelBuildPanel.testRelationAction.setEnabled(false);

                            if (dobject instanceof Subscription) {
                                String view = ((Subscription) dobject).getCurrentView();
                                DomeModelBuildPanel.setSubscriptionView(view);
                                DomeModelBuildPanel.enableViewMenu();
                            } else {
                                DomeModelBuildPanel.disableViewMenu();
                            }
                        }
                    }
                } else {
//System.out.println("disable test2 relation");
//((DomeModelBuilder) scope).setRelationToTest(null);
//todo line above causes exception if select a parameter in an open context GUI that is from a subscription
                    DomeModelBuildPanel.testRelationAction.setEnabled(false);
                    DomeModelBuildPanel.disableViewMenu();
                }
                setEditMenusForSelection();
            }
        }
    }

    public void switchSubscriptionView(String view) {
        int selCount = tree.getSelectionCount();
        if (selCount == 1) {
            BuildObjectTreeNode selectedNode = (BuildObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
            TreeObject tob = selectedNode.getTreeObject();
            if (tob instanceof SubscriptionTreeObject) {
                SubscriptionTreeObject tobj = (SubscriptionTreeObject) tob;
                Object o = tobj.getDomeObject();
                ((DefaultSubscription) o).setView(view);
            } else {
                showinterfaceViewdialog();
            }
        } else {
            showinterfaceViewdialog();
        }
    }

    private void showinterfaceViewdialog() {
        String msg = "Please select a subscription to change the view.";
        OneButton1Msg.showWarning(BuildFocusTracker.getCurrentComponent(), "View warning",
                msg, "OK", new Dimension(1, 1));
    }

    //Qing add for plugin model copy/paste support
    private boolean isInPluginModel() {
        return getCurrentContextBuilder().getModel() instanceof PluginModelBuilder;
    }

    /**
     * the method works to filter valid datatype supportted by plugin model
     * @param items
     */
    private List filterValidDataTypes(List items) {

        //first decide which type of plugin it is
        PluginModelBuilder pModel = (PluginModelBuilder) getCurrentContextBuilder().getModel();
        String type = pModel.getPluginTypeName();
        List[] validated_and_invalidated = PluginUtils.FilterValidDataTypes(type, items);
        List validated = validated_and_invalidated[0];
        List invalidated = validated_and_invalidated[1];
        if (!invalidated.isEmpty()) {
            DefaultContextBuilder.showWarning(this, INVALID_PASTE, getNamesOfChildren(invalidated),
                    pModel.getName());
        }
        return validated;
    }




////---------------Integration Wizard (automated mapping) starts here---------------------------------
    public void automatedMapping(){
    ModelObjectScope scope = getCurrentContextBuilder().getScope();
    if (scope instanceof DomeModelBuilder) {
        DomeModelBuilder modelBuilder = (DomeModelBuilder) scope;
        Collection modelObjects = modelBuilder.getModelObjects();
        ArrayList subscriptions = new ArrayList();
        for(Iterator objectIterator=modelObjects.iterator();objectIterator.hasNext();)
        {
            Object object = objectIterator.next();
            if(object instanceof DefaultSubscription)
                subscriptions.add(object);
        }
        wizardFrame = new IntegrationWizardFrame(subscriptions);

        wizardFrame.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent event) {
                    implementMappings(wizardFrame.getAcceptedMappings(),wizardFrame.getObjectiveModels());
                    wizardFrame = null;
                }
            });
    }
}

    //implements automapping
    public void implementMappings(MappingMatrix matrix,ArrayList objectiveModels){
        if(matrix!=null){
            int numModels = matrix.numModels();
            for (int columnIndex=0;columnIndex<numModels;columnIndex++){
                for (int rowIndex=(columnIndex+1);rowIndex<numModels;rowIndex++){
                    Object entry = matrix.getEntry(columnIndex,rowIndex);
                    if(entry instanceof ModelMapping){
                        ModelMapping map = (ModelMapping)entry;
                        FuzzyARG columnModel = (FuzzyARG)objectiveModels.get(columnIndex);
                        FuzzyARG rowModel = (FuzzyARG)objectiveModels.get(rowIndex);
                        ArrayList outputMappings = map.getOutputParameterMapping();
                        ArrayList inputMappings = map.getInputParameterMapping();
                        if(inputMappings.size()>0)
                            mapInputsInputs(columnModel,rowModel,inputMappings);
                        if(outputMappings.size()>0)
                            mapInputsOutputs(columnModel,rowModel,outputMappings);
                    }
                }
            }
        }
    }

    //implements input to output mappings
    private void mapInputsOutputs(FuzzyARG columnModel,FuzzyARG rowModel,ArrayList mappings){
        ConnectionMappingManager mm = null;
        ModelObjectScope scope = getCurrentContextBuilder().getScope();
        if (scope instanceof DomeModelBuilder) {
            DomeModelBuilder mod = (DomeModelBuilder) scope;
            mm = mod.getMappingManager();
            for(int mapIndex=0;mapIndex<mappings.size();mapIndex++){
                ParameterPair pair = (ParameterPair)mappings.get(mapIndex);
                Parameter columnParameter = columnModel.getCorrespondingParameter(pair.getColumnNode());
                Parameter rowParameter = rowModel.getCorrespondingParameter(pair.getRowNode());
                if(columnParameter instanceof InterfaceParameterClient){
                    InterfaceParameterClient param = (InterfaceParameterClient)columnParameter;
                    columnParameter = getConcreteParameterForInterfaceParameter(param,columnModel.id);
                }
                if(rowParameter instanceof InterfaceParameterClient){
                    InterfaceParameterClient param = (InterfaceParameterClient)rowParameter;
                    rowParameter = getConcreteParameterForInterfaceParameter(param,rowModel.id);
                }
                if(rowParameter!=null && columnParameter!=null)
                    mm.addMapping(columnParameter,rowParameter);
                else
                    OneButton1Msg.showWarning(null, "Warning: Unknown Mapping Error" ,"Error mapping parameters:\n"+
                      pair.getColumnNode().getName().getSampleSet().iterator().next() + " and "+
                      pair.getRowNode().getName().getSampleSet().iterator().next(), "Ok", OneButton1Msg.DEFAULT_SIZE);
            }
        }
    }

    //implements input to input mappings
    private void mapInputsInputs(FuzzyARG columnModel,FuzzyARG rowModel,ArrayList mappings)
    {
        ConnectionMappingManager mm = null;
        ModelObjectScope scope = getCurrentContextBuilder().getScope();
        if (scope instanceof DomeModelBuilder) {
            DomeModelBuilder mod = (DomeModelBuilder) scope;
            mm = mod.getMappingManager();

            for(int mapIndex=0;mapIndex<mappings.size();mapIndex++){
                ParameterPair pair = (ParameterPair)mappings.get(mapIndex);
                Parameter columnParameter = columnModel.getCorrespondingParameter(pair.getColumnNode());
                Parameter rowParameter = rowModel.getCorrespondingParameter(pair.getRowNode());
                if(columnParameter instanceof InterfaceParameterClient){
                    InterfaceParameterClient param = (InterfaceParameterClient)columnParameter;
                    columnParameter = getConcreteParameterForInterfaceParameter(param,columnModel.id);
                }
                if(rowParameter instanceof InterfaceParameterClient){
                    InterfaceParameterClient param = (InterfaceParameterClient)rowParameter;
                    rowParameter = getConcreteParameterForInterfaceParameter(param,rowModel.id);
                }
                Parameter copy = addCopy(rowParameter);
                if(rowParameter!=null && columnParameter!=null && copy!=null){
                    mm.addMapping(columnParameter,copy);
                    mm.addMapping(rowParameter,copy);
                }
                else
                    OneButton1Msg.showWarning(null, "Warning: Unknown Mapping Error" ,"Error mapping parameters:\n"+
                      pair.getColumnNode().getName().getSampleSet().iterator().next() + " and "+
                      pair.getRowNode().getName().getSampleSet().iterator().next(), "Ok", OneButton1Msg.DEFAULT_SIZE);
            }
        }
    }

    //adds and returns a copy of one of the input to input parameters to be mapped
    private Parameter addCopy(Parameter parameter)
    {
        List copies = new ArrayList();
        copies.add(parameter);
        boolean deepCopy = shouldDeepCopy(copies);
        Collection newCopies = getCurrentContextBuilder().addModelObjectCopies(copies, deepCopy);
        return (Parameter)newCopies.iterator().next();
    }

    private ConcreteParameter getConcreteParameterForInterfaceParameter(InterfaceParameterClient param,String ifaceId){
        ModelObjectScope scope = getCurrentContextBuilder().getScope();
        if (scope instanceof DomeModelBuilder) {
            DomeModelBuilder modelBuilder = (DomeModelBuilder) scope;
            Collection modelObjects = modelBuilder.getModelObjects();
            for(Iterator objectIterator=modelObjects.iterator();objectIterator.hasNext();){
                Object object = objectIterator.next();
                if(object instanceof DefaultSubscription){
                    DefaultSubscription sub = (DefaultSubscription)object;
                    HashMap idMap = sub.getParamIdMap();
                    sub.getIfaceId();
                    if(sub.getIfaceId().equals(ifaceId)){
                        Set idSet = idMap.keySet();
                        for(Iterator iterator=idSet.iterator();iterator.hasNext();){
                            String id = (String)iterator.next();
                            if(idMap.get(id).equals(param.getId().toString())){
                                Object modelObject = sub.getModelObjectById(new Id(id));
                                if(modelObject instanceof ConcreteParameter)
                                    return (ConcreteParameter)modelObject;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
///////------------------End automated mappings -------------------------










    // --- focus tracking support --------------------
    public static abstract class FocusTrackerAction extends AbstractAction {

        public FocusTrackerAction(String name) {
            super(name);
        }

        protected final ContextTreeBuilderPanel getContextTreeBuilderPanel(ActionEvent e) {
            if (e != null) {
                Object o = e.getSource();
                if (o instanceof ContextTreeBuilderPanel) {
                    return (ContextTreeBuilderPanel) o;
                }
            }
            JComponent comp = BuildFocusTracker.getCurrentComponent();
            if (comp instanceof ContextTreeBuilderPanel)
                return (ContextTreeBuilderPanel) comp;
            throw new NullPointerException("No current ContextTreeBuilderPanel");
        }
    }

    public static class AddItemAction extends FocusTrackerAction {
        public AddItemAction(String itemType) {
            super(itemType);
        }

        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).addNewModelObject((String) getValue(AbstractAction.NAME));
        }
    }

    // --- actions for menus and buttons --------------------
    public static final AbstractAction copyAction = new FocusTrackerAction("Copy") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).copySelectedModelObjects();
        }
    };

    public static final AbstractAction cutAction = new FocusTrackerAction("Cut") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).cutSelectedModelObjects();
        }
    };

    public static final AbstractAction pasteCopyLastSelectionAction = new FocusTrackerAction("Last selection") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).pasteCopyLastSelection();
        }
    };

    public static final AbstractAction pasteCopyClipboardAction = new FocusTrackerAction("Clipboard...") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).pasteCopyFromClipboard();
        }
    };

    public static final AbstractAction pasteReferenceLastSelectionAction = new FocusTrackerAction("Last selection") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).pasteReferenceLastSelection();
        }
    };

    public static final AbstractAction pasteReferenceClipboardAction = new FocusTrackerAction("Clipboard...") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).pasteReferenceFromClipboard();
        }
    };

    public static final AbstractAction removeAction = new FocusTrackerAction("Remove") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).removeSelectedModelObjects();
        }
    };

    public static final AbstractAction deleteAction = new FocusTrackerAction("Delete") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).deleteSelectedModelObjects();
        }
    };

    public static final AbstractAction clearSelectionAction = new FocusTrackerAction("Clear selection") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).getContextTree().clearSelection();
        }
    };

    public static final AbstractAction selectAllAction = new FocusTrackerAction("Select all") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).getContextTree().selectAllVisibleRows();
        }
    };

    public static final AbstractAction mapLastSelectionAction = new FocusTrackerAction("Last selection") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).mapLastSelection();
        }
    };

    public static final AbstractAction mapClipboardAction = new FocusTrackerAction("Clipboard...") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).mapFromClipboard();
        }
    };

    public static final AbstractAction addAndMapLastSelectionAction = new FocusTrackerAction("Last selection") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).addAndMapLastSelection();
        }
    };

    public static final AbstractAction integrationWizardAction = new FocusTrackerAction("Integration Wizard"){
        public void actionPerformed(ActionEvent e)
		{
			getContextTreeBuilderPanel(e).automatedMapping();
        }
    };

    public static final AbstractAction addAndMapClipboardAction = new FocusTrackerAction("Clipboard...") {
        public void actionPerformed(ActionEvent e) {
            getContextTreeBuilderPanel(e).addAndMapFromClipboard();
        }
    };


}
