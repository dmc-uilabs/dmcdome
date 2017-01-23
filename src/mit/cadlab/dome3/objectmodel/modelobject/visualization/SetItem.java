package mit.cadlab.dome3.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.AbstractModelObject;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Wei Mao
 * Date: Sep 30, 2003
 * Time: 2:11:49 AM
 * To change this template use Options | File Templates.
 */
public class SetItem implements XMLSupport
{
    public DomeObject data;
    public String aliasname;
    public boolean Selected;
    public String[] rowAliasName;
	public String[] colAliasName;
    public boolean[] rowSelected;
	public boolean[] colSelected;
    public int[][] rowColor;
	public int[][] colColor;

    public static final String XML_TAG = "setItem";


    /**
     *
     * @param d     : DomeVector Object
     * @param isSelected  :
     * @param alias  : to define the series name
     */
    public SetItem(DomeObject d, String alias, boolean isSelected)
    {
        data = d;
        aliasname = alias;
        Selected = isSelected;
        if (((Parameter) d).getDataObjectForType("Matrix") != null){
            rowAliasName = new String[((DomeMatrixData)((Parameter) d).getDataObjectForType("Matrix")).getRowCount()];
            rowSelected = new boolean[((DomeMatrixData)((Parameter) d).getDataObjectForType("Matrix")).getRowCount()];
	        rowColor = new int[((DomeMatrixData)((Parameter) d).getDataObjectForType("Matrix")).getRowCount()][4];
            for (int i = 0; i < ((DomeMatrixData)((Parameter) d).getDataObjectForType("Matrix")).getRowCount(); i++){
                rowAliasName[i] = alias + " - Row " + i;
                rowSelected[i] = isSelected;
	            rowColor[i][0] = 0;
            }

	        colAliasName = new String[((DomeMatrixData)((Parameter) d).getDataObjectForType("Matrix")).getColumnCount()];
	        colSelected = new boolean[((DomeMatrixData)((Parameter) d).getDataObjectForType("Matrix")).getColumnCount()];
		    colColor = new int[((DomeMatrixData)((Parameter) d).getDataObjectForType("Matrix")).getColumnCount()][4];
	        for (int i = 0; i < ((DomeMatrixData)((Parameter) d).getDataObjectForType("Matrix")).getColumnCount(); i++){
	            colAliasName[i] = alias + " - Col " + i;
	            colSelected[i] = isSelected;
		        colColor[i][0] = 0;
	        }
        } else {
	        rowColor = new int[1][4];         // Vector
	        rowColor[0][0] = 0;
        }
    }


	public SetItem(SetItem item)
	{
		this.data = item.data;
		this.aliasname = item.aliasname;
		this.Selected = item.Selected;
        if (item.rowAliasName != null) {
            if (item.rowAliasName.length != 0) {
                this.rowAliasName = new String[item.rowAliasName.length];
                for (int i = 0; i < item.rowAliasName.length; i++) {
                    this.rowAliasName[i] = item.rowAliasName[i];
                }
            }
        }
        if (item.rowSelected != null) {
            if (item.rowSelected.length != 0) {
                this.rowSelected = new boolean[item.rowSelected.length];
                for (int i = 0; i < item.rowSelected.length; i++) {
                    this.rowSelected[i] = item.rowSelected[i];
                }
            }
        }
		if (item.rowColor != null) {
			if (item.rowColor.length != 0) {
				this.rowColor = new int[item.rowColor.length][4];
				for (int i = 0; i < item.rowColor.length; i++) {
					for (int j = 0; j < 4; j++) {
						this.rowColor[i][j] = item.rowColor[i][j];
					}
				}
			}
		}

		if (item.colAliasName != null) {
		    if (item.colAliasName.length != 0) {
		        this.colAliasName = new String[item.colAliasName.length];
		        for (int i = 0; i < item.colAliasName.length; i++) {
		            this.colAliasName[i] = item.colAliasName[i];
		        }
		    }
		}
		if (item.colSelected != null) {
		    if (item.colSelected.length != 0) {
		        this.colSelected = new boolean[item.colSelected.length];
		        for (int i = 0; i < item.colSelected.length; i++) {
		            this.colSelected[i] = item.colSelected[i];
		        }
		    }
		}
		if (item.colColor != null) {
			if (item.colColor.length != 0) {
				this.colColor = new int[item.colColor.length][4];
				for (int i = 0; i < item.colColor.length; i++) {
					for (int j = 0; j < 4; j++) {
						this.colColor[i][j] = item.colColor[i][j];
					}
				}
			}
		}

	}

