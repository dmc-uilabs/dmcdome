/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Aug 29, 2002
 * Time: 4:32:55 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package test.plugin;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.plugin.Plugin;
import mit.cadlab.dome3.plugin.excel.ExcelPlugin;
import mit.cadlab.dome3.plugin.excel.dataobject.ExcelMatrix;
import mit.cadlab.dome3.plugin.excel.dataobject.ExcelReal;

public class ExcelPlugTest
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
			//plg = new ExcelPlugin("ExcelPlugin",
			//                      "C:\\DOME\\dome\\nativeplugincode\\NewPlugins\\ExcelPlugin_vs7\\testbook.xls", true);
			plg = new ExcelPlugin("ExcelPlugin",
                    "C:\\aDigitalMfg\\Test\\testbook.xlsx", true);
			
			print("Creating ExcelModel");
			plg.createModel();

			Object real1 = ((ExcelPlugin) plg).createReal("Sheet1", "A1");
			print("real1 = " + real1);
			Object real2 = ((ExcelPlugin) plg).createReal("Sheet1", "B1");
			Object real3 = ((ExcelPlugin) plg).createReal("Sheet1", "C1");
			Object matrix = ((ExcelPlugin) plg).createMatrix("Sheet1", "E3:G6");
			print("matrix = " + matrix.toString());
			Object matrixSum = ((ExcelPlugin) plg).createReal("Sheet1", "F9");

			if (!plg.isModelLoaded()) {
				print("Model is not already loaded, Loading model.");
				plg.loadModel();
			}
			//set values in java object
			((ExcelMatrix) matrix).setElement(0, 0, 5.67);
			((ExcelMatrix) matrix).setElement(0, 1, 5.67);
			((ExcelMatrix) matrix).setElement(0, 2, 5.67);
			((ExcelMatrix) matrix).setElement(1, 0, 5.67);
			((ExcelMatrix) matrix).setElement(1, 1, 5.67);
			((ExcelMatrix) matrix).setElement(1, 2, 5.67);
			((ExcelMatrix) matrix).setElement(2, 0, 5.67);
			((ExcelMatrix) matrix).setElement(2, 1, 5.67);
			((ExcelMatrix) matrix).setElement(2, 2, 5.67);
			((ExcelMatrix) matrix).setElement(3, 0, 5.67);
			((ExcelMatrix) matrix).setElement(3, 1, 5.67);
			((ExcelMatrix) matrix).setElement(3, 2, 5.67);

			((ExcelReal) real1).setValue(6.66);
			((ExcelReal) real2).setValue(2.22);
			((ExcelReal) real3).setIsResult(true);
			((ExcelReal) matrixSum).setIsResult(true);

			print("Executing...");
			plg.execute();

			print("In java, real1 = " + ((ExcelReal) real1).getValue());
			print("In java, real2 = " + ((ExcelReal) real2).getValue());

			print("Matrix rows =" + ((ExcelMatrix) matrix).getRows());
			print("Matrix columns = " + ((ExcelMatrix) matrix).getColumns());
			//get values in actual spreadsheet
			double[][] arr = ((ExcelMatrix) matrix).getValues(true);

			for (int i = 0; i < arr.length; i++) {
				for (int j = 0; j < arr[i].length; j++) {
					print("In java, matrix element[" + i + "]" + "[" + j + "]= " + arr[i][j]);
				}
			}
			print("In java, result = real3 value = " + ((ExcelReal) real3).getValue());
			print("In java, result = New Sum = " + ((ExcelReal) matrixSum).getValue());

			print("Unloading Model.");
			plg.unloadModel();

			((ExcelReal) real1).setValue(4.4);
			plg.execute();

			print("In java, result = real3 value = " + ((ExcelReal) real3).getValue());
			print("In java, result = New Sum = " + ((ExcelReal) matrixSum).getValue());

			print("Unloading Model.");
			plg.unloadModel();

			print("Finished.");
		} catch (Exception e) {
			print(e.toString());
			//  plg.unloadModel();
		}
	}
}
