/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 *
 * Tag          Person   Defect/Feature      Comments
 * ----------   ------   --------------      --------------------------------------------------
 */

package com.ibm.ws.security.wim.registry;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.websphere.security.wim.Service;

/**
 * Constants and definitions for the WIMUserRegistry.
 *
 * @author Ankit Jain
 */
@Trivial
public interface WIMUserRegistryDefines {
    /**
     * Copyright notice.
     */
    String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    /**
     * Backslash character.
     */
    char BACKSLASH = '\\';

    /**
     * UserRegistry group level property
     */
    // d115192
    String GROUP_LEVEL = "com.ibm.ws.wim.registry.grouplevel";

    String RETURN_REALM_QUALIFIED_ID = "com.ibm.ws.wim.registry.returnRealmQualifiedId";

    /**
     * Key for the UserRegistry UniqueUserID.
     */
    String UNIQUE_USER_ID_DEFAULT = Service.PROP_UNIQUE_NAME;

    /**
     * Key for the UserRegistry UserSecurityName.
     */
    // New:: Change to Input/Output property
    // String USER_SECURITY_NAME_DEFAULT = Service.PROP_PRINCIPAL_NAME;
    String OUTPUT_USER_SECURITY_NAME_DEFAULT = Service.PROP_UNIQUE_NAME;

    /**
     * Key for the UserRegistry UserSecurityName.
     */
    String USER_SECURITY_NAME_DEFAULT = Service.PROP_PRINCIPAL_NAME;

    // New:: Change to Input/Output property
    /**
     * Key for the UserRegistry UserSecurityName.
     */
    String INPUT_USER_SECURITY_NAME_DEFAULT = Service.PROP_PRINCIPAL_NAME;

    /**
     * Key for the UserRegistry UserDisplayName.
     */
    String USER_DISPLAY_NAME_DEFAULT = Service.PROP_PRINCIPAL_NAME;

    /**
     * Key for the UserRegistry UniqueGroupID.
     */
    // New:: Change to Input/Output property
    // String UNIQUE_GROUP_ID_DEFAULT = Service.PROP_UNIQUE_NAME;
    String OUTPUT_UNIQUE_GROUP_ID_DEFAULT = Service.PROP_UNIQUE_NAME;

    // New:: Change to Input/Output property
    /**
     * Key for the UserRegistry UniqueGroupID.
     */
    String INPUT_UNIQUE_GROUP_ID_DEFAULT = "cn";

    /**
     * Key for the UserRegistry GroupSecurityName.
     */
    // New:: Change to Input/Output property
    // String GROUP_SECURITY_NAME_DEFAULT = "cn";
    String INPUT_GROUP_SECURITY_NAME_DEFAULT = "cn";

    // New:: Change to Input/Output property
    /**
     * Key for the UserRegistry GroupSecurityName.
     */
    String OUTPUT_GROUP_SECURITY_NAME_DEFAULT = Service.PROP_UNIQUE_NAME;

    /**
     * Key for the UserRegistry GroupDisplayName.
     */
    String GROUP_DISPLAY_NAME_DEFAULT = "cn";
}
