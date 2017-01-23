package mit.cadlab.dome3.util;

import mit.cadlab.dome3.objectmodel.util.Names;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import cern.colt.matrix.DoubleMatrix2D;
import com.Ostermiller.util.SignificantFigures;

/**
 * mit.cadlab.dome3.util.FormatUtils.java
 */
public class FormatUtils
{
    public static final int DEFAULT_SIGNIFICANT_FIGURE = 9;

	public static void sortAlphabeticalOrder(List objects)
	{
		Collections.sort(objects, new AlphabeticalOrder());
	}

	public static String mapToString(HashMap map)
	{
		List nodes = new ArrayList(map.keySet());
		Collections.sort(nodes, new AlphabeticalOrder());
		return mapToStringInOrder(map, nodes);
	}

	public static String mapToString(Hashtable map)
	{
		List nodes = new ArrayList(map.keySet());
		Collections.sort(nodes, new AlphabeticalOrder());
		return mapToStringInOrder(map, nodes);
	}

	public static String mapToStringInOrder(HashMap map, List keyOrder)
	{
		if (map.isEmpty()) {
			return "{ }";
		}
		StringBuffer str = new StringBuffer("{ ");
		String sep = " --> ";
		str.append(Names.getName(keyOrder.get(0)) + sep);
		Object obj = map.get(keyOrder.get(0));
		if (obj instanceof Collection)
			str.append(Names.getNames((Collection) obj));
		else
			str.append(Names.getName(obj));
		for (int i = 1; i < keyOrder.size(); i++) {
			str.append(",\n  " + Names.getName(keyOrder.get(i)) + sep);
			obj = map.get(keyOrder.get(i));
			if (obj instanceof Collection)
				str.append(Names.getNames((Collection) obj));
			else
				str.append(Names.getName(obj));
		}
		str.append(" }");
		return str.toString();
	}

	public static String mapToStringInOrder(Hashtable map, List keyOrder)
	{
		if (map.isEmpty()) {
			return "{ }";
		}
		StringBuffer str = new StringBuffer("{ ");
		String sep = " --> ";
		str.append(Names.getName(keyOrder.get(0)) + sep);
		Object obj = map.get(keyOrder.get(0));
		if (obj instanceof Collection)
			str.append(Names.getNames((Collection) obj));
		else
			str.append(Names.getName(obj));
		for (int i = 1; i < keyOrder.size(); i++) {
			str.append(",\n  " + Names.getName(keyOrder.get(i)) + sep);
			obj = map.get(keyOrder.get(i));
			if (obj instanceof Collection)
				str.append(Names.getNames((Collection) obj));
			else
				str.append(Names.getName(obj));
		}
		str.append(" }");
		return str.toString();
	}

	public static String formatMatrixWithLabels(int[][] matrix, List labels)
	{
		StringBuffer sb = new StringBuffer("  ");
		// top row of labels; get first character only
		Iterator it = labels.iterator();
		while (it.hasNext()) {
			sb.append(it.next().toString().charAt(0) + " ");
		}
		for (int i = 0; i < labels.size(); i++) {
			sb.append("\n" + labels.get(i).toString().charAt(0));
			for (int j = 0; j < labels.size(); j++) {
				sb.append(" " + matrix[i][j]);
			}
		}
		return sb.toString();
	}

    public static String formatMatrixWithLabels(DoubleMatrix2D matrix, List labels)
	{
		StringBuffer sb = new StringBuffer("  ");
		// top row of labels; get first character only
		Iterator it = labels.iterator();
		while (it.hasNext()) {
			sb.append(it.next().toString().charAt(0) + " ");
		}
		for (int i = 0; i < labels.size(); i++) {
			sb.append("\n" + labels.get(i).toString().charAt(0));
			for (int j = 0; j < labels.size(); j++) {
				sb.append(" " + matrix.get(i,j));
			}
		}
		return sb.toString();
	}

	public static class AlphabeticalOrder implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			if (o1 != null && o2 != null) {
				String s1 = Names.getName(o1);
				String s2 = Names.getName(o2);
				return s1.compareTo(s2);
			} else if (o1 == null && o2 == null) {
				return 0;
			} else if (o1 == null) {
				return -1;
			} else { // o2==null and o1!=null
				return 1;
			}
		}
	}

	public static double cleanJavaError(double number)
    /* tested 0.06999999999999
    246999999999999E-4
    24.6999999999999
    20000000000009E-4
    0.060000000000000000001
    6090.0000000000000001
    */
	{
		if (Double.isInfinite(number))
			return number;

		//System.out.println(number);
		char[] charArray = (new Double(number)).toString().toCharArray();
		int startIndex = charArray.length - 2; // starts at the second to last digit
		int count = 0;
		int decimalPointIndex = -1;
		int eIndex = -2;
		boolean hasE = false;
		for (int i = 0; i < charArray.length; i++) {
			if (charArray[i] == 'E') {
				hasE = true;
				eIndex = i;
				startIndex = eIndex - 2;
				break;
			}
			if (charArray[i] == '.')
				decimalPointIndex = i;
		}

		if (decimalPointIndex == -1)
			throw new RuntimeException("the double number, "+ number+ ", doesn't contain a decimal point");

		int index = startIndex;
		if (charArray[startIndex] == '0') {
			while (charArray[index] == '0') {
				index--;
				count++;
			}
			if (count >= 10) {
				String sub = (new String(charArray)).substring(0, index+1);
				double roundedDown = (new Double(sub)).doubleValue();
				if (hasE) {
					roundedDown *=  Math.pow(10, (new Double((new String(charArray)).substring(eIndex + 1, charArray.length))).doubleValue());
				}
				return roundedDown;
			}
			else
				return number;
		}
		else if (charArray[startIndex] == '9') {
			while (charArray[index] == '9') {
				index--;
				count++;
			}
			if (count >= 10) {
				String sub = (new String(charArray)).substring(0, index + 1); // get number before decimal point (1.999999 would give 1)
                int pow = index - decimalPointIndex;
				double subVal = (new Double(sub)).doubleValue()* Math.pow(10, pow);
                double roundedUp = (subVal + 1.0)/ Math.pow(10, pow); // thus, 1.9999999 would become 2.0
				if (hasE) {
                    roundedUp = new Double((new Double(roundedUp)).toString() + "E"+(new String(charArray)).substring(eIndex + 1, charArray.length)).doubleValue();
					//roundedUp *=  Math.pow(10,(new Double((new String(charArray)).substring(eIndex + 1, charArray.length))).doubleValue());
                }
                return roundedUp;
			}
			else
				return number;
		}
		else
			return number;
	}

    public static String formatSigFig(double number) {
        if (number==0)
            return "0.0";
        return SignificantFigures.format(number,DEFAULT_SIGNIFICANT_FIGURE);
    }

    /**
     * warning: used only for a linear one-to-one map
     * @param map
     * @return
     */
    public static HashMap reverseLinearMap(HashMap map) {
        HashMap reverseMap = new HashMap();
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            reverseMap.put(map.get(o),o);
        }
        if (map.size() != reverseMap.size())
            throw new UnsupportedOperationException("FormatUtils:reverseLinearMap: map is not linear");
        return reverseMap;
    }
}
