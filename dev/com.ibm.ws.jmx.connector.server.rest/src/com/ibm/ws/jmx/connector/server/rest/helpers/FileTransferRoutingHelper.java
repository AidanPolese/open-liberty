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

import com.ibm.wsspi.rest.handler.RESTRequest;
import com.ibm.wsspi.rest.handler.RESTResponse;

/**
 *
 */
public interface FileTransferRoutingHelper {

    String processSymbolicRoutingPath(String path, String targetHost, String targetServer, String targetUserDir, ServerPath symbolToResolve);

    void routedDeleteInternal(FileTransferHelper helper, RESTRequest request, String filePath, boolean recursiveDelete);

    void routedUploadInternal(FileTransferHelper helper, RESTRequest request, String filePath, boolean expansion, boolean legacyFileTransfer);

    void routedDownloadInternal(FileTransferHelper helper, RESTRequest request, RESTResponse response, String filePath, boolean legacyFileTransfer);

}