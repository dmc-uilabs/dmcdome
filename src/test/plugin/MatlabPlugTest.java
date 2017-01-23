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
import mit.cadlab.dome3.plugin.matlab.MatlabPlugin;
import mit.cadlab.dome3.plugin.matlab.dataobject.MatlabMatrix;
import mit.cadlab.dome3.plugin.matlab.dataobject.MatlabReal;

public class MatlabPlugTest
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
			plg = new MatlabPlugin("MatlabPlugin",
			                       "C:\\Users\\weimao\\Research\\Dome\\dome\\nativeplugincode\\olddomeplugins\\MatlabPlugin\\model1.m",
			                       true,
			                       false);// "C:\\Dome3\\Plugins\\MatlabPlugin\\model1.m");
			print("Creating MatlabModel");
			plg.createModel();

			Object real1 = ((MatlabPlugin) plg).createReal("A");
			Object real2 = ((MatlabPlugin) plg).createReal("B");
			Object real3 = ((MatlabPlugin) plg).createReal("C");

			if (!plg.isModelLoaded()) {
				print("Model is not already loaded, Loading model.");
				plg.loadModel();
			}
			((MatlabReal) real1).setValue(6.66);
			((MatlabReal) real2).setValue(2.22);
			((MatlabReal) real3).setIsResult(true);

			print("Executing...");
			plg.execute();

			print("In java, real1 = " + ((MatlabReal) real1).getValue());
			print("In java, real2 = " + ((MatlabReal) real2).getValue());

			print("In java, result = real3 value = " + ((MatlabReal) real3).getValue());

			print("Unloading Model.");
			plg.unloadModel();

			((MatlabReal) real1).setValue(4.4);
			plg.execute();

			print("In java, real1 = " + ((MatlabReal) real1).getValue());
			print("In java, real2 = " + ((MatlabReal) real2).getValue());
			print("In java, result = real3 value = " + ((MatlabReal) real3).getValue());

			print("Unloading Model.");
			plg.unloadModel();


			Object matrix1 = ((MatlabPlugin) plg).createMatrix("A", 2, 2);
			Object matrix2 = ((MatlabPlugin) plg).createMatrix("B", 2, 2);
			Object matrix3 = ((MatlabPlugin) plg).createMatrix("C", 2, 2);

			if (!plg.isModelLoaded()) {
				print("Model is not already loaded, Loading model.");
				plg.loadModel();
			}

			((MatlabMatrix) matrix3).setIsResult(true);
			double[][] val = new double[2][2];
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					val[i][j] = 100.1234;
				}
			}
			double[][] val1 = new double[2][2];
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					val1[i][j] = 455.4321;
				}
			}
			((MatlabMatrix) matrix1).setValues(val);
			((MatlabMatrix) matrix2).setValues(val1);
			/*
			   ((MatlabMatrix)matrix1).setElement(0,1, 100.1234, true);
			      ((MatlabMatrix)matrix1).setElement(1,0, 100.1234, true);
			    ((MatlabMatrix)matrix1).setElement(1,1, 100.1234, true);


			     ((MatlabMatrix)matrix2).setElement(0,0, 455.4321);
			     ((MatlabMatrix)matrix2).setElement(0,1, 455.4321);
			     ((MatlabMatrix)matrix2).setElement(1,0, 455.4321);
			    ((MatlabMatrix)matrix2).setElement(1,1, 455.4321);
			*/

			print("Executing...");
			plg.execute();

			((MatlabMatrix) matrix1).setElement(0, 1, 500.1234, true);

			print("In C++, matrix A is:\n");
			for (int i = 0; i < ((MatlabMatrix) matrix1).getRows(); i++) {
				for (int j = 0; j < ((MatlabMatrix) matrix1).getColumns(); j++) {
					print(((MatlabMatrix) matrix1).getElement(i, j, true) + "\t");
				}
			}

			print("In C++, matrix B is:\n");
			for (int i = 0; i < ((MatlabMatrix) matrix2).getRows(); i++) {
				for (int j = 0; j < ((MatlabMatrix) matrix2).getColumns(); j++) {
					print(((MatlabMatrix) matrix2).getElement(i, j, true) + "\t");
				}
			}

			print("In java, matrix C is:\n");
			for (int i = 0; i < ((MatlabMatrix) matrix3).getRows(); i++) {
				for (int j = 0; j < ((MatlabMatrix) matrix3).getColumns(); j++) {
					print(((MatlabMatrix) matrix3).getElement(i, j) + "\t");
				}
			}

			int[][] mat = new int[2][3];
			mat[0][0] = 125;
			mat[0][1] = 150;
			mat[0][2] = 175;
			mat[1][0] = 1125;
			mat[1][1] = 1150;
			mat[1][2] = 1175;

			print("In java, matrix mat is:\n");
			int[][] ret = ((MatlabPlugin) plg).testIntMatrix(mat);
			for (int i = 0; i < ret.length; i++) {
				for (int j = 0; j < ret[i].length; j++) {
					print(ret[i][j] + "\t");
				}
			}

			print("Unloading Model.");
			plg.unloadModel();

			print("Finished.");
		} catch (Exception e) {
			print("***" + e.toString());
			//  plg.unloadModel();
		}
	}
}
