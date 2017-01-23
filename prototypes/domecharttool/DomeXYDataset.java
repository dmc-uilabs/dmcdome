//DomeXYDataSet.java  May 21, 2002
// ver 0.1 :inherit from DefaultXYDataSet which parse constructure by taking 
//         DomeVector, and DomeMatrix
// ver 0.2 : May 29,2002
//           add content for DomeMatrix

/* ==================================================
 * JCommon : a general purpose class library for Java
 * ==================================================
 *
 * Project Info:  http://www.object-refinery.com/jcommon/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ---------------------
 * DefaultXYDataset.java
 * ---------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DomeXYDataset.java,v 1.1.1.1 2003/05/05 16:12:31 renu Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Moved to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 07-Dec-2001 : Replaced XYDataItem class with XYDataPair (DG);
 * 15-Mar-2002 : Modified to use ResourceBundle for elements that require localisation (DG);
 *
 */



import java.util.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

import  com.jrefinery.data.*;


/**
 * A convenience class that provides a default implementation of the XYDataset interface.
 * The standard constructor accepts data in a two dimensional array where the first dimension is
 * the series, and the second dimension is the category.
 *
 * subclassed to take in DomeVector, DomeMatrix
 *
 */
public class DomeXYDataset extends DefaultXYDataset {

   
    public DomeXYDataset() {
	super();
    }

    /**
     * Constructs a new dataset, and populates it with the given data.
     * <P>
     * The dimensions of the data array are [series][item][x=0, y=1]. The x-values should be Number
     * or Date objects, the y-values should be Number objects.  Any other types are interpreted as
     * zero. The data will be sorted so that the x-values are ascending.
     */
    public DomeXYDataset(Object[][][] data) {
	super(data);
    }

    /**
     * Constructs a new dataset with the given data.
     */
    public DomeXYDataset(String[] seriesNames, Object[][][] data) {
	super(seriesNames, data);
    }

    /**
     * Constructs a new dataset with the given data.
     */
    public DomeXYDataset(List seriesNames, Object[][][] data) {
	super(seriesNames,data);
    }

   
    /**
     * Constructs a new dataset with DomeVector
     * the first DomeVector will be the x data
     * and pair with each of the following line to be xy pair
     *  ver 0.2 note: should taken care of if only 1 row or only 1 column
     *                however now, it doesn't show any error and just returns
     *                an empty set
     */
    public DomeXYDataset(DomeVectorData[] data){
	//check if input is valid
	if(data.length==0) 
	    throw new IllegalArgumentException("DomeXYDataset----Bad DomeVector Input Array: Empty Array");
	
	
	boolean isRowVector=data[0].isRowVector();
	int size=data[0].getSize();
	
	for(int i=1;i<data.length;i++)
	    {
		if(data[i].isRowVector()!=isRowVector)
		    throw new IllegalArgumentException("DomeXYDataset----Bad DomeVector Input Array: Mixed Types in Array");
		if(data[i].getSize()!=size)
		    throw new IllegalArgumentException("DomeXYDataset----Bad DomeVector Input Array: Mixed size elements in Array");
	    }
	
	
	//do transform into x-y pair array

	
	

	int seriesCount = data.length-1;//bcz the first element is the x-axis

	allSeriesData = new ArrayList(seriesCount);

	for (int series=0; series<seriesCount; series++) {
	    List oneSeriesData = new ArrayList();
	    int maxItemCount = data[0].getSize();
	    for (int itemIndex=0; itemIndex<maxItemCount; itemIndex++) {
		Object xObject = data[0].getItem(itemIndex);
		if (xObject!=null) {
		    Number xNumber = null;
		    if (xObject instanceof Number) {
			xNumber = (Number)xObject;
		    }
		    //note: the following is not happening in out domevector case but we still save it here
		    else if (xObject instanceof Date) {
			Date xDate = (Date)xObject;
			xNumber = new Long(xDate.getTime());
		    }
		    //end of note
		    else xNumber = new Integer(0);

		    Number yNumber = data[series+1].getItem(itemIndex);
		    oneSeriesData.add(new XYDataPair(xNumber, yNumber));
		}
	    }
	    Collections.sort(oneSeriesData);
	    allSeriesData.add(series, oneSeriesData);
	}


	this.seriesNames = seriesNameListFromDataArray(seriesCount);
    }
  /**
   *    DomeMatrixData as input
   *    This is very similar to the above case, taking the first row as X, each following row as Y
   *
   *
   */


