package mit.cadlab.dome3.api.demo;

import mit.cadlab.dome3.api.*;

import java.util.List;
import java.util.Date;
import java.io.*;

/**
 * User: Sangmok Han
 * Date: 2006. 4. 21.
 */
public class SealFEMDataCollector {
    public static void main(String[] args) {

        double[][] femResult = new double[4*4*3*3][7];
        DomeConnection conn = new DomeConnection("root", "cadlab", "2009laptop3:8080");
        //DomeInterface domeInterface = conn.getInterfaceByPath("Server", "Public/demo resources/Parametric door seal CLD analysis/FEA subscription interface");
        DomeInterface domeInterface = conn.getInterfaceByPath("Server", "Public/demo resources/Seal Accu FEA Model/Accu FEA interface");

        double[] p1s = { 20.5, 21, 22, 22.5 }; // width
        double[] p2s = { 11, 12, 13, 13.5 }; // height
        double[] p3s = { 0.90, 1.0, 1.10 }; // thickness
        double[] p4s = { 7.6, 8, 8.6 }; // gap

        try {

            File file = new File("c:/fem_result.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            //RuntimeInterface runtimeInterface = domeInterface.createRuntimeInterface();

            int startFrom = 142;
            int counter = 0;
            for (int i = 0; i < p1s.length; i++) {
                for (int j = 0; j < p2s.length; j++) {
                    for (int k = 0; k < p3s.length; k++) {
                        for (int l = 0; l < p4s.length; l++) {
                            if (counter < startFrom) {
                                counter++;
                                continue;
                            }

                            RuntimeInterface runtimeInterface = domeInterface.createRuntimeInterface();

                            // input
                            RuntimeParameter param1 = runtimeInterface.getParameterByName("seal width"); // 20 - 23 mm
                            RuntimeParameter param2 = runtimeInterface.getParameterByName("seal height"); // 11 - 14 mm
                            RuntimeParameter param3 = runtimeInterface.getParameterByName("seal thickness"); // 0.85 - 1.15 mm
                            RuntimeParameter param4 = runtimeInterface.getParameterByName("seal gap"); // 7 - 9 mm

                            // output
                            RuntimeParameter param5 = runtimeInterface.getParameterByName("Final Contact  Length");
                            RuntimeParameter param6 = runtimeInterface.getParameterByName("Deflection Nom");
                            RuntimeParameter param7 = runtimeInterface.getParameterByName("Load / 100mm Nom");

                            param1.setRealValue(p1s[i]);
                            param2.setRealValue(p2s[j]);
                            param3.setRealValue(p3s[k]);
                            param4.setRealValue(p4s[l]);

                            double p1v = param1.getRealValue();
                            double p2v = param2.getRealValue();
                            double p3v = param3.getRealValue();
                            double p4v = param4.getRealValue();
                            double p5v = param5.getRealValue();
                            double p6v = param6.getRealValue();
                            double p7v = param7.getRealValue();

                            System.out.println("[counter=" + counter + " before submission @ " + new Date() + "] input: " + p1v + " " + p2v + " " + p3v + " " + p4v + " output: " + p5v + " " + p6v + " " + p7v);
                            runtimeInterface.setExecutionTimeLimit(0);
                            runtimeInterface.submit();

                            p1v = param1.getRealValue();
                            p2v = param2.getRealValue();
                            p3v = param3.getRealValue();
                            p4v = param4.getRealValue();
                            p5v = param5.getRealValue();
                            p6v = param6.getRealValue();
                            p7v = param7.getRealValue();

                            femResult[counter] = new double[] { p1v, p2v, p3v, p4v, p5v, p6v, p7v };
                            System.out.println("[counter=" + counter + "  after submission @ " + new Date() + "] input: " + p1v + " " + p2v + " " + p3v + " " + p4v + " output: " + p5v + " " + p6v + " " + p7v);
                            counter = counter + 1;
                            writer.write(p1v + " " + p2v + " " + p3v + " " + p4v + " " + p5v + " " + p6v + " " + p7v);
                            writer.newLine();
                            writer.flush();

                            runtimeInterface.close();
                        }
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }
}
