package com.example.smpp_sim;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Hashtable;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.smpp.*;
import org.smpp.debug.*;
import org.smpp.pdu.*;

import org.smpp.pdu.PDUException;
import org.smpp.pdu.tlv.TLVOctets;
import org.smpp.smscsim.util.Table;
import org.smpp.smscsim.PDUProcessorGroup;
import org.smpp.smscsim.DeliveryInfoSender;
//import org.smpp.smscsim.SMSCListenerImpl;

import org.smpp.util.ByteBuffer;
import org.smpp.util.Unprocessed;
import java.util.regex.*;

public class Simulator {


    /**
     * Name of file with user (client) authentication information.
     */
    static String usersFileName = System.getProperty("usersFileName", "etc/users.txt");

    /**
     * Directory for creating of debug and event files.
     */
    static final String dbgDir = "./";

    /**
     * The debug object.
     */
    static Debug debug = new FileDebug(dbgDir, "sim.dbg");

    /**
     * The event object.
     */
    static Event event = new FileEvent(dbgDir, "sim.evt");

    public static final int DSIM = 16;
    public static final int DSIMD = 17;
    public static final int DSIMD2 = 18;

    static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    boolean keepRunning = true;

    private SMSCListener smscListener = null;
    private SimulatorPDUProcessorFactory factory = null;
    private static   PDUProcessorGroup processors = null;
    private ShortMessageStore messageStore = null;
    private DeliveryInfoSender deliveryInfoSender = null;
    private Table users = null;
    private boolean displayInfo = true;
    public static final byte PSSR_IND = 1;
    public static final short ussd_service_op =  0x0501;
//    private static String sourceAddress = null;
//    private static String destinationAddress = null;
    PDUProcessorFactory processorFactory;
    private static Unprocessed unprocessed = new Unprocessed();
//    private static String msg = null;
    private static SMSCSession session;
    private static SMSCSession session1;
    private static SMSCSession session2;
    private static Connection connection;
    private static Session smscsession;
    private  static int ans=0,procCount=0;
private  static  PDU pdu=null;
    private Simulator() {
    }


    public static void main(String args[]) throws IOException {
        SmppObject.setDebug(debug);
        SmppObject.setEvent(event);
        debug.activate();
        event.activate();
        debug.deactivate(SmppObject.DRXTXD2);
        debug.deactivate(SmppObject.DPDUD);
        debug.deactivate(SmppObject.DCOMD);
        debug.deactivate(DSIMD2);
        Simulator s = new Simulator();
        s.start();
//        SMSCSession session = new SMSCSessionImpl(connection);
        int v=1,ans=0,j=0;
        Scanner sc=new Scanner(System.in);
        while ( processors.count()!=2);

//        SimulatorPDUProcessor proc2 = (SimulatorPDUProcessor)processors.get(1);
        PDU pdu1,pdu2;
        SimulatorPDUProcessor proc1 = (SimulatorPDUProcessor)processors.get(0);
        SimulatorPDUProcessor proc2 = (SimulatorPDUProcessor)processors.get(1);
        while (s.keepRunning) {



            while(proc1.getRequest()==null);
            if(proc1.getRequest().getExtraOptional(ussd_service_op)!=null) {
                if (processors.count() == 2 && (proc1.getRequest() != null)) {

//                System.out.println(pdu1+":"+pdu2);
                    pdu = proc1.getRequest();
                    s.extractPDU(pdu);
                    proc1.setRequest(null);

                    while (proc2.getRequest() == null || proc2.getRequest().getExtraOptional(ussd_service_op) == null) ;
                    System.out.println(proc2.getRequest());
                    if (proc2.getRequest().getExtraOptional(ussd_service_op) != null) {
//                        System.out.println(proc2.getResponse());
                        if (processors.count() == 2 && (proc2.getRequest() != null)) {

//                System.out.println(pdu1+":"+pdu2);
                            pdu = proc2.getRequest();
                            s.extractPDU(pdu);
                            proc2.setRequest(null);

                        }
                    }
                }
            }
        }
    }


