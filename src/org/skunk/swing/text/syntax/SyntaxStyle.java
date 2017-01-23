/*
 *  Copyright (c) 2001, Jacob Smullyan.
 *
 *  This is part of SkunkDAV, a WebDAV client.  See http://skunkdav.sourceforge.net/
 *  for the latest version.
 *
 *  SkunkDAV is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  SkunkDAV is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SkunkDAV; see the file COPYING.  If not, write to the Free
 *  Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 *  02111-1307, USA.
*/

package org.skunk.swing.text.syntax;

import org.skunk.trace.Debug;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class SyntaxStyle implements Serializable, Cloneable {
    static final long serialVersionUID = 6891694368647729611L;

    public static final String DEFAULT_STYLE = "default";
    public static final String UNFINISHED_STYLE = "unfinished";
    public static final String KEYWORD1_STYLE = "keyword1";
    public static final String KEYWORD2_STYLE = "keyword2";
    public static final String KEYWORD3_STYLE = "keyword3";
    public static final String KEYWORD4_STYLE = "keyword4";
    public static final String LITERAL1_STYLE = "literal1";
    public static final String LITERAL2_STYLE = "literal2";
    public static final String LITERAL3_STYLE = "literal3";
    public static final String LITERAL4_STYLE = "literal4";
    public static final String COMMENT1_STYLE = "comment1";
    public static final String COMMENT2_STYLE = "comment2";
    public static final String COMMENT3_STYLE = "comment3";
    public static final String COMMENT4_STYLE = "comment4";
    public static final String TAG1_STYLE = "tag1";
    public static final String TAG2_STYLE = "tag2";
    public static final String TAG3_STYLE = "tag3";
    public static final String TAG4_STYLE = "tag4";
    public static final String OPERATOR_STYLE = "operator";
    public static final String SEPARATOR1_STYLE = "separator1";
    public static final String SEPARATOR2_STYLE = "separator2";
    public static final String INCLUDE_STYLE = "include";
    public static final String IDENTIFIER1_STYLE = "identifier1";
    public static final String IDENTIFIER2_STYLE = "identifier2";
    public static final String ERROR_STYLE = "error";


    public static final int UNFINISHED = 1;
    public static final int DEFAULT = 2;
    public static final int KEYWORD1 = 3;
    public static final int KEYWORD2 = 4;
    public static final int KEYWORD3 = 5;
    public static final int KEYWORD4 = 6;
    public static final int LITERAL1 = 7;
    public static final int LITERAL2 = 8;
    public static final int LITERAL3 = 9;
    public static final int LITERAL4 = 10;
    public static final int COMMENT1 = 11;
    public static final int COMMENT2 = 12;
    public static final int COMMENT3 = 13;
    public static final int COMMENT4 = 14;
    public static final int TAG1 = 15;
    public static final int TAG2 = 16;
    public static final int TAG3 = 17;
    public static final int TAG4 = 18;
    public static final int OPERATOR = 19;
    public static final int SEPARATOR1 = 20;
    public static final int SEPARATOR2 = 21;
    public static final int INCLUDE = 22;
    public static final int IDENTIFIER1 = 23;
    public static final int IDENTIFIER2 = 24;
    public static final int ERROR = 25;

    public static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 12);
    public static final Color DEFAULT_FOREGROUND = Color.white;
    /* abandoning 0, 51, 51 for a new mod look! */
    public static final Color DEFAULT_BACKGROUND = new Color(0, 51, 102);

    public static final String DEFAULT_STYLES_FILE = "default.styles";

    private static SyntaxStyleSet defaultStyleSet = new SyntaxStyleSet();

    private SyntaxStyleSet parentSet;
    private Color foregroundColor;
    private boolean bold;
    private boolean italic;
    private String name;
    private int styleID;
    private Font font;

    static {
        initDefaultStyles(defaultStyleSet);
    }

    public static final SyntaxStyleSet getDefaultStyleSet() {
        return SyntaxStyle.defaultStyleSet;
    }

    public static final Iterator styles() {
        return defaultStyleSet.styles();
    }

    public static final SyntaxStyle getStyle(String name) {
        return defaultStyleSet.getStyle(name);
    }

    public static final SyntaxStyle getStyle(int id) {
        return defaultStyleSet.getStyle(id);
    }

    public static final Font getDefaultFont() {
        return defaultStyleSet.getDefaultFont();
    }

    public static final void initDefaultStyles(SyntaxStyleSet styleSet) {
        try {
            InputStream in = SyntaxStyle.class.getResourceAsStream(DEFAULT_STYLES_FILE);
            BufferedReader breader = new BufferedReader(new InputStreamReader(in));
            styleSet.readConfig(breader);
        } catch (Exception oyVeh) {
            if (Debug.DEBUG) Debug.trace(SyntaxStyle.class, Debug.DP2, oyVeh);
        }
    }

    private static Color getColor(String colorStr) throws RuntimeException {
        if (colorStr.indexOf(",") >= 0) {
            StringTokenizer st = new StringTokenizer(colorStr, ",");
            try {
                int r = Integer.parseInt(st.nextToken());
                int g = Integer.parseInt(st.nextToken());
                int b = Integer.parseInt(st.nextToken());
                return new Color(r, g, b);
            } catch (NoSuchElementException smullinium) {
                if (Debug.DEBUG) Debug.trace(SyntaxStyle.class, Debug.DP2, smullinium);
                throw new RuntimeException(smullinium.getMessage());
            } catch (NumberFormatException numbForm) {
                if (Debug.DEBUG) Debug.trace(SyntaxStyle.class, Debug.DP2, numbForm);
                throw new RuntimeException(numbForm.getMessage());
            }
        } else {
            //a string representing one of the static fields in the Color class. instantiate it via reflection
            try {
                Field f = Color.class.getField(colorStr);
                return (Color) f.get(null);
            } catch (Exception e) {
                if (Debug.DEBUG) Debug.trace(SyntaxStyle.class, Debug.DP2, e);
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private static final int getStyle(boolean bBold, boolean bItalic) {
        return (bBold)
            ? Font.BOLD + ((bItalic) ? Font.ITALIC : 0)
            : (bItalic) ? Font.ITALIC : Font.PLAIN;
    }

    public static final Font deriveFont(Font baseFont, boolean bold, boolean italic) {
        return baseFont.deriveFont(getStyle(bold, italic));
    }

    private SyntaxStyle(String name,
                        Color foregroundColor,
                        boolean bold,
                        boolean italic,
                        int styleID,
                        SyntaxStyleSet sss) {
        this.name = name;
        this.foregroundColor = foregroundColor;
        this.bold = bold;
        this.italic = italic;
        this.styleID = styleID;
        if (sss != null)
            sss.addStyle(this);
        this.font = deriveFont(bold, italic);
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    private Font deriveFont(boolean bBold, boolean bItalic) {
        int fontStyle = getStyle(bBold, bItalic);
        Font defaultFont = (parentSet == null) ? getDefaultFont() : parentSet.getDefaultFont();
        return defaultFont.deriveFont(fontStyle);
    }

    public void setBold(boolean bold) {
        this.bold = bold;
        this.font = deriveFont(bold, italic);
    }

    public boolean isBold() {
        return this.bold;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
        this.font = deriveFont(bold, italic);
    }

    public boolean isItalic() {
        return this.italic;
    }

    public Font getFont() {
        return font;
    }

    private void setFont(Font font) {
        this.font = font;
    }

    public final String getName() {
        return this.name;
    }

    public final int getID() {
        return this.styleID;
    }

    public String toString() {
        return new StringBuffer("SyntaxStyle [name=")
            .append(getName())
            .append(", id=")
            .append(getID())
            .append(", font=")
            .append(getFont())
            .append(", bold=")
            .append(isBold())
            .append(", italic=")
            .append(isItalic())
            .append(", color=")
            .append(getForegroundColor())
            .append("]")
            .toString();
    }

    /**
     * placing the styleNameMap in this container
     * eases its configuration by the config package.
     */
    public static class SyntaxStyleSet implements Serializable, Cloneable {
        static final long serialVersionUID = -5571840012443245785L;

        public static final String DEFAULT_FONT_PROPERTY = "defaultFont";
        public static final String STYLE_NAME_MAP_PROPERTY = "styleNameMap";
        public static final String CONFIG_DATA_PROPERTY = "configData";

        private HashMap styleNameMap;
        private HashMap styleIDMap;
        private Font defaultFont;

        public SyntaxStyleSet() {
            this(SyntaxStyle.DEFAULT_FONT);
	        SyntaxStyle s1 = new SyntaxStyle("default", Color.black, false, false, 1, this);
	        addStyle(s1);
	        SyntaxStyle s2 = new SyntaxStyle("keyword", Color.blue, false, false, 26, this);
	        addStyle(s2);
        }

        public SyntaxStyleSet(Font defaultFont) {
            this.styleNameMap = new HashMap();
            this.styleIDMap = new HashMap();
            this.defaultFont = defaultFont;
	        SyntaxStyle s1 = new SyntaxStyle("default", Color.black, false, false, 1, this);
	        addStyle(s1);
	        SyntaxStyle s2 = new SyntaxStyle("keyword", Color.blue, false, false, 26, this);
	        addStyle(s2);
        }

        private void addStyle(SyntaxStyle ss) {
            styleNameMap.put(ss.getName(), ss);
            styleIDMap.put(new Integer(ss.getID()), ss);
            ss.parentSet = this;
        }

        public HashMap getStyleNameMap() {
            return this.styleNameMap;
        }

        protected final void clear() {
            styleNameMap.clear();
            styleIDMap.clear();
        }

        public final Iterator styles() {
            return styleNameMap.values().iterator();
        }

        public final void setStyleNameMap(HashMap styleNameMap) {
            this.styleNameMap = styleNameMap;
            styleIDMap.clear();
            for (Iterator it = styles(); it.hasNext();) {
                SyntaxStyle ss = (SyntaxStyle) it.next();
                styleIDMap.put(new Integer(ss.getID()), ss);
            }
        }

        public final SyntaxStyle getStyle(String name) {
            if (styleNameMap.containsKey(name))
                return (SyntaxStyle) styleNameMap.get(name);
            return null;
        }

        public final SyntaxStyle getStyle(int id) {
            Integer idInt = new Integer(id);
            if (styleIDMap.containsKey(idInt))
                return (SyntaxStyle) styleIDMap.get(idInt);
            return null;
        }

        public final String getConfigData() {
            StringWriter sw = new StringWriter();
            try {
                writeConfig(new BufferedWriter(sw));
            } catch (IOException oyVeh) {
                return null;
            }
            return sw.getBuffer().toString();
        }

        public final void setConfigData(String configData, boolean clearOut) {
            if (clearOut) clear();
            setConfigData(configData);
        }

        public final void setConfigData(String configData) {
            StringReader sr = new StringReader(configData);
            try {
                readConfig(new BufferedReader(sr));
            } catch (IOException oyVeh) {
                //will already be logged
            }
        }

        public Font getDefaultFont() {
            return this.defaultFont;
        }

        public void setDefaultFont(Font f) {
            this.defaultFont = f;
            for (Iterator it = styles(); it.hasNext();) {
                SyntaxStyle ss = (SyntaxStyle) it.next();
                ss.setFont(SyntaxStyle.deriveFont(f, ss.isBold(), ss.isItalic()));
            }
        }

        private static final String CONFIG_BOILERPLATE =
            "####################################################################"
            + "\n" +
            "# NAME          COLOR           BOLD            ITALIC          NO #"
            + "\n" +
            "####################################################################"
            + "\n";

        private static final int CONFIG_TAB_SIZE = 16;

        /**
         * writes the state of the style set in a config file format to the stream.
         * @param brighter the stream to which to write
         */
        public void writeConfig(BufferedWriter brighter)
            throws IOException {
            StringBuffer sb = new StringBuffer(CONFIG_BOILERPLATE);
            for (Iterator it = (new TreeSet(styleIDMap.keySet())).iterator(); it.hasNext();) {
                SyntaxStyle ss = (SyntaxStyle) styleIDMap.get(it.next());
                sb.append(pad(ss.getName(), CONFIG_TAB_SIZE));
                formatColor(sb, ss.getForegroundColor());
                sb.append(pad(new Boolean(ss.isBold()).toString(), CONFIG_TAB_SIZE));
                sb.append(pad(new Boolean(ss.isItalic()).toString(), CONFIG_TAB_SIZE));
                sb.append(ss.getID());
                sb.append("\n");
            }
            try {
                brighter.write(sb.toString(), 0, sb.length());
                brighter.flush();
                brighter.close();
            } catch (IOException oyVeh) {
                if (Debug.DEBUG) Debug.trace(this, Debug.DP2, oyVeh);
                throw oyVeh;
            }
        }

        private void formatColor(StringBuffer sb, Color c) {
            int startlen = sb.length();
            sb.append(c.getRed());
            sb.append(",");
            sb.append(c.getGreen());
            sb.append(",");
            sb.append(c.getBlue());
            int padding = Math.max(0, CONFIG_TAB_SIZE - (sb.length() - startlen));
            for (int i = 0; i < padding; i++)
                sb.append(" ");
        }

        private String pad(String s, int length) {
            StringBuffer sb = new StringBuffer(s);
            while (sb.length() < length)
                sb.append(" ");
            return sb.toString();
        }

        /**
         * sets the style set according the config information in the stream,
         * in the same format generated by writeConfig (and in "default.styles", the
         * default config file).
         * @param breader the stream from which to read the config information.
         */
        public void readConfig(BufferedReader breader)
            throws IOException {
            try {
                String line;
                while ((line = breader.readLine()) != null) {
                    if (line.trim().startsWith("#"))
                        continue;
                    StringTokenizer st = new StringTokenizer(line);
                    String name = st.nextToken();
                    Color color = SyntaxStyle.getColor(st.nextToken());
                    boolean bBold = new Boolean(st.nextToken()).booleanValue();
                    boolean bItalic = new Boolean(st.nextToken()).booleanValue();
                    int number = Integer.parseInt(st.nextToken());
                    new SyntaxStyle(name,
                                    color,
                                    bBold,
                                    bItalic,
                                    number,
                                    this);
                }
            } catch (Exception oyVeh) {
                if (Debug.DEBUG) Debug.trace(SyntaxStyleSet.class, Debug.DP2, oyVeh);
                throw new IOException(oyVeh.getMessage());
            }
        }
    }
}