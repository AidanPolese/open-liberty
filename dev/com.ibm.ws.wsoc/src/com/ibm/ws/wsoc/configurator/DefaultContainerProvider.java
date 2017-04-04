/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.wsoc.configurator;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import com.ibm.ws.wsoc.external.WebSocketContainerExt;

/**
 *
 */
public class DefaultContainerProvider extends ContainerProvider {

    /*
     * (non-Javadoc)
     * 
     * @see javax.websocket.ContainerProvider#getContainer()
     */
    @Override
    protected WebSocketContainer getContainer() {
        return new WebSocketContainerExt();
    }
}
