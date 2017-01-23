// MathematicaPlugTest.java

package test.plugin;


import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.plugin.Plugin;
import mit.cadlab.dome3.plugin.mathematica.MathematicaPlugin;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaVector;

public class MathematicaPlugTest
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
			//System.out.println("path = " + System.getProperty("java.library.path"));

			plg = new MathematicaPlugin("MathematicaPlugin",
			                            "C:/Documents and Settings/weimao/Personal/MathematicaModel1.m"); //"Z:/Mathematica/dome/nativeplugincode/NewPlugins/MathematicaPlugin/model1.m");
			print("Creating MathematicaModel");
			plg.createModel();

			//*****************************************************************************
			print("Creating vector1,2,3..");
			Object vector1 = ((MathematicaPlugin) plg).createVector("a", 2);
			Object vector2 = ((MathematicaPlugin) plg).createVector("b", 2);
			Object vector3 = ((MathematicaPlugin) plg).createVector("c", 2);

			if (!plg.isModelLoaded()) {
				print("Model is not loaded, loading model.");
				plg.loadModel();
			}

			((MathematicaVector) vector3).setIsResult(true);

			double[] val = new double[2];
			val[0] = 106.1;
			val[1] = 85.2;

			double[] val1 = new double[2];
			val1[0] = 15.99;
			val1[1] = 8;

			print("Setting values for vector1 and 2");
			((MathematicaVector) vector1).setValues(val);
			((MathematicaVector) vector2).setValues(val1);

			print("Java: checking vector1 and 2 dimensions");
			System.out.print("dim1 = " + ((MathematicaVector) vector1).getSize() + " ,");

			System.out.println("dim2 = " + ((MathematicaVector) vector2).getSize());

			print("Java: checking set values of vector1");
			for (int i = 0; i < ((MathematicaVector) vector1).getSize(); i++) {
				System.out.print(((MathematicaVector) vector1).getElement(i) + "\t");
			}
			System.out.println("");

			print("Java: checking set values of vector2");
			for (int i = 0; i < ((MathematicaVector) vector2).getSize(); i++) {
				System.out.print(((MathematicaVector) vector2).getElement(i) + "\t");
			}
			System.out.println("");

			print("Port data to the C++ side...");
			plg.execute();

			print("C++: check vector1");
			for (int i = 0; i < ((MathematicaVector) vector1).getSize(); i++) {
				System.out.print(((MathematicaVector) vector1).getElement(i, true) + "\t");
			}
			System.out.println("");

			print("C++: check vector2");
			for (int i = 0; i < ((MathematicaVector) vector2).getSize(); i++) {
				System.out.print(((MathematicaVector) vector2).getElement(i, true) + "\t");
			}
			System.out.println("");

			print("C++: result vector3");
			for (int i = 0; i < ((MathematicaVector) vector3).getSize(); i++) {
				System.out.print(((MathematicaVector) vector3).getElement(i, true) + "\t");
			}
			System.out.println("");

			print("Java: result vector3");
			for (int i = 0; i < ((MathematicaVector) vector3).getSize(); i++) {
				System.out.print(((MathematicaVector) vector3).getElement(i) + "\t");
			}
			System.out.println("");

			//*****************************************************************************
/*            print("Creating integer1,2,3..");
            Object int1 = ((MathematicaPlugin)plg).createInteger("a");
            Object int2 = ((MathematicaPlugin)plg).createInteger("b");
            Object int3 = ((MathematicaPlugin)plg).createInteger("c");

            if(!plg.isModelLoaded()) {
                print("Model is not already loaded, Loading model.");
                plg.loadModel();
            }

            print("Setting values for integer1 and 2");
            ((MathematicaInteger)int1).setValue(6);
            ((MathematicaInteger)int2).setValue(2);
            ((MathematicaInteger)int3).setIsResult(true);

            print("Checking the set values of integer1 and 2");
            System.out.println("integer1 = "+ ((MathematicaInteger)int1).getValue());
            System.out.println("integer2 = "+ ((MathematicaInteger)int2).getValue());

            print("Executing...");
            plg.fireModelChanged();

            print("Returned integer3 value = " + ((MathematicaInteger)int3).getValue());

            print("Unloading Model.");
            plg.unloadModel();

            if(!plg.isModelLoaded()) {
                print("Model is not loaded, Loading model.");
                plg.loadModel();
            }

            print("Resetting values for integer1 and 2");
            ((MathematicaInteger)int1).setValue(900);
            ((MathematicaInteger)int2).setValue(-99);

            print("Checking the reset values of integer1 and 2");
            System.out.println("integer1 = "+ ((MathematicaInteger)int1).getValue());
            System.out.println("integer2 = "+ ((MathematicaInteger)int2).getValue());

            print("Executing...");
            plg.fireModelChanged();

            print("New returned integer3 value = " + ((MathematicaInteger)int3).getValue());

            print("Unloading Model.");
            plg.unloadModel();
*/
			//*****************************************************************************

