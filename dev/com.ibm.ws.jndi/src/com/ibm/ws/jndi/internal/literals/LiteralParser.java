/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jndi.internal.literals;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class LiteralParser {
    private static final TraceComponent tc = Tr.register(LiteralParser.class);

    public static Object parse(String s) {
        for (LiteralType type : LiteralType.values())
            if (type.matches(s))
                return type.parse(s);
        if (tc.isDebugEnabled())
            Tr.debug(tc, "String did not match any known types", s);
        return s;
    }

    private LiteralParser() {} // no instances, please!
}
