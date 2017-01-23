// DefaultContextBuilder.java
package mit.cadlab.dome3.objectmodel.modelobject.context;

import mit.cadlab.dome3.gui.guiutils.msg.FourButton2Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton3Msg;
import mit.cadlab.dome3.gui.guiutils.msg.ThreeButton0Msg;
import mit.cadlab.dome3.gui.guiutils.msg.ThreeButton2Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.exceptions.DuplicateContentError;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.exceptions.IllegalCrossModelPasteError;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.exceptions.IllegalRecursiveContextError;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.exceptions.NoReferenceWarning;
import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.exceptions.NoReferenceException;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.AbstractModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.MultipleErrorsException;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DefaultContextBuilder extends DefaultContext implements ContextBuilder {

    protected static final Dimension WARNING_SIZE1 = new Dimension(280, 130);
    protected static final Dimension WARNING_SIZE2 = new Dimension(250, 100);
    protected static final String CONTEXT_PASTE_RECURSIVE = "Reference cannot be pasted.\nIt would create a recursive reference.";
    protected static final String CONTEXT_PASTE_DUPLICATE = "Reference cannot be pasted.\nIt would create a duplicate reference.";
    protected static final String CROSS_MODEL_REFERENCE_PASTE = "Reference cannot be pasted here because\nit is in different model.";

    public DefaultContextBuilder(ModelObjectScope m, Id id) {
        super(m, id);
    }

    public DefaultContextBuilder(ModelObjectScope m, Id id, Context context) {
        super(m, id, context);
    }

    public DefaultContextBuilder(ModelObjectScope m, Element eml) {
        this(m,eml,true);
    }

	public DefaultContextBuilder(ModelObjectScope m, Element eml, boolean loadReferences)
	{
		super(m, eml, loadReferences);
	}

    protected void handleAddReferenceErrors(Exception ex) {
        if (ex instanceof MultipleErrorsException) {
            List errors = ((MultipleErrorsException) ex).getErrorList();
            int printOuts = 0;
            for (int i = 0; i < errors.size(); ++i) {
                Object error = errors.get(i);
                if (error instanceof DArrayList.BeforeHookException)
                    handleBeforeHookException((DArrayList.BeforeHookException) error);
                else {
                    System.err.println("add reference error: " + error.toString());
                    printOuts++;
                }
            }
            if (printOuts > 0)
                OneButton1Msg.showError(getParentComponent(), "Add reference error",
                        "Exception adding reference.\nSee message log for details.",
                        "OK", WARNING_SIZE2);
        } else if (ex instanceof DArrayList.BeforeHookException) {
            handleBeforeHookException((DArrayList.BeforeHookException) ex);
        } else {
            System.err.println("add reference error: " + ex.toString());
            OneButton1Msg.showError(getParentComponent(), "Add reference error",
                    "Exception adding reference.\nSee message log for details.",
                    "OK", WARNING_SIZE2);
        }
    }

    protected void handleBeforeHookException(DArrayList.BeforeHookException ex) {
        if (ex.exception instanceof IllegalCrossModelPasteError) {
            showWarning(getParentComponent(), CROSS_MODEL_REFERENCE_PASTE,
                    ((ModelObject) ex.object).getName(),
                    ((ModelObject) ex.object).getScope().getName());
        } else if (ex.exception instanceof DuplicateContentError) {
            showWarning(getParentComponent(), CONTEXT_PASTE_DUPLICATE,
                    ((ModelObject) ex.object).getName(),
                    getName());
        } else if (ex.exception instanceof IllegalRecursiveContextError) {
            showWarning(getParentComponent(), CONTEXT_PASTE_RECURSIVE,
                    ((ModelObject) ex.object).getName(),
                    getName());
        } else {
            OneButton1Msg.showError(getParentComponent(), "Add reference error",
                    CONTEXT_PASTE_DUPLICATE,
                    "OK", WARNING_SIZE2);
        }
    }

    public static void showWarning(Component comp, String msg, String item1, String item2) {
        OneButton3Msg.showWarning(comp, "Warning: illegal operation",
                msg, item1, item2, "OK", WARNING_SIZE1);
    }

    public static void showWarning(Component comp, String msg, String item1) {
        OneButton2Msg.showWarning(comp, "Warning: illegal operation",
                msg, item1, "OK", WARNING_SIZE2);
    }

    public void removeModelObjectReference(ModelObject mObj) {
        try {
            super.removeModelObjectReference(mObj);
        } catch (Exception ex) {
            if (ex instanceof MultipleErrorsException) {
                MultipleErrorsException meex = (MultipleErrorsException) ex;
                List noReferenceWarnings = getNoReferenceWarnings(meex);
                if (noReferenceWarnings.size() == 1) {
//                    handleNoReferenceWarning((NoReferenceWarning) noReferenceWarnings.get(0));
                    if (mObj.getScope() instanceof Model) {
                        handleNoReferenceWarning((NoReferenceException) noReferenceWarnings.get(0));
                    }
                } else if (noReferenceWarnings.size() > 1) {
                    if (mObj.getScope() instanceof Model) {
                        handleNoReferenceWarnings(noReferenceWarnings);
                    }
                } // should probably catch exceptions here...
                if (meex.getErrorList().size() != 0)
                    throw new AbstractDomeObject.DomeObjectException("removeModelObjectReference", ex);
            } else if (ex instanceof DArrayList.AfterHookException) {
                DArrayList.AfterHookException afex = (DArrayList.AfterHookException) ex;
                List noReferenceWarnings = getNoReferenceWarnings(afex);
                if (noReferenceWarnings.size() == 1) {
//                    handleNoReferenceWarning((NoReferenceWarning) noReferenceWarnings.get(0));
                    if (mObj.getScope() instanceof Model) {
                        handleNoReferenceWarning((NoReferenceException) noReferenceWarnings.get(0));
                    }
                } else {
                    throw new AbstractDomeObject.DomeObjectException("removeModelObjectReference", ex);
                }
            } else {
                throw new AbstractDomeObject.DomeObjectException("removeModelObjectReference", ex);
            }
        }
    }

    public void removeModelObjectReferences(List modelObjects) {
        try {
            super.removeModelObjectReferences(modelObjects);
        } catch (Exception ex) {
            if (ex instanceof MultipleErrorsException) {
                MultipleErrorsException meex = (MultipleErrorsException) ex;
                List noReferenceWarnings = getNoReferenceWarnings(meex);
                //assumption - all selected objects belong to the same scope
                ModelObjectScope scope = ((ModelObject) modelObjects.get(0)).getScope();
                if (noReferenceWarnings.size() == 1) {
//                    handleNoReferenceWarning((NoReferenceWarning) noReferenceWarnings.get(0));
                    if (scope instanceof Model) {
                        handleNoReferenceWarning((NoReferenceException) noReferenceWarnings.get(0));
                    }
                } else if (noReferenceWarnings.size() > 1) {
                    if (scope instanceof Model) {
                        handleNoReferenceWarnings(noReferenceWarnings);
                    }
                } // should probably catch exceptions here...
                if (meex.getErrorList().size() != 0)
                    throw new AbstractDomeObject.DomeObjectException("removeModelObjectReferences", ex);
            } else if (ex instanceof DArrayList.AfterHookException) {
                DArrayList.AfterHookException afex = (DArrayList.AfterHookException) ex;
                List noReferenceWarnings = getNoReferenceWarnings(afex);
                if (noReferenceWarnings.size() == 1 && (scope instanceof Model)) {
                    NoReferenceException noRefEx = (NoReferenceException) noReferenceWarnings.get(0);
                    handleNoReferenceWarning(new NoReferenceWarning(noRefEx));
                } else if (noReferenceWarnings.size() == 1 && (scope instanceof ModelInterfaceBuilder)) {
                    ((ModelInterfaceBuilder) scope).removeModelObjects(modelObjects);
                } else {
                    throw new AbstractDomeObject.DomeObjectException("removeModelObjectReference", ex);
                }
            } else {
                throw new AbstractDomeObject.DomeObjectException("removeModelObjectReferences", ex);
            }
        }
    }

    protected static final String DELETE_TITLE1 = "Options: delete context ";
    protected static final String DELETE_TITLE2 = "Options: no reference to context ";
    protected static final String DELETE_ALL_MSG = "delete everything in context";
    protected static final String DELETE_LAST_MSG = "delete contents not used elsewhere";
    protected static final String DELETE_CONTEXT_MSG = "delete context only";

    protected static final String REMOVE_TITLE = "Options: remove context";
    protected static final String REMOVE_MSG = "is no longer referenced elsewhere";
    protected static final String REMOVE_TOP_BUTTON = "keep context object and its contents";
    protected static final String REMOVE_UP_MID_BUTTON = "delete context, keep its contents";
    protected static final String REMOVE_LOW_MID_BUTTON = "delete context and contents not used elsewhere";
    protected static final String REMOVE_BOTTOM_BUTTON = "delete context and all its contents";
    protected static final Dimension REMOVE_SIZE = new Dimension(565, 130);
    protected static final Dimension REMOVE_SIZE2 = new Dimension(300, 100);

    public synchronized void removeDeletionListener(DeletionListener l) {
        try {
            super.removeDeletionListener(l);
        } catch (NoReferenceWarning ex) {
            if (modelObjectReferences.size() > 0) {
                int answer = FourButton2Msg.showOption(getParentComponent(), REMOVE_TITLE, REMOVE_MSG, getName(),
                        REMOVE_TOP_BUTTON, REMOVE_UP_MID_BUTTON, REMOVE_LOW_MID_BUTTON, REMOVE_BOTTOM_BUTTON, REMOVE_SIZE);
                switch (answer) {
                    case FourButton2Msg.TOP_OPTION: // do not delete
                        return;
                    case FourButton2Msg.LOWER_MIDDLE_OPTION: // delete last references
                        delete(null, true);
                        return;
                    case FourButton2Msg.BOTTOM_OPTION: // delete everything
                        recursiveDelete(null);
                        return;
                }
            } else {
                int answer = TwoButton2Msg.showOption(getParentComponent(), REMOVE_TITLE, REMOVE_MSG, getName(),
                        "keep context", "delete context", REMOVE_SIZE2);
                switch (answer) {
                    case TwoButton2Msg.LEFT_OPTION: // do not delete
                        return;
                    default: // delete everything
                        recursiveDelete(null);
                        return;
                }
            }
            // FourButton2Msg.UPPER_MIDDLE_OPTION: delete context, keep contents, prompt if no references
            deleteAction(null);
        }
    }

    public synchronized void delete(DeletionListener notifier) {
        if (isDeleted) return;
        if (modelObjectReferences.size() > 0) {
            int answer = ThreeButton0Msg.showOption(getParentComponent(), DELETE_TITLE1 + getName(),
                    DELETE_ALL_MSG, DELETE_LAST_MSG, DELETE_CONTEXT_MSG, ThreeButton0Msg.DEFAULT_SIZE);
            switch (answer) {
                case ThreeButton0Msg.CANCEL_OPTION:
                    return;
                case ThreeButton0Msg.TOP_OPTION: // delete everything
                    recursiveDelete(notifier);
                    return;
                case ThreeButton0Msg.MIDDLE_OPTION: // delete last references
                    delete(notifier, true);
                    return;
            }
        }
        // default: ThreeButton0Msg.BOTTOM_OPTION: // delete context only
        deleteAction(notifier);
    }

    protected void deleteAction(DeletionListener notifier) {
        try {
            super.delete(notifier);
        } catch (Exception ex) {
            if (ex instanceof MultipleErrorsException) {
                MultipleErrorsException meex = (MultipleErrorsException) ex;
                List noReferenceWarnings = getNoReferenceWarnings(meex);
                if (noReferenceWarnings.size() == 1) {
//                    handleNoReferenceWarning((NoReferenceWarning) noReferenceWarnings.get(0));
                    handleNoReferenceWarning((NoReferenceException) noReferenceWarnings.get(0));
                } else if (noReferenceWarnings.size() > 1) {
                    handleNoReferenceWarnings(noReferenceWarnings);
                } // should probably catch exceptions here...
                if (meex.getErrorList().size() != 0)
                    throw new AbstractDomeObject.DomeObjectException("delete", ex);
            } else if (ex instanceof DArrayList.AfterHookException) {
                DArrayList.AfterHookException afex = (DArrayList.AfterHookException) ex;
                List noReferenceWarnings = getNoReferenceWarnings(afex);
                if (noReferenceWarnings.size() == 1) {
//                    handleNoReferenceWarning((NoReferenceWarning) noReferenceWarnings.get(0));
                    handleNoReferenceWarning((NoReferenceException) noReferenceWarnings.get(0));
                } else {
                    throw new AbstractDomeObject.DomeObjectException("removeModelObjectReference", ex);
                }
            } else { // don't know what this could be...
                throw new AbstractDomeObject.DomeObjectException("delete", ex);
            }
        }
    }

    protected static final String NOREF_TITLE1 = "Options: Context remove";
    protected static final String NOREF_MSG1 = "is no longer referenced elsewhere.";
    protected static final String NOREF_OK = "delete object";
    protected static final String NOREF_CANCEL = "keep object";
    protected static final Dimension NOREF_SIZE1 = new Dimension(310, 90);

    protected void handleNoReferenceWarning(NoReferenceWarning ex) {
        handleNoReferenceWarning(ex.modelObject);
    }

    public void handleNoReferenceWarning(NoReferenceException ex) {
        handleNoReferenceWarning(ex.modelObject);
    }

    private void handleNoReferenceWarning(ModelObject modelObject) {
        int answer = TwoButton2Msg.showOption(getParentComponent(), NOREF_TITLE1,
                NOREF_MSG1, modelObject.getScope().getName() + " " +
                modelObject.getName(),
                NOREF_OK, NOREF_CANCEL, NOREF_SIZE1);
        if (answer == TwoButton2Msg.LEFT_OPTION) {
            modelObject.delete(modelReference);
	        ModelObjectScope scope = this.getScope();
	        if((scope instanceof DomeModel) && (modelObject instanceof Parameter)) {
		        ConnectionMappingManager mgr = ((DomeModel)scope).getMappingManager();
		        mgr.removeAllMappings((Parameter)modelObject);
	        }
        } // otherwise, keep it
        else {
            /*
            // remove the incoming mappings from all the model objects
            // keep the outgoing mappings in case this object is pasted back into the model
            if (getModel() instanceof DomeModel) {
                DomeModel model = (DomeModel) getModel();
                ConnectionMappingManager mgr = model.getMappingManager();
                removeIncomingMappingsandConnections(mgr, Collections.singletonList(modelObject));
            }
            */
        }
    }


    protected static final String NOREF_TITLE = "Options: Context remove";
    protected static final String NOREF_MSG = "are no longer referenced elsewhere.";
    protected static final String NOREF_TOP_OPTION = "delete all objects";
    protected static final String NOREF_MIDDLE_OPTION = "keep all objects";
    protected static final String NOREF_BOTTOM_OPTION = "decide individually";
    protected static final Dimension NOREF_SIZE = new Dimension(430, 100);

    public static int showMultipleNoReferencesOptions(Component parent, String objectNames) {
        return ThreeButton2Msg.showOption(parent, NOREF_TITLE, NOREF_MSG, objectNames,
                NOREF_TOP_OPTION, NOREF_MIDDLE_OPTION, NOREF_BOTTOM_OPTION,
                NOREF_SIZE);
    }

    protected void handleNoReferenceWarnings(List ex) {
        String names = getNamesFromNoReferencesList(ex);
        int answer = showMultipleNoReferencesOptions(getParentComponent(), names);
        switch (answer) {
            case ThreeButton2Msg.TOP_OPTION: // delete all objects
                deleteObjectsInNoReferencesList(ex);
                return;
            case ThreeButton2Msg.MIDDLE_OPTION: // keep all objects
                return;
            default: // prompt option for each object
                promptOptionsForObjectsInNoReferencesList(ex);
        }
    }

    protected String getNamesFromNoReferencesList(List ex) {
        if (ex == null || ex.size() == 0) return "";
        if (ex.size() == 1) return ((NoReferenceWarning) ex.get(0)).modelObject.getName();
        if (ex.size() == 2)
            return ((NoReferenceException) ex.get(0)).modelObject.getName() +
                    " and " + ((NoReferenceException) ex.get(1)).modelObject.getName();
        // 3 or more objects
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < ex.size() - 1; ++i) {
            sb.append(((NoReferenceWarning) ex.get(i)).modelObject.getName() + ", ");
        }
        sb.append("and " + ((NoReferenceWarning) ex.get(ex.size() - 1)).modelObject.getName());
        return sb.toString();
    }

    protected void deleteObjectsInNoReferencesList(List ex) {
        Iterator it = ex.iterator();
        while (it.hasNext()) {
            ((NoReferenceWarning) it.next()).modelObject.delete(null);
        }
    }

    protected void promptOptionsForObjectsInNoReferencesList(List ex) {
        Iterator it = ex.iterator();
        while (it.hasNext()) {
            handleNoReferenceWarning((NoReferenceWarning) it.next());
        }
    }

    protected JComponent getParentComponent() {
        return null;
    }

    // ContextBuilder interface
    public void addNewModelObject(String modelObjectType) {
        ArrayList errors = new ArrayList();
        ModelObject mObj = null;
        try {
            ModelObjectScope scope = getScope();
//Qing change Sep 22, should treat iteration relation differently
            if (modelObjectType.equals(IterationRelation.WHILE_LOOP) || modelObjectType.equals(IterationRelation.DO_WHILE_LOOP) || modelObjectType.equals(IterationRelation.Timestep_LOOP)) {
                mObj = scope.newModelObject(IterationRelation.TYPE_INFO.getTypeName());
                if (mObj != null)
                    ((IterationRelation) mObj).setIterationType(modelObjectType);
                mObj.setName(IterationRelation.TYPE_INFO.getTypeName() +" "+ modelObjectType);
            } else
                mObj = scope.newModelObject(modelObjectType);
            //Qing change Sep 22, shoudl check equal relation first
            if (scope instanceof DomeModelBuilder && mObj instanceof EqualRelation) {
                ((DomeModelBuilder) scope).addListToCausalFilters((EqualRelation) mObj);
            } else if (scope instanceof DomeModelBuilder && mObj instanceof ProceduralRelation) {
                ((DomeModelBuilder) scope).addListToCausalFilters((ProceduralRelation) mObj);
            } else if (scope instanceof DomeModelBuilder && mObj instanceof Subscription) {
                ((DomeModelBuilder) scope).addListToCausalFilters((Subscription) mObj);
            }
        } catch (AbstractDomeObject.DomeObjectException ex) {
            mObj = (ModelObject) ex.getMethodResult();
            errors.add(ex);
        } catch (Exception ex) {
            errors.add(ex);
        }
        if (mObj != null) {
            try {
                modelObjectReferences.add(mObj);
            } catch (Exception ex) {
                errors.add(ex);
            }
        }
        if (errors.size() == 1) {
            ((Exception) errors.get(0)).printStackTrace();
            throw new AbstractDomeObject.DomeObjectException("addNewModelObject", (Exception) errors.get(0));
        } else if (errors.size() > 1)
            throw new AbstractDomeObject.DomeObjectException("addNewModelObject", new MultipleErrorsException(errors));
    }

    public void addNewModelObject(String modelObjectType, int index) {
        ArrayList errors = new ArrayList();
        ModelObject mObj = null;
        try {
            ModelObjectScope scope = getScope();
            //Qing change Sep 22, should treat iteration relation differently
            if (modelObjectType.equals(IterationRelation.WHILE_LOOP) || modelObjectType.equals(IterationRelation.DO_WHILE_LOOP) || modelObjectType.equals(IterationRelation.Timestep_LOOP)) {
                mObj = scope.newModelObject(IterationRelation.TYPE_INFO.getTypeName());
                if (mObj != null)
                {
                    ((IterationRelation) mObj).setIterationType(modelObjectType);
                    mObj.setName(IterationRelation.TYPE_INFO.getTypeName() +" "+ modelObjectType);
                }

            } else
                mObj = scope.newModelObject(modelObjectType);
            if (scope instanceof DomeModelBuilder && mObj instanceof EqualRelation) {
                ((DomeModelBuilder) scope).addListToCausalFilters((EqualRelation) mObj);
            } else if (scope instanceof DomeModelBuilder && mObj instanceof ProceduralRelation) {
                ((DomeModelBuilder) scope).addListToCausalFilters((ProceduralRelation) mObj);
            } else if (scope instanceof DomeModelBuilder && mObj instanceof Subscription) {
                ((DomeModelBuilder) scope).addListToCausalFilters((Subscription) mObj);
            }
        } catch (AbstractDomeObject.DomeObjectException ex) {
            mObj = (ModelObject) ex.getMethodResult();
            errors.add(ex);
        } catch (Exception ex) {
            errors.add(ex);
        }
        if (mObj != null) {
            try {
                modelObjectReferences.add(index, mObj);
            } catch (Exception ex) {
                errors.add(ex);
            }
        }
        if (errors.size() == 1)
            throw new AbstractDomeObject.DomeObjectException("addNewModelObject", (Exception) errors.get(0));
        else if (errors.size() > 1)
            throw new AbstractDomeObject.DomeObjectException("addNewModelObject", new MultipleErrorsException(errors));
    }

    public void addModelObjectReference(ModelObject modelObject, int index) {
        try {
            modelObjectReferences.add(index, modelObject);
        } catch (Exception ex) {
            handleAddReferenceErrors(ex);
        }
    }

    public void addModelObjectReferences(Collection modelObjects, int index) {
        try {
            modelObjectReferences.addAll(index, modelObjects);
        } catch (Exception ex) {
            handleAddReferenceErrors(ex);
        }
    }

    public void addModelObjectCopy(ModelObject modelObject, boolean deepCopy) {
        ArrayList errors = new ArrayList();
        ModelObject mObj = null;
        try {
            ModelObjectScope scope = getScope();
            mObj = scope.newModelObject(modelObject, deepCopy);
            if (scope instanceof DomeModelBuilder && mObj instanceof EqualRelation) {
                ((DomeModelBuilder) scope).addListToCausalFilters((EqualRelation) mObj);
            } else if (scope instanceof DomeModelBuilder && mObj instanceof ProceduralRelation) {
                ((DomeModelBuilder) scope).addListToCausalFilters((ProceduralRelation) mObj);
            } else if (scope instanceof DomeModelBuilder && mObj instanceof Subscription) {
                ((DomeModelBuilder) scope).addListToCausalFilters((Subscription) mObj);
            }
        } catch (AbstractDomeObject.DomeObjectException ex) {
            mObj = (ModelObject) ex.getMethodResult();
            errors.add(ex);
        } catch (Exception ex) {
            errors.add(ex);
        }
        if (mObj != null) {
            try {
                mObj.setName(mObj.getName() + COPY);
                modelObjectReferences.add(mObj);
            } catch (Exception ex) {
                errors.add(ex);
            }
        }
        if (errors.size() == 1)
            throw new AbstractDomeObject.DomeObjectException("addModelObjectCopy", (Exception) errors.get(0));
        else if (errors.size() > 1)
            throw new AbstractDomeObject.DomeObjectException("addModelObjectCopy", new MultipleErrorsException(errors));
    }

    public void addModelObjectCopy(ModelObject modelObject, int index, boolean deepCopy) {
        ArrayList errors = new ArrayList();
        ModelObject mObj = null;
        try {
            ModelObjectScope scope = getScope();
            mObj = scope.newModelObject(modelObject, deepCopy);
            if (scope instanceof DomeModelBuilder && mObj instanceof ProceduralRelation) {
                ((DomeModelBuilder) scope).addListToCausalFilters((ProceduralRelation) mObj);
            } else if (scope instanceof DomeModelBuilder && mObj instanceof EqualRelation) {
                ((DomeModelBuilder) scope).addListToCausalFilters((EqualRelation) mObj);
            } else if (scope instanceof DomeModelBuilder && mObj instanceof Subscription) {
                ((DomeModelBuilder) scope).addListToCausalFilters((Subscription) mObj);
            }
        } catch (AbstractDomeObject.DomeObjectException ex) {
            mObj = (ModelObject) ex.getMethodResult();
            errors.add(ex);
        } catch (Exception ex) {
            errors.add(ex);
        }
        if (mObj != null) {
            try {
                mObj.setName(mObj.getName() + COPY);
                modelObjectReferences.add(index, mObj);
            } catch (Exception ex) {
                errors.add(ex);
            }
        }
        if (errors.size() == 1)
            throw new AbstractDomeObject.DomeObjectException("addModelObjectCopy", (Exception) errors.get(0));
        else if (errors.size() > 1)
            throw new AbstractDomeObject.DomeObjectException("addModelObjectCopy", new MultipleErrorsException(errors));
    }

    //Modified by Ligon: Switched to returning the collection of new model objects
    public Collection addModelObjectCopies(Collection modelObjects, boolean deepCopy) {
        ArrayList errors = new ArrayList();
        Collection copies = null;
        try {
            ModelObjectScope scope = getScope();
            copies = scope.newModelObjects(modelObjects, deepCopy);
            for (Iterator i = copies.iterator(); i.hasNext();) {
                Object mObj = i.next();
                if (scope instanceof DomeModelBuilder && mObj instanceof ProceduralRelation) {
                    ((DomeModelBuilder) scope).addListToCausalFilters((ProceduralRelation) mObj);
                }
                //Qing-- add for Equal relation
                else if (scope instanceof DomeModelBuilder && mObj instanceof EqualRelation) {
                    ((DomeModelBuilder) scope).addListToCausalFilters((EqualRelation) mObj);
                } else if (scope instanceof DomeModelBuilder && mObj instanceof Subscription) {
                    ((DomeModelBuilder) scope).addListToCausalFilters((Subscription) mObj);
                }
            }
        } catch (AbstractDomeObject.DomeObjectException ex) {
            copies = (List) ex.getMethodResult();
            errors.add(ex);
        } catch (Exception ex) {
            errors.add(ex);
        }
        if (copies != null && !copies.isEmpty()) {
            try {
                Iterator it = copies.iterator();
                while (it.hasNext()) {
                    ModelObject mObj = (ModelObject) it.next();
                    mObj.setName(mObj.getName() + COPY);
                }
                modelObjectReferences.addAll(copies);
                return copies;
            } catch (Exception ex) {
                errors.add(ex);
            }
        }
        if (errors.size() == 1)
            throw new AbstractDomeObject.DomeObjectException("addModelObjectCopies", (Exception) errors.get(0));
        else if (errors.size() > 1)
            throw new AbstractDomeObject.DomeObjectException("addModelObjectCopies", new MultipleErrorsException(errors));
        return null;
    }

    public void addModelObjectCopies(Collection modelObjects, int index, boolean deepCopy) {
        ArrayList errors = new ArrayList();
        Collection copies = null;
        try {
            ModelObjectScope scope = getScope();
            copies = scope.newModelObjects(modelObjects, deepCopy);
            for (Iterator i = copies.iterator(); i.hasNext();) {
                Object mObj = i.next();
                if (scope instanceof DomeModelBuilder && mObj instanceof ProceduralRelation) {
                    ((DomeModelBuilder) scope).addListToCausalFilters((ProceduralRelation) mObj);
                } else if (scope instanceof DomeModelBuilder && mObj instanceof EqualRelation) {
                    ((DomeModelBuilder) scope).addListToCausalFilters((EqualRelation) mObj);
                } else if (scope instanceof DomeModelBuilder && mObj instanceof Subscription) {
                    ((DomeModelBuilder) scope).addListToCausalFilters((Subscription) mObj);
                }
            }
        } catch (AbstractDomeObject.DomeObjectException ex) {
            copies = (List) ex.getMethodResult();
            errors.add(ex);
        } catch (Exception ex) {
            errors.add(ex);
        }
        if (copies != null && !copies.isEmpty()) {
            try {
                Iterator it = copies.iterator();
                while (it.hasNext()) {
                    ModelObject mObj = (ModelObject) it.next();
                    mObj.setName(mObj.getName() + COPY);
                }
                modelObjectReferences.addAll(index, copies);
            } catch (Exception ex) {
                errors.add(ex);
            }
        }
        if (errors.size() == 1)
            throw new AbstractDomeObject.DomeObjectException("addModelObjectCopies", (Exception) errors.get(0));
        else if (errors.size() > 1)
            throw new AbstractDomeObject.DomeObjectException("addModelObjectCopies", new MultipleErrorsException(errors));
    }

    public void deleteModelObject(ModelObject modelObject) {
        try {
            ModelObjectScope scope = getScope();
            scope.deleteModelObject(modelObject);
            if (scope instanceof DomeModelBuilder && modelObject instanceof ProceduralRelation) {
                ((DomeModelBuilder) scope).removeListFromCausalFilters((ProceduralRelation) modelObject);
            } else if (scope instanceof DomeModelBuilder && modelObject instanceof EqualRelation) {
                ((DomeModelBuilder) scope).removeListFromCausalFilters((EqualRelation) modelObject);
            } else if (scope instanceof DomeModelBuilder && modelObject instanceof Subscription) {
                ((DomeModelBuilder) scope).removeListFromCausalFilters((Subscription) modelObject);
            }
        } catch (Exception ex) {
            throw new AbstractDomeObject.DomeObjectException("deleteModelObject", ex);
        }
    }

    public void deleteModelObjects(Collection modelObjects) {
        try {
            ModelObjectScope scope = getScope();
            scope.deleteModelObjects(modelObjects);
            for (Iterator i = modelObjects.iterator(); i.hasNext();) {
                Object modelObject = i.next();
                if (scope instanceof DomeModelBuilder && modelObject instanceof ProceduralRelation) {
                    ((DomeModelBuilder) scope).removeListFromCausalFilters((ProceduralRelation) modelObject);
                } else if (scope instanceof DomeModelBuilder && modelObject instanceof EqualRelation) {
                    ((DomeModelBuilder) scope).removeListFromCausalFilters((EqualRelation) modelObject);
                } else if (scope instanceof DomeModelBuilder && modelObject instanceof Subscription) {
                    ((DomeModelBuilder) scope).removeListFromCausalFilters((Subscription) modelObject);
                }
            }
        } catch (Exception ex) {
            throw new AbstractDomeObject.DomeObjectException("deleteModelObjects", ex);
        }
    }

    public void shiftLeft(int[] indices) {
        try {
            modelObjectReferences.shiftLeft(indices);
        } catch (Exception ex) {
            throw new AbstractDomeObject.DomeObjectException("shiftLeft", ex);
        }
    }

    public void shiftRight(int[] indices) {
        try {
            modelObjectReferences.shiftRight(indices);
        } catch (Exception ex) {
            throw new AbstractDomeObject.DomeObjectException("shiftRight", ex);
        }
    }

}
