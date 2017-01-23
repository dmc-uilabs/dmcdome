package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.plugin.catalog.core.*;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.Collator;
import java.lang.Short;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 19
 */
public class RelationEditor extends JPanel {

    public static final int ACCIDENTAL_MOVE_DISTANCE = 20;

    private SpringLayout layout;
    private ComponentReference compRef;
    private boolean addedLocalLastTime = true; // remembers which panel to be shown
    private RelationDialog relDialog;

    protected boolean startDrag = false;
    protected List fixedCellList = new ArrayList();
    protected Point startDragPoint = null;
    protected int barInsertionIdx = -1;
    protected int cellInsertionIdx = -1;
    protected int numberOfSelectedCells = 0;
    protected boolean insertionIntoLeftPanel = false;

    public static final int SORT_BY_REL_NAME = 1;
    public static final int SORT_BY_REL_ALIAS = 2;

    public RelationEditor(ComponentReference compRef) {
        super();
        this.compRef = compRef;
        this.setBackground(UIUtil.REL_EDITOR_BG_COLOR);
        layout = new SpringLayout();
        this.setLayout(layout);
        this.setBorder(null);
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                clicked();
            }
        });
    }

    public void clicked() {
        if (compRef.getImplementationEditor().isScriptEditorShown()) {
            compRef.getImplementationEditor().closeScriptEditor(true);
            System.out.println("relation param cell clicked. save changes and close editor");
        }
        compRef.clearBarAndCellSelection();
    }


    public ComponentReference getComponentReference() {
        return compRef;
    }

    public void closeDomeConnnectionsOfRelationDialog() {
        if (relDialog != null) {
            relDialog.closeDomeConnections();
        }
    }

    /** returns RelationBar with given relationName. returns null if not found. */
    public RelationBar getRelationBar(String relAlias) {
        Component[] components = this.getComponents();
        for (int i = 0; i < components.length; i++) {
            RelationBar bar = (RelationBar) components[i];
            if (relAlias.equals(bar.getRelAlias())) {
                return bar;
            }
        }
        return null;
    }

    public Dimension getEditorSize() {
        int maxBarWidth = 0;
        int sumBarHeight = 0;
        for (int i = 0; i < getComponentCount(); i++) {
            Dimension barSize = ((BaseBar) getComponent(i)).getBarSize();
            if (maxBarWidth < barSize.width) {
                maxBarWidth = barSize.width;
            }
            sumBarHeight = sumBarHeight + barSize.height;
        }

        if (getComponentCount() == 0) {
            return new Dimension(maxBarWidth + UIUtil.BAR_LEFT_MARGIN * 2, UIUtil.MIN_EDITOR_HEIGHT);
        }

        return new Dimension(maxBarWidth + UIUtil.BAR_LEFT_MARGIN * 2, sumBarHeight + UIUtil.GAP_BETWEEN_BARS * (getComponentCount() - 1) + UIUtil.BAR_TOP_MARGIN * 2);
    }

    /** returns RelationBar with given index. */
    public RelationBar getRelationBar(int relationIndex) {
        return (RelationBar) this.getComponent(relationIndex);
    }

    /** returns the number of RelationBars in this Relation Editor */
    public int getRelationBarCount() {
        return this.getComponentCount();
    }

    /** this  method updates GUI components. remove relation using relAlias as a key. relName is not unique in the editor, but relAlias is. */
    public int removeRelationBar(String relAlias) {
        for (int i = 0; i < getComponents().length; i++) {
            RelationBar bar = (RelationBar) getComponent(i);
            if (relAlias.equals(bar.getRelAlias())) {
                this.remove(bar);
                updateLayoutConstraints();
                UIUtil.updateEditorBounds(compRef);

                /* below code should not be here. this method just removes GUI components  */
                // compRef.getCurrentCImplementation().removeRelationBar(relAlias);

                return i;
            }
        }
        return -1;
    }

    public void editRelation(RelationBar editedRelBar) {
        /* close script editor */
        compRef.getImplementationEditor().closeScriptEditor(true);

        /* relation is inserted after the selected relation bar */
        String relAlias = editedRelBar.getRelAlias();

        String implName = compRef.getImplementationEditor().getImplementationName();
        String itfName = compRef.getImplementationEditor().getInterfaceName();

        CModel model = compRef.getCurrentCModel();
        CImplementation impl = model.getInterface(itfName).getImplementation(implName);
        CRelation rel = impl.getNamingService().getRelation(relAlias);

        if (rel instanceof CLocalRelation) {
            CLocalRelation localRel = (CLocalRelation) rel;
            CNamingService namingService = localRel.getNamingService();

            RelationDialog relDialog = new RelationDialog(compRef);
            relDialog.updateLabelsForEditing(true);

            relDialog.setPanelVisibility(true);
            relDialog.setRelationName(rel.getRelationName());

            List paramInfoList = new ArrayList();
            List iParamNameList = rel.getInputParameterNames();
            for (int i = 0; i < iParamNameList.size(); i++) {
                String paramName = (String) iParamNameList.get(i);
                CRelationInputParameter cParam = namingService.getRelationInputParameter(relAlias + "." + paramName);
                paramInfoList.add(new String[] { cParam.getName(), cParam.getDataType(), cParam.getUnit(), cParam.getDefaultValue() });
            }
            relDialog.setInputParamList(paramInfoList);

            paramInfoList = new ArrayList();
            List oParamNameList = rel.getOutputParameterNames();
            for (int i = 0; i < oParamNameList.size(); i++) {
                String paramName = (String) oParamNameList.get(i);
                CRelationOutputParameter cParam = namingService.getRelationOutputParameter(relAlias + "." + paramName);
                paramInfoList.add(new String[] { cParam.getName(), cParam.getDataType(), cParam.getUnit(), cParam.getDefaultValue() });
            }
            relDialog.setOutputParamList(paramInfoList);
            relDialog.setRelationScript(rel.getRelationScript());

            List depInfoList = new ArrayList();
            for (int i = 0; i < oParamNameList.size(); i++) {
                String oParamName = (String) oParamNameList.get(i);
                Set drivers = rel.getDriversOf(oParamName);
                depInfoList.add(new Object[] { oParamName, drivers.toArray(new String[drivers.size()]) });
            }
            relDialog.setDependency(depInfoList);

            relDialog.setVisible(true);

            /* handles submission */
            if (relDialog.isSubmitted()) {
                if (relDialog.isLocalPanelVisible()) {
                    int insertIndex = removeRelationBar(relAlias);
                    /* update CodeBase */
                    compRef.getImplementationEditor().unindexRelationOrInterface(compRef.getImplementationEditor().getInterfaceName(), compRef.getImplementationEditor().getImplementationName(), relAlias);
                    //impl.removeRelationBar(relAlias);
                    addLocalRelationToRelationBarAndCModel(relDialog, relAlias, insertIndex);
                } else {
                    int insertIndex = removeRelationBar(relAlias);
                    /* update CodeBase */
                    compRef.getImplementationEditor().unindexRelationOrInterface(compRef.getImplementationEditor().getInterfaceName(), compRef.getImplementationEditor().getImplementationName(), relAlias);
                    //impl.removeRelationBar(relAlias);
                    addRemoteRelationToRelationBarAndCModel(relDialog, relAlias, insertIndex);
                }
                compRef.getCurrentEvaluationContext().refresh();
                EvaluationMode.initEditorPanes(compRef);
            }
        } if (rel instanceof CRemoteRelation) {
            CRemoteRelation remoteRel = (CRemoteRelation) rel;
            String server = remoteRel.getServerPort();
            String userName = remoteRel.getUser();
            String password = remoteRel.getPassword();
            String space = remoteRel.getSpace();
            String path = remoteRel.getInterfacePath();

            RelationDialog relDialog = new RelationDialog(compRef);
            relDialog.updateLabelsForEditing(true);

            relDialog.setPanelVisibility(false);
            relDialog.setServer(server);
            relDialog.setUserName(userName);
            relDialog.setPassword(password);
            relDialog.setSpace(space);
            relDialog.setPath(path);
            relDialog.updateServerNavigationPane();
            relDialog.setVisible(true);

            /* handles submission */
            if (relDialog.isSubmitted()) {
                if (relDialog.isLocalPanelVisible()) {
                    int insertIndex = removeRelationBar(relAlias);
                    //impl.removeRelationBar(relAlias); // addLocalRelationToRelationBarAndCModel() will invoke impl.removeRelationBar(relAlias) in it
                    addLocalRelationToRelationBarAndCModel(relDialog, relAlias, insertIndex);
                } else {
                    int insertIndex = removeRelationBar(relAlias);
                    //impl.removeRelationBar(relAlias); // addRemoteRelationToRelationBarAndCModel() will invoke impl.removeRelationBar(relAlias) in it
                    addRemoteRelationToRelationBarAndCModel(relDialog, relAlias, insertIndex);
                }
            }
        }
    }

    /** open RelationDialog and collect input to add a new RelationBar */
    public RelationBar addRelation() {
        /* relation is inserted after the selected relation bar */
        RelationBar selectedBar = compRef.getSelectedRelationBar();
        int insertIndex = compRef.getRelationEditor().getComponentCount();
        if (selectedBar != null) {
            insertIndex = UIUtil.indexOfComponent(selectedBar);
            if (insertIndex == -1) {
                insertIndex = 0;
            } else {
                insertIndex++;
            }
        }

        if (relDialog == null) {
            relDialog = new RelationDialog(compRef);
        }
        /* initialize relation dialog */
        relDialog.resetLocalRelationPanel();
        relDialog.setPanelVisibility(addedLocalLastTime);
        if (addedLocalLastTime) {
            relDialog.resetRemoteRelationPanel();
        }
        relDialog.updateLabelsForEditing(false);
        relDialog.setVisible(true);

        if (relDialog.isSubmitted()) {
            RelationBar ret = null;
            if (relDialog.isLocalPanelVisible()) {
                addedLocalLastTime = true;
                ret = addLocalRelationToRelationBarAndCModel(relDialog, null, insertIndex);
            } else {
                addedLocalLastTime = false;
                ret = addRemoteRelationToRelationBarAndCModel(relDialog, null, insertIndex);
            }
            ret.getCenterPanel().updateRelNameLabelHeight();
            ret.getCenterPanel().invalidate();
            compRef.getCurrentEvaluationContext().refresh();
            EvaluationMode.initEditorPanes(compRef);
            return ret;
        } else {
            return null;
        }
    }

    /** remove the selected relation */
    public void removeRelationOrCell() {
        compRef.getImplementationEditor().closeScriptEditor(true);
        RelationBar[] selectedBars = compRef.getSelectedRelationBars();
        BaseCell[] selectedCells = compRef.getSelectedCells();

        if (selectedBars.length > 0) {
            Object[] options = { "OK", "Cancel" };
            int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(compRef.getInterfaceBar()), "Click OK to delete the selected relation(s).", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (answer != 0) {
                return;
            } else {
                for (int j = 0; j < selectedBars.length; j++) {
                    compRef.getRelationEditor().removeRelationBar(selectedBars[j].getRelAlias()); // update UI
                    compRef.getCurrentCImplementation().removeRelation(selectedBars[j].getRelAlias()); // update CModel
                    compRef.getImplementationEditor().unindexRelationOrInterface(compRef.getImplementationEditor().getInterfaceName(), compRef.getImplementationEditor().getImplementationName(), selectedBars[j].getRelAlias()); // update CodeBase
                }
                return;
            }
        }

        if (selectedCells.length > 0) {
            Object[] options = { "OK", "Cancel" };
            int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(compRef.getInterfaceBar()), "Click OK to delete the selected cell(s).", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (answer != 0) {
                return;
            } else {
                for (int j = 0; j < selectedCells.length; j++) {
                    if (selectedCells[j] instanceof RelationInputCell) {
                        RelationBar relBar = (RelationBar) selectedCells[j].getBar();
                        compRef.getCurrentCNamingService().getRelation(relBar.getRelAlias()).removeInputParameter(selectedCells[j].getParamName()); // update CModel
                        compRef.getImplementationEditor().unindexParameter(compRef.getImplementationEditor().getInterfaceName(), compRef.getImplementationEditor().getImplementationName(), relBar.getRelAlias(), selectedCells[j].getParamName()); // update CodeBase
                        relBar.removeRelationInputCell(selectedCells[j].getParamName()); // update UI
                    } else if (selectedCells[j] instanceof RelationOutputCell) {
                        RelationBar relBar = (RelationBar) selectedCells[j].getBar();
                        compRef.getCurrentCNamingService().getRelation(relBar.getRelAlias()).removeOutputParameter(selectedCells[j].getParamName()); // update CModel
                        compRef.getImplementationEditor().unindexParameter(compRef.getImplementationEditor().getInterfaceName(), compRef.getImplementationEditor().getImplementationName(), relBar.getRelAlias(), selectedCells[j].getParamName()); // update CodeBase
                        relBar.removeRelationOutputCell(selectedCells[j].getParamName()); // update UI
                    } else if (selectedCells[j] instanceof InterfaceInputCell) {
                        InterfaceBar itfBar = (InterfaceBar) selectedCells[j].getBar();
                        compRef.getCurrentCImplementation().getParentInterface().removeInputParameter(selectedCells[j].getParamName()); // update CModel
                        compRef.getImplementationEditor().unindexParameter(compRef.getImplementationEditor().getInterfaceName(), compRef.getImplementationEditor().getImplementationName(), itfBar.getRelAlias(), selectedCells[j].getParamName()); // update CodeBase
                        itfBar.removeInterfaceInputCell(selectedCells[j].getParamName()); // update UI
                    } else if (selectedCells[j] instanceof InterfaceOutputCell) {
                        InterfaceBar itfBar = (InterfaceBar) selectedCells[j].getBar();
                        compRef.getCurrentCImplementation().getParentInterface().removeOutputParameter(selectedCells[j].getParamName()); // update CModel
                        compRef.getImplementationEditor().unindexParameter(compRef.getImplementationEditor().getInterfaceName(), compRef.getImplementationEditor().getImplementationName(), itfBar.getRelAlias(), selectedCells[j].getParamName()); // update CodeBase
                        itfBar.removeInterfaceOutputCell(selectedCells[j].getParamName()); // update UI
                    }
                }

                compRef.getCurrentCImplementation().getParentInterface().synchronizeInterfaceParametersOfAllImplementations();
                return;
            }
        } else {
            Object[] options = { "OK" };
            int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(compRef.getInterfaceBar()), "Please select relations or cells to be deleted by clicking on their names.", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            if (answer != 0) {
                return;
            }
        }



        /* mark this model has been changed */
        compRef.markUnsavedChanges();
    }


    /** using a CRelation instance, construct UI elements to diplay contents of the CRelation */
    public RelationBar displayRelation(CRelation rel) {
        /* relation is inserted after the selected relation bar */
        RelationBar selectedBar = compRef.getSelectedRelationBar();
        int insertIndex = compRef.getRelationEditor().getComponentCount();
        if (selectedBar != null) {
            insertIndex = UIUtil.indexOfComponent(selectedBar);
            if (insertIndex == -1) {
                insertIndex = 0;
            } else {
                insertIndex++;
            }
        }

        /* based on given CRelation object, construct GUI */
        RelationBar bar = new RelationBar(rel.getRelationName(), rel.getRelAlias(), compRef);
        this.add(bar, insertIndex);

        java.util.List inputParams = rel.getInputParameterNames();
        for (Iterator i = inputParams.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter param = rel.getNamingService().getParameter(rel.getRelAlias() + "." + paramName);
            param.setDataType(param.getDataType());
            param.setUnit(param.getUnit());
            param.setDefaultValue(param.getDefaultValue());
            bar.addRelationInputCell(param.getName(), param.getDataType(), param.getUnit(), ((CRelationInputParameter) param).getMapping().getMappingScript());
        }

        java.util.List outputParams = rel.getOutputParameterNames();
        for (Iterator i = outputParams.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter param = rel.getNamingService().getParameter(rel.getRelAlias() + "." + paramName);
            param.setDataType(param.getDataType());
            param.setUnit(param.getUnit());
            param.setDefaultValue(param.getDefaultValue());
            bar.addRelationOutputCell(param.getName(), param.getDataType(), param.getUnit());
        }

        /* below two lines update layout and refresh screen */
        updateLayoutConstraints();
        UIUtil.updateEditorBounds(compRef);

        return bar;
    }



    /** by collecting inputs in dialog, create CRelation instance, add it to CImplementation, and construct GUI element for it
     * relAlias is an optional argument, to assign an self-generated, unique relAlias leave it as null */
    public RelationBar addLocalRelationToRelationBarAndCModel(RelationDialog relDialog, String relAlias, int insertIndex) {

        String implName = compRef.getImplementationEditor().getImplementationName();
        String itfName = compRef.getImplementationEditor().getInterfaceName();

        CModel model = compRef.getCurrentCModel();
        CImplementation impl = model.getInterface(itfName).getImplementation(implName);

        /* scriptMap is populated when relAlias is not null. it is a map of paramName -> mappingScript */
        Map scriptMap = new HashMap();


        /* create CRelation object */
        CLocalRelation rel = null;
        if (relAlias == null) {
            rel = impl.addLocalRelation(relDialog.getRelationName());
        } else {
            /* populate scriptMap. it will be supplied to set the mapping script of below newly-created 'rel' */
            CRelation removedRel = impl.getNamingService().getRelation(relAlias);
            List iParamNames = removedRel.getInputParameterNames();
            for (int i = 0; i < iParamNames.size(); i++) {
                String paramName = (String) iParamNames.get(i);
                CRelationInputParameter riParam = impl.getNamingService().getRelationInputParameter(relAlias + "." + paramName);
                scriptMap.put(paramName, riParam.getMapping().getMappingScript());
            }
            impl.removeRelation(relAlias);
            rel = impl.addLocalRelation(relDialog.getRelationName(), relAlias);
        }

        java.util.List paramList = relDialog.getInputParamList();
        for (int i = 0; i < paramList.size(); i++) {
            String[] paramInfo = (String[]) paramList.get(i);
            CRelationInputParameter param = rel.addInputParameter(paramInfo[0]);
            param.setDataType(paramInfo[1]);
            param.setUnit(paramInfo[2]);
            param.setDefaultValue(paramInfo[3]);

            /* we can reuse script based on param name matching */
            if (scriptMap.get(param.getName()) != null) {
                String script = (String) scriptMap.get(param.getName());
                param.getMapping().setMappingScript(script, CoreUtil.getParamNames(script, param.getNamingService()));
            }
        }

        paramList = relDialog.getOutputParamList();
        for (int i = 0; i < paramList.size(); i++) {
            String[] paramInfo = (String[]) paramList.get(i);
            CRelationOutputParameter param = rel.addOutputParameter(paramInfo[0]);
            param.setDataType(paramInfo[1]);
            param.setUnit(paramInfo[2]);
            param.setDefaultValue(paramInfo[3]);
        }

        rel.setRelationScript(relDialog.getRelationScript());

        List depList = relDialog.getDependency();
        for (int i = 0; i < depList.size(); i++) {
            Object[] item = (Object[]) depList.get(i);
            String outputName = (String) item[0];
            String[] inputNames = (String[]) item[1];
            for (int j = 0; j < inputNames.length; j++) {
                String inputName = inputNames[j];
                rel.setDependency(inputName, outputName);
            }
        }

        /* update CodeBase with the modified CLocalRelation */
        compRef.getImplementationEditor().indexRelationOrInterface(itfName, implName, rel.getRelAlias());

        /* based on created CRelation object, construct GUI */
        RelationBar bar = new RelationBar(rel.getRelationName(), rel.getRelAlias(), compRef);

        this.add(bar, insertIndex);

        java.util.List inputParams = rel.getInputParameterNames();
        for (Iterator i = inputParams.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CRelationInputParameter param = rel.getNamingService().getRelationInputParameter(rel.getRelAlias() + "." + paramName);
            param.setDataType(param.getDataType());
            param.setUnit(param.getUnit());
            param.setDefaultValue(param.getDefaultValue());
            bar.addRelationInputCell(param.getName(), param.getDataType(), param.getUnit(), param.getMapping().getMappingScript());
        }

        java.util.List outputParams = rel.getOutputParameterNames();
        for (Iterator i = outputParams.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter param = rel.getNamingService().getParameter(rel.getRelAlias() + "." + paramName);
            param.setDataType(param.getDataType());
            param.setUnit(param.getUnit());
            param.setDefaultValue(param.getDefaultValue());
            bar.addRelationOutputCell(param.getName(), param.getDataType(), param.getUnit());
        }

        /* below two lines update layout and refresh screen */
        updateLayoutConstraints();
        UIUtil.updateEditorBounds(compRef);

        /* mark this model has been changed */
        compRef.markUnsavedChanges();

        return bar;
    }

    /** by collecting inputs in dialog, create CRelation instance, add it to CImplementation, and construct GUI element for it
     * relAlias is an optional argument, to assign an self-generated, unique relAlias leave it as null */
    public RelationBar addRemoteRelationToRelationBarAndCModel(RelationDialog relDialog, String relAlias, int insertIndex) {

        String implName = compRef.getImplementationEditor().getImplementationName();
        String itfName = compRef.getImplementationEditor().getInterfaceName();

        DomeConnection domeConn = relDialog.getDomeConnection();
//        DomeInterface domeItf = domeConn.getInterfaceByPath(relDialog.getSpace(), relDialog.getPath());
//        RuntimeInterface rtItf = domeItf.createRuntimeInterface();

        CModel model = compRef.getCurrentCModel();
        CImplementation impl = model.getInterface(itfName).getImplementation(implName);

        /* scriptMap is populated when relAlias is not null. it is a map of paramName -> mappingScript */
        Map scriptMap = new HashMap();

        CRemoteRelation rel = null;
        if (relAlias == null) {
            rel = impl.addRemoteRelation(relDialog.getRelationName());
        } else {
            /* populate scriptMap. it will be supplied to set the mapping script of below newly-created 'rel' */
            CRelation removedRel = impl.getNamingService().getRelation(relAlias);
            List iParamNames = removedRel.getInputParameterNames();
            for (int i = 0; i < iParamNames.size(); i++) {
                String paramName = (String) iParamNames.get(i);
                CRelationInputParameter riParam = impl.getNamingService().getRelationInputParameter(relAlias + "." + paramName);
                scriptMap.put(paramName, riParam.getMapping().getMappingScript());
            }
            impl.removeRelation(relAlias);
            rel = impl.addRemoteRelation(relDialog.getRelationName(), relAlias);
        }
        rel.setServerPort(relDialog.getServer());
        rel.setUser(relDialog.getUserName());
        rel.setPassword(relDialog.getPassword());
        rel.setSpace(relDialog.getSpace());
        rel.setInterfacePath(relDialog.getPath());
        rel.configureRemoteRelationUsingRuntimeInterfaceInformation(domeConn);
        //rel.setRelationName(domeItf.getParentModel().getModelName() + "/" + domeItf.getInterfaceName());

        RelationBar bar = new RelationBar(rel.getRelationName(), rel.getRelAlias(), compRef);
        this.add(bar, insertIndex);

//        /* store RuntimeParameter instances of input parameters */
//        java.util.List inputRtParams = new ArrayList();

        java.util.List inputParams = rel.getInputParameters();
        for (Iterator i = inputParams.iterator(); i.hasNext(); ) {
            CRelationInputParameter param = (CRelationInputParameter) i.next();
            bar.addRelationInputCell(param.getName(), param.getDataType(), param.getUnit(), param.getMapping().getMappingScript());
        }

        java.util.List outputParams = rel.getOutputParameters();
        for (Iterator i = outputParams.iterator(); i.hasNext(); ) {
            CRelationOutputParameter param = (CRelationOutputParameter) i.next();
            bar.addRelationOutputCell(param.getName(), param.getDataType(), param.getUnit());
        }

        /* update CodeBase with the modified CLocalRelation */
        compRef.getImplementationEditor().indexRelationOrInterface(itfName, implName, rel.getRelAlias());

        System.out.println("newly created CRelation: " + rel);

        /* below two lines update layout and refresh screen */
        updateLayoutConstraints();
        UIUtil.updateEditorBounds(compRef);

        /* mark this model has been changed */
        compRef.markUnsavedChanges();

        return bar;
    }

    /** to sort cells in the interface bar, set currentBarIdx as -1. to sort cells in the first relation bar, set currentBarIdx as 0 */
    public void sortCells(int currentBarIdx, boolean isLeftPanel, final boolean isAscending) {
        BarSidePanel sidePanel = null;

        if (currentBarIdx == -1) {
            sidePanel = (isLeftPanel ? compRef.getInterfaceBar().getLeftPanel() : compRef.getInterfaceBar().getRightPanel());
            List referenceList = (isLeftPanel ? compRef.getCurrentCInterface().getInputParameterList() : compRef.getCurrentCInterface().getOutputParameterList());
            Collections.sort(referenceList, new Comparator() {
                Collator collator = Collator.getInstance();
                    public int compare(Object obj1, Object obj2) {
                        String paramName1 = ((CParameter) obj1).getName();
                        String paramName2 = ((CParameter) obj2).getName();
                        if (isAscending) {
                            return collator.compare(paramName1, paramName2);
                        } else {
                            return -1 * collator.compare(paramName1, paramName2);
                        }
                    }
                });
            compRef.getCurrentCInterface().synchronizeInterfaceParametersOfAllImplementations();
        } else {
            String relAlias = compRef.getRelationBar(currentBarIdx).getRelAlias();
            sidePanel = (isLeftPanel ? compRef.getRelationBar(currentBarIdx).getLeftPanel() : compRef.getRelationBar(currentBarIdx).getRightPanel());
            List referenceList = (isLeftPanel ? compRef.getCurrentCNamingService().getRelation(relAlias).getInputParameterNames() : compRef.getCurrentCNamingService().getRelation(relAlias).getOutputParameterNames());
            Collections.sort(referenceList, new Comparator() {
                Collator collator = Collator.getInstance();
                    public int compare(Object obj1, Object obj2) {
                        String paramName1 = (String) obj1;
                        String paramName2 = (String) obj2;
                        if (isAscending) {
                            return collator.compare(paramName1, paramName2);
                        } else {
                            return -1 * collator.compare(paramName1, paramName2);
                        }
                    }
                });
        }

        List paramCellList = new ArrayList(Arrays.asList(sidePanel.getComponents()));
        List oldParamNameList = new ArrayList();

        Map paramNameToParamCellMap = new HashMap();
        for (int i = 0; i < paramCellList.size(); i++) {
            BaseCell cell = (BaseCell) paramCellList.get(i);
            oldParamNameList.add(cell.getParamName());
            paramNameToParamCellMap.put(cell.getParamName(), cell);
        }

        List newParamNameList = new ArrayList(oldParamNameList);

        Collections.sort(newParamNameList, new Comparator() {
            Collator collator = Collator.getInstance();
            public int compare(Object obj1, Object obj2) {
                String paramName1 = (String) obj1;
                String paramName2 = (String) obj2;
                if (isAscending) {
                    return collator.compare(paramName1, paramName2);
                } else {
                    return -1 * collator.compare(paramName1, paramName2);
                }
            }
        });

        if (! oldParamNameList.equals(newParamNameList)) {
            sidePanel.removeAll();
            for (int i = 0; i < newParamNameList.size(); i++) {
                String paramName = (String) newParamNameList.get(i);
                sidePanel.add((Component) paramNameToParamCellMap.get(paramName));
            }
            sidePanel.updateLayoutConstraints();
            sidePanel.revalidate();
        }
    }

