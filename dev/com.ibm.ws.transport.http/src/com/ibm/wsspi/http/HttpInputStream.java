/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.http;

import java.io.InputStream;

/**
 *
 */
public abstract class HttpInputStream extends InputStream {

    /**
     * Query whether this stream has been closed already or not.
     * 
     * @return boolean
     */
    public abstract boolean isClosed();
}