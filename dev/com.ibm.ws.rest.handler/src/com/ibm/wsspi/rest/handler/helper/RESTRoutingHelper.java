/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.wsspi.rest.handler.helper;

import java.io.IOException;

import com.ibm.wsspi.rest.handler.RESTRequest;
import com.ibm.wsspi.rest.handler.RESTResponse;

/**
 *
 */
public interface RESTRoutingHelper {

    /**
     * @return
     */
    boolean routingAvailable();

    /**
     * @param request
     * @param response
     */
    void routeRequest(RESTRequest request, RESTResponse response) throws IOException;

    /**
     * @param request
     * @param response
     * @param legacyURI
     */
    void routeRequest(RESTRequest request, RESTResponse response, boolean legacyURI) throws IOException;

    /**
     * @param request
     * @return
     */
    boolean containsLegacyRoutingContext(RESTRequest request);

    /**
     * @param request
     * @return
     */
    boolean containsRoutingContext(RESTRequest request);

}