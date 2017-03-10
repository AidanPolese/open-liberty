/*
 * IBM Confidential
 * 
 * OCO Source Materials
 * 
 * Copyright IBM Corp. 2009
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

package com.ibm.ws.config.schemagen.internal;

import java.util.regex.Pattern;

interface SchemaGenConstants {
    /**
     * Strings for trace and nls messages (for those classes w/in the bundle that
     * use Tr)
     */
    String TR_GROUP = "config";
    String NLS_PROPS = "com.ibm.ws.config.internal.resources.ConfigMessages";

    /** Property identifying prefix of configuration key */
    String CFG_CONFIG_PREFIX = "config.";

    /** Property defines an attribute to identify an instance id for factory-based configuration */
    String CFG_INSTANCE_ID = "id";

    /** This is the URI for the ibm: namespace we use for IBM extensions to metatype. */
    String METATYPE_EXTENSION_URI = "http://www.ibm.com/xmlns/appservers/osgi/metatype/v1.0.0";

    /** This is the URI for the ibmui: namespace we use for IBM UI extensions to metatype. */
    String METATYPE_UI_EXTENSION_URI = "http://www.ibm.com/xmlns/appservers/osgi/metatype/ui/v1.0.0";

    String CFG_REFERENCE_SUFFIX = "Ref";

    String UNIQUE_PREFIX = "UNIQUE_";
    String VAR_IN_USE = "WLP_VAR_IN_USE";

    String VAR_OPEN = "${";
    String VAR_CLOSE = "}";
    Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");

    /** Used to prefix a kernel bundle location. */
    String BUNDLE_LOC_KERNEL_TAG = "kernel@";

    /** Used to prefix a feature bundle location. */
    String BUNDLE_LOC_FEATURE_TAG = "feature@";

    /** Bundle location product extension tag. */
    String BUNDLE_LOC_PROD_EXT_TAG = "productExtension:";
}