//    /** to move cells in the interface bar, set currentBarIdx as -1. to move cells in the first relation bar, set currentBarIdx as 0 */
//    public void moveCell(int currentBarIdx, boolean isLeftPanel, int currentCellIdx, int newInsertionIdx) {
//        BaseBar bar = null;
//        BarSidePanel sidePanel = null;
//        if (currentBarIdx == -1) {
//            bar = compRef.getInterfaceBar();
//        } else {
//            bar = compRef.getRelationBar(currentBarIdx);
//        }
//
//        if (isLeftPanel) {
//            sidePanel = bar.getLeftPanel();
//        } else {
//            sidePanel = bar.getRightPanel();
//        }
//
//        int columnCount = sidePanel.getColumnCount();
//
//        /* find row & col of new insertion idx */
//        int newRowIdx = newInsertionIdx / (columnCount + 1);
//        int newColIdx = newInsertionIdx % (columnCount + 1);
//
//        /* convert row & col of insertion idx into row & col of cell */
//        if (newColIdx == columnCount) {
//            newRowIdx = newRowIdx + 1;
//            newColIdx = 0;
//        }
//
//        int newCellIdx = newRowIdx * columnCount + newColIdx;
//
//        System.out.println("newRowIdx = " + newRowIdx + ", newColIdx = " + newColIdx + ", newCellIdx = " + newCellIdx);
//
//        /* do rearrange in an array */
//        List sidePanelComps = new ArrayList(Arrays.asList(sidePanel.getComponents()));
//        BaseCell cellToBeMoved = (BaseCell) sidePanelComps.remove(currentCellIdx);
//        if (currentCellIdx < newCellIdx) {
//            sidePanelComps.add(newCellIdx - 1, cellToBeMoved);
//        } else {
//            sidePanelComps.add(newCellIdx, cellToBeMoved);
//        }
//
//        /* apply the changes made to an array to the actual side panel */
//        sidePanel.removeAll();
//        for (int i = 0; i < sidePanelComps.size(); i++) {
//            sidePanel.addCell((BaseCell) sidePanelComps.get(i));
//        }
//
//        /* retrieve a list that is directly backing the orderly arrangement of parameters: the list contains either param-name string or CParameter object depending on the bar type */
//        List paramReferenceList = null;
//        if (cellToBeMoved.cellType == BaseCell.ITF_INPUT) {
//            paramReferenceList = compRef.getCurrentCImplementation().getParentInterface().getInputParameterList();
//        } else if (cellToBeMoved.cellType == BaseCell.ITF_OUTPUT) {
//            paramReferenceList = compRef.getCurrentCImplementation().getParentInterface().getOutputParameterList();
//        } else if (cellToBeMoved.cellType == BaseCell.REL_INPUT) {
//            paramReferenceList = compRef.getCurrentCImplementation().getNamingService().getRelation(bar.getRelAlias()).getInputParameterNames();
//        } else if (cellToBeMoved.cellType == BaseCell.REL_OUTPUT) {
//            paramReferenceList = compRef.getCurrentCImplementation().getNamingService().getRelation(bar.getRelAlias()).getOutputParameterNames();
//        }
//
//        if (currentCellIdx > newCellIdx) {
//            Object paramNameToBeMoved = paramReferenceList.get(currentCellIdx);
//            paramReferenceList.add(newCellIdx, paramNameToBeMoved);
//            paramReferenceList.remove(currentCellIdx + 1);
//        } else {
//            Object paramNameToBeMoved = paramReferenceList.get(currentCellIdx);
//            paramReferenceList.add(newCellIdx, paramNameToBeMoved);
//            paramReferenceList.remove(currentCellIdx);
//        }
//
//        /* copy changes made to the current interface to its implementations */
//        if (cellToBeMoved.cellType == BaseCell.ITF_INPUT || cellToBeMoved.cellType == BaseCell.ITF_OUTPUT) {
//            compRef.getCurrentCImplementation().getParentInterface().synchronizeInterfaceParametersOfAllImplementations();
//        }
//
//        updateLayoutConstraints();
//        UIUtil.updateEditorBounds(compRef);
//    }

    /** move a relation at the current index to a new index.
     * to move B to a position right after C, move(1, 3)
     * [A at 0]
     * [B at 1]
     * [C at 2]
     * [D at 3]
     * note that insertIdx is the index of the new position before this movement is made */
    public void moveRelation(int currentIdx, int insertIdx) {
        if (currentIdx == insertIdx) {
            return;
        }
        RelationBar barToBeMoved = (RelationBar) this.getComponent(currentIdx);
        List relAliasList = compRef.getCurrentCImplementation().getRelationAliases();

        /* shift components after the current point */
        for (int i = currentIdx + 1; i <= insertIdx; i++) {
            RelationBar barToBePulledFront = (RelationBar) this.getComponent(i);
            this.add(barToBePulledFront, i - 1);
        }

        this.add(barToBeMoved, insertIdx);

        if (currentIdx > insertIdx) {
            String relAliasToBeMoved = (String) relAliasList.get(currentIdx);
            relAliasList.add(insertIdx, relAliasToBeMoved);
            relAliasList.remove(currentIdx + 1);
        } else {
            String relAliasToBeMoved = (String) relAliasList.get(currentIdx);
            relAliasList.add(insertIdx + 1, relAliasToBeMoved);
            relAliasList.remove(currentIdx);
        }

        updateLayoutConstraints();
        UIUtil.updateEditorBounds(compRef);
    }

    /** sort relations */
    public void sortRelations(int sortByOption, final boolean isAscending) {
        List relAliasList = compRef.getCurrentCImplementation().getRelationAliases();

        if (sortByOption == SORT_BY_REL_NAME) { // sort by alias
            Collections.sort(relAliasList, new Comparator() {
                Collator collator = Collator.getInstance();
                    public int compare(Object obj1, Object obj2) {
                        String relName1 = compRef.getCurrentCNamingService().getRelation((String) obj1).getRelationName();
                        String relName2 = compRef.getCurrentCNamingService().getRelation((String) obj2).getRelationName();
                        if (isAscending) {
                            return collator.compare(relName1, relName2);
                        } else {
                            return -1 * collator.compare(relName1, relName2);
                        }
                    }
                });
        }

        if (sortByOption == SORT_BY_REL_ALIAS) { // sort by alias
            Collections.sort(relAliasList, new Comparator() {
                Collator collator = Collator.getInstance();
                    public int compare(Object obj1, Object obj2) {
                        String paramName1 = ((String) obj1);
                        String paramName2 = ((String) obj2);
                        if (isAscending) {
                            return collator.compare(paramName1, paramName2);
                        } else {
                            return -1 * collator.compare(paramName1, paramName2);
                        }
                    }
                });
        }

        Map relAliasToBarMap = new HashMap();
        for (int i = 0; i < this.getComponentCount(); i++) {
            BaseBar bar = (BaseBar) this.getComponent(i);
            String relAlias = bar.getRelAlias();
            relAliasToBarMap.put(relAlias, bar);
        }
        this.removeAll();
        for (int i = 0; i < relAliasList.size(); i++) {
            String relAlias = (String) relAliasList.get(i);
            BaseBar bar = (BaseBar) relAliasToBarMap.get(relAlias);
            this.add(bar);
        }
        
        updateLayoutConstraints();
        UIUtil.updateEditorBounds(compRef);
    }

    /** this method supports moving multiple relations together.
     * once any of those selected reaches the bottom, they stop at that point,
     * but others that have not reaches their lower limits can continue to move.
     * Therefore, all of the selected bars can be placed grouped at the bottom. */
    public void moveDownSelectedRelation() {
        RelationBar[] selectedBars = compRef.getSelectedRelationBars();
        for (int i = selectedBars.length - 1; i >= 0; i--) {
            int currentIdx = UIUtil.indexOfComponent(selectedBars [i]);
            if (currentIdx < (this.getComponentCount() - (selectedBars.length - i))) { // substracting (selectedBars.length - i) is used as a stopper for the multi-selection moving
                moveRelation(currentIdx, currentIdx + 1);
            } else {
                //speedup Clog.debug("can't move the relation down any more: " + (i + 1) + "th bar of the selection");
            }
        }
    }

    /** this method supports moving multiple relations together.
     * once any of those selected reaches the top, they stop at that point,
     * but others that have not reaches their upper limits can continue to move.
     * Therefore, all of the selected bars can be placed grouped at the top. */
    public void moveUpSelectedRelation() {
        RelationBar[] selectedBars = compRef.getSelectedRelationBars();
        for (int i = 0; i < selectedBars.length; i++) {
            int currentIdx = UIUtil.indexOfComponent(selectedBars [i]);
            if (currentIdx > i) { // the (i+1)th bar in the selection can move down to the (i+1)th to the top (ex) when i = 0, the 1st bar can move up to the 1st to the top. when i = 1, the 2nd bar can move up to the 2nd to the top.
                moveRelation(currentIdx, currentIdx - 1);
            } else {
                //speedup Clog.debug("can't move the relation up any more: " + (i + 1) + "th bar of the selection");
            }
        }
    }

    public RelationBar addRelation(String relationName) {
        System.out.println("adding relation: " + relationName);

        RelationBar bar = new RelationBar(relationName, relationName, compRef);

        /* relation is inserted after the selected relation bar */
        RelationBar selectedBar = compRef.getSelectedRelationBar();
        int insertIndex = compRef.getRelationEditor().getComponentCount();
        if (selectedBar != null) {
            insertIndex = UIUtil.indexOfComponent(selectedBar);
            if (insertIndex == -1) {
                System.out.println("something wrong");
                insertIndex = 0;
            } else {
                insertIndex++;
            }
        }
        this.add(bar, insertIndex);
        updateLayoutConstraints();
        UIUtil.updateEditorBounds(compRef);

        return bar;
    }

    public void updateLayoutConstraints() {
        Component[] components = this.getComponents();
        SpringLayout.Constraints prevCons = null;
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            SpringLayout.Constraints relBarCons = layout.getConstraints(comp);
            if (prevCons == null) {
                relBarCons.setY(Spring.constant(UIUtil.BAR_TOP_MARGIN));
            } else {
                relBarCons.setY(Spring.sum(Spring.constant(UIUtil.GAP_BETWEEN_BARS), prevCons.getConstraint(SpringLayout.SOUTH)));
            }
            relBarCons.setX(Spring.constant(UIUtil.BAR_LEFT_MARGIN));
            prevCons = relBarCons;
        }
    }

    /** clear all relations from editor */
    public void clearAllRelationBars() {
        this.removeAll();
        this.revalidate();
        this.repaint();
    }

    public java.util.List getRelationBarList() {
        java.util.List ret = new ArrayList();
        Component[] components = this.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RelationBar) {
                ret.add(components[i]);
            }
        }
        return ret;
    }

    /* this method needs UIUtil.updateCellAndBarLayout(compRef); and UIUtil.updateEditorBounds(compRef); to be called to show the constraints change made by this method */
    public void restoreRelationBars(List relationBarList) {
        for (int i = 0; i < relationBarList.size(); i++) {
            RelationBar bar = (RelationBar) relationBarList.get(i);
            this.add(bar);
        }

        /* update constraints: align relations side by side from top to bottom */
        updateLayoutConstraints();
    }
}

