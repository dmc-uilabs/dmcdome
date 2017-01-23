package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 9.
 * StyleUtil knows how to style a document when styleName is given: setStyle(langName, document)
 * It also knows what is styleName for a given keyword if a token's styleName should be determined by membership in a keyword set: getStyleNameByKeyword(langName, keyword)
 */
public class StyleUtil {

    /* lang names */
    public static final String JAVA_LANG = "JAVA_LANG";
    public static final String GROOVY_LANG = "GROOVY_LANG";
    public static final String CELLSCRIPT_LANG = "CELLSCRIPT_LANG";

    /* style names */
    public static final String QUOTED_STRING_STYLE = "QUOTED_STRING_STYLE";
    public static final String NUMBER_STYLE = "NUMBER_STYLE";
    public static final String COMMENTED_STYLE = "COMMENTED_STYLE";
    public static final String KEYWORD_BASED_STYLE = "KEYWORD_BASED";

    public static final String KEYWORD_1_STYLE = "KEYWORD_1_STYLE";
    public static final String KEYWORD_2_STYLE = "KEYWORD_2_STYLE";
    public static final String KEYWORD_3_STYLE = "KEYWORD_3_STYLE";

    public static final String REFERENCE_STYLE = "REFERENCE_BASED"; // this is not a actual style
    public static final String PARAM_NAME_STYLE = "PARAM_NAME_STYLE"; // this is a actual style
    public static final String ARRAY_INDEX_STYLE = "ARRAY_INDEX_STYLE"; // this is a actual style
    public static final String PARAM_NAME_ERROR_STYLE = "PARAM_NAME_ERROR_STYLE"; // this is a actual style
    public static final String LOCAL_VARIABLE_STYLE = "LOCAL_VARIABLE_STYLE";
    //public static final String REL_ALIAS_ERROR_STYLE = "REL_ALIAS_ERROR_STYLE"; // this is a actual style

    private static Map styleInfoMap = new HashMap(); // a map of langName.styleName -> styleInfo of Object[] { fg, bg, bold, italic }

    //private static Map keywordToStyleNameMap = new HashMap(); // a Map of [langName.keyword -> styleName]

    private static Map styleNameMap = new HashMap(); // a Map of [langName -> Map of [keyword List -> styleName]]


    static {
        /* register Style instance. it can be retrieved using langName and styleName combination */
        registerStyle(GROOVY_LANG, QUOTED_STRING_STYLE, new Color(0, 128, 0), Color.WHITE, true, false);
        registerStyle(GROOVY_LANG, COMMENTED_STYLE, new Color(128, 128, 128), Color.WHITE, false, true);
        registerStyle(GROOVY_LANG, NUMBER_STYLE, Color.BLUE, Color.WHITE, false, false);
        registerStyle(GROOVY_LANG, KEYWORD_1_STYLE, new Color(0, 0, 128), Color.WHITE, true, false);
//        registerStyle(GROOVY_LANG, KEYWORD_2_STYLE, Color.DARK_GRAY, Color.WHITE, true, false);
//        registerStyle(GROOVY_LANG, KEYWORD_3_STYLE, new Color(102, 14, 122), Color.WHITE, true, false);
        registerStyle(GROOVY_LANG, KEYWORD_2_STYLE, new Color(0x00, 0x00, 0xA8), Color.WHITE, true, false);
        registerStyle(GROOVY_LANG, KEYWORD_3_STYLE, new Color(0xA8, 0x00, 0x00), Color.WHITE, true, false);

        /* register Style instance. it can be retrieved using langName and styleName combination */
        registerStyle(CELLSCRIPT_LANG, QUOTED_STRING_STYLE, new Color(0, 128, 0), Color.WHITE, false, false);
        registerStyle(CELLSCRIPT_LANG, COMMENTED_STYLE, new Color(128, 128, 128), Color.WHITE, false, true);
        registerStyle(CELLSCRIPT_LANG, NUMBER_STYLE, Color.BLUE, Color.WHITE, false, false);
        registerStyle(CELLSCRIPT_LANG, KEYWORD_1_STYLE, new Color(0, 0, 128), Color.WHITE, true, false);
        registerStyle(CELLSCRIPT_LANG, KEYWORD_2_STYLE, Color.DARK_GRAY, Color.WHITE, true, false);
        registerStyle(CELLSCRIPT_LANG, KEYWORD_3_STYLE, new Color(102, 14, 122), Color.WHITE, true, false);

        registerStyle(CELLSCRIPT_LANG, PARAM_NAME_STYLE, new Color(0, 0, 0), Color.WHITE, true, false);
        //registerStyle(CELLSCRIPT_LANG, PARAM_NAME_STYLE, new Color(0, 0, 0), Color.WHITE, false, false);
        registerStyle(CELLSCRIPT_LANG, ARRAY_INDEX_STYLE, Color.DARK_GRAY, Color.WHITE, false, false);
        registerStyle(CELLSCRIPT_LANG, PARAM_NAME_ERROR_STYLE, new Color(0, 0, 0), Color.WHITE, false, false);

        /* register Style instance. it can be retrieved using langName and styleName combination */
        for (int i = 0; i < UIUtil.REL_BG_COLORS.length; i++) {
            registerStyle(CELLSCRIPT_LANG, "REL_ALIAS_STYLE_" + i, new Color(0, 0, 0), UIUtil.REL_BG_COLORS[i], false, false);
            //registerStyle(CELLSCRIPT_LANG, "REL_ALIAS_STYLE_" + i, UIUtil.REL_BG_COLORS[i], Color.WHITE, true, false);
        }
        //registerStyle(CELLSCRIPT_LANG, REL_ALIAS_ERROR_STYLE, Color.RED, UIUtil.REL_ERROR_BG_COLOR, false, false);
    }

