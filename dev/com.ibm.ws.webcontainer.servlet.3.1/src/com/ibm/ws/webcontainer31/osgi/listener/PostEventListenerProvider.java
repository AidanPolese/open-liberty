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
 * PostEventListenerProviders are notified to register listeners after all other listeners are registered during application start.
 * For application start the registration order is:
 * 1. PreEventProvider listeners.
 * 2. Application Listeners.
 * 3. Listeners registered by a servlet context listener.
 * 4. PostEventPorvider listeners
 * 
 * PostEventListenerProviders will be called in reverse service ranking order - the highest ranked service will be called last.
 */

public interface PostEventListenerProvider {
    
    public void registerListener(IServletContext sc);
    
    /*
     * This method is called just before the  AsyncListener onComplete. onError or onTimeout
     * methods are called. It can be used to register an AsyncListener which will be run after
     * any application AsyncListenes. 
     */
    public void registerListener(IServletContext sc, AsyncContextImpl ac);

}
