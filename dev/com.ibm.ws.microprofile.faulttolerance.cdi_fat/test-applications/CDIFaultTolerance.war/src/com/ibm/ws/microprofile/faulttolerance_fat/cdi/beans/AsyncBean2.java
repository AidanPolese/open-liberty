package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import java.util.concurrent.Future;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.Asynchronous;

import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

@ApplicationScoped
public class AsyncBean2 {

    @Inject
    AsyncBean3 bean;

    @Asynchronous
    public Future<Connection> connectA() throws InterruptedException {
        return bean.connectA();
    }

}
