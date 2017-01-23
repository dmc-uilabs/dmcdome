// DomeObjectSet.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.visualization;


//import org.jfree.data.AbstractDataset;
//import org.jfree.data.DefaultCategoryDataset;
//import org.jfree.data com.jrefinery.data.DefaultXYDataset;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelobject.AbstractModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DomeJavaBean;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.awt.*;

/**
 *     A collection of DomeVectors
 */
public class DomeObjectSet extends DomeJavaBean implements XMLSupport
{

	protected Vector datas;
	protected String setname;
	protected boolean useDomeName;
    protected int rowHorizontalIndex = -1;
	protected int colHorizontalIndex = -1;
    protected int[][] colCategoryColor;      //used to store columns color when it's in rows
    protected int[][] rowCategoryColor;      //used to store rows color when it's in columns
	protected int rowsOrColumns = 0; //0: no item; 1: rows; 2: columns

	protected ChartProperties jchartProperties = null;
	public static final String SELECTIONCHANGED = "Selection changed";
	public static final String SETCHANGED = "set changed";
	public static final String SERIESNAMECHANGED = "seriesname  changed";
	public static final String SETCONTENTCHANGED = "set content changed";

	public static final String XML_TAG = "DomeObjectSet";


	//todo:--add for x y label as well as the unit selection

	public DomeObjectSet(String _setname, PropertyChangeListener listener)
	{
		datas = new Vector();
		setname = _setname;
		useDomeName = true; //be default
		addPropertyChangeListener(listener);
	}

	public DomeObjectSet(DomeObjectSet set)
	{
		this.setname = set.getName();
		this.useDomeName = set.isUsingDomeName();
		this.rowsOrColumns = set.getRowsOrColumns();
		this.rowHorizontalIndex = set.getRowHorizontalIndex();
		this.colHorizontalIndex = set.getColHorizontalIndex();
		if (set.getColCategoryColor() != null) {
			if (set.getColCategoryColor().length != 0) {
				this.colCategoryColor = new int[set.getColCategoryColor().length][4];
				for (int i = 0; i < set.getColCategoryColor().length; i++) {
					for (int j = 0; j < 4; j++) {
						this.colCategoryColor[i][j] = set.getColCategoryColor()[i][j];
					}
				}
			}
		}
		if (set.getRowCategoryColor() != null) {
			if (set.getRowCategoryColor().length != 0) {
				this.rowCategoryColor = new int[set.getRowCategoryColor().length][4];
				for (int i = 0; i < set.getRowCategoryColor().length; i++) {
					for (int j = 0; j < 4; j++) {
						this.rowCategoryColor[i][j] = set.getRowCategoryColor()[i][j];
					}
				}
			}
		}

		this.jchartProperties = set.getJchartProperties(); //** should be clone
		this.datas = new Vector();
		for (int i = 0; i < set.getSize(); i++) {
			this.datas.addElement(new SetItem((SetItem) set.getData().get(i)));
		}

	}

	//create a constructor to read in xml name
	public DomeObjectSet(ModelObjectScope scope, Element xmlElement)
	{
		datas = new Vector();

		XMLUtils.makeRootElement(xmlElement);

		setname = xmlElement.attributeValue("name");
		useDomeName = Boolean.valueOf(xmlElement.attributeValue("isUsingDomeName")).booleanValue();
        rowHorizontalIndex =  Integer.valueOf(xmlElement.attributeValue("rowHorizontalAxis")).intValue();
		colHorizontalIndex =  Integer.valueOf(xmlElement.attributeValue("colHorizontalAxis")).intValue();
		rowsOrColumns = Integer.valueOf(xmlElement.attributeValue("rowsOrColumns")).intValue();
		List setitems = xmlElement.selectNodes(SetItem.XML_TAG);
		for (Iterator i = setitems.iterator(); i.hasNext();) {
			Element item = (Element) i.next();
			SetItem it = new SetItem(scope, item);
			datas.add(it);
		}

		setitems = xmlElement.selectNodes("colCategoryColor");
		if (setitems != null)
			if (setitems.size() != 0) {
				colCategoryColor = new int[setitems.size()][4];
				int j = -1;
				for (Iterator i = setitems.iterator(); i.hasNext();) {
					Element item = (Element) i.next();
					j = j + 1;
					colCategoryColor[j][0] = Integer.valueOf(item.attributeValue("setColor")).intValue();
					colCategoryColor[j][1] = Integer.valueOf(item.attributeValue("redValue")).intValue();
					colCategoryColor[j][2] = Integer.valueOf(item.attributeValue("greenValue")).intValue();
					colCategoryColor[j][3] = Integer.valueOf(item.attributeValue("blueValue")).intValue();
				}
			}

		setitems = xmlElement.selectNodes("rowCategoryColor");
		if (setitems != null)
			if (setitems.size() != 0) {
				rowCategoryColor = new int[setitems.size()][4];
				int j = -1;
				for (Iterator i = setitems.iterator(); i.hasNext();) {
					Element item = (Element) i.next();
					j = j + 1;
					rowCategoryColor[j][0] = Integer.valueOf(item.attributeValue("setColor")).intValue();
					rowCategoryColor[j][1] = Integer.valueOf(item.attributeValue("redValue")).intValue();
					rowCategoryColor[j][2] = Integer.valueOf(item.attributeValue("greenValue")).intValue();
					rowCategoryColor[j][3] = Integer.valueOf(item.attributeValue("blueValue")).intValue();
				}
			}

		Element xmlChart = (Element) xmlElement.selectSingleNode("chartProperties");
		if (xmlChart != null ) {
            jchartProperties = new ChartProperties();
            getChartPropertiesFromXml(xmlChart);
		}
	}


	/**
	 *
	 * @param d: usually all domeobject is wrapped by parameter
	 */
	public void add(DomeObject d)
	{

		// by default the aliasname is equal to the domename and isSelected is set to ture
		datas.add(new SetItem(d, d.getName(), true));
		//if(rowHorizontalIndex == -1 && getSeriesSize() > 0) setHorizontalIndex(0);  //**default set first row as horizontal axis
        //if(colCategoryColor == null && this.getColumnCount() != 0) colCategoryColor = new int[this.getColumnCount()][4];
		setRowsOrColumns();
		firePropertyChange(SETCHANGED, null, this);

	}

	public void insertElementAt(DomeObject d, int index)
	{

		datas.insertElementAt(new SetItem(d, d.getName(), true), index);
        //if(colCategoryColor == null && this.getColumnCount() != 0) colCategoryColor = new int[this.getColumnCount()][4];
		setRowsOrColumns();
		firePropertyChange(SETCHANGED, null, this);
	}

	public DomeObject get(int index)
	{
		SetItem si = (SetItem) datas.get(index);
		return si.data;
	}

    public Vector getData()
    {
        return datas;
    }

	public void setData(Vector obj)
	{
	    datas = obj;
/*		if(rowHorizontalIndex == -1 && getSeriesSize() > 0) setHorizontalIndex(0);  //**default set first row as horizontal axis
        if(colCategoryColor == null) {
             if(this.getColumnCount() != 0) colCategoryColor = new int[this.getColumnCount()][4];
        }
        else {
            if(this.getColumnCount() != colCategoryColor.length) colCategoryColor = new int[this.getColumnCount()][4];
        }
*/
		setRowsOrColumns();
		firePropertyChange(SETCONTENTCHANGED, null, this);
	}

    public DomeObject getBySeries(int index)
    {
        {   int i = -1;
            for (int j = 0; j < getSize(); j++) {
                if (((Parameter) get(j)).getDataObjectForType("Matrix") != null){
                    int count = 0;
	                if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
	                if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
	                for (int k = 0; k < count; k++) {
                        i = i +1;
                        if (i == index) return ((SetItem) datas.get(j)).data;
                    }
                } else {
                    i = i +1;
                    if (i == index) return ((SetItem) datas.get(j)).data;
                }
            }
            return null;
        }

    }

	public DomeObject getBySeries(String s)
	{
	        for (int j = 0; j < getSize(); j++) {
	            if (((Parameter) get(j)).getDataObjectForType("Matrix") != null){
	                int count = 0;
		            if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
		            if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
		            for (int k = 0; k < count; k++) {
			            if (this.rowsOrColumns ==1 && s.equals(((SetItem) datas.get(j)).rowAliasName[k])) return ((SetItem) datas.get(j)).data;
			            if (this.rowsOrColumns ==2 && s.equals(((SetItem) datas.get(j)).colAliasName[k])) return ((SetItem) datas.get(j)).data;
	                }
	            } else {
		            if (s.equals(((SetItem) datas.get(j)).aliasname)) return ((SetItem) datas.get(j)).data;
	            }
	        }
	        return null;
	}

