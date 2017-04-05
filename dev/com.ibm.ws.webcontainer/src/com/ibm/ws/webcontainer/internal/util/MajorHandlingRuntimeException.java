// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.internal.util;

/**
 * Exceptions for major errors during handling after discrimination
 * 
 */
public class MajorHandlingRuntimeException extends RuntimeException {

    /** Serialization ID value */
    static final private long serialVersionUID = -8639277872739606826L;

    /**
     * Constructor for this exception
     * 
     * @param message
     */
    public MajorHandlingRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructor for this exception
     * 
     * @param message
     * @param nested exception
     */
    public MajorHandlingRuntimeException(String message, Throwable t) {
        super(message, t);
    }
}
