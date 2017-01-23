package mit.cadlab.dome3.api.demo.hla;

import hla.rti1516.*;
import se.pitch.prti1516.LogicalTimeDouble;

public class AirplaneFederate extends TimeSynchronizedMessagingFederate {

    public static String AIRPLANE_1_ID = "AIRPLANE-1";
    public static String AIRPLANE_2_ID = "AIRPLANE-2";
    private int targetCount = 5;
    private double[] pictureTimePoints = null;
    private int nextPictureTimeIdx = 0;
    private int processedImageCounter = 0;
    private boolean finishedRunning = false;

    /** (ex) new AirplaneFederate("000", "C:/dome3/out/WarField.xml"); */
    public AirplaneFederate(String instanceId, String sessionId, String filePath) {
        super(instanceId, sessionId, 1000, 1, 0.01, filePath);
        nextPictureTimeIdx = 0;
        processedImageCounter = 0;
        finishedRunning = false;
    }

    /** pictureTimePoints: when is the time point a picture taken event to be genereated? */
    public void setPictureTakingSchedule(double[] timePoints) {
        this.pictureTimePoints = timePoints;
        this.nextPictureTimeIdx = 0;
    }

    /** tickInRealTime: how much of the real time is a tick equivalent to? */
    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    public boolean isFinished() {
        return finishedRunning;
    }

    public void run() {
        while (true) {
            isPaused = false;

            if (nextPictureTimeIdx < pictureTimePoints.length) {
                double nextPictureTimePoint = pictureTimePoints[nextPictureTimeIdx];
                if (nextPictureTimePoint > getLocalTime() && nextPictureTimePoint <= getNextLocalTime()) {
                    sendInteractionMessage(ImageProcessorFederate.IMGPROC_ID, "a picture taken", new LogicalTimeDouble(nextPictureTimePoint));
//                    addLog("sent a picture: MSG TIME=" + nf.format(nextPictureTimePoint) + ", AIRPLANE TIME=" + getLocalTime());
                    addLog("sent a picture: timestamped=" + nf.format(nextPictureTimePoint));
                    nextPictureTimeIdx++;
                }
            }

            /* wait for a certain amount of time before time-advancing */
            try {
                Thread.sleep(tickInRealTime);
            } catch (InterruptedException e) { }

            isPaused = true;
            requestForTimeAdvance();
            if (processedImageCounter >= targetCount) {
                addLog("mission complete: total " + processedImageCounter + " pictures are processed");
                break;
            }
        }
        finishedRunning = true;
    }

    public final void receiveInteraction(InteractionClassHandle interaction, ParameterHandleValueMap valueMap, byte[] bytes, OrderType orderType, TransportationType transportationType, LogicalTime timeStampedTime, OrderType orderType1) {
        synchronized (reservationSemaphore) {
            if (interaction.equals(CID_COMMUNICATION)) {
                /* read message, sender, and receiver from interaction */
                String message = new String((byte[]) valueMap.get(PID_MESSAGE));
                String sender = new String((byte[]) valueMap.get(PID_SENDER));
                String receiver = new String((byte[]) valueMap.get(PID_RECEIVER));

                if (instanceId.equals(receiver)) {
                    try {
                        processedImageCounter++;
                        double timeStampedTimeValue = ((LogicalTimeDouble) timeStampedTime).getValue();
                        addLog("received 'image processed': timestamped=" + nf.format(timeStampedTimeValue) + ", goal=" + processedImageCounter + "/" + targetCount);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }
    }
}
