package mit.cadlab.dome3.plugin.catalog.ui;

import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 3. 1.
 */
public class CodeCompletionCandidate {

    public static final int VARNAME_CC_TYPE = 0;
    public static final int PATTERN_CC_TYPE = 1;
    public static final int DIMENSION_CC_TYPE = 2;
    public static final int NO_SUGGESTION_TYPE = -1;

    public static final String CANDIDATE_NM_FG = "#000000";
    public static final String CANDIDATE_HL_FG = "#FFFFFF";
    public static final String CROPPING_NM_FG = "#C2279F";
    public static final String CROPPING_HL_FG = "#F3C7EA";

    public static final Color POPUP_NM_BG = new Color(0xEC, 0xF5, 0xFF);
    public static final Color POPUP_HL_BG = new Color(0x00, 0x40, 0x80);
    public static final Color POPUP_ER_BG = new Color(0xFF, 0xCA, 0xCA);

    private int type;
    private String text;
    private int cropPos;


    public CodeCompletionCandidate(int type, String text, int cropPos) {
        this.type = type;
        this.text = text;
        this.cropPos = cropPos;
    }

    /** used for insertion when this candidate is chosen */
    public String getInsertedText() {
        if (cropPos > 0) {
            return text.substring(cropPos);
        } else {
            return text;
        }
    }

    /** one of VARNAME_CC_TYPE, PATTERN_CC_TYPE, and DIMENSION_CC_TYPE */
    public int getType() {
        return type;
    }

    /** to retrieve original candidate text */
    public String getCandidateText() {
        return text;
    }

    /** get the cropped string when this candidate is inserted */
    public String getCroppedText() {
        return text.substring(0, cropPos);
    }

    /** used for JList visualization */
    public String getHtmlText(boolean isSelected) {
        if (type == NO_SUGGESTION_TYPE) {
            return "<html><font color=" + CANDIDATE_NM_FG + ">" + text + "</font></html>";
        }

        String candColor;
        String cropColor;
        if (isSelected) {
            candColor = CANDIDATE_HL_FG;
            cropColor = CROPPING_HL_FG;

        } else {
            candColor = CANDIDATE_NM_FG;
            cropColor = CROPPING_NM_FG;
        }

        return "<html><font color=" + cropColor + ">" + text.substring(0, cropPos) + "</font><font color=" + candColor + ">" + text.substring(cropPos) + "</font></html>";
    }

    public Color getBackground(boolean isSelected) {
        if (type == NO_SUGGESTION_TYPE) {
            return POPUP_ER_BG;
        }

        return (isSelected ? POPUP_HL_BG: POPUP_NM_BG);
    }

    public String toString() {
        String typeStr = null;
        switch (type) {
            case 0:
                typeStr = "varname"; break;
            case 1:
                typeStr = "pattern"; break;
            case 2:
                typeStr = "dimension"; break;
            case -1:
                typeStr = "no_suggestion"; break;
        }
        return "[Candidate: type=" + typeStr + ", text=" + text + ", cropPos=" + cropPos + "]";
    }
}
