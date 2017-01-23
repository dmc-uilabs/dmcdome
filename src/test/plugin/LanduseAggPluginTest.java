package test.plugin;

import mit.cadlab.dome3.plugin.LanduseAgg.LanduseAggPlugin;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Mar 15, 2003
 * Time: 11:31:48 PM
 * To change this template use Options | File Templates.
 */
public class LanduseAggPluginTest
{
	private static LanduseAggPlugin plg;
	private static final String inpath = "C:\\DOME\\GUIPorting\\src\\mit\\cadlab\\dome\\plugin\\LanduseAgg\\Data\\";
	private static final String outpath = "C:\\DOME\\GUIPorting\\src\\mit\\cadlab\\dome\\plugin\\LanduseAgg\\Output\\";

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
			boolean flag,boolResult;
			int type_num,i,j,k;
			String strResult;
			//System.out.println("path = " + System.getProperty("java.library.path"));
			plg = new LanduseAggPlugin("LandUseAggPlugin", inpath + "GISnames.txt");
			print("*** Creating LanduseAgg model...");
			plg.createModel();
			print("OK!\n");

			String nameFile = "GISnames.txt";
			String dataFile = "newgistest.txt";
			String typeFile = "bldgSelectInfo.txt";
			String selectMapFile = "testSelectMap.txt";
			String outputDataFile = "output.txt";
			String outputTMDataFile = "outputTM.txt";

			println("*** reading nameFile...");
			boolResult = plg.readNameFile(inpath + nameFile);
			if (boolResult == true) {
				println("finished reading name file!");
			} else {
				println("problem in reading name file.");
			}

			println("*** loading data file - please wait...");
			boolResult = plg.readDataFile(inpath + dataFile);
			if (boolResult == true) {
				println("finished loading data file!");
			} else {
				println("problem in loading data file.");
			}

			println("*** loading type file - please wait...");
			boolResult = plg.readTypeFile(inpath + typeFile);
			if (boolResult == true) {
				println("finished loading type file!");
			} else {
				println("problem in loading type file.");
			}

			println("*** loading map file - please wait...");
			flag = false;
			boolResult = plg.readSelectMapFile(inpath + selectMapFile, flag);
			if (boolResult == false) {
				println("problem in loading map file.");
			}

			println("*** testing get functions...");
			println("    *** total types: " + plg.getTotalTypes());
			println("    *** getTotalFields: " + plg.getTotalFields());
			println("    *** getHorizontalSize: " + plg.getHorizontalSize());
			println("    *** getVerticalSize: " + plg.getVerticalSize());

			print("*** set_all_bldg_types to false...");
			flag = false;
			plg.set_all_bldg_types(flag);
			print("OK!\n");

			print("*** switchBldgType: 1-20 24 25...");
			for (type_num = 1; type_num < 20; ++type_num) {
				plg.switchBldgType(type_num);
			}
			type_num = 24;
			plg.switchBldgType(type_num);
			type_num = 25;
			plg.switchBldgType(type_num);
			print("OK!\n");

			println("*** get bldg type and info...");
			for (type_num = 1; type_num < 30; ++type_num) {
				println("    bldg type " + type_num + " - " + plg.getBldgTypeSelect(type_num) + ": " + plg.getBldgInfo(type_num));
			}

			type_num = 5;
			println("*** getAdminAreaName at " + type_num + ": " + plg.getAdminAreaName(type_num));

			i = 1;
			j = 2;
			k = 3;
			println("*** getTypeMatrixData at " + i + " " + j + " " + k + ": " + plg.getTypeMatrixData(i, j, k));

			println("*** getMatrixTypeRowTot: " + plg.getMatrixTypeRowTot());
			println("*** getMatrixTypeColTot: " + plg.getMatrixTypeColTot());

			i = 50;
			j = 50;
			println("*** get_mesh_flrarea at " + i + " " + j + ": " + plg.get_mesh_flrarea(i, j));

			i = 50;
			j = 50;
			println("*** get_mesh_gndarea at " + i + " " + j + ": " + plg.get_mesh_gndarea(i, j));

			i = 50;
			j = 50;
			println("*** get_mesh_numbldg at " + i + " " + j + ": " + plg.get_mesh_numbldg(i, j));

			print("*** set_useAdminArea...");
			flag = true;
			boolResult = plg.set_useAdminArea(flag);
			print("OK!\n");

			print("*** set_all_admin_areas...");
			flag = false;
			plg.set_all_admin_areas(flag);
			print("OK!\n");

			print("*** set_all_bldg_types...");
			flag = false;
			plg.set_all_bldg_types(flag);
			print("OK!\n");

			print("*** set_admin_area_types...");
			flag = false;
			type_num = 10;
			plg.set_admin_area_types(flag, type_num);
			print("OK!\n");

			print("*** set_admin_area_names...");
			plg.set_admin_area_names();
			print("OK!\n");

			print("*** setTypeMatrixData...");
			plg.setTypeMatrixData();
			print("OK!\n");

			print("*** set_select_map_all to 1...");
			i = 1;
			plg.set_select_map_all(i);
			print("OK!\n");

			print("*** calc_selectMap...");
			flag = false;
			plg.calc_selectMap(flag);
			print("OK!\n");

			println("*** get_select_map_gndarea: " + plg.get_select_map_gndarea());
			println("*** get_select_map_flrarea: " + plg.get_select_map_flrarea());
			println("*** get_select_map_numbldg: " + plg.get_select_map_numbldg());

			println("*** output data file...");
			plg.writeData(outpath + outputDataFile);

			println("*** output TypeMatrixData file...");
			i = 10;
			plg.writeTypeMatrixData(outpath + outputTMDataFile, i);

		} catch (Exception e) {
			print("Error:" + e.toString());
			plg.unloadModel();
		}
	}

}
