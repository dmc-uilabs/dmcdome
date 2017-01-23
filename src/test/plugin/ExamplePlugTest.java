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
import mit.cadlab.dome3.plugin.example.ExamplePlugin;
import mit.cadlab.dome3.plugin.example.dataobject.ExampleBoolean;
import mit.cadlab.dome3.plugin.example.dataobject.ExampleReal;

public class ExamplePlugTest
{
	private static Plugin plg;

	static void print(String msg)
	{
		System.out.println(msg);
	}

	public static void main(String[] args)
	{
		try {
			DomeInit.initializeDOME();
			plg = new ExamplePlugin("ExamplePlugin");
			print("Creating ExampleModel");
			plg.createModel();

			Object real1 = ((ExamplePlugin) plg).createReal();
			Object real2 = ((ExamplePlugin) plg).createReal(2.22459);
			Object real3 = ((ExamplePlugin) plg).createReal();

			Object bol1 = ((ExamplePlugin) plg).createBoolean();
			Object bol2 = ((ExamplePlugin) plg).createBoolean(true);
			Object bol3 = ((ExamplePlugin) plg).createBoolean();

			if (!plg.isModelLoaded()) {
				print("Model is not already loaded, Loading model.");
				plg.loadModel();
			}
			((ExampleReal) real1).setValue(6.66);
			((ExampleReal) real3).setIsResult(true);

			((ExampleBoolean) bol1).setValue(true);
			((ExampleBoolean) bol3).setIsResult(true);

			print("Executing...");
			try {
				plg.execute();
			} catch (IllegalStateException ile) {
				ile.printStackTrace();
			}
			print("In java, real1 = " + ((ExampleReal) real1).getValue());
			print("In java, real2 = " + ((ExampleReal) real2).getValue());
			print("In java, result = real3 = real1 + real2 = " + ((ExampleReal) real3).getValue());

			print("In java, bol1 = " + ((ExampleBoolean) bol1).getValue());
			print("In java, bol2 = " + ((ExampleBoolean) bol2).getValue());
			print("In java, result = bol3 = bol1 OR bol2 = " + ((ExampleBoolean) bol3).getValue());

			print("In java, returnString = " + ((ExamplePlugin) plg).returnString());

			print("In java, returnDoubleVector ");
			double[] d1arr = ((ExamplePlugin) plg).returnDoubleVector(new double[]{0.3, 0.67, 3.78});
			for (int k = 0; k < d1arr.length; k++) {
				print("darr[" + k + "] = " + d1arr[k]);
			}

			print("In java, returnIntVector ");
			int[] example2 = new int[]{3, 678, 20, 45, 23456};
			int[] i1arr = ((ExamplePlugin) plg).returnIntVector(example2);
			for (int k = 0; k < i1arr.length; k++) {
				print("iarr[" + k + "] = " + i1arr[k]);
			}

			print("In java, return2dimDoubleVector ");
			double[][] d2arr = ((ExamplePlugin) plg).return2DimDoubleVector();
			for (int l = 0; l < d2arr.length; l++) {
				double[] darr = d2arr[l];
				for (int k = 0; k < darr.length; k++) {
					print("d2arr[" + l + "][" + k + "] = " + darr[k]);
				}
			}

			print("In java, return2dimIntVector ");
			int[][] i2arr = ((ExamplePlugin) plg).return2DimIntVector();
			for (int l = 0; l < i2arr.length; l++) {
				int[] iarr = i2arr[l];
				for (int k = 0; k < iarr.length; k++) {
					print("i2arr[" + l + "][" + k + "] = " + iarr[k]);
				}
			}

			print("Unloading Model.");
			plg.unloadModel();

			((ExampleReal) real1).setValue(4.4);
			plg.execute();

			print("In java, real1 = " + ((ExampleReal) real1).getValue());
			print("In java, real2 = " + ((ExampleReal) real2).getValue());
			print("In java, result = real3 value = " + ((ExampleReal) real3).getValue());


			print("Unloading Model.");
			plg.unloadModel();


			print("Finished.");
		} catch (Exception e) {
			print("***" + e.toString());
			//  plg.unloadModel();
		}
	}
}
