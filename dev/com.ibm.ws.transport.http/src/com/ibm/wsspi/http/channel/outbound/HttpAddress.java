// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.outbound;

import com.ibm.wsspi.tcpchannel.TCPConnectRequestContext;

/**
 * Address object to pass in to connect to a remote host.
 * 
 * @ibm-private-in-use
 */
public interface HttpAddress extends TCPConnectRequestContext {

    /**
     * Hostname to pass into the Host header of the request.
     * 
     * @return String
     */
    String getHostname();

    /**
     * Query whether the target in the address is a forward proxy. If this
     * is true, then the request message will send out the full URL (scheme
     * plus hostname plus URI, etc), otherwise the request will only send out
     * the URI ([GET /index.html HTTP/1.1] for example).
     * 
     * @return boolean
     */
    boolean isForwardProxy();

}
