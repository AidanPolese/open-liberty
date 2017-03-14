// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.genericbnf.exception;

/**
 * Thrown when the method given is not supported or enabled.
 * 
 * @ibm-private-in-use
 */
public class UnsupportedMethodException extends MalformedMessageException {

    /** Serialization ID value */
    static final private long serialVersionUID = -5148185552401118734L;

    /**
     * Constructor for the unsupported method exception
     * 
     * @param message
     */
    public UnsupportedMethodException(String message) {
        super(message);
    }
}
