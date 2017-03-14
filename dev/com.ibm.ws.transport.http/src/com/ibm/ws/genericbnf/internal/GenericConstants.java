// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.genericbnf.internal;

/**
 * Constants used throughout the generic BNF package.
 */
public interface GenericConstants {

    /** RAS trace name for the genericbnf package */
    String GENERIC_TRACE_NAME = "GenericBNF";

    /** Mask used for known/defined values */
    int KNOWN_MASK = 0x20000; // 128 * 1024
    /** Mask used for unknown/undefined values */
    int UNKNOWN_MASK = 0x1FFFF; // KNOWN-1

    /** ID representing the end of headers marker */
    int END_OF_HEADERS = 0;
    /** ID representing a known header is next */
    int KNOWN_HEADER = 1;
    /** ID representing an unknown header is next */
    int UNKNOWN_HEADER = 2;

    /** Default non-parsing state */
    int PARSING_NOTHING = 0;
    /** Parsing the flag on what type of header is coming next */
    int PARSING_HDR_FLAG = 1;
    /** Parsing the known header ordinal */
    int PARSING_HDR_KNOWN = 2;
    /** Parsing the unknown header name length */
    int PARSING_HDR_NAME_LEN = 3;
    /** Parsing the unknown header string */
    int PARSING_HDR_NAME_VALUE = 4;
    /** Parsing the length of the header value string */
    int PARSING_HDR_VALUE_LEN = 5;
    /** Parsing the header value string */
    int PARSING_HDR_VALUE = 6;

}
