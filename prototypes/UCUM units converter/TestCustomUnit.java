/*
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Oct 1, 2002
 * Time: 5:12:13 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TestCustomUnit {

    public static void TestCustomUnitBuild() {
        CustomUnitBuildPanel p = new CustomUnitBuildPanel();
        showTestFrame("Custom Units",p);
    }

    public static void showTestFrame(String title, JComponent content) {
        JFrame f = new JFrame(title);
        f.getContentPane().add(content);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.addWindowListener(new TestFrameListener());
        f.pack();
        f.setVisible(true);
    }

    static class TestFrameListener extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
	        ((JFrame)event.getSource()).dispose();
        }
    }

    public static void main(String[] args) {
      /*try {
        String laf = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(laf);
        } catch (Exception ex) { ex.getMessage(); }
        */
      TestCustomUnitBuild();
    }

}