class MouseListenerForBarDragging extends MouseAdapter {
    ComponentReference compRef;
    BaseBar bar;

    MouseListenerForBarDragging(BaseBar bar) {
        this.bar = bar;
        this.compRef = bar.getComponentReference();
    }

    public void mouseReleased(MouseEvent event) {
        if (compRef.getRelationEditor().startDrag) {
            compRef.getRelationEditor().startDrag = false;
            bar.setSelected(true);
            compRef.getImplementationEditor().hideGhostBar();
            compRef.getImplementationEditor().hideInsertionMarker();
            compRef.getModelEditor().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            int draggedBarIdx = UIUtil.indexOfComponent(bar);
            int insertionIdx = compRef.getRelationEditor().barInsertionIdx;
            if (draggedBarIdx == insertionIdx || (draggedBarIdx + 1) == insertionIdx) {
                // no move
            } else if (draggedBarIdx < insertionIdx) {
                compRef.getRelationEditor().moveRelation(draggedBarIdx, insertionIdx - 1); // two insertion points in front and at the back of the dragged bar indicate the same location. therefore substract one from the insertion index counted in that way.
            } else {
                compRef.getRelationEditor().moveRelation(draggedBarIdx, insertionIdx);
            }
        }
    }
}

class MouseMotionListenerForBarDragging extends MouseMotionAdapter {
    int dragInsetX;
    int dragInsetY;
    Point barOrigin;
    int barWidth;
    int barHeight;
    ComponentReference compRef;
    BaseBar bar;
    private long lastComputedTime = 0;

