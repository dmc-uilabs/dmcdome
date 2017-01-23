package mit.cadlab.dome3.plugin.catalog.core;

import mit.cadlab.dome3.plugin.catalog.core.dataobject.DataObjectUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Boolean;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 22.
 */
public class CoreUtil {

    /** returns a list of qualified param name list in this mapping script, given CNamingService is used to confirm that test if a token is a param name */
    public static Set getParamNames(String mappingScript, CNamingService namingService) {
        Set ret = new HashSet();
        List paramNameList = new ArrayList();
        for (Iterator i = namingService.getParameters().iterator(); i.hasNext(); ) {
            CParameter param = (CParameter) i.next();
            paramNameList.add(param.getQualifiedName());
        }

        /* longer param name should come first */
        Collections.sort(paramNameList, new Comparator() {
            public int compare(Object left, Object right) {
                return ((String) right).length() - ((String) left).length();
            }
        });

        StringBuffer paramNamePt = new StringBuffer();
        for (Iterator i = paramNameList.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            paramNamePt.append('(').append(paramName.replaceAll("\\.", "\\\\.")).append(')');
            if (i.hasNext()) {
                paramNamePt.append("|");
            }
        }

        Pattern pattern = Pattern.compile(paramNamePt.toString());
        Matcher matcher = pattern.matcher(mappingScript);
        while (matcher.find()) {
            int patternIdx = findPatternIdx(matcher);
            ret.add(paramNameList.get(patternIdx - 1));
        }
        return ret;
    }

    private static int findPatternIdx(Matcher matcher) {
        int groupCount = matcher.groupCount();
        for (int i = 1; i <= groupCount; i++) {
            if (matcher.group(i) != null)
                return i;
        }
        throw new RuntimeException("invalid matcher status. please invoke this method only matcher returns true for find()");
    }

    public static String convertDefaultValueIntoMappingScript(String dataType, String defaultValue) {
        if (CConstant.REAL_DATA_TYPE.equals(dataType) || CConstant.INTEGER_DATA_TYPE.equals(dataType)) {
            return defaultValue;
        } else if (CConstant.STRING_DATA_TYPE.equals(dataType)) {
            return "\"" + defaultValue + "\"";
        } else if (CConstant.BOOLEAN_DATA_TYPE.equals(dataType)) {
            return "\"" + defaultValue + "\"";
        } else if (CConstant.ENUM_DATA_TYPE.equals(dataType)) {
            List enumList = DataObjectUtil.createEnumList(defaultValue);
            for (int i = 0; i < enumList.size(); i++) {
                Object[] objs = (Object[]) enumList.get(i);
                String enumName = (String) objs [0];
                Boolean enumBool = (Boolean) objs [2];
                if (enumBool.booleanValue()) {
                    return "\"" + enumName + "\"";
                }
            }
            if (enumList.size() > 0) {
                return (String) ((Object[]) enumList.get(0)) [0]; // first enum item's name
            } else {
                return "";
            }
        } else if (CConstant.FILE_DATA_TYPE.equals(dataType)) {
            return "";
        } else if (CConstant.MATRIX_DATA_TYPE.equals(dataType)) {
            return DataObjectUtil.removeDataType(defaultValue);
        } else if (CConstant.VECTOR_DATA_TYPE.equals(dataType)) {
            return DataObjectUtil.removeDataType(defaultValue);
        } else {
            throw new RuntimeException("currently unsupported data type: " + dataType);
        }
    }
}
