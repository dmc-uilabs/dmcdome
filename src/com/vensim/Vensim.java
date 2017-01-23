/**
* Vensim class for defining the methods in the venjava.dll
* which in turn calls vensim.dll
*/
package com.vensim;
/* NOTE if you change the package name you will need to rebuild
   venjava.dll using the new javah generated function declarations */

//import java.awt.*;

public class Vensim
{
/* variable types  for get_varnames */
public static final int VARTYPE_WORKBENCH =-1;
public static final int VARTYPE_ALL =0;
public static final int VARTYPE_LEVEL =1;
public static final int VARTYPE_AUXILIARY =2;
public static final int VARTYPE_DATA =3;
public static final int VARTYPE_INIITIAL =4; 
public static final int VARTYPE_CONSTANT =5;
public static final int VARTYPE_LOOKUP =6;
public static final int VARTYPE_GROUP =7;
public static final int VARTYPE_SUBSCRIPT =8;
public static final int VARTYPE_CONSTRAINT =9;
public static final int VARTYPE_TEST_INPUT =10;
public static final int VARTYPE_TIME_BASE =11;
public static final int VARTYPE_GAME =12;
public static final int VVARTYPE_SUBSCRIPT_CONSTANT =13;


/* attribute types for get_varattrib */
public static final int ATTRIB_UNITS =1;
public static final int ATTRIB_COMMENT =2;
public static final int ATTRIB_EQUATIONS =3;
public static final int ATTRIB_CAUSES =4;
public static final int ATTRIB_USES =5;
public static final int ATTRIB_INITCAUSES =6; /* outputs only initial causes */
public static final int ATTRIB_ACTIVECAUSES =7; /* outputs only active causes - not initial */
public static final int ATTRIB_SUBFAMILY =8; /* list the subscript ranges associated with the variable */
public static final int ATTRIB_SUBALL =9; /* lists the expanded subscript list for the variable */
public static final int ATTRIB_SUBWORK =10; /* lists the expanded set of subscipts that would be used on tool invocation */
public static final int ATTRIB_MIN =11;
public static final int ATTRIB_MAX =12;
public static final int ATTRIB_INCREMENT =13;
public static final int ATTRIB_VARTYPE =14;
public static final int ATTRIB_GROUP =15;

/* return values for check_status */
public static final int STATUS_IDLE =0;
public static final int STATUS_SIMULATING =1;
public static final int STATUS_SIMHANG =2;
public static final int STATUS_BLOCKACTION =3;
public static final int STATUS_MEMLOCK =4;
public static final int STATUS_INGAME =5;
public static final int STATUS_NEEDFREE =6;

/* information queries for get_info */
public static final int INFO_DLL =1;/* returns Minimal, Silent, Full or Redist */
public static final int INFO_VERSION =2; /* version info for Vensim */
public static final int INFO_USER =3;/* user name \0 user company \0 */
public static final int INFO_DIRECTORY =4 ;/* the currently active directory */
public static final int INFO_MODELNAME =5 ;/* the name of the currently loaded model (no directory) */
public static final int INFO_TOOLSET =6 ;/* the name of the toolset */
public static final int INFO_TOOLLIST =7 ;/* the names of the tools in the loaded toolset */
public static final int INFO_GRAPHSET =8 ;/* name of the loaded graph set */
public static final int INFO_GRAPHLIST =9 ;/* list of available graphs */
public static final int INFO_RUNLIST =10 ;/* the list of loaded runs */
public static final int INFO_RUNNAME =11 ;/* the name of the run to be maded changed with SIMULATE>RUNNAME */
public static final int INFO_CINFILES =12 ;/* cin files set with SIMULATE>CHGFILE */
public static final int INFO_DATAFILES =13 ;/* data file set with SIMULATE>DATA */
public static final int INFO_BASED =14 ;/* name of run to base on set with SIMULATE>BASED */
public static final int INFO_OPTMARM =15 ;/* optimization parameter files SIMULATE>OPTPARM */
public static final int INFO_PAYOFF =16 ;/* name of payoff control files SIMULATE>PAYOFF */
public static final int INFO_RESUME =17 ;/* resume satus (0 or 1) SIMULATE>RESUME */
public static final int INFO_SAVELIST =18 ;/* name of savelist file SIMULATE>SAVELIST */
public static final int INFO_SENSSAVELIST =19 ;/* name of sensitivity save list files SIMULATE>SENSSAVELIST */
public static final int INFO_SENSITIVITY =20 ;/* name of sensitity contol file SIMULATE>SENSITIVITY */
public static final int INFO_BENCHVAR =21 ;/* workbench var name as it would appear in title bar */
public static final int INFO_VIEWLIST =22 ; /* list of views in the model */
public static final int INFO_TIMEAXIS =23 ; /* min\0max\0special */


/* vensim be quiet flags*/
public static final int QUIET_NORMAL=0;
public static final int QUIET_NO_WIP=1;
public static final int QUIET_NO_WIP_NO_DIALOGS=2;

