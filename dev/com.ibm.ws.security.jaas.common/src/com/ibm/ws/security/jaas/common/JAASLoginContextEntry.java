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

import java.util.List;

/**
 *
 */
public interface JAASLoginContextEntry {
    public static final String CFG_KEY_LOGIN_MODULE_REF = "loginModuleRef";
    public static final String CFG_KEY_ID = "id";

    String getId();

    String getEntryName();

    List<JAASLoginModuleConfig> getLoginModules();
}