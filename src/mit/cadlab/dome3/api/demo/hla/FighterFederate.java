package mit.cadlab.dome3.api.demo.hla;

import hla.rti1516.*;
import se.pitch.prti1516.FederateAmbassadorImpl;
import se.pitch.prti1516.RTI;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.MalformedURLException;

import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.RuntimeInterface;
import mit.cadlab.dome3.api.domewc.WebClientUtil;

public class FighterFederate extends FederateAmbassadorImpl {
    public static final String ID = "FIGHTER";
    public final static int FIGHTER_START_IDX = 10;
    public final static int FIGHTER_STEP_TIME = 120;
    public final static int ROCKET_FULL_COUNT = 5;
    public final static int BOMB_FULL_COUNT = 5;
    private static final int CRC_PORT = 8989;

    private RTIambassador ambs;
    private InteractionClassHandle CID_COMMUNICATION;
    private ParameterHandle PID_MESSAGE;
    private ParameterHandle PID_SENDER;
    private ParameterHandle PID_RECEIVER;
    private ObjectClassHandle CID_PARTICIPANT;
    private ObjectInstanceHandle IID_INSTANCE_ID;
    private AttributeHandle PID_NAME;
    private AttributeHandle PID_LOCATION;
    private AttributeHandle PID_SHIELD;
    private AttributeHandle PID_ROCKET_COUNT;
    private AttributeHandle PID_BOMB_COUNT;
    private final Object reservationSemaphore = new Object();

    private boolean waitingForTankComputation = false;
    private String sessionId = null;
    private String instanceId = null;
    private int rocketCount = ROCKET_FULL_COUNT;
    private int bombCount = BOMB_FULL_COUNT;
    private int fighterIdx = FIGHTER_START_IDX;

    private Timer timer = null;

    /** (ex) new FighterFederate("000", "C:/dome3/out/WarField.xml"); */
    public FighterFederate(String sessionId, String filePath) {
        this.sessionId = sessionId;
        setup(filePath);
    }

    public void setup(String filePath) {
        String rtiHost = "localhost";
        try {
            ambs = RTI.getRTIambassador(rtiHost, CRC_PORT);
        } catch (Exception e) {
            System.out.println("Unable to connect to CRC on " + rtiHost + ":" + CRC_PORT);
            return;
        }

        try {
            ambs.destroyFederationExecution("FIELD-" + sessionId);
        } catch (Exception e) { /* ignored */ }

        try {
            final File fddFile = new File(filePath);
            ambs.createFederationExecution("FIELD-" + sessionId, fddFile.toURL());
        } catch (MalformedURLException e) {
            System.out.println("error in reading WarField.xml");
        } catch (Exception e) { /* ignored */ }

        try {
            ambs.joinFederationExecution("WarField", "FIELD-" + sessionId, this, null);
        } catch (Exception e) { System.out.println("error in joining WarField Federation called FIELD-" + sessionId); /* ignored */ }

        try {
            // Subscribe and publish interactions
            CID_COMMUNICATION = ambs.getInteractionClassHandle("Communication");
            PID_MESSAGE = ambs.getParameterHandle(CID_COMMUNICATION, "Message");
            PID_SENDER = ambs.getParameterHandle(CID_COMMUNICATION, "Sender");
            PID_RECEIVER = ambs.getParameterHandle(CID_COMMUNICATION, "Receiver");

            ambs.subscribeInteractionClass(CID_COMMUNICATION);
            ambs.publishInteractionClass(CID_COMMUNICATION);

            // Subscribe and publish objects
            CID_PARTICIPANT = ambs.getObjectClassHandle("Participant");
            PID_NAME = ambs.getAttributeHandle(CID_PARTICIPANT, "Name");
            PID_LOCATION = ambs.getAttributeHandle(CID_PARTICIPANT, "Location");
            PID_ROCKET_COUNT = ambs.getAttributeHandle(CID_PARTICIPANT, "RocketCount");
            PID_BOMB_COUNT = ambs.getAttributeHandle(CID_PARTICIPANT, "BombCount");
            PID_SHIELD = ambs.getAttributeHandle(CID_PARTICIPANT, "Shield");

            AttributeHandleSet attrSet;
            attrSet = ambs.getAttributeHandleSetFactory().create();
            attrSet.add(PID_NAME);
            attrSet.add(PID_LOCATION);
            attrSet.add(PID_ROCKET_COUNT);
            attrSet.add(PID_BOMB_COUNT);
            attrSet.add(PID_SHIELD);

            ambs.subscribeObjectClassAttributes(CID_PARTICIPANT, attrSet);
            ambs.publishObjectClassAttributes(CID_PARTICIPANT, attrSet);
        } catch (Exception e) { System.out.println("error in setting up publishing & subscription"); /* ignored */}

        try {
            instanceId = FighterFederate.ID;
            ambs.reserveObjectInstanceName(instanceId);
            synchronized (reservationSemaphore) {
                try {
                    reservationSemaphore.wait();
                } catch (InterruptedException ignored) { }
            }
            IID_INSTANCE_ID = ambs.registerObjectInstance(CID_PARTICIPANT, instanceId);
            System.out.println("successfully registered as " + instanceId);
        } catch (Exception e) { System.out.println("error in registering instanceId"); }

        /* notify itself to others */
        try {
            AttributeHandleValueMap valueMap;
            valueMap = ambs.getAttributeHandleValueMapFactory().create(2);
            valueMap.put(PID_LOCATION, Integer.toString(fighterIdx).getBytes());
            valueMap.put(PID_ROCKET_COUNT, Integer.toString(rocketCount).getBytes());
            valueMap.put(PID_BOMB_COUNT, Integer.toString(bombCount).getBytes());
            valueMap.put(PID_NAME, instanceId.getBytes());
            ambs.updateAttributeValues(IID_INSTANCE_ID, valueMap, null);
        } catch (Exception e) { e.printStackTrace(); System.out.println("error in setting my attribute and getting others' attributes"); }
    }

