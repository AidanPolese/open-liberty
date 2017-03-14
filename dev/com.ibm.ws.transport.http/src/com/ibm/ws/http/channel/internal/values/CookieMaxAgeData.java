// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.channel.internal.values;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.genericbnf.internal.GenericUtils;
import com.ibm.ws.http.channel.internal.HttpMessages;
import com.ibm.wsspi.genericbnf.HeaderKeys;
import com.ibm.wsspi.http.HttpCookie;
import com.ibm.wsspi.http.channel.values.HttpHeaderKeys;

/**
 * This class is used to set the MaxAge of the cookie.
 * 
 */
public class CookieMaxAgeData extends CookieData {

    /** Trace component for debugging */
    private static final TraceComponent tc = Tr.register(CookieMaxAgeData.class, HttpMessages.HTTP_TRACE_NAME, HttpMessages.HTTP_BUNDLE);

    /**
     * Constructor for a Cookie's max-age information.
     */
    public CookieMaxAgeData() {
        super("max-age");
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.values.CookieData#set(com.ibm.websphere
     * .http.HttpCookie, byte[])
     */
    @Override
    public boolean set(HttpCookie cookie, byte[] attribValue) {
        try {
            if (null != attribValue && 0 < attribValue.length) {
                cookie.setMaxAge(GenericUtils.asIntValue(attribValue));
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Cookie max-age set to " + cookie.getMaxAge());
                }
            }
            return true;
        } catch (NumberFormatException e) {
            FFDCFilter.processException(e, getClass().getName(), "set", this);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Error in cookie max-age attribute: " + String.valueOf(attribValue));
            }
        }
        return false;
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.values.CookieData#validForHeader(com.ibm
     * .wsspi.genericbnf.HeaderKeys, boolean)
     */
    @Override
    public boolean validForHeader(HeaderKeys hdr, boolean includesDollar) {
        // Only valid for Set-Cookie headers
        if (HttpHeaderKeys.HDR_SET_COOKIE.equals(hdr) || HttpHeaderKeys.HDR_SET_COOKIE2.equals(hdr)) {
            return !includesDollar;
        }
        return false;
    }

}
