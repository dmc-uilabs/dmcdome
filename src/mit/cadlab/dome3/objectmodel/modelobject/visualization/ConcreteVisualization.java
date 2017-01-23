// ConcreteVisualization.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.visualization;

import org.jfree.chart.JFreeChart;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.DeletionEvent;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.InterfaceModelView;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.AbstractDataObject;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList;
import mit.cadlab.dome3.objectmodel.modelobject.AbstractModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 *
 */
public class ConcreteVisualization extends AbstractModelObject implements Visualization
{

	protected boolean isConstant = false;
	protected boolean vertical = true;
	protected DataList availableList = new DataList();
	protected Vector setsList = new Vector();
	protected String currentVisType = XYCHART;
	protected String currentVisSubType = XYLINECHART;
	protected int selectedIndex = -1;
	HashMap vector_parameter = new HashMap();  //key is vector,parameter is value

	protected PropertyChangeListener shadowListener = null;
	protected ModelObjectNameListener nameListener = new ModelObjectNameListener();

	protected JFreeChart chart = null;

	protected PropertyChangeListener propertyListener;
	AvailableSeriesDeletionListener myDeletionListener = new AvailableSeriesDeletionListener();

	public ConcreteVisualization(ModelObjectScope scope, Id id)
	{
		super(scope, id);
		//System.out.println(id);

		propertyListener = getPropertyListener();
		availableList.addDListListener(new DataListListener());
	}

