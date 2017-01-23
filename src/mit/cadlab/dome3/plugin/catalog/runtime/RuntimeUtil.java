package mit.cadlab.dome3.plugin.catalog.runtime;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2006. 7. 30.
 */
public class RuntimeUtil {
    public static void main(String[] args) {
        String[] paramNames = new String[] { "itf.A PARAM", "itf.B-PARAM", "itf.A_standard open-circuit voltage" };
        String converted44 = RuntimeUtil.convertMappingScript(paramNames, "\"C:\\dome3-file-tutorial\\server-drive\\UnzipResult\"");
        System.out.println("dddddddddd =  " + converted44);

        String converted = RuntimeUtil.convertMappingScript(paramNames, "itf.A PARAM + 4 * Math.round([4 3 ; 3 3] + 5) + itf.B-PARAM [3] - inverse([3]) + [ 3 ; 4.3 ; 5.4 ; ] * 5 + [3 4].get(0,0) + 9");
        String converted2 = RuntimeUtil.convertMappingScript(paramNames, "itf.A PARAM + 4 * Math.round([4 3 ; 3 3] + 5) + itf.C-PARAM [3] - inverse([3]) + [ 3 ; 4.3 ; 5.4 ; ] * 5 + [3 4].get(0,0) + 9");
        String[] invalidRefs = RuntimeUtil.findInvalidReferences(paramNames, "itf.A PARAM + 4 * Math.round([4 3 ; 3 3] + 5) + itf.C-PARAM [3] - inverse([3]) + [ 3 ; 4.3 ; 5.4 ; ] * 5 + [3 4].get(0,0) + 9 + A.width");
        System.out.println(converted);
        System.out.println(converted2);
        for (int i = 0; i < invalidRefs.length; i++) {
            System.out.println("invalid : " + invalidRefs[i]);
        }

        List varNameList = Arrays.asList(new String[] { "material", "material_cost", "width" });;
        String source3 = "material = \"aluminium\";\nmax_material_cost = 600;\nif (true) {\n\tmaterial_cost = 300;\n}\nif (true) { material = \"steel\"; }\nwidth_temp = width;";
        String converted3 = convertRelationScript(varNameList, source3);
        System.out.println(source3);
        System.out.println("----------------");
        System.out.println(converted3);

    }
    private static Matcher createMatcherForMappingScript(String[] paramNames, String originEqn) {
        StringBuffer paramNamePt = new StringBuffer();
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames [i].replaceAll("\\.", "\\\\.");; // quote paramName to create a literal pattern string.
            paramNamePt.append(paramName);
            if (i != paramNames.length - 1) {
                paramNamePt.append("|");
            }
        }

        List patternList = new ArrayList();
        patternList.add("\\[(?:\\s*[0-9]+(?:\\.[0-9]+)?\\s*;?\\s*)*\\]");   /* 1: array index or matrix */
        patternList.add("[0-9]+(?:\\.[0-9]+)?");     /* 2: number */
        patternList.add("[*/+\\-]");                 /* 3: operator */
        patternList.add("[A-Za-z0-9_\\.]+\\(");      /* 4: open method */
        patternList.add("\\(");                      /* 5: open parenthesis */
        patternList.add("\\)");                      /* 6: close parenthesis or close method */
        if (paramNamePt.length() > 0) {
            patternList.add("(?:" + paramNamePt + ")");  /* 7: param name */
        }
        patternList.add("[A-Za-z]+\\.[A-Za-z0-9_\\-\\. ]*[A-Za-z0-9]"); /* 8: not recognizable param name */
        //patternList.add("\\s+");  /* 8: white space */

