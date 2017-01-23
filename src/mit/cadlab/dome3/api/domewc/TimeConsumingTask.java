package mit.cadlab.dome3.api.domewc;

import java.io.PrintWriter;

/**
 * User: Sangmok Han
 * Date: 2005. 9. 19.
 */
public class TimeConsumingTask {
    public static void main(String[] args) {

        long period = 2000;
        int count = 10;
        System.out.println("Timer line before passing");

        HeavyThread ht = new HeavyThread(1000, new PrintWriter(System.out), new String[] { "Step1", "Step2", "Step3", "Step4" });
        ht.start();

        try {
            ht.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Timer line after passing");
    }
}