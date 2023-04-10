package com.example.smpp_sim;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



import java.io.IOException;

import org.smpp.*;

import org.smpp.pdu.PDU;
import org.smpp.pdu.PDUException;
import org.smpp.pdu.Request;
import org.smpp.pdu.Response;
import org.smpp.smscsim.PDUProcessor;
import org.smpp.smscsim.PDUProcessorFactory;
import org.smpp.util.ByteBuffer;
import org.smpp.util.NotEnoughDataInByteBufferException;
import org.smpp.util.Unprocessed;

public class SMSCSessionImpl extends SmppObject implements SMSCSession {
    private Receiver receiver;
    private Transmitter transmitter;
    private PDUProcessor pduProcessor;
    private Connection connection;
    private long receiveTimeout = 60000L;
    private boolean keepReceiving = true;
    private boolean isReceiving = false;
    private int timeoutCntr = 0;
    PDU pdu = null;
    private Request request=null;
    private Response response=null;
    private byte messageIncompleteRetryCount = 0;

    public SMSCSessionImpl(Connection connection) {
        this.connection = connection;
        this.transmitter = new Transmitter(connection);
        this.receiver = new Receiver(this.transmitter, connection);
    }

    public void stop() {
        debug.write("SMSCSession stopping");
        this.keepReceiving = false;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void run() {

        debug.enter(this, "SMSCSession run()");
        debug.write("SMSCSession starting receiver");
        this.receiver.start();
        this.isReceiving = true;

        try {
            while (this.keepReceiving) {
                try {
                    debug.write("SMSCSession going to receive a PDU");
                    pdu = this.receiver.receive(this.getReceiveTimeout());
                    setPdu(pdu);
                } catch (Exception var8) {
                    debug.write("SMSCSession caught exception receiving PDU " + var8.getMessage());
                }

                if (pdu != null) {
                    this.timeoutCntr = 0;
                    if (pdu.isRequest()) {
                        debug.write("SMSCSession got request " + pdu.debugString());
                        this.pduProcessor.clientRequest((Request) pdu);
                        setRequest((Request) pdu);

                    } else if (pdu.isResponse()) {
                        debug.write("SMSCSession got response " + pdu.debugString());

                        this.pduProcessor.clientResponse((Response) pdu);
                        setResponse((Response) pdu);
                    } else {
                        debug.write("SMSCSession not reqest nor response => not doing anything.");
                    }
                } else {
                    ++this.timeoutCntr;
                    if (this.timeoutCntr > 5) {
                        debug.write("SMSCSession stoped due to inactivity");
                        this.stop();
                    }
                }
            }
        } finally {
            this.isReceiving = false;
        }

        debug.write("SMSCSession stopping receiver");
        this.receiver.stop();
        debug.write("SMSCSession exiting PDUProcessor");
        this.pduProcessor.exit();

        try {
            debug.write("SMSCSession closing connection");
            this.connection.close();
        } catch (IOException var7) {
            event.write(var7, "closing SMSCSession's connection.");
        }

        debug.write("SMSCSession exiting run()");
        debug.exit(this);
    }

    public void send(PDU pdu) throws IOException, PDUException {
        this.timeoutCntr = 0;
        debug.write("SMSCSession going to send pdu over transmitter");
        this.transmitter.send(pdu);
        debug.write("SMSCSession pdu sent over transmitter");
    }

    public void setPDUProcessor(PDUProcessor pduProcessor) {
        this.pduProcessor = pduProcessor;
    }

    public void setPDUProcessorFactory(PDUProcessorFactory pduProcessorFactory) {
    }


    public void setReceiveTimeout(long timeout) {
        this.receiveTimeout = timeout;
    }

    public long getReceiveTimeout() {
        return this.receiveTimeout;
    }

    public Object getAccount() {
        return null;
    }

    public void setAccount(Object account) {
    }

    public boolean isReceiving() {
        return this.isReceiving;
    }

    public void setReceiving(boolean isReceiving) {
        this.isReceiving = isReceiving;
    }

    public PDU getPdu() {
        return pdu;
    }

    public void setPdu(PDU pdu) {
        this.pdu = pdu;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
//
//    public final PDU receivePDUFromConnection(Connection connection, Unprocessed unprocessed) {
//        debug.write(3, "ReceiverBase.receivePDUFromConnection start");
//        PDU pdu = null;
//
//        try {
//            ByteBuffer unprocBuffer;
//            if (unprocessed.getHasUnprocessed()) {
//                unprocBuffer = unprocessed.getUnprocessed();
//                debug.write(1, "have unprocessed " + unprocBuffer.length() + " bytes from previous try");
//                pdu = this.tryGetUnprocessedPDU(unprocessed);
//            }
//
//            if (pdu == null) {
//                ByteBuffer buffer = connection.receive();
//                unprocBuffer = unprocessed.getUnprocessed();
//                if (buffer.length() != 0) {
//                    unprocBuffer.appendBuffer(buffer);
//                    unprocessed.setLastTimeReceived();
//                    pdu = this.tryGetUnprocessedPDU(unprocessed);
//                } else {
//                    debug.write(3, "no data received this time.");
//                    long timeout = this.getReceiveTimeout();
//                    if (unprocBuffer.length() > 0 && unprocessed.getLastTimeReceived() + timeout < Data.getCurrentTime()) {
//                        debug.write(1, "and it's been very long time.");
//                        unprocessed.reset();
//                        throw new TimeoutException(timeout, unprocessed.getExpected(), unprocBuffer.length());
//                    }
//                }
//            }
//        } catch (Exception var8) {
//            event.write(var8, "There is _probably_ garbage in the unprocessed buffer - flushing unprocessed buffer now.");
//            unprocessed.reset();
//        }
//
//        debug.write(3, "ReceiverBase.receivePDUFromConnection finished");
//        return pdu;
//    }
//
//
//    private final PDU tryGetUnprocessedPDU(Unprocessed unprocessed) throws UnknownCommandIdException, PDUException {
//        debug.write(1, "trying to create pdu from unprocessed buffer");
//        PDU pdu = null;
//        ByteBuffer unprocBuffer = unprocessed.getUnprocessed();
//
//        try {
//            pdu = PDU.createPDU(unprocBuffer);
//            unprocessed.check();
//            this.messageIncompleteRetryCount = 0;
//        } catch (HeaderIncompleteException var7) {
//            debug.write(2, "incomplete message header, will wait for the rest.");
//            unprocessed.setHasUnprocessed(false);
//            unprocessed.setExpected(16);
//        } catch (MessageIncompleteException var8) {
//            if (this.messageIncompleteRetryCount > 5) {
//                this.messageIncompleteRetryCount = 0;
//                event.write("Giving up on incomplete messages - probably garbage in unprocessed buffer. Flushing unprocessed buffer.");
//                unprocessed.reset();
//            }
//
//            debug.write(2, "incomplete message, will wait for the rest.");
//            unprocessed.setHasUnprocessed(false);
//            unprocessed.setExpected(16);
//            ++this.messageIncompleteRetryCount;
//        } catch (UnknownCommandIdException var9) {
//            UnknownCommandIdException e = var9;
//            debug.write(1, "unknown pdu, might remove from unprocessed buffer. CommandId=" + var9.getCommandId());
//            if (var9.getCommandLength() <= unprocBuffer.length()) {
//                try {
//                    unprocBuffer.removeBytes(e.getCommandLength());
//                } catch (NotEnoughDataInByteBufferException var6) {
//                    throw new Error("Not enough data in buffer even if previously checked that there was enough.");
//                }
//
//                unprocessed.check();
//                throw var9;
//            }
//
//            throw var9;
//        } catch (PDUException var10) {
//            unprocessed.check();
//            throw var10;
//        }
//
//        if (pdu != null) {
//            debug.write(1, "received complete pdu" + pdu.debugString());
//            debug.write(1, "there is " + unprocBuffer.length() + " bytes left in unprocessed buffer");
//        }
//
//        return pdu;
//    }
//}
//
