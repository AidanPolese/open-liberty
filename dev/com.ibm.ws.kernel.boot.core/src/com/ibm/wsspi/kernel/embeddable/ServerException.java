/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.embeddable;

/**
 * Exception that encapsulates an issue encountered by the runtime
 * while processing a server operation.
 */
public abstract class ServerException extends RuntimeException {

    private static final long serialVersionUID = -2314997212544086815L;

    private final String translatedMsg;

    public ServerException(String message, String translatedMsg, Throwable cause) {
        super(message, cause);
        this.translatedMsg = translatedMsg;
    }

    public ServerException(String message, String translatedMsg) {
        super(message);
        this.translatedMsg = translatedMsg;
    }

    public String getTranslatedMessage() {
        return translatedMsg;
    }

}
