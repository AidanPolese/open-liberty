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
package com.ibm.ws.wsoc.external.v11;

import com.ibm.ws.wsoc.external.SessionExt;
import com.ibm.ws.wsoc.external.WebSocketFactory;

/**
 * WebSocket 1.1 Factory implementation
 */
public class WebSocketFactoryV11 implements WebSocketFactory {

    @Override
    public SessionExt getWebSocketSession() {
        return new SessionExtV11();
    }

}
