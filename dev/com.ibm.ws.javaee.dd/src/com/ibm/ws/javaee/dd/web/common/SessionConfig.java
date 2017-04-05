// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// F46946    WAS85     20110712 bkail    : New
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.javaee.dd.web.common;

import java.util.List;

/**
 *
 */
public interface SessionConfig {

    static enum TrackingModeEnum {
        // lexical value must be (COOKIE|URL|SSL)
        COOKIE,
        URL,
        SSL;
    }

    /**
     * @return true if &lt;session-timeout> is specified
     * @see #getSessionTimeout
     */
    boolean isSetSessionTimeout();

    /**
     * @return &lt;session-timeout> if specified
     * @see #isSetSessionTimeout
     */
    int getSessionTimeout();

    /**
     * @return &lt;cookie-config>, or null if unspecified
     */
    CookieConfig getCookieConfig();

    /**
     * @return &lt;tracking-mode> as a read-only list
     */
    List<TrackingModeEnum> getTrackingModeValues();
}
