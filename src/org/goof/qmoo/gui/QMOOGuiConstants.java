package org.goof.qmoo.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 8, 2004
 * Time: 12:12:02 PM
 * To change this template use Options | File Templates.
 */
public class QMOOGuiConstants
{
    public static final String STANDARD_FONT_NAME = "Dialog";
    public static final Font FONT9 = new Font(STANDARD_FONT_NAME, Font.PLAIN, 9);
    public static final Font FONT9B = new Font(STANDARD_FONT_NAME, Font.BOLD, 9);
    public static final Font FONT9I = new Font(STANDARD_FONT_NAME, Font.ITALIC, 9);
    public static final Font FONT10 = new Font(STANDARD_FONT_NAME, Font.PLAIN, 10);
    public static final Font FONT10B = new Font(STANDARD_FONT_NAME, Font.BOLD, 10);
    public static final Font FONT10I = new Font(STANDARD_FONT_NAME, Font.ITALIC, 10);
    public static final Font FONT11 = new Font(STANDARD_FONT_NAME, Font.PLAIN, 11);
    public static final Font FONT11B = new Font(STANDARD_FONT_NAME, Font.BOLD, 11);
    public static final Font FONT11I = new Font(STANDARD_FONT_NAME, Font.ITALIC, 11);
    public static final Font FONT12 = new Font(STANDARD_FONT_NAME, Font.PLAIN, 12);
    public static final Font FONT12B = new Font(STANDARD_FONT_NAME, Font.BOLD, 12);
    public static final Font FONT12I = new Font(STANDARD_FONT_NAME, Font.ITALIC, 12);

    // tabbed panes
	public static JTabbedPane makeTabbedPane()
	{ // default tabs on bottom
		JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT11);
		tabs.setTabPlacement(JTabbedPane.BOTTOM);
		return tabs;
	}
}
