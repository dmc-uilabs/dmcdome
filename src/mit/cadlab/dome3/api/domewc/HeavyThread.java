package mit.cadlab.dome3.api.domewc;

import java.io.PrintWriter;

public class HeavyThread extends Thread {
    long period = 0;
    String[] lines = new String[0];
    PrintWriter writer = null;
    int index = 0;

    public HeavyThread(long period, PrintWriter writer, String[] lines) {
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
