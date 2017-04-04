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
package com.ibm.ws.jmx.request;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RequestMetadata holds a Map<String, Object> that provides metadata about a
 * request. It also automatically generates a request sequence number and adds
 * it to the metadata with key RequestMetadata.REQUEST_ID.
 */
public class RequestMetadata {

    public static final String REQUEST_ID = "requestId";

    private static final AtomicLong sequenceNumber = new AtomicLong(0);
    private Map<String, Object> metadata;

    /**
     * The no arg constructor can be used when the only information to be
     * included in the metadata is the request ID. The request ID is
     * automatically generated and added to the metadata.
     */
    public RequestMetadata() {
        initialize((Map<String, Object>) null);
    }

    /**
     * Creates a RequestMetadata from the given Map and adds to it a generated
     * request ID. If a null Map is given the result is the same as calling
     * the no arg contructor.
     * 
     * @param metadata the metadata for a request
     */
    public RequestMetadata(Map<String, Object> metadata) {
        initialize(metadata);
    }

    /**
     * Generates a new metadata Map if none is given. Generates and adds a
     * request ID to the metadata.
     * 
     * @param metadata the metadata for a request
     */
    private void initialize(Map<String, Object> metadata) {
        if (metadata == null) {
            metadata = new HashMap<String, Object>();
        }
        metadata.put(REQUEST_ID, generateRequestID());
        this.metadata = metadata;
    }

    private String generateRequestID() {
        return String.valueOf(sequenceNumber.getAndIncrement());
    }

    /**
     * Gets the request metadata
     * 
     * @return the request metadata
     */
    public Map<String, Object> getRequestMetadata() {
        return metadata;
    }

    /**
     * Gets the request ID from the metadata
     * 
     * @return the request ID
     */
    public String getRequestId() {
        return (String) metadata.get(REQUEST_ID);
    }
}