        StringBuffer patternSb = new StringBuffer();
        for (int i = 0; i < patternList.size(); i++) {
            patternSb.append("(" + patternList.get(i) + ")");
            if (i != patternList.size() - 1) {
                patternSb.append("|");
            }
        }
        Pattern pattern = Pattern.compile(patternSb.toString());
        Matcher matcher = pattern.matcher(originEqn);
        return matcher;
    }

    public static String convertRelationScript(List paramNameList, String src) {
        /* longer param name should come first */
        Collections.sort(paramNameList, new Comparator() {
            public int compare(Object left, Object right) {
                return ((String) right).length() - ((String) left).length();
            }
        });

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < paramNameList.size(); i++) {
            String varName = (String) paramNameList.get(i);
            sb.append("((?:[{\\t\\n\\r ]+|^)" + varName + "[ ]*=)");
            if (i != paramNameList.size() - 1) {
                sb.append("|");
            }
        }

        Pattern pt = Pattern.compile(sb.toString());

        StringBuffer convertedSb = new StringBuffer();
        Matcher matcher = pt.matcher(src);
        while (matcher.find()) {
            int patternIdx = findPatternIdx(matcher);
            String originLine = matcher.group(patternIdx);
            matcher.appendReplacement(convertedSb, originLine.substring(0, originLine.length() - 1) + "<<");
        }
        matcher.appendTail(convertedSb);
        return convertedSb.toString();
    }


    public static String convertMappingScript(String[] paramNames, String originEqn) {
        // do not convert if in a string literal format
        if (originEqn.matches("\"([^\"\\\\]|\\\\.)*\"")) {
            return originEqn;
        }

        Matcher matcher = createMatcherForMappingScript(paramNames, originEqn);

        int previsouMatcherIdx = -1;
        StringBuffer convertedSb = new StringBuffer();
        while (matcher.find()) {
            int patternIdx = findPatternIdx(matcher);
            switch (patternIdx) {
                case 1:
                    if (previsouMatcherIdx == 7 || previsouMatcherIdx == 6) { // when followed by param name or closing parenthesis
                        matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // array index notation
                    } else if (previsouMatcherIdx == -1 || previsouMatcherIdx == 3 || previsouMatcherIdx == 4 || previsouMatcherIdx == 5) { // when followed by non, operator, an opening method, or an opening parenthesis
                        matcher.appendReplacement(convertedSb, "matrix(\"$" + patternIdx + "\")"); // matrix notation
                    }
                    previsouMatcherIdx = patternIdx;
                    break;
                case 2:
                    if (previsouMatcherIdx == -1 || previsouMatcherIdx == 3) { // when followed by none or operator
                        if (matcher.group(patternIdx).indexOf(".") == -1) {
                            matcher.appendReplacement(convertedSb, "integer(\"$" + patternIdx + "\")"); // integer notation
                        } else { // other cases such as being followed by an opening method
                            matcher.appendReplacement(convertedSb, "real(\"$" + patternIdx + "\")"); // real notation
                        }
                    } else {
                        matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // native number notation
                    }
                    previsouMatcherIdx = patternIdx;
                    break;
                case 3:
//                    System.out.println("operator : " + matcher.group(patternIdx));
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // operator
                    previsouMatcherIdx = patternIdx;
                    break;
                case 4:
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // opening method
                    previsouMatcherIdx = patternIdx;
                    break;
                case 5:
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // opening parenthesis
                    previsouMatcherIdx = patternIdx;
                    break;
                case 6:
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // closing parenthesis or closing method
                    previsouMatcherIdx = patternIdx;
                    break;
                case 7:
                    matcher.appendReplacement(convertedSb, "context.getDataObject(\"$" + patternIdx + "\")"); // param name notation
                    previsouMatcherIdx = patternIdx;
                    break;
                case 8:
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // not recognizable param reference
                    break;
            }
        }
        matcher.appendTail(convertedSb);
        return convertedSb.toString();
    }

    /** return an array of String containing invalid references in the originEqn */
    public static String[] findInvalidReferences(String[] paramNames, String originEqn) {
        // do not convert if in a string literal format
        if (originEqn.matches("\"([^\"\\\\]|\\\\.)*\"")) {
            return new String[0];
        }

        List ret = new ArrayList();

        Matcher matcher = createMatcherForMappingScript(paramNames, originEqn);

        int previsouMatcherIdx = -1;
        StringBuffer convertedSb = new StringBuffer();
        while (matcher.find()) {
            int patternIdx = findPatternIdx(matcher);
            switch (patternIdx) {
                case 1:
                    if (previsouMatcherIdx == 7 || previsouMatcherIdx == 6) { // when followed by param name or closing parenthesis
                        matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // array index notation
                    } else if (previsouMatcherIdx == -1 || previsouMatcherIdx == 3 || previsouMatcherIdx == 4 || previsouMatcherIdx == 5) { // when followed by non, operator, an opening method, or an opening parenthesis
                        matcher.appendReplacement(convertedSb, "matrix(\"$" + patternIdx + "\")"); // matrix notation
                    }
                    previsouMatcherIdx = patternIdx;
                    break;
                case 2:
                    if (previsouMatcherIdx == -1 || previsouMatcherIdx == 3) { // when followed by none or operator
                        if (matcher.group(patternIdx).indexOf(".") == -1) {
                            matcher.appendReplacement(convertedSb, "integer(\"$" + patternIdx + "\")"); // integer notation
                        } else { // other cases such as being followed by an opening method
                            matcher.appendReplacement(convertedSb, "real(\"$" + patternIdx + "\")"); // real notation
                        }
                    } else {
                        matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // native number notation
                    }
                    previsouMatcherIdx = patternIdx;
                    break;
                case 3:
//                    System.out.println("operator : " + matcher.group(patternIdx));
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // operator
                    previsouMatcherIdx = patternIdx;
                    break;
                case 4:
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // opening method
                    previsouMatcherIdx = patternIdx;
                    break;
                case 5:
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // opening parenthesis
                    previsouMatcherIdx = patternIdx;
                    break;
                case 6:
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // closing parenthesis or closing method
                    previsouMatcherIdx = patternIdx;
                    break;
                case 7:
                    matcher.appendReplacement(convertedSb, "context.getDataObject(\"$" + patternIdx + "\")"); // param name notation
                    previsouMatcherIdx = patternIdx;
                    break;
                case 8:
                    ret.add(matcher.group(patternIdx));
                    matcher.appendReplacement(convertedSb, "$" + patternIdx + ""); // not recognizable param reference
                    break;
            }
        }
        matcher.appendTail(convertedSb);
        return (String[]) ret.toArray(new String[ret.size()]);
    }

    public static int findPatternIdx(Matcher matcher) {
        int groupCount = matcher.groupCount();
        for (int i = 1; i <= groupCount; i++) {
            if (matcher.group(i) != null)
                return i;
        }
        throw new RuntimeException("invalid matcher status. please invoke this method only matcher returns true for find()");
    }
}
