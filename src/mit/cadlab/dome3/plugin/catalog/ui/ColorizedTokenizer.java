package mit.cadlab.dome3.plugin.catalog.ui;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 9.
 */
import java.util.regex.Matcher;

/** returns colorized token based on TokenDelimiter given to its constructor */
public class ColorizedTokenizer {

    protected DelimiterList delimList;
    protected Matcher matcher;

    public ColorizedTokenizer(DelimiterList delimList, String text) {
        this.delimList = delimList;
        matcher = delimList.getMatcher(text);
    }

    protected ColorizedToken getToken(int pos) {
        int count = delimList.getDelimCount();
        for (int i = 1; i <= count; i++) {
            String token = matcher.group(i);
            if (token != null) {
                String type = delimList.getStyleName(i - 1);
                return new ColorizedToken(token, type, pos);
            }
        }
        /* shoud not reach here */
        return null;
    }

    public ColorizedToken nextToken() {
        if (matcher.find()) {
            return getToken(matcher.start());
        }
        return null;
    }

    public static class ColorizedToken {
        public String text;
        public String styleName;
        protected int pos;

        public ColorizedToken(String text, String styleName, int pos) {
            this.text = text;
            this.styleName = styleName;
            this.pos = pos;
        }

        public String getText() {
            return text;
        }

        public String getStyleName() {
            return styleName;
        }

        public int getPos() {
            return pos;
        }

        public String toString() {
            return "[ColorizedToken:" + styleName + "(" + text + ", " + pos + ')';
        }
    }
}
