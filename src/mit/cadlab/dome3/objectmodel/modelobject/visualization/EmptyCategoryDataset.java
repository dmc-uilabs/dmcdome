// EmptyCategoryDataset.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.visualization;

import org.jfree.data.AbstractSeriesDataset;
import org.jfree.data.CategoryDataset;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EmptyCategoryDataset extends AbstractSeriesDataset implements CategoryDataset
{
	/**
	 * Default constructor.
	 */
	public EmptyCategoryDataset()
	{
	}


	public int getCategoryCount()
	{
		return 0;
	}

	public List getCategories()
	{
		//can't return null
		//return null;
		//can return a empty list
		return new ArrayList();
	}

	public Number getValue(int series, Object category)
	{
		return null;
	}

	/**
	 * Returns the name of the series.
	 * @param series The index (zero-based) of the series;
	 * @return The name of the series.
	 */
	public String getSeriesName(int series)
	{
		return null;
	}

	public int getSeriesCount()
	{
		return 0;
	}

	public int getRowCount()
	{
		return 0;
	}

	public int getColumnCount()
	{
		return 0;
	}

	public Number getValue(int i, int i1)
	{
		return null;
	}

	public Comparable getRowKey(int i)
	{
		return null;
	}

	public int getRowIndex(Comparable comparable)
	{
		return 0;
	}

	public List getRowKeys()
	{
		return null;
	}

	public Comparable getColumnKey(int i)
	{
		return null;
	}

	public int getColumnIndex(Comparable comparable)
	{
		return 0;
	}

	public List getColumnKeys()
	{
		return null;
	}

	public Number getValue(Comparable comparable, Comparable comparable1){
		return null;
	}
}
