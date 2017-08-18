package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;

import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

/**
 * Servlet implementation class Test
 */
@RequestScoped
@Retry(maxRetries = 2)
@Fallback(MyFallbackHandler.class)
public class FallbackBean {

    private int connectCountA = 0;
    private int connectCountB = 0;

    public Connection connectA() throws ConnectException {
        throw new ConnectException("FallbackBean.connectA: " + (++connectCountA));
    }

    public Connection connectB(String param) throws ConnectException {
        throw new ConnectException("FallbackBean.connectB: " + (++connectCountB));
    }

    public Connection fallback(ExecutionContext executionContext) {
        return new Connection() {

            @Override
            public String getData() {
                return "Fallback Connection: " + executionContext.getMethod().getName();
            }
        };
    }

}
