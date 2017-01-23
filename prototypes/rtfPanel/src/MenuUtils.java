// MenuUtils.java

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Convenience functions for creating menus and menu items.
 */
public class MenuUtils implements GuiConstants {

    // menus
    public static JMenu makeMenu(String text) {
        return makeMenu(text,FONT12);
    }

    public static JMenu makeBoldMenu(String text) {
        return makeMenu(text,FONT12B);
    }

    public static JMenu makeMenu(String text, Font f) {
        JMenu m = new JMenu(text);
        m.setFont(f);
        return m;
    }

    // menu items
    public static JMenuItem makeMenuItem(String text) {
        JMenuItem mi = new JMenuItem(text);
        mi.setFont(FONT12);
        return mi;
    }

    public static JMenuItem makeMenuItem(Action action) {
        JMenuItem mi = new JMenuItem(action);
        mi.setFont(FONT12);
        return mi;
    }

    public static JMenuItem makeMenuItem(Action action, String text) {
        JMenuItem mi = new JMenuItem(action);
        mi.setText(text); // override action text
        mi.setFont(FONT12);
        return mi;
    }

    // checkbox menu item
    public static JCheckBoxMenuItem makeCheckBoxMenuItem(String text) {
        JCheckBoxMenuItem mi = new JCheckBoxMenuItem(text);
        mi.setFont(FONT12);
        return mi;
    }

    // for testing
    public static JMenu makeTestMenu(String text, String[] items) {
        JMenu m = makeBoldMenu(text);
        addTestMenuItems(m,items);
        return m;
    }

    public static JMenu makeTestSubMenu(String text, String[] items) {
        JMenu m = makeMenu(text);
        addTestMenuItems(m,items);
        return m;
    }

    public static void addTestMenuItems(JMenu menu, String[] items) {
        for (int i=0; i<items.length; ++i) {
            String item = items[i];
            if (item.equals("--"))
                menu.addSeparator();
            else
                menu.add(makeTestMenuItem(item));
        }
    }

    public static JMenuItem makeTestMenuItem(String text) {
        return makeMenuItem(new TestMenuAction(text));
    }

    public static class TestMenuAction extends AbstractAction {

        public TestMenuAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("Selected: "+getValue(Action.NAME));
        }

    }

}
