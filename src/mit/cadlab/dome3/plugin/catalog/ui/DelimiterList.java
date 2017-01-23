package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CLog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 9.
 */
public abstract class DelimiterList {
    private List delimList;
    private Pattern pattern;
    private ColorizedDocument colorizedDocument;

    public DelimiterList() {
        delimList = new ArrayList();
    }

    /** called inside the constructor of ColorizedDocument */
    protected void setColorizedDocument(ColorizedDocument colorizedDocument) {
        this.colorizedDocument = colorizedDocument;
    }

    /** used to query what ColorizedDocument instance is using this DelimiterList */
    public ColorizedDocument getColorizedDocument() {
        return colorizedDocument;
    }

    /* called after addition of Delimiter */
    private void updatePattern() {
        StringBuffer sb = new StringBuffer();
        int typeCount = getDelimCount();
        for (int i = 0; i < typeCount; i++) {
            Delimiter reType = (Delimiter) delimList.get(i);
            sb.append("(" + reType.getPattern() + ")");
            if (i != (typeCount - 1)) {
                sb.append("|");
            }
        }
        pattern = Pattern.compile(sb.toString(), Pattern.MULTILINE);

        //speedup Clog.debug("pattern updated: " + sb.toString());
    }

    public String getStyleName(int index) {
        return ((Delimiter) delimList.get(index)).getStyleName();
    }

    public int getDelimCount() {
        return delimList.size();
    }

    public Matcher getMatcher(String text) {
        return pattern.matcher(text);
    }

    public void addDelimiter(String styleName, String pattern) {
        delimList.add(new Delimiter(styleName, pattern));
        updatePattern();
    }

    public abstract String getLangName();
    public abstract List getKeywordList1();
    public abstract List getKeywordList2();
    public abstract List getKeywordList3();

//    public void addKeyword1(String keyword) { StyleUtil.addKeyword(getLangName(), keyword, StyleUtil.KEYWORD_1_STYLE); }
//    public void addKeyword2(String keyword) { StyleUtil.addKeyword(getLangName(), keyword, StyleUtil.KEYWORD_2_STYLE); }
//    public void addKeyword3(String keyword) { StyleUtil.addKeyword(getLangName(), keyword, StyleUtil.KEYWORD_3_STYLE); }

//    public void addKeyword(String keyword) { StyleUtil.addKeyword(getLangName(), keyword); }
//    public void removeKeyword(String keyword) { StyleUtil.removeKeyword(getLangName(), keyword); }
//    public void clearKeywords() { StyleUtil.clearKeywords(getLangName()); }



    public class Delimiter {
        private String styleName;
        private String pattern;

        /** setup how given pattern should be styled. note. when styleName is StyleUtil.KEYWORD_BASED_STYLE, actual styleName is determined by a returned value of StyleUtil.getStyleName(String langName, String word) */
        public Delimiter(String styleName, String pattern) {
            this.styleName = styleName;
            this.pattern = pattern;
        }

        public String getStyleName() {
            return styleName;
        }

        public String getPattern() {
            return pattern;
        }
    }
}