/*            print("Creating real1,2,3..");
            Object real1 = ((MathematicaPlugin)plg).createReal("a");
            Object real2 = ((MathematicaPlugin)plg).createReal("b");
            Object real3 = ((MathematicaPlugin)plg).createReal("c");

            if(!plg.isModelLoaded()) {
                print("Model is not already loaded, Loading model.");
                plg.loadModel();
            }

            print("Setting values for real1 and 2");
            ((MathematicaReal)real1).setValue(6.66);
            ((MathematicaReal)real2).setValue(2.22);
            ((MathematicaReal)real3).setIsResult(true);

            print("Checking the set values of real1 and 2");
            System.out.println("real1 = "+ ((MathematicaReal)real1).getValue());
            System.out.println("real2 = "+ ((MathematicaReal)real2).getValue());

            print("Executing...");
            plg.execute();

            print("Returned real3 value = " + ((MathematicaReal)real3).getValue());

            print("Unloading Model.");
            plg.unloadModel();

            if(!plg.isModelLoaded()) {
                print("Model is not loaded, reloading model.");
                plg.loadModel();
            }

            print("Resetting values for real1 and 2");
            ((MathematicaReal)real1).setValue(4.4);
            ((MathematicaReal)real1).setValue(105.9);
            print("Executing again...");
            plg.fireModelChanged();

            print("updated real1 = " + ((MathematicaReal)real1).getValue());
            print("updated real2 = " + ((MathematicaReal)real2).getValue());
            print("new result, real3 value = " + ((MathematicaReal)real3).getValue());

            print("Unloading Model.");
            plg.unloadModel();
*/
			//*****************************************************************************
