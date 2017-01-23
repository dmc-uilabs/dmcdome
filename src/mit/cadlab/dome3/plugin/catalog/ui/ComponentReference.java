package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * ComponentReference is used to easily access ImplementationEditor, CodeCompletionBox, RelationBar, InputParamCell, OutputParamCell
 * one instance of ComponentReference exists for one implementation editor instance
 * the implementation editor instance is given as a constructor
 *
 * note. relation between components
 * model editor : implementation editor = 1 : 1
 * implementation editor : relation editor = 1 : 1
 * relation editor : relation bar = 1 : n
 * relation bar : relation left panel : relation right panel = 1 : 1 : 1
 * relation left panel : input param cell = 1 : n
 * relation right panel : output param cell = 1 : n
 * Sangmok Han
 * Date: 2006. 1. 22.
 */
public class ComponentReference {
    ImplementationEditor implEditor;


    /** implToStyleMapMap: CImplementation as a key -> styleMap as a value.
     *  styleMap: String relAlias as a key -> String styleName as a value */
    Map implToStyleMapMap = new HashMap();


    public ComponentReference(ImplementationEditor implEditor) {
        this.implEditor = implEditor;
        this.implToStyleMapMap.clear();
    }


    /* constructor only for testing */
    protected ComponentReference() {
        this.implToStyleMapMap.clear();
    }

    public ModelEditor getModelEditor() {
        // parent (JSplitPane). get (JFrame == ModelEditor)
        return (ModelEditor) implEditor.getParent().getParent();
    }

    public ModelNavigationPanel getModelNavigationPanel() {
        return getModelEditor().getModelNavigationPanel();
    }

//    public JFrame getFrame() {
//        return frame;
//    }
//
//    public void setFrame(JFrame frame) {
//        this.frame = frame;
//    }

    public CModel getCurrentCModel() {
        return getModelEditor().getModel();
    }

    public CInterface getCurrentCInterface() {
        String curItfName = getImplementationEditor().getInterfaceName();
        return getCurrentCModel().getInterface(curItfName);
    }

    public CImplementation getCurrentCImplementation() {
        String curItfName = getImplementationEditor().getInterfaceName();
        String curImplName = getImplementationEditor().getImplementationName();
        return getCurrentCModel().getInterface(curItfName).getImplementation(curImplName);
    }

    public CNamingService getCurrentCNamingService() {
        String curItfName = getImplementationEditor().getInterfaceName();
        String curImplName = getImplementationEditor().getImplementationName();
        return getCurrentCModel().getInterface(curItfName).getImplementation(curImplName).getNamingService();
    }

    public EvaluationContext getCurrentEvaluationContext() {
        String itfName = getImplementationEditor().getInterfaceName();
        String implName = getImplementationEditor().getImplementationName();
        return getModelEditor().getEvaluationContext(itfName, implName);
    }

    public void resetCurrentEvaluationContext() {
        String itfName = getImplementationEditor().getInterfaceName();
        String implName = getImplementationEditor().getImplementationName();
        getModelEditor().resetEvaluationContext(itfName, implName);
    }

    public ImplementationEditor getImplementationEditor() {
        return implEditor;
    }

    public InterfaceEditor getInterfaceEditor() {
        return getImplementationEditor().getInterfaceEditor();
    }

    public InterfaceBar getInterfaceBar() {
        return getImplementationEditor().getInterfaceEditor().getInterfaceBar();
    }

    public InterfaceInputCell getInterfaceInputCell(String paramName) {
        return this.getInterfaceBar().getInterfaceInputCell(paramName);
    }

    public InterfaceOutputCell getInterfaceOutputCell(String paramName) {
        return this.getInterfaceBar().getInterfaceOutputCell(paramName);
    }

    public RelationEditor getRelationEditor() {
        return getImplementationEditor().getRelationEditor();
    }

    public RelationToolPanel getRelationToolPanel() {
        return getImplementationEditor().getRelationToolBar();
    }

