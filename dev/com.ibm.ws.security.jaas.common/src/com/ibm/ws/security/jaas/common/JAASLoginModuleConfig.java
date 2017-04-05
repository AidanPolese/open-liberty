/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.jaas.common;

import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

/**
 *
 */
public interface JAASLoginModuleConfig {
    public static final String PROXY = "proxy";
    public static final String LOGIN_MODULE_PROXY = "com.ibm.ws.kernel.boot.security.LoginModuleProxy";
    public static final String KEY_CONFIGURATION_ADMIN = "configurationAdmin";
    public static final String WAS_LM_SHARED_LIB = "WAS_LM_SHAREDLIB";
    public static final String KEY_CLASSLOADING_SVC = "classLoadingSvc";

    String getId();

    String getClassName();

    /**
     * Returns the loginModule control flag. Default value is OPTIONAL.
     */
    LoginModuleControlFlag getControlFlag();

    Map<String, ?> getOptions();

    boolean isDefaultLoginModule();
}