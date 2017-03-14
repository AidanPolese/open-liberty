// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.channel.internal.values;

import java.text.ParseException;
import java.util.Date;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.genericbnf.internal.GenericUtils;
import com.ibm.ws.http.channel.internal.HttpMessages;
import com.ibm.ws.http.dispatcher.internal.HttpDispatcher;
import com.ibm.wsspi.genericbnf.HeaderKeys;
import com.ibm.wsspi.http.HttpCookie;
import com.ibm.wsspi.http.channel.values.HttpHeaderKeys;

/**
 * This class is used to set the expiry date of the cookie.
 * 
 */
public class CookieExpiresData extends CookieData {

    /** Trace component for debugging */
    private static final TraceComponent tc = Tr.register(CookieExpiresData.class, HttpMessages.HTTP_TRACE_NAME, HttpMessages.HTTP_BUNDLE);

    /**
     * Constructor for a Cookie expires object.
     */
    public CookieExpiresData() {
        super("expires");
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.values.CookieData#set(com.ibm.websphere
     * .http.HttpCookie, byte[])
     */
    @Override
    public boolean set(HttpCookie cookie, byte[] attribValue) {
        if (null == cookie || null == attribValue || 0 == attribValue.length) {
            return false;
        }

        // Start parsing the byte array here to set the expiry date
        // For example: 24-Jun-03 16:01:45 GMT is a valid attrib value
        // Using the HttpDateFormat class
        try {
            Date expiryDate = HttpDispatcher.getDateFormatter().parseTime(attribValue);
            long remainingTime = expiryDate.getTime() - HttpDispatcher.getApproxTime();
            if (-1 < remainingTime) {
                cookie.setMaxAge((int) (remainingTime / 1000L));
            } else {
                // PK62826 - old date will translate to a 0 max-age (immediate
                // expiration)
                cookie.setMaxAge(0);
            }
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Expires/max-age set to " + cookie.getMaxAge());
            }
            return true;
        } catch (ParseException e) {
            // No FFDC required
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Error parsing expires date: " + GenericUtils.getEnglishString(attribValue));
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