  public DomeXYDataset(DomeMatrixData data){
	
    //it should be ok to have a empty dataset
    if(data.getRowCount()==0||data.getColumnCount()==0) 
        throw new IllegalArgumentException("DomeXYDataset----Bad DomeMatrix Input Array: Empty Array");

	
    int rowCount=data.getRowCount();
    int colCount=data.getColumnCount();
	
    //do transform into x-y pair array

    int seriesCount = rowCount-1;//bcz the first element is the x-axis

    allSeriesData = new ArrayList(seriesCount);

    for (int series=0; series<seriesCount; series++) {
      List oneSeriesData = new ArrayList();
      int maxItemCount = colCount;
      for (int itemIndex=0; itemIndex<maxItemCount; itemIndex++) {
	Object xObject = data.getItem(0,itemIndex);
	if (xObject!=null) {
	  Number xNumber = null;
	  if (xObject instanceof Number) {
	    xNumber = (Number)xObject;
	  }
	  //note: the following is not happening in out domeMatrix case but we still save it here
	  else if (xObject instanceof Date) {
	    Date xDate = (Date)xObject;
	    xNumber = new Long(xDate.getTime());
	  }
	  //end of note
	  else xNumber = new Integer(0);

	  Number yNumber = data.getItem(series+1,itemIndex);
	  oneSeriesData.add(new XYDataPair(xNumber, yNumber));
	}
      }
      Collections.sort(oneSeriesData);
      allSeriesData.add(series, oneSeriesData);
    }


    this.seriesNames = seriesNameListFromDataArray(seriesCount);
  }

 
    /**
     * Returns the number of series.
     */
    public int getSeriesCount() {
	return allSeriesData.size();
    }

    /**
     * Returns the number of items in the specified series.
     * @param series The index of the series (zero-based).
     */
    public int getItemCount(int series) {
	List oneSeriesData = (List)allSeriesData.get(series);
	return oneSeriesData.size();
    }

    /**
     * Returns the name of the specified series.
     * @param series The index of the required series (zero-based).
     */
    public String getSeriesName(int series) {
	return seriesNames.get(series).toString();
    }

    /**
     * Sets the names of the series in the data source.
     * @param seriesNames The names of the series in the data source.
     */
    public void setSeriesNames(String[] seriesNames) {
	this.seriesNames = Arrays.asList(seriesNames);
	fireDatasetChanged();
    }

  
    /**
     * Returns a List of String objects that can be used as series names.
     * @param data An array containing the data for the data source.
     */
    public static List seriesNameListFromDataArray(Object[][] data) {

        String baseName = "com.jrefinery.data.resources.DataPackageResources";
        ResourceBundle resources = ResourceBundle.getBundle(baseName);

        String prefix = resources.getString("series.default-prefix")+" ";

	int seriesCount = data.length;
	List seriesNameList = new ArrayList(seriesCount);
	for (int i=0; i<seriesCount; i++) {
	    seriesNameList.add(prefix+(i+1));
	}
	return seriesNameList;

    }
    
    /**
     * Returns a List of String objects that can be used as series names.
     * @param length:  how many series
     */
    public static List seriesNameListFromDataArray(int length) {

        String baseName = "com.jrefinery.data.resources.DataPackageResources";
        ResourceBundle resources = ResourceBundle.getBundle(baseName);

        String prefix = resources.getString("series.default-prefix")+" ";

	int seriesCount = length;
	List seriesNameList = new ArrayList(seriesCount);
	for (int i=0; i<seriesCount; i++) {
	    seriesNameList.add(prefix+(i+1));
	}
	return seriesNameList;

    }

}
