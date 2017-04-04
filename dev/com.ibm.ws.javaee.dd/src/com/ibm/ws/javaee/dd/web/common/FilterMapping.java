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
public interface FilterMapping {

    static enum DispatcherEnum {
        // lexical value must be (FORWARD|INCLUDE|REQUEST|ASYNC|ERROR)
        FORWARD,
        INCLUDE,
        REQUEST,
        ASYNC,
        ERROR;
    }

    /**
     * @return &lt;filter-name>
     */
    String getFilterName();

    /**
     * @return &lt;url-pattern>, or null if not specified
     */
    String getURLPattern();

    /**
     * @return &lt;servlet-name>, or null if not specified
     */
    String getServletName();

    /**
     * @return &lt;dispatcher> as a read-only list
     */
    List<DispatcherEnum> getDispatcherValues();

}
