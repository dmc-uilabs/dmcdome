// GuiConstants.java
package mit.cadlab.dome.swing;

import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Font;

public interface GuiConstants {

  public static final GridBagConstraints gbc=null;

  public static final Color STALE_COLOR = new Color(255,255,128);
  public static final Color IN_PROCESS_COLOR = new Color(128,255,255);
  public static final Color CURRENT_COLOR = Color.white;
  public static final Color NOT_EDITABLE_COLOR = new Color(153,153,153);
  public static final Color DARKER_BACKGROUND_COLOR = new Color(170,170,170);

  public static final String STANDARD_FONT_NAME = "Dialog";
  public static final Font FONT9 = new Font(STANDARD_FONT_NAME, Font.PLAIN, 9);
  public static final Font FONT9B = new Font(STANDARD_FONT_NAME, Font.BOLD,9);
  public static final Font FONT9I = new Font(STANDARD_FONT_NAME, Font.ITALIC,9);
  public static final Font FONT10 = new Font(STANDARD_FONT_NAME, Font.PLAIN,10);
  public static final Font FONT10B = new Font(STANDARD_FONT_NAME, Font.BOLD,10);
  public static final Font FONT10I = new Font(STANDARD_FONT_NAME, Font.ITALIC,10);
  public static final Font FONT11 = new Font(STANDARD_FONT_NAME, Font.PLAIN, 11);
  public static final Font FONT11B = new Font(STANDARD_FONT_NAME, Font.BOLD, 11);
  public static final Font FONT11I = new Font(STANDARD_FONT_NAME, Font.ITALIC, 11);
  public static final Font FONT12 = new Font(STANDARD_FONT_NAME, Font.PLAIN, 12);
  public static final Font FONT12B = new Font(STANDARD_FONT_NAME, Font.BOLD, 12);
  public static final Font FONT12I = new Font(STANDARD_FONT_NAME, Font.ITALIC, 12);
  
  public static final String[] FRAME_ICON_COLORS = {
    "234,107,72", "255,249,157", "169,212,109", "63,187,239",
    "106,79,154", "166,166,166", "150,0,24", "245,183,87",
    "82,132,19", "0,119,158", "38,7,81", "26,26,26" };

}
