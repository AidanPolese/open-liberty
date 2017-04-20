/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.web.interfaces;

import com.ibm.ws.webcontainer.async.AsyncContextImpl;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

public interface PostEventListenerProvider {

    /*
     * This method is called to notify the provider to register any listener which must be
     * registered after any application listener.
     * For application start the registration order is:
     * 1. PreEventProvider listeners. Highest priority goes first.
     * 2. Application Listeners.
     * 3. Listeners registered by a servlet context listener.
     * 4. PostEventProvider listeners. Highest priority goes last.
     */
    public void registerListener(IServletContext sc);

    /*
     * This method is called to notify the provider to register an asyncListener which must be called
     * after any application asyncListeners.
     * AyncListeners are notified in this order"
     * 1. PreEventProvider asyncListeners. Highest priority goes first.
     * 2. Application AsyncListeners
     * 3. PostEventPorvider asyncListeners. Highest priority last.
     */
    public void registerListener(IServletContext sc, AsyncContextImpl ac);

}
