/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.faulttolerance.Fallback;

import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

@RequestScoped
public class FallbackBeanWithoutRetry {

    private int connectCountA = 0;

    @Fallback(MyFallbackHandler.class)
    public Connection connectA() throws ConnectException {
        throw new ConnectException("FallbackBean.connectA: " + (++connectCountA));
    }

    // Overridden as MyFallbackHandler2 in config
    @Fallback(MyFallbackHandler.class)
    public Connection connectB() throws ConnectException {
        throw new ConnectException("FallbackBean.connectB");
    }

    // Overridden as connectFallback2 in config
    @Fallback(fallbackMethod = "connectFallback")
    public Connection connectC() throws ConnectException {
        throw new ConnectException("FallbackBean.connectC");
    }

    public Connection connectFallback() {
        return new Connection() {
            @Override
            public String getData() {
                return "connectFallback";
            }
        };
    }

    public Connection connectFallback2() {
        return new Connection() {
            @Override
            public String getData() {
                return "connectFallback2";
            }
        };
    }

    public int getConnectCountA() {
        return connectCountA;
    }

}
