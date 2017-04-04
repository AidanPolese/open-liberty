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
package com.ibm.ws.webcontainer31.osgi.response;

import java.io.IOException;

/**
 * This custom exception will be thrown when non-blocking i/o is started but an blocking write is attempted.
 */
public class BlockingWriteNotAllowedException extends IOException {

    /**
     * @param formatMessage
     */
    public BlockingWriteNotAllowedException(String formatMessage) {
        super(formatMessage);
    }

    /**  */
    private static final long serialVersionUID = 1L;

}
