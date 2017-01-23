//CirculationPluginTest.java
//author: Sittha Sukkasi
//last update: 1/28/2003

package test.plugin;


import mit.cadlab.dome3.plugin.circulation.CirculationPlugin;
import mit.cadlab.dome3.plugin.circulation.ParseFile;
import mit.cadlab.dome3.plugin.Plugin;

import java.util.Vector;
import java.io.PrintWriter;
import java.io.FileWriter;


public class CirculationPlugTest
{
	private static CirculationPlugin plg;
	private static final String inpath = "C:/dome/GUIPorting/CirculationPluginData/InputData/";
	private static final String resultpath = "C:/dome/GUIPorting/CirculationPluginData/result/";
	private static ParseFile parse;

	static void print(String msg)
	{
		System.out.println(msg);
	}

	public static void main(String[] args)
	{
		try {
			//System.out.println("path = " + System.getProperty("java.library.path"));

			plg = new CirculationPlugin("Circulation",
			                            "C:\\dome\\GUIPorting\\CirculationPluginData\\InputData");
			print("Creating Circulation model");


			plg.createModel();


			double tt = 0.0;
			double dt = 37.29;
			int kmax = 11;

			parse = new ParseFile();
			double[][] bedtop = parse.readBedTopFile(inpath + "Bed_topgraphy.dat");

			int imax = parse.getFirstLine()[0];
			int jmax = parse.getFirstLine()[1];
			int dx = parse.getFirstLine()[2];
			int dy = parse.getFirstLine()[3];


			plg.input_max(imax, jmax, kmax);
			plg.input_controls(dt, 60, 1, 4, 16);
			plg.input_dx_dy(dx, dy);
			plg.input_coeff(9.28, 9.28, 3.50E-03, 5.28, 5.28, 1.00E-03);
			plg.input_topography_vector(false, bedtop);
			//plg.input_data_pythonTestTop();
			plg.input_data_pythonTestFlag();

			int nt = plg.get_nt();
			int nt_start_circ = plg.get_nt_start();
			int nt_end_circ = plg.get_nt_end();
			print("nt = " + nt + ", nt_start_circ = " + nt_start_circ + ", nt_end_circ = " + nt_end_circ);

			PrintWriter vel_timeseries = new PrintWriter(new FileWriter(resultpath + "timeseries.dat", true));
			while (nt <= nt_end_circ) {
				nt = nt + 1;
				plg.set_nt(nt);
				tt = tt + dt;
				print("nt = " + nt + "   Time = " + tt + "\tsecond\n");

				plg.initial_update();
				plg.cal_density();
				plg.cal_velocity();
				plg.cal_vertical_w();
				plg.cal_temperature();
				plg.cal_salinity();
				double[][] matrixOutput = plg.out_flowfield_python(0);
				print("flow field type=0: " + matrixOutput[0][255] + "\n");

				if (nt == nt_end_circ) {
					double[][] matrixOutput2 = plg.OutputUVinZlevel_python(-1, 5.0);
					PrintWriter uv_write = new PrintWriter(new FileWriter(resultpath + "uv_result.dat", true));
					uv_write.println("UV\n");
					for (int i = 0; i < matrixOutput2.length; i++) {
						uv_write.println(matrixOutput2[i][0] + "\t" + matrixOutput2[i][1] + "\t"
						                 + matrixOutput2[i][2] + "\t" + matrixOutput2[i][3]);
					}
					uv_write.close();
				}

				if (nt >= nt_start_circ) {
					int jPoint = 1;
					int iPoint = 1;
					while (iPoint < 13) {
						vel_timeseries.print("\t" + plg.get_ef(iPoint, jPoint));
						iPoint++;
					}
					vel_timeseries.println();
				}
			}
			vel_timeseries.close();

			System.out.println("circulation test2 successful");
		} catch (Exception e) {
			print("***" + e.toString());
			//  plg.unloadModel();
		}
	}
}
