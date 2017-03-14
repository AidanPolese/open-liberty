// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.genericbnf;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Class that encapsulates a single header value pair for a BNF message.
 */
public interface HeaderField {

    /**
     * Access the name of this header.
     * 
     * @return String
     */
    String getName();

    /**
     * Access the name of this header as an enumerated object.
     * 
     * @return HeaderKeys
     */
    HeaderKeys getKey();

    /**
     * Access the value of this header as a string. This is regular ASCII encoding
     * applied, if another encoding is wanted access the bytes directly.
     * 
     * @return String
     * @see HeaderField#asBytes()
     */
    String asString();

    /**
     * Access the value of this header as a byte[].
     * 
     * @return byte[]
     */
    byte[] asBytes();

    /**
     * Access the value of this header as a Date object.
     * 
     * @return Date
     * @throws ParseException
     *             - if it wasn't a date
     */
    Date asDate() throws ParseException;

    /**
     * Access the value of this header as an integer object.
     * 
     * @return int
     * @throws NumberFormatException
     *             - if it wasn't an number
     */
    int asInteger() throws NumberFormatException;

    /**
     * Tokenize the value of this header using the input delimiter.
     * 
     * @param delimiter
     * @return List<byte[]>
     */
    List<byte[]> asTokens(byte delimiter);

}
