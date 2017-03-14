// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel;

import com.ibm.wsspi.http.channel.inbound.HttpInboundServiceContext;

/**
 * Interface of the HTTP service that interacts with the WAS platform utils
 * service when running inside of WAS.
 * 
 */
public interface HttpPlatformUtils {

    /**
     * Log the z/os legacy message about a failure for this connection.
     * 
     * @param isc
     */
    void logLegacyMessage(HttpInboundServiceContext isc);

}
