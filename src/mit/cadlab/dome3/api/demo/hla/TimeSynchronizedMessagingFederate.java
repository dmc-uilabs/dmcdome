package mit.cadlab.dome3.api.demo.hla;

import hla.rti1516.*;
import se.pitch.prti1516.LogicalTimeDouble;
import se.pitch.prti1516.LogicalTimeIntervalDouble;
import se.pitch.prti1516.RTI;
import se.pitch.prti1516.FederateAmbassadorImpl;

import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.net.MalformedURLException;

/**
 * Implements common functionalities for time synchronized messagine federate
 */
public abstract class TimeSynchronizedMessagingFederate extends FederateAmbassadorImpl {
    protected RTIambassador absd;
    protected InteractionClassHandle CID_COMMUNICATION;
    protected ParameterHandle PID_MESSAGE;
    protected ParameterHandle PID_SENDER;
    protected ParameterHandle PID_RECEIVER;
    protected ObjectClassHandle CID_PARTICIPANT;
    protected ObjectInstanceHandle IID_INSTANCE_ID;
    protected final Object reservationSemaphore = new Object();

    protected String sessionId = null;
    protected String instanceId = null;
    private static final int CRC_PORT = 8989;

    protected long tickInRealTime = 2000; // how often to increase local time
    protected double tickStepSize = 1; // local time increment
    protected double epsilonSize = 0.01; // look ahead size

    protected LogicalTimeDouble localTime = null;
    protected LogicalTimeIntervalDouble tick = new LogicalTimeIntervalDouble(tickStepSize);
    protected LogicalTimeIntervalDouble epsilon = new LogicalTimeIntervalDouble(epsilonSize);

    protected NumberFormat nf;

    protected boolean isPaused = false;
    protected List eventLog = new ArrayList();

    /** (ex) new AirplaneFederate("000", "C:/dome3/out/WarField.xml"); */
    public TimeSynchronizedMessagingFederate(String instanceId, String sessionId, long tickInRealTime, double tickStepSize, double epsilonSize, String filePath) {
        this.instanceId = instanceId;
        this.sessionId = sessionId;
        this.tickInRealTime = tickInRealTime;
        this.tickStepSize = tickStepSize;
        this.epsilonSize = epsilonSize;

        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        isPaused = false;
        eventLog = new ArrayList();
        localTime = new LogicalTimeDouble(0);

        setupFederate(filePath);
    }

