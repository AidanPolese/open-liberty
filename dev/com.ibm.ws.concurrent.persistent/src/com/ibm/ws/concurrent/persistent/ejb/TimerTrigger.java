/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.concurrent.persistent.ejb;

import java.io.Serializable;

import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.Trigger;

/**
 * Serializable Callable or Runnable that is also a ManagedTask and Trigger
 * for a persistent EJB timer.
 */
public interface TimerTrigger extends ManagedTask, Serializable, Trigger {
    /**
     * Returns the name of the application that schedules the timer.
     * Null may be returned after the task has been deserialized.
     * 
     * @return the name of the application that schedules the timer.
     */
    String getAppName();

    /**
     * Returns the class loader of the EJB that schedules the timer.
     * Null may be returned after the task has been deserialized.
     * 
     * @return the class loader of the EJB that schedules the timer.
     */
    ClassLoader getClassLoader();
}
