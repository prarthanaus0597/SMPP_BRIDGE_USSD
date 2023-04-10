package com.example.smpp_sim;


import java.io.IOException;
import java.io.InterruptedIOException;
import org.smpp.Connection;
import org.smpp.SmppObject;
import org.smpp.TCPIPConnection;
import org.smpp.smscsim.*;

public class SMSCListenerImpl extends SmppObject implements Runnable, SMSCListener {
    private Connection serverConn = null;
    private int port;
    private long acceptTimeout = 60000L;
    private PDUProcessorFactory processorFactory = null;
    private boolean keepReceiving = true;
    private boolean isReceiving = false;
    private boolean asynchronous = false;
    private SMSCSession session =null;
    public SMSCListenerImpl(int port) {
        this.port = port;
    }

    public SMSCListenerImpl(int port, boolean asynchronous) {
        this.port = port;
        this.asynchronous = asynchronous;
    }


    public synchronized Connection start() throws IOException {
        debug.write("going to start SMSCListener on port " + this.port);
        if (!this.isReceiving) {
            this.serverConn = new TCPIPConnection(this.port);
            this.serverConn.setReceiveTimeout(this.getAcceptTimeout());
            this.serverConn.open();
            this.keepReceiving = true;
            if (this.asynchronous) {
                debug.write("starting listener in separate thread.");
                Thread serverThread = new Thread(this);
                serverThread.start();
                debug.write("listener started in separate thread.");
            } else {
                debug.write("going to listen in the context of current thread.");
                this.run();
            }
        } else {
            debug.write("already receiving, not starting the listener.");
        }
        return this.serverConn;
    }

    public synchronized void stop() throws IOException {
        debug.write("going to stop SMSCListener on port " + this.port);
        this.keepReceiving = false;

        while(this.isReceiving) {
            Thread.yield();
        }

        this.serverConn.close();
        debug.write("SMSCListener stopped on port " + this.port);
    }

    public void run() {
        debug.enter(this, "run of SMSCListener on port " + this.port);
        this.isReceiving = true;

        try {
            while(this.keepReceiving) {
                this.listen();
                Thread.yield();
            }
        } finally {
            this.isReceiving = false;
        }

        debug.exit(this);
    }

    private void listen() {
        debug.enter(18, this, "SMSCListener listening on port " + this.port);

        try {
            Connection connection = null;
            this.serverConn.setReceiveTimeout(this.getAcceptTimeout());
            connection = this.serverConn.accept();
            if (connection != null) {
                debug.write("SMSCListener accepted a connection on port " + this.port);
                SMSCSession session = new SMSCSessionImpl(connection);
                PDUProcessor pduProcessor = null;
                if (this.processorFactory != null) {
                    pduProcessor = this.processorFactory.createPDUProcessor(session);
                }
                session.setPDUProcessor(pduProcessor);
                this.session=session;
                Thread thread = new Thread(session);
                thread.start();
                debug.write("SMSCListener launched a session on the accepted connection.");

            } else {
                debug.write(18, "no connection accepted this time.");
            }
        } catch (InterruptedIOException var5) {
            debug.write("InterruptedIOException accepting, timeout? -> " + var5);
        } catch (IOException var6) {
            event.write(var6, "IOException accepting connection");
            this.keepReceiving = false;
        }
        debug.exit(18, this);

    }

    public void setPDUProcessorFactory(PDUProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    public void setAcceptTimeout(int value) {
        this.acceptTimeout = (long)value;
    }

    public long getAcceptTimeout() {
        return this.acceptTimeout;
    }
}