    protected void setupFederate(String filePath) {
        String rtiHost = "localhost";
        try {
            absd = RTI.getRTIambassador(rtiHost, CRC_PORT);
        } catch (Exception e) {
            printLog("Unable to connect to CRC on " + rtiHost + ":" + CRC_PORT);
            return;
        }

        try {
            absd.destroyFederationExecution("FIELD-" + sessionId);
        } catch (Exception e) { /* ignored */ }

        try {
            final File fddFile = new File(filePath);
            absd.createFederationExecution("FIELD-" + sessionId, fddFile.toURL());
        } catch (MalformedURLException e) {
            printLog("error in reading WarField.xml");
        } catch (Exception e) { /* ignored */ }

        try {
            absd.joinFederationExecution("WarField", "FIELD-" + sessionId, this, null);
        } catch (Exception e) { printLog("error in joining WarField Federation called FIELD-" + sessionId); /* ignored */ }

        try {
            // Subscribe and publish interactions
            CID_COMMUNICATION = absd.getInteractionClassHandle("Communication");
            PID_MESSAGE = absd.getParameterHandle(CID_COMMUNICATION, "Message");
            PID_SENDER = absd.getParameterHandle(CID_COMMUNICATION, "Sender");
            PID_RECEIVER = absd.getParameterHandle(CID_COMMUNICATION, "Receiver");

            absd.subscribeInteractionClass(CID_COMMUNICATION);
            absd.publishInteractionClass(CID_COMMUNICATION);

            CID_PARTICIPANT = absd.getObjectClassHandle("Participant");
            AttributeHandleSet attrSet;
            attrSet = absd.getAttributeHandleSetFactory().create();
            attrSet.add(absd.getAttributeHandle(CID_PARTICIPANT, "Name"));

            absd.subscribeObjectClassAttributes(CID_PARTICIPANT, attrSet);
            absd.publishObjectClassAttributes(CID_PARTICIPANT, attrSet);
        } catch (Exception e) { printLog("error in setting up publishing & subscription"); /* ignored */}

        try {
            absd.reserveObjectInstanceName(instanceId);
            synchronized (reservationSemaphore) {
                try {
                    reservationSemaphore.wait();
                } catch (InterruptedException ignored) { }
            }
            IID_INSTANCE_ID = absd.registerObjectInstance(CID_PARTICIPANT, instanceId);
            printLog("successfully registered as " + instanceId);
        } catch (Exception e) { printLog("error in registering instanceId"); e.printStackTrace(); return; }

        localTime = new LogicalTimeDouble(0);

        synchronized (reservationSemaphore) {
            try {
                absd.enableTimeRegulation(epsilon);
                reservationSemaphore.wait();
            } catch (Exception e) { }
        }

        synchronized (reservationSemaphore) {
            try {
                absd.enableTimeConstrained();
                reservationSemaphore.wait();
            } catch (Exception e) { }
        }
        try {
            absd.modifyLookahead(epsilon);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** how many logical time step is changed at each tick.
     * it is the maximum amount of time during which the scouter federate is sure that
     * there will be no interaction sent out from it. */
    public void setTickStepSize(double tickStepSize) {
        this.tickStepSize = tickStepSize;
    }

    /** tickInRealTime: how much of the real time is a tick equivalent to? */
    public void setTickInRealTime(long tickInRealTime) {
        this.tickInRealTime = tickInRealTime;
    }

    public long getTickInRealTime() {
        return tickInRealTime;
    }

    public double getLocalTime() {
        return localTime.getValue();
    }

    public double getNextLocalTime() {
        return localTime.getValue() + tick.getValue();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public List getEventLog() {
        return eventLog;
    }

    public void addLog(String msg) {
        String printedStr = "[t=" + nf.format(getLocalTime()) + "] " + msg;
//        String printedStr = "[t=" + (int) getLocalTime() + "] " + msg;
        printLog(printedStr);
        eventLog.add(printedStr);
    }

    protected void requestForTimeAdvance() {
        try {
            synchronized (reservationSemaphore) {
                LogicalTimeDouble nextLocalTime =  new LogicalTimeDouble(getNextLocalTime());
                absd.nextMessageRequest(nextLocalTime);
                reservationSemaphore.wait();
                localTime = nextLocalTime;
//                addLog("time advances: " + instanceId + " TIME=" + nf.format(getTimeValue(nextLocalTime)));
                addLog("time updated: now t=" + nf.format(getTimeValue(nextLocalTime)));
            }
        } catch (Exception e) {
            printLog("error occurred when requesting for a time advance: " + e.getMessage());
        }
    }

    public void printLog(String msg) {
        System.out.println("{" + instanceId + "} " + msg);
    }

    public void timeConstrainedEnabled(LogicalTime theFederateTime) {
        addLog("'time constrained' enabled");
        synchronized (reservationSemaphore) {
            reservationSemaphore.notify();
        }
    }

    public void timeRegulationEnabled(LogicalTime theFederateTime) {
        addLog("'time regulation' enabled");
        synchronized (reservationSemaphore) {
            reservationSemaphore.notify();
        }
    }

    public static double getTimeValue(LogicalTime logicalTime) {
        return ((LogicalTimeDouble) logicalTime).getValue();
    }

    public void timeAdvanceGrant(LogicalTime theFederateTime) {
        synchronized (reservationSemaphore) {
            reservationSemaphore.notify();
        }
    }

    /** when an interaction with a timestamp is received */
    public void receiveInteraction(InteractionClassHandle interaction, ParameterHandleValueMap valueMap, byte[] bytes, OrderType orderType, TransportationType transportationType, LogicalTime timeStampedTime, OrderType orderType1) {
        synchronized (reservationSemaphore) {
            if (interaction.equals(CID_COMMUNICATION)) {
                /* read message, sender, and receiver from interaction */
                String message = new String((byte[]) valueMap.get(PID_MESSAGE));;
                String sender = new String((byte[]) valueMap.get(PID_SENDER));
                String receiver = new String((byte[]) valueMap.get(PID_RECEIVER));

                if (instanceId.equals(receiver)) {
                    try {
                        addLog("received '" + message + "': MSG TIMESTAMP = " + getTimeValue(timeStampedTime));
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }
    }

    /** when an interaction without a timestamp is received */
    public void receiveInteraction(InteractionClassHandle interaction, ParameterHandleValueMap valueMap, byte[] userSuppliedTag, OrderType orderType, TransportationType transport) {
        if (interaction.equals(CID_COMMUNICATION)) {
            /* read message, sender, and receiver from interaction */
            String message = new String((byte[]) valueMap.get(PID_MESSAGE));
            String sender = new String((byte[]) valueMap.get(PID_SENDER));
            String receiver = new String((byte[]) valueMap.get(PID_RECEIVER));

            if (instanceId.equals(receiver)) {
                try {
                    addLog("received '" + message + "': NO TIMESTAMP, LOCAL TIME = " + getTimeValue(localTime));
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    public void sendInteractionMessage(String toId, String msg, LogicalTime time) {
        try {
            ParameterHandleValueMap parameters = absd.getParameterHandleValueMapFactory().create(3);
            parameters.put(PID_MESSAGE, msg.getBytes());
            parameters.put(PID_SENDER, instanceId.getBytes());
            parameters.put(PID_RECEIVER, toId.getBytes());
            absd.sendInteraction(CID_COMMUNICATION, parameters, null, time);
        } catch (Exception e) { printLog("error in sending an interaction"); e.printStackTrace();}
    }

    public void close() {
        try {
            absd.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
            absd.destroyFederationExecution("FIELD-" + sessionId);
        } catch (Exception e) { printLog("error in cleaning up the federate: " + e); }
    }

    public final void discoverObjectInstance(final ObjectInstanceHandle _theInstance, final ObjectClassHandle _theObject, final String _theInstanceName) {
        new Thread() {
            public void run() { }
        }.start();
    }

    public final void reflectAttributeValues(final ObjectInstanceHandle _theInstance, final AttributeHandleValueMap _theAttributes, final byte[] _theUserSuppliedTag, final OrderType _theOrderType, final TransportationType _theTransport) {
        new Thread() {
            public void run() { }
        }.start();
    }

    public final void objectInstanceNameReservationSucceeded(String objectName) {
        synchronized (reservationSemaphore) {
            reservationSemaphore.notify();
        }
    }

    public final void objectInstanceNameReservationFailed(String objectName) {
        synchronized (reservationSemaphore) {
            reservationSemaphore.notify();
        }
    }
}