    MouseMotionListenerForBarDragging(BaseBar bar) {
        this.bar = bar;
        this.compRef = bar.getComponentReference();
    }

    public void mouseDragged(MouseEvent event) {
        if (! compRef.getRelationEditor().startDrag) {
            compRef.getRelationEditor().startDrag = true;
            dragInsetX = event.getX();
            dragInsetY = event.getY();

            compRef.getModelEditor().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

            compRef.clearBarAndCellSelection();
            bar.setBorder(UIUtil.BAR_DR_BORDER);
            int barIdx = UIUtil.indexOfComponent(bar);
            barOrigin = UIUtil.getRelationBarOrigin(barIdx, compRef);

            barWidth = bar.getWidth();
            barHeight = bar.getHeight();
            int ghostX = barOrigin.x + event.getX() - dragInsetX;
            int ghostY = barOrigin.y + event.getY() - dragInsetY;
            compRef.getImplementationEditor().showGhostBar(ghostX, ghostY, barWidth, barHeight);
        }

        if (compRef.getRelationEditor().startDrag) {
            int ghostX = barOrigin.x + event.getX() - dragInsetX;
            int ghostY = barOrigin.y + event.getY() - dragInsetY;
            compRef.getImplementationEditor().moveGhostBar(ghostX, ghostY);
            int currentY = ghostY + barHeight / 2;
            //int currentY = barOrigin.y + event.getY();
            long currentTime = System.currentTimeMillis();
            long gap = currentTime - lastComputedTime;
            if (gap > 250) {
                lastComputedTime = currentTime;
                int[] insertionYAndInsertionIdx = UIUtil.findTheClosestBarInsertionPoint(currentY, compRef);
                int insertionY = insertionYAndInsertionIdx [0];
                compRef.getImplementationEditor().showBarInsertionMarker(insertionY);
                compRef.getRelationEditor().barInsertionIdx = insertionYAndInsertionIdx [1]; // set the current insertion idx
            }
        }
    }
}

