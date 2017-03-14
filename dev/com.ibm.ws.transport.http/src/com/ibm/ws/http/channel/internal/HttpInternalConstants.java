// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.channel.internal;

/**
 * This class contains various defined constants used throughout HTTP Channel
 * impl code.
 */
public class HttpInternalConstants {

    /**
     * Private constructor... this class just provides constants
     * 
     */
    private HttpInternalConstants() {
        // nothing to do
    }

    /** Static variable for the '/' byte */
    public static final byte FORWARD_SLASH = '/';

    /** Binary transport protocol version 1 ID */
    public static final byte BINARY_TRANSPORT_V1 = '1';

    /** Parsing the int ID or length of the unknown version */
    public static final int PARSING_VERSION_ID_OR_LEN = 5;
    /** Parsing an unknown version string */
    public static final int PARSING_UNKNOWN_VERSION = 6;
    /** Parsing the int ID or the length of the unknown method */
    public static final int PARSING_METHOD_ID_OR_LEN = 7;
    /** Parsing the unknown method string */
    public static final int PARSING_UNKNOWN_METHOD = 8;
    /** Parsing the URI length */
    public static final int PARSING_URI_LEN = 9;
    /** Parsing the URI string */
    public static final int PARSING_URI = 10;
    /** Parsing the status code integer */
    public static final int PARSING_STATUS = 11;
    /** Parsing the length of the reason phrase */
    public static final int PARSING_REASON_LEN = 12;
    /** Parsing the reason phrase string */
    public static final int PARSING_REASON = 13;
    /** Parsing the binary protocol version */
    public static final int PARSING_BINARY_VERSION = 14;
    /** Parsing the chunk length marker */
    public static final int PARSING_CHUNK_LENGTH = 15;
    /** Parsing past a chunk extension string */
    public static final int PARSING_CHUNK_EXTENSION = 16;
    /** Parsing a CRLF sequence */
    public static final int PARSING_CRLF = 17;
    /** Parsing of End of Message */
    public static final int PARSING_END_OF_MESSAGE = 18; //PI33453

}