	/**
	 *  this function is to "clone" a visualization
	 * @param scope
	 * @param id
	 * @param vis
	 */
	public ConcreteVisualization(ModelObjectScope scope, Id id, Visualization vis)
	{
		super(scope, id, vis);
		isConstant = vis.isConstant();
		vertical = vis.isVertical();
		currentVisType = vis.getCurrentVisType();
		currentVisSubType = vis.getCurrentVisSubType();
		selectedIndex = vis.getSelectedSetIndex();
		Object topScope = getObjectTopScope(scope);
		if(topScope!= getObjectTopScope(vis.getScope()))
		//**if (topScope instanceof ModelInterface || topScope instanceof InterfaceModelView)
		{
            //** two cases: add subscription to iModel; then a subscriptionInterface will be created
			if(scope instanceof Subscription || topScope instanceof SubscriptionInterface) add(0, vis.getAvailableList());

			else addAndMap(0, vis.getAvailableList());

			if(vis.getSetsList().size() !=0) {
				for(int i = 0; i<vis.getSetsList().size();i++) {
					DomeObjectSet oriSet = (DomeObjectSet) vis.getSetsList().get(i);
					DomeObjectSet newSet = new DomeObjectSet(oriSet.getName(), null);
					newSet.useDomeName = oriSet.useDomeName;
					newSet.rowsOrColumns = oriSet.getRowsOrColumns();
					newSet.rowHorizontalIndex = oriSet.getRowHorizontalIndex();
					newSet.colHorizontalIndex = oriSet.getColHorizontalIndex();
					if (oriSet.getColCategoryColor() != null) {
						if (oriSet.getColCategoryColor().length != 0) {
							newSet.colCategoryColor = new int[oriSet.getColCategoryColor().length][4];
							for (int k = 0; k < oriSet.getColCategoryColor().length; k++) {
								for (int j = 0; j < 4; j++) {
									newSet.colCategoryColor[k][j] = oriSet.getColCategoryColor()[k][j];
								}
							}
						}
					}
					if (oriSet.getRowCategoryColor() != null) {
						if (oriSet.getRowCategoryColor().length != 0) {
							newSet.rowCategoryColor = new int[oriSet.getRowCategoryColor().length][4];
							for (int k = 0; k < oriSet.getRowCategoryColor().length; k++) {
								for (int j = 0; j < 4; j++) {
									newSet.rowCategoryColor[k][j] = oriSet.getRowCategoryColor()[k][j];
								}
							}
						}
					}

					for(int j = 0;j<oriSet.getData().size(); j++) {
						SetItem oriSetItem = (SetItem) oriSet.getData().get(j);
						int index = vis.getAvailableList().indexOf(oriSetItem.data);
						SetItem newSetItem = new SetItem((DomeObject)availableList.get(index),oriSetItem.aliasname,oriSetItem.Selected);
						if (oriSetItem.rowAliasName != null) {
						    if (oriSetItem.rowAliasName.length != 0) {
						        newSetItem.rowAliasName = new String[oriSetItem.rowAliasName.length];
						        for (int k = 0; k < oriSetItem.rowAliasName.length; k++) {
						            newSetItem.rowAliasName[k] = oriSetItem.rowAliasName[k];
						        }
						    }
						} else newSetItem.rowAliasName = null;

						if (oriSetItem.rowSelected != null) {
						    if (oriSetItem.rowSelected.length != 0) {
						        newSetItem.rowSelected = new boolean[oriSetItem.rowSelected.length];
						        for (int k = 0; k < oriSetItem.rowSelected.length; k++) {
						            newSetItem.rowSelected[k] = oriSetItem.rowSelected[k];
						        }
						    }
						} else newSetItem.rowSelected = null;

						if (oriSetItem.rowColor != null) {
							if (oriSetItem.rowColor.length != 0) {
								newSetItem.rowColor = new int[oriSetItem.rowColor.length][4];
								for (int k = 0; k < oriSetItem.rowColor.length; k++) {
									for (int n = 0; n < 4; n++) {
										newSetItem.rowColor[k][n] = oriSetItem.rowColor[k][n];
									}
								}
							}
						} else newSetItem.rowColor = null;

						if (oriSetItem.colAliasName != null) {
							if (oriSetItem.colAliasName.length != 0) {
								newSetItem.colAliasName = new String[oriSetItem.colAliasName.length];
								for (int k = 0; k < oriSetItem.colAliasName.length; k++) {
								    newSetItem.colAliasName[k] = oriSetItem.colAliasName[k];
								}
							}
						} else newSetItem.colAliasName = null;

						if (oriSetItem.colSelected != null) {
							if (oriSetItem.colSelected.length != 0) {
								newSetItem.colSelected = new boolean[oriSetItem.colSelected.length];
								for (int k = 0; k < oriSetItem.colSelected.length; k++) {
								    newSetItem.colSelected[k] = oriSetItem.colSelected[k];
								}
							}
						} else newSetItem.colSelected = null;

						if (oriSetItem.colColor != null) {
							if (oriSetItem.colColor.length != 0) {
								newSetItem.colColor = new int[oriSetItem.colColor.length][4];
								for (int k = 0; k < oriSetItem.colColor.length; k++) {
									for (int n = 0; n < 4; n++) {
										newSetItem.colColor[k][n] = oriSetItem.colColor[k][n];
									}
								}
							}
						} else newSetItem.colColor = null;

						/*if (((Parameter) newSetItem.data).getDataObjectForType("Matrix") != null){
							  for(int k = 0; k<newSetItem.rowAliasName.length; k++) {
								  newSetItem.rowAliasName[k] = oriSetItem.rowAliasName[k];
								  newSetItem.rowSelected[k] = oriSetItem.rowSelected[k];
							  }
						}*/
						newSet.datas.addElement(newSetItem);
					}

					this.setsList.addElement(newSet);
				}
			}
		} else {
		    this.addToAvailabelList(vis.getAvailableList());
		    //*addCopies(list.getValues());
			if(vis.getSetsList().size() !=0) {
				for(int i = 0; i<vis.getSetsList().size();i++) {
					DomeObjectSet oriSet = (DomeObjectSet) vis.getSetsList().get(i);
					DomeObjectSet newSet = new DomeObjectSet(oriSet);

					/*newSet.setHorizontalIndex(oriSet.getHorizontalIndex());
					newSet.useDomeName = oriSet.useDomeName;

					for(int j = 0;j<oriSet.getData().size(); j++) {
						SetItem oriSetItem = (SetItem) oriSet.getData().get(j);
						SetItem newSetItem = new SetItem(oriSetItem.data,oriSetItem.aliasname,oriSetItem.Selected);
						if (((Parameter) newSetItem.data).getDataObjectForType("Matrix") != null){
							  for(int k = 0; k<newSetItem.rowAliasName.length; k++) {
								  newSetItem.rowAliasName[k] = oriSetItem.rowAliasName[k];
								  newSetItem.rowSelected[k] = oriSetItem.rowSelected[k];
							  }
						}
						newSet.datas.addElement(newSetItem);
					}*/

					this.setsList.addElement(newSet);
				}
			}

		}

		propertyListener = getPropertyListener();
		availableList.addDListListener(new DataListListener());
	}

	/**
	 * to read in a xml representation to create a visualization
	 * @param scope
	 * @param xmlElement
	 */
	public ConcreteVisualization(ModelObjectScope scope, Element xmlElement)
	{
		super(scope, xmlElement); //id and name

		XMLUtils.makeRootElement(xmlElement);

		Element typeelement = (Element) xmlElement.selectSingleNode("currentVisualType");
		currentVisType = new String(typeelement.attributeValue("value"));
		Element subtypeelement = (Element) xmlElement.selectSingleNode("currentVisualSubType");
		currentVisSubType = subtypeelement.attributeValue("value");
		propertyListener = getPropertyListener();

		List items = xmlElement.selectNodes("AvailableList/" + Parameter.XML_TAG);
		for (Iterator i = items.iterator(); i.hasNext();) {
			Element item = (Element) i.next();
			Id paramId = AbstractModelObject.parseXmlRef(item);
			DomeObject param = scope.getModelObjectById(paramId);
			if (param != null) {
				this.addToAvailableList(param);
			}
		}
		items.clear();


		items = xmlElement.selectNodes("SetList/" + DomeObjectSet.XML_TAG);
		for (Iterator i = items.iterator(); i.hasNext();) {
			Element item = (Element) i.next();
			DomeObjectSet dos = new DomeObjectSet(scope, item);
			dos.addPropertyChangeListener(propertyListener);
			setsList.add(dos);
		}

		Element selectedindexelement = (Element) xmlElement.selectSingleNode("selectedSetIndex");
		selectedIndex = (new Integer(selectedindexelement.attributeValue("value"))).intValue();

		Element isConstantelement = (Element) xmlElement.selectSingleNode("isConstant");
		isConstant = (new Boolean(isConstantelement.attributeValue("value"))).booleanValue();

		Element verticalelement = (Element) xmlElement.selectSingleNode("vertical");
		vertical = (new Boolean(verticalelement.attributeValue("value"))).booleanValue();

		//create a chart accorindly
		if(selectedIndex == -1)  chart = VisualizationUtils.createChart(null, currentVisType, currentVisSubType, vertical);
		else chart = VisualizationUtils.createChart((DomeObjectSet) setsList.get(selectedIndex), currentVisType, currentVisSubType, vertical);

		availableList.addDListListener(new DataListListener());

	}