class MouseListenerForBarSelection extends MouseAdapter {
    BaseBar bar;
//    BaseCell cell;
    ComponentReference compRef;
    boolean isEnabled = true;

    MouseListenerForBarSelection(BaseBar bar) {
        this.bar = bar;
        this.compRef = bar.getComponentReference();
    }

//    MouseListenerForBarSelection(BaseCell cell) {
//        this.cell = cell;
//        this.compRef = cell.getComponentReference();
//    }

    public void mousePressed(MouseEvent event) {
        if (isEnabled) {
            boolean isDoubleClicked = (event.getClickCount() > 1);
//            if (bar == null) {
//                cell.getBar().getCenterPanel().clicked(isDoubleClicked, event.isControlDown());
//            } else {
//                bar.getCenterPanel().clicked(isDoubleClicked, event.isControlDown());
//            }

            if (MouseEvent.BUTTON3 == event.getButton()) {
                if (! bar.isSelected()) {
                    bar.getCenterPanel().clicked(false, event.isControlDown());
                }
            } else {
                bar.getCenterPanel().clicked(isDoubleClicked, event.isControlDown());
            }
        }
    }

    public void mouseClicked(MouseEvent event) {
        if (MouseEvent.BUTTON3 == event.getButton()) {
            compRef.getImplementationEditor().relPopupMenu.show(event.getComponent(), event.getX(), event.getY());
        }
    }

