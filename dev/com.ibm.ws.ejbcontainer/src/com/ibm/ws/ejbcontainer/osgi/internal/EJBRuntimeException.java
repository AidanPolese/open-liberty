/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal;

/**
 * Thrown when an error occurs while starting or stopping a module.
 */
@SuppressWarnings("serial")
public class EJBRuntimeException extends Exception {
    EJBRuntimeException(Throwable t) {
        super(t);
    }
}
