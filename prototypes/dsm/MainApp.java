package dsm;

import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.Toolkit;


public class MainApp {
  
 
  
    int reach[][]=      {{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {1,1,1,1,1,0,0,1,1,0,0,0,0,0,0},
                        {1,1,0,1,1,0,0,0,1,0,0,0,0,0,0},
                        {0,1,0,0,1,0,0,0,0,0,0,0,0,0,0},
                        {0,1,0,0,0,1,0,0,0,0,0,0,0,0,0},
                        {1,1,1,1,1,0,1,1,1,0,0,0,0,0,0},
                        {1,1,1,1,1,0,0,1,1,0,0,0,0,0,0},
                        {1,1,0,1,1,0,0,0,1,0,0,0,0,0,0},
                        {0,1,0,0,1,1,0,0,0,1,0,0,0,0,0},
                        {1,1,1,1,1,0,1,1,1,0,1,0,0,0,0},
                        {1,1,1,1,1,0,1,1,1,0,1,1,0,0,0},
                        {1,1,0,1,1,0,0,0,1,0,0,0,1,0,0},
                        {0,1,0,0,1,1,0,0,0,1,0,0,0,1,0},
                        {0,1,0,0,1,1,0,0,0,1,0,0,0,0,1}};

  String title[] = {"Jacob Wronski", "Charles Dumont", "Elaine Yang", "Qing Cao", "David Wallace", "Jacob Wronski", "Charles Dumont", "Elaine Yang", "Keith Thoresz", "Jacob Wronski", "Jacob Wronski", "Jacob Wronski", "Jacob Wronski", "Charles Dumont", "Elaine Yang"};
  boolean packFrame = false;

	public MainApp()
	{
		String[] names = new String[title.length];
		for(int k=0; k<title.length; k++)
		{
			names[k] = new Integer(k+1).toString();
		}
		DSMFrame frame = new DSMFrame(reach,names, title);
		frame.pack();
		frame.redraw();
//		frame.validate();

		//Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	//Main method
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		new MainApp();
	}
}