    /** used when one wants to block this listener */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}

class MouseListenerForCellDragging extends MouseAdapter {
    ComponentReference compRef;
    BaseCell cell;
    boolean isEnabled = true;

    MouseListenerForCellDragging(BaseCell cell) {
        this.cell = cell;
        this.compRef = cell.getComponentReference();
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void mouseReleased(MouseEvent event) {
        if (! isEnabled) {
            return;
        }

        if (compRef.getRelationEditor().startDrag) {
            compRef.getRelationEditor().startDrag = false;

            /* ignore accidental drag */
            if (compRef.getRelationEditor().startDragPoint.distance(event.getX(), event.getY()) < RelationEditor.ACCIDENTAL_MOVE_DISTANCE) {
                ignoreAccidentalDrag();
                return; // make it just like the current dragging motion didn't exist.
            }


            compRef.getImplementationEditor().hideGhostBar();
            compRef.getModelEditor().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            cell.setSelected(true);

            if (event.getButton() == MouseEvent.BUTTON1) {
                if (! event.isControlDown()) { // plain left button drag = prompted-for-what-to-do mode
                    tryJustMove(event);
                } else { // ctrl-down left button drag = no-prompt-just-copy mode
                    justCopy();
                }
            } else if (event.getButton() == MouseEvent.BUTTON3) {
                showCopyOptionPopupMenu(event);
            }
        }
    }

    private void justCopy() {
        int targetBarIdx = compRef.getImplementationEditor().getDragTargetBarIndex();
        BaseCell[] cells = compRef.getSelectedCells();
        if (compRef.getRelationEditor().cellInsertionIdx != -1) {
            compRef.getImplementationEditor().moveSelectedCells(cells, targetBarIdx, true, true);
        } else {
            for (int i = 0; i < cells.length; i++) {
                cells [i].setBorder(UIUtil.CELL_HL_BORDER);
            }
        }
        compRef.getImplementationEditor().hideInsertionMarker();
    }

    /** just move cell if target bar and source bar are the same. otherwise, show popup menu. */
    private void tryJustMove(MouseEvent event) {
        int targetBarIdx = compRef.getImplementationEditor().getDragTargetBarIndex();
        int sourceBarIdx = compRef.getImplementationEditor().getDragSourceBarIndex();
        BaseCell[] cells = compRef.getSelectedCells();
        if (compRef.getRelationEditor().cellInsertionIdx != -1) {
            if (targetBarIdx == sourceBarIdx) {
                compRef.getImplementationEditor().moveSelectedCells(cells, targetBarIdx, false, false);
                compRef.getImplementationEditor().hideInsertionMarker();
            } else {
                showCopyOptionPopupMenu(event);
            }

        } else {
            for (int i = 0; i < cells.length; i++) {
                cells [i].setBorder(UIUtil.CELL_HL_BORDER);
            }
        }
    }

    private void showCopyOptionPopupMenu(MouseEvent e) {
        compRef.getImplementationEditor().dragPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
    }

    private void ignoreAccidentalDrag() {
        compRef.getImplementationEditor().hideGhostBar();
        compRef.getImplementationEditor().hideInsertionMarker();
        compRef.getModelEditor().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        BaseCell[] cells = compRef.getSelectedCells();
        for (int i = 0; i < cells.length; i++) {
            cells [i].setSelected(true);
        }
        //speedup Clog.debug("consider the last mouse move as a accidental move. keep the current selection");
    }


//    private static String getUniqueName(String originParamName, BarSidePanel panel) {
//        int compCount = panel.getComponentCount();
//        boolean isUnique = false;
//        int idx = 0;
//        String tryThisName = originParamName;
//        while (! isUnique) {
//            isUnique = true;
//            for (int i = 0; i < compCount; i++) {
//                if (tryThisName.equals(((BaseCell) panel.getComponent(i)).getParamName())) {
//                    tryThisName = originParamName + "_" + (++idx);
//                    isUnique = false;
//                    break;
//                }
//            }
//
//        }
//        return tryThisName;
//    }
}


class MouseMotionListenerForCellDragging extends MouseMotionAdapter {
    int dragInsetX;
    int dragInsetY;
    Point barOrigin;
    Point cellOrigin;
    int cellWidth;
    int cellHeight;
    ComponentReference compRef;
    BaseCell cell;
    boolean isEnabled = true;
    private long lastComputedTime = 0;

