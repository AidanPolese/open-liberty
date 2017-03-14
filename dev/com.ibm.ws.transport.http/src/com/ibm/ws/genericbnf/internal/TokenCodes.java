// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.genericbnf.internal;

import com.ibm.wsspi.genericbnf.GenericKeys;

/**
 * Class for return codes on the various parsing methods.
 * 
 */
public class TokenCodes extends GenericKeys {

    /**
     * Constructor of a token code object
     * 
     * @param s
     * @param o
     */
    public TokenCodes(String s, int o) {
        super(s, o);
    }

    /** Return code representing "needing more data" */
    public static final TokenCodes TOKEN_RC_MOREDATA = new TokenCodes("Need_more_data", 0);
    /** Return code representing "search delimiter found" */
    public static final TokenCodes TOKEN_RC_DELIM = new TokenCodes("Delimiter_found", 1);
    /** Return code representing "CRLF found while searching" */
    public static final TokenCodes TOKEN_RC_CRLF = new TokenCodes("CRLF_found", 2);
    /** Return code representing "search arg not found" */
    public static final TokenCodes TOKEN_RC_NOTFOUND = new TokenCodes("Not found", 3);

}
