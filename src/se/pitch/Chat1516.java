package se.pitch;

import hla.rti1516.*;
import se.pitch.prti1516.FederateAmbassadorImpl;
import se.pitch.prti1516.RTI;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Iterator;

class Chat extends FederateAmbassadorImpl
{
   private RTIambassador _rtiAmbassador;
   private final String[] _args;
   private InteractionClassHandle _messageId;
   private ParameterHandle _parameterIdText;
   private ParameterHandle _parameterIdSender;
   private ObjectClassHandle _participantId;
   private ObjectInstanceHandle _userId;
   private AttributeHandle _attributeIdName;
   private String _username;

   private boolean _reservationSucceeded;
   private Object _reservationSemaphore = new Object();

   private static final int CRC_PORT = 8989;

   public static void main(String[] args)
   {
      new Chat(args).run();
   }

   private Chat(String[] args)
   {
      _args = args;
   }

   private void run()
   {
      try {
         BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

         String rtiHost = "";

         if(_args.length > 0) {
            rtiHost = _args[0];
         } else {
            System.out.print("Enter the IP address of the CRC host [localhost]: ");
            rtiHost = in.readLine();
            if(rtiHost.length() == 0) {
               rtiHost = "localhost";
            }
         }

         try {
            _rtiAmbassador = RTI.getRTIambassador(rtiHost, CRC_PORT);
         } catch (Exception e) {
            System.out.println("Unable to connect to CRC on " + rtiHost + ":" + CRC_PORT);
            return;
         }

         try {
            _rtiAmbassador.destroyFederationExecution("ChatRoom");
         } catch (FederatesCurrentlyJoined ignored) {
         } catch (FederationExecutionDoesNotExist ignored) {
         }

         try {
            final File fddFile;
            fddFile = new File("Chat.xml");
            _rtiAmbassador.createFederationExecution("ChatRoom", fddFile.toURL());
         } catch (FederationExecutionAlreadyExists ignored) {
         }

         _rtiAmbassador.joinFederationExecution("Chat", "ChatRoom", this, null);

         // Subscribe and publish interactions
         _messageId = _rtiAmbassador.getInteractionClassHandle("Communication");
         _parameterIdText = _rtiAmbassador.getParameterHandle(_messageId, "Message");
         _parameterIdSender = _rtiAmbassador.getParameterHandle(_messageId, "Sender");

         _rtiAmbassador.subscribeInteractionClass(_messageId);
         _rtiAmbassador.publishInteractionClass(_messageId);

         // Subscribe and publish objects
         _participantId = _rtiAmbassador.getObjectClassHandle("Participant");
         _attributeIdName = _rtiAmbassador.getAttributeHandle(_participantId, "Name");

         AttributeHandleSet _attributeSet;
         _attributeSet = _rtiAmbassador.getAttributeHandleSetFactory().create();
         _attributeSet.add(_attributeIdName);

         _rtiAmbassador.subscribeObjectClassAttributes(_participantId, _attributeSet);
         _rtiAmbassador.publishObjectClassAttributes(_participantId, _attributeSet);

         // Reserve object instance name and register object instance
         do {
            System.out.print("Enter your name: ");
            _username = in.readLine();
            //_username += "\0";

            try {
               synchronized (_reservationSemaphore) {
                  _rtiAmbassador.reserveObjectInstanceName(_username);
                  // Wait for response from RTI
                  waitForReservation();
               }
               if(_reservationSucceeded)
                  _userId = _rtiAmbassador.registerObjectInstance(_participantId, _username);
               else
                  System.out.println("Name already taken, try again.");
            } catch(RTIexception ignored) {
            }
         } while(!_reservationSucceeded);

         AttributeHandleValueMap _attributeValues;
         _attributeValues = _rtiAmbassador.getAttributeHandleValueMapFactory().create(1);
         _attributeValues.put(_attributeIdName, _username.getBytes());
         _rtiAmbassador.updateAttributeValues(_userId, _attributeValues, null);

         _username += "\0";

         // Find out who else is joined
         _rtiAmbassador.requestAttributeValueUpdate(_participantId, _attributeSet, null);

         System.out.println("Type messages you want to send. To exit, type . <ENTER>");
         while (true) {
            System.out.print("> ");
            String _message = in.readLine();

            if (_message.equals(".")) {
               break;
            }
            _message += "\0";
            ParameterHandleValueMap parameters = _rtiAmbassador.getParameterHandleValueMapFactory().create(1);

            parameters.put(_parameterIdText, _message.getBytes());
            parameters.put(_parameterIdSender, _username.getBytes());

            _rtiAmbassador.sendInteraction(_messageId, parameters, null);
         }

         _rtiAmbassador.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
         try {
            _rtiAmbassador.destroyFederationExecution("ChatRoom");
         } catch (FederatesCurrentlyJoined ignored) {
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void waitForReservation() {
      try {
         synchronized (_reservationSemaphore) {
            _reservationSemaphore.wait();
         }
      } catch (InterruptedException ignored) {
      }
   }

   public final void receiveInteraction(
      InteractionClassHandle _theInteraction,
      ParameterHandleValueMap _theParameters,
      byte[] _theUserSuppliedTag,
      OrderType _theOrderType,
      TransportationType _theTransport)
   {
      if (_theInteraction.equals(_messageId)) {
         String _message = "";
         String _sender = "";
         for (Iterator i = _theParameters.keySet().iterator(); i.hasNext();) {
            ParameterHandle parameterHandle = (ParameterHandle) i.next();
            if (parameterHandle.equals(_parameterIdText)) {
               _message = new String((byte[]) _theParameters.get(parameterHandle));
            }
            else if(parameterHandle.equals(_parameterIdSender)) {
               _sender = new String((byte[]) _theParameters.get(parameterHandle));
            }
         }
         // Since strings sent between federates are null terminated,
         // we don't want to print the null character.
         System.out.println(_sender.substring(0,_sender.length() -1) + ": " + _message.substring(0,_message.length()-1));
         System.out.print("> ");
      }
   }

   public final void objectInstanceNameReservationSucceeded(String _theObject) {
      _reservationSucceeded = true;
      synchronized (_reservationSemaphore) {
         _reservationSemaphore.notify();
      }
   }

   public final void objectInstanceNameReservationFailed(String _theObject) {
      _reservationSucceeded = false;
      synchronized (_reservationSemaphore) {
         _reservationSemaphore.notify();
      }
   }

   public final void discoverObjectInstance(
      final ObjectInstanceHandle _theInstance,
      final ObjectClassHandle _theObject,
      final String _theInstanceName) {
      new Thread() {
         public void run() {
            try {
               if(_theObject.equals(_participantId)) {
                  AttributeHandleSet _attributes = _rtiAmbassador.getAttributeHandleSetFactory().create();
                  _attributes.add(_attributeIdName);
                  _rtiAmbassador.requestAttributeValueUpdate(_theInstance, _attributes, null);
               }
            } catch(Exception ignored) {
            }
         }
      }.start();
   }

   public final void reflectAttributeValues(
      final ObjectInstanceHandle _theInstance,
      final AttributeHandleValueMap _theAttributes,
      final byte[] _theUserSuppliedTag,
      final OrderType _theOrderType,
      final TransportationType _theTransport) {
      new Thread() {
         public void run() {
            if(_theAttributes.containsKey(_attributeIdName)) {
               String _newMember = new String((byte[])_theAttributes.get(_attributeIdName));
               System.out.println(_newMember + " has joined.");
               System.out.print("> ");
            }
         }
      }.start();
   }

   public final void provideAttributeValueUpdate(
      final ObjectInstanceHandle _theObject,
      final AttributeHandleSet _theAttributes,
      final byte[] _theUserSuppliedTag) {
      new Thread() {
         public void run() {
            if(_theAttributes.contains(_attributeIdName)) {
               try {
                  AttributeHandleValueMap _attributeValues;
                  _attributeValues = _rtiAmbassador.getAttributeHandleValueMapFactory().create(1);
                  _attributeValues.put(_attributeIdName, _username.getBytes());
                  _rtiAmbassador.updateAttributeValues(_userId, _attributeValues, null);
               } catch (Exception ignored) {
               }
            }
         }
      }.start();
   }
}
