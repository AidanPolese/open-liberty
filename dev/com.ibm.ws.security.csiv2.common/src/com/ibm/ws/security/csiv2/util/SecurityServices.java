/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.csiv2.util;

import com.ibm.ws.security.authentication.UnauthenticatedSubjectService;
import com.ibm.ws.security.csiv2.config.ssl.SSLConfig;

/**
 * Class to obtain security OSGi services from non-OSGi code.
 */
public class SecurityServices {

    private static SSLConfig sslConfig;
    private static UnauthenticatedSubjectService unauthenticatedSubjectService;

    /**
     * @param sslSupport
     */
    public static synchronized void setupSSLConfig(SSLConfig sslConfig) {
        SecurityServices.sslConfig = sslConfig;
    }
    
    /**
     * @param unauthenticatedSubjectService
     */
    public static synchronized void setUnauthenticatedSubjectService(UnauthenticatedSubjectService unauthenticatedSubjectService) {
        SecurityServices.unauthenticatedSubjectService = unauthenticatedSubjectService;
    }

    public static synchronized SSLConfig getSSLConfig() {
        return sslConfig;
    }

    public static synchronized UnauthenticatedSubjectService getUnauthenticatedSubjectService() {
        return unauthenticatedSubjectService;
    }

    /**
     * Nulls out all allocated services.
     */
    public static synchronized void clean() {
        sslConfig = null;
        unauthenticatedSubjectService = null;
    }

}
