package com.example.simplesender;
import org.smpp.*;
import org.smpp.pdu.*;
import org.smpp.pdu.tlv.TLVOctets;
import org.smpp.util.ByteBuffer;

import java.io.IOException;
import java.util.*;

public class SMPPMobileStation {
    //Mobile initiated
    private Session session = null;
    private String ipAddress = "172.16.144.189";
    private String systemId = "mobilestation";
    private String password = "mobile";
    private int port = 2775;
    private String shortMessage = "abcdefg";
    private String sourceAddress = "mobilestation";
    private String destinationAddress = "gateway";

    public static final short ussd_service_op =  0x0501;

    public static final byte PSSR_IND = 1;


    public static void main(String[] args) {
    try {
        SMPPMobileStation objSMPPMobileStation = new SMPPMobileStation();
        objSMPPMobileStation.bindToSMSC();

        String msg;
        while(true) {
        Scanner sc=new Scanner(System.in);
            msg=sc.next();


            Response resp = objSMPPMobileStation.InitUSSDSession(objSMPPMobileStation.destinationAddress,msg);
//            if((msg!="1")&&(msg!="2")&&(msg!="3")&&(msg!="123"))
//            {
//                System.exit(0);
//            }
//            if((msg=="1")||(msg=="2")||(msg=="3")||(msg=="5")) {
                objSMPPMobileStation.receiveUSSD();
//            }
//            else{
//                System.exit(0);
//            }

        }

//            Request request = objSimpleSMSTransmitter.receive();
//            if(request != null){
//                System.out.println("Request:" + request.debugString());
//
////                System.out.println();
//
//                if( !objSimpleSMSTransmitter.processLink(request)){
//                    break;
//                }
//            }
//        }

//            System.out.println("Program terminated");
    }
    catch (Exception e){
        System.out.println(e);
    }
        System.exit(0);
    }

    public void bindToSMSC() {
        try {
            Connection conn = new TCPIPConnection(ipAddress, port);
            session = new Session(conn);

            BindRequest breq = new BindTransciever();
            breq.setSystemId(systemId);
            breq.setPassword(password);
            breq.setInterfaceVersion((byte) 0x34);

            BindResponse bresp = (BindResponse) session.bind(breq);

            if(bresp.getCommandStatus() == Data.ESME_ROK) {
                System.out.print("Connected to SMSC.\nEnter the USSD code:\n> ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    DeliverSM createUSSDRequest(String phoneNumber,String msg) throws WrongLengthOfStringException{
        DeliverSM request = new DeliverSM();
        request.setCommandId( Data.DELIVER_SM);
        request.setServiceType(Data.SERVICE_USSD);
        request.assignSequenceNumber(true);
        Address srcAddr = new Address(sourceAddress);
        request.setSourceAddr(srcAddr);
        Address destAddr = new Address((byte)1,(byte)1,phoneNumber);
        request.setDestAddr(destAddr);
        TLVOctets tlv =  new TLVOctets();
        tlv.setTag(ussd_service_op);
        tlv.setValue(new ByteBuffer(new byte[]{PSSR_IND}));
        request.setExtraOptional(tlv);
        request.setShortMessage(msg);
        return request;
    }
    public DeliverSMResp InitUSSDSession(String phoneNumber,String msg) throws ValueNotSetException, TimeoutException, PDUException, WrongSessionStateException, IOException {
        DeliverSM request = createUSSDRequest(phoneNumber,msg);
//        System.out.println( "USSDRequest"+request.debugString());
        DeliverSMResp response = session.deliver(request);
//        System.out.println(response.getData().getBuffer());
//        if (response.getCommandStatus() == Data.ESME_ROK) {
//            System.out.println("USSD submitted....");
//        }
        return response;
    }



    private void receiveUSSD() {
        try {

            PDU pdu = session.receive(1500);

            if (pdu != null) {
                DeliverSM sms = (DeliverSM) pdu;

//                System.out.println("Service Type: " + sms.getServiceType());
//                System.out.println("To: " + sms.getDestAddr());

                if ((int)sms.getDataCoding() == 0 ) {
                    //message content is English
                    System.out.println("***** New Message Received *****");
                    System.out.println("From: " + sms.getSourceAddr().getAddress());
                    System.out.println("To: " + sms.getDestAddr().getAddress());
                    System.out.print("\n" + sms.getShortMessage());

                if(sms.getShortMessage().equals("Thank you choosing ABC Bank")){
                 System.exit(0);
                }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
