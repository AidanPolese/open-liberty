package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.faulttolerance.Asynchronous;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.AsyncServlet;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

@ApplicationScoped
public class AsyncBean3 {

    public static final String CONNECT_A_DATA = "AsyncBean3.connectA";

    @Asynchronous
    public Future<Connection> connectA() throws InterruptedException {
        System.out.println(System.currentTimeMillis() + " - " + CONNECT_A_DATA + " started");
        Thread.sleep(AsyncServlet.WORK_TIME);
        Connection conn = new Connection() {
            @Override
            public String getData() {
                return CONNECT_A_DATA;
            }
        };
        System.out.println(System.currentTimeMillis() + " - " + CONNECT_A_DATA + " returning");
        return CompletableFuture.completedFuture(conn);
    }

}
