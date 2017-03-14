// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.genericbnf.exception;

/**
 * Thrown when the object set is not a valid response object
 * 
 * @ibm-private-in-use
 */
public class IllegalResponseObjectException extends Exception {

    /** Serialization ID value */
    static final private long serialVersionUID = 4074608858088631945L;

    /**
     * Constructor for this exception
     * 
     * @param message
     */
    public IllegalResponseObjectException(String message) {
        super(message);
    }
}
