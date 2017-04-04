//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2003, 2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
// 
//@(#) 1.2 SERV1/ws/code/ssl.channel.impl/src/com/ibm/ws/ssl/channel/exception/SocketEstablishedSSLException.java, WAS.channel.ssl, WASX.SERV1 5/10/04 22:31:38 [1/4/05 10:36:30]

package com.ibm.ws.channel.ssl.internal.exception;

import javax.net.ssl.SSLException;

/**
 * Socket has already been established and therefore
 * a user cannot set this parameter.
 */
public class SocketEstablishedSSLException extends SSLException {

    /** Serialization ID string */
    private static final long serialVersionUID = 5731482978051458363L;

    /**
     * @param arg0
     */
    public SocketEstablishedSSLException(String arg0) {
        super(arg0);
    }

}
