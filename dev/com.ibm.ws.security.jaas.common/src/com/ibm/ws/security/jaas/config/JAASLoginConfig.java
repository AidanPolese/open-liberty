/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.jaas.config;

import java.util.List;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;

/**
 *
 */
public interface JAASLoginConfig
{
    public Map<String, List<AppConfigurationEntry>> getEntries();

}
