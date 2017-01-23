package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 25
 */
public class InterfaceEditor extends JPanel {

    private SpringLayout layout;
    private ComponentReference compRef;
    private InterfaceBar interfaceBar;
    private boolean lastShowingLocal = false;

    public InterfaceEditor(ComponentReference compRef) {
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
            System.out.println("interface editor clicked. save changes and close editor");
        }
       compRef.clearBarAndCellSelection();
    }


    public ComponentReference getComponentReference() {
        return compRef;
    }

    public InterfaceBar getInterfaceBar() {
        return interfaceBar;
    }

    public void clearInterfaceBar() {
        this.removeAll();
        this.revalidate();
        this.repaint();
    }

    /** this method creates InterfaceBar with given name and returns it */
    public InterfaceBar setInterfaceBar(String interfaceName) {
        this.removeAll();

        interfaceBar = new InterfaceBar(interfaceName, compRef);
        this.add(interfaceBar);

        SpringLayout.Constraints relBarCons = layout.getConstraints(interfaceBar);
        relBarCons.setY(Spring.constant(UIUtil.BAR_TOP_MARGIN));
        relBarCons.setX(Spring.constant(UIUtil.BAR_LEFT_MARGIN));

        UIUtil.updateEditorBounds(compRef);

        return interfaceBar;
    }

    /* this method needs UIUtil.updateCellAndBarLayout(compRef); and UIUtil.updateEditorBounds(compRef); to be called to show the constraints change made by this method */
    public void restoreInterfaceBar(InterfaceBar cachedInterfaceBar) {
        this.removeAll();
        this.add(cachedInterfaceBar);
        this.interfaceBar = cachedInterfaceBar;

        /* copy the ordering of the current interface parameter cells to the cached interface bar, which is going to be displayed */
        updateOrderingOfCachedInterfaceBar(cachedInterfaceBar.getLeftPanel(), compRef.getCurrentCInterface().getInputParameterList(), compRef);
        updateOrderingOfCachedInterfaceBar(cachedInterfaceBar.getRightPanel(), compRef.getCurrentCInterface().getOutputParameterList(), compRef);

        /* update constraints: align interface to top and left */
        SpringLayout.Constraints relBarCons = layout.getConstraints(cachedInterfaceBar);
        relBarCons.setY(Spring.constant(UIUtil.BAR_TOP_MARGIN));
        relBarCons.setX(Spring.constant(UIUtil.BAR_LEFT_MARGIN));
    }

    private static void updateOrderingOfCachedInterfaceBar(BarSidePanel sidePanel, List newParamList, ComponentReference compRef) {
        List paramCellList = new ArrayList(Arrays.asList(sidePanel.getComponents()));

        Map paramNameToParamCellMap = new HashMap();
        for (int i = 0; i < paramCellList.size(); i++) {
            BaseCell cell = (BaseCell) paramCellList.get(i);
            paramNameToParamCellMap.put(cell.getParamName(), cell);
        }

        sidePanel.removeAll();
        for (int i = 0; i < newParamList.size(); i++) {
            CParameter newParam = (CParameter) newParamList.get(i);
            BaseCell paramCell = (BaseCell) paramNameToParamCellMap.get(newParam.getName());
            if (paramCell != null && isCellReusable(newParam, paramCell)) {
                sidePanel.add(paramCell);
            } else {
                if (sidePanel instanceof InterfaceBarLeftPanel) {
                    paramCell = new InterfaceInputCell(newParam.getNamespace(), newParam.getName(), newParam.getDataType(), newParam.getUnit(), compRef);
                    sidePanel.add(paramCell);
                } else if (sidePanel instanceof InterfaceBarRightPanel) {
                    paramCell = new InterfaceOutputCell(newParam.getNamespace(), newParam.getName(), newParam.getDataType(), newParam.getUnit(), "", compRef);
                    sidePanel.add(paramCell);
                }
            }
        }
        sidePanel.validate();
    }

    private static boolean isCellReusable(CParameter param, BaseCell cell) {
        if (cell.getDataType().equals(param.getDataType()) && cell.getUnit().equals(param.getUnit())) {
            return true;
        }
        return false;
    }

    public void editInterface() {
        /* close script editor */
        compRef.getImplementationEditor().closeScriptEditor(true);

        /* relation is inserted after the selected relation bar */
        String relAlias = interfaceBar.getRelAlias(); // for interface bar, relAlias is itf

        String implName = compRef.getImplementationEditor().getImplementationName();
        String itfName = compRef.getImplementationEditor().getInterfaceName();

        CModel model = compRef.getCurrentCModel();
        CInterface itf = model.getInterface(itfName);
        CImplementation impl = itf.getImplementation(implName);

        CNamingService namingService = impl.getNamingService();

        InterfaceDialog itfDialog = new InterfaceDialog(compRef, true);
        itfDialog.updateLabelsForEditing(true);

        itfDialog.setInterfaceName(itf.getName());
        itfDialog.setImplementationName(impl.getName());

        List paramInfoList = new ArrayList();
        List iParamNameList = impl.getInputParameterNames();
        for (int i = 0; i < iParamNameList.size(); i++) {
            String paramName = (String) iParamNameList.get(i);
            CInterfaceInputParameter cParam = namingService.getInterfaceInputParameter(relAlias + "." + paramName);
            paramInfoList.add(new String[] { cParam.getName(), cParam.getDataType(), cParam.getUnit(), cParam.getDefaultValue() });
        }
        itfDialog.setInputParamList(paramInfoList);

        paramInfoList = new ArrayList();
        List oParamNameList = impl.getOutputParameterNames();
        for (int i = 0; i < oParamNameList.size(); i++) {
            String paramName = (String) oParamNameList.get(i);
            CInterfaceOutputParameter cParam = namingService.getInterfaceOutputParameter(relAlias + "." + paramName);
            paramInfoList.add(new String[] { cParam.getName(), cParam.getDataType(), cParam.getUnit(), cParam.getDefaultValue() });
        }
        itfDialog.setOutputParamList(paramInfoList);

        List itfDepInfoList = new ArrayList();
        for (int i = 0; i < oParamNameList.size(); i++) {
            String oParamName = (String) oParamNameList.get(i);
            Set drivers = itf.getDriversOf(oParamName);
            itfDepInfoList.add(new Object[] { oParamName, drivers.toArray(new String[drivers.size()]) });
        }

        List implDepInfoList = new ArrayList();
        for (int i = 0; i < oParamNameList.size(); i++) {
            String oParamName = (String) oParamNameList.get(i);
            Set drivers = impl.getDriversOf(oParamName);
            implDepInfoList.add(new Object[] { oParamName, drivers.toArray(new String[drivers.size()]) });
        }

        itfDialog.setInterfaceDependency(itfDepInfoList); //todo: interface depedency can be acquired by superposing all impl dependency

        itfDialog.setImplementationDependency(iParamNameList, oParamNameList, implDepInfoList);

        itfDialog.setPanelVisibility(lastShowingLocal);

        itfDialog.setVisible(true);

        lastShowingLocal = itfDialog.isLocalPanelVisible();

        /* handles submission */
        if (itfDialog.isSubmitted()) {
            if (itfDialog.isLocalPanelVisible()) {
                updateImplementationNameAndCModel(itfDialog);
            } else {
                updateInterfaceBarAndCModel(itfDialog);
            }
            compRef.getCurrentEvaluationContext().refresh();
            EvaluationMode.initEditorPanes(compRef);
        }
    }

    /** popup InterfaceDialog, collect user inputs, and add a CInterface */
    public void addInterface() {
        InterfaceDialog itfDialog = new InterfaceDialog(compRef, false);
        itfDialog.updateLabelsForEditing(false);
        itfDialog.setPanelVisibility(false);

        itfDialog.setVisible(true);

        /* handles submission */
        if (itfDialog.isSubmitted()) {
            if (itfDialog.isLocalPanelVisible()) {
                /* currently this condition is never satisfied because we hided it */
                addImplementationNameAndCModel(itfDialog.getImplementationName());
            } else {
                addInterfaceBarAndCModel(itfDialog);
            }
            compRef.getCurrentEvaluationContext().refresh();
            EvaluationMode.initEditorPanes(compRef);
        }
    }

    /** handle case when only impl name is changed */
    public void updateImplementationNameAndCModel(InterfaceDialog itfDialog) {
        String oldImplName = compRef.getImplementationEditor().getImplementationName();
        String newImplName = itfDialog.getImplementationName();
        String itfName = compRef.getImplementationEditor().getInterfaceName();

        /* unindex CodeBase */
        if (! newImplName.equalsIgnoreCase(oldImplName)) {
            compRef.getImplementationEditor().unindexImplementation(oldImplName);
            compRef.getModelEditor().clearModelEditorCache(null, oldImplName);
        }

        /* update CModel */
        for (Iterator i = compRef.getCurrentCModel().getInterfaceMap().entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            CInterface curItf = (CInterface) entry.getValue();
            /* change name of implementation */
            CImplementation curImpl = curItf.getImplementation(oldImplName);
            curImpl.setName(newImplName);

            /* remove the entry with old name, and put an entry with new name */
            curItf.getImplementationMap().remove(oldImplName);
            curItf.getImplementationMap().put(newImplName, curImpl);
        }

        /* index CodeBase */
        if (! newImplName.equalsIgnoreCase(oldImplName)) {
            compRef.getImplementationEditor().indexImplementation(newImplName);
        }

        /* update tree */
        ModelNavigationPanel navPanel = compRef.getModelEditor().getModelNavigationPanel();
        navPanel.renameImplementationNode(oldImplName, itfDialog.getImplementationName());

        /* update implementation editor's member var */
        compRef.getImplementationEditor().setImplementationName(itfDialog.getImplementationName());

//        InterfaceBar bar  = this.getInterfaceBar();
//        bar.setRelName(impl.getName());

        //UIUtil.updateEditorBounds(compRef);

        /* mark this model has been changed */
        compRef.markUnsavedChanges();
    }

    /** add interface using data collected from InterfaceDialog */
    public void addInterfaceBarAndCModel(InterfaceDialog itfDialog) {

        String itfName = itfDialog.getInterfaceName();

        /* update CModel */
        CModel model = compRef.getCurrentCModel();
        CInterface itf = model.addInterface(itfName);

        /* initialize CInterface object */
        java.util.List paramList = itfDialog.getInputParamList();
        for (int i = 0; i < paramList.size(); i++) {
            String[] paramInfo = (String[]) paramList.get(i);
            CInterfaceInputParameter param = itf.getInputParameter(paramInfo[0]);
            if (param == null) {
                param = itf.addInputParameter(paramInfo[0]);
            }
            param.setDataType(paramInfo[1]);
            param.setUnit(paramInfo[2]);
            param.setDefaultValue(paramInfo[3]);
        }

        paramList = itfDialog.getOutputParamList();
        for (int i = 0; i < paramList.size(); i++) {
            String[] paramInfo = (String[]) paramList.get(i);
            CInterfaceOutputParameter param = itf.getOutputParameter(paramInfo[0]);
            if (param == null) {
                param = itf.addOutputParameter(paramInfo[0]);
            }
            param.setDataType(paramInfo[1]);
            param.setUnit(paramInfo[2]);
            param.setDefaultValue(paramInfo[3]);
        }

        /* get a set of current implementation names, and add empty implementation under the new interface */
        Set implNames = compRef.getCurrentCInterface().getImplementationMap().keySet();
        for (Iterator i = implNames.iterator(); i.hasNext();) {
            String implName = (String) i.next();
            CImplementation newImpl = itf.addImplementation(implName);
            newImpl.synchronizeInterfaceParameters();
        }

        /* update CodeBase */
        implNames = compRef.getCurrentCInterface().getImplementationMap().keySet();
        for (Iterator i = implNames.iterator(); i.hasNext();) {
            String curImplName = (String) i.next();
            /* index CodeBase */
            compRef.getImplementationEditor().indexRelationOrInterface(itfName, curImplName, CConstant.ITF_ALIAS);
        }

        /* update interface dependency */
        List depList = itfDialog.getInterfaceDependency();
        itf.clearDependency();
        for (int i = 0; i < depList.size(); i++) {
            Object[] item = (Object[]) depList.get(i);
            String outputName = (String) item[0];
            String[] inputNames = (String[]) item[1];
            for (int j = 0; j < inputNames.length; j++) {
                String inputName = inputNames[j];
                itf.setDependency(inputName, outputName);
            }
        }

        /* update tree. it will select newly added interface so that user can see the result */
        compRef.getModelNavigationPanel().addInterfaceNode(itfName);
    }

    /** add implementation using data collected from InterfaceDialog */
    public void addImplementationNameAndCModel(String newImplName) {
        /* update CModel */
        Collection itfSet = compRef.getCurrentCModel().getInterfaceMap().values();
        for (Iterator i = itfSet.iterator(); i.hasNext();) {
            CInterface itf = (CInterface) i.next();
            itf.addImplementation(newImplName);
        }

        /* update tree */
        compRef.getModelNavigationPanel().addImplementationNode(newImplName);

        /* mark this model has been changed */
        compRef.markUnsavedChanges();
    }

    /** handle case when interface is changed */
    public InterfaceBar updateInterfaceBarAndCModel(InterfaceDialog itfDialog) {

        String implName = compRef.getImplementationEditor().getImplementationName();
        String oldItfName = compRef.getImplementationEditor().getInterfaceName();
        String newItfName = itfDialog.getInterfaceName();

        if (! newItfName.equals(oldItfName)) {
            compRef.getImplementationEditor().unindexInterface(oldItfName);
            compRef.getModelEditor().clearModelEditorCache(oldItfName, null);
        }

        /* update CModel */
        CModel model = compRef.getCurrentCModel();
        CInterface itf = model.getInterface(oldItfName);
        itf.setName(itfDialog.getInterfaceName());

        CImplementation impl = itf.getImplementation(implName);

        /* update implementation editor's member var */
        compRef.getImplementationEditor().setInterfaceName(newItfName);

        /* update tree */
        ModelNavigationPanel navPanel = compRef.getModelEditor().getModelNavigationPanel();
        navPanel.renameInterfaceNode(oldItfName, newItfName);

        /* update CInterface object */
        java.util.List paramList = itfDialog.getInputParamList();

        /* remove input */
        java.util.List iParamNames = itf.getInputParameterNames();
        for (int i = 0; i < iParamNames.size(); i++) {
            String paramNameInItf = (String) iParamNames.get(i);
            boolean isValidParamName = false;
            for (int j = 0; j < paramList.size(); j++) {
                String paramNameInDialog = ((String[]) paramList.get(j) )[0];
                if (paramNameInDialog.equals(paramNameInItf)) {
                    isValidParamName = true;
                }
            }
            if (! isValidParamName) {
                itf.removeInputParameter(paramNameInItf);
            }
        }

        /* add/update input */
        for (int i = 0; i < paramList.size(); i++) {
            String[] paramInfo = (String[]) paramList.get(i);
            CInterfaceInputParameter param = itf.getInputParameter(paramInfo[0]);
            if (param == null) {
                param = itf.addInputParameter(paramInfo[0]);
            }
            param.setDataType(paramInfo[1]);
            param.setUnit(paramInfo[2]);
            param.setDefaultValue(paramInfo[3]);
        }

        paramList = itfDialog.getOutputParamList();

        /* remove output */
        java.util.List oParamNames = itf.getOutputParameterNames();
        for (int i = 0; i < oParamNames.size(); i++) {
            String paramNameInItf = (String) oParamNames.get(i);
            boolean isValidParamName = false;
            for (int j = 0; j < paramList.size(); j++) {
                String paramNameInDialog = ((String[]) paramList.get(j) )[0];
                if (paramNameInDialog.equals(paramNameInItf)) {
                    isValidParamName = true;
                }
            }
            if (! isValidParamName) {
                itf.removeOutputParameter(paramNameInItf);
            }
        }

        /* add/update output */
        for (int i = 0; i < paramList.size(); i++) {
            String[] paramInfo = (String[]) paramList.get(i);
            CInterfaceOutputParameter param = itf.getOutputParameter(paramInfo[0]);
            if (param == null) {
                param = itf.addOutputParameter(paramInfo[0]);
            }
            param.setDataType(paramInfo[1]);
            param.setUnit(paramInfo[2]);
            param.setDefaultValue(paramInfo[3]);
        }

        //impl.synchronizeInterfaceParameters();

        /* get a set of current implementation names, and add empty implementation under the new interface */
        Set implNames = compRef.getCurrentCInterface().getImplementationMap().keySet();
        for (Iterator i = implNames.iterator(); i.hasNext();) {
            String curImplName = (String) i.next();
            CImplementation curImpl = itf.getImplementation(curImplName);
            curImpl.synchronizeInterfaceParameters();
        }

        /* update interface dependency */
        List depList = itfDialog.getInterfaceDependency();
        itf.clearDependency();
        for (int i = 0; i < depList.size(); i++) {
            Object[] item = (Object[]) depList.get(i);
            String outputName = (String) item[0];
            String[] inputNames = (String[]) item[1];
            for (int j = 0; j < inputNames.length; j++) {
                String inputName = inputNames[j];
                itf.setDependency(inputName, outputName);
            }
        }

        if (! newItfName.equals(oldItfName)) {
            compRef.getImplementationEditor().indexInterface(newItfName);
            compRef.getModelEditor().clearModelEditorCache(oldItfName, null);
        }

        /* based on created CImplementation object, construct GUI */
        //InterfaceBar bar  = this.setInterfaceBar(implName);
        InterfaceBar bar  = this.setInterfaceBar(newItfName);

        java.util.List inputParams = itf.getInputParameterNames();
        for (Iterator i = inputParams.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter param = impl.getNamingService().getParameter(CConstant.ITF_ALIAS + "." + paramName);
//            param.setDataType(param.getDataType());
//            param.setUnit(param.getUnit());
//            param.setDefaultValue(param.getDefaultValue());
            bar.addInterfaceInputCell(param.getName(), param.getDataType(), param.getUnit());
        }

        java.util.List outputParams = itf.getOutputParameterNames();
        for (Iterator i = outputParams.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) impl.getNamingService().getParameter(CConstant.ITF_ALIAS + "." + paramName);
//            param.setDataType(param.getDataType());
//            param.setUnit(param.getUnit());
//            param.setDefaultValue(param.getDefaultValue());
            bar.addInterfaceOutputCell(param.getName(), param.getDataType(), param.getUnit(), param.getMapping().getMappingScript());
        }

        /* below two lines update layout and refresh screen */
        UIUtil.updateEditorBounds(compRef);

        /* mark this model has been changed */
        compRef.markUnsavedChanges();

        return bar;
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

    public void removeInterface() {
        ModelNavigationPanel naviPanel = compRef.getModelNavigationPanel();
        if ("interface".equals(naviPanel.getSelectedNodeType())) {
            String itfName = naviPanel.getSelectedNodeInfo().itfName;
            Object[] options = { "OK", "Cancel" };
            int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(naviPanel), "Click OK to delete the selected interface", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (answer != 0) {
                return;
            } else {
                /* remove from CModel */
                CModel model = compRef.getCurrentCModel();
                model.removeInterface(itfName);

                /* remove from tree */
                naviPanel.removeInterfaceNode(itfName);

                /* mark this model has been changed */
                compRef.markUnsavedChanges();
            }
        } else {
            Object[] options = { "OK" };
            JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(naviPanel), "Please select an interface to deleted in the navigation panel", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        }
    }
}
