package mit.cadlab.dome3.plugin.catalog.ui;

import java.util.*;

/**
 * User: sangmok
 * Date: 2006. 3. 16
 *
 * (e.g.) usage
 *
 * VarNameConverter nameConverter = new VarNameConverter();
 * nameConverter.put("my width");
 * nameConverter.put("123 width");
 * nameConverter.put("his-height");
 * nameConverter.put("his height");
 * nameConverter.convert();
 * nameConverter.get("my width") --> "my_width")
 * nameConverter.get("my width") --> "_123_width")
 * nameConverter.get("his-height") --> "his_height_1")
 * nameConverter.get("his_height") --> "his_height_2")
 */
public class VarNameConverter {

    private Map dupCounterMap; // origin name -> dup index (Integer)
    private Map fixedNameMap; // origin name -> fixed name (String)
    private Map uniqueNameMap; // origin name -> isUnique (Boolean)

    private List originNames;
    private boolean converted = false;

    protected static final char UNDERSCORE = '_';
    protected static final char SCRIPT_VAR_POSTFIX = '_';

    public VarNameConverter() {
        dupCounterMap = new HashMap();
        fixedNameMap = new HashMap();
        uniqueNameMap = new HashMap();
        originNames = new ArrayList();
    }

    /** after putting all original names, call this method to prepare fixed parameter names */
    public void convert() {
        for (int i = 0; i < originNames.size(); i++) {
            String originName = (String) originNames.get(i);
            String fixedName = getFixedName(originName);
            Set entrySet = fixedNameMap.entrySet();
            int dupCounter = 1;
            uniqueNameMap.put(fixedName, Boolean.TRUE);
            for (Iterator j = entrySet.iterator(); j.hasNext();) {
                Map.Entry entry = (Map.Entry) j.next();
                if (fixedName.equals(entry.getValue())) {
                    dupCounter++;
                    uniqueNameMap.put(fixedName, Boolean.FALSE);
                }
            }
            fixedNameMap.put(originName, fixedName);
            dupCounterMap.put(originName, new Integer(dupCounter));
        }
        converted = true;
    }

    /** add originName to be converted */
    public void put(String originName) {
        originNames.add(originName);
    }

    /** returns a unique script variable name */
    public String get(String originName) {
        if (! converted) {
            throw new RuntimeException("please invoke convert() before get the conversion result.");
        }

        String fixedName = (String) fixedNameMap.get(originName);

        if (fixedName == null) {
            throw new RuntimeException("the given originName seems not having been put to the converter: given origin name = " + originName + ", existing origin names = " + originNames);
        }

        int dupIndex = ((Integer) dupCounterMap.get(originName)).intValue();
        boolean isUnique = (uniqueNameMap.get(fixedName) != null) && ((Boolean) uniqueNameMap.get(fixedName)).booleanValue();
        if (isUnique) {
            return fixedName;
        } else {
            return fixedName + SCRIPT_VAR_POSTFIX + dupIndex;
        }
    }

    /** fixes given parameter name so that it consists of letters, digits and underscore */
    public static String getFixedName(String originName) {
        if (originName == null || originName.length() == 0) {
            return null;
        } else {
            StringBuffer fixedStr = new StringBuffer("");
            char nameChar = originName.charAt(0);

            /* check first letter, if it is not valid, append _ at the beginning */
            if (! Character.isLetter(nameChar)) {
                fixedStr.append(UNDERSCORE);
            }

            /* for remaining letters, check if they are either a letter or a number. if they are not, append _ instead of them. */
            for (int i = 0; i < originName.length(); ++i) {
                nameChar = originName.charAt(i);
                if (Character.isLetter(nameChar) || Character.isDigit(nameChar)) {
                    fixedStr.append(nameChar);
                } else {
                    fixedStr.append(UNDERSCORE);
                }
            }
            return fixedStr.toString();
        }
    }

    public static void main(String[] args) {
        VarNameConverter nameConverter = new VarNameConverter();
        nameConverter.put("my width");
        nameConverter.put("123 width");
        nameConverter.put("his-height");
        nameConverter.put("his height");
        nameConverter.convert();
        System.out.println(nameConverter.get("my width"));
        System.out.println(nameConverter.get("my width"));
        System.out.println(nameConverter.get("his-height"));
        System.out.println(nameConverter.get("his height"));
    }
}
