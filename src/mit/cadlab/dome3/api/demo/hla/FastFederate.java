package mit.cadlab.dome3.api.demo.hla;

import hla.rti1516.*;
import se.pitch.prti1516.FederateAmbassadorImpl;
import se.pitch.prti1516.RTI;
import se.pitch.prti1516.LogicalTimeDouble;
import se.pitch.prti1516.LogicalTimeIntervalDouble;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.MalformedURLException;

import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.RuntimeInterface;

public class FastFederate extends FederateAmbassadorImpl {
    public static final String ID = "FAST_FED";

    private RTIambassador absd;
    private InteractionClassHandle CID_COMMUNICATION;
    private ParameterHandle PID_MESSAGE;
    private ParameterHandle PID_SENDER;
    private ParameterHandle PID_RECEIVER;
    private ObjectClassHandle CID_PARTICIPANT;
    private ObjectInstanceHandle IID_INSTANCE_ID;
    private AttributeHandle PID_NAME;
    private AttributeHandle PID_LOCATION;
    private AttributeHandle PID_SHIELD;
    private final Object reservationSemaphore = new Object();

    private int location = 100;
    private int shield = 100;
    private Timer timer = null;
    private String sessionId = null;
    private String instanceId = null;
    private LogicalTimeDouble theTime = null;
    private LogicalTimeIntervalDouble interval = new LogicalTimeIntervalDouble(1);
    private LogicalTimeIntervalDouble halfInterval = new LogicalTimeIntervalDouble(0.5);
    private static final int CRC_PORT = 8989;

    /** (ex) new FastFederate("000", "C:/dome3/out/WarField.xml"); */
    public FastFederate(String sessionId, String filePath) {
        this.sessionId = sessionId;
        setup(filePath);
    }

    public void setup(String filePath) {
        String rtiHost = "localhost";
        try {
            absd = RTI.getRTIambassador(rtiHost, CRC_PORT);
        } catch (Exception e) {
            System.out.println("Unable to connect to CRC on " + rtiHost + ":" + CRC_PORT);
            return;
        }

        try {
            absd.destroyFederationExecution("FIELD-" + sessionId);
        } catch (Exception e) { /* ignored */ }

        try {
            final File fddFile = new File(filePath);
            absd.createFederationExecution("FIELD-" + sessionId, fddFile.toURL());
        } catch (MalformedURLException e) {
            System.out.println("error in reading WarField.xml");
        } catch (Exception e) { /* ignored */ }

        try {
            absd.joinFederationExecution("WarField", "FIELD-" + sessionId, this, null);
        } catch (Exception e) { System.out.println("error in joining WarField Federation called FIELD-" + sessionId); /* ignored */ }

        try {
            // Subscribe and publish interactions
            CID_COMMUNICATION = absd.getInteractionClassHandle("Communication");
            PID_MESSAGE = absd.getParameterHandle(CID_COMMUNICATION, "Message");
            PID_SENDER = absd.getParameterHandle(CID_COMMUNICATION, "Sender");
            PID_RECEIVER = absd.getParameterHandle(CID_COMMUNICATION, "Receiver");

            absd.subscribeInteractionClass(CID_COMMUNICATION);
            absd.publishInteractionClass(CID_COMMUNICATION);

            // Subscribe and publish objects
            CID_PARTICIPANT = absd.getObjectClassHandle("Participant");
            PID_NAME = absd.getAttributeHandle(CID_PARTICIPANT, "Name");
            PID_LOCATION = absd.getAttributeHandle(CID_PARTICIPANT, "Location");
            PID_SHIELD = absd.getAttributeHandle(CID_PARTICIPANT, "Shield");

            AttributeHandleSet attrSet;
            attrSet = absd.getAttributeHandleSetFactory().create();
            attrSet.add(PID_NAME);
            attrSet.add(PID_LOCATION);
            attrSet.add(PID_SHIELD);

            absd.subscribeObjectClassAttributes(CID_PARTICIPANT, attrSet);
            absd.publishObjectClassAttributes(CID_PARTICIPANT, attrSet);
        } catch (Exception e) { System.out.println("error in setting up publishing & subscription"); /* ignored */}

        try {
            instanceId = this.ID;
            absd.reserveObjectInstanceName(instanceId);
            synchronized (reservationSemaphore) {
                try {
                    reservationSemaphore.wait();
                } catch (InterruptedException ignored) { }
            }
            IID_INSTANCE_ID = absd.registerObjectInstance(CID_PARTICIPANT, instanceId);
            System.out.println("successfully registered as " + instanceId);
        } catch (Exception e) { System.out.println("error in registering instanceId"); e.printStackTrace(); return; }

        /* notify itself to others */
        try {
            AttributeHandleValueMap valueMap;
            valueMap = absd.getAttributeHandleValueMapFactory().create(2);
            valueMap.put(PID_LOCATION, Integer.toString(location).getBytes());
            valueMap.put(PID_SHIELD, Integer.toString(shield).getBytes());
            valueMap.put(PID_NAME, instanceId.getBytes());
            absd.updateAttributeValues(IID_INSTANCE_ID, valueMap, null);
        } catch (Exception e) { e.printStackTrace(); System.out.println("error in setting my attribute and getting others' attributes"); }

        theTime = new LogicalTimeDouble(0);
        synchronized (reservationSemaphore) {
            try {
                absd.enableTimeRegulation(halfInterval);
                absd.enableTimeConstrained();
                reservationSemaphore.wait();
            } catch (Exception e) { }
        }

    }

