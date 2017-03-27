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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

class DynamicURL {

    private static final Logger logger = Logger.getLogger(DynamicURL.class.getName());

    private final String name;
    private final Connector connector;

    DynamicURL(Connector connector, String name) {
        this.name = name;
        this.connector = connector;
    }

    URL getURL() throws MalformedURLException {
        String[] endpoint = RESTMBeanServerConnection.splitEndpoint(connector.getCurrentEndpoint());
        URL retURL = new URL("https", endpoint[0], Integer.valueOf(endpoint[1]), getName());

        if (logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, logger.getName(), "getURL", "URL: " + retURL.toString());
        }

        return retURL;
    }

    String getName() {
        return name;
    }
}
