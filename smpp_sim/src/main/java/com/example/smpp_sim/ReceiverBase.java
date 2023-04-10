package com.example.smpp_sim;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.io.IOException;

import org.smpp.Connection;
import org.smpp.Data;
import org.smpp.TimeoutException;
import org.smpp.pdu.HeaderIncompleteException;
import org.smpp.pdu.MessageIncompleteException;
import org.smpp.pdu.PDU;
import org.smpp.pdu.PDUException;
import org.smpp.pdu.UnknownCommandIdException;
import org.smpp.util.ByteBuffer;
import org.smpp.util.NotEnoughDataInByteBufferException;
import org.smpp.util.ProcessingThread;
import org.smpp.util.Unprocessed;

public abstract class ReceiverBase extends ProcessingThread {
    private long receiveTimeout = 60000L;
    private byte messageIncompleteRetryCount = 0;

    public ReceiverBase() {
    }

    protected abstract void receiveAsync();

    protected abstract PDU tryReceivePDU(Connection var1, PDU var2) throws UnknownCommandIdException, TimeoutException, PDUException, IOException;

    public void process() {
        this.receiveAsync();
    }

    protected final PDU tryReceivePDUWithTimeout(Connection connection, PDU expectedPDU) throws UnknownCommandIdException, TimeoutException, PDUException, IOException {
        return this.tryReceivePDUWithTimeout(connection, expectedPDU, this.getReceiveTimeout());
    }

    protected final PDU tryReceivePDUWithTimeout(Connection connection, PDU expectedPDU, long timeout) throws UnknownCommandIdException, TimeoutException, PDUException, IOException {
        debug.write(1, "receivePDU: Going to receive response.");
        long startTime = Data.getCurrentTime();
        PDU pdu = null;
        if (timeout == 0L) {
            pdu = this.tryReceivePDU(connection, expectedPDU);
        } else {
            while(pdu == null && this.canContinueReceiving(startTime, timeout)) {
                pdu = this.tryReceivePDU(connection, expectedPDU);
            }
        }

        if (pdu != null) {
            debug.write(1, "Got pdu " + pdu.debugString());
        }

        return pdu;
    }

    public final PDU receivePDUFromConnection(Connection connection, Unprocessed unprocessed) throws UnknownCommandIdException, TimeoutException, PDUException, IOException {
        debug.write(3, "ReceiverBase.receivePDUFromConnection start");
        PDU pdu = null;

        try {
            ByteBuffer unprocBuffer;
            if (unprocessed.getHasUnprocessed()) {
                unprocBuffer = unprocessed.getUnprocessed();
                debug.write(1, "have unprocessed " + unprocBuffer.length() + " bytes from previous try");
                pdu = this.tryGetUnprocessedPDU(unprocessed);
            }

            if (pdu == null) {
                ByteBuffer buffer = connection.receive();
                unprocBuffer = unprocessed.getUnprocessed();
                if (buffer.length() != 0) {
                    unprocBuffer.appendBuffer(buffer);
                    unprocessed.setLastTimeReceived();
                    pdu = this.tryGetUnprocessedPDU(unprocessed);
                } else {
                    debug.write(3, "no data received this time.");
                    long timeout = this.getReceiveTimeout();
                    if (unprocBuffer.length() > 0 && unprocessed.getLastTimeReceived() + timeout < Data.getCurrentTime()) {
                        debug.write(1, "and it's been very long time.");
                        unprocessed.reset();
                        throw new TimeoutException(timeout, unprocessed.getExpected(), unprocBuffer.length());
                    }
                }
            }
        } catch (UnknownCommandIdException var8) {
            event.write(var8, "There is _probably_ garbage in the unprocessed buffer - flushing unprocessed buffer now.");
            unprocessed.reset();
        }

        debug.write(3, "ReceiverBase.receivePDUFromConnection finished");
        return pdu;
    }

    private final PDU tryGetUnprocessedPDU(Unprocessed unprocessed) throws UnknownCommandIdException, PDUException {
        debug.write(1, "trying to create pdu from unprocessed buffer");
        PDU pdu = null;
        ByteBuffer unprocBuffer = unprocessed.getUnprocessed();

        try {
            pdu = PDU.createPDU(unprocBuffer);
            unprocessed.check();
            this.messageIncompleteRetryCount = 0;
        } catch (HeaderIncompleteException var7) {
            debug.write(2, "incomplete message header, will wait for the rest.");
            unprocessed.setHasUnprocessed(false);
            unprocessed.setExpected(16);
        } catch (MessageIncompleteException var8) {
            if (this.messageIncompleteRetryCount > 5) {
                this.messageIncompleteRetryCount = 0;
                event.write("Giving up on incomplete messages - probably garbage in unprocessed buffer. Flushing unprocessed buffer.");
                unprocessed.reset();
            }

            debug.write(2, "incomplete message, will wait for the rest.");
            unprocessed.setHasUnprocessed(false);
            unprocessed.setExpected(16);
            ++this.messageIncompleteRetryCount;
        } catch (UnknownCommandIdException var9) {
            UnknownCommandIdException e = var9;
            debug.write(1, "unknown pdu, might remove from unprocessed buffer. CommandId=" + var9.getCommandId());
            if (var9.getCommandLength() <= unprocBuffer.length()) {
                try {
                    unprocBuffer.removeBytes(e.getCommandLength());
                } catch (NotEnoughDataInByteBufferException var6) {
                    throw new Error("Not enough data in buffer even if previously checked that there was enough.");
                }

                unprocessed.check();
                throw var9;
            }

            throw var9;
        } catch (PDUException var10) {
            unprocessed.check();
            throw var10;
        }

        if (pdu != null) {
            debug.write(1, "received complete pdu" + pdu.debugString());
            debug.write(1, "there is " + unprocBuffer.length() + " bytes left in unprocessed buffer");
        }

        return pdu;
    }

    public void setReceiveTimeout(long timeout) {
        this.receiveTimeout = timeout;
    }

    public long getReceiveTimeout() {
        return this.receiveTimeout;
    }

    private boolean canContinueReceiving(long startTime, long timeout) {
        return timeout == -1L ? true : Data.getCurrentTime() <= startTime + timeout;
    }
}

