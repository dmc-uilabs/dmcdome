package mit.cadlab.dome3.util;

import net.iharder.Base64;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import mit.cadlab.dome3.network.client.functions.Vectors;

// to do: move to DCollections?

public class Converters
{

	/**
	 * Each non-null item is represented by its toString value.
	 * null items are null in array
	 *
	 * @param l a <code>List</code> value
	 * @return a <code>String[]</code> value
	 */
	public static String[] toStringArray(List l)
	{
		String[] objStrings = new String[l.size()];
		Iterator it = l.iterator();
		for (int i = 0; it.hasNext(); ++i) {
			Object obj = it.next();
			objStrings[i] = (obj == null) ? null : obj.toString();
		}
		return objStrings;
	}

	/**
	 * Each Number instance is represented by its intValue
	 * Each non-null item is represented by parsing its toString value.
	 *
	 * @param l a <code>List</code> value
	 * @return a <code>int[]</code> value
	 * @exception NullPointerException any item is null
	 * @exception NumberFormatException string of item can not be parsed into int
	 */
	public static int[] toIntArray(List l)
	{
		int[] objInts = new int[l.size()];
		Iterator it = l.iterator();
		for (int i = 0; it.hasNext(); ++i) {
			Object obj = it.next();
			if (obj == null)
				throw new NullPointerException();
			else if (obj instanceof Number)
				objInts[i] = ((Number) obj).intValue();
			else
				objInts[i] = Integer.parseInt(obj.toString());
		}
		return objInts;
	}

	/**
	 * Converts List to given array type (can cast result)
	 *
	 * @param l a <code>List</code> to convert to an array
	 * @param arrayComponentType a <code>Class</code> type of array
	 * @return an <code>Object[]</code> result
	 * @exception ArrayStoreException the arrayComponentType is not a supertype
	 * of the runtime type of every element in this List.
	 */
	public static Object[] toArray(List l, Class arrayComponentType)
	{
		Object[] a = (Object[]) java.lang.reflect.Array.newInstance(arrayComponentType,
		                                                            l.size());
		return l.toArray(a);
	}

	/**
	 * Creates new list without duplicate items,
	 * preserving order of first occurrence of each item
	 * in original list.
	 *
	 * @param l a <code>List</code> value
	 * @return a <code>List</code> value
	 */
	public static List toOrderedSet(List l)
	{
		List setList = new ArrayList();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (!setList.contains(obj))
				setList.add(obj);
		}
		return setList;
	}

	/**
	 * Creates new list without duplicate items,
	 * preserving order of first occurrence of each item
	 * in array.
	 *
	 * @param objs an <code>Object[]</code> value
	 * @return a <code>List</code> value
	 */
	public static List toOrderedSet(Object[] objs)
	{
		List setList = new ArrayList();
		for (int i = 0; i < objs.length; ++i) {
			Object obj = objs[i];
			if (!setList.contains(obj))
				setList.add(obj);
		}
		return setList;
	}

	public static List toList(int[] a)
	{
		List l = new ArrayList();
		for (int i = 0; i < a.length; ++i)
			l.add(new Integer(a[i]));
		return l;
	}

	public static List toList(double[] a)
	{
		List l = new ArrayList();
		for (int i = 0; i < a.length; ++i)
			l.add(new Double(a[i]));
		return l;
	}

	public static int[][] doubleArrayToIntArray(double[][] dArray)
	{
		int row = dArray.length;
		int col = dArray[1].length;
		int[][] intArray = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				intArray[i][j] = (int) dArray[i][j];
			}
		}
		return intArray;
	}

	public static String byteArrayToString(byte[] bytes)
	{
		return Base64.encodeBytes(bytes);
	}

	public static byte[] stringToByteArray(String byteString)
	{
		return Base64.decode(byteString);
	}

    /**
     *  parse a string representation of a vector (e.g. 1 2 3) into a vector
     */
    public static Vector parseVector(String str) {
        if (str.trim().equals(""))
            return new Vector();
        List items = Regex.split(" ", str.trim());
        Vector vals = new Vector();
        for (int i = 0; i < items.size(); i++) {
            String s = ((String) items.get(i)).trim();
            vals.add(s.equals("") ? new Double(0) : new Double(s));
        }
        return vals;
    }

    /**
     *  parse a string representation of a string vector (e.g. 1: 2: 3) into a vector
     */
    public static Vector parseStringVector(String str) {
        if (str.trim().equals(""))
            return new Vector();
        List items = Regex.split(":", str.trim());
        Vector vals = new Vector();
        for (int i = 0; i < items.size(); i++) {
            vals.add(items.get(i));
        }
        return vals;
    }

    /**
     *  parse a string representation of a matrix (e.g. 1 2 3; 4 5 6) into a matrix (a vector of vectors)
     */
    public static Vector parseMatrix(String str) {
        if (str.trim().equals(""))
            return Vectors.create(new Vector());
        Vector mat = new Vector();
        List rows = Regex.split(";", str.trim());
        for (int i = 0; i < rows.size(); i++) {
            String r = ((String) rows.get(i)).trim();
            Vector row = new Vector();
            if (!r.equals("")) {
                List items = Regex.split(" ", r);
                for (int j = 0; j < items.size(); j++) {
                    String s = ((String) items.get(j)).trim();
                    row.add(s.equals("") ? new Double(0) : new Double(s));
                }
            }
            mat.add(row);
        }
        return mat;
    }

    /**
     * convert a vector to a string of format: item1 item2 item3
     */
    public static String vectorToString(Vector v) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < v.size(); i++) {
            Object item = v.get(i);
            sb.append(item == null ? "0" : item).append(" ");
        }
        return sb.toString();
    }

    /**
     * convert a string vector to a string of format: item1: item2: item3
     */
    public static String stringVectorToString(Vector v) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < v.size(); i++) {
            Object item = v.get(i);
            sb.append(item == null ? "" : item).append(i==v.size()-1 ? "" : ": ");
        }
        return sb.toString();
    }

    /**
     * convert a matrix to a string of format: item1 item2 item3 ;item4 item5 item6
     */
    public static String matrixToString(Vector v) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < v.size(); i++) {
            Vector row = (Vector) v.get(i);
            if (row==null)
                sb.append("0");
            else {
                for (int j = 0; j < row.size(); j++) {
                    Object item = row.get(j);
                    sb.append(item == null ? "0" : item).append(" ");
                }
            }
            if (i != v.size()-1)
                sb.append(";");
        }
        return sb.toString();
    }
}
