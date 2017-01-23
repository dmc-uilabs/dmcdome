// DialogFactory.java
package mit.cadlab.dome.swing;

import mit.cadlab.dome.swing.Templates;
import java.awt.Component;
import java.awt.Window;
import java.awt.Dialog;
import java.awt.Container;
import java.awt.Frame;
import javax.swing.SwingUtilities;
import javax.swing.JDialog;

public class DialogFactory {
  
  public static JDialog createDialog(Component parentComponent,
				     String title,
				     Component contentComponent,
				     boolean modal,
				     boolean isResizable) {
    JDialog dialog;
    Window window = getWindowForComponent(parentComponent);
    if (window instanceof Frame) {
      dialog = new JDialog((Frame)window, title, modal);	
    } else {
      dialog = new JDialog((Dialog)window, title, modal);
    }		
    Container contentPane = dialog.getContentPane();
    contentPane.add(contentComponent);
    dialog.setResizable(isResizable);
    dialog.pack();
    dialog.setLocationRelativeTo(parentComponent);
    return dialog;
  }
  
  protected static Window getWindowForComponent(Component parentComponent) {
    if (parentComponent == null)
      return getSharedOwnerFrame();
    return SwingUtilities.windowForComponent(parentComponent);
  }
  
  protected static Frame sharedOwnerFrame;
  
  protected static Frame getSharedOwnerFrame() {
    if (sharedOwnerFrame == null) {
      sharedOwnerFrame = new Frame() {
	  public void show() {
	    // This frame can never be shown
	  }
	  public synchronized void dispose() {
	    try {
	      sharedOwnerFrame.getToolkit().getSystemEventQueue();
	      sharedOwnerFrame.dispose();
	    } catch (Exception e) {
	      // untrusted code not allowed to dispose
	    }
	  }
	};
      sharedOwnerFrame.setIconImage(Templates.makeImageIcon("mit/cadlab/dome/icons/domeWindow.gif").getImage());
    }
    return sharedOwnerFrame;
  }

}
