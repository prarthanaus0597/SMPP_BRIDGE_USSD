package com.example.simplereciever;
import org.smpp.*;
import org.smpp.pdu.*;
import org.smpp.pdu.tlv.TLVOctets;
import org.smpp.util.ByteBuffer;
;
import java.io.IOException;

public class SMPPGateway {

    /**
     * Parameters used for connecting to SMSC (or  SMPPSim)
     */
    private Session session = null;
    private String ipAddress = "172.16.144.189";
    private String systemId = "gateway";
    private String password = "gateway";
    private int port = 2775;
    public static final short ussd_service_op =  0x0501;
    public static final byte USSR_REQ = 2;
    private int ans=0;
    public static  String input = "0";
    public static  String msg = "NA";
    private String destinationAddress ="mobilestation";
    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Sms receiver starts");

        SMPPGateway objSMPPGateway = new SMPPGateway();
        objSMPPGateway.bindToSmsc();

        while(true) {
           objSMPPGateway.receiveUSSD();

//            String msg =objSimpleSMSReceiver.receiveSms();
//            System.out.println("Sms receiving");
            // String msg= processMessage(msg) --- response msg is stored in msg
//        sendSingleSMS(msg)
            if(objSMPPGateway.ans==1) {
//                System.out.println(objSimpleSMSReceiver.ans);
                try {
                    Response resp = objSMPPGateway.InitUSSDSession(objSMPPGateway.destinationAddress);
                    objSMPPGateway.ans=0;
                    if(resp!=null && msg=="Thank you choosing ABC Bank"){
                        System.exit(0);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        }
    }

    private void bindToSmsc() {
        try {
            // setup connection
            TCPIPConnection connection = new TCPIPConnection(ipAddress, port);
            connection.setReceiveTimeout(20 * 1000);
            session = new Session(connection);

            // set request parameters
            BindRequest request = new BindTransciever();
            request.setSystemId(systemId);
            request.setPassword(password);

            // send request to bind
            BindResponse response = session.bind(request);
            if (response.getCommandStatus() == Data.ESME_ROK) {
                System.out.println("Sms receiver is connected to SMPPSim.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveUSSD() {
        try {

            PDU pdu = session.receive(1500);

            if (pdu != null) {
                DeliverSM sms = (DeliverSM) pdu;

                System.out.println("From: " + sms.getServiceType());
//                System.out.println("To: " + sms.getDestAddr());

                if ((int)sms.getDataCoding() == 0 ) {
                    //message content is English
                    System.out.println("***** New Message Received *****");
                    System.out.println("From: " + sms.getSourceAddr().getAddress());
                    System.out.println("To: " + sms.getDestAddr().getAddress());
                    System.out.println("Content: " + sms.getShortMessage());
                }
                input=sms.getShortMessage();
                switch(input){
                  case  "*123#": msg="\nPlease select one of the below options to process -->\n" +

                          "1. View Account Details\n"+
                          "2. Check Account Balance \n"+
                          "3. Last Transaction \n"+
                          "4. Exit\n>";
                    break;
                    case  "1":  msg="\nAccount Holder Name: Tom Jackson\n" +
                            "Account Number : 10243278910\n" +
                            "Registered Mobile No. : 844867XXXX \n" +

                            "\nPlease select one of the below options to process -->\n" +

                        "1. View Account Details\n"+
                                "2. Check Account Balance \n"+
                                "3. Last Transaction \n"+
                                "4. Exit\n>";
                        break;
                    case  "2":  msg="\nYour Account Balance is Rs.1,20,108 \n " +

                            "\nPlease select one of the below options to process -->\n" +

                            "1. View Account Details\n"+
                            "2. Check Account Balance \n"+
                            "3. Last Transaction \n"+
                            "4. Exit\n>";
                        break;

                    case  "3":  msg="\nBelow is your last transaction Detail:\n" +
                            "[Debit]- 10,April,2023 - 1000Rs - ATM Withrawl\n " +

                            "\nPlease select one of the below options to process -->\n" +

                            "1. View Account Details\n"+
                            "2. Check Account Balance \n"+
                            "3. Last Transaction \n"+
                            "4. Exit\n>";
                        break;



                    default:  msg="Thank you choosing ABC Bank";
                    break;
                }
                ans=1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    DeliverSM createUSSDResponse(String phoneNumber) throws WrongLengthOfStringException{
        DeliverSM request = new DeliverSM();
        request.setCommandId( Data.DELIVER_SM);
        request.setServiceType(Data.SERVICE_USSD);
        request.assignSequenceNumber(true);
        Address srcAddr = new Address(systemId);
        request.setSourceAddr(srcAddr);
        Address destAddr = new Address((byte)1,(byte)1,phoneNumber);
        request.setDestAddr(destAddr);
        TLVOctets tlv =  new TLVOctets();
        tlv.setTag(ussd_service_op);
        tlv.setValue(new ByteBuffer(new byte[]{USSR_REQ}));
        request.setExtraOptional(tlv);
        request.setShortMessage(msg);
        return request;
    }
    public DeliverSMResp InitUSSDSession(String phoneNumber) throws ValueNotSetException, TimeoutException, PDUException, WrongSessionStateException, IOException {
        DeliverSM request = createUSSDResponse(phoneNumber);
        System.out.println( "USSDResponse"+request.debugString());
        DeliverSMResp response = session.deliver(request);
        System.out.println(response.getData().getBuffer());
        if (response.getCommandStatus() == Data.ESME_ROK) {
            System.out.println("USSD submitted....");
        }
        return response;
    }

}