	protected PropertyChangeListener getPropertyListener()
	{
		return new ConcreteVisualization.dataPropertyChangeListener();
	}


	/**
	 * to put into a XML representation to save it
	 * @return
	 */
	protected String contentToString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("currentVisType: " + getCurrentVisType() + "(" + getCurrentVisSubType() + ")");
		sb.append("\navailable series: " + availableList.toString());
		for (int i = 0; i < setsList.size(); i++) {
			sb.append("\nset:" + ((DomeObjectSet) setsList.get(i)).getName() + ":");
			DomeObject[] items = ((DomeObjectSet) setsList.get(i)).getAllSetItems();
			for (int j = 0; j < items.length; j++)
				sb.append(items[j].getName() + ", isSelected:" + ((DomeObjectSet) setsList.get(i)).isSelected(j));
		}

		return sb.toString();
	}

	protected void addXmlContent(Element xmlElement)
	{
		//save currentVistype and subType
		xmlElement.addElement("currentVisualType").addAttribute("value", getCurrentVisType());
		xmlElement.addElement("currentVisualSubType").addAttribute("value", getCurrentVisSubType());
		xmlElement.addElement("selectedSetIndex").addAttribute("value", (new Integer(selectedIndex)).toString());
		xmlElement.addElement("isConstant").addAttribute("value", (new Boolean(isConstant)).toString());
		xmlElement.addElement("vertical").addAttribute("value", (new Boolean(vertical)).toString());
		//save avalilable list

		XMLUtils.addCollectionRef(xmlElement, "AvailableList", availableList);

		XMLUtils.addCollection(xmlElement, "SetList", setsList);


	}

	protected TypeInfo getTypeInfo()
	{
		return TYPE_INFO;
	}

	public String getXmlTag()
	{
		return XML_TAG;
	}

	public String getXmlMappedTag()
	{
		return XML_MAPPED_TAG;
	}

	public Element toXmlMappedRef()
	{
		Element xmlElement = DocumentHelper.createElement(getXmlMappedTag());
		createXmlRef(xmlElement);
		return xmlElement;
	}
	public void addAvailableListListener(DListListener dl)
	{
		availableList.addDListListener(dl);
	}

	public void removeAvailableListListener(DListListener dl)
	{
		availableList.removeDListListener(dl);
	}

	public boolean isConstant()
	{
		return isConstant;
	}

	public boolean isVertical()
	{
		return vertical;
	}

	public void setConstant(boolean isConstant)
	{
		Boolean oldIsConstant = new Boolean(this.isConstant);
		this.isConstant = isConstant;
		firePropertyChange(CONSTANT, oldIsConstant, new Boolean(this.isConstant));
	}

	public void setVertical(boolean vertical)
	{
		Boolean oldVertical = new Boolean(this.vertical);
		this.vertical = vertical;
		firePropertyChange(VERTICAL, oldVertical, new Boolean(this.vertical));
	}

	public void setRowName(int setNO, int rowNO, String name)
	{
		ArrayList newValue = new ArrayList();
		newValue.add(new Integer(setNO));
		newValue.add(new Integer(rowNO));
		newValue.add(name);
		firePropertyChange(ROWNAME,null,newValue);
	}

	public void addToAvailableList(DomeObject obj)
	{
		if ((obj instanceof Parameter) && (((Parameter) obj).getDataObjectForType("Vector") != null)) {
			obj.addPropertyChangeListener("name", nameListener);

			DomeVectorData d = (DomeVectorData) (((Parameter) obj).getDataObjectForType("Vector"));
			d.addPropertyChangeListener(propertyListener);
			vector_parameter.put(d, obj);
			availableList.add(obj);
			firePropertyChange(AVAILABLESERIESCHANGED, null, availableList);
		}
		if ((obj instanceof Parameter) && (((Parameter) obj).getDataObjectForType("Matrix") != null)) {
			obj.addPropertyChangeListener("name", nameListener);

			DomeMatrixData d = (DomeMatrixData) (((Parameter) obj).getDataObjectForType("Matrix"));
			d.addPropertyChangeListener(propertyListener);
			vector_parameter.put(d, obj);
			availableList.add(obj);
			firePropertyChange(AVAILABLESERIESCHANGED, null, availableList);
		}

	}


	public void addToAvailableList(int index, DomeObject obj)
	{
		if ((obj instanceof Parameter) && (((Parameter) obj).getDataObjectForType("Vector") != null)) {
			obj.addPropertyChangeListener("name", nameListener);

			DomeVectorData d = (DomeVectorData) (((Parameter) obj).getDataObjectForType("Vector"));
			d.addPropertyChangeListener(propertyListener);
			vector_parameter.put(d, obj);
			availableList.add(index, obj);
			firePropertyChange(AVAILABLESERIESCHANGED, null, availableList);
		}
		if ((obj instanceof Parameter) && (((Parameter) obj).getDataObjectForType("Matrix") != null)) {
			obj.addPropertyChangeListener("name", nameListener);

			DomeMatrixData d = (DomeMatrixData) (((Parameter) obj).getDataObjectForType("Matrix"));
			d.addPropertyChangeListener(propertyListener);
			vector_parameter.put(d, obj);
			availableList.add(index, obj);
			firePropertyChange(AVAILABLESERIESCHANGED, null, availableList);
		}

	}

	//assume all set must be add to available before putting into visualization set, so it already has propertylistener in it

	public void addToSetsList(DomeObjectSet set)
	{
		setsList.add(set);
		firePropertyChange(SETSCHANGED, null, setsList);
	}

	public void removeFromSet(DomeObject obj)
	{
		if (obj instanceof Parameter)
        if ((((Parameter) obj).getDataObjectForType("Vector") != null) || (((Parameter) obj).getDataObjectForType("Matrix") != null)) {
			for (int i = 0; i < setsList.size(); i++) {
				DomeObjectSet dos = (DomeObjectSet) setsList.get(i);
				if (dos.exist(obj)) {

					dos.remove(obj);
				}
			}
			firePropertyChange(SETSCHANGED, null, setsList);
		}
	}

	public void setAvailableList(Vector available)
	{
		availableList.clear();
		for (int i = 0; i < available.size(); i++) {
			DomeObject dobj = (DomeObject) available.get(i);
			DomeVectorData d = (DomeVectorData) (((Parameter) dobj).getDataObjectForType("Vector"));
			if (d != null) {
				dobj.addPropertyChangeListener("name", nameListener);
				d.addPropertyChangeListener(getPropertyListener());
				vector_parameter.put(d, dobj);
				availableList.add(available.get(i));
			}
		}
		firePropertyChange(AVAILABLESERIESCHANGED, null, availableList);
	}


	public void setSetsList(Vector sets)
	{
		setsList.clear();
		for (int i = 0; i < sets.size(); i++) {
			setsList.addElement(sets.get(i));
		}
		if (selectedIndex > sets.size()-1){
			if (sets.size() == 0) selectedIndex = -1;
			else selectedIndex = 0;
		} else if (selectedIndex == -1 && sets.size() !=0) selectedIndex = 0;
		firePropertyChange(DATACHANGED, null, this);
	}

	public List getAvailableList()
	{
		return availableList;
	}

	public Vector getSetsList()
	{
		return setsList;
	}


	public void removeFromAvailableList(Collection c)
	{
		Iterator i = c.iterator();
		while (i.hasNext()) {
			DomeObject dobj = (DomeObject) i.next();
			if (availableList.contains(dobj)) {
				DomeVectorData d = (DomeVectorData) (((Parameter) dobj).getDataObjectForType("Vector"));
				if (d != null) {
					dobj.removePropertyChangeListener("name", nameListener);
					d.removePropertyChangeListener(getPropertyListener());
					vector_parameter.remove(d);
					//also has to remove from set
					removeFromSet(dobj);
					availableList.remove(dobj);


				}

			}
		}
	}


	public void addToAvailabelList(Collection c)
	{


		Iterator i = c.iterator();
		while (i.hasNext()) {

			DomeObject dobj = (DomeObject) i.next();
			if ((dobj instanceof Parameter) && (((Parameter) dobj).getDataObjectForType("Vector") != null)) {
				DomeVectorData d = (DomeVectorData) (((Parameter) dobj).getDataObjectForType("Vector"));
				if (d != null) {
					dobj.addPropertyChangeListener("name", nameListener);
					d.addPropertyChangeListener(getPropertyListener());
					vector_parameter.put(d, dobj);
					availableList.add(dobj);
				}
			}
				if ((dobj instanceof Parameter) && (((Parameter) dobj).getDataObjectForType("Matrix") != null)) {
					DomeMatrixData d = (DomeMatrixData) (((Parameter) dobj).getDataObjectForType("Matrix"));
					if (d != null) {
						dobj.addPropertyChangeListener("name", nameListener);
						d.addPropertyChangeListener(getPropertyListener());
						vector_parameter.put(d, dobj);
						availableList.add(dobj);
					}
			}

		}
		firePropertyChange(AVAILABLESERIESCHANGED, null, availableList);

	}


	public List getView()
	{
		return Collections.unmodifiableList(availableList);
	}

	public void addViewListener(DListListener l)
	{
	}

	public void removeViewListener(DListListener l)
	{
	}

	public String[] getValidVisTypes()
	{

		//   return new String[]{XYCHART, CATEGORYCHART, PIECHART};
		//disable bar chart now
		return new String[]{XYCHART, CATEGORYCHART};

	}


	public boolean amongValidType(String option)
	{
		String[] validTypes = getValidVisTypes();
		for (int i = 0; i < validTypes.length; i++) {
			if (option.equals(validTypes[i]))
				return true;
		}
		return false;
	}

	public boolean amongValidSubType(String option)
	{
		String[] validTypes = getValidVisTypesForCurrentVisType();
		for (int i = 0; i < validTypes.length; i++) {
			if (option.equals(validTypes[i]))
				return true;
		}
		return false;
	}

	public String getCurrentVisType()
	{
		return currentVisType;
	}

	public void setCurrentVisType(String newType)
	{
		if (amongValidType(newType)) {
			currentVisType = newType;
			//fire propetychange should happen to subtype change
			//firePropertyChange(CHARTTYPESELECTION, null, currentVisType);
		}

	}

	public String getCurrentVisSubType()
	{
		return currentVisSubType;
	}

	public void setCurrentVisSubType(String newType)
	{
		if (amongValidSubType(newType)) {
			currentVisSubType = newType;
			firePropertyChange(CHARTTYPESELECTION, null, currentVisSubType);
		}

	}

	public String[] getValidVisTypesForCurrentVisType()
	{
		if (currentVisType.equals(XYCHART)) {
			return new String[]{XYLINECHART, XYSCATTERCHART, XYAREACHART, XYSTACKEDAREACHART};
		} else if (currentVisType.equals(CATEGORYCHART)) {
			return new String[]{CATEGORYBARCHART, CATEGORY3DBARCHART, STACKEDBARCHART, STACKEDBAR3DCHART};
		}


		//more to add
		return null;
	}

	public void setSelectedSet(int index)
	{
		selectedIndex = index;
		firePropertyChange(SETSELECTIONCHANGED, null, new Integer(index));
	}

	public DomeObjectSet getSelectedSet()
	{
		if (selectedIndex == -1)
			return null;
		else
			return (DomeObjectSet) setsList.get(selectedIndex);
	}

	public DomeObjectSet getSet(int index)
	{
		if(index>-1 && index<setsList.size()) return (DomeObjectSet) setsList.get(index);
		else return null;
	}

	public int getSelectedSetIndex()
	{
		return selectedIndex;

	}


	/**
	 * this is for saving chart property only, inner use only
	 * @param c     JFreeChart
	 */
	public void setChart(JFreeChart c)
	{
		chart = c;
		//doesn't fire propertychange bcz it's very complicated to match the cresponding sets back...

	}

	public JFreeChart getChart()
	{
		return chart;
	}


	/**
	 *  Listen to vector and matrix data change
	 */
	class dataPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
            //** for Vector
			if (e.getSource() instanceof DomeVectorData) {
			if (property.equals(DomeVectorData.SIZE)) {
				DomeVectorData d = (DomeVectorData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(INVALIDVECTORCHANGE, null, dobj);
			} else if (property.equals(DomeVectorData.ROWVECTOR)) {
				DomeVectorData d = (DomeVectorData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(INVALIDVECTORCHANGE, null, dobj);
			} else if (property.equals(DomeVectorData.DATA)) {
				DomeVectorData d = (DomeVectorData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(VECTORDATACHANGED, null, dobj);
			} else if (property.equals(DomeVectorData.VALUETYPE)) {
				DomeVectorData d = (DomeVectorData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(VECTORDATACHANGED, null, dobj);
			} else if (property.equals(DomeVectorData.FIXEDSIZE)) {
				//do nothing for now

			} else if (property.equals(DomeVectorData.ITEM)) {
				DomeVectorData d = (DomeVectorData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(VECTORDATACHANGED, null, dobj);
			} else if (property.equals(DomeVectorData.ITEMS)) {
				DomeVectorData d = (DomeVectorData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(VECTORDATACHANGED, null, dobj);
			} else if (property.equals(DomeVectorData.UNIT)) {
				DomeVectorData d = (DomeVectorData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(VECTORDATACHANGED, null, dobj);
			}
			}

			if (e.getSource() instanceof DomeMatrixData) {
			if (property.equals(DomeMatrixData.ITEM)) {
				DomeMatrixData d = (DomeMatrixData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(MATRIXDATACHANGED, null, dobj);
			} else if (property.equals(DomeMatrixData.DATA)) {
				DomeMatrixData d = (DomeMatrixData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(MATRIXDATACHANGED, null, dobj);
			} else 	if (property.equals(DomeMatrixData.COLSIZE)) {
				DomeMatrixData d = (DomeMatrixData) e.getSource();
				DomeObject dobj = (DomeObject) vector_parameter.get(d);
				firePropertyChange(INVALIDMATRIXCHANGE, null, dobj);
			}
			}
			//**todo: listen to other properties change of matrix
		}
	}

	class ModelObjectNameListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			if (property.equals("name")) {
				firePropertyChange(SETSCHANGED, null, setsList);
			}
		}
	}

	public PropertyChangeListener getValueShadowListener()
	{
		if (shadowListener == null)
			shadowListener = createValueShadowListener();
		return shadowListener;
	}

	public PropertyChangeListener getValueUnitShadowListener()
	{
		if (shadowListener == null)
			shadowListener = createValueUnitShadowListener();
		return shadowListener;
	}

	protected PropertyChangeListener createValueUnitShadowListener() {
	    return new VisualizationValueShadowListener();
	}

	protected PropertyChangeListener createValueShadowListener() {
	    return new VisualizationValueShadowListener();
	}

	public class VisualizationValueShadowListener implements PropertyChangeListener {
	    public void propertyChange(PropertyChangeEvent evt) {
	        String property = evt.getPropertyName();
	        Object obj = evt.getSource();
	        if (obj instanceof Visualization) {
	            if (property.equals(Visualization.LIST)) {
	                processListChange((DListEvent) evt.getNewValue());
	            } else {
		            processChange(evt);
	            }
	        }
	    }
	}

	protected void processListChange(DListEvent e) {
	    switch (e.getType()) {
	        case DListEvent.INTERVAL_ADDED:
	            addAndMap(e.getFirstIndex(), e.getItems());
	            break;
	        case DListEvent.INTERVAL_CHANGED:

	            break;
	        case DListEvent.INTERVAL_REMOVED:
	            deleteAndRemoveMapping(e.getIndices());
	            break;
	        case DListEvent.ITEMS_REMOVED:
	            deleteAndRemoveMapping(e.getIndices());
	            break;
	        case DListEvent.ITEMS_REPLACED:
	            break;
	    }

	}

	protected void processChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		Object obj = evt.getSource();
		if (property.equals(Visualization.CONSTANT)) {
			Boolean oldIsConstant = new Boolean(this.isConstant);
			this.setConstant(((Boolean)evt.getNewValue()).booleanValue());
			firePropertyChange(CONSTANT, oldIsConstant, new Boolean(this.isConstant));
		} else if (property.equals(Visualization.VERTICAL)) {
			Boolean oldVertical = new Boolean(this.vertical);
			this.setVertical(((Boolean)evt.getNewValue()).booleanValue());
			firePropertyChange(VERTICAL, oldVertical, new Boolean(this.vertical));
		} else if (property.equals(Visualization.CHARTTYPESELECTION)) {
			if (!this.getCurrentVisType().equals(((Visualization)obj).getCurrentVisType())) {
				this.setCurrentVisType(((Visualization)obj).getCurrentVisType());
			}
			if (!this.getCurrentVisSubType().equals(((Visualization)obj).getCurrentVisSubType())) {
				this.setCurrentVisSubType(((Visualization)obj).getCurrentVisSubType());
				firePropertyChange(CHARTTYPESELECTION, null, currentVisSubType);
			}
		} else if (property.equals(Visualization.SETSELECTIONCHANGED)) {
			    this.setSelectedSet(((Integer)evt.getNewValue()).intValue());
    			firePropertyChange(SETSELECTIONCHANGED, null, evt.getNewValue());
		} else if (property.equals(Visualization.ROWNAME)) {
                ArrayList newValue = (ArrayList) evt.getNewValue();
			    int setNO = ((Integer)newValue.get(0)).intValue();
			    int rowNO = ((Integer)newValue.get(1)).intValue();
			    String name = (String)newValue.get(2);
			    this.getSet(setNO).setSeriesAlias(name,rowNO);
		} else if (property.equals(Visualization.VECTORDATACHANGED)) {
			    firePropertyChange(VECTORDATACHANGED, null, null);
		} else if (property.equals(Visualization.MATRIXDATACHANGED)) {
			    firePropertyChange(MATRIXDATACHANGED, null, null);
		} else if (property.equals(Visualization.SETSCHANGED) || property.equals(Visualization.DATACHANGED)) {
			Visualization vis = (Visualization)obj;
			this.setsList.clear();
			if(vis.getSetsList().size() !=0) {
				for(int i = 0; i<vis.getSetsList().size();i++) {
					DomeObjectSet oriSet = (DomeObjectSet) vis.getSetsList().get(i);
					DomeObjectSet newSet = new DomeObjectSet(oriSet.getName(), null);
					newSet.useDomeName = oriSet.useDomeName;
					newSet.rowsOrColumns = oriSet.getRowsOrColumns();
					newSet.rowHorizontalIndex = oriSet.getRowHorizontalIndex();
					newSet.colHorizontalIndex = oriSet.getColHorizontalIndex();
					if (oriSet.getColCategoryColor() != null) {
						if (oriSet.getColCategoryColor().length != 0) {
							newSet.colCategoryColor = new int[oriSet.getColCategoryColor().length][4];
							for (int k = 0; k < oriSet.getColCategoryColor().length; k++) {
								for (int j = 0; j < 4; j++) {
									newSet.colCategoryColor[k][j] = oriSet.getColCategoryColor()[k][j];
								}
							}
						}
					}
					if (oriSet.getRowCategoryColor() != null) {
						if (oriSet.getRowCategoryColor().length != 0) {
							newSet.rowCategoryColor = new int[oriSet.getRowCategoryColor().length][4];
							for (int k = 0; k < oriSet.getRowCategoryColor().length; k++) {
								for (int j = 0; j < 4; j++) {
									newSet.rowCategoryColor[k][j] = oriSet.getRowCategoryColor()[k][j];
								}
							}
						}
					}


					for(int j = 0;j<oriSet.getData().size(); j++) {
						SetItem oriSetItem = (SetItem) oriSet.getData().get(j);
						int index = vis.getAvailableList().indexOf(oriSetItem.data);
						SetItem newSetItem = new SetItem((DomeObject)availableList.get(index),oriSetItem.aliasname,oriSetItem.Selected);
						if (oriSetItem.rowAliasName != null) {
						    if (oriSetItem.rowAliasName.length != 0) {
						        newSetItem.rowAliasName = new String[oriSetItem.rowAliasName.length];
						        for (int k = 0; k < oriSetItem.rowAliasName.length; k++) {
						            newSetItem.rowAliasName[k] = oriSetItem.rowAliasName[k];
						        }
						    }
						} else newSetItem.rowAliasName = null;

						if (oriSetItem.rowSelected != null) {
						    if (oriSetItem.rowSelected.length != 0) {
						        newSetItem.rowSelected = new boolean[oriSetItem.rowSelected.length];
						        for (int k = 0; k < oriSetItem.rowSelected.length; k++) {
						            newSetItem.rowSelected[k] = oriSetItem.rowSelected[k];
						        }
						    }
						} else newSetItem.rowSelected = null;

						if (oriSetItem.rowColor != null) {
							if (oriSetItem.rowColor.length != 0) {
								newSetItem.rowColor = new int[oriSetItem.rowColor.length][4];
								for (int k = 0; k < oriSetItem.rowColor.length; k++) {
									for (int n = 0; n < 4; n++) {
										newSetItem.rowColor[k][n] = oriSetItem.rowColor[k][n];
									}
								}
							}
						} else newSetItem.rowColor = null;

						if (oriSetItem.colAliasName != null) {
							if (oriSetItem.colAliasName.length != 0) {
								newSetItem.colAliasName = new String[oriSetItem.colAliasName.length];
								for (int k = 0; k < oriSetItem.colAliasName.length; k++) {
								    newSetItem.colAliasName[k] = oriSetItem.colAliasName[k];
								}
							}
						} else newSetItem.colAliasName = null;

						if (oriSetItem.colSelected != null) {
							if (oriSetItem.colSelected.length != 0) {
								newSetItem.colSelected = new boolean[oriSetItem.colSelected.length];
								for (int k = 0; k < oriSetItem.colSelected.length; k++) {
								    newSetItem.colSelected[k] = oriSetItem.colSelected[k];
								}
							}
						} else newSetItem.colSelected = null;

						if (oriSetItem.colColor != null) {
							if (oriSetItem.colColor.length != 0) {
								newSetItem.colColor = new int[oriSetItem.colColor.length][4];
								for (int k = 0; k < oriSetItem.colColor.length; k++) {
									for (int n = 0; n < 4; n++) {
										newSetItem.colColor[k][n] = oriSetItem.colColor[k][n];
									}
								}
							}
						} else newSetItem.colColor = null;

						/*if (((Parameter) newSetItem.data).getDataObjectForType("Matrix") != null){
							  for(int k = 0; k<newSetItem.rowAliasName.length; k++) {
								  newSetItem.rowAliasName[k] = oriSetItem.rowAliasName[k];
								  newSetItem.rowSelected[k] = oriSetItem.rowSelected[k];
							  }
						}*/
						newSet.datas.addElement(newSetItem);
					}

					this.setsList.addElement(newSet);
				}
			}

			firePropertyChange(SETSCHANGED, null, setsList);

		}

	}

	protected void add(int index, Parameter p) {
	    Parameter newObj = (Parameter) scope.newModelObject(p);
	    this.addToAvailableList(index, newObj);
	}

	protected void add(int index, List l) {
	    for (int i = 0; i < l.size(); i++) {
	        add(index + i, (Parameter) l.get(i));
	    }
	}

	protected void addAndMap(int index, Parameter p) {
	    Parameter newObj = (Parameter) scope.newModelObject(p);
	    this.addToAvailableList(index, newObj);
	    if (getObjectTopScope(newObj) instanceof ModelInterface || getObjectTopScope(newObj) instanceof InterfaceModelView) {
	        if (getObjectTopScope(p) instanceof Model) {
	            if(scope.getModel() instanceof DomeModel) ((AbstractDomeModel) scope.getModel()).getMappingManager().addInterfaceMapping(newObj, p);
		        if(scope.getModel() instanceof IntegrationProject) ((IntegrationProject) scope.getModel()).getMappingManager().addInterfaceMapping(newObj, p);
	        } else if (getObjectTopScope(p) instanceof ModelInterface || getObjectTopScope(p) instanceof InterfaceModelView) { //when interface is duplicated
		        if (p.getModel() instanceof DomeModel) {
			        ConnectionMappingManager mgr = ((DomeModel) p.getModel()).getMappingManager();
			        Collection mappings = mgr.getMappingsForParameter(p);
			        mgr.addMappings(newObj, mappings);
		        }
		        if (scope.getModel() instanceof IntegrationProject) {
			        ConnectionMappingManager mgr = ((IntegrationProject) p.getModel()).getMappingManager();
			        Collection mappings = mgr.getMappingsForParameter(p);
			        mgr.addMappings(newObj, mappings);
		        }
	        }
	    }
	}

	protected void addAndMap(int index, List l) {
	    for (int i = 0; i < l.size(); i++) {
	        addAndMap(index + i, (Parameter) l.get(i));
	    }
	}

	protected void deleteAndRemoveMapping(int[] indices) {
	    Arrays.sort(indices);
	    for (int i = indices.length - 1; i >= 0; i--) {
	        Parameter p = this.deleteElementAt(indices[i]);
	        if (p != null)
	            ((AbstractDomeModel) scope.getModel()).getMappingManager().removeAllMappings(p);
	    }
	}

	public Parameter deleteElementAt(int index) {
	    if (index < 0 || index > availableList.size()) {
	        return null;
	    } else {
	        Parameter p = (Parameter) availableList.remove(index);
	        if (p != null) p.delete(myDeletionListener);
	        return p;
	    }
	}

	public static ModelObjectScope getObjectTopScope(ModelObjectScope s) {
	    if (s instanceof ModelInterface || s instanceof Model) {
	        return s;
	    } else if (s instanceof Relation) {
	        return ((Relation) s).getScope();
	    } else if (s instanceof Subscription) {
	        return ((Subscription) s).getScope();
	    } else
	        return s;
	}

	public static ModelObjectScope getObjectTopScope(ModelObject obj) {
	    if (obj.getScope() instanceof SubscriptionInterface) {
	        return ((SubscriptionInterface) obj.getScope()).getSubscription().getScope();
	    }
	    while (!(obj.getScope() instanceof ModelInterface) && !(obj.getScope() instanceof Model) &&
	            !(obj.getScope() instanceof InterfaceModelView)) {
	        obj = (ModelObject) obj.getScope();
	    }
	    return obj.getScope();
	}

	class DataList extends DArrayList {
	    public DataList() {
	    }

	    public DataList(int initialCapacity) {
	        super(initialCapacity);
	    }

	    public DataList(Collection objs) {
	        super(objs);
	    }

	    public DataList(Object[] objs) {
	        super(objs);
	    }

	    // Hooks into add/remove operations on List
	    // assumption: list is not changed by add/remove Hooks!
	    // hooks for doing something before adding/removing objects from List
	    // returns true if add/remove should proceed, false otherwise
	    protected boolean addHookBefore(Object obj) {
	        return obj instanceof Parameter;
	    }

	    // hooks for doing something after adding/removing objects from List
	    protected void addHookAfter(Object obj) {
	        ((Parameter) obj).setContainerCollection(ConcreteVisualization.this);
	        ((Parameter) obj).addDeletionListener(myDeletionListener);
	    }

	    protected boolean removeHookBefore(Object obj) {
	        return true;
	    }

	    protected void removeHookAfter(Object obj) {
	        ((Parameter) obj).setContainerCollection(null);
	        ((Parameter) obj).removeDeletionListener(myDeletionListener);
	    }
	}

	class AvailableSeriesDeletionListener implements DeletionListener {
	    public void objectDeleted(DeletionEvent e) {
	        availableList.remove(e.getSource());
	        //*firePropertyChange(SIZE, null, new Integer(getSize()));
	    }
	}

	class DataListListener implements DListListener {
	    public void intervalChanged(DListEvent e) {
	        firePropertyChange(Visualization.LIST, null, e);
	    }

	    public void intervalAdded(DListEvent e) {
	        firePropertyChange(Visualization.LIST, null, e);
	    }

	    public void intervalRemoved(DListEvent e) {
	        firePropertyChange(Visualization.LIST, null, e);
	    }

	    public void itemsRemoved(DListEvent e) {
	        firePropertyChange(Visualization.LIST, null, e);
	    }

	    public void itemsReplaced(DListEvent e) {
	        firePropertyChange(Visualization.LIST, null, e);
	    }
	}

	public void deleteContentsFromScope() {
	    scope.deleteModelObjects(availableList);
	}

	public Collection getContents() {
        return Collections.unmodifiableCollection(availableList);
    }
}