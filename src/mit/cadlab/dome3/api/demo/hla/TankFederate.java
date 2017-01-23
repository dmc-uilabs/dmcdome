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

public class TankFederate extends FederateAmbassadorImpl {
    public static final String ID = "TANK";

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
    private final Object reservationSemaphore = new Object();


    private String sessionId = null;
    private String instanceId = null;
    private int tankShield = FULL_SHEILD;
    private int tankLocation = TANK_START_IDX;
    public final static int  TANK_START_IDX = 15;
    public final static int TANK_STEP_TIME = 1500;
    public final static int FULL_SHEILD = 100;

    private static final int CRC_PORT = 8989;
    private Timer timer = null;
    private RuntimeInterface runtimeItf = null;
    private DomeConnection domeConn = null;

    /** (ex) new TankFederate("000", "C:/dome3/out/WarField.xml"); */
    public TankFederate(String sessionId, String filePath) {
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
            PID_SHIELD = ambs.getAttributeHandle(CID_PARTICIPANT, "Shield");

            AttributeHandleSet attrSet;
            attrSet = ambs.getAttributeHandleSetFactory().create();
            attrSet.add(PID_NAME);
            attrSet.add(PID_LOCATION);
            attrSet.add(PID_SHIELD);

            ambs.subscribeObjectClassAttributes(CID_PARTICIPANT, attrSet);
            ambs.publishObjectClassAttributes(CID_PARTICIPANT, attrSet);
        } catch (Exception e) { System.out.println("error in setting up publishing & subscription"); /* ignored */}

        try {
            instanceId = TankFederate.ID;
            ambs.reserveObjectInstanceName(instanceId);
            synchronized (reservationSemaphore) {
                try {
                    reservationSemaphore.wait();
                } catch (InterruptedException ignored) { }
            }
            IID_INSTANCE_ID = ambs.registerObjectInstance(CID_PARTICIPANT, instanceId);
            System.out.println("successfully registered as " + instanceId);
        } catch (Exception e) { System.out.println("error in registering instanceId"); e.printStackTrace(); return; }

        /* notify itself to others */
        try {
            AttributeHandleValueMap valueMap;
            valueMap = ambs.getAttributeHandleValueMapFactory().create(2);
            valueMap.put(PID_LOCATION, Integer.toString(tankLocation).getBytes());
            valueMap.put(PID_SHIELD, Integer.toString(tankShield).getBytes());
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
                    tankLocation++;
                    if (tankLocation >= 50) {
                        tankLocation = 0;
                    }
                }
            }, 0, TANK_STEP_TIME);
        } else {
            timer.cancel();
        }
    }

    public void setLocation(int location) {
        tankLocation = location;
    }

    public void setTankShield(int tankShield) {
        this.tankShield = tankShield;
    }

    public int getTankShield() {
        return tankShield;
    }

    public int getTankLocation() {
        return tankLocation;
    }

    public int getTankIndex() {
        return tankLocation;
    }

    public final void receiveInteraction(InteractionClassHandle interaction, ParameterHandleValueMap valueMap, byte[] userSuppliedTag, OrderType orderType, TransportationType transport) {
        if (interaction.equals(CID_COMMUNICATION)) {
            /* read message, sender, and receiver from interaction */
            String message = new String((byte[]) valueMap.get(PID_MESSAGE));;
            String sender = new String((byte[]) valueMap.get(PID_SENDER));
            String receiver = new String((byte[]) valueMap.get(PID_RECEIVER));

            if (FighterFederate.ID.equals(sender) && TankFederate.ID.equals(receiver)) {
                /* parse message (ex) "FL=15,WT=1" */
                Matcher m = Pattern.compile("FL=([-]?\\d+),WT=([-]?\\d+)").matcher(message);
                m.find();
                int fighterLoc = Integer.parseInt(m.group(1));
                int weaponType = Integer.parseInt(m.group(2));

                /** connect to DOME server and locate the Extend model */
                if (domeConn == null) {
                    String userId = "guest";
                    String passwd = "";
                    String host = "localhost:8080";
                    domeConn = new DomeConnection(userId, passwd, host);
                    String path = "Public/extendsim/Tank Shield  Model/Complete Interface";
                    runtimeItf = domeConn.getInterfaceByPath(path).createRuntimeInterface();
                }

                /* change input parameters & execute the Extend model & retrieve input parameters */
                runtimeItf.getParameterByName("weapon type").setIntegerValue(weaponType);
                runtimeItf.getParameterByName("fighter location").setRealValue(fighterLoc);
                runtimeItf.getParameterByName("tank location").setRealValue(tankLocation);
                runtimeItf.submit();
                double damage = runtimeItf.getParameterByName("shield reduction").getRealValue();
                System.out.println("[DOME API] weaponType=" + weaponType + ", locDiff=" + Math.abs(fighterLoc - tankLocation) + " -> damage=" + damage);

                /* adjust tank shield based on damage */
                final int prevShield = tankShield;
                final int afterShield = ((tankShield - (int) damage) > 0) ? (tankShield - (int) damage) : 0;
                tankShield = afterShield;

                /* notify computation finished */
                new Thread() { public void run() { sendComputationFinished(prevShield - afterShield); } }.start();
            }
        }
    }

    public void sendComputationFinished(int shieldReduction) {
        try {
            ParameterHandleValueMap parameters = ambs.getParameterHandleValueMapFactory().create(3);
            parameters.put(PID_MESSAGE, Integer.toString(shieldReduction).getBytes());
            parameters.put(PID_SENDER, TankFederate.ID.getBytes());
            parameters.put(PID_RECEIVER, FighterFederate.ID.getBytes());
            ambs.sendInteraction(CID_COMMUNICATION, parameters, null);
        } catch (Exception e) { System.out.println("error in sending an interaction"); e.printStackTrace();}
    }

    public void close() {
        try {
            ambs.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
            ambs.destroyFederationExecution("FIELD-" + sessionId);
        } catch (Exception e) { System.out.println("error in cleaning up the federate: " + e); }

        try {
            domeConn.close();
        } catch (Exception e) { System.out.println("error in closing DOME connection: " + e);}
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
                        valueMap = ambs.getAttributeHandleValueMapFactory().create(1);
                        valueMap.put(PID_NAME, instanceId.getBytes());
                        valueMap.put(PID_LOCATION, Integer.toString(tankLocation).getBytes());
                        valueMap.put(PID_SHIELD, Integer.toString(tankShield).getBytes());
                        ambs.updateAttributeValues(IID_INSTANCE_ID, valueMap, null);
                    } catch (Exception ignored) { /* ingored */ }
                }
            }
        }.start();
    }
}