	public void removeElementAt(int index)
	{
		datas.removeElementAt(index);
        //if(this.getColumnCount() == 0) colCategoryColor = null;
		setRowsOrColumns();
		firePropertyChange(SETCHANGED, null, this);
	}

	public void remove(DomeObject obj)
	{

		if (exist(obj)) {
			System.out.println("pre-size:" + datas.size());

			System.out.println("find in domeobjectset" + getName());
			for (int i = 0; i < datas.size(); i++) {
				if (obj.equals(get(i))) datas.removeElementAt(i);
				;
			}

			System.out.println("after-size:" + datas.size());
		}
        //if(this.getColumnCount() == 0) colCategoryColor = null;
		//firePropertyChange(SETCHANGED, null, this);
	}

	public DomeObject[] getSelectedSetItems()       //no use
	{
		int size = datas.size();


		ArrayList l = new ArrayList();
		for (int i = 0; i < size; i++) {
			SetItem si = (SetItem) datas.get(i);
			if (si.Selected) l.add(si.data);
		}

		return (DomeObject[]) (l.toArray(new DomeObject[]{}));
	}

	public ArrayList getSelectedSeries()
	{
    		ArrayList l = new ArrayList();
            int i = -1;
            for (int j = 0; j < getSize(); j++) {
	            if (((Parameter) get(j)).getDataObjectForType("Matrix") != null){
		            int count = 0;
			        if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
			        if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
                    for (int k = 0; k < count; k++) {
                        i = i +1;
                        if (this.rowsOrColumns ==1 && isSelected(i)) l.add((DomeVectorData)((DomeMatrixData)((Parameter) get(j)).getDataObjectForType("Matrix")).getRow(k));
	                    if (this.rowsOrColumns ==2 && isSelected(i)) l.add((DomeVectorData)((DomeMatrixData)((Parameter) get(j)).getDataObjectForType("Matrix")).getCol(k));
                    }
                } else {
                    i = i +1;
                    if (isSelected(i)) l.add((DomeVectorData) ((Parameter) get(j)).getDataObjectForType("Vector"));
                }
            }
		return l;
	}

	public int getIndexinSelectedSeriesforHorizontalAxis()
	{

		    int index = -1;
            int i = -1;
            for (int j = 0; j < getSize(); j++) {
                if (((Parameter) get(j)).getDataObjectForType("Matrix") != null){
	                int count = 0;
		            if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
		            if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
                    for (int k = 0; k < count; k++) {
                        i = i +1;
                        if (isSelected(i)) index = index +1;
	                    if (this.rowsOrColumns ==1 && i == this.rowHorizontalIndex ) return index;
	                    if (this.rowsOrColumns ==2 && i == this.colHorizontalIndex ) return index;
                    }
                } else {
                    i = i +1;
                    if (isSelected(i)) index = index +1;
	                if (this.rowsOrColumns ==1 && i == this.rowHorizontalIndex ) return index;
	                if (this.rowsOrColumns ==2 && i == this.colHorizontalIndex ) return index;
                }
            }
		return -1;
	}

	public DomeVectorData getDomeVectorDataBySeries(int index)
	{     //** don't select horizontal axis!
            int i = -1;
            for (int j = 0; j < getSize(); j++) {
                if (((Parameter) get(j)).getDataObjectForType("Matrix") != null){
	                int count = 0;
		            if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
		            if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
                    for (int k = 0; k < count; k++) {
                        i = i +1;
                        if (this.rowsOrColumns ==1 && i == index) return (DomeVectorData)((DomeMatrixData)((Parameter) get(j)).getDataObjectForType("Matrix")).getRow(k);
	                    if (this.rowsOrColumns ==2 && i == index) return (DomeVectorData)((DomeMatrixData)((Parameter) get(j)).getDataObjectForType("Matrix")).getCol(k);
                    }
                } else {
                    i = i +1;
                    if (i == index) return ((DomeVectorData) ((Parameter) get(j)).getDataObjectForType("Vector"));
                }
            }

		return null;
	}

	public DomeObject[] getAllSetItems()
	{
		int size = datas.size();

		DomeObject[] l = new DomeObject[size];
		for (int i = 0; i < size; i++)
			l[i] = get(i);

		return l;
	}

	public int getSize()
	{
		return datas.size();
	}

    public int getHorizontalIndex()
    {
        if(this.rowsOrColumns == 1) return this.getRowHorizontalIndex();
	    if(this.rowsOrColumns == 2) return this.getColHorizontalIndex();

	    return -1;
    }

    public int getSeriesSize()
    {   int totalSize = datas.size();
        for (int i = 0; i < getSize(); i++) {
            if (((Parameter) get(i)).getDataObjectForType("Matrix") != null){
	            int count = 0;
		        if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(i)).getDataObjectForType("Matrix")).getRowCount();
		        if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(i)).getDataObjectForType("Matrix")).getColumnCount();
                if (count == 0)
                   totalSize = totalSize - 1;
	            else totalSize  = totalSize + count -1;
            }
        }
        return totalSize;
    }

	public String getName()
	{
		return setname;

	}

	public boolean exist(DomeObject o)
	{
		for (int i = 0; i < datas.size(); i++)
			if (o.equals(get(i))) return true;
		return false;

	}

	public void setName(String newName)
	{
		setname = newName;
		firePropertyChange(SETCHANGED, null, this);
	}

    public void setHorizontalIndex(int index)
    {
        if(this.rowsOrColumns ==1) setRowHorizontalIndex(index);
	    else if(this.rowsOrColumns ==2) setColHorizontalIndex(index);
        else {
		    this.rowHorizontalIndex = -1;
		    this.colHorizontalIndex = -1;
	    }
	    setSeriesSelected(index,true);
    }

	public int getRowHorizontalIndex()
	{
		return rowHorizontalIndex;
	}

	public void setRowHorizontalIndex(int rowHorizontalIndex)
	{
		this.rowHorizontalIndex = rowHorizontalIndex;
	}

	public int getColHorizontalIndex()
	{
		return colHorizontalIndex;
	}

	public void setColHorizontalIndex(int colHorizontalIndex)
	{
		this.colHorizontalIndex = colHorizontalIndex;
	}

	/**
	 *
	 * @param index
	 * @param isSelected
	 */
	public void setSelected(int index, boolean isSelected)
	{
		SetItem si = (SetItem) datas.get(index);
		si.Selected = isSelected;
		firePropertyChange(SELECTIONCHANGED, null, this);
	}

    public void setSeriesSelected(int index, boolean isSelected)
    {
        int i = -1;
        for (int j = 0; j < getSize(); j++) {
            if (((Parameter) get(j)).getDataObjectForType("Matrix") != null) {
	            int count = 0;
		        if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
		        if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
                for (int k = 0; k < count; k++) {
                    i = i + 1;
                    if (i == index) {
                        if(this.rowsOrColumns ==1) ((SetItem) datas.get(j)).rowSelected[k] = isSelected;
	                    if(this.rowsOrColumns ==2) ((SetItem) datas.get(j)).colSelected[k] = isSelected;
                        firePropertyChange(SELECTIONCHANGED, null, this);
                          return;
                      }
                }
            } else {
                i = i + 1;
                if (i == index) {
                    ((SetItem) datas.get(j)).Selected = isSelected;
                firePropertyChange(SELECTIONCHANGED, null, this);
                return;                }

            }
        }
    }

	/**
	 *
	 * @param index
	 * @return isSelected
	 */
	public boolean isSelected(int index)
    {
        int i = -1;
        for (int j = 0; j < getSize(); j++) {
            if (((Parameter) get(j)).getDataObjectForType("Matrix") != null) {
	            int count = 0;
		        if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
		        if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
                for (int k = 0; k < count; k++) {
                    i = i + 1;
                    if (this.rowsOrColumns ==1 && i == index) return ((SetItem) datas.get(j)).rowSelected[k];
	                if (this.rowsOrColumns ==2 && i == index) return ((SetItem) datas.get(j)).colSelected[k];
                }
            } else {
                i = i + 1;
                if (i == index) return ((SetItem) datas.get(j)).Selected;
            }
        }
        return false;

    }

	public boolean isUsingDomeName()
	{
		return useDomeName;
	}

	public void setUsingDomeName(boolean use)
	{
		useDomeName = use;
		firePropertyChange(SERIESNAMECHANGED, null, this);
	}

	public void moveToTop(int index)
	{
		if (index == 0) {
			//already at top
			return;
		}
		if (index < 0) {
			System.err.println("DomeObjectSet:moveToTop(): invalid index");

		}
		SetItem si = (SetItem) datas.get(index);
		datas.removeElementAt(index);


		//insert the one into the one place higher

		datas.insertElementAt(si, 0);

		firePropertyChange(SETCHANGED, null, this);
	}


	public void moveUp(int index)
	{
		SetItem si = (SetItem) datas.get(index);
		datas.removeElementAt(index);


		//insert the one into the one place higher
		if (index > 0) {
			datas.insertElementAt(si, index - 1);

		}
		firePropertyChange(SETCHANGED, null, this);
	}

	public void moveDown(int index)
	{
		SetItem si = (SetItem) datas.get(index);
		datas.removeElementAt(index);

		if (index < getSize()) {
			datas.insertElementAt(si, index + 1);

		}
		firePropertyChange(SETCHANGED, null, this);
	}

	/**
	 *
	 * @param index
	 *  @return : alias name as series name
	 */
	public String getAlias(int index)
	{
		SetItem si = (SetItem) datas.get(index);
		return si.aliasname;
	}

    public String getSeriesName(int index)
        {   int i = -1;
            for (int j = 0; j < getSize(); j++) {
                if (((Parameter) get(j)).getDataObjectForType("Matrix") != null){
	                int count = 0;
		            if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
		            if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
                    for (int k = 0; k < count; k++) {
                        i = i +1;
                        if (i == index && k == 0) return ((SetItem) datas.get(j)).data.getName();
                        if (i == index && k != 0) return "";
                    }
                } else {
                    i = i +1;
                    if (i == index) return ((SetItem) datas.get(j)).data.getName();
                }
            }
            return "";
        }

    public String getSeriesAlias(int index)
        {   int i = -1;
            for (int j = 0; j < getSize(); j++) {
                if (((Parameter) get(j)).getDataObjectForType("Matrix") != null){
	                int count = 0;
		            if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
		            if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
                    for (int k = 0; k < count; k++) {
                        i = i +1;
                        if (this.rowsOrColumns ==1 && i == index) return ((SetItem) datas.get(j)).rowAliasName[k];
	                    if (this.rowsOrColumns ==2 && i == index) return ((SetItem) datas.get(j)).colAliasName[k];
                    }
                } else {
                    i = i +1;
                    if (i == index) return ((SetItem) datas.get(j)).aliasname;
                }
            }
            return "";
        }

	public String getAlias(DomeObject obj)
	{
		for (int i = 0; i < getSize(); i++) {
			if (obj.equals(get(i))) {
				SetItem si = (SetItem) datas.get(i);
				return si.aliasname;
			}
		}

		return "";
	}

	public void setAlias(String s, int index)
	{
		SetItem si = (SetItem) datas.get(index);
		si.aliasname = s;
		firePropertyChange(SERIESNAMECHANGED, null, this);
	}

    public void setSeriesAlias(String s, int index)
    {
        int i = -1;
        for (int j = 0; j < getSize(); j++) {
            if (((Parameter) get(j)).getDataObjectForType("Matrix") != null) {
	            int count = 0;
		        if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
		        if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
                for (int k = 0; k < count; k++) {
                    i = i + 1;
                    if (this.rowsOrColumns ==1 && i == index) {
                        ((SetItem) datas.get(j)).rowAliasName[k] = s;
                        firePropertyChange(SERIESNAMECHANGED, null, this);
                        return;
                     }
	                if (this.rowsOrColumns ==2 && i == index) {
	                    ((SetItem) datas.get(j)).colAliasName[k] = s;
	                    firePropertyChange(SERIESNAMECHANGED, null, this);
	                    return;
	                 }
               }
            } else {
                i = i + 1;
                if (i == index) {
                    ((SetItem) datas.get(j)).aliasname = s;
                    firePropertyChange(SERIESNAMECHANGED, null, this);
                    return;
                }
            }
        }
    }

	public void setAlias(String s, DomeObject obj)
	{
		for (int i = 0; i < getSize(); i++) {
			if (obj.equals(get(i))) {
				SetItem si = (SetItem) datas.get(i);
				si.aliasname = s;
			}
		}

		firePropertyChange(SERIESNAMECHANGED, null, this);
	}


	public String toString()
	{
		return setname;
	}