    public void setInMove(boolean inMove) {
        if (inMove) {
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                public void run() {
                    fighterIdx++;
                    if (fighterIdx >= 140) {
                        fighterIdx = 0;
                    }
                }
            }, 0, FIGHTER_STEP_TIME);
        } else {
            timer.cancel();
        }
    }

    public int getFighterLocation() {
        if (fighterIdx >= 10 && fighterIdx < 60) {
            return (fighterIdx - 10);
        } else {
            return 1000;
        }
    }

    public int getFighterIndex() {
        return fighterIdx;
    }

    public void setLocation(int location) {
        fighterIdx = location;
    }

    public void setRocketCount(int rocketCount) {
        this.rocketCount = rocketCount;
    }

    public void setBombCount(int bombCount) {
        this.bombCount = bombCount;
    }

    public void close() {
        try {
            ambs.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
            ambs.destroyFederationExecution("FIELD-" + sessionId);
        } catch (Exception e) { System.out.println("error in cleaning up the federatation: " + e); }
    }

    public void sendFireRocketInteraction() {
        try {
            ParameterHandleValueMap parameters = ambs.getParameterHandleValueMapFactory().create(3);
            parameters.put(PID_MESSAGE, ("FL=" + getFighterLocation() + ",WT=1").getBytes());
            parameters.put(PID_SENDER, FighterFederate.ID.getBytes());
            parameters.put(PID_RECEIVER, TankFederate.ID.getBytes());
            ambs.sendInteraction(CID_COMMUNICATION, parameters, null);
            rocketCount--;
        } catch (Exception e) { System.out.println("error in sending an interaction"); }
    }

    public void sendFireBombInteraction() {
        try {
            waitingForTankComputation = true;
            ParameterHandleValueMap parameters = ambs.getParameterHandleValueMapFactory().create(3);
            parameters.put(PID_MESSAGE, ("FL=" + getFighterLocation() + ",WT=2").getBytes());
            parameters.put(PID_SENDER, FighterFederate.ID.getBytes());
            parameters.put(PID_RECEIVER, TankFederate.ID.getBytes());
            ambs.sendInteraction(CID_COMMUNICATION, parameters, null);
            bombCount--;
        } catch (Exception e) { System.out.println("error in sending an interaction"); }
    }

    public final void receiveInteraction(InteractionClassHandle interaction, ParameterHandleValueMap valueMap, byte[] _theUserSuppliedTag, OrderType _theOrderType, TransportationType _theTransport) {
        /* receive Done! message from TankFederate */
        if (interaction.equals(CID_COMMUNICATION)) {
            String message = "";
            String sender = "";
            String receiver = "";
            for (Iterator i = valueMap.keySet().iterator(); i.hasNext();) {
                ParameterHandle parameterHandle = (ParameterHandle) i.next();
                if (parameterHandle.equals(PID_MESSAGE)) {
                    message = new String((byte[]) valueMap.get(parameterHandle));
                } else if(parameterHandle.equals(PID_SENDER)) {
                    sender = new String((byte[]) valueMap.get(parameterHandle));
                } else if(parameterHandle.equals(PID_RECEIVER)) {
                    receiver = new String((byte[]) valueMap.get(parameterHandle));
                }
            }

            if (TankFederate.ID.equals(sender) && FighterFederate.ID.equals(receiver)) {
                synchronized (this) {
//                    System.out.println("Tank notifies " + message);
                    this.notify();
                }
            }
        }
    }

    public boolean isWaitingForTankComputation() {
        return waitingForTankComputation;
    }

    public int getRocketCount() {
        return rocketCount;
    }

    public int getBombCount() {
        return bombCount;
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
