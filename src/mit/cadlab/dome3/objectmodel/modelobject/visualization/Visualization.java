// Visualization.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.visualization;

import org.jfree.chart.JFreeChart;
import org.dom4j.Element;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeCollection;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.util.DListListener;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.beans.PropertyChangeListener;

/**
 *
 */
public interface Visualization extends ModelObject, ViewSupport, DomeCollection
{

	public static TypeInfo TYPE_INFO = new TypeInfo("Visualization");
	public static String XML_TAG = "visualization";
	public static final String XML_MAPPED_TAG = "mappedVisualization";
	public static final String CONSTANT = "constant";
	public static final String VERTICAL = "vertical";
	public static final String ROWNAME = "row name";
	public static final String CHARTTYPESELECTION = "chartTypeSelection";
	public static final String SETSCHANGED = "sets changed";
	public static final String SETSELECTIONCHANGED = "set selection changed";
    public static final String DATACHANGED = "visualization changed";

	public static final String LIST = "list";
	public static final String AVAILABLESERIESCHANGED = "available series changed";
	public static final String VECTORDATACHANGED = "vector data changed";
	public static final String INVALIDVECTORCHANGE = "invalid vector change";
	public static final String MATRIXDATACHANGED = "matrix data changed";
	public static final String INVALIDMATRIXCHANGE = "invalid matrix change";


	public static final String XYCHART = "XY plot";
	public static final String PIECHART = "Pie plot";
	public static final String CATEGORYCHART = "Bar plot";
	public static final String STACKEDCHART = "Stacked plot";

	public static final String XYLINECHART = "Line plot";
	public static final String XYSCATTERCHART = "Scatter plot";
	public static final String XYSTEPCHART = "Step plot";
	public static final String XYAREACHART = "Area plot";
	public static final String XYSTACKEDAREACHART = "Stacked Area plot";
	public static final String XYSTEPAREACHART = "Step Area plot";

	public static final String CATEGORYBARCHART = "Plane Bar plot";
	public static final String CATEGORY3DBARCHART = "3D Bar plot";
	public static final String STACKEDBARCHART = "Stacked Bar plot";
	public static final String STACKEDBAR3DCHART = "Stacked 3D Bar plot";

	//**public static final String STACKEDAREACHART = "Stacked Area plot";

	public boolean isConstant();

	public boolean isVertical();

	public void setConstant(boolean isConstant);

	public void setVertical(boolean vertical);

	public List getAvailableList();

	public Vector getSetsList();

	public void setAvailableList(Vector available);

	public void setSetsList(Vector sets);

	public void addToAvailabelList(Collection c);

	public String[] getValidVisTypes();

	public String[] getValidVisTypesForCurrentVisType();

	public String getCurrentVisType();

	public void setCurrentVisType(String newType);

	public String getCurrentVisSubType();

	public void setCurrentVisSubType(String newType);

	public void setSelectedSet(int index);

	public DomeObjectSet getSelectedSet();

	public int getSelectedSetIndex();

	public void setChart(JFreeChart c);

	public JFreeChart getChart();

	public void addAvailableListListener(DListListener dl);

	public void removeAvailableListListener(DListListener dl);

	void removeFromAvailableList(Collection c);

	public PropertyChangeListener getValueShadowListener();

	public PropertyChangeListener getValueUnitShadowListener();

	public Element toXmlMappedRef();

}
