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

package com.ibm.ws.javaee.dd.app;

import java.util.List;

import com.ibm.ws.javaee.dd.DeploymentDescriptor;
import com.ibm.ws.javaee.dd.common.DescriptionGroup;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRefs;
import com.ibm.ws.javaee.dd.common.MessageDestination;
import com.ibm.ws.javaee.dd.common.SecurityRole;

/**
 *
 */
public interface Application
                extends DeploymentDescriptor, DescriptionGroup, JNDIEnvironmentRefs {
    static final String DD_NAME = "META-INF/application.xml";

    /**
     * @return version="..." attribute value
     */
    String getVersion();

    /**
     * @return &lt;application-name>, or null if unspecified
     */
    String getApplicationName();

    /**
     * @return true if &lt;initialize-in-order> is specified
     * @see #isInitializeInOrder
     */
    boolean isSetInitializeInOrder();

    /**
     * @return &lt;initialize-in-order> if specified
     * @see #isSetInitializeInOrder
     */
    boolean isInitializeInOrder();

    /**
     * @return &lt;module> as a read-only list
     */
    List<Module> getModules();

    /**
     * @return &lt;security-role> as a read-only list
     */
    List<SecurityRole> getSecurityRoles();

    /**
     * @return &lt;library-directory>, or null if unspecified
     */
    String getLibraryDirectory();

    /**
     * @return &lt;message-destination> as a read-only list
     */
    List<MessageDestination> getMessageDestinations();
}
