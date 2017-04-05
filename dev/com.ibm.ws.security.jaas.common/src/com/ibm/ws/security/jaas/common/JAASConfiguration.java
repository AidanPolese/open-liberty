/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.jaas.common;

import java.util.List;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;

import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceMap;

/**
 *
 */
public interface JAASConfiguration {

    /**
     * Get all jaasLoginContextEntry in the server.xml and create any missing default entries.
     * If there is no jaas configuration, then create all the default entries:
     * On the server: system.DEFAULT,
     * system.WEB_INBOUND, system.DESERIALIZE_CONTEXT, system.UNAUTHENTICATED and WSLogin.
     * On the client: ClientContainer
     * 
     * @return list of the JAAS login context entries mapped to their names
     */
    public Map<String, List<AppConfigurationEntry>> getEntries();

    /**
     * 
     * @param jaasLoginContextEntries
     */
    public void setJaasLoginContextEntries(ConcurrentServiceReferenceMap<String, JAASLoginContextEntry> jaasLoginContextEntries);

}
