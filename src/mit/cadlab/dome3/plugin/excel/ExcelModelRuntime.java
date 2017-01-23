package mit.cadlab.dome3.plugin.excel;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.excel.dataobject.ExcelEnumeration;
import mit.cadlab.dome3.plugin.excel.dataobject.ExcelMatrix;
import mit.cadlab.dome3.plugin.excel.dataobject.ExcelReal;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Name: ExcelModelRuntime
 * User: thorek
 * Date: Mar 25, 2003
 * Time: 1:18:36 PM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class ExcelModelRuntime extends PluginModelRuntime {
    protected ExcelPlugin plg;

    public ExcelModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource) {
        super(parentId, xml, isProjectResource);
        loadNativeModel();
    }

    protected void executeNativePlugin(List affectedOutputParams) {
        if (!plg.isModelLoaded()) {
            plg.loadModel();
        }
        plg.execute(affectedOutputParams);
    }

    /**
     * Halt the native model.
     */
    public void stopModel() {
        plg.unloadModel();
    }

    public void deleteModel() {
	    if (solver.isSolving()) {
		    solver.stopSolving();
		    waitingToDie = Boolean.TRUE;
		    return;
	    }
        plg.deleteModel();
	    super.deleteModel();
    }

    protected void loadNativeModel() {
        // get configuration parameters
        //String fileName = ((DomeFile) pluginConfiguration.getSetupParameter(ExcelConfiguration.FILE_LOCATION).getCurrentDataObject()).getFilePath();
        String fileName = getMainModelFileName();
        EnumerationData verData = (EnumerationData) pluginConfiguration.getSetupParameter(ExcelConfiguration.SOFTWARE_VERSION).getCurrentDataObject();
        String excelVersion = verData.getElementName(verData.getLastSelection());
        String excelDllName = ExcelConfiguration.EXCEL_2010_DLL; // default
        
        if (ExcelConfiguration.EXCEL_97.equals(excelVersion))
            excelDllName = ExcelConfiguration.EXCEL_97_DLL;
        // Added next two lines
        if (ExcelConfiguration.EXCEL_2000.equals(excelVersion))
            excelDllName = ExcelConfiguration.EXCEL_2KXP_DLL;
       // Added info for Debugging
        Debug.trace(Debug.ALL, "ExcelModelRuntime:excelVersion =" + excelVersion);
        Debug.trace(Debug.ALL, "ExcelModelRuntime:excelDllName =" + excelDllName);
        Debug.trace(Debug.ALL, "ExcelModelRuntime:fileName =" + fileName);
            
        boolean runInForeground = ((DomeBoolean) pluginConfiguration.getSetupParameter(ExcelConfiguration.RUN_IN_FOREGROUND).getCurrentDataObject()).getValue();
        if (fileName == null)
            throw new UnsupportedOperationException("can not start Excel model - no filename");
        plg = new ExcelPlugin(excelDllName, fileName, runInForeground);
        plg.createModel();

        // create map of dome object to corresponding excel object
        Iterator it = getModelObjects().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                Object map = getPluginMappingManager().getMappingObjectForParameter(p);
                if (map != null) {
                    createExcelLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
                }
            }
        }
    }

    protected void createExcelLink (Parameter p, String refString, boolean isInput)
    {
	    String[] refData = parseRefString(refString);
	    Object xlObj = null;
	    if (p.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
		    xlObj = (plg).createReal(refData[0], refData[1], p);
		    if (!isInput)
			    ((ExcelReal) xlObj).setIsResult(true);
	    }
	    else if (p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName())) {
		    if (refData[1].indexOf(":") == -1)
			    throw new IllegalArgumentException("invalid Excel matrix reference: " + refString);
		    xlObj = (plg).createMatrix(refData[0], refData[1], p);
		    if (!isInput)
			    ((ExcelMatrix) xlObj).setIsResult(true);
	    }
	    else if (p.getCurrentType().equals(DomeEnumeration.TYPE_INFO.getTypeName())) {
		    xlObj = (plg).createEnumeration(refData[0], refData[1], p);
		    if (!isInput)
			    ((ExcelEnumeration) xlObj).setIsResult(true);
	    }
    }

    protected String[] parseRefString (String refString) {
        if (refString == null)
            throw new IllegalArgumentException("null Excel reference");
        refString = refString.trim();
        if (refString.length() < 2)
            throw new IllegalArgumentException("invalid Excel reference: " + refString);
        String[] refData = new String[2];
        int sheetSep = refString.indexOf("!");
        if (sheetSep == -1 || sheetSep == 0) {
            System.out.println("Warning: no sheet reference specified for " + refString + "; assuming Sheet1");
            refData[0] = "Sheet1";
        } else {
            refData[0] = refString.substring(0, sheetSep).trim();
        }
        refData[1] = refString.substring(sheetSep + 1).trim();
        if (refData[1].length() < 2)
            throw new IllegalArgumentException("invalid Excel cell reference: " + refString);
        return refData;
    }

}
