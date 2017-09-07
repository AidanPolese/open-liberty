/*
 *
 * ===========================================================================
 * IBM Confidential
 * OCO Source Materials
 * Licensed Materials - Property of IBM
 *
 * (C) Copyright IBM Corp. 2016 All Rights Reserved.
 *
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 * ===========================================================================
 *
 */
package com.ibm.ws.security.mp.jwt.error;

/**
 * Represents an exception while processing the request with micro profile jwt.
 *
 */
public class MpJwtProcessingException extends Exception {

    /**  */
    private static final long serialVersionUID = 1L;

    public MpJwtProcessingException(String message) {
        super(message);
    }

    public MpJwtProcessingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
