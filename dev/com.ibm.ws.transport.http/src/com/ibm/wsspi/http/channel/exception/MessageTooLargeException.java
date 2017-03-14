// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.exception;

/**
 * As an incoming message is being read, it may exceed the configured limit for
 * an acceptable message size. If an application channel is involved at this
 * point, then the body request APIs that discovered the excessive messsage
 * size will use this exception to notify the caller of the error. A typical
 * HTTP error response at this point would be the "413 Request Entity Too Large"
 * status code.
 */
public class MessageTooLargeException extends IllegalHttpBodyException {

    /** Serialization ID value */
    static final private long serialVersionUID = -6773650949819504802L;

    /**
     * Constructor for this exception
     * 
     * @param msg
     */
    public MessageTooLargeException(String msg) {
        super(msg);
    }

}