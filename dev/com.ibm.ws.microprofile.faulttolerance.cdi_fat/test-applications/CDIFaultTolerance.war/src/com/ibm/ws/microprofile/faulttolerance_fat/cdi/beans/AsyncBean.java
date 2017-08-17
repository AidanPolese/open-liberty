package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.AsyncServlet;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

@RequestScoped
public class AsyncBean {

    public static final String CONNECT_A_DATA = "AsyncBean.connectA";
    public static final String CONNECT_B_DATA = "AsyncBean.connectB";
    public static final String CONNECT_C_DATA = "AsyncBean.connectC";

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

    @Timeout(AsyncServlet.TIMEOUT)
    @Asynchronous
    public Future<Connection> connectB() throws InterruptedException {
        System.out.println(System.currentTimeMillis() + " - " + CONNECT_B_DATA + " started");
        Thread.sleep(AsyncServlet.WORK_TIME);
        Connection conn = new Connection() {
            @Override
            public String getData() {
                return CONNECT_B_DATA;
            }
        };
        System.out.println(System.currentTimeMillis() + " - " + CONNECT_B_DATA + " returning");
        return CompletableFuture.completedFuture(conn);
    }

    @Asynchronous
    public Future<Void> connectC() throws InterruptedException {
        System.out.println(System.currentTimeMillis() + " - " + CONNECT_C_DATA + " started");
        Thread.sleep(AsyncServlet.WORK_TIME);
        System.out.println(System.currentTimeMillis() + " - " + CONNECT_C_DATA + " returning");
        return CompletableFuture.completedFuture(null);
    }
}
