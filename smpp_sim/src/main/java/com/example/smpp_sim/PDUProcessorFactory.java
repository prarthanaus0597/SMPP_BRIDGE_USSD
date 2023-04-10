package com.example.smpp_sim;

import org.smpp.smscsim.PDUProcessor;

public interface PDUProcessorFactory {
    PDUProcessor createPDUProcessor(SMSCSession var1);
}
