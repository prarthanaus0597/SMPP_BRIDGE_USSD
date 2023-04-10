package com.example.smpp_sim;


import org.smpp.Connection;

import java.io.IOException;

public interface SMSCListener {
    Connection start() throws IOException;

    void stop() throws IOException;

    void run();

    void setPDUProcessorFactory(PDUProcessorFactory var1);

    void setAcceptTimeout(int var1);

    long getAcceptTimeout();
}
