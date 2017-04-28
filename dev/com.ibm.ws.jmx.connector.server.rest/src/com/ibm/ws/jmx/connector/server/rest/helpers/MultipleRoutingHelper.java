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

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.ibm.wsspi.rest.handler.RESTRequest;

/**
 *
 */
public interface MultipleRoutingHelper {

    String multipleDeleteInternal(RESTRequest request, String targetPath, boolean recursive) throws IOException;

    String multipleUploadInternal(RESTRequest request, String targetPath, boolean expand, boolean local) throws IOException;

    String getTaskProperty(String taskID, String property);

    String getTaskProperties(String taskID);

    String getAllStatus(Set<Entry<String, List<String>>> filter);

    String getStatus(String taskID);

    String getHosts(String taskID);

    String getHostDetails(String taskID, String host);

}