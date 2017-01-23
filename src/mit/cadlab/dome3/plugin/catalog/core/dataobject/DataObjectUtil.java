package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.ui.UIUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2006. 3. 9.
 */
public class DataObjectUtil {

    public static void main(String[] args) {
        UIUtil.initUnit();

        List paramNameList = new ArrayList();
        paramNameList.add("aaaaa");
        paramNameList.add("aa");
        paramNameList.add("bbbb");
        paramNameList.add("aaaaaaaa");
        paramNameList.add("aaa");
        System.out.println("before= " + paramNameList);
        Collections.sort(paramNameList, new Comparator() {
            public int compare(Object left, Object right) {
                return ((String) left).length() - ((String) right).length();
            }
        });
        System.out.println("after= " + paramNameList);

//        CVector b = new CVector(CConstant.REAL_DATA_TYPE, 0);
//        b.setDouble(3, 3);
//        System.out.println(b);


        StringBuffer paramNamePattern = new StringBuffer("("); // 1 match param name
        paramNamePattern.append("itf\\.Y").append(')');
        String openMethodParenPattern = "((?:real|integer|vector|matrix)\\()"; // 2 open method parenthesis
        String openParenPattern = "(\\()"; // 3 open grouping parenthesis
        String closeParenPattern = "(\\))"; // 4 close grouping parenthesis or method parenthesis
        String operatorPattern = "([+\\-*/)\\^])"; // 5 operator
        String intPattern = "([0-9]+)"; // 6 match integer
        String realPattern = "([0-9.]+)"; // 7 match real
        String openBracketPattern = "(\\[)"; // 8 open bracket
        String closeBrackertPattern = "(\\])"; // 9 close bracket
        String etcPattern = "(\\S+)"; // 10 etc string

        String patternMerged = paramNamePattern + "|" + openMethodParenPattern + "|" + openParenPattern + "|" + closeParenPattern + "|" + operatorPattern+ "|" + intPattern + "|" + realPattern + "|" + openBracketPattern + "|" + closeBrackertPattern + "|" + etcPattern;

        StringBuffer convertedMappingScript = new StringBuffer();
        Pattern objectizePattern = Pattern.compile(patternMerged);
        Matcher objectizeMatcher = objectizePattern.matcher("3 + [3 4] + 3 + itf.A");
        //Matcher objectizeMatcher = objectizePattern.matcher("itf.Y + 3 + [2 3] + ");
        int parentOpenCount = 0;
        int bracketOpenCount = 0;
        boolean operatorOpen = false;
        while (objectizeMatcher.find()) {
            if (objectizeMatcher.group(1) != null) {
                objectizeMatcher.appendReplacement(convertedMappingScript, "context.getDataObject(\"$1\")");
                if (parentOpenCount == 0) {
                    operatorOpen = false; // pop this
                }
            } else if (objectizeMatcher.group(2) != null) {
                objectizeMatcher.appendReplacement(convertedMappingScript, "$2");
                parentOpenCount++;
            } else if (objectizeMatcher.group(3) != null) {
                objectizeMatcher.appendReplacement(convertedMappingScript, "$3");
                parentOpenCount++;
            } else if (objectizeMatcher.group(4) != null) {
                objectizeMatcher.appendReplacement(convertedMappingScript, "$4");
                parentOpenCount--;
                if (parentOpenCount == 0) {
                    operatorOpen = false; // pop this
                }
            } else if (objectizeMatcher.group(5) != null) {
                objectizeMatcher.appendReplacement(convertedMappingScript, "$5");
                operatorOpen = true;
            } else if (objectizeMatcher.group(6) != null) { // real
                if (bracketOpenCount == 0) {
                    objectizeMatcher.appendReplacement(convertedMappingScript, "real(\"$7\")");
                } else {
                    objectizeMatcher.appendReplacement(convertedMappingScript, "$7");
                }
            } else if (objectizeMatcher.group(7) != null) { // integer
                if (bracketOpenCount == 0) {
                    objectizeMatcher.appendReplacement(convertedMappingScript, "integer(\"$7\")");
                } else {
                    objectizeMatcher.appendReplacement(convertedMappingScript, "$7");
                }
            } else if (objectizeMatcher.group(8) != null) { // bracket open
                bracketOpenCount++;
            } else if (objectizeMatcher.group(9) != null) { // bracket close
                bracketOpenCount--;
                if (bracketOpenCount == 0) {
                    operatorOpen = false; // pop this
                }
            } else if (objectizeMatcher.group(10) != null) { // etc

                if (parentOpenCount == 0) {
                    operatorOpen = false; // pop this
                }
            }
            System.out.println("objectizeMatcher.group(1) = " + objectizeMatcher.group(1));
            System.out.println("objectizeMatcher.group(2) = " + objectizeMatcher.group(2));
            System.out.println("objectizeMatcher.group(3) = " + objectizeMatcher.group(3));
        }


//        System.out.println(objectizeMatcher.find());
//        System.out.println("objectizeMatcher.group(1) = " + objectizeMatcher.group(1));
//        System.out.println("objectizeMatcher.group(2) = " + objectizeMatcher.group(2));
//        System.out.println("objectizeMatcher.group(3) = " + objectizeMatcher.group(3));

//        if (true) return;

        String paramName = "itf.param A";
        paramName = paramName.replaceAll("\\.", "\\\\.");
        System.out.println(paramName);

        Pattern p = Pattern.compile("cat");
        Matcher m = p.matcher("one cat two cats in the yard");
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "dog");
        }
        m.appendTail(sb);
        System.out.println(sb.toString());

        CInteger second = new CInteger(3, "mm");
        CInteger first = new CInteger(2, "mm");
        System.out.println(pow(first, second));

        String mappingScript = "context.getDataObject(\"itf.param A\") + [4.5 4.5 6.6; 7.0 8 9.9;;] + context.getDataObject(\"itf.param A\") + [3 4 5; 7 8 9]";
        Pattern matPattern = Pattern.compile("(\\[(?:[0-9\\., ]*;{0,1})*\\])");
        Matcher matMatcher = matPattern.matcher(mappingScript);
        while (matMatcher.find()) {
            System.out.println(matMatcher.group(1) + " " + matMatcher.group(1).length() + ":" + matMatcher.start(1) + " ~ " + matMatcher.end(1));
        }

        String vecMappingScript = "context.getDataObject(\"itf.param A\") + [4.5 4.5 6.6] + context.getDataObject(\"itf.param A\") + [3 4 5]";
        Pattern vectPattern = Pattern.compile("(\\[[0-9\\., ]*\\])");
        Matcher vectMatcher = vectPattern.matcher(vecMappingScript);
        while (vectMatcher.find()) {
            System.out.println(vectMatcher.group(1));
        }

        /* how to create empty vector */
        System.out.println("empty vector = " + createVector("[]"));
        System.out.println("empty vector = " + createVector("[]|Real"));
        System.out.println("empty vector = " + createVector("[]|Integer"));


        /* how to create real vector */
        System.out.println("real vector = " + createMatrix("[4.0 3.5 5.0]|Real"));
        System.out.println("real vector = " + createMatrix("[4.0 3.5 5.0]")); // data type is determined by the existence of a dot character
        System.out.println("real vector = " + createMatrix("[4.0 3.5 5.0]", CConstant.REAL_DATA_TYPE));
        System.out.println("real vector = " + createMatrix("4.0 3.5 5.0"));

        /* how to create integer vector */
        System.out.println("integer vector = " + createMatrix("[20 30 40]|Integer"));
        System.out.println("integer vector = " + createMatrix("[20 30 40]"));
        System.out.println("integer vector = " + createMatrix("[20 30 40]", CConstant.INTEGER_DATA_TYPE));
        System.out.println("integer vector = " + createMatrix("20 30 40"));

        /* how to convert a matrix into a text */
        List realVector= createVector("[4.0 3.5 5.0]|Real");
        List integerVector = createVector("[20 30 40]|Integer");
        System.out.println("vector string=" + toVectorString(realVector));
        System.out.println("vector string=" + toVectorString(realVector, CConstant.REAL_DATA_TYPE));
        System.out.println("vector string=" + toVectorString(integerVector));
        System.out.println("vector string=" + toVectorString(integerVector, CConstant.INTEGER_DATA_TYPE));

        /* how to create empty matrix */
        System.out.println("empty matrix = " + createMatrix("[]"));
        System.out.println("empty matrix = " + createMatrix("[]|Real"));
        System.out.println("empty matrix = " + createMatrix("[]|Integer"));

        /* how to create real matrix */
        System.out.println("real matrix = " + createMatrix("[4.0 3.5 5.0; 1.2 1.2 1.8]|Real"));
        System.out.println("real matrix = " + createMatrix("[4.0 3.5 5.0; 1.2 1.2 1.8]")); // data type is determined by the existence of a dot character
        System.out.println("real matrix = " + createMatrix("[4.0 3.5 5.0; 1.2 1.2 1.8]", CConstant.REAL_DATA_TYPE));
        System.out.println("real matrix = " + createMatrix("4.0 3.5 5.0; 1.2 1.2 1.8"));

        /* how to create integer matrix */
        System.out.println("integer matrix = " + createMatrix("[20 30 40; 10 10 10]|Integer"));
        System.out.println("integer matrix = " + createMatrix("[20 30 40; 10 10 10]"));
        System.out.println("integer matrix = " + createMatrix("[20 30 40; 10 10 10]", CConstant.INTEGER_DATA_TYPE));
        System.out.println("integer matrix = " + createMatrix("20 30 40; 10 10 10"));

        /* how to convert a matrix into a text */
        List realMatrix = createMatrix("[4.0 3.5 5.0; 1.2 1.2 1.8]|Real");
        List integerMatrix = createMatrix("[20 30 40; 10 10 10]|Integer");
        System.out.println("matrix string=" + toMatrixString(realMatrix));
        System.out.println("matrix string=" + toMatrixString(realMatrix, CConstant.REAL_DATA_TYPE));
        System.out.println("matrix string=" + toMatrixString(integerMatrix));
        System.out.println("matrix string=" + toMatrixString(integerMatrix, CConstant.INTEGER_DATA_TYPE));

        /* how to convert a matrix into a text */
        List enumList = createEnumList("my first=1, my second=2, my third=3|2,Integer");
        for (int i = 0; i < enumList.size(); i++) {
            System.out.println(Arrays.asList((Object[]) enumList.get(i)));
        }

        List originIntegerVector = createVector("[20 30 40]", "integer");
        List sameRealVector = createVector("[20 30 40]", "real");
        List sameIntegerVector = createVector("[20 30 40]", "integer");
        System.out.println("isEqualVector : " + originIntegerVector + " and " + sameIntegerVector + " are equal:" + isEqualVectorValue(originIntegerVector, sameIntegerVector));
        System.out.println("isEqualVector : " + originIntegerVector + " and " + sameRealVector + " are equal:" + isEqualVectorValue(originIntegerVector, sameRealVector));

        List originIntegerMatrix = createMatrix("[20 30 40; 30 40 50]", "integer");
        List sameRealMatrix = createMatrix("[20 30 40; 30 40 50]", "real");
        List sameIntegerMatrix = createMatrix("[20 30 40; 30 40 50]", "integer");
        System.out.println("isEqualMatrix : " + originIntegerMatrix + " and " + sameIntegerMatrix + " are equal:" + isEqualMatrixValue(originIntegerMatrix, sameIntegerMatrix));
        System.out.println("isEqualMatrix : " + originIntegerMatrix + " and " + sameRealMatrix + " are equal:" + isEqualMatrixValue(originIntegerMatrix, sameRealMatrix));
    }

    private static boolean isValidMatrix(List matrixValue) {
        if (matrixValue.size() > 0 && matrixValue.get(0) instanceof List) {
            List row = (List) matrixValue.get(0);
            if (row.size() == 0 || (row.size() > 0 && row.get(0) instanceof Number)) {
                return true;
            } else {
                return false;
            }
        } else if (matrixValue.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isValidVector(List vectorValue) {
        if (vectorValue.size() == 0 || (vectorValue.size() > 0 && vectorValue.get(0) instanceof Number)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * matrixValue is given as a list of numbers contained in a list
     * matrixValue is converted into a text representation such as "[4.0 3.5 5.0; 1.2 1.2 1.8]|Real"
     * data type, which is either Real or Integer, is specified explicitly.
     */
    public static String toMatrixString(List matrixValue, String dataType) {
        if (! isValidMatrix(matrixValue)) {
            throw new RuntimeException("matrix should be a List of a List of a Number");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < matrixValue.size(); i++) {
            List row = (List) matrixValue.get(i);
            for (int j = 0; j < row.size(); j++) {
                Number num = (Number) row.get(j);
                sb.append(' ').append(num);
            }
            sb.append(';');
        }

        if (sb.length() > 0) {
            sb.setCharAt(0, '[');
            sb.setCharAt(sb.length() - 1, ']');
        } else {
            sb.append('[').append(']');
        }

        sb.append('|').append(dataType);
        return sb.toString();
    }

    /**
     * vectorValue is given as a list of numbers
     * vectorValue is converted into a text representation such as "[4.0 3.5 5.0]|Real"
     * data type, which is either Real or Integer, is specified explicitly.
     */
    public static String toVectorString(List vectorValue, String dataType) {
        if (! isValidVector(vectorValue)) {
            throw new RuntimeException("vector should be a List of a Number");
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < vectorValue.size(); i++) {
            Number num = (Number) vectorValue.get(i);
            sb.append(' ').append(num);
        }

        if (sb.length() > 0) {
            sb.setCharAt(0, '[');
            sb.append(']');
        } else {
            sb.append('[').append(']');
        }

        sb.append('|').append(dataType);
        return sb.toString();
    }

    /**
     * matrixValue is given as a list of numbers contained in a list
     * matrixValue is converted into a text representation such as "[[4.0 3.5 5.0] [1.2 1.2 1.8]]|Real"
     * data type, which is either Real or Integer, is determined by looking at the data type of the first element in the matrix.
     */
    public static String toMatrixString(List matrixValue) {
        String dataType = CConstant.REAL_DATA_TYPE;
        if (matrixValue.size() > 0 && matrixValue.get(0) instanceof List) {
            List firstRow = (List) matrixValue.get(0);
            if (firstRow.size() > 0 && firstRow.get(0) instanceof Integer) {
                dataType = CConstant.INTEGER_DATA_TYPE;
            }
        }

        return toMatrixString(matrixValue, dataType);
    }

    /** extract data type or decide data type from text representation of matrix
     * "Real" returned from "[4.0 3.5 5.0; 1.2 1.2 1.8]|Real"
     * "Real" returned from "[4.0 3.5 5.0; 1.2 1.2 1.8]"
     * "Integer" returned from "[20 30 40; 10 10 10]|Integer"
     * "Integer" returned from "[20 30 40; 10 10 10]"
     */
    public static String getDataType(String matrixStrAndDataType) {
        int separatorIdx = matrixStrAndDataType.indexOf('|');
        int dotIdx = matrixStrAndDataType.indexOf('.');
        if (separatorIdx == -1) {
            if (dotIdx == -1) {
                return CConstant.INTEGER_DATA_TYPE;
            } else {
                return CConstant.REAL_DATA_TYPE;
            }
        } else {
            return matrixStrAndDataType.substring(separatorIdx + 1);
        }
    }

    /**
     * "[4.0 3.5 5.0; 1.2 1.2 1.8]|Real" or "[20 30 40; 10 10 10]|Integer" is parsed into
     * a List of a List having Double or Integer objects as an element
     */
    public static List createMatrix(String matrixStrAndDataType) {
        int separatorIdx = matrixStrAndDataType.indexOf('|');
        int dotIdx = matrixStrAndDataType.indexOf('.');
        if (separatorIdx == -1) {
            if (dotIdx == -1) {
                String matrixStr = matrixStrAndDataType;
                return createMatrix(matrixStr, CConstant.INTEGER_DATA_TYPE);
            } else {
                String matrixStr = matrixStrAndDataType;
                return createMatrix(matrixStr, CConstant.REAL_DATA_TYPE);
            }
        } else {
            String matrixStr = matrixStrAndDataType.substring(0, separatorIdx);
            String dataType = matrixStrAndDataType.substring(separatorIdx + 1);
            return createMatrix(matrixStr, dataType);
        }
    }

    /** parse matlab style matrix string to construct a DomeMatrixData object
     * (ex) createNumberRowList("[[20 30 40] [10 10 10]]", "Real");
     * (ex) createNumberRowList("[[20 30 40] [10 10 10]]", "Integer");
     * dataType is either "Real" or "Integer" */
    public static List createMatrix(String matrixStr, String dataType) {
        //Pattern rowPattern = Pattern.compile("\\[([0-9\\., ]*)\\]");
        Pattern rowPattern = Pattern.compile("([0-9\\., ]*)(?:[;\\]]|$)");
        Matcher rowMatcher = rowPattern.matcher(matrixStr);
        List rowList = new Vector(); // changed from new ArrayList()
        if (CConstant.REAL_DATA_TYPE.equalsIgnoreCase(dataType)) {
            while (rowMatcher.find()) {
                String rowStr = rowMatcher.group(1);
                if (! "".equals(rowStr.trim())) {
                    rowList.add(createDoubleList(rowStr));
                }
            }
//
//            Double[][] doubleMatrix = null;
//            if (rowList.size() == 0) {
//                DomeMatrixData ret = new DomeMatrixData();
//                ret.setUnit(new Unit(unit));
//                return ret;
//            } else {
//                int columnSize = ((Double[]) rowList.get(0)).length;
//                doubleMatrix = (Double[][]) rowList.toArray(new Double[rowList.size()][columnSize]);
//                DomeMatrixData ret = new DomeMatrixData(doubleMatrix);
//                ret.setUnit(new Unit(unit));
//                return ret;
//            }
        } else if ("Integer".equalsIgnoreCase(dataType)) {
            while (rowMatcher.find()) {
                String rowStr = rowMatcher.group(1);
                if (! "".equals(rowStr.trim())) {
                    rowList.add(createIntegerList(rowStr));
                }
            }
//            Integer[][] integerMatrix = null;
//            if (rowList.size() == 0) {
//                DomeMatrixData ret = new DomeMatrixData();
//                ret.setUnit(new Unit(unit));
//                return ret;
//            } else {
//                int columnSize = ((Integer[]) rowList.get(0)).length;
//                integerMatrix = (Integer[][]) rowList.toArray(new Integer[rowList.size()][columnSize]);
//                DomeMatrixData ret = new DomeMatrixData(integerMatrix);
//                ret.setUnit(new Unit(unit));
//                return ret;
//            }
        }
        return rowList;
    }

    /* turn "20 30" or "20, 30" into [ 20.0, 30.0 ] */
    private static List createDoubleList(String rowStr) {
        StringTokenizer st = new StringTokenizer(rowStr, "[] ,", false);
        List valueList = new Vector(); // changed from new ArrayList()
        while(st.hasMoreTokens()) {
            String value = st.nextToken();
            if (! "".equals(value)) {
                valueList.add(new Double(value));
            } else {
                valueList.add(new Double(0));
            }
        }
        return valueList;
    }

    /** returns int [] { row size, column size } of the given matrix data */
    public static int[] getMatrixSize(List rowList) {
        if (rowList == null) {
            return new int[] { 0, 0 };
        }
        if (rowList.size() > 0) {
            return new int[] { rowList.size(), ((List) rowList.get(0)).size() };
        } else {
            return new int[] { 0, 0 };
        }
    }

    /**
     * vectorValue is given as a list of numbers contained in a list
     * vectorValue is converted into a text representation such as "[[4.0 3.5 5.0] [1.2 1.2 1.8]]|Real"
     * data type, which is either Real or Integer, is determined by looking at the data type of the first element in the vector.
     */
    public static String toVectorString(List vectorValue) {
        String dataType = CConstant.REAL_DATA_TYPE;
        if (vectorValue.size() > 0 && vectorValue.get(0) instanceof Integer) {
            dataType = CConstant.INTEGER_DATA_TYPE;
        }

        return toVectorString(vectorValue, dataType);
    }

    /** convert "[2 3]|Integer" into "[2 3]. this method used for cleaner display */
    public static String removeDataType(String vectorOrMatrixStr) {
        int separatorIdx = vectorOrMatrixStr.indexOf("|");
        if (separatorIdx == -1) {
            return vectorOrMatrixStr;
        } else {
            return vectorOrMatrixStr.substring(0, separatorIdx);
        }
    }

    /**
     * "[3.0 4.5 5.0]|Real" or "[20 30 40]|Integer" is parsed into
     * a List of a List having Double or Integer objects as an element
     */
    public static List createVector(String vectorStrAndDataType) {
        int separatorIdx = vectorStrAndDataType.indexOf('|');
        int dotIdx = vectorStrAndDataType.indexOf('.');
        if (separatorIdx == -1) {
            if (dotIdx == -1) {
                String vectorStr = vectorStrAndDataType;
                return createVector(vectorStr, CConstant.INTEGER_DATA_TYPE);
            } else {
                String vectorStr = vectorStrAndDataType;
                return createVector(vectorStr, CConstant.REAL_DATA_TYPE);
            }
        } else {
            String vectorStr = vectorStrAndDataType.substring(0, separatorIdx);
            String dataType = vectorStrAndDataType.substring(separatorIdx + 1);
            return createVector(vectorStr, dataType);
        }
    }

    /**
     * convert a string such as "20 30" or "[20 30]" into a List of numbers [ 20.0, 30.0 ] or [ 20, 30 ]
     * dataType is either Real or Integer, defined in CConstant.REAL_DATA_TYPE and CConstant.INTEGER_DATA_TYPE
     */
    public static List createVector(String vectorStr, String dataType) {
        if (CConstant.REAL_DATA_TYPE.equalsIgnoreCase(dataType)) {
            return createDoubleList(vectorStr);
        } else {
            return createIntegerList(vectorStr);
        }
    }

    /** used to compare values of two matrix */
    public static boolean isEqualMatrixValue(List leftNumberRowList, List rightNumberRowList) {
        if (leftNumberRowList == rightNumberRowList) {
            return true;
        }

        if (leftNumberRowList.size() != rightNumberRowList.size()) {
            return false;
        }

        if (leftNumberRowList.size() > 0) {
            List leftFirstRow = (List) leftNumberRowList.get(0);
            List rightFirstRow = (List) rightNumberRowList.get(0);
            if (leftFirstRow.size() != rightFirstRow.size()) {
                return false;
            }
        }

        for (int i = 0; i < leftNumberRowList.size(); i++) {
            List leftNumberRow = (List) leftNumberRowList.get(i);
            List rightNumberRow = (List) rightNumberRowList.get(i);

            if (! isEqualVectorValue(leftNumberRow, rightNumberRow)) {
                return false;
            }
        }
        return true;
    }

    /** used to compare values of two vector */
    public static boolean isEqualVectorValue(List leftNumberList, List rightNumberList) {
        if (leftNumberList == rightNumberList) {
            return true;
        }

        if (leftNumberList.size() != rightNumberList.size()) {
            return false;
        }

        for (int i = 0; i < leftNumberList.size(); i++) {
            if ((leftNumberList.get(i) instanceof Integer && rightNumberList.get(i) instanceof Integer)
                    || (leftNumberList.get(i) instanceof Double && rightNumberList.get(i) instanceof Double)) {
                if (! leftNumberList.get(i).equals(rightNumberList.get(i))) {
                    return false;
                }
            } else if (leftNumberList.get(i) instanceof Number && rightNumberList.get(i) instanceof Number) {
                if (((Number) leftNumberList.get(i)).doubleValue() != ((Number) rightNumberList.get(i)).doubleValue()) {
                    return false;
                }
            }
        }
        return true;
    }

//    /* turn "20 30" or "20, 30" into { 20.0, 30.0 } */
//    private static Double[] createDoubleArray(String rowStr) {
//        StringTokenizer st = new StringTokenizer(rowStr, " ,", false);
//        List valueList = new Vector(); // changed from new ArrayList()
//        while(st.hasMoreTokens()) {
//            String value = st.nextToken();
//            if (! "".equals(value)) {
//                valueList.add(new Double(value));
//            } else {
//                valueList.add(new Double(0));
//            }
//        }
//        return (Double[]) valueList.toArray(new Double[valueList.size()]);
//    }

    /* turn "20 30" or "20, 30" into [ 20, 30 ] */
    private static List createIntegerList(String rowStr) {
        StringTokenizer st = new StringTokenizer(rowStr, "[] ,", false);
        List valueList = new Vector(); // changed from new ArrayList()
        while(st.hasMoreTokens()) {
            String value = st.nextToken();
            if (! "".equals(value)) {
                valueList.add(new Integer(value));
            } else {
                valueList.add(new Integer(0));
            }
        }
        return valueList;
    }

//    /* turn "20 30" or "20, 30" into { 20, 30 } */
//    private static Integer[] createIntegerArray(String rowStr) {
//        StringTokenizer st = new StringTokenizer(rowStr, " ,", false);
//        List valueList = new Vector(); // changed from new ArrayList()
//        while(st.hasMoreTokens()) {
//            String value = st.nextToken();
//            if (! "".equals(value)) {
//                valueList.add(new Integer(value));
//            } else {
//                valueList.add(new Integer(0));
//            }
//        }
//        return (Integer[]) valueList.toArray(new Integer[valueList.size()]);
//    }

    /**
     * get selectedIndex of Enum List of { String name, Object value, Boolean selected }
     * if no row is selected, -1 returned.
     */
    public static int getSelectedIndex(List enumList) {
        for (int i = 0; i < enumList.size(); i++) {
            Object[] enumItem = (Object[]) enumList.get(i);
            if (((Boolean) enumItem[2]).booleanValue()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * change the selectedIndex of Enum List of { String name, Object value, Boolean selected } to a given selectedIdx
     */
    public static boolean setSelectedIndex(int selectedIdx, List enumList) {
        boolean isSuccess = false;
        for (int i = 0; i < enumList.size(); i++) {
            Object[] enumItem = (Object[]) enumList.get(i);
            if (i == selectedIdx) {
                enumItem[2] = Boolean.TRUE;
                isSuccess = true;
            } else {
                enumItem[2] = Boolean.FALSE;
            }
        }
        return isSuccess;
    }

    /**
     * clone (=do a deep-copy so that any change made to the copy would not affect the original) of Enum List of { String name, Object value, Boolean selected } to a given selectedIdx
     * this method assumes that the value object is one of somewhat special classes such as String, Integer or Double:
     * those classes cannot change their value without changing reference, so we don't have to create a clone of the classes.
     */
    public static List cloneEnumList(List enumList) {
        List cloneList = new Vector(); // changed from new ArrayList()
        for (int i = 0; i < enumList.size(); i++) {
            Object[] enumItem = (Object[]) enumList.get(i);
            cloneList.add(new Object[] { enumItem[0], enumItem[1], enumItem[2] });
        }
        return cloneList;
    }

    /**
     * clone (=do a deep-copy so that any change made to the copy would not affect the original) of Row List (=List of List of Numbers used for storing Matrix data)
     * this method assumes that the value object is one of somewhat special classes such as String, Integer or Double:
     * those classes cannot change their value without changing reference, so we don't have to create a clone of the classes.
     */
    public static List cloneRowList(List rowList) {
        List clonedRowList = new ArrayList();
        for (int i = 0; i < rowList.size(); i++) {
            List row = new ArrayList();
            row.addAll((List) rowList.get(i));
            clonedRowList.add(row);
        }
        return clonedRowList;
    }

    /**
     * change the selectedIndex of Enum List of { String name, Object value, Boolean selected } to an index corresponding to a given enum name
     */
    public static boolean setSelectedIndex(String enumName, List enumList) {
        boolean isSuccess = false;
        for (int i = 0; i < enumList.size(); i++) {
            Object[] enumItem = (Object[]) enumList.get(i);
            if (enumName.equals(enumItem[0])) {
                enumItem[2] = Boolean.TRUE;
                isSuccess = true;
            } else {
                enumItem[2] = Boolean.FALSE;
            }
        }
        if (! isSuccess) throw new RuntimeException("no match for the enumName is found in the enumList: " + enumName + " / " + getEnumString(enumList));
        return isSuccess;
    }

    public static String getEnumString(List enumList) {
        boolean isInteger = false;
        int selectedIdx = -1;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < enumList.size(); i++) {
            Object[] enumPair = (Object[]) enumList.get(i);
            sb.append(enumPair[0]).append("=").append(enumPair[1]);
            if (i != (enumList.size() - 1)) {
                sb.append(",");
            }
            if (enumPair[1] instanceof Integer) {
                isInteger = true;
            }
            if (((Boolean) enumPair[2]).booleanValue()) {
                selectedIdx = i;
            }
        }
        return sb.toString() + "|" + selectedIdx + "," + (isInteger ? "Integer" : "Real");
    }

    /** if selected index is -1, runtime exception is thrown */
    public static String getEnumName(List enumList, int selectedIdx) {
        if (selectedIdx <= -1 || selectedIdx >= enumList.size()) {
            throw new IllegalArgumentException("Given selected index '" + selectedIdx + "' is invalid. It must be between 0 and " + (enumList.size() - 1) + ".");
        }
        return (String) ((Object[]) enumList.get(selectedIdx)) [0];
    }

    public static Object getEnumValue(List enumList, int selectedIdx) {
        return ((Object[]) enumList.get(selectedIdx)) [1];
    }

    public static List createEnumList(List names, List values, int selectedIdx) {
        List enumList = new ArrayList();
        for (int i = 0; i < names.size(); i++) {
            enumList.add(new Object[] { names.get(i) , values.get(i), new Boolean(i == selectedIdx) });
        }
        return enumList;
    }

    /**
     * parse "my first=1, my second=2, my third=3|-1, Integer" or "my first=1, my second=2, my third=3|-1"
     * to create Enum List, a List of { String name, Object value, Boolean selected } */
    public static List createEnumList(String enumStr) {
        int selectedIdx = -1;
        String typeStr = CConstant.REAL_DATA_TYPE;
        String enumPairListStr = enumStr;
        if (enumStr.indexOf("|") != -1) {
            String selectedIdxAndTypeStr = enumStr.substring(enumStr.indexOf("|") + 1);
            String selectedIdxStr = selectedIdxAndTypeStr.trim();
            if (selectedIdxAndTypeStr.indexOf(",") != -1) {
                selectedIdxStr = selectedIdxAndTypeStr.substring(0, selectedIdxAndTypeStr.indexOf(",")).trim();
                typeStr = selectedIdxAndTypeStr.substring(selectedIdxAndTypeStr.indexOf(",") + 1).trim();
            }
            selectedIdx = Integer.parseInt(selectedIdxStr);
            enumPairListStr = enumStr.substring(0, enumStr.indexOf("|"));
        }

        StringTokenizer st = new StringTokenizer(enumPairListStr, ",", false);
        List enumPairList = new ArrayList();
        int i = 0;
        while(st.hasMoreTokens()) {
            String enumPair = st.nextToken();
            int sepIdx = enumPair.indexOf("=");
            if (sepIdx == -1) {
                throw new RuntimeException("given enum string has an invalid format: " + enumStr);
            }
            String enumName = enumPair.substring(0, sepIdx).trim();
            Object enumValue = null;
            if (! CConstant.REAL_DATA_TYPE.equals(typeStr)) {
                enumValue = new Integer(enumPair.substring(sepIdx + 1).trim());
            } else {
                enumValue = new Double(enumPair.substring(sepIdx + 1).trim());
            }
            enumPairList.add(new Object[] { enumName, enumValue, new Boolean(i == selectedIdx) });
            i++;
        }
        return enumPairList;
    }

    /** parse the defaultValue of CFile-typed parameter to get String[] { fileName, defaultValue } */
    public static String[] getFileNameAndFileValue(String defaultValueOfFileParam) {
        String fileName;
        String fileValue;
        int idxOfSeparator = defaultValueOfFileParam.indexOf("|");
        if (idxOfSeparator == -1) {
            fileName = defaultValueOfFileParam;
            fileValue = "";
        } else {
            fileName = defaultValueOfFileParam.substring(0, idxOfSeparator);
            fileValue = defaultValueOfFileParam.substring(idxOfSeparator + 1);
        }
        return new String[] { fileName, fileValue };
    }

    /** used as a default value of a file parameter */
    public static String getDefaultValueOfFileParam(String fileName, String defaultFileValue) {
        return fileName + "|" + defaultFileValue;
    }

    public static CReal number(double value) {
        return new CReal(value);
    }

    public static CInteger number(int value) {
        return new CInteger(value);
    }

    /** get (the first number) ^ (the second number). use the second number as a power */
    public static CReal pow(CReal first, double second) {
        double powered = Math.pow(first.getDoubleValue(), second);
        CReal ret = new CReal(powered, first.getUnit().cloneUnit());
        ret.getUnit().getUnit().pow(second);
        return ret;
    }

    /** get (the first number) ^ (the second number). use the second number as a power */
    public static CReal pow(CInteger first, double second) {
        double powered = Math.pow(first.getDoubleValue(), second);
        CReal ret = new CReal(powered, first.getUnit().cloneUnit());
        ret.getUnit().getUnit().pow(second);
        return ret;
    }

    /** get (the first number) ^ (the second number). use the second number as a power */
    public static CReal pow(CReal first, CReal second) {
        double powered = Math.pow(first.getDoubleValue(), second.getDoubleValue());
        CReal ret = new CReal(powered, first.getUnit().cloneUnit());
        ret.getUnit().getUnit().pow(second.getDoubleValue());
        return ret;
    }

    /** get (the first number) ^ (the second number). use the second number as a power */
    public static CReal pow(CReal first, CInteger second) {
        double powered = Math.pow(first.getDoubleValue(), second.getDoubleValue());
        CReal ret = new CReal(powered, first.getUnit().cloneUnit());
        ret.getUnit().getUnit().pow(second.getDoubleValue());
        return ret;
    }

    /** get (the first number) ^ (the second number). use the second number as a power */
    public static CReal pow(CInteger first, CReal second) {
        double powered = Math.pow(first.getDoubleValue(), second.getDoubleValue());
        CReal ret = new CReal(powered, first.getUnit().cloneUnit());
        ret.getUnit().getUnit().pow(second.getDoubleValue());
        return ret;
    }

    /** get (the first number) ^ (the second number). use the second number as a power */
    public static CReal pow(CInteger first, CInteger second) {
        double powered = Math.pow(first.getDoubleValue(), second.getDoubleValue());
        CReal ret = new CReal(powered, first.getUnit().cloneUnit());
        return ret;
    }
}
