/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.service.location;

/**
 *
 */
public class ResourceMismatchException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String msgString = "Expected resource type does not match the type of the existing resource (path=%s, expected=%s, found=%s";

    private String path;
    private WsResource.Type expected;
    private WsResource.Type found;

    /**
     * @param message
     */
    public ResourceMismatchException(String path, WsResource.Type expected, WsResource.Type found) {
        super(String.format(msgString, path, expected.toString(), found.toString()));
        this.path = path;
        this.expected = expected;
        this.found = found;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the expected
     */
    public WsResource.Type getExpected() {
        return expected;
    }

    /**
     * @return the found
     */
    public WsResource.Type getFound() {
        return found;
    }
}
