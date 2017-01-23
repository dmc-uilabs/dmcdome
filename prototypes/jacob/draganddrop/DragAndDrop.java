package jacob.draganddrop;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Mar 22, 2004
 * Time: 5:46:44 PM
 * To change this template use Options | File Templates.
 */

/**
 * Original class name: DragTwo.java
 * Source: Java Tutorials (java.sun.com)
 * Looking at sample implementations of drag and drop
 */


import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;

public class DragAndDrop
{

    public static void main(String args[])
    {

        JFrame frame = new JFrame("Second Drag");
        frame.setDefaultCloseOperation
                (JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();

        Toolkit kit = Toolkit.getDefaultToolkit();
        final Clipboard clipboard =
                kit.getSystemClipboard();

        JTextField tf = new JTextField();
        contentPane.add(tf, BorderLayout.NORTH);

        Icon icon = new ImageIcon("scott.jpg");
        JLabel label1 = new JLabel(icon);
        label1.setTransferHandler(new ImageSelection());

        MouseListener pressListener = new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler =
                        comp.getTransferHandler();
                handler.exportAsDrag
                        (comp, e, TransferHandler.COPY);
            }
        };
        label1.addMouseListener(pressListener);

        TransferHandler handler = new
                TransferHandler("text");
        JLabel label2 = new JLabel("Drag Me");
        label2.setTransferHandler(handler);
        label2.addMouseListener(pressListener);

        JPanel panel = new JPanel();
        panel.add(label1);
        panel.add(label2);
        contentPane.add(panel, BorderLayout.SOUTH);

        final JLabel dropZone = new JLabel();
        dropZone.setTransferHandler(new ImageSelection());
        MouseListener releaseListener =
                new MouseAdapter()
                {
                    public void mouseReleased(MouseEvent e)
                    {
                        Transferable clipData = clipboard.getContents
                                (clipboard);
                        if (clipData != null)
                        {
                            if (clipData.isDataFlavorSupported
                                    (DataFlavor.imageFlavor))
                            {
                                TransferHandler handler =
                                        dropZone.getTransferHandler();
                                handler.importData(dropZone, clipData);
                            }
                        }
                    }
                };
        dropZone.addMouseListener(releaseListener);

        JScrollPane pane = new JScrollPane(dropZone);
        contentPane.add(pane, BorderLayout.CENTER);

        frame.setSize(400, 400);
        frame.show();
    }
}
