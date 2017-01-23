// RtfPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.JMenu;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

public class RtfPanel extends JTextPane {

    public RtfPanel() {
        setEditorKit(new RTFEditorKit());
    }

    // support for easily reading/writing to Strings
    // to do: how to tell when it is empty?
    public String getRtfText() {
        try {
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            StyledDocument doc = (StyledDocument)getDocument();
            int length=doc.getLength();
            SimpleAttributeSet attr=new SimpleAttributeSet();
            StyleConstants.setForeground(attr, Color.black);
            StyleConstants.setFontSize(attr,12);
            StyleConstants.setFontFamily(attr,"Default");
            StyleConstants.setAlignment(attr,StyleConstants.ALIGN_LEFT);
            doc.setParagraphAttributes(0,length,attr,false);
            getEditorKit().write(writer,doc,0,doc.getLength());
            writer.close();
            return writer.toString();
        } catch (Exception ex) {
            System.out.println(ex);
            return "";
        }
    }

    public void setRtfText(String rtfText) {
        if (rtfText == null || rtfText.equals(""))
            return;
        StringReader sr = new StringReader(rtfText);
        try {
            getEditorKit().read(sr,getDocument(),0);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }


    public static JMenu createStyleMenu() {
        JMenu menu = MenuUtils.makeBoldMenu("Format documentation");

        JMenu styleMenu = MenuUtils.makeMenu("Font style");
        styleMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.BoldAction(),"Bold"));
        styleMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ItalicAction(),"Italic"));
        styleMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.UnderlineAction(),"Underline"));
        menu.add(styleMenu);
        menu.addSeparator();

        JMenu sizeMenu = MenuUtils.makeMenu("Font size");
        sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("12", 12)));
        sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("14", 14)));
        sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("16", 16)));
        sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("18", 18)));
        sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("24", 24)));
        sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("36", 36)));
        sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("48", 48)));
        menu.add(sizeMenu);
        menu.addSeparator();

        JMenu familyMenu = MenuUtils.makeMenu("Font family");
        familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Sans Serif","SansSerif")));
        familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Serif","Serif")));
        familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Dialog","Dialog")));
        familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Dialog Input","DialogInput")));
        familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Monospaced","Monospaced")));
        menu.add(familyMenu);
        menu.addSeparator();

        JMenu colorMenu = MenuUtils.makeMenu("Font color");
        colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Black",Color.black)));
        colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Blue",Color.blue)));
        colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Cyan",Color.cyan)));
        colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Green",Color.green)));
        colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Magenta",Color.magenta)));
        colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Orange",Color.orange)));
        colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Pink",Color.pink)));
        colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Red",Color.red)));
        colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Yellow",Color.yellow)));
        menu.add(colorMenu);
        /*menu.addSeparator();

        JMenu justifyMenu = MenuUtils.makeMenu("Justification");
        justifyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.AlignmentAction("Left justify", StyleConstants.ALIGN_LEFT)));
        justifyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.AlignmentAction("Center justify", StyleConstants.ALIGN_CENTER)));
        justifyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.AlignmentAction("Right justify", StyleConstants.ALIGN_RIGHT)));
        menu.add(justifyMenu);*/

        return menu;
    }

}
