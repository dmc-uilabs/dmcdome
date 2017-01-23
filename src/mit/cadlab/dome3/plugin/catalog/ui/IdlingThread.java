package mit.cadlab.dome3.plugin.catalog.ui;

import java.io.PrintWriter;

/**
 * User: Sangmok Han
 * Date: Sep 22, 2006
 */
public class IdlingThread extends Thread {
    long period = 0;
    String[] lines = new String[0];
    PrintWriter writer = null;
    int index = 0;

    public IdlingThread(long period, PrintWriter writer, String[] lines) {
        this.period = period;
        this.lines = lines;
        this.writer = writer;
    }

    public void run() {
        while (index < lines.length) {
            System.out.println("[flush] index = " + index + ", line = " + lines[index]);
            writer.println(lines[index]);
            writer.flush();
            try {
                sleep(period);
                index++;
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}