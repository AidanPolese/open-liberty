/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.http;

import java.util.concurrent.Executor;

/**
 * Work classification
 * 
 * Used to classify a piece of inbound work and setup the execution environment.
 * 
 */
public interface WorkClassifier {

    /**
     * Classify the request and return an Executor to run on.
     * 
     * @param request HTTP request to classify.
     * @param inboundConnection HTTP connection related to the request.
     * @return an Executor to run the HTTP request.
     */
    Executor classify(HttpRequest request, HttpInboundConnection inboundConnection);

}
