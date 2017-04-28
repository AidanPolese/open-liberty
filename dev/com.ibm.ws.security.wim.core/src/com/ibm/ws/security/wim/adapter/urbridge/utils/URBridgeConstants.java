/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013, 2014
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.adapter.urbridge.utils;

import com.ibm.wsspi.security.wim.SchemaConstants;

public interface URBridgeConstants {
    // Strings for the map and reverse map
    public static final String USER_DISPLAY_NAME_PROP = "userDisplayNameProperty";
    public static final String USER_SECURITY_NAME_PROP = "userSecurityNameProperty";
    public static final String UNIQUE_USER_ID_PROP = "uniqueUserIdProperty";
    public static final String GROUP_DISPLAY_NAME_PROP = "groupDisplayNameProperty";
    public static final String GROUP_SECURITY_NAME_PROP = "groupSecurityNameProperty";
    public static final String UNIQUE_GROUP_ID_PROP = "uniqueGroupIdProperty";

    public static final String USER_DISPLAY_NAME_DEFAULT_PROP = "displayName";
    public static final String USER_SECURITY_NAME_DEFAULT_PROP = SchemaConstants.PROP_UNIQUE_NAME;
    public static final String UNIQUE_USER_ID_DEFAULT_PROP = SchemaConstants.PROP_UNIQUE_ID;
    public static final String GROUP_DISPLAY_NAME_DEFAULT_PROP = "displayName";
    public static final String GROUP_SECURITY_NAME_DEFAULT_PROP = SchemaConstants.PROP_UNIQUE_NAME;
    public static final String UNIQUE_GROUP_ID_DEFAULT_PROP = SchemaConstants.PROP_UNIQUE_ID;

    public static final String DISPLAY_NAME = "displayName";

    public static final String CUSTOM_REGISTRY_IMPL_CLASS = "registryImplClass";
}
