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

import com.ibm.ws.javaee.dd.common.DescriptionGroup;
import com.ibm.ws.javaee.dd.common.ParamValue;
import com.ibm.ws.javaee.dd.common.RunAs;
import com.ibm.ws.javaee.dd.common.SecurityRoleRef;

/**
 *
 */
public interface Servlet
                extends DescriptionGroup {

    /**
     * @return &lt;servlet-name>
     */
    String getServletName();

    /**
     * @return &lt;servlet-class>, or null if unspecified
     */
    String getServletClass();

    /**
     * @return &lt;jsp-file>, or null if unspecified
     */
    String getJSPFile();

    /**
     * @return &lt;init-param> as a read-only list
     */
    List<ParamValue> getInitParams();

    /**
     * @return true if &lt;load-on-startup> is specified
     * @see #isNullLoadOnStartup
     * @see #getLoadOnStartup
     */
    boolean isSetLoadOnStartup();

    /**
     * @return true if &lt;load-on-startup> is specified and was null
     * @see #isSetLoadOnStartup
     * @see #getLoadOnStartup
     */
    boolean isNullLoadOnStartup();

    /**
     * @return &lt;load-on-startup> if specified and not null
     * @see #isSetLoadOnStartup
     * @see #isNullLoadOnStartup
     */
    int getLoadOnStartup();

    /**
     * @return true if &lt;enabled> is specified
     * @see #isEnabled
     */
    boolean isSetEnabled();

    /**
     * @return &lt;enabled> if specified
     * @see #isSetEnabled
     */
    boolean isEnabled();

    /**
     * @return true if &lt;async-supported> is specified
     * @see #isAsyncSupported
     */
    boolean isSetAsyncSupported();

    /**
     * @return &lt;async-supported> if specified
     * @see #isSetAsyncSupported
     */
    boolean isAsyncSupported();

    /**
     * @return &lt;run-as>, or null if unspecified
     */
    RunAs getRunAs();

    /**
     * @return &lt;security-role-ref> as a read-only list
     */
    List<SecurityRoleRef> getSecurityRoleRefs();

    /**
     * @return &lt;multipart-config>, or null if unspecified
     */
    MultipartConfig getMultipartConfig();

}