/*
            print("Creating matrix1,2,3..");
            Object matrix1 = ((MathematicaPlugin)plg).createMatrix("a", 2, 2);
		    Object matrix2 = ((MathematicaPlugin)plg).createMatrix("b", 2, 2);
            Object matrix3 = ((MathematicaPlugin)plg).createMatrix("c", 2, 2);

            if(!plg.isModelLoaded()) {
                print("Model is not loaded, reloading model.");
                plg.loadModel();
            }

            ((MathematicaMatrix)matrix3).setIsResult(true);

            double[][] val = new double[2][2];
            val[0][0] = 105.2;
            val[0][1] = 85.2;
            val[1][0] = 10;
            val[1][1] = 0.99;

            double[][] val1 = new double[2][2];
            val1[0][0] = 15;
            val1[0][1] = 8;
            val1[1][0] = 1.25;
            val1[1][1] = 9.119;

            print("Setting values for matrix1 and 2");
            ((MathematicaMatrix)matrix1).setValues(val);
            ((MathematicaMatrix)matrix2).setValues(val1);

            print("Java: checking matrix1 and 2 dimensions");
            System.out.print("row1 = "+ ((MathematicaMatrix)matrix1).getRows() + " ,");
            System.out.println("col1 = "+ ((MathematicaMatrix)matrix1).getColumns());

            System.out.print("row2 = "+ ((MathematicaMatrix)matrix2).getRows() + " ,");
            System.out.println("col2 = "+ ((MathematicaMatrix)matrix2).getColumns());

            print("Java: checking set values of matrix1");
            for(int i =0; i < ((MathematicaMatrix)matrix1).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix1).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix1).getElement(i, j) + "\t");
                }
                System.out.println("");
            }

            print("Java: checking set values of matrix2");
            for(int i =0; i < ((MathematicaMatrix)matrix2).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix2).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix2).getElement(i, j) + "\t");
                }
                System.out.println("");
            }

            print("Port data to the C++ side...");
            plg.fireModelChanged();

            print("C++: check matrix1");
            for(int i =0; i < ((MathematicaMatrix)matrix1).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix1).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix1).getElement(i, j,true) + "\t");
                }
                System.out.println("");
            }

            print("C++: check matrix2");
            for(int i =0; i < ((MathematicaMatrix)matrix2).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix2).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix2).getElement(i, j,true) + "\t");
                }
                System.out.println("");
            }

            print("C++: result = returned matrix3");
            for(int i =0; i < ((MathematicaMatrix)matrix3).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix3).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix3).getElement(i, j,true) + "\t");
                }
                System.out.println("");
            }

            print("C++: result = returned matrix3 (by getValues)");
            double[][] mat3val = ((MathematicaMatrix)matrix3).getValues(true);
            for(int i =0; i < ((MathematicaMatrix)matrix3).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix3).getColumns(); j++ ) {
                    System.out.print(mat3val[i][j] + "\t");
                }
                System.out.println("");
            }

            print("Java: result returned matrix3");
            for(int i =0; i < ((MathematicaMatrix)matrix3).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix3).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix3).getElement(i, j) + "\t");
                }
                System.out.println("");
            }

            val[0][0] = 0.0001;
            val[0][1] = 0.0002;
            val[1][0] = 0.0003;
            val[1][1] = 0.0004;
            print("Resetting values for matrix1");
            ((MathematicaMatrix)matrix1).setValues(val);

            print("Updating values of matrix1 on C++ side...");
            plg.fireModelChanged();

            print("Java: check the reset matrix1");
            for(int i =0; i < ((MathematicaMatrix)matrix1).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix1).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix1).getElement(i, j) + "\t");
                }
                System.out.println("");
            }

            print("C++: check the reset matrix1");
            for(int i =0; i < ((MathematicaMatrix)matrix1).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix1).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix1).getElement(i, j) + "\t");
                }
                System.out.println("");
            }

            print("And here's the new result");
            print("Java: new matrix3");
            for(int i =0; i < ((MathematicaMatrix)matrix3).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix3).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix3).getElement(i, j) + "\t");
                }
                System.out.println("");
            }

            print("Unloading Model.");
            plg.unloadModel();

            if(!plg.isModelLoaded()) {
                print("Model is not loaded, reloading model.");
                plg.loadModel();
            }

            val[0][0] = 69;
            val[0][1] = 98;
            val[1][0] = 87;
            val[1][1] = 74;

            val1[0][0] = 41;
            val1[0][1] = 12;
            val1[1][0] = 23;
            val1[1][1] = 36;

            print("Resetting values for matrix1 and 2");
            ((MathematicaMatrix)matrix1).setValues(val);
            ((MathematicaMatrix)matrix2).setValues(val1);

            print("Java: checking reset values of matrix1");
            for(int i =0; i < ((MathematicaMatrix)matrix1).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix1).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix1).getElement(i, j) + "\t");
                }
                System.out.println("");
            }

            print("Java: checking reset values of matrix2");
            for(int i =0; i < ((MathematicaMatrix)matrix2).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix2).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix2).getElement(i, j) + "\t");
                }
                System.out.println("");
            }

            print("Port new data to the C++ side...");
            plg.fireModelChanged();

            print("C++: check reset matrix1");
            for(int i =0; i < ((MathematicaMatrix)matrix1).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix1).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix1).getElement(i, j,true) + "\t");
                }
                System.out.println("");
            }

            print("C++: check reset matrix2");
            for(int i =0; i < ((MathematicaMatrix)matrix2).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix2).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix2).getElement(i, j,true) + "\t");
                }
                System.out.println("");
            }

            print("C++: new result = returned matrix3");
            for(int i =0; i < ((MathematicaMatrix)matrix3).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix3).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix3).getElement(i, j,true) + "\t");
                }
                System.out.println("");
            }

            print("Java: new result returned matrix3");
            for(int i =0; i < ((MathematicaMatrix)matrix3).getRows(); i++ ) {
                for(int j =0; j < ((MathematicaMatrix)matrix3).getColumns(); j++ ) {
                    System.out.print(((MathematicaMatrix)matrix3).getElement(i, j) + "\t");
                }
                System.out.println("");
            }

            print("Unloading Model.");
            plg.unloadModel();
            print("Finished.");
 */
		} catch (Exception e) {
			print("***" + e.toString());
			//  plg.unloadModel();
		}
	}
}
