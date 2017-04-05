//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2003, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//@(#) 1.5 SERV1/ws/code/ssl.channel.impl/src/com/ibm/ws/ssl/channel/impl/SSLHandshakeCompletedCallback.java, WAS.channel.ssl, WASX.SERV1, pp0919.25 2/22/08 16:16:16 [5/15/09 18:21:27]
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 070404   leeja       LIDB2924-15     Remove JSSE2 usage
// 022108   leeja       499653          Fix double release of decnetbuffers

package com.ibm.ws.channel.ssl.internal;

import java.io.IOException;

import javax.net.ssl.SSLEngineResult;

/**
 * This callback will be used when calling the SSL utils code to do an asynchronous
 * handshake. When it is complete or an error occurs, this callback will be called
 * to take the next step.
 */
public interface SSLHandshakeCompletedCallback {

    /**
     * Called when the handshake is completed with the input result.
     * 
     * @param sslResult
     */
    void complete(SSLEngineResult sslResult);

    /**
     * Called when the handshake fails with the input exception.
     * 
     * @param ioe
     */
    void error(IOException ioe);

}
