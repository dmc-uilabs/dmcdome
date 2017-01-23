// DomePreferenceData.java
//  ver

package mit.cadlab.dome3.objectmodel.dataobject;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomePreference;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.util.units.Quantity;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;
import org.python.core.PyList;
import org.python.core.PyInteger;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


/**
 * Data structure for a Dome Preference.
 */
public class DomePreferenceData extends AbstractDataObject
        implements DomePreference
{

	protected Vector data = new Vector();
	protected Unit unit = Quantity.NO_UNIT;
	//protected String unit = _unit.getId();

	protected boolean isFixedSize = false;
	protected Number initialValue = new Double(0.0); // determines type

//todo: in each firepropertychange add a fire      (Qing's note)
	public DomePreferenceData()
	{

	}

	public DomePreferenceData(DomePreference mat)
	{
		DomePreferenceData v = (DomePreferenceData) mat;
		data = (Vector) v.data.clone();
		unit = v.unit;
		initialValue = v.initialValue;
		isFixedSize = v.isFixedSize;
	}

	public DomePreferenceData(DomeVectorData v)
	{

		unit = v.getUnit();
		isFixedSize = v.isFixedSize();
		initialValue = v.initialValue;
		List l1 = v.getValues();

		Vector aRow = new Vector();
		if (v.isRowVector()) {
			for (int i = 0; i < l1.size(); i++) {
				aRow.add(l1.get(i));
			}
			data.add(aRow);
		}
		else {
			for (int i = 0; i < l1.size(); i++) {
				aRow = new Vector();
				aRow.add(l1.get(i));
				data.add(aRow);
			}
		}
	}

	public DomePreferenceData(RealData r, int row, int col)
	{
		double v = r.getValue();
		initialValue = new Double(r.getValue());
		unit = r.getUnit();
		createDomePreferenceDataFromValue(v, row, col);
	}

	public DomePreferenceData(IntegerData r, int row, int col)
	{
		double v = r.getValue();
		initialValue = new Integer(r.getValue());
		unit = r.getUnit();
		createDomePreferenceDataFromValue(v, row, col);
	}

	public DomePreferenceData(Double r, int row, int col)
	{
		double v = r.doubleValue();
		initialValue = r;
		unit = new Unit();
		createDomePreferenceDataFromValue(v, row, col);
	}

	public DomePreferenceData(Integer r, int row, int col)
	{
		double v = (double) r.intValue();
		initialValue = r;
		unit = new Unit();
		createDomePreferenceDataFromValue(v, row, col);
	}

	public DomePreferenceData(Integer[][] a)
	{
		unit = new Unit();
		isFixedSize = false;
		initialValue = new Integer(a[0][0].intValue());
		int row = a.length;
		int col = a[0].length;

		for (int i = 0; i < row; i++) {
			Vector aRow = new Vector();
			for (int j = 0; j < col; j++) {
				aRow.add(new Integer(a[i][j].intValue()));
			}
			data.add(aRow);
		}
	}

	public DomePreferenceData(Float[][] a)
	{
		unit = new Unit();
		isFixedSize = false;
		initialValue = new Double(a[0][0].floatValue());
		int row = a.length;
		int col = a[0].length;

		for (int i = 0; i < row; i++) {
			Vector aRow = new Vector();
			for (int j = 0; j < col; j++) {
				aRow.add(new Double(a[0][0].floatValue()));
			}
			data.add(aRow);
		}
	}

	public DomePreferenceData(Double[][] a)
	{
		unit = new Unit();
		isFixedSize = false;
		initialValue = a[0][0];
		int row = a.length;
		int col = a[0].length;

		for (int i = 0; i < row; i++) {
			Vector aRow = new Vector();
			for (int j = 0; j < col; j++) {
				aRow.add(a[i][j]);
			}
			data.add(aRow);
		}
	}

	public DomePreferenceData(int[][] a)
	{
		unit = new Unit();
		isFixedSize = false;
		initialValue = new Integer(a[0][0]);
		int row = a.length;
		int col = a[0].length;

		for (int i = 0; i < row; i++) {
			Vector aRow = new Vector();
			for (int j = 0; j < col; j++) {
				aRow.add(new Integer(a[i][j]));
			}
			data.add(aRow);
		}
	}

	public DomePreferenceData(double[][] a)
	{
		unit = new Unit();
		isFixedSize = false;
		initialValue = new Double(a[0][0]);
		int row = a.length;
		int col = a[0].length;

		for (int i = 0; i < row; i++) {
			Vector aRow = new Vector();
			for (int j = 0; j < col; j++) {
				aRow.add(new Double(a[i][j]));
			}
			data.add(aRow);
		}
	}

	public DomePreferenceData(float[][] a)
	{
		unit = new Unit();
		isFixedSize = false;
		initialValue = new Double(a[0][0]);
		int row = a.length;
		int col = a[0].length;

		for (int i = 0; i < row; i++) {
			Vector aRow = new Vector();
			for (int j = 0; j < col; j++) {
				aRow.add(new Double(a[i][j]));
			}
			data.add(aRow);
		}
	}


	private void createDomePreferenceDataFromValue(double v, int row, int col)
	{
		isFixedSize = false;
		initialValue = new Double(v);
		for (int i = 0; i < row; i++) {
			Vector aRow = new Vector();
			for (int j = 0; j < col; j++) {
				aRow.add(new Double(v));
			}
			data.add(aRow);
		}
	}

	public DomePreferenceData(Element xmlElement)
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
				// parse the rows
				String[] rows = data.split(";");
				for (int row = 0; row < rows.length; row++) {
					// parse the columns and create the new row
					Vector newRow = new Vector();
					String[] cols = rows[row].split(",");
					if (this.data.size() > 0 &&
					        this.data.elementAt(0) != null &&
					        cols.length != ((Vector) this.data.elementAt(0)).size())
						throw new NumberFormatException("too few columns in the matrix");
					try {
						for (int i = 0; i < cols.length; i++) {
							if (type.equals("real"))
								newRow.addElement(new Double(cols[i]));
							else
								newRow.addElement(new Integer(cols[i]));
						}
					}
					catch (NumberFormatException e) {
						e.printStackTrace();
					}
					// add the new row to the matrix
					this.data.add(newRow);
				}
			}
		}
	}

	public DomePreferenceData(int row, int col, boolean isFixed, Number initV)
	{
		isFixedSize = isFixed;

		initialValue = initV;

		for (int i = 0; i < row; i++) {
			Vector therow = new Vector(col);
			//fillRow(therow,initialValue);
			data.add(therow);
		}
		//otherwise the columncount will be zero;
		setColumnCount(col);
	}

	public DomePreferenceData(int row, int col)
	{
		this(row, col, false, new Double(0));
	}

	protected PropertyChangeListener createValueShadowListener()
	{
		return new PreferenceValueShadowListener();
	}

	protected PropertyChangeListener createValueUnitShadowListener()
	{
		return new PreferenceValueUnitShadowListener();
	}

	public boolean isCompatibleType(DataObject newObj)
	{
		return (newObj instanceof DomePreferenceData);
	}

	public DataObject duplicate()
	{
		return new DomePreferenceData(this);
	}

	public String toString()
	{
		return "DomePreference(" + getRowCount() + "*" + getColumnCount() + "):" + data.toString() + " " + unit;
	}

	public List getValues()
	{
		return data;
	}

	public void setValues(List values)
	{
		if (values.size() > 0) {
			if (values.get(0) instanceof Hashtable)
				setData((Hashtable) values.get(0), false);
			else {
				data.clear();
				data.addAll(values);
				firePropertyChange(DATA, null, data);
				fireValueChange();
			}
		}
	}

	public void setValues(DataObject newObj)
	{
		if (newObj instanceof DomePreferenceData)
			setData((DomePreferenceData) newObj);
	}

	public void setData(DomePreference v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomePreference - null parameter");
		if (!(v instanceof DomePreferenceData))
			throw new IllegalArgumentException("DomePreference - DomePreferenceData type parameter required");

        data = convertToThisUnit(((DomePreferenceData) v).data, ((DomePreferenceData) v).unit);
        // unit is not changed
		initialValue = ((DomePreferenceData) v).initialValue;
		isFixedSize = ((DomePreferenceData) v).isFixedSize;

		firePropertyChange(DATA, null, this);//pass the data vector as a whole
		fireValueChange();
	}

    protected Vector convertToThisUnit(Vector newData, Unit newUnit) {
        Vector converted = new Vector();
        for (int i = 0; i < newData.size(); i++) {
            Vector newRow = new Vector();
            Vector row = (Vector) newData.get(i);
            for (int j = 0; j < row.size(); j++) {
                newRow.add(new Double(unit.convertFrom(((Number) row.get(j)).doubleValue(), newUnit)));
            }
            converted.add(newRow);
        }
        return converted;
    }

	//getting from xmlrpc
	public void setData(Hashtable table, boolean changeValueOnly)
	{
		boolean unitChanged = true;
		Vector oldData = (Vector) data.clone();

		Vector d = (Vector) table.get("data");
		String unitS = (String) table.get("unit");
		Unit newUnit = new Unit(unitS);
		if (newUnit.equals(unit)) unitChanged = false;

		if (changeValueOnly) {
			if (unit == Quantity.NO_UNIT || (!unitChanged))
				data = (Vector) d.clone();
			else
				data = convertUnit(d, unit, newUnit, initialValue);
		}
		else {
			unit = newUnit;
			data = (Vector) d.clone();
		}

		if ((!compareVector(oldData, data)) || unitChanged) {
			firePropertyChange(DATA, null, this);//pass the data vector as a whole
			fireValueChange();
		}
	}

	public DomeVectorData getRow(int row)
	{
		if (row >= getRowCount())
			return null;

		Vector v = (Vector) data.get(row);
		return new DomeVectorData(v, unit, true, true, initialValue);
	}

	public DomeVectorData getCol(int col)
	{
		if (col >= getColumnCount())
			return null;

		Vector vCol = new Vector();
		for (int row = 0; row < getRowCount(); row++) {
			Vector vRow = (Vector) data.get(row);
			vCol.addElement(vRow.get(col));
		}

		return new DomeVectorData(vCol, unit, false, true, initialValue);
	}

	public List getData()
	{
		return Collections.unmodifiableList(data);
	}

	public double[][] getDoubleArrayData()
	{
		int row = data.size();
		int col = ((Vector) data.get(0)).size();
		double[][] arrayData = new double[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				Vector arow = (Vector) data.get(i);
				arrayData[i][j] = ((Number) arow.get(j)).doubleValue();
			}
		}
		return arrayData;
	}

	public Number[][] getNumberArrayData()
	{
		int row = data.size();
		int col = ((Vector) data.get(0)).size();
		Number[][] arrayData = new Number[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				Vector arow = (Vector) data.get(i);
				arrayData[i][j] = (Number) arow.get(j);
			}
		}
		return arrayData;
	}

	public int getRowCount()
	{
		return data.size();
	}

	public int getColumnCount()
	{
		if (data.size() == 0)
			return 0;
		else
			return ((Vector) data.get(0)).size();
	}

    public int[] getSize() {
        int[] size = {getRowCount(), getColumnCount()};
        return size;
    }

	public void setColumnCount(int columnCount)
	{
		if (columnCount == getColumnCount()) return;
		int oldSize = getColumnCount();
		for (int r = 0; r < getRowCount(); r++) {
			Vector row = (Vector) data.elementAt(r);

			if (oldSize > columnCount) {
				row.setSize(columnCount); //being truncated;
			}
			else
				appendColumnItems(row, oldSize, columnCount);
		}

		//fire both items and size change
		if (oldSize > columnCount) {
			Vector info = new Vector();
			info.add("delcol");
			for (int i = oldSize; i >= columnCount; i--)
				info.add(new Integer(i));
			firePropertyChange(ITEMS, null, info);
			firePropertyChange(SIZE, null, new String("delcol"));
			firePropertyChange(COLSIZE, null, new Integer(getColumnCount()));
			fireValueChange();
		}
		else {
			Vector info = new Vector();
			info.add("addcol");
			for (int i = oldSize; i < columnCount; i++)
				info.add(new Integer(i));
			firePropertyChange(ITEMS, null, info);
			firePropertyChange(SIZE, null, new String("addcol"));
			firePropertyChange(COLSIZE, null, new Integer(getColumnCount()));
			fireValueChange();
		}
	}

	public void setRowCount(int newSize)
	{
		if (isFixedSize) {
			debug("Warning! dataMatrix size is fixed, can't change row count");
			return;
		}
		if ((newSize < 0) || (newSize == getRowCount()))
			return;

		int oldNumRows = getRowCount();
		if (newSize <= getRowCount()) {
			// newSize is smaller than our current size, so we can just
			// let Vector discard the extra rows
			data.setSize(newSize);
		}
		else {
			int columnCount = getColumnCount();
			// We are adding rows to the model
			while (getRowCount() < newSize) {
				Vector newRow = new Vector(columnCount);
				//fill with initial value
				fillRow(newRow, initialValue);
				data.addElement(newRow);
			}
		}
		//fire both items and size change
		if (oldNumRows > newSize) {
			Vector info = new Vector();
			info.add("delrow");
			for (int i = oldNumRows - 1; i >= newSize; i--)
				info.add(new Integer(i));
			firePropertyChange(ITEMS, null, info);
			firePropertyChange(SIZE, null, new String("delrow"));
			firePropertyChange(ROWSIZE, null, new Integer(getRowCount()));
			fireValueChange();
		}
		else {
			Vector info = new Vector();
			info.add("addrow");
			for (int i = oldNumRows; i < newSize; i++)
				info.add(new Integer(i));
			firePropertyChange(ITEMS, null, info);
			firePropertyChange(SIZE, null, new String("addrow"));
			firePropertyChange(ROWSIZE, null, new Integer(getRowCount()));
			fireValueChange();
		}
	}

    public void setNumRows(int row, Object valSetter)
    {
        setSize(new Integer(row), new Integer(getColumnCount()), valSetter);
    }

    public void setNumCols(int col, Object valSetter) {
        setSize(new Integer(getRowCount()), new Integer(col), valSetter);
    }

    public void setSize(Object x, Object y, Object z)
    {
        int row, col;
        Object valSetter;
        if (x instanceof Integer) { // x is row, y is col
            row = ((Integer) x).intValue();
            col = ((Integer) y).intValue();
            valSetter = z;

        } else { // x is an array [row,col]
            row = ((PyInteger) ((PyList) x).__finditem__(0)).getValue();
            col = ((PyInteger) ((PyList) x).__finditem__(1)).getValue();
            valSetter = y;
        }

        double initVal;
        if (valSetter instanceof RealData) {
            RealData real = (RealData) valSetter;
            try { // see if units are compatible
                initVal = getUnit().convertFrom(real.getValue(), real.getUnit());
            } catch (Exception e) {
                throw new IllegalArgumentException("DomePreferenceData.setSize: unit of " + real + " is not compatible" +
                        "with that of the matrix");
            }
        } else if (valSetter instanceof IntegerData) {
            IntegerData integer = (IntegerData) valSetter;
            try { // see if units are compatible
                initVal = getUnit().convertFrom(integer.getValue(), integer.getUnit());
            } catch (Exception e) {
                throw new IllegalArgumentException("DomePreferenceData.setSize: unit of " + integer + " is not compatible" +
                        "with that of the matrix");
            }
        } else if (valSetter instanceof Integer) {
            initVal = ((Integer) valSetter).intValue();
        } else if (valSetter instanceof Number) {
            initVal = ((Double) valSetter).doubleValue();
        } else {
            throw new IllegalArgumentException("DomePreferenceData.setSize: Invalid input: " + valSetter);
        }
        initialValue = new Double(initVal);
        setRowCount(row);
        setColumnCount(col);
    }


	public void setFixedSize(boolean isFixed)
	{
		this.isFixedSize = isFixed;
		firePropertyChange(FIXEDSIZE, null, new Boolean(isFixed));
		fireValueChange();
	}

	public boolean isFixedSize()
	{
		return this.isFixedSize;
	}

	public Number getItem(int row, int col)
	{
		if (getRowCount() == 0 || getColumnCount() == 0) return null;
		Vector rowVector = (Vector) data.elementAt(row);
		return (Number) rowVector.elementAt(col);
	}

	public void setItem(int row, int col, Number n)
	{
		if (n.getClass() != initialValue.getClass()) {
			if (initialValue instanceof Double)
				n = new Double(n.doubleValue());
			else if (initialValue instanceof Integer)
				n = new Integer(n.intValue());
		}
		Vector rowVector = (Vector) data.elementAt(row);
		rowVector.setElementAt(n, col);
		firePropertyChange(ITEM, null, new Point(row, col));
		fireValueChange();
	}

    public void setRow(int row, DomeVectorData vector)
    {
        if (getColumnCount()!=vector.getSize())
            throw new IllegalArgumentException("DomePreferenceData.setRow: "+vector+" has incompatible size");

        for (int i = 0; i < vector.getSize(); i++) {
            double val;
            try {
                val = getUnit().convertFrom(vector.getItem(i).doubleValue(), vector.getUnit());
            } catch (Exception e) {
                throw new IllegalArgumentException("DomePreferenceData.setCol: vector " + vector + " has in compatible unit");
            }
            setItem(row, i, new Double(val));
        }
    }

    public void setCol(int col, DomeVectorData vector) {
        if (getRowCount() != vector.getSize())
            throw new IllegalArgumentException("DomePreferenceData.setCol: " + vector + " has incompatible size");

        for (int i = 0; i < vector.getSize(); i++) {
            double val;
            try {
                val = getUnit().convertFrom(vector.getItem(i).doubleValue(),vector.getUnit());
            } catch (Exception e) {
                throw new IllegalArgumentException("DomePreferenceData.setCol: vector "+vector+ " has in compatible unit");
            }
            setItem(i, col, new Double(val));
        }
    }

	public void setValues(double[][] newValues)
	{
		data = new Vector();
		for (int i = 0; i < newValues.length; i++) {
			double[] row = newValues[i];
			Vector r = new Vector();
			for (int j = 0; j < row.length; j++) {
				double v = row[j];
				if (initialValue instanceof Integer) {
					r.add(new Integer((int) v));
				}
				else // double
					r.add(new Double(v));
			}
			data.add(r);
		}
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
		this.unit = u;

		firePropertyChange(UNIT, old, u);
		fireValueChange();
	}

	protected TypeInfo getTypeInfo()
	{
		return DomePreference.TYPE_INFO;
	}


	public String getValueType()
	{
		if (initialValue instanceof Integer) return "Integer";
		return "Real";
	}

	public void setValueType(String type)
	{
		if (type.toLowerCase().equals("real")) {
			if (initialValue instanceof Integer)
				changeClass(false);

		}
		else if (type.toLowerCase().equals("integer")) {
			if (initialValue instanceof Double)
				changeClass(true);
		}

		firePropertyChange(DATA, null, this);
		fireValueChange();
	}

	public Number getInitialValue()
	{
		return initialValue;
	}

	//not fire property change, should be only inner use
	private void changeClass(boolean isInteger)
	{
		if (isInteger) {
			initialValue = new Integer(initialValue.intValue());
			for (int i = 0; i < data.size(); i++) {
				Vector rowData = (Vector) data.get(i);
				for (int j = 0; j < rowData.size(); j++) {
					Number n = (Number) rowData.elementAt(j);
					rowData.set(j, new Integer(n.intValue()));
				}
			}
		}
		else { // must be Double
			initialValue = new Double(initialValue.doubleValue());
			for (int i = 0; i < data.size(); i++) {
				Vector rowData = (Vector) data.get(i);
				for (int j = 0; j < rowData.size(); j++) {
					Number n = (Number) rowData.elementAt(j);
					rowData.set(j, new Double(n.doubleValue()));
				}
			}
		}
	}

	public void setInitialValue(Number n)
	{
		if (n == null) return;
		if (initialValue.getClass() != n.getClass()) { // wrong type...convert
			if (initialValue instanceof Integer) {
				initialValue = new Integer(n.intValue());
			}
			else { // must be Double
				initialValue = new Double(n.doubleValue());
			}
		}
		else { // correct type
			initialValue = n;    //This is wrong
			//This will not overwrite the refernce
			//reference will revert back to Integer object
			//after this method has exited.
		}
	}

	public void addRowItems(int index, int howMany, Number newInitialValue)
	{
		if (isFixedSize) {
			debug("Warning! dataVector size is fixed, can't add...");
			return;
		}
		if (index < 0 || index > data.size())
			return;
		setInitialValue(newInitialValue);
		//stores information to pass when propertychange is fired
		Vector info = new Vector();
		info.add("addrow");//put {0} as a add/update/del identification

		for (int i = 0; i < howMany; ++i) {
			Vector row = new Vector(getColumnCount());
			fillRow(row, initialValue);
			data.insertElementAt(row, index);
			info.add(new Integer(index + i));
		}

		firePropertyChange(ITEMS, null, info);
		firePropertyChange(SIZE, null, new String("addrow"));
		firePropertyChange(ROWSIZE, null, new Integer(getRowCount()));
		fireValueChange();

	}


	public void removeRowItems(int[] indices)
	{
		if (isFixedSize) {
			debug("Warning! dataVector size is fixed, can't delete...");
			return;
		}
		// assume indices are all good

		//stores information to pass when propertychange is fired
		Vector info = new Vector();
		info.add("delrow");//put {0} as a add/update/del identification
		Arrays.sort(indices);
		for (int i = indices.length - 1; i >= 0; --i) {
			data.remove(indices[i]);
			info.add(new Integer(indices[i]));
		}

		firePropertyChange(ITEMS, null, info);
		firePropertyChange(SIZE, null, new String("delrow"));
		firePropertyChange(ROWSIZE, null, new Integer(getRowCount()));
		fireValueChange();
	}


	public void addColumnItems(int index, int howMany, Number newInitialValue)
	{

		if (isFixedSize) {
			debug("Warning! dataPreference size is fixed, can't add...");
			return;
		}
		if (index < 0 || index > data.size())
			return;

		setInitialValue(newInitialValue);
		Vector info = new Vector();
		info.add("addcol");

		for (int j = 0; j < getRowCount(); j++) {
			//for each row add columns into it
			Vector row = (Vector) (data.elementAt(j));
			for (int i = 0; i < howMany; i++) {
				row.insertElementAt(initialValue, index);
			}
		}

		for (int i = 0; i < howMany; i++) {
			info.add(new Integer(index + i));
		}

		firePropertyChange(ITEMS, null, info);
		firePropertyChange(SIZE, null, new String("addcol"));
		firePropertyChange(COLSIZE, null, new Integer(getColumnCount()));
		fireValueChange();
	}

	public void removeColumnItems(int[] indices)
	{
		if (isFixedSize) {
			debug("Warning! dataVector size is fixed, can't delete...");
			return;
		}
		// assume indices are all good
		Arrays.sort(indices);
		Vector info = new Vector();
		info.add("delcol");

		for (int j = 0; j < getRowCount(); j++) {
			//for each row delete columns
			Vector row = (Vector) (data.elementAt(j));
			for (int i = indices.length - 1; i >= 0; --i) {
				row.remove(indices[i]);
			}
		}

		for (int i = 0; i < indices.length; i++) {
			info.add(new Integer(indices[i]));
		}

		firePropertyChange(ITEMS, null, info);
		firePropertyChange(SIZE, null, new String("delcol"));
		firePropertyChange(COLSIZE, null, new Integer(getColumnCount()));
		fireValueChange();
	}


	//startIndex is inclusive and endIndex is exclusive
	public void removeRange(int startIndex, int endIndex, boolean isForRow)
	{
		if (isFixedSize) {
			debug("Warning! dataVector size is fixed, can't delete...");
			return;
		}

		if (isForRow) //removing rows
		{
			if (startIndex < 0 || endIndex >= getRowCount()) return;
			Vector info = new Vector();
			info.add("delrow");
			for (int i = endIndex - 1; i >= startIndex; i--) {
				data.removeElementAt(i);
				info.add(new Integer(i));
			}

			firePropertyChange(ITEMS, null, info);
			firePropertyChange(SIZE, null, new String("delrow"));
			firePropertyChange(ROWSIZE, null, new Integer(getRowCount()));
			fireValueChange();


		}
		else {//removing columns
			if (startIndex < 0 || endIndex >= getColumnCount()) return;
			Vector info = new Vector();
			info.add("delcol");
			for (int j = 0; j < getRowCount(); j++) {
				//for each row delete columns
				Vector row = (Vector) (data.elementAt(j));
				for (int i = endIndex - 1; i >= startIndex; i--)
					row.removeElementAt(i);
			}
			for (int i = endIndex - 1; i > startIndex; i--)
				info.add(new Integer(i));

			firePropertyChange(ITEMS, null, info);
			firePropertyChange(SIZE, null, new String("delcol"));
			firePropertyChange(COLSIZE, null, new Integer(getColumnCount()));
			fireValueChange();

		}


	}

	public void fillItems(Vector selectedPoints, Number n)
	{
		//selectedPoints: vector of java.awt.Point, with x:row, y:column
		// assume indices are all good
		setInitialValue(n);
		for (int i = 0; i < selectedPoints.size(); ++i) {
			Point p = (Point) selectedPoints.elementAt(i);

			setItem(p.x, p.y, initialValue); //will fire property change in this function

		}
		return;
	}

	//not fire propertychange, should be used by this function itself
	private void fillRow(Vector row, Number n)
	{

		// assume indices are all good
		setInitialValue(n);

		for (int i = 0; i < getColumnCount(); ++i)
			row.add(i, initialValue);
		return;
	}

	//not fire propertychange, should be used by this function itself
	private void appendColumnItems(Vector row, int oldSize, int newSize)
	{
		if (oldSize >= newSize) return;
		for (int i = 0; i < (newSize - oldSize); i++)
			row.add(initialValue);
	}

	//static function for use

	//then check is they are all same type with the first domeMatrix
	//this has two cases: one is all the rowCount is the same, the other is all the colCount is the same, depends on how to append then together
	//if all rowCount is the same, then append then horizontally and colCount doesn't need to be same
	//else if all colCount is the same, then append then vertically and rowCount doesn't need to be same

	// Matrix +/-/*

	/**
	 * matrix plus, must be same dimension, also the first domematrix data determines the result matrix property
	 */

	/*public static DomeMatrixData plus(DomeMatrixData m1, DomeMatrixData m2)
	{
		//first check dimension is right
		if (m1.getRowCount() == m2.getRowCount() && m1.getColumnCount() == m2.getColumnCount()) {
			if (m1.getValueType().equals(m2.getValueType())) {
				DomeMatrixData result = new DomeMatrixData(m1);
				for (int i = 0; i < result.getRowCount(); i++)
					for (int j = 0; j < result.getColumnCount(); j++) {
						result.setItem(i, j, new Double(m1.getItem(i, j).doubleValue() + m2.getItem(i, j).doubleValue()));
					}
				return result;
			}
			else //should all changed to the valuetype of the first Matrix
			{
				DomeMatrixData result = new DomeMatrixData(m1);
				for (int i = 0; i < result.getRowCount(); i++)
					for (int j = 0; j < result.getColumnCount(); j++) {
						result.setItem(i, j, new Double(m1.getItem(i, j).doubleValue() + m2.getItem(i, j).doubleValue()));//it automatically takes type conversion(integer/double)
					}
				return result;
			}
		}
		else
			return null;

	}

*/
	/**
	 * matrix minus,
	 * must be same dimension, also the first domematrix data determines the result matrix property
	 */

