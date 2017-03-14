// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.error;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

/**
 * Interface for a provider of HTTP error pages used by the error page service.
 * 
 */
public interface HttpErrorPageProvider {

    /**
     * Access the configured error page, if it exists, for the given information.
     * The host may be a hostname or an IP address. The request may or may not
     * be null, depending on what error scenario is happening. The response
     * message will always exist and will have the status code set to the desired
     * value.
     * 
     * @param localHost
     *            - this is from the socket layer
     * @param localPort
     *            - this is from the socket layer
     * @param request
     * @param response
     * @return WsByteBuffer[] - may be null if no page was found
     */
    WsByteBuffer[] accessPage(String localHost, int localPort, HttpRequestMessage request, HttpResponseMessage response);

}
