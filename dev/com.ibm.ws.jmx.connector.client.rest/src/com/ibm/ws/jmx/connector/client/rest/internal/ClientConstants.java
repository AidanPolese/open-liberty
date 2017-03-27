/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

package com.ibm.ws.jmx.connector.client.rest.internal;

class ClientConstants {

    enum HttpMethod {
        GET, POST, PUT, DELETE,
        HEAD, OPTIONS, TRACE
    };

    static final String JSON_MIME_TYPE = "application/json";

    static final boolean DISABLE_HOSTNAME_VERIFICATION_DEFAULT = false;

    static final int NOTIFICATION_DELIVERY_INTERVAL_DEFAULT = 0;

    static final int NOTIFICATION_FETCH_INTERVAL_DEFAULT = 1000;

    static final int NOTIFICATION_INBOX_EXPIRY_DEFAULT = 5 * 60 * 1000;

    static final int SERVER_FAILOVER_INTERVAL_DEFAULT = 30 * 1000;

    static final int READ_TIMEOUT_DEFAULT = 60 * 1000;

    static final int MAX_SERVER_WAIT_TIME_DEFAULT = 120 * 1000;

    static final int SERVER_STATUS_POLLING_INTERVAL_DEFAULT = 4 * 1000;

    static final String CONNECTOR_URI = "IBMJMXConnectorREST";

    static final String ROUTER_URI = CONNECTOR_URI + "/router";
}
