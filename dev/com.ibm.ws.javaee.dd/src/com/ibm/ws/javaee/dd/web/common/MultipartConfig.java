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
package com.ibm.ws.javaee.dd.web.common;

/**
 *
 */
public interface MultipartConfig {

    /**
     * @return &lt;location>, or null if unspecified
     */
    String getLocation();

    /**
     * @return true if &lt;max-file-size> is specified
     * @see #getMaxFileSize
     */
    boolean isSetMaxFileSize();

    /**
     * @return &lt;max-file-size> if specified
     * @see #isSetMaxFileSize
     */
    long getMaxFileSize();

    /**
     * @return true if &lt;max-request-size> is specified
     * @see #getMaxRequestSize
     */
    boolean isSetMaxRequestSize();

    /**
     * @return &lt;max-request-size> if specified
     * @see #isSetMaxRequestSize
     */
    long getMaxRequestSize();

    /**
     * @return true if &lt;file-size-threshold> is specified
     * @see #getFileSizeThreshold
     */
    boolean isSetFileSizeThreshold();

    /**
     * @return &lt;file-size-threshold> if specified
     * @see #isSetFileSizeThreshold
     */
    int getFileSizeThreshold();

}
