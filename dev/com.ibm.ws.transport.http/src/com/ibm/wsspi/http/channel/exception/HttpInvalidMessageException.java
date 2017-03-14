// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.exception;

import java.io.IOException;

/**
 * Exception that represents when an invalid message has been sent out such
 * that the connection is now "broken". Examples include when the content-length
 * does not match the actual number of bytes sent with the message, causing the
 * other end of the socket to be unable to properly read the body.
 */
public class HttpInvalidMessageException extends IOException {

    /** Serialization ID value */
    static final private long serialVersionUID = -899943671770861147L;

    /**
     * Constructor for this exception
     * 
     * @param msg
     */
    public HttpInvalidMessageException(String msg) {
        super(msg);
    }
}
