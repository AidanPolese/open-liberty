package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Timeout;

@RequestScoped
@Asynchronous
public class BulkheadBean {

    private final AtomicInteger connectBCounter = new AtomicInteger(0);
    private final AtomicInteger connectATokens = new AtomicInteger(0);
    private final AtomicInteger connectBTokens = new AtomicInteger(0);

    //disabled until api is updated
    //@Bulkhead(value = 2, waitingTaskQueue = 2)
    public Future<Boolean> connectA(String data) throws InterruptedException {
        System.out.println("connectA starting " + data);
        int token = connectATokens.incrementAndGet();
        try {
            if (token > 2) {
                throw new RuntimeException("Too many threads in connectA[" + data + "]: " + token);
            }
            Thread.sleep(5000);
            return CompletableFuture.completedFuture(Boolean.TRUE);
        } finally {
            connectATokens.decrementAndGet();
            System.out.println("connectA complete " + data);
        }
    }

    @Timeout(2000)
    //disabled until api is updated
    //@Bulkhead(value = 2, waitingTaskQueue = 2)
    public Future<Boolean> connectB(String data) throws InterruptedException {
        System.out.println("connectB starting " + data);
        int token = connectBTokens.incrementAndGet();
        try {
            if (token > 2) {
                throw new RuntimeException("Too many threads in connectB[" + data + "]: " + token);
            }
            int counter = connectBCounter.incrementAndGet();
            System.out.println("connectB counter " + counter);
            if (counter <= 2) {
                System.out.println("connectB sleeping " + data);
                Thread.sleep(5000);
                return CompletableFuture.completedFuture(Boolean.FALSE);
            } else {
                return CompletableFuture.completedFuture(Boolean.TRUE);
            }
        } finally {
            connectBTokens.decrementAndGet();
            System.out.println("connectB complete " + data);
        }
    }
}
