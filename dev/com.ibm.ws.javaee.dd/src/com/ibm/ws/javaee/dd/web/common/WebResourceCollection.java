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

import com.ibm.ws.javaee.dd.common.Describable;

/**
 *
 */
public interface WebResourceCollection
                extends Describable {

    /**
     * @return &lt;web-resource-name>
     */
    String getWebResourceName();

    /**
     * @return &lt;url-pattern> as a read-only list
     */
    List<String> getURLPatterns();

    /**
     * @return &lt;http-method> as a read-only list
     */
    List<String> getHTTPMethods();

    /**
     * @return &lt;http-method-omission> as a read-only list
     */
    List<String> getHTTPMethodOmissions();

}
