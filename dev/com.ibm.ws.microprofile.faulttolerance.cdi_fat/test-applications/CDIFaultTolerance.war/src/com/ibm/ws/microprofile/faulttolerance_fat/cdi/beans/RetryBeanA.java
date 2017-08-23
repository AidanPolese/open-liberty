package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import org.eclipse.microprofile.faulttolerance.Retry;

import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;
import com.ibm.ws.microprofile.faulttolerance_fat.util.DisconnectException;

public class RetryBeanA {

    private int connectCount = 0;
    private int disconnectCount = 0;

    public Connection connectA() throws ConnectException {
        throw new ConnectException("RetryBeanA Connect: " + (++connectCount));
    }

    @Retry(maxRetries = 4)
    public void disconnectA() throws DisconnectException {
        throw new DisconnectException("RetryBeanA Disconnect: " + (++disconnectCount));
    }
}
