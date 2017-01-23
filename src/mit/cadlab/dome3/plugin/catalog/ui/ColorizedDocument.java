package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 9.
 */

public class ColorizedDocument extends DefaultStyledDocument {
    protected DelimiterList delimList;
    private String langName;

    public ColorizedDocument(DelimiterList delimList) {
        this.delimList = delimList;
        this.langName = delimList.getLangName();

        delimList.setColorizedDocument(this);
        StyleUtil.installStylesToDocument(langName, delimList, this);
    }

    public void insertString(int offset, String text, AttributeSet style) throws BadLocationException {
        super.insertString(offset, text, style);
        highlightSyntax();
    }

    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        highlightSyntax();
    }

    public void highlightSyntax() {
        try {
            String text = getText(0, getLength());
            setCharacterAttributes(0, getLength(), getStyle(StyleContext.DEFAULT_STYLE), true);
            ColorizedTokenizer.ColorizedToken token;
            ColorizedTokenizer tokenizer = new ColorizedTokenizer(delimList, text);
            int delimCount = delimList.getDelimCount();
            while ((token = tokenizer.nextToken()) != null) {
                String word = token.getText();
                int len = word.length();
                for (int i = 0; i < delimCount; i++) {
                    if (token.getStyleName().equals(delimList.getStyleName(i))) {
                        //setCharacterAttributes(token.getPos(), len, getStyle(delimList.getStyleName(i)), false);
                        if (StyleUtil.KEYWORD_BASED_STYLE.equals(delimList.getStyleName(i))) {
                            String style = StyleUtil.getStyleNameForKeyword(langName, word);

//                            System.out.println("keyword based style: word=" + word + ", style=" + style + " among " + StyleUtil.getStyleNames(langName));
                            if (style != null) {
                                setCharacterAttributes(token.getPos(), len, getStyle(style), false);
                            }
                        } else if (StyleUtil.REFERENCE_STYLE.equals(delimList.getStyleName(i))) {
                            String qualifiedParamPattern = "(?:^[\\s/*-+^%!\\(\\)]*([a-zA-Z_0-9]+)[\\.]*([a-zA-Z_0-9 ]*)[\\.]*([a-zA-Z_0-9 ]*)(\\[[0-9 ]*\\])*([\\s/*-+^%!]*$))";

                            Pattern paramPattern = Pattern.compile(qualifiedParamPattern);
                            Matcher m = paramPattern.matcher(token.getText());
                            if (m.matches()) {
                                ComponentReference compRef = ((CellScriptDelimiterList) delimList).getComponentReference();
                                String relAlias = m.group(1).trim();
                                String paramName = m.group(2).trim();
                                String derivedName = m.group(3).trim();
                                String arrayIndex = m.group(4);

                                if ("".equals(paramName)) {
                                    if (compRef != null && compRef.isValidRelAlias(relAlias)) {
                                        String styleName = compRef.getStyleNameForRelAlias(relAlias);
                                        setCharacterAttributes(token.getPos() + m.start(1), token.getPos() + m.end(1), getStyle(styleName), false);
                                    } else {
                                        setCharacterAttributes(token.getPos() + m.start(1), token.getPos() + m.end(1), getStyle(StyleUtil.PARAM_NAME_ERROR_STYLE), false);
                                    }
                                } else {
                                    if ("".equals(derivedName)) {
                                        String qualifiedName = relAlias + "." + paramName;
                                        if (compRef != null && compRef.isValidQualifiedParamName(qualifiedName.trim())) {
                                            String styleName = compRef.getStyleNameForRelAlias(relAlias);
                                            setCharacterAttributes(token.getPos() + m.start(1), token.getPos() + m.end(1), getStyle(styleName), false);
                                            setCharacterAttributes(token.getPos() + m.start(2) - 1, token.getPos() + m.end(2), getStyle(StyleUtil.PARAM_NAME_STYLE), false);
                                            setCharacterAttributes(token.getPos() + m.end(2), token.getPos() + m.end(5), getStyle(StyleUtil.PARAM_NAME_ERROR_STYLE), false); // this makes symbols like +-/* not bold
                                            if (m.start(4) != -1) {
                                                setCharacterAttributes(token.getPos() + m.start(4), token.getPos() + m.start(4) + 1, getStyle(StyleUtil.ARRAY_INDEX_STYLE), false);
                                                setCharacterAttributes(token.getPos() + m.start(4) + 1, token.getPos() + m.end(4) - 1, getStyle(StyleUtil.NUMBER_STYLE), false);
                                                setCharacterAttributes(token.getPos() + m.end(4) - 1, token.getPos() + m.end(4), getStyle(StyleUtil.ARRAY_INDEX_STYLE), false);
                                            }


                                        } else {
                                            setCharacterAttributes(token.getPos() + m.start(1), token.getPos() + m.end(1), getStyle(StyleUtil.PARAM_NAME_ERROR_STYLE), false);
                                            setCharacterAttributes(token.getPos() + m.start(2) - 1, token.getPos() + m.end(2), getStyle(StyleUtil.PARAM_NAME_ERROR_STYLE), false);
                                            if (m.start(4) != -1) {
                                                setCharacterAttributes(token.getPos() + m.start(4), token.getPos() + m.end(4), getStyle(StyleUtil.PARAM_NAME_ERROR_STYLE), false);
                                            }
                                        }
                                    } else {
                                        String qualifiedName = relAlias + "." + paramName + "." + derivedName;
                                        if (compRef.isValidQualifiedParamName(qualifiedName.trim())) {
                                            String styleName = compRef.getStyleNameForRelAlias(relAlias);
                                            setCharacterAttributes(token.getPos() + m.start(1), token.getPos() + m.end(1), getStyle(styleName), false);
                                            setCharacterAttributes(token.getPos() + m.start(2) - 1, token.getPos() + m.end(3), getStyle(StyleUtil.PARAM_NAME_STYLE), false);
                                            setCharacterAttributes(token.getPos() + m.end(2), token.getPos() + m.end(5), getStyle(StyleUtil.PARAM_NAME_ERROR_STYLE), false); // this makes symbols like +-/* not bold
                                            if (m.start(4) != -1) {
                                                setCharacterAttributes(token.getPos() + m.start(4), token.getPos() + m.start(4) + 1, getStyle(StyleUtil.ARRAY_INDEX_STYLE), false);
                                                setCharacterAttributes(token.getPos() + m.start(4) + 1, token.getPos() + m.end(4) - 1, getStyle(StyleUtil.NUMBER_STYLE), false);
                                                setCharacterAttributes(token.getPos() + m.end(4) - 1, token.getPos() + m.end(4), getStyle(StyleUtil.ARRAY_INDEX_STYLE), false);
                                            }
                                        } else {
                                            setCharacterAttributes(token.getPos() + m.start(1), token.getPos() + m.end(1), getStyle(StyleUtil.PARAM_NAME_ERROR_STYLE), false);
                                            setCharacterAttributes(token.getPos() + m.start(2) - 1, token.getPos() + m.end(3), getStyle(StyleUtil.PARAM_NAME_ERROR_STYLE), false);
                                            if (m.start(4) != -1) {
                                                setCharacterAttributes(token.getPos() + m.start(4), token.getPos() + m.end(4), getStyle(StyleUtil.PARAM_NAME_ERROR_STYLE), false);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            setCharacterAttributes(token.getPos(), len, getStyle(delimList.getStyleName(i)), false);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDocumentWidth(int row) {
        return 0;
    }
}