	/**
	* Native method declarations
	*/
	static native int get_jversion() ;
	public static native int command( String command );
	public static native int CCommand(int context,String command );
	public static native int get_data( String fileName, String varName, String timeAxisName, float vectorOfValues[], float timeVectorValues[], int maxNumberValues );
	public static native int CGetData(int context, String fileName, String varName, String timeAxisName, float vectorOfValues[], float timeVectorValues[], int maxNumberValues );
	public static native int tool_command( String command, int windowHandle, int aswiptool );//generally references to windows just won't work
	public static native int CToolCommand(int context, String command, int windowHandle, int aswiptool );//generally references to windows just won't work
	public static native int start_simulation(int loadFirst, int game, int overwrite );
	public static native int CStartSimulation(int context, int loadFirst, int game, int overwrite );
	public static native int continue_simulation( int numIntervals );
	public static native int CContinueSimulation(int context, int numIntervals );
	public static native int finish_simulation();
	public static native int CFinishSimulation(int context);
	public static native int get_val( String varName, float val[] );
	public static native int CGetVal(int context, String varName, float val[] );
	public static native int get_dpval( String varName, double val[] );
	public static native int CGetDPVval(int context, String varName, double val[] );
	public static native int show_sketch( int sketchNum, int wantScroll, int zoomPercent, int windowHandle );//generally references to windows just won't work
	public static native int CShowSketch(int context, int sketchNum, int wantScroll, int zoomPercent, int windowHandle );//generally references to windows just won't work
	public static native int be_quiet( int quietflag );
	public static native int CBeQuiet(int context, int quietflag );
	public static native int check_status();
	public static native int CCheckStatus(int context);
	public static native int ContextAdd(int wantcleanup) ;
	public static native int ContextDrop(int context) ;

	/* these methods return string arrays - if there is no information found
	   the lenght of these arrays is 0 */
	public static native String[] get_varnames(String filter, int vartype) ;
	public static native String[] CGetVarNames(int context,String filter, int vartype) ; 
	public static native String[] get_varattrib(String varname,int attrib) ;
	public static native String[] CGetVarAttrib(int context,String varname,int attrib) ; 
	public static native String[] get_info(int infowanted) ;
	public static native String[] CGetInfo(int context,int infowanted) ;
	/* note the get_info almost always returns an array of length 1 */

	/* this method is of no use and has been commented out but is 
	   still supported by venjava.dll
	   public static native int get_substring(String fullstring,int offset,String buf,int maxbuflen) ;
	   */ 

    public static native int get_varoff(String varname) ;
    public static native int CGetVarOff(int context,String varname) ;
	public static native int get_vecvals(int vecoff[],float val[],int nvals)  ;
	public static native int CGetVecVals(int context,int vecoff[],float val[],int nvals)  ;
	public static native int get_dpvecvals(int offsets[],double dpvals[],int veclen) ;
	public static native int CGetDPVecVals(int context,int offsets[],double dpvals[],int veclen) ; 
	public static native int get_sens_at_time(String filename,String varname,String tname,float intime[],float vals[],int maxn) ;
	public static native int CGetSensAtTime(int context,String filename,String varname,String tname,float intime[],float vals[],int maxn) ;
	public static native int set_parent_window(int window,int r1,int r2) ;//generally references to windows just won't work
	public static native int CSetParentWindow(int context,int window,int r1,int r2) ;//generally references to windows just won't work


	/**
	* Static, load the dynamic library
	*/
	public Vensim(String libname)
	{
        try
        {

//        System.out.println("Library path is " + System.getProperty("java.library.path","")) ;
            System.loadLibrary(libname);
			int rval = Vensim.get_jversion() ;
			if(rval != 5050) {
			   System.out.println("Bad version " + rval + " java class does not match DLL") ;
			   }
	    }
        catch( UnsatisfiedLinkError e )
        {
            System.out.println( "Cannot load native library. Error: " + e.toString() );
        }
	}


}
