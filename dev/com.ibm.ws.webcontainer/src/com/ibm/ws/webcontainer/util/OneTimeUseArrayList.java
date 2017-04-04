/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.webcontainer.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * This subclass of ArrayList will only allow elements to be added to
 * it until an iterator is created for it.  At that point, the list
 * becomes a doorstop.  The add method will simply return false.
 */
@SuppressWarnings("serial")
public class OneTimeUseArrayList {


    private boolean enabled = true;
    
    private ArrayList<Future<?>> runnables = new ArrayList<Future<?>>();

    public synchronized boolean add(ExecutorService es, Runnable r) {
        if (enabled) {
            return runnables.add(es.submit(r));
        }
        return false;
    }

    public synchronized Iterator<Future<?>> iterator() {
        enabled = false;
        return runnables.iterator();
    }

    public synchronized ListIterator<Future<?>> listIterator() {
        enabled = false;
        return runnables.listIterator();
    }

    public synchronized ListIterator<Future<?>> listIterator(int index) {
        enabled = false;
        return runnables.listIterator(index);
    }
    
    public int size() {
        return runnables.size();
    }
    
    public Future<?> get(int index) {
        return runnables.get(index);
    }
}
