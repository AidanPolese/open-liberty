/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.wsspi.http.logging;

import java.util.HashSet;
import java.util.Set;

/**
 * Registers the supported LogForwarder's to use with the Logging Service.
 */
public class LogForwarderManager {
    private static final Set<AccessLogForwarder> forwarders = new HashSet<AccessLogForwarder>();

    public static boolean registerAccessLogForwarder(AccessLogForwarder forwarder) {
        return forwarders.add(forwarder);
    }

    public static boolean deregisterAccessLogForwarder(AccessLogForwarder forwarder) {
        return forwarders.remove(forwarder);
    }

    /**
     * @return
     */
    public static Set<AccessLogForwarder> getAccessLogForwarders() {
        return forwarders;
    }
}
