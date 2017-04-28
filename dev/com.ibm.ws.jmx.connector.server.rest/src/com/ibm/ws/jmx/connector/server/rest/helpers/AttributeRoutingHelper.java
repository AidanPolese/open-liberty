/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.server.rest.helpers;

import java.util.List;

import com.ibm.wsspi.rest.handler.RESTRequest;
import com.ibm.wsspi.rest.handler.RESTResponse;

/**
 *
 */
public interface AttributeRoutingHelper {

    void getAttributes(RESTRequest request, RESTResponse response, String objectName, List<String> queryAttributes, boolean isLegacy);

    void getAttribute(RESTRequest request, RESTResponse response, String objectName, String attributeName, boolean isLegacy);

    void setAttributes(RESTRequest request, RESTResponse response, boolean isLegacy);

    void setAttribute(RESTRequest request, RESTResponse response, boolean isLegacy);

    void deleteAttributes(RESTRequest request, RESTResponse response, boolean isLegacy);

    void deleteAttribute(RESTRequest request, RESTResponse response, boolean isLegacy);

}