    public RelationBar getRelationBar(String relAlias) {
        return getImplementationEditor().getRelationEditor().getRelationBar(relAlias);
    }

    public BaseBar getBar(String relAlias) {
        if (relAlias.equals(CConstant.ITF_ALIAS)) {
            return getImplementationEditor().getInterfaceEditor().getInterfaceBar();
        }
        return getImplementationEditor().getRelationEditor().getRelationBar(relAlias);
    }

    /** returns the number of RelationBars */
    public int getRelationBarCount() {
        return getImplementationEditor().getRelationEditor().getComponentCount();
    }

    /** returns a List of RelationBars */
    public List getRelationBars() {
        return getImplementationEditor().getRelationEditor().getRelationBarList();
    }

    /** used to iterate through all cells (InterfaceInputCell, InterfaceOutputCell, RelationInputCell, RelationOutputCell, or RelationDerivedCell) in the implementation */
    public List getAllCells() {
        List ret = new ArrayList();

        List interfaceCells = getInterfaceCells();
        ret.addAll(interfaceCells);

        List bars = getRelationBars();
        for (int i = 0; i < bars.size(); i++) {
            RelationBar bar = (RelationBar) bars.get(i);
            List relationCells = getRelationCells(bar.getRelAlias());
            ret.addAll(relationCells);
        }

        return ret;
    }

    /** used to iterate through all cells (InterfaceInputCell or InterfaceOutputCell) in the interface */
    public List getInterfaceCells() {
        List ret = new ArrayList();

        for (int j = 0; j < getInterfaceBar().getLeftPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) getInterfaceBar().getLeftPanel().getComponent(j);
            ret.add(cell);
        }

