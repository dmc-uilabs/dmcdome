//IOAnalysisPluginTest.java
//author: Sittha Sukkasi
//last update: 1/29/2003

package test.plugin;


import mit.cadlab.dome3.plugin.ioanalysis.IOAnalysisPlugin;
import mit.cadlab.dome3.plugin.Plugin;

import java.util.Vector;

public class IOAnalysisPlugTest
{
	private static IOAnalysisPlugin plg;
	private static final String inpath = "c:/dome/GUIPorting/IOAnalysisPluginData/";

	static void print(String msg)
	{
		System.out.print(msg);
	}

	static void println(String msg)
	{
		System.out.println(msg);
	}

	public static void main(String[] args)
	{
		try {
			//System.out.println("path = " + System.getProperty("java.library.path"));

			plg = new IOAnalysisPlugin("IOAnalysis", "");
			println("Creating IOAnalysis model");
			plg.createModel();

			String filename1 = "filename";
			String finalfilename1 = "ShanxiIOfinalDemands.txt";
			String leonfilename1 = "ShanxiLeontief.txt";
			plg.readInfoFile(inpath + filename1);
			plg.readFinalDemandsFile(inpath + finalfilename1);
			plg.readLeontiefFile(inpath + leonfilename1);

			int sectorNo1 = 11, sectorNo2 = 21;

			println("**** TEST 1 *************************************************");
			print("total sectors: " + plg.getTotalSectors());
			print("; total agg sectors: " + plg.getTotalAggSectors());
			println("; total data types: " + plg.getTotalDataTypes());
			print("; total CO2 types: " + plg.getTotalCO2Types());
			println("; total poll types: " + plg.getTotalPollTypes());
			println("************************************************************");
			println("");
			println("TESTING DATA GET/SET FUNCTIONS: test2 sector: " + sectorNo1 + "; " + sectorNo2);
			println("");
			println("");
			println("getSectorName: " + plg.getSectorName(sectorNo1) + "; " + plg.getSectorName(sectorNo2));
			println("getFullSectorName: " + plg.getFullSectorName(sectorNo1) + "; " + plg.getFullSectorName(sectorNo2));
			println("getAggSectorName: " + plg.getAggSectorName(sectorNo1) + "; " + plg.getAggSectorName(sectorNo2));

			println("getTOData: " + plg.getTOData(sectorNo1, 0) + "; " + plg.getTOData(sectorNo2, 0));
			println("getAggTOData: " + plg.getAggTOData(sectorNo1, 0) + "; " + plg.getAggTOData(sectorNo2, 0));
			println("getAggFDData: " + plg.getAggFDData(sectorNo1, 0) + "; " + plg.getAggFDData(sectorNo2, 0));
			println("getAggCO2Data: " + plg.getAggCO2Data(sectorNo1, 0) + "; " + plg.getAggCO2Data(sectorNo2, 0));
			println("getDisAggCO2Data: " + plg.getDisAggCO2Data(sectorNo1, 0, 0) + "; " + plg.getDisAggCO2Data(sectorNo2, 0, 0));
			println("");
			println("************************************************************");

			plg.setFDData(111.11, sectorNo1, 0);
			println("TESTING DATA GET/SET FUNCTIONS: test2 sector: " + sectorNo1 + "; " + sectorNo2);
			println("");
			println("");
			println("getSectorName: " + plg.getSectorName(sectorNo1) + "; " + plg.getSectorName(sectorNo2));
			println("getFullSectorName: " + plg.getFullSectorName(sectorNo1) + "; " + plg.getFullSectorName(sectorNo2));
			println("getAggSectorName: " + plg.getAggSectorName(sectorNo1) + "; " + plg.getAggSectorName(sectorNo2));

			println("getTOData: " + plg.getTOData(sectorNo1, 0) + "; " + plg.getTOData(sectorNo2, 0));
			println("getAggTOData: " + plg.getAggTOData(sectorNo1, 0) + "; " + plg.getAggTOData(sectorNo2, 0));
			println("getAggFDData: " + plg.getAggFDData(sectorNo1, 0) + "; " + plg.getAggFDData(sectorNo2, 0));
			println("getAggCO2Data: " + plg.getAggCO2Data(sectorNo1, 0) + "; " + plg.getAggCO2Data(sectorNo2, 0));
			println("getDisAggCO2Data: " + plg.getDisAggCO2Data(sectorNo1, 0, 0) + "; " + plg.getDisAggCO2Data(sectorNo2, 0, 0));
			println("");
			println("************************************************************");
			plg.clearFDData();
			println("");
			println("");
			println("getSectorName: " + plg.getSectorName(sectorNo1) + "; " + plg.getSectorName(sectorNo2));
			println("getFullSectorName: " + plg.getFullSectorName(sectorNo1) + "; " + plg.getFullSectorName(sectorNo2));
			println("getAggSectorName: " + plg.getAggSectorName(sectorNo1) + "; " + plg.getAggSectorName(sectorNo2));

			println("getTOData: " + plg.getTOData(sectorNo1, 0) + "; " + plg.getTOData(sectorNo2, 0));
			println("getAggTOData: " + plg.getAggTOData(sectorNo1, 0) + "; " + plg.getAggTOData(sectorNo2, 0));
			println("getAggFDData: " + plg.getAggFDData(sectorNo1, 0) + "; " + plg.getAggFDData(sectorNo2, 0));
			println("getAggCO2Data: " + plg.getAggCO2Data(sectorNo1, 0) + "; " + plg.getAggCO2Data(sectorNo2, 0));
			println("getDisAggCO2Data: " + plg.getDisAggCO2Data(sectorNo1, 0, 0) + "; " + plg.getDisAggCO2Data(sectorNo2, 0, 0));
			println("");
			println("getPollutantCoeff: " + plg.getPollutantCoeff(sectorNo1, 2));
			plg.setPollutantCoeff(-99.23, sectorNo1, 2);
			println("getPollutantCoeff: " + plg.getPollutantCoeff(sectorNo1, 2));
			println("getPollutantData: " + plg.getPollutantData(sectorNo1, 2));


			println("**** END OF TEST 1 ******************************************");

			plg.addPollutantSector(sectorNo1);
			plg.addPollutantSector(sectorNo2);
			plg.addCO2Sector(sectorNo2);
			plg.addFDSector(sectorNo2);
			plg.addTOSector(sectorNo2);

			println("end!!");


		} catch (Exception e) {
			print("***" + e.toString());
			//  plg.unloadModel();
		}
	}
}
