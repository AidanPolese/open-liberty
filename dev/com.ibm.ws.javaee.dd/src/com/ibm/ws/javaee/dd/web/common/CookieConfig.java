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

/**
 *
 */
public interface CookieConfig {

    /**
     * @return &lt;name>, or null if unspecified
     */
    String getName();

    /**
     * @return &lt;domain>, or null if unspecified
     */
    String getDomain();

    /**
     * @return &lt;path>, or null if unspecified
     */
    String getPath();

    /**
     * @return &lt;comment>, or null if unspecified
     */
    String getComment();

    /**
     * @return true if &lt;http-only> is specified
     * @see #isHTTPOnly
     */
    boolean isSetHTTPOnly();

    /**
     * @return &lt;http-only> if specified
     * @see #isSetHTTPOnly
     */
    boolean isHTTPOnly();

    /**
     * @return true if &lt;secure> is specified
     * @see #isSecure
     */
    boolean isSetSecure();

    /**
     * @return &lt;secure> if specified
     * @see #isSetSecure
     */
    boolean isSecure();

    /**
     * @return true if &lt;max-age> is specified
     * @see #getMaxAge
     */
    boolean isSetMaxAge();

    /**
     * @return &lt;max-age> if specified
     * @see #isSetMaxAge
     */
    int getMaxAge();

}
