package test.plugin;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Jan 20, 2003
 * Time: 10:20:13 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.plugin.Plugin;
import mit.cadlab.dome3.plugin.ideas.IdeasPlugin;

public class IdeasPluginTest
{
	private static Plugin plg;

	static void print(String msg)
	{
		System.out.println(msg);
	}

	public static void main(String[] args)
	{
		try {
/*			plg = new IdeasPlugin("IdeasPluginDll","ASSEMBLY","E:\\DOME\\MODELS\\IDEAS","SYSTEM","cadlab19");
			System.out.println("Creating model...");
			plg.createModel();
			System.out.println("Creating assembly object...");
			Object _idAss = ((IdeasPlugin)plg).createAssembly("Assembly", "Assembly1");
			System.out.println("Creating assembly object mass properties");
			Object _idmass = ((IdeasAssembly)_idAss).createMassProperty("mass");
			Object _idvolass = ((IdeasAssembly)_idAss).createMassProperty("volume");
			Object _idsurass = ((IdeasAssembly)_idAss).createMassProperty("solidSurfaceArea");
			System.out.println("Creating assembly export file..");
			Object _idExp = ((IdeasAssembly)_idAss).createExportFile("iges");
			System.out.println("Creating part object...");
			Object _idPart1 = ((IdeasPlugin)plg).createPart("Parts", "BLOCK");
			System.out.println("Creating part object mass properties");
			Object _idmasspt1 = ((IdeasPart)_idPart1).createMassProperty("mass");
			Object _idvolpt1 = ((IdeasPart)_idPart1).createMassProperty("volume");
			Object _idsurpt1 = ((IdeasPart)_idPart1).createMassProperty("solidSurfaceArea");
			System.out.println("Creating a dimension object...");
			Object _iddimin = ((IdeasPart)_idPart1).createDimensionIn("Length");
			Object _iddimout = ((IdeasPart)_idPart1).createDimensionOut("Height");
			System.out.println("Creating part object...");
			Object _idPart2 = ((IdeasPlugin)plg).createPart("Parts", "Rod");
			System.out.println("Creating part object mass properties");
			Object _idmasspt2 = ((IdeasPart)_idPart2).createMassProperty("mass");
			Object _idvolpt2 = ((IdeasPart)_idPart2).createMassProperty("volume");
			Object _idsurpt2 = ((IdeasPart)_idPart2).createMassProperty("solidSurfaceArea");
			System.out.println("Creating a dimension object...");
			Object _iddimin2 = ((IdeasPart)_idPart2).createDimensionIn("Diameter");
			Object _iddimout2 = ((IdeasPart)_idPart2).createDimensionOut("RHeight");
			System.out.println("Creating VRML file");
			Object _idVrml = ((IdeasPlugin)plg).createVrmlFile("vrml");
			System.out.println("Loading model...");
			plg.loadModel();
			if(plg.isModelLoaded()) System.out.println("Model is loaded");
			plg.execute();
			System.out.println("Reading dimensions...");
			System.out.println("Block dimension: " + (((IdeasDimensionIn)_iddimin).getDimName()) + ", Units: " + (((IdeasDimensionIn)_iddimin).getDimUnit()) + ", Value: " + (((IdeasDimensionIn)_iddimin).getDimValue()));
			System.out.println("Block dimension: " + (((IdeasDimensionOut)_iddimout).getDimName()) + ", Units: " + (((IdeasDimensionOut)_iddimout).getDimUnit()) + ", Value: " + (((IdeasDimensionOut)_iddimout).getDimValue()));
			System.out.println("Rod dimension: " + (((IdeasDimensionIn)_iddimin2).getDimName()) + ", Units: " + (((IdeasDimensionIn)_iddimin2).getDimUnit()) + ", Value: " + (((IdeasDimensionIn)_iddimin2).getDimValue()));
		    System.out.println("Rod dimension: " + (((IdeasDimensionOut)_iddimout2).getDimName()) + ", Units: " + (((IdeasDimensionOut)_iddimout2).getDimUnit()) + ", Value: " + (((IdeasDimensionOut)_iddimout2).getDimValue()));
			System.out.println("Setting dimensions...");
			((IdeasDimensionIn)_iddimin).setIsResult(false); ((IdeasDimensionIn)_iddimin).setDimValue(100.0);
			((IdeasDimensionIn)_iddimin2).setIsResult(false); ((IdeasDimensionIn)_iddimin2).setDimValue(30.0);
			System.out.println("Executing...");
			plg.execute();
			System.out.println("Reading dimensions...");
			System.out.println("Block dimension: " + (((IdeasDimensionIn)_iddimin).getDimName()) + ", Units: " + (((IdeasDimensionIn)_iddimin).getDimUnit()) + ", Value: " + (((IdeasDimensionIn)_iddimin).getDimValue()));
			System.out.println("Block dimension: " + (((IdeasDimensionOut)_iddimout).getDimName()) + ", Units: " + (((IdeasDimensionOut)_iddimout).getDimUnit()) + ", Value: " + (((IdeasDimensionOut)_iddimout).getDimValue()));
			System.out.println("Rod dimension: " + (((IdeasDimensionIn)_iddimin2).getDimName()) + ", Units: " + (((IdeasDimensionIn)_iddimin2).getDimUnit()) + ", Value: " + (((IdeasDimensionIn)_iddimin2).getDimValue()));
			System.out.println("Rod dimension: " + (((IdeasDimensionOut)_iddimout2).getDimName()) + ", Units: " + (((IdeasDimensionOut)_iddimout2).getDimUnit()) + ", Value: " + (((IdeasDimensionOut)_iddimout2).getDimValue()));
			System.out.println("Reading model properties...");
			System.out.println("Block properties...");
			System.out.println("Part property: " + (((IdeasMassProperty)_idmasspt1).getMPName()) + ", Unit: " + (((IdeasMassProperty)_idmasspt1).getMPUnit()) + ", Value: " + (((IdeasMassProperty)_idmasspt1).getMPValue()));
			System.out.println("Part property: " + (((IdeasMassProperty)_idvolpt1).getMPName()) + ", Unit: " + (((IdeasMassProperty)_idvolpt1).getMPUnit()) + ", Value: " + (((IdeasMassProperty)_idvolpt1).getMPValue()));
			System.out.println("Part property: " + (((IdeasMassProperty)_idsurpt1).getMPName()) + ", Unit: " + (((IdeasMassProperty)_idsurpt1).getMPUnit()) + ", Value: " + (((IdeasMassProperty)_idsurpt1).getMPValue()));
			System.out.println("Rod properties...");
			System.out.println("Part property: " + (((IdeasMassProperty)_idmasspt2).getMPName()) + ", Unit: " + (((IdeasMassProperty)_idmasspt2).getMPUnit()) + ", Value: " + (((IdeasMassProperty)_idmasspt2).getMPValue()));
			System.out.println("Part property: " + (((IdeasMassProperty)_idvolpt2).getMPName()) + ", Unit: " + (((IdeasMassProperty)_idvolpt2).getMPUnit()) + ", Value: " + (((IdeasMassProperty)_idvolpt2).getMPValue()));
			System.out.println("Part property: " + (((IdeasMassProperty)_idsurpt2).getMPName()) + ", Unit: " + (((IdeasMassProperty)_idsurpt2).getMPUnit()) + ", Value: " + (((IdeasMassProperty)_idsurpt2).getMPValue()));
			System.out.println("Assembly properties...");
			System.out.println("Name of assembly: " + (((IdeasAssembly)_idAss).getCompName()) + ", in bin: " + (((IdeasAssembly)_idAss).getBinName()));
			System.out.println("Assembly properties...");
            System.out.println("Assembly property: " + (((IdeasMassProperty)_idmass).getMPName()) + ", Units: " + (((IdeasMassProperty)_idmass).getMPUnit()) + ", Value: " + (((IdeasMassProperty)_idmass).getMPValue()));
			System.out.println("Assembly property: " + (((IdeasMassProperty)_idvolass).getMPName()) + ", Unit: " + (((IdeasMassProperty)_idvolass).getMPUnit()) + ", Value: " + (((IdeasMassProperty)_idvolass).getMPValue()));
			System.out.println("Assembly property: " + (((IdeasMassProperty)_idsurass).getMPName()) + ", Unit: " + (((IdeasMassProperty)_idsurass).getMPUnit()) + ", Value: " + (((IdeasMassProperty)_idsurass).getMPValue()));
			System.out.println("Exporting VRML...");
			((IdeasPlugin)plg).export();
			System.out.println("Unloading model");
			plg.unloadModel();
			if(!plg.isModelLoaded()) System.out.println("Model is unloaded");
*/
			DomeInit.initializeDOME();
			plg = new IdeasPlugin("IdeasPluginDll", "Suspension", "E:\\dome3\\SuspensionModel", "FORD", "cadlab19");
			System.out.println("Creating model...");
			plg.createModel();
			System.out.println("Creating objects...");
			Object _idassembly = ((IdeasPlugin) plg).createAssembly(null, "DemoAssemblyParts", "TruckSuspensionAssembly");
//			Object _idpart1 = ((IdeasPlugin)plg).createPart("DemoAssemblyParts","FrontLeafSpring");
//			Object _idpart2 = ((IdeasPlugin)plg).createPart("Main","AssemblyTop");
			Object _idVrml = ((IdeasPlugin)plg).createVrmlFile(null, "vrml");
//			Object _idimpt = ((IdeasPlugin)plg).createIdeasImportPart("i_BLOCK");
//			Object _idDim = ((IdeasPart) _idpt).createDimensionIn("Length");
//			Object _idVol = ((IdeasPart) _idpt).createMassProperty("volume");
//			Object _idMass = ((IdeasPart) _idpt).createMassProperty("mass");
			plg.loadModel();
//			((IdeasDimensionIn) _idDim).setIsResult(false);
//			((IdeasDimensionIn) _idDim).setDimValue(10);
//            ((IdeasImportPart)_idimpt).setIsChanged(true); ((IdeasImportPart)_idimpt).setIsResult(false);
			plg.execute();

//			((IdeasPlugin)plg).importNeutralFile();

			//		plg.unloadModel();

//			System.out.println("The volume is " + ((IdeasMassProperty) _idVol).getMPValue());
//			System.out.println("The mass is: " + ((IdeasMassProperty) _idMass).getMPValue());
//			System.out.println("Name: " + ((IdeasImportPart)_idimpt).getName() + " , FileName: " + ((IdeasImportPart)_idimpt).getFileName());

			((IdeasPlugin)plg).export();

			plg.unloadModel();

		} catch (Exception e) {
			System.out.println("ERROR");
		}
	}
}
