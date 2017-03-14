/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.access;

/**
 *
 */
public class TransportConstants {

    // following added for WebSockets
    public static final String UPGRADED_CONNECTION = "UpgradedConnection";

    public static final String UPGRADED_WEB_CONNECTION_OBJECT = "UpgradedWebConnectionObject";

    // following added for Upgrade
    public static final String CLOSE_NON_UPGRADED_STREAMS = "CloseNonUpgradedStreams";
    public static final String UPGRADED_LISTENER = "UpgradedListener";
    public static final String CLOSE_UPGRADED_WEBCONNECTION = "CloseUpgradedWebConnection";

}
