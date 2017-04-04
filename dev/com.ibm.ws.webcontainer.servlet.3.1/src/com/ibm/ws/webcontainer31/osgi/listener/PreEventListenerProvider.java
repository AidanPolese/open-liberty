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
package com.ibm.ws.webcontainer31.osgi.listener;

import com.ibm.ws.webcontainer.async.AsyncContextImpl;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 * PreEventListenerProviders are notified to register listeners before any other listeners are registered during application start.
 * For application start the registration order is:
 * 1. PreEventProvider listeners.
 * 2. Application Listeners.
 * 3. Listeners registered by a servlet context listener.
 * 4. PostEventProvider listeners.
 * 
 * PreEventListenerProviders will be called in service ranking order - the highest ranked service will be called first.
 */
public interface PreEventListenerProvider {
    
    public void registerListener(IServletContext sc);
    
    /*
     * This method is called just before the first AsyncListener is registered for
     * an Async Servlet Request. It can registers an AsyncListener which will be run before
     * any application AsyncListenes.
     */ 
    public void registerListener(IServletContext sc, AsyncContextImpl ac);

}
