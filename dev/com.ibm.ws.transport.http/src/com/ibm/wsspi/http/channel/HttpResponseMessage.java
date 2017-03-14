// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel;

import com.ibm.wsspi.http.channel.values.StatusCodes;

/**
 * Interface extending the base HTTP message with Response
 * specifics
 * 
 * @ibm-private-in-use
 */
public interface HttpResponseMessage extends HttpBaseMessage {

    // ******************************************************************
    // Response-line specific methods
    // ******************************************************************

    /**
     * Query the status-code (200, 404, etc) from the response
     * 
     * @return int
     */
    int getStatusCodeAsInt();

    /**
     * Get the status code as an enumerated type.
     * 
     * @return StatusCodes
     */
    StatusCodes getStatusCode();

    /**
     * Set the status code of the response message. An input code that does
     * not match an existing defined StatusCode will create a new "Undefined"
     * code where the getByteArray() API will return the input code as a
     * byte[].
     * 
     * @param code
     */
    void setStatusCode(int code);

    /**
     * Using the defined StatusCodes, set the status-code and the
     * reason-phrase to the default matching phrase.
     * 
     * @param code
     */
    void setStatusCode(StatusCodes code);

    /**
     * Query the value of the reason phrase ("Ok", "Not Found", etc)
     * in the response object
     * 
     * @return String
     */
    String getReasonPhrase();

    /**
     * Get the reason phrase as a byte array.
     * 
     * @return bytes
     */
    byte[] getReasonPhraseBytes();

    /**
     * Set the value of the reason phrase to the given reason string
     * 
     * @param reason
     */
    void setReasonPhrase(String reason);

    /**
     * Set the value of the reason phrase to the given reason byte array
     * 
     * @param reason
     */
    void setReasonPhrase(byte[] reason);

    // ******************************************************************
    // Message specific methods
    // ******************************************************************

    /**
     * Create a duplicate of this message, including all headers and other
     * information.
     * 
     * @return HttpResponseMessage
     */
    HttpResponseMessage duplicate();

}
