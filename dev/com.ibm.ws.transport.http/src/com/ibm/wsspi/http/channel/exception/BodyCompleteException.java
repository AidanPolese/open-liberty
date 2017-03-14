// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.exception;

/**
 * There is no more body to be processed.
 * 
 * @ibm-private-in-use
 */
public class BodyCompleteException extends Exception {

    /** Serialization ID value */
    static final private long serialVersionUID = 9133046536096026337L;

    /**
     * Constructor for BodyCompleteException.
     * 
     * @param message
     */
    public BodyCompleteException(String message) {
        super(message);
    }
}
