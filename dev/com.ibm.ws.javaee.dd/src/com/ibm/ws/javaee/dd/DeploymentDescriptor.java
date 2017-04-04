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

package com.ibm.ws.javaee.dd;

/**
 *
 */
public interface DeploymentDescriptor {
    /**
     * @return the path for the deployment descriptor.
     */
    String getDeploymentDescriptorPath();

    /**
     * @return the deployment descriptor component with the matching id="..." attribute, or null if not present
     */
    Object getComponentForId(String id);

    /**
     * @return the id="..." attribute for the deployment descriptor component, or null if unspecified
     */
    String getIdForComponent(Object ddComponent);

}