    /** let StyleUtil to know styleMetaInfo for given styleName of given langName. in order to put registered styles in action, use setStyle(langName, document) method.  */
    private static void registerStyle(String langName, String styleName, Color foreground, Color background, boolean isBold, boolean isItallic) {
        Object[] styleInfo = new Object[] { foreground, background, new Boolean(isBold), new Boolean(isItallic) };
        styleInfoMap.put(langName + "." + styleName, styleInfo);
    }

    /** get style name for given word of given language. if match not found, word is not a keyword, and then return null. */
    public static String getStyleNameForKeyword(String langName, String word) {
        //return (String) keywordToStyleNameMap.get(langName + "." + word);
        Set entrySet = ((Map) styleNameMap.get(langName)).entrySet();

        for (Iterator i = entrySet.iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            List keywordList = (List) entry.getValue();
            if (keywordList.contains(word)) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    /** get style name for given word of given language. if match not found, word is not a keyword, and then return null. */
    public static Set getStyleNames(String langName) {
        return ((Map) styleNameMap.get(langName)).keySet();
    }

    /** among registered styles, find styles applicable to given langName, and put them in action in the given doc */
    public static void installStylesToDocument(String langName, DelimiterList delimList, StyledDocument doc) {
        /* set how keyword set 1, 2, and 3 of given langname should be styled */
        StyleUtil.setKeywordList(langName, KEYWORD_1_STYLE, delimList.getKeywordList1());
        StyleUtil.setKeywordList(langName, KEYWORD_2_STYLE, delimList.getKeywordList2());
        StyleUtil.setKeywordList(langName, KEYWORD_3_STYLE, delimList.getKeywordList3());

        Set stylistSet = styleInfoMap.entrySet();
        Iterator i = stylistSet.iterator();
        StyleContext styleContext = new StyleContext();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String langNameDotStyleName = (String) entry.getKey();
            Object[] styleInfo = (Object[]) entry.getValue(); // fg, bg, bold, italic
            if (langNameDotStyleName.startsWith(langName + ".")) {
                String styleName = langNameDotStyleName.substring((langName + ".").length());
                Style addedStyle = doc.addStyle(styleName, styleContext.getStyle(StyleContext.DEFAULT_STYLE));
                StyleConstants.setForeground(addedStyle, (Color) styleInfo[0]);
                StyleConstants.setBackground(addedStyle, (Color) styleInfo[1]);
                StyleConstants.setBold(addedStyle, ((Boolean) styleInfo[2]).booleanValue());
                StyleConstants.setItalic(addedStyle, ((Boolean) styleInfo[3]).booleanValue());
            }
        }
    }

    /** let style manager to know the styleName for given keywords of given langName */
    public static void setKeywordList(String langName, String styleName, List keywords) {
        Map keywordListToStyleNameMap = (Map) styleNameMap.get(langName);
        if (keywordListToStyleNameMap == null) {
            keywordListToStyleNameMap = new HashMap();
            styleNameMap.put(langName, keywordListToStyleNameMap);
        }
        keywordListToStyleNameMap.put(styleName, keywords);
    }

    public static List getKeywordList(String langName, String styleName) {
        Map keywordListToStyleNameMap = (Map) styleNameMap.get(langName);
        return (List) keywordListToStyleNameMap.get(styleName);
    }
}
