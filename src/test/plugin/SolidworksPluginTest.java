/*
 * Created by IntelliJ IDEA.
 * User: wronski
 * Date: Nov 29, 2002
 * Time: 6:07:42 PM
 * To change this template use Options | File Templates.
 */
package test.plugin;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.plugin.Plugin;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksPlugin;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksAngleUnit;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksColors;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksDimension;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksLengthUnit;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksMass;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksSurfaceArea;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.SolidworksVolume;

public class SolidworksPluginTest
{
	private static Plugin plg;

	static void print(String msg)
	{
		System.out.println(msg);
	}

	public static void main(String[] args)
	{
		DomeInit.initializeDOME();
		try {
			System.out.println("path = " + System.getProperty("java.library.path"));
			plg = new SolidworksPlugin("SolidWorksPlugIn", "C:\\Documents and Settings\\weimao\\Personal\\bolt.SLDPRT", true);
			System.out.println("Creating model...");
			plg.createModel();
			System.out.println("Loading model...");
			plg.loadModel();
			if (plg.isModelLoaded()) System.out.println("Model is loaded");
			System.out.println("Unloading model");
			plg.unloadModel();
			if (!plg.isModelLoaded()) System.out.println("Model is unloaded");
			System.out.println("Creating Solidworks objects");
			Object dim1 = ((SolidworksPlugin) plg).createDimension("Diameter@Sketch1");
			Object col1 = ((SolidworksPlugin) plg).createColors();
			Object len1 = ((SolidworksPlugin) plg).createLengthUnit();
			Object ang1 = ((SolidworksPlugin) plg).createAngleUnit();
			Object vol1 = ((SolidworksPlugin) plg).createVolume();
			Object mas1 = ((SolidworksPlugin) plg).createMass();
			Object sur1 = ((SolidworksPlugin) plg).createSurfaceArea();
			Object fil1 = ((SolidworksPlugin) plg).createFile("C:\\Documents and Settings\\weimao\\Personal\\bolt.wrl");
			Object fil2 = ((SolidworksPlugin) plg).createFile("C:\\Documents and Settings\\weimao\\Personal\\bolt.step");
			Object fil3 = ((SolidworksPlugin) plg).createFile("C:\\Documents and Settings\\weimao\\Personal\\bolt.igs");
			System.out.println("Loading model...");
			plg.loadModel();
			if (plg.isModelLoaded()) System.out.println("Model is loaded");
			((SolidworksDimension) dim1).setIsResult(false);
			((SolidworksDimension) dim1).setValue(100.0);
			//((SolidworksFile)fil).save(true);
			System.out.println("Executing...");
			plg.execute();
			System.out.println("The dimension is: " + (((SolidworksDimension) dim1).getValue()));
			System.out.println("The red call:getRed() is: " + ((SolidworksColors) col1).getRed());
			System.out.println("The green call:getGreen() is: " + ((SolidworksColors) col1).getGreen());
			System.out.println("The blue call:getBlue() is: " + ((SolidworksColors) col1).getBlue());
			System.out.println("The red color is: " + (((SolidworksColors) col1).getValues()).getItem(0));
			System.out.println("The green color is: " + (((SolidworksColors) col1).getValues()).getItem(1));
			System.out.println("The blue color is: " + (((SolidworksColors) col1).getValues()).getItem(2));
			System.out.println("The length unit is: " + (((SolidworksLengthUnit) len1).getValue()));
			System.out.println("The angle unit is: " + (((SolidworksAngleUnit) ang1).getValue()));
			System.out.println("The volume of the part is: " + ((SolidworksVolume) vol1).getValue());
			System.out.println("The mass of the part is: " + ((SolidworksMass) mas1).getValue());
			System.out.println("The surface area of the part is: " + ((SolidworksSurfaceArea) sur1).getValue());
			plg.unloadModel();
			if (!plg.isModelLoaded()) System.out.println("Model is unloaded");
		} catch (Exception e) {
			System.out.println("ERROR - SolidworksPluginTest.java");
		}
	}
}
