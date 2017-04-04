/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2008, 2010
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.runtime;

/**
 * This interface is used internally by the EJB runtime to notify listeners
 * for a module when their containing application has started or stopped.
 */
public interface EJBApplicationEventListener
{
    /**
     * This method is invoked when the EJB runtime indicates an application has
     * finished starting. If the module for this listener was started as part
     * of a fine-grained application update, then this callback will be called
     * at the end of module start.
     */
    void applicationStarted(String appName);

    /**
     * This method is invoked when the EJB runtime indicates an application is
     * about to begin stopping. This event occurs prior to any modules in the
     * application actually being stopped.
     * 
     * <p>If the module for this listener is being stopped as part of a
     * fine-grained application update, then this callback will be called at the
     * beginning of module stop rather than application stop.
     */
    void applicationStopping(String appName);
}
