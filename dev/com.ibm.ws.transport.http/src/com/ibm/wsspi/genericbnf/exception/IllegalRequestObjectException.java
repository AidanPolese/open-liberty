// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.genericbnf.exception;

/**
 * Thrown when the object set is not a valid request object
 * 
 * @ibm-private-in-use
 */
public class IllegalRequestObjectException extends Exception {

    /** Serialization ID value */
    static final private long serialVersionUID = -6834366662772796503L;

    /**
     * Constructor for this exception
     * 
     * @param message
     */
    public IllegalRequestObjectException(String message) {
        super(message);
    }
}
