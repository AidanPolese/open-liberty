/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.security.util;

import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;
import com.ibm.ws.webcontainer.security.WebAppSecurityConfig;
import com.ibm.ws.webcontainer.security.internal.WebSecurityHelperImpl;
import com.ibm.ws.webcontainer.security.metadata.SecurityMetadata;
import com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData;
import com.ibm.wsspi.webcontainer.metadata.WebModuleMetaData;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

/**
 * This class contains methods for getting web app config information
 */
public class WebConfigUtils {

    /**
     * Get the web application config
     * 
     * @return the web application config
     */
    public static WebAppConfig getWebAppConfig() {
        WebAppConfig wac = null;
        ComponentMetaData cmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
        if (cmd instanceof WebComponentMetaData) { // Only get the header for web modules, i.e. not for EJB
            WebModuleMetaData wmmd = (WebModuleMetaData) ((WebComponentMetaData) cmd).getModuleMetaData();
            wac = wmmd.getConfiguration();
            if (!(wac instanceof com.ibm.ws.webcontainer.osgi.webapp.WebAppConfiguration)) {
                wac = null;
            }
        }
        return wac;
    }

    /**
     * Get the web app security config
     * 
     * @return the web app security config
     */
    public static WebAppSecurityConfig getWebAppSecurityConfig() {
        return WebSecurityHelperImpl.getWebAppSecurityConfig();
    }

    /**
     * Get the security metadata
     * 
     * @return the security metadata
     */
    public static SecurityMetadata getSecurityMetadata() {
        SecurityMetadata secMetadata = null;
        ComponentMetaData cmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
        WebModuleMetaData wmmd = (WebModuleMetaData) cmd.getModuleMetaData();
        secMetadata = (SecurityMetadata) wmmd.getSecurityMetaData();
        return secMetadata;
    }
}
