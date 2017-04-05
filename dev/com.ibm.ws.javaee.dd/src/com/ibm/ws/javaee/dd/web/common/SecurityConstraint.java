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

import com.ibm.ws.javaee.dd.common.DisplayName;

/**
 *
 */
public interface SecurityConstraint {

    /**
     * @return &lt;display-name> as a read-only list
     */
    List<DisplayName> getDisplayNames();

    /**
     * @return &lt;web-resource-collection> as a read-only list
     */
    List<WebResourceCollection> getWebResourceCollections();

    /**
     * @return &lt;auth-constraint>, or null if unspecified
     */
    AuthConstraint getAuthConstraint();

    /**
     * @return &lt;user-data-constraint>, or null if unspecified
     */
    UserDataConstraint getUserDataConstraint();

}
