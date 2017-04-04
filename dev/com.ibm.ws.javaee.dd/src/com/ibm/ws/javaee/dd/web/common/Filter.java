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

/**
 *
 */
public interface Filter
                extends DescriptionGroup {

    /**
     * @return &lt;filter-name>
     */
    String getFilterName();

    /**
     * @return &lt;filter-class>, or null if unspecified
     */
    String getFilterClass();

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
     * @return &lt;init-param> as a read-only list
     */
    List<ParamValue> getInitParams();

}
