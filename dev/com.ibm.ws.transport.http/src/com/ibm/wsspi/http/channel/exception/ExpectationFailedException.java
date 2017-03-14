// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.exception;

import java.io.IOException;

/**
 * If an outbound request uses the "Expect: 100-continue" header and the server
 * responds back with the "417 Expectation Failed" response, then this exception
 * will be thrown by the the outbound service context to inform the caller of
 * the failure.
 * 
 * @ibm-private-in-use
 */
public class ExpectationFailedException extends IOException {

    /** Serialization ID value */
    static final private long serialVersionUID = -165530030652963006L;

    /**
     * Constructor for this exception
     * 
     * @param msg
     */
    public ExpectationFailedException(String msg) {
        super(msg);
    }
}
