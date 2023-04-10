package com.example.smpp_sim;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.io.IOException;
import org.smpp.Connection;

import org.smpp.pdu.PDU;
import org.smpp.pdu.PDUException;
import org.smpp.pdu.Request;
import org.smpp.pdu.Response;
import org.smpp.smscsim.PDUProcessor;
import org.smpp.smscsim.PDUProcessorFactory;
import org.smpp.util.Unprocessed;

public interface SMSCSession extends Runnable {
    void stop();

    void run();

    void send(PDU var1) throws IOException, PDUException;

    void setPDUProcessor(PDUProcessor var1);
//    PDUProcessor getPDUProcessor();
    void setPDUProcessorFactory(PDUProcessorFactory var1);

    void setReceiveTimeout(long var1);

    long getReceiveTimeout();

    Object getAccount();

    void setAccount(Object var1);

    Connection getConnection();
    public void setPdu(PDU pdu);
    public PDU getPdu();
//    public  PDU receivePDUFromConnection(Connection connection, Unprocessed unprocessed);

    public void setRequest(Request request);
    public Request getRequest();
    public Response getResponse();

    public void setResponse(Response response);


}