//-------------the following are for loading data into chart

	/**
	 *  Check if this domeobjectset is okay for ported into Chart
	 * @return boolean isValid
	 */

/*	public AbstractDataset portIntoChartDataset(String option)
	{
		DomeObject[] alldata = getSelectedSetItems();

		if (option.equals(Visualization.XYCHART)) {
			if (alldata.length == 0) {
				Error("empty data");
				return new EmptyXYDataset();
			} else if (alldata.length == 1) {
				Error("only one item in data");
				return new EmptyXYDataset();
			}

			//DefaultXYDataset takes in String[] seriesname, and Object[serie][itemx][itemy]
			//the first item in domeobjectset will be X axis
			// <P>
			// The dimensions of the data array are [series][item][x=0, y=1]. The x-values should be Number
			// or Date objects, the y-values should be Number objects.  Any other types are interpreted as
			// zero. The data will be sorted so that the x-values are ascending.
			DomeObject xaxis = alldata[0];
			ArrayList yaxis = new ArrayList();
			ArrayList seriesNameArrayList = new ArrayList();

			for (int i = 0; i < alldata.length; i++) {
				if (!alldata[i].equals(xaxis)) {
					yaxis.add(alldata[i]);
					if (isUsingDomeName())
						seriesNameArrayList.add(alldata[i].getName());
					else
						seriesNameArrayList.add(getAlias(alldata[i]));
				}
			}

			DomeVectorData d_x = getDomeVectorData(xaxis);
			if (d_x == null) {
				Error("null item in data");
				return new EmptyXYDataset();
			}

			Object[][][] items = new Object[yaxis.size()][d_x.getSize()][2];

			for (int i = 0; i < yaxis.size(); i++) {
				DomeVectorData d_y = getDomeVectorData((DomeObject) yaxis.get(i));
				if (d_y == null) {
					Error("null item in data");
					return new EmptyXYDataset();
				}
				if (d_y.getSize() != d_x.getSize()) {
					Error("x-axis item doesn't match y-axis item!");
					return new EmptyXYDataset();
				}
				for (int j = 0; j < d_x.getSize(); j++) {
					items[i][j][0] = d_x.getItem(j);
					items[i][j][1] = d_y.getItem(j);
				}
			}

			return new DefaultXYDataset(seriesNameArrayList, items);
		} else if (option.equals(Visualization.PIECHART)) {
			//disable piechart for now

		} else if (option.equals(Visualization.CATEGORYCHART)) {
			//for now, it will display "vertical bar chart"

			if (alldata.length == 0) {
				Error("empty data");
				return new EmptyCategoryDataset();
			}

			//the way to make vector into category dataset is to make each vector a category
			// Category names are generated automatically ("Category 1", "Category 2", etc).
			// public DefaultCategoryDataset(String[] seriesNames, Number[][] data) {
			// data[series][category]

			DomeVectorData firstone = getDomeVectorData(alldata[0]);

			//ArrayList seriesNameArrayList = new ArrayList();


			Number[][] items = new Number[firstone.getSize()][alldata.length];

			for (int i = 0; i < alldata.length; i++) {
				//if (isUsingDomeName())
				//	seriesNameArrayList.add(alldata[i].getName());
				//else
				//	seriesNameArrayList.add(getAlias(alldata[i]));

				for (int j = 0; j < firstone.getSize(); j++) {
					items[j][i] = getDomeVectorData(alldata[i]).getItem(j);
				}

			}

			//return new DefaultCategoryDataset((String[])seriesNameArrayList.toArray(new String[]{}),items);
			return new DefaultCategoryDataset(items);
		}



		//other case
		return null;
	}
*/

	public DataObject getDataObject(DomeObject obj)
	{
		if (((Parameter) obj).getDataObjectForType("Matrix") != null) return (DomeMatrix) ((Parameter) obj).getDataObjectForType("Matrix");
		if (((Parameter) obj).getDataObjectForType("Vector") != null) return (DomeVectorData) ((Parameter) obj).getDataObjectForType("Vector");
        return null;
	}

	/**
	 *  Inner class to hold information for each set item
	 */


	public String getXmlTag()
	{
		return XML_TAG;
	}

	public Element toXmlElement()
	{
		Element xml = DocumentHelper.createElement(XML_TAG);
		xml.addAttribute("name", setname);
		xml.addAttribute("isUsingDomeName", (new Boolean(useDomeName)).toString());
		xml.addAttribute("rowHorizontalAxis", (new Integer(rowHorizontalIndex)).toString());
		xml.addAttribute("colHorizontalAxis", (new Integer(colHorizontalIndex)).toString());
		xml.addAttribute("rowsOrColumns", (new Integer(rowsOrColumns)).toString());
		if(colCategoryColor != null) {
			  for(int i = 0; i < colCategoryColor.length; i ++) {
				  Element series = xml.addElement("colCategoryColor");
				  series.addAttribute("setColor",(new Integer(colCategoryColor[i][0])).toString());
				  series.addAttribute("redValue",(new Integer(colCategoryColor[i][1])).toString());
				  series.addAttribute("greenValue",(new Integer(colCategoryColor[i][2])).toString());
				  series.addAttribute("blueValue",(new Integer(colCategoryColor[i][3])).toString());
			  }
		}
		if(rowCategoryColor != null) {
			  for(int i = 0; i < rowCategoryColor.length; i ++) {
				  Element series = xml.addElement("rowCategoryColor");
				  series.addAttribute("setColor",(new Integer(rowCategoryColor[i][0])).toString());
				  series.addAttribute("redValue",(new Integer(rowCategoryColor[i][1])).toString());
				  series.addAttribute("greenValue",(new Integer(rowCategoryColor[i][2])).toString());
				  series.addAttribute("blueValue",(new Integer(rowCategoryColor[i][3])).toString());
			  }
		}

		XMLUtils.addCollection(xml, "", datas);
		chartPropertiesToXmlElement(xml);
		return xml;
	}

	public void chartPropertiesToXmlElement(Element xml)
	{
		if(jchartProperties == null) return;
		Element xmlChart = xml.addElement("chartProperties");

		// save chartArea
		Element xmlChartArea = xmlChart.addElement("chartArea");
		xmlChartArea.addAttribute("antiAliased", (new Boolean(jchartProperties.isAntiAliased())).toString());
        xmlChartArea.addAttribute("plotTitle", jchartProperties.getPlotTitle());
		xmlChartArea.addAttribute("useDomeSetName", (new Boolean(jchartProperties.isUseDomeSetName())).toString());
        xmlChartArea.addAttribute("chartBackgroundColorRed", (new Integer(jchartProperties.getChartBackgroundColor()[0])).toString());
		xmlChartArea.addAttribute("chartBackgroundColorGreen", (new Integer(jchartProperties.getChartBackgroundColor()[1])).toString());
		xmlChartArea.addAttribute("chartBackgroundColorBlue", (new Integer(jchartProperties.getChartBackgroundColor()[2])).toString());
		xmlChartArea.addAttribute("showBorder", (new Boolean(jchartProperties.isShowBorder())).toString());
		xmlChartArea.addAttribute("borderStroke", (new Float(jchartProperties.getBorderStroke())).toString());
		xmlChartArea.addAttribute("borderColorRed", (new Integer(jchartProperties.getBorderColor()[0])).toString());
		xmlChartArea.addAttribute("borderColorGreen", (new Integer(jchartProperties.getBorderColor()[1])).toString());
		xmlChartArea.addAttribute("borderColorBlue", (new Integer(jchartProperties.getBorderColor()[2])).toString());

		// save legend
		Element xmlLegend = xmlChart.addElement("legend");
		xmlLegend.addAttribute("showLegend", (new Boolean(jchartProperties.isShowLegend())).toString());
		xmlLegend.addAttribute("legendOutlineStroke", (new Float(jchartProperties.getLegendOutlineStroke())).toString());
		xmlLegend.addAttribute("legendOutlineColorRed", (new Integer(jchartProperties.getLegendOutlineColor()[0])).toString());
		xmlLegend.addAttribute("legendOutlineColorGreen", (new Integer(jchartProperties.getLegendOutlineColor()[1])).toString());
		xmlLegend.addAttribute("legendOutlineColorBlue", (new Integer(jchartProperties.getLegendOutlineColor()[2])).toString());
		xmlLegend.addAttribute("legendBackgroundColorRed", (new Integer(jchartProperties.getLegendBackgroundColor()[0])).toString());
		xmlLegend.addAttribute("legendBackgroundColorGreen", (new Integer(jchartProperties.getLegendBackgroundColor()[1])).toString());
		xmlLegend.addAttribute("legendBackgroundColorBlue", (new Integer(jchartProperties.getLegendBackgroundColor()[2])).toString());
        xmlLegend.addAttribute("legendFontName", jchartProperties.getLegendFontName());
        xmlLegend.addAttribute("legendFontStyle",(new Integer(jchartProperties.getLegendFontStyle())).toString());
		xmlLegend.addAttribute("legendFontSize",(new Integer(jchartProperties.getLegendFontSize())).toString());
		xmlLegend.addAttribute("legendLabelColorRed", (new Integer(jchartProperties.getLegendLabelColor()[0])).toString());
		xmlLegend.addAttribute("legendLabelColorGreen", (new Integer(jchartProperties.getLegendLabelColor()[1])).toString());
		xmlLegend.addAttribute("legendLabelColorBlue", (new Integer(jchartProperties.getLegendLabelColor()[2])).toString());

		Element xmlPlot = xmlChart.addElement("plot");
		// save plot-vertical
		Element xmlVerticalAxis = xmlPlot.addElement("verticalAxis");
		if(jchartProperties.getvXYLabel() != null) xmlVerticalAxis.addAttribute("vXYLabel", jchartProperties.getvXYLabel());
		if(jchartProperties.getvBarLabel() != null) xmlVerticalAxis.addAttribute("vBarLabel", jchartProperties.getvBarLabel());
		xmlVerticalAxis.addAttribute("vUnit", jchartProperties.getvUnit());
		xmlVerticalAxis.addAttribute("vFontName", jchartProperties.getvFontName());
		xmlVerticalAxis.addAttribute("vFontStyle",(new Integer(jchartProperties.getvFontStyle())).toString());
		xmlVerticalAxis.addAttribute("vFontSize",(new Integer(jchartProperties.getvFontSize())).toString());
		xmlVerticalAxis.addAttribute("vColorRed", (new Integer(jchartProperties.getvColor()[0])).toString());
		xmlVerticalAxis.addAttribute("vColorGreen", (new Integer(jchartProperties.getvColor()[1])).toString());
		xmlVerticalAxis.addAttribute("vColorBlue", (new Integer(jchartProperties.getvColor()[2])).toString());
		xmlVerticalAxis.addAttribute("vLabelInsetsTop", (new Integer(jchartProperties.getvLabelInsets()[0])).toString());
		xmlVerticalAxis.addAttribute("vLabelInsetsLeft", (new Integer(jchartProperties.getvLabelInsets()[1])).toString());
		xmlVerticalAxis.addAttribute("vLabelInsetsBottom", (new Integer(jchartProperties.getvLabelInsets()[2])).toString());
		xmlVerticalAxis.addAttribute("vLabelInsetsRight", (new Integer(jchartProperties.getvLabelInsets()[3])).toString());
		xmlVerticalAxis.addAttribute("vTickLabelInsetsTop", (new Integer(jchartProperties.getvTickLabelInsets()[0])).toString());
		xmlVerticalAxis.addAttribute("vTickLabelInsetsLeft", (new Integer(jchartProperties.getvTickLabelInsets()[1])).toString());
		xmlVerticalAxis.addAttribute("vTickLabelInsetsBottom", (new Integer(jchartProperties.getvTickLabelInsets()[2])).toString());
		xmlVerticalAxis.addAttribute("vTickLabelInsetsRight", (new Integer(jchartProperties.getvTickLabelInsets()[3])).toString());

		Element xmlTicks = xmlVerticalAxis.addElement("ticks");
        xmlTicks.addAttribute("vShowTickLabel",  (new Boolean(jchartProperties.isvShowTickLabel())).toString());
		xmlTicks.addAttribute("vShowTickMarks",  (new Boolean(jchartProperties.isvShowTickMarks())).toString());
		xmlTicks.addAttribute("vTickLabelFontName", jchartProperties.getvTickLabelFontName());
		xmlTicks.addAttribute("vTickLabelFontStyle",(new Integer(jchartProperties.getvTickLabelFontStyle())).toString());
		xmlTicks.addAttribute("vTickLabelFontSize",(new Integer(jchartProperties.getvTickLabelFontSize())).toString());
		Element xmlRange = xmlVerticalAxis.addElement("range");
        xmlRange.addAttribute("vAutoRange", (new Boolean(jchartProperties.isvAutoRange())).toString());
        xmlRange.addAttribute("vMinimumRange", (new Double(jchartProperties.getvMinimumRange())).toString());
		xmlRange.addAttribute("vMaximumRange", (new Double(jchartProperties.getvMaximumRange())).toString());
		Element xmlGrid = xmlVerticalAxis.addElement("grid");
		xmlGrid.addAttribute("vShowGrid", (new Boolean(jchartProperties.isvShowGrid())).toString());
        xmlGrid.addAttribute("vGridStroke", (new Float(jchartProperties.getvGridStroke())).toString());
		xmlGrid.addAttribute("vGridColorRed", (new Integer(jchartProperties.getvGridColor()[0])).toString());
		xmlGrid.addAttribute("vGridColorGreen", (new Integer(jchartProperties.getvGridColor()[1])).toString());
		xmlGrid.addAttribute("vGridColorBlue", (new Integer(jchartProperties.getvGridColor()[2])).toString());

		// save plot-horizontal
		Element xmlHorizontalAxis = xmlPlot.addElement("horizontalAxis");
		if(jchartProperties.gethXYLabel() != null) xmlHorizontalAxis.addAttribute("hXYLabel", jchartProperties.gethXYLabel());
		if(jchartProperties.gethBarLabel() != null) xmlHorizontalAxis.addAttribute("hBarLabel", jchartProperties.gethBarLabel());
		xmlHorizontalAxis.addAttribute("hUnit", jchartProperties.gethUnit());
		xmlHorizontalAxis.addAttribute("hFontName", jchartProperties.gethFontName());
		xmlHorizontalAxis.addAttribute("hFontStyle",(new Integer(jchartProperties.gethFontStyle())).toString());
		xmlHorizontalAxis.addAttribute("hFontSize",(new Integer(jchartProperties.gethFontSize())).toString());
		xmlHorizontalAxis.addAttribute("hColorRed", (new Integer(jchartProperties.gethColor()[0])).toString());
		xmlHorizontalAxis.addAttribute("hColorGreen", (new Integer(jchartProperties.gethColor()[1])).toString());
		xmlHorizontalAxis.addAttribute("hColorBlue", (new Integer(jchartProperties.gethColor()[2])).toString());
		xmlHorizontalAxis.addAttribute("hLabelInsetsTop", (new Integer(jchartProperties.gethLabelInsets()[0])).toString());
		xmlHorizontalAxis.addAttribute("hLabelInsetsLeft", (new Integer(jchartProperties.gethLabelInsets()[1])).toString());
		xmlHorizontalAxis.addAttribute("hLabelInsetsBottom", (new Integer(jchartProperties.gethLabelInsets()[2])).toString());
		xmlHorizontalAxis.addAttribute("hLabelInsetsRight", (new Integer(jchartProperties.gethLabelInsets()[3])).toString());
		xmlHorizontalAxis.addAttribute("hTickLabelInsetsTop", (new Integer(jchartProperties.gethTickLabelInsets()[0])).toString());
		xmlHorizontalAxis.addAttribute("hTickLabelInsetsLeft", (new Integer(jchartProperties.gethTickLabelInsets()[1])).toString());
		xmlHorizontalAxis.addAttribute("hTickLabelInsetsBottom", (new Integer(jchartProperties.gethTickLabelInsets()[2])).toString());
		xmlHorizontalAxis.addAttribute("hTickLabelInsetsRight", (new Integer(jchartProperties.gethTickLabelInsets()[3])).toString());

		xmlTicks = xmlHorizontalAxis.addElement("ticks");
        xmlTicks.addAttribute("hShowTickLabel",  (new Boolean(jchartProperties.ishShowTickLabel())).toString());
		xmlTicks.addAttribute("hShowTickMarks",  (new Boolean(jchartProperties.ishShowTickMarks())).toString());
		xmlTicks.addAttribute("hTickLabelFontName", jchartProperties.gethTickLabelFontName());
		xmlTicks.addAttribute("hTickLabelFontStyle",(new Integer(jchartProperties.gethTickLabelFontStyle())).toString());
		xmlTicks.addAttribute("hTickLabelFontSize",(new Integer(jchartProperties.gethTickLabelFontSize())).toString());
		if (jchartProperties.gethXYLabel() != null) {
			xmlRange = xmlHorizontalAxis.addElement("range");
	        xmlRange.addAttribute("hAutoRange", (new Boolean(jchartProperties.ishAutoRange())).toString());
	        xmlRange.addAttribute("hMinimumRange", (new Double(jchartProperties.gethMinimumRange())).toString());
			xmlRange.addAttribute("hMaximumRange", (new Double(jchartProperties.gethMaximumRange())).toString());
			xmlGrid = xmlHorizontalAxis.addElement("grid");
			xmlGrid.addAttribute("hShowGrid", (new Boolean(jchartProperties.ishShowGrid())).toString());
			xmlGrid.addAttribute("hGridStroke", (new Float(jchartProperties.gethGridStroke())).toString());
			xmlGrid.addAttribute("hGridColorRed", (new Integer(jchartProperties.gethGridColor()[0])).toString());
			xmlGrid.addAttribute("hGridColorGreen", (new Integer(jchartProperties.gethGridColor()[1])).toString());
			xmlGrid.addAttribute("hGridColorBlue", (new Integer(jchartProperties.gethGridColor()[2])).toString());
		}

		// save plot-appearance
		Element xmlAppearance = xmlPlot.addElement("appearance");
		xmlAppearance.addAttribute("appImage", jchartProperties.getAppImage());
		xmlAppearance.addAttribute("appInsetsTop", (new Integer(jchartProperties.getAppInsets()[0])).toString());
		xmlAppearance.addAttribute("appInsetsLeft", (new Integer(jchartProperties.getAppInsets()[1])).toString());
		xmlAppearance.addAttribute("appInsetsBottom", (new Integer(jchartProperties.getAppInsets()[2])).toString());
		xmlAppearance.addAttribute("appInsetsRight", (new Integer(jchartProperties.getAppInsets()[3])).toString());
        xmlAppearance.addAttribute("appOutlineStroke",(new Float(jchartProperties.getAppOutlineStroke())).toString());
		xmlAppearance.addAttribute("appOutlineColorRed", (new Integer(jchartProperties.getAppOutlineColor()[0])).toString());
		xmlAppearance.addAttribute("appOutlineColorGreen", (new Integer(jchartProperties.getAppOutlineColor()[1])).toString());
		xmlAppearance.addAttribute("appOutlineColorBlue", (new Integer(jchartProperties.getAppOutlineColor()[2])).toString());

		xmlAppearance.addAttribute("appBackgroundColorRed", (new Integer(jchartProperties.getAppBackgroundColor()[0])).toString());
		xmlAppearance.addAttribute("appBackgroundColorGreen", (new Integer(jchartProperties.getAppBackgroundColor()[1])).toString());
		xmlAppearance.addAttribute("appBackgroundColorBlue", (new Integer(jchartProperties.getAppBackgroundColor()[2])).toString());

		xmlAppearance.addAttribute("appSeriesStroke",(new Float(jchartProperties.getAppSeriesStroke())).toString());

	}

    public void getChartPropertiesFromXml(Element xmlChart)
    {
	    int[] newInsets = new int[4];
	    int[] newColor = new int[3];

	    // for chartArea
	    Element xmlChartArea = (Element) xmlChart.selectSingleNode("chartArea");
	    if(xmlChartArea == null ) { jchartProperties = null; return;}
        jchartProperties.setAntiAliased(Boolean.valueOf(xmlChartArea.attributeValue("antiAliased")).booleanValue());
	    jchartProperties.setPlotTitle(xmlChartArea.attributeValue("plotTitle"));
        jchartProperties.setUseDomeSetName(Boolean.valueOf(xmlChartArea.attributeValue("useDomeSetName")).booleanValue());
        newColor[0] = Integer.valueOf(xmlChartArea.attributeValue("chartBackgroundColorRed")).intValue();
	    newColor[1] = Integer.valueOf(xmlChartArea.attributeValue("chartBackgroundColorGreen")).intValue();
	    newColor[2] = Integer.valueOf(xmlChartArea.attributeValue("chartBackgroundColorBlue")).intValue();
	    jchartProperties.setChartBackgroundColor(newColor);
        jchartProperties.setShowBorder(Boolean.valueOf(xmlChartArea.attributeValue("showBorder")).booleanValue());
        jchartProperties.setBorderStroke(Float.valueOf(xmlChartArea.attributeValue("borderStroke")).floatValue());
	    newColor[0] = Integer.valueOf(xmlChartArea.attributeValue("borderColorRed")).intValue();
		newColor[1] = Integer.valueOf(xmlChartArea.attributeValue("borderColorGreen")).intValue();
		newColor[2] = Integer.valueOf(xmlChartArea.attributeValue("borderColorBlue")).intValue();
	    jchartProperties.setBorderColor(newColor);

	    //for legend
	    Element xmlLegend = (Element) xmlChart.selectSingleNode("legend");
	    if(xmlLegend == null ) { jchartProperties = null; return;}
        jchartProperties.setShowLegend(Boolean.valueOf(xmlLegend.attributeValue("showLegend")).booleanValue());
        jchartProperties.setLegendOutlineStroke(Float.valueOf(xmlLegend.attributeValue("legendOutlineStroke")).floatValue());
	    newColor[0] = Integer.valueOf(xmlLegend.attributeValue("legendOutlineColorRed")).intValue();
		newColor[1] = Integer.valueOf(xmlLegend.attributeValue("legendOutlineColorGreen")).intValue();
		newColor[2] = Integer.valueOf(xmlLegend.attributeValue("legendOutlineColorBlue")).intValue();
	    jchartProperties.setLegendOutlineColor(newColor);
	    newColor[0] = Integer.valueOf(xmlLegend.attributeValue("legendBackgroundColorRed")).intValue();
		newColor[1] = Integer.valueOf(xmlLegend.attributeValue("legendBackgroundColorGreen")).intValue();
		newColor[2] = Integer.valueOf(xmlLegend.attributeValue("legendBackgroundColorBlue")).intValue();
	    jchartProperties.setLegendBackgroundColor(newColor);
        jchartProperties.setLegendFontName(xmlLegend.attributeValue("legendFontName"));
	    jchartProperties.setLegendFontStyle(Integer.valueOf(xmlLegend.attributeValue("legendFontStyle")).intValue());
	    jchartProperties.setLegendFontSize(Integer.valueOf(xmlLegend.attributeValue("legendFontSize")).intValue());
	    newColor[0] = Integer.valueOf(xmlLegend.attributeValue("legendLabelColorRed")).intValue();
		newColor[1] = Integer.valueOf(xmlLegend.attributeValue("legendLabelColorGreen")).intValue();
		newColor[2] = Integer.valueOf(xmlLegend.attributeValue("legendLabelColorBlue")).intValue();
	    jchartProperties.setLegendLabelColor(newColor);

	    Element xmlPlot = (Element) xmlChart.selectSingleNode("plot");
	    if(xmlPlot == null ) { jchartProperties = null; return;}
	    //for plot-vertical
	    Element xmlVerticalAxis = (Element) xmlPlot.selectSingleNode("verticalAxis");
	    if(xmlVerticalAxis == null ) { jchartProperties = null; return;}
        if(xmlVerticalAxis.attribute("vXYLabel") != null ) jchartProperties.setvXYLabel(xmlVerticalAxis.attributeValue("vXYLabel"));
	    if(xmlVerticalAxis.attribute("vBarLabel") != null ) jchartProperties.setvBarLabel(xmlVerticalAxis.attributeValue("vBarLabel"));
        jchartProperties.setvUnit(xmlVerticalAxis.attributeValue("vUnit"));
	    jchartProperties.setvFontName(xmlVerticalAxis.attributeValue("vFontName"));
		jchartProperties.setvFontStyle(Integer.valueOf(xmlVerticalAxis.attributeValue("vFontStyle")).intValue());
		jchartProperties.setvFontSize(Integer.valueOf(xmlVerticalAxis.attributeValue("vFontSize")).intValue());
	    newInsets[0] = Integer.valueOf(xmlVerticalAxis.attributeValue("vLabelInsetsTop")).intValue();
		newInsets[1] = Integer.valueOf(xmlVerticalAxis.attributeValue("vLabelInsetsLeft")).intValue();
		newInsets[2] = Integer.valueOf(xmlVerticalAxis.attributeValue("vLabelInsetsBottom")).intValue();
	    newInsets[3] = Integer.valueOf(xmlVerticalAxis.attributeValue("vLabelInsetsRight")).intValue();
        jchartProperties.setvLabelInsets(newInsets);
	    newInsets[0] = Integer.valueOf(xmlVerticalAxis.attributeValue("vTickLabelInsetsTop")).intValue();
		newInsets[1] = Integer.valueOf(xmlVerticalAxis.attributeValue("vTickLabelInsetsLeft")).intValue();
		newInsets[2] = Integer.valueOf(xmlVerticalAxis.attributeValue("vTickLabelInsetsBottom")).intValue();
	    newInsets[3] = Integer.valueOf(xmlVerticalAxis.attributeValue("vTickLabelInsetsRight")).intValue();
        jchartProperties.setvTickLabelInsets(newInsets);

	    Element xmlTicks = (Element) xmlVerticalAxis.selectSingleNode("ticks");
	    jchartProperties.setvShowTickLabel(Boolean.valueOf(xmlTicks.attributeValue("vShowTickLabel")).booleanValue());
	    jchartProperties.setvShowTickMarks(Boolean.valueOf(xmlTicks.attributeValue("vShowTickMarks")).booleanValue());
        jchartProperties.setvTickLabelFontName(xmlTicks.attributeValue("vTickLabelFontName"));
	    jchartProperties.setvTickLabelFontStyle(Integer.valueOf(xmlTicks.attributeValue("vTickLabelFontStyle")).intValue());
	    jchartProperties.setvTickLabelFontSize(Integer.valueOf(xmlTicks.attributeValue("vTickLabelFontSize")).intValue());
	    Element xmlRange = (Element) xmlVerticalAxis.selectSingleNode("range");
	    jchartProperties.setvAutoRange(Boolean.valueOf(xmlRange.attributeValue("vAutoRange")).booleanValue());
        jchartProperties.setvMinimumRange(Double.valueOf(xmlRange.attributeValue("vMinimumRange")).doubleValue());
	    jchartProperties.setvMaximumRange(Double.valueOf(xmlRange.attributeValue("vMaximumRange")).doubleValue());

	    Element xmlGrid = (Element) xmlVerticalAxis.selectSingleNode("grid");
	    jchartProperties.setvShowGrid(Boolean.valueOf(xmlGrid.attributeValue("vShowGrid")).booleanValue());
        jchartProperties.setvGridStroke(Float.valueOf(xmlGrid.attributeValue("vGridStroke")).floatValue());
	    newColor[0] = Integer.valueOf(xmlGrid.attributeValue("vGridColorRed")).intValue();
		newColor[1] = Integer.valueOf(xmlGrid.attributeValue("vGridColorGreen")).intValue();
		newColor[2] = Integer.valueOf(xmlGrid.attributeValue("vGridColorBlue")).intValue();
	    jchartProperties.setvGridColor(newColor);

	    //for plot-horizontal
	    Element xmlHorizontalAxis = (Element) xmlPlot.selectSingleNode("horizontalAxis");
	    if(xmlHorizontalAxis == null ) { jchartProperties = null; return;}
        if(xmlHorizontalAxis.attribute("hXYLabel") != null ) jchartProperties.sethXYLabel(xmlHorizontalAxis.attributeValue("hXYLabel"));
	    if(xmlHorizontalAxis.attribute("hBarLabel") != null ) jchartProperties.sethBarLabel(xmlHorizontalAxis.attributeValue("hBarLabel"));
        jchartProperties.sethUnit(xmlHorizontalAxis.attributeValue("hUnit"));
	    jchartProperties.sethFontName(xmlHorizontalAxis.attributeValue("hFontName"));
		jchartProperties.sethFontStyle(Integer.valueOf(xmlHorizontalAxis.attributeValue("hFontStyle")).intValue());
		jchartProperties.sethFontSize(Integer.valueOf(xmlHorizontalAxis.attributeValue("hFontSize")).intValue());
	    newInsets[0] = Integer.valueOf(xmlHorizontalAxis.attributeValue("hLabelInsetsTop")).intValue();
		newInsets[1] = Integer.valueOf(xmlHorizontalAxis.attributeValue("hLabelInsetsLeft")).intValue();
		newInsets[2] = Integer.valueOf(xmlHorizontalAxis.attributeValue("hLabelInsetsBottom")).intValue();
	    newInsets[3] = Integer.valueOf(xmlHorizontalAxis.attributeValue("hLabelInsetsRight")).intValue();
        jchartProperties.sethLabelInsets(newInsets);
	    newInsets[0] = Integer.valueOf(xmlHorizontalAxis.attributeValue("hTickLabelInsetsTop")).intValue();
		newInsets[1] = Integer.valueOf(xmlHorizontalAxis.attributeValue("hTickLabelInsetsLeft")).intValue();
		newInsets[2] = Integer.valueOf(xmlHorizontalAxis.attributeValue("hTickLabelInsetsBottom")).intValue();
	    newInsets[3] = Integer.valueOf(xmlHorizontalAxis.attributeValue("hTickLabelInsetsRight")).intValue();
        jchartProperties.sethTickLabelInsets(newInsets);

	    xmlTicks = (Element) xmlHorizontalAxis.selectSingleNode("ticks");
	    jchartProperties.sethShowTickLabel(Boolean.valueOf(xmlTicks.attributeValue("hShowTickLabel")).booleanValue());
	    jchartProperties.sethShowTickMarks(Boolean.valueOf(xmlTicks.attributeValue("hShowTickMarks")).booleanValue());
        jchartProperties.sethTickLabelFontName(xmlTicks.attributeValue("hTickLabelFontName"));
	    jchartProperties.sethTickLabelFontStyle(Integer.valueOf(xmlTicks.attributeValue("hTickLabelFontStyle")).intValue());
	    jchartProperties.sethTickLabelFontSize(Integer.valueOf(xmlTicks.attributeValue("hTickLabelFontSize")).intValue());

	    if (jchartProperties.gethXYLabel() != null) {
		    xmlRange = (Element) xmlHorizontalAxis.selectSingleNode("range");
		    jchartProperties.sethAutoRange(Boolean.valueOf(xmlRange.attributeValue("hAutoRange")).booleanValue());
		    jchartProperties.sethMinimumRange(Double.valueOf(xmlRange.attributeValue("hMinimumRange")).doubleValue());
		    jchartProperties.sethMaximumRange(Double.valueOf(xmlRange.attributeValue("hMaximumRange")).doubleValue());

		    xmlGrid = (Element) xmlHorizontalAxis.selectSingleNode("grid");
		    jchartProperties.sethShowGrid(Boolean.valueOf(xmlGrid.attributeValue("hShowGrid")).booleanValue());
		    jchartProperties.sethGridStroke(Float.valueOf(xmlGrid.attributeValue("hGridStroke")).floatValue());
		    newColor[0] = Integer.valueOf(xmlGrid.attributeValue("hGridColorRed")).intValue();
		    newColor[1] = Integer.valueOf(xmlGrid.attributeValue("hGridColorGreen")).intValue();
		    newColor[2] = Integer.valueOf(xmlGrid.attributeValue("hGridColorBlue")).intValue();
		    jchartProperties.sethGridColor(newColor);
	    }

	    //for plot-appearance
	    Element xmlAppearance = (Element) xmlPlot.selectSingleNode("appearance");
	    if(xmlAppearance == null ) { jchartProperties = null; return;}
	    jchartProperties.setAppImage(xmlAppearance.attributeValue("appImage"));
	    newInsets[0] = Integer.valueOf(xmlAppearance.attributeValue("appInsetsTop")).intValue();
		newInsets[1] = Integer.valueOf(xmlAppearance.attributeValue("appInsetsLeft")).intValue();
		newInsets[2] = Integer.valueOf(xmlAppearance.attributeValue("appInsetsBottom")).intValue();
	    newInsets[3] = Integer.valueOf(xmlAppearance.attributeValue("appInsetsRight")).intValue();
        jchartProperties.setAppInsets(newInsets);
	    jchartProperties.setAppOutlineStroke(Float.valueOf(xmlAppearance.attributeValue("appOutlineStroke")).floatValue());
	    newColor[0] = Integer.valueOf(xmlAppearance.attributeValue("appOutlineColorRed")).intValue();
	    newColor[1] = Integer.valueOf(xmlAppearance.attributeValue("appOutlineColorGreen")).intValue();
	    newColor[2] = Integer.valueOf(xmlAppearance.attributeValue("appOutlineColorBlue")).intValue();
        jchartProperties.setAppOutlineColor(newColor);
	    newColor[0] = Integer.valueOf(xmlAppearance.attributeValue("appBackgroundColorRed")).intValue();
	    newColor[1] = Integer.valueOf(xmlAppearance.attributeValue("appBackgroundColorGreen")).intValue();
	    newColor[2] = Integer.valueOf(xmlAppearance.attributeValue("appBackgroundColorBlue")).intValue();
        jchartProperties.setAppBackgroundColor(newColor);

	    jchartProperties.setAppSeriesStroke(Float.valueOf(xmlAppearance.attributeValue("appSeriesStroke")).floatValue());


    }

	private void Error(String msg)
	{
		boolean debug = true;
		if (debug)
			System.out.println("DomeObjectSet: " + msg);
	}

	public ChartProperties getJchartProperties()
	{
		return jchartProperties;
	}

	public void setJchartProperties(ChartProperties jchartProperties)
	{
		this.jchartProperties = jchartProperties;
	}

	public void setSeriesColor(String s, int[] color)
	{
	    for (int j = 0; j < getSize(); j++) {
	        if (((Parameter) get(j)).getDataObjectForType("Matrix") != null) {
		        int count = 0;
			    if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
			    if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
	            for (int k = 0; k < count; k++) {
	                if (this.rowsOrColumns ==1 && s.equals(((SetItem) datas.get(j)).rowAliasName[k])) {
                        ((SetItem) datas.get(j)).rowColor[k][0] = 1;
		                ((SetItem) datas.get(j)).rowColor[k][1] = color[0];
		                ((SetItem) datas.get(j)).rowColor[k][2] = color[1];
		                ((SetItem) datas.get(j)).rowColor[k][3] = color[2];
	                    return;
	                 }
		            if (this.rowsOrColumns ==2 && s.equals(((SetItem) datas.get(j)).colAliasName[k])) {
	                    ((SetItem) datas.get(j)).colColor[k][0] = 1;
			            ((SetItem) datas.get(j)).colColor[k][1] = color[0];
			            ((SetItem) datas.get(j)).colColor[k][2] = color[1];
			            ((SetItem) datas.get(j)).colColor[k][3] = color[2];
		                return;
		             }
	           }
	        } else {
	            if (s.equals(((SetItem) datas.get(j)).aliasname)) {
		            ((SetItem) datas.get(j)).rowColor[0][0] = 1;
		            ((SetItem) datas.get(j)).rowColor[0][1] = color[0];
		            ((SetItem) datas.get(j)).rowColor[0][2] = color[1];
		            ((SetItem) datas.get(j)).rowColor[0][3] = color[2];
	                return;
	            }
	        }
	    }
	}

	public int[] getSeriesColor(String s)
	    {
	        for (int j = 0; j < getSize(); j++) {
	            if (((Parameter) get(j)).getDataObjectForType("Matrix") != null){
		            int count = 0;
			        if(this.rowsOrColumns ==1) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getRowCount();
			        if(this.rowsOrColumns ==2) count = ((DomeMatrix)((Parameter) get(j)).getDataObjectForType("Matrix")).getColumnCount();
	                for (int k = 0; k < count; k++) {
		                if (this.rowsOrColumns ==1 && s.equals(((SetItem) datas.get(j)).rowAliasName[k])) {
	                        if(((SetItem) datas.get(j)).rowColor[k][0] == 0) return null;
			                else {
		                        int[] color = new int[3];
			                    color[0] = ((SetItem) datas.get(j)).rowColor[k][1];
			                    color[1] = ((SetItem) datas.get(j)).rowColor[k][2];
			                    color[2] = ((SetItem) datas.get(j)).rowColor[k][3];
		                        return color;
	                        }
		                 }
		                if (this.rowsOrColumns ==2 && s.equals(((SetItem) datas.get(j)).colAliasName[k])) {
	                        if(((SetItem) datas.get(j)).colColor[k][0] == 0) return null;
			                else {
		                        int[] color = new int[3];
			                    color[0] = ((SetItem) datas.get(j)).colColor[k][1];
			                    color[1] = ((SetItem) datas.get(j)).colColor[k][2];
			                    color[2] = ((SetItem) datas.get(j)).colColor[k][3];
		                        return color;
	                        }
		                 }

	                }
	            } else {
		            if (s.equals(((SetItem) datas.get(j)).aliasname)) {
	                    if(((SetItem) datas.get(j)).rowColor[0][0] == 0) return null;
			            else {
		                    int[] color = new int[3];
			                color[0] = ((SetItem) datas.get(j)).rowColor[0][1];
			                color[1] = ((SetItem) datas.get(j)).rowColor[0][2];
			                color[2] = ((SetItem) datas.get(j)).rowColor[0][3];
		                    return color;
	                    }
		             }

	            }
	        }
	        return null;
	    }

    public int getCategorySize()
    {
        int categorySize = 0;
        if(this.getSize() != 0){
            if (((Parameter) get(0)).getDataObjectForType("Matrix") != null){
                if(this.rowsOrColumns ==1) categorySize = ((DomeMatrix)((Parameter) get(0)).getDataObjectForType("Matrix")).getColumnCount();
	            if(this.rowsOrColumns ==2) categorySize = ((DomeMatrix)((Parameter) get(0)).getDataObjectForType("Matrix")).getRowCount();
            } else {
                categorySize = ((DomeVector)((Parameter) get(0)).getDataObjectForType("Vector")).getSize();
            }
        }
        return categorySize;
    }

	public void setCategoryColor(int index, int[] color)
	{
		if (this.rowsOrColumns == 1) {
			if (colCategoryColor == null) return;
			if (index > colCategoryColor.length - 1) return;
			colCategoryColor[index][0] = 1;
			colCategoryColor[index][1] = color[0];
			colCategoryColor[index][2] = color[1];
			colCategoryColor[index][3] = color[2];
		}
		if (this.rowsOrColumns == 2) {
			if (rowCategoryColor == null) return;
			if (index > rowCategoryColor.length - 1) return;
			rowCategoryColor[index][0] = 1;
			rowCategoryColor[index][1] = color[0];
			rowCategoryColor[index][2] = color[1];
			rowCategoryColor[index][3] = color[2];
		}

	}

    public int[] getSeriesColor(int index) {
	    if (this.rowsOrColumns == 1) {
		    if (colCategoryColor == null) return null;
		    if (index > colCategoryColor.length - 1) return null;
		    if (colCategoryColor[index][0] == 0) return null;
		    int[] color = new int[3];
		    color[0] = colCategoryColor[index][1];
		    color[1] = colCategoryColor[index][2];
		    color[2] = colCategoryColor[index][3];
		    return color;
	    }
	    if (this.rowsOrColumns == 2) {
		    if (rowCategoryColor == null) return null;
		    if (index > rowCategoryColor.length - 1) return null;
		    if (rowCategoryColor[index][0] == 0) return null;
		    int[] color = new int[3];
		    color[0] = rowCategoryColor[index][1];
		    color[1] = rowCategoryColor[index][2];
		    color[2] = rowCategoryColor[index][3];
		    return color;
	    }

	    return null;
    }

	public int[][] getColCategoryColor()
	{
		if(colCategoryColor == null) return null;
		return colCategoryColor;
	}

	public void setColCategoryColor(int[][] colCategoryColor)
	{
		this.colCategoryColor = colCategoryColor;
	}

	public int[][] getRowCategoryColor()
	{
		if(rowCategoryColor == null) return null;
		return rowCategoryColor;
	}

	public void setRowCategoryColor(int[][] rowCategoryColor)
	{
		this.rowCategoryColor = rowCategoryColor;
	}

	public int getRowsOrColumns()
	{
		return rowsOrColumns;
	}

	//this method can be called only if isRowsAndColumns() == true
    public void setRowsOrColumns(int rowsOrColumns)
	{
		this.rowsOrColumns = rowsOrColumns;

		if(rowsOrColumns == 0) {
            this.rowHorizontalIndex = -1;
			this.colHorizontalIndex  = -1;
			this.rowCategoryColor = null;
			this.colCategoryColor = null;
		}

		if(rowsOrColumns == 1) {
            if(this.getSeriesSize() == 0) this.rowHorizontalIndex = -1;
			else if(this.rowHorizontalIndex > this.getSeriesSize() -1) this.rowHorizontalIndex = 0;
			if(this.getCategorySize() == 0) this.colCategoryColor = null;
			else if(this.getCategorySize() != 0) {
				if(this.colCategoryColor == null) {
					colCategoryColor = new int[this.getCategorySize()][4];
					for(int i=0; i<this.getCategorySize(); i++) {
						colCategoryColor[i][0] = 0;
					}
				} else if(colCategoryColor.length != this.getCategorySize()) {
					colCategoryColor = new int[this.getCategorySize()][4];
					for(int i=0; i<this.getCategorySize(); i++) {
						colCategoryColor[i][0] = 0;
					}
				}
			}

		}

		if(rowsOrColumns == 2) {
            if(this.getSeriesSize() == 0) this.colHorizontalIndex = -1;
			else if(this.colHorizontalIndex > this.getSeriesSize() -1) this.colHorizontalIndex = 0;
			if(this.getCategorySize() == 0) this.rowCategoryColor = null;
			else if(this.getCategorySize() != 0) {
				if(this.rowCategoryColor == null) {
					rowCategoryColor = new int[this.getCategorySize()][4];
					for(int i=0; i<this.getCategorySize(); i++) {
						rowCategoryColor[i][0] = 0;
					}
				} else if(rowCategoryColor.length != this.getCategorySize()) {
					rowCategoryColor = new int[this.getCategorySize()][4];
					for(int i=0; i<this.getCategorySize(); i++) {
						rowCategoryColor[i][0] = 0;
					}
				}
			}

		}

	}

	//** to see if it can be in either of rows and columns
    public boolean isRowsAndColumns()
	{
        if(datas.size() ==0 ) return false;

        boolean hasVector = false;
        int rowsSize = 0;
        int columnsSize = 0;

        //** to find out the format of datas
        //** if there is a DomeVector in datas, DomeObjectSet can only be in rows or columns
        for(int i = 0; i<datas.size(); i++) {
            Parameter objInVec = (Parameter)((SetItem)datas.get(i)).data;
            if (objInVec.getDataObjectForType("Vector") != null) {
                hasVector = true;
            }
        }

        if(hasVector == true) return false;
        //** no DomeVector in datas(all are DomeMatrix)
        if(hasVector == false) {
            boolean rows = true;
            boolean columns = true;
            Parameter firstObj = (Parameter)((SetItem)datas.get(0)).data;
            rowsSize = ((DomeMatrixData)firstObj.getDataObjectForType("Matrix")).getRowCount();
            columnsSize = ((DomeMatrixData)firstObj.getDataObjectForType("Matrix")).getColumnCount();
            for(int i = 1; i<datas.size(); i++) {
                Parameter otherObj = (Parameter)((SetItem)datas.get(i)).data;
                if(((DomeMatrixData)otherObj.getDataObjectForType("Matrix")).getRowCount() != rowsSize) columns = false;
                if(((DomeMatrixData)otherObj.getDataObjectForType("Matrix")).getColumnCount() != columnsSize) rows = false;
            }

            if(rows == true && columns == true)  return true;    //** it can be in either of rows and columns
            else return false;
        }

        return false;
	}

    //This method is used to set DomeObjectSet to be in rows or columns according to the structure of datas.
    //If DomeObjectSet can be in either of rows and columns, set it to be in rows by default if former rowsOrColumns is equal to zero.
    public void setRowsOrColumns()
    {
        if(datas.size() ==0 ) {
            setRowsOrColumns(0);
            return;
        }

        boolean hasVector = false;
        boolean rowsAndColumns = false;   //**  it can be in either of rows and columns(same with DomeObjectSet.isRowsAndColumns())
        boolean rowsOrColumns = false;
        int rowsSize = 0;
        int columnsSize = 0;

        //** to find out the format of datas
        //** if there is a DomeVector in datas, DomeObjectSet can only be in rows or columns
        for(int i = 0; i<datas.size(); i++) {
            Parameter objInVec = (Parameter)((SetItem)datas.get(i)).data;
            if (objInVec.getDataObjectForType("Vector") != null) {
                hasVector = true;
                rowsOrColumns = ((DomeVectorData) objInVec.getDataObjectForType("Vector")).isRowVector();
                if(rowsOrColumns == true) columnsSize = ((DomeVectorData) objInVec.getDataObjectForType("Vector")).getSize();
                else rowsSize = ((DomeVectorData) objInVec.getDataObjectForType("Vector")).getSize();
            }
        }

        //** no DomeVector in datas(all are DomeMatrix)
        if(hasVector == false) {
            boolean rows = true;
            boolean columns = true;
            Parameter firstObj = (Parameter)((SetItem)datas.get(0)).data;
            rowsSize = ((DomeMatrixData)firstObj.getDataObjectForType("Matrix")).getRowCount();
            columnsSize = ((DomeMatrixData)firstObj.getDataObjectForType("Matrix")).getColumnCount();
            for(int i = 1; i<datas.size(); i++) {
                Parameter otherObj = (Parameter)((SetItem)datas.get(i)).data;
                if(((DomeMatrixData)otherObj.getDataObjectForType("Matrix")).getRowCount() != rowsSize) columns = false;
                if(((DomeMatrixData)otherObj.getDataObjectForType("Matrix")).getColumnCount() != columnsSize) rows = false;
            }

            if(rows == true && columns == true) rowsAndColumns = true;
            if(rows == true && columns == false) rowsOrColumns = true;
            if(rows == false && columns == true) rowsOrColumns = false;
            if(rows == false && columns == false) return;  //** should be no such a case
        }

        if(hasVector == true || (hasVector == false && rowsAndColumns == false)) {   //** DomeObjectSet can only be in rows or columns
            if(rowsOrColumns ==true) setRowsOrColumns(1);
            else setRowsOrColumns(2);
        } else if(hasVector == false && rowsAndColumns == true) {
            if(this.rowsOrColumns == 0) setRowsOrColumns(1); //If DomeObjectSet can be in either of rows and columns, set it to be in rows by default if former rowsOrColumns is equal to zero.
            else setRowsOrColumns(this.rowsOrColumns);
        }

    }


}

