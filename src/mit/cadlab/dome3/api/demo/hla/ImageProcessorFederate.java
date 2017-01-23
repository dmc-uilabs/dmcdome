package mit.cadlab.dome3.api.demo.hla;

import hla.rti1516.*;
import se.pitch.prti1516.LogicalTimeDouble;
import java.util.*;
import mit.cadlab.dome3.plugin.extendsim.ExtendSimPluginCaller;

public class ImageProcessorFederate extends TimeSynchronizedMessagingFederate {
    public static String IMGPROC_ID = "IMGPROC";

    private long extendPollingPeriod = 50; // how often to check extend app time

    private boolean isExtendPaused = true;
    private double extendTime = 0;
    private ExtendSimPluginCaller caller;
    private List itemQueue;
    private int inCount = 0;
    private int outCount = 0;

    private static String COUNTER_ID = "6";
    private static String GEN_SWITCH_ID = "1";

    public ImageProcessorFederate(String sessionId, String filePath) {
        super(IMGPROC_ID, sessionId, 1000, 1, 0.01, filePath);
        isExtendPaused = true;
        extendTime = 0;
        itemQueue = new ArrayList();
    }

    public boolean isExtendPaused() {
        return isExtendPaused;
    }

    public boolean isImageProcessorPaused() {
        return isPaused;
    }

    private void getExtendReady() {
        caller = new ExtendSimPluginCaller("ExtendSimPlugin");
        caller.setDebug(false);
        Thread starterThread = new Thread() {
                public void run() {
                ExtendSimPluginCaller pluginCaller = new ExtendSimPluginCaller("ExtendSimPlugin");
                pluginCaller.setDebug(false);
                pluginCaller.executeCommand("ExecuteMenuCommand(6000);"); // start
                isExtendPaused = false;
                pluginCaller.finishUsingThisCaller();
            }
        };
        starterThread.setDaemon(false);
        starterThread.start();
        try { Thread.sleep(300); } catch (InterruptedException e) { }
        caller.executeCommand("ExecuteMenuCommand(30001);"); // pause
        isExtendPaused = true;
        try { Thread.sleep(300); } catch (InterruptedException e) { }
    }

    private void generateExtendItem() {
        caller.setRealValue("conValue:#" + GEN_SWITCH_ID, 1);
        caller.setRealValue("conValue:#" + GEN_SWITCH_ID, 0);
    }

    public double getExtendTime() {
        return extendTime;
    }

    public int getQueueSize() {
        return inCount - outCount;
    }

    private void resumeExtend() {
        caller.executeCommand("ExecuteMenuCommand(30002);"); // resume
    }

    private void pauseExtend() {
        caller.executeCommand("ExecuteMenuCommand(30001);"); // pause
    }

    private int getExtendCounter() {
        return (int) caller.getRealValue("count:#" + COUNTER_ID);
    }

    /** if there is no scheduled item, it returns Double.MAX_VALUE */
    public double getTimeForNextScheduledItem() {
        if (itemQueue.size() > 0) {
            return ((Double) itemQueue.get(0)).doubleValue();
        } else {
            return Double.MAX_VALUE;
        }
    }

    public void consumeAnScheduledItem() {
        itemQueue.remove(0);
    }

    public void run() {
        getExtendReady();

        int curCounter = getExtendCounter();
        while (true) {
            isPaused = false;

            resumeExtend();
            isExtendPaused = false;
            double localTime = getLocalTime();
            while (extendTime < localTime) {
                extendTime = caller.getCurrentTime();

                if (extendTime >= getTimeForNextScheduledItem()) {
                    /* generate an item */
                    pauseExtend();
                    generateExtendItem();
                    inCount++;
                    addLog("item generated: Ghost=" + getLocalTime() + ", Extend=" + extendTime + " Scheduled=" + getTimeForNextScheduledItem());
//                    addLog("item generated: GHOST t=" + getLocalTime() + ", EXTEND t=" + extendTime + " (scheduled for t=" + getTimeForNextScheduledItem() + ", QUEUED=" + (inCount - outCount));
                    consumeAnScheduledItem();
                    resumeExtend();
                }

                int newCounter = getExtendCounter();
                if (curCounter != newCounter) {
                    /* interaction message is sent immediately */
                    LogicalTimeDouble timeStampedTime = new LogicalTimeDouble(getLocalTime() + epsilon.getValue() * 2);
                    sendInteractionMessage(AirplaneFederate.AIRPLANE_1_ID, "image processed", timeStampedTime);
                    sendInteractionMessage(AirplaneFederate.AIRPLANE_2_ID, "image processed", timeStampedTime);
                    outCount++;
                    curCounter = newCounter;
                    addLog("sent 'image processed': timestamped=" + nf.format(timeStampedTime.getValue()) + ", Extend=" + extendTime);
                }

                try { Thread.sleep(extendPollingPeriod); } catch (InterruptedException e) { }
                resumeExtend();
            }

            pauseExtend();
            isExtendPaused = true;

            /* make a time advance request */
            isPaused = true;
            requestForTimeAdvance();
        }
    }

    public final void receiveInteraction(InteractionClassHandle interaction, ParameterHandleValueMap valueMap, byte[] bytes, OrderType orderType, TransportationType transportationType, LogicalTime timeStampedTime, OrderType orderType1) {
        synchronized (reservationSemaphore) {
            if (interaction.equals(CID_COMMUNICATION)) {
                /* read message, sender, and receiver from interaction */
                String message = new String((byte[]) valueMap.get(PID_MESSAGE));
                String sender = new String((byte[]) valueMap.get(PID_SENDER));
                String receiver = new String((byte[]) valueMap.get(PID_RECEIVER));

                if (ImageProcessorFederate.IMGPROC_ID.equals(receiver)) {
                    try {
                        double timeStampedTimeValue = ((LogicalTimeDouble) timeStampedTime).getValue();
                        itemQueue.add(new Double(timeStampedTimeValue));
                        addLog("received a picture: timestamped=" + nf.format(timeStampedTimeValue));
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }
    }
}
