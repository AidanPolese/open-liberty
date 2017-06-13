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
package com.ibm.ws.jaxrs20;

/**
 * Exception indicates that configurations for the endpoint is incorrect, the configuration may be from annotation or deployment plans.
 */
public class InvalidMetaDataException extends Exception {

    public InvalidMetaDataException(String msg) {
        super(msg);
    }
}
