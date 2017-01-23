package mit.cadlab.dome3.plugin.vensim.sample;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import com.vensim.Vensim;

/**
* VensimApp.java
* This application connects to the Full Vensim DLL (vendll32).
* To connect to the minimal DLL initialize the class Vensim
* using vendlm32
*/
public class VensimApp extends Frame implements ActionListener
{
	private Vensim vensim = new Vensim("vendll32") ; /* vendml32 for the minimal dll */
	private OutputCanvas oc;

	final static int APPWIDTH = 600;
	final static int APPHEIGHT = 400;

	private Label l1, l2, l3, l4;
	private TextField tf1, tf2, tf3, tf4;
	private TextArea ta1;
	private Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b10;
	private Panel p1, p2, p3, p4, p5;

	private int tpoints, result;
	float rval[], tval[];
	float population[] = new float[2];
	float populationinitial[] = new float[2];
	float birthrate[] = new float[2];
	float capitalinvestmentratenormal[] = new float[2];

	private String popInit, birthRate, capitalInvestmentRateNormal, vensimCommand;


	/**
	* Constructor creates the application interface
	*/
	public VensimApp()
	{
		setSize( APPWIDTH, APPHEIGHT );
		setLayout( new BorderLayout(20,20) );
		setTitle( "Java to Vensim Interface" );


		p1 = new Panel();
		p1.setLayout( new BorderLayout(20,20) );
		add( p1, BorderLayout.WEST );

		p2 = new Panel();
		p2.setLayout( new BorderLayout(20,20) );
		add( p2, BorderLayout.CENTER );

		p3 = new Panel();
		p3.setLayout( new GridLayout(3,2) );
		p1.add( p3, BorderLayout.NORTH );

		p4 = new Panel();
		p4.setLayout( new GridLayout(5,2,10,10) );
		p1.add( p4, BorderLayout.CENTER );

		p5 = new Panel();
		p5.setLayout( new FlowLayout() );
		p1.add( p5, BorderLayout.SOUTH );

		//add label and input field to panel 3
		l1 = new Label( "Set initial population" );
		p3.add( l1 );
		tf1 = new TextField( 10 );
		p3.add( tf1 );

		//add label and input field to panel 3
		l2 = new Label( "Set birth rate" );
		p3.add( l2 );
		tf2 = new TextField( 10 );
		p3.add( tf2 );

		//add label and input field to panel 3
		l3 = new Label( "Set capital invest rate" );
		p3.add( l3 );
		tf3 = new TextField( 10 );
		p3.add( tf3 );

		//add label and output field to panel 5
		l4 = new Label( "Population = " );
		p5.add( l4 );
		tf4 = new TextField( 15 );
		tf4.setEditable(false);
		p5.add( tf4 );

		//add buttons to panel 4
		b1 = new Button( "Load Model" );
		p4.add( b1 );
		b1.addActionListener( this );

		b2 = new Button( "Simulate" );
		p4.add( b2 );
		b2.addActionListener( this );

		b3 = new Button( "Setup Game" );
		p4.add( b3 );
		b3.addActionListener( this );

		b4 = new Button( "Graph Results" );
		p4.add( b4 );
		b4.addActionListener( this );

		b5 = new Button( "Continue Game" );
		p4.add( b5 );
		b5.addActionListener( this );

		b6 = new Button( "Finish Game" );
		p4.add( b6 );
		b6.addActionListener( this );

		b7 = new Button( "Show status" );
		p4.add( b7 );
		b7.addActionListener( this );

		b8 = new Button( "Get Varnames" );
		p4.add( b8 );
		b8.addActionListener( this );

		b9 = new Button( "get varattrib" );
		p4.add( b9 );
		b9.addActionListener( this );

		b10 = new Button( "get info" );
		p4.add( b10 );
		b10.addActionListener( this );

		//add output area to panel 2
		ta1 = new TextArea("This string appears in the text area", 10,50);
		ta1.setEditable( false );
		p2.add( ta1, BorderLayout.NORTH );


		//Set up a canvas for panel 2.  The color background is working but the setSize is overridden to stretch to fit
		oc = new OutputCanvas();
		oc.setBackground( Color.white);
		p2.add( oc, BorderLayout.CENTER );


		//Make the application visible
		setVisible( true );

		// create arrays for time and values, hardwired array length
		tval = new float[41];
		rval = new float[41];

//		result = vensim.be_quiet(2);
//		if (result == 0)
//			System.out.println( "Vensim be quiet failed" );



	}





