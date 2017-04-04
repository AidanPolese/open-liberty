//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2003, 2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
// 
//@(#) 1.2 SERV1/ws/code/ssl.channel.impl/src/com/ibm/ws/ssl/channel/exception/ReadNeededInternalException.java, WAS.channel.ssl, WASX.SERV1 5/10/04 22:31:33 [1/4/05 10:36:36]

package com.ibm.ws.channel.ssl.internal.exception;

import com.ibm.wsspi.channelfw.exception.ChannelException;

/**
 * This exception is used internally by the SSL channel to pass information
 * along that an additional read must be done in order to get enough data for
 * the SSL engine to run unencryption.
 */
public class ReadNeededInternalException extends ChannelException {

    /** Serialization ID string */
    private static final long serialVersionUID = -3236620232328367856L;

    /**
     * Constructor.
     * 
     * @param message
     */
    public ReadNeededInternalException(String message) {
        super(message);
    }

}
