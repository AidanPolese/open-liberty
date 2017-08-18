package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.faulttolerance.Timeout;

import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

@RequestScoped
public class TimeoutBean {

    @Timeout
    public Connection connectA() throws ConnectException {
        try {
            Thread.sleep(20000);
            throw new ConnectException("Timeout did not interrupt");
        } catch (InterruptedException e) {
            //expected
            System.out.println("TimeoutBean Interrupted");
        }
        return null;

    }

    @Timeout
    public Connection connectB() throws ConnectException {
        throw new ConnectException("A simple exception");
    }
}
