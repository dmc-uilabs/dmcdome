package jacob;

import jacob.rightclickmenu.SketchPad;
import jacob.threads.SimpleThread;

import javax.swing.*;
import java.awt.event.MouseListener;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 14, 2004
 * Time: 9:33:13 PM
 * To change this template use Options | File Templates.
 */
public class MainMethod
{
//    public static void main(String[] args)
//    {
//        JFrame f = new JFrame();
//
//        f.getContentPane().add(new SketchPad());
//
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.show();
//        f.pack();
//    }

    public static void main(String[] args)
    {
        SimpleThread bob = new SimpleThread("Bob");
        Thread bobThread = new Thread(bob);
        SimpleThread joe = new SimpleThread("Joe");
        Thread joeThread = new Thread(joe);

        bobThread.start();
        joeThread.start();
    }
}
