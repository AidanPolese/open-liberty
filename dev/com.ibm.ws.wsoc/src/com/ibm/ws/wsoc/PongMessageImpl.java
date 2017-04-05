/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.wsoc;

import java.nio.ByteBuffer;

import javax.websocket.PongMessage;

/**
 *
 */
public class PongMessageImpl implements PongMessage {

    private ByteBuffer _content = null;

    /**
     * 
     */
    public PongMessageImpl(ByteBuffer content) {
        _content = content;

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.websocket.PongMessage#getApplicationData()
     */
    @Override
    public ByteBuffer getApplicationData() {
        return _content;
    }
}
