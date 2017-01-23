// DomeVectorData.java
// Version history:
//  0.1
//  0.2 4/14/02
//  0.3 5/10/02: add javabean support
//         policy:
//          1.for now, if a row is changed, fire single element(item) change, if multiple rows changed,fire items change.
//          2.if size change, fire size change.
//  0.4 5/21/02: add a constructor directly takes in all parameters
//

package mit.cadlab.dome3.objectmodel.dataobject;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.util.Position;
import mit.cadlab.dome3.objectmodel.util.Positions;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.util.units.Quantity;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class DomeVectorData extends AbstractDataObject
        implements DomeVector
{

	protected Vector data = new Vector();

	protected Unit unit = Quantity.NO_UNIT;
	//protected String unit = _unit.getId();
	protected boolean isRowVector = false;
	protected boolean isFixedSize = false;
	protected Number initialValue = new Double(0.0); // determines type
    private Hashtable DUMMY = new Hashtable(0);

	/**
	 *  contructor
	 */
	public DomeVectorData()
	{
	}

	public DomeVectorData(DomeVector vec)
	{
		if (vec == null)
			throw new IllegalArgumentException("DomeVector - null parameter");

		DomeVectorData v = (DomeVectorData) vec;
		this.data = (Vector) v.data.clone();

		this.unit = (Unit) v.unit.clone();
		this.isRowVector = v.isRowVector;
		this.initialValue = v.initialValue;
		this.isFixedSize = v.isFixedSize;
	}

	public DomeVectorData(Element xmlElement)
	{
		super(xmlElement);

		// make this element the root
		XMLUtils.makeRootElement(xmlElement);

		// load base values
		String unitname = xmlElement.elementText("unit");
		if (unitname == null || unitname.length() == 0)
			unit = Quantity.NO_UNIT;
		else
			unit = new Unit(unitname);
		Boolean rowVector = Boolean.valueOf(xmlElement.elementText("rowVector"));
		isRowVector = (rowVector == Boolean.TRUE ? true : false);
		Element initValueElement = (Element) xmlElement.selectSingleNode("initialValue");
		if (initValueElement != null) {
			if (initValueElement.attributeValue("type").equals("real"))
				initialValue = new Double(initValueElement.getText());
			else
				initialValue = new Integer(initValueElement.getText());
		}
		Boolean fixedSize = Boolean.valueOf(xmlElement.elementText("fixedSize"));
		isFixedSize = (fixedSize == Boolean.TRUE ? true : false);

		// load data
		Element dataElement = (Element) xmlElement.selectSingleNode("data");
		String type = dataElement.attributeValue("type");
		if (dataElement != null) {
			String data = dataElement.getText();
			if (data != null && data.length() > 0) {
				String[] values = data.split(",");
				try {
					for (int i = 0; i < values.length; i++) {
						if (type.equals("real"))
							this.data.addElement(new Double(values[i]));
						else
							this.data.addElement(new Integer(values[i]));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public DomeVectorData(int size)
	{
		data = new Vector();
		setSize(size);
	}

    // original constructor. clone the vector
	public DomeVectorData(Vector v, Unit u, boolean isRow, boolean isFix, Number initialV)
	{
		this(v, u, isRow, isFix, initialV, false);
	}

    // have a choice whether to clone the vector
    public DomeVectorData(Vector v, Unit u, boolean isRow, boolean isFix, Number initialV, boolean isExactVector) {
        data = isExactVector ? v : (Vector) v.clone();
        unit = u;
        isRowVector = isRow;
        isFixedSize = isFix;
        initialValue = initialV;
        //if the initialvalue has differient type with data vector, make them coherent
        changeClass(initialValue instanceof Integer);
    }

	protected PropertyChangeListener createValueShadowListener()
	{
		return new VectorValueShadowListener();
	}

	protected PropertyChangeListener createValueUnitShadowListener()
	{
		return new VectorValueUnitShadowListener();
	}

	public boolean isCompatibleType(DataObject newObj)
	{
		return (newObj instanceof DomeVectorData);
	}

	public DomeVector getDomeVector()
	{
		return this;
	}

	public DataObject duplicate()
	{
		return new DomeVectorData(this);
	}

	public String toString()
	{
		return "DomeVector: " + data.toString() + " " + unit.toString();
	}

	protected TypeInfo getTypeInfo()
	{
		return DomeVector.TYPE_INFO;
	}

	public List getValues()
	{
        return data;
	}

    /**
     * used for computation math functions such as sine
     * it is unit-dependent. for example, if the unit is pi, return the real value multiplied by 3.14..
     * if the unit is percent, return the real value multiplied by 0.01
     */
    public List getComputationalValues() {
        return unit.isConstantUnit() ? getDataConstantUnit() : data;
    }

	public Number[] getValuesArray()
	{
		Number[] values = new Number[data.size()];
		for (int i = 0; i < data.size(); i++) {
			values[i] = new Double(((Number) data.get(i)).doubleValue());
		}
		return values;
	}

    /**
     * used for computation math functions such as sine
     * it is unit-dependent. for example, if the unit is pi, return the real value multiplied by 3.14..
     * if the unit is percent, return the real value multiplied by 0.01
     */
    public Number[] getComputationalValuesArray() {
        double factor = 1;
        if (unit.isConstantUnit())
            factor = unit.getConstantUnitFactor();
        Number[] values = new Number[data.size()];
        for (int i = 0; i < data.size(); i++) {
            values[i] = new Double(((Number) data.get(i)).doubleValue() * factor);
        }
        return values;
    }

	public void setValues (double[] values)
	{
		if (values.length > 0)
		{
			Vector newValues = new Vector ();
			for (int i = 0; i < values.length; i++) {
                newValues.add(new Double(values[i]));
			}
			setValues (newValues);
		}
	}


	public void setValues(List values)
	{
		if (values.size() > 0) {
			//coming from server
			Object vals = values.get(0);
			if(vals instanceof Hashtable) {
				setData((Hashtable)vals, true);
			}
			else {
				if (!(values instanceof Vector)) { // data is a Vector
					values = (List) values.get(0);
				}
				else { // see if values needs to be unwrapped
					Object firstItem = values.get(0);
					if (firstItem instanceof List) {
						values = (List)firstItem;
					} else if (!(firstItem instanceof Number)) {
						throw new IllegalArgumentException("DomeVectorData::setValues - illegal values: " + values);
					}
				}
				data.clear();
				data.addAll(values);
				changeClass(initialValue instanceof Integer); // enforces current type
				firePropertyChange(DATA, null, data);
				fireValueChange();
			}
		}
	}

	public void setValues(List values, boolean matchFirstType)
	{
		if (values.size() > 0) {
			if (values.get(0) instanceof Integer)
				setValueType("integer");
			else if (values.get(0) instanceof Number)
				setValueType("real");
			setValues(values);
		}
	}

	public void setValues(DataObject newObj)
	{
		if (newObj instanceof DomeVectorData)
			setData((DomeVectorData) newObj);
	}

	//inherited from domeVector interface
	public String getValueType()
	{
		if (initialValue instanceof Integer) return "integer";
		return "real";
	}

	public void setValueType(String type)
	{
		if (type.equals("real")) {
			if (initialValue instanceof Integer)
				changeClass(false);
		} else if (type.equals("integer")) {
			if (initialValue instanceof Double)
				changeClass(true);
		}
		firePropertyChange(VALUETYPE, null, type);
		firePropertyChange(DATA, null, data);
	}

	public int getSize()
	{
		return data.size();
	}

    public void setSize(int size)
    {
        setSize(size,new Double(0));
    }

    public void addDoubleItem(double num) {
        int index = getSize() + 1;
        setSize(index);
        setItem(index-1, new Double(num));
    }

	public void setSize(int size, Object valSetter)
	{
        double initVal;
        if (valSetter instanceof RealData) {
            RealData real = (RealData) valSetter;
            try { // see if units are compatible
                initVal = getUnit().convertFrom(real.getValue(), real.getUnit());
            } catch (Exception e) {
                throw new IllegalArgumentException("DomeMatrixData.setSize: unit of " + real + " is not compatible" +
                        "with that of the matrix");
            }
        } else if (valSetter instanceof IntegerData) {
            IntegerData integer = (IntegerData) valSetter;
            try { // see if units are compatible
                initVal = getUnit().convertFrom(integer.getValue(), integer.getUnit());
            } catch (Exception e) {
                throw new IllegalArgumentException("DomeMatrixData.setSize: unit of " + integer + " is not compatible" +
                        "with that of the matrix");
            }
        } else if (valSetter instanceof Integer) {
            initVal = ((Integer) valSetter).intValue();
        } else if (valSetter instanceof Number) {
            initVal = ((Double) valSetter).doubleValue();
        } else {
            throw new IllegalArgumentException("DomeVectorData.setSize: Invalid input: " + valSetter);
        }
		setSize(size, new Double(initVal));
	}

	public void setSize(int size, Number value)
	{
		int currentsize = getSize();
		if (currentsize == size) return;
		if (currentsize < size)  //append
		{
			addItems(currentsize, size - currentsize, value);
		} else //truncate from end
		{
			removeRange(size, currentsize);
		}

		firePropertyChange(SIZE, null, new Integer(data.size()));
	}

    public List getData() {
        return Collections.unmodifiableList(data);
    }

    /**
     * used for computation math functions such as sine
     * it is unit-dependent. for example, if the unit is pi, return the real value multiplied by 3.14..
     * if the unit is percent, return the real value multiplied by 0.01
     */
	public List getComputationalData()
	{
		return unit.isConstantUnit() ? Collections.unmodifiableList(getDataConstantUnit()) : Collections.unmodifiableList(data);
	}

    private Vector getDataConstantUnit() {
        Vector data_ = (Vector) data.clone();
        int row = data_.size();
        double factor = unit.getConstantUnitFactor();
        for (int i = 0; i < row; i++) {
            data_.setElementAt(new Double(((Number) data_.get(i)).doubleValue() * factor), i);
        }
        return data_;
    }

	public void setData(DomeVectorData v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeVector - null parameter");
		data = convertToThisUnit(v.data, v.unit);
        // unit is not changed
		isRowVector = v.isRowVector;
		initialValue = v.initialValue;
		isFixedSize = v.isFixedSize;

		firePropertyChange(DATA, null, data);//pass the data vector as a whole
		firePropertyChange(SIZE, null, new Integer(data.size()));
		firePropertyChange(FIXEDSIZE, null, new Boolean(isFixedSize));
		firePropertyChange(UNIT, null, unit);
		firePropertyChange(ROWVECTOR, null, new Boolean(isRowVector));
		fireValueChange();
	}

	public boolean isFixedSize()
	{
		return this.isFixedSize;
	}

	public void setFixedSize(boolean yesOrno)
	{
		Boolean oldProp = new Boolean(isFixedSize);
		this.isFixedSize = yesOrno;
		firePropertyChange(FIXEDSIZE, oldProp, new Boolean(yesOrno));
	}

	public Number getItem(int index)
	{
		return new Double(((Number) data.get(index)).doubleValue());
	}

    public int getFirstIndexForValue(Number val) {
        for (int i = 0; i < data.size(); i++) {
            if (((Number) data.get(i)).doubleValue() == val.doubleValue())
                return i;
        }
        return -1;
    }

    /**
     * used for computation math functions such as sine
     * it is unit-dependent. for example, if the unit is pi, return the real value multiplied by 3.14..
     * if the unit is percent, return the real value multiplied by 0.01
     */
    public Number getComputationalItem(int index) {
        double factor = 1;
        if (unit.isConstantUnit())
            factor = unit.getConstantUnitFactor();
        return new Double(((Number) data.get(index)).doubleValue() * factor);
    }

     /**
     * used to populate the vector with incremental data
     */
    public void populateIncrData() {
         int size = getSize();
         if (size>=2) {
             double incr = getItem(1).doubleValue() - getItem(0).doubleValue();
             for (int i = 2; i < size; i++) {
                 setItem(i, new Double(getItem(i-1).doubleValue() + incr));
             }
         }
    }

	public void setItem(int index, Number n)
	{
		if(getItem(index).equals(n)) {
			return;
		}
		if (n.getClass() != initialValue.getClass()) {
			if (initialValue instanceof Double)
				n = new Double(n.doubleValue());
			else if (initialValue instanceof Integer)
				n = new Integer(n.intValue());
		}
		setInitialValue(n);
		data.set(index, this.initialValue);

		Position p;
		if (isRowVector)
			p = new Position(0, index);
		else
			p = new Position(index, 0);
		firePropertyChange(ITEM, null, p);
		fireValueChange();
	}

	public Unit getUnit()
	{
        return unit;
	}

	public void setUnit(Unit u)
	{
		if (u == null) return;

		Unit old = this.unit;
		this.unit = (Unit) u.clone();

		firePropertyChange(UNIT, old, u);
	}

	public boolean isRowVector()
	{
		return isRowVector;
	}

    public Boolean isRow() {
        return new Boolean(isRowVector && getSize()!=0);
    }

    public Boolean isCol() {
        return new Boolean(!isRowVector && getSize() != 0);
    }

	public void setRowVector(boolean rowVector)
	{
		isRowVector = rowVector;

		firePropertyChange(ROWVECTOR, null, new Boolean(rowVector));
	}

	public void changeClass(boolean isInteger)
	{
		if (isInteger) {
			initialValue = new Integer(initialValue.intValue());
			for (int i = 0; i < data.size(); i++)
				data.set(i, new Integer(((Number) data.get(i)).intValue()));
		} else { // must be Double
			initialValue = new Double(initialValue.doubleValue());
			for (int i = 0; i < data.size(); i++)
				data.set(i, new Double(((Number) data.get(i)).doubleValue()));
		}
	}

	public void setInitialValue(Number n)
	{
		if (n == null) return;
		if (initialValue.getClass() != n.getClass()) { // wrong type...convert
			if (initialValue instanceof Integer) {
				initialValue = new Integer(n.intValue());
			} else { // must be Double
				initialValue = new Double(n.doubleValue());
			}
		} else { // correct type
			initialValue = n;
		}
	}

	public Number getInitialValue()
	{
		return initialValue;
	}

	public boolean addItems(int index, int howMany, Number newInitialValue)
	{
		if (isFixedSize) {
			debug("DomeVector: Warning! dataVector size is fixed, can't add...");
			return false;
		}
		if (index < 0 || index > data.size())
			return false;
		setInitialValue(newInitialValue);
		for (int i = 0; i < howMany; ++i)
			data.add(index, this.initialValue);

		firePropertyChange(ITEMS, null, new Positions(Positions.ADD, index, howMany));
		firePropertyChange(SIZE, null, new Integer(data.size()));
		fireValueChange();

		return true;
	}

	public void removeItems(int[] indices)
	{
		if (isFixedSize) {
			debug("DomeVector: Warning! dataVector size is fixed, can't delete...");
			return;
		}
		// assume indices are all good
		Arrays.sort(indices);
		for (int i = indices.length - 1; i >= 0; --i)
			data.remove(indices[i]);


		firePropertyChange(ITEMS, null, new Positions(Positions.DEL, indices.length));
		firePropertyChange(SIZE, null, new Integer(data.size()));
		fireValueChange();
	}


	public void fillItems(int[] indices, Number n)
	{
		// assume indices are all good
		setInitialValue(n);
		for (int i = 0; i < indices.length; ++i)
			data.set(indices[i], initialValue);

		firePropertyChange(ITEMS, null, new Positions(Positions.UPDATE, indices.length));
		fireValueChange();
	}

	//startIndex is inclusive and endIndex is exclusive

	public void removeRange(int startIndex, int endIndex)
	{
		if (isFixedSize) {
			debug("Warning! dataVector size is fixed, can't delete...");
			return;
		}
		//assume the index para are good
		for (int i = endIndex - 1; i >= startIndex; i--)
			data.removeElementAt(i);

		firePropertyChange(ITEMS, null, new Positions(Positions.DEL, startIndex, endIndex - startIndex));
		firePropertyChange(SIZE, null, new Integer(data.size()));
		fireValueChange();
	}

	public void fireValueChange()
	{
		firePropertyChange(VALUE, DUMMY, getValuesForXmlRpcUse());
	}

	public Object getValuesForXmlRpcUse()
	{
		Hashtable t = new Hashtable();
		//put data, and unit inside
		t.put(DomeVector.DATA, data);
		t.put(DomeVector.UNIT, unit.toString());
		return t;
	}


	//for debug

	private void debug(String msg)
	{
		boolean debug = false;
		if (debug)
			System.out.println("DomeVectorData: " + msg);
	}


	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		boolean isReal = (initialValue.getClass() == Double.class);

		// store base values
		xml.addElement("unit").setText(unit.toString());
		xml.addElement("rowVector").setText(Boolean.toString(isRowVector));
		Element initValElement = xml.addElement("initialValue");
		initValElement.addAttribute("type", (isReal ? "real" : "integer"));
		initValElement.setText(initialValue.toString());
		xml.addElement("fixedSize").setText(Boolean.toString(isFixedSize));

		// store data
		Element dataElement = xml.addElement("data");
		dataElement.addAttribute("type", (isReal ? "real" : "integer"));
		if (data.size() > 0) {
			StringBuffer text = new StringBuffer();
			Object[] values = data.toArray();
			int length = values.length;
			for (int i = 0; i < length - 1; i++) {
				if(isReal)
					text.append(values[i]);
				else
					text.append(((Number)values[i]).intValue());    //kludge to save integers TODO fix it
				text.append(",");
			}
			if(isReal)
				text.append(values[length - 1]);
			else
				text.append(((Number) values[length - 1]).intValue());  //kludge to save integers TODO fix it
			dataElement.setText(text.toString());
		}

		return xml;
	}


	protected class VectorValueShadowListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			Object newObj = evt.getNewValue();
			 if (newObj instanceof Hashtable) {   //run mode, from client to server
				if (property.equals(VALUE)) {
					Vector v = (Vector)((Hashtable) newObj).get(DATA);
					Object firstObj = v.get(0);
					if(firstObj instanceof Hashtable)
						setData((Hashtable) firstObj, true);
					else
						setData((Hashtable) newObj, true);
				}
			}
			else if (obj instanceof DomeVectorData) {     //build mode
                if(property.equals(DomeVector.SIZE)) {
                    DomeVectorData vec = (DomeVectorData) obj;
                    setInitialValue(vec.getInitialValue());
                    setSize(vec.getSize(), initialValue);
                }
                else if (property.equals(DomeVector.ROWVECTOR)) {
	                if(isRowVector != ((DomeVectorData) obj).isRowVector()) {
						isRowVector = ((DomeVectorData) obj).isRowVector();
						firePropertyChange(ROWVECTOR, null, new Boolean(isRowVector));
	                }
                }
                else if(property.equals(DomeVector.VALUETYPE)) {
	                DomeVectorData.this.setValueType((String)newObj);
                }
				else if (property.equals(DomeVector.ITEMS)) {
 					setData((DomeVectorData) obj);
				}
				else if (property.equals(DomeVector.ITEM)) {
					Position p = (Position) evt.getNewValue();
					if (isRowVector) {
						Number n = ((DomeVectorData) obj).getItem(p.col);
						Number val = new Double(unit.convertFrom(n.doubleValue(), ((DomeVectorData) obj).getUnit()));
						setItem(p.col, val);
					}
					else  {
						Number n = ((DomeVectorData) obj).getItem(p.row);
						Number val = new Double(unit.convertFrom(n.doubleValue(), ((DomeVectorData) obj).getUnit()));
						setItem(p.row, val);
					}
				}
				else if(property.equals(DomeVector.FIXEDSIZE))  {
					if (isFixedSize != ((DomeVectorData) obj).isFixedSize()) {
						isFixedSize = ((DomeVectorData) obj).isFixedSize();
						firePropertyChange(FIXEDSIZE, null, new Boolean(isFixedSize));
					}
				}
			}
		}
	}

	protected class VectorValueUnitShadowListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			Object newObj = evt.getNewValue();
			if (newObj instanceof Hashtable) {
				if (property.equals(VALUE)) {
					Vector v = (Vector) ((Hashtable) newObj).get(DATA);
					Object firstObj = v.get(0);
					if (firstObj instanceof Hashtable)
						setData((Hashtable) firstObj, false);
					else
						setData((Hashtable) newObj, false);
				}
				if (property.equals(DomeVector.UNIT)) {
					setUnit((Unit) ((Hashtable) newObj).get(DomeVector.UNIT));
				}
			}
			else if (obj instanceof DomeVectorData) {
                if (property.equals(DomeVector.SIZE)) {
                    DomeVectorData vec = (DomeVectorData) obj;
                    setInitialValue(vec.getInitialValue());
                    setSize(vec.getSize(), initialValue);
                }
                else if (property.equals(DomeVector.ROWVECTOR)) {
	                if (isRowVector != ((DomeVectorData) obj).isRowVector()) {
                        isRowVector = ((DomeVectorData) obj).isRowVector();
	                    firePropertyChange(ROWVECTOR, null, new Boolean(isRowVector));
	                }
                }
                else if (property.equals(DomeVector.VALUETYPE)) {
	                DomeVectorData.this.setValueType((String) newObj);
                }
                else if (property.equals(DomeVector.ITEMS)) {
	                setData((DomeVectorData) obj);
                }
                else if (property.equals(DomeVector.ITEM)) {
	                Position p = (Position) evt.getNewValue();
	                if (isRowVector) {
		                Number n = ((DomeVectorData) obj).getItem(p.col);
		                setItem(p.col, n);
	                } else {
		                Number n = ((DomeVectorData) obj).getItem(p.row);
		                setItem(p.row, n);
	                }
                }
				else if (property.equals(DomeVector.UNIT)) {
					setUnit(((DomeVectorData) obj).getUnit());
				}
				else if (property.equals(DomeVector.FIXEDSIZE)) {
					if (isFixedSize != ((DomeVectorData) obj).isFixedSize()) {
						isFixedSize = ((DomeVectorData) obj).isFixedSize();
						firePropertyChange(FIXEDSIZE, null, new Boolean(isFixedSize));
					}
				}
			}
		}
	}

	//getting from xmlrpc
	public void setData(Hashtable table, boolean changeValueOnly)
	{
		boolean unitChanged = true;
		Vector oldData = (Vector) data.clone();

		Vector d = (Vector) table.get(DomeVector.DATA);
		String unitS = (String) table.get(DomeVector.UNIT);
		Unit newUnit = new Unit(unitS);
		if (newUnit.equals(unit)) unitChanged = false;

		if (changeValueOnly) {
			if (unit.equals(Quantity.NO_UNIT) || (!unitChanged))
				data = convertToThisType(d);
			else
				data = convertToThisUnit(d, newUnit);
		}
		else {
			unit = newUnit;
			data = convertToThisType(d);
		}

		if ((!oldData.equals(data)) || unitChanged) {
			firePropertyChange(DATA, null, this);//pass the data vector as a whole
		}
		fireValueChange();
	}

	protected Vector convertToThisUnit(Vector v, Unit vUnit)
	{
		Vector result = new Vector();
		for (int j = 0; j < v.size(); j++) {
			if (v.get(j) instanceof Number) {
				double num = ((Number) v.get(j)).doubleValue();
				double num_result = unit.convertFrom(num, vUnit);
				if (initialValue instanceof Integer)
					result.add(new Integer((int)num_result));
				else
					result.add(new Double(num_result));
			}
		}
		return result;
	}

	protected Vector convertToThisType(Vector v) {
		Vector result = new Vector();
		Number number;
		for (int i = 0; i < v.size(); i++) {
			number = (Number) v.elementAt(i);
			if (initialValue instanceof Integer)
				result.add(new Integer(number.intValue()));
			else
				result.add(new Double(number.doubleValue()));
		}
		return result;
	}

	public Object __add__(Object obj)
	{
		if (obj instanceof DomeVectorData) {
			DomeVectorData that = (DomeVectorData) obj;

			if (this.getSize() != that.getSize())
				throw new IllegalArgumentException("Incompatible size: Can't add " + this + " to " + obj);

			List l1 = this.getValues();
			List l2 = that.getValues();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			Object o = l2.get(0);   // get the first item to check object type

			DomeVectorData result = new DomeVectorData(that.getSize());

			if (o instanceof Number) {
				double v1;
				double converted_v2;

				for (int i = 0; i < l2.size(); i++) {
					v1 = ((Number) l1.get(i)).doubleValue();
					//convert value of "that" into unit of "this"
					converted_v2 = u1.convertFrom(((Number) l2.get(i)).doubleValue(), u2);
					result.setItem(i, new Double(v1 + converted_v2));
				}
			} else {
				throw new IllegalArgumentException("Incompatible size: Can't add " + this + " to " + obj);
			}

			// use unit of LHS
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
			DomeMatrixData this_matrix = new DomeMatrixData(this);
			return that.__add__(this_matrix);
		} else if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			List l1 = this.getValues();
			int v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double v1;
			double converted_v2;
			DomeVectorData result = new DomeVectorData(this.getSize());

			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				//convert value of "that" into unit of "this"
				converted_v2 = u1.convertFrom(v2, u2);
				result.setItem(i, new Double(v1 + converted_v2));
			}

			// use unit of Vector
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			List l1 = this.getValues();
			double v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double v1;
			double converted_v2;
			DomeVectorData result = new DomeVectorData(this.getSize());

			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				//convert value of "that" into unit of "this"
				converted_v2 = u1.convertFrom(v2, u2);
				result.setItem(i, new Double(v1 + converted_v2));
			}

			// use unit of Vector
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			List l1 = this.getValues();
			int v2 = that.intValue();
			Unit u1 = this.getUnit();

			double v1;
			DomeVectorData result = new DomeVectorData(this.getSize());

			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				result.setItem(i, new Double(v1 + v2));
			}

			// use unit of Vector
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			List l1 = this.getValues();
            double v2 = that.doubleValue();
			Unit u1 = this.getUnit();

			double v1;
			DomeVectorData result = new DomeVectorData(this.getSize());

			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				result.setItem(i, new Double(v1 + v2));
			}

			// use unit of Vector
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj.getClass().isArray()) {
			try {
				List l1 = this.getValues();
				Unit u1 = this.getUnit();

				if (this.getSize() != Array.getLength(obj))
					throw new IllegalArgumentException("Incompatible size: Can't add " + this + " to " + obj);

				double v1,v2;
				DomeVectorData result = new DomeVectorData(this.getSize());

				for (int i = 0; i < l1.size(); i++) {
					v1 = ((Number) l1.get(i)).doubleValue();
					v2 = ((Number) Array.get(obj, i)).doubleValue();

					result.setItem(i, new Double(v1 + v2));
				}

				// use unit of Vector
				result.setUnit((Unit) u1.clone());

				return result;

			} catch (IllegalArgumentException e) { // should never happen if we are calling
				System.err.println(e);             // method correctly
				throw new IllegalArgumentException("Can't add " + this + " to " + obj);
			} catch (ArrayIndexOutOfBoundsException ex) { // array has zero length
				System.err.println("array has zero length");
				throw new IllegalArgumentException("Can't add " + this + " to " + obj);
			}
		} else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
	}

	public Object __sub__(Object obj)
	{
		if (obj instanceof DomeVectorData) {
			DomeVectorData that = (DomeVectorData) obj;

			if (this.getSize() != that.getSize())
				throw new IllegalArgumentException("Incompatible size: Can't substract " + this + " to " + obj);

			List l1 = this.getValues();
			List l2 = that.getValues();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			Object o = l2.get(0);   // get the first item to check object type

			DomeVectorData result = new DomeVectorData(that.getSize());

			if (o instanceof Number) {
				double v1;
				double converted_v2;

				for (int i = 0; i < l2.size(); i++) {
					v1 = ((Number) l1.get(i)).doubleValue();
					//convert value of "that" into unit of "this"
					converted_v2 = u1.convertFrom(((Number) l2.get(i)).doubleValue(), u2);
					result.setItem(i, new Double(v1 - converted_v2));
				}
			} else {
				throw new IllegalArgumentException("Incompatible size: Can't substract " + this + " to " + obj);
			}

			// use unit of LHS
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
			DomeMatrixData this_matrix = new DomeMatrixData(this);
			return this_matrix.__sub__(that);
		} else if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			List l1 = this.getValues();
			int v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double v1;
			double converted_v2;
			DomeVectorData result = new DomeVectorData(this.getSize());

			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				//convert value of "that" into unit of "this"
				converted_v2 = u1.convertFrom(v2, u2);
				result.setItem(i, new Double(v1 - converted_v2));
			}

			// use unit of Vector
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			List l1 = this.getValues();
			double v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double v1;
			double converted_v2;
			DomeVectorData result = new DomeVectorData(this.getSize());

			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				//convert value of "that" into unit of "this"
				converted_v2 = u1.convertFrom(v2, u2);
				result.setItem(i, new Double(v1 - converted_v2));
			}

			// use unit of Vector
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			List l1 = this.getValues();
			int v2 = that.intValue();
			Unit u1 = this.getUnit();

			double v1;
			DomeVectorData result = new DomeVectorData(this.getSize());

			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				result.setItem(i, new Double(v1 - v2));
			}

			// use unit of Vector
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			List l1 = this.getValues();
            double v2 = that.doubleValue();
			Unit u1 = this.getUnit();

			double v1;
			DomeVectorData result = new DomeVectorData(this.getSize());

			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				result.setItem(i, new Double(v1 - v2));
			}

			// use unit of Vector
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj.getClass().isArray()) {
			try {
				List l1 = this.getValues();
				Unit u1 = this.getUnit();

				if (this.getSize() != Array.getLength(obj))
					throw new IllegalArgumentException("Incompatible size: Can't substract " + this + " to " + obj);

				double v1,v2;
				DomeVectorData result = new DomeVectorData(this.getSize());

				for (int i = 0; i < l1.size(); i++) {
					v1 = ((Number) l1.get(i)).doubleValue();
					v2 = ((Number) Array.get(obj, i)).doubleValue();

					result.setItem(i, new Double(v1 - v2));
				}

				// use unit of Vector
				result.setUnit((Unit) u1.clone());

				return result;

			} catch (IllegalArgumentException e) { // should never happen if we are calling
				System.err.println(e);             // method correctly
				throw new IllegalArgumentException("Can't substract " + this + " to " + obj);
			} catch (ArrayIndexOutOfBoundsException ex) { // array has zero length
				System.err.println("array has zero length");
				throw new IllegalArgumentException("Can't substract " + this + " to " + obj);
			}
		} else {
			throw new IllegalArgumentException("Can't substract " + this + " to " + obj);
		}
	}

	public Object __neg__()
	{
		List l1 = this.getValues();
		Unit u1 = this.getUnit();
		DomeVectorData result = new DomeVectorData(this.getSize());

		double v1;
		for (int i = 0; i < l1.size(); i++) {
			v1 = ((Number) l1.get(i)).doubleValue();
			result.setItem(i, new Double(-v1));
		}
		// use unit of Vector
		result.setUnit((Unit) u1.clone());

		return result;
	}


	public Object __mul__(Object obj)
	{
		if (obj instanceof DomeVectorData) {
			DomeVectorData that = (DomeVectorData) obj;

			List l1 = this.getValues();
			List l2 = that.getValues();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			Object o = l2.get(0);   // get the first item to check object type
			if (o instanceof Number) {
				double v1;
				double converted_v2;

				// outer product (col vec * row vec)
				if (!this.isRowVector() && that.isRowVector()) {
					// result is a matrix
					DomeMatrixData result = new DomeMatrixData(this.getSize(), that.getSize());

					Unit u3 = null;
					for (int i = 0; i < l1.size(); i++) {
						for (int j = 0; j < l2.size(); j++) {
							v1 = ((Number) l1.get(i)).doubleValue();

							try { //convert value of "that" into unit of "this" if both are the same type
								converted_v2 = u1.convertFrom(((Number) l2.get(j)).doubleValue(), u2);
								//convert the units themselves -- only once!
								if (i == 0 && j == 0) {
									try {
										u3 = new Unit("S" + u1.toString());
									} catch (Exception e) {
										if (u1.equals(Quantity.NO_UNIT))
											u3 = (Unit) u1.clone();
										else
											u3 = ((Unit) u1.clone()).mul(u1);
									}
								}
							} catch (Exception e) { //otherwise create a compound unit
								converted_v2 = ((Number) l2.get(j)).doubleValue();
								if (i == 0 && j == 0) {
									if (u1.equals(Quantity.NO_UNIT))
										u3 = (Unit) u2.clone();
									else if (u2.equals(Quantity.NO_UNIT))
										u3 = (Unit) u1.clone();
									else {
										Unit u1Non = u1;
										Unit u2Non = u2;
										if (u1.getFunction() != null) {
											u1Non = u1.getNonFunctionVersion();
											v1 = u1.isDegTempUnit() ? v1 : u1Non.convertFrom(v1, u1);
										}
										if (u2.getFunction() != null) {
											u2Non = u2.getNonFunctionVersion();
											converted_v2 = u2.isDegTempUnit() ? converted_v2 : u2Non.convertFrom(converted_v2, u2);
										}
										u3 = ((Unit) u1Non.clone()).mul(u2Non);
									}
								}
							}
							result.setItem(i, j, new Double(v1 * converted_v2));
						}
					}
					result.setUnit(u3);
					return result;
				} else { // inner product and simple multiplication
					if (this.getSize() != that.getSize())
						throw new IllegalArgumentException("Incompatible size: Can't multiply " + this + " to " + obj);

					Unit u3 = null;
					RealData result = new RealData();
					for (int i = 0; i < l1.size(); i++) {
						v1 = ((Number) l1.get(i)).doubleValue();

						try { //convert value of "that" into unit of "this" if both are the same type
							converted_v2 = u1.convertFrom(((Number) l2.get(i)).doubleValue(), u2);
							//convert the units themselves -- only once!
							if (i == 0) {
								try {
									u3 = new Unit("S" + u1.toString());
								} catch (Exception e) {
									if (u1.equals(Quantity.NO_UNIT))
										u3 = (Unit) u1.clone();
									else
										u3 = ((Unit) u1.clone()).mul(u1);
								}
							}
						} catch (Exception e) { //otherwise keep the old value
							converted_v2 = ((Number) l2.get(i)).doubleValue();
							if (i == 0) {
								if (u1.equals(Quantity.NO_UNIT))
									u3 = (Unit) u2.clone();
								else if (u2.equals(Quantity.NO_UNIT))
									u3 = (Unit) u1.clone();
								else {
									Unit u1Non = u1;
									Unit u2Non = u2;
									if (u1.getFunction() != null) {
										u1Non = u1.getNonFunctionVersion();
										v1 = u1.isDegTempUnit() ? v1 : u1Non.convertFrom(v1, u1);
									}
									if (u2.getFunction() != null) {
										u2Non = u2.getNonFunctionVersion();
										converted_v2 = u2.isDegTempUnit() ? converted_v2 : u2Non.convertFrom(converted_v2, u2);
									}
									u3 = ((Unit) u1Non.clone()).mul(u2Non);
								}
							}
						}
						result = (RealData) result.__add__(new Double(v1 * converted_v2));
					}
					result.setUnit(u3);
					return result;
				}
			} else {
				throw new IllegalArgumentException("Incompatible size: Can't multiply " + this + " to " + obj);
			}
		} else if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
			DomeMatrixData this_matrix = new DomeMatrixData(this);
			return this_matrix.__mul__(that);
		} else if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			List l1 = this.getValues();
			int v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double v1, converted_v2;
			Unit u3 = null;
			DomeVectorData result = new DomeVectorData(this.getSize());
			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();

				try { //convert value of "that" into unit of "this" if both are the same type
					converted_v2 = u1.convertFrom(v2, u2);
					if (i == 0) {
						try {
							u3 = new Unit("S" + u1.toString());
						} catch (Exception e) {
							if (u1.equals(Quantity.NO_UNIT))
								u3 = (Unit) u1.clone();
							else
								u3 = ((Unit) u1.clone()).mul(u1);
						}
					}
				} catch (Exception e) { //otherwise keep the old value
					converted_v2 = v2;
					if (i == 0) {
						if (u1.equals(Quantity.NO_UNIT))
							u3 = (Unit) u2.clone();
						else if (u2.equals(Quantity.NO_UNIT))
							u3 = (Unit) u1.clone();
						else {
							Unit u1Non = u1;
							Unit u2Non = u2;
							if (u1.getFunction() != null) {
								u1Non = u1.getNonFunctionVersion();
								v1 = u1.isDegTempUnit() ? v1 : u1Non.convertFrom(v1, u1);
							}
							if (u2.getFunction() != null) {
								u2Non = u2.getNonFunctionVersion();
								converted_v2 = u2.isDegTempUnit() ? converted_v2 : u2Non.convertFrom(converted_v2, u2);
							}
							u3 = ((Unit) u1Non.clone()).mul(u2Non);
						}
					}
				}
				result.setItem(i, new Double(v1 * converted_v2));
			}
			result.setUnit(u3);
			return result;
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			List l1 = this.getValues();
			double v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double v1, converted_v2;
			Unit u3 = null;
			DomeVectorData result = new DomeVectorData(this.getSize());
			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();

				try { //convert value of "that" into unit of "this" if both are the same type
					converted_v2 = u1.convertFrom(v2, u2);
					if (i == 0) {
						try {
							u3 = new Unit("S" + u1.toString());
						} catch (Exception e) {
							if (u1.equals(Quantity.NO_UNIT))
								u3 = (Unit) u1.clone();
							else
								u3 = ((Unit) u1.clone()).mul(u1);
						}
					}
				} catch (Exception e) { //otherwise keep the old value
					converted_v2 = v2;
					if (i == 0) {
						if (u1.equals(Quantity.NO_UNIT))
							u3 = (Unit) u2.clone();
						else if (u2.equals(Quantity.NO_UNIT))
							u3 = (Unit) u1.clone();
						else {
							Unit u1Non = u1;
							Unit u2Non = u2;
							if (u1.getFunction() != null) {
								u1Non = u1.getNonFunctionVersion();
								v1 = u1.isDegTempUnit() ? v1 : u1Non.convertFrom(v1, u1);
							}
							if (u2.getFunction() != null) {
								u2Non = u2.getNonFunctionVersion();
								converted_v2 = u2.isDegTempUnit() ? converted_v2 : u2Non.convertFrom(converted_v2, u2);
							}
							u3 = ((Unit) u1Non.clone()).mul(u2Non);
						}
					}
				}
				result.setItem(i, new Double(v1 * converted_v2));
			}
			result.setUnit(u3);
			return result;
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			List l1 = this.getValues();
			int v2 = that.intValue();
			Unit u1 = this.getUnit();

			double v1;
			DomeVectorData result = new DomeVectorData(this.getSize());
			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				result.setItem(i, new Double(v1 * v2));
			}
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			List l1 = this.getValues();
            double v2 = that.doubleValue();
			Unit u1 = this.getUnit();

			double v1;
			DomeVectorData result = new DomeVectorData(this.getSize());
			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				result.setItem(i, new Double(v1 * v2));
			}
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj.getClass().isArray()) {
			try {
				DomeMatrixData this_matrix = new DomeMatrixData(this);
				return this_matrix.__mul__(obj);
			} catch (IllegalArgumentException e) { // should never happen if we are calling
				System.err.println(e);             // method correctly
				throw new IllegalArgumentException("Can't substract " + this + " to " + obj);
			} catch (ArrayIndexOutOfBoundsException ex) { // array has zero length
				System.err.println("array has zero length");
				throw new IllegalArgumentException("Can't substract " + this + " to " + obj);
			}
		} else {
			throw new IllegalArgumentException("Can't multiply " + this + " to " + obj);
		}
	}

	public Object __div__(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			List l1 = this.getValues();
			int v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double v1, converted_v2;
			Unit u3 = null;
			DomeVectorData result = new DomeVectorData(this.getSize());
			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();

				try { //convert value of "that" into unit of "this" if both are the same type
					converted_v2 = u1.convertFrom(v2, u2);
					if (i == 0) {
						u3 = new Unit();
					}
				}
				catch (Exception e) { //otherwise keep the old value
					converted_v2 = v2;
					if (i == 0) {
						Unit u1Non = u1;
						Unit u2Non = u2;
						if (u1.getFunction() != null) {
							u1Non = u1.getNonFunctionVersion();
							v1 = u1.isDegTempUnit() ? v1 : u1Non.convertFrom(v1, u1);
						}
						if (u2.getFunction() != null) {
							u2Non = u2.getNonFunctionVersion();
							converted_v2 = u2.isDegTempUnit() ? converted_v2 : u2Non.convertFrom(converted_v2, u2);
						}
						u3 = ((Unit) u1Non.clone()).div(u2Non);
					}
				}
				result.setItem(i, new Double(v1 / converted_v2));
			}
			result.setUnit(u3);
			return result;
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			List l1 = this.getValues();
			double v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double v1, converted_v2;
			Unit u3 = null;
			DomeVectorData result = new DomeVectorData(this.getSize());
			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();

				try { //convert value of "that" into unit of "this" if both are the same type
					converted_v2 = u1.convertFrom(v2, u2);
					if (i == 0) {
						u3 = new Unit();
					}
				} catch (Exception e) { //otherwise keep the old value
					converted_v2 = v2;
					if (i == 0) {
						Unit u1Non = u1;
						Unit u2Non = u2;
						if (u1.getFunction() != null) {
							u1Non = u1.getNonFunctionVersion();
							v1 = u1.isDegTempUnit() ? v1 : u1Non.convertFrom(v1, u1);
						}
						if (u2.getFunction() != null) {
							u2Non = u2.getNonFunctionVersion();
							converted_v2 = u2.isDegTempUnit() ? converted_v2 : u2Non.convertFrom(converted_v2, u2);
						}
						u3 = ((Unit) u1Non.clone()).div(u2Non);
					}
				}
				result.setItem(i, new Double(v1 / converted_v2));
			}
			result.setUnit(u3);
			return result;
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			List l1 = this.getValues();
			int v2 = that.intValue();
			Unit u1 = this.getUnit();

			double v1;
			DomeVectorData result = new DomeVectorData(this.getSize());
			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				result.setItem(i, new Double(v1 / v2));
			}
			result.setUnit((Unit) u1.clone());
			return result;
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			List l1 = this.getValues();
			double v2 = that.doubleValue();
			Unit u1 = this.getUnit();

			double v1;
			DomeVectorData result = new DomeVectorData(this.getSize());
			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				result.setItem(i, new Double(v1 / v2));
			}
			result.setUnit((Unit) u1.clone());
			return result;
		} else {
			throw new IllegalArgumentException("Can't divide " + this + " to " + obj);
		}
	}

	public Object __getitem__(Object index)
	{   int ind;
        if (index instanceof Integer)
            ind = ((Integer) index).intValue();
        else if (index instanceof IntegerData)
            ind = ((IntegerData) index).getValue();
        else
            throw new IllegalArgumentException("DomeVectorData.__getitem__(): the specified index is not an integer");
        if (initialValue instanceof Integer)
		    return new IntegerData(this.getItem(ind).intValue(),unit);
        else
            return new RealData(this.getItem(ind).doubleValue(), unit);
	}

    public void __setitem__(Object c, Object value) {
        if (c instanceof Integer || c instanceof IntegerData) {
            Number num;
            if (value instanceof Number)
                num = (Number) value;
            else if (value instanceof RealData) {
                RealData real = (RealData) value;
                num = new Double(unit.convertFrom(real.getValue(),real.getUnit()));
            } else if (value instanceof IntegerData) {
                IntegerData real = (IntegerData) value;
                num = new Double(unit.convertFrom(real.getValue(), real.getUnit()));
            }
            else
                throw new IllegalArgumentException("DomeVectorData.__setitem__: " + value + " is not a number");
            int index = c instanceof Integer ? ((Integer) c).intValue() : ((IntegerData) c).getValue();
            setItem(index, num);
        }
        else
            throw new IllegalArgumentException("DomeVectorData.__setitem__: " + c + " is not an integer");

    }

	public Object __radd__(Object obj)
	{
		return this.__add__(obj);
	}

	public Object __rsub__(Object obj)
	{
		DomeVectorData neg_this = (DomeVectorData) this.__neg__();
		return neg_this.__add__(obj);
	}

    public BooleanData isEmpty() {
        return new BooleanData(getSize()==0);
    }

    public Object __eq__(Object o)
	{
		return new BooleanData(equals(o));
	}

    public Object __ne__(Object o)
    {
        return new BooleanData(!equals(o));
    }

	public boolean equals(Object anotherVector)
	{
		if (anotherVector instanceof DomeVectorData)
        {
           DomeVectorData that = (DomeVectorData) anotherVector;
           if (this.getSize() != that.getSize())  return false;
           if(this.getSize()==0) return true;

           DomeVectorData result=(DomeVectorData)__sub__(anotherVector);
           List resultValue=result.getValues();
           for (int i = 0; i < resultValue.size(); i++) {
               if(((Number) resultValue.get(i)).doubleValue()!=0.0) return false;
           }
           return true;
        }
        else
			return false;
	}
/*
	public Object __rmul__(Object obj)
	{
		return this.__mul__(obj);
	}

	public Object __rdiv__(Object obj)
	{
		if (obj instanceof Number) {
			Double that = (Double) obj;
			List l1 = this.getValues();
			double v2 = that.doubleValue();
			Unit u1 = this.getUnit();

			double v1;
			DomeVectorData result = new DomeVectorData(this.getSize());
			for (int i = 0; i < l1.size(); i++) {
				v1 = ((Number) l1.get(i)).doubleValue();
				result.setItem(i, new Double(v2 / v1));
			}
			result.setUnit(u1.inv());
			return result;
		}
		else {
			throw new IllegalArgumentException("Can't divide " + obj + " to " + this);
		}
	}
	*/

}