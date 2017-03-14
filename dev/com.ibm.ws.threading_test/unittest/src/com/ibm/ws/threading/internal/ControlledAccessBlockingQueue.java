/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.threading.internal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 *
 */
@SuppressWarnings("serial")
public class ControlledAccessBlockingQueue extends ArrayBlockingQueue<Runnable> {

    Semaphore semaphore = new Semaphore(1);

    /**
     * @param capacity
     * @throws InterruptedException
     */
    public ControlledAccessBlockingQueue(int capacity) {
        super(capacity);
        try {
            semaphore.acquire(); // acquire the first one, so all access requires a call to allowAccess()
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Runnable take() throws InterruptedException {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
            // http://was.pok.ibm.com/xwiki/bin/view/Liberty/LoggingFFDC
            e.printStackTrace();
        }
        return super.take();
    }

    void allowAccess() throws InterruptedException {
        semaphore.release();
    }
}
