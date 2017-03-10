/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.xml.internal;

import com.ibm.websphere.config.ConfigParserException;

/**
 * Indicate that some sort of exception occurred while parsing, but it's cause
 * is something that we can tolerate.
 */
public class ConfigParserTolerableException extends ConfigParserException {
    private static final long serialVersionUID = -185687558103213805L;

    public ConfigParserTolerableException() {
        super();
    }

    public ConfigParserTolerableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigParserTolerableException(String message) {
        super(message);
    }

    public ConfigParserTolerableException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
