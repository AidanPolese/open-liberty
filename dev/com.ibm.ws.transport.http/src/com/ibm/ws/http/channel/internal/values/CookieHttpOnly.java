// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.channel.internal.values;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.http.channel.internal.HttpMessages;
import com.ibm.wsspi.genericbnf.HeaderKeys;
import com.ibm.wsspi.http.HttpCookie;
import com.ibm.wsspi.http.channel.values.HttpHeaderKeys;

/**
 * MS IE added an attribute called HttpOnly to try and prevent client browsers
 * scripting from accessing the cookie. This is an attempt avoid some
 * cross-site scripting problems.
 */
public class CookieHttpOnly extends CookieData {

    /** Trace component for debugging */
    private static final TraceComponent tc = Tr.register(CookieHttpOnly.class, HttpMessages.HTTP_TRACE_NAME, HttpMessages.HTTP_BUNDLE);

    /**
     * Constructor.
     */
    public CookieHttpOnly() {
        super("httponly");
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.values.CookieData#set(com.ibm.websphere
     * .http.HttpCookie, byte[])
     */
    @Override
    @SuppressWarnings("unused")
    public boolean set(HttpCookie cookie, byte[] data) {
        cookie.setHttpOnly(true);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Cookie HttpOnly attribute set");
        }
        return true;
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.values.CookieData#validForHeader(com.ibm
     * .wsspi.genericbnf.HeaderKeys, boolean)
     */
    @Override
    public boolean validForHeader(HeaderKeys hdr, boolean includesDollar) {
        if (HttpHeaderKeys.HDR_SET_COOKIE.equals(hdr) || HttpHeaderKeys.HDR_SET_COOKIE2.equals(hdr)) {
            return !includesDollar;
        }
        return false;
    }

}