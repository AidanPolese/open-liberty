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

package com.ibm.ws.javaee.dd.web;

import com.ibm.ws.javaee.dd.DeploymentDescriptor;
import com.ibm.ws.javaee.dd.common.ModuleDeploymentDescriptor;
import com.ibm.ws.javaee.dd.web.common.AbsoluteOrdering;
import com.ibm.ws.javaee.dd.web.common.WebCommon;

/**
 *
 */
public interface WebApp extends ModuleDeploymentDescriptor, DeploymentDescriptor, WebCommon {

    static final String DD_NAME = "WEB-INF/web.xml";

    static final int VERSION_3_1 = 31;

    static final int VERSION_3_0 = 30;

    /**
     * @return version="..." attribute value
     */
    String getVersion();

    /**
     * @return true if metadata-complete="..." attribute is specified
     */
    boolean isSetMetadataComplete();

    /**
     * @return metadata-complete="..." attribute value if specified
     */
    boolean isMetadataComplete();

    /**
     * @return &lt;module-name>, or null if unspecified
     */
    @Override
    String getModuleName();

    /**
     * @return &lt;absolute-ordering>, or null if unspecified
     */
    AbsoluteOrdering getAbsoluteOrdering();

}
