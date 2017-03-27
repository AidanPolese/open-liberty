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
package com.ibm.ws.fat.util.jmx;

import java.io.Serializable;

/**
 * Indicates that a JMX-related failure occurred; Typically wraps another exception with more specific problem analysis
 * 
 * @author Tim Burns
 */
public class JmxException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public JmxException() {
        super();
    }

    public JmxException(String message) {
        super(message);
    }

    public JmxException(String message, Throwable cause) {
        super(message, cause);
    }

    public JmxException(Throwable cause) {
        super(cause);
    }

}