    protected void start() throws IOException {
        if (smscListener == null) {
//            System.out.print("Enter port number> ");
            int port =2775;// Integer.parseInt(keyboard.readLine());
            System.out.print("Starting listener... ");
            smscListener = new SMSCListenerImpl(port, true);
            processors = new PDUProcessorGroup();
            messageStore = new ShortMessageStore();
            deliveryInfoSender = new DeliveryInfoSender();
            deliveryInfoSender.start();
            users = new Table(usersFileName);
            factory = new SimulatorPDUProcessorFactory(processors, messageStore, deliveryInfoSender, users);
            factory.setDisplayInfo(displayInfo);//displays information of connection
            smscListener.setPDUProcessorFactory(factory);

            connection=smscListener.start();

            System.out.println("started.");

        } else {
            System.out.println("Listener is already running.");
        }

    }



    void extractPDU(PDU pdu) {

    try {
//        pdu = session.getRequest();

//        System.out.println("\n\nExtracted PDU  : \n"+pdu.getExtraOptional(ussd_service_op).debugString()+"\n\n");
        System.out.println("\n\nExtracted PDU  : \n" + pdu.debugString() + "\n\n");
//        System.out.println("\n\nExtracted PDU  : \n"+pdu.getCommandStatus()+"\n"+pdu.getCommandLength()+"\n"+pdu.getBody().getBuffer()+"\n\n");
        String p = pdu.debugString();


        String source = new String();
        String destination = new String();
        String msg = new String();

        int index_source = p.indexOf("addr: ") + 10;
        int index_destination = p.lastIndexOf("addr: ") + 10;
        int index_msg = p.indexOf("msg:") + 4;
        if (p.indexOf("addr: ") != -1 &&p.lastIndexOf("addr: ")!=-1 &&p.indexOf("msg:")!=-1) {
            for (int i = index_source; i < p.length(); i++) {
                if (p.charAt(i) != ')') {
                    source += p.charAt(i);
                } else
                    break;
            }

            for (int i = index_destination; i < p.length(); i++) {
                if (p.charAt(i) != ')') {
                    destination += p.charAt(i);
                } else
                    break;
            }

            for (int i = index_msg; i < p.length(); i++) {
                if (p.charAt(i) != ')') {
                    msg += p.charAt(i);
                } else
                    break;
            }

            System.out.println(source + ":" + destination + ":" + msg);
        }


        sendUSSD(source, destination, msg);

    }
    catch(Exception e){
        System.out.println(e);
    }
}

    protected void sendUSSD(String sourceAddr,String destAddr, String shortMessage) throws IOException {
        if (smscListener != null) {



            if (processors.count()  > 0) {
                System.out.println (processors.count());
                String client;
                SimulatorPDUProcessor proc;


                client=destAddr;
                for (int i = 0; i < processors.count(); i++) {
                    proc = (SimulatorPDUProcessor) processors.get(i);
                    while(proc.getSystemId()==null);
//                    System.out.println(proc.getSystemId()+" "+client);
                    if (proc.getSystemId().equals(client)) {
                        if (proc.isActive()) {
//                            System.out.print("Type the message> ");
//                            String message = keyboard.readLine();
                            String message= shortMessage;
                            try {
                            DeliverSM request = (DeliverSM) pdu;//this.createUSSDRequest(sourceAddr,destAddr,message);
//                            DeliverSM request = this.createUSSDRequest(sourceAddr,destAddr,message);

                                proc.serverRequest(request);
                                System.out.println("Message sent.");
                            } catch (WrongLengthOfStringException e) {
                                System.out.println("Message sending failed");
                                event.write(e, "");
                            } catch (IOException ioe) {
                            } catch (PDUException pe) {
                            }
                        } else {
                            System.out.println("This session is inactive.");
                        }
                    }
                    else{
//                        System.out.println("not this");
                    }
                }
            } else {
                System.out.println("No client connected.");
            }
        } else {
            System.out.println("You must start listener first.");
        }
    }
}