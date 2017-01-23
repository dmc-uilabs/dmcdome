package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CNamingService;
import mit.cadlab.dome3.plugin.catalog.core.CParameter;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 11.
 */
public class CellScriptDelimiterList extends DelimiterList {
    private ComponentReference compRef;

    public CellScriptDelimiterList(ComponentReference compRef) {
        super();
        this.compRef = compRef;

//        this.addDelimiter(StyleUtil.COMMENTED_STYLE, "(?:\\/\\*(?:.*?)*\\*\\/)|(?:\\/\\/.*)");

        this.addDelimiter(StyleUtil.NUMBER_STYLE, "\\b\\d+(?:.\\d+)?\\b");
        this.addDelimiter(StyleUtil.NUMBER_STYLE, "\\[(?:\\s*\\d+(?:.\\d+)?[ ;\\\\t\\\\n\\\\x0B\\\\f\\\\r]*)+\\]"); // vector or matrix
        this.addDelimiter(StyleUtil.QUOTED_STRING_STYLE, "\"(?:\\\\.|[^\"\\\\])*\"");
        //this.addDelimiter(StyleUtil.KEYWORD_BASED_STYLE, "\\b[a-zA-Z_0-9]+[\\.]*[a-zA-Z_0-9]*\\b");
        this.addDelimiter(StyleUtil.REFERENCE_STYLE, "(?:^|[/*-+^%! ]*)[a-zA-Z_0-9. ]+(?:\\[[0-9 ]*\\])*(?:$|[/*-+^%! ]*)");

        //"([a-zA-Z_0-9]+)[\\.]*([a-zA-Z_0-9]*)[\\.]*([a-zA-Z_0-9]*)"

    }

    public List getKeywordList1() {
        return Collections.EMPTY_LIST;
    };

    /** param name */
    public List getKeywordList2() {
        return Collections.EMPTY_LIST;
    }

    public void updateKeywordList2() {
        List keywordList = new ArrayList();
        CNamingService namingService = compRef.getCurrentCNamingService();
        Set params = namingService.getParameters();
        for (Iterator i = params.iterator(); i.hasNext(); ) {
            CParameter param = (CParameter) i.next();
            keywordList.add(param.getQualifiedName());
        }
    }

    public List getKeywordList3() { return Collections.EMPTY_LIST; }

    public String getLangName() {
        return StyleUtil.CELLSCRIPT_LANG;
    }

    public ComponentReference getComponentReference() {
        return compRef;
    }
}