/*	public static DomeMatrixData minus(DomeMatrixData m1, DomeMatrixData m2)
	{
		//first check dimension is right
		if (m1.getRowCount() == m2.getRowCount() && m1.getColumnCount() == m2.getColumnCount()) {
			if (m1.getValueType().equals(m2.getValueType())) {
				DomeMatrixData result = new DomeMatrixData(m1);
				for (int i = 0; i < result.getRowCount(); i++)
					for (int j = 0; j < result.getColumnCount(); j++) {
						result.setItem(i, j, new Double(m1.getItem(i, j).doubleValue() - m2.getItem(i, j).doubleValue()));
					}
				return result;
			}
			else {
				DomeMatrixData result = new DomeMatrixData(m1);
				for (int i = 0; i < result.getRowCount(); i++)
					for (int j = 0; j < result.getColumnCount(); j++) {

						result.setItem(i, j, new Double(m1.getItem(i, j).doubleValue() - m2.getItem(i, j).doubleValue()));//it automatically takes type conversion(integer/double)
					}
				return result;
			}

		}
		else
			return null;

	}

	/**
	 * matrix multiply,
	 * must be match dimension--which means they have flipped row/col count, also the first domematrix data determines the result matrix property
	 */
/*
	public static DomeMatrixData multiply(DomeMatrixData m1, DomeMatrixData m2)
	{
		if (m1.getColumnCount() == m2.getRowCount()) {
			DomeMatrixData result = new DomeMatrixData(m1.getRowCount(), m2.getColumnCount());
			result.setInitialValue(m1.getInitialValue());
			for (int i = 0; i < m1.getRowCount(); i++)
				for (int j = 0; j < m2.getColumnCount(); j++) {
					double n = 0;
					for (int k = 0; k < m1.getColumnCount(); k++)
						n += m1.getItem(i, k).doubleValue() * m2.getItem(k, j).doubleValue();
					result.setItem(i, j, new Double(n));
				}
			return result;

		}
		else
			return null;


	}
*/

	// Matrix append vertically /horizontally

	/*
	 *  Matrix appendHorizontally
	 *  must be same row count
	 *  the first matrix determins property
	 */

	public static DomePreferenceData appendHorizontally(DomePreferenceData m1, DomePreferenceData m2)
	{
		if (m1.getRowCount() == m2.getRowCount()) {
			DomePreferenceData result = new DomePreferenceData(m1.getRowCount(), m1.getColumnCount() + m2.getColumnCount(), m1.isFixedSize(), m1.getInitialValue());

			for (int i = 0; i < result.getRowCount(); i++) {
				for (int j = 0; j < m1.getColumnCount(); j++)
					result.setItem(i, j, m1.getItem(i, j));
				for (int k = 0; k < m2.getColumnCount(); k++)
					result.setItem(i, m1.getColumnCount() + k, m2.getItem(i, k));
			}

			return result;
		}
		else
			return null;

	}

	/*
	 *  Matrix appendHorizontally
	 *  must be same column count
	 *  the first matrix determins property
	 */

	public static DomePreferenceData appendVertically(DomePreferenceData m1, DomePreferenceData m2)
	{
		if (m1.getColumnCount() == m2.getColumnCount()) {
			DomePreferenceData result = new DomePreferenceData(m1.getRowCount() + m2.getRowCount(), m2.getColumnCount(), m1.isFixedSize(), m1.getInitialValue());

			for (int j = 0; j < result.getColumnCount(); j++) {
				for (int i = 0; i < result.getRowCount(); i++) {
					if (i < m1.getRowCount())
						result.setItem(i, j, m1.getItem(i, j));
					else
						result.setItem(i, j, m2.getItem(i - m1.getRowCount(), j));
				}
			}

			return result;

		}
		else
			return null;
	}

	private void debug(String msg)
	{
		boolean debug = false;
		if (debug)
			System.out.println("DomePreferenceData: " + msg);
	}

	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		boolean isReal = (initialValue.getClass() == Double.class);

		// store base values
		xml.addElement("unit").setText(unit.toString());
		Element initValElement = xml.addElement("initialValue");
		initValElement.addAttribute("type", (isReal ? "real" : "integer"));
		initValElement.setText(initialValue.toString());
		xml.addElement("fixedSize").setText(Boolean.toString(isFixedSize));

		// store data
		Element dataElement = xml.addElement("data");
		dataElement.addAttribute("type", (isReal ? "real" : "integer"));
		if (data.size() > 0) {
			StringBuffer text = new StringBuffer();
			int size = data.size();
			for (int row = 0; row < size; row++) {
				Vector nextRow = (Vector) data.elementAt(row);
				Object[] values = nextRow.toArray();
				int length = values.length;
				for (int i = 0; i < length - 1; i++) {
					text.append(values[i]);
					text.append(",");
				}
				text.append(values[length - 1]);
				if (row < size - 1)
					text.append(";");
			}
			dataElement.setText(text.toString());
		}

		return xml;
	}

	protected class PreferenceValueShadowListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getNewValue();
			if (obj instanceof Hashtable) {
				if (property.equals(VALUE)) {
					setData((Hashtable) obj, true);
				}
			}
		}
	}

	protected class PreferenceValueUnitShadowListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			if (obj instanceof Hashtable) {
				if (property.equals(VALUE)) {
					setData((Hashtable) obj, false);
				}
				if (property.equals(DomePreference.UNIT)) {
					setUnit((Unit) ((Hashtable) obj).get("unit"));
				}
			}
		}
	}

	public Object __neg__()
	{

		DomePreferenceData result = new DomePreferenceData(this);
		for (int i = 0; i < result.getRowCount(); i++) {
			for (int j = 0; j < result.getColumnCount(); j++) {
				result.setItem(i, j, new Double(-(this.getItem(i, j)).doubleValue()));
			}
		}
		return result;
	}


	public Object __add__(Object obj)
	{

		if (obj instanceof DomePreferenceData) {
			DomePreferenceData that = (DomePreferenceData) obj;

			if ((this.getRowCount() != that.getRowCount()) || (this.getColumnCount() != that.getColumnCount()))
				throw new IllegalArgumentException("Incompatible size: Can't add " + this + " to " + obj);

			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			DomePreferenceData result = new DomePreferenceData(this.getRowCount(), this.getColumnCount());

			if (that.getItem(0, 0) instanceof Number) {
				double v1;
				double converted_v2;

				for (int i = 0; i < result.getRowCount(); i++) {
					for (int j = 0; j < result.getColumnCount(); j++) {
						v1 = this.getItem(i, j).doubleValue();
						converted_v2 = u1.convertFrom(that.getItem(i, j).doubleValue(), u2);
						result.setItem(i, j, new Double(v1 + converted_v2));
					}

				}
			}
			else {
				throw new IllegalArgumentException("Incompatible size: Can't add " + this + " to " + obj);
			}
			// use unit of LHS
			result.setUnit(u1);
			return result;

		}
		else if (obj instanceof DomeVectorData) {

			DomePreferenceData dmd = new DomePreferenceData((DomeVectorData) obj);
			return this.__add__(dmd);

		}
		else if (obj instanceof RealData) {

			DomePreferenceData dmd = new DomePreferenceData((RealData) obj, this.getRowCount(), this.getColumnCount());
			return this.__add__(dmd);

		}
		else if (obj instanceof IntegerData) {

			DomePreferenceData dmd = new DomePreferenceData((IntegerData) obj, this.getRowCount(), this.getColumnCount());
			return this.__add__(dmd);

		}
		else if (obj instanceof Integer) {

			DomePreferenceData dmd = new DomePreferenceData((Integer) obj, this.getRowCount(), this.getColumnCount());
			dmd.setUnit(this.getUnit());
			return this.__add__(dmd);

		}
		else if (obj instanceof Float) {

			DomePreferenceData dmd = new DomePreferenceData(new Double(((Float) obj).floatValue()), this.getRowCount(), this.getColumnCount());
			dmd.setUnit(this.getUnit());
			return this.__add__(dmd);

		}
		else if (obj instanceof Double) {

			DomePreferenceData dmd = new DomePreferenceData((Double) obj, this.getRowCount(), this.getColumnCount());
			dmd.setUnit(this.getUnit());
			return this.__add__(dmd);

		}
		else if (obj.getClass().isArray()) {
			try {
				Object a = Array.get(obj, 0);
				//System.out.println("a = " + a);
				Object o = Array.get(a, 0);
				DomePreferenceData dmd;
				if (o instanceof Integer)
					try {
						dmd = new DomePreferenceData((Integer[][]) obj);
					}
					catch (ClassCastException e) {
						dmd = new DomePreferenceData((int[][]) obj);
					}
				else if (o instanceof Double)
					try {
						dmd = new DomePreferenceData((Double[][]) obj);
					}
					catch (ClassCastException e) {
						dmd = new DomePreferenceData((double[][]) obj);
					}
				else if (o instanceof Float)
					try {
						dmd = new DomePreferenceData((Float[][]) obj);
					}
					catch (ClassCastException e) {
						dmd = new DomePreferenceData((float[][]) obj);
					}
				else {
					throw new IllegalArgumentException("Can't add " + this + " to " + obj);
				}

				//dmd.setUnit(this.getUnit());
				return this.__add__(dmd);

			}
			catch (IllegalArgumentException e) { // should never happen if we are calling
				System.err.println(e);             // method correctly
				throw new IllegalArgumentException("Can't add " + this + " to " + obj);
			}
			catch (ArrayIndexOutOfBoundsException ex) { // array has zero length
				System.err.println("array has zero length");
				throw new IllegalArgumentException("Can't add " + this + " to " + obj);
			}

		}
		else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
	}

	public Object __sub__(Object obj)
	{

		if (obj instanceof DomePreferenceData) {
			DomePreferenceData that = (DomePreferenceData) obj;

			return this.__add__(that.__neg__());

		}
		else if (obj instanceof DomeVectorData) {

			DomePreferenceData dmd = new DomePreferenceData((DomeVectorData) obj);
			return this.__add__(dmd.__neg__());

		}
		else if (obj instanceof RealData) {

			DomePreferenceData dmd = new DomePreferenceData((RealData) obj, this.getRowCount(), this.getColumnCount());
			return this.__add__(dmd.__neg__());

		}
		else if (obj instanceof IntegerData) {

			DomePreferenceData dmd = new DomePreferenceData((IntegerData) obj, this.getRowCount(), this.getColumnCount());
			return this.__add__(dmd.__neg__());

		}
		else if (obj instanceof Integer) {

			DomePreferenceData dmd = new DomePreferenceData((Integer) obj, this.getRowCount(), this.getColumnCount());
			dmd.setUnit(this.getUnit());
			return this.__add__(dmd.__neg__());

		}
		else if (obj instanceof Float) {

			DomePreferenceData dmd = new DomePreferenceData(new Double(((Float) obj).floatValue()), this.getRowCount(), this.getColumnCount());
			dmd.setUnit(this.getUnit());
			return this.__add__(dmd.__neg__());

		}
		else if (obj instanceof Double) {

			DomePreferenceData dmd = new DomePreferenceData((Double) obj, this.getRowCount(), this.getColumnCount());
			dmd.setUnit(this.getUnit());
			return this.__add__(dmd.__neg__());

		}
		else if (obj.getClass().isArray()) {
			try {
				Object a = Array.get(obj, 0);
				//System.out.println("a = " + a);
				Object o = Array.get(a, 0);
				DomePreferenceData dmd;
				if (o instanceof Integer)
					try {
						dmd = new DomePreferenceData((Integer[][]) obj);
					}
					catch (ClassCastException e) {
						dmd = new DomePreferenceData((int[][]) obj);
					}
				else if (o instanceof Double)
					try {
						dmd = new DomePreferenceData((Double[][]) obj);
					}
					catch (ClassCastException e) {
						dmd = new DomePreferenceData((double[][]) obj);
					}
				else if (o instanceof Float)
					try {
						dmd = new DomePreferenceData((Float[][]) obj);
					}
					catch (ClassCastException e) {
						dmd = new DomePreferenceData((float[][]) obj);
					}
				else {
					throw new IllegalArgumentException("Can't add " + this + " to " + obj);
				}

				//dmd.setUnit(this.getUnit());
				return this.__add__(dmd.__neg__());

			}
			catch (IllegalArgumentException e) { // should never happen if we are calling
				System.err.println(e);             // method correctly
				throw new IllegalArgumentException("Can't add " + this + " to " + obj);
			}
			catch (ArrayIndexOutOfBoundsException ex) { // array has zero length
				System.err.println("array has zero length");
				throw new IllegalArgumentException("Can't add " + this + " to " + obj);
			}

		}
		else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
	}


	public Object __radd__(Object obj)
	{
		return this.__add__(obj);
	}

	public Object __rsub__(Object obj)
	{
		return ((DomePreferenceData) this.__neg__()).__add__(obj);
	}

	public Object __getitem__(int r, int c)
	{
		return this.getItem(r, c);
	}

	public Object __mul__(Object obj)
	{
		Unit u1 = this.getUnit();
		Unit u2, u3;
		if (obj instanceof DomePreferenceData) {
			DomePreferenceData that = (DomePreferenceData) obj;
			u2 = that.getUnit();
			if (this.getColumnCount() != that.getRowCount())
				throw new IllegalArgumentException("Incompatible size: Can't multiply " + this + " with " + obj);

			try {
				u1.convertFrom(1.0, u2);
				u3 = new Unit("S" + u1.toString());
			}
			catch (Exception e) {
				if (u1.equals(Quantity.NO_UNIT))
					u3 = u2;
				else if (u2.equals(Quantity.NO_UNIT))
					u3 = u1;
				else
					u3 = u1.mul(u2);
			}
			DomePreferenceData result = new DomePreferenceData(this.getRowCount(), that.getColumnCount());
			if (that.getItem(0, 0) instanceof Number) {
				result.setInitialValue(this.getInitialValue());
				for (int i = 0; i < this.getRowCount(); i++) {
					for (int j = 0; j < that.getColumnCount(); j++) {
						double n = 0;
						for (int k = 0; k < this.getColumnCount(); k++) {
							n += this.getItem(i, k).doubleValue() * that.getItem(k, j).doubleValue();
						}
						result.setItem(i, j, new Double(n));
					}
				}
				result.setUnit(u3);
				return result;

			}
			else {
				throw new IllegalArgumentException("Incompatible size: Can't add " + this + " to " + obj);
			}
			// use unit of LHS
		}
		else if (obj instanceof DomeVectorData) {
			DomePreferenceData dmd = new DomePreferenceData((DomeVectorData) obj);
			return this.__mul__(dmd);
		}
		else if (obj instanceof RealData) {
			DomePreferenceData dmd = new DomePreferenceData((RealData) obj, 1, 1);
			return this.__scale__(dmd);
		}
		else if (obj instanceof IntegerData) {
			DomePreferenceData dmd = new DomePreferenceData((IntegerData) obj, 1, 1);
			return this.__scale__(dmd);
		}
		else if (obj instanceof Integer) {
			DomePreferenceData dmd = new DomePreferenceData((Integer) obj, 1, 1);
			return this.__scale__(dmd);
		}
		else if (obj instanceof Float) {
			DomePreferenceData dmd = new DomePreferenceData(new Double(((Float) obj).floatValue()), 1, 1);
			return this.__scale__(dmd);
		}
		else if (obj instanceof Double) {
			DomePreferenceData dmd = new DomePreferenceData((Double) obj, 1, 1);
			return this.__scale__(dmd);
		}
		else if (obj.getClass().isArray()) {
			try {
				Object a = Array.get(obj, 0);
				//System.out.println("a = " + a);
				Object o = Array.get(a, 0);
				DomePreferenceData dmd;
				if (o instanceof Integer)
					try {
						dmd = new DomePreferenceData((Integer[][]) obj);
					}
					catch (ClassCastException e) {
						dmd = new DomePreferenceData((int[][]) obj);
					}
				else if (o instanceof Double)
					try {
						dmd = new DomePreferenceData((Double[][]) obj);
					}
					catch (ClassCastException e) {
						dmd = new DomePreferenceData((double[][]) obj);
					}
				else if (o instanceof Float)
					try {
						dmd = new DomePreferenceData((Float[][]) obj);
					}
					catch (ClassCastException e) {
						dmd = new DomePreferenceData((float[][]) obj);
					}
				else {
					throw new IllegalArgumentException("Can't add " + this + " to " + obj);
				}

				//dmd.setUnit(this.getUnit());
				return this.__mul__(dmd);

			}
			catch (IllegalArgumentException e) { // should never happen if we are calling
				System.err.println(e);             // method correctly
				throw new IllegalArgumentException("Can't add " + this + " to " + obj);
			}
			catch (ArrayIndexOutOfBoundsException ex) { // array has zero length
				System.err.println("array has zero length");
				throw new IllegalArgumentException("Can't add " + this + " to " + obj);
			}

		}
		else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
	}

	public Object __div__(Object obj)
	{

		if (obj instanceof DomePreferenceData) {

			throw new IllegalArgumentException("Incompatible type: Can't add " + this + " to " + obj);

		}
		else if (obj instanceof DomeVectorData) {

			throw new IllegalArgumentException("Incompatible type: Can't add " + this + " to " + obj);

		}
		else if (obj.getClass().isArray()) {

			throw new IllegalArgumentException("Incompatible type: Can't add " + this + " to " + obj);

		}
		else if (obj instanceof RealData) {

			DomePreferenceData dmd = new DomePreferenceData((RealData) obj, 1, 1);
			return this.__scale__(dmd.falseInverse());

		}
		else if (obj instanceof IntegerData) {

			DomePreferenceData dmd = new DomePreferenceData((IntegerData) obj, 1, 1);
			return this.__scale__(dmd.falseInverse());

		}
		else if (obj instanceof Integer) {

			DomePreferenceData dmd = new DomePreferenceData((Integer) obj, 1, 1);
			return this.__scale__(dmd.falseInverse());

		}
		else if (obj instanceof Float) {

			DomePreferenceData dmd = new DomePreferenceData(new Double(((Float) obj).floatValue()), 1, 1);
			return this.__scale__(dmd.falseInverse());

		}
		else if (obj instanceof Double) {

			DomePreferenceData dmd = new DomePreferenceData((Double) obj, 1, 1);
			return this.__scale__(dmd.falseInverse());

		}
		else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
	}


	private DomePreferenceData falseInverse()
	{
		DomePreferenceData result = new DomePreferenceData(this);
		result.setUnit(result.getUnit().inv());

		for (int i = 0; i < result.getRowCount(); i++) {
			for (int j = 0; j < result.getColumnCount(); j++) {
				result.setItem(i, j, new Double(1.0 / (this.getItem(i, j)).doubleValue()));
			}
		}
		return result;
	}


	private DomePreferenceData __scale__(DomePreferenceData v)
	{
		DomePreferenceData result = new DomePreferenceData(this);
		double val = v.getItem(0, 0).doubleValue();
		Unit u1 = this.getUnit();
		Unit u2 = v.getUnit();
		Unit u3;

		try {
			u1.convertFrom(1.0, u2);
			u3 = new Unit("S" + u1.toString());
		}
		catch (Exception e) {
			u3 = u1.mul(u2);
		}
		result.setUnit(u3);

		for (int i = 0; i < result.getRowCount(); i++) {
			for (int j = 0; j < result.getColumnCount(); j++) {
				result.setItem(i, j, new Double(val * this.getItem(i, j).doubleValue()));
			}
		}
		return result;
	}

	public void fireValueChange()
	{
		firePropertyChange(VALUE, new Hashtable(), PreferenceDataForXmlRpcUse());
	}

	private Hashtable PreferenceDataForXmlRpcUse()
	{
		Hashtable t = new Hashtable();
		//put data, and unit inside
		t.put("data", data);
		t.put("unit", unit.toString());

		return t;
	}

	public Vector convertUnit(Vector v, Unit oldUnit, Unit newUnit, Number initialV)
	{
		Vector result = (Vector) v.clone();
		for (int i = 0; i < v.size(); i++) {
			if (v.get(i) instanceof Vector) {
				Vector row = (Vector) v.get(i);
				Vector row_result = (Vector) v.get(i);
				for (int j = 0; j < row.size(); j++) {
					if (row.get(j) instanceof Number) {
						double num = ((Number) row.get(j)).doubleValue();
						double num_result = oldUnit.convertFrom(num, newUnit);
						Double d = new Double(num_result);
						if (initialV instanceof Integer)
							row_result.setElementAt(new Integer(d.intValue()), j);
						else
							row_result.setElementAt(d, j);
					}
				}
			}
		}
		return result;
	}

	//assume all vector a vector of vectors
	public boolean compareVector(Vector v1, Vector v2)
	{
		if (v1.size() != v2.size())
			return false;
		else {
			for (int i = 0; i < v1.size(); i++) {
				Vector row_v1 = (Vector) v1.get(i);
				Vector row_v2 = (Vector) v2.get(i);
				if (row_v1.size() != row_v2.size())
					return false;
				else {
					for (int j = 0; j < row_v1.size(); j++) {
						if (row_v1.get(j) instanceof Number && row_v2.get(j) instanceof Number) {
							Number n1 = (Number) row_v1.get(j);
							Number n2 = (Number) row_v2.get(j);
							if (n1.doubleValue() != n2.doubleValue()) return false;
						}
						else {
							return false;
						}
					}
				}
			}
			return true;
		}
	}

    public BooleanData isEmpty()
    {
        return new BooleanData(getRowCount()==0 && getColumnCount() == 0);
    }

    /**
     * isSquare
     * Returns boolean for DomeMatrixData input (Allowable input units = all units)
     * Result has no unit
     */
    public BooleanData isSquare() {
        return new BooleanData(getRowCount() == getColumnCount());
    }
}
