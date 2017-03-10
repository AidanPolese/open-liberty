/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot;

import com.ibm.wsspi.kernel.embeddable.ServerException;

/**
 * The LaunchException is used when a condition occurs that prevents the
 * launcher from being able to load or start the platform and supporting
 * OSGi framework. The exception message will contain information describing the
 * condition.
 */
public class LaunchException extends ServerException {
    private static final long serialVersionUID = 2021355231888752283L;

    private ReturnCode returnCode = ReturnCode.LAUNCH_EXCEPTION;

    public LaunchException(String message, String translatedMsg) {
        super(message, translatedMsg);
    }

    public LaunchException(String message, String translatedMsg, Throwable cause) {
        super(message, translatedMsg, cause);
    }

    public LaunchException(String message, String translatedMsg, Throwable cause, ReturnCode rc) {
        super(message, translatedMsg, cause);
        this.returnCode = rc;
    }

    public void setReturnCode(ReturnCode rc) {
        returnCode = rc;
    }

    public ReturnCode getReturnCode() {
        return returnCode;
    }
}
