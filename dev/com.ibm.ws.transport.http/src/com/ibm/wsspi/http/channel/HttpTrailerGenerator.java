// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel;

import com.ibm.wsspi.genericbnf.HeaderKeys;

/**
 * <code>HttpTrailerGenerator</code> defines an interface for
 * creating the values for a trailer dynamically.
 * 
 * <p>
 * These objects are intended to be called after the 0-byte chunk is sent.
 * </p>
 * 
 * @ibm-private-in-use
 */
public interface HttpTrailerGenerator {

    /**
     * Create a value for a specifc trailer.
     * 
     * @param hdr
     *            the HTTP header to generate as a trailer.
     * @param message
     *            the message to append the trailer to.
     * @return byte[] - the value of the trailer.
     */
    byte[] generateTrailerValue(HeaderKeys hdr, HttpTrailers message);

    /**
     * Create a value for a specifc trailer.
     * 
     * @param hdr
     *            the HTTP header to generate as a trailer.
     * @param message
     *            the message to append the trailer to.
     * @return byte[] - the value of the trailer.
     */
    byte[] generateTrailerValue(String hdr, HttpTrailers message);

}