        for (int j = 0; j < getInterfaceBar().getRightPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) getInterfaceBar().getRightPanel().getComponent(j);
            ret.add(cell);
        }

        return ret;
    }

    /** used to iterate through all input cells (InterfaceInputCell) in the interface */
    public List getInterfaceInputCells() {
        List ret = new ArrayList();

        for (int j = 0; j < getInterfaceBar().getLeftPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) getInterfaceBar().getLeftPanel().getComponent(j);
            ret.add(cell);
        }

        return ret;
    }

    /** used to iterate through all output cells (InterfaceOutputCell) in the interface */
    public List getInterfaceOutputCells() {
        List ret = new ArrayList();

        for (int j = 0; j < getInterfaceBar().getRightPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) getInterfaceBar().getRightPanel().getComponent(j);
            ret.add(cell);
        }

        return ret;
    }

    /** used to iterate through all input cells (RelationInputCell) in a relation specified by relAlias */
    public List getRelationInputCells(String relAlias) {
        List ret = new ArrayList();

        BaseBar bar = getRelationBar(relAlias);

        if (bar == null) {
            throw new RuntimeException("invalid relAlias: no relation aliased as " + relAlias + " is found.");
        }

        for (int j = 0; j < bar.getLeftPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) bar.getLeftPanel().getComponent(j);
            ret.add(cell);
        }

        return ret;
    }

    /** used to iterate through all input cells (RelationOutputCell or RelationDerivedCell) in a relation specified by relAlias */
    public List getRelationOutputCells(String relAlias) {
        List ret = new ArrayList();

        BaseBar bar = getRelationBar(relAlias);

        if (bar == null) {
            throw new RuntimeException("invalid relAlias: no relation aliased as " + relAlias + " is found.");
        }

        for (int j = 0; j < bar.getRightPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) bar.getRightPanel().getComponent(j);
            ret.add(cell);
        }

        return ret;
    }

    /** used to iterate through all BaseCells (RelationInputCell, RelationOutputCell, or RelationDerivedCell)
     * in a relation specified by relAlias */
    public List getRelationCells(String relAlias) {
        List ret = new ArrayList();

        BaseBar bar = getRelationBar(relAlias);

        if (bar == null) {
            throw new RuntimeException("invalid relAlias: no relation aliased as " + relAlias + " is found.");
        }

        for (int j = 0; j < bar.getLeftPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) bar.getLeftPanel().getComponent(j);
            ret.add(cell);
        }

        for (int j = 0; j < bar.getRightPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) bar.getRightPanel().getComponent(j);
            ret.add(cell);
        }

        return ret;
    }

    public BaseCell getCell(String qualifiedParamName) {
        int dotIdx = qualifiedParamName.indexOf(".");
        if (dotIdx == -1) {
            throw new RuntimeException("invalid qualified param name. it should include '.': " + qualifiedParamName);
        }

        String relAlias = qualifiedParamName.substring(0, dotIdx);
        String paramName = qualifiedParamName.substring(dotIdx + 1);
        return getCell(relAlias, paramName);
    }

    /** Note that this method accept relAlias of "itf", too. In that case, it returns Cell in the interface bar */
    public BaseCell getCell(String relAlias, String paramName) {
        BaseBar bar = null;
        if (relAlias.equals(CConstant.ITF_ALIAS)) {
            bar = getInterfaceBar();
        } else {
            bar = getRelationBar(relAlias);
        }

        if (bar == null) {
            throw new RuntimeException("invalid relAlias: no relation aliased as " + relAlias + " is found.");
        }

        for (int j = 0; j < bar.getLeftPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) bar.getLeftPanel().getComponent(j);
            if (paramName.equals(cell.getParamName())) {
                return cell;
            }
        }

        for (int j = 0; j < bar.getRightPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) bar.getRightPanel().getComponent(j);
            if (paramName.equals(cell.getParamName())) {
                return cell;
            }
        }

        throw new RuntimeException("invalid paramName: the given relAlias '" + relAlias + "' is found, but no param named as " + paramName + " is found.");
    }

    public RelationBar getRelationBar(int relationIndex) {
        return getImplementationEditor().getRelationEditor().getRelationBar(relationIndex);
    }

    public RelationInputCell getRelationInputCell(String relationName, String paramName) {
        RelationBar bar = getImplementationEditor().getRelationEditor().getRelationBar(relationName);
        if (bar != null) {
            return bar.getRelationInputCell(paramName);
        } else {
            return null;
        }
    }

    public RelationOutputCell getRelationOutputCell(String relationName, String paramName) {
        RelationBar bar = getImplementationEditor().getRelationEditor().getRelationBar(relationName);
        if (bar != null) {
            return bar.getRelationOutputCell(paramName);
        } else {
            return null;
        }
    }

    public RelationDerivedCell getRelationDerivedCell(String relationName, String paramName) {
        RelationBar bar = getImplementationEditor().getRelationEditor().getRelationBar(relationName);
        if (bar != null) {
            return bar.getRelationDerivedCell(paramName);
        } else {
            return null;
        }
    }

    /** returns first found selected relation bar. returns null if not found */
    public RelationBar getSelectedRelationBar() {
        RelationEditor relEditor = getImplementationEditor().getRelationEditor();
        Component[] comps = relEditor.getComponents();
        for (int i = 0 ; i < comps.length; i++) {
            if (((RelationBar) comps[i]).isSelected) {
                return (RelationBar) comps[i];
            }
        }
        return null;
    }

    /** returns first found selected interface bar. returns null if not found */
    public InterfaceBar getSelectedInterfaceBar() {
        InterfaceBar interfaceBar = this.getInterfaceBar();
        if (interfaceBar != null && interfaceBar.isSelected()) {
            return interfaceBar;
        }
        return null;
    }

    public BaseBar getSelectedBar() {
        BaseBar ret = getSelectedInterfaceBar();
        if (ret == null) {
            ret = getSelectedRelationBar();
        }
        return ret;
    }

    /** returns array of all selected relation bars */
    public RelationBar[] getSelectedRelationBars() {
        java.util.List ret = new ArrayList();
        RelationEditor relEditor = getImplementationEditor().getRelationEditor();
        Component[] comps = relEditor.getComponents();
        for (int i = 0 ; i < comps.length; i++) {
            if (((RelationBar) comps[i]).isSelected) {
                ret.add(comps[i]);
            }
        }
        return (RelationBar[]) ret.toArray(new RelationBar[ret.size()]);
    }

    /** returns firt found selected parameter cells. returns null if not found */
    public BaseCell getSelectedCell() {
        java.util.List ret = new ArrayList();

        InterfaceBar interfaceBar = this.getInterfaceBar();
        if (interfaceBar != null) {
            Container leftPanel = interfaceBar.getLeftPanel();
            for (int j = 0; j < leftPanel.getComponentCount(); j++) {
                if (((BaseCell) leftPanel.getComponent(j)).isSelected()) {
                    return (BaseCell) leftPanel.getComponent(j);
                }
            }
            Container rightPanel = interfaceBar.getRightPanel();
            for (int j = 0; j < rightPanel.getComponentCount(); j++) {
                if (((BaseCell) rightPanel.getComponent(j)).isSelected()) {
                    return (BaseCell) rightPanel.getComponent(j);
                }
            }
        }

        RelationEditor relEditor = getImplementationEditor().getRelationEditor();
        Component[] comps = relEditor.getComponents();
        for (int i = 0 ; i < comps.length; i++) {
            RelationBar relationBar = (RelationBar) comps[i];
            for (int j = 0; j < relationBar.getLeftPanel().getComponentCount(); j++) {
                if (((BaseCell) relationBar.getLeftPanel().getComponent(j)).isSelected()) {
                    return (BaseCell) relationBar.getLeftPanel().getComponent(j);
                }
            }
            for (int j = 0; j < relationBar.getRightPanel().getComponentCount(); j++) {
                if (((BaseCell) relationBar.getRightPanel().getComponent(j)).isSelected()) {
                    return (BaseCell) relationBar.getRightPanel().getComponent(j);
                }
            }
        }
        return null;
    }

    /** returns array of all selected parameter cells */
    public BaseCell[] getSelectedCells() {
        java.util.List ret = new ArrayList();

        InterfaceBar interfaceBar = this.getInterfaceBar();
        if (interfaceBar != null) {
            Container leftPanel = interfaceBar.getLeftPanel();
            for (int j = 0; j < leftPanel.getComponentCount(); j++) {
                if (((BaseCell) leftPanel.getComponent(j)).isSelected()) {
                    ret.add(leftPanel.getComponent(j));
                }
            }
            Container rightPanel = interfaceBar.getRightPanel();
            for (int j = 0; j < rightPanel.getComponentCount(); j++) {
                if (((BaseCell) rightPanel.getComponent(j)).isSelected()) {
                    ret.add(rightPanel.getComponent(j));
                }
            }
        }

        RelationEditor relEditor = getImplementationEditor().getRelationEditor();
        Component[] comps = relEditor.getComponents();
        for (int i = 0 ; i < comps.length; i++) {
            RelationBar relationBar = (RelationBar) comps[i];
            Container leftPanel = relationBar.getLeftPanel();
            for (int j = 0; j < leftPanel.getComponentCount(); j++) {
                if (((BaseCell) leftPanel.getComponent(j)).isSelected()) {
                    ret.add(leftPanel.getComponent(j));
                }
            }
            Container rightPanel = relationBar.getRightPanel();
            for (int j = 0; j < rightPanel.getComponentCount(); j++) {
                if (((BaseCell) rightPanel.getComponent(j)).isSelected()) {
                    ret.add(rightPanel.getComponent(j));
                }
            }
        }
        return (BaseCell[]) ret.toArray(new BaseCell[ret.size()]);
    }

    /** returns if any cell is selected */
    public boolean isAnyCellSelected() {
        InterfaceBar interfaceBar = getImplementationEditor().getInterfaceEditor().getInterfaceBar();
        for (int j = 0; j < interfaceBar.getLeftPanel().getComponentCount(); j++) {
            if (((BaseCell) interfaceBar.getLeftPanel().getComponent(j)).isSelected()) {
                return true;
            }
        }
        for (int j = 0; j < interfaceBar.getRightPanel().getComponentCount(); j++) {
            if (((BaseCell) interfaceBar.getRightPanel().getComponent(j)).isSelected()) {
                return true;
            }
        }

        RelationEditor relEditor = getImplementationEditor().getRelationEditor();
        Component[] comps = relEditor.getComponents();
        for (int i = 0 ; i < comps.length; i++) {
            RelationBar relationBar = (RelationBar) comps[i];
            for (int j = 0; j < relationBar.getLeftPanel().getComponentCount(); j++) {
                if (((BaseCell) relationBar.getLeftPanel().getComponent(j)).isSelected()) {
                    return true;
                }
            }

            for (int j = 0; j < relationBar.getRightPanel().getComponentCount(); j++) {
                if (((BaseCell) relationBar.getRightPanel().getComponent(j)).isSelected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** returns if any bar is selected */
    public boolean isAnyBarSelected() {
        RelationEditor relEditor = getImplementationEditor().getRelationEditor();
        Component[] comps = relEditor.getComponents();
        for (int i = 0 ; i < comps.length; i++) {
            RelationBar bar = (RelationBar) comps[i];
            if (bar.isSelected()) {
                return true;
            }
        }

        InterfaceEditor itfEditor = getImplementationEditor().getInterfaceEditor();
        comps = itfEditor.getComponents();
        for (int i = 0 ; i < comps.length; i++) {
            InterfaceBar bar = (InterfaceBar) comps[i];
            if (bar.isSelected()) {
                return true;
            }
        }
        return false;
    }

    /** clear selection of relation bars */
    public void clearBarAndCellSelection() {
        if (! getImplementationEditor().isImplLoaded()) {
            return;
        }
        InterfaceBar interfaceBar = getImplementationEditor().getInterfaceEditor().getInterfaceBar();
        interfaceBar.setSelected(false);
        for (int j = 0; j < interfaceBar.getLeftPanel().getComponentCount(); j++) {
            ((InterfaceInputCell) interfaceBar.getLeftPanel().getComponent(j)).setSelected(false);
        }
        for (int j = 0; j < interfaceBar.getRightPanel().getComponentCount(); j++) {
            ((InterfaceOutputCell) interfaceBar.getRightPanel().getComponent(j)).setSelected(false);
        }

        RelationEditor relEditor = getImplementationEditor().getRelationEditor();
        Component[] comps = relEditor.getComponents();
        for (int i = 0 ; i < comps.length; i++) {
            RelationBar relationBar = (RelationBar) comps[i];
            relationBar.setSelected(false);
            for (int j = 0; j < relationBar.getLeftPanel().getComponentCount(); j++) {
                ((RelationInputCell) relationBar.getLeftPanel().getComponent(j)).setSelected(false);
            }

            for (int j = 0; j < relationBar.getRightPanel().getComponentCount(); j++) {
                if (relationBar.getRightPanel().getComponent(j) instanceof RelationOutputCell) {
                    ((RelationOutputCell) relationBar.getRightPanel().getComponent(j)).setSelected(false);
                }
                if (relationBar.getRightPanel().getComponent(j) instanceof RelationDerivedCell) {
                    ((RelationDerivedCell) relationBar.getRightPanel().getComponent(j)).setSelected(false);
                }
            }
        }
    }

    /** it first invokes getColorIndexForRelAlias() to get a color index. if a color index is 1, style name is "REL_BG_1" */
    public String getStyleNameForRelAlias(String relAlias) {
        return "REL_ALIAS_STYLE_" + getColorIndexForRelAlias(relAlias, false);
    }

    /** returns a color index for relAlias, if relAlias is CConstant.ITF_ALIAS, it returns 0. */
    public int getColorIndexForRelAlias(String relAlias, boolean assignNewColorIndexIfRelAliasIsNew) {
        if (CConstant.ITF_ALIAS.equals(relAlias)) {
            return 0;
        }

        CImplementation impl = getCurrentCImplementation();
        String itfAndImplKey = impl.getParentInterface().getName() + "/" + impl.getName();
        Map relAliasToColorIdxMap = (Map) implToStyleMapMap.get(itfAndImplKey);
        if (relAliasToColorIdxMap == null) {
            relAliasToColorIdxMap = new HashMap();
            //speedup Clog.debug("relAliasToColorIdxMap has been initialized");
            implToStyleMapMap.put(itfAndImplKey, relAliasToColorIdxMap);
        }
        Integer colorIdx = (Integer) relAliasToColorIdxMap.get(relAlias);

        if (colorIdx == null) {
            if (! assignNewColorIndexIfRelAliasIsNew) {
                if (impl.getRelationAliases().contains(relAlias)) {
                    /* even when assignNewColorIndexIfRelAliasIsNew is false, we are good to assign a new color index for the given relAlias if the relAlias is a valid, existing one of this implementation
                     * this happens when some interface output param's mapping script is "relA.result." because a relation bar for "relA" has not instantiated, relAliasToColorIdxMap won't have a color index of "relA"
                     * we resolve this problematic situation by assign a new color index for "relA"
                     */
                } else {
                    throw new RuntimeException("exception at getColorIndexForRelAlias(). non-existing invalid relAlias is given.");
                }
            }

            /* no color index exists for the relAlias */
            colorIdx = new Integer(getRandomInteger(1, UIUtil.REL_BG_COLORS.length - 1));

            /* trick to possibly pick a different color */
            int tryMax = 5;
            Collection values = relAliasToColorIdxMap.values(); // values are Integer set
            while (tryMax-- > 0) {
                if (values.contains(colorIdx)) {
                    colorIdx = new Integer(getRandomInteger(1, UIUtil.REL_BG_COLORS.length - 1));
                } else {
                    break;
                }
            }
            relAliasToColorIdxMap.put(relAlias, colorIdx);
            //speedup Clog.debug("relAliasToColorIdxMap: " + relAliasToColorIdxMap);
        }

        return colorIdx.intValue();
    }

	private static int getRandomInteger(int startBoundary, int endBoundary) {
		int ret = startBoundary + (int) (Math.random() * (double) (endBoundary - startBoundary) + 0.5);
		return ret;
	}

    public boolean isValidRelAlias(String relAlias) {
        if (CConstant.ITF_ALIAS.equals(relAlias)) {
            return true;
        }
        CNamingService namingService = getCurrentCNamingService();
        Set relations = namingService.getRelations();
        for (Iterator i = relations.iterator(); i.hasNext();) {
            CRelation relation = (CRelation) i.next();
            if (relAlias.equals(relation.getRelAlias())) {
                return true;

            }
        }
        return false;
    }

    public boolean isValidQualifiedParamName(String qualifiedParamName) {
        CNamingService namingService = getCurrentCNamingService();
        Set params = namingService.getParameters();
        for (Iterator i = params.iterator(); i.hasNext();) {
            CParameter param = (CParameter) i.next();
            if (qualifiedParamName.equals(param.getQualifiedName())) {
                return true;
            }
        }
        return false;
    }

    public static Graphics getGraphics() {
        return ModelEditorKit.compRef.getModelEditor().getGraphics();
    }

    public CellConfig getCellConfig() {
        return getModelEditor().getCellConfig();
    }

    public void markUnsavedChanges() {
        getModelEditor().setHasUnsavedChanges(true);
    }
}
