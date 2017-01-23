package mit.cadlab.dome3.plugin.ug;

import mit.cadlab.dome3.plugin.Plugin;
import mit.cadlab.dome3.plugin.ug.dataobject.UgComponent;
import mit.cadlab.dome3.plugin.ug.dataobject.UgImportFile;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: May 13, 2003
 * Time: 10:53:40 PM
 * To change this template use Options | File Templates.
 */
public class UgPluginTest
{
	private static Plugin plg;

	static void print(String msg)
	{
		System.out.println(msg);
	}

	public static void main(String[] args)
	{
		try
		{
			mit.cadlab.dome3.DomeInit.initializeDOME();
			plg = new UgPlugin("UgPluginDll", "E:\\DOME\\MODELS\\UG", "JavaSideAssembly");
			System.out.println("creating model...");
			plg.createModel();
			System.out.println("creating objects...");
			Object ugVrml = ((UgPlugin)plg).createVrmlFile("ug", null);
			UgComponent ugPart = ((UgPlugin)plg).createComponent(null, "ROD","kg m");
			Object ugImport = ((UgPlugin)plg).createUgImportFile("s_Block", null);

//			UgDimensionIn ugDimensionIn = (UgDimensionIn)ugPart.createDimensionIn("Length");
//			UgDimensionOut ugDimensionOut = (UgDimensionOut)ugPart.createDimensionOut("Height");
//			UgMassProperty ugBlockVolume = (UgMassProperty)ugPart.createMassProperty("volume");
//			UgExportFile ugExportStep = (UgExportFile)ugPart.createExportFile("step");
			System.out.println("loading model...");
			plg.loadModel();
			System.out.println("executing...");
			((UgImportFile)ugImport).setIsChanged(true);
			((UgImportFile)ugImport).setIsResult(false);
			plg.execute();
			System.out.println("getting values...");
//			System.out.println("Length of Block is: " + ugDimensionIn.getDimValue());
//			System.out.println("Height of Block is: " + ugDimensionOut.getDimValue());
//			System.out.println("Volume of Block is: " + ugBlockVolume.getMassPropertyValue());
			System.out.println("unloading model...");
			plg.unloadModel();
			System.out.println("exiting...");
			System.exit(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
