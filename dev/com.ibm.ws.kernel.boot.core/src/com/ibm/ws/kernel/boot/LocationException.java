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
package com.ibm.ws.kernel.boot;

import com.ibm.wsspi.kernel.embeddable.ServerException;

/**
 * The LocationException is used when configured (or calculated) locations
 * can not be resolved.
 * The exception message will contain information describing the condition.
 */
public class LocationException extends ServerException {
    private static final long serialVersionUID = 5567704962465063487L;

    public LocationException(String message, String translatedMsg, Throwable cause) {
        super(message, translatedMsg, cause);
    }

    public LocationException(String message, String translatedMsg) {
        super(message, translatedMsg);
    }
}