    MouseMotionListenerForCellDragging(BaseCell cell) {
        this.cell = cell;
        this.compRef = cell.getComponentReference();
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void mouseDragged(MouseEvent event) {
        if (! isEnabled) {
            return;
        }

        if (! compRef.getRelationEditor().startDrag) {
            compRef.getRelationEditor().startDrag = true;
            compRef.getRelationEditor().startDragPoint = new Point(event.getX(), event.getY());

            System.out.println("startDragPoint = " + compRef.getRelationEditor().startDragPoint);

            if (compRef.getImplementationEditor().isScriptEditorShown()) {
                compRef.getImplementationEditor().closeScriptEditor(true);
                System.out.println("relation param cell clicked. save changes and close editor");
            }

            dragInsetX = event.getX();
            dragInsetY = event.getY();

            compRef.getModelEditor().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

            //compRef.clearBarAndCellSelection();
            BaseCell[] cells = compRef.getSelectedCells();

            int identicalBarIdx = 0;
            if (cells.length > 0) {
                identicalBarIdx = (cells [0].getBar() instanceof InterfaceBar) ? -1 : UIUtil.indexOfComponent(cells [0].getBar());
            } else {
                identicalBarIdx = Short.MIN_VALUE;
            }

            compRef.getRelationEditor().fixedCellList.clear();
            for (int i = 0; i < cells.length; i++) {
                cells [i].setBorder(UIUtil.CELL_DR_BORDER);
                if (cells [i].getBar().getBarType() == BaseBar.REL_BAR && compRef.getCurrentCNamingService().getRelation(cells [i].getBar().getRelAlias()) instanceof CRemoteRelation) {
                    compRef.getRelationEditor().fixedCellList.add(cells [i].getParamName());
                }

                int tempBarIdx = (cells [0].getBar() instanceof InterfaceBar) ? -1 : UIUtil.indexOfComponent(cells [0].getBar());
                if (tempBarIdx != identicalBarIdx) {
                    identicalBarIdx = Short.MIN_VALUE;
                }
            }

            compRef.getImplementationEditor().setDragSourceBarIndex(identicalBarIdx);

            if (cell.getBar() instanceof RelationBar) {
                int barIdx = UIUtil.indexOfComponent(cell.getBar());
                barOrigin = UIUtil.getRelationBarOrigin(barIdx, compRef);
            } else {
                barOrigin = UIUtil.getInterfaceBarOrigin();
            }

            int[] rowAndColumnIdx = cell.getRowAndColumnIndex();
            cellOrigin = UIUtil.getCellOrigin(rowAndColumnIdx [0], rowAndColumnIdx [1]);

            if (((BarSidePanel) cell.getParent()).getPanelType() == BarSidePanel.ITF_RIGHT || ((BarSidePanel) cell.getParent()).getPanelType() == BarSidePanel.REL_RIGHT) {
                cellOrigin.x = cellOrigin.x + cell.getBar().getLeftPanelSize().width + cell.getBar().getCenterPanelSize().width;
            }

            cellWidth = cell.getWidth();
            cellHeight = cell.getHeight();
            int ghostX = barOrigin.x + cellOrigin.x + event.getX() - dragInsetX;
            int ghostY = barOrigin.y + cellOrigin.y + event.getY() - dragInsetY;
            compRef.getRelationEditor().numberOfSelectedCells = cells.length;

            compRef.getImplementationEditor().showGhostCell(ghostX, ghostY, cellWidth, cellHeight);
            //compRef.getImplementationEditor().updateDragInfoLabel(ghostX, ghostY, event.getX(), event.getY(), event.isControlDown() || isLeftClick);
        }

        if (compRef.getRelationEditor().startDrag) {
            int ghostX = barOrigin.x + cellOrigin.x + event.getX() - dragInsetX;
            int ghostY = barOrigin.y + cellOrigin.y + event.getY() - dragInsetY;
            compRef.getImplementationEditor().moveGhostCell(ghostX, ghostY, event.isControlDown());
            //compRef.getImplementationEditor().updateDragInfoLabel(ghostX, ghostY, event.getX(), event.getY(), event.isControlDown() || isLeftClick);
            int currentX = ghostX + cellWidth / 2;
            int currentY = ghostY + cellHeight / 2;
//            int currentX = barOrigin.x + cellOrigin.x + event.getX();
//            int currentY = barOrigin.y + cellOrigin.y + event.getY();

            compRef.implEditor.showTestMarker(currentX, currentY);

            int barIdx = UIUtil.findBarIndexOnWhichMouseIsOver(currentX, currentY, compRef);

            if (barIdx == -2) {
                compRef.implEditor.hideInsertionMarker();
                compRef.getRelationEditor().cellInsertionIdx = -1;
                return; // mouse is at an ignorable position
            }

            compRef.getImplementationEditor().setDragTargetBarIndex(barIdx);

            long currentTime = System.currentTimeMillis();
            long gap = currentTime - lastComputedTime;
            if (gap > 150) {
                lastComputedTime = currentTime;
//                int currentBarIdx = -1;
//                if (cell.getBar().barType == BaseBar.ITF_BAR) {
//                    currentBarIdx = -1;
//                } else {
//                    currentBarIdx = UIUtil.indexOfComponent(cell.getBar());
//                }
                int currentBarIdx = barIdx;
                //boolean isLeftPanel = (cell.cellType == BaseCell.ITF_INPUT) || (cell.cellType == BaseCell.REL_INPUT);
                int side = UIUtil.findWhichSideMouseIsOver(currentX, currentBarIdx, compRef);
                if (side == 0) {
                    compRef.getRelationEditor().cellInsertionIdx = -1;
                    return;
                }

                boolean isLeftPanel = (side == -1);
                int[] insertionYAndInsertionIdx = UIUtil.findTheClosestCellInsertionPoint(currentBarIdx, isLeftPanel, currentX, currentY, compRef);
                int insertionX = insertionYAndInsertionIdx [0];
                int insertionY = insertionYAndInsertionIdx [1];
                compRef.getRelationEditor().cellInsertionIdx = insertionYAndInsertionIdx [2]; // set the current insertion idx
                compRef.getRelationEditor().insertionIntoLeftPanel = isLeftPanel;
                if (insertionYAndInsertionIdx [2] != -1) {
                    compRef.getImplementationEditor().showCellInsertionMarker(insertionX, insertionY, cellHeight);
                }
            }
        }
    }
}

class MouseListenerForCellSelection extends MouseAdapter {
    BaseCell cell;
    ComponentReference compRef;
    boolean isEnabled = true;


    MouseListenerForCellSelection(BaseCell cell) {
        this.cell = cell;
        this.compRef = cell.getComponentReference();
    }

    public void mousePressed(MouseEvent event) {
        if (isEnabled) {
            boolean isDoubleClicked = (event.getClickCount() > 1);
            if (isDoubleClicked) {
                System.out.println("add this cell to interface");
            }
            cell.cellClicked(event.isControlDown());
        }
    }

    public void mouseClicked(MouseEvent event) {
        if (MouseEvent.BUTTON1 == event.getButton()) {
            if (isEnabled) {
                if (! compRef.getRelationEditor().startDrag && ! cell.isSelected() && ! event.isControlDown()) {
                    cell.cellClicked(event.isControlDown());
                }
            }
        } else if (MouseEvent.BUTTON3 == event.getButton()) {
            compRef.getImplementationEditor().closeScriptEditor(true);
            compRef.getImplementationEditor().cellPopupMenu.show(event.getComponent(), event.getX(), event.getY());
        }
    }

    /** used when one wants to block this listener */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}