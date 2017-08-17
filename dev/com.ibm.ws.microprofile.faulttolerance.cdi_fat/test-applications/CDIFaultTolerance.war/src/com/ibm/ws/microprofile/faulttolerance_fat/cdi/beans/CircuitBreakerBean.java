package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import java.time.temporal.ChronoUnit;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;

@RequestScoped
public class CircuitBreakerBean {

    private int executionCounterA = 0;
    private int executionCounterB = 0;

    @CircuitBreaker(delay = 1, delayUnit = ChronoUnit.SECONDS, requestVolumeThreshold = 3, failureRatio = 1.0)
    @Timeout(value = 3, unit = ChronoUnit.SECONDS)
    public String serviceA() {
        executionCounterA++;
        System.out.println("serviceA: " + executionCounterA);

        if (executionCounterA <= 3) {
            //Sleep for 10 secs to force a timeout
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("serviceA interrupted");
            }
        }
        return "serviceA: " + executionCounterA;
    }

    @CircuitBreaker(delay = 2, delayUnit = ChronoUnit.SECONDS, requestVolumeThreshold = 3, failureRatio = 1.0)
    public String serviceB() throws ConnectException {
        executionCounterB++;
        System.out.println("serviceB: " + executionCounterB);

        if (executionCounterB <= 3) {
            throw new ConnectException("serviceB exception: " + executionCounterB);
        }
        return "serviceB: " + executionCounterB;
    }
}
