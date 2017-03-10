/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *
 * Change activity:
 *
 * Issue Date Name Description
 * ----------- ----------- -------- ------------------------------------
 *
 */

package com.ibm.ws.config.admin.internal;

/**
 *
 */
public interface ConfigAdminConstants {

    /**
     * Strings for trace and nls messages (for those classes w/in the bundle that
     * use Tr)
     */
    String TR_GROUP = "config";
    String NLS_PROPS = "com.ibm.ws.config.internal.resources.ConfigMessages";

    /** Property identifying prefix of configuration key */
    String CFG_CONFIG_PREFIX = "config.";

    /** Internal property identifying an instance id for factory-based configuration */
    String CFG_CONFIG_INSTANCE_ID = CFG_CONFIG_PREFIX + "id";

    String VAR_IN_USE = "WLP_VAR_IN_USE";

    /** a subdirectory name where the config files would be persisted */
    String CONFIG_PERSISTENT_SUBDIR = "configs";

}