    public void setInMove(boolean inMove) {
        while (theTime.getValue() < 15) {
            try {
                synchronized (reservationSemaphore) {
                    LogicalTimeDouble theNextTime =  new LogicalTimeDouble(theTime.getValue() + interval.getValue());
//                    absd.timeAdvanceRequest(theNextTime);
                    absd.nextMessageRequest(theNextTime);
                    System.out.println("[FAST] waiting... after requesting time advance: " + theNextTime);
                    reservationSemaphore.wait();
                    theTime = theNextTime;
                }

                double theTimeValue = theTime.getValue();
                if (theTimeValue == 1) { System.out.println("[FAST] shoot img #1: " + theTime); }
                if (theTimeValue == 2) { sendInteractionMessageTimeStamped("[FAST] send img #1"); }
                if (theTimeValue == 3) { System.out.println("[FAST] shoot img #2: " + theTime); }
                if (theTimeValue == 4) { sendInteractionMessageTimeStamped("[FAST] send img #2"); }
                if (theTimeValue == 5) { System.out.println("[FAST] shoot img #3: " + theTime); }
                if (theTimeValue == 6) { sendInteractionMessageTimeStamped("[FAST] send img #3"); }
                if (theTimeValue == 7) { System.out.println("[FAST] shoot img #4: " + theTime); }
                if (theTimeValue == 8) { sendInteractionMessageTimeStamped("[FAST] send img #4"); }

                Thread.sleep(35);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void timeConstrainedEnabled(LogicalTime theFederateTime) {
        System.out.println("[FAST] time constrained enabled: " + theFederateTime);
        synchronized (reservationSemaphore) {
            reservationSemaphore.notify();
        }
    }

    public void timeRegulationEnabled(LogicalTime theFederateTime) {
        System.out.println("[FAST] time regulation enabled: " + theFederateTime);
        synchronized (reservationSemaphore) {
            reservationSemaphore.notify();
        }
    }
    public void timeAdvanceGrant(LogicalTime theFederateTime) {
        System.out.println("[FAST] time advance granted: " + theFederateTime);
        synchronized (reservationSemaphore) {
            theTime = (LogicalTimeDouble) theFederateTime;
            reservationSemaphore.notify();
        }
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public int getShield() {
        return shield;
    }

    public int getLocation() {
        return location;
    }

    public final void receiveInteraction(InteractionClassHandle interaction, ParameterHandleValueMap valueMap, byte[] bytes, OrderType orderType, TransportationType transportationType, LogicalTime logicalTime, OrderType orderType1) {
        synchronized (reservationSemaphore) {
            if (interaction.equals(CID_COMMUNICATION)) {
                /* read message, sender, and receiver from interaction */
                String message = new String((byte[]) valueMap.get(PID_MESSAGE));;
                String sender = new String((byte[]) valueMap.get(PID_SENDER));
                String receiver = new String((byte[]) valueMap.get(PID_RECEIVER));

                if (SlowFederate.ID.equals(sender) && FastFederate.ID.equals(receiver)) {
                    try {
                        System.out.println("[FAST] received " + message + " time-stamped " + logicalTime + " at " + theTime);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }
    }

    public final void receiveInteraction(InteractionClassHandle interaction, ParameterHandleValueMap valueMap, byte[] userSuppliedTag, OrderType orderType, TransportationType transport) {
        if (interaction.equals(CID_COMMUNICATION)) {
            /* read message, sender, and receiver from interaction */
            String message = new String((byte[]) valueMap.get(PID_MESSAGE));;
            String sender = new String((byte[]) valueMap.get(PID_SENDER));
            String receiver = new String((byte[]) valueMap.get(PID_RECEIVER));

            if (SlowFederate.ID.equals(sender) && FastFederate.ID.equals(receiver)) {
                try {
                    System.out.println("received " + message + " time-stamped " + "NONO" + " at " + theTime);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    public void sendInteractionMessageTimeStamped(String msg) {
        LogicalTimeDouble wantedSendTime =  new LogicalTimeDouble(theTime.getValue() + halfInterval.getValue());
        sendInteractionMessage(msg, wantedSendTime);
    }

    public void sendInteractionMessage(String msg, LogicalTime time) {
        try {
            ParameterHandleValueMap parameters = absd.getParameterHandleValueMapFactory().create(3);
            parameters.put(PID_MESSAGE, msg.getBytes());
            parameters.put(PID_SENDER, FastFederate.ID.getBytes());
            parameters.put(PID_RECEIVER, SlowFederate.ID.getBytes());
            absd.sendInteraction(CID_COMMUNICATION, parameters, null, time);
        } catch (Exception e) { System.out.println("error in sending an interaction"); e.printStackTrace();}
    }

    public void close() {
        try {
            absd.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
            absd.destroyFederationExecution("FIELD-" + sessionId);
        } catch (Exception e) { System.out.println("error in cleaning up the federate: " + e); }
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

    public final void provideAttributeValueUpdate(final ObjectInstanceHandle _theObject, final AttributeHandleSet _theAttributes, final byte[] _theUserSuppliedTag) {
        new Thread() {
            public void run() {
                System.out.println("[" + instanceId + "] provide attribute is called!");
                if(_theAttributes.contains(PID_NAME) || _theAttributes.contains(PID_LOCATION) || _theAttributes.contains(PID_SHIELD)) {
                    try {
                        AttributeHandleValueMap valueMap;
                        valueMap = absd.getAttributeHandleValueMapFactory().create(1);
                        valueMap.put(PID_NAME, instanceId.getBytes());
                        valueMap.put(PID_LOCATION, Integer.toString(location).getBytes());
                        valueMap.put(PID_SHIELD, Integer.toString(shield).getBytes());
                        absd.updateAttributeValues(IID_INSTANCE_ID, valueMap, null);
                    } catch (Exception ignored) { /* ingored */ }
                }
            }
        }.start();
    }
}
