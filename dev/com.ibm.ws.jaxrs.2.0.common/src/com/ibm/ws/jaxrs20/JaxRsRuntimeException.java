/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20;

/**
 * Exception initially intended to allow a ServletExeption nested inside to flow out of CXF code
 * to the IBMRestServlet. But it may be used in other situations in the future.
 */
public class JaxRsRuntimeException extends RuntimeException {

    public JaxRsRuntimeException(Throwable ex) {
        super(ex);
    }
}