    /**
     * a constructor for read in xml
     * @param xmlElement
     */
    public SetItem(ModelObjectScope scope, Element xmlElement)
    {
        XMLUtils.makeRootElement(xmlElement);
        if (xmlElement.attributeValue("isSelected").equals((new Boolean(true)).toString()))
            Selected = true;
        else
            Selected = false;
        aliasname = xmlElement.elementText("alias");
        // load data
        Element paramterEle = (Element) xmlElement.selectSingleNode("parameter");
        Id paramId = AbstractModelObject.parseXmlRef(paramterEle);
        DomeObject param = scope.getModelObjectById(paramId);
        if (param != null)
            data = param;
	    if (((Parameter) data).getDataObjectForType("Matrix") != null){
		    rowAliasName = new String[((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getRowCount()];
		    rowSelected = new boolean[((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getRowCount()];
            rowColor = new int[((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getRowCount()][4];
		    for (int i = 0; i < ((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getRowCount(); i++){
			    Element rowEle = (Element) xmlElement.selectSingleNode("row"+(i+1));
			    rowAliasName[i] = rowEle.attributeValue("rowAlias");
			    rowSelected[i] = (new Boolean(rowEle.attributeValue("rowSelected"))).booleanValue();
			    rowColor[i][0] = Integer.valueOf(rowEle.attributeValue("setColor")).intValue();
			    rowColor[i][1] = Integer.valueOf(rowEle.attributeValue("redValue")).intValue();
			    rowColor[i][2] = Integer.valueOf(rowEle.attributeValue("greenValue")).intValue();
			    rowColor[i][3] = Integer.valueOf(rowEle.attributeValue("blueValue")).intValue();
		    }

		    colAliasName = new String[((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getColumnCount()];
		    colSelected = new boolean[((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getColumnCount()];
            colColor = new int[((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getColumnCount()][4];
		    for (int i = 0; i < ((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getColumnCount(); i++){
			    Element rowEle = (Element) xmlElement.selectSingleNode("col"+(i+1));
			    colAliasName[i] = rowEle.attributeValue("colAlias");
			    colSelected[i] = (new Boolean(rowEle.attributeValue("colSelected"))).booleanValue();
			    colColor[i][0] = Integer.valueOf(rowEle.attributeValue("setColor")).intValue();
			    colColor[i][1] = Integer.valueOf(rowEle.attributeValue("redValue")).intValue();
			    colColor[i][2] = Integer.valueOf(rowEle.attributeValue("greenValue")).intValue();
			    colColor[i][3] = Integer.valueOf(rowEle.attributeValue("blueValue")).intValue();
		    }
	    } else {
		    rowColor = new int[1][4];             //for vector
		    Element rowEle = (Element) xmlElement.selectSingleNode("rowColor");
		    rowColor[0][0] = Integer.valueOf(rowEle.attributeValue("setColor")).intValue();
		    rowColor[0][1] = Integer.valueOf(rowEle.attributeValue("redValue")).intValue();
		    rowColor[0][2] = Integer.valueOf(rowEle.attributeValue("greenValue")).intValue();
		    rowColor[0][3] = Integer.valueOf(rowEle.attributeValue("blueValue")).intValue();

	    }
    }


    public Element toXmlElement()
    {
        Element xml = DocumentHelper.createElement(this.XML_TAG);
        xml.addAttribute("isSelected", (new Boolean(Selected)).toString());
        xml.addElement("alias").addText(aliasname);
	    if (((Parameter) data).getDataObjectForType("Matrix") != null){
		    for (int i = 0; i < ((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getRowCount(); i++){
               Element row = xml.addElement("row"+(i+1));
			   row.addAttribute("rowAlias", rowAliasName[i]);
			   row.addAttribute("rowSelected",(new Boolean(rowSelected[i])).toString());
			   row.addAttribute("setColor",(new Integer(rowColor[i][0])).toString());
     		   row.addAttribute("redValue",(new Integer(rowColor[i][1])).toString());
			   row.addAttribute("greenValue",(new Integer(rowColor[i][2])).toString());
			   row.addAttribute("blueValue",(new Integer(rowColor[i][3])).toString());
		    }

		    for (int i = 0; i < ((DomeMatrixData)((Parameter) data).getDataObjectForType("Matrix")).getColumnCount(); i++){
               Element row = xml.addElement("col"+(i+1));
			   row.addAttribute("colAlias", colAliasName[i]);
			   row.addAttribute("colSelected",(new Boolean(colSelected[i])).toString());
			   row.addAttribute("setColor",(new Integer(colColor[i][0])).toString());
     		   row.addAttribute("redValue",(new Integer(colColor[i][1])).toString());
			   row.addAttribute("greenValue",(new Integer(colColor[i][2])).toString());
			   row.addAttribute("blueValue",(new Integer(colColor[i][3])).toString());
		    }
	    } else {
		    Element row = xml.addElement("rowColor");
		    row.addAttribute("setColor",(new Integer(rowColor[0][0])).toString());
	        row.addAttribute("redValue",(new Integer(rowColor[0][1])).toString());
		    row.addAttribute("greenValue",(new Integer(rowColor[0][2])).toString());
		    row.addAttribute("blueValue",(new Integer(rowColor[0][3])).toString());
	    }

        xml.add(data.toXmlRef());
        return xml;
    }

    public String getXmlTag()
    {
        return this.XML_TAG;
    }

}

