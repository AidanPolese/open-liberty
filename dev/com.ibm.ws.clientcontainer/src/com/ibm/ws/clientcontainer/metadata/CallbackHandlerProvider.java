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
package com.ibm.ws.clientcontainer.metadata;

import javax.security.auth.callback.CallbackHandler;

/**
 * <p>This allows other bundles to register themselves as listeners for getting a login callback handler, which may be
 * defined in the application-client.xml of a client module.</p>
 * 
 */
public interface CallbackHandlerProvider {

    /**
     * Return the callback handler from the application-client.xml.
     * 
     * @return The callback handler in the application-client.xml, or null when a callback handler element is not specified.
     */
    public CallbackHandler getCallbackHandler();

}