	public void actionPerformed ( ActionEvent e )
	{
		if (e.getSource() == b1)
		{
			//needs a backslash as an escape character for the DOS directory backslash
                        result = vensim.command( "SPECIAL>LOADMODEL|c:\\program files\\vensim\\dll\\worldapp.vpm" );
			if (result == 0)
                                System.out.println( "SPECIAL>LOADMODEL|c:\\program files\\vensim\\dll\\worldapp.vpm -- failed" );
			else if (result == 1)
			{
				System.out.println( "Vensim loaded model" );
				ta1.setText("Vensim loaded model");
			}


			//get the value for population initial and put it in the text field
			result = vensim.get_val("population initial", populationinitial);
			if (result == 0)
			{
				System.out.println( "get_val  -- failed" );
				ta1.setText("Failed to get population initial");
			}

			tf1.setText(Float.toString(populationinitial[0]));

			//get the value for birth rate and put it in the text field
			result = vensim.get_val("birth rate normal", birthrate);
			if (result == 0)
			{
				System.out.println( "get_val  -- failed" );
				ta1.setText("Failed to get birth rate normal");
			}

			tf2.setText(Float.toString(birthrate[0]));

			//get the value for capital investment rate normal c and put it in the text field
			result = vensim.get_val("capital investment rate normal c", capitalinvestmentratenormal);
			if (result == 0)
			{
				System.out.println( "get_val  -- failed" );
				ta1.setText("Failed to get capital investment rate normal c");
			}

			tf3.setText(Float.toString(capitalinvestmentratenormal[0]));

		}
		else if (e.getSource() == b2)
		{

			//set run name
			result = vensim.command( "SIMULATE>RUNNAME|base" );
			if (result == 0)
				System.out.println( "SIMULATE>RUNNAME|base -- failed" );

			//set population initial to the input field value
			popInit = tf1.getText();
			System.out.println( "popInit = " + popInit );
			vensimCommand = "SIMULATE>SETVAL|population initial = " + popInit;
			System.out.println( "vensimCommand = " + vensimCommand );
			result = vensim.command( vensimCommand );
			if (result == 0)
				System.out.println( "SIMULATE>SETVAL|population initial = " + popInit + " -- failed" );

			//set the birth rate to the input field value
			birthRate = tf2.getText();
			System.out.println( "birthRate = " + birthRate );
			vensimCommand = "SIMULATE>SETVAL|birth rate normal = " + birthRate;
			System.out.println( "vensimCommand = " + vensimCommand );
			result = vensim.command( vensimCommand );
			if (result == 0)
				System.out.println( "SIMULATE>SETVAL|birth rate normal = " + birthRate + " -- failed" );

			//set the capital investment rate to the input field value
			capitalInvestmentRateNormal = tf3.getText();
			System.out.println( "capitalInvestmentRateNormal = " + capitalInvestmentRateNormal );
                        vensimCommand = "SIMULATE>SETVAL|capital investment rate normal c = " + capitalInvestmentRateNormal;
			System.out.println( "vensimCommand = " + vensimCommand );
			result = vensim.command( vensimCommand );
			if (result == 0)
				System.out.println( vensimCommand + " -- failed" );


			//simulate model
			result = vensim.command( "MENU>RUN|O" );
			if (result == 0)
				System.out.println( "MENU>RUN|O -- failed" );

			/**
			* Check the status of vensim
			*/
			result = vensim.check_status();
			if (result != 0) {
				System.out.println( "Check Status -- failed" );

		          System.out.println( "Check Vensim Status = : " + result );
                    }

			//output of data
			tpoints = vensim.get_data( "base.vdf", "Population", "time", rval, tval, 41 );

			StringBuffer buf = new StringBuffer("Population \n");

			for (int i = 0; i < tpoints; i++)
			{
				buf.append(Float.toString(tval[i]));
				buf.append("\t");
			}

			buf.append("\n");

			for (int i = 0; i < tpoints; i++)
			{
				buf.append(Float.toString(rval[i]));
				buf.append("\t");
			}

			//write the stringbuffer to the output area
			ta1.setText(buf.toString());

			//get the value for Population and put it in the text field
			result = vensim.get_val("population", population);
			if (result == 0)
			{
				System.out.println( "get_val  -- failed" );
				ta1.setText("Failed to get Population Values");
			}

            //get the value for Population and put it in the text field
			float[] initTime = new float[1];
            result = vensim.get_val("INITIAL TIME", initTime);
            System.out.println("initTime[0] = " + initTime[0]);
            System.out.println("initTime result = " + result);

            float[] finalTime = new float[1];
            result = vensim.get_val("FINAL TIME", finalTime);
            System.out.println("finalTime[0] = " + finalTime[0]);
            System.out.println("finalTime result = " + result);

            float[] timeStep = new float[1];
            result = vensim.get_val("TIME STEP", timeStep);
            System.out.println("timeStep[0] = " + timeStep[0]);
            System.out.println("timeStep result = " + result);

			float[] population = new float[1];
            result = vensim.get_val("Population", population);
            System.out.println("population[0] = " + (double) population[0]);
            System.out.println("population result = " + result);

			float[] capital = new float[1];
            result = vensim.get_val("food coefficient", capital);
            System.out.println("food coefficient[0] = " + (double) capital[0]);
            System.out.println("food coefficient result = " + result);

            float[] callbackValue = new float[1];

            result = vensim.get_val("crowding", callbackValue);
            System.out.println("crowding = " + callbackValue [0]);

            result = vensim.get_val("quality crowding mult tab", callbackValue);
            System.out.println("quality crowding mult tab = " + callbackValue [0]);



			tf4.setText(Float.toString(rval[tpoints-1]));



		}
		else if (e.getSource() == b3)
		{
			//SETUP A GAME
			result = vensim.command("GAME>GAMEINTERVAL|20");
			if (result == 0)
				ta1.setText("set game interval fails");
			else
				ta1.setText("set game interval succeeds");

			result = vensim.command("MENU>GAME");
			if (result == 0)
				ta1.setText("Start game fails");
			else
				ta1.setText("Start game succeeds");


			/**
			* Check the status of vensim
			result = vensim.start_simulation(1,1,1);
			if (result != 5)
				System.out.println( "Start a gaming simulation -- failed" );
			else
				ta1.setText("Vensim started a game");
			*/



		}
		else if (e.getSource() == b4)
		{
			System.out.println( "tval[0] is: " + tval[0] );
			System.out.println( "rval[0] is: " + rval[0] );
			System.out.println( "tval[40] is: " + tval[40] );
			System.out.println( "rval[40] is: " + rval[40] );
			// draw x,y graph
			// oc.draw(tval, rval, graphXPosition, graphYPosition, graphwidth, graphheight, "name");
			oc.draw(tval, rval, 125, 30, 150, 80, "Population" );
		}
		else if (e.getSource() == b5)
		{
			//CONTINUE A GAME
			//set the capital investment rate to the input field value
			capitalInvestmentRateNormal = tf3.getText();
			System.out.println( "capitalInvestmentRateNormal = " + capitalInvestmentRateNormal );
			vensimCommand = "SIMULATE>SETVAL|capital investment rate normal = " + capitalInvestmentRateNormal;
			System.out.println( "vensimCommand = " + vensimCommand );
			result = vensim.command( vensimCommand );
			if (result == 0)
				System.out.println(vensimCommand + " -- failed" );

			//continue the simulation game
			result = vensim.command("GAME>GAMEON") ;
			if (result == 1)
			{
				System.out.println( "game in progress" );
				ta1.setText("game in progress");
			}
			else
			{
				System.out.println( "Vensim failed to continue game" );
				ta1.setText("Vensim failed to continue game");
			}

			//get the value for Population and put it in the text field
			result = vensim.get_val("population", population);
			if (result == 0)
			{
				System.out.println( "get_val  -- failed" );
				ta1.setText("Failed to get Population Values");
			}

			tf4.setText(Float.toString(population[0]));

		}
		else if (e.getSource() == b6)
		{
			result = vensim.command("GAME>ENDGAME");
			if (result == 0)
			{
				System.out.println( "end game  -- failed" );
			}
		}


		else if (e.getSource() == b7)
		{
			result = vensim.check_status();
			ta1.setText("Status is " + result) ;
		}
		else if (e.getSource() == b8)
		{
		// get the variable names
		    String vlist[] = vensim.get_varnames("*",0) ;
            int n = vlist.length ;
			if(n > 0) {
     		   StringBuffer buf = new StringBuffer("") ;
			   for(int j=0;j<n;j++) {
   			      buf.append(vlist[j]) ;
			      buf.append("\n") ;
			      }
			   ta1.setText(buf.toString());
			   }
			else
			   ta1.setText("No matches to wildcard") ;

		}
		else if (e.getSource() == b9)
		{
		// get a variable attrib
		    //String vlist[] = vensim.get_varattrib("population",4) ;
            String vlist[] = vensim.get_varattrib("INIT TIME",4) ;
            int n = vlist.length ;
			if(n > 0) {
     		   StringBuffer buf = new StringBuffer("") ;
			   for(int j=0;j<n;j++) {
   			      buf.append(vlist[j]) ;
			      buf.append("\n") ;
			      }
			   ta1.setText(buf.toString());
			   }
			else
			   ta1.setText("No info on variable") ;


		}
		else if (e.getSource() == b10)
		{
            float[] callbackValue = new float[1];

            result = vensim.get_val("crowding", callbackValue);
            System.out.println("crowding1 = " + callbackValue [0]);

            result = vensim.get_val("quality crowding mult tab", callbackValue);
            System.out.println("quality crowding mult tab = " + callbackValue [0]);

            tpoints = vensim.get_data("base.vdf", "crowding", "time", rval, tval, 41);
            System.out.println("tpoints = " + tpoints);
            System.out.println("crowding2 = " + rval [tpoints - 1]);

		   /* vensim get info */
		    String tlist[] = vensim.get_info(vensim.INFO_DLL) ;
		    String vlist[] = vensim.get_info(vensim.INFO_VERSION) ;
			if(tlist.length + vlist.length > 0) {
     		   StringBuffer buf = new StringBuffer("") ;
			   buf.append("Vensim ") ;
			   for(int j=0;j<tlist.length;j++)
   			      buf.append(tlist[j]) ;
			   buf.append(" DLL Version ") ;
			   for(int j=0;j<vlist.length;j++)
   			      buf.append(vlist[j]) ;
  		       buf.append("\n") ;
			   ta1.setText(buf.toString());
			   }
			else
			   ta1.setText("No info available") ;
		}
	}


	/**
	* Method main starts application.	*/
	public static void main( String args[] )
	{
		VensimApp vensim = new VensimApp();
		vensim.addWindowListener( new CloseWindowAndExit() );
	}
}

/**
* CloseWindowAndExit class used in Calculator application.
* by James Melhuish.
 *
* Close the window and exit the application
*/
class CloseWindowAndExit extends WindowAdapter
{
	public void windowClosing( WindowEvent e )
	{
		System.exit( 0 );
	}
}
